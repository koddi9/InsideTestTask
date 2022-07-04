FROM adoptopenjdk/openjdk11:alpine-jre
ARG JAR_FILE=build/libs/InsideTestTask-0.0.1-SNAPSHOT.jar
WORKDIR /opt/app
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=dev","app.jar"]