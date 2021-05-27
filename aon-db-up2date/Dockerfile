FROM openjdk:11-jre-slim

ARG AON_VERSION=9.23-SNAPSHOT

COPY /target/aon.db.up2date-$AON_VERSION-jar-with-dependencies.jar aon-db-up2date.jar

COPY docker-entrypoint.sh .

RUN chmod +x docker-entrypoint.sh
ENTRYPOINT ["./docker-entrypoint.sh"]

CMD [ "java" , "-jar" , "aon-db-up2date.jar" ]
