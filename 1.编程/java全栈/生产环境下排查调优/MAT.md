# MAT是什么

**MAT = Eclipse Memory Analyzer Tool**，Eclipse 基金会开源、**免费离线堆内存分析工具**Eclipse Fo...。专门解析 `.hprof` 堆转储快照文件，用来排查 **OOM、内存泄漏、内存占用过高** 问题。

> 注意：**MAT 不部署在生产服务器**；生产只导出 hprof，下载到本地电脑用 MAT 打开分析。
>
> ⚠️ 无官方中文，不建议汉化，记住核心英文单词即可。

**下载**

```
https://download.eclipse.org/mat/1.17.0/rcp/MemoryAnalyzer-1.17.0.202606011933-win32.win32.x86_64.zip

```

```
http://mirrors.ustc.edu.cn/eclipse/mat/1.16.1/rcp/MemoryAnalyzer-1.16.1.20250109-win32.win32.x86_64.zip 镜像下载目录
```

### 一、核心概念

1. **Shallow Heap（浅堆）**：对象本身占用内存大小，**不包含它引用的其他对象**。

2. **Retained Heap（深堆 / 保留堆）**：该对象被 GC 回收之后，**总共可以释放的全部内存**。排查内存泄漏重点看**深堆**。

3. **GC Roots**：垃圾回收的根对象（static 静态变量、线程、本地变量、JNI 引用等）；对象被 GC Roots 强引用，就无法被回收，造成内存泄漏。

4. 强 / 软 / 弱 / 虚引用

   ：

   - 强引用：普通`new`对象，**内存泄漏就是强引用导致**；
   - 软 / 弱 / 虚引用：GC 会回收，分析泄漏时要过滤掉。

## 二、获取 hprof 堆快照（生产 / 测试）

### 方式 1：JVM 启动参数自动 dump（**生产首选，推荐**）

```
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=D:/dump/
```

> 发生 OOM 的时候自动生成 hprof 文件，**不会带入 Arthas agent 对象，分析最干净**。

### 方式 2：jcmd 命令手动 dump（测试环境）

```
jcmd <pid> GC.heap_dump -all dump.hprof
```

### 方式 3：Arthas heapdump（仅测试，不建议用于泄漏分析）

```
heapdump --live demo-heap.hprof
```

> 缺点：会把 Arthas agent、netty 全部对象写入 dump，干扰业务分析。

> `--live`：只 dump 存活对象，排除已经不可达对象，文件更小。

------

## 三、MAT 完整操作流程（完整步骤）

### 步骤 1：导入 hprof 文件

打开 MAT → `File -> Open Heap Dump`，选择 hprof 文件。 弹出向导窗口：

1. **`Leak Suspects Report`【泄漏嫌疑报告，首选】**：自动扫描，找出疑似泄漏对象。面试 / 排查泄漏必选。
2. `Component Report`：分析重复字符串、空集合、finalizer 等细节。
3. `Re‑open previously run reports`：打开旧报告。

> 勾选`Show this dialog when opening a heap dump`：打开 hprof 是否弹出这个窗口。 点击`Finish`。

### 步骤 2：Overview 概览页面

- 总堆大小、对象数量、类数量；
- 饼图`Biggest Objects by Retained Size`：按深堆展示最大对象；
- 快捷链接：`Histogram`、`Dominator Tree`、`Unreachable Objects`。

### 步骤 3：三大核心视图（重点）

#### ① Histogram 直方图

> 统计**每个类的实例数量、浅堆、深堆**。

- 作用：快速看哪个类实例数量异常膨胀；
- 操作：可以过滤包名，筛选自己业务代码；
- 右键对象 → `Path to GC Roots`，追踪引用链。

#### ② Dominator Tree 支配树【最重要！排查泄漏核心】

> 按 **Retained Heap（深堆）** 降序排序。 支配关系：A 支配 B，所有 GC Roots 到达 B 必须经过 A。

**核心操作：定位泄漏根源**

1. 在列表找到**业务包的可疑对象**（跳过 jdk、arthas、netty 第三方类）
2. 右键对象 → `Path to GC Roots`
3. 选择：`exclude all phantom/weak/soft references`

> ✅ 过滤虚、弱、软引用，**只看强引用链**。 这条引用链，就是造成对象无法回收的根源。

#### ③ Leak Suspects Report 泄漏嫌疑报告

> MAT 自动分析生成报告。

- `Problem Suspect`：嫌疑对象；
- 点击`Details >>`展开详情，查看对象引用链；

> 注意：报告里出现第三方框架对象，不一定是泄漏，要区分业务对象。

#### ④ Unreachable Objects 不可达对象

> 已经没有 GC Roots 引用，等待 GC 回收的对象，**不属于内存泄漏**。

### 步骤 4：OQL 查询（高级，类似 SQL 查询堆对象）

工具栏 OQL 按钮，示例：

```
select * from com.demo.User
```

查询业务类所有对象实例。

------

## 四、常见内存泄漏场景，MAT 看到的现象

1. **static 静态集合无限 add，没有清理** 支配树：static 集合对象 Retained Heap 巨大；引用链指向 static 静态变量（GC Root），持有大量业务对象。
2. **ThreadLocal 没有 remove** 引用链指向`ThreadLocalMap`（线程池线程），线程复用，对象无法回收。
3. **缓存 Map 没有淘汰策略** static Map 无限 put，深堆很大，塞满业务对象。
4. **长生命周期对象持有短生命周期对象** 全局单例，保存请求临时对象，请求结束不释放。