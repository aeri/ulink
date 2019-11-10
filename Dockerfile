FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY build/libs/UrlShortener.jar app.jar
COPY src/main/resources/en_US.json en_US.json
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]