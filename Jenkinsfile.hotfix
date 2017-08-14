properties ([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5']]
	, [$class: 'GithubProjectProperty', displayName: '', projectUrlStr: 'https://github.com/aonsolutions/aon-application/']])

node {
   def mvnHome = tool 'M3'

   // Mark the code checkout 'stage'....
   stage 'Checkout'

   // Get some code from a GitHub repository
   git url: 'https://github.com/aonsolutions/aon-application', branch: env.BRANCH_NAME, credentialsId: '0057f1a5-ba06-4421-8785-7288a1eecfc4'
       
   // We want to pick up the version from the pom
   def pom = readMavenPom file: 'pom.xml'
   
   def hotfix = hotfix(pom.version)   
   
   sh "git cherry -v origin/${env.BRANCH_NAME} origin/master > cherryOut"
   
   def cherryOut = readFile 'cherryOut'
   
   def commitsParams = parameters(cherryOut);

   def commits = '';
   if ( commitsParams.size() > 2 ) {

      def commitsMap = input message: "Peform HotFix ${hotfix}", parameters: commitsParams
   
      for ( commitParam in commitsParams ) {
         if ( commitsMap[commitParam.name] ) {
            commits = commits + ' ' + commitParam.name
         } 
      }
   }   
   
   if ( commits ) {
      // Mark the perform hotfix 'stage'....
      stage "Perform HotFix ${hotfix}"

      // Apply the changes introduced by introduced commits
      sh "git cherry-pick ${commits}"

      // Prepare hotfix   
      //sh "find -name 'pom.xml'  | while read pom; do sed -i  -e 's/${pom.version}/${hotfix}/' \$pom; done"
      sh "${mvnHome}/bin/mvn versions:set -DnewVersion=${hotfix}"
      sh "${mvnHome}/bin/mvn versions:use-latest-releases -Dincludes=net.aonsolutions.core"
      sh "${mvnHome}/bin/mvn versions:commit"

      // Reread pom
      pom = readMavenPom file: 'pom.xml'
   }   
   
   // Mark the build 'stage'....
   stage "Build HotFix ${pom.version}"
   
   sh "echo yes | ${mvnHome}/bin/mvn  -Drpm.release=false -DskipTests clean deploy"
   
   if ( commits ) {

      //stage "Publish HotFix ${pom.version}"

      //sh "${mvnHome}/bin/mvn  -B clean"

      sh "git commit -a -m 'Hotfix ${pom.version}'"
   
      sh "git push --repo=https://j3nk1ns:aon945121010@github.com/aonsolutions/aon-application.git"
       
   }
          
   // Docker
   stage 'Docker Build'

   def aio_pom = readMavenPom file: 'aon-web-aio/pom.xml'
   def pool_version = getDependencyVersion(aio_pom, "pool")
   sh "echo ${pool_version}"

   // Run the docker build
   sh "docker build --build-arg AON_VERSION=${pom.version} --build-arg POOL_VERSION=${pool_version} --build-arg AON_MAVEN_REPOSITORY_URL=http://dev.esferalia.net/maven2_repositories/inhouse/com/code/aon -t aonsolutions/aon-application:${pom.version}-tomcat9-jre8 ."

   stage 'Docker Publish'

   sh "docker login -u rtrepiana -p aon945121010"

   sh "docker push aonsolutions/aon-application:${pom.version}-tomcat9-jre8"

   sh "aws ecs list-task-definitions --family-prefix RELEASE > release-task-definitions.json"

   def release_task_definitions_json = readFile 'release-task-definitions.json'

   def release_task_definitions_arns = getTaskDefinitionArns(release_task_definitions_json)

   def last_release_task_definition_arn = release_task_definitions_arns[release_task_definitions_arns.size()-1]

   sh "aws ecs describe-task-definition --task-definition ${last_release_task_definition_arn} > last-release-task-definition.json"

   def last_release_task_definition_json = readFile 'last-release-task-definition.json'

   def release_container_definitions_json = getContainerDefinitions(last_release_task_definition_json, "aonsolutions/aon-application:${pom.version}-tomcat9-jre8")

   sh "aws ecs register-task-definition --family RELEASE --container-definitions '${release_container_definitions_json}' > release-task-definition.json"

   def release_task_definition_json = readFile 'release-task-definition.json'

   def release_task_definition_arn = getTaskDefinitionArn(release_task_definition_json)

   sh "aws ecs update-service --cluster RELEASE --service RELEASE --task-definition ${release_task_definition_arn}"
    
}

@NonCPS
def hotfix(text) {
   def matcher = text =~ '([0-9]+).([0-9]+)(.([0-9]+))?'
   def major = matcher[0][1]
   def minor = matcher[0][2]
   def hotfix = matcher[0][4] ? (matcher[0][4] as int) + 1 : 1;
   return "${major}.${minor}.${hotfix}";
}

@NonCPS
def parameters(text) {
   def matcher = text =~ '(?m)^\\+\\s+([0-9a-fA-F]+)\\s+(.*)$'
   def parameters =  []
   def i = 0;
   for ( match in matcher ) {
      parameter = [
      $class: 'BooleanParameterDefinition',
      name:  match[1],
      defaultValue: false,
      description: match[2]    
      ]
      parameters[i++] = parameter
   }
   return parameters
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
def getDependencyVersion(def pom, def artifactId) {
    for (int i = 0; i < pom.dependencies.size(); i++) {
        if ( pom.dependencies[i].artifactId == artifactId ) {
	    return "${pom.dependencies[i].version}"
        }
    }
}

