FROM maven:3.8.6-openjdk-22-slim AS build
WORKDIR /app
COPY src ./src
COPY pom.xml .
RUN mvn clean package -DskipTests

FROM openjdk:22-jdk-slim
COPY --from=build /app/target/*.jar scheduled-service.jar
ENTRYPOINT ["java", "-jar", "/scheduled-service.jar"]
