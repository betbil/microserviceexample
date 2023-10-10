# --- Build stage ---
FROM maven:3.9.4-eclipse-temurin-17 as build
WORKDIR /app
COPY . /app
RUN mvn clean package

# --- Package stage ---
FROM openjdk:17
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
