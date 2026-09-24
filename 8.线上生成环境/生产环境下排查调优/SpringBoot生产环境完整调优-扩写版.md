> 说明：以下配置适用于 **SpringBoot 2.7.x / 3.x，JDK17（G1/ZGC）**，生产环境，容器 / 物理机通用；**所有参数需要结合服务器实际内存做调整，不要直接复制照搬**。

## 目录

- [一、SpringBoot 生产完整配置](#一springboot-生产完整配置)
- [二、MySQL 生产推荐配置 my.cnf](#二mysql-生产推荐配置-mycnf57--80-通用)
- [三、生产问题排查清单](#三生产问题排查清单cpu-高--内存高--rt-高--gc-频繁)
- [四、压测后调优实操完整步骤](#四压测后调优实操完整步骤)
- [五、Redis 中间件完整配置与调优（含每项设置的原因）](#五redis-中间件完整配置与调优)
- [六、生产坑大全](#六生产坑大全)

## 一、SpringBoot 生产完整配置

### 1. JVM 参数（启动脚本，jar 启动）

> 建议：`-Xms` 与 `-Xmx` 相等，避免运行时堆扩容；服务器内存要预留系统、数据库、中间件内存。 示例：服务器总内存 8G，分配给应用 4G 堆；JDK17 优先 G1，大堆 > 8G 可使用 ZGC。

#### 方案 A：G1GC（通用生产，推荐绝大多数业务）

```
java -jar app.jar \
-Xms4g \
-Xmx4g \
-Xmn1.5g \
-XX:+UseG1GC \
-XX:MaxGCPauseMillis=200 \
-XX:+PrintGCDetails \
-XX:+PrintGCDateStamps \
-XX:+PrintHeapAtGC \
-Xloggc:/data/logs/gc.log \
-XX:+UseGCLogFileRotation \
-XX:NumberOfGCLogFiles=5 \
-XX:GCLogFileSize=100M \
-XX:+HeapDumpOnOutOfMemoryError \
-XX:HeapDumpPath=/data/logs/heapdump.hprof \
-XX:-OmitStackTraceInFastThrow \
-Dspring.profiles.active=prod
```

#### 方案 B：ZGC（大堆，低停顿，JDK17+）

```
java -jar app.jar \
-Xms8g \
-Xmx8g \
-XX:+UseZGC \
-XX:MaxGCPauseMillis=100 \
-Xloggc:/data/logs/gc.log \
-XX:+UseGCLogFileRotation \
-XX:+NumberOfGCLogFiles=5 \
-XX:GCLogFileSize=100M \
-XX:+HeapDumpOnOutOfMemoryError \
-XX:HeapDumpPath=/data/logs/heapdump.hprof \
-XX:-OmitStackTraceInFastThrow \
-Dspring.profiles.active=prod
```

> 关键说明：
>
> 1. `-XX:+HeapDumpOnOutOfMemoryError`：OOM 自动 dump 堆快照，**生产必须开启**，磁盘要预留空间。
> 2. GC 日志滚动，防止单日志无限变大。
> 3. `-XX:-OmitStackTraceInFastThrow`：异常打印完整堆栈，方便排查。
> 4. ⚠️ 勘误：`PrintGCDetails / -Xloggc / UseGCLogFileRotation` 是 JDK 8 参数，JDK 9+ 已废弃（JDK17 下能跑但有告警），应改用 `-Xlog:gc*:file=/data/logs/gc-%t.log:time,uptime:filecount=5,filesize=100m`；G1 下不建议固定 `-Xmn`（详见第六节勘误表）；ZGC 不吃 `MaxGCPauseMillis`（其停顿目标 <1ms 是设计内建，该参数仅对 G1 有效）。

### 2. application-prod.yml HikariCP 数据库连接池配置

> 核心原则：**最大连接数不要盲目设置很大**；数据库服务器能承载的总连接数是上限。 公式参考：`最大连接数 ≈ CPU核心数 * 2 + 磁盘IO线程数`；一般业务 20-50 足够。

```
spring:
  datasource:
    type: com.zaxxer.hikari.HikariDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true
    username: root
    password: xxx
    hikari:
      # 最小空闲连接
      minimum-idle: 5
      # 最大连接池大小
      maximum-pool-size: 30
      # 连接空闲超时，空闲超过该时间释放连接
      idle-timeout: 600000
      # 连接最大生命周期，必须小于mysql wait_timeout
      max-lifetime: 1800000
      # 获取连接超时，拿不到连接直接报错
      connection-timeout: 30000
      # 连接有效性检测
      connection-test-query: SELECT 1
      # 开启池统计，便于监控
      pool-name: HikariPool-Prod
```

> 重点坑：`max-lifetime` 必须小于 MySQL 的`wait_timeout`，否则会出现连接失效，报`Connection is closed`。
> 补充两点：`connection-timeout: 30000` 偏长——拿不到连接干等 30s 会把 Tomcat 工作线程全部挂住，建议改 3000~5000 快速失败；JDBC4 驱动（MySQL 8 驱动）支持 `isValid()` 检测，不需要 `connection-test-query`，配了它反而会禁用更轻量的 isValid 检测。

### 3. 日志配置 logback-spring.xml（生产环境）

生产关闭 DEBUG，只输出 INFO；滚动日志，防止磁盘打满；禁止打印敏感信息。

```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    <include resource="org/springframework/boot/logging/logback/console-appender.xml"/>

    <property name="LOG_PATH" value="/data/logs"/>
    <property name="APP_NAME" value="app"/>

    <!-- 文件输出 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/${APP_NAME}.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/${APP_NAME}-%d{yyyy-MM-dd}.log</fileNamePattern>
            <!-- 保留30天日志 -->
            <maxHistory>30</maxHistory>
            <totalSizeCap>10GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- 生产级别：INFO，关闭DEBUG -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>

    <!-- 第三方框架降低日志级别，避免大量无用日志 -->
    <logger name="org.springframework" level="WARN"/>
    <logger name="com.mysql" level="WARN"/>
    <logger name="org.apache.ibatis" level="WARN"/>
</configuration>
```

> 注意：生产不要开启 `debug=true`，大量日志会打满磁盘、拖慢 IO。
> 高并发服务建议再套一层 AsyncAppender（`neverBlock=true`），日志永远不能阻塞业务线程。

------

## 二、MySQL 生产推荐配置 my.cnf

（5.7 / 8.0 通用）

> 假设服务器物理内存 16G，MySQL 独占；如果是混合部署，需要下调 buffer_pool。

```
[mysqld]
# 基础
datadir=/var/lib/mysql
socket=/var/lib/mysql/mysql.sock
user=mysql
symbolic-links=0
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci

# 最重要：innodb缓冲池，独占机器设置物理内存50-70%
innodb_buffer_pool_size=10G
innodb_log_file_size=2G
innodb_log_buffer_size=256M
innodb_flush_log_at_trx_commit=1
innodb_flush_method=O_DIRECT
innodb_autoinc_lock_mode=2
innodb_thread_concurrency=0

# 连接
max_connections=500
wait_timeout=28800
interactive_timeout=28800

# 慢查询日志
slow_query_log=ON
slow_query_log_file=/var/log/mysql/slow.log
long_query_time=1
log_queries_not_using_indexes=ON

# 临时表、排序
tmp_table_size=256M
max_heap_table_size=256M
sort_buffer_size=2M
join_buffer_size=2M

# 二进制日志
log_bin=/var/log/mysql/mysql-bin
binlog_format=ROW
expire_logs_days=7

# 其他
query_cache_type=0
query_cache_size=0

[mysqld_safe]
log-error=/var/log/mysql/mysqld.log
pid-file=/var/run/mysqld/mysqld.pid
```

> 重要提醒：
>
> 1. `innodb_buffer_pool_size` 是 MySQL 第一优先级参数；**不要设置超过物理内存 80%**。
> 2. `sort_buffer_size / join_buffer_size` 不要调很大，每个连接会单独分配，连接多会爆内存。
> 3. MySQL8.0 已经废弃 query_cache，直接关闭。
> 4. 慢查询日志开启，`long_query_time=1` 记录执行超过 1 秒 SQL。
> 5. `wait_timeout=28800`（8 小时）偏长，配合 HikariCP `max-lifetime` 是安全的；但若 DB 与应用之间有 LB/防火墙，空闲连接可能在 8 小时前就被中间设备静默断掉——这是"偶发 Connection is closed"的另一个来源，必要时将 `wait_timeout` 主动下调并保持 `max-lifetime` 更小。

------

## 三、生产问题排查清单

（CPU 高 / 内存高 / RT 高 / GC 频繁）

### 1. CPU 使用率过高

| 现象 | 排查步骤 |
| ---------------- | ------------------------------------------------------------ |
| CPU 100%，负载高 | 1. top 看进程 PID，确认是 Java 进程；2. `top -Hp <pid>` 找到高 CPU 线程 ID；3. jstack 输出线程栈，把线程 ID 转 16 进制，定位死循环、大量正则、复杂计算；4. 看 GC 日志，是否 GC 线程疯狂占用 CPU（频繁 YGC）；5. 检查是否大量慢 SQL、数据库查询；6. 检查外部接口循环重试、大量日志打印 |

### 2. 内存高（OOM、内存占用持续上涨）

1. 看 JVM 监控：堆内存是否持续上涨，老年代不断增加。
2. 触发 OOM：获取 heapdump 堆快照，使用 MAT 分析；
3. 排查方向：
   - 内存泄漏：静态集合、线程池未关闭、连接未释放、第三方框架缓存；
   - 大对象：一次性加载超大集合、大文件读取；
   - 堆设置过小；
   - 外部堆：DirectByteBuffer 直接内存溢出（NIO）；
4. 命令：`jmap -heap <pid>` 查看堆配置；`jstat -gc <pid> 1000` 实时看 GC。

### 3. RT 接口响应时间高

1. 看监控：接口耗时分布，是大部分慢还是个别接口慢。
2. 区分：是**数据库慢**、**外部 RPC 调用慢**、**内部锁等待**、**GC 停顿 STW**。
3. 排查：
   - 数据库：抓慢 SQL，看是否缺少索引、大事务、锁等待；
   - 外部接口：调用第三方超时、网络抖动；
   - 线程池：线程池打满，队列堆积；
   - GC：STW 停顿时间长；
   - 锁：synchronized、分布式锁竞争；
   - Redis：慢命令（`SLOWLOG GET`）、大 key 网络 serialization、缓存击穿打到 DB。
4. 工具：arthas `trace` 追踪接口耗时；`watch` 观察参数。

### 4. GC 频繁（YGC 频繁 / FullGC 频繁）

1. 查看 GC 日志，确认是 YGC 频繁还是 FGC 频繁。

2. **YGC 频繁**：新生代过小；大量短生命周期对象；

3. 频繁 FullGC：

   - 老年代内存不足；
   - 内存泄漏；
   - 大对象直接进入老年代；
   - 元空间 Metaspace 溢出（大量动态代理、反射）。

4. 命令：`jstat -gc <pid> 1000` 实时观察；

5. 不要无脑加大堆，优先定位根源。

> 必备工具：**Arthas**，生产环境强烈建议集成，不用重启应用即可排查。

------

## 四、压测后调优实操完整步骤

> 原则：**压测环境尽量对齐生产配置；一次只改一个参数；对比压测指标；记录每一次变更；回滚预案**

### 步骤 1：环境对齐

1. 压测环境硬件、JVM 参数、数据库配置、连接池参数，尽量与生产一致。
2. 压测前清空缓存、预热数据库；
3. 监控全部开启：JVM GC、CPU、内存、MySQL 慢查询、HikariCP 连接池、QPS/RT/ 错误率。

### 步骤 2：基准压测

1. 跑基准压测，记录指标：
   - QPS、平均 RT、P95/P99 RT、错误率
   - CPU、内存、GC 情况、数据库 CPU、慢 SQL 数量、连接池使用率
2. 记录基线，后续所有调优都和基线对比。

### 步骤 3：定位瓶颈（优先业务层，再参数调优）

> 优先级：**代码 SQL > 连接池 > JVM > 数据库参数 > OS 参数**

1. 看压测结果：
   - 若 RT 高，错误率上升：优先排查慢 SQL、接口逻辑；
   - 若 CPU 打满：业务计算、GC、数据库；
   - 若连接池打满：HikariCP 最大连接数不足；
   - 若大量 FullGC：堆大小、内存泄漏。

### 步骤 4：迭代调优（每次只改一项）

1. 第一步优化：业务代码、SQL 索引、减少循环查询、批量操作；
2. 第二步：调优连接池（HikariCP 最大连接数）；
3. 第三步：JVM 调优（堆大小、GC 参数）；
4. 第四步：MySQL 参数；
5. 第五步：操作系统内核调优。

> ❗禁止一次性修改多个参数，否则无法定位哪个改动生效。

### 步骤 5：验证效果

每次修改后，重新跑压测，对比指标：

- QPS 是否提升；P99 RT 是否下降；错误率是否降低；
- GC 是否改善；数据库负载是否合理。

### 步骤 6：风险评估 & 生产上线

1. 确认压测结果达标，同时确认**没有引入新的风险**（比如加大连接池，数据库扛不住）。
2. 准备回滚方案：参数备份，回滚配置。
3. 灰度发布：先小流量机器上线，观察监控，再全量。
4. 上线后持续观察：GC、CPU、数据库，观察 24 小时。

### 步骤 7：复盘记录

记录：

- 基线指标；
- 修改的参数；
- 修改前后对比；
- 遇到的问题； 方便后续故障复盘。

------

## 五、Redis 中间件完整配置与调优

> 调优 Redis 前先接受两个事实：**Redis 命令执行是单线程的**（一个慢命令会阻塞所有命令）；**它是内存数据库**（没有 maxmemory 就会一路吃到被 OS 杀死）。下面每一项配置都围绕这两条展开。

### 1. 客户端选型：为什么默认用 Lettuce 而不是 Jedis

| 对比项 | Lettuce（SpringBoot 默认） | Jedis |
| --- | --- | --- |
| 底层 | Netty（NIO） | 阻塞 IO |
| 连接模型 | 连接线程安全，多线程可共用 1 条连接 | 每个线程需要独占 1 条连接 |
| 并发下连接数 | 少（1 条即可支撑高并发） | 多（并发数 = 连接数，必须配池） |
| 支持异步/响应式 | 是 | 否 |

**为什么这重要**：连接数多不只是占 fd，Redis 服务端每个连接都有读写缓冲区，几万条连接本身就是内存负担。Lettuce 在一条连接上可以并发发命令（Redis 协议本身支持 pipeline 交错），天然省资源。

**但仍然要开连接池**——Lettuce 有几类操作必须独占连接：事务（MULTI/EXEC）、阻塞命令（BLPOP）、`RedisTemplate.executePipelined`。不开池的话，这些操作会与其他命令互相阻塞。

### 2. SpringBoot 客户端配置（逐项说明为什么）

SpringBoot 2.7.x 前缀是 `spring.redis`，3.x 是 `spring.data.redis`，其余一致：

```yaml
spring:
  data:
    redis:
      host: redis-master.prod
      port: 6379
      password: ${REDIS_PASSWORD}
      database: 0
      # ---- 超时 ----
      timeout: 3000          # 命令读写超时
      connect-timeout: 2000  # TCP 建连超时
      client-name: order-service   # 连接名，服务端 CLIENT LIST 可见，排查谁连的
      lettuce:
        # ---- 连接池：为什么必须有 ----
        pool:
          max-active: 32     # 最大连接数
          max-idle: 16       # 最大空闲
          min-idle: 8        # 最小空闲（预热，避免突发流量时现建连）
          max-wait: 2000     # 从池里借连接的等待时间
        shutdown-timeout: 100ms
      # ---- 哨兵模式 ----
      # sentinel:
      #   master: mymaster
      #   nodes: sentinel-1:26379,sentinel-2:26379,sentinel-3:26379
```

每个参数为什么这么设：

| 参数 | 值 | 为什么 |
| --- | --- | --- |
| `timeout` | 3000ms | Redis 正常命令耗时 < 1ms；超过 3s 说明 Redis 已经病了（慢命令/大 key/swap），继续等只会挂住更多 Tomcat 线程。**快速失败 + 熔断降级**比傻等强。不要用默认的无限等待 |
| `connect-timeout` | 2000ms | 建连是 TCP 握手 + AUTH，正常毫秒级；2s 还建不上就是网络/认证问题，重试无意义 |
| `max-active` | 32 | **Redis 单实例是单线程执行**：客户端开 500 条连接不会让 Redis 变快，只会增加服务端内存压力。32 条对单实例已经是充足余量（瓶颈在服务端 CPU，不在客户端连接数） |
| `max-idle` / `min-idle` | 16 / 8 | 空闲连接保留一部分做预热；`min-idle` 保证瞬时流量上来不用现场 TCP 握手 |
| `max-wait` | 2000ms | 池耗尽时最多等 2s——和 timeout 同理，池满说明 Redis 或业务出问题了，排队只会雪崩。配合告警"连接池水位 > 80%" |
| `client-name` | 应用名 | 出问题时在 Redis 端 `CLIENT LIST` 一眼看出哪个应用在打我，多服务共用实例时救命 |
| `database` | 0 | **Cluster 模式只支持 db0**；从单机迁集群时用了其他 db 会改代码。一开始就用 0 + key 前缀隔离，未来免迁移 |

### 3. redis.conf 服务端配置逐项解读（为什么）

```
########## 内存 ##########
maxmemory 6gb
maxmemory-policy allkeys-lru

# 为什么必须有 maxmemory：
# 不设上限时，Redis 会吃光物理内存直到被 OOM Killer 干掉——
# 进程被杀 = 所有数据全丢 + 重启后全量重放 AOF，比"淘汰部分缓存"惨得多。
# 设为物理内存的 70-80%，给 fork（持久化/复制需要的子进程）留 COW 内存。

# 为什么缓存场景选 allkeys-lru：
# volatile-lru 只淘汰设了 TTL 的 key——只要有一个业务忘了设 TTL，
# 这些 key 永不淘汰，最终照样 OOM。纯缓存场景所有 key 都可丢，allkeys 最安全。
# 反过来，把 Redis 当存储用（计数、队列、去重表）必须 noeviction：
# 宁可写报错，也不能默默丢数据——很多"数据莫名少了"的事故根因就是 allkeys-lru 用在了存储场景。

########## 线程 ##########
io-threads 4
io-threads-do-reads no

# 为什么：Redis 6+ 依然单线程执行命令，但网络读写和协议解析可交给 io-threads。
# 官方建议：4 核机器给 2-3，8 核给 6，再往上无收益；
# 只有 QPS > 10万 或大量大 value（网络 IO 重）时才有明显收益，普通业务默认 1（关闭）也够。

########## 持久化 ##########
# 场景一：纯缓存（数据可全丢，DB 是唯一真相源）
save ""
appendonly no

# 场景二：缓存 + 重要数据（计数器、去重、锁）——混合持久化
appendonly yes
appendfsync everysec
aof-use-rdb-preamble yes

# 为什么 everysec 而不是 always/no：
# always：每条命令 fsync，最安全但磁盘 IO 拖垮吞吐（普通 SSD 只有几千 fsync/s）；
# no：交给 OS，最快但宕机丢 30s+；
# everysec：折中，最多丢 1 秒数据。缓存业务天然可接受。
# 为什么混合持久化（aof-use-rdb-preamble）：
# 重启恢复时先加载 RDB 快照（快），再重放尾部 AOF（补 1 秒差）——
# 恢复速度接近纯 RDB，数据完整性接近纯 AOF。

########## 惰性删除（大 key 保护）##########
lazyfree-lazy-eviction yes
lazyfree-lazy-expire yes
lazyfree-lazy-server-del yes

# 为什么：同步删除一个 500MB 的大 key，主线程要卡几秒——
# 这几秒内所有命令排队，表现就是"Redis 假死"。
# 开启后删除动作丢给后台线程异步做，主线程立刻返回。

########## 复制 ##########
repl-backlog-size 256mb
client-output-buffer-limit replica 256mb 64mb 60

# 为什么 repl-backlog：主从短暂断线（网络抖动 10s）后，
# 若断点偏移量还在积压缓冲区内，做"部分重同步"（只补差量）；
# 否则触发全量重同步——master 执行 BGSAVE + 全量传输，生产事故级开销。
# backlog 默认 1mb 太小，抖一下就全量，建议 64-256mb。

# 为什么 output-buffer-limit：从库消费慢（或挂了没断开）时，
# master 会为它积压待发送数据 → master 自己 OOM。
# 硬限 256mb 达到直接断开该从库，牺牲从库保 master。

########## 慢查询 ##########
slowlog-log-slower-than 10000   # 微秒，即 10ms
slowlog-max-len 256

# 为什么 10ms：正常命令亚毫秒级。超过 10ms 基本就是大 key 操作、
# keys * 类全量扫描、或网络问题——这些正是要抓的。
# slowlog 只记在内存里，256 条够用且不占资源。

########## 网络 ##########
tcp-keepalive 300
timeout 0

# 为什么 keepalive 300：客户端崩溃（kill -9）来不及断开时，
# 连接变僵尸、fd 泄漏。keepalive 探测 5 分钟清一次死连接。
# timeout 0 = 不主动断空闲连接，交给 keepalive 判断死活——
# 因为中间的 LB/防火墙常在 900s 静默丢弃空闲连接，keepalive 包同时能保活链路。

########## 内存碎片 ##########
activedefrag yes

# 为什么：频繁修改不同大小的 value，jemalloc 分配器会产生碎片——
# used_memory 2G 但 RSS 3G。碎片率 > 1.5 时主动碎片整理能回收内存，
# 代价是少量 CPU。注意需要编译时带 Jemalloc（官方构建默认有）。

########## 危险命令禁用 ##########
rename-command KEYS ""
rename-command FLUSHALL ""
rename-command FLUSHDB ""
rename-command CONFIG ""

# 为什么：KEYS * 是 O(N) 全量阻塞扫描，亿级 key 实例上执行 = 全站冻结几十秒。
# FLUSHALL 曾是勒索软件打裸奔 Redis 的第一招。
# CONFIG 改为随机串而不是空（自己还要用时）。
```

### 4. 缓存三大问题：为什么这么解

| 问题 | 发生机制 | 为什么这个方案有效 |
| --- | --- | --- |
| **穿透**（查不存在的数据） | 请求的 key 在 Redis 和 DB 都不存在 → 每次都打到 DB。恶意用随机 ID 刷接口 = 直接打 DB | **布隆过滤器**：O(1) 判定"一定不存在"，把非法请求挡在 Redis 之前（代价：有小概率误判存在，需定期重建）**空值缓存**：DB 查无此数据时也写入 `key=null`，TTL 60s——同一个坏 key 只打一次 DB |
| **击穿**（热 key 过期瞬间） | 百万 QPS 的热 key 到期 → 数千并发同时 miss → 全部涌向 DB 重建 | **互斥锁重建**：setnx 抢到锁的那 1 个线程查 DB 回填，其余线程短暂等待/返回旧值——把"数千并发查 DB"降为"1 个"**逻辑过期**：物理上永不过期，value 里存过期时间；发现逻辑过期后返回旧值 + 异步线程重建——热 key 永远不会"断供" |
| **雪崩**（大批 key 同时失效 / Redis 挂） | ①缓存集中设置相同 TTL，到期时间也相同，DB 被整批打；②Redis 实例宕机，全量流量穿透 | **TTL 加随机偏移**（`基础时长 + random(0, 600s)`）：把到期时间打散在时间轴上，DB 压力变平缓**多级缓存**：Caffeine 本地一级 + Redis 二级——Redis 整个挂了，本地缓存还能顶住热点流量**集群高可用 + 熔断**：哨兵/Cluster 保可用性；Redis 故障时 Sentinel 熔断降级，宁可拒绝服务也不能打死 DB |

一句话记忆：**穿透是"查不存在的"，击穿是"一个热 key 过期"，雪崩是"一大批同时失效"**。

### 5. 大 key / 热 key：为什么是性能杀手

**大 key 为什么危险**（不是占内存一件事）：

1. 读写大 value 时，单线程要做完整的序列化/网络传输——10MB 的 value 每次读都占住主线程几十毫秒，**所有命令跟着排队**；
2. `DEL` 一个大 key 是同步释放内存，GB 级大 key 能卡几秒（用 `UNLINK` + lazyfree 解决）；
3. 集群模式下大 key 偏斜在单个分片，该分片 CPU/带宽先爆；
4. 大 key 所在实例做 RDB/AOF rewrite 时，fork 和写盘都更重。

**发现**：

```bash
redis-cli --bigkeys                     # 低峰在线扫（采样，温和）
redis-cli --memkeys                     # 按内存排（6.0+）
MEMORY USAGE user:1001                  # 精确看单个 key
# 离线精确分析：redis-rdb-tools 解析 RDB 文件出报表
```

**治理标准**：

| 类型 | 阈值 | 治理手段 |
| --- | --- | --- |
| String | < 10KB | 大 JSON 拆结构 |
| Hash/Set/ZSet | 元素 < 5000、总内存 < 10MB | 按 hash 分片：`key:{1..100}`，先算路由再读写 |
| List | 元素 < 5000 | 拆分或改用 stream |

**热 key 为什么危险**：集群按 key 分片，**同一个 key 的百万 QPS 只落在单个分片上**——16 个分片只有 1 个 CPU 100%，其余全闲。扩容分片无效。

治理：①本地 Caffeine 缓存兜住（读多写少场景最优）；②key 复制：写时同步写 `key:1..N`，读随机挑一个，流量摊到 N 个分片；③监控指标 `redis_instance:qps` 分布的方差。

### 6. 分布式锁：为什么每一句都不能省

错误写法（每一行都有对应的事故）：

```java
// 反例
jedis.setnx("lock:order:1", "1");        // 崩在这句和下句之间 → 锁永不过期，死锁
jedis.expire("lock:order:1", 30);
// ...
jedis.del("lock:order:1");                // A 的锁已过期、B 已拿到锁 → A 删掉了 B 的锁
```

正确姿势一：**SET 原子加锁 + Lua 原子解锁**：

```java
// 加锁：一条命令完成"不存在才设置 + 过期时间"，不给死锁留窗口
String token = UUID.randomUUID().toString();
redisTemplate.execute((conn) -> conn.set(
    "lock:order:1".getBytes(), token.getBytes(),
    Expiration.seconds(30),
    RedisStringCommands.SetOption.SET_IF_ABSENT));   // SET key token NX EX 30

// 解锁：必须校验 value 是自己的才能删（Lua 保证"读-比-删"原子性）
String script =
    "if redis.call('get', KEYS[1]) == ARGV[1] then " +
    "  return redis.call('del', KEYS[1]) " +
    "else return 0 end";
redisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
    List.of("lock:order:1"), token);
```

- 为什么 value 用随机 token：防止删掉别人的锁（业务超时后锁自动过期，别人已持有）。
- 为什么解锁必须 Lua：GET 比较 + DEL 是两步，中间锁可能刚好过期易主；Lua 在 Redis 单线程里原子执行。

正确姿势二：**Redisson（生产推荐）**：

```java
RLock lock = redisson.getLock("lock:order:" + orderId);
lock.lock(10, TimeUnit.SECONDS);   // 或 tryLock(waitTime, leaseTime, unit)
try {
    // 业务
} finally {
    lock.unlock();
}
```

- 为什么用 Redisson：内置**看门狗（watchdog）**——不指定 leaseTime 时默认锁 30s，后台线程每 10s 自动续期；业务没跑完锁不会提前过期，业务进程挂了 watchdog 一起死、锁 30s 后自动释放。解决了"拍脑袋估业务耗时"的难题。
- 认识边界：Redis 锁（含 Redisson）保证的是**效率**（防重复执行），不保证**绝对正确**（主从切换瞬间锁可能丢失）。强一致场景（资金扣减）用 `RedLock` 争议也很大，直接上数据库乐观锁 / ZooKeeper / etcd。

### 7. 批量与脚本：为什么、什么时候用

| 手段 | 适用 | 为什么 |
| --- | --- | --- |
| `MGET/MSET/HMGET` | 批量读写同一结构 | 单命令原子执行；比 pipeline 更少协议开销 |
| pipeline | 批量异构命令 | N 次网络往返压成 1 次（RTT 是局域网 0.5~1ms 的主要成本）。**单批 ≤ 1000 条**：Redis 同步执行整批，超大 pipeline 会阻塞其他客户端，客户端本身也要缓冲整个响应 |
| Lua 脚本 | 多步原子操作（校验+写、限流计数） | 脚本在 Redis 单线程内原子执行，天然免锁。**脚本必须短小**，长脚本 = 大慢命令 |
| `SCAN` | 全量遍历 | 游标分批、每批之间让出主线程，替代 `KEYS *` 的阻塞扫描。注意 count 只是指引，遍历期间 key 变化可能重复/遗漏，需业务幂等 |

### 8. Spring Cache 使用规范

```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 1800        # 必设！默认永不过期
      cache-null-values: true   # 防穿透（缓存 null）
      key-prefix: "order:"
```

| 规范 | 为什么 |
| --- | --- |
| 必须设 TTL | 默认永久有效 → 缓存无限膨胀 → maxmemory 打满 → 静默淘汰热数据（LRU 淘汰的是"最久未用"，不是"最久未写"） |
| `cache-null-values: true` | 空结果也缓存，否则不存在的 ID 每次穿透到 DB |
| 手动指定 key | 默认 key 含方法签名和参数序列化，又长又脆（改方法名缓存全失效） |
| 序列化用 GenericJackson2 或 GenericFastJson | JDK 序列化体积大 3~5 倍且跨语言不可读；注意带泛型的对象反序列化需要启用类型信息 |
| 慎用 `@CacheEvict(allEntries=true)` | 清整个命名空间 = 雪崩式回源；宁可按 key 逐个失效 |

### 9. 哨兵 vs Cluster 怎么选

| 维度 | 主从 + 哨兵 Sentinel | Cluster 集群 |
| --- | --- | --- |
| 容量 | 单机内存上限 | 水平扩展（按 slot 分片） |
| 故障转移 | 哨兵投票，10~30s | 节点间 gossip，秒级 |
| 客户端复杂度 | 低（识别哨兵即可） | 高（MOVED 重定向、slot 感知） |
| 多 key 操作 | 随意 | 必须同 slot（hash tag `{user1001}:orders` 强制同 slot） |
| SELECT db | 支持 | **只支持 db0** |
| 适用 | 数据 < 单机内存、简单高可用 | 数据量大、写扩展、海量 key |

**为什么 Cluster 限制多 key**：key 按 CRC16 分散在 16384 个 slot、slot 分布在不同节点——跨节点的"原子操作"在分布式下不存在。hash tag 让相关 key 落同一 slot 才能用事务/multi-key 命令，代价是这些 key 无法再分片。

### 10. Redis 监控指标清单（为什么盯这些）

| 指标 | 来源 | 为什么盯 |
| --- | --- | --- |
| 命中率 `keyspace_hits/(hits+misses)` | INFO stats | **缓存的价值度量**。< 90% 要查：TTL 太短、key 设计不合理、容量不够被 LRU 提前淘汰 |
| `evicted_keys` 增速 | INFO stats | 持续增长 = maxmemory 不够或内存泄漏的大 key 在挤占 |
| `instantaneous_ops_per_sec` | INFO stats | 容量水位；配合集群各分片方差看热 key |
| `connected_clients` / `blocked_clients` | INFO clients | 客户端连接泄漏；blocked 高 = BLPOP 类命令堆积 |
| `used_memory` / `mem_fragmentation_ratio` | INFO memory | 碎片率 > 1.5 需要关注（jemalloc 碎片或大量删改） |
| `latest_fork_usec` | INFO stats | 最近一次 fork 耗时（微秒）；大内存实例 fork 能到秒级，fork 期间主线程阻塞 |
| `master_link_status` / 复制偏移差 | 从库 INFO replication | 主从延迟决定"写后立读"走主还是走从 |
| 慢查询 `SLOWLOG GET` | — | 大 key、keys *、误用命令的直接证据 |
| `aof_delayed_fsync` | INFO persistence | 磁盘 IO 跟不上 everysec，出现延迟 fsync（主线程被拖慢的前兆） |
| 持久化/全量同步发生时刻 | 日志 | 生产 99% 的 Redis 卡顿 = 大 key 删除、fork、全量重同步三件事 |

------

## 六、生产坑大全

### 1. Redis 专属坑

| # | 坑 | 现象 | 根因与规避 |
| --- | --- | --- | --- |
| 1 | 生产执行 `KEYS *` | 全站 Redis 命令冻结几十秒 | O(N) 阻塞扫描。禁用命令（rename-command）+ 遍历一律 `SCAN` |
| 2 | `DEL` 大 key 卡死 | 删一个 GB 级 hash 后 Redis 假死几秒 | 同步释放内存阻塞主线程。用 `UNLINK` + 开 lazyfree；大 hash 用 `HSCAN`+`HDEL` 分批删 |
| 3 | 不设 `maxmemory` | 某天 Redis 进程消失，所有缓存全丢 | OS OOM Killer。必须设上限 + 淘汰策略；OOMKilled 的进程用 `dmesg` 能查到记录 |
| 4 | 拿缓存当存储用还配了 `allkeys-lru` | 计数器、去重表数据"莫名消失" | 淘汰策略与用途不匹配。存储场景 `noeviction`，或干脆放 DB |
| 5 | `volatile-lru` + 忘设 TTL 的 key | 缓存内存只涨不降直至 OOM | volatile 系策略只淘汰有 TTL 的 key；用 `redis-cli --bigkeys` + 定期审计 |
| 6 | `SET` 覆盖后 TTL 丢失 | key 永不过期，内存泄漏 | **SET 会清掉原 key 的 TTL**。更新要保值用 `SET key val KEEPTTL`，或改 HSET 局部更新 |
| 7 | 双写不一致 | 改了 DB，缓存还是旧值 | 缓存一致性靠"更新 DB + 删缓存"，不是改缓存；删失败要补偿（重试/订阅 binlog 的 Canal 异步删）；接受最终一致而不是强一致 |
| 8 | 缓存-DB 循环依赖雪崩 | Redis 一挂全站瘫 | Redis 挂 → 全量穿 DB → DB 慢 → 线程池耗尽 → 健康检查也超时。对 Redis 调用配熔断降级 + 本地缓存兜底热点 |
| 9 | 裸奔 6379 无密码 | 数据被 FLUSHALL、被写入挖矿脚本 | 公网暴露的 Redis 是勒索重灾区。密码 + 内网 + rename 危险命令三件套 |
| 10 | AOF rewrite 发生在高峰 | 大内存实例周期性卡顿 | fork 需要复制页表，10G 实例 fork 可达秒级。磁盘慢还会 aof_delayed_fsync。容量规划留 30% 内存给 COW，rewrite 避开高峰（`auto-aof-rewrite-percentage` 触发时机不可控时手动凌晨触发） |
| 11 | 主从全量重同步风暴 | master 重启后网络被打满 | 默认 backlog 1mb 太小，抖一下就全量。调大 `repl-backlog-size`；从库逐台重启错峰 |
| 12 | INCR 生成唯一 ID 丢号 | 重启后 ID 重复 | RDB/AOF 间隔内的增量丢失。ID 生成要 DB 序列/雪花算法，Redis 计数只做加速展示 |
| 13 | 集群多 key 命令报 CROSSSLOT | MSET/事务/Lua 多 key 报错 | key 分布在不同 slot。hash tag `{}` 强制同 slot；或拆成单 key 操作 |
| 14 | 从库读旧数据 | 写后立读不一致 | 主从异步复制有延迟。写后立读强制走主（读写分离只给容忍延迟的读） |
| 15 | LB/防火墙静默断空闲连接 | 偶发第一条命令挂起超时 | 中间设备 900s 回收 TCP 但双方不知情。`tcp-keepalive 300` + 客户端心跳保活 |

### 2. 连接池与超时坑（全链路）

| # | 坑 | 现象 | 规避 |
| --- | --- | --- | --- |
| 16 | HikariCP `max-lifetime` ≥ MySQL `wait_timeout` | 偶发 `Connection is closed` | max-lifetime 至少小 30s，且要小于中间 LB 的空闲超时 |
| 17 | `connection-timeout` 设 30s+ | 拿不到连接时线程成批挂住，RT 全线飙升 | 3~5s 快速失败 + 池水位告警 |
| 18 | 微服务连接池总和 > MySQL max_connections | 数据库连接打满，全站不可用 | 实例数 × maximum-pool-size 统计全局容量，控制在 max_connections 的 75% 内 |
| 19 | Lettuce 不开池还用事务/BLPOP | 命令互相阻塞甚至饿死 | 阻塞类操作需独占连接，必须开 pool |
| 20 | 超时预算倒挂（上游 < 下游） | 上游已超时返回，下游还在白跑占用资源 | 网关超时 > Feign 超时 > Redis/DB 超时，自外向内收紧 |

### 3. JVM 坑（含本文档开头配置的勘误）

| # | 坑 | 现象 | 规避 |
| --- | --- | --- | --- |
| 21 | **G1 配 `-Xmn` 固定新生代**（本文档方案 A 即此问题） | G1 自适应失效，停顿目标失控 | G1 下删掉 `-Xmn`，交给自适应；除非压测证明特定场景有收益 |
| 22 | **JDK17 用 JDK8 的 GC 日志参数**（方案 A/B 的 `-Xloggc` 等） | 能跑但有告警，未来版本移除 | JDK9+ 统一用 `-Xlog:gc*:file=...` |
| 23 | **ZGC 配 `MaxGCPauseMillis`**（方案 B） | 参数无效 | ZGC 停顿 <1ms 是设计内建不可调；该参数只对 G1 有意义 |
| 24 | 开了 HeapDump 但没监控磁盘 | OOM 时 dump 失败或磁盘被打满 | dump 目录独立挂盘 + 磁盘告警；dump 体积 ≈ 堆大小 |
| 25 | 容器里堆 = 容器 limit | OOMKilled（退出码 137） | JVM 实际内存 = 堆 + 元空间 + 线程栈 + 直接内存，用 `MaxRAMPercentage` 控制在 75% 内 |
| 26 | 不开 GC 日志 | 出问题靠猜 | `-Xlog:gc*` 开销 < 1%，生产常开 |

### 4. MySQL 坑

| # | 坑 | 现象 | 规避 |
| --- | --- | --- | --- |
| 27 | `sort_buffer_size` 调大"优化排序" | 连接一多内存爆炸 | 每连接独占分配；保持默认 2M 级别，靠索引消除 filesort |
| 28 | 大事务包 RPC/发消息 | 锁持有时间长、主从延迟飙升 | 事务只包 DB 操作；外部调用移出事务 |
| 29 | 隐式类型转换索引失效 | 字符串列传数字，全表扫描 | `WHERE phone = 13800000000` 改 `'13800000000'`；EXPLAIN 看 type=ALL |
| 30 | `LIMIT 1000000, 20` 深分页 | 扫百万行丢弃 | 游标分页 `WHERE id > ? LIMIT 20` |

### 5. 压测与上线坑

| # | 坑 | 现象 | 规避 |
| --- | --- | --- | --- |
| 31 | 空表压测 | 压测 1 万 QPS，上线秒变 100 | 数据量级、热点分布、缓存命中率必须对齐生产 |
| 32 | 压测不带监控 | 只知道 QPS 高，不知道瓶颈在哪 | 压测全程盯 GC、慢 SQL、连接池、中间件指标 |
| 33 | 一次改一堆参数 | 效果无法归因，回滚都不知道回哪个 | 一次一个变量，改前留基线 |
| 34 | 生产直改不灰度 | 出问题全量故障 | 灰度 1 台 → 观察监控 → 全量；配置中心改参记录旧值 |
| 35 | 只压应用不压依赖 | 应用扛住了，DB/Redis 先倒 | 全链路压测 + 影子表，依赖中间件一起观测 |

> 全文一条主线：**监控先行 → 定位瓶颈 → 业务与 SQL 层收益最大 → 参数微调 → 压测对比 → 灰度上线**。参数是最后一环，坑大全里一半的事故，都是跳过前面几环直接改参数造成的。
