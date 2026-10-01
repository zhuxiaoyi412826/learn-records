

# 目录

[toc]

# Java基础

## **javase 基础语法**  

- 变量与数据类型：8 种基本数据类型、引用类型、类型转换、自动装箱拆箱 
- 字符串 数学运算 位运算 编码
- 流程控制：分支、循环、跳转语句、循环嵌套
- 数组：一维 / 二维数组、内存分配、数组遍历与排序、Arrays 工具类
- 方法：定义、重载、参数传递、可变参数、递归
- 编码规范：标识符、关键字、注释、命名规范

## 面向对象  

- 类与对象：属性、方法、构造方法、this 关键字、对象内存模型 值传递/引用传递
- 三大特性：封装、继承、多态（向上 / 向下转型、方法重写）
- 抽象类与接口：定义、区别、使用场景、函数式接口
- 权限修饰符：public/protected/default/private 作用范围
- 内部类、代码块、final、static 关键字的用法与底层原理
- Object 类核心方法：equals、hashCode、toString、clone

## 高级应用开发

- **异常机制**：异常体系、受检 / 非受检异常、try-catch-finally、throw/throws、自定义异常
- **集合框架**：Collection/Map 体系，ArrayList/LinkedList/HashMap/HashSet/ConcurrentHashMap 底层原理、迭代器、Collections 工具类
- **IO 流**：字节流 / 字符流、缓冲流、转换流、序列化流、NIO 基础概念
- **流式输出**
- **多线程**：线程创建方式、线程生命周期、线程安全、synchronized、Lock、线程通信、线程池、ThreadLocal、volatile
- **泛型**：泛型类 / 方法 / 接口、通配符、类型擦除
- **反射**：Class 对象获取、反射操作构造 / 方法 / 字段、反射的应用场景
- **注解**：元注解、自定义注解、注解解析
- **Lambda & Stream**：函数式编程、流式计算、常用中间 / 终端操作
- **JVM 基础**：内存区域、类加载过程、垃圾回收算法与回收器、JVM 参数调优入门
- **正则表达式**
- **JDK新特性 ** 8-25
- **函数式编程**

## 并发编程

并发是 Java 面试**第一高频**模块，必须系统掌握。

#### 1.1 线程基础

- 线程创建的 4 种方式：继承 Thread、实现 Runnable、实现 Callable + FutureTask、线程池
- 线程生命周期：NEW → RUNNABLE → BLOCKED → WAITING → TIMED_WAITING → TERMINATED
- 线程常用方法：start/run、sleep/wait/notify/notifyAll、join、yield、interrupt、setDaemon
- 线程中断机制：interrupt 标志位、isInterrupted、interrupted 区别、优雅停止线程
- 线程优先级、守护线程、线程组

#### 1.2 线程安全核心

- **synchronized 关键字**
  - 作用范围：对象锁（方法/代码块） vs 类锁（静态方法/类.class）
  - 底层原理：monitorenter/monitorexit、Monitor 对象、对象头 MarkWord
  - 锁升级过程：无锁 → 偏向锁 → 轻量级锁 → 重量级锁（JDK6 后）
  - 锁消除、锁粗化、自适应自旋
- **volatile 关键字**
  - 两大特性：可见性（内存屏障）、禁止指令重排序
  - 不保证原子性的原因
  - 应用场景：状态标记、双重检查锁单例、防止指令重排
- **Lock 接口体系**
  - ReentrantLock 可重入锁：公平/非公平、lock/tryLock/lockInterruptibly
  - ReentrantReadWriteLock 读写锁：读读共享、读写互斥、写写互斥
  - Condition 条件变量：await/signal/signalAll，替代 wait/notify
  - 与 synchronized 的对比

#### 1.3 JUC 并发工具包

- **原子类 Atomic**：AtomicInteger、AtomicReference、LongAdder、AtomicStampedReference（ABA 问题解决）
- **并发集合**
  - ConcurrentHashMap：分段锁 → CAS + synchronized（JDK8）、size 计算、扩容transfer
  - CopyOnWriteArrayList：写时复制思想、适用场景
  - ConcurrentLinkedQueue：非阻塞队列、CAS 实现
  - BlockingQueue 家族：ArrayBlockingQueue、LinkedBlockingQueue、DelayQueue、SynchronousQueue、PriorityBlockingQueue
- **同步工具类**
  - CountDownLatch：倒计时门闩，一等多
  - CyclicBarrier：循环栅栏，多等一，可重置
  - Semaphore：信号量，限流
  - Exchanger：交换器，两线程交换数据
  - Phaser：阶段器，替代 CyclicBarrier

#### 1.4 线程池

- 七大核心参数：corePoolSize、maximumPoolSize、keepAliveTime、unit、workQueue、threadFactory、handler
- 四种拒绝策略：AbortPolicy、CallerRunsPolicy、DiscardPolicy、DiscardOldestPolicy
- 任务提交流程：核心池 → 队列 → 最大池 → 拒绝策略
- Executors 提供的四种线程池及各自问题（为什么《阿里规约》不推荐）
  - FixedThreadPool：队列无界 → OOM 风险
  - SingleThreadExecutor：队列无界 → OOM 风险
  - CachedThreadPool：最大线程数 Integer.MAX_VALUE → 线程过多 OOM
  - ScheduledThreadPool：最大线程数 Integer.MAX_VALUE → 同上
- 线程池监控：活跃线程数、任务完成数、队列大小
- 线程池配置最佳实践：CPU 密集型 vs IO 密集型、动态调整
- 优雅关闭：shutdown vs shutdownNow、awaitTermination

#### 1.5 ThreadLocal

- 底层原理：ThreadLocalMap、Entry（弱引用）、set/get/remove
- 内存泄漏原因及解决方案（为什么要手动 remove）
- 父子线程传递：InheritableThreadLocal
- 应用场景：用户上下文、事务连接、SimpleDateFormat 线程安全问题

#### 1.6 AQS 抽象队列同步器

- 核心思想：state 状态 + CLH 双向队列 + CAS
- 独占模式 vs 共享模式
- 模板方法：tryAcquire/tryRelease、tryAcquireShared/tryReleaseShared
- ReentrantLock、Semaphore、CountDownLatch 基于 AQS 的实现原理

#### 1.7 死锁

- 死锁四条件：互斥、请求与保持、不可剥夺、循环等待
- 死锁排查：jstack、死锁检测
- 避免死锁：破坏任一条件、按顺序加锁、限时锁

## jdbc

- JDBC 核心 API：DriverManager、Connection、Statement、PreparedStatement、ResultSet
- 数据库连接池：Druid/HikariCP 配置与使用，连接池原理
- 事务管理：手动提交 / 回滚事务
- 常见问题：SQL 注入、PreparedStatement 防注入原理

# 数据库

## MySQL 

- **SQL 语法**：DQL/DML/DDL/DCL，多表查询、子查询、聚合函数、分组排序、分页
- **数据库设计**：三大范式、表关系（一对一 / 一对多 / 多对多）、外键、索引设计
- **索引**：索引类型、B + 树结构、聚簇 / 非聚簇索引、最左前缀原则、索引失效场景、Explain 分析
- **事务**：ACID 特性、隔离级别、脏读 / 不可重复读 / 幻读、MVCC 原理
- **锁机制**：行锁、表锁、间隙锁、死锁
- **性能优化**：慢查询定位、SQL 优化、分库分表基础概念
- **存储引擎**：InnoDB 与 MyISAM 的区别
- **一主两从 二主5从 如何设计**
- **数据库优化**
- **1慢查询日志开启、explain 完整字段解读 (type/key/rows/Extra)** 
- **2 锁：MVCC 底层 undo/redo log、Read View、隔离级别实现原理** 
- **3 redo/undo 日志作用、崩溃恢复机制**
-  **4 分库分完整方案：水平 / 垂直分表、分片算法、跨分页、跨 join 解决方案** 
- **5 读写分离：主从同步原理、binlog 三种格式、延迟问题与优化**
-  **6 死锁排查、锁等待超时优化 7 百万级数据分页优化、大事务危害**

## Redis

- **数据类型：String/List/Hash/Set/ZSet 及对应应用场景**
- **核心特性：过期策略、内存淘汰机制、持久化（RDB/AOF）**
- **高可用：主从复制、哨兵模式、Cluster 集群**
- **常见问题：缓存穿透 / 击穿 / 雪崩、分布式锁、缓存与数据库一致性**
- **典型应用：验证码、购物车、排行榜、点赞关注、接口限流**
- **1RDB/AOF 混合持久化、AOF 重写机制**
-  **2 集群哈希槽、槽迁移、集群故障转移** 
- **3 布隆过滤器、HyperLogLog、Geo、BitMap 实战场景**
-  **4 Redis 事务、Lua 脚本原子操作**
-  **5 Redis 内存碎片优化、大 key 处理方案** 
- **6 Redisson 完整能力：可重入锁、读写锁、公平锁、信号量、分布式限流器**
- **集群验证**

