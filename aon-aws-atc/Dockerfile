FROM public.ecr.aws/amazoncorretto/amazoncorretto:8-al2023

# Configure environment
#RUN yum install -y cabextract
RUN yum install -y cabextract xorg-x11-font-utils fontconfig
RUN yum install  -y https://downloads.sourceforge.net/project/mscorefonts2/rpms/msttcore-fonts-installer-2.6-1.noarch.rpm
RUN fc-cache -v -f

WORKDIR /function

COPY  target/dependency/*.jar ./
COPY  target/*.jar ./

# Set runtime interface client as default command for the container runtime
ENTRYPOINT [ "/usr/bin/java", "-cp", "./*", "com.amazonaws.services.lambda.runtime.api.client.AWSLambda" ]
# Pass the name of the function handler as an argument to the runtime
CMD [ "net.aonsolutions.aws.atc.lambda.MIModelo420RequestHandler::handleRequest" ]
