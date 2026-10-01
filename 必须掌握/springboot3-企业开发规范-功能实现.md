# 互联网企业 SpringBoot 项目开发规范

> 以一个标准的互联网企业 SpringBoot 项目为例，覆盖结构、命名、API、数据库、日志、Git、协作流程、线上排查、网络攻击防范、基础项目功能清单。入职第一天不被导师皱眉的最低标准。

***

**完整链路请求：**  SpringBoot 接收请求 → 参数校验 → 登录鉴权 (JWT) →会话管理 → 业务逻辑 → 调用数据库 → 返回结果，全局异常处理→ 返回前端渲染（逐环展开见第十四章）

## 一、项目结构规范

### 标准目录树

```
com.company.project
├── common/                  # 通用基础
│   ├── result/              #   统一返回 Result<T>
│   ├── exception/           #   BusinessException + 全局异常处理器
│   ├── constant/            #   常量类（按业务分文件）
│   └── util/                #   工具类（静态方法，无状态）
├── config/                  # 配置类：MybatisPlus、Redis、Swagger、线程池、跨域
├── controller/              # Web 层：只做参数校验 + 调 Service + 组装返回
├── service/                 # 接口 + impl 子包，业务逻辑唯一归属地
│   └── impl/
├── mapper/                  # 数据访问层（MyBatis-Plus BaseMapper）
├── entity/                  # 数据库表映射对象（PO/DO）
├── model/
│   ├── dto/                 # 接口入参对象
│   ├── vo/                  # 接口出参对象（View Object）
│   ├── bo/                  # 业务中间对象（Business Object）
│   └── query/               # 查询参数对象（含分页参数）
├── enums/                   # 业务枚举（订单状态、错误码）
├── aspect/                  # 切面：操作日志、幂等、限流
├── interceptor/             # 拦截器：登录校验、租户隔离
├── task/                    # 定时任务（@Scheduled / xxl-job）
├── listener/                # 事件监听 / MQ 消费者
└── ProjectApplication.java # 启动类放根包，保证包扫描覆盖全部
```

### 分层职责铁律

| 层          | 只做                               | 绝不做                            |
| ---------- | -------------------------------- | ------------------------------ |
| Controller | 参数校验（@Validated）、调 Service、VO 转换 | 业务逻辑、直接调 Mapper、写 SQL          |
| Service    | 业务逻辑、事务边界、DO/DTO 转换              | 处理 HTTP 对象（HttpServletRequest） |
| Mapper     | 数据访问、SQL                         | 业务判断（if/else 业务分支）             |

三条红线：

1. **禁止跨层调用**（Controller 直调 Mapper 是 Code Review 一票否决项）
2. **禁止 Service 向上依赖**（不能注入 Controller 相关对象）
3. **POJO 跨层使用需转对象**（entity 不直接出接口，dto 不直接进 SQL）

***

## 二、命名规范

| 对象       | 规则                  | 示例                                                         |
| ---------- | --------------------- | ------------------------------------------------------------ |
| 类         | 大驼峰 + 类型后缀     | `UserController` / `OrderServiceImpl` / `UserDTO` / `PayStatusEnum` |
| 方法       | 小驼峰 + 动词开头     | `getUserById` / `createOrder` / `listByCondition`            |
| 常量       | 全大写下划线          | `MAX_RETRY_TIMES`                                            |
| 包名       | 全小写、单数          | `controller` 不是 `controllers`                              |
| 数据库表   | 小写下划线            | `user_order`、`order_item`                                   |
| 数据库字段 | 小写下划线            | `create_time`、`user_id`                                     |
| 索引       | 前缀语义              | 主键 `pk_`、唯一 `uk_`、普通 `idx_`                          |
| 接口 URL   | 名词复数 + kebab-case | `/api/v1/order-items`                                        |
| 测试类     | 被测类名 + Test       | `UserServiceTest`                                            |

### springboot

#### 包命名

（全小写，分层，禁止大写，使用`.`分隔）

**反向域名风格，业务模块分层**

| 目录        | 状态  | 说明                                         |
| ----------- | ----- | -------------------------------------------- |
| aspect      | ✅正常 | 通用 AOP 切面（日志、限流）                  |
| audit       | ✅正常 | 审计日志全套（注解、切面、service、DO）      |
| common      | ✅正常 | 工具、异常、枚举、常量                       |
| config      | ✅正常 | Bean 配置、第三方 Bean                       |
| controller  | ✅正常 | http 接口层                                  |
| client      | ✅正常 | 远程调用                                     |
| dto         | ✅正常 | 接口入参                                     |
| entity      | ✅正常 | DO 数据库实体                                |
| integration | ✅正常 | 第三方集成层（Redis、ES、S3、B 站 API 封装） |
| mapper      | ✅正常 | Mybatis‑Plus Mapper 接口                     |
| service     | ✅正常 | 业务接口 + impl 实现                         |
| task        | ✅正常 | 定时、异步任务                               |
| vo          | ✅正常 | 返回前端视图对象                             |

```
com.algoviz.video
├── aspect                # AOP切面：请求日志、限流
├── audit                 # 审计日志（注解、切面、DO、service）
├── client                # ✅【微服务专用】Feign接口 + 降级实现，调用其他微服务
│   ├── auth
│   │   ├── AuthFeignClient.java
│   │   └── AuthFallback.java
│   └── search
│       └── SearchFeignClient.java
├── common                # 本服务局部公共（优先复用common-core，不要重复造轮子）
├── config                # Bean配置、nacos、redis、es、mq的Bean装配
├── controller            # http对外接口
├── dto                   # http接口入参DTO
├── entity                # DO数据库实体
├── integration           # ✅外部第三方调用（非微服务）
│   ├── redis
│   ├── es
│   ├── s3                # RustFS/SeaweedFS对象存储
│   └── bilibili          # B站API、biliup‑rs调用
├── mapper                # Mybatis‑Plus Mapper
├── service
│   ├── impl
├── task                  # 定时、异步任务
├── vo                    # 返回前端VO
├── bo                    # 业务中间对象，按需新增
└── VideoServiceApplication.java #启动类
```

> 包命名全部小写，不使用下划线；模块多可以继续拆分：`com.xxx.oj.video`、`com.xxx.oj.judge`。

#### 类命名

（大驼峰 UpperCamelCase）

| 类型           | 命名示例                               | 规则                                                         |
| -------------- | -------------------------------------- | ------------------------------------------------------------ |
| Controller     | `VideoController`、`ProblemController` | 业务名 + Controller，每个 Controller 对应一组接口            |
| Service 接口   | `VideoService`                         | 业务名 + Service，**接口名不带 I 前缀**（阿里规范，不写 IVideoService） |
| Service 实现类 | `VideoServiceImpl`                     | 业务名 + ServiceImpl                                         |
| Mapper 接口    | `VideoMapper`                          | 表名 + Mapper                                                |
| DO 数据库实体  | `VideoDO`                              | 表名 + DO，和数据库表一一对应                                |
| DTO（入参）    | `VideoPublishDTO`                      | 业务 + DTO，接收前端 post/json 参数                          |
| VO（出参）     | `VideoVO`、`VideoDetailVO`             | 业务 + VO，返回前端数据                                      |
| BO 业务对象    | `VideoTaskBO`                          | service 内部流转对象，不对外暴露                             |
| 配置类         | `RustFsS3Config`、`WebConfig`          | 功能 + Config                                                |
| 切面 Aspect 类 | `RequestLogAspect`、`RateLimitAspect`  | 功能 + Aspect，加`@Aspect`                                   |
| 工具类         | `S3Util`、`JsonUtil`                   | 功能 + Util，全部 static 方法                                |
| 枚举           | `VideoStatusEnum`                      | 业务 + Enum                                                  |
| 异常类         | `BusinessException`                    | 业务 + Exception                                             |

> 禁止：`VideoInfo`、`VideoModel`模糊命名；分清 DO/DTO/VO/BO。

#### 资源文件

cert 密钥文件

```
src/main/resources
├── application.yml               # 主配置
├── application-dev.yml           # 开发环境
├── application-test.yml          # 测试环境
├── application-prod.yml          # 生产环境
├── db
│   └── schema.sql                # 建表脚本
├── mapper                        # Mybatis XML映射文件
├── static                        # 静态资源（极少用，业务文件存对象存储RustFS/SeaweedFS）
└── scripts                       # shell/powershell、工具脚本（biliup‑rs脚本等）
```

### 微服务

**一、Maven 多模块整体工程（外层）**

```
algoviz-parent               # 父pom（聚合工程）
├── algoviz‑common‑core      # 公共核心模块：Result、异常、工具、常量、通用注解（所有微服务依赖）
├── algoviz‑common‑feign     # 【可选】feign公共接口、远程调用DTO（API契约包）
├── algoviz‑service‑video    # 视频微服务（业务服务，你的单体改造过来）
├── algoviz‑service‑auth     # 认证授权微服务
└── algoviz‑service‑search   # 搜索微服务
```

**二、单个微服务内部包（以 algoviz‑service‑video 为例）**

