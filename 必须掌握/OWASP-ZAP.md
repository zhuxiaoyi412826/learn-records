# OWASP ZAP（DAST 动态应用安全测试）总结

## 一、是什么

**DAST：动态应用安全测试（黑盒扫描）** OWASP ZAP（Zed Attack Proxy），由**OWASP（开放 Web 应用安全项目，现由 Checkmarx 维护）**开发的开源 DAST 工具GitHub。用来模拟黑客对**运行中的 Web 应用**发送网络请求，探测 Web 层漏洞（OWASP Top10：SQL 注入、XSS、路径遍历、越权访问、不安全配置等）。

> 和另外两类安全工具对比

1. SAST (SonarQube)：扫描源码，**不需要启动程序**，找代码写法问题。
2. SCA (Trivy)：扫描 pom 等依赖文件，**不需要启动程序**，检测第三方库 CVE 漏洞。
3. DAST(ZAP)：**必须应用启动并且可访问**，黑盒访问接口，检测运行时真实可利用漏洞。

> ⚠️法律提醒：只能扫描自己拥有权限的应用，禁止扫描公网第三方网站，属于违法行为。

### 官方下载 & 仓库地址

1. ZAP 官方网站（下载页，选 Core Cross‑Platform Package 免安装 zip）： https://www.zaproxy.org/download/ZAP
2. ZAP 官方 GitHub 源代码仓库： https://github.com/zaproxy/zaproxyGitHub
3. 第三方便携包（内置 JRE，不用自己装 Java）Auto‑ZAP GitHub： https://github.com/anubissbe/auto‑zap/releasesGitHub

> 注意：`zap‑baseline.py`脚本**仅 Docker 镜像内置**，Windows 本地 Core‑zip 包没有该脚本。

## 二、为什么要用 ZAP

1. SAST/SCA 检测不到运行时业务漏洞：有些漏洞只有程序跑起来、传入恶意参数之后才会暴露。
2. 模拟外部黑客视角，只知道 URL 就可以探测接口风险。
3. 检测类型：XSS、SQL 注入、路径遍历、IDOR 越权、不安全 HTTP 头、未授权访问等 Web 漏洞。
4. 输出 HTML/JSON 报告，可以用于 DevSecOps 流程。

> 缺点：会产生**误报**，报告必须人工复核；需要服务启动；登录后的接口需要配置 Cookie/Token 才能扫描。

### 两种使用形态

1. GUI 图形界面：适合本地学习测试，功能完整。
2. 命令行模式：适合 CI 流水线。

> Windows 官方 Core 免安装 zip 包的命令行有很多系统限制，不要照搬 Docker 教程。

## 三、怎么用（Windows Core‑zip 免安装包实操）

> 环境：ZAP 2.17.0 Core zip（免安装，需要 JDK17+）；被扫描 SpringBoot 运行在`http://127.0.0.1:80`

### 前置条件

1. SpringBoot 项目启动，浏览器访问`http://127.0.0.1`访问正常。
2. 关闭所有 ZAP GUI 窗口，避免 home 目录占用冲突。
3. JDK 版本 ≥17。
4. zip 解压路径**全英文，不能有中文空格**。

### 方式 1：GUI 图形界面（✅Windows 本地学习首选，坑最少）

1. 在官网下载页下载`Core Cross‑Platform Package`的 zip 包，解压；双击`zap.bat`启动。
2. 修改代理端口：`Tools → Options → Local Proxies`，把默认 8080 改为`8889`，规避端口占用。
3. `Quick Start` → `Target`填入 `http://127.0.0.1:80` → 点击`Attack`开始扫描。
4. 底部`Alerts`查看漏洞，颜色区分风险等级：🔴High、🟠Medium、🟡Low、🔵Info。
5. 导出报告：`Report → Generate HTML Report`，保存到桌面。

### 方式 2：Windows 命令行（坑多，仅做学习，不推荐日常使用）

> Windows Core‑zip 限制：`‑quickout`只能输出到程序**当前目录**，不支持直接写桌面等外部绝对路径；扫描完成后再复制文件到桌面。

