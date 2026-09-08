# SpringBoot 线上问题排查手册

> 姊妹篇：[springboot3-补充内容.md] 第十三节"高频线上问题排查表"的完整展开。
> 回答一个问题：**线上生产环境到底会遇到哪些问题，遇到后怎么排查。**

## 〇、排查总原则（先记住这三条）

1. **止血优先于定位**：用户在流血，先恢复服务再找根因。手段按优先级：回滚 > 重启 > 降级 > 扩容。
2. **重启之前先留现场**：重启会销毁所有运行时证据。重启前先执行 `jstack`、`jmap -dump`（或提前配好 `HeapDumpOnOutOfMemoryError`）。
3. **按现象分诊，不要瞎猜**：CPU 高、OOM、接口慢、报错激增，四类现象对应四套完全不同的排查路径。

### 标准 SOP

```
发现（告警/用户反馈）
→ 确认影响面（哪些接口、多少用户、是否资损）
→ 止血（回滚/重启/降级/扩容）+ 保留现场（dump/jstack/GC日志/慢SQL）
→ 按现象分诊定位（本文第二~十章）
→ 修复验证 → 复盘归档（时间线、根因、改进 TODO）
```

---

## 一、问题全景：线上会遇到的八大类

| # | 类别 | 典型现象 | 高频根因 |
| - | ---- | -------- | -------- |
| 1 | 启动类 | 起不来、启动卡住 | 端口占用、循环依赖、依赖冲突、连不上配置中心 |
| 2 | 内存类 | OOM、接口越跑越慢、周期性卡顿 | 全表查询、缓存堆积、ThreadLocal 泄漏、Metaspace 溢出 |
| 3 | CPU 类 | CPU 100%、负载告警 | 死循环、频繁 Full GC、正则回溯、大量序列化 |
| 4 | 数据库类 | 连接池耗尽、慢接口、死锁 | 慢 SQL、连接泄漏、大事务、索引失效 |
| 5 | 缓存类 | Redis 超时、数据不一致 | 大 key、热 key、慢命令、序列化配置不一致 |
| 6 | 接口性能类 | 超时、503、线程池打满 | 未设超时引发堆积、下游雪崩、GC 停顿 |
| 7 | 业务并发类 | 超卖、重复扣款、数据错乱 | 无锁并发、缺幂等、定时任务重复执行 |
| 8 | 环境/运维类 | 时区错乱、磁盘满、容器被杀 | 时区、日志未切割、Docker 内存限制 |

---

## 二、启动类问题

| 报错关键字 | 根因 | 处理 |
| ---------- | ---- | ---- |
| `Port 8080 was already in use` | 端口占用 | Windows：`netstat -ano \| findstr 8080` → `taskkill /F /PID <pid>`；或改 `server.port` |
| `The dependencies of some of the beans form a cycle` | 循环依赖（Boot 2.6+ 默认禁止） | 重构分层解耦；应急 `spring.main.allow-circular-references=true` |
| `UnsatisfiedDependencyException` / `NoSuchBeanDefinitionException` | 缺 starter、包扫描不到、条件注解不满足 | 核对 `@ComponentScan` 范围、依赖是否引入、`@ConditionalOnXxx` 条件 |
| `ClassNotFoundException: javax.servlet.*` | Boot3 项目引了 Boot2 组件（jakarta 迁移坑） | 换 boot3 专用 starter |
| `UnsupportedClassVersionError` | 编译 JDK 版本高于运行环境 | 统一 JDK 版本 |
| `NoClassDefFoundError` / `NoSuchMethodError` | 依赖冲突（jar 不同版本共存） | `mvn dependency:tree -Dincludes=<group:artifact>` 找冲突，`<exclusion>` 排除 |
| 启动卡住不动 | 连不上 Nacos/DB/Redis，阻塞在重试 | 看日志最后一行停在哪个组件；检查网络、防火墙、密码 |
| 容器刚启动就被杀（`OOMKilled`） | JVM 堆 + 元空间 + 堆外 > 容器内存限制 | 调小 `-Xmx`；JDK8u191+ 开启容器内存感知 `-XX:+UseContainerSupport` |

