#!/bin/bash

#####################################################################
# Copyright (c) 2020, AON SOLUTIONS,S.L.U
#
# The copyright of the computer program herein is the property
# of AON SOLUTIONS.
#####################################################################
# The program may be used and/or copied only with the written
# permission of AON SOLUTIONS, or in accordance with the terms
# and conditions stipulated in the agreement contract under
# which the program has been supplied.
####################################################################
#
#      _                       _           _
#     | |                     (_)         | |
#   __| | ___  _ __ ___   __ _ _ _ __   __| |_   _ _ __ ___  _ __
#  / _` |/ _ \| '_ ` _ \ / _` | | '_ \ / _` | | | | '_ ` _ \| '_ \
# | (_| | (_) | | | | | | (_| | | | | | (_| | |_| | | | | | | |_) |
#  \__,_|\___/|_| |_| |_|\__,_|_|_| |_|\__,_|\__,_|_| |_| |_| .__/
#                                                           | |
#                                                           |_|
#
#
#

VERSION="aondump 1.0
Copyright (c) 2020, AON SOLUTIONS,S.L.U.

The copyright of the computer program herein is the property
of AON SOLUTIONS.

The program may be used and/or copied only with the written
permission of AON SOLUTIONS, or in accordance with the terms
and conditions stipulated in the agreement contract under
which the program has been supplied.

"

USAGE="Usage: $0 [OPTIONS] database [domains]
Dumping structure and contents of AON databases and domains.

  -h, --host=<name>	Connect to host.
  -P, --port=<#>        Port number to use for connection.
  -u, --user=<name>	User for the connection to the server.
  -p, --password=[<pass>]
			Password to use when connecting to server. If
			password is empty, connection will be made
			without using a password.
  -n, --no-create-db  	Suppress the CREATE DATABASE ... IF EXISTS statement that
                      	normally is output for each dumped domain.
  -t, --no-create-info	Don't write table creation info.
  --replace           	Use REPLACE INTO instead of INSERT INTO.
  -V, --version       	Output version information and exit.

Report bugs to <soporte@aonsolutions.es >."



# Initialize our own variables:
port=3306
HOST=127.0.0.1
USER=""
PASSWORD=""
REPLACE=""
NO_CREATE_DB=""
NO_CREATE_INFO=""
SKIP_COMMENTS="--skip-comments"

while :
do
    case "$1" in
      -h | --host)
	  HOST="$2"   # You may want to check validity of $2
	  shift 2
	  ;;
      -h | --help)
	  echo "$USAGE"  # Call your function
	  # no shifting needed here, we're done.
	  exit 0
	  ;;
      -u | --user)
	  USER="$2" # You may want to check validity of $2
	  shift 2
	  ;;
      -p | --password)
	  PASSWORD="$2" # You may want to check validity of $2
	  shift 2
	  ;;
      -n | --no-create-db)
	  NO_CREATE_DB="--no-create-db"
	  shift
	  ;;
      -t | --no-create-info)
	  NO_CREATE_INFO="--no-create-info"
	  shift
	  ;;
      --replace)
	  REPLACE="--replace"
	  shift
	  ;;
      -v | --verbose)
          #  It's better to assign a string, than a number like "verbose=1"
	  #  because if you're debugging the script with "bash -x" code like this:
	  #
	  #    if [ "$verbose" ] ...
	  #
	  #  You will see:
	  #
	  #    if [ "verbose" ] ...
	  #
          #  Instead of cryptic
	  #
	  #    if [ "1" ] ...
	  #
	  VERBOSE="verbose"
	  shift
	  ;;
      -V | --version)
          echo "$VERSION"  # Call your function
          # no shifting needed here, we're done.
          exit 0
          ;;         --) # End of all options
	  shift
	  break
	  ;;
      -*)
	  echo "Error: Unknown option: $1" >&2
	  exit 1
	  ;;
      *)  # No more options
	  break
	  ;;
    esac
done

ECHO="echo 1>&2"

DATABASE=$1
DOMAIN=$2

MYSQL="mysql -h $HOST -p $PORT -u $USER --password=$PASSWORD $DATABASE"

DOMAIN_TABLES=()
SYSTEM_TABLES=()
DOMAIN_ID=$($MYSQL -e "SELECT \`id\` FROM \`domain\` WHERE \`name\`='$DOMAIN'" -sN 2>/dev/null)

while read table; do
	if [ "$table" = "domain" ]; then
		continue;
	fi
	$MYSQL -e "SELECT \`domain\` FROM \`$table\` LIMIT 1" &>/dev/null && DOMAIN_TABLES+=($table) || SYSTEM_TABLES+=($table);
done < <($MYSQL -e "SHOW TABLES" -sN 2>/dev/null)

DUMP="mysqldump $SKIP_COMMENTS $REPLACE -h $HOST -p $PORT -u $USER --password=$PASSWORD --single-transaction"

$DUMP --no-data $NO_CREATE_DB $NO_CREATE_INFO --databases $DATABASE 2>/dev/null

$DUMP --no-create-info  $DATABASE $(printf "%s "  "${SYSTEM_TABLES[@]}") 2>/dev/null

$DUMP --no-create-info --where="id=0"  $DATABASE domain 2>/dev/null

$DUMP --no-create-info --where="domain=0"  $DATABASE $(printf "%s "  "${DOMAIN_TABLES[@]}") 2>/dev/null

$DUMP --no-create-info --where="domain IS NULL"  $DATABASE $(printf "%s "  "${DOMAIN_TABLES[@]}") 2>/dev/null

$DUMP --no-create-info --where="id=$DOMAIN_ID"  $DATABASE domain  2>/dev/null

$DUMP --no-create-info --where="domain=$DOMAIN_ID"  $DATABASE $(printf "%s "  "${DOMAIN_TABLES[@]}") 2>/dev/null

$DUMP --no-create-info --where="id IN (SELECT id FROM domain WHERE parent=$DOMAIN_ID)"  $DATABASE domain 2>/dev/null

$DUMP --no-create-info --where="domain IN (SELECT id FROM domain WHERE parent=$DOMAIN_ID)"  $DATABASE $(printf "%s "  "${DOMAIN_TABLES[@]}") 2>/dev/null

$DUMP --no-create-info --where="id IN (SELECT parent FROM domain WHERE id=$DOMAIN_ID)"  $DATABASE domain 2>/dev/null

$DUMP --no-create-info --where="domain IN (SELECT parent FROM domain WHERE id=$DOMAIN_ID)"  $DATABASE $(printf "%s "  "${DOMAIN_TABLES[@]}") 2>/dev/null

echo "UPDATE user SET password='0jtZh1BMGz3khL8uR8dvdau3lNM=' WHERE domain=$DOMAIN_ID;"

echo "UPDATE user SET password='0jtZh1BMGz3khL8uR8dvdau3lNM=' WHERE domain IN (SELECT id FROM domain WHERE parent=$DOMAIN_ID);"

echo "UPDATE user SET password='0jtZh1BMGz3khL8uR8dvdau3lNM=' WHERE domain IN (SELECT parent FROM domain WHERE id=$DOMAIN_ID);"