```
# 1.扫描，报告生成在ZAP本地目录
java -jar zap-2.17.0.jar -cmd -dir ./zap-tmp -port 8889 -quickurl http://127.0.0.1:80 -quickout zap1-report.html `
&& Copy-Item zap1-report.html $env:USERPROFILE\Desktop\zap1-report.html
```

- `-dir ./zap-tmp`：独立工作目录，不和 GUI 冲突
- `-port 8889`：修改 ZAP 自身代理端口，解决 8080 端口占用报错
- `-quickurl`：填写你的业务服务地址
- 执行完成后，文件复制到桌面打开查看。

### 方式 3：CI 流水线真正用法（不在 Windows 本地执行）

使用 ZAP Docker 镜像，可以使用`zap‑baseline.py`基线脚本。

> 流水线位置：代码部署到测试环境、服务启动完成之后，执行 DAST 扫描；**不要放在 PR 阶段**（PR 阶段应用没有启动）。

## 四、你踩过的全部坑汇总（笔记）

1. ❌下载 exe 安装包：要下载`Core Cross‑Platform Package`的 zip 免安装包。
2. ❌照搬 Docker 的`zap‑baseline.py`命令：Windows Core 包没有该脚本。
3. ❌同时打开 GUI 又跑命令行：报 home 目录已占用。
4. ❌不指定`‑port`：ZAP 默认代理 8080 端口容易被占用，报`BindException`。
5. ❌`‑quickout`写绝对路径到桌面：Windows Core zip 不支持，只能输出当前目录，扫描完成再复制。
6. ❌解压路径带中文、空格：会启动失败。
7. ❌DAST 在服务没启动的时候执行：必然扫描失败。

## 五、DevSecOps 完整链路回顾

1. PR 阶段：SonarQube (SAST 源码扫描)+Trivy (SCA 依赖 & 密钥扫描)，**不需要启动应用**。
2. 合并代码，CI 打包，部署到测试环境，服务启动成功。
3. 执行 DAST (ZAP) 动态扫描 Web 接口。
4. 高危漏洞阻止发布到预发 / 生产环境。

> 本地 Windows 学习优先 GUI；CI 流水线使用 Docker 版本 ZAP。 DAST 报告需要人工甄别误报，不能完全自动信任扫描结果。

# OWASP ZAP 真实漏洞 / 误报快速甄别清单

> 重点看两个字段：**Risk（风险等级） + Confidence（置信度）**

- 置信度：High 高、Medium 中、Low 低；**置信度越低越容易是误报**
- 每一条告警点开看：`Attack`（ZAP 发的攻击载荷）、`Evidence`（响应里的证据片段），**证据不存在 = 大概率误报**

> 验证通用步骤：复制 ZAP 的攻击请求，在 Postman / 浏览器手动重放，看是否真的能复现危害；GUI 中确认是误报可以右键标记 `False Positive`（误报）。

## 优先级矩阵（排查顺序）

表格

| 风险 \ 置信度       | High 置信度            | Medium 置信度      | Low 置信度                 |
| ------------------- | ---------------------- | ------------------ | -------------------------- |
| 🔴High 高危          | 优先核查，大多真实     | 重点核查，可能真实 | 极高概率误报，必须手动复现 |
| 🟠Medium 中危        | 重点核查               | 需要验证           | 大概率误报                 |
| 🟡Low 低危           | 配置类问题，一般可修复 | 大多配置提示       | 基本可以忽略               |
| ⚪Informational 提示 | 仅参考，不阻断发布     | 忽略               | 忽略                       |

> 经验：**High 风险 + Low 置信度，不要直接认定漏洞，10 条里面 7‑8 条是误报**。

------

## 1、SQL 注入（High 高危，告警 ID：40018）

### ✅真实漏洞特征

1. 传入单引号 `'` 等 payload，后端返回数据库原生报错（ORA‑、MySQL、PostgreSQL 错误堆栈）。
2. 可以构造布尔条件，页面返回内容发生明显变化。
3. 项目使用**字符串拼接 SQL**，没有使用 MyBatis 参数化`#{}`、没有 JPA/ORM 预编译。

### ❌常见误报场景

1. Spring 全局异常处理器统一返回 500，不是数据库报错，只是收到特殊参数直接报错。
2. 接口响应时间偶然波动，ZAP 的时间盲注入规则误判延迟。
3. WAF 防火墙拦截攻击包返回 403，被扫描器误认为注入成功。

> 验证：把 ZAP 的 payload 复制到接口，看是否出现数据库原始报错；MyBatis 使用`#{}`预编译几乎不会出现 SQL 注入。

## 2、XSS 跨站脚本（Reflected 反射 XSS，High/Medium，ID:40012）

### ✅真实漏洞

用户传入的`<script>alert(1)</script>`，**原样输出到 HTML 页面，没有转义编码，浏览器会执行脚本**。

### ❌误报场景

1. 返回 JSON 接口：JSON 会对`< >`做转义，只是响应文本包含 payload 字符串，浏览器不会执行 JS（前后端分离项目高频误报）。
2. Vue/React 前端框架自动做 HTML 转义，后端直接返回标签，前端渲染时自动转义。
3. 内容放在`textContent`，不在 HTML innerHTML 渲染位置。

> 验证：浏览器打开该 URL，看弹窗会不会弹出来；只看响应报文有`<script>`不等于就是 XSS。

## 3、路径遍历 / 目录穿越 Path Traversal（High）

