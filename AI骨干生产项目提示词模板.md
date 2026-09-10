# AI 生成生产级项目 · 固定提示词模板

> **用法**：复制全文 → 只填【定稿区】6个空位 → 发给 AI。
> **设计原则**：固定的叫「基线」（链路+工程规范/Web通用/数据/缓存/性能/安全/日志/环境/CI-CD，每个项目不变）；可变的叫「业务」（栈/功能/表/接口，每次填空）。

---

## 【定稿区】每次必填（5 个空位）

### 空位 1 · 项目定位（一句话）

<!-- 例：AI 聊天骨架：登录后对话，static 内置聊天页，同步 + SSE 流式 -->

### 空位 2 · 技术栈版本表（全文唯一出处）

| 项 | 定稿 |
|---|---|
| JDK | 21（开启虚拟线程） |
| Spring Boot | 3.5.x |
| 核心依赖 | <!-- 例：SpringAI 1.1.x + spring-ai-alibaba 1.1.2.2 --> |
| ORM / DB | MyBatis-Plus / MySQL 8 |
| 缓存 | Redis（+ Caffeine） |
| 文档 | Knife4j（dev 开启，prod 关闭） |

规则：版本号只允许出现在这张表里，正文一律写"按定稿表"。

### 空位 3 · 模块功能清单

<!-- 按模块列必含点，一行一个模块 -->
<!-- 例：认证模块：注册/登录/双token刷新/登出黑名单；聊天模块：同步+流式、会话CRUD、上下文维护 -->

### 空位 4 · 表设计

| 表 | 关键字段（通用字段见基线二，不用重复写） |
|---|---|
|  |  |

### 空位 5 · 接口清单

| 接口 | 方法 | 鉴权 | 说明 |
|---|---|---|---|
|  |  |  |  |

注意：如含流式/SSE 接口，一律 POST（不写 GET）；无流式接口则本条不适用。

### 空位 5 · 接口清单

日志文件位置

---

## 【基线一】请求链路与工程规范（固定）

请求 → JWT 拦截器鉴权（失败 → 全局异常 → 统一 VO，不进 Service）→ Controller 收 DTO → @Valid 校验（失败同上）→ ServiceImpl：**事务只包 DB 写**，外部调用（LLM/HTTP）一律在事务外 → Mapper 落库 + Redis 缓存 → 组装 VO 返回，全局异常兜底。

铁律：

1. 事务时长 ≤100ms，事务内严禁调用外部 HTTP / LLM
2. 流式接口中途异常走 `emitter.completeWithError`（全局异常救不了已开流的响应）
3. 三层异常兜底各自负责：拦截器（鉴权）/ 参数校验（DTO）/ 业务（ServiceImpl）

### API 路径规范（RESTful）

1. 统一前缀 + 版本号：`/api/v1/**`，与定稿区接口清单一致
2. URL 用**名词复数 + kebab-case**：`/api/v1/chat-sessions`；禁止动词入 URL（`/getUser` ❌ → `GET /users/{id}` ✅）
3. HTTP 动词语义：GET 查 / POST 增 / PUT 全量改 / PATCH 部分改 / DELETE 删
4. 无法映射到动词语义的操作，收敛为子资源路径：如 `POST /api/v1/auth/login`、`POST /api/v1/chat-sessions/{id}/messages/stream`
5. 资源层级 ≤2 级（动作子资源如 `/stream` 除外）：`/api/v1/chat-sessions/{sessionId}/messages`；更深层级改用查询参数
6. 路径/查询参数一律 camelCase

### 目录与命名规范

1. 包结构固定：`com.{org}.{project}` 下分层 `controller / service / mapper / entity / dto / vo / config / common / interceptor / aspect / util`
2. 类名后缀固定：`XxxController`、`XxxService` + `XxxServiceImpl`、`XxxMapper`、`XxxDO` / `XxxDTO` / `XxxVO`、配置类 `XxxConfig`、拦截器 `XxxInterceptor`、切面 `XxxAspect`
3. Mapper XML 与接口同包路径镜像（resources 下目录一致）；SQL 脚本放 `db/`
4. 非代码文件全小写 kebab-case：workflow yml（`ci-push.yml`）、前端资源（`chat-page.js`）
5. 前端 static 目录分离：`static/css`、`static/js`、`static/html` 或页面平铺根目录

## 【基线二】数据基线（固定）

- 所有表通用字段：`id`（雪花）、`user_id`、`create_time`、`update_time`（MP 自动填充）、`is_deleted`（软删）
- 调外部 API 的表预留计量字段（如 `prompt_tokens` / `completion_tokens`）
- 禁 `select *`、禁无索引查询、禁大 offset（深分页用 ID 定位）
- 建表 SQL 与代码同仓库；单表千万级、冷数据定期归档

## 【基线三】缓存策略（固定：Caffeine → Redis → MySQL）

| 层 | 放什么 | 不放什么 |
|---|---|---|
| L1 Caffeine（JVM 堆） | 静态配置、提示词模板 | 会话动态数据（多实例不一致） |
| L2 Redis | 会话上下文（TTL + 最多 15 轮截断）、token 黑名单 | 大 key、超长会话消息 |
| L3 MySQL | **准源**：会话记录、消息明细兜底存储 | — |

