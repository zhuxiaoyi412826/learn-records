# JDBC

# 基础篇

## 一、引言

#### 1.1 数据的存储

> 我们在开发Java程序时，数据都是存储在内存中，属于临时存储，当程序停止或重启时，内存中的数据就丢失了！我们为了解决数据的长期存储问题，有如下解决方案：
>
> 1. 数据通过I/O流技术，存储在本地磁盘中，解决了持久化问题，但是没有结构和逻辑，不方便管理和维护。
> 2. 通过关系型数据库，将数据按照特定的格式交由数据库管理系统维护。关系型数据库是通过库和表分隔不同的数据，表中数据存储的方式是行和列，区分相同格式不同值的数据。

|                        数据库存储数据                        |
| :----------------------------------------------------------: |
| ![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/数据库存储.png) |



#### 1.2 数据的操作

> 数据存储在数据库，仅仅解决了我们数据存储的问题，但当我们程序运行时，需要读取数据，以及对数据做增删改的操作，那么我们如何通过Java程序对数据库中的数据做增删改查呢？

|                      Java程序读取数据库                      |
| :----------------------------------------------------------: |
| ![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/java操作数据库.png) |



## 二、JDBC

### 2.1 JDBC的概念

> - JDBC：Java Database Connectivity，意为Java数据库连接。
> - JDBC是Java提供的一组独立于任何数据库管理系统的API。
> - Java提供接口规范，由各个数据库厂商提供接口的实现，厂商提供的实现类封装成jar文件，也就是我们俗称的数据库驱动jar包。
> - 学习JDBC，充分体现了面向接口编程的好处，程序员只关心标准和规范，而无需关注实现过程。

|                       JDBC简单执行过程                       |
| :----------------------------------------------------------: |
| ![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/jdbc执行过程.png) |



### 2.2 JDBC的核心组成

- 接口规范：
  - 为了项目代码的可移植性，可维护性，SUN公司从最初就制定了Java程序连接各种数据库的统一接口规范。这样的话，不管是连接哪一种DBMS软件，Java代码可以保持一致性。
  - 接口存储在java.sql和javax.sql包下。
- 实现规范：
  - 因为各个数据库厂商的DBMS软件各有不同，那么各自的内部如何通过SQL实现增、删、改、查等操作管理数据，只有这个数据库厂商自己更清楚，因此把接口规范的实现交给各个数据库厂商自己实现。
  - 厂商将实现内容和过程封装成jar文件，我们程序员只需要将jar文件引入到项目中集成即可，就可以开发调用实现过程操作数据库了。

## 三、JDBC快速入门

### 3.1 JDBC搭建步骤