---

## 三、内存类问题（OOM 专题）

### 溢出的四种形态

| 报错 | 位置 | 典型根因 |
| ---- | ---- | ---- |
| `Java heap space` | 堆 | 一次查百万行不分页、Excel 全量导出、本地 Map 缓存无上限、大文件读进 byte[] |
| `Metaspace` | 元空间 | CGLIB 动态代理类暴涨、Groovy/脚本引擎反复生成类 |
| `Direct buffer memory` | 堆外 | Netty/NIO 场景堆外内存未释放 |
| `GC overhead limit exceeded` | 堆将尽 | 98% 时间在 GC 却回收不到 2% 空间 |

### 内存泄漏三大惯犯

1. **ThreadLocal 未 remove**：线程池线程复用，Entry 永远持有value → 用完必须 `finally { tl.remove(); }`
2. **静态集合只增不减**：`static Map` 当缓存用又无淘汰策略 → 换 Caffeine 设上限
3. **连接/流未关闭**：手动 JDBC、HTTP 连接忘关 → try-with-resources

### 排查动作

```bash
# 提前配好（重启前最重要的保险）
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/dump/

# 手动抓堆快照
jmap -dump:format=b,file=/data/heap.hprof <pid>

# 看内存概况（不用 dump，轻量）
jmap -histo <pid> | head -20        # 对象直方图，找最多的那个类

# 看 GC 频率（每秒一次，观察 FGC 列是否持续上涨）
jstat -gcutil <pid> 1000

# dump 文件用 MAT（Eclipse Memory Analyzer）打开：
# Dominator Tree 找支配树最大对象 → Leak Suspects 一键报告
```

**判断标准**：Full GC 后老年代占用能回落 = 正常波动；回不去且持续爬升 = 泄漏。

---

## 四、CPU 类问题（100% 排查四板斧）

```bash
# 1. 找到最耗 CPU 的 Java 进程
top                          # 记下 PID

# 2. 找到进程内最耗 CPU 的线程
top -Hp <pid>                # 记下线程 TID（比如 4789）

# 3. 线程号转十六进制（jstack 里线程是十六进制）
printf "%x\n" 4789           # → 12b5

# 4. 拿到线程栈，定位到具体代码行
jstack <pid> | grep "0x12b5" -A 30
```

**关键分叉**：
- 抓到的线程是**业务线程** → 死循环 / 正则灾难回溯 / 大量序列化，直接看栈顶代码
- 抓到的全是 **GC 线程（G1 Young/Old）** → 不是代码死循环，是内存问题，转第三章
- 线程状态 `BLOCKED` 且等同一把锁 → 锁竞争，看它等的锁被谁持有（`jstack` 里 `waiting to lock <0x...>` 再搜这个地址）

**Windows 环境**：没有 top，直接用 Arthas：`thread -n 3`（最忙的 3 个线程一键定位）。

---

## 五、数据库类问题（线上占比最高）

### 1. 连接池耗尽

```
HikariPool-1 - Connection is not available, request timed out after 30000ms
```

| 根因 | 验证方式 |
| ---- | ---- |
| 慢 SQL 长期占用连接 | `show processlist` 看长事务；开启 MySQL 慢查询日志 |
| 连接泄漏（代码手动拿了没还） | `spring.datasource.hikari.leak-detection-threshold=60000`，泄漏超 60s 打印堆栈 |
| 并发突增超过池上限 | Actuator 看 `hikaricp.connections.active` 是否顶满 `maximum-pool-size` |
| 事务方法里调外部接口 | 事务持有连接期间被远程调用拖住 → 外调移出事务 |

调参只能缓解（`maximum-pool-size` 默认 10），**必须找到根因**，盲目调大只会把压力转移给 DB。

### 2. 慢 SQL

```sql
EXPLAIN SELECT ...;   -- 重点看 type（ALL 全表扫）、key（用的索引）、rows（扫描行数）
```

