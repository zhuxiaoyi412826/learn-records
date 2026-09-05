# 小团队 GitHub Flow + GitHub Actions 全托管 CI/CD 落地方案

> 定位:3-8 人团队,一台云服务器,docker compose 运行,零自运维
> 核心链路:GitHub Flow → GitHub Actions → SonarCloud(质量门禁)→ Gitleaks / Trivy(安全扫描)→ GHCR 镜像仓库 → SSH 自动部署云服务器 → OWASP ZAP(DAST)→ Prometheus + Alertmanager 告警(飞书)

---

**CI-CD关键准则**

```
开发提交代码
    ↓
CI 流水线自动构建 → 产出带版本号的制品（如 app-1.2.3.jar 或镜像 xxx:1.2.3）
    ↓
推入制品库（Nexus / Artifactory / Harbor）
    ↓
部署到测试环境（拉取的是制品，不是源码重新编译）
    ↓
测试通过、审批通过
    ↓
同一个制品发布到预发/生产环境
```

## 0.前置知识

### 分支命名规则

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

**示例**

```
feat/#42-user-login-page
fix/#58-api-timeout
hotfix/#62-main-cors-error
refactor/backend-controller-split
docs/update-install-guide
release/v1.2.0
```

### Commit 提交规范

格式：`类型(模块): 描述`

> 模块可选：`frontend` / `backend` / `docs` / `common` / `db` 区分全栈不同部分

```
feat(frontend): 新增用户登录表单
fix(backend): 修复分页接口越界问题
refactor(common): 封装请求工具类
docs: 更新部署文档
chore: 调整eslint配置
test(backend): 新增用户service单元测试
```

- chore：构建、工程配置、依赖更新，无业务代码改动

### PR 和提交

**1. 提交 commit /push**

- **commit**：本地把改动保存到本地 git 仓库，只是电脑上的记录。
- **push**：把本地的 commit 推送到**远程 GitHub 仓库**。

> `push` 是把代码上传到远程分支；**可以直接推到 main，也可以推到 feature 临时分支**。

**2. PR（Pull Request）**

> PR**不是提交代码**，它是**远程仓库之间的合并请求**。

- 前提：代码已经 `push` 到远程的一个临时分支（`feature/xxx`）
- PR 含义：我请求把【远程 feature 分支】合并到【远程 main 分支】
- PR 本身**不会改动 main 分支**，只有手动点击「合并」，代码才进入 main。

表格

| 操作                          | 作用                               | 代码是否进入 main |
| ----------------------------- | ---------------------------------- | ----------------- |
| `git push origin main`        | 直接把本地代码推到远程 main        | ✅立刻进入 main    |
| `git push origin feature/xxx` | 推到远程临时分支                   | ❌不进入 main      |
| 创建 PR                       | 发起合并请求，运行 CI 门禁         | ❌不进入 main      |
| 点击合并 PR                   | 执行合并，把 feature 代码并入 main | ✅才进入 main      |

> 关键：
>
> - `push` 是上传代码；
> - PR 是**合并闸门**：在代码进入 main 之前跑 CI 校验。

### 合并流程

#### 前置仓库配置（必须开）

1. 分支保护：**禁止直接 push main**，main 只能通过 PR 合并
2. 开启：`Automatically delete head branches`，合并 PR 后**自动删除远程临时分支**，远程不会堆积一堆 feature 分支
3. 单人项目：关闭「需要代码评审」，自己可以直接合并自己的 PR；多人项目保留评审。

#### ✅标准工作流（GitHub Flow，推荐小项目）

1. 切回 main，拉取最新代码

```
git checkout main
git pull
```

1. 创建本地临时分支写代码

```
git checkout -b feature/xxx
# 修改代码
git add .
git commit -m "feat:xxx"
```

1. push 到远程，生成远程临时分支

```
git push -u origin feature/xxx
```

1. GitHub 网页新建 PR：源分支

   ```
   feature/xxx
   ```

   ，目标

   ```
   main
   ```

   - 自动触发`pull_request`事件，运行 CI 门禁：单测、gitleaks、trivy 扫描
   - **只做校验，不部署**；失败就不能合并，main 不会被污染

2. CI 全部通过，手动点合并 PR

   - GitHub**自动删除远程 feature/xxx 分支**

3. 本地收尾

```
git checkout main
git pull
git branch -d feature/xxx  # 删除本地临时分支
git fetch --prune          # 清理本地缓存已经删除的远程分支
```

4. 合并完成，触发`push:main`事件，执行 CD 构建 + 部署。

**流水线事件对应**

- `pull_request`：PR 打开 / 更新 → **CI 门禁，不部署**
- `push main`：PR 合并完成代码进入 main → **CD 部署**

> 可选：生产环境开启 GitHub Environment，部署增加人工审批。

### CI/CD 工具链

