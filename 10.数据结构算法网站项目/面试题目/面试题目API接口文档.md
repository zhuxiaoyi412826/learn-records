# AlgoVize 面试题目 - API 接口文档

## 一、系统概述

面试题目模块是独立于 OJ 系统的面试学习模块，核心能力为：**后台管理（导入/编辑/AI生成/导出）+ 前台查询（搜索/筛选/收藏）**。所有题目内容采用 **Markdown** 格式存储和展示。

### 1.1 技术栈

| 模块 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.2.0 |
| 数据库 | MySQL | 8.0+ |
| ORM | MyBatis | 3.0+ |
| AI 模型 | DeepSeek | v4-pro |
| API 文档 | Knife4j | 4.5.0 |
| 端口 | HTTP | 80 |
| 前缀 | `/api/interview` | 所有接口的统一前缀 |

### 1.2 统一响应格式

```json
{
  "success": true,
  "message": "操作成功",
  "data": {},
  "code": 200
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| success | Boolean | 是否成功 |
| message | String | 描述信息 |
| data | Object/Array | 业务数据 |
| code | Integer | 状态码，200=成功，4xx=客户端错误，5xx=服务端错误 |

> 为保持项目一致性，老接口（/api/problems 等）返回结构允许不带 `data`，新的 `/api/interview/*` 接口**必须使用上述统一结构**。

---

## 二、接口总览

### 2.1 后台管理接口（`/api/interview/admin/*`，需管理员登录）

| 序号 | 方法 | 路径 | 说明 |
|------|------|------|------|
| B1 | GET | `/api/interview/admin/problems` | 题目列表（分页 + 多条件筛选） |
| B2 | GET | `/api/interview/admin/problems/{id}` | 题目详情 |
| B3 | POST | `/api/interview/admin/problems` | 手动添加单道题 |
| B4 | PUT | `/api/interview/admin/problems/{id}` | 修改题目 |
| B5 | PUT | `/api/interview/admin/problems/{id}/status` | 切换上线/下线状态 |
| B6 | DELETE | `/api/interview/admin/problems/{id}` | 逻辑删除 |
| B7 | DELETE | `/api/interview/admin/problems/batch` | 批量删除 |
| B8 | DELETE | `/api/interview/admin/problems/real/{id}` | 物理删除(慎用) |
| B9 | DELETE | `/api/interview/admin/problems/real/batch` | 批量物理删除(慎用) |
| B10 | POST | `/api/interview/admin/problems/batch-import` | JSON 批量导入 |
| B11 | POST | `/api/interview/admin/problems/import-json` | 上传 JSON 文件导入 |
| B12 | GET | `/api/interview/admin/problems/export-json` | 一键导出全部题目为 JSON 文件 |
| B13 | POST | `/api/interview/admin/ai/generate` | AI 生成题目（同步，返回可预览内容） |
| B14 | POST | `/api/interview/admin/problems/batch-save` | AI 生成结果「批量保存到题库」 |
| B15 | GET | `/api/interview/admin/stats` | 面试题统计总览（题库数量、分类标签、前台统计等） |

### 2.2 前台用户接口（`/api/interview/user/*`）

> 权限说明：GET 查询类接口（F1-F5、F16-F17）无需用户登录；POST / DELETE 等用户行为接口（收藏、历史、点赞、清空等）必须登录鉴权。

| 序号 | 方法   | 路径                                              | 说明                                             |
| ---- | ------ | ------------------------------------------------- | ------------------------------------------------ |
| F1   | GET    | `/api/interview/user/problems`                    | 查询上线题目（分页）                             |
| F2   | GET    | `/api/interview/user/problems/{id}`               | 题目详情（Markdown）                             |
| F3   | GET    | `/api/interview/user/problems/by-no/{problemNo}`  | 按题号查详情                                     |
| F4   | GET    | `/api/interview/user/tags`                        | 获取所有标签（按热度排序）                       |
| F5   | GET    | `/api/interview/user/categories`                  | 获取分类(简单中等困难)                           |
| F6   | GET    | `/api/interview/user/favorites`                   | 获取用户收藏列表                                 |
| F7   | GET    | `/api/interview/user/history`                     | 获取用户浏览历史记录（分页，最近查看题目）       |
| F8   | DELETE | `/api/interview/user/history/{problemId}`         | 删除单条浏览历史                                 |
| F9   | DELETE | `/api/interview/user/history/clear`               | 删除全部浏览历史                                 |
| F10  | POST   | `/api/interview/user/favorites`                   | 收藏题目                                         |
| F11  | DELETE | `/api/interview/user/favorites/{problemId}`       | 取消收藏                                         |
| F12  | DELETE | `/api/interview/user/favorites/clear`             | 一键清空当前用户所有收藏题目                     |
| F13  | POST   | `/api/interview/user/problems/{id}/like`          | 题目点赞                                         |
| F14  | POST   | `/api/interview/user/problems/{id}/dislike`       | 题目点踩                                         |
| F15  | GET    | `/api/interview/user/favorites/{problemId}/check` | 查询某题是否已收藏                               |
| F16  | GET    | `/api/interview/user/stats`                       | 前台统计（题目阅读次数 题目收藏 题目点赞 点踩 ） |
| F17  | GET    | `/api/interview/user/search`                      | 题目模糊搜索(题目)                               |

---

## 三、后台管理接口详解

所有接口统一前缀：**/api/interview/admin/\***，需管理员登录授权后才可访问