1. 准备数据库。
2. 官网下载数据库连接驱动jar包。[https://downloads.mysql.com/archives/c-j/]()
3. 创建Java项目，在项目下创建lib文件夹，将下载的驱动jar包复制到文件夹里。
4. 选中lib文件夹右键->Add as Library，与项目集成。
5. 编写代码

查看MySQL版本

```
mysql --version MySQL -v  Linux下 rpm -qa | grep mysql
```

-- 方法1（最常用） SELECT VERSION();

 -- 方法2 SHOW VARIABLES LIKE 'version'; 

-- 方法3 STATUS;

### 3.2 代码实现

###  3.3数据库

```sql
CREATE DATABASE atguigu;

use atguigu;

create table t_emp
(
    emp_id     int auto_increment comment '员工编号' primary key,
    emp_name   varchar(100)  not null comment '员工姓名',
    emp_salary double(10, 5) not null comment '员工薪资',
    emp_age    int           not null comment '员工年龄'
);

insert into t_emp (emp_name,emp_salary,emp_age)
values  ('andy', 777.77, 32),
        ('大风哥', 666.66, 41),
        ('康师傅',111, 23),
        ('Gavin',123, 26),
        ('小鱼儿', 123, 28);
```

###  **3.4jdbc标准流程**

```
1. 注册驱动 → 2. 获取连接 → 3. 创建 Statement → 4. 执行SQL → 5. 处理结果 → 6. 释放资源
```

```
┌─────────────────────────────────┐
│         Java 应用程序          │
│┌────────────────────────────────┐│
││ 1. Class.forName() 加载驱动    ││
│└────────────────────────────────┘│
└────────────────┬────────────────┘
                 │
┌────────────────▼────────────────┐
│      2. 建立 TCP 连接            │
│         (三次握手)               │
└────────────────┬────────────────┘
                 │
┌────────────────▼────────────────┐
│      3. 发送用户名密码验证       │
└────────────────┬────────────────┘
                 │
┌────────────────▼────────────────┐
│      4. 发送 SQL 语句            │
└────────────────┬────────────────┘
                 │
┌────────────────▼────────────────┐
│      5. 接收查询结果            │
└────────────────┬────────────────┘
                 │
┌────────────────▼────────────────┐
│      6. 关闭连接                │
└─────────────────────────────────┘
```

**步骤1:注册驱动**

```
Class.forName("com.mysql.cj.jdbc.Driver");
```

工作原理：

1. 加载驱动类 ： Class.forName() 方法会通过类加载器加载 com.mysql.cj.jdbc.Driver 类到 JVM 内存中
2. 自动注册 ：在 Driver 类的静态代码块中，会**自动调用 DriverManager.registerDriver() 注册驱动**
3. MySQL 8.x 变化 ：从 MySQL 8.x 开始，驱动类名从 com.mysql.jdbc.Driver 改为 com.mysql.cj.jdbc.Driver
4. 自动加载机制 ：JDBC 4.0 之后可以省略这一步，但显式注册更安全
Driver 类的静态代码块（伪代码）：

```
static {
    java.sql.DriverManager.registerDriver
    (new com.mysql.cj.jdbc.Driver());
}
```

**步骤2:获取数据库连接**

1. DriverManager 寻找合适的驱动 ：
   
   - DriverManager 会遍历已注册的所有驱动
   - 根据 URL 匹配能处理 MySQL 的驱动
   - 选中 MySQL 驱动并调用其 connect() 方法
2. URL 详解 ：
   
   ```
   jdbc:mysql://localhost:3306/jdbc
   │     │     │        │    └── 数据库名
   │     │     │        └── 端口号
   │     │     └── 主机地址
   │     └── 子协议（数据库类型）
   └── JDBC 协议前缀
   ```
3. 建立 TCP 连接 ：
   
   - 创建 Socket 连接到 MySQL 服务器（localhost:3306）
   - 进行 TCP 三次握手
   - 发送用户名和密码进行身份验证
   - MySQL 返回连接成功/失败响应
4. Connection 对象的作用 ：
   
   - 代表 Java 程序和 MySQL 数据库之间的一次会话
   - 负责管理这个会话的事务（提交/回滚）
   - 提供创建 Statement 的方法
   - 提供关闭连接的方法

**步骤 3：创建 PreparedStatement 对象**

```
PreparedStatement preparedStatement = 
connection.prepareStatement(
    "select emp_id,emp_name,emp_salary,
    emp_age from t_emp"
);
```

**工作原理：**

1. **Statement vs PreparedStatement**：
   - Statement：简单 SQL 执行对象，容易产生 SQL 注入
   - PreparedStatement：预编译 SQL 语句，更安全，性能更好
2. **预编译过程**：
   - 将 SQL 语句发送给 MySQL 服务器
   - MySQL 对 SQL 进行语法分析和编译优化
   - 生成执行计划，返回占位符信息
   - PreparedStatement 对象保存执行计划和占位符
3. **好处**：
   - **防 SQL 注入**：参数化查询，不会把用户输入当 SQL 执行
   - **更好性能**：SQL 只编译一次，多次执行复用执行计划
   - **更好可读性**：SQL 和参数分离，代码更清晰

------

**步骤 4：执行 SQL 语句并获取结果**

```
ResultSet resultSet = preparedStatement.
executeQuery();
```

**工作原理：**

1. **发送 SQL 到服务器**：
   - 通过已建立的 TCP 连接发送 SQL
   - MySQL 接收并执行 SQL
   - 查询数据库表中的数据
   - 构建结果集（ResultSet）
2. **executeQuery vs executeUpdate**：
   - executeQuery()：执行查询，返回 ResultSet
   - executeUpdate()：执行增删改，返回影响行数
3. **ResultSet 的作用**：
   - 表示数据库查询返回的结果集
   - 类似一个指针（游标），指向结果集的当前行
   - 提供 next() 方法移动指针
   - 提供各种 getXxx() 方法获取列数据

------

**步骤 5：处理结果**

```
while (resultSet.next()) {
    int empId = resultSet.getInt("emp_id");
    String empName = resultSet.getString
    ("emp_name");
    String empSalary = resultSet.getString
    ("emp_salary");
    int empAge = resultSet.getInt
    ("emp_age");
    System.out.println(empId + "\t" + 
    empName + "\t" + empSalary + "\t" + 
    empAge);
}
```

**工作原理：**

1. **resultSet.next() 的作用**：
   - 初始时，指针指向结果集的第一行之前
   - 每次调用 next()，指针向下移动一行
   - 如果存在下一行，返回 true，否则返回 false
2. **获取列数据的方式**：
   - 按列名获取：getInt("emp_id") - 更直观，推荐
   - 按索引获取：getInt(1) - 性能稍好，但顺序容易错
3. **数据类型转换**：
   - MySQL 的 INT → Java 的 int/Integer
   - MySQL 的 VARCHAR/CHAR → Java 的 String
   - MySQL 的 DATETIME → Java 的 LocalDateTime/Date
   - MySQL 的 DECIMAL → Java 的 BigDecimal

------

**步骤 6：释放资源（先开后关原则）**

```
resultSet.close();
preparedStatement.close();
connection.close();
```

### 3.5完整代码实现

```java
package com.atguigu;

import java.sql.*;

public class JdbcQuick {
    public static void main(String[] args) throws ClassNotFoundException， SQLException {
        //1.注册驱动
        Class.forName("com.mysql.cj.jdbc.Driver");
      
        //2.获取数据库连接
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atguigu", "root", "atguigu");

        //3.创建Statement对象
        PreparedStatement preparedStatement = connection.prepareStatement("select emp_id,emp_name,emp_salary,emp_age from t_emp");

        //4.编写SQL语句并执行，获取结果
        ResultSet resultSet = preparedStatement.executeQuery();


        //5.处理结果
        while (resultSet.next()) {
            int empId = resultSet.getInt("emp_id");
            String empName = resultSet.getString("emp_name");
            String empSalary = resultSet.getString("emp_salary");
            int empAge = resultSet.getInt("emp_age");
            System.out.println(empId + "\t" + empName + "\t" + empSalary + "\t" + empAge);
        }

        //6.释放资源(先开后关原则)
        resultSet.close();
        preparedStatement.close();
        connection.close();

    }
}

```

###  3.6步骤总结

1. 注册驱动【依赖的驱动类，进行安装】
2. 获取连接【Connection建立连接】
3. 创建发送SQL语句对象【Connection创建发送SQL语句的Statement】
4. 发送SQL语句，并获取返回结果【Statement 发送sql语句到数据库并且取得返回结果】
5. 结果集解析【结果集解析，将查询结果解析出来】
6. 资源关闭【释放ResultSet、Statement 、Connection】

## 四、核心API理解

#### 4.1 注册驱动

- ```java
  Class.forName("com.mysql.cj.jdbc.Driver");
  ```

- 在 Java 中，当使用 JDBC（Java Database Connectivity）连接数据库时，需要加载数据库特定的驱动程序，以便与数据库进行通信。加载驱动程序的目的是为了注册驱动程序，使得 JDBC API 能够识别并与特定的数据库进行交互。

- 从JDK6开始，不再需要显式地调用 `Class.forName()` 来加载 JDBC 驱动程序，只要在类路径中集成了对应的jar文件，会自动在初始化时注册驱动程序。

#### 4.2 Connection

- Connection接口是JDBC API的重要接口，用于建立与数据库的通信通道。换而言之，Connection对象不为空，则代表一次数据库连接。
- 在建立连接时，需要指定数据库URL、用户名、密码参数。
  - URL：jdbc:mysql://localhost:3306/atguigu
    - jdbc:mysql://IP地址:端口号/数据库名称?参数键值对1&参数键值对2
- `Connection` 接口还负责管理事务，`Connection` 接口提供了 `commit` 和 `rollback` 方法，用于提交事务和回滚事务。
- 可以创建 `Statement` 对象，用于执行 SQL 语句并与数据库进行交互。
- 在使用JDBC技术时，必须要先获取Connection对象，在使用完毕后，要释放资源，避免资源占用浪费及泄漏。

#### 4.3 Statement

- `Statement` 接口用于执行 SQL 语句并与数据库进行交互。它是 JDBC API 中的一个重要接口。通过 `Statement` 对象，可以向数据库发送 SQL 语句并获取执行结果。
- 结果可以是一个或多个结果。
  - 增删改：受影响行数单个结果。
  - 查询：单行单列、多行多列、单行多列等结果。
- 但是`Statement` 接口在执行SQL语句时，会产生`SQL注入攻击问题`:
  - 当使用 `Statement` 执行动态构建的 SQL 查询时，往往需要将查询条件与 SQL 语句拼接在一起，直接将参数和SQL语句一并生成，让SQL的查询条件始终为true得到结果。



#### 4.4 PreparedStatement

- `PreparedStatement`是 `Statement` 接口的子接口，用于执行`预编译`的 SQL 查询，作用如下：
  - 预编译SQL语句：在创建PreparedStatement时，就会预编译SQL语句，也就是SQL语句已经固定。
  - 防止SQL注入：`PreparedStatement` 支持参数化查询，将数据作为参数传递到SQL语句中，采用?占位符的方式，将传入的参数用一对单引号包裹起来''，无论传递什么都作为值。有效防止传入关键字或值导致SQL注入问题。
  - 性能提升：PreparedStatement是预编译SQL语句，同一SQL语句多次执行的情况下，可以复用，不必每次重新编译和解析。
- 后续的学习我们都是基于PreparedStatement进行实现，更安全、效率更高！



#### 4.5 ResultSet

- `ResultSet`是 JDBC API 中的一个接口，用于表示从数据库中`执行查询语句所返回的结果集`。它提供了一种用于遍历和访问查询结果的方式。
- 遍历结果：ResultSet可以使用 `next()` 方法将游标移动到结果集的下一行，逐行遍历数据库查询的结果，返回值为boolean类型，true代表有下一行结果，false则代表没有。
- 获取单列结果：可以通过getXxx的方法获取单列的数据，该方法为重载方法，支持索引和列名进行获取。

## 五、基于PreparedStatement实现CRUD

在 Java 数据库编程（JDBC）中，**PreparedStatement** 是执行 SQL 操作的**核心接口**，专门用来实现**增 (Create)、查 (Retrieve)、改 (Update)、删 (Delete)** 即 CRUD 操作，**防注入、高效、代码简洁**，是企业开发唯一选择  **增删改用 executeUpdate ()，查询用 executeQuery ()**

### **PreparedStatement如何使用**

**1. 是什么**

**预编译 SQL 语句对象**，Java 操作数据库专用，继承 Statement。

**2. 核心作用**

1. **预编译 SQL**：SQL 结构先编译缓存，多次执行只编译一次，效率更高

2. **防 SQL 注入**：使用`?`占位符传参，自动转义特殊字符，彻底拦截注入攻击

3. **统一赋值**：用`setXxx()`方法给占位符设值，写法规范

4. **预编译**：重复执行效率高

   **代码优雅**：不用拼接复杂字符串

   **支持多种数据类型**：自动处理日期、Blob 等复杂类型

**3. 两大对比**

- **Statement**：拼接字符串 SQL，易注入、效率低
- **PreparedStatement**：占位符`?`传参，安全、高效、企业必用

**4. 固定语法模板**

```
// 1. 带?占位符SQL
String sql = "select * from emp where id = ?";
// 2. 创建预编译对象
PreparedStatement pstmt = conn.prepareStatement(sql);
// 3. 给占位符赋值（下标从1开始）
pstmt.setInt(1, 10);
// 4. 执行查询/更新
ResultSet rs = pstmt.executeQuery();
```

**5. 常用方法**

- `setInt(下标,值)` 设整数
- `setString(下标,值)` 设字符串
- `executeQuery()` 查（返回结果集）
- `executeUpdate()` 增删改（返回影响行数）

**6. 一句话总结**

**带问号占位符、预编译、防注入、Java 开发数据库首选对象**

7. **两个核心执行方法**

| 方法              | 用途           | 返回值            |
| ----------------- | -------------- | ----------------- |
| `executeUpdate()` | **增、删、改** | int：受影响的行数 |
| `executeQuery()`  | **查询**       | ResultSet：结果集 |

8. **ResultSet 结果集使用**

- `rs.next()`：向下移动一行，有数据返回 true
- 获取数据：`rs.getXXX("列名")` 或 `rs.getXXX(列索引)`
- 列索引**从 1 开始**

### 查询单行单列

```java
	@Test
    public void querySingleRowAndColumn() throws SQLException {
        //1.注册驱动
//        Class.forName("com.mysql.cj.jdbc.Driver");

        //2.获取数据库连接
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atguigu", "root","atguigu");

        //3.创建PreparedStatement对象，并预编译SQL语句
        PreparedStatement preparedStatement = connection.prepareStatement("select count(*) as count from t_emp");

        //4.执行SQL语句，获取结果
        ResultSet resultSet = preparedStatement.executeQuery();

        //5.处理结果
        while (resultSet.next()){
            int count = resultSet.getInt("count");
            System.out.println("count = " + count);
        }

        //6.释放资源(先开后关原则)
        resultSet.close();
        preparedStatement.close();
        connection.close();

    }
```

### 查询单行多列

```java
	@Test
    public void querySingleRow() throws SQLException {
        //1.注册驱动
//        Class.forName("com.mysql.cj.jdbc.Driver");

        //2.获取数据库连接
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atguigu","root","atguigu");

        //3.创建PreparedStatement对象，并预编译SQL语句，使用?占位符
        PreparedStatement preparedStatement = connection.prepareStatement("select emp_id,emp_name,emp_salary,emp_age from t_emp where emp_id = ?");

        //4.为占位符赋值，索引从1开始，执行SQL语句，获取结果
        preparedStatement.setInt(1,1);
        ResultSet resultSet = preparedStatement.executeQuery();

        //5.处理结果
        while (resultSet.next()){
            int empId = resultSet.getInt("emp_id");
            String empName = resultSet.getString("emp_name");
            String empSalary = resultSet.getString("emp_salary");
            int empAge = resultSet.getInt("emp_age");
            System.out.println(empId+"\t"+empName+"\t"+empSalary+"\t"+empAge);
        }

        //6.释放资源(先开后关原则)
        resultSet.close();
        preparedStatement.close();
        connection.close();

    }
```

###  查询多行多列

```java
	@Test
    public void queryMoreRow() throws SQLException {
        //1.注册驱动
//        Class.forName("com.mysql.cj.jdbc.Driver");

        //2.获取数据库连接
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atguigu","root","atguigu");

        //3.创建Statement对象
        PreparedStatement preparedStatement = connection.prepareStatement("select emp_id,emp_name,emp_salary,emp_age from t_emp");

        //4.编写SQL语句并执行，获取结果
        ResultSet resultSet = preparedStatement.executeQuery();


        //5.处理结果
        while (resultSet.next()){
            int empId = resultSet.getInt("emp_id");
            String empName = resultSet.getString("emp_name");
            String empSalary = resultSet.getString("emp_salary");
            int empAge = resultSet.getInt("emp_age");
            System.out.println(empId+"\t"+empName+"\t"+empSalary+"\t"+empAge);
        }

        //6.释放资源(先开后关原则)
        resultSet.close();
        preparedStatement.close();
        connection.close();

    }
```

###  新增

```java
	@Test
    public void insert() throws SQLException {
        //1.注册驱动
//        Class.forName("com.mysql.cj.jdbc.Driver");

        //2.获取数据库连接
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atguigu","root", "atguigu");

        //3.创建Statement对象
        PreparedStatement preparedStatement = connection.prepareStatement("insert into t_emp (emp_name,emp_salary,emp_age)values  (?, ?,?)");

        //4.为占位符赋值，索引从1开始，编写SQL语句并执行，获取结果
        preparedStatement.setString(1,"rose");
        preparedStatement.setDouble(2,666.66);
        preparedStatement.setDouble(3,28);
        int result = preparedStatement.executeUpdate();

        //5.处理结果
        if(result>0){
            System.out.println("添加成功");
        }else{
            System.out.println("添加失败");
        }

        //6.释放资源(先开后关原则)
        preparedStatement.close();
        connection.close();

    }
```

###  修改

```java
	@Test
    public void update() throws SQLException {
        //1.注册驱动
//        Class.forName("com.mysql.cj.jdbc.Driver");

        //2.获取数据库连接
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atguigu", "root", "atguigu");

        //3.创建Statement对象
        PreparedStatement preparedStatement = connection.prepareStatement("update t_emp set emp_salary = ? where emp_id = ?");

        //4.为占位符赋值，索引从1开始，编写SQL语句并执行，获取结果
        preparedStatement.setDouble(1,888.88);
        preparedStatement.setDouble(2,8);
        int result = preparedStatement.executeUpdate();

        //5.处理结果
        if(result>0){
            System.out.println("修改成功");
        }else{
            System.out.println("修改失败");
        }

        //6.释放资源(先开后关原则)
        preparedStatement.close();
        connection.close();

    }
```

###  删除

```java
	@Test
    public void delete() throws SQLException {
        //1.注册驱动
//        Class.forName("com.mysql.cj.jdbc.Driver");

        //2.获取数据库连接
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atguigu", "root", "atguigu");

        //3.创建Statement对象
        PreparedStatement preparedStatement = connection.prepareStatement("delete from t_emp where emp_id = ?");

        //4.为占位符赋值，索引从1开始，编写SQL语句并执行，获取结果
        preparedStatement.setInt(1,8);
        int result = preparedStatement.executeUpdate();

        //5.处理结果
        if(result>0){
            System.out.println("删除成功");
        }else{
            System.out.println("删除失败");
        }

        //6.释放资源(先开后关原则)
        preparedStatement.close();
        connection.close();

    }
```

## 六、常见问题

#### 6.1 资源的管理

> 在使用JDBC的相关资源时，比如Connection、PreparedStatement、ResultSet，使用完毕后，要及时关闭这些资源以释放数据库服务器资源和避免内存泄漏是很重要的。

#### 6.2 SQL语句问题

> java.sql.SQLSyntaxErrorException：SQL语句错误异常，一般有几种可能：
>
> 1. SQL语句有错误，检查SQL语句！建议SQL语句在SQL工具中测试后再复制到Java程序中！
> 2. 连接数据库的URL中，数据库名称编写错误，也会报该异常！
>
> ![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/SQL语句.png)

#### 6.3 SQL语句未设置参数问题

> java.sql.SQLException：No value specified for parameter 1
>
> 在使用预编译SQL语句时，如果有?占位符，要为每一个占位符赋值，否则报该错误！
>
> ![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/SQL语句参数设置.png)



#### 6.4 用户名或密码错误问题

> 连接数据库时，如果用户名或密码输入错误，也会报SQLException，容易混淆！所以一定要看清楚异常后面的原因描述
>
> ![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/用户密码错误.png)



#### 6.5 通信异常

> 在连接数据库的URL中，如果IP或端口写错了，会报如下异常：
>
> com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
>
> ![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/通信异常.png)

# 进阶篇

## 七、JDBC扩展

| 核心作用     | 详细释义                                                     | 实际 JDBC 使用体现                                           |
| ------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 数据封装载体 | 把数据库查询出的零散字段数据，整合封装成完整 Java 对象，避免分散变量存储 | ResultSet 遍历取值后，赋值给实体属性，一行数据对应一个实体实例 |
| 表结构映射   | Java 实体类对应数据库数据表，成员属性一一匹配表字段，建立映射关系 | 建表参照实体属性，操作数据直观对应库表结构                   |
| 统一数据传输 | 作为标准数据载体，在 JDBC 层、业务层之间传递数据，替代零散参数传值 | 增删改查直接传递实体对象，不用逐个单独传字段值               |
| 规范代码结构 | 固定数据格式，统一读写规则，减少杂乱代码，提升可读性与复用性 | 新增 / 修改直接读取实体属性赋值占位符，查询统一封装对象返回  |
| 降低程序耦合 | 隔离数据库底层操作与上层业务逻辑，数据库小幅变动仅调整实体即可 | 业务代码无需关心 JDBC 取值细节，只操作实体属性               |
| 方便集合存储 | 多条查询结果可统一存入 List 集合，批量管理、遍历处理数据     | 查询全部数据，批量封装实体对象存入集合返回调用方             |

| 数据库层面                | Java 实体类层面           | 对应规则                           |
| ------------------------- | ------------------------- | ---------------------------------- |
| **一张表（Table）**       | **一个实体类（Class）**   | 一张表 = 一个类                    |
| **一行数据（Row）**       | **一个对象（Object）**    | 一行记录 = 一个实例                |
| **一列 / 字段（Column）** | **一个成员变量（Field）** | 字段名 = 属性名                    |
| **主键（Primary Key）**   | **标记为主键的属性**      | 用 `id` 字段唯一标识               |
| **字段类型（Type）**      | **变量类型（Type）**      | int ↔ Integer，varchar ↔ String 等 |

#### 7.1 实体类和ORM

> - 在使用JDBC操作数据库时，我们会发现数据都是零散的，明明在数据库中是一行完整的数据，到了Java中变成了一个一个的变量，不利于维护和管理。而我们Java是面向对象的，一个表对应的是一个类，一行数据就对应的是Java中的一个对象，一个列对应的是对象的属性，所以我们要把数据存储在一个载体里，这个载体就是实体类！
> - **ORM（Object Relational Mapping）思想**，**对象到关系数据库的映射**，作用是在编程中，把面向对象的概念跟数据库中表的概念对应起来，以面向对象的角度操作数据库中的数据，即**一张表对应一个类，一行数据对应一个对象，一个列对应一个属性**！
> - 当下JDBC中这种过程我们称其为手动ORM。后续我们也会学习ORM框架，比如MyBatis、JPA等。
> - **实体类** = 专门用来封装数据库一行数据的 Java 类

```java
package com.atguigu.pojo;
//类名和数据库名对应，但是表名一般缩写，类名要全写！
public class Employee {
    private Integer empId;//emp_id = empId 数据库中列名用下划线分隔，属性名用驼峰！
    private String empName;//emp_name = empName
    private Double empSalary;//emp_salary = empSalary
    private Integer empAge;//emp_age = empAge

    //省略get、set、无参、有参、toString方法。
}
```

封装代码：

```java
	@Test
    public void querySingleRow() throws SQLException {
        //1.注册驱动
//        Class.forName("com.mysql.cj.jdbc.Driver");

        //2.获取数据库连接
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atguigu", "root","atguigu");

        //3.创建PreparedStatement对象，并预编译SQL语句，使用?占位符
        PreparedStatement preparedStatement = connection.prepareStatement("select emp_id,emp_name,emp_salary,emp_age from t_emp where emp_id = ?");

        //4.为占位符赋值，索引从1开始，执行SQL语句，获取结果
        preparedStatement.setInt(1, 1);
        ResultSet resultSet = preparedStatement.executeQuery();
	      //预先创建实体类变量
        Employee employee = null;
        //5.处理结果
        while (resultSet.next()) {
            int empId = resultSet.getInt("emp_id");
            String empName = resultSet.getString("emp_name");
            Double empSalary = Double.valueOf(resultSet.getString("emp_salary"));
            int empAge = resultSet.getInt("emp_age");
          	//当结果集中有数据，再进行对象的创建
            employee = new Employee(empId,empName,empSalary,empAge);
        }

        System.out.println("employee = " + employee);

        //6.释放资源(先开后关原则)
        resultSet.close();
        preparedStatement.close();
        connection.close();

    }
```

1. 数据封装 将分散的数据包装成对象 new Employee(empId, empName, ...) 
2. 提供访问接口 通过getter方法访问私有属性 employee.getEmpName() 
3.  统一格式化输出 重写toString()方法 System.out.println(employee)

**💡 一句话总结**

Employee 类是 ORM（对象关系映射） 的体现：

- 数据库的 t_emp 表 → Java中的 Employee 对象
- 表的一行记录 → Java的一个 Employee 实例
- 表的列 → Java的属性
没有Employee类，数据就无法被对象化，代码就失去了面向对象的思想

使用实体类输出结果

```
employee = Employee{empId=1, empName='andy', empSalary=777.77, empAge=32}
```

没有实体类

```
// 没有Employee类的话，数据是这样的：
int empId = 1;
String empName = "andy";
Double empSalary = 777.77;
int empAge = 32;

// 打印结果可能是：
System.out.println("empId=" + empId + ", empName=" + empName + ...);
// 输出：empId=1, empName=andy, empSalary=777.77, empAge=32
```

#### 7.2 主键回显

- 在数据中，执行新增操作时，主键列为自动增长，可以在表中直观的看到，但是在Java程序中，我们执行完新增后，只能得到受影响行数，无法得知当前新增数据的主键值。在Java程序中获取数据库中插入新数据后的主键值，并赋值给Java对象，此操作为主键回显。

- 代码实现：

  - ```java
    @Test
      public void testReturnPK() throws SQLException {
          //1.注册驱动
    //        Class.forName("com.mysql.cj.jdbc.Driver");
    
            //2.获取数据库连接
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atguigu", "root", "atguigu");
    
            //3.创建preparedStatement对象，传入需要主键回显参数Statement.RETURN_GENERATED_KEYS
            PreparedStatement preparedStatement = connection.prepareStatement("insert into t_emp (emp_name, emp_salary, emp_age)values  (?, ?,?)",Statement.RETURN_GENERATED_KEYS);
    
            //4.编写SQL语句并执行，获取结果
            Employee employee = new Employee(null,"rose",666.66,28);
            preparedStatement.setString(1,employee.getEmpName());
            preparedStatement.setDouble(2,employee.getEmpSalary());
            preparedStatement.setDouble(3,employee.getEmpAge());
            int result = preparedStatement.executeUpdate();
    
            //5.处理结果
            if(result>0){
                System.out.println("添加成功");
            }else{
                System.out.println("添加失败");
            }
    
            //6.获取生成的主键列值，返回的是resultSet，在结果集中获取主键列值
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()){
                int empId = resultSet.getInt(1);
                employee.setEmpId(empId);
            }
    		
           	System.out.println(employee.toString());
           
            //7.释放资源(先开后关原则)
            resultSet.close();
            preparedStatement.close();
            connection.close();
    
        }
    ```



#### 7.3 批量操作

- 插入多条数据时，一条一条发送给数据库执行，效率低下！

- 通过批量操作，可以提升多次操作效率！

- 代码实现：

  - ```java
    @Test
       public void testBatch() throws Exception {
           //1.注册驱动
    //        Class.forName("com.mysql.cj.jdbc.Driver");
    
            //2.获取连接
            Connection connection = DriverManager.getConnection("jdbc:mysql:///atguigu?rewriteBatchedStatements=true", "root", "atguigu");
    
            //3.编写SQL语句
            /*
                注意：1、必须在连接数据库的URL后面追加?rewriteBatchedStatements=true，允许批量操作
                    2、新增SQL必须用values。且语句最后不要追加;结束
                    3、调用addBatch()方法，将SQL语句进行批量添加操作
                    4、统一执行批量操作，调用executeBatch()
             */
            String sql = "insert into t_emp (emp_name,emp_salary,emp_age) values (?,?,?)";
    
            //4.创建预编译的PreparedStatement，传入SQL语句
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
    
            //获取当前行代码执行的时间。毫秒值
            long start = System.currentTimeMillis();
            for(int i = 0;i<10000;i++){
                //5.为占位符赋值
                preparedStatement.setString(1, "marry"+i);
                preparedStatement.setDouble(2, 100.0+i);
                preparedStatement.setInt(3, 20+i);
    
                preparedStatement.addBatch();
            }
    
            //执行批量操作
            preparedStatement.executeBatch();
    
            long end = System.currentTimeMillis();
    
            System.out.println("消耗时间："+(end - start));
    
            preparedStatement.close();
            connection.close();
        }
    ```



## 八、连接池

#### 8.1 现有问题

> - 每次操作数据库都要获取新连接，使用完毕后就close释放，频繁的创建和销毁造成资源浪费。
> - 连接的数量无法把控，对服务器来说压力巨大。



#### 8.2 连接池

> 连接池就是数据库连接对象的缓冲区，通过配置，由连接池负责创建连接、管理连接、释放连接等操作。
>
> 预先创建数据库连接放入连接池，用户在请求时，通过池直接获取连接，使用完毕后，将连接放回池中，避免了频繁的创建和销毁，同时解决了创建的效率。
>
> 当池中无连接可用，且未达到上限时，连接池会新建连接。
>
> 池中连接达到上限，用户请求会等待，可以设置超时时间。



#### 8.3 常见连接池

JDBC 的数据库连接池使用 javax.sql.DataSource接口进行规范，所有的第三方连接池都实现此接口，自行添加具体实现！也就是说，所有连接池获取连接的和回收连接方法都一样，不同的只有性能和扩展功能!

- DBCP 是Apache提供的数据库连接池，速度相对C3P0较快，但自身存在一些BUG。
- C3P0 是一个开源组织提供的一个数据库连接池，速度相对较慢，稳定性还可以。
- Proxool 是sourceforge下的一个开源项目数据库连接池，有监控连接池状态的功能， 稳定性较c3p0差一点
- **Druid 是阿里提供的数据库连接池，是集DBCP 、C3P0 、Proxool 优点于一身的数据库连接池，性能、扩展性、易用性都更好，功能丰富**。
- **Hikari（ひかり[shi ga li]） 取自日语，是光的意思，是SpringBoot2.x之后内置的一款连接池，基于 BoneCP （已经放弃维护，推荐该连接池）做了不少的改进和优化，口号是快速、简单、可靠。**

|                     主流连接池的功能对比                     |
| :----------------------------------------------------------: |
| ![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/主流连接池功能对比.png) |

#### 8.4 Druid连接池使用

- 使用步骤：

  - 引入jar包。
  - 编码。

- 代码实现：

  - 硬编码方式（了解）：

    - ```java
      @Test
          public void testHardCodeDruid() throws SQLException {
              /*
                  硬编码：将连接池的配置信息和Java代码耦合在一起。
                  1、创建DruidDataSource连接池对象。
                  2、设置连接池的配置信息【必须 | 非必须】
                  3、通过连接池获取连接对象
                  4、回收连接【不是释放连接，而是将连接归还给连接池，给其他线程进行复用】
               */
      
              //1.创建DruidDataSource连接池对象。
              DruidDataSource druidDataSource = new DruidDataSource();
      
              //2.设置连接池的配置信息【必须 | 非必须】
              //2.1 必须设置的配置
              druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
              druidDataSource.setUrl("jdbc:mysql:///atguigu");
              druidDataSource.setUsername("root");
              druidDataSource.setPassword("atguigu");
      
              //2.2 非必须设置的配置
              druidDataSource.setInitialSize(10);
              druidDataSource.setMaxActive(20);
              
              //3.通过连接池获取连接对象
              Connection connection = druidDataSource.getConnection();
              System.out.println(connection);
      
              //基于connection进行CRUD
      
              //4.回收连接
              connection.close();
          }
      ```

  - 软编码方式（推荐）：

    - 在项目目录下创建resources文件夹，标识该文件夹为资源目录，创建db.properties配置文件，将连接信息定义在该文件中。

      - ```properties
        # druid连接池需要的配置参数，key固定命名
        driverClassName=com.mysql.cj.jdbc.Driver
        url=jdbc:mysql:///atguigu
        username=root
        password=atguigu
        initialSize=10
        maxActive=20
        ```

    - Java代码：

      - ```java
        @Test
            public void testResourcesDruid() throws Exception {
                //1.创建Properties集合，用于存储外部配置文件的key和value值。
                Properties properties = new Properties();
        
                //2.读取外部配置文件，获取输入流，加载到Properties集合里。
                InputStream inputStream = DruidTest.class.getClassLoader().getResourceAsStream("db.properties");
                properties.load(inputStream);
        
                //3.基于Properties集合构建DruidDataSource连接池
                DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
        
                //4.通过连接池获取连接对象
                Connection connection = dataSource.getConnection();
                System.out.println(connection);
        
                //5.开发CRUD
        
                //6.回收连接
                connection.close();
            }
        ```



#### 8.5 Druid其他配置【了解】

| 配置                          | **缺省** | **说明**                                                     |
| ----------------------------- | -------- | ------------------------------------------------------------ |
| name                          |          | 配置这个属性的意义在于，如果存在多个数据源，监控的时候可以通过名字来区分开来。 如果没有配置，将会生成一个名字，格式是：”DataSource-” + System.identityHashCode(this) |
| jdbcUrl                       |          | 连接数据库的url，不同数据库不一样。例如：mysql : jdbc:mysql://10.20.153.104:3306/druid2 oracle : jdbc:oracle:thin:@10.20.149.85:1521:ocnauto |
| username                      |          | 连接数据库的用户名                                           |
| password                      |          | 连接数据库的密码。如果你不希望密码直接写在配置文件中，可以使用ConfigFilter。详细看这里：<https://github.com/alibaba/druid/wiki/%E4%BD%BF%E7%94%A8ConfigFilter> |
| driverClassName               |          | 根据url自动识别 这一项可配可不配，如果不配置druid会根据url自动识别dbType，然后选择相应的driverClassName(建议配置下) |
| initialSize                   | 0        | 初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时 |
| maxActive                     | 8        | 最大连接池数量                                               |
| maxIdle                       | 8        | 已经不再使用，配置了也没效果                                 |
| minIdle                       |          | 最小连接池数量                                               |
| maxWait                       |          | 获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁。 |
| poolPreparedStatements        | false    | 是否缓存preparedStatement，也就是PSCache。PSCache对支持游标的数据库性能提升巨大，比如说oracle。在mysql下建议关闭。 |
| maxOpenPreparedStatements     | -1       | 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。在Druid中，不会存在Oracle下PSCache占用内存过多的问题，可以把这个数值配置大一些，比如说100 |
| validationQuery               |          | 用来检测连接是否有效的sql，要求是一个查询语句。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会其作用。 |
| testOnBorrow                  | true     | 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。 |
| testOnReturn                  | false    | 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能 |
| testWhileIdle                 | false    | 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。 |
| timeBetweenEvictionRunsMillis |          | 有两个含义： 1)Destroy线程会检测连接的间隔时间2)testWhileIdle的判断依据，详细看testWhileIdle属性的说明 |
| numTestsPerEvictionRun        |          | 不再使用，一个DruidDataSource只支持一个EvictionRun           |
| minEvictableIdleTimeMillis    |          |                                                              |
| connectionInitSqls            |          | 物理连接初始化的时候执行的sql                                |
| exceptionSorter               |          | 根据dbType自动识别 当数据库抛出一些不可恢复的异常时，抛弃连接 |
| filters                       |          | 属性类型是字符串，通过别名的方式配置扩展插件，常用的插件有： 监控统计用的filter:stat日志用的filter:log4j防御sql注入的filter:wall |
| proxyFilters                  |          | 类型是List，如果同时配置了filters和proxyFilters，是组合关系，并非替换关系 |



