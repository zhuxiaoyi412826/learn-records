# GIT+GitHubFlow + Actions + CD 集成流水线

> **一份文档打通全栈项目从「写代码」到「写代码」到「上线」的完整工程链路**：Git → GitFlow 分支策略 → GitHub Actions 自动化流水线 → CD 部署与质量保障。
>
> - **适用场景**：前后端同仓 / 单体全栈项目、有 PR、有 Release、社区可贡献；兼顾个人维护 + 外部开发者协作,不搞企业重度流程
> - **版本规范**：严格 SemVer 语义化版本 `Major.Minor.Patch`(Major 不兼容破坏性变更 / Minor 新增功能向下兼容 / Patch Bug 修复与小优化)
> - **官方文档**:[GitHub Docs](https://docs.github.com/en)
>
> 本文档共九篇,可按顺序通读,也可按目录直达所需章节:

## 目录

- [〇、全景总览:一条主线看懂全部](#〇全景总览一条主线看懂全部)
- [第一篇 GitFlow 分支策略与协作规范](#第一篇-gitflow-分支策略与协作规范)
- [第二篇 版本发布管理(SemVer + Release + Hotfix)](#第二篇-版本发布管理semver--release--hotfix)
- [第三篇 GitHub Actions 基础](#第三篇-github-actions-基础)
- [第四篇 四条流水线详解(CI / E2E / Release / Deploy)](#第四篇-四条流水线详解ci--e2e--release--deploy)
- [第五篇 CD 部署方案](#第五篇-cd-部署方案)
- [第六篇 Dockerfile 最佳实践](#第六篇-dockerfile-最佳实践)
- [第七篇 质量保障与运维](#第七篇-质量保障与运维)
- [第八篇 工具链:gh CLI](#第八篇-工具链gh-cli)
- [第九篇 落地路径与学习路线](#第九篇-落地路径与学习路线)

---

# 〇、全景总览:一条主线看懂全部

把整个体系想象成「**三道门 + 一次发布 + 一次上线 + 一条应急通道**」,分支模型是骨架,流水线是自动化的肌肉:

```
开发者: 从 develop 拉 feat/xxx 分支
    │ 本地自测(lint/编译/单测/手动验证) → commit → push
    ▼
┌─────────────┐
│ ① CI 流水线  │  ← PR 自动跑:lint + 构建 + 单元测试
│  (ci.yml)   │     不通过 → PR 标红,不能合并
└──────┬──────┘
       │ CI ✅ + 代码审查(CR) ✅ → 合并到 develop,删除特性分支
       ▼
┌─────────────┐
│ ② E2E 流水线 │  ← 合并 develop 自动跑:Playwright 端到端测试
│  (e2e.yml)  │
└──────┬──────┘
       │ 通过 → 功能在 develop 积攒,直到准备发版
       ▼
   从 develop 拉 release/v1.2.0
   (只改版本号 + CHANGELOG,禁止新增功能)
    │ PR 合并到 main + 同步合回 develop
    ▼
   在 main 打 tag:git tag v1.2.0 && git push --tags
       ▼
┌─────────────┐
│ ③ Release   │  ← 打 tag 自动跑:构建 Docker 镜像 + 推送镜像仓库
│  流水线     │     + 生成 GitHub Release + 部署到测试环境(staging)
│(release.yml)│
└──────┬──────┘
       ▼
   手动触发生产部署(配置审批,需 1-2 人 Approve)
       ▼
┌─────────────┐
│ ④ Deploy    │  ← SSH 到服务器:拉镜像 → 先更后端 → 健康检查
│  流水线     │     → 再更前端 → 清理旧镜像
│ (deploy.yml)│
└──────┬──────┘
       │ 线上出紧急故障?
       ▼
   ⑤ 应急通道:从 main 拉 hotfix/xxx → 修复 → PR 合 main
      → 打 Patch tag v1.2.1 → 同步合回 develop(必须双向!)
```

**四条流水线的角色分工**:

| 流水线 | 触发时机 | 一句话职责 |
| :--- | :--- | :--- |
| **CI** | 每次提 PR | 守门员,保证代码质量;不通过 PR 不能合并 |
| **E2E** | 合并到 `develop` 后 | 集成验证,跑完整端到端流程 |
| **Release** | 打 `v*.*.*` tag | 打包发布,出镜像 + GitHub Release + 上 staging |
| **Deploy** | 手动触发 | 上线生产,部署到生产服务器 |

---

# 第一篇 GitFlow 分支策略与协作规范

## 1.1 常驻长期分支(永久存在)

| 分支名 | 作用 | 准入规则 |
| :--- | :--- | :--- |
| `main` | 生产稳定版,对应已发布 Release 标签,**随时可部署上线** | ❌ 禁止直接 push;只能通过 PR 合并,合并前必须自测 / CI 通过 |
| `develop` | 开发集成分支,存放下个版本待发布特性,全栈所有新功能汇总 | 所有功能分支最终合入这里;不直接上线 |

> 如果是**极简轻量开源、没有复杂版本规划**,可以直接砍掉 `develop`,只用 `main + 短期特性分支`(Trunk 模式)。

## 1.2 短期临时分支(开发完成合并后删除)

统一前缀 `类型/issue编号-简短描述`,有 issue 优先带上 issue 号,开源协作更清晰:

| 分支前缀 | 用途 | 来源分支 | 合并目标分支 |
| :--- | :--- | :--- | :--- |
| `feat/` | 新功能(前端页面、后端接口、新组件等) | `develop` | `develop` |
| `fix/` | 普通 bug 修复 | `develop` | `develop` |
| `hotfix/` | `main` 线上紧急故障 | `main` | `main` + `develop`(两边同步修复) |
| `refactor/` | 重构(无业务逻辑变更,前后端代码整理) | `develop` | `develop` |
| `docs/` | 文档、README、注释、官网文案修改 | `develop` | `develop` |
| `style/` | 纯格式调整(eslint/prettier,不改动逻辑) | `develop` | `develop` |
| `test/` | 单元测试 / e2e 测试补充 | `develop` | `develop` |
| `release/v*.*.*` | 版本预发布分支,只做版本号、changelog、最终测试 | `develop` | `main` + `develop` |

**分支命名示例**:

```
feat/#42-user-login-page
fix/#58-api-timeout
hotfix/#62-main-cors-error
refactor/backend-controller-split
docs/update-install-guide
release/v1.2.0
```

## 1.3 四大核心场景流转流程

### 场景一:新功能开发(标准流程)

1. 在本地 `feat/xxx` 分支开发代码(前端 + 后端)
2. ✅【本地自测】:本地跑 lint、编译、单元测试、手动验证功能
   → 确认:代码能编译、单元测试通过、功能跑通,没有明显 bug
3. 提交本地 commit,push 到远程
4. 创建 PR,目标分支:`develop`
5. 🤖 CI 流水线自动执行:前端构建、后端单元测试、静态检查
   → CI 是**二次校验兜底**,防止本地环境漏测、环境差异、提交漏文件
6. 🧑‍💻 代码审查(CR):看代码逻辑、规范、设计
7. 全部 CI 通过 + CR 通过 → 合并 PR 到 `develop`
8. 删除临时 feat 分支(本地 + 远程)

### 场景二:普通 bug 修复

从 `develop` 拉 `fix/xxx` → 修复 → PR 合 `develop`

### 场景三:版本发布

从 `develop` 拉出 `release/v1.2.0` → 只改版本号、更新 CHANGELOG、全栈联调测试(**禁止新增功能**)→ 测试无误后 PR 合并到 `main`,同时合并回 `develop` → 在 `main` 打 tag `v1.2.0` 生成 Release(详见第二篇)

### 场景四:线上紧急修复

从 `main` 拉 `hotfix/xxx` → 修复 → PR 合 `main`(打 Patch 版本 tag `v1.2.1`),**同时同步合并回 `develop`**(详见第二篇)

## 1.4 Commit 提交规范(Conventional Commits)

格式:`类型(模块): 描述`。模块可选:`frontend` / `backend` / `docs` / `common` / `db`,区分全栈不同部分:

```
feat(frontend): 新增用户登录表单
fix(backend): 修复分页接口越界问题
refactor(common): 封装请求工具类
docs: 更新部署文档
chore: 调整eslint配置
test(backend): 新增用户service单元测试
```

> `chore`:构建、工程配置、依赖更新,无业务代码改动。

## 1.5 PR 规范与合并策略

### PR 的价值

1. 触发 CI 流水线自动校验
2. 留下变更记录(哪个功能、什么时候合并到 develop)
3. 可以在 PR 写备注、记录修改点
4. 以后多人协作可以直接复用这套流程

PR 标题遵循 commit 规范,模板包含:变更内容(前端改动 / 后端改动 / 数据库变更)、自测清单、是否有破坏性改动、关联 issue 编号 `Closes #42`(完整模板见 1.7 节)。

### 网页上合并 PR(GitHub)

1. 网页看到 CI 全部绿色通过
2. 自己做 CR,看 diff 变更
3. 点「Merge pull request」,选择合并策略(merge / squash / rebase)
4. GitHub 自动把 feat 分支的提交合并到远程 `develop`
5. 可以选择**删除远程的 feat 分支**

> ⚠️ 网页看代码 diff 的缺点:没有 IDE 的语法高亮、跳转、智能提示,**阅读大段代码体验很差**。

### 个人项目怎么选:两种方案

**方案 A:走 GitHub PR 流程(推荐,保留规范)**

1. feat 分支开发完成,本地自测全部通过
2. push 到远程 `feat/xxx`
3. GitHub 网页创建 PR 到 `develop`
4. ⚠️ 代码审查不要在网页硬看:
   - 把 PR 的代码拉到本地 IDE:`git fetch`,在本地 IDE 打开这个 feat 分支,**在编辑器里做 CR 审查**
   - 发现问题:本地修改,commit,push 回远程 feat 分支,PR 会自动更新
5. 本地 CR 确认没问题,CI 流水线全部绿色 ✅
6. 回到 GitHub 网页,执行**网页合并 PR**
7. 合并完成,删除远程 feat 分支;本地删除本地 feat 分支

> ✨ 关键点:**CR 审查可以在本地 IDE 做,不一定要在网页看 diff;只是合并动作在 GitHub 网页完成**。网页只做:看 CI 状态、确认合并、保留 PR 记录。

**方案 B:完全本地合并,不使用 GitHub PR(极简个人模式)**

```bash
# 本地feat写完,自测完成
git checkout develop
git merge feat/xxx
# 本地IDE再审查一遍合并后的代码
git push origin develop
# 删除本地feat分支
git branch -d feat/xxx
```

✅ 优点:完全 IDE 操作,体验最好。
❌ 缺点:**没有 PR 记录,没有 CI 自动校验;需要自己手动跑一遍 CI 检查,否则坏代码会直接进 develop**。
> 如果想保留 CI 兜底:本地合并完成后 push 到 develop,CI 会自动跑(push 触发);只是没有 PR 这个载体。

### 一个重要的误区

> ❌ 误区:做 CR 就必须在 GitHub 网页看代码。
> ✅ 真相:CR 是**审查代码变更**,工具可以是本地 IDE,网页只是载体。

## 1.6 代码审查(CR)Checklist

**通用**

- 代码逻辑是否正确,有没有明显 bug
- 命名是否清晰,函数/变量名能表达意图
- 有没有重复代码,可以抽取复用
- 注释是否必要且不过度
- 是否有破坏性改动(接口变更、字段删除等)

**前端专项**

- 组件拆分是否合理,props 设计是否清晰
- 状态管理是否正确,有无不必要的全局状态
- 错误边界 / 加载态 / 空态是否处理
- 列表是否加了 key,有无性能隐患
- TypeScript 类型是否完整,禁用了 any

**后端专项**

- 接口入参校验是否完整
- SQL 是否有注入风险,是否走索引
- 事务边界是否正确
- 异常处理和错误码是否统一
- 外部依赖调用是否有超时和重试

**安全**

- 是否有敏感信息硬编码(密钥、密码)
- 权限校验是否到位(越权风险)
- 用户输入是否做了过滤/转义

## 1.7 Issue 与 PR 模板

模板文件放在 `.github/ISSUE_TEMPLATE/` 与 `.github/PULL_REQUEST_TEMPLATE.md`。

**Bug Issue 模板**:

```markdown
**Bug 描述**
清晰描述 bug 是什么

**复现步骤**
1. 打开 '...'
2. 点击 '...'
3. 滚动到 '...'
4. 出现错误

**预期行为**
应该发生什么

**实际行为**
实际发生了什么

**环境信息**
- 操作系统:
- 浏览器版本:
- 后端版本:

**截图 / 日志**
```

**PR 模板**:

```markdown
## 变更内容

<!-- 描述这次 PR 做了什么 -->

- 前端改动:
- 后端改动:
- 数据库变更:

## 关联 Issue

Closes #XX

## 自测清单

- [ ] 本地 lint 通过
- [ ] 本地构建通过
- [ ] 单元测试通过
- [ ] 手动测试了核心流程
- [ ] 验证了边界情况(空输入、异常等)
- [ ] 数据库迁移脚本验证过

## 破坏性改动

- [ ] 有(请描述影响范围和迁移方案)
- [ ] 没有

## 截图 / 录屏

(前端改动请附上效果图)
```

## 1.8 全栈同仓项目目录结构

```
repo-root/
├── frontend/              # 前端项目(独立 package.json)
│   ├── src/
│   ├── package.json
│   └── vite.config.ts
├── backend/               # 后端项目(独立构建配置)
│   ├── src/
│   ├── pom.xml / go.mod / requirements.txt
│   └── db/migration/      # 数据库迁移脚本
├── docs/                  # 项目文档(部署、API、架构图)
│   ├── README.md
│   ├── api/
│   └── architecture/
├── scripts/               # 公共脚本(构建、部署、CI 辅助)
├── .github/               # GitHub 配置
│   ├── workflows/         # CI/CD 流水线
│   │   ├── ci.yml         # PR 触发:lint + 构建 + 单元测试
│   │   ├── e2e.yml        # 合并 develop 触发:E2E 测试
│   │   ├── release.yml    # 打 tag 触发:构建镜像 + 发布 Release
│   │   └── deploy.yml     # 手动触发:部署到生产
│   ├── ISSUE_TEMPLATE/    # Issue 模板
│   └── PULL_REQUEST_TEMPLATE.md
├── docker-compose.yml     # 本地一键启动全环境
└── README.md
```

- 前后端代码物理隔离,各自独立构建、独立 lint
- 共享文档、脚本、CI 配置,避免重复
- 一个 PR 可以同时包含前后端改动 + 文档更新,review 上下文完整

## 1.9 开源贡献者指南(外部开发者)

建议在 `CONTRIBUTING.md` 中写明:

1. **Fork & Clone**:fork 到自己账号,clone 到本地
2. **设置上游**:`git remote add upstream <原仓库地址>`
3. **同步最新代码**:`git fetch upstream && git rebase upstream/develop`
4. **从 develop 拉特性分支**:命名遵循项目规范
5. **本地自测**:跑 lint、测试、手动验证
6. **提 PR**:目标分支选 `develop`,填写 PR 模板
7. **响应 CR**:根据 review 意见修改,push 到同一分支

---

# 第二篇 版本发布管理(SemVer + Release + Hotfix)

## 2.1 发布前检查清单

在从 `develop` 拉出 `release/vX.Y.Z` 之前,确认:

- 所有计划内功能的 PR 都已合入 `develop`
- `develop` 分支 CI 全绿
- CHANGELOG 草稿已准备(按 feat / fix / refactor / docs 分类)
- 数据库迁移脚本已验证可正向执行 + 回滚
- 核心功能手动冒烟测试通过

## 2.2 Release 发布操作手册

```bash
# 1. 从 develop 拉出 release 分支
git checkout develop
git pull origin develop
git checkout -b release/v1.2.0

# 2. 更新版本号
#   - 前端:package.json version 字段
#   - 后端:pom.xml / version.go / __version__.py
#   - 文档:CHANGELOG.md 填入本次版本内容和日期

# 3. 提交版本号变更
git add .
git commit -m "chore: bump version to v1.2.0"

# 4. 推送并创建 PR 到 main(标题:release: v1.2.0)
git push origin release/v1.2.0
```

## 2.3 发布后收尾

PR 合并到 `main` 后:

```bash
# 1. 打 tag
git checkout main
git pull origin main
git tag -a v1.2.0 -m "Release v1.2.0"
git push origin v1.2.0

# 2. 合并回 develop(避免 develop 落后于 main)
git checkout develop
git merge main
git push origin develop

# 3. 删除 release 分支
git branch -d release/v1.2.0
git push origin --delete release/v1.2.0
```

> 打完 tag 并推送后,`release.yml` 流水线会自动接管:构建镜像 → 推镜像仓库 → 生成 GitHub Release → 部署 staging(见第四篇)。

## 2.4 Hotfix 紧急修复手册

```bash
# 1. 从 main 拉出 hotfix 分支
git checkout main
git pull origin main
git checkout -b hotfix/#62-payment-timeout

# 2. 修复 bug,本地自测
# ... 改代码、跑测试 ...

# 3. 提交
git add .
git commit -m "fix(backend): 修复支付回调超时问题"

# 4. 推送并提 PR 到 main
git push origin hotfix/#62-payment-timeout

# 5. PR 合入 main 后,打 Patch tag
git checkout main
git pull origin main
git tag -a v1.2.1 -m "Release v1.2.1 - hotfix"
git push origin v1.2.1

# 6. ⚠️ 同步回 develop(重要!否则下次发版修复会丢失)
git checkout develop
git merge main
git push origin develop

# 7. 删除 hotfix 分支
git branch -d hotfix/#62-payment-timeout
git push origin --delete hotfix/#62-payment-timeout
```

**关键点**:hotfix 必须**双向合并**,既合 `main` 也合 `develop`,否则下次 release 从 develop 拉出时会丢修复。

## 2.5 版本回滚策略

| 场景 | 操作 |
| :--- | :--- |
| 发布后发现严重 bug,影响范围大 | 从 `main` 回退到上一个 tag,重新发布 `git revert <commit>` |
| 单个功能有问题 | 快速合入 hotfix,发 Patch 版本 |
| 数据库迁移已执行无法回退 | 写反向迁移脚本 + hotfix 发布 |
| 线上服务起不来 | 部署层回滚:重跑 Deploy 流水线填上一版本号,或服务器执行 `./rollback.sh v1.1.0`(见第五篇) |

> **原则**:永远用 `git revert` 生成反向提交,不要 `git push -f` 改写 main 历史。

---

# 第三篇 GitHub Actions 基础

## 3.1 核心概念速查

| 概念 | 说明 |
| :--- | :--- |
| `workflow` | 一个完整的流水线(一个 yml 文件) |
| `job` | 流水线中的一个阶段(如 build、test),默认并行 |
| `step` | job 中的具体步骤(如 checkout、run、action) |
| `runs-on` | 运行环境(`ubuntu-latest`、`windows-latest` 等) |
| `on` | 触发条件(push、pull_request、schedule、workflow_dispatch) |
| `needs` | job 依赖,控制执行顺序 |
| `secrets.*` | 仓库/组织级加密变量(密钥、token 等) |
| `${{ }}` | 表达式语法,引用变量、上下文 |
| `uses` | 引用官方或社区 Action(复用逻辑) |

## 3.2 常用官方 Action

| Action | 用途 |
| :--- | :--- |
| `actions/checkout@v4` | 拉取代码 |
| `actions/setup-node@v4` | 安装 Node.js |
| `actions/setup-java@v4` | 安装 JDK |
| `actions/setup-python@v5` | 安装 Python |
| `actions/setup-go@v5` | 安装 Go |
| `actions/cache@v4` | 缓存依赖,加速构建 |
| `actions/upload-artifact@v4` | 上传构建产物 |
| `actions/download-artifact@v4` | 下载构建产物 |
| `docker/setup-buildx-action@v3` | Docker Buildx 多架构构建 |
| `docker/login-action@v3` | 登录镜像仓库 |
| `docker/build-push-action@v6` | 构建并推送镜像 |
| `appleboy/ssh-action@v1.0.3` | SSH 到远程服务器执行脚本 |
| `appleboy/scp-action@v0.1.7` | SCP 传文件到远程服务器 |
| `softprops/action-gh-release@v2` | 创建 GitHub Release |

## 3.3 配置 Secrets(密钥存在哪)

所有不能写在代码里的密钥(密码、token、SSH key 等),都存在 GitHub 仓库的 Secrets 里。

**操作路径**:仓库页面 → Settings → Secrets and variables → Actions → New repository secret

| 名称(Name) | 值(Secret) | 用途 |
| :--- | :--- | :--- |
| `DOCKERHUB_USERNAME` | 你的 Docker Hub 用户名 | 登录镜像仓库推镜像 |
| `DOCKERHUB_TOKEN` | Docker Hub 的 Access Token | 登录镜像仓库 |
| `STAGING_SERVER_HOST` | 测试服务器 IP 地址 | SSH 部署用 |
| `STAGING_SERVER_USER` | 测试服务器登录用户名(如 root) | SSH 部署用 |
| `STAGING_SSH_KEY` | 测试服务器 SSH 私钥(完整内容) | SSH 登录认证 |
| `PROD_SERVER_HOST` | 生产服务器 IP | 生产部署用 |
| `PROD_SERVER_USER` | 生产服务器用户名 | 生产部署用 |
| `PROD_SSH_KEY` | 生产服务器 SSH 私钥 | 生产部署用 |

> Secrets 是加密存储的,日志里只会显示 `***`,不会泄露私钥。
> SSH 私钥就是本地 `~/.ssh/id_rsa`(或部署专用密钥)的完整内容,以 `-----BEGIN OPENSSH PRIVATE KEY-----` 开头、`-----END OPENSSH PRIVATE KEY-----` 结尾,**包括首尾行完整复制粘贴进去**。

### 服务器端一次性准备(生成部署专用 SSH 密钥对)

> 建议不要直接用日常密钥,为 GitHub Actions 部署单独生成一对(约 5 分钟,只做一次):

```bash
# 1. 登录服务器,生成部署密钥对
ssh-keygen -t ed25519 -f ~/.ssh/github_deploy -N "" -C "github-actions-deploy"

# 2. 让这个密钥能登录本机
cat ~/.ssh/github_deploy.pub >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys

# 3. 取出私钥内容(复制,填到 GitHub Secrets 里)
cat ~/.ssh/github_deploy

# 4. 验证可用(应免密直接登录)
ssh -i ~/.ssh/github_deploy 用户名@127.0.0.1
```

## 3.4 配置 Environments(环境审批)

**操作路径**:仓库页面 → Settings → Environments → New environment

- 建一个 `staging` 环境(测试环境,不用审批)
- 建一个 `production` 环境(生产环境,**勾上 Required reviewers**,选 1-2 个人审批)

这样生产部署必须有人点同意才能执行,防止手滑直接上线。

```yaml
# workflow 中引用
jobs:
  deploy:
    environment:
      name: production
      url: https://yourdomain.com
```

## 3.5 流水线阶段设计总览

三条触发链,对应后文四条流水线(第四篇详解):

**PR 流水线(每次提 PR 触发)**:

```
1. Lint 检查        → 前端 eslint + 后端 checkstyle
2. 依赖安全扫描     → npm audit / dependency-check
3. 构建验证         → 前端 build + 后端 compile
4. 单元测试         → 前后端分别跑,输出覆盖率
5. 集成测试         → 后端接口测试
6. 数据库迁移测试   → 跑 migration 验证脚本正确性
```

**合并到 develop 触发**:

```
7. 构建 Docker 镜像
8. 部署到测试环境
9. E2E 测试(Playwright / Cypress)
```

**合并到 main / 打 tag 触发**:

```
10. 构建生产镜像,推送到镜像仓库
11. 部署到预发布环境
12. 手动确认 → 部署生产
13. 生成 Release Note + GitHub Release
```

---

# 第四篇 四条流水线详解(CI / E2E / Release / Deploy)

## 4.1 CI 流水线(PR 触发)

### 触发条件

- 对 `main`、`develop` 提 PR 时自动触发
- 直接 push 到 `develop` 时也触发(本地合并方案的 CI 兜底)

### 先改好配置文件

把 `ci.yml` 里的这些地方改成你自己的:

| 要改的地方 | 改成什么 | 在哪找 |
| :--- | :--- | :--- |
| `working-directory: frontend` | 你前端代码的目录名 | 你的目录结构 |
| `node-version: "20"` | 你项目用的 Node 版本 | `package.json` 里的 engines 或本地 `node -v` |
| `npm run lint` | 你前端 lint 的命令 | `package.json` 的 scripts 里 |
| `npm run build` | 你前端构建命令 | 同上 |
| `npm run test:ci` | 你前端测试命令 | 同上 |
| `backend` 部分的 JDK 版本和构建工具 | 你后端的技术栈 | Java 用 maven/gradle,Go 用 go build,Python 用 pytest |
| MySQL / Redis 连接信息 | 你后端读取的环境变量名 | 后端配置文件里的变量名 |

### 第一次跑 CI

1. 把 `.github/workflows/ci.yml` 提交到一个新分支
2. 提一个 PR 到 `develop` 或 `main`
3. 打开 PR 页面,往下滚,你会看到 CI 在跑(有个转圈的图标)
4. 点 **Details** 可以进到 GitHub Actions 页面看实时日志

```
PR 页面底部会显示:
✅ All checks have passed      ← 全通过了
  - CI / Frontend - Lint & Build
  - CI / Backend - Lint & Test
  - CI / DB Migration Test
```

### 常见失败原因 & 排查

| 报错信息 | 原因 | 解决方法 |
| :--- | :--- | :--- |
| `npm: command not found` | 没装 Node 或 setup-node 版本不对 | 检查 `setup-node` 的版本号 |
| `npm ci` 失败 | 没有 package-lock.json 或有冲突 | 本地跑 `npm install` 提交 lock 文件 |
| 连接不上 MySQL | services 配置有问题或端口不对 | 检查 services 的 ports 和 env 变量 |
| 构建成功但测试失败 | 代码有 bug 或测试用例挂了 | 点 Details 看具体哪个测试挂了 |
| `permission denied` | 脚本没有执行权限 | 本地 `chmod +x` 再提交,或用 `bash xxx.sh` 跑 |

> **排查技巧**:在 Actions 页面打开失败的 job,展开失败的 step,看红色错误信息,从上往下找第一个报错。

### 完整 CI 配置

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

    # 服务容器:测试需要的数据库、缓存等
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
          # 如果用的是 undo 迁移,验证回滚
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

### 关键设计说明

| 设计点 | 说明 |
| :--- | :--- |
| `concurrency` + `cancel-in-progress` | 同一分支多次 push 时,取消上一次未完成的流水线,节省资源 |
| `npm ci` 而非 `npm install` | 严格按照 `package-lock.json` 安装,保证依赖一致性 |
| `cache` | 缓存 node_modules / maven 依赖,第二次构建速度提升明显 |
| `services` | 直接在 CI 环境起 MySQL / Redis,测试用真数据库而不是全 Mock |
| `retention-days` | 构建产物保留 7 天,够排查问题,不浪费存储 |
| `continue-on-error: true` | 依赖安全扫描只告警不阻断,避免误报影响发版 |

## 4.2 E2E 流水线(合并 develop 触发)

### 触发条件

- 代码合并到 `develop` 分支后自动触发
- 也可以手动触发:Actions → E2E Tests → Run workflow

### E2E 干了什么

1. 起 MySQL + Redis
2. 编译启动后端服务
3. 编译前端
4. 装 Playwright 浏览器
5. 跑 E2E 测试脚本
6. 失败了自动上传截图报告(artifact 里下载)

### 为什么 E2E 不放在 CI 里

- E2E 慢(几分钟到十几分钟),放 CI 里每次提 PR 都跑太耗时间
- PR 阶段保证代码质量就够了,合并后再跑完整流程
- 真挂了影响范围也小,只是 develop 分支有问题,不影响 main

### 完整 E2E 配置

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

## 4.3 Release 流水线(打 tag 触发)

### 触发条件

打一个 `v` 开头的 tag 并推送,就会触发:

```bash
git tag v1.2.0
git push origin v1.2.0
```

或者在 GitHub 网页上手动打:Releases → Draft a new release → 填版本号 → Publish release

### 这个流水线做了三件事

```
打 tag v1.2.0
    │
    ├─► 1. 构建 Docker 镜像,推送到 Docker Hub
    │      打两个 tag:v1.2.0 和 latest
    │
    ├─► 2. 生成 GitHub Release 页面
    │      自动从 CHANGELOG.md 提取版本说明
    │
    └─► 3. 自动部署到测试环境 (staging)
           SSH 到服务器,拉新镜像,重启容器
```

### 镜像构建的原理(Docker 多阶段)

前端和后端各自有 `Dockerfile`,核心思想是「**在 CI 里构建,只把运行时需要的东西打包进镜像**」:

- 前端:Node 环境里 `npm run build` 生成 dist → 把 dist 扔到 Nginx 镜像里 → 最终镜像只有几十 MB
- 后端:JDK 环境里 `mvn package` 打 jar → 把 jar 扔到 JRE 镜像里 → 不用带编译工具

这样镜像体积小,拉取快,也更安全(没有编译工具链)。Dockerfile 完整写法见第六篇。

### 完整 Release 配置

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
          # 简单实现:找到版本标题到下一个版本标题之间的内容
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

## 4.4 Deploy 流水线(手动触发,部署生产)

生产环境不建议自动部署,推荐手动触发 + 审批:

- Actions → Deploy to Production → Run workflow → 填版本号 → 确定
- 如果配置了 Environments 审批,需要审批人去点 Approve

```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  workflow_dispatch:
    inputs:
      version:
        description: "版本号 (如 v1.2.0),默认 latest"
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

---

# 第五篇 CD 部署方案

## 5.1 部署架构总览

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

### 部署过程到底在干嘛

```
GitHub Actions 服务器
    │
    │ SSH 连接到你的服务器
    │
    ▼
你的服务器执行:
1. cd /opt/app
2. ./deploy.sh v1.2.0
   ├─ docker compose pull        ← 拉新版本镜像
   ├─ docker compose up -d backend   ← 先更后端
   ├─ 等健康检查通过
   └─ docker compose up -d frontend  ← 再更前端
3. docker image prune -f        ← 清理旧镜像
```

### 出问题了怎么回滚

**方法一:在 Actions 里手动触发回滚版本**

- 跑 Deploy 流水线,版本号填上个版本(如 `v1.1.0`)

**方法二:直接在服务器上执行回滚脚本**

```bash
cd /opt/app
./rollback.sh v1.1.0
```

本质都是「把镜像版本换成旧的,重新 `docker compose up -d`」。

## 5.2 三种 CD 模式对比

| 模式 | 适用场景 | 优点 | 缺点 |
| :--- | :--- | :--- | :--- |
| **SSH 直连部署** | 单服务器、小项目 | 简单直接,零额外组件 | 不可控,出问题难回滚 |
| **Docker Compose** | 1-3 台服务器,中小项目 | 环境一致,易迁移,回滚快 | 多机部署需手动协调 |
| **Kubernetes** | 多实例、高可用、大项目 | 自动扩缩容、滚动更新、自愈 | 学习成本高,运维复杂 |

## 5.3 方案一:Docker Compose 部署(推荐中小项目)

### 5.3.1 前置准备(服务器上,只做一次)

```bash
# 1. 装 Docker 和 Docker Compose
# 2. 建目录
mkdir -p /opt/app/{nginx/ssl,data/mysql,data/redis,logs/nginx,logs/backend}

# 3. 上传 docker-compose.yml 和 nginx.conf 到 /opt/app/
# 4. 创建 .env 文件,填敏感信息
cat > /opt/app/.env << 'EOF'
DB_ROOT_PASSWORD=你的数据库密码
DB_USERNAME=你的数据库用户
DB_PASSWORD=你的数据库密码
REDIS_PASSWORD=你的Redis密码
EOF

# 5. 登录 Docker Hub(不然拉不了私有镜像)
docker login -u 你的用户名

# 6. 传 deploy.sh 和 rollback.sh 到 /opt/app/,加执行权限
chmod +x /opt/app/deploy.sh
chmod +x /opt/app/rollback.sh
```

### 5.3.2 服务器目录结构

```
/opt/app/
├── docker-compose.yml     # 生产环境 compose
├── .env                   # 环境变量(敏感信息,不进 git)
├── deploy.sh              # 部署脚本
├── rollback.sh            # 回滚脚本
├── nginx/
│   └── nginx.conf         # 反向代理配置
├── data/
│   ├── mysql/             # MySQL 数据卷
│   └── redis/             # Redis 数据卷
└── logs/
    ├── nginx/
    └── backend/
```

### 5.3.3 生产环境 docker-compose.yml

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

### 5.3.4 Nginx 反向代理配置

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

### 5.3.5 部署脚本

```bash
#!/bin/bash
# /opt/app/deploy.sh
set -e

VERSION=${1:-latest}
echo "🚀 Deploying version: $VERSION"

# 1. 拉取最新镜像
cd /opt/app
VERSION=$VERSION docker compose pull

# 2. 先只更新后端(数据库迁移由后端启动时自动执行)
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

### 5.3.6 回滚脚本

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

## 5.4 方案二:GitHub Actions + SSH 自动部署

核心配置见 4.4 节 `deploy.yml`。要点回顾:

- `workflow_dispatch` 手动触发,支持填版本号
- `environment: production` 接入审批保护
- `appleboy/ssh-action` 远程执行服务器上的 `deploy.sh`
- 部署后自动 curl 健康检查验证上线成功

> 打 tag 自动触发(用于 staging),生产建议手动 + 审批。

## 5.5 方案三:Kubernetes 部署(进阶)

> 如果项目规模大、需要多实例高可用,用 K8s。

### 5.5.1 目录结构

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

### 5.5.2 Backend Deployment 示例

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
                command: ["sleep", "15"]   # 优雅关闭,等流量切走
```

### 5.5.3 HPA 自动扩缩容

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

### 5.5.4 K8s CD 方式:ArgoCD(GitOps)

**核心思想**:

- Git 仓库是唯一可信源(k8s 配置都在 git 里)
- ArgoCD 持续监听 Git 变化,自动同步到集群
- 回滚 = `git revert` + 自动同步

```
代码 push → CI 构建镜像 → 更新 k8s 配置中的镜像 tag → ArgoCD 自动部署
```

## 5.6 特殊场景:单文件产物直传服务器(scp)

> 如果你的项目产物不是 Docker 镜像,而是单个可执行文件(如 Windows 的 `exe`、游戏打包产物),用 `scp-action` 直接把文件推到服务器。

**你需要准备的东西(共 4 样)**:

| # | 项目 | 说明 |
| :--- | :--- | :--- |
| 1 | **服务器 SSH 地址** | 公网 IP 或域名 + 端口(默认 22,改过的话记下实际端口) |
| 2 | **登录用户名** | root 或普通用户(建议专用部署用户如 `deploy`) |
| 3 | **部署专用 SSH 密钥对** | 在服务器上生成(见 3.3 节),公钥留在服务器,私钥交给 GitHub |
| 4 | **服务器上的目标目录** | 文件要放到哪,如 `/var/www/portlens/download/` |

**服务器端准备**:

```bash
# 生成密钥对 + 授权登录(见 3.3 节)
# 创建目标目录并授权
mkdir -p /var/www/portlens/download
chown -R $USER /var/www/portlens
```

**GitHub 端 Secrets**(按此命名,与上文密钥准备对应):

| Secret 名称 | 填入内容 |
| :--- | :--- |
| `DEPLOY_HOST` | 服务器 IP 或域名 |
| `DEPLOY_USER` | 登录用户名 |
| `DEPLOY_SSH_KEY` | 私钥全部内容(含 BEGIN/END 行) |
| `DEPLOY_PORT` | SSH 端口,没改就填 22 |

**在构建流水线(如 build.yml)的 "Upload to Release" 之后加**:

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

之后每次打 `v*` 标签:编译 → 测试 → 发 Release → **自动把产物推到你服务器**,全流程无需人工干预。

## 5.7 环境管理

### 多环境配置

| 环境 | 用途 | 分支 | 数据 | 访问权限 |
| :--- | :--- | :--- | :--- | :--- |
| **本地开发** | 开发者本地调试 | feature/* | mock / 测试数据 | 本人 |
| **开发环境 (dev)** | 日常联调、功能验证 | develop | 测试数据 | 开发团队 |
| **测试环境 (test/staging)** | QA 测试、预发布验证 | release/* | 接近生产的测试数据 | 开发 + 测试 |
| **生产环境 (prod)** | 正式对外服务 | main | 真实用户数据 | 仅限运维/管理员 |

### GitHub Environments 与流水线的对应

- **staging** 环境:打 tag 后自动部署,无需审批
- **production** 环境:需要 1-2 人审批后才能部署,开启部署保护

---

# 第六篇 Dockerfile 最佳实践

## 6.1 前端多阶段构建

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

## 6.2 后端多阶段构建

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

## 6.3 Dockerfile 优化原则

- **多阶段构建**:最终镜像只含运行时依赖,体积小
- **分层缓存**:先复制依赖文件(package.json / pom.xml),再复制源码,依赖不变时命中缓存
- **非 root 用户运行**:安全加固
- **精简基础镜像**:用 alpine 版本
- **`.dockerignore`**:排除 node_modules、target、.git 等,减小构建上下文

---

# 第七篇 质量保障与运维

## 7.1 部署前检查清单

- CI 流水线全绿(lint、build、test、e2e)
- 数据库迁移脚本已验证可正向 + 回滚执行
- 配置文件 / 环境变量已更新到目标环境
- Release Note / 变更说明已准备
- 回滚方案已确认(上一个稳定版本号)
- 运维 / 相关人员已通知

## 7.2 部署后验证清单

- 服务健康检查通过(/health 接口返回 200)
- 前端页面可正常访问,静态资源加载正常
- 核心业务流程手动跑通(登录、核心功能)
- 接口监控无大量 5xx 错误
- 日志无异常报错
- 数据库迁移执行成功,数据正常

## 7.3 蓝绿部署 / 金丝雀发布(进阶)

| 策略 | 原理 | 适用场景 |
| :--- | :--- | :--- |
| **滚动更新** | 逐个替换旧实例,零停机 | 常规发布,大多数场景 |
| **蓝绿部署** | 同时跑两套环境,流量一次性切换 | 重大版本、需要快速全量回滚 |
| **金丝雀发布** | 先切少量流量,观察没问题再逐步放量 | 高风险变更、大版本,降低影响面 |

**金丝雀发布(Nginx 简单实现)**:

```nginx
# 90% 流量去旧版本,10% 去新版本
upstream backend {
    server backend-old:8080 weight=9;
    server backend-new:8080 weight=1;
}
```

逐步调整权重:10% → 30% → 50% → 100%,每步观察 10-30 分钟。

## 7.4 监控与告警(CD 的配套设施)

> 部署完能跑起来不够,还要知道跑得好不好。

### 监控三件套

| 类型 | 工具 | 看什么 |
| :--- | :--- | :--- |
| **基础设施监控** | Prometheus + Grafana | CPU、内存、磁盘、网络 |
| **应用监控 (APM)** | SkyWalking / Pinpoint | 接口耗时、慢 SQL、调用链 |
| **日志** | ELK (Elastic + Logstash + Kibana) | 错误日志、业务日志检索 |

### 关键告警指标

- 服务不可用(健康检查失败)
- 5xx 错误率 > 1%
- 接口 P99 延迟 > 3s
- CPU / 内存使用率 > 80% 持续 5 分钟
- 磁盘使用率 > 85%
- 数据库连接池耗尽
- Redis 内存使用率 > 90%

## 7.5 怎么看流水线运行结果

**进入方式**:仓库页面 → Actions 标签

每个 workflow run 进去后能看到:

```
Summary 页面
  ├── Jobs 列表  ← 哪些成功哪些失败
  ├── Artifacts  ← 上传的产物(构建包、测试报告、截图等)
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

**失败了怎么看**:

1. 找到红色的 step
2. 展开看日志
3. 往上翻,找到第一个 error 行
4. 根据错误信息定位问题

---

# 第八篇 工具链:gh CLI

> **`git` 管本地代码版本;`gh` 管 GitHub 云端平台操作**。
> `gh` 是 GitHub 官方命令行工具,把 PR、Issue、Actions、Release、仓库管理放到终端,**不用频繁切浏览器**。
> 它**不处理本地文件、不做 git commit/add**,只操作 GitHub 云端资源。

## 8.1 安装与登录

**Windows 安装(PowerShell)**:

```
winget install --id GitHub.cli
```

**验证安装**:

```
gh --version
```

**登录授权(必须做)**:

```
gh auth login
```

按交互选择:

1. `GitHub.com`
2. `HTTPS`
3. `Login with a web browser`
4. 复制验证码,浏览器打开,授权登录

**验证登录状态**:

```
gh auth status
```

## 8.2 非常适合用 gh 的工作

**1. 日常开发,减少浏览器来回切换**

- 推送完分支,**终端直接创建 PR**:`gh pr create --fill`
- 查看 PR 的 CI 检查状态:`gh pr checks 33`(不用打开网页看失败日志)
- 查看 CI 运行日志、重跑失败流水线:`gh run view` / `gh run rerun`
- 快速浏览 PR diff、批准审核 PR:`gh pr diff`、`gh pr review`
- 打开 Issue、查看 issue 内容、关闭 issue

> 例:PR#33 CI 失败,直接 `gh pr checks 33` 终端看失败原因,不用切浏览器。

**2. 批量操作(网页点鼠标很累)**

- 批量列出仓库、批量打标签、批量归档仓库
- 批量导出 Issue / PR 数据
- 批量克隆多个仓库

**3. 脚本 / 自动化(CLI 独有的优势)**

可以写 shell/powershell 脚本,自动完成 GitHub 操作:

> 例如:推送分支后自动创建草稿 PR;定时导出仓库统计;自动给 issue 打标签。网页做不到脚本自动化。

**4. 无图形界面环境(服务器、远程机器)**

服务器没有浏览器,只能用 `gh` 操作 GitHub。

**5. 快速跳转**

```
gh repo view --web
```

直接打开当前仓库网页,不用复制地址。

## 8.3 不适合用 gh 的场景(还是要用浏览器)

1. **复杂可视化代码评审**:大段代码 diff、并排对比,网页 / VSCode 体验更好;gh 只输出文本 diff
2. **仓库复杂配置**:分支规则(Ruleset)、权限、保护规则、组织设置,网页 UI 更直观
3. **处理复杂合并冲突**:冲突解决优先 VSCode / 网页
4. **看项目看板、仪表盘、统计图表**

---

# 第九篇 落地路径与学习路线

## 9.1 第一次跑通的推荐步骤(按顺序,踩坑少)

不要一次性把所有流水线都加上,按这个顺序来:

**第 1 步:先跑通 CI 的前端部分**

- 只保留 `frontend-lint-and-build` 这个 job
- 提 PR 看能不能正常 lint + build
- 成功了再加单元测试

**第 2 步:加后端 CI**

- 加上 `backend-lint-and-test` job
- 先不加 services(数据库),只跑构建和不需要数据库的测试
- 没问题了再加 MySQL services,跑集成测试

**第 3 步:加数据库迁移测试**

- 单独的 job,专门测 migration 能不能跑通

**第 4 步:本地手动打 Docker 镜像试试**

- 本地 `docker build -t test .` 确认 Dockerfile 没问题
- 本地 `docker run` 跑起来,访问一下看能不能用

**第 5 步:加 Release 流水线**

- 先只做「构建镜像 + 推送 Docker Hub」
- 手动打个 tag 试试,确认镜像推上去了

**第 6 步:加部署**

- 先部署到测试服务器,SSH 连成功就行
- 测试环境跑稳了再加生产环境 + 审批

## 9.2 最容易踩的坑

### 9.2.1 路径问题

- `working-directory` 是相对于仓库根目录的,别写错
- 前端构建产物路径要和 Dockerfile 里对得上(比如 `dist/` 还是 `build/`)

### 9.2.2 环境变量问题

- CI 里跑测试用的是 CI 环境的数据库,不是你本地的
- 后端要能从环境变量读数据库配置,不能写死在代码里

### 9.2.3 权限问题

- GitHub Actions 默认只有读权限,推镜像、创建 Release 需要加 `permissions: write`
- SSH 私钥要完整复制,包括开头结尾的 `-----BEGIN...` 和 `-----END...` 行

### 9.2.4 缓存问题

- `npm ci` 比 `npm install` 严格,必须有 lock 文件
- 如果缓存了依赖但 package.json 改了,缓存可能有问题,清一下缓存重新跑

### 9.2.5 部署后服务起不来

- 先看日志:`docker logs app-backend`
- 多半是配置不对(数据库连不上、环境变量缺失、端口冲突)
- 本地用同样的镜像跑一下,排除镜像本身的问题

## 9.3 分阶段学习目标

- **阶段 1(基础,1-2 周)**:掌握 Git 基本概念和常用命令;在 GitHub 上创建/克隆仓库、push/pull、分支与合并
- **阶段 2(协作,2-4 周)**:掌握 Fork + PR 工作流、代码审查、Issues、协作流程与冲突处理
- **阶段 3(自动化与进阶,3-6 周)**:学会 GitHub Actions(CI/CD)、保护分支、代码所有权、Packages、Pages、Dependabot
- **阶段 4(安全与管理,并行)**:权限管理、组织/团队策略、审计、Secrets 管理与合规
- **阶段 5(实战)**:用真实项目练手,参与开源项目,复盘与优化个人/团队流程
- **阶段 6(检验)**:达到预定目标(见下)

### 各阶段要点与练习

**阶段 1:Git 与 GitHub 基础(重点:理解快照、分支、远端)**

- 要点:
  - Git 的工作区 / 暂存区 / 本地仓库 / 远端仓库概念
  - 提交(commit)和提交信息规范
  - 分支(branch)的作用与常见操作
  - 推、拉、取、克隆的区别
- 操作练习:
  - 在本地初始化仓库,做几次 commit;使用 github.com 创建远端仓库并 push
  - 克隆一个仓库,修改文件,提交并 push
  - 创建分支、切换分支、合并分支(merge),模拟开发/修复流程
- 常用命令速查:

```
git clone <仓库>
git status
git add <file>
git commit -m "msg"
git branch / git branch -b <name>
git checkout <branch> 或 git switch <branch>
git merge <branch>
git push origin <branch>
git pull
git fetch
git log --oneline --graph --all
```

**阶段 2:协作与代码评审(重点:Pull Request 工作流)**

- 要点:
  - Fork vs Clone 的使用场景
  - Feature-branch + PR 流程:如何发起 PR、写好 PR 描述、关联 Issue
  - 代码审查(review)要点:可读性、测试、性能、安全、可回滚性
  - 处理冲突:rebase vs merge 冲突解决策略
- 操作练习:
  - 在他人仓库 Fork → 本地修改 → push 到自己的 fork → 发起 PR
  - 在组织 repo 中用 feature branch 发起 PR,进行 review、修改、合并
  - 模拟冲突:两个人修改同一文件并合并,练习解决冲突
- 好习惯:
  - 小而频繁的提交、清晰的 commit 信息、PR 模板、Issue 模板、CI 通过才合并

**阶段 3:自动化(GitHub Actions)与进阶功能**

- 要点:
  - 基本工作流:在 push 或 PR 时运行测试/lint/构建
  - Actions 基本语法(YAML)、Runner、矩阵测试、缓存、artifact
  - 保护分支(分支保护规则)、必要检查、代码所有者
  - Packages、GitHub Pages、Codespaces 简介
- 操作练习:
  - 写一个简单的 GitHub Actions 工作流:每次 PR 运行单元测试并报告结果
  - 为仓库启用 branch protection,要求 CI 通过并至少一人审查

**阶段 4:安全、权限与组织管理**

- 要点:
  - 组织与团队权限模型(owner、maintainer、member)
  - 仓库权限细化、最小权限原则
  - Secrets 管理(Actions secrets,Dependabot secrets)
  - Dependabot 自动更新、依赖扫描、代码扫描(CodeQL)
  - 审计日志与合规设置
- 操作练习:
  - 在组织中创建团队并设置不同权限,测试访问控制
  - 启用 Dependabot 并处理依赖更新 PR
  - 运行 CodeQL 扫描,查看并修复发现的问题

**阶段 5:实战与开源贡献**

- 找 1-2 个你感兴趣的开源项目,阅读贡献指南(CONTRIBUTING.md),从修复小 bug 或改进文档开始
- 在个人项目中实践 CI/CD、release 流程(使用 Releases / semantic versioning)
- 定期复盘:统计 PR 合并时间、代码审查效率、CI 成功率并优化流程

**阶段 6:检验目标**

- 能熟练用 Git 命令完成日常任务并解决常见冲突
- 熟练使用 PR 流程,能撰写高质量 PR、进行有效代码审查
- 能搭建 CI/CD(至少一个项目),并能配置 branch protections 与 secret
- 能为组织/团队设置合适权限并处理依赖及安全告警
- 能独立向开源项目贡献并被接受(PR 合并)

## 9.4 实际练习清单(可直接做)

- 创建个人仓库并部署一个 GitHub Pages 静态站点(练习 repo → Pages → 域名)
- 在同一仓库实现 CI:每次 push 运行测试并上传 artifact
- 为仓库添加 Issue 模板和 PR 模板
- 使用 Dependabot 自动管理依赖并处理一次更新 PR
- 参与 1 个开源项目:提交至少 1 个 PR(修文档或修 bug)

---

## 附录:快速索引

| 我想… | 去哪 |
| :--- | :--- |
| 建一个功能分支 | 第一篇 1.2 分支命名 |
| 写 commit message | 第一篇 1.4 |
| 合并 PR | 第一篇 1.5 |
| 审查别人代码 | 第一篇 1.6 Checklist |
| 发一个版本 | 第二篇 2.1-2.3 |
| 线上救火 | 第二篇 2.4 Hotfix |
| 配置密钥 | 第三篇 3.3 Secrets |
| 配置生产审批 | 第三篇 3.4 Environments |
| 改 CI 配置 | 第四篇 4.1 |
| CI 挂了排查 | 第四篇 4.1 + 第七篇 7.5 |
| 写 Dockerfile | 第六篇 |
| 部署到服务器 | 第五篇 5.3 |
| 回滚 | 第二篇 2.5 + 第五篇 5.1 |
| 不用浏览器看 CI | 第八篇 gh CLI |
| 从零搭建流水线 | 第九篇 9.1 六步走 |
