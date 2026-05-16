FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy pom first for better Docker layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy sources and build the fat JAR
COPY src ./src
RUN mvn clean package -DskipTests -q

# ── Stage 2: Runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the fat JAR from the build stage
COPY --from=builder /build/target/lab3-1.0-SNAPSHOT.jar app.jar

# Data directory (CSV will be mounted here as a volume)
RUN mkdir -p /app/data

ENTRYPOINT ["java", "-jar", "app.jar"]