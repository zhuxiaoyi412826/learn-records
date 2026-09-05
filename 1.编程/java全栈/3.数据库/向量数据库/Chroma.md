

# **Chroma**向量化搜索

## 前置知识

用户在 OJ 前端输入自然语言描述的题意或解题思路 → Spring 服务转发请求至向量服务执行语义检索 → 向量库返回相似题目 ID 及对应相似度 → Spring 根据题目 ID 从 MySQL 查询原题详情 → 组装数据后返回前端渲染题目卡片

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260814162049.png)

**目录**

```
algoviz-vector/                    # 新项目目录
├── app/
│   ├── __init__.py
│   ├── main.py                   # FastAPI 入口
│   ├── config.py                 # 配置管理
│   ├── embeddings.py             # bge-small-zh-v1.5 封装
│   ├── vector_store.py           # Chroma 操作封装
│   ├── routers/
│   │   ├── __init__.py
│   │   ├── health.py             # 健康检查
│   │   ├── embedding.py          # 向量化接口
│   │   ├── search.py             # 语义检索接口
│   │   └── sync.py               # 数据同步接口
│   └── schemas/
│       ├── __init__.py
│       └── search.py             # 请求/响应模型
├── data/
│   └── chroma_db/                # Chroma 持久化目录
├── requirements.txt
└── Dockerfile
```

**依赖**

```
fastapi
uvicorn
chromadb
sentence-transformers
numpy
pydantic
```

- fastapi==0.115.0：高性能 Python 异步 Web 接口框架，用来搭建向量检索 / 嵌入 API 服务。
- uvicorn==0.30.6：ASGI 异步 Web 服务器，用来运行 FastAPI 项目。
- chromadb==0.5.5：轻量本地向量数据库，用来存储 embedding 向量并做相似度检索。
- sentence-transformers==3.0.1：文本向量化库，把句子转换成语义向量。
- numpy==1.26.4：高性能数值数组库，支撑向量矩阵运算，是 embedding 依赖基础。
- pydantic==2.9.2：数据校验序列化库，FastAPI 依靠它做接口入参、出参校验

## 环境准备

### 一键安装

 指定清华源加速

```
pip install fastapi uvicorn chromadb sentence-transformers numpy pydantic -i https://pypi.tuna.tsinghua.edu.cn/simple

```

###  chroma

**1.下载安装**

```
pip install chromadb
查看版本 chroma --version
pip show chromadb
```

**2 启动端口 持久化**

```
# 启动服务，端口8000，数据存在 ./chroma_data
chroma run --path ./chroma_data --host 0.0.0.0 --port 8000

后台启动
Start-Process chroma -ArgumentList "run","--path","./chroma_data","--host","0.0.0.0","--port","8000" -WindowStyle Hidden
```

**3 验证**

