FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY src ./src
COPY pom.xml .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine
COPY --from=build /app/target/*.jar email-service.jar
ENTRYPOINT ["java", "-jar", "/email-service.jar"]
