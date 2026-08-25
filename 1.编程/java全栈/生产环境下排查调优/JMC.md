# JFR + JMC（JDK 自带，免费，Windows 原生，首选）

是**JDK 自带的一套性能分析工具**，不需要额外安装第三方软件，专门用来排查 Java 程序：CPU、内存、锁、数据库 IO、线程、GC 问题。

**1. JFR（录制器）**

- 内置在 JDK 里，**低开销，生产环境也可以用**，性能损耗很小。
- 作用：**录制程序运行日志**，输出 `.jfr` 文件。
- 可以采集：
  - CPU 方法采样（`ExecutionSample`，就是火焰图的数据源）
  - GC、内存分配
  - 线程阻塞、锁等待
  - JDBC 数据库 SQL 执行事件
  - Socket 网络 IO、文件 IO
  - 异常、类加载

**2. JMC（分析查看器）**

- 用来打开 `.jfr` 文件，可视化分析录制下来的数据。
- 你现在电脑上已经安装好了，就是那个 JDK Mission Control。

JMC 里面能看什么（对你项目最重要的）

1. **方法概要**：看哪些 Java 方法消耗 CPU（替代火焰图）
2. **数据库 → JDBC**：直接看到每条 SQL 执行耗时，慢 SQL 一目了然
3. **线程视图**：看线程是 RUNNING (跑 CPU)，还是 WAITING (等待数据库 / 锁)
4. **事件浏览器**：筛选各种事件：锁等待、socket 读取、park 等待
5. **GC 分析**：看垃圾回收停顿



JFR (Java Flight Recorder) 是 OpenJDK 内置的性能采样，**Windows/Linux/macOS 全部支持**，可以输出火焰图，不需要第三方工具。

- 载 **JMC（Java Mission Control）**：https://github.com/openjdk/jmc
- 打开`demo.jfr`，进入`火焰图(Flame Graph)`视图，支持 CPU、内存分配、锁。

## 1 jps-l

获取PID

```
C:\Users\Administrator>jps -l
11968 jdk.jcmd/sun.tools.jps.Jps
21432 c:\Users\Administrator\.trae-cn\extensions\redhat.java-1.55.0-win32-x64\server\plugins\org.eclipse.equinox.launcher_1.7.200.v20260619-2039.jar
24668 org.codehaus.plexus.classworlds.launcher.Launcher
8780 com.algoviz.Application

C:\Users\Administrator>
```

| PID   | 进程                                                | 说明                                                         |
| ----- | --------------------------------------------------- | ------------------------------------------------------------ |
| 11968 | `jdk.jcmd/sun.tools.jps.Jps`                        | **就是你当前执行的 jps 命令本身**，执行完就会消失，临时进程  |
| 21432 | `org.eclipse.equinox.launcher_*.jar`                | **Trae‑CN（VSCode 的 Java 插件）的 Eclipse Equinox 启动器**，VSCode 的 Java 语言服务器后台进程，IDE 后台服务，不是你的业务程序 |
| 24668 | `org.codehaus.plexus.classworlds.launcher.Launcher` | **Maven 进程**！你执行`mvn spring-boot:run`时，Maven 本身就是这个类启动的。👉 这个是 Maven 父进程，**你的 SpringBoot 应用是它 fork 出来的子进程** |
| 8780  | `com.algoviz.Application`                           | ✅ **你的 SpringBoot 业务主程序**，就是你要调试的后端服务     |

## 2 录制采样

### **固定采样**

在管理员账号下 录制采用 60s在你项目根目录下生成 algoviz.jfr文件

```
# 录制60秒，输出文件 algoviz.jfr
jcmd 8780 JFR.start duration=60s filename=algoviz.jfr
```

**现在你要做两件事**

1. **在这 60 秒内操作你的程序（com.algoviz.Application）**，复现性能场景（比如跑算法、渲染可视化），不要闲置，否则采样数据没有意义。
2. 等待 60 秒自动结束，文件就生成完成。

**提前停止采样**

```
jcmd 8780 JFR.stop
```

**录制内存分配**

```
jcmd 8780 JFR.start duration=60s filename=alloc.jfr settings=profile
```





### 手动采样

```
# 1. 启动录制
jcmd 8780 JFR.start
```

> 此时不会生成文件，开始采集数据。 你去操作你的算法可视化程序，复现性能问题。

```
# 2. 操作完之后，手动保存快照（这一步才会生成文件！）
jcmd 8780 JFR.dump filename=D:\daima\XiangMu\算法数据结构可视化\AlgoVize\houduan\algoviz.jfr
# 3. 停止录制
jcmd 8780 JFR.stop
#4. 查看当前录制状态
jcmd 8780 JFR.check
```

> ✅ `JFR.dump` 是**强制把内存里的采样数据写入磁盘**，执行完立刻就会出现 jfr 文件。

## 3 拿到 jfr 文件之后

1. 下载 **OpenJDK JMC**：https://github.com/openjdk/jmc/releases
2. 打开 JMC，把 `algoviz.jfr` 拖拽进去
3. 切换到 **Flame Graph（火焰图）**，查看 CPU 热点、内存分配、锁等待。

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260825174241.png)

4. 点击方法概要分析，点击火焰图展示

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260825180027.png)

## 4 下载jfr-converter.jar 

把jir 文件进行转换 

| 输出格式    | 作用                                                         |
| ----------- | ------------------------------------------------------------ |
| `html`      | **交互式火焰图网页（推荐）**，浏览器打开，支持点击、搜索、过滤，和 async‑profiler 输出的 html 一模一样 |
| `collapsed` | 折叠堆栈文本格式，给 Brendan Gregg 的`flamegraph.pl`脚本生成 svg |
| `pprof`     | 转成 Google pprof 格式，可被 pprof 工具分析                  |
| `heatmap`   | 热力图                                                       |
| `otlp`      | OpenTelemetry 格式，导入可观测平台                           |

```
https://github.com/async-profiler/async-profiler/releases/latest/download/jfr-converter.jar
```

```
java -jar jfr-converter.jar --cpu algoviz.jfr algoviz_flame.html
```

