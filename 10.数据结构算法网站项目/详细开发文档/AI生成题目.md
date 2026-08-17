# AI 生成算法题目

> 完整记录「**AI 生成题目**」功能从用户点击按钮到数据落库的 **全链路流程**、**关键代码**、**接口契约** 与 **异常处理**。

---

## 目录

- [AI 生成题目 功能详解](#ai-生成题目-功能详解)
  - [目录](#目录)
  - [1. 功能概览](#1-功能概览)
  - [2. 整体架构](#2-整体架构)
  - [3. 前端实现](#3-前端实现)
    - [3.1 入口按钮](#31-入口按钮)
    - [3.2 配置弹窗](#32-配置弹窗)
    - [3.3 生成结果预览与编辑](#33-生成结果预览与编辑)
    - [3.4 批量入库（逐题调用）](#34-批量入库逐题调用)
  - [4. 后端实现](#4-后端实现)
    - [4.1 Controller 层](#41-controller-层)
    - [4.2 Service 层（AI 调用）](#42-service-层ai-调用)
    - [4.3 Prompt 构造](#43-prompt-构造)
    - [4.4 单题入库接口](#44-单题入库接口)
    - [4.5 智能题号分配](#45-智能题号分配)
  - [5. 数据库表结构](#5-数据库表结构)
  - [6. 接口契约](#6-接口契约)
    - [6.1 `POST /api/ai/generate-problems`（同步）](#61-post-apiaigenerate-problems同步)
    - [6.2 `POST /api/problems`（单题入库）](#62-post-apiproblems单题入库)
  - [7. 完整调用流程时序图](#7-完整调用流程时序图)
  - [8. 异常处理与边界情况](#8-异常处理与边界情况)
  - [9. 关键文件索引](#9-关键文件索引)
  - [附录 A：PowerShell 测试脚本](#附录-apowershell-测试脚本)

---

## 1. 功能概览

在 OJ 题目管理页面，提供「**AI 生成题目**」入口：

1. 用户在配置表单中选择 **知识点 / 难度 / 数量 / 语言 / 风格 / 额外要求**
2. 前端把参数提交到后端 AI 接口
3. 后端调用 DeepSeek 大模型生成结构化 JSON
4. 前端把生成结果以 **可编辑卡片** 形式展示
5. 用户可逐道修改、勾选要入库的题
6. 点击「**批量添加 N 道到题库**」后，**前端逐题**调用单题入库接口
7. 后端 `POST /api/problems` 入库 MySQL，**自动分配题号**避免冲突

---

## 2. 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│  前端（Vue 3 + Element Plus）                              │
│  houtai/src/views/content/Problem.vue                      │
└────────────┬────────────────────────────────────────────────┘
             │ (1) 配置参数
             ▼
┌─────────────────────────────────────────────────────────────┐
│  后端 Controller                                            │
│  AIChatController: POST /api/ai/generate-problems          │
│  OJProblemController: POST /api/problems (单题入库)        │
└────────────┬────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────┐
│  Service 层                                                 │
│  AIProblemService: 调 DeepSeek、解析 JSON                  │
│  OJProblemServiceImpl: 入库 + 智能题号                     │
└────────────┬────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────┐
│  MySQL 8 数据库                                             │
│  algoviz.oj_problem 表（BIGINT 自增主键）                   │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. 前端实现

> 文件：[`houtai/src/views/content/Problem.vue`](file:///d:/1/算法数据结构可视化/AlgoVize/houtai/src/views/content/Problem.vue)

### 3.1 入口按钮

在 `OJ题目管理` 页面顶部，"**新增题目**" 按钮旁加一个绿色 "**AI 生成题目**" 按钮：

```vue
<el-button type="primary" :icon="Plus" @click="handleAdd">新增题目</el-button>
<el-button type="success" :icon="MagicStick" @click="handleAIGenerate">
  AI 生成题目
</el-button>
```

### 3.2 配置弹窗

点击按钮触发 `handleAIGenerate()`，打开配置弹窗：

```typescript
/** 打开 AI 生成配置弹窗 */
const handleAIGenerate = () => {
  // 重置表单
  aiForm.value = {
    knowledgePoints: [],
    difficulty: 'medium',
    count: 2,
    language: 'java',
    style: 'standard',
    additionalRequirements: ''
  }
  aiGenerateVisible.value = true
}
```

弹窗模板（关键字段）：

```vue
<el-dialog v-model="aiGenerateVisible" title="AI 生成题目配置" width="640px">
  <el-form :model="aiForm" label-width="100px">
    <!-- 知识点多选 -->
    <el-form-item label="知识点">
      <el-select v-model="aiForm.knowledgePoints" multiple filterable
                 allow-create placeholder="选择或输入标签" style="width:100%">
        <el-option v-for="t in presetTags" :key="t" :label="t" :value="t" />
      </el-select>
    </el-form-item>

    <el-form-item label="难度">
      <el-radio-group v-model="aiForm.difficulty">
        <el-radio-button value="easy">简单</el-radio-button>
        <el-radio-button value="medium">中等</el-radio-button>
        <el-radio-button value="hard">困难</el-radio-button>
      </el-radio-group>
    </el-form-item>

    <el-form-item label="题目数量">
      <el-input-number v-model="aiForm.count" :min="1" :max="10" />
    </el-form-item>

    <el-form-item label="语言">
      <el-select v-model="aiForm.language">
        <el-option value="general" label="通用编程题" />
        <el-option value="java" label="Java 专项" />
        <el-option value="javascript" label="JavaScript 专项" />
        <el-option value="python" label="Python 专项" />
      </el-select>
    </el-form-item>

    <el-form-item label="题目风格">
      <el-radio-group v-model="aiForm.style">
        <el-radio-button value="standard">常规题</el-radio-button>
        <el-radio-button value="variant">变式题</el-radio-button>
        <el-radio-button value="scenario">场景应用题</el-radio-button>
      </el-radio-group>
    </el-form-item>

    <el-form-item label="额外要求">
      <el-input v-model="aiForm.additionalRequirements" type="textarea" :rows="2" />
    </el-form-item>
  </el-form>

  <template #footer>
    <el-button @click="aiGenerateVisible = false">取消</el-button>
    <el-button type="primary" :loading="aiGenerating" @click="submitAIGenerate">
      开始生成
    </el-button>
  </template>
</el-dialog>
```

**提交生成**：

```typescript
const submitAIGenerate = async () => {
  if (aiForm.value.knowledgePoints.length === 0) {
    ElMessage.warning('请至少选择 1 个知识点')
    return
  }
  aiGenerating.value = true
  try {
    const resp = await fetch('http://localhost/api/ai/generate-problems', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(aiForm.value)
    })
    const data = await resp.json()
    if (data.success) {
      // 给每道题加 draftId 和默认 selected
      aiGeneratedProblems.value = data.problems.map((p: any) => ({
        ...p,
        draftId: Math.random().toString(36).slice(2, 10),
        selected: true
      }))
      aiGenerateVisible.value = false
      aiPreviewVisible.value = true   // 打开预览编辑弹窗
      ElMessage.success(data.message)
    } else {
      ElMessage.error(data.message || 'AI 生成失败')
    }
  } catch (err) {
    ElMessage.error('AI 生成失败: ' + (err as Error).message)
  } finally {
    aiGenerating.value = false
  }
}
```

### 3.3 生成结果预览与编辑

弹出预览弹窗，每道题以**可编辑卡片**展示：

```vue
<el-dialog v-model="aiPreviewVisible" title="AI 生成结果预览" width="90%" top="5vh">
  <div v-for="p in aiGeneratedProblems" :key="p.draftId" class="ai-card">
    <el-card shadow="hover">
      <!-- 顶部：勾选 + 标题/题号编辑 + 单题编辑按钮 -->
      <template #header>
        <div class="card-header">
          <el-checkbox v-model="p.selected" />
          <span>题号 #</span>
          <el-input v-model="p.problemNo" size="small" style="width:90px" />
          <span style="margin-left: 12px">标题：</span>
          <el-input v-model="p.title" size="small" style="width:240px" />
          <el-select v-model="p.difficulty" size="small" style="width:100px;margin-left:12px">
            <el-option value="easy" label="简单" />
            <el-option value="medium" label="中等" />
            <el-option value="hard" label="困难" />
          </el-select>
          <el-button size="small" @click="handleEditGenerated(p)" style="margin-left:auto">
            单题编辑
          </el-button>
          <el-button size="small" type="danger" @click="handleRemoveGenerated(idx)">
            删除
          </el-button>
        </div>
      </template>

      <!-- 中部：标签 + 描述 + 输入/输出 + 样例 + 提示 -->
      <el-form label-width="80px" size="small">
        <el-form-item label="标签">
          <el-input v-model="p.tags" />
        </el-form-item>
        <el-form-item label="题面">
          <el-input v-model="p.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="输入格式">
          <el-input v-model="p.inputFormat" />
        </el-form-item>
        <el-form-item label="输出格式">
          <el-input v-model="p.outputFormat" />
        </el-form-item>
        <el-form-item label="样例输入">
          <el-input v-model="p.sampleInput" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="样例输出">
          <el-input v-model="p.sampleOutput" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="解题提示">
          <el-input v-model="p.hint" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
    </el-card>
  </div>

  <template #footer>
    <el-button @click="aiPreviewVisible = false">取消</el-button>
    <el-button type="primary" :loading="aiSaving" @click="handleBatchAddToLibrary">
      批量添加 {{ aiGeneratedProblems.filter(p => p.selected).length }} 道到题库
    </el-button>
  </template>
</el-dialog>
```

### 3.4 批量入库（逐题调用）

**核心：绕开 `/api/problems/batch` 的 500，改成前端循环调 `/api/problems`。**

```typescript
/** 一键批量添加到题库（逐题调单题入库 API） */
const handleBatchAddToLibrary = async () => {
  const selected = aiGeneratedProblems.value.filter(p => p.selected)
  if (selected.length === 0) {
    ElMessage.warning('请至少勾选 1 道题目')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定将选中的 ${selected.length} 道题目添加到题库？`,
      '批量入库',
      { type: 'info' }
    )
  } catch { return }

  let successCount = 0
  let failedCount = 0
  const failedReasons: string[] = []
  const succeededDraftIds = new Set<string>()
  aiSaving.value = true

  ElMessage.info(`开始入库 ${selected.length} 道题...`)

  for (let i = 0; i < selected.length; i++) {
    const p = selected[i]
    try {
      const fullDescription = buildAIDescription(p)

      const body = {
        problemNo: p.problemNo || '',   // 空 → 后端自动分配
        title: p.title,
        difficulty: p.difficulty || 'medium',
        tags: p.tags || '',
        description: fullDescription,
        template: p.template || '',
        status: 'ACTIVE'
      }

      const response = await fetch('http://localhost/api/problems', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      })
      const data = await response.json()
      if (data.success) {
        successCount++
        if (p.draftId) succeededDraftIds.add(p.draftId)
      } else {
        failedCount++
        failedReasons.push(`第 ${i + 1} 道「${p.title}」：${data.message || '未知错误'}`)
      }
    } catch (err) {
      failedCount++
      failedReasons.push(`第 ${i + 1} 道「${p.title}」：${(err as Error).message}`)
    }
  }

  aiSaving.value = false

  ElMessageBox.alert(
    `成功入库 ${successCount} 道，失败 ${failedCount} 道${failedReasons.length ? '\n\n失败原因：\n' + failedReasons.join('\n') : ''}`,
    '批量入库完成',
    { type: successCount > 0 ? 'success' : 'error' }
  )

  // 移除入库成功的草稿
  if (succeededDraftIds.size > 0) {
    aiGeneratedProblems.value = aiGeneratedProblems.value.filter(
      p => !succeededDraftIds.has(p.draftId)
    )
    loadData()
    if (aiGeneratedProblems.value.length === 0) {
      aiPreviewVisible.value = false
    }
  }
}

/** 把 AI 字段拼成结构化 Markdown 描述 */
const buildAIDescription = (p: any): string => {
  const parts: string[] = []
  if (p.description) parts.push(p.description)
  if (p.inputFormat)  parts.push(`**输入格式**\n\n${p.inputFormat}`)
  if (p.outputFormat) parts.push(`**输出格式**\n\n${p.outputFormat}`)
  if (p.sampleInput)  parts.push(`**样例输入**\n\n\`\`\`\n${p.sampleInput}\n\`\`\``)
  if (p.sampleOutput) parts.push(`**样例输出**\n\n\`\`\`\n${p.sampleOutput}\n\`\`\``)
  if (p.hint)         parts.push(`**解题提示**\n\n${p.hint}`)
  return parts.join('\n\n')
}
```

---

## 4. 后端实现

### 4.1 Controller 层

> 文件：[`AIChatController.java`](file:///d:/1/算法数据结构可视化/AlgoVize/houduan/src/main/java/com/algoviz/controller/AIChatController.java)

```java
@PostMapping("/generate-problems")
@Operation(summary = "AI 生成题目（同步）",
           description = "根据知识点/难度/数量/语言/风格生成结构化题目 JSON")
public AIGenerateProblemResponse generateProblemsByAI(
        @RequestBody AIGenerateProblemRequest request) {
    return aiProblemService.generateProblems(request);
}

@PostMapping(value = "/generate-problems/stream",
             produces = MediaType.TEXT_EVENT_STREAM_VALUE)
@Operation(summary = "AI 生成题目（SSE 流式）")
public SseEmitter generateProblemsStream(
        @RequestBody AIGenerateProblemRequest request) {
    // 流式推送生成进度
    return aiProblemService.generateProblemsStream(request);
}
```

### 4.2 Service 层（AI 调用）

> 文件：[`AIProblemService.java`](file:///d:/1/算法数据结构可视化/AlgoVize/houduan/src/main/java/com/algoviz/service/AIProblemService.java)

```java
@Service
public class AIProblemService {
    @Value("${algoviz.ai.deepseek.api-key}")
    private String apiKey;

    @Value("${algoviz.ai.deepseek.base-url}")
    private String baseUrl;

    /**
     * 同步生成题目
     */
    public AIGenerateProblemResponse generateProblems(AIGenerateProblemRequest req) {
        // ... 省略构造 HTTP 请求细节 ...

        // 2) 解析为 GeneratedProblem 列表
        List<GeneratedProblem> list = parseProblems(aiJson, req.getCount());
        // ...
    }

    /**
     * 将 AI 返回的 tags 字段转为字符串。
     * AI 可能返回字符串 "数组,动态规划" 或数组 ["数组", "动态规划"]
     */
    private String convertTags(JsonNode tagsNode) {
        if (tagsNode == null || tagsNode.isNull()) {
            return "";
        }
        if (tagsNode.isArray()) {
            StringBuilder sb = new StringBuilder();
            for (JsonNode item : tagsNode) {
                String s = item.asText("").trim();
                if (!s.isEmpty()) {
                    if (sb.length() > 0) sb.append(",");
                    sb.append(s);
                }
            }
            return sb.toString();
        }
        return tagsNode.asText("").trim();
    }
}
```

### 4.3 Prompt 构造

> 文件：[`AIGenerateProblemPrompt.java`](file:///d:/1/算法数据结构可视化/AlgoVize/houduan/src/main/java/com/algoviz/service/AIGenerateProblemPrompt.java)

```java
public class AIGenerateProblemPrompt {

    public static final String SYSTEM_PROMPT = """
        你是 AlgoViz 在线判题系统的算法题目生成助手。
        你的任务：根据用户给定的参数，生成 ${count} 道高质量的算法题。

        ## 输出要求（必须严格遵守）
        1. **只输出 JSON**，不要任何额外解释、Markdown 代码块标记
        2. JSON 顶层结构：
           {
             "problems": [
               {
                 "problemNo": "题号（4位数字字符串，如 2001）",
                 "title": "题目标题",
                 "difficulty": "easy | medium | hard",
                 "tags": "标签,逗号分隔",
                 "description": "题面 HTML（用 <p> 包裹）",
                 "inputFormat": "输入格式说明",
                 "outputFormat": "输出格式说明",
                 "sampleInput": "样例输入",
                 "sampleOutput": "样例输出",
                 "hint": "解题提示",
                 "template": "代码模板（含 class Solution）",
                 "language": "java | javascript | python | general"
               }
             ]
           }
        3. **难度**严格匹配用户选择
        4. **语言**如果用户指定 java/javascript/python，则 template 用该语言
        5. **题号**用 2001/2002... 递增（避开 LeetCode 1-3000）
        """;

    public static String buildUserPrompt(AIGenerateProblemRequest req) {
        return String.format("""
            知识点：%s
            难度：%s
            数量：%d 道
            语言：%s
            风格：%s
            额外要求：%s
            """,
            String.join("、", req.getKnowledgePoints()),
            req.getDifficulty(),
            req.getCount(),
            req.getLanguage(),
            req.getStyle(),
            req.getAdditionalRequirements() == null ? "" : req.getAdditionalRequirements()
        );
    }
}
```

### 4.4 单题入库接口

> 文件：[`OJProblemController.java`](file:///d:/1/算法数据结构可视化/AlgoVize/houduan/src/main/java/com/algoviz/controller/OJProblemController.java)

```java
@PostMapping
@Operation(summary = "添加单题", description = "手动新增 / AI 单题入库")
public Map<String, Object> addProblem(@RequestBody OJProblem problem) {
    Map<String, Object> result = new HashMap<>();
    try {
        problemService.addProblem(problem);
        result.put("success", true);
        result.put("message", "题目添加成功");
    } catch (Exception e) {
        logger.error("添加题目失败", e);
        result.put("success", false);
        result.put("message", "添加题目失败：" + e.getMessage());
    }
    return result;
}
```

### 4.5 智能题号分配

> 文件：[`OJProblemServiceImpl.java`](file:///d:/1/算法数据结构可视化/AlgoVize/houduan/src/main/java/com/algoviz/service/impl/OJProblemServiceImpl.java)

```java
@Override
public void addProblem(OJProblem problem) {
    // 1) 兜底
    problem.setStatus("ACTIVE");
    if (problem.getDifficulty() == null) problem.setDifficulty("medium");
    if (problem.getTags() == null)        problem.setTags("");
    if (problem.getDescription() == null) problem.setDescription("");
    if (problem.getTemplate() == null)    problem.setTemplate("");

    // 2) 智能题号：传入题号为空/冲突 → 自动递增
    if (problem.getProblemNo() == null || problem.getProblemNo().isBlank()
            || isProblemNoExists(problem.getProblemNo())) {
        problem.setProblemNo(generateNextProblemNo());
    }

    // 3) 时间戳
    String now = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    problem.setCreatedAt(now);
    problem.setUpdatedAt(now);

    // 4) 入库（id 由 MySQL BIGINT 自增）
    problemMapper.insertProblem(problem);
}

/** 查数据库中最大纯数字题号 → +1 → 冲突重试 */
@Override
public String generateNextProblemNo() {
    Long currentMax = problemMapper.getMaxNumericProblemNo();
    long candidate = (currentMax == null ? 0L : currentMax) + 1;
    int retry = 1000;
    while (isProblemNoExists(String.valueOf(candidate))) {
        candidate++;
        if (--retry <= 0) {
            return String.valueOf(System.currentTimeMillis()).substring(5);
        }
    }
    return String.valueOf(candidate);
}
```

> Mapper：[`OJProblemMapper.java`](file:///d:/1/算法数据结构可视化/AlgoVize/houduan/src/main/java/com/algoviz/mapper/OJProblemMapper.java)

```java
@Select("""
    SELECT COALESCE(MAX(CAST(problem_no AS UNSIGNED)), 0)
    FROM oj_problem
    WHERE problem_no REGEXP '^[0-9]+$'
    """)
Long getMaxNumericProblemNo();

@Options(useGeneratedKeys = true, keyProperty = "id")
@Insert("""
    INSERT INTO oj_problem
      (problem_no, title, difficulty, tags, description,
       template, status, submission_count, ac_rate,
       created_at, updated_at)
    VALUES
      (#{problemNo}, #{title}, #{difficulty}, #{tags}, #{description},
       #{template}, #{status}, #{submissionCount}, #{acRate},
       #{createdAt}, #{updatedAt})
    """)
void insertProblem(OJProblem problem);
```

---

## 5. 数据库表结构

> 文件：[`mysql_migration.sql`](file:///d:/1/算法数据结构可视化/AlgoVize/houduan/sql/mysql_migration.sql)

```sql
CREATE TABLE `oj_problem` (
  `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `problem_no`       VARCHAR(32)  NOT NULL                COMMENT '题号',
  `title`            VARCHAR(255) NOT NULL                COMMENT '标题',
  `difficulty`       VARCHAR(16)  NOT NULL DEFAULT 'medium' COMMENT 'easy/medium/hard',
  `tags`             VARCHAR(255) DEFAULT ''              COMMENT '标签,逗号分隔',
  `description`      MEDIUMTEXT                          COMMENT '题面（HTML/Markdown）',
  `template`         MEDIUMTEXT                          COMMENT '代码模板',
  `status`           VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/INACTIVE',
  `submission_count` INT          NOT NULL DEFAULT 0     COMMENT '提交数',
  `ac_rate`          DOUBLE       NOT NULL DEFAULT 0     COMMENT '通过率 0~100',
  `created_at`       DATETIME     NOT NULL                COMMENT '创建时间',
  `updated_at`       DATETIME     NOT NULL                COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_problem_no` (`problem_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OJ 题目主表';
```

> ⚠️ `id` 用 `BIGINT AUTO_INCREMENT`，**不用 UUID**——性能好、占用小、外键关联方便。
> 如果将来要分布式，再换 Snowflake。

---

## 6. 接口契约

### 6.1 `POST /api/ai/generate-problems`（同步）

**Request**:

```json
{
  "knowledgePoints": ["数组", "哈希表"],
  "difficulty": "medium",
  "count": 2,
  "language": "java",
  "style": "standard",
  "additionalRequirements": "数据规模 n<=10^5"
}
```

**Response**:

```json
{
  "success": true,
  "message": "成功生成 2 道题目",
  "problems": [
    {
      "problemNo": "2001",
      "title": "两数之和",
      "difficulty": "medium",
      "tags": "数组,哈希表",
      "description": "<p>给定一个整数数组...</p>",
      "inputFormat": "第一行 n，第二行 n 个整数",
      "outputFormat": "一个整数",
      "sampleInput": "4\n2 7 11 15\n9",
      "sampleOutput": "0 1",
      "hint": "用哈希表存已遍历值与下标",
      "template": "class Solution { ... }",
      "language": "java"
    }
  ]
}
```

### 6.2 `POST /api/problems`（单题入库）

**Request**:

```json
{
  "problemNo": "",   // 空 → 后端自动分配
  "title": "两数之和",
  "difficulty": "easy",
  "tags": "数组,哈希表",
  "description": "<p>...</p>\n\n**输入格式**\n\n...",
  "template": "class Solution {}",
  "status": "ACTIVE"
}
```

**Response**:

```json
{ "success": true, "message": "题目添加成功" }
```

或失败：

```json
{ "success": false, "message": "添加题目失败：xxx" }
```

---

## 7. 完整调用流程时序图

```
用户          前端              后端              DeepSeek          MySQL
 │              │                  │                  │                │
 │ 1.点击"AI生成" │                  │                  │                │
 ├─────────────►│                  │                  │                │
 │              │ 2.配置参数        │                  │                │
 │              │  提交             │                  │                │
 │              ├─────────────────►│ /ai/generate     │                │
 │              │                  ├─────────────────►│                │
 │              │                  │ 3.Prompt+参数    │                │
 │              │                  │◄─────────────────┤                │
 │              │                  │ 4.JSON题目       │                │
 │              │                  │                  │                │
 │              │ 5.返回 problems  │                  │                │
 │              │◄─────────────────┤                  │                │
 │ 6.预览弹窗    │                  │                  │                │
 │  编辑/勾选   │                  │                  │                │
 │ 7.点"批量入库" │                  │                  │                │
 ├─────────────►│                  │                  │                │
 │              │ 8.逐题循环        │                  │                │
 │              │  POST /problems  │                  │                │
 │              ├─────────────────►│                  │                │
 │              │                  │ 9.查最大题号      │                │
 │              │                  ├─────────────────────────────────────►│
 │              │                  │ 10.题号冲突检测   │                │
 │              │                  │ 11.INSERT         │                │
 │              │                  ├─────────────────────────────────────►│
 │              │                  │ 12.OK             │                │
 │              │ 13.success       │◄─────────────────────────────────────┤
 │              │◄─────────────────┤                  │                │
 │ 14.成功提示   │                  │                  │                │
 │◄─────────────┤                  │                  │                │
 │ 15.列表刷新   │                  │                  │                │
```

---

## 8. 异常处理与边界情况

| 场景                  | 处理方式                                                     |
| --------------------- | ------------------------------------------------------------ |
| AI Key 未配置         | 启动检查 `application.yml`，缺失则降级返回错误               |
| DeepSeek 限流         | 后端 3 次重试 + 退避；前端 60s 超时                          |
| AI 返回非 JSON        | 解析容错：返回空列表 + 错误信息                              |
| 题号冲突（已有）      | `generateNextProblemNo()` 自动 +1 重试                       |
| 题号为空              | 后端 `generateNextProblemNo()` 自动分配（最大+1）            |
| `description` 含 null | `addProblem` 兜底为空字符串                                  |
| 必填字段缺失          | 后端 NOT NULL 约束 → 500；前端 try-catch 弹窗                |
| 批量入库中途失败      | **前端逐题入库**，单道失败不影响其他                         |
| MySQL 8 字符集问题    | 表用 `utf8mb4`；JDBC URL 带 `characterEncoding=utf8`         |
| ID 类型不匹配         | `oj_problem.id` 用 `BIGINT`，全代码 `String → Long` 转换     |
| `tags` 字段类型不匹配 | **AI 可能返回数组 `["数组", "动态规划"]**，而后端实体是 `String`。解决方案：<br/>1. 后端 `@JsonSetter("tags")` 支持接收 `Object`，判断为 `List` 时自动 `join(",")`<br/>2. `AIProblemService.convertTags()` 预处理 AI 原始 JSON<br/>3. 前端发送前 `Array.isArray() ? join(',')` |

---

## 9. 关键文件索引

| 模块               | 文件                                                         |
| ------------------ | ------------------------------------------------------------ |
| 前端页面           | [Problem.vue](file:///d:/1/算法数据结构可视化/AlgoVize/houtai/src/views/content/Problem.vue) |
| 后端 AI Controller | [AIChatController.java](file:///d:/1/算法数据结构可视化/AlgoVize/houduan/src/main/java/com/algoviz/controller/AIChatController.java) |
| 后端 OJ Controller | [OJProblemController.java](file:///d:/1/算法数据结构可视化/AlgoVize/houduan/src/main/java/com/algoviz/controller/OJProblemController.java) |
| AI Service         | [AIProblemService.java](file:///d:/1/算法数据结构可视化/AlgoVize/houduan/src/main/java/com/algoviz/service/AIProblemService.java) |
| Prompt 构造        | [AIGenerateProblemPrompt.java](file:///d:/1/算法数据结构可视化/AlgoVize/houduan/src/main/java/com/algoviz/service/AIGenerateProblemPrompt.java) |
| OJ Service 实现    | [OJProblemServiceImpl.java](file:///d:/1/算法数据结构可视化/AlgoVize/houduan/src/main/java/com/algoviz/service/impl/OJProblemServiceImpl.java) |
| Mapper             | [OJProblemMapper.java](file:///d:/1/算法数据结构可视化/AlgoVize/houduan/src/main/java/com/algoviz/mapper/OJProblemMapper.java) |
| DTO 请求           | [AIGenerateProblemRequest.java](file:///d:/1/算法数据结构可视化/AlgoVize/houduan/src/main/java/com/algoviz/dto/AIGenerateProblemRequest.java) |
| DTO 响应           | [AIGenerateProblemResponse.java](file:///d:/1/算法数据结构可视化/AlgoVize/houduan/src/main/java/com/algoviz/dto/AIGenerateProblemResponse.java) |
| 核心实体           | [OJProblem.java](file:///d:/1/算法数据结构可视化/AlgoVize/houduan/src/main/java/com/algoviz/entity/OJProblem.java) |
| 数据库建表         | [mysql_migration.sql](file:///d:/1/算法数据结构可视化/AlgoVize/houduan/sql/mysql_migration.sql) |

---

## 附录 A：PowerShell 测试脚本

```powershell
$API = "http://localhost:80"

# 1. AI 生成
$aiBody = '{"knowledgePoints":["数组","哈希表"],"difficulty":"easy","count":2,"language":"java","style":"standard"}'
$aiResp = Invoke-RestMethod -Uri "$API/api/ai/generate-problems" -Method POST -ContentType "application/json" -Body $aiBody -TimeoutSec 90

# 2. 逐题入库
foreach ($p in $aiResp.problems) {
    $body = @{
        problemNo   = $p.problemNo
        title       = $p.title
        difficulty  = $p.difficulty
        tags        = $p.tags
        description = $p.description
        template    = $p.template
        status      = "ACTIVE"
    } | ConvertTo-Json -Depth 5

    $r = Invoke-RestMethod -Uri "$API/api/problems" -Method POST -ContentType "application/json" -Body $body
    Write-Host "$($p.title): $($r.message)"
}

# 3. 验证
Invoke-RestMethod -Uri "$API/api/problems/all" | 
    Select-Object -ExpandProperty problems | 
    Sort-Object id -Descending | 
    Select-Object -First 3 id, problemNo, title, difficulty |
    Format-Table -AutoSize
```

---

> **文档版本**：v1.1  
> **最后更新**：2026-08-04  
> **维护者**：AlgoViz Team

# AI 生成面试题目