```
验证：浏览器访问 `http://127.0.0.1:8000/api/v1/heartbeat`，返回 nanosecond heartbeat 即成功
文档地址：[http://127.0.0.1:8000/docs] 可以直接在线调试 API
```

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260814162934.png)

方式 1｜Python 代码心跳检测（推荐，最准）输出一串数字（时间戳= ✅ 服务正常可用 报错连不上 = ❌ 有问题

```
import chromadb
client = chromadb.HttpClient(host="127.0.0.1", port=8000)
print(client.heartbeat())

```

方式 2｜curl 命令测试（Windows 可以用 PowerShell）

```
curl http://127.0.0.1:8000/api/v2/tenants/default_tenant/databases/default_database/collections
```

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260814163025.png)

**4 docker安装**

```
docker run -d -p 8000:8000 -v ./chroma-data:/data chromadb/chroma
```

### **sentence-transformers**

本地 Embedding 模型（不用 OpenAI，离线可用） sentence-transformers 库本身

| 模型                                  | 磁盘缓存大小 | 加载后内存 (CPU) | 说明                        |
| ------------------------------------- | ------------ | ---------------- | --------------------------- |
| all-MiniLM-L6-v2（入门首选）          | ~80MB        | ≈350~400MB       | 轻量、速度快，英文通用      |
| BAAI/bge-small-zh-v1.5（中文推荐）    | ~150MB       | ≈700MB           | 国内 RAG 最常用中文向量模型 |
| paraphrase-multilingual-MiniLM-L12-v2 | ~190MB       | ≈800MB           | 多语言含中文                |
| all-mpnet-base-v2（高精度英文）       | ~420MB       | ≈1.2GB           | 精度更高、更耗资源          |
| LaBSE 跨语言                          | ~1.3GB       | ≈2.5GB           | 跨语言检索                  |

选用模型 BAAI/bge-small-zh-v1.5

| 模型              | 参数量 | 磁盘大小   |
| ----------------- | ------ | ---------- |
| bge‑small‑zh‑v1.5 | 24M    | **95.8MB** |
| bge‑base‑zh‑v1.5  | 102M   | 409MB      |
| bge‑large‑zh‑v1.5 | 326M   | 1.3GB      |

**切换不同的模型**

```
# model = SentenceTransformer("BAAI/bge-small-zh-v1.5")   24
# model = SentenceTransformer("BAAI/bge-base-zh-v1.5")     102
model = SentenceTransformer("BAAI/bge-large-zh-v1.5")           326
```

**下载**

```
# 1. pip重装（清华源，解决torch/sentence-transformers慢）
pip install sentence-transformers chromadb -i https://pypi.tuna.tsinghua.edu.cn/simple
# 2. 设置HF镜像（hf-mirror，后续自动加速BAAI/bge-small-zh-v1.5）
$env:HF_ENDPOINT="https://hf-mirror.com"
# 3. 直接跑验证脚本
python test_bge.py
```

```
# test_bge.py
$env:HF_ENDPOINT="https://hf-mirror.com"
import os
# 配置hf镜像加速
os.environ["HF_ENDPOINT"] = "https://hf-mirror.com"

from sentence_transformers import SentenceTransformer

if __name__ == "__main__":
    # 加载BAAI/bge-small-zh-v1.5
    print("🔄 正在加载 BAAI/bge-small-zh-v1.5 ...")
    model = SentenceTransformer("BAAI/bge-small-zh-v1.5")
    print("✅ 模型加载完成")

    # 测试文本
    text = "RAG检索增强生成原理"
    emb = model.encode(text)

    print(f"📐 向量维度: {emb.shape}")
    print(f"🔢 向量前6位: {emb[:6].round(4)}")
    print("\n🎉 BGE模型验证成功！")
```

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260814182834.png)

**查看版本**

```
pip show sentence-transformers
```

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260814182357.png)

**下载自定义模型和安装位置**

```
from sentence_transformers import SentenceTransformer

# 会自动去HF下载 all-mpnet-base-v2，缓存到本地
model = SentenceTransformer('all-mpnet-base-v2')

# 测试向量化
emb = model.encode("测试文本")
print(emb.shape)

```

默认安装位置 C:\Users\你的用户名\.cache\huggingface\hub 默认目录



### 验证是否安装成功

使用pip 命令

```
pip show fastapi
pip show uvicorn
pip show chromadb
pip show sentence-transformers
pip show numpy
pip show pydantic

```

check_env.py

```
import sys

def check_package(name, import_name=None):
    if import_name is None:
        import_name = name
    try:
        mod = __import__(import_name)
        ver = getattr(mod, "__version__", "未知版本")
        print(f"✅ {name:<25} 已安装 | 版本: {ver}")
        return True
    except ImportError:
        print(f"❌ {name:<25} 未安装 / 导入失败")
        return False

print(f"Python版本: {sys.version}\n")

check_package("fastapi")
check_package("uvicorn")
check_package("chromadb")
check_package("sentence_transformers", import_name="sentence_transformers")
check_package("numpy")
check_package("pydantic")

```

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260814183436.png)

## 编写 FastAPI + Chroma 向量服务

2.1 配置管理 config.py

2.2 嵌入模型封装 embeddings.py

2.3 Chroma 存储封装 vector_store.py

2.4 搜索请求/响应模型

| 接口     | 方法   | 路径                       | 功能                |
| -------- | ------ | -------------------------- | ------------------- |
| 健康检查 | GET    | `/health`                  | 服务状态、模型状态  |
| 单题入库 | POST   | `/api/v1/embedding/single` | 同步单题到向量库    |
| 批量入库 | POST   | `/api/v1/embedding/batch`  | 批量同步所有题      |
| 语义检索 | POST   | `/api/v1/search`           | 语义搜索返回题目 ID |
| 删除向量 | DELETE | `/api/v1/embedding/{id}`   | 删除指定题目的向量  |

2.5 探索请求和响应

```
# 请求
class SearchRequest(BaseModel):
    query: str          # 搜索文本
    top_k: int = 10     # 返回数量
    threshold: float = 0.3  # 相似度阈值

# 响应
class SearchResult(BaseModel):
    problem_id: int         # MySQL 题目 ID
    problem_no: str         # 题目编号
    similarity: float       # 相似度分数
    title: str              # 题目标题（可选，便于调试）
```

启动

```
python main.py
http://127.0.0.1:8000/docs
```

## SpringBoot 调用 FastAPI 向量服务

### RestTemplate

**依赖 pom.xml**

```
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

**配置 RestTemplate Bean**

```
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
```

**封装 DTO**

```
import lombok.Data;
import java.util.Map;

// 新增文档请求
@Data
public class AddDocReq {
    private String doc_id;
    private String content;
    private Map<String,Object> metadata;
}

// 检索请求
@Data
public class QueryReq {
    private String query_text;
    private Integer top_k = 3;
}
```

**向量服务 Client**

```
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Service
@RequiredArgsConstructor
public class ChromaVectorClient {
    private final RestTemplate restTemplate;
    // FastAPI地址
    private static final String BASE_URL = "http://127.0.0.1:8000";

    // 新增文本向量化入库
    public void addDoc(String docId, String content){
        AddDocReq req = new AddDocReq();
        req.setDoc_id(docId);
        req.setContent(content);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AddDocReq> entity = new HttpEntity<>(req, headers);

        restTemplate.postForObject(BASE_URL + "/vector/add", entity, Object.class);
    }

    // 相似度检索
    public Object search(String question, int topK){
        QueryReq req = new QueryReq();
        req.setQuery_text(question);
        req.setTop_k(topK);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<QueryReq> entity = new HttpEntity<>(req, headers);

        ResponseEntity<Object> resp = restTemplate.postForEntity(BASE_URL + "/vector/query", entity, Object.class);
        return resp.getBody();
    }
}
```

**测试 Controller**

```
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rag")
@RequiredArgsConstructor
public class RagController {
    private final ChromaVectorClient vectorClient;

    @GetMapping("/add")
    public String add(@RequestParam String id, @RequestParam String text){
        vectorClient.addDoc(id, text);
        return "ok";
    }

    @GetMapping("/search")
    public Object search(@RequestParam String q){
        return vectorClient.search(q,3);
    }
}
```

### OpenFeign

**添加依赖 pom.xml**

```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
    <!-- 注意版本和SpringBoot对齐 -->
</dependency>
```

**启动类加注解 `@EnableFeignClients`**

```
@SpringBootApplication
@EnableFeignClients
public class Application { ... }
```

**Feign 客户端 VectorSearchClient.java**

```
@FeignClient(name = "vector-service", url = "${vector.service.url}")
public interface VectorSearchClient {
    
    @PostMapping("/api/v1/search")
    SearchResponse search(@RequestBody SearchRequest request);
    
    @PostMapping("/api/v1/embedding/single")
    EmbeddingResponse embed(@RequestBody EmbeddingRequest request);
    
    @PostMapping("/api/v1/embedding/batch")
    BatchEmbeddingResponse batchEmbed(@RequestBody BatchEmbeddingRequest request);
    
    @DeleteMapping("/api/v1/embedding/{id}")
    void deleteEmbedding(@PathVariable("id") Long id);
}
```

**配置文件** 

```
vector:
  service:
    url: http://localhost:8000
    timeout: 30s
```



------

**测试流程**

1. 启动 `python main.py` → FastAPI:8000
2. 启动 SpringBoot
3. 先写入知识库：

```
GET http://localhost:8080/rag/add?id=doc001&text=SpringBoot是Java后端框架
GET http://localhost:8080/rag/add?id=doc002&text=FastAPI是Python高性能web框架
```

```
GET http://localhost:8080/rag/search?q=java后端框架
```

返回会命中 doc001，距离最小。

## 向量文本构造策略



```
# 文本拼接策略：title + tags + description
# 权重：title > tags > description

def construct_embedding_text(problem):
    """
    将题目转换为适合向量化的文本
    """
    parts = []
    
    # 1. 标题（权重最高）
    if problem.title:
        parts.append(f"题目：{problem.title}")
    
    # 2. 标签
    if problem.tags:
        parts.append(f"标签：{problem.tags}")
    
    # 3. 分类
    if problem.category:
        parts.append(f"分类：{problem.category}")
    
    # 4. 描述（截断到前 500 字）
    if problem.description:
        desc = problem.description[:500]
        parts.append(f"描述：{desc}")
    
    # 5. 难度
    if problem.difficulty:
        parts.append(f"难度：{problem.difficulty}")
    
    return "\n".join(parts)
```

```
输入：
{
  "title": "实现一个LRU缓存",
  "tags": "哈希表,双向链表,设计",
  "category": "数据结构",
  "difficulty": "medium",
  "description": "请你设计并实现一个满足 LRU (最近最少使用) ..."
}

输出：
题目：实现一个LRU缓存
标签：哈希表,双向链表,设计
分类：数据结构
难度：medium
描述：请你设计并实现一个满足 LRU (最近最少使用) ...
```

## 数据同步策略

全量

同步 

定时

## 语义搜索集成

搜索接口改造

```
POST /api/interview/search

请求：
{
  "keyword": "动态规划",
  "mode": "semantic",    // semantic（语义）| hybrid（混合）| keyword（关键词）
  "top_k": 10
}

流程（semantic 模式）：
1. 调用 OpenFeign → FastAPI /api/v1/search
2. FastAPI: 文本向量化 → Chroma 检索 → 返回 [problem_id, similarity]
3. Spring Boot: 根据 ID 列表查询 MySQL
4. 前端：展示题目列表 + 相似度标签

流程（hybrid 模式，推荐默认）：
1. 向量检索（权重 0.7）
2. MySQL LIKE 关键词检索（权重 0.3）
3. 融合排序 → 返回结果

降级处理：
- 向量服务不可用 → 自动降级到关键词检索
- 相似度 < 阈值 → 提示"未找到相关题目"
```

**结果融合算法**

```
def hybrid_search(keyword, top_k=10):
    # 1. 语义检索
    vector_results = vector_search(keyword, top_k * 2)
    
    # 2. 关键词检索
    keyword_results = mysql_keyword_search(keyword, top_k * 2)
    
    # 3. 融合：加权排序
    scored_items = {}
    for r in vector_results:
        scored_items[r.id] = r.similarity * 0.7
    for r in keyword_results:
        if r.id in scored_items:
            scored_items[r.id] += r.score * 0.3
        else:
            scored_items[r.id] = r.score * 0.3
    
    # 4. 排序取 TopK
    sorted_items = sorted(scored_items.items(), key=lambda x: x[1], reverse=True)
    return sorted_items[:top_k]
```

## 部署与运维

**性能监控指标**

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260814165022.png)



