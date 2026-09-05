我在看视频的时候 有一个有意思的弹幕    怎么不讲docker 集群        萧炎五星大斗师 学习docker

**阿里云个人镜像厂库地址**

https://cr.console.aliyun.com/cn-shenzhen/instance/repositories

dockerfiler  镜像构建

 Compose是在单机进行容器编排 Horbor 镜像仓库  

Docker swarm 在多机进行容器编排

[Docker](https://so.csdn.net/so/search?q=Docker&spm=1001.2101.3001.7020) Compose缺点是不能在分布式多机器上使用；Docker swarm缺点是不能同时编排多个服务，所以才有了Docker Stack，可以在分布式多机器上同时编排多个服务。

所以才有了Docker Stack

# 1 Docker

## 1.1 Docker常用命令

![](https://cdn.nlark.com/yuque/0/2021/png/1613913/1625373590853-2aaaa76e-d5b5-446b-850a-f6cfa26ac70a.png?x-oss-process=image%2Fwatermark%2Ctype_d3F5LW1pY3JvaGVp%2Csize_85%2Ctext_YXRndWlndS5jb20gIOWwmuehheiwtw%3D%3D%2Ccolor_FFFFFF%2Cshadow_50%2Ct_80%2Cg_se%2Cx_10%2Cy_10)

如果在使用 镜像或者是容器的别名有问题的话，就使用它的id

docker 服务 启动  停止  重启 查看 开机自启

systemctl    start |stop | restart | status|enable docker   

**docker 镜像** 

docker images  查看镜像  docker search 搜索镜像
    docker pull centos:7 拉取镜像 docker push 推送镜像  
docker rmi 删除镜像  docker rmi docker images -q  删除所有版本

**docker 容器**

docker ps    正在运行的容器  (-a 查看所有容器)   -l :显示最近创建的容器。 -n :列出最近创建的n个容器。

docker  start |stop | restart | status|enable  容器id    启动 停止 重启 查看  开机自启  

docker run   -i：表示运行容器  -t：启动后会进入其命令行 -d：后台运行

--name :创建的别名 -v：表示目录映射关系 -p：表示端口映射， /  如果一行写不下用/来换行

exec  进入运行时容器    示例docker exec -it 容器id /bin/bash  

 logs -f  容器的日志  inspect 容器的信息信息 rm删除容器

docker build -f 指定文件  -t 指定镜像名 

**Dockerfiler**  

FROM 指定基础镜像   RUN  运行命令   EXPOSE 开放端口  ADD 复制文件到镜像 自动解压 docker build 构建示例    docker build -t reggie_take_out-1.0-SNAPSHOT.jar reggie.jar .   （-t表示 构建的文件 .是在当前目录）

**docker-compose**   docker-compose.yml   docker-compose config -q 坚持语法是否有问题

docker-compose up 启动   -d 后台启动

**其他**

**commit**   容器提交镜像  tag 打标签   **port** 查看端口映射情况  top 查看正在运行的进程  inspect 查看容器详细信息  后面可以跟| tail -n 20

docker stats  查看  

**docker cp** 复制一个文件到容器内部   文件路径 容器名：文件路径    docker cp /root/F.sql mysql:/

docker update --restart=always <CONTAINER_ID>  自动重启容器

复制到MySQL容器目录下

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221122163458.png)

## 1.2什么是docker

Docker 是一个开源的**应用容器引擎**，基于 Go 语言开发。Docker 可以让开发者打包他们的应用以及依赖包到一个轻量级、可移植的容器中，然后发布到任何流行的 Linux 机器上，也可以实现虚拟化。容器是完全使用沙箱机制，相互之间不会有任何接口（类似 iPhone 的 app）,更重要的是容器性能开销极低。

把环境和项目进行打包，发布到docker仓库  

**Docker应用场景**

- Web 应用的自动化打包和发布

- 自动化测试和持续集成、发布

- 在服务型环境中部署和调整数据库或其他的后台应  


docker 通过镜像和隔离机制来解决 

**DockerRUN**

如果docker run hello-world

开始运行  docker会在本机寻找镜像，如果有就在本机运行，如果没有就去Dockerhup上下载，DockerHup是否可以找到，如果能找到就下载，找不到，就返回

Docke 如何工作的 是一个Client-Server结构系统，Docker的守护运行在主机上，通过Socket从客户端访问

DockerSever接收到Docker Client的指令

Docker 为什么比虚拟机更快

Docker 不需要在重新加载一个操作系统内核，而docker是利用宿主机的操作系统来操作的

## 1.3 docker 架构图

Docker 是一个 C/S 模式的架构，后端是一个松耦合架构，模块各司其职

Docker Client 是一个客户端   systemctl start dcker    启动其客户端  进入客户端 然后才能发送一系列命令 

docker run -d  也是这么一个意思

![](https://pics4.baidu.com/feed/3b292df5e0fe9925352c266b0d1303d78db17128.jpeg@f_auto?token=c40cb7490618ce8a15a9032ad3bb0eea)

https://baijiahao.baidu.com/s?id=1697352130792897690&wfr=spider&for=pc

1 用户是使用 Docker Client 与 Docker Daemon 建立通信，并发送请求给后者。

2 Docker Daemon 作为 Docker 架构中的主体部分，首先提供 Docker Server 的功能使其可以接受 Docker Client 的请求。

3 Docker Engine 执行 Docker 内部的一系列工作，每一项工作都是以一个 Job 的形式的存在。

4 Job 的运行过程中，当需要容器镜像时，则从 Docker Registry 中下载镜像，并通过镜像管理驱动 Graphdriver 将下载镜像以 Graph 的形式存储。

5 当需要为 Docker 创建网络环境时，通过网络管理驱动 Networkdriver 创建并配置 Docker容器网络环境。

6 当需要限制 Docker 容器运行资源或执行用户指令等操作时，则通过 Execdriver 来完成。

7 Libcontainer 是一项独立的容器管理包，Networkdriver 以及 Execdriver 都是通过 Libcontainer 来实现具体对容器进行的操作。

# 2 Docker基础使用

如果在安装docker 升级yum源 提升yum源找不到的话

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221120145401.png)

   yum clean all   清理  然后重新加载一下 yum makecache

## 2.1 docker安装

### 2.1.1 docker安装

或者使用脚本文件安装

bash <(curl -sL http://120.46.214.226:8081/docker.sh)   

touch docker.sh

```
#!/bin/bash
yum install -y yum-utils
yum-config-manager \
    --add-repo \
    https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
mkdir -p /etc/docker && touch /etc/docker/daemon.json
cat > /etc/docker/daemon.json <<END
{
  "registry-mirrors": ["https://3sf1ht53.mirror.aliyuncs.com"]
}                                                                                    
END
yum install docker-ce docker-ce-cli containerd.io
systemctl start docker
```

chmod +x docker.yml  && ./docker.yml



0、安装完docker一定要先启动docker 

1、使用uname命令验证 内核版本  

```
[root@localhost docker]# uname -r
3.10.0-1127.el7.x86_64
```

2、卸载已安装的Docker

如果已经安装过Docker，请先卸载，再重新安装，来确保整体的环境是一致的。

```
yum remove docker 
docker-client 
docker-client-latest 
docker-common 
docker-latest 
docker-latest-logrotate 
docker-logrotate docker-engine
```

3、安装yum工具包和存储驱动

```
yum install -y yum-utils
```

4、设置镜像的仓库

```
用国内的，阿里云docker镜像
yum-config-manager \
    --add-repo \
    https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
```

5、安装docker

注意 : docker-ce 社区版 而ee是企业版。这里我们使用社区版即可。

```
yum install docker-ce docker-ce-cli containerd.io
```

6、启动docker

```
systemctl start docker
```

7、设置开机启动

```
systemctl enable docker
```

### 2.2 docker卸载

卸载docker

```
yum remove docker \
docker-client \
docker-client-latest \
docker-common \
docker-latest \
docker-latest-logrotate \
docker-logrotate \
docker-engine
```

### 2.1.3 离线安装

对于yum命令不能用的情况下

1 下载安装包

镜像地址

https://download.docker.com/linux/static/stable/x86_64/docker-18.03.1-ce.tgz

wget https://cce-tools.bj.bcebos.com/docker/docker-cce-19.03.13.tar.gz  

2 解压 

tar -zxvf

3 把解压的文件移动到/usr/bin 目录里

**这个目录不要随便改，否则可能出错**

mv  1.txt 2.txt /usr/bin  可以移动多个文件

4 创建docker.service文件  进入/etc/systemd/system/目录下，创建docker.service 

我理解应该就是docker的服务文件 

5 编写docker.service

**--insecure-registry=116.62.205.170 此处改为你自己服务器ip**

```
[Unit]
Description=Docker Application Container Engine
Documentation=https://docs.docker.com
After=network-online.target firewalld.service
Wants=network-online.target

[Service]
Type=notify
# the default is not to use systemd for cgroups because the delegate issues still
# exists and systemd currently does not support the cgroup feature set required
# for containers run by docker
ExecStart=/usr/bin/dockerd --selinux-enabled=false --insecure-registry=116.62.205.170
ExecReload=/bin/kill -s HUP $MAINPID
# Having non-zero Limit*s causes performance problems due to accounting overhead
# in the kernel. We recommend using cgroups to do container-local accounting.
LimitNOFILE=infinity
LimitNPROC=infinity
LimitCORE=infinity
# Uncomment TasksMax if your systemd version supports it.
# Only systemd 226 and above support this version.
#TasksMax=infinity
TimeoutStartSec=0
# set delegate yes so that systemd does not reset the cgroups of docker containers
Delegate=yes
# kill only the docker process, not all processes in the cgroup
KillMode=process
# restart the docker process if it exits prematurely
Restart=on-failure
StartLimitBurst=3
StartLimitInterval=60s
[Install]
WantedBy=multi-user.target

```

6 添加文件权限

```
chmod 777 /etc/systemd/system/docker.service
```

7 刷新权限  

意思就是说**，你注册了一个服务进行所以要刷新一下**

```
systemctl daemon-reload
```

8 启动docker

```
systemctl start docker
```

一定要先启动  

9 参考博客

https://blog.csdn.net/weixin_45552405/article/details/119935387

### 2.1.4 docker升级

```
1列出包含docker字段的软件的信息
rpm -qa | grep docker 
2 yum remove移除 docker
yum remove docker-1.13.1-96.gitb2f74b2.el7.centos.x86_64
yum remove docker-client-1.13.1-96.gitb2f74b2.el7.centos.x86_64
yum remove docker-common-1.13.1-96.gitb2f74b2.el7.centos.x86_64
3 升级docker的版本为最新版本
curl -fsSL https://get.docker.com/ | sh
3 重启docker
systemctl restart docker
```

## 2.2 设置镜像加速器

1、 编辑文件/etc/docker/daemon.json  这个需要自己创建

```text
# 执行如下命令： 
mkdir -p /etc/docker && touch /etc/docker/daemon.json
vi /etc/docker/daemon.json 

```

2、在文件中加入下面内容

```text
{
  "registry-mirrors": ["https://3sf1ht53.mirror.aliyuncs.com"]
}                                                                                    
```

如果之前重启docker  systemctl restart docker 会报错 

直接启动或者直接关闭

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221120112513.png)

## 2.3 dock基础命令

```text
# 启动docker服务： 
systemctl start docker 
# 停止docker服务： 
systemctl stop docker 
# 重启docker服务： 
systemctl restart docker 
# 查看docker服务状态： 
systemctl status docker 
# 设置开机启动docker服务： 
systemctl enable docker
```

## 2.4 镜像 

```java
docker images  查看镜像
```

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221122183156.png)