```
com.algoviz.video
├── aspect                # AOP切面：请求日志、限流
├── audit                 # 审计日志（注解、切面、DO、service）
├── client                # ✅【微服务专用】Feign接口 + 降级实现，调用其他微服务
│   ├── auth
│   │   ├── AuthFeignClient.java
│   │   └── AuthFallback.java
│   └── search
│       └── SearchFeignClient.java
├── common                # 本服务局部公共（优先复用common-core，不要重复造轮子）
├── config                # Bean配置、nacos、redis、es、mq的Bean装配
├── controller            # http对外接口
├── dto                   # http接口入参DTO
├── entity                # DO数据库实体
├── integration           # ✅外部第三方调用（非微服务）
│   ├── redis
│   ├── es
│   ├── s3                # RustFS/SeaweedFS对象存储
│   └── bilibili          # B站API、biliup‑rs调用
├── mapper                # Mybatis‑Plus Mapper
├── service
│   ├── impl
├── task                  # 定时、异步任务
├── vo                    # 返回前端VO
├── bo                    # 业务中间对象，按需新增
└── VideoServiceApplication.java #启动类
```

**三、资源文件 resources（微服务）**

```
src/main/resources
├── bootstrap.yml               # ✅微服务优先bootstrap，nacos配置放这里
├── bootstrap-dev.yml
├── bootstrap-prod.yml
├── application.yml             # 本地少量配置
├── mapper                      # mybatis xml
└── db                          # flyway迁移脚本 V1_0_0__xxx.sql
```

### 前端

```
src/assets
├── images          # 图片
│   ├── common      # 公共图标、logo
│   └── page‑home   # 首页页面专属图片
├── videos          # 内置演示视频（少量，不要放大视频）
├── fonts           # 字体文件
└── styles          # scss/css全局样式
```

#### 视频

```
/static
├─/video           # 全部视频根目录
│  ├─/cover        # 视频封面图、视频缩略图
│  ├─/original     # 原始源文件（不对外访问，后台转码用）
│  ├─/hd           # 高清转码视频
│  ├─/sd           # 标清转码视频
│  ├─/preview      # 预览小片段、预告片
│  ├─/trailer      # 宣传片、广告片
│  ├─/short        # 短视频
│  ├─/live         # 直播回放录像
│  └─/temp         # 临时上传待处理视频（定期清理）

```

### 分支命名规范

**本地临时开发分支**

统一前缀 `类型/issue编号-简短描述`，有 issue 优先带上 issue 号，开源协作更清晰

| 分支前缀         | 用途                                            | 来源分支  | 合并目标分支                       |
| ---------------- | ----------------------------------------------- | --------- | ---------------------------------- |
| `feat/`          | 新功能（前端页面、后端接口、新组件等）          | `develop` | `develop`                          |
| `fix/`           | 普通 bug 修复                                   | `develop` | `develop`                          |
| `hotfix/`        | `main`线上紧急故障                              | `main`    | `main` + `develop`（两边同步修复） |
| `refactor/`      | 重构（无业务逻辑变更，前后端代码整理）          | `develop` | `develop`                          |
| `docs/`          | 文档、README、注释、官网文案修改                | `develop` | `develop`                          |
| `style/`         | 纯格式调整（eslint/prettier，不改动逻辑）       | `develop` | `develop`                          |
| `test/`          | 单元测试 /e2e 测试补充                          | `develop` | `develop`                          |
| `release/v*.*.*` | 版本预发布分支，只做版本号、changelog、最终测试 | `develop` | `main` + `develop`                 |

**✅ 分支命名示例**

```
feat/#42-user-login-page
fix/#58-api-timeout
hotfix/#62-main-cors-error
refactor/backend-controller-split
docs/update-install-guide
release/v1.2.0
```

## 三、API 设计规范

### RESTful + 版本

```
GET    /api/v1/users?pageSize=10&pageNo=1    # 分页列表
GET    /api/v1/users/{id}                    # 详情
POST   /api/v1/users                         # 新增
PUT    /api/v1/users/{id}                    # 全量更新
PATCH  /api/v1/users/{id}                    # 部分更新
DELETE /api/v1/users/{id}                    # 删除
```

- URL 只用名词，动作交给 HTTP 方法；版本进 URL（`/v1/`），大版本不兼容升级时开 `/v2/`

- 前后端分离项目加 `/api` 前缀，网关按前缀路由

### 统一返回体

URL：小写中划线，名词复数   /api/videos/{videoId}/comments
动词：GET 查询 / POST 新增 / PUT 更新 / DELETE 删除

```json
{ "code": 200, "message": "success", "data": { "id": 1 }, "traceId": "a1b2c3" }
```

- HTTP 状态码表达传输层，业务成败看 code（200 成功、4xx 客户端错、5xx 服务端错）

- 错误码用枚举统一管理：`ResultCode.USER_NOT_FOUND(10001, "用户不存在")`

### 分页规范

```java
public class PageQuery {
    @Min(1)  private Integer pageNo = 1;
    @Min(1) @Max(100) private Integer pageSize = 10;
    private String orderBy;   // 白名单校验，防 SQL 注入
}
```

***

## 四、数据库规范（互联网惯例）

> 变更走 Flyway 脚本 `V1_0_1__add_video_like.sql`（目录里已预留 `db/flyway`，正好配套）。**SQL 变更脚本提交 PR 审核后才可执行**。

### 每张表必备五字段

```sql
CREATE TABLE user_order (
    id           BIGINT       NOT NULL COMMENT '主键',
    ...业务字段...,
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否 1是',
    version      INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (id)
) COMMENT '用户订单表';
```

### 强制项

| 规则                      | 原因                   |
| ----------------------- | -------------------- |
| 所有字段 NOT NULL + 默认值     | NULL 导致索引统计复杂、代码判空负担 |
| 禁用外键约束                  | 高并发下锁竞争，一致性由应用层保证    |
| 禁用存储过程 / 触发器 / 视图       | 难版本管理、难迁移、难排查        |
| 金额用 DECIMAL             | float/double 精度丢失    |
| 状态字段用 TINYINT + 枚举注释    | 可读、省空间               |
| 表/字段必须有 COMMENT         | 半年后没人记得 a1 是什么       |
| 单表超 500 万行需评估分表（见分库分表篇） | B+ 树层级加深             |
| 大表 DDL（加字段）需 DBA 评审     | 锁表风险                 |
| 建表必须建索引：查询条件列、JOIN 列    | 否则慢 SQL 通报           |
| 唯一业务约束用 uk 索引兜底（如订单号）   | 防重复提交的最后一道防线         |

***

1. 数据库审计字段（必加）

   ```
   create_by, create_time, update_by, update_time, deleted
   ```

   （逻辑删除），所有业务表统一带上。

   > 逻辑删除：不要物理删除数据，便于数据追溯。

2. **数据库版本管理 Flyway / Liquibase** 脚本版本化管理，团队协作自动建表、升级结构，不需要手动执行 SQL。

3. **唯一索引**：用户名、邮箱、第三方平台唯一标识，防止重复注册。

4. **SQL 打印 / 慢 SQL 监控**（开发环境）MyBatis-Plus 日志。

5. 数据库修改日志，任何对数据库的修改都要保存下来

| 项目     | 规范                                                         |
| :------- | :----------------------------------------------------------- |
| 表名     | 蛇形小写，带业务前缀：`video_info`、`user_account`           |
| 必备字段 | `id`、`create_time`、`update_time`、`deleted`（逻辑删除）    |
| 字段命名 | 蛇形，禁用数据库关键字；布尔用 `is_xxx`（tinyint）           |
| 索引命名 | 主键 `pk_`、唯一 `uk_表_字段`、普通 `idx_表_字段`            |
| 禁止     | 外键约束（应用层维护）、存储过程、`select *`、大字段混在主表 |

## 五、编码规范（阿里巴巴 Java 手册精选）

按入职后 Code Review 最常打回的顺序：

1. **禁止魔法值**：`if (status == 3)` → `if (status == OrderStatus.PAID.getCode())`
2. **POJO 必须写 toString**：日志排查全靠它
3. **日期用 LocalDate / LocalDateTime**，禁用 Date
4. **线程池手动 new ThreadPoolExecutor**，禁用 Executors 工厂方法（OOM 风险）
5. **@Transactional(rollbackFor = Exception.class)**，默认只回滚运行时异常
6. **常量在前的 equals**：`"PAID".equals(status)` 防空指针
7. **Long 型字段序列化转 String**：前端 JS 精度丢失（雪花 ID 必踩）
8. **循环内禁用 StringBuilder 拼接、禁查数据库**（N+1 查询是性能头号杀手）
9. **删除/修改前先查再改**，不盲操作
10. **方法不超过 80 行，嵌套不超过 3 层**：超过就抽方法或卫语句提前返回

工具：IDE 装 **Alibaba Java Coding Guidelines** 插件，实时扫描。

***

## 六、日志规范

> 合并自《springboot3-日志输出清单.md》。本章回答三件事：**在哪打（8 个位置）、怎么打（编写规则）、打什么级别**。
> 基础设施日志（GC / MySQL / Redis / Nginx）不在本章范围，见《springboot3-日志全景-位置与级别.md》。

### 日志级别使用场景

| 级别  | 场景                                     |
| :---- | :--------------------------------------- |
| DEBUG | 开发调试细节，生产默认关闭               |
| INFO  | 业务关键节点（登录、支付、状态流转）     |
| WARN  | 可自愈异常（重试成功、降级触发、慢接口） |
| ERROR | 需要人工介入（DB 失败、第三方连续超时）  |

> ERROR 只在"需要处理"时打，禁止 catch 里无脑 error（会刷爆告警）。补一条脱敏清单：手机号/邮箱/密码/token/身份证 必脱敏后入日志。

