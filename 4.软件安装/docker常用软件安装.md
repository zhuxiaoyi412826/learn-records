### **jdk**

```
dockere pull openjdk:11
```

```
docker run -d -t --name java-11 openjdk:11 
```

###  MySQL

```bash
可以从docker hup中查找自己想要安装的版本
docker pull mysql:5.7  拉取镜像 
创建容器 
# 在/root目录下创建mysql目录用于存储mysql数据信息
mkdir /root/mysql    cd /root/mysql

docker run -id \
-p 3307:3306 \
--name=ydl_mysql \
-v /root/mysql/conf:/etc/mysql/conf.d \
-v /root/mysql/logs:/logs \
-v /root/mysql/data:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=123456 \
mysql:5.7

进入容器
docker exec -it ydl_mysql /bin/bash
进入MySQL  
mysql -uroot -p 
123456
远程连接MySQL

exit退出 
如果远程连接有问题 
https://www.cnblogs.com/zhangxiaoxia/p/13043508.html
修改MySQL的字符编码
修改my.cnf 文件
cd /mydata/mysql/conf
vim my.conf 

[client]
default-character-set=utf8mb4
[mysql]
default-character-set=utf8mb4
[mysqld]
init_connect='SET collation_connection = utf8mb4_bin'
init_connect='SET NAMES utf8mb4'
character_set_server = utf8mb4
collation_server = utf8mb4_bin
skip-character-set-client-handshake
skip-name-resolve

```

- **-p 3307:3306**：将容器的 3306 端口映射到宿主机的 3307 端口。
- **-v $PWD/conf:/etc/mysql/conf.d**：将主机当前目录下的 conf/my.cnf 挂载到容器的 /etc/mysql/my.cnf。配置目录
- **-v $PWD/logs:/logs**：将主机当前目录下的 logs 目录挂载到容器的 /logs。日志目录
- **-v $PWD/data:/var/lib/mysql** ：将主机当前目录下的data目录挂载到容器的 /var/lib/mysql 。数据目录
- **-e MYSQL_ROOT_PASSWORD=123456：**初始化 root 用户的密码。

###  MySQL5.7

**谷粒商城** 

1 docker pull mysql:5.7  下载docker镜像

2 运行MySQL 容器

```
docker run -p 3306:3306 --name mysql \
-v /mydata/mysql/log:/var/log/mysql \
-v /mydata/mysql/data:/var/lib/mysql \
-v /mydata/mysql/conf:/etc/mysql \
-e MYSQL_ROOT_PASSWORD=root \
-d mysql:5.7
参数说明
-p 3306:3306：将容器的 3306 端口映射到主机的 3306 端口
-v /mydata/mysql/conf:/etc/mysql：将配置文件夹挂载到主机
-v /mydata/mysql/log:/var/log/mysql：将日志文件夹挂载到主机
-v /mydata/mysql/data:/var/lib/mysql/：将配置文件夹挂载到主机
-e MYSQL_ROOT_PASSWORD=root：初始化 root 用户的密码为root
```

3 SQLyong进行远程连接 

```
grant all privileges on *.* to 'root'@'%' identified by 'root' with grant option;
flush privileges 刷新权限 
```

4 修改配置文件

```
cd /mydata/mysql/conf
vim my.conf 
[client]
default-character-set=utf8mb4

[mysql]
default-character-set=utf8mb4

[mysqld]
init_connect='SET collation_connection = utf8mb4_bin'
init_connect='SET NAMES utf8mb4'
character_set_server = utf8mb4
collation_server = utf8mb4_bin
skip-character-set-client-handshake
skip-name-resolve

这个字符编码不要设置为utf8 MySQL容器会启动不起来 报错
Error response from daemon: Container 7819b1b3c5a7f3efe4ec7e8bab59e80ad13f10a57b7b5484f664b205d3c1ce0d is not running
这个是错误示范
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8
[mysqld]
init_connect='SET collation_connection = utf8_unicode_ci' init_connect='SET NAMES utf8' character-set-server=utf8
collation-server=utf8_unicode_ci
skip-character-set-client-handshake
skip-name-resolve
[mysqld]
skip-name-resolve


```