###  B1 \- 题目列表（管理后台）

**GET**`/api/interview/admin/problems`

| 参数       | 类型    | 必填 | 说明                                                |
| ---------- | ------- | ---- | --------------------------------------------------- |
| keyword    | String  | 否   | 搜索：标题/描述/标签模糊匹配                        |
| difficulty | String  | 否   | 难度：easy / medium / hard                          |
| category   | String  | 否   | 分类：数组/链表/树/图/动态规划/数学/其他            |
| status     | String  | 否   | ACTIVE / INACTIVE，空=全部                          |
| isFrequent | Integer | 否   | 1=只看高频，0=只看非高频                            |
| page       | Integer | 否   | 页码，默认 1                                        |
| pageSize   | Integer | 否   | 每页条数，默认 20，最大 200                         |
| sortBy     | String  | 否   | 排序：id（默认）/ problemNo / viewCount / createdAt |
| order      | String  | 否   | desc（默认）/ asc                                   |

**响应示例（200 OK）：**

```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "list": [
      {
        "id": 1,
        "problemNo": "MS001",
        "title": "两数之和",
        "difficulty": "easy",
        "difficultyLabel": "简单",
        "tags": "数组,哈希表",
        "tagList": ["数组", "哈希表"],
        "category": "数组",
        "status": "ACTIVE",
        "isFrequent": 1,
        "viewCount": 1280,
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2026-08-06 10:00:00"
      }
    ],
    "total": 128,
    "page": 1,
    "pageSize": 20,
    "totalPages": 7
  },
  "code": 200
}
```

---

###  B2 \- 题目详情（管理后台）

**GET**`/api/interview/admin/problems/{id}`

| 参数 | 类型 | 必填 | 说明       |
| ---- | ---- | ---- | ---------- |
| id   | Long | 是   | 题目主键ID |

**响应示例（200 OK）：**

```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "id": 1,
    "problemNo": "MS001",
    "title": "两数之和",
    "difficulty": "easy",
    "difficultyLabel": "简单",
    "tags": "数组,哈希表",
    "tagList": ["数组", "哈希表"],
    "category": "数组",
    "description": "## 题目描述\n给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target 的那 两个 整数，并返回它们的数组下标。",
    "inputFormat": "## 输入格式\n第一行输入数组长度，第二行输入数组元素，第三行输入目标值",
    "outputFormat": "## 输出格式\n输出两个满足条件的数组下标，空格分隔",
    "solution": "## 题解\n利用哈希表存储遍历过的元素和下标，一次遍历即可求解，时间复杂度O(n)。",
    "status": "ACTIVE",
    "isFrequent": 1,
    "viewCount": 1280,
    "createdBy": "admin",
    "updatedBy": "admin",
    "createdAt": "2026-08-06 10:00:00"
  },
  "code": 200
}
```

---

###  B3 \- 手动添加单道题（管理后台）

**POST**`/api/interview/admin/problems`

| 参数         | 类型    | 必填 | 说明                             |
| ------------ | ------- | ---- | -------------------------------- |
| problemNo    | String  | 否   | 题目唯一编号，格式如 MS001；为空时系统自动分配（见第六节题号分配策略） |
| title        | String  | 是   | 题目标题                         |
| difficulty   | String  | 是   | 难度：easy / medium / hard       |
| category     | String  | 否   | 题目分类：数组/链表/树等         |
| tags         | String  | 否   | 题目标签，多个标签逗号分隔       |
| description  | String  | 否   | 题目描述，支持Markdown格式       |
| inputFormat  | String  | 否   | 输入格式说明，支持Markdown格式   |
| outputFormat | String  | 否   | 输出格式说明，支持Markdown格式   |
| solution     | String  | 是   | 题目题解与答案，支持Markdown格式 |
| isFrequent   | Integer | 否   | 是否高频题：1=是，0=否，默认0    |

**响应示例（200 OK）：**

```json
{
  "success": true,
  "message": "新增题目成功",
  "data": {
    "id": 1,
    "problemNo": "MS001"
  },
  "code": 200
}
```