#### 8.6 HikariCP连接池使用

- 使用步骤：

  - 引入jar包

  - 硬编码方式：

    - ```java
      @Test
      public void testHardCodeHikari() throws SQLException {
          /*
           硬编码：将连接池的配置信息和Java代码耦合在一起。
           1、创建HikariDataSource连接池对象
           2、设置连接池的配置信息【必须 ｜ 非必须】
           3、通过连接池获取连接对象
           4、回收连接
           */
          //1.创建HikariDataSource连接池对象
          HikariDataSource hikariDataSource = new HikariDataSource();
      
          //2.设置连接池的配置信息【必须 ｜ 非必须】
          //2.1必须设置的配置
          hikariDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
          hikariDataSource.setJdbcUrl("jdbc:mysql:///atguigu");
          hikariDataSource.setUsername("root");
          hikariDataSource.setPassword("atguigu");
      
          //2.2 非必须设置的配置
          hikariDataSource.setMinimumIdle(10);
          hikariDataSource.setMaximumPoolSize(20);
      
          //3.通过连接池获取连接对象
          Connection connection = hikariDataSource.getConnection();
      
          System.out.println(connection);
      
          //回收连接
          connection.close();
      }
      ```

  - 软编码方式：

    - 在项目下创建resources/hikari.properties配置文件

      - ```properties
        driverClassName=com.mysql.cj.jdbc.Driver
        jdbcUrl=jdbc:mysql:///atguigu
        username=root
        password=atguigu
        minimumIdle=10
        maximumPoolSize=20
        ```

    - 编写代码：

      - ```java
        @Test
          	public void testResourcesHikari()throws Exception{
                //1.创建Properties集合，用于存储外部配置文件的key和value值。
               Properties properties = new Properties();
               
               //2.读取外部配置文件，获取输入流，加载到Properties集合里。
               InputStream inputStream = HikariTest.class.getClassLoader().getResourceAsStream("db.properties");
               properties.load(inputStream);
               
               // 3.创建Hikari连接池配置对象，将Properties集合传进去
               HikariConfig hikariConfig = new HikariConfig(properties);
               
               // 4. 基于Hikari配置对象，构建连接池
               HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
          
               // 5. 获取连接
               Connection connection = hikariDataSource.getConnection();
               System.out.println("connection = " + connection);
               
               //6.回收连接
               connection.close();
           }
        }
        ```

