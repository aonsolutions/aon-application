## Install aon-application development sofware on [Ubuntu 22.04](https://releases.ubuntu.com/22.04/)

- #### Update lists of packages
``` bash
sudo apt-get update
```
- #### Install [Java JDK 17](https://openjdk.org/projects/jdk/17/)
``` bash
sudo apt-get install openjdk-17-jdk
```
- #### Install [Apache Maven](https://maven.apache.org/)
``` bash
sudo apt-get install maven
```
- #### Install [Docker](https://www.docker.com/)
``` bash
sudo apt-get install docker
```
- #### Executing the Docker Command Without Sudo (Optional)
If you want to avoid typing sudo whenever you run the docker command, add your username to the docker group :
``` bash
sudo usermod -aG docker ${USER}
```
To apply the new group membership, log out of the server and back in, or type the following:
``` bash
su - ${USER}
```
- #### Start and Setup MySQL8
If local folder for databases is not created:
``` bash
sudo mkdir /var/lib/mysql
```

Its posible that you have an older mysql installation, if you have it, you need to create another mysql folder and link that to the docker below in the parameters.

Run mysql:8  Docker 
``` bash
docker run -p 3306:3306 --name mysql -v /var/lib/mysql:/var/lib/mysql  -e MYSQL_ALLOW_EMPTY_PASSWORD=yes -e MYSQL_USER=dbuser -e MYSQL_PASSWORD=serubd2000 -d mysql:8 --sql-mode="0" --default-authentication-plugin=mysql_native_password
```
Grant all privileges to 'dbuser'
``` bash
docker exec -it mysql mysql -e "GRANT ALL ON *.* TO 'dbuser'@'%'"
```
Try MySQL8 
``` bash
docker exec -it mysql mysql -u dbuser --password=serubd2000 -e "SHOW DATABASES"
```
```bash
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
```


- #### Install [Eclipse IDE](https://www.eclipse.org/downloads/)
``` bash
sudo snap install --classic eclipse
```
- #### Import, Check out [aon-application](https://github.com/aonsolutions/aon-application) Projects from SCM 

