FROM gradle:7.4.0-jdk11-alpine
ARG PORT
ARG INSTANCE
ENV server.port=${PORT}
ENV production.instance=${INSTANCE}
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test --no-daemon
EXPOSE ${PORT}
RUN mkdir /app
RUN cp /home/gradle/src/build/libs/*SNAPSHOT.jar /app/spring-boot-app.jar
RUN touch /home/startupWithDelay.sh
RUN chmod 777 /home/startupWithDelay.sh
RUN echo $'#!/bin/bash\n sleep 90;\n java -jar /app/spring-boot-app.jar;\n while true; do sleep 1000; done' >> /home/startupWithDelay.sh
ENTRYPOINT ["sh","/home/startupWithDelay.sh"]
