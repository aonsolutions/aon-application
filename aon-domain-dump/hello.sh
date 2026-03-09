#####################################################################
# Copyright (c) 2020, AON SOLUTIONS,S.L.U
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
#      _                       _           _
#     | |                     (_)         | |
#   __| | ___  _ __ ___   __ _ _ _ __   __| |_   _ _ __ ___  _ __
#  / _` |/ _ \| '_ ` _ \ / _` | | '_ \ / _` | | | | '_ ` _ \| '_ \
# | (_| | (_) | | | | | | (_| | | | | | (_| | |_| | | | | | | |_) |
#  \__,_|\___/|_| |_| |_|\__,_|_|_| |_|\__,_|\__,_|_| |_| |_| .__/
#                                                           | |
#                                                           |_|
#
#
#

# EVENT_DATA
#
#{
#    ...
#    "path": "/release.cluster-cdhc46h3yjvt.eu-west-1.rds.amazonaws.com/pro-aonsolutions-net/isho.aonsolutions.net",
#    "httpMethod": "GET",
#    "queryStringParameters": {
#        "replace": "true",
#        "no-create-db": "true",
#        "user": "aonsolutons",
#        "password": "40ns0lut10ns",
#        "ignore-table": "invoice_attach",
#	 ...
#    },
#    ...
#}


