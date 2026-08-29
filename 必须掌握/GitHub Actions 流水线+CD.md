# GitHub Actions 流水线 + 项目 CD 部署

**整体流程：先搞懂「什么时候触发什么」**

把流水线想象成三道门 + 一次发布：

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
   合并到 develop
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

**简单说**：

- **CI** 是守门员，保证代码质量，每提 PR 就跑
- **E2E** 是集成验证，合并后跑完整流程
- **Release** 是打包发布，打 tag 后出镜像
- **Deploy** 是上线，手动触发，部署到生产

## 一、GitHub Actions 基础

### 1.1 目录结构

```
.github/
└── workflows/
    ├── ci.yml              
    ├── e2e.yml             
    ├── release.yml        
    └── deploy.yml          
你的仓库/
├── .github/
│   └── workflows/
│       ├── ci.yml          # PR 触发：lint + 构建 + 单元测试
│       ├── e2e.yml         # 合并后触发：E2E 测试
│       ├── release.yml      # 打 tag 触发：构建镜像 + 发布 Release
│       └── deploy.yml      # 手动触发 / 合并 main：部署到生产
├── frontend/          # 你的前端代码
│   ├── package.json
│   └── ...
├── backend/           # 你的后端代码
│   ├── pom.xml (或 go.mod / requirements.txt)
│   └── ...
├── docker-compose.yml
└── README.md
```

### 1.2 核心概念速查

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

### 1.3 常用官方 Action

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

------

### 1.4 配置 Secrets（密钥存在哪）

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

> SSH 私钥就是你本地 `~/.ssh/id_rsa` 的内容（以 `-----BEGIN OPENSSH PRIVATE KEY-----` 开头的一大段），完整复制粘贴进去。

### 1.5 配置 Environments（环境审批）

**操作路径**：仓库页面 → Settings → Environments → New environment

- 建一个 `staging` 环境（测试环境，不用审批）
- 建一个 `production` 环境（生产环境，**勾上 Required reviewers**，选 1-2 个人审批）

这样生产部署必须有人点同意才能执行，防止手滑直接上线。

## 二、全栈项目 CI 流水线（PR 触发）

### 2.1 先改好配置文件

把 ci.yml 里的这些地方改成你自己的：

| 要改的地方                          | 改成什么               | 在哪找                                                 |
| :---------------------------------- | :--------------------- | :----------------------------------------------------- |
| `working-directory: frontend`       | 你前端代码的目录名     | 你的目录结构                                           |
| `node-version: "20"`                | 你项目用的 Node 版本   | `package.json` 里的 engines 或本地 `node -v`           |
| `npm run lint`                      | 你前端 lint 的命令     | `package.json` 的 scripts 里                           |
| `npm run build`                     | 你前端构建命令         | 同上                                                   |
| `npm run test:ci`                   | 你前端测试命令         | 同上                                                   |
| `backend` 部分的 JDK 版本和构建工具 | 你后端的技术栈         | Java 用 maven/gradle，Go 用 go build，Python 用 pytest |
| MySQL / Redis 连接信息              | 你后端读取的环境变量名 | 后端配置文件里的变量名                                 |

### 2.2 第一次跑 CI

1. 把 `.github/workflows/ci.yml` 提交到一个新分支
2. 提一个 PR 到 `develop` 或 `main`
3. 打开 PR 页面，往下滚，你会看到 CI 在跑（有个转圈的图标）
4. 点 **Details** 可以进到 GitHub Actions 页面看实时日志

```
PR 页面底部会显示：
✅ All checks have passed      ← 全通过了
  - CI / Frontend - Lint & Build
  - CI / Backend - Lint & Test
  - CI / DB Migration Test
```

### 2.3 常见失败原因 & 排查

| 报错信息                 | 原因                             | 解决方法                                      |
| :----------------------- | :------------------------------- | :-------------------------------------------- |
| `npm: command not found` | 没装 Node 或 setup-node 版本不对 | 检查 `setup-node` 的版本号                    |
| `npm ci` 失败            | 没有 package-lock.json 或有冲突  | 本地跑 `npm install` 提交 lock 文件           |
| 连接不上 MySQL           | services 配置有问题或端口不对    | 检查 services 的 ports 和 env 变量            |
| 构建成功但测试失败       | 代码有 bug 或测试用例挂了        | 点 Details 看具体哪个测试挂了                 |
| `permission denied`      | 脚本没有执行权限                 | 本地 `chmod +x` 再提交，或用 `bash xxx.sh` 跑 |

> **排查技巧**：在 Actions 页面打开失败的 job，展开失败的 step，看红色错误信息，从上往下找第一个报错。

### 2.4 完整 CI 配置

