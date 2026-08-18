# Elasticsearch 分词搜索与索引同步全流程文档

> 更新日期：2026-08-18
> 涉及模块：Vue 前端（EsManage.vue）、Java 后端（VectorSearchService / VectorAdminController）、Python 服务（know-retrieval）、Elasticsearch 7.12.1 + IK 分词器

---

## 一、整体架构

```
┌─────────────┐   HTTP    ┌──────────────────┐  OpenFeign  ┌──────────────────┐  elasticsearch-py  ┌─────────────┐
│ Vue 前端     │ ───────→ │ Java Spring Boot │ ──────────→ │ Python FastAPI   │ ─────────────────→ │ ES 7.12.1   │
│ EsManage.vue │           │ :80              │             │ know-retrieval   │                    │ + IK 分词器  │
└─────────────┘           │ VectorAdmin      │             │ :8001            │                    │ :9200       │
                           │ Controller/Service│            │ app/main.py      │                    └─────────────┘
                           │ + SyncProgress   │             │ app/es_search.py │
                           └──────────────────┘             └──────────────────┘
```

职责分工：

| 层 | 文件 | 职责 |
|----|------|------|
| 前端 | `AlgoVize/houtai/src/views/content/EsManage.vue` | 管理操作按钮、进度条轮询、结果展示 |
| Java | `VectorAdminController.java` | 暴露 `/api/vector/admin/es/**` 管理接口、`/api/vector/es-search` 用户搜索接口 |
| Java | `VectorSearchService.java` | 全量/批量同步编排、异步分批、进度上报、搜索回查 MySQL、降级 |
| Java | `SyncProgressHolder.java` | 全局内存进度状态（processed/failed/total/phase/耗时） |
| Python | `app/main.py` | HTTP 接口：`/api/v1/es/index/batch`、`/api/v1/es-search` 等 |
| Python | `app/es_search.py` | 索引 mapping 定义、bulk 写入、IK 查询构建、高亮 |

---

## 二、索引如何设立（IK 分词器设计）

### 2.1 索引定义（Python：`app/es_search.py` 的 `INDEX_SETTINGS`）

```python
INDEX_SETTINGS = {
    "settings": {
        "number_of_shards": 1,
        "number_of_replicas": 0,
        "analysis": {
            "analyzer": {
                # 索引时用 ik_max_word：细粒度切分，最大化召回
                "ik_max_word_analyzer": {
                    "type": "custom",
                    "tokenizer": "ik_max_word",
                    "filter": ["lowercase"]
                },
                # 搜索时用 ik_smart：粗粒度切分，减少噪音，提高精度
                "ik_smart_analyzer": {
                    "type": "custom",
                    "tokenizer": "ik_smart",
                    "filter": ["lowercase"]
                }
            }
        }
    },
    "mappings": {
        "properties": {
            "problem_id":   { "type": "integer" },
            "problem_no":   { "type": "keyword" },
            "title":        { "type": "text", "analyzer": "ik_max_word_analyzer",
                              "search_analyzer": "ik_smart_analyzer",
                              "fields": { "keyword": { "type": "keyword", "ignore_above": 256 } } },
            "tags":         { "type": "text", "analyzer": "ik_max_word_analyzer",
                              "search_analyzer": "ik_smart_analyzer",
                              "fields": { "keyword": { "type": "keyword" } } },
            "category":     { "type": "text", "analyzer": "ik_max_word_analyzer",
                              "search_analyzer": "ik_smart_analyzer",
                              "fields": { "keyword": { "type": "keyword" } } },
            "difficulty":   { "type": "keyword" },   # 精确过滤用
            "description":  { "type": "text", "analyzer": "ik_max_word_analyzer",
                              "search_analyzer": "ik_smart_analyzer" },
            "content":      { "type": "text", "analyzer": "ik_max_word_analyzer",
                              "search_analyzer": "ik_smart_analyzer" },
            "view_count":   { "type": "integer" },
            "created_at":   { "type": "date",
                              "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis" }
        }
    }
}
```

### 2.2 设计要点

1. **双分词器策略（核心）**
   - 索引期 `ik_max_word`：把"动态规划入门"切成「动态/规划/动态规划/入门」等多个词元，倒排索引条目多 → **召回率高**；
   - 搜索期 `ik_smart`：把用户查询切成粗粒度词元 → 减少 OR 噪音 → **精度高**。
   - 这是中文搜索的经典组合，避免了"一个词库两头兼顾"的矛盾。

