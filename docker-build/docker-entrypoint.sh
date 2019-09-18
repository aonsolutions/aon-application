#!/bin/bash
set -eo pipefail
shopt -s nullglob

#echo 'Initializing database'
#mysql_install_db 
#echo 'Database initialized'

mysqld_safe --datadir='/var/lib/mysql' &


mysql=( mysql -uroot -hlocalhost )

for i in {30..0}; do
	if echo 'SELECT 1' | "${mysql[@]}" &> /dev/null; then
		break
	fi
	echo 'MySQL init process in progress...'
	sleep 1
done
if [ "$i" = 0 ]; then
	echo >&2 'MySQL init process failed.'
	exit 1
fi

mysqladmin -u root password 'new-password'

"${mysql[@]}" <<-EOSQL
	-- 
	-- 
	GRANT ALL ON *.* TO 'dbuser'@'localhost' IDENTIFIED BY 'serubd2000' ;
EOSQL

echo
echo 'MySQL init process done. Ready for start up.'
echo

exec "$@"