| 企业级组件                                 | 个人 / 2-5 人替代                                     | 理由                                                       |
| :----------------------------------------- | :---------------------------------------------------- | :--------------------------------------------------------- |
| Jenkins + K8s 动态 Agent                   | **GitHub Actions / GitLab CI**                        | 零运维，免费额度够用(Actions 公开仓库无限免费)             |
| Nexus Maven 私服                           | 不建，直连中央仓库/阿里云镜像                         | 私服的收益来自多人复用缓存，单人无收益                     |
| Harbor                                     | **GHCR** / 阿里云 ACR 个人版                          | 免费托管，无需运维                                         |
| SonarQube 质量门禁                         | 只留一条硬门禁：**单测必须过**;可选 SonarCloud 免费版 | 全套门禁对单人是负担不是保障                               |
| Trivy / Dependency-Check / Gitleaks 三件套 | **只留 Trivy**(CI 里扫镜像)+ 可选 Gitleaks            | 一个免费工具覆盖最关键漏洞面                               |
| Helm + Argo CD                             | docker-compose 或单节点 **K3s + 脚本部署**            | GitOps 的收益来自“多人改配置需要审计”，单人直接 apply 即可 |
| Vault + Sealed Secrets                     | GitHub Secrets + `.env`(gitignore)                    | 密钥集中管理的需求随人数出现                               |
| Prometheus/Grafana/Loki 全家桶             | **Uptime Kuma**(自托管、免费)+ Actuator + 日志落文件  | Grafana Cloud 免费版也可顶一阵                             |

## 1. 方案定位与总体架构

### 1.1 为什么是这套

| 对比项 | 本方案 | 自建 Jenkins 方案 |
|---|---|---|
| 流水线维护 | workflow YAML 在 Git 里,零运维 | Jenkins 实例 + 插件要人养 |
| 凭证管理 | Secrets + GITHUB_TOKEN 自动注入 | Credentials 手工配 |
| 服务器开销 | 云服务器只跑业务 + 监控 | 至少多一台 CI 节点 |
| 费用 | 公开仓库全免费,私有仓库免费额度内够用 | 全自费 |
| 迁移成本 | 镜像和 YAML 可平移到任何 CI | Jenkinsfile 绑定深 |

小团队的第一原则:**把运维预算花在业务服务器上,CI/CD 能托管就托管**。

### 1.2 全链路架构

**简化版**

```
开发者提 PR
    │
    ▼
┌─────────────┐
│  CI 流水线   │  ← 自动跑：lint + 构建 + 单元测试
│  (ci.yml)   │     不通过 → PR 标红，不能合并
└──────┬──────┘
       │ 通过
       ▼
   合并到 mian
       │
       ▼
┌─────────────┐
│ E2E 流水线  │  ← 自动跑：完整端到端测试
│ (e2e.yml)   │
└──────┬──────┘
       │ 通过
       ▼
   打 tag v1.2.0
       │
       ▼
┌─────────────┐
│ Release 流水线│ ← 自动跑：构建 Docker 镜像 + 推送到镜像仓库
│(release.yml)│     + 生成 GitHub Release + 部署到测试环境
└──────┬──────┘
       │
       ▼
  手动点击部署生产
       │
       ▼
┌─────────────┐
│ Deploy 流水线│ ← SSH 到服务器拉镜像重启
│(deploy.yml) │
└─────────────┘
```

- **CI** 是守门员，保证代码质量，每提 PR 就跑
- **E2E** 是集成验证，合并后跑完整流程
- **Release** 是打包发布，打 tag 后出镜像
- **Deploy** 是上线，手动触发，部署到生产

**复杂版**

```
开发者 ──push──▶ GitHub(main 受保护 + PR 评审)
                    │ 提 PR
                    ▼
        ┌─ PR 阶段(ci.yml)───────────────────────┐
        │ Gitleaks 密钥泄露扫描(容器直跑)          │
        │ mvn verify:单测 + JaCoCo 覆盖率         │← 自动跑：lint + 构建 + 单元测试
        │ SonarCloud 静态分析 + 质量门禁轮询       │ 不通过 → PR 标红，不能合并
        │ Trivy fs:依赖漏洞扫描                   │
        └───────────────┬────────────────────────┘
                        │ 全绿 → merge to main
                        ▼
                  ┌─────────────┐
                  │ E2E 流水线   │  ← 自动跑：完整端到端测试
                  │ (e2e.yml)   │
                  └─────┬───────┘ 
                        │ 通过
                        │
                        ▼
                打 tag v1.2.0
                        │
                        │
                        ▼
        ┌─ 部署阶段(deploy.yml)──────────────────┐
        │ environment: production 人工审批        │
        │ mvn package → docker build              │
        │ docker save → Trivy 镜像扫描(高危阻断) │
        │ 推送 GHCR(SHA tag + main tag)         │
        │ SSH 到云服务器 → 更新 .env → compose up │
        │ 健康检查 /actuator/health/readiness     │
        │ OWASP ZAP 基线扫描(部署后)            │
        │ 失败 → 飞书 Webhook 告警                │
        └───────────────┬────────────────────────┘
                        ▼
              云服务器(docker compose 拉取 GHCR 镜像)
        app + Prometheus + node-exporter + cadvisor
                        │ 宕机 / 高负载 / 磁盘满
                        ▼
               Grafana Cloud → 飞书群告警
        (Watchdog 规则持续验证告警链路存活)
```

### 1.3 工具清单

