# 算法题目向量检索子服务（know-qrdant）设计与实现总结

> 范围：Spring Boot 直连 Qdrant + Dubbo 3 远程 RPC 的独立向量检索服务
> 代码位置：`AlgoVize/algo-common-api/`（多模块）
> 主服务：`AlgoVize/houduan/`

---

## 一、背景与目标

原有向量检索（面试题）走 **Python 服务**（`Agent/know-retrieval`，端口 8001）+ ChromaDB/ES，主服务用 OpenFeign 调用。本次新建一套**算法题目（OJ 题目）**的向量检索体系，要求：

1. **独立子服务**：`know-qrdant`，主服务通过 **Dubbo 3 远程 RPC** 调用，不占用主服务进程
2. **向量数据库**：本地 **Qdrant**（`D:\software\Qdrant\qdrant-x86_64-pc-windows-msvc`，REST 6333 / gRPC 6334）
3. **向量模型**：本地 `bge-large-zh-v1.5`（1024 维），Java 端 ONNX Runtime 推理
4. **容错**：know-qrdant 未启动 → 主服务核心功能不受影响，仅算法题向量接口返回 404/离线
5. **数据手动同步**：后台「算法题目向量管理」页面点按钮触发，不自动拉取
6. 与面试题那套（Python 512 维）完全隔离、互不影响

---

## 二、总体架构

```
┌────────────────────────── 主服务 houduan（HTTP 80） ──────────────────────────┐
│  · 后台：AlgorithmVectorController /api/algorithm-vector/admin/*（Sa-Token）   │
│  · 前台：OJProblemController /api/problems/semantic-search（公开）              │
│  · AlgorithmVectorService（@DubboReference 直连 + mock 降级）                  │
└──────────────────────────────┬────────────────────────────────────────────────┘
                       Dubbo 3 直连（registry=N/A, url=dubbo://127.0.0.1:20999）
┌──────────────────────────────▼────────────────────────────────────────────────┐
│  独立子服务 know-qrdant（Spring Boot 3.2，HTTP 8090 / Dubbo 20999）              │
│    KnowSearchServiceImpl（@DubboService Provider）                              │
│    ├─ BgeEmbeddingService  ONNX Runtime 推理 bge-large-zh-v1.5（1024 维）       │
│    └─ QdrantRestClient     REST 直连 Qdrant 6333（HNSW + Cosine）               │
└──────────────────────────────┬────────────────────────────────────────────────┘
                        TCP/REST 直连（6333）
              Qdrant 本地进程（algorithm_knowledge 集合）
```

---

## 三、工程结构（Maven 多模块）

```
AlgoVize/algo-common-api/
├── pom.xml                    # 父 POM（Spring Boot 3.2.0 / Dubbo 3.2.20 / onnxruntime）
├── know-api/                  # ① 公共 API 模块（纯接口+DTO，零实现）
│   └── com/algoviz/know/api/
│       ├── KnowSearchService.java          # Dubbo 接口（10 个方法）
│       └── dto/                            # KnowUpsertItem/KnowSearchRequest/
│                                          #   KnowSearchResult/KnowStats/...
└── know-qrdant/               # ② 独立服务（Provider）
    ├── pom.xml
    ├── src/main/java/com/algoviz/know/qrdant/
    │   ├── KnowQrdantApplication.java      # @EnableDubbo
    │   ├── config/                         # QdrantProperties / EmbeddingProperties
    │   ├── embedding/                      # BgeEmbeddingService + WordPieceTokenizer
    │   ├── qdrant/QdrantRestClient.java    # Qdrant REST 客户端（零第三方向量依赖）
    │   ├── service/KnowSearchServiceImpl.java  # @DubboService 实现
    │   └── controller/HealthController.java   # HTTP 探活 /health
    ├── src/main/resources/application.yml
    └── tools/export_onnx.py    # 模型转换脚本（torch → ONNX，自动合并+IR降级）
```

**主服务 houduan 引入**：`know-api`（接口）+ `dubbo-spring-boot-starter`（Consumer），同包放 `KnowSearchServiceMock`（降级）。

---

## 四、关键技术点

### 4.1 Dubbo 3 直连（不注册中心）

| 端 | 配置 |
|---|---|
| know-qrdant | `dubbo.application.name=know-qrdant`、`dubbo.protocol.port=20999`、`dubbo.registry.address=N/A` |
| houduan | `@DubboReference(check=false, timeout=60000, retries=0, mock="true", url="dubbo://127.0.0.1:20999")` |

