# Arthas

## 💰收费情况

**Arthas 完全免费、开源，Apache‑2.0 协议，可商用，无任何收费版本GitHub。** 阿里开源 Java 线上诊断工具，基于 Java Agent，**不需要重启服务、不修改代码，attach 挂载到正在运行的 Java 进程做诊断**Arthas。

> 注意：仅用于临时线上排查，排查完成执行`stop`卸载 agent，不要常驻生产进程。

## 📦快速启动 Demo（官方示例）

### 1、下载并运行官方 demo 程序

```
#下载demo
curl -O https://arthas.aliyun.com/arthas-demo.jar
#启动demo程序
java -jar arthas-demo.jar
```

> demo 逻辑：每秒生成随机数，做质因数分解，模拟业务运行。

### 2、下载启动器 arthas‑boot.jar

新开一个终端窗口（不要关闭 demo 程序）

```
curl -O https://arthas.aliyun.com/arthas-boot.jar
java -jar arthas-boot.jar
```

终端会列出本机所有 Java 进程，输入 demo 程序对应的**序号**回车，attach 连接成功，进入 arthas 交互控制台Arthas。

> ⚠️注意：启动 arthas 的操作系统用户，必须和目标 Java 进程是同一个用户，否则 attach 失败。

## 🚩高频实战命令 Demo（面试常考）

### 1. dashboard 总览面板

```
dashboard
```

实时展示：线程、内存、GC、JVM 版本整体面板，按`Q`退出。

### 2. thread 排查 CPU 高、死锁

```
#打印CPU占用最高前3个线程堆栈（排查CPU飙升）
thread -n 3

#检测死锁，直接打印死锁线程
thread -b

#打印指定线程ID堆栈
thread 1
```

### 3. trace 追踪方法调用链路耗时（定位慢接口）

> 打印方法内部每个子方法耗时，找出慢的那一步。

```
#追踪demo的run方法
trace demo.MathGame run

#只打印耗时大于10ms的调用，-n 2只采样2次自动退出
trace demo.MathGame run '#cost>10' -n 2
```

> 按 `Ctrl+C` 停止追踪。

### 4. watch 观测方法入参、返回值、异常（线上不用打日志）

```
#监控run方法，打印入参、返回值，-x 2对象展开2层
watch demo.MathGame run '{params,returnObj}' -x 2 -n 3

#捕获抛出异常的调用
watch demo.MathGame run '{params,throwExp}' -e
```

### 5. jad 反编译线上运行的 class

```
#反编译，看线上实际运行的代码，确认是否是最新版本
jad demo.MathGame
```

### 6. profiler 生成 CPU 火焰图（排查 CPU 高）

```
#开始采样CPU
profiler start --cpu
#等待几十秒后停止，输出html火焰图文件
profiler stop --file /tmp/cpu-flame.html
```

把 `/tmp/cpu‑flame.html`下载到本地浏览器打开，就是传统火焰图。

### 7. heapdump 导出堆快照（类似 jcmd，排查 OOM）

```
heapdump --live /tmp/demo-heap.hprof
```

> 生成 hprof，下载本地用 MAT 分析。

win 没有/tmp 目录

```
# 当前目录
heapdump --live demo-heap.hprof
# 绝对目录
heapdump --live D:/daima/Lianshi/Arthas/demo-heap.hprof
```

**MAT 是 Eclipse 出品的免费开源内存分析工具**，专门用来分析 JVM 的 `.hprof` 堆转储文件，排查**内存泄漏、大对象、内存占用过高**问题

https://www.eclipse.org/mat/





### 8. 退出 Arthas（两种）

```
#退出交互控制台，agent还留在jvm内，后续可以重连
quit

#完全停止，卸载agent，彻底从JVM移除（生产排查完必须执行）
stop
```

## 常用命令

- `dashboard`：JVM 整体面板；
- `thread‑n`：查看高 CPU 线程；`thread‑b`检测死锁；
- `trace`：追踪方法调用链耗时；
- `watch`：观测入参返回值异常；
- `jad`：反编译线上 class；
- `profiler`：生成 CPU 火焰图；
- `heapdump`导出堆快照；
- `stop`：卸载 agent。