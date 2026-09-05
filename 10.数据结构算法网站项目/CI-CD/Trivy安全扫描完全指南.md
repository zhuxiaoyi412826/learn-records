# Trivy 安全扫描工具完全指南
## ——从"是什么"到"怎么用好它"的深度解析

> **一句话定位**：Trivy 是一款开源、轻量、开箱即用的一体化安全扫描工具，由 Aqua Security 公司开发，能在本地或 CI 流水线中自动扫描项目的**第三方依赖漏洞（SCA）**、**密钥与敏感信息泄露（Secret）**、**容器镜像漏洞**以及**基础设施错误配置（IaC Misconfiguration）**。
>
> - 项目地址：<https://github.com/aquasecurity/trivy>
> - 官方文档：<https://trivy.dev>
> - 漏洞数据库可视化查询：<https://avd.aquasec.com>
> - 授权协议：Apache-2.0（完全免费开源，可商用）

本文基于一份 Trivy 使用笔记扩写而成，围绕**是什么、为什么、怎么使用**三大问题展开，并补充项目落地实践与常见踩坑指南，适合 Java / Spring Boot 后端开发者、DevOps 工程师以及所有关注软件供应链安全的读者阅读。

---

## 一、Trivy 是什么

### 1.1 定义与出身

**Trivy**（取自 "trivia"，寓意"扫遍那些容易被忽视的细枝末节"）是 Aqua Security 公司于 2019 年开源的安全扫描器。Aqua Security 是一家专注于云原生安全的以色列公司，其商业产品线覆盖容器安全、云安全态势管理等领域，而 Trivy 则是它贡献给社区的一面旗帜性工具——用 Go 语言编写、单个二进制文件分发、零外部依赖、不需要安装任何数据库服务。

与传统商业扫描器"重、贵、难部署"的形象相反，Trivy 的设计哲学是**开箱即用**：下载解压即得一个可执行文件，一条命令即可完成扫描，第一次运行时它会自动从远端拉取漏洞数据库到本地缓存目录，之后就可以离线复用。正因如此，它在 GitHub 上收获了数万 Star，并被 Google Cloud、GitLab、GitHub 等平台内置或集成，成为事实上的开源漏洞扫描标准工具之一。

### 1.2 核心能力全景

Trivy 定位为 "All-in-One" 扫描器，一个工具覆盖五大安全场景：

| 能力维度 | 扫描对象 | 解决的问题 | 典型触发方式 |
|---------|---------|-----------|-------------|
| **SCA 依赖漏洞扫描** | pom.xml、package.json、requirements.txt、go.mod、Gemfile、Cargo.lock 等 20+ 种语言生态 | 引入的第三方库存在已知 CVE 漏洞 | `trivy fs .` |
| **Secret 密钥泄露检测** | 源码、编译产物（target/、dist/）、镜像层中的硬编码敏感信息 | 私钥、密码、API Token、云凭证意外泄露 | `trivy fs . --scanners secret` |
| **容器镜像扫描** | Docker/OCI 镜像内的 OS 包（apt/yum/apk）与应用依赖 | 基础镜像和镜像层中的漏洞 | `trivy image nginx:latest` |
| **IaC 错误配置扫描** | Terraform、CloudFormation、Kubernetes YAML、Dockerfile | 基础设施定义中的安全隐患 | `trivy config .` |
| **License 合规扫描** | 依赖的开源许可证 | 引入了不合规或有法律风险的许可证 | `trivy fs . --scanners license` |

对绝大多数后端开发者而言，前两项（依赖漏洞 + 密钥泄露）是日常最常用、价值最高的能力，本文也将围绕它们重点展开。

### 1.3 工作原理：Trivy 是怎么"查出"漏洞的

理解 Trivy 的工作原理，能帮助你在遇到误报、漏报或性能问题时做出正确判断。它的核心流程可以概括为"**解析依赖 → 查询数据库 → 匹配输出**"三步：

1. **解析依赖清单**：Trivy 首先遍历目标目录（或镜像层、Git 仓库），识别出所有它认识的"依赖描述文件"——对 Maven 项目就是 pom.xml，对 npm 项目就是 package-lock.json。它会解析出项目实际引用的每个组件及其版本号。
2. **加载漏洞数据库**：Trivy 依赖两个独立的数据库（以 OCI 镜像形式分发在 ghcr.io 上）：
   - **trivy-db**：综合漏洞库，聚合了 NVD（美国国家漏洞数据库）、GitHub Advisory Database、各语言官方安全公告等上游数据源，按小时级频率更新；
   - **trivy-java-db**：Java 专用库。之所以 Java 需要单独一个库，是因为 Maven 的**传递依赖机制**非常复杂——pom.xml 里声明的依赖会层层展开成庞大的依赖树，而 trivy-java-db 索引了 Maven Central 全量构件的元数据，使 Trivy 能精确还原依赖树中每个 jar 包的真实版本。