#### 1. 基础入门

- 安装与启动：Windows / Linux / Docker
- Redis 五大数据类型及应用场景：
  - **String**：缓存对象、计数器、分布式锁、Session
  - **Hash**：对象缓存、购物车
  - **List**：消息队列、文章列表、排行榜
  - **Set**：去重、共同关注、点赞、标签
  - **ZSet**：排行榜、带权重的任务队列
- Java 客户端：Jedis vs Lettuce vs Redisson
- Spring Boot 整合 Redis：`spring-boot-starter-data-redis`
- RedisTemplate 配置：序列化策略（Jackson2JsonRedisSerializer）

#### 2. 核心特性

- 过期策略：定期删除 + 惰性删除
- 内存淘汰机制：8 种淘汰策略（LRU、LFU、随机等）
- 持久化：RDB、AOF、混合持久化
- 发布订阅：Pub/Sub

#### 3. 经典问题与解决方案

- **缓存穿透**：查询不存在的数据 → 布隆过滤器、缓存空值
- **缓存击穿**：热点 Key 过期 → 互斥锁、永不过期
- **缓存雪崩**：大量 Key 同时过期 → 随机过期时间、多级缓存
- **缓存与数据库一致性**：
  - Cache Aside 模式（旁路缓存）
  - 先更数据库还是先删缓存？
  - 延迟双删方案
  - 订阅 binlog 异步更新（Canal）

#### 4. 分布式锁

- Redis 分布式锁实现：`SET key value NX PX 30000`
- 锁的释放：Lua 脚本保证原子性
- 锁续期：看门狗机制
- Redisson 分布式锁：可重入锁、公平锁、读写锁、红锁
- **动手实践**：基于 Redis 实现一个防重复提交注解

#### 5. 项目中的应用

- 给项目加上缓存：热点数据缓存（用户信息、配置项）
- 验证码：登录注册验证码（String 类型 + 过期时间）
- 接口限流：基于 Redis + 注解实现接口限流
- 点赞/收藏功能：Set 数据结构实现
- 排行榜：ZSet 实现积分排行榜

#### 6. 进阶了解

- Redis 集群：主从复制、哨兵模式、Cluster 集群
- 布隆过滤器：RedisBloom 插件、Guava 布隆过滤器
- HyperLogLog：UV 统计
- Geo：地理位置、附近的人
- BitMap：用户签到、在线状态

## MongoDB

- 文档型数据库，BSON 格式
- 核心概念：Database、Collection、Document、Field
- 常用操作：CRUD、聚合管道（Aggregation Pipeline）
- 索引：单字段、复合、文本、地理空间索引
- 适用场景：日志存储、内容管理、物联网数据、灵活 schema 的业务
- 与 MySQL/Redis 的选型对比

# 数据结构算法

左程云 算法讲解  

黑马程序员算法

## 基础

1. 基础概念
   - 区分大 O（上界）、Ω（下界）、Θ（紧界）的定义，面试以大 O 时间 / 空间复杂度为核心
   - 区分**最坏情况、最好情况、平均情况**，分析算法默认说最坏情况
2. **复杂度排序**：牢记常见复杂度量级 `O(1) < O(logn) < O(n) < O(nlogn) < O(n²) < O(2ⁿ) < O(n!)`
3. **摊还分析**：理解动态数组扩容、哈希表操作的平摊复杂度，能解释 “为什么动态数组插入是 O (1) 平摊”
4. **主定理**：快速计算递归算法的时间复杂度，覆盖二分查找、归并排序、二叉树遍历等经典递归
5. **能力要求**：看到任意一段代码，10 秒内说出时间、空间复杂度；能对比不同算法的优劣

![image-20260816160835430](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260816160835430.png)

## 基础数据结构

### 1. 数组（Arrays）

#### 核心掌握内容

- 底层原理：静态数组 vs 动态数组，动态数组的**扩容机制**（2 倍 / 1.5 倍扩容、内存重新分配、元素拷贝）
- 核心特性：随机访问 O (1) 的本质 —— 通过内存地址偏移直接定位
- 必练操作：原地删除元素、数组合并、二分查找、**双指针技巧**（快慢指针、左右指针、滑动窗口）
- 时间复杂度：访问 O (1)，中间插入 / 删除 O (n)，尾部插入平摊 O (1)
- 延伸：数组的缓存友好性，为什么数组比链表遍历更快

### 2. 链表（Linked Lists）

#### 核心掌握内容

- 结构分类：单链表、双向链表、循环链表的结构差异与适用场景
- 基础操作：增、删、查、改；熟练使用**虚拟头节点（dummy node）**简化边界处理
- 高频面试题型（必须手写无 bug）：
  - 反转链表（整体反转、局部反转）
  - 链表环检测、环入口节点（快慢指针法）
  - 删除倒数第 N 个节点
  - 合并两个 / 多个有序链表
  - 相交链表找交点
- 复杂度：随机访问 O (n)，头尾增删 O (1)（带尾指针），中间增删 O (n)
- 能清晰对比数组与链表的优劣势，说明各自适用场景

### 3. 堆栈（Stack）

#### 核心掌握内容

- 特性：后进先出（LIFO），能用数组、链表两种方式实现栈
- 经典应用场景：括号匹配、逆波兰表达式求值、函数调用栈模拟、**单调栈**
- 单调栈必刷题：每日温度、柱状图中最大矩形、接雨水
- 复杂度：入栈、出栈、取栈顶均为 O (1)

### 4. 队列（Queue）

#### 核心掌握内容

- 特性：先进先出（FIFO），能用循环数组、链表两种方式实现
- 扩展结构：双端队列（Deque）、优先队列、阻塞队列的概念
- 经典应用：BFS 层序遍历、**单调队列**（滑动窗口最大值）、任务调度
- 循环队列：掌握判空、判满条件，队头 / 队尾指针的计算方式
- 复杂度：入队、出队均为 O (1)

### 5. 哈希表（Hash table）

#### 核心掌握内容

- 哈希函数：设计原则，常见哈希方法，如何减少冲突
- 哈希冲突解决方案：
  - 链地址法（拉链法）：Java HashMap 采用
  - 开放寻址法：线性探测、二次探测
- 核心机制：负载因子、**扩容与重哈希**的过程与代价
- 高频应用：频次统计、数据去重、两数之和、LRU 缓存实现
- 复杂度：平均增删查 O (1)，最坏情况 O (n)（全冲突）
- 了解你常用语言的内置哈希结构底层（如 Python dict、Java HashMap）

## 三、基础算法

### 1. 二分查找（Binary search）

#### 核心掌握内容

- 前提条件：有序数组；熟练掌握**左闭右闭、左闭右开**两种模板，边界处理零错误
- 基础题型：查找目标值、查找左边界、查找右边界
- 扩展题型（面试高频）：
  - 旋转排序数组中查找
  - 山脉数组峰值
  - 二分答案类：最大化最小值、最小化最大值
- 细节：避免整数溢出的 mid 计算方式；时间复杂度 O (logn)

### 2. 按位运算（Bitwise operations）

#### 核心掌握内容

- 6 种基础运算符：`&` 与、`|` 或、`^` 异或、`~` 非、`<<` 左移、`>>` 右移
- 必背技巧：
  - 异或：找出现 1 次的数字、不使用临时变量交换两个数
  - `n & (n-1)`：消除最低位的 1 → 统计 1 的个数、判断 2 的幂
  - 位掩码：状态压缩、子集枚举
- 编码基础：原码、反码、补码的定义，补码的工程意义
- 区分：算术右移 vs 逻辑右移

------

## 四、树（Trees）

### 1. 树基础与遍历

#### 核心掌握内容

- 基础概念：根节点、叶子节点、深度、高度、层、度
- 四种遍历（必须会递归 + 迭代两种写法）：
  - 前序遍历：根 → 左 → 右
  - 中序遍历：左 → 根 → 右
  - 后序遍历：左 → 右 → 根
  - 层序遍历：BFS + 队列实现
- 遍历应用：中序遍历 BST 得有序数组；后序遍历做树形 DP；层序遍历求树的宽度
- 延伸：N 叉树的前序、后序、层序遍历

### 2. 二叉查找树（BST）

#### 核心掌握内容

- 核心性质：左子树所有节点值 < 根节点值 < 右子树所有节点值；**中序遍历结果有序**
- 基础操作：插入、查找、删除（删除分 3 种情况：叶子节点、单子节点、双子节点）
- 验证 BST 合法性：不能只判断左右子节点，必须传递上下界
- 寻找前驱节点、后继节点
- 复杂度：平均操作 O (logn)，最坏 O (n)（退化成链表）

### 3. 堆 / 优先级队列 / 二叉堆

