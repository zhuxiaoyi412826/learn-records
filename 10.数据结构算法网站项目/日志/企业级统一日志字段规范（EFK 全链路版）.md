# 企业级统一日志字段规范（EFK 全链路版）

## 1\. 文档目的

统一前后端、服务端、中间件全链路日志字段定义、命名、格式、取值规范，解决日志字段混乱、无法聚合检索、无法全链路追踪、无法脱敏治理、Kibana 无法统一统计等问题。

本规范适用于：Java 后端服务、Python 服务、浏览器前端日志、网关日志，统一接入 EFK（Elasticsearch\+Fluentd\+Kibana）日志平台。

所有日志经过 Fluentd 清洗后，必须**严格遵守本字段结构**，禁止自定义随意字段。

## 2\. 整体日志分层

全链路日志分为两类，通过 `log_source` 字段硬性区分，Kibana 天然隔离：

- **server**：服务端日志（Java / Python / 网关）

- **browser**：浏览器前端日志（JS 报错、接口异常、用户埋点、性能、白屏）

## 3\. 全局通用字段（所有日志必含）

无论前后端，所有日志**强制携带**，用于检索、聚合、链路追踪、环境隔离。

|字段名|类型|必填|说明|示例值|
|---|---|---|---|---|
|@timestamp|date|是|日志标准时间戳（ES 检索时间基准，UTC ISO8601）|2026\-08\-17T10:20:30\.123Z|
|log\_source|keyword|是|日志来源：server / browser|server|
|env|keyword|是|运行环境，用于环境隔离|dev / test / prod|
|service\_name|keyword|是|服务名称，全局唯一|rag\-know\-service、gateway\-service|
|trace\_id|keyword|是|全链路追踪ID，**前后端贯通**，一次请求唯一|trace\-20260817102030123|
|span\_id|keyword|否|单环节子ID，用于细分调用链路|span\-abc123|
|log\_level|keyword|是|日志级别，统一枚举|DEBUG/INFO/WARN/ERROR|
|message|text|是|日志核心描述信息、异常摘要、业务说明|向量检索接口请求成功|
|host\_name|keyword|是|服务器主机名/设备标识|WIN\-DEV\-01|
|host\_ip|keyword|是|服务部署IP|127\.0\.0\.1|
|marker|keyword|否|特殊标记：BROWSER（标识前端上报日志）|BROWSER|

## 4\. 服务端专属字段（Java/Python/网关）

仅 `log_source=server` 日志使用，用于接口、业务、异常排查。

|字段名|类型|必填|说明|示例|
|---|---|---|---|---|
|api\_path|keyword|否|请求接口路径|/api/rag/search|
|http\_method|keyword|否|请求方式|GET/POST|
|http\_status|integer|否|HTTP 响应码|200、500、404|
|cost\_time|integer|否|接口耗时（ms）|230|
|exception\_class|keyword|否|异常类名（仅错误日志）|NullPointerException|
|stack\_trace|text|否|完整异常堆栈|异常堆栈文本|
|user\_id|keyword|否|登录用户ID|10001|
|req\_body|text|否|请求体（自动脱敏）|脱敏后报文|
|resp\_body|text|否|响应体（自动脱敏）|脱敏后报文|

## 5\. 前端浏览器专属字段（log\_source=browser）

所有前端上报日志统一字段，Fluentd 清洗后结构化存入 ES，支持分类检索、前端专项排查。

|字段名|类型|必填|说明|示例|
|---|---|---|---|---|
|log\_type|keyword|是|前端日志类型（固定枚举）|js\_error / api\_exception / promise\_error / blank\_screen / performance / user\_click|
|page\_url|text|是|报错/操作页面完整地址|http://localhost:8080/index|
|user\_agent|text|是|浏览器UA|Mozilla/5\.0 Chrome/120\.0\.0\.0|
|browser|keyword|是|浏览器名称（结构化提取）|Chrome / Edge / Safari|
|os|keyword|是|操作系统|Windows / MacOS / iOS|
|js\_stack|text|否|JS 异常堆栈|前端报错堆栈信息|
|api\_status|integer|否|前端接口响应码|403、500、0（跨域/断网）|
|api\_error\_type|keyword|否|接口异常类型|cors / timeout / network / server\_error|
|page\_load\_time|integer|否|页面总加载耗时 ms|1200|
|lcp\_time|integer|否|最大内容绘制（首屏性能）|800|
|click\_action|keyword|否|用户行为埋点|submit\_search / login\_click|

## 6\. 前端 log\_type 枚举规范（固定不可新增）

用于 Kibana 分类统计、告警、仪表盘展示，所有前端日志必须命中以下类型：

- **js\_error**：前端未捕获 JS 语法错误、运行时异常

- **promise\_error**：未捕获 Promise 异步异常

- **api\_exception**：前端 fetch/xhr 接口异常（4xx/5xx/跨域/超时/断网）

- **blank\_screen**：页面白屏检测日志

- **performance**：页面性能指标日志（采样上报）

- **user\_click**：用户交互行为埋点

## 7\. 日志脱敏规范（企业合规必做）

Fluentd 清洗阶段自动脱敏，禁止明文存储敏感字段：

- 手机号：138\*\*\*\*8888

- 身份证：110101\*\*\*\*\*\*\*\*1234

- 密码、token、cookie、密钥：统一替换为 \*\*\*\*\*\*

- 银行卡、地址、邮箱：脱敏展示

## 8\. 日志采样与降噪规范

- **ERROR/WARN 日志**：100% 全量采集

- **INFO 常规业务日志**：生产采样 30%，测试/开发全量

- **DEBUG 调试日志**：生产直接丢弃

- **前端性能日志**：全局采样 30%

- **心跳、健康检查、重复刷屏日志**：Fluentd 过滤丢弃

## 9\. 索引命名规范

- 服务端日志：`algoviz-logs-yyyy.MM.dd`

- 前端浏览器日志：`frontend-logs-yyyy.MM.dd`

物理索引隔离，逻辑可通过 trace\_id 关联查询。

## 10\. Kibana 标准检索语句（可直接落地）

```kusto
# 1. 只查服务端错误日志
log_source:server AND log_level:ERROR

# 2. 只查前端所有报错
log_source:browser AND log_level:ERROR

# 3. 前端JS错误专项
log_type:js_error

# 4. 前端接口5xx异常
log_type:api_exception AND api_status >=500

# 5. 全链路贯通：根据trace_id查前后端完整日志
trace_id: "xxx"

# 6. 白屏问题排查
log_type:blank_screen
```

## 11\. 落地约束（开发强制遵守）

1. 禁止私自新增自定义字段，所有字段必须在本规范内；

2. 所有服务必须输出 `trace_id`，前端必须透传 `trace_id`，保证全链路贯通；

3. 前端日志必须携带 `marker=BROWSER`，由后端统一输出、Fluentd 统一识别；

4. 敏感数据必须脱敏，禁止明文入库；

5. 异常日志必须携带堆栈、异常类名，方便统计 Top 报错；

6. 上线前必须验证：Kibana 可正常区分前后端日志、可按类型筛选。

## 12\. 方案适配说明（适配你当前项目）

本规范完全适配你当前最优方案：**前端postMessage \-\> 后端/api/logs \-\> Slf4j Marker\(BROWSER\) \-\> Fluentd清洗 \-\> ES \-\> Kibana**

无需 Kafka、无需改造现有 EFK 架构，仅需统一字段、规范上报，即可达到企业级日志治理标准。

> (Note: May contain AI-generated content.)
