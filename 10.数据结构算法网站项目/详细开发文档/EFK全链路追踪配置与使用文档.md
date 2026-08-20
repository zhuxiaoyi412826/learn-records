# EFK + 全链路追踪 配置与使用文档

> 更新时间：2026-08-17
> 适用项目：AlgoVize（Spring Boot + Python 向量服务 + Chroma + MySQL）
> 技术栈：Elasticsearch 7.12.1 + Fluentd 1.14.3 + Kibana 7.12.1

---

## 一、架构总览

```
┌────────────┐    HTTP     ┌───────────────────────┐  OpenFeign   ┌──────────────────────┐
│ 前端浏览器 │────────────▶│ Spring Boot 后端(5000) │─────────────▶│ Python 向量服务(8001) │
└────────────┘             └──────────┬────────────┘              └──────────┬───────────┘
                                      │                                          │
                                      │ 写日志                                   │ 写日志
                                      ▼                                          ▼
                           ┌──────────────────────┐                   ┌──────────────────
                           │   D:/rizi/info.log   │            │ D:/rizi/python-app.log │
                           │   D:/rizi/error.log  │                   └──────────┬───────
                           └──────────┬───────────┘                              │
                                      │                                          │
                                      └──────────────┬───────────────────────────┘
                                                     │ Fluentd tail 采集 (filter清洗)
                                                     ▼
                                            ┌─────────────────┐
                                            │    Fluentd      │
                                            │  (解析/格式化)   │
                                            └────────┬────────┘
                                                     │ HTTP bulk 写入
                                                     ▼
                                            ┌─────────────────┐
                                            │  Elasticsearch  │  ←索引algoviz-logs-YYYY.MM.DD
                                            │    (7.12.1)     │
                                            └────────┬────────┘
                                                     │ 查询
                                                     ▼
                                            ┌─────────────────┐
                                            │    Kibana       │  ← 按TraceId 聚合查询全链路
                                            │    (7.12.1)     │
                                            └─────────────────┘
```

**核心设计**：以 `TraceId` 为串联锚点，在 Java 和 Python 两边的日志里都带上同一个 ID，Fluentd 解析后统一写入 ES，Kibana 按 TraceId 聚合成一条完整的调用链。

---

## 二、环境准备

### 2.1 版本与组件

| 组件 | 版本 | 作用 | 监听端口 |
|------|------|------|---------|
| Elasticsearch | 7.12.1 | 日志存储与检索 | 9200 |
| Kibana | 7.12.1 | 日志可视化与查询 | 5601 |
| Fluentd (td-agent) | 1.14.3 | 日志采集与解析 | 24220（监控） |
| fluent-plugin-elasticsearch | 4.3.3 ~ 5.1.4 | Fluentd 输出到 ES | - |
| Spring Boot | 内置 | 业务后端（Java） | 5000 |
| Python FastAPI | 内置 | 向量服务（Python） | 8001 |

### 2.2 关键文件清单

