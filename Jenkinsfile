   properties ([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5']]
		, [$class: 'GithubProjectProperty', displayName: '', projectUrlStr: 'https://github.com/aonsolutions/aon-application/']
		, [$class: 'ParametersDefinitionProperty', parameterDefinitions: [[$class: 'BooleanParameterDefinition', defaultValue: false, description: '', name: 'MAVEN_RELEASE']]]	
	])

node {

   // Mark the code checkout 'stage'....
   stage 'Checkout'

   // Get some code from a GitHub repository
   git url: 'https://github.com/aonsolutions/aon-application', credentialsId: '0057f1a5-ba06-4421-8785-7288a1eecfc4'

   // Get the maven tool.
   // ** NOTE: This 'M3' maven tool must be configured
   // **       in the global configuration.           
   def mvnHome = tool 'M3'
   
   // we want to pick up the version from the pom
   def pom = readMavenPom file: 'pom.xml'
   
   if ( env.MAVEN_RELEASE ) {
      stage 'Perform Maven Release'
   
      def tag = pom.version.replace("-SNAPSHOT", ".x")

      def releaseVersion = pom.version.replace("-SNAPSHOT", "")
  
      def developmentVersion = pom.version.replace("-SNAPSHOT", "-SNAPSHOT")
  
      def mavenRelease = input id: 'mavenRelease', message: 'Peform Maven Release', ok: 'Schedule Maven Release Build', 
      parameters: [[$class: 'StringParameterDefinition', defaultValue: "${releaseVersion}", description: 'Release Version', name: 'releaseVersion'], 
                [$class: 'StringParameterDefinition', defaultValue: "${developmentVersion}", description: 'Development version', name: 'developmentVersion'], 
                [$class: 'BooleanParameterDefinition', defaultValue: true, description: 'Dry run only?', name: 'dryRun'], 
                [$class: 'StringParameterDefinition', defaultValue: 'j3nk1ns', description: 'SCM Username', name: 'username'], 
                [$class: 'PasswordParameterDefinition', defaultValue: 'aon945121010', description: 'SCM Password', name: 'password'], 
                [$class: 'StringParameterDefinition', defaultValue: '[maven-release-plugin]', description: 'SCM Comment Prefix', name: 'scmCommentPrefix'], 
                [$class: 'StringParameterDefinition', defaultValue: "${tag}", description: 'SCM Tag', name: 'tag']
      ]


      sh "${mvnHome}/bin/mvn  -T 4 -B  -DdevelopmentVersion=${mavenRelease['developmentVersion']} -DreleaseVersion=${mavenRelease['releaseVersion']} -Dusername=${mavenRelease['username']} -Dpassword=${mavenRelease['password']} -Dtag=${mavenRelease['tag']} -Dresume=false -DdryRun=${mavenRelease['dryRun']} -DscmCommentPrefix=${mavenRelease['scmCommentPrefix']} release:prepare"

      // Mark the RPMs deploy 'stage'....
      stage 'Deploy RPMs'
    
      // Upload RPMs 
      sh "scp `find -name *.noarch.rpm` dev.esferalia.net:/var/www/rpms/aon-solutions/noarch"
   
      // Remove oldest RPMs. Keep 2 newest RPMs
      sh "ssh dev.esferalia.net 'repomanage --keep=2 --old /var/www/rpms/aon-solutions/noarch | xargs rm -rf'"
   
      // Create RPMs repository
      sh "ssh dev.esferalia.net 'createrepo /var/www/rpms/aon-solutions'"
   }
   else {
      // Mark the code build 'stage'....
      stage 'Build'
   
      // Run the maven build
      //sh "${mvnHome}/bin/mvn  -T 4 -B -Drpm.release=true -Dmaven.test.failure.ignore=true -Dgwt.working=true clean deploy"

      // Mark the RPMs deploy 'stage'....
      stage 'Deploy RPMs'
    
      // Upload RPMs 
      sh "scp `find -name *.noarch.rpm` dev.esferalia.net:/var/www/rpms/aon-inetserver/noarch"
   
      // Remove oldest RPMs. Keep 2 newest RPMs
      sh "ssh dev.esferalia.net 'repomanage --keep=2 --old /var/www/rpms/aon-inetserver/noarch | xargs rm -rf'"
   
      // Create RPMs repository
      sh "ssh dev.esferalia.net 'createrepo /var/www/rpms/aon-inetserver'"
   
      // Mark the Integration Tests 'stage'....
      stage 'Integration Tests'
   
      // 
      sh "sudo yum clean all"

      // 
      sh "sudo yum update -y --enablerepo=aon-testing"
   
      //    
      sh "sudo service tomcat8 restart"

      //       
      sh "sudo mysql < aon-htmlunit/src/test/resources/com/esferalia/aon/htmlunit/payroll/test-aonsolutions-org.sql"
   
      //
      sh "sudo /usr/share/aon-master/bin/up2datedbs.sh"
   
      //
      sh "echo 127.0.0.1 trainning-payroll-test.aonsolutions.org | sudo tee -a /etc/hosts"

      // Run the maven integration tests
      //sh "${mvnHome}/bin/mvn  -B -Dmaven.test.failure.ignore=true -Dintegration.test.user=admin -Dintegration.test.password=org -Dintegration.test.general.payroll.url=http://general-payroll-test.aonsolutions.org:8080/aon-aio/ -Dintegration.test.trainning.payroll.url=http://trainning-payroll-test.aonsolutions.org:8080/aon-aio/ -f aon-htmlunit/pom.xml integration-test"
  
      // Recording test results
      //step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])

      // Mark the AWS deploy 'stage'....
      stage 'AWS CodeDeploy'

      env.VERSION=pom.version

      // 
      sh '''

temp_dir=$(mktemp -d)
mkdir ${temp_dir}/files
mkdir ${temp_dir}/scripts

for rpm_file in $(find -name aon-aio8*.rpm -o -name aon-common*.rpm -o -name aon-infoweb*.rpm -o -name aon.aeat*.rpm -o -name aon-master*.rpm -o -name aon-dbutils*.rpm ); do

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

#aws deploy push --application-name AON-SNAPSHOT-APP --s3-location s3://aon-solutions/aon-snapshot-app-${VERSION}${BUILD_ID}.zip --source ${temp_dir}

#aws deploy create-deployment --application-name AON-SNAPSHOT-APP --s3-location bucket=aon-solutions,key=aon-snapshot-app-${VERSION}${BUILD_ID}.zip,bundleType=zip --deployment-group-name AON-NET-GROUP  --deployment-config-name  CodeDeployDefault.AllAtOnce  

'''
       sh "aws s3api list-objects --bucket aon-solutions --prefix aon-snapshot-app > aon-snapshot-apps.json"
       def aon_snapshot_apps_json = readFile 'aon-snapshot-apps.json'    
       def keys = getKeys(aon_snapshot_apps_json)
//       for (int i = 0; i < keys.size() - 3; i++){
//          def key = key[i]
//          sh "aws s3api delete-object --bucket aon-solutions --key ${key}"   
//       }
   
   }   

}

@NonCPS
def getKeys(def json) {
    def objects = new groovy.json.JsonSlurper().parseText(json)
    dek keys = new String[10]
//    dek keys = new String[objects.Contents.size()]
//    for (int i = 0; i < objects.Contents.size(); i++)
//       keys[i]=objects.Contents[i].Key
    return keys
}
