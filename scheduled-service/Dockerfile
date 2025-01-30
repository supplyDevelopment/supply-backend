# Dockerfile
FROM openjdk:22-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} scheduled-service.jar
ENTRYPOINT ["java", "-jar", "/scheduled-service.jar"]
