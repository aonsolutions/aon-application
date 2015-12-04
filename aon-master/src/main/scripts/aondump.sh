
#/bin/sh
#####################################################################
# Copyright (c) 2015, AON SOLUTIONS,S.L.U
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
#                        _
#                       | |
#   __ _  ___  _ __   __| |_   _ _ __ ___  _ __
#  / _` |/ _ \| '_ \ / _` | | | | '_ ` _ \| '_ \
# | (_| | (_) | | | | (_| | |_| | | | | | | |_) |
#  \__,_|\___/|_| |_|\__,_|\__,_|_| |_| |_| .__/
#                                         | |
#                                         |_|

version="aondump 8.34
Copyright (c) 2015, AON SOLUTIONS,S.L.U.

The copyright of the computer program herein is the property
of AON SOLUTIONS.

The program may be used and/or copied only with the written
permission of AON SOLUTIONS, or in accordance with the terms
and conditions stipulated in the agreement contract under
which the program has been supplied.

"

usage="Usage: $0 [OPTION]...
Dumping structure and contents of AON databases and domains.

  -a, --admin       write database admin domain info
  -c, --create      write database creation info
  -d, --defaults    write database defaults info
  -v, --verbose     verbose mode
      --help        display this help and exit
      --version     display version information and exit


Report bugs to <soporte@aonsolutions.es >."

function contains() {
	local e
	for e in "${@:2}"; do [[ "$e" == "$1" ]] && return 0; done
  	return 1
}


function encrypt() {
	python -c "import hashlib; import base64; print base64.encodestring(hashlib.sha1('$1').digest()).strip()"
}

function logTableDump(){
	while read line; do 
		message=`echo $line | grep -Eo "Dumping data for table .*"`
		if [ -n "$message" ]; then 
			>&2 echo -e "\t"$message" "$1
		fi
	done 
}

function getOption() {
        local OPTION=$1;
        grep -E "^[[:space:]]*$OPTION[[:space:]]*=" /etc/aon-aio/connection | cut -d"=" -f2
}

function getHostName() {
	getOption 'jdbcUrl' | sed -e 's/.*\/\/\([^:^,^\/]*\).*/\1/'
}


function getUsers() {
        mysql -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'` $1 -sNe "SELECT login FROM user WHERE domain=$2"
}

function getDatabases() {
        mysql -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'` -sNe "SHOW DATABASES" | \
        while read DB; do
                mysql -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'` $DB -e "SELECT 1 FROM registry" &>/dev/null && echo $DB;
        done
}


function getDatabase(){
	>&2 echo "  Selection    Database"
	>&2 echo "-----------------------------------------------"
	databases=(`getDatabases`)

	for i in ${!databases[@]}
	do
		>&2 echo "  "$((i+1))"           "${databases[$i]};
	done;
	>&2 echo -n "Type selection number and press [ENTER]: "
	read selection
	database=${databases[$((selection-1))]}
}

function getAdminDomain() {
	mysql -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'` $1 -sNe "SELECT id FROM domain WHERE type = 5"
}

function getDomainInfo() {
	mysql -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'` $1 -sNe "SELECT CONCAT(description,' (', name, ')')  FROM domain WHERE id = $2"
}

function create(){
	getDatabase
        mysqldump -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'`  --databases $database  -d
}

function getDomainTables() {
	sysTables=(`getSysDomainTables $1`)
        mysql -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'` $1 -sNe "SHOW TABLES" | \
        while read table; do
		[[ " ${sysTables[@]} " =~ " ${table} " ]] && continue
                [[ "1" == "$(mysql -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'` $1 -sNe "SELECT 1 FROM $table WHERE domain=$2 LIMIT 1" 2>/dev/null)" ]] && echo $table;
        done
}

function getNoDomainTables() {
        mysql -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'` $1 -sNe "SHOW TABLES" | \
        while read table; do
		[[ "$table" == "domain" ]]  && continue
                mysql -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'` $1 -e "SELECT domain FROM $table" &>/dev/null || echo $table;
        done
}

function getNullDomainTables() {
        mysql -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'` $1 -sNe "SHOW TABLES" | \
        while read table; do
                [[ "1" == "$(mysql -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'` $1 -sNe "SELECT 1 FROM $table WHERE domain IS NULL LIMIT 1" 2>/dev/null)" ]] && echo $table;
        done
}

function getSysDomainTables() {
        mysql -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'` $1 -sNe "SHOW TABLES" | \
        while read table; do
		[[ "$table" =~ ^(system_.*) ]] && echo $table && continue 
		[[ "$table" =~ ^(.*_concept) ]] && echo $table && continue 
#                [[ "1" == "$(mysql -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'` $1 -sNe "SELECT 1 FROM $table #WHERE domain < 0 LIMIT 1" 2>/dev/null)" ]] && echo $table;
		
        done
}


function defaults(){
	getDatabase
	>&2 echo -e "\tDumping defaults for database '$database'"
	getNoDomainTables $database | xargs \
        mysqldump -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'`  $database  -t | tee >(logTableDump)
	getNullDomainTables $database  | xargs \
        mysqldump -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'`  $database  -t --where="domain IS NULL" \
	| tee >(logTableDump "where domain is NULL")
	getSysDomainTables $database  | xargs \
        mysqldump -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'`  $database  -t --where="domain <=0 " \
	| tee >(logTableDump "where domain <= 0")
	
}

function admin(){
	getDatabase
	domain=`getAdminDomain $database`

	>&2 echo -n "Type new name for "`getDomainInfo $database $domain`" and press [ENTER]: "
	read name;
	
	users=(`getUsers $database $domain`)
	for i in ${!users[@]}
	do
		>&2 echo -n "Type new password for '"${users[$i]}"' and press [ENTER]: ";
		read password
		passwords[$i]=`encrypt $password`
	done;

	>&2 echo -e "\tDumping data for admin domain '"`getDomainInfo $database $domain`"'  for database '$database'"
        mysqldump -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'`  $database  -t --where="id = $domain" domain | tee >(logTableDump "where id=$domain")
	getDomainTables $database $domain | xargs \
        mysqldump -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'`  $database  -t --where="domain = $domain"| tee >(logTableDump "where domain=$domain")

	echo ""
	echo "--"
	echo "-- Updating passwords for users"
	echo "--"
	echo ""
	for i in ${!users[@]}
	do
		echo "UPDATE user SET password='${passwords[$i]}' WHERE domain=$domain AND login='${users[$i]}';"
	done;
	echo ""
	
	echo ""
	echo "--"
	echo "-- Updating name of domain "
	echo "--"
	echo ""
	echo "UPDATE domain SET name='$name' WHERE domain=$domain;"
	echo ""
	
}


case $1 in
--admin) admin;;
--create) create;;
--defaults) defaults;;
--version) exec echo "$version";;
*)         exec echo "$usage";;
esac

# I know, very ugly . It's for wait tee (log) to finish
sleep 2s

