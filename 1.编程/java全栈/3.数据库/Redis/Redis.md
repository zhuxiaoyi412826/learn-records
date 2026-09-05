# 必会知识点

1. **五大基础数据类型 + 底层结构** **手写五大数据结构和应用场景和命令**（核心重中之重）
2. **四种特殊增强类型 业务场景与使用**
3. **Redis 事务机制、乐观锁 WATCH**
4. **过期删除策略 + 六大内存淘汰策略**
5. **RDB/AOF 持久化原理与混合持久化**
6. **缓存三大问题：穿透、击穿、雪崩解决方案**
7. **分布式锁实现原理、Lua 脚本防误删**
8. **单线程模型快的原因、IO 多路复用**
9. **主从复制、哨兵、Cluster 集群架构**
10. **Pipeline 管道、Lua 脚本原子性**
11. **BigKey 热 Key 危害与优化方案**
12. **常用高频命令、生产禁用危险命令**
13. **Redis 缓存设计规范与业务实战用法**
14. **Redis 性能调优、内存配置优化**
15. **Redis 常见故障排查与线上问题处理**
16. **1 java如何操作redis书写格式  2Jedis   3Spring Data Redis**

# Redis

## 什么是NoSQL

### 1. 一句话定义

**NoSQL = 非关系型数据库**，**不遵循表格行列结构**，用来存**海量、灵活、高并发**数据。

### 2. 和 MySQL（SQL）最大区别

- **SQL（MySQL/Oracle）**：表格、行、列、固定字段、讲究**关系** 结构化数据
- **NoSQL**：无固定表结构、**灵活自由**、主打快、量大 非结构化

### 3. 四类主流 NoSQL

1. 键值型（Redis）

   存 key=value，最快，做缓存、会话

2. 文档型（MongoDB）

   存 JSON / 类 JSON，字段随便加，最常用

3. 列族型（HBase）

   超大海量数据、大数据场景

4. 图数据库（Neo4j）

   存关系：好友、社交、路径、人脉

### 4. NoSQL 优点

- 结构灵活，字段随便增减
- 读写速度极快，扛高并发
- 轻松横向扩容，存海量数据
- 不用设计复杂表关系

### 5. NoSQL 缺点

- **不支持强事务**（转账这类慎用）
- 查询语法不如 SQL 通用
- 复杂联表查询很麻烦

## 什么是Redis

### 1. 一句话

Redis诞生于2009年全称是Remote Dictionary Server，远程词典服务器

**Redis 是最快的 NoSQL 键值内存数据库，主打高速缓存**

**日常使用 5 种 数据类型**

**底层源码：8 种底层数据结构**

**完整版可使用：9 种**（5 基础 + 4 扩展）

