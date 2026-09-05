# Mongodb

> MongoDB 是**文档型 NoSQL（非关系型）数据库**，C++ 编写，2007 年发布，采用 BSON（二进制 JSON）存储数据，是目前最流行的文档数据库抖音百科。区别于 MySQL 这类关系型数据库，它没有固定表结构，适合存储**嵌套、字段经常变化**的数据。

## 区分 3 个东西（很容易搞混）

| 文件                               | 类型                    | 说明                                                         |
| ---------------------------------- | ----------------------- | ------------------------------------------------------------ |
| `mongodb‑compass‑1.46.5‑win32‑x64` | **GUI 图形客户端**      | 你现在打开的那个带界面的软件，**可视化点鼠标操作数据库**，内置嵌入式 mongosh，但是**没有独立的命令行程序**MongoDB。 |
| `mongosh`                          | **命令行 Shell 客户端** | 纯黑窗口命令行工具，用来在 cmd/powershell 敲 mongodb 命令；**需要单独下载**，compass 不自带独立 mongosh.exe。 |
| `mongodb‑server`                   | **服务端程序**          | 数据库服务本体，负责存数据，不是客户端。                     |

## 一、下载

1. 官网下载页：

   https://www.mongodb.com/try/download/community

   - Platform：`Windows x64`
   - Package：**zip**（不要选 msi）

2. 单独下载 mongosh（命令行 shell）：

   https://www.mongodb.com/try/download/shell

   - 同样选 Windows x64，zip 包。

3. Compass 可视化工具：**独立安装**（zip 版不带，必须单独装）

## 二、解压目录结构

1. MongoDB 官网下载 **MongoDB Shell (mongosh)**，Windows 版本
2. 解压后把 `mongosh.exe` 复制到你的 `bin` 文件夹，之后就可以用 `.\mongosh.exe` 连接

```
D:\mongodb\server
├─ bin\          # mongod.exe 服务端程序
├─ data\db       # 数据库数据目录（手动新建！）
└─ logs          # 日志目录（手动新建！）
```

把 mongosh zip 解压，把里面`mongosh.exe`复制到 `D:\mongodb\server\bin`

> 目录全部自己手动创建：

```
md D:\mongodb\server\data\db
md D:\mongodb\server\logs
```

## 三、启动

**创建目录**

```
D:\mongodb\server
├─ bin
│  └─ mongod.exe
      mongosh.exe
├─ data
│  └─ db       ✅必须手动建好这两层文件夹
└─ logs
   └─ mongo.log  ✅logs文件夹要存在，mongo.log可以不用手动建
```

### 方式 1：临时启动（cmd 窗口关掉就停止数据库，开发测试首选）

启动方式

```
cd /d D:\mongodb\server\bin
mongod.exe --dbpath="D:\mongodb\server\data\db" --logpath="D:\mongodb\server\logs\mongo.log"

相对目录 bin 文件夹启动
.\mongod.exe --dbpath="../data/db" --logpath="../logs/mongo.log"

```

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260828010329.png)

### 方式 2：注册为 Windows 服务（可选，开机自启）

管理员 CMD 执行：在bin 目录

```
mongod.exe --dbpath=..\data\db --logpath=..\logs\mongo.log
```

启动服务：

```
net start MongoDB
```

停止服务：

```
net stop MongoDB
```



## 四、连接数据库

**默认端口：`27017`**

- 连接地址：`127.0.0.1:27017`
- 如果你启动时没指定 `--port`，就使用 27017

**MongoDB 默认没有账号密码！** 刚安装启动，**没有任何用户，不需要账号密码就能直接连接**，就是你现在的状态。 警告日志：`Access control is not enabled for the database` 就是这个意思。

新开一个 CMD，进入 bin 目录执行：

```
mongosh
```

默认连接：`mongodb://localhost:27017`

### 测试 demo 命令

```
use demo_db
db.user.insertOne({name:"张三",age:22})
db.user.find()
```

```
// 查看所有数据库
show dbs

// 创建/切换数据库
use mydb

// 插入一条测试数据
db.test.insertOne({name:"test",age:18})

// 查询数据
db.test.find()
```



## 五、连接 Compass 可视化工具

Compass 是独立软件，**zip 版 mongodb 不会自带**，需要单独下载安装。 连接字符串：

```
mongodb://localhost:27017
```

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260828011025.png)

使用第三方

**DBeaver**

首选创建一个驱动管理 默认的mongdb是商业的用不了

| 字段           | 填入内容                                 |
| -------------- | ---------------------------------------- |
| **驱动名称**   | `MongoDB-new`                            |
| **类名**       | `com.dbschema.MongoJdbcDriver`           |
| **URL 模板**   | `mongodb://{host}[:{port}]/[{database}]` |
| **默认端口**   | `27017`                                  |
| **默认数据库** | 留空                                     |
| **默认用户**   | 留空                                     |

✅勾选两个复选框：

- ☑ **无需身份验证**
- ☑ **允许空密码**

> 其他选项保持原样：`Thread safe driver` 保持勾选，其余不勾。

**接下来操作步骤**

1. 填完上面设置页后，**切换到【库】标签页**
2. 点击【下载 / 更新】，DBeaver 会自动下载 `mongo‑jdbc‑standalone.jar` 驱动 jar 包
3. 手动[下载](https://download.jetbrains.com/idea/jdbc-drivers/MongoDB/mongo-jdbc-standalone-1.20.jar)jar包

> ⚠️必须等下载完成，不然连接会报找不到驱动。

1. 下载完成，点【确定】保存这个驱动。

**后续新建连接**

数据库 → 新建数据库连接 → 选中刚才建好的`MongoDB`驱动

- 主机：`localhost`
- 端口：`27017`
- 用户名、密码全部留空（你现在没有开启认证）
- 点击测试连接，成功就可以使用。



![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260828012414.png)