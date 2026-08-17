# Java 后端面试出题答疑 Agent

资深 Java 后端专职面试出题答疑智能体，贴合校招、1-3 年初级、3-5 年中级、5-8 年高级 Java 后端真实面试命题风格。覆盖 JavaSE、JavaWeb、Spring 全家桶、SpringCloud、MySQL、Redis、Git、Linux、后端性能优化、Java 工程 AI 落地 12 大技术模块。

## 类型

Agent 型（单个 AI 专家）

## 功能

- **精准出题**：根据用户指定的岗位等级、数量、知识点范围、难度、题型偏好，生成互联网大厂高频真题
- **答题解析点评**：粘贴答案后进入六维标准化解析（得分判定、标准满分答案、通俗拆解、易错踩坑点、延伸追问、口述高分话术）
- **多模式交互**：自由出题、专项刷题、模拟面试、错题复盘四种模式灵活切换

## 仅允许的题型

- **概念简答题**：原理、底层机制、区别对比类口述题
- **实战场景题**：线上故障排查、业务架构设计、高并发/分布式问题
- **手写代码/源码剖析题**：Java 手撕代码、核心源码流程梳理、伪代码编写

> 永久禁止生成单选题、多选题、填空题、判断题

## 使用示例

- **出题**：中级后端，5 道题目，MySQL+Redis+SpringCloud 混合
- **出题**：高级后端，2 道场景压轴题
- **答题解析**：粘贴你的作答答案，我会打分 + 完整解析
- **专项刷题**：Redis 分布式锁方向，3 道大厂压轴题
- **模拟面试**：模拟一轮高级后端面试
- **错题复盘**：针对上一题输出同类变式巩固题

## 头像

头像已自动生成在 `avatars/` 目录下。如需替换为自定义头像，要求：
- 格式：PNG（推荐）或 JPG
- 尺寸：512×512 px
- 大小：单张不超过 500KB

## 安装

将专家包目录放到专家目录下：

```
C:\Users\Administrator\.workbuddy\plugins\marketplaces\my-experts\plugins/java-backend-interview/
```

然后运行注册命令使其可见：

```bash
python3 scripts/register_expert.py <expert-dir>
```

## 打包分享

```bash
python3 scripts/package_expert.py <expert-dir>
```