[指令 |文档](https://redis.io/docs/latest/commands/) 官网

### 2. 核心特点

- **数据存在内存**，读写**超级快**
- 断电怕丢数据，支持**持久化存硬盘**
- 纯键值 `key:value` 结构
- 单线程模型，并发极强

### 3. 五大常用数据类型

1. **String** 字符串（存文字、数字、手机号）
2. **Hash** 哈希（存用户信息、商品）
3. **List** 列表（消息队列、排队）
4. **Set** 集合（去重、好友列表）
5. **ZSet** 有序集合（排行榜、积分排名）

四种扩展类型

Bitmap（位图）

HyperLogLog

Geospatial（GEO 地理空间）

Stream（流）

### 4. 日常用途（面试必背）

1. **接口缓存**：查数据库慢，先查 Redis
2. **登录令牌 Token 存储**
3. **验证码、短信倒计时**
4. **分布式锁**（多服务抢资源）
5. **点赞、浏览量、排行榜**
6. **限流、防重复提交**
7. **消息队列简易实现**

### 5. 优缺点

**优点**

- 速度天花板
- 用法简单
- 高并发友好
- 功能极多

**缺点**

- 内存成本高，不能存超大冷数据
- 内存满会淘汰数据
- 不适合存复杂事务

### 6. 最简单对比

- **MySQL**：存正式业务数据，稳、持久
- **Redis**：存热点高频数据，快、提速

### 7. 底层数据结构

| 底层结构                | 对应上层数据类型                   | 补充说明                                               |
| ----------------------- | ---------------------------------- | ------------------------------------------------------ |
| **SDS 简单动态字符串**  | String、所有结构的键、值字符串部分 | Redis 键一律是 SDS；String 类型底层纯 SDS              |
| **Dict 哈希表**         | Hash、Set、全局键空间              | Set (元素非整数)、Hash 主力实现；全局 KV 存储依赖 Dict |
| **IntSet 整数集合**     | Set                                | Set 内**全是整数**时使用，内存更紧凑                   |
| **ZipList 压缩列表**    | Hash、ZSet、List (旧版)            | 元素数量少、值体积小时启用，节约内存                   |
| **LinkedList 双向链表** | List（Redis 3.2 之前）             | 老版本 List 底层，现已被 QuickList 替代                |
| **QuickList 快速列表**  | List（Redis 3.2+ 主流）            | LinkedList + ZipList 组合，现在 List 默认底层          |
| **SkipList 跳跃表**     | ZSet（有序集合）                   | ZSet 有序排序、范围查询的核心，搭配 Dict 使用          |
| **ListPack 列表压缩包** | Hash、ZSet、Stream                 | Redis 6.0+ 逐步替代 ZipList，解决连锁更新缺陷          |

**简单动态字符串（SDS）**

Redis 自己实现的字符串，比 C 字符串更安全、支持扩容、能高效获取长度。

**双向链表（LinkedList）**

有序、可前后遍历、支持快速头尾操作，是 Redis List 的底层实现之一。

**压缩列表（ZipList）**

内存极紧凑的连续数组结构，元素少时用它，省内存、访问快。

**哈希表（Dict/HashTable）**

键值对高速存储结构，靠哈希函数快速查找，Redis 所有 kv 都靠它管理。

**跳跃表（SkipList）**

带多层索引的有序链表，查找效率接近平衡树，实现简单，是 ZSet 的核心。

**整数集合（IntSet）**

专门存纯整数的紧凑小集合，自动升级类型，省内存、查找快。

**快速列表（QuickList）**

双向链表 + 压缩列表的结合体，兼顾链表的便捷和 ziplist 的省内存。

**列表压缩包（ListPack）**

新一代紧凑存储结构，替代 ziplist，解决了连锁更新问题，更安全高效。

## 安装

redis-server 前台启动   start /b 临时后台启动

INFO server 查看redis版本  cmd 下查看redis版本 redis-server --version

redis版本下载  

[Releases · redis-windows/redis-windows](https://github.com/redis-windows/redis-windows/releases)

![image-20260519075159882](C:\Users\DELL\Desktop\笔记\数据库\img\Reids\下载.png)

选择这个 **Redis-8.4.1-Windows-x64-msys2-with-Service.zip**

# Redis 命令

## 数据结构

Redis是一个key-value的数据库，key一般是String类型，不过value的类型多种多样：

| 数据类型 | 全称     | 存储结构           | 有序性       | 能否重复     | 核心特点                         | 常用命令                         | 适用业务场景                                      |
| -------- | -------- | ------------------ | ------------ | ------------ | -------------------------------- | -------------------------------- | ------------------------------------------------- |
| String   | 字符串   | 简单动态字符串     | 无顺序       | 互不影响     | 最简单、可存文本 / 数字 / JSON   | set、get、incr、append           | 缓存数据、验证码、Token、计数器、手机号、普通配置 |
| List     | 列表     | QuickList 快速链表 | **有序**     | **允许重复** | 头尾操作极快，中间查询慢         | lpush、rpush、lpop、rpop、lrange | 消息队列、浏览记录、朋友圈时间线、排队队列        |
| Hash     | 哈希     | 压缩列表 + 字典    | 无序         | field 唯一   | 适合存对象，节省内存             | hset、hget、hgetall、hincrby     | 用户信息、商品信息、购物车、员工资料              |
| Set      | 无序集合 | 哈希表             | **无序**     | **自动去重** | 天然去重，支持交集 / 差集 / 并集 | sadd、smembers、sinter、sdiff    | 好友列表、共同好友、点赞统计、抽奖去重            |
| ZSet     | 有序集合 | 跳表 + 哈希表      | **有序排序** | 成员唯一     | 带分数排序，可升降序             | zadd、zrange、zrevrank、zscore   | 排行榜、成绩排名、积分排行、延时队列              |

**存单个值 → String**

**存对象字段 → Hash**

**做队列顺序数据 → List**

**需要自动去重 → Set**

**需要排序排名 → ZSet**

Redis6 + 还支持：

1. **Geospatial** 地理位置（经纬度、附近人）
2. **Bitmap** 位图（签到、状态标记）
3. **HyperLogLog** 基数统计（海量去重统计）
4. **Stream** 消息流（可靠消息队列）

## Redis 通用命令

（所有数据类型都能用）

1. **keys \*** 查看所有 key
2. **exists key** 判断 key 是否存在
3. **del key** 删除 key
4. **expire key 秒数** 设置过期时间
5. **ttl key** 查看剩余过期时间
6. **persist key** 取消过期
7. **type key** 查看 key 是什么类型
8. **rename 旧 key 新 key** 重命名 key
9. **randomkey** 随机获取一个 key
10. **move key 库号** 移动 key 到其他数据库
11. **select 0** 切换数据库（0-15）
12. **flushdb** 清空当前数据库
13. **flushall** 清空全部数据库

COMMAND INFO keys  查看某个命令怎么使用 

help keys cmd Linux下使用



## String

String类型

**String类型，也就是字符串类型，是Redis中最简单的存储类型。其value是字符串，不过根据字符串的格式不同，又可以分为3类：**
**string：普通字符串**
**int：整数类型，可以做自增、自减操作**
**float：浮点类型，可以做自增、自减操作**
**不管是哪种格式，底层都是字节数组形式存储，只不过是编码方式不同。字符串类型的最大空间不能超过512m.**

String的常见命令有：

```
SET：添加或者修改已经存在的一个String类型的键值对
GET：根据key获取String类型的value
MSET：批量添加多个String类型的键值对
MGET：根据多个key获取多个String类型的value
INCR：让一个整型的key自增1
INCRBY:让一个整型的key自增并指定步长，例如：incrby num 2 让num值自增2
INCRBYFLOAT：让一个浮点类型的数字自增并指定步长
SETNX：添加一个String类型的键值对，前提是这个key不存在，否则不执行
SETEX：添加一个String类型的键值对，并且指定有效期
```

```
# 1. 设置值
set k1 hello

# 2. 获取值
get k1

# 3. 不存在才设置（防覆盖）
setnx k1 123

# 4. 设置+指定过期时间(秒)
setex k2 10 666

# 5. 批量设置
mset name zhang age 20 sex nan

# 6. 批量获取
mget name age sex

# 7. 数值自增1
incr num

# 8. 数值自减1
decr num

# 9. 增减指定步数
incrby num 5
decrby num 3

# 10. 末尾追加内容
append k1 world

# 11. 获取字符串长度
strlen k1

# 12. 获取部分字符
getrange k1 0 3

# 13. 覆盖指定位置
setrange k1 2 abc
```



Redis的key允许有多个单词形成层级结构，多个单词之间用':'隔开，格式如下：

如果Value是一个Java对象，例如一个User对象，则可以将对象序列化为JSON字符串后存储：

```
setex student:1 3600 '{"id":1,"name":"小明","age":19,"gender":"男","address":"河南驻马店"}'
```

```
get student:1
```

## Hash

**1.hash类型**

**Hash类型，也叫散列，其value是一个无序字典，类似于Java中的HashMap结构。**
**String结构是将对象序列化为JSON字符串后存储，当需要修改对象某个字段时很不方便：**
**Hash结构可以将对象中的每个字段独立存储，可以针对单个字段做CRUD：**

**ash = Redis 里的「对象」**

专门用来存 **一个 key 对应多个字段** 的数据。

比如：

- 学生：id、姓名、年龄、性别
- 用户：id、昵称、手机号、头像
- 商品：id、名称、价格、库存

**一个 key 存一整个对象！**

```
key  →  { 字段1:值1, 字段2:值2, 字段3:值3 ... }
```

```
student:1  →  { id:1, name:"小明", age:19, gender:"男" }
```

**2.和 String 存 JSON 的区别**

- **String JSON**：整个对象一整块存，**要改必须全改**
- **Hash**：可以**单独改名字、单独改年龄**，不用动其他字段

**实际开发：存对象优先用 Hash！**

 **3.为什么要用 Hash？（重点）**

1. **结构清晰**，像对象一样
2. **单独修改字段**，不用全量更新
3. **节省内存**，比存 JSON 更高效
4. **最适合存：用户、商品、学生、订单**

**4.常用命令**

```
HSET key field value：添加或者修改hash类型key的field的值
HGET key field：获取一个hash类型key的field的值
HMSET：批量添加多个hash类型key的field的值
HMGET：批量获取多个hash类型key的field的值
HGETALL：获取一个hash类型的key中的所有的field和value
HKEYS：获取一个hash类型的key中的所有的field
HVALS：获取一个hash类型的key中的所有的value
HINCRBY:让一个hash类型key的字段值自增并指定步长
HSETNX：添加一个hash类型的key的field值，前提是这个field不存在，否则不执行
```

```
练习 1：存入学生信息
redis
hset stu:01 sid 1 name 李明 age 18 class 一班 score 88
练习 2：单独查姓名
redis
hget stu:01 name
练习 3：查姓名 + 年龄 + 分数
redis
hmget stu:01 name age score
练习 4：查看学生全部信息
redis
hgetall stu:01
练习 5：修改年龄
redis
hset stu:01 age 19
练习 6：分数加 5
redis
hincrby stu:01 score 5
练习 7：删除班级字段
redis
hdel stu:01 class
练习 8：统计有几个属性
redis
hlen stu:01
练习 9：清空当前库
redis
flushdb
```

## List

Redis中的List类型与Java中的LinkedList类似，可以看做是一个双向链表结构。既可以支持正向检索和也可以支持反向检索。
特征也与LinkedList类似：
有序
元素可以重复
插入和删除快
查询速度一般
常用来存储一个有序数据，例如：朋友圈点赞列表，评论列表等。

```
List的常见命令有：
LPUSH key  element ... ：向列表左侧插入一个或多个元素
LPOP key：移除并返回列表左侧的第一个元素，没有则返回nil
RPUSH key  element ... ：向列表右侧插入一个或多个元素
RPOP key：移除并返回列表右侧的第一个元素
LRANGE key star end：返回一段角标范围内的所有元素
BLPOP和BRPOP：与LPOP和RPOP类似，只不过在没有元素时等待指定时间，而不是直接返回nil
```

```
练习 1：从左侧插入班级学生
redis
lpush class:stu 小明 小红 小刚
练习 2：从右侧插入两名学生
redis
rpush class:stu 小丽 小强
练习 3：查看列表所有学生（全部数据）
redis
lrange class:stu 0 -1
练习 4：查看列表长度
redis
llen class:stu
练习 5：移除左侧第一个学生
redis
lpop class:stu
练习 6：移除右侧最后一个学生
redis
rpop class:stu
练习 7：修改索引为 1 位置的学生名字
redis
lset class:stu 1 小宇
练习 8：根据值删除指定数量相同元素
redis
lrem class:stu 1 小红
练习 9：截取保留前 3 个学生，其余删除
redis
ltrim class:stu 0 2
练习 10：获取指定索引位置学生
redis
lindex class:stu 0
清空当前数据库
redis
flushdb
```

## set

Redis的Set结构与Java中的HashSet类似，可以看做是一个value为null的HashMap。因为也是一个hash表，因此具备与HashSet类似的特征：
无序
元素不可重复
查找快
支持交集、并集、差集等功能

set的常见命令有：

```
SADD key member ... ：向set中添加一个或多个元素
SREM key member ... : 移除set中的指定元素
SCARD key： 返回set中元素的个数
SISMEMBER key member：判断一个元素是否存在于set中
SMEMBERS：获取set中的所有元素
SINTER key1 key2 ... ：求key1与key2的交集
SDIFF key1 key2 ... ：求key1与key2的差集
SUNION key1 key2 ..：求key1和key2的并集
```

```
练习 1：存入好友数据
redis
sadd zs:friend 李四 王五 赵六
sadd ls:friend 王五 麻子 二狗
练习 2：统计张三好友人数
redis
scard zs:friend
练习 3：查询两人共同好友
redis
sinter zs:friend ls:friend
练习 4：查询张三独有好友
redis
sdiff zs:friend ls:friend
练习 5：查询两人所有好友合集
redis
sunion zs:friend ls:friend
练习 6：判断李四是否是张三好友
redis
sismember zs:friend 李四
练习 7：判断张三是否是李四好友
redis
sismember ls:friend 张三
练习 8：移除张三好友里的李四
redis
srem zs:friend 李四
练习 9：查看张三全部好友
redis
smembers zs:friend
```

## SortedSet

Redis的SortedSet是一个可排序的set集合，与Java中的TreeSet有些类似，但底层数据结构却差别很大。SortedSet中的每一个元素都带有一个score属性，可以基于score属性对元素排序，底层的实现是一个跳表（SkipList）加 hash表。
SortedSet具备下列特性：
可排序
元素不重复
查询速度快
因为SortedSet的可排序特性，经常被用来实现排行榜这样的功能。

```
SortedSet的常见命令有：
ZADD key score member：添加一个或多个元素到sortedset ，如果已经存在则更新其score值
ZREM key member：删除sorted set中的一个指定元素
ZSCORE key member : 获取sorted set中的指定元素的score值
ZRANK key member：获取sorted set 中的指定元素的排名
ZCARD key：获取sorted set中的元素个数
ZCOUNT key min max：统计score值在给定范围内的所有元素的个数
ZINCRBY key increment member：让sorted set中的指定元素自增，步长为指定的increment值
ZRANGE key min max：按照score排序后，获取指定排名范围内的元素
ZRANGEBYSCORE key min max：按照score排序后，获取指定score范围内的元素
ZDIFF、ZINTER、ZUNION：求差集、交集、并集
注意：所有的排名默认都是升序，如果要降序则在命令的Z后面添加REV即可

```

```
练习 1：存入学生成绩数据
redis
zadd class:score 85 Jack 89 Lucy 82 Rose 95 Tom 78 Jerry 92 Amy 76 Miles
练习 2：删除 Tom 同学
redis
zrem class:score Tom
练习 3：获取 Amy 同学的分数
redis
zscore class:score Amy
练习 4：获取 Rose 同学的排名（从低到高，从 0 开始）
redis
zrank class:score Rose
练习 4 扩展：获取 Rose 同学排名（从高到低，热门榜）
redis
zrevrank class:score Rose
练习 5：查询 80 分以下有几个学生
redis
zcount class:score 0 79
练习 6：给 Amy 同学加 2 分
redis
zincrby class:score 2 Amy
练习 7：查出成绩前 3 名的同学（从高到低）
redis
zrevrange class:score 0 2
练习 8：查出成绩 80 分以下的所有同学
redis
zrangebyscore class:score 0 79
练习 9：查看所有同学成绩（从低到高）
redis
zrange class:score 0 -1 withscores
练习 10：清空当前库
redis
flushdb
```

# java操作Redis

https://redis.io/clients

以Redis命令作为方法名称，学习成本低，简单实用。但是Jedis实例是线程不安全的，多线程环境下需要基于连接池来使用

Lettuce是基于Netty实现的，支持同步、异步和响应式编程方式，并且是线程安全的。支持Redis的哨兵模式、集群模式和管道模式。

Redisson是一个基于Redis实现的分布式、可伸缩的Java数据结构集合。包含了诸如Map、Queue、Lock、 Semaphore、AtomicLong等强大功能

## 0.yml配置

### 1. 最常用：单机版 Redis 完整配置（推荐）

```
spring:
  redis:
    # 基础配置
    host: 127.0.0.1          # Redis 地址
    port: 6379               # 端口
    password:                # 密码（没有就留空）
    database: 0              # 使用的数据库号
    connect-timeout: 10s     # 连接超时
    timeout: 3000            # 读写超时

    # Lettuce 连接池（SpringBoot 默认使用）
    lettuce:
      pool:
        max-active: 8        # 最大连接数
        max-idle: 8          # 最大空闲连接
        min-idle: 2          # 最小空闲连接
        max-wait: -1ms       # 获取连接最大等待时间（-1 无限制）

    # 关闭 SSL（公网需要才开启）
    ssl: false
```

### 2. 哨兵模式（Sentinel）高可用配置

```
spring:
  redis:
    password: 
    database: 0
    timeout: 3000

    # 哨兵配置
    sentinel:
      master: mymaster       # 哨兵主节点名称
      nodes:
        - 192.168.1.10:26379
        - 192.168.1.11:26379
        - 192.168.1.12:26379
```

### 3. 集群模式（Redis Cluster）配置

```
spring:
  redis:
    password: 
    timeout: 3000
    cluster:
      nodes:
        - 192.168.1.10:6379
        - 192.168.1.11:6379
        - 192.168.1.12:6379
      max-redirects: 3       # 最大重定向次数
```

## 1.jedis

1. https://github.com/redis/jedis

Jedis使用的基本步骤：

**1.引入依赖**

```
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>7.1.0</version>
</dependency>
```

**2.确保 Redis 服务正常运行**

redis-server

- Redis 已启动
- 端口默认：**6379**
- 若有密码，需要认证
- 防火墙 / 安全组开放 6379 端口（远程连接时）



**3.创建 Jedis 对象**

```
// 1. 创建 Jedis 连接（参数：host, port）
Jedis jedis = new Jedis("localhost", 6379);
```

**4.如果 Redis 设置了密码，进行认证**

```
// 2. 密码认证（没有密码可省略）
jedis.auth("your-redis-password");
```

5.**测试连接是否成功**

```
// 3. 测试连通性（执行 PING 命令，返回 PONG 表示成功）
System.out.println("连接成功：" + jedis.ping());
```

**6.执行 Redis 操作（字符串、哈希、列表等）**

```
// 字符串操作
jedis.set("name", "JedisTest");
System.out.println("获取name：" + jedis.get("name"));

// 哈希操作
jedis.hset("user:1", "name", "张三");
jedis.hset("user:1", "age", "20");
System.out.println("用户信息：" + jedis.hgetAll("user:1"));
```

**7.使用完毕关闭连接**

```
// 5. 关闭连接（重要！避免连接泄漏）
jedis.close();
```

**8.代码案例**

```
import redis.clients.jedis.Jedis;
public class JedisDemo {
    public static void main(String[] args) {
        // 1. 创建连接
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        // 2. 密码认证（没有则注释）
        // jedis.auth("123456");
        
        // 3. 测试连接
        System.out.println("连接状态：" + jedis.ping());

        // 4. 执行操作
        jedis.set("key1", "hello redis");
        System.out.println("key1 = " + jedis.get("key1"));

        // 5. 关闭连接
        jedis.close();
    }
}
```

在使用javase是编译时要把导入的jar包也进行编译

**9.使用连接池**

```
import redis.clients.jedis.Jedis; //  Redis操作的核心类，用于执行Redis命令
import redis.clients.jedis.JedisPool; //  连接池类，用于管理Redis连接
import redis.clients.jedis.JedisPoolConfig; //  连接池配置类，用于配置连接池参数

// 连接池工具类，用于创建连接池，获取连接，释放连接等操作

class JedisPoolUtil {
    private static JedisPool jedisPool;

    static {    
        JedisPoolConfig config = new JedisPoolConfig(); //  连接池配置对象
        config.setMaxTotal(30); //  设置连接池中最大连接数
        config.setMaxIdle(10); //  设置连接池中最大空闲连接数
        config.setMinIdle(5); //  设置连接池中最小空闲连接数
        config.setTestOnBorrow(true); //  设置是否在借连接时测试连接是否有效

        jedisPool = new JedisPool(
            config,
            "localhost",
            6379,
            5000,
            null //  Redis 密码（无则填 null）
        );
    }

    public static Jedis getJedis() {    
        return jedisPool.getResource(); //  从Jedis连接池中获取一个Jedis资源
    }

    public static void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}

public class JedisPoolDemo {
    public static void main(String[] args) {
        Jedis jedis = JedisPoolUtil.getJedis();
        try {
            jedis.set("poolKey", "poolTest"); //  设置一个键值对
            System.out.println("poolKey = " + jedis.get("poolKey"));
        } finally {
            JedisPoolUtil.close(jedis); //  在finally块中确保Jedis实例被正确关闭，归还给连接池
        }
    }
}
```

## **2.SpringBoot来整合下Jedis的连接池**

**1.目录结构**

```
src
└── main
    ├── java
    │   └── com
    │       └── demo
    │           ├── JedisApplication.java  // 启动类
    │           ├── config
    │           │   └── JedisConfig.java   // Jedis连接池配置类
    │           ├── properties
    │           │   └── RedisProperties.java // Redis配置属性类
    │           └── util
    │               └── JedisUtil.java      // Jedis工具类
    └── resources
        └── application.yml  // 配置文件
```

**2.引入 jedis 依赖**

```
  <!--springboot父工程-->
   <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.5</version>
        <relativePath/>
    </parent>
    
      <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
            <exclusions> <!--       依赖排除配置，用于移除默认的 Lettuce 连接池实现      Lettuce 是一个高性能的 Redis 客户端，支持同步、异步和响应式模式    -->
                <exclusion> <!-- 排除 Lettuce 核心依赖 -->
                    <groupId>io.lettuce</groupId>
                    <artifactId>lettuce-core</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
  <!-- redis依赖-->
  <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
            <version>7.4.0</version>
        </dependency>
        
```

2 **application.yml 配置**（连接池参数）

```
spring:
  application:
    name: redis
  data:
    redis:
      host: localhost
      port: 6379
      password: ~
      timeout: 5000
      jedis:
        pool:
          max-active: 30
          max-idle: 10
          min-idle: 5
          max-wait: 2000
```

 **3 Jedis 配置类并注入bean**（创建连接池）

```
package com.example.redis.config;

import com.example.redis.properties.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class JedisConfig {
//  定义一个不可变的 RedisProperties 类型对象 redisProperties，用于在 JedisConfig 类中存储和引用 Redis 的配置信息。
    private final RedisProperties redisProperties;

    public JedisConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean    //  定义一个Bean，用于创建JedisPool连接池实例
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(redisProperties.getPool().getMaxActive()); //  设置连接池最大连接数
        poolConfig.setMaxIdle(redisProperties.getPool().getMaxIdle()); //  配置连接池的最大空闲连接数
        poolConfig.setMinIdle(redisProperties.getPool().getMinIdle()); //  配置连接池的最小空闲连接数
        poolConfig.setMaxWaitMillis(redisProperties.getPool().getMaxWait()); //  配置获取连接时的最大等待时间（毫秒）
        poolConfig.setTestOnBorrow(redisProperties.getPool().getTestOnBorrow()); //  配置在获取连接时是否进行有效性检查

        String password = redisProperties.getPassword(); //  获取Redis密码
        if (password != null && password.isEmpty()) { //  如果密码为空，设置为null
            password = null;
        }

        return new JedisPool( //  创建并返回JedisPool连接池实例
                poolConfig,
                redisProperties.getHost(),
                redisProperties.getPort(),
                redisProperties.getTimeout(),
                password
        );
    }
}
```

**4 编写配置属性类绑定 yml 配置**

```
package com.example.redis.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component  //  标记为组件，用于自动注入到其他类中
@ConfigurationProperties(prefix = "spring.data.redis")
// 类中的 默认值 是作为**后备值（Fallback）**使用的：
// 1. 优先读取配置文件 ：Spring Boot 会先从 application.yml 读取属性值
// 2. 如果配置文件中没有配置 ：才使用代码中声明的默认值
public class RedisProperties {
    private String host = "localhost";
    private Integer port = 6379; /**     * Redis服务器端口号     * 默认值为6379     */
    private String password;
    private Integer timeout = 5000;
    private Pool pool = new Pool();

    public static class Pool{ /**     * 连接池配置类     */
        private Integer maxActive = 30;
        private Integer maxIdle = 10;
        private Integer minIdle = 5;
        private Long maxWait = 2000L;
        private Boolean testOnBorrow = true;

        public Integer getMaxActive() {
            return maxActive;
        }
        public void setMaxActive(Integer maxActive) {
            this.maxActive = maxActive;
        }
        public Integer getMaxIdle() {
            return maxIdle;
        }
        public void setMaxIdle(Integer maxIdle) {
            this.maxIdle = maxIdle;
        }
        public Integer getMinIdle() {
            return minIdle;
        }
        public void setMinIdle(Integer minIdle) {
            this.minIdle = minIdle;
        }
        public Long getMaxWait() {
            return maxWait;
        }
        public void setMaxWait(Long maxWait) {
            this.maxWait = maxWait;
        }
        public Boolean getTestOnBorrow() {
            return testOnBorrow;
        }
        public void setTestOnBorrow(Boolean testOnBorrow) {
            this.testOnBorrow = testOnBorrow;
        }
    }

    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public Integer getPort() {
        return port;
    }
    public void setPort(Integer port) {
        this.port = port;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Integer getTimeout() {
        return timeout;
    }
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
    public Pool getPool() {
        return pool;
    }
    public void setPool(Pool pool) {
        this.pool = pool;
    }
}
```

 **5 工具类 / Service** 拿连接

```
package com.example.redis.util;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component  //  标记为组件，用于自动注入到其他类中
public class JedisUtil {

    private final JedisPool jedisPool; //  声明一个私有的final类型的JedisPool对象，用于管理Redis连接池

    public JedisUtil(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    public void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
```

 **6 测试接口** 验证

```
package com.example.redis;

import com.example.redis.util.JedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

@SpringBootTest
public class RedisTest {

    @Autowired
    private JedisUtil jedisUtil;

    @Test
    void testRedis() {
        Jedis jedis = jedisUtil.getJedis();
        System.out.println("连接状态：" + jedis.ping());

        jedis.set("name", "springboot-jedis");
        System.out.println("name = " + jedis.get("name"));

        jedisUtil.close(jedis);
    }
}
```

**7 综合案例**

所有 Redis 命令，这里都有对应的 Java 方法，直接调用，不用管连接、关闭、连接池！

**完整版 Redis 五大常用数据类型工具类**，**复制即用，零修改**。

**String（字符串）、Hash（哈希）、List（列表）、Set（集合）、ZSet（有序集合）**

全部写好 **增、删、改、查** 方法，

```
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class JedisUtil {

    @Resource
    private JedisPool jedisPool;

    /**
     * 获取 Jedis 连接
     */
    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    /**
     * 归还连接
     */
    public void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    // ================================ String ====================================

    /**
     * 设置字符串
     */
    public void set(String key, String value) {
        Jedis jedis = getJedis();
        jedis.set(key, value);
        close(jedis);
    }

    /**
     * 设置带过期时间（秒）
     */
    public void setex(String key, int seconds, String value) {
        Jedis jedis = getJedis();
        jedis.setex(key, seconds, value);
        close(jedis);
    }

    /**
     * 获取字符串
     */
    public String get(String key) {
        Jedis jedis = getJedis();
        String val = jedis.get(key);
        close(jedis);
        return val;
    }

    /**
     * 删除key
     */
    public void del(String key) {
        Jedis jedis = getJedis();
        jedis.del(key);
        close(jedis);
    }

    /**
     * 判断key是否存在
     */
    public boolean exists(String key) {
        Jedis jedis = getJedis();
        boolean res = jedis.exists(key);
        close(jedis);
        return res;
    }

    // ================================ Hash ====================================

    /**
     * hash 设置一个字段
     */
    public void hset(String key, String field, String value) {
        Jedis jedis = getJedis();
        jedis.hset(key, field, value);
        close(jedis);
    }

    /**
     * hash 批量设置
     */
    public void hmset(String key, Map<String, String> map) {
        Jedis jedis = getJedis();
        jedis.hmset(key, map);
        close(jedis);
    }

    /**
     * hash 获取一个字段
     */
    public String hget(String key, String field) {
        Jedis jedis = getJedis();
        String val = jedis.hget(key, field);
        close(jedis);
        return val;
    }

    /**
     * hash 获取所有字段
     */
    public Map<String, String> hgetAll(String key) {
        Jedis jedis = getJedis();
        Map<String, String> map = jedis.hgetAll(key);
        close(jedis);
        return map;
    }

    /**
     * hash 删除字段
     */
    public void hdel(String key, String... fields) {
        Jedis jedis = getJedis();
        jedis.hdel(key, fields);
        close(jedis);
    }

    // ================================ List ====================================

    /**
     * list 左插
     */
    public void lpush(String key, String... values) {
        Jedis jedis = getJedis();
        jedis.lpush(key, values);
        close(jedis);
    }

    /**
     * list 右插
     */
    public void rpush(String key, String... values) {
        Jedis jedis = getJedis();
        jedis.rpush(key, values);
        close(jedis);
    }

    /**
     * list 获取范围
     */
    public List<String> lrange(String key, int start, int end) {
        Jedis jedis = getJedis();
        List<String> list = jedis.lrange(key, start, end);
        close(jedis);
        return list;
    }

    /**
     * list 左出队
     */
    public String lpop(String key) {
        Jedis jedis = getJedis();
        String val = jedis.lpop(key);
        close(jedis);
        return val;
    }

    /**
     * list 右出队
     */
    public String rpop(String key) {
        Jedis jedis = getJedis();
        String val = jedis.rpop(key);
        close(jedis);
        return val;
    }

    /**
     * list 获取长度
     */
    public long llen(String key) {
        Jedis jedis = getJedis();
        long len = jedis.llen(key);
        close(jedis);
        return len;
    }

    // ================================ Set ====================================

    /**
     * set 添加
     */
    public void sadd(String key, String... values) {
        Jedis jedis = getJedis();
        jedis.sadd(key, values);
        close(jedis);
    }

    /**
     * set 获取所有
     */
    public Set<String> smembers(String key) {
        Jedis jedis = getJedis();
        Set<String> set = jedis.smembers(key);
        close(jedis);
        return set;
    }

    /**
     * set 删除元素
     */
    public void srem(String key, String... values) {
        Jedis jedis = getJedis();
        jedis.srem(key, values);
        close(jedis);
    }

    /**
     * set 判断是否存在
     */
    public boolean sismember(String key, String value) {
        Jedis jedis = getJedis();
        boolean b = jedis.sismember(key, value);
        close(jedis);
        return b;
    }

    // ================================ ZSet ====================================

    /**
     * zset 添加（带分数）
     */
    public void zadd(String key, double score, String member) {
        Jedis jedis = getJedis();
        jedis.zadd(key, score, member);
        close(jedis);
    }

    /**
     * zset 获取范围（正序）
     */
    public Set<String> zrange(String key, int start, int end) {
        Jedis jedis = getJedis();
        Set<String> set = jedis.zrange(key, start, end);
        close(jedis);
        return set;
    }

    /**
     * zset 获取范围（倒序）
     */
    public Set<String> zrevrange(String key, int start, int end) {
        Jedis jedis = getJedis();
        Set<String> set = jedis.zrevrange(key, start, end);
        close(jedis);
        return set;
    }

    /**
     * zset 删除元素
     */
    public void zrem(String key, String... members) {
        Jedis jedis = getJedis();
        jedis.zrem(key, members);
        close(jedis);
    }

    /**
     * zset 获取元素分数
     */
    public Double zscore(String key, String member) {
        Jedis jedis = getJedis();
        Double score = jedis.zscore(key, member);
        close(jedis);
        return score;
    }
}
```

**8. 综合工具类**

自动序列化、统一过期时间、注解缓存、分页缓存、防空缓存、批量操作

```
SpringBoot+Jedis 通用高级 Redis 缓存工具
实现：自动序列化、统一过期时间、注解缓存、分页缓存、防空缓存、批量操作
基于上面已有的JedisUtil改造升级，无缝接入现有项目
一、新增 JSON 序列化依赖
xml
<!-- JSON序列化 -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson2</artifactId>
    <version>2.0.51</version>
</dependency>
二、升级版 RedisCacheUtil（替换原有 JedisUtil）
java
运行
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class RedisCacheUtil {

    @Resource
    private JedisPool jedisPool;

    private static final long DEFAULT_EXPIRE = 3600L; // 默认过期1小时

    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    public void close(Jedis jedis) {
        if (jedis != null) jedis.close();
    }

    // ====================== 通用基础操作 ======================
    public Boolean hasKey(String key) {
        Jedis jedis = getJedis();
        boolean exist = jedis.exists(key);
        close(jedis);
        return exist;
    }

    public void deleteKey(String key) {
        Jedis jedis = getJedis();
        jedis.del(key);
        close(jedis);
    }

    public void setExpire(String key, long seconds) {
        Jedis jedis = getJedis();
        jedis.expire(key, (int) seconds);
        close(jedis);
    }

    // ====================== String 序列化缓存(核心) ======================
    /**
     * 存入对象，默认1小时过期
     */
    public <T> void setCacheObj(String key, T obj) {
        setCacheObj(key, obj, DEFAULT_EXPIRE);
    }

    /**
     * 存入对象，自定义过期时间
     */
    public <T> void setCacheObj(String key, T obj, long expireSecond) {
        if (obj == null) return;
        String json = JSON.toJSONString(obj);
        Jedis jedis = getJedis();
        jedis.setex(key, (int) expireSecond, json);
        close(jedis);
    }

    /**
     * 获取缓存对象
     */
    public <T> T getCacheObj(String key, Class<T> clazz) {
        Jedis jedis = getJedis();
        String json = jedis.get(key);
        close(jedis);
        if (json == null || json.isEmpty()) return null;
        return JSON.parseObject(json, clazz);
    }

    /**
     * 获取List集合缓存
     */
    public <T> List<T> getCacheList(String key, TypeReference<List<T>> type) {
        Jedis jedis = getJedis();
        String json = jedis.get(key);
        close(jedis);
        if (json == null) return null;
        return JSON.parseObject(json, type);
    }

    // ====================== Hash 缓存 ======================
    public void hashSet(String key, String field, String value) {
        Jedis jedis = getJedis();
        jedis.hset(key, field, value);
        close(jedis);
    }

    public String hashGet(String key, String field) {
        Jedis jedis = getJedis();
        String val = jedis.hget(key, field);
        close(jedis);
        return val;
    }

    public Map<String, String> hashGetAll(String key) {
        Jedis jedis = getJedis();
        Map<String, String> map = jedis.hgetAll(key);
        close(jedis);
        return map;
    }

    // ====================== List 队列缓存 ======================
    public void listPush(String key, String... values) {
        Jedis jedis = getJedis();
        jedis.rpush(key, values);
        close(jedis);
    }

    public List<String> listRange(String key, int start, int end) {
        Jedis jedis = getJedis();
        List<String> list = jedis.lrange(key, start, end);
        close(jedis);
        return list;
    }

    // ====================== Set / ZSet 常用 ======================
    public void setAdd(String key, String... vals) {
        Jedis jedis = getJedis();
        jedis.sadd(key, vals);
        close(jedis);
    }

    public Set<String> setMembers(String key) {
        Jedis jedis = getJedis();
        Set<String> set = jedis.smembers(key);
        close(jedis);
        return set;
    }

    public void zSetAdd(String key, double score, String val) {
        Jedis jedis = getJedis();
        jedis.zadd(key, score, val);
        close(jedis);
    }
}
三、自定义缓存注解（实现注解式缓存）
1. 新建注解 CacheData.java
java
运行
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheData {
    // 缓存key前缀
    String keyPrefix() default "";
    // 过期时间 秒
    long expireTime() default 3600;
}
2. 缓存切面 AOP CacheAspect.java
java
运行
import com.alibaba.fastjson2.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Aspect
@Component
public class CacheAspect {

    @Resource
    private RedisCacheUtil redisCacheUtil;

    @Around("@annotation(cacheData)")
    public Object around(ProceedingJoinPoint joinPoint, CacheData cacheData) throws Throwable {
        // 拼接缓存key
        String key = getCacheKey(joinPoint, cacheData);
        // 先查缓存
        String cacheJson = redisCacheUtil.getJedis().get(key);
        if (StringUtils.hasText(cacheJson)) {
            redisCacheUtil.close(redisCacheUtil.getJedis());
            return JSON.parseObject(cacheJson, Object.class);
        }
        // 缓存没有，执行原方法
        Object result = joinPoint.proceed();
        // 存入缓存
        if (result != null) {
            redisCacheUtil.setCacheObj(key, result, cacheData.expireTime());
        }
        return result;
    }

    // 简易生成key：前缀+方法名
    private String getCacheKey(ProceedingJoinPoint point, CacheData cacheData) {
        String prefix = cacheData.keyPrefix();
        String methodName = point.getSignature().getName();
        return prefix + ":" + methodName;
    }
}
四、实体类测试 User.java
java
运行
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private Integer age;
}
五、业务层使用注解缓存 + 手动缓存
java
运行
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Resource
    private RedisCacheUtil redisCacheUtil;

    // ========== 1. 注解式缓存（最简单） ==========
    @CacheData(keyPrefix = "user", expireTime = 1800)
    public User getUserInfo(){
        // 模拟数据库查询
        return new User(1L,"李四",22);
    }

    // ========== 2. 手动存入对象缓存 ==========
    public void saveUserCache(){
        User user = new User(2L,"王五",25);
        // 存入缓存 2小时过期
        redisCacheUtil.setCacheObj("user:info:2",user,7200);
    }

    // ========== 3. 手动读取对象缓存 ==========
    public User getUserCache(){
        return redisCacheUtil.getCacheObj("user:info:2",User.class);
    }

    // ========== 4. 缓存集合 ==========
    public void saveUserListCache(){
        List<User> userList = new ArrayList<>();
        userList.add(new User(3L,"赵六",20));
        userList.add(new User(4L,"孙七",23));
        redisCacheUtil.setCacheObj("user:list",userList);
    }
}
六、测试调用
java
运行
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class CacheTest {

    @Resource
    private UserService userService;
    @Resource
    private RedisCacheUtil cacheUtil;

    @Test
    void testAnnoCache(){
        // 第一次查数据库，存入缓存
        System.out.println(userService.getUserInfo());
        // 第二次直接走缓存
        System.out.println(userService.getUserInfo());
    }

    @Test
    void testObjCache(){
        userService.saveUserCache();
        System.out.println(cacheUtil.getCacheObj("user:info:2",User.class));
    }
}
七、核心功能亮点
自动 JSON 序列化：直接存实体类、List 集合，不用手动转 JSON
统一过期管理：默认 1 小时，支持自定义秒级过期
注解一键缓存：业务方法加@CacheData自动实现缓存查询 + 存入
五大数据类型全覆盖：String/Hash/List/Set/ZSet
连接池自动管理：无需手动写获取关闭连接
空值防护：空对象不存入缓存，避免缓存穿透
八、项目新增目录
plaintext
com.xxx
├─ annotation
│  └─ CacheData.java       // 缓存注解
├─ aspect
│  └─ CacheAspect.java     // 缓存AOP切面
├─ util
│  └─ RedisCacheUtil.java // 高级缓存工具
九、常用场景推荐
首页热门数据 → 注解缓存
用户信息 → 序列化对象缓存
购物车 → Hash 缓存
消息队列 → List 缓存
排行榜 → ZSet 缓存
点赞去重 → Set 缓存
```

## **3.SpringDataRedis**

https://spring.io/projects/spring-data-redis

| Redis 数据类型           | RedisTemplate 获取方法              | 操作对象类型                 | 常用功能说明                           |
| ------------------------ | ----------------------------------- | ---------------------------- | -------------------------------------- |
| **字符串 String**        | `redisTemplate.opsForValue()`       | `ValueOperations<K,V>`       | set、get、incr、decr、过期时间         |
| **哈希 Hash**            | `redisTemplate.opsForHash()`        | `HashOperations<K,HK,HV>`    | hput、hget、hkeys、hvals、hgetAll      |
| **列表 List**            | `redisTemplate.opsForList()`        | `ListOperations<K,V>`        | lpush、rpush、lpop、rpop、lrange       |
| **集合 Set**             | `redisTemplate.opsForSet()`         | `SetOperations<K,V>`         | sadd、srem、smembers、sismember、scard |
| **有序集合 ZSet**        | `redisTemplate.opsForZSet()`        | `ZSetOperations<K,V>`        | zadd、zrange、zrem、zrank、zscore      |
| **基数统计 HyperLogLog** | `redisTemplate.opsForHyperLogLog()` | `HyperLogLogOperations<K,V>` | pfadd、pfcount                         |
| **地理位置 Geo**         | `redisTemplate.opsForGeo()`         | `GeoOperations<K,M>`         | geoadd、geopos、geodist、georadius     |
| **流 Stream**            | `redisTemplate.opsForStream()`      | `StreamOperations<K,HK,V>`   | xadd、xread、xrange                    |

### 1. Spring Data Redis

**Spring Data Redis** 是 Spring 全家桶里，**专门用来简化 Redis 操作**的框架，属于 **Spring Data** 系列组件。

**SpringDataRedis = Spring 封装好的 Redis 模板工具**

底层默认帮你整合了 **Lettuce**（也可切换 Jedis），不用自己写连接池、不用手动管理连接，直接开箱即用。

### 2.和你刚才写的 Jedis 对比

**1. Jedis**

- Redis **原生 Java 客户端**
- 原生 API，命令和 Redis 一模一样
- 需要**自己手写连接池、工具类、序列化**
- 配置繁琐，项目大了维护麻烦

**2. Spring Data Redis**

- **Spring 二次封装**
- 提供统一模板：`RedisTemplate`
- **自动管理连接池、自动序列化**
- 整合 SpringBoot，**零配置快速使用**
- 企业项目**主流首选**

------

### 3.底层客户端区别（重点）

SpringDataRedis 底层支持两种客户端：

1. **Lettuce（默认）**
   - 基于**Netty**，异步非阻塞
   - 线程安全，**多线程共用一个连接**
   - 无需连接池也够用，性能好
   - SpringBoot2.x+ 默认使用
2. **Jedis（你之前学的）**
   - 同步阻塞
   - **非线程安全**，必须配连接池
   - 老项目常用

**现在企业新项目统一用：SpringDataRedis + Lettuce**

### 4.Spring Data Redis 核心优点

1. 极致简化

   不用写连接池、不用获取关闭连接

2. 自带序列化

   默认 Jdk 序列化，可一键改成 

   JSON 序列化

3. 五大数据类型全覆盖

   ```
   StringRedisTemplate
   ```

   ```
   RedisTemplate
   ```

    直接操作

4. 完美整合 SpringBoot

   yml 一键配置主机、端口、密码、过期时间

5. **支持事务、管道、哨兵、集群**

6. 支持 缓存注解

   ```
   @Cacheable、@CachePut、@CacheEvict
   ```

   直接实现 声明式缓存，比自定义 AOP 更标准

------

### 5.两大核心模板（必记）

**1. StringRedisTemplate（最常用）**

- **只存字符串**
- key、value 都是 String
- **日常开发 90% 场景用它**
- 简单、无乱码、不用改序列化

**2. RedisTemplate<Object,Object>**

- 可以存**任意对象**
- 默认 JDK 序列化，key/value 会乱码
- 一般手动改成 **FastJSON/Jackson JSON 序列化**
- 适合缓存实体类、集合

------

### 6.最简使用流程（SpringBoot）

#### 1. 引入依赖

```
<!-- SpringDataRedis 起步依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

#### 2. yml 极简配置

```
spring:
  redis:
    host: localhost
    port: 6379
    password: #无密码空着
    database: 0
```

#### 3. 直接注入使用

```
@Autowired
private StringRedisTemplate stringRedisTemplate;

//存
stringRedisTemplate.opsForValue().set("name","张三");
//取
String name = stringRedisTemplate.opsForValue().get("name");
```

#### 4. 五大类型对应方法

```
//字符串
opsForValue()
//哈希
opsForHash()
//列表
opsForList()
//集合
opsForSet()
//有序集合
opsForZSet()
```

#### 5.代码示例

```

### 9.1 步骤一：创建 Maven 项目

创建标准的 Spring Boot 项目结构，并配置 `pom.xml`：

​```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
        <exclusions>
            <exclusion>
                <groupId>io.lettuce</groupId>
                <artifactId>lettuce-core</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>redis.clients</groupId>
        <artifactId>jedis</artifactId>
        <version>5.1.0</version>
    </dependency>
    <!-- 其他依赖 -->
</dependencies>
​```

### 9.2 步骤二：配置 RedisTemplate

创建 `RedisConfig.java`，配置 JSON 序列化：

​```java
@Bean
public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.activateDefaultTyping(
        objectMapper.getPolymorphicTypeValidator(),
        ObjectMapper.DefaultTyping.NON_FINAL
    );
    
    GenericJackson2JsonRedisSerializer jsonSerializer = 
        new GenericJackson2JsonRedisSerializer(objectMapper);
    
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(jsonSerializer);
    
    return template;
}
​```

### 9.3 步骤三：创建实体类

​```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private Long id;
    private String username;
    private String email;
    private Integer age;
    private LocalDateTime createTime;
}
​```

### 9.4 步骤四：实现缓存服务

​```java
@Service
public class CacheService {
    
    @Cacheable(cacheNames = "userCache", key = "#userId")
    public User getUserById(Long userId) {
        // 模拟数据库查询
        return User.builder().id(userId).build();
    }
    
    @CachePut(cacheNames = "userCache", key = "#user.id")
    public User updateUser(User user) {
        // 模拟数据库更新
        return user;
    }
    
    @CacheEvict(cacheNames = "userCache", key = "#userId")
    public void deleteUser(Long userId) {
        // 模拟数据库删除
    }
}
​```

### 9.5 步骤五：实现数据结构服务

​```java
@Service
public class RedisDataStructureService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public void setString(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }
    
    public String getString(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }
    
    // 其他数据结构操作...
}
​```

### 9.6 步骤六：编写单元测试

​```java
@SpringBootTest
class SpringDataRedisTest {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private CacheService cacheService;
    
    @Autowired
    private RedisDataStructureService dataStructureService;
    
    @Test
    void testJsonSerialization() {
        User user = User.builder().id(1L).username("张三").build();
        redisTemplate.opsForValue().set("test:user:1", user);
        User result = (User) redisTemplate.opsForValue().get("test:user:1");
        assertNotNull(result);
    }
    
    // 其他测试方法...
}
```



### 7.三者学习顺序（最合理）

1. **原生 Redis 命令**（先会敲命令）
2. **Jedis**（理解底层客户端、连接池原理）
3. **Spring Data Redis**（企业实际开发干活用）

**面试标准答案：**

平时项目中使用 **Spring Data Redis** 操作 Redis，底层默认使用 **Lettuce** 客户端，也可切换为 Jedis；日常业务优先使用`StringRedisTemplate`做字符串缓存，复杂对象缓存自定义`RedisTemplate`实现 JSON 序列化，同时结合 Spring 缓存注解`@Cacheable`实现业务缓存开发。

### 8.简单总结

- **Jedis**：原生手动版
- **SpringDataRedis**：Spring 封装懒人版、企业标准版
- 以后工作**全都用 SpringDataRedis**，几乎没人手写 Jedis 工具类















SpringDataRedis中提供了RedisTemplate工具类，其中封装了各种对Redis的操作。并且将不同数据类型的操作API封装到了不同的类型中： 用表格表示



SpringDataRedis的使用步骤：
引入spring-boot-starter-data-redis依赖
在application.yml配置Redis信息
注入RedisTemplate

进行测试

4.序列化

RedisTemplate可以接收任意Object作为值写入Redis，只不过写入前会把Object序列化为字节形式，默认是采用JDK序列化，得到的结果是这样的

为了节省内存空间，我们并不会使用JSON序列化器来处理value，而是统一使用String序列化器，要求只能存储String类型的key和value。当需要存储Java对象时，手动完成对象的序列化和反序列化。

RedisTemplate的两种序列化实践方案：
方案一：
自定义RedisTemplate
修改RedisTemplate的序列化器为GenericJackson2JsonRedisSerializer
方案二：
使用StringRedisTemplate
写入Redis时，手动把对象序列化为JSON
读取Redis时，手动把读取到的JSON反序列化为对象

5.



# 缓存

**常用三大注解**

1. `@Cacheable`：查询缓存，有缓存不走方法，无则执行并存缓存
2. `@CachePut`：更新缓存，每次都执行方法，同步更新缓存
3. `@CacheEvict`：清除缓存
4. `@Caching`：组合注解

单机的Redis存在四大问题：

![](C:\Users\DELL\Desktop\笔记\数据库\img\Reids\单机存在的问题.png)

## 1.Redis持久化

Redis有两种持久化方案：

- RDB持久化
- AOF持久化

### 1.1.RDB持久化

RDB全称Redis Database Backup file（Redis数据备份文件），也被叫做Redis数据快照。简单来说就是把内存中的所有数据都记录到磁盘中。当Redis实例故障重启后，从磁盘读取快照文件，恢复数据。快照文件称为RDB文件，默认是保存在当前运行目录。

#### 1.1.1.执行时机

RDB持久化在四种情况下会执行：

- 执行save命令
- 执行bgsave命令
- Redis停机时
- 触发RDB条件时

**1）save命令**

执行下面的命令，可以立即执行一次RDB：

![image-20210725144536958](F:/资料/java/12.数据库/黑马Redis-笔记资料/03-高级篇/讲义/01-分布式缓存/assets/image-20210725144536958.png)

save命令会导致主进程执行RDB，这个过程中其它所有命令都会被阻塞。只有在数据迁移时可能用到。

**2）bgsave命令**

下面的命令可以异步执行RDB：

![image-20210725144725943](F:/资料/java/12.数据库/黑马Redis-笔记资料/03-高级篇/讲义/01-分布式缓存/assets/image-20210725144725943.png)

这个命令执行后会开启独立进程完成RDB，主进程可以持续处理用户请求，不受影响。

**3）停机时**

Redis停机时会执行一次save命令，实现RDB持久化。

**4）触发RDB条件**

Redis内部有触发RDB的机制，可以在redis.conf文件中找到，格式如下：

```properties
# 900秒内，如果至少有1个key被修改，则执行bgsave ， 如果是save "" 则表示禁用RDB
save 900 1  
save 300 10  
save 60 10000 
```

RDB的其它配置也可以在redis.conf文件中设置：

```properties
# 是否压缩 ,建议不开启，压缩也会消耗cpu，磁盘的话不值钱
rdbcompression yes

# RDB文件名称
dbfilename dump.rdb  

# 文件保存的路径目录
dir ./ 
```

#### 1.1.2.RDB原理

bgsave开始时会fork主进程得到子进程，子进程共享主进程的内存数据。完成fork后读取内存数据并写入 RDB 文件。

fork采用的是copy-on-write技术：

- 当主进程执行读操作时，访问共享内存；
- 当主进程执行写操作时，则会拷贝一份数据，执行写操作。

![image-20210725151319695](F:/资料/java/12.数据库/黑马Redis-笔记资料/03-高级篇/讲义/01-分布式缓存/assets/image-20210725151319695.png)





#### 1.1.3.小结

RDB方式bgsave的基本流程？

- fork主进程得到一个子进程，共享内存空间
- 子进程读取内存数据并写入新的RDB文件
- 用新RDB文件替换旧的RDB文件

RDB会在什么时候执行？save 60 1000代表什么含义？

- 默认是服务停止时
- 代表60秒内至少执行1000次修改则触发RDB

RDB的缺点？

- RDB执行间隔时间长，两次RDB之间写入数据有丢失的风险
- fork子进程、压缩、写出RDB文件都比较耗时



### 1.2.AOF持久化

#### 1.2.1.AOF原理

AOF全称为Append Only File（追加文件）。Redis处理的每一个写命令都会记录在AOF文件，可以看做是命令日志文件。

![image-20210725151543640](F:/资料/java/12.数据库/黑马Redis-笔记资料/03-高级篇/讲义/01-分布式缓存/assets/image-20210725151543640.png)



#### 1.2.2.AOF配置

AOF默认是关闭的，需要修改redis.conf配置文件来开启AOF：

```properties
# 是否开启AOF功能，默认是no
appendonly yes
# AOF文件的名称
appendfilename "appendonly.aof"
```



AOF的命令记录的频率也可以通过redis.conf文件来配：

```properties
# 表示每执行一次写命令，立即记录到AOF文件
appendfsync always 
# 写命令执行完先放入AOF缓冲区，然后表示每隔1秒将缓冲区数据写到AOF文件，是默认方案
appendfsync everysec 
# 写命令执行完先放入AOF缓冲区，由操作系统决定何时将缓冲区内容写回磁盘
appendfsync no
```



三种策略对比：

![image-20210725151654046](F:/资料/java/12.数据库/黑马Redis-笔记资料/03-高级篇/讲义/01-分布式缓存/assets/image-20210725151654046.png)



#### 1.2.3.AOF文件重写

因为是记录命令，AOF文件会比RDB文件大的多。而且AOF会记录对同一个key的多次写操作，但只有最后一次写操作才有意义。通过执行bgrewriteaof命令，可以让AOF文件执行重写功能，用最少的命令达到相同效果。

![image-20210725151729118](F:/资料/java/12.数据库/黑马Redis-笔记资料/03-高级篇/讲义/01-分布式缓存/assets/image-20210725151729118.png)

如图，AOF原本有三个命令，但是`set num 123 和 set num 666`都是对num的操作，第二次会覆盖第一次的值，因此第一个命令记录下来没有意义。

所以重写命令后，AOF文件内容就是：`mset name jack num 666`



Redis也会在触发阈值时自动去重写AOF文件。阈值也可以在redis.conf中配置：

```properties
# AOF文件比上次文件 增长超过多少百分比则触发重写
auto-aof-rewrite-percentage 100
# AOF文件体积最小多大以上才触发重写 
auto-aof-rewrite-min-size 64mb 
```



#### 1.3.RDB与AOF对比

RDB和AOF各有自己的优缺点，如果对数据安全性要求较高，在实际开发中往往会**结合**两者来使用。

![image-20210725151940515](F:/资料/java/12.数据库/黑马Redis-笔记资料/03-高级篇/讲义/01-分布式缓存/assets/image-20210725151940515.png)

## 2.多级缓存

**1.什么是多级缓存**

传统的缓存策略一般是请求到达Tomcat后，先查询Redis，如果未命中则查询数据库，如图：

![image-20210821075259137](F:/资料/java/12.数据库/黑马Redis-笔记资料/03-高级篇/讲义/02-多级缓存/assets/image-20210821075259137.png)

存在下面的问题：

•请求要经过Tomcat处理，Tomcat的性能成为整个系统的瓶颈

•Redis缓存失效时，会对数据库产生冲击

多级缓存就是充分利用请求处理的每个环节，分别添加缓存，减轻Tomcat压力，提升服务性能：

- 浏览器访问静态资源时，优先读取浏览器本地缓存
- 访问非静态资源（ajax查询数据）时，访问服务端
- 请求到达Nginx后，优先读取Nginx本地缓存
- 如果Nginx本地缓存未命中，则去直接查询Redis（不经过Tomcat）
- 如果Redis查询未命中，则查询Tomcat
- 请求进入Tomcat后，优先查询JVM进程缓存
- 如果JVM进程缓存未命中，则查询数据库

![image-20210821075558137](F:/资料/java/12.数据库/黑马Redis-笔记资料/03-高级篇/讲义/02-多级缓存/assets/image-20210821075558137.png)



在多级缓存架构中，Nginx内部需要编写本地缓存查询、Redis查询、Tomcat查询的业务逻辑，因此这样的nginx服务不再是一个**反向代理服务器**，而是一个编写**业务的Web服务器了**。



因此这样的业务Nginx服务也需要搭建集群来提高并发，再有专门的nginx服务来做反向代理，如图：

![image-20210821080511581](F:/资料/java/12.数据库/黑马Redis-笔记资料/03-高级篇/讲义/02-多级缓存/assets/image-20210821080511581.png)



另外，我们的Tomcat服务将来也会部署为集群模式：

![image-20210821080954947](F:/资料/java/12.数据库/黑马Redis-笔记资料/03-高级篇/讲义/02-多级缓存/assets/image-20210821080954947.png)



可见，多级缓存的关键有两个：

- 一个是在nginx中编写业务，实现nginx本地缓存、Redis、Tomcat的查询

- 另一个就是在Tomcat中实现JVM进程缓存

其中Nginx编程则会用到OpenResty框架结合Lua这样的语言。

# 持久化

# 实战

# Redis 最全最佳实践（面试 + 工作通用）

## 一、基础使用规范

1.**命令规范**

- 开发测试用 `keys *`，**生产绝对禁用**，改用 `scan` 遍历
- 拒绝长事务、大量 `mget/mset` 一次性批量超大键值
- 禁用 `flushall / flushdb` 线上随意执行

**2.键名命名规范**

格式：

```
业务:模块:id
```

示例：

```
user:info:1001
```

```
order:pay:2026
```

- 统一分隔符用冒号 `:`
- 长度不宜过长，简洁易懂
- 不要中文、特殊字符、空格

3.**合理选择 5 大数据类型**

| 业务场景 | 推荐类型 |

|----|----|

| 普通缓存、手机号、配置 | String |

| 购物车、消息队列、浏览记录 | List |

| 好友、点赞、共同关注 | Set |

| 用户信息、商品详情 | Hash |

| 排行榜、积分排序、成绩 | ZSet |

## 二、缓存设计最佳实践

### 1. 三大缓存问题解决方案

1. **缓存穿透**

- 原因：查不存在数据，直打数据库
- 方案：空值缓存、布隆过滤器、接口限流

1. **缓存击穿**

- 原因：热点 key 过期瞬间大量请求打库
- 方案：**永不过期**、互斥锁、热点 key 永续期

1. **缓存雪崩**

- 原因：大量 key 同时过期 / Redis 宕机
- 方案：过期时间加随机值、集群高可用、服务熔断降级

### 2. 过期时间设置

- 缓存统一加过期时间，**禁止大量永久 Key**
- 热门数据：30 分钟～2 小时
- 临时数据：5~10 分钟
- 批量 key 过期时间**加随机偏移**，避免集体失效

## 三、内存优化

1. 禁用大 Key

- String 建议小于 **10KB**
- Hash/List/Set 元素数量控制在千级以内
- 大 key 拆分存储

1. 内存淘汰策略（线上必配）

   优先使用：

plaintext

```
allkeys-lru  # 优先淘汰最近最少使用key
```

1. 关闭无用持久化，纯缓存业务只开 RDB 即可

## 四、持久化最佳实践

1. **纯缓存业务**：只开 RDB，关闭 AOF
2. **数据不能丢业务**：RDB+AOF 双开
3. 不使用 AOF 秒级刷盘，性能极低

## 五、集群与高可用

1. 单机只用于**开发测试**
2. 线上正式环境必用：**主从 + 哨兵** 实现自动故障转移
3. 海量数据分片用 **Redis Cluster 集群**
4. 主库只写，从库只读，读写分离

## 六、业务场景最佳实践

1. **登录验证码**：String + 5 分钟过期
2. **用户登录 token**：String + 30 分钟过期，自动续期
3. **购物车**：Hash 结构存储
4. **朋友圈时间线**：List 链表
5. **点赞 / 好友**：Set 集合
6. **直播间榜单 / 积分排行**：ZSet 有序集合
7. **限流防刷**：String 计数器 + 过期时间

## 七、代码层面规范

1. 统一封装 Redis 工具类，统一序列化方式
2. 操作 Redis 加**异常捕获**，超时重试控制次数
3. 先查缓存，未命中再查数据库
4. 更新数据库**同步更新 / 删除缓存**（先更库再删缓存）
5. 批量操作尽量用 `mget mset` 减少网络 IO

## 八、安全最佳实践

1. 设置 Redis 密码，禁止空密码
2. 禁止外网直接暴露 6379 端口
3. 绑定内网 IP 访问，禁止 0.0.0.0 全网监听
4. 重命名危险命令：`flushdb、keys、config`

## 九、运维监控

1. 监控：内存使用率、客户端连接数、命中率、过期 key 数量
2. 慢查询日志开启，排查慢命令
3. 定期清理无效垃圾 Key
4. 定时备份 RDB 数据

## 十、极简总结（背诵版）

1. 键名规范，类型选对
2. 线上禁用 keys，多用 scan
3. 解决穿透、击穿、雪崩三大问题
4. 严控大 Key，合理使用内存淘汰
5. 线上不用单机，必做哨兵 / 集群
6. 密码 + 内网隔离做好安全
7. 缓存统一加过期，错开过期时间
8. 读写分离，缓存与数据库双写一致

# 原理

## 配置文件

```
一、基础与网络（最常用）
ini
# 包含其他配置文件
include /etc/redis/extra.conf

# 绑定IP，默认只本机
bind 127.0.0.1
# bind 0.0.0.0                # 公网/内网开放（务必配合密码）

# 端口
port 6379

# 保护模式：无密码+bind非0.0.0.0时，只允许本地
protected-mode yes

# 守护进程（后台运行）
daemonize yes

# PID 文件
pidfile /var/run/redis_6379.pid

# 客户端连接超时（秒）
timeout 300

# TCP 保活
tcp-keepalive 300

# 最大客户端连接数
maxclients 10000

# Unix 域套接字（本地高性能）
# unixsocket /tmp/redis.sock
# unixsocketperm 700
二、日志与系统
ini
# 日志级别：debug/verbose/notice/warning
loglevel notice

# 日志文件
logfile /var/log/redis/redis.log

# 系统日志
syslog-enabled no
syslog-ident redis
syslog-facility local0

# 数据库数量（默认16个，0~15）
databases 16
三、安全（必配）
ini
# 设置密码（生产必须）
requirepass yourStrongPassword123

# 重命名危险命令（可选，加固）
rename-command FLUSHDB ""
rename-command FLUSHALL ""
rename-command KEYS ""
四、RDB 持久化（快照）
ini
# 快照触发规则：秒 变更次数
save 900 1
save 300 10
save 60 10000

# 关闭 RDB（纯缓存）
# save ""

# RDB 压缩
rdbcompression yes

# RDB 校验和
rdbchecksum yes

# RDB 文件名
dbfilename dump.rdb

# 数据目录（rdb/aof 存放位置）
dir /var/lib/redis
五、AOF 持久化（日志，数据更安全）
ini
# 开启 AOF
appendonly yes

# AOF 文件名
appendfilename appendonly.aof

# 刷盘策略：always/everysec/no
appendfsync everysec

# 重写时是否停止 fsync
no-appendfsync-on-rewrite no

# AOF 自动重写条件（增长100%，最小64MB）
auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb

# AOF 加载时忽略最后一条不完整指令
aof-load-truncated yes
六、内存管理（生产核心）
ini
# 最大内存（必设，防止 OOM）
maxmemory 2gb

# 内存淘汰策略（满了删谁）
# volatile-lru：过期键LRU（默认）
# allkeys-lru：所有键LRU
# volatile-random：过期键随机
# allkeys-random：所有键随机
# volatile-ttl：过期时间最短
# noeviction：不删，直接报错
maxmemory-policy allkeys-lru

# LRU 采样数
maxmemory-samples 5
七、主从复制（Replication）
ini
# 作为从库，指定主库地址
# replicaof 192.168.1.100 6379

# 主库密码
# masterauth yourStrongPassword123

# 从库只读
replica-read-only yes

# 复制超时
replica-timeout 60

# 无盘复制
replica-diskless-sync no
八、哨兵（Sentinel，高可用）
ini
# 哨兵端口
port 26379

# 监控主库：名称 IP 端口 票数
sentinel monitor mymaster 192.168.1.100 6379 2

# 主库密码
sentinel auth-pass mymaster yourStrongPassword123

# 故障转移超时
sentinel failover-timeout mymaster 180000

# 平行同步数
sentinel parallel-syncs mymaster 1
九、集群（Cluster）
ini
# 开启集群
cluster-enabled yes

# 集群节点配置文件
cluster-config-file nodes-6379.conf

# 节点超时（毫秒）
cluster-node-timeout 15000

# 集群从库迁移
cluster-replica-validity-factor 10
cluster-migration-barrier 1
十、性能与高级
ini
# TCP backlog（高并发调大）
tcp-backlog 511

# 内存碎片整理（Redis 4.4+）
activedefrag yes
active-defrag-ignore-bytes 100mb
active-defrag-threshold-lower 10
active-defrag-threshold-upper 40

# 慢查询日志
slowlog-log-slower-than 10000  # 微秒
slowlog-max-len 128

# 延迟监控
latency-monitor-threshold 0
十一、YAML 与 redis.conf 对应关系（你之前问的）
yaml
# Spring Boot YAML
spring:
  redis:
    host: 175.178.228.138
    port: 6379
    password: yourStrongPassword123  # 对应 requirepass
    database: 0
    timeout: 3s
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 2
快速记忆
网络：bind/port/protected-mode
安全：requirepass（YAML 里是 password）
持久化：save(RDB)、appendonly(AOF)
内存：maxmemory、maxmemory-policy
高可用：replicaof（主从）、sentinel（哨兵）、cluster（集群）
```



# 集群部署

# Redis Stack

[Redis作为矢量数据库快速入门指南 |雷迪斯](https://redis-stack.io/docs/get-started/vector-database/)

## 什么是Redis stack 

**Redis Stack = 普通 Redis + 官方全套增强模块 + 可视化管理面板（RedisInsight）**。

**Redis 核心（OSS）**：普通 Redis 所有功能（字符串、哈希、列表、缓存、分布式锁等）。

**预装 5 大模块（关键）**：

1. **RedisJSON**：原生存 JSON，直接查 / 改 JSON 字段（普通 Redis 只能当字符串存）。
2. **RediSearch**：全文搜索 + 向量检索（做搜索引擎、AI 相似匹配）。
3. **RedisTimeSeries**：时间序列数据（监控、IoT、埋点日志）。
4. **RedisBloom**：布隆过滤器、去重、黑名单、高频统计。
5. **RedisGraph**：图数据库（关系网络、社交、推荐）Redis。

**RedisInsight（Web 面板）**：

- 你刚才映射的 **8001 端口**就是它。
- 浏览器直接访问，可视化看数据、执行命令、监控性能。

## 向量数据库

Redis 做向量数据库，本质是用 **Redis Stack（含 RediSearch）+ Hash/JSON 存向量 + HNSW/FLAT 索引**，直接在 Redis 里实现**向量存储、索引、KNN 检索、混合过滤**。下面从原理、部署、实操到选型，一步到位讲清楚。

| 维度     | Redis 向量库               | Milvus/Pinecone      |
| -------- | -------------------------- | -------------------- |
| **部署** | 极低（复用 Redis）         | 独立集群，重         |
| **延迟** | 内存级，毫秒               | 网络 + 磁盘，较高    |
| **规模** | 千万级以内（HNSW）         | 亿～十亿级           |
| **集成** | 天然融合缓存 / 队列 / 事务 | 只做向量，需额外组件 |
| **成本** | 免费 / 低                  | 付费 / 高            |

## 安装

Windows 没有原生redisstack

使用docker安装

**1安装docker**

```
# 1. 卸载旧版本（如有）
sudo yum remove -y docker docker-client docker-client-latest docker-common docker-latest docker-latest-logrotate docker-logrotate docker-engine

# 2. 安装依赖
sudo yum install -y yum-utils

# 3. 添加 Docker 官方源
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo

# 如果国外源不行使用国内源   删掉 docker 官方 repo
rm -f /etc/yum.repos.d/docker-ce.repo
 # 1.更新
yum clean all
yum makecache
yum update -y

 #2. 安装工具
yum install -y yum-utils curl ca-certificates

 # 3. 加阿里云 docker 源（代替官方）
yum-config-manager --add-repo=https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo


# 4. 安装 Docker
sudo yum install -y docker-ce docker-ce-cli containerd.io

# 5. 启动并开机自启
sudo systemctl enable docker
sudo systemctl start docker

# 6. 验证
docker -v
# 7.让普通用户也生效  执行完必须重新登录终端 才能生效
sudo usermod -aG docker $USER
# 8.docker是否正常运行
docker run hello-world
```

**2配置国内镜像加速**

```
# 备份旧配置（如果有）
cp -a /etc/docker/daemon.json /etc/docker/daemon.json.bak 2>/dev/null

# 写入国内多镜像源
mkdir -p /etc/docker
cat > /etc/docker/daemon.json <<'EOF'
{
  "registry-mirrors": [
    "https://docker.1ms.run",
    "https://docker.m.daocloud.io",
    "https://hub-mirror.c.163.com",
    "https://mirror.aliyuncs.com"
  ]
}
EOF

# 重载并重启docker
systemctl daemon-reload
systemctl restart docker

# 验证是否生效
docker info
```

**3安装redis stack**

```
#1安装
docker run -d \
  --name redis-stack \
  --restart=always \
  -p 6379:6379 \
  -p 8001:8001 \
  -v redis-data:/data \
  redis/redis-stack:latest
  
  
  
#2可视化访问
http://你的LinuxIP:8001

#3国内镜像包
docker run -d \
  --name redis-stack \
  --restart=always \
  -p 6379:6379 \
  -p 8001:8001 \
  -v redis-data:/data \
  docker.1ms.run/redis/redis-stack:latest
```

**4 可视化访问**

http://你的LinuxIP:8001

![image-20260529084611799](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20260529084611799.png)

**5 redis stack 设置密码**

```
docker stop redis-stack
docker rm redis-stack 

docker run -d \
  --name redis-stack \
  --restart=always \
  -p 6379:6379 \
  -p 8001:8001 \
  -v redis-data:/data \
  -e REDIS_ARGS="--requirepass 412826zxyZXY --protected-mode yes --bind 0.0.0.0" \
  redis/redis-stack:latest
```

- `-e REDIS_ARGS="--requirepass 密码"`：给 Redis 设置全局密码Redis

- 端口 `6379`：Redis 连接端口

- 端口 `8001`：RedisInsight Web 管理面板

- 卷 `redis-data`：数据持久化

- `--protected-mode yes`：**开启保护模式**

  `--bind 0.0.0.0`：依然允许外网访问，但必须密码



查看密码设置 

你是用 `-e REDIS_ARGS="--requirepass 密码"` 启动的，密码直接写在容器配置里：

```
docker inspect redis-stack | grep -i requirepass
```

**6 测试连接**

```
三、连接测试（必须带密码）
1. 容器内部连
bash
运行
docker exec -it redis-stack redis-cli
# 进入后认证 如果不认证虽然进去了但是使用不了任何命令
AUTH YourStrongPassword 123
2. 外部机器用 redis-cli
bash
运行
redis-cli -h 你的IP -p 6379 -a YourStrongPassword123
3. RedisInsight Web（8001 端口）
浏览器打开：
plaintext
http://你的IP:8001
添加连接时：
Host: redis-stack 或 localhost
Port: 6379
Username: （空，不要填）
Password: 填你设置的密码
```

7 如何验证自己安装的redisstack

```
# 不进入容器
docker exec -it redis-stack redis-server --version

# 连进 redis-cli 看（
INFO server

MODULE LIST
如果返回 search、json、timeseries、bloom 这些模块，就是 Redis Stack 官方完整版。
```

**8 一键启动脚本**

```
#!/bin/bash

# 定义你自己的密码（可自行修改）
REDIS_PWD="412826zxyZXY"

echo "============================================="
echo "        Docker + Redis Stack 一键安装"
echo "         已内置密码 + 国内加速 + 安全加固"
echo "============================================="

# 1. 卸载旧版本
yum remove -y docker docker-ce docker-ce-cli containerd.io >/dev/null 2>&1

# 2. 安装依赖
yum install -y yum-utils >/dev/null 2>&1

# 3. 阿里云 Docker 源
yum-config-manager --add-repo https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo >/dev/null 2>&1

# 4. 安装 Docker
yum install -y docker-ce docker-ce-cli containerd.io >/dev/null 2>&1

# 5. 启动 Docker
systemctl enable docker >/dev/null 2>&1
systemctl start docker >/dev/null 2>&1

# 6. 配置国内镜像加速器
mkdir -p /etc/docker
cat > /etc/docker/daemon.json <<EOF
{
  "registry-mirrors": [
    "https://docker.1ms.run",
    "https://docker.m.daocloud.io",
    "https://hub-mirror.c.163.com",
    "https://mirror.aliyuncs.com"
  ]
}
EOF

systemctl daemon-reload
systemctl restart docker

# 7. 删除旧 redis-stack
docker rm -f redis-stack >/dev/null 2>&1

# 8. 启动 Redis Stack（带密码 + 强制安全模式）
docker run -d \
  --name redis-stack \
  --restart=always \
  -p 6379:6379 \
  -p 8001:8001 \
  -v redis-data:/data \
  -e REDIS_ARGS="--requirepass $REDIS_PWD --protected-mode yes --bind 0.0.0.0" \
  docker.1ms.run/redis/redis-stack:latest

# 9. 放行防火墙端口
firewall-cmd --add-port=6379/tcp --permanent >/dev/null 2>&1
firewall-cmd --add-port=8001/tcp --permanent >/dev/null 2>&1
firewall-cmd --reload >/dev/null 2>&1

echo ""
echo " ========================================="
echo " 安装完成！"
echo " Redis 地址：IP:6379"
echo " 密码：$REDIS_PWD"
echo " 可视化面板：http://本机IP:8001"
echo " ========================================="
echo ""
```

# Redis 向量数据库demo

## 一、项目概述

本项目是一个基于 **Spring Boot + Redis Stack** 的向量数据库演示项目，实现了文本向量化存储和相似度检索功能。

### 1.1 技术栈

| 技术              | 版本    | 说明                         |
| ----------------- | ------- | ---------------------------- |
| Java              | 17      | 编程语言                     |
| Spring Boot       | 3.2.0   | 应用框架                     |
| Spring Data Redis | 3.2.x   | Redis 数据访问               |
| Redis Stack       | 2.10.20 | 含RedisSearch、RedisJSON模块 |
| Jackson           | 2.15.x  | JSON序列化                   |
| Lombok            | 1.18.x  | 简化代码                     |

### 1.2 项目结构

```
redisstack/
├── src/
│   ├── main/
│   │   ├── java/com/example/redisvector/
│   │   │   ├── RedisVectorApplication.java    # 启动类
│   │   │   ├── config/
│   │   │   │   └── RedisConfig.java           # Redis配置
│   │   │   ├── entity/
│   │   │   │   └── DocData.java               # 数据实体
│   │   │   ├── service/
│   │   │   │   └── RedisVectorService.java    # 核心服务
│   │   │   └── util/
│   │   │       └── VectorUtil.java            # 向量工具
│   │   └── resources/
│   │       └── application.yml                # 配置文件
│   └── test/
│       └── java/com/example/redisvector/
│           └── VectorTest.java                # 测试类
├── pom.xml                                    # Maven配置
└── README.md                                  # 项目说明
```

---

## 二、核心代码解析

### 2.1 启动类

**文件**: `src/main/java/com/example/redisvector/RedisVectorApplication.java`

```java
@SpringBootApplication
public class RedisVectorApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedisVectorApplication.class, args);
    }
}
```

**说明**: 标准的Spring Boot启动类，负责启动应用上下文。

### 2.2 Redis配置类

**文件**: `src/main/java/com/example/redisvector/config/RedisConfig.java`

```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }
}
```

**说明**: 配置RedisTemplate，指定序列化方式为JSON格式。

### 2.3 数据实体类

**文件**: `src/main/java/com/example/redisvector/entity/DocData.java`

```java
public class DocData {
    private String id;           // 文档ID
    private String content;      // 文档内容
    private String category;     // 分类标签
    private List<Float> vector;  // 向量表示(384维)
    
    // Getter/Setter/Constructor/toString
}
```

**说明**: 定义文档数据结构，包含向量字段用于相似度计算。

### 2.4 向量工具类

**文件**: `src/main/java/com/example/redisvector/util/VectorUtil.java`

```java
@Component
public class VectorUtil {

    private static final int VECTOR_DIMENSION = 384;
    private final Random random = new Random();

    public List<Float> textToVector(String text) {
        List<Float> vector = new ArrayList<>(VECTOR_DIMENSION);
        long seed = text.hashCode();
        random.setSeed(seed);
        
        for (int i = 0; i < VECTOR_DIMENSION; i++) {
            vector.add(random.nextFloat());
        }
        return vector;
    }
}
```

**说明**: 将文本转换为384维向量。**注意**: 当前使用基于哈希的随机向量生成（演示用），生产环境应替换为真实的Embedding模型（如Sentence-BERT）。

### 2.5 核心服务类

**文件**: `src/main/java/com/example/redisvector/service/RedisVectorService.java`

#### 2.5.1 创建向量索引

```java
public void createIndex() {
    // 删除旧索引
    redisTemplate.execute((RedisCallback<Object>) connection -> {
        try {
            connection.execute("FT.DROPINDEX", new byte[][]{INDEX_NAME.getBytes()});
        } catch (Exception e) {}
        return null;
    });

    // 创建新索引
    byte[][] createArgs = {
        INDEX_NAME.getBytes(),
        "ON".getBytes(), "JSON".getBytes(),
        "PREFIX".getBytes(), "1".getBytes(), KEY_PREFIX.getBytes(),
        "SCHEMA".getBytes(),
        "$.content".getBytes(), "TEXT".getBytes(),
        "$.category".getBytes(), "TAG".getBytes(),
        "$.vector".getBytes(), "VECTOR".getBytes(),
        "HNSW".getBytes(), "6".getBytes(),
        "TYPE".getBytes(), "FLOAT32".getBytes(),
        "DIM".getBytes(), "384".getBytes(),
        "DISTANCE_METRIC".getBytes(), "COSINE".getBytes()
    };
    
    redisTemplate.execute((RedisCallback<Object>) connection -> {
        connection.execute("FT.CREATE", createArgs);
        return null;
    });
}
```

**索引配置说明**:

- **索引类型**: JSON（存储JSON格式数据）
- **前缀**: `doc:`
- **字段配置**:
  - `$.content`: TEXT类型（全文检索）
  - `$.category`: TAG类型（分类过滤）
  - `$.vector`: VECTOR类型（向量索引）
- **向量参数**:
  - 算法: HNSW（高效近似最近邻搜索）
  - 类型: FLOAT32（单精度浮点）
  - 维度: 384
  - 距离度量: COSINE（余弦相似度）

#### 2.5.2 保存文档

```java
public void saveDoc(String id, String content, String category) {
    DocData data = new DocData();
    data.setId(id);
    data.setContent(content);
    data.setCategory(category);
    data.setVector(vectorUtil.textToVector(content));

    String key = KEY_PREFIX + id;
    
    redisTemplate.delete(key);  // 删除旧数据
    
    String json = objectMapper.writeValueAsString(data);
    
    byte[][] setArgs = {
        key.getBytes(),
        "$".getBytes(),
        json.getBytes(StandardCharsets.UTF_8)
    };
    redisTemplate.execute((RedisCallback<Object>) connection -> {
        connection.execute("JSON.SET", setArgs);
        return null;
    });
}
```

**数据写入流程**:

1. 创建DocData对象并生成向量
2. 删除同名旧数据（避免类型冲突）
3. 序列化为JSON格式
4. 使用`JSON.SET`命令存储

#### 2.5.3 向量检索

```java
public List<Object> search(String queryText, int topK, String filterCategory) {
    List<Float> queryVector = vectorUtil.textToVector(queryText);

    Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
    
    List<Object> filteredResults = new ArrayList<>();
    
    if (keys != null) {
        for (String key : keys) {
            byte[][] getArgs = {key.getBytes(), "$".getBytes()};
            
            String jsonStr = redisTemplate.execute((RedisCallback<String>) connection -> {
                Object result = connection.execute("JSON.GET", getArgs);
                if (result instanceof byte[]) {
                    return new String((byte[]) result, StandardCharsets.UTF_8);
                }
                return null;
            });
            
            if (jsonStr != null) {
                List<DocData> docs = objectMapper.readValue(jsonStr, new TypeReference<List<DocData>>() {});
                if (!docs.isEmpty()) {
                    DocData doc = docs.get(0);
                    double similarity = calculateCosineSimilarity(queryVector, doc.getVector());
                    
                    if (filterCategory == null || filterCategory.isEmpty() || 
                        filterCategory.equals(doc.getCategory())) {
                        List<Object> resultItem = new ArrayList<>();
                        resultItem.add(key);
                        resultItem.add(similarity);
                        resultItem.add(doc);
                        filteredResults.add(resultItem);
                    }
                }
            }
        }
    }
    
    // 按相似度降序排序
    filteredResults.sort((a, b) -> {
        double simA = (Double) ((List<?>) a).get(1);
        double simB = (Double) ((List<?>) b).get(1);
        return Double.compare(simB, simA);
    });
    
    // 返回topK结果
    List<Object> finalResults = new ArrayList<>();
    int count = Math.min(topK, filteredResults.size());
    finalResults.add(count);
    for (int i = 0; i < count; i++) {
        finalResults.add(filteredResults.get(i));
    }
    
    return finalResults;
}
```

**检索流程**:

1. 将查询文本转换为向量
2. 获取所有文档键
3. 逐个读取文档并计算余弦相似度
4. 应用分类过滤
5. 按相似度排序
6. 返回Top-K结果

#### 2.5.4 余弦相似度计算

```java
private double calculateCosineSimilarity(List<Float> vec1, List<Float> vec2) {
    if (vec1.size() != vec2.size()) return 0.0;
    
    double dotProduct = 0.0;
    double norm1 = 0.0;
    double norm2 = 0.0;
    
    for (int i = 0; i < vec1.size(); i++) {
        dotProduct += vec1.get(i) * vec2.get(i);
        norm1 += vec1.get(i) * vec1.get(i);
        norm2 += vec2.get(i) * vec2.get(i);
    }
    
    if (norm1 == 0.0 || norm2 == 0.0) return 0.0;
    
    return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
}
```

**公式**: 
$$\text{similarity} = \frac{\vec{a} \cdot \vec{b}}{\|\vec{a}\| \times \|\vec{b}\|}$$

---

## 三、配置文件

**文件**: `src/main/resources/application.yml`

```yaml
spring:
  data:
    redis:
      host: 175.178.228.138    # Redis服务器地址
      port: 6379               # Redis端口
      database: 0              # 数据库编号
      connect-timeout: 10s     # 连接超时
      password: 412826zxyZXY   # 密码

server:
  port: 8083                   # 应用端口
```

---

## 四、测试类

**文件**: `src/test/java/com/example/redisvector/VectorTest.java`

```java
@SpringBootTest
public class VectorTest {

    @Autowired
    private RedisVectorService vectorService;

    @Test
    public void testVector() {
        // 1. 创建索引
        vectorService.createIndex();

        // 2. 写入测试数据
        vectorService.saveDoc("1", "Redis 向量数据库教程", "tech");
        vectorService.saveDoc("2", "Spring Boot 实战教程", "tech");
        vectorService.saveDoc("3", "春天的花海非常美丽", "life");

        // 3. 向量检索：查询 tech 分类下与"Redis 教程"最相似的2条
        List<Object> result = vectorService.search("Redis 教程", 2, "tech");

        // 4. 输出结果
        System.out.println("\n🔍 检索结果：");
        if (result != null && !result.isEmpty()) {
            int count = Integer.parseInt(result.get(0).toString());
            System.out.println("找到 " + count + " 条结果");
            
            for (int i = 1; i < result.size(); i++) {
                Object item = result.get(i);
                System.out.println("结果 " + i + ": " + item);
            }
        } else {
            System.out.println("未找到结果");
        }
    }
}
```

---

## 五、执行流程详解

### 5.1 完整调用流程

```
┌─────────────────────────────────────────────────────────────────┐
│                      向量数据库调用流程                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 启动应用                                                    │
│     │                                                           │
│     ▼                                                           │
│  2. 创建索引 (createIndex)                                      │
│     │                                                           │
│     ├─> FT.DROPINDEX doc_idx (删除旧索引)                        │
│     │                                                           │
│     └─> FT.CREATE doc_idx ON JSON PREFIX 1 doc:                 │
│         SCHEMA $.content TEXT $.category TAG                    │
│         $.vector VECTOR HNSW 6 TYPE FLOAT32 DIM 384             │
│         DISTANCE_METRIC COSINE                                  │
│                                                                 │
│  3. 写入文档 (saveDoc)                                          │
│     │                                                           │
│     ├─> textToVector(content) → 生成384维向量                   │
│     │                                                           │
│     ├─> DELETE doc:1 (删除旧数据)                               │
│     │                                                           │
│     └─> JSON.SET doc:1 $ {"id":"1","content":"...","vector":[...]}│
│                                                                 │
│  4. 向量检索 (search)                                           │
│     │                                                           │
│     ├─> textToVector(queryText) → 生成查询向量                  │
│     │                                                           │
│     ├─> KEYS doc:* → 获取所有文档键                             │
│     │                                                           │
│     ├─> 遍历每个文档:                                           │
│     │     ├─> JSON.GET doc:1 $ → 读取JSON数据                   │
│     │     ├─> calculateCosineSimilarity → 计算相似度            │
│     │     └─> 应用分类过滤                                      │
│     │                                                           │
│     ├─> 按相似度降序排序                                         │
│     │                                                           │
│     └─> 返回Top-K结果                                           │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 5.2 数据存储格式

**Redis中的存储结构**:

```
Key: doc:1
Value: {"id":"1","content":"Redis 向量数据库教程","category":"tech","vector":[0.123,0.456,...]}

Key: doc:2
Value: {"id":"2","content":"Spring Boot 实战教程","category":"tech","vector":[0.789,0.012,...]}

Key: doc:3  
Value: {"id":"3","content":"春天的花海非常美丽","category":"life","vector":[0.345,0.678,...]}
```

### 5.3 检索结果格式

```java
// 返回List<Object>结构:
// [结果数量, [文档键, 相似度, DocData对象], [文档键, 相似度, DocData对象], ...]

// 示例结果:
// [2, 
//  ["doc:1", 0.85, DocData{id="1", content="Redis 向量数据库教程", ...}],
//  ["doc:2", 0.72, DocData{id="2", content="Spring Boot 实战教程", ...}]
// ]
```

---

## 六、问题与解决方案

### 6.1 问题背景

**原始问题**: Redis Stack 的 KNN 查询语法 `FT.SEARCH idx *=>[KNN 3 @vector $vec AS score]` 在当前版本（2.10.20）中不支持，报错 `Syntax error at offset 1 near >[`。

### 6.2 解决方案

由于 Redis Stack 的原生 KNN 查询功能不可用，采用**客户端向量相似度计算**方案：

| 方案                      | 说明                    | 优缺点                         |
| ------------------------- | ----------------------- | ------------------------------ |
| 方案1：客户端计算         | 在应用层计算余弦相似度  | 简单可靠，但大数据量时性能较差 |
| 方案2：升级Redis Stack    | 升级到支持KNN语法的版本 | 需运维配合，有升级风险         |
| 方案3：使用其他向量数据库 | 如Milvus、Pinecone      | 需要额外部署                   |

**当前实现选择方案1**，适用于中小规模数据场景。

### 6.3 注意事项

1. **向量维度一致性**: 所有文档向量必须保持相同维度（当前为384维）
2. **向量生成方式**: 当前使用随机哈希生成，生产环境需替换为真实Embedding模型
3. **性能优化**: 大数据量时考虑：
   - 使用Redis的Lua脚本减少网络往返
   - 引入专门的向量数据库
   - 实现倒排索引优化

---

## 七、使用方式

### 7.1 编译运行

```bash
# 进入项目目录
cd redisstack

# 编译项目
mvn clean compile

# 运行测试
mvn test -Dtest=VectorTest#testVector

# 打包
mvn clean package

# 运行
java -jar target/redis-vector-demo-1.0.0.jar
```

### 7.2 API调用示例

```java
// 注入服务
@Autowired
private RedisVectorService vectorService;

// 创建索引
vectorService.createIndex();

// 写入文档
vectorService.saveDoc("1", "文档内容", "分类标签");

// 向量检索
List<Object> results = vectorService.search("查询文本", 10, "分类过滤");
```

---

## 八、未来优化方向

1. **替换向量生成方式**: 集成Sentence-BERT等预训练模型生成真实向量
2. **支持批量操作**: 增加批量写入和批量检索接口
3. **性能优化**: 
   - 实现基于Redis Lua的批量相似度计算
   - 考虑引入近似最近邻算法(如FAISS)
4. **API接口封装**: 提供RESTful API供外部调用
5. **监控与日志**: 添加详细的监控指标和日志记录

---

## 九、总结

本项目实现了一个完整的向量数据库演示系统，包含：

1. **索引管理**: 创建支持向量检索的Redis Search索引
2. **数据存储**: 使用Redis JSON模块存储文档数据
3. **向量生成**: 将文本转换为384维向量表示
4. **相似度检索**: 基于余弦相似度的文档匹配
5. **分类过滤**: 支持按分类标签过滤结果

项目采用客户端相似度计算方案，绕过了Redis Stack KNN语法兼容性问题，提供了可靠的向量检索能力。



# 实战案例