###  Tomcat

官方的webapps是没有文件是需要自己弄得

```bash
docker search tomcat  
docker pull tomcat
 创建端口映射
# 在/root目录下创建tomcat目录用于存储tomcat数据信息
mkdir /root/tomcat
cd /root/tomcat
docker run -id --name=ydl_tomcat \
-p 8081:8080 \
-v /root/tomcat:/usr/local/tomcat/webapps \
tomcat 
外部访问
http://宿主机ip:8081/
```

###  Nginx

```bash
docker search nginx
docker pull nginx
端口映射
# 在/root目录下创建nginx目录用于存储nginx数据信息
mkdir /root/nginx
cd /root/nginx
mkdir conf
cd conf
文件配置
# 在~/nginx/conf/下创建nginx.conf文件,粘贴下面内容
vim nginx.conf


user  nginx;
worker_processes  1;
error_log  /var/log/nginx/error.log warn;
pid        /var/run/nginx.pid;


events {
    worker_connections  1024;
}


http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    keepalive_timeout  65;

    #gzip  on;

    include /etc/nginx/conf.d/*.conf;
}
docker run -id --name=ydl_nginx \
-p 80:80 \
-v /root/nginx/conf/nginx.conf:/etc/nginx/nginx.conf \
-v /root/nginx/logs:/var/log/nginx \
-v /root/nginx/html:/usr/share/nginx/html \
nginx

外部机器访问
```

###  Redis

```bash
docker search redis
docker pull redis:5.0
docker run -id --name=ydl_redis -p 6380:6379 redis:5.0
外部连接
```

**谷粒商城**

```
1下载redis
docker pull redis
如果不先创建这个conf文件 等会目录进行挂载的时候会吧redis.conf文件当成一个目录
mkdir -p /mydata/redis/conf && touch /mydata/redis/conf/redis.conf
2 启动容器 目录挂载
docker run -p 6379:6379 --name redis -v /mydata/redis/data:/data \
-v /mydata/redis/conf/redis.conf:/etc/redis/redis.conf \
-d redis redis-server /etc/redis/redis.conf
启动redis客户端 
3  docker exec -it redis redis-cli
4  在redis.conf 中写入
appendonly yes  开启AOF 持久化
```

需要配置密码 如果不陪着密码很危险 

```
1在创建容器的时候配置密码
--requirepass
2 创建容器之后配置密码
docker exec -it 容器ID bash
进入redis目录
cd /usr/local/bin
运行命令：
redis-cli
设置redis密码
config set requirepass 密码
如出现：(error) NOAUTH Authentication required
这是因为redis设置了密码，我们需要使用密码来进行验证之后再来对redis客户端进行操作，否则我们没有操作redis缓存数据库的权限。
需要用  auth 密码
```

如果使用redis  连接 Another Redis Desktop Manager

报错  Redis Client On Error: ReplyError: WRONGPASS invalid username-password pair or user is disabled. Con  就不要设置用户名

###  部署ELK

**elasticsearch**安装

1 下载镜像

```
docker pull elasticsearch:7.4.2
```

2  创建和容器内配置文件映射的文件

```
mkdir -p /mydata/elasticsearch/config 
mkdir -p /mydata/elasticsearch/data
```

3 添加配置文件

```
echo "http.host: 0.0.0.0" >> /mydata/elasticsearch/config/elasticsearch.yml
```

4 添加权限 

```
chmod -R 777 /mydata/elasticsearch/
```

5 运行容器

```
 docker run --name elasticsearch -p 9200:9200 -p 9300:9300 \
 -e "discovery.type=single-node" \
 -e ES_JAVA_OPTS="-Xms256m -Xmx512m" \
 -v /mydata/elasticsearch/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
 -v /mydata/elasticsearch/data:/usr/share/elasticsearch/data \
 -v /mydata/elasticsearch/plugins:/usr/share/elasticsearch/plugins \
 -d elasticsearch:7.4.2
```

**kiban**

```
docker pull kibana:7.4.2
```

```
docker run --name kibana \
-e ELASTICSEARCH_HOSTS=http://120.78.150.188:9200 -p 5601:5601 \
-d kibana:7.4.2
```