function handler () {
    EVENT_DATA=$1

    HOST=$(echo ${EVENT_DATA} | jq -r .path | cut -d/ -f2)
    DATABASE=$(echo ${EVENT_DATA} | jq -r .path | cut -d/ -f3)
    DOMAIN=$(echo ${EVENT_DATA} | jq -r .path | cut -d/ -f4)

    USER=$(echo ${EVENT_DATA} | jq -r .queryStringParameters.user)
    PASSWORD=$(echo ${EVENT_DATA} | jq -r .queryStringParameters.password)

    REPLACE=$(echo ${EVENT_DATA} | jq -r .queryStringParameters.replace | sed -e 's/null//')
    NO_CREATE_DB=$(echo ${EVENT_DATA} | jq -r .queryStringParameters.no_create_db | sed -e 's/null//')
    SKIP_COMMENTS=$(echo ${EVENT_DATA} | jq -r .queryStringParameters.skip_comments | sed -e 's/null//')
    NO_CREATE_INFO=$(echo ${EVENT_DATA} | jq -r .queryStringParameters.no_create_info | sed -e 's/null//')

    IGNORE_TABLES=$(echo ${EVENT_DATA} | jq -r .queryStringParameters.ignore_table | sed -e 's/null//' -e 's/,/ /g' -e 's/%\([0-9A-F][0-9A-F]\)/\\\\\x\1/g' | xargs echo -e )

    MYSQL="mysql -h $HOST -u $USER --password=$PASSWORD $DATABASE"
    DUMP="mysqldump $SKIP_COMMENTS $REPLACE -h $HOST -u $USER --password=$PASSWORD --single-transaction --set-gtid-purged=OFF"

    DOMAIN_ID=$($MYSQL -e "SELECT \`id\` FROM \`domain\` WHERE \`name\`='$DOMAIN'" -sN 2>/dev/null)

#    DOMAIN_TABLES=()
#    SYSTEM_TABLES=()
#    while read table; do
#        if [ "$table" = "domain" ]; then
#                continue;
#        fi
#        $MYSQL -e "SELECT \`domain\` FROM \`$table\` LIMIT 1" &>/dev/null && DOMAIN_TABLES+=($table) || SYSTEM_TABLES+=($table);
#    done < <($MYSQL -e "SHOW TABLES" -sN 2>/dev/null)

    DOMAIN_TABLES=(
    $($MYSQL -e "SHOW TABLES" -sN 2>/dev/null |
    while read table; do
        if [ "$table" = "domain" ]; then
                continue;
        fi
	if [[ " ${IGNORE_TABLES[*]} " =~ " ${table} " ]]; then
		continue;
	fi ;
        $MYSQL -e "SELECT \`domain\` FROM \`$table\` LIMIT 1" &>/dev/null && echo $table ;
    done))

    SYSTEM_TABLES=(
    $($MYSQL -e "SHOW TABLES" -sN 2>/dev/null |
    while read table; do
        if [ "$table" = "domain" ]; then
                continue;
        fi
        $MYSQL -e "SELECT \`domain\` FROM \`$table\` LIMIT 1" &>/dev/null || echo $table ;
    done))


    $DUMP --no-data $NO_CREATE_DB $NO_CREATE_INFO --databases $DATABASE 2>/dev/null | gzip >/tmp/${DOMAIN}.sql.gz

    $DUMP --no-create-info  $DATABASE $(printf "%s "  "${SYSTEM_TABLES[@]}") 2>/dev/null | gzip >>/tmp/${DOMAIN}.sql.gz

    $DUMP --no-create-info --where="id<=0"  $DATABASE domain 2>/dev/null | gzip >>/tmp/${DOMAIN}.sql.gz

    $DUMP --no-create-info --where="domain<=0"  $DATABASE $(printf "%s "  "${DOMAIN_TABLES[@]}") 2>/dev/null | gzip >>/tmp/${DOMAIN}.sql.gz

    $DUMP --no-create-info --where="domain IS NULL"  $DATABASE $(printf "%s "  "${DOMAIN_TABLES[@]}") 2>/dev/null | gzip >>/tmp/${DOMAIN}.sql.gz

    $DUMP --no-create-info --where="id=$DOMAIN_ID"  $DATABASE domain  2>/dev/null | gzip >>/tmp/${DOMAIN}.sql.gz

    $DUMP --no-create-info --where="domain=$DOMAIN_ID"  $DATABASE $(printf "%s "  "${DOMAIN_TABLES[@]}") 2>/dev/null | gzip >>/tmp/${DOMAIN}.sql.gz

    $DUMP --no-create-info --where="id IN (SELECT id FROM domain WHERE parent=$DOMAIN_ID)"  $DATABASE domain 2>/dev/null | gzip >>/tmp/${DOMAIN}.sql.gz

    $DUMP --no-create-info --where="domain IN (SELECT id FROM domain WHERE parent=$DOMAIN_ID)"  $DATABASE $(printf "%s "  "${DOMAIN_TABLES[@]}") 2>/dev/null | gzip >>/tmp/${DOMAIN}.sql.gz

    $DUMP --no-create-info --where="id IN (SELECT parent FROM domain WHERE id=$DOMAIN_ID)"  $DATABASE domain 2>/dev/null | gzip >>/tmp/${DOMAIN}.sql.gz

    $DUMP --no-create-info --where="domain IN (SELECT parent FROM domain WHERE id=$DOMAIN_ID)"  $DATABASE $(printf "%s "  "${DOMAIN_TABLES[@]}") 2>/dev/null | gzip >>/tmp/${DOMAIN}.sql.gz

    echo "UPDATE user SET password='0jtZh1BMGz3khL8uR8dvdau3lNM=' WHERE domain=$DOMAIN_ID;"  | gzip >>/tmp/${DOMAIN}.sql.gz
    echo "UPDATE user SET password='0jtZh1BMGz3khL8uR8dvdau3lNM=' WHERE domain IN (SELECT id FROM domain WHERE parent=$DOMAIN_ID);"  | gzip >>/tmp/${DOMAIN}.sql.gz
    echo "UPDATE user SET password='0jtZh1BMGz3khL8uR8dvdau3lNM=' WHERE domain IN (SELECT parent FROM domain WHERE id=$DOMAIN_ID);"  | gzip >>/tmp/${DOMAIN}.sql.gz
    
    #gzip -f /tmp/${DOMAIN}.sql  &>/dev/null
    
    aws s3 cp /tmp/${DOMAIN}.sql.gz s3://aon-dump/ --acl public-read &>/dev/null

    RESPONSE="{\"statusCode\": 302, \"headers\": { \"Location\":\"https://aon-dump.s3-eu-west-1.amazonaws.com/${DOMAIN}.sql.gz\"} }"
    echo $RESPONSE

}


