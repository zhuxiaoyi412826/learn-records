# JDBC

## 链接

这是 MySQL JDBC 连接串，拆开看就两部分：定位数据库 + 连接参数。

```
url: ${DB_URL:jdbc:mysql://127.0.0.1:3306/restaurant?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true}
```

## 一、定位部分（`?` 之前）

表格

| 片段          | 含义                                                         |
| ------------- | ------------------------------------------------------------ |
| `jdbc:mysql:` | 协议头，告诉 DriverManager 使用 MySQL 驱动（对应 `com.mysql.cj.jdbc.Driver`） |
| `//127.0.0.1` | 数据库主机 IP，本机回环地址（等价 [localhost](https://localhost)） |
| `:3306`       | MySQL 默认端口                                               |
| `/restaurant` | 默认连接的数据库名；后续 SQL 可省略库前缀，直接写表名        |

> 注意：建库场景（`CREATE DATABASE`）需要去掉 `/restaurant`，先连接到数据库服务实例，再执行建库语句。

## 二、参数部分（`?` 之后，`&` 分隔）

表格

| 参数                           | 作用                            | 不写会怎样                                                   |
| ------------------------------ | ------------------------------- | ------------------------------------------------------------ |
| `serverTimezone=Asia/Shanghai` | 指定 MySQL 会话时区             | MySQL8 必填，否则报时区异常；`LocalDateTime` 读写相差 8 小时 |
| `useUnicode=true`              | 开启 Unicode 传输               | 配合`characterEncoding`防止中文乱码                          |
| `characterEncoding=utf8`       | 设置字符编码为 UTF-8            | 中文会显示问号`???`                                          |
| `useSSL=false`                 | 关闭 SSL 加密                   | 生产环境建议改为`true`；本地不加会弹出证书校验警告，MySQL8 默认启用 SSL |
| `allowPublicKeyRetrieval=true` | 允许客户端从服务端获取 RSA 公钥 | 使用`mysql_native_password`认证时，会报错 `Public Key Retrieval is not allowed` |

## DriverManager

`java.sql.DriverManager` 是 **JDBC 原生的驱动管理器**，位于 `java.sql` 包，JDK 自带。

> 一句话：**负责加载数据库驱动类、根据 JDBC 连接 URL 获取数据库连接 Connection**。

### 核心职责

1. 注册 / 加载数据库驱动（Driver）
2. 遍历所有已注册驱动，匹配 JDBC URL，拿到数据库连接 `Connection`
3. 管理驱动列表，提供关闭、获取日志等辅助方法

### 核心方法

| 方法                                               | 作用                                                   |
| -------------------------------------------------- | ------------------------------------------------------ |
| `Class.forName("com.mysql.cj.jdbc.Driver")`        | **加载驱动类**（MySQL8 新版本可省略，SPI 自动加载）    |
| `DriverManager.getConnection(url, user, password)` | 最常用，传入连接串、账号密码，返回`Connection`连接对象 |

### 典型代码示例

```
// 1. 加载驱动（MySQL8 可省略，SPI自动注册驱动）
Class.forName("com.mysql.cj.jdbc.Driver");

// 2. JDBC连接URL
String url = "jdbc:mysql://127.0.0.1:3306/restaurant?serverTimezone=Asia/Shanghai...";
String user = "root";
String pwd = "123456";

// 3. 获取数据库连接
Connection conn = DriverManager.getConnection(url, user, pwd);
```

### 底层原理

1. `com.mysql.cj.jdbc.Driver` 类里面**静态代码块**，会把自己注册到 `DriverManager`

```
static {
    DriverManager.registerDriver(new Driver());
}
```

1. 调用 `getConnection()` 时：DriverManager 遍历内部所有注册的 Driver
2. 逐个调用 `driver.acceptsURL(url)` 判断驱动能不能处理这个 jdbc url
3. 匹配成功，调用驱动的`connect()`创建连接返回 Connection

> MySQL8+ 驱动包 `mysql-connector-java 8.x` 里有 `META-INF/services/java.sql.Driver` 文件（SPI），启动自动加载驱动，**不再需要手动写 Class.forName**

### 缺点（重点）

1. **所有连接全局统一管理，没有连接池**，每次 getConnection 新建物理连接，频繁创建销毁性能差
2. 不支持连接复用，生产环境**几乎不会直接用 DriverManager**
3. 多驱动场景容易出现驱动冲突

### 和连接池的关系

- `DriverManager`：**原始 API，用来创建单个物理连接**
- HikariCP、Druid：**连接池**，底层内部还是调用`DriverManager`来创建连接，做连接复用、超时管理

### 面试一句话总结

> DriverManager 是 JDBC 原生驱动管理器，作用是加载数据库驱动，根据 url、账号密码创建数据库连接；**生产不直接使用，连接池底层依赖它**。

## Driver / DriverManager / Connection

> 一句话概括： **Driver：数据库驱动；DriverManager：驱动管理器，帮你找 Driver；Connection：数据库连接会话。**
>
> Driver 是干活的底层驱动；DriverManager 是中介帮你找到 Driver；Connection 就是 Java 和数据库之间的那条 TCP 通道。

| 类 / 接口       | 包                                 | 作用                                                         |
| --------------- | ---------------------------------- | ------------------------------------------------------------ |
| `Driver`        | `java.sql.Driver`（接口）          | 数据库厂商实现的驱动接口；知道怎么解析 JDBC URL、创建物理连接。MySQL 实现类：`com.mysql.cj.jdbc.Driver` |
| `DriverManager` | `java.sql.DriverManager`（工具类） | JDK 提供的驱动管理器；维护所有注册的 Driver；根据 url 挑选合适 Driver，调用 Driver 获取 Connection |
| `Connection`    | `java.sql.Connection`（接口）      | **数据库连接会话**。代表 Java 程序和 MySQL 之间的一条物理通道；用来创建 Statement/PreparedStatement 执行 SQL |

### 执行流程（顺序）

1. 加载 

   ```
   com.mysql.cj.jdbc.Driver
   ```

   - 类的静态代码块执行：`DriverManager.registerDriver(this)`，把自己注册进 DriverManager

2. 调用 `DriverManager.getConnection(url,user,pwd)`

3. DriverManager 遍历所有注册的 Driver，调用 `driver.acceptsURL(url)` 判断该驱动是否能处理这个 url

4. 找到匹配的 MySQL Driver，调用 `driver.connect(url, properties)` → 返回 **Connection**

5. 通过 Connection 创建 PreparedStatement 执行 SQL

6. 使用完毕关闭 Connection（原生 DriverManager 模式下，关闭直接销毁物理连接）

### 核心方法

#### Driver（接口）

- `boolean acceptsURL(String url)`：判断这个驱动能不能处理当前 jdbc 连接串
- `Connection connect(String url, Properties info)`：**真正建立 TCP 连接，返回 Connection**

#### DriverManager（工具类）

- `registerDriver(Driver driver)`：注册驱动
- `getConnection(url,user,password)`：核心方法，获取连接

#### Connection（连接会话）

- `PreparedStatement prepareStatement(String sql)`：预编译 SQL（推荐）
- `Statement createStatement()`：普通语句
- `commit()` / `rollback()`：事务提交、回滚
- `close()`：关闭连接

### 关键区分（面试高频）

1. **Driver**：是数据库厂商写的底层实现，知道怎么和 MySQL 建立 TCP 通信。
2. **DriverManager**：JDK 自带的 “中介”，**本身不建立连接**，只是帮你找到合适 Driver。
3. **Connection**：**一次 TCP 连接会话**，所有 SQL 都走这个通道。原生 JDBC 每次`getConnection()`新建 TCP，`close()`直接断开 TCP。

> 连接池（Hikari/Druid）：缓存复用 Connection，**底层依然调用 Driver 去创建物理连接**，只是 close 不会断开 TCP，归还到池。

### 极简代码串联三者

```
// 1. MySQL驱动实现类（Driver）
// Class.forName("com.mysql.cj.jdbc.Driver"); //MySQL8 SPI自动加载，可省略

String url="jdbc:mysql://127.0.0.1:3306/restaurant?xxx";
// 2. DriverManager作为中介，调用Driver的connect，拿到Connection
Connection conn = DriverManager.getConnection(url,"root","123456");

// 3. 使用连接执行SQL
PreparedStatement pstmt = conn.prepareStatement("select * from t_user");
```

# Moder 

> 一般叫 **Model / Entity / Bean**，原生 JavaWeb（Servlet+JDBC）里，用来**映射数据库表**，俗称数据模型。
>
> Model 实体类就是**数据库表的 Java 对象映射**，用来保存一行记录，在 DAO、Servlet 之间传递数据，规范要求私有属性、无参构造、getter/setter。

## ✅ 作用

1. **封装数据库一行数据**：一张表对应一个 Model 类，表的每一列对应类的属性。
2. **数据载体**：Servlet 从 JDBC 查询出来的 ResultSet，封装成 Model 对象，在 Servlet、JSP、页面之间传递数据。
3. **解耦**：数据库字段和业务代码分离，不用到处写零散变量。
4. **面向对象思想**：把数据库的 “行” 变成 Java 对象。

> 三层架构对应： `DAO层(JDBC)` ↔ `Model实体类` ↔ `Servlet(Controller)`

## ✅ 书写规范（原生 JavaWeb，点餐系统示例：菜品 Food）

```
// 实体类：对应数据库 food 表
public class Food {
    // 1. 私有成员变量，和表字段一一对应
    private Integer id;
    private String foodName;
    private Double price;
    private String img;
    private Integer stock;

    // 2. 无参构造【必须要有！JDBC反射实例化要用】
    public Food(){}

    // 可选：全参构造
    public Food(Integer id, String foodName, Double price, String img, Integer stock) {
        this.id = id;
        this.foodName = foodName;
        this.price = price;
        this.img = img;
        this.stock = stock;
    }

    // 3. Getter & Setter 【必须】读写属性
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    // 4. toString() 方便打印调试
    @Override
    public String toString() {
        return "Food{" +
                "id=" + id +
                ", foodName='" + foodName + '\'' +
                ", price=" + price +
                ", img='" + img + '\'' +
                ", stock=" + stock +
                '}';
    }
}
```

##  书写硬性要求（面试重点）

1. 属性**private**，不能直接访问，通过 get/set
2. **必须无参构造函数**（JDBC 反射创建对象，没有会报错）
3. 属性名尽量和数据库字段一致；不一致时 DAO 手动映射
4. 包装类型：`Integer` / `Double`，不要用`int`/`double`

> 原因：数据库字段允许 NULL，基础类型不能存 null，会出现空值异常

5. 重写`toString`，方便日志、调试

## ✅ 使用示例（JDBC DAO 层）

```
// 从ResultSet封装成Model对象
Food food = new Food();
food.setId(rs.getInt("id"));
food.setFoodName(rs.getString("food_name"));
food.setPrice(rs.getDouble("price"));
```

# DAO

> DAO = **Data Access Object 数据访问对象**
>
> 职责：**只负责和数据库交互**，只写增删改查 SQL，**不处理业务逻辑**。 调用关系：`Servlet → DAO → DBUtil → MySQL`
>
> DAO 层封装 JDBC 数据库 CRUD，获取连接、执行 SQL、把 ResultSet 封装成 Model；原生 JDBC DAO 最大缺点是无连接池、代码重复、事务难以控制，每次都新建数据库连接性能差。

## 核心原理

1. 通过`DBUtil`拿到 JDBC 数据库连接`Connection`
2. 编写 SQL，使用`PreparedStatement`预编译 SQL 语句
3. 给 SQL 占位符`?`赋值
4. 执行 SQL：
   - 查询：`executeQuery()` 返回`ResultSet`结果集
   - 增删改：`executeUpdate()` 返回受影响行数
5. 遍历`ResultSet`，把数据库每行数据封装成 Model 实体对象
6. 关闭资源：`ResultSet → PreparedStatement → Connection`
7. 将实体对象返回给上层 Servlet

> 职责分离： 
>
> Servlet：接收参数、业务判断、跳转页面
>
> DAO：只做数据库 CRUD，**不写 request、response、跳转**

## 书写模板

```
import com.model.User;
import com.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    // 查询：根据用户名密码查询用户
    public User findUserByUsernameAndPwd(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;
        try {
            // 1. 获取数据库连接
            conn = DBUtil.getConnection();
            // 2. 写SQL，使用?占位符（防止SQL注入）
            String sql = "select id,username from user where username=? and password=?";
            // 3. 创建预编译对象
            pstmt = conn.prepareStatement(sql);
            // 4. 给占位符赋值
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            // 5. 执行查询，得到结果集
            rs = pstmt.executeQuery();
            // 6. 遍历结果集，封装为Model
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
            }
        } catch (SQLException e) {
            // 捕获数据库异常
            e.printStackTrace();
        } finally {
            // 7. 关闭资源！顺序：rs → pstmt → conn
            DBUtil.close(rs, pstmt, conn);
        }
        return user;
    }
}
```

# ResultSet

> ResultSet 是 JDBC 查询返回的结果集，保存查询出来的多行数据；依靠`next()`移动游标逐行读取，把数据库记录封装成 Java 实体