但是还要进行目录挂载修改 yml文件 设置中文

**IK分词器**

**Ik分词器版本要和ES和Kibana版本保持一致** 不然可能启动不了docker  

进入容器
#此命令需要在容器中运行
elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.8.0/elasticsearch-analysis-ik-7.8.0.zip

退出容器，重启容器

```
exit
docker restart es7.8
```

###  nacos

如果用docker安装naocs如果是 2版本可能会启动不了

```
 docker pull nacos/nacos-server:1.3.1
```

```
docker  run \
--name nacos -d \
-p 8848:8848 \
--privileged=true \
--restart=always \
-e JVM_XMS=256m \
-e JVM_XMX=256m \
-e MODE=standalone \
-e PREFER_HOST_MODE=hostname \
nacos/nacos-server:1.3.1
```

访问测试 http://43.138.137.168:8848/nacos

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20230102164254.png)

###  MQ

```
docker run \
 -e RABBITMQ_DEFAULT_USER=zhuxiaoyi \
 -e RABBITMQ_DEFAULT_PASS=412826zxyZXY \
 --name rabbitmq \
 --hostname mq1 \
 -p 15672:15672 \
 -p 5672:5672 \
 -d \
 rabbitmq:3-management
```

###  Nginx

```
mkdir /root/docker/nginx
mkdir /root/docker/nginx/conf
```

由于我们现在没有配置文件，也不知道配置什么。可以先启动一个nginx，讲他的配置文件拷贝出来

再作为映射，启动真正的nginx

```
docker pull nginx:1.17.4
docker run --name some-nginx -d nginx:1.17.4
docker container cp some-nginx:/etc/nginx /root/docker/nginx/conf
```

然后就可以删除这个容器了

```
docker docker rm -f some-nginx
```

在重新启动nginx

```
docker run --name nginx -p 80:80 \
        -v /root/docker/nginx/conf:/etc/nginx \
        -v /root/docker/nginx/html:/usr/share/nginx/html \
        -d nginx:1.17.4
```

###  FTP服务器

1 需要账号和密码

```
docker run -v /data/dav:/usr/local/nginx/html  -d -p 88:80 lutixiaya/nwebdav:latest
chmod o+w /data/dav
```

ip+端口访问测试 需要输入账号和密码

使用winscp 进行连接

```
1、点击新建站点
2、选择协议
3、输入服务器ip
4、输入端口
5、输入用户名，默认用户：admin
6、输入密码，默认密码：bash.lutixia.cn
7、登录
```

https://zhuanlan.zhihu.com/p/573721115   参考链接

2 无需账号和密码

在同一个文件目录下准备好这个三个文件

```
start-nginx.sh
```

```bash
#!/bin/bash
mkdir data
docker stop nginx_file_server
docker rm nginx_file_server

docker run -d -p 8081:8080\
        --name nginx_file_server \
        -v $(pwd)/data:/data \
        -v $(pwd)/nginx.conf:/etc/nginx/nginx.conf \
        -v $(pwd)/default.conf:/etc/nginx/conf.d/default.conf \
        nginx:stable-alpine
nginx.conf
```

nginx.conf

```dart
user  root;
worker_processes  1;

error_log  /var/log/nginx/error.log warn;
pid        /var/run/nginx.pid;

events {
    worker_connections  1024;
}

http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;
    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;
    sendfile        on;
    #tcp_nopush     on;
    keepalive_timeout  65;
    #gzip  on;
    include /etc/nginx/conf.d/*.conf;
}
default.conf
```

default.conf

```csharp
server {
    listen 8080; #端口
    server_name localhost; #服务名

    # for SSL listen port only
    #ssl_certificate                /etc/nginx/conf.d/server.pem;
    #ssl_certificate_key            /etc/nginx/conf.d/server-key.pem;
    #ssl_protocols                  TLSv1.2;
    #ssl_prefer_server_ciphers      on;
    #ssl_session_timeout            5m;
    #ssl_ciphers                    ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4;
    #underscores_in_headers         on;

    charset utf-8; # 避免中文乱码
    root /data; #显示的根索引目录，注意这里要改成你自己的，目录要存在
    location / {
        autoindex on;             #开启索引功能
        autoindex_exact_size off; # 关闭计算文件确切大小（单位bytes），只显示大概大小（单位kb、mb、gb）
        autoindex_localtime on;   # 显示本机时间而非 GMT 时间
    }
}
```