```yaml
# .github/workflows/ci.yml
name: CI

on:
  pull_request:
    branches: [main, develop]
  push:
    branches: [develop]

concurrency:
  group: ci-${{ github.ref }}
  cancel-in-progress: true

jobs:
  # ---------- 前端 ----------
  frontend-lint-and-build:
    name: Frontend - Lint & Build
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: frontend

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: "20"
          cache: "npm"
          cache-dependency-path: frontend/package-lock.json

      - name: Install dependencies
        run: npm ci

      - name: Lint
        run: npm run lint

      - name: Type check
        run: npm run typecheck

      - name: Unit test
        run: npm run test:ci

      - name: Build
        run: npm run build

      - name: Upload build artifact
        uses: actions/upload-artifact@v4
        with:
          name: frontend-dist
          path: frontend/dist
          retention-days: 7

  # ---------- 后端 ----------
  backend-lint-and-test:
    name: Backend - Lint & Test
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: backend

    # 服务容器：测试需要的数据库、缓存等
    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: test123456
          MYSQL_DATABASE: test_db
        ports:
          - 3306:3306
        options: >-
          --health-cmd "mysqladmin ping -h localhost"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

      redis:
        image: redis:7
        ports:
          - 6379:6379
        options: >-
          --health-cmd "redis-cli ping"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Setup JDK
        uses: actions/setup-java@v4
        with:
          java-version: "17"
          distribution: "temurin"
          cache: "maven"

      - name: Build & Test
        run: mvn -B clean verify
        env:
          SPRING_DATASOURCE_URL: jdbc:mysql://localhost:3306/test_db
          SPRING_DATASOURCE_USERNAME: root
          SPRING_DATASOURCE_PASSWORD: test123456
          SPRING_REDIS_HOST: localhost
          SPRING_REDIS_PORT: 6379

      - name: Upload coverage report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: backend-coverage
          path: backend/target/site/jacoco
          retention-days: 7

  # ---------- 数据库迁移测试 ----------
  db-migration-test:
    name: DB Migration Test
    runs-on: ubuntu-latest
    needs: backend-lint-and-test

    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: test123456
          MYSQL_DATABASE: test_db
        ports:
          - 3306:3306
        options: >-
          --health-cmd "mysqladmin ping -h localhost"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Setup Flyway
        run: |
          wget -qO- https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/10.0.0/flyway-commandline-10.0.0-linux-x64.tar.gz | tar xvz
          sudo ln -s $PWD/flyway-10.0.0/flyway /usr/local/bin/flyway

      - name: Run migration
        run: |
          flyway -url=jdbc:mysql://localhost:3306/test_db \
                 -user=root -password=test123456 \
                 -locations=filesystem:backend/src/main/resources/db/migration \
                 migrate

      - name: Test rollback (undo migration)
        run: |
          # 如果用的是 undo 迁移，验证回滚
          flyway -url=jdbc:mysql://localhost:3306/test_db \
                 -user=root -password=test123456 \
                 -locations=filesystem:backend/src/main/resources/db/migration \
                 undo

  # ---------- 依赖安全扫描 ----------
  dependency-security:
    name: Dependency Security Scan
    runs-on: ubuntu-latest
    needs: [frontend-lint-and-build, backend-lint-and-test]

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Frontend npm audit
        run: npm audit --audit-level=high
        working-directory: frontend
        continue-on-error: true

      - name: Backend dependency check
        run: mvn org.owasp:dependency-check-maven:check
        working-directory: backend
        continue-on-error: true

  # ---------- 代码质量门禁 ----------
  quality-gate:
    name: Quality Gate
    runs-on: ubuntu-latest
    needs: [frontend-lint-and-build, backend-lint-and-test, db-migration-test]
    if: success()

    steps:
      - name: All checks passed
        run: echo "✅ All CI checks passed!"
```

### 2.5 关键设计说明

| 设计点                               | 说明                                                        |
| :----------------------------------- | :---------------------------------------------------------- |
| `concurrency` + `cancel-in-progress` | 同一分支多次 push 时，取消上一次未完成的流水线，节省资源    |
| `npm ci` 而非 `npm install`          | 严格按照 `package-lock.json` 安装，保证依赖一致性           |
| `cache`                              | 缓存 node_modules / maven 依赖，第二次构建速度提升明显      |
| `services`                           | 直接在 CI 环境起 MySQL / Redis，测试用真数据库而不是全 Mock |
| `retention-days`                     | 构建产物保留 7 天，够排查问题，不浪费存储                   |
| `continue-on-error: true`            | 依赖安全扫描只告警不阻断，避免误报影响发版                  |

------

## 三、E2E 测试流水线（合并后触发）

### 3.1 什么时候触发

- 代码合并到 `develop` 分支后自动触发
- 也可以手动触发：Actions → E2E Tests → Run workflow

### 3.2 E2E 干了什么

1. 起 MySQL + Redis
2. 编译启动后端服务
3. 编译前端
4. 装 Playwright 浏览器
5. 跑 E2E 测试脚本
6. 失败了自动上传截图报告（artifact 里下载）

### 3.3 为什么 E2E 不放在 CI 里

