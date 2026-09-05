# bge‑large‑zh‑v1.5 +Odrant 

> 方案特点：Windows 原生 Qdrant‑exe，不依赖 Docker/WSL；使用本地离线`bge‑large‑zh‑v1.5`中文 Embedding 模型；两套可视化面板：Qdrant 原生 Dashboard + Streamlit 业务管理面板；提供**requests HTTP 调用方案**，规避 qdrant‑client SDK 兼容异常。

## 一、组件说明

1. **Qdrant**：Windows 独立 exe 向量数据库，提供向量存储、余弦相似度检索、payload 元数据过滤；需要单独下载`dist‑qdrant.zip`静态资源，部署 web 后台面板。

2. bge‑large‑zh‑v1.5

   中文嵌入模型

   - 输出向量维度：`1024`，最大 token：512
   - 模型权重不会通过 pip 下载，需要手动下载，可通过 Modelscope 魔搭社区下载到本地文件夹
   - 代码直接指定本地模型路径加载，开启`HF_HUB_OFFLINE=1`可以完全禁止联网

3. Python 依赖包（使用国内清华镜像加速安装）

```
pip install qdrant-client FlagEmbedding streamlit python-dotenv numpy -i https://pypi.tuna.tsinghua.edu.cn/simple
```

- `qdrant‑client`：Qdrant 官方 SDK；如出现连接异常，可以直接使用`requests`调用 Qdrant HTTP REST 接口
- `FlagEmbedding`：加载 BGE 系列模型，实现中文文本转向量
- `streamlit`：快速开发业务 Web 管理页面，新增题目、语义检索、查看题库
- `numpy`：向量数组运算
- `python‑dotenv`：备用，读取环境配置文件，Demo 未使用

## 二、部署操作步骤

### 1. Qdrant 部署

