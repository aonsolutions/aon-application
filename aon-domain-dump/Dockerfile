FROM public.ecr.aws/lambda/provided:latest

# Install AWS command-line
RUN dnf install --assumeyes aws-cli

# Install mysqldump
#RUN yum install --assumeyes mariadb

RUN dnf install --assumeyes wget
RUN wget https://dev.mysql.com/get/mysql80-community-release-el9-1.noarch.rpm 
RUN rpm --install mysql80-community-release-el9-1.noarch.rpm
# We need the public key of mysql to install the software. 
RUN rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql-2023
# install mysql client
RUN dnf install mysql-community-client -y
# Test version
RUN mysqldump --version

# Install command-line JSON processor
RUN dnf install --assumeyes jq
RUN dnf install --assumeyes gzip
RUN dnf install --assumeyes findutils

RUN dnf clean all

# Copy custom runtime bootstrap
COPY bootstrap ${LAMBDA_RUNTIME_DIR}
# Copy function code
#COPY function.sh ${LAMBDA_TASK_ROOT}
COPY hello.sh ${LAMBDA_TASK_ROOT}

# Set the CMD to your handler (could also be done as a parameter override outside of the Dockerfile)
CMD [ "hello.handler" ]
