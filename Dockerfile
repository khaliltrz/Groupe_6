FROM openjdk:8
EXPOSE 8089
WORKDIR /eventProject-main
COPY eventsProject-1.0.0.jar /eventProject-main/eventsProject-1.0.0.jar
ENTRYPOINT ["java", "-jar", "eventsProject-1.0.0.jar" ]