### 输出规范

| 问题     | 对应字段                          |
| -------- | --------------------------------- |
| 谁       | traceId / userId                  |
| 在哪     | 类名 + 方法名（日志框架自动输出） |
| 做了什么 | 入参（脱敏后）                    |
| 结果如何 | 返回值 / 异常堆栈 / 状态          |
| 花了多久 | 耗时 ms                           |

### 日志文件

**按级别拆分（Logback 常见做法）**

| 文件        | 内容                     | 生产环境     |
| :---------- | :----------------------- | :----------- |
| `debug.log` | 调试细节                 | 通常关闭落盘 |
| `info.log`  | 业务关键节点             | 开           |
| `warn.log`  | 可自愈异常、降级触发     | 开           |
| `error.log` | 需人工介入的错误，含堆栈 | 开，配告警   |

> 很多团队不会 4 个全拆，常见是 `app.log`（info 及以上）+ `error.log` 单独拆出来方便告警和排障，共 2 个。

**按用途/来源拆分（数量随架构增长）**

| 日志           | 来源            | 说明                                                         |
| :------------- | :-------------- | :----------------------------------------------------------- |
| 业务日志       | 应用打印        | 状态流转、关键操作，量大时单独文件                           |
| 访问日志       | Nginx / Tomcat  | `access.log`，QPS、状态码、耗时                              |
| 慢 SQL 日志    | MySQL           | `slow.log`，超过 `long_query_time` 落盘                      |
| 慢接口日志     | 应用 AOP 自埋   | 超过阈值的接口调用                                           |
| 慢外部调用日志 | 应用自埋        | 第三方 API 超时统计（如 B 站 API）                           |
| GC 日志        | JVM             | `-Xlog:gc*:file=gc.log`，排查内存问题                        |
| JVM 崩溃日志   | JVM             | `hs_err_pid*.log`，OOM/段错误时自动生成                      |
| 审计日志       | 应用            | 谁在什么时间做了什么，合规用，您文档里的 audit 模块就是干这个的 |
| 中间件日志     | Redis / ES / MQ | 一般在容器/主机层，不归应用管                                |

### 6.1 设计原则：用排查场景反推

日志是给"半年后凌晨三点排查问题的你"看的。每条合格的日志必须能回答五个问题：

| 问题 | 对应字段 |
| ---- | ---- |
| 谁 | traceId / userId |
| 在哪 | 类名 + 方法名（日志框架自动输出） |
| 做了什么 | 入参（脱敏后） |
| 结果如何 | 返回值 / 异常堆栈 / 状态 |
| 花了多久 | 耗时 ms |

打每条日志前自问：**排查时这条能帮我定位问题吗？** 不能 = 删掉。

### 6.2 必须打日志的 8 个位置

| # | 位置 | 级别 | 必含信息 |
| - | ---- | ---- | ---- |
| 1 | 接口出入口（AOP 统一打） | INFO | URL、入参（脱敏）、返回码、耗时 |
| 2 | 业务关键节点 | INFO | 状态流转、业务主键（orderId） |
| 3 | 外部调用（出方向） | INFO/WARN | 参数、响应码、耗时、重试次数 |
| 4 | 第三方回调（入方向） | INFO | 回调参数 + 验签结果（纠纷唯一证据） |
| 5 | 异常 | ERROR | 堆栈 + 业务上下文 |
| 6 | 降级 / 自愈 | WARN | 重试成功、兜底触发 |
| 7 | MQ 消费 / 定时任务 | INFO | 开始、结束、处理条数、失败重试 |
| 8 | 安全审计 | WARN | 登录失败、越权尝试、敏感操作 |

**位置 1：接口出入口——用 AOP 统一打，严禁每个方法手写**

```java
@Aspect
@Component
@Slf4j
public class AccessLogAspect {

    @Pointcut("execution(* com.xxx.controller..*(..))")
    public void controllerPointcut() {}

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest req = attrs.getRequest();
        long start = System.currentTimeMillis();

        log.info("[REQ] {} {} args={}", req.getMethod(), req.getRequestURI(),
                Arrays.toString(pjp.getArgs()));

        try {
            Object result = pjp.proceed();
            log.info("[RESP] {} {} cost={}ms", req.getMethod(),
                    req.getRequestURI(), System.currentTimeMillis() - start);
            return result;
        } catch (Throwable e) {
            log.error("[EXCEPTION] {} {} cost={}ms", req.getMethod(),
                    req.getRequestURI(), System.currentTimeMillis() - start, e);
            throw e;
        }
    }
}
```

要点：`[REQ]`/`[RESP]`/`[EXCEPTION]` 标签统一格式便于 grep；敏感接口（登录、支付）的入参按注解跳过或脱敏。

**位置 2：业务关键节点——只打"事后需要还原"的节点**

```java
// 好：状态流转必须可追溯
log.info("订单状态流转, orderId={}, {} -> {}, operator={}",
        orderId, oldStatus, newStatus, operator);

// 坏：方法进来出去各打一条没信息量的
log.info("createOrder start");
log.info("createOrder end");
```

**位置 3：外部调用——失败时能分清是自己的问题还是对方的问题**

```java
long start = System.currentTimeMillis();
try {
    PayResp resp = payClient.create(order);
    log.info("[CALL-pay] orderId={} code={} msg={} cost={}ms",
            order.getId(), resp.getCode(), resp.getMsg(),
            System.currentTimeMillis() - start);
} catch (Exception e) {
    log.error("[CALL-pay-FAIL] orderId={} cost={}ms", order.getId(),
            System.currentTimeMillis() - start, e);
    throw new BusinessException(ResultCode.PAY_ERROR);
}
```

LLM 调用专项（RAG/AI 项目）——成本和延迟排查全靠它：

```java
log.info("[LLM] model={} promptTokens={} completionTokens={} cost={}ms userId={}",
        model, usage.promptTokens(), usage.completionTokens(), cost, userId);
log.warn("[LLM-RETRY] attempt={} reason={}", attempt, e.getMessage());
log.warn("[RAG-FALLBACK] 向量检索超时, 降级为关键词检索, query={}", query);
```

**位置 4：第三方回调——资损纠纷的唯一仲裁证据**

```java
log.info("[PAY-CALLBACK] orderId={} verifySign={} body={}",
        orderId, signOk, desensitizedBody);
```

**位置 5~6：异常带堆栈、自愈用 WARN**

```java
// 对：异常对象作最后一个参数，堆栈完整打印，附业务上下文
log.error("创建订单失败, userId={}, amount={}", userId, amount, e);

// 错：拼接吞掉堆栈；catch 后什么都不干
log.error("创建订单失败: " + e.getMessage());   // 没有堆栈
```

预期内的失败（重试成功、降级触发）用 WARN，不是 ERROR——否则告警系统天天狼来了：

```java
log.warn("重试第{}次成功, api={}", attempt, apiName);
```

**位置 7：MQ 消费 / 定时任务——多实例下判断"谁执行的、执行了几条"**

```java
@RabbitListener(queues = "order.timeout.queue")
public void onMessage(OrderTimeoutMsg msg) {
    log.info("[MQ-RECV] orderTimeout orderId={}", msg.getOrderId());
    try {
        int result = orderService.cancelTimeout(msg.getOrderId());
        log.info("[MQ-DONE] orderId={} result={}", msg.getOrderId(), result);
    } catch (Exception e) {
        log.error("[MQ-FAIL] orderId={} 等待重试", msg.getOrderId(), e);
        throw e;   // 抛出触发重试/死信机制
    }
}

@Scheduled(cron = "0 */5 * * * ?")
public void cancelTimeoutOrders() {
    log.info("[TASK-START] cancelTimeoutOrders");
    int n = orderService.cancelAllTimeout();
    log.info("[TASK-END] cancelTimeoutOrders processed={}", n);
}
```

**位置 8：安全审计**

```java
log.warn("[SEC] 登录失败, username={}, ip={}, reason={}", username, ip, "密码错误次数超限");
log.warn("[SEC] 越权尝试, userId={} 访问 orderId={}", currentUserId, orderId);
log.warn("[SEC] 敏感操作, userId={} action=CHANGE_PASSWORD ip={}", userId, ip);
```

### 6.3 编写规则（红线）

```java
// 正确：占位符 + 关键业务上下文
log.info("创建订单成功, orderId={}, userId={}, amount={}", orderId, userId, amount);
log.error("调用支付服务失败, orderId={}", orderId, e);   // 异常对象放最后一个参数

// 错误：字符串拼接（白拼了性能）、System.out（不进日志文件）
```

### 6.4 级别使用速查

| 级别 | 用途 | 告警 |
| ---- | ---- | ---- |
| ERROR | 影响业务的失败，必须带堆栈 | 触发（电话/短信） |
| WARN | 可自愈异常、降级、审计事件 | 触发（IM 通知） |
| INFO | 关键业务节点、出入口 | 不触发 |
| DEBUG | 调试细节，生产默认关闭 | 不触发 |

### 6.5 traceId 全链路串联（EFK / 微服务的前提）

没有 traceId，一次请求跨 3 个服务后日志就是三堆散沙：