## 增强生成效果

| 组件                         | 类型             | 能力                                 | 局限                                   |
| ---------------------------- | ---------------- | ------------------------------------ | -------------------------------------- |
| Embedding(bge‑small‑zh‑v1.5) | 双塔编码器模型   | 语义同义匹配，用户换说法也能搜到     | 专有名词、数字容易漂移；粗召回精度有限 |
| BM25                         | 统计算法，无模型 | 关键词、专有名词、数字、字面匹配     | 转述、同义改写搜不到                   |
| Reranker(bge‑reranker)       | 交叉编码器模型   | 对候选做深度语义精细打分，提升准确率 | 速度慢，只能小批量，不能全库检索       |

# Chroma RAG

## 前置知识

### LLM 自我纠错循环

```
普通 RAG（单次）：
  检索 → 生成 → 返回

LLM 内循环 RAG（迭代纠错）：
  检索 → 生成 → 自我评估 → 不达标？
         ↑                    ↓ 是
         └── 重检索 + 批评反馈 ←┘
                              ↓ 否
                            返回最终答案
```

### 3种循环对比

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260814170909.png)

### 循环流程图

```
用户提问："怎么参加竞赛"
     │
     ▼
┌─────────────────────────────────────────────────────┐
│ 第 1 轮                                              │
│                                                     │
│  1. 检索: "怎么参加竞赛" → Chroma → Top3 片段        │
│  2. 生成: SystemPrompt + 片段 → DeepSeek → 回答A    │
│  3. 规则校验: 长度OK? 无幻觉? → PASS                │
│  4. LLM 自评: score=0.5, passed=false              │
│     critique: "回答未提及具体报名步骤"               │
│                                                     │
│  → 未通过，继续                                      │
└──────────────────────┬──────────────────────────────┘
                       ▼
┌─────────────────────────────────────────────────────┐
│ 第 2 轮                                              │
│                                                     │
│  1. Query 重写: "怎么参加竞赛" → "竞赛报名流程步骤"  │
│  2. 重新检索: "竞赛报名流程步骤" → Top3 新片段       │
│  3. 生成: SystemPrompt + 新片段 + 批评 → 回答B      │
│  4. 规则校验: PASS                                   │
│  5. LLM 自评: score=0.85, passed=true               │
│                                                     │
│  → 通过，退出循环 ✅                                 │
└──────────────────────┬──────────────────────────────┘
                       ▼
              返回回答B + score=0.85 + 2轮迭代详情
              总耗时: ~6s (2次检索 + 4次LLM调用)
```