1. 从 release [下载](https://github.com/qdrant/qdrant/releases) Windows 版本`qdrant‑x86_64‑pc‑windows‑msvc.exe`
2. [下载](https://github.com/qdrant/qdrant-web-ui/releases/download/v0.2.16/dist-qdrant.zip) webUI 静态包：`dist‑qdrant.zip`  [访问](http://127.0.0.1:6333/dashboard)
3. 在`qdrant.exe`同级目录新建`static`文件夹，将 zip 内全部文件解压到 static 根目录，保证`static/index.html`直接可见，禁止嵌套文件夹
4. PowerShell 启动服务

```
.\qdrant.exe --config-path config.yaml
.\qdrant.exe
```

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260827212724.png)

### 2. bge‑large‑zh‑v1.5

1. 安装 modelscope 2. 下载模型

```
pip list | findstr modelscope 查询是否安装
pip install modelscope
# 使用清华园加速镜像
pip install modelscope -i https://pypi.tuna.tsinghua.edu.cn/simple --trusted-host pypi.tuna.tsinghua.edu.cn

modelscope download --model BAAI/bge-large-zh-v1.5 --local-dir ./bge-large-zh-v1.5
```

### 3. Python 业务代码

**下载基础软件包**

**qdrant‑client**

> Qdrant 向量数据库的 Python 客户端。用来连接 Qdrant exe 服务，做：创建集合、插入向量、向量索、查询数据。

**FlagEmbedding**

> BGE 系列模型的专用库，用来加载 `bge‑large‑zh‑v1.5`，把中文题目文本转换成 1024 维向量。

**streamlit**

> 快速做 Web 可视化面板。就是我们写的管理页面，浏览器打开就能新增题目、语义搜索、查看题库。

**python‑dotenv** 读取 `.env` 环境配置文件，本 demo 暂时没用到，属于备用依赖。

**numpy** 数值计算库，向量是 numpy 数组，模型输出向量、向量运算都要靠它。

（HTTP 接口版本，规避 SDK 问题） 下载  使用清华园加速镜像

```
pip install qdrant-client FlagEmbedding streamlit python-dotenv numpy -i https://pypi.tuna.tsinghua.edu.cn/simple
```

```
PS C:\Users\Administrator\.cache\huggingface\hub> pip list | findstr "qdrant-client FlagEmbedding streamlit numpy"
FlagEmbedding                            1.4.2
numpy                                    2.5.1
qdrant-client                            1.19.0
streamlit                                1.62.0
PS C:\Users\Administrator\.cache\huggingface\hub>
```

**启动测试**

1. `main.py`：底层业务逻辑脚本，实现：创建集合、题目向量化入库、向量语义检索、支持分类过滤；内置算法题库样例数据，直接运行即可导入测试数据。

2. `app.py`

   ：Streamlit 可视化业务面板

   - Tab1：新增题目入库；
   - Tab2：语义检索，支持按分类过滤；
   - Tab3：查看全部题库，支持清空集合；

3. 启动 web 业务面板命令

```
streamlit run app.py
```

访问地址：`http://localhost:8501/`

> 集合配置参数：向量 size=1024，距离算法`Cosine`余弦相似度；`upsert`根据 id 更新，相同 qid 会覆盖旧数据，不会重复存储。



main.py

```
import requests
import numpy as np
from FlagEmbedding import FlagModel

# ==========配置==========
QDRANT_URL = "http://127.0.0.1:6333"
COLLECTION_NAME = "algorithm_question"
VECTOR_DIM = 1024
LOCAL_MODEL_PATH = r"C:\Users\Administrator\.cache\huggingface\hub\bge-large-zh-v1.5"

embedding_model = FlagModel(
    model_name_or_path=LOCAL_MODEL_PATH,
    use_fp16=False
)

def text_to_vector(text: str) -> np.ndarray:
    return embedding_model.encode(text)

def create_collection_if_not_exist():
    """HTTP方式：如果集合不存在则创建"""
    resp = requests.get(f"{QDRANT_URL}/collections/{COLLECTION_NAME}")
    if resp.status_code == 404:
        body = {
            "vectors": {
                "size": VECTOR_DIM,
                "distance": "Cosine"
            }
        }
        res = requests.put(f"{QDRANT_URL}/collections/{COLLECTION_NAME}", json=body)
        res.raise_for_status()
        print(f"✅ 集合 {COLLECTION_NAME} 已创建")
    else:
        print(f"✅ 集合 {COLLECTION_NAME} 已存在")

def upsert_point(qid: int, title: str, category: str):
    vec = text_to_vector(title).tolist()
    payload = {"title": title, "category": category}
    body = {
        "points": [
            {
                "id": qid,
                "vector": vec,
                "payload": payload
            }
        ]
    }
    resp = requests.put(f"{QDRANT_URL}/collections/{COLLECTION_NAME}/points", json=body)
    resp.raise_for_status()

def search_similar_question(query_text: str, top_k=3, category_filter=None):
    query_vec = text_to_vector(query_text).tolist()
    body = {
        "vector": query_vec,
        "limit": top_k,
        "with_payload": True
    }
    if category_filter is not None:
        body["filter"] = {
            "must": [
                {
                    "key": "category",
                    "match": {"value": category_filter}
                }
            ]
        }
    resp = requests.post(f"{QDRANT_URL}/collections/{COLLECTION_NAME}/points/search", json=body)
    resp.raise_for_status()
    return resp.json()["result"]

if __name__ == "__main__":
    # 关键：先确保集合存在
    create_collection_if_not_exist()

    question_list = [
        {"title": "如何仅用递归函数和栈操作逆序一个栈", "category": "栈", "qid": 1},
        {"title": "最大值减去最小值小于或等于num的子数组数量", "category": "数组", "qid": 2},
        {"title": "在单链表和双链表中删除倒数第K个节点", "category": "链表", "qid": 3},
        {"title": "环形单链表的约瑟夫问题", "category": "链表", "qid": 4},
        {"title": "打印两个有序链表的公共部分", "category": "链表", "qid": 5},
    ]
    for q in question_list:
        upsert_point(q["qid"], q["title"], q["category"])
    print("✅算法题库数据导入完成")

    print("\n=====检索：栈逆序相关=====")
    hits = search_similar_question("递归实现栈逆序", top_k=3)
    for hit in hits:
        score = hit["score"]
        payload = hit["payload"]
        print(f"相似度:{score:.4f} | {payload['category']} | {payload['title']}")

    print("\n=====只筛选链表类别检索=====")
    hits2 = search_similar_question("链表删除节点", top_k=3, category_filter="链表")
    for hit in hits2:
        score = hit["score"]
        payload = hit["payload"]
        print(f"相似度:{score:.4f} | {payload['category']} | {payload['title']}")

```

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260827211851.png)



**appipy**

