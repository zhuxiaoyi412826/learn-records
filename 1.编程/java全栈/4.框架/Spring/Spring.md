

# Spring整体架构

**2002–2006 轻量级容器时代**：打败 EJB，IoC/AOP 成型，XML 为主

**2007–2013 注解 & SSM 时代**：注解普及、JavaConfig、传统单体 Web 主流

**2014–2017 微服务萌芽**：Spring Boot 简化开发，Spring Cloud 诞生

**2018–2022 响应式微服务**：Spring5/WebFlux，Boot2+Cloud 大规模落地

**2022–至今 云原生时代**：Spring6/Boot3，JDK17、GraalVM、K8s、原生镜像

![fWmQJ8DYV](D:\谷歌下载内容\fWmQJ8DYV.jpeg)

Spring 5 唯一长期支持（LTS）版本：**Spring Framework 5.3.x**

https://github.com/spring-projects/spring-framework.git  5.3X源码



# 什么是 Spring Data JPA？

用最简单的话讲：**Spring Data JPA 是 Spring 提供的一套简化数据库操作的框架，它基于 JPA 规范，让你不用写繁琐的 JDBC/MyBatis 代码，几乎只需要定义接口，就能自动实现增删改查、分页、排序等功能。**

它的核心价值：**告别重复的数据库操作代码，极大提升开发效率**。

------

## 先搞懂两个基础概念

1. **JPA**

   全称 Java Persistence API（Java 持久化 API），**是一套规范（接口 / 标准）**，不是具体实现。

   它规定了 Java 对象和数据库表映射、操作数据库的标准规则。

   常见实现：Hibernate（Spring Data JPA 默认用它）、EclipseLink。

   

2. **Spring Data**

   Spring 的一个大家族项目，统一对各种数据源（关系型数据库、Redis、MongoDB 等）提供**简化的数据访问层**。

   Spring Data JPA 就是它针对**关系型数据库 + JPA** 的子项目。

   

------

## Spring Data JPA 核心特点

1. 无需写实现类

   

   只需要定义一个

   接口

   ，继承 JpaRepository，自动拥有全套 CRUD 方法。

2. 方法名自动生成 SQL

   

   按照规则写方法名（比如 

   ```
   findByNameAndAge
   ```

   ），框架自动生成 SQL，不用手写。

3. 支持自定义 SQL

   

   复杂查询用 

   ```
   @Query
   ```

    注解直接写 JPQL 或原生 SQL。

4. 自带分页、排序、批量操作

   

   开箱即用，不用自己封装工具类。

5. 与 Spring Boot 无缝整合

   

   极少配置，直接开发。

------

## 最直观的代码示例（Spring Boot）

### 1. 引入依赖（Maven）

xml









```
<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<!-- 数据库驱动（以MySQL为例） -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 2. 实体类（映射数据库表）

用注解标注对象和表的关系：

java



运行







```
@Entity // 标识这是一个JPA实体类
@Table(name = "t_user") // 对应数据库表名
public class User {
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增
    private Long id;
    
    private String name;
    private Integer age;
    
    // getter、setter、构造器
}
```

### 3. Dao 层接口（核心！）

**只写接口，不写实现类**：

java



运行







```
// 继承 JpaRepository<实体类, 主键类型>
public interface UserRepository extends JpaRepository<User, Long> {

    // 方法名自动生成 SQL：SELECT * FROM t_user WHERE name = ?
    User findByName(String name);
    
    // 自动生成：SELECT * FROM t_user WHERE age > ?
    List<User> findByAgeGreaterThan(Integer age);
}
```

### 4. 直接使用

java



运行







```
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void test() {
        // 1. 新增/保存
        userRepository.save(new User("张三", 20));
        
        // 2. 查询所有
        List<User> all = userRepository.findAll();
        
        // 3. 根据ID查询
        User user = userRepository.findById(1L).orElse(null);
        
        // 4. 自定义方法查询
        User zhangsan = userRepository.findByName("张三");
        
        // 5. 分页查询
        Page<User> page = userRepository.findAll(PageRequest.of(0, 10));
        
        // 6. 删除
        userRepository.deleteById(1L);
    }
}
```

------

## Spring Data JPA 解决了什么痛点？

- 不用写 `JDBC` 的 `Connection`、`PreparedStatement`、结果集封装
- 不用像 MyBatis 那样写 XML 或注解 SQL（简单查询完全不用）
- 不用自己封装分页、排序、批量操作工具
- 统一数据访问层代码风格，维护成本极低

------

## 适用场景

- 快速开发中小型项目
- 单表 / 简单关联查询居多的业务
- 不想写大量重复 SQL 的场景

**不适合**：极其复杂的多表关联查询、存储过程重度使用（这种场景更适合 MyBatis）。

------

### 总结

1. **Spring Data JPA = Spring 对 JPA 的封装 + 简化数据操作**
2. 核心：**定义接口 → 继承父接口 → 直接用方法**，零实现
3. 底层默认实现是 **Hibernate**
4. 优势：开发极快、代码极简、适合快速开发