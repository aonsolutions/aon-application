FROM public.ecr.aws/lambda/provided:al2023

#Install Amazon Coretto 21
RUN dnf install --assumeyes java-21-amazon-corretto-headless

# Install MySQL
# Download the RPM file  
RUN curl -O https://repo.mysql.com//mysql80-community-release-el9-5.noarch.rpm
# Install rpm file 
RUN rpm --install mysql80-community-release-el9-5.noarch.rpm
# We need the public key of mysql to install the software.
RUN rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql-2023
# Install mysql client
RUN dnf install mysql-community-client -y 

# Install AWS command-line
RUN dnf install --assumeyes aws-cli

# Install command-line JSON processor
RUN dnf install --assumeyes jq 

# Install FindUtils, The tools supplied with this package are: 
# find - search for files in a directory hierarchy 
# ...
# xargs - build and execute command lines from standard input
RUN dnf install --assumeyes findutils

RUN dnf clean all


# Copy custom runtime bootstrap
COPY bootstrap ${LAMBDA_RUNTIME_DIR}
# Copy function code
#COPY function.sh ${LAMBDA_TASK_ROOT}
COPY audit.sh ${LAMBDA_TASK_ROOT}

# Copy jar 
COPY target/aon.audit-9.23-SNAPSHOT-jar-with-dependencies.jar ${LAMBDA_TASK_ROOT}

# Set the CMD to your handler (could also be done as a parameter override outside of the Dockerfile)
CMD [ "audit.handler" ]