REPOSITORY：镜像名称  TAG：镜像标签  IMAGE ID：镜像ID CREATED：镜像的创建日期 SIZE：镜像大小

```
docker search 镜像名称
docker pull centos:7 拉取镜像，需要先查看一下镜像版本，否则拉取的镜像就不是想要的版本
docker rmi 镜像id/或者名字+版本    
docker rmi docker images -q  删除所有版本
```

镜像是一种轻量级，可执行的独立软件包，包括代码，运行时，库，环境变量和配置文件

所有的应用，直接打包docker 

**UnionFs联合文件系统**   是一种分层，轻量级并且高性能的文件系统，镜像是通过分层来进行继承，制作各种具体的镜像应用，docker的镜像是有一层一层的文件系统组成的

## 2.5 容器

```text
查看正在运行的容器使用命令：
docker ps
查看所有容器使用命令：
docker ps -a
查看容器的日志
docker logs -f 容器名  
查看容器详细信息
docker inspect ydlcentos3（容器名/容器id）
删除容器
docker rm 容器名称（容器ID）
删除所有容器：docker rm docker ps -a -q
# 停止正在运行的容器：docker stop 容器名称或者ID 
docker stop ydlcentos2 
# 启动已运行过的容器：docker start 容器名称或者ID 
docker start ydlcentos2
进入运行时容器用exec
docker exec -it 容器id /bin/bash 

# 启动所有镜像
docker start $(docker ps -a -q)
 
# stop停止所有容器
docker stop $(docker ps -a -q)
 
# remove删除所有容器
docker rm $(docker ps -a -q) 
```