3. **匹配与输出**：将解析出的"组件名 + 版本号"与数据库中的漏洞记录做比对，命中即输出漏洞详情（CVE 编号、CVSS 评分与等级、漏洞标题、修复版本等）；密钥扫描则基于内置的检测规则集（如 AWS AccessKey 模式、PEM 私钥头特征、JWT 结构等）做正则与熵值匹配。

首次运行时 Trivy 需要下载这两个数据库（合计几十到上百 MB，国内网络环境下经常超时，这正是后文"常见坑"第一坑的来源）；数据库会被缓存在本地（Windows 下位于 `C:\Users\<用户名>\AppData\Local\aquasecurity\trivy` 或 `~/.cache/trivy`），之后的扫描直接复用缓存，速度非常快。

### 1.4 必须先弄懂的几个术语

阅读 Trivy 报告前，请先建立这套词汇表：

| 术语 | 全称 | 含义 |
|------|------|------|
| **CVE** | Common Vulnerabilities and Exposures | 公开安全漏洞的唯一编号，如 CVE-2021-44228 |
| **CVSS** | Common Vulnerability Scoring System | 漏洞严重性评分体系（0–10 分） |
| **CRITICAL / HIGH / MEDIUM / LOW / UNKNOWN** | — | CVSS 分数对应的等级档位（严重 / 高危 / 中危 / 低危 / 未知），`--severity` 参数即按此过滤 |
| **SCA** | Software Composition Analysis | 软件成分分析，即"扫描你引入了什么第三方组件、它们有什么漏洞" |
| **SBOM** | Software Bill of Materials | 软件物料清单，枚举软件中所有组件的清单文件（CycloneDX / SPDX 格式） |
| **Fixed Version** | — | 修复了该漏洞的最低版本号，是排障时最重要的字段之一 |
| **Unfixed** | — | 官方尚未发布修复版本的漏洞，`--ignore-unfixed` 用于过滤这类记录 |

### 1.5 与同类工具的对比：Trivy 在生态中的坐标

很多团队已经在用 SonarQube，会产生"有了 Sonar 还需要 Trivy 吗"的疑问。答案是：**两者根本不是竞争关系，而是互补关系**——它们的扫描对象几乎不重叠。

| 对比维度 | Trivy | SonarQube | OWASP Dependency-Check | Snyk |
|---------|-------|-----------|------------------------|------|
| **扫描对象** | 第三方依赖、密钥、镜像、IaC | 自己写的代码 | 第三方依赖 | 依赖、镜像、代码 |
| **回答的问题** | "我引入的库有洞吗？我泄密了吗？" | "我的代码写得规范吗、有 bug 吗？" | "我的依赖有洞吗？" | 同 Trivy 类似 |
| **误报率** | 较低 | 低 | 偏高（NVD 元数据匹配） | 低 |
| **Java 依赖树还原** | 精确（trivy-java-db） | 不涉及 | 一般 | 精确 |
| **密钥泄露检测** | 内置 | 有限 | 无 | 有 |
| **授权** | Apache-2.0 免费 | 社区版免费 | Apache-2.0 | 核心商业收费 |
| **部署形态** | 单二进制，零依赖 | 需部署服务端 + 数据库 | 插件/CLI | 云服务为主 |

一个直观的记忆方式：**SonarQube 管"你写的代码"，Trivy 管"你没写的代码（第三方依赖）和你不小心泄露的东西（密钥）"**。一个典型 Spring Boot 项目的安全水位线，需要两者共同守护。

### 1.6 适用场景

- **后端开发者**：本地扫描 Maven/Gradle/npm 项目，在提交代码前发现依赖漏洞与误提交的密钥；
- **DevOps / 平台团队**：在 CI 流水线中设置安全门禁，阻止带高危漏洞的构建产物发布；
- **容器化团队**：扫描 Docker 镜像，评估基础镜像与镜像层风险；
- **合规审计团队**：生成 SBOM 与漏洞报告，满足等保、ISO 27001、PCI DSS 等对漏洞管理流程的审计要求。

---

## 二、为什么要用 Trivy

### 2.1 软件供应链安全：你的代码只是冰山一角

现代应用的构成早已不是"从零手写"——一个再简单的 Spring Boot 服务，pom.xml 展开后动辄数百个 jar 包：Spring 全家桶、Netty、Jackson、Logback、MyBatis、各类驱动……业界普遍的观察是，**现代应用中来自第三方组件的代码占比远超自研代码**。这意味着：

- 你的代码质量再高，也**不代表整体安全**；
- 任何一个第三方组件爆出漏洞，你的应用就"被动中招"；
- 漏洞影响面是乘法级的：一个流行组件的 CVE 会同时影响数百万个项目。

