# RAG

客服知识库文档 → 文本分块 → BGE‑M3 生成`dense稠密向量 + sparse稀疏向量` → 存入 Qdrant 用户提问 → 同时做稠密 + 稀疏混合召回 (Hybrid) → 拿到相关知识库片段 → 喂给大模型生成客服回答

## BAAI/bge‑m3 

| 模型                   | dense 维度 | 最大 token | 是否可直接 SentenceTransformer 加载 |
| ---------------------- | ---------- | ---------- | ----------------------------------- |
| BAAI/bge‑small‑zh‑v1.5 | 512        | 512        | ✅可以                               |
| BAAI/bge‑base‑zh‑v1.5  | 768        | 512        | ✅可以                               |
| BAAI/bge‑m3            | 1024       | 8192       | ❌必须 FlagEmbedding                 |

三种输出（M3 = 三合一输出）

1. **dense 稠密向量：1024 维**（普通 RAG 主流用这个）
2. **sparse 稀疏向量：词表维度 250002 维**，只存非零权重，做关键词 BM25 式检索
3. **ColBERT 多向量（token‑level）：每个 token 输出 128 维子向量**，细粒度多向量检索

- **最大 8192 tokens**，中文大约 6000‑7000 汉字，远大于 bge‑v1.5 的 512 token 上限。

| 类型        | 向量形态                         | 存储开销 | 擅长                   | 不擅长           | 数据库支持情况               |
| ----------- | -------------------------------- | -------- | ---------------------- | ---------------- | ---------------------------- |
| dense 稠密  | 1 条 / 文档，1024 维             | 低       | 整体语义、同义改写     | 专有名词、实体   | 全部向量库支持               |
| sparse 稀疏 | 非零词 ID + 权重，词表 250002 维 | 中       | 关键词、实体、专业名词 | 语义同义改写     | Qdrant 支持；Chroma 不支持   |
| ColBERT     | N 条 / 文档，每条 128 维         | 很高     | 局部细粒度片段匹配     | 整体语义；成本高 | 几乎没有原生支持，需自行实现 |

FlagEmbedding 加速安装命令（Windows PowerShell / CMD） 微调时安装

```
pip install -U FlagEmbedding -i https://pypi.tuna.tsinghua.edu.cn/simple
```

```
pip install -U FlagEmbedding -i https://pypi.mirrors.ustc.edu.cn/simple

```

只做推理不需要微调，**不要装 `FlagEmbedding[finetune]`**，会装一堆训练依赖。

HF加速下载

```
$env:HF_ENDPOINT="https://hf-mirror.com"
```

测试代码

```
import os
# 设置huggingface镜像，缓存路径自定义
os.environ["HF_ENDPOINT"] = "https://hf-mirror.com"
os.environ["HUGGINGFACE_HUB_CACHE"] = r"D:\software\Chroma\SentenceTransformer\hub"

from FlagEmbedding import BGEM3FlagModel

# use_fp16=True GPU加速；CPU环境设置 use_fp16=False
model = BGEM3FlagModel('BAAI/bge-m3', use_fp16=False)

text = "测试OJ题目文本"
out = model.encode([text], return_dense=True, return_sparse=False, return_colbert_vecs=False)

dense = out["dense_vecs"][0]
print(f"✅稠密向量维度: {dense.shape[0]}") #输出1024
# dense可以直接存入Chroma，模型输出已经L2归一化

```



| 对比项     | ChromaDB                             | Qdrant                             |
| ---------- | ------------------------------------ | ---------------------------------- |
| 定位       | 原型、Demo、本地调试                 | **生产环境 RAG 首选**              |
| 运行模式   | 嵌入式（直接读磁盘文件） / HTTP 服务 | 独立服务进程，无嵌入模式           |
| GUI        | 无官方 GUI，第三方工具坑多           | ✅内置 Dashboard，开箱即用          |
| 向量维度   | 实践建议≤4096 维                     | 最大**65535 维**                   |
| 规模上限   | 建议≤30 万向量，无集群               | 单机千万级，支持分片集群           |
| 内存优化   | 无量化，索引全量加载内存             | 支持标量 / 乘积量化，大幅省内存    |
| 元数据过滤 | 基础过滤                             | 高性能索引级过滤，复杂条件依然很快 |
| 版本稳定性 | CLI、接口频繁破坏性改动              | Rust 实现，API 稳定，更新规范      |

**更换向量数据库Qdrant  更换维度**  

**FlagEmbedding**的**BAAI/bge‑m3**

**bge‑m3****569M****1024****8192**





文档 → 文本切分 → 向量化 → Chroma入库
用户提问 → 向量化 → Chroma召回相似片段 → 拼接Prompt → 请求LLM回答

原始文档经过文本切分、BAAI/bge-small-zh-v1.5 向量化后存入 Chroma 向量库完成知识库构建。 用户提问先转为向量，在 Chroma 中召回语义相似的文本片段作为参考上下文。 系统将用户问题与检索到的上下文拼接成提示词，交由 LLM 生成贴合知识库内容的回答。

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260814164742.png)

