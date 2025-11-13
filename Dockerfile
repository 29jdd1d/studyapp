# 多阶段构建 (Multi-stage build)
# Stage 1: Build the application
FROM maven:3.8.6-openjdk-8-slim AS builder

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Create runtime image
FROM openjdk:8-jre-slim

LABEL maintainer="studyapp"
LABEL description="Postgraduate Study Mini Program Backend"

WORKDIR /app

# Create a non-root user
RUN groupadd -r studyapp && useradd -r -g studyapp studyapp

# Copy the JAR file from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Create upload directory
RUN mkdir -p /uploads && chown -R studyapp:studyapp /uploads /app

# Switch to non-root user
USER studyapp

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/api/actuator/health || exit 1

# JVM options for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
