# GitHup

网站

[文档]([GitHub Docs](https://docs.github.com/en))

## GLI

> **`git` 管本地代码版本；`gh` 管 GitHub 云端平台操作**GitHub Doc...。 `gh` 是 GitHub 官方命令行工具，把 PR、Issue、Actions、Release、仓库管理放到终端，**不用频繁切浏览器**。
>
> 它**不处理本地文件、不做 git commit/add**，只操作 GitHub 云端资源。

#### 1.Windows 安装（PowerShell）

```
winget install --id GitHub.cli
```

GithupCLI

```
gh --version
```

#### 2.登录授权（必须做）

```
gh auth login
```

按交互选择：

1. `GitHub.com`
2. `HTTPS`
3. `Login with a web browser`
4. 复制验证码，浏览器打开，授权登录。

验证登录状态

```
gh auth status
```

### 3.非常适合用 gh CLI 的工作

**1. 日常开发，减少浏览器来回切换（你的场景）**

- 推送完分支，**终端直接创建 PR**：`gh pr create --fill`
- 查看 PR 的 CI 检查状态：`gh pr checks 33`（不用打开网页看失败日志）
- 查看 CI 运行日志、重跑失败流水线：`gh run view` / `gh run rerun`
- 快速浏览 PR diff、批准审核 PR：`gh pr diff`、`gh pr review`
- 打开 Issue、查看 issue 内容、关闭 issue

> 你的 PR#33 CI 失败，就可以用 `gh pr checks 33` 终端直接看失败原因，不用切浏览器。

**2. 批量操作（网页点鼠标很累）**

- 批量列出仓库、批量打标签、批量归档仓库
- 批量导出 Issue / PR 数据
- 批量克隆多个仓库

**3. 脚本 / 自动化（CLI 独有的优势）**

可以写 shell/powershell 脚本，自动完成 GitHub 操作：

> 例如：推送分支后自动创建草稿 PR；定时导出仓库统计；自动给 issue 打标签。 网页做不到脚本自动化。

**4. 无图形界面环境（服务器、远程机器）**

服务器没有浏览器，只能用 `gh` 操作 GitHub。

**5. 快速跳转**

```
gh repo view --web
```

直接打开当前仓库网页，不用复制地址。

------

### 4.不适合用 gh 的场景（还是要用浏览器）

1. **复杂可视化代码评审**：大段代码 diff、并排对比，网页 / VSCode 体验更好；gh 只输出文本 diffGitHub。
2. **仓库复杂配置**：分支规则 (Ruleset)、权限、保护规则、组织设置，网页 UI 更直观。
3. **处理复杂合并冲突**：冲突解决优先 VSCode / 网页。
4. **看项目看板、仪表盘、统计图表**。

## 玩转

### 一、分阶段学习目标（总览）

- 阶段 1（基础，1–2 周）：掌握 Git 基本概念和常用命令;在 GitHub 上创建/克隆仓库、push/pull、分支与合并。
- 阶段 2（协作，2–4 周）：掌握 Fork + PR 工作流、代码审查、Issues、协作流程与冲突处理。
- 阶段 3（自动化与进阶，3–6 周）：学会 GitHub Actions（CI/CD）、保护分支、代码所有权、Packages、Pages、Dependabot。
- 阶段 4（安全与管理，并行）：权限管理、组织/团队策略、审计、Secrets 管理与合规。
- 阶段 5（实战）：用真实项目练手，参与开源项目，复盘与优化个人/团队流程。
- 阶段6 （检验）： 达到预定目标

### 二、阶段详细内容与练习

**阶段 1：Git 与 GitHub 基础（重点：理解快照、分支、远端）**

- 要点：
  - Git 的工作区 / 暂存区 / 本地仓库 / 远端仓库概念
  - 提交（commit）和提交信息规范
  - 分支（branch）的作用与常见操作
  - 推、拉、取、克隆的区别
- 操作练习：
  - 在本地初始化仓库，做几次 commit;使用 github.com 创建远端仓库并 push。
  - 克隆一个仓库，修改文件，提交并 push。
  - 创建分支、切换分支、合并分支（merge），模拟开发/修复流程。
- 常用命令速查（示例）：
  - git clone <仓库>
  - Git 状态
  - git 添加 <file>
  - git commit -m “msg”
  - git branch / git branch -b <name>
  - git checkout <branch> 或 git switch <branch>
  - git merge <branch>
  - git push origin <branch>
  - git pull
  - git fetch
  - git日志 --oneline --graph --all

**阶段 2：协作与代码评审（重点：Pull Request 工作流）**

- 要点：
  - Fork vs Clone 的使用场景
  - Feature-branch + PR 流程：如何发起 PR、写好 PR 描述、关联 Issue
  - 代码审查（review）要点：可读性、测试、性能、安全、可回滚性
  - 处理冲突：rebase vs merge 冲突解决策略
- 操作练习：
  - 在他人仓库 Fork -> 本地修改 -> push 到自己的 fork -> 发起 PR。
  - 在组织 repo 中用 feature branch 发起 PR，进行 review、修改、合并。
  - 模拟冲突：两个人修改同一文件并合并，练习解决冲突。
- 好习惯：
  - 小而频繁的提交、清晰的 commit 信息、PR 模板、Issue 模板、CI 通过才合并。

**阶段 3：自动化（GitHub Actions）与进阶功能**

- 要点：
  - 基本工作流：在 push 或 PR 时运行测试/lint/构建
  - 动作基本语法（YAML）、Runner、矩阵测试、缓存、artifact
  - 保护分支（分支保护规则）、必要检查、代码所有者
  - Packages、GitHub Pages、Codespaces 简介
- 操作练习：
  - 写一个简单的 GitHub Actions 工作流程：每次 PR 运行单元测试并报告结果。
  - 为仓库启用 branch protection，要求 CI 通过并至少一人审查。
- 示例（简短 工作流程）：
  - 在 .github/workflows/ci.yml 中定义：在 push/PR 时运行 node/npm 测试（我可以帮你写样例）。

**阶段 4：安全、权限与组织管理**

- 要点：
  - 组织与团队权限模型（owner、maintainer、member）
  - 仓库权限细化、最小权限原则
  - Secrets 管理（Actions secrets，Dependabot secrets）
  - Dependabot 自动更新、依赖扫描、代码扫描（CodeQL）
  - 审计日志与合规设置
- 操作练习：
  - 在组织中创建团队并设置不同权限，测试访问控制。
  - 启用 Dependabot 并处理依赖更新 PR。
  - 运行 CodeQL 扫描，查看并修复发现的问题。

**阶段 5：实战与开源贡献**

- 做法：
  - 找 1–2 个你感兴趣的开源项目，阅读贡献指南（CONTRIBUTING.md），从修复小 bug 或改进文档开始。
  - 在个人项目中实践 CI/CD、release 流程（使用 Releases / semantic versioning）。
  - 定期复盘：统计 PR 合并时间、代码审查效率、CI 成功率并优化流程。

**阶段6：目标**

- 能熟练用 Git 命令完成日常任务并解决常见冲突
- 熟练使用 PR 流程，能撰写高质量 PR、进行有效代码审查
- 能搭建 CI/CD（至少一个项目），并能配置 branch protections 与 secret
- 能为组织/团队设置合适权限并处理依赖及安全告警
- 能独立向开源项目贡献并被接受（PR 合并）

### 三、实际练习清单（可直接做）

- 创建个人仓库并部署一个 GitHub Pages 静态站点（练习 repo -> Pages ->域名）。
- 在同一仓库实现 CI：每次 push 运行测试并上传 artifact。
- 为仓库添加 Issue 模板和 PR 模板。
- 使用 Dependabot 自动管理依赖并处理一次更新 PR。
- 参与 1 个开源项目：提交至少 1 个 PR（修文档或修 bug）。