---

###  B4 \- 修改题目（管理后台）

**PUT**`/api/interview/admin/problems/{id}`

| 参数         | 类型    | 必填 | 说明                                |
| ------------ | ------- | ---- | ----------------------------------- |
| id           | Long    | 是   | 待修改题目主键ID                    |
| problemNo    | String  | 是   | 题目唯一编号                        |
| title        | String  | 是   | 题目标题                            |
| difficulty   | String  | 是   | 难度：easy / medium / hard          |
| category     | String  | 否   | 题目分类                            |
| tags         | String  | 否   | 题目标签，逗号分隔                  |
| description  | String  | 否   | 题目描述（Markdown）                |
| inputFormat  | String  | 否   | 输入格式（Markdown）                |
| outputFormat | String  | 否   | 输出格式（Markdown）                |
| solution     | String  | 是   | 题目题解答案（Markdown）            |
| status       | String  | 是   | 题目状态：ACTIVE上线 / INACTIVE下线 |
| isFrequent   | Integer | 是   | 是否高频：1=是，0=否                |

**响应示例（200 OK）：**

```json
{
  "success": true,
  "message": "修改题目成功",
  "data": null,
  "code": 200
}
```

---

###  B5 \- 切换题目上线/下线状态（管理后台）

**PUT**`/api/interview/admin/problems/{id}/status`

| 参数   | 类型   | 必填 | 说明                       |
| ------ | ------ | ---- | -------------------------- |
| id     | Long   | 是   | 题目主键ID                 |
| status | String | 是   | ACTIVE=上线，INACTIVE=下线 |

**响应示例（200 OK）：**

```json
{
  "success": true,
  "message": "状态修改成功",
  "data": null,
  "code": 200
}
```

---

###  B6 \- 题目逻辑删除（管理后台）

**DELETE**`/api/interview/admin/problems/{id}`

| 参数 | 类型 | 必填 | 说明                             |
| ---- | ---- | ---- | -------------------------------- |
| id   | Long | 是   | 题目主键ID，逻辑删除，数据可恢复 |

**响应示例（200 OK）：**

```json
{
  "success": true,
  "message": "删除成功",
  "data": null,
  "code": 200
}
```

---

###  B7 \- 批量逻辑删除题目（管理后台）

**DELETE**`/api/interview/admin/problems/batch`

| 参数 | 类型         | 必填 | 说明                                           |
| ---- | ------------ | ---- | ---------------------------------------------- |
| ids  | List\<Long\> | 是   | 题目ID数组，通过 RequestBody 以 JSON 数组格式传递，批量逻辑删除 |

**响应示例（200 OK）：**

```json
{
  "success": true,
  "message": "批量删除成功",
  "data": null,
  "code": 200
}
```

---

###  B8 \- 题目物理删除（慎用）（管理后台）

**DELETE**`/api/interview/admin/problems/real/{id}`

| 参数 | 类型 | 必填 | 说明                                   |
| ---- | ---- | ---- | -------------------------------------- |
| id   | Long | 是   | 题目主键ID，永久物理删除，数据不可恢复 |

**响应示例（200 OK）：**

```json
{
  "success": true,
  "message": "永久删除成功",
  "data": null,
  "code": 200
}
```

---

###  B9 \- 批量物理删除题目（慎用）（管理后台）

**DELETE**`/api/interview/admin/problems/real/batch`

| 参数 | 类型         | 必填 | 说明                                                   |
| ---- | ------------ | ---- | ------------------------------------------------------ |
| ids  | List\<Long\> | 是   | 题目ID数组，通过 RequestBody 以 JSON 数组格式传递，批量永久物理删除，不可恢复 |

**响应示例（200 OK）：**

```json
{
  "success": true,
  "message": "批量永久删除成功",
  "data": null,
  "code": 200
}
```

---

###  B10 \- JSON批量导入题目（管理后台）

**POST**`/api/interview/admin/problems/batch-import`

| 参数        | 类型               | 必填 | 说明                                 |
| ----------- | ------------------ | ---- | ------------------------------------ |
| problemList | List\<ProblemDTO\> | 是   | 题目数据列表，单条字段同新增题目接口 |

**响应示例（200 OK）：**

```json
{
  "success": true,
  "message": "批量导入成功",
  "data": {
    "total": 10,
    "successNum": 10,
    "failNum": 0,
    "failList": []
  },
  "code": 200
}
```

---

###  B11 \- 上传JSON文件导入题目（管理后台）

**POST**`/api/interview/admin/problems/import-json`

| 参数 | 类型          | 必填 | 说明                                         |
| ---- | ------------- | ---- | -------------------------------------------- |
| file | MultipartFile | 是   | 上传的标准JSON格式题目文件，自动解析批量入库 |

