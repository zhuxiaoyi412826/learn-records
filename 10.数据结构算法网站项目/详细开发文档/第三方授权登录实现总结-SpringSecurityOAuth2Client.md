# 第三方授权登录（GitHub / Gitee）实现总结 —— Spring Security OAuth2 Client

> 版本：v1.0 ｜ 日期：2026-09-06
> 项目：AlgoVize 前台门户第三方登录
> 配套：[第三方授权登录测试文档-OAuth2.md](第三方授权登录测试文档-OAuth2.md)
> 后端代码入口：`houduan/src/main/java/com/algoviz/config/security/`

---

## 一、Spring Security OAuth2 Client 是什么

Spring Security 对 **OAuth 2.0 / OIDC** 的客户端（Client）能力封装，我们只用了其中 `oauth2Login` 一族（授权码模式 Authorization Code）：

| 术语 | 本项目对应角色 |
|---|---|
| Resource Owner | 网站用户 |
| Client | 本系统后端（AlgoViz） |
| Authorization Server | GitHub / Gitee |
| Resource Server（UserInfo） | GitHub API `/user`、Gitee API `/v5/user` |

一句话：**它把“跳第三方授权页 → 拿授权码 → 换 token → 拉用户信息”整套协议流程做成了框架内置过滤器**，我们只需要：
1. 注册第三方应用信息（ClientRegistration）；
2. 配置好 SecurityFilterChain（oauth2Login）；
3. 在 SuccessHandler 里把第三方用户映射成本地账号。

### 授权码模式时序（Spring 内部已自动完成 ①~⑤）
```
① 浏览器 → GET /oauth2/authorization/{平台}
② 302 → GitHub/Gitee 授权页（带 client_id/redirect_uri/state）
③ 用户在第三方确认后回跳  /login/oauth2/code/{平台}?code=..&state=..
④ Spring 用 code POST 到 token-uri 换取 access_token（并校验 state 防 CSRF）
⑤ Spring GET user-info-uri 拿用户资料 → 生成 OAuth2AuthenticationToken
⑥ 我们的 AuthenticationSuccessHandler 接管：绑定/注册 → 建立站内登录态 → 302 回前端
```

### 关键 Spring 组件
- **ClientRegistration**：一个第三方应用的登记信息（id/secret/端点/scope/回调）
- **ClientRegistrationRepository**：登记表仓库（框架用它找 `github`/`gitee` 配置）
- **HttpSecurity.oauth2Login(...)**：激活第②~⑤步的过滤器链
- **AuthenticationSuccessHandler / FailureHandler**：成功/失败后自定义处理

---

## 二、为什么用它（方案选型）

| 对比项 | 自研 OAuth2 客户端 | Spring Security OAuth2 Client |
|---|---|---|
| 协议细节（state/CSRF/token/错误处理） | 全部手写，易错 | 框架内置、经过广泛验证 |
| 多平台扩展 | 每个平台都要造轮子 | 只加一个 Registration + 端点配置 |
| 证书/重试/超时 | 手搓 | 框架处理 |
| 学习成本 | 低 | 需理解过滤链与双轨鉴权兼容 |

本项目选择它，还因为要同时支持 **GitHub + Gitee 双平台**，用框架把协议差异收敛到“配置 + Handler”。

---

## 三、怎么用（项目落地的关键代码）

### 3.1 依赖（pom.xml）
```xml
<!-- 第三方授权登录：Spring Security OAuth2 Client
     注意：会连带引入 spring-boot-starter-security，
     必须在 SecurityConfig 中放行除 OAuth 端点外的所有请求，
     避免与前台 AuthInterceptor(Session) / 后台 Sa-Token 双轨鉴权冲突 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

### 3.2 平台凭据走环境变量（禁止明文入库）
`OAuth2SecurityConfig` 内：
```java
@Value("${OAUTH_GITHUB_CLIENT_ID:}")   private String githubClientId;
@Value("${OAUTH_GITHUB_CLIENT_SECRET:}") private String githubClientSecret;
@Value("${OAUTH_GITEE_CLIENT_ID:}")    private String giteeClientId;
@Value("${OAUTH_GITEE_CLIENT_SECRET:}") private String giteeClientSecret;
```

### 3.3 注册两个平台 + 固定回调（核心）
> ⚠️ 回调地址必须**固定写死**，不要用默认 `{baseUrl}` 模板——否则请求主机是 `localhost` 还是 `127.0.0.1`、带不带 `:80` 都会拼出不同字符串，导致第三方平台“回调不匹配”。

```java
@Value("${app.oauth-redirect-base:http://localhost:80}")
private String oauthRedirectBase;   // 生产改 https://正式域名

