#!/bin/sh
#####################################################################
# Copyright (c) 2015, AON SOLUTIONS,S.L.U
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

temp_dir=$(mktemp -d)
mkdir ${temp_dir}/files
mkdir ${temp_dir}/scripts

for rpm_file in $(find -name aon-aio8*.rpm -o -name aon-common*.rpm -o -name aon-infoweb*.rpm -o -name aon.aeat*.rpm -o -name aon-master*.rpm -o -name aon-dbutils*.rpm -o -name aon-msm8*.rpm -o -name aon.google.apis*.rpm ); do

	rpm_file=$(cd "$(dirname "$rpm_file")"; pwd)/$(basename "$rpm_file")

	pushd ${temp_dir}/files
	rpm2cpio ${rpm_file} | cpio -imd
	popd

	rpm -qlcp ${rpm_file} | while read file; do
		mkdir -p $(dirname  ${temp_dir}/conf${file}) 
		mv ${temp_dir}/files${file}  ${temp_dir}/conf${file} 
	done

	permissions+=`IF_DIR=0040000; 
	rpm --dump -qlp ${rpm_file} | while read line; do 
		arr=($line); 
		[ -e ${temp_dir}/files/${arr[0]} ] || continue;
		[ -L ${temp_dir}/files/${arr[0]} ] && continue;
		mode=${arr[4]}; 
		owner=${arr[5]}; 
		group=${arr[6]}; 
		object=$(dirname ${arr[0]}); 
		pattern=$(basename ${arr[0]}); 
		(( $IF_DIR & $mode )) && type=directory || type=file;  
		echo -e "  - object: ${object}\r\n    pattern: \"${pattern}\"\r\n    owner: $owner\r\n    group: $group\r\n    mode: ${mode:(-3)}\r\n    type:\r\n      - $type";  
	done`

	[[ $(rpm --info -qp $rpm_file ) =~ ^Name[[:space:]]*:[[:space:]]*([^[:space:]]+) ]] && name=${BASH_REMATCH[1]};

	OLD_IFS="$IFS"
	IFS=
	rpm --scripts -qp ${rpm_file} | while read line; do
		[[ $line =~ ^([^[:space:]]*)[[:space:]]scriptlet(.*):$ ]] && script=${BASH_REMATCH[1]} && echo -n '' > ${temp_dir}/scripts/${script}_${name} && continue;
		echo -e "$line" >> ${temp_dir}/scripts/${script}_${name};
	done
	IFS="$OLD_IFS"
done

pushd ${temp_dir}

cat << EOF > scripts/links
#!/bin/bash
$(for link in $(find -type l); do echo -e "ln -sf $(readlink -f $link) ${link##./files};"; done)
EOF

for link in $(find -type l); do 
	rm -rf $link; 
done

cat << EOF > scripts/stop_server
#!/bin/bash
service tomcat8 stop
EOF

cat << EOF > scripts/start_server
#!/bin/bash
service tomcat8 start
EOF

cat << EOF > scripts/cleanup
#!/bin/bash
$(
OLD_IFS="$IFS"
IFS=$'\n'
for file in $(find files -type f); do
file=$(printf %q "$file")
file=${file/-[[:digit:]]*./-\*.}
echo "rm -f ${file#files}"
done
IFS="$OLD_IFS"
)
EOF


cat << EOF > appspec.yml
version: 0.0
os: linux 
files:
  - source: files
    destination: /
permissions:
${permissions}
hooks:
  ApplicationStop:
    - location: scripts/stop_server
      timeout: 300
      runas: root
  BeforeInstall:
    - location: scripts/cleanup
      timeout: 300
      runas: root
  AfterInstall:
    - location: scripts/links
      runas: root
$(for script in $(find scripts -name postinstall*); do 
echo "    - location: ${script}
      timeout: 300
      runas: root"
done)
  ApplicationStart:
    - location: scripts/start_server
      timeout: 300
      runas: root
EOF

popd

cat << EOF | aws configure
AKIAILS6NXXC5HKMLPYA
ooBCbXh1R+Aut/iQM+uipDbCqrLvcNyJtG8ard71
eu-west-1


EOF

APP=${1:-AON-SNAPSHOT-APP}
GROUP=${1:-AON-NET-GROUP}

aws deploy push --application-name ${APP} --s3-location s3://aon-solutions/${APP,,}-${VERSION}-${BUILD_ID}.zip --source ${temp_dir}

aws deploy create-deployment --application-name ${APP} --s3-location bucket=aon-solutions,key=${APP,,}-${VERSION}-${BUILD_ID}.zip,bundleType=zip --deployment-group-name ${GROUP}  --deployment-config-name  CodeDeployDefault.AllAtOnce  


