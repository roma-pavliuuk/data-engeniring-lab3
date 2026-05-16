package edu.pzks.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import edu.pzks.model.DivvyTrip;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

public class TripProducer {
    private static final Logger log = LoggerFactory.getLogger(TripProducer.class);

    private final KafkaProducer<String, String> kafkaProducer;
    private final List<String> topics;
    private final ObjectMapper objectMapper;
    private final long delayMs;


    public TripProducer(KafkaProducer<String, String> kafkaProducer,
                        List<String> topics,
                        long delayMs) {
        this.kafkaProducer = kafkaProducer;
        this.topics        = topics;
        this.objectMapper  = new ObjectMapper();
        this.delayMs       = delayMs;
    }


    public void sendFromCsv(String csvFilePath) {
        log.info("Starting to read CSV file: {}", csvFilePath);

        long sentCount  = 0;
        long errorCount = 0;

        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {

            String[] header = reader.readNext();
            if (header == null) {
                log.error("CSV file is empty: {}", csvFilePath);
                return;
            }
            log.info("CSV header detected: {} columns", header.length);

            String[] line;
            while ((line = reader.readNext()) != null) {

                try {
                    DivvyTrip trip  = DivvyTrip.fromCsvLine(line);
                    String    key   = trip.getTripId();
                    String    value = objectMapper.writeValueAsString(trip);

                    for (String topic : topics) {
                        ProducerRecord<String, String> record =
                                new ProducerRecord<>(topic, key, value);

                        Future<RecordMetadata> future = kafkaProducer.send(record, (meta, ex) -> {
                            if (ex != null) {
                                log.error("Failed to send record to topic {}: {}", topic, ex.getMessage());
                            } else {
                                log.debug("Sent to {} partition={} offset={}",
                                        meta.topic(), meta.partition(), meta.offset());
                            }
                        });
                    }

                    sentCount++;

                    if (sentCount % 1000 == 0) {
                        log.info("Progress: {} records sent", sentCount);
                    }

                    if (delayMs > 0) {
                        Thread.sleep(delayMs);
                    }

                } catch (Exception e) {
                    errorCount++;
                    log.warn("Skipping malformed row #{}: {}", sentCount + errorCount, e.getMessage());
                }
            }

        } catch (IOException | CsvValidationException e) {
            log.error("Error reading CSV file: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

        kafkaProducer.flush();
        log.info("======================================");
        log.info("Finished! Records sent: {}, Errors: {}", sentCount, errorCount);
        log.info("======================================");
    }
}
