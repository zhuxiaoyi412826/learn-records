# MySQL综合实战项目——企业员工管理系统

# 一、项目背景与需求

模拟中小型企业员工管理场景，实现员工信息、部门信息、工资信息、考勤信息的管理，满足以下核心需求：

- 1\. 部门管理：新增、查询、修改部门信息

- 2\. 员工管理：维护员工基本信息，关联所属部门

- 3\. 工资管理：记录员工月薪、奖金、扣款，统计薪资

- 4\. 考勤管理：记录员工打卡、请假情况

- 5\. 业务查询：满足多维度统计（部门人数、薪资排名、考勤达标率等）

项目核心：覆盖MySQL表设计、约束、多表联查、分组统计、窗口函数、索引优化等核心知识点，贴合面试实战手写场景。

# 二、数据库与表设计（核心重点）

遵循三大范式，设计4张核心表，合理设置主键、外键、索引，选择合适数据类型，避免冗余。

## 1\. 数据库创建

```sql
-- 创建数据库（指定编码，避免中文乱码）
CREATE DATABASE IF NOT EXISTS employee_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 切换数据库
USE employee_management;
```

## 2\. 四张核心表设计（带约束、索引）

### （1）部门表（dept）

```sql
CREATE TABLE IF NOT EXISTS dept (
    dept_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID（主键）',
    dept_name VARCHAR(50) NOT NULL UNIQUE COMMENT '部门名称（唯一，非空）',
    dept_location VARCHAR(100) DEFAULT '未知' COMMENT '部门地址',
    create_time DATETIME DEFAULT NOW() COMMENT '部门创建时间',
    -- 索引：部门名称（查询频繁）
    INDEX idx_dept_name (dept_name)
) COMMENT '部门表';
```

### （2）员工表（emp）

```sql
CREATE TABLE IF NOT EXISTS emp (
    emp_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '员工ID（主键）',
    emp_name VARCHAR(50) NOT NULL COMMENT '员工姓名（非空）',
    gender TINYINT UNSIGNED COMMENT '性别：0-女，1-男',
    age TINYINT UNSIGNED COMMENT '年龄',
    dept_id INT COMMENT '所属部门ID（外键，关联dept表）',
    hire_date DATE NOT NULL COMMENT '入职日期',
    phone VARCHAR(20) UNIQUE COMMENT '手机号（唯一）',
    email VARCHAR(50) COMMENT '邮箱',
    status TINYINT UNSIGNED DEFAULT 1 COMMENT '状态：0-离职，1-在职，2-请假',
    -- 外键约束（级联更新，删除时置空）
    FOREIGN KEY (dept_id) REFERENCES dept(dept_id) ON UPDATE CASCADE ON DELETE SET NULL,
    -- 索引：员工姓名、部门ID（多表联查频繁）
    INDEX idx_emp_name (emp_name),
    INDEX idx_emp_dept (dept_id)
) COMMENT '员工表';
```

### （3）工资表（salary）

```sql
CREATE TABLE IF NOT EXISTS salary (
    salary_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '工资记录ID（主键）',
    emp_id INT NOT NULL COMMENT '员工ID（外键，关联emp表）',
    basic_salary DECIMAL(10,2) NOT NULL COMMENT '基本工资（精准，用DECIMAL）',
    bonus DECIMAL(10,2) DEFAULT 0.00 COMMENT '奖金',
    deduction DECIMAL(10,2) DEFAULT 0.00 COMMENT '扣款（迟到、请假等）',
    pay_date DATE NOT NULL COMMENT '发薪日期',
    -- 外键约束
    FOREIGN KEY (emp_id) REFERENCES emp(emp_id) ON UPDATE CASCADE ON DELETE CASCADE,
    -- 索引：员工ID、发薪日期（薪资统计频繁）
    INDEX idx_salary_emp (emp_id),
    INDEX idx_salary_date (pay_date)
) COMMENT '工资表';
```

### （4）考勤表（attendance）

