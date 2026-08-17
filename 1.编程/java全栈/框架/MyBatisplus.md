# 什么是mybatisplus

MyBatis-Plus（简称 MP）是一个 MyBatis的增强工具，在 MyBatis 的基础上只做增强不做改变，为 简化开发、提高效率而生。

官方文档 [简介 | MyBatis-Plus](https://baomidou.com/introduce/)

## **特性**

- **无侵入**：只做增强不做改变，引入它不会对现有工程产生影响，如丝般顺滑
- **损耗小**：启动即会自动注入基本 CURD，性能基本无损耗，直接面向对象操作
- **强大的 CRUD 操作**：内置通用 Mapper、通用 Service，仅仅通过少量配置即可实现单表大部分 CRUD 操作，更有强大的条件构造器，满足各类使用需求
- **支持 Lambda 形式调用**：通过 Lambda 表达式，方便的编写各类查询条件，无需再担心字段写错
- **支持主键自动生成**：支持多达 4 种主键策略（内含分布式唯一 ID 生成器 - Sequence），可自由配置，完美解决主键问题
- **支持 ActiveRecord 模式**：支持 ActiveRecord 形式调用，实体类只需继承 Model 类即可进行强大的 CRUD 操作
- **支持自定义全局通用操作**：支持全局通用方法注入（ Write once, use anywhere ）
- **内置代码生成器**：采用代码或者 Maven 插件可快速生成 Mapper 、 Model 、 Service 、 Controller 层代码，支持模板引擎，更有超多自定义配置等您来使用
- **内置分页插件**：基于 MyBatis 物理分页，开发者无需关心具体操作，配置好插件之后，写分页等同于普通 List 查询
- **分页插件支持多种数据库**：支持 MySQL、MariaDB、Oracle、DB2、H2、HSQL、SQLite、Postgre、SQLServer 等多种数据库
- **内置性能分析插件**：可输出 SQL 语句以及其执行时间，建议开发测试时启用该功能，能快速揪出慢查询
- **内置全局拦截插件**：提供全表 delete 、 update 操作智能分析阻断，也可自定义拦截规则，预防误操作

## 支持数据库 

![image-20260530223317473](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20260530223317473.png)

任何能使用 MyBatis 进行增删改查，并且支持标准 SQL 的数据库应该都在 MyBatis-Plus 的支持范围内

## 框架结构

![image-20260530223517088](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20260530223517088.png)

### 一、左侧：MyBatis-Plus 的核心执行流程

这部分展示了 MP 是如何**自动生成 SQL 并注入 MyBatis 容器**的，是它实现 “无 SQL CRUD” 的关键逻辑。

1. **Scan Entity（扫描实体类）**
   - 项目启动时，MP 会扫描你定义的 `@TableName` 实体类。
   - 利用**反射机制（Reflection extraction）**，自动解析实体的字段、类型、注解（比如 `@TableId`、`@TableField`）。
2. **Analysis Table Name Column（解析表名与列）**
   - 把实体类的信息，映射成数据库里的**表名、列名、主键策略**。
   - 自动生成对应的 CRUD SQL：`Insert` / `Update` / `Delete` / `Select` 等基础语句。
3. **Injection Mybatis Container（注入 MyBatis 容器）**
   - MP 会把生成好的 SQL，动态注入到 MyBatis 的 `SqlSession` 容器中。
   - 这样你在调用 `BaseMapper` 的方法（如 `insert()`、`selectById()`）时，就能直接执行这些预生成的 SQL，不用自己写 XML 或注解 SQL。

------

### 二、右侧：MyBatis-Plus 的模块组成

这部分是 MP 的核心依赖与功能模块，其中 `mybatis-plus-boot-starter` 是 Spring Boot 项目的入口依赖，它整合了下面 4 个核心模块：

表格

| 模块           | 作用说明                                                     |
| -------------- | ------------------------------------------------------------ |
| **core**       | 核心功能模块，包含 CRUD 封装、条件构造器（`QueryWrapper`）、主键策略、分页插件、逻辑删除等核心能力 |
| **annotation** | 注解模块，提供 `@TableName`、`@TableId`、`@TableField` 等注解，用于实体类与数据库表的映射配置 |
| **extension**  | 扩展模块，提供多租户、动态表名、SQL 性能分析、乐观锁、字段自动填充等高级扩展功能 |
| **generator**  | 代码生成器模块，可一键生成 `Entity`、`Mapper`、`Service`、`Controller` 层代码，大幅提升开发效率 |

------

### 三、整体关系总结

`mybatis-plus-boot-starter` 是你在 Spring Boot 中引入的依赖入口，它整合了 core/annotation/extension/generator 这 4 个模块。

项目启动后，`core` 模块就会按照左侧的流程，自动扫描实体、解析表结构、生成 SQL 并注入 MyBatis 容器，让你开箱即用 MP 的所有能力。

## yml 配置

```
# 1. 数据源（必须先配这个）
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/mp_demo?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: 123456
    # 连接池（默认 Hikari，可选）
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5

# 2. MyBatis‑Plus 核心配置
mybatis-plus:
  # Mapper XML 位置
  mapper-locations: classpath*:/mapper/**/*.xml
  # 实体类包（别名）
  type-aliases-package: com.example.demo.entity
  # 类型处理器包（如自定义枚举）
  type-handlers-package: com.example.demo.handler

  # MyBatis 原生配置
  configuration:
    map-underscore-to-camel-case: true  # 下划线 → 驼峰（默认 true）
    cache-enabled: false                  # 二级缓存（开发关）
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # 打印完整 SQL（开发必开）
    default-statement-timeout: 3000

  # 全局策略
  global-config:
    db-config:
      # 主键策略：AUTO(自增)、ASSIGN_ID(雪花)、INPUT(手动)、UUID
      id-type: auto
      # 逻辑删除（常用）
      logic-delete-field: deleted       # 全局逻辑删除字段名
      logic-delete-value: 1             # 已删除
      logic-not-delete-value: 0         # 未删除
      # 字段策略（控制 NULL 是否更新）
      insert-strategy: not_null
      update-strategy: not_null
      where-strategy: not_null
      # 表名前缀（可选）
      # table-prefix: t_

    # 刷新 mapper（开发热加载）
    refresh-mapper: true
```

## 三种查询方法

1. BaseMapper 自动方法（无需任何配置）
   └── userService.save(), userService.removeById() 等
2. @Select/@Update 等注解（适合简单查询）
   └── @Select("SELECT * FROM users WHERE id = #{id}")
3. XML 映射文件（适合复杂动态SQL）
   └── selectByConditions 动态多条件查询

| 方式                        | 代码示例                                                     | SQL 来源              |
| --------------------------- | ------------------------------------------------------------ | --------------------- |
| BaseMapper 自动             | `java<br>// 查询所有<br>List<User> list = baseMapper.selectList(null);<br>// 按主键查询<br>User user = baseMapper.selectById(1L);<br>` | MyBatis Plus 自动生成 |
| 注解 @Select 手写 SQL       | `java<br>@Select("SELECT * FROM user WHERE username = #{username}")<br>User selectByUsername(@Param("username") String username);<br>` | 手写 SQL（注解内）    |
| LambdaQueryWrapper 动态构建 | `java<br>LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();<br>wrapper.like(User::getUsername, "admin")<br>       .ge(User::getAge, 18);<br>List<User> list = baseMapper.selectList(wrapper);<br>` | MyBatis Plus 动态拼接 |

# 入门案例

### 1开发环境

\- Java 17

\- Spring Boot 3.2.0

\- MyBatis Plus 3.5.5

\- H2 Database (开发/测试)

\- MySQL 8.x (生产环境)

## 2创建数据库和表

| id   | name   | age  | email                                           |
| ---- | ------ | ---- | ----------------------------------------------- |
| 1    | Jone   | 18   | [test1@baomidou.com](mailto:test1@baomidou.com) |
| 2    | Jack   | 20   | [test2@baomidou.com](mailto:test2@baomidou.com) |
| 3    | Tom    | 28   | [test3@baomidou.com](mailto:test3@baomidou.com) |
| 4    | Sandy  | 21   | [test4@baomidou.com](mailto:test4@baomidou.com) |
| 5    | Billie | 24   | [test5@baomidou.com](mailto:test5@baomidou.com) |

```
CREATE DATABASE `mybatis_plus` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
 USE `mybatis_plus`;
 CREATE TABLE `user` (
 `id` BIGINT(20) NOT NULL COMMENT '主键ID',
 `name` VARCHAR(30) DEFAULT NULL COMMENT '姓名',
 `age` INT(11) DEFAULT NULL COMMENT '年龄',
 `email` VARCHAR(50) DEFAULT NULL COMMENT '邮箱',
 PRIMARY KEY (`id`)
 ) ENGINE=INNODB DEFAULT CHARSET=utf8;
 INSERT INTO USER (id, NAME, age, email) VALUES
 (1, 'Jone', 18, 'test1@baomidou.com'),
 (2, 'Jack', 20, 'test2@baomidou.com'),
 (3, 'Tom', 28, 'test3@baomidou.com'),
 (4, 'Sandy', 21, 'test4@baomidou.com'),
 (5, 'Billie', 24, 'test5@baomidou.com');
```

### 3创建一个初始springboot工程

使用 Spring Initializr 快速初始化一个 Spring Boot 工程

核心依赖

```
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.15</version>
</dependency>
```

引入依赖

```
<dependencies>
 <dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter</artifactId>
 </dependency>
 <dependency>
 <groupId>org.springframework.boot</groupId>
更多Java –大数据 – 前端 – UI/UE - Android - 人工智能资料下载，可访问百度：尚硅谷官网(www.atguigu.com)
<artifactId>spring-boot-starter-test</artifactId>
 <scope>test</scope>
 </dependency>
 <dependency>
 <groupId>com.baomidou</groupId>
 <artifactId>mybatis-plus-boot-starter</artifactId>
 <version>3.5.1</version>
 </dependency>
 <dependency>
 <groupId>org.projectlombok</groupId>
 <artifactId>lombok</artifactId>
 <optional>true</optional>
 </dependency>
 <dependency>
 <groupId>mysql</groupId>
 <artifactId>mysql-connector-java</artifactId>
 <scope>runtime</scope>
 </dependency>
 </dependencies>
```



## 4编写代码和配置

**创建实体类**

```
@Data
@TableName("`user`")
public class User {
    private Long id;
    private String name;
    private Integer age;
    private String email;
}
```

**编写mapper接口**

```
public interface UserMapper extends BaseMapper<User> {

}
```

**yml**

**H2数据库配置**

```
# DataSource Config
spring:
  datasource:
    driver-class-name: org.h2.Driver
    username: root
    password: test
  sql:
    init:
      schema-locations: classpath:db/schema-h2.sql
      data-locations: classpath:db/data-h2.sql
```

**测试类**

```
@SpringBootTest
public class SampleTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        Assert.isTrue(5 == userList.size(), "");
        userList.forEach(System.out::println);
    }

}
```

**输出**

```
User(id=1, name=Jone, age=18, email=test1@baomidou.com)
User(id=2, name=Jack, age=20, email=test2@baomidou.com)
User(id=3, name=Tom, age=28, email=test3@baomidou.com)
User(id=4, name=Sandy, age=21, email=test4@baomidou.com)
User(id=5, name=Billie, age=24, email=test5@baomidou.com)
```

## 5执行过程

### **执行链条**

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              调用链路                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. 测试类调用                                                               │
│     userService.list()                                                      │
│         │                                                                   │
│         ▼                                                                   │
│  2. UserServiceImpl (继承 ServiceImpl<UserMapper, User>)                    │
│     └── list() 方法来自父类 IService                                         │
│         │                                                                   │
│         ▼                                                                   │
│  3. ServiceImpl 内部调用                                                     │
│     └── baseMapper.selectList(null)                                         │
│         │                                                                   │
│         ▼                                                                   │
│  4. UserMapper (继承 BaseMapper<User>)                                      │
│     └── MyBatis Plus 自动生成 SQL                                           │
│         │                                                                   │
│         ▼                                                                   │
│  5. 执行 SQL                                                                 │
│     SELECT id,username,email,age,create_time,update_time FROM users         │
│         │                                                                   │
│         ▼                                                                   │
│  6. 结果映射                                                                 │
│     ResultSet → User 对象                                                   │
│         │                                                                   │
│         ▼                                                                   │
│  7. 返回 List<User>                                                          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 核心组件关系

```
┌──────────────────┐    继承     ┌──────────────────┐
│   UserService    │ ──────────► │   IService<User> │
│    (接口)        │             │   (CRUD方法)      │
└──────────────────┘             └──────────────────┘
         ▲
         │ 实现
         │
┌──────────────────┐    继承     ┌──────────────────┐
│ UserServiceImpl  │ ──────────► │ ServiceImpl<     │
│    (实现类)      │             │  UserMapper,User>│
└──────────────────┘             └──────────────────┘
         │                              │
         │ 持有                         │ 持有
         ▼                              ▼
┌──────────────────┐    继承     ┌──────────────────┐
│   UserMapper     │ ──────────► │  BaseMapper<User>│
│    (接口)        │             │   (CRUD方法)      │
└──────────────────┘             └──────────────────┘
```

### 详细执行步骤

**1. 启动阶段**

- Spring 扫描 mapper 包下的接口
- MyBatis Plus 为每个 Mapper 接口创建 动态代理类
- 代理类实现了 BaseMapper 中定义的所有 CRUD 方法

**2. 调用 userService.list()**

- list() 方法来自 IService 接口
- ServiceImpl 已实现该方法

**3. ServiceImpl 内部实现**

**4. BaseMapper 的 selectList() 方法**

- UserMapper 继承 BaseMapper<User>
- BaseMapper 定义了 selectList() 等方法
- MyBatis Plus 自动生成 SQL ，无需手写

**5. SQL 自动生成**

MyBatis Plus 根据实体类 User 自动生成 SQL：

```
@Data
@TableName("users")  // 表名
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String username;
    // ...
}
```
生成的 SQL：

```
SELECT id, username, email, age, create_time, update_time FROM users
```
**6. 结果映射**

- 数据库字段 create_time → Java 属性 createTime （自动驼峰转换）
- 返回 List<User> 对象

# 持久层接口

| 接口                  | 层级                  | 职责                         | 继承自        |
| --------------------- | --------------------- | ---------------------------- | ------------- |
| **Mapper Interface**  | 数据访问层（DAO）     | **只做数据库 CRUD**          | BaseMapper<T> |
| **Service Interface** | 业务逻辑层（Service） | **处理业务、事务、组合操作** | IService<T>   |

**ervice Interface 是业务层接口，它内部 持有并调用 Mapper Interface，专门给上层（Controller）提供业务服务。**

**Mapper 负责和数据库打交道（CRUD），Service 负责处理业务逻辑（事务、组合操作）**

## Mapper Interface

BaseMapper 是 Mybatis-Plus 提供的一个通用 Mapper 接口，它封装了一系列常用的数据库操作方法，包括增、删、改、查等。通过继承 BaseMapper，开发者可以快速地对数据库进行操作，而无需编写繁琐的 SQL 语句。

你不需要写任何实现类，MyBatis-Plus 会自动生成

```
package com.baomidou.mybatisplus.core.mapper;
 public interface BaseMapper<T> extends Mapper<T> {
 /**
 * 插入一条记录
* @param entity 实体对象
*/
 int insert(T entity);
 /**
 * 根据 ID 删除
* @param id 主键ID
 */
 int deleteById(Serializable id);
 /**
 * 根据实体(ID)删除
* @param entity 实体对象
* @since 3.4.4
 */
更多Java –大数据 – 前端 – UI/UE - Android - 人工智能资料下载，可访问百度：尚硅谷官网(www.atguigu.com)
    int deleteById(T entity);
    /**
     * 根据 columnMap 条件，删除记录
     * @param columnMap 表字段 map 对象
     */
    int deleteByMap(@Param(Constants.COLUMN_MAP) Map<String, Object> columnMap);
    /**
     * 根据 entity 条件，删除记录
     * @param queryWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 
语句）
     */
    int delete(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);
    /**
     * 删除（根据ID 批量删除）
     * @param idList 主键ID列表(不能为 null 以及 empty)
     */
    int deleteBatchIds(@Param(Constants.COLLECTION) Collection<? extends 
Serializable> idList);
    /**
     * 根据 ID 修改
     * @param entity 实体对象
     */
    int updateById(@Param(Constants.ENTITY) T entity);
    /**
     * 根据 whereEntity 条件，更新记录
     * @param entity        实体对象 (set 条件值,可以为 null)
     * @param updateWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 
where 语句）
     */
    int update(@Param(Constants.ENTITY) T entity, @Param(Constants.WRAPPER) 
Wrapper<T> updateWrapper);
    /**
     * 根据 ID 查询
     * @param id 主键ID
     */
    T selectById(Serializable id);
    /**
     * 查询（根据ID 批量查询）
     * @param idList 主键ID列表(不能为 null 以及 empty)
     */
    List<T> selectBatchIds(@Param(Constants.COLLECTION) Collection<? extends 
Serializable> idList);
    /**
     * 查询（根据 columnMap 条件）
     * @param columnMap 表字段 map 对象
     */
    List<T> selectByMap(@Param(Constants.COLUMN_MAP) Map<String, Object> 
columnMap);
    /**
更多Java –大数据 – 前端 – UI/UE - Android - 人工智能资料下载，可访问百度：尚硅谷官网(www.atguigu.com)
     * 根据 entity 条件，查询一条记录
     * <p>查询一条记录，例如 qw.last("limit 1") 限制取一条记录, 注意：多条数据会报异常
</p>
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    default T selectOne(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper) {
        List<T> ts = this.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(ts)) {
            if (ts.size() != 1) {
                throw ExceptionUtils.mpe("One record is expected, but the query 
result is multiple records");
            }
            return ts.get(0);
        }
        return null;
    }
    /**
     * 根据 Wrapper 条件，查询总记录数
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    Long selectCount(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);
    /**
     * 根据 entity 条件，查询全部记录
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    List<T> selectList(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);
    /**
     * 根据 Wrapper 条件，查询全部记录
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    List<Map<String, Object>> selectMaps(@Param(Constants.WRAPPER) Wrapper<T> 
queryWrapper);
    /**
     * 根据 Wrapper 条件，查询全部记录
     * <p>注意： 只返回第一个字段的值</p>
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    List<Object> selectObjs(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);
    /**
     * 根据 entity 条件，查询全部记录（并翻页）
     * @param page         分页查询条件（可以为 RowBounds.DEFAULT）
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    <P extends IPage<T>> P selectPage(P page, @Param(Constants.WRAPPER) 
Wrapper<T> queryWrapper);
    /**
     * 根据 Wrapper 条件，查询全部记录（并翻页）
     * @param page         分页查询条件
     * @param queryWrapper 实体对象封装操作类
     */
    <P extends IPage<Map<String, Object>>> P selectMapsPage(P page, 
@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);
```

## Service Interface

[IService](https://gitee.com/baomidou/mybatis-plus/blob/3.0/mybatis-plus-extension/src/main/java/com/baomidou/mybatisplus/extension/service/IService.java) 是 MyBatis-Plus 提供的一个通用 Service 层接口，它封装了常见的 CRUD 操作，包括插入、删除、查询和分页等。通过继承 IService 接口，可以快速实现对数据库的基本操作，同时保持代码的简洁性和可维护性。

IService 接口中的方法命名遵循了一定的规范，如 get 用于查询单行，remove 用于删除，list 用于查询集合，page 用于分页查询，这样可以避免与 Mapper 层的方法混淆。

**Service Interface 是业务层接口，它内部 持有并调用 Mapper Interface，专门给上层（Controller）提供业务服务。**

**Mapper 负责和数据库打交道（CRUD），Service 负责处理业务逻辑（事务、组合操作）。**



# CRUD

## 插入

```
@Test
    public void testInsert() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setAge(25);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        userService.save(user);
        System.out.println("插入用户ID: " + user.getId());
    }
```

执行SQL

```
INSERT INTO users ( username, email, age, create_time, update_time ) VALUES ( ?, ?, ?, ?, ? )
Parameters: testuser, test@example.com, 25, 2026-05-30T23:41:40.727871500, 2026-05-30T23:41:40.727871500
<== Updates: 1
```

## 删除

#### **通过id删除**

```
   @Test
    public void testDelete() {
        boolean result = userService.removeById(1L);
        System.out.println("删除结果: " + (result ? "成功" : "失败"));
    }
```

执行SQL

```
DELETE FROM users WHERE id=?
Parameters: 1(Long)
<== Updates: 1
```

#### 批量删除

```
  @Test
    public void testBatchDelete() {
        int count = userService.batchDeleteUsers(Arrays.asList(2L, 3L));
        System.out.println("批量删除数量: " + count);
    }
```

SQL

```
DELETE FROM users WHERE id IN (?, ?)
Parameters: 2(Long), 3(Long)
<== Updates: 2
```

#### 条件删除

```
   @Test
    public void testConditionDelete() {
        int count = userService.deleteUsersByAge(25);
        System.out.println("条件删除数量: " + count);
    }
```

SQL

```
DELETE FROM users WHERE id IN (?, ?)
Parameters: 2(Long), 3(Long)
<== Updates: 2
```

## 修改

```
    @Test
    public void testUpdate() {
        User user = userService.getById(1L);
        if (user != null) {
            user.setAge(20);
            user.setUpdateTime(LocalDateTime.now()); //  设置更新时间为当前时间
            userService.updateById(user); //  执行更新操作
            System.out.println("更新成功");
        }
    }
```

SQL

```
UPDATE users SET username=?, email=?, age=?, create_time=?, update_time=? WHERE id=?
Parameters: admin, admin@example.com, 20(Integer), 2026-05-30T23:50:57.695632, 2026-05-30T23:50:59.530865200, 1(Long)
```

## 查询

### 查询所有

```
    @Test
    public void testSelectAll() {
        List<User> users = userService.list();
        System.out.println("用户列表:");
        users.forEach(user -> System.out.println(user));
    }
```

### 通过id查询

```
   @Test
    public void testSelectById() {
        User user = userService.getById(1L);
        if (user != null) {
            System.out.println("查询到用户: " + user);
        } else {
            System.out.println("未找到用户");
        }
    }
```

批量查询



### 分页查询

```
   @Test
    public void testPageQuery() {
        List<User> users = userService.getUsersByPage(1, 10);
        System.out.println("分页查询结果:");
        users.forEach(user -> System.out.println(user));
    }
```

SQL

```
SELECT COUNT(*) AS total FROM users          -- 先统计总数
SELECT id, username, email, age, create_time, update_time FROM users LIMIT ?   -- 再分页
Parameters: 10(Long)
```

### 条件查询

```
 @Test
    public void testConditionQuery() {
        List<User> users = userService.getUsersByAgeGreaterThan(20);
        System.out.println("年龄大于20的用户:");
        users.forEach(user -> System.out.println(user));
    }
```

SQL

```
SELECT * FROM users WHERE age > ?
Parameters: 20(Integer)
```

# 常用注解

## @TableName

MyBatis-Plus在确定操作的表时，由BaseMapper的泛型决定，即实体类型决 定，且默认操作的表名和实体类型的类名一致

在实体类类型上添加@TableName("t_user")，标识实体类对应的表，即可成功执行SQL语句

在开发的过程中，我们经常遇到以上的问题，即实体类所对应的表都有固定的前缀，例如t_或tbl_ 此时，可以使用MyBatis-Plus提供的全局配置，为实体类所对应的表名设置默认的前缀，那么就 不需要在每个实体类上通过@TableName标识实体类对应的表

```
@TableName("sys_user")
public class User {
    private Long id;
    private String name;
    private Integer age;
    private String email;
}
```

## @TableId

MyBatis-Plus在实现CRUD时，会默认将id作为主键列，并在插入数据时，默认 基于雪花算法的策略生成id

在实体类中uid属性上通过@TableId将其标识为主键，即可成功执行SQL语句

```
@TableName("sys_user")
public class User {
    @TableId
    private Long id;
    private String name;
    private Integer age;
    private String email;
}
```

@TableId注解的value属性，指定表中的主键字段，@TableId("uid")或 @TableId(value="uid")

| 注解写法                                | 枚举值             | 作用说明                 | 适用场景                       |
| --------------------------------------- | ------------------ | ------------------------ | ------------------------------ |
| `@TableId(type = IdType.AUTO)`          | **AUTO**           | 数据库**自增**           | MySQL 自增主键、int/bigint     |
| `@TableId(type = IdType.NONE)`          | **NONE**           | 无策略，跟随**全局配置** | 不想单独指定，用全局默认       |
| `@TableId(type = IdType.INPUT)`         | **INPUT**          | **手动输入**ID           | 自己 setId，程序控制主键       |
| `@TableId(type = IdType.ASSIGN_ID)`     | **ASSIGN_ID**      | **雪花算法**（默认）     | 分布式 ID、Long 类型、全局唯一 |
| `@TableId(type = IdType.ASSIGN_UUID)`   | **ASSIGN_UUID**    | 自动生成 UUID            | 字符串主键、唯一标识           |
| `@TableId(type = IdType.ID_WORKER)`     | **ID_WORKER**      | 旧版雪花算法             | 兼容旧项目，不推荐新用         |
| `@TableId(type = IdType.ID_WORKER_STR)` | **ID_WORKER_STR`** | 旧版雪花字符串           | 兼容旧项目，不推荐新用         |

**雪花算法**

```
雪花算法（Snowflake）超通俗解释
一句话：它是一种能生成全局唯一、趋势递增、不重复的长数字 ID 的算法。
MyBatis-Plus 默认主键策略 ASSIGN_ID 用的就是它。
1. 它长什么样？
就是一串 18~19 位的数字，比如：
plaintext
1562345678901234567
2. 为什么叫 “雪花”？
因为雪花世界上没有两片完全相同的，
这个算法生成的 ID 全世界唯一、绝对不重复，所以叫雪花算法。
3. 核心优点（开发必记）
✅ 全局唯一：分布式系统、多服务器、多线程都不会重复
✅ 趋势递增：数据库索引效率极高
✅ 不依赖数据库：纯代码生成，速度极快
✅ Long 类型：存数据库方便，比 UUID 性能好太多
✅ 无序但递增：不是连续自增，安全、防爬数据
4. 雪花 ID 由什么组成？（简单看）
一个 ID 分成 5 段：
符号位（1 位）：固定 0，表示正数
时间戳（41 位）：精确到毫秒，能用 69 年
机器 ID（10 位）：区分不同服务器
序列号（12 位）：同一毫秒内自增（1 毫秒能生成 4096 个 ID）
5. 为什么现在都用它？（对比 UUID / 自增）
表格
方式	优点	缺点	推荐度
数据库自增 AUTO	简单、有序	分布式会重复、分库分表麻烦	单机可用
UUID	全局唯一	太长、无序、索引性能差	❌ 不推荐
雪花算法	全局唯一、有序、高性能、Long 类型	依赖服务器时间	✅ 分布式首选
6. MyBatis-Plus 里怎么用？
① 实体类（最常用）
java
运行
@TableId(type = IdType.ASSIGN_ID)
private Long id; // 必须是 Long 类型
② 全局配置（推荐）
yaml
mybatis-plus:
  global-config:
    db-config:
      id-type: assign_id # 全局雪花算法
超精简总结
雪花算法 = 分布式系统专用的全局唯一 ID 生成器
生成 18 位纯数字 Long ID
不重复、高性能、有序
MyBatis-Plus 默认主键策略
分布式项目必用
```

## @TableField

MyBatis-Plus在执行SQL语句时，要保证实体类中的属性名和 表中的字段名一致

1若实体类中的属性使用的是驼峰命名风格，而表中的字段使用的是下划线命名风格 例如实体类属性userName，表中字段user_name 

此时MyBatis-Plus会自动将下划线命名风格转化为驼峰命名风格 相当于在MyBatis中配置

2若实体类中的属性和表中的字段不满足情况

 例如实体类属性name，表中字段username 此时需要在实体类属性上使用@TableField("username")设置属性所对应的字段名

```
@TableName("sys_user")
public class User {
    @TableId
    private Long id;
    @TableField("nickname") // 映射到数据库字段 "nickname"
    private String name;
    private Integer age;
    private String email;
}
```



## @TableLogic

- 物理删除：真实删除，将对应数据从数据库中删除，之后查询不到此条被删除的数据 逻辑删除：
- 假删除，将对应数据中代表是否被删除字段的状态修改为“被删除状态”，之后在数据库 中仍旧能看到此条数据记录 使用场景：可以进行数据恢复

# 条件构造器

- **AbstractWrapper**：这是一个抽象基类，提供了所有 Wrapper 类共有的方法和属性。它定义了条件构造的基本逻辑，包括字段（column）、值（value）、操作符（condition）等。所有的 QueryWrapper、UpdateWrapper、LambdaQueryWrapper 和 LambdaUpdateWrapper 都继承自 AbstractWrapper。
- **QueryWrapper**：专门用于构造查询条件，支持基本的等于、不等于、大于、小于等各种常见操作。它允许你以链式调用的方式添加多个查询条件，并且可以组合使用 `and` 和 `or` 逻辑。
- **UpdateWrapper**：用于构造更新条件，可以在更新数据时指定条件。与 QueryWrapper 类似，它也支持链式调用和逻辑组合。使用 UpdateWrapper 可以在不创建实体对象的情况下，直接设置更新字段和条件。
- **LambdaQueryWrapper**：这是一个基于 Lambda 表达式的查询条件构造器，它通过 Lambda 表达式来引用实体类的属性，从而避免了硬编码字段名。这种方式提高了代码的可读性和可维护性，尤其是在字段名可能发生变化的情况下。
- **LambdaUpdateWrapper**：类似于 LambdaQueryWrapper，LambdaUpdateWrapper 是基于 Lambda 表达式的更新条件构造器。它允许你使用 Lambda 表达式来指定更新字段和条件，同样避免了硬编码字段名的问题。

| 类名                    | 父类            | 核心作用                                                     | 特点 / 说明                                                  |
| ----------------------- | --------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **AbstractWrapper**     | Wrapper         | **所有条件构造器的抽象基类**提供通用的条件方法（eq、like、ge、le 等） | 定义了**condition、column、value**等基础逻辑不能直接使用，仅作为父类被继承 |
| **QueryWrapper**        | AbstractWrapper | **普通查询条件构造器**                                       | 用**字符串写字段名**（如 `"username"`）支持所有查询条件      |
| **UpdateWrapper**       | AbstractWrapper | **普通更新条件构造器**                                       | 用**字符串写字段名**可直接 set 字段 + 拼接条件               |
| **LambdaQueryWrapper**  | AbstractWrapper | **Lambda 安全查询构造器**                                    | 用 **Lambda 方法引用**（`User::getUsername`）无硬编码、编译期检查 |
| **LambdaUpdateWrapper** | AbstractWrapper | **Lambda 安全更新构造器**                                    | 用 **Lambda 方法引用**安全、防写错字段、易维护               |

## wapper介绍

专门用来**自动生成 WHERE 条件、ORDER BY、GROUP BY、HAVING** 等 SQL 片段，**不用手写 SQL**，纯 Java 代码拼接。

Wrapper = 条件构造器 = 自动拼接 SQL 条件的工具

**Wrapper 能帮你用纯 Java 代码，动态、安全、简洁地拼出：**

- **任意复杂的 WHERE 条件**
- **SELECT 字段、排序、分页**
- **UPDATE 的 SET 字段 + 条件**
- **支持 Lambda，零硬编码**
- **防 SQL 注入、减少 XML、提升效率**

```
Wrapper（顶级接口）  条件构造抽象类，最顶端父类 
 ↓
AbstractWrapper（抽象类，提供所有条件方法 用于查询条件封装，生成 sql 的 where 条件）
 ↓
QueryWrapper / UpdateWrapper / LambdaQueryWrapper / LambdaUpdateWrapper
查询条件封装         跟新          推荐                 推荐            
```

| Wrapper 类型        | 写法                            | 优点                 | 缺点                     |
| ------------------- | ------------------------------- | -------------------- | ------------------------ |
| QueryWrapper        | `eq("username", "张三")`        | 简单直观             | 字段名写字符串，容易写错 |
| LambdaQueryWrapper  | `eq(User::getUsername, "张三")` | **防写错、编译检查** | 稍微长一点               |
| UpdateWrapper       | `set("age", 20).eq(...)`        | 适合动态更新         | 字符串字段名             |
| LambdaUpdateWrapper | `set(User::getAge, 20).eq(...)` | 安全优雅             | 推荐使用                 |

### 实现功能

```
1. 基础条件查询（最常用）
等于 / 不等于：eq、ne
大于 / 小于：gt、lt、ge、le
模糊查询：like、likeLeft、likeRight
范围查询：between、notBetween
包含 / 不包含：in、notIn
空值判断：isNull、isNotNull
示例：
java
运行
queryWrapper
  .eq("age", 18)
  .like("username", "张")
  .between("create_time", "2026-01-01", "2026-12-31");
2. 动态条件（condition 机制，你之前代码用到的）
核心：第一个参数为 true 才拼接条件，为 false 忽略
java
运行
// username 为 null 时，like 不生效
queryWrapper.like(StringUtils.isNotBlank(username), "username", username);
3. 复杂逻辑：and /or/ 括号嵌套
java
运行
queryWrapper
  .eq("sex", 1)
  .and(i -> i.gt("age", 20).or().like("email", "@qq.com"));
对应 SQL：
sql
WHERE sex=1 AND (age>20 OR email LIKE '%@qq.com%')
4. 字段选择（只查指定列）
java
运行
queryWrapper.select("id", "username", "age");
// Lambda 更安全
lambdaWrapper.select(User::getId, User::getUsername);
5. 排序
java
运行
queryWrapper
  .orderByAsc("age")         // 升序
  .orderByDesc("create_time");// 降序
6. 分页（配合 Page 对象）
java
运行
Page<User> page = new Page<>(1, 10); // 第1页，10条
userMapper.selectPage(page, queryWrapper);
7. 条件更新（UpdateWrapper）
不用实体，直接 SET 字段 + WHERE 条件
java
运行
updateWrapper
  .set("age", 18)
  .set("email", "new@123.com")
  .eq("id", 1001);
userMapper.update(null, updateWrapper);
8. Lambda 表达式（无硬编码，最推荐）
java
运行
LambdaQueryWrapper<User> lambda = Wrappers.lambdaQuery();
lambda.eq(User::getAge, 18).like(User::getUsername, "张");
字段名通过方法引用获取，编译期校验，杜绝字段名写错。
9. 自定义 SQL 片段（last、apply、exists）
last：直接拼到 SQL 末尾（如 FORCE INDEX）
java
运行
queryWrapper.last("FORCE INDEX (idx_age)");
apply：自定义函数 / 表达式
java
运行
queryWrapper.apply("DATE(create_time) = {0}", "2026-05-31");
exists：子查询
java
运行
queryWrapper.exists("SELECT 1 FROM role WHERE user.id=role.user_id");
10. 逻辑删除自动支持
配合 @TableLogic，Wrapper 自动忽略已删除数据。
```



## QuerWrapp

## UpdateWrapper

## Condition

动态SQL

在真正开发的过程中，组装条件是常见的功能，而这些条件数据来源于用户输入，是可选的，因 此我们在组装这些条件时，必须先判断用户是否选择了这些条件，若选择则需要组装该条件，若 没有选择则一定不能组装，以免影响SQL执行的结果

**condition = 条件开关**

**true → 拼接这个 SQL 条件**

**false → 忽略这个条件，不拼 SQL**

专门用来做 **动态 SQL**：**有值才拼，没值不拼**。

## LambdaQueryWrapper

## LambdaUpdateWrapper

# 插件

## 分页插件

MyBatis Plus自带分页插件，只要简单的配置即可实现分页功能

```
@Configuration
@MapperScan("scan.your.mapper.package")
public class MybatisPlusConfig {

    /**
     * 添加分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL)); // 如果配置多个插件, 切记分页最后添加
        // 如果有多数据源可以不配具体类型, 否则都建议配上具体的 DbType
        return interceptor;
    }
}
```

### 自定义分页

## 乐观锁悲观锁

# 通用枚举

# 代码生成器

## 引入依赖

```
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-generator</artifactId>
    <version>3.5.15</version>
</dependency>
```

## 配置

```
// 1. 创建代码生成器，传入数据库连接信息：url、用户名、密码
FastAutoGenerator.create("jdbc:mysql://localhost:3306/数据库名?useSSL=false&characterEncoding=utf8", "username", "password")
        // 2. 全局配置（作者、输出目录、日期格式等）
        .globalConfig(builder -> builder
                .author("Baomidou")           // 生成文件的作者名
                .outputDir(Paths.get(System.getProperty("user.dir")) + "/src/main/java") // 生成到项目 src/main/java
                .commentDate("yyyy-MM-dd")     // 注释中的日期格式
        )
        // 3. 包配置（生成的代码放在哪个包下）
        .packageConfig(builder -> builder
                .parent("com.baomidou.mybatisplus")   // 父包名（根包）
                .entity("entity")                     // 实体类包名
                .mapper("mapper")                     // Mapper接口包名
                .service("service")                   // Service接口包名
                .serviceImpl("service.impl")          // Service实现类包名
                .xml("mapper.xml")                    // Mapper XML文件包名
        )
        // 4. 策略配置（实体类、Controller、Mapper的生成规则）
        .strategyConfig(builder -> builder
                .entityBuilder()       // 实体类策略配置
                .enableLombok()        // 开启 Lombok 注解（生成 @Data @Getter @Setter）
        )
        // 5. 使用 Freemarker 模板引擎（必须引入依赖）
        .templateEngine(new FreemarkerTemplateEngine())
        // 6. 执行生成
        .execute();
```

# 多数据源

**一句话**：一个 Spring Boot 项目里**同时连多个数据库**（MySQL/Oracle/SQLite 等），并能**动态切换**用哪个库，这就是 “多数据源”。

**MyBatis‑Plus 多数据源 = 多库配置 + @DS 动态切库 + 自动路由**，是**读写分离、多库拆分**的标准方案

两种 MyBatis-Plus 的多数据源扩展插件：[ 开源](https://baomidou.com/guides/dynamic-datasource/#)生态的 `dynamic-datasource` 和 企业级生态的 `mybatis-mate`。  本文使用这个

dynamic-datasource  

https://github.com/baomidou/dynamic-datasource-spring-boot-starter  仓库

https://doc.xiuceyun.cn 文档

**特性**

- **数据源分组**：适用于多种场景，如读写分离、一主多从等。
- **敏感信息加密**：使用 `ENC()` 加密数据库配置信息。
- **独立初始化**：支持每个数据库独立初始化表结构和数据库。
- **自定义注解**：支持自定义注解，需继承 `DS`。
- **简化集成**：提供对 Druid、HikariCP 等连接池的快速集成。
- **组件集成**：支持 Mybatis-Plus、Quartz 等组件的集成方案。
- **动态数据源**：支持项目启动后动态增加或移除数据源。
- **分布式事务**：提供基于 Seata 的分布式事务方案。



## 创建数据库表

```
 CREATE DATABASE `mybatis_plus_1` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
 use `mybatis_plus_1`;
 CREATE TABLE product
 (
    id BIGINT(20) NOT NULL COMMENT '主键ID',
    name VARCHAR(30) NULL DEFAULT NULL COMMENT '商品名称',
    price INT(11) DEFAULT 0 COMMENT '价格',
    version INT(11) DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (id)
 );
```

## 引入依赖

```
<dependency>
 <groupId>com.baomidou</groupId>
 <artifactId>dynamic-datasource-spring-boot-starter</artifactId>
 <version>3.5.0</version>
 </dependency>
```

### 配置数据源

```
spring:
  datasource:
    dynamic:
      primary: master       # 默认数据源
      strict: false
      datasource:
        master:             # 主库（写）
          url: jdbc:mysql://localhost:3306/db_master
          username: root
          password: 123456
          driver-class-name: com.mysql.cj.jdbc.Driver
        slave:               # 从库（读）
          url: jdbc:mysql://localhost:3307/db_slave
          username: root
          password: 123456
          driver-class-name: com.mysql.cj.jdbc.Driver
        orderdb:             # 订单库
          url: jdbc:mysql://localhost:3308/db_order
          username: root
          password: 123456
```

## 切库`@DS` 注解

```
import com.baomidou.dynamic.datasource.annotation.DS;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    // 默认走 master（可不写 @DS）
    public void add() {
        userMapper.insert(...);
    }

    // 读操作走 slave
    @DS("slave")
    public List<User> list() {
        return userMapper.selectList(null);
    }

    // 订单操作走 orderdb
    @DS("orderdb")
    public void createOrder() {
        orderMapper.insert(...);
    }
}
```

**原理**

1. 启动时把所有 `datasource` 注册到**动态数据源路由**
2. 调用方法时，AOP 拦截 `@DS`，把**数据源名**放到**当前线程上下文**
3. 执行 SQL 时，路由从上下文拿到名字，选择真实数据源连接
4. 方法结束，自动清除上下文，避免污染

**关键特性**

- ✅ **注解零侵入**：不用改 Mapper/XML
- ✅ **数据源分组**：`slave_1`、`slave_2` 组成 slave 组，自动负载
- ✅ **敏感信息加密**：密码用 `ENC(xxx)` 加密
- ✅ **支持多种连接池**：Hikari、Druid
- ✅ **动态增删数据源**：运行时可加新库

## 创建用户service

```
public interface UserService extends IService<User> {
 }
 @DS("master") //指定所操作的数据源
@Service
 public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements 
UserService {
 }
```

## 创建商品service

```
public interface ProductService extends IService<Product> {
 }
```

##  测试 

```
@DS("slave_1")
 @Service
 public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> 
implements ProductService {
 }
 @Autowired
 private UserService userService;
 @Autowired
 private ProductService productService;
 @Test
 public void testDynamicDataSource(){
 System.out.println(userService.getById(1L));
 System.out.println(productService.getById(1L));
 }

```

结果：
1、都能顺利获取对象，则测试成功
2、如果我们实现读写分离，将写操作方法加上主库数据源，读操作方法加上从库数据源，自动切
换，是不是就能实现读写分离

# 项目实战