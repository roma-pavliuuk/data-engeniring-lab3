package edu.pzks;

import edu.pzks.config.KafkaProducerConfig;
import edu.pzks.producer.TripProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        String bootstrapServers = getEnv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
        String csvFilePath = getEnv("CSV_FILE_PATH", "data/Divvy_Trips_2019_Q4.csv");
        String topic1 = getEnv("TOPIC1", "Topic1");
        String topic2 = getEnv("TOPIC2", "Topic2");
        long delayMs = Long.parseLong(getEnv("SEND_DELAY_MS", "10"));

        List<String> topics = Arrays.asList(topic1, topic2);

        log.info("===========================================");
        log.info("Kafka Producer – Lab 3");
        log.info("Bootstrap servers : {}", bootstrapServers);
        log.info("CSV file          : {}", csvFilePath);
        log.info("Topics            : {}", topics);
        log.info("Delay between rows: {} ms", delayMs);
        log.info("===========================================");


        waitForKafka(bootstrapServers, 60);

        try (KafkaProducer<String, String> producer =
                     KafkaProducerConfig.createProducer(bootstrapServers)) {

            TripProducer tripProducer = new TripProducer(producer, topics, delayMs);
            tripProducer.sendFromCsv(csvFilePath);

        } catch (Exception e) {
            log.error("Producer encountered a fatal error: {}", e.getMessage(), e);
            System.exit(1);
        }

        log.info("Producer finished successfully. Exiting.");
    }


    private static String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }

    private static void waitForKafka(String bootstrapServers, int maxWaitSeconds) {
        log.info("Waiting for Kafka brokers to become available (max {}s)...", maxWaitSeconds);

        long deadline = System.currentTimeMillis() + maxWaitSeconds * 1000L;

        while (System.currentTimeMillis() < deadline) {
            try {
                java.util.Properties props = new java.util.Properties();
                props.put("bootstrap.servers", bootstrapServers);
                props.put("connections.max.idle.ms", "5000");
                props.put("request.timeout.ms", "3000");
                props.put("default.api.timeout.ms", "5000");

                try (org.apache.kafka.clients.admin.AdminClient admin =
                             org.apache.kafka.clients.admin.AdminClient.create(props)) {
                    admin.listTopics().names().get(5, java.util.concurrent.TimeUnit.SECONDS);
                    log.info("Kafka is ready!");
                    return;
                }
            } catch (Exception e) {
                log.info("Kafka not ready yet, retrying in 3 seconds... ({})", e.getMessage());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        log.error("Kafka did not become available within {}s. Proceeding anyway...", maxWaitSeconds);
    }
}