#!/bin/sh

: ${DB_PORT:=3306}
: ${DB_HOST:=localhost}
: ${DB_USER:=aonsolutions}
: ${DB_PASSWD:=40ns0lut10ns}
#: ${DB_TABLE:=contract-doc}
: ${AWS_REGION:=eu-south-2}
: ${AWS_ACCESSKEY:=AKIARG5OEKO7FTQF2IP7}
: ${AWS_SECRETKEY:=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}
#: ${S3_BUCKET:=aon-contract-doc}
: ${S3_ENDPOINT:=s3.eu-south-2.amazonaws.com}

echo
echo $(date)
echo
echo -e "Using DB_HOST:\t$DB_HOST"
echo -e "Using DB_PORT:\t$DB_PORT"
echo -e "Using DB_USER:\t$DB_USER"
echo -e "Using DB_PASSWD:\t$DB_PASSWD"
echo -e "Using DB_NAME:\t$DB_NAME"
echo -e "Using DB_TABLE:\t$DB_TABLE"
echo -e "Using S3_BUCKET:\t$S3_BUCKET"
echo -e "Using S3_ENDPOINT:\t$S3_ENDPOINT"
echo -e "Using AWS_REGION:\t$AWS_REGION"
echo -e "Using AWS_ACCESSKEY:\t$AWS_ACCESSKEY"
echo -e "Using AWS_SECRETKEY:\t$AWS_SECRETKEY"
echo


exec java -jar aon-storage.jar \
--host=${DB_HOST} \
--user=${DB_USER} \
--password=${DB_PASSWD} \
--database=${DB_NAME} \
--table=${DB_TABLE} \
--bucket=${S3_BUCKET} \
--endpoint=${S3_ENDPOINT} \
--region=${AWS_REGION} \
--accesskey=${AWS_ACCESSKEY} \
--secretkey=${AWS_SECRETKEY}



