FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /build/target/lab3-1.0-SNAPSHOT.jar app.jar

RUN mkdir -p /app/data

ENTRYPOINT ["java", "-jar", "app.jar"]