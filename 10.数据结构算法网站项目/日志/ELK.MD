# SpringBoot + ELK 全链路日志系统开发文档

**EFK = Elasticsearch + Fluentd + Kibana**，是替代 ELK (Logstash) 的日志技术栈，去掉重量级 Logstash，改用轻量 Fluentd 采集清洗。

| 组件                          | 角色            | 端口         | 核心职责                                                     |
| ----------------------------- | --------------- | ------------ | ------------------------------------------------------------ |
| **Elasticsearch**             | 存储 & 检索引擎 | 9200         | 存储日志，倒排索引分词检索，支持向量存储；接收 Fluentd 推送的数据 |
| **Fluentd(td‑agent Windows)** | 采集 + 日志清洗 | 24220 (监控) | 读取日志文件、接收日志；filter 做清洗解析；输出推送到 ES     |
| **Kibana**                    | 可视化控制台    | 5601         | 索引模式配置、Discover 查看日志、搜索过滤、图表、告警、仪表盘 |

注意区分  

**Filebeat：纯采集 Agent，轻量，清洗能力弱** 

**Fluentd：采集 + 强大清洗 ETL，完整日志管道，就是你现在 Windows 用的 td‑agent** (filter清洗插件)

## 一、系统整体架构

### 1.1 架构总览

```
┌─────────────────────────────────────────────────────────────────┐
│                        SpringBoot 应用层                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌─────────────────┐ │
│  │ 业务模块 │  │日志埋点  │  │MDC链路ID │  │关键词过滤服务   │ │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────────┬────────┘ │
└───────┼─────────────┼─────────────┼───────────────────┼──────────┘
        │             │             │                   │
        └─────────────┴──────┬──────┴───────────────────┘
                             │ Logback TCP
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                        ELK 日志处理层                             │
│  ┌──────────┐    ┌──────────────┐    ┌──────────────────────┐  │
│  │ Logstash │───▶│ Elasticsearch│───▶│    Kibana 可视化     │  │
│  │ 日志采集  │    │ 存储+分词检索 │    │    看板+查询         │  │
│  └──────────┘    └──────┬───────┘    └──────────────────────┘  │
│                         │ IK分词器                               │
└─────────────────────────┼───────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                      业务功能层                                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │分词日志查询  │  │关键词屏蔽系统│  │定时分析+人工审核     │  │
│  └──────┬───────┘  └──────┬───────┘  └──────────┬───────────┘  │
└─────────┼─────────────────┼──────────────────────┼──────────────┘
          │                 │                      │
          ▼                 ▼                      ▼
┌─────────────────────────────────────────────────────────────────┐
│                        MySQL 数据层                               │
│  关键词库表 | 审核记录表 | 推送任务表 | 系统配置表                │
└─────────────────────────────────────────────────────────────────┘
```

```
┌────────────────────────────────────────────────────────────────────────────────────────────┐
│                                    Windows 开发机                                           │
│                                                                                            │
│  ┌──────────────┐        JSON over TCP        ┌────────────┐ 写入索引 ┌────────────────┐  │
│  │  Spring Boot  │◄──────────────────────────▶  Logstash   │─────────▶│ Elasticsearch   │  │
│  │  刷题网站      │    :5044                   │            │          │                │  │
│  └───┬──────┬───┘                             └─────┬──────┘          └───────┬────────┘  │
│      │      │                                         │                       │           │
│      │      │ DFA命中审计日志                          │死信(失败日志)         │定时任务分析高频可疑词
│      │      └─────────────────────────────────────────┘                       ▼           │
│      │                                                                 ┌──────────────┐  │
│      │ MySQL业务库                                                     │敏感词候选表 │  │
│      │                                                                 └───┬─────┬────┘  │
│      │                                                                     │     │置信度字段
│      ▼                                                                     │     │
│┌────────────────┐                                                          │     │
││用户黑名单表     │◀────多次违规触发                                         │人工审核│
│└────────────────┘                                                          ▼     │
│                                                                            │     │
│      ┌────────────────┐      ┌────────────────┐                      ┌──────────────┐ │
│      │ 敏感词正式库    │◄─────│ 审核操作日志表 │◄─────────────────────┤告警通知模块  │ │
│      └──────┬─────────┘      └────────────────┘                      └───────▲──────┘ │
│             │▲                                                                 │        │
│             ││人工审核通过写入                                                  │        │
│             ││                                                                 │        │
│             │└─────────────────────────────────────────────────────────────────┘        │
│             │热加载DFA词库                                                      │        │
│             ▼                                                                  │        │
│      ┌────────────────┐                                                        │        │
│      │ DFA实时过滤器   │◄── 用户请求前置拦截               ES大量违规 /高分候选词触发 │        │
│      └────────────────┘                                                               │        │
│                                                                          ┌─────▼─────┐│
│                                                                          │  Kibana    ││
│                                                                          │ 可视化查询 ││
│                                                                          └───────────┘│
└────────────────────────────────────────────────────────────────────────────────────────────┘

```

![image-20260816221302185](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260816221302185.png)

### 1.2 版本与组件

| 组件                        | 版本          | 作用               | 监听端口      |
| --------------------------- | ------------- | ------------------ | ------------- |
| Elasticsearch               | 7.12.1        | 日志存储与检索     | 9200          |
| IK Analyzer                 | 7.12.1        | 中文分词插件       |               |
| Kibana                      | 7.12.1        | 日志可视化与查询   | 5601          |
| Fluentd (td-agent)          | 1.14.3        | 日志采集与解析     | 24220（监控） |
| Fluentd(filter 插件)        |               | 数据清洗           |               |
| fluent-plugin-elasticsearch | 4.3.3 ~ 5.1.4 | Fluentd 输出到 ES  | -             |
| Spring Boot                 | 内置          | 业务后端（Java）   | 5000          |
| Python FastAPI              | 内置          | 向量服务（Python） | 8001          |

### 1.3 关键文件配置清单

