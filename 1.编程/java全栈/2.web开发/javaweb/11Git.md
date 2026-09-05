**1 下载git**

https://git-scm.com/downloads/win

![Git下载.jpg](http://yanxuan.nosdn.127.net/c86012f2a3622031d7ce34ee4655ee36.jpg)

**2  配置用户名和邮箱**
打开命令行（如 Windows 的命令提示符、Git Bash，或 macOS/Linux 的终端）

```
1 全局配置（所有仓库生效）：
bash
git config --global user.name "Your Name"  
git config --global user.email "your_email@example.com"  
2 若只想针对当前仓库配置（去掉 --global）：
bash
git config user.name "Your Name"  
git config user.email "your_email@example.com" 
3 检查配置是否成功
输入以下命令查看所有配置信息：
bash
git config --list  
若想单独查看某一项（如用户名）：
bash
git config user.name  
```

**3 设置公钥和私钥**

```
ssh-keygen -t rsa -C 你的邮箱
一直按回车如果有y或n输入yes
```

![Git生成公钥和私钥.png](http://yanxuan.nosdn.127.net/a47e8d36af444102a351e8c5d212cf08.png)

```
 ls -al ~/.ssh
可以查看你配置的公钥
```

![公钥私钥位置.png](http://yanxuan.nosdn.127.net/1ec3c27c3641ffcf77824fcec1934f32.png)

**4 在gitee上配置公钥** 

![gitee上添加公钥.jpg](http://yanxuan.nosdn.127.net/fa826173f900af370870a10582603713.jpg)

**5 提交代码到远程仓库**

1. 选择+创建仓库 输入仓库名，下方有仓库地址保存一下，

![git上创建仓库.png](http://yanxuan.nosdn.127.net/58c26475842aeb6a78f546b6096232a4.png)

2. 本地仓库需要先初始化一下

```
git init
```

![git初始化.jpg](http://yanxuan.nosdn.127.net/f9575e02812eb0b53849be4f58c09d41.jpg)

3. git add .

`git add .` 命令的作用是把当前工作目录下所有修改、新增以及删除的文件添加到暂存区。暂存区是 Git 里的一个中间区域，在把修改提交到本地仓库之前，你可以先将文件放到这里。

```
方法一 git add . 当前目录下
git add -A 和 git add --all 是等价的，它们能将工作区里所有新创建、修改和删除的文件添加到暂存区，不管文件处于哪个目录
```

4. git commit -m "第一次提交"

   初始提交  " " 之间可以写修改了什么内容

   ```
   当你提交之后出现如下代码
   $ git commit -m "1"
   On branch master
   Initial commit
   nothing to commit (create/copy files and use "git add" to track)  
   这是没有文件添加
   1 file changed, 1 insertion(+)
    create mode 100644 javaweb/b.html 
    显示这样才是成功 有一个文件被修改了
   ```

5. git remote add origin https://gitee.com/zhuxiaoyizxy/mall.git  

   origin是别名 连接仓库地址   后面的代码是你仓库的地址，需要现在gitee上创建仓库

6. 推送

   ```
   git push -u origin master 
   将本地仓库推送到gitee上 配置正确的SSH秘钥 能登录你的账号
    git push -u origin master --force 
    强制推送你的仓到远程
   git commit -m "Initial commit on master"
   git commit -m "Initial commit on master"
   git push -u origin master
   game-wabgz.git
   git remote add origin https://gitee.com/zhuxiaoyizxy/daima.git
   ```

7. 其他命令

```
git checkout -b master 
创建并切换分支
git remote remove origin 
删除 origin远程仓库
git remote add <新的别名> <远程仓库地址>  使用新的别名
git log 
查看提交信息
git pull 仓库地址 分支
拉取远程仓库文件
```