#### 8.7 HikariCP其他配置【了解】

| 属性                | 默认值       | 说明                                                         |
| ------------------- | ------------ | ------------------------------------------------------------ |
| isAutoCommit        | true         | 自动提交从池中返回的连接                                     |
| connectionTimeout   | 30000        | 等待来自池的连接的最大毫秒数                                 |
| maxLifetime         | 1800000      | 池中连接最长生命周期如果不等于0且小于30秒则会被重置回30分钟  |
| minimumIdle         | 10           | 池中维护的最小空闲连接数 minIdle<0或者minIdle>maxPoolSize，则被重置为maxPoolSize |
| maximumPoolSize     | 10           | 池中最大连接数，包括闲置和使用中的连接                       |
| metricRegistry      | null         | 连接池的用户定义名称，主要出现在日志记录和JMX管理控制台中以识别池和池配置 |
| healthCheckRegistry | null         | 报告当前健康信息                                             |
| poolName            | HikariPool-1 | 连接池的用户定义名称，主要出现在日志记录和JMX管理控制台中以识别池和池配置 |
| idleTimeout         |              | 是允许连接在连接池中空闲的最长时间                           |



# 高级篇

## 九、JDBC优化及工具类封装

#### 9.1 现有问题

> 我们在使用JDBC的过程中，发现部分代码存在冗余的问题：
>
> - 创建连接池。
> - 获取连接。
> - 连接的回收。



#### 9.2 JDBC工具类封装V1.0

- resources/db.properties配置文件：

  - ```properties
    # druid连接池需要的配置参数，key固定命名
    driverClassName=com.mysql.cj.jdbc.Driver
    username=root
    password=atguigu
    url=jdbc:mysql:///atguigu
    ```

- 工具类代码：

  - ```java
    import com.alibaba.druid.pool.DruidDataSourceFactory;
    
    import javax.sql.DataSource;
    import java.sql.Connection;
    import java.sql.SQLException;
    import java.util.Properties;
    /**
    *	JDBC工具类（V1.0）：
    *		1、维护一个连接池对象。
    *		2、对外提供在连接池中获取连接的方法
    *		3、对外提供回收连接的方法
    *	注意：工具类仅对外提供共性的功能代码，所以方法均为静态方法！
    */
    public class JDBCTools {
        //创建连接池引用，因为要提供给当前项目全局使用，所以创建为静态的。
        private static DataSource dataSource;
        //在项目启动时，即创建连接池对象，赋值给dataSource
        static{
            try {
                Properties properties = new Properties();
                InputStream inputStream =  JDBCTools.getClass().getClassLoader().getSystemResourceAsStream("db.properties");
                properties.load(inputStream);
                dataSource = DruidDataSourceFactory.createDataSource(properties);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    	//对外提供获取连接的静态方法！
        public static Connection getConnection() throws SQLException {
            return ds.getConnection();
        }
    	//对外提供回收连接的静态方法
        public static void release(Connection conn) throws SQLException {
            conn.close();//还给连接池
        }
    }
    ```

- 注意：此种封装方式，无法保证单个请求连接的线程，多次操作数据库时，连接是同一个，无法保证事务！



#### 9.3 ThreadLocal

> JDK 1.2的版本中就提供java.lang.ThreadLocal，为解决多线程程序的并发问题提供了一种新的思路。使用这个工具类可以很简洁地编写出优美的多线程程序。通常用来在在多线程中管理共享数据库连接、Session等。
>
> ThreadLocal用于保存某个线程共享变量，原因是在Java中，每一个线程对象中都有一个ThreadLocalMap<ThreadLocal， Object>，其key就是一个ThreadLocal，而Object即为该线程的共享变量。
>
> 而这个map是通过ThreadLocal的set和get方法操作的。对于同一个static ThreadLocal，不同线程只能从中get，set，remove自己的变量，而不会影响其他线程的变量。
>
> - 在进行对象跨层传递的时候，使用ThreadLocal可以避免多次传递，打破层次间的约束。
> - 线程间数据隔离。
> - 进行事务操作，用于存储线程事务信息。
> - 数据库连接，`Session`会话管理。
>
> 1、ThreadLocal对象.get: 获取ThreadLocal中当前线程共享变量的值。
>
> 2、ThreadLocal对象.set: 设置ThreadLocal中当前线程共享变量的值。
>
> 3、ThreadLocal对象.remove: 移除ThreadLocal中当前线程共享变量的值。

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/threadLocal对象.png)

#### 9.4 JDBC工具类封装V2.0

> 在V1.0的版本基础上，我们将连接对象放在每个线程的ThreadLocal中，保证从头到尾当前线程操作的是同一连接对象。

代码实现：