> **文件校验规则：**
> - 文件扩展名必须为 `.json`，Content-Type 应为 `application/json`
> - 文件大小限制 10MB，超出返回 413001 错误码
> - 文件内容必须为合法 JSON 数组格式，非法 JSON 返回 400001 错误码
> - 单次导入条数上限 500 条

**响应示例（200 OK）：**

```json
{
  "success": true,
  "message": "文件导入成功",
  "data": {
    "fileName": "interview-problem.json",
    "total": 20,
    "successNum": 20,
    "failNum": 0
  },
  "code": 200
}
```

---

### B12 \- 一键导出全部题目为JSON文件（管理后台）

**GET**`/api/interview/admin/problems/export-json`

| 参数       | 类型   | 必填 | 说明                                 |
| ---------- | ------ | ---- | ------------------------------------ |
| difficulty | String | 否   | 按难度筛选导出，为空导出全部难度题目 |
| category   | String | 否   | 按分类筛选导出，为空导出全部分类题目 |

**响应说明：**接口直接返回JSON文件流，浏览器自动触发下载，文件命名格式：面试题库\_yyyyMMdd\.json

> **响应头信息：**
> - `Content-Type: application/octet-stream`
> - `Content-Disposition: attachment; filename=面试题库_yyyyMMdd.json`

---

###  B13 \- AI生成题目（同步预览）（管理后台）

**POST**`/api/interview/admin/ai/generate`

| 参数       | 类型    | 必填 | 说明                           |
| ---------- | ------- | ---- | ------------------------------ |
| category   | String  | 是   | 题目分类，如数组、算法、链表等 |
| difficulty | String  | 是   | 题目难度：easy / medium / hard |
| num        | Integer | 是   | 生成题目数量，单次最大生成10条 |

> **调用限制与超时说明：**
> - 接口超时时间：60 秒（AI 生成可能较慢，前端需设置合理的 loading 状态）
> - 调用频率限制：同一管理员每分钟最多调用 5 次，防止滥用和成本失控
> - DeepSeek 服务不可用时返回 500001 错误码，前端应展示友好提示并支持重试
> - 当前为同步生成模式，后续版本可考虑提供异步生成方案

**响应示例（200 OK）：**

```json
{
  "success": true,
  "message": "AI生成成功",
  "data": [
    {
      "title": "手写快速排序算法",
      "difficulty": "medium",
      "category": "算法",
      "tags": "排序,分治,算法基础",
      "description": "## 题目描述\n请手动实现快速排序算法，对输入的整数数组进行升序排序。",
      "solution": "## 题解\n快速排序基于分治思想，选取基准值，将数组分区后递归排序，平均时间复杂度O(nlogn)。"
    }
  ],
  "code": 200
}
```

###  B14 \- AI生成结果批量保存题库（管理后台）

**POST**`/api/interview/admin/problems/batch-save`

| 参数        | 类型                 | 必填 | 说明                                   |
| ----------- | -------------------- | ---- | -------------------------------------- |
| problemList | List\<AiProblemDTO\> | 是   | AI预览生成的题目数据列表，批量入库保存 |

**响应示例（200 OK）：**

```json
{
  "success": true,
  "message": "批量保存成功",
  "data": {
    "successNum": 5,
    "failNum": 0,
    "failList": []
  },
  "code": 200
}
```

---

###  B15 \- 面试题统计总览（管理后台）

**GET**`/api/interview/admin/stats`

无请求参数

> **统计维度：** 题库数量（总数/上线/下线/高频）、难度分布（简单/中等/困难）、分类标签统计、前台统计数据等。

**响应示例（200 OK）：**

```json
{
  "success": true,
  "message": "统计查询成功",
  "data": {
    "totalNum": 128,
    "activeNum": 116,
    "inactiveNum": 12,
    "frequentNum": 45,
    "easyNum": 58,
    "mediumNum": 52,
    "hardNum": 18
  },
  "code": 200
}
```

## 四、前台用户接口详解

统一接口前缀：`/api/interview/user/*`

权限说明：GET 查询类接口（F1-F5、F16-F17）无需登录；POST / DELETE 用户行为接口（收藏、历史、点赞、清空等）必须登录鉴权

---

### F1 - 查询上线题目（分页）

**GET** `/api/interview/user/problems`