chmod +x start-nginx.sh && ./start-nginx.sh`

测试一下：

```bash
echo file_server > data/file1.txt
```

打开浏览器 [http://127.0.0.1:8081/](https://links.jianshu.com/go?to=http%3A%2F%2F127.0.0.1%3A8081%2F)

![img](https:////upload-images.jianshu.io/upload_images/18097588-6ef614e54a61f96e.png?imageMogr2/auto-orient/strip|imageView2/2/w/1200/format/webp)

###  DockerCompose 

```
sudo curl -L https://get.daocloud.io/docker/compose/releases/download/1.25.1/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
```

```
sudo chmod +x /usr/local/bin/docker-compose 
```

```
docker-compose --version
```

###  安装GitLab

1 下载镜像

```
docker pull twang2218/gitlab-ce-zh
```

2 启动容器

```
docker run -d -p 8443:443 -p 8090:80 -p 8022:22 --restart always --name gitlab -v /usr/local/gitlab/etc:/etc/gitlab -v /usr/local/gitlab/log:/var/log/gitlab -v /usr/local/gitlab/data:/var/opt/gitlab --privileged=true twang2218/gitlab-ce-zh
```

3 进入容器修改配置文件

由于进行了目录映射 也可以不在容器内部进行修改

```
docker exec -it gitlab bash
cd /etc/gitlab 
vim /etc/gitlab/gitlab.yml
```

4 修改配置文件

搜索URL 

external_url 'http://gitlab.example.com'

把url换成自己的

external_url 'http://116.205.133.97/'        

nginx['listen_port'] = nil

nginx['listen_port'] = 82    这个是注释掉的   

5 重启服务

这是在容器内部重启服务

```
gitlab-ctl restart
```

gitlab是有很多组件组成的只有这些组件都运行成功了，才启动成功。

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221206183340.png)

6 访问测试

http://120.78.214.226:8090

第一次登录默认是root用户 密码自己设定  不要低于8位

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221206183621.png)

###  安装Jenkins

1 下载镜像

```
docker pull jenkinsci/blueocean    中文版本
```

2 创建目录

```
# mkdir /home/jenkins_home
# chown -R 1000:1000 /home/jenkins_home/
# chown -R 1000:1000 /usr/local/src/jdk/jdk1.8/
# chown -R 1000:1000 /opt/apache-maven-3.5.0/
```

3 运行容器

```
docker run \
-d \
--name jenkins \
-p 9999:8080 \
-p 8888:8888 \
-p 50000:50000 \
-v /usr/local/src/jdk/jdk1.8:/usr/local/src/jdk/jdk1.8 \
-v /opt/apache-maven-3.5.0:/opt/apache-maven-3.5.0 \
-v/home/jenkins_home:/var/jenkins_home \
jenkins/jenkins:2.222.3-centos
```



4 查看密码

```
docker exec -it jenkins bash
cat /var/jenkins_home/secrets/initialAdminPassword 
```

b0468f2eb870422abf509fe59f74e003 

5 访问测试

http://120.78.214.226:9999/

6 进行汉化  

在安装插件页面输入 chinese 

7 替换插件下载地址

```
https://blog.csdn.net/weixin_45878889/article/details/123867587 
```

###  安装SonarQube

https://blog.csdn.net/OfficerGoodbody/article/details/126662724

新版SonarQube不支持MySQL

1 下载postgres镜像

docker pull postgres

2 创建文件

```
mkdir -p /opt/postgres/postgresql
mkdir -p /opt/postgres/data
```

3 创建网络

docker network create sonarqube

4 运行postgres 容器

```
docker run --name postgres -d -p 5432:5432 --net sonarqube \
-v /opt/postgres/postgresql:/var/lib/postgresql \
-v /opt/postgres/data:/var/lib/postgresql/data \
-v /etc/localtime:/etc/localtime:ro \
-e POSTGRES_USER=sonar \
-e POSTGRES_PASSWORD=sonar \
-e POSTGRES_DB=sonar \
-e TZ=Asia/Shanghai \
--restart always \
--privileged=true \
--network-alias postgres \
postgres:latest
```

5 安装 sonarQube

```
docker pull sonarqube
```

6 准备文件夹

mkdir -p /opt/sonarqube

```
echo "vm.max_map_count=262144" > /etc/sysctl.conf
sysctl -p
```

7 先运行一下拷贝文件

```
docker run -d --name sonarqube sonarqube
```

```
docker cp sonarqube:/opt/sonarqube/conf /opt/sonarqube
docker cp sonarqube:/opt/sonarqube/data /opt/sonarqube
docker cp sonarqube:/opt/sonarqube/logs /opt/sonarqube
docker cp sonarqube:/opt/sonarqube/extensions /opt/sonarqube
```

8 删除容器

```
docker stop  sonarqube
docker rm  sonarqube
```

9 添加权限

```
chmod -R 777 /opt/sonarqube/
```

10 修改配置文件

```
vim /opt/sonarqube/conf/ sonar.properties
修改账号和密码
sonar.jdbc.username=sonar
sonar.jdbc.password=sonar
sonar.jdbc.url=jdbc:postgresql://postgres:5432/sonar
```

11 运行容器

```
docker run -d --name sonarqube -p 9090:9000 \
 -e ALLOW_EMPTY_PASSWORD=yes \
 -e SONARQUBE_DATABASE_USER=sonar \
 -e SONARQUBE_DATABASE_NAME=sonar \
 -e SONARQUBE_DATABASE_PASSWORD=sonar \
 -e SONARQUBE_JDBC_URL="jdbc:postgresql://postgres:5432/sonar" \
 --net sonarqube \
 --privileged=true \
 --restart always \
 -v /opt/sonarqube/logs:/opt/sonarqube/logs \
 -v /opt/sonarqube/conf:/opt/sonarqube/conf \
 -v /opt/sonarqube/data:/opt/sonarqube/data \
 -v /opt/sonarqube/extensions:/opt/sonarqube/extensions\
 sonarqube