```java
package com.atguigu.senior.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
/**
 *  JDBC工具类（V2.0）：
 *      1、维护一个连接池对象、维护了一个线程绑定变量的ThreadLocal对象
 *      2、对外提供在ThreadLocal中获取连接的方法
 *      3、对外提供回收连接的方法，回收过程中，将要回收的连接从ThreadLocal中移除！
 *  注意：工具类仅对外提供共性的功能代码，所以方法均为静态方法！
 *  注意：使用ThreadLocal就是为了一个线程在多次数据库操作过程中，使用的是同一个连接！
 */
public class JDBCUtilV2 {
    //创建连接池引用，因为要提供给当前项目的全局使用，所以创建为静态的。
    private static DataSource dataSource;
    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    //在项目启动时，即创建连接池对象，赋值给dataSource
    static {
        try {
            Properties properties = new Properties();
            InputStream inputStream = JDBCUtil.class.getClassLoader().getResourceAsStream("db.properties");
            properties.load(inputStream);

            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //对外提供在连接池中获取连接的方法
    public static Connection getConnection(){
        try {
            //在ThreadLocal中获取Connection、
            Connection connection = threadLocal.get();
            //threadLocal里没有存储Connection，也就是第一次获取
            if (connection == null) {
                //在连接池中获取一个连接，存储在threadLocal里。
                connection = dataSource.getConnection();
                threadLocal.set(connection);
            }
            return connection;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //对外提供回收连接的方法
    public static void release(){
        try {
            Connection connection = threadLocal.get();
            if(connection!=null){
                //从threadLocal中移除当前已经存储的Connection对象
                threadLocal.remove();
                //如果开启了事务的手动提交，操作完毕后，归还给连接池之前，要将事务的自动提交改为true
                connection.setAutoCommit(true);
                //将Connection对象归还给连接池
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

```





## 十、DAO封装及BaseDAO工具类

#### 10.1 DAO概念

> DAO：Data Access Object，数据访问对象。
>
> Java是面向对象语言，数据在Java中通常以对象的形式存在。一张表对应一个实体类，一张表的操作对应一个DAO对象！
>
> **在Java操作数据库时，我们会将对同一张表的增删改查操作统一维护起来，维护的这个类就是DAO层。**
>
> **DAO层只关注对数据库的操作，供业务层Service调用，将职责划分清楚！**



#### 10.1 BaseDAO概念

> 基本上每一个数据表都应该有一个对应的DAO接口及其实现类，发现对所有表的操作（增、删、改、查）代码重复度很高，所以可以抽取公共代码，给这些DAO的实现类可以抽取一个公共的父类，复用增删改查的基本操作，我们称为BaseDAO。



#### 10.2 BaseDAO搭建

```java
public abstract class BaseDAO {
    /*
    通用的增、删、改的方法
    String sql：sql
    Object... args：给sql中的?设置的值列表，可以是0~n
     */
    protected int update(String sql,Object... args) throws SQLException {
//        创建PreparedStatement对象，对sql预编译
        Connection connection = JDBCTools.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql);
        //设置?占位符的值
        if(args != null && args.length>0){
            for(int i=0; i<args.length; i++) {
                ps.setObject(i+1,args[i]);//?的编号从1开始，不是从0开始，数组的下标是从0开始
            }
        }

        //执行sql
        int len = ps.executeUpdate();
        ps.close();
        //这里检查下是否开启事务，开启不关闭连接，业务方法关闭!
        //connection.getAutoCommit()为false，不要在这里回收connection，由开启事务的地方回收
        //connection.getAutoCommit()为true，正常回收连接
        //没有开启事务的话，直接回收关闭即可!
        if (connection.getAutoCommit()) {
            //回收
            JDBCTools.release();
        }
        return len;
    }

    /*
    通用的查询多个Javabean对象的方法，例如：多个员工对象，多个部门对象等
    这里的clazz接收的是T类型的Class对象，
    如果查询员工信息，clazz代表Employee.class，
    如果查询部门信息，clazz代表Department.class，
    返回List<T> list
     */
    protected <T> ArrayList<T> query(Class<T> clazz,String sql,Object... args) throws Exception {
        //        创建PreparedStatement对象，对sql预编译
        Connection connection = JDBCTools.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql);
        //设置?的值
        if(args != null && args.length>0){
            for(int i=0; i<args.length; i++) {
                ps.setObject(i+1, args[i]);//?的编号从1开始，不是从0开始，数组的下标是从0开始
            }
        }

        ArrayList<T> list = new ArrayList<>();
        ResultSet res = ps.executeQuery();

        /*
        获取结果集的元数据对象。
        元数据对象中有该结果集一共有几列、列名称是什么等信息
         */
        ResultSetMetaData metaData = res.getMetaData();
        int columnCount = metaData.getColumnCount();//获取结果集列数

        //遍历结果集ResultSet，把查询结果中的一条一条记录，变成一个一个T 对象，放到list中。
        while(res.next()){
            //循环一次代表有一行，代表有一个T对象
            T t = clazz.newInstance();//要求这个类型必须有公共的无参构造

            //把这条记录的每一个单元格的值取出来，设置到t对象对应的属性中。
            for(int i=1; i<=columnCount; i++){
                //for循环一次，代表取某一行的1个单元格的值
                Object value = res.getObject(i);

                //这个值应该是t对象的某个属性值
                //获取该属性对应的Field对象
                //String columnName = metaData.getColumnName(i);//获取第i列的字段名
                //这里再取别名可能没办法对应上
                String columnName = metaData.getColumnLabel(i);//获取第i列的字段名或字段的别名
                Field field = clazz.getDeclaredField(columnName);
                field.setAccessible(true);//这么做可以操作private的属性

                field.set(t,value);
            }

            list.add(t);
        }

        res.close();
        ps.close();
        //这里检查下是否开启事务，开启不关闭连接，业务方法关闭!
        //没有开启事务的话，直接回收关闭即可!
        if (connection.getAutoCommit()) {
            //回收
            JDBCTools.release();
        }
        return list;
    }

    protected <T> T queryBean(Class<T> clazz,String sql, Object... args) throws Exception {
        ArrayList<T> list = query(clazz, sql,args);
        if(list == null || list.size() == 0){
            return null;
        }
        return list.get(0);
    }
}
```



#### 10.3 BaseDAO的应用

##### 10.3.1 创建员工DAO接口

```java
package com.atguigu.senior.dao;

import com.atguigu.senior.pojo.Employee;

import java.util.List;

/**
 * EmployeeDao这个类对应的是t_emp这张表的增删改查的操作
 */
public interface EmployeeDao {
    /**
     * 数据库对应的查询所有的操作
     * @return 表中所有的数据
     */
    List<Employee> selectAll();

    /**
     * 数据库对应的根据empId查询单个员工数据操作
     * @param empId 主键列
     * @return 一个员工对象（一行数据）
     */
    Employee selectByEmpId(Integer empId);

    /**
     * 数据库对应的新增一条员工数据
     * @param employee ORM思想中的一个员工对象
     * @return 受影响的行数
     */
    int insert(Employee employee);

    /**
     * 数据库对应的修改一条员工数据
     * @param employee ORM思想中的一个员工对象
     * @return 受影响的行数
     */
    int update(Employee employee);

    /**
     * 数据库对应的根据empId删除一条员工数据
     * @param empId 主键列
     * @return 受影响的行数
     */
    int delete(Integer empId);
}

```



##### 10.3.2 创建员工DAO接口实现类

```java
package com.atguigu.senior.dao.impl;

import com.atguigu.senior.dao.BaseDAO;
import com.atguigu.senior.dao.EmployeeDao;
import com.atguigu.senior.pojo.Employee;

import java.util.List;

public class EmployeeDaoImpl extends BaseDAO implements EmployeeDao {
    @Override
    public List<Employee> selectAll() {
        try {
            String sql = "SELECT emp_id empId,emp_name empName,emp_salary empSalary,emp_age empAge FROM t_emp";
            return executeQuery(Employee.class,sql,null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Employee selectByEmpId(Integer empId) {
        try {
            String sql = "SELECT emp_id empId,emp_name empName,emp_salary empSalary,emp_age empAge FROM t_emp where emp_id = ?";
            return executeQueryBean(Employee.class,sql,empId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int insert(Employee employee) {
        try {
            String sql = "INSERT INTO t_emp(emp_name,emp_salary,emp_age) VALUES (?,?,?)";
            return executeUpdate(sql,employee.getEmpName(),employee.getEmpSalary(),employee.getEmpAge());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int update(Employee employee) {
        try {
            String sql = "UPDATE t_emp SET emp_salary = ? WHERE emp_id = ?";
            return executeUpdate(sql,employee.getEmpSalary(),employee.getEmpId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int delete(Integer empId) {
        try {
            String sql = "delete from t_emp where emp_id = ?";
            return executeUpdate(sql,empId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

```



## 十一、事务

#### 11.1 事务回顾

- 数据库事务就是一种SQL语句执行的缓存机制，不会单条执行完毕就更新数据库数据，最终根据缓存内的多条语句执行结果统一判定!   一个事务内所有语句都成功及事务成功，我们可以触发commit提交事务来结束事务，更新数据!   一个事务内任意一条语句失败，即为事务失败，我们可以触发rollback回滚结束事务，数据回到事务之前状态!
- 一个业务涉及多条修改数据库语句!   例如:
  -  经典的转账案例，转账业务(A账户减钱和B账户加钱，要一起成功)   
  -  批量删除(涉及多个删除)       
  -  批量添加(涉及多个插入)  
- 事务的特性：
  1. 原子性（Atomicity）原子性是指事务是一个不可分割的工作单位，事务中的操作要么都发生，  要么都不发生。 
  2. 一致性（Consistency）事务必须使数据库从一个一致性状态变换到另外一个一致性状态。
  3. 隔离性（Isolation）事务的隔离性是指一个事务的执行不能被其他事务干扰，  即一个事务内部的操作及使用的数据对并发的其他事务是隔离的，并发执行的各个事务之间不能互相干扰。
  4. 持久性（Durability）持久性是指一个事务一旦被提交，它对数据库中数据的改变就是永久性的，  接下来的其他操作和数据库故障不应该对其有任何影响
- 事务的提交方式：
  - 自动提交：每条语句自动存储一个事务中，执行成功自动提交，执行失败自动回滚! 
  - 手动提交:  手动开启事务，添加语句，手动提交或者手动回滚即可!



#### 11.2 JDBC中事务实现

- 关键代码：

  - ```java
    try{
        connection.setAutoCommit(false); //关闭自动提交了
        //connection.setAutoCommit(false)也就类型于SET autocommit = off
        
        //注意，只要当前connection对象，进行数据库操作，都不会自动提交事务
        //数据库动作!
        //prepareStatement - 单一的数据库动作 c r u d 
        //connection - 操作事务 
        
        //所有操作执行正确，提交事务！
        connection.commit();
      }catch(Execption e){
        //出现异常，则回滚事务！
        connection.rollback();
      }
    ```



#### 11.3  JDBC事务代码实现

- 准备数据库表：

  - ```sql
    -- 继续在atguigu的库中创建银行表
    CREATE TABLE t_bank(
       id INT PRIMARY KEY AUTO_INCREMENT COMMENT '账号主键',
       account VARCHAR(20) NOT NULL UNIQUE COMMENT '账号',
       money  INT UNSIGNED COMMENT '金额，不能为负值') ;
       
    INSERT INTO t_bank(account,money) VALUES
      ('zhangsan',1000),('lisi',1000);
    
    ```

- DAO接口代码：

  - ```java
    public interface BankDao{
        int addMoney(Integer id,Integer money);
    
        int subMoney(Integer id,Integer money);
    }
    ```

- DAO实现类代码：

  - ```java
    public class BankDaoImpl  extends BaseDao implements BankDao{
        public int addMoney(Integer id,Integer money){
            try {
                String sql = "update t_bank set money = money + ? where id = ? ";
                return executeUpdate(sql,money,id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    
       public int subMoney(Integer id,Integer money){
            try {
                String sql = "update t_bank set money = money - ? where id = ? ";
                return executeUpdate(sql,money,id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    ```