最著名的案例当属 2021 年 12 月的 **Log4Shell（CVE-2021-44228）**：一个几乎所有 Java 项目都在用的日志组件 log4j-core 爆出 CVSS 10.0 满分漏洞，攻击者仅通过一段构造的日志字符串就能远程执行任意代码。全球范围内连夜升级、应急处置，连 Minecraft 服务器都未能幸免。事后复盘，受灾最重的团队恰恰是那些**不知道自己用了 log4j、更不知道自己用的是哪个版本**的团队——如果你当时手里有一份 SBOM 或者跑过一次 Trivy，"排查受影响服务"只是几条命令的事。这就是供应链可见性的价值。

### 2.2 依赖漏洞（SCA）：SCA 解决"我引入了什么风险"

回到笔记中记录的真实场景：一个 Spring Boot 项目执行 `trivy fs .` 后，报告出 **76 个漏洞**，涉及 netty、jackson、logback 等常用组件。这不是个例，而是常态——因为依赖树层级深，即使你从不直接使用 netty，Spring 的某个 starter 也会把它传递进来。

SCA 扫描解决三个具体问题：

1. **可见性**：依赖树太深，人不可能逐个核对。Trivy 自动枚举全部组件并标记漏洞；
2. **优先级**：通过 CVSS 等级与 Fixed Version 字段，告诉你"先修哪个、怎么修"；
3. **持续性**：漏洞是动态产生的——今天干净的依赖，明天可能爆雷。CI 中持续扫描（而非一次性扫描）才能形成防线。

### 2.3 密钥泄露：从一枚 .pem 文件说起

笔记中记录的第一个典型场景更具警示意义：Trivy 检测到 `target/classes/cert/apiclient_key.pem`——一枚微信支付商户 API 私钥，被打进了 Maven 编译产物。

这个场景为什么危险，值得逐层拆解：

- **进入产物的路径**：开发者把密钥文件放在了 `src/main/resources/cert/` 目录下，Maven 编译时会把 resources 目录中的所有文件原样复制进 `target/classes`，最终打进 jar 包。也就是说，**只要 jar 包被分发，私钥就跟着分发**；
- **进入 Git 的路径**：更糟的情况是密钥文件被 commit 进仓库。Git 的历史是只增不减的，即使后续删除文件，历史提交中依然可以完整取出私钥。业界称之为"永久泄露"——唯一稳妥的补救是**吊销并轮换密钥**，而不是删文件；
- **攻击者的自动化**：不要指望"仓库是私有的"作为保护。公开研究多次证实，攻击者会实时监控 GitHub 的事件流，一旦有疑似 AWS Key、私钥、Token 的内容被推送，几分钟内就会被自动化程序捕获并尝试利用。GitHub 自身也因此提供了 Secret Scanning 与 Push Protection 功能；
- **业务后果**：微信支付商户私钥泄露，攻击者可能伪造签名发起退款、篡改支付通知；云平台 AccessKey 泄露，一夜之间可能产生天价账单或数据被拖库。

Trivy 的 Secret 扫描器正是针对这类问题：它在扫描文件系统时会用内置规则集匹配各类密钥特征（PEM 私钥头、AWS AccessKey 格式、高熵字符串等），在密钥**进入 Git 历史、进入 jar 包之前**就把风险暴露出来。

### 2.4 容器镜像：冰山之下的部分

应用容器化之后，攻击面进一步扩大：Dockerfile 里一行 `FROM openjdk:17` 拉下来的基础镜像，内含一整个 Debian/Ubuntu 的系统包集合（glibc、openssl、zlib……），每个系统包都可能有 CVE。业务代码零漏洞 + 基础镜像一堆洞，是容器安全审计中最常见的"木桶短板"。`trivy image` 能把镜像逐层解开，同时扫描 OS 包与应用依赖，让这块冰山之下的风险显形。

### 2.5 合规与监管驱动

如果你的业务涉及金融、支付、政务等强监管领域，漏洞管理不是"可选项"而是"审计项"：

- **等保 2.0**（网络安全等级保护）要求对系统漏洞进行发现与修复管理；
- **ISO 27001** 信息安全管理体系要求组织建立系统的漏洞管理流程；
- **PCI DSS**（支付卡行业数据安全标准）明确要求定期扫描并按等级修复漏洞，高危漏洞有明确的处理时限要求。

Trivy 输出的 JSON / SARIF / HTML 报告可以直接作为漏洞管理流程的留痕材料，SBOM 输出则能满足监管对"软件成分透明"的审查要求。

### 2.6 DevSecOps 左移：越早发现，修复成本越低

安全领域有一条被反复验证的成本曲线：**漏洞在需求阶段修复的成本记为 1，到上线后再修复可能放大数十倍**。原因很简单——上线前修复只是改几行配置或升一个版本号；上线后修复则涉及应急响应、停机窗口、数据核查、事后复盘，甚至直接资金损失。

"左移（Shift Left）"就是把安全检查从运维阶段挪到开发与构建阶段：在 CI 里跑 Trivy，高危漏洞直接让构建失败，**带病代码根本走不到生产环境**。Trivy 轻量到可以嵌入任何流水线（单二进制、无服务端依赖），正是为左移而生的工具。

