# 生产环境 JVM 

需要采集的日志清单（适配你的 OJ SpringBoot，EFKS 架构 Filebeat 采集）

> 区分两类：**JVM 虚拟机原生日志**、**应用框架输出 Java 业务日志 (logback/log4j2)**；同时区分「日志文本（Filebeat）」和「JVM 指标（Metricbeat+Jolokia）」，指标不属于日志，但是配套必须采集。

## 一、JVM 虚拟机原生日志

需要采集的日志清单（适配你的 OJ SpringBoot，EFKS 架构 Filebeat 采集）

> 这部分不由 logback/log4j2 控制，是 HotSpot 虚拟机直接输出。

### 1. GC 垃圾回收日志【必采】

- **文件示例**：`gc.log`、`gc.log.0`、`gc.log.1`、`gc.log9`（轮转后的日志）
- **作用**：
  1. 查看 YGC / FullGC 频率、STW 停顿时间；定位 OJ 接口卡顿、判题服务延迟升高。
  2. Metricbeat (JMX 指标) 在 GC STW 停顿期间拿不到数据，**排查 GC 问题必须依赖原始 GC 日志**。
- JDK8 / JDK17 都必须开启，配置日志滚动，避免单文件无限膨胀占满磁盘。

JDK GC 日志只支持**按大小轮转**，不支持按天。

- `-Xloggc`：GC 日志输出路径，带上服务名 `oj-backend-gc.log`
- `PrintGCDetails`：打印 GC 详细信息（新生代、老代、内存变化）
- `PrintGCDateStamps`：打印时间戳，方便和业务日志时间对齐
- `UseGCLogFileRotation`：开启 GC 日志轮转
- `NumberOfGCLogFiles=10`：最多保留 10 个轮转文件，写满后覆盖最老
- `GCLogFileSize=100M`：单个文件达到 100M 触发切割

```
-Xloggc:/var/log/oj-app/oj-backend-gc.log \
-XX:+PrintGCDetails \
-XX:+PrintGCDateStamps \
-XX:+UseGCLogFileRotation \
-XX:NumberOfGCLogFiles=10 \
-XX:GCLogFileSize=100M

oj-backend-gc.log
oj-backend-gc.log.0
oj-backend-gc.log.1
……
oj-backend-gc.log.9
```

- 单个 GC 文件：**100MB**
- 最多保留 10 个轮转文件
- GC 日志总最大占用：`100M *10 = 1000MB ≈1GB`

> 调优：

- 判题服务压力大、GC 频繁，可以调到单文件 200M；
- 不要设置过小（比如 10M），GC 日志被切的很碎，不方便排查；
- 不要设置过大（500M+），单文件打开分析卡顿。

GC模板

**GC 轮转日志阅读顺序**

配置：`filesize=100M，filecount=10`

> 当前正在写入：**order‑service_gc_2345.log**（最新日志） `.0`、`.1` … `.9` 是轮转归档文件

**轮转发生过程**

1. `order‑service_gc_2345.log` 写满 100M
2. 把它重命名 → `order‑service_gc_2345.log.0`
3. 创建全新空的 `order‑service_gc_2345.log`，继续写新日志
4. 再次写满：
   - `log.0` → `log.1`
   - 当前 `log` → `log.0`
   - 新建 `log`

> **数字越大，文件内容越古老**

- `.0` = 最近一次轮转出来的旧日志
- `.1` = 比.0 更早
- `.9` = 本组 PID 里**最老**的 GC 日志，filecount 到上限时，`.9`直接被 JVM 删除

**✅ 阅读顺序（从新 → 旧）**

1. `order‑service_gc_2345.log` 👉 **最新，正在写**
2. `order‑service_gc_2345.log.0`
3. `order‑service_gc_2345.log.1`
4. …
5. `order‑service_gc_2345.log.9` 👉 本组最古老

> 千万不要反过来，不是 0 最老！ 数字越大，时间越早。

### 2. JVM 崩溃转储日志 hs_err_pid<% p>.log【必采】

- 触发场景：JVM 底层崩溃；JNI 错误、内存越界、系统 OOM‑killer、硬件问题，**Java 代码 OOM 不会生成该文件**（代码 OOM 输出在应用业务日志）。
- 现象：SpringBoot 进程直接消失，没有业务异常堆栈，只有这个文件。OJ 判题服务如果突然宕机，这是最重要排查依据。

> 注意：该文件不是每次运行都产生，只有致命崩溃才输出，Filebeat 要监控该文件匹配规则。

### 3. 堆 Dump 文件（不采集日志，只做留存）

`‑XX:+HeapDumpOnOutOfMemoryError` 生成`.hprof`堆转储文件

- ❌**不要 Filebeat 采集上传 ES**：hprof 文件 GB 级别，体积巨大。
- ✅策略：磁盘本地留存，运维手动下载分析；设置自动清理旧 dump。

