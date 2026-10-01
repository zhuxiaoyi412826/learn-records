## 1. Agent 开发与使用

- 核心组成：大模型大脑 + 规划能力 + 工具调用 + 记忆模块
- 典型框架：LangChain / LlamaIndex / Spring AI Agent
- 核心能力：多轮对话、工具调用（API / 数据库 / 搜索引擎）、任务拆解、自主规划
- 落地场景：智能客服、代码助手、数据分析 Agent、工作流自动化
- 什么是 Agent：大模型 + 工具调用 + 记忆 + 规划
- Agent 的核心能力：
  - 工具调用（Function Calling）
  - 任务规划（Planning）
  - 记忆（Memory）
  - 反思（Reflection）
- **函数调用**什么是 Function Calling：让大模型调用你提供的工具Spring AI 函数调用：`@Description` 注解定义工具工具注册与调用流程**动手实践**：给 AI 加几个工具（查天气、查数据库、调用计算器）
- 多 Agent 协作：多个 Agent 分工合作

## 2. AIGC 多模态生成

- 文本生成：文案写作、摘要提取、代码生成、结构化输出
- 图片生成：Stable Diffusion / 文心一格 / DALL・E 调用、参数调优
- 视频生成：文字生成视频、数字人播报、视频脚本生成
- 音频生成：语音合成（TTS）、语音识别（ASR）、声音克隆
- 工程落地：API 调用封装、流式响应、内容审核、成本优化

## 3. 向量数据库

- 核心概念：向量嵌入（Embedding）、相似度检索、向量索引
- 主流产品：Milvus、Pinecone、Chroma、Redis Vector
- 核心操作：向量写入、相似度查询、元数据过滤、索引构建
- 应用场景：语义检索、推荐系统、图像检索、RAG 知识库

## 4. RAG 检索增强生成

- 完整流程：文档加载 → 文本分块 → 向量化 → 向量存储 → 检索召回 → 大模型生成
- 关键优化：分块策略、召回率优化、重排序（Rerank）、多轮对话上下文
- 工程落地：知识库管理、增量更新、引用溯源、问答效果评估
- 常见框架：LangChain RAG、LlamaIndex、Spring AI RAG 实现

## 5. 大模型运作原理

- 数据处理流程：原始文本 → 清洗 → 分词（Tokenizer）→ 词表构建
- 表示层：词嵌入（Embedding）、位置编码、语义向量空间
- 核心架构：Transformer 结构、自注意力机制（Self-Attention）、多头注意力
- 训练过程：预训练（语言建模）→ 监督微调（SFT）→ 人类反馈强化学习（RLHF）
- 推理过程：输入编码 → 注意力计算 → 逐词生成 → 概率采样
- 核心概念：上下文窗口、幻觉、温度系数、Top-P/Top-K 采样

## 6.RAG智能客服实战平台

做一个完整的 AI 应用项目，包含以下功能：

- [ ] 智能对话：流式对话、多轮对话、对话历史
- [ ] 图片生成：文生图、图片保存、我的作品
- [ ] 图片理解：上传图片提问
- [ ] 视频生成：文生视频、异步任务、状态查询
- [ ] 知识库问答：
  - [ ] 文档上传（PDF/Word/Markdown）
  - [ ] 文档解析与向量化
  - [ ] 语义检索问答
  - [ ] 引用溯源
- [ ] 我的助手：创建自定义 AI 助手（设定人设、提示词）
- [ ] 用户系统：注册登录、积分/额度管理
- [ ] 管理后台：用户管理、模型配置、知识库管理

# SpringAI

### 大模型 API 接入

### 一、必须掌握（核心基础，开发天天用）

#### 1. Spring AI 核心概念与模型接入

- **Spring AI 是什么**：Spring 生态的 AI 开发框架，统一各种大模型 API

- **核心抽象**：`ChatModel`（聊天模型）、`Prompt`（提示词）、`ChatResponse`（响应）

- 接入主流大模型

  （国内推荐优先学）：

  - 通义千问（阿里云）
  - 豆包（字节火山）
  - DeepSeek
  - OpenAI（国外）

- 依赖引入 + 配置文件（API Key、模型名称、Base URL）

- 最简单的调用：`chatModel.call("你好")` 一句话调用

#### 2. Prompt 模板与结构化提示

- `PromptTemplate` 提示词模板

  ：动态填充变量

  Java

  ```java
  PromptTemplate template = new PromptTemplate("你是一个{role}，请回答：{question}");
  Prompt prompt = template.create(Map.of("role", "Java 专家", "question", "什么是多线程"));
  ```

- **System Prompt**：设定 AI 人设、角色、输出规范

