# SLF4J + Logback 日志框架使用文档

**Logback 是 Java 生态最常用的日志实现框架，由 log4j 原作者开发，SpringBoot 默认自带的日志底层实现。**

> 配套门面：`slf4j`（日志门面，统一日志 API），组合：**SLF4J + Logback**

[文档](https://logback.qos.ch/manual/index.html)

## 1. 概述

> 本项目使用 **SLF4J（Simple Logging Facade for Java）** 作为日志门面框架，**Logback** 作为底层实现。SLF4J 提供统一的日志 API，Logback 负责实际的日志输出和管理。

- `logback-core`：核心基础
- `logback-classic`：对接 SLF4J（日常项目必引）
- `logback-access`：web 容器访问日志（很少用）

3. 三大核心组件

| 组件           | 作用                                                         |
| -------------- | ------------------------------------------------------------ |
| Logger         | 日志记录器，写代码`log.info()`，控制**日志级别**，存在继承关系 |
| Appender       | 输出目的地：控制台、文件、数据库、kafka                      |
| Encoder/Layout | 日志格式化，定义一行日志长什么样                             |

**输出目标（Appender）**

- `ConsoleAppender`：控制台打印（开发环境）
- `FileAppender / RollingFileAppender`：写入文件、**按大小 / 时间滚动切割日志**（生产常用，避免单个日志文件无限变大）

**配置文件**

默认读取：`logback.xml` / `logback-spring.xml`（推荐后者，支持 Spring 环境变量） 可以配置：日志级别、格式、保存路径、日志保留天数、滚动策略、异步输出。

**日志级别（从低到高）**

```
TRACE` < `DEBUG` < `INFO` < `WARN` < `ERROR
```

> 设为 INFO 时，TRACE、DEBUG 不会输出

### 1.1 依赖说明

Spring Boot 默认已集成 `spring-boot-starter-logging`，包含：
- `slf4j-api` — 日志门面 API
- `logback-classic` — Logback 核心实现
- `logback-core` — Logback 基础设施

无需额外引入依赖。

### 1.2 配置文件位置

**配置文件加载顺序**

> 区别：`logback.xml`加载时机早，不能读取 Spring 环境变量；`logback-spring.xml`是 Spring 容器加载后解析，支持`<springProfile>`、`<springProperty>`。

1. `logback-test.xml`（单元测试优先）
2. `logback.xml`
3. `logback-spring.xml`（✅ SpringBoot 推荐，可以使用 springProfile 多环境）

```
AlgoVize/houduan/src/main/resources/logback-spring.xml
```

Spring Boot 启动时会自动加载 `logback-spring.xml`。

```
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true" scanPeriod="60 seconds" debug="false">
    <!-- 定义日志根目录 -->
    <property name="LOG_HOME" value="D:/lianxishi-rizi"/>
    <!-- 通用日志pattern：满足5问：谁、在哪、做什么、结果、耗时 -->
    <property name="COMMON_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %X{traceId:-} %X{userId:-} %logger{36}.%M - %msg cost:%d{ms}%n"/>

    <!-- 控制台输出：DEBUG级别 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${COMMON_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- DEBUG.log 文件：和控制台同DEBUG日志，重启清空 -->
    <appender name="DEBUG_FILE" class="ch.qos.logback.core.FileAppender">
        <file>${LOG_HOME}/DEBUG.log</file>
        <!-- 重启清空文件 -->
        <append>false</append>
        <encoder>
            <pattern>${COMMON_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>DEBUG</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- INFO 文件：双切割 按天+100M，滚动命名 info-yyyy-MM-dd.0.log -->
    <appender name="INFO_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_HOME}/info.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_HOME}/info-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <!-- 保留天数按需自行调整 -->
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>${COMMON_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- WARN 文件 -->
    <appender name="WARN_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_HOME}/warn.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_HOME}/warn-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>${COMMON_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>WARN</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- ERROR 文件 -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_HOME}/error.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_HOME}/error-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>${COMMON_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- 慢SQL独立Appender BF-xxx-slow.日期.0.log -->
    <appender name="SLOW_SQL_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_HOME}/BF-slow.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_HOME}/BF-%d{yyyyMMddHHmmss}-slow.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>60</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>${COMMON_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
        <!-- 只捕获慢SQL logger，在mybatis-plus/mybatis配置指定logger name -->
        <filter class="ch.qos.logback.classic.filter.LoggerNameFilter">
            <loggerName>slowSqlLogger</loggerName>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- 根日志级别 DEBUG，绑定所有appender -->
    <root level="DEBUG">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="DEBUG_FILE"/>
        <appender-ref ref="INFO_FILE"/>
        <appender-ref ref="WARN_FILE"/>
        <appender-ref ref="ERROR_FILE"/>
        <appender-ref ref="SLOW_SQL_FILE"/>
    </root>

    <!-- 单独定义慢SQL logger，mybatis慢sql输出到slowSqlLogger -->
    <logger name="slowSqlLogger" level="INFO" additivity="false">
        <appender-ref ref="SLOW_SQL_FILE"/>
    </logger>
</configuration>


    <!-- Spring Profile 配置 -->
    <springProfile name="!prod">
        <logger name="com.algoviz" level="DEBUG" additivity="false">
            <appender-ref ref="CONSOLE"/>
            <appender-ref ref="INFO_FILE"/>
            <appender-ref ref="ERROR_FILE"/>
        </logger>
        <root level="INFO">
            <appender-ref ref="CONSOLE"/>
            <appender-ref ref="INFO_FILE"/>
            <appender-ref ref="ERROR_FILE"/>
        </root>
    </springProfile>

    <springProfile name="prod">
        <logger name="com.algoviz" level="DEBUG" additivity="false">
            <appender-ref ref="INFO_FILE"/>
            <appender-ref ref="ERROR_FILE"/>
        </logger>
        <root level="INFO">
            <appender-ref ref="INFO_FILE"/>
            <appender-ref ref="ERROR_FILE"/>
        </root>
    </springProfile>

</configuration>

```

---

## 2. 日志目录结构

### 2.1 存储路径

```
D:/rizi/
├── info.log          ← 当前普通运行日志（正在写入）
├── info-1.log        ← 历史归档（第 1 次切割）
├── info-2.log        ← 历史归档（第 2 次切割）
├── ...
├── error.log         ← 当前错误日志（正在写入）
├── error-1.log       ← 历史归档
├── error-2.log       ← 历史归档
└── ...
```

### 2.2 切割策略

| 配置项 | 值 | 说明 |
|--------|------|------|
| 单文件最大 | 1MB | 超过后自动归档 |
| 归档策略 | FixedWindowRollingPolicy | 序号从 1 开始递增 |
| 最大归档数 | 99999 | 几乎无限 |
| 目录自动创建 | 已启用 | 目录不存在时自动创建 |

### 2.3 日志级别

| 级别 | 说明 | 示例 |
|------|------|------|
| **TRACE** | 最细粒度，追踪程序执行路径 | 循环变量值、方法入口出口 |
| **DEBUG** | 调试信息 | SQL 语句、参数值、分支判断 |
| **INFO** | 重要运行信息 | 服务启动、关键业务操作 |
| **WARN** | 警告信息（可继续运行） | 配置非默认值、降级处理 |
| **ERROR** | 错误信息 | 异常、业务失败 |

### 2.4 日志文件分工

| 文件 | 级别 | 用途 |
|------|------|------|
| `info.log` | INFO 及以上（INFO/WARN/ERROR） | 完整运行日志，排查流程问题 |
| `error.log` | 仅 ERROR | 专注错误日志，快速定位异常 |

> **注意**：ERROR 级别的日志会同时写入 `info.log` 和 `error.log`，方便在完整日志中追踪上下文，同时也能在 `error.log` 中快速筛选。

---

### 2.5XML文件解析

**XML标签解析**

| 标签                         | 说明                                                         |
| ---------------------------- | ------------------------------------------------------------ |
| scan="true"                  | true：logback 会定时监测 `logback-spring.xml` 文件；**文件修改后自动重新加载配置，无需重启服务**false：只在项目启动时读取一次，修改 xml 不生效 |
| scanPeriod="60 seconds"      | 配置文件扫描周期                                             |
| debug="false"                | true：输出 logback 框架本身加载、appender 初始化、过滤器、滚动策略等内部日志，**排查 logback 配置错误时临时打开**false：关闭框架内部日志（生产必须 false，避免大量额外噪音） |
| `<property>`                 | 定义变量，${name} 引用    ${value}                           |
| `<springProperty>`           | 读取 application.yml 里的配置（logback-spring.xml 专属）     |
| `<appender>`                 | 日志输出器，name 唯一                                        |
| `<rollingPolicy>`            | 滚动策略TimeBasedRollingPolicy：按天SizeAndTimeBasedRollingPolicy：时间 + 大小双限制 |
| `<filter>`                   | 过滤器，按级别过滤日志                                       |
| `<logger name="包名">`       | 单独设置某个包的日志级别additivity="false"：日志不向上传递到 root |
| `<root>`                     | 全局根日志，所有日志最终都会走到 root                        |
| `<springProfile name="dev">` | 多环境区分，logback-spring.xml 独有                          |
| ConsoleAppender              | **控制台输出器**，日志打印到 IDEA /cmd/powershell 的黑框控制台 |
| <encoder>                    | **把日志事件（时间、线程、日志内容、异常堆栈）组装成一行文本字符串**，同时处理字符编码 |
| <pattern>                    | 引用前面用`<property>`定义的日志格式变量。 pattern 就是**日志行模板**，决定打印出来的日志长什么样 |

**占位符**

```
%d{yyyy-MM-dd HH:mm:ss.SSS}  时间
[%thread]                   线程名称
%-5level                    日志级别，占5字符左对齐
%logger{36}                 类名，最多36字符
%msg                        日志消息
%n                          换行
%X{traceId}                 MDC链路追踪ID（sleuth/skywalking）
%wEx                        打印异常堆栈
```

**示例** %d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n

**输出** 2026-09-10 14:30:22.123 [http-nio-8080-exec-1] INFO  com.example.UserService - 用户登录成功

**异步日志 AsyncAppender**

**MDC 链路追踪（TraceId）**

> MDC = Mapped Diagnostic Context，在线程上绑定自定义变量（traceId），日志自动打印

## 3. SLF4J使用

### 3.1 方式一：SLF4J 原生写法（推荐）

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public void createUser(String username) {
        // INFO：记录关键业务操作
        logger.info("开始创建用户：{}", username);

        // DEBUG：记录调试信息
        logger.debug("用户参数校验通过，username={}", username);

        // WARN：记录警告
        if (username == null || username.isEmpty()) {
            logger.warn("用户名为空，使用默认值");
        }

        // ERROR：记录错误
        try {
            // 业务逻辑
        } catch (Exception e) {
            logger.error("创建用户失败，username={}", username, e);
            throw e;
        }

        logger.info("用户创建成功：{}", username);
    }
}
```

### 3.2 方式二：Lombok @Slf4j 注解（更简洁）

```java
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderService {

    public void createOrder(String productId) {
        log.info("创建订单，商品ID：{}", productId);
        log.debug("订单参数：productId={}", productId);
        log.warn("库存不足：productId={}", productId);
        log.error("订单创建失败，productId={}", productId, e);
    }
}
```

### 3.3 各场景日志级别选择

| 场景 | 级别 | 示例 |
|------|------|------|
| 服务启动/关闭 | `logger.info()` | `"服务启动完成，端口：80"` |
| 关键业务操作开始/结束 | `logger.info()` | `"开始导入题目：3 道"` / `"导入完成，成功：3"` |
| 方法参数、分支判断 | `logger.debug()` | `"查询参数：keyword={}"` |
| 非预期但可恢复的情况 | `logger.warn()` | `"Redis 连接超时，降级为缓存"` |
| 异常捕获 | `logger.error()` | `"保存失败，userId={}, error={}"` |

---

## 4. 日志格式

### 4.1 输出格式

```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

