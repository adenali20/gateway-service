#FROM openjdk:17-jdk-alpine
FROM openjdk:26-ea-trixie
ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} gateway-service.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/gateway-service.jar"]