- E2E 慢（几分钟到十几分钟），放 CI 里每次提 PR 都跑太耗时间
- PR 阶段保证代码质量就够了，合并后再跑完整流程
- 真挂了影响范围也小，只是 develop 分支有问题，不影响 main

```yaml
# .github/workflows/e2e.yml
name: E2E Tests

on:
  push:
    branches: [develop]
  workflow_dispatch:

jobs:
  e2e:
    name: E2E Tests (Playwright)
    runs-on: ubuntu-latest

    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: test123456
          MYSQL_DATABASE: test_db
        ports:
          - 3306:3306
        options: >-
          --health-cmd "mysqladmin ping -h localhost"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

      redis:
        image: redis:7
        ports:
          - 6379:6379

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Setup JDK & Build Backend
        uses: actions/setup-java@v4
        with:
          java-version: "17"
          distribution: "temurin"
          cache: "maven"

      - name: Start backend
        working-directory: backend
        run: |
          mvn -B clean package -DskipTests
          java -jar target/app.jar &
        env:
          SPRING_DATASOURCE_URL: jdbc:mysql://localhost:3306/test_db
          SPRING_DATASOURCE_USERNAME: root
          SPRING_DATASOURCE_PASSWORD: test123456
          SPRING_REDIS_HOST: localhost

      - name: Setup Node.js & Build Frontend
        uses: actions/setup-node@v4
        with:
          node-version: "20"
          cache: "npm"
          cache-dependency-path: frontend/package-lock.json

      - name: Install frontend deps
        working-directory: frontend
        run: npm ci

      - name: Install Playwright browsers
        working-directory: frontend
        run: npx playwright install --with-deps

      - name: Wait for backend to be ready
        run: |
          for i in {1..30}; do
            curl -s http://localhost:8080/actuator/health && break
            sleep 2
          done

      - name: Run E2E tests
        working-directory: frontend
        run: npm run test:e2e
        env:
          BASE_URL: http://localhost:5173
          API_BASE_URL: http://localhost:8080

      - name: Upload Playwright report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: playwright-report
          path: frontend/playwright-report
          retention-days: 14
```

------

## 四、Release 发布流水线（打 tag 触发）

### 4.1 触发条件

打一个 `v` 开头的 tag 并推送，就会触发：

```bash
git tag v1.2.0
git push origin v1.2.0
```

或者在 GitHub 网页上手动打：Releases → Draft a new release → 填版本号 → Publish release

### 4.2 这个流水线做了三件事

```
打 tag v1.2.0
    │
    ├─► 1. 构建 Docker 镜像，推送到 Docker Hub
    │      打两个 tag：v1.2.0 和 latest
    │
    ├─► 2. 生成 GitHub Release 页面
    │      自动从 CHANGELOG.md 提取版本说明
    │
    └─► 3. 自动部署到测试环境 (staging)
           SSH 到服务器，拉新镜像，重启容器
```

### 4.3 镜像构建的原理（Docker 多阶段）

前端和后端各自有 `Dockerfile`，核心思想是「**在 CI 里构建，只把运行时需要的东西打包进镜像**」：

- 前端：Node 环境里 `npm run build` 生成 dist → 把 dist 扔到 Nginx 镜像里 → 最终镜像只有几十 MB
- 后端：JDK 环境里 `mvn package` 打 jar → 把 jar 扔到 JRE 镜像里 → 不用带编译工具

这样镜像体积小，拉取快，也更安全（没有编译工具链）。

```yaml
# .github/workflows/release.yml
name: Release

on:
  push:
    tags:
      - "v*.*.*"

permissions:
  contents: write

jobs:
  build-and-push-image:
    name: Build & Push Docker Image
    runs-on: ubuntu-latest
    outputs:
      version: ${{ steps.get-version.outputs.version }}

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Extract version
        id: get-version
        run: echo "version=${GITHUB_REF#refs/tags/}" >> $GITHUB_OUTPUT

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Login to Docker Hub
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_TOKEN }}

      - name: Build and push backend image
        uses: docker/build-push-action@v6
        with:
          context: ./backend
          file: ./backend/Dockerfile
          push: true
          tags: |
            yourname/backend:${{ steps.get-version.outputs.version }}
            yourname/backend:latest
          cache-from: type=gha
          cache-to: type=gha,mode=max

      - name: Build and push frontend image
        uses: docker/build-push-action@v6
        with:
          context: ./frontend
          file: ./frontend/Dockerfile
          push: true
          tags: |
            yourname/frontend:${{ steps.get-version.outputs.version }}
            yourname/frontend:latest
          cache-from: type=gha
          cache-to: type=gha,mode=max

  create-github-release:
    name: Create GitHub Release
    runs-on: ubuntu-latest
    needs: build-and-push-image

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Extract version
        id: get-version
        run: echo "version=${GITHUB_REF#refs/tags/}" >> $GITHUB_OUTPUT

      - name: Generate Release Notes from CHANGELOG
        id: release-notes
        run: |
          # 从 CHANGELOG.md 中提取当前版本的内容
          VERSION="${{ steps.get-version.outputs.version }}"
          # 简单实现：找到版本标题到下一个版本标题之间的内容
          awk '/^## '"$VERSION"'/{flag=1; next} /^## [0-9]/{flag=0} flag' CHANGELOG.md > release_notes.md
          cat release_notes.md

      - name: Create GitHub Release
        uses: softprops/action-gh-release@v2
        with:
          body_path: release_notes.md
          generate_release_notes: true
          draft: false
          prerelease: false

  deploy-to-staging:
    name: Deploy to Staging
    runs-on: ubuntu-latest
    needs: create-github-release
    environment:
      name: staging
      url: https://staging.yourdomain.com

    steps:
      - name: Deploy via SSH
        uses: appleboy/ssh-action@v1.0.3
        with:
          host: ${{ secrets.STAGING_SERVER_HOST }}
          username: ${{ secrets.STAGING_SERVER_USER }}
          key: ${{ secrets.STAGING_SSH_KEY }}
          script: |
            cd /opt/app
            export VERSION=${{ needs.build-and-push-image.outputs.version }}
            docker compose pull
            docker compose up -d
            docker image prune -f
```

