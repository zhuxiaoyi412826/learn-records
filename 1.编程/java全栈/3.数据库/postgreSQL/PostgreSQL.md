







# 什么是PostgreSQL

PostgreSQL 是 “功能最全的教科书式关系数据库”；MySQL 是 “简单好用、互联网普及度最高的关系数据库”。

## 和MySQL的区别

| 对比项            | PostgreSQL(PG)                                               | MySQL                                                        |
| ----------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **定位**          | 企业级、功能完备高级关系库，对标准 SQL 遵循度高              | 轻量高性能，Web 业务广泛使用，简单场景上手快                 |
| **SQL 标准**      | 高度兼容 SQL 标准，支持完整高级语法                          | 部分语法对标准 SQL 做了裁剪 / 扩展，有很多 MySQL 特有语法    |
| **数据类型**      | 非常丰富：JSONB、数组、GIS、自定义类型、枚举、大文本、复合类型 | 基础类型齐全；JSON 支持弱，无原生数组，GIS 能力较弱          |
| **事务与 MVCC**   | MVCC 实现优秀，**默认可重复读**，严格 ACID，无幻读问题       | InnoDB 引擎支持事务；默认隔离级别 REPEATABLE‑READ，会存在幻读；MyISAM 不支持事务 |
| **索引**          | B‑tree、GIN、GIST、SP‑GIST、BRIN；支持部分索引、表达式索引、JSONB 索引 | B‑tree、HASH；索引类型较少，JSON 索引能力有限                |
| **并发能力**      | 写并发强，多版本机制，适合大量读写混合场景                   | 读性能极强；高并发大量写入场景锁开销相对更大                 |
| **扩展能力**      | 强大：自定义函数、存储过程、触发器、CTE 递归查询、窗口函数、物化视图、表分区 | 基础存储过程、触发器；CTE 支持弱，物化视图需要手动实现，分区功能较晚完善 |
| **字符集 & 排序** | 编码、Collate 排序规则强；Windows 下容易遇到 locale 不匹配问题（你刚刚遇到的 umami 建库报错） | 默认 utf8mb4，Windows/linux 几乎不会出现 locale 报错，配置简单 |
| **权限模型**      | **数据库、Schema 两级权限**；库下面有 schema（默认 public），用户需要同时授权 database + schema 权限（umami 必须给 public schema 授权） | 库等价于 schema，没有双层概念，授权简单，GRANT 直接到库即可  |
| **JSON 处理**     | `JSONB`二进制 json，支持索引、查询过滤，性能很高             | JSON 类型，不能直接建索引，查询效率低                        |
| **全文检索**      | 内置强大全文检索                                             | 依赖第三方插件，原生能力弱                                   |
| **适用场景**      | 数据分析、BI、GIS、复杂查询、报表、umami 这类自托管分析系统、复杂业务、开源后台 | 网站、小程序、中小型业务、高读低写互联网业务，PHP/Java 简单 Web 项目 |
| **生态 & 运维**   | 配置项多，调参复杂；docker 部署友好；Windows 本地坑较多      | 运维简单，学习资料极多，国内使用最广泛                       |



1. PG 有 **Database → Schema(public)** 两层结构，新建数据库后，还需要给用户授权`public` schema，否则应用会报权限不足；MySQL 没有 schema 这一层。
2. PG 建库时`LC_COLLATE / LC_CTYPE`依赖操作系统 locale，**Windows 没有 en_US.UTF‑8**，直接复制 Linux 脚本会报错；MySQL 不存在该问题。
3. DBeaver 默认不展示全部 PG 数据库，需要打开`Show all databases`并重连；MySQL 会直接全部显示。























![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260821194648.png)