创建容器有两种形式

```text
以**交互式**方式创建并启动容器，启动完成后，直接进入当前容器。使用exit命令退出容器。需要注意的是以此种方式启动容器，如果退出容器，则容器会进入**停止**状态。
docker run -it --name=ydlcentos1 centos:7 /bin/bash
#创建并启动守护式容器 
docker run -di --name=ydlcentos2 centos:7 
#登录进入容器命令为：
docker exec -it container_name (或者 container_id) /bin/bash
退出命令
exit
```

参数的意思



- -i：表示运行容器

- -t：表示容器启动后会进入其命令行。加入这两个参数后，容器创建就能登录进去。即分配一个伪终端。

- -d：在run后面加上-d参数,则会创建一个守护式容器在后台运行（这样创建容器后不会自动登录容器，如果只加-i -t两个参数，创建后就会自动进去容器，但当退出的时候容器就会停止）。

- --name :为创建的容器命名。**是在创建的时候使用在进入容器内部的时候不行**

- -v：表示目录映射关系（前者是宿主机目录，后者是映射到宿主机上的目录），可以使用多个－v做多个目录或文件映射。注意：最好做目录映射，在宿主机上做修改，然后共享到容器上。

- -p：表示端口映射，前者是宿主机端口，:分割 后者是容器内的映射端口。可以使用多个-p做多个端口映射

