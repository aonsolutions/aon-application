#!/bin/sh

TOMCAT_CONF_DIR=/etc/tomcat8
TOMCAT_CONF_FILE=${TOMCAT_CONF_DIR}/tomcat8.conf
RPM_AIO_TMP_DIR=/tmp/aon-aio-%{release}

echo AIO RPM TMP DIR:
echo ${RPM_AIO_TMP_DIR}

echo
echo Checking tomcat8.conf configuration .......
echo

if [ -f ${TOMCAT_CONF_FILE} ]; then
if grep "Modified by AON INSTALLATION" ${TOMCAT_CONF_FILE}; then
    echo
    echo Tomcat Launch options already set. Nothing to do.
  else
    echo
    echo Tomcat Launch options not set. Adding ...
    echo '#Modified by AON INSTALLATION' >> ${TOMCAT_CONF_FILE}
    echo 'JAVA_OPTS="${JAVA_OPTS} -Djava.security.auth.login.config=${CATALINA_BASE}/conf/login.config"' >> ${TOMCAT_CONF_FILE}
    echo 'JAVA_OPTS="${JAVA_OPTS} -Duser.language=es -Duser.country=ES"' >> ${TOMCAT_CONF_FILE}
  fi
fi