| 占位符 | 含义 | 示例输出 |
|--------|------|----------|
| `%d{...}` | 时间戳 | `2025-01-15 10:30:45.123` |
| `%thread` | 线程名 | `http-nio-80-exec-1` |
| `%-5level` | 日志级别（左对齐） | `INFO ` / `ERROR` |
| `%logger{36}` | Logger 名（最长 36 字符） | `c.a.controller.OJProblemController` |
| `%msg` | 日志消息 | `获取题目列表，count=10` |
| `%n` | 换行符 | 系统相关 |

### 4.2 实际输出示例

```
2025-01-15 10:30:45.123 [http-nio-80-exec-1] INFO  c.a.controller.OJProblemController - 获取题目列表，keyword=链表, page=1
2025-01-15 10:30:45.125 [http-nio-80-exec-1] DEBUG c.a.service.OJProblemService - SQL: SELECT * FROM oj_problem WHERE title LIKE '%链表%'
2025-01-15 10:30:45.128 [http-nio-80-exec-1] INFO  c.a.controller.OJProblemController - 查询完成，count=15
2025-01-15 10:31:00.456 [http-nio-80-exec-2] ERROR c.a.service.PaymentService - 支付失败，orderId=ORD001, error=连接超时
```

---

## 5. 占位符使用规范

