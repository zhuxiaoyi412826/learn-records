---
name: 题库知识
version: 2.0.0
description: Internal DSA problem categories and patterns knowledge base, referenced by the problem generation skill. Integrates 4 classic algorithm books (600+ problems).
description_zh: 数据结构与算法题目分类知识库，整合4本经典算法书籍（600+题目），供出题技能参考，用户不可直接调用。
user-invocable: false
---

# 题库知识库

本技能是内部知识库，包含数据结构与算法的题目分类、常见模式、出题方向参考，以及来自 4 本经典算法书籍的 600+ 道题目索引。由「编程出题」技能在构思题目时引用。

## 使用方式

「编程出题」技能根据用户指定的主题，从下方分类中选取匹配的题目方向。如果用户要求"随机出题"，则从以下分类中随机选取。

**出题时务必查阅书籍题解索引**，确保所出题目不与经典题完全重复，但可以参考其解题思路和技巧进行变体设计。

## 分类索引

### 基础知识分类
- [数据结构分类](references/dsa-categories.md#数据结构)
- [算法分类](references/dsa-categories.md#算法)
- [难度映射](references/dsa-categories.md#难度映射)
- [常见出题模式](references/dsa-categories.md#常见出题模式)

### 书籍题解索引（核心题库）
- [算法书籍题解索引](references/book-reference.md) — 整合 4 本经典书籍，600+ 题目按 19 大类索引
  - 书籍 A：BAT霜神Leetcode刷题笔记（Go, 607题）
  - 书籍 B：LeetCode题解 - Java语言实现（Java, 102题）
  - 书籍 C：LeetCode 101 谷歌高畅刷题笔记（C++, 150+题）
  - 书籍 D：Leetcode1470题解-Go语言实现（Go, 1470题）
- [经典多解法题目 TOP 20](references/book-reference.md#经典多解法题目-top-20) — 适合"多解法对比"出题
- [出题建议](references/book-reference.md#出题建议) — 按难度和多解法类型的推荐