```java
@Component
public class TraceIdFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp,
                                    FilterChain chain) throws ServletException, IOException {
        String traceId = Optional.ofNullable(req.getHeader("X-Trace-Id"))
                .orElse(UUID.randomUUID().toString().replace("-", ""));
        MDC.put("traceId", traceId);
        resp.setHeader("X-Trace-Id", traceId);
        try {
            chain.doFilter(req, resp);
        } finally {
            MDC.clear();   // 线程池复用，必须清理
        }
    }
}
```

logback pattern 加 `%X{traceId}`：

```xml
<pattern>%d{HH:mm:ss.SSS} [%thread] %-5level [%X{traceId}] %logger{36} - %msg%n</pattern>
```

微服务间传递：Feign 拦截器把 traceId 塞进请求头传给下游。

### 6.6 反面清单（不打什么）

| 不打 | 原因 |
| ---- | ---- |
| 循环体内高频日志 | 日志风暴：IO 打满、EFK 存储爆炸 |
| 密码 / token / 完整身份证 / 手机号明文 | 安全审计红线；手机号脱敏 `138****5678` |
| 大对象全量 JSON（几 MB body） | 一条日志撑爆日志系统 |
| 方法 start / end 无信息日志 | 纯噪音，耗时 AOP 已覆盖 |
| 同一信息打两遍 | 误判"请求执行了两次"的经典事故源 |
| System.out.println | 不进文件、无级别、无异步，等于没打 |

### 6.7 与 EFK 的衔接

1. **格式对齐**：logback 输出改 JSON layout（logstash-logback-encoder），ES 里字段可直接检索聚合，Kibana 里 `traceId:xxx AND level:ERROR` 一查到底
2. **量级控制**：`maxHistory` + `totalSizeCap` 本地滚动 + Filebeat 只采集必要文件，避免反面清单里的内容冲进 ES

生产 logback 必配滚动策略 + totalSizeCap（防磁盘写满，见排查手册第九章）。

***

### 6.8 日志文件种类

慢SQL日志 慢接口日志  慢外部调用接口  info error worring 日志  GC  JVM 异常堆转储 线程转储 ThreadDump

## 七、异常处理规范

```
业务异常：throw new BusinessException(ResultCode.USER_NOT_FOUND)
                ↓
全局异常处理器 @RestControllerAdvice 接住
                ↓
统一转 Result{code, message} 返回，日志留 ERROR + traceId
```

- **业务异常不上堆栈**（预期内，打 WARN 即可），**系统异常必须带堆栈**（ERROR）

- 不允许 catch 后什么都不干（吞异常 = 排查黑洞）

- finally 释放资源用 try-with-resources

***

## 八、Git 工作流

### 分支模型

| 分支            | 生命周期 | 用途                   | 保护            |
| ------------- | ---- | -------------------- | ------------- |
| main / master | 永久   | 线上运行版本               | 禁止直推，只能 MR 合入 |
| dev / develop | 永久   | 开发集成、提测版本            | 禁止直推          |
| feature/xxx   | 短    | 一个需求一个分支             | 用完即删          |
| fix/xxx       | 短    | 测试阶段缺陷修复             | 用完即删          |
| hotfix/xxx    | 紧急   | 线上紧急修复，合回 main 和 dev | 需审批           |
| release/x.y   | 短    | 发布准备、回归验证            | 发布后删除         |

命名示例：`feature/order-export`、`fix/login-token-expired`。

### Commit Message 规范（Angular / 约定式提交）

```
<type>(<scope>): <subject>

type: feat 新功能 / fix 缺陷 / docs 文档 / refactor 重构
      / test 测试 / chore 构建·依赖 / perf 性能
示例：
feat(order): 新增订单导出为 Excel 功能
fix(auth): 修复 token 刷新后旧 token 仍可用的问题
```

- 一个 commit 只做一件事；禁止 "update"、"修改bug" 这种 message
- **Code Review 是合并必经环节**：PR 描述写清改了什么、为什么改、怎么验证

### GitHupFlow 流程规范

1. main 分支始终保持可发布状态，develop 为日常集成分支
2. 从 develop 拉 feat/#issue-xxx 分支开发
3. 提 PR → CI 自动构建 + 单测通过 → 至少 1 人 Code Review 通过
4. 合并回 develop（squash merge，保持提交历史干净）
5. 发版：develop → release/v*.*.* → 验证通过 → 合入 main + 打 tag
6. 线上故障：从 main 拉 hotfix/#issue-xxx → 修复合入 main + develop + 打补丁版本 tag

### 版本与发布规范

- 版本号遵循 SemVer：`主版本.次版本.修订号`（不兼容变更 / 新功能 / 修复）
- Tag 格式 `v1.2.0`，打在 main 上，**推 tag 后同步推送分支**
- 每次发版更新 `CHANGELOG.md`（Keep a Changelog 格式）

## 九、测试流程

**流程当 checklist 用，深度按风险裁剪，能脚本的绝不用手**

1. **环境漂移**：测试环境和生产的 MySQL 版本、Nginx 配置、Docker Compose 不一致，测试全绿上线照样炸。解法就是“一套 compose 定义，本地和生产共用”，你已经这么做了。
2. **脚本腐化**：ps1 回归脚本失败时，小团队的本能是“先怀疑脚本坏了”然后注释掉断言——三个月后脚本形同虚设。纪律是：脚本失败要么修代码、要么明确改断言并写原因，禁止静默跳过。

开发自测 → 提测准入 → 冒烟 → 用例执行 → 缺陷修复回归 → 预发验证 → 上线验证

```
需求评审（PRD）
→ 技术方案设计（类图/时序图/表设计，团队评审）
→ 开发（feature 分支 + 每日进度同步）
→ 自测（单元测试 + 本地联调）
→ 提测（冒烟通过才转 QA）
→ Code Review（≥1 人 approve）
→ QA 测试（提测单流转）
→ 预发布验证（prod 同配置验证）
→ 发布（发布窗口 + 灰度 → 全量，回滚预案先行）
→ 线上观察（监控告警盯 30 分钟）
```

关键纪律：

- **提测冒烟**：自测主要路径跑不通就提测，会被测试直接打回并计入质量分

- **发布回滚预案**：没有回滚方案的发布不予执行

- **变更留痕**：线上配置、SQL、发布记录全部归档（出事追溯 + 复盘依据）

***

## 十、配置与安全规范

### 多环境

```
application.yml            # 公共配置
application-dev.yml         # 本地开发
application-test.yml        # 测试环境
application-pre.yml         # 预发布
application-prod.yml        # 生产
```

### 安全强制项

| 项      | 规范                                                          |
| ------ | ----------------------------------------------------------- |
| 密钥管理   | 数据库密码/ApiKey 走环境变量或 Nacos 加密配置，**严禁提交到 Git**（.gitignore 兜底） |
| 密码存储   | BCrypt 加盐哈希，禁 MD5 明文                                        |
| SQL 注入 | 预编译参数绑定，禁字符串拼 SQL；排序字段白名单                                   |
| XSS    | 富文本入库前过滤，出库转义                                               |
| 越权     | 接口层校验资源归属（改别人的订单 id 直接 403）                                 |
| 敏感数据   | 手机号/身份证出参脱敏（138\*\*\*\*5678）                                |
| 接口文档   | 生产环境关闭 Knife4j（springdoc.api-docs.enabled=false）            |

> 上表是速查；各攻击的原理、绕过手段与代码级防御详见**十三、网络攻击防范**。

***

## 十一、单元测试规范

- 核心业务方法必须有单测，主干工程覆盖率门槛一般 60%+

- 命名：`test{Method}_{Condition}_{Result}`，如 `testCreateOrder_stockEmpty_throwException`

- 使用 JUnit5 + Mockito（Service 层 mock 掉 Mapper）

- 测试代码不入生产构建，但随仓库提交

***

## 十二、线上排查规范

> 合并自《springboot3-线上问题排查手册.md》，速查版。完整命令细节、案例与 Arthas 用法见原手册。

### 12.1 三条铁律

1. **止血优先于定位**：用户在流血先恢复服务，手段按优先级：回滚 > 重启 > 降级 > 扩容
2. **重启之前先留现场**：重启即销毁运行时证据——先抓 `jstack`、`jmap -dump`、GC 日志、慢 SQL
3. **按现象分诊，不瞎猜**：CPU 高、OOM、接口慢、报错激增，四类现象四套路径

### 12.2 标准 SOP

```
发现（告警/用户反馈）
→ 确认影响面（哪些接口、多少用户、是否资损）
→ 止血（回滚/重启/降级/扩容）+ 保留现场
→ 按现象分诊定位
→ 修复验证 → 复盘归档（时间线、根因、改进 TODO）
```

### 12.3 八大类问题速查

| # | 类别 | 典型现象 | 高频根因 |
| - | ---- | -------- | -------- |
| 1 | 启动类 | 起不来、卡住 | 端口占用、循环依赖、依赖冲突、连不上配置中心 |
| 2 | 内存类 | OOM、越跑越慢、周期性卡顿 | 全表查询、缓存堆积、ThreadLocal 泄漏 |
| 3 | CPU 类 | CPU 100% | 死循环、频繁 Full GC、正则回溯 |
| 4 | 数据库类 | 连接池耗尽、慢接口、死锁 | 慢 SQL、连接泄漏、大事务、索引失效（占比最高） |
| 5 | 缓存类 | Redis 超时、不一致 | 大 key、热 key、慢命令 |
| 6 | 接口性能类 | 超时、503、线程池打满 | 未设超时引发堆积、下游雪崩、GC 停顿 |
| 7 | 业务并发类 | 超卖、重复扣款 | 无锁并发、缺幂等、定时任务重复执行 |
| 8 | 环境/运维类 | 时区错乱、磁盘满、容器被杀 | 时区、日志未切割、JVM 超容器内存限制 |

