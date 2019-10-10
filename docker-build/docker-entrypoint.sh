#!/bin/bash
set -eo pipefail
shopt -s nullglob

#echo 'Initializing database'
#mysql_install_db 
#echo 'Database initialized'

sed -i -s 's/^\s*\(bind-address.*\)$/# \1/' /etc/mysql/mariadb.conf.d/50-server.cnf

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
	GRANT ALL ON *.* TO 'dbuser'@'%' IDENTIFIED BY 'serubd2000' ;
EOSQL

echo
echo 'MySQL init process done. Ready for start up.'
echo

dockerd \
	--host=unix:///var/run/docker.sock \
	--host=tcp://127.0.0.1:2375 &>/var/log/docker.log &

for i in {30..0}; do
        if docker info >/dev/null 2>&1; then
                break
        fi
        echo 'Docker init process in progress...'
        sleep 1
done
if [ "$i" = 0 ]; then
        echo >&2 'Docker init process failed.'
        exit 1
fi

echo
echo 'Docker init process done. Ready for start up.'
echo

echo "127.0.0.1    payroll-test.aonsolutions.org" >> /etc/hosts
echo "127.0.0.1    home-payroll-test.aonsolutions.org" >> /etc/hosts
echo "127.0.0.1    general-payroll-test.aonsolutions.org" >> /etc/hosts
echo "127.0.0.1    trainning-payroll-test.aonsolutions.org" >> /etc/hosts

exec "$@"

