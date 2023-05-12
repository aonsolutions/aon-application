#!/bin/bash

# exit immediately if a pipeline returns a non-zero status.
# return value of a pipeline is the value of the last
# (rightmost) command to exit with a non-zero status,
# or zero if all commands in the pipeline exit successfully.
set -eo pipefail

# if command starts with an option, prepend catalina.sh
if [ "${1:0:1}" = '-' ]; then
	set -- catalina.sh "$@"
fi

# skip setup if they want an option that stops tomcat
wantHelp=
for arg; do
	case "$arg" in
		stop|configtest|version)
			wantHelp=1
			break
			;;
	esac
done

if [ "$1" = 'catalina.sh' -a -z "$wantHelp" ]; then
	if [ -n "${DB_HOST+x}" ]; then
	: ${DB_PORT:=3306}
	: ${DB_HOST:=localhost}
	: ${DB_USER:=aonsolutions}
	: ${DB_PASSWD:=40ns0lut10ns}
	cat << EOF > $AON_SOLUTIONS_CONF/connection
driverClass=org.gjt.mm.mysql.Driver
jdbcUrl=jdbc:mysql://$DB_HOST:$DB_PORT
user=$DB_USER
password=$DB_PASSWD
EOF
	fi

	if [ -n "${AWS_REGION+x}" ]; then
	: ${AWS_REGION:=eu-west-1}
        cat << EOF > $AWS_HOME/config
[default]
region = $AWS_REGION
EOF
	fi

	if [ -n "${AWS_ACCESS_KEY_ID+x}" ]; then
        cat << EOF > $AWS_HOME/credentials
[default]
aws_access_key_id = $AWS_ACCESS_KEY_ID \
aws_secret_access_key = $AWS_SECRET_ACCESS_KEY
EOF
	fi

        cat << EOF > $TOMCAT_BINDIR/setenv.sh
CATALINA_OPTS="-Duser.language=es \
-Duser.country=ES \
-Djavax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema=com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory"
EOF
	echo
	echo $(date)
	echo
	cat $AON_SOLUTIONS_CONF/connection
	echo
	echo 'AON init process complete; ready for start up.'
	echo

	if [ -z "$TOMCAT_HTTP_PORT" ]
	then
	  echo "Tomcat HTTP port not changed"
	else
	  echo "Tomcat HTTP port set to $TOMCAT_HTTP_PORT"
	  sed -i "s/port=\"[0-9]\+\" protocol=\"HTTP\/1.1\"/port=\"$TOMCAT_HTTP_PORT\" protocol=\"HTTP\/1.1\"/" $CATALINA_HOME/conf/server.xml
	fi

	if [ -z "$TOMCAT_AJP_PORT" ]
	then
	  echo "Tomcat AJP port not changed"
	else
	  echo "Tomcat AJP port set to $TOMCAT_AJP_PORT"
	  sed -i "s/port=\"[0-9]\+\" protocol=\"AJP\/1.3\"/port=\"$TOMCAT_AJP_PORT\" protocol=\"AJP\/1.3\"/" $CATALINA_HOME/conf/server.xml
	fi

	if [ -z "$TOMCAT_SHD_PORT" ]
	then
	  echo "Tomcat shutdown port not changed"
	else
	  echo "Tomcat shutdown port set to $TOMCAT_SHD_PORT"
	  sed -i "s/port=\"[0-9]\+\" shutdown=\"SHUTDOWN\"/port=\"$TOMCAT_SHD_PORT\" shutdown=\"SHUTDOWN\"/" $CATALINA_HOME/conf/server.xml
	fi

fi


exec "$@"
