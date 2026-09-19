# MyBatis

## MyBatis 原理

https://www.bilibili.com/video/BV1WZ4y1P7Bp/?spm_id_from=333.337.search-card.all.click&vd_source=8e232ecca082f1beea092de8718f15c6

https://mybatis.net.cn/getting-started.html  MyBatis 官网

参考文章 

1 mybatis的一级二级缓存

https://blog.csdn.net/xing_jian1/article/details/123943859

https://mp.weixin.qq.com/s?__biz=MzU0OTE4MzYzMw==&mid=2247515501&idx=4&sn=cc0a281c98141cbc82afa411fd71d6ff&chksm=fbb13493ccc6bd854134aed3c0e02c43c415e59f509715aec6d1adaffd6940a892d16628a801&scene=27



XML 配置文件中包含了对 MyBatis 系统的核心设置，包括获取数据库连接实例的数据源（DataSource）以及决定事务作用域和控制方式的事务管理器（TransactionManager）

```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.mybatis.example.BlogMapper">
  <select id="selectBlog" resultType="Blog">
    select * from Blog where id = #{id}
  </select>
</mapper>
```

每个基于 MyBatis 的应用都是以一个 SqlSessionFactory 的实例为核心的。SqlSessionFactory 的实例可以通过 SqlSessionFactoryBuilder 获得。而 SqlSessionFactoryBuilder 则可以从 XML 配置文件或一个预先配置的 Configuration 实例来构建出 SqlSessionFactory 实例。XML配置文件也可以注解来代替

```
InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
```

有了 SqlSessionFactory，顾名思义，我们可以从中获得 SqlSession 的实例。SqlSession 提供了在数据库执行 SQL 命令所需的所有方法。你可以通过 SqlSession 实例来直接执行已映射的 SQL 语句

```
try (SqlSession session = sqlSessionFactory.openSession()) {
  Blog blog = (Blog) session.selectOne("org.mybatis.example.BlogMapper.selectBlog", 101);
}
```

通过注解实现

```
  @Select("SELECT * FROM blog WHERE id = #{id}")
  Blog selectBlog(int id);
```



## MyBatis demo

0 准备好一个test数据库  并创建一个user表

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20230201104230.png)

1 导入Maven坐标

```
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.4.5</version>
</dependency>
<!--mysql驱动坐标-->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.32</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>
<!--日志坐标-->
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.12</version>
</dependency>
```

2 编写user 类

```
public class User {
    private int id;
    private String username;
    private String password;
    }
```

3 编写UserMapper.xml文件

```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.zxy.mapper.userMapper">
    <select id="findAll" resultType="com.zxy.domain.User">
        select * from User
    </select>
</mapper>
```

4 编写 SqlMapConfig.xml文件

```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <!--通过properties标签加载外部properties文件-->
    <properties resource="jdbc.properties"></properties>

    <!--自定义别名-->
    <typeAliases>
        <typeAlias type="com.zxy.domain.User" alias="user"></typeAlias>
    </typeAliases>

    <!--数据源环境-->
    <environments default="developement">
        <environment id="developement">
            <transactionManager type="JDBC"></transactionManager>
            <dataSource type="POOLED">
                <property name="driver" value="${jdbc.driver}"/>
                <property name="url" value="${jdbc.url}"/>
                <property name="username" value="${jdbc.username}"/>
                <property name="password" value="${jdbc.password}"/>
            </dataSource>
        </environment>
    </environments>

    <!--加载映射文件-->
    <mappers>
        <mapper resource="com/zxy/mapper/UserMapper.xml"></mapper>
    </mappers>
</configuration>
```

5 编写数据库配置文件jdbc.properties

```
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/test
jdbc.username=root
jdbc.password=412826zxyZXY
```

6 编写test测试文件

```
public void test1() throws IOException {
    //加载核心配置文件
    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.XML");
    //获得sqlSession工厂对象
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    //获得sqlSession对象
    SqlSession sqlSession = sqlSessionFactory.openSession();
    //执行sql语句
    List<User> userList = sqlSession.selectList("userMapper.findAll");
//打印结果
System.out.println(userList);
//释放资源
 sqlSession.commit();
 sqlSession.close(); 
```