------

## 五、项目 CD 部署方案

### 5.1前置准备

在你自己的服务器上做这些准备（**只做一次**）：

```bash
# 1. 装 Docker 和 Docker Compose
# 2. 建目录
mkdir -p /opt/app/{nginx/ssl,data/mysql,data/redis,logs/nginx,logs/backend}

# 3. 上传 docker-compose.yml 和 nginx.conf 到 /opt/app/
# 4. 创建 .env 文件，填敏感信息
cat > /opt/app/.env << 'EOF'
DB_ROOT_PASSWORD=你的数据库密码
DB_USERNAME=你的数据库用户
DB_PASSWORD=你的数据库密码
REDIS_PASSWORD=你的Redis密码
EOF

# 5. 登录 Docker Hub（不然拉不了私有镜像）
docker login -u 你的用户名
```

 **部署脚本放哪**

把 `deploy.sh` 和 `rollback.sh` 传到服务器的 `/opt/app/` 目录下，加执行权限：

```bash
chmod +x /opt/app/deploy.sh
chmod +x /opt/app/rollback.sh
```

**怎么触发部署**

两种方式：

**方式一：打 tag 自动触发**

- 在 `release.yml` 末尾加上部署 job，打 tag 后自动部署到 staging
- 生产环境不建议自动，还是手动稳

**方式二：手动触发（推荐生产用）**

- Actions → Deploy to Production → Run workflow → 填版本号 → 确定
- 如果配置了审批，需要审批人去点 Approve

**部署过程到底在干嘛**

```
GitHub Actions 服务器
    │
    │ SSH 连接到你的服务器
    │
    ▼
你的服务器执行：
1. cd /opt/app
2. ./deploy.sh v1.2.0
   ├─ docker compose pull        ← 拉新版本镜像
   ├─ docker compose up -d backend   ← 先更后端
   ├─ 等健康检查通过
   └─ docker compose up -d frontend  ← 再更前端
3. docker image prune -f        ← 清理旧镜像
```

** 出问题了怎么回滚**

两个办法：

**方法一：在 Actions 里手动触发回滚版本**

- 跑 Deploy 流水线，版本号填上个版本（如 `v1.1.0`）

**方法二：直接在服务器上执行回滚脚本**

```bash
cd /opt/app
./rollback.sh v1.1.0
```

本质都是「把镜像版本换成旧的，重新 `docker compose up -d`」。

### 5.1 部署架构总览

```
开发者本地                      GitHub                       你的服务器
   │                              │                              │
   │ git push feat/xxx            │                              │
   │─────────────────────────────►│                              │
   │                              │                              │
   │                           触发 CI                           │
   │                        (lint + build + test)                │
   │                              │                              │
   │                              │ ✅ 通过                      │
   │  合并 PR ◄───────────────────┤                              │
   │                              │                              │
   │ git push to develop          │                              │
   │─────────────────────────────►│                              │
   │                              │                              │
   │                           触发 E2E                         │
   │                        (端到端全流程测试)                    │
   │                              │                              │
   │ git tag v1.2.0               │                              │
   │ git push --tags              │                              │
   │─────────────────────────────►│                              │
   │                              │                              │
   │                          触发 Release                       │
   │                     构建 Docker 镜像                        │
   │                     推送到 Docker Hub ─────────────────────►│
   │                              │                           拉取镜像
   │                              │                           部署到 staging
   │                              │                              │
   │  手动点部署生产               │                              │
   │─────────────────────────────►│                              │
   │                              │ SSH 执行 deploy.sh           │
   │                              ├─────────────────────────────►│
   │                              │                           重启容器
   │                              │                           ✅ 上线完成
```

### 5.2 三种 CD 模式对比

