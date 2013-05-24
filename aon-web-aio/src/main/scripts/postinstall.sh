#!/bin/sh

TOMCAT_CONF_DIR=/etc/tomcat6
RPM_AIO_TMP_DIR=/tmp/aon-aio-%{release}

echo AIO RPM TMP DIR:
echo ${RPM_AIO_TMP_DIR}

echo
echo Checking tomcat6.conf configuration .......
echo

if [ -f ${TOMCAT_CONF_DIR}/tomcat6.conf ]; then
  if grep "Modified by AON INSTALLATION" ${TOMCAT_CONF_DIR}/tomcat6.conf; then
    echo
    echo Tomcat Launch options already set. Nothing to do.
  else
    echo
    echo Tomcat Launch options not set. Adding ...
    sed -i.rpmsaved -e 's/^\([[:space:]]*JAVA_OPTS=.*\)$/\1\
\
# Modified by AON INSTALLATION\
JAVA_OPTS="${JAVA_OPTS} -Xms2048m -Xmx2048m"\
JAVA_OPTS="${JAVA_OPTS} -XX:PermSize=196m -XX:MaxPermSize=196m "\
JAVA_OPTS="${JAVA_OPTS} -Xss1024k"\
JAVA_OPTS="${JAVA_OPTS} -Djava.security.auth.login.config=${CATALINA_BASE}\/conf\/login.config" \
JAVA_OPTS="${JAVA_OPTS} -Dorg.apache.el.parser.COERCE_TO_ZERO=false"\
JAVA_OPTS="${JAVA_OPTS} -Duser.language=es -Duser.country=ES"/' ${TOMCAT_CONF_DIR}/tomcat6.conf
  fi
fi




