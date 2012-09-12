#!/bin/sh

TOMCAT_CONF_DIR=/etc/tomcat6
RPM_AIO_TMP_DIR=/tmp/aon-aio-%{release}

echo AIO RPM TMP DIR:
echo ${RPM_AIO_TMP_DIR}

echo
echo Checking server.xml configuration .......
echo


if grep "com.code.aon.jaas.vendor.tomcat.SecurityLifecycleListener" ${TOMCAT_CONF_DIR}/server.xml; then
 echo
 echo "Listener 'com.code.aon.jaas.vendor.tomcat.SecurityLifecycleListener' already exists in server.xml. Nothing to do."
 echo
else
 cp ${TOMCAT_CONF_DIR}/server.xml ${TOMCAT_CONF_DIR}/server.xml.rpmsaved
 echo
 echo "Listener 'com.code.aon.jaas.vendor.tomcat.SecurityLifecycleListener' Not found. Adding ...."
 echo
 xsltproc ${RPM_AIO_TMP_DIR}/server.xsl ${TOMCAT_CONF_DIR}/server.xml | diff ${TOMCAT_CONF_DIR}/server.xml - | patch ${TOMCAT_CONF_DIR}/server.xml
fi


if grep "com.code.aon.bridge.jmx.mbean.core.TomcatConsoleAdminFactory" ${TOMCAT_CONF_DIR}/context.xml; then
 echo
 echo "Tomcat console already exists in context.xml. Nothing to do."
 echo
else
 cp ${TOMCAT_CONF_DIR}/context.xml ${TOMCAT_CONF_DIR}/context.xml.rpmsaved
 echo
 echo "Tomcat console not found. Adding ...."
 echo
 xsltproc ${RPM_AIO_TMP_DIR}/context.xsl ${TOMCAT_CONF_DIR}/context.xml | diff ${TOMCAT_CONF_DIR}/context.xml - | patch ${TOMCAT_CONF_DIR}/context.xml
fi


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
JAVA_OPTS="${JAVA_OPTS} -Xms512m -Xmx1024m -XX:MaxPermSize=196m -Xss1024k" \
JAVA_OPTS="${JAVA_OPTS} -Djava.security.auth.login.config=${CATALINA_BASE}\/conf\/login.config" \
JAVA_OPTS="${JAVA_OPTS} -Dorg.apache.el.parser.COERCE_TO_ZERO=false"/' ${TOMCAT_CONF_DIR}/tomcat6.conf
  fi
fi
