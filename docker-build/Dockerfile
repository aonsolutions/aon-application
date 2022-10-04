FROM maven:3-openjdk-17

RUN set -eux; \
	microdnf install \
	selinux-policy-targeted procps sudo hostname mysql-server

#RUN sudo -u mysql mysqld --initialize-insecure

# Install git, SSH, unzip and other utilities
RUN set -eux; \
	microdnf install \
	ca-certificates curl gnupg2 unzip \
	btrfs-progs e2fsprogs xfsprogs iproute

# Install Docker
WORKDIR /etc/yum.repos.d
RUN curl -fsSLO  https://download.docker.com/linux/centos/docker-ce.repo
RUN set -eux; \
	microdnf install docker-ce docker-ce-cli containerd.io
ENV DIND_COMMIT="37498f009d8bf25fbb6199e8ccd34bed84f2874b"
RUN set -eux \
	&& curl "https://raw.githubusercontent.com/docker/docker/${DIND_COMMIT}/hack/dind" --output /usr/local/bin/dind \
	&& chmod +x /usr/local/bin/dind

# Install Python3
RUN set -eux; \
	microdnf install python3 python3-pip \
	&& rm -rf /var/lib/apt/lists/* \
	&& microdnf clean all

# Install AWS CLI
RUN set -eux; \
	pip3 install awscli --upgrade \
	&& aws --version

COPY docker-entrypoint.sh /usr/local/bin/
RUN ln -s usr/local/bin/docker-entrypoint.sh /entrypoint.sh # backwards compat

# Set the time zone
ENV TZ=Europe/Madrid
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

#ENV AON_TMP=/tmp/aon-application

WORKDIR /root/.m2
COPY repository.zip .
RUN set -x \
    && unzip repository.zip \
    && rm repository.zip

#COPY araba-eus.pem .
#COPY bizkaia-eus.pem .
#COPY gipuzkoa-eus.pem .
#COPY seg-social-es.pem .
#ENV KEYSTORE=/opt/java/openjdk/lib/security/cacerts
#RUN set -x \
#    && for pem in *.pem; do \
#    keytool -importcert -alias ${pem%.*} -keystore $KEYSTORE -file $pem -storepass changeit -noprompt \
#    && keytool -list -keystore $KEYSTORE -storepass changeit -alias ${pem%.*}; \
#    done


COPY tomcat:9-jdk17.tar /root
COPY openjdk:17-slim.tar /root

ENV AON_CONF=/etc/aon-aio

RUN mkdir -p $AON_CONF
COPY connection $AON_CONF
#RUN mkdir -p $AON_TMP
#WORKDIR /tmp/aon-application
#COPY pom.xml .
#RUN set -x   \
#    && mvn -N clean     \
#    && mvn -N install   \
#    && mvn -N dependency:go-offline         \
#    && rm -rf $AON_TMP


WORKDIR /root
COPY .aws.tar.gz .aws.tar.gz
RUN set -x \
        \
        && tar -xvzf .aws.tar.gz \
        && rm .aws.tar.gz



RUN chmod +x /usr/local/bin/docker-entrypoint.sh
ENTRYPOINT ["docker-entrypoint.sh"]

CMD ["mvn"]
