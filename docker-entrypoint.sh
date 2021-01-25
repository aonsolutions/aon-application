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

	: ${DB_PORT:=3306}
	: ${DB_HOST:=localhost}
	: ${DB_USER:=aonsolutions}
	: ${DB_PASSWD:=40ns0lut10ns}
	: ${DB_TIMEZONE:=Europe/Madrid}
	cat << EOF > $AON_AIO_CONF/connection
driverClass=com.mysql.cj.jdbc.Driver
jdbcUrl=jdbc:mysql://$DB_HOST:$DB_PORT
user=$DB_USER
password=$DB_PASSWD
timezone=$DB_TIMEZONE
useSSL=false
EOF

	CLASSPATH=`find $TOMCAT_LIBDIR -name 'mysql-connector-java-*.jar'`
	CLASSPATH=$CLASSPATH:`find $AON_AIO_HOME -name 'dbutils-*.jar'`
	CLASSPATH=$CLASSPATH:`find $AON_AIO_HOME -name 'aon-master-*.jar'`
	CLASSPATH=$CLASSPATH:`find $TOMCAT_LIBDIR -name 'slf4j-api-*.jar'`
	CLASSPATH=$CLASSPATH:`find $TOMCAT_LIBDIR -name 'slf4j-jdk14-*.jar'`
	CLASSPATH=$CLASSPATH:`find $TOMCAT_LIBDIR -name 'commons-lang-*.jar'`
	CLASSPATH=$CLASSPATH:`find $TOMCAT_LIBDIR -name 'commons-dbutils-*.jar'`
	java -classpath $CLASSPATH com.code.aon.master.Up2DateDB \
	jdbc:mysql://$DB_HOST:$DB_PORT $DB_USER $DB_PASSWD com.mysql.cj.jdbc.Driver \
	|| echo -e "Can't up2date all databases";

	[[ -n $DYNAMODB_MANAGER_REGION_ID ]] && \
	sed -i \
	-e 's/Manager-->/Manager>/' \
	-e 's/<!--Manager/<Manager/' \
	-e 's/DYNAMODB_MANAGER_REGION_ID/'$DYNAMODB_MANAGER_REGION_ID'/' $AON_AIO_HOME/META-INF/context.xml;

        cat << EOF > $TOMCAT_BINDIR/setenv.sh
CATALINA_OPTS="-Duser.language=es \
-Duser.country=ES \
-Djava.security.auth.login.config=$TOMCAT_CONFDIR/login.config \
-Djavax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema=com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory"
EOF
	echo
	echo $(date)
	echo
	echo -e "Using DB_HOST:\t\t$DB_HOST"
	echo -e "Using DB_PORT:\t\t$DB_PORT"
	echo -e "Using DB_USER:\t\t$DB_USER" 
	echo -e "Using DB_PASSWD:\t$DB_PASSWD"
	echo -e "Using DB_TIMEZONE:\t$DB_TIMEZONE"
	[[ -n $MEMCACHED_NODES ]] && echo -e "Using MEMCACHED_NODES:\t$MEMCACHED_NODES" 
	echo
	echo 'AON init process complete; ready for start up.'
	echo
fi


exec "$@"