7 log4j.properties

```
### direct log messages to stdout ###
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target=System.out
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n

### direct messages to file mylog.log ###
log4j.appender.file=org.apache.log4j.FileAppender
log4j.appender.file.File=c:/mylog.log
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n

### set log levels - for more verbose logging change 'info' to 'debug' ###
log4j.rootLogger=debug, stdout
```

问题 1 

如果 查询出来的结果 中文乱码  MySQL的驱动版本太低 

问题 2

```
InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.XML");  这里需要抛出异常
      SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
      SqlSession sqlSession = sqlSessionFactory.openSession();
```

**mybatis的映射文件**   

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20230201105218.png)

## **mybatis核心配置文件**

1、properties标签：该标签可以加载外部的properties文件 

2、typeAliases标签：设置类型别名 

3、mappers标签：加载映射配置

4、environments标签：数据源环境配置标签

5、typeHandlers标签 类型转换器 

6、plugins标签

**typeHandlers**

你可以重写类型处理器或创建你自己的类型处理器来处理不支持的或非标准的类型。具体做法为：实现 org.apache.ibatis.type.TypeHandler 接口， 或继承一个很便利的类 org.apache.ibatis.type.BaseTypeHandler， 然 后可以选择性地将它映射到一个JDBC类型。例如需求：一个Java中的Date数据类型，我想将之存到数据库的时候存成一 个1970年至今的毫秒数，取出来时转换成java的Date，即java的Date与数据库的varchar毫秒值之间转换。

① 定义转换类继承类BaseTypeHandler 

② 覆盖4个未实现的方法，其中setNonNullParameter为java程序设置数据到数据库的回调方法，getNullableResult 为查询时 mysql的字符串类型转换成 java的Type类型的方法 

```
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
public class DateTypeHandler extends BaseTypeHandler<Date> {
    //将java类型 转换成 数据库需要的类型
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Date date, JdbcType jdbcType) throws SQLException {
        long time = date.getTime();
        preparedStatement.setLong(i,time);    }
    //将数据库中类型 转换成java类型
    //String参数  要转换的字段名称
    //ResultSet 查询出的结果集
    public Date getNullableResult(ResultSet resultSet, String s) throws SQLException {
        //获得结果集中需要的数据(long) 转换成Date类型 返回
        long aLong = resultSet.getLong(s);
        Date date = new Date(aLong);
        return date;    }
    //将数据库中类型 转换成java类型
    public Date getNullableResult(ResultSet resultSet, int i) throws SQLException {
        long aLong = resultSet.getLong(i);
        Date date = new Date(aLong);
        return date;   }
    //将数据库中类型 转换成java类型
    public Date getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        long aLong = callableStatement.getLong(i);
        Date date = new Date(aLong);
        return date;   }}
```

③ 在MyBatis核心配置文件中进行注册

```
<typeHandlers>
    <typeHandler handler="com.itheima.handler.DateTypeHandler"></typeHandler>
</typeHandlers>
```

 ④ 测试转换是否正确

**plugins标签**

MyBatis可以使用第三方的插件来对功能进行扩展，分页助手PageHelper是将分页的复杂操作进行封装，使用简单的方式即 可获得分页的相关数据 开发步骤：

 ① 导入通用PageHelper的坐标

```
<dependency>
<groupId>com.github.pagehelper</groupId>
<artifactId>pagehelper</artifactId>
<version>3.7.5</version>
</dependency>
<dependency>
<groupId>com.github.jsqlparser</groupId>
<artifactId>jsqlparser</artifactId>
<version>0.9.1</version>
</dependency
```

 ② 在mybatis核心配置文件中配置PageHelper插件

```
plugin interceptor="com.github.pagehelper.PageHelper">
<!-- 指定方言 不同的数据库采用不同的方言 -->
<property name="dialect" value="mysql"/>
</plugin>
```

 ③ 测试分页数据获取

