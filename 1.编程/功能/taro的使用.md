### 一、环境准备

1. Node.js

   ：≥ 18.0.0（推荐 LTS 版）

   ```
   node -v  # 检查版本
   ```

2. **包管理器**：npm/yarn/pnpm（推荐 pnpm）

### 二、安装 Taro CLI

```
# npm
npm install -g @tarojs/cli
# yarn
yarn global add @tarojs/cli
# pnpm
pnpm add -g @tarojs/cli

# 验证（输出版本即成功）
taro --version  # 应显示 Taro v4.x.x
```

### 三、创建项目

```
# 初始化项目（交互式选择配置）
taro init my-taro4-project

# 或用npx（无需全局安装）
npx @tarojs/cli init my-taro4-project
```

### 四、启动项目

**交互式配置参考**：

- 框架：React（推荐）
- TypeScript：Yes
- CSS 预处理器：Sass
- 包管理器：pnpm
- 编译工具：Webpack5/Vite（Taro4 支持 Vite）

```
# 进入项目目录
cd my-taro4-project

# 安装依赖（init自动执行，失败时手动）
pnpm install

# 启动微信小程序（开发模式）
pnpm dev:weapp

# 启动H5
pnpm dev:h5

# 打包微信小程序（生产模式）
pnpm build:weapp
```

### 五、平台预览

- **微信小程序**：打开微信开发者工具 → 导入项目 → 选择`dist/weapp`目录
- **H5**：浏览器打开终端输出的地址（如`http://localhost:10086`

### 六、目录结构（关键）

plaintext

```
my-taro4-project/
├── src/                # 源码
│   ├── pages/          # 页面
│   └── app.tsx         # 入口
├── config/             # 配置
│   └── index.ts        # 全局配置
└── dist/               # 编译产物
    ├── weapp/          # 小程序代码
    └── h5/             # H5代码
```

### 七、安装H5缺失的依赖

pnpm add @tarojs/plugin-platform-h5 @tailwindcss/postcss --force