| 环节 | 工具 | 费用 |
|---|---|---|
| 分支协作 | GitHub Flow + PR + 分支保护 | 免费 |
| CI 引擎 | GitHub Actions | 公开无限,私有 2000 分钟/月 |
| 静态扫描 | SonarCloud(SonarQube 云端版) | 公开仓库免费 |
| 密钥泄露 | Gitleaks(容器直跑,无授权问题) | 免费 |
| 依赖/镜像漏洞 | Trivy | 免费 |
| DAST | OWASP ZAP 基线扫描 | 免费 |
| 镜像仓库 | GHCR(GitHub Packages) | 公开免费,私有 500MB |
| 部署 | SSH action + docker compose | 自备云服务器 |
| 指标 | Grafana Cloud | 免费额度 |
| 日志 | Grafana Cloud Loki | 免费额度 |
| 告警 | Alertmanager + PrometheusAlert(飞书格式转换) | 开源 |
| 依赖升级 | Dependabot | 免费 |
| 代码安全 | CodeQL | 免费 |
| 发版 | Release Drafter(自动 changelog) | 免费 |

---

## 2. GitHub Flow 小团队规范

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260830195514.png)

### 2.1 分支规则

只有 `main` 和短命 `feature/*`,外加约定俗成的 `hotfix/*` 前缀(结构上与 feature 完全相同,只用于标记紧急程度):

| 分支 | 用途 | 生命周期 |
|---|---|---|
| `main` | 唯一长期分支,随时可部署 | 永久 |
| `feature/*` | 功能开发 | 1-2 天合回 |
| `hotfix/*` | 线上紧急修复(本质是插队的 PR) | 数小时 |

1. **main 始终可部署**——唯一长期分支，开启分支保护(禁止直推)；
2. **切功能分支**:`git checkout -b feature/xxx`,命名即描述，生命周期控制在 1-2 天；
3. **提交并推送，开 PR**:push 到远端立刻开 PR,即使没写完——PR 是讨论区，不是完成证书；
4. **评审 + CI 门禁**：评审人提意见、CI 自动跑测试，不通过就继续在分支上补提交，PR 自动更新；
5. **合并回 main**:全部通过后 squash merge,删除 feature 分支；
6. **合并即部署**：main 更新触发 CD 自动发布(或打 tag 后发布)，部署完成即进入下一轮。

两条纪律：**main 受保护，只能通过 PR 合入**；**CI 不绿不允许合并**。其余全靠习惯，没有流程仪式

合并方式统一选 **Squash and merge**:PR 压成一个提交,历史线性,回滚粒度就是"一个 PR 一次变更",与镜像 SHA tag 一一对应。

### 2.2 提交规范

约定式提交(conventional commits),前缀决定 Release Drafter 的归类:

```
feat: 订单导出支持 Excel
fix: 库存扣减并发下超卖
docs: 补充部署文档
chore: 升级 spring-boot 3.2.9
```

### 2.3 PR 约定

- push 当天就开 PR(标题带 `[WIP]` 或用 draft PR),CI 先跑起来,评审人早介入;
- 提交补齐后 Request review,1 人批准 + CI 全绿即可合并;
- hotfix PR 标题加 `hotfix:` 前缀,评审人优先处理,合并后自动走同一条部署链路。

### 2.4 GitHub Actions知识

---

**核心概念**

| 概念        | 说明                                                        |
| :---------- | :---------------------------------------------------------- |
| `workflow`  | 一个完整的流水线（一个 yml 文件）                           |
| `job`       | 流水线中的一个阶段（如 build、test），默认并行              |
| `step`      | job 中的具体步骤（如 checkout、run、action）                |
| `runs-on`   | 运行环境（`ubuntu-latest`、`windows-latest` 等）            |
| `on`        | 触发条件（push、pull_request、schedule、workflow_dispatch） |
| `needs`     | job 依赖，控制执行顺序                                      |
| `secrets.*` | 仓库/组织级加密变量（密钥、token 等）                       |
| `${{ }}`    | 表达式语法，引用变量、上下文                                |
| `uses`      | 引用官方或社区 Action（复用逻辑）                           |

**Action**

| Action                          | 用途                     |
| :------------------------------ | :----------------------- |
| `actions/checkout@v4`           | 拉取代码                 |
| `actions/setup-node@v4`         | 安装 Node.js             |
| `actions/setup-java@v4`         | 安装 JDK                 |
| `actions/setup-python@v5`       | 安装 Python              |
| `actions/setup-go@v5`           | 安装 Go                  |
| `actions/cache@v4`              | 缓存依赖，加速构建       |
| `actions/upload-artifact@v4`    | 上传构建产物             |
| `actions/download-artifact@v4`  | 下载构建产物             |
| `docker/setup-buildx-action@v3` | Docker Buildx 多架构构建 |
| `docker/login-action@v3`        | 登录镜像仓库             |
| `docker/build-push-action@v6`   | 构建并推送镜像           |

**配置 Secrets（密钥存在哪）**

所有不能写在代码里的密钥（密码、token、SSH key 等），都存在 GitHub 仓库的 Secrets 里：

**操作路径**：仓库页面 → Settings → Secrets and variables → Actions → New repository secret

| 名称（Name）          | 值（Secret）                    | 用途               |
| :-------------------- | :------------------------------ | :----------------- |
| `DOCKERHUB_USERNAME`  | 你的 Docker Hub 用户名          | 登录镜像仓库推镜像 |
| `DOCKERHUB_TOKEN`     | Docker Hub 的 Access Token      | 登录镜像仓库       |
| `STAGING_SERVER_HOST` | 测试服务器 IP 地址              | SSH 部署用         |
| `STAGING_SERVER_USER` | 测试服务器登录用户名（如 root） | SSH 部署用         |
| `STAGING_SSH_KEY`     | 测试服务器 SSH 私钥（完整内容） | SSH 登录认证       |
| `PROD_SERVER_HOST`    | 生产服务器 IP                   | 生产部署用         |
| `PROD_SERVER_USER`    | 生产服务器用户名                | 生产部署用         |
| `PROD_SSH_KEY`        | 生产服务器 SSH 私钥             | 生产部署用         |

