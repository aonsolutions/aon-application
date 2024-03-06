#!/bin/bash
set -eo pipefail
shopt -s nullglob


sudo -u mysql mysqld --datadir='/var/lib/mysql' --sql-mode=0 --default-time-zone='+01:00'  &


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

#mysqladmin -u root password 'new-password'

"${mysql[@]}" -v  <<-EOSQL
	--
	CREATE USER 'dbuser'@'%' IDENTIFIED BY 'serubd2000' ;
	GRANT ALL ON *.* TO 'dbuser'@'%' ;
	--
	-- default privileges include a row with Host='localhost' and User=''. 
	CREATE USER 'dbuser'@'localhost' IDENTIFIED BY 'serubd2000' ;
	GRANT ALL ON *.* TO 'dbuser'@'localhost';
EOSQL


mysql -h127.0.0.1 -udbuser --password=serubd2000 -e "SELECT 1" &> /dev/null

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

docker load < /root/eclipse-temurin:21-jre-jammy.tar
docker load < /root/tomcat:10.1.19-jdk21-temurin-jammy.tar

echo
echo 'Docker init process done. Ready for start up.'
echo

echo "127.0.0.1    payroll-test.aonsolutions.org" >> /etc/hosts
echo "127.0.0.1    tgss-payroll-test.aonsolutions.org" >> /etc/hosts
echo "127.0.0.1    home-payroll-test.aonsolutions.org" >> /etc/hosts
echo "127.0.0.1    agrarian-payroll-test.aonsolutions.org" >> /etc/hosts
echo "127.0.0.1    general-payroll-test.aonsolutions.org" >> /etc/hosts
echo "127.0.0.1    trainning-payroll-test.aonsolutions.org" >> /etc/hosts

exec "$@"

