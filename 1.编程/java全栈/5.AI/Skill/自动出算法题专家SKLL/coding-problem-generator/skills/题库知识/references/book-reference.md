# 算法书籍题解索引

> 本文件整合了 4 本经典 LeetCode 题解书籍的知识，作为出题专家插件的核心题库参考。
> 共计覆盖 600+ 道经典题目，涵盖 19 个算法大类。

## 书籍来源

| 编号 | 书名 | 语言 | 题数 | 特点 |
|------|------|------|------|------|
| A | BAT霜神Leetcode刷题笔记 | Go | 607 | 分类最全面，含复杂度分析和技巧总结 |
| B | LeetCode题解 - Java语言实现 | Java | 102 | 多解法对比，含错误示范和修正 |
| C | LeetCode 101 (谷歌高畅) | C++ | 150+ | 按算法类别分章，含练习题 |
| D | Leetcode1470题解-Go语言实现 | Go | 1470 | 覆盖面最广，Go语言参考 |

## 分类题解索引

### 1. 数组 (Array)

**核心技巧**: 双指针、前缀和、差分数组、矩阵操作、原地操作

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 1 | Two Sum | 简单 | ABCD | HashMap O(n) |
| 11 | Container With Most Water | 中等 | ABC | 双指针，贪心收缩 |
| 15 | 3Sum | 中等 | AB | 排序+双指针，去重 |
| 18 | 4Sum | 中等 | AB | 排序+双指针扩展 |
| 26 | Remove Duplicates from Sorted Array | 简单 | B | 快慢指针 |
| 48 | Rotate Image | 中等 | BC | 转置+翻转 / 分层旋转 |
| 54 | Spiral Matrix | 中等 | AB | 四边界模拟 |
| 56 | Merge Intervals | 中等 | AB | 排序+合并 |
| 73 | Set Matrix Zeroes | 中等 | B | 原地标记法 |
| 75 | Sort Colors | 中等 | BC | 三指针/荷兰国旗 |
| 80 | Remove Duplicates from Sorted Array II | 中等 | B | 计数器+快慢指针 |
| 88 | Merge Sorted Array | 简单 | BC | 从后往前合并 |
| 118 | Pascal's Triangle | 简单 | AB | 逐行构造 |
| 128 | Longest Consecutive Sequence | 困难 | BC | HashSet O(n) |
| 164 | Maximum Gap | 困难 | B | 桶排序思想 |
| 169 | Majority Element | 简单 | BC | Boyer-Moore投票法 |
| 189 | Rotate Array | 中等 | B | 三次翻转法 O(1)空间 |
| 238 | Product of Array Except Self | 中等 | C | 前缀积+后缀积 |
| 283 | Move Zeroes | 简单 | A | 快慢指针 |
| 448 | Find All Numbers Disappeared | 简单 | C | 原地标记法 |
| 560 | Subarray Sum Equals K | 中等 | C | 前缀和+HashMap |
| 566 | Reshape the Matrix | 简单 | A | 索引映射 |

### 2. 字符串 (String)

**核心技巧**: 滑动窗口、双指针、KMP、回文中心扩展、字符串哈希

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 3 | Longest Substring Without Repeating | 中等 | ABC | 滑动窗口+HashSet |
| 5 | Longest Palindromic Substring | 中等 | BC | DP/中心扩展/Manacher 4种解法 |
| 10 | Regular Expression Matching | 困难 | BC | 二维DP |
| 14 | Longest Common Prefix | 简单 | B | 逐字符比较 |
| 20 | Valid Parentheses | 简单 | BC | 栈匹配 |
| 28 | Implement strStr() | 简单 | BC | KMP算法 |
| 38 | Count and Say | 简单 | B | 模拟 |
| 49 | Group Anagrams | 中等 | A | 排序+HashMap |
| 76 | Minimum Window Substring | 困难 | AC | 滑动窗口经典模板 |
| 91 | Decode Ways | 中等 | AC | 一维DP |
| 125 | Valid Palindrome | 简单 | B | 双指针 3种解法 |
| 131 | Palindrome Partitioning | 中等 | AB | 回溯+DP预处理 |
| 151 | Reverse Words in a String | 中等 | AB | 翻转技巧 |
| 205 | Isomorphic Strings | 简单 | AC | 双向映射 |
| 242 | Valid Anagram | 简单 | AC | 字符计数 |
| 340 | Longest Substring At Most K Distinct | 困难 | C | 滑动窗口+HashMap |
| 344 | Reverse String | 简单 | A | 双指针 |
| 387 | First Unique Character | 简单 | A | 字符频率统计 |
| 409 | Longest Palindrome | 简单 | AC | 贪心+字符计数 |
| 438 | Find All Anagrams | 中等 | A | 滑动窗口 |
| 567 | Permutation in String | 中等 | A | 滑动窗口 |
| 647 | Palindromic Substrings | 中等 | C | 中心扩展 |
| 763 | Partition Labels | 中等 | AC | 贪心+区间合并 |
| 767 | Reorganize String | 中等 | A | 贪心+频率排序 |