|     参数     |  类型   | 必填 |          说明           |
| :----------: | :-----: | :--: | :---------------------: |
|   keyword    | String  |  否  |     搜索标题 / 标签     |
|  difficulty  | String  |  否  |  easy / medium / hard   |
|   category   | String  |  否  |          分类           |
|     tag      | String  |  否  | 标签精确匹配（单标签）  |
| onlyFrequent | Integer |  否  |       1 = 仅高频        |
|     page     | Integer |  否  |         默认 1          |
|   pageSize   | Integer |  否  | 默认 20，可选 20/50/100（前台限制 pageSize ≤ 100 以控制查询性能） |
|    sortBy    | String  |  否  |  id（默认）/ viewCount  |
|    order     | String  |  否  |       desc / asc        |

> 注意：自动过滤 status != ACTIVE 的下线题目，无需登录

**响应示例（200 OK）**

```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "list": [
      {
        "id": 1,
        "problemNo": "MS001",
        "title": "两数之和",
        "difficulty": "easy",
        "difficultyLabel": "简单",
        "tags": "数组,哈希表",
        "tagList": ["数组", "哈希表"],
        "category": "数组",
        "isFrequent": 1,
        "viewCount": 1280
      }
    ],
    "total": 110,
    "page": 1,
    "pageSize": 20,
    "totalPages": 6
  },
  "code": 200
}
```

---

### F2 - 题目详情（Markdown）

**GET** `/api/interview/user/problems/{id}`

| 参数 | 类型 | 必填 |    说明     |
| :--: | :--: | :--: | :---------: |
|  id  | Long |  是  | 题目主键 ID |

> 注意：无需登录；访问自动累加阅读量；仅返回上线题目

**响应示例（200 OK）**

```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "id": 1,
    "problemNo": "MS001",
    "title": "两数之和",
    "difficulty": "easy",
    "difficultyLabel": "简单",
    "tags": "数组,哈希表",
    "tagList": ["数组", "哈希表"],
    "category": "数组",
    "description": "## 题目描述\n给定一个整数数组 nums 和一个整数目标值 target...",
    "inputFormat": "## 输入格式\n第一行输入数组长度...",
    "outputFormat": "## 输出格式\n输出两个下标...",
    "solution": "## 题解\n哈希表解法...",
    "isFrequent": 1,
    "viewCount": 1280,
    "likeCount": 320,
    "dislikeCount": 12
  },
  "code": 200
}
```

---

### F3 - 按题号查详情

**GET** `/api/interview/user/problems/by-no/{problemNo}`

|   参数    |  类型  | 必填 |            说明            |
| :-------: | :----: | :--: | :------------------------: |
| problemNo | String |  是  | 题目唯一编号（MS001 格式） |

> 注意：无需登录，仅查询上线题目；访问自动累加阅读量（与 F2 行为一致）

**响应示例（200 OK）**

```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "id": 1,
    "problemNo": "MS001",
    "title": "两数之和",
    "difficulty": "easy",
    "difficultyLabel": "简单",
    "tags": "数组,哈希表",
    "tagList": ["数组", "哈希表"],
    "category": "数组",
    "description": "## 题目描述\n给定一个整数数组 nums 和一个整数目标值 target...",
    "inputFormat": "## 输入格式\n第一行输入数组长度...",
    "outputFormat": "## 输出格式\n输出两个下标...",
    "solution": "## 题解\n哈希表解法...",
    "isFrequent": 1,
    "viewCount": 1280,
    "likeCount": 320,
    "dislikeCount": 12
  },
  "code": 200
}
```

---

### F4 - 获取所有标签（按热度排序）

**GET**`/api/interview/user/tags`

无请求参数

> 注意：无需登录

**响应示例（200 OK）**

```json
{
  "success": true,
  "message": "查询成功",
  "data": [
    {
      "tagName": "数组",
      "useCount": 520
    },
    {
      "tagName": "哈希表",
      "useCount": 410
    }
  ],
  "code": 200
}
```

---

### F5 - 获取分类

**GET**`/api/interview/user/categories`

无请求参数

> 注意：无需登录

**响应示例（200 OK）**

```json
{
  "success": true,
  "message": "查询成功",
  "data": [
    "数组",
    "链表",
    "树",
    "动态规划",
    "操作系统"
  ],
  "code": 200
}
```

---

### F6 - 获取用户收藏列表

**GET** `/api/interview/user/favorites`

> 权限：需要登录鉴权

|   参数   |  类型   | 必填 |  说明   |
| :------: | :-----: | :--: | :-----: |
|   page   | Integer |  否  | 默认 1  |
| pageSize | Integer |  否  | 默认 20 |

**响应示例（200 OK）**

```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "list": [
      {
        "id": 1,
        "problemNo": "MS001",
        "title": "两数之和",
        "difficulty": "easy",
        "difficultyLabel": "简单",
        "category": "数组",
        "viewCount": 1280,
        "collectTime": "2026-08-01 14:20:00"
      }
    ],
    "total": 15,
    "page": 1,
    "pageSize": 20,
    "totalPages": 1
  },
  "code": 200
}
```

