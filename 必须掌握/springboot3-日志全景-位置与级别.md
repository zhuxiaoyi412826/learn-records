# 线上系统日志全景：位置、开关与级别配置

> 姊妹篇：《springboot3-日志输出清单.md》讲的是**业务代码里在哪打日志**；本文讲的是**整个系统的日志版图**——JVM、MySQL、Redis、Nginx、容器各自的日志在哪、怎么开、什么时候看。
> 一个关键认知：**日志级别（ERROR/WARN/INFO/DEBUG）是应用层概念**，由 SLF4J/Logback 控制；基础设施日志（GC、MySQL、Redis）不走这套体系，各有自己的开关和详细度配置。

---

## 一、全景总览表（收藏这张）

| 层 | 日志 | 位置 / 开关 | 级别体系 | 排查场景 |
| ---- | ---- | ---- | ---- | ---- |
| 应用层 | 业务日志 | `/data/logs/app.log` | logback 级别 | 一切问题的主入口 |
| 应用层 | 框架日志 | `logging.level.*` | logback 级别 | 框架内部行为 |
| 应用层 | 接口访问日志 | Tomcat accesslog | 无级别（全量记录） | 流量、状态码、耗时分位 |
| JVM 层 | GC 日志 | `-Xlog:gc*` 启动参数 | tags 控制详细度 | 频繁 Full GC、停顿 |
| JVM 层 | 崩溃日志 | `hs_err_pid.log` 自动生成 | 无 | JVM 进程直接死亡 |
| JVM 层 | 堆转储 | `.hprof` | 无 | OOM 分析 |
| 中间件层 | MySQL 错误日志 | `log_error` 变量 | 无（全量） | 启动失败、死锁 |
| 中间件层 | MySQL 慢查询 | `slow_query_log` | 阈值 `long_query_time` | 慢 SQL 定位 |
| 中间件层 | MySQL binlog | `log_bin` | 无 | 数据恢复、主从、Canal |
| 中间件层 | Redis 服务日志 | `logfile` 配置 | `loglevel`（debug~warning） | 持久化失败、内存淘汰 |
| 中间件层 | Redis 慢日志 | `slowlog`（内存） | 阈值（微秒） | 慢命令、大 key |
| 中间件层 | Nginx access/error | `logs/access.log` | 无 / 级别 | 502/504、攻击分析 |
| 系统层 | OOM killer 记录 | `dmesg`、`/var/log/messages` | 无 | 进程无声消失 |
| 系统层 | 容器日志 | `docker logs` | 无 | 容器内 stdout/stderr |
| 系统层 | CI 流水线日志 | GitHub Actions 页面 | 无 | 构建、部署失败 |

---

## 二、应用层日志

### 1. 业务日志

见《springboot3-日志输出清单.md》，此处只补 logback 生产配置骨架：

```yaml
logging:
  file:
    path: /data/logs           # app.log + 滚动文件落这里
  level:
    root: info
```

### 2. 框架日志（logging.level 按包调级别）

这是"框架日志"的核心配置——控制 Spring、连接池、MyBatis 这些框架往你的日志里输出什么：

```yaml
logging:
  level:
    root: info                          # 全局默认
    com.xxx: info                       # 自己的业务代码
    com.xxx.mapper: debug              # SQL 日志（仅 dev！生产关）
    org.springframework: warn           # Spring 内部噪音压掉
    org.springframework.boot.autoconfigure: warn
    com.zaxxer.hikari: info             # 连接池
    com.zaxxer.hikari.pool: debug      # 排连接池问题时临时开
    com.baomidou.mybatisplus: warn
    org.apache.tomcat: warn
    io.lettuce: warn                    # Redis 客户端
    RocketmqClient: warn
```

常用开关场景：

| 想看什么 | 临时调整 |
| ---- | ---- |
| SQL 到底执行没执行 | `com.xxx.mapper: debug` |
| 连接池耗尽根因 | `com.zaxxer.hikari: debug` + `leak-detection-threshold` |
| 自动装配为什么没生效 | 启动加 `--debug`，看 Conditions Evaluation Report |
| 运行时改级别（不重启） | `POST /actuator/loggers/com.xxx.mapper {"configuredLevel":"DEBUG"}` |

