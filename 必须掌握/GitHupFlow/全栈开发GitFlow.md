# 全栈GitFlow

> ✅ 适用场景：**前后端同仓 / 单体全栈开源项目**、有 PR、有 Release、社区可贡献；**不搞企业重度流程，兼顾个人维护 + 外部开发者协作** ✅ 版本号：严格 **SemVer 语义化版本 `Major.Minor.Patch`**
>
> - Major：不兼容破坏性变更
> - Minor：新增功能，向下兼容
> - Patch：Bug 修复、小优化

## 一、常驻长期分支（永久存在）

表格

| 分支名    | 作用                                                     | 准入规则                                                    |
| --------- | -------------------------------------------------------- | ----------------------------------------------------------- |
| `main`    | 生产稳定版，对应已发布 Release 标签，**随时可部署上线**  | ❌ 禁止直接 push；只能通过 PR 合并，合并前必须自测 / CI 通过 |
| `develop` | 开发集成分支，存放下个版本待发布特性，全栈所有新功能汇总 | 所有功能分支最终合入这里；不直接上线                        |

> 如果是**极简轻量开源、没有复杂版本规划**，可以直接砍掉`develop`，只用`main + 短期特性分支`（Trunk 模式，文末补充）

## 二、短期临时分支（开发完成合并后删除）

**本地临时开发分支**

统一前缀 `类型/issue编号-简短描述`，有 issue 优先带上 issue 号，开源协作更清晰

表格

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

### ✅ 分支命名示例

```
feat/#42-user-login-page
fix/#58-api-timeout
hotfix/#62-main-cors-error
refactor/backend-controller-split
docs/update-install-guide
release/v1.2.0
```

## 三、完整流转流程（全栈同仓标准流程）

1. 新功能：从 `develop` 拉 `feat/xxx` → 本地开发前端 + 后端代码 →本地自测 → 提 PR 到`develop` → CI 跑前端构建、后端单元测试、代码审查 → 合并 → 删除分支

   1. 在本地feat/xxx分支开发代码
      ↓
   2. ✅【本地自测】：本地跑lint、编译、单元测试、手动验证功能
      → 本地确认：代码能编译、单元测试通过、功能跑通，没有明显bug
      ↓
   3. 提交本地commit，push到远程（如果需要远程PR）
      ↓
   4. 创建 PR 目标分支：develop
      ↓
   5. 🤖 CI流水线自动执行：前端构建、后端单元测试、静态检查
      → CI是**二次校验兜底**，防止你本地环境漏测、环境差异、提交漏文件
      ↓
   6. 🧑‍💻 代码审查（CR）：看代码逻辑、规范、设计
      ↓
   7. 全部CI通过 + CR通过 → 合并PR到develop
      ↓
   8. 删除临时feat分支（本地+远程）

2. 普通 bug：从 `develop` 拉 `fix/xxx` → 修复 → PR 合`develop`

3. 版本发布：从

   ```
   develop
   ```

   拉出

   ```
   release/v1.2.0
   ```

   - 只改版本号、更新 CHANGELOG、全栈联调测试，**禁止新增功能**
   - 测试无误后，PR 合并到`main`，同时合并回`develop`
   - 在`main`打 tag：`git tag v1.2.0 && git push --tags`，生成 Release

4. 线上紧急 bug：从`main`拉`hotfix/xxx` → 修复 → PR 合`main`（打 Patch 版本 tag `v1.2.1`），同时同步合并回`develop`

## 四、Commit 提交规范（开源必备，推荐 Conventional Commits）

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

## 五、PR 规范（开源协作核心）

PR 标题遵循 commit 规范，模板包含：

1. 变更内容（前端改动 / 后端改动 / 数据库变更）
2. 自测清单
3. 是否有破坏性改动
4. 关联 issue 编号 `Closes #42`

## 六、PR合并

> 核心：**PR 本身是 GitHub/Gitee 平台概念，PR 的合并操作可以在网页上点按钮完成；但你也可以本地合并，再 push，不走网页 PR。**

### 1、什么是 PR（Pull Request）

PR 是**远程平台 (GitHub) 的功能**：

> 你本地`feat/xxx` → push 到远程 → 在 GitHub 网页发起 PR，目标分支`develop`。 网页上可以看到：代码变更、CI 运行结果、代码审查 (CR) 评论。

**✅网页上合并 PR（GitHub）**

1. 网页看到 CI 全部绿色通过
2. 自己做 CR，看 diff 变更
3. 点「Merge pull request」，选择合并策略（merge /squash/rebase）
4. GitHub 自动把 feat 分支的提交合并到远程`develop`
5. 可以选择**删除远程的 feat 分支**

👉缺点：

- 网页看代码 diff，没有 IDE 编辑器的语法高亮、跳转、智能提示，**阅读大段代码体验很差**，不如本地 IDE 舒服。

------