### 3. 链表 (Linked List)

**核心技巧**: 虚拟头节点、快慢指针、递归、区间翻转

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 2 | Add Two Numbers | 中等 | AB | 模拟进位 |
| 19 | Remove Nth Node From End | 中等 | AC | 快慢指针 |
| 21 | Merge Two Sorted Lists | 简单 | ABC | 迭代/递归 |
| 23 | Merge k Sorted Lists | 困难 | ABC | 分治/优先队列 |
| 24 | Swap Nodes in Pairs | 中等 | AC | 递归/迭代 |
| 25 | Reverse Nodes in k-Group | 困难 | A | 区间翻转 |
| 61 | Rotate List | 中等 | A | 找断点+重连 |
| 82 | Remove Duplicates from Sorted List II | 中等 | A | 虚拟头+跳过重复 |
| 83 | Remove Duplicates from Sorted List | 简单 | AC | 直接删除 |
| 86 | Partition List | 中等 | AB | 双链表拼接 |
| 92 | Reverse Linked List II | 中等 | A | 区间翻转 |
| 138 | Copy List with Random Pointer | 中等 | AB | 交织法/HashMap 3种解法 |
| 141 | Linked List Cycle | 简单 | AB | Floyd判环 |
| 142 | Linked List Cycle II | 中等 | ABC | Floyd+数学推导 |
| 143 | Reorder List | 中等 | AB | 找中点+翻转+合并 |
| 146 | LRU Cache | 中等 | ABC | HashMap+双向链表 |
| 147 | Insertion Sort List | 中等 | AB | 插入排序 |
| 148 | Sort List | 中等 | ABC | 归并排序 |
| 160 | Intersection of Two Linked Lists | 简单 | AB | 双指针等距 |
| 203 | Remove Linked List Elements | 简单 | A | 虚拟头节点 |
| 206 | Reverse Linked List | 简单 | AC | 迭代/递归 |
| 234 | Palindrome Linked List | 简单 | AC | 找中点+翻转+比较 |
| 237 | Delete Node in a Linked List | 简单 | A | 值覆盖 |
| 328 | Odd Even Linked List | 中等 | AC | 双指针分组 |
| 445 | Add Two Numbers II | 中等 | A | 栈辅助 |
| 707 | Design Linked List | 中等 | A | 双向链表实现 |

### 4. 栈与队列 (Stack & Queue)

**核心技巧**: 单调栈、辅助栈、表达式求值、双栈模拟

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 20 | Valid Parentheses | 简单 | BC | 栈匹配 |
| 71 | Simplify Path | 中等 | A | 栈处理路径 |
| 84 | Largest Rectangle in Histogram | 困难 | A | 单调栈经典 |
| 150 | Evaluate Reverse Polish Notation | 中等 | AB | 栈求值 2种解法 |
| 155 | Min Stack | 简单 | ABC | 辅助栈/差值栈 |
| 225 | Implement Stack using Queues | 简单 | AC | 双队列模拟 |
| 232 | Implement Queue using Stacks | 简单 | AC | 双栈模拟 |
| 394 | Decode String | 中等 | A | 栈处理嵌套 |
| 456 | 132 Pattern | 中等 | A | 单调栈 |
| 496 | Next Greater Element I | 简单 | A | 单调栈+HashMap |
| 503 | Next Greater Element II | 中等 | AC | 循环单调栈 |
| 682 | Baseball Game | 简单 | A | 栈模拟 |
| 739 | Daily Temperatures | 中等 | AC | 单调栈经典 |
| 856 | Score of Parentheses | 中等 | A | 栈/数学 |
| 901 | Online Stock Span | 中等 | A | 单调栈 |
| 907 | Sum of Subarray Minimums | 中等 | A | 单调栈+贡献法 |