### 3. Tomcat 接口访问日志（access log）

比 AOP 更早一层的记录：请求到没到应用、状态码分布、原始耗时。**生产建议开启**：

```yaml
server:
  tomcat:
    accesslog:
      enabled: true
      directory: /data/logs/tomcat
      pattern: '%h %l %u %t "%r" %s %b %D'
      # %h 客户端IP  %t 时间  %r 请求行
      # %s 状态码  %b 响应字节  %D 处理耗时(ms)
```

用途：统计 QPS、找异常 IP 扫描、确认"请求根本没到应用"这类问题（Nginx 有记录、access log 没有 = 请求没进来）。

### 4. 连接池泄漏日志

```yaml
spring:
  datasource:
    hikari:
      leak-detection-threshold: 60000   # 连接借出超60秒未还，打印持有堆栈
```

输出进 `com.zaxxer.hikari` 的 WARN 日志——连接池耗尽排查的第一手证据。

---

## 三、JVM 层日志（最常被忽略的三件套）

### 1. GC 日志

**JDK 9+（含 17/21）统一 Xlog 体系：**

```bash
-Xlog:gc*:file=/data/logs/gc-%t.log:time,uptime,level,tags:filecount=5,filesize=50m
```

解读：`gc*` 所有 GC 相关标签 / `filecount+filesize` 自动滚动 5 个 50M 文件，防止爆盘。

**JDK 8 老参数（对照认识即可）：**

```bash
-XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:/data/logs/gc.log
-XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=50M
```

**怎么看**——抓两样东西：

```
[2026-09-08T10:15:32] GC(42) Pause Young (G1 Evacuation Pause) 512M->64M(1024M) 15ms
                     ↑第42次Young GC       回收前512M→回收后64M(总堆1024M)   停顿15ms
```

- **Full GC 频率**：一天几十次 = 内存有问题（泄漏或堆太小）
- **停顿时间**：单次 >1s 或频繁 >200ms = 接口抖动元凶
- 图形化分析：GCEasy（上传 gc.log 免费分析）、GCViewer，或 Arthas `dashboard` 实时看

**生产必开**。它不产生业务开销（量极小），但没有它，"接口周期性卡顿是不是 GC 导致"这种问题永远靠猜。

### 2. 崩溃日志（hs_err_pid.log）

- **什么时候产生**：JVM 自身发生致命错误（SIGSEGV 段错误、内部断言失败）时，进程死亡前自动在工作目录生成 `hs_err_pid<进程号>.log`
- **注意区分**：OOM 是 Java 层异常（JVM 还活着，会打业务日志）；hs_err 是 JVM 本身挂了（业务日志戛然而止，这个"戛然而止"本身就是证据）
- **预配置**：`-XX:ErrorFile=/data/logs/hs_err_%p.log`
- **看哪里**：文件头 `# SIGSEGV` 错误类型、`Current thread` 栈帧、`C heap` 内存映射
- **常见原因**：JIT 编译 bug、JNI/native 库（压缩库、加密库）、被系统信号误杀

### 3. 堆转储（.hprof）

```bash
# OOM 自动抓现场（保险，必配）
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/dump/

# 手动抓
jmap -dump:format=b,file=/data/dump/heap.hprof <pid>
```

用 MAT（Eclipse Memory Analyzer）打开 → Dominator Tree 找最大对象。详见排查手册第三章。

### 4. JFR（一句话）

JDK 17 免费的飞行记录器：`-XX:StartFlightRecording=duration=10m,filename=rec.jfr`，低开销持续录制 CPU/内存/锁/线程，Mission Control 打开。进阶工具，先知道存在即可。

---

## 四、MySQL 日志体系（六种，面试高频）