### 12.4 四条高频排查路径

**CPU 100% 四板斧**：

```bash
top                          # 找进程 PID
top -Hp <pid>               # 找线程 TID
printf "%x\n" <tid>         # 转十六进制
jstack <pid> | grep "0x<hex>" -A 30    # 定位代码行
```

分叉判断：抓到业务线程 → 死循环/正则回溯，看栈顶；全是 GC 线程 → 是内存问题，转 OOM 路径。Windows 或嫌麻烦：Arthas `thread -n 3` 一键定位。

**OOM 排查**：

```bash
jstat -gcutil <pid> 1000                        # 先看 GC 频率（FGC 持续涨 = 异常）
jmap -dump:format=b,file=heap.hprof <pid>      # 抓堆快照
# MAT 打开 → Dominator Tree 找最大对象 → Leak Suspects 报告
```

判断标准：Full GC 后老年代能回落 = 正常波动；回不去且爬升 = 泄漏。三大惯犯：ThreadLocal 未 remove、静态集合无上限、连接/流未关闭。

**接口变慢**（按顺序）：AOP 耗时日志定位接口 → MySQL slow.log 找慢 SQL → gc.log 停顿时间对齐故障时段 → Nginx `$request_time` vs `$upstream_response_time`（相等=慢在应用，相差大=慢在排队）。

**报错激增**：Kibana `level:ERROR` 聚类 + traceId 串联 → 全局异常处理器日志 → 堆栈自底向上第一行业务代码。

### 12.5 故障现象 → 排查动作对照

| 现象 | 按顺序看什么 |
| ---- | ---- |
| 服务起不来 | app.log 尾部（卡在哪个组件）→ MySQL error log → 配置中心/Redis 连接 |
| 进程无声消失 | dmesg（OOM killer）→ hs_err_pid.log（JVM 崩溃）→ 都没有查发布记录 |
| 频繁 Full GC | gc.log → heap dump + MAT → 业务日志找泄漏模式 |
| 大面积 502/504 | Nginx error.log → 后端进程存活 → access.log 状态码时间分布与发布时间对齐 |
| 连接池耗尽 | `show processlist` 长事务 → `leak-detection-threshold` 持有堆栈 → Actuator 看池水位 |
| Redis 抖动 | `SLOWLOG GET`（立刻！存内存重启即丢）→ `--bigkeys` → 服务日志看淘汰 |

### 12.6 排查工具箱速查

| 工具 | 核心命令 | 用途 |
| ---- | ---- | ---- |
| JDK | `jstat -gcutil <pid> 1000` | GC 实时监控 |
| JDK | `jstack <pid>` | 死锁、CPU 高、线程堆积 |
| JDK | `jmap -dump / -histo` | 堆转储 / 对象直方图 |
| 可视化 | MAT / JVisualVM | dump 分析 |
| Arthas | `dashboard` | 进程总览一屏看 |
| Arthas | `thread -n 3` / `thread -b` | 最忙线程 / 死锁一键定位 |
| Arthas | `trace 类 方法 '#cost>100'` | 调用链耗时分解 |
| Arthas | `watch` / `jad` | 观察入参返回值 / 确认线上代码版本 |
| 监控 | Actuator + Prometheus + Grafana | 事前发现（比事后排查更重要） |

### 12.7 上线前自检清单

- [ ] `HeapDumpOnOutOfMemoryError` 已配置且 dump 目录磁盘充足
- [ ] GC 日志已开启（`-Xlog:gc*:file=...:filecount=5,filesize=50m`）
- [ ] 所有外部调用（HTTP/DB/Redis）都设了超时
- [ ] HikariCP 开启 `leak-detection-threshold`
- [ ] 日志滚动策略 + `totalSizeCap` 已配置，生产级别 INFO
- [ ] Actuator 端点收敛（env 暴露需鉴权），监控告警接入
- [ ] 时区参数 `-Duser.timezone=Asia/Shanghai`
- [ ] 部署回滚脚本演练过一次

***

## 十三、网络攻击防范

> 安全总原则三条：**所有外部输入都不可信**（参数校验是第一道防线）、**纵深防御**（WAF/网关/应用/数据库层层设防，任何一层被绕过不致全线失守）、**最小权限**（数据库账号、服务器账号只给必要权限）。

### 13.1 输入校验防护（第一道防线）

#### SQL 注入

原理一句话：用户输入被拼接进 SQL，改变了原语句语义（`' OR '1'='1`）。

```java
// 正确：#{} 预编译，值作参数传递，不可能改变语法结构
WHERE username = #{username}

// 危险：${} 字符串直接替换进 SQL
ORDER BY ${orderBy}      // 用户传 "id; DROP TABLE user" 就完了
```

| 写法 | 本质 | 使用场景 |
| ---- | ---- | ---- |
| `#{}` | 预编译占位符（PreparedStatement） | 所有值，默认选择 |
| `${}` | 字符串直接替换 | 表名/列名/排序字段——**必须白名单** |

`${}` 唯一的正确用法（白名单校验）：

```java
private static final Set<String> ORDER_WHITELIST = Set.of("create_time", "id", "amount");

String orderBy = ORDER_WHITELIST.contains(query.getOrderBy())
        ? query.getOrderBy() : "id";   // 不在白名单一律回退默认值
```

配套两招：MyBatis-Plus QueryWrapper 天然参数化（`eq("username", name)`）；数据库账号最小权限（应用账号不给 DROP/FILE/GRANT，被注入也删不了表）。

#### XSS 跨站脚本攻击

三种形态：**存储型**（脚本入库，所有浏览者中招，最危险）、反射型（脚本藏在 URL 参数里）、DOM 型（前端 JS 拼接）。

前后端分离 JSON API 主要防**存储型**——昵称/评论里塞 `<script>`：

```java
// 纯文本字段：入库前转义（hutool）
String safe = HtmlUtil.escape(nickname);   // <script> → &lt;script&gt;

// 富文本字段（确实要支持 HTML）：白名单过滤（Jsoup）
String clean = Jsoup.clean(richText, Whitelist.relaxed());
```

三层防御：输入转义（核心）→ 输出上下文编码（Vue/React 默认转义；**禁用 v-html** 除非已过滤）→ CSP 响应头兜底（`Content-Security-Policy: default-src 'self'`）。

#### 命令注入 / 路径遍历

```java
// 命令注入：输入拼接命令，host 传 "8.8.8.8; rm -rf /" 直接执行
Runtime.getRuntime().exec("ping " + host);              // 危险
// 防护：能不用系统命令就不用；必须用 → 参数化传参（不经 shell 解释）+ 值校验
if (!host.matches("^[\\w.-]+$")) throw new IllegalArgumentException();
new ProcessBuilder("ping", "-c", "1", host).start();
```

```java
// 路径遍历：fileName 传 "../../etc/passwd" 读任意文件
File file = new File(baseDir, fileName);                // 危险
// 防护：规范化后必须仍在基目录内；文件名不信任用户输入（用 UUID 重命名 + DB 存映射）
String canonical = file.getCanonicalPath();
if (!canonical.startsWith(new File(baseDir).getCanonicalPath() + File.separator)) {
    throw new BusinessException(ResultCode.ILLEGAL_ARGUMENT);
}
```

延伸坑：**ZipSlip**——解压时压缩包内 entry 名含 `../`，同法校验解压目标路径。

#### 请求参数校验（JSR-303）

```java
public class CreateUserDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度 4-20")
    private String username;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
}

@PostMapping("/users")
public Result<Void> create(@Validated @RequestBody CreateUserDTO dto) { ... }
```

纪律：**Controller 校验格式，Service 校验业务规则**（账号是否已存在）；分页参数强制上限（pageSize ≤ 100，防深分页拖库）。

#### 文件上传攻击

| 防线 | 实现 | 对抗的绕过手段 |
| ---- | ---- | ---- |
| 后缀白名单 | 只允许 jpg/png/pdf，**按最后一个 `.` 判断** | `a.jsp.png` 双扩展名 |
| 文件头校验 | 读 magic number（JPEG=FFD8FF），不信 Content-Type | 改 Content-Type 的图片马 |
| 重命名 | UUID 存储，原名进 DB 映射 | 路径穿越、特殊文件名 |
| 存储隔离 | 存 Web 根目录外 / 专属 OSS，目录禁执行权限 | webshell 被执行 |
| 大小限制 | `spring.servlet.multipart.max-file-size=10MB` | 大文件耗尽磁盘/内存 |

### 13.2 会话与身份认证攻击

#### 会话劫持

攻击者拿到 sessionId 冒充用户（来源：XSS 偷 cookie、网络嗅探、会话固定攻击）。

| 防护 | 实现 |
| ---- | ---- |
| Cookie 三属性 | `HttpOnly`（JS 读不到，防 XSS 偷）+ `Secure`（仅 HTTPS 传输）+ `SameSite=Lax` |
| 防会话固定 | 登录成功后**重新生成 sessionId**，老 ID 作废 |
| 会话超时 | 30 分钟无操作过期（敏感系统 5-10 分钟） |
| 异常检测 | 异地登录提醒/踢下线；并发会话数限制 |