### 2、两种方案，个人项目怎么选

#### 方案 A：走 GitHub PR 流程（推荐，保留规范）

> 流程：

1. dev分支开发完成，本地自测全部通过
2. push 到远程 `feat/xxx`
3. GitHub 网页创建 PR 到`develop`
4. ⚠️代码审查不要在网页硬看！
   - 把 PR 的代码拉到本地 IDE：`git fetch`，在本地 IDE 打开这个 feat 分支，**在编辑器里做 CR 审查**，看代码、改 bug。
   - 发现问题：本地修改，commit，push 回远程 feat 分支，PR 会自动更新。
5. 本地 CR 确认没问题，CI 流水线全部绿色 ✅
6. 回到 GitHub 网页，执行**网页合并 PR**。
7. 合并完成，删除远程 feat 分支；本地删除本地 feat 分支。

> ✨关键点：**CR 审查可以在本地 IDE 做，不一定要在网页上看 diff；只是合并动作在 GitHub 网页完成。** 网页只做：看 CI 状态、确认合并、保留 PR 记录。

#### 方案 B：完全本地合并，不使用 GitHub PR（极简个人模式）

> 不创建 PR，全部本地操作，不用网页。

```
# 本地feat写完，自测完成
git checkout dev
git merge feat/xxx
# 本地IDE再审查一遍合并后的代码
git push origin dev
# 删除本地feat分支
git branch -d feat/xxx
```

✅优点：完全 IDE 操作，体验最好。 ❌缺点：**没有 PR 记录，没有 CI 自动校验；需要你自己手动跑一遍 CI 检查，否则坏代码会直接进 dev。**

> 如果你想保留 CI 兜底：本地合并完成后，push 到 dev，CI 会自动跑；但是没有 PR 这个载体。

------

### 3、一个很重要的误区

> ❌误区：做 CR 就必须在 GitHub 网页看代码。 ✅真相：CR 是**审查代码变更**，工具可以是本地 IDE，网页只是载体。 PR 的价值：
>
> 1. 触发 CI 流水线自动校验
> 2. 留下变更记录（历史可以看到：哪个功能、什么时候合并到 dev）
> 3. 可以在 PR 写备注、记录修改点
> 4. 以后多人协作可以直接复用这套流程

## 七、项目目录

```
repo-root/
├── frontend/              # 前端项目（独立 package.json）
│   ├── src/
│   ├── package.json
│   └── vite.config.ts
├── backend/               # 后端项目（独立构建配置）
│   ├── src/
│   ├── pom.xml / go.mod / requirements.txt
│   └── db/migration/      # 数据库迁移脚本
├── docs/                  # 项目文档（部署、API、架构图）
│   ├── README.md
│   ├── api/
│   └── architecture/
├── scripts/               # 公共脚本（构建、部署、CI 辅助）
├── .github/               # GitHub 配置
│   ├── workflows/         # CI/CD 流水线
│   ├── ISSUE_TEMPLATE/    # Issue 模板
│   └── PULL_REQUEST_TEMPLATE.md
├── docker-compose.yml     # 本地一键启动全环境
└── README.md
```

- 前后端代码物理隔离，各自独立构建、独立 lint
- 共享文档、脚本、CI 配置，避免重复
- 一个 PR 可以同时包含前后端改动 + 文档更新，review 上下文完整

## 八、版本发布详细流程（Release 分支操作手册）

###  发布前检查清单

在从 `develop` 拉出 `release/vX.Y.Z` 之前，确认：

-  所有计划内功能的 PR 都已合入 `develop`
-  `develop` 分支 CI 全绿
-  CHANGELOG 草稿已准备（按 feat / fix / refactor / docs 分类）
-  数据库迁移脚本已验证可正向执行 + 回滚
-  核心功能手动冒烟测试通过

###  发布步骤

```bash
# 1. 从 develop 拉出 release 分支
git checkout develop
git pull origin develop
git checkout -b release/v1.2.0

# 2. 更新版本号
#   - 前端：package.json version 字段
#   - 后端：pom.xml / version.go / __version__.py
#   - 文档：CHANGELOG.md 填入本次版本内容和日期

# 3. 提交版本号变更
git add .
git commit -m "chore: bump version to v1.2.0"

# 4. 推送并创建 PR 到 main（标题：release: v1.2.0）
git push origin release/v1.2.0
```

###  发布后收尾

PR 合并到 `main` 后：

```bash
# 1. 打 tag
git checkout main
git pull origin main
git tag -a v1.2.0 -m "Release v1.2.0"
git push origin v1.2.0

# 2. 合并回 develop（避免 develop 落后于 main）
git checkout develop
git merge main
git push origin develop

# 3. 删除 release 分支
git branch -d release/v1.2.0
git push origin --delete release/v1.2.0
```

###  版本回滚策略