---

### F7 - 获取用户浏览历史记录（分页，最近查看题目）

**GET** `/api/interview/user/history`

> 权限：需要登录鉴权

|   参数   |  类型   | 必填 |  说明   |
| :------: | :-----: | :--: | :-----: |
|   page   | Integer |  否  | 默认 1  |
| pageSize | Integer |  否  | 默认 20 |

**响应示例（200 OK）**

```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "list": [
      {
        "id": 1,
        "problemNo": "MS001",
        "title": "两数之和",
        "difficulty": "easy",
        "difficultyLabel": "简单",
        "viewTime": "2026-08-06 09:10:00"
      }
    ],
    "total": 22,
    "page": 1,
    "pageSize": 20,
    "totalPages": 2
  },
  "code": 200
}
```

---

### F8 - 删除单条浏览历史

**DELETE** `/api/interview/user/history/{problemId}`

> 权限：需要登录鉴权

|   参数    | 类型 | 必填 |  说明   |
| :-------: | :--: | :--: | :-----: |
| problemId | Long |  是  | 题目 ID |

**响应示例（200 OK）**

```json
{
  "success": true,
  "message": "删除历史成功",
  "data": null,
  "code": 200
}
```

---

### F9 - 删除全部浏览历史

**DELETE** `/api/interview/user/history/clear`

> 权限：需要登录鉴权
>
> 无请求参数

**响应示例（200 OK）**

```json
{
  "success": true,
  "message": "清空浏览历史成功",
  "data": null,
  "code": 200
}
```

---

### F10 - 收藏题目

**POST** `/api/interview/user/favorites`

> 权限：需要登录鉴权

|   参数    | 类型 | 必填 |    说明     |
| :-------: | :--: | :--: | :---------: |
| problemId | Long |  是  | 题目主键 ID |

> 说明：同一用户重复收藏不会新增记录，直接返回成功

**响应示例（200 OK）**

```json
{
  "success": true,
  "message": "收藏成功",
  "data": null,
  "code": 200
}
```

---

### F11 - 取消收藏

**DELETE** `/api/interview/user/favorites/{problemId}`

> 权限：需要登录鉴权

|   参数    | 类型 | 必填 |  说明   |
| :-------: | :--: | :--: | :-----: |
| problemId | Long |  是  | 题目 ID |

**响应示例（200 OK）**

```json
{
  "success": true,
  "message": "取消收藏成功",
  "data": null,
  "code": 200
}
```

---

### F12 - 一键清空当前用户所有收藏题目

**DELETE** `/api/interview/user/favorites/clear`

> 权限：需要登录鉴权
>
> 无请求参数

**响应示例（200 OK）**

```json
{
  "success": true,
  "message": "清空全部收藏成功",
  "data": null,
  "code": 200
}
```

---

### F13 - 题目点赞

**POST** `/api/interview/user/problems/{id}/like`

> 权限：需要登录鉴权

| 参数 | 类型 | 必填 |    说明     |
| :--: | :--: | :--: | :---------: |
|  id  | Long |  是  | 题目主键 ID |

> 说明：同一用户只能点赞一次，重复请求不重复计数

**响应示例（200 OK）**

```json
{
  "success": true,
  "message": "点赞成功",
  "data": null,
  "code": 200
}
```

---

### F14 - 题目点踩

**POST** `/api/interview/user/problems/{id}/dislike`

> 权限：需要登录鉴权

| 参数 | 类型 | 必填 |    说明     |
| :--: | :--: | :--: | :---------: |
|  id  | Long |  是  | 题目主键 ID |

**响应示例（200 OK）**

```json
{
  "success": true,
  "message": "点踩成功",
  "data": null,
  "code": 200
}
```

---

### F15 - 查询某题是否已收藏

**GET** `/api/interview/user/favorites/{problemId}/check`

> 权限：需要登录鉴权

|   参数    | 类型 | 必填 |  说明   |
| :-------: | :--: | :--: | :-----: |
| problemId | Long |  是  | 题目 ID |

**响应示例（200 OK）**

```json
{
  "success": true,
  "message": "查询成功",
  "data": true,
  "code": 200
}
```

> data：true = 已收藏，false = 未收藏

---

### F16 - 前台统计

**GET** `/api/interview/user/stats`

> 注意：无需登录，返回全站题目阅读、收藏、点赞、踩汇总数据
>
> 无请求参数

**响应示例（200 OK）**

```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "totalView": 125600,
    "totalCollect": 3680,
    "totalLike": 9200,
    "totalDislike": 1120
  },
  "code": 200
}
```

---

### F17 - 题目模糊搜索

**GET** `/api/interview/user/search`