### 5.1 使用 `{}` 占位符，不要字符串拼接

```java
// ❌ 错误：字符串拼接（即使日志级别不满足，也会拼接字符串）
logger.info("处理用户：" + username + "，年龄：" + age);

// ✅ 正确：占位符（只有日志级别满足时才会格式化）
logger.info("处理用户：{}，年龄：{}", username, age);
```

### 5.2 多个参数

```java
logger.info("导出题目，userId={}, count={}, format={}", userId, count, format);
```

### 5.3 记录异常堆栈

```java
// ❌ 错误：e.getMessage() 丢失堆栈
logger.error("操作失败：" + e.getMessage());

// ✅ 正确：传入异常对象，自动打印完整堆栈
logger.error("操作失败，userId={}", userId, e);
```

---

## 6. 配置详解（logback-spring.xml）

### 6.1 Appender 配置说明

#### CONSOLE — 控制台输出

```xml
<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
        <pattern>${LOG_PATTERN}</pattern>
        <charset>UTF-8</charset>
    </encoder>
</appender>
```

开发时实时查看日志，生产环境（prod profile）不输出。

#### INFO_FILE — 普通日志

```xml
<appender name="INFO_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${LOG_PATH}/info.log</file>
    <createDirs>true</createDirs>
    <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
        <level>INFO</level>
    </filter>
    <rollingPolicy class="ch.qos.logback.core.rolling.FixedWindowRollingPolicy">
        <fileNamePattern>${LOG_PATH}/info-%i.log</fileNamePattern>
        <minIndex>1</minIndex>
        <maxIndex>99999</maxIndex>
    </rollingPolicy>
    <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
        <maxFileSize>1MB</maxFileSize>
    </triggeringPolicy>
</appender>
```