- ps 

- -e` 指定容器环境变量

- -l :显示最近创建的容器。

  -n :列出最近创建的n个容器。

  **-s :**显示总的文件大小。

- /  如果一行写不下用/来换行

- **-f :**根据条件过滤显示的内容

- docker run --link  连接其他容器

**文件拷贝**

从宿主机到docker容器中 

```text
# docker cp 需要拷贝的文件或目录 容器名称:容器目录 
# 创建一个文件abc.txt 
touch itlils.txt 
# 复制abc.txt到mycentos2的容器的 / 目录下 
docker cp itlils.txt ydlcentos2:/root
# 进入mycentos2容器 
docker exec -it ydlcentos2 /bin/bash 
# 查看容器 / 目录下文件 
ll
```

从docker容器中拷贝到宿主机中

```text
# docker cp 容器名称:容器目录 需要拷贝的文件或目录
#进入容器后创建文件cba.txt 
touch itnanls.log 
# 退出容器 
exit 
# 在Linux宿主机器执行复制；将容器mycentos2的/cba.txt文件复制到宿主机器的/root目录下 
docker cp ydlcentos2:/root/itnanls.log /root
```

**容器数据卷**

可以在创建容器的时候，将宿主机的目录与容器内的目录进行映射，这样我们就可以通过修改宿主机某个目录的文件从而去影响容器，创建容器时添加-v参数，后边为宿主机目录:容器目录

```text
# 创建linux宿主机器要挂载的目录 
mkdir /root/binlog 
# 创建并启动容器ydlcentos3 ,并挂载linux中的/root/binlog 目录到容器的/root/binlog ；也就是在 linux中的/root/binlog 中操作相当于对容器相应目录操作 
docker run -di -v /root/binlog:/root/binlog --name=ydlcentos3 centos:7
# 在linux下创建文件 
touch /root/binlog/mysql.log 
# 进入容器 docker exec -it ydlcentos3 /bin/bash
# 在容器中查看目录中是否有对应文件def.txt 
ll /root/binlog
```

**案例**

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

**问题**

如果进入不到运行的容器    该怎么解决-bash找不到 

## **2.5**更新容器

https://blog.lanweihong.com/posts/26195/

## 2.6DockerFile 

Dockerfile 是一个用来构建镜像的文本文件，文本内容包含了一条条构建镜像所需的指令和说明。

开始构建   -f 构建文件 -t 指定镜像名 . 是代表在这一个文件夹里面

**编写规则**

- 指令大写 后面的参数小写
- Dockerfile 非注释行第一行必须是 FROM
- 文件名必须是 Dockerfile  
- 每一条指令都会生成一个镜像层，镜像层多了执行效率就慢，能写成一条指定的就写成一条指令

**运行过程**

```
把当前目录和子目录当做上下文传递给docker服务，命令最后的点表示当前上下文。
从当前目录（不包括子目录）找到Dockerfile文件，如果不指定文件，必须是此文件名。
检查docker语法。
从基础镜像运行一个容器。
执行指令，修改容器，如上面操作添加数据卷，修改首页。
对修改后的容器提交一个新的镜像层，也可叫做中间层镜像。
针对中间层生成的镜像，运行新的容器。
重复执行命令修改容器、提交镜像、运行容器指令，直到所有指令执行完成
```

**Dockerfile 命令**   后面跟的参数都是小写

菜鸟教程  

https://www.runoob.com/docker/docker-dockerfile.html

FROM		指定基础镜像   这个必须要有
MAINTAINER	指定作者
RUN				     执行参数中定义的命令，构建镜像时需要的命令
EXPOSE			    向容器外部公开的端口号
WORKDIR		 设置容器内默认工作目录
USER		        	指定用户
ENTROYPOINT	指定一个容器启动时运行的命令
ENV			      	设置环境变量
ADD|COPY		 复制文件到镜像中
VOLUME			  容器数据卷，向镜像创建的容器添加卷
CMD				      容器启动时要运行的命令，可以有多个，但只有最后一个生效

**构建命令** 

docker build -t nginx:v3 .   

.代表的是在当前目录下   -t 代表的是 需要构建的 

**在进行构建的时候出现的问题** 

1 构建的.jar文件小大写    语法结尾没有标点 " . "

docker build -t reggie_take_out-1.0-SNAPSHOT.jar reggie.jar .  EXPOSE:8080  这个不要带：号  都会提升语法错误

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221122124138.png) 2 如果在用FROM 指定jdk版本的时候要加上open    如果openjdk:11

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221122130306.png)

tag 打标签的时候 如果被打的对象有其他版本 要带上版本号 

## 2.7 推送镜像到仓库

需要先登录

**这里特别提醒** 在推送仓库的 时候   你打的镜像标签要带前缀   如果是dockerhup 就是 这种形式  

![](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20221122135446562.png)

就是让你在推送的时候找到你的仓库  

如果是阿里云要加上你的 阿里云全名

```
$ docker login --username=规范办法给您发 registry.cn-shenzhen.aliyuncs.com
$ docker tag [ImageId] registry.cn-shenzhen.aliyuncs.com/zhuxiaoyi/docker:[镜像版本号]
$ docker push registry.cn-shenzhen.aliyuncs.com/zhuxiaoyi/docker:[镜像版本号]
```

这个zhuxiaoyi是命名空间 docker是这个仓库的名字 镜像版本号就是你镜像文件的名字+版本号

如果是  自己搭建的 需要有加上

 ip:端口号/仓库位置 /镜像id:版本号

### 1 推送到DockHup

账号zhuxiaoyizxy

https://hub.docker.com/

### 2 推送到阿里云

登录

```
docker login --username=规范办法给您发 registry.cn-shenzhen.aliyuncs.com
```

打标签 

```
 docker tag mysql:5.7 registry.cn-shenzhen.aliyuncs.com/zhuxiaoyi/ruggie/mysql:5.7
