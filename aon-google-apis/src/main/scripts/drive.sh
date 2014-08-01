#/bin/sh


CLASSPATH=/usr/share/java/mysql-connector-java.jar
CLASSPATH=$CLASSPATH:/usr/share/java/aon-dbutils.jar:/usr/share/java/aon.google.apis.jar
CLASSPATH=$CLASSPATH:/usr/share/java/slf4j/jcl.jar:/usr/share/java/slf4j/api.jar
CLASSPATH=$CLASSPATH:/usr/share/java/commons-cli.jar
CLASSPATH=$CLASSPATH:/usr/share/java/google-api-client-1.18.0-rc.jar
CLASSPATH=$CLASSPATH:/usr/share/java/google-http-client-1.18.0-rc.jar
CLASSPATH=$CLASSPATH:/usr/share/java/google-http-client-jackson2-1.18.0-rc.jar
CLASSPATH=$CLASSPATH:/usr/share/java/javamail/mail-1.4.jar
CLASSPATH=$CLASSPATH:/usr/share/java/aon.sql.google.jar
CLASSPATH=$CLASSPATH:/usr/share/java/google-api-services-drive-v2-rev102-1.17.0-rc.jar
CLASSPATH=$CLASSPATH:/usr/share/java/aon.pool.jar
CLASSPATH=$CLASSPATH:/usr/share/java/javax.servlet-3.0.0.v201112011016.jar
CLASSPATH=$CLASSPATH:/usr/share/java/c3p0-0.9.2.jar
CLASSPATH=$CLASSPATH:/usr/share/java/mchange-commons-java-0.2.3.3.jar
CLASSPATH=$CLASSPATH:/usr/share/java/jooq-3.3.2.jar
CLASSPATH=$CLASSPATH:/usr/share/java/aon.master.jooq.jar
CLASSPATH=$CLASSPATH:/usr/share/java/jackson-core-2.1.3.jar
CLASSPATH=$CLASSPATH:/usr/share/java/google-oauth-client-1.18.0-rc.jar
CLASSPATH=$CLASSPATH:/usr/share/java/aon.entity.common.jar
CLASSPATH=$CLASSPATH:/usr/share/java/aon.common.jar
CLASSPATH=$CLASSPATH:/usr/share/java/commons-lang-2.5.jar
CLASSPATH=$CLASSPATH:/usr/share/java/commons-io-1.4.jar

COMMAND=${1-help}


case $COMMAND in
sync) java -classpath $CLASSPATH com.code.aon.google.apis.drive.SynchronizeFiles ${*:2};;
delete) java -classpath $CLASSPATH com.code.aon.google.apis.drive.DeleteFiles ${*:2};;
search) java -classpath $CLASSPATH com.code.aon.google.apis.drive.SearchFiles ${*:2};;
upload) java -classpath $CLASSPATH com.code.aon.google.apis.drive.UploadFiles ${*:2};;
download) java -classpath $CLASSPATH com.code.aon.google.apis.drive.DownloadFiles ${*:2};;
help) echo "	sync		Sincronizar arhivos con Google Drive.
	delete		Borrar archivos de Google Drive.
	search		Buscar archivos de Google Drive.
	upload		--
	download	--
 	help		Ayuda." ;; 
*) echo “EL comando no existe” ;;
esac

