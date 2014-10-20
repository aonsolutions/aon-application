#/bin/sh
#####################################################################
# Copyright (c) 2012, esferalia NETWORKS S.A
#
# The copyright of the computer program herein is the property 
# of esferalia NETWORKS.
#####################################################################
# The program may be used and/or copied only with the written 
# permission of esferalia NETWORKS, or in accordance with the 
# terms and conditions stipulated in the agreement contract 
# under which the program has been supplied.
####################################################################
#                                                _            
#                                               | |           
#    __ _  ___  _ __         _ __ ___   __ _ ___| |_ ___ _ __ 
#   / _` |/ _ \| '_ \   _   | '_ ` _ \ / _` / __| __/ _ \ '__|
#  | (_| | (_) | | | | |_|  | | | | | | (_| \__ \ ||  __/ |   
#   \__,_|\___/|_| |_|      |_| |_| |_|\__,_|___/\__\___|_|   
#                                                           
#                                                           



CLASSPATH=/usr/share/java/mysql-connector-java.jar
CLASSPATH=$CLASSPATH:/usr/share/java/aon-dbutils.jar:/usr/share/java/aon-master.jar
CLASSPATH=$CLASSPATH:/usr/share/java/slf4j/jcl.jar:/usr/share/java/slf4j/api.jar
CLASSPATH=$CLASSPATH:/usr/share/java/commons-dbutils.jar:/usr/share/java/commons-logging.jar:/usr/share/java/commons-lang.jar


ERR=1


function getDBs() {
	mysql -h $1 -u $2 --password=$3 -sNe "SHOW DATABASES" | \
	while read DB; do
		mysql -h $1 -u $2 --password=$3 $DB -e "SELECT 1 FROM registry" &>/dev/null && echo -n ' '$DB;
	done
}

function getOption() {
	local OPTION=$1;
	grep -E "^[[:space:]]*$OPTION[[:space:]]*=" /etc/aon-aio/connection | cut -d"=" -f2
}

#driverClass=org.gjt.mm.mysql.Driver
#jdbcUrl=jdbc:mysql://sig.cdhc46h3yjvt.eu-west-1.rds.amazonaws.com:3306
#user=dbuser
#password=dbpassword


URL=`getOption 'jdbcUrl'`
USERNAME=`getOption 'user'`
PASSWORD=`getOption 'password'`

HOST=`echo $URL | sed -e 's/.*\/\/\([^:^,^\/]*\).*/\1/'`

DBS=`getDBs $HOST $USERNAME $PASSWORD`


for DB in $DBS; do
        echo -n "Actualizando '$DB'..." ;
        ERR=$(java -classpath $CLASSPATH com.code.aon.master.Up2DateDB $URL/$DB  $USERNAME  $PASSWORD com.mysql.jdbc.Driver 2>&1);
        [ $? -eq 0 ] && echo -e "\\033[1;32mOK\\033[0;39m" || echo -e "\\033[1;31mERROR $ERR\\033[0;39m";
done
