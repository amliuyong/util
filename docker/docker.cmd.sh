https://lug.ustc.edu.cn/wiki/mirrors/help/docker

{
  "registry-mirrors": ["https://docker.mirrors.ustc.edu.cn"]
}


docker -v
docker info
docker --help

docker pull --help

### images ###

# list images on local server
docker images

# search images
docker search tomcat
docker search mysql
docker search zookeeper
docker search redis
docker search centos

# download image
docker pull tomcat 

# remove a image
docker rmi <image_id>

# remove all images
docker rmi `docker images -q`
# remove dangling images
docker rmi $(docker images -q -f dangling=true)

### container ###

docker ps
docker ps -a 
docker ps -l 

docker ps -f status=existed

# create/run container 
docker run -i -t 
   -i  
   -t  # enter the command line 交互式，前台运行
   -d  # demon  守护式， 后台运行
   --name # --name=<name>
   -v  # directory mapping
   -p  # port mapping

#交互式容器
docker run -it  --name=mycentos centos:latest /bin/bash
docker run -it  --name=mycentos001 centos:latest /bin/bash

#守护式容器
docker run -id --name=mycentos002 centos:latest 

# 重新进入容器
docker exec -it mycentos002 /bin/bash

docker start mycentos
docker stop mycentos


### files copy ###

# copy into 
docker cp trgit.sh  mycentos:/usr/local
docker exec -it mycentos /bin/bash

# copy out
docker cp mycentos:/usr/local/trgit.sh test.sh

### directory mount ###

# /Users/yongliu/myhtml -> /usr/local/html
docker run -id --name=mycentos003 -v /Users/yongliu/myhtml:/usr/local/html centos:latest 

docker ps -a
docker exec -it mycentos003 /bin/bash

docker run -id --name=mycentos004 -v /Users/yongliu/myhtml:/usr/local/html --privileged=true centos:latest 
docker exec -it mycentos004 /bin/bash

### container IP ###
docker inspect mycentos

docker inspect mycentos | grep IPAddress

docker inspect --format="{{.NetworkSettings.IPAddress}}" mycentos
 

### rm container ###
docker rm id/name

docker rm mycentos001

docker rm `docker ps -a -q`

### rm dangling volume ###

docker volume rm $(docker volume ls -f dangling=true -q)


### MySQL ###

docker pull mysql

docker run -id --name mysql001  -p 33306:3306 -e MYSQL_ROOT_PASSWORD=123456 mysql

docker ps 

docker exec -it mysql001 /bin/bash

mysql -uroot -p123456  # in contianer 

mysql -h 10.35.14.34 -P 33306 -uroot -p123456  # in localhost

docker inspect --format="{{.NetworkSettings.IPAddress}}" mysql001
>> 172.17.0.6


### tomcat ###

docker pull tomcat

docker run -id --name tomcat001 -v /Users/yongliu/myhtml:/usr/local/tomcat/webapps  --privileged=true -p 9000:8080 tomcat:latest

# if want to connect to mysql, use ip 172.17.0.6 of mysql001.

docker exec -it tomcat001 /bin/bash
cd /usr/local/tomcat/webapps/abc

echo "Hello Docker!" > index.html

http://localhost:9000/abc/


docker inspect --format="{{.NetworkSettings.IPAddress}}" tomcat001
>> 172.17.0.7

### nginx ###

docker pull nginx

docker run -id  --name nginx001 -p 9091:80  nginx:latest

docker ps

http://localhost:9091/

docker exec -it nginx001 /bin/bash

/etc/nginx/nginx.conf

docker cp nginx001:/etc/nginx/nginx.conf  ./nginx.conf

vi ./nginx.conf   -> add proxy, point to tomcat001 IP(172.17.0.7) :8080

docker cp ./nginx.conf nginx001:/etc/nginx/nginx.conf 

docker restart nginx001


### redis ###

docker pull redis

docker run -id  --name redis001 -p 6379:6379  redis:latest

docker ps -l

docker exec -it redis001 /bin/bash

redis-cli -h 10.35.14.34 

docker stop redis001

docker start redis001



### contianer -> image ###

docker commit nginx001 mynginx_image

# export image -> file
docker save -o  mynginx_image.tar  mynginx_image

# import file -> image
docker rmi mynginx_image
docker load -i mynginx_image.tar