索引失效六宗罪：对索引列做函数运算、隐式类型转换（varchar 列用数字查）、前导模糊 `like '%xx'`、`or` 连接了非索引列、联合索引不满足最左前缀、`!=` / `not in`。

**深分页优化**：`limit 1000000,10` → 延迟关联 `where id > 上次最大id limit 10` 或先查 id 再回表。

### 3. 锁问题

| 报错 | 含义 | 处理 |
| ---- | ---- | ---- |
| `Deadlock found when trying to get lock` | 死锁（InnoDB 自动回滚一个） | `show engine innodb status` 看 LATEST DETECTED DEADLOCK；统一多表更新顺序 |
| `Lock wait timeout exceeded` | 行锁等待超时（长事务占着不放） | 查 `information_schema.innodb_trx` 找未提交事务；拆小事务 |

### 4. 主从延迟

写完立即读从库读不到 → 读写分离场景强制关键读走主库，或延迟双读（sleep 后重试）。

---

## 六、Redis / 缓存类问题

| 现象 | 根因 | 处理 |
| ---- | ---- | ---- |
| 大量 Redis 命令超时 | 网络抖动 / `maxclients` 满 / 慢命令阻塞 | 慢查询日志 `slowlog get`；**生产禁用 `keys *`**，用 `scan` |
| 某个 key 操作一次卡几百 ms | 大 key（value 几十 MB 的集合） | `redis-cli --bigkeys` 找出；拆分结构；删除用 `unlink` 异步 |
| 单个分片 CPU 打满 | 热 key | 本地缓存（Caffeine）做二级缓存 |
| 缓存读出来是乱码/null | 序列化器配置不一致 | 统一 `RedisTemplate` 序列化配置 |
| 缓存三大问题 | 穿透/击穿/雪崩 | 见《springboot3-补充内容.md》第八节 |
| 分布式锁失效 | 删了别人的锁 / 业务超时锁先过期 | 唯一标识 + Lua 原子删；生产用 Redisson 看门狗 |

---

## 七、接口性能类问题

### 现象 → 根因对照

| 现象 | 根因链 | 排查 |
| ---- | ---- | ---- |
| 接口整体变慢 | 慢 SQL / 下游接口变慢 / GC 停顿 | Arthas `trace` 看调用链每段耗时 |
| 请求超时 + 日志里大量线程 WAITING | 外部调用**没设超时**，线程堆积雪崩 | 所有 RestTemplate/HttpClient/Feign 必须设 connect + read timeout |
| 周期性整体卡顿几秒 | Full GC STW | `jstat -gcutil` 看 FGC 频率 |
| 返回 503 / 连接拒绝 | Tomcat 线程池打满，accept-count 溢出 | 调 `server.tomcat.threads.max` 只是应急；找占用线程的根因 |
| 一挂全挂 | 无熔断降级，一个下游拖垮全部线程 | 引入 Sentinel / Resilience4j 熔断 + 降级预案 |
| 平台正常但某接口打爆 | 被刷 / 异步任务涌入 | 网关限流（令牌桶）、接口幂等 |

**原则**：耗时操作（发邮件、生成报表、推送）不要同步做在请求线程里 → MQ 异步化 / `@Async` + 独立线程池。

---

## 八、业务并发类问题

| 问题 | 场景 | 标准解法 |
| ---- | ---- | ---- |
| 超卖/库存为负 | 并发扣库存 | DB 乐观锁 `update stock set num=num-1 where id=? and num>0`；或 Redis 分布式锁 |
| 重复提交/重复扣款 | 用户连点、支付回调重试 | 幂等：唯一索引兜底 + token 机制 + 状态机流转（只有待支付才能变已支付） |
| 定时任务重复执行 | 多实例部署同一 @Scheduled | 分布式锁抢占 / xxl-job 调度平台 |
| 金额算错 | 用 double 运算 | 一律 `BigDecimal`，数据库 `decimal` |
| 跨服务数据不一致 | 两个库写了一半失败 | 本地消息表 / MQ 事务消息做最终一致性 |

---

## 九、日志与磁盘类问题