```

查看镜像 

docker images 

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221122144549.png)

推送  

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221122144808.png)

请求资源被拒绝 

是因为镜像打标签错误  厂库后面就跟着镜像 用:

拉取

### 3 推送harbor

# 3进阶

## 3.1 数据卷

docker volume ls  列出所有数据卷

**docker inspcet**  查看 容器卷别名 容器的详细信息

docker volume 数据卷名

容器之间可以有一个数据共享技术   docker容器中产生的数据同步到本地，容器的目录挂载到我们宿主机上

其目的，容器的持久化和同步操作，容器之间的数据也是可以共享的

-v 参数    来实现  

docker run -it -v 主机目录：容器目录

如果容器内部修改，也会同步到主机上去

如果在主机上修改也会同步到容器上

**MySQL数据同步**

**commit**   

```
docker commit [OPTIONS] CONTAINER [REPOSITORY[:TAG]]
docker commit -a "runoob.com" -m "my apache" a404c6c174a2  mymysql:v1   
```

- **-a :**提交的镜像作者；
- **-c :**使用Dockerfile指令来创建镜像；
- **-m :**提交时的说明文字；
- **-p :**在commit时，将容器暂停。

进入运行时容器的几种方法

http://t.zoukankan.com/-wenli-p-13307542.html

## 3.2网络

服务名 写死   ip不要写死   

### 什么是docker网络 

https://blog.csdn.net/Trollz/article/details/126176819

 网络  跟物理虚拟机的网络差不多   docker 默认的是etho

网络默认情况   

ifconfig 

ens33  宿主机ip

lo    本地回环地址

**docker 网络来干什么**

容器间的互联和通信以及端口映射

容器IP变动时候可以通过服务名直接网络通信而不受到影响

如果容器ip 写死的话  容器重启后  容器内的ip就会重新分配  就会访问不同

### 网络命令

拟网桥    查看网络默认

​     查看网卡  name 网络模式  docker network ls       driver 驱动   scope 范围

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221123175442.png)

docker pull  

docker network inspect    查看网络数据源

docker network ls              查看网络

docker network rm  网络名字   删除网络

 docker network    create      创建网络

### **网络模式**

**bridge**   Docker 服务默认会创建一个 docker0 网桥（其上有一个 docker0 内部接口），该桥接网络的名称为docker0，它在内核层连通了其他的物理或虚拟网卡，这就将所有容器和本地主机都放到同一个物理网络。**Docker 默认指定了 docker0 接口 的 IP 地址和子网掩码，让主机和容器之间可以通过网桥相互通信。**

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221123182615.png)

1 Docker使用Linux桥接，在宿主机虚拟一个Docker容器网桥(docker0)，Docker启动一个容器时会根据Docker网桥的网段分配给容器一个IP地址，称为Container-IP，同时Docker网桥是每个容器的默认网关。因为在同一宿主机内的容器都接入同一个网桥，这样容器之间就能够通过容器的Container-IP直接通信。

2 docker run 的时候，没有指定network的话默认使用的网桥模式就是bridge，使用的就是docker0。在宿主机ifconfig,就可以看到docker0和自己create的network(后面讲)eth0，eth1，eth2……代表网卡一，网卡二，网卡三……，lo代表127.0.0.1，即localhost，inet addr用来表示网卡的IP地址

3 网桥d  ocker0创建一对对等虚拟设备接口一个叫veth，另一个叫eth0，成对匹配。
   3.1 整个宿主机的网桥模式都是docker0，类似一个交换机有一堆接口，每个接口叫veth，在本地主机和容器内分别创建一个虚拟接口，并让他们彼此联通（这样一对接口叫veth pair）；
   3.2 每个容器实例内部也有一块网卡，每个接口叫eth0；
   3.3 docker0上面的每个veth匹配某个容器实例内部的eth0，两两配对，一一匹配。
 通过上述，将宿主机上的所有容器都连接到这个内部网络上，两个容器在同一个网络下,会从这个网关下各自拿到分配的ip，此时两个容器的网络是互通的。

**host**  容器不会配置自己的ip 使用宿主机的ip和端口

<img src="https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221123182852.png" style="zoom:50%;" />



**none**   容器有独立的 network namespance   但并没有对其进行任何网络设置    禁用网络

​      直接使用宿主机的 IP 地址与外界进行通信，不再需要额外进行NAT 转换。

**container**   新创建的容器不会创建自己的网卡和配置自己的ip 而是和一个指定的容器共享ip 端口范围

<img src="https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221123183204.png" style="zoom:50%;" />

**自定义网络模式**

docker link 过时了    

自定义网络本身就维护好了主机名和ip的对应关系（ip和服务名名都能通）

after 引入自定义网络  

新建自定义网络     



## 3.3 dockercompsose

https://baijiahao.baidu.com/s?id=1753599611954609453&wfr=spider&for=pc

### dockercompsose是什么

是docker官方的开源项目 负责实现对docker容器集群的快速编排

能干什么 

以很容易地用一个配置文件定义一个多容器的应用，然后使用一条指令安装这个应用的所有依赖，完成构建。Docker-Compose 解决了容器与容器之间如何管理编排的问题。

相当与spring管理对象  而dockercompose管理的是容器  容器多了  涉及了容器的启动和加载条件及要求，需要来管理

compose允许用户通过一个单独的docker-compose.yml模板文件 来定义一组关联的应用容器为一个项目

### **下载dokcer-compose**

```
sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
docker-compose --version   
```

**docker-compose config -q** 检查语法是否有问题

docker-compose.yml 是dockercompose 的配置文件 

**一个文件** 

docker-compose.yml 

**两个要素**

  服务 一个一个 应用容器实例，

工程  有一组关联的容器组成的一个完整的业务单元。

**三个步骤**    

编写dockerfile定义各个微服务应用并构建出对应的镜像文件

使用docker-compose.yml定义一个完整的业务单元，安排好整体应用中的各个容器服务

最后执行docker-compos up命令 来启动并运行整个应用程序，完成一键部署上线

**问题**

如果出现这种问题

```
ERROR: The Compose file './docker-compose_v3_centos_mysql_latest.yaml' is invalid because:
Unsupported config option for services.zabbix-agent: 'profiles'
Unsupported config option for services.zabbix-java-gateway: 'profiles'
Unsupported config option for services.zabbix-proxy-mysql: 'profiles'
Unsupported config option for services.zabbix-proxy-sqlite3: 'profiles'
Unsupported config option for services.zabbix-snmptraps: 'profiles'
Unsupported config option for services.zabbix-web-apache-mysql: 'profiles'
Unsupported config option for services.zabbix-web-service: 'profiles'
```

升级docker-compose 

```
 curl -L "https://get.daocloud.io/docker/compose/releases/download/v2.2.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
 chmod +x /usr/local/bin/docker-compose
 docker-compose --version

