# --- Build stage ---
FROM maven:3.6.3-jdk-17 as build
WORKDIR /app
COPY . /app
RUN mvn clean package

# --- Package stage ---
FROM openjdk:17
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