| 问题 | 现象 | 处理 |
| ---- | ---- | ---- |
| 磁盘写满导致服务假死 | 日志写不进、临时文件创建失败 | `df -h` 定位大目录；logback 配 `maxHistory` + `totalSizeCap`；紧急 `> app.log` 清空 |
| 日志风暴 | 生产误开 debug 级别 | 运行时用 Actuator 动态改：`POST /actuator/loggers/com.xxx {"configuredLevel":"INFO"}` |
| 出问题查不到日志 | 到处 `System.out`、无 traceId | 统一 logback + MDC 埋 traceId 串联一次请求 |

---

## 十、部署环境类问题（最容易忽略）

| 问题 | 现象 | 处理 |
| ---- | ---- | ---- |
| 时区差 8 小时 | 容器/云服务器默认 UTC | 启动参数 `-Duser.timezone=Asia/Shanghai`；Docker 加 `-e TZ=Asia/Shanghai` |
| jar 内读不到文件 | 本地正常，打包后 `new File()` 失败 | 改 `ClassPathResource` + `getInputStream()`，禁用 File 方式 |
| 端口不通 | 本机 curl 通，外部不通 | 云服务器查安全组、`firewall-cmd --list-ports` |
| 外部配置不生效 | 改了配置没反应 | 理解优先级：命令行 > 环境变量 > 外部文件 > jar 内；`--debug` 启动看条件报告 |
| 容器莫名重启 | 内存超 limit 被 OOMKill | `docker stats` 看用量；限制 JVM 堆小于容器 limit（留足堆外空间） |

---

## 十一、排查工具箱

### 命令速查

| 层 | 命令 | 用途 |
| -- | ---- | ---- |
| 系统 | `top` / `htop` | CPU、内存总览 |
| 系统 | `free -h`、`df -h`、`iostat` | 内存 / 磁盘 / IO |
| 系统 | `netstat -anp \| grep <port>`、`ss -lntp` | 端口与连接 |
| JDK | `jps -l` | 找 Java 进程 |
| JDK | `jstat -gcutil <pid> 1000` | GC 实时监控 |
| JDK | `jstack <pid>` | 线程栈：死锁、CPU 高、线程堆积 |
| JDK | `jmap -dump/-histo` | 堆转储 / 对象直方图 |
| 可视化 | MAT / JVisualVM | dump 分析、远程监控 |
| 监控 | Actuator + Prometheus + Grafana | 指标与告警（事前发现，比事后排查更重要） |

### Arthas 线上诊断（生产神器，无需重启）

| 命令 | 作用 |
| ---- | ---- |
| `dashboard` | 进程总览：线程、内存、GC 一屏看 |
| `thread -n 3` | 最忙的 3 个线程（CPU 高一键定位） |
| `thread -b` | 直接找出死锁 |
| `thread <id>` | 看指定线程栈 |
| `trace com.x.OrderService create '#cost > 100'` | 方法调用链耗时，只显示超 100ms 的 |
| `watch com.x.OrderService create '{params, returnObj, throwExp}'` | 观察入参/返回值/异常 |
| `jad com.x.OrderService` | 反编译，确认线上跑的到底是不是最新代码 |
| `heapdump /tmp/d.hprof` | 堆转储 |
| `profiler start` / `profiler stop --format html` | 火焰图，看 CPU 到底烧在哪 |

---

## 十二、上线前自检清单

- [ ] `HeapDumpOnOutOfMemoryError` 已配置且 dump 目录磁盘充足
- [ ] 所有外部调用（HTTP/DB/Redis）都设了超时时间
- [ ] HikariCP 开启 `leak-detection-threshold`
- [ ] 日志滚动策略 + `totalSizeCap` 已配置，生产级别 INFO
- [ ] Actuator 端点收敛（env 暴露需鉴权），接入监控告警
- [ ] 时区参数 `-Duser.timezone=Asia/Shanghai`
- [ ] 数据库金额字段 decimal、代码 BigDecimal
- [ ] 幂等与防重复提交已覆盖核心写接口
- [ ] 部署回滚脚本演练过一次