| 日志 | 作用 | 默认 | 生产建议 |
| ---- | ---- | ---- | ---- |
| 错误日志 error log | 启动失败、运行错误、**死锁信息** | **开** | 必开，排 DB 问题第一现场 |
| 慢查询日志 slow log | 超过阈值的 SQL 落盘 | 关 | **必开**，线上慢 SQL 唯一来源 |
| 通用查询日志 general log | 记录所有 SQL | 关 | 临时抓包用，性能杀手，用完就关 |
| binlog（归档日志） | 数据变更流水：主从复制、数据恢复、Canal 订阅 | 关（可开） | 按需开，DBA 管理 |
| redo log | InnoDB 崩溃恢复，保证已提交事务不丢（crash-safe） | 开 | 引擎内部，不能关 |
| undo log | 事务回滚 + MVCC 旧版本读 | 开 | 引擎内部，不能关 |

### 慢查询日志配置（my.cnf）

```ini
[mysqld]
slow_query_log = ON
slow_query_log_file = /data/mysql/slow.log
long_query_time = 1                 # 超过1秒记录
log_queries_not_using_indexes = ON  # 没走索引的也记（量大再开）
```

### 排查命令

```sql
SHOW VARIABLES LIKE '%slow_query%';      -- 确认开关和路径
SHOW VARIABLES LIKE 'long_query_time';
```

```bash
# 慢日志聚合分析：按总耗时取 Top 10
mysqldumpslow -s t -t 10 /data/mysql/slow.log
# 更强的：pt-query-digest slow.log
```

### 临时开 general log 抓全量 SQL

```sql
SET GLOBAL general_log = 'ON';          -- 抓完立刻关
```

### 死锁排查入口

错误日志 + `SHOW ENGINE INNODB STATUS` 的 `LATEST DETECTED DEADLOCK` 段（详见排查手册第五章）。

---

## 五、Redis 日志

### 1. 服务日志（redis.conf）

```conf
loglevel notice                    # debug / verbose / notice / warning
logfile "/data/redis/redis.log"    # 空字符串 = 输出到 stdout（docker 场景用 docker logs 看）
```

看什么：RDB/AOF 持久化失败告警、内存达到 maxmemory 后的淘汰行为、主从复制断连。

### 2. 慢日志（重点，和 MySQL 完全不同）

```conf
slowlog-log-slower-than 10000     # 单位微秒！10000 = 10ms
slowlog-max-len 128               # 最多存 128 条
```

```bash
SLOWLOG GET 10        # 查最近 10 条慢命令
SLOWLOG RESET         # 清空
```

**关键差异：Redis 慢日志存在内存里，不落盘、重启即丢**——出了抖动问题必须立刻看，不能事后补。

配套：`redis-cli --bigkeys` 找大 key、`SLOWLOG` 里常见 `KEYS *`、大集合操作、Lua 长脚本。

---

## 六、Nginx 日志

```nginx
http {
    logformat main '$remote_addr - $time_local "$request" '
                   '$status $body_bytes_sent $request_time $upstream_response_time';
    access_log /data/nginx/logs/access.log main;
    error_log  /data/nginx/logs/error.log warn;    # debug/info/notice/warn/error/crit
}
```

**最有价值的一对字段**：

| 字段 | 含义 | 用途 |
| ---- | ---- | ---- |
| `$request_time` | Nginx 收到请求到发完响应的总耗时 | 判断"整体慢" |
| `$upstream_response_time` | 后端应用处理耗时 | 判断"是后端慢还是网络/排队慢" |

两者相等 → 慢在应用；相差巨大 → 慢在排队/连接层。

error.log 是 **502/504 的第一现场**：502 `connect() failed (111: Connection refused)` = 后端进程挂了；504 `upstream timed out` = 后端太慢。

---

## 七、系统层日志

### 1. OOM killer（进程无声消失的唯一证据）

Java 进程消失、hs_err 也没有、jstat 都连不上——第一反应查系统层：

```bash
dmesg -T | grep -i -E "oom|kill"
# 输出形如：Out of memory: Killed process 4321 (java) ...
grep -i "killed process" /var/log/messages
```

典型场景：容器内存 limit 设小了，Linux 杀掉占用最高的进程（往往是 Java）。对应排查手册第十章容器被杀。