### 5. 二叉树 (Binary Tree)

**核心技巧**: 递归遍历、迭代遍历、路径和问题、LCA、BST性质

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 94 | Binary Tree Inorder Traversal | 中等 | ABC | 迭代+递归 |
| 95 | Unique BST II | 中等 | A | 递归构造 |
| 96 | Unique BST | 中等 | A | 卡特兰数/DP |
| 98 | Validate BST | 中等 | ABC | 中序遍历/递归边界 |
| 99 | Recover BST | 困难 | AC | 中序遍历找逆序 |
| 100 | Same Tree | 简单 | A | 递归比较 |
| 101 | Symmetric Tree | 简单 | ABC | 递归/迭代 |
| 102 | Level Order Traversal | 中等 | A | BFS队列 |
| 103 | Zigzag Level Order | 中等 | A | BFS+翻转 |
| 104 | Maximum Depth | 简单 | ABC | 递归 |
| 105 | Construct from Preorder+Inorder | 中等 | ABC | 递归+HashMap |
| 106 | Construct from Inorder+Postorder | 中等 | ABC | 递归 |
| 108 | Convert Sorted Array to BST | 简单 | ABC | 递归取中点 |
| 109 | Convert Sorted List to BST | 中等 | ABC | 快慢指针+递归 |
| 110 | Balanced Binary Tree | 简单 | ABC | 递归高度差 |
| 111 | Minimum Depth | 简单 | ABC | BFS/DFS |
| 112 | Path Sum | 简单 | AB | DFS/BFS 2种解法 |
| 113 | Path Sum II | 中等 | A | DFS回溯 |
| 114 | Flatten BT to Linked List | 中等 | AB | 前序+重连 |
| 124 | BT Maximum Path Sum | 困难 | AB | 递归+全局变量 2种解法 |
| 129 | Sum Root to Leaf Numbers | 中等 | A | DFS |
| 144 | Preorder Traversal | 中等 | ABC | 迭代+递归 |
| 145 | Postorder Traversal | 困难 | ABC | 迭代(双栈)+递归 |
| 199 | BT Right Side View | 中等 | A | BFS/DFS |
| 222 | Count Complete Tree Nodes | 中等 | A | 二分+位运算 |
| 226 | Invert Binary Tree | 简单 | AC | 递归 |
| 235 | LCA of BST | 简单 | AC | BST性质 |
| 236 | LCA of BT | 中等 | AC | 递归经典 |
| 257 | Binary Tree Paths | 简单 | AC | DFS回溯 |
| 404 | Sum of Left Leaves | 简单 | AC | DFS |
| 437 | Path Sum III | 中等 | AC | 前缀和+DFS |
| 450 | Delete Node in BST | 中等 | C | 递归+替换 |
| 513 | Find Bottom Left Value | 中等 | AC | BFS/DFS |
| 538 | Convert BST to Greater Tree | 简单 | AC | 反向中序遍历 |
| 543 | Diameter of BT | 简单 | C | 递归+全局变量 |
| 572 | Subtree of Another Tree | 简单 | AC | 递归匹配 |
| 617 | Merge Two BT | 简单 | C | 递归合并 |
| 637 | Average of Levels | 简单 | AC | BFS |
| 653 | Two Sum IV - BST | 简单 | AC | HashSet/双指针 |
| 662 | Maximum Width | 中等 | A | BFS编号 |
| 669 | Trim BST | 中等 | AC | 递归裁剪 |
| 889 | Construct from Pre+Post | 中等 | C | 递归 |
| 897 | Increasing Order Search Tree | 简单 | C | 中序遍历重连 |

### 6. 动态规划 (Dynamic Programming)

**核心技巧**: 状态定义、转移方程、空间优化、记忆化搜索