### 2.7 小结：Trivy 在研发流程中的位置

把上文串成一句话：**开发本地用 Trivy 自查依赖与密钥 → 提交触发 CI 中的 Trivy 门禁 → 高危漏洞阻断合并/发布 → 镜像构建后再扫镜像 → 报告归档满足合规**。Trivy 不是"上线后的救命稻草"，而是嵌在研发流程每一站的安全闸门。

---

## 三、怎么使用 Trivy

### 3.1 安装部署

#### 方式一：Windows 免安装绿色版（推荐日常开发使用）

1. 打开 GitHub Releases 页面：<https://github.com/aquasecurity/trivy/releases>；
2. 找到最新版本，下载 `trivy_x.xx.x_windows-64bit.zip`；
3. 解压得到单个 `trivy.exe`，无需任何安装过程；
4. 二选一使用：
   - **便携用法**：把 `trivy.exe` 直接丢进项目目录，随项目走（适合 U 盘、无管理员权限的机器）；
   - **全局用法**：将 exe 所在目录加入系统 `PATH` 环境变量，此后在任意路径的终端中都能直接敲 `trivy`。

验证安装：

```powershell
trivy --version
```

#### 方式二：其他平台安装速查

| 平台 | 安装命令 |
|------|---------|
| macOS | `brew install trivy` |
| Debian/Ubuntu | 参考官方 apt 源安装（见 trivy.dev）或下载 deb 包 |
| RHEL/CentOS | 官方 yum 源安装 |
| 任意 Linux 一键脚本 | `curl -sfL https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/install.sh \| sh -s -- -b /usr/local/bin` |
| Docker 运行 | `docker run --rm -v trivy-cache:/root/.cache/ aquasec/trivy image nginx:latest` |

#### 首次运行的"隐形步骤"

第一次执行扫描命令时，Trivy 会自动从 ghcr.io 拉取漏洞数据库（trivy-db 约 60MB+，Java 项目还会拉 trivy-java-db，体积更大）。**这一步在国内网络下经常超时失败**，是新手第一道坎，解决方案见 3.4 节的国内镜像配置与第五章第 1 坑。

### 3.2 命令体系总览

Trivy 的 CLI 以"扫描对象"为子命令组织，先记这张表：

| 子命令 | 扫描对象 | 常用场景 |
|--------|---------|---------|
| `trivy fs .` | 文件系统（本地目录） | 扫描项目源码与依赖清单，日常最常用 |
| `trivy image <镜像名>` | 容器镜像 | 镜像构建后的安全检查 |
| `trivy repo <git地址>` | 远程 Git 仓库 | 不 clone 也能扫 |
| `trivy config .` | IaC 文件 | 检查 Dockerfile / K8s YAML / Terraform 错误配置 |
| `trivy rootfs <目录>` | 根文件系统 | 扫描已解包的镜像或系统目录 |
| `trivy k8s` | K8s 集群资源 | 扫描集群内工作负载 |
| `trivy server` / `client` | 服务化模式 | 数据库只下载一次、多客户端共享，节省 CI 时间 |

### 3.3 核心参数详解

| 参数 | 作用 | 使用建议 |
|------|------|---------|
| `fs` | 子命令：扫描文件系统 | Maven 项目本地扫描的入口 |
| `--severity CRITICAL,HIGH` | 只显示严重 + 高危漏洞 | 日常排查噪声过多时的第一道过滤 |
| `--ignore-unfixed` | 忽略官方还没有修复方案的漏洞 | **注意语义**：已有修复版本的漏洞依然会报 |
| `--scanners vuln` | 只跑依赖漏洞扫描，关闭密钥扫描 | 提速用；默认是 `vuln,secret,misconfig` |
| `--scanners secret` | 只跑密钥扫描 | 专项排查泄密时使用 |
| `--exit-code 1` | 发现问题（达到报告阈值）时返回非 0 退出码 | CI 门禁的核心开关，本地手动跑可不加 |
| `--db-repository <url>` | 指定漏洞库下载源 | 国内网络替换为南京大学镜像 |
| `--java-db-repository <url>` | 指定 Java 库下载源 | 同上，Java 项目必配 |
| `-f, --format` | 输出格式：table / json / sarif / html / cyclonedx / spdx … | 人看 table，机器看 json，GitHub 看 sarif |
| `-o, --output <文件>` | 结果写入文件 | 与 `-f` 搭配使用 |
| `--skip-dirs <目录>` | 跳过指定目录 | 排除 `node_modules`、`target` 等噪声目录 |

### 3.4 本地扫描 Maven/Spring Boot 项目实战

进入项目根目录（`pom.xml` 所在目录），由简入繁依次是：

**命令一：最简版（网络通畅时）**

```powershell
trivy fs . --severity CRITICAL,HIGH --ignore-unfixed
```

**命令二：国内镜像加速版（网络不畅时的主力命令）**