2. **字段类型选择**
   - `title/tags/category/description/content`：`text` + IK，参与分词与评分；
   - `difficulty/problem_no`：`keyword`，不分词，用于 term 精确过滤；
   - `title/tags/category` 额外带 `.keyword` 子字段，可用于聚合/精确排序。

3. **content 是冗余拼接字段**：`title + tags + category + description` 拼在一起，权重最低（0.5），作为兜底召回。

4. **索引生命周期管理**（Python 提供三个函数）：
   - `ensure_index()`：不存在则创建（批量写入前自动调用，保证索引永远存在）；
   - `recreate_index()`：先删后建，**切换分词器/修改 mapping 时必须用**（mapping 不可原地修改）；
   - `delete_index()`：整库删除。

---

## 三、题目如何导入（同步流程）

### 3.1 触发入口（两个场景）

| 场景 | 入口 | Java 方法 | 说明 |
|------|------|-----------|------|
| 全量同步 | EsManage 页面「全量同步到 ES」按钮 | `esSyncAll()` → `doAsyncEsSyncAll()` | MySQL 全部题目 → ES，异步分批 + 进度条 |
| 增量导入 | 题目管理页导入 JSON 题目成功后 | `esSyncBatch(problems)` | 只同步本次导入的题目 |

### 3.2 全量同步链路（异步 + 分批 + 进度）

**① Java：提交任务，立即返回**

```java
// VectorSearchService.java
public Map<String, Object> esSyncAll() {
    if (!progress.isRunning()) {
        progress.startTask("es_sync", 0);        // 全局进度状态：PREPARING
    } else {
        return Map.of("submitted", false, "message", "已有同步任务在执行中，请等待完成后再试");
    }
    doAsyncEsSyncAll();                           // 丢给专用线程池，立即返回
    return Map.of("submitted", true, "message", "已提交 ES 索引同步任务，请查看进度条");
}
```

**② Java：异步任务分批推送到 Python（每批 200 条）**

```java
@Async("syncTaskExecutor")                        // AsyncConfig 配置的专用线程池
public void doAsyncEsSyncAll() {
    try {
        progress.updateMessage("正在从 MySQL 读取面试题...");
        List<InterviewProblem> all = problemMapper.listAllForExport(null, null);  // 一次读全量
        progress.enterRunningPhase(all.size());   // RUNNING，total=7800

        int success = 0, fail = 0;
        int totalSize = all.size();               // ES_BATCH_SIZE = 200

        for (int i = 0; i < totalSize; i += ES_BATCH_SIZE) {
            List<InterviewProblem> batch = all.subList(i, Math.min(i + ES_BATCH_SIZE, totalSize));
            try {
                List<Map<String, Object>> problemList = new ArrayList<>();
                for (InterviewProblem p : batch) {
                    if (p == null || p.getId() == null) continue;
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", p.getId());
                    item.put("problemNo", p.getProblemNo());
                    item.put("title", p.getTitle());
                    item.put("tags", p.getTags());
                    item.put("category", p.getCategory());
                    item.put("difficulty", p.getDifficulty());
                    item.put("description", p.getDescription());
                    item.put("content", p.getTitle() + " " + p.getTags() + " "
                                  + p.getCategory() + " " + p.getDescription());
                    item.put("viewCount", p.getViewCount() != null ? p.getViewCount() : 0);
                    // ★ 关键：必须输出 ES mapping 要求的 "yyyy-MM-dd HH:mm:ss"
                    // LocalDateTime.toString() 会输出 ISO 的 "T" 分隔格式，ES 直接拒绝
                    item.put("createdAt", formatEsDate(p.getCreatedAt()));
                    problemList.add(item);
                }
                Map<String, Object> result = vectorClient.esIndexBatch(
                        Map.of("problems", problemList));       // OpenFeign → Python

                int batchOk   = ((Number) result.getOrDefault("success", 0)).intValue();
                int batchFail = ((Number) result.getOrDefault("failed", 0)).intValue();
                success += batchOk;  fail += batchFail;
                progress.addProcessed(batchOk);
                progress.addFailed(batchFail);
                progress.updateMessage(String.format(
                        "ES 同步: 已处理 %d / %d (成功 %d, 失败 %d)",
                        success + fail, totalSize, success, fail));
            } catch (Exception e) {                 // 单批失败不中断整体
                fail += batch.size();
                progress.addFailed(batch.size());
                progress.updateMessage("... 当前批次失败, 错误=" + e.getMessage());
            }
        }
        progress.complete(String.format("ES 索引同步完成! 成功 %d, 失败 %d, 共 %d, 耗时 %.1f秒",
                          success, fail, totalSize, (System.currentTimeMillis() - t0) / 1000.0));
    } catch (Exception e) {
        progress.fail(e.getMessage());
    }
}
```

