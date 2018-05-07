FROM java:jre-alpine

ARG AON_VERSION=9.23-SNAPSHOT

COPY /target/aon.tests-$AON_VERSION-jar-with-dependencies.jar aon-tests.jar

COPY docker-entrypoint.sh .

ENTRYPOINT ["./docker-entrypoint.sh"]

CMD [ "java" , "-jar" , "aon-tests.jar" ]
