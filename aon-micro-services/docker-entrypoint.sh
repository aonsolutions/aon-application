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
	cat << EOF > $AON_MICRO_SERVICES_CONF/connection
driverClass=org.gjt.mm.mysql.Driver
jdbcUrl=jdbc:mysql://$DB_HOST:$DB_PORT
user=$DB_USER
password=$DB_PASSWD
EOF

	CLASSPATH=`find $TOMCAT_LIBDIR -name 'mysql-connector-java-*.jar'`
	CLASSPATH=$CLASSPATH:`find $AON_MICRO_SERVICES_HOME -name 'dbutils-*.jar'`
	CLASSPATH=$CLASSPATH:`find $AON_MICRO_SERVICES_HOME -name 'aon-master-*.jar'`
	CLASSPATH=$CLASSPATH:`find $TOMCAT_LIBDIR -name 'slf4j-api-*.jar'`
	CLASSPATH=$CLASSPATH:`find $TOMCAT_LIBDIR -name 'slf4j-jdk14-*.jar'`
	CLASSPATH=$CLASSPATH:`find $TOMCAT_LIBDIR -name 'commons-lang-*.jar'`
	CLASSPATH=$CLASSPATH:`find $TOMCAT_LIBDIR -name 'commons-dbutils-*.jar'`
	java -classpath $CLASSPATH com.code.aon.master.Up2DateDB \
	jdbc:mysql://$DB_HOST:$DB_PORT $DB_USER $DB_PASSWD org.gjt.mm.mysql.Driver \
	|| echo -e "Can't up2date all databases";

        cat << EOF > $TOMCAT_BINDIR/setenv.sh
CATALINA_OPTS="-Duser.language=es -Duser.country=ES "
EOF
	echo
	echo $(date)
	echo
	echo -e "Using DB_HOST:\t\t$DB_HOST"
	echo -e "Using DB_PORT:\t\t$DB_PORT"
	echo -e "Using DB_USER:\t\t$DB_USER" 
	echo -e "Using DB_PASSWD:\t$DB_PASSWD"
	echo
	echo 'AON init process complete; ready for start up.'
	echo
fi


exec "$@"