![Screenshot from 2023-01-07 14-26-06](https://user-images.githubusercontent.com/9419112/214888048-19917d70-e684-4570-a3f8-8388a7471832.png)

- #### Install Maven SCM Handler for EGit (m2e-egit) 

![m2e Marketplace](https://user-images.githubusercontent.com/9419112/214890102-98f05530-1aa0-4f27-948d-ca1e3ca27c57.png) 

Accept the terms of the license, Trust all content, Finish and Restart Eclipse.

![m2e-egit](https://user-images.githubusercontent.com/9419112/214890147-03874c78-6f72-4ac2-b96b-6abaca7d58f1.png)

- #### Finally Import, Check out [aon-application](https://github.com/aonsolutions/aon-application.git) Projects from SCM 

![aon-application io](https://user-images.githubusercontent.com/9419112/214894816-c954b274-6ff3-4b63-a7b6-692966b16c3f.png)

Wait, wait , wait and finally  Show Solutions 

- #### Install Eclipse Enterprise Java and Web Developer Tools 3.28

![JWT](https://user-images.githubusercontent.com/9419112/214896736-40a174a1-89fc-4b17-b3ad-99a1e6bc81fe.png)

Better Wait Restart Eclipse IDE to Impoting Maven projects Finish 100%

Close Eclipse IDE 

- #### Build aon-applicaton 

Go to aon-application directory, usually at '$HOME/eclipse-workspace/aon-parent'
``` bash
cd $HOME/eclipse-workspace/aon.parent
```
Build, by now skip tests :-( 
``` bash
mvn -DskipTests=true -Dgwt.working=true clean install
```
And.... wait for a long long time ...  for example 35 min 
``` bash
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  34:51 min
[INFO] Finished at: 2023-01-26T18:22:12Z
[INFO] ------------------------------------------------------------------------
```
Build Docker image 


To try to resolve future errors, if doesnt exist, create the folder and file **/etc/aon-aio/connection** . This file contains the metada connection to the BD. The file must contain the following text : 

``` bash
driverClass=com.mysql.cj.jdbc.Driver
jdbcUrl=jdbc:mysql://172.17.0.2:3306
user=dbuser
password=serubd2000
timezone=Europe/Madrid
useSSL=false
```
Build the docker image

``` bash
docker build --network=host --no-cache -t aonsolutions/aon-solutions:test -f ./aon-solutions/Dockerfile .

```
Build Docker image unzip error

If you get an unzip error in the previous step, go to the Dockerfile file in aon-solutions .

![photo_2023-03-03_08-22-03](https://user-images.githubusercontent.com/126753797/222659271-541cf714-d74a-4255-a06d-fbf0148daee7.jpg)

Change the line 68 of the file for:

``` bash
RUN apt update
RUN apt install unzip
```
This will make it unzip correctly.


Try Docker image 
``` bash
docker run --rm --name aonsolutions -d -p 8080:8080 -e DB_USER=dbuser -e DB_PASSWD=serubd2000 -v /etc/aon-aio:/etc/aon-aio -e DB_HOST=$(ip addr show docker0 | grep -oP 'inet \K[0-9\.]+')  aonsolutions/aon-solutions:test
```
Open a browser and navigate to http://localhost:8080

![localhost](https://user-images.githubusercontent.com/9419112/214926662-f0264237-2fbe-4a72-a573-029ee1d1e4b7.png)

- #### Setup Tomcat on Eclipse IDE 

- ##### Get Tomcat Config
Export running container ''aonsolutions'' to a custom direcotry
``` bash 
docker export aonsolutions > /tmp/aonsolutions.tar
```
``` bash
mkdir $HOME/aon-solutions
```
``` bash 
cd $HOME/aon-solutions
```
``` bash
tar -xvf /tmp/aonsolutions.tar
```
``` bash 
rm -rf usr/local/tomcat/webapps/ROOT
```
Stop running container '''aonsolutions''' 
``` bash
docker stop aonsolutions
```
- ##### Setup Tomcat 10.1 Server

Open Eclipse. Wait for Eclipse to finish building and updating. Then **close all projects in the package explorer**.
Next, find the server configuration and create a new server. In the configuration choose the options Apache> Tomcat 10.1 Server.

![tomcat9](https://user-images.githubusercontent.com/9419112/214929338-3fff6012-480e-413d-b4c4-7b0dc96286f2.png)

Use previous ''$HOME/aon-solutions'' as ''Tomcat installation directory''

![tomcatdir](https://user-images.githubusercontent.com/9419112/214929926-201c46ce-3157-495a-a459-75e248062154.png)

Setup Server Locations 

![conftomcat](https://user-images.githubusercontent.com/9419112/214930595-d6485304-a30e-4c5b-bec9-60e0278e9d72.png)

Open '''aon-solutions''' project and '''aon-aio''' . Suspend all validators like you can see in the image below. 

![suspen_validate](https://user-images.githubusercontent.com/9419112/216049741-1c1d8848-9f1d-47f4-992d-e3f0f92a4c22.png)

Add '''aon-solutions''' and '''aon-aio'''  to Tomcat 10.1 Server al localhost

![add](https://user-images.githubusercontent.com/9419112/216050246-e7e758b4-5b55-4698-a697-1f55fae422b3.png)

Change Path of '''aon-solutions''' to '''/''' ( ROOT ) 

![root](https://user-images.githubusercontent.com/9419112/216052207-dc3ef457-5246-41aa-a188-e91868c0355b.png)

Finally, we need to change the tomcat server argument configuration. You can do that with double click over the server an then **Overview > Open Launch configuration > Arguments**

There will be some arguments already, you are argument neet to similar to this ->

``` bash
-Dcatalina.base="/home/ubuntu/aon-solutions/usr/local/tomcat" -Dcatalina.home="/home/ubuntu/aon-solutions/usr/local/tomcat" -Dwtp.deploy="/home/ubuntu/aon-solutions/usr/local/tomcat/webapps" --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.util.concurrent=ALL-UNNAMED --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED --add-opens=java.base/sun.net.www.protocol.jar=ALL-UNNAMED -Duser.country=ES  -Dcom.sun.security.enableAIAcaIssuers=true -Djava.security.auth.login.config=/home/ubuntu/aon-solutions/usr/local/tomcat/conf/login.config -Djavax.xml.validation.SchemaFactory:http://www.w3.org/2001
```
You need to change the '''ubuntu''' user for yours.

- #### Use Aon All in One

To work correctly we need a domain to connect to Aon All in One. To do that you need a new database structure with some domain information inside. The dockerfile doesnt build that information, so you need the SQL file.

To import it you have to download the mysql client:

``` bash
sudo apt install mysql-client
```

Then you have to conect to the mysql docker server. 
``` bash
mysql -u dbuser -pserubd2000 -h 172.17.0.2
```
When you has been connected succesfully then create the test database ->
``` mysql
create database test-aonsolutions-org;
```
And import the sql ->
```mysql
use test-aonsolutions-org

source /home/ubuntu/test.sql
```
Now you need to now the domain information inside the table name ->

``` mysql
select name from domain
```
And copy a domain name.

Finally go to the file ''' /etc/host ''' and add a dns rule (reemplace my domain name with yours) ->

``` bash
127.0.0.1 admin-test.aonsolutions.org
```

Now start the server.

Now if you go to '''admin-test.aonsolutions.org:8080''' you will see Aon and if you go to '''admin-test.aonsolutions.org:8080/aon-aio''' you will see the All in one project.

