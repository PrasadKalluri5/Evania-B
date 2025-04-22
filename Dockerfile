# Use a Java 17 JDK base image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built Spring Boot JAR file into the container
COPY target/gpt-course-bot-1.0-SNAPSHOT.jar app.jar

# Expose the port (Render maps it dynamically)
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]