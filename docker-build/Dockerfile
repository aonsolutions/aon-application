FROM maven:3-jdk-14



RUN apt-get update \
    && apt-get install -y --no-install-recommends mariadb-server

RUN mysql_install_db

# Install git, SSH, and other utilities
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
			 apt-transport-https ca-certificates curl gnupg2 \
			 software-properties-common btrfs-progs e2fsprogs xfsprogs

# Install Docker
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add -
RUN add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable"
RUN apt-get update \
		&& apt-get install -y --no-install-recommends docker-ce docker-ce-cli containerd.io
ENV DIND_COMMIT="37498f009d8bf25fbb6199e8ccd34bed84f2874b"
RUN set -ex \
  	&& wget -O /usr/local/bin/dind "https://raw.githubusercontent.com/docker/docker/${DIND_COMMIT}/hack/dind" \
		&& chmod +x /usr/local/bin/dind

# Install Python3
RUN apt-get update \
   && apt-get install -y python3 python3-pip \
	 && rm -rf /var/lib/apt/lists/* \
	 && apt-get clean

# Install AWS CLI
RUN set -ex \
    && pip3 install awscli --upgrade \
		&& aws --version

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
