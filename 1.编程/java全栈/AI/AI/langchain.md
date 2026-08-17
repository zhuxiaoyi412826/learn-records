文档 

[LangChain overview - Docs by LangChain](https://docs.langchain.com/oss/python/langchain/overview)

RAG 

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20260619121223.png)

![image-20260619121312396](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20260619121312396.png)

1、文件解析 2、文件切割  3、知识检索   4、知识重排序

Agent 架构

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20260619121501.png)

短期记忆上下文 长期记忆向量数据库

Agent = LLM + Memory + Tools + Planning +  Action

现代 Agent 核心特征：**目标驱动、自主规划、工具交互、分层记忆、反思闭环、可编排、支持多智能体协作**，不再是简单对话链，而是具备自主执行闭环的智能系统。



应用的4个场景

**场景1**：纯 Prompt  Prompt是操作大模型的唯一接口 当人看：你说一句，ta回一句，你再说一句，ta再回一句...

  

**场景2**：RAG (Retrieval-Augmented Generation)  RAG：需要补充领域知识时使用 Embeddings：把文字转换为更易于相似度计算的编码。这种编码叫向量 向量数据库：把向量存起来，方便查找 向量搜索：根据输入向量，找到最相似的向量

读取私有文档、实时外部数据，消除大模型幻觉、弥补知识截止缺陷。

**场景3**：Agent + Function Calling  Agent：AI 主动提要求 Function Calling：需要对接外部系统时，AI 要求执行某个函数 当人看：你问 ta「我明天去杭州出差，要带伞吗？」，ta 让你先看天气预报，你看了告诉ta，ta  再告诉你要不要带伞

自主智能 Agent 应用（高阶自动化）

**场景4**：Fine-tuning(精调/微调)  举例：努力学习考试内容，长期记住，活学活用。

定制 Prompt 模板、模型微调、结构化输出、批量任务调度、多模态生成模型。



**底层：LangGraph**：状态编排运行时，提供循环、断点、状态持久、人机交互

**中层：LangChain**：提供模型、工具、RAG、Prompt 等基础组件

**上层：DeepAgents**：封装全套高级 Agent 能力，开箱即用，内置规划、子智能体、虚拟文件系统、长期记忆

# 入门案例

安装依赖

pip install -U langchain

```
# Requires Python 3.10+
```

LangChain 提供数百个大型语言模型和数千个其他集成的集成。这些服务由独立供应商包包组成。

安装如下

```
pip install -U langchain deepagents
```

`DeepAgents`（深度智能体）是 **LangChain 官方推出的独立上层 SDK 库**，官方称呼为 **Agent Harness（智能体生产脚手架）**，专门用来构建**长周期、复杂多步骤自主智能体**LangChain。





```
import os
# 配置OpenAI API密钥，否则模型无法正常调用
os.environ["OPENAI_API_KEY"] = "sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
```

```
import os
# 配置OpenAI API密钥，否则模型无法正常调用
os.environ["DEEPSEEK_API_KEY"] = 


# 导入创建智能体函数
from langchain.agents import create_agent

# 自定义工具：查询城市天气
def get_weather(city: str) -> str:
    """Get weather for a given city.
    根据城市名称查询天气
    :param city: 待查询城市名
    :return: 天气描述文本
    """
    return f"It's always sunny in {city}!"

# 实例化Agent，模型切换为DeepSeek
agent = create_agent(
    # 格式：厂商:模型名，deepseek-chat 通用对话模型；deepseek-reasoner 推理强化模型
    model="deepseek:deepseek-chat",
    # 注入自定义工具列表
    tools=[get_weather],
    # 智能体人设提示词
    system_prompt="You are a helpful assistant",
)

# 发起请求，询问北京天气
result = agent.invoke(
    {
        "messages": [
            {
                "role": "user",
                "content": "What's the weather in Beijing?"
            }
        ]
    }
)

# 打印AI最终回复的结构化内容
print(result["messages"][-1].content_blocks)
```

导入依赖

```
pip install -qU langchain "langchain[deepseek]" -i https://pypi.tuna.tsinghua.edu.cn/simple
```

启动访问 python1.py

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20260619131216.png)











## 入门案例2

安装依赖

```
python langchain_demo.py
pip install langchain-openai
```

env 配置

```
DEEPSEEK_API_KEY=sk-b3ab35cd5969496cafbe6ce538959c49
DEEPSEEK_API_BASE=https://api.deepseek.com/v1
DEEPSEEK_MODEL=deepseek-chat

# 嵌入模型配置（使用本地模型）
EMBEDDING_MODEL=sentence-transformers/all-MiniLM-L6-v2

# FAISS 向量存储路径
FAISS_INDEX_PATH=./faiss_index

# 文档路径
DOCUMENTS_PATH=./documents
```

虚环境配置

```
# 创建虚拟环境
python -m venv venv

# 激活虚拟环境
# Windows
venv\Scripts\activate
# Linux/Mac
source venv/bin/activate

退出虚环境
deactivate

python quick_start.py
```



```
"""
LangChain 快速入门 - DeepSeek API
最简单的 LangChain HelloWorld 示例
"""

import os
from dotenv import load_dotenv
from langchain_openai import ChatOpenAI

# 加载环境变量
load_dotenv()

def quick_start():
    """
    快速入门 - 5分钟上手 LangChain
    """
    print("=" * 60)
    print("LangChain 快速入门 - DeepSeek API")
    print("=" * 60)
    
    # 创建 DeepSeek LLM
    llm = ChatOpenAI(
        model="deepseek-chat",
        openai_api_key=os.getenv("DEEPSEEK_API_KEY"),
        openai_api_base="https://api.deepseek.com/v1",
        temperature=0.7
    )
    
    print("\n✅ DeepSeek LLM 已创建")
    
    # 简单对话
    print("\n开始对话...")
    
    while True:
        user_input = input("\n请输入问题（输入 'quit' 退出）: ")
        
        if user_input.lower() == 'quit':
            print("\n再见！")
            break
        
        # 发送消息
        response = llm.invoke(user_input)
        print(f"\n回答: {response.content}")


if __name__ == "__main__":
    quick_start()
```



运行 python langchain_demo.py

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20260619134032.png)









**pip**：Python 官方标准包管理器，原生内置，所有 Python 自带；

**uv**：由 Astral 开发、**兼容 pip 语法的新一代极速包管理器**，完全替代 pip /pipenv/poetry /virtualenv；

关系：**uv 是 pip 的高性能替代品，语法高度兼容，能无缝替换 pip 所有命令**。



文本嵌入模型

sentence-transformers>=2.2.0 

将文字转换成数字向量 ，让计算机能"理解"文本的语义含义