## 3. 仓库结构

```
mall-app/
├── .github/
│   ├── workflows/
│   │   ├── ci.yml            # PR 阶段:测试 + Sonar + 扫描
│   │   ├── deploy.yml        # main 阶段:构建 + 部署 + ZAP
│   │   ├── codeql.yml        # 每周代码安全扫描
│   │   └── release.yml       # tag 时起草 Release
│   │   └── e2e.yml           # 端到端测试
│   ├── dependabot.yml        # 依赖自动升级 PR
│   └── release-drafter.yml   # changelog 分类规则
├── src/
├── pom.xml                   # Spring Boot + JaCoCo
├── Dockerfile                # 运行时镜像(jar 在 CI 已构建)
├── zap-rules.conf            # ZAP 基线规则
├── .trivyignore              # Trivy 豁免(需带理由)
├── .gitleaks.toml            # Gitleaks 白名单
└── ops/                      # 服务器侧部署物料(整目录拷到 /opt/mall)
    ├── docker-compose.yml    # app + 监控全家桶
    ├── prometheus.yml
    ├── rules.yml
    ├── alertmanager.yml
    ├── deploy.sh             # 服务器上的部署脚本(CI 调用)
    └── rollback.sh           # 回滚脚本
```

`ops/` 目录与代码同仓库,服务器上 `git pull` 或 `scp` 一次即可同步——基础设施定义跟着代码版本走。



## 4. PR 阶段流水线(ci.yml)

两个 job 并行:`quality`(编译 + 单测 + SonarCloud + 质量门禁)与 `security`(Gitleaks + Trivy fs)。两者都是分支保护里的必过检查。

```yaml
name: CI

on:
  pull_request:
    branches: [main]

permissions:
  contents: read

concurrency:
  group: ci-${{ github.ref }}
  cancel-in-progress: true      # 同一 PR 新推送取消旧跑,省分钟数

jobs:
  quality:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0        # SonarCloud 新代码计算需要完整历史

      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'
          cache: maven           # 依赖缓存,二次构建省 80% 时间

      - name: Build, Test & Sonar Analysis
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        run: >
          mvn -B verify
          org.sonarsource.scanner.maven:sonar-maven-plugin:3.11.0.3922:sonar
          -Dsonar.token=$SONAR_TOKEN

      - name: SonarCloud Quality Gate
        uses: sonarsource/sonarqube-quality-gate-action@master
        timeout-minutes: 5
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}

  security:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Gitleaks
        run: docker run --rm -v "$PWD:/repo" zricethezav/gitleaks:latest
             detect --source /repo --redact --exit-code 1

      - name: Trivy fs scan
        uses: aquasecurity/trivy-action@0.28.0
        with:
          scan-type: fs
          scanners: vuln
          severity: CRITICAL,HIGH
          ignore-unfixed: true
          exit-code: '1'
```

要点:

- `cache: maven` 是小团队最划算的一行配置,Actions 分钟数直接砍半;
- `fetch-depth: 0` 不能省,否则 SonarCloud 判定"全部代码都是新代码",门禁误报;
- Gitleaks 用容器直跑而非官方 action,规避 gitleaks-action 对组织仓库的授权限制;
- `concurrency` 块取消旧跑:同一 PR 连续 push 时只跑最新一次,分钟数省一截;

## 5. 部署阶段流水线(deploy.yml)

merge 到 main 自动触发,`environment: production` 提供人工审批卡点。

```yaml
name: Deploy

on:
  push:
    branches: [main]
  workflow_dispatch:

permissions:
  contents: read
  packages: write            # 推 GHCR 必须显式声明

concurrency:
  group: deploy-production
  cancel-in-progress: false   # 部署绝不并发,排队执行

env:
  IMAGE: ghcr.io/${{ github.repository_owner }}/mall-app

jobs:
  deploy:
    runs-on: ubuntu-latest
    environment: production   # Settings → Environments → production 加审批人
    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'
          cache: maven

      - name: Package
        run: mvn -B package -DskipTests   # 测试已在 PR 门禁强制

      - name: Build image (lowercase required)
        run: |
          IMAGE="${IMAGE,,}"              # GHCR 镜像名必须全小写
          SHA_TAG="${GITHUB_SHA::8}"
          echo "IMAGE_LC=$IMAGE" >> $GITHUB_ENV
          echo "SHA_TAG=$SHA_TAG" >> $GITHUB_ENV
          docker build -t "$IMAGE:$SHA_TAG" -t "$IMAGE:main" .

      - name: Trivy image scan (blocking)
        uses: aquasecurity/trivy-action@0.28.0
        with:
          input: image.tar
          scanners: vuln
          severity: CRITICAL,HIGH
          ignore-unfixed: true
          exit-code: '1'

      - name: Save image tar for Trivy
        run: docker save "$IMAGE_LC:$SHA_TAG" -o image.tar

      - name: Login to GHCR
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}   # 自动 token,无需配置

      - name: Push
        run: |
          docker push "$IMAGE_LC:$SHA_TAG"
          docker push "$IMAGE_LC:main"

      - name: Deploy to server
        uses: appleboy/ssh-action@v1.2.0
        with:
          host: ${{ secrets.DEPLOY_HOST }}
          username: ${{ secrets.DEPLOY_USER }}
          key: ${{ secrets.DEPLOY_KEY }}
          script: |
            cd /opt/mall
            echo "IMAGE_TAG=${{ env.SHA_TAG }}" > .env
            docker compose pull app
            docker compose up -d --remove-orphans app
            sleep 25
            curl -fsS http://localhost:8080/actuator/health/readiness \
              || (docker compose logs --tail 100 app && exit 1)
            docker image prune -f

  zap:
    needs: deploy
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: ZAP baseline
        uses: zaproxy/action-baseline@v0.12.0
        with:
          target: 'https://mall.example.com'
          rules_file_name: zap-rules.conf
          fail_action: true

  notify:
    needs: [deploy, zap]
    if: always() && contains(needs.*.result, 'failure')
    runs-on: ubuntu-latest
    steps:
      - name: Feishu alert
        run: |
          curl -s -X POST -H "Content-Type: application/json" \
            -d '{"msg_type":"text","content":{"text":"部署失败: '"${{ github.repository }} run ${{ github.run_id }}"'"}}' \
            "${{ secrets.FEISHU_WEBHOOK }}"
```

