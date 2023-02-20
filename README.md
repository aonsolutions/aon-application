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
``` bash
docker build --network=host --no-cache -t aonsolutions/aon-solutions:test -f ./aon-solutions/Dockerfile .
```
Try Docker image 
``` bash
docker run --rm --name aonsolutions -d -p 8080:8080 -e DB_USER=dbuser -e DB_PASSWD=serubd2000 -e DB_HOST=$(ip addr show docker0 | grep -oP 'inet \K[0-9\.]+')  aonsolutions/aon-solutions:test
```
Open a browser and navigate to http://localhost:8080

![localhost](https://user-images.githubusercontent.com/9419112/214926662-f0264237-2fbe-4a72-a573-029ee1d1e4b7.png)

- #### Setup Tomcat on Eclipse IDE .
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

Open Eclipse IDE closes all Projects and install Server ''Apache Tomact 9''

![tomcat9](https://user-images.githubusercontent.com/9419112/214929338-3fff6012-480e-413d-b4c4-7b0dc96286f2.png)

Use previous ''$HOME/aon-solutions'' as ''Tomcat installation directory''

![tomcatdir](https://user-images.githubusercontent.com/9419112/214929926-201c46ce-3157-495a-a459-75e248062154.png)

Setup Server Locations 

![conftomcat](https://user-images.githubusercontent.com/9419112/214930595-d6485304-a30e-4c5b-bec9-60e0278e9d72.png)

- #### Deploy aon-solutions on Tomcat v9.9 Server.

Opoen '''aon-solutions''' project. Suspend all validators. 

![suspen_validate](https://user-images.githubusercontent.com/9419112/216049741-1c1d8848-9f1d-47f4-992d-e3f0f92a4c22.png)

Add '''aon-solutions''' to Tomcatv9.9 Server al localhost

![add](https://user-images.githubusercontent.com/9419112/216050246-e7e758b4-5b55-4698-a697-1f55fae422b3.png)

Change Path of '''aon-solutions''' to '''/''' ( ROOT ) 

![root](https://user-images.githubusercontent.com/9419112/216052207-dc3ef457-5246-41aa-a188-e91868c0355b.png)

Start the server , wait until started open a browser a navigate to '''http://localhost:8080'''

![localhost](https://user-images.githubusercontent.com/9419112/216053284-998f9456-ceac-46c7-97d7-9cb74a104537.png)

- #### Congratulations... :-) 



