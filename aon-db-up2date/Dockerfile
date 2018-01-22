FROM java:jre-alpine

ARG AON_VERSION=9.00-SNAPSHOT

COPY /target/aon.db.up2date-$AON_VERSION-jar-with-dependencies.jar aon-db-up2date.jar

COPY docker-entrypoint.sh .

ENTRYPOINT ["./docker-entrypoint.sh"]

CMD [ "java" , "-jar" , "aon-db-up2date.jar" ]
