## 一、官方下载地址（8.4 LTS 长期支持版，推荐）

### 下载页

[https://dev.mysql.com/downloads/mysql/](https://link.wtturl.cn/?target=https%3A%2F%2Fdev.mysql.com%2Fdownloads%2Fmysql%2F&scene=im&aid=497858&lang=zh)

1. 版本选择：`8.4.x LTS`
2. 系统：`Microsoft Windows`
3. 安装包：**Windows (x86, 64-bit), ZIP Archive**（免安装压缩包）
4. 点击 `Download` → `No thanks, just start my download.` 无需登录 Oracle 账号直接下载

![img](data:image/svg+xml,%3csvg%20xmlns=%27http://www.w3.org/2000/svg%27%20version=%271.1%27%20width=%27256%27%20height=%27192%27/%3e)![image](https://p26-flow-imagex-sign.byteimg.com/labis/image/a016a6883dd19fa117b4cf94e9a613ef~tplv-be4g95zd3a-448x448.jpeg?lk3s=8e244e95&rcl=20260706145028403C872BE6672D00399B&rrcfp=2033b573&x-expires=1783925429&x-signature=nZJ%2BZx5iLEpd5N6oOFFttvmi5O4%3D)

选择ZIP包

![img](data:image/svg+xml,%3csvg%20xmlns=%27http://www.w3.org/2000/svg%27%20version=%271.1%27%20width=%27256%27%20height=%27192%27/%3e)![image](https://p6-flow-imagex-sign.byteimg.com/labis/image/d31869bce28efa82d1867a2b9dfe379d~tplv-be4g95zd3a-448x448.jpeg?lk3s=8e244e95&rcl=20260706145028403C872BE6672D00399B&rrcfp=2033b573&x-expires=1783925429&x-signature=X31n8PFSIpOdXjd4d1X1RizIrB4%3D)

跳过登录下载

## 二、解压准备（关键：路径不能有中文 / 空格）

1. 将压缩包解压到纯英文路径，示例：

   

   ```
   D:\Software\mysql-8.4.1-winx64
   ```

2. 进入根目录，**新建 `my.ini` 配置文件**（自带无配置文件，必须手动创建）

![img](data:image/svg+xml,%3csvg%20xmlns=%27http://www.w3.org/2000/svg%27%20version=%271.1%27%20width=%27256%27%20height=%27192%27/%3e)![image](https://p6-flow-imagex-sign.byteimg.com/labis/image/c3aa724e40b06aaa55990e6f1dbe2ace~tplv-be4g95zd3a-448x448.jpeg?lk3s=8e244e95&rcl=20260706145028403C872BE6672D00399B&rrcfp=2033b573&x-expires=1783925429&x-signature=o3PDpSKVtpFdQMYUrgWAuMzDwd4%3D)

目录结构

## 三、my.ini 完整配置（直接复制，改路径即可）

ini





# 你自己解压的目录

```
[mysqld]
basedir = D:/software/mysql/mysql-8.4.10-winx64/mysql-8.4.10-winx64
datadir = D:/software/mysql/mysql-8.4.10-winx64/mysql-8.4.10-winx64/data
port = 3306
character-set-server = utf8mb4
max_connections = 200

[mysql]
default-character-set = utf8mb4
```

> 注意：路径用 `/` 或者 `\\`，不能单写 `\`

## 四、初始化数据库（必须管理员 CMD）

1. 右键开始菜单 → **Windows 终端 (管理员)** / CMD 管理员
2. 切换到 mysql 的 bin 目录

cmd







```
D:
cd D:\Software\mysql-8.4.1-winx64\bin
```

1. 执行初始化命令，生成临时密码（重点！保存密码）

cmd







```
mysqld --initialize --console
```

输出日志找到这行，后面字符串就是临时 root 密码：

```
A temporary password is generated for root@localhost: xxxxxx
```

![img](data:image/svg+xml,%3csvg%20xmlns=%27http://www.w3.org/2000/svg%27%20version=%271.1%27%20width=%27256%27%20height=%27192%27/%3e)![image](https://p26-flow-imagex-sign.byteimg.com/labis/image/ce8a9f2957c23ee8ba31df1cd1044837~tplv-be4g95zd3a-448x448.jpeg?lk3s=8e244e95&rcl=20260706145028403C872BE6672D00399B&rrcfp=2033b573&x-expires=1783925429&x-signature=JzogxQ9mLKifZfueQpVB25ZkY78%3D)

管理员CMD进入bin目录

### 无密码初始化（本地开发懒人方案，不推荐生产）

cmd

7N4j;X4NBFXv





```
mysqld --initialize-insecure
```

## 五、注册 Windows 系统服务（开机自启）

bin 目录下执行：

cmd







```
# 服务名自定义为MySQL8，方便区分多版本
mysqld --install MySQL8 --defaults-file="D:\software\mysql\mysql-8.4.10-winx64\mysql-8.4.10-winx64\my.ini"
D:\software\mysql\mysql-8.4.10-winx64\mysql-8.4.10-winx64
```

提示 `Service successfully installed` 代表注册成功。

## 六、启动 / 停止服务

cmd







```
# 启动
net start MySQL8
# 停止
net stop MySQL8
```

## 七、登录并修改 root 密码

1. 登录 mysql，输入刚才保存的临时密码

cmd







```
mysql -u root -p
```

1. 修改密码（新密码自定义，示例 `123456`）

sql





ALTER USER 'root'@'localhost' IDENTIFIED BY '412826';
FLUSH PRIVILEGES;

```
ALTER USER 'root'@'localhost' IDENTIFIED BY '123456';
FLUSH PRIVILEGES;
exit;
```

## 八、配置系统环境变量（任意窗口直接使用 mysql 命令）

1. 此电脑右键 → 属性 → 高级系统设置 → 环境变量

2. 系统变量找到 

   ```
   Path
   ```

    → 编辑 → 新建，填入你的 bin 路径：

   

   ```
   D:\Software\mysql-8.4.1-winx64\bin
   ```

3. 全部窗口确定，新开 cmd 测试：

cmd







```
mysql -V
```

## 九、允许远程连接（前后端联调必备）

登录 mysql 执行：

sql







```
# 创建可远程访问root账号
CREATE USER 'root'@'%' IDENTIFIED BY '123456';
# 赋予全部权限
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;
```

## 十、常见报错解决

1. 初始化报错找不到 VCRUNTIME140.dll

   

   安装微软 VC++2015-2022 运行库（MySQL8 依赖）

2. 服务启动失败

   

   删除根目录自动生成的

   ```
   data
   ```

   文件夹，重新执行初始化命令

3. 忘记临时密码

   

   删除 data 文件夹，重新 

   ```
   mysqld --initialize --console
   ```

4. Navicat 连接报错 1251

   

   my.ini 里配置 

   ```
   default_authentication_plugin = mysql_native_password
   ```

   ，重启服务

## 十一、卸载免安装版

cmd







```
# 管理员CMD停止服务，删除服务
net stop MySQL8
sc delete MySQL8
# 直接删除整个mysql文件夹即可，无残留注册表
```