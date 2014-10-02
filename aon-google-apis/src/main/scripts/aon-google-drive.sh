#/bin/sh
#
# aon-google-drive     This shell script takes care of manage google drive
#

CLASSPATH=/usr/share/java/mysql-connector-java.jar

CLASSPATH=$CLASSPATH:/usr/share/java/slf4j/api.jar
CLASSPATH=$CLASSPATH:/usr/share/java/slf4j/jcl.jar

CLASSPATH=$CLASSPATH:/usr/share/java/aon-dbutils.jar
CLASSPATH=$CLASSPATH:/usr/share/java/commons-dbutils.jar


for jar in /usr/share/java/aon-google-apis/*.jar ; do 
  CLASSPATH=$CLASSPATH:$jar; 
done 

case $1 in
  help)
    ;;
  sync) 
    java -classpath $CLASSPATH com.code.aon.google.apis.drive.SynchronizeFiles ${*:2} 
    ;;
  delete) 
    java -classpath $CLASSPATH com.code.aon.google.apis.drive.DeleteFiles ${*:2} 
    ;;
  search) 
    java -classpath $CLASSPATH com.code.aon.google.apis.drive.SearchFiles ${*:2}
    ;;
  *) 
    [[ -n $1 ]] && echo "Unknown subcommand: '$1'"
    echo "Type '$0 help' for usage."
    ;; 

esac