要点：
- **`@EnableDubbo` 必须加**在两端启动类（否则 `@DubboService`/`@DubboReference` 不生效）
- `check=false`：provider 不在线不影响主服务启动
- `mock="true"` + 同包 `KnowSearchServiceMock`：调用失败自动降级
- **端口必须避开 20880**：Dubbo 3 默认把内部 `MetadataService` 导出到 20880，主服务（纯 Consumer 也会因启用 Dubbo 而占用它）——子服务用 **20999** 隔离
- 容错兜底在主服务 `AlgorithmVectorService`：`ping()` 任何异常返回离线；操作类 `guard()` 统一抛「向量检索服务未启动」→ Controller 返回 404

### 4.2 向量化（bge-large-zh-v1.5 → ONNX）

- 模型源：`C:\Users\Administrator\.cache\huggingface\hub\bge-large-zh-v1.5`（PyTorch 格式，Java 读不了）
- 转换：`tools/export_onnx.py` → 一次性导出 `model.onnx` + `vocab.txt` 到独立目录 `...\hub\java-bge-large-zh-v1.5`
- 三个关键坑（已固化进脚本）：
  1. **torch 新版导出依赖 `onnxscript`**：需 `pip install onnxscript onnx`
  2. **外部数据格式**：torch 导出可能生成 `model.onnx.data`（1.2GB 分文件），Java onnxruntime 加载不稳定 → 用 `onnx` 合并为单文件
  3. **IR version 10**：onnxruntime Java 1.17 最高支持 IR 9 → `m.ir_version=9` 降级后加载成功
- Java 推理：`WordPieceTokenizer`（自实现，读 vocab.txt）+ ONNX Runtime；BGE query 加前缀「为这个句子生成表示以用于检索相关文章：」；mean pooling + L2 归一化 → 1024 维
- 产物约 **1.3GB**（fp32）；运行时内存峰值约 1.3GB

### 4.3 Qdrant 直连

- 用 **JDK HttpClient + Jackson** 走 REST（6333），零第三方 gRPC 依赖
- 集合：`algorithm_knowledge`，`size=1024, distance=Cosine, HNSW(m=16, ef_construct=100)`，启动幂等建集合
- 点 id：`UUID.nameUUIDFromBytes(algorithmId)` 稳定派生 → 重复同步幂等覆盖
- 关键坑：
  - **search 必须带 `with_payload=true`**，否则返回的 payload 为空、拿不到 algorithmId
  - scroll 需 `with_vector=true` 才能返回完整向量（Qdrant 实时检测页展示用）
- 过滤：按 `algorithmId`（包含）、标题 `name`（模糊）、难度 `category`（精确）、标签 `tags`（包含）组合过滤（内存过滤，数据量小可行）

### 4.4 同步 / 检索 / 容错

```
后台页面「全量同步」→ POST /api/algorithm-vector/admin/sync
  → AlgorithmVectorService.startSync()（异步）
    → 读 oj_problem（status=ACTIVE，737 道）→ 只取标题 buildText()
    → upsertBatch（Dubbo）→ know-qrdant 向量化 → 写 Qdrant（16 条/批）
  → 进度轮询 /sync/progress（processed/total/failed/elapsed/estimated）
  → 支持取消 /sync/cancel（取消标志，循环内检查）
```

- **只同步标题**（buildText = title），检索按标题近似匹配
- payload 存：`algorithmId / problemNo / name(标题) / category(难度) / tags / vectorizedText`
- 前台语义搜索：`POST /api/problems/semantic-search`（公开）→ Qdrant 检索 → 按 algorithmId 回查 MySQL → 返回题目+相似度

### 4.5 端到端链路详解

#### 链路 A：前台语义搜索（用户输入 → 返回相近题目）

```
用户 oj-list.html 搜索框输入「动态规划」→ 点🧠语义搜索
  │
  ▼
1. 前端 fetch POST /api/problems/semantic-search   {query:"动态规划", topK:10}
  │
  ▼
2. 主服务 OJProblemController.semanticSearch()
  │  → AlgorithmVectorService.search(query, topK, null)   【Dubbo 直连 20999】
  │
  ▼
3. know-qrdant KnowSearchServiceImpl.search()
  │  ① BgeEmbeddingService.embed(query, isQuery=true)
  │      = 加 BGE 前缀「为这个句子生成表示以用于检索相关文章：动态规划」
  │      → WordPieceTokenizer → ONNX 推理 → mean pooling → L2 归一化 → 1024 维向量
  │  ② QdrantRestClient.search(vec, topK, null)
  │      = POST /collections/algorithm_knowledge/points/search
  │        {vector:[...], limit:10, with_payload:true}   ← with_payload 才能拿到 algorithmId
  │      → 返回 [{id(点UUID), score, payload:{algorithmId, name, category, tags, ...}}]
  │  ③ item.id = payload.algorithmId（题目 id）
  │
  ▼
4. 主服务回查 MySQL：
  │  for each item → ojProblemMapper.findById(algorithmId)
  │  → 组装 {id, problemNo, title, difficulty, tags, description, score}
  │
  ▼
5. 返回 {success:true, problems:[...], total}（按相似度排序）
  │
  ▼
6. 前端 semanticSearch() 渲染表格（标题 + 相似度%绿色标签 + 难度 + 标签）
  │  点击行 → goToProblem(题号) → oj.html?id=题号 进入题目详情
```

