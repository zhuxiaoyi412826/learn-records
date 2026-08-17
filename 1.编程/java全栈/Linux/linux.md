学习三要素 在学习之前我们要知道这项技术是什么，为什么，怎么学。

不要为了学习而学习，学习的这个过程是枯燥无味的，你如果不知道自己的目的是什么很容易被带偏，坚持不下去半途而废

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/ln软连接.jpg)



https://oss-cn-shenzhen.aliyuncs.com/UTOOLS1742021346623.jpg

https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/UTOOLS1742021490604.jpg

https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/jdk.package.jpg

CnpCpTQaIGODaFfc1lzb61F2krpDTc

# 2 操作系统

发展 什么是Linux

Linux介绍  内核 发行版本

虚拟机网络设置  

云服务器的连接  

Xshell   Xftp

# 1 前言

我学习的主要课程是，尚硅谷嵌入式Linux之Ubuntu

https://www.bilibili.com/video/BV1JF4m1A7mN/?spm_id_from=333.1387.favlist.content.click&vd_source=8e232ecca082f1beea092de8718f15c6

为什么选择ubuntu，1Ubuntu的图形界面好，2Centos也停服了

推荐两个教程 

https://www.runoob.com/linux/linux-tutorial.html

https://www.w3cschool.cn/linux/

https://blog.csdn.net/as604049322/article/details/120446586?ops_request_misc=%257B%2522request%255Fid%2522%253A%252254215bdd3651060e91452003a21a521f%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=54215bdd3651060e91452003a21a521f&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~top_positive~default-1-120446586-null-null.142^v102^pc_search_result_base3&utm_term=Linux&spm=1018.2226.3001.4187

博客

教程是有侧重点的，不一定全面，遇到解决不了可以查询一下其他的

三个网站 帮助我们学习

1 豆包https://www.doubao.com/chat/1856257831091970

如何有不会的命令直接问他就好了

2 Linux命令手册查询大全  https://www.linuxcool.com/

3utools https://www.u-tools.cn/  安装Linux文档插件，不会的命令可以查询

# 2 环境准备

## 资源下载

我使用的是VMware+unbantu  

1 下载VMware 虚拟机

https://support.broadcom.com/group/ecx/productdownloads?subfamily=VMware Workstation Pro&freeDownloads=true

下载这个东西真不容易 

2 下载unbantu镜像文件

清华源

https://mirrors.tuna.tsinghua.edu.cn/ubuntu-releases/22.04/

后缀是    desktop-amd64.iso  4.4G大小

阿里源

https://developer.aliyun.com/mirror/ubuntu?spm=a2c6h.13651102.0.0.3e221b11rhacGu

## 安装

安装VMware  一步一步傻瓜操作  可以参考这个文档

https://blog.csdn.net/Mylilizhi/article/details/142969657

创建虚拟机也是傻瓜操作 ，参考这个文档

https://blog.csdn.net/xxjc2025/article/details/145669621

1 磁盘大小可以弄到50G 

2 网络链接选择NAT模式

![](C:\Users\DELL\Desktop\Linux\img\网络编辑.jpg)

安装完后可以下载中文

https://shurufa.sogou.com/linux 中文输入法

再次打开VMware时出现这种情况

![](C:\Users\DELL\Desktop\Linux\img\VMware.png)

解决办法 右键单机软件 使用系统管理员身份来运行

ctrl + alt 键 在虚拟机里就会显现你的鼠标键 

# 3 命令

Linux一起皆文件

cd / 移动到根目录   ls查看所有文件目录

Linux目录   

1 一切皆文件

bin 存储命令  (之前存储二进制文件) 

boot 核心镜像配置文件

cdrom  光盘光驱文件

dev  设备 CPU 磁盘 网络等

erv 环境配置

home 家  用户所使用的目录

lib  依赖

media 音频文件

mnt 临时存储的文件

opt 安装软件

proc 内存

root 管理员 

run 运行程序 

sbin 高级命令系统运行

snap 系统快照 

srv 系统运行存储文件  var 存储系统运行日志  tmp 存储临时文件

sys 操作系统系统文件

usr 用户  各个用户公用的

## **常用命令**

sudo 提升命令的权限

cd 移动目录   cd .. 移动到上一个目录   cd /移动到根目录   ./是当前目录 

~号代表家目录 home目录  tab键补齐     hostname 查看主机名  uname -a 查看系统命令

mkdir 创建目录    ls 查看目录 ls -al查看所有目录  pwd显示当前工作目录

touch  创建空文件    cp  复制文件   -- help帮助  history 查看历史命令

ps -aux 显示进程  kill -9 进程号杀死进程   cat 查看文件

dpkg -i  安装deb包  tar -zxvf 解压文件.gz       --help 帮助命令

vim 默认查看模式   i 插入模式  esc 命令模式    :wq 退出保存 ！强制执行

## 1 基础命令

### 3.1.1 防火墙

默认开放22号端口

 **NetworkManager***网络服务**

systemctl start I stop I restart I status  服务名      

​                  启动   停止    重启       查看

查看/usr/lib/systemd/system目录下的文件列表 每一个文件都对应一个服务

**disable 自动启动**

systemctl list-unit-files （功能描述：查看服务开机启动状态）

 systemctl disable service_name （功能描述：关掉指定服务的自动启动） 

systemctl enable service_name （功能描述：开启指定服务的自动启动）

**ufw 防火墙**

（1）查看防火墙状态  sudo systemctl status ufw 

（2）临时关闭防火墙  sudo systemctl stop ufw 

开机启动时关闭防火墙

（1）设置开机时启动防火墙  sudo systemctl enable ufw 

（2）设置开机时关闭防火墙  sudo systemctl disable ufw

（3）查看服务是否开机自启 sudo systemctl is-enabled ufw

 disabled  表示开机不自启 enabled 表示开机自启

### 3.1.2 包管理器

unbantu使用的是  APT（Advanced Packaging Tools）是 Debian 及其派生 Linux 的软件包管理器，可以自 动下载，配置，安装二进制或者源代码格式的软件包，因此简化了 Unix 系统上管理软件的 过程。

 APT 常用命令： 用法： apt [选项] 命令 命令行软件包管理器 apt 提供软件包搜索，管理和信息查询等功能。 

输入apt 可查看提升命令

 list - 根据名称列出软件包 

search - 搜索软件包描述 

show - 显示软件包细节

 install - 安装软件包 

reinstall - 重新安装软件包

 remove - 移除软件包 

