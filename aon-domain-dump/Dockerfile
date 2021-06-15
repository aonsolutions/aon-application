FROM public.ecr.aws/lambda/provided:al2

# Install AWS command-line
RUN yum install --assumeyes aws-cli

# Install mysqldump
RUN yum install --assumeyes mariadb

# Install command-line JSON processor
RUN yum install --assumeyes jq

RUN yum clean all

# Copy custom runtime bootstrap
COPY bootstrap ${LAMBDA_RUNTIME_DIR}
# Copy function code
#COPY function.sh ${LAMBDA_TASK_ROOT}
COPY hello.sh ${LAMBDA_TASK_ROOT}

# Set the CMD to your handler (could also be done as a parameter override outside of the Dockerfile)
CMD [ "hello.handler" ]