#### 核心掌握内容

- 结构：大顶堆、小顶堆；数组存储的下标映射（父 i → 左 2i+1、右 2i+2）
- 核心操作：上浮（sift up）、下沉（sift down）的代码实现
- 建堆：自底向上建堆，时间复杂度 O (n)
- 堆排序：原理、实现、O (nlogn) 时间复杂度、不稳定排序
- 高频应用：TopK 问题、数据流中位数、任务优先级调度

### 4. 平衡搜索树（概念级）

#### 核心掌握内容

- 平衡树的意义：避免 BST 退化，保证操作复杂度稳定 O (logn)
- 常见类型与特点：
  - AVL 树：严格平衡，插入删除慢，查询快
  - 红黑树：近似平衡，插入删除快，工程更常用；掌握 5 条性质
  - B 树 / B + 树：多路平衡查找树；B + 树为什么适合数据库索引
- 面试不要求手写实现，但能说出核心区别与应用场景

### K叉树

### B树

### 树状数组

### 字典树

## KD树

------

## 五、排序（Sorting）

### 核心掌握内容

每种排序必须掌握：**原理 + 手写代码 + 时间 / 空间复杂度 + 稳定性 + 适用场景**

1. 基础排序
   - 冒泡排序：O (n²)，稳定，原地；仅作入门
   - 选择排序：O (n²)，不稳定，原地
   - 插入排序：O (n²)，稳定，原地；数据量小、接近有序时效率极高
2. 进阶排序（面试核心）
   - 归并排序：分治思想，O (nlogn)，稳定，空间 O (n)；延伸：求逆序对
   - 快速排序：分治 + 分区，平均 O (nlogn)、最坏 O (n²)，不稳定，原地；掌握分区点选择、三路快排优化
   - 堆排序：O (nlogn)，不稳定，原地
3. **排序稳定性**：定义是什么，稳定性有什么工程意义
4. **算法选型**：不同场景下选哪种排序最优
5. **线性排序（了解）**：计数排序、桶排序、基数排序；适用场景与局限性

------

## 六、图（Graphs）

### 核心掌握内容

1. **基础概念**：有向 / 无向图、加权 / 无权图、度、入度出度、连通分量

2. 存储方式

   ：

   - 邻接矩阵：适合稠密图，空间 O (n²)
   - 邻接表：适合稀疏图，空间 O (n+m)
   - 能对比两者优劣势，按需选择

3. 两大遍历（必须手写）

   - BFS：队列实现，层序扩展；求无权图最短路径
   - DFS：递归 / 栈实现；求连通性、环检测、拓扑排序

4. 最短路径

   - Dijkstra：单源最短路径，无负权加权图，贪心思想
   - Bellman-Ford：单源，支持负权，可检测负权环
   - Floyd-Warshall：多源最短路径，动态规划，O (n³)

5. 最小生成树

   - Kruskal：边排序 + 并查集，稀疏图更优
   - Prim：点扩展，稠密图更优

6. **拓扑排序**：仅适用于有向无环图（DAG）；掌握 Kahn 算法（BFS）、DFS 两种实现

7. 并查集（Union-Find）

   - 核心操作：合并、查询；路径压缩 + 按秩合并优化
   - 应用：连通性判断、朋友圈、岛屿数量、Kruskal 算法

8. **环检测**：有向图、无向图的 DFS 检测方法

## 七、进阶知识与计算机基础

### 1. 递归与回溯

#### 核心掌握内容

- 递归三要素：终止条件、本级逻辑、递归调用
- 能画递归树，分析递归的时间、空间复杂度，理解栈溢出风险
- 回溯算法通用模板：选择 → 递归 → 撤销选择
- 经典题型：子集、全排列、组合总和、括号生成、N 皇后、单词搜索
- 剪枝技巧：提前排除无效路径，大幅降低时间开销

### 2. 动态规划（Dynamic Programming）

#### 核心掌握内容

- DP 四步解题法：状态定义 → 状态转移方程 → 初始化 → 遍历顺序
- 核心特性：重叠子问题、最优子结构、无后效性
- 优化：空间压缩（滚动数组、降维）
- 高频题型分类（每类至少练 5 道）：
  - 线性 DP：爬楼梯、最大子数组和、打家劫舍
  - 子序列 DP：最长递增子序列、最长公共子序列、编辑距离
  - 背包问题：01 背包、完全背包、多重背包
  - 区间 DP：回文子串、戳气球
  - 树形 DP：二叉树最大路径和
- 区分：记忆化搜索（自顶向下） vs 递推（自底向上）



- 1 LRU 缓存 2 单例模式多版本 3 生产者消费者模型 4 树形结构递归组装 5 手写限流工具
- 队列算法

### 3. 字符串搜索和操作

#### 核心掌握内容

- 暴力匹配原理与复杂度
- KMP 算法：next 数组构建、匹配过程；O (n+m) 时间复杂度
- Rabin-Karp：滚动哈希思想，字符串匹配
- 高频题型：反转字符串、回文子串、字符串异位词、字符串转换

### 4. 字典树（Tries）

#### 核心掌握内容

- 结构：前缀树，以字符为路径的多叉树
- 操作：插入字符串、查找字符串、前缀匹配
- 应用场景：单词搜索、输入法联想、路由前缀匹配、词频统计
- 复杂度：插入、查询均为 O (L)，L 为字符串长度

### 5.缓存算法

### 6.贪心算法



#  前后端

## 前端基础

#### HTML5 & CSS3

- HTML5 语义化标签、Canvas、Web Storage、Web Worker
- CSS3 选择器、Flexbox、Grid 布局、动画（transition/animation）、响应式设计
- CSS 预处理器：Less / Sass（选一个）

#### JavaScript 

- 原型与原型链、闭包、作用域链、this 指向
- ES6+ 新特性：let/const、箭头函数、解构赋值、Promise、async/await、模块化 import/export、Map/Set、Proxy/Reflect、Symbol
- 事件循环：宏任务/微任务、EventLoop 机制
- 深浅拷贝、防抖节流、柯里化、偏函数

## 框架

###  Vue 

#### 1. Vue 3 核心

- 组合式 API：`setup` 语法糖
- 响应式：`ref`、`reactive`、`computed`、`watch`
- 生命周期：`onMounted`、`onUnmounted`、`onUpdated`
- 模板语法：插值、指令、事件绑定、双向绑定
- 组件基础：组件定义、Props、Emit、插槽

#### 2. Vue Router

- 路由配置、动态路由、嵌套路由
- 路由守卫：登录拦截、权限控制
- 路由懒加载

#### 3. Pinia 状态管理

- Store 定义、State、Getters、Actions
- 持久化插件

#### 4. TypeScript 基础

- 基本类型、接口、泛型、联合类型
- 在 Vue 3 中使用 TS

### Recat

### UI 组件库

- **Element Plus**：PC 端最常用
- 常用组件：表单、表格、对话框、分页、上传、树形控件
- 组件二次封装：通用表格、通用表单、通用弹窗

### js

> JS 是动态弱类型，运行时检查；TS 在 JS 基础增加静态类型，编译阶段校验，最终编译输出 JS 执行，适合大型前端项目。
>
> Node.js 是基于 V8 JavaScript 引擎的服务端 JS 运行时，让 JavaScript 脱离浏览器，可以在服务器、本地命令行执行代码

Node.js 

TS

### 工程化

- Vite 构建工具
- 项目结构规范
- 接口封装：Axios 封装、请求拦截、响应拦截、错误处理
- 环境变量：开发环境、生产环境
- 代码规范：ESLint + Prettier

### 跨平台

**Electron**：老牌桌面跨端框架，打包自带完整 Chromium + Node.js

**Tauri2**：Tauri2 以 Rust 作为原生层，复用操作系统自带 WebView 而不捆绑浏览器内核

## WEB开发

  XML Tomcat HTTP协议   Nginx Git  等 Ajax JSON Jquery  Maven Docker Redis

#### XML / Tomcat / HTTP 协议

- XML：语法、约束（DTD/Schema）、解析方式（DOM/SAX）
- Tomcat：目录结构、部署项目、虚拟路径、端口配置、Connector 容器
- HTTP 协议：请求 / 响应结构、请求方法、状态码、请求头 / 响应头、HTTPS 基础、无状态特性

#### Nginx

- 核心功能：反向代理、负载均衡、动静分离、跨域配置
- 基础配置：server 块、location 匹配规则、upstream 配置
- 常用场景：前端项目部署、接口代理、静态资源缓存

#### Git

- 核心命令：初始化、提交、分支、合并、变基、回退、暂存、远程仓库交互
- 工作流：Git Flow、团队协作冲突解决、代码提交规范

#### Ajax / JSON / jQuery