// GitHub：Spring 内置 CommonOAuth2Provider，端点自动补齐
if (hasText(githubClientId) && hasText(githubClientSecret)) {
    ClientRegistration github = CommonOAuth2Provider.GITHUB.getBuilder("github")
            .clientId(githubClientId)
            .clientSecret(githubClientSecret)
            .redirectUri(oauthRedirectBase + "/login/oauth2/code/{registrationId}")
            .scope("read:user", "user:email")
            .build();
    registrations.add(github);
}

// Gitee：非内置平台，手动补齐三个端点
if (hasText(giteeClientId) && hasText(giteeClientSecret)) {
    ClientRegistration gitee = ClientRegistration.withRegistrationId("gitee")
            .clientId(giteeClientId)
            .clientSecret(giteeClientSecret)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri(oauthRedirectBase + "/login/oauth2/code/{registrationId}")
            .scope("user_info")
            .authorizationUri("https://gitee.com/oauth/authorize")
            .tokenUri("https://gitee.com/oauth/token")
            .userInfoUri("https://gitee.com/api/v5/user")
            .userNameAttributeName("id")
            .clientName("Gitee")
            .build();
    registrations.add(gitee);
}
```

### 3.4 SecurityFilterChain —— 与既有双轨鉴权共存 + 优雅降级
项目已有：前台 AuthInterceptor(Session/Cookie) + 后台 Sa-Token。引入 spring-security 后**必须**放行业务请求，只让框架接管 OAuth 端点：

```java
@Bean
public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .formLogin(form -> form.disable())
        .httpBasic(basic -> basic.disable())
        .logout(logout -> logout.disable())
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // 关键：业务鉴权仍走既有机制

    ConfigurableClientRegistrationRepository repository = repositoryProvider.getIfAvailable();
    if (repository != null && !repository.isEmpty()) {   // 无配置平台则完全不挂 oauth2Login
        http.oauth2Login(oauth -> oauth
                .clientRegistrationRepository(repository)
                .successHandler(oauth2LoginSuccessHandler)
                .failureHandler(oauth2LoginFailureHandler));
    }
    return http.build();
}
```

### 3.5 可空仓库 Bean（解决两难）
Spring Security 6.2 两个硬约束叠加：
- `InMemoryClientRegistrationRepository` 构造要求**非空**（`registrations cannot be empty`）
- `spring-security-config` 的 `OAuth2ClientConfiguration` 又**强制要求仓库 Bean 存在**

解法：自实现“允许为空 + 仍实现 Iterable”的仓库，使新机器未配环境变量时应用照常启动（第三方登录降级为不可用）：
```java
public class ConfigurableClientRegistrationRepository
        implements ClientRegistrationRepository, Iterable<ClientRegistration> {
    private final List<ClientRegistration> registrations;
    public boolean isEmpty() { return registrations.isEmpty(); }
    // findByRegistrationId(...) / iterator() 委托内部 list
}
```

### 3.6 成功/失败 Handler
成功：拿第三方 openId → `OauthLoginService`（命中 user_oauth 直接登录 / 未命中自动注册）→ 建立与账号密码登录一致的登录态（Session + Cookie + 置在线 + 刷最后登录时间）→ **302 到前端中转页**：

```java
User user = oauthLoginService.loginOrRegister(provider, openId, login, nickname, avatar);
HttpSession session = request.getSession(true);
session.setAttribute(AuthInterceptor.SESSION_USER, user);
AuthInterceptor.setCookie(response, AuthInterceptor.COOKIE_USER_ID,
        String.valueOf(user.getId()), AuthInterceptor.COOKIE_MAX_AGE_DAYS_4);