```powershell
trivy fs . `
--db-repository ghcr.nju.edu.cn/aquasecurity/trivy-db:2 `
--java-db-repository ghcr.nju.edu.cn/aquasecurity/trivy-java-db:1 `
--severity CRITICAL,HIGH `
--ignore-unfixed
```

说明：`ghcr.nju.edu.cn` 是南京大学维护的 ghcr.io 镜像站，把两个数据库源指过去后，首次下载的稳定性大幅改善。注意 PowerShell 中行尾的反引号 ` 是续行符（等价于 Linux 的 `\`），反引号后不能有空格。

**命令三：只扫依赖漏洞（提速版）**

```powershell
trivy fs . --severity CRITICAL,HIGH --ignore-unfixed --scanners vuln
```

关闭密钥扫描后，不需要遍历所有文件内容，速度明显提升。适合"我只想快速看一眼依赖漏洞"的场景。

**命令四：CI 门禁版（发现问题即失败）**

```powershell
trivy fs . --severity CRITICAL,HIGH --ignore-unfixed --exit-code 1
```

退出码非 0 会让流水线步骤标记为失败，从而阻断后续发布——这是把 Trivy 接入 CI 的关键一行。

### 3.5 读懂扫描报告

table 格式的输出大致长这样（节选示意）：

```
target/classes (secrets)
========================
Total: 1 (CRITICAL: 1)

┌───────── Result ─────────┐
│ Path: target/classes/cert/apiclient_key.pem
│ Rule: private-key
│ Severity: CRITICAL
└──────────────────────────┘

pom.xml
=======
Total: 76 (UNKNOWN: 0, LOW: 21, MEDIUM: 34, HIGH: 18, CRITICAL: 3)

┌──────────────────┬─────────────────────┬──────────┬───────────────┬──────────────────────────────────┐
│     Library      │    Vulnerability    │ Severity │ Installed Ver │          Fixed Version           │
├──────────────────┼─────────────────────┼──────────┼───────────────┼──────────────────────────────────┤
│ netty-codec-http │ CVE-2023-34462      │ HIGH     │ 4.1.86        │ 4.1.94                          │
└──────────────────┴─────────────────────┴──────────┴───────────────┴──────────────────────────────────┘
```

逐字段解读：

| 字段 | 含义 | 你该做什么 |
|------|------|-----------|
| **Target（分组标题）** | 扫描目标，如 `pom.xml`、`target/classes` | 先看分组，判断风险来自依赖还是产物 |
| **Library** | 存在漏洞的组件名 | 定位是直接依赖还是传递依赖 |
| **Vulnerability（CVE 编号）** | 漏洞唯一标识 | 拿去 NVD / GitHub Advisory 查详情与利用条件 |
| **Severity** | 严重等级 | 决定处理优先级 |
| **Installed Version** | 当前使用的版本 | — |
| **Fixed Version** | 修复该漏洞的最低版本 | **行动依据**：升级目标版本 |

### 3.6 从报告到修复：处理漏洞的正确姿势

扫描只是发现问题，闭环在修复。处理顺序建议：

1. **先修 CRITICAL 与 HIGH**：按等级从高到低处理，MEDIUM/LOW 可以放入技术债清单按迭代消化；
2. **看 Fixed Version 升级**：优先把组件升到"修复版本或更高的稳定版本"。例如 netty-codec-http 报 Fixed Version 为 4.1.94，就把依赖管理中的 netty 版本锁定到 ≥ 4.1.94；
3. **判断"可达性"再决定是否豁免**：有些漏洞只在特定使用方式下才可被利用（如某反序列化漏洞仅在启用了特定功能时危险）。确认不可达后，用 `.trivyignore` 豁免并写清原因；
4. **升级后必须回归测试**：升级依赖可能引入行为变化，跑一遍完整测试再合并；
5. **传递依赖的升级技巧**：如果漏洞组件是传递依赖（你没直接声明它），在 Maven 中用 `<dependencyManagement>` 或父 BOM 锁定版本，而不是在子 pom 里直接声明依赖。

### 3.7 输出格式与结果落盘

#### 方式 1：文本格式（人类可读，与控制台输出一致）

```powershell
trivy fs . `
--db-repository ghcr.nju.edu.cn/aquasecurity/trivy-db:2 `
--java-db-repository ghcr.nju.edu.cn/aquasecurity/trivy-java-db:1 `
--severity CRITICAL,HIGH `
--ignore-unfixed `
--scanners vuln `
> trivy-scan-result.txt
```

`>` 把全部输出重定向写入 `trivy-scan-result.txt`，**会覆盖旧文件**；想追加内容用 `>>`。执行完后用记事本打开即可查看全部漏洞报告。

#### 方式 2：JSON 格式（机器解析，CI 工具读取，推荐）

```powershell
trivy fs . `
--db-repository ghcr.nju.edu.cn/aquasecurity/trivy-db:2 `
--java-db-repository ghcr.nju.edu.cn/aquasecurity/trivy-java-db:1 `
--severity CRITICAL,HIGH `
--ignore-unfixed `
--scanners vuln `
-f json -o trivy-scan-result.json
```

`-f json` 指定输出格式为 JSON，`-o 文件名` 输出到指定文件。适合后续用脚本统计、对接自有平台。

#### 方式 3：SARIF 格式（GitHub Actions 专用）

```powershell
trivy fs . `
--db-repository ghcr.nju.edu.cn/aquasecurity/trivy-db:2 `
--java-db-repository ghcr.nju.edu.cn/aquasecurity/trivy-java-db:1 `
--severity CRITICAL,HIGH `
--ignore-unfixed `
-f sarif -o trivy-result.sarif
```