| 模式               | 适用场景               | 优点                       | 缺点                 |
| :----------------- | :--------------------- | :------------------------- | :------------------- |
| **SSH 直连部署**   | 单服务器、小项目       | 简单直接，零额外组件       | 不可控，出问题难回滚 |
| **Docker Compose** | 1-3 台服务器，中小项目 | 环境一致，易迁移，回滚快   | 多机部署需手动协调   |
| **Kubernetes**     | 多实例、高可用、大项目 | 自动扩缩容、滚动更新、自愈 | 学习成本高，运维复杂 |

------

### 5.3 方案一：Docker Compose 部署（推荐中小项目）

#### 5.3.1 服务器目录结构

```
/opt/app/
├── docker-compose.yml     # 生产环境 compose
├── .env                   # 环境变量（敏感信息，不进 git）
├── nginx/
│   └── nginx.conf         # 反向代理配置
├── data/
│   ├── mysql/             # MySQL 数据卷
│   └── redis/             # Redis 数据卷
└── logs/
    ├── nginx/
    └── backend/
```

#### 5.3.2 生产环境 docker-compose.yml

```yaml
# /opt/app/docker-compose.yml
version: "3.8"

services:
  frontend:
    image: yourname/frontend:${VERSION:-latest}
    container_name: app-frontend
    restart: always
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/ssl:/etc/nginx/ssl:ro
      - ./logs/nginx:/var/log/nginx
    depends_on:
      - backend

  backend:
    image: yourname/backend:${VERSION:-latest}
    container_name: app-backend
    restart: always
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/app_db
      SPRING_DATASOURCE_USERNAME: ${DB_USERNAME}
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      SPRING_REDIS_HOST: redis
    volumes:
      - ./logs/backend:/app/logs
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy

  mysql:
    image: mysql:8.0
    container_name: app-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_ROOT_PASSWORD}
      MYSQL_DATABASE: app_db
    volumes:
      - ./data/mysql:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7
    container_name: app-redis
    restart: always
    command: redis-server --requirepass ${REDIS_PASSWORD}
    volumes:
      - ./data/redis:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5
```

#### 5.3.3 Nginx 反向代理配置

```nginx
# /opt/app/nginx/nginx.conf
events {
    worker_connections 1024;
}

http {
    include       mime.types;
    default_type  application/octet-stream;

    # 日志格式
    log_format main '$remote_addr - $remote_user [$time_local] '
                    '"$request" $status $body_bytes_sent '
                    '"$http_referer" "$http_user_agent"';

    access_log /var/log/nginx/access.log main;
    error_log  /var/log/nginx/error.log;

    sendfile on;
    keepalive_timeout 65;

    # gzip 压缩
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml;
    gzip_min_length 1024;

    # 前端静态资源
    server {
        listen 80;
        server_name yourdomain.com;

        # 前端静态页面
        location / {
            root /usr/share/nginx/html;
            index index.html;
            try_files $uri $uri/ /index.html;  # SPA 路由
        }

        # 后端 API 代理
        location /api/ {
            proxy_pass http://backend:8080/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;

            # 超时配置
            proxy_connect_timeout 30s;
            proxy_read_timeout 60s;
            proxy_send_timeout 60s;
        }

        # WebSocket 支持
        location /ws/ {
            proxy_pass http://backend:8080/ws/;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
            proxy_set_header Host $host;
        }

        # 静态资源缓存
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff2?)$ {
            root /usr/share/nginx/html;
            expires 30d;
            add_header Cache-Control "public, immutable";
        }
    }
}
```

#### 5.3.4 部署脚本（服务器上）

```bash
#!/bin/bash
# /opt/app/deploy.sh
set -e

VERSION=${1:-latest}
echo "🚀 Deploying version: $VERSION"

# 1. 拉取最新镜像
cd /opt/app
VERSION=$VERSION docker compose pull

# 2. 先只更新后端（数据库迁移由后端启动时自动执行）
echo "📦 Updating backend..."
VERSION=$VERSION docker compose up -d backend

# 3. 等后端健康检查通过
echo "⏳ Waiting for backend health check..."
for i in {1..30}; do
  if curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "✅ Backend is healthy"
    break
  fi
  sleep 3
done

# 4. 滚动更新前端
echo "📦 Updating frontend..."
VERSION=$VERSION docker compose up -d frontend

# 5. 清理旧镜像
docker image prune -f

echo "🎉 Deployment completed: $VERSION"
```

#### 5.3.5 回滚脚本

```bash
#!/bin/bash
# /opt/app/rollback.sh
set -e

PREVIOUS_VERSION=${1:?"Usage: ./rollback.sh <previous-version>"}
echo "⏪ Rolling back to: $PREVIOUS_VERSION"

cd /opt/app
VERSION=$PREVIOUS_VERSION docker compose pull
VERSION=$PREVIOUS_VERSION docker compose up -d

echo "✅ Rollback completed: $PREVIOUS_VERSION"
```

------

### 5.4 方案二：GitHub Actions + SSH 自动部署

