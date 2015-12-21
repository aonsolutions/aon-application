#/bin/sh
#
# aon-warehouse     This shell script takes care of manage google drive
#


JAVA=java
# Turning off logging for Hibernate c3p0
JAVA=$JAVA" -Dcom.mchange.v2.log.MLog=com.mchange.v2.log.FallbackMLog"
JAVA=$JAVA" -Dcom.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL=WARNING"
# Damn those wheel-reinvented custom logging implementations, like the one used by c3p0...


CLASSPATH=/usr/share/java/mysql-connector-java.jar

CLASSPATH=$CLASSPATH:/usr/share/java/slf4j/api.jar
CLASSPATH=$CLASSPATH:/usr/share/java/slf4j/simple.jar

CLASSPATH=$CLASSPATH:/usr/share/java/aon-dbutils.jar
CLASSPATH=$CLASSPATH:/usr/share/java/commons-dbutils.jar
CLASSPATH=$CLASSPATH:/usr/share/java/commons-codec.jar
CLASSPATH=$CLASSPATH:/usr/share/java/commons-net-3.3.jar

for jar in /usr/share/java/aon-ui-warehouse/*.jar ; do 
  CLASSPATH=$CLASSPATH:$jar; 
done 


case $1 in
  help)
  	echo "usage: $0 <subcommand> [options]"
  	echo "aon Warehouse command-line client."
  	echo "Type '$0 <subcommand> help' for help on a specific subcommand."
  	echo ""
  	echo "Available subcommands"
  	echo "calculate"
  	echo ""
  	echo "aon Solutions is a business management software."
  	echo "For additional information, see http://www.aonsolutions.es/"
    ;;
  calculate) 
    $JAVA -classpath $CLASSPATH com.code.aon.ui.warehouse.rpm.CalculatePurchasePrice "${@:2}" 
    ;;
  *) 
    [[ -n $1 ]] && echo "Unknown subcommand: '$1'"
    echo "Type '$0 help' for usage."
    ;; 

esac