```sql
CREATE TABLE IF NOT EXISTS attendance (
    attend_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '考勤记录ID（主键）',
    emp_id INT NOT NULL COMMENT '员工ID（外键，关联emp表）',
    attend_date DATE NOT NULL COMMENT '考勤日期',
    sign_in TIME COMMENT '上班打卡时间',
    sign_out TIME COMMENT '下班打卡时间',
    attend_status TINYINT UNSIGNED DEFAULT 1 COMMENT '考勤状态：0-迟到，1-正常，2-早退，3-旷工',
    -- 外键约束
    FOREIGN KEY (emp_id) REFERENCES emp(emp_id) ON UPDATE CASCADE ON DELETE CASCADE,
    -- 索引：员工ID、考勤日期（考勤查询频繁）
    INDEX idx_attend_emp (emp_id),
    INDEX idx_attend_date (attend_date)
) COMMENT '考勤表';
```

# 三、测试数据插入（可直接复制运行）

插入测试数据，用于后续业务查询实战，模拟真实企业数据场景：

```sql
-- 1. 插入部门数据
INSERT INTO dept (dept_name, dept_location) VALUES
('研发部', '1号楼3层'),
('市场部', '2号楼1层'),
('人事部', '1号楼1层'),
('财务部', '2号楼2层');

-- 2. 插入员工数据
INSERT INTO emp (emp_name, gender, age, dept_id, hire_date, phone, email, status) VALUES
('张三', 1, 28, 1, '2021-03-15', '13800138001', 'zhangsan@163.com', 1),
('李四', 1, 32, 1, '2020-05-20', '13800138002', 'lisi@163.com', 1),
('王五', 0, 26, 2, '2022-01-10', '13800138003', 'wangwu@163.com', 1),
('赵六', 0, 30, 3, '2019-09-01', '13800138004', 'zhaoliu@163.com', 1),
('孙七', 1, 35, 4, '2018-07-05', '13800138005', 'sunqi@163.com', 1),
('周八', 1, 27, 1, '2022-06-18', '13800138006', 'zhouba@163.com', 2), -- 请假状态
('吴九', 0, 29, 2, '2021-08-22', '13800138007', 'wujia@163.com', 0); -- 离职状态

-- 3. 插入工资数据（2024年5月薪资）
INSERT INTO salary (emp_id, basic_salary, bonus, deduction, pay_date) VALUES
(1, 8000.00, 1500.00, 200.00, '2024-05-10'),
(2, 9500.00, 2000.00, 0.00, '2024-05-10'),
(3, 7500.00, 1000.00, 100.00, '2024-05-10'),
(4, 6000.00, 800.00, 0.00, '2024-05-10'),
(5, 10000.00, 2500.00, 300.00, '2024-05-10'),
(6, 7000.00, 0.00, 500.00, '2024-05-10'), -- 请假扣款
(7, 8500.00, 0.00, 0.00, '2024-05-10'); -- 离职，无奖金

-- 4. 插入考勤数据（2024年5月20-22日）
INSERT INTO attendance (emp_id, attend_date, sign_in, sign_out, attend_status) VALUES
(1, '2024-05-20', '09:00:00', '18:00:00', 1),
(1, '2024-05-21', '09:10:00', '18:00:00', 0), -- 迟到
(1, '2024-05-22', '09:00:00', '17:50:00', 2), -- 早退
(2, '2024-05-20', '08:50:00', '18:10:00', 1),
(2, '2024-05-21', '08:55:00', '18:05:00', 1),
(2, '2024-05-22', '09:00:00', '18:00:00', 1),
(3, '2024-05-20', '09:05:00', '18:00:00', 0), -- 迟到
(3, '2024-05-21', '08:58:00', '18:00:00', 1),
(3, '2024-05-22', '00:00:00', '00:00:00', 3), -- 旷工
(4, '2024-05-20', '09:00:00', '18:00:00', 1),
(5, '2024-05-20', '08:45:00', '18:15:00', 1);
```

# 四、核心业务SQL实战（面试高频手写）

以下SQL覆盖多表联查、分组统计、窗口函数、模糊查询、索引使用等核心知识点，全部可直接运行，贴合真实面试场景。

## 1\. 基础查询（入门必练）

```sql
-- 1. 查询所有在职员工的姓名、性别、部门名称（多表联查）
SELECT e.emp_name, 
       CASE e.gender WHEN 0 THEN '女' WHEN 1 THEN '男' ELSE '未知' END AS gender,
       d.dept_name
FROM emp e
LEFT JOIN dept d ON e.dept_id = d.dept_id
WHERE e.status = 1;

-- 2. 模糊查询：查询姓名包含“张”或“李”的员工信息
SELECT * FROM emp WHERE emp_name LIKE '%张%' OR emp_name LIKE '%李%';

-- 3. 查询2021年及以后入职的员工，按入职日期升序排序
SELECT emp_name, hire_date, dept_id FROM emp 
WHERE hire_date >= '2021-01-01' 
ORDER BY hire_date ASC;
```

