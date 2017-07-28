FROM tomcat:9.0-jre8

RUN apt-get update && apt-get install -y \
	procps \
	libreoffice-calc \
	libreoffice-writer

ARG AON_VERSION=9.00-SNAPSHOT
ARG POOL_VERSION=9.00-SNAPSHOT

ENV CATALINA_HOME /usr/local/tomcat

ENV AON_AIO_HOME $CATALINA_HOME/webapps/ROOT

ARG NET_MAVEN_REPOSITORY_URL=http://repo.maven.aonsolutions.net/maven2/net/aonsolutions/
ARG AON_MAVEN_REPOSITORY_URL=http://dev.esferalia.net/maven2_repositories/inhouse_snapshot/com/code/aon

ENV TOMCAT_LIBDIR $CATALINA_HOME/lib

WORKDIR $TOMCAT_LIBDIR

RUN set -x \
	\
	&& wget $AON_MAVEN_REPOSITORY_URL/aon.jaas/$AON_VERSION/aon.jaas-`wget -O - $AON_MAVEN_REPOSITORY_URL/aon.jaas/$AON_VERSION/maven-metadata.xml 2>/dev/null | grep -m 1 -Po "(?<=<value>).*(?=</value>)" || echo $AON_VERSION`.jar \
	&& wget $NET_MAVEN_REPOSITORY_URL/core/pool/$POOL_VERSION/pool-`wget -O - $NET_MAVEN_REPOSITORY_URL/core/pool/$POOL_VERSION/maven-metadata.xml 2>/dev/null | grep -m 1 -Po "(?<=<value>).*(?=</value>)" || echo $POOL_VERSION`.jar


ENV SLF4J_API_URL=http://central.maven.org/maven2/org/slf4j/slf4j-api/1.5.11/slf4j-api-1.5.11.jar
ENV SLF4J_JDK14_URL=http://central.maven.org/maven2/org/slf4j/slf4j-jdk14/1.5.11/slf4j-jdk14-1.5.11.jar
ENV C3P0_JDBC_URL=http://central.maven.org/maven2/com/mchange/c3p0/0.9.2/c3p0-0.9.2.jar
ENV MYSQL_JDBC_URL=http://central.maven.org/maven2/mysql/mysql-connector-java/5.1.10/mysql-connector-java-5.1.10.jar
ENV COMMONS_LANG_URL=http://central.maven.org/maven2/commons-lang/commons-lang/2.5/commons-lang-2.5.jar
ENV COMMONS_DBUTILS_URL=http://central.maven.org/maven2/commons-dbutils/commons-dbutils/1.5/commons-dbutils-1.5.jar
ENV MCHANGE_COMMONS_URL=http://central.maven.org/maven2/com/mchange/mchange-commons-java/0.2.3.3/mchange-commons-java-0.2.3.3.jar
ENV COMMONS_LOGGING_URL=http://central.maven.org/maven2/commons-logging/commons-logging-api/1.1/commons-logging-api-1.1.jar
ENV COMMONS_COLLECTIONS_URL=http://central.maven.org/maven2/commons-collections/commons-collections/3.1/commons-collections-3.1.jar
ENV SPY_MEMCACHED_URL=http://central.maven.org/maven2/net/spy/spymemcached/2.11.1/spymemcached-2.11.1.jar
ENV MEMCACHED_SESSION_MANAGER_URL=http://central.maven.org/maven2/de/javakaffee/msm/memcached-session-manager/2.1.1/memcached-session-manager-2.1.1.jar
ENV MEMCACHED_SESSION_MANAGER_TC9_URL=http://dev.esferalia.net/maven2_repositories/external_free/de/javakaffee/msm/memcached-session-manager-tc9/2.1.1/memcached-session-manager-tc9-2.1.1.jar
ENV IZENPESIGNER_APPLET=http://dev.esferalia.net/maven2_repositories/external_free/izenpe/izenpesigner-applet/1.0/izenpesigner-applet-1.0.jar

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
	&& wget "$SPY_MEMCACHED_URL" \
	&& wget "$MEMCACHED_SESSION_MANAGER_URL" \
	&& wget "$MEMCACHED_SESSION_MANAGER_TC9_URL"


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

RUN set -x \
	&& wget -O aon-common-resources-templates.jar $AON_MAVEN_REPOSITORY_URL/aon-common-resources/$AON_VERSION/aon-common-resources-`wget -O - $AON_MAVEN_REPOSITORY_URL/aon-common-resources/$AON_VERSION/maven-metadata.xml 2>/dev/null | grep -m 1 -Po "(?<=<value>).*(?=</value>)" || echo $AON_VERSION`-templates.jar  \
	&& unzip aon-common-resources-templates.jar -x META-INF/* \
	&& rm aon-common-resources-templates.jar 

RUN mkdir -p "$AON_AIO_COMMON/aon-ui-sign"
WORKDIR $AON_AIO_COMMON/aon-ui-sign

RUN set -x \
	&& wget $IZENPESIGNER_APPLET

RUN ln -s izenpesigner-applet-*.jar izenpesigner-applet.jar 

RUN mkdir -p "$AON_AIO_HOME"
WORKDIR $AON_AIO_HOME


RUN set -x \
	\
	&& wget -O aon-aio.war $AON_MAVEN_REPOSITORY_URL/aon-aio/$AON_VERSION/aon-aio-`wget -O - $AON_MAVEN_REPOSITORY_URL/aon-aio/$AON_VERSION/maven-metadata.xml 2>/dev/null | grep -m 1 -Po "(?<=<value>).*(?=</value>)" || echo $AON_VERSION`.war \
	&& unzip aon-aio.war \
	&& rm aon-aio.war 


RUN mkdir -p "$AON_AIO_CONF"

COPY aon-web-aio/src/main/scripts/login.config $TOMCAT_CONFDIR
COPY aon-web-aio/src/main/scripts/default.pool-properties $AON_AIO_CONF


COPY docker-entrypoint.sh /usr/local/bin/
RUN ln -s usr/local/bin/docker-entrypoint.sh /entrypoint.sh # backwards compat

# Set the time zone
RUN echo "Europe/Madrid" > /etc/timezone && dpkg-reconfigure -f noninteractive tzdata

ENTRYPOINT ["docker-entrypoint.sh"]

EXPOSE 8080

ENV JAVA_OPTS="${JAVA_OPTS} -Duser.language=es -Duser.country=ES"
ENV JAVA_OPTS="${JAVA_OPTS} -Djava.security.auth.login.config=${TOMCAT_CONFDIR}/login.config"

CMD ["catalina.sh", "run"]
	