**③ Python：批量写入 ES（`_bulk` API）**

```python
# app/main.py
@app.post("/api/v1/es/index/batch")
async def es_index_batch(req: ESIndexBatchRequest):
    es = es_search.get_es_client(ES_URL)
    if es is None:
        raise HTTPException(status_code=503, detail="ES 服务不可用")
    es_search.ensure_index(es)                     # 索引不存在则按 IK mapping 创建
    result = es_search.bulk_index_problems(es, req.problems)
    return result                                  # {"success": N, "failed": M}

# app/es_search.py
def bulk_index_problems(es_client, problems):
    success = failed = 0
    actions = []
    for p in problems:
        doc = { "problem_id": p.get("id"), "problem_no": p.get("problemNo", ""),
                "title": p.get("title", ""), "tags": p.get("tags", ""),
                "category": p.get("category", ""), "difficulty": p.get("difficulty", ""),
                "description": p.get("description", ""), "content": p.get("content", ""),
                "view_count": p.get("viewCount", 0) }
        created_at = p.get("createdAt")            # 空日期不下发，避免 date 解析失败
        if created_at:
            doc["created_at"] = created_at
        actions.append({"index": {"_index": ES_INDEX, "_id": str(p.get("id"))}})
        actions.append(doc)

    result = es_client.bulk(body=actions, refresh=True)   # refresh=True 立即可搜
    if result.get("errors"):
        for item in result.get("items", []):
            if item.get("index", {}).get("status", 0) < 400: success += 1
            else: failed += 1
        # 记录第一条失败原因（排查 date 格式/字段类型问题的关键日志）
        for item in result.get("items", []):
            err = item.get("index", {}).get("error")
            if err:
                logger.error("ES bulk 首条失败详情: id=%s type=%s reason=%s",
                             item.get("index", {}).get("_id"), err.get("type"), err.get("reason"))
                break
    else:
        success = len(problems)
    return {"success": success, "failed": failed}
```

> 说明：`_id` 固定使用 MySQL 主键 `problem.id`，因此**重复同步是幂等的覆盖写**（upsert 效果），不需要先清空索引。

**④ 前端进度条轮询**

```typescript
// vector.ts
syncProgress: () => request.get<any>('/vector/admin/sync/progress'),

// EsManage.vue：提交同步后 startProgressPolling()，每 1s 拉一次
// 显示：进度%、已导入、失败、总数、剩余、已用时间、预计剩余时间
// phase ∈ preparing / running / completed / failed，完成后保留 10s 再隐藏
```

后端进度接口：

```java
// VectorAdminController.java
@GetMapping("/sync/progress")
public InterviewResponse<SyncProgressHolder.ProgressSnapshot> syncProgress() {
    return InterviewResponse.ok(progressHolder.snapshot());   // 不可变快照
}
```

### 3.3 顺序图（全量同步）

```
Vue        Java(esSyncAll)   Java(@Async)      Python(/es/index/batch)   ES(_bulk)
 │ POST /es/sync-all │              │                    │                 │
 │──────────────────→│ startTask    │                    │                 │
 │←─ submitted:true ─│ doAsyncEsSyncAll() 抛线程         │                 │
 │                   │─────────────→│ 读 MySQL 7800 条    │                 │
 │ GET /sync/progress│  (每秒)      │ 分批200条 ─────────→│ ensure_index    │
 │──────────────────→│ snapshot()   │                    │────────────────→│
 │←─ 进度快照 ────────│              │←─ success/failed ──│←─ 200 OK ───────│
 │   ...循环...       │              │ 循环 39 批          │                 │
 │                   │              │ complete()         │                 │
 │←─ 完成 成功7800 ───│              │                    │                 │
```

---

## 四、搜索题目如何返回结果

### 4.1 Java 入口（前台用户搜索）

