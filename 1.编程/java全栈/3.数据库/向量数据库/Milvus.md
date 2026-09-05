# 安装Milvus

# Milvus向量化

**Java 实现：算法题 JSON → Chunk 切分 → Embedding → 向量库入库完整流程→ 提问输出** 

**RAG 检索侧简单逻辑（补充）普通 RAG（单次）：
  检索 → 生成 → 返回

LLM 内循环 RAG（迭代纠错）：
  检索 → 生成 → 自我评估 → 不达标？
         ↑                    ↓ 是
         └── 重检索 + 批评反馈 ←┘
                              ↓ 否
                            返回最终答案用户输入问题文本 → 调用 embedding 得到 query 向量 → Milvus ANN 搜索，按 cos 相似度召回 top5 chunk → 取出 text 字段拼接 Prompt，交给大模型生成回答。

业务场景：算法题 JSON，拆分题干、思路、代码 Chunk；调用 Embedding 接口生成向量；存入向量库（这里选用 **Milvus**，也可以替换 pgvector chroma）。

> 技术栈：Java 17，SpringBoot3，Milvus Java SDK，JSON 解析 Jackson，HTTP 客户端 OkHttp。
>
> Embedding：调用 DeepSeek‑Embedding API（输出 1024 维）；也可以替换 BGE 本地模型。

**整体流程**

plaintext

```
1.读取算法题JSON文件/数据库记录
↓
2.按业务语义切分Chunk（不是粗暴按字符切割；分为题干块、思路块、代码块）
↓
3.调用Embedding接口，每个Chunk生成float向量数组
↓
4.组装Milvus实体：id，原始文本块，向量，元数据（题目id、难度、标签）
↓
5.批量插入向量数据库Milvus
```

## 依赖 pom.xml

xml

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    <groupId>com.oj</groupId>
    <artifactId>oj‑vector‑builder</artifactId>
    <version>0.0.1‑SNAPSHOT</version>

    <dependencies>
        <!-- SpringBoot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <!-- Milvus Java SDK -->
        <dependency>
            <groupId>io.milvus</groupId>
            <artifactId>milvus‑sdk‑java</artifactId>
            <version>2.4.5</version>
        </dependency>
        <!-- JSON解析 Jackson -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson‑databind</artifactId>
        </dependency>
        <!-- HTTP 请求调用DeepSeek Embedding -->
        <dependency>
            <groupId>com.squareup.okhttp3</groupId>
            <artifactId>okhttp</artifactId>
        </dependency>
        <!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
</project>
```

## 1、算法题 JSON 示例 oj_question.json

json

```
{
  "questionId":1001,
  "title":"两数之和",
  "difficulty":"easy",
  "tags":["数组","哈希表"],
  "content":"给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target 的那两个整数，并返回它们的数组下标。\n你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。",
  "sampleInput":"nums = [2,7,11,15], target = 9",
  "sampleOutput":"[0,1]",
  "solutionIdea":"使用哈希表，遍历数组，判断target‑num是否存在map中；空间换时间，时间复杂度O(n)，空间O(n)。避免双重循环暴力解法。",
  "code":"public int[] twoSum(int[] nums, int target){\nHashMap<Integer,Integer> map=new HashMap<>();\nfor(int i=0;i<nums.length;i++){\nint need=target‑nums[i];\nif(map.containsKey(need)) return new int[]{map.get(need),i};\nmap.put(nums[i],i);\n}\nreturn new int[]{};\n}"
}
```

## 2、Java 实体

### Question.java 原始题目对象

```
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Question {
    private Long questionId;
    private String title;
    private String difficulty;
    private List<String> tags;
    private String content;
    private String sampleInput;
    private String sampleOutput;
    private String solutionIdea;
    private String code;
}
```

### ChunkItem.java 切分之后的块

```
import lombok.Data;

@Data
public class ChunkItem {
    // 归属题目ID
    private Long questionId;
    // 块类型：content题干 / idea思路 / code代码
    private String chunkType;
    // 文本内容，送入embedding
    private String text;
    // 元数据，过滤检索用
    private String difficulty;
    private List<String> tags;
}
```

## 3、第一步：语义切分 Chunk

> 算法题不要用通用 RecursiveCharacterTextSplitter 按字符暴力切；**按业务字段拆成 3 个 Chunk**。

```
import java.util.ArrayList;
import java.util.List;

