FROM amazoncorretto:21

WORKDIR /app

COPY target/api-complaint-0.0.1-SNAPSHOT.jar /app/api-complaint-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/app/api-complaint-0.0.1-SNAPSHOT.jar"]