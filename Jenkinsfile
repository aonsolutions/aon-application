   properties ([disableConcurrentBuilds()
		, [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5']]
		, [$class: 'GithubProjectProperty', displayName: '', projectUrlStr: 'https://github.com/aonsolutions/aon-application/']
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

    // Mark the code build 'stage'....
    stage 'Build'

    // Run the maven build
    sh "echo yes | ${mvnHome}/bin/mvn  -Dmaven.test.failure.ignore=true -Drpm.release=false -Dgwt.working=true -DSNAPSHOT clean install"

    // Recording fingerprints of files to track usage
    fingerprint '**/target/*SNAPSHOT.jar'

    echo "currentBuild.result = ${currentBuild.result}"

    if ( currentBuild.result != 'UNSTABLE' ) {

    	// Docker
    	stage 'Docker Build'

    	// Run the docker build
    	def rolling_version = new Date().format('yyyy.MM.dd-HH.mm.ss')
    	sh "docker build --no-cache --build-arg AON_VERSION=${pom.version} -t aonsolutions/aon-db-up2date:${rolling_version}-jre-alpine ./aon-db-up2date"
    	sh "docker build --no-cache --build-arg AON_VERSION=${pom.version} --build-arg POOL_VERSION=${pom.version} -t aonsolutions/aon-application:${rolling_version}-tomcat9-jre8 ."
	// For intermediate builds micro-services aren't used. 
    	//sh "docker build --no-cache --build-arg AON_VERSION=${pom.version} --build-arg POOL_VERSION=${pom.version} -t aonsolutions/aon-micro-services:${rolling_version}-tomcat9-jre8 -f ./aon-micro-services/Dockerfile ."

    	// Mark the Integration Tests 'stage'....
    	stage 'Integration Tests'

    	sh 'sudo mysql -e "DROP DATABASE IF EXISTS \\`test-aonsolutions-org\\`"'

    	sh 'sudo mysql -e "GRANT ALL ON *.* TO \'dbuser\'@\'172.17.0.2\' IDENTIFIED BY \'serubd2000\';"'

    	sh "sudo mysql < aon-htmlunit/src/test/resources/com/esferalia/aon/htmlunit/payroll/test-aonsolutions-org.sql"

    	sh "sudo docker stop aon-application && sudo docker rm aon-application || echo 'No previous aon-application running'"

    	sh "sudo docker run -e DB_HOST=172.17.0.1 -e DB_USER=dbuser -e DB_PASSWD=serubd2000 aonsolutions/aon-db-up2date:${rolling_version}-jre-alpine"

    	sh "sudo docker run --name aon-application -d -p 8080:8080 -e DB_HOST=172.17.0.1 -e DB_USER=dbuser -e DB_PASSWD=serubd2000 aonsolutions/aon-application:${rolling_version}-tomcat9-jre8"

    	sh "echo 127.0.0.1 payroll-test.aonsolutions.org | sudo tee -a /etc/hosts"

    	sh "echo 127.0.0.1 home-payroll-test.aonsolutions.org | sudo tee -a /etc/hosts"

    	sh "echo 127.0.0.1 trainning-payroll-test.aonsolutions.org | sudo tee -a /etc/hosts"

    	sleep 30

    	// Run the maven integration tests
    	sh "${mvnHome}/bin/mvn  -B -Dmaven.test.failure.ignore=true -Dintegration.test.user=admin -Dintegration.test.password=org -Dintegration.test.payroll.url=http://payroll-test.aonsolutions.org:8080/ -Dintegration.test.general.payroll.url=http://general-payroll-test.aonsolutions.org:8080/ -Dintegration.test.trainning.payroll.url=http://trainning-payroll-test.aonsolutions.org:8080/ -Dintegration.test.home.payroll.url=http://home-payroll-test.aonsolutions.org:8080/  -f aon-htmlunit/pom.xml integration-test"

    	// Recording test results
    	step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
    }

    echo "currentBuild.result = ${currentBuild.result}"

    if ( currentBuild.result != 'UNSTABLE' ) {
        
        stage 'Build Release'

    	rolling_version = new Date().format('yyyy.MM.dd-HH.mm.ss')

        sh "git tag ${rolling_version}"
	sh "git push https://j3nk1ns:aon945121010@github.com/aonsolutions/aon-application.git ${rolling_version}"

	// Run the maven build
        sh "echo yes | ${mvnHome}/bin/mvn  -Drpm.release=false -DskipTests=true -Dmaven.test.failure.ignore=true clean deploy"

    	sh "docker build --no-cache --build-arg AON_VERSION=${pom.version} -t aonsolutions/aon-db-up2date:${rolling_version}-jre-alpine ./aon-db-up2date"
    	sh "docker build --no-cache --build-arg AON_VERSION=${pom.version} --build-arg POOL_VERSION=${pom.version} -t aonsolutions/aon-application:${rolling_version}-tomcat9-jre8 ."
    	sh "docker build --no-cache --build-arg AON_VERSION=${pom.version} --build-arg POOL_VERSION=${pom.version} -t aonsolutions/aon-micro-services:${rolling_version}-tomcat9-jre8 -f ./aon-micro-services/Dockerfile ."

        stage 'Docker Publish'

        sh "docker login -u rtrepiana -p aon945121010"

        sh "docker push aonsolutions/aon-db-up2date:${rolling_version}-jre-alpine"
        sh "docker push aonsolutions/aon-application:${rolling_version}-tomcat9-jre8"
        sh "docker push aonsolutions/aon-micro-services:${rolling_version}-tomcat9-jre8"

        sh "aws ecs list-task-definitions --family-prefix SNAPSH0T-DB-UP2DATE > snapshot-db-up2date-task-definitions.json"
	def snapshot_db_up2date_task_definitions_json = readFile 'snapshot-db-up2date-task-definitions.json'
	def snapshot_db_up2date_task_definitions_arns = getTaskDefinitionArns(snapshot_db_up2date_task_definitions_json)
	def last_snapshot_db_up2date_task_definition_arn = snapshot_db_up2date_task_definitions_arns[snapshot_db_up2date_task_definitions_arns.size()-1]
	sh "aws ecs describe-task-definition --task-definition ${last_snapshot_db_up2date_task_definition_arn} > last-snapshot-db-up2date-task-definition.json"
	def last_snapshot_db_up2date_task_definition_json = readFile 'last-snapshot-db-up2date-task-definition.json'

	def snapshot_db_up2date_cpu = getCpu(last_snapshot_db_up2date_task_definition_json)
	def snapshot_db_up2date_memory = getMemory(last_snapshot_db_up2date_task_definition_json)
	def snapshot_db_up2date_network_mode = getNetworkMode(last_snapshot_db_up2date_task_definition_json)
	def snapshot_db_up2date_execution_role_arn = getExecutionRoleArn(last_snapshot_db_up2date_task_definition_json)
	def snapshot_db_up2date_compatibilities = getCompatibilities(last_snapshot_db_up2date_task_definition_json)
	def snapshot_db_up2date_container_definitions_json = getContainerDefinitions(last_snapshot_db_up2date_task_definition_json, "aonsolutions/aon-db-up2date:${rolling_version}-jre-alpine")
	sh "aws ecs register-task-definition --family SNAPSH0T-DB-UP2DATE --task-role-arn '${snapshot_db_up2date_execution_role_arn}' --execution-role-arn '${snapshot_db_up2date_execution_role_arn}' --network-mode '${snapshot_db_up2date_network_mode}' --cpu '${snapshot_db_up2date_cpu}' --memory '${snapshot_db_up2date_memory}' --container-definitions '${snapshot_db_up2date_container_definitions_json}' --requires-compatibilities ${snapshot_db_up2date_compatibilities} > snapshot-db-up2date-task-definition.json"

	def snapshot_db_up2date_task_definition_json = readFile 'snapshot-db-up2date-task-definition.json'
	def snapshot_db_up2date_task_definition_arn = getTaskDefinitionArn(snapshot_db_up2date_task_definition_json)
	sh "aws ecs run-task --count 1 --cluster SNAPSH0T --task-definition ${snapshot_db_up2date_task_definition_arn} > snapshot-db-up2date-task.json"
	def snapshot_db_up2date_task_json = readFile 'snapshot-db-up2date-task.json'
	def snapshot_db_up2date_task_arn = getTaskArn(snapshot_db_up2date_task_json)
	sh "aws ecs wait tasks-stopped --cluster SNAPSH0T --tasks ${snapshot_db_up2date_task_arn}"


        sh "aws ecs list-task-definitions --family-prefix SNAPSHOT > snapshot-task-definitions.json"
	def snapshot_task_definitions_json = readFile 'snapshot-task-definitions.json'
	def snapshot_task_definitions_arns = getTaskDefinitionArns(snapshot_task_definitions_json)
	def last_snapshot_task_definition_arn = snapshot_task_definitions_arns[snapshot_task_definitions_arns.size()-1]
	sh "aws ecs describe-task-definition --task-definition ${last_snapshot_task_definition_arn} > last-snapshot-task-definition.json"
	def last_snapshot_task_definition_json = readFile 'last-snapshot-task-definition.json'

	def snapshot_cpu = getCpu(last_snapshot_task_definition_json)
	def snapshot_memory = getMemory(last_snapshot_task_definition_json)
	def snapshot_network_mode = getNetworkMode(last_snapshot_task_definition_json)
	def snapshot_execution_role_arn = getExecutionRoleArn(last_snapshot_task_definition_json)
	def snapshot_compatibilities = getCompatibilities(last_snapshot_task_definition_json)
	def snapshot_container_definitions_json = getContainerDefinitions(last_snapshot_task_definition_json, "aonsolutions/aon-application:${rolling_version}-tomcat9-jre8")
	sh "aws ecs register-task-definition --family SNAPSH0T --task-role-arn '${snapshot_execution_role_arn}' --execution-role-arn '${snapshot_execution_role_arn}' --network-mode '${snapshot_network_mode}' --cpu '${snapshot_cpu}' --memory '${snapshot_memory}'  --requires-compatibilities ${snapshot_compatibilities} --container-definitions '${snapshot_container_definitions_json}' > snapshot-task-definition.json"

	def snapshot_task_definition_json = readFile 'snapshot-task-definition.json'
	def snapshot_task_definition_arn = getTaskDefinitionArn(snapshot_task_definition_json)
	sh "aws ecs update-service --cluster SNAPSHOT --service SNAPSHOT --task-definition ${snapshot_task_definition_arn}"


	sh "aws ecs list-task-definitions --family-prefix SNAPSHOT-SERVICES > snapshot-services-task-definitions.json"
	def snapshot_services_task_definitions_json = readFile 'snapshot-services-task-definitions.json'
	def snapshot_services_task_definitions_arns = getTaskDefinitionArns(snapshot_services_task_definitions_json)
	def last_snapshot_services_task_definition_arn = snapshot_services_task_definitions_arns[snapshot_services_task_definitions_arns.size()-1]
	sh "aws ecs describe-task-definition --task-definition ${last_snapshot_services_task_definition_arn} > last-snapshot-services-task-definition.json"
	def last_snapshot_services_task_definition_json = readFile 'last-snapshot-services-task-definition.json'

	def snapshot_services_cpu = getCpu(last_snapshot_services_task_definition_json)
	def snapshot_services_memory = getMemory(last_snapshot_services_task_definition_json)
	def snapshot_services_network_mode = getNetworkMode(last_services_up2date_task_definition_json)
	def snapshot_services_execution_role_arn = getExecutionRoleArn(last_snapshot_services_task_definition_json)
	def snapshot_services_compatibilities = getCompatibilities(last_snapshot_services_task_definition_json)
	def snapshot_services_container_definitions_json = getContainerDefinitions(last_snapshot_services_task_definition_json, "aonsolutions/aon-micro-services:${rolling_version}-tomcat9-jre8")
	sh "aws ecs register-task-definition --family SNAPSH0T-SERVICES --task-role-arn '${snapshot_services_execution_role_arn}' --execution-role-arn '${snapshot_services_execution_role_arn}' --network-mode '${snapshot_services_network_mode}' --cpu '${snapshot_services_cpu}' --memory '${snapshot_services_memory}'  --requires-compatibilities ${snapshot_services_compatibilities} --container-definitions '${snapshot_services_container_definitions_json}' > snapshot-services-task-definition.json"


	def snapshot_services_task_definition_json = readFile 'snapshot-services-task-definition.json'
	def snapshot_services_task_definition_arn = getTaskDefinitionArn(snapshot_services_task_definition_json)
	sh "aws ecs update-service --cluster SNAPSHOT --service SNAPSHOT-SERVICES --task-definition ${snapshot_services_task_definition_arn}"


        sh "aws ecs list-task-definitions --family-prefix RELEASE-DB-UP2DATE > release-db-up2date-task-definitions.json"
        def release_db_up2date_task_definitions_json = readFile 'release-db-up2date-task-definitions.json'
        def release_db_up2date_task_definitions_arns = getTaskDefinitionArns(release_db_up2date_task_definitions_json)
        def last_release_db_up2date_task_definition_arn = release_db_up2date_task_definitions_arns[release_db_up2date_task_definitions_arns.size()-1]
        sh "aws ecs describe-task-definition --task-definition ${last_release_db_up2date_task_definition_arn} > last-release-db-up2date-task-definition.json"
        def last_release_db_up2date_task_definition_json = readFile 'last-release-db-up2date-task-definition.json'
        def release_db_up2date_container_definitions_json = getContainerDefinitions(last_release_db_up2date_task_definition_json, "aonsolutions/aon-db-up2date:${rolling_version}-jre-alpine")
        sh "aws ecs register-task-definition --family RELEASE-DB-UP2DATE --container-definitions '${release_db_up2date_container_definitions_json}' > release-db-up2date-task-definition.json"
	
	sh "aws ecs list-task-definitions --family-prefix RELEASE > release-task-definitions.json"
	def release_task_definitions_json = readFile 'release-task-definitions.json'
	def release_task_definitions_arns = getTaskDefinitionArns(release_task_definitions_json)
	def last_release_task_definition_arn = release_task_definitions_arns[release_task_definitions_arns.size()-1]
	sh "aws ecs describe-task-definition --task-definition ${last_release_task_definition_arn} > last-release-task-definition.json"
	def last_release_task_definition_json = readFile 'last-release-task-definition.json'
	def release_container_definitions_json = getContainerDefinitions(last_release_task_definition_json, "aonsolutions/aon-application:${rolling_version}-tomcat9-jre8")
	sh "aws ecs register-task-definition --family RELEASE --container-definitions '${release_container_definitions_json}' > release-task-definition.json"

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
def getTaskArn(def json) {
    new groovy.json.JsonSlurper().parseText(json).tasks[0].taskArn
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

@NonCPS
def getCpu(def json) {
    def cpu = new groovy.json.JsonSlurper().parseText(json).taskDefinition.cpu
    cpu
}

@NonCPS
def getMemory(def json) {
    def memory = new groovy.json.JsonSlurper().parseText(json).taskDefinition.memory
    memory
}

@NonCPS
def getCompatibilities(def json) {
    def compatibilities = new groovy.json.JsonSlurper().parseText(json).taskDefinition.compatibilities
    compatibilities.join(" ")
}

@NonCPS
def getNetworkMode(def json) {
    def networkMode = new groovy.json.JsonSlurper().parseText(json).taskDefinition.networkMode
    networkMode
}

@NonCPS
def getExecutionRoleArn(def json) {
    def executionRoleArn = new groovy.json.JsonSlurper().parseText(json).taskDefinition.executionRoleArn
    executionRoleArn
}

