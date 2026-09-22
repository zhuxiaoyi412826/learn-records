# AlgoViz 项目 GitHub Actions 集成总结

> 项目：算法数据结构可视化（AlgoViz）
> 仓库：`zhuxiaoyi412826/algoviz`
> 镜像仓库：GitHub Container Registry（ghcr.io）
> 日期：2026-08-30

---

## 目录

1. [概述](#1-概述)
2. [GitHub Actions 核心概念](#2-github-actions-核心概念)
3. [build.yml —— 打 jar 包 + 静态检测 + 单元测试 + 发布 Release](#3-buildyml--打-jar-包--静态检测--单元测试--发布-release)
4. [docker.yml —— 构建并推送 Docker 镜像到 ghcr.io](#4-dockeryml--构建并推送-docker-镜像到-ghcrio)
5. [Dockerfile 容器化适配](#5-dockerfile-容器化适配)
6. [打 tag 与 GitHub Release](#6-打-tag-与-github-release)
7. [CodeQL 代码安全扫描](#7-codeql-代码安全扫描)
8. [用 curl 验证镜像是否 public 且存在](#8-用-curl-验证镜像是否-public-且存在)
9. [实战中遇到的问题与解决方案](#9-实战中遇到的问题与解决方案)
10. [不足与扩展方向](#10-不足与扩展方向)
11. [总结](#11-总结)

---

## 1. 概述

AlgoViz 是一个"算法数据结构可视化"项目，包含：

- **houduan**：Spring Boot 3.2 / JDK 17 / Maven 后端
- **houtai**：Vue 3.5 + Vite 8 后台管理（TypeScript）
- **qianduan**：原生 HTML 前台
- **algo-common-api**：公共 Maven 模块（know-api + know-qrdant，Dubbo + Qdrant 向量检索）

为了自动化"提交 → 验证 → 构建 → 发布"的流程，我们在 GitHub 仓库中配置了两套 Actions 工作流：

| 工作流 | 文件 | 职责 |
|---|---|---|
| CI Build & Release | `.github/workflows/build.yml` | 编译、静态检测、单元测试、打 jar、上传产物、打 tag 发布 Release |
| Docker Build & Push | `.github/workflows/docker.yml` | 每次提交/PR 构建 Docker 镜像，推送 ghcr.io，并保持包可见性为 public |

另外，CodeQL 通过 GitHub 安全中心的**默认设置（Default setup）**运行，无需手工编写 workflow 文件。

---

## 2. GitHub Actions 核心概念

在理解两个 yml 文件前，先厘清几个关键概念：

### 2.1 触发器（on）

```yaml
on:
  push:          # 代码 push（含打 tag —— tag push 也是 push 事件）
  pull_request:  # 合并请求
```

- `push`：推送任意分支或 tag 时触发。**打 tag 也是 push 事件**，无需单独声明 `tags:` 过滤器。
- `pull_request`：发起/更新 PR 时触发。
- 可以加 `paths:` 过滤（只在这些路径变化时触发）、`branches:`/`tags:` 过滤，本项目为了"每次提交都构建"，不设过滤。

### 2.2 权限（permissions）

GitHub 自动注入 `secrets.GITHUB_TOKEN`，但其权限由 `permissions` 块决定。**一旦写了 permissions 块，未列出的权限全部为 `none`**。

| 权限 | 含义 |
|---|---|
| `contents: write` | 可写仓库内容，创建 Release 需要 |
| `packages: write` | 可推送/下载 ghcr.io 镜像与 GitHub Packages |
| `contents: read` | 只读仓库内容（构建默认） |

> ⚠️ 踩坑：早期 build.yml 只写了 `contents: write`，没有 `packages`，导致所有涉及 ghcr.io 的操作报 `403 Resource not accessible by integration`。这就是"没有下载权限"的根因。

### 2.3 上下文（context）

- `github.repository`：`owner/repo`，如 `zhuxiaoyi412826/algoviz`
- `github.repository_owner`：owner 名
- `github.ref`：`refs/heads/main` 或 `refs/tags/v1.0.0`
- `github.ref_name`：`main` 或 `v1.0.0`
- `github.ref_type`：`branch` 或 `tag`
- `github.event_name`：`push` / `pull_request` 等
- `github.sha`：当前提交完整 SHA
- `github.event.repository.default_branch`：仓库默认分支名

### 2.4 产物与缓存

- `actions/upload-artifact@v4`：把 jar 等产物上传，可在 Actions 页面下载。
- `docker/setup-buildx-action@v3`：启用 BuildKit 多架构构建。
- `cache-from/cache-to: type=gha`：把镜像层缓存存到 GitHub Actions 缓存，加速下次构建。

---

## 3. build.yml —— 打 jar 包 + 静态检测 + 单元测试 + 发布 Release

### 3.1 完整文件

```yaml
# ============================================================
# AlgoViz 后端 CI：编译 → 静态检测 → 单元测试 → 打包 jar → 上传产物 → 打 tag 发布 Release
# 触发：push（含打 tag）、pull_request
# 发布：仅 push 到 v* 标签时触发 Release；普通 push / PR 只跑 CI，不发布
# ============================================================
name: CI Build & Release

on:
  push:          # 代码 push（含打 tag —— tag push 也是 push 事件）
  pull_request:  # 合并请求

permissions:
  contents: write   # 允许打 tag 时创建 Release（软著/版本发布用）
  packages: write   # 允许推送/下载 ghcr.io 镜像与包

jobs:
  build:
    name: 编译·检测·测试·打包
    runs-on: ubuntu-latest

    steps:
      - name: 检出代码
        uses: actions/checkout@v4

      - name: 配置 JDK 17 + Maven 缓存
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'          # 项目 java.version = 17（Spring Boot 3.2）
          cache: maven

      # houduan 依赖 com.algoviz:know-api:1.0.0（algo-common-api 公共模块），
      # 必须先 install 到本地 Maven 仓库，houduan 才能编译
      - name: 0) 构建公共模块（algo-common-api → know-api / know-qrdant）
        working-directory: algo-common-api
        run: mvn -B -q install -DskipTests

      # 静态检测：编译期即做类型/引用检查（如需更严格可换 SpotBugs/Checkstyle：
      #   mvn -B com.github.spotbugs:spotbugs-maven-plugin:4.8.6.6:check）
      - name: 1) 静态检测（编译检查）
        working-directory: houduan
        run: mvn -B -q -DskipTests compile

      - name: 2) 单元测试
        working-directory: houduan
        run: mvn -B test

      - name: 3) 打包 jar
        working-directory: houduan
        run: mvn -B package -DskipTests

      - name: 4) 上传构建产物（push / PR / tag 均上传）
        uses: actions/upload-artifact@v4
        with:
          name: houduan-backend-jar
          path: houduan/target/*.jar
          if-no-files-found: error

      # 仅打 tag（如 git tag v1.0.0 && git push --tags）时发布 GitHub Release；
      # 普通 push 分支 / PR 到这一步直接跳过，不会创建 Release
      - name: 5) 发布 Release（仅 tag 触发）
        if: startsWith(github.ref, 'refs/tags/v')
        uses: softprops/action-gh-release@v2
        with:
          files: houduan/target/*.jar
          generate_release_notes: true
```

### 3.2 各步骤解读

**第 0 步：构建公共模块**

```yaml
- name: 0) 构建公共模块（algo-common-api → know-api / know-qrdant）
  working-directory: algo-common-api
  run: mvn -B -q install -DskipTests
```

houduan 的 pom.xml 依赖 `com.algoviz:know-api:1.0.0`（见下图代码段）。这是一个**本地 Maven 模块**，不在中央仓库，必须先 `mvn install` 到 runner 的本地仓库，houduan 才能解析依赖：

```xml
<dependency>
    <groupId>com.algoviz</groupId>
    <artifactId>know-api</artifactId>
    <version>1.0.0</version>
</dependency>
```

**第 1 步：静态检测（编译检查）**

`mvn compile` 是 Maven 的编译期检查：语法错误、注解处理、类型检查在编译阶段全部暴露。当前用编译检查兜底，注释里预留了 SpotBugs/Checkstyle 的升级方式：

```bash
mvn -B com.github.spotbugs:spotbugs-maven-plugin:4.8.6.6:check
```

**第 2 步：单元测试**

`mvn test` 运行 `src/test` 下的 JUnit 测试。当前仓库有 `MockExcelImportTest` 等测试类。

**第 3 步：打 jar 包**

`mvn package -DskipTests` 生成 `houduan/target/backend-1.0.0.jar`（Spring Boot 可执行 fat-jar）。

**第 4 步：上传构建产物**

```yaml
- uses: actions/upload-artifact@v4
  with:
    name: houduan-backend-jar
    path: houduan/target/*.jar
    if-no-files-found: error
```

每次 push/PR/tag 后，jar 会作为 artifact 上传。下载方式：

> GitHub 仓库 → Actions → 对应运行记录 → **Artifacts** → 下载 `houduan-backend-jar.zip`（需登录且有仓库读权限）。

**第 5 步：发布 Release（仅 tag）**

```yaml
- if: startsWith(github.ref, 'refs/tags/v')
  uses: softprops/action-gh-release@v2
  with:
    files: houduan/target/*.jar
    generate_release_notes: true
```

只有 push 到 `v*` 标签（如 `v1.0.0`）时才创建 Release，并把 jar 附到 Release 附件。**Release 附件公开可下载（无需登录）**，适合对外发布版本。

---

## 4. docker.yml —— 构建并推送 Docker 镜像到 ghcr.io

### 4.1 完整文件

```yaml
# ============================================================
# AlgoViz Docker 构建 & 推送（ghcr.io / GitHub Packages）
# 触发：push（分支 / v* 标签）、pull_request —— 每次提交/PR 都构建
#  - PR：只构建验证，不推送（防止 PR 污染镜像仓库）
#  - push 到 main：推送 latest + sha-xxxx
#  - push 其他分支：推送 分支名 + sha-xxxx
#  - 打 tag（v1.2.3）：推送 版本号 + latest
# 镜像：ghcr.io/<owner>/backend、ghcr.io/<owner>/nginx（用户级，避免带斜杠包名导致网页 404）
# 前置：docker/ 已在仓库内，context 根 = 仓库根
# ============================================================
name: Docker Build & Push

on:
  push:
  pull_request:

permissions:
  contents: read
  packages: write      # 推送 ghcr.io 需要；write 同时涵盖下载（read）

concurrency:
  group: docker-${{ github.ref }}
  cancel-in-progress: true

jobs:
  docker:
    name: 构建并推送 Docker 镜像
    runs-on: ubuntu-latest
    steps:
      - name: 检出代码
        uses: actions/checkout@v4

      - name: 设置 Buildx
        uses: docker/setup-buildx-action@v3

      - name: 登录 ghcr.io
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: 计算镜像标签
        id: meta
        shell: bash
        run: |
          # ghcr.io 镜像名必须全小写
          # 用用户级镜像名 ghcr.io/<owner>/<image>：不带仓库前缀。
          # 若带仓库前缀（ghcr.io/<owner>/<repo>/<image>）包名会含斜杠（如 algoviz/backend），
          # GitHub 网页/详情 API 无法解析 → 包详情页 404（registry 本身正常）
          OWNER="${{ github.repository_owner }}"; OWNER="${OWNER,,}"
          BASE="ghcr.io/${OWNER}"

          if [[ "$GITHUB_EVENT_NAME" == "pull_request" ]]; then
            # PR：只构建验证，不推送
            PUSH="false"
            BACKEND_TAGS="${BASE}/backend:pr-${{ github.event.pull_request.number }}"
            NGINX_TAGS="${BASE}/nginx:pr-${{ github.event.pull_request.number }}"
          elif [[ "$GITHUB_REF_TYPE" == "tag" ]]; then
            # 打 tag（v1.2.3）→ 版本号 + latest
            PUSH="true"
            BACKEND_TAGS="${BASE}/backend:${GITHUB_REF_NAME},${BASE}/backend:latest"
            NGINX_TAGS="${BASE}/nginx:${GITHUB_REF_NAME},${BASE}/nginx:latest"
          elif [[ "$GITHUB_REF_NAME" == "${{ github.event.repository.default_branch }}" ]]; then
            # 默认分支（main / devlops 等）→ latest + sha-xxxx
            PUSH="true"
            BACKEND_TAGS="${BASE}/backend:latest,${BASE}/backend:sha-${GITHUB_SHA::7}"
            NGINX_TAGS="${BASE}/nginx:latest,${BASE}/nginx:sha-${GITHUB_SHA::7}"
          else
            BRANCH="${GITHUB_REF_NAME//\//-}"
            PUSH="true"
            BACKEND_TAGS="${BASE}/backend:${BRANCH},${BASE}/backend:sha-${GITHUB_SHA::7}"
            NGINX_TAGS="${BASE}/nginx:${BRANCH},${BASE}/nginx:sha-${GITHUB_SHA::7}"
          fi

          echo "push=${PUSH}"             >> "$GITHUB_OUTPUT"
          echo "backend_tags=${BACKEND_TAGS}" >> "$GITHUB_OUTPUT"
          echo "nginx_tags=${NGINX_TAGS}"     >> "$GITHUB_OUTPUT"

      - name: 构建后端镜像 backend
        uses: docker/build-push-action@v6
        with:
          context: .
          file: docker/backend/Dockerfile
          push: ${{ steps.meta.outputs.push }}
          tags: ${{ steps.meta.outputs.backend_tags }}
          cache-from: type=gha
          cache-to: type=gha,mode=max

      - name: 构建 Nginx 镜像 nginx
        uses: docker/build-push-action@v6
        with:
          context: .
          file: docker/nginx/Dockerfile
          push: ${{ steps.meta.outputs.push }}
          tags: ${{ steps.meta.outputs.nginx_tags }}
          cache-from: type=gha
          cache-to: type=gha,mode=max

      # 双保险：推送后主动把包可见性设为 public（仓库已是 public，镜像默认就 public；
      # 这一步确保即使后续仓库改为 private，新包也保持 public）
      - name: 确保镜像包可见性为 public
        if: steps.meta.outputs.push == 'true'
        env:
          GH_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        run: |
          for pkg in backend nginx; do
            code=$(curl -s -o /tmp/pkg_vis.json -w "%{http_code}" -X PATCH \
              -H "Authorization: Bearer $GH_TOKEN" \
              -H "Accept: application/vnd.github+json" \
              "https://api.github.com/user/packages/container/${pkg}/visibility" \
              -d '{"visibility":"public"}')
            echo "package ${pkg}: HTTP ${code}"
            if [ "$code" != "200" ] && [ "$code" != "204" ]; then
              echo "警告: GITHUB_TOKEN 设置 ${pkg} 可见性失败（HTTP ${code}）。仓库为 public 时镜像默认即 public，可忽略。"
            fi
          done
```

### 4.2 触发与并发控制

```yaml
on:
  push:
  pull_request:

concurrency:
  group: docker-${{ github.ref }}
  cancel-in-progress: true
```

- 任何 push / PR 都触发构建（不再用 `paths:` 过滤，保证每次提交都产出镜像）。
- `concurrency`：同一分支的连续推送会取消上一次未完成的构建，避免排队堆积、省资源。

### 4.3 登录与认证

```yaml
- uses: docker/login-action@v3
  with:
    registry: ghcr.io
    username: ${{ github.actor }}
    password: ${{ secrets.GITHUB_TOKEN }}
```

登录 ghcr.io 用 `GITHUB_TOKEN`（零配置）。注意必须配合 `permissions.packages: write`，否则推送时报 `permission denied`。

### 4.4 镜像标签策略（meta 步骤）

这是最核心的一段逻辑，输出两个关键值：

1. `push`：`true`（推送到仓库）/ `false`（PR 只构建不推）
2. `backend_tags`、`nginx_tags`：逗号分隔的镜像标签列表

| 事件 | backend / nginx 标签 | 说明 |
|---|---|---|
| PR | `:pr-<编号>` | 只构建验证，`push=false` 不推送 |
| tag `v1.2.3` | `:v1.2.3` + `:latest` | 发布版本 |
| 默认分支（main/devlops） | `:latest` + `:sha-<前7位>` | 主分支随时可拉 latest |
| 其他分支 | `:分支名` + `:sha-<前7位>` | 功能分支专属标签 |

要点：

- **ghcr.io 镜像名必须全小写**，所以 `OWNER="${OWNER,,}"`。
- **使用用户级镜像名** `ghcr.io/<owner>/<image>`，而不是 `ghcr.io/<owner>/<repo>/<image>`。后者会让 GitHub 把包注册成带斜杠的包名（`algoviz/backend`），导致网页/详情 API 无法解析（详见第 9 节问题 5）。
- **`latest` 只在默认分支打**：用 `github.event.repository.default_branch` 判断，兼容 `main`、`devlops` 等任意默认分支名。早期写死 `main`，导致默认分支为 devlops 的仓库永远没有 `latest`。

### 4.5 构建并推送

```yaml
- uses: docker/build-push-action@v6
  with:
    context: .            # 仓库根为构建上下文
    file: docker/backend/Dockerfile
    push: ${{ steps.meta.outputs.push }}
    tags: ${{ steps.meta.outputs.backend_tags }}
    cache-from: type=gha
    cache-to: type=gha,mode=max
```

- `context: .`：context 根必须是**仓库根**（AlgoVize/），Dockerfile 里的 COPY 路径都相对它。
- `cache-from/to: type=gha`：镜像层缓存复用，后续构建大幅提速。

### 4.6 确保包可见性 public

```yaml
- if: steps.meta.outputs.push == 'true'
  run: |
    for pkg in backend nginx; do
      code=$(curl ... -X PATCH "https://api.github.com/user/packages/container/${pkg}/visibility" -d '{"visibility":"public"}')
      ...
    done
```

推送成功后调用 GitHub REST API 把包可见性主动设为 `public`。**仓库本身是 public 时镜像默认就 public**，这一步是"双保险"：即使以后仓库转 private，新包也保持 public。

> 注意：包名必须是**简单名**（`backend`、`nginx`），不能是带斜杠的 `algoviz/backend`，否则 REST API 的 `%2F` 编码解析会 404。

---

## 5. Dockerfile 容器化适配

### 5.1 backend/Dockerfile（多阶段 Maven 构建）

```dockerfile
# ====== 阶段1: Maven 构建 ======
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

# 0) 先安装公共模块 algo-common-api（houduan 依赖 know-api，必须先 install 进本地 Maven 仓库）
COPY algo-common-api/ ./algo-common-api/
RUN mvn -B -q -f algo-common-api/pom.xml install -DskipTests

# 1) 先拷 pom，利用层缓存加速依赖下载
COPY houduan/pom.xml ./
RUN mvn dependency:go-offline -B

# 2) 拷源码
COPY houduan/src ./src

# 容器化适配：
# 1) 删除 jar 内 application.yml（含明文密码/微信密钥，改由外部挂载注入）
# 2) 把 logback 写死的 Windows 路径 D:/rizi 替换为 Linux 容器路径 /app/logs
RUN rm -f src/main/resources/application.yml \
 && sed -i 's|D:/rizi|/app/logs|g' src/main/resources/logback-spring.xml

RUN mvn clean package -DskipTests -B

# ====== 阶段2: JRE 运行 ======
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /build/target/backend-1.0.0.jar app.jar
RUN mkdir -p /app/uploads /app/logs

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar", \
  "--spring.config.additional-location=file:/app/config/", \
  "--spring.profiles.active=docker"]
```

关键点：

- **阶段 0 必须先 `install` algo-common-api**：houduan 依赖 know-api 本地模块，容器里没有它，直接 `mvn clean package` 会因找不到依赖而失败。
- **COPY 路径去掉 `AlgoVize/` 前缀**：GitHub 仓库根就是 AlgoVize/，context 根 = 仓库根，路径直接写 `houduan/...`。
- **删除 `application.yml`**：明文密码/微信密钥不打包进 jar，改由外部挂载 `/app/config/` 注入。
- **`sed` 替换 Windows 日志路径**：`D:/rizi` → `/app/logs`，适配 Linux 容器。

### 5.2 nginx/Dockerfile（前端构建 + 静态托管）

```dockerfile
# ====== 阶段1: 构建后台 Vue3 管理界面 ======
# houtai 使用 Vite 8 / TS 6，要求 Node >= 20.19；node:18 无法构建
FROM node:22-alpine AS houtai-builder
WORKDIR /build

RUN npm config set registry https://registry.npmmirror.com

COPY houtai/package.json houtai/package-lock.json* ./
RUN npm install

COPY houtai/ .
# 【临时】跳过 vue-tsc 类型检查，仅 vite build（esbuild 转译，产物与类型检查无关）
# 原因：Docker 全新环境无 lock 时 npm install 装最新依赖，触发存量 TS 类型错误；
#       镜像构建验证通过后，提交 package-lock.json 固定版本，再恢复 npm run build
RUN npx vite build
# 产物：/build/dist

# ====== 阶段2: Nginx 运行（托管前台 + 后台 dist + SSL 终端 + /api 反代） ======
FROM nginx:1.25-alpine

RUN rm -f /etc/nginx/conf.d/default.conf
COPY docker/nginx/nginx.conf /etc/nginx/conf.d/dsaol.conf
COPY qianduan/ /usr/share/nginx/html/
COPY --from=houtai-builder /build/dist/ /usr/share/nginx/admin/

EXPOSE 80 443
```

关键点：

- **Node 版本必须是 22**：Vite 8 要求 Node ≥ 20.19，node:18 会直接构建失败。
- **当前临时跳过 `vue-tsc`**：`npm run build` = `vue-tsc -b && vite build`。无 lock 文件时 Docker 装了最新依赖，触发存量 TS 类型错误。临时改用 `npx vite build`（只转译不检查类型）。根治方案是提交 `package-lock.json`（见问题 4）。

### 5.3 配套文件

**仓库根 `.dockerignore`**（构建上下文排除，防止把 node_modules/target 发到 daemon）：

```dockerignore
.git/
.github/
.idea/
.vscode/
**/node_modules/
**/target/
**/dist/
**/.vite/
**/*.log
Agent/
bin/
doc/
docker/.env
docker/.env.example
docker/ssl/
```

**`.gitignore` 白名单**（`*.yml`、`*.json` 规则会误吞关键文件，必须反向排除）：

```gitignore
!.github/workflows/build.yml
!.github/workflows/docker.yml
!docker/docker-compose.yml
# houtai 的 lock 必须提交：Docker 构建 npm install 需要锁文件保证可复现构建
!houtai/package-lock.json
```

> ⚠️ 注意 gitignore 的 **last-match-wins**：`!houtai/package-lock.json` 必须放在 `package-lock.json` 忽略规则**之后**才生效。

---

## 6. 打 tag 与 GitHub Release

### 6.1 本地打 tag 命令

```bash
git tag v1.0.0
git push origin v1.0.0      # tag push 触发 build.yml + docker.yml
```

或者带说明的标签：

```bash
git tag -a v1.0.0 -m "release v1.0.0"
git push origin v1.0.0
```

### 6.2 tag push 后两个工作流各自做什么

| 工作流 | tag 时行为 |
|---|---|
| build.yml | 走完 CI，最后一步 `softprops/action-gh-release` 创建 **GitHub Release** 并附上 jar |
| docker.yml | 推送 `:v1.0.0` + `:latest` 两个镜像 tag |

### 6.3 触发条件写法

```yaml
# build.yml 中 Release 只在 tag 时执行
- if: startsWith(github.ref, 'refs/tags/v')
```

```yaml
# docker.yml 中根据 ref_type 区分 tag/分支
elif [[ "$GITHUB_REF_TYPE" == "tag" ]]; then
  BACKEND_TAGS="${BASE}/backend:${GITHUB_REF_NAME},${BASE}/backend:latest"
```

---

## 7. CodeQL 代码安全扫描

### 7.1 两种配置方式

**方式一：默认设置（Default setup）——本项目采用**

不需要编写任何 workflow 文件。在仓库：

> Settings → Code security and analysis → CodeQL → **Set up** → Default

GitHub 会自动生成 `github/codeql-action` 的运行，在每次 push 和 PR 上扫描仓库语言的漏洞（对 Java/TypeScript/JavaScript 均支持），并把告警展示在 Security → Code scanning alerts 页面。这也是本项目 `gh run list` 中能看到 `CodeQL` 运行记录的原因。

**方式二：高级设置（Advanced setup）——自定义 codeql.yml**

如需自定义扫描语言、触发条件、配置密度，可创建 `.github/workflows/codeql.yml`：

```yaml
name: CodeQL

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]
  schedule:
    - cron: '0 0 * * 0'   # 每周日扫描一次

jobs:
  analyze:
    name: Analyze
    runs-on: ubuntu-latest
    permissions:
      security-events: write
      contents: read
    strategy:
      fail-fast: false
      matrix:
        language: ['java', 'javascript-typescript']
    steps:
      - name: 检出代码
        uses: actions/checkout@v4

      - name: 初始化 CodeQL
        uses: github/codeql-action/init@v3
        with:
          languages: ${{ matrix.language }}

      - name: 自动构建
        uses: github/codeql-action/autobuild@v3

      - name: 执行 CodeQL 分析
        uses: github/codeql-action/analyze@v3
```

### 7.2 注意点

- CodeQL 需要 `security-events: write` 权限才能写告警。
- Java 项目建议在 `init` 前先安装 JDK（`actions/setup-java@v4`），否则 autobuild 可能失败。
- 告警可在 **Security → Code scanning** 页面查看、标记误报、指派处理。

---

## 8. 用 curl 验证镜像是否 public 且存在

ghcr.io 采用 Docker Registry HTTP API V2 + Bearer Token 认证，**直接访问 manifest 接口一定会先返回 401**（要求换 token），因此判断 public/private 的正确方法如下。

### 8.1 第一步：匿名换取 pull token（判断是否 public）

```cmd
curl "https://ghcr.io/token?service=ghcr.io&scope=repository:zhuxiaoyi412826/backend:pull"
```

- 返回 `{"token":"djE6..."}` → **PUBLIC**（可免登录拉取）
- 返回 `401` / 错误 → **PRIVATE**（需要登录）

> 这是**判断公私的唯一可靠方法**。token 交换成功 = 镜像允许匿名读取 = public。

### 8.2 第二步：用 token 拉取 manifest（确认存在）

```cmd
curl -H "Authorization: Bearer 这里贴token" -H "Accept: application/vnd.oci.image.index.v1+json, application/vnd.docker.distribution.manifest.list.v2+json" https://ghcr.io/v2/zhuxiaoyi412826/backend/manifests/devlops
```

判读：

| 返回 | 含义 |
|---|---|
| `HTTP 200` + JSON | 镜像存在且 public 可拉 |
| `UNAUTHORIZED` | token 无效或镜像私有 |
| `MANIFEST_UNKNOWN` | **tag 不存在**（不是私有！换 tag 试试） |

> ⚠️ 关键坑：本项目镜像是**多架构 OCI Index**，curl 的 `Accept` 头**必须包含** `application/vnd.oci.image.index.v1+json` 或 `application/vnd.docker.distribution.manifest.list.v2+json`，否则即使 token 正确也返回 `MANIFEST_UNKNOWN: OCI index found, but Accept header does not support OCI indexes`。`docker pull` 不受影响（docker 客户端会自动带上正确的 Accept）。

### 8.3 查看全部 tag

```cmd
curl -H "Authorization: Bearer <token>" https://ghcr.io/v2/zhuxiaoyi412826/backend/tags/list
```

### 8.4 完整 PowerShell 验证脚本

```powershell
$tok = (curl.exe -s "https://ghcr.io/token?service=ghcr.io&scope=repository:zhuxiaoyi412826/backend:pull" | ConvertFrom-Json).token
if ($tok) { "PUBLIC" } else { "PRIVATE" }
curl.exe -s -o NUL -w "manifest HTTP:%{http_code}`n" `
  -H "Authorization: Bearer $tok" `
  -H "Accept: application/vnd.oci.image.index.v1+json, application/vnd.docker.distribution.manifest.list.v2+json" `
  "https://ghcr.io/v2/zhuxiaoyi412826/backend/manifests/devlops"
```

---

## 9. 实战中遇到的问题与解决方案

### 问题 1：build.yml 上传了但 Actions 不构建

**现象**：上传 workflow 后 Actions 页面没有任何运行。

**根因**：`.gitignore` 第 21 行 `*.yml` 会把 `.github/workflows/*.yml` 全部忽略，workflow 文件根本没提交上去。

**解决**：gitignore 白名单反向排除：

```gitignore
!.github/workflows/build.yml
!.github/workflows/docker.yml
```

### 问题 2：Actions 报"没有下载权限" / 403

**现象**：涉及 ghcr.io 的操作报 `403 Resource not accessible by integration`。

**根因**：workflow 写了 `permissions:` 块后，未列出的权限全部为 `none`。早期只声明了 `contents: write`，`packages` 缺失。

**解决**：

```yaml
permissions:
  contents: write
  packages: write
```

### 问题 3：Docker 构建 nginx 镜像时 `vue-tsc` 报大量 TS 错误

**现象**：`RUN npm run build`（=`vue-tsc -b && vite build`）报 `headerIds` 不存在、`AxiosResponse` 无属性、element-plus 组件 props 类型不符等上百个错误。

**根因**：`.gitignore` 忽略了 `package-lock.json` → Docker 全新环境 `npm install` 装了**最新版依赖**（marked 18 移除 `headerIds`、axios/element-plus 新版类型更严）。本地没报是因为 `vue-tsc -b` 的增量缓存（`.tsbuildinfo`）掩盖了存量错误。

**解决（临时）**：Dockerfile 里跳过类型检查：

```dockerfile
RUN npx vite build
```

**解决（根治）**：提交 `package-lock.json` 固定依赖版本：

```gitignore
!houtai/package-lock.json   # 必须放在 package-lock.json 忽略规则之后
```

### 问题 4：Dockerfile COPY 路径报错、找不到模块

**现象**：Docker 构建失败：`COPY failed: file not found` 或 `Could not resolve dependencies for com.algoviz:know-api`。

**根因（双因素）**：

1. `docker/` 目录在 git 仓库外，COPY 路径带 `AlgoVize/` 前缀（按旧 context 根写的），GitHub 上 checkout 没有这些文件。
2. backend Dockerfile 没预构建 `algo-common-api`，houduan 依赖的 know-api 本地模块不存在。

**解决**：docker/ 移入仓库根；COPY 路径去掉 `AlgoVize/` 前缀；backend Dockerfile 增加阶段 0：

```dockerfile
COPY algo-common-api/ ./algo-common-api/
RUN mvn -B -q -f algo-common-api/pom.xml install -DskipTests
```

### 问题 5：镜像包网页点击 404

**现象**：Packages 页面点开 `algoviz/backend` 显示 404；REST API 详情、PATCH 可见性也 404；但 `docker pull` 正常。

**根因**：镜像名用了**带仓库前缀**的 `ghcr.io/<owner>/<repo>/<image>`，GitHub 把它注册成**带斜杠的包名**（`algoviz/backend`）。GitHub 网页和详情 API 无法正确解析带斜杠的包名，只有 registry 数据层正常。

**解决**：改用**用户级镜像名** `ghcr.io/<owner>/<image>`（去掉仓库前缀），包名变为简单名 `backend`，网页/API/拉取全部正常。

```yaml
BASE="ghcr.io/${OWNER}"   # 而非 ghcr.io/${OWNER}/${REPO}
```

### 问题 6：拉不到 `:latest`，一直 `MANIFEST_UNKNOWN`

**现象**：`docker pull ...:latest` 报 `MANIFEST_UNKNOWN`。

**根因（双因素）**：

1. 默认分支叫 `devlops` 而非 `main`，workflow 写死 `== "main"` 才打 latest。
2. 还有一部分"404"是 curl 的 Accept 头没带 OCI Index 类型（见第 8 节）。

**解决**：用默认分支判断替换写死的 main：

```yaml
elif [[ "$GITHUB_REF_NAME" == "${{ github.event.repository.default_branch }}" ]]; then
```

### 问题 7：`MYSQL_ROOT_PASSWORD` 缺失导致 MySQL 起不来

**现象**：compose 中 `MYSQL_ROOT_PASSWORD` 被 .env.example 删除，MySQL healthcheck 永远失败，backend 等不到 healthy。

**解决**：补回 `.env.example`，并让 backend 用 root 远程连（compose 加 `MYSQL_ROOT_HOST: '%'`）：

```yaml
MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}
MYSQL_USER=root
MYSQL_PASSWORD=${MYSQL_ROOT_PASSWORD}
```

```yaml
environment:
  MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
  MYSQL_ROOT_HOST: '%'
```

### 问题 8：compose 挂载路径在新仓库布局下失效

**现象**：`../AlgoVize/houduan/sql/algovize.sql` 在新布局（docker/ 在仓库内）解析到 `AlgoVize/AlgoVize/...`。

**解决**：修正为相对 compose 目录的正确路径：

```yaml
- ../houduan/src/main/resources/db/algovize.sql:/docker-entrypoint-initdb.d/algovize.sql:ro
```

### 问题 9：`.env.example` 含明文密钥

**现象**：`.env.example` 曾包含真实 MySQL 密码、微信 AppSecret、支付密钥，且不被 gitignore 拦截，push 后即公开。

**解决**：全部改为环境变量占位符：

```ini
MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}
WECHAT_APPSECRET=${WECHAT_APPSECRET}
DEEPSEEK_API_KEY=${DEEPSEEK_API_KEY}
```

---

## 10. 不足与扩展方向

### 10.1 当前不足

| 不足 | 说明 |
|---|---|
| 前端跳过类型检查 | nginx 镜像临时用 `npx vite build`，`vue-tsc` 类型错误未治理；提交 lock 后应恢复并修复存量 TS 错误 |
| 支付证书不在镜像 | `cert/*.pem` 被 `.gitignore` 忽略，ghcr 镜像**不含微信支付证书**（支付功能在镜像内不可用），仅本地构建有 |
| 镜像体积偏大 | houtai 构建产物中 `echarts` 等单 chunk 达 2.5MB+，且未做 gzip/压缩优化 |
| 可见性设 public 用 GITHUB_TOKEN | 依赖仓库 public 才有效；若仓库转 private，GITHUB_TOKEN 可能无权 PATCH，需换 PAT |
| 无镜像安全扫描 | 未接入 Trivy / Grype 镜像漏洞扫描 |
| 双 job 重复拉依赖 | build.yml 与 docker.yml 各自跑一遍 Maven/npm 依赖下载，可合并或复用缓存 |

### 10.2 扩展方向

1. **恢复 vue-tsc 并治理类型错误**：提交 lock 后，在本地 `npm install && npm run build` 验证；若仍报错，逐文件修复（多为 `res.xxx` 应为 `res.data.xxx`、props 类型收窄），恢复 `npm run build`。
2. **支付证书进镜像**：证书 base64 存 GitHub Secrets，Dockerfile 用 `RUN --mount=type=secret` 注入，避免密钥进层；或运行时挂载并改配置为 `file:` 路径。
3. **镜像安全扫描**：

   ```yaml
   - name: Trivy 镜像漏洞扫描
     uses: aquasecurity/trivy-action@0.28.0
     with:
       image-ref: ${{ steps.meta.outputs.backend_tags }}
       format: 'sarif'
       output: 'trivy-results.sarif'
   ```

4. **多架构推送**：`build-push-action` 加 `platforms: linux/amd64,linux/arm64`，配合 `docker/setup-qemu-action`，服务器是 ARM 也能拉。
5. **PAT 强设可见性**：创建 `write:packages` 的 PAT 存 `GHCR_TOKEN` secret，把可见性步骤的 token 换成它，仓库转 private 也能强制 public。
6. **自动部署（CD）**：push 到 main 后通过 SSH 登录服务器执行 `docker compose pull && docker compose up -d`，或接入 webhook 到现有部署脚本。
7. **Release 镜像一起发**：tag 时在 Release 描述里附上 ghcr 镜像地址，方便使用者直接 pull。
8. **npm 锁文件一致性**：提交 lock 后，Dockerfile 用 `npm ci`（严格按 lock 安装）替代 `npm install`，保证构建完全可复现。
9. **CI 成本优化**：给 docker.yml 加回 `paths:` 过滤（只有 docker/houduan/houtai 等变化才构建），或把两个 workflow 合并、复用 `actions/setup-java` 的 Maven 缓存。

---

## 11. 总结

通过两套 GitHub Actions 工作流，AlgoViz 实现了完整的自动化链路：

- **build.yml**：每次提交自动完成 公共模块构建 → 编译检查 → 单元测试 → 打 jar → 上传产物，打 `v*` 标签时自动发布 GitHub Release。
- **docker.yml**：每次提交/PR 构建 `backend`、`nginx` 两个镜像，按 PR/分支/tag/默认分支计算标签，推送到 ghcr.io，并保持包可见性 public。
- **CodeQL**：GitHub 默认设置自动做代码安全扫描，无需维护 workflow 文件。
- **验证手段**：用 ghcr.io 的 token 端点判断镜像公私，用带 OCI Index Accept 头的 manifest 请求确认镜像存在。

过程中踩过的坑（gitignore 吞 workflow、packages 权限缺失、无 lock 依赖漂移、context 路径、带斜杠包名 404、OCI Index Accept、latest 依赖默认分支等）都已在第 9 节记录，并给出了对应的修复代码。

下一步建议：优先提交 `package-lock.json` 恢复 vue-tsc、决策支付证书进镜像的方案、接入 Trivy 安全扫描，并在服务器上用 `docker compose` 实际拉起这套镜像完成端到端验证。

---

*本文档基于 2026-08-30 仓库实际配置整理。*