## 系统架构

```
┌──────────────────────────────────────────────────────────────────┐
│                    RAG 智能客服架构                                │
└──────────────────────────────────────────────────────────────────┘

用户提问："怎么提交代码？"
    │
    ▼
┌───────────────────────┐
│  Spring Boot 主后端    │  (8080)
│                       │
│  1. 接收用户问题       │
│  2. OpenFeign → 向量服务│
│  4. 拼接 RAG Prompt    │
│  5. 调用 DeepSeek API  │
│  6. 返回回答 + 引用来源 │
└───────────┬───────────┘
            │ OpenFeign
            ▼
┌───────────────────────┐
│  向量服务 Vectorsvc    │  (8000)
│  FastAPI + uvicorn     │
│                       │
│  1. bge-small-zh 向量化│
│  2. Chroma 语义检索    │
│  3. 返回 TopK 片段     │
└───────────┬───────────┘
            │
            ▼
┌───────────────────────┐
│  Chroma 向量库         │
│  Collection:           │
│    kb_articles         │  ← 知识库文章向量
└───────────────────────┘

数据来源：
┌───────────────────────┐
│  MySQL kb_article 表   │
│  (知识库文章管理)       │
│  - FAQ 问答            │
│  - 使用指南            │
│  - 技术文档            │
│  - 常见问题            │
└───────────────────────┘

RAG 流程：
  提问 → 向量化 → Chroma召回 → 拼接Prompt → DeepSeek → 回答
```

**区别**

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260814170057.png)

## 数据库设计

```
-- 知识库文章表
CREATE TABLE kb_article (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    title           VARCHAR(255) NOT NULL COMMENT '标题',
    category        VARCHAR(50) NOT NULL COMMENT '分类：FAQ/GUIDE/TECH/RULE',
    content         TEXT NOT NULL COMMENT '正文内容（Markdown）',
    tags            VARCHAR(255) COMMENT '标签（逗号分隔）',
    source_type     VARCHAR(20) DEFAULT 'MANUAL' COMMENT '来源：MANUAL/IMPORTED/FEEDBACK',
    source_id       BIGINT COMMENT '来源ID（如反馈ID）',
    status          VARCHAR(20) DEFAULT 'ACTIVE' COMMENT 'ACTIVE/INACTIVE',
    view_count      INT DEFAULT 0 COMMENT '查看次数',
    helpful_count   INT DEFAULT 0 COMMENT '有用次数',
    sort_order      INT DEFAULT 0 COMMENT '排序权重',
    created_by      VARCHAR(50),
    updated_by      VARCHAR(50),
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category_status(category, status),
    INDEX idx_sort(sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库文章';

-- 对话记录表（多轮对话上下文）
CREATE TABLE kb_chat_session (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id      VARCHAR(64) NOT NULL COMMENT '会话ID（UUID）',
    user_id         BIGINT COMMENT '用户ID（可空=游客）',
    role            VARCHAR(20) NOT NULL COMMENT 'user/assistant',
    content         TEXT NOT NULL COMMENT '消息内容',
    retrieved_refs  TEXT COMMENT '检索到的知识库ID列表（JSON）',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_session(session_id),
    INDEX idx_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客服对话记录';

-- 初始知识库数据（示例）
INSERT INTO kb_article (title, category, content, tags) VALUES
('如何提交代码', 'GUIDE', '在OJ页面选择题目后，点击"提交代码"按钮，选择编程语言，粘贴代码后点击提交即可。系统会自动编译运行并返回结果。', 'OJ,提交,代码'),
('支持哪些编程语言', 'FAQ', '目前支持 C/C++、Java、Python3、JavaScript、Go 五种编程语言。', '编程语言,支持'),
('金币有什么用', 'FAQ', '金币可以用于购买商品中心的虚拟商品，如高级算法可视化、专属题目包等。每个用户初始拥有1000金币。', '金币,商品,购买'),
('如何使用算法可视化', 'GUIDE', '在首页选择要学习的算法或数据结构，进入可视化页面后，可以调整动画速度、暂停、回放。支持自定义输入数据。', '可视化,动画,使用');
```

## Python 向量服务 — 新增 RAG 检索接口

## Spring Boot — 知识库管理 CRUD

## RAG 核心 — Prompt 工程与 LLM 调用

## 管理后台 Vue 页面

```
houtai/src/views/
└── knowledge/
    ├── KnowledgeList.vue        # 知识库文章列表（CRUD）
    ├── KnowledgeEdit.vue        # 文章编辑器（Markdown 编辑）
    ├── KnowledgeImport.vue      # 从反馈导入 / 批量导入
    └── ChatHistory.vue          # 查看客服对话记录
```

## 前端用户聊天组件

## OpenFeign 接口扩展