## 实现

### 循环配置

```
/**
 * RAG 循环配置
 */
@Data
public class RagLoopConfig {
    
    /** 最大迭代次数（默认 3） */
    private int maxIterations = 3;
    
    /** 自评分数阈值（0~1，默认 0.7） */
    private double confidenceThreshold = 0.7;
    
    /** 最小答案长度（字符数） */
    private int minAnswerLength = 20;
    
    /** 最大答案长度（字符数，防止啰嗦） */
    private int maxAnswerLength = 2000;
    
    /** 是否启用自评（关闭则只做规则校验） */
    private boolean enableSelfEvaluation = true;
    
    /** 是否启用检索纠正（query 重写） */
    private boolean enableRetrievalCorrection = true;
    
    /** 检索相似度阈值（低于此值触发重检索） */
    private double retrievalThreshold = 0.35;
    
    /** 每轮最大 Token 数（控制成本） */
    private int maxTokens = 2048;
}
```

### 模板角色

```
public class RagLoopPrompts {

    // ==========================================
    // 角色 1：生成器 Prompt（带检索上下文）
    // ==========================================
    public static String buildGeneratorPrompt(List<KBRetrieveResult> chunks, 
                                               String query, 
                                               String critique,         // 上一轮的批评（可空）
                                               int iteration) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是 AlgoViz 算法学习平台的智能客服。\n");
        sb.append("请根据以下知识库内容回答用户问题。\n\n");
        sb.append("【规则】\n");
        sb.append("1. 只基于知识库内容回答，不要编造\n");
        sb.append("2. 回答简洁清晰，使用 Markdown\n");
        sb.append("3. 如果知识库不足以回答，明确告知\n\n");
        sb.append("【知识库内容】\n");
        
        for (int i = 0; i < chunks.size(); i++) {
            sb.append(String.format("文档%d [%s] %s\n%s\n\n",
                i + 1, chunks.get(i).getCategory(),
                chunks.get(i).getTitle(), chunks.get(i).getContent()));
        }
        
        // 如果有上一轮的批评，加入改进指令
        if (critique != null && !critique.isEmpty()) {
            sb.append("【上一轮回答的问题】\n");
            sb.append(critique).append("\n\n");
            sb.append("请针对以上问题改进你的回答（这是第 ").append(iteration).append(" 次尝试）。\n");
        }
        
        return sb.toString();
    }

    // ==========================================
    // 角色 2：评估器 Prompt（自评 + 批评）
    // ==========================================
    public static String buildEvaluatorPrompt(String query, 
                                               String answer, 
                                               List<KBRetrieveResult> chunks) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是回答质量评估器。请评估以下客服回答的质量。\n\n");
        sb.append("【用户问题】\n").append(query).append("\n\n");
        sb.append("【知识库可用内容】\n");
        for (KBRetrieveResult c : chunks) {
            sb.append("- ").append(c.getTitle()).append("\n");
        }
        sb.append("\n【待评估的回答】\n").append(answer).append("\n\n");
        sb.append("【评估标准】\n");
        sb.append("1. 准确性：回答是否基于知识库内容，有无编造\n");
        sb.append("2. 相关性：回答是否切中用户问题\n");
        sb.append("3. 完整性：回答是否完整解答了用户问题\n");
        sb.append("4. 清晰度：回答是否简洁易懂\n\n");
        sb.append("【输出格式】严格按以下 JSON 输出，不要输出其他内容：\n");
        sb.append("```\n");
        sb.append("{\"score\": 0.8, \"passed\": true, \"critique\": \"具体问题描述\"}\n");
        sb.append("```\n");
        sb.append("score: 0~1 分（>= 0.7 为通过）\n");
        sb.append("passed: true/false\n");
        sb.append("critique: 如果未通过，说明具体问题（用于下一轮改进）；通过则为空字符串\n");
        
        return sb.toString();
    }

    // ==========================================
    // 角色 3：Query 重写器 Prompt（检索纠正）
    // ==========================================
    public static String buildQueryRewriterPrompt(String originalQuery, 
                                                   String previousAnswer,
                                                   List<KBRetrieveResult> previousChunks) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是搜索查询优化器。用户的问题检索到的知识库内容不够相关，请重写搜索查询。\n\n");
        sb.append("【原始问题】\n").append(originalQuery).append("\n\n");
        sb.append("【上一轮检索到的文档】\n");
        if (previousChunks.isEmpty()) {
            sb.append("（无结果）\n\n");
        } else {
            for (KBRetrieveResult c : previousChunks) {
                sb.append("- ").append(c.getTitle())
                  .append(" (相似度:").append(String.format("%.2f", c.getSimilarity())).append(")\n");
            }
            sb.append("\n");
        }
        sb.append("【上一轮回答】\n").append(previousAnswer).append("\n\n");
        sb.append("请重写一个更精准的搜索查询（只输出查询文本，不要解释）：\n");
        
        return sb.toString();
    }
}
```

### **循环引擎**

```
@Service
@Slf4j
public class RagLoopEngine {

