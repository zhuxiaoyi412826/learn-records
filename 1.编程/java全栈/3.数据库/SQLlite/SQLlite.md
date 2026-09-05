# SQLite 完整学习路线 + 必备知识点 + Java 操作指南

**SQLite = 文件型数据库 + 标准 SQL**必备知识点：增删改查、数据类型、约束、事务、索引

## 一、SQLite 是什么？（先搞懂定位）

- **超轻量级嵌入式数据库**，无需安装、无需启动服务，直接以**文件形式**存储（.db 文件）
- 支持标准 SQL 语法，体积小、速度快
- 常用于：移动端（Android）、桌面软件、嵌入式设备、小型项目、测试环境

# 二、SQLite 学习路线（分 五 阶段）

## 阶段 1：环境搭建（10 分钟搞定）

1. 下载 

   SQLite 工具

   （推荐）

   - 官方：https://www.sqlite.org/download.html
   - 可视化工具：**DB Browser for SQLite**（最适合新手，免费图形界面）

   [Downloads - DB Browser for SQLite](https://sqlitebrowser.org/dl/)

2. 创建第一个数据库文件（test.db）

## 阶段 2：SQL 基础必学（核心）

这是所有数据库通用的基本功，SQLite 完全支持。

## 阶段 3：SQLite 独有特性（重点）

区别于 MySQL/Oracle 的知识点

## 阶段 4：Java 代码操作 SQLite（最终目标）

JDBC 连接 + 增删改查实战

## 阶段5：图书管理系统实战

# 三、SQLite 必备知识点（必须背下来）

## 1. 基础 SQL 命令（90% 场景都用这些）

```
-- 创建表
CREATE TABLE user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    age INTEGER,
    email TEXT UNIQUE
);

-- 插入
INSERT INTO user(name, age, email) VALUES('张三', 20, 'zhangsan@test.com');

-- 查询
SELECT * FROM user WHERE age > 18;

-- 更新
UPDATE user SET age=21 WHERE id=1;

-- 删除
DELETE FROM user WHERE id=1;

-- 排序/分页
SELECT * FROM user ORDER BY age DESC LIMIT 5;
```

## 2. SQLite 数据类型（独特！）

SQLite 是**动态类型**，比其他数据库简单：

- `NULL`：空值
- `INTEGER`：整数
- `REAL`：浮点数
- `TEXT`：字符串（最常用）
- `BLOB`：二进制（存文件 / 图片）

> 重点：你写 VARCHAR/INT 它也能识别，但最终会自动转成上面 5 种。

## 3. SQLite 高级必备知识点

1. **主键自增**：`INTEGER PRIMARY KEY AUTOINCREMENT`

2. **唯一约束**：`UNIQUE`

3. **非空约束**：`NOT NULL`

4. **外键**（默认关闭，需手动开启）

5. 事务：保证数据安全

   ```
   BEGIN TRANSACTION;
   -- 执行操作
   COMMIT;  -- 或 ROLLBACK 回滚
   ```

6. 索引：加速查询

   ```
   CREATE INDEX idx_user_name ON user(name);
   ```

## 4. 与 MySQL 的区别（面试常问）

- 无用户、无权限
- 无服务器进程，直接文件读写
- 不支持大量并发写入
- 不支持存储过程、触发器功能有限
- 适合**单应用、轻量、嵌入式**场景

## 5.CRUD

```
-- ===================== 1. 建表 =====================
DROP TABLE IF EXISTS student;

CREATE TABLE student (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    gender TEXT,
    age INTEGER,
    score REAL,
    address TEXT
);

-- ===================== 2. 插入多条数据 =====================
INSERT INTO student (name, gender, age, score, address)
VALUES
('张三', '男', 19, 88.5, '北京市'),
('李四', '女', 18, 92.0, '上海市'),
('王五', '男', 20, 76.0, '广州市'),
('赵六', '女', 19, 95.5, '深圳市');

-- ===================== 3. 查询操作 =====================
-- 查所有数据
SELECT * FROM student;

-- 指定字段查询
SELECT id, name, age, score FROM student;

-- 条件查询：年龄大于18
SELECT * FROM student WHERE age > 18;

-- 分数排序 降序
SELECT * FROM student ORDER BY score DESC;

-- 分页 只取前3条
SELECT * FROM student LIMIT 3;

-- ===================== 4. 修改更新 =====================
-- 把王五年龄改成21，分数改成80
UPDATE student 
SET age = 21, score = 80 
WHERE name = '王五';

-- 修改后查询看效果
SELECT * FROM student;

-- ===================== 5. 删除操作 =====================
-- 删除年龄等于18的学生
DELETE FROM student WHERE age = 18;

-- 删除后再次查询
SELECT * FROM student;
```

## 6.常用命令

| 命令                | 作用                         |
| ------------------- | ---------------------------- |
| `.open 数据库名.db` | 打开 / 创建新数据库          |
| `.tables`           | 查看当前数据库里的所有表     |
| `.schema 表名`      | 查看表的建表语句             |
| `.mode column`      | 让查询结果以列对齐的方式显示 |
| `.headers on`       | 显示查询结果的列名           |
| `.backup 备份名.db` | 备份当前数据库               |
| `.quit`             | 退出 SQLite 命令行           |

# 四、Java 操作 SQLite（最实用实战代码）

Java 使用 **JDBC** 连接 SQLite，非常简单！

## 1. 引入依赖（Maven）

```
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.45.1.0</version>
</dependency>
```

## 2. 核心步骤

1. 加载驱动
2. 建立连接（指向 .db 文件）
3. 创建 Statement / PreparedStatement
4. 执行 SQL
5. 关闭资源

## 3. 完整可运行代码

```
import java.sql.*;

public class SQLiteDemo {
    // 数据库地址（文件会自动创建）
    private static final String URL = "jdbc:sqlite:test.db";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // 1. 连接数据库
            conn = DriverManager.getConnection(URL);
            System.out.println("连接成功！");

            stmt = conn.createStatement();

            // 2. 创建表
            String createSql = """
                CREATE TABLE IF NOT EXISTS user (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    age INTEGER
                )
            """;
            stmt.execute(createSql);

            // 3. 插入数据
            String insertSql = "INSERT INTO user(name, age) VALUES('李四', 22)";
            stmt.executeUpdate(insertSql);

            // 4. 查询数据
            String querySql = "SELECT * FROM user";
            rs = stmt.executeQuery(querySql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                System.out.println(id + " " + name + " " + age);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
```

## 4. 推荐使用 PreparedStatement（防 SQL 注入）

```
String sql = "INSERT INTO user(name, age) VALUES(?, ?)";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setString(1, "王五");
pstmt.setInt(2, 25);
pstmt.executeUpdate();
```

# 五 项目实战

## 1.Java + SQLite 记事本项目实战

### 功能：

1. 新增记事本笔记

2. 查询所有笔记、按标题搜索

3. 修改笔记内容

4. 删除笔记

5. 数据持久化到 SQLite 

   ```
   note.db
   ```

   架构：MVC 分层

- 实体类 Note
- 数据库工具类 SQLiteDB
- DAO 数据访问层
- 主程序控制台交互

解决乱码问题

```
package exc.dao;

import exc.entiy.Note;
import exc.util.SQLiteDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 笔记 DAO 层：所有数据库增删改查
 */
public class NoteDAO {

    /**
     * 添加笔记
     */
    public int addNote(Note note) {
        Connection conn = SQLiteDB.getConnection();
        PreparedStatement pstmt = null;
        int rows = 0;
        String sql = "INSERT INTO note(title,content,createTime) VALUES(?,?,?)";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, note.getTitle());
            pstmt.setString(2, note.getContent());
            pstmt.setString(3, note.getCreateTime());
            rows = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLiteDB.close(conn, pstmt);
        }
        return rows;
    }

    /**
     * 删除笔记 by id
     */
    public int deleteNoteById(int id) {
        Connection conn = SQLiteDB.getConnection();
        PreparedStatement pstmt = null;
        int rows = 0;
        String sql = "DELETE FROM note WHERE id=?";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rows = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLiteDB.close(conn, pstmt);
        }
        return rows;
    }

    /**
     * 修改笔记
     */
    public int updateNote(Note note) {
        Connection conn = SQLiteDB.getConnection();
        PreparedStatement pstmt = null;
        int rows = 0;
        String sql = "UPDATE note SET title=?,content=? WHERE id=?";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, note.getTitle());
            pstmt.setString(2, note.getContent());
            pstmt.setInt(3, note.getId());
            rows = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLiteDB.close(conn, pstmt);
        }
        return rows;
    }

    /**
     * 根据ID查询单条笔记
     */
    public Note getNoteById(int id) {
        Connection conn = SQLiteDB.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Note note = null;
        String sql = "SELECT * FROM note WHERE id=?";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                note = new Note();
                note.setId(rs.getInt("id"));
                note.setTitle(rs.getString("title"));
                note.setContent(rs.getString("content"));
                note.setCreateTime(rs.getString("createTime"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLiteDB.close(conn, pstmt, rs);
        }
        return note;
    }

    /**
     * 查询所有笔记
     */
    public List<Note> listAllNote() {
        Connection conn = SQLiteDB.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Note> noteList = new ArrayList<>();
        String sql = "SELECT * FROM note ORDER BY id DESC";

        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Note note = new Note();
                note.setId(rs.getInt("id"));
                note.setTitle(rs.getString("title"));
                note.setContent(rs.getString("content"));
                note.setCreateTime(rs.getString("createTime"));
                noteList.add(note);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLiteDB.close(conn, pstmt, rs);
        }
        return noteList;
    }

    /**
     * 根据标题模糊搜索笔记
     */
    public List<Note> searchNoteByTitle(String keyword) {
        Connection conn = SQLiteDB.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Note> noteList = new ArrayList<>();
        String sql = "SELECT * FROM note WHERE title LIKE ? ORDER BY id DESC";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Note note = new Note();
                note.setId(rs.getInt("id"));
                note.setTitle(rs.getString("title"));
                note.setContent(rs.getString("content"));
                note.setCreateTime(rs.getString("createTime"));
                noteList.add(note);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLiteDB.close(conn, pstmt, rs);
        }
        return noteList;
    }
}
```

```
package exc.entiy;

/**
 * 记事本笔记实体类
 */
public class Note {
    private Integer id;
    private String title;
    private String content;
    private String createTime;

    public Note() {
    }

    public Note(String title, String content, String createTime) {
        this.title = title;
        this.content = content;
        this.createTime = createTime;
    }

    public Note(Integer id, String title, String content, String createTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createTime = createTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "【笔记ID】：" + id +
                "\n【标题】：" + title +
                "\n【内容】：" + content +
                "\n【创建时间】：" + createTime + "\n------------------------";
    }
}
```

```
package exc.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SQLite 数据库工具类
 * 负责获取连接、关闭资源
 */
public class SQLiteDB {
    // SQLite 数据库文件路径，自动生成 note.db
    private static final String URL = "jdbc:sqlite:note.db";

    // 静态加载驱动
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.err.println("数据库连接失败！");
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 关闭资源重载方法
     */
    public static void close(Connection conn, Statement stmt) {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化数据表：不存在则创建 note 表
     */
    public static void initTable() {
        Connection conn = getConnection();
        Statement stmt = null;
        String sql = "CREATE TABLE IF NOT EXISTS note (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "content TEXT," +
                "createTime TEXT" +
                ")";
        try {
            stmt = conn.createStatement();
            stmt.execute(sql);
            System.out.println("数据表初始化完成！");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, stmt);
        }
    }
}
```

```
package exc;

import exc.dao.NoteDAO;
import exc.entiy.Note;
import exc.util.SQLiteDB;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Java + SQLite 记事本 主程序
 * 控制台版本，可扩展 Swing 图形界面
 */
public class Main {
    public static void main(String[] args) {
        // 检查数据库文件是否存在
        File dbFile = new File("note.db");
        if (dbFile.exists()) {
            System.out.println("数据库文件已存在，直接加载...");
        } else {
            // 初始化数据库表
            SQLiteDB.initTable();
        }
        NoteDAO noteDAO = new NoteDAO();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("========== Java 记事本(SQLite版) ==========");
            System.out.println("1. 新增笔记");
            System.out.println("2. 查看所有笔记");
            System.out.println("3. 根据ID查看笔记");
            System.out.println("4. 搜索笔记(标题)");
            System.out.println("5. 修改笔记");
            System.out.println("6. 删除笔记");
            System.out.println("0. 退出系统");
            System.out.println("==========================================");
            System.out.print("请输入功能编号：");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("输入格式错误，请输入数字！");
                continue;
            }

            switch (choice) {
                case 1:
                    // 新增笔记
                    addNewNote(sc, noteDAO);
                    break;
                case 2:
                    // 查看所有
                    showAllNote(noteDAO);
                    break;
                case 3:
                    // 根据ID查询
                    showNoteById(sc, noteDAO);
                    break;
                case 4:
                    // 搜索笔记
                    searchNote(sc, noteDAO);
                    break;
                case 5:
                    // 修改笔记
                    editNote(sc, noteDAO);
                    break;
                case 6:
                    // 删除笔记
                    delNote(sc, noteDAO);
                    break;
                case 0:
                    System.out.println("退出记事本系统，再见！");
                    sc.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("编号不存在，请重新输入！");
            }
            System.out.println("\n按回车键继续...");
            sc.nextLine();
        }
    }

    /**
     * 新增笔记
     */
    private static void addNewNote(Scanner sc, NoteDAO noteDAO) {
        System.out.print("请输入笔记标题：");
        String title = sc.nextLine();
        System.out.print("请输入笔记内容：");
        String content = sc.nextLine();

        // 获取当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = sdf.format(new Date());

        Note note = new Note(title, content, nowTime);
        int rows = noteDAO.addNote(note);
        if (rows > 0) {
            System.out.println("✅ 笔记新增成功！");
        } else {
            System.out.println("❌ 笔记新增失败！");
        }
    }

    /**
     * 查看所有笔记
     */
    private static void showAllNote(NoteDAO noteDAO) {
        List<Note> noteList = noteDAO.listAllNote();
        if (noteList == null || noteList.isEmpty()) {
            System.out.println("暂无任何笔记数据！");
            return;
        }
        for (Note note : noteList) {
            System.out.println(note);
        }
    }

    /**
     * 根据ID查看单条笔记
     */
    private static void showNoteById(Scanner sc, NoteDAO noteDAO) {
        System.out.print("请输入要查询的笔记ID：");
        int id;
        try {
            id = Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
            System.out.println("ID必须是数字！");
            return;
        }
        Note note = noteDAO.getNoteById(id);
        if (note == null) {
            System.out.println("未找到该ID的笔记！");
        } else {
            System.out.println(note);
        }
    }

    /**
     * 标题模糊搜索
     */
    private static void searchNote(Scanner sc, NoteDAO noteDAO) {
        System.out.print("请输入要搜索的标题关键词：");
        String key = sc.nextLine();
        List<Note> list = noteDAO.searchNoteByTitle(key);
        if (list.isEmpty()) {
            System.out.println("未搜索到相关笔记！");
            return;
        }
        for (Note note : list) {
            System.out.println(note);
        }
    }

    /**
     * 修改笔记
     */
    private static void editNote(Scanner sc, NoteDAO noteDAO) {
        System.out.print("请输入要修改的笔记ID：");
        int id;
        try {
            id = Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
            System.out.println("ID格式错误！");
            return;
        }
        Note oldNote = noteDAO.getNoteById(id);
        if (oldNote == null) {
            System.out.println("不存在该笔记！");
            return;
        }
        System.out.println("原标题：" + oldNote.getTitle());
        System.out.print("请输入新标题：");
        String newTitle = sc.nextLine();
        System.out.println("原内容：" + oldNote.getContent());
        System.out.print("请输入新内容：");
        String newContent = sc.nextLine();

        Note newNote = new Note();
        newNote.setId(id);
        newNote.setTitle(newTitle);
        newNote.setContent(newContent);

        int rows = noteDAO.updateNote(newNote);
        if (rows > 0) {
            System.out.println("✅ 笔记修改成功！");
        } else {
            System.out.println("❌ 笔记修改失败！");
        }
    }

    /**
     * 删除笔记
     */
    private static void delNote(Scanner sc, NoteDAO noteDAO) {
        System.out.print("请输入要删除的笔记ID：");
        int id;
        try {
            id = Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
            System.out.println("ID必须为数字！");
            return;
        }
        Note note = noteDAO.getNoteById(id);
        if (note == null) {
            System.out.println("该笔记不存在！");
            return;
        }
        System.out.print("确定要删除这条笔记吗？(Y/N)：");
        String confirm = sc.nextLine();
        if ("Y".equalsIgnoreCase(confirm)) {
            int rows = noteDAO.deleteNoteById(id);
            if (rows > 0) {
                System.out.println("✅ 删除成功！");
            } else {
                System.out.println("❌ 删除失败！");
            }
        } else {
            System.out.println("已取消删除！");
        }
    }
}
```



## 2.增加前端

## 3.改造后端

## 4.前后端联调