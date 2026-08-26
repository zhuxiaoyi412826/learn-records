# Graalvm

## 什么是Graalvm

**GraalVM 是 Oracle 出品的高性能 JDK 发行版，基于 HotSpot 虚拟机，两大核心能力：Graal JIT 编译器 即时编译、Native‑Image (AOT 原生编译)提前编译**。

<!--GraalVM 能够以更少的资源消耗提升应用程序性能，从而提高应用程序效率并降低 IT 成本。它通过预先将 Java 应用程序编译成原生二进制文件来实现这一点。该二进制文件体积更小，启动速度提升高达 100 倍，无需预热即可提供峰值性能，并且比在 Java 虚拟机 (JVM) 上运行的应用程序占用更少的内存和 CPU 资源。借助性能分析引导的优化和 G1（垃圾优先）垃圾回收器，与在 JVM 上运行的应用程序相比，您可以获得更低的延迟，以及与之相当甚至更高的峰值性能和吞吐量。-->

[文档](https://www.graalvm.org/latest/docs/)

> 它可以**完全当做普通 JDK 使用**，也能把 Java 程序编译成**不需要 JVM 的独立 exe / 二进制文件**（这是它最出名的功能）

1️⃣ JVM 模式（普通运行，和 OpenJDK 一样）

- 依旧跑在 HotSpot 虚拟机，**替换掉传统 C2 JIT 编译器，换成 Graal 编译器**GraalVM。
- 所有 Java/Kotlin/SpringBoot 代码**直接跑，不用改代码**，性能更好。
- 优点：完全兼容，反射、动态代理、动态类加载全部正常。
- 缺点：还是需要 JVM，启动慢、占用内存高。

2️⃣ Native‑Image（AOT 提前编译，最核心特性⭐）

> **AOT：构建阶段就把 Java 字节码编译成操作系统原生机器码，生成独立可执行文件，运行时**不需要 JDK/JVM**Oracle。

性能对比

| 指标     | 普通 OpenJDK (JVM)               | GraalVM Native‑Image                                         |
| -------- | -------------------------------- | ------------------------------------------------------------ |
| 启动时间 | 秒级（SpringBoot 一般 3‑8s）     | **毫秒级，几十～几百毫秒**GraalVM                            |
| 空闲内存 | 200MB+                           | 几十 MB，内存大幅降低GitHub                                  |
| 运行依赖 | 必须安装 JRE/JDK                 | **完全不需要 JVM，单文件直接运行**                           |
| 预热     | 需要跑一会才达到峰值性能         | **启动即峰值性能，无预热**GraalVM                            |
| 动态特性 | 反射、动态代理、动态加载全部支持 | **封闭世界假设，编译期要知道全部代码；反射、动态代理需要配置文件** |

**3. 两个发行版本（重点区分）**

1. Oracle GraalVM for JDK21 LTS（企业版，推荐生产）
   - 基于 Oracle JDK，**持续安全更新**，Native‑Image 完整功能，GFTC 许可，免费商用。
   - 有 PGO、G1GC 等高级优化。
2. GraalVM Community Edition (CE，社区开源版)
   - 基于 OpenJDK，GPL 开源，**JDK21 版本已经停止维护**，不再发布新版本。
   - 基础 Native‑Image 可用，但缺少企业版高级优化。

**4. 缺陷**

1. **反射、动态代理、资源文件**：编译时静态分析看不到，需要写`reflect-config.json`等配置；SpringBoot3 AOT 插件会自动生成一部分，但第三方库（Nacos、Mybatis、Netty）经常要手动补配置。
2. **必须要有 C 编译器工具链**：Linux 需要 gcc，Windows 需要 VS Build Tools，Mac 需要 Xcode 命令行工具。
3. **编译时间很长**：打包 native 镜像比普通 jar 慢很多。
4. **跨平台编译困难**：Windows 编译出来只能跑 Windows，Linux 只能跑 Linux，一般用 Docker 做交叉构建。

**5.下载**

最新lats  Graalvmforjdk21

下载链接

https://github.com/graalvm/graalvm-ce-builds/releasesGitHub

## 实践

需要安装VSstudio   

- 安装时勾选 **"C++ 桌面开发"** 工作负载（包含 MSVC 编译器、Windows SDK）

大约5-6个G

