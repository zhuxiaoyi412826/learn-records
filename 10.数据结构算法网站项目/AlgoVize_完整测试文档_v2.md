# AlgoVize 算法数据结构可视化平台 - 完整测试文档 V2

> **版本**: v2.0  
> **生成日期**: 2026-08-20  
> **基于接口文档**: `doc/json/default_OpenAPI.json`  
> **测试脚本**: `doc/tools/test_api_v2.py`

---

## 目录
- [1. 项目概述与测试环境](#1-项目概述与测试环境)
- [2. 前台操作测试（用户端）](#2-前台操作测试用户端)
- [3. 后台管理测试（管理员端）](#3-后台管理测试管理员端)
- [4. CMD 命令行 API 测试](#4-cmd-命令行-api-测试)
- [5. 测试数据准备与 SQL 脚本](#5-测试数据准备与-sql-脚本)
- [6. 项目不足与后续优化方案](#6-项目不足与后续优化方案)
- [7. 自动化测试脚本说明](#7-自动化测试脚本说明)

---

## 1. 项目概述与测试环境

### 1.1 技术栈
| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.x + MyBatis + MySQL 8.0 |
| 前端前台 | 原生 HTML/CSS/JavaScript |
| 前端后台 | Vue 3 + Element Plus |
| 数据库 | MySQL 8.0 |
| 其他 | Redis、Elasticsearch、Chroma |

### 1.2 核心模块
| 模块 | 说明 | 控制器 |
|------|------|--------|
| 用户系统 | 登录、注册、信息管理 | UserController, AdminAuthController |
| OJ 题库 | 题目浏览、搜索、排序、分页 | OJProblemController, AdminContentController |
| 题解系统 | 题解发布、评论、点赞、浏览量 | OJSolutionController, OJSolutionCommentController |
| 代码运行 | 在线判题、代码执行 | SubmissionController |
| 内容审核 | 敏感词检测、人工审核 | AuditController, SensitiveWordController |
| 后台管理 | 用户/题目/审核/系统管理 | 多个 Admin*Controller |
| AI 功能 | AI 聊天、向量搜索 | AIChatController, VectorSearchController |
| 支付系统 | 硬币购买、订单管理 | PaymentController, AdminPaymentController |
| 面试系统 | 面试题 CRUD、向量/ES 搜索 | InterviewProblemController |

### 1.3 测试环境要求
| 组件 | 要求 | 地址 |
|------|------|------|
| JDK | 17+ | - |
| Node.js | 16+ | - |
| MySQL | 8.0+ | localhost:3306 |
| Redis | 6.0+ | localhost:6379 |
| Elasticsearch | 8.x | localhost:9200 |
| Milvus | 2.x | localhost:19530 |
| 后端服务 | Spring Boot | http://localhost:80 |
| 后台前端 | Vue 3 Dev Server | http://localhost:5000 |
| 前台页面 | Nginx / Dev Server | http://localhost:5500 |

### 1.4 测试账号
| 账号类型 | 用户名 | 密码 | 用途 |
|---------|--------|------|------|
| 普通用户 | testuser | 123456 | 前台功能测试 |
| 普通用户 | testuser2 | 123456 | 多用户/并发测试 |
| 管理员 | admin | admin123 | 后台管理测试 |

---

## 2. 前台操作测试（用户端）

### 2.1 首页与导航

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| F-001 | 首页加载 | 访问 `http://localhost:80` | 页面正常加载，展示主要功能入口（题库、题解、面试、AI等） | P0 |
| F-002 | 导航菜单跳转 | 点击"在线OJ"导航项 | 跳转至 `oj-list.html` 题目列表页 | P0 |
| F-003 | 返回首页 | 点击"返回首页"按钮 | 返回首页，URL 变为 `/index.html` | P1 |
| F-004 | 未登录访问权限页 | 未登录状态点击"我的" | 跳转登录页 | P0 |
| F-005 | 公告展示 | 首页公告栏区域 | 显示已发布公告列表 | P2 |

### 2.2 用户登录/注册

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| F-101 | 用户注册 | 填写用户名、密码、邮箱提交注册 | 注册成功，提示"注册成功"并跳转登录页 | P0 |
| F-102 | 用户登录成功 | 输入正确的用户名密码登录 | 登录成功，显示用户信息（昵称、头像、硬币数） | P0 |
| F-103 | 登录失败（密码错误） | 输入错误密码登录 | 显示"用户名或密码错误" | P0 |
| F-104 | 登录失败（用户不存在） | 输入不存在的用户名登录 | 显示"用户不存在" | P1 |
| F-105 | 退出登录 | 点击右上角退出按钮 | 退出成功，清除登录状态，跳转首页 | P0 |
| F-106 | 登录状态保持 | 刷新页面 | 登录状态保持（Token 存储在 localStorage） | P1 |
| F-107 | 重复注册检测 | 注册已存在的用户名 | 提示"用户名已存在" | P1 |
| F-108 | 注册参数校验 | 用户名为空/密码过短 | 前端提示校验错误，不发送请求 | P1 |

### 2.3 OJ 题库功能

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| F-201 | 题目列表加载 | 访问在线 OJ 题库页面 | 显示题目列表，支持分页（默认 20 条/页） | P0 |
| F-202 | 分页切换 | 点击下一页/上一页/跳转页码 | 正确切换页面，数据正确，URL 参数同步 | P0 |
| F-203 | 搜索题号 | 输入题号（如 "2006"）搜索 | 显示匹配该题号的题目 | P0 |
| F-204 | 搜索标题 | 输入题目关键词（如 "两数之和"） | 显示标题包含关键词的题目 | P0 |
| F-205 | 搜索标签 | 输入标签名（如 "数组"） | 显示包含该标签的题目 | P1 |
| F-206 | 难度筛选 | 选择难度筛选（简单/中等/困难） | 只显示对应难度的题目 | P0 |
| F-207 | ID 升序排序 | 点击"ID升序"按钮 | 题目按 ID 从小到大排列 | P0 |
| F-208 | ID 降序排序 | 点击"ID降序"按钮 | 题目按 ID 从大到小排列 | P0 |
| F-209 | 时间升序排序 | 点击"时间升序"按钮 | 题目按创建时间从早到晚排列 | P0 |
| F-210 | 时间降序排序 | 点击"时间降序"按钮 | 题目按创建时间从晚到早排列 | P0 |
| F-211 | 查看题目详情 | 点击题目标题 | 跳转题目详情页，显示题目描述、输入输出、示例 | P0 |
| F-212 | 组合筛选 | 同时设置搜索词+难度筛选 | 返回同时满足两个条件的题目 | P1 |
| F-213 | 空搜索结果 | 搜索不存在的关键词 | 显示"暂无数据"空状态提示 | P1 |

### 2.4 代码运行功能

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| F-221 | 代码编辑器加载 | 进入题目详情页 | 代码编辑器正常加载，默认加载 Java 模板 | P0 |
| F-222 | 语言切换 | 切换代码语言（Java/Python/C++） | 编辑器切换语言，加载对应模板 | P0 |
| F-223 | 代码提交 | 编写代码后点击"提交" | 代码提交成功，返回运行结果 | P0 |
| F-224 | 代码运行（不提交） | 编写代码后点击"运行" | 仅运行代码（不计入提交记录），显示结果 | P1 |
| F-225 | 运行结果展示 | 提交后查看结果 | 显示通过/答案错误/超时等状态 | P0 |
| F-226 | 历史提交记录 | 查看提交记录标签页 | 显示当前用户的历史提交 | P1 |

### 2.5 题解功能

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| F-301 | 题解列表加载 | 访问题解列表 | 显示所有已通过审核的题解，按创建时间排列 | P0 |
| F-302 | 题解详情 | 点击题解标题 | 显示题解详情（标题、思路、过程、代码、评论） | P0 |
| F-303 | 发布题解 | 填写题解表单提交 | 题解发布成功，进入审核状态 | P0 |
| F-304 | 发布空内容校验 | 发布空标题/空代码 | 提示"内容不能为空"，阻止提交 | P0 |
| F-305 | 敏感词过滤-发布题解 | 发布包含"赌博网站"的题解 | 被拦截（BLOCK），不存储到数据库 | P0 |
| F-306 | 敏感词过滤-中危词 | 发布包含"刷单返利"的题解 | 被拦截（BLOCK）或标记待审核 | P0 |
| F-307 | 浏览量统计 | 访问题解详情页 | 浏览量 +1，小眼睛图标数字增加 | P0 |
| F-308 | 点赞功能 | 点击点赞按钮 | 点赞数 +1，按钮状态切换为已点赞 | P0 |
| F-309 | 取消点赞 | 再次点击点赞按钮 | 点赞数 -1，按钮状态恢复 | P1 |
| F-310 | 点赞幂等性 | 快速连续点击 | 点赞数正确，不出现重复计数 | P1 |
| F-311 | 评论列表加载 | 查看题解下的评论 | 显示评论列表，支持树形结构 | P0 |
| F-312 | 发布评论 | 填写评论内容提交 | 评论发布成功，进入审核状态 | P0 |
| F-313 | 回复评论 | 点击回复按钮回复某条评论 | 回复成功，显示回复关系（parentId/rootId） | P0 |
| F-314 | 评论敏感词过滤 | 发布包含敏感词的评论 | 被拦截或标记审核 | P0 |
| F-315 | 评论滚动 | 在详情页滚动评论区 | 评论区可独立滚动，不影响题解内容区 | P1 |
| F-316 | 题解代码高亮 | 查看含代码的题解 | 代码段正确高亮显示 | P2 |
| F-317 | 匿名浏览 | 未登录访问题解 | 可查看题解详情，但无法点赞/评论 | P1 |
| F-318 | 我的题解 | 登录用户查看自己发布的题解 | 显示当前用户的题解列表及审核状态 | P1 |

### 2.6 搜索功能（题解）

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| F-321 | 关键词搜索 | 输入关键词搜索题解 | 返回匹配的题解列表 | P1 |
| F-322 | AI 语义搜索 | 点击"AI 搜索"按钮 | 调用向量数据库，返回语义相似的题解 | P2 |
| F-323 | ES 分词搜索 | 点击"ES 分词搜索"按钮 | 调用 Elasticsearch，返回关键词匹配的题解 | P2 |

### 2.7 用户中心

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| F-401 | 个人信息展示 | 登录后进入个人中心 | 显示昵称、头像、硬币数、注册时间 | P0 |
| F-402 | 修改昵称 | 修改昵称后保存 | 昵称更新成功，页面立即刷新 | P1 |
| F-403 | 修改头像 | 上传新头像 | 头像上传成功，显示新头像 | P1 |
| F-404 | 硬币商品浏览 | 进入硬币商城 | 显示所有在售硬币商品 | P1 |
| F-405 | 购买硬币 | 点击购买按钮完成支付 | 硬币余额增加，生成订单记录 | P1 |
| F-406 | 订单查询 | 查看购买订单 | 显示历史订单列表及状态 | P1 |

### 2.8 面试题功能（前台）

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| F-501 | 面试题列表 | 访问面试题页面 | 显示面试题列表，支持分类筛选 | P1 |
| F-502 | 面试题详情 | 点击面试题 | 显示题目详情（Markdown 渲染） | P1 |
| F-503 | 向量语义搜索 | 输入搜索词点击 AI 搜索 | 返回语义相关的面试题 | P1 |
| F-504 | 浏览量自增 | 访问面试题详情 | 浏览量 +1 | P2 |

### 2.9 AI 聊天功能

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| F-601 | AI 对话 | 在 AI 聊天页发送消息 | AI 返回回答，流式输出 | P2 |
| F-602 | 上下文对话 | 连续多轮对话 | AI 正确理解上下文 | P2 |
| F-603 | 代码解释 | 发送代码请求解释 | AI 返回代码解释 | P2 |

---

## 3. 后台管理测试（管理员端）

### 3.1 管理员登录

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| B-001 | 管理员登录 | 访问 `http://localhost:5000`，输入管理员账号密码 | 登录成功，进入后台首页 | P0 |
| B-002 | 登录失败 | 输入错误密码 | 显示"用户名或密码错误" | P0 |
| B-003 | 权限校验 | 普通用户 Token 访问后台接口 | 返回 403 或权限不足 | P0 |
| B-004 | 登录日志记录 | 管理员登录后查看登录日志 | 登录成功日志记录到 `login_log` 表 | P1 |
| B-005 | 登录失败锁定 | 连续错误登录 5 次 | 账号暂时锁定，需等待或联系管理员 | P2 |

### 3.2 数据统计大屏

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| B-011 | 用户总数统计 | 查看首页统计卡片 | 显示正确的用户总数 | P1 |
| B-012 | 题目总数统计 | 查看题目统计卡片 | 显示正确的题目总数 | P1 |
| B-013 | 提交总数统计 | 查看提交统计卡片 | 显示正确的提交总数 | P1 |
| B-014 | 活跃用户统计 | 查看日活跃用户 | 显示今日活跃用户数 | P2 |
| B-015 | 趋势图表 | 查看用户增长趋势图 | 图表正确渲染，数据与数据库一致 | P2 |

### 3.3 用户管理

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| B-101 | 用户列表加载 | 访问用户管理页面 | 显示用户列表，支持分页（默认 100 条/页） | P0 |
| B-102 | 关键词搜索 | 输入用户名/邮箱搜索 | 显示匹配的用户 | P0 |
| B-103 | 性别筛选 | 按性别筛选 | 只显示对应性别的用户 | P1 |
| B-104 | 账号状态筛选 | 按状态筛选（正常/封禁） | 只显示对应状态的用户 | P1 |
| B-105 | 封禁用户 | 点击"封禁"按钮 | 用户状态变为"封禁"，该用户无法登录 | P0 |
| B-106 | 解封用户 | 点击"解封"按钮 | 用户状态恢复"正常"，可正常登录 | P0 |
| B-107 | 删除用户 | 点击"删除"按钮确认 | 用户被删除（逻辑删除或物理删除） | P1 |
| B-108 | 用户详情查看 | 点击用户详情按钮 | 显示用户详细信息 | P1 |
| B-109 | 用户数量统计 | 调用 `/api/admin/user/count` | 返回正确的用户数量 | P1 |

### 3.4 题目管理

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| B-201 | 题目列表（含禁用） | 访问题目管理页面 | 显示所有题目（含 ACTIVE 和 INACTIVE 状态） | P0 |
| B-202 | 添加题目 | 填写题目表单提交 | 题目添加成功，状态为 INACTIVE | P0 |
| B-203 | 编辑题目 | 修改题目信息保存 | 题目信息更新成功 | P0 |
| B-204 | 删除题目 | 删除指定题目 | 题目被删除 | P1 |
| B-205 | 上线题目 | 切换状态为 ACTIVE | 题目在前台可见 | P0 |
| B-206 | 下线题目 | 切换状态为 INACTIVE | 题目在前台不可见 | P0 |
| B-207 | ID 排序 | 点击 ID 排序按钮 | 按 ID 升序/降序排列 | P0 |
| B-208 | 时间排序 | 点击时间排序按钮 | 按创建时间升序/降序排列 | P0 |
| B-209 | 难度筛选 | 按难度筛选题目 | 只显示对应难度的题目 | P1 |
| B-210 | 题目总数核对 | 后台数量 vs 前台数量 | 后台显示所有题目，前台仅显示 ACTIVE 题目 | P0 |

### 3.5 提交管理

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| B-221 | 提交列表加载 | 访问提交管理页面 | 显示所有提交记录 | P1 |
| B-222 | 按题目筛选 | 按题目 ID 筛选提交 | 只显示该题目的提交 | P1 |
| B-223 | 按状态筛选 | 按 AC/WA/TLE 等状态筛选 | 只显示对应状态的提交 | P1 |
| B-224 | 删除提交 | 删除指定提交记录 | 提交记录被删除 | P2 |
| B-225 | 用户提交统计 | 查看用户提交统计 | 显示总提交数、AC 数、AC 率 | P2 |

### 3.6 内容审核 - 题解审核

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| B-301 | 题解审核列表 | 访问"题解审核"标签页 | 显示待审核/已处理的题解列表 | P0 |
| B-302 | 审核状态筛选 | 按状态筛选（待审核/已通过/已驳回/已拦截） | 只显示对应状态的题解 | P0 |
| B-303 | 审核通过 | 点击"通过"按钮 | 题解状态变为"已通过"，前台可见 | P0 |
| B-304 | 审核驳回 | 点击"驳回"按钮，填写原因 | 题解状态变为"已驳回"，前台不可见 | P0 |
| B-305 | 审核拦截 | 点击"拦截"按钮 | 题解状态变为"已拦截"，前台不可见 | P1 |
| B-306 | 批量审核 | 批量选择多条题解操作 | 批量审核成功 | P2 |
| B-307 | 查看题解详情 | 点击题解标题查看内容 | 显示题解详细内容（含代码） | P0 |
| B-308 | 审核操作日志 | 审核后查看操作日志 | 审核操作记录到 operation_log 表 | P1 |

### 3.7 内容审核 - 评论审核

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| B-311 | 评论审核列表 | 切换到"评论审核"标签页 | 显示待审核/已处理的评论列表 | P0 |
| B-312 | 评论审核通过 | 点击"通过"按钮 | 评论状态变为"已通过" | P0 |
| B-313 | 评论审核驳回 | 点击"驳回"按钮 | 评论状态变为"已驳回" | P0 |
| B-314 | 按审核状态筛选 | 按状态筛选评论 | 只显示对应状态的评论 | P0 |
| B-315 | 空状态提示 | 无待审核数据时 | 表格显示"暂无数据"空状态 | P1 |

### 3.8 敏感词管理

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| B-401 | 敏感词列表加载 | 访问敏感词管理页面 | 显示敏感词列表，支持分页 | P0 |
| B-402 | 添加敏感词 | 添加新敏感词（词+等级+分类） | 添加成功，刷新列表可见 | P0 |
| B-403 | 修改敏感词 | 修改敏感词内容或等级 | 修改成功 | P1 |
| B-404 | 删除敏感词 | 删除指定敏感词 | 删除成功 | P1 |
| B-405 | 敏感词测试 | 输入文本点击测试 | 返回检测结果（是否包含敏感词及等级） | P0 |
| B-406 | 敏感词分类管理 | 查看/添加/删除分类 | 分类 CRUD 正常 | P2 |
| B-407 | 缓存刷新 | 添加/修改敏感词后刷新缓存 | 新敏感词即时生效 | P1 |

### 3.9 硬币商品管理

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| B-501 | 商品列表 | 访问硬币商品管理 | 显示所有商品（含下架） | P1 |
| B-502 | 添加商品 | 添加新硬币商品 | 商品添加成功 | P1 |
| B-503 | 编辑商品 | 修改商品信息 | 商品信息更新成功 | P1 |
| B-504 | 上架/下架 | 切换商品状态 | 商品在前端可见/不可见 | P1 |
| B-505 | 购买记录查看 | 查看商品购买记录 | 显示历史购买记录 | P2 |

### 3.10 订单/支付管理

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| B-601 | 订单列表 | 访问订单管理 | 显示所有订单（含用户信息） | P1 |
| B-602 | 订单状态筛选 | 按状态筛选（待支付/已支付/已退款） | 只显示对应状态的订单 | P1 |
| B-603 | 退款操作 | 对订单执行退款操作 | 退款状态更新，用户硬币扣回 | P1 |
| B-604 | 支付统计 | 查看支付统计页面 | 显示总金额、订单数等统计数据 | P2 |

### 3.11 系统管理

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| B-701 | 登录日志查看 | 访问登录日志页面 | 显示所有登录记录 | P1 |
| B-702 | 操作日志查看 | 访问操作日志页面 | 显示所有管理操作记录 | P1 |
| B-703 | 系统配置 | 修改系统配置项 | 配置更新成功 | P2 |
| B-704 | 日志统计 | 查看日志统计图表 | 图表正确渲染 | P2 |

### 3.12 运维监控

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| B-801 | 服务状态监控 | 查看服务状态页面 | 显示后端/前端/数据库运行状态 | P2 |
| B-802 | API 性能监控 | 查看 API 性能数据 | 显示 API 调用次数、响应时间等 | P2 |
| B-803 | API 日志查看 | 查看 API 详细日志 | 显示具体 API 调用记录 | P2 |

### 3.13 数据导出

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| B-901 | 导出用户数据 | 点击导出用户（Excel/CSV） | 下载用户数据文件 | P2 |
| B-902 | 导出日志数据 | 点击导出日志 | 下载日志数据文件 | P2 |

### 3.14 文件管理

| 编号 | 测试项 | 操作步骤 | 预期结果 | 优先级 |
|------|--------|---------|---------|--------|
| B-911 | 文件上传 | 上传图片/文档 | 文件上传成功，返回文件 URL | P1 |
| B-912 | 文件列表 | 查看已上传文件列表 | 显示所有上传的文件 | P1 |
| B-913 | 文件下载 | 点击下载文件 | 文件下载成功 | P1 |
| B-914 | 文件删除 | 删除指定文件 | 文件删除成功 | P1 |
| B-915 | 文件大小限制 | 上传超过 10MB 的文件 | 提示"文件超过大小限制" | P2 |

---

## 4. CMD 命令行 API 测试

### 4.1 基础接口测试（curl 命令）

> **前提条件**: 后端服务运行在 `http://localhost:80`，以下命令在 CMD/PowerShell 中执行。

#### 4.1.1 用户认证接口

```bash
# === 用户登录 ===
curl -X POST http://localhost:80/api/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"testuser\",\"password\":\"123456\"}"

# 预期结果:
# HTTP 200
# {"success":true,"token":"<jwt_token>","user":{"id":1,"username":"testuser",...}}

# === 用户注册 ===
curl -X POST http://localhost:80/api/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"newuser\",\"password\":\"123456\",\"email\":\"user@test.com\"}"

# 预期结果:
# HTTP 200
# {"success":true,"message":"注册成功"}

# === 获取当前用户信息 ===
curl -X GET http://localhost:80/api/user/info ^
  -H "Authorization: Bearer <token>"

# 预期结果:
# HTTP 200
# {"success":true,"data":{"id":1,"username":"testuser","nickname":"...",...}}
```

#### 4.1.2 OJ 题目接口

```bash
# === 获取题目列表（默认分页）===
curl -X GET "http://localhost:80/api/problems?page=1&size=20"

# 预期结果:
# HTTP 200
# {"success":true,"problems":[...],"total":736,"page":1,"size":20,"totalPages":37}

# === 搜索题号 ===
curl -X GET "http://localhost:80/api/problems?keyword=2006"

# 预期结果:
# HTTP 200
# {"success":true,"problems":[{"id":5,"problemNo":"2006","title":"...",...}],...}

# === 搜索标题/标签 ===
curl -X GET "http://localhost:80/api/problems?keyword=两数之和"

# 预期结果: 返回标题包含"两数之和"的题目

# === 按难度筛选 ===
curl -X GET "http://localhost:80/api/problems?difficulty=easy&page=1&size=10"

# 预期结果: 只返回 difficulty=easy 的题目

# === ID 升序排序 ===
curl -X GET "http://localhost:80/api/problems?sort=asc&sortBy=id&page=1&size=10"

# 预期结果: 题目按 ID 从小到大排列，第 1 条 ID 最小

# === ID 降序排序 ===
curl -X GET "http://localhost:80/api/problems?sort=desc&sortBy=id&page=1&size=10"

# 预期结果: 题目按 ID 从大到小排列，第 1 条 ID 最大

# === 创建时间降序 ===
curl -X GET "http://localhost:80/api/problems?sort=desc&sortBy=createdAt&page=1&size=10"

# 预期结果: 最新创建的题目排在前面

# === 题目详情 ===
curl -X GET "http://localhost:80/api/problems/1"

# 预期结果:
# HTTP 200
# {"success":true,"problem":{"id":1,"problemNo":"...","title":"...","description":"...","tags":"...",...}}

# === 后台获取所有题目（含禁用） ===
curl -X GET "http://localhost:80/api/problems/all?page=1&size=20"

# 预期结果: 包含 ACTIVE 和 INACTIVE 状态的题目
```

#### 4.1.3 题解接口

```bash
# === 获取题解列表 ===
curl -X GET "http://localhost:80/api/solutions?problemId=2"

# 预期结果:
# HTTP 200
# {"success":true,"data":[{"id":1,"title":"...","content":"...",...}]}

# === 发布题解 ===
curl -X POST http://localhost:80/api/solutions ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer <token>" ^
  -d "{\"problemId\":2,\"title\":\"两数之和解题思路\",\"idea\":\"使用哈希表\",\"process\":\"1.创建哈希表 2.遍历数组\",\"codeLang\":\"java\",\"code\":\"class Solution { ... }\"}"

# 预期结果:
# HTTP 200
# {"success":true,"solutionId":123,"auditStatus":"pending","message":"题解发布成功，等待审核"}

# === 题解详情 ===
curl -X GET "http://localhost:80/api/solutions/1"

# 预期结果:
# HTTP 200
# {"success":true,"data":{"id":1,"viewCount":15,"likeCount":3,...}}
# 注意: 每次访问 viewCount +1

# === 点赞 ===
curl -X POST http://localhost:80/api/solutions/1/like ^
  -H "Authorization: Bearer <token>"

# 预期结果:
# HTTP 200
# {"success":true,"liked":true,"likeCount":4}

# === 取消点赞（再次调用）===
curl -X POST http://localhost:80/api/solutions/1/like ^
  -H "Authorization: Bearer <token>"

# 预期结果:
# HTTP 200
# {"success":true,"liked":false,"likeCount":3}
```

#### 4.1.4 评论接口

```bash
# === 获取评论列表 ===
curl -X GET "http://localhost:80/api/comments?solutionId=1"

# 预期结果:
# HTTP 200
# {"success":true,"data":[{"id":1,"content":"...","replies":[...]}]}

# === 发布顶级评论 ===
curl -X POST http://localhost:80/api/comments ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer <token>" ^
  -d "{\"solutionId\":1,\"problemId\":2,\"content\":\"这篇题解得很好\"}"

# 预期结果:
# HTTP 200
# {"success":true,"commentId":101,"auditStatus":"pending"}

# === 回复评论 ===
curl -X POST http://localhost:80/api/comments ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer <token>" ^
  -d "{\"solutionId\":1,\"problemId\":2,\"content\":\"同意你的观点\",\"parentId\":100,\"rootId\":100}"

# 预期结果:
# HTTP 200
# {"success":true,"commentId":102,"auditStatus":"pending","replyTo":100}

# === 点赞评论 ===
curl -X POST http://localhost:80/api/comments/101/like ^
  -H "Authorization: Bearer <token>"

# 预期结果:
# {"success":true,"liked":true,"likeCount":1}
```

#### 4.1.5 内容审核接口（管理员）

```bash
# === 管理员登录 ===
curl -X POST http://localhost:80/api/admin/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"admin123\"}"

# === 获取待审核题解 ===
curl -X GET "http://localhost:80/api/admin/solutions?status=pending" ^
  -H "Authorization: Bearer <admin_token>"

# 预期结果:
# HTTP 200
# {"success":true,"data":{"list":[...],"total":N}}

# === 审核通过题解 ===
curl -X PUT http://localhost:80/api/admin/solutions/1/audit ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer <admin_token>" ^
  -d "{\"status\":\"passed\"}"

# 预期结果:
# HTTP 200
# {"success":true,"message":"审核通过"}

# === 审核驳回题解 ===
curl -X PUT http://localhost:80/api/admin/solutions/1/audit ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer <admin_token>" ^
  -d "{\"status\":\"rejected\",\"reason\":\"内容不符合要求\"}"

# 预期结果:
# HTTP 200
# {"success":true,"message":"已驳回"}

# === 获取待审核评论 ===
curl -X GET "http://localhost:80/api/admin/comments?status=pending" ^
  -H "Authorization: Bearer <admin_token>"

# 预期结果: 返回待审核评论列表
```

#### 4.1.6 敏感词接口

```bash
# === 获取敏感词列表 ===
curl -X GET http://localhost:80/api/admin/sensitive-words ^
  -H "Authorization: Bearer <admin_token>"

# 预期结果:
# HTTP 200
# {"success":true,"data":[{...},{...}]}

# === 添加敏感词 ===
curl -X POST http://localhost:80/api/admin/sensitive-words ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer <admin_token>" ^
  -d "{\"word\":\"赌博\",\"level\":\"HIGH\",\"category\":\"illegal\"}"

# 预期结果:
# HTTP 200
# {"success":true,"message":"添加成功"}

# === 测试敏感词检测 ===
curl -X POST http://localhost:80/api/admin/sensitive-words/test ^
  -H "Content-Type: application/json" ^
  -d "{\"content\":\"这是一条赌博网站信息\"}"

# 预期结果:
# HTTP 200
# {"success":true,"detected":true,"level":"HIGH","matchedWords":["赌博"],"action":"BLOCK"}

# === 刷新敏感词缓存 ===
curl -X POST http://localhost:80/api/admin/sensitive-words/refresh-cache ^
  -H "Authorization: Bearer <admin_token>"

# 预期结果:
# HTTP 200
# {"success":true,"message":"缓存已刷新"}
```

#### 4.1.7 后台用户管理接口

```bash
# === 获取用户列表 ===
curl -X GET "http://localhost:80/api/admin/user/list?page=1&pageSize=10" ^
  -H "Authorization: Bearer <admin_token>"

# 预期结果:
# {"success":true,"data":{"list":[...],"total":N}}

# === 封禁用户 ===
curl -X PUT http://localhost:80/api/admin/user/1 ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer <admin_token>" ^
  -d "{\"status\":0}"

# 预期结果: 用户状态变为封禁

# === 解封用户 ===
curl -X PUT http://localhost:80/api/admin/user/1 ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer <admin_token>" ^
  -d "{\"status\":1}"

# === 删除用户 ===
curl -X DELETE http://localhost:80/api/admin/user/1 ^
  -H "Authorization: Bearer <admin_token>"

# === 用户统计 ===
curl -X GET http://localhost:80/api/statistics/summary ^
  -H "Authorization: Bearer <admin_token>"

# === 用户趋势 ===
curl -X GET "http://localhost:80/api/statistics/trend?type=user" ^
  -H "Authorization: Bearer <admin_token>"
```

#### 4.1.8 提交管理接口

```bash
# === 获取所有提交 ===
curl -X GET http://localhost:80/api/submissions ^
  -H "Authorization: Bearer <admin_token>"

# === 按题目筛选提交 ===
curl -X GET "http://localhost:80/api/submissions?problemId=2" ^
  -H "Authorization: Bearer <admin_token>"

# === 按状态筛选提交 ===
curl -X GET "http://localhost:80/api/submissions?status=AC" ^
  -H "Authorization: Bearer <admin_token>"

# === 获取提交详情 ===
curl -X GET http://localhost:80/api/submissions/{submissionId} ^
  -H "Authorization: Bearer <admin_token>"

# === 删除提交 ===
curl -X DELETE http://localhost:80/api/submissions/{submissionId} ^
  -H "Authorization: Bearer <admin_token>"

# === 用户提交统计 ===
curl -X GET http://localhost:80/api/submissions/stats/user/1 ^
  -H "Authorization: Bearer <admin_token>"
```

#### 4.1.9 系统管理接口

```bash
# === 登录日志 ===
curl -X GET "http://localhost:80/api/system/login-log?page=1&pageSize=20" ^
  -H "Authorization: Bearer <admin_token>"

# === 登录日志统计 ===
curl -X GET http://localhost:80/api/system/login-log/stats ^
  -H "Authorization: Bearer <admin_token>"

# === 操作日志 ===
curl -X GET "http://localhost:80/api/system/operation-log?page=1&pageSize=20" ^
  -H "Authorization: Bearer <admin_token>"

# === 操作日志详情 ===
curl -X GET http://localhost:80/api/system/operation-log/1 ^
  -H "Authorization: Bearer <admin_token>"

# === 系统配置 ===
curl -X GET http://localhost:80/api/system/config ^
  -H "Authorization: Bearer <admin_token>"
```

#### 4.1.10 其他接口

```bash
# === 运维监控 ===
curl -X GET http://localhost:80/api/monitor/service ^
  -H "Authorization: Bearer <admin_token>"

# === API 性能数据 ===
curl -X GET "http://localhost:80/api/monitor/api?hours=24" ^
  -H "Authorization: Bearer <admin_token>"

# === 数据导出 ===
curl -X GET "http://localhost:80/api/export/users?format=excel" ^
  -H "Authorization: Bearer <admin_token>"

# === 公告管理 ===
curl -X GET http://localhost:80/api/extension/announcement ^
  -H "Authorization: Bearer <admin_token>"

# === 反馈管理 ===
curl -X GET http://localhost:80/api/extension/feedback ^
  -H "Authorization: Bearer <admin_token>"

# === 硬币商品列表（前端）===
curl -X GET http://localhost:80/api/payment/products

# === 面试题列表 ===
curl -X GET "http://localhost:80/api/interview/user/problems?page=1&size=20"

# === AI 聊天 ===
curl -X POST http://localhost:80/api/ai/chat ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer <token>" ^
  -d "{\"message\":\"请解释快速排序算法\"}"
```

### 4.2 命令行测试预期结果汇总表

| 接口 | 方法 | 路径 | 预期状态码 | 预期成功标识 | 备注 |
|------|------|------|-----------|-------------|------|
| 用户登录 | POST | /api/login | 200 | success=true | 返回 Token |
| 用户注册 | POST | /api/register | 200 | success=true | - |
| 获取用户信息 | GET | /api/user/info | 200 | success=true | 需 Token |
| 获取题目列表 | GET | /api/problems | 200 | success=true | 分页参数正常 |
| 搜索题号 | GET | /api/problems?keyword=2006 | 200 | success=true | 题号正确匹配 |
| ID 升序 | GET | /api/problems?sort=asc&sortBy=id | 200 | success=true | ID 递增 |
| ID 降序 | GET | /api/problems?sort=desc&sortBy=id | 200 | success=true | ID 递减 |
| 时间降序 | GET | /api/problems?sort=desc&sortBy=createdAt | 200 | success=true | 最新在前 |
| 题目详情 | GET | /api/problems/{id} | 200 | success=true | 返回完整信息 |
| 发布题解 | POST | /api/solutions | 200 | success=true | 返回 solutionId |
| 题解详情 | GET | /api/solutions/{id} | 200 | success=true | 浏览量+1 |
| 点赞题解 | POST | /api/solutions/{id}/like | 200 | success=true | liked=true/false |
| 发布评论 | POST | /api/comments | 200 | success=true | 返回 commentId |
| 回复评论 | POST | /api/comments | 200 | success=true | parentId/rootId 正确 |
| 管理员登录 | POST | /api/admin/login | 200 | success=true | 返回管理员 Token |
| 待审核题解 | GET | /api/admin/solutions?status=pending | 200 | success=true | 返回列表 |
| 审核通过 | PUT | /api/admin/solutions/{id}/audit | 200 | success=true | 状态更新 |
| 敏感词测试 | POST | /api/admin/sensitive-words/test | 200 | success=true | 检测结果正确 |
| 敏感词列表 | GET | /api/admin/sensitive-words | 200 | success=true | 返回列表 |
| 登录日志 | GET | /api/system/login-log | 200 | success=true | 分页返回 |
| 提交列表 | GET | /api/submissions | 200 | success=true | 返回列表 |
| 服务监控 | GET | /api/monitor/service | 200 | success=true | 返回状态 |
| 未授权访问 | POST | /api/solutions (无 Token) | 401/403/500 | - | ⚠️ 建议返回 401 |

---

## 5. 测试数据准备与 SQL 脚本

### 5.1 数据库测试数据统计 SQL

```sql
-- ===== 题目统计 =====
-- 题目总数
SELECT COUNT(*) AS total_problems FROM oj_problem;

-- 各状态题目数量
SELECT status, COUNT(*) AS count FROM oj_problem GROUP BY status;

-- 各难度题目数量
SELECT difficulty, COUNT(*) AS count FROM oj_problem GROUP BY difficulty;

-- 按 ID 升序验证（分页排序测试用）
SELECT id, problem_no, title FROM oj_problem WHERE status = 'ACTIVE' ORDER BY id ASC LIMIT 20;

-- 按 ID 降序验证
SELECT id, problem_no, title FROM oj_problem WHERE status = 'ACTIVE' ORDER BY id DESC LIMIT 20;

-- 按创建时间降序
SELECT id, problem_no, title, created_at FROM oj_problem WHERE status = 'ACTIVE' ORDER BY created_at DESC LIMIT 20;

-- ===== 题解统计 =====
SELECT COUNT(*) AS total_solutions FROM oj_solution;
SELECT audit_status, COUNT(*) AS count FROM oj_solution GROUP BY audit_status;

-- ===== 评论统计 =====
SELECT COUNT(*) AS total_comments FROM oj_solution_comment;
SELECT audit_status, COUNT(*) AS count FROM oj_solution_comment GROUP BY audit_status;

-- ===== 用户统计 =====
SELECT COUNT(*) AS total_users FROM user;
SELECT status, COUNT(*) AS count FROM user GROUP BY status;

-- ===== 提交统计 =====
SELECT COUNT(*) AS total_submissions FROM submission;
SELECT status, COUNT(*) AS count FROM submission GROUP BY status;
```

### 5.2 测试数据需求

| 测试场景 | 数据要求 | 准备脚本 |
|---------|---------|---------|
| 大规模性能测试 | 1000+ 用户，10000+ 评论 | `doc/tools/评论题解脚本/batch_test_data.py` |
| 敏感词测试 | 各等级敏感词 100+ 条 | `doc/tools/脚本/audit-test/` |
| 压力测试 | 100 并发请求 | `doc/tools/test_api.py` |
| 边界测试 | 极端数据（超长文本、特殊字符等） | 手动构造 |

---

## 6. 项目不足与后续优化方案

### 6.1 已完成优化 ✅

| 优化项 | 说明 | 完成时间 |
|--------|------|---------|
| OJ 题目数据库分页 | 使用 MyBatis 实现数据库层面分页+排序 | 2026-08-20 |
| 排序正确性 | 先排序后分页，支持 ID/创建时间升降序 | 2026-08-20 |
| 数据库索引 | 为排序和筛选字段创建索引 | 2026-08-20 |
| 敏感词过滤 | DFA 算法实现，支持多等级分类 | 2026-08-20 |
| 日志系统 | 同时输出控制台和文件，启动时清空 | 2026-08-20 |
| 浏览量/点赞修复 | 修复前端显示和后端计数 | 2026-08-20 |
| 评论系统 | 支持嵌套回复，敏感词过滤 | 2026-08-20 |
| 题解审核 | 后台审核页面支持全状态筛选 | 2026-08-20 |

### 6.2 待优化项 ⏳

#### 6.2.1 性能优化

| 问题 | 现状 | 优化方案 | 优先级 |
|------|------|---------|--------|
| 全文搜索响应慢 | 模糊搜索使用 `LIKE '%keyword%'` | 引入 Elasticsearch 全文检索 | P1 |
| 热点数据缓存缺失 | 每次请求都查数据库 | Redis 缓存热门题目和题解 | P1 |
| 批量操作性能 | 单条 SQL 插入 | 使用批量 INSERT 优化 | P2 |
| 数据库连接池 | 默认 HikariCP 配置 | 根据压测结果调整池大小 | P2 |
| 大数据量分页 | 深分页 `LIMIT 100000,20` 慢 | 使用游标分页（Cursor-based Pagination） | P2 |

#### 6.2.2 安全加固

| 问题 | 现状 | 优化方案 | 优先级 |
|------|------|---------|--------|
| 未授权访问返回 500 | 未登录调用接口返回 500 而非 401 | 添加全局异常处理器，返回正确的 HTTP 状态码 | P0 |
| 暴力破解防护 | 无登录次数限制 | 添加登录失败计数 + 验证码机制 | P1 |
| CSRF 防护 | 无 CSRF Token | 前后端分离添加 Token 校验 | P1 |
| XSS 防护 | 前端直接渲染用户输入 | 使用 DOMPurify 等库过滤 XSS | P1 |
| 敏感信息泄露 | 错误信息暴露堆栈 | 生产环境关闭详细错误信息 | P1 |
| 文件上传安全 | 仅校验大小 | 增加文件类型白名单校验 | P2 |
| HTTPS 强制 | HTTP 明文传输 | 生产环境强制 HTTPS | P1 |

#### 6.2.3 功能完善

| 功能 | 现状 | 建议 | 优先级 |
|------|------|------|--------|
| 代码运行语言 | 仅支持 Java | 添加 Python、C++、Go、JavaScript | P1 |
| 判题功能 | 基础判题 | 添加特殊判题（Presentation Error） | P2 |
| 用户反馈 | 基础反馈 | 添加反馈分类、图片上传、状态跟踪 | P1 |
| 移动端适配 | 响应式布局 | 开发独立小程序或 App | P2 |
| 消息通知 | 无 | 引入 WebSocket 实现实时通知 | P2 |
| 数据可视化 | 基础统计图表 | 添加更多维度的统计和趋势图 | P2 |
| 社交功能 | 无 | 添加关注、收藏、分享功能 | P3 |

#### 6.2.4 用户体验

| 问题 | 建议 | 优先级 |
|------|------|--------|
| 加载状态 | 所有异步操作显示 Loading 动画 | P1 |
| 错误提示 | 友好的 Toast 提示（中文） | P1 |
| 空状态 | 列表无数据时显示友好空状态 | P1 |
| 快捷操作 | 支持键盘快捷键操作 | P3 |
| 面包屑导航 | 提供清晰的导航路径 | P2 |
| 响应式设计 | 适配不同屏幕尺寸 | P2 |
| 暗色模式 | 支持亮色/暗色主题切换 | P3 |

#### 6.2.5 代码质量

| 问题 | 现状 | 建议 | 优先级 |
|------|------|------|--------|
| 异常处理 | 部分接口无统一异常处理 | 添加全局异常处理器 `@ControllerAdvice` | P1 |
| 参数校验 | 部分接口无参数校验 | 使用 `@Valid` + JSR-303 校验 | P1 |
| 响应格式 | 部分接口返回 `Map<String,Object>` | 统一使用 `ApiResponse<T>` 封装 | P1 |
| 日志规范 | 日志级别使用不规范 | 约定 DEBUG/INFO/WARN/ERROR 使用场景 | P1 |
| 单元测试 | 测试覆盖率低 | 添加 Service 层单元测试 | P2 |
| API 文档 | 部分接口无文档 | 完善 Swagger/Knife4j 注解 | P2 |

#### 6.2.6 架构优化

| 方向 | 说明 | 优先级 |
|------|------|--------|
| 微服务拆分 | 用户、题目、提交、审核拆分为独立服务 | P3 |
| 消息队列 | 使用 RabbitMQ/Kafka 处理异步任务（审核、通知） | P2 |
| 服务监控 | 引入 Prometheus + Grafana | P2 |
| 容器化 | Docker + Kubernetes 编排部署 | P1 |
| CI/CD | GitLab CI / GitHub Actions 自动化部署 | P1 |
| 接口版本化 | API 路径加 `/v1/` 前缀 | P2 |

---

## 7. 自动化测试脚本说明

### 7.1 脚本概述

| 项目 | 说明 |
|------|------|
| 脚本路径 | `doc/tools/test_api_v2.py` |
| 框架 | pytest + requests |
| 覆盖范围 | 认证、题目、题解、评论、审核、性能、安全 |
| 报告生成 | 自动生成测试报告 |

### 7.2 运行方法

```bash
# 安装依赖
pip install requests pytest pytest-html

# 运行所有测试
python doc/tools/test_api_v2.py

# 运行指定测试类
python -m pytest doc/tools/test_api_v2.py -k TestOJProblem

# 显示详细输出
python -m pytest doc/tools/test_api_v2.py -v

# 生成 HTML 报告
python -m pytest doc/tools/test_api_v2.py --html=report.html
```

### 7.3 测试用例映射

| 测试类 | 覆盖的文档编号 | 说明 |
|--------|--------------|------|
| `TestAuth` | F-101 ~ F-108 | 认证相关测试 |
| `TestOJProblem` | F-201 ~ F-213 | OJ 题库测试 |
| `TestOJSolution` | F-301 ~ F-308 | 题解功能测试 |
| `TestComment` | F-309 ~ F-315 | 评论功能测试 |
| `TestAudit` | B-301 ~ B-315 | 内容审核测试 |
| `TestSensitiveWord` | F-305, B-401 ~ B-407 | 敏感词测试 |
| `TestPerformance` | - | 性能测试 |
| `TestSecurity` | - | 安全性测试 |

### 7.4 持续集成

将自动化测试集成到 CI/CD 流水线：

```yaml
# .gitlab-ci.yml / GitHub Actions
stages:
  - test
  - report

api_test:
  stage: test
  script:
    - pip install requests pytest
    - python doc/tools/test_api_v2.py --junitxml=report.xml
  artifacts:
    reports:
      junit: report.xml
```

---

## 附录 A: 接口清单（基于 OpenAPI.json）

### A.1 用户模块
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/login | 用户登录 |
| POST | /api/register | 用户注册 |
| GET | /api/user/info | 获取当前用户信息 |
| PUT | /api/user | 更新用户信息 |
| GET | /api/users | 获取用户列表 |
| GET | /api/users/{id} | 获取用户详情 |
| PUT | /api/users/{id} | 更新用户 |
| DELETE | /api/users/{id} | 删除用户 |
| PUT | /api/users/{id}/status | 更新用户状态 |

### A.2 OJ 题目模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/problems | 获取题目列表（分页） |
| GET | /api/problems/all | 获取所有题目（含禁用） |
| GET | /api/problems/{id} | 获取题目详情 |
| POST | /api/problems | 创建题目 |
| PUT | /api/problems/{id} | 更新题目 |
| DELETE | /api/problems/{id} | 删除题目 |

### A.3 题解模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/solutions | 获取题解列表 |
| GET | /api/solutions/{id} | 获取题解详情 |
| POST | /api/solutions | 发布题解 |
| PUT | /api/solutions/{id} | 更新题解 |
| DELETE | /api/solutions/{id} | 删除题解 |
| POST | /api/solutions/{id}/like | 点赞/取消点赞 |

### A.4 评论模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/comments | 获取评论列表 |
| POST | /api/comments | 发布评论 |
| POST | /api/comments/{id}/like | 点赞评论 |
| DELETE | /api/comments/{id} | 删除评论 |

### A.5 审核模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/admin/solutions | 审核题解列表 |
| PUT | /api/admin/solutions/{id}/audit | 审核题解 |
| GET | /api/admin/comments | 审核评论列表 |
| PUT | /api/admin/comments/{id}/audit | 审核评论 |

### A.6 敏感词模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/admin/sensitive-words | 获取敏感词列表 |
| POST | /api/admin/sensitive-words | 添加敏感词 |
| PUT | /api/admin/sensitive-words/{id} | 修改敏感词 |
| DELETE | /api/admin/sensitive-words/{id} | 删除敏感词 |
| POST | /api/admin/sensitive-words/test | 测试敏感词检测 |
| POST | /api/admin/sensitive-words/refresh-cache | 刷新缓存 |

### A.7 系统管理模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/login-log | 登录日志 |
| GET | /api/system/login-log/stats | 登录日志统计 |
| GET | /api/system/operation-log | 操作日志 |
| GET | /api/system/operation-log/{id} | 操作日志详情 |
| GET | /api/system/config | 系统配置 |
| PUT | /api/system/config | 更新配置 |

### A.8 运维监控模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/monitor/service | 服务状态 |
| GET | /api/monitor/alerts | 告警列表 |
| GET | /api/monitor/api | API 性能数据 |
| GET | /api/monitor/api/hourly | 每小时 API 统计 |
| GET | /api/monitor/api/daily | 每日 API 统计 |
| GET | /api/monitor/api/logs | API 日志 |
| DELETE | /api/monitor/api/logs/clean | 清理 API 日志 |

### A.9 数据导出模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/export/users | 导出用户数据 |
| GET | /api/export/logs | 导出日志数据 |

### A.10 其他模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/statistics/trend | 用户趋势 |
| GET | /api/statistics/summary | 用户统计 |
| GET | /api/submissions | 提交列表 |
| POST | /api/ai/chat | AI 聊天 |
| GET | /api/interview/user/problems | 面试题列表 |

---

## 附录 B: 错误码说明

| 状态码 | 说明 | 处理建议 |
|--------|------|---------|
| 200 | 成功 | 正常处理 |
| 400 | 请求参数错误 | 检查请求参数 |
| 401 | 未登录/Token 过期 | 重新登录 |
| 403 | 无权限 | 联系管理员授权 |
| 404 | 资源不存在 | 检查资源 ID |
| 500 | 服务器内部错误 | 查看后端日志 |
| 1001 | 参数校验失败 | 检查必填字段 |
| 1002 | 敏感词拦截 | 修改内容后重新提交 |
| 1003 | 内容待审核 | 等待管理员审核 |

---

## 附录 C: 测试执行记录模板

| 序号 | 测试编号 | 测试项 | 执行时间 | 执行人 | 结果 | 备注 |
|------|---------|--------|---------|--------|------|------|
| 1 | F-001 | 首页加载 | 2026-08-20 10:00 | QA | ✅ 通过 | - |
| 2 | F-101 | 用户注册 | 2026-08-20 10:02 | QA | ✅ 通过 | - |
| 3 | F-102 | 用户登录 | 2026-08-20 10:03 | QA | ✅ 通过 | - |
| 4 | F-201 | 题目列表加载 | 2026-08-20 10:05 | QA | ✅ 通过 | - |
| ... | ... | ... | ... | ... | ... | ... |

> **文档维护说明**: 本文档随项目迭代持续更新，如有接口变更请同步修改对应测试用例。
