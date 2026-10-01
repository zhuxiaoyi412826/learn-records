### 5.1 基础题

1. **@Controller 与 @RestController 的区别？** 后者 = 前者 + @ResponseBody，方法返回值直接写响应体（经 Jackson 序列化），不再走视图解析器；写页面跳转接口用前者 + 返回视图名。

2. **@RequestParam 与 @PathVariable 的区别？** 前者取查询串/表单（`?page=1`），后者取路径段（`/problems/42`）；资源 ID 用 PathVariable 体现 RESTful 资源定位，筛选分页条件用 RequestParam。

3. **@RequestBody 有什么限制？** 一个方法最多一个（请求体流只能读一次）；要求 Content-Type 与 HttpMessageConverter 匹配（JSON 场景常见 415 就是这层拒绝的）；与 GET 搭配无意义。

4. **GET 和 POST 的本质区别？** 语义上 GET 是安全幂等的读取、POST 是非幂等的提交；报文上 GET 参数在 URL、POST 在体；安全上两者都是明文（HTTPS 才加密），"GET 参数暴露"不是本质区别。

5. **什么是 Servlet？生命周期？** Java Web 的服务器端组件规范；init（一次）→ service（每次请求，按方法分发）→ destroy（一次）；单实例多线程，禁止可变成员变量。

6. **DispatcherServlet 处理一次请求的流程？** service → HandlerMapping 找 Handler 与拦截器链 → preHandle → HandlerAdapter 做参数解析/校验并反射调用 Controller → 返回值经 HttpMessageConverter 写响应 → postHandle → afterCompletion；任何环节异常交给 HandlerExceptionResolver（@RestControllerAdvice）。

7. **拦截器和过滤器的区别？** Filter 属 Servlet 容器规范、拦所有请求、拿不到目标方法；Interceptor 属 SpringMVC、只拦 DispatcherServlet 的请求、能拿 Handler 元数据；执行顺序 Filter → Interceptor → Controller → Interceptor → Filter。

8. **转发和重定向的区别？** forward 服务器内部跳转（一次请求，浏览器无感知，可共享 request 域数据）；redirect 返回 302 让浏览器发新请求（两次请求，地址栏变化，跨域可用）。前后端分离下后端基本只返回数据，两者都少用，但原理要会说。

9. **Cookie 和 Session 的关系？Session 和 JWT 的区别？** Cookie 是浏览器端存储与自动携带机制，Session 是服务端状态，靠 Cookie 里的 JSESSIONID 关联；JWT 把状态编码进 token 本身，服务端只验签不存储（实战中黑名单/refresh 仍需 Redis，是半状态）。

10. **JWT 的三段结构？签名等于加密吗？** Header.Payload.Signature；签名是防篡改不是保密，Payload 是 Base64 可解码，禁止放敏感信息。

11. **什么是幂等性？哪些 HTTP 方法幂等？** 同一请求重复执行结果不变；GET/PUT/DELETE 幂等，POST 不幂等；幂等性设计手段：唯一索引、token 机制、状态机、乐观锁。

12. **为什么要 DO/DTO/VO 分层，禁止 DO 直接返回？** 解耦表结构与接口契约；防止懒加载序列化问题；雪花 Long 前端精度丢失；字段最小暴露原则（安全）。

13. **统一响应为什么要封装 Result？** 前端拦截逻辑只写一次；错误码集中管理；异常路径与正常路径返回结构一致；网关/监控可按 code 聚合。

14. **@RestControllerAdvice 的原理？** 本质是 @ControllerAdvice + @ResponseBody；被 ExceptionHandlerExceptionResolver 收集，DispatcherServlet 捕获异常后按异常类型找最匹配的 @ExceptionHandler 方法执行，返回值走正常消息转换流程。

15. **跨域为什么发生？什么是预检请求？** 浏览器同源策略限制 Ajax 跨域读响应（服务端其实收到了请求）；非简单请求先发 OPTIONS 预检确认服务器允许（方法、头、origin），通过后真请求才发出；预检结果可被 `maxAge` 缓存。

