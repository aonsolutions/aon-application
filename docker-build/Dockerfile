FROM maven:3-jdk-11

RUN apt-get update \
    && apt-get install -y --no-install-recommends mariadb-server

RUN mysql_install_db

COPY docker-entrypoint.sh /usr/local/bin/
RUN ln -s usr/local/bin/docker-entrypoint.sh /entrypoint.sh # backwards compat

# Set the time zone
#RUN echo "Europe/Madrid" > /etc/timezone && dpkg-reconfigure -f noninteractive tzdata
ENV TZ=Europe/Madrid
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && dpkg-reconfigure -f noninteractive tzdata

#ENV AON_TMP=/tmp/aon-application

RUN mysql_install_db

WORKDIR /root/.m2
COPY repository.zip .
RUN set -x \
    && unzip repository.zip \
    && rm repository.zip

#RUN mkdir -p $AON_TMP
#WORKDIR /tmp/aon-application
#COPY pom.xml .
#RUN set -x   \
#    && mvn -N clean     \
#    && mvn -N install   \
#    && mvn -N dependency:go-offline         \
#    && rm -rf $AON_TMP

RUN chmod +x /usr/local/bin/docker-entrypoint.sh
ENTRYPOINT ["docker-entrypoint.sh"]

CMD ["mvn"]
