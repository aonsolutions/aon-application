#!/bin/sh

: ${DB_PORT:=3306}
: ${DB_HOST:=localhost}
: ${DB_USER:=aonsolutions}
: ${DB_PASSWD:=40ns0lut10ns}

echo
echo $(date)
echo
echo -e "Using DB_NAME:\t\t$DB_NAME"
echo -e "Using DB_HOST:\t\t$DB_HOST"
echo -e "Using DB_PORT:\t\t$DB_PORT"
echo -e "Using DB_USER:\t\t$DB_USER" 
echo -e "Using DB_PASSWD:\t$DB_PASSWD"
echo


exec java -jar aon-tests.jar --host=${DB_HOST} --port=${DB_PORT} --user=${DB_USER} --password=${DB_PASSWD} 