### 5.2 进阶题

1. **完整讲一次 POST JSON 请求的链路。** 按 1 节全景图顺序讲：Filter 链 → DispatcherServlet → HandlerMapping → preHandle → 参数绑定（HandlerMethodArgumentResolver 从请求体读流，MappingJackson2HttpMessageConverter 反序列化为 DTO）→ @Validated 校验 → Controller → AOP/事务代理 → Service → Mapper → VO → 序列化 → postHandle → afterCompletion → Filter 写回。能加上异常分叉是加分项。

2. **@Transactional 失效场景，至少五个。** 见 2.10 表格；每个都要能说出根因（绕过代理 / 非公有 / 异常被吞 / rollbackFor 缺失 / 跨线程 / 引擎不支持）。

3. **REQUIRES_NEW 与 NESTED 的区别？** REQUIRES_NEW 是完全独立的新事务，内层提交后外层回滚不影响内层；NESTED 是保存点，外部回滚会连带回滚到保存点；NESTED 依赖 JDBC 保存点支持。

4. **MySQL 默认隔离级别下有什么并发问题？** InnoDB 默认 REPEATABLE READ，通过 MVCC + 间隙锁基本解决幻读；常见追问 RR 与 RC 的差异、MVCC 的 undo log 版本链。

5. **JWT 如何实现登出？** 纯 JWT 无法主动失效，三种工程方案：黑名单（登出把 jti 写 Redis，TTL 为剩余有效期）；token 版本号（用户表存版本，改版本使全部旧 token 失效，支持踢人）；refresh 白名单（refresh 存服务端，登出即删，accessToken 等自然过期）。

6. **双 token 机制为什么存在？** accessToken 短有效控制泄露窗口，refreshToken 长有效免频繁登录；refresh 接口独立、token 存服务端可控可吊销；本质是在"无状态便利"与"可控性"之间折中。

7. **雪花 ID 前端精度丢失的原因与方案？** JS Number 是 64 位浮点，安全整数上限 2^53 - 1（约 9 千万亿），雪花 19 位超限，末几位变 0；方案：VO 字段 @JsonSerialize(ToStringSerializer) 或全局 Long → String 序列化。

8. **Jackson 对 Date/LocalDateTime 的全局配置？** `spring.jackson.date-format` + `time-zone` 处理 Date；LocalDateTime 需要 jackson-datatype-jsr310 模块（Boot 自动注册）+ `spring.jackson.date-format` 不直接生效，用 @JsonFormat 或自定义 ObjectMapper；时间链路统一用 LocalDateTime + 显式格式。

9. **#{} 与 ${} 的区别？什么场景必须用 ${}，怎么保证安全？** 预编译占位 vs 字符串拼接；动态表名、排序列只能 ${}；必须白名单校验（列名集合 contains 判断），绝不拼接用户原始输入。

10. **MP 乐观锁的实现原理与局限？** @Version 字段，UPDATE 时自动带 `WHERE version = #{oldVersion} AND ...`，更新行数为 0 即冲突；局限：只保护单行更新，冲突高频时重试成本高，不适合复杂一致性场景（那要上悲观锁或分布式锁）。

11. **ThreadLocal 存用户上下文，为什么异步线程拿不到？怎么解决？** ThreadLocal 的数据挂在线程对象上，线程池线程是另一个线程；解法：TransmittableThreadLocal（阿里 TTL，配合 TtlExecutors 包装线程池）、或者提交任务时显式传 userId 参数；同时记得 afterCompletion 清理防串号。

12. **AOP 与 Filter/Interceptor 的选型标准？** 见 2.13 末段：字节流层 Filter、Handler 元数据层 Interceptor、方法级 AOP；再能各举两个真实场景。

13. **业务码与 HTTP 状态码怎么设计？** 两种流派（全 200 + 业务码；鉴权 401/403、参数 400、业务错 200 + code）；关键在团队一致 + 网关可监控 + 前端拦截器逻辑简单；说出自己项目的选择和理由即可。