> hprof 是二进制快照，不属于日志文本，不送入 ELK/EFKS。

## 二、SpringBoot 应用输出日志（logback/log4j2，业务代码、框架打印）【必采】

> 这是 Java 应用最主要日志，属于应用日志，但是里面包含大量 JVM 相关异常堆栈。

1. **应用 info 日志**：Bean 加载、服务启动、定时任务执行、业务流程（OJ 判题、用户提交、AIGC 调用）
2. **应用 warn 日志**：潜在警告，连接池告警、参数警告
3. 应用 error 日志【重点】
   - Java 代码层面 OOM：`java.lang.OutOfMemoryError`（代码抛出，**不会产生 hs_err_pid**，只会打印在这里）
   - 空指针、NPE、数据库异常、线程池拒绝、业务异常完整堆栈；
   - SpringMVC 异常、MyBatis 异常、定时任务异常。

> ⚠️关键：Java 异常堆栈是**多行日志**，Filebeat 必须配置`multiline`多行合并，否则堆栈被切割成多条 ES 文档，无法排查。

## 三、可选日志（根据业务按需开启）

1. JIT 编译日志（`‑XX:+PrintCompilation`）

> 一般生产**不开启**，日志量巨大；只有排查 JIT 编译异常、性能抖动的时候临时打开。

1. Safepoint 安全点日志

> 排查神秘 STW 停顿（不是 GC 引起的停顿），平时关闭，问题排查时临时开启。

## 四、配套：JVM 指标

不是日志！Metricbeat + Jolokia 采集时序指标

> 日志记录**发生过什么事件**；指标记录**实时数值状态**，二者互补。

- 堆内存、非堆内存使用
- 新生代、老代占用
- GC 次数、GC 总耗时
- 线程总数、活跃线程、阻塞线程、死锁检测
- 类加载数量
- 系统 CPU，进程 CPU

> Metricbeat 只是拿数值，拿不到完整 GC 停顿详情，**不能替代 GC 日志**。

## 生产 JVM 日志采集总结表

表格

| 日志类型                             | 来源           | 是否必须采集 | 采集工具     | 说明                                        |
| ------------------------------------ | -------------- | ------------ | ------------ | ------------------------------------------- |
| GC 日志 gc.log                       | JVM 虚拟机参数 | ✅必采        | Filebeat     | 定位 GC 停顿、FullGC，排查接口卡顿          |
| hs_err_pid*.log JVM 崩溃日志         | JVM 虚拟机     | ✅必采        | Filebeat     | JVM 底层宕机，进程消失唯一依据              |
| SpringBoot 业务 info/warn/error 日志 | logback/log4j2 | ✅必采        | Filebeat     | Java 代码异常、业务 OOM、业务堆栈，多行合并 |
| hprof 堆 dump 快照                   | JVM OOM 触发   | ❌不上传 ES   | 本地磁盘留存 | 二进制大文件，手动下载分析                  |
| JIT 编译日志                         | JVM 参数       | ❌默认关闭    | ‑            | 仅性能问题临时开启                          |
| Safepoint 日志                       | JVM 参数       | ❌默认关闭    | ‑            | 排查非 GC 的 STW 停顿                       |
| JVM 各项运行指标 (JMX)               | JMX/Jolokia    | ✅必采        | Metricbeat   | 时序监控大盘，配合日志定位故障              |

## 生产环境最佳实践（OJ SpringBoot）

1. **日志轮转**：GC 日志、业务日志必须配置滚动策略，限制单文件大小与文件个数，防止磁盘占满。
2. **多行合并**：业务异常堆栈、GC 日志都需要 multiline，这是 Java 日志采集高频踩坑点。
3. 区分两种 OOM
   - Java 代码抛出 OOM → 在 SpringBoot error 日志；
   - JVM 底层内存损坏崩溃 → 输出 hs_err_pid 日志。
4. ES 设置索引生命周期：GC、应用日志保存 15‑30 天；审计类日志保存≥6 个月。
5. 告警配置：
   - error 日志激增告警；
   - 捕获 hs_err_pid 文件出现，立刻告警；
   - FullGC 频繁、GC 停顿过大告警。

## JVM 启动参数极简参考（JDK17，生产）

```
# GC日志输出
‑Xlog:gc*,gc+age=trace,safepoint:file=/var/log/oj-app/gc.log:utctime,pid,tags:filecount=10,filesize=100M
# JVM崩溃日志路径
‑XX:ErrorFile=/var/log/oj-app/hs_err_pid%p.log
# OOM生成dump文件（本地保存，不采集进ES）
‑XX:+HeapDumpOnOutOfMemoryError
‑XX:HeapDumpPath=/data/dump
```

如果你需要，我可以输出一份完整 Filebeat 配置，一次性包含 GC 日志、hs_err、SpringBoot 业务日志的 input 片段。