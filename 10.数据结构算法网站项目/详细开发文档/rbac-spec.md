# RBAC 角色权限体系完整规格说明书

> 版本：v2.0 · 补全版（含 EFK 二级管理、数据分析管理员、审核二级管理员）

---

## 目录

- [一、文档概述](#一文档概述)
- [二、完整角色体系总览](#二完整角色体系总览)
- [三、角色详细规格](#三角色详细规格)
  - [3.1 超级管理员](#31-超级管理员)
  - [3.2 一级管理员](#32-一级管理员)
  - [3.3 权限分配子管理员](#33-权限分配子管理员)
  - [3.4 内容发布管理员（含二级）](#34-内容发布管理员含二级)
  - [3.5 内容审核管理员（含二级）](#35-内容审核管理员含二级)
  - [3.6 EFK 日志分析管理员（含二级）](#36-efk-日志分析管理员含二级)
  - [3.7 数据分析管理员（含二级）](#37-数据分析管理员含二级)
  - [3.8 订单分析管理员（含二级）](#38-订单分析管理员含二级)
  - [3.9 运营活动管理员](#39-运营活动管理员)
  - [3.10 财务管理员](#310-财务管理员)
  - [3.11 客服管理员](#311-客服管理员)
  - [3.12 安全审计管理员](#312-安全审计管理员)
  - [3.13 外部出题人](#313-外部出题人)
- [四、前台普通用户端权限](#四前台普通用户端权限)
- [五、MySQL 数据库表结构](#五mysql-数据库表结构)
- [六、权限矩阵](#六权限矩阵)
- [七、关键安全约束汇总](#七关键安全约束汇总)

---

## 一、文档概述

### 1.1 设计原则

| 原则 | 说明 |
|------|------|
| 菜单后端动态生成 | 没有权限直接不返回菜单；手动访问 URL 后端返回 403 |
| 三层权限模型 | 菜单可见性 + 页面内按钮权限 + 行级数据权限 |
| 通用菜单 | 所有角色都可见「个人中心、修改密码」 |
| 审计日志隔离 | 仅超级管理员、安全审计管理员可见，其他角色看不到 |
| 后端接口强校验 | 所有后台接口必须做权限校验，不能只靠前端隐藏菜单、按钮 |

### 1.2 权限三层模型

```
┌─────────────────────────────────────────────────────┐
│  第一层：菜单可见性    后端按角色返回菜单树，前端渲染  │
├─────────────────────────────────────────────────────┤
│  第二层：按钮级权限    页面内操作按钮按 perm_code 控制 │
├─────────────────────────────────────────────────────┤
│  第三层：行级数据权限  按规则过滤可见数据行           │
│           - ALL：全部数据                            │
│           - SELF：仅本人创建的数据                   │
│           - DEPT：本部门数据                         │
│           - CUSTOM：自定义 SQL 条件                  │
└─────────────────────────────────────────────────────┘
```

### 1.3 本次补全内容

| 补全项 | 说明 |
|--------|------|
| EFK 日志管理二级拆分 | 原 EFK 日志分析管理员下新增 4 个二级角色：业务日志、开发日志、系统日志、运维管理 |
| 数据分析管理员体系升级 | 原数据报表只读管理员升级为数据分析管理员（一级），新增 3 个二级分析师角色 |
| 审核管理员二级拆分 | 原内容审核管理员下新增 2 个二级角色：关键词审核、题解评论审核 |

---

## 二、完整角色体系总览

### 2.1 角色层级图

```
超级管理员 (SUPER_ADMIN)
├── 一级管理员 (LEVEL1_ADMIN)
│   ├── 权限分配子管理员 (PERM_MANAGER)
│   ├── 内容发布管理员 (CONTENT_PUBLISHER)
│   │   ├── 二级-数据结构发布管理员 (DS_PUBLISHER)
│   │   ├── 二级-算法发布管理员 (ALGO_PUBLISHER)
│   │   └── 二级-通用子管理员 (GENERAL_SUB_ADMIN)
│   ├── 内容审核管理员 (CONTENT_AUDITOR)
│   │   ├── 题目校验测试管理员 (TEST_VALIDATOR)
│   │   ├── 二级-关键词审核管理员 (KEYWORD_AUDITOR)          [新增]
│   │   └── 二级-题解评论审核管理员 (SOLUTION_COMMENT_AUDITOR)[新增]
│   ├── EFK日志分析管理员 (EFK_LOG_ADMIN)
│   │   ├── 二级-业务日志管理员 (BIZ_LOG_ADMIN)              [新增]
│   │   ├── 二级-开发日志管理员 (DEV_LOG_ADMIN)              [新增]
│   │   ├── 二级-系统日志管理员 (SYS_LOG_ADMIN)              [新增]
│   │   └── 二级-运维管理员 (OPS_LOG_ADMIN)                  [新增]
│   ├── 数据分析管理员 (DATA_ANALYST)                        [升级]
│   │   ├── 二级-UV/PV数据分析师 (UV_PV_ANALYST)             [新增]
│   │   ├── 二级-用户留存数据分析师 (RETENTION_ANALYST)      [新增]
│   │   └── 二级-订单数据分析师 (ORDER_ANALYST_SUB)          [新增]
│   ├── 订单分析管理员 (ORDER_ADMIN)
│   │   ├── 商品管理员 (PRODUCT_ADMIN)
│   │   └── 金币管理员 (COIN_ADMIN)
│   ├── 运营活动管理员 (OPS_ADMIN)
│   ├── 财务管理员 (FINANCE_ADMIN)
│   └── 客服管理员 (CS_ADMIN)
├── 安全审计管理员 (SECURITY_AUDITOR) [独立只读]
外部出题人 (EXTERNAL_AUTHOR) [外部角色]
```

### 2.2 角色一览表

| ID | 角色编码 | 角色名称 | 层级 | 父角色 | 数据范围 | 标记 |
|----|----------|----------|------|--------|----------|------|
| 1 | SUPER_ADMIN | 超级管理员 | 超级 | - | 全部 | - |
| 2 | LEVEL1_ADMIN | 一级管理员 | 一级 | 1 | 全部 | - |
| 3 | PERM_MANAGER | 权限分配子管理员 | 二级 | 2 | 本部门 | - |
| 4 | CONTENT_PUBLISHER | 内容发布管理员 | 一级 | 2 | 全部 | - |
| 5 | CONTENT_AUDITOR | 内容审核管理员 | 一级 | 2 | 全部 | - |
| 6 | EFK_LOG_ADMIN | EFK日志分析管理员 | 一级 | 2 | 全部 | - |
| 7 | DATA_ANALYST | 数据分析管理员 | 一级 | 2 | 全部 | **升级** |
| 8 | ORDER_ADMIN | 订单分析管理员 | 一级 | 2 | 全部 | - |
| 9 | OPS_ADMIN | 运营活动管理员 | 一级 | 2 | 全部 | - |
| 10 | FINANCE_ADMIN | 财务管理员 | 一级 | 2 | 全部 | - |
| 11 | CS_ADMIN | 客服管理员 | 一级 | 2 | 全部 | - |
| 12 | SECURITY_AUDITOR | 安全审计管理员 | 一级 | 1 | 全部 | 独立只读 |
| 13 | EXTERNAL_AUTHOR | 外部出题人 | 二级 | - | 本人 | - |
| 14 | DS_PUBLISHER | 二级-数据结构发布管理员 | 二级 | 4 | 本人 | - |
| 15 | ALGO_PUBLISHER | 二级-算法发布管理员 | 二级 | 4 | 本人 | - |
| 16 | GENERAL_SUB_ADMIN | 二级-通用子管理员 | 二级 | 4 | 本人 | - |
| 17 | TEST_VALIDATOR | 题目校验测试管理员 | 二级 | 5 | 全部 | - |
| 18 | KEYWORD_AUDITOR | 二级-关键词审核管理员 | 二级 | 5 | 全部 | **新增** |
| 19 | SOLUTION_COMMENT_AUDITOR | 二级-题解评论审核管理员 | 二级 | 5 | 全部 | **新增** |
| 20 | BIZ_LOG_ADMIN | 二级-业务日志管理员 | 二级 | 6 | 全部 | **新增** |
| 21 | DEV_LOG_ADMIN | 二级-开发日志管理员 | 二级 | 6 | 全部 | **新增** |
| 22 | SYS_LOG_ADMIN | 二级-系统日志管理员 | 二级 | 6 | 全部 | **新增** |
| 23 | OPS_LOG_ADMIN | 二级-运维管理员 | 二级 | 6 | 全部 | **新增** |
| 24 | UV_PV_ANALYST | 二级-UV/PV数据分析师 | 二级 | 7 | 全部 | **新增** |
| 25 | RETENTION_ANALYST | 二级-用户留存数据分析师 | 二级 | 7 | 全部 | **新增** |
| 26 | ORDER_ANALYST_SUB | 二级-订单数据分析师 | 二级 | 7 | 全部 | **新增** |
| 27 | PRODUCT_ADMIN | 商品管理员 | 二级 | 8 | 全部 | - |
| 28 | COIN_ADMIN | 金币管理员 | 二级 | 8 | 本人 | - |

---

## 三、角色详细规格

### 3.1 超级管理员

> 运维应急，不日常业务操作。全部菜单可见，全部按钮可用，可以新增/删除所有账号角色，可以看全部数据。

**菜单树：**

```
┌─系统管理
│   用户管理｜角色管理｜权限配置｜审计日志（全部管理员操作记录）
├─内容管理
│   内容发布｜数据结构题管理｜算法题管理｜面试题管理
│   题解管理｜评论管理｜关键词屏蔽管理
├─题目辅助模块
│   题目校验测试
├─日志平台
│   EFK系统日志｜业务日志｜开发日志｜系统日志｜运维管理
│   用户行为日志｜Umami分析面板
├─数据统计报表
│   全局业务报表｜UV/PV、留存统计
├─订单&财务中心
│   订单管理｜商品管理｜金币管理｜财务对账｜退款审核｜发票管理
├─运营中心
│   活动配置｜签到配置｜优惠券｜VIP套餐配置
└─客服工单
    用户申诉工单｜用户账号管理（禁言、解封、封禁）

个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 新增、编辑、删除、导出、批量操作全部可用；可修改任意用户金币、VIP 等级 |
| 数据权限 | 查看系统全部数据 |
| 审计日志 | 可见，不可删除 |
| 限制 | 尽量少登录，日常业务使用一级管理员账号操作 |

---

### 3.2 一级管理员

> 由超级管理员创建；不能创建超级管理员账号，可创建/管理下面全部业务管理员。

**菜单树：**

```
┌─系统管理（受限）
│   用户管理（只能管理二级/业务管理员，看不到超级管理员账号）
│   角色管理（只能分配二级及以下角色）
│   ❌看不到审计日志
├─内容管理
│   内容发布｜数据结构题｜算法题｜面试题
│   题解管理｜评论管理｜关键词屏蔽
├─题目辅助模块
│   题目校验测试
├─日志平台
│   EFK系统日志｜业务日志｜开发日志｜系统日志｜运维管理
│   用户行为日志｜Umami分析
├─数据统计报表
│   全局业务报表
├─订单&财务中心
│   订单｜商品｜金币｜财务对账｜退款审核
├─运营中心
│   活动、签到、优惠券、VIP套餐
└─客服工单
    用户申诉、账号禁言解封

个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 全部业务操作权限 |
| 数据权限 | 查看全部业务数据 |
| 审计日志 | 不可见 |
| 限制 | 不能创建超级管理员账号；不能查看审计日志 |

---

### 3.3 权限分配子管理员

> 专门用来维护二级管理员账号，不能碰题目、订单、日志。

**菜单树：**

```
┌─系统管理
│   用户管理（仅二级业务账号）
│   角色管理（仅可分配二级业务角色）
│   ❌审计日志不可见
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 新增二级账号、分配角色、重置密码 |
| 数据权限 | 仅二级业务账号列表 |
| 限制 | 没有任何内容、订单、日志相关菜单；不能查看审计日志；不能创建超级/一级管理员 |

---

### 3.4 内容发布管理员（含二级）

> 管理内容发布，管理下属二级发布管理员。

**菜单树（一级）：**

```
┌─系统管理（极简）
│   用户管理（仅查看自己下属二级管理员账号，不能新增一级/超级）
├─内容管理
│   数据结构题管理｜算法题管理｜面试题管理
│   题解管理｜评论管理
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 发布、编辑、下线题目；管理下属二级发布管理员 |
| 数据权限 | 可以看到全部题目；下属二级管理员只能看到自己发布的题目 |
| 限制 | 没有审核、日志、订单、财务菜单 |

#### 3.4.1 二级-数据结构发布管理员

**菜单树：**

```
┌─内容管理
│   数据结构题（新增、编辑、提交发布）
│   ❌看不到算法题、面试题
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 新增、编辑、提交发布（提交后需审核管理员审核上线） |
| 数据权限 | **行级权限：只能看到自己创建的数据结构题目**，看不到别人发布的 |
| 限制 | 不能直接上线；看不到算法题、面试题 |

#### 3.4.2 二级-算法发布管理员

**菜单树：**

```
┌─内容管理
│   算法题（新增、编辑、提交发布）
│   ❌看不到数据结构题、面试题
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 新增、编辑、提交发布（需审核） |
| 数据权限 | **行级权限：仅自己创建的算法题** |
| 限制 | 不能直接上线；看不到数据结构题、面试题 |

#### 3.4.3 二级-通用子管理员

> 由内容发布管理员分配，按需开放部分内容模块。

**菜单树：**

```
┌─内容管理
│   （按分配的数据结构题/算法题/面试题模块）
│   题解管理（按需开放）
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 按分配的模块决定 |
| 数据权限 | 仅自己创建的题目 |
| 限制 | 权限由上级内容发布管理员动态分配 |

---

### 3.5 内容审核管理员（含二级）

> 只做审核，不能新增发布题目。

**菜单树（一级）：**

```
┌─内容审核
│   算法内容审核｜面试题审核｜题解审核｜评论审核
├─关键词屏蔽管理
│   屏蔽词新增、编辑、启用禁用、审核
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 通过审核、驳回、下架内容；关键词管理全部操作 |
| 数据权限 | 全部待审核内容 |
| 限制 | 没有发布题目的按钮；没有审计日志、订单、财务菜单 |

#### 3.5.1 题目校验测试管理员

**菜单树：**

```
┌─内容审核
│   题目测试校验（测试用例、判题校验，不可直接上线）
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 执行测试用例、查看判题结果、提交校验报告 |
| 数据权限 | 全部待校验题目 |
| 限制 | 仅校验，校验完成提交给审核管理员上线；不能直接审核通过/驳回 |

#### 3.5.2 二级-关键词审核管理员 `[新增]`

> 专门负责关键词屏蔽词的审核工作，不能审核题目、题解、评论。

**菜单树：**

```
┌─内容审核
│   关键词屏蔽审核
│   ├── 待审核屏蔽词列表（查看、审核）
│   ├── 屏蔽词申请审核（通过、驳回）
│   └── 屏蔽词启用/禁用审核
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 审核通过、驳回关键词申请；启用/禁用审核 |
| 数据权限 | 全部待审核关键词申请 |
| 限制 | 不能审核算法题、面试题、题解、评论；不能新增/编辑屏蔽词（仅审核） |
| 职责分离 | 关键词新增由内容发布管理员/一级管理员操作，审核由本角色完成 |

#### 3.5.3 二级-题解评论审核管理员 `[新增]`

> 专门负责题解和评论的审核工作，不能审核题目内容、不能管理关键词。

**菜单树：**

```
┌─内容审核
│   题解审核（查看、通过、驳回、下架）
│   评论审核（查看、通过、驳回、删除）
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 通过审核、驳回、下架题解；通过、驳回、删除评论 |
| 数据权限 | 全部待审核题解和评论 |
| 限制 | 不能审核算法题/面试题内容；不能管理关键词屏蔽词 |
| 职责分离 | 题目内容审核由一级审核管理员完成，题解/评论审核由本角色完成 |

---

### 3.6 EFK 日志分析管理员（含二级）

> 日志检索，可操作 ES、查看 Umami 行为分析，不能修改业务数据。

**菜单树（一级）：**

```
┌─日志平台
│   EFK系统日志｜业务日志｜开发日志｜系统日志｜运维管理
│   用户行为日志｜Umami分析面板
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 查询、检索、导出日志；日志平台配置；ES索引管理；告警规则配置；管理下属二级日志管理员 |
| 数据权限 | 全部日志数据 |
| 限制 | 没有内容、订单、财务菜单；不能修改业务数据 |

#### 3.6.1 二级-业务日志管理员 `[新增]`

> 仅负责业务日志的检索与导出，看不到开发/系统/运维日志。

**菜单树：**

```
┌─日志平台
│   业务日志（查询、检索、导出）
│   ├── 用户操作日志
│   ├── 订单处理日志
│   ├── 金币变更日志
│   └── 内容发布日志
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 查询、检索、导出业务日志 |
| 数据权限 | 全部业务日志 |
| 限制 | 看不到开发日志、系统日志、运维管理；不能操作 ES 引擎配置；不能配置告警规则 |

#### 3.6.2 二级-开发日志管理员 `[新增]`

> 仅负责开发日志的检索与导出（应用异常堆栈、API调用链、调试日志）。

**菜单树：**

```
┌─日志平台
│   开发日志（查询、检索、导出）
│   ├── 应用异常堆栈
│   ├── API调用链追踪
│   └── 调试日志
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 查询、检索、导出开发日志 |
| 数据权限 | 全部开发日志 |
| 限制 | 看不到业务日志、系统日志、运维管理；不能操作 ES 引擎配置；不能配置告警规则 |

#### 3.6.3 二级-系统日志管理员 `[新增]`

> 仅负责系统日志的检索与导出（服务器运行日志、资源监控、数据库慢查询）。

**菜单树：**

```
┌─日志平台
│   系统日志（查询、检索、导出）
│   ├── 服务器运行日志
│   ├── 资源监控日志（CPU/内存/磁盘）
│   ├── 数据库慢查询日志
│   └── 中间件日志（Redis/MQ）
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 查询、检索、导出系统日志 |
| 数据权限 | 全部系统日志 |
| 限制 | 看不到业务日志、开发日志、运维管理；不能操作 ES 引擎配置；不能配置告警规则 |

#### 3.6.4 二级-运维管理员 `[新增]`

> 负责日志平台的运维配置（ES 索引管理、EFK 配置、告警规则、日志保留策略），可只读查看所有类型日志。

**菜单树：**

```
┌─日志平台
│   运维管理
│   ├── ES索引管理（创建、删除、别名配置）
│   ├── EFK配置（Filebeat/Fluentd 采集配置）
│   ├── 告警规则配置（阈值、通知渠道）
│   └── 日志保留策略（TTL、归档）
│   EFK系统日志（只读查看，全类型）
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 配置 ES 索引、配置 EFK 采集规则、配置告警规则、设置日志保留策略、重启日志服务 |
| 数据权限 | 全部日志数据（只读查看，运维需要全局视野） |
| 限制 | 不能查看业务数据、不能修改用户数据；不能修改日志内容本身 |
| 职责区分 | 与日志分析管理员区分：本角色偏运维配置，日志分析管理员偏检索分析 |

---

### 3.7 数据分析管理员（含二级） `[升级]`

> 原「数据报表只读管理员」升级为「数据分析管理员」（一级角色）。可以查看全部报表、配置报表模板和数据看板、管理下属二级数据分析师。**不能操作 ES/EFK 引擎，不能修改业务数据。**

**菜单树（一级）：**

```
┌─数据统计报表
│   全局业务报表｜UV/PV报表｜用户留存报表｜订单统计报表
├─日志平台
│   Umami分析面板｜用户行为日志
├─数据分析配置
│   报表模板配置｜数据看板配置｜定时任务配置
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 查看、导出报表；配置报表模板；配置数据看板；配置定时报表任务；管理下属二级分析师 |
| 数据权限 | 全部报表数据（只读） |
| 限制 | 不能操作 ES/EFK 引擎（与日志管理员区分）；不能修改业务数据；不能查看审计日志 |
| 与原角色区别 | 原数据报表只读管理员仅可查看导出；升级后可配置报表模板、数据看板、定时任务，并可管理下属二级分析师 |

#### 3.7.1 二级-UV/PV 流量数据分析师 `[新增]`

> 仅负责 UV/PV 流量相关报表的查看与导出。

**菜单树：**

```
┌─数据统计报表
│   UV/PV报表（查看、导出）
│   来源分析报表（查看、导出）
│   页面访问路径分析（查看、导出）
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 查看、导出 |
| 数据权限 | UV/PV 相关报表数据（只读） |
| 限制 | 不能配置报表；不能查看用户留存、订单数据 |

#### 3.7.2 二级-用户留存数据分析师 `[新增]`

> 仅负责用户留存与漏斗分析相关报表的查看与导出。

**菜单树：**

```
┌─数据统计报表
│   用户留存报表（查看、导出）
│   漏斗分析报表（查看、导出）
│   用户活跃度报表（查看、导出）
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 查看、导出 |
| 数据权限 | 留存/漏斗相关报表数据（只读） |
| 限制 | 不能配置报表；不能查看 UV/PV、订单数据 |

#### 3.7.3 二级-订单数据分析师 `[新增]`

> 仅负责订单与收入分析相关报表的查看与导出。注意与订单分析管理员区分：本角色为只读分析，不能修改订单数据。

**菜单树：**

```
┌─数据统计报表
│   订单统计报表（查看、导出）
│   收入分析报表（查看、导出）
│   商品销售排行报表（查看、导出）
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 查看、导出 |
| 数据权限 | 订单/收入相关报表数据（只读） |
| 限制 | 不能配置报表；不能查看 UV/PV、留存数据；**不能修改订单数据**（与订单分析管理员区分） |

---

### 3.8 订单分析管理员（含二级）

**菜单树（一级）：**

```
┌─订单&财务中心
│   订单管理｜商品管理｜金币管理
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 订单查看、商品管理、金币调整全部操作 |
| 数据权限 | 全部订单、商品、金币数据 |
| 限制 | 看不到财务对账、退款审核、发票管理（由财务管理员负责） |

#### 3.8.1 商品管理员

**菜单树：**

```
┌─订单&财务中心
│   商品管理（新增、编辑、上下架商品）
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 新增、编辑、上下架商品 |
| 数据权限 | 全部商品数据 |
| 限制 | 看不到金币、财务对账菜单 |

#### 3.8.2 金币管理员

**菜单树：**

```
┌─订单&财务中心
│   用户金币管理（调整用户金币、查看金币流水）
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 调整用户金币、查看金币流水 |
| 数据权限 | 全部用户金币数据 |
| 限制 | **看不到财务对账、退款审核页面**，只管金币调整，不能对账 |

---

### 3.9 运营活动管理员

> 配置活动规则，不能直接修改用户金币余额。

**菜单树：**

```
┌─运营中心
│   活动配置｜签到配置｜优惠券管理｜VIP套餐配置
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 新增、编辑、启用/禁用活动、签到规则配置、优惠券发放、VIP套餐配置 |
| 数据权限 | 全部运营活动数据 |
| 限制 | 不能直接修改用户金币余额；不能查看审计日志、订单、内容 |

---

### 3.10 财务管理员

> 对账、退款审核，禁止修改用户金币。职责分离。

**菜单树：**

```
┌─订单&财务中心
│   订单流水｜财务对账｜退款审核｜发票管理
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 查看流水、审核退款、发票开具、财务对账 |
| 数据权限 | 全部财务数据 |
| 限制 | **没有调整金币、商品编辑的权限** |

---

### 3.11 客服管理员

> 用户申诉处理，处理评论题解封禁，不能碰题目、订单。

**菜单树：**

```
┌─客服工单
│   用户申诉工单
│   用户账号管理：禁言、解除禁言、账号解封、题解/评论下架
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 处理工单、禁言、解禁、解封、下架题解/评论 |
| 数据权限 | 全部用户申诉工单 |
| 限制 | 不能修改金币、VIP 等级；只能执行禁言解封；不能碰题目、订单 |

---

### 3.12 安全审计管理员

> 独立角色，审计所有管理员操作，不能修改任何业务数据。

**菜单树：**

```
┌─系统管理
│   审计日志（全部管理员登录、变更金币、权限变更、屏蔽词修改记录）
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 只有查看、导出；所有新增/编辑/删除按钮全部置灰消失 |
| 数据权限 | 全部审计日志 |
| 限制 | 审计日志不可删除；不能修改任何业务数据 |

---

### 3.13 外部出题人

> 外部合作出题人员，访问简化后台。非后台管理员。

**菜单树：**

```
┌─我的题目
│   我的算法题｜我的面试题（编辑、保存草稿、提交审核）
└─个人中心｜修改密码
```

| 维度 | 权限 |
|------|------|
| 按钮权限 | 编辑、保存草稿、提交审核 |
| 数据权限 | **行级权限：只能看到自己提交的题目**，看不到其他人题目 |
| 限制 | 不能直接上线，提交等待审核管理员审核；无任何后台管理权限 |

---

## 四、前台普通用户端权限

> 用户登录前台网站，根据用户身份控制页面展示，后端接口鉴权。

### 4.1 用户类型

| 用户类型 | 题目可刷题范围 | 说明 |
|----------|-------------|------|
| 普通用户 | 仅公开题目 | 根据账号状态控制是否可以发评论、发布题解 |
| VIP 用户 | 公开 + VIP 题目 | 受评论、题解权限控制 |
| SVIP 用户 | 公开 + VIP + SVIP 专属题目 | 最高权限 |
| 限时体验 VIP | 有效期内拥有 VIP 权限 | 到期收回 |
| 封禁用户 | 部分场景限制访问 | 禁止评论、禁止发布题解 |

### 4.2 用户侧权限维度

| 维度 | 可选值 | 说明 |
|------|--------|------|
| 评论权限 | 允许 / 禁止评论 | 控制用户是否可以发表评论 |
| 题解发布权限 | 允许发布 / 仅阅读 | 控制用户是否可以发布题解 |
| 题目可见权限 | 公开 / VIP 可见 / SVIP 可见 / 金币解锁 | 控制题目对不同用户的可见性 |
| 购买解锁 | 金币付费解锁单道题目 | 用户可用金币解锁单道付费题目 |

---

## 五、MySQL 数据库表结构

### 5.1 ER 关系说明

```
sys_user ──1:N── sys_user_role ──N:1── sys_role
sys_role ──1:N── sys_role_menu ──N:1── sys_menu
sys_role ──1:N── sys_role_permission ──N:1── sys_permission
sys_menu ──1:N── sys_permission
sys_role ──1:N── sys_data_permission_rule
sys_user ──1:N── sys_audit_log
sys_user ──1:N── sys_login_log
sys_user ──1:N── sys_operation_log
sys_role ──自关联── sys_role (parent_role_id)
sys_menu ──自关联── sys_menu (parent_id)
```

### 5.2 完整 DDL

```sql
-- 0台管理员用户表 id为入职公司的员工超级管理员为1 长度为char 无服务
CREATE TABLE `sys_user` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT,
    `username`        VARCHAR(64)  NOT NULL COMMENT '登录账号',
    `password`        VARCHAR(128) NOT NULL COMMENT 'bcrypt 加密密码',
    `real_name`       VARCHAR(64)  NOT NULL COMMENT '真实姓名',
    `email`           VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `phone`           VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `avatar`          VARCHAR(256) DEFAULT NULL COMMENT '头像 URL',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-正常 2-封禁',
    `account_type`    TINYINT      NOT NULL DEFAULT 1 COMMENT '账号类型: 1-内部管理员 2-外部出题人',
    `last_login_time` DATETIME     DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip`   VARCHAR(45)  DEFAULT NULL COMMENT '最后登录 IP',
    `created_by`      BIGINT       DEFAULT NULL COMMENT '创建人 ID',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`         TINYINT      NOT NULL DEFAULT 0 COMMENT '软删除: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_status` (`status`),
    KEY `idx_account_type` (`account_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台用户表';


-- 1. 角色表
-- ============================================================
CREATE TABLE `sys_role` (
    `id`             BIGINT      NOT NULL AUTO_INCREMENT,
    `role_code`      VARCHAR(64) NOT NULL COMMENT '角色编码',
    `role_name`      VARCHAR(64) NOT NULL COMMENT '角色名称',
    `role_level`     TINYINT     NOT NULL COMMENT '角色层级: 1-超级 2-一级 3-二级',
    `parent_role_id` BIGINT      DEFAULT NULL COMMENT '父角色 ID（二级角色归属，NULL 表示顶级）',
    `data_scope`     TINYINT     NOT NULL DEFAULT 1 COMMENT '数据范围: 1-全部数据 2-本部门 3-本人数据',
    `description`    VARCHAR(256) DEFAULT NULL COMMENT '角色描述',
    `status`         TINYINT     NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    `is_system`      TINYINT     NOT NULL DEFAULT 0 COMMENT '是否系统内置: 0-否 1-是（不可删除）',
    `created_at`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`),
    KEY `idx_role_level` (`role_level`),
    KEY `idx_parent_role` (`parent_role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 2. 菜单表（树形结构，含目录/菜单/按钮三种类型）
-- ============================================================
CREATE TABLE `sys_menu` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `parent_id`  BIGINT       NOT NULL DEFAULT 0 COMMENT '父菜单 ID，0 为根节点',
    `menu_name`  VARCHAR(64)  NOT NULL COMMENT '菜单名称',
    `menu_type`  TINYINT      NOT NULL COMMENT '类型: 1-目录 2-菜单 3-按钮',
    `path`       VARCHAR(128) DEFAULT NULL COMMENT '路由路径',
    `component`  VARCHAR(128) DEFAULT NULL COMMENT '前端组件路径',
    `perms`      VARCHAR(128) DEFAULT NULL COMMENT '权限标识（如 content:ds:list）',
    `icon`       VARCHAR(64)  DEFAULT NULL COMMENT '菜单图标',
    `sort_order` INT          NOT NULL DEFAULT 0 COMMENT '排序号',
    `visible`    TINYINT      NOT NULL DEFAULT 1 COMMENT '是否可见: 0-隐藏 1-显示',
    `status`     TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_parent` (`parent_id`),
    KEY `idx_type` (`menu_type`),
    KEY `idx_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- 3. 权限点表（按钮级权限）
CREATE TABLE `sys_permission` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `menu_id`     BIGINT       NOT NULL COMMENT '所属菜单 ID',
    `perm_code`   VARCHAR(128) NOT NULL COMMENT '权限编码（如 content:ds:add）',
    `perm_name`   VARCHAR(64)  NOT NULL COMMENT '权限名称',
    `perm_type`   TINYINT      NOT NULL COMMENT '类型: 1-新增 2-编辑 3-删除 4-导出 5-审核 6-其他',
    `api_method`  VARCHAR(10)  DEFAULT NULL COMMENT 'API 方法: GET/POST/PUT/DELETE',
    `api_path`    VARCHAR(256) DEFAULT NULL COMMENT 'API 路径',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_perm_code` (`perm_code`),
    KEY `idx_menu` (`menu_id`),
    KEY `idx_type` (`perm_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限点表（按钮级）';

-- 4 用户-角色关联表
CREATE TABLE `sys_user_role` (
    `id`         BIGINT   NOT NULL AUTO_INCREMENT,
    `user_id`    BIGINT   NOT NULL COMMENT '用户 ID',
    `role_id`    BIGINT   NOT NULL COMMENT '角色 ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 5 角色-菜单关联表
CREATE TABLE `sys_role_menu` (
    `id`         BIGINT   NOT NULL AUTO_INCREMENT,
    `role_id`    BIGINT   NOT NULL COMMENT '角色 ID',
    `menu_id`    BIGINT   NOT NULL COMMENT '菜单 ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
    KEY `idx_role` (`role_id`),
    KEY `idx_menu` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 6 角色-权限关联表
CREATE TABLE `sys_role_permission` (
    `id`            BIGINT   NOT NULL AUTO_INCREMENT,
    `role_id`       BIGINT   NOT NULL COMMENT '角色 ID',
    `permission_id` BIGINT   NOT NULL COMMENT '权限 ID',
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_perm` (`role_id`, `permission_id`),
    KEY `idx_role` (`role_id`),
    KEY `idx_perm` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 7 数据权限规则表（行级数据权限）
CREATE TABLE `sys_data_permission_rule` (
    `id`              BIGINT      NOT NULL AUTO_INCREMENT,
    `role_id`         BIGINT      NOT NULL COMMENT '角色 ID',
    `target_table`    VARCHAR(64) NOT NULL COMMENT '目标表名（如 t_algorithm, t_solution）',
    `rule_type`       TINYINT     NOT NULL COMMENT '规则类型: 1-全部数据 2-本人创建 3-指定部门 4-自定义 SQL',
    `rule_expression` TEXT        DEFAULT NULL COMMENT '规则表达式（如 created_by = {current_user_id}）',
    `status`          TINYINT     NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    `created_at`      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_role` (`role_id`),
    KEY `idx_table` (`target_table`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据权限规则表';

-- 8 审计日志表（不可删除，仅超级管理员和安全审计管理员可查）
CREATE TABLE `sys_audit_log` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`        BIGINT       NOT NULL COMMENT '操作人 ID',
    `username`       VARCHAR(64)  NOT NULL COMMENT '操作人账号',
    `role_name`      VARCHAR(64)  DEFAULT NULL COMMENT '操作时角色',
    `module`         VARCHAR(64)  NOT NULL COMMENT '操作模块',
    `operation`      VARCHAR(128) NOT NULL COMMENT '操作行为',
    `method`         VARCHAR(10)  DEFAULT NULL COMMENT '请求方法',
    `request_url`    VARCHAR(512) DEFAULT NULL COMMENT '请求 URL',
    `request_params` TEXT         DEFAULT NULL COMMENT '请求参数',
    `response_code`  INT          DEFAULT NULL COMMENT '响应状态码',
    `ip`             VARCHAR(45)  NOT NULL COMMENT '操作 IP',
    `location`       VARCHAR(128) DEFAULT NULL COMMENT '操作地点',
    `cost_time`      BIGINT       DEFAULT NULL COMMENT '耗时(ms)',
    `operation_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_module` (`module`),
    KEY `idx_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表';


-- 10. 管理员日志登录表
-- ============================================================
CREATE TABLE `sys_login_log` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`    BIGINT       DEFAULT NULL COMMENT '用户 ID',
    `username`   VARCHAR(64)  NOT NULL COMMENT '登录账号',
    `login_type` TINYINT      NOT NULL COMMENT '登录类型: 1-登录 2-退出',
    `status`     TINYINT      NOT NULL COMMENT '状态: 0-失败 1-成功',
    `ip`         VARCHAR(45)  NOT NULL COMMENT '登录 IP',
    `location`   VARCHAR(128) DEFAULT NULL COMMENT '登录地点',
    `browser`    VARCHAR(64)  DEFAULT NULL COMMENT '浏览器',
    `os`         VARCHAR(64)  DEFAULT NULL COMMENT '操作系统',
    `message`    VARCHAR(256) DEFAULT NULL COMMENT '提示消息',
    `login_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- 11.管理员业务日志表
CREATE TABLE `sys_operation_log` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`        BIGINT       NOT NULL COMMENT '操作人 ID',
    `username`       VARCHAR(64)  NOT NULL COMMENT '操作人账号',
    `business_type`  VARCHAR(64)  NOT NULL COMMENT '业务类型（内容发布/金币调整/订单处理等）',
    `operation_type` VARCHAR(32)  NOT NULL COMMENT '操作类型（新增/编辑/删除/审核/导出）',
    `target_id`      BIGINT       DEFAULT NULL COMMENT '操作对象 ID',
    `target_name`    VARCHAR(128) DEFAULT NULL COMMENT '操作对象名称',
    `before_data`    TEXT         DEFAULT NULL COMMENT '变更前数据（JSON）',
    `after_data`     TEXT         DEFAULT NULL COMMENT '变更后数据（JSON）',
    `remark`         VARCHAR(256) DEFAULT NULL COMMENT '备注',
    `operation_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_business` (`business_type`),
    KEY `idx_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务操作日志表';
```

### 5.3 种子数据 — 角色表

```sql
INSERT INTO `sys_role` (`id`, `role_code`, `role_name`, `role_level`, `parent_role_id`, `data_scope`, `description`, `is_system`) VALUES
-- 顶级 / 一级角色
(1,  'SUPER_ADMIN',                '超级管理员',                 1, NULL, 1, '运维应急，全部权限，不日常业务操作',                 1),
(2,  'LEVEL1_ADMIN',               '一级管理员',                 2, 1,    1, '日常业务管理员，可管理二级业务管理员',              1),
(3,  'PERM_MANAGER',               '权限分配子管理员',           3, 2,    2, '仅分配账号角色，无业务权限',                        1),
(4,  'CONTENT_PUBLISHER',          '内容发布管理员',             2, 2,    1, '管理内容发布及下属二级发布管理员',                  1),
(5,  'CONTENT_AUDITOR',            '内容审核管理员',             2, 2,    1, '只做审核，不能新增发布题目',                        1),
(6,  'EFK_LOG_ADMIN',              'EFK日志分析管理员',          2, 2,    1, '日志检索，可操作ES，不能修改业务数据',              1),
(7,  'DATA_ANALYST',               '数据分析管理员',             2, 2,    1, '数据分析与报表管理，可配置报表，不可操作ES引擎',    1),
(8,  'ORDER_ADMIN',                '订单分析管理员',             2, 2,    1, '订单与商品管理',                                    1),
(9,  'OPS_ADMIN',                  '运营活动管理员',             2, 2,    1, '配置活动规则，不能修改用户金币',                    1),
(10, 'FINANCE_ADMIN',              '财务管理员',                 2, 2,    1, '对账退款审核，禁止修改用户金币',                    1),
(11, 'CS_ADMIN',                   '客服管理员',                 2, 2,    1, '用户申诉处理，禁言解封',                            1),
(12, 'SECURITY_AUDITOR',           '安全审计管理员',             2, 1,    1, '只读角色，审计所有管理员操作',                      1),
(13, 'EXTERNAL_AUTHOR',            '外部出题人',                 3, NULL, 3, '外部合作出题，访问简化后台',                        1),
-- 内容发布二级
(14, 'DS_PUBLISHER',               '二级-数据结构发布管理员',     3, 4,    3, '仅自己创建的数据结构题目',                          1),
(15, 'ALGO_PUBLISHER',             '二级-算法发布管理员',         3, 4,    3, '仅自己创建的算法题目',                              1),
(16, 'GENERAL_SUB_ADMIN',          '二级-通用子管理员',           3, 4,    3, '按需开放部分内容模块',                              1),
-- 内容审核二级
(17, 'TEST_VALIDATOR',             '题目校验测试管理员',         3, 5,    1, '仅校验，不可直接上线',                              1),
(18, 'KEYWORD_AUDITOR',            '二级-关键词审核管理员',       3, 5,    1, '关键词屏蔽词审核',                                  1),
(19, 'SOLUTION_COMMENT_AUDITOR',   '二级-题解评论审核管理员',     3, 5,    1, '题解审核、评论审核',                                1),
-- EFK 日志二级
(20, 'BIZ_LOG_ADMIN',              '二级-业务日志管理员',         3, 6,    1, '业务日志检索导出',                                  1),
(21, 'DEV_LOG_ADMIN',              '二级-开发日志管理员',         3, 6,    1, '开发日志检索导出',                                  1),
(22, 'SYS_LOG_ADMIN',              '二级-系统日志管理员',         3, 6,    1, '系统日志检索导出',                                  1),
(23, 'OPS_LOG_ADMIN',              '二级-运维管理员',             3, 6,    1, '日志平台配置、ES索引管理、告警规则',                1),
-- 数据分析二级
(24, 'UV_PV_ANALYST',              '二级-UV/PV数据分析师',        3, 7,    1, 'UV/PV报表、来源分析',                               1),
(25, 'RETENTION_ANALYST',          '二级-用户留存数据分析师',     3, 7,    1, '留存报表、漏斗分析',                                1),
(26, 'ORDER_ANALYST_SUB',          '二级-订单数据分析师',         3, 7,    1, '订单统计报表、收入分析（只读）',                    1),
-- 订单二级
(27, 'PRODUCT_ADMIN',              '商品管理员',                 3, 8,    1, '商品增删改查、上下架',                              1),
(28, 'COIN_ADMIN',                 '金币管理员',                 3, 8,    2, '用户金币调整、金币流水查看',                        1);
```

### 5.4 种子数据 — 菜单表

```sql
-- 一级目录
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort_order`) VALUES
(1,  0, '系统管理',       1, '/system',       NULL, NULL, 'Setting',   1),
(2,  0, '内容管理',       1, '/content',      NULL, NULL, 'Document',  2),
(3,  0, '内容审核',       1, '/audit',        NULL, NULL, 'Checked',   3),
(4,  0, '题目辅助模块',   1, '/assist',       NULL, NULL, 'Tools',     4),
(5,  0, '日志平台',       1, '/log',           NULL, NULL, 'Monitor',  5),
(6,  0, '数据统计报表',   1, '/report',       NULL, NULL, 'TrendCharts', 6),
(7,  0, '数据分析配置',   1, '/analytics-config', NULL, NULL, 'DataAnalysis', 7),
(8,  0, '订单&财务中心', 1, '/finance',      NULL, NULL, 'Money',     8),
(9,  0, '运营中心',       1, '/operation',    NULL, NULL, 'Promotion', 9),
(10, 0, '客服工单',       1, '/support',      NULL, NULL, 'Service',   10),
(11, 0, '我的题目',       1, '/my-questions',  NULL, NULL, 'EditPen',   11),

-- 系统管理子菜单
(101, 1, '用户管理',   2, 'user',     'system/user/index',     'system:user:list',     'User',      1),
(102, 1, '角色管理',   2, 'role',     'system/role/index',     'system:role:list',     'Role',      2),
(103, 1, '权限配置',   2, 'perm',     'system/perm/index',     'system:perm:list',     'Key',       3),
(104, 1, '审计日志',   2, 'audit-log','system/audit/index',    'system:audit:list',    'View',      4),

-- 内容管理子菜单
(201, 2, '内容发布',         2, 'publish',    'content/publish/index',    'content:publish:list',    'Upload',     1),
(202, 2, '数据结构题管理',   2, 'ds',         'content/ds/index',         'content:ds:list',         'Connection', 2),
(203, 2, '算法题管理',       2, 'algorithm',  'content/algo/index',       'content:algo:list',       'Guide',       3),
(204, 2, '面试题管理',       2, 'interview',  'content/interview/index',  'content:interview:list',  'ChatLineUni', 4),
(205, 2, '题解管理',         2, 'solution',   'content/solution/index',   'content:solution:list',   'Tickets',     5),
(206, 2, '评论管理',         2, 'comment',    'content/comment/index',    'content:comment:list',    'ChatDotRound', 6),
(207, 2, '关键词屏蔽管理',   2, 'keyword',    'content/keyword/index',    'content:keyword:list',    'MessageBox',  7),

-- 内容审核子菜单
(301, 3, '算法内容审核',       2, 'algo-audit',    'audit/algo/index',       'audit:algo:list',    'Select',     1),
(302, 3, '面试题审核',         2, 'iv-audit',      'audit/interview/index',  'audit:iv:list',      'Select',     2),
(303, 3, '题解审核',           2, 'sol-audit',     'audit/solution/index',   'audit:sol:list',     'Select',     3),
(304, 3, '评论审核',           2, 'comment-audit', 'audit/comment/index',    'audit:comment:list', 'Select',     4),
(305, 3, '题目校验测试',       2, 'test-validate', 'audit/test/index',       'audit:test:list',    'Select',     5),
(306, 3, '关键词屏蔽审核',     2, 'kw-audit',      'audit/keyword/index',    'audit:keyword:list', 'Select',     6),

-- 日志平台子菜单
(501, 5, 'EFK系统日志',   2, 'efk-sys',  'log/efk/index',     'log:efk:list',     'Monitor',   1),
(502, 5, '业务日志',       2, 'biz',     'log/biz/index',     'log:biz:list',     'Document',   2),
(503, 5, '开发日志',       2, 'dev',     'log/dev/index',     'log:dev:list',     'EditPen',    3),
(504, 5, '系统日志',       2, 'sys',     'log/sys/index',     'log:sys:list',     'Setting',    4),
(505, 5, '运维管理',       2, 'ops-mgmt','log/ops/index',     'log:ops:list',     'Tools',      5),
(506, 5, '用户行为日志',   2, 'behavior','log/behavior/index','log:behavior:list','View',       6),
(507, 5, 'Umami分析面板',  2, 'umami',   'log/umami/index',   'log:umami:list',   'DataLine',   7),

-- 数据统计报表子菜单
(601, 6, '全局业务报表',   2, 'global',     'report/global/index',   'report:global:list',    'TrendCharts', 1),
(602, 6, 'UV/PV报表',      2, 'uv-pv',      'report/uvpv/index',     'report:uvpv:list',     'DataLine',    2),
(603, 6, '用户留存报表',   2, 'retention',  'report/retention/index','report:retention:list','DataAnalysis', 3),
(604, 6, '订单统计报表',   2, 'order-stat', 'report/order/index',    'report:order:list',   'Money',       4),

-- 数据分析配置子菜单
(701, 7, '报表模板配置',   2, 'tpl',    'analytics/tpl/index',   'analytics:tpl:list',    'Setting',  1),
(702, 7, '数据看板配置',   2, 'board',  'analytics/board/index', 'analytics:board:list', 'Grid',     2),
(703, 7, '定时任务配置',   2, 'sched',  'analytics/sched/index', 'analytics:sched:list', 'Timer',    3),

-- 订单&财务中心子菜单
(801, 8, '订单管理',   2, 'order',   'finance/order/index',   'finance:order:list',    'ShoppingCart', 1),
(802, 8, '商品管理',   2, 'product', 'finance/product/index', 'finance:product:list', 'Goods',        2),
(803, 8, '金币管理',   2, 'coin',    'finance/coin/index',    'finance:coin:list',    'Coin',         3),
(804, 8, '财务对账',   2, 'reconcile','finance/reconcile/index','finance:reconcile:list','Wallet',     4),
(805, 8, '退款审核',   2, 'refund',  'finance/refund/index',  'finance:refund:list',  'RefreshLeft',  5),
(806, 8, '发票管理',   2, 'invoice', 'finance/invoice/index', 'finance:invoice:list', 'Document',     6),

-- 运营中心子菜单
(901, 9, '活动配置',     2, 'activity', 'ops/activity/index', 'ops:activity:list', 'Flag',       1),
(902, 9, '签到配置',     2, 'signin',   'ops/signin/index',   'ops:signin:list',   'Calendar',   2),
(903, 9, '优惠券管理',   2, 'coupon',   'ops/coupon/index',   'ops:coupon:list',   'Ticket',     3),
(904, 9, 'VIP套餐配置',  2, 'vip',      'ops/vip/index',      'ops:vip:list',      'Medal',      4),

-- 客服工单子菜单
(1001, 10, '用户申诉工单',   2, 'complaint', 'support/complaint/index', 'support:complaint:list', 'Service', 1),
(1002, 10, '用户账号管理',   2, 'account',   'support/account/index',   'support:account:list',   'User',    2),

-- 我的题目子菜单
(1101, 11, '我的算法题', 2, 'my-algo', 'my/algo/index', 'my:algo:list', 'EditPen', 1),
(1102, 11, '我的面试题', 2, 'my-iv',   'my/iv/index',   'my:iv:list',   'ChatLineUni', 2),

-- 通用菜单
(1200, 0, '个人中心', 2, 'profile', 'profile/index', 'system:profile:view', 'UserFilled', 99),
(1201, 0, '修改密码', 2, 'password','profile/password','system:password:view','Key',       100);
```

### 5.5 种子数据 — 数据权限规则示例

```sql
-- 二级发布角色：行级数据权限，仅本人创建的题目
INSERT INTO `sys_data_permission_rule` (`role_id`, `target_table`, `rule_type`, `rule_expression`) VALUES
(14, 't_data_structure', 2, 'created_by = {current_user_id}'),  -- DS_PUBLISHER: 仅自己的数据结构题
(15, 't_algorithm',       2, 'created_by = {current_user_id}'),  -- ALGO_PUBLISHER: 仅自己的算法题
(13, 't_algorithm',       2, 'created_by = {current_user_id}'),  -- EXTERNAL_AUTHOR: 仅自己的题目
(13, 't_interview',       2, 'created_by = {current_user_id}'),  -- EXTERNAL_AUTHOR: 仅自己的面试题
(28, 't_user_coin_log',   3, 'user_id IS NOT NULL');              -- COIN_ADMIN: 金币流水（部门范围）
```

---

## 六、权限矩阵

### 6.1 角色 x 菜单组权限矩阵

> 说明：✅ 可见 ｜ ❌ 不可见 ｜ ✏️ 可见但受限

| 角色 | 系统管理 | 内容管理 | 内容审核 | 题目辅助 | 日志平台 | 数据报表 | 分析配置 | 订单财务 | 运营中心 | 客服工单 | 我的题目 | 审计日志 |
|------|---------|---------|---------|---------|---------|---------|---------|---------|---------|---------|---------|---------|
| 超级管理员 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | - | ✅ | ✅ | ✅ | - | ✅ |
| 一级管理员 | ✏️ | ✅ | ✅ | ✅ | ✅ | ✅ | - | ✅ | ✅ | ✅ | - | ❌ |
| 权限分配子管理员 | ✏️ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | - | ❌ |
| 内容发布管理员 | ✏️ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | - | ❌ |
| ┖二级-DS发布 | ❌ | ✏️ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | - | ❌ |
| ┖二级-算法发布 | ❌ | ✏️ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | - | ❌ |
| 内容审核管理员 | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | - | ❌ |
| ┖题目校验测试 | ❌ | ❌ | ✏️ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | - | ❌ |
| ┖二级-关键词审核 `[新]` | ❌ | ❌ | ✏️ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | - | ❌ |
| ┖二级-题解评论审核 `[新]` | ❌ | ❌ | ✏️ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | - | ❌ |
| EFK日志分析管理员 | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | - | ❌ |
| ┖二级-业务日志 `[新]` | ❌ | ❌ | ❌ | ❌ | ✏️ | ❌ | ❌ | ❌ | ❌ | ❌ | - | ❌ |
| ┖二级-开发日志 `[新]` | ❌ | ❌ | ❌ | ❌ | ✏️ | ❌ | ❌ | ❌ | ❌ | ❌ | - | ❌ |
| ┖二级-系统日志 `[新]` | ❌ | ❌ | ❌ | ❌ | ✏️ | ❌ | ❌ | ❌ | ❌ | ❌ | - | ❌ |
| ┖二级-运维管理 `[新]` | ❌ | ❌ | ❌ | ❌ | ✏️ | ❌ | ❌ | ❌ | ❌ | ❌ | - | ❌ |
| 数据分析管理员 `[升级]` | ❌ | ❌ | ❌ | ❌ | ✏️ | ✅ | ✅ | ❌ | ❌ | ❌ | - | ❌ |
| ┖二级-UV/PV分析 `[新]` | ❌ | ❌ | ❌ | ❌ | ❌ | ✏️ | ❌ | ❌ | ❌ | ❌ | - | ❌ |
| ┖二级-留存分析 `[新]` | ❌ | ❌ | ❌ | ❌ | ❌ | ✏️ | ❌ | ❌ | ❌ | ❌ | - | ❌ |
| ┖二级-订单分析 `[新]` | ❌ | ❌ | ❌ | ❌ | ❌ | ✏️ | ❌ | ❌ | ❌ | ❌ | - | ❌ |
| 订单分析管理员 | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✏️ | ❌ | ❌ | - | ❌ |
| ┖商品管理员 | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✏️ | ❌ | ❌ | - | ❌ |
| ┖金币管理员 | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✏️ | ❌ | ❌ | - | ❌ |
| 运营活动管理员 | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ | - | ❌ |
| 财务管理员 | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✏️ | ❌ | ❌ | - | ❌ |
| 客服管理员 | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | - | ❌ |
| 安全审计管理员 | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | - | ✅ |
| 外部出题人 | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ |

> 备注：所有角色均可访问「个人中心、修改密码」。✏️ 表示该菜单组下仅部分子菜单可见。

### 6.2 职责分离对照表

| 业务场景 | 可操作角色 | 不可操作角色 | 隔离说明 |
|----------|-----------|-------------|----------|
| 金币调整 | 金币管理员 | 财务管理员、数据分析管理员 | 金币管理员可改金币，看不到财务对账 |
| 财务对账/退款 | 财务管理员 | 金币管理员、运营活动管理员 | 财务管理员可对账退款，不能改金币 |
| 题目发布 | 内容发布管理员及二级 | 内容审核管理员及二级 | 发布者可提交，不能审核；审核者不能发布 |
| 题目审核 | 内容审核管理员 | 内容发布管理员 | 审核者不能发布题目 |
| 关键词审核 | 二级-关键词审核管理员 | 二级-题解评论审核管理员 | 关键词审核不能审核题解/评论 |
| 题解/评论审核 | 二级-题解评论审核管理员 | 二级-关键词审核管理员 | 题解评论审核不能管理关键词 |
| 日志检索 | EFK日志分析管理员及二级 | 数据分析管理员 | 日志管理员可操作ES引擎，数据分析管理员不能 |
| 数据分析配置 | 数据分析管理员 | EFK日志分析管理员及二级 | 数据分析管理员可配置报表，日志管理员不能 |
| 审计日志查看 | 超级管理员、安全审计管理员 | 其他所有角色 | 审计日志仅这两个角色可见 |
| 用户封禁/解封 | 客服管理员 | 内容发布管理员、运营活动管理员 | 客服管理员可禁言解封，不能碰题目和运营配置 |

---

## 七、关键安全约束汇总

| 序号 | 约束 | 说明 |
|------|------|------|
| 1 | 审计日志访问控制 | 仅超级管理员、安全审计管理员可见，任何人不能删除 |
| 2 | 金币与财务职责分离 | 金币管理员可改金币看不到财务对账；财务管理员可对账退款不能改金币 |
| 3 | 发布与审核职责分离 | 发布管理员可以提交题目不能审核；审核管理员不能发布题目 |
| 4 | 日志与数据分析职责分离 | 日志管理员可操作 ES/EFK 引擎不能配置报表；数据分析管理员可配置报表不能操作 ES 引擎 |
| 5 | 后端接口强校验 | 所有后台接口必须做权限校验，不能只靠前端隐藏菜单、按钮 |
| 6 | 行级数据权限 | 发布类角色只能看到自己创建的数据（data_scope = SELF） |
| 7 | 超级管理员低频使用 | 超级管理员尽量少登录，日常业务使用一级管理员账号操作 |
| 8 | 关键词审核职责分离 | 关键词新增由发布管理员操作，审核由二级关键词审核管理员完成 |
| 9 | 题解评论审核职责分离 | 题目内容审核与题解/评论审核由不同二级管理员分别负责 |
| 10 | 二级日志管理员不可配置引擎 | 业务/开发/系统日志管理员仅可检索导出，运维管理员负责引擎配置 |
| 11 | 二级数据分析师只读 | UV/PV、留存、订单数据分析师仅可查看导出，不能配置报表 |
| 12 | 外部出题人隔离 | 外部出题人只能看到自己提交的题目，不能直接上线，无任何后台管理权限 |
| 13 | 安全审计管理员全只读 | 所有新增/编辑/删除按钮全部置灰消失，仅查看和导出 |
| 14 | 一级管理员不可见审计日志 | 一级管理员看不到审计日志菜单，不能查看任何审计记录 |
| 15 | 权限分配子管理员受限 | 仅可管理二级业务账号，不能创建超级/一级管理员，无任何业务菜单 |
