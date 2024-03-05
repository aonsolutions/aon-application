FROM openjdk:21-jdk-bullseye

RUN set -eux; \
	apt-get update

# Install sudo
RUN set -eux; \
	apt-get install -y sudo debconf-utils iproute2

# Install Maven
RUN set -eux; \
	apt-get install -y maven

# Install MySQL
RUN set -eux; \
	wget https://dev.mysql.com/get/mysql-apt-config_0.8.25-1_all.deb; \
	DEBIAN_FRONTEND=noninteractive apt-get install -y ./mysql-apt-config_0.8.25-1_all.deb; \
	echo "mysql-apt-config     mysql-apt-config/select-server  select  mysql-8.0" | debconf-set-selections; 

RUN set -eux; \
	apt-get update; \
	DEBIAN_FRONTEND=noninteractive apt-get install -y mysql-server; \
	echo "mysql-community-server        mysql-server/default-auth-override      select Use Legacy Authentication Method (Retain MySQL 5.x Compatibility)" | debconf-set-selections

# Add Docker's official GPG key:
RUN set -eux; \
	apt-get install -y ca-certificates curl gnupg; \
	sudo install -m 0755 -d /etc/apt/keyrings; \
	wget -O - https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg; \
	chmod a+r /etc/apt/keyrings/docker.gpg

# Add Docker's repository to Apt sources:
RUN set -eux; \
	echo \
	"deb [arch="$(dpkg --print-architecture)" signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/debian \
	"$(. /etc/os-release && echo "$VERSION_CODENAME")" stable" | \
	tee /etc/apt/sources.list.d/docker.list > /dev/null

RUN set -eux; \
	apt-get update;

# Install the Docker's packages.	
RUN set -eux; \
	apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Install AWS CLI
RUN set -eux; \
	apt-get install -y awscli 

RUN set -eux \
	rm -rf /var/lib/apt/lists/*

COPY docker-entrypoint.sh /usr/local/bin/
RUN ln -s usr/local/bin/docker-entrypoint.sh /entrypoint.sh # backwards compat

# Set the time zone
ENV TZ=Europe/Madrid
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone


WORKDIR /root/.m2
COPY repository.zip .
RUN set -x \
    && unzip repository.zip \
    && rm repository.zip

COPY openjdk:21-slim.tar /root
COPY tomcat:11.0-jdk21-openjdk.tar /root

ENV AON_CONF=/etc/aon-aio

RUN mkdir -p $AON_CONF
COPY connection $AON_CONF

WORKDIR /root
COPY .aws.tar.gz .aws.tar.gz
RUN set -x \
        && tar -xvzf .aws.tar.gz \
        && rm .aws.tar.gz



RUN chmod +x /usr/local/bin/docker-entrypoint.sh
ENTRYPOINT ["docker-entrypoint.sh"]

CMD ["mvn"]