### 2. Docker 容器日志

```bash
docker logs -f --tail 200 <container>      # 看 stdout/stderr（SpringBoot 控制台输出）
```

**必须限制大小防爆盘**：

```bash
docker run --log-driver=json-file --log-opt max-size=10m --log-opt max-file=3 ...
# 或 /etc/docker/daemon.json 全局配置后 restart docker
```

### 3. GitHub Actions 日志

仓库 → Actions → 点失败的运行 → 逐级展开步骤看日志。常见坑：`GITHUB_TOKEN` 权限不足、缓存命中失败、部署 SSH 私钥没配 secret。本地复现：`act`（本地跑 workflow）。

---

## 八、按故障现象反查日志（背这张表）

| 故障现象 | 按顺序看什么 |
| ---- | ---- |
| 服务起不来 | ① app.log 尾部（卡在哪个组件）② MySQL error log ③ Nacos/Redis 连接日志 |
| 接口偶发慢 | ① AOP 耗时日志（定位接口）② MySQL slow.log ③ gc.log（停顿对时间）④ Nginx `$request_time` vs `$upstream_response_time` |
| 频繁 Full GC | ① gc.log ② heap dump + MAT ③ 业务日志找泄漏模式（缓存堆积类操作） |
| OOM 告警 | ① heap.hprof（若配了自动 dump）② app.log 异常前的业务上下文 |
| 进程无声消失 | ① dmesg（OOM killer）② hs_err_pid.log（若有 = JVM 崩溃）③ 都没有 = 人为/脚本杀的，查发布记录 |
| 大面积 502/504 | ① Nginx error.log ② 确认后端进程存活 ③ access.log 状态码时间分布（和发布时间对齐？） |
| 数据莫名不一致 | ① binlog（谁改的）② 业务日志 traceId 全链路还原 |
| Redis 抖动 | ① SLOWLOG GET（立刻！重启就没了）② --bigkeys ③ 服务日志看淘汰 |
| 部署后行为异常 | ① Actions 构建日志（确认包是新的）② jar 里的配置 vs 服务器配置 |

---

## 九、EFK 采集规划（哪些进、哪些不进）

| 日志 | 进 EFK？ | 理由 |
| ---- | ---- | ---- |
| 业务日志（JSON 化） | ✅ 必进 | 核心检索目标，traceId 串联 |
| Tomcat / Nginx access | ✅ 进 | 流量分析、故障定位 |
| 框架日志 | ✅ 进（与业务日志同一文件） | 告警来源 |
| MySQL 慢日志 | ✅ 定时采集（Filebeat） | 低频高价值 |
| Redis 慢日志 | ⚠️ 定时任务导出到文件再采集 | 本身在内存 |
| GC 日志 | ❌ 不进 | 低频、专用工具分析，留在本地滚动保留 |
| heap dump / hs_err | ❌ 不进 | 体积巨大，留磁盘 + 告警通知人工处理 |
| binlog | ❌ 不进 | DBA 域，属于数据管道不是日志管道 |

不进 EFK 的都要有**本地保留策略**（滚动 + 大小上限）+ 磁盘水位告警，否则下次排查时文件早被覆盖了。

---

## 十、一次完整故障的时间线还原（综合示例）

> 场景：某天 14:30 起，订单接口偶发 500ms+，CPU 告警。

1. **AOP 业务日志**：锁定慢的接口集中在 `POST /api/order`，耗时 480ms，traceId 已收集
2. **gc.log**：14:30 起 Young GC 频率翻倍，无 Full GC → 排除停顿，但内存压力上升
3. **MySQL slow.log**：新增两条 1.2s 的 `select * from order_item where ...`，无索引
4. **Nginx access**：`$request_time ≈ $upstream_response_time` → 确认慢在后端而非网关
5. **结论**：慢 SQL 拖长事务 → 连接池排队 → GC 压力。加索引 + 拆事务，曲线恢复

四层日志各出一份证据，交叉印证才敢下结论——这就是"全景日志"的意义。

## 十一、日志文件种类