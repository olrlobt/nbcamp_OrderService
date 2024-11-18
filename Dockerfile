FROM openjdk:17-jdk-slim

COPY *.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