#### 6.1 一维DP / Fibonacci型

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 53 | Maximum Subarray | 简单 | ABC | Kadane算法 3种解法 |
| 70 | Climbing Stairs | 简单 | AC | Fibonacci |
| 121 | Best Time Buy Sell Stock | 简单 | ABC | 贪心/DP |
| 122 | Best Time Buy Sell Stock II | 简单 | AC | 贪心 |
| 123 | Best Time Buy Sell Stock III | 困难 | B | 状态机DP |
| 139 | Word Break | 中等 | BC | DP+字典 4种解法 |
| 152 | Maximum Product Subarray | 中等 | AB | 维护min/max 2种解法 |
| 188 | Best Time Buy Sell Stock IV | 困难 | BC | 状态机DP+空间优化 |
| 198 | House Robber | 中等 | AC | 滚动DP |
| 213 | House Robber II | 中等 | AC | 环形拆分为两个线性 |
| 279 | Perfect Squares | 中等 | C | 完全背包/BFS |
| 300 | Longest Increasing Subsequence | 中等 | AC | DP O(n²)+二分 O(nlogn) |
| 309 | Buy Sell Stock with Cooldown | 中等 | AC | 状态机DP |
| 337 | House Robber III | 中等 | A | 树形DP |
| 343 | Integer Break | 中等 | AC | DP/数学 |
| 413 | Arithmetic Slices | 中等 | C | 差分+计数 |
| 714 | Buy Sell Stock with Fee | 中等 | AC | 状态机DP |

#### 6.2 二维DP / 矩阵DP

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 10 | Regular Expression Matching | 困难 | BC | 二维DP |
| 62 | Unique Paths | 中等 | A | 组合数学/DP |
| 63 | Unique Paths II | 中等 | A | 障碍DP |
| 64 | Minimum Path Sum | 中等 | AC | 矩阵DP |
| 72 | Edit Distance | 困难 | ABC | 经典二维DP |
| 115 | Distinct Subsequences | 困难 | B | 子序列DP 2种解法 |
| 120 | Triangle | 中等 | B | 自底向上DP 2种解法 |
| 221 | Maximal Square | 中等 | C | DP边长递推 |
| 304 | Range Sum Query 2D | 中等 | C | 二维前缀和 |
| 542 | 01 Matrix | 中等 | AC | 多源BFS/DP |

#### 6.3 背包问题

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 322 | Coin Change | 中等 | AC | 完全背包 |
| 377 | Combination Sum IV | 中等 | A | 完全背包(有序) |
| 416 | Partition Equal Subset Sum | 中等 | AC | 0-1背包 |
| 474 | Ones and Zeroes | 中等 | AC | 二维0-1背包 |
| 494 | Target Sum | 中等 | AC | 0-1背包变体 |
| 518 | Coin Change II | 中等 | A | 完全背包计数 |

#### 6.4 字符串DP / 区间DP

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 32 | Longest Valid Parentheses | 困难 | A | 栈+DP |
| 516 | Longest Palindromic Subsequence | 中等 | A | 区间DP |
| 583 | Delete Operation for Two Strings | 中等 | C | LCS变体 |
| 650 | 2 Keys Keyboard | 中等 | C | 因式分解/DP |
| 1143 | Longest Common Subsequence | 中等 | C | 经典LCS |
| 312 | Burst Balloons | 困难 | C | 区间DP |
| 354 | Russian Doll Envelopes | 困难 | A | LIS+排序 |

### 7. 贪心算法 (Greedy)

**核心技巧**: 局部最优→全局最优、排序+贪心、区间调度

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 55 | Jump Game | 中等 | AB | 贪心可达性 |
| 122 | Best Time Buy Sell Stock II | 简单 | AC | 贪心累加正差 |
| 134 | Gas Station | 中等 | B | 贪心+前缀和 |
| 135 | Candy | 困难 | BC | 两次遍历 |
| 406 | Queue Reconstruction by Height | 中等 | C | 排序+插入 |
| 435 | Non-overlapping Intervals | 中等 | AC | 区间调度贪心 |
| 452 | Min Arrows to Burst Balloons | 中等 | C | 区间贪心 |
| 455 | Assign Cookies | 简单 | AC | 排序+双指针 |
| 605 | Can Place Flowers | 简单 | AC | 贪心种植 |
| 665 | Non-decreasing Array | 简单 | C | 贪心修改 |
| 881 | Boats to Save People | 中等 | A | 排序+双指针 |

