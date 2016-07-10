node {
   def mvnHome = tool 'M3'

   // Mark the code checkout 'stage'....
   stage 'Checkout'

   // Get some code from a GitHub repository
   git url: 'https://github.com/aonsolutions/aon-application', branch: '8.60.X', credentialsId: '0057f1a5-ba06-4421-8785-7288a1eecfc4'
       
   // We want to pick up the version from the pom
   def pom = readMavenPom file: 'pom.xml'
   
   def hotfix = hotfix(pom.version)   
   
   sh "git cherry -v origin/8.60.X origin/master > cherryOut"
   
   def cherryOut = readFile 'cherryOut'
   
   def commitsMap = input message: "Peform HotFix ${hotfix}", parameters: parameters(cherryOut)

   def commits = '';
   for ( commitEntry in commitsMap ) {
      if ( commitEntry.value ) 
          commits = commits + ' ' + commitEntry.key
   }
   
   
   if ( commits ) {
      // Mark the perform hotfix 'stage'....
      stage "Perform HotFix ${hotfix}"

      // Apply the changes introduced by introduced commits
      sh "git cherry-pick ${commits}"

      // Prepare hotfix   
      sh "find -name 'pom.xml'  | while read pom; do sed -i  -e 's/<version>${pom.version}/<version>${hotfix}/' \$pom; done"

      // Reread pom
      pom = readMavenPom file: 'pom.xml'
   }   
   
   // Mark the build 'stage'....
   stage "Build HotFix ${pom.version}"
   
   sh "${mvnHome}/bin/mvn  -B -Drpm.release=true  clean deploy"
   
   // Mark the RPMs deploy 'stage'....
   stage 'Deploy RPMs'
   
   // Upload RPMs 
   sh "scp `find -name *${pom.version}*.noarch.rpm` dev.esferalia.net:/var/www/rpms/aon-solutions/noarch"
   
   // Remove oldest RPMs. Keep 2 newest RPMs
   sh "ssh dev.esferalia.net 'repomanage --keep=2 --old /var/www/rpms/aon-solutions/noarch | xargs rm -rf'"
   
   // Create RPMs repository
   sh "ssh dev.esferalia.net 'createrepo /var/www/rpms/aon-solutions'"

   if ( commits ) {

      // Mark the commit 'stage'....
      stage "Commit HotFix ${pom.version}"

      sh "${mvnHome}/bin/mvn  -B clean"

      sh "git commit -a -m 'Hotfix ${pom.version}'"
   
      sh "git push --repo=https://j3nk1ns:aon945121010@github.com/aonsolutions/aon-application.git"
       
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