- 测试代码：

  - ```java
    @Test
       public void testTransaction(){
           BankDao bankDao = new BankDaoImpl();
           Connection connection=null;
           try {
               //1.获取连接，将连接的事务提交改为手动提交
               connection = JDBCUtilV2.getConnection();
               connection.setAutoCommit(false);//开启事务，当前连接的自动提交关闭。改为手动提交！
      
               //2.操作减钱
               bankDao.subMoney(1,100);
      
               int i = 10 / 0;
      
               //3.操作加钱
               bankDao.addMoney(2,100);
      
               //4.前置的多次dao操作，没有异常，提交事务！
               connection.commit();
           } catch (Exception e) {
               try {
                   connection.rollback();
               } catch (Exception ex) {
                   throw new RuntimeException(ex);
               }
           }finally {
               JDBCUtilV2.release();
           }
       }
    ```

**注意：**

> 当开启事务后，切记一定要根据代码执行结果来决定是否提交或回滚！否则数据库看不到数据的操作结果！

# SpringBoot实现mybatis 

## 0.准备

查看你的jar包是否下载好了文件一般在

```
C:\Users\你的用户名\.m2\repository\org\mybatis
```

手动下载jar包

```
https://mvnrepository.com/artifact/org.mybatis.spring.boot/mybatis-spring-boot-autoconfigure/3.0.3

https://mvnrepository.com/artifact/org.mybatis/mybatis-spring/3.0.3

https://repo1.maven.org/maven2/org/mybatis/spring/boot/mybatis-spring-boot-autoconfigure/3.0.3/mybatis-spring-boot-autoconfigure-3.0.3.jar
```

手动添加依赖jar包

```
【正确命令 1】安装 mybatis-3.5.14
powershell
mvn install:install-file "-Dfile=D:\1\1\mybatis\mylib\mybatis-3.5.14.jar" "-DgroupId=org.mybatis" "-DartifactId=mybatis" "-Dversion=3.5.14" "-Dpackaging=jar" "-DgeneratePom=true"
【正确命令 2】安装 mybatis-spring-3.0.3
powershell
mvn install:install-file "-Dfile=D:\1\1\mybatis\mylib\mybatis-spring-3.0.3.jar" "-DgroupId=org.mybatis" "-DartifactId=mybatis-spring" "-Dversion=3.0.3" "-Dpackaging=jar" "-DgeneratePom=true"
【正确命令 3】安装 mybatis-spring-boot-autoconfigure-3.0.3
powershell
mvn install:install-file "-Dfile=D:\1\1\mybatis\mylib\mybatis-spring-boot-autoconfigure-3.0.3.jar" "-DgroupId=
```

## 1.数据库配置

```
-- 创建数据库
CREATE DATABASE IF NOT EXISTS bookdb
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE bookdb;

-- 创建图书表
CREATE TABLE IF NOT EXISTS book (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '图书ID',
    title VARCHAR(200) NOT NULL COMMENT '书名',
    author VARCHAR(100) NOT NULL COMMENT '作者',
    isbn VARCHAR(20) UNIQUE COMMENT 'ISBN编号',
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '价格',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    description TEXT COMMENT '图书描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_title (title),
    INDEX idx_author (author),
    INDEX idx_isbn (isbn)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书表';

-- 插入初始数据
INSERT INTO book (title, author, isbn, price, stock, description) VALUES
('Java核心技术卷I', 'Cay S. Horstmann', '978-7-111-52193-0', 119.00, 50, 'Java技术经典参考书，覆盖Java基础知识和核心特性'),
('Spring Boot实战', 'Craig Walls', '978-7-115-44096-5', 79.00, 35, 'Spring Boot权威指南，详细讲解Spring Boot框架的使用'),
('MyBatis从入门到精通', '刘增辉', '978-7-115-46138-0', 69.00, 40, 'MyBatis框架实战指南，包含完整的CRUD操作示例'),
('Redis设计与实现', '黄健宏', '978-7-115-35155-1', 79.00, 28, '深入剖析Redis内部实现原理与设计思想'),
('Vue.js实战', '梁灏', '978-7-115-49177-7', 89.00, 22, 'Vue.js框架全面教程，包含大量实战案例');

-- 验证数据
SELECT * FROM book;
```

**修改数据库配置**

在 `src/main/resources/application.properties` 中修改数据库连接信息：

```
``properties

spring.datasource.url=jdbc:mysql://localhost:3306/bookdb?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai

spring.datasource.username=root

spring.datasource.password=YOUR_PASSWORD
```

项目结构

```
# 🚀 项目结构
​```
mybatis-demo/
├── pom.xml
└── src/main/
    ├── java/com/example/mybatis/
    │   ├── MybatisApplication.java          # 主启动类
    │   ├── entity/
    │   │   └── Book.java                    # 图书实体类
    │   ├── mapper/
    │   │   ├── BookMapper.java              # Mapper接口
    │   └── mapper.xml                       # Mapper XML配置
    │   ├── service/
    │   │   └── BookService.java             # 业务服务层
    │   └── controller/
    │       └── BookController.java          # REST API控制器
    └── resources/
        ├── application.properties            # 应用配置
        ├── mapper/
        │   └── BookMapper.xml               # MyBatis SQL映射
        └── script/
            └── init_database.sql            # 数据库初始化脚本
```

## **2. 添加mybatis依赖**

```
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.3</version>
</dependency>

<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

## 3.配置mapper扫描

```
在主启动类添加 `@MapperScan` 注解：
​```java
@SpringBootApplication
@MapperScan("com.example.mybatis.mapper")  // 扫描Mapper接口
public class MybatisApplication {
    public static void main(String[] args) {
        SpringApplication.run(MybatisApplication.class, args);
    }
}
```

## **4.配置MyBatis**

```
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/bookdb?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# 数据库连接池配置
# 数据库连接池最大连接数
spring.datasource.hikari.maximum-pool-size=10
# 连接池最小空闲连接
spring.datasource.hikari.minimum-idle=5
# 连接超时时间（毫秒）
spring.datasource.hikari.connection-timeout=30000