SARIF 是静态分析结果的标准交换格式，上传到 GitHub 后能在仓库的 Security 标签页中以可视化方式展示漏洞，并在 PR 中标注代码位置。

#### 其他值得知道的格式

| 格式 | 用途 |
|------|------|
| `-f html -o report.html` | 生成可直接给管理层看的 HTML 报告 |
| `-f cyclonedx -o sbom.json` | 生成 CycloneDX 格式 SBOM（软件物料清单） |
| `-f spdx -o sbom.spdx` | 生成 SPDX 格式 SBOM（部分合规审计指定格式） |
| `-f template --template @xxx.tpl` | 自定义模板渲染输出 |

#### 小技巧：控制台与文件同时输出（tee）

```powershell
trivy fs . `
--db-repository ghcr.nju.edu.cn/aquasecurity/trivy-db:2 `
--java-db-repository ghcr.nju.edu.cn/aquasecurity/trivy-java-db:1 `
--severity CRITICAL,HIGH `
--ignore-unfixed `
--scanners vuln `
| tee trivy-scan-result.txt
```

`tee` 让输出同时出现在屏幕与文件中。另一个小提示：如果加了 `--exit-code 1`，有高危漏洞时命令会返回非 0 错误码，**结果文件依然会正常生成**，只是 PowerShell 会红字报错——这是预期行为，不是扫描失败。

### 3.8 忽略机制的正确姿势

#### `.trivyignore` 文件

在项目根目录新建 `.trivyignore`，每行一条忽略规则，支持两类内容：

```text
# 按文件/路径忽略（常用于密钥误报处理，例如示例密钥、测试证书）
target/classes/cert/apiclient_key.pem

# 按 CVE 编号忽略（用于确认不可达/暂不修复的漏洞，务必附上原因与复审时间）
CVE-2023-XXXXX
```

#### `--skip-dirs` 参数

```powershell
trivy fs . --skip-dirs "node_modules,target/test-classes"
```

跳过无需扫描的目录，既提速又消除噪声。

#### 使用原则：忽略是例外，不是常规

**不要用 `.trivyignore` 去系统性忽略 CVE 漏洞——优先升级依赖版本。** 每一条忽略都应该对应一个"为什么可以忽略"的理由（不可达、误报、风险接受），并且最好写明复审时间。否则半年之后，这份文件会变成无人敢动、也无人看懂的技术债。

### 3.9 CI/CD 流水线集成

#### GitHub Actions 完整示例

```yaml
name: Security Scan

on:
  push:
    branches: [ main ]
  pull_request:

jobs:
  trivy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Trivy扫描依赖
        uses: aquasecurity/trivy-action@v0.36.0
        with:
          scan-type: fs
          scan-ref: .
          severity: CRITICAL,HIGH
          ignore-unfixed: true
          format: sarif
          output: trivy-result.sarif
          # 学习项目建议注释掉 exit-code，只提醒不阻断；生产环境开启：
          # exit-code: "1"

      - name: 上传SARIF到GitHub安全标签页
        uses: github/codeql-action/upload-sarif@v3
        if: always()
        with:
          sarif_file: trivy-result.sarif
```

要点说明：

- 官方 Action `aquasecurity/trivy-action` 把 Trivy 二进制下载、执行、参数映射全部封装好，`scan-type: fs` 对应 `trivy fs`；
- `exit-code: "1"` 是门禁开关，注释掉则只扫描记录、不阻断构建；
- 上传 SARIF 后，漏洞会出现在仓库 **Security → Code scanning** 页面，并在 PR 里以代码注解形式提示，体验与原生代码扫描一致。

#### GitLab CI 简例

```yaml
trivy_scan:
  stage: test
  image:
    name: aquasec/trivy:latest
    entrypoint: [""]
  script:
    - trivy fs . --severity CRITICAL,HIGH --ignore-unfixed --exit-code 1
  allow_failure: false   # 生产流水线保持 false，高危漏洞直接失败
```

#### Jenkins 简例

```groovy
stage('Trivy Security Scan') {
    steps {
        sh 'trivy fs . --severity CRITICAL,HIGH --ignore-unfixed --exit-code 1'
    }
}
```

#### 门禁策略分级建议