### 8. 回溯 (Backtracking)

**核心技巧**: 递归树、剪枝、去重、状态恢复

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 17 | Letter Combinations | 中等 | A | 回溯+映射表 |
| 22 | Generate Parentheses | 中等 | AB | 回溯计数 |
| 37 | Sudoku Solver | 困难 | AC | 回溯+约束 |
| 39 | Combination Sum | 中等 | AC | 回溯(可重复选) |
| 40 | Combination Sum II | 中等 | AC | 回溯+去重 |
| 46 | Permutations | 中等 | ABC | 回溯used数组/交换 2种解法 |
| 47 | Permutations II | 中等 | ABC | 回溯+去重 |
| 51 | N-Queens | 困难 | AC | 回溯+位运算优化 |
| 52 | N-Queens II | 困难 | A | 回溯计数 |
| 60 | Permutation Sequence | 困难 | AB | 数学推导 2种解法 |
| 77 | Combinations | 中等 | AC | 回溯 |
| 78 | Subsets | 中等 | A | 回溯/位运算 |
| 79 | Word Search | 中等 | AC | 网格回溯 |
| 89 | Gray Code | 中等 | A | 格雷码规律 |
| 90 | Subsets II | 中等 | A | 回溯+去重 |
| 93 | Restore IP Addresses | 中等 | A | 回溯分割 |
| 131 | Palindrome Partitioning | 中等 | AB | 回溯+DP预处理 |
| 216 | Combination Sum III | 中等 | AC | 回溯 |
| 306 | Additive Number | 中等 | A | 回溯+大数加法 |
| 784 | Letter Case Permutation | 中等 | A | 回溯/位运算 |

### 9. 二分查找 (Binary Search)

**核心技巧**: 搜索区间、四种变体(首个/末个/插入点/旋转)

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 4 | Median of Two Sorted Arrays | 困难 | BC | 二分划分 |
| 33 | Search in Rotated Sorted Array | 中等 | A | 二分+判断有序半段 |
| 34 | Find First and Last Position | 中等 | AC | 二分找上下界 |
| 35 | Search Insert Position | 简单 | B | 二分下界 |
| 69 | Sqrt(x) | 简单 | AC | 二分 |
| 74 | Search a 2D Matrix | 中等 | AB | 二维转一维二分 |
| 81 | Search in Rotated Array II | 中等 | AC | 含重复元素 |
| 153 | Find Min in Rotated Array | 中等 | AB | 二分 |
| 154 | Find Min in Rotated Array II | 中等 | AC | 含重复 |
| 162 | Find Peak Element | 中等 | AB | 二分 |
| 167 | Two Sum II (sorted) | 简单 | BC | 双指针/二分 |
| 240 | Search a 2D Matrix II | 中等 | AC | Z形搜索 |
| 275 | H-Index II | 中等 | A | 二分 |
| 287 | Find the Duplicate Number | 中等 | AC | 二分/快慢指针 |
| 367 | Valid Perfect Square | 简单 | A | 二分 |
| 378 | Kth Smallest in Sorted Matrix | 中等 | A | 二分+计数 |
| 540 | Single Element in Sorted Array | 中等 | C | 二分奇偶位 |
| 704 | Binary Search | 简单 | A | 基础模板 |
| 852 | Peak Index in Mountain Array | 简单 | A | 二分 |
| 875 | Koko Eating Bananas | 中等 | A | 二分答案 |

### 10. DFS / BFS (搜索)

