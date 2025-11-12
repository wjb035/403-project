# Stage 1: Build the application
FROM gradle:8.3.3-jdk17 AS build

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradle gradle
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Download dependencies
RUN ./gradlew --no-daemon dependencies || true

# Copy the rest of the source code
COPY src src

# Build the Spring Boot jar
RUN ./gradlew --no-daemon bootJar

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port (adjust to your Spring Boot port)
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