| 配置项 | 值 | 说明 |
|--------|------|------|
| `<file>` | `D:/rizi/info.log` | 当前日志文件路径 |
| `<createDirs>` | `true` | 目录不存在时自动创建 |
| `<filter>` | ThresholdFilter ≥ INFO | 只接受 INFO 及以上级别 |
| `<fileNamePattern>` | `info-%i.log` | 归档文件命名规则 |
| `<minIndex>` | `1` | 归档起始序号 |
| `<maxIndex>` | `99999` | 最大归档数量（几乎无限） |
| `<maxFileSize>` | `1MB` | 单文件最大限制 |

#### ERROR_FILE — 错误日志

```xml
<appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${LOG_PATH}/error.log</file>
    <createDirs>true</createDirs>
    <filter class="ch.qos.logback.classic.filter.LevelFilter">
        <level>ERROR</level>
        <onMatch>ACCEPT</onMatch>
        <onMismatch>DENY</onMismatch>
    </filter>
    <!-- rollingPolicy 和 triggeringPolicy 与 INFO_FILE 相同 -->
</appender>
```

**LevelFilter 说明**：仅接受精确匹配 `ERROR` 级别的日志，其他级别一律拒绝。

### 6.2 Spring Profile 配置

```xml
<!-- 开发环境：输出到控制台 + 文件 -->
<springProfile name="!prod">
    <logger name="com.algoviz" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="INFO_FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </logger>
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="INFO_FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>
</springProfile>

<!-- 生产环境：仅输出到文件 -->
<springProfile name="prod">
    <logger name="com.algoviz" level="DEBUG" additivity="false">
        <appender-ref ref="INFO_FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </logger>
    <root level="INFO">
        <appender-ref ref="INFO_FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>
</springProfile>
```

通过 `spring.profiles.active=prod` 切换。

---

## 7. 日志滚动行为详解

### 7.1 滚动时机

当当前日志文件大小达到 `maxFileSize`（1MB）时触发滚动：

```
info.log (0.9MB) → 写入继续 → info.log (1.1MB，超过 1MB)
                                    ↓
                            触发 SizeBasedTriggeringPolicy
                                    ↓
                            FixedWindowRollingPolicy 执行滚动
                                    ↓
                    info.log → info-1.log, 新 info.log 创建
```

### 7.2 归档序号规则

```
第 1 次滚动:  info.log → info-1.log,     新 info.log
第 2 次滚动:  info-1.log → info-2.log,   info.log → info-1.log,   新 info.log
第 3 次滚动:  info-2.log → info-3.log,   info-1.log → info-2.log, info.log → info-1.log, 新 info.log
```