```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  workflow_dispatch:
    inputs:
      version:
        description: "版本号 (如 v1.2.0)，默认 latest"
        required: false
        default: "latest"
  push:
    tags:
      - "v*.*.*"

jobs:
  deploy:
    name: Deploy to Production Server
    runs-on: ubuntu-latest
    environment:
      name: production
      url: https://yourdomain.com

    steps:
      - name: Extract version
        id: version
        run: |
          if [[ "${{ github.event.inputs.version }}" ]]; then
            echo "tag=${{ github.event.inputs.version }}" >> $GITHUB_OUTPUT
          elif [[ "${GITHUB_REF}" == refs/tags/* ]]; then
            echo "tag=${GITHUB_REF#refs/tags/}" >> $GITHUB_OUTPUT
          else
            echo "tag=latest" >> $GITHUB_OUTPUT
          fi

      - name: Deploy via SSH
        uses: appleboy/ssh-action@v1.0.3
        with:
          host: ${{ secrets.PROD_SERVER_HOST }}
          username: ${{ secrets.PROD_SERVER_USER }}
          key: ${{ secrets.PROD_SSH_KEY }}
          port: 22
          script: |
            cd /opt/app
            ./deploy.sh ${{ steps.version.outputs.tag }}

      - name: Verify deployment
        run: |
          for i in {1..20}; do
            STATUS=$(curl -s -o /dev/null -w "%{http_code}" https://yourdomain.com/health)
            if [ "$STATUS" = "200" ]; then
              echo "✅ Deployment verified, site is up"
              exit 0
            fi
            echo "⏳ Waiting for site to be ready... (status: $STATUS)"
            sleep 5
          done
          echo "❌ Deployment verification failed"
          exit 1
```

------

### 5.5 方案三：Kubernetes 部署（进阶）

> 如果项目规模大、需要多实例高可用，用 K8s。以下是核心配置。

#### 5.5.1 目录结构

```
k8s/
├── namespace.yml
├── backend/
│   ├── deployment.yml
│   ├── service.yml
│   └── hpa.yml           # 水平自动扩缩容
├── frontend/
│   ├── deployment.yml
│   ├── service.yml
│   └── ingress.yml
├── mysql/
│   ├── statefulset.yml
│   ├── pvc.yml
│   └── service.yml
├── redis/
│   ├── deployment.yml
│   └── service.yml
└── configs/
    ├── configmap.yml
    └── secrets.yml       # 用 Sealed Secrets 或外部密钥管理
```

#### 5.5.2 Backend Deployment 示例

YAML



```yaml
# k8s/backend/deployment.yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backend
  namespace: app
  labels:
    app: backend
spec:
  replicas: 3
  selector:
    matchLabels:
      app: backend
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1        # 滚动更新时最多多起 1 个
      maxUnavailable: 0  # 更新过程中不可用实例数为 0
  template:
    metadata:
      labels:
        app: backend
    spec:
      containers:
        - name: backend
          image: yourname/backend:v1.2.0
          ports:
            - containerPort: 8080
          envFrom:
            - configMapRef:
                name: app-config
            - secretRef:
                name: app-secrets
          resources:
            requests:
              cpu: "500m"
              memory: "512Mi"
            limits:
              cpu: "2000m"
              memory: "1Gi"
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 10
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 10
            periodSeconds: 5
          lifecycle:
            preStop:
              exec:
                command: ["sleep", "15"]   # 优雅关闭，等流量切走
```

#### 5.5.3 HPA 自动扩缩容

YAML



```yaml
# k8s/backend/hpa.yml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: backend-hpa
  namespace: app
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: backend
  minReplicas: 2
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70     # CPU 超过 70% 就扩容
    - type: Resource
      resource:
        name: memory
        target:
          type: Utilization
          averageUtilization: 80     # 内存超过 80% 就扩容
```

#### 5.5.4 K8s CD 方式：ArgoCD（GitOps）

> K8s 生态推荐用 GitOps 方式部署，ArgoCD 是主流选择。

**核心思想**：

- Git 仓库是唯一可信源（k8s 配置都在 git 里）
- ArgoCD 持续监听 Git 变化，自动同步到集群
- 回滚 = `git revert` + 自动同步

Plain Text



```
代码 push → CI 构建镜像 → 更新 k8s 配置中的镜像 tag → ArgoCD 自动部署
```

------

## 六、环境管理

### 6.1 多环境配置

