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
   def strategy = 'resolve';
   if ( commitsParams.size() > 1 ) {

      def cherryParams = [choice(
			name: 'strategy',
			description: 'MERGE STRATEGIES', 
			choices: 'resolve\nrecursive\noctopus\nours\nsubtree'
			)]	

      def commitsMap = input message: "Peform HotFix ${hotfix}", parameters: ( commitsParams + cherryParams )
   
      for ( commitParam in commitsParams ) {
         if ( commitsMap[commitParam.name] ) {
            commits = commits + ' ' + commitParam.name
         } 
      }
      strategy = commitsMap['strategy'] 
   }   
   
   if ( commits ) {
      // Mark the perform hotfix 'stage'....
      stage "Perform HotFix ${hotfix} "
      
      // Apply the changes introduced by introduced commits
      sh "git cherry-pick --strategy=${strategy} ${commits}"

      // Prepare hotfix   
      sh "find -name 'pom.xml'  | while read pom; do sed -i  -e 's/${pom.version}/${hotfix}/' \$pom; done"

      // Reread pom
      pom = readMavenPom file: 'pom.xml'
   }   
   
   // Mark the build 'stage'....
   stage "Build HotFix ${pom.version}"
   
   sh "echo yes | ${mvnHome}/bin/mvn  -Drpm.release=true -DskipTests clean deploy"
   
   // Mark the RPMs deploy 'stage'....
   stage 'Deploy RPMs'
   
   // Upload RPMs 
   sh "scp `find -name *${pom.version}*.noarch.rpm` dev.esferalia.net:/var/www/rpms/aon-solutions/noarch"
   
   // Remove oldest RPMs. Keep 2 newest RPMs
   sh "ssh dev.esferalia.net 'repomanage --keep=2 --old /var/www/rpms/aon-solutions/noarch | xargs rm -rf'"
   
   // Create RPMs repository
   sh "ssh dev.esferalia.net 'createrepo /var/www/rpms/aon-solutions'"

   if ( commits ) {

      // Mark the RPMs deploy 'stage'....
      stage "Publish HotFix ${pom.version}"

      //sh "${mvnHome}/bin/mvn  -B clean"

      sh "git commit -a -m 'Hotfix ${pom.version}'"
   
      sh "git push --repo=https://j3nk1ns:aon945121010@github.com/aonsolutions/aon-application.git"
       
   }
          
   // Mark the AWS deploy 'stage'....
   stage 'AWS CodeDeploy'

   env.VERSION=pom.version

   // RPMs 2 AWS
   sh "/bin/sh rpms2aws.sh AON-RELEASE-APP AON-RELEASE-GROUP"
      
   sh "aws s3api list-objects --bucket aon-solutions --prefix aon-release-app > aon-release-apps.json"
   def aon_release_apps_json = readFile 'aon-release-apps.json'    
   def keys = getKeys(aon_release_apps_json)
   for (int i = 0; i < keys.size() - 20; i++){
      def key = keys[i]
      sh "aws s3api delete-object --bucket aon-solutions --key ${key}"   
   }
    
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
def getKeys(def json) {
    def objects = new groovy.json.JsonSlurper().parseText(json)
    def keys = new String[objects.Contents.size()]
    for (int i = 0; i < objects.Contents.size(); i++)
       keys[i]=objects.Contents[i].Key
    keys
}

