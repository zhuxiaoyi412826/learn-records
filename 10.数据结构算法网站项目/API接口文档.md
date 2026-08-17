# AlgoVize API 接口文档

## 一、系统概述

AlgoVize 是一个算法数据结构可视化平台，后端基于 Spring Boot 3.2.0，使用 MySQL 数据库存储题目数据，集成 DeepSeek v4-pro 大模型实现 AI 题目生成与智能问答。

### 技术栈

| 模块 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.2.0 |
| 数据库 | MySQL | 8.0+ |
| ORM | MyBatis | 3.0+ |
| API 文档 | Swagger/OpenAPI | 2.3.0 |
| AI 模型 | DeepSeek | v4-pro |
| 端口 | HTTP | 80 |

---

## 二、题目管理 API 设计思路

### 2.1 设计原则

1. **RESTful 风格**：使用标准 HTTP 方法（GET/POST/PUT/DELETE）表示资源操作
2. **统一响应格式**：所有接口返回统一的 JSON 结构，包含 `success`、`message`、`data` 字段
3. **题号策略**：支持智能题号分配，冲突时可选择覆盖或重新分配
4. **批量操作**：提供批量添加和 Excel 导入功能，提高数据录入效率
5. **状态管理**：题目支持 ACTIVE/INACTIVE 状态，软删除机制

### 2.2 数据模型

```
OJProblem (题目实体)
├── id           (主键，自增)
├── problemNo    (题号，唯一标识)
├── title        (题目标题)
├── difficulty   (难度: easy/medium/hard)
├── tags         (标签，逗号分隔)
├── description  (题目描述，Markdown 格式)
├── template     (代码模板)
├── status       (状态: ACTIVE/INACTIVE)
├── submissionCount (提交次数)
├── acRate       (通过率)
├── createdAt    (创建时间)
└── updatedAt    (更新时间)
```

### 2.3 题号分配策略

| 场景 | 处理方式 |
|------|----------|
| 题号为空 | 自动生成（数据库最大题号 + 1） |
| 题号存在且不覆盖 | 重新分配新题号 |
| 题号存在且覆盖 | 更新已有记录 |

---

## 三、题目管理 API 接口

### 3.1 获取题目列表

**GET** `/api/problems`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | String | 否 | 关键词搜索（标题/描述） |
| difficulty | String | 否 | 难度筛选（easy/medium/hard） |

**响应示例**：
```json
{
  "success": true,
  "problems": [
    {
      "id": 1,
      "problemNo": "1500",
      "title": "设计一个有getMin功能的栈",
      "difficulty": "easy",
      "tags": "栈,数据结构",
      "description": "题目描述...",
      "template": "public class Solution {...}",
      "status": "ACTIVE",
      "submissionCount": 100,
      "acRate": 0.65,
      "createdAt": "2026-07-27 10:00:00",
      "updatedAt": "2026-07-27 10:00:00"
    }
  ],
  "count": 328
}
```

### 3.2 获取所有题目（含禁用）

**GET** `/api/problems/all`

同上，区别在于返回包含 INACTIVE 状态的题目。

### 3.3 获取题目详情

**GET** `/api/problems/{id}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 题目 ID |

**响应示例**：
```json
{
  "success": true,
  "problem": {
    "id": 1,
    "problemNo": "1500",
    "title": "设计一个有getMin功能的栈",
    "difficulty": "easy",
    "tags": "栈,数据结构",
    "description": "实现一个特殊的栈...",
    "template": "public class Solution {...}",
    "status": "ACTIVE",
    "submissionCount": 100,
    "acRate": 0.65
  }
}
```

### 3.4 按题号获取题目

**GET** `/api/problems/by-no/{problemNo}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| problemNo | String | 是 | 题号 |

### 3.5 添加题目

**POST** `/api/problems`

