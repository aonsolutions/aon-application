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

      def branch = pom.version.replace("-SNAPSHOT", ".X")

      def releaseVersion = pom.version.replace("-SNAPSHOT", "")
  
      def developmentVersion = pom.version.replace("-SNAPSHOT", "-SNAPSHOT")
  
      def mavenRelease = input id: 'mavenRelease', message: 'Peform Maven Release', ok: 'Schedule Maven Release Build', 
      parameters: [[$class: 'StringParameterDefinition', defaultValue: "${releaseVersion}", description: 'Release Version', name: 'releaseVersion'], 
                [$class: 'StringParameterDefinition', defaultValue: "${developmentVersion}", description: 'Development version', name: 'developmentVersion'], 
                [$class: 'BooleanParameterDefinition', defaultValue: true, description: 'Dry run only?', name: 'dryRun'], 
                [$class: 'StringParameterDefinition', defaultValue: 'j3nk1ns', description: 'SCM Username', name: 'username'], 
                [$class: 'PasswordParameterDefinition', defaultValue: 'aon945121010', description: 'SCM Password', name: 'password'], 
                [$class: 'StringParameterDefinition', defaultValue: '[maven-release-plugin]', description: 'SCM Comment Prefix', name: 'scmCommentPrefix'], 
                [$class: 'StringParameterDefinition', defaultValue: "${tag}", description: 'SCM Tag', name: 'tag'],
                [$class: 'StringParameterDefinition', defaultValue: "${branch}", description: 'SCM Branch', name: 'branch'],
      ]

      sh "${mvnHome}/bin/mvn versions:force-releases -Dincludes=net.aonsolutions.core"
      sh "${mvnHome}/bin/mvn versions:commit"
      sh "git commit -a -m 'Replaces any -SNAPSHOT versions ( non child ) with a release version'"
      sh "git push https://${mavenRelease['username']}:${mavenRelease['password']}@github.com/aonsolutions/aon-application.git"

      sh "echo yes | ${mvnHome}/bin/mvn  -DdevelopmentVersion=${mavenRelease['developmentVersion']} -DreleaseVersion=${mavenRelease['releaseVersion']} -Dusername=${mavenRelease['username']} -Dpassword=${mavenRelease['password']} -Dtag=${mavenRelease['tag']} -Dresume=false -DdryRun=${mavenRelease['dryRun']} -DscmCommentPrefix=${mavenRelease['scmCommentPrefix']} -Darguments='-Drpm.release=false -DskipTests=true -Dgwt.localWorkers=4' release:prepare"
      
      // Create release branch for future HotFixes
      // Checking out to release tag
      sh "git checkout ${mavenRelease['tag']}"
      // Create release branch
      sh "git checkout -b ${mavenRelease['branch']}"
      sh "mv Jenkinsfile.hotfix Jenkinsfile"
      sh "git commit -a -m 'For build hotfixes'"
      sh "git push https://${mavenRelease['username']}:${mavenRelease['password']}@github.com/aonsolutions/aon-application.git ${mavenRelease['branch']}"


      // Delete any backup POM & release.properties files
      sh "echo yes | ${mvnHome}/bin/mvn  release:clean"
      // Switched to branch 'master'
      sh "git checkout master"
      sh "${mvnHome}/bin/mvn versions:use-latest-snapshots -Dincludes=net.aonsolutions.core -DallowMajorUpdates=true"
      sh "${mvnHome}/bin/mvn versions:commit"
      sh "git commit -a -m 'Replaces any release versions ( non child ) with the latest -SNAPSHOT version'"
      sh "git push https://${mavenRelease['username']}:${mavenRelease['password']}@github.com/aonsolutions/aon-application.git"
   }
   else {

      // Mark the code build 'stage'....
      stage 'Build'
   
      // Run the maven build
      sh "echo yes | ${mvnHome}/bin/mvn  -Drpm.release=false -Dmaven.test.failure.ignore=true -Dgwt.working=true -DSNAPSHOT clean deploy"

      // Recording fingerprints of files to track usage
      fingerprint '**/target/*SNAPSHOT.jar'

      // Docker 
      stage 'Docker Build'

      // Run the docker build
      def rolling_version = new Date().format('yyyy.MM.dd-HH.mm.ss')
      sh "docker build --build-arg AON_VERSION=${pom.version} --build-arg POOL_VERSION=${pom.version} -t aonsolutions/aon-application:${rolling_version}-tomcat9-jre8 ."
      sh "docker build --build-arg AON_VERSION=${pom.version} --build-arg POOL_VERSION=${pom.version} -t aonsolutions/aon-micro-services:${rolling_version}-tomcat9-jre8 ./aon-micro-services"

      // Mark the Integration Tests 'stage'....
      stage 'Integration Tests'
      
      sh 'sudo mysql -e "DROP DATABASE IF EXISTS \\`test-aonsolutions-org\\`"'

      sh 'sudo mysql -e "GRANT ALL ON *.* TO \'dbuser\'@\'172.17.0.2\' IDENTIFIED BY \'serubd2000\';"'

      sh "sudo mysql < aon-htmlunit/src/test/resources/com/esferalia/aon/htmlunit/payroll/test-aonsolutions-org.sql"
   
      sh "sudo docker stop aon-application && sudo docker rm aon-application || echo 'No previous aon-application running'"

      sh "sudo docker run --name aon-application -d -p 8080:8080 -e DB_HOST=172.17.0.1 -e DB_USER=dbuser -e DB_PASSWD=serubd2000 aonsolutions/aon-application:${pom.version}-$BUILD_NUMBER-tomcat9-jre8"   

      sh "echo 127.0.0.1 payroll-test.aonsolutions.org | sudo tee -a /etc/hosts"

      sh "echo 127.0.0.1 trainning-payroll-test.aonsolutions.org | sudo tee -a /etc/hosts"
      
      sleep 30      
	
      // Run the maven integration tests
      sh "${mvnHome}/bin/mvn  -B -Dmaven.test.failure.ignore=true -Dintegration.test.user=admin -Dintegration.test.password=org  -Dintegration.test.payroll.url=http://payroll-test.aonsolutions.org:8080/ -Dintegration.test.general.payroll.url=http://general-payroll-test.aonsolutions.org:8080/ -Dintegration.test.trainning.payroll.url=http://trainning-payroll-test.aonsolutions.org:8080/ -f aon-htmlunit/pom.xml integration-test"
  
      // Recording test results
      step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])

      echo "currentBuild.result = ${currentBuild.result}"

      if ( currentBuild.result != 'UNSTABLE' ) {

      stage 'Docker Publish'

      sh "docker login -u rtrepiana -p aon945121010"

      sh "docker push aonsolutions/aon-application:${pom.version}-$BUILD_NUMBER-tomcat9-jre8"
      sh "docker push aonsolutions/aon-micro-services:${pom.version}-$BUILD_NUMBER-tomcat9-jre8"

      sh "aws ecs list-task-definitions --family-prefix SNAPSHOT > snapshot-task-definitions.json"

      def snapshot_task_definitions_json = readFile 'snapshot-task-definitions.json'

      def snapshot_task_definitions_arns = getTaskDefinitionArns(snapshot_task_definitions_json)

      def last_snapshot_task_definition_arn = snapshot_task_definitions_arns[snapshot_task_definitions_arns.size()-1]

      sh "aws ecs describe-task-definition --task-definition ${last_snapshot_task_definition_arn} > last-snapshot-task-definition.json"

      def last_snapshot_task_definition_json = readFile 'last-snapshot-task-definition.json'

      def snapshot_container_definitions_json = getContainerDefinitions(last_snapshot_task_definition_json, "aonsolutions/aon-application:${pom.version}-${BUILD_NUMBER}-tomcat9-jre8")

      sh "aws ecs register-task-definition --family SNAPSHOT --container-definitions '${snapshot_container_definitions_json}' > snapshot-task-definition.json"
	
      def snapshot_task_definition_json = readFile 'snapshot-task-definition.json'

      def snapshot_task_definition_arn = getTaskDefinitionArn(snapshot_task_definition_json)

      sh "aws ecs update-service --cluster SNAPSHOT --service SNAPSHOT --task-definition ${snapshot_task_definition_arn}"


      sh "aws ecs list-task-definitions --family-prefix SNAPSHOT-SERVICES > snapshot-services-task-definitions.json"

      def snapshot_services_task_definitions_json = readFile 'snapshot-services-task-definitions.json'

      def snapshot_services_task_definitions_arns = getTaskDefinitionArns(snapshot_services_task_definitions_json)

      def last_snapshot_services_task_definition_arn = snapshot_services_task_definitions_arns[snapshot_services_task_definitions_arns.size()-1]

      sh "aws ecs describe-task-definition --task-definition ${last_snapshot_services_task_definition_arn} > last-snapshot-services-task-definition.json"

      def last_snapshot_services_task_definition_json = readFile 'last-snapshot-services-task-definition.json'

      def snapshot_services_container_definitions_json = getContainerDefinitions(last_snapshot_services_task_definition_json, "aonsolutions/aon-micro-services:${pom.version}-${BUILD_NUMBER}-tomcat9-jre8")

      sh "aws ecs register-task-definition --family SNAPSHOT-SERVICES --container-definitions '${snapshot_services_container_definitions_json}' > snapshot-services-task-definition.json"
	
      def snapshot_services_task_definition_json = readFile 'snapshot-services-task-definition.json'

      def snapshot_services_task_definition_arn = getTaskDefinitionArn(snapshot_services_task_definition_json)

      sh "aws ecs update-service --cluster SNAPSHOT --service SNAPSHOT-SERVICES --task-definition ${snapshot_services_task_definition_arn}"

      }

      
   }   

}

@NonCPS
def toJson(def object) {
    groovy.json.JsonOutput.toJson(object)
}

@NonCPS
def getKeys(def json) {
    def objects = new groovy.json.JsonSlurper().parseText(json)
    def keys = new String[objects.Contents.size()]
    for (int i = 0; i < objects.Contents.size(); i++)
       keys[i]=objects.Contents[i].Key
    keys
}


@NonCPS
def getTaskDefinitionArn(def json) {
    new groovy.json.JsonSlurper().parseText(json).taskDefinition.taskDefinitionArn
}

@NonCPS
def getTaskDefinitionArns(def json) {
    new groovy.json.JsonSlurper().parseText(json).taskDefinitionArns
}

@NonCPS
def getContainerDefinitions(def json, def image) {
    def containerDefinitions = new groovy.json.JsonSlurper().parseText(json).taskDefinition.containerDefinitions
    containerDefinitions[0].image = image
    groovy.json.JsonOutput.toJson(containerDefinitions)
}
