FROM tomcat:9.0-jdk14

ARG AON_VERSION=9.23-SNAPSHOT

ENV AWS_HOME /root/.aws

ENV CATALINA_HOME /usr/local/tomcat

ENV AON_AIO_HOME $CATALINA_HOME/webapps/ROOT

ARG NET_MAVEN_REPOSITORY_URL=http://repo.maven.aonsolutions.net/maven2/net/aonsolutions/

ENV TOMCAT_LIBDIR $CATALINA_HOME/lib

WORKDIR $TOMCAT_LIBDIR

COPY aon-jaas/target/aon.jaas-${AON_VERSION}.jar aon-jaas.jar
COPY pool/target/pool-${AON_VERSION}.jar pool.jar


ENV SLF4J_API_URL=https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.8.0-beta2/slf4j-api-1.8.0-beta2.jar
ENV SLF4J_JDK14_URL=https://repo1.maven.org/maven2/org/slf4j/slf4j-jcl/1.7.25/slf4j-jcl-1.7.25.jar
ENV C3P0_JDBC_URL=https://repo1.maven.org/maven2/com/mchange/c3p0/0.9.5.4/c3p0-0.9.5.4.jar
ENV COMMONS_LANG_URL=https://repo1.maven.org/maven2/commons-lang/commons-lang/2.5/commons-lang-2.5.jar
ENV COMMONS_DBUTILS_URL=https://repo1.maven.org/maven2/commons-dbutils/commons-dbutils/1.5/commons-dbutils-1.5.jar
ENV MYSQL_JDBC_URL=https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.13/mysql-connector-java-8.0.13.jar
ENV MCHANGE_COMMONS_URL=https://repo1.maven.org/maven2/com/mchange/mchange-commons-java/0.2.15/mchange-commons-java-0.2.15.jar
ENV COMMONS_LOGGING_URL=https://repo1.maven.org/maven2/commons-logging/commons-logging-api/1.1/commons-logging-api-1.1.jar
ENV COMMONS_COLLECTIONS_URL=https://repo1.maven.org/maven2/commons-collections/commons-collections/3.1/commons-collections-3.1.jar
#ENV IZENPESIGNER_APPLET=http://aonsolutions.github.io/aon-application/maven/2/external_free/izenpe/izenpesigner-applet/1.0/izenpesigner-applet-1.0.jar
ENV IZENPESIGNER_APPLET=https://github.com/aonsolutions/izenpe/raw/master/izenpesigner-applet-1.0.jar
ENV DYNAMODB_SESSION_MANAGER=https://github.com/aws/aws-dynamodb-session-tomcat/releases/download/v2.0.4/aws-dynamodb-session-tomcat-2.0.4.jar

ENV JAVA_JWT_URL=https://repo1.maven.org/maven2/com/auth0/java-jwt/3.9.0/java-jwt-3.9.0.jar
ENV JACKSON_ANNOTATIONS_URL=https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.10.0.pr3/jackson-annotations-2.10.0.pr3.jar
ENV JACKSON_CORE_URL=https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.10.0.pr3/jackson-core-2.10.0.pr3.jar
ENV JACKSON_DATABIND_URL=https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.10.0.pr3/jackson-databind-2.10.0.pr3.jar
ENV COMMONS_CODEC_URL=https://repo1.maven.org/maven2/commons-codec/commons-codec/1.9/commons-codec-1.9.jar
ENV JSON_URL=https://repo1.maven.org/maven2/org/json/json/20180813/json-20180813.jar

RUN set -x \
	\
	&& wget "$SLF4J_API_URL" \
	&& wget "$SLF4J_JDK14_URL" \
	&& wget "$C3P0_JDBC_URL" \
	&& wget "$MYSQL_JDBC_URL" \
	&& wget "$COMMONS_LANG_URL" \
	&& wget "$COMMONS_DBUTILS_URL" \
	&& wget "$COMMONS_LOGGING_URL" \
	&& wget "$MCHANGE_COMMONS_URL" \
	&& wget "$COMMONS_COLLECTIONS_URL" \
	&& wget "$DYNAMODB_SESSION_MANAGER" \
	&& wget "$JAVA_JWT_URL" \
	&& wget "$JACKSON_ANNOTATIONS_URL" \
	&& wget "$JACKSON_CORE_URL" \
	&& wget "$JACKSON_DATABIND_URL" \
	&& wget "$COMMONS_CODEC_URL" \
	&& wget "$JSON_URL"


ENV TOMCAT_BINDIR $CATALINA_HOME/bin
ENV TOMCAT_CONFDIR $CATALINA_HOME/conf
ENV AON_AIO_CONF /etc/aon-aio
ENV AON_AIO_COMMON /home/COMMON-RESOURCES


WORKDIR $CATALINA_HOME/webapps

RUN set -x \
	&& rm -rf docs \
	&& rm -rf ROOT \
	&& rm -rf examples \
	&& rm -rf manager \
	&& rm -rf host-manager


RUN mkdir -p "$AON_AIO_COMMON"
RUN mkdir -p "$AON_AIO_COMMON/aon-report"
WORKDIR $AON_AIO_COMMON/aon-report

COPY aon-common-resources/target/aon-common-resources-${AON_VERSION}-templates.jar aon-common-resources-templates.jar

RUN set -x \
	&& unzip aon-common-resources-templates.jar -x META-INF/* \
	&& rm aon-common-resources-templates.jar

RUN mkdir -p "$AON_AIO_COMMON/aon-ui-sign"
WORKDIR $AON_AIO_COMMON/aon-ui-sign

RUN set -x \
	&& wget $IZENPESIGNER_APPLET

RUN ln -s izenpesigner-applet-*.jar izenpesigner-applet.jar

RUN mkdir -p "$AON_AIO_HOME"
WORKDIR $AON_AIO_HOME

COPY aon-web-aio/target/aon-aio.war aon-aio.war

RUN set -x \
	\
	&& unzip aon-aio.war \
	&& rm aon-aio.war

RUN mkdir -p "$AWS_HOME"

RUN mkdir -p "$AON_AIO_CONF"

COPY aon-web-aio/src/main/scripts/login.config $TOMCAT_CONFDIR
COPY aon-web-aio/src/main/scripts/default.pool-properties $AON_AIO_CONF
COPY aon-web-aio/src/main/scripts/pro-aonsolutions-net.pool-properties $AON_AIO_CONF


COPY docker-entrypoint.sh /usr/local/bin/
RUN ln -s usr/local/bin/docker-entrypoint.sh /entrypoint.sh # backwards compat

# Set the time zone
#RUN echo "Europe/Madrid" > /etc/timezone && dpkg-reconfigure -f noninteractive tzdata
ENV TZ=Europe/Madrid
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && dpkg-reconfigure -f noninteractive tzdata

RUN chmod +x /usr/local/bin/docker-entrypoint.sh
ENTRYPOINT ["docker-entrypoint.sh"]

EXPOSE 8080

ENV JAVA_OPTS="${JAVA_OPTS} -Duser.language=es -Duser.country=ES"
ENV JAVA_OPTS="${JAVA_OPTS} -Djava.security.auth.login.config=${TOMCAT_CONFDIR}/login.config"

CMD ["catalina.sh", "run"]