**核心技巧**: 网格遍历、拓扑排序、连通分量、最短路

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 126 | Word Ladder II | 困难 | AC | BFS+回溯 |
| 127 | Word Ladder | 困难 | AB | BFS最短路径 2种解法 |
| 130 | Surrounded Regions | 中等 | AC | DFS/BFS边界 |
| 133 | Clone Graph | 中等 | B | BFS+HashMap |
| 200 | Number of Islands | 中等 | A | DFS/BFS |
| 207 | Course Schedule | 中等 | A | 拓扑排序/DFS环检测 |
| 210 | Course Schedule II | 中等 | AC | 拓扑排序 |
| 310 | Minimum Height Trees | 中等 | C | BFS剥叶子 |
| 329 | Longest Increasing Path in Matrix | 困难 | A | DFS+记忆化 |
| 417 | Pacific Atlantic Water Flow | 中等 | C | 多源DFS |
| 547 | Number of Provinces | 中等 | AC | DFS/BFS/并查集 |
| 695 | Max Area of Island | 中等 | AC | DFS |
| 733 | Flood Fill | 简单 | A | DFS/BFS |
| 785 | Is Graph Bipartite? | 中等 | AC | BFS/DFS着色 |
| 841 | Keys and Rooms | 中等 | A | DFS/BFS |
| 934 | Shortest Bridge | 中等 | C | DFS+BFS |
| 994 | Rotting Oranges | 中等 | A | 多源BFS |

### 11. 位运算 (Bit Manipulation)

**核心技巧**: XOR、位计数、位掩码、lowbit

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 136 | Single Number | 简单 | BC | XOR |
| 137 | Single Number II | 中等 | AB | 位计数/状态机 |
| 190 | Reverse Bits | 简单 | BC | 位翻转 |
| 191 | Number of 1 Bits | 简单 | AB | 位计数 |
| 260 | Single Number III | 中等 | AC | XOR分组 |
| 268 | Missing Number | 简单 | AC | XOR/求和 |
| 318 | Max Product of Word Lengths | 中等 | C | 位掩码 |
| 338 | Counting Bits | 中等 | AC | DP+位运算 |
| 342 | Power of Four | 简单 | AC | 位运算 |
| 371 | Sum of Two Integers | 中等 | A | 位运算模拟加法 |
| 421 | Maximum XOR of Two Numbers | 中等 | A | Trie+贪心 |
| 461 | Hamming Distance | 简单 | AC | XOR+计数 |
| 476 | Number Complement | 简单 | AC | 位翻转 |
| 693 | Alternating Bits | 简单 | AC | 位模式检测 |

### 12. 哈希表 (Hash Table)

**核心技巧**: 频率统计、前缀和+HashMap、设计类

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 1 | Two Sum | 简单 | ABCD | HashMap |
| 49 | Group Anagrams | 中等 | A | 排序键+HashMap |
| 128 | Longest Consecutive Sequence | 困难 | BC | HashSet O(n) |
| 149 | Max Points on a Line | 困难 | C | 斜率HashMap |
| 217 | Contains Duplicate | 简单 | AC | HashSet |
| 347 | Top K Frequent Elements | 中等 | AC | HashMap+桶排序/堆 |
| 350 | Intersection of Two Arrays II | 简单 | A | HashMap |
| 451 | Sort Characters By Frequency | 中等 | AC | HashMap+排序 |
| 560 | Subarray Sum Equals K | 中等 | C | 前缀和+HashMap |
| 594 | Longest Harmonious Subsequence | 简单 | C | HashMap |
| 705 | Design HashSet | 简单 | A | 数组+链表 |
| 706 | Design HashMap | 简单 | A | 数组+链表 |

### 13. 排序 (Sorting)

**核心技巧**: 快排/归并/堆排、自定义比较、桶排序/计数排序

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 56 | Merge Intervals | 中等 | AB | 排序+合并 |
| 57 | Insert Interval | 中等 | AB | 排序+插入 |
| 75 | Sort Colors | 中等 | BC | 三路快排/计数 |
| 147 | Insertion Sort List | 中等 | AB | 插入排序 |
| 148 | Sort List | 中等 | ABC | 归并排序链表 |
| 164 | Maximum Gap | 困难 | AB | 桶排序 |
| 179 | Largest Number | 中等 | AB | 自定义比较器 |
| 215 | Kth Largest Element | 中等 | AC | 快选/堆 |
| 315 | Count of Smaller After Self | 困难 | AC | 归并排序/树状数组 |
| 324 | Wiggle Sort II | 中等 | A | 三路划分 |
| 347 | Top K Frequent | 中等 | AC | 桶排序/堆 |
| 451 | Sort Characters | 中等 | AC | 频率排序 |

### 14. 数学 (Math)

