# VIM

## 一、什么是 Vim / Neovim

**Neovim（nvim）**：**Vim 的现代化重构分支，尽量兼容 Vim 操作**，目标是解决 Vim 架构缺陷，更适合现代代码开发。

[仓库](https://github.com/neovim/neovim) [官网](https://neovim.io/)

## 二、Neovim 和 Vim 核心区别

| 特性       | Vim                 | Neovim                                                |
| ---------- | ------------------- | ----------------------------------------------------- |
| 主脚本语言 | Vimscript           | **原生 Lua**（主流配置 / 插件用 Lua，兼容 Vimscript） |
| LSP        | 需要第三方插件      | **内置 LSP 客户端**，原生支持代码补全、跳转、诊断     |
| 异步       | 支持弱，容易卡顿    | 原生异步任务，不会阻塞 UI                             |
| 语法解析   | 基础正则高亮        | **Treesitter** 精准语法解析、高级高亮 / 文本对象      |
| 配置文件   | `.vimrc` / init.vim | **init.lua（推荐）**，也兼容 init.vim                 |
| RPC        | 弱                  | 内置 RPC，可外部程序控制 nvim                         |
| 包管理     | 自带，使用繁琐      | 自带 packpath，社区主流 lazy.nvim                     |

## 三、Nvim 核心 6 种模式

1. **Normal（普通模式）** nvim 启动默认进入，所有复杂编辑指令（`yy`复制、`dd`删除、`p`粘贴）都在这里执行，`h j k l` 左 下 上 右，**所有模式都可以按 ESC 切回普通模式**。

2. **Insert（插入模式）** 就是常规编辑器输入文字的模式，适合写 Java 代码。

   i：光标**前**插入；a光标**后**插入；I：行首插入；A：行尾插入；o：下方新开一行插入；O：上方新开一行插入。

3. **Visual（可视模式）** 区域选中，三种子模式：

- `v`：字符可视（选中单个 / 连续字符）
- `V`：行可视（整行选中）
- `Ctrl+v`：块可视（矩形块选中，批量对齐代码超好用）
- 选中后按 `y` 复制、`d` 删除、`>` 缩进、`<` 取消缩进。

4. **Select（选择模式）** 和可视很像，但行为不一样：选中后直接敲字符**直接覆盖选中内容**，和记事本鼠标选中效果一致。

   普通模式输入`gh`直接进入选择模式；可视模式下按`Ctrl+g`可在可视模式与选择模式之间直接切换。

5. **Cmdline（命令行模式）** 底部单行输入指令：保存`:w`、退出`:q`、全局替换、搜索文本。

6. **Terminal（终端模式）** Nvim 内置终端，可以直接在编辑器里执行 cmd、git、java 编译等命令；

   普通模式输入 `:terminal` 回车，即可进入 Neovim 内置终端模式，简写`:ter` 回车

   退出终端输入`Ctrl+\ Ctrl+n`切回普通模式

## 四、高频基础命令 & 快捷键（必记）

外部先`Ctrl+C`复制内容，普通模式用`"+p`粘贴；插入模式用`Ctrl+R`后按`+`粘贴；配置`unnamedplus`后直接`p`就能粘贴系统剪贴板内容。

### ✅ 普通模式（Normal）

**光标移动**

```
h 左  j 下  k 上  l 右
w 下单词开头   b 上单词开头   e 单词结尾
0 行首  $ 行尾
gg 文件开头  G 文件末尾
% 跳匹配括号 () [] {}
```

**删除 / 修改 / 复制粘贴**

```
x 删单个字符
dd 删除整行   dw 删除单词
ciw 修改当前单词  ci( 修改括号内
yy 复制整行   yw 复制单词
p 粘贴光标后   P 粘贴光标前
```

**搜索**

```
/关键词 向下搜索
?关键词 向上搜索
n 下一处匹配  N 上一处匹配
```

**分屏（前缀 Ctrl+w）**

```
Ctrl+w s 水平分屏
Ctrl+w v 垂直分屏
Ctrl+w h/j/k/l 切换窗口
Ctrl+w q 关闭窗口
```

### ✅ 命令行模式（: 开头）

```
:w      保存
:q      退出（无修改才可退出）
:wq     保存并退出
:q!     强制退出，丢弃修改
:%s/a/b/g 全文把a替换成b
:ls     列出所有buffer
:checkhealth  nvim环境自检
:Tutor  官方入门教程
```

## 五、配置文件

Neovim 主配置文件：`~/.config/nvim/init.lua`（Linux/Mac）

 Windows：`%LOCALAPPDATA%\nvim\init.lua` 可以写 lua 开启行号、缩进、自定义快捷键、加载插件。

## 六、 Neovim 搭建开发环境

lazy.nvim 插件管理器 

Neovim/lazy.nvim **没有官方插件商店**，所有插件本质都是 **GitHub/GitLab 上的开源 git 仓库**

> 方案：**lazy.nvim + mason.nvim + nvim-lspconfig + nvim-jdtls + nvim-cmp + nvim-dap** 能力：语法高亮、自动补全、跳转定义、变量重命名、报错诊断、断点调试、文件检索、文件树 语言 LSP 清单
>
> - Java：**jdtls（必须 nvim-jdtls，不能只用 lspconfig）**
> - C：**clangd**
> - Python：**pyright** 调试器：java-debug-adapter、cppdbg、debugpy Windows 配置路径：`%LOCALAPPDATA%\nvim\init.lua` 等价 `C:\Users\你的用户名\AppData\Local\nvim\init.lua`

