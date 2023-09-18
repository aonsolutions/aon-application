FROM openjdk:17-slim

ARG AON_VERSION=9.23-SNAPSHOT

COPY /target/aon.storage-$AON_VERSION-jar-with-dependencies.jar aon-storage.jar

COPY docker-entrypoint.sh .

RUN chmod +x docker-entrypoint.sh
ENTRYPOINT ["./docker-entrypoint.sh"]

CMD [ "java" , "-jar" , "aon-storage.jar" ]
