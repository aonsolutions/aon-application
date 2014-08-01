#/bin/sh
#####################################################################
# Copyright (c) 2012, esferalia NETWORKS S.A
#
# The copyright of the computer program herein is the property 
# of esferalia NETWORKS.
#####################################################################
# The program may be used and/or copied only with the written 
# permission of esferalia NETWORKS, or in accordance with the 
# terms and conditions stipulated in the agreement contract 
# under which the program has been supplied.
####################################################################
#                                                _            
#                                               | |           
#    __ _  ___  _ __         _ __ ___   __ _ ___| |_ ___ _ __ 
#   / _` |/ _ \| '_ \   _   | '_ ` _ \ / _` / __| __/ _ \ '__|
#  | (_| | (_) | | | | |_|  | | | | | | (_| \__ \ ||  __/ |   
#   \__,_|\___/|_| |_|      |_| |_| |_|\__,_|___/\__\___|_|   
#                                                           
#                                                           



CLASSPATH=/usr/share/java/mysql-connector-java.jar
CLASSPATH=$CLASSPATH:/usr/share/java/aon-dbutils.jar:/usr/share/java/aon-master.jar
CLASSPATH=$CLASSPATH:/usr/share/java/slf4j/jcl.jar:/usr/share/java/slf4j/api.jar
CLASSPATH=$CLASSPATH:/usr/share/java/commons-logging.jar:/usr/share/java/commons-lang.jar


ERR=1

java -classpath $CLASSPATH com.code.aon.dbutils.AonDomainMerger $@  ;