autoremove - 卸载所有自动安装且不再使用的软件包

 update - 更新可用软件包列表

 upgrade - 通过 安装/升级 软件来更新系统 

full-upgrade - 通过 卸载/安装/升级 来更新系统

 edit-sources - 编辑软件源信息文件

 satisfy - 使系统满足依赖关系字符串

**安装 dpkg**

- i：安装软件包；
  -r：删除软件包；
  -P：删除软件包的同时删除其配置文件；
  -L：显示于软件包关联的文件；
  -l：显示已安装软件包列表；
  --unpack：解开软件包；
  -c：显示软件包内文件列表；
  --confiugre：配置软件包。

Debian Linux系统上安装、创建和管理软件包

```
sudo dpkg -i jdk-21_linux-x64_bin.deb
```

### 3.1.3其他

**关机重启**

reboot （功能描述：重启，等同于 shutdown -r now）

shutdown  -r      -now       -h           时间

​                  重启  立刻关机    关机    时间 

sync 将数据由内存同步到硬盘中 

（1）将数据由内存同步到硬盘中 sync 

（2）重启sudo reboot 

（3）终止 CPU 的所有活动  sudo halt

**主机名**

查看主机名 hostname 

修改主机名

sudo hostname new_hostname                   

修改主机名  hostnamectl  sudo hostnamectl hostname 

    查看系统命令
     uname -a
      uname -r
**帮助命令**  

man 查找帮手  

man man 全部手册 按q 退出

man ls  查找特定的 文档 

--help

## **2 常用命令**

### 文件目录

**ls**

ls 文件夹的目录  查看 文件里面的东西    ls /home/zxy  查看指定文件夹目录

选项 

 -a 全部的文件，连同隐藏档( 开头为 . 的文件) 一起列出来(常用) 

-l 长数据串列出，包含文件的属性与权限等等数据；

显示说明 每行列出的信息依次是：

文件类型与权限 链接数 文件属主 文件属组 文件大小用 byte 来表示 建立或最近修改的时间 名字

![](C:\Users\DELL\Desktop\Linux\img\ls查看.jpg)

**cd**

d 绝对路径 切换路径 

cd 相对路径 切换路径

 cd ~或者 cd 回到自己的家目录

 cd - 回到上一次所在目录 

cd .. 回到当前目录的上一级目录

 cd -P 跳转到实际物理路径，而非快捷方式路径

**mkdir** 

创建文件夹的命令

mkdir 
-m<目标属性>或--mode<目标属性>建立目录的同时设置目录的权限；
-p或--parents 若所要建立目录的上层目录目前尚未建立，则会一并建立上层目录；
--version 显示版本信息。

sudo mkdir -p 2/3/4

**touch** 

 touch  创建空文件

```
touch a.txt  #创建一个文件
touch {.classpath,README}  #创建多个
```

-a：或--time=atime或--time=access或--time=use  只更改存取时间；
-c：或--no-create  不建立任何文件；
-d：<时间日期> 使用指定的日期时间，而非现在的时间；
-f：此参数将忽略不予处理，仅负责解决BSD版本touch指令的兼容性问题；
-m：或--time=mtime或--time=modify  只更该变动时间；
-r：<参考文件或目录>  把指定文件或目录的日期时间，统统设成和参考文件或目录的日期时间相同；
-t：<日期时间>  使用指定的日期时间，而非现在的时间；
--help：在线帮助；
--version：显示版本信息

**cp**

cp  需要复制文件的目录/文件   复制到的目录   （../ 代表上级目录）~代表家目录·

-r 递归复制整个文件夹  

**cp: 无法以目录 '2/' 来覆盖非目录 '/home/zhuxiaoyi/1/2'**

当你在执行 `cp -r 2 1` 命令时遇到 `cp: 无法以目录 '2/' 来覆盖非目录 '/home/zhuxiaoyi/1/2'` 错误，这表明在目标目录 `1` 下已经存在一个名为 `2` 的非目录文件（可能是普通文件、链接文件等），而你尝试用一个目录去覆盖它，`cp` 命令不允许这样的操作。

**远程文件复制：scp**

scp 命令用于 Linux 之间复制文件和目录，scp是 secure copy 的缩写是linux系统下基于ssh登陆进行安全的远程文件拷贝命令。

