   properties ([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5']]])

node {


   // Mark the code checkout 'stage'....
   stage 'Checkout'

   // Get some code from a GitHub repository
   git url: 'https://github.com/aonsolutions/aon-application', credentialsId: '0057f1a5-ba06-4421-8785-7288a1eecfc4'

   // Get the maven tool.
   // ** NOTE: This 'M3' maven tool must be configured
   // **       in the global configuration.           
   def mvnHome = tool 'M3'

   // Mark the code build 'stage'....
   stage 'Build'
   
   // Run the maven build
   sh "${mvnHome}/bin/mvn  -T 4 -B -Drpm.release=true -Dmaven.test.failure.ignore=true -Dgwt.working=true clean deploy"
   
   // Recording test results
   //step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])

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
   sh "${mvnHome}/bin/mvn  -B -Dmaven.test.failure.ignore=true -Dintegration.test.user=admin -Dintegration.test.password=org -Dintegration.test.general.payroll.url=http://general-payroll-test.aonsolutions.org:8080/aon-aio/ -Dintegration.test.trainning.payroll.url=http://trainning-payroll-test.aonsolutions.org:8080/aon-aio/ -f aon-htmlunit/pom.xml integration-test"
  
   // Recording test results
   step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])

}