- JSON：语法、与对象互转、前后端数据交互格式
- Ajax：原生 XMLHttpRequest、Fetch、Axios 发送异步请求
- jQuery：选择器、DOM 操作、事件、Ajax 封装（了解即可，主流已转向 Vue）

#### Maven

- 核心概念：坐标、依赖、仓库（本地 / 远程 / 中央）、生命周期
- 依赖管理：依赖范围、依赖传递、依赖排除、版本冲突解决
- 多模块项目：聚合与继承、私服 Nexus 基础使用

#### Docker

- **核心概念：镜像、容器、仓库、Dockerfile**
- **常用命令：镜像操作、容器启停、数据卷、端口映射**
- **镜像构建：Dockerfile 编写、镜像推送、Docker Compose 编排多容器**
- **自动化构建镜像流水线**
-  **Docker 镜像分层原理、联合文件系统** 
- **容器资源限制 CPU / 内存、容器隔离 namespace/cgroups**
-  **镜像安全、多阶段构建减小镜像体积**

## 后台三大组件

**Servlet**

- 生命周期、执行流程、ServletConfig、ServletContext
- 请求响应：HttpServletRequest/HttpServletResponse 常用方法
- 请求转发与重定向的区别、路径问题

**Filter**

- 过滤器生命周期、过滤器链、编码过滤、登录权限校验
- 过滤器执行顺序、url-pattern 匹配规则

**Listener**

- 三大域对象监听器（ServletContext/HttpSession/ServletRequest）
- 常见应用：在线人数统计、系统初始化参数加载

## 会话控制

- **Cookie**：是**浏览器端存储的一小段文本**，由服务端通过 `Set-Cookie` 响应头下发；**浏览器后续同源请求自动带上 Cookie**
- **Session**：Session 是**服务端会话**，Cookie 用来传递 sessionId
- **Token**: 登录成功，后端生成 Token（JWT）返回给客户端，**由客户端自己保存**（localStorage / 内存），每次请求放在请求头

## API规范