**请求体**：
```json
{
  "problemNo": "1500",
  "title": "设计一个有getMin功能的栈",
  "difficulty": "easy",
  "tags": "栈,数据结构",
  "description": "题目描述",
  "template": "public class Solution {...}",
  "status": "ACTIVE"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| problemNo | String | 否 | 题号，为空自动分配 |
| title | String | 是 | 题目标题 |
| difficulty | String | 否 | 难度，默认 medium |
| tags | String | 否 | 标签 |
| description | String | 否 | 题目描述 |
| template | String | 否 | 代码模板 |
| status | String | 否 | 状态，默认 ACTIVE |

### 3.6 更新题目

**PUT** `/api/problems/{id}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 题目 ID |

**请求体**：同添加题目

### 3.7 删除题目

**DELETE** `/api/problems/{id}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 题目 ID |

### 3.8 获取统计信息

**GET** `/api/problems/stats`

**响应示例**：
```json
{
  "success": true,
  "total": 328,
  "easy": 55,
  "medium": 75,
  "hard": 198
}
```

### 3.9 导出题目（SQL 格式）

**GET** `/api/problems/export/sql`

**说明**：导出所有题目为 SQL 文件，包含建表语句 + INSERT 数据。

**响应**：`application/sql` 文件下载（`oj_problems.sql`）

### 3.10 导出题目（JSON 格式）

**GET** `/api/problems/export/json`

**说明**：导出所有题目为 JSON 文件，包含元数据 + 题目列表。

**响应**：`application/json` 文件下载（`oj_problems.json`）

---

## 四、批量导入接口

### 4.1 批量添加题目

**POST** `/api/problems/batch`

**请求体**：
```json
{
  "problems": [
    {
      "problemNo": "1500",
      "title": "设计一个有getMin功能的栈",
      "difficulty": "easy",
      "tags": "栈",
      "description": "题目描述",
      "inputFormat": "输入格式",
      "outputFormat": "输出格式",
      "sampleInput": "样例输入",
      "sampleOutput": "样例输出",
      "hint": "解题提示",
      "template": "代码模板"
    }
  ],
  "overwriteOnConflict": false
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| problems | Array | 是 | 题目列表 |
| overwriteOnConflict | Boolean | 否 | 题号冲突时是否覆盖，默认 false |

**响应示例**：
```json
{
  "success": true,
  "message": "成功 3 道，失败 0 道",
  "successCount": 3,
  "failedCount": 0,
  "failedReasons": []
}
```

### 4.2 Excel 批量导入

**POST** `/api/problems/import-excel`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | MultipartFile | 是 | Excel 文件（.xlsx/.xls） |
| overwriteOnConflict | Boolean | 否 | 题号冲突时是否覆盖，默认 false |

**Excel 模板格式**：

| 列名 | 必填 | 说明 | 别名支持 |
|------|------|------|----------|
| 题号 | 是 | 题目编号 | problemNo, 编号 |
| 标题 | 是 | 题目名称 | title, 题目名称, 题目 |
| 难度 | 是 | 难度等级 | difficulty |
| 标签 | 否 | 标签 | tags |
| 题目描述 | 否 | 题目描述 | description |
| 解答 | 否 | 解题提示 | hint, 解题提示, 解析 |
| 代码实现 | 否 | 代码模板 | template, 代码模板, 代码 |

**难度映射规则**：

| Excel 难度值 | 系统难度 |
|-------------|----------|
| 士 / 简单 / 1 / easy | easy |
| 尉 / 中等 / 2 / medium | medium |
| 将 / 困难 / 3 / hard | hard |

---

## 五、AI 相关接口

### 5.1 AI 聊天

**POST** `/api/ai/chat`

**请求体**：
```json
{
  "messages": [
    {"role": "system", "content": "你是一个算法辅导助手"},
    {"role": "user", "content": "解释一下什么是二叉树"}
  ]
}
```

### 5.2 AI 生成题目（同步）

**POST** `/api/ai/generate-problems`

**请求体**：
```json
{
  "count": 3,
  "difficulty": "medium",
  "language": "java",
  "style": "leetcode",
  "knowledgePoints": ["链表", "二叉树"],
  "additionalRequirements": "题目要有详细的解题思路"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| count | Integer | 否 | 生成数量，默认 1，最大 10 |
| difficulty | String | 否 | 难度，默认 medium |
| language | String | 否 | 代码语言，默认 java |
| style | String | 否 | 题目风格，默认 leetcode |
| knowledgePoints | Array | 否 | 知识点列表 |
| additionalRequirements | String | 否 | 额外要求 |

### 5.3 AI 生成题目（流式）

**POST** `/api/ai/generate-problems/stream`

请求体同同步版本，返回 SSE 流式响应。

---

## 六、数据导入方式

### 6.1 JSON 导入

1. 准备 JSON 文件，格式如下：
```json
[
  {
    "id": 1,
    "title": "1. 两数之和",
    "description": "题目描述",
    "examples": [...],
    "constraints": [...],
    "solutions": [...]
  }
]
```

2. 使用批量添加接口 `/api/problems/batch` 导入

### 6.2 Excel 导入

1. 准备 Excel 文件（.xlsx/.xls）
2. 按照模板格式填写数据
3. 通过 `/api/problems/import-excel` 接口上传

### 6.3 AI 生成导入

1. 调用 `/api/ai/generate-problems` 生成题目
2. 人工审核编辑
3. 通过 `/api/problems/batch` 批量入库

---

## 七、测试方法

### 7.1 手动测试

1. **Swagger UI**：访问 `http://localhost/swagger-ui.html`
2. **curl 命令**：
```bash
# 获取题目列表
curl http://localhost/api/problems

# 添加题目
curl -X POST http://localhost/api/problems \
  -H "Content-Type: application/json" \
  -d '{"title":"测试题目","difficulty":"medium"}'

# 获取统计
curl http://localhost/api/problems/stats
```

3. **Postman/Insomnia**：导入 API 文档进行测试

### 7.2 自动测试

项目提供自动测试工具，见 `tools/api_test.py`。

---

## 八、自动测试工具使用说明

### 8.1 工具位置

```
tools/api_test.py
```

### 8.2 功能特性

| 功能 | 说明 |
|------|------|
| 健康检查 | 验证服务是否正常运行 |
| 题目 CRUD | 测试增删改查功能 |
| 批量导入 | 测试批量添加接口 |
| 统计接口 | 验证统计数据正确性 |
| Excel 导入 | 测试 Excel 批量导入 |
| AI 接口 | 测试 AI 聊天和题目生成 |
| 报告生成 | 生成详细的测试报告 |

### 8.3 使用方法

```bash
# 安装依赖
pip install requests openpyxl

# 运行所有测试
python tools/api_test.py

# 运行特定测试
python tools/api_test.py --test=crud
python tools/api_test.py --test=batch
python tools/api_test.py --test=excel
python tools/api_test.py --test=ai

# 查看帮助
python tools/api_test.py --help
```

### 8.4 测试报告

测试完成后会生成 `test_report.md` 文件，包含：
- 测试时间
- 通过/失败统计
- 每个接口的测试详情
- 响应时间统计

---

## 九、错误码说明

| HTTP 状态码 | 说明 |
|-------------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 十、附录

### 10.1 难度枚举

| 值 | 含义 | 颜色标识 |
|----|------|----------|
| easy | 简单 | 绿色 |
| medium | 中等 | 黄色 |
| hard | 困难 | 红色 |

### 10.2 题目状态

| 值 | 含义 |
|----|------|
| ACTIVE | 启用 |
| INACTIVE | 禁用 |

### 10.3 API 访问地址

| 环境 | 地址 |
|------|------|
| 本地 | http://localhost |
| Swagger | http://localhost/swagger-ui.html |
| API Docs | http://localhost/api-docs |

---

## 十一、用户管理 API 接口

### 11.1 获取用户列表（分页 + 多条件筛选）

**GET** `/api/users`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | String | 否 | 关键词搜索（用户名/邮箱） |
| gender | String | 否 | 性别筛选（男/女） |
| status | Integer | 否 | 账号状态（1:正常 0:封禁） |
| loginStatus | String | 否 | 登录状态（online/offline） |
| order | String | 否 | 注册时间排序（desc倒序/asc正序），默认 desc |
| page | Integer | 否 | 页码，默认 1 |
| pageSize | Integer | 否 | 每页条数，默认 100 |

**响应示例**：
```json
{
  "success": true,
  "users": [
    {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "age": 28,
      "gender": "男",
      "nickname": "管理员",
      "avatarUrl": "https://i.pravatar.cc/150?u=1",
      "loginStatus": "offline",
      "status": 1,
      "createdAt": "2026-07-27T14:17:23",
      "updatedAt": "2026-08-04T21:51:35",
      "lastLoginAt": "2026-07-27T14:17:23"
    }
  ],
  "count": 6806,
  "page": 1,
  "pageSize": 100
}
```

### 11.2 获取用户详情

**GET** `/api/users/{id}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Integer | 是 | 用户 ID |

### 11.3 添加用户

**POST** `/api/users`

**请求体**：
```json
{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "123456",
  "gender": "男",
  "nickname": "新用户"
}
```

> 自动设置默认值：`avatarUrl` = `https://i.pravatar.cc/150?u={timestamp}`，`loginStatus` = `offline`，`status` = `1`

### 11.4 更新用户状态（封禁/解封）

**PUT** `/api/users/{id}/status`

**说明**：仅更新用户的账号状态字段，不影响其他字段。

**请求体**：
```json
{
  "status": 0
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | Integer | 是 | 账号状态（1:正常 0:封禁） |

**响应示例**：
```json
{
  "success": true,
  "message": "用户状态更新成功"
}
```

### 11.5 删除用户

**DELETE** `/api/users/{id}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Integer | 是 | 用户 ID |

### 11.6 导出用户数据（JSON）

**GET** `/api/users/export/json`

**说明**：导出所有用户数据为 JSON 文件，**自动排除密码字段**。

**响应**：`application/json` 文件下载（`users.json`）

**导出字段**：id, username, email, age, gender, nickname, avatarUrl, loginStatus, status, createdAt, updatedAt, lastLoginAt

---

## 十二、提交管理 API 接口

### 12.1 获取提交列表

**GET** `/api/submissions`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| problemId | Long | 否 | 按题目 ID 筛选 |
| status | String | 否 | 按状态筛选（AC/WA/CE/RE/TLE/PENDING） |

**响应示例**：
```json
{
  "success": true,
  "submissions": [
    {
      "submissionId": "SUB17859064723950d67591d",
      "problemId": 456,
      "problemTitle": "N皇后",
      "userId": 1,
      "username": "前端用户",
      "code": "public class Main {...}",
      "language": "java",
      "status": "AC",
      "runtime": 359,
      "memory": 8616,
      "submitTime": "2026-08-05T14:30:00"
    }
  ],
  "count": 10
}
```

### 12.2 获取用户提交记录

**GET** `/api/submissions/user/{userId}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | 是 | 用户 ID |

### 12.3 获取提交详情

**GET** `/api/submissions/{submissionId}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| submissionId | String | 是 | 提交 ID |

**响应示例**：
```json
{
  "success": true,
  "submission": {
    "submissionId": "SUB17859064723950d67591d",
    "problemId": 456,
    "problemTitle": "N皇后",
    "code": "public class Main {...}",
    "language": "java",
    "status": "AC",
    "runtime": 359,
    "memory": 8616,
    "judgeLog": "测试用例 #1: 通过\n测试用例 #2: 通过\n",
    "errorMessage": null,
    "submitTime": "2026-08-05T14:30:00"
  }
}
```

### 12.4 运行代码（测试）

**POST** `/api/submissions/run`

**说明**：前端运行代码测试，使用沙箱模式编译执行 Java 代码。用户提交完整的 Java 文件（类名为 `Main`），后端直接编译运行。输入通过命令行参数传递。

**请求体**：
```json
{
  "problemId": 456,
  "code": "public class Main { public static void main(String[] args) { System.out.println(\"Hello\"); } }",
  "language": "java",
  "input": "3 1"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| problemId | Long | 是 | 题目 ID |
| code | String | 是 | 完整的 Java 代码（类名 Main） |
| language | String | 是 | 编程语言（目前支持 java） |
| input | String | 否 | 自定义输入，为空时使用第一个测试用例 |

**响应示例（成功）**：
```json
{
  "success": true,
  "status": "SUCCESS",
  "output": "40\n",
  "error": null,
  "runtime": 103,
  "compileError": null
}
```

**响应示例（编译错误）**：
```json
{
  "success": true,
  "status": "CE",
  "output": "",
  "error": "编译错误",
  "runtime": 0,
  "compileError": "Main.java:1: 错误: 需要';'\n..."
}
```

**状态码说明**：

| status 值 | 含义 |
|-----------|------|
| SUCCESS | 运行成功 |
| CE | 编译错误（Compile Error） |
| RE | 运行时错误（Runtime Error） |
| TLE | 运行超时（Time Limit Exceeded） |

### 12.5 提交代码（判题）

**POST** `/api/submissions`

**说明**：提交代码进行正式判题。后端异步执行判题逻辑，遍历所有测试用例比对输出结果。前端通过轮询获取判题结果。

**请求体**：
```json
{
  "problemId": 456,
  "code": "public class Main { public static void main(String[] args) { ... } }",
  "language": "java",
  "userId": 1,
  "username": "前端用户"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| problemId | Long | 是 | 题目 ID |
| code | String | 是 | 完整的 Java 代码（类名 Main） |
| language | String | 是 | 编程语言 |
| userId | Long | 否 | 用户 ID |
| username | String | 否 | 用户名 |

**响应示例**：
```json
{
  "success": true,
  "message": "提交成功",
  "submissionId": "SUB17859064723950d67591d"
}
```

**判题状态流转**：

```
PENDING → AC / WA / CE / RE / TLE
```

| 状态 | 含义 |
|------|------|
| PENDING | 等待判题 |
| AC | 通过（Accepted） |
| WA | 答案错误（Wrong Answer） |
| CE | 编译错误 |
| RE | 运行时错误 |
| TLE | 运行超时 |

### 12.6 获取题目测试用例

**GET** `/api/submissions/testcases/{problemId}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| problemId | Long | 是 | 题目 ID |

**响应示例**：
```json
{
  "success": true,
  "testCases": [
    {
      "id": 1,
      "problemId": "456",
      "input": "3 1",
      "output": "a"
    }
  ],
  "count": 5
}
```

### 12.7 删除提交记录

**DELETE** `/api/submissions/{submissionId}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| submissionId | String | 是 | 提交 ID |

### 12.8 用户提交统计

**GET** `/api/submissions/stats/user/{userId}`

**响应示例**：
```json
{
  "success": true,
  "totalSubmissions": 10,
  "acCount": 6,
  "acRate": "60.00%"
}
```

### 12.9 题目提交统计

**GET** `/api/submissions/stats/problem/{problemId}`

**响应示例**：
```json
{
  "success": true,
  "totalSubmissions": 128
}
```

### 12.10 沙箱执行限制

| 限制项 | 值 |
|--------|-----|
| 编译超时 | 2500 ms |
| 运行超时 | 5000 ms |
| 内存限制 | 256 MB |
| 栈大小 | 1 MB |
| 安全管理器 | 启用（`-Djava.security.manager=allow`） |

### 12.11 代码提交规范

用户需提交**完整的 Java 文件**，类名必须为 `Main`，包含 `main` 方法：

```java
public class Main {
    public static void main(String[] args) {
        // 在此处编写代码
        // 输入通过 args[0] 获取（如有）
        System.out.println("结果");
    }
}
```

> ⚠️ 类名必须为 `Main`，不能使用 Java 关键字（如 `default`）。