```java
// VectorSearchService.java
public InterviewResponse<List<InterviewProblem>> esSearch(String query, int topK, String difficulty) {
    try {
        ESSearchRequest req = new ESSearchRequest();
        req.setQuery(query); req.setTopK(topK);
        req.setDifficulty(difficulty != null ? difficulty : "");
        Map<String, Object> result = vectorClient.esSearch(req);   // Feign → Python

        // 1) 提取 ES 返回的 problemId 列表（已按相关性 _score 排好序）
        List<Long> ids = extractIds(result);

        // 2) 回 MySQL 查完整题目（ES 只存搜索字段，详情以 MySQL 为准 → 天然解耦）
        List<InterviewProblem> problems = problemMapper.selectByIds(ids);

        // 3) 按 ES 相关性顺序重排（MySQL IN 查询不保序，必须手动还原）
        Map<Long, InterviewProblem> idMap = problems.stream()
                .collect(Collectors.toMap(InterviewProblem::getId, p -> p, (a, b) -> a));
        List<InterviewProblem> ordered = ids.stream().map(idMap::get)
                .filter(Objects::nonNull).toList();

        return InterviewResponse.ok("ES 分词搜索成功", ordered);
    } catch (Exception e) {
        log.warn("[ES 搜索] 服务异常，降级到 MySQL: {}", e.getMessage());
        return fallbackSearch(query);          // ★ 降级：ES 挂了走 MySQL LIKE
    }
}
```

### 4.2 Python：查询构建（多字段加权 + 高亮）

```python
# app/es_search.py  search_problems()
should_queries = [
    {"match_phrase": {"title":    {"query": query, "boost": 4.0}}},  # 标题整短语（最高分）
    {"match":        {"title":    {"query": query, "boost": 3.0, "operator": "or"}}},
    {"match_phrase": {"tags":     {"query": query, "boost": 3.0}}},  # 标签整短语
    {"match":        {"tags":     {"query": query, "boost": 2.0}}},
    {"match":        {"category": {"query": query, "boost": 1.5}}},
    {"match":        {"description": {"query": query, "boost": 1.0}}},
    {"match":        {"content":  {"query": query, "boost": 0.5}}},  # 冗余兜底
]
filter_queries = []
if difficulty:
    filter_queries.append({"term": {"difficulty": difficulty}})      # 不参与评分

body = {
    "query": {"bool": {
        "should": should_queries,
        "filter": filter_queries,
        "minimum_should_match": 1
    }},
    "highlight": {                       # 关键词高亮，前端直接渲染 <em>
        "fields": {"title": {}, "tags": {}, "description": {}, "category": {}},
        "pre_tags": ["<em>"], "post_tags": ["</em>"],
        "fragment_size": 150
    },
    "from": 0, "size": top_k,
    "_source": ["problem_id", "problem_no", "title", "tags", "category", "difficulty"]
}
result = es_client.search(index=ES_INDEX, body=body)
# 返回 [{problemId, title, tags, score, highlight}, ...], total, took_ms
```

### 4.3 搜索效果举例

用户搜「二叉树遍历」：
1. IK 用 `ik_smart` 把查询切成 `二叉树 / 遍历`；
2. `match_phrase`（整短语"二叉树遍历"）命中的题目 boost=4 排最前；
3. 标题含"二叉树"或"遍历"的次之（OR 命中一个就有分）；
4. 仅 description/content 模糊提及的排最后；
5. 若勾选难度=hard，`term` filter 直接过滤，不影响打分；
6. 命中字段带 `<em>二叉树</em>` 高亮片段返回。

---

## 五、踩过的坑（重要经验）

| # | 现象 | 根因 | 修复 |
|---|------|------|------|
| 1 | 全量同步 7800 条**全部失败**，耗时仅 1.6 秒 | Java `LocalDateTime.toString()` 输出 `2024-01-15T10:30:00`（T 分隔），ES mapping 只接受 `yyyy-MM-dd HH:mm:ss`，每条报 `mapper_parsing_exception` | Java 用 `DateTimeFormatter` 格式化；Python 端空日期不下发该字段 |
| 2 | 批量失败但**看不到原因** | Python 只统计 failed 数，不打印 ES 拒绝详情 | bulk 有 errors 时记录首条 `error.reason` 到日志（`D:/rizi/python-app.log`） |
| 3 | 改完代码仍失败 | `replace_all` 因缩进不同漏改了 `doAsyncEsSyncAll` 里的第二处日期代码 | 排查时以"运行进程+日志证据"定位，不依赖记忆 |

