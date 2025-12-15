#####################################################################
# Copyright (c) 2025, AON SOLUTIONS,S.L.U
#
# The copyright of the computer program herein is the property
# of AON SOLUTIONS.
#####################################################################
# The program may be used and/or copied only with the written
# permission of AON SOLUTIONS, or in accordance with the terms
# and conditions stipulated in the agreement contract under
# which the program has been supplied.
####################################################################
#
#                                          _ _ _   
#   __ _  ___  _ __         __ _ _   _  __| (_) |_ 
#  / _` |/ _ \| '_ \ _____ / _` | | | |/ _` | | __|
# | (_| | (_) | | | |_____| (_| | |_| | (_| | | |_ 
#  \__,_|\___/|_| |_|      \__,_|\__,_|\__,_|_|\__|
#
# 
# EVENT_DATA
#
#{
#    ...
#    ...
#}


function handler () {
    EVENT_DATA=$1

    AON_HOST=$(echo ${EVENT_DATA} | jq -r .aon.host )
    AON_DATABASE=$(echo ${EVENT_DATA} | jq -r .aon.database )
    AON_USER=$(echo ${EVENT_DATA} | jq -r .aon.user)
    AON_PASSWORD=$(echo ${EVENT_DATA} | jq -r .aon.password)

    AUDIT_HOST=$(echo ${EVENT_DATA} | jq -r .audit.host )
    AUDIT_DATABASE=$(echo ${EVENT_DATA} | jq -r .audit.database )
    AUDIT_USER=$(echo ${EVENT_DATA} | jq -r .audit.user)
    AUDIT_PASSWORD=$(echo ${EVENT_DATA} | jq -r .audit.password)

    START_DATE=$(echo ${EVENT_DATA} | jq -r .startDate)
    LAST_ACCESS_DATE=$(mysql -h $AUDIT_HOST -u $AUDIT_USER --password=$AUDIT_PASSWORD ${AUDIT_DATABASE} -sN -e "SELECT lastAccess_date FROM domain ORDER BY lastAccess_date DESC LIMIT 1")

    BINARY_LOGS=$(mysql -h $AON_HOST -u $AON_USER --password=$AON_PASSWORD -sN -e 'SHOW BINARY LOGS' | cut -f 1 | xargs)

    mysqlbinlog  --read-from-remote-server --host=${AON_HOST} --port=3306 --user ${AON_USER} --password=${AON_PASSWORD} --start-datetime="${START_DATE:-$LAST_ACCESS_DATE}" --raw --verbose --result-file=/tmp/ ${BINARY_LOGS}

    ls -lha /tmp

    for BINARY_LOG in ${BINARY_LOGS}; do
        mysqlbinlog --base64-output=decode-rows --start-datetime="${START_DATE:-$LAST_ACCESS_DATE}" --verbose /tmp/${BINARY_LOG} \
	| java -jar aon.audit-9.23-SNAPSHOT-jar-with-dependencies.jar ${AON_DATABASE} \
	| mysql -h $AUDIT_HOST -u $AUDIT_USER --password=$AUDIT_PASSWORD ${AUDIT_DATABASE}
    done

    RESPONSE="{\"statusCode\": 400, \"headers\": { } , \"body\":\":-)\"}"
    echo $RESPONSE

}