    @Autowired
    private AIChatService aiChatService;
    
    @Autowired
    private VectorSearchClient vectorSearchClient;

    /**
     * RAG 内循环核心方法
     * 
     * 流程：
     *   第 1 轮：检索 → 生成 → 评估
     *   第 2 轮：若未通过 → 重写 query → 重新检索 → 带批评重新生成 → 评估
     *   第 3 轮：若仍未通过 → 带累积批评再生成 → 评估
     *   达到阈值或最大轮数 → 返回
     */
    public RagLoopResult chatWithLoop(String query, 
                                       String sessionId,
                                       List<ChatHistory> history,
                                       RagLoopConfig config) {
        
        long startTime = System.currentTimeMillis();
        List<RagLoopIteration> iterations = new ArrayList<>();
        
        // ==============================
        // 初始检索
        // ==============================
        String searchQuery = query;
        List<KBRetrieveResult> chunks = retrieve(searchQuery, config);
        
        String accumulatedCritique = null;  // 累积批评
        String bestAnswer = null;
        double bestScore = 0;
        
        for (int i = 1; i <= config.getMaxIterations(); i++) {
            log.info("[RAG Loop] 会话{} 第{}轮迭代开始", sessionId, i);
            
            // ==============================
            // 步骤 1：生成回答
            // ==============================
            String systemPrompt = RagLoopPrompts.buildGeneratorPrompt(
                chunks, query, accumulatedCritique, i);
            
            List<ChatRequest.Message> messages = buildMessages(
                systemPrompt, history, query);
            
            String answer = callLLM(messages, config);
            
            // ==============================
            // 步骤 2：规则校验（快速失败）
            // ==============================
            RuleCheckResult ruleResult = ruleCheck(answer, config);
            
            // ==============================
            // 步骤 3：LLM 自评（如果规则通过）
            // ==============================
            EvaluationResult evalResult;
            if (ruleResult.passed() && config.isEnableSelfEvaluation()) {
                evalResult = selfEvaluate(query, answer, chunks, config);
            } else {
                // 规则校验失败，直接构造不通过的评估
                evalResult = EvaluationResult.fail(ruleResult.reason());
            }
            
            // 记录本轮迭代
            iterations.add(new RagLoopIteration(
                i, searchQuery, chunks.size(), answer, 
                evalResult.getScore(), evalResult.isPassed(),
                evalResult.getCritique()
            ));
            
            log.info("[RAG Loop] 第{}轮: score={}, passed={}, critique={}",
                i, evalResult.getScore(), evalResult.isPassed(),
                evalResult.getCritique());
            
            // 记录最佳答案
            if (evalResult.getScore() > bestScore) {
                bestScore = evalResult.getScore();
                bestAnswer = answer;
            }
            
            // ==============================
            // 步骤 4：检查退出条件
            // ==============================
            if (evalResult.isPassed()) {
                log.info("[RAG Loop] 第{}轮评估通过，退出循环", i);
                break;
            }
            
            // ==============================
            // 步骤 5：未通过 → 准备下一轮
            // ==============================
            if (i < config.getMaxIterations()) {
                // 累积批评（用于下一轮生成器改进）
                accumulatedCritique = evalResult.getCritique();
                
                // 检索纠正：重写 query 重新检索
                if (config.isEnableRetrievalCorrection()) {
                    String rewrittenQuery = rewriteQuery(query, answer, chunks, config);
                    log.info("[RAG Loop] Query 重写: '{}' → '{}'", searchQuery, rewrittenQuery);
                    searchQuery = rewrittenQuery;
                    chunks = retrieve(searchQuery, config);
                }
            }
        }
        
        // ==============================
        // 返回最终结果
        // ==============================
        long totalTime = System.currentTimeMillis() - startTime;
        
        return RagLoopResult.builder()
            .answer(bestAnswer)
            .sessionId(sessionId)
            .iterations(iterations)
            .totalIterations(iterations.size())
            .finalScore(bestScore)
            .passed(bestScore >= config.getConfidenceThreshold())
            .references(buildReferences(chunks))
            .latencyMs(totalTime)
            .build();
    }