14. **Bean 的生命周期？** 实例化 → 属性填充 → Aware 回调 → BeanPostProcessor 前置 → 初始化（@PostConstruct / afterPropertiesSet）→ BeanPostProcessor 后置（AOP 代理在这步生成）→ 使用 → 销毁；三级缓存解决 setter 循环依赖，但构造器循环依赖无解（直接报错）。

### 5.3 场景排查题

1. **线上接口偶发 500，日志里找不到堆栈。** 排查路径：全局异常兜底是否只 log 了 message 没带异常对象；异步线程里抛的异常是否绕过了主线程日志上下文；log 级别配置是否把 error 落到别的 appender；是否被 try-catch 吞了只返回统一错误。

2. **POST 提交中文乱码。** 三层排查：自定义 Filter 是否抢在 CharacterEncodingFilter 之前读了流；MySQL 连接串是否缺 `characterEncoding=utf8`（存进去就乱了）；响应是否漏了 charset。先在 DB 客户端确认库里数据是否正常，定位是入库前乱还是出库后乱。

3. **前端拿到的 id 和数据库对不上，末尾变 0。** 雪花 Long 精度丢失；确认响应 JSON 里 id 是数字还是字符串；修复：VO 加 ToStringSerializer 或全局配置。

4. **并发扣库存超卖。** 先复现（压测脚本）；方案对比：数据库原子 UPDATE（`stock = stock - 1 WHERE stock > 0`，最简单可靠）、乐观锁版本号（冲突重试）、悲观锁 `FOR UPDATE`（热点行锁竞争大）、Redis 预扣 + Lua（高并发）；能说出适用并发量级。

5. **token 疑似泄露。** 缩短 accessToken 有效期；核查 refresh 逻辑是否可被重放（一次性）；版本号机制强制下线；日志排查泄露源头（是否打印了 token）。

6. **公开接口被刷，服务器 CPU 飙高。** 单机先上 Guava RateLimiter（注解 + AOP）；多节点用 Redis + Lua 滑动窗口；网关层限流（Nginx limit_req）兜底；区分用户级与 IP 级维度。

7. **事务方法里调外部 HTTP 接口超时 30 秒，拖垮连接池。** 长事务问题：事务里只做本地 SQL，外部调用移出事务边界（先查再事务内写，或事务提交后再调用）；HikariCP `maximum-pool-size` 被长事务占满的连锁反应也要能讲。

8. **部署双节点后用户频繁掉线。** Session 存单机内存 + 负载均衡不粘滞；解法：Nginx ip_hash（治标）、Spring Session + Redis 共享（标准解）、换 JWT（无状态）；顺带能讲 Session 方案与 JWT 在此场景的取舍。

### 5.4 手写题

面试现场高频手写，平时练习限时 10 分钟一道：

1. **手写 Result + ErrorCode 枚举 + BizException**（三者配套，见 2.6、2.7）。
2. **手写全局异常处理器**，含 MethodArgumentNotValidException 的字段级消息组装。
3. **手写 JWT 工具类**：生成（payload 含 userId/exp/jti）、解析验签、判断过期。
4. **手写登录拦截器 + WebMvcConfigurer 注册**，含放行路径与 ThreadLocal 清理。
5. **手写防重复提交注解**：`@NoRepeatSubmit` + AOP，以"用户 + 方法 + 参数 hash"为 key 写 Redis SETNX，TTL 几秒，冲突抛 BizException。

### 其他

1. 拦截器和过滤器的区别与执行顺序

2. JWT 怎么实现登出

3. 全局异常处理是怎么做的

4. Long 类型 ID 直接返回前端有什么问题

5. 一次 HTTP 请求在 SpringMVC 中的完整流转（DispatcherServlet 必背）

6. 接口幂等性怎么保证

7. 用户注册接口完整执行流程，密码为什么不能明文存入数据库，用什么加密算法？