**核心技巧**: 质数筛、GCD/LCM、快速幂、进制转换

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 7 | Reverse Integer | 简单 | B | 溢出处理 4种解法 |
| 9 | Palindrome Number | 简单 | B | 数字翻转 |
| 50 | Pow(x,n) | 中等 | AB | 快速幂 4种解法 |
| 67 | Add Binary | 简单 | AC | 模拟进位 |
| 168 | Excel Sheet Column Title | 简单 | AC | 26进制 |
| 172 | Factorial Trailing Zeroes | 简单 | AC | 因子5计数 |
| 202 | Happy Number | 简单 | AC | Floyd判环 |
| 204 | Count Primes | 简单 | AC | 埃氏筛 |
| 231 | Power of Two | 简单 | A | 位运算 |
| 326 | Power of Three | 简单 | AC | 数学/取模 |
| 384 | Shuffle an Array | 中等 | C | Fisher-Yates |
| 470 | Implement Rand10() Using Rand7() | 中等 | AC | 拒绝采样 |
| 528 | Random Pick with Weight | 中等 | AC | 前缀和+二分 |

### 15. 图论 (Graph)

**核心技巧**: BFS/DFS、拓扑排序、最短路、最小生成树、二分图

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 207 | Course Schedule | 中等 | A | 拓扑排序/环检测 |
| 210 | Course Schedule II | 中等 | AC | 拓扑排序 |
| 332 | Reconstruct Itinerary | 中等 | C | 欧拉路径 |
| 399 | Evaluate Division | 中等 | A | 带权并查集/BFS |
| 785 | Is Graph Bipartite? | 中等 | AC | BFS/DFS着色 |
| 882 | Reachable Nodes in Subdivided Graph | 困难 | C | Dijkstra |
| 1135 | Connecting Cities Min Cost | 中等 | C | Kruskal MST |

### 16. 并查集 (Union Find)

**核心技巧**: 路径压缩、按秩合并、连通分量计数

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 128 | Longest Consecutive Sequence | 困难 | BC | HashSet/并查集 |
| 200 | Number of Islands | 中等 | A | DFS/并查集 |
| 305 | Number of Islands II | 困难 | A | 动态并查集 |
| 399 | Evaluate Division | 中等 | A | 带权并查集 |
| 547 | Number of Provinces | 中等 | AC | 并查集计数 |
| 684 | Redundant Connection | 中等 | AC | 环检测 |
| 685 | Redundant Connection II | 困难 | A | 有向图并查集 |
| 721 | Accounts Merge | 中等 | A | 并查集合并 |
| 765 | Couples Holding Hands | 困难 | A | 并查集 |

### 17. 滑动窗口 (Sliding Window)

**核心技巧**: 定长/变长窗口、窗口收缩条件、HashMap辅助

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 3 | Longest Substring Without Repeating | 中等 | ABC | 滑动窗口+HashSet |
| 76 | Minimum Window Substring | 困难 | AC | 滑动窗口模板 |
| 209 | Minimum Size Subarray Sum | 中等 | A | 滑动窗口 |
| 239 | Sliding Window Maximum | 困难 | AC | 单调队列 |
| 340 | Longest Substring At Most K Distinct | 困难 | C | HashMap+窗口 |
| 424 | Longest Repeating Character Replacement | 中等 | A | 窗口+最大频率 |
| 438 | Find All Anagrams | 中等 | A | 定长窗口 |
| 480 | Sliding Window Median | 困难 | A | 双堆 |
| 567 | Permutation in String | 中等 | A | 定长窗口 |
| 713 | Subarray Product Less Than K | 中等 | A | 窗口+乘积 |

### 18. 高级数据结构 (Advanced)

#### 18.1 Trie (字典树)

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 208 | Implement Trie | 中等 | AC | 标准Trie |
| 211 | Design Add and Search Words | 中等 | A | Trie+DFS |
| 212 | Word Search II | 困难 | A | Trie+回溯 |
| 421 | Maximum XOR of Two Numbers | 中等 | A | 0-1 Trie |
| 745 | Prefix and Suffix Search | 困难 | A | 双Trie |