> 注意：无需登录，只检索上线题目
>
> 与 F1 的区别：F1 的 keyword 参数仅匹配标题和标签，F17 扩展检索范围至题干内容（description 字段），适用于全局搜索场景。

|   参数   |  类型   | 必填 |             说明             |
| :------: | :-----: | :--: | :--------------------------: |
| keyword  | String  |  是  | 关键词（标题 / 题干 / 标签） |
|   page   | Integer |  否  |            默认 1            |
| pageSize | Integer |  否  |           默认 20            |

**响应示例（200 OK）**

```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "list": [
      {
        "id": 1,
        "problemNo": "MS001",
        "title": "两数之和",
        "difficultyLabel": "简单",
        "category": "数组",
        "viewCount": 1280
      }
    ],
    "total": 3,
    "page": 1,
    "pageSize": 20,
    "totalPages": 1
  },
  "code": 200
}
```

## 五、题目字段详细规范（Markdown）

### 5.1 Markdown 字段

| 字段 | 示例内容（片段） | 前台渲染 |
|------|------------------|----------|
| description | `## 题目描述\n给定一个数组...` | Markdown → HTML |
| inputFormat | `## 输入格式\n- 第一行...` | Markdown → HTML |
| outputFormat | `## 输出格式\n- 一行整数` | Markdown → HTML |
| solution | `## 题解\n### 方法一：...\n` | Markdown → HTML（代码高亮） |

### 5.2 标签规范

- 使用**中文**（如：数组、哈希表、双指针），与前台保持一致
- 多个标签使用英文逗号 `,` 分隔，无空格
- 长度合计 ≤ 500 字符

### 5.3 安全渲染规范

> **XSS 防护要求：**
> - 后端存储题目内容前，应对 Markdown 中的原始 HTML 标签进行过滤（如使用 `jsoup` 白名单机制）
> - 前端渲染 Markdown 时，必须使用 `DOMPurify` 等库对生成的 HTML 进行净化，防止 XSS 攻击
> - 代码高亮（highlight.js）应配置 `sanitize` 选项，禁止执行内联脚本
> - 所有用户可编辑的 Markdown 字段（description、inputFormat、outputFormat、solution）均适用上述规则

### 5.4 SQL 注入防护