public class QuestionChunkSplitter {

    /**
     * 一道题目拆多个chunk：题干、解题思路、代码
     */
    public static List<ChunkItem> split(Question q){
        List<ChunkItem> list=new ArrayList<>();

        // chunk1：题干块：题目描述+样例
        ChunkItem contentChunk=new ChunkItem();
        contentChunk.setQuestionId(q.getQuestionId());
        contentChunk.setChunkType("content");
        contentChunk.setText(q.getTitle()+"\n"+q.getContent()+"\n输入样例："+q.getSampleInput()+"\n输出样例："+q.getSampleOutput());
        contentChunk.setDifficulty(q.getDifficulty());
        contentChunk.setTags(q.getTags());
        list.add(contentChunk);

        // chunk2：解题思路块
        ChunkItem ideaChunk=new ChunkItem();
        ideaChunk.setQuestionId(q.getQuestionId());
        ideaChunk.setChunkType("idea");
        ideaChunk.setText(q.getTitle()+" 解题思路："+q.getSolutionIdea());
        ideaChunk.setDifficulty(q.getDifficulty());
        ideaChunk.setTags(q.getTags());
        list.add(ideaChunk);

        // chunk3：代码块
        ChunkItem codeChunk=new ChunkItem();
        codeChunk.setQuestionId(q.getQuestionId());
        codeChunk.setChunkType("code");
        codeChunk.setText(q.getTitle()+" 代码实现：\n"+q.getCode());
        codeChunk.setDifficulty(q.getDifficulty());
        codeChunk.setTags(q.getTags());
        list.add(codeChunk);

        return list;
    }
}
```

> 一道题输出 3 个 ChunkItem；对应前面说的一题拆 2‑3 向量方案。

## 4、第二步：调用 DeepSeek Embedding 接口，把 text 转为 float [] 向量

> DeepSeek embedding 输出维度：**1024 维 float 数组**。

```
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DeepSeekEmbeddingClient {
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public DeepSeekEmbeddingClient(String apiKey){
        this.apiKey=apiKey;
        this.objectMapper=new ObjectMapper();
        this.client=new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 输入文本，返回1024维float向量
     */
    public float[] embed(String text) throws IOException {
        String jsonBody = """
                {
                    "model":"deepseek‑embedding",
                    "input":"%s"
                }
                """.formatted(text.replace("\"","\\\""));

        RequestBody body=RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf‑8"));
        Request request=new Request.Builder()
                .url("https://api.deepseek.com/v1/embeddings")
                .header("Authorization","Bearer "+apiKey)
                .post(body)
                .build();

        try(Response resp=client.newCall(request).execute()){
            String respBody=resp.body().string();
            JsonNode root=objectMapper.readTree(respBody);
            JsonNode arr=root.at("/data/0/embedding");
            List<Float> floatList=objectMapper.convertValue(arr,List.class);
            float[] vec=new float[floatList.size()];
            for(int i=0;i<vec.length;i++) vec[i]=floatList.get(i);
            return vec;
        }
    }
}
```

## 5、第三步 Milvus 写入入库（创建集合、批量插入）

> collection 名称：`oj_question_vector`，维度 1024；字段：

- id：主键 Long
- question_id：题目 id
- chunk_type：块类型 content/idea/code
- text：原始 chunk 文本
- difficulty：难度
- tags：标签数组
- vector：1024 维向量

```
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.*;
import io.milvus.param.collection.*;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.grpc.DataType;

import java.util.*;

public class MilvusVectorRepo {
    private final MilvusServiceClient milvusClient;
    private static final String COLLECTION_NAME="oj_question_vector";
    private static final int DIM=1024;

    public MilvusVectorRepo(String host,int port){
        milvusClient=new MilvusServiceClient(ConnectParam.newBuilder()
                .withHost(host).withPort(port).build());
    }

    /**
     * 初始化集合，只执行一次
     */
    public void createCollectionIfNotExist(){
        // 字段定义
        List<FieldType> fields=List.of(
                FieldType.newBuilder().withName("id").withDataType(DataType.Int64).withPrimaryKey(true).withAutoID(true).build(),
                FieldType.newBuilder().withName("question_id").withDataType(DataType.Int64).build(),
                FieldType.newBuilder().withName("chunk_type").withDataType(DataType.VarChar).withMaxLength(32).build(),
                FieldType.newBuilder().withName("text").withDataType(DataType.VarChar).withMaxLength(4096).build(),
                FieldType.newBuilder().withName("difficulty").withDataType(DataType.VarChar).withMaxLength(16).build(),
                FieldType.newBuilder().withName("tags").withDataType(DataType.Array).withElementType(DataType.VarChar).build(),
                FieldType.newBuilder().withName("vector").withDataType(DataType.FloatVector).withDimension(DIM).build()
        );

        CreateCollectionParam param=CreateCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withFieldTypes(fields)
                .build();
        milvusClient.createCollection(param);

        // 创建HNSW索引
        CreateIndexParam indexParam=CreateIndexParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withFieldName("vector")
                .withIndexType(IndexType.HNSW)
                .withMetricType(MetricType.COSINE)
                .withExtraParam("{\"M\":16,\"efConstruction\":100}")
                .build();
        milvusClient.createIndex(indexParam);

        // 加载集合到内存
        milvusClient.loadCollection(LoadCollectionParam.newBuilder().withCollectionName(COLLECTION_NAME).build());
    }

    /**
     * 批量插入Chunk
     */
    public void insertChunk(List<ChunkItem> chunkList, List<float[]> vectorList){
        List<Long> questionIdList=new ArrayList<>();
        List<String> chunkTypeList=new ArrayList<>();
        List<String> textList=new ArrayList<>();
        List<String> diffList=new ArrayList<>();
        List<List<String>> tagsList=new ArrayList<>();

        for(ChunkItem item:chunkList){
            questionIdList.add(item.getQuestionId());
            chunkTypeList.add(item.getChunkType());
            textList.add(item.getText());
            diffList.add(item.getDifficulty());
            tagsList.add(item.getTags());
        }

        InsertParam insert=InsertParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .addField("question_id",questionIdList)
                .addField("chunk_type",chunkTypeList)
                .addField("text",textList)
                .addField("difficulty",diffList)
                .addField("tags",tagsList)
                .addField("vector",vectorList)
                .build();
        milvusClient.insert(insert);
        milvusClient.flush(FlushParam.newBuilder().addCollectionName(COLLECTION_NAME).build());
    }
}
```

## 6、主流程入口：读取 JSON →切分→Embedding→入库

```
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImportMain {
    public static void main(String[] args) throws Exception {
        //1.配置
        String deepseekApiKey="sk‑xxxx";
        String milvusHost="127.0.0.1";
        int milvusPort=19530;

        DeepSeekEmbeddingClient embeddingClient=new DeepSeekEmbeddingClient(deepseekApiKey);
        MilvusVectorRepo milvusRepo=new MilvusVectorRepo(milvusHost,milvusPort);
        ObjectMapper objectMapper=new ObjectMapper();

        // 只初始化一次集合
        milvusRepo.createCollectionIfNotExist();

        //2.读取一道算法题JSON文件；实际可以循环读取大量题目文件
        File jsonFile=new File("oj_question.json");
        Question question=objectMapper.readValue(jsonFile, Question.class);

        //3.切分Chunk
        List<ChunkItem> chunkItems=QuestionChunkSplitter.split(question);

        //4.循环每个chunk调用embedding
        List<float[]> vectors=new ArrayList<>();
        for(ChunkItem chunk:chunkItems){
            float[] vec=embeddingClient.embed(chunk.getText());
            vectors.add(vec);
        }

        //5.批量入库Milvus
        milvusRepo.insertChunk(chunkItems,vectors);

        System.out.println("导入完成，生成chunk数量："+chunkItems.size());
    }
}
```

## 7 整套软件环境清单

1. JDK17
2. SpringBoot3 + Maven
3. Milvus 服务（本地 Docker 一键启动）/**Chroma**
4. DeepSeek API‑Key（获取 embedding 向量；如果想本地，替换 BGE‑large‑zh 模型，使用 OpenVINO / ONNX‑Java 推理）
5. OkHttp、Jackson

## 8 重要工程实践注意点

1. **批量处理**：不要一道题调用一次入库；攒 20‑50 道题做批量 insert，减少网络 IO。
2. Embedding 调用有 QPS 限制，要加限流重试。
3. 如果想改用 768 维 BGE，修改 DIM=768，替换 Embedding 客户端。
4. 检索时：使用余弦距离 `MetricType.COSINE`，top‑k=3‑5，可以过滤 difficulty、tags 元数据。
5. 原型阶段不想部署 Milvus，可以替换 pgvector，JDBC 写入向量。

# Java Milvus RAG 检索查询代码

延续上面项目：集合`oj_question_vector`，维度 1024，度量类型 **COSINE 余弦相似度**。

流程：

```
用户提问文本 → 调用DeepSeek‑Embedding生成query向量 → Milvus ANN向量检索 → 可附加元数据过滤（难度、标签）→ 返回匹配的Chunk列表
```

> 依赖和前面完全一致，直接新增查询 Repo 方法 + 查询 DTO。

## 1、返回 DTO：检索结果实体

```
import lombok.Data;
import java.util.List;

@Data
public class QuestionRetrievalDTO {
    private Long id;               // milvus主键id
    private Long questionId;       // 原始题目id
    private String chunkType;      // content / idea / code
    private String text;           // chunk原文，送入大模型prompt
    private String difficulty;
    private List<String> tags;
    private float score;           // cosine相似度，越接近1语义越相似
}
```

## 2、MilvusVectorRepo 新增检索方法

在前面 `MilvusVectorRepo` 类里面追加下面代码。

```
import io.milvus.param.dml.SearchParam;
import io.milvus.response.SearchResultsWrapper;
import io.milvus.grpc.SearchResult;
import java.util.ArrayList;
import java.util.List;

public class MilvusVectorRepo {
    // ==== 前面已有代码省略（createCollectionIfNotExist、insertChunk）====

    /**
     * 向量检索
     * @param queryVec 用户问题embedding向量
     * @param topK 返回多少条
     * @param filterExpr 过滤表达式，例如 difficulty == "easy" && tags in ["数组"]；传null不过滤
     * @return 检索结果列表
     */
    public List<QuestionRetrievalDTO> search(float[] queryVec, int topK, String filterExpr){
        List<String> outFields = List.of("question_id","chunk_type","text","difficulty","tags");

        SearchParam.Builder builder = SearchParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withVectorFieldName("vector")
                .withVectors(List.of(queryVec))
                .withTopK(topK)
                .withMetricType(MetricType.COSINE)
                .withOutFields(outFields)
                .withParams("{\"ef\":64}"); // HNSW查询参数 ef，越大召回越好，速度变慢

        if(filterExpr != null && !filterExpr.isBlank()){
            builder.withExpr(filterExpr);
        }

        SearchParam searchParam = builder.build();
        SearchResult resp = milvusClient.search(searchParam);
        SearchResultsWrapper wrapper = new SearchResultsWrapper(resp.getResults());

        List<SearchResultsWrapper.IDScore> hitList = wrapper.getIDScore(0);
        List<QuestionRetrievalDTO> resultList = new ArrayList<>();

        for(SearchResultsWrapper.IDScore hit : hitList){
            QuestionRetrievalDTO dto = new QuestionRetrievalDTO();
            dto.setId(hit.getLongID());
            dto.setScore(hit.getScore());

            dto.setQuestionId( (Long)hit.getFieldValue("question_id") );
            dto.setChunkType( (String)hit.getFieldValue("chunk_type") );
            dto.setText( (String)hit.getFieldValue("text") );
            dto.setDifficulty( (String)hit.getFieldValue("difficulty") );
            dto.setTags( (List<String>) hit.getFieldValue("tags") );

            resultList.add(dto);
        }
        return resultList;
    }
}
```

## 3、RAG 完整查询入口示例

```
import java.util.List;

public class QueryMain {
    public static void main(String[] args) throws Exception {
        String apiKey = "sk‑xxxx";
        String milvusHost = "127.0.0.1";
        int milvusPort = 19530;

        DeepSeekEmbeddingClient embeddingClient = new DeepSeekEmbeddingClient(apiKey);
        MilvusVectorRepo milvusRepo = new MilvusVectorRepo(milvusHost, milvusPort);

        // 用户问题
        String userQuery = "两数之和怎么做，时间复杂度能否优化？";

        // 1、用户问题生成embedding向量
        float[] queryVector = embeddingClient.embed(userQuery);

        // 2、向量检索 top‑k=4
        // filterExpr示例：只查easy难度: "difficulty == \"easy\""；不需要过滤传null
        String filter = null;
        List<QuestionRetrievalDTO> hits = milvusRepo.search(queryVector,4,filter);

        // 打印召回结果
        for(QuestionRetrievalDTO item : hits){
            System.out.println("==== score:"+item.getScore()+" | type:"+item.getChunkType()+" ====");
            System.out.println(item.getText());
        }

        // 3、把hits里面text拼接成上下文，送入大模型prompt
        StringBuilder contextSb = new StringBuilder();
        for(QuestionRetrievalDTO hit : hits){
            contextSb.append(hit.getText()).append("\n");
        }
        String prompt = """
                基于下面参考资料回答用户问题，资料：
                %s
                用户问题：%s
                """.formatted(contextSb,userQuery);

        System.out.println("\n==== 组装给大模型的Prompt ====");
        System.out.println(prompt);
    }
}
```

## 4、过滤表达式示例（filterExpr 语法）

Milvus 表达式语法，可以做元数据过滤，缩小检索范围

```
// 示例1：只查询简单难度
String filter1 = "difficulty == \"easy\"";

// 示例2：标签包含数组，并且难度中等
String filter2 = "tags in [\"数组\"] && difficulty == \"medium\"";

// 示例3：题目id范围
String filter3 = "question_id > 1000";
```

## 关键参数说明

1. `MetricType.COSINE`：余弦相似度，embedding 标准选择；分数范围 [-1,1]，越接近 1 代表语义越接近。
2. 查询参数 `{"ef":64}`：HNSW 查询 ef，范围 16‑128；ef 越大召回效果越好，CPU 消耗越高。
3. top‑k：算法知识库一般取 **3‑5**，不要取太大，避免上下文过长。

# Milvus Java 更新、删除、修改向量数据

> Milvus **没有直接的 update‑update（原地更新向量）API**。底层逻辑：

1. 向量字段**不支持直接 update 修改**；
2. 修改策略：**删除旧 Chunk，插入新 Chunk**；
3. 只有普通标量字段（difficulty、tags）可以部分更新，**FloatVector 向量字段不能部分更新**。

> 业务场景：一道算法题更新题干 / 题解 → 原来该题生成的 2‑3 条向量全部删掉，重新切分、生成 embedding、插入新向量。

## 核心操作

1. 根据`question_id`删除该题目全部旧向量（一题多条 chunk）
2. 对更新后的题目 JSON 重新切分 Chunk
3. 重新调用 Embedding 生成向量
4. 批量插入新 Chunk

> ❗不要直接做原地更新向量字段，Milvus 不支持。只能删旧插新。

## 在 MilvusVectorRepo 增加方法

```
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.QueryParam;
import io.milvus.response.QueryResultsWrapper;

public class MilvusVectorRepo {
    //====前面已有代码省略====

    /**
     * 根据questionId 删除该题所有chunk向量
     * @param questionId 题目id
     */
    public void deleteByQuestionId(Long questionId){
        String expr = "question_id == " + questionId;
        DeleteParam deleteParam = DeleteParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withExpr(expr)
                .build();
        milvusClient.delete(deleteParam);
        milvusClient.flush(FlushParam.newBuilder().addCollectionName(COLLECTION_NAME).build());
    }

    /**
     * 根据milvus主键id单条删除（很少用）
     */
    public void deleteById(Long milvusId){
        String expr = "id == " + milvusId;
        DeleteParam deleteParam = DeleteParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withExpr(expr)
                .build();
        milvusClient.delete(deleteParam);
        milvusClient.flush(FlushParam.newBuilder().addCollectionName(COLLECTION_NAME).build());
    }

    /**
     * 查询某一题所有milvus主键id，用于调试
     */
    public List<Long> queryMilvusIdsByQuestionId(Long questionId){
        String expr = "question_id == " + questionId;
        QueryParam queryParam = QueryParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withExpr(expr)
                .withOutFields(List.of("id"))
                .build();
        QueryResultsWrapper wrapper = new QueryResultsWrapper(milvusClient.query(queryParam).getResults());
        return wrapper.getFieldWrapper("id").getFieldData();
    }
}
```

## 更新一道题完整业务流程代码

```
public class UpdateQuestionMain {
    public static void main(String[] args) throws Exception {
        String deepseekApiKey = "sk‑xxxx";
        String milvusHost = "127.0.0.1";
        int milvusPort = 19530;

        DeepSeekEmbeddingClient embeddingClient = new DeepSeekEmbeddingClient(deepseekApiKey);
        MilvusVectorRepo milvusRepo = new MilvusVectorRepo(milvusHost, milvusPort);
        ObjectMapper objectMapper = new ObjectMapper();

        // 1. 待更新的题目ID
        Long updateQid = 1001L;

        // 2.读取修改后的新题目JSON（业务已经更新题干/题解）
        File newJsonFile = new File("oj_question_updated.json");
        Question newQuestion = objectMapper.readValue(newJsonFile, Question.class);

        // ==========核心更新步骤==========
        //① 删除该题目所有旧向量chunk
        milvusRepo.deleteByQuestionId(updateQid);

        //② 新题目切分chunk
        List<ChunkItem> newChunks = QuestionChunkSplitter.split(newQuestion);

        //③生成新embedding向量
        List<float[]> newVectors = new ArrayList<>();
        for(ChunkItem chunk : newChunks){
            float[] vec = embeddingClient.embed(chunk.getText());
            newVectors.add(vec);
        }

        //④插入新向量
        milvusRepo.insertChunk(newChunks, newVectors);

        System.out.println("题目更新完成，旧向量已删除，新向量已入库，chunk数量："+newChunks.size());
    }
}
```

## 其他场景

### 场景 1：只修改元数据

（difficulty、tags，**不修改文本、不修改向量**）

> 向量没有变化，不需要重新 embedding。
>
> Milvus 支持对标量字段做 partial update，**向量字段不能 partial update**。
>
> 注意：Milvus 2.4 + 才支持 partial update。

```
// 只更新标签、难度，文本和向量不变
// 根据question_id找到记录，执行partial update
// 注意：不改动vector，不要传入vector字段
```

> 但是工程实践，算法题库场景，**宁可统一走【删除‑重插入】，逻辑简单，不容易出错**，不建议用 partial update。

### 场景 2：删除某一道题目java

```
//直接调用删除即可
milvusRepo.deleteByQuestionId(1001L);
```

### 场景 3：批量更新大量题目

1. 遍历需要更新的 questionId 列表
2. 批量攒一批 delete 表达式；不要循环单条 delete，尽量批量删除
3. 批量切分、批量调用 embedding，批量 insert。

### 重要工程坑点

1. 删除只是逻辑删除

   Milvus delete 不会立刻释放磁盘，只是标记删除；真正物理清理发生在 

   Compaction 合并段

   。大量删除后，磁盘不会马上下降，属于正常现象。

2. 不要频繁高频删改；RAG 知识库偏向读多写少。

3. 并发场景：更新题目期间，查询依然可以查到旧数据，直到新数据插入完成；如果要求强一致性，业务层加锁。

4. 主键 id 是 Milvus 自动生成 AutoID；**业务使用自己的 question_id 做关联，不要依赖 milvus 内部 id**。