- **消息列表**：`SystemMessage` + `UserMessage` + `AssistantMessage` 构造多轮对话

- **提示词设计基础**：清晰指令、角色设定、示例引导（Few-shot）

#### 3. 输出解析

- **`BeanOutputConverter`**：把 AI 返回的 JSON 转成 Java 对象
- **`ListOutputConverter`**、`MapOutputConverter` 等常用解析器
- 结构化输出：让 AI 按指定格式返回数据（比如 JSON、列表）

#### 4. 流式输出（SSE）

- 为什么需要流式：提升用户体验，不用等全部生成完
- `chatModel.stream()` 流式 API
- **SSE（Server-Sent Events）**：后端向前端流式推送
- 前端配合：`EventSource` 或 `fetch + ReadableStream`
- 流式响应的异常处理

#### 5. 对话记忆（多轮对话）

- **`ChatMemory`**：对话记忆接口
- **`MessageChatMemory`**：内存版对话历史
- 记忆管理：保留最近 N 条、Token 限制、对话清空
- 多用户对话隔离（按用户 ID 存不同的 ChatMemory）

------

### 二、建议掌握（做项目必备）

#### 6. Embedding 与向量检索

- **`EmbeddingModel`**：文本向量化模型接口

- 接入 Embedding 模型：通义 embedding、BGE、M3E

- 向量相似度计算（余弦相似度）

- 向量数据库集成

  ：

  - Redis Vector（简单方便）
  - Milvus（专业、高性能）
  - Chroma / Pinecone（了解）

- 基本操作：向量写入、相似度查询、Top-K 召回

#### 7. RAG 检索增强生成

- RAG 完整流程：文档加载 → 文本分块 → 向量化 → 存储 → 检索 → 生成

- 文档加载器

  ：

  - PDF 解析（Apache PDFBox、Tika）
  - Word 解析（Apache POI）
  - Markdown / TXT 解析

- **文本分块**：固定大小分块、按标题分块

- **检索 + 生成**：把召回的上下文拼到 Prompt 里，让大模型基于文档回答

- **引用溯源**：返回答案引用了哪些文档片段

#### 8. 多模态能力

- 图片理解

  ：多模态模型调用（qwen-vl、gpt-4o）

  - 图片输入方式：URL、Base64
  - 应用：图片描述、OCR、图表分析

- 图片生成

  ：调用文生图 API（通义万相、DALL·E）

  - 提示词工程（图片生成的提示词技巧）
  - 图片保存与返回

#### 9. 函数调用 / 工具调用（Function Calling）

- 什么是 Function Calling：让 AI 调用你写的 Java 方法
- Spring AI 函数调用方式：
  - `@Description` 注解定义工具函数
  - 函数注册到 ChatModel
  - 函数调用流程：AI 决定调用 → 执行函数 → 结果返回给 AI → AI 生成最终答案
- 实用工具例子：
  - 查询数据库（查订单、查用户信息）
  - 调用外部 API（查天气、查快递）
  - 计算器、时间工具

#### 10. Token 计数与成本控制

- Token 计费原理：输入 + 输出都收费
- 成本估算：不同模型的价格对比
- 成本优化手段：
  - 选合适的模型（简单问题用便宜模型）
  - 控制上下文长度（摘要压缩历史对话）
  - 缓存常用问答

------

### 三、进阶了解（高级玩法 / 面试加分）

#### 11. AI Agent 开发

- Agent 核心组成：大模型 + 工具调用 + 记忆 + 规划
- ReAct 模式（推理 + 行动）
- Spring AI Agent 模块
- 多 Agent 协作：多个 AI 分工合作
- 任务规划（Task Planner）

#### 12. RAG 高级优化

- **重排序（Rerank）**：用 Rerank 模型对召回结果二次排序
- **混合检索**：关键词检索（BM25）+ 向量检索融合
- **查询改写**：用大模型改写用户问题，提升召回率
- **多轮对话 RAG**：历史对话 + 查询重构
- 知识库增量更新、版本管理
- RAG 效果评估（召回率、准确率）

#### 13. 模型路由与降级

- 多模型管理：根据任务难度选择不同模型
- 故障降级：一个模型挂了自动切另一个
- 负载均衡：多个 API Key 轮询使用

#### 14. 安全与合规

- 输入校验：防止 Prompt 注入攻击
- 输出审核：敏感词过滤、内容安全
- 数据隐私：敏感数据不上传大模型
- 调用日志与审计

#### 15. 高级特性

- **结构化输出**：JSON Schema 约束输出格式
- **图像生成高级**：批量生成、风格控制
- **语音能力**：TTS 语音合成、ASR 语音识别
- **视频生成**：异步任务处理、状态轮询