   properties ([disableConcurrentBuilds()
		, [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5']]
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

   echo "MAVEN_RELEASE = ${MAVEN_RELEASE}"
   
   if ( MAVEN_RELEASE == 'true' ) {
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


      sh "echo yes | ${mvnHome}/bin/mvn  -DdevelopmentVersion=${mavenRelease['developmentVersion']} -DreleaseVersion=${mavenRelease['releaseVersion']} -Dusername=${mavenRelease['username']} -Dpassword=${mavenRelease['password']} -Dtag=${mavenRelease['tag']} -Dresume=false -DdryRun=${mavenRelease['dryRun']} -DscmCommentPrefix=${mavenRelease['scmCommentPrefix']} -Darguments='-Drpm.release=true -DskipTests=true' release:prepare"
      
      // Mark the RPMs deploy 'stage'....
      stage 'Deploy RPMs'
    
      // Upload RPMs 
      sh "scp `find -name *.noarch.rpm` dev.esferalia.net:/var/www/rpms/aon-solutions/noarch"
   
      // Remove oldest RPMs. Keep 2 newest RPMs
      sh "ssh dev.esferalia.net 'repomanage --keep=2 --old /var/www/rpms/aon-solutions/noarch | xargs rm -rf'"
   
      // Create RPMs repository
      sh "ssh dev.esferalia.net 'createrepo /var/www/rpms/aon-solutions'"

      def branch = tag.replace(".x", ".X")
      //sh "echo yes | ${mvnHome}/bin/mvn release:branch -DbranchName=${branch}  -DreleaseVersion=${mavenRelease['releaseVersion']} -DupdateBranchVersions=true -DupdateWorkingCopyVersions=false "
   }
   else {
      // Mark the code build 'stage'....
      stage 'Build'
   
      // Run the maven build
      sh "echo yes | ${mvnHome}/bin/mvn  -T 4 -Drpm.release=true -Dmaven.test.failure.ignore=true -Dgwt.working=true clean deploy"

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

      sh 'sudo mysql -e "DROP DATABASE IF EXISTS \\`test-aonsolutions-org\\`"'

      //       
      sh "sudo mysql < aon-htmlunit/src/test/resources/com/esferalia/aon/htmlunit/payroll/test-aonsolutions-org.sql"
   
      //
      sh "sudo /usr/share/aon-master/bin/up2datedbs.sh"
   
      //
      sh "echo 127.0.0.1 payroll-test.aonsolutions.org | sudo tee -a /etc/hosts"

      sh "echo 127.0.0.1 trainning-payroll-test.aonsolutions.org | sudo tee -a /etc/hosts"

      // Run the maven integration tests
      sh "${mvnHome}/bin/mvn  -B -Dmaven.test.failure.ignore=true -Dintegration.test.user=admin -Dintegration.test.password=org  -Dintegration.test.payroll.url=http://payroll-test.aonsolutions.org:8080/aon-aio/ -Dintegration.test.general.payroll.url=http://general-payroll-test.aonsolutions.org:8080/aon-aio/ -Dintegration.test.trainning.payroll.url=http://trainning-payroll-test.aonsolutions.org:8080/aon-aio/ -f aon-htmlunit/pom.xml integration-test"
  
      // Recording test results
      step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])

      // Mark the AWS deploy 'stage'....
      stage 'AWS CodeDeploy'

      env.VERSION=pom.version

      // RPMs 2 AWS
      //def rpms2aws = readFile 'rpms2aws.sh'
      sh "/bin/sh rpms2aws.sh"
      
      sh "aws s3api list-objects --bucket aon-solutions --prefix aon-snapshot-app > aon-snapshot-apps.json"
      def aon_snapshot_apps_json = readFile 'aon-snapshot-apps.json'    
      def keys = getKeys(aon_snapshot_apps_json)
      for (int i = 0; i < keys.size() - 20; i++){
         def key = keys[i]
         sh "aws s3api delete-object --bucket aon-solutions --key ${key}"   
      }
   
   }   

}

@NonCPS
def getKeys(def json) {
    def objects = new groovy.json.JsonSlurper().parseText(json)
    def keys = new String[objects.Contents.size()]
    for (int i = 0; i < objects.Contents.size(); i++)
       keys[i]=objects.Contents[i].Key
    keys
}