```yaml
server.servlet.session.cookie.http-only: true
server.servlet.session.cookie.secure: true
server.servlet.session.timeout: 30m
```

#### 暴力破解

组合拳：**失败 N 次锁定/出验证码**（Redis 计数，5 次后强制滑块）→ **限流**（账号 + IP 双维度）→ **BCrypt 慢哈希**（拖慢离线破解）→ **错误信息模糊化**（统一"用户名或密码错误"，不暴露哪个错、不暴露账号是否存在）。

#### CSRF 跨站请求伪造

原理：浏览器对已登录站点自动携带 Cookie，恶意页面诱导用户浏览器发起转账等请求。

- **前后端分离 + JWT（header 传 token）天然免疫**：token 在 JS 变量里，第三方页面无法让浏览器自动携带——"为什么用 token 不用 cookie"的标准答案
- 传统 session 方案：CSRF token（页面埋 token，请求头带回，服务端比对）+ `SameSite` Cookie
- 关键操作（支付/改密）二次验证：密码或验证码确认
- Spring Security 的 CSRF 默认开启；前后端分离项目常显式关闭改用 JWT——要知道关掉后靠什么补位

#### JWT / Token 安全

| 规则 | 说明 |
| ---- | ---- |
| 算法白名单 | 服务端校验时固定算法（HS256/RS256），**禁信任 header 里的 alg**——防 `alg:none` 攻击 |
| 必设过期 | 双 token：access 2h + refresh 7d；服务端必须校验 exp |
| payload 最小化 | 只放 userId/角色；Base64 只是编码不是加密，**能直接解出来** |
| Secret 管理 | 环境变量/Nacos；HS256 密钥 ≥ 256bit；泄露 = 全线失守 |
| 注销难题 | 无状态 JWT 无法主动失效 → Redis 黑名单 / 双 token 短时效 / 用户版本号 |
| 存储位置 | localStorage（XSS 可偷）vs HttpOnly Cookie（要防 CSRF）——讲得清 trade-off 即可 |

双 token 流程：access 过期 → refresh 换新 access → refresh 也过期才重新登录。refresh 服务端可控撤销，弥补了"JWT 没法主动踢人"的缺陷。

#### 越权攻击（渗透测试第一高频漏洞）

| 类型 | 场景 | 防护 |
| ---- | ---- | ---- |
| 水平越权 | 用户 A 把 URL `orders/1001` 改成 `1002`，看到别人的订单 | **资源归属校验**：service 层查询条件带 userId，不只查 orderId |
| 垂直越权 | 普通用户直接调 `/api/admin/delete` | 接口权限注解（`@PreAuthorize` / 自定义 `@RequiresRole`）+ 网关路由隔离管理端 |

```java
// 水平越权防护：查/改资源必须验证归属
Order order = orderMapper.selectById(orderId);
if (!order.getUserId().equals(currentUserId)) {
    throw new BusinessException(ResultCode.FORBIDDEN);   // 不是你的单子
}
```

### 13.3 网络传输与网络层防护

#### TLS / HTTPS

- 全站 HTTPS：登录/支付强制；HTTP 301 跳转；HSTS 头（`Strict-Transport-Security: max-age=31536000`）防降级攻击
- 证书：Let's Encrypt 免费 + certbot 自动续期

```nginx
server {
    listen 443 ssl;
    ssl_certificate     /etc/nginx/cert/fullchain.pem;
    ssl_certificate_key /etc/nginx/cert/privkey.pem;
    ssl_protocols       TLSv1.2 TLSv1.3;    # 禁 SSLv3 / TLS1.0 / 1.1
}
server {
    listen 80;
    return 301 https://$host$request_uri;
}
```

#### IP 黑白名单

按流量经过顺序三层实现：

```nginx
# Nginx 层：管理后台只允许办公网
location /admin/ {
    allow 10.0.0.0/8;
    deny all;
}
```

```java
// 微服务：Gateway GlobalFilter 统一拦截
// 单体：HandlerInterceptor（preHandle 里校验 IP）
```

坑：多层代理后 `request.getRemoteAddr()` 拿到的是 Nginx IP；真实 IP 在 `X-Forwarded-For`，但**该头可伪造**——只信任自己配置的代理链的最后一跳，不能无脑取第一个。

#### CORS 跨域安全

```java
// 错误：Origin 反射（把请求 Origin 原样返回）+ allowCredentials = 任意网站带 cookie 调你的接口
// 正确：白名单
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    private static final List<String> ALLOWED_ORIGINS =
            List.of("https://www.example.com", "https://admin.example.com");

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(ALLOWED_ORIGINS.toArray(new String[0]))  // 白名单，禁 *
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true)
                .maxAge(3600);   // 预检结果缓存，减少 OPTIONS 请求
    }
}
```

理解：CORS 是**浏览器**的安全机制；服务端配置过宽等于主动放弃保护。`allowedOrigins("*")` + `allowCredentials(true)` 的组合新版 Spring 会直接抛异常。

#### WAF 防护（网关层）

| 层 | 工具 | 防什么 |
| ---- | ---- | ---- |
| 云 WAF | 阿里云/腾讯云 WAF | SQL 注入、XSS、扫描器、CC（托管，省心） |
| 开源自建 | ModSecurity + OWASP CRS / 雷池 SafeLine | 同上，自己运维 |
| 网关规则 | Nginx + Lua / Sentinel | CC 攻击、IP 封禁、接口限流 |

定位认知（面试标准答法）："代码层保证注入不可能发生（参数化查询），WAF 拦掉 99% 的扫描流量、降低到达应用的噪音——**纵深防御的一环，不能替代代码级防护**（变形 payload 绕 WAF 手段很多）。"

### 13.4 攻击面自检表

| 攻击 | 一句话验证 |
| ---- | ---- |
| SQL 注入 | 全局搜 `${`，逐一确认有白名单 |
| XSS | 昵称存 `<img onerror=alert(1)>`，看列表页是否执行 |
| 越权 | 登录 A 账号，URL 里资源 id 换成 B 的，看能否读到 |
| CSRF | 接口是否仅凭 cookie 鉴权且无 token |
| 文件上传 | 改后缀的文件马上传，看能否存下来/被解析执行 |
| 暴力破解 | 连续输错 5 次密码，观察是否有验证码/锁定 |
| CORS | 看响应头 Access-Control-Allow-Origin 是不是 `*` 或反射 |

***

## 十四、基础项目功能清单（功能实现）

> 回答"一个基础项目要实现哪些功能"。优先级标注：**P0** 没有 = 项目不完整；**P1** 应该有，面试加分；**P2** 锦上添花。一个能写上简历的项目 = 全部 P0 + 大部分 P1。

### 14.1 主业务链路（P0）

```
请求 → Filter（traceId 生成）
     → 拦截器（JWT 鉴权 / 登录校验）
     → AOP（出入参日志 + 耗时）
     → Controller（@Validated 参数校验）
     → Service（业务逻辑 + 事务边界 + 资源归属校验）
     → Mapper（参数化 SQL）
     → 统一返回 Result<T>
     → 全局异常处理器兜底 → 前端渲染
```

链路每一环都对应本规范的章节（拦截器见六、异常见七、鉴权与越权见十三）。**这条链就是面试"讲讲你的项目"的故事主线**：请求从进来到返回，每一站做了什么、为什么这么做。

### 14.2 安全模块

| 功能 | 实现方案 | 优先级 |
| ---- | ---- | ---- | ---- |
| 密码加密 | BCrypt（自带盐、慢哈希）；注册 `encode`，登录 `matches`。禁 MD5（快、彩虹表） | P0 |
| 接口防重 / 幂等 | 四方案见下表，至少落地"唯一索引 + token 机制" | P0 |
| 接口限流 | Guava RateLimiter（单机）→ Redis + Lua（分布式）→ Sentinel（生产）；注解 + AOP 封装 `@RateLimit` | P0 |
| 数据脱敏 | Jackson 自定义序列化器（`@JsonSerialize`）对手机号/身份证/邮箱出参脱敏 | P0 |
| 验证码 | 行为验证码（滑块）/ hutool 图形码；登录失败 3-5 次后强制 | P1 |
| 会话管控 | 单设备在线 / 踢下线（Redis 存 userId → token 映射）、会话超时、异地提醒 | P1 |
| 文件上传管控 | 13.1 文件上传五防线 | P1 |
| 数据导出管控 | 导出鉴权 + 导出行为审计日志 + 大数据量异步导出（线程池 + 完成通知） | P2 |

幂等四方案（面试高频）：

| 方案 | 原理 | 适用 |
| ---- | ---- | ---- |
| 唯一索引 | 业务单号建 uk，重复插入报错兜底 | 最后防线，必配 |
| token 机制 | 进页面发一次性 token，提交带上，Redis 校验后删除 | 防表单重复提交 |
| 状态机 | 只允许合法状态流转（已支付不能再支付） | 有状态的业务对象 |
| 分布式锁 | Redis SETNX 抢锁，串行化重复请求 | 并发写同一资源 |

### 14.3 基础框架能力