序号越大表示日志越旧。`maxIndex=99999` 意味着最多保留 99999 个归档文件。

### 7.3 启动时自动创建

```
D:/rizi/ 目录不存在
    ↓
Logback 启动时检测到 <createDirs>true</createDirs>
    ↓
自动创建 D:/rizi/ 目录
    ↓
创建 info.log 和 error.log 文件（空文件，等待写入）
```

---

## 8. 日志级别调整

### 8.1 全局调整

在 `logback-spring.xml` 中修改 `<root>` 的 `level`：

```xml
<root level="DEBUG">   <!-- 改为 DEBUG 输出更多信息 -->
```

### 8.2 包级别调整

```xml
<!-- com.algoviz 包下所有类输出 DEBUG 级别 -->
<logger name="com.algoviz" level="DEBUG"/>

<!-- 特定类输出 TRACE 级别 -->
<logger name="com.algoviz.service.OJProblemService" level="TRACE"/>

<!-- 第三方库输出 WARN 级别（减少噪音） -->
<logger name="org.springframework" level="WARN"/>
<logger name="com.baomidou" level="WARN"/>
```

### 8.3 动态调整（运行时）

Spring Boot Actuator 提供日志级别动态调整功能：

```bash
# 查询当前所有 logger 级别
GET /actuator/loggers

# 查询特定包级别
GET /actuator/loggers/com.algoviz

# 动态调整为 DEBUG
POST /actuator/loggers/com.algoviz
{"configuredLevel": "DEBUG"}

# 恢复默认
POST /actuator/loggers/com.algoviz
{"configuredLevel": null}
```

---

## 9.MDC

（满足你的需求：traceId、userId，日志模板`%X{traceId}`读取）

MDC = 线程上下文，**同线程内全局携带**，AOP 接口出入口放，请求结束清理。

## 10. 最佳实践

### 常见问题

1. **logback.xml 不能读取 spring 变量**：改用`logback-spring.xml`
2. 日志重复打印：`<logger>`标签忘记写`additivity="false"`，日志向上传递到 root，输出多次
3. 磁盘打满：忘记配置`maxHistory`、`totalSizeCap`，日志无限堆积
4. 异步日志丢日志：`discardingThreshold>0`，队列满丢弃低级别日志
5. 中文乱码：encoder 指定`charset>UTF-8`
6. 异常堆栈不输出：pattern 缺少`%wEx`

### 生产实践

1. 使用 `logback-spring.xml`
2. 区分控制台（dev）、文件输出（prod）
3. 业务日志 + 独立 error 日志
4. 开启异步 Appender 提升性能
5. 设置保留天数、总容量上限，防止磁盘爆满
6. 接入 MDC 打印 traceId，链路追踪
7. 第三方包（spring、mybatis）级别设置为 WARN，减少无效日志
8. 开启`<configuration debug="true">`，查看 logback 自身加载日志
9. 使用`StatusPrinter.print()`打印内部状态（代码调试）

### ✅ 应该做的

| 场景 | 做法 |
|------|------|
| 业务关键节点 | 使用 `info()` 记录操作开始/结束 |
| 异常捕获 | 使用 `error()` 并传入异常对象 |
| 调试信息 | 使用 `debug()` + `{}` 占位符 |
| 高频日志 | 使用 `debug()` 或 `trace()`，避免性能影响 |
| 日志中包含上下文 | 记录关键 ID（userId、orderId 等）便于追踪 |

### ❌ 不应该做的

| 场景 | 反例 | 正确做法 |
|------|------|----------|
| 字符串拼接 | `log.info("用户：" + name)` | `log.info("用户：{}", name)` |
| 吞掉异常堆栈 | `log.error("失败：" + e.getMessage())` | `log.error("失败", e)` |
| System.out.println | `System.out.println("调试")` | `log.debug("调试")` |
| 循环内大量日志 | 每次循环都 `log.debug()` | 聚合后记录一次 |
| 在日志中打印敏感信息 | 密码、token 等 | 脱敏后记录 |

### 敏感信息脱敏示例

```java
// ❌ 危险：打印密码
logger.info("用户登录，密码：{}", password);

// ✅ 安全：脱敏处理
logger.info("用户登录，用户名：{}", username);
logger.info("用户登录，密码长度：{}", password.length());
```