### ✅真实漏洞

传入`../../etc/passwd` / `../../windows/system.ini`，服务器返回服务器本地文件内容。

### ❌误报高频

1. URL 参数本身携带域名（如 saml 回调 entityId 参数写完整域名，包含多个`.`点号），ZAP 正则误命中GitHub。
2. SpringMVC 路径过滤、Servlet 安全机制已经拦截向上跳转，仅返回 404，没有读取文件。

## 4、CORS 跨域配置错误（High/Medium）

### ✅真实漏洞

`Access‑Control‑Allow‑Origin: *` 同时开启 `Access‑Control‑Allow‑Credentials: true`（浏览器标准禁止，属于高危）；或者 Origin 直接反射传入的请求源，没有白名单校验。

### ❌误报

1. 静态资源接口返回`*`，该接口**不携带 cookie、不涉及敏感数据**，业务无风险。
2. 内网接口仅内网访问，CORS 通配不对外暴露。

## 5、XXE XML 外部实体注入（High）

### ✅真实漏洞

传入恶意 XML payload，服务器读取服务器本地文件、发起内网请求。

### ❌误报

1. SpringBoot 默认已经关闭 XML 外部实体解析；只是接收 XML 报文，解析器做了安全防护。

## 6、安全头缺失类（Low / Medium，如 HSTS、CSP、X‑Content‑Type‑Options）

> 这类**几乎不会误报，是配置缺陷，但不是直接可利用漏洞**。

- ✅真实：响应头确实没有对应 Header；属于安全最佳实践，公网项目建议补全；内网演示可以暂缓。
- ❌几乎没有误报；但如果是**内部管理后台不对外 HTTPS，可以酌情不处理 HSTS**。

## 7、敏感信息泄露（堆栈信息、版本号泄露 Medium/Low）

### ✅真实漏洞

500 页面直接打印 Java 完整堆栈、数据库账号、服务器路径；响应头暴露 Tomcat、Spring 版本号。

> SpringBoot 生产环境务必关闭`‑Dspring.profiles.active=dev`，关闭 whitelabel 错误页。

### ❌误报

1. 返回的是自定义业务错误提示，**不包含代码堆栈、路径、数据库信息**。

## 8、开放重定向 Open Redirect（Medium/Low）

### ✅真实漏洞

传入可控跳转参数，可以跳转到任意第三方恶意域名。

### ❌误报

跳转目标做了域名白名单，只允许跳转到本系统域名。

## 9、CSRF 跨站请求伪造（Medium）

### ✅真实漏洞

修改数据接口，没有 CSRF‑Token、没有校验 SameSite Cookie；浏览器可以跨站发起请求篡改业务。

### ❌误报

1. 接口是前后端分离，全部使用 JWT/Tocken 放在 Header，不依赖 Cookie 登录。
2. 接口是 GET 只读查询接口，不会修改数据，不存在 CSRF 风险。

------

## 快速实操判断流程（做报告时照着走）

1. 点开告警，看`Confidence`置信度；**Low 置信度优先怀疑是误报**。
2. 复制 ZAP 的攻击 payload，用 Postman / 浏览器手动重放请求。
3. 核对`Evidence`证据：响应里面是否出现漏洞对应的实际危害。
4. 看业务上下文：
   - 公网对外接口：严格，高危尽量修复；
   - 仅内网、后台管理、普通用户接触不到：风险降级。
5. 确认是误报，ZAP GUI 右键告警标记`False Positive`，导出报告会标记该条目为误报。

## SpringBoot 项目高频误报汇总（你大概率会遇到）

1. JSON 接口报 XSS（后端返回脚本标签字符串，JSON 转义，前端不会执行 JS）
2. 500 全局异常页面，报 SQL 注入（只是统一报错，不是数据库报错）
3. CORS 配置`*`，静态资源接口，业务不带 Cookie
4. 路径参数带域名，误报路径遍历
5. baseline 基线扫描很多告警置信度为 Low，基线扫描是轻量扫描，证据不足。

### DevSecOps 流水线落地建议

1. CI 不要直接阻断构建：只阻断**High+High 置信度**漏洞；Medium/Low 仅记录报告，不阻断流水线。
2. 误报需要人工审核，**DAST 报告不能完全自动化信任**。
3. 基线 baseline 适合 CI；完整 Active 主动扫描适合定时跑，不要每次提交都跑完整扫描。

> 补充：SAST (SonarQube) 看代码写法；SCA (Trivy) 看第三方库 CVE；**DAST (ZAP) 看运行时真实可被利用的漏洞，但是误报多，必须人工复核**。

如果你需要，我可以整理一份简单的 ZAP 修复建议速查表，对应上面漏洞给出 SpringBoot 的代码 / 配置修复示例。