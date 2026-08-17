官网下载 https://www.python.org/downloads/

python 查看版本号

print(100) 输出100

PyCharm  PyCharm 官方地址：https://www.jetbrains.com/pycharm/download

供程序开发环境的应用程序，一般包括代码编辑器、编译器、调试器和图形用户界面等工具。集成了代码编写功能、分析功能、编译功能、调试功能等多种功能

安装python  历史版本

3.13下载链接

https://www.python.org/ftp/python/3.13.14/python-3.13.14-amd64.exe

默认安装路径

C:\Users\<你的用户名>\AppData\Local\Programs\Python\Python313\



安装pygaem

```
py -3.13 -m pip install pygame -i https://pypi.tuna.tsinghua.edu.cn/simple
```

测试是否安装成功

py -3.13 -m pygame --version

使用pip

py -3.13 -m pip



快捷键

| **Ctrl + /**       | 行注释（可选中多行）           |
| ------------------ | ------------------------------ |
| **Ctrl + Alt + L** | 代码格式化                     |
| **Ctrl + C**       | 复制当前行  /  复制选定的代码  |
| **Ctrl + D**       | 重复当前行  /  重复选定的代码  |
| **Ctrl + Z**       | 撤销                           |
| **Ctrl + Y**       | 删除当前行   /   反撤销(重做)  |
| **Ctrl + X**       | 复制当前行  /  剪切选定的代码  |
| **Shift + Enter**  | 换行（光标不在结尾处也可换行） |



运行python 三种方式 

- 第一种方式：命令行（终端）模式            命令行下输入 
- 第二种方式：脚本模式                                python test.py
- 第三种方式：集成开发环境（IDE）模式









1. 

`

# 基础

## 字符串 

字符串必须要放到引号中，使用：单引号、双引号、三个单引号、三个双引号都可以，但必须是英文的引号。

文档字符串的主要作用是：对当前 Python 文件进行说明，且文档字符串必须用三个双引号。

```
"""这是我写的第一个Python文件"""

'张三'
18
65.2
```

## 变量与常量

```
ques1, ans1 = 'Python中用于输出的函数是？', 'print'
ques2, ans2 = 'Python中用于表示逻辑“并且”的关键字是？', 'and'
ques3, ans3 = 'Python属于编译型还是解释型？', '解释型'
```

```
这是 Python 特有的解包赋值，语法完全合法且常用。
一、原理
变量1, 变量2 = 值1, 值2会按位置一一对应赋值，左右两边元素数量必须一致。
```



```
print('张三的体重是', 65.2)
print('对于', 65.2, '这个体重，张三觉得不满意')
print('张三决定开始减肥，希望体重比', 65.2, '还要小')
```

print 把内容打印到控制台上

语法 变量后面跟，号

```
name = '张三'
age = 18
weight = 65.2
print(name, '的体重是', weight)
print('对于', weight, '这个体重，',name, '不满意')
print(name,'决定开始减肥，希望体重比',weight,'还要小')
```

关键字

```
False     	None      	True      	and       	as
assert    	async     	await     	break     	class
continue  	def       	del       	elif      	else
except    	finally   	for       	from      	global
if        	import    	in        	is        	lambda
nonlocal  	not       	or        	pass      	raise
return    	try       	while     	with      	yield
```

标识符命名规则

```
1. 只能包含：数字、字母、下划线，且不能以数字开头，不能包含空格。
2. 区分大小写，即Name和name是两个不同的标识符。
3. 不能使用关键字（关键字的解释在下面⬇️）。
4. 标识符尽量不要与内置函数同名。
5. 标识符虽然没有长度限制，但应追求：简洁清晰，具有描述性。
```

常量

```
在程序中一旦被赋值，就不希望被修改的量（区别于变量）。
```

```
ADULT_AGE = 18
MONTHS_IN_YEAR = 12
MAX_USERS = 1200
PASSING_SCORE = 60
MAX_USERS = 1300
```

但是python中没有约束常量的机制 也能改但是一般不改

```
MONTHS_IN_YEAR = 12
print(MONTHS_IN_YEAR)

MONTHS_IN_YEAR = 13
print(MONTHS_IN_YEAR)
```

## 注释

```
# 单行注释
多行注释
"""
我是一些注释
我还是一些注释
"""
Python 中并没有真正的多行注释语法，所谓多行注释的本质其实还是字符串

文件编码又称“字符编码”，文件编码注释写在 Python 文件的首行，是一种特殊的注释。
它的作用是：指定当前文件的字符编码。
```

## 字符编码

- - 存储数据时，计算机会进行**编码**。
  - 读取数据时，计算机会进行**解码**。

- python 默认utf-8 编码

1. `ASCII`：大写字母、小写字母、数字、一些符号，共计 28个字符。

2. `ISO 8859-1`：在`ASCII`基础上扩展，支持西欧语言，共计 256 个字符。

3. `GB2312`：中国国家编码标准，收录约 6763 个简体中文常用汉字和符号。

4. `GBK`：兼容`GB2312`，进一步扩展，支持简繁体中文和其他汉字，共收录 2 万多个字符。

5. `**UTF-8**`：国际通用的编码格式，也叫“**万国码**”，支持世界所有语言的字符，包括：中文、英文、阿拉伯文、日文、韩文等，向下兼容`ASCII`，是现代互联网最常用的编码格式。

## 数据类型

| 数据类型 | 英文标识   | 描述                                   | 示例                                   | 可变 / 不可变 |
| -------- | ---------- | -------------------------------------- | -------------------------------------- | ------------- |
| 整数     | `int`      | 正负整数、零，无大小限制               | `10`、`-20`、`0`                       | 不可变        |
| 浮点数   | `float`    | 小数、科学计数法数值                   | `3.14`、`-0.5`、`1e3`                  | 不可变        |
| 布尔型   | `bool`     | 逻辑值，只有真 / 假                    | `True`、`False`                        | 不可变        |
| 字符串   | `str`      | 文本内容，单 / 双 / 三引号包裹         | `"python"`、`'123'`、`"""多行文本"""`  | 不可变        |
| 列表     | `list`     | 有序可变序列，元素可重复、类型不限     | `[1, 2, "a", True]`                    | **可变**      |
| 元组     | `tuple`    | 有序不可变序列，写法 `()`              | `(1, 2, 3)`、`(99,)`（单元素必加逗号） | 不可变        |
| 字典     | `dict`     | 键值对集合，键唯一、无序（3.7 + 有序） | `{"name":"Tom", "age":18}`             | **可变**      |
| 集合     | `set`      | 无序、元素唯一，常用于去重             | `{1, 2, 3}`                            | **可变**      |
| 空类型   | `NoneType` | 空值，表示无内容                       | `None`                                 | 不可变        |

type()`可以查看数据类型，`type()`会返回当前数据的具体类型

```
name = '张三'
age = 18
weight = 72.5

# 使用变量接收type()返回的类型
result1 = type(name)
result2 = type(age)
result3 = type(weight)

# 打印这三个数据类型
print(result1)  # <class 'str'>
print(result2)  # <class 'str'>
print(result3)  # <class 'float'>
# 直接打印也可以
print(type(name))
print(type(age))
print(type(weight))
```

**在 Python 中：变量无类型，数据有类型。**

例如`a = 10`，其中`a`是没有类型的，但`a`所关联的数据`10`是有类型的，`10`是整型，我们经常说`a`是整型，其实是一种不太严谨的表述，严谨的表述应该是：`a`所对应的数据`10`是整型。

### 整形

1️⃣**什么是整型？**

所谓整型就是没有小数点的数字， Python 中的整型，可以是**任意大小**的整数，包括负整数。



2️⃣**分隔符**

当书写很大的数时，可使用下划线将数字分组，使其更清晰易读；Python 自动忽略数字之间的下划线，并且这种写法也适用于浮点数，但要注意：此种写法只有 Python3.6 及以上版本才支持。

```
num1 = 10_000_000
print(num1)
```

3️⃣**整型上限值**

Python 中存储整数上限值的大小取决于：计算机的内存和处理能力，我们先来认识一下『幂运算符』，代码如下：

```
a = 3 ** 2  # 表示3的平方
b = 2 ** 3  # 表示2的3次方

print(a)  # 9
print(b)  # 8
```

调用`print(a)`时，Python 底层会把`a`的类型转换成『字符串类型』再输出，而从 Python3.11 起，Python 对超大整数转换字符串的长度进行了限制，默认位数是`4300`位。

### **浮点型**

1️⃣**什么是浮点型？**

所谓浮点型，就是带小数点的数字，比如：`3.14`、`-0.5`、`2.0`都是浮点数。



1 直接写

2 通过科学计算法

```
# 浮点型的科学计数法表示。
speed_of_sound = 3.4e+2  # 3.4乘以10的2次方。
world_population = 7.8e9  # 7.8乘以10的9次方。
distance_sun_earth = 1.496E8  # 1.496乘以10的8次方。
speed_of_light = 2.998E+8  # 2.998乘以10的8次方。

one_ml = 1e-3  # 1乘以10的-3次方。
one_mg = 1E-3  # 1乘以10的-3次方。
```

### **字符串**

1️⃣**字符的**四种定义方式

| 写法       | 示例                  | 适用场景                                       |
| ---------- | --------------------- | ---------------------------------------------- |
| 单引号     | `'你好，尚硅谷'`      | 单行字符串（不能直接换行，换行需要使用圆括号） |
| 双引号     | `"你好，尚硅谷"`      |                                                |
| 三个单引号 | `'''你好，尚硅谷'''`  | 多行字符串（可以直接换行）                     |
| 三个双引号 | `"""你好，尚硅谷""""` |                                                |

```
# 单引号和双引号的写法是等价的，二者都不能直接换行（要用圆括号才能换行），单引号用的多。
message1 = '尚硅谷，让天下没有难学的技术!'
message2 = "尚硅谷，让天下没有难学的技术!"

# 三个单引号的写法，可以直接换行，并且可以作为多行注释使用。
message3 = '''尚硅谷，让天下没有难学的技术!'''

# 三个双引号的写法，可以直接换行，也可以作为多行注释使用，还能作为文档字符串使用。
message4 = """尚硅谷，让天下没有难学的技术!"""
```

**2️⃣**字符串的格式化输出

格式化输出

1 用+号拼接

```
name = '张三'
gender = '男'
weight = 65.2
age = 12

info1 = '我叫' + name + '，我是' + gender + '生'
```

2 使用占位符

- `%s`占位字符串
- `%f`占位浮点数 默认是占位6位  %.1f 占位1位
- `%i`占位整数
- `%d`占位十进制的整数
- `%s`是万能的（如果我们提供的数据不是字符串，那 Python 就会把数据转成字符串）

```
name = '张三'
gender = '男'
weight = 65.2
age = 12

info2 = '我叫%s，我是%s生，我体重是%f，年龄是%d' % (name, gender, weight, age)
```

3 使用 f-string，这是目前 Python 最推荐的方式

```
name = '张三'
gender = '男'
weight = 65.2
age = 12

info3 = f'我叫{name}，我是{gender}生，我体重是{weight}，年龄是{age}'
```

4 转义字符

| 转义字符 | 表示的含义                                 |
| -------- | ------------------------------------------ |
| `\'`     | `'`                                        |
| `\"`     | `"`                                        |
| `\n`     | 换行                                       |
| `\\`     | `\`                                        |
| `\b`     | 删除前一个字符                             |
| `\r`     | 使光标回到本行开头，覆盖输出               |
| `\t`     | 表示水平制表符（让光标跳转到下一个制表位） |

```
# 使用 \' 输出 '
print('在Python中，可以使用\'包裹一个字符串')

# 使用 \" 输出 "
print("在Python中，可以使用\"包裹一个字符串")

# 使用 \n 进行换行
print('注册会员需要以下信息：\n姓名\n年龄\n手机号')

# 使用 \\ 输出 \
print('D:\\nice')

# 使用 \b 删除前一个字符
print('helloo\b')

# 使用 \r 使光标回到本行开头，覆盖输出
print('67%\r68%')

# 使用 \t 表示水平制表符（让光标跳转到下一个制表位）
# 一个制表位到底是几位，是不确定的，但我们可以通过在字符串后面加.expandtabs()来指定位数。
print('1234123412341234')
print('ab\tcd.expandtabs(4)')
print('abc\td.expandtabs(4)')
print('abcd\ta.expandtabs(4)')
print('我是\t中文.expandtabs(4)')

print('12341234123412341234')
print('姓名\t性别\t年龄')
print('张三\t男\t\t18')
print('李四\t女\t\t25')
print('王五\t男\t\t32')
```

### 布尔类型

但布尔类型的具体值，只有两个，分别是：`True`和`False`，其中：`True`表示真，`False`表示假。

![img](https://cdn.nlark.com/yuque/0/2025/png/35780599/1760689122751-f2e8c455-920c-464c-8e3e-aaed34394f22.png)

布尔值常用于表示：条件是否成立、事件是否发生、操作是否成功、等逻辑状态。

📢**注意：**`True` 和 `False` 的首字母必须大写。  

```
# 自己定义的布尔值
a = True
b = False

# 靠程序执行得到的布尔值
c = 5 > 3
d = 7 < 2

print(type(a), a)  # True
print(type(b), b)  # Flase
print(type(c), c)  # True
print(type(d), d)  # Flase
```

布尔类型是`int`类型的子类型，底层的本质是用`1`表示`True`，用`0`表示`False`。

```
# 布尔类型是int类型的子类型，底层的本质是用1表示True，用0表示False
print(int(True))   # 1
print(int(False))  # 0

print(4 + True)   # 5
print(8 - False)  # 8

print(True + True)   # 2
print(True - False)  # 1

print(7 > True)    # True
print(False <= 0)  # True
```

Python中除`0`以外的任何数，转为布尔值后都为 True

Python中除空字符串以外的任何字符串，转为布尔值都是 True



### 数据类型转换

```
何为数据类型转换？—— 把一种类型的数据，变成另一种类型。
```

## 运算符

算术运算符

![image-20260612102135028](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20260612102135028.png)



![image-20260612102103582](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20260612102103582.png)

## 进制

**二进制**：以`0b`或`0B`开头表示。

**八进制**：以`0o`开头表示

**十进制**：无需前缀，正常编写即可。

**十六进制**：以`0x`或`0X`开头表示，此处的`A-F`不区分大小写。

**：**Python 中所有的『非十进制』数字，只是代码层面的编写方式，只是给程序员看的，Python 在进行：计算、打印等操作时，会自动将这些『非十进制』数字，转为『十进制』数字

## 输入语句

在 Python 中，输入语句用于：从键盘接收用户输入的内容

```
# 使用input()获取用户的输入
name = input('请输入你的姓名：')
age = input('请输入你的年龄：')

# input()获取到的内容全都是字符串类型
print(type(age))
```

# 流程控制语句

程序的执行流程大体上可分为三类：**顺序**、**分之**、**循环**。

## 分支

单分支

```
# 接收键盘输入，转为整数类型，赋值给变量 age
age = int(input('请输入你的年龄：'))

# 判断年龄是否大于等于18
if age >= 18:
    # 条件成立时，执行这两行缩进代码
    print('你是成年人')
    print('成年人的世界，虽不容易，但很精彩！')

# 无缩进，不属于if代码块，无论条件是否成立都会执行
print('欢迎你来学习Python！')
```

双分支

```
age = int(input('请输入你的年龄：'))
if age >= 18:
    print('你是成年人')
    print('成年人的世界，虽不容易，但很精彩！')
else:
    print('你是未成年人')
    print('好好加油，努力学习，未来可期！')
print('欢迎你来学习Python！')
```

多分支

if 判断条件1:
    条件1【成立】时执行的代码
elif 判断条件2:
    条件2【成立】时执行的代码
elif 判断条件3:
    条件3【成立】时执行的代码
else:  # else如不需要可以省略
    上述所有条件都不成立时执行的代码

```
# 根据年龄来判断处于人生哪个阶段。
age = int(input('请输入你的年龄：'))
if age <= 10:
    print('你是幼儿')
elif age <= 18:
    print('你是青少年')
elif age <= 30:
    print('你是青年')
elif age <= 50:
    print('你是中年')
elif age <= 60:
    print('你是中老年')
else:
    print('你是老年')
```

1. 一个`if`语句只能匹配`1`个`else`语句，但可以匹配多个`elif`语句，并且`else`语句要在所有的`elif`语句之后。
2. 一旦某个分支语句检测为`true`，其他的`elif`以及`else`语句都将不再执行。

嵌套分支

```
if 判断条件1:
    # 条件1 成立时执行的代码1
    # 条件1 成立时执行的代码2
    # ......
    if 判断条件2:
        # 条件2 成立时执行的代码1
        # 条件2 成立时执行的代码2
        # ......
    elif 判断条件3:
        # 条件3 成立时执行的代码
        # ......
    else:
        # 条件2、条件3 都不成立时执行的代码1
        # 条件2、条件3 都不成立时执行的代码2
        # ......
else:
    # 条件1 不成立时执行的代码1
    # 条件1 不成立时执行的代码2
    # ......
    if 判断条件4:
        # 条件4 成立时执行的代码
        # ......
    else:
        # 条件4 不成立时执行的代码
        # ......
```



```
age = int(input('请输入你的年龄：'))
has_report = input('您是否提交了体检报告？（是/否）')
level = int(input('请输入你的会员等级（1/2/3）'))

print('******⬇️程序的识别结果如下⬇️：******')
if 18 <= age <= 45:
    print('✅️您的年龄符合比赛要求！')
    if has_report == '是':
        print('✅️您已提交体检报告！')
        print('✅️您可以参加比赛！')
        if level == 1:
            print(f'😊尊敬的{level}会员，比赛结束后，您可以领取纪念T恤👕一件！')
        elif level == 2:
            print(f'😊尊敬的{level}会员，比赛结束后，您可以领取专业跑鞋👟一双！')
        elif level == 3:
            print(f'😊尊敬的{level}会员，比赛结束后，您可以领取运动耳机🎧️一副！')
        else:
            print('❌您输入的会员等级不正确！')
    elif has_report == '否':
        print('❌您未提交体检报告，不能参加比赛！')
    else:
        print('❌您输入的体检报告有误！')
else:
    print('❌抱歉，参赛年龄需要在18~45之间！')
```

## 循环

循环是一种让代码“重复执行”的机制，当某个条件成立时，程序会反复执行一些语句，直到条件不再满足时，再停止运行

```
 : 是 Python 语法标记，作用：表示接下来缩进的代码，都属于这个 for 循环内部。
简单理解
循环语句（for/if/while/def 等）末尾必须加 :
看到 :，解释器就知道：下面缩进的代码块，是循环体
搭配缩进，区分哪部分代码要循环执
```



### while循环

```
while 循环条件:
    条件成立时执行的操作1
    条件成立时执行的操作2
```

```
1. 先判断循环条件是否成立（是否为 True）
2. 如果成立 → 执行循环中的代码
3. 执行完循环体 → 再次判断循环条件
4. 若仍成立 → 继续执行循环中的代码
5. 若不成立 → 循环结束  
```

```
n = 1
while n <= 10:
    print(f'第{n}次你好啊')
    n += 1
print(f'我是while循环以外的代码，执行到这里时，循环已经结束了，此时的n是：{n}')
```

### for循序

```
for 临时变量 in 可迭代对象：
    要执行的操作1
    要执行的操作2
```

1. 从可迭代对象中取出第一个元素 → 赋值给临时变量
2. 执行循环中的代码
3. 取出下一个元素 → 重复执行
4. 当所有元素取完后 → 循环结束 

```
range(1,11) 详解
作用：生成整数序列，Python 里是左闭右开区间。
含义：从 1 开始，到 10 结束（取不到 11），步长默认为 1。
等价序列：1, 2, 3, 4, 5, 6, 7, 8, 9, 10
```



```
# 使用for循环遍历range()所指定的数字范围
n = 0
for n in range(1, 11):
    print(f'第{n}次你好啊')
print(f'我是for循环以外的代码，执行到这里时，循环已经结束了，此时的n是：{n}')

# 使用for循环遍历字符串
for m in 'abcdef':
    print(m)

# 演示由于误操作造成的死循环（下面代码中，用到了列表，我们后面会讲解）
# 备注：for循环还能遍历很多我们没有讲到的东西，比如：元组、列表、对象......
nums = [1,2,3]
for i in nums:
    # nums.append(4) # 此行代码会造成死循环
    print(i)
```

![](https://cdn.nlark.com/yuque/0/2025/png/35780599/1760708051227-3de1c2d0-a4d3-4048-9e64-21cac91a5a9c.png)

```
# 加密代码
text = input('📝请输入要加密的文字：')
secret = ''
for t in text:
    secret += chr(ord(t) + 1)
#     取出变量 t 里的单个字符 → 转成 ASCII 码 → 数值 +1 → 再转回字符 → 拼接到 secret 后面。
print(f'㊙️经过加密后的内容为：{secret}')

# 解密代码
secret = input('📝请输入要解密的文字：')
text = ''
for s in secret:
    text += chr(ord(s) - 1)
#     ord(s)：将字符s转为对应的 ASCII 数值   ord(s) - 1：数值减 1  chr(...)：把减完的数值转回字符 text +...：将新字符追加到字符串text末尾
print(f'📃经过解密后的内容为：{text}')
```

### while和for的区别

![image-20260612110021806](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20260612110021806.png)



### 嵌套循环

```
# for循环实现
day = 1
for day in range(1,31):
    print(f'********📅第{day}天********')
    for group in range(1,4):
        print(f'💪这是第{group}组仰卧起坐')
    print(f'✅第{day}天任务已完成！明天继续！\n')
print(f'🎉为期{day}天的健身计划完成，我的腹肌在闪闪发光！')
```

```
# while循环实现
day = 1
while day <= 30:
    print(f'********📅第{day}天********')
    group = 1
    while group <= 3:
        print(f'💪这是第{group}组仰卧起坐')
        group += 1
    print(f'✅第{day}天任务已完成！明天继续！\n')
    day += 1
print(f'🎉为期{day - 1}天的健身计划完成，我的腹肌在闪闪发光！')
```



**九九乘法表**

```
# for循环实现九九乘法表
for row in range(1, 10):
    for item in range(1, row + 1):
        print(f'{item}*{row}={item * row}', end='\t')
    print()
```

### 退出循环

`break`：**彻底结束循环**

`continue`：**只跳过当前这一轮**，直接进入下一轮

## 综合案例

```
print('🏆欢迎来到：答题闯关挑战赛（输入q可随时退出）\n')

# 题目与答案
ques1, ans1 = 'Python中用于输出的函数是？', 'print'
ques2, ans2 = 'Python中用于表示逻辑“并且”的关键字是？', 'and'
ques3, ans3 = 'Python属于编译型还是解释型？', '解释型'

# 最多可尝试次数
max_tries = 3
# 总关卡数
total_levels = 3
# 是否处于可游戏状态
is_playing = True

# 根据题目数量开始循环
for level in range(1, total_levels + 1):
    # 打印当前是第几关
    print(f'********🎯第{level}关********')
    # 取出当前关卡所对应的题目和答案
    if level == 1:
        question, answer = ques1, ans1
    elif level == 2:
        question, answer = ques2, ans2
    else:
        question, answer = ques3, ans3
    # 记录当前关卡的尝试次数
    tries = 1
    # 若已经尝试的次数，小于等于最大尝试次数，则进入循环
    while tries <= max_tries:
        # 向用户提问
        user_input = input('📢'+question)
        # 根据用户的输入，来决定做什么
        if user_input == answer:
            print('✅回答正确！\n')
            break
        elif user_input == '':
            print('⚠️您的输入为空，请重新作答！\n')
            continue
        elif user_input == 'q':
            print('👋您已退出游戏！\n')
            is_playing = False
            break
        else:
            # 计算剩余次数
            leave = max_tries - tries
            # 判断是否还有剩余次数
            if leave > 0:
                print(f'❌回答错误，您还剩{leave}次机会！\n')
                tries += 1
                continue
            else:
                print(f'😢挑战失败，本题的正确答案是：{answer}，游戏结束！')
                is_playing = False
                break
    # 每次进入下一关之前，都要看一下is_playing，如果is_playing为False就要结束游戏！
    if not is_playing:
        break
# 如果到了这里，is_playing的值依然为True，那就意味着用户已经通关了！
if is_playing:
    print('🎉🎉🎉恭喜您！全部通关！🎉🎉🎉')
```

# 函数

函数（function）是：**组织好**的、可**重复使用**的、用于执行**特定任务**的代码块

## 什么是函数

Python 中函数分为三类：①内置函数、②模块提供的函数、③自定义函数

| 分类              | 说明                                            | 定义方式              | 示例                                 |
| ----------------- | ----------------------------------------------- | --------------------- | ------------------------------------ |
| **内置函数**      | Python 解释器自带，无需定义、无需导入，直接使用 | 系统预先实现          | `print()`、`len()`、`int()`、`max()` |
| **自定义函数**    | 开发者根据业务需求自己编写的函数                | 使用 `def` 关键字定义 | `def add(a,b): return a+b`           |
| **模块 / 库函数** | 存放在标准库 / 第三方库中，需先导入模块再调用   | `import 模块` 后使用  | `math.sqrt()`、`random.randint()`    |

## 基本使用

### 定义函数

```
def 函数名():
    函数体
    函数体
```

```
# 定义函数
def welcome():
    print('欢迎来到尚硅谷课堂！')
    print('尚硅谷，让天下没有难学的技术！')
```

### 调用函数

```
# 定义函数
def welcome():
    print('欢迎来到尚硅谷课堂！')
    print('尚硅谷，让天下没有难学的技术！')

# 调用函数（让函数中的代码运行起来）
welcome()
welcome()
welcome()
```

### 参数

参数可以让函数接收外部传入的数据，能让函数更具通用性和灵活性，

```
# 定义函数（定义的同时：声明需要两个参数，分别是：菜品数量 num，和菜品名称 dish）
def order(num, dish):
    print(f'您点的是：{num}份 {dish}')
    print(f'{dish}可是很好吃的！')
    print(f'你只点了{num}份，够吃吗？\n')

# 调用函数（调用的同时：传递了两个值）
order(1, '辣椒炒肉')
order(2, '辣子鸡')
```

**实参 形参**

```
● 形参（形式参数）：在定义函数时，用来接收数据的变量叫形参，形参是函数定义者设置的。
● 实参（实际参数）：在调用函数时，给函数传递的具体值叫实参，实参是函数调用者提供的。
```

**位置参数：**调用函数时，根据参数在函数定义时出现的顺序，把实参的值，依次传递给对应的形参。

```
def order(num, dish):
    print(f'您点的是：{num}份 {dish}')
    print(f'{dish}可是很好吃的！')
    print(f'你只点了{num}份，够吃吗？\n')

# 以下是错误示范
order(3)  # 参数少了
order(4, '宫保鸡丁', 7)  # 参数多了
order('宫保鸡丁', 4)  # 实参顺序没有和形参保持一致，不会报错，但会造成数据错乱。
```

**关键字参数:** 函数调用时通过`**形参名 = 值**`的形式传递的参数，就是关键字参数

```
# 定义函数
def greet(name, gender, age, height):
    print(f'我叫{name}，性别{gender}，年龄是{age}，身高是{height}cm')

# 调用函数（使用关键字参数）
greet(name='张三', gender='男', age=18, height=172)
greet(height=172, age = 18, gender='男', name='张三')
```

关键字参数和位置参数可以混用，但是位置参数必须在关键字参数之前

```
# 正确使用方式
greet('张三', '男', height=172, age=18)

# 错误示例
greet(height=172, age=18, '张三', '男')
greet(name='张三', '男', 18, 172)
greet(name='张三', '男', age=18, 172)
greet(height=172, age=18, gender='男', name='张三', age=19)
greet(height=172, age=18, gender='男', name='张三', school='尚硅谷')
```

**具体限制方式：**`/`前面只能用『位置参数』，`*`后面只能用『关键字参数』**。**

```
# 定义函数（使用/和*限制传参方式）
def greet(name, /, gender, *, age, height):
    print(f'我叫{name}，性别{gender}，年龄是{age}，身高是{height}cm')

# 正确示例
greet('张三', '男', age=18, height=172)
greet('张三', gender='男', age=18, height=172)

# 错误示例
greet(name='张三', gender='男', age=18, height=172)
greet('张三', '男', 18, height=172)
```

**参数默认值**

在定义函数时，可以通过`**形参名 = 值**`的形式，为形参设置一个默认值，这样就可以实现：

- 若调用函数时**没有传入**该参数的值，就使用默认值。
- 若调用函数时**传入了**该参数的值，就使用传入的值。

```
# 定义函数（设置参数默认值）
def greet(name, gender, age, height, msg='你好'):
    print(f'我叫{name}，性别{gender}，年龄是{age}，身高是{height}cm')
    print(f'我想说：{msg}')
    
# 调用函数
greet('张三', '男', 18, 172)
greet('张三', '男', 18, 172, 'hello')
greet('张三', '男', 18, 172, msg='hello')
```

**注意：**定义函数时，『默认参数』必须放在『必选参数』的后面，或者换一种说法就是：某个形参，一旦设置了默认值，那它后面的所有形参，也必须要写默认值！



**可变参数**

在定义函数时，如果不确定会传入多少个参数，那就可以使用可变参数，具体写法有两种：

- 使用`*形参名`来接收任意数量的『位置参数』，多个位置参数最终会被打包成一个『元组』。
- 使用`**形参名`来接收任意数量的『关键字参数』，多个关键字参数最终会被打包成一个『字典』。

```
# 定义函数（使用*args去接收：可变位置参数，args只是大家习惯这么写，当然也可以换成其他变量）
def test1(*args):
    # 此处args的值，是一种新的数据类型，叫：元组，我们下一章就去讲元组
    print(args)

# 调用函数
test1('张三', '男', 18, 172)
```

```
# 定义函数（使用**kwargs去接收：可变关键字参数，kwargs只是大家习惯这么写，当然也可以换成其他变量）
def test2(**kwargs):
    # 此处kwargs的值，是一种新的数据类型，叫：字典，我们下一章就去讲字典
    print(kwargs)

# 调用函数
test2(name='张三', gender='男', age=18, height=172)
```

『可变位置参数』和『可变关键字参数』，可以同时使用，但必须要**先写**『**可变位置参数**』。

```
# 定义函数（同时使用：可变位置参数、可变关键字参数）
def test3(a, b, *args, c='尚硅谷', **kwargs):
    print(a)
    print(b)
    print(c)
    print(args)
    print(kwargs)
# 调用函数
test3('张三', '男', '抽烟', '喝酒', age=18, height=172)
```

特殊字面None

None 是一个特殊的字面量，用来表示：空值、无值、无意义。

例如：`msg = None` 的含义是 —— 我先定义一个变量 `msg`，但目前还不知道它会存储什么类型的值，那能不能写成 `msg = 0` 呢？这要看具体情况

```
● 如果确定 msg 之后会存放数值类型的数据，那这样写是可以的。
● 但如果还不确定 msg 将来会存放什么类型的数据，最好不要写成 msg = 0，否则可能会误导别人以为它一定是数值类型
```

```
1. None的类型是NoneType。
2. None出现在布尔判断中(if判断条件、while循环条件)，会被当作False来处理。
3. None不能参与任何数学运算，也不能与字符串拼接。
4. 不给函数设置返回值，那函数默认就会返回None
```

## 返回值

### **什么是返回值**

**函数返回值：**函数执行完毕后，会把执行结果交给调用者，这个执行结果就是函数的返回值。

对于自定义的函数，即便我们不去设置返回值，函数也会默认返回`None`，由于`None`表示空，所以如果一个函数的返回值是`None`的话，就也可以说：这个函数“没有”返回值。

```
# 定义函数
def add(n1, n2):
    print(f'我收到了：{n1}、{n2}，二者相加是：{n1 + n2}')
    print('add函数执行完毕了')

# 调用函数
result = add(100, 200)
print(result)  # None
```

### **如何设置返回值**

使用`return`关键字可以设置函数的返回值，`return`的作用有两个，分别是：

1. 结束函数的运行。
2. 把`return`后面的值，作为函数的返回值。

```
# 定义函数
def add(n1, n2):
    print(f'我收到了：{n1}、{n2}，二者相加是：{n1 + n2}')
    print('add函数执行完毕了')
    return n1 + n2

# 调用函数
result = add(100, 200)
print(result)

# print函数是没有返回值的
res = print('hello')
print(res)
```

## 全局作用域 VS 局部作用域

作用域就是变量能**起作用的范围**（变量在哪里能用，在哪里不能用），Python 中有多种作用域，我们先来学习：全局作用域、局部作用域。

1. **全局作用域：**整个`.py`文件最外层的范围，就是全局作用域。
2. **全局变量：**写在全局作用域中的变量，就叫：全局变量，全局变量在整个程序中都可以访问。

1. **局部作用域：**函数的内部范围，就是局部作用域。 
2. **局部变量：**写在局部作用域中（函数内部）的变量，叫：局部变量，它只能在当前函数中使用。

global  在函数内部使用`global`关键字，可以声明变量为全局变量。

```
a = 100

def test():
    global a  # 使用 global 关键字，将a声明为全局变量。
    a = 300
    print('函数中的打印（a）', a)
test()
print('全局的打印（a）', a)
```

测试

```
# 全局作用域 与 局部作用域，以及global的使用
a = 100
b = 200

def test():
    c = '尚硅谷'
    d = '你好啊'
    global a
    a = 300
    print('函数中的打印（a）', a)
    print('函数中的打印（b）', b)
    print('函数中的打印（c）', c)
    print('函数中的打印（d）', d)
test()
print('***************')
print('全局的打印（a）', a)
print('全局的打印（b）', b)
print(c)
print(d)


# 局部作用域 和 局部变量，会在函数调用时创建，在函数执行结束后自动销毁
def test2():
    m = 100
    m += 1
    print(f'我是test2函数中打印的m：{m}')
test2()
test2()
test2()


# 全局作用域 与 全局变量，会在程序开始时创建，在程序结束后销毁
n = 100
def test3():
    global n
    n += 1
    print(f'我是test3函数中打印的n：{n}')
test3()
test3()
test3()
print(n)
```

## 嵌套调用

```
# 函数嵌套调用测试1
def greet(name, msg):
    print(f'我叫{name}，我想说的话在下面：')
    speak(msg)
    print('嗯，我想说的结束了')

def speak(msg):
    print('----------')
    print(msg)
    print('----------')

greet('张三', '你好啊')

# 函数嵌套调用测试2
def test1():
    print('进入 test1 函数')
    test2()
    print('退出 test1 函数')

def test2():
    print('进入 test2 函数')
    test3()
    print('退出 test2 函数')

def test3():
    print('进入 test3 函数')
    print('***正在执行 test3 函数')
    print('退出 test3 函数')

test1()
```

## 递归调用

1️⃣递归调用：函数自己调用自己的一种操作

错误**示例**会出现逻辑死循环

```
def welcome():
    print("你好啊！")
    welcome() # welcome 函数内部在调用自己

welcome()
```

2️⃣递归必须要具备终止条件（不能无限的一直调用，总得有停下来的时候。）

```
# 使用递归打印n次“你好啊”（从大到小）
def welcome(n):
    print(f'你好啊{n}')
    if n > 1:
        welcome(n - 1)
# 调用函数
welcome(5)

# 使用递归打印n次“你好啊”（从小到大）
def welcome(n):
    if n > 1:
        welcome(n - 1)
    print(f'你好啊{n}')
# 调用函数
welcome(5)
```

核心原因：**递归调用位置不同**，一个先打印再递归，一个先递归再打印。

打印写在**递归前面**：每一层立刻输出，顺序**从大到小**。

打印写在**递归后面**：先一路递归到最底层，再回溯输出，顺序**从小到大**。

案例

```
# 使用递归求阶乘
def factorial(num):
    if num == 0:
        return 1
    else:
        return num * factorial(num - 1)
# 调用函数，求5的阶乘
result = factorial(6)
print(result)
```

## 函数说明文档

**函数说明文档**：写在函数里的文字说明，用来描述：函数的功能、需要哪些参数、返回什么结果，它的语法和普通字符串一样，用三引号包裹：

有了函数说明文档之后，可以通过鼠标悬浮的方式，查看函数的具体信息

## 综合案例

```
def calc_total(*nums):
    """
    计算总运动量（个）
    :param nums: 每一天的运动量（可变参数）
    :return: 总运动量（个）
    """
    # 备注：nums的类型是元组（下一章马上就讲了），sum是内置函数，可以对元组中的数据求和
    return sum(nums)

def calc_avg(total, days=7):
    """
    计算平均值
    :param total: 总运动量（个）
    :param days: 天数（默认值是7）
    :return: 平均值
    """
    return total / days

def check_success(total, goal=120):
    """
    判断本次挑战是否成功
    :param total: 总运动量
    :param goal: 成功数量（默认值为120）
    :return: 成功或失败的具体信息
    """
    if total >= goal:
        return '✅恭喜！挑战成功！'
    else:
        return '❌抱歉！挑战失败！'

def main(title, duration, goal):
    """
    主函数，用于开始一场挑战赛
    :param title: 比赛标题
    :param duration: 比赛持续天数
    :param goal: 目标运动量
    :return: None
    """
    print(f'【{title}】【{duration}天】✊️挑战赛（请输入每天的数量）')
    num1 = int(input('第1天：'))
    num2 = int(input('第2天：'))
    num3 = int(input('第3天：'))
    # 计算总数
    total = calc_total(num1, num2, num3)
    # 计算平均值
    avg = calc_avg(total, duration)
    # 判断挑战是否成功
    result = check_success(total, goal)
    # 打印相关信息
    print(f'【{title}】【{duration}天】健身总结')
    print(f'总数：{total}，平均值：{avg:.1f}')
    print(result)

main('俯卧撑', 3, 40)
```

# 数据容器

1️⃣**数据容器的特点：**

1. 数据容器，有时也简称为**容器**。
2. 数据容器可以存放**多个数据**，每个数据也被称为一个**元素**。
3. 数据容器中的元素可以是**任意类型**。
4. 数据容器会给我们提供多种**操作元素**的方法。

2️⃣**Python 中常用的数据容器:**

1. 列表（List）
2. 元组（tuple）
3. 字符串（str）
4. 集合（set）
5. 字典（dict）

## 列表

**列表：**用来存放一组**有序的数据**，并且可以对其中的数据进行：增删改查。

```
# 定义有内容的列表
list1 = [34, 56, 21, 56, 11]
list2 = ['北京', '尚硅谷', '你好啊']
list3 = [23, '尚硅谷', True, None]
list4 = [23, '尚硅谷', True, None, [100, 200, 300]] # list4 是一个嵌套列表

# 定义空列表（列表中的数据，后期会通过特定写法填充）
list5 = []
list6 = list()

print(list1, type(list1))  # [34, 56, 21, 56, 11] <class 'list'>
print(list2, type(list2))  # ['北京', '尚硅谷', '你好啊'] <class 'list'>
print(list3, type(list3))  # [23, '尚硅谷', True, None] <class 'list'>
print(list4, type(list4))  # [23, '尚硅谷', True, None, [100, 200, 300]] <class 'list'>
print(list5, type(list5))  # [] <class 'list'>
print(list6, type(list6))  # [] <class 'list'>
```

下标又叫索引值，其实就是元素在列表中的“位置编号”，分为：『正索引』、『负索引』。

正索引 从左往右

负索引 从右往左

```
# 定义一个列表
nums = [10, 20, 30, 40, 50]

# 测试正索引
print(nums[0])  # 10
print(nums[1])  # 20
print(nums[2])  # 30
print(nums[3])  # 40
print(nums[4])  # 50

# 测试负索引
print(nums[-1])  # 50
print(nums[-2])  # 40
print(nums[-3])  # 30
print(nums[-4])  # 20
print(nums[-5])  # 10

# 测试错误索引
print(nums[5]) 

# 定义一个嵌套列表
nums2 = [10, 20, ['你好啊','尚硅谷'], 40, 50]
# 取出“尚硅谷”
print(nums2[2][1])  # 尚硅谷
```

### **列表的增删改查**

那方法和函数之间是什么关系呢？从更正式的角度来说：当一个函数隶属于某个对象时，这个函数就被称为该对象的方法。

| 方法        | 作用             |
| ----------- | ---------------- |
| `sort()`    | 列表原地升序排序 |
| `reverse()` | 列表原地反转顺序 |

| 分类           | 方法 / 操作 | 语法格式                  | 作用说明                                           | 示例                |
| -------------- | ----------- | ------------------------- | -------------------------------------------------- | ------------------- |
| **查（查询）** | 下标取值    | `列表[索引]`              | 根据索引获取单个元素，索引从 0 开始                | `lst[0]`            |
|                | 切片        | `列表[起始:结束:步长]`    | 获取一段子列表                                     | `lst[1:3]`          |
|                | `index()`   | `列表.index(元素)`        | 查找元素**首次**出现的索引，找不到报错             | `lst.index(2)`      |
|                | `count()`   | `列表.count(元素)`        | 统计元素在列表中出现的次数                         | `lst.count(3)`      |
|                | `len()`     | `len(列表)`               | 获取列表元素总个数                                 | `len(lst)`          |
| **增（添加）** | `append()`  | `列表.append(元素)`       | 在**列表末尾**追加单个元素                         | `lst.append(5)`     |
|                | `extend()`  | `列表.extend(可迭代对象)` | 末尾**批量追加**多个元素（列表 / 字符串等）        | `lst.extend([6,7])` |
|                | `insert()`  | `列表.insert(索引, 元素)` | 在**指定索引位置**插入元素                         | `lst.insert(1, 9)`  |
| **改（修改）** | 下标赋值    | `列表[索引] = 新值`       | 通过索引覆盖原有元素                               | `lst[0] = 10`       |
| **删（删除）** | `del` 语句  | `del 列表[索引]`          | 根据索引删除元素，也可删除整个列表                 | `del lst[2]`        |
|                | `pop()`     | `列表.pop(索引)`          | 根据索引删除并**返回**该元素；不传参默认删最后一位 | `lst.pop()`         |
|                | `remove()`  | `列表.remove(元素)`       | 根据**元素值**删除第一个匹配项，找不到报错         | `lst.remove(4)`     |
|                | `clear()`   | `列表.clear()`            | 清空列表所有元素，保留空列表                       | `lst.clear()`       |

### 内置函数

| 函数         | 语法             | 功能说明                             | 示例                                |
| ------------ | ---------------- | ------------------------------------ | ----------------------------------- |
| `len()`      | `len(list)`      | 返回列表元素个数                     | `len([1,2,3])` → 3                  |
| `max()`      | `max(list)`      | 返回列表中最大值                     | `max([2,5,1])` → 5                  |
| `min()`      | `min(list)`      | 返回列表中最小值                     | `min([2,5,1])` → 1                  |
| `sum()`      | `sum(list)`      | 对列表数值元素求和（仅数字）         | `sum([1,2,3])` → 6                  |
| `sorted()`   | `sorted(list)`   | 生成**新列表**并升序排序，原列表不变 | `sorted([3,1,2])` → [1,2,3]         |
| `reversed()` | `reversed(list)` | 返回反转迭代器，需转列表查看结果     | `list(reversed([1,2,3]))` → [3,2,1] |

### 循环遍历

while循环

```
# 定义一个成绩列表
score_list = [62, 50, 60, 48, 80, 20, 95]

# 使用while循环遍历列表
index = 0
while index < len(score_list):
    print(score_list[index])
    index += 1
```



```
# 使用for循环遍历列表
for item in score_list:
    print(item)

# 使用for循环遍历列表（通过range函数 和 len函数按照索引遍历）
for index in range(len(score_list)):
    print(score_list[index])
```



```
# 使用for循环遍历列表（通过enumerate函数，同时获取下标（索引值）和元素）
# enumerate 的 start 参数，可以让计数从指定值开始（改变的是循环时的“编号”，不是真正的索引值）
for index, item in enumerate(score_list, start=5):
    print(index, item, score_list[0])
print('最后的打印', score_list[0])
```

### 总结

1. 可存放不同类型的元素。
2. 元素是有序存储的（正索引、负索引）。
3. 列表中的元素允许重复。
4. 元素是允许修改的（增、删、改、查、其他操作）。
5. 长度不固定，可以随着操作自动调整大小。

**📍**一句话总结：列表是最常用的数据容器，当遇到要“存储一批数据”的场景时，首选列表。

### 综合练习

```
print('请输入学生成绩，输入“结束”停止录入')
score_list = []

# 持续循环，让用户输入学生成绩
while True:
    data = input('📝请输入成绩：')
    if data == '结束':
        break
    else:
        score_list.append(int(data))

# 如果score_list中有数据，则开始统计
if score_list:
    # 统计平均分
    avg = sum(score_list) / len(score_list)
    # 合格人数
    pass_count = 0
    # 优秀人数
    excellent_count = 0
    # 遍历列表，开始统计
    for item in score_list:
        if item >= 60:
            pass_count += 1
        if item >= 90:
            excellent_count += 1
    # 合格率
    pass_rate = pass_count / len(score_list) * 100
    # 优秀率
    excellent_rate = excellent_count / len(score_list) * 100
    # 打印信息
    print('********⬇️统计信息如下⬇️********')
    print(f'🧑‍🎓总人数为：{len(score_list)}')
    print(f'🔺最高分为：{max(score_list)}')
    print(f'🔻最低分为：{min(score_list)}')
    print(f'✅合格人数：{pass_count}人')
    print(f'📈合格率为：{pass_rate:.1f}%')
    print(f'🏆优秀人数：{excellent_count}人')
    print(f'📈优秀率为：{excellent_rate:.1f}%')
    print(f'📊平均分数：{avg:.1f}')
else:
    print('您没有输入任何成绩！')
```

## 元祖

**元组：**用来存放一组有序的数据，但其中的内容一旦创建就**不可修改**（不能增、删、改，只能查）。

定义 

```
# 定义有内容的元组
t1 = (28, 67, 21, 67, 11)
t2 = ('北京', '尚硅谷', '你好')
t3 = (100, True, '你好', None)
t4 = (100, True, '你好', None, (50, 60, 70))
print(type(t1), t1)  # <class 'tuple'> (28, 67, 21, 67, 11)
print(type(t2), t2)  # <class 'tuple'> ('北京', '尚硅谷', '你好')
print(type(t3), t3)  # <class 'tuple'> (100, True, '你好', None)
print(type(t4), t4)  # <class 'tuple'> (100, True, '你好', None, (50, 60, 70))

# 定义空元组
t1 = ()
t2 = tuple()
print(type(t1), t1)  # <class 'tuple'> ()
print(type(t2), t2)  # <class 'tuple'> ()
```

当元组中只有一个元素时，末尾必须写上`,`

## 序列切片

## 字符串

## 集合

集合是一种：**无序**、**元素唯一**的容器类型。

📋**备注：**无序是指从集合中取出元素的顺序，与定义集合时存入的顺序不一定一致。

集合分为两种，分别是：

1. **可变集合（set）**：内部的元素无序（不保证顺序）、不能通过下标访问元素、会自动去除重复元素。
2. **不可变集合（forzenset）**：特点和可变集合一样，唯一的区别就是：其中的元素不可修改。

1. 无序：集合中的元素没有固定顺序，无法通过下标访问。
2. 不重复：集合会自动去重，同一个元素只会保留一份。
3. 分为两种：可变集合合（set）和不可变集合（forzenset）。
4. 集合中的元素必须是不可变类型（如：数字、字符串、元组）。
5. 集合支持：并集、交集、差集、对称差集等数学操作。

### 定义

1️⃣可变集合的定义方式：使用花括号`{}`包裹，不同的数据项之间，用`,`做分隔。

```python
# 定义有内容的【可变集合】
s1 = {10, 20, 20, 30, 40, 40, 50, 60, 60, 70, 80, 90, 100}
s2 = {'你好', 'hello', '你好', 'atguigu', '北京'}
s3 = {10, '你好', True, 1, 12.4}
print(type(s1), s1)  # <class 'set'> {100, 70, 40, 10, 80, 50, 20, 90, 60, 30}
print(type(s2), s2)  # <class 'set'> {'atguigu', 'hello', '你好', '北京'}
print(type(s3), s3)  # <class 'set'> {True, 10, '你好', 12.4}

# 定义空集合（可变集合）
s1 = set()
print(type(s1), s1)  # <class 'set'> set()
```

**📢****注意：**不能直接写`{}`来定义空集合，因为直接写`{}`定义的是：空字典。

```python
# 不能直接写{}来定义空集合，因为直接写{}定义的是：空字典
s2 = {}
print(type(s2), s2)  # <class 'dict'> {}
```

2️⃣不可变集合的定义方式：借助内置的`forzenset`函数。

```python
# 定义有内容的【不可变集合】
s1 = frozenset({10, 20, 20, 30, 40, 40, 50, 60, 60, 70, 80, 90, 100})
s2 = frozenset({'你好', 'hello', '你好', 'atguigu', '北京'})
s3 = frozenset({10, '你好', True, 1, 12.4})
print(type(s1), s1)
print(type(s2), s2)
print(type(s3), s3)

# frozenset 接收的参数，可以是任意可迭代对象，但最终返回的一定是【不可变集合】
s1 = frozenset([10, 20, 30, 40, 50])
s2 = frozenset((10, 20, 30, 40, 50))
s3 = frozenset('hello')
print(type(s1), s1)
print(type(s2), s2)
print(type(s3), s3)

# 定义空集合（不可变集合）
s3 = frozenset()
print(type(s3), s3)
```

3️⃣集合中不能嵌套【可变集合】，但可以嵌套【不可变集合】

为什么会这样？—— 只有“不可变”的东西，才能安全的放进集合里。

```python
# 集合中不能嵌套【可变集合】，但可以嵌套【不可变集合】
# 通俗理解：只有“不可变”的东西，才能安全的放进集合里
s1 = {10, 20, 30, 40, 50}
s2 = frozenset({100, 200, 300, 400, 500})
l1 = [666, 777, 888]
t1 = ('hello', 'atguigu', '北京')

s3 = {11, 22, 33, s1}  # 报错
s3 = {11, 22, 33, s2}  # 没问题
s3 = {11, 22, 33, l1}  # 报错
s3 = {11, 22, 33, t1}  # 没问题
print(s3)
```

### 增删改查

1️⃣**新增**

**方式1：**使用`集合.add(元素)`，向集合中添加元素，无返回值。

```python
# add方法：向集合中添加元素
s1 = {10, 20, 30, 40, 50}
s1.add(60)
print(s1)
```

**方式2：**使用`集合.update(元素)`，向集合中批量添加元素（接收可迭代对象），无返回值。

```python
# update方法：向集合中添加元素（必须传递可迭代对象，例如：列表、元组、集合等）
s1 = {10, 20, 30, 40, 50}
s1.update([60, 70])
s1.update((80, 90))
s1.update({100, 200})
s1.update(range(300, 308))
print(s1)
```

2️⃣**删除**

**方式1：**使用`集合.remove(元素)`，从集合中移除指定元素（若元素不存在，会报错），无返回值。

```python
# remove方法：从集合中移除元素（移除不存在的元素，会报错）
s1 = {10, 20, 30, 40, 50}
s1.remove(20)
print(s1)
```

**方式2：**使用`集合.discard(元素)`，从集合中移除指定元素（若元素不存在，不会报错），无返回值。

```python
# discard方法：从集合中移除元素（移除不存在的元素，不会报错）
s1 = {10, 20, 30, 40, 50}
s1.discard(80)
print(s1)
```

**方式3：**使用`集合.pop()`，从集合中移除一个任意元素，返回值：移除的那个元素。

```python
# pop方法：从集合中移除一个任意元素，返回值是移除的那个元素
s1 = {10, 20, 30, 40, 50}
s2 = {'你好', '北京', '尚硅谷', 'hello'}
result = s1.pop()
print(s1)
print(result)
```

**方式4：**使用`集合.clear()`，清空集合，无返回值。

```python
# clear方法：清空集合
s1 = {10, 20, 30, 40, 50}
s1.clear()
print(s1)
```

3️⃣**修改**

**📢****注意：**集合没有下标，也不支持`replace`方法，所以集合没有专门用于“改”的方法，但可以使用：`remove`+`add`的组合，来达到“修改”的效果。

```python
# 改
# 使用 add + remove 的组合，来实现修改的效果
s1 = {10, 20, 30, 40, 50}
s1.remove(20)
s1.add(66)
print(s1)
```

4️⃣**查询**

**📢****注意：**由于集合没有下标，也不支持切片操作，所以集合不具备按位置访问的能力。虽然不能通过下标读取元素，但可以使用【成员运算符】来判断：某个元素是否在集合中，成员运算符我们会放在后面讲，不过大家可以提前感受一下：

```python
# 查：集合不能通过下标去读取元素，但能通过 【成员运算符】去查看集合中是否包含指定元素
# 由于成员运算符适用于所有数据容器，所以我们会等所有数据容器都讲完以后，再说成员运算符
s1 = {10, 20, 30, 40, 50}
# s1[0] # 此行报错，因为集合不能通过下标访问元素

# 先提前感受一下成员运算符
result = 20 not in s1
print(result)
```

### 常用方法

集合常用的方法有如下几个：

1️⃣使用`集合A.difference(集合B)`，找出集合A中，不同于集合B的元素。

```python
# 集合A.difference(集合B)：
# 作用：找出集合A中，不同于集合B的元素（集合A 与 集合B 都不变，返回的是一个新的集合）
s1 = {10, 20, 30, 40, 50}
s2 = {30, 40, 50, 60, 70}
result = s1.difference(s2)
print(s1)
print(s2)
print(result)
```

2️⃣使用`集合A.difference_update(集合B)`，从集合A中，删除集合B中存在的元素。

```python
# 集合A.difference_update(集合B)：
# 作用：从集合A中，删除集合B中存在的元素（集合A会被修改，集合B不会）
s1 = {10, 20, 30, 40, 50}
s2 = {30, 40, 50, 60, 70}
s1.difference_update(s2)
print(s1)
print(s2)
```

3️⃣使用`集合A.union(集合B)`，合并两个集合，集合A 和 集合B 都不变，返回的是一个新的集合。

```python
# 集合A.union(集合B)：
# 作用：合并两个集合，集合A 和 集合B 都不变，返回的是一个新的集合
s1 = {10, 20, 30, 40, 50}
s2 = {30, 40, 50, 60, 70}
result = s2.union(s1)
print(s1)
print(s2)
print(result)
```

4️⃣使用`集合A.issubset(集合B)`，判断集合A是否为集合B的子集，返回值为布尔值。

```python
# 集合A.issubset(集合B)：
# 作用：判断集合A是否为集合B的子集
# 如果 集合A的所有元素都在集合B中，那就返回True，否则返回False
s1 = {10, 20, 30, 40, 50}
s2 = {30, 40, 50, 60, 70}
s3 = {30, 40, 50}
result = s3.issubset(s1)
print(result)
```

5️⃣使用`集合A.issuperset(集合B)`，判断集合A是否是集合B的超集，返回值为布尔值。

```python
# 集合A.issuperset(集合B)：
# 作用：判断集合A是否是集合B的超集
# 如果集合A中，包含了集合B中的所有元素，那就返回True，否则返回False
s1 = {10, 20, 30, 40, 50}
s2 = {30, 40, 50, 60, 70}
s3 = {30, 40, 50}
result = s1.issuperset(s3)
print(result)
```

6️⃣使用`集合A.isdisjoint(集合B)`，判断集合A和集合B是否没有交集，返回值为布尔值。

```python
# 集合A.isdisjoint(集合B)：
# 作用：
# 如果没有交集，返回True；只要有一个公共元素，就返回False
s1 = {10, 20, 30, 40, 50}
s2 = {30, 40, 50, 60, 70}
s3 = {80, 90}
result = s1.isdisjoint(s2)
print(result)
```

### 集合的数学运算

```
s1 = {10, 20, 30, 40, 50, 60}
s2 = {40, 50, 60, 70, 80, 90}

# 并集
result = s1 | s2
print(result)

# 交集
result = s1 & s2
print(result)

# 差集
result = s1 - s2
print(result)

# 对称差集
result = s1 ^ s2
print(result)
```

### 集合的遍历

```
s1 = {10, 20, 30, 40, 50, 60}

# 集合不能使用while循环遍历（以下是错误示例）
# index = 0
# while index < len(s1):
#     print(s1[index])
#     index += 1

# 集合可以使用for循环遍历
for item in s1:
    print(item)
```



## 字典

用来存放一组『键值对』数据，可通过『键(key)』对『值(value)』进行：增、删、改、查操作。

1. 键值对结构：字典中的数据以`key:value`的形式存在，每个键都对应一个值。
2. 键唯一：字典中的键（key）不能重复，若重复则后写的会覆盖前写的。
3. 键不可变：：键必须是不可变类型（如数字、字符串、元组等），而值可以是任意类型。
4. 不支持下标：字典中的元素不能通过下标取值。
5. 支持增删改查，支持for循环遍历。

### 定义

1. 大括号`{}`包裹，每个元素之间用逗号`,`分隔，每个元素的格式为`key:value`
2. 字典中的 key 不能重复，若出现重复，则后写的会覆盖之前写的。
3. 字典中的 key 必须是不可变类型，但 value 可以是任意类型。
4. 字典可以嵌套

```
# 定义有内容的字典
d1 = {'张三': 72, '李四': 60, '王五': 85}
print(type(d1), d1)
```

```
# 字典可以嵌套
student_dict = {
    2025001: {
        '姓名': '张三',
        '年龄': 18,
        '成绩': 72,
        '爱好': ['抽烟', '喝酒', '烫头']
    },
    2025002: {
        '姓名': '李四',
        '年龄': 19,
        '成绩': 60,
        '爱好': ['唱歌', '跳舞', '打台球']
    },
    2025003: {
        '姓名': '王五',
        '年龄': 20,
        '成绩': 85,
        '爱好': ['学习', '看书', '打太极']
    }
}
print(student_dict)
```

### 字典的增删改查

1️⃣**新增**新增语法：`字典[key] = 值`

```
# 新增
d1 = {'张三': 72, '李四': 60, '王五': 85}
d1['赵六'] = 100
print(d1)
```

**2️⃣**删除

```
# 删除
d1 = {'张三': 72, '李四': 60, '王五': 85}

# 删除指定key所对应的那组键值对
del d1['张三']
print(d1)

# 删除指定key所对应的那组键值对，并返回这个key所对应的值
result = d1.pop('张三')
print(d1)
print(result)

# pop方法可以设置默认值
# 默认值可以保证：当要删除的key不存在的情况下，程序不会报错，并且返回这个默认值
result = d1.pop('奥特曼', '删除失败！')
print(d1)
print(result)

# 清空字典
d1.clear()
print(d1)
```

3️⃣修改

```
d1 = {'张三': 72, '李四': 60, '王五': 85}

# 修改的写法，与新增的写法一样，若字典中有对应的key，就是修改；若没有，就是新增
d1['张三'] = 97
print(d1)

# 批量修改
d1.update({'李四': 40, '王五': 67})
print(d1)
```

**4️⃣**查询

```
# 查询
d1 = {'张三': 72, '李四': 60, '王五': 85}

# 直接取值，若键（key）不存在，会报错
result = d1['张三']

# 安全取值，若键（key）不存在，会返回默认值（若没有设置默认值，则会返回None）
result = d1.get('奥特曼', '抱歉，key不存在！')
print(result)
```

### 字典中常用的方法

1️⃣使用`keys`方法，获取字典中所有的键。

```python
# keys方法：用于获取字典中所有的键
d1 = {'张三': 72, '李四': 60, '王五': 85}

# keys方法的返回值不是list，而是一种叫做dict_keys的类型
result = d1.keys()
print(result)
print(type(result))

# dict_keys和列表类似，可以被遍历，但要注意的是：它不能通过下标访问元素
for item in result:
    print(item)
print(result[0])

# 借助内置的list函数，可以将dict_keys转换成list
l1 = list(result)
print(l1)
print(type(l1))
```

2️⃣使用`values`方法，获取字典中所有的值。

```python
# values方法：获取字典中所有的值
d1 = {'张三': 72, '李四': 60, '王五': 85}
# values方法的返回值类型是：dict_values，它的特点和dict_keys一样
result = d1.values()
print(result)
print(type(result))
```

3️⃣使用`items`方法，获取字典中所有的键值对（每组键值对以元组的形式呈现）

```python
# items方法：获取字典中所有的键值对（每组键值对以元组的形式呈现）
d1 = {'张三': 72, '李四': 60, '王五': 85}

# items方法返回的类型是：dict_items，它的特点也和dict_keys一样
result = d1.items()
print(result)
print(type(result))
```

###  字典的循环遍历

```
# 字典不能使用while循环遍历，但可以使用for循环遍历
d1 = {'张三': 72, '李四': 60, '王五': 85}

for key in d1:
    print(f'{key}的成绩是{d1[key]}')

for key in d1.keys():
    print(f'{key}的成绩是{d1[key]}')
```

## 数据容器通用操作

| 操作       | 语法               | 作用                         | 适用容器                               | 示例                       |
| ---------- | ------------------ | ---------------------------- | -------------------------------------- | -------------------------- |
| 求长度     | `len(容器)`        | 返回元素 / 字符总数          | 全部                                   | `len([1,2])` → 2           |
| 遍历       | `for 变量 in 容器` | 逐个取出元素                 | 全部                                   | `for i in (1,2): print(i)` |
| 成员判断   | `元素 in 容器`     | 判断元素是否存在，返回布尔值 | 全部                                   | `3 in {1,2,3}` → True      |
| 非成员判断 | `元素 not in 容器` | 判断元素是否不存在           | 全部                                   | `"a" not in "bc"` → True   |
| 最大值     | `max(容器)`        | 返回最大元素                 | list/tuple/str/set（元素可比较）       | `max([2,5,1])` → 5         |
| 最小值     | `min(容器)`        | 返回最小元素                 | list/tuple/str/set（元素可比较）       | `min("bac")` → 'a'         |
| 求和       | `sum(容器)`        | 数值元素累加                 | list/tuple/set（仅数字）               | `sum((1,2,3))` → 6         |
| 转为列表   | `list(容器)`       | 强制转换成列表               | list/tuple/str/set/dict（dict 只取键） | `list((1,2))` → [1,2]      |
| 转为元组   | `tuple(容器)`      | 强制转换成元组               | list/tuple/str/set/dict（dict 只取键） | `tuple([1,2])` → (1,2)     |
| 转为集合   | `set(容器)`        | 强制转集合，自动去重         | list/tuple/str/dict（dict 只取键）     | `set([1,1,2])` → {1,2}     |



```
# 以下这五个函数：既能定义对应的【空容器】，又能将【其他类型】转换为对应的数据类型

# 1.list 函数：1.定义空列表。2.将【可迭代对象】转换为列表
res1 = list(range(8))
res2 = list('欢迎来到尚硅谷')
res3 = list({10, 20, 30, 40, 50})
res4 = list({'张三': 75, '李四': 60, '王五':85}.items())
print(type(res1), res1)
print(type(res2), res2)
print(type(res3), res3)
print(type(res4), res4)

# 2.tuple 函数：1.定义空元组。2.将【可迭代对象】转换为元组
res1 = tuple(range(8))
res2 = tuple('欢迎来到尚硅谷')
res3 = tuple({10, 20, 30, 40, 50})
res4 = tuple({'张三': 75, '李四': 60, '王五':85})
print(type(res1), res1)
print(type(res2), res2)
print(type(res3), res3)
print(type(res4), res4)

# 3.set 函数：1.定义空集合。2.将【可迭代对象】转换为集合
res1 = set(range(8))
res2 = set('欢迎来到尚硅谷')
res3 = set({10, 20, 30, 40, 50})
res4 = set({'张三': 75, '李四': 60, '王五':85})
print(type(res1), res1)
print(type(res2), res2)
print(type(res3), res3)
print(type(res4), res4)


# 4.str 函数：1.定义空字符串。2.将【任意类型】转换为字符串
res1 = str(range(8))
res2 = str('欢迎来到尚硅谷')
res3 = str({10, 20, 30, 40, 50})
res4 = str({'张三': 75, '李四': 60, '王五':85})
res5 = str(False)
res6 = str(None)
res7 = str(100)
print(type(res1), res1)
print(type(res2), res2)
print(type(res3), res3)
print(type(res4), res4)
print(type(res5), res5)
print(type(res6), res6)
print(type(res6), res6)
print(type(res7), res7)

# 5.dict 函数：1.定义空字典。2.将【可迭代对象】转换为字典
# 备注：交给dict函数的内容必须是键值对才可以，否则就会报错
res1 = dict({'张三': 75, '李四': 60, '王五':85})
res2 = dict([('张三', 75), ('李四', 60), ('王五', 85)])
res3 = dict((('张三', 75), ('李四', 60), ('王五', 85)))
res4 = dict({('张三', 75), ('李四', 60), ('王五', 85)})
print(type(res1), res1)
print(type(res2), res2)
print(type(res3), res3)
print(type(res4), res4)

# 所有的数据容器，都支持【成员运算符】： in / not in  作用：判断某个“元素”是否在于容器中。
hobby = ['抽烟', '喝酒', '烫头']
nums = (10, 20, 30, 40, 50)
message = 'hello,atgiugu'
citys = {'北京', '天津', '上海', '重庆'}
score = {'张三': 75, '李四': 60, '王五':85}

print('喝酒' not in hobby)
print(20 not in nums)
print('hel' not in message)
print('上海' not in citys)
print('李华' not in score)
```



## 数据容器练习

## 数据容器总结

**有序与无序：**

- 有序：列表(list)、元组(tuple)、字符串(str)—— 元素有顺序，可通过下标访问元素。
- 无序：集合(set)、字典(dict) —— 元素没有固定位置，不能用下标访问。

**可修改：**

-  可变：列表(list)、集合(set)、字典(dict) —— 可以对内容进行增、删、改操作。
-  不可变：元组(tuple) 、字符串(str) —— 内容固定，创建后无法修改。

**可重复：**

-  允许重复：列表(list) 、元组(tuple) 、字符串(str)
-  不允许重复：集合(set) 、字典(dict)   备注：字典的 key 是唯一的，但 value 可重复 

| 容器类型       | 标识语法                    | 有序性     | 可变性 | 元素重复性       | 元素类型                     | 核心特点                     | 常用场景                         |
| -------------- | --------------------------- | ---------- | ------ | ---------------- | ---------------------------- | ---------------------------- | -------------------------------- |
| **列表 list**  | `[1, 2, "a"]`               | 有序       | 可变   | 允许重复         | 任意类型                     | 下标访问、支持切片，增删灵活 | 通用存储、有序数据、频繁增删改查 |
| **元组 tuple** | `(1, 2, "a")`               | 有序       | 不可变 | 允许重复         | 任意类型                     | 定义后无法修改，更安全       | 固定数据、函数多返回值、字典键   |
| **字符串 str** | `"hello"` / `'hi'`          | 有序       | 不可变 | 字符可重复       | 仅字符                       | 字符序列，内置丰富文本方法   | 文本处理、信息展示               |
| **集合 set**   | `{1, 2, 3}`                 | 无序       | 可变   | 自动去重         | 不可变类型（int/str/tuple）  | 交集、并集、差集运算         | 数据去重、集合关系判断           |
| **字典 dict**  | `{"name":"张三", "age":18}` | 3.7 + 有序 | 可变   | 键唯一，值可重复 | 键：不可变类型；值：任意类型 | 键值映射，按键快速查找       | 存储映射关系、配置、对象属性     |

# 对象

## 对象定义

## 类

### **基本使用**

1️⃣语法格式

```
# 定义一个类（类名通常用大驼峰写法）
class 类名:
    # 当一个函数被定义在类中时，它就被称为“方法”。
    # __init__方法又叫：初始化方法，它主要用来给当前实例对象添加属性。构造方法
    # __init__方法收到的参数是：当前正在创建的实例对象、其他自定义参数。
    # 当我们后期编写代码，对类进行实例化的时候，Python就会自动调用__init__方法，去完成对实例的初始化。
    def __init__(self, 参数1, 参数2, 参数3):
        # 通过self给当前实例添加属性，语法格式为：self.属性名 = 属性值
        self.属性名 = 参数1
        self.属性名 = 参数2
        self.属性名 = 参数3
```

2️⃣相关说明：

1. 类名通常采用**大驼峰**命名法（如:`Person`、`UserInfo`）。
2. 类中所定义的函数，通常又称为**方法**。
3. `__init__`方法叫**初始化方法**，当我们对类进行实例化时，Python会**自动调用**`__init__`方法。
4. `__init__`方法的名字不能更改，否则 Python 无法自动调用。
5. `__init__`收到的第一个参数是当前**正在创建的**实例对象，形参通常用`self`。
6. `__init__`方法收到的除`self`以外的参数，通常用来设置实例的属性值，通过“点”语法实现：`**self.属性名 = 属性值**`

3️⃣代码示例：

```
# 定义一个Person类（类名通常使用：大驼峰写法）
class Person:
    # 说明：当一个函数被定义在了类中时，那这个函数就被称为：方法。
    # __init__方法：初始化方法，主要作用：给当前正在创建的实例对象添加属性
    # __init__方法收到的参数：当前正在创建的实例对象（self）、其它的自定义参数
    # 当我们以后编写代码去创建Person类实例的时候，Python会自动调用__init__方法
    def __init__(self, name, age, gender):
        # 给实例添加属性（语法为：self.属性名 = 值）
        self.name = name
        self.age = age
        self.gender = gender
```

### **构造方法**

1️⃣语法格式：

```
实例名 = 类名(参数1, 参数2, ...)
```

2️⃣代码示例：

```
Python
运行代码复制代码
class Person:
​    def __init__(self, name, age, gender):
​        self.name = name
​        self.age = age
​        self.gender = gender
\# 创建Person的实例对象
p1 = Person('张三', 18, '男')
p2 = Person('李四', 22, '女')
```

3️⃣通过实例的“点”语法，可以『访问』或『修改』实例的属性。

```
Python

运行代码复制代码

\# 如果直接打印一个实例的话，我们是看不到实例身上的属性的

print(p1)

print(p2)

\# 通过点语法可以访问或修改实例身上的属性

print(p1.name)

print(p1.age)

print(p1.gender)

print('-' * 20)

print(p2.name)

print(p2.age)

print(p2.gender)

p1.name = '阿三'

print(p1.name)
```

4️⃣通过实例.__dict__ 的方式，可以查看实例身上的所有属性。

```
Python

运行代码复制代码

\# 通过 实例.__dict__ 可以查看实例身上的所有属性

print(p1.__dict__)

print(p2.__dict__)
```

5️⃣在实例创建完毕后，也可以通过实例.属性名 = 值的形式，给实例追加属性。

```
Python

运行代码复制代码

\# 实例创建完毕后，依然可以通过 实例.属性名 = 值 去给实例追加属性

p1.address = '北京昌平宏福科技园'

print(p1.__dict__)


```

6️⃣通过type() 函数，可以查看某个实例对象，是由哪个类创建出来的。

Python

运行代码复制代码

\# 通过type函数，可以查看某个实例对象，是由哪个类创建出来的

print(type(p1))

print(type(p2))

### **自定义方法**

我们之前提到过：类是用来规定一类事物所具有的『属性』和『行为』，在上一小节中，我们已经通过 `**self.属性名 = 值**`的形式，为实例对象添加了『属性』；接下来，要想让对象具备相应的『行为』，就需要通过『自定义方法』来实现

『自定义方法』的第一个参数也是`self`，是调用该方法的实例对象。

```
# 定义一个Person类
class Person:
    # 初始化方法（给实例添加属性）
    def __init__(self, name, age, gender):
        self.name = name
        self.age = age
        self.gender = gender

    # 自定义方法（给实例添加行为）
    # speak方法收到的参数是：调用speak方法的实例对象（self）、其它参数
    # speak方法只有一份，保存在Person类身上的，所有Person类的实例对象，都可以调用到speak方法
    def speak(self, msg):
        print(f'我叫{self.name}， 年龄是{self.age}， 性别是{self.gender}，我想说：{msg}')

# 验证一下：speak方法是存在Person类身上的
# print(Person.__dict__)

# 创建Person类的实例对象
p1 = Person('张三', 18, '男')
p2 = Person('李四', 22, '女')

# 验证一下：Person的实例对象身上是没有speak方法的
# print(p1.__dict__)
# print(p2.__dict__)

# 所有Person类的实例对象，都可以调用到speak方法
# 当执行p1.speak()的时候，查找speak方法的过程：1.实例对象自身(p1)  =>  2.实例的“缔造者”的身上(Person)
# p1.speak('好好学习')
# p2.speak('天天向上')

# 验证一下上述的查找过程
def speak():
    print('巴拉巴拉巴拉巴拉巴拉')
p1.speak = speak
print(Person.__dict__)
print(p1.__dict__)
print(p2.__dict__)
p1.speak()
```

## 实例属性 类属性

**实例属性**

**1️⃣****定义：**通过`**实例.属性名 = 值**`定义在实例身上的属性，称为：实例属性。

1. 每个实例都有自己『独立的一份』实例属性，各个实例之间是互不影响的。  
2. 实例属性只能通过`**实例.xxxx**`访问和修改，不能通过『类名』访问或修改。

```
# 定义一个Person类
class Person:
    # 初始化方法
    def __init__(self, name, age, gender):
        # 通过【实例.属性名 = 值】给实例添加的属性，就叫实例属性
        # 实例属性只能通过实例访问，不能通过类访问
        # 每个实例都有自己【独一份的】实例属性，各个实例之间是互不干扰的
        self.name = name
        self.age = age
        self.gender = gender

# 创建Person类的实例对象
p1 = Person('张三', 18, '男')
p2 = Person('李四', 22, '女')

# 实例属性只能通过实例访问，不能通过类访问
print(p1.name)
print(Person.name)
```

**类属性**

**1️⃣****定义：** 在类中直接写赋值语句（例如：`a = 100`），就会在类身上添加一个`a`属性，值为 `100`，此时的`a`就是『类属性』，它属于类本身，由类所拥有，并且该类创建出来的**所有实例对象**，都能去访问`a`属性。

```
1. 所有实例访问的，都是同一个类属性，所以类属性通常用于：存放公共数据。
2. 类属性即可以通过『类』访问，也可以『实例』访问。
```

```
# 定义一个Person类
class Person:
    # max_age、planet 他们都是类属性，类属性是保存在类身上的
    # 类属性可以通过类访问，也可以通过实例访问
    # 类属性通常用于保存：公共数据
    max_age = 120
    planet = '地球'

    # 初始化方法
    def __init__(self, name, age, gender):
        # 给实例添加属性
        self.name = name
        self.gender = gender
        # 限制age的最大值
        if age <= Person.max_age:
            self.age = age
        else:
            print(f'年龄超出范围了，已经将年龄设置为最大值：{Person.max_age}')
            self.age = Person.max_age

# 验证一下：类属性是保存在类身上的
# print(Person.__dict__)

# 创建Person类的实例对象
p1 = Person('张三', 18, '男')
p2 = Person('李四', 22, '女')

# 验证一下：实例身上是没有类属性的
# print(p1.__dict__)
# print(p2.__dict__)


# 验证一下：类属性可以通过类访问，也可以通过实例访问
# print(Person.max_age)
# print(p1.max_age)  # 查找max_age的过程：1.实例自身(p1)  => 2.实例的“缔造者”(Person)
# print(p2.planet)

# 测试一下年龄超出范围
# p3 = Person('王五', 170, '女')
# print(p3.__dict__)
```

##  实例方法、类方法、静态方法