#### 链路 B：题目标题向量化（后台手动全量同步 → 写入 Qdrant）

```
后台「算法题目向量管理」→ 点「全量同步」按钮
  │
  ▼
1. POST /api/algorithm-vector/admin/sync
  │
  ▼
2. 主服务 AlgorithmVectorService.startSync()（异步线程，不阻塞页面）
  │  → 读 oj_problem WHERE status='ACTIVE'（737 道）
  │  → 每道题 buildText(p) = p.title（只取标题，与检索粒度一致）
  │  → buildPayload(p) = {algorithmId, problemNo, name, category, tags, vectorizedText}
  │
  ▼
3. 按 16 条/批 → knowSearchService.upsertBatch(batch)   【Dubbo 直连 20999】
  │
  ▼
4. know-qrdant KnowSearchServiceImpl.upsertBatch()
  │  每道题：BgeEmbeddingService.embed(title, isQuery=false)   ← 入库不加大检索前缀
  │         → WordPieceTokenizer → ONNX 推理 → mean pool → L2 归一化 → 1024 维
  │  点 id = UUID.nameUUIDFromBytes(algorithmId)（稳定，重复同步幂等覆盖）
  │  QdrantRestClient.upsert：PUT /collections/algorithm_knowledge/points?wait=true
  │    {points:[{id:稳定UUID, vector:[1024], payload:{...}}]}
  │
  ▼
5. 进度轮询 GET /sync/progress（1s/次，processed/total/failed/elapsed/estimated）
  │  支持取消 POST /sync/cancel（循环内检查取消标志，停止后续入库）
  │
  ▼
6. 完成 → 页面显示「同步完成 成功736/失败0」→ stats.vectorCount=737
```

**两个链路的关系**：链路 B 先把题目标题向量化入库（Qdrant 有 737 条），链路 A 查询时把用户输入向量化后到同一向量空间里做余弦相似度检索——入库与查询用**同一模型/同一归一化**，才能保证语义可比。

---

## 五、前端页面

| 页面 | 路径 | 说明 |
|---|---|---|
| 算法题目向量管理 | 后台 内容管理→算法题目向量管理 | 状态卡/详情/全量同步+取消+清空/进度条 |
| Qdrant 实时检测 | 后台 内容管理→Qdrant实时检测 | 仿 ChromaDB：集合信息+向量列表(组合搜索/分页)+向量数值弹窗 |
| 前台语义搜索 | 前台 oj-list.html | 输入题目名/标签 → 语义搜索 → 相似度列表 |

后台菜单/路由为**写死**（`router/index.ts` + `layout/sider/index.vue`），新增菜单需同步两处；权限走 `rbac-permissions.ts` 的 `content` 组。

---

## 六、主要接口

