FROM openjdk:11-jre-slim

EXPOSE 32228
COPY /build/libs/pastie-me.noobgam.pastie.core.jar /app/
COPY /srv/noobgam/pastie/secrets /srv/noobgam/pastie/secrets

WORKDIR /app

ENV ENV=PROD

CMD java -jar pastie-me.noobgam.pastie.core.jar