## 2\. 分组统计（工作/面试高频）

```sql
-- 1. 统计每个部门的在职员工人数、平均年龄
SELECT d.dept_name, 
       COUNT(e.emp_id) AS emp_count,
       ROUND(AVG(e.age), 1) AS avg_age
FROM dept d
LEFT JOIN emp e ON d.dept_id = e.dept_id AND e.status = 1 -- 只统计在职
GROUP BY d.dept_id, d.dept_name
ORDER BY emp_count DESC;

-- 2. 统计2024年5月各员工的实发工资（实发=基本工资+奖金-扣款），并按实发工资降序
SELECT e.emp_name,
       s.basic_salary,
       s.bonus,
       s.deduction,
       (s.basic_salary + s.bonus - s.deduction) AS real_salary
FROM emp e
JOIN salary s ON e.emp_id = s.emp_id
WHERE s.pay_date = '2024-05-10'
ORDER BY real_salary DESC;

-- 3. 统计各部门2024年5月的平均实发工资，只显示平均工资大于8000的部门
SELECT d.dept_name,
       ROUND(AVG(s.basic_salary + s.bonus - s.deduction), 2) AS avg_real_salary
FROM dept d
JOIN emp e ON d.dept_id = e.dept_id
JOIN salary s ON e.emp_id = s.emp_id
WHERE s.pay_date = '2024-05-10'
GROUP BY d.dept_id, d.dept_name
HAVING avg_real_salary > 8000;
```

## 3\. 窗口函数实战（面试必问）

```sql
-- 1. 按部门分组，给员工的实发工资排名（1-3名），显示排名、姓名、部门、实发工资
SELECT 
    d.dept_name,
    e.emp_name,
    (s.basic_salary + s.bonus - s.deduction) AS real_salary,
    ROW_NUMBER() OVER(PARTITION BY d.dept_id ORDER BY (s.basic_salary + s.bonus - s.deduction) DESC) AS sal_rank
FROM dept d
JOIN emp e ON d.dept_id = e.dept_id
JOIN salary s ON e.emp_id = s.emp_id
WHERE s.pay_date = '2024-05-10'
HAVING sal_rank <= 3;

-- 2. 统计每个员工的考勤达标率（达标=正常考勤，统计2024年5月）
SELECT 
    e.emp_name,
    COUNT(a.attend_id) AS total_days, -- 总考勤天数
    SUM(CASE WHEN a.attend_status = 1 THEN 1 ELSE 0 END) AS normal_days, -- 正常天数
    ROUND(SUM(CASE WHEN a.attend_status = 1 THEN 1 ELSE 0 END) / COUNT(a.attend_id) * 100, 2) AS attend_rate
FROM emp e
JOIN attendance a ON e.emp_id = a.emp_id
WHERE a.attend_date BETWEEN '2024-05-01' AND '2024-05-31'
GROUP BY e.emp_id, e.emp_name;
```

## 4\. 复杂业务查询（综合能力）

```sql
-- 1. 查询2024年5月实发工资最高的员工，显示姓名、部门、工资明细、考勤达标率
SELECT 
    e.emp_name,
    d.dept_name,
    s.basic_salary,
    s.bonus,
    s.deduction,
    (s.basic_salary + s.bonus - s.deduction) AS real_salary,
    ROUND(SUM(CASE WHEN a.attend_status = 1 THEN 1 ELSE 0 END) / COUNT(a.attend_id) * 100, 2) AS attend_rate
FROM emp e
JOIN dept d ON e.dept_id = d.dept_id
JOIN salary s ON e.emp_id = s.emp_id
JOIN attendance a ON e.emp_id = a.emp_id
WHERE s.pay_date = '2024-05-10' 
  AND a.attend_date BETWEEN '2024-05-01' AND '2024-05-31'
GROUP BY e.emp_id, e.emp_name, d.dept_name, s.basic_salary, s.bonus, s.deduction
HAVING real_salary = (SELECT MAX(basic_salary + bonus - deduction) FROM salary WHERE pay_date = '2024-05-10');

-- 2. 查询离职员工的姓名、离职前薪资、最后考勤日期
SELECT 
    e.emp_name,
    s.basic_salary + s.bonus - s.deduction AS last_salary,
    MAX(a.attend_date) AS last_attend_date
FROM emp e
JOIN salary s ON e.emp_id = s.emp_id
LEFT JOIN attendance a ON e.emp_id = a.emp_id
WHERE e.status = 0
GROUP BY e.emp_id, e.emp_name, last_salary;
```