> 注:上面为便于阅读做了步骤拆分,实际使用时把 `Save image tar` 放到 `Trivy image scan` 之前——先 `docker save` 出 tar,Trivy 用 `input: image.tar` 扫 tar 文件,这样扫描发生在推送之前,坏镜像根本到不了 GHCR。

流程纪律:SHA tag 是部署事实上的"不可变版本号"(GHCR 不支持锁 tag,但 SHA tag 不会被重复构建覆盖——commit SHA 全局唯一),`.env` 记录当前部署的 SHA,回滚就是改 `.env`。

## 6. SonarCloud 配置

仓库根目录 `sonar-project.properties`:

```properties
sonar.projectKey=yourname_mall-app
sonar.organization=yourname
sonar.sources=src/main/java
sonar.java.binaries=target/classes
sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
sonar.exclusions=**/dto/**,**/entity/**,**/*Application.java
```

pom.xml 里配 JaCoCo 给 SonarCloud 喂数据:

```xml
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.12</version>
  <executions>
    <execution>
      <goals><goal>prepare-agent</goal></goals>
    </execution>
    <execution>
      <id>report</id>
      <phase>verify</phase>
      <goals><goal>report</goal></goals>
    </execution>
  </executions>
</plugin>
```

SonarCloud 网页侧配置(一次):

1. sonarcloud.io → Import from GitHub 授权;
2. Quality Gate 用默认的 Sonar way(新代码覆盖率 < 80%、重复率 > 3% 阻断),小团队不必自建复杂门禁;
3. Administration → Analysis Scope 排除生成代码;
4. My Account → Security 拿 token,填入 GitHub Secret `SONAR_TOKEN`。

SonarCloud 公开仓库免费无限;私有仓库按代码行计费,团队不想付费时,用自建 SonarQube 替换 `quality` job 即可,流水线其余部分不变。

## 7. Trivy 豁免管理

CI 阻断是"未修复的高危漏洞",对确定不影响的误报,写入 `.trivyignore` 并带理由:

```
# CVE-2021-xxxx  仅测试范围使用 H2,生产无此依赖,跟踪工单 #142
CVE-2021-xxxx
```

豁免必须写原因与工单号,没有理由的豁免三个月后没人敢删。

## 8. OWASP ZAP 基线规则

`zap-rules.conf`(ruleId + 等级 + 路径):

```
# ruleId  threshold  param
10015     WARN       https://mall.example.com      # 响应头缺失降为告警
10038     FAIL       https://mall.example.com/api  # CSP 缺失保持失败
10202     WARN       *                              # 反 CSRF 提示
```

基线扫描是被动探测 + 爬虫,单次 2-5 分钟,适合每 PR 部署后跑;全量主动扫描(Attacker 模式)耗时长,放每周定时任务或手动触发。首轮跑完常见十几条 WARN,按规则逐条调级,不要一上来就全 PASS。

## 9. GHCR 镜像管理

| 事项 | 做法 |
|---|---|
| 登录 | CI 用自动 `GITHUB_TOKEN`(需 `packages: write` 权限);服务器用 PAT(read:packages)一次性 `docker login` |
| tag | `sha8`(不可变事实版本)+ `main`(滚动最新) |
| 可见性 | Package 页面 Settings 里改 public/private |
| 清理 | 私有 500MB 免费额度,Actions 里加每周 job 按 workflow 删除 30 天前的 SHA tag |
| 权限 | 默认与仓库绑定,团队内加 collaborator 即可拉取 |

服务器拉私有镜像的一次性初始化:

```bash
docker login ghcr.io -u <github用户名> -p <PAT(read:packages)>
```

公开镜像可跳过登录——这也是小团队最省心的选择:仓库公开则全链路零成本。

## 10. 云服务器部署体系

### 10.1 服务器初始化(一次性)