```

12测试访问

浏览器输入http://ip:9090，开始初始化数据库初始化成功后进入登录界面，账号：admin  密码：admin

### 轻量级容器监控

**portanier**

  下载   docker pull lihaixin/portainer

```
docker run -d -p 9000:9000 --restart=always \
-v /var/run/docker.sock:/var/run/docker.sock \
--name portainer lihaixin/portainer
```

访问测试   http://43.138.137.168:9000/#/home

![](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20221122163043067.png)

### 重量级 容器监控 

docker-compose.yml配置  

```
version: '3.1'
volumes:
  grafana_data: {} 
services:
 influxdb:
  image: tutum/influxdb:0.9
  restart: always
  environment:
    - PRE_CREATE_DB=cadvisor
  ports:
    - "8083:8083"
    - "8086:8086"
  volumes:
    - ./data/influxdb:/data
 
 cadvisor:
  image: google/cadvisor
  links:
    - influxdb:influxsrv
  command:
   -storage_driver=influxdb - storage_driver_db=cadvisor -storage_driver_host=influxsrv:8086
  restart: always
  ports:
    - "8080:8080"
  volumes:
    - /:/rootfs:ro
    - /var/run:/var/run:rw
    - /sys:/sys:ro
    - /var/lib/docker/:/var/lib/docker:ro
 
 grafana:
  user: "104"
  image: grafana/grafana
  user: "104"
  restart: always
  links:
    - influxdb:influxsrv
  ports:
    - "3000:3000"
  volumes:
    - grafana_data:/var/lib/grafana
  environment:
    - HTTP_USER=admin
    - HTTP_PASS=admin
    - INFLUXDB_HOST=influxsrv
    - INFLUXDB_PORT=8086
    - INFLUXDB_NAME=cadvisor
    - INFLUXDB_USER=root
    - INFLUXDB_PASS=root

```

docker-compose up -d 