[题库向量检索管理面板](http://localhost:8501/)

```
import streamlit as st
import requests
import numpy as np
from FlagEmbedding import FlagModel

# ==========配置==========
QDRANT_URL = "http://127.0.0.1:6333"
COLLECTION_NAME = "algorithm_question"
VECTOR_DIM = 1024
LOCAL_MODEL_PATH = r"C:\Users\Administrator\.cache\huggingface\hub\bge-large-zh-v1.5"

# 加载模型
embedding_model = FlagModel(
    model_name_or_path=LOCAL_MODEL_PATH,
    use_fp16=False
)

def text_to_vector(text: str) -> np.ndarray:
    return embedding_model.encode(text)

def create_collection_if_not_exist():
    resp = requests.get(f"{QDRANT_URL}/collections/{COLLECTION_NAME}")
    if resp.status_code == 404:
        body = {
            "vectors": {
                "size": VECTOR_DIM,
                "distance": "Cosine"
            }
        }
        res = requests.put(f"{QDRANT_URL}/collections/{COLLECTION_NAME}", json=body)
        res.raise_for_status()

def add_question(title: str, category: str, qid: int):
    vec = text_to_vector(title).tolist()
    payload = {"title": title, "category": category}
    body = {
        "points": [
            {
                "id": qid,
                "vector": vec,
                "payload": payload
            }
        ]
    }
    resp = requests.put(f"{QDRANT_URL}/collections/{COLLECTION_NAME}/points", json=body)
    resp.raise_for_status()

def search_similar_question(query_text: str, top_k=3, category_filter=None):
    query_vec = text_to_vector(query_text).tolist()
    body = {
        "vector": query_vec,
        "limit": top_k,
        "with_payload": True
    }
    if category_filter is not None:
        body["filter"] = {
            "must": [
                {
                    "key": "category",
                    "match": {"value": category_filter}
                }
            ]
        }
    resp = requests.post(f"{QDRANT_URL}/collections/{COLLECTION_NAME}/points/search", json=body)
    resp.raise_for_status()
    return resp.json()["result"]

def scroll_all_points(limit=100):
    resp = requests.post(f"{QDRANT_URL}/collections/{COLLECTION_NAME}/points/scroll", json={"limit": limit})
    resp.raise_for_status()
    return resp.json()["result"]["points"]

def delete_collection():
    requests.delete(f"{QDRANT_URL}/collections/{COLLECTION_NAME}").raise_for_status()

# ---------------------- Streamlit页面 ----------------------
st.set_page_config(page_title="题库向量检索管理面板", layout="wide")
st.title("📚 题库向量检索管理面板（bge‑large‑zh‑v1.5 + Qdrant）")

# 启动时保证集合存在
create_collection_if_not_exist()

tab1, tab2, tab3 = st.tabs(["新增题目", "语义检索", "题库列表"])

# Tab1：新增题目
with tab1:
    st.subheader("新增题目入库")
    title = st.text_input("题目文本")
    subject = st.selectbox("科目", ["栈", "数组", "链表", "数学", "语文", "生物", "物理"])
    qid = st.number_input("题目ID", min_value=1, step=1, value=100)
    if st.button("提交并向量化存入Qdrant"):
        if title.strip():
            add_question(title, subject, qid)
            st.success(f"✅题目【{title}】已完成向量化入库！")
        else:
            st.warning("请输入题目文本")

# Tab2：语义检索
with tab2:
    st.subheader("语义搜索相似题目")
    query = st.text_input("输入查询题目")
    filter_subject = st.selectbox("科目过滤（可选）", ["全部", "栈", "数组", "链表", "数学", "语文", "生物", "物理"])
    top_k = st.slider("返回结果数量", min_value=1, max_value=10, value=3)

    filter_subject_val = None
    if filter_subject != "全部":
        filter_subject_val = filter_subject

    if st.button("执行向量检索"):
        if query.strip():
            res = search_similar_question(query, top_k=top_k, category_filter=filter_subject_val)
            st.markdown("### 检索结果")
            for item in res:
                st.info(f"""
**相似度得分：{item['score']:.4f}**
- 科目：{item['payload']['category']}
- 题目：{item['payload']['title']}
- 向量ID：{item['id']}
                """)
        else:
            st.warning("请输入查询文本")

# Tab3：题库列表
with tab3:
    st.subheader("全部题库数据")
    points = scroll_all_points(limit=100)
    for p in points:
        st.write(f"ID:{p['id']} | 科目:{p['payload']['category']} | 题目：{p['payload']['title']}")

    if st.button("⚠️清空整个题库集合（谨慎）"):
        delete_collection()
        st.success("集合已删除，下次运行会自动重建")

st.divider()
st.markdown(f"> Qdrant原生可视化面板：[http://127.0.0.1:6333/dashboard](http://127.0.0.1:6333/dashboard)")

```

```
streamlit run app.py
```

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260827213634.png)

## 三、关键注意事项

1. pip 仅安装 Python 程序库，**不会下载 bge 模型权重文件**，模型必须手动准备本地文件；代码传入模型文件夹路径，不要传入单个 bin 文件。
2. Qdrant 访问 404 Dashboard：检查 static 文件夹位置、解压层级，重启 qdrant.exe。
3. qdrant‑client SDK 连接异常时，直接使用 requests 调用官方 HTTP REST 接口，兼容性更好。
4. bge‑large‑zh‑v1.5 最大输入 token=512，长文本 / 长题目需要做文本分块，避免截断丢失语义。
5. 向量维度必须严格保持 1024，集合创建 size 参数和模型输出维度保持一致。

**完整业务调用**

1. 连接 Qdrant
2. 创建集合，维度 1024，余弦相似度
3. 题库文本向量化，存入 Qdrant
4. 检索相似题目，支持科目过滤
5. 封装函数，供 streamlit 面板调用

这是**完全不依赖 qdrant‑client SDK**，直接调用 Qdrant 的 HTTP 接口做算法题向量检索。

1. **向量库存储的是：题目文本向量 + 元数据（题目名称、分类标签）**
2. 检索原理：输入查询文字转向量，在库中找余弦距离最接近的向量；可以附加条件过滤分类。
3. 同 qid 重复 upsert 会覆盖旧数据，不会产生重复记录



## 整体回顾

1. 使用 pip 安装全部 Python 依赖包；
2. 下载 Qdrant Windows exe + webUI 静态资源，启动 Qdrant 服务；
3. 下载`bge‑large‑zh‑v1.5`模型到本地，运行测试脚本确认模型正常输出 1024 维向量；
4. 运行业务脚本，导入样例题库；
5. 启动 Streamlit 业务管理面板，进行题目入库、语义检索操作；
6. 可打开 Qdrant 原生 Dashboard 查看底层向量集合、payload 元数据。