```bash
# 1. 装 Docker + compose 插件(略)
# 2. 部署密钥:本地生成,公钥写服务器,私钥存 GitHub Secret DEPLOY_KEY
ssh-keygen -t ed25519 -f deploy_key -N ""
ssh-copy-id -i deploy_key.pub deploy@your-server

# 3. 同步部署物料
scp -r ops/ deploy@your-server:/opt/mall/

# 4. 服务器登录 GHCR(私有镜像才需要)
ssh deploy@your-server "docker login ghcr.io -u <user> -p <PAT>"

# 5. 拉起监控全家桶(应用由流水线部署)
cd /opt/mall && docker compose up -d prometheus alertmanager \
    prometheusalert node-exporter cadvisor
```

### 10.2 docker-compose.yml

```yaml
services:
  app:
    image: ghcr.io/yourname/mall-app:${IMAGE_TAG}
    container_name: mall-app
    restart: unless-stopped
    ports:
      - "8080:8080"
    environment:
      - TZ=Asia/Shanghai
    healthcheck:
      test: ["CMD", "curl", "-fsS", "http://localhost:8080/actuator/health"]
      interval: 30s
      retries: 3
    deploy:
      resources:
        limits: { memory: 1200M }

  node-exporter:
    image: prom/node-exporter:v1.8.2
    restart: unless-stopped
    pid: host
    command: [--path.rootfs=/host]
    volumes:
      - /:/host:ro

  cadvisor:
    image: gcr.io/cadvisor/cadvisor:v0.49.1
    restart: unless-stopped
    volumes:
      - /:/rootfs:ro
      - /var/run:/var/run:ro
      - /sys:/sys:ro
      - /var/lib/docker/:/var/lib/docker:ro

  prometheus:
    image: prom/prometheus:v2.54.1
    restart: unless-stopped
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml:ro
      - ./rules.yml:/etc/prometheus/rules.yml:ro
      - prom-data:/prometheus

  alertmanager:
    image: prom/alertmanager:v0.27.0
    restart: unless-stopped
    volumes:
      - ./alertmanager.yml:/etc/alertmanager/alertmanager.yml:ro

  prometheusalert:
    image: feiyu563/prometheus-alert:v4.9.1
    restart: unless-stopped
    ports:
      - "8081:8080"     # 管理页,首次启动在此填飞书 Webhook

volumes:
  prom-data:
```

`.env`(CI 每次部署重写,这就是唯一的部署状态):

```
IMAGE_TAG=abc12345
```

### 10.3 回滚(分钟级)

`ops/rollback.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail
TAG=$1                     # 用法: rollback.sh <sha8>
cd /opt/mall
echo "IMAGE_TAG=$TAG" > .env
docker compose up -d --remove-orphans app
sleep 20
curl -fsS http://localhost:8080/actuator/health/readiness
echo "rolled back to $TAG"
```

SHA tag 永远留在 GHCR 里,回滚不重新构建,一条命令完成。

### 10.4 替代部署模式(二选一)

| 模式 | 原理 | 适合 |
|---|---|---|
| SSH 推(本文主方案) | CI 主动 SSH 上去 `compose up` | 部署即审批后触发,节奏可控 |
| Watchtower 拉 | 服务器跑 watchtower,检测 `main` tag 镜像更新自动拉起 | 想要"push 完全自动上线",compose 加 watchtower 服务,app 打 `com.centurylinklabs.watchtower.enable=true` label |
| Self-hosted runner | runner 装在云服务器上,deploy job 直接本地执行 | 私有仓库分钟数超 2000/月,自托管 runner 无限免费,且免 SSH 密钥 |

三种可叠加演进:起步用 SSH,流量大了先换 self-hosted runner 省分钟数,再按需上 Watchtower。

---

## 11. 告警系统(Alertmanager)

### 11.1 链路结构

```
Prometheus(30s 抓取)
  ├─ node-exporter  → 主机 CPU / 内存 / 磁盘
  ├─ cadvisor       → 容器维度资源
  └─ app /actuator/prometheus → JVM / HTTP 指标
        │ 规则触发(rules.yml)
        ▼
  Alertmanager(分组 / 抑制 / 静默)
        │ webhook
        ▼
  PrometheusAlert(JSON → 飞书卡片格式转换)
        ▼
  飞书群机器人(值班手机可收)
```

Alertmanager 原生 webhook 只会 POST 原始 JSON,飞书机器人需要特定卡片格式,PrometheusAlert 就是这一层转换器——这是国内小团队最常用的组合,比自写转换脚本省事。

### 11.2 prometheus.yml

```yaml
global:
  scrape_interval: 30s

rule_files:
  - /etc/prometheus/rules.yml

alerting:
  alertmanagers:
    - static_configs:
        - targets: ['alertmanager:9093']

scrape_configs:
  - job_name: node
    static_configs:
      - targets: ['node-exporter:9100']
  - job_name: cadvisor
    static_configs:
      - targets: ['cadvisor:8080']
  - job_name: app
    metrics_path: /actuator/prometheus
    static_configs:
      - targets: ['app:8080']
```

Spring Boot 侧开启指标端点:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,prometheus
  endpoint:
    health:
      probes:
        enabled: true
