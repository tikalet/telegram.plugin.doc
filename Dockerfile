FROM openjdk:17
WORKDIR /app

ENV TZ=Europe/Moscow
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY telegram.plugin.doc-*.jar ./
CMD java -Dspring.profiles.active=prod -Dspring.config.location=config/application-prod.properties -jar telegram.plugin.doc-*.jar