- 读流程：L1 → L2 → L3，逐级回写
- 写流程：先更 DB → 删 L2 → 删 L1（用「删除缓存」而非「更新缓存」）
- Redis 硬指标：命中率 ≥80%、单命令 P95 ≤8ms、内存使用率 ≤70%

## 【基线四】性能基线（固定）

| 指标 | 基线 |
|---|---|
| 读 QPS / 写 TPS（单机） | ≤1000 / ≤120 |
| 第三方 IO QPS（LLM 等） | 按上游限流约束（如 ≤80），不按本机能力压 |
| SSE 活跃长连接 | ≤150 |
| 读 / 写接口 P95 | ≤200ms / ≤300ms |
| 外部调用 P95 / SSE 首包 P95 | ≤1800ms / ≤300ms |
| SQL P95 / 慢 SQL 阈值 | ≤150ms / 500ms |
| HikariCP | max 20、空闲 5、使用率 ≤70% |

## 【基线五】安全基线（固定 6 条）

1. **认证**：Cookie-JWT 双 token + Redis 黑名单，组件细节按基线九落地
2. **越权**：查询一律带当前登录用户 ID 条件（水平越权）；接口按角色校验（垂直越权）——不信任前端传的任何 ID
3. **注入**：只用 `#{}` 参数化；禁 `${}`、禁拼接 SQL；数据库最小权限账号
4. **XSS**：用户输入做 HTML 转义后再入库/出库
5. **CSRF**：Cookie 设 SameSite=Lax
6. **脱敏**：密码/手机号/token 不进日志、不返明文

> 防刷限流见基线九；错误响应规范并入基线九全局异常处理器。

## 【基线六】日志基线（固定）

| 项 | dev | prod |
|---|---|---|
| 根级别 / SQL 打印 | DEBUG / 开 | INFO / 关 |
| 出入参 | 完整打印 | traceId + 耗时 + 脱敏摘要 |
| JVM GC / 堆 dump | 关 | 必开 |
| MySQL 慢查询 | 关 | 必开（500ms） |
| 异常堆栈 | 完整 | 仅 ERROR 级打印 |

全链路 traceId 贯穿；日志按大小/时间滚动归档；应用/JVM/数据库日志分开存放。

日志文件输出 慢SQL 慢接口 慢外部接口   info.log warn.log error.log  DEBUG.log输出控制台  这7个日志文件

## 【基线七】环境与密钥（固定）

- 密钥只走环境变量，命名跟官方（如 `DASHSCOPE_API_KEY`），禁止进 git、禁止硬编码
- starter 用标准配置项接密钥（如 `spring.ai.dashscope.api-key: ${DASHSCOPE_API_KEY:}`），不用 `@Value` 手搓变量
- 启动校验：密钥为空时给出友好报错
- profile 三份：application.yml / -dev / -prod

---

## 【基线八】CI/CD 流水线（固定：三级触发 + 质量门禁）

### 触发矩阵

| 级别 | 触发事件 | 执行内容 | 目的 |
|---|---|---|---|
| 静态检测 CI | push 到 main / develop | 编译 + 静态检查（Checkstyle/PMD）+ 快速单测 | 提交即反馈，坏代码进不了主干 |
| 全面 CI | PR 指向 main | 编译 + 全量单测 + 覆盖率 + **CodeQL**（SAST）+ **SonarQube**（质量门禁）+ **CVE** 依赖扫描 | 合并门禁 |
| 发布流水线 | 推送 tag `v*` | 多阶段 Docker 构建 → 推 **ghcr.io**（GitHub Packages）→ 创建 GitHub Release 并附 jar 产物 | 出成品 |

### 固定规则

1. 三个独立 workflow 放 `.github/workflows/`：`ci-push.yml`（轻）、`ci-pr.yml`（重）、`release.yml`（发布），职责不混
2. JDK 版本与定稿区对齐（`actions/setup-java@v4`，`cache: maven`）
3. 同一 PR 新提交自动取消旧任务（`concurrency` + `cancel-in-progress: true`），省额度
4. SonarQube 的 `SONAR_TOKEN` / `SONAR_HOST_URL` 只走 GitHub Secrets，**yml 里零密钥**
5. CVE 扫描用 `dependency-review-action`（或 Trivy），高危漏洞 = 阻断合并
6. Docker 镜像：多阶段构建（builder 层打 jar → `eclipse-temurin:21-jre` 运行层）；同时打 `v版本号` 与 `latest` 双 tag；推 ghcr.io 用 `GITHUB_TOKEN`（`permissions: packages: write`）
7. 仓库开启分支保护：main 必须**全面 CI 绿灯 + ≥1 人 approve** 才可合并；发版只从 main 打 tag
8. 可选：CodeQL 每周定时巡检（`schedule` 触发）

---

## 【基线九】Web 通用基础（固定：基建组件清单）