```

### 11.3 告警规则 rules.yml

小团队不贪多,六条规则覆盖"服务器会死的所有方式":

```yaml
groups:
  - name: infra
    rules:
      - alert: InstanceDown
        expr: up == 0
        for: 1m
        labels: { severity: critical }
        annotations:
          summary: "{{ $labels.job }} 抓取失败 1 分钟(服务或主机宕机)"

      - alert: DiskSpaceHigh
        expr: (1 - node_filesystem_avail_bytes / node_filesystem_size_bytes) > 0.85
        for: 10m
        labels: { severity: warning }
        annotations:
          summary: "磁盘使用率超过 85%: {{ $labels.mountpoint }}"

      - alert: MemoryLow
        expr: node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes < 0.10
        for: 5m
        labels: { severity: warning }
        annotations:
          summary: "可用内存不足 10%"

      - alert: AppHigh5xx
        expr: |
          sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m]))
          / sum(rate(http_server_requests_seconds_count[5m])) > 0.02
        for: 3m
        labels: { severity: critical }
        annotations:
          summary: "应用 5xx 比例超过 2%"

      - alert: ContainerRestartLoop
        expr: increase(kube_pod_container_status_restarts_total[30m]) > 3
        labels: { severity: critical }
        annotations:
          summary: "容器 30 分钟内重启超过 3 次( CrashLoop)"

      - alert: Watchdog
        expr: vector(1)
        labels: { severity: none }
        annotations:
          summary: "告警链路心跳(此条常发即链路正常)"
```

Watchdog 是"永远处于 firing 状态"的死信规则:飞书群收不到它,说明 Prometheus / Alertmanager / 转发器某一环挂了——监控系统自己也要被监控。

### 11.4 alertmanager.yml

```yaml
route:
  group_by: [alertname]
  group_wait: 30s
  group_interval: 5m
  repeat_interval: 4h
  receiver: feishu

receivers:
  - name: feishu
    webhook_configs:
      - url: http://prometheusalert:8080/prometheus/feishu
        send_resolved: true      # 恢复时也发一条
```

PrometheusAlert 首次启动:浏览器开 `http://服务器IP:8081`,默认账号 `prometheusalert / prometheusalert`,在飞书配置里填群机器人 Webhook,保存后整条链路即通。8081 端口用完记得防火墙封掉或加反代鉴权,不要裸暴露公网。

### 11.5 两条告警通道的分工

| 通道 | 覆盖 | 特点 |
|---|---|---|
| Actions notify job → 飞书 | 流水线失败(部署 / ZAP / 门禁) | 部署挂了当场知道,链接直达 run 页 |
| Prometheus → Alertmanager → 飞书 | 运行时(宕机 / 磁盘 / 5xx / 重启) | 分组降噪,恢复通知 |

发布后 30 分钟内如果 AppHigh5xx 或 ContainerRestartLoop 响了,处置动作就是 10.3 节的 `rollback.sh`,两次命令内回到上一个 SHA。

---

## 12. 扩充工具

### 12.1 Dependabot(依赖自动升级)

`.github/dependabot.yml`:

```yaml
version: 2
updates:
  - package-ecosystem: maven
    directory: /
    schedule: { interval: weekly, day: monday }
    labels: [dependencies]
  - package-ecosystem: github-actions
    directory: /
    schedule: { interval: weekly }
    labels: [dependencies]
  - package-ecosystem: docker
    directory: /
    schedule: { interval: monthly }
    labels: [dependencies]
```

每周一自动开依赖升级 PR,PR 天然过全套 CI 门禁——升级是否安全由流水线回答,人只看结论。actions 自身也在升级范围内,workflow 不会烂在旧版本。

### 12.2 CodeQL(代码安全扫描)

`.github/workflows/codeql.yml`:

```yaml
name: CodeQL
on:
  push:
    branches: [main]
  schedule:
    - cron: '0 20 * * 6'      # 每周六扫描

jobs:
  analyze:
    runs-on: ubuntu-latest
    permissions:
      security-events: write
      contents: read
    steps:
      - uses: actions/checkout@v4
      - uses: github/codeql-action/init@v3
        with:
          languages: java
      - uses: github/gradle/actions  # 或 maven autobuild
      - uses: github/codeql-action/analyze@v3
```

与 Trivy 互补:Trivy 看"依赖版本有没有洞",CodeQL 看"你自己的代码有没有洞"(SQL 注入、硬编码密码)。免费,结果在 Security 标签页。

### 12.3 Release Drafter(自动发版)

`.github/workflows/release.yml`:

```yaml
name: Release
on:
  push:
    tags: ['v*']
jobs:
  release:
    runs-on: ubuntu-latest
    permissions:
      contents: write
    steps:
      - uses: release-drafter/release-drafter@v6
        with:
          publish: true
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

`.github/release-drafter.yml` 按提交前缀归类:

```yaml
name-template: 'v$RESOLVED_VERSION'
tag-template: 'v$RESOLVED_VERSION'
categories:
  - { title: '新增功能', labels: [feature] }
  - { title: '缺陷修复', labels: [fix, bug] }
  - { title: '维护', labels: [chore, docs, dependencies] }
version-resolver:
  major: { labels: [major] }
  minor: { labels: [minor, feature] }
  patch: { labels: [patch, fix, bug] }
