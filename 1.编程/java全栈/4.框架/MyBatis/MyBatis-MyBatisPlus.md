#### 1.基础

**1. 快速上手**

- Spring Boot 整合 MyBatis-Plus（引入 Starter、配置文件）

- ```
  BaseMapper<T>
  ```

  ：内置的 CRUD 方法

  - `insert`、`deleteById`、`updateById`、`selectById`、`selectList`
  - `selectBatchIds`、`deleteBatchIds`

- ```
  IService<T>
  ```

  ```
  ServiceImpl<M, T>
  ```

  ：Service 层 CRUD

  - `save`、`remove`、`update`、`getById`、`list`、`page`

**2. 条件构造器**（最常用，必须熟练）

- ```
  QueryWrapper
  ```

  ：查询条件

  - `eq`、`ne`、`gt`、`ge`、`lt`、`le`（等于、不等于、大于、小于...）
  - `like`、`notLike`、`likeLeft`、`likeRight`
  - `in`、`notIn`、`between`
  - `orderByAsc`、`orderByDesc`
  - `select`（指定查询字段）
  - `and`、`or`（条件拼接）

- ```
  UpdateWrapper
  ```

  ：更新条件

  - `set`（设置更新字段）

- Lambda 版本

  （推荐，避免字段名写错）：

  - `LambdaQueryWrapper`、`LambdaUpdateWrapper`
  - `Wrappers.<User>lambdaQuery()` 快捷创建

**3. 分页插件**

- 分页配置类（`MybatisPlusInterceptor` + `PaginationInnerInterceptor`）
- `Page<T>` 对象使用
- `page(page, wrapper)` 分页查询
- 分页结果：`records`、`total`、`current`、`size`

**4. 常用注解**

- `@TableName`：指定表名
- `@TableId`：主键 + 主键策略（ASSIGN_ID 雪花算法、AUTO 自增）
- `@TableField`：字段映射、`exist = false`（非数据库字段）
- `@TableLogic`：逻辑删除

**5. 代码生成器**

- MyBatis-Plus Generator 或 CodeGenerator
- 一键生成 Entity、Mapper、Service、Controller
- 自定义模板（生成 VO、DTO、Query 等）

#### 2. 核心原理

- MyBatis-Plus 启动流程：如何自动注入 BaseMapper 方法
- Mapper 代理原理：JDK 动态代理 + MapperProxy
- SQL 注入原理：SqlInjector、AbstractMethod
- **动手实践**：自定义 BaseMapper 方法（比如批量插入 MySQL 专属方法）

#### 3. 条件构造器进阶

- QueryWrapper / UpdateWrapper / LambdaQueryWrapper / LambdaUpdateWrapper 区别
- 条件构造器的链式调用原理
- 自定义 SQL 条件：`apply`、`inSql`、`exists`
- 逻辑删除：全局配置、自定义删除值、联表查询注意事项
- 自动填充：`MetaObjectHandler`、创建时间/更新时间/创建人/更新人
- 乐观锁：`@Version` 注解、乐观锁插件、适用场景
- 多租户：TenantLineInnerInterceptor、租户隔离原理
- 动态表名：DynamicTableNameInnerInterceptor

#### 4. 分页插件

- 分页原理：Mybatis Plus 拦截器（InnerInterceptor）
- 物理分页 vs 逻辑分页
- 分页查询优化：count 查询优化、大页码优化
- **动手实践**：实现一个通用分页工具类，支持排序、条件查询

#### 5. 代码生成器

- MyBatis-Plus Generator 使用
- 自定义代码模板：Controller、Service、Mapper、Entity、VO、DTO
- 一键生成 CRUD 代码

#### 6. 高级映射

- 一对一、一对多、多对多关联查询
- `@TableName`、`@TableField`、`@TableId` 注解详解
- 字段类型处理器：TypeHandler、枚举处理器、JSON 处理器
- 主键策略：ASSIGN_ID（雪花算法）、AUTO、INPUT、UUID

#### **7. 自定义 SQL**

- 在 Mapper.xml 中写复杂 SQL
- `@Select`、`@Update` 注解写 SQL
- 条件构造器 + 自定义 XML 结合使用

#### 8.进阶了解（源码级理解）

- MyBatis-Plus 启动流程与注入原理
- `BaseMapper` 方法如何自动生成 SQL（SqlInjector）
- 拦截器链原理（InnerInterceptor）
- 分页插件源码分析
- 扩展自定义通用方法（比如自定义批量插入）