**后台（/api/algorithm-vector/admin/*，Sa-Token 鉴权）**
- `GET /health` `GET /stats` `GET /collection-info`
- `POST /sync` `GET /sync/progress` `POST /sync/cancel`
- `POST /clear` `GET /vectors`（组合过滤）`POST /search` `DELETE /vectors/{algorithmId}`

**前台（公开）**
- `POST /api/problems/semantic-search`：`{query, topK}` → `{problems:[{id,problemNo,title,difficulty,tags,score}]}`

**子服务探活**
- `GET http://localhost:8090/health`：`{modelReady, qdrantConnected, dim, collection, total}`

---

## 七、部署与启动

1. 启动 Qdrant：`qdrant.exe`（默认 6333/6334）
2. 模型转换（一次性）：`pip install onnxscript onnx` → `python tools\export_onnx.py`（生成单文件 model.onnx，IR9）
3. 启动子服务：`cd know-qrdant && mvn spring-boot:run`（Dubbo 20999 / HTTP 8090，日志出现 `Export dubbo service ... bind.port=20999`）
4. 重启主服务：`cd houduan && mvn spring-boot:run`（HTTP 80）
5. 后台「算法题目向量管理」→ 全量同步 → 状态在线、vectorCount=737

**启动顺序注意**：子服务端口 20999 与主服务 20880（MetadataService 默认）不冲突；残留进程占用端口时先 `taskkill`。

---

## 八、踩坑记录（排障要点）

| 现象 | 根因 | 解法 |
|---|---|---|
| 主服务 health/stats 500 | 缺 `@EnableDubbo`，引用未注入 | 两端启动类加 `@EnableDubbo` |
| 直连 decode 失败 | 主服务误占 20880（MetadataService），consumer 连到自己 | 子服务改 20999 + 主服务删 `dubbo.provider` 段 |
| 模型 `modelReady:false` | ONNX 外部数据分文件 / IR 10 | 合并单文件 + `ir_version=9` |
| 同步全失败 | Dubbo timeout 3s 太短 | timeout 调 60s |
| 语义搜索返回空 | search 未带 `with_payload`，拿不到 algorithmId | 请求加 `with_payload=true` |
| 预计剩余 12 小时 | 基于超时慢速外推 | 运行≥3s 才估算 + 上限 1h |
| 数据源 0 条 | 误用 algorithm 表（空） | 改为 oj_problem（737 道） |
| 端口残留占用 | `mvn spring-boot:run` 停止后 java 子进程残留 | 按端口 kill + scripts/stop.bat |

---

## 九、不足与优化方向

### 9.1 性能
- **`/vectors` 列表全量 scroll + 内存过滤**：737 条可行，数据量上万后内存/耗时线性增长 → 改用 Qdrant `filter` 服务端过滤 + 游标分页
- **ONNX CPU 推理较慢**（单条标题向量化几十~上百 ms）→ 可换 GPU/ONNX 优化算子、或升级 onnxruntime 支持 IR10
- **模型 1.3GB 常驻**：可考虑 fp16 导出（~650MB）或按需加载
- **Dubbo 直连单点**：无注册中心无负载均衡 → 多实例部署需引入 Nacos + 注册中心

### 9.2 功能
- 目前**只同步标题**，语义粒度较粗 → 可按需扩展 `buildText`（标题+标签+难度）并**重建向量**（Qdrant 维度/语义变更需重建集合）
- 缺少**增量同步**（只同步新增/变更题）→ 可基于 Qdrant 已有 algorithmId 集合做差集
- 语义搜索**未做相关性阈值过滤**（低分也会返回）→ 可加 score 阈值/归一化
- 缺后台**单题入库/重算**接口（现仅全量+删除）

### 9.3 可靠性与运维
- know-qrdant 无健康检查/自动拉起 → 建议纳入 bin 启停脚本 + 心跳监控
- Qdrant 数据**无备份策略** → 需定期 snapshot/备份
- 主服务容错靠 try-catch + mock，**缺少降级告警**（建议打日志/落监控）
- `export_onnx.py` 依赖本地 Python 环境 → 后续可 Docker 化或预置模型产物
- Dubbo 直连地址写死 → 抽配置（已抽 `know.qrdant.url`），多环境用 profile 区分

### 9.4 安全
- 前台 `/api/problems/semantic-search` 公开无鉴权/限流 → 建议加频率限制
- Qdrant REST 6333 未鉴权 → 生产环境应加 API key / 内网隔离

---

## 十、关键文件索引

| 作用 | 文件 |
|---|---|
| Dubbo 接口 | `algo-common-api/know-api/.../KnowSearchService.java` |
| 子服务实现 | `algo-common-api/know-qrdant/.../service/KnowSearchServiceImpl.java` |
| Qdrant 客户端 | `algo-common-api/know-qrdant/.../qdrant/QdrantRestClient.java` |
| 模型推理 | `algo-common-api/know-qrdant/.../embedding/BgeEmbeddingService.java` |
| 模型转换 | `algo-common-api/know-qrdant/tools/export_onnx.py` |
| 主服务门面 | `houduan/.../service/AlgorithmVectorService.java` |
| 后台管理接口 | `houduan/.../controller/AlgorithmVectorController.java` |
| 前台语义搜索 | `houduan/.../controller/OJProblemController.java`（semantic-search） |
| 降级实现 | `houduan/.../know/api/KnowSearchServiceMock.java` |
| 前后端页面 | `houtai/src/views/content/AlgorithmVector.vue` / `QdrantMonitor.vue`、`qianduan/pages/oj-list.html` |