8. JWT 过滤器在整个请求链路哪个环节执行？

9. 登出为什么需要 Redis 黑名单，原生 JWT 存在什么缺陷？

10. DO、DTO、VO 在注册登录场景各自作用，为什么不能直接返回 DO 给前端？

11. @Transactional 在注册场景作用，什么时候会失效？

12. **面经**

    **Web 注解、请求相关**

    1. `@RestController` 由哪两个注解组成？
    2. `@GetMapping`、`@PostMapping`区别，什么时候用 POST？
    3. `@PathVariable / @RequestParam / @RequestBody` 三者接收参数位置、Content-Type 要求；一个接口可以有几个`@RequestBody`？
    4. `@RequestHeader`作用，举一个使用场景（拿 token）
    5. JSR303：`@NotBlank` 和 `@NotNull` 的区别；`@Valid` 和 `@Validated`差别，分组校验适用场景
    6. 参数校验和参数过滤有什么不同？参数过滤用来防御什么攻击？

    **Servlet / Filter / Interceptor / AOP（重点区分）**

    1. Servlet 三大生命周期方法？DispatcherServlet 是什么？
    2. Filter、HandlerInterceptor 执行顺序；二者属于哪一层？能不能拿到 Controller 方法参数？
    3. 写请求完整执行链路：浏览器发起请求，从进入服务器到返回结果完整流程（Filter→Interceptor→Controller→AOP）

    **异常、统一返回、跨域**

    1. `@RestControllerAdvice`作用，搭配什么注解捕获异常？
    2. 自定义业务异常 BusinessException 怎么设计？
    3. 全局跨域三种实现方式，全局 CORS 配置原理

    **文件上传、接口文档、序列化**

    1. MultipartFile 文件上传，怎么限制文件大小？MinIO / 本地存储区别
    2. Knife4j (SpringDoc) 常用注解`@Tag @Operation`作用
    3. Jackson：`@JsonFormat`作用；雪花 ID Long 转 String 解决什么问题？`@JsonIgnore`什么时候用？

    **认证授权**

    1. JWT 的三段结构；登录签发、校验流程；原生 JWT 为什么需要黑名单才能实现登出？
    2. Sa-Token 可以实现哪三大能力；对比手写 JWT 有什么优缺点
    3. SpringSecurity 三大核心功能

    **其他**

    1. forward 转发 和 redirect 重定向区别
    2. Session 原理，Redis 共享 Session 解决什么问题
    3. RESTful 幂等性有哪些实现方案？

13. **在你oj项目上改造**

    1. 找出项目所有 DTO，**补全缺失的 JSR303 参数校验**，全部 Controller 入参加上`@Valid`，测试非法参数能不能被拦截
    2. 新增一个拦截器，记录接口访问日志（IP、请求地址、耗时），区分 Filter 和 Interceptor
    3. 把部分 JWT 鉴权迁移成 Sa-Token，保留原有登录逻辑，使用注解鉴权替代过滤器手写判断
    4. 新增一个 SSE 接口，模拟 AI 流式返回
    5. 新增文件下载接口，设置`Content-Disposition`实现浏览器弹出下载

    > ✅ 达标标准：改动后原有业务全部正常运行，无空指针、无 50 异常

14. **手写一个聊天项目实现如下功能**

    1. 注册：用户名重复时，数据库唯一索引 / 代码校验抛出业务异常，全局异常捕获，返回友好提示；密码加密存入数据库。
    2. 登录：账号密码错误返回提示；登录成功下发 JWT。
    3. 鉴权：不带 token /token 篡改 /token 过期 / 加入黑名单，访问`/api/user/info`直接返回 401。
    4. 登出接口：把当前 JWT 放入 Redis 黑名单，该 token 立刻失效。
    5. 所有入参 DTO 校验生效（空账号、非法长度直接拦截，不进入数据库查询）
    6. 数据库查询正常，返回 VO**不含密码**。
    7. Knife4j 文档可以调试全部接口。