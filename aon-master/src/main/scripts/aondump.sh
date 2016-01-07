
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
  -d, --domain      write domain(s) info
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


function getDomains() {
        mysql -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'` $1 -sNe "SELECT name FROM domain" 
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

function getDomain(){
	>&2 echo "  Selection    Domain"
	>&2 echo "-----------------------------------------------"
	domains=(`getDomains $1`)

	for i in ${!domains[@]}
	do
		>&2 echo "  "$((i+1))"           "${domains[$i]};
	done;
	>&2 echo -n "Type selection number (1-${#domains[@]}) or domain name and press [ENTER]: "
	read selection
	while [[  "1" == "1" ]]; do 
		if [[ -z "$selection" ]]; then 
			>&2 echo -n "Domain CAN NOT be blank"
		elif [[ "$selection" =~ ^[0-9]+$ ]]; then 
			if [[ $selection -ge 1 && $selection -le ${#domains[@]} ]]; then
				domain=${domains[$((selection-1))]}		
				return
			fi
			>&2 echo -n "Selection CAN NOT be $selection"
		else
			for i in ${!domains[@]}
			do
				if [[ "${domains[$i]}" == "$selection" ]]; then
					domain=$selection		
					return
				fi
			done;			
			>&2 echo -n "Domain $selection NOT FOUND"
		fi
		>&2 echo -n ", type selection number (1-${#domains[@]}) or domain name and press [ENTER]: "
		read selection;
	done	

	
}

function getAdminDomain() {
	mysql -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'` $1 -sNe "SELECT id FROM domain WHERE type = 5"
}

function getDomainInfo() {
	mysql -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'` $1 -sNe "SELECT CONCAT(description,' (', name, ')')  FROM domain WHERE id = $2"
}

function getDomainId() {
	mysql -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'` $1 -sNe "SELECT id FROM domain WHERE name = '$2'"
}

function getChildDomainsIds() {
	mysql -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'` $1 -sNe "SELECT id FROM domain WHERE parent = $2"
}

function create(){
	getDatabase

	>&2 echo -n "Type new name for '$database' and press [ENTER]: "
	read name;

        mysqldump -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'`  --databases $database  -d \
	| sed -e "s/"$database"/"$name"/g"
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
	while [[ -z "$name" ]]; do 
		>&2 echo -n "Name CAN NOT be blank, type new name and press [ENTER]: "
		read name;
	done
	
	users=(`getUsers $database $domain`)
	rm_users=()
	cp_users=()
	passwords=()
	for i in ${!users[@]}
	do
		>&2 echo -n "Do you want to add user '"${users[$i]}"' [y/n]: ";
		read -n 1 add
		while [[ ! "$add" =~ [yYnN] ]]; do 
			>&2 echo
			>&2 echo -n "Please answer y or n : ";
			read -n 1 add
		done
		if [[ "$add" =~ [nN] ]]; then 
			>&2 echo
			rm_users+=(${users[$i]})
			continue
		fi
				
		>&2 echo
		>&2 echo -n "Type new password for '"${users[$i]}"' and press [ENTER]: ";
		read password
		cp_users+=(${users[$i]})
		passwords+=(`encrypt $password`)
	done;

	>&2 echo -n "Type new owner for "`getDomainInfo $database $domain`" and press [ENTER]: "
	read owner;
	while [[ -z "$owner" ]]; do 
		>&2 echo -n "Owner CAN NOT be blank, type new name and press [ENTER]: "
		read owner;
	done

	>&2 echo -e "\tDumping data for admin domain '"`getDomainInfo $database $domain`"'  for database '$database'"
        mysqldump -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'`  $database  -t --where="id = $domain" domain | tee >(logTableDump "where id=$domain")
	getDomainTables $database $domain | xargs \
        mysqldump -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'`  $database  -t --where="domain = $domain"| tee >(logTableDump "where domain=$domain")

	echo ""
	echo "--"
	echo "-- Updating passwords for users"
	echo "--"
	echo ""
	for i in ${!cp_users[@]}
	do
		echo "UPDATE \`user\` SET \`password\`='${passwords[$i]}' WHERE \`domain\`=$domain AND \`login\`='${cp_users[$i]}';"
	done;
	echo ""
	
	echo ""
	echo "--"
	echo "-- Deleting users"
	echo "--"
	echo ""
	for i in ${!rm_users[@]}
	do
		echo "DELETE FROM \`application_user_profile\` WHERE \`application_user\` IN ( SELECT \`id\` FROM  \`application_user\` WHERE \`user_id\` IN ( SELECT \`id\` FROM \`user\` WHERE \`login\`='${rm_users[$i]}' ));"
		echo "DELETE FROM \`application_user\` WHERE \`user_id\` IN ( SELECT \`id\` FROM \`user\` WHERE \`login\`='${rm_users[$i]}' );"
		echo "DELETE FROM \`user_workgroup\` WHERE \`user_id\` IN ( SELECT \`id\` FROM \`user\` WHERE \`login\`='${rm_users[$i]}' );"
		echo "DELETE FROM \`user\` WHERE \`domain\`=$domain AND \`login\`='${rm_users[$i]}';"
	done;
	echo ""

	echo ""
	echo "--"
	echo "-- Updating name, suffix, owner, users and dates of domain "
	echo "--"
	echo ""
	echo -n "UPDATE \`domain\`"
	echo -n " SET \`name\`='$name'";
	echo -n ", \`owner\`='$owner'"	
	echo -n ", \`subDomainSuffix\`=NULL"	
	echo -n ", \`creation_user\`=NULL"
	echo -n ", \`creation_date\`=NOW()"
	echo -n ", \`lastAccess_user\`=NULL"
	echo -n ", \`lastAccess_date\`=NULL"
	echo -n ", \`modification_user\`=NULL"
	echo -n ", \`modification_date\`=NULL"
	echo " WHERE \`id\`=$domain;"
	echo ""
	
	echo ""
	echo "--"
	echo "-- Clean personal data ( document... ) from registry "
	echo "--"
	echo ""
	echo -n "UPDATE \`registry\`"
	echo -n " SET \`name\`=NULL";
	echo -n ", \`alias\`=NULL"	
	echo -n ", \`type\`=NULL"	
	echo -n ", \`document\`=NULL"	
	echo -n ", \`document_type\`=NULL"	
	echo -n ", \`document_country\`=NULL"	
	echo -n ", \`nationality\`=NULL"	
	echo " WHERE \`domain\`=$domain;"
	echo ""
	echo ""
	echo "DELETE FROM \`rmedia\` WHERE \`domain\`=$domain;"
	echo ""
	echo "DELETE FROM \`raddress\` WHERE \`domain\`=$domain;"
	echo ""
}

function domain(){
	getDatabase
	getDomain $database

	ignoreTables=(`getNoDomainTables $database`)
	
	ignoreOption="--ignore-table=$database.domain"
	for i in ${!ignoreTables[@]}
	do
		ignoreOption+=" --ignore-table=$database.${ignoreTables[$i]}"
	done	
		
	domainId=`getDomainId $database $domain` 
	childDomainsIds=(`getChildDomainsIds $database $domainId`)

	domains=$domainId
	for i in ${!childDomainsIds[@]}
	do
		domains+=", "${childDomainsIds[i]}
	done	
	
	mysqldump -t  -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'`  $database  domain --where="id IN ( $domains )";

	mysqldump -t  -h `getHostName` -u `getOption 'user'` --password=`getOption 'password'`  $database $ignoreOption --where="domain IN ( $domains )";

}

case $1 in
--admin) admin;;
--domain) domain;;
--create) create;;
--defaults) defaults;;
--version) exec echo "$version";;
*)         exec echo "$usage";;
esac

# I know, very ugly . It's for wait tee (log) to finish
sleep 2s

