FROM gradle:7.4.0-jdk11-alpine
ARG PORT
ARG INSTANCE
ARG GRADLE_PATH
ENV server.port=${PORT}
ENV production.instance=${INSTANCE}
COPY --chown=gradle:gradle ${GRADLE_PATH} /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test --no-daemon
EXPOSE ${PORT}
RUN mkdir /app
RUN cp /home/gradle/src/build/libs/*SNAPSHOT.jar /app/spring-boot-app.jar
RUN sleep 90
ENTRYPOINT ["java", "-jar","/app/spring-boot-app.jar"]