```



### 常用命令

docker-compose 

-h 查看帮助 

up 启动所有的compose服务

-d 启动所有的服务并后台运行

down 停止并删除容器，网络，卷，镜像  docker-compose  down 

ps 展示compose 编配过运行所有的容器

top 展示compose 编配过运行所有的容器id

logs  yml里面的服务id 查看容器输出日志

confg 检查配置

restart 重启

satart 启动

stop 停止

**up** 

-d 在后台运行服务容器 
–no-color 不使用颜色来区分不同的服务的控制输出 
–no-deps 不启动服务所链接的容器 
–force-recreate 强制重新创建容器，不能与–no-recreate同时使用 
–no-recreate 如果容器已经存在，则不重新创建，不能与–force-recreate同时使用 
–no-build 不自动构建缺失的服务镜像 
–build 在启动容器前构建服务镜像 
–abort-on-container-exit 停止所有容器，如果任何一个容器被停止，不能与-d同时使用 
-t, --timeout TIMEOUT 停止容器时候的超时（默认为10秒） 
–remove-orphans 删除服务中没有在compose文件中定义的容器 
–scale SERVICE=NUM 设置服务运行容器的个数，将覆盖在compose中通过scale指定的参数

touch docker-compose.yml

```
version: "3" 

services:
  microService:  定义的服务名不冲突即可
    image: zzyy_docker:1.6  镜像名
    container_name: ms01  如果不指定这个只是一个名字  会给你加一个前缀
    ports:
      - "6001:6001"                  到这的意思是  docker run -d -p 6001:6001
    volumes:                            
      - /app/microService:/data                     -v
    networks:                                        --netwok   
      - atguigu_net 
    depends_on:    代表的意思是他依赖于 redis和MySQL 只有他俩先启动
      - redis
      - mysql
      
 docker run -d -p 6001:6001 -v /app/microService:/data --networks atguigu_net 
 --name microService zzyy_docker:1.6