```
PageHelper.startPage(3,3);

List<User> userList = mapper.findAll();
for (User user : userList) {
    System.out.println(user);
}

//获得与分页相关参数
PageInfo<User> pageInfo = new PageInfo<User>(userList);
System.out.println("当前页："+pageInfo.getPageNum());
System.out.println("每页显示条数："+pageInfo.getPageSize());
System.out.println("总条数："+pageInfo.getTotal());
System.out.println("总页数："+pageInfo.getPages());
System.out.println("上一页："+pageInfo.getPrePage());
System.out.println("下一页："+pageInfo.getNextPage());
System.out.println("是否是第一个："+pageInfo.isIsFirstPage());
System.out.println("是否是最后一个："+pageInfo.isIsLastPage());
```

## MyBatisXML 映射器

MyBatis 的真正强大在于它的语句映射，这是它的魔力所在。由于它的异常强大，映射器的 XML 文件就显得相对简单。如果拿它跟具有相同功能的 JDBC 代码进行对比，你会立即发现省掉了将近 95% 的代码。MyBatis 致力于减少使用成本，让用户能更专注于 SQL 代码。

SQL 映射文件只有很少的几个顶级元素（按照应被定义的顺序列出）：

- `cache` – 该命名空间的缓存配置。
- `cache-ref` – 引用其它命名空间的缓存配置。
- `resultMap` – 描述如何从数据库结果集中加载对象，是最复杂也是最强大的元素。
- `parameterMap` – 老式风格的参数映射。此元素已被废弃，并可能在将来被移除！请使用行内参数映射。文档中不会介绍此元素。
- `sql` – 可被其它语句引用的可重用语句块。
- `insert` – 映射插入语句。
- `update` – 映射更新语句。
- `delete` – 映射删除语句。
- `select` – 映射查询语句。

```
parameterType="输入参数类型" resultType="输出参数类型"
```

## MyBatis CRUD

1 在之前的测试类里添加 插入 删除 修改   查询 等操作

```
 @Test
// 插入操作
      public void test1() throws IOException {
      // 模拟数据
        User user = new User();
        user.setId(9);
        user.setUsername("zhuxiaoyi");
        user.setPassword("666666");
      InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.XML");
      SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
      SqlSession sqlSession = sqlSessionFactory.openSession();
        int insert = sqlSession.insert("userMapper.add", user);
        System.out.println(insert);
        // 需要手动提交事务
        sqlSession.commit();
        sqlSession.close();
    }
//  修改操作
    @Test
      public void test2() throws IOException {
        User user = new User();
        user.setId(9);
        user.setUsername("zzzz");
        user.setPassword("666666");
        InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        int update = sqlSession.update("userMapper.update", user);
        System.out.println(update);
        sqlSession.commit();
        sqlSession.close();
    }
//    删除操作
    @Test
    public void test3() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        int delete = sqlSession.delete("userMapper.delete",9);
        System.out.println(delete);
        sqlSession.commit();
        sqlSession.close();
    }
//    单个查询
    @Test
    public void test4() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        List<User> userList = sqlSession.selectList("userMapper.find",4);
        System.out.println(userList);
        sqlSession.commit();
        sqlSession.close();

    }
```

2 在mapper 文件里  编写对应的sql

```
<!--    查询单个-->
    <select id="find" resultType="com.zxy.user.User">
        select * from User where id=#{id}
    </select>
<!--    插入-->
    <insert id="add" parameterType="com.zxy.user.User">
        insert into user values (#{id},#{username},#{password})
    </insert>
<!--    修改-->
    <update id="update" parameterType="com.zxy.user.User">
        update user set username=#{username},password=#{password} where id=#{id}
    </update>
<!--    删除-->
    <delete id="delete" parameterType="com.zxy.user.User">
        delete from user where id=#{id}
    </delete>
```

**问题1** 

修改数据如果是中文会出现修改不了的问题

## MyBatis Dao

### 1 传统Dao开发方式

需要手动写出接口的实现

1 创建一个UserMapper接口

```
public interface UserMapper {
    public List<User> findAll() throws IOException;
}
```

2 创建一个UserMapperImper 类实现UserMapper 

```
public List<User> findAll() throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();
    List<User> userList = sqlSession.selectList("userMapper.findAll");
    sqlSession.close();
    return userList;
```

3 创建一个servicedemo 进行测试