| 功能 | 要点 | 优先级 |
| ---- | ---- | ---- | ---- |
| 全局异常 + 统一返回 + 错误码 | 见第七章；错误码枚举按模块分段 | P0 |
| 全局请求/响应拦截 | traceId、鉴权、访问日志三件套（见第六章） | P0 |
| 通用分页组件 | PageQuery 入参 + PageVO 出参 + MP 分页插件 | P0 |
| API 文档管控 | Knife4j：dev 开 / prod 关（`springdoc.api-docs.enabled=false`）、文档访问密码、接口分组 | P0 |
| 测试基座 | 造数工具类、单测 `@Transactional` 自动回滚、冒烟用例基线（登录 → 下单 → 查询主链路） | P1 |
| 通用文件服务 | 上传/下载/预览统一封装，本地存储与 OSS 可切换 | P2 |

### 14.4 运维监控

| 功能 | 实现方案 | 优先级 |
| ---- | ---- | ---- | ---- |
| 日志五件套 | 业务日志（AOP 出入口）、框架日志（logging.level）、GC 日志、慢 SQL 日志、崩溃日志兜底（hs_err）——详见第六章与《日志全景》 | P0 |
| TraceId 全链路 | Filter + MDC + Feign 透传，日志和返回体都带 | P0 |
| 服务指标监控 | Actuator（health/metrics）→ Prometheus 抓取 → Grafana 看板：JVM、CPU、线程池、连接池四大看板 | P1 |
| 接口监控 | Micrometer 打点：QPS、耗时 P95/P99、错误率 | P1 |
| 异常告警 | Grafana 告警规则 → 钉钉/邮件 webhook；EFK 侧 ERROR 日志突增告警 | P1 |

### 14.5 清单的用法：变成面试弹药

每个功能按"三段式"准备：**为什么需要（场景）→ 怎么实现（方案对比）→ 踩过什么坑（细节）**。

例：限流不能只说"用了 Guava"，而是——"压测发现下单接口被打挂 → 单机 RateLimiter 先挡住 → 部署两台后限流不齐 → 换 Redis + Lua 分布式限流，Lua 脚本保证判量+扣减原子性"。一个功能讲成一条演进故事，胜过罗列十个功能名。

***

## 十五、对照自查（新人前三个月高频打回项）

- [ ] Controller 里有业务逻辑 → 挪到 Service

- [ ] 直接返回 entity 给前端 → 转 VO

- [ ] 魔法值遍地 → 常量类/枚举

- [ ] @Transactional 没写 rollbackFor

- [ ] 循环里查数据库 → 批量查 + Map 组装

- [ ] 日志字符串拼接 → 占位符

- [ ] log.error 只拼了 e.getMessage() 没带堆栈

- [ ] 循环体内打高频日志 → 移出循环或降频

- [ ] 异常被 catch 后什么都不干

- [ ] 新表没建索引 / 没 COMMENT

- [ ] 密码写进了 application.yml 并提交

- [ ] commit message 是 "update"

- [ ] 没自测就提测

- [ ] 线上出问题直接重启（没先留 dump / jstack 现场）

- [ ] 代码里有 `${}` 拼接且没做白名单

- [ ] 查询/修改资源没校验归属（水平越权漏洞）

- [ ] 上传文件只校验了后缀，没查文件头

- [ ] CORS 配了 `*` 还开了 allowCredentials

## 十六、三层缓存

# SpringBoot 企业级网站完整能力清单

分为：**基础框架能力、用户权限体系、业务核心、存储、对外调用、运维监控、安全、前端交互适配、扩展能力**。

**🔐安全模块**

- 密码加密：BCrypt、MD5 加盐、SHA256
- 接口防重放：时间戳 + nonce 随机签名
- 数据脱敏：数据库存储脱敏、接口返回脱敏（手机号、身份证）
- 业务数据 AES 加密传输
- 登录防护：登录失败限流、账号锁定
- 会话管控：Token 有效期、强制下线、多端登录控制
- XSS、SQL 注入防护，CSRF 防护
- IP 黑白名单访问控制
- 敏感操作二次身份校验
- 安全审计日志（不可篡改，敏感信息禁止打印日志）
- 密钥统一管理、密钥轮换
- 文件上传安全校验

**👤用户权限体系**

- 用户账号生命周期：启用 / 禁用 / 冻结
- 角色管理，用户多角色绑定
- 按钮级权限
- 数据行级权限
- 字段级权限
- 组织 / 部门维度数据隔离
- 权限变更审计记录

**🧱基础框架能力**

- 全局统一异常处理、统一返回体、统一错误码
- 通用分页组件
- 系统字典管理
- 定时任务调度（启停、重试、执行日志）
- 通用文件上传下载服务

**💾存储能力**

- 数据库事务，分布式事务
- Redis 缓存 + 缓存防护（击穿 / 穿透 / 雪崩）
- 数据库乐观锁 / 悲观锁并发控制
- 数据库定时备份与恢复

**📞对外调用**

- 第三方调用超时、重试、熔断降级
- 调用日志记录
- 异步调用、失败回调
- 下游调用限流

**📊运维监控**

- TraceId 全链路追踪
- 服务指标监控（JVM、CPU、线程池、连接池）
- 接口 QPS、耗时、错误率监控统计
- 异常告警（钉钉 / 邮件）
- 业务操作日志 + 安全审计日志分离
- 服务健康检查

**💻前端交互适配**

- 根据权限动态渲染按钮、表格列
- 表单前后端双重校验
- 大数据列表分页 / 虚拟滚动
- 统一请求错误处理

**⚙️扩展能力**

- 配置中心热更新系统参数
- AOP 切面扩展点
- API 接口版本兼容

**📦通用业务底座**

- 分布式唯一 ID 生成器
- Excel 批量导入导出（异步大文件导出）
- 站内消息通知

**完整链路请求：**  SpringBoot 接收请求 → 参数校验 → 登录鉴权 (JWT) →会话管理 → 业务逻辑 → 调用数据库 → 返回结果，全局异常处理→ 返回前端渲染

**完整响应链条**：

```
前端请求
  → Nginx 反向代理 + 跨域处理
  → 过滤器链：字符编码 → CORS → 请求体重复读
  → 拦截器：JWT 鉴权 → Redis 校验 Token 有效性 → 获取当前用户
  → AOP 切面：操作日志记录 → 接口限流校验 → 幂等令牌校验
  → Controller 层：参数校验（@Valid） → 接收请求 → 调用 Service
  → Service 层：业务逻辑处理 → 事务控制 → 读写 Redis 缓存 → MySQL
  → Mapper 层：MyBatis-Plus 操作 MySQL → 数据返回
  → 结果封装：统一 Result<T> 包装 → 异常统一捕获转换
  → 响应返回前端
```

# 网络防范

## 一、输入校验类（注入攻击防护）

1. **SQL 注入**

- 禁止拼接 SQL 字符串，使用**预编译参数化查询**
- ORM 框架（MyBatis‑Plus、JPA）避免 ${}，优先 #{}
- 最小权限数据库账号，禁止 root 业务账号

1. **XSS（虽然主要前端，但后端也要兜底）**

- 对用户输入做 HTML 转义，过滤危险标签、脚本
- 输出到页面时做转义，不要直接把用户输入原样返回

1. **命令注入**

- 避免调用系统 shell 执行用户传入参数；必须调用时使用白名单过滤参数

## 二、身份认证 & 会话安全

1. **暴力破解 / 字典攻击**

- 账号密码强度校验，密码哈希存储（BCrypt/Argon2，**禁止明文存密码**）
- 登录失败限流、IP 黑名单、验证码、账号锁定

1. **CSRF 跨站请求伪造**

- 后端校验 CSRF Token；使用 JWT 时，不要放在 Cookie 里，放请求头；
- 配置 SameSite=Strict/Lax Cookie 属性

1. **会话劫持**

- Session 设置 HttpOnly、Secure；会话超时；登录后销毁旧 Session；
- JWT 设置过期时间，支持黑名单（Redis 存作废 token）

## 三、越权漏洞（后端高频高危）

> 重点：**不能只靠前端判断权限，后端必须校验**

- **水平越权**：查询数据时，必须带上当前登录用户 ID 做条件，不能只拿前端传过来的 id 查询
- **垂直越权**：接口调用前校验角色权限，判断是否拥有该接口操作权限

> 示例：`/user/info?id=100`，不能直接查 id=100，要校验当前登录用户是否允许访问该 id

## 四、文件上传漏洞

- 校验文件后缀、文件头魔数，禁止上传可执行脚本（jsp/php 等）
- 文件存储不要存到 web 可访问目录，使用对象存储 MinIO/OSS；重命名文件，防止路径遍历

## 五、DDoS / CC 攻击防护

- 接口限流：Redis 实现接口 QPS 限流、IP 限流
- 网关层（Spring Cloud Gateway）做流量控制；
- 业务层避免无缓存的重查询，防止大量请求打数据库；
- 外部配合云服务商 DDoS 防护

## 六、业务逻辑漏洞

1. 防止重放攻击：请求携带 nonce 随机数 + 时间戳，Redis 校验，拒绝重复请求
2. 防止金额篡改：服务端重新计算金额，**不能信任前端传过来的金额**
3. 防止参数篡改：敏感参数后端重新计算校验

## 七、其他安全

1. **接口返回信息脱敏**：不要返回完整手机号、身份证、密码等敏感信息
2. 错误信息不要返回详细堆栈，统一返回友好提示，避免泄露框架、版本信息
3. 依赖组件安全：定期扫描依赖漏洞（Dependency‑Check、Snyk），升级存在 CVE 的组件
4. 避免路径遍历：文件路径参数做白名单校验，不要直接拼接用户输入路径