| 用途 | 路径 |
|------|------|
| Fluentd 主配置 | [fluentd.conf](file:///d:/daima/XiangMu/算法数据结构可视化/AlgoVize/Agent/know-retrieval/fluentd.conf) |
| Fluentd → ES 索引模板 | [algoviz-template.json](file:///d:/daima/XiangMu/算法数据结构可视化/AlgoVize/Agent/know-retrieval/templates/algoviz-template.json) |
| Spring Boot 日志格式（含 TraceId） | [logback-spring.xml](file:///d:/daima/XiangMu/算法数据结构可视化/AlgoVize/houduan/src/main/resources/logback-spring.xml) |
| Java TraceId 生成/透传 Filter | [TraceIdFilter.java](file:///d:/daima/XiangMu/算法数据结构可视化/AlgoVize/houduan/src/main/java/com/algoviz/config/TraceIdFilter.java) |
| Java Feign 调用透传 TraceId | [FeignTracingInterceptor.java](file:///d:/daima/XiangMu/算法数据结构可视化/AlgoVize/houduan/src/main/java/com/algoviz/config/FeignTracingInterceptor.java) |
| Python TraceId 中间件 + 日志 | [trace_middleware.py](file:///d:/daima/XiangMu/算法数据结构可视化/AlgoVize/Agent/know-retrieval/app/trace_middleware.py) |
| Python 验证脚本 | [verify_tracing.py](file:///d:/daima/XiangMu/算法数据结构可视化/AlgoVize/Agent/know-retrieval/verify_tracing.py) |
| 日志统一存放目录 | `D:/rizi` |

---

## 三、分步配置过程

### 3.1 步骤一：Java（Spring Boot）侧 —— 生成 TraceId + 统一日志格式

**目标**：让 Spring Boot 每条日志带上 `TraceId`，并输出到 `D:/rizi` 目录，按 10MB 滚动，保留 30 天。

#### 3.1.1 `TraceIdFilter.java` —— 请求入口生成/提取 TraceId

在所有请求进入 Controller 前执行：
1. 从 HTTP Header `X-Trace-Id` 读取上游传来的 TraceId
2. 没有则用 `UUID` 生成 16 位短 ID
3. 放入 **MDC**（Mapped Diagnostic Context），logback 通过 `%X{trace_id}` 直接引用
4. 响应结束后清空 MDC，避免线程复用导致串 ID

```java
// 伪代码（实际见 TraceIdFilter.java）
public class TraceIdFilter extends OncePerRequestFilter {
    public static final String TRACE_HEADER = "X-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) {
        String traceId = req.getHeader(TRACE_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }
        MDC.put("trace_id", traceId);    // 关键：logback 的 %X{trace_id} 从这里取值
        try {
            chain.doFilter(req, resp);
        } finally {
            MDC.clear();                  // 线程池复用时必须清
        }
    }
}
```

#### 3.1.2 `FeignTracingInterceptor.java` —— 跨服务调用透传 TraceId

Spring Boot 通过 OpenFeign 调用 Python 向量服务时，**必须把 MDC 中的 trace_id 放到 HTTP Header 里**，否则 Python 端拿到的是另一个 ID，链条断开：

```java
@Component
public class FeignTracingInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        String traceId = MDC.get("trace_id");
        if (traceId != null) {
            template.header("X-Trace-Id", traceId);
        }
    }
}
```

#### 3.1.3 `logback-spring.xml` —— 日志格式 + 滚动策略

关键配置（[logback-spring.xml#L4-L8](file:///d:/daima/XiangMu/算法数据结构可视化/AlgoVize/houduan/src/main/resources/logback-spring.xml#L4-L8)）：

```xml
<!-- 日志统一写到 D:/rizi（与 Python 端统一，Fluentd 集中采集） -->
<property name="LOG_PATH" value="D:/rizi"/>

<!-- 格式：时间 [TraceId] [线程] 级别 Logger - 消息 -->
<!-- %X{trace_id:-no-trace}：MDC 中有 trace_id 就用，没有就显示 no-trace -->
<property name="LOG_PATTERN"
          value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%X{trace_id:-no-trace}] [%thread] %-5level %logger{36} - %msg%n"/>
```

滚动策略使用 `SizeAndTimeBasedRollingPolicy`（按天 + 按大小双重触发）：

```xml
<rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
    <fileNamePattern>${LOG_PATH}/info-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
    <maxFileSize>10MB</maxFileSize>          <!-- 单文件上限 10MB -->
    <maxHistory>30</maxHistory>              <!-- 归档保留 30 天 -->
    <totalSizeCap>5GB</totalSizeCap>         <!-- 所有归档总和不超 5GB -->
</rollingPolicy>
```

### 3.2 步骤二：Python（FastAPI）侧 —— 提取 TraceId + 统一日志格式

**目标**：Python 向量服务从 Header 提取 Java 传来的 TraceId，日志格式与 Java 对齐，也写到 `D:/rizi`。

#### 3.2.1 `trace_middleware.py` —— FastAPI 中间件 + 日志配置

```python
# 1. TraceId 存到 ContextVar（ASGI 下比 threading.local 安全）
trace_id_var: ContextVar[str] = ContextVar("trace_id", default="no-trace")

# 2. 日志文件路径（与 Java 统一在 D:/rizi）
LOG_DIR = Path("D:/rizi")
LOG_DIR.mkdir(parents=True, exist_ok=True)
LOG_FILE = LOG_DIR / "python-app.log"

# 3. 自定义 Formatter —— 在每条日志里注入 trace_id
class TraceIdFormatter(logging.Formatter):
    def format(self, record):
        if not hasattr(record, "trace_id"):
            record.trace_id = trace_id_var.get()
        return super().format(record)

# 4. FastAPI 中间件
class TraceIdMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request: Request, call_next):
        # 从 Header 取 Java 端传来的 TraceId；没有则新生成
        incoming_trace = request.headers.get("X-Trace-Id")
        trace_id = incoming_trace or generate_trace_id()

        token = trace_id_var.set(trace_id)    # 存入 ContextVar
        try:
            logging.getLogger("algoviz").info(f"请求入口 method={request.method} path={request.url.path}")
            response = await call_next(request)
            response.headers["X-Trace-Id"] = trace_id  # 响应头也带回
            return response
        finally:
            trace_id_var.reset(token)         # 清理
```

日志格式字符串为：

```
fmt = "%(asctime)s [%(trace_id)s] [%(levelname)s] %(name)s - %(message)s"
datefmt = "%Y-%m-%d %H:%M:%S"   # 注意：Python 默认不带毫秒，Fluentd 正则要匹配此格式
```

### 3.3 步骤三：Fluentd 配置 —— 采集、解析、写 ES

**目标**：Fluentd 同时 tail Java 和 Python 两个日志文件，用正则拆字段，加元数据（service/language/component），批量写 ES。

#### 3.3.1 Source 配置（核心）

| Source | 采集文件 | 正则格式重点 |
|--------|---------|-------------|
| `algoviz.backend` | `D:/rizi/info.log,error.log,info-*.log,error-*.log` | 带 **毫秒** 的时间 `HH:mm:ss.SSS` + 多了 `[线程]` 字段 |
| `algoviz.vector` | `D:/rizi/python-app.log` | **无毫秒** 的时间 `HH:mm:ss` + 无线程字段 |

**Java 解析正则**（[fluentd.conf#L70](file:///d:/daima/XiangMu/算法数据结构可视化/AlgoVize/Agent/know-retrieval/fluentd.conf#L70)）：
```
format1 /^(?<logtime>\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}\.\d{3}) \[(?<trace_id>[^\]]+)\] \[(?<thread>[^\]]+)\] (?<level>[A-Z]+)\s+(?<logger>[^\s]+) - (?<message>.*)$/
```

**Python 解析正则**（[fluentd.conf#L105](file:///d:/daima/XiangMu/算法数据结构可视化/AlgoVize/Agent/know-retrieval/fluentd.conf#L105)）：
```
format1 /^(?<logtime>\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}) \[(?<trace_id>[^\]]+)\] \[(?<level>[A-Z]+)\] (?<logger>[^\s]+) - (?<message>.*)$/
```

> **重要坑点**：TraceId 正则使用 `[^\]]+`（任意非 `]` 字符），不要写成严格的 `[a-f0-9]{16}`。否则外部传入的 `test-trace-xxx`、UUID 等格式全部解析失败，直接丢弃。

#### 3.3.2 不要设置 `encoding UTF-8`

Fluentd 的 tail 插件在读取 UTF-8 文件时**不能设置 `encoding UTF-8`**，否则会触发二次转码，中文全部变成 `\uFFFD`（替换字符）。直接删除 `encoding` 行即可。

#### 3.3.3 多行堆栈处理

```
<parse>
  @type multiline
  format_firstline /^\d{4}-\d{2}-\d{2}/   # 日期开头 = 新日志的开始
  format1 /...真正的正则.../               # 匹配第一行
  # 后续不以日期开头的行（堆栈）自动合并到上一条
</parse>
```

这样 Java 的 `NullPointerException` 大堆栈、Python 的 `Traceback (most recent call last):` 都会被识别成一条日志，不会被拆成几十条垃圾。

#### 3.3.4 Filter：补字段 + 转 @timestamp

对所有 `algoviz.**` 日志加 `hostname`；把 `logtime` 赋给 ES 标准字段 `@timestamp`：

```xml
<filter algoviz.**>
  @type record_transformer
  enable_ruby true
  <record>
    @timestamp ${record["logtime"]}
  </record>
</filter>
```

#### 3.3.5 Output：写入 Elasticsearch

```xml
<match algoviz.**>
  @type elasticsearch
  host localhost
  port 9200

  logstash_format true
  logstash_prefix algoviz-logs            # 索引名：algoviz-logs-2026.08.17
  logstash_dateformat %Y.%m.%d

  template_name algoviz-logs
  # ⚠️ 必须写项目实际路径，不能写不存在的 C:/fluentd/templates/
  template_file d:/daima/XiangMu/算法数据结构可视化/AlgoVize/Agent/know-retrieval/templates/algoviz-template.json
  template_overwrite true

  # 性能调优
  flush_size 500
  flush_interval 5s
  async true

  # 文件缓冲（ES 宕机时不丢日志，自动重试）
  <buffer>
    @type file
    path C:/fluentd/buffer/algoviz
    chunk_limit_size 16MB
    total_limit_size 5GB
    retry_wait 5s
    retry_max_times 10
    overflow_action block
  </buffer>
</match>
```

### 3.4 步骤四：Elasticsearch 索引模板

[algoviz-template.json](file:///d:/daima/XiangMu/算法数据结构可视化/AlgoVize/Agent/know-retrieval/templates/algoviz-template.json) 定义字段类型，关键几列：

| 字段 | ES 类型 | 原因 |
|------|---------|------|
| `@timestamp` | `date` | Kibana 时间轴依赖 |
| `logtime` | `date` | 格式兼容毫秒/无毫秒两种 |
| `trace_id` | `keyword` | **精确匹配**聚合查询用 keyword，不是 text |
| `level` | `keyword` | 按级别筛选用 |
| `service` / `language` / `component` | `keyword` | 按服务筛选 |
| `message` | `text` | 全文搜索，用 standard 分词器 |
| `logger` | `text + keyword` 多字段 | 既可全文搜 logger 名，也可精确聚合 |

**模板会在 Fluentd 启动时自动推送到 ES**（`template_overwrite true` 每次覆盖）。手动推送也可以：

```bash
curl -XPUT "http://localhost:9200/_template/algoviz-logs" \
  -H "Content-Type: application/json" \
  -d @templates/algoviz-template.json
```

### 3.5 步骤五：启动顺序

```
① 先启 Elasticsearch →  http://localhost:9200
② 再启 Kibana        →  http://localhost:5601
③ 启动 Spring Boot   →  写日志 D:/rizi/info.log
④ 启动 Python 服务   →  写日志 D:/rizi/python-app.log
  cd AlgoVize/Agent/know-retrieval
  python -m app.main
⑤ 启动 Fluentd       →  tail 两个日志 →  批量写 ES
  fluentd -c d:\daima\XiangMu\算法数据结构可视化\AlgoVize\Agent\know-retrieval\fluentd.conf
```

启动 Fluentd 正常输出：

```
following tail of D:/rizi/info.log
following tail of D:/rizi/error.log
following tail of D:/rizi/info-*.log
following tail of D:/rizi/python-app.log
fluentd worker is now running worker=0
```

出现 `pattern not matched`、乱码、template not found → 见「六、常见问题排查」。

---

## 四、从"用户语义搜索"到 Kibana 展示的完整链路

### 4.1 流程全景

以下是**用户在前台页面输入「多线程」点击语义搜索**时，发生的每一步：

```
 ① 用户 → 前端                              输入关键词，前端发 HTTP 语义搜索请求
     │                                      POST http://localhost:5000/api/.../semantic-search
     ▼
 ② Spring Boot TraceIdFilter                从请求头 X-Trace-Id 取，没取到 → 新生成
     │                                      MDC.put("trace_id", "abc123def4567890")
     ▼
 ③ Spring Boot Controller/Service           记录 INFO: 收到语义搜索请求，query=多线程
     │                                      → info.log 写入一条 [abc123def4567890]
     ▼
 ④ Spring Boot → Feign                      调用向量服务 embedding/search
     │                                      FeignTracingInterceptor 自动把
     │                                      X-Trace-Id: abc123def4567890 放进请求头
     ▼
 ⑤ Python TraceIdMiddleware                 从请求头拿到 abc123def4567890
     │                                      存入 ContextVar
     ▼
 ⑥ Python embedding/search 逻辑             记录"收到搜索请求"、"向量查询命中 N 条"...
     │                                      → python-app.log 写入 2-3 条 [abc123def4567890]
     ▼
 ⑦ Python → Chroma 向量检索                 (向量库内部日志，可选，本文未采集)
     ▼
 ⑧ Python 返回响应 + 响应头带 X-Trace-Id     返回给 Spring Boot
     ▼
 ⑨ Spring Boot Service 继续处理             用向量命中的 problem_id 去 MySQL 查询题目详情
     │                                      → info.log 再写"SQL 查询命中 N 条"
     ▼
⑩ Spring Boot Filter doFilter finally       MDC.clear()  清理线程
     ▼
⑪ 返回结果给前端                            前端展示题目列表
```

与此同时，**Fluentd 在后台默默工作**：
- Fluentd tail 发现 `info.log` 有新增 → 正则解析 → 写 ES
- Fluentd tail 发现 `python-app.log` 新增 → 正则解析 → 写 ES
- 最终两条来源**同一个 trace_id** 的日志落在同一个 ES 索引里

### 4.2 本地日志 vs Kibana 日志对比表

| 维度 | 本地磁盘日志 `D:/rizi/*.log` | Kibana / Elasticsearch |
|------|------------------------------|------------------------|
| **存储介质** | 本地文本文件（滚动分割） | 分布式索引 `algoviz-logs-YYYY.MM.DD` |
| **保留策略** | logback maxHistory=30 天，自动删 | 可手动/ILM 生命周期管理（见第五节） |
| **跨服务关联** | 两个文件分开，无法自动关联 | 通过 `trace_id=abc123...` 一条 DSL 全拉出来 |
| **查询能力** | grep/Notepad++ 正则搜索 | **倒排索引**毫秒级全文搜索 + 字段过滤 + 聚合 |
| **多条件组合** | 需写复杂 sed/awk 脚本 | 可视化点选：service=backend AND level=ERROR AND 时间范围 |
| **时间范围** | 按文件名找日志文件 | Kibana 时间轴拉条，任意粒度 |
| **堆栈聚合** | 肉眼翻多行 | multiline 合并成一条记录，message 字段完整保留堆栈 |
| **可视化** | 无 | 折线图看错误数、饼图看服务分布、TraceId 做调用链路 Dashboard |
| **丢失风险** | 磁盘坏了就丢 | ES 副本 + Fluentd 磁盘缓冲双保险 |
| **检索耗时** | 查 1 个月日志 → 几分钟 | 1 亿条日志 → 毫秒级 |
| **适合场景** | 实时开发调试、快速 tail -f | 生产排障、全链路分析、长期审计、趋势报表 |

### 4.3 Kibana 查询示例

假设一次请求的 TraceId 是 `abc123def4567890`：

#### ① 基础查询：拉取同一条链路所有日志

在 Kibana → Discover → KQL 栏输入：

```
trace_id: "abc123def4567890"
```

按 `@timestamp` 升序，应该能看到类似：

| 时间 | service | level | message |
|------|---------|-------|---------|
| 17:09:17.101 | algoviz-backend | INFO | 收到语义搜索请求，query=多线程 |
| 17:09:17.105 | algoviz-backend | DEBUG | Feign 调用向量服务 embedding/search |
| 17:09:17.110 | algoviz-vector | INFO | 请求入口 method=POST path=/api/v1/search |
| 17:09:17.230 | algoviz-vector | INFO | 向量检索完成，命中 5 条，最高相似度 0.7544 |
| 17:09:17.240 | algoviz-backend | INFO | Feign 返回，5 道题，耗时 135ms |
| 17:09:17.252 | algoviz-backend | INFO | MySQL 查询题目详情，耗时 12ms |
| 17:09:17.258 | algoviz-backend | INFO | 返回语义搜索结果，共 5 道题 |

**完整一条链**，按时间顺序从 Java 进、到 Python 回、再到 Java 出，一目了然。

#### ② 异常排查：筛某服务错误

```
service: "algoviz-backend" AND level: "ERROR"
```

#### ③ 慢请求分析：按 TraceId 聚合响应时间

```json
POST algoviz-logs-*/_search
{
  "size": 0,
  "aggs": {
    "by_trace": {
      "terms": { "field": "trace_id", "size": 1000 },
      "aggs": {
        "duration_ms": { "scripted_metric": { /* 首末日志时间差 */ } }
      }
    }
  }
}
```

或在 Kibana Dashboard 建「按 service 的错误率折线图」、「TraceId 分布饼图」。

---

## 五、后续扩展

### 5.1 手动创建 / 管理 ES 索引

Fluentd 用 logstash_format 会**自动按天创建索引**，但遇到以下场景需要手动操作：

#### 5.1.1 手动创建索引

```bash
# 创建一个一次性测试索引
curl -XPUT "http://localhost:9200/algoviz-logs-test-001" -H "Content-Type: application/json" -d '{
  "settings": {
    "index.number_of_shards": 1,
    "index.number_of_replicas": 0
  }
}'
```

#### 5.1.2 手动推送索引模板（Fluentd 启动也会推，但手动推方便测试）

```bash
curl -XPUT "http://localhost:9200/_template/algoviz-logs" \
  -H "Content-Type: application/json" \
  -d @d:/daima/XiangMu/算法数据结构可视化/AlgoVize/Agent/know-retrieval/templates/algoviz-template.json
```

查看已推送的模板：

```bash
curl "http://localhost:9200/_template/algoviz-logs?pretty"
```

#### 5.1.3 查看当前索引列表 + 状态

```bash
curl "http://localhost:9200/_cat/indices/algoviz-logs-*?v&s=index"
```

#### 5.1.4 删除过期索引（比如 2026-08-15 的历史）

```bash
curl -XDELETE "http://localhost:9200/algoviz-logs-2026.08.15"
```

### 5.2 接入 Index Lifecycle Management (ILM) —— 日志自动老化

> 生产必须上 ILM，否则磁盘会爆。**开发环境可以先不搞**，上线前弄。

策略示例（7 天热查询 → 30 天温存储 → 90 天后自动删除）：

```bash
PUT _ilm/policy/algoviz-logs-policy
{
  "policy": {
    "phases": {
      "hot": {
        "actions": { "rollover": { "max_age": "1d", "max_size": "50gb" } }
      },
      "warm": {
        "min_age": "7d",
        "actions": {
          "forcemerge": { "max_num_segments": 1 },
          "shrink": { "number_of_shards": 1 }
        }
      },
      "cold": { "min_age": "30d", "actions": { "freeze": {} } },
      "delete": { "min_age": "90d", "actions": { "delete": {} } }
    }
  }
}
```

把 ILM 策略挂到索引模板：在 algoviz-template.json 的 `settings` 里加：

```json
"index.lifecycle.name": "algoviz-logs-policy",
"index.lifecycle.rollover_alias": "algoviz-logs"
```

### 5.3 采集更多服务

按同样模式扩：**新服务写日志 → 开 Fluentd source → 加 filter 打 service 标签**。

```
# 示例：再加一个 Chroma 向量数据库的日志
<source>
  @type tail
  path D:/rizi/chroma.log
  tag algoviz.chroma
  ...
</source>

<filter algoviz.chroma>
  @type record_transformer
  <record>
    service    algoviz-chroma
    language   python
    component  chromadb
  </record>
</filter>
```

然后把写 Chroma 日志的代码里，同样从上游 Header 取 `X-Trace-Id` 注入日志，链条就自然延长。

### 5.4 前端浏览器日志接入（可选）

技术链路里前端浏览器的日志目前没采集。要做可以：

- 前端用 `postMessage` 把 `console.error` / 用户交互埋点打给后端 `/api/logs` 接口
- 后端用 `@Slf4j` 打 `BROWSER` 级别日志（或用自定义 Marker）
- Fluentd 侧 `level=BROWSER` 自动采集
- 同一个 TraceId 链条延伸到浏览器端

### 5.5 慢查询监控 Dashboard 建议

在 Kibana → Dashboard 新建以下 Panel：

| Panel 名称 | 类型 | 指标 / 过滤 |
|-----------|------|------------|
| 每分钟请求量 | 折线 | `service=*` 按时间 date_histogram count |
| 错误率（按服务） | 折线 | `level=ERROR` / `*` 分组 by service |
| TraceId 调用热度 Top10 | 条形 | terms agg on trace_id, order by count desc |
| Python 向量检索耗时分布 | 直方图 | `message` 包含"向量检索完成"的时间差 |
| 日志量（按语言） | 饼图 | terms agg by language |
| 超时请求列表 | Data Table | `message` 包含"timeout"或"exceeded"的过滤 |

### 5.6 告警接入（ElastAlert / Watcher）

典型规则：

- **ERROR 突增告警**：5 分钟内 ERROR 日志数量 > 50 → 企业微信/邮件告警
- **TraceId 断链**：Java 侧有 "调用向量服务"，但 30 秒内没出现 Python 侧的对应 TraceId → 告警
- **慢请求**：单次请求总耗时（Java 首末时间差）> 3s → 告警

---

## 六、常见问题排查

### 6.1 `pattern not matched: "..."` 警告

**现象**：Fluentd 启动后不断刷 `pattern not matched`。

**原因**：
- Java/Python 格式的正则写反了（比如用了 `,\d{3}` 毫秒去匹配无毫秒日志）
- TraceId 正则过严（`[a-f0-9]{16}` 匹配不上 `test-trace-xxx`）

**解决**：用 [algoviz-template.json 中 format1](#33-source-配置核心) 宽松版，TraceId 用 `[^\]]+`。

### 6.2 中文全变成 `\uFFFD`

**现象**：日志 message 字段出现大量 `\uFFFD`（Unicode 替换字符）。

**原因**：fluentd.conf 里 `encoding UTF-8` 让 Fluentd 对 UTF-8 文件二次转码。

**解决**：**删除所有 source 里的 `encoding UTF-8`**。tail 插件默认就是字节原样传输。

### 6.3 `If you specify a template_name you must specify a valid template file`

**现象**：Fluentd 刷 buffer 时 RuntimeError。

**原因**：`template_file C:/fluentd/templates/algoviz-template.json` 路径不存在。模板在项目目录里，不在 Fluentd 安装目录。

**解决**：改成项目实际绝对路径：
```
template_file d:/daima/XiangMu/算法数据结构可视化/AlgoVize/Agent/know-retrieval/templates/algoviz-template.json
```

如果路径里有中文导致 Ruby 编码问题，**备选方案**：把模板文件复制到纯英文路径（如 `D:/fluentd-templates/algoviz-template.json`）再配置。

### 6.4 Fluentd 采集了旧归档（`info-1.log` ~ `info-21.log`）

**现象**：启动时 tail 了一堆 `info-1.log` 等旧文件。

**原因**：这些是前版 logback（FixedWindowRollingPolicy，1MB 分割）遗留的归档文件，被 `info-*.log` 通配符匹配。

**解决**：
```powershell
# 在 D:/rizi 目录下清理历史归档（只保留 info.log / info-YYYY-MM-DD.N.log 格式）
Remove-Item D:/rizi/info-[0-9]*.log, D:/rizi/error-[0-9]*.log
# Fluentd 重启前顺带清 pos 记录
Remove-Item C:/fluentd/pos/*.pos -Force
```

### 6.5 日志进了 ES 但字段没按模板（trace_id 是 text 不是 keyword）

**原因**：索引创建时模板还没推送到 ES，Fluentd 动态映射把 trace_id 当 text。

**解决**：
```bash
# 1. 手动推模板
curl -XPUT "http://localhost:9200/_template/algoviz-logs" ...（见 5.1.2）

# 2. 删除当天旧索引（它用的是动态映射，重建才能用模板）
curl -XDELETE "http://localhost:9200/algoviz-logs-$(Get-Date -Format 'yyyy.MM.dd')"

# 3. 重启 Fluentd，新索引用模板创建
```

### 6.6 TraceId 不同步（Java 和 Python 不是同一个）

**排查顺序**：
1. 确认 `FeignTracingInterceptor` 已注册成 `@Component` 被 Spring 扫描
2. 抓包看 Feign 发出去的 Header 里是否带 `X-Trace-Id`
3. Python 中间件取 `request.headers.get("X-Trace-Id")`，如果是 Feign 默认行为**Header 名会转小写** → FastAPI 默认忽略大小写，一般没问题；自定义的库要注意。
4. 日志格式里 `trace_id` 确实是 `[xxx]` 且没有被格式化错误截断

### 6.7 Fluentd 丢了 Elasticsearch 连接但没恢复

看 buffer 目录 `C:/fluentd/buffer/algoviz` 有没有 `.q` 文件堆积：

- 有 → buffer 堆积，检查 ES 是否挂、网络是否通
- 无 → 检查 tail 是否还在走（`read_from_head true` 会每次从头读）

---

## 七、快速验证清单

上线/改配置后，按此清单依次打勾：

- [ ] Elasticsearch `GET /` 返回 JSON，版本 7.12.1
- [ ] Kibana `http://localhost:5601` 可访问
- [ ] Spring Boot 启动后 `D:/rizi/info.log` 有日志，格式含 `[TraceId]`
- [ ] Python 服务启动后 `D:/rizi/python-app.log` 有日志，格式含 `[TraceId]`
- [ ] Fluentd 启动无 `pattern not matched` 和乱码告警
- [ ] Fluentd 正常输出 `following tail of ...` 包含 4 种日志文件
- [ ] 做一次语义搜索，Java + Python 日志都写新条目
- [ ] ES `GET algoviz-logs-*/_count` 的 count 在增长
- [ ] Kibana 创建 Index Pattern `algoviz-logs-*`，time field 选 `@timestamp`
- [ ] 在 Discover 里搜同一个 TraceId，能看到 Java + Python 两边日志按时间排序

---

## 八、参考命令速查

```bash
# ===== ES 基础 =====
curl "http://localhost:9200/_cat/indices/algoviz-logs-*?v"        # 列表
curl "http://localhost:9200/algoviz-logs-2026.08.17/_count"        # 条数
curl -XDELETE "http://localhost:9200/algoviz-logs-2026.08.15"      # 删除某天
curl "http://localhost:9200/_template/algoviz-logs?pretty"         # 看模板

# ===== Fluentd =====
fluentd -c fluentd.conf --dry-run                                   # 配置语法检查
fluentd -c fluentd.conf                                             # 前台启动
# td-agent 包用 td-agent -c ... 代替 fluentd -c ...

# ===== 日志生成（触发全链路测试）=====
cd d:/daima/XiangMu/算法数据结构可视化/AlgoVize/Agent/know-retrieval
python verify_tracing.py                                            # 运行验证脚本

# ===== 直接发请求模拟跨服务链路 =====
curl -X POST "http://localhost:8001/api/v1/search" ^
  -H "Content-Type: application/json" ^
  -H "X-Trace-Id: demo-trace-0001" ^
  -d "{\"query\":\"多线程\",\"topK\":5,\"threshold\":0.1}"
```

---

**文档结束**。配置好后，全链路调试的关键体验是：任何一次用户语义搜索，**只需要拿响应里的 TraceId，粘到 Kibana 搜索框按回车，整条链每一步的耗时、日志、异常都按时间展开**，比以前分别 grep 两个日志文件拼时间省 90% 排障时间。