userService.updateLoginStatus(user.getId(), 0);   // 在线
userService.updateLastLogin(user.getId());        // 写 user_visit_stat.last_login_at（复用既有口径）
response.sendRedirect(frontendBaseUrl + frontendCallbackPath + "?id=" + user.getId());
```
失败：不向 URL 泄漏原始 SQL/堆栈，仅回登录页带友好提示：
```java
String friendly = (e instanceof BusinessException) ? e.getMessage() : "服务异常，请稍后重试或使用其他方式登录";
response.sendRedirect(frontendBaseUrl + frontendLoginPath + "?oauth_error=" + urlEncode(friendly));
```

### 3.7 账号绑定/自动注册（user_oauth + user）
- 按 `(provider, open_id)` 查 `user_oauth`；命中 → 校验 user.status=1 且未逻辑删除 → 登录
- 未命中 → **自动注册**：用户名 `{provider}_{openId}`、邮箱 `{provider}_{openId}@oauth.local`（占位），写 user + 写绑定行（`UNIQUE(provider,open_id)` 防并发重复）
- 个人中心提供：首次**设置密码**（无密码账号，`/api/login/set-password`）、修改邮箱/昵称/性别/头像（`/api/login/update-profile`）、忘记密码（forgot-password.html）

### 3.8 前端
- 登录页两个入口：`http://localhost:80/oauth2/authorization/github` 与 `.../gitee`
- 探测接口 `GET /api/oauth/providers`：后端未配置的平台按钮置灰
- **中转页 `oauth-callback.html`**：fetch `/api/login/me` 确认会话 → 写 localStorage 登录态 → `location.replace('index.html')` 直达首页

### 3.9 关键配置（application.yml）
```yaml
app:
  frontend-base-url: http://localhost:5500/AlgoVize/qianduan  # 前端内容根（含路径前缀）
  frontend-login-path: /pages/login.html
  frontend-callback-path: /oauth-callback.html                # OAuth 成功中转页
  oauth-redirect-base: http://localhost:80                    # 必须与平台后台回调完全一致
```

---

## 四、GitHub / Gitee 控制台怎么配

### GitHub（OAuth Apps）
- 入口：GitHub → Settings → Developer settings → OAuth Apps → New
- `Homepage URL`：`http://localhost:80`
- `Authorization callback URL`：**`http://localhost:80/login/oauth2/code/github`**（精确匹配、无尾斜杠）
- 拿到 Client ID / Client Secret

### Gitee（第三方应用）
- 入口：Gitee → 设置 → 安全设置 → 第三方应用 → 创建应用
- `应用主页`：`http://localhost:80`
- `回调地址`：**`http://localhost:80/login/oauth2/code/gitee`**
- 授权类型勾选 authorization_code，权限至少 `user_info`
- 拿到 Client ID / Client Secret

### 启动后端前
```powershell
$env:OAUTH_GITHUB_CLIENT_ID="..."
$env:OAUTH_GITHUB_CLIENT_SECRET="..."
$env:OAUTH_GITEE_CLIENT_ID="..."
$env:OAUTH_GITEE_CLIENT_SECRET="..."
mvn spring-boot:run
```
数据库需有 `user_oauth` 表（通用模板，见测试文档附录），后端连接库为 `algoviz`。

---

## 五、遇到并解决的问题（踩坑总结）

