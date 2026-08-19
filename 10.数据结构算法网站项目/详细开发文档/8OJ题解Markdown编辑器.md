# OJ 题解 Markdown 编辑器开源方案

> 截图是**力扣分屏双栏编辑器：左侧 markdown 源码编辑，右侧实时预览**，OJ 题解场景刚需能力：**代码块高亮、LaTeX 数学公式、`[TOC]`目录生成、图片粘贴上传、表格、链接、工具栏、XSS 安全**，存储原始 markdown 文本存入数据库，**不要存 HTML**GitHub。

## 选型对比（适合 OJ 题解）

表格

| 编辑器             | 协议 | 核心能力                                                     | 适配 OJ        | 备注                                                         |
| ------------------ | ---- | ------------------------------------------------------------ | -------------- | ------------------------------------------------------------ |
| **Vditor**         | MIT  | 分屏编辑 / 预览、`[TOC]`目录、KaTeX 公式、代码高亮、图片拖拽粘贴上传、GFM、工具栏、XSS 防护 | ⭐⭐⭐⭐⭐ **首选** | 国内维护，HydroOJ 就在用，完全复刻力扣分屏模式，Java‑SpringBoot OJ 项目大量采用CSDN博... |
| ByteMD（掘金开源） | MIT  | 分屏、公式、代码块，轻量                                     | ⭐⭐⭐⭐           | Svelte 编写，Vue/React 均可接入，体积小，插件生态略少        |
| EasyMDE            | MIT  | 简洁分屏编辑器，GFM 语法                                     | ⭐⭐⭐            | 轻量，缺少原生 LaTeX 公式，需要额外插件扩展，老 OJ 常用CSDN博... |
| TUI‑Editor         | MIT  | 分屏、WYSIWYG，公式、Mermaid                                 | ⭐⭐⭐⭐           | Samsung 开源，功能全，包体积偏大                             |
| Editor.md          | MIT  | 老牌，分屏、公式、代码块                                     | ⭐⭐             | 项目维护停滞，不推荐新项目使用CSDN博...                      |

> ✅ **强烈推荐 Vditor**，和你截图力扣界面几乎一模一样，完美适配 OJ 题解场景，支持`[TOC]`生成目录，算法题的数学公式、大段代码块、粘贴截图上传全部支持GitHub。

## Vditor 关键 OJ 题解特性（正好匹配截图）

1. ✅ **左右分屏模式（split）**：左边源码 Markdown，右边实时预览，和力扣题解编辑器界面一致
2. ✅ `[TOC]` 自动生成目录，截图里就用到 `[TOC]`
3. ✅ KaTeX LaTeX 数学公式：`$O(log(min(m,n)))$`，算法复杂度直接写
4. ✅ 代码块语法高亮，支持 Java/C++/Python 等几十种语言
5. ✅ 截图直接粘贴上传图片（可对接后端上传接口）
6. ✅ 完整工具栏：标题、加粗、引用、表格、链接、代码块、公式按钮
7. ✅ 输出原始 markdown 文本，**保存原始 md 字符串存入 MySQL `oj_solution.content TEXT`字段**，前端渲染再转 HTML，不要存渲染后的 HTML
8. ✅ 内置 DOMPurify 做 XSS 过滤，防止用户恶意注入脚本，OJ 安全必备GitHub

### 最简前端初始化示例 (Vditor)

```
const vditor = new Vditor('vditor', {
    mode: 'split', // 分屏模式，和力扣界面一致
    height: 600,
    preview: {
        toc: true, // 开启[TOC]目录
        math: {engine: 'KaTeX'} // 数学公式
    },
    upload: {
        url: '/api/upload/image', // SpringBoot后端图片上传接口
        max: 10 * 1024 *1024
    },
    cache: {
        enable: true // 本地自动草稿，防止用户写题解丢失
    },
    toolbar: ["heading", "bold", "italic", "quote", "code", "table", "link", "image", "math"]
})

// 获取编辑后的原始markdown文本，提交给后端保存
let mdText = vditor.getValue();
```

## 后端 SpringBoot 配套方案（oj_solution 表）

1. **数据库只存原始 Markdown 字符串，TEXT 字段**，不要存渲染后的 HTML

```
CREATE TABLE `oj_solution` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  `problem_id` BIGINT NOT NULL COMMENT '关联题目id',
  `user_id` BIGINT NOT NULL COMMENT '发布用户',
  `title` VARCHAR(256) COMMENT '题解标题',
  `content` TEXT NOT NULL COMMENT '原始markdown文本',
  `is_public` TINYINT DEFAULT 1 COMMENT '是否公开',
  `status` TINYINT DEFAULT 1 COMMENT '0待审核 1正常',
  `like_count` INT DEFAULT 0,
  `is_deleted` TINYINT DEFAULT 0,
  `create_time` DATETIME,
  `update_time` DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

1. **展示页面**：前端引入 Vditor 预览组件，传入原始 md 文本渲染 HTML；**不要后端预渲染 HTML 返回**，避免前后渲染不一致。
2. **图片上传接口**：编辑器粘贴截图，调用后端接口，保存图片到对象存储，返回图片 url，自动写进 markdown。
3. **安全防护**：前端 Vditor 自带 XSS 过滤；后端再次使用 DOMPurify 校验渲染后的 HTML，拦截恶意脚本。

## 备选方案：自己搭底层（不推荐，工作量大）

- 编辑区：CodeMirror 6；渲染层：`markdown‑it` + `markdown‑it‑katex` + `markdown‑it‑toc` + `shiki`代码高亮，自己封装工具栏、分屏、上传。适合深度定制，开发成本高。

## 完整 OJ 题解页面流程

1. 用户打开发布题解页面 → Vditor 分屏编辑器
2. 用户编写题解，粘贴截图、写代码块、写公式、写`[TOC]`
3. 获取原始 markdown 字符串，POST 提交 SpringBoot 后端存入`oj_solution.content`
4. 查看题解页面：读取 content 字段，Vditor 仅预览模式渲染展示

> 开源参考完整 OJ 项目：**HydroOJ**，内部就是 Vditor 做题目描述 + 题解编辑器，可以参考它的集成逻辑CSDN博...。

如果你需要，我可以给一份 SpringBoot 图片上传接口示例，以及 Vue3 集成 Vditor 最小 demo。

 如何在自己的OJ题目中使用Vditor？

 除了Vditor，还有哪些适合OJ题解的Markdown编辑器？

 如何在自己的OJ题目中使用ByteMD？