| 组件 | 要求 |
|---|---|
| 统一响应 `Result<T>` | code/message/data；静态工厂 `success()/fail()`；错误码用**枚举集中管理**（分段：系统/认证/业务） |
| 全局异常处理器 | `@RestControllerAdvice`：校验异常→400 带字段级提示；业务异常→对应错误码；鉴权→401、越权→403；兜底→500 只回友好提示，**堆栈只进日志不进响应，不带框架版本信息** |
| 参数校验 | DTO 入参 Bean Validation（@NotBlank/@Size…）+ Controller `@Valid`；VO 出参同样过校验 |
| DTO/VO 分层 | Controller 只见 DTO/VO；DO 不出 Service 层；转换独立成 converter（MapStruct 或手写） |
| 工具类 | JwtUtil、CookieUtil、RedisUtil、脱敏工具、日期工具、SSE 工具，统一放 `common/util` |
| Cookie-JWT 双 token | access 短效 + refresh 长效，均放 HttpOnly Cookie；拦截器校验 access，过期返回特定 code 触发前端静默刷新；refresh 接口**轮换**新双 token |
| Redis 黑名单 | 登出/改密作废未到期 token（key=token，TTL=剩余有效期）；拦截器校验前**先查黑名单** |
| 接口限流 | 注解式 `@RateLimit`（维度 + 窗口 + 阈值）+ Redis Lua 滑动窗口；默认 IP 维度，LLM 等烧钱接口单独严阈值 |
| 跨域 CORS | 统一配置类；static 同源场景默认不需要，预留配置并注释说明用途 |
| API 文档 Knife4j | dev 开启 `/doc.html`、prod 关闭；按模块分组；SSE 接口在文档注明「用内置页面调试」 |

配套铁律：

1. 拦截器执行顺序：**CORS → 限流 → JWT 校验**，顺序错了限流保护不了鉴权接口
2. refresh 轮换时旧 refresh token 立即作废（防重放）；refresh 接口本身也要限流
3. 所有拦截器放行的路径（登录/注册/刷新/静态资源）集中配置成白名单常量，禁止散落硬编码

---

## 【交付区】固定

1. 完整可运行代码（目录分层与命名按基线一）
2. schema.sql 建表脚本
3. docker-compose.yml（MySQL8 + Redis）
4. `.github/workflows/` 三个生产级 yml：`ci-push.yml` / `ci-pr.yml` / `release.yml`（按基线八）
5. Dockerfile（多阶段构建，供 release 流水线用）
6. `.gitignore`：覆盖 target/、.idea/、*.iml、logs/、.env、application-local.yml、node_modules/、*.log
7. README：环境变量清单、启动步骤、接口文档地址、自测清单 + **三大功能开发说明**：① web 通用基础组件（统一返回/全局异常/参数校验/DTO-VO/工具类）② Cookie-JWT 双 token + Redis 黑名单认证体系（含刷新轮换流程图）③ 接口限流与跨域配置说明
8. 冒烟自测：注册 → 登录 → 核心业务全链路跑通
9. jar 包和容器镜像文件（由 release 流水线构建产出，附于 GitHub Release / ghcr.io）

---

## 【负面清单】每次发给 AI 时原样附上

- 版本号多处出现且不一致
- 硬编码密钥 / 明文存密码
- 事务内调 LLM / 外部 HTTP
- `select *` / `${}` 拼接 / 大 offset 分页
- SSE 用 GET + EventSource（一律 POST + fetch）
- 拿前端传的 ID 直接查库、不校验归属
- 日志打印敏感字段
- dev 配置混进 prod
- workflow yml 里硬编码密钥 / token
- 发布绕过门禁：不经 PR 直接推 main、从不打 tag 直接改 ghcr 镜像
- URL 里写动词（`/getUser`、`/sendMessage`）或不带版本前缀
- 目录/类命名随意（包名大写、类名不带 Controller/Service/DTO 等规范后缀）
- 没有参数校验，异常返回时不经全局异常处理

---

## 附：填写示例（AI 聊天骨架，30 秒填完）

> **定位**：AI 聊天骨架，登录后对话，static 内置聊天页，同步 + SSE 流式
> **栈**：JDK21 / Boot 3.5.x / SpringAI 1.1.x（spring-ai-alibaba 1.1.2.2，模型 qwen3.7-flash）/ MyBatis-Plus / MySQL8 / Redis / Knife4j
> **模块**：认证（注册、登录、双 token、黑名单）；聊天（同步 + POST 流式、会话 CRUD、Redis 上下文 15 轮截断）；系统（Result 统一返回、全局异常、参数校验、限流）
> **表**：`sys_user`；`ai_chat_session`；`ai_chat_message`（含 tokens 计量字段）
> **接口**：`POST /api/v1/auth/register|login|refresh|logout`；`POST /api/v1/chat-sessions`（建会话）；`GET /api/v1/chat-sessions`（列表）；`GET /api/v1/chat-sessions/{id}/messages`（历史）；`POST /api/v1/chat-sessions/{id}/messages`（同步发送）；`POST /api/v1/chat-sessions/{id}/messages/stream`（流式发送）
> **密钥**：`DASHSCOPE_API_KEY` 环境变量注入
