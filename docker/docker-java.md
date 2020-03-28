
# Docker Cheat Sheet for Spring Developers


## Images
- List all Docker Images

       docker images -a

- Remove a Docker Image
  
       docker rmi <image name>

- Delete All Docker Images
  
      docker rmi $(docker images -q)

- Delete All Untagged (dangling) Docker Images

      docker rmi $(docker images -q -f dangling=true)

- Delete All Images
  
      docker rmi $(docker images -q)

- Save a Running Docker Container as an Image

      docker commit <image name> <name for image>


## Containers
- List All Running Docker Containers
   
      docker ps

- List All Docker Containers
  
      docker ps -a

- Start a Docker Container
  
      docker start <container name>

- Stop a Docker Container

      docker stop <container name>

- SSH Into a Running Docker Container

      docker exec -it <container name> bash

- Kill All Running Containers

      docker kill $(docker ps -q)

- View the logs of a Running Docker Container
  
      docker logs <container name>
      docker -f logs <container name>

- Delete All Stopped Docker Containers

      docker rm $(docker ps -a -q)

## Volumes

- Remove Dangling Volumes

      docker volume rm -f $(docker volume ls -f dangling=true -q)



## Docker Compose

- Run from directory of your docker-compose.yml file.

      docker-compose build

- Use Docker Compose to Start a Group of Containers

    Use this command from directory of your docker-compose.yml file.

      docker-compose up -d

    This will tell Docker to fetch the latest version of the container from the repo, and not use the local cache.

- force-recreate
  
      docker-compose up -d --force-recreate

    This can be problematic if you’re doing CI builds with Jenkins and pushing Docker images to another host, or using for CI testing. I was deploying a Spring Boot Web Application from Jekins, and found the docker container was not getting refreshed with the latest Spring Boot artifact.

- stop docker containers, and rebuild
  
      docker-compose stop -t 1

      docker-compose rm -f

      docker-compose pull

      docker-compose build

      docker-compose up -d


- Follow the Logs of Running Docker Containers With Docker Compose

      docker-compose logs -f

- Follow the logs of one container running under Docker Compose

      docker-compose logs pump <name>



## Running Spring Boot in a Docker Container Example
Dockerfile

```docker
FROM centos

ENV JAVA_VERSION 8u31
ENV BUILD_VERSION b13

# Upgrading system
RUN yum -y upgrade
RUN yum -y install wget

# Downloading & Config Java 8
RUN wget --no-cookies --no-check-certificate --header "Cookie: oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/$JAVA_VERSION-$BUILD_VERSION/jdk-$JAVA_VERSION-linux-x64.rpm" -O /tmp/jdk-8-linux-x64.rpm
RUN yum -y install /tmp/jdk-8-linux-x64.rpm
RUN alternatives --install /usr/bin/java jar /usr/java/latest/bin/java 200000
RUN alternatives --install /usr/bin/javaws javaws /usr/java/latest/bin/javaws 200000
RUN alternatives --install /usr/bin/javac javac /usr/java/latest/bin/javac 200000

EXPOSE 8080

#install Spring Boot artifact
VOLUME /tmp
ADD /maven/sfg-thymeleaf-course-0.0.1-SNAPSHOT.jar sfg-thymeleaf-course.jar
RUN sh -c 'touch /sfg-thymeleaf-course.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/sfg-thymelea

```


## Building the Docker Image Using Maven

Here is a typical configuration for the Fabric8 Maven plugin for Docker.

Fabric8 Maven Docker Plugin Configuration
```xml
<plugin>
  <groupId>io.fabric8</groupId>
  <artifactId>docker-maven-plugin</artifactId>
  <version>0.15.3</version>
  <configuration>
    <dockerHost>http://127.0.0.1:2375</dockerHost>
    <verbose>true</verbose>
    <images>
      <image>
        <name>springframeworkguru/masteringthymeleaf</name>
        <build>
          <dockerFile>Dockerfile</dockerFile>
          <assembly>
            <descriptorRef>artifact</descriptorRef>
          </assembly>
        </build>
      </image>
    </images>
  </configuration>
</plugin>
```
pom.xml

```xml

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>guru.springframework</groupId>
    <artifactId>sfg-thymeleaf-course</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>
    <name>sfg-thymeleaf-course</name>
    <description>Thymeleaf Course</description>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.3.1.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <java.version>1.8</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.thymeleaf.extras</groupId>
            <artifactId>thymeleaf-extras-springsecurity4</artifactId>
            <version>2.1.2.RELEASE</version>
        </dependency>
        <!--testing deps-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>io.fabric8</groupId>
                <artifactId>docker-maven-plugin</artifactId>
                <version>0.15.3</version>
                <configuration>
                    <dockerHost>http://127.0.0.1:2375</dockerHost>
                    <verbose>true</verbose>
                    <images>
                        <image>
                            <name>springframeworkguru/masteringthymeleaf</name>
                            <build>
                                <dockerFile>Dockerfile</dockerFile>
                                <assembly>
                                    <descriptorRef>artifact</descriptorRef>
                                </assembly>
                            </build>
                        </image>
                    </images>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```


To build the Docker image with Spring Boot artifact run this command:

    mvn clean package docker:build


Running the Spring Boot Docker Image

    docker run -p 8080:8080 -d springframeworkguru/masteringthymeleaf

    docker logs springframeworkguru/masteringthymeleaf

    sudo docker exec -it springframeworkguru/masteringthymeleaf bash

    docker stop springframeworkguru/masteringthymeleaf