| 环境类型 | exit-code | 严重等级阈值 | 说明 |
|---------|-----------|-------------|------|
| 个人学习/演示项目 | 关闭 | CRITICAL,HIGH | 只提醒不阻断，保持开发流畅 |
| 团队开发/内部测试 | 开启 | CRITICAL | 只拦截致命漏洞 |
| 生产发布流水线 | 开启 | CRITICAL,HIGH | 高危即阻断，SARIF/报告归档 |

### 3.10 容器镜像扫描

镜像构建完成后一行命令体检：

```powershell
trivy image myapp:latest --severity CRITICAL,HIGH
```

进阶用法：

```powershell
# 顺手检查 Dockerfile 的错误配置（基础镜像用 latest、root 用户运行、明文传密钥等）
trivy config Dockerfile

# 只扫镜像里的 OS 包漏洞
trivy image myapp:latest --scanners vuln --severity CRITICAL,HIGH
```

如果基础镜像漏洞太多，优先考虑换更精简的基础镜像（如 distroless / alpine 系），往往比逐个升级系统包更有效。

### 3.11 进阶能力速览

- **生成 SBOM**：`trivy fs . -f cyclonedx -o sbom.json`，交付给甲方或留档，"软件里到底装了什么"从此有据可查；
- **K8s 集群扫描**：`trivy k8s --report summary`，扫描集群内工作负载的镜像与配置风险；
- **离线环境**：在有网机器上用 `trivy image --download-db-only` 与 `--download-java-db-only` 预下载数据库，拷贝到离线机器后用 `--skip-db-update` 跳过更新；
- **Server 模式**：CI 机器多时，用 `trivy server` 部署一个数据库服务端，各构建节点以 client 模式连接，避免每个节点都下载几十 MB 数据库。

---

## 四、项目实践建议（以 Spring Boot 项目为例）

### 4.1 三套环境、三种策略

| 环境 | 扫描方式 | exit-code | 关注点 |
|------|---------|-----------|--------|
| **本地开发** | 提交前手动跑 `trivy fs .` | 不加 | 快速自查依赖漏洞与误提交密钥 |
| **个人学习项目** | CI 中扫描 | 关闭 | 只做风险提醒，不阻断构建，保持学习流畅 |
| **生产项目** | CI 强制门禁 + 定期全量扫描 | 开启（CRITICAL,HIGH 即失败） | 漏洞分级处理、报告归档、合规留痕 |

### 4.2 Trivy + SonarQube 双保险体系

两个工具各管一段，拼起来才是完整的安全与质量防线：

| 职责 | SonarQube | Trivy |
|------|-----------|-------|
| 自研代码 bug / 规范 | ✅ | — |
| 代码异味、重复率、覆盖率 | ✅ | — |
| 第三方依赖 CVE 漏洞 | — | ✅ |
| 密钥 / 敏感信息泄露检测 | 有限 | ✅ |
| 容器镜像 / IaC 扫描 | — | ✅ |

建议的流水线顺序：单元测试 → SonarQube 质量门禁 → Trivy 安全门禁 → 构建镜像 → `trivy image` 镜像扫描 → 发布。

### 4.3 密钥管理的正确姿势

结合第二章的 .pem 案例，密钥管理的完整实践是：

1. **密钥永不进源码目录**：`src/main/resources` 下只允许放"丢了也不心疼"的资源文件，任何真实密钥都通过环境变量、启动参数（`--spring.config.additional-location`）、配置中心（Nacos/Apollo）或密钥管理服务（KMS/Vault）注入；
2. **用 `.gitignore` 兜底**：把 `*.pem`、`*.key`、`*.p12`、`application-local.yml` 等模式加入忽略清单，并定期用 `git log --all --full-history -- "**/*.pem"` 检查是否已有历史泄露；
3. **已经泄露的密钥立即轮换**：吊销旧密钥、签发新密钥、更新所有使用方——删除文件不解决 Git 历史问题；
4. **测试用证书与真实证书分开**：测试密钥放在 test resources 或由测试框架动态生成，避免"为了方便把真钥匙当测试数据"。

### 4.4 修复优先级决策框架

拿到 76 个漏洞的报告不必慌，按这个框架分流：

- **CRITICAL + 有 Fixed Version + 组件被业务实际使用** → 本迭代内升级修复（P0）；
- **HIGH + 有 Fixed Version** → 下一迭代修复（P1）；
- **无 Fixed Version（unfixed）** → 记录在案，评估临时缓解措施（如 WAF 规则、关闭相关功能），开启 `--ignore-unfixed` 减少报告噪声，等待官方补丁发布后优先处理；
- **确认不可达（漏洞功能未被使用）** → `.trivyignore` 豁免 + 注明原因 + 定期复审。

---

## 五、常见坑与避雷指南

### 坑 1：国内网络首次下载数据库超时

- **现象**：第一次运行长时间卡在 `Downloading DB...`，最终超时报错；
- **原因**：默认数据库源在 ghcr.io（海外），国内直连不稳定；
- **解法**：统一加南京大学镜像参数——`--db-repository ghcr.nju.edu.cn/aquasecurity/trivy-db:2` 与 `--java-db-repository ghcr.nju.edu.cn/aquasecurity/trivy-java-db:1`。数据库下载成功后本地有缓存，后续扫描不再需要网络。

