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
sudo apt-get install maven
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
- #### Install/Run MySQL8

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

Wait Restart Eclipse IDE to Impoting Maven projects Finish 100%