# ✅（最小化-Demo）

接收请求 → 参数校验 → 登录鉴权 (JWT) → MySQL 存用户、对话历史 → Redis 存 token → 业务逻辑 → 调用 LLM → 返回结果，全局异常处理。

## 基础必备

- 用户注册/登录/第三方登录 互相绑定 — sa-token-jwt 权限认证，密码加密存储
- CRUD 完整闭环 — 增删改查 + 分页 + 搜索 
- 权限管理 — 至少管理员/普通用户两级
- 全局跨域处理   CORS，前后端分离必备
- 数据库设计 — 5-8 张核心表，有关联关系
- API 接口 — RESTful 规范
- 自定义业务异常继承 RuntimeException，携带业务码 + 消息，和系统异常区分
- 错误处理 — 全局异常处理 + 友好提示
- 统一参数校验`@Valid` + 自定义校验注解，请求参数非空、长度、手机号格式校验，区分业务校验异常
- 代码规范 — 统一风格 + 注释完整+文件夹+包名+类名
- 字典枚举统一管理状态码、用户类型、账号状态枚举，避免硬编码魔法值
- 分页统一封装统一 PageVO、PageDTO，不要每个接口手写分页返回结构
- 接口防重放：时间戳 + 随机 nonce 签名  
- 接口幂等性
- 数据脱敏：数据库存储脱敏、接口返回脱敏（手机号 / 身份证） 
- 数据加密传输
- README — 项目介绍 + 启动说明 + 技术栈
- 接口日志（请求日志）记录：请求 URL、请求方式、IP、请求参数、响应耗时、操作人 ID；可用于审计、排查问题
- 接口请求日志+IP 工具类获取客户端真实 IP，防刷、登录 IP 记录
- 文件上传
- 配置环境分离
- 事务管理

## 页面需求

### 一、前台页面（游客 + 普通用户）

| 页面        | 核心功能                                 | 优先级 |
| :---------- | :--------------------------------------- | :----- |
| 首页        | 导航、轮播/推荐位、内容列表入口、搜索框  | MVP    |
| 登录/注册   | 账号密码、验证码、忘记密码               | MVP    |
| 内容列表页  | 分类筛选、排序、分页（列表类站点的主体） | MVP    |
| 内容详情页  | 正文展示、点赞/收藏、评论区              | MVP    |
| 搜索结果页  | 关键词结果、高亮、分页                   | MVP    |
| 个人中心    | 资料、头像、密码修改                     | MVP    |
| 关于/帮助页 | 站点介绍、FAQ、联系方式                  | MVP    |

**按需增加（取决于产品形态）：**

| 页面         | 什么时候需要                   |
| :----------- | :----------------------------- |
| 发布/编辑页  | UGC 产品（用户能投稿内容）     |
| 我的发布管理 | UGC 产品配套                   |
| 消息中心     | 有互动（评论/点赞/系统通知）时 |
| 收藏/历史    | 有收藏和浏览行为时             |
| 用户主页     | 有社交属性时                   |
| 分类频道页   | 内容分类多于 2 级时            |

### 二、后台管理页面（管理员）

后台是高度同构的，核心就这几类：

| 页面          | 核心功能                       | 优先级 |
| :------------ | :----------------------------- | :----- |
| 登录页        | 管理员独立入口、防暴力破解     | MVP    |
| 工作台/仪表盘 | 数据概览、待办（待审核数）     | MVP    |
| 用户管理      | 列表、搜索、封禁、重置密码     | MVP    |
| 内容管理      | 列表、审核通过/驳回、上架/下架 | MVP    |
| 评论管理      | 举报处理、删除                 | MVP    |
| 分类/标签管理 | 分类 CRUD、排序                | MVP    |

**按需增加：**

| 页面             | 什么时候需要                               |
| :--------------- | :----------------------------------------- |
| 操作日志（审计） | 有敏感操作（封号、删除内容）时，强烈建议有 |
| 轮播/推荐位管理  | 首页有运营位时                             |
| 公告管理         | 需要给用户发系统通知时                     |
| 角色权限管理     | 管理员多于一种角色时（RBAC）               |
| 字典/配置管理    | 有较多状态项、开关项时                     |
| 系统监控         | 自部署、无专业运维时                       |

### 三、公共页面（任何项目都一样）

- 404 页、403 无权限页、500 错误页、维护中页面
- 空状态/加载中/骨架屏（组件级，不是页面）

## 安全相关

1. **请求限流（接口防刷）** 基于 IP + 用户 ID 限流，防止暴力注册、密码爆破；Sa-Token 自带限流，开箱即用。

2. **XSS 防护** 入参过滤 / 转义，防止前端注入恶意脚本。

3. 敏感字段存储：区分【加密】vs【脱敏】

   > 你写了数据库存储脱敏：⚠️ 脱敏是**单向掩码**（例如 138****1234，不能还原）；如果需要**原始数据可解密**，要使用**AES 对称加密存储**。
   >
   > - 脱敏：仅展示用，无法解密；
   > - 加密存储：数据库存密文，业务可解密拿到明文。 手机号 / 身份证：一般**数据库 AES 加密存储，接口返回脱敏掩码**。

4. 登录安全策略

   - 登录失败次数锁定（防暴力破解）
   - 单点登录 / 同账号多端登录控制（Sa-Token 支持）
   - 登录日志：记录登录时间、IP、设备、登录状态（成功 / 失败）

5. 防重放补充：**签名有效期校验**（单纯 timestamp+nonce 不够，必须校验时间窗口，例如 5 分钟内有效），过期请求直接拒绝；nonce 存入 Redis 校验一次性使用。

## 缓存层

>  三层缓存：**本地内存缓存 (Caffeine) → Redis 分布式缓存 → 数据库**，属于读多写少场景的经典多级缓存方案，非常适合放进你的 SpringBoot 骨架项目。

| 层级          | 组件                 | 作用                           | 特点                                 |
| ------------- | -------------------- | ------------------------------ | ------------------------------------ |
| L1 本地缓存   | Caffeine（JVM 堆内） | 热点数据，离应用最近，速度最快 | 进程内，单机有效；重启丢失；容量有限 |
| L2 分布式缓存 | Redis                | 集群共享缓存，减轻 DB 压力     | 跨实例共享；网络 IO；可设置过期时间  |
| L3 持久层     | MySQL/H2             | 原始数据存储                   | 持久化，最慢，最终数据源             |

## 接口文档

**Knife4j / SpringDoc OpenAPI3**，自动生成 API 文档，参数、返回示例，前后端联调必备。

## 测试

单元测试

冒烟测试

功能测试 

回归测试

## 配置与环境隔离

- 多环境配置 `application-dev.yml / application-prod.yml`
- 敏感配置（密钥、数据库密码、邮箱授权码）**使用环境变量注入，禁止硬编码到配置文件**（就是你刚才研究的环境变量）
- 配置读取统一管理，自定义配置类`@ConfigurationProperties`

##  运维 / 可观测基础（可选，工程化加分）

- 日志规范：SLF4J + Logback，日志分级输出，区分控制台 / 文件日志
- 全局日志 ID（TraceId）：一次请求全链路日志带上 traceId，方便排查问题
- 健康检查端点：SpringBoot Actuator `/actuator/health`，用于服务监控
- 日志文件 Logback (info warn error ) 这三种日志  GC日志 JVM堆崩溃日志 数据库日志 Redis日志 等统一监控

## 工程化

- Docker 部署 — Dockerfile + docker-compose
- CI/CD — GitHub Actions 自动部署
- SonarQube：代码规范、漏洞检测；Trivy 容器漏洞扫描；OWASP ZAP 动态安全扫描。
- 单元测试 — 核心业务逻辑测试覆盖率 60%+
- 缓存 — Redis 缓存热点数据
- 消息队列 — 异步处理场景（邮件/短信/通知）
- 接口文档 — Swagger/OpenAPI 自动生成
- 日志 — 结构化日志 + 请求链路追踪 ID
- 环境区分 — dev/test/prod 多环境配置
- GitFlow规范 — 分支管理 + Commit 规范

## 进阶

- 锁 — 秒杀/抢购场景
- 事务
- 搜索引擎 — Elasticsearch 全文检索--语义搜索
- 关键词屏蔽 
- 日志分析----EFK
- openfien-dubbo远程调用 
- kafka 消息队列
- 链路追踪 SkyWalking / Sleuth+Zipkin
- 性能优化 — 接口从 1s 优化到 100ms 的过程
- 安全加固 — XSS/CSRF/SQL 注入防护
- 压测报告 — JMeter 压测数据 + 优化过程
- 监控告警 — Prometheus + Grafana 看板
- 技术博客 — 记录项目中的技术难点与解决方案
- SQL 优化：索引、避免 select *、分页优化、避免大事务
- 缓存优化：三层缓存，减少 DB 访问
- 异步化：Kafka 异步剥离非核心流程
- 序列化优化：Jackson 替换、Protobuf
- 连接池调优：HikariCP、Redis 连接池、Tomcat 线程池
- JVM 调优：GC 参数、内存模型
- 锁优化：减少锁粒度、避免锁竞争

## 加分项

- AI 对话助手 — 接入大模型 API
- RAG 知识库 — 基于项目文档的问答
- 智能推荐 — 个性化内容/商品推荐
- 语义搜索 — 向量检索替代关键词匹配
- 告警系统 
- 全日志观察系统