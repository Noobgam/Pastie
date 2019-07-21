FROM gradle:5.5.1-jdk11 as builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

RUN gradle build

FROM openjdk:11-jre-slim
EXPOSE 228
COPY --from=builder /home/gradle/src/build/libs/pastie-me.noobgam.pastie.core.jar /app/
WORKDIR /app
CMD java -jar pastie-me.noobgam.pastie.core.jar