| # | 现象 | 根因 | 解决 |
|---|---|---|---|
| 1 | 编译报“找不到 CommonOAuth2Provider” | 它在 `spring-security-config` 的 `org.springframework.security.config.oauth2.client` 包，而非 oauth2-client 包 | 修正 import |
| 2 | `ClientRegistrationRepository` 无法 for-each / 无 findAll() | Spring Security 6.2 接口只有 `findByRegistrationId`，`InMemory` 实现才 `Iterable` | 遍历用具体实现 / 自研仓库 |
| 3 | 未配置环境变量时启动报 `registrations cannot be empty` | InMemory 构造不允许空 | 自研可空仓库 + 条件挂 oauth2Login（优雅降级） |
| 4 | 空仓库返回 null 后启动报 `NoSuchBeanDefinitionException: ClientRegistrationRepository` | `OAuth2ClientConfiguration` 强制要求仓库 Bean 存在 | 仓库 Bean **始终存在**（可空实现） |
| 5 | 设置了环境变量却仍提示未配置 | User 级环境变量只对新进程生效，旧终端快照拿不到 | 新开终端 / 重启 IDE；`[Environment]::GetEnvironmentVariable(...,'User')` 核对 |
| 6 | GitHub/Gitee 报“redirect_uri 与该应用无关联/无效回调” | 回调用默认 `{baseUrl}` 按请求主机拼：`localhost` 与 `127.0.0.1`、带不带 `:80` 会不一致 | **固定** `app.oauth-redirect-base=http://localhost:80`；平台后台填同值 |
| 7 | 授权成功后跳到 404 页面 | 5500 是“仓库根目录”静态服务，前端真实路径带 `/AlgoVize/qianduan` 前缀 | `frontend-base-url` 含路径前缀并做成可配置 |
| 8 | OAuth 成功回跳 profile 后又被弹回 login.html | 前端登录态靠 localStorage，profile 的 `/api/login/me` 校验不过就跳登录；且纯 HTTP 下 Secure Cookie 可能不被存 | 新增前端**中转页** `oauth-callback.html`：验证会话 + 写 localStorage → 落 `index.html` |
| 9 | GitHub 换 token 报 `PKIX path building failed: unable to find valid certification path` | 运行 JDK 的 cacerts 缺 GitHub 证书链（Gitee 却正常） | 启动加 `$env:JAVA_TOOL_OPTIONS="-Djavax.net.ssl.trustStoreType=WINDOWS-ROOT"`，或向 cacerts 导入证书 |
| 10 | 业务接口被 spring-security 干扰 | 引入 starter 后默认安全链接管全部请求 | SecurityConfig 内 `anyRequest().permitAll()`，鉴权仍交还 AuthInterceptor / Sa-Token |
| 11 | 页面提示把整段 SQL/堆栈显示给用户 | 成功 Handler catch 直接把 e.getMessage() 拼进 URL | 区分 BusinessException 与系统异常，只回友好文案并记日志 |
| 12 | Java 文件长注释导致编译错乱 | 注释里写了 `OAUTH_GITHUB_*/OAUTH_GITEE_*`，`*/` 提前终止注释 | 注释中避免 `*/`（写作 `OAUTH_GITHUB_* / OAUTH_GITEE_*`） |
| 13 | 首次登录自动注册邮箱/用户名冲突 | 数字 ID 前缀隔离 + 唯一性占位 | 用户名 `{provider}_{id}`、邮箱 `@oauth.local` 占位，并保留冲突重试后缀 |

---

## 六、安全边界与最佳实践

1. **OAuth 回调属于事后身份入口，不是万能安全**：`state` 防 CSRF 由框架保证；本系统成功回调后仍要建立**站内会话**并复用 AuthInterceptor 的实时账号状态校验（封禁/注销立即失效）。
2. **回调地址必须固定并精确登记**，且生产环境使用 HTTPS 域名。
3. **密钥走环境变量**，禁止进代码库/日志；生产建议用密钥管理服务。
4. **双轨鉴权隔离**：Spring Security 只做 OAuth 协议层；站内业务鉴权仍用既有 Session + Sa-Token，职责单一、互不干扰。
5. 失败提示只给友好信息，原始异常只进服务端日志。
6. 无密码第三方账号建议提示**设置本地密码**，避免平台不可用时无法登录。

---

## 七、未来扩展方向

- [ ] **已有账号绑定**：登录态下“绑定 GitHub/Gitee”（带绑定态 state），与“自动注册”两条路径并存
- [ ] **头像真实上传**：接入文件上传接口（当前仅支持图片 URL / emoji，dataURL 过长不入库）
- [ ] **回跳原页面**：授权前记录来源，成功后跳回发起页而非固定首页
- [ ] **PKCE / OAuth 2.1**：提升安全性（GitHub 已支持 PKCE 模式）
- [ ] **access_token 刷新与续期**：需要长期调用第三方 API 时存 refresh_token 并定时刷新
- [ ] **第三方登录解绑/多账号合并**：一个本地账号可绑多平台，user_oauth 支持多行（user_id 不唯一）
- [ ] **后台管理端第三方登录**：Sa-Token 管理后台也支持 OAuth
- [ ] **更多平台**：GitLab / 企业微信 / 钉钉 / 微信开放平台（仅需新增 Registration + userinfo 端点）
- [ ] **统一身份/SSO 演进**：token 换成 OIDC ID Token，接入 JWT 会话与网关
- [ ] **限流与风控**：第三方登录尝试频率限制、设备指纹、异地登录提醒
- [ ] **测试文档沉淀为自动化**：把冒烟/回归脚本与 CI 打通（当前手动脚本见 doc/md/scripts）