- API 协议 / 风格：**REST**、JSON API、SOAP、**gRPC**、**GraphQL**、HATEOAS
- API 规范：开放 API 规格（OpenAPI）
- **身份认证方式**：基本认证、Cookie 认证、令牌认证、OAuth、JWT、SAML、OpenID
- **OpenAPI (接口描述规范**):用 YAML/JSON 描述 REST 接口,请求路径、请求方法、请求参数、请求体、响应、状态码、数据模型。
- [Swagger](https://github.com/swagger-api/swagger-ui)/[Knife4](https://github.com/xiaoymin/knife4j): OpenAPI 规范的 UI 渲染工具，用来可视化展示、在线调试文档，统一返回码、参数校验、接口限流、版本控制、文档自动生成.
- **安全攻防落地**：SQL 注入、XSS、CSRF、DDoS 防护、敏感数据脱敏、数据加密 AES/RSA
- **权限体系**：RBAC 权限模型、接口黑白名单、接口鉴权链路设计,[sa-token](https://github.com/dromara/sa-token) [springsecuity](https://github.com/spring-projects/spring-security)

# 框架

## 1. Spring 6

- 核心思想：**IOC 控制反转**、**AOP 面向切面编程**
- IOC 容器：Bean 的定义、作用域、生命周期、依赖注入（构造 /setter/ 注解）
- 常用注解：@Component、@Autowired、@Qualifier、@Value、@Configuration、@Bean
- AOP 原理：动态代理（JDK/CGLIB）、通知类型、切入点表达式、切面执行顺序
- 事务管理：声明式事务 @Transactional、传播行为、隔离级别、失效场景
- 资源管理：国际化、事件机制、Bean 后置处理器
- 认证授权 OAuth2 JWT认证

## 2. SpringMVC

- 执行流程：九大组件、请求处理全链路
- 核心注解：@Controller、@RequestMapping、@RequestParam、@RequestBody、@ResponseBody
- 参数绑定：简单类型、POJO、集合、日期类型转换、数据校验 JSR303
- 返回值处理：页面跳转、JSON 返回、ModelAndView
- 拦截器：定义、配置、执行顺序、与过滤器的区别
- 异常处理：全局异常处理器 @RestControllerAdvice
- RESTful 风格：资源定位、HTTP 方法对应 CRUD

## 3. MyBatis / MyBatis-Plus

### MyBatis

- 核心原理：ORM 思想、SqlSessionFactory、Mapper 代理原理
- 映射文件：resultMap、动态 SQL（if/choose/foreach/sql 片段）
- 缓存机制：一级缓存、二级缓存、缓存失效场景
- 高级映射：一对一、一对多、多对多关联查询
- 分页插件、通用 Mapper 基础

### MyBatis-Plus

- 核心特性：无侵入 CRUD、条件构造器 QueryWrapper/LambdaQueryWrapper
- 主键策略、自动填充、逻辑删除、乐观锁
- 代码生成器、分页插件、多租户插件

## 5. Spring Boot

- 核心原理：起步依赖、自动配置原理（@SpringBootApplication、SPI 机制、条件注解）
- 配置管理：application.yml/properties、多环境配置、配置绑定 @ConfigurationProperties
- yml书写格式
- 常用 Starter：Web、Validation、Redis、RabbitMQ、MyBatis-Plus
- 内嵌服务器、打包部署（jar/war）
- 监控与运维：Actuator 端点、Spring Boot Admin
- 异常处理、拦截器、跨域配置的最佳实践

## 6. Spring AI

- 核心概念：模型抽象、Prompt 模板、输出解析、对话记忆
- 模型对接：接入 OpenAI / 通义千问 / 豆包等主流大模型
- 高级特性：函数调用、多模态输入、流式输出
- 场景落地：智能问答、内容生成、结构化数据提取

## 项目规范

**完整链路请求：**  SpringBoot 接收请求 → 参数校验 → 登录鉴权 (JWT) →会话管理 → 业务逻辑 → 调用数据库 → 返回结果，全局异常处理→ 返回前端渲染

### 1. 分层架构

- 标准分层：Controller → Service → Mapper → Entity
- 扩展分层：
  - VO（View Object）：返回给前端的对象
  - DTO（Data Transfer Object）：前端传入的对象
  - BO（Business Object）：业务对象
  - Query：查询条件对象
  - Convert：对象转换工具
- **为什么需要分层？** 解耦、易维护、可复用、安全（不暴露数据库字段）

### 2. 工具类与规范

- **Lombok**：`@Data`、`@NoArgsConstructor`、`@AllArgsConstructor`、`@Builder`、`@Slf4j`
- **Hutool**：字符串、日期、文件、加密、验证码等工具
- **MapStruct**：实体转换，替代 BeanUtils（高性能，编译期生成代码）
- **Hutool 工具类使用练习**：至少掌握 20 个常用工具方法

### 3. 异常处理规范

- 自定义业务异常：`BusinessException`
- 错误码枚举：`ErrorCodeEnum`（统一管理错误码）
- 全局异常捕获：分类处理业务异常、参数校验异常、系统异常
- 异常日志规范：什么级别打什么日志

### 4. 日志规范

- SLF4J + Logback 配置
- 日志级别：TRACE、DEBUG、INFO、WARN、ERROR
- 日志格式规范：时间、级别、线程、类名、消息
- 敏感信息脱敏：手机号、身份证、密码不能明文打日志
- MDC 日志链路追踪：TraceId 贯穿全链路

# 第三方框架

## 一、安全权限框架（核心：Spring Security）

Spring Security（企业首选认证授权框架）认证 授权 防护

Sa‑Token

OAuth2.0/ OpenID‑Connect

| 平台              | 个人能否申请  | 是否收费 | 说明                                                         |
| ----------------- | ------------- | -------- | ------------------------------------------------------------ |
| **Gitee（码云）** | ✅个人可以     | 🆓免费    | 国内，访问快，无需审核，直接创建 OAuth 应用，拿到`client_id/client_secret`，适合国内个人项目。 |
| **QQ 互联**       | ✅个人实名认证 | 🆓免费    | 需要简单审核，国内用户基数大；注意：回调地址要配置正确，修改后需要等待生效。 |
| **微博登录**      | ✅个人可以     | 🆓免费    | 个人开发者可以创建应用，需要简单审核。                       |
| **百度账号登录**  | ✅个人可以     | 🆓免费    | 百度开放平台，个人账号可接入。                               |
| **GitHub**        | ✅个人可以     | 🆓免费    | 海外，无需审核；国内访问速度一般，适合技术类网站、博客。     |

## 二、工具 & 通用集成框架

**1. Hutool / Apache Commons**

通用工具包，封装字符串、日期、加密、文件、验证码、UUID 等，减少重复工具类。

**2. Lombok**

简化实体类代码，@Data/@NoArgsConstructor 等，消除 get/set/ 构造器，所有后端项目必引。

**3. MapStruct**

实体转换框架（DTO↔Entity↔VO），替代手动 set，高性能无反射，企业规范必备。

**4. Validation（Jakarta Validation / Hibernate Validator）**

参数校验框架，@NotBlank/@Length/@Email 等注解，你 AI 项目参数校验依赖此框架。

**5. EasyExcel / POI**

Excel 导入导出：

- POI：传统，内存易 OOM；
- EasyExcel：阿里开源，低内存百万级读写，项目报表导出首选。

**6. POI-TL Word 模板导出**

基于模板填充 Word 合同、报表，政务 / 电商后台常用。

**7. Caffeine（本地缓存）**

JVM 进程内高性能缓存，搭配 Redis 二级缓存，减轻中间件压力。

## 三、定时任务 & 异步框架

**1. Spring Task（Spring 内置）**

简单定时，@Scheduled，轻量单体项目够用；缺点不支持分布式。

**2. XXL-JOB（分布式定时调度）**

替代原生定时、替代阿里 SchedulerX，可视化后台、任务分片、失败重试、日志追踪，微服务通用。

**3. Quartz**

老牌定时框架，支持持久化任务，复杂度高，现在大多被 XXL-JOB 替代。

**4. Spring Async**

@Async 异步线程池，解耦耗时操作（日志保存、消息推送、文档向量化，AI 项目大量使用）。

## 四、文件、图片、多媒体处理框架

1. MinIO：私有对象存储，对标 OSS，本地私有化文件存储，无需依赖云厂商。
2. Hutool-Img / Thumbnails：图片压缩、水印、裁剪。
3. Tika：文档解析 PDF/Word/TXT，RAG 知识库文档上传解析必备（AI Agent 项目）。
4. FFmpeg 工具包：视频转码、音频提取，AIGC 多媒体项目使用。

## 五、数据库增强框架

1. PageHelper：纯 Mybatis 分页插件（无 MP 时使用）
2. Sharding-JDBC：分库分表、读写分离，海量数据场景
3. Flyway / Liquibase：数据库版本管理，自动执行 SQL 脚本，团队协同开发必备

## 六、接口文档 & 前后端协作

1. Knife4j（增强 Swagger）：SpringBoot 主流，UI 美观、支持 JWT 调试、导出文档
2. SpringDoc OpenAPI3：新一代 Swagger，兼容 Spring6/3
3. ApiPost / Postman：客户端工具，非代码框架

## 七、监控、链路、运维框架

**1. Spring Boot Actuator**

项目内置监控端点：健康、内存、线程池、接口指标，配合监控面板使用。

**2. Spring Boot Admin**

可视化监控面板，统一查看所有服务健康、日志、JVM 状态。

**3. SkyWalking / Pinpoint / Sleuth+Zipkin**

分布式链路追踪：记录完整调用链、耗时、异常，微服务排查瓶颈必备； 搭配 EFK 日志形成完整观测体系。

**4. Prometheus + Grafana**

时序指标监控，采集 QPS、响应时间、错误率，配置告警。

## 八、消息队列配套封装

1. Spring Cloud Stream：统一消息抽象，一套代码切换 RocketMQ/RabbitMQ/Kafka
2. RabbitTemplate / RocketMQTemplate：Spring 原生消息封装模板

## 九、AI 相关配套框架（AI Agent 项目专用）

1. Spring AI：对接各大 LLM、向量库、RAG、Function Call（你的 AI 项目核心）
2. LangChain4j：Java 版 LangChain，知识库、Agent 工具调用
3. Milvus SDK / RedisSearch：向量数据库操作框架

## 十、测试框架（开发 / 单元测试必备）

1. JUnit5：单元测试基础
2. Mockito：Mock 依赖，隔离数据库 / 中间件
3. TestContainers：本地启动 MySQL/RocketMQ 容器做集成测试
4. RestAssured：接口自动化测试 
5. jmeter压力测试

## 十一、防重复、限流、灰度专用

1. Sentinel：阿里流量防护，限流熔断（微服务标配）
2. Guava RateLimiter：单机限流
3. 分布式防重框架：基于 Redis 注解封装

## 十二、短信、邮件、第三方对接

1. Spring Mail：项目邮件发送（告警、验证码）
2. 阿里云 / 腾讯云 SMS SDK：短信验证码、通知
3. 微信开放平台 SDK：小程序、公众号登录推送
4. OSS

# 微服务

**1）接入层（流量入口）**

- 四层负载均衡：LVS / F5 / Keepalived
- 七层网关：Nginx、OpenResty、Spring Cloud Gateway、Kong、Ingress‑Nginx

> 能力：负载均衡、SSL、限流、灰度、路由、鉴权、WAF 防火墙

**2）缓存层（多级缓存）**

- 本地缓存：Caffeine、Guava Cache
- 分布式缓存：Redis

> 解决热点、降低 DB 压力；问题：穿透、击穿、雪崩、一致性

**3）消息中间件（异步通信）**

- RocketMQ、Kafka、RabbitMQ、Pulsar

> 能力：异步、解耦、削峰、事件通知；关注点：丢失、重复、积压、顺序

**4）服务治理（微服务）**

- 注册发现：Nacos / Eureka / Consul
- 配置中心：Nacos / Apollo / Spring‑Cloud‑Config
- RPC 调用：OpenFeign、Dubbo、gRPC
- 熔断降级：Sentinel、Hystrix

**5）存储层**

- 关系库：MySQL（主从、分库分表 Sharding‑JDBC）
- NoSQL：Redis、MongoDB
- 对象存储：MinIO、OSS

**6）可观测组件（监控链路日志）**

- 链路追踪：SkyWalking、Pinpoint、Jaeger（TraceId）
- 监控告警：Prometheus + Grafana
- 日志系统：EFK（Elasticsearch + Filebeat + Kibana）

**7）安全组件**

- 认证授权：Spring‑Security、Sa‑Token、OAuth2
- 接口水印、敏感词过滤、WAF、防重幂等

**8）定时任务**

- XXL‑Job、Spring‑Task、Elastic‑Job

## 什么是微服务

1. **通用设计流程** 澄清需求 → 规模估算 → 顶层架构 → 细化组件 → 瓶颈优化 → 容错扩容
2. 核心理论
   - CAP 定理：一致性、可用性、分区容错性三者不可兼得
   - BASE 理论：基本可用、软状态、最终一致性
   - 水平扩展 vs 垂直扩展
3. 核心组件
   - 负载均衡：作用、常见算法（轮询、加权、IP 哈希、一致性哈希）
   - 消息队列：三大作用（解耦、异步、削峰）；常见产品对比
   - 数据库：分库分表策略、读写分离、索引优化
   - 分布式缓存：Redis 集群架构、缓存分层
4. 经典题型要点
   - 短链接系统：发号器、哈希映射、301/302 重定向
   - 限流系统：令牌桶、漏桶、固定窗口、滑动窗口
   - 秒杀系统：限流、削峰、库存防超卖、防重复提交
   - 分布式 ID：雪花算法、号段模式、数据库自增
5. **分布式事务**：2PC、3PC、TCC、最终一致性方案

```
1 CAP、BASE 理论详细推导，什么时候选 CP/AP
2 一致性算法：Raft（Nacos 底层）简单原理
3 分布式 ID 全套方案：雪花算法、号段、Redis 自增、数据库自增优缺点对比
4 分布式会话三种实现：Redis 共享、session 同步、JWT 无状态
5 分布式锁完整方案：Redis 锁、Zookeeper 锁、数据库锁优缺点
6 分布式定时任务对比：SpringTask/XXL-Job/SchedulerX
7 接口幂等全套实现：唯一索引、Redis 令牌、状态机、防重 Token
```

## SpringCloudAibaba 

1. Nacos
   - 服务注册发现：服务提供者 / 消费者接入、服务心跳、健康检查
   - 动态配置中心：配置管理、命名空间 / 分组 / DataId、配置热更新、灰度发布
   - 集群部署、数据持久化、AP+CP 模式切换
   - 集注册中心 + 配置中心于一体，服务注册发现、动态配置管理
2. Sentinel
   - 流量控制：QPS / 线程数限流、流控模式（直接 / 关联 / 链路）、冷启动 / 匀速排队
   - 熔断降级：慢调用比例、异常比例、异常数策略
   - 热点参数限流、系统自适应保护、授权规则
   - 持久化规则、与 Feign/Gateway 整合
   - 流量控制组件，做服务限流、熔断降级、系统自适应保护
3. Dubbo
   - RPC 架构：服务提供者 / 消费者、注册中心、监控中心
   - 调用流程、负载均衡策略、集群容错机制
   - 序列化方式、超时与重试、服务分组与版本
   - 高性能 RPC 调用框架，实现微服务之间远程方法调用
4. RocketMQ
   - 核心架构：NameServer/Broker/Producer/Consumer
   - 消息发送：同步 / 异步 / 单向、批量消息、顺序消息、延迟消息
   - 消息消费：集群 / 广播模式、消息重试、死信队列
   - 事务消息、消息幂等性、消息可靠性保证
   - 阿里消息中间件，提供异步解耦、削峰填谷的消息队列能力
5. Seata
   - 分布式事务理论：CAP、BASE、柔性事务
   - 四大模式：AT（自动补偿）、TCC、SAGA、XA 模式对比与适用场景
   - AT 模式落地：TC/TM/RM 角色、一阶段提交 / 二阶段回滚、undo_log
   - 常见问题：脏写、悬挂、空回滚
   - 分布式事务组件，解决微服务跨库跨服务的数据一致性问题
6. Alibaba Cloud OSS
   - 对象存储 SDK 集成、服务端签名直传、图片处理
   - 权限控制、生命周期管理、跨域配置
   - 对象存储，用于文件上传下载云端存储
7. Alibaba Cloud SchedulerX
   - 分布式定时任务、Cron 表达式、任务分片
   - 失败重试、任务监控、工作流编排
   - 分布式任务调度，定时任务分布式执行
8. Spring Cloud Gateway
   - 核心概念：路由、断言、过滤器
   - 路由配置：路径断言、参数 / 请求头断言、动态路由
   - 过滤器：局部 / 全局过滤器、鉴权、日志、限流、跨域处理
   - 集成 Sentinel 网关限流、集成 Nacos 服务发现
   - （网关，属于 SpringCloud，SCAlibaba 适配）：API 网关，请求路由、鉴权过滤、流量转发。

**补充微服务核心知识**

- OpenFeign：远程调用、参数传递、超时配置、日志配置、与 Sentinel 整合降级
- 服务调用链路：OpenFeign + LoadBalancer 负载均衡
- 分布式问题：分布式锁、分布式 ID、分布式缓存、分布式会话
- 微服务架构设计：服务拆分原则、领域驱动设计入门、接口设计规范

## 分布式事务

> 问题：跨服务 / 多数据库，保证数据一致性。分**强一致、最终一致**。

### 1）2PC（两阶段提交 XA）

- 准备阶段：协调者让参与者执行事务，不提交，返回 ok/fail
- 提交阶段：全 ok 就提交；有失败就全部回滚 ✅优点：强一致，业务侵入小 ❌缺点：同步阻塞、协调者单点、存在数据不一致风险、性能差 👉适用：少量数据库，低并发，**生产很少直接用**

### 2）3PC

CanCommit→PreCommit→DoCommit，解决部分阻塞；依然有一致性问题，几乎不用。

### 3）TCC Try‑Confirm‑Cancel

- Try：校验、预留资源
- Confirm：确认执行业务
- Cancel：补偿回滚释放资源 ✅优点：最终一致，性能较好 ❌缺点：**业务侵入大，要手写补偿代码** 👉适用：金融、资金扣减；框架：Hmily、ByteTCC

### 4）Seata AT（互联网最常用）

一阶段本地提交，记录 undo_log 快照；二阶段根据 undo_log 提交 / 回滚 ✅优点：几乎零业务侵入 ❌缺点：隔离级别有限，生成 undo_log 👉适用：微服务普通业务

### 5）SAGA

大事务拆成多个本地子事务，每个事务配补偿操作；失败反向执行补偿回滚 👉适用：长流程业务，订单履约

### 6）本地消息表 / RocketMQ 事务消息

业务 + 消息落库，异步重试投递，**最终一致性** 👉适用：异步场景，积分、通知，不能做强一致

**选型口诀**

> 强一致选 2PC；金融资金选 TCC；普通微服务选 Seata‑AT；长流程选 SAGA；异步解耦选事务消息。

------

## 分布式锁

> 问题：多服务实例争抢共享资源，保证同一时刻只有一个线程执行。核心：互斥、防死锁、可重入、防误删。

### 1.Redis（Redisson，主流）

加锁：`SET key val NX EX`；释放用 Lua 脚本。 Redisson：看门狗自动续期、可重入锁、公平锁、读写锁。 ❌问题：主从切换锁丢失；锁超时业务未跑完。

### 2.Zookeeper(Curator)

创建临时有序节点，最小节点获得锁，监听前序节点；会话断开自动释放锁。 ✅可靠性高；❌性能一般。

### 3. 数据库

- 悲观锁 `select for update`：阻塞，高并发差
- 乐观锁 version 版本号：无阻塞，读多写少
- 唯一索引：插入冲突代表抢锁失败

### 4.etcd

租约 lease，云原生 k8s 场景。

**选型口诀**

> 高并发业务优先 Redisson；追求可靠性选 ZK；无中间件用数据库乐观锁；k8s 用 etcd。 常见坑：锁超时、锁误释放、主从丢锁、不可重入。

------

## 分布式缓存

> 作用：减轻 DB 压力，加速热点数据。常用：Redis、Memcached；架构：本地缓存 + 分布式缓存两级（Caffeine+Redis）

### Redis 部署模式

单机 → 主从 → Sentinel 哨兵（自动故障转移）→ Redis‑Cluster 分片集群

### 四大缓存问题

1. **缓存穿透**：查询不存在的数据 解决：布隆过滤器、缓存空值设置短过期时间
2. **缓存击穿**：热点 key 过期，大量请求打 DB 解决：互斥锁、热点 key 永不过期
3. **缓存雪崩**：大量 key 同时过期 / Redis 宕机 解决：过期时间加随机值、集群高可用、限流降级
4. **缓存 DB 一致性** 方案：更新库删缓存、延迟双删、Canal 监听 binlog 更新缓存

### 组件对比

- Redis：支持丰富数据结构、持久化、集群，工业首选
- Memcached：简单 KV，无持久化，现在少用
- Caffeine：高性能本地缓存，和 Redis 组成两级缓存；缺点多实例本地缓存不一致

**选型口诀**

> 通用业务：Caffeine 本地缓存 + Redis‑Cluster 两级缓存。

# AI应用

# 面经

## 核心

- **八股背诵**：Java 基础、JVM、并发、MySQL、Redis、Spring、微服务、计算机网络分模块梳理
- **算法刷题**：按题型分类突破，保持每日 2-3 题手感
- **项目深挖**：每个项目准备技术选型理由、架构图、难点解决方案、性能优化成果
- **场景题**：分布式事务、缓存一致性、秒杀、限流、高可用等经典场景题准备
- **HR 面**：自我介绍、职业规划、离职原因、优缺点、薪资谈判等常规问题准备

## 进阶

### 设计模式

- **单例模式**：饿汉式、懒汉式、双重校验锁、静态内部类、枚举实现；线程安全问题
- **工厂模式**：简单工厂、工厂方法、抽象工厂；适用场景差异
- **策略模式**：封装算法族，运行时可替换
- **观察者模式**：发布 - 订阅模型，事件通知机制
- **装饰器模式**：动态扩展对象功能，继承的替代方案
- **适配器模式**：接口兼容，新旧系统对接
- **代理模式**：控制对象访问，静态代理 vs 动态代理
- 要求：能画出 UML 简图，说出核心思想与典型应用场景

### 线上故障复盘模板

缓存雪崩、消息堆积、数据库慢 SQL、服务 OOM、大模型超时故障等完整复盘思路

**回答方案**

1. **故障现象**（用户侧、监控侧、时间）
2. **影响范围**（用户数、接口、时长、降级与否）
3. **根因定位**（表层原因 + 真实代码 / 架构根因）
4. **临时止血方案**（当时怎么快速恢复）
5. **长期根治方案**（架构 / 代码 / 配置优化）
6. **预防机制 + 监控兜底**

### 基础设计方法论

需求梳理 → 流量存储估算 → 分层架构 → 瓶颈分析 → 容灾扩容

### 高频系统设计题

1 短链接系统设计

 2 高并发秒杀系统 

3 统一网关平台

 4 分布式日志 EFK 平台 

5 限流系统 

6 海量用户 IM 聊天

7 RAG 知识库平台

### 通用架构组件

1 负载均衡四层 / 七层区别 

2 多级缓存：本地 Caffeine + Redis 分布式缓存 

3 消息队列削峰、异步解耦落地方案 

4 限流四大算法：固定窗口、滑动窗口、漏桶、令牌桶

## 计算机基础

## JVM

## 并发

## 数据库

## 系统设计

### 系统设计方法论

1. **需求澄清**：功能需求 + 非功能需求（QPS、DAU、延迟、可用性、数据量）
2. **顶层设计**：整体架构图、核心模块、数据流
3. **细化设计**：各模块详细设计、数据库设计、接口设计
4. **瓶颈分析**：性能瓶颈、单点故障、扩展性瓶颈
5. **优化方案**：缓存、异步、分片、扩容、降级

### 必掌握的核心系统设计题

| 题目                 | 核心考察点                                                  |
| -------------------- | ----------------------------------------------------------- |
| 短链接系统           | 发号器（雪花/号段）、哈希冲突、301/302、数据存储、防刷      |
| 秒杀系统             | 限流、库存防超卖、异步削峰、防重复提交、热点隔离            |
| 设计 Twitter/微博    | feed 流（推/拉/推拉结合）、关注关系、点赞评论               |
| 设计聊天系统（IM）   | 长连接、消息投递、离线消息、已读回执、群聊                  |
| 设计搜索系统         | 倒排索引、分词、相关性排序、高亮、推荐                      |
| 设计支付系统         | 支付状态机、幂等、对账、容错、资金安全                      |
| 设计限流系统         | 四大算法实现、分布式限流、多级限流                          |
| 设计日志平台         | 采集（Filebeat）→ 队列（Kafka）→ 存储（ES）→ 展示（Kibana） |
| 设计 RAG 知识库      | 文档处理→向量化→检索→生成→评估，全链路设计                  |
| 设计分布式 ID 生成器 | 雪花算法、号段模式、数据库自增、UUID、Redis 自增            |

### 通用架构设计原则

- 高可用：冗余、故障转移、熔断降级、限流隔离
- 高性能：缓存、异步、并行、就近访问、读写分离
- 可扩展：水平扩展、无状态服务、模块化、微服务拆分
- 可观测：日志、指标、链路追踪三位一体

# 面试

## 简历优化

- 简历结构：个人信息、专业技能、项目经历、工作经历、教育背景
- 项目写法：STAR 法则（背景 - 任务 - 行动 - 结果）、量化成果、突出技术难点
- 技能匹配：针对 JD 关键词优化、技术栈分层呈现
- 避坑原则：不写不熟悉的技术、项目真实可深挖、简历一页纸原则

## 面试流程

- [ ] [如何在2021年通过工程师面试](https://davidbyttow.medium.com/how-to-pass-the-engineering-interview-in-2021-45f1b389a1)
- [ ] [揭秘技术招聘过程](https://www.youtube.com/watch?v=N233T0epWTs)
- [ ] 如何在四大科技巨头公司中找到工作：
  - [ ] [如何在四大科技巨头公司中找到工作 - 亚马逊、Facebook、谷歌和微软（视频）](https://www.youtube.com/watch?v=YJZCUhxNCv8)
  - [ ] [如何在四大科技巨头公司中找到工作1（后续视频）](https://www.youtube.com/watch?v=6790FVXWBw8&feature=youtu.be)
- [ ] 《破解编程面试》第一集：
  - [ ] [Gayle L McDowell - Cracking The Coding Interview（视频）](https://www.youtube.com/watch?v=rEJzOhC5ZtQ)
  - [ ] [与作者Gayle Laakmann McDowell一起破解编程面试（视频）](https://www.youtube.com/watch?v=aClxtDcdpsQ)
- [ ] 破解Facebook编程面试：
  - [ ] [方法论](https://www.youtube.com/watch?v=wCl9kvQGHPI)
  - [ ] [问题演示](https://www.youtube.com/watch?v=4UWDyJq8jZg)
- 面试准备课程：
  - [数据结构、算法和面试的Python课程（付费课程）：](https://www.udemy.com/python-for-data-structures-algorithms-and-interviews/):
    - 以Python为中心的面试准备课程，涵盖数据结构、算法、模拟面试等内容。
  - [使用Python的数据结构和算法简介（Udacity免费课程）：](https://www.udacity.com/course/data-structures-and-algorithms-in-python--ud513):
    - 一个免费的以Python为中心的数据结构和算法课程。
  - [数据结构和算法纳米学位！（Udacity付费纳米学位）：](https://www.udacity.com/course/data-structures-and-algorithms-nanodegree--nd256):
    - 提供超过100个数据结构和算法练习的实际操作体验，并得到专属导师的指导，以帮助您为面试和实际工作做准备。
  - [Grokking行为面试（Educative免费课程）：](https://www.educative.io/courses/grokking-the-behavioral-interview):
    - 很多时候，阻碍您获得梦想工作的不是您的技术能力，而是您在行为面试中的表现。
  - [AlgoMonster（付费课程，提供免费内容）：)](https://algo.monster/?utm_campaign=jwasham&utm_medium=referral&utm_content=coding-interview-university&utm_source=github):
    - LeetCode的速成课程。涵盖了从成千上万的问题中提炼出的所有模式。

模拟面试：

- [Gainlo.co：来自大公司的模拟面试官](http://www.gainlo.co/#!/) - 我用过这个，帮助我放松进行电话和现场面试。
- [Pramp：与同行进行模拟面试](https://www.pramp.com/) - 同行模式的实践面试。
- [interviewing.io：与资深工程师进行模拟面试 ](https://interviewing.io) - 匿名算法/系统设计面试，与FAANG公司的资深工程师进行。
- [Meetapro：与顶级FAANG面试官进行模拟面试 ](https://meetapro.com/?utm_source=ciu) - 类似Airbnb的模拟面试/指导平台。
- [Hello Interview：与专家教练和人工智能模拟面试](https://www.hellointerview.com/?utm_source=ciu) - 直接与人工智能或 FAANG 员工工程师和经理面试。
- [Codemia：通过人工智能或社区解决方案和反馈来练习系统设计问题](https://codemia.io/?utm_source=ciu) - 通过AI练习工具来解决系统设计问题。与社区分享你的解决方案，以获得反馈。.

## 面试问题

- 你为什么想得到这份工作？
- 你解决过的最有难度的问题是什么？
- 面对过的最大挑战是什么?
- 见过的最好或者最坏的设计是怎么样的?
- 对某个产品提出改进建议。
- 你作为一个个体同时也是团队的一员，如何达到最好的工作状态?
- 你的什么技能或者经验是你的角色中不可或缺的，为什么？
- 你在某份工作或某个项目中最享受的是什么?
- 你在某份工作或某个项目中面临过的最大挑战是什么?
- 你在某份工作或某个项目中遇到过的最硬的 Bug 是什么样的？
- 你在某份工作或某个项目中学到了什么？
- 你在某份工作或某个项目中哪些地方还可以做的更好？

## 你问面试官的问题

- 我会问的一些：(可能我已经知道了答案但我想听听面试官的看法或者了解团队的前景):

- 团队多大规模?
- 开发周期是怎样的? 会使用瀑布流/极限编程/敏捷开发么?
- 经常会为截止日期（deadlines）加班么? 或者是有弹性的?
- 团队里怎么做技术选型?
- 每周平均开多少次会?
- 你觉得工作环境有助于员工集中精力吗?
- 目前正在做什么工作?
- 喜欢这些事情吗?
- 工作期限是怎么样的?
- 工作生活怎么平衡?

# 额外学习内容

## 脚本语言

### python

### javascripts

### Shell

### .ps1

### Lua

### Groovy

| 技术 / 组件 | 一句话作用                                                   | 典型场景                                                     |
| ----------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| Groovy      | 运行在 JVM 上的动态脚本语言，兼容 Java 语法，可无缝调用 Java 类，语法简洁，支持静态 / 动态双模式，可编译为 class 字节码 | Gradle 构建脚本、Maven/Groovy 单元测试、DSL 脚本、快速原型、Jenkins Pipeline 脚本 |

## 声明式编程

- **命令式 (Imperative)**：**怎么做**。详细告诉计算机每一步步骤、流程，控制循环、判断。（强调过程）
- **声明式 (Declarative)**：**要什么**。只描述**想要的结果**，不写具体执行步骤，底层框架 / 引擎帮你实现过程。（强调结果）

| 技术      | 声明式写法说明                                               |
| --------- | ------------------------------------------------------------ |
| SQL       | `select name from user where age>18`，只说明查什么，数据库引擎决定怎么扫描索引（最经典声明式！） |
| HTML      | `<div><p>hello</p></div>`，描述页面长什么样；浏览器负责渲染，不用写绘图步骤 |
| Vue 模板  | `<div v‑for="item in list">{{item}}</div>`，声明循环渲染，不用手动操作 DOM |
| React JSX | 声明 UI 应该是什么状态，框架负责更新 DOM                     |

## JAVAFX

> JavaFX 是 Java 生态跨平台桌面 GUI 框架，JDK11 后独立维护；支持 FXML+CSS 声明式界面，UI 操作必须在 UI 线程；用来开发 Windows/Mac/Linux 桌面客户端，作为 Swing 的替代方案。

## Quarkus

| 技术 / 组件 | 一句话作用                                                   | 典型场景                                                     |
| ----------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| Quarkus     | 基于 Jakarta EE、MicroProfile 的云原生 Java/Kotlin 微服务框架，主打**原生镜像、极速启动、低内存占用**，兼容 Spring 生态，支持响应式编程 | 容器 / K8s 微服务、Serverless、FaaS，需要快速冷启动、资源受限的云环境 |

## Micronaut

| 技术 / 组件 | 一句话作用                                                   | 典型场景                                                    |
| ----------- | ------------------------------------------------------------ | ----------------------------------------------------------- |
| Micronaut   | 云原生 JVM 微服务框架，AOT 编译优先，**编译期注解处理**，无反射，支持 GraalVM 原生镜像，Java/Kotlin 双支持，命令式 + 响应式双编程模型 | K8s、Serverless、FaaS、轻量微服务，追求极小内存、毫秒级启动 |

## Reactive Stack（Spring 响应式堆栈）

> 一句话面试背诵：**Spring Reactive Stack 是 Spring 生态的一套全栈非阻塞异步开发体系，底层基于 Project Reactor，遵循 Reactive Streams 规范，支持背压，使用少量线程支撑大量并发连接；和传统 Servlet Stack（SpringMVC）是两套并行技术栈**

**Reactor + WebFlux + R2DBC + Spring Security Reactive**

> 这 4 个组件共同组成完整响应式后端链路：Project Reactor 是底层响应式 API；WebFlux 负责接收 HTTP 请求；R2DBC 是非阻塞数据库访问；Spring Security Reactive 提供响应式鉴权，整套全链路异步非阻塞、支持背压，属于 Reactive Stack

## Git-GitHupFlows-CI/CD

Git‑GitLab + WebHook + Jenkins Pipeline + Maven + SonarQube + 静态漏洞分析 + Docker + Harbor + K8s 部署

## java函数式编程

## Kubernetes

####  Kubernetes 核心概念

- 架构：Master 节点（API Server、Controller Manager、Scheduler、etcd）+ Node 节点（Kubelet、Kube-proxy、Container Runtime）
- 核心资源：Pod、Deployment、Service、Namespace、ConfigMap、Secret、Volume
- 工作负载：Deployment（无状态）、StatefulSet（有状态）、DaemonSet、Job/CronJob

####  服务网络与存储

- Service 类型：ClusterIP、NodePort、LoadBalancer、ExternalName
- Ingress：Nginx Ingress Controller、域名路由、HTTPS 配置
- 存储：PV、PVC、StorageClass、常见存储插件

####  配置与安全

- ConfigMap / Secret 的使用方式
- RBAC 权限控制：Role、ClusterRole、RoleBinding
- ServiceAccount

#### Helm 包管理

- Chart 结构、常用命令、自定义模板

#### 实践操作

- 本地集群：Minikube / Kind
- kubectl 常用命令
- 应用部署流程：编写 Deployment + Service YAML → kubectl apply → 验证
- 日志查看与排障：kubectl logs、kubectl describe、kubectl exec

## EFKBstat

- - **Elasticsearch**：分布式存储与检索、索引设计、分片副本、查询优化
  - **Filebeat/Fluentd**：日志采集、多行日志合并、字段解析、过滤清洗
  - **Kafka**：日志缓冲削峰、解耦采集与存储、消费积压处理
  - **Kibana**：日志查询、可视化仪表盘、告警配置、索引模式管理
- 完整数据流：应用输出日志 → Filebeat 采集 → Kafka 缓冲 → Logstash/Fluentd 清洗 → Elasticsearch 存储 → Kibana 可视

## 中间件

### 消息队列

| 中间件       | 特点                                                        | 适用场景                         |
| :----------- | :---------------------------------------------------------- | :------------------------------- |
| **RocketMQ** | 阿里开源，Java 开发，高吞吐、低延迟，支持事务消息、延迟消息 | 电商订单、金融支付、异步解耦     |
| **Kafka**    | 高吞吐百万级，分布式消息引擎，生态完善                      | 日志收集、大数据流处理、事件溯源 |
| **RabbitMQ** | Erlang 开发，路由灵活，支持多种交换机模式                   | 业务解耦、复杂路由、延迟任务     |
| **Pulsar**   | 存算分离架构，支持多租户，云原生                            | 大规模消息、流批一体             |
| **ActiveMQ** | Java 老牌 MQ，支持多种协议                                  | 传统企业集成、遗留系统           |

### 缓存

| 中间件        | 特点                                       | 适用场景                              |
| :------------ | :----------------------------------------- | :------------------------------------ |
| **Redis**     | 内存数据库，支持丰富数据类型，单线程高性能 | 热点缓存、分布式锁、排行榜、限流      |
| **Memcached** | 纯内存 KV，简单轻量，多线程                | 简单缓存场景（逐渐被 Redis 替代）     |
| **Caffeine**  | JVM 本地缓存，高性能，支持多种过期策略     | 进程内一级缓存、与 Redis 组成多级缓存 |
| **Ehcache**   | Java 老牌本地缓存，支持持久化              | 单体应用、Hibernate 二级缓存          |

### 分库分表

## 框架源码

黑马程序员 spring高级原理

## 计算机原理

![image-20260816160421772](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260816160421772.png)

## 算法

DFA

[附加学习](#附加学习)

- [编译器](#编译器)
- [Emacs and vi(m)](#emacs-and-vim)
- [Unix 命令行工具](#unix-命令行工具)
- [信息论](#信息论-视频)
- [奇偶校验位 & 汉明码 (视频)](#奇偶校验位--汉明码-视频)
- [系统熵值](#系统熵值Entropy)
- [密码学](#密码学)
- [压缩](#压缩)
- [计算机安全](#计算机安全)
- [垃圾回收](#垃圾回收)
- [并行编程](#并行编程)
- [消息传递，序列化和队列化的系统](#消息传递序列化和队列系统)
- [A*搜索算法](#a*搜索算法)
- [快速傅里叶变换](#快速傅里叶变换)
- [布隆过滤器](#布隆过滤器)
- [HyperLogLog](#hyperloglog)
- [局部敏感哈希](#局部敏感哈希)
- [van Emde Boas 树](#van-emde-boas-树)
- [增强数据结构](#增强数据结构)
- [平衡查找树](#平衡查找树balanced-search-trees)
  - AVL 树
  - 伸缩树（Splay tree）
  - 2-3 查找树
  - 2-3-4 树(也称 2-4 树)
  - N-ary (K-ary, M-ary)树
  - B 树
- [k-D 树](#k-d树)
- [跳表](#跳表)
- [网络流](#网络流)
- [不相交集 & 联合查找](#不相交集--联合查找)
- [快速处理的数学](#快速处理的数学)
- [树堆 (Treap)](#树堆-treap)
- [线性规划](#线性规划linear-programming视频)
- [几何：凸包（Geometry, Convex hull）](#几何凸包geometry-convex-hull视频)
- [离散数学](#离散数学)

## 中间件

1. **消息代理**：RabbitMQ、Kafka，用于服务异步解耦、流量削峰
2. **搜索引擎**：Elasticsearch、Solr，实现全文检索能力
3. **实时数据通信**：WebSocket、服务器发送事件（SSE）、长轮询、短轮询，支撑实时交互场景

## 脚本

python 

shell

## C++

## 互联网原理

- 互联网的基本工作原理
- 超文本传输协议（HTTP）
- 域名、网站托管的基本概念
- 域名系统（DNS）的作用与工作原理
- 浏览器的运行机制

## AI

跟李沫学AI 动手学深度学习v2  

 跟李沫学AI实用机器学习中文版

## 可视化

# 项目开发总结规范

# 工具

[工具](必须掌握/开源工具)

## 流程图

**Mermaid**：Markdown 内嵌首选，GitHub 原生渲染，开箱即用；小图、README 文档

**D2**：现代架构图，布局好看，嵌套强；复杂微服务架构图，需要 CLI 编译出 SVG

**Graphviz(DOT)**：老牌图论引擎，算法极强；依赖图、拓扑、大规模节点图

**ECharts**Apache ECharts**（简称 ECharts）是由百度开源、现在由 Apache 基金会托管的 **JavaScript 数据可视化图表库**，底层依赖 ZRender 渲染引擎，采用 Apache 2.0 协议，**可免费商用**Apache ECh...。

**手动**

draw.io：开源拖拽绘图工具，支持流程图、UML、ER、架构图，还能导入 Mermaid/PlantUML 代码。

Excalidraw：手绘风格白板绘图，适合草稿、架构草图，支持时序、流程、ER。

# 数学

深度学习   线性代数 微积分

 3Blue1Brown全系列视频

傅里叶变换

# 书籍

[算法第四版](https://algs4.cs.princeton.edu/code/) 

[算法导论](https://sites.math.rutgers.edu/~ajl213/CLRS/CLRS.html)

   [onjava](https://github.com/BruceEckel/OnJava8-Examples)

[凤凰架构](https://icyfenix.cn/)

[zh-v2.d2l.ai](https://zh-v2.d2l.ai/chapter_preface/index.html)

# 论文

查询论文网站

https://arxiv.org/