    // ==========================================
    // 内部方法
    // ==========================================

    /** 调用 LLM 生成回答 */
    private String callLLM(List<ChatRequest.Message> messages, RagLoopConfig config) {
        try {
            ChatRequest chatRequest = new ChatRequest(messages);
            HttpURLConnection conn = aiChatService.chat(chatRequest, false);
            return readResponse(conn);
        } catch (Exception e) {
            log.error("LLM 调用失败", e);
            return "抱歉，生成回答时出现错误。";
        }
    }

    /** LLM 自评 */
    private EvaluationResult selfEvaluate(String query, String answer, 
                                           List<KBRetrieveResult> chunks,
                                           RagLoopConfig config) {
        try {
            String evalPrompt = RagLoopPrompts.buildEvaluatorPrompt(query, answer, chunks);
            
            ChatRequest chatRequest = new ChatRequest(List.of(
                new ChatRequest.Message("system", "你是回答质量评估器，只输出JSON。"),
                new ChatRequest.Message("user", evalPrompt)
            ));
            
            HttpURLConnection conn = aiChatService.chat(chatRequest, false);
            String response = readResponse(conn);
            
            // 解析 JSON 响应
            return parseEvaluation(response);
        } catch (Exception e) {
            log.warn("自评失败，默认通过: {}", e.getMessage());
            return EvaluationResult.pass(0.75, "");  // 降级：默认通过
        }
    }