#### 18.2 线段树 / 树状数组

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 218 | The Skyline Problem | 困难 | AC | 线段树/扫描线 |
| 307 | Range Sum Query - Mutable | 中等 | AC | 线段树/树状数组 |
| 315 | Count Smaller After Self | 困难 | AC | 归并/树状数组 |
| 327 | Count of Range Sum | 困难 | A | 树状数组 |
| 493 | Reverse Pairs | 困难 | A | 归并/树状数组 |
| 699 | Falling Squares | 困难 | A | 线段树 |
| 715 | Range Module | 困难 | A | 动态线段树 |

#### 18.3 设计类

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 146 | LRU Cache | 中等 | ABC | HashMap+双向链表 |
| 380 | Insert Delete GetRandom O(1) | 中等 | C | 数组+HashMap |
| 432 | All O'one Data Structure | 困难 | C | 双向链表+HashMap |
| 460 | LFU Cache | 困难 | A | 双HashMap+频率链表 |

### 19. 分治 (Divide and Conquer)

| LC# | 题目 | 难度 | 来源 | 关键解法/备注 |
|-----|------|------|------|---------------|
| 23 | Merge k Sorted Lists | 困难 | ABC | 分治合并 |
| 53 | Maximum Subarray | 简单 | ABC | 分治/Kadane |
| 241 | Different Ways to Add Parentheses | 中等 | C | 递归分治 |
| 312 | Burst Balloons | 困难 | C | 区间DP(分治思想) |
| 395 | Longest Substring with At Least K Repeating | 中等 | A | 分治 |
| 932 | Beautiful Array | 中等 | C | 分治构造 |

## 经典多解法题目 TOP 20

以下题目在多本书中均有 2+ 种解法，适合用于"多解法对比"出题：

| LC# | 题目 | 解法数 | 解法概览 |
|-----|------|--------|----------|
| 5 | Longest Palindromic Substring | 4 | 暴力O(n³)→DP O(n²)→中心扩展→Manacher O(n) |
| 139 | Word Break | 4 | 暴力→DP→正则→扩展讨论 |
| 189 | Rotate Array | 3 | 中间数组→冒泡旋转→三次翻转 |
| 7 | Reverse Integer | 4 | 朴素→优化→简洁→溢出处理 |
| 50 | Pow(x,n) | 4 | 朴素→递归→快速幂→最优 |
| 125 | Valid Palindrome | 3 | 朴素→栈→双指针 |
| 46 | Permutations | 2 | used数组法→交换法 |
| 72 | Edit Distance | 3 | 记忆化→标准DP→空间优化 |
| 138 | Copy List Random Pointer | 3 | 错误示范→交织法→HashMap |
| 141 | Linked List Cycle | 2 | HashSet→Floyd判环 |
| 152 | Maximum Product Subarray | 2 | 暴力→维护min/max |
| 120 | Triangle | 2 | 自顶向下(错误)→自底向上(正确) |
| 300 | LIS | 2 | DP O(n²)→贪心+二分 O(nlogn) |
| 55 | Jump Game | 2 | DP→贪心 |
| 148 | Sort List | 2 | 归并排序→快速排序 |
| 23 | Merge k Sorted Lists | 3 | 暴力→优先队列→分治 |
| 215 | Kth Largest | 2 | 堆→快速选择 |
| 287 | Find Duplicate | 2 | 二分→Floyd判环 |
| 1 | Two Sum | 2 | 暴力O(n²)→HashMap O(n) |
| 322 | Coin Change | 2 | DP→BFS |

## 出题建议

### 按难度推荐

- **简单**: 优先从 Two Sum、Valid Parentheses、Merge Lists、Climbing Stairs、Maximum Depth 等经典入门题变形
- **中等**: 优先从 LRU Cache、3Sum、Word Break、Course Schedule、Longest Palindromic Substring 等面试高频题变形
- **困难**: 优先从 Edit Distance、N-Queens、Median of Two Sorted Arrays、Trapping Rain Water 等思维深度题变形

### 按多解法对比推荐

- 需要展示 **暴力→优化** 递进: Two Sum, Maximum Subarray, LIS, Longest Palindromic Substring
- 需要展示 **不同数据结构** 选择: Linked List Cycle(HashSet vs Floyd), Kth Largest(堆 vs 快选)
- 需要展示 **空间换时间** 优化: Edit Distance(2D→1D), House Robber(数组→滚动变量)