```

Squash merge 时保留规范前缀,Release 页从此自动维护,无需手写 changelog。

### 12.4 可选增强

| 工具 | 作用 | 启用时机 |
|---|---|---|
| Uptime Kuma | 外部拨测(服务器整机死了 Prometheus 也哑) | 有第二台便宜 VPS 或用云厂商免费站点监控即可替代 |
| actionlint | workflow 语法静态检查 | pre-commit 阶段本地跑,docker `rhysd/actionlint` |
| Gitleaks pre-commit | 提交前本地拦截,不等 CI | `pre-commit install` 后免提交被拒的往返 |
| Renovate | Dependabot 的加强版(分组升级) | Dependabot 每周 PR 太碎时换 |
| GitHub Pages | 项目文档 / 静态站点托管 | mkdocs + actions 构建推 gh-pages 分支,零成本 |
| Self-hosted runner | 私有仓库超免费分钟数 | 见 10.4 |

---

## 13. Secrets 清单

Settings → Secrets and variables → Actions:

| Secret | 用途 | 生成方式 |
|---|---|---|
| `SONAR_TOKEN` | SonarCloud 分析 | sonarcloud.io → My Account → Security |
| `DEPLOY_HOST` | 云服务器 IP | - |
| `DEPLOY_USER` | SSH 用户(deploy) | - |
| `DEPLOY_KEY` | SSH 私钥全文 | `ssh-keygen -t ed25519` |
| `FEISHU_WEBHOOK` | 流水线失败通知 | 飞书群 → 设置 → 群机器人 |

`GITHUB_TOKEN` 每个 run 自动生成,不占 Secrets;服务器侧的 GHCR PAT 不进 GitHub,只在服务器 `docker login` 一次。

---

## 14. 免费额度与成本

| 项目 | 免费额度 | 超出后 |
|---|---|---|
| Actions(公开仓库) | 无限 | - |
| Actions(私有仓库) | 2000 分钟/月(Linux) | $0.008/分钟 |
| GHCR(公开镜像) | 无限 | - |
| GHCR(私有镜像) | 500MB 存储 | 按 GB 计费 |
| SonarCloud(公开仓库) | 免费 | - |
| SonarCloud(私有仓库) | 按代码行计费 | 换自建 SonarQube |
| Trivy / ZAP / Prometheus / Alertmanager | 开源 | - |
| 云服务器 | 自备 | 2C4G 起步可跑全套(应用 + 监控全家桶约 1.5G 内存) |

省额度三板斧:`cache: maven`、`concurrency` 取消旧跑、self-hosted runner。私有仓库月构建超 2000 分钟时,把 deploy job 挪到服务器上的 runner,分钟数立刻归零。

---

## 15. 落地步骤(一天搭完)

| 步骤 | 内容 | 耗时 |
|---|---|---|
| 1 | 建仓库,开 main 分支保护(1 审批 + CI 必过) | 10 min |
| 2 | 提交 Dockerfile / pom(JaCoCo)/ sonar-project.properties / zap-rules.conf | 30 min |
| 3 | 写 ci.yml,提 PR 演练,处理 ZAP/Trivy 首轮误报 | 30 min |
| 4 | SonarCloud 导入仓库,拿 token 填 Secret | 15 min |
| 5 | 服务器初始化:Docker、SSH 密钥、GHCR 登录、scp ops/、起监控栈 | 60 min |
| 6 | 写 deploy.yml,配 Environment production 审批人 | 20 min |
| 7 | 合并 PR 走全链路:构建 → 扫描 → GHCR → 部署 → ZAP | 15 min |
| 8 | 故意改坏 health,验证失败告警到飞书;修好后跑 rollback.sh 验证回滚 | 30 min |

第 8 步不能省:没演练过的告警和回滚,等于没有。

---

## 16. 常见坑

- **GHCR 镜像名大写报错**:`ghcr.io/YourName/app` 拒绝推送,owner 或仓库名含大写时用 `${IMAGE,,}` 转小写;
- **`packages: write` 没声明**:push 步骤 401,GITHUB_TOKEN 默认只读,permissions 块必须写;
- **SonarCloud 判定全仓库是新代码**:`fetch-depth: 0` 忘了加,覆盖率门禁全线飘红;
- **SSH action 连不上**:Secret 里要贴私钥全文(含 BEGIN/END 行),服务器上 `authorized_keys` 权限 600;
- **fork PR 拿不到 Secrets**:外部贡献者的 PR 只读 token,secrets 不注入——这是安全设计,门禁 job 对 fork 用 `pull_request_target` 需慎重;
- **Watchdog 刷屏**:severity=none 的心跳告警别给 repeat 4h,在 PrometheusAlert 侧关掉该级别推送或把 repeat_interval 拉长到 24h;
- **ZAP 首轮一片红**:baseline 默认规则偏严,按 `zap-rules.conf` 逐条降 WARN,别整体 fail_action=false 掩盖问题;
- **compose pull 拉了旧镜像**:`main` tag 是滚动的,本地已有同名 tag 时 compose 不会自动重拉——所以部署脚本用 SHA tag(.env 指向),每个 SHA 本地必不存在,强制拉取。

---

## 17. 演进路线

```
本文方案(3-8 人,1 台服务器)
   │ 服务超过 3 个 / 加第 2 台服务器
   ▼
ops/ 升级为 K3s + Helm(单机集群,compose 平移)
   │ 团队超 10 人 / 需要环境隔离与审计
   ▼
Argo CD GitOps + 多环境 values(参考企业级方案文档)
   │ 分钟数与镜像规模上来
   ▼
自建 Harbor + Nexus(参考企业级方案文档)
```

每个阶段的产物都是下一阶段的输入:GitHub Flow 与 GHCR 镜像原样保留,只有部署面从 compose 换 Helm、从 SSH 换 Argo CD——小团队方案不是临时的,它是演进的第一层。