    /** Query 重写 */
    private String rewriteQuery(String originalQuery, String previousAnswer,
                                 List<KBRetrieveResult> previousChunks,
                                 RagLoopConfig config) {
        try {
            String rewritePrompt = RagLoopPrompts.buildQueryRewriterPrompt(
                originalQuery, previousAnswer, previousChunks);
            
            ChatRequest chatRequest = new ChatRequest(List.of(
                new ChatRequest.Message("system", "你是搜索查询优化器，只输出查询文本。"),
                new ChatRequest.Message("user", rewritePrompt)
            ));
            
            HttpURLConnection conn = aiChatService.chat(chatRequest, false);
            return readResponse(conn).trim();
        } catch (Exception e) {
            log.warn("Query 重写失败，使用原始 query: {}", e.getMessage());
            return originalQuery;  // 降级：用原始 query
        }
    }

    /** 规则校验（快速失败，不消耗 API 额度） */
    private RuleCheckResult ruleCheck(String answer, RagLoopConfig config) {
        if (answer == null || answer.isBlank()) {
            return RuleCheckResult.fail("回答为空");
        }
        if (answer.length() < config.getMinAnswerLength()) {
            return RuleCheckResult.fail("回答过短（" + answer.length() + " 字）");
        }
        if (answer.length() > config.getMaxAnswerLength()) {
            return RuleCheckResult.fail("回答过长（" + answer.length() + " 字）");
        }
        // 幻觉标记检测
        if (answer.contains("我不确定") && answer.contains("可能")) {
            return RuleCheckResult.fail("回答含有不确定标记，可能存在幻觉");
        }
        return RuleCheckResult.pass();
    }