# MyBatis XML映射文件路径
mybatis.mapper-locations=classpath:mapper/*.xml
# 实体类别名包
mybatis.type-aliases-package=com.example.mybatis.entity
# 下划线转驼峰命名
mybatis.configuration.map-underscore-to-camel-case=true
# 打印SQL日志
mybatis.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl

# 项目启动端口
server.port=8081
```

## 5.**CRUD操作实现**

**1.book实体类**

```
​```java
public class Book {
    private Integer id;
    private String title;
    private String author;
    private String isbn;
    private Double price;
    private Integer stock;
    private String description;
    // getter和setter方法
}
​```
```

 **2. BookMapper接口**

```
@Mapper
public interface BookMapper {

    List<Book> findAll();                    // 查询所有图书

    Book findById(@Param("id") Integer id);   // 根据ID查询

    int insert(Book book);                    // 添加图书

    int update(Book book);                   // 更新图书

    int delete(@Param("id") Integer id);     // 删除图书

    List<Book> findByTitle(@Param("title") String title);  // 按书名搜索
}
​```
```

**3. BookMapper.xml SQL映射**

```
​```xml
<mapper namespace="com.example.mybatis.mapper.BookMapper">

    <resultMap id="BookResultMap" type="Book">
        <id property="id" column="id"/>
        <result property="title" column="title"/>
        <result property="author" column="author"/>
        <result property="isbn" column="isbn"/>
        <result property="price" column="price"/>
        <result property="stock" column="stock"/>
        <result property="description" column="description"/>
    </resultMap>

    <!-- 查询所有 -->
    <select id="findAll" resultMap="BookResultMap">
        SELECT * FROM book ORDER BY id
    </select>

    <!-- 根据ID查询 -->
    <select id="findById" resultMap="BookResultMap">
        SELECT * FROM book WHERE id = #{id}
    </select>

    <!-- 插入数据 -->
    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO book (title, author, isbn, price, stock, description)
        VALUES (#{title}, #{author}, #{isbn}, #{price}, #{stock}, #{description})
    </insert>

    <!-- 更新数据 -->
    <update id="update">
        UPDATE book
        SET title=#{title}, author=#{author}, isbn=#{isbn},
            price=#{price}, stock=#{stock}, description=#{description}
        WHERE id = #{id}
    </update>

    <!-- 删除数据 -->
    <delete id="delete">
        DELETE FROM book WHERE id = #{id}
    </delete>

</mapper>
```

 **4. BookService业务层**

```
​```
@Service
public class BookService {

    @Autowired
    private BookMapper bookMapper;

    public List<Book> getAllBooks() {
        return bookMapper.findAll();
    }

    public Book getBookById(Integer id) {
        return bookMapper.findById(id);
    }

    public Book addBook(Book book) {
        bookMapper.insert(book);
        return book;
    }

    public Book updateBook(Book book) {
        bookMapper.update(book);
        return book;
    }

    public boolean deleteBook(Integer id) {
        return bookMapper.delete(id) > 0;
    }
}
​```
```

**5. BookController REST API**

```
``java
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Integer id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return bookService.addBook(book);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Integer id, @RequestBody Book book) {
        book.setId(id);
        return bookService.updateBook(book);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Integer id) {
        bookService.deleteBook(id);
    }
}
​```
```

## 6.REST API接口列表

| 方法   | 路径              | 描述             | 请求体    |
| ------ | ----------------- | ---------------- | --------- |
| GET    | `/api/books`      | 获取所有图书     | -         |
| GET    | `/api/books/{id}` | 根据 ID 获取图书 | -         |
| POST   | `/api/books`      | 添加新图书       | Book 对象 |
| PUT    | `/api/books/{id}` | 更新图书信息     | Book 对象 |
| DELETE | `/api/books/{id}` | 删除图书         | -         |

## 7.调用过程

### 查询调用过程

```

当客户端调用 `GET /api/books/1` 查询图书时，整个调用链路如下：
​```
┌─────────────┐
│  客户端请求  │
│ GET /api/books/1
└──────┬──────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────────────┐
│  BookController.getBookById(1)                                       │
│  ─────────────────────────────────────────────────────────────────── │
│  @GetMapping("/{id}")                                                │
│  public Book getBookById(@PathVariable Integer id) {                 │
│      return bookService.getBookById(id);  // 调用Service层          │
│  }                                                                   │
└──────┬───────────────────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────────────┐
│  BookService.getBookById(1)                                          │
│  ─────────────────────────────────────────────────────────────────── │
│  @Service                                                            │
│  public class BookService {                                           │
│      @Autowired                                                       │
│      private BookMapper bookMapper;                                  │
│                                                                       │
│      public Book getBookById(Integer id) {                           │
│          return bookMapper.findById(id);  // 调用Mapper接口          │
│      }                                                               │
│  }                                                                   │
└──────┬───────────────────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────────────┐
│  BookMapper.findById(1) - MyBatis自动实现                            │
│  ─────────────────────────────────────────────────────────────────── │
│  @Mapper                                                            │
│  public interface BookMapper {                                       │
│      Book findById(@Param("id") Integer id);                         │
│  }                                                                   │
│                                                                       │
│  // MyBatis会根据XML配置自动生成代理对象                              │
└──────┬───────────────────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────────────┐
│  BookMapper.xml - 执行SQL                                            │
│  ─────────────────────────────────────────────────────────────────── │
│  <select id="findById" resultMap="BookResultMap">                    │
│      SELECT id, title, author, isbn, price, stock, description       │
│      FROM book                                                       │
│      WHERE id = #{id}                                               │
│  </select>                                                           │
└──────┬───────────────────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────────────┐
│  MySQL数据库执行                                                     │
│  ─────────────────────────────────────────────────────────────────── │
│  SELECT id, title, author, isbn, price, stock, description           │
│  FROM book                                                           │
│  WHERE id = 1;                                                      │
│                                                                       │
│  返回结果: {id:1, title:"Java核心技术卷I", author:"Cay S. Horstmann",...} │
└──────┬───────────────────────────────────────────────────────────────┘
       │
       ▼
       数据沿着调用链逐层返回：
       ResultSet → BookMapper.findById() → BookService.getBookById()
       → BookController.getBookById() → JSON响应给客户端
​```


```

### 查询调用过程总结

| 层级 | 组件       | 职责                         |
| ---- | ---------- | ---------------------------- |
| 1️⃣    | Controller | 接收HTTP请求，调用Service    |
| 2️⃣    | Service    | 处理业务逻辑，调用Mapper     |
| 3️⃣    | Mapper接口 | MyBatis生成代理对象，执行SQL |
| 4️⃣    | Mapper XML | 定义SQL语句和结果映射        |
| 5️⃣    | MySQL      | 执行SQL，返回数据            |

### 🔄 修改调用过程

```
当客户端调用 `PUT /api/books/1` 更新图书时，整个调用链路如下：

​```
┌─────────────┐
│  客户端请求  │
│ PUT /api/books/1
│ Body: {title:"新书名", ...}
└──────┬──────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────────────┐
│  BookController.updateBook(1, book)                                 │
│  ─────────────────────────────────────────────────────────────────── │
│  @PutMapping("/{id}")                                                │
│  public Book updateBook(@PathVariable Integer id,                    │
│                         @RequestBody Book book) {                    │
│      book.setId(id);                        // 设置ID               │
│      return bookService.updateBook(book);  // 调用Service层          │
│  }                                                                   │
└──────┬───────────────────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────────────┐
│  BookService.updateBook(book)                                        │
│  ─────────────────────────────────────────────────────────────────── │
│  public Book updateBook(Book book) {                                 │
│      bookMapper.update(book);  // 调用Mapper接口                     │
│      return book;                                                    │
│  }                                                                   │
└──────┬───────────────────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────────────┐
│  BookMapper.update(book) - MyBatis自动实现                           │
│  ─────────────────────────────────────────────────────────────────── │
│  @Mapper                                                            │
│  public interface BookMapper {                                       │
│      int update(Book book);                                          │
│  }                                                                   │
└──────┬───────────────────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────────────┐
│  BookMapper.xml - 执行SQL                                            │
│  ─────────────────────────────────────────────────────────────────── │
│  <update id="update">                                                │
│      UPDATE book                                                      │
│      SET title = #{title},                                           │
│          author = #{author},                                         │
│          isbn = #{isbn},                                             │
│          price = #{price},                                           │
│          stock = #{stock},                                           │
│          description = #{description}                                │
│      WHERE id = #{id}                                               │
│  </update>                                                           │
└──────┬───────────────────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────────────┐
│  MySQL数据库执行                                                     │
│  ─────────────────────────────────────────────────────────────────── │
│  UPDATE book                                                         │
│  SET title = '新书名',                                               │
│      author = '...',                                                 │
│      ...                                                             │
│  WHERE id = 1;                                                      │
│                                                                       │
│  返回: 影响行数 1                                                     │
└──────┬───────────────────────────────────────────────────────────────┘
       │
       ▼
       执行结果沿调用链返回：
       影响行数 → BookMapper.update() → BookService.updateBook()
       → BookController.updateBook() → JSON响应给客户端
​```
```

### 修改调用过程总结

| 步骤 | 组件 | 操作 |

|------|------|------|

| 1️⃣ | Controller | 接收请求体JSON，反序列化为Book对象 |

| 2️⃣ | Controller | 设置ID，调用Service |

| 3️⃣ | Service | 验证数据，调用Mapper |

| 4️⃣ | Mapper | 执行UPDATE SQL语句 |

| 5️⃣ | MySQL | 更新数据，返回影响行数 |

| 6️⃣ | 返回 | 逐层返回更新后的Book对象 |

## 8.API使用示例

 查询所有图书

\```bash

curl http://localhost:8080/api/books

\```

**### 根据ID查询**

\```bash

curl http://localhost:8080/api/books/1

\```

**### 添加图书**

\```bash

curl -X POST http://localhost:8080/api/books \

 -H "Content-Type: application/json" \

 -d '{"title":"新书","author":"作者","isbn":"123-456","price":59.99,"stock":100,"description":"这是一本新书"}'

\```

curl -X POST http://localhost:8081/api/books -H "Content-Type: application/json" -d "{"title":"新书","author":"作者","isbn":"123-456","price":59.99,"stock":100,"description":"这是一本新书"}"

**### 更新图书**



\```bash

curl -X PUT http://localhost:8080/api/books/1 \

 -H "Content-Type: application/json" \

 -d '{"title":"更新后的书名","author":"新作者","isbn":"123-456","price":69.99,"stock":50,"description":"更新后的描述"}'

\```

curl -X PUT http://localhost:8081/api/books/1 -H "Content-Type: application/json" -d "{"title":"更新后的书名","author":"新作者","isbn":"123-456","price":69.99,"stock":50,"description":"更新后的描述"}"



**### 删除图书**



\```bash

curl -X DELETE http://localhost:8080/api/books/1

\```



## **9 常见问题**

**### 1. 数据库连接失败**

\- 检查MySQL服务是否启动

\- 验证用户名和密码是否正确

\- 确认数据库`bookdb`已创建

**### 2. Mapper XML找不到**

\- 确保`application.properties`中配置了正确的mapper路径

\- 检查XML文件的namespace是否与Mapper接口全限定名一致

**### 3. 字段映射失败**

\- 确认实体类属性名与数据库列名的映射关系

\- 启用`map-underscore-to-camel-case=true`处理驼峰命名

## **10学习资源**

\- [MyBatis官方文档](https://mybatis.org/mybatis-3/zh_CN/index.html)

\- [Spring Boot官方文档](https://spring.io/projects/spring-boot)

\- [MyBatis-Spring-Boot官方文档](https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/)

# MyBatis进阶

## 动态SQL

# SpringBoot实现Hibernate

## 一、开发环境

- JDK 17
- Spring Boot 3.x 都行
- MySQL
- Maven
- SQL

```
CREATE DATABASE hibernate_demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hibernate_demo;

CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(30) DEFAULT NULL COMMENT '用户名',
  `age` INT DEFAULT NULL COMMENT '年龄',
  `email` VARCHAR(50) DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

INSERT INTO `user` (username,age,email) VALUES
('李四',22,'lisi@163.com'),
('王五',25,'wangwu@qq.com');
```

## 二、添加依赖（pom.xml）

只需要 2 个核心依赖：

**Spring Data JPA**（底层就是 Hibernate） + **MySQL 驱动**

```
<dependencies>
    <!-- Spring Boot Data JPA（内置 Hibernate） -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- MySQL 驱动 -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Spring Web（用于接口测试） -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

------

## 三、配置 application.yml

在 `src/main/resources/application.yml` 中添加：

```
spring:
  # 数据库配置
  datasource:
    url: jdbc:mysql://localhost:3306/hibernate_demo?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: zxy
    password: 412826
    driver-class-name: com.mysql.cj.jdbc.Driver

  # JPA + Hibernate 配置
  jpa:
    hibernate:
      ddl-auto: update  # 自动建表/更新表（开发用）
    show-sql: true      # 打印执行的SQL
    properties:
      hibernate:
        format_sql: true # 格式化SQL
    database-platform: org.hibernate.dialect.MySQL8Dialect
```

**说明**：

- `ddl-auto: update`：实体类改了，数据库表自动更新
- 底层就是 **Hibernate** 工作

------

## 四、编写实体类（映射数据库表）

以 `User` 用户表为例：

```
package com.example.demo.entity;

import jakarta.persistence.*; // Spring Boot 3.x用这个
// Spring Boot 2.x 用：javax.persistence.*

@Entity
@Table(name = "user") // 对应数据库表名
public class User {

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增
    private Long id;

    private String username;
    private Integer age;
    private String email;

    // 无参构造（必须）
    public User() {}

    // getter / setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
```

------

## 五、编写 DAO（Repository）

直接继承 `JpaRepository`，**自带增删改查**，不用写 SQL！

```
package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<实体类, 主键类型>
public interface UserRepository extends JpaRepository<User, Long> {
    // 这里什么都不用写，直接拥有CRUD
}
```

------

## 六、编写 Service（业务层）

```
package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 1. 新增/更新用户
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // 2. 根据ID查询
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // 3. 查询所有
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 4. 删除用户
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
```

------

## 七、编写 Controller（接口层）

提供 HTTP 接口测试增删改查：

```
package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 1. 新增用户 POST
    @PostMapping("/add")
    public User addUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    // 2. 根据ID查询 GET
    @GetMapping("/get/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // 3. 查询所有 GET
    @GetMapping("/all")
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    // 4. 更新用户 PUT
    @PutMapping("/update")
    public User updateUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    // 5. 删除用户 DELETE
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "删除成功";
    }
}
```

------

## 八、接口测试（直接用）

启动项目后，用 **Postman / Apifox / 浏览器** 测试：

### 1. 新增用户

- 请求：POST `http://localhost:8080/user/add`
- Body：

```
{
    "username": "张三",
    "age": 20,
    "email": "zhangsan@qq.com"
}
```

### 2. 根据 ID 查询

- GET `http://localhost:8080/user/get/1`

### 3. 查询所有

- GET `http://localhost:8080/user/all`

### 4. 更新用户

- PUT `http://localhost:8080/user/update`
- 带上 id 即可自动更新

### 5. 删除用户

- DELETE `http://localhost:8080/user/delete/1`

------

## 九、完整流程总结（超清晰）

1. 引入 **spring-boot-starter-data-jpa**（内置 Hibernate）
2. 配置 `application.yml` 数据库 + Hibernate 行为
3. 写 **@Entity 实体类** 映射表
4. 写 **Repository** 继承 JpaRepository
5. 写 **Service** 调用 CRUD
6. 写 **Controller** 提供接口
7. 测试接口 ✅

------

### 总结

1. Spring Boot **无需手动集成 Hibernate**，JPA 已内置
2. 只需继承 `JpaRepository`，**自动拥有全套增删改查**
3. 代码极简、可直接运行、适合学习和项目使用







**多条件查询 + 分页 + HQL + 原生 SQL**

## 十、先给 UserRepository 加进阶方法（最关键）

直接在你原来的 `UserRepository.java` 里追加这些方法：

java



运行







```
package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    // ===================== 【1】多条件查询（自动生成SQL） =====================
    // 根据 用户名模糊 + 年龄 精确查询
    List<User> findByUsernameLikeAndAge(String username, Integer age);

    // 根据 用户名 或 邮箱 模糊查询
    List<User> findByUsernameContainingOrEmailContaining(String username, String email);

    // 年龄大于等于
    List<User> findByAgeGreaterThanEqual(Integer age);

    // ===================== 【2】HQL 查询（面向对象写SQL） =====================
    @Query("SELECT u FROM User u WHERE u.age >= :age AND u.username LIKE %:name%")
    List<User> findByHql(@Param("name") String name, @Param("age") Integer age);

    // ===================== 【3】原生 SQL 查询 =====================
    @Query(value = "SELECT * FROM user WHERE age >= ?1 AND email LIKE CONCAT('%',?2,'%')", nativeQuery = true)
    List<User> findByNativeSql(Integer age, String email);

    // ===================== 【4】分页查询 =====================
    Page<User> findByAgeGreaterThanEqual(Integer age, Pageable pageable);
}
```

------

## 十一、给 UserService 加进阶方法

java



运行







```
// ===================== 多条件查询 =====================
public List<User> findByCondition(String name, Integer age){
    return userRepository.findByUsernameLikeAndAge("%"+name+"%", age);
}

// 用户名或邮箱模糊查询
public List<User> findByKeyword(String keyword){
    return userRepository.findByUsernameContainingOrEmailContaining(keyword, keyword);
}

// ===================== HQL 查询 =====================
public List<User> findByHql(String name, Integer age){
    return userRepository.findByHql(name, age);
}

// ===================== 原生SQL查询 =====================
public List<User> findBySql(Integer age, String email){
    return userRepository.findByNativeSql(age, email);
}

// ===================== 分页查询 =====================
public Page<User> findByPage(Integer age, int page, int size){
    Pageable pageable = PageRequest.of(page-1, size); // 页码从0开始
    return userRepository.findByAgeGreaterThanEqual(age, pageable);
}
```

------

## 十二、给 Controller 加接口（可直接测试）

java



运行







```
// 1. 多条件查询：用户名模糊 + 年龄精确
@GetMapping("/condition")
public List<User> condition(
        @RequestParam String name,
        @RequestParam Integer age) {
    return userService.findByCondition(name, age);
}

// 2. 关键词搜索（用户名 or 邮箱）
@GetMapping("/search")
public List<User> search(@RequestParam String keyword) {
    return userService.findByKeyword(keyword);
}

// 3. HQL 查询
@GetMapping("/hql")
public List<User> hql(
        @RequestParam String name,
        @RequestParam Integer age) {
    return userService.findByHql(name, age);
}

// 4. 原生SQL查询
@GetMapping("/sql")
public List<User> sql(
        @RequestParam Integer age,
        @RequestParam String email) {
    return userService.findBySql(age, email);
}

// 5. 分页查询（年龄>=?，第几页，每页几条）
@GetMapping("/page")
public Page<User> page(
        @RequestParam Integer age,
        @RequestParam int page,
        @RequestParam int size) {
    return userService.findByPage(age, page, size);
}
```

------

## 十三、直接给你测试接口（复制就能用）

### 1. 多条件查询

plaintext









```
GET http://localhost:8080/user/condition?name=张&age=20
```

### 2. 关键词搜索（用户名 / 邮箱）

plaintext









```
GET http://localhost:8080/user/search?keyword=qq
```

### 3. HQL 查询

plaintext









```
GET http://localhost:8080/user/hql?name=李&age=20
```

### 4. 原生 SQL 查询

plaintext









```
GET http://localhost:8080/user/sql?age=20&email=qq
```

### 5. 分页查询（年龄 >=20，第 1 页，每页 2 条）

plaintext









```
GET http://localhost:8080/user/page?age=20&page=1&size=2
```

------

## 十四、超清晰总结

1. **多条件查询**：不用写 SQL，用 `findByXxxAndXxx` 自动生成
2. **HQL**：面向对象写 SQL，`from User u where...`
3. **原生 SQL**：直接写 MySQL 语句，`nativeQuery=true`
4. **分页**：用 `Pageable` + `Page`，自动返回总条数、总页数



**排序 + 多表关联 + 批量删除 + DTO 返回 + 2 张新表 SQL**

```
-- 角色表（1对1：一个用户一个角色）
CREATE TABLE role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(20) NOT NULL,
    user_id BIGINT NOT NULL
);

-- 订单表（1对多：一个用户多个订单）
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(30) NOT NULL,
    money DECIMAL(10,2),
    user_id BIGINT NOT NULL
);

-- 测试数据
INSERT INTO role(role_name, user_id) VALUES ('管理员',1),('普通用户',2);
INSERT INTO orders(order_no, money, user_id) VALUES 
('ORDER001',99.8,1),
('ORDER002',199.8,1),
('ORDER003',299.8,2);
```





## 十五、创建 2 个新实体类（做关联映射）

### 1. Role.java（1 对 1）

java



运行







```
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roleName;

    // 1对1：一个角色对应一个用户
    @OneToOne
    @JoinColumn(name = "user_id") // 外键列
    private User user;

    // getter setter
}
```

### 2. Orders.java（1 对多）

java



运行







```
@Entity
@Table(name = "orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNo;
    private Double money;

    // 多对1：多个订单对应一个用户
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // getter setter
}
```

------

## 十六、DTO 返回（不暴露实体，更安全、更灵活）

创建一个**用户 + 角色 + 订单**的返回对象，**前端最爱用**：

java



运行







```
package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data // 自动生成get/set/toString
public class UserDTO {
    private Long userId;
    private String username;
    private Integer age;
    private String roleName; // 角色名
    private List<OrderDTO> orders; // 订单列表
}

@Data
class OrderDTO {
    private String orderNo;
    private Double money;
}
```

------

## 十七、Repository 新增高级方法

直接在你的 `UserRepository` 里追加：

java



运行







```
// ===================== 排序查询 =====================
List<User> findByAgeGreaterThanEqual(Integer age, Sort sort);

// ===================== 批量删除 =====================
void deleteByIdIn(List<Long> ids);

// ===================== 多表关联查询（HQL） =====================
@Query("SELECT u FROM User u " +
       "LEFT JOIN Role r ON u.id=r.user.id " +
       "LEFT JOIN Orders o ON u.id=o.user.id " +
       "WHERE r.roleName = :roleName")
List<User> findByRoleName(@Param("roleName") String roleName);
```

------

## 十八、Service 新增高级方法

java



运行







```
// ===================== 1. 排序查询 =====================
public List<User> getSortByAge(Integer age){
    // 按年龄倒序（ASC 正序）
    Sort sort = Sort.by(Sort.Direction.DESC, "age");
    return userRepository.findByAgeGreaterThanEqual(age, sort);
}

// ===================== 2. 批量删除 =====================
public void batchDelete(List<Long> ids){
    userRepository.deleteByIdIn(ids);
}

// ===================== 3. 多表关联查询（用户+角色+订单） =====================
public List<UserDTO> getUserByRole(String roleName){
    List<User> userList = userRepository.findByRoleName(roleName);
    // 转 DTO 返回
    return userList.stream().map(user -> {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setAge(user.getAge());
        // 这里你可以自己set角色、订单数据
        return dto;
    }).collect(Collectors.toList());
}
```

------

## 十九、Controller 新增接口（可直接测试）

java



运行







```
// 1. 排序查询：年龄>=? 按年龄倒序
@GetMapping("/sort")
public List<User> sort(@RequestParam Integer age) {
    return userService.getSortByAge(age);
}

// 2. 批量删除：传入多个ID，用逗号分隔
@DeleteMapping("/batchDelete")
public String batchDelete(@RequestParam List<Long> ids) {
    userService.batchDelete(ids);
    return "批量删除成功";
}

// 3. 多表关联：根据角色名查询用户
@GetMapping("/byRole")
public List<UserDTO> byRole(@RequestParam String roleName) {
    return userService.getUserByRole(roleName);
}
```

------

## 二十、直接给你测试接口（复制就能用）

### 1. 排序查询

plaintext









```
GET http://localhost:8080/user/sort?age=20
```

### 2. 批量删除

plaintext









```
DELETE http://localhost:8080/user/batchDelete?ids=1,2,3
```

### 3. 多表关联查询（查角色为 “管理员” 的用户）

plaintext









```
GET http://localhost:8080/user/byRole?roleName=管理员
```

------

## 二十一、你现在拥有的所有功能（全了）

✅ 基础增删改查

✅ 多条件模糊查询

✅ 分页查询

✅ HQL / 原生 SQL

✅ **排序查询**

✅ **多表关联（1 对 1、1 对多）**

✅ **批量删除**

✅ **DTO 安全返回**

✅ 自动建表、打印 SQL

# Hibernate、MyBatis、PreparedStatement 三者区别

## 一、基础定位

**PreparedStatement 是 JDBC 原生 API**

JDBC 原生预编译对象，最底层数据库操作 API，手写 SQL、手动映射结果。

**Hibernate、MyBatis 是封装 JDBC 的持久层框架**

**Hibernate** 通过「元数据映射 + 自动 SQL + 自动建表 + 自动结果封装 + 自动关联」，让 Java 对象和数据库表完全透明交互:**全自动ORM**，对象直接操作数据库



**MyBatis** 是 **半自动 ORM**（半自动化持久层框架），它只做对象与 SQL 的映射，不自动生成 SQL。

SQL 与代码分离，可控 SQL，灵活度高。

Hibernate 全自动工作流程图

![image-20260523072328178](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20260523072328178.png)

- **元数据解析**：读取实体注解，确立对象与数据表、字段的映射关系
- **动作触发**：程序调用保存、查询、删除等业务方法
- **自动 SQL 生成**：依托映射规则，拼接对应的增删改查 SQL
- **表结构维护**：依据 ddl-auto 策略，自动创建、更新数据表
- **事务管控**：执行过程异常则回滚，正常执行提交事务
- **数据库交互**：通过 JDBC 发送 SQL，完成数据读写
- **结果反向映射**：查询结果集自动反射赋值，转为 Java 对象交付业务层

## 二、核心对比表

|    对比项    |    PreparedStatement     |         Hibernate          |            MyBatis             |
| :----------: | :----------------------: | :------------------------: | :----------------------------: |
|     层级     |    JDBC 底层原生 API     |       上层 ORM 框架        |         上层 ORM 框架          |
|   SQL 编写   |   **必须手写完整 SQL**   |   极少手写，框架自动生成   | **手动写 SQL**，XML / 注解编写 |
|   ORM 映射   | 手动封装对象、解析结果集 |      自动表与实体映射      |        手动配置字段映射        |
| SQL 注入防护 |     占位符`?`防注入      |      底层封装自带防护      |        #{} 占位符防注入        |
|   开发效率   |      低，重复代码多      |    极高，CRUD 开箱即用     |     中等，需维护 SQL 文件      |
|     性能     |   原生最快，无额外开销   |   稍慢，有反射、缓存开销   |          性能接近原生          |
| 数据库兼容性 |      切换库需改 SQL      |  跨库适配强，基本不改代码  |       切换库少量调整 SQL       |
|   学习难度   |      简单，语法固定      | 偏高，规则、缓存、关联复杂 |              适中              |
|   适用场景   |  简单临时查询、底层源码  |    快速开发、中小型项目    |  复杂业务、多联表、大数据项目  |

------

## 三、关键特性差异

### 1. PreparedStatement 特点

- 最基础底层，所有框架底层最终都会调用它
- 固定流程：写 SQL→设占位符参数→执行→手动封装数据
- 代码冗余，增删改查模板重复度极高
- 无封装、无缓存、无对象映射能力

### 2. Hibernate 特点

- 面向对象编程，不用关心数据表细节
- 自动建表、自动生成 CRUD SQL
- 自带一级、二级缓存，查询提速明显
- 多表关联、级联操作便捷
- 复杂 SQL、定制化查询编写不便

### 3. MyBatis 特点

- SQL 独立存放，开发者全权掌控 SQL 语句
- 解除代码与 SQL 强耦合，方便 DBA 优化调优
- 支持动态 SQL，适配多变查询条件
- 无自带强缓存，轻量化、占用资源少
- 实体和字段映射需手动配置

------

## 四、执行流程直观区别

1. **PreparedStatement**

   Java 代码 → 手写 SQL → 赋值占位符 → 执行 → 手动转 Java 对象

2. **Hibernate**

   Java 实体对象 → 框架自动生成 SQL → 底层调用 PreparedStatement → 自动封装对象

3. **MyBatis**

   Java 方法 → 调用 XML / 注解 SQL → 框架封装参数 → 底层调用 PreparedStatement → 按配置映射返回对象

------

## 五、选型速记

1. 底层原理学习、简单小查询 → **PreparedStatement**
2. 追求极速开发、少写代码、常规业务 → **Hibernate**
3. 业务复杂、SQL 需优化、联表多 → **MyBatis**