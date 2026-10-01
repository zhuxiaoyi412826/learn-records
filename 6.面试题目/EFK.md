# EFK 面试题 100 道（仅问题）

## 一、基础概念与架构（15 道）

1. 什么是 EFK 技术栈？它主要解决什么业务问题？
2. EFK 和 ELK 的核心区别是什么？各自的适用场景有哪些？
3. EFK 三个核心组件 Elasticsearch、Fluentd、Kibana 各自的职责是什么？
4. 为什么很多场景下选择 Fluentd 替代 Logstash？
5. 描述 EFK 完整的日志数据流向。
6. 什么是日志聚合？EFK 在日志聚合方案中的核心优势是什么？
7. EFK 可以处理哪些类型的日志数据？
8. EFK 整体架构分为哪几层？每层的作用是什么？
9. EFK 各组件之间通过什么协议和方式通信？
10. 什么是 Sidecar 边车模式？在 Kubernetes 环境中 EFK 如何应用该模式？
11. EFK 技术栈适合什么规模的业务系统使用？
12. 除了 EFK/ELK，你还知道哪些主流的企业级日志解决方案？
13. EFK 相比传统的文件日志 + grep 排查方式，有哪些核心价值？
14. 什么是结构化日志和非结构化日志？EFK 如何分别处理这两类日志？
15. EFK 在微服务架构中扮演什么角色？

## 二、Elasticsearch 核心（30 道）

1. 什么是 Elasticsearch？它的核心特性有哪些？
2. Elasticsearch 底层基于什么开源搜索引擎库实现？
3. 解释 Elasticsearch 中索引、文档、字段、分片的概念。
4. Elasticsearch 7.x 版本之后为什么移除了 type 类型？
5. 什么是倒排索引？简述它的工作原理。
6. 正排索引和倒排索引的区别是什么？分别适用于什么场景？
7. 什么是主分片和副本分片？它们各自的作用是什么？
8. 索引分片数量设置的依据是什么？是不是分片越多性能越好？
9. 什么是 Segment 段？段合并的过程和作用是什么？
10. 简述 Elasticsearch 的数据写入完整流程。
11. 简述 Elasticsearch 的查询请求完整处理流程。
12. 什么是近实时（NRT）？为什么 Elasticsearch 是近实时的？
13. refresh 操作和 flush 操作的本质区别是什么？
14. translog 事务日志的作用是什么？
15. Elasticsearch 有哪些节点类型？各自的职责是什么？
16. 什么是协调节点？它在查询过程中起到什么作用？
17. Elasticsearch 集群脑裂是什么原因造成的？如何避免？
18. discovery.zen.minimum_master_nodes 参数的作用是什么？
19. 什么是 Elasticsearch 的路由（Routing）机制？
20. 默认情况下，文档是如何路由到对应分片的？
21. Elasticsearch 支持哪几大类查询方式？
22. match 查询和 term 查询的核心区别是什么？
23. bool 查询包含哪几种子句？各自的逻辑作用是什么？
24. 什么是聚合查询？Metric 聚合和 Bucket 聚合的区别是什么？
25. 什么是深度分页问题？产生的原因是什么？
26. scroll 查询和 search_after 都可以解决深度分页，它们的区别和适用场景是什么？
27. 什么是 Mapping？动态映射和静态映射的区别是什么？
28. text 类型和 keyword 类型的区别是什么？分别适用于什么场景？
29. 分词器（Analyzer）由哪几部分组成？
30. IK 分词器有哪两种分词模式？区别是什么？

## 三、Fluentd 核心（20 道）

1. 什么是 Fluentd？它的核心设计理念是什么？
2. Fluentd 的核心组件有哪些？
3. 解释 Fluentd 中 Source、Filter、Match、Buffer 四个核心模块的作用。
4. Fluentd 支持哪些常见的日志输入源？
5. Fluentd 支持哪些常见的数据输出目标？
6. Fluentd 的事件（Event）结构包含哪几个部分？
7. 什么是 Fluentd 的标签（Tag）机制？它有什么作用？
8. Fluentd 的缓冲机制是怎样的？内存缓冲和文件缓冲的区别是什么？
9. Fluentd 如何保证日志数据传输过程中不丢失？
10. @type 参数在 Fluentd 配置文件中的作用是什么？
11. 列举 3 个常用的 Fluentd 输入插件及其用途。
12. 列举 3 个常用的 Fluentd 过滤插件及其用途。
13. Fluentd 如何解析不同格式的日志（如 JSON、Nginx、Syslog）？
14. 什么是 Fluent Bit？它和 Fluentd 的关系与区别是什么？
15. 什么场景下选择 Fluent Bit 而不是 Fluentd？
16. Fluentd 如何实现同一份日志多路输出到不同目的地？
17. Fluentd 的失败重试机制是怎样的？
18. 如何监控 Fluentd 的运行状态和性能指标？
19. Fluentd 在 Kubernetes 环境中通常有哪几种部署方式？
20. Fluentd 如何处理 Java 异常栈这类多行日志？

## 四、Kibana 核心（15 道）