**排查方法论**（本次实际使用）：
1. 查进度接口 → `failed=7800, elapsed=1.6s` → 秒失败 = 逐条拒绝而非超时；
2. 查 Python 日志 → `_bulk` 返回 200 但 `success=0 failed=200` → 问题在 ES 侧解析；
3. 直接向 ES 发 T 格式日期 → 复现 `mapper_parsing_exception` → 铁证。

---

## 六、当前不足

1. **全量读内存**：`listAllForExport` 一次把 7800 条（含长 description）读进 JVM，题目到 5 万+ 时内存与 MySQL 单查询压力都会成为瓶颈。
2. **单线程同步**：一个全局 `SyncProgressHolder` 只支持一个任务，向量同步与 ES 同步不能并行；批次也是串行推送。
3. **无增量更新机制**：只有"全量重推"和"导入时随批推送"两种；后台直接改库、批量 UPDATE 不会反映到 ES，存在数据漂移风险（没有对账手段）。
4. **同步无持久化**：进度在内存里，后端重启进度丢失；也没有失败重试/断点续传，失败的那批只能整体重来。
5. **搜索功能较基础**：
   - 无分页（固定 `from:0, size:top_k`）；
   - 无搜索词高亮以外的纠错/拼音/同义词（IK 自带词库较通用，算法术语如"背包九讲""单调栈"可能切分不理想）；
   - 搜索结果只回 MySQL 主表，浏览数等动态字段每次搜索时不同步；
   - `refresh=True` 每批强制刷新，写入吞吐量牺牲较大。
6. **索引单分片单副本0**：数据量增长后无法水平扩展，也无高可用。
7. **权限与安全**：管理端同步/删索引接口未见鉴权限制（依赖前端路由守卫）。
8. 进行拆分把ES拆分出去从向量语义检索里

---

## 七、后续扩展方向

### 7.1 数据链路健壮性（优先级高）
- **游标分页读库**：用 `WHERE id > lastId ORDER BY id LIMIT batchSize` 或 MyBatis `ResultHandler` 流式读取，替代全量进内存；
- **失败重试队列**：批次失败进入重试表（或内存队列+退避重试），进度增加 `retrying` 状态；进度快照落 Redis，重启可恢复；
- **定时对账任务**：低峰期比对 `MySQL count` 与 `ES count` + 抽样校验，不一致自动触发补偿同步；更进一步可用 MySQL binlog（Canal）驱动准实时增量。

### 7.2 搜索体验
- **分页与排序**：请求增加 `page/pageSize/sortField`，响应带 `total`；
- **同义词词库**：IK 支持远程词典，配置"算法术语同义词"（如 `dp=动态规划`、`双指针=two pointers`）；
- **拼音检索**：接入 `elasticsearch-analysis-pinyin`，支持 "ecs"/"erchashu" 拼音首字母召回；
- **搜索建议**：用 `completion suggester` 做标题前缀联想（搜索框下拉）；
- **混合检索**：项目已有向量语义检索（Chroma）与 ES 关键词检索，可做 **RRF（Reciprocal Rank Fusion）融合排序**：语义召回解决"意思相近但用词不同"，关键词解决"术语精确命中"，互补短板。

### 7.3 性能与容量
- 写入侧：同步期间去掉 `refresh=True`，改为完成后一次 `POST /_refresh`；批量从 200 提到 500~1000 观察；
- 索引侧：数据过 10 万时 `number_of_shards` 调到 3~5，副本设 1 保证可用性；
- 查询侧：热字段（category.keyword 聚合）结果缓存；`profile API` 分析慢查询。

### 7.4 运维可观测
- 复用现有 EFK 链路：把每次同步任务的 trace_id 写入日志，Kibana 中可按 TraceId 串起 Vue→Java→Python→ES 全链路；
- 同步完成/失败推送企业微信或站内信通知管理员；
- 管理页展示 ES 集群健康度（`_cluster/health`）与索引磁盘占用。

### 7.5 安全
- 管理端同步/重建/删除索引接口加管理员角色校验（后端拦截器层面）；
- 搜索接口做查询长度/频率限制，防恶意大查询打垮 ES。

## 八、实际过程

让我先用你环境里实际运行的 ES 做个分词演示，再讲规则。

实测结果出来了，结合代码讲清整套规则。规则分 三层 ：字段规则、分词规则、查询匹配规则。

