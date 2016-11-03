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
	cat << EOF > $AON_AIO_CONF/connection
driverClass=org.gjt.mm.mysql.Driver
jdbcUrl=jdbc:mysql://$DB_HOST:$DB_PORT
user=$DB_USER
password=$DB_PASSWD
EOF

        cat << EOF > $TOMCAT_BINDIR/setenv.sh
CATALINA_OPTS="-Duser.language=es -Duser.country=ES -Djava.security.auth.login.config=$TOMCAT_CONFDIR/login.config"
EOF
	echo
	echo	   __ _  ___  _ __
	echo	  / _` |/ _ \| '_ \
	echo	 | (_| | (_) | | | |
	echo	  \__,_|\___/|_| |_|
	echo
	echo

	echo
	echo -e "Using DB_HOST:\t\t$DB_HOST"
	echo -e "Using DB_PORT:\t\t$DB_PORT"
	echo -e "Using DB_USER:\t\t$DB_USER" 
#	echo -e "Using DB_PASSWD:\t$DB_PASSWD"
	echo
	echo 'AON init process complete; ready for start up.'
	echo
fi

exec "$@"