### 坑 2：误解 `--ignore-unfixed` 的语义

- **现象**：以为加了它就不会再看到某些漏洞，结果报告里还有；
- **原因**：该参数只过滤**官方尚未发布修复版本**的漏洞；已有 Fixed Version 的漏洞依然照报不误——这是刻意设计，因为"可修而未修"恰恰是最需要暴露的风险；
- **解法**：真正想豁免特定 CVE，用 `.trivyignore`（并遵守"例外必须有理由"的原则）。

### 坑 3：滥用 `.trivyignore` 一键消音

- **现象**：图省事把所有报出的 CVE 全写进忽略文件，报告瞬间清爽，风险也瞬间隐形；
- **解法**：把忽略清单当作"风险接受登记表"管理——每条必须有原因与复审日期；**修复路径永远是优先升级依赖版本**，忽略只是给确实修不了的情况留的口子。

### 坑 4：密钥文件放进 `src/main/resources`

- **现象**：`target/classes/` 下出现密钥文件，Trivy 报 CRITICAL 秘密泄露；
- **原因**：Maven 会把 resources 目录原样打进产物；
- **解法**：真实密钥移出源码树，走环境变量 / 配置中心注入；`.gitignore` 补充密钥文件模式；已进 Git 历史的密钥立即吊销轮换。

### 坑 5：`--exit-code 1` 让 PowerShell "报错"

- **现象**：开启 exit-code 后终端一片红，疑似扫描失败；
- **原因**：非 0 退出码是给 CI 判断用的信号，**结果文件依然正常生成**，红字只是 PowerShell 对非 0 返回码的提示；
- **解法**：本地手动扫描不必加此参数；CI 中它正是门禁生效的证明。

### 坑 6：扫描到构建产物目录造成重复报告与误报

- **现象**：报告里 target/ 目录的内容被单独扫了一遍，漏洞条目翻倍；
- **原因**：Trivy 默认递归扫描整个目录，包括构建产物；
- **解法**：加 `--skip-dirs target`，或者干脆在执行 `mvn clean` 后扫描。

### 坑 7：PowerShell 续行符踩坑

- **现象**：多行命令复制粘贴后报"无法识别的参数"；
- **原因**：PowerShell 的续行符是反引号 `（而不是 Linux 的 `\`），且反引号后面**不能有任何空格**；
- **解法**：要么整条命令写在一行，要么确保反引号紧贴行尾。

---

## 附录

### A. 快速参考命令卡片

```powershell
# 日常本地扫描（国内镜像 + 只看严重/高危 + 忽略无修复版本 + 只扫依赖漏洞）
trivy fs . `
--db-repository ghcr.nju.edu.cn/aquasecurity/trivy-db:2 `
--java-db-repository ghcr.nju.edu.cn/aquasecurity/trivy-java-db:1 `
--severity CRITICAL,HIGH `
--ignore-unfixed `
--scanners vuln

# 输出结果到文本文件
... | tee trivy-scan-result.txt      # 屏幕与文件同时输出
... > trivy-scan-result.txt          # 只写文件（覆盖）

# JSON / SARIF / HTML 输出
trivy fs . -f json  -o trivy-scan-result.json
trivy fs . -f sarif -o trivy-result.sarif
trivy fs . -f html  -o trivy-report.html

# 镜像扫描
trivy image myapp:latest --severity CRITICAL,HIGH

# 生成 SBOM
trivy fs . -f cyclonedx -o sbom.json
```

### B. 相关资源

| 资源 | 地址 |
|------|------|
| Trivy GitHub 仓库 | <https://github.com/aquasecurity/trivy> |
| Trivy 官方文档 | <https://trivy.dev> |
| Aqua 漏洞数据库查询（按 CVE/组件搜） | <https://avd.aquasec.com> |
| 官方 GitHub Action | <https://github.com/aquasecurity/trivy-action> |
| NVD 国家漏洞数据库 | <https://nvd.nist.gov> |
| 南京大学 ghcr 镜像站 | <https://ghcr.nju.edu.cn> |

---

## 结语

安全工具的价值不在扫描本身，而在它带来的**可见性**与**流程闭环**：Trivy 让"我引入了什么、泄露了什么"从不可见变为可见，让安全检查从上线后的救火前移到每次提交的构建时。对个人开发者，它是提交前的自查清单；对团队，它是 CI 里的安全闸门；对企业，它是合规审计的证据链。从今天起，在项目根目录敲下第一行 `trivy fs .`，就是左移的第一步。

> **注**：本文档基于用户提供的 Trivy 使用笔记整理扩写而成；工具版本、Action 版本（如 `trivy-action@v0.36.0`）与数据库镜像地址可能随时间推移更新，使用时请以官方仓库最新发布为准。