1. 什么是 Kibana？它的主要功能模块有哪些？
2. Kibana 和 Elasticsearch 是什么关系？
3. Kibana 的 Discover 功能是做什么的？
4. 什么是 Index Pattern（索引模式）？它的作用是什么？
5. Kibana 的 Visualize 模块可以创建哪些类型的可视化图表？
6. Dashboard 的作用是什么？制作一个业务监控 Dashboard 的步骤是什么？
7. 什么是 Lucene 查询语法？在 Kibana 中如何使用？
8. KQL（Kibana Query Language）和 Lucene 查询语法的区别是什么？
9. 什么是 Timelion？它的典型使用场景是什么？
10. Kibana 如何实现日志的实时监控与刷新？
11. 什么是 Elastic APM？它和 Kibana、EFK 是什么关系？
12. Kibana 的告警（Alerting）功能如何配置和使用？
13. 如何在 Kibana 中进行日志的全文检索和条件过滤？
14. Kibana 的 Spaces（空间）功能有什么作用？
15. Kibana 如何实现基于角色的用户权限管理？

## 五、部署与运维（10 道）

1. EFK 常见的部署方式有哪几种？各有什么优缺点？
2. 在 Docker 环境下部署 EFK 的大致步骤是什么？
3. 在 Kubernetes 中部署 EFK 有哪些主流方案？
4. 如何实现 EFK 各组件的高可用部署？
5. EFK 各组件之间的版本兼容性需要注意哪些问题？
6. 如何对 EFK 集群进行存储和计算资源的容量规划？
7. 日志的生命周期和保留策略如何在 EFK 中配置？
8. 如何实现 EFK 集群的滚动升级？
9. EFK 集群需要重点监控哪些核心指标？
10. 如何对 EFK 中的历史日志数据进行备份与恢复？

## 六、性能优化与故障排查（10 道）

1. Elasticsearch 写入性能优化的常用手段有哪些？
2. Elasticsearch 查询性能优化的常用手段有哪些？
3. Fluentd 高并发日志采集场景下的性能优化点有哪些？
4. 海量日志写入场景下，整体 EFK 栈的性能优化思路是什么？
5. 分片大小对 Elasticsearch 性能有什么影响？合理的分片大小范围是多少？
6. 段合并对 Elasticsearch 读写性能有什么影响？如何调优？
7. 如何降低 EFK 方案的整体存储成本？
8. 冷热数据分离架构在 EFK 中如何实现？
9. 高并发日志洪峰场景下，如何避免 Elasticsearch 集群被打垮？
10. 当发现 Kibana 查询日志很慢时，你的排查思路是什么？

## 七、实战题目（20 道）

1. 如何使用 Docker Compose 部署完整 EFK 栈，并配置 Elasticsearch 数据卷持久化与端口映射？
2. 在 Kubernetes 集群中，如何以 DaemonSet 方式部署 Fluentd，实现全节点容器日志与系统日志采集？
3. 详细说明配置 Fluentd 采集 Nginx access 日志，将其解析为结构化 JSON 后写入 Elasticsearch 的配置流程。
4. 如何配置 Fluentd 处理 Java 应用的多行异常堆栈日志，确保一次完整异常不被拆分为多条日志？
5. 如何配置 Elasticsearch 索引生命周期管理（ILM）策略，实现日志索引按日滚动、30 天后自动删除？
6. Fluentd 写入 Elasticsearch 时频繁报 429 Too Many Requests 错误，说明你的排查流程与解决方案。
7. 生产环境日志峰值写入量远超预期，Elasticsearch 集群写入队列积压，给出 EFK 全链路的性能优化方案。
8. 如何通过 Fluentd 配置，根据日志的 level 字段，将 ERROR 级日志和普通日志分别写入不同的 ES 索引？
9. 如何在 Fluentd 的 filter 阶段对日志中的手机号、身份证号、密钥等敏感字段进行掩码脱敏？
10. 描述搭建三节点 Elasticsearch 高可用集群，并对接 Fluentd 采集端与 Kibana 可视化端的核心配置要点。
11. 如何基于 EFK 实现业务错误日志的实时告警，并将告警消息推送到企业微信或钉钉群？
12. 如何设计并配置 Elasticsearch 冷热节点架构，将 7 天以上的历史日志自动迁移至冷节点存储？
13. Kibana 无法发现 Elasticsearch 中已存在的日志索引，无法创建索引模式，请列出你的排查步骤。
14. 在 Kubernetes 微服务场景下，如何使用 Fluentd Sidecar 模式采集指定 Pod 内的应用日志文件？
15. 如何为 Elasticsearch 核心业务日志索引配置定时快照备份，以及如何从快照恢复指定索引数据？
16. 如何在 Fluentd 中配置重复日志去重机制，减少海量重复异常日志对 ES 存储的占用？
17. 当存在单条大体积日志（如超长异常栈、完整请求报文）时，如何配置 EFK 避免日志截断或丢失？
18. 针对日均产生 1TB 日志量的业务系统，你如何规划 Elasticsearch 的分片数、副本数与节点资源配置？
19. 如何对 EFK 日志采集全链路进行压测，定位系统性能瓶颈并评估最大吞吐能力？
20. Elasticsearch 集群部分主分片损坏不可恢复时，如何快速恢复 EFK 系统的写入与查询服务？