    /** 向量检索 */
    private List<KBRetrieveResult> retrieve(String query, RagLoopConfig config) {
        try {
            KBRetrieveRequest req = new KBRetrieveRequest();
            req.setQuery(query);
            req.setTopK(3);
            req.setThreshold((float) config.getRetrievalThreshold());
            return vectorSearchClient.retrieveKb(req).getResults();
        } catch (Exception e) {
            log.warn("向量检索失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /** 解析 LLM 自评 JSON */
    private EvaluationResult parseEvaluation(String response) {
        try {
            // 提取 JSON 部分（LLM 可能包裹在 markdown 代码块中）
            String json = extractJson(response);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);
            
            double score = node.path("score").asDouble(0.5);
            boolean passed = node.path("passed").asBoolean(score >= 0.7);
            String critique = node.path("critique").asText("");
            
            return new EvaluationResult(score, passed, critique);
        } catch (Exception e) {
            log.warn("自评 JSON 解析失败，默认通过: {}", e.getMessage());
            return EvaluationResult.pass(0.75, "");
        }
    }

    /** 从 LLM 响应中提取 JSON */
    private String extractJson(String response) {
        // 尝试从 ```json ... ``` 中提取
        int start = response.indexOf("```");
        if (start >= 0) {
            int jsonStart = response.indexOf("\n", start) + 1;
            int end = response.indexOf("```", jsonStart);
            if (end > jsonStart) {
                return response.substring(jsonStart, end).trim();
            }
        }
        // 尝试直接解析
        int braceStart = response.indexOf("{");
        int braceEnd = response.lastIndexOf("}");
        if (braceStart >= 0 && braceEnd > braceStart) {
            return response.substring(braceStart, braceEnd + 1);
        }
        return response;
    }

    /** 读取 LLM 响应 */
    private String readResponse(HttpURLConnection connection) throws Exception {
        int code = connection.getResponseCode();
        if (code < 200 || code >= 300) {
            throw new RuntimeException("LLM API 错误: " + code);
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        // 解析 DeepSeek 响应格式
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(sb.toString());
        return root.path("choices").path(0).path("message").path("content").asText("");
    }

    /** 构建消息列表 */
    private List<ChatRequest.Message> buildMessages(String systemPrompt,
                                                      List<ChatHistory> history,
                                                      String query) {
        List<ChatRequest.Message> messages = new ArrayList<>();
        messages.add(new ChatRequest.Message("system", systemPrompt));
        for (ChatHistory h : history) {
            messages.add(new ChatRequest.Message(h.getRole(), h.getContent()));
        }
        messages.add(new ChatRequest.Message("user", query));
        return messages;
    }
}
```

### 返回结果模型

```
@Data
@Builder
public class RagLoopResult {
    private String answer;                    // 最终回答
    private String sessionId;
    private List<RagLoopIteration> iterations; // 每轮迭代详情
    private int totalIterations;               // 总迭代次数
    private double finalScore;                 // 最终评分
    private boolean passed;                    // 是否通过评估
    private List<Reference> references;        // 引用来源
    private long latencyMs;                    // 总耗时
}

@Data
@AllArgsConstructor
public class RagLoopIteration {
    private int round;                 // 第几轮
    private String searchQuery;        // 本轮使用的检索 query
    private int retrievedCount;        // 检索到的片段数
    private String answer;             // 本轮生成的回答
    private double score;              // 自评分数
    private boolean passed;            // 是否通过
    private String critique;           // 批评内容
}
```

### Controll

```
@RestController
@RequestMapping("/api/rag")
@Tag(name = "智能客服", description = "RAG 检索增强生成")
public class RagChatController {

    @Autowired
    private RagLoopEngine ragLoopEngine;

    @PostMapping("/chat")
    @Operation(summary = "智能客服对话（内循环）", 
               description = "LLM 自动迭代优化回答，直到评估通过或达到最大轮数")
    public RagLoopResult chat(@RequestBody RagChatRequest request) {
        if (request.getSessionId() == null) {
            request.setSessionId(UUID.randomUUID().toString());
        }
        
        // 可自定义循环配置
        RagLoopConfig config = new RagLoopConfig();
        config.setMaxIterations(request.getMaxIterations() != null 
            ? request.getMaxIterations() : 3);
        config.setConfidenceThreshold(request.getThreshold() != null 
            ? request.getThreshold() : 0.7);
        
        List<ChatHistory> history = getHistory(request.getSessionId());
        
        return ragLoopEngine.chatWithLoop(
            request.getQuery(), request.getSessionId(), history, config);
    }
}
```