redis:
    image: redis:6.0.8
    ports:
      - "6379:6379"
    volumes:
      - /app/redis/redis.conf:/etc/redis/redis.conf
      - /app/redis/data:/data
    networks: 
      - atguigu_net
    command: redis-server /etc/redis/redis.conf
 
  mysql:
    image: mysql:5.7
    environment:   环境变量                                                                                                                                                                                                           
      MYSQL_ROOT_PASSWORD: '123456'
      MYSQL_ALLOW_EMPTY_PASSWORD: 'no'
      MYSQL_DATABASE: 'db2021'
      MYSQL_USER: 'zzyy'
      MYSQL_PASSWORD: 'zzyy123'
    ports:
       - "3306:3306"
    volumes:
       - /app/mysql/db:/var/lib/mysql
       - /app/mysql/conf/my.cnf:/etc/my.cnf
       - /app/mysql/init:/docker-entrypoint-initdb.d
    networks:
      - atguigu_net
    command: --default-authentication-plugin=mysql_native_password #解决外部无法访问
 
networks: 
   atguigu_net: 
 

```

## 3.4 docker swarm

![](https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fyqfile.alicdn.com%2F8bee80c3bd9551a71ee687e3d940d42cc286a797.png&refer=http%3A%2F%2Fyqfile.alicdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1671708461&t=5ce319357cb9723e08e237ffd4a5d36c)

[Docker三剑客之Docker Swarm - 一本正经的搞事情 - 博客园 (cnblogs.com)](https://www.cnblogs.com/zhujingzhi/p/9792432.html#_label0)

就是一个集群

## 3.5 Docker stack

# 4 Docker容器部署软件

需要经过这几个步骤

1 查找需要安装的版本

2 拉取镜像 

3 创建容器 （目录挂载）

4 开放端口 （如果是阿里云需要，开放阿里云端口）

5 外部访问

## 4.0 jdk

```
dockere pull openjdk:11
```

```
docker run -d -t --name java-11 openjdk:11 
```

## 4.1 MySQL

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

## 4.1 MySQL5.7

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
```

