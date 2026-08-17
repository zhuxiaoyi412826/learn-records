# 完整 Vibe Coding 流水线（依照图中链路整理）

整套是 **Vibe Design + Vibe Coding** 一体化 AI 开发工作流

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260728133951.png)



```
自然语言需求
     ↓
【Google Stitch】Vibe Design AI生成UI初稿
     ↓（一键导入）
【Figma】精细化设计、组件规范、交互定稿
     ↓ 设计资产输入
┌───────────┬────────────┬───────────────┐
│方案A      │方案B       │方案C（新增）
│AI Studio  │Antigravity │本地AI编辑器
│云端前端开发│云端全栈开发│本地环境开发微调
└───────────┴────────────┴───────────────┘
     ↓（三路代码汇总）
【GitHub】代码版本托管
     ↓（CI/CD自动流水线）
【Vercel】前端项目云端部署上线
```

### 1. UI 设计阶段：Google Stitch（Vibe Design）

- 通过文字描述、草图生成整套页面 UI 原型
- 产出文件可直接导入 Figma，快速完成方案验证
- 作用：快速产出界面创意初稿，省去手工画线框图

### 2. 设计精加工：Figma

1. 导入 Stitch 生成的界面初稿
2. 统一色彩、组件、排版规范，完善交互原型
3. 开放 Dev 模式，输出尺寸、样式、页面结构给开发工具

### 3. 三大并行开发方案（核心，新增方案 C）

#### 方案 A：AI Studio（云端前端 APP 快速开发）

适合：快速搭建前端页面原型，专注界面还原

读取 Figma 设计 → AI 生成前端代码，快速预览页面效果

#### 方案 B：Google Antigravity（云端全栈 APP 开发）

适合：一站式完整项目开发

AI 智能体自主读取设计需求 → 设计数据表、编写前端 + 后端逻辑

自动执行 npm 安装、调试、修复依赖报错，云端完成全流程开发

#### 方案 C：本地 AI 编辑器开发 / 微调【新增】

**工具代表：Cursor、Windsurf、VS Code + Copilot/Claude Code**

适用场景：

1. 项目复杂、需要本地运行环境；
2. 云端工具网络受限、想要掌控完整工程；
3. 在 Antigravity/AI Studio 生成初始代码后，**本地深度微调、修复 bug、优化架构**；
4. 对接本地数据库、本地调试后端服务。

工作流：

① 从 Figma 获取设计规范

② 拉取仓库代码至本地

③ 使用本地 AI 编辑器以 Vibe Coding 模式开发 / 迭代：

自然语言下达需求 → AI 编写代码 → 本地运行调试

> 优势：不受外网限制，可自由配置 Node、Java、数据库本地环境，适合老项目改造（比如你的苍穹外卖）

### 4. 代码统一托管：GitHub

A/B/C 任意方案写完代码，统一 Push 到 Github 仓库，统一管理版本、备份源码。

### 5. 云端发布：Vercel

绑定 Github 仓库，代码更新后自动打包构建，部署前端站点，生成公网访问链接。

## 拓展可选增强节点（按需插入）

如果项目需要持久化数据，在【开发阶段 ↔ GitHub】之间加入：

> Supabase / Firebase 作为后端数据库、用户登录、文件存储服务

## 场景选择建议

- 快速 Demo 原型：方案 A / B
- 正式商用项目、需要深度可控、本地调试、改造旧项目：**优先方案 C（本地 Cursor 等编辑器）**
- 不想搭建本地环境，追求零配置一键开发上线：方案 B Antigravity

我可以把这套流程整理成 PPT 可用的图文结构或者 Mermaid 流程图，方便你直接复制使用。

# 国产化替代

| 原版海外工具                       | 用途                           | 推荐国产替代方案                                             |
| ---------------------------------- | ------------------------------ | ------------------------------------------------------------ |
| Google Stitch                      | AI 生成 UI（Vibe Design）      | **Pixso AI / MasterGo AI**                                   |
| Figma                              | 精细化 UI 设计、协作、交付开发 | **Pixso（首选）、MasterGo、即时设计**                        |
| Google AI Studio                   | 云端前端快速生成               | **豆包开发工作台 / 通义灵码云端版**                          |
| Google Antigravity                 | AI 智能体全栈云端 IDE          | **通义灵码智能体、CodeGeeX 云端开发、智谱 Code 智能体**      |
| GitHub                             | 代码托管                       | **Gitee（码云，首选）、Gitee 企业版**                        |
| Vercel                             | 前端自动部署、全球 CDN         | **Cloudflare Pages 备选，国内首选：****阿里云静态网站托管 / 腾讯云静态托管 / Vercel 国内访问不稳定，替换为「Zeabur、Render 备选，商用选阿里云 Serverless 应用引擎 SAE」** |
| Supabase/Firebase（拓展后端 BaaS） | 后端数据库、登录服务           | **CloudBase 云开发（腾讯）、阿里云云开发、LeanCloud、火山引擎 VeDB** |