scp 是加密的，[rcp](https://xiaoxiaoming.xyz/linux/linux-comm-rcp.html) 是不加密的，scp 是 rcp 的加强版。

-1：使用ssh协议版本1；
-2：使用ssh协议版本2；
-4：使用ipv4；
-6：使用ipv6；
-B：以批处理模式运行；
-C：使用压缩；
-F：指定ssh配置文件；
-i：identity_file 从指定文件中读取传输时使用的密钥文件（例如亚马逊云pem），此参数直接传递给ssh；
-l：指定宽带限制；
-o：指定使用的ssh选项；
-P：指定远程主机的端口号；
-p：保留文件的最后修改时间，最后访问时间和权限模式；
-q：不显示复制进度；
-r：以递归方式复制。

```
上传
scp -r /opt/soft/mongodb root@10.10.10.10:/opt/soft/scptest

下载
scp -r root@10.10.10.10:/opt/soft/mongodb /opt/soft/
scp -P 4588 remote@xiaoxiaoming.xyz:/usr/local/sin.sh /home/administrator
```

**rm** 

删除

（1）删除目录中的内容  rm xiyou/qujing/sunwukong.txt 

（2）递归删除目录中所有内容  rm -rf dssz/

选项 功能 -r 递归删除目录中所有内容

 -f 强制执行删除操作，而不提示用于进行确认。

 -v 显示指令的详细执行过程

rm -rf *. 删除所有此类型文件

**mv** 

（1）mv oldNameFile newNameFile （功能描述：重命名） 

（2）mv /temp/movefile /targetFolder （功能描述：移动文件）

###  查看文件

**cat**

-n 显示行号

**more** 

more 指令是一个基于 VI 编辑器的文本过滤器，它以全屏幕的方式按页显示文本文件的 内容。more 指令中内置了若干快捷键，详见操作说明。

空白键 (space) 代表向下翻一页；

 Enter 代表向下翻『一行』； 

q 代表立刻离开 more ，不再显示该文件内容。

 Ctrl+F 向下滚动一屏 

Ctrl+B 返回上一屏 = 输出当前行的行号 

f 输出文件名和当前行的行号

**lesss** 

操作 功能说明 空白键 向下翻动一页；

 [pagedown] 向下翻动一行

 [pageup] 向上翻动一行； /字串 向下搜寻『字串』的功能；

n：向下查找；

N：向上查找； ?字串 向上搜寻『字串』的功能；

**tail**

 输出文件

-n 输出行数 

-f 实时跟新

**输出重定向**

1 > 覆盖  

2 >> 追加   

 将test.txt的内容追加到README文件中 cat test.txt >> README

### vim

```
sudo apt install vim
```

有三种模式

![](C:\Users\DELL\Desktop\Linux\img\屏幕截图 2025-03-11 105105.jpg)

Vim 有多种模式，但最常用的是三种：命令模式、插入模式和底行模式，以下是它们之间的切换方法及操作：

**一般模式（默认模式）**

当你启动 Vim 后，默认进入的就是命令模式。在这个模式下，你可以执行各种导航、复制、粘贴、删除等操作，但不能直接输入文本。

- 移动光标

  ：

  - `h`：向左移动一个字符。
  - `j`：向下移动一行。
  - `k`：向上移动一行。
  - `l`：向右移动一个字符。
  - `gg`：移动到文件开头。
  - `G`：移动到文件末尾。
  - `数字G`：移动到指定行，例如 `10G` 移动到第 10 行。

- 复制和粘贴

  ：

  - `yy`：复制当前行。
  - `nyy`：复制当前行及下面的 `n - 1` 行，例如 `3yy` 复制当前行及下面 2 行。
  - `p`：在当前光标位置之后粘贴复制的内容。
  - `P`：在当前光标位置之前粘贴复制的内容。

- 删除

  ：

  - `dd`：删除当前行。
  - `ndd`：删除当前行及下面的 `n - 1` 行，例如 `3dd` 删除当前行及下面 2 行。

**插入模式**

在命令模式下，按下以下键可以进入插入模式，在该模式下你可以输入和编辑文本：

- `i`：在当前光标位置之前插入文本。
- `a`：在当前光标位置之后插入文本。
- `o`：在当前行的下一行插入新行并进入插入模式。
- `O`：在当前行的上一行插入新行并进入插入模式。

要从插入模式回到命令模式，按下 `Esc` 键即可。

**命令模式**

在命令模式下，按下 `:` 键进入底行模式，该模式主要用于执行一些文件操作、搜索替换等命令。

- 保存和退出

  ：

  - `:w`：保存文件。
  - `:q`：退出 Vim（如果文件未修改）。
  - `:wq`：保存文件并退出 Vim。
  - `:q!`：不保存文件，强制退出 Vim。
  -  : ! 强制
  -  :set nu 显示行号
  -  :set nonu 不显示行号

- 搜索和替换

  ：

  - `/关键词`：在文件中搜索指定的关键词，按下 `n` 查找下一个匹配项，按下 `N` 查找上一个匹配项。
  - `:%s/原内容/新内容/g`：将文件中所有的 “原内容” 替换为 “新内容”。例如 `:%s/hello/world/g` 将文件中所有的 `hello` 替换为 `world`。

**4. 退出 Vim**

在命令模式下，输入以下命令可以退出 Vim：

- `:wq`：保存文件并退出。
- `:q`：如果文件未修改，直接退出。
- `:q!`：不保存文件，强制退出。
- 

**data**

-d<时间字符串> 显示指定的“时间字符串”表示的时间，而非当前时间

 -s<日期时间> 设置系统日期时间

 显示时间

（1）date （功能描述：显示当前时间） 

（2）date +%Y （功能描述：显示当前年份）

 （3）date +%m （功能描述：显示当前月份） 

（4）date +%d （功能描述：显示当前是哪一天）

```
date +%y%m%d
```

 （5）date "+%Y-%m-%d %H:%M:%S" （功能描述：显示年月日时分秒）

data -s 设置系统时间

date -s "2017-06-19 20:52:18"

### 用户组

#### 用户

**adduser** 

cat /etc/passwd 查看用户

- -c<备注>：加上备注文字。备注文字会保存在passwd的备注栏位中；
  -d<登入目录>：指定用户登入时的启始目录；
  -D：变更预设值；
  -e<有效期限>：指定帐号的有效期限；
  -f<缓冲天数>：指定在密码过期后多少天即关闭该帐号；
  -g<群组>：指定用户所属的群组； 
  -G<群组>：指定用户所属的附加群组；
  -m：自动建立用户的登入目录；
  -M：不要自动建立用户的登入目录；
  -n：取消建立以用户名称为名的群组；
  -r：建立系统帐号；
  -s<shell>：指定用户登入后所使用的shell；
  -u<uid>：指定用户id。

```
把Tom用户设置users组
usermod -g users tom
把Tomcat添加到sys，root组中
usermod -G sys,root tomcat

usermod -g users tom   
usermod -c "hr tom" tom 增加注释
```

添加新用户  同时在home目录创建zxy目录

```
adduser zxy
```

**passwd**

设置修改密码

```
sudo passwd tangseng
```

**id** 

```
id zxy
```

查看用户名是否存在

cat /etc/passwd

查看创建了多少用户

**userdel**

删除用户

（1）userdel 用户名 （功能描述：删除用户但保存用户主目录） 

（2）userdel -r 用户名 （功能描述：用户和用户主目录，都删除）

**usermode** 

修改用户的组别

```
sudo usermod -aG sudo zhangzhong  #组别在前面 用户在后面
```

**su** 

切换用户

su 用户名称 （功能描述：切换用户，只能获得用户的执行权限，不能获得环境变量）

 su - 用户名称 （功能描述：切换到用户并获得该用户的环境变量及执行权限）

#### 组

 groupadd  增加用户组

```
sudo groupadd xitianqujing
```

groupdel 删除组

```
sudo groupdel xitianqujing
```

groupmod -n 新组名 老组名  修改组

```
groupmod -n xitian xitianqujing
```

usermod -g 组名 用户名  修改用户主组

![](C:\Users\DELL\Desktop\Linux\img\用户组别.jpg)

在 Linux 和 Unix 系统中，每个用户都有一个主组（primary group）和可能的多个附加 组（secondary groups 或 additional groups）. 用户的主组在用户创建时被指定，默认与用户名称相同，当用户创建一个新文件或目录 时，默认情况下，这些文件或目录会被分配给用户的主组。

```
sudo usermod -g zhuxiaoyi zxy
```

**groups**

查找用户所在的组

```
groups d
```

**cat /etc/group  查看用户组**

/etc/group 文件存储了用户和附加组的映射关系，每一行对应一个用户组，第三个冒号 后面是以该组作为附加组的用户列表，列表为空表示没有用户将其作为附加组。

![](C:\Users\DELL\Desktop\Linux\img\附加组.jpg)

usermod -aG 组名 用户名  指定用户加入附加组

```
usermod -aG gdm zxy
```

**deluser**

 用户名 组名  用户从组中删除

```
deluser zhubajie atguigu
```

**查看一个组里有多少个用户**

```
grep "^组名:" /etc/group | awk -F: '{split($4,users,","); print length(users)}'

### 文件权限
```

查看一个组里用户分别是谁

```
getent group sudo | cut -d: -f4 | tr ',' '\n'
```

### 权限

在 Linux 中第一个字符代表这个文件是目录、文件或链接文件等等 

![](C:\Users\DELL\Desktop\Linux\img\screenshot20250311.png)

- 代表文件 d 代表目录 l 链接文档(link file)

（2）第 1-3 位确定属主（该文件的所有者）拥有该文件的权限。---User 

（3）第 4-6 位确定属组（所有者的同组用户）拥有该文件的权限，---Group （

（4）第 7-9 位确定其他用户拥有该文件的权限 ---Other

[ r ]代表可读（read）: 可以读取，查看

  [ w ]代表可写（write）: 可以修改，但是不代表可以删除该文件，删除一个文件的 前提条件是对该文件所在的目录有写权限，才能删除该文件.

  [ x ]代表可执行（execute）:可以被系统执行

![](C:\Users\DELL\Desktop\Linux\img\权限.jpg)



**chmod**

![](C:\Users\DELL\Desktop\Linux\img\屏幕截图 2025-03-11 144915.jpg)



U：所有者 g：所有组 o：其他人 a：所有人（u、g、o 的总和）

1

chmod +x 文件名   所有者可以执行

chmod g+w   属组可以执行

2 

U：所有者 g：所有组 o：其他人 a：所有人（u、g、o 的总和）

 r=4 w=2 x=1 rwx=4+2+1=7

 改变权限

chmod ug+w,o-w a.txt b.txt  可以设置两个

**chown**

用来变更文件或目录的拥有者或所属群组个用户授权，使该用户变成指定文件的所有者或者改变文件所属的组。用户可以是用户或者是用户D，用户组可以是组名或组id。文件名可以使由空格分开的文件列表，在文件名中可以包含通配符。

 -r递归操作

chown [选项] [最终用户]:[组别]  [文件或目录] 

```
chown sunwukong houge.txt
```

改变所有者的时候也可以改变所有组    先是所有者在是组名

![](C:\Users\DELL\Desktop\Linux\img\qianrushi.jpg)

```
chown zhangzhong:qianrushi qianrushi
改变qianrushi目录的所有者，和所属组
```

![](C:\Users\DELL\Desktop\Linux\img\qianrushi1.jpg)

**chgrp**   

改变所属组

chgrp [最终用户组] [文件或目录]

```
sudo chgrp root houge.txt
```

### 管道命令

Linux的管道命令是’|’，通过它可以对数据进行连续处理，其示意图如下

常用来作为接收数据管道命令有： less,more,head,tail，而ls, cp, mv就不行。

```
wc - 统计字数
可以计算文件的Byte数、字数、或是列数，若不指定文件名称、或是所给予的文件名为"-"，则wc指令会从标准输入设备读取数据。
```

### tar

 gzip 文件 （功能描述：压缩文件，只能将文件压缩为*.gz 文件）

```
gzip -d hadoop.tar.gz -d 解压或 gunzip hadoop.tar.gz
```

 gunzip 文件.gz （功能描述：解压缩文件命令）

tar  打包

-c 产生.tar 打包文件

 -v 显示详细信息 

 -f 指定压缩后的文件名 

-z 打包同时压缩 有gzip属性的

-x 解包.tar 文件

-j有bz2属性的

```
tar -zcvf hadoop.tar.gz hadoop #打包
tar -zxvf hadoop.tar.gz # 解压
tar -jxvf hadoop.tar.bz2 -C /usr/  #解压到指定路径
```

tar -zxvf 文件 -c 解压路径

```
jar格式
压缩：jar -cvf [目标文件名].jar [原文件名/目录名]
解压：jar -xvf [原文件名].jar
注：如果是打包的是Java类库，并且该类库中存在主类，那么需要写一个META-INF/MANIFEST.MF配置文件，内容如下：
Manifest-Version: 1.0
Created-By: 1.6.0_27 (Sun Microsystems Inc.)
Main-class: the_name_of_the_main_class_should_be_put_here
然后用如下命令打包：
jar -cvfm [目标文件名].jar META-INF/MANIFEST.MF [原文件名/目录名]
这样以后就能用“java -jar [文件名].jar”命令直接运行主类中的public static void main方法了。
```

根据文件类型选择命令：

| 文件类型  | 解压命令               | 示例命令                            |
| --------- | ---------------------- | ----------------------------------- |
| `.tar.gz` | `tar -xzf file.tar.gz` | `tar -xzf mysql-8.4.4-linux.tar.gz` |
| `.gz`     | `gzip -d file.gz`      | `gzip -d data.gz`                   |
| `.tar`    | `tar -xf file.tar`     | `tar -xf backup.tar`                |

**磁盘**  

df -h  查看磁盘使用情况 

du 查看文件和目录占用的磁盘空间

-a 显示当前目录所有及子目录下文件的大型

### 进程

```
1.查看用户最近登录情况
last
lastlog
2.查看硬盘使用情况
df
3.查看文件大小
du
4.查看内存使用情况
free
5.查看文件系统
/proc
6.查看日志
ls /var/log/
7.查看系统报错日志
tail /var/log/messages
8.查看进程
top
9.结束进程
kill 1234
kill -9 4333

```

ps 只可以查看当前窗口的命令

ps -aux  显示所有进程

-a 选择所有进程 

-u 显示所有用户的所有进程

 -x 显示没有终端的进程

ps -ef   查看子父进程之间的关系

UID：用户 ID  PID：

进程 ID  PPID：

父进程 ID



**kill 杀死进程**

-9 进程号  

killall 进程名   

**磁盘**

free -m 查看总体的内存

top 查看系统的状态

top -d n  每隔n秒刷新一次

top -i 不显示闲置进程或者僵死进程

操作 

功能 P 以 CPU 使用率排序，

默认就是此项 M 以内存的使用率排序

 N 以 PID 排序

 q 退出 top

执行上述命令后，可以按 P、M、N 对查询出的进程结果进行排序。

 **netstat** 端口号

netstat  -p | grep 端口号

-n 拒绝显示别名，能显示数字的全部转化成数字

 -l 仅列出有在 listen（监听）的服务状态 

-p 表示显示哪个进程在调用

```
netstat -nlp | grep 12345   
```

检测端口号是否被占用 

crontab  系统定时服务

```
sudo systemctl status crontab 
```

 查看crontab 服务是否被启动

-e 编辑 crontab 定时任务

 -l 查询 crontab 任务

 -r 删除当前用户所有的 crontab 任务

 -u 指定用户创建任务

### 其他

**echo**

输出内容到控制台

echo [选项] [输出内容]  选项：

  -e：支持反斜线控制的字符转换 控制字符 作用 

\\ 输出\本身

 \n 换行符

 \t 制表符，也就是 Tab 键

**输出重定向**

1 > 输出重定向>>追加

l为内容

（1）ls -l > 文件 （功能描述：列表的内容写入文件 a.txt 中（覆盖写））

 （2）ls -al >> 文件 （功能描述：列表的内容追加到文件 aa.txt 的末尾） 

（3）cat 文件 l > 文件 2 （功能描述：将文件 1 的内容覆盖到文件 2） 

（4）echo “内容” >> 文件

**ln** 

ln命令 用来为文件创建链接，链接类型分为硬链接和符号链接两种，默认的链接类型是硬链接。如果要创建符号链接必须使用"-s"选项。

-s创建软连接  

ln -s [原文件或目录] [软链接名]  创建完成之后软连接名在前面

```
ln -s 1.txt 1111
```

![](C:\Users\DELL\Desktop\Linux\img\ln软连接.jpg)

删除软链接： rm -rf 软链接名，而不是 rm -rf 软链接名/ 

```
rm -rf 1111
```

**history**

 查看历史命令

history n  查询n条命令   history 10  查询最后几条命令

!n  执行第n条命令   !要用英文状态下 

!281

!10s 执行前10s的命令

**find**

 搜索范围 选项

-name  按照文件名查询

find ./ -name "*.txt"       在当前目录下搜索

-user  指定用户名查询

find ./ -user atguigu

-size  按照文件大小查询

 find ./ -size +200c

b —— 块（512 字节）

 c —— 字节

 w —— 字（2 字节）

 k —— 千字节

 M —— 兆字节 

G —— 吉字节

**which** 查找安装的目录

```
python@ubuntu:/var/lib/mlocate$ which locate
/usr/bin/locate
```

**grep** 过滤

管道符，“|”，表示将前一个命令的处理结果输出传递给后面的命令处理

压缩

**wget**

测试连接   wget --spider URL

下载  

-c 断点续传

```
wget -c http://www.jsdig.com/testfile.zip
```

-O 下载以不同的名字保存

```
wget -O wordpress.zip http://www.jsdig.com/download.aspx?id=1080
```

-b 后台下载

```
wget -b http://www.jsdig.com/testfile.zip
tail -f wget-log
```

```
下载多个文件

wget -i filelist.txt
首先，保存一份下载链接文件：

cat > filelist.txt
url1
url2
url3
url4
```



# 4 案例

## 题目1

1.在opt目录下创建嵌入式目录，用于安装嵌入式框架。所有者为嵌入式部门的领导张总，所属组为嵌入式部门，张总有全部的权限，部门其他开发有读和执行的权限，其他人只有读的权限。
	    (1) 创建张总账号 密码123456 赋予sudo权限

```
	sudo adduser zhangzhong
	sudo usermod -aG sudo zhangzhong
```

(2) 创建嵌入式组

```
	sudo addgroup qianrushi
```

(3) 创建小张账号 密码123456 添加到嵌入式组中

```
sudo adduser xiaozhang
sudo usermod -aG qianrushi xiaozhang
```

(4) 在opt目录下创建嵌入式工作目录 修改对应的所有者和所属组  张总:嵌入式

```
sudo mkdir qianrushi
sudo chown zhangzhong:qianrushi qianrushi
```

(5) 目录的权限比较特殊  读和执行的权限必须都有才能进入目录看有哪些文件   嵌入式目录权限等级 755 

```
sudo chmod 755 qianrushi/
```

​	(6) 修改工作文件所有者和所属组  张总:嵌入式   只有root权限才能去修改文件的所有者和所属组  即使是文件的所有者也不能修改

```
sudo chown zhangzhong:qianrushi qianrushi.txt 
```

​	(7) 对应目录下的工作文件  754权限 

```
chmod 754 qianrushi.txt
```

2.在opt目录下创建bigdata目录，用于大数据框架的安装。所有者为大数据部门的王总，所属组为bigdata部门，王总有全部的权限，部门其他人有读写执行权限，其他人没有权限。
	(1) 创建王总账号 密码123456 赋予sudo权限  sudo adduser wangzong
		sudo usermod -aG sudo wangzong
	(2) 创建小王账号 密码123456 sudo adduser xiaowang
	(3) 创建bigdata部门 sudo groupadd bigdata
	(4) 添加小王到bigdata部门 sudo usermod -aG bigdata xiaowang
	(5) 创建大数据部门工作目录  sudo mkdir bigdata
	(6) 修改大数据部门工作目录的所有者和所属组  sudo chown wangzong:bigdata bigdata/
	(7) 修改工作目录为对应的权限  chmod 770 bigdata
	(8) 创建大数据部门可执行的文件  vim bigdata.txt
	(9) 修改文件所有者和所属组  sudo chown wangzong:bigdata bigdata.txt
	(10) 修改文件为对应的执行权限 sudo chmod 770 bigdata.txt

3.使用root用户在opt目录下创建mysql文件夹，用于安装mysql，赋予其他用户读的权限。
4.使用root用户在opt目录下创建redis文件夹，用于安装使用redis，赋予其他用户读和执行的权限。

## 题目2

下载最新的flume框架安装包tar.gz完成解压缩，放置到/opt/module目录下，查找超过1M的jar包有哪些。
	(1) 下载对应的安装包https://mirrors.tuna.tsinghua.edu.cn/apache/flume/1.11.0/
	(2) tar -zxvf apache-flume-1.11.0-bin.tar.gz apache-flume-1.11.0-bin ./
	(3) find ./ -name  "*.jar" -size +1M
	或者使用 find -size +1M | grep jar
./lib/asynchbase-1.8.2.jar
./lib/netty-3.9.4.Final.jar
./lib/scala-library-2.13.9.jar
./lib/scala-reflect-2.13.3.jar
./lib/kafka_2.13-2.7.2.jar
./lib/jackson-databind-2.13.2.1.jar
./lib/zstd-jni-1.4.5-6.jar
./lib/snappy-java-1.1.8.4.jar
./lib/derby-10.14.2.0.jar
./lib/zookeeper-3.6.2.jar
./lib/tomcat-embed-core-8.5.46.jar
./lib/kafka-clients-2.7.2.jar
./lib/log4j-core-2.18.0.jar
./lib/guava-11.0.2.jar
./lib/curator-client-5.1.0.jar
./tools/flume-ng-log4jappender-1.11.0-jar-with-dependencies.jar

## 题目3

**题目：基于目录继承与特殊权限的企业级权限管理**

**场景描述**：
某企业开发部门需要创建一个项目目录 `/project/develop`，要求：

1. 目录属主为 `devuser`，属组为 `developers`。

2. 所有新创建的文件 / 目录自动继承 `developers` 组权限。

3. ```
   developers 
   ```

   组内成员：

   - 对目录有读写执行权限。
   - 只能删除自己创建的文件，无法删除他人文件。

4. 特定用户

   ```
   manager
   ```

   需额外拥有以下权限：

   - 可修改目录内所有文件（包括他人创建的文件）。
   - 可创建、删除子目录。

5. 其他用户无任何权限。

6. 目录内的 `scripts` 子目录仅允许用户 `admin` 执行 `.sh` 脚本。

**答案与步骤解析**

**1. 创建用户、组及目录**

bash

```bash
sudo useradd devuser
sudo useradd manager
sudo useradd admin
sudo groupadd developers
sudo usermod -aG developers devuser
sudo usermod -aG developers manager
sudo mkdir -p /project/develop/scripts
sudo chown devuser:developers /project/develop
```

**2. 配置基本权限与特殊属性**

bash

```bash
# 设置目录基本权限：rwxrwx--- + SGID
sudo chmod 2770 /project/develop
# 确保子目录继承组权限
sudo chmod g+s /project/develop
# 设置粘滞位防止非属主删除文件（需结合ACL）
sudo chmod +t /project/develop
```

**3. 配置 ACL 权限（核心部分）**

bash

```bash
# 为developers组配置默认继承权限
sudo setfacl -m d:g:developers:rwx /project/develop
# 限制developers组成员只能删除自己的文件（粘滞位生效）
sudo setfacl -m default:o::--- /project/develop
# 为manager用户添加额外权限
sudo setfacl -m u:manager:rwx /project/develop
# 为scripts子目录配置admin用户权限
sudo setfacl -m u:admin:rx /project/develop/scripts
sudo setfacl -m d:u:admin:rx /project/develop/scripts  # 子目录继承
sudo setfacl -m u:admin:rx /.sh /project/develop/scripts  # 仅允许.sh文件执行
```

**4. 验证配置**

bash

```bash
# 检查目录权限
ls -ld /project/develop
getfacl /project/develop
getfacl /project/develop/scripts

# 测试1：developers组成员创建文件
sudo -u devuser touch /project/develop/file1.txt
sudo -u manager touch /project/develop/file2.txt

# 测试2：权限继承验证
sudo -u devuser mkdir /project/develop/subdir
ls -ld /project/develop/subdir  # 应属组为developers

# 测试3：删除权限验证
sudo -u devuser rm /project/develop/file2.txt  # 应失败（只能删除自己的文件）
sudo -u manager rm /project/develop/file2.txt  # 应成功（manager有额外权限）

# 测试4：scripts目录权限
sudo -u admin ls /project/develop/scripts
sudo -u admin touch /project/develop/scripts/test.sh
sudo -u admin chmod +x /project/develop/scripts/test.sh
sudo -u admin ./project/develop/scripts/test.sh  # 应允许执行
sudo -u admin rm /project/develop/scripts/test.sh  # 应失败（无写权限）
```

**关键技术点解析**

1. **SGID（2770）**：
   确保新创建的文件 / 目录自动继承父目录的属组（`developers`）。
2. **粘滞位（+t）**：
   结合 ACL，防止非属主用户删除他人文件（`manager` 用户因拥有完全权限可删除）。
3. **ACL 的灵活运用**：
   - `d:g:developers:rwx`：设置默认继承权限，确保子目录和新文件自动应用。
   - `u:manager:rwx`：为 `manager` 用户添加额外权限，突破粘滞位限制。
   - `u:admin:rx` + 文件类型过滤：仅允许 `admin` 用户执行 `.sh` 脚本。
4. **权限继承验证**：
   通过 `getfacl` 命令查看 ACL 规则，通过不同用户身份执行操作验证权限逻辑。

**扩展思考**

- **如何进一步限制 `manager` 用户的权限**：
  可通过 `sudo` 配置或 SELinux 策略，仅允许其在特定目录执行操作。
- **审计与监控**：
  结合 `auditd` 记录权限变更和文件删除操作，增强安全性

## 题目4

**SELinux 与 ACL 结合的容器权限隔离**

**要求**：

1. 创建目录 `/var/lib/containers`，属主为 `container_user`，属组为 `docker`。
2. 配置该目录允许 `docker` 服务读写，但限制其他用户（包括 root）仅能执行容器启动命令。
3. 使用 SELinux 确保容器文件无法被外部修改，且日志文件自动归类到 `container_log_t` 上下文。
4. 所有新创建的容器配置文件自动继承目录权限，且仅允许 `container_user` 管理。

**答案**：

bash

```bash
# 步骤1：创建用户、组和目录
sudo useradd -s /sbin/nologin container_user
sudo groupadd docker
sudo usermod -aG docker container_user
sudo mkdir -p /var/lib/containers/{configs,logs}
sudo chown -R container_user:docker /var/lib/containers

# 步骤2：配置权限与SELinux
sudo chmod -R 2770 /var/lib/containers
sudo chcon -R -t container_file_t /var/lib/containers  # 设置SELinux上下文
sudo semanage fcontext -a -t container_file_t "/var/lib/containers(/.*+)?"
sudo restorecon -Rv /var/lib/containers

# 步骤3：配置ACL与SELinux布尔值
sudo setfacl -m d:u:container_user:rwx /var/lib/containers/configs
sudo setfacl -m d:u:container_user:rwX /var/lib/containers/logs
sudo setsebool -P container_manage_cgroup 1  # 允许容器管理cgroup

# 步骤4：验证
ls -lZd /var/lib/containers
sudo -u container_user touch /var/lib/containers/configs/nginx.conf  # 允许
sudo -u root touch /var/lib/containers/logs/error.log  # 拒绝（SELinux阻止）
```

## **题目5**

**NFS 共享的跨主机权限管理**

**要求**：

1. 在 Server A 上创建共享目录 `/exports/nfs_share`，属主为 `nfs_user`，属组为 `nfs_group`。
2. 配置 NFS 共享，允许 Client B（IP: 192.168.1.100）以读写方式挂载，并强制将用户映射为 `nfs_user`。
3. 在 Client B 上挂载该目录，要求：
   - 普通用户 `client_user` 能读写文件，但无法删除他人文件。
   - 管理员用户 `client_admin` 拥有完全权限。
4. 确保跨主机操作时，文件权限与本地一致。

**答案**：

**Server A 配置**

bash

```bash
# 步骤1：创建用户、组和共享目录
sudo useradd -u 1001 nfs_user
sudo groupadd -g 1001 nfs_group
sudo mkdir /exports/nfs_share
sudo chown -R nfs_user:nfs_group /exports/nfs_share
sudo chmod -R 2770 /exports/nfs_share

# 步骤2：配置NFS共享
sudo tee -a /etc/exports <<EOF
/exports/nfs_share 192.168.1.100(rw,sync,no_root_squash,all_squash,anonuid=1001,anongid=1001)
EOF
sudo exportfs -ra

# 步骤3：设置SELinux（若启用）
sudo semanage fcontext -a -t nfs_export_t "/exports/nfs_share(/.*+)?"
sudo restorecon -Rv /exports/nfs_share
```

**Client B 配置**

bash

```bash
# 步骤1：创建本地用户并挂载
sudo useradd -u 1001 client_user
sudo useradd client_admin
sudo mount -t nfs 192.168.1.100:/exports/nfs_share /mnt/nfs

# 步骤2：配置本地权限
sudo setfacl -m u:client_admin:rwx /mnt/nfs
sudo setfacl -m default:o::--- /mnt/nfs
sudo chmod +t /mnt/nfs  # 粘滞位防止非属主删除

# 验证
sudo -u client_user touch /mnt/nfs/file1.txt
sudo -u client_admin rm /mnt/nfs/file1.txt  # 允许
sudo -u client_user rm /mnt/nfs/file1.txt  # 拒绝（非属主）
```

## **题目 6**

**企业级文件服务器的多级权限控制**

**要求**：

1. 创建

   ```
   /data/files
   ```

   目录，结构如下：

   plaintext

   ```plaintext
   /data/files/
   ├── public/        # 所有员工可读
   ├── department/
   │   ├── finance/   # 财务组读写，其他部门只读
   │   └── it/        # IT组读写，其他部门无权限
   └── secret/        # 仅高管组（executives）读写
   ```

2. 配置目录继承规则，确保：

   - 新创建的文件自动继承父目录权限。
   - `public` 目录内的文件允许所有员工下载，但禁止修改。
   - `secret` 目录内的文件删除时需记录审计日志。

3. 使用特殊权限和 ACL 实现以下约束：

   - 财务组用户可修改 `finance` 目录内文件，但无法删除。
   - IT 组用户可删除 `it` 目录内文件，但无法修改他人文件。

**答案**：

bash

```bash
# 步骤1：创建用户、组和目录结构
sudo groupadd employees
sudo groupadd finance
sudo groupadd it
sudo groupadd executives
sudo useradd -G employees user1  # 普通员工
sudo useradd -G finance,employees user2  # 财务员工
sudo useradd -G it,employees user3  # IT员工
sudo useradd -G executives user4  # 高管
sudo mkdir -p /data/files/{public,department/{finance,it},secret}

# 步骤2：配置公共目录权限
sudo chown -R root:employees /data/files
sudo chmod -R 750 /data/files
sudo chmod -R o+r /data/files/public  # 其他用户可读
sudo chattr +i /data/files/public  # 防止修改

# 步骤3：配置部门目录权限
# Finance目录
sudo chown -R root:finance /data/files/department/finance
sudo chmod -R 2770 /data/files/department/finance
sudo setfacl -m u:finance:rwX /data/files/department/finance
sudo setfacl -m d:u:finance:rwX /data/files/department/finance
sudo setfacl -m u::rwx /data/files/department/finance  # root完全控制

# IT目录
sudo chown -R root:it /data/files/department/it
sudo chmod -R 2770 /data/files/department/it
sudo setfacl -m u:it:rwx /data/files/department/it
sudo setfacl -m d:u:it:rwx /data/files/department/it
sudo chmod +t /data/files/department/it  # 粘滞位

# Secret目录
sudo chown -R root:executives /data/files/secret
sudo chmod -R 700 /data/files/secret
sudo chattr +a /data/files/secret  # 仅允许追加内容

# 步骤4：开启审计
sudo auditctl -w /data/files/secret -p del  # 监控删除操作
sudo service auditd restart

# 验证
sudo -u user2 touch /data/files/department/finance/report.xlsx  # 允许
sudo -u user2 rm /data/files/department/finance/report.xlsx  # 拒绝（无删除权限）
sudo -u user3 rm /data/files/department/it/file.txt  # 允许（属主或IT组）
sudo -u user4 echo "confidential" >> /data/files/secret/plan.txt  # 允许
sudo -u user4 rm /data/files/secret/plan.txt  # 拒绝（chattr +a）
```

**关键技术点总结**

1. **SELinux 与权限的结合**：
   通过 `chcon` 和 `semanage` 调整文件上下文，结合布尔值实现容器隔离。
2. **NFS 用户映射**：
   `all_squash` 和 `anonuid` 确保跨主机权限一致性，避免权限混乱。
3. **多级目录继承与特殊属性**：
   - `SGID`（2770）保证组权限继承。
   - `chattr +i` 防止公共目录被修改。
   - `chattr +a` 限制秘密目录仅能追加内容。
4. **审计与监控**：
   使用 `auditd` 跟踪敏感操作（如秘密文件删除），增强安全性。

这些题目需要综合运用 Linux 权限管理的高阶技术，实际操作时需根据环境调整参数，并通过测试验证配置的正确性。

## 题目7

在当前目录下有一系列以 `.txt` 结尾的文件，文件名格式为 `file_1.txt`、`file_2.txt` 等。需要将这些文件重命名为 `new_file_1.txt`、`new_file_2.txt` 等，并且将文件名中的数字部分补齐到 3 位，例如 `new_file_001.txt`、`new_file_002.txt`。

**答案**

bash

```bash
for file in file_*.txt; do
    num=$(echo $file | grep -oP '\d+' | xargs printf "%03d")
    new_name="new_file_$num.txt"
    mv "$file" "$new_name"
done
```

**解释**

- `for file in file_*.txt; do ... done`：遍历当前目录下所有以 `file_` 开头、以 `.txt` 结尾的文件。
- `num=$(echo $file | grep -oP '\d+' | xargs printf "%03d")`：从文件名中提取数字部分，并使用 `printf "%03d"` 将其补齐到 3 位。
- `new_name="new_file_$num.txt"`：生成新的文件名。
- `mv "$file" "$new_name"`：将原文件重命名为新文件名。

## 题目 8

在当前目录及其子目录下查找所有以 `.py` 结尾的 Python 文件，并统计这些文件的总行数。

**答案**

bash

```bash
find . -name "*.py" -type f -exec cat {} + | wc -l
```

- `find . -name "*.py" -type f`：在当前目录（`.`）及其子目录下查找所有以 `.py` 结尾的普通文件（`-type f`）。
- `-exec cat {} +`：对查找到的每个文件执行 `cat` 命令，将文件内容输出。
- `wc -l`：统计输出内容的行数。

## 题目 9

有一个日志文件 `access.log`，每行记录的格式为 `IP 地址 - 用户标识 [日期时间] "请求方法 URL 协议" 状态码 响应字节数`。需要筛选出所有状态码为 404 的记录，并统计不同 IP 地址出现的次数，按出现次数从高到低排序。

**答案**

bash

```bash
grep ' "404" ' access.log | awk '{print $1}' | sort | uniq -c | sort -nr
```

- `grep ' "404" ' access.log`：从 `access.log` 文件中筛选出包含 `"404"` 的行。
- `awk '{print $1}'`：提取每行的第一个字段，即 IP 地址。
- `sort`：对 IP 地址进行排序。
- `uniq -c`：统计每个 IP 地址出现的次数。
- `sort -nr`：按出现次数从高到低排序。

## 难题 10

使用 `top` 命令监控系统资源，每隔 5 秒记录一次 CPU 使用率，持续记录 1 分钟，最后计算这 1 分钟内的平均 CPU 使用率。

**答案**

```bash
total=0
count=0
for i in {1..12}; do
    cpu_usage=$(top -bn1 | grep "Cpu(s)" | awk '{print $2 + $4}')
    total=$(echo "$total + $cpu_usage" | bc)
    count=$((count + 1))
    sleep 5
done
average=$(echo "scale=2; $total / $count" | bc)
echo "Average CPU usage: $average%"
```

**解释**

- `for i in {1..12}; do ... done`：循环 12 次，每次间隔 5 秒，总共持续 1 分钟。
- `top -bn1 | grep "Cpu(s)" | awk '{print $2 + $4}'`：使用 `top` 命令获取系统的 CPU 使用率信息，提取用户态和内核态的 CPU 使用率之和。
- `total=$(echo "$total + $cpu_usage" | bc)`：累加每次记录的 CPU 使用率。
- `count=$((count + 1))`：记录记录的次数。
- `sleep 5`：暂停 5 秒。
- `average=$(echo "scale=2; $total / $count" | bc)`：计算平均 CPU 使用率，保留两位小数。
- `echo "Average CPU usage: $average%"`：输出平均 CPU 使用率。



# **5 Linux 常用快捷键**

## **一、终端操作快捷键**

### 1. **基础操作**

- `Ctrl + C`：中断当前命令

- `Ctrl + D`：退出终端或结束输入（等同于 `exit`）

- `Ctrl + L`：清屏（等同于 `clear` 命令）reset 

- `Ctrl + Z`：挂起当前任务（可通过 `fg` 恢复）

- ctrl +u 清除当前的命令

- `Tab`：自动补全文件名或命令

- 上下键 查找执行过的命令

  

### 2. **命令行编辑**

- `Ctrl + A`：光标移至行首
- `Ctrl + E`：光标移至行尾
- `Ctrl + K`：删除从光标到行尾的内容
- `Ctrl + U`：删除整行内容
- `Ctrl + W`：删除光标前的一个单词
- `Ctrl + Y`：粘贴之前删除的内容（需配合 `Ctrl + K` 或 `Ctrl + U`）
- `↑`/`↓`：查看历史命令

## **二、系统控制快捷键**

### 1. **窗口管理**

- `Alt + F4`：关闭当前窗口
- `Alt + Tab`：切换应用程序
- `Alt + Shift + Tab`：反向切换应用程序
- `Super键（Win键） + D`：显示 / 隐藏桌面
- `Super键 + 数字键`：快速启动任务栏第 N 个应用（如 `Super + 1`）

### 2. **终端窗口操作**

- `Ctrl + Shift + T`：新建终端标签页
- `Ctrl + Shift + W`：关闭当前标签页
- `Ctrl + Shift + N`：打开新终端窗口
- `Alt + Page Up`/`Alt + Page Down`：切换终端标签页

## **三、文本编辑快捷键**

### 1. **nano 编辑器**

- `Ctrl + O`：保存文件
- `Ctrl + X`：退出编辑器
- `Ctrl + W`：搜索文本
- `Ctrl + K`：剪切当前行
- `Ctrl + U`：粘贴剪切内容

### 2. **vim 编辑器**



- `i`：进入插入模式
- `Esc`：退出插入模式
- `:wq`：保存并退出
- `:q!`：强制退出不保存
- `dd`：删除当前行
- `p`：粘贴已复制内容

### 3.move查看文件

空白键 (space) 代表向下翻一页；

 Enter 代表向下翻『一行』； 

q 代表立刻离开 more ，不再显示该文件内容。

 Ctrl+F 向下滚动一屏 

Ctrl+B 返回上一屏 = 输出当前行的行号 

f 输出文件名和当前行的行号

## **四、其他实用快捷键**

### 1. **截屏操作**

- `PrintScreen`：截取整个屏幕
- `Alt + PrintScreen`：截取当前窗口
- `Shift + PrintScreen`：手动选择截取区域

### 2. **文件管理器（如 Nautilus）**

- `Ctrl + N`：打开新窗口
- `Ctrl + W`：关闭当前窗口
- `F2`：重命名文件 / 文件夹

### 3. **系统设置**

- `Super键 + I`：打开系统设置
- `Ctrl + Alt + T`：快速打开终端

## **五、自定义快捷键**

1. 打开 **系统设置 → 键盘 → 快捷键**
2. 选择需修改的功能（如 “启动终端”），点击右侧绑定，输入新组合键。

通过以上快捷键，可大幅提升 Linux 操作效率。具体工具的详细快捷键可查阅官方文档（如 `man bash`）

# 6 总结

学习 学习一段内容就要总计一段内容，不要想着把这拖到明天