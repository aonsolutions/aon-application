node {
   def mvnHome = tool 'M3'

   // Mark the code checkout 'stage'....
   stage 'Checkout'

   // Get some code from a GitHub repository
   git url: 'https://github.com/aonsolutions/aon-application', branch: '8.58.X', credentialsId: '0057f1a5-ba06-4421-8785-7288a1eecfc4'
       
   // we want to pick up the version from the pom
   def pom = readMavenPom file: 'pom.xml'
   
   // def hotfix = hotfix(pom.version)   
   // sh "git cherry -v origin/8.58.X origin/master > cherry-commits"   
   //def cherryCommits =readFile 'cherry-commits'
   
   echo "${cherryCommits}"

   def commits = input message: "Peform HotFix ${hotfix}", 
   parameters: [[$class: 'TextParameterDefinition', defaultValue: '', description: 'Commits to cherry-pick', name: 'commits']
   ]
   
   if ( commits ) {
   stage "Perform HotFix ${hotfix}"
   
   sh "git cherry-pick ${commits}"
   
   sh "find -name 'pom.xml'  | while read pom; do sed -i  -e 's/${pom.version}/${hotfix}/' \$pom; done"

   pom = readMavenPom file: 'pom.xml'
   }   

   stage "Build HotFix ${pom.version}"
   
   sh "${mvnHome}/bin/mvn  -B -Drpm.release=true  clean deploy"
   
   if ( commits ) {

   sh "${mvnHome}/bin/mvn  -B clean"

   sh "git commit -a -m 'Hotfix ${pom.version}'"
   
   sh "git push --repo=https://j3nk1ns:aon945121010@github.com/aonsolutions/aon-application.git"
       
   }
   
       
   // Mark the RPMs deploy 'stage'....
   stage 'Deploy RPMs'
   
   def pom = readMavenPom file: 'pom.xml'
   
   // Upload RPMs 
   sh "scp `find -name *${pom.version}*.noarch.rpm` dev.esferalia.net:/var/www/rpms/aon-solutions/noarch"
   
   // Remove oldest RPMs. Keep 2 newest RPMs
   sh "ssh dev.esferalia.net 'repomanage --keep=2 --old /var/www/rpms/aon-solutions/noarch | xargs rm -rf'"
   
   // Create RPMs repository
   sh "ssh dev.esferalia.net 'createrepo /var/www/rpms/aon-solutions'"

    
}

@NonCPS
def hotfix(text) {
   def matcher = (text =~ /([0-9]+).([0-9]+)(.([0-9]+))?/);
   matcher.matches();
   return "${matcher[0][1]}.${matcher[0][2]}.${matcher[0][4]?++(matcher[0][4]):1}";
}
