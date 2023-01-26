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
- #### Install [Docker] (https://www.docker.com/)
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

- #### Install [Eclipse IDE] (https://www.eclipse.org/downloads/)
``` bash
sudo snap install --classic eclipse
```