| 场景                           | 操作                                                       |
| :----------------------------- | :--------------------------------------------------------- |
| 发布后发现严重 bug，影响范围大 | 从 `main` 回退到上一个 tag，重新发布 `git revert <commit>` |
| 单个功能有问题                 | 快速合入 hotfix，发 Patch 版本                             |
| 数据库迁移已执行无法回退       | 写反向迁移脚本 + hotfix 发布                               |

> **原则**：永远用 `git revert` 生成反向提交，不要 `git push -f` 改写 main 历史。

## 九、Hotfix 紧急修复详细流程

```bash
# 1. 从 main 拉出 hotfix 分支
git checkout main
git pull origin main
git checkout -b hotfix/#62-payment-timeout

# 2. 修复 bug，本地自测
# ... 改代码、跑测试 ...

# 3. 提交
git add .
git commit -m "fix(backend): 修复支付回调超时问题"

# 4. 推送并提 PR 到 main
git push origin hotfix/#62-payment-timeout

# 5. PR 合入 main 后，打 Patch tag
git checkout main
git pull origin main
git tag -a v1.2.1 -m "Release v1.2.1 - hotfix"
git push origin v1.2.1

# 6. ⚠️ 同步回 develop（重要！否则下次发版修复会丢失）
git checkout develop
git merge main
git push origin develop

# 7. 删除 hotfix 分支
git branch -d hotfix/#62-payment-timeout
git push origin --delete hotfix/#62-payment-timeout
```

**关键点**：hotfix 必须**双向合并**，既合 `main` 也合 `develop`，否则下次 release 从 develop 拉出时会丢修复。

## 十、代码审查（CR）Checklist

PR Reviewer 按以下清单检查：

###  通用

-  代码逻辑是否正确，有没有明显 bug
-  命名是否清晰，函数/变量名能表达意图
-  有没有重复代码，可以抽取复用
-  注释是否必要且不过度
-  是否有破坏性改动（接口变更、字段删除等）

###  前端专项

-  组件拆分是否合理，props 设计是否清晰
-  状态管理是否正确，有无不必要的全局状态
-  错误边界 / 加载态 / 空态是否处理
-  列表是否加了 key，有无性能隐患
-  TypeScript 类型是否完整，禁用了 any

###  后端专项

-  接口入参校验是否完整
-  SQL 是否有注入风险，是否走索引
-  事务边界是否正确
-  异常处理和错误码是否统一
-  外部依赖调用是否有超时和重试

### 安全

-  是否有敏感信息硬编码（密钥、密码）
-  权限校验是否到位（越权风险）
-  用户输入是否做了过滤/转义

## 十一、CI/CD 流水线阶段设计

###  PR 流水线（每次提 PR 触发）

Plain Text

```
1. Lint 检查        → 前端 eslint + 后端 checkstyle
2. 依赖安全扫描     → npm audit / dependency-check
3. 构建验证         → 前端 build + 后端 compile
4. 单元测试         → 前后端分别跑，输出覆盖率
5. 集成测试         → 后端接口测试
6. 数据库迁移测试   → 跑 migration 验证脚本正确性
```

###  合并到 develop 触发

Plain Text

```
7. 构建 Docker 镜像
8. 部署到测试环境
9. E2E 测试（Playwright / Cypress）
```

###  合并到 main / 打 tag 触发

Plain Text

```
10. 构建生产镜像，推送到镜像仓库
11. 部署到预发布环境
12. 手动确认 → 部署生产
13. 生成 Release Note + GitHub Release
```

## 十二、开源贡献者指南（外部开发者）

> 针对社区贡献者，建议在 `CONTRIBUTING.md` 中写明：

1. **Fork & Clone**：fork 到自己账号，clone 到本地
2. **设置上游**：`git remote add upstream <原仓库地址>`
3. **同步最新代码**：`git fetch upstream && git rebase upstream/develop`
4. **从 develop 拉特性分支**：命名遵循项目规范
5. **本地自测**：跑 lint、测试、手动验证
6. **提 PR**：目标分支选 `develop`，填写 PR 模板
7. **响应 CR**：根据 review 意见修改，push 到同一分支

## 十三、Issue 与 PR 模板示例

###  Bug Issue 模板

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
- 操作系统：
- 浏览器版本：
- 后端版本：

**截图 / 日志**
```

###  PR 模板

```markdown
## 变更内容

<!-- 描述这次 PR 做了什么 -->

- 前端改动：
- 后端改动：
- 数据库变更：

## 关联 Issue

Closes #XX

## 自测清单

- [ ] 本地 lint 通过
- [ ] 本地构建通过
- [ ] 单元测试通过
- [ ] 手动测试了核心流程
- [ ] 验证了边界情况（空输入、异常等）
- [ ] 数据库迁移脚本验证过

## 破坏性改动

- [ ] 有（请描述影响范围和迁移方案）
- [ ] 没有

## 截图 / 录屏

（前端改动请附上效果图）
```

