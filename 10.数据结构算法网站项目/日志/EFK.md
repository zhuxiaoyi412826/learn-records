Elastic Stack 是什么

```
在上面是一个典型的运用 Elastic Stack 的架构。我们通常不会在客户端直接调用 Elasticsearch 的 REST 接口。取而代之的是，我们使用一个 Search Service 作为中间接口。Search Service 再向 Elasticsearch 发送请求。我们可以通过 Beats 来采集 service 的日志及指标信息，我们甚至可以使用 Elastic Stack 所提供的 APM （应用性能监控）来监控应用及服务的性能并调优。Beats 所收集的信息，我们可以直接发送至 Elasticsearch，也可以发送至 Logstash 对数据做更进一步的加工（丰富，转换，删除，结构化等）再发送至 Elasticsearch。我们可以通过 Kibana 对数据进行可视化，分析，管理，对服务进行监控等。

在 Elastic 公司，我们称上面的技术栈为 Elastic Stack。
```

**Elasticsearch** 是一个分布式的基于 [REST](https://www.jianshu.com/p/ee92c9accedd) 接口的为云而设计的搜索引擎

**功能特性**

```
布式及高可用性的搜素引擎
每个索引（index）都使用可配置数量的分片进行完全分片
每个分片都可以有一个或多个副本
在任何副本分片上可执行读取/搜索操作
多租户
支持多个索引
索引级别配置（分片数，索引存储，......）
各种API
HTTP RESTful API
Native Java API
所有 API 都执行自动节点操作重新路由

面向文档
无需前期定义 schema （文档结构）
可以定义 schema 以定制索引过程
可靠，异步写入，可实现长期持续性
(近)实时搜索
建在 Lucene 之上
每个分片都是一个功能齐全的 Lucene 索引
Lucene 的所有功能都可以通过简单的配置/插件轻松暴露出来
每次操作一致性
单文档级操作具有原子性，一致性，隔离性和持久性。
```

**使用 cURL 命令和 Elasticsearch 对话**



```
<VERB> ：适当的 HTTP 方法或动词。 例如，GET，POST，PUT，HEAD 或 DELETE
<PROTOCOL>：http 或 https。 如果你在 Elasticsearch 前面有一个 HTTPS 代理，或者你使用 Elasticsearch 安全功能来加密 HTTP 通信，请使用后者
<HOST>：Elasticsearch 集群中任何节点的主机名。 或者，将 localhost 用于本地计算机上的节点
<PORT>：运行 Elasticsearch HTTP 服务的端口，默认为 9200
<PATH>：API 端点，可以包含多个组件，例如 _cluster/stats 或 _nodes/stats/jvm
<QUERY_STRING>：任何可选的查询字符串参数。 例如，?pretty 将漂亮地打印 JSON 响应以使其更易于阅读
<BODY>：JSON 编码的请求正文（如有必要）

```

```
检测是否安装成功
$ curl -XGET 'http://localhost:9200/' -H 'Content-Type: application/json'
```

创建索引

```
curl -XPUT 'http://localhost:9200/twitter/_doc/1?pretty' -H 'Content-Type: application/json' -d '
{
    "user": "kimchy",
    "post_date": "2009-11-15T13:12:00",
    "message": "Trying out Elasticsearch, so far so good?"
}'
 
curl -XPUT 'http://localhost:9200/twitter/_doc/2?pretty' -H 'Content-Type: application/json' -d '
{
    "user": "kimchy",
    "post_date": "2009-11-15T14:12:12",
    "message": "Another tweet, will it be indexed?"
}'
 
curl -XPUT 'http://localhost:9200/twitter/_doc/3?pretty' -H 'Content-Type: application/json' -d '
{
    "user": "elastic",
    "post_date": "2010-01-15T01:46:38",
    "message": "Building the site, should be kewl"
}'
```

搜索







数据摄取、丰富、存储、分析和可视化的免费开放工具

lastic 公司也同时拥有 Logstash 及 Kibana 开源项目。这个三个项目组合在一起，就形成了 ELK 软件栈。他们三个共同形成了一个强大的生态圈。简单地说，Logstash 负责数据的采集，处理（丰富数据，数据转换等），Kibana 负责数据展示，分析，管理，监督，警报及方案。Elasticsearch 处于最核心的位置，它可以帮我们对数据进行存储，并快速地搜索及分析数据。随着后来的 Beats 加入，ELK  软件栈，也被称为 ELKB。

Beats 是一些轻量级可以允许在客户端服务器中的代理。它并不需要部署到我们的 Elastic 云中。它可以帮我们收集所有需要的事件。如果把 Beats 也纳入到我的架构中，

运行在**源机器**（产生日志的服务器、容器），专门做：**读取本地数据，简单预处理，发送给下游（Fluentd / Logstash / Kafka / Elasticsearch）**。





# EFK引入

## 核心理念差异

1. **Filebeat：只管采集搬运**

> “我把日志读出来，安全交给下游，解析过滤交给后面组件” 适合：服务器、传统业务，**主要输出到 Elasticsearch**，不想写复杂采集处理逻辑。

1. **Fluentd：统一日志层（Unified Logging Layer）**

> “在本端就完成日志解析、清洗、路由，一份日志分发到多个目的地” 适合：多数据源、一份日志同时发给 ES、S3、Kafka；需要复杂日志转换；非 Elastic 为主的混合架构。

| 对比项        | Filebeat                                                     | Fluentd                                                      |
| ------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 开发方        | Elastic，Go 语言开发                                         | CNCF，Ruby+C 混合开发腾讯云开发...                           |
| 定位          | **轻量采集 Agent**，侧重读取、转发日志，**处理能力弱**       | **完整日志处理引擎**：采集 + 过滤 + 转换 + 路由 + 输出一站式 |
| 生态归属      | Elastic Stack (ELK) 原生组件Elastic                          | 厂商中立，不属于 Elastic 生态，插件数量几千个                |
| 数据处理      | 仅简单处理（多行、基础 json 解析）；复杂处理交给 Logstash / ES Ingest Pipeline | 内置完整 filter 流水线：字段提取、转换、过滤、丢弃、修改标签，本地即可完成复杂解析 |
| 输入源        | 主要：文件、容器 stdout；其它输入较少                        | 文件、syslog、kafka、mysql binlog、tcp、udp 等**超多数据源**，靠插件扩展腾讯云 |
| 输出目标      | ES、Logstash、Kafka、Redis 等，优先适配 Elastic 栈           | 几乎所有存储 / 消息队列：ES、S3、Kafka、ClickHouse、数据库等，多输出路由能力强 |
| 缓冲 / 可靠性 | 文件 offset 持久化；**磁盘缓冲能力弱**，网络中断内存排队，极端情况丢数据；支持背压机制Elastic | 强大内存 + 磁盘持久化缓冲，队列落盘，网络断开不易丢日志；支持多输出故障隔离 |
| 配置语法      | yaml，简单易上手，官方模块开箱即用（nginx/mysql 等）         | 自定义 DSL 语法，ruby 插件，学习成本高，调试麻烦腾讯云       |
| 资源占用      | 内存 30‑50MB，Go，资源很低                                   | 内存 60MB+，Ruby 虚拟机开销更大；**节点 agent 一般不推荐 Fluentd，优先 Fluent‑Bit**腾讯云开发... |
| 典型架构      | Filebeat → Logstash/Kafka → ES（EFK）                        | Fluentd 直接输出 ES；或 Fluent‑Bit 采集 → Fluentd 聚合处理 → ES/S3 |

# 关键词屏蔽

## 本地内存关键词屏蔽

检测是 Java 干的活；ES 只是把 Java 干完活留下的工作记录全部存起来。

敏感词过滤 ES收集反馈到MySQL 加强敏感词表

```
1.用户提交作答内容
    ↓
2.SpringBoot JVM内存DFA敏感词检测（提交链路不访问ES、Redis）
    ├A：命中已有敏感词
    │   ·输出WARN日志：userId、problemId、submission_id、hit_words、submit_content_short截断摘要
    │   ·MySQL保存submission status=BLOCKED
    │   ·返回前端提示违规
    │
    └B：未命中现有敏感词，但业务判定疑似违规（人工标记/业务规则识别）
        ·正常放行，作答完整内容存入MySQL submission
        ·输出INFO标记日志 message:"疑似违规内容，需要人工复核"，带上submit_content_short、submission_id

3.日志写入磁盘文件 → Filebeat采集 → Elasticsearch存储事件索引

4.【定时任务半自动提取候选新词】
    ↓定时（例如每小时执行一次）SpringBoot定时任务
    4‑1 使用ES RestHighLevelClient调用ES查询API
        查询条件：
        ①时间窗口：最近1小时/最近24小时
        ②过滤：level:WARN（命中敏感词） OR message:"疑似违规内容，需要人工复核"
    4‑2 批量拉取ES日志文档，拿到 submit_content_short、hit_sensitive_words、submission_id、user_id
    4‑3 对submit_content_short文本做简单分词提取候选关键词（简单分词，不做DFA判断）
    4‑4 过滤掉已经存在于MySQL sensitive_word表中的已有敏感词，过滤掉过短无意义字符
    4‑5 将筛选后的【候选新词、来源submission_id、来源用户ID、发生时间】存入一张MySQL中间表 `sensitive_word_candidate`
        >⚠️这里仅仅存入候选表，**不会直接写入正式sensitive_word敏感词表**

5.管理后台读取 `sensitive_word_candidate` 候选词列表页面
    运营人员查看候选词，可以查看来源的submission_id，跳转查询原始提交记录
    ├人工审核通过：点击新增敏感词，插入正式表`sensitive_word`
    └人工判定为正常词汇：标记忽略，丢弃该候选

6.新增正式敏感词落库MySQL sensitive_word
    ↓触发敏感词库刷新 sensitiveManager.reload()
    SpringBoot读取MySQL正式敏感词，重建JVM内存DFA树

7.后续用户提交作答，新加入的敏感词即可被DFA拦截

```

## 远程调用ES

一般不会怎么干

# 跨服务日志链条

业务流程 

业务流程：用户提问 → SpringBoot 后端 → OpenFeign 调用 Python RAG 服务 → 向量库检索 → 拼接 Prompt → 请求大模型 → 返回回答给用户 EFK 作用：把全链路日志统一收集到 ES，Kibana 查看，**依靠 TraceId 实现一条请求完整串起 Java+Python 两段日志**。

**跨服务必须传递 TraceId，否则 Java 日志和 Python 日志完全割裂，看不出是同一个用户请求**。

```
用户前端(浏览器)
      ↓ HTTP请求
SpringBoot(Java后端)
  1. Filter拦截，生成TraceId（MDC）
  2. 打印业务日志（用户query、用户id）
  3. OpenFeign调用Python RAG服务，HTTP Header携带TraceId
      ↓ HTTP 携带 X‑Trace‑Id 请求头
Python(RAG知识库检索服务)
  4. 读取Header里X‑Trace‑Id，写入Python日志上下文
  5. 打印日志：向量库检索、召回文档、拼接prompt、调用大模型API
      ↓调用向量数据库（如Milvus）
      ↓调用大模型API（通义/OpenAI）
  6. RAG结果返回SpringBoot
SpringBoot
  7. 接收RAG返回，组装结果返回前端
————————日志采集分割线————————
SpringBoot磁盘日志文件 → Filebeat采集 → Elasticsearch
Python服务磁盘日志文件 → Filebeat采集 → Elasticsearch
      ↓
Kibana 根据 trace_id 筛选，查看整条完整链路所有日志

```

**完整时许图**

1. 用户提交问题，HTTP 请求打到 SpringBoot
2. Filter 生成 traceId，MDC 存入，logback 打印收到用户提问日志
3. SpringBoot OpenFeign 发起 HTTP 调用 Python RAG 服务，Header 带上`X‑Trace‑Id`
4. Python FastAPI 中间件读取`X‑Trace‑Id`，绑定到本次请求所有日志输出
5. Python 打印日志：收到用户 query
6. Python 调用 Milvus 向量数据库，打印检索日志，召回文档
7. Python 把原始 query + 召回文档拼接完整 Prompt，打印 prompt 日志
8. Python 调用大模型 API，打印调用耗时日志
9. 拿到大模型回答，组装 RAG 结果，HTTP 返回给 SpringBoot
10. SpringBoot 收到 RAG 返回，打印接收结果日志，组装 HTTP 响应返回前端
11. SpringBoot 日志文件、Python 日志文件持续写入磁盘
12. Filebeat 分别读取两个服务日志文件，发送 Elasticsearch
13. Kibana 输入 traceId，一次性看到整条请求所有环节的全部日志。

# 不跨服务日志链条

技术链路：前端浏览器 → Tomcat → SpringBoot (Filter → Interceptor → DispatcherServlet → Controller → Service → Mapper (MySQL))，全部在一个服务内部，日志输出到磁盘文件 → Filebeat 采集 → Elasticsearch 存储 → Kibana 查询分析。

**完整时序链条**

### 链路 1：用户查询题目（GET /problem/{id}）

1. 前端浏览器发起 HTTP 查询题目请求
2. Tomcat 接收 HTTP 请求，交给 SpringBoot Filter 链
3. JWT Filter 做登录鉴权，token 校验失败直接返回 401；校验通过放行
4. Interceptor 拦截器，解析登录用户，把 userId 存入 ThreadLocal
5. DispatcherServlet 分发到 ProblemController
6. Controller 接收路径变量 problemId，@Valid 参数校验
7. Controller 打印日志：`INFO 用户userId=1001 查询题目 problemId=2001`
8. 调用 ProblemService
9. Service 执行业务逻辑，调用 ProblemMapper 查询 MySQL 题目数据（题干、选项、难度）
10. Service 打印日志：`INFO 查询MySQL题目完成 problemId=2001`
11. Service 把题目数据返回 Controller
12. Controller 组装统一返回对象`Result<T>`
13. HttpMessageConverter 序列化为 JSON
14. Tomcat 组装 HTTP 响应报文返回前端
15. 前端接收 JSON 渲染题目页面

> 如果发生异常：异常向上抛出，`@RestControllerAdvice`全局异常处理器捕获，打印错误日志，输出错误 JSON 响应。

```
2026‑08‑15 23:10:01.100 INFO 用户userId=1001 查询题目 problemId=2001
2026‑08‑15 23:10:01.140 INFO 查询MySQL题目完成 problemId=2001
```

### 链路 2：用户提交作答（POST /submission/submit）

1. 前端提交作答 HTTP 请求（userId、problemId、用户代码 / 答案）
2. Tomcat 接收请求进入 SpringBoot
3. JWT Filter 鉴权，未登录直接返回 401
4. Interceptor 取出登录用户信息存入 ThreadLocal
5. DispatcherServlet 分发到 SubmissionController
6. Controller 接收提交参数，`@Valid`参数校验；校验失败抛出异常
7. Controller 打印日志：`INFO 用户userId=1001 提交作答 problemId=2001`
8. SubmissionService 执行业务
   - 打印日志：`INFO 用户作答内容：xxxx`（内容做截断）
   - Mapper 插入 submission 提交记录写入 MySQL
   - 打印日志：`INFO 写入submission提交记录成功`
   - 执行本地判题业务逻辑
   - Mapper 更新用户做题统计、题目提交统计
   - 打印日志：`INFO 判题完成，得分=80，更新统计数据入库`
9. Service 返回判题结果给 Controller
10. Controller 封装`Result<T>`统一响应对象
11. HttpMessageConverter 序列化为 JSON
12. Tomcat 返回 HTTP 响应给前端
13. 前端解析 JSON 展示判题结果

> 异常分支：SQL 异常、业务逻辑异常，向上抛到全局异常处理器，打印 ERROR 日志，输出错误响应。

```
2026‑08‑15 23:11:20.200 INFO 用户userId=1001 提交作答 problemId=2001
2026‑08‑15 23:11:20.230 INFO 写入submission提交记录成功
2026‑08‑15 23:11:20.450 INFO 判题完成，得分=80，更新统计数据入库
```