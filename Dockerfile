# Multi-stage Dockerfile for building and running the Spring Boot application

# Builder stage
FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /workspace
COPY pom.xml mvnw .
COPY .mvn .mvn
# copy sources
COPY src src
# package the application
RUN mvn -B -e -DskipTests package

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# copy jar from builder
COPY --from=builder /workspace/target/rest-api-orders-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8633
ENTRYPOINT ["java","-jar","/app/app.jar"]