| 环境                        | 用途                | 分支      | 数据               | 访问权限        |
| :-------------------------- | :------------------ | :-------- | :----------------- | :-------------- |
| **本地开发**                | 开发者本地调试      | feature/* | mock / 测试数据    | 本人            |
| **开发环境 (dev)**          | 日常联调、功能验证  | develop   | 测试数据           | 开发团队        |
| **测试环境 (test/staging)** | QA 测试、预发布验证 | release/* | 接近生产的测试数据 | 开发 + 测试     |
| **生产环境 (prod)**         | 正式对外服务        | main      | 真实用户数据       | 仅限运维/管理员 |

### 6.2 GitHub Environments 配置

在仓库 Settings → Environments 中配置：

- **staging** 环境：打 tag 后自动部署，无需审批
- **production** 环境：需要 1-2 人审批后才能部署，开启部署保护

```yaml
# workflow 中引用
environment:
  name: production
  url: https://yourdomain.com
```

------

## 七、Dockerfile 最佳实践

### 7.1 前端多阶段构建

```dockerfile
# frontend/Dockerfile

# ---- 构建阶段 ----
FROM node:20-alpine AS builder

WORKDIR /app
COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

# ---- 运行阶段 ----
FROM nginx:1.27-alpine

# 复制构建产物
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制 nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

### 7.2 后端多阶段构建

dockerfile

```dockerfile
# backend/Dockerfile

# ---- 构建阶段 ----
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# ---- 运行阶段 ----
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 创建非 root 用户
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# 复制 jar 包
COPY --from=builder /app/target/*.jar app.jar

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 7.3 Dockerfile 优化原则

- **多阶段构建**：最终镜像只含运行时依赖，体积小
- **分层缓存**：先复制依赖文件（package.json / pom.xml），再复制源码，依赖不变时命中缓存
- **非 root 用户运行**：安全加固
- **精简基础镜像**：用 alpine 版本
- **`.dockerignore`**：排除 node_modules、target、.git 等，减小构建上下文

------

## 八、CD 质量保障机制

### 8.1 部署前检查清单

-  CI 流水线全绿（lint、build、test、e2e）
-  数据库迁移脚本已验证可正向 + 回滚执行
-  配置文件 / 环境变量已更新到目标环境
-  Release Note / 变更说明已准备
-  回滚方案已确认（上一个稳定版本号）
-  运维 / 相关人员已通知

### 8.2 部署后验证清单

-  服务健康检查通过（/health 接口返回 200）
-  前端页面可正常访问，静态资源加载正常
-  核心业务流程手动跑通（登录、核心功能）
-  接口监控无大量 5xx 错误
-  日志无异常报错
-  数据库迁移执行成功，数据正常

### 8.3 蓝绿部署 / 金丝雀发布（进阶）

| 策略           | 原理                               | 适用场景                       |
| :------------- | :--------------------------------- | :----------------------------- |
| **滚动更新**   | 逐个替换旧实例，零停机             | 常规发布，大多数场景           |
| **蓝绿部署**   | 同时跑两套环境，流量一次性切换     | 重大版本、需要快速全量回滚     |
| **金丝雀发布** | 先切少量流量，观察没问题再逐步放量 | 高风险变更、大版本，降低影响面 |

**金丝雀发布（Nginx 简单实现）**：

nginx



```nginx
# 90% 流量去旧版本，10% 去新版本
upstream backend {
    server backend-old:8080 weight=9;
    server backend-new:8080 weight=1;
}
```

逐步调整权重：10% → 30% → 50% → 100%，每步观察 10-30 分钟。

------

## 九、监控与告警（CD 的配套设施）

> 部署完能跑起来不够，还要知道跑的好不好。

### 9.1 监控三件套

| 类型               | 工具                              | 看什么                   |
| :----------------- | :-------------------------------- | :----------------------- |
| **基础设施监控**   | Prometheus + Grafana              | CPU、内存、磁盘、网络    |
| **应用监控 (APM)** | SkyWalking / Pinpoint             | 接口耗时、慢 SQL、调用链 |
| **日志**           | ELK (Elastic + Logstash + Kibana) | 错误日志、业务日志检索   |

### 9.2 关键告警指标

-  服务不可用（健康检查失败）
-  5xx 错误率 > 1%
-  接口 P99 延迟 > 3s
-  CPU / 内存使用率 > 80% 持续 5 分钟
-  磁盘使用率 > 85%
-  数据库连接池耗尽
-  Redis 内存使用率 > 90%

------

以上就是 GitHub CI 流水线 + 项目 CD 部署的完整补充内容，你可以根据自己项目的技术栈（后端 Java / Go / Python）和规模选择对应的方案。

## 十、怎么看流水线运行结果

**进入方式**：仓库页面 → Actions 标签

每个 workflow run 进去后能看到：

Plain Text

```
Summary 页面
  ├── Jobs 列表  ← 哪些成功哪些失败
  ├── Artifacts  ← 上传的产物（构建包、测试报告、截图等）
  └── Annotations ← 警告、错误摘要

点进具体的 Job
  ├── 按 step 展开
  │   ├── Set up job
  │   ├── Checkout           ← 拉代码
  │   ├── Setup Node.js      ← 装环境
  │   ├── Install dependencies
  │   ├── Lint
  │   └── Build
  └── 每个 step 点展开看日志
```

**失败了怎么看**：

1. 找到红色的 step
2. 展开看日志
3. 往上翻，找到第一个 error 行
4. 根据错误信息定位问题

## 建议

### 第一次跑通的推荐步骤（建议顺序）

不要一次性把所有流水线都加上，按这个顺序来，踩坑少：

#### 第 1 步：先跑通 CI 的前端部分

- 只保留 `frontend-lint-and-build` 这个 job
- 提 PR 看能不能正常 lint + build
- 成功了再加单元测试

#### 第 2 步：加后端 CI

- 加上 `backend-lint-and-test` job
- 先不加 services（数据库），只跑构建和不需要数据库的测试
- 没问题了再加 MySQL services，跑集成测试

#### 第 3 步：加数据库迁移测试

- 单独的 job，专门测 migration 能不能跑通

#### 第 4 步：本地手动打 Docker 镜像试试

- 本地 `docker build -t test .` 确认 Dockerfile 没问题
- 本地 `docker run` 跑起来，访问一下看能不能用

#### 第 5 步：加 Release 流水线

- 先只做「构建镜像 + 推送 Docker Hub」
- 手动打个 tag 试试，确认镜像推上去了

#### 第 6 步：加部署

- 先部署到测试服务器，SSH 连成功就行
- 测试环境跑稳了再加生产环境 + 审批

### 最容易踩的坑

#### 9.1 路径问题

- `working-directory` 是相对于仓库根目录的，别写错
- 前端构建产物路径要和 Dockerfile 里对得上（比如 `dist/` 还是 `build/`）

#### 9.2 环境变量问题

- CI 里跑测试用的是 CI 环境的数据库，不是你本地的
- 后端要能从环境变量读数据库配置，不能写死在代码里

#### 9.3 权限问题

- GitHub Actions 默认只有读权限，推镜像、创建 Release 需要加 `permissions: write`
- SSH 私钥要完整复制，包括开头结尾的 `-----BEGIN...` 和 `-----END...` 行

#### 9.4 缓存问题

- `npm ci` 比 `npm install` 严格，必须有 lock 文件
- 如果缓存了依赖但 package.json 改了，缓存可能有问题，清一下缓存重新跑

#### 9.5 部署后服务起不来

- 先看日志：`docker logs app-backend`
- 多半是配置不对（数据库连不上、环境变量缺失、端口冲突）
- 本地用同样的镜像跑一下，排除镜像本身的问题

# CD

### 一、你需要准备的东西（共 4 样）

| #    | 项目                    | 说明                                                   |
| :--- | :---------------------- | :----------------------------------------------------- |
| 1    | **服务器 SSH 地址**     | 公网 IP 或域名 + 端口（默认 22，改过的话记下实际端口） |
| 2    | **登录用户名**          | root 或普通用户（建议专用部署用户如 `deploy`）         |
| 3    | **部署专用 SSH 密钥对** | 在服务器上生成，公钥留在服务器，私钥交给 GitHub        |
| 4    | **服务器上的目标目录**  | exe 要放到哪，如 `/var/www/portlens/download/`         |

### 二、服务器端步骤（一次性，约 5 分钟）

**1. 生成部署密钥对**（登录服务器执行）：

```bash
ssh-keygen -t ed25519 -f ~/.ssh/github_deploy -N "" -C "github-actions-deploy"
```

**2. 让这个密钥能登录本机**：

```bash
cat ~/.ssh/github_deploy.pub >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys
```

**3. 创建目标目录并授权**

```bash
mkdir -p /var/www/portlens/download
chown -R $USER /var/www/portlens
```

**4. 取出私钥内容**（复制，下一步要用）：

```bash
cat ~/.ssh/github_deploy
```

**5. 验证可用**（应免密直接登录）：

```bash
ssh -i ~/.ssh/github_deploy 用户名@127.0.0.1
```

### 三、GitHub 端步骤（一次性）

仓库页面 → **Settings → Secrets and variables → Actions → New repository secret**，添加 4 个：

| Secret 名称      | 填入内容                                             |
| :--------------- | :--------------------------------------------------- |
| `DEPLOY_HOST`    | 服务器 IP 或域名                                     |
| `DEPLOY_USER`    | 登录用户名                                           |
| `DEPLOY_SSH_KEY` | 上面 `cat` 出来的**私钥全部内容**（含 BEGIN/END 行） |
| `DEPLOY_PORT`    | SSH 端口，没改就填 22                                |

> Secrets 是加密存储的，日志里只会显示 `***`，不会泄露私钥。

### 四、build.yml 新增部署步骤（我来改）

在现有 "Upload to Release" 之后加：

```yaml
      - name: Deploy to server
        if: startsWith(github.ref, 'refs/tags/v')
        uses: appleboy/scp-action@v0.1.7
        with:
          host: ${{ secrets.DEPLOY_HOST }}
          username: ${{ secrets.DEPLOY_USER }}
          key: ${{ secrets.DEPLOY_SSH_KEY }}
          port: ${{ secrets.DEPLOY_PORT }}
          source: port.exe
          target: /var/www/portlens/download/
```

之后每次打 `v*` 标签：编译 → 测试 → 发 Release → **自动把 exe 推到你服务器**，全流程无需人