---

## 11. 故障排查

### Q1：日志文件没有生成？

1. 确认 `D:/rizi/` 目录有写入权限
2. 检查 `logback-spring.xml` 是否在 `classpath` 下
3. 启动时观察控制台是否有 Logback 初始化错误

### Q2：日志文件内容为空？

1. 检查日志级别设置：`root level="INFO"` 才会写入 INFO 日志
2. 检查 Appender 的 filter 配置是否正确
3. 确认代码中的 logger 调用是否在业务路径上

### Q3：中文乱码？

配置文件中编码器已设置 `<charset>UTF-8</charset>`，确保：
- 编辑器打开日志文件使用 UTF-8 编码
- 文件系统支持 UTF-8（Windows 下可能需要 chcp 65001）

### Q4：日志滚动没触发？

1. 确认 `<maxFileSize>1MB</maxFileSize>` 配置正确
2. 观察当前文件大小是否接近 1MB
3. 检查磁盘空间是否充足

### Q5：ERROR 日志没写入 error.log？

`error.log` 使用 `LevelFilter` 只接受 `ERROR` 级别。如果代码中使用了 `logger.error()` 但文件为空，检查：
1. 是否有异常真正发生
2. Appender 是否被正确引用

---

## 12. 快速参考卡

```java
// Logger 声明
private static final Logger log = LoggerFactory.getLogger(ClassName.class);

// 日志调用
log.trace("追踪：{}", value);     // 最细粒度
log.debug("调试：{}", value);     // 调试信息
log.info("信息：{}", value);       // 重要信息
log.warn("警告：{}", value);       // 警告
log.error("错误：{}", value, e);   // 错误（带异常堆栈）

// 配置文件
// 位置：src/main/resources/logback-spring.xml
// 路径：D:/rizi/
// 切割：1MB 序号归档
// 级别：info.log(INFO+) / error.log(仅ERROR)
```

## 13.AI生成模板

### 基础信息

- 文件：`resources/logback-spring.xml`（SpringBoot 专属，可读取 spring 配置）
- 日志根目录：`D:\lianxishi-rizi`
- 日志规范：每条日志要回答 5 个问题：**谁 (traceId/userId)、在哪 (类 + 方法)、做了什么 (脱敏入参)、结果如何 (返回 / 异常)、耗时 (ms)**
- 代码埋点规范（8 类日志场景）
  | # | 位置 | 级别 | 必含信息 |

|---|---|---|---|
|1 | 接口出入口 (AOP 统一打印)|INFO|URL、脱敏入参、返回码、耗时 |
|2 | 业务关键节点 | INFO | 状态流转、业务主键 orderId|
|3 | 外部调用 (出方向)|INFO/WARN | 参数、响应码、耗时、重试次数 |
|4 | 第三方回调 (入方向)|INFO | 回调参数、验签结果（纠纷证据）|
|5 | 异常 | ERROR | 堆栈、业务上下文 |
|6 | 降级 / 自愈 | WARN | 重试成功、兜底触发 |
|7|MQ 消费 / 定时任务 | INFO | 开始、结束、处理条数、失败重试 |
|8 | 安全审计 | WARN | 登录失败、越权尝试、敏感操作 |

### 文件输出规则

1. **info.log**：当日 INFO 日志；滚动文件命名 `info-日期.0.log`，单文件上限 100M，按天 + 大小双切割
2. **warn.log**：当日 WARN 日志；滚动文件命名 `warn-日期.0.log`，单文件上限 100M，按天 + 大小双切割
3. **error.log**：当日 ERROR 日志；滚动文件命名 `error-日期.0.log`，单文件上限 100M，按天 + 大小双切割
4. **DEBUG.log**：控制台 DEBUG 级别日志同步写入此文件；**应用重启自动清空 DEBUG.log**
5. **慢 SQL 日志**：独立 Appender，文件名模板 `BF-202607031052-slow.2026-09-07.0.log`，按天切割，新文件输出

### 日志 Pattern（适配 5 问：traceId, 类名 + 方法，耗时）

```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %X{traceId:-} %X{userId:-} %logger{36}.%M - %msg cost:%d{ms}%n
```

> 字段说明：
>
>
> - `%X{traceId:-}` MDC 链路 ID
> - `%X{userId:-}` MDC 用户 ID
> - `%logger{36}.%M` 类名。方法名
> - `cost:%d{ms}` 耗时毫秒（业务代码放入 MDC）