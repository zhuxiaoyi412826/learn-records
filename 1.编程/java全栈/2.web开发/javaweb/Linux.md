装机

大白菜 

1 http://dbc.nxexvq.cn/?bd_vid=10837448489050321574  下载启动器

2 大白菜U盘启动盘制作工具完整使用教程-大白菜u盘启动 (dabaicai.com)](https://www.dabaicai.com/help_920.html)

3 下载镜像 

   https://next.itellyou.cn/Original/#

linux    centos7.6

https://mirrors.aliyun.com/centos-vault/7.6.1810/isos/x86_64/?spm=a2c6h.25603864.0.0.1ae3289bcnoNgI

当我们下载CentOS7 时会发现有几个版本可以选择，如下：

1、CentOS-7-DVD版：DVD是标准安装盘，一般下载这个就可以了。

2、CentOS-7-NetInstall版：网络安装镜像。

3、CentOS-7-Everything版：对完整版安装盘的软件进行补充，集成所有软件。

4、CentOS-7-GnomeLive版：GNOME桌面版。

5、CentOS-7-KdeLive版：KDE桌面版。

6、CentOS-7-livecd版：光盘上运行的系统，类拟于winpe

7、CentOS-7-Minimal版：最小安装盘，只有必要的软件，自带的软件最少

# 0 常用命令

cat > /etc/mysql/my.cnf <<END
追加内容
END

```
 #init 0 - 停机（千万不能把initdefault 设置为0 ）
 #init 1 - 单用户模式，只root用户进行维护
 #init 2 - 多用户，不能使用NFS(Net File System) 不联网
 #init 3 - 完全多用户模式(标准的运行级)
 #init 4 - 安全模式
 #init 5 - X11 （xwindow) 图形化界面模式
 #init 6 - 重新启动 （千万不要把initdefault 设置为6 ）
```

解决下载的jdk没有bin目录

```
yum install java-1.8.0-openjdk-devel.x86_64
```

nslookup www.baidu.com   查看域名后面的ip

查看内核版本  uname -srm  

查看系统版本   cat /etc/centos-release  

立即重启 reboot

```
swapoff -a
```

***which java\***  查找java的安装目录

sudo passwd root    忘记root密码

ls- lh 查看文件有多大

如果发现在vim中  按esc键没反应，看看是不是输入法是中文的

**解决linux问题  1看日志 2 看进程**

netstat -tunlp  显示所有的端口跟应用

echo 输出文件所在的位置

mkdir -p /mydata/redis/conf  创建多级目录

**top命令用于实时显示 process 的动态。**

搜索 

rpm -qa | grep jenkins     查看Jenkins是否还有残留

ps -qa | grep 端口号   查看这个端口号的使用情况  



rpm安装 

-i 安装 

rpm -i --force --nodeps 强制安装

-e 卸载 

-qa 查找安装包

```
rpm -qa |grep sql
```

```
which mysql  查找MySQL安装到哪里了
```

--force --nodeps强制安装  



 whereis MySQL 查看装到哪里了

top  类似用windos的资源管理器

free -m 查看linux内存的整体情况

free -h

crul  可以用来下载 通信 

wget用来下载  

tee指令会从标准输入设备读取数据，将其内容输出到标准输出设备，同时保存成文件。

如 

```bash
$ sudo tee /etc/docker/daemon.json <<-'EOF'
{
   "registry-mirrors": ["https://registry.docker-cn.com"]
}
EOF
```

把 这是配置个镜像加速地址 

读取数据输出到json格式的文件里

npm config set registry http://registry.npm.taobao.org/   替换npm的下载依赖

需要学习的知识点

**yun源的学习**

service --status-all | grep runing

查看正在运行的服务

日志文件   /var/log

在使用服务器 创建镜像的时候 不要选择 cnetos 8（阿里云的 因为他的yum 源实失效了   

user add  用户


chown -R sonar:sonar /home/sonarqube/sonarqube/ 给用户赋予权限

# 1 命令

## 内存

atop 它显示的是各种系统资源（CPU, memory, network, I/O, kernel）的综合，并且在高负载的情况下进行了彩色标注

yum install -y atop

cat /proc/meminfo

**atop**命令显示了每个进程的内存实时使用率。它提供了所有进程的常驻内存大小、程序总内存大小、共享库大小等的报告。列表可以水平及垂直滚动



## Wget

使用 wget 下载单个文件

以下的例子是从网络下载一个文件并保存在当前目录

在下载的过程中会显示进度条，包含（下载完成百分比，已经下载的字节，当前下载速度，剩余下载时间）

2、使用 wget -O 下载并以不同的文件名保存

我们可以使用参数-O来指定一个文件名：

3、使用 wget -c 断点续传

使用wget -c重新启动下载中断的文件:

对于我们下载大文件时突然由于网络等原因中断非常有帮助，我们可以继续接着下载而不是重新下载一个文件

4、使用 wget -b 后台下载

对于下载非常大的文件的时候，我们可以使用参数-b进行后台下载

tail -f wget -log

你可以使用以下命令来察看下载进读

5、使用 wget –spider 测试下载链接

当你打算进行定时下载，你应该在预定时间测试下载链接是否有效。我们可以增加–spider参数进行检查。

6、使用wget -p 下载 到指定目录

## Curl 

网络请求 进行测试

有的网址是自动跳转的。使用 `-L` 参数，curl 就会跳转到新的网址。

## --help

1、cd命令：这是一个非常基本，也是大家经常需要使用的命令，它用于切换当前目录，它的参数是要切换到的目录的路径，可以是绝对路径，也可以是相对路径。如：

cd /home 　 进入根目录下面的home目录
cd home 　 进入当前目录下的home目录
cd ..　　　 返回上一层目录
cd ../.. 　　 返回上两级目录
cd /　　　　　返回跟目录
cd - 　　　　返回上次所在的目录 

2、ls命令：这是一个非常有用的查看文件与目录的命令，list之意，它的参数非常多，下面就列出一些我常用的参数吧，如下：

ls 　　　查看目录中的文件 
ls -a　　列出全部的文件，连同隐藏文件（开头为.的文件）一起列出来
ls -l 　 显示文件和目录的详细资料 

## mkdir

命令：创建

mkdir dir1 创建一个叫做 'dir1' 的目录' 
mkdir dir1 dir2 同时创建两个目录 

-r  递归创建

touch 创建文件

## rm

命令：删除

rm -f file1　　 删除一个叫做 'file1' 的文件' 
rmdir dir1 　　删除一个叫做 'dir1' 的目录' （空目录才能删除）
rm -rf dir1 　　删除一个叫做 'dir1' 的目录并同时删除其内容 
rm -rf dir1 dir2　　 同时删除两个目录及它们的内容 

-r，删除文件夹使用 选项：-f，强制删除，

## mv

命令：该命令用于移动文件、目录或更名，move之意，它的常用参数如下：-f 如果目标文件已经存在，不会询问而直接覆盖

mv file1 file2  把文件file1重命名为file2 

mv file1 file2 dir  把文件file1、file2移动到目录dir中

## cp

命令：该命令用于复制文件，copy之意，它还可以把多个文件一次性地复制到一个目录下， 它的常用参数如下：

cp -a file1 file2 连同文件的所有特性把文件file1复制成文件file2
cp dir/* . 复制一个目录下的所有文件到当前工作目录 
cp -a /tmp/dir1 . 复制一个目录到当前工作目录 
cp -a dir1 dir2 复制一个目录 

-r参数 复制文件夹 

## find

find 路径 

 -name 参数  搜索以这个参数结尾的命令

-name filename //查找名为filename的文件。
-iname filename //与-name相同，查找名为filename的文件，但忽略大小写，即不区分大小写。
-type b/d/c/p/l/f //按照文件类型查找，
b - 块设备文件。
d - 目录。
c - 字符设备文件。
p - 管道文件。
l - 符号链接文件。
f - 普通文件。
s -socket文件

命令：find是一个基于查找的功能非常强大的命令

find / -name file1 从 '/' 开始进入根文件系统搜索文件和目录 
find / -user user1 搜索属于用户 'user1' 的文件和目录 
find /home/user1 -name \*.bin 在目录 '/ home/user1' 中搜索带有'.bin' 结尾的文件 
find /usr/bin -type f -atime +100 搜索在过去100天内未被使用过的执行文件 
find /usr/bin -type f -mtime -10 搜索在10天内被创建或者修改过的文件 

## ps

ps命令 功能：查看进程信息 语法：ps 

ps -ef | grep 进程名

命令：该命令用于将某个时间点的进程运行情况选取下来并输出，process之意，它的常用参数如下：

-A ：所有的进程均显示出来
-a ：不与terminal有关的所有进程
-u ：有效用户的相关进程
-x ：一般与a参数一起使用，可列出较完整的信息
-l ：较长，较详细地将PID的信息列出
其实我们只要记住ps一般使用的命令参数搭配即可，它们并不多，如下：

ps aux  查看系统所有的进程数据
ps ax  查看不与terminal有关的所有进程
ps -lA  查看系统所有的进程数据
ps axjf  查看连同一部分进程树状态

## kill命令

：该命令用于向某个工作（%jobnumber）或者是某个PID（数字）传送一个信号，它通常与ps和jobs命令一起使用，它的基本语法如下：

kill -signal PID
signal的常用参数如下：注：最前面的数字为信号的代号，使用时可以用代号代替相应的信号。

1：SIGHUP，启动被终止的进程
2：SIGINT，相当于输入ctrl+c，中断一个程序的进行
9：SIGKILL，强制中断一个进程的进行
15：SIGTERM，以正常的结束进程方式来终止进程
17：SIGSTOP，相当于输入ctrl+z，暂停一个进程的进行
例如：

\# 以正常的结束进程方式来终于第一个后台工作，可用jobs命令查看后台中的第一个工作进程
kill -SIGTERM %1
\# 重新改动进程ID为PID的进程，PID可用ps命令通过管道命令加上grep命令进行筛选获得
kill -SIGHUP PID

## tar命令

如何解压zip

yum -y install unzip

unzip   进行解压即可

**.tgz**

用tar -xzf  

**tar.xz**

用 tar -xf 来解压

**tar.gz**

该命令用于对文件进行打包，默认情况并不会压缩，如果指定了相应的参数，它还会调用相应的压缩程序（如gzip和bzip等）进行压缩和解压。它的常用参数如下：

1. 压缩：tar -jcv -f filename.tar.bz2 要被处理的文件或目录名称 
2. 查询：tar -jtv -f filename.tar.bz2 
3. 解压：tar -jxv -f filename.tar.bz2 -C 欲解压缩的目录 

4 tar -zxvf 被解压的文件 -C 要解压去的地方 -z表示使用gzip，可以省略 -C，可以省略，指定要解压去的地方，不写解压到当前目录

## chown

命令

 修改文件、文件夹所属用户、组 语法：chown [-R] [用户][:][用户组] 文件或文件夹

## **chmod**

命令：该命令用于改变文件的权限，一般的用法如下：

-R 文件夹和内容一起生效

chmod -R 777   chmod -R 777 意思就是将当前目录及目录下所有文件都给予777权限（所有权限）

## **查看文件内容** 

more命令 功能：查看文件，可以支持翻页查看 语法：more 参数 参数：被查看的文件路径 在查看过程中： 空格键翻页 q退出查看 

cat file1 从第一个字节开始正向查看文件的内容 
tac file1 从最后一行开始反向查看一个文件的内容 
more file1 查看一个长文件的内容 
less file1 类似于 'more' 命令，但是它允许在文件中和正向操作一样的反向操作 
head -2 file1 查看一个文件的前两行 

tail命令 功能：查看文件尾部内容 

tail -2 file1 查看一个文件的最后两行 
tail -f /var/log/messages 实时查看被添加到一个文件中的内容 

head功能：查看文件头部内容 语法：

head [-n] 参数 参数：被查看的文件 选项：-n，查看的行数



## **YUM 包** 

CentOS系统使用： 

yum 软件名称 

install 安装 remove 卸载 search 搜索 -y，自动确认





yum install package_name 下载并安装一个rpm包 
yum localinstall package_name.rpm 将安装一个rpm包，使用你自己的软件仓库为你解决所有依赖关系 
yum update package_name.rpm 更新当前系统中所有安装的rpm包 
yum update package_name 更新一个rpm包 
yum remove package_name 删除一个rpm包 
yum list 列出当前系统中安装的所有包 
yum search package_name 在rpm仓库中搜寻软件包 
yum clean packages 清理rpm缓存删除下载的包 
yum clean headers 删除所有头文件 
yum clean all 删除所有缓存的包和头文件 

## RPM



--force --nodeps   强制安装

## Systemctl

功能：控制系统服务的启动关闭等 语法：

systemctl start | stop | restart | disable | enable | status 服务名 

start启动， stop停止 status查看状态， disable关闭开机自启， enable开启开机自启 ，restart重启



## 防火墙

cd  mkdir touch    crul 网络连接  等常见的命令

防火墙

1.开启端口3306

firewall-cmd --zone=public --add-port=3306/tcp --permanent

2.重启防火墙

firewall-cmd --reload

3.查看已经开放的端口

firewall-cmd --list-ports

4 查看防火墙

 systemctl status firewalld

## vim

![](C:\Users\DELL\Desktop\linux\命令行模式.jpg)

命令模式下 

![](C:\Users\DELL\Desktop\linux\命令模式下的参数.jpg)

底线模式  ：set nu    显示行号 

## WC

wc命令 功能：统计 语法：

wc  文件路径

 选项，-c，统计bytes数量 选项，

-m，统计字符数量 选项，

-l，统计行数 选项

，-w，统计单词数量 参数，文件路径，被统计的文件，可作为内容输入端口

## greap

grep命令 功能：过滤关键字

grep [-n] 关键字 文件路径 选项-n

，可选，表示在结果中显示匹配的行的行号。 参数，关键字，必填，表示过滤的关键字，带有空格或其它特殊符号，建议使 用””将关键字包围起来 参数，文件路径，必填，表示要过滤内容的文件路径，可作为内容输入端口

## | 

写法：| 功能：将符号左边的结果，作为符号右边的输入 进行过滤

## echo

echo命令 功能：输出内容 语法：echo 参数 参数：被输出的内容

## iostat

iostat 用法

命令参数：

**-c：** 显示CPU使用情况
**-d：** 显示磁盘使用情况
**-N：** 显示[磁盘阵列](https://so.csdn.net/so/search?q=磁盘阵列&spm=1001.2101.3001.7020)(LVM) 信息
**-n：** 显示[NFS](https://so.csdn.net/so/search?q=NFS&spm=1001.2101.3001.7020) 使用情况
**-k：** 以 KB 为单位显示
**-m：** 以 M 为单位显示
**-t：** 报告每秒向终端读取和写入的字符数和CPU的信息
**-V：** 显示版本信息
**-x：** 显示详细信息
**-p：**[磁盘] 显示磁盘和分区的情况

如果istat不能用的话 

-bash: iostat: command not found

**yum install -y sysstat**

## 环境变量

 临时设置：export 变量名=变量值 永久设置： 针对用户，设置用户HOME目录内：.bashrc文件 针对全局，设置/etc/profile 

PATH变量 记录了执行程序的搜索路径 可以将自定义路径加入PATH内，实现自定义命令在任意地方均可执行的效果

 $符号 可以取出指定的环境变量的值 语法：$变量名 示例： echo $PATH，输出PATH环境变量的值 echo ${PATH}ABC，输出PATH环境变量的值以及ABC 如果变量名和其它内容混淆在一起，可以使用${}

## 日期

日期 语法：date [-d] [+格式化字符串]

 -d 按照给定的字符串显示日期，一般用于日期计算 格式化字符串：通过特定的字符串标记，来控制显示的日期格式 %Y 年%y 年份后两位数字 (00 .99) %m 月份 (01 .12) %d 日 (01 .31) %H 小时 (00 .23) %M 分钟 (00 .59) %S 秒 (00 .60) %s 自 1970-01-01 00:00:00 UTC 到现在的秒数

## 其他

df -h 查看磁盘容量

hostname 查看主机名 

free -h 查看内存容量

su 切换用户  sudo 一条普通用户的命令带有root的权限

env 查看系统环境变量 

# 