| 用途                               | 路径                                                         |
| ---------------------------------- | ------------------------------------------------------------ |
| Fluentd 主配置                     | [fluentd.conf](file:///d:/daima/XiangMu/算法数据结构可视化/AlgoVize/Agent/know-retrieval/fluentd.conf) |
| Fluentd → ES 索引模板              | [algoviz-template.json](file:///d:/daima/XiangMu/算法数据结构可视化/AlgoVize/Agent/know-retrieval/templates/algoviz-template.json) |
| Spring Boot 日志格式（含 TraceId） | [logback-spring.xml](file:///d:/daima/XiangMu/算法数据结构可视化/AlgoVize/houduan/src/main/resources/logback-spring.xml) |
| Java TraceId 生成/透传 Filter      | [TraceIdFilter.java](file:///d:/daima/XiangMu/算法数据结构可视化/AlgoVize/houduan/src/main/java/com/algoviz/config/TraceIdFilter.java) |
| Java Feign 调用透传 TraceId        | [FeignTracingInterceptor.java](file:///d:/daima/XiangMu/算法数据结构可视化/AlgoVize/houduan/src/main/java/com/algoviz/config/FeignTracingInterceptor.java) |
| Python TraceId 中间件 + 日志       | [trace_middleware.py](file:///d:/daima/XiangMu/算法数据结构可视化/AlgoVize/Agent/know-retrieval/app/trace_middleware.py) |
| Python 验证脚本                    | [verify_tracing.py](file:///d:/daima/XiangMu/算法数据结构可视化/AlgoVize/Agent/know-retrieval/verify_tracing.py) |
| 日志统一存放目录                   | `D:/rizi`                                                    |

## 二、Windows 环境 ELK 部署

### 2.1 环境准备

**系统要求**：Windows 10/11 64 位，内存 ≥ 8GB，JDK 17+

**安装目录规划**：

```
D:\ELK\
├── elasticsearch-8.13.4\
├── logstash-8.13.4\
├── kibana-8.13.4\
└── ik-analyzer\
```

### 2.2 Elasticsearch 安装配置

#### 2.2.1 下载安装

1. 下载地址：[https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-8.13.4-windows-x86_64.zip](https://link.wtturl.cn/?target=https%3A%2F%2Fartifacts.elastic.co%2Fdownloads%2Felasticsearch%2Felasticsearch-8.13.4-windows-x86_64.zip&scene=im&aid=497858&lang=zh)
2. 解压到 `D:\ELK\elasticsearch-8.13.4`

#### 2.2.2 核心配置

编辑 `config/elasticsearch.yml`：

```
# 集群名称
cluster.name: dev-elk-cluster
# 节点名称
node.name: node-1
# 数据存储路径
path.data: D:\ELK\elasticsearch-8.13.4\data
path.logs: D:\ELK\elasticsearch-8.13.4\logs
# 网络绑定
network.host: 0.0.0.0
http.port: 9200
# 发现节点
discovery.type: single-node

# 开发环境关闭安全认证（生产环境建议开启）
xpack.security.enabled: false
xpack.security.enrollment.enabled: false
xpack.security.http.ssl.enabled: false
xpack.security.transport.ssl.enabled: false
```

编辑 `config/jvm.options`：

```
# 根据机器内存调整，建议不超过物理内存50%
-Xms2g
-Xmx2g
```

#### 2.2.3 启动验证

```
cd D:\ELK\elasticsearch-8.13.4\bin
elasticsearch.bat
```

访问 `http://localhost:9200`，返回 JSON 即启动成功。

<img src="https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260817155455.png" style="zoom: 80%;" />

2.24 查看索引  curl http://127.0.0.1:9200/_cat/indices?v

```
health status index                           uuid                   pri rep docs.count docs.deleted store.size pri.store.size
green  open   .kibana_7.12.1_001              ffON48q-R62scxHnl1HskQ   1   0         34           13      2.6mb          2.6mb
green  open   .apm-custom-link                npZo918cQBeK8fqxlyAatw   1   0          0            0       208b           208b
green  open   .apm-agent-configuration        iefK2Yc4TDWel-8wLlNOoA   1   0          0            0       208b           208b
green  open   .kibana_task_manager_7.12.1_001 H44ldf1tSoua2b9uAqIDrQ   1   0          9          676    121.2kb        121.2kb
green  open   .kibana-event-log-7.12.1-000001 GQkxgjIgRBit9tQwH5BD7A   1   0          1            0      5.6kb          5.6kb
```

| 字段           | 含义                                                         |
| -------------- | ------------------------------------------------------------ |
| health         | `green`：健康，主分片副本分片全部正常；yellow：副本分片未分配；red：主分片丢失，数据不可用 |
| status         | `open`索引正常打开；close 索引关闭不可读写                   |
| index          | 索引名，`.`开头 = 系统内部索引                               |
| pri            | 主分片数量                                                   |
| rep            | 副本分片数量，这里全部 rep=0，没有副本                       |
| docs.count     | 文档总数                                                     |
| docs.deleted   | 已删除待合并文档                                             |
| store.size     | 总占用磁盘（主 + 副本）                                      |
| pri.store.size | 仅主分片磁盘大小                                             |

1. `.kibana_7.12.1_001`：Kibana 核心存储，保存可视化、仪表盘、搜索、配置
2. `.kibana_task_manager_7.12.1_001`：kibana 后台定时任务
3. `.kibana-event-log-7.12.1-000001`：kibana 事件日志
4. `.apm-*`：APM 性能监控的内置索引，当前无数据

### 2.3 IK 分词器安装

1. 下载对应版本：https://release.infinilabs.com/analysis-ik/stable/elasticsearch-analysis-ik-7.12.1.zip 
2. 在 `elasticsearch/plugins` 目录下创建 `ik` 文件夹
3. 将解压后的所有文件放入 `ik` 目录
4. 重启 Elasticsearch

**验证分词器**：

```
curl -XGET http://127.0.0.1:9200/_analyze?pretty -H"Content-Type:application/json" -d"{\"analyzer\":\"ik_max_word\",\"text\":\"测试中文分词\"}"
```

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260817152809.png)

### 2.4 Logstash 安装配置

#### 2.4.1 下载安装

下载地址：[https://artifacts.elastic.co/downloads/logstash/logstash-8.13.4-windows-x86_64.zip](https://link.wtturl.cn/?target=https%3A%2F%2Fartifacts.elastic.co%2Fdownloads%2Flogstash%2Flogstash-8.13.4-windows-x86_64.zip&scene=im&aid=497858&lang=zh)

#### 2.4.2 管道配置

创建 `config/logback-springboot.conf`：

```
input {
  tcp {
    mode => "server"
    host => "0.0.0.0"
    port => 5044
    codec => json_lines
  }
}

filter {
  # 解析JSON日志
  json {
    source => "message"
  }
  
  # 添加时间戳
  date {
    match => ["timestamp", "yyyy-MM-dd HH:mm:ss.SSS"]
    target => "@timestamp"
  }
  
  # 提取traceId用于链路追踪
  mutate {
    add_field => { 
      "trace_id" => "%{traceId}"
      "app_name" => "%{appName}"
    }
  }
}

output {
  elasticsearch {
    hosts => ["http://localhost:9200"]
    index => "springboot-logs-%{+YYYY.MM.dd}"
  }
  
  # 控制台输出（调试用）
  stdout {
    codec => rubydebug
  }
}
```

#### 2.4.3 启动 Logstash

```
cd D:\ELK\logstash-8.13.4\bin
logstash.bat -f ../config/logback-springboot.conf
```

### 2.5**Logstash** 替换为Fluentd

d‑agent 本体不需要和 ES 版本对齐，插件`fluent‑plugin‑elasticsearch=4.3.3`才要匹配 ES7.12.1

**[下载]( https://s3.amazonaws.com/packages.treasuredata.com/4/windows/td-agent-4.3.0-x64.msi)**

**安装** 执行msi文件  在安装bin目录把该目录加入到path中

[离线下载](td-agent-gem install --local fluent-plugin-elasticsearch-4.3.3.gem)  [安装命令](td-agent-gem install --local fluent-plugin-elasticsearch-4.3.3.gem) 

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260817154617.png)

查看插件  td-agent-gem list

查看插件版本 td-agent-gem list fluent-plugin-elasticsearch

```
D:\downlowad\Google>

*** LOCAL GEMS ***

fluent-plugin-elasticsearch (5.1.4, 4.3.3)

D:\downlowad\Google>
```

配置文件 `fluent.conf` 核心内容：

ruby

```
# 输入源：监听 TCP 端口，接收 JSON lines 格式日志
<source>
  @type tcp
  tag app.logs
  port 24224
  <parse>
    @type json
  </parse>
</source>

# 可选：添加额外字段（例如环境标识）
<filter app.logs>
  @type record_transformer
  <record>
    env dev
  </record>
</filter>

# 输出到 Elasticsearch
<match app.logs>
  @type elasticsearch
  host 127.0.0.1
  port 9200
  index_name app-logs-%Y.%m.%d
  include_timestamp true
  # 使用日志中的 @timestamp 作为时间
  time_key @timestamp
  time_key_format %Y-%m-%dT%H:%M:%S.%L%z
  # 若日志中已有 @timestamp 字段，删除避免冲突
  remove_keys @timestamp
</match>
```

**关键点**：

- Logback 的 `LogstashEncoder` 会输出 `@timestamp` 字段（ISO8601 格式），Fluentd 默认会添加自己的 `time` 字段，为避免冲突，使用 `time_key @timestamp` 让 Fluentd 识别该字段作为事件时间。
- `remove_keys @timestamp` 可防止重复写入。

如果不需要精确时间映射，可以简化为：

ruby

```
<match app.logs>
  @type elasticsearch
  host 127.0.0.1
  port 9200
  index_name app-logs-%Y.%m.%d
</match>
```

**启动** Fluentd\td-agent\etc\td-agent目录下执行 

```
# 切换到程序bin目录
cd C:\opt\td-agent\bin

# 前台启动，读取配置文件，窗口不能关闭，关闭服务就停止
td-agent -c C:\opt\td-agent\etc\td-agent.conf
```

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260817162735.png)

fluentd -c fluentd.conf

配置文件



### 2.6 Kibana 安装配置

#### 2.6.1 下载安装

[下载地址](https://mirrors.huaweicloud.com/kibana/7.12.1/kibana-7.12.1-windows-x86_64.zip)

#### 2.6.2 配置

编辑 `config/kibana.yml`：

```
server.port: 5601
server.host: "0.0.0.0"
elasticsearch.hosts: ["http://localhost:9200"]
i18n.locale: "zh-CN"
```

#### 2.6.3 启动

```
cd D:\ELK\kibana-8.13.4\bin
kibana.bat
```

访问 `http://localhost:5601` 进入控制台。

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260817155708.png)



在 Kibana 中：

1. 进入 Management → Stack Management → Index Patterns
2. 创建索引模式： algoviz-logs-*
3. 时间字段选择： @timestamp
4. 进入 Discover 页面，即可查看所有日志
5. 通过搜索 trace_id: "xxx" 查看完整链路

```
# 1. 查看某个 TraceId 的完整链路
trace_id: "a1b2c3d4e5f6g7h8"

# 2. 只看 Java 后端的错误日志
service: "algoviz-backend" AND level: "ERROR"

# 3. 只看 Python 向量服务的搜索日志
service: "algoviz-vector" AND message: "语义搜索"

# 4. 按 TraceId 分组查看
trace_id: * | group by trace_id

# 5. 最近 15 分钟的所有错误
level: "ERROR" AND @timestamp > now-15m
```

创建索引

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260817173427.png)

查询

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260817173313.png)

## 三、SpringBoot 集成 ELK + 全链路追踪

### 3.1架构总览

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

### 3.2 开始配置

####  步骤一Java（Spring Boot）侧 —— 生成 TraceId + 统一日志格式

**目标**：让 Spring Boot 每条日志带上 `TraceId`，并输出到 `D:/rizi` 目录，按 10MB 滚动，保留 30 天。

**3.1.1 `TraceIdFilter.java` —— 请求入口生成/提取 TraceId**

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

**3.1.2 `FeignTracingInterceptor.java` —— 跨服务调用透传 TraceId**

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

**3.1.3 `logback-spring.xml` —— 日志格式 + 滚动策略**

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

#### 步骤二：Python（FastAPI）侧 —— 提取 TraceId + 统一日志格式

**目标**：Python 向量服务从 Header 提取 Java 传来的 TraceId，日志格式与 Java 对齐，也写到 `D:/rizi`。

**3.2.1 `trace_middleware.py` —— FastAPI 中间件 + 日志配置**

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

####  步骤三：Fluentd 配置 —— 采集、解析、写 ES

**目标**：Fluentd 同时 tail Java 和 Python 两个日志文件，用正则拆字段，加元数据（service/language/component），批量写 ES。

**3.3.1 Source 配置（核心）**

| Source            | 采集文件                                            | 正则格式重点                                           |
| ----------------- | --------------------------------------------------- | ------------------------------------------------------ |
| `algoviz.backend` | `D:/rizi/info.log,error.log,info-*.log,error-*.log` | 带 **毫秒** 的时间 `HH:mm:ss.SSS` + 多了 `[线程]` 字段 |
| `algoviz.vector`  | `D:/rizi/python-app.log`                            | **无毫秒** 的时间 `HH:mm:ss` + 无线程字段              |

**Java 解析正则**（[fluentd.conf#L70](file:///d:/daima/XiangMu/算法数据结构可视化/AlgoVize/Agent/know-retrieval/fluentd.conf#L70)）：

```
format1 /^(?<logtime>\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}\.\d{3}) \[(?<trace_id>[^\]]+)\] \[(?<thread>[^\]]+)\] (?<level>[A-Z]+)\s+(?<logger>[^\s]+) - (?<message>.*)$/
```

**Python 解析正则**（[fluentd.conf#L105](file:///d:/daima/XiangMu/算法数据结构可视化/AlgoVize/Agent/know-retrieval/fluentd.conf#L105)）：

```
format1 /^(?<logtime>\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}) \[(?<trace_id>[^\]]+)\] \[(?<level>[A-Z]+)\] (?<logger>[^\s]+) - (?<message>.*)$/
```

> **重要坑点**：TraceId 正则使用 `[^\]]+`（任意非 `]` 字符），不要写成严格的 `[a-f0-9]{16}`。否则外部传入的 `test-trace-xxx`、UUID 等格式全部解析失败，直接丢弃。

**3.3.2 不要设置 `encoding UTF-8`**

Fluentd 的 tail 插件在读取 UTF-8 文件时**不能设置 `encoding UTF-8`**，否则会触发二次转码，中文全部变成 `\uFFFD`（替换字符）。直接删除 `encoding` 行即可。

**3.3.3 多行堆栈处理**

```
<parse>
  @type multiline
  format_firstline /^\d{4}-\d{2}-\d{2}/   # 日期开头 = 新日志的开始
  format1 /...真正的正则.../               # 匹配第一行
  # 后续不以日期开头的行（堆栈）自动合并到上一条
</parse>
```

这样 Java 的 `NullPointerException` 大堆栈、Python 的 `Traceback (most recent call last):` 都会被识别成一条日志，不会被拆成几十条垃圾。

**3.3.4 Filter：补字段 + 转 @timestamp**

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

**3.3.5 Output：写入 Elasticsearch**

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

#### 步骤四：Elasticsearch 索引模板

[algoviz-template.json](file:///d:/daima/XiangMu/算法数据结构可视化/AlgoVize/Agent/know-retrieval/templates/algoviz-template.json) 定义字段类型，关键几列：

| 字段                                 | ES 类型                 | 原因                                      |
| ------------------------------------ | ----------------------- | ----------------------------------------- |
| `@timestamp`                         | `date`                  | Kibana 时间轴依赖                         |
| `logtime`                            | `date`                  | 格式兼容毫秒/无毫秒两种                   |
| `trace_id`                           | `keyword`               | **精确匹配**聚合查询用 keyword，不是 text |
| `level`                              | `keyword`               | 按级别筛选用                              |
| `service` / `language` / `component` | `keyword`               | 按服务筛选                                |
| `message`                            | `text`                  | 全文搜索，用 standard 分词器              |
| `logger`                             | `text + keyword` 多字段 | 既可全文搜 logger 名，也可精确聚合        |

**模板会在 Fluentd 启动时自动推送到 ES**（`template_overwrite true` 每次覆盖）。手动推送也可以：

```bash
curl -XPUT "http://localhost:9200/_template/algoviz-logs" \
  -H "Content-Type: application/json" \
  -d @templates/algoviz-template.json
```

#### 步骤五：启动顺序

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

### 3.3完整链路展示

#### **流程全景**

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
- 最终两条来源**同一个 trace_id** 的日志落在同一个 ES 索引里→kibana展示

#### 本地日志 vs Kibana 日志对比表

| 维度           | 本地磁盘日志 `D:/rizi/*.log`     | Kibana / Elasticsearch                                       |
| -------------- | -------------------------------- | ------------------------------------------------------------ |
| **存储介质**   | 本地文本文件（滚动分割）         | 分布式索引 `algoviz-logs-YYYY.MM.DD`                         |
| **保留策略**   | logback maxHistory=30 天，自动删 | 可手动/ILM 生命周期管理（见第五节）                          |
| **跨服务关联** | 两个文件分开，无法自动关联       | 通过 `trace_id=abc123...` 一条 DSL 全拉出来                  |
| **查询能力**   | grep/Notepad++ 正则搜索          | **倒排索引**毫秒级全文搜索 + 字段过滤 + 聚合                 |
| **多条件组合** | 需写复杂 sed/awk 脚本            | 可视化点选：service=backend AND level=ERROR AND 时间范围     |
| **时间范围**   | 按文件名找日志文件               | Kibana 时间轴拉条，任意粒度                                  |
| **堆栈聚合**   | 肉眼翻多行                       | multiline 合并成一条记录，message 字段完整保留堆栈           |
| **可视化**     | 无                               | 折线图看错误数、饼图看服务分布、TraceId 做调用链路 Dashboard |
| **丢失风险**   | 磁盘坏了就丢                     | ES 副本 + Fluentd 磁盘缓冲双保险                             |
| **检索耗时**   | 查 1 个月日志 → 几分钟           | 1 亿条日志 → 毫秒级                                          |
| **适合场景**   | 实时开发调试、快速 tail -f       | 生产排障、全链路分析、长期审计、趋势报表                     |

#### Kibana 查询示例

假设一次请求的 TraceId 是 `abc123def4567890`：

**① 基础查询：拉取同一条链路所有日志**

在 Kibana → Discover → KQL 栏输入：

```
trace_id: "abc123def4567890"
```

按 `@timestamp` 升序，应该能看到类似：

| 时间         | service         | level | message                                    |
| ------------ | --------------- | ----- | ------------------------------------------ |
| 17:09:17.101 | algoviz-backend | INFO  | 收到语义搜索请求，query=多线程             |
| 17:09:17.105 | algoviz-backend | DEBUG | Feign 调用向量服务 embedding/search        |
| 17:09:17.110 | algoviz-vector  | INFO  | 请求入口 method=POST path=/api/v1/search   |
| 17:09:17.230 | algoviz-vector  | INFO  | 向量检索完成，命中 5 条，最高相似度 0.7544 |
| 17:09:17.240 | algoviz-backend | INFO  | Feign 返回，5 道题，耗时 135ms             |
| 17:09:17.252 | algoviz-backend | INFO  | MySQL 查询题目详情，耗时 12ms              |
| 17:09:17.258 | algoviz-backend | INFO  | 返回语义搜索结果，共 5 道题                |

**完整一条链**，按时间顺序从 Java 进、到 Python 回、再到 Java 出，一目了然。

**② 异常排查：筛某服务错误**

```
service: "algoviz-backend" AND level: "ERROR"
```

**③ 慢请求分析：按 TraceId 聚合响应时间**

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

### 3.4 问题解决

####  `pattern not matched: "..."` 警告

**现象**：Fluentd 启动后不断刷 `pattern not matched`。

**原因**：

- Java/Python 格式的正则写反了（比如用了 `,\d{3}` 毫秒去匹配无毫秒日志）
- TraceId 正则过严（`[a-f0-9]{16}` 匹配不上 `test-trace-xxx`）

**解决**：用 [algoviz-template.json 中 format1](#33-source-配置核心) 宽松版，TraceId 用 `[^\]]+`。

####  中文全变成 `\uFFFD`

**现象**：日志 message 字段出现大量 `\uFFFD`（Unicode 替换字符）。

**原因**：fluentd.conf 里 `encoding UTF-8` 让 Fluentd 对 UTF-8 文件二次转码。

**解决**：**删除所有 source 里的 `encoding UTF-8`**。tail 插件默认就是字节原样传输。

#### If you specify a template_name you must specify a valid template file

**现象**：Fluentd 刷 buffer 时 RuntimeError。

**原因**：`template_file C:/fluentd/templates/algoviz-template.json` 路径不存在。模板在项目目录里，不在 Fluentd 安装目录。

**解决**：改成项目实际绝对路径：

```
template_file d:/daima/XiangMu/算法数据结构可视化/AlgoVize/Agent/know-retrieval/templates/algoviz-template.json
```

如果路径里有中文导致 Ruby 编码问题，**备选方案**：把模板文件复制到纯英文路径（如 `D:/fluentd-templates/algoviz-template.json`）再配置。

#### Fluentd 采集了旧归档（`info-1.log` ~ `info-21.log`）

**现象**：启动时 tail 了一堆 `info-1.log` 等旧文件。

**原因**：这些是前版 logback（FixedWindowRollingPolicy，1MB 分割）遗留的归档文件，被 `info-*.log` 通配符匹配。

**解决**：

```powershell
# 在 D:/rizi 目录下清理历史归档（只保留 info.log / info-YYYY-MM-DD.N.log 格式）
Remove-Item D:/rizi/info-[0-9]*.log, D:/rizi/error-[0-9]*.log
# Fluentd 重启前顺带清 pos 记录
Remove-Item C:/fluentd/pos/*.pos -Force
```

#### 日志进了 ES 但字段没按模板（trace_id 是 text 不是 keyword）

**原因**：索引创建时模板还没推送到 ES，Fluentd 动态映射把 trace_id 当 text。

**解决**：

```bash
# 1. 手动推模板
curl -XPUT "http://localhost:9200/_template/algoviz-logs" ...（见 5.1.2）

# 2. 删除当天旧索引（它用的是动态映射，重建才能用模板）
curl -XDELETE "http://localhost:9200/algoviz-logs-$(Get-Date -Format 'yyyy.MM.dd')"

# 3. 重启 Fluentd，新索引用模板创建
```

#### TraceId 不同步（Java 和 Python 不是同一个）

**排查顺序**：

1. 确认 `FeignTracingInterceptor` 已注册成 `@Component` 被 Spring 扫描
2. 抓包看 Feign 发出去的 Header 里是否带 `X-Trace-Id`
3. Python 中间件取 `request.headers.get("X-Trace-Id")`，如果是 Feign 默认行为**Header 名会转小写** → FastAPI 默认忽略大小写，一般没问题；自定义的库要注意。
4. 日志格式里 `trace_id` 确实是 `[xxx]` 且没有被格式化错误截断

####  Fluentd 丢了 Elasticsearch 连接但没恢复

看 buffer 目录 `C:/fluentd/buffer/algoviz` 有没有 `.q` 文件堆积：

- 有 → buffer 堆积，检查 ES 是否挂、网络是否通
- 无 → 检查 tail 是否还在走（`read_from_head true` 会每次从头读）

### 3.9后续扩展

企业真正价值在于：**标准化、不丢日志、主动告警、运维、安全、全链路追踪、安全合规、成本可控**

| 企业级 ELK                               | 维度     | Demo 简单 ELK        |
| ---------------------------------------- | -------- | -------------------- |
| Filebeat → Kafka → Logstash/Fluentd → ES | 数据流   | Filebeat → ES        |
| Kafka 削峰，磁盘缓冲，防止丢日志         | 可靠性   | 无队列，直接写 ES    |
| 强制统一字段规范，traceId 全链路透传     | 字段     | 日志字段随意         |
| ILM 生命周期，冷热分离，归档             | 存储     | 索引无限增长         |
| 完善告警、降噪、分级通知                 | 告警     | 基本不用             |
| RBAC 权限、脱敏、传输加密、审计          | 安全     | 无权限、明文敏感数据 |
| 接入浏览器日志，traceId 打通前后端       | 前端日志 | 基本不做             |
| 同时监控 ELK 整套组件健康状态            | 观测     | 只看业务日志         |

#### **5.1 手动创建 / 管理 ES 索引**

Fluentd 用 logstash_format 会**自动按天创建索引**，但遇到以下场景需要手动操作：

5.1.1 手动创建索引

```bash
# 创建一个一次性测试索引
curl -XPUT "http://localhost:9200/algoviz-logs-test-001" -H "Content-Type: application/json" -d '{
  "settings": {
    "index.number_of_shards": 1,
    "index.number_of_replicas": 0
  }
}'
```

**5.1.2 手动推送索引模板（Fluentd 启动也会推，但手动推方便测试）**

```bash
curl -XPUT "http://localhost:9200/_template/algoviz-logs" \
  -H "Content-Type: application/json" \
  -d @d:/daima/XiangMu/算法数据结构可视化/AlgoVize/Agent/know-retrieval/templates/algoviz-template.json
```

查看已推送的模板：

```bash
curl "http://localhost:9200/_template/algoviz-logs?pretty"
```

**5.1.3 查看当前索引列表 + 状态**

```bash
curl "http://localhost:9200/_cat/indices/algoviz-logs-*?v&s=index"
```

**5.1.4 删除过期索引（比如 2026-08-15 的历史）**

```bash
curl -XDELETE "http://localhost:9200/algoviz-logs-2026.08.15"
```

#### **5.2 接入 Index Lifecycle Management (ILM) —— 日志自动老化**

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

#### 5.3 采集更多服务

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

#### 5.4 前端浏览器日志接入（可选）

技术链路里前端浏览器的日志目前没采集。要做可以：

- 前端用 `postMessage` 把 `console.error` / 用户交互埋点打给后端 `/api/logs` 接口
- 后端用 `@Slf4j` 打 `BROWSER` 级别日志（或用自定义 Marker）
- Fluentd 侧 `level=BROWSER` 自动采集
- 同一个 TraceId 链条延伸到浏览器端

前端 SDK → kafka → Logstash/Fluentd 消费 kafka → ES（高并发大流量）

两种方案

#### 5.5 慢查询监控 Dashboard 建议

在 Kibana → Dashboard 新建以下 Panel：

| Panel 名称              | 类型       | 指标 / 过滤                                |
| ----------------------- | ---------- | ------------------------------------------ |
| 每分钟请求量            | 折线       | `service=*` 按时间 date_histogram count    |
| 错误率（按服务）        | 折线       | `level=ERROR` / `*` 分组 by service        |
| TraceId 调用热度 Top10  | 条形       | terms agg on trace_id, order by count desc |
| Python 向量检索耗时分布 | 直方图     | `message` 包含"向量检索完成"的时间差       |
| 日志量（按语言）        | 饼图       | terms agg by language                      |
| 超时请求列表            | Data Table | `message` 包含"timeout"或"exceeded"的过滤  |

#### 5.6 告警接入（ElastAlert / Watcher）

典型规则：

- **ERROR 突增告警**：5 分钟内 ERROR 日志数量 > 50 → 企业微信/邮件告警
- **TraceId 断链**：Java 侧有 "调用向量服务"，但 30 秒内没出现 Python 侧的对应 TraceId → 告警
- **慢请求**：单次请求总耗时（Java 首末时间差）> 3s → 告警

1. Kibana Alerting / Watcher 告警
   - 错误日志突增告警；5xx 错误率超过阈值告警；特定异常关键字告警。
   - 支持：钉钉、企业微信、邮件、短信。
2. 告警降噪、合并告警 短时间大量相同报错只触发 1 次告警，避免告警风暴轰炸运维。
3. 告警分级：P0/P1/P2，不同级别通知不同人员。

#### 多种日志采集

不止 Java/Python 应用日志：

- Nginx 网关访问日志
- MySQL 慢查询日志
- Redis、MQ 中间件日志
- 容器 Docker/K8s 容器 stdout 容器日志
- 浏览器前端日志（就是你前面讨论的方案）
- Windows/Linux 系统日志

#### 数据脱敏 多租户权限隔离

1. Kibana 多租户权限隔离

   - 不同开发组只能看自己服务的索引；开发环境不能查看生产日志。
   - 基于角色控制：只能查询，禁止修改删除 ES 数据。

2. 日志数据脱敏 Fluentd/Logstash 清洗阶段自动脱敏：手机号、身份证、银行卡、token、密码。

   > 合规要求，日志不能明文存储敏感信息。

3. 传输加密 Filebeat→Kafka、Kafka→Logstash/Fluentd、Agent 到 ES 全部 TLS 加密。

4. 审计日志 记录谁在 Kibana 查询了哪些日志，满足等保审计要求。

#### 全链路打通

1. 全链路 TraceId 透传
   - Gateway 网关 → Java 后端 → MQ → 数据库 → **浏览器前端日志**全部携带同一个 traceId。
   - ELK 中输入 traceId，一次性拿到网关日志、服务日志、MQ 日志、浏览器报错日志。
2. 和 APM（SkyWalking / Pinpoint）联动 ELK 侧重原始日志；APM 侧重调用链耗时。 Kibana 通过 traceId 跳转到 APM 链路；APM 链路里面可以跳转查看 ELK 原始日志。

#### 日志统一处理

1. **统一日志字段规范** 强制统一字段：`@timestamp、trace_id、span_id、service_name、env、hostname、ip、log_level、log_type`。 所有服务、前端、中间件日志输出统一字段，不能各服务自定义一套字段。
2. 日志分类打标
   - 服务端业务日志、异常日志、访问日志、网关日志、数据库慢日志、浏览器前端日志，通过标签区分。
   - 环境标签：dev/test/prod，Kibana 按环境隔离查看。
3. 日志采样 & 降噪
   - 正常 INFO/DEBUG 日志采样（例如 10%），ERROR 异常 100% 全量采集，控制存储成本。
   - 自动过滤心跳、健康检查、重复刷屏噪音日志。
4. **日志大小截断** 超长堆栈、大报文自动截断，防止单条日志过大导致 ES 写入失败。
5. **索引生命周期管理 ILM** ES 索引自动轮转：按天生成索引；热阶段保存 7 天，温阶段压缩，冷阶段归档，到期自动删除。 不会无限膨胀磁盘，开发环境经常忽略，企业必做。

#### 日志可靠性

1. 多级缓冲、断点续传

   - Filebeat：内存缓冲 + 磁盘队列，服务挂掉日志落本地磁盘，恢复后继续发送，不丢日志。
   - Logstash/Fluentd：磁盘 buffer，ES 不可用时消息落盘，避免内存溢出丢失。

2. 消息队列削峰（Kafka）

   业务突增、大量报错风暴，日志先入 Kafka，消费端限速消费，

   日志流量和业务解耦

   ，不会把 ES 打崩。

   > 生产几乎不会直接 Filebeat → ES，中间加 Kafka 做缓冲是企业标准。

3. **至少一次投递保障、重复数据去重** 网络抖动会产生重复日志，通过`traceId+logId`做去重，ES 写入时过滤重复文档。

4. **优雅关闭、刷盘机制** Agent 进程重启前把缓冲区全部刷完，避免重启瞬间丢日志。

## 四、分词查询功能实现

### 4.1 ES 索引模板配置

在 Kibana Dev Tools 中执行：

```
PUT _template/springboot_logs_template
{
  "index_patterns": ["springboot-logs-*"],
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 0,
    "index.refresh_interval": "5s"
  },
  "mappings": {
    "properties": {
      "@timestamp": { "type": "date" },
      "level": { "type": "keyword" },
      "loggerName": { "type": "keyword" },
      "threadName": { "type": "keyword" },
      "appName": { "type": "keyword" },
      "traceId": { "type": "keyword" },
      "message": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart"
      },
      "stack_trace": {
        "type": "text",
        "analyzer": "ik_max_word"
      }
    }
  }
}
```

### 4.2 日志查询服务

#### 4.2.1 查询 DTO

```
@Data
public class LogQueryDTO {
    private String keyword;           // 搜索关键词
    private String level;             // 日志级别
    private String traceId;           // 链路ID
    private String appName;           // 应用名
    private String startTime;         // 开始时间
    private String endTime;           // 结束时间
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}
```

#### 4.2.2 查询服务实现

```
@Service
@RequiredArgsConstructor
public class LogSearchService {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    /**
     * 分词查询日志
     */
    public Page<LogDocument> searchLogs(LogQueryDTO queryDTO) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        
        // 构建布尔查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        
        // 关键词分词查询（message字段使用IK分词）
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery("message", queryDTO.getKeyword())
                    .analyzer("ik_max_word"));
        }
        
        // 精确匹配条件
        if (StringUtils.hasText(queryDTO.getLevel())) {
            boolQuery.filter(QueryBuilders.termQuery("level", queryDTO.getLevel()));
        }
        if (StringUtils.hasText(queryDTO.getTraceId())) {
            boolQuery.filter(QueryBuilders.termQuery("traceId", queryDTO.getTraceId()));
        }
        if (StringUtils.hasText(queryDTO.getAppName())) {
            boolQuery.filter(QueryBuilders.termQuery("appName", queryDTO.getAppName()));
        }
        
        // 时间范围
        if (StringUtils.hasText(queryDTO.getStartTime()) && StringUtils.hasText(queryDTO.getEndTime())) {
            boolQuery.filter(QueryBuilders.rangeQuery("@timestamp")
                    .gte(queryDTO.getStartTime())
                    .lte(queryDTO.getEndTime()));
        }
        
        queryBuilder.withQuery(boolQuery);
        
        // 按时间倒序
        queryBuilder.withSort(SortBuilders.fieldSort("@timestamp").order(SortOrder.DESC));
        
        // 分页
        Pageable pageable = PageRequest.of(queryDTO.getPageNum() - 1, queryDTO.getPageSize());
        queryBuilder.withPageable(pageable);
        
        // 高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .field("message")
                .preTags("<span style='color:red'>")
                .postTags("</span>")
                .fragmentSize(200);
        queryBuilder.withHighlightBuilder(highlightBuilder);
        
        // 指定索引（按日期范围优化）
        String indexPattern = "springboot-logs-*";
        queryBuilder.withIndices(indexPattern);
        
        SearchHits<LogDocument> searchHits = elasticsearchTemplate.search(
                queryBuilder.build(), LogDocument.class);
        
        // 处理高亮结果
        List<LogDocument> list = searchHits.getSearchHits().stream()
                .map(hit -> {
                    LogDocument doc = hit.getContent();
                    if (hit.getHighlightFields().containsKey("message")) {
                        doc.setMessage(String.join("...", hit.getHighlightFields().get("message")));
                    }
                    return doc;
                })
                .collect(Collectors.toList());
        
        return new PageImpl<>(list, pageable, searchHits.getTotalHits());
    }

    /**
     * 根据traceId查询全链路日志
     */
    public List<LogDocument> getTraceLogs(String traceId) {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("traceId", traceId))
                .withSort(SortBuilders.fieldSort("@timestamp").order(SortOrder.ASC))
                .withPageable(PageRequest.of(0, 500))
                .build();
        
        SearchHits<LogDocument> searchHits = elasticsearchTemplate.search(query, LogDocument.class);
        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
```

#### 4.2.3 日志文档实体

```
@Data
@Document(indexName = "springboot-logs-*")
public class LogDocument {
    @Id
    private String id;
    
    @Field(name = "@timestamp", type = FieldType.Date)
    private String timestamp;
    
    @Field(type = FieldType.Keyword)
    private String level;
    
    @Field(type = FieldType.Keyword)
    private String loggerName;
    
    @Field(type = FieldType.Keyword)
    private String threadName;
    
    @Field(type = FieldType.Keyword)
    private String appName;
    
    @Field(type = FieldType.Keyword)
    private String traceId;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String message;
    
    @Field(type = FieldType.Text)
    private String stackTrace;
}
```

------

## 五、关键词屏蔽系统设计

**业务流程**

```
用户搜索/提交内容
        │
        ▼
产生日志 → Logstash → Elasticsearch
        │
        ▼
定时任务分析 ES 高频搜索词
        │
        ▼
过滤已存在敏感词、停用词、频率阈值
        │
        ▼
写入敏感词候选表（status=0 待审核）
        │
        ▼
管理员人工审核
        │
        ├──通过 → 写入敏感词正式库 → 刷新 DFA 过滤器
        │
        └──驳回 → 仅更新候选表状态
```



### 5.1 系统架构

```
日志输入 → 关键词检测引擎 → 命中检测 → 分级处理
                    ↑
           ┌────────┴────────┐
           │                 │
      本地词库缓存       远程词库加载
           ↑                 ↑
        Redis             MySQL
           ↑
      定时同步任务
```

### 5.2 核心算法：DFA 敏感词树

```
package com.example.elk.sensitive;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class SensitiveWordFilter {

    private final Map<String, Object> sensitiveWordMap = new HashMap<>();
    private static final String END_FLAG = "isEnd";

    /**
     * 初始化敏感词库
     */
    public void initWordTree(Set<String> words) {
        sensitiveWordMap.clear();
        for (String word : words) {
            addWord(word);
        }
    }

    /**
     * 添加单个敏感词到DFA树
     */
    @SuppressWarnings("unchecked")
    private void addWord(String word) {
        Map<String, Object> currentMap = sensitiveWordMap;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            String key = String.valueOf(c);
            
            Map<String, Object> nextMap = (Map<String, Object>) currentMap.get(key);
            if (nextMap == null) {
                nextMap = new HashMap<>();
                currentMap.put(key, nextMap);
            }
            currentMap = nextMap;
            
            if (i == word.length() - 1) {
                currentMap.put(END_FLAG, true);
            }
        }
    }

    /**
     * 检测文本中的敏感词
     * @return 命中的敏感词列表
     */
    @SuppressWarnings("unchecked")
    public List<SensitiveMatchResult> checkSensitiveWords(String text) {
        List<SensitiveMatchResult> results = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return results;
        }

        for (int i = 0; i < text.length(); i++) {
            Map<String, Object> currentMap = sensitiveWordMap;
            StringBuilder matchWord = new StringBuilder();
            int matchIndex = i;
            boolean found = false;

            while (matchIndex < text.length()) {
                String key = String.valueOf(text.charAt(matchIndex));
                Map<String, Object> nextMap = (Map<String, Object>) currentMap.get(key);
                
                if (nextMap == null) {
                    break;
                }
                
                matchWord.append(key);
                currentMap = nextMap;
                
                if (Boolean.TRUE.equals(currentMap.get(END_FLAG))) {
                    found = true;
                    break;
                }
                matchIndex++;
            }

            if (found) {
                results.add(new SensitiveMatchResult(matchWord.toString(), i, matchIndex));
                i = matchIndex; // 跳过已匹配部分
            }
        }
        return results;
    }

    /**
     * 替换敏感词为指定字符
     */
    public String replaceSensitiveWords(String text, String replaceChar) {
        List<SensitiveMatchResult> matches = checkSensitiveWords(text);
        if (matches.isEmpty()) {
            return text;
        }

        StringBuilder sb = new StringBuilder(text);
        // 从后往前替换，避免索引偏移
        Collections.reverse(matches);
        for (SensitiveMatchResult match : matches) {
            String replacement = replaceChar.repeat(match.getWord().length());
            sb.replace(match.getStartIndex(), match.getEndIndex() + 1, replacement);
        }
        return sb.toString();
    }

    @Data
    @AllArgsConstructor
    public static class SensitiveMatchResult {
        private String word;
        private int startIndex;
        private int endIndex;
    }
}
```

### 5.3 关键词服务层

```
@Service
@RequiredArgsConstructor
public class SensitiveWordService {

    private final SensitiveWordMapper sensitiveWordMapper;
    private final SensitiveWordFilter sensitiveWordFilter;
    private final StringRedisTemplate redisTemplate;
    
    private static final String SENSITIVE_WORDS_CACHE_KEY = "sensitive:words:active";
    private static final String SENSITIVE_WORDS_LOCK_KEY = "sensitive:words:refresh:lock";

    /**
     * 加载敏感词到内存
     */
    @PostConstruct
    public void loadSensitiveWords() {
        refreshWordCache();
    }

    /**
     * 刷新敏感词缓存
     */
    public void refreshWordCache() {
        // 分布式锁防止并发刷新
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(SENSITIVE_WORDS_LOCK_KEY, "1", 30, TimeUnit.SECONDS);
        
        if (Boolean.TRUE.equals(locked)) {
            try {
                // 从数据库查询启用的敏感词
                Set<String> words = sensitiveWordMapper.selectActiveWords().stream()
                        .map(SensitiveWord::getWord)
                        .collect(Collectors.toSet());
                
                // 初始化DFA树
                sensitiveWordFilter.initWordTree(words);
                
                // 缓存到Redis
                redisTemplate.opsForSet().add(SENSITIVE_WORDS_CACHE_KEY, words.toArray(new String[0]));
                
                log.info("敏感词库刷新完成, 共加载 {} 个敏感词", words.size());
            } finally {
                redisTemplate.delete(SENSITIVE_WORDS_LOCK_KEY);
            }
        }
    }

    /**
     * 检测日志内容并过滤敏感词
     */
    public LogFilterResult filterLogMessage(String message) {
        List<SensitiveWordFilter.SensitiveMatchResult> matches = 
                sensitiveWordFilter.checkSensitiveWords(message);
        
        LogFilterResult result = new LogFilterResult();
        result.setOriginalMessage(message);
        result.setHitWords(matches.stream()
                .map(SensitiveWordFilter.SensitiveMatchResult::getWord)
                .collect(Collectors.toList()));
        result.setSensitive(!matches.isEmpty());
        result.setFilteredMessage(sensitiveWordFilter.replaceSensitiveWords(message, "*"));
        
        return result;
    }

    @Data
    public static class LogFilterResult {
        private String originalMessage;
        private String filteredMessage;
        private boolean sensitive;
        private List<String> hitWords;
    }
}
```

### 5.4 日志过滤器集成

```
@Slf4j
@Component
public class SensitiveLogFilter implements Filter {

    private final SensitiveWordService sensitiveWordService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // 包装请求，读取请求体
        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(
                (HttpServletRequest) request);
        
        // 对请求参数进行敏感词检测
        String requestBody = cachedRequest.getCachedBody();
        if (StringUtils.hasText(requestBody)) {
            SensitiveWordService.LogFilterResult filterResult = 
                    sensitiveWordService.filterLogMessage(requestBody);
            
            if (filterResult.isSensitive()) {
                log.warn("请求包含敏感词: {}, traceId: {}", 
                        filterResult.getHitWords(), TraceContext.getTraceId());
            }
        }
        
        chain.doFilter(cachedRequest, response);
    }
}
```

------

## 六、日志分析定时推送 + 人工审核系统

### 6.1 整体流程设计

```
定时任务触发
    ↓
从ES聚合分析高频异常关键词
    ↓
提取疑似新敏感词候选
    ↓
写入待审核表
    ↓
推送通知给审核人员
    ↓
人工审核（通过/驳回）
    ↓
通过 → 并入正式敏感词库 → 刷新缓存
    ↓
驳回 → 标记为误报，加入白名单
```

### 6.2 定时任务实现

```
@Slf4j
@Component
@RequiredArgsConstructor
public class LogAnalysisScheduler {

    private final LogSearchService logSearchService;
    private final SensitiveWordCandidateMapper candidateMapper;
    private final ElasticsearchRestTemplate elasticsearchTemplate;

    /**
     * 每小时执行一次：分析日志提取疑似敏感词
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void analyzeSensitiveKeywords() {
        log.info("开始定时日志关键词分析任务");
        
        try {
            // 1. 聚合过去1小时的ERROR/WARN日志中的高频词汇
            List<String> candidateWords = aggregateHighFrequencyWords();
            
            // 2. 过滤已存在于词库中的词
            List<String> newCandidates = filterExistingWords(candidateWords);
            
            // 3. 写入待审核表
            saveCandidates(newCandidates);
            
            // 4. 推送通知
            if (!newCandidates.isEmpty()) {
                pushNotification(newCandidates);
            }
            
            log.info("日志关键词分析完成, 新增候选词 {} 个", newCandidates.size());
        } catch (Exception e) {
            log.error("日志关键词分析任务异常", e);
        }
    }

    /**
     * 从ES聚合高频异常词汇
     */
    private List<String> aggregateHighFrequencyWords() {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery()
                        .filter(QueryBuilders.termsQuery("level", "ERROR", "WARN"))
                        .filter(QueryBuilders.rangeQuery("@timestamp")
                                .gte("now-1h")))
                .addAggregation(AggregationBuilders.terms("top_words")
                        .field("message")
                        .size(50)
                        .minDocCount(3))
                .withPageable(PageRequest.of(0, 1))
                .build();
        
        SearchHits<LogDocument> searchHits = elasticsearchTemplate.search(query, LogDocument.class);
        ParsedStringTerms terms = searchHits.getAggregations().get("top_words");
        
        return terms.getBuckets().stream()
                .map(bucket -> bucket.getKeyAsString())
                .filter(word -> word.length() >= 2 && word.length() <= 20)
                .collect(Collectors.toList());
    }

    private void saveCandidates(List<String> words) {
        List<SensitiveWordCandidate> candidates = words.stream()
                .map(word -> {
                    SensitiveWordCandidate candidate = new SensitiveWordCandidate();
                    candidate.setWord(word);
                    candidate.setSource("log_analysis");
                    candidate.setStatus(0); // 待审核
                    candidate.setHitCount(1);
                    candidate.setCreateTime(LocalDateTime.now());
                    return candidate;
                })
                .collect(Collectors.toList());
        
        // 批量插入，去重
        candidateMapper.batchInsertIgnore(candidates);
    }

    private void pushNotification(List<String> newCandidates) {
        // 可接入企业微信/钉钉/邮件通知
        log.info("待审核敏感词通知: {}", newCandidates);
    }
}
```

### 6.3 人工审核服务

```
@Service
@RequiredArgsConstructor
public class SensitiveWordAuditService {

    private final SensitiveWordCandidateMapper candidateMapper;
    private final SensitiveWordMapper sensitiveWordMapper;
    private final SensitiveWordService sensitiveWordService;

    /**
     * 获取待审核列表
     */
    public Page<SensitiveWordCandidate> getPendingList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<SensitiveWordCandidate> list = candidateMapper.selectByStatus(0);
        return (Page<SensitiveWordCandidate>) list;
    }

    /**
     * 审核通过 - 并入正式词库
     */
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long candidateId, Long auditorId, String remark) {
        SensitiveWordCandidate candidate = candidateMapper.selectById(candidateId);
        if (candidate == null || candidate.getStatus() != 0) {
            throw new BusinessException("候选词状态异常");
        }
        
        // 1. 插入正式敏感词表
        SensitiveWord word = new SensitiveWord();
        word.setWord(candidate.getWord());
        word.setCategory(candidate.getCategory());
        word.setLevel(candidate.getLevel());
        word.setStatus(1);
        word.setSource("audit");
        word.setCreateTime(LocalDateTime.now());
        sensitiveWordMapper.insert(word);
        
        // 2. 更新候选词状态
        candidate.setStatus(1);
        candidate.setAuditorId(auditorId);
        candidate.setAuditRemark(remark);
        candidate.setAuditTime(LocalDateTime.now());
        candidateMapper.updateById(candidate);
        
        // 3. 刷新内存词库
        sensitiveWordService.refreshWordCache();
    }

    /**
     * 审核驳回
     */
    public void reject(Long candidateId, Long auditorId, String remark) {
        SensitiveWordCandidate candidate = candidateMapper.selectById(candidateId);
        if (candidate == null || candidate.getStatus() != 0) {
            throw new BusinessException("候选词状态异常");
        }
        
        candidate.setStatus(2); // 已驳回
        candidate.setAuditorId(auditorId);
        candidate.setAuditRemark(remark);
        candidate.setAuditTime(LocalDateTime.now());
        candidateMapper.updateById(candidate);
    }
}
```

------

## 七、数据库表设计

### 7.1 数据库初始化脚本

```
CREATE DATABASE IF NOT EXISTS elk_log_system 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

USE elk_log_system;
```

### 7.2 敏感词主表

```
CREATE TABLE `sys_sensitive_word` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `word` VARCHAR(100) NOT NULL COMMENT '敏感词内容',
    `category` VARCHAR(50) NOT NULL DEFAULT 'general' 
        COMMENT '分类: general通用, politics政治, porn色情, violence暴力, ad广告, insult辱骂',
    `level` TINYINT NOT NULL DEFAULT 2 
        COMMENT '敏感等级: 1-低, 2-中, 3-高',
    `status` TINYINT NOT NULL DEFAULT 1 
        COMMENT '状态: 0-禁用, 1-启用',
    `replace_char` VARCHAR(10) DEFAULT '*' COMMENT '替换字符',
    `source` VARCHAR(50) DEFAULT 'manual' 
        COMMENT '来源: manual人工, log_analysis日志分析, audit审核入库',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '备注说明',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_word` (`word`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`),
    KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='敏感词主表';
```

### 7.3 敏感词候选审核表

```
CREATE TABLE `sys_sensitive_word_candidate` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `word` VARCHAR(100) NOT NULL COMMENT '候选敏感词',
    `category` VARCHAR(50) DEFAULT 'general' COMMENT '分类',
    `level` TINYINT DEFAULT 2 COMMENT '敏感等级',
    `source` VARCHAR(50) NOT NULL DEFAULT 'log_analysis' 
        COMMENT '来源: log_analysis日志分析, user_report用户举报, manual手动添加',
    `hit_count` INT NOT NULL DEFAULT 1 COMMENT '命中次数',
    `status` TINYINT NOT NULL DEFAULT 0 
        COMMENT '状态: 0-待审核, 1-审核通过, 2-审核驳回',
    `auditor_id` BIGINT DEFAULT NULL COMMENT '审核人ID',
    `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
    `audit_remark` VARCHAR(500) DEFAULT NULL COMMENT '审核意见',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_word_source` (`word`, `source`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='敏感词候选审核表';
```

### 7.4 日志分析推送任务表

```
CREATE TABLE `sys_log_analysis_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_type` VARCHAR(50) NOT NULL COMMENT '任务类型: keyword关键词分析, error错误统计, performance性能分析',
    `task_date` DATE NOT NULL COMMENT '分析日期',
    `time_range` VARCHAR(20) NOT NULL COMMENT '时间范围: 1h, 24h, 7d',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待执行, 1-执行中, 2-成功, 3-失败',
    `result_summary` TEXT COMMENT '分析结果摘要',
    `candidate_count` INT DEFAULT 0 COMMENT '发现候选词数量',
    `error_msg` VARCHAR(1000) DEFAULT NULL COMMENT '错误信息',
    `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_task_date` (`task_date`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日志分析任务表';
```

### 7.5 审核操作日志表

```
CREATE TABLE `sys_audit_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `operator_id` BIGINT NOT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(100) NOT NULL COMMENT '操作人姓名',
    `business_type` VARCHAR(50) NOT NULL COMMENT '业务类型: sensitive_word敏感词审核',
    `business_id` BIGINT NOT NULL COMMENT '业务ID',
    `action` VARCHAR(50) NOT NULL COMMENT '操作: approve通过, reject驳回, edit编辑',
    `before_content` TEXT COMMENT '变更前内容',
    `after_content` TEXT COMMENT '变更后内容',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_business` (`business_type`, `business_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审核操作日志表';
```

### 7.6 系统配置表

```
CREATE TABLE `sys_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value` TEXT COMMENT '配置值',
    `config_desc` VARCHAR(255) DEFAULT NULL COMMENT '配置说明',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 初始化配置
INSERT INTO sys_config (config_key, config_value, config_desc) VALUES
('log.analysis.cron', '0 0 * * * ?', '日志分析定时任务cron表达式'),
('sensitive.word.auto.enable', '0', '是否自动通过低风险候选词'),
('sensitive.word.min.hit.count', '3', '候选词最小命中次数阈值'),
('notification.webhook.url', '', '通知Webhook地址');
```

------

## 八、核心 API 接口

### 8.1 日志查询接口

```
@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogSearchController {

    private final LogSearchService logSearchService;

    /**
     * 分词查询日志
     */
    @PostMapping("/search")
    public Result<Page<LogDocument>> searchLogs(@RequestBody LogQueryDTO queryDTO) {
        return Result.success(logSearchService.searchLogs(queryDTO));
    }

    /**
     * 根据traceId查询全链路日志
     */
    @GetMapping("/trace/{traceId}")
    public Result<List<LogDocument>> getTraceLogs(@PathVariable String traceId) {
        return Result.success(logSearchService.getTraceLogs(traceId));
    }
}
```

### 8.2 敏感词管理接口

```
@RestController
@RequestMapping("/api/sensitive-words")
@RequiredArgsConstructor
public class SensitiveWordController {

    private final SensitiveWordService sensitiveWordService;
    private final SensitiveWordAuditService auditService;

    /**
     * 分页查询敏感词
     */
    @GetMapping
    public Result<Page<SensitiveWord>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return Result.success(sensitiveWordService.getWordList(pageNum, pageSize, keyword, category));
    }

    /**
     * 新增敏感词
     */
    @PostMapping
    public Result<Void> add(@RequestBody SensitiveWord word) {
        sensitiveWordService.addWord(word);
        return Result.success();
    }

    /**
     * 刷新词库缓存
     */
    @PostMapping("/refresh")
    public Result<Void> refresh() {
        sensitiveWordService.refreshWordCache();
        return Result.success();
    }

    /**
     * 检测文本敏感词
     */
    @PostMapping("/check")
    public Result<SensitiveWordService.LogFilterResult> check(@RequestBody String text) {
        return Result.success(sensitiveWordService.filterLogMessage(text));
    }
}
```

### 8.3 审核管理接口

```
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final SensitiveWordAuditService auditService;

    /**
     * 获取待审核列表
     */
    @GetMapping("/candidates")
    public Result<Page<SensitiveWordCandidate>> getPendingList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(auditService.getPendingList(pageNum, pageSize));
    }

    /**
     * 审核通过
     */
    @PostMapping("/approve/{id}")
    public Result<Void> approve(@PathVariable Long id,
                                @RequestParam Long auditorId,
                                @RequestParam(required = false) String remark) {
        auditService.approve(id, auditorId, remark);
        return Result.success();
    }

    /**
     * 审核驳回
     */
    @PostMapping("/reject/{id}")
    public Result<Void> reject(@PathVariable Long id,
                               @RequestParam Long auditorId,
                               @RequestParam(required = false) String remark) {
        auditService.reject(id, auditorId, remark);
        return Result.success();
    }
}
```

------

## 九、部署与启动顺序

### 9.1 Windows 启动顺序

1. **启动 Elasticsearch**

   ```
   D:\ELK\elasticsearch-8.13.4\bin\elasticsearch.bat
   ```

2. **启动 Kibana**（等 ES 启动完成后）

   ```
   D:\ELK\kibana-8.13.4\bin\kibana.bat
   ```

3. **启动 Logstash**

   ```
   D:\ELK\logstash-8.13.4\bin\logstash.bat -f ../config/logback-springboot.conf
   ```

4. **启动 MySQL** 并执行建表脚本

5. **启动 Redis**

6. **启动 SpringBoot 应用**

### 9.2 验证步骤

1. 访问 `http://localhost:9200` 验证 ES
2. 访问 `http://localhost:5601` 验证 Kibana
3. 调用 SpringBoot 接口生成日志
4. 在 Kibana 中创建索引模式 `springboot-logs-*`
5. 在 Discover 页面查看日志，验证 traceId 链路追踪
6. 测试分词查询功能
7. 添加测试敏感词，验证过滤效果

------

## 十、性能优化建议

1. **ES 索引优化**：按天分索引，设置合理的分片数，定期清理过期日志
2. **批量写入**：Logstash 配置批量提交，减少 ES 写入压力
3. **词库热更新**：IK 分词器支持远程词库，敏感词变更无需重启 ES
4. **多级缓存**：敏感词使用 DFA 内存树 + Redis 二级缓存
5. **异步处理**：日志分析、通知推送使用异步线程池
6. **慢查询优化**：对高频查询字段设置 keyword 类型，避免全表扫描

需要我补充某一部分的详细代码实现，或者提供完整的项目结构示例吗？







### 4. Spring Boot 配置修改

#### 4.1 修改 `logback-spring.xml`

仅需修改 `destination` 地址为 Fluentd 的 TCP 端口（24224）：

xml

```
<appender name="FLUENTD" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
    <destination>127.0.0.1:24224</destination>
    <encoder class="net.logstash.logback.encoder.LogstashEncoder">
        <customFields>{"appName":"question-bank","env":"dev"}</customFields>
        <includeMdc>true</includeMdc>
    </encoder>
    <keepAliveDuration>5 minutes</keepAliveDuration>
</appender>
```



同时将 root 中的 `LOGSTASH` 改为 `FLUENTD`：

xml

```
<root level="INFO">
    <appender-ref ref="CONSOLE"/>
    <appender-ref ref="FLUENTD"/>
</root>
```



**无需修改 Maven 依赖**，继续使用 `logstash-logback-encoder`。

#### 4.2 其他代码不变

- `TraceIdFilter`、MDC、全链路追踪逻辑不变。
- 结构化日志输出不变。
- 定时分析任务、敏感词系统不变。

------

### 5. Elasticsearch 索引模板（不变）

无需修改之前创建的 `app-logs-template`，字段映射依然适用。

------

### 6. 验证步骤（更新）

#### 6.1 启动顺序

1. 启动 Elasticsearch、Kibana。
2. 启动 Fluentd（Docker 容器或直接运行）。
3. 启动 Spring Boot 应用。
4. 调用搜索接口：

cmd

```
curl "http://localhost:8080/api/questions/search?keyword=动态规划"
```



#### 6.2 验证 Fluentd 接收日志

检查 Fluentd 日志输出（Docker 容器可 `docker logs fluentd`），应看到接收的日志记录。

#### 6.3 验证 Elasticsearch 索引

在 Kibana Dev Tools 中查询：

json

```
GET app-logs-*/_search
{
  "query": {
    "match_all": {}
  },
  "size": 1
}
```



确认有数据。

#### 6.4 验证全链路 traceId

同原方案，从响应头获取 `X-Trace-Id` 并在 ES 中查询。

#### 6.5 验证分词查询

同原方案，使用 `match` 查询 `message` 或 `searchWord`。

------

### 7. 常见问题

#### 7.1 Fluentd 无法接收日志

- 检查 Fluentd 是否监听 24224 端口：`netstat -an | findstr 24224`。
- 检查 Spring Boot 日志中是否有连接错误。
- 若使用 Docker，确认端口映射 `-p 24224:24224` 正确。

#### 7.2 Fluentd 输出到 Elasticsearch 失败

- 检查 Fluentd 日志中是否有错误信息。
- 确认 `fluent-plugin-elasticsearch` 插件已安装（Docker 镜像已包含）。
- 确认 Elasticsearch 地址和端口正确（127.0.0.1:9200）。

#### 7.3 时间字段问题

- 如果 Kibana 中时间不正确，检查 Fluentd 的 `time_key` 配置和 Logback 输出的 `@timestamp` 格式是否兼容。
- 可简化配置，让 Fluentd 使用自身时间（去掉 `time_key`），但会丢失原始日志时间。

#### 7.4 Windows 直接安装 Fluentd 失败

- 推荐使用 Docker 方式，避免 Ruby 环境问题。
- 如果必须直接安装，请使用 RubyInstaller + DevKit 并安装 `fluentd` gem，确保网络可访问 [rubygems.org](https://rubygems.org/)。

#### 7.5 Logback 连接 Fluentd 后日志丢失

- 检查 Fluentd 的 `in_tcp` 是否使用 `json` 解析，且 `tag` 匹配。
- 若日志中包含非 JSON 内容（如堆栈换行），可能解析失败，可在 Fluentd 中使用 `@type multiline` 或 `@type json` 并设置 `unmatched_lines`。

------

### 8. 总结

本次改造仅将日志收集器从 **Logstash** 替换为 **Fluentd**：

| 组件             | 原方案                     | 新方案                      |
| :--------------- | :------------------------- | :-------------------------- |
| 日志收集器       | Logstash                   | Fluentd                     |
| 接收端口         | 5044                       | 24224                       |
| 输入协议         | TCP + JSON lines           | TCP + JSON lines            |
| 输出目标         | Elasticsearch              | Elasticsearch               |
| Spring Boot 配置 | destination 127.0.0.1:5044 | destination 127.0.0.1:24224 |

其余功能（全链路追踪、分词查询、关键词屏蔽、人工审核）完全保持不变。Fluentd 相比 Logstash 更轻量，适合开发机资源有限的环境，且可通过 Docker 快速部署。