```
UserMapper userMapper = new UserMapperImpl();
        List<User> all = userMapper.findAll();
        System.out.println(all);
```

### 2 mapper代理方式

Mapper 接口开发方法只需要程序员编写Mapper 接口（相当于Dao 接口），由Mybatis 框架根据接口定义创建接 口的动态代理对象，代理对象的方法体同上边Dao接口实现类方法。

 Mapper 接口开发需要遵循以下规范：

 1、 Mapper.xml文件中的namespace与mapper接口的全限定名相同 

 2、 Mapper接口方法名和Mapper.xml中定义的每个statement的id相同

 3、 Mapper接口方法的输入参数类型和mapper.xml中定义的每个sql的parameterType的类型相同 

4、 Mapper接口方法的输出参数类型和mapper.xml中定义的每个sql的resultType的类型相同

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20230201143619.png)



## MyBatis 动态SQL

include 抽取

```
<sql id="selectUser">select * from user </sql>
```

```
<include refid="selectUser"></include>
```

foreach

```
<where>
    <foreach collection="list" open="id in(" close=")" item="id" separator=",">
        #{id}
    </foreach>
</where>
```

if 

```
   select * from user
<where>
   <if test="id!=0">
       and id=#{id}
   </if>
   <if test="username!=null">
       and username=#{username}
   </if>
   <if test="password！=null">
       and password=#{password}
   </if>
</where>
```



：select查询 ：insert插入 ：update修改 ：delete删除 ：where条件 ：if判断 ：循环 ：sql片段抽取



## MyBatis  多表查询

### 1 一对一查询 

通过resultmap进行封装 

```
 <resultMap id="orderMap" type="order">
        <!--手动指定字段与实体属性的映射关系
            column: 数据表的字段名称
            property：实体的属性名称
        -->
        <id column="oid" property="id"></id>
        <result column="ordertime" property="ordertime"></result>
        <result column="total" property="total"></result>
      <result column="uid" property="user.id"></result>
        <result column="username" property="user.username"></result>
       <result column="password" property="user.password"></result
        <result column="birthday" property="user.birthday"></result>

或者在把user进行封装
        <!--
            property: 当前实体(order)中的属性名称(private User user)
            javaType: 当前实体(order)中的属性的类型(User)
        -->
        <association property="user" javaType="user">
            <id column="uid" property="id"></id>
            <result column="username" property="username"></result>
            <result column="password" property="password"></result>
            <result column="birthday" property="birthday"></result>
        </association>

    </resultMap>

    <select id="findAll" resultMap="orderMap">
         SELECT *,o.id oid FROM orders o,USER u WHERE o.uid=u.id
    </select>
```

### 2  一对多查询

### 3  多对多查询

## MyBatis   注解开发

@Insert：实现新增 

@Update：实现更新

 @Delete：实现删除 

@Select：实现查询

 @Result：实现结果集封装 

@Results：可以与@Result 一起使用，封装多个结果集

 @One：实现一对一结果集封装 

@Many：实现一对多结果集封装

```
@Insert("insert into user values(#{id},#{username},#{password},#{birthday})")
    public void save(User user);

    @Update("update user set username=#{username},password=#{password} where id=#{id}")
    public void update(User user);

    @Delete("delete from user where id=#{id}")
    public void delete(int id);

    @Select("select * from user where id=#{id}")
    public User findById(int id);

 @Results({
            @Result(id=true ,column = "id",property = "id"),
            @Result(column = "username",property = "username"),
            @Result(column = "password",property = "password"),
            @Result(
                    property = "orderList",
                    column = "id",
                    javaType = List.class,
                    many = @Many(select = "com.itheima.mapper.OrderMapper.findByUid")
            )
    })
    public List<User> findUserAndOrderAll();
```

加载映射关系   用注解代替配置文件  

```
<mappers>
    <!--指定接口所在的包-->
    <package name="com.itheima.mapper"></package>
</mappers>
```

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20230202152947.png)

![](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20230202153041746.png)

```
 @Before
    public void before() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        mapper = sqlSession.getMapper(UserMapper.class);
    }
    @Test
    public void testSave(){
        List<Order> all = mapper.findAll();
        for (Order order : all) {
            System.out.println(order);
        }
    可以抽取公共代码
```