## 4.2 Tomcat

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

## 4.3 Nginx

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

## 4.4 Redis

```bash
docker search redis
docker pull redis:5.0
docker run -id --name=ydl_redis -p 6380:6379 redis:5.0
外部连接
```

## 4.4 Redis1

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

## 4.5 部署ELK

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

## 4.6 nacos

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

## 4.7 MQ

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

## 4.8 Nginx

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

## 4.9 FTP服务器

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

## 4.10 DockerCompose 

```
sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
```

```
sudo chmod +x /usr/local/bin/docker-compose 
```

```
docker-compose --version
```

## 4.11 安装GitLab

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

## 4.12 安装Jenkins

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

## 4.13 安装SonarQube

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

# 5 docker可视化工具

## 轻量级portanier

  下载   docker pull lihaixin/portainer

```
docker run -d -p 9000:9000 --restart=always \
-v /var/run/docker.sock:/var/run/docker.sock \
--name portainer lihaixin/portainer
```

访问测试   http://43.138.137.168:9000/#/home

![](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20221122163043067.png)

## 重量级 容器监控 

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

后台启动并下载  



之  CAdvisor + lnfluxDB+Granfana    简称CLG

CAdvisor 

 收集       容器监控资源  CPU 网络IO  磁盘IO  等监控   web页面实时查看容器运行情况

监控  默认存储2分钟     也可以存储 Redis，kafka，Elasticsearch  等   

InfluxDB   存储    用go语言编写的一个开源分布式时序，事件和指标数据库，无需外部依赖

Granfan     监控     一个开源的数据监控分析可视化 监控

## prometheus

# 6 项目实战

## 单体

### 瑞吉外卖

### 若依项目

## 微服务