## 5\. 索引优化实战（面试重点）

```sql
-- 1. 查看索引使用情况（explain分析）
EXPLAIN SELECT e.emp_name, d.dept_name 
FROM emp e 
LEFT JOIN dept d ON e.dept_id = d.dept_id 
WHERE e.status = 1 AND e.emp_name LIKE '张%'; -- 走idx_emp_name索引

-- 2. 优化模糊查询（避免%开头，走索引）
-- 优化前（索引失效）
EXPLAIN SELECT * FROM emp WHERE emp_name LIKE '%张%';
-- 优化后（走索引，适合“前缀匹配”场景）
EXPLAIN SELECT * FROM emp WHERE emp_name LIKE '张%';

-- 3. 给频繁查询的字段添加索引（比如考勤表的attend_status）
ALTER TABLE attendance ADD INDEX idx_attend_status (attend_status);
-- 查看添加后的索引
SHOW INDEX FROM attendance;
```

# 五、项目拓展（面试加分项）

## 1\. 事务实战（模拟发薪场景）

```sql
-- 模拟给员工发薪，同时更新员工状态（原子操作，要么都成功，要么都失败）
START TRANSACTION;
-- 1. 插入薪资记录
INSERT INTO salary (emp_id, basic_salary, bonus, deduction, pay_date) 
VALUES (1, 8000.00, 1800.00, 100.00, '2024-06-10');
-- 2. 更新员工状态（假设发薪后状态不变，这里模拟更新）
UPDATE emp SET status = 1 WHERE emp_id = 1;
-- 提交事务（成功）
COMMIT;
-- 若出错，回滚事务
-- ROLLBACK;
```

## 2\. 存储过程（批量插入考勤数据）

```sql
-- 创建存储过程：批量插入指定员工、指定日期范围的考勤记录
DELIMITER //
CREATE PROCEDURE batch_insert_attendance(IN emp_id INT, IN start_date DATE, IN end_date DATE)
BEGIN
    DECLARE current_date DATE DEFAULT start_date;
    WHILE current_date <= end_date DO
        INSERT INTO attendance (emp_id, attend_date, sign_in, sign_out, attend_status)
        VALUES (emp_id, current_date, '09:00:00', '18:00:00', 1); -- 默认正常考勤
        SET current_date = DATE_ADD(current_date, INTERVAL 1 DAY);
    END WHILE;
END //
DELIMITER ;

-- 调用存储过程：给员工ID=1，插入2024-05-23到2024-05-31的考勤
CALL batch_insert_attendance(1, '2024-05-23', '2024-05-31');
```

# 六、面试关联考点（实战对应面试题）

结合本项目，整理高频面试题，直接背即可：

1. Q：你设计的员工表，为什么用INT作为emp\_id，不用VARCHAR？A：INT占用空间小（4字节），支持自增，查询效率高；VARCHAR作为主键，排序、查询速度慢，且不易维护。

2. Q：工资表为什么用DECIMAL存储薪资，不用FLOAT？A：FLOAT是浮点数，会有精度丢失；薪资是金融数据，需精准，DECIMAL可自定义精度，无精度丢失。

3. Q：多表联查时，为什么给dept\_id、emp\_name加索引？A：这些字段是联查条件、查询条件，加索引能提升查询速度，避免全表扫描。

4. Q：写出一条本项目中的多表联查SQL，并说明用了哪种连接方式，为什么？A：如查询员工\+部门信息，用LEFT JOIN，因为要显示所有员工（即使无部门），符合业务需求。

5. Q：如何优化“查询姓名包含‘张’的员工”这个SQL？A：避免用LIKE \&\#39;%张%\&\#39;（索引失效），若业务允许，用LIKE \&\#39;张%\&\#39;（走索引）；若必须模糊匹配中间，可考虑全文索引。

6. Q：用窗口函数实现“每个部门工资前3名”，写出SQL（直接用项目中的SQL）。

。