# MyBatisPlus

## 理论

MyBatisPlus（简称MP）是基于MyBatis框架基础上开发的增强型工具，旨在简化开发、提高效率 通过刚才的案例，相信大家能够体会简化开发和提高效率这两个方面的优点。 MyBatisPlus的官网为: https://mp.baomidou.com/

无侵入：只做增强不做改变，不会对现有工程产生影响 强大的 CRUD 操作：内置通用 Mapper，少量配置即可实现单表CRUD 操作 支持 Lambda：编写查询条件无需担心字段写错 支持主键自动生成 内置分页插件

MyBatisPlus 生态圈

- [MybatisX (opens new window)](https://github.com/baomidou/MybatisX)- 一款全免费且强大的 IDEA 插件，支持跳转，自动补全生成 SQL，代码生成。
- [Mybatis-Mate (opens new window)](https://gitee.com/baomidou/mybatis-mate-examples)- 为 MyBatis-Plus 企业级模块，支持分库分表、数据审计、字段加密、数据绑定、数据权限、表结构自动生成 SQL 维护等高级特性。
- [Dynamic-Datasource (opens new window)](https://gitee.com/baomidou/dynamic-datasource-spring-boot-starter)- 基于 SpringBoot 的多数据源组件，功能强悍，支持 Seata 分布式事务。
- [Shuan (opens new window)](https://gitee.com/baomidou/shaun)- 基于 Pac4J-JWT 的 WEB 安全组件, 快速集成。
- [Kisso (opens new window)](https://github.com/baomidou/kisso)- 基于 Cookie 的单点登录组件。
- [Lock4j (opens new window)](https://gitee.com/baomidou/lock4j)- 基于 SpringBoot 同时支持 RedisTemplate、Redission、Zookeeper 的分布式锁组件。
- [Kaptcha (opens new window)](https://gitee.com/baomidou/kaptcha-spring-boot-starter)- 基于 SpringBoot 和 Google Kaptcha 的简单验证码组件，简单验证码就选它。
- [Aizuda 爱组搭 (opens new window)](https://gitee.com/aizuda)- 低代码开发平台组件库。

MyBatisPlus的优点 

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

## demo

### 1 创建SpringBoot工程

勾选所需要的技术   mybatis mysql 的驱动   springboot的版本  但是springboot的版本不要太低  有可能mysql的驱动也会太低，中文会有问题

### 2 创建数据库文件

```
create database if not exists mybatisplus_db character set utf8;
use mybatisplus_db;
CREATE TABLE user (
id bigint(20) primary key auto_increment,
name varchar(32) not null,
password varchar(32) not null,
age int(3) not null ,
tel varchar(32) not null
);
insert into user values(1,'Tom','tom',3,'18866668888');
insert into user values(2,'Jerry','jerry',4,'16688886666');
insert into user values(3,'Jock','123456',41,'18812345678');
insert into user values(4,'传智播客','itcast',15,'4006184000');
```

### 3 引入MP和Lombok依赖

```
<dependency>
<groupId>com.baomidou</groupId>
<artifactId>mybatis-plus-boot-starter</artifactId>
<version>3.4.1</version>
</dependency>
<dependency>
<groupId>com.alibaba</groupId>
<artifactId>druid</artifactId>
<version>1.1.16</version>
</dependency>
<dependency>
<groupId>org.projectlombok</groupId>
<artifactId>lombok</artifactId>
<!--<version>1.18.12</version>-->
</dependency>
```

Lombok常用注解

@Setter:为模型类的属性提供setter方法 

@Getter:为模型类的属性提供getter方法 

@ToString:为模型类的属性提供toString方法 

@EqualsAndHashCode:为模型类的属性提供equals和hashcode方法

 @Data:是个组合注解，包含上面的注解的功能 

@NoArgsConstructor:提供一个无参构造函数 

@AllArgsConstructor:提供一个包含所有参数的构造函数

### 4 配置application.yml文件

```
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/mybatisplus_db?serverTimezone=UTC
    username: root
    password: 412826zxyZXY
# 开启mp的日志（输出到控制台）
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

### 5 创建实体类

```
@Data
public class User {
private Long id;
private String name;
private String password;
private Integer age;
private String tel;
//setter...getter...toString方法略
}
```

### 6 创建Dao接口

```
@Mapper
public interface UserDao extends BaseMapper<User>{
}
```

继承BaseMapper 

### 7 编写引导类

```
@SpringBootApplication
//@MapperScan("com.itheima.dao")
public class Mybatisplus01QuickstartApplication {
public static void main(String[] args) {
SpringApplication.run(Mybatisplus01QuickstartApplication.class, args);}}
```

### 8 MP CRUD 

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20230204094312.png)

我们所调用的方法都是来自于DAO接口继承的BaseMapper类中。里面的方法有很多，我们后面会慢慢 去学习里面的内容。

### 9 分页功能

**1 分页**

```
IPage<T> selectPage(IPage<T> page, Wrapper<T> queryWrapper
```

IPage:用来构建分页查询条件

 Wrapper：用来构建条件查询的条件，目前我们没有可直接传为

Null IPage:返回值，你会发现构建分页条件和方法的返回值都是IPage

**2 设置分页拦截器让它受到Spring的管理**

```
@Configuration
public class MybatisPlusConfig {
@Bean
public MybatisPlusInterceptor mybatisPlusInterceptor(){
//1 创建MybatisPlusInterceptor拦截器对象
MybatisPlusInterceptor mpInterceptor=new MybatisPlusInterceptor();
//2 添加分页拦截器
mpInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
return mpInterceptor;}}
```

### 10 编写测试类

```
package com.itheima;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.dao.UserDao;
import com.itheima.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
@SpringBootTest
class Mybatisplus01QuickstartApplicationTests {
    @Autowired
    private UserDao userDao;
    @Test
    void testSave(){
        User user = new User();
        user.setName("黑马程序员");
        user.setPassword("itheima");
        user.setAge(12);
        user.setTel("4006184000");
        userDao.insert(user);   }
    @Test
    void testDelete(){
        userDao.deleteById(1621679910610853889L);  }
    @Test
    void testUpdate(){
        User user = new User();
        user.setId(1L);
        user.setName("Tom888");
        user.setPassword("tom888");
        userDao.updateById(user);   }
    @Test
    void testGetById(){
        User user = userDao.selectById(2L);
        System.out.println(user);    }
    @Test
    void testGetAll() {
        List<User> userList = userDao.selectList(null);
        System.out.println(userList);   }
    @Test
    void testGetByPage(){
        //IPage对象封装了分页操作相关的数据
        IPage page  = new Page(2,3);
        userDao.selectPage(page,null);
        System.out.println("当前页码值："+page.getCurrent());
        System.out.println("每页显示数："+page.getSize());
        System.out.println("一共多少页："+page.getPages());
        System.out.println("一共多少条数据："+page.getTotal());
        System.out.println("数据："+page.getRecords());  }}
```

## DQL查询

## 1 构建环境

1 取消初始化spring日志打印，

resources目录下添加logback.xml，名称固定

```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
</configuration>
```

2 关闭mybatisplus的启动图标

```
mybatis-plus:
configuration:
log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
global-config:
banner: off # 关闭mybatisplus启动图标
```

3 取消springboot打印图标

```
spring:
main:
banner-mode: off # 关闭SpringBoot启动图标(banner)
```

### 2 条件查询

MyBatisPlus将书写复杂的SQL查询条件进行了封装，使用编程的形式完成查询条件的组合。

```
//        方式一：按条件查询
//        QueryWrapper qw = new QueryWrapper();
//        qw.lt("age",10);
//        List<User> userList = userDao.selectList(qw);
//        System.out.println(userList);
//        QueryWrapper qw = new QueryWrapper();
//        qw.lt("age",18);
//        List<User> userList = userDao.selectList(qw);
//        System.out.println(userList);
//        方式二：lambda格式按条件查询
//        QueryWrapper<User> qw = new QueryWrapper<User>();
//        qw.lambda().lt(User::getAge, 10);
//        List<User> userList = userDao.selectList(qw);
//        System.out.println(userList);
        //方式三：lambda格式按条件查询
//        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<User>();
//        lqw.lt(User::getAge, 10);
//        List<User> userList = userDao.selectList(lqw);
//        System.out.println(userList);

        //并且与或者关系
//        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<User>();
//        //并且关系：10到30岁之间
//        //lqw.lt(User::getAge, 30).gt(User::getAge, 10);
//        //或者关系：小于10岁或者大于30岁
//        lqw.lt(User::getAge, 10).or().gt(User::getAge, 30);
//        List<User> userList = userDao.selectList(lqw);
//        System.out.println(userList);

//需求:查询数据库表中，根据输入年龄范围来查询符合条件的记录
//用户在输入值的时候，
//如果只输入第一个框，说明要查询大于该年龄的用户
//如果只输入第二个框，说明要查询小于该年龄的用户
//如果两个框都输入了，说明要查询年龄在两个范围之间的用户

        //模拟页面传递过来的查询数据
//        UserQuery uq = new UserQuery();
//        uq.setAge(1);
//        uq.setAge2(24);

        //null判定
//        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<User>();
//        lqw.lt(User::getAge, uq.getAge2());
//        if( null != uq.getAge()) {
//            lqw.gt(User::getAge, uq.getAge());
//        }
//        List<User> userList = userDao.selectList(lqw);
//        System.out.println(userList);

//        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<User>();
//        //先判定第一个参数是否为true，如果为true连接当前条件
////        lqw.lt(null != uq.getAge2(),User::getAge, uq.getAge2());
////        lqw.gt(null != uq.getAge(),User::getAge, uq.getAge());
//        lqw.lt(null != uq.getAge2(),User::getAge, uq.getAge2())
//           .gt(null != uq.getAge(),User::getAge, uq.getAge());
//        List<User> userList = userDao.selectList(lqw);
//        System.out.println(userList);

```

### 3 查询投影

目前我们在查询数据的时候，什么都没有做默认就是查询表中所有字段的内容，我们所说的查询投影 即不查询所有字段，只查询出指定内容的数据。 

查询指定字段   聚会    分组查询 

```
//        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<User>();
//        lqw.select(User::getId,User::getName,User::getAge);
//        QueryWrapper<User> lqw = new QueryWrapper<User>();
//        lqw.select("id","name","age","tel");
//        List<User> userList = userDao.selectList(lqw);
//        System.out.println(userList);
            // 聚合查询
//        QueryWrapper<User> lqw = new QueryWrapper<User>();
//        lqw.select("count(*) as count, tel");
//        lqw.groupBy("tel");
//        List<Map<String, Object>> userList = userDao.selectMaps(lqw);
//        System.out.println(userList);
```

### 4 查询条件设计

```
        //条件查询
//        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<User>();
//        //等同于=
//        lqw.eq(User::getName,"Jerry").eq(User::getPassword,"jerry");
//        User loginUser = userDao.selectOne(lqw);
//        System.out.println(loginUser);

//        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<User>();
//        //范围查询 lt le gt ge eq between
//        lqw.between(User::getAge,10,30);
//        List<User> userList = userDao.selectList(lqw);
//        System.out.println(userList);

//        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<User>();
//        //模糊匹配 like
//        lqw.likeLeft(User::getName,"J");
//        List<User> userList = userDao.selectList(lqw);
//        System.out.println(userList);
```

### 5  映射关系

当表的列名和模型类的属性名发生不一致，就会导致数据封装不到模型对象，这个时候就需要其中一 方做出修改，那如果前提是两边都不能改又该如何解决?

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20230204104211.png)

该问题主要是表的名称和模型类的名称不一致，导致查询失败，

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20230204104410.png)

## DML查询

### 1 ID生成策略

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20230204104516.png)

### 2 多记录删除

### 3  逻辑删除

物理删除:业务数据从数据库中丢弃，执行的是delete操作

 逻辑删除:为数据设置是否可用状态字段，删除时设置状态字段为不可用状态，数据保留在数据库 中，执行的是update操作

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20230204105817.png)

### 4 乐观锁

## 代码生成器

## 谷粒商城逆向工程