## 第一层：字段规则（什么字段参与分词）
| 字段                                        | 类型      | 规则                                                         |
| ------------------------------------------- | --------- | ------------------------------------------------------------ |
| title、tags、category、description、content | text + IK | 分词、参与相关性打分                                         |
| difficulty、problem_no                      | keyword   | 不分词，整体精确匹配（term 过滤）                            |
| title/tags/category 的 .keyword 子字段      | keyword   | 不分词，留作聚合 / 精确排序                                  |
| created_at                                  | date      | 只接受 `yyyy‑MM‑dd HH:mm:ss` 等三种格式（上次 7800 全失败就是这个规则卡的） |

## 第二层：分词规则（IK 怎么切词）——刚实测的结果
同一个句子，两种切法：

```
原文: 动态规划入门二叉树遍历

索引时 ik_max_word（最细粒度）:
  动态 | 规划 | 划入 | 入门 | 二叉树 | 二叉 | 二 | 叉 | 树 | 遍历   ← 10个词元

搜索时 ik_smart（最粗粒度）:
  动态 | 规划 | 入门 | 二叉树 | 遍历                                    ← 5个词元
```

k_max_word ：词典正向最大匹配 + 递归细分，能切出来的词 全切 （“二叉树”会同时产出「二叉树、二叉、二、叉、树」）。目的是把倒排索引的入口铺满——用户不管搜“二叉”还是“二叉树”都能命中。

ik_smart ：只做 最粗粒度 切分，不做冗余细分。用户查询“二叉树遍历”只会产生 5 个干净词元，避免「二」「叉」这种碎片词把不相关文档也捞进来。

这就是你代码里的经典组合（ es_search.py:66-78 ）：

```
"ik_max_word_analyzer": { "type": "custom", "tokenizer": "ik_max_word", "filter": ["lowercase"] },  # 索引
"ik_smart_analyzer":    { "type": "custom", "tokenizer": "ik_smart",    "filter": ["lowercase"] },  # 搜索
```

lowercase 过滤器让英文不区分大小写——实测里 LeetCode 变成了 leetcode ，所以用户搜小写 leetcode 也能命中。

IK 的底层是 词典匹配 （主词典 + 停用词词典 + quantifier），不认识的词按字切分，所以「力扣」被切成了「力 | 扣」（词典没收录）——这也是中文分词的固有局限。

## 第三层：查询匹配规则（切完词怎么算命中和排序）
es_search.py:276-372 构建 bool 查询：

**① 命中规则**

- match_phrase ： 短语匹配 ——查询词必须 连续且顺序一致 地出现在字段里。搜“二叉树遍历”，只有标题里真有“二叉树遍历”这串连续文字才命中；
- match (operator or) ： 分词后 OR 匹配 ——切出来的任意一个词元命中就有分，词元越多分越高；
- minimum_should_match: 1 ：7 个子查询至少命中 1 个才算结果。
**② 打分规则（boost 权重表）**

| 子查询       | 字段                    | 权重                           |
| ------------ | ----------------------- | ------------------------------ |
| match_phrase | title                   | 4.0（标题含完整短语 → 最高分） |
| match        | title                   | 3.0                            |
| match_phrase | tags                    | 3.0                            |
| match        | tags                    | 2.0                            |
| match        | category                | 1.5                            |
| match        | description             | 1.0                            |
| match        | content（冗余拼接字段） | 0.5（兜底）                    |

**③ 过滤规则**

```
{"term": {"difficulty": difficulty}}   # 放在 filter 里，只过滤不打分
```

难度筛选用 term 精确匹配（ difficulty 是 keyword），且放在 filter 上下文—— 不参与评分、结果可被 ES 缓存 。

**④ 高亮规则** 

命中字段的片段用 <em>关键词</em> 包裹返回，片段长度 150 字符。

## 一图总结
```
写入: "动态规划入门..." ──ik_max_word──→ [动态]
[规划][划入][入门]... ──→ 倒排索引(铺满入口)
搜索: "二叉树遍历"     ──ik_smart───→ [二叉树]
[遍历]           ──→ 在索引里查这2个词
                                            
                ──→ 短语命中×4 + OR命中×3...打
分排序
                                            
                ──→ 高亮 + top_k 返回
```
想自己试不同句子的切词效果，管理页有现成接口： GET /api/vector/admin/es/test-analyzer?text=你想测的词 