# ===== Build Stage =====
FROM openjdk:17-jdk-slim AS build

WORKDIR /app

# Copy Maven wrapper & pom.xml first (dependency cache)
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn/ .mvn/

# Pre-fetch dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src/ src/

# Build app
RUN ./mvnw clean package -DskipTests && \
    mv target/*-SNAPSHOT.jar app.jar

# ===== Runtime Stage =====
FROM openjdk:17-jdk-slim

WORKDIR /app

# Create non-root user
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# Copy built jar
COPY --from=build /app/app.jar app.jar

# Expose port
EXPOSE 8080

# JVM options can be set at runtime with -e JAVA_OPTS="..."
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Entrypoint without sh -c (signal-safe)
ENTRYPOINT ["java", "-jar", "app.jar"]