> 所有搜索类接口（B1 的 keyword、F1 的 keyword、F17 的 keyword）的搜索关键词均通过 MyBatis `#{}` 参数化绑定，杜绝 SQL 注入。对 LIKE 查询中的特殊字符（`%`、`_`、`\`）进行转义处理。

---

## 六、题号分配策略（problemNo）

| 场景 | 策略 |
|------|------|
| 手动新增为空 | `MS` + 「当前最大数字部分 + 1」 |
| AI 生成未指定 | `MS_AUTO_` + 时间戳 + 序号 |
| 批量导入冲突且 overwrite=false | 在原号后追加 `_v2` / `_v3` |
| 批量导入冲突且 overwrite=true | 更新原记录，id 不变 |

---

## 七、错误码与 HTTP 状态码

| HTTP 状态 | code | 说明 | 典型场景 |
|-----------|------|------|----------|
| 200 | 200 | 成功 | — |
| 400 | 400001 | 参数校验失败 | title 为空、difficulty 非法值 |
| 400 | 400002 | 题号冲突（overwrite=false 无法自动修复） | 预留场景 |
| 401 | 401001 | 未登录/登录态过期 | 后台接口 |
| 403 | 403001 | 无权限 | 普通用户访问 /admin/* |
| 404 | 404001 | 题目不存在/未上线 | 前台 F2/F3 |
| 413 | 413001 | 上传文件过大 | 导入 JSON > 10MB |
| 415 | 415001 | 不支持的文件格式 | 非 JSON |
| 500 | 500001 | AI 服务调用失败 | DeepSeek 超时/限流 |
| 500 | 500000 | 服务端内部错误 | 其他 |

**错误响应示例**：

```json
{
  "success": false,
  "message": "标题不能为空",
  "data": null,
  "code": 400001
}
```

---

## 八、权限控制说明

| 接口组 | 认证要求 | 角色 |
|--------|----------|------|
| `/api/interview/admin/*` | 必须登录（请求头携带 Token/Session） | Admin 角色：content_admin / super_admin |
| `/api/interview/user/problems*`（查询） | 无需登录（登录态下额外返回收藏/点赞状态） | 游客可访问；登录用户获得更多状态信息 |
| `/api/interview/user/favorites*` | 必须登录 | 普通用户 |
| `/api/interview/user/history*` | 必须登录 | 普通用户 |
| `/api/interview/user/problems/{id}/like` | 必须登录 | 普通用户 |
| `/api/interview/user/problems/{id}/dislike` | 必须登录 | 普通用户 |
| `/api/interview/user/stats` | 无需登录 | 游客可访问 |
| `/api/interview/user/search` | 无需登录 | 游客可访问 |
| `/api/interview/user/tags` | 无需登录 | 游客可访问 |
| `/api/interview/user/categories` | 无需登录 | 游客可访问 |

---

## 九、前端对接常见流程

### 9.1 后台：AI 生成 → 预览 → 修改→批量保存

```
1. 配置参数 → POST /api/interview/admin/ai/generate          → 拿到 problems[] 预览
2. 用户逐题修改标题/描述/题解                    → 前端暂存
3. 点击「一键入库」 → POST /api/interview/admin/problems/batch-save
   → 返回 successNum / failNum / failList
4. 列表页刷新 / 展示成功提示 Toast
```

### 9.2 前台：搜索 → 查看详情 → 收藏

```
1. GET /api/interview/user/problems?keyword=链表&difficulty=easy&pageSize=50 → 渲染列表
2. 点击题目 → GET /api/interview/user/problems/{id}   → Markdown 渲染（marked/highlight.js）
3. 点击「收藏」→ POST /api/interview/user/favorites   → 状态切换为实心星
4. 收藏夹 → GET /api/interview/user/favorites         → 按收藏时间倒序展示
```

---

## 十、接口调用示例（curl）

### 后台

#### 1. 后台题目列表查询

```bash
curl -s "http://localhost/api/interview/admin/problems?keyword=链表&pageSize=50" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```

#### 2. 后台 JSON 批量导入

```bash
curl -X POST "http://localhost/api/interview/admin/problems/batch-import" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -d '{
    "problemList": [
      { "title":"测试面试题","difficulty":"easy","tags":"数组" }
    ]
  }'
```

#### 3. AI 生成 3 道中等题

```bash
curl -X POST "http://localhost/api/interview/admin/ai/generate" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"category":"算法","difficulty":"medium","num":3}'
```

#### 4. 一键导出 JSON

```bash
curl -sS -o "面试题导出.json" \
  "http://localhost/api/interview/admin/problems/export-json" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```

---

### 前台

#### 1. 前台查询上线题目

```bash
curl -s "http://localhost/api/interview/user/problems?keyword=链表&pageSize=50" \
  -H "Content-Type: application/json"
```

#### 2. 题目详情

```bash
curl -s "http://localhost/api/interview/user/problems/1" \
  -H "Content-Type: application/json"
```

#### 3. 收藏题目（需登录）

```bash
curl -X POST "http://localhost/api/interview/user/favorites" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <USER_TOKEN>" \
  -d '{"problemId": 1}'
```

#### 4. 模糊搜索

```bash
curl -s "http://localhost/api/interview/user/search?keyword=排序&page=1&pageSize=20" \
  -H "Content-Type: application/json"
```

---

## 十一、附录

### 11.1 难度 / 标签枚举

| 字段 | 允许值 |
|------|--------|
| difficulty | `easy` / `medium` / `hard` |
| status | `ACTIVE` / `INACTIVE` |
| category | `数组` `链表` `栈` `队列` `哈希表` `字符串` `树` `堆` `图` `动态规划` `贪心` `回溯` `二分` `位运算` `数学` `设计` `其他` |

### 11.2 数据库表结构设计

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `interview_problem` | 题目主表 | id, problem_no, title, difficulty, category, tags, description, input_format, output_format, solution, status, is_frequent, view_count, is_deleted, created_by, updated_by, created_at, updated_at |
| `interview_favorite` | 收藏关系表 | id, user_id, problem_id, collect_time（联合唯一索引：user_id + problem_id） |
| `interview_history` | 浏览历史表 | id, user_id, problem_id, view_time（索引：user_id + view_time） |
| `interview_like` | 点赞点踩表 | id, user_id, problem_id, type(like/dislike), created_at（联合唯一索引：user_id + problem_id） |

> **关键索引建议：**
> - `interview_problem`：`status` + `category` + `difficulty` 联合索引（前台筛选查询）
> - `interview_problem`：`title` 索引（关键词搜索）
> - `interview_favorite`：`user_id` + `collect_time` 联合索引（收藏列表分页）
> - `interview_history`：`user_id` + `view_time` 联合索引（历史记录分页）

### 11.3 测试 / 验证入口

| 工具 | 地址 |
|------|------|
| Knife4j / Swagger UI | `http://localhost/doc.html#/面试题目模块` |
| OpenAPI JSON | `http://localhost/v3/api-docs` |

### 11.4 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0 | 2026-08-06 | 初版：后台 15 个 + 前台 17 个接口 |
| v1.1 | 2026-08-07 | 文档审查修订：修复参数名不一致、格式排版、权限表补全、安全说明补充 |
