# 学习目标

1. 配置环境 HelloWorld

2. 各种工具开发

3. 进制 字符 

4. 什么是数据类型 数据类型转换

5. 什么是基础数据类型 引用数据类型 抽象数据类型

6. (算数  复制 逻辑 关系 三元 )运算符 位运算

7. 语句

8. break、continue、return

9. 会使用Scanner和Random

10. 会使用if

11. 会使用for循环,while循环,嵌套循环

12. 会使用switch以及知道case的穿透性

       

# HelloWorld

## javaSE

![JavaSE课程内容介绍.png](http://yanxuan.nosdn.127.net/d949eb10d88deda3de62d23d454ddd4e.png)

JavaSE知识模块介绍

* **第一部分：计算机编程语言核心结构：**`数据类型`、`运算符`、`流程控制`、`数组`、…  

* **第二部分：Java面向对象核心逻辑：**`类和对象`、`封装`、`继承`、`多态`、`抽象`、`接口`、…

* **第三部分：JavaSE核心高级应用：**`集合`、`I/O`、`多线程`、`网络编程`、`反射机制`、…

* **第四部分：Java新特性：**`Lambda表达式`、`函数式编程`、`新Date/Time API`、`接口的默认、静态和私有方法`、…  

* **第五部分：MySQL/JDBC核心技术：**`SQL语句`、`数据库连接池`、`DBUtils`、`事务管理`、`批处理`、

## 基础知识

java语言基础 

原始数据类型                数据类型就是一组数据和对其所能进行的操作的集合

语句   声明 赋值 条件 循环 调用 返回

数组

静态方法

字符串

输入/输出

数据抽象      支持面向对象

API 模块化编程 

java表达式中 1/0 和1.0/0.0的值是什么

整数除以 0：抛异常；浮点数除以 0：得 Infinity

整数除法严格遵守数学规则，不允许除以 0；浮点数遵循 IEEE 754 标准，专门定义了无穷大、NaN，所以不会报错



Java 字节码就是 .class 文件里的内容，是 Java 源码编译后的中间代码，只能由 JVM（Java 虚拟机）执行

 整型溢出就是：数字太大，超出了整型（int/long）能存储的最大值，导致数据 “转圈” 变成负数，结果完全错误。

**一个等号 `=`**：是**赋值**，把右边的值给左边（例：`a = 5`）

**两个等号 `==`**：是**判断**，问 “**a 和 b 相等吗？**”

**true**（真）→ **a 和 b 一样大**

**false**（假）→ **a 和 b 不一样大**

if的执行语句后面要加

格式化输出

\n 换行

| 符号   | 作用              |
| ------ | ----------------- |
| `%d`   | 输出 **整数**     |
| `%f`   | 输出 **小数**     |
| `%.2f` | 保留 **2 位小数** |
| `%s`   | 输出 **字符串**   |

print：不换行 println：自带换行 printf：格式化，不自动换行，用来规整排版

`System.out.println('a');`

- `'a'` 是**字符**（单引号）
- 直接输出字符本身
- **输出：a**

`System.out.println('b' + 'c');`

**Java 中，两个字符 + 相加，不会拼接！而是把它们变成【ASCII 数字】相加！**

- `'b'` 的 ASCII 值 = **98**
- `'c'` 的 ASCII 值 = **99**

`System.out.println((char) ('a' + 1));`

第一步：`'a' + 1`

- `'a'` 的 ASCII = **97**
- 97 + 1 = **98**

第二步：`(char)` 强制类型转换

把数字 **98** 转回字符：

- 98 对应的字符是 **'b'**

  

- **输出：b**

**字节**

```
1.字节:计算机中存储数据的最小存储单元(计量单位),用byte或者B表示
  二进制位:用bit(比特)表示
  8个二进制位代表一个字节    
2.存储单元之间的转换:
  8bit = 1B
  1024B = 1KB
  1024KB = 1MB
  1024MB = 1GB
  1024GB = 1TB    
  PB   EB  ZB ... 
```

**常用的dos命令**

```
1.打开dos命令窗口:
  win+r -> 输入cmd -> 回车
```

| 作用                           | 命令                                                         |
| ------------------------------ | ------------------------------------------------------------ |
| 切换盘符                       | 盘符名: -> 回车<br>盘符名不区分大小写,但是计算机上必须有指定的盘符 |
| 查看当前路径下的文件或者文件夹 | dir                                                          |
| 进入到指定文件夹下             | cd 文件夹名字                                                |
| 进入到多级文件夹下             | cd 文件夹名字\文件夹名字                                     |
| 退到上一级目录                 | cd..或者 cd ..                                               |
| 直接退到磁盘位置(退到根目录)   | cd\或者cd \                                                  |
| 清屏                           | cls                                                          |
| 退出黑窗口                     | exit                                                         |
| 创建文件夹                     | mkdir 文件夹名                                               |
| 创建多级文件夹                 | mkdir 文件夹名\文件夹名                                      |
| 删除文件夹                     | rd 文件夹名<br>注意:删除的文件夹必须是空的<br>不走回收站     |
| 删除文件                       | del 文件名.后缀名<br>不走回收站                              |
| 批量删除文件                   | del *.后缀名                                                 |

> 1.如何区分正斜杠和反斜杠:
>
> /:正斜杠
>
> \:反斜杠
>
> 2.快速打开该目录对应的dos命令窗口:
>
> a.选中路径
>
> b.输入cmd -> 回车
>
> 3.按上下箭头,切换之前输入过的命令
>
> 4.在dos命令窗口中,可以输入文件夹名或者文件名的一部分,按 -> tab键 -> 自动补全文件夹名或者文件名

**jvm**

```
1.jvm(java虚拟机):java运行程序的假想计算机,主要用来运行java程序的
2.跨平台:java代码可以在不同的操作系统上运行(一次编写,到处运行)
  跨:跨越
  平台:操作系统 -> windows linux mac os        
3.关系:java程序想要在不同的操作系统上运行,实现跨平台,就需要安装不同版本的jvm 
```

  ![1698304141613.png](http://yanxuan.nosdn.127.net/40ff6958dbe9ba307f7b8abc2aa9d366.png)

**jdk jre jrm**

```
1.jdk:(Java Development Kit):java开发工具包,包含了jre
  javac 编译工具
  java 运行工具
  jdb  调试工具
  jhat 内存分析工具
  ...  
2.jre:(Java Runtime Environment):java运行环境,包含了jvm以及后面开发用到的核心类库
    
3.jdk和jre以及jvm的关系:
  jdk包含了jre,jre包含了jvm,所以我们只需要安装jdk即可
```

## 配置环境变量

**为什么要配置环境变量**

```
1.问题:将来我们需要创建一个java文件写代码,然后编译和运行的时候需要打开此java文件所在的目录,java文件的目录如果和javac以及java命令所在的bin目录不一致,那么javac和java命令就用不了了,毕竟javac和java命令在bin目录下,所以难道我们将来必须将所有的java文件都放到bin目录下吗?
 2.配置环境变量的目的:在任意路径下都可以随意使用javac和java命令进行对java代码的编译和运行
```

![](C:\Users\DELL\Desktop\笔记\javase\java基础\img\java环境变量.png)

问豆包如何安装jdk   

**1 查询jdk安装路径** 

```
C:\Program Files\Java\jdk1.8.0_391
```

**2  打开环境变量设置**
按下面步骤：右键 此电脑 → 属性点 高级系统设置  点 环境变量

**3 新建 JAVA_HOME**

填写jdk安装路径

**4 修改 Path**

```
%JAVA_HOME%\bin
%JAVA_HOME%\jre\bin
```

**5 测试修改**

```
java -version
```

![](C:\Users\DELL\Desktop\笔记\javase\java基础\img\java --version.jpg)

### 安装jdk

**1 验证jdk是否清理干净**

**1. 检查 JDK 默认安装目录**

JDK 通常安装在`C:\Program Files\Java` 或 `C:\Program Files (x86)\Java`目录。打开文件资源管理器，进入这两个路径，查看是否存在 JDK 相关文件夹 。如果有，说明可能存在残留文件。

**2. 查看用户目录下的相关文件**

JDK 和基于它运行的程序可能会在用户目录留下配置文件或缓存。比如，Maven 等构建工具会在`C:\Users\你的用户名\.m2`仓库中存放 Java 包。可进入用户目录，搜索包含 “java” 关键词的文件或文件夹，查看是否有与 JDK 相关的残留。

**3. 检查环境变量**

- 右键点击 “此电脑”，选择 “属性”，再点击 “高级系统设置”，然后在弹出窗口中点击 “环境变量”。
- 在 “系统变量” 和 “用户变量” 中，查找与 Java 相关的变量，如`JAVA_HOME`（若存在，其值通常指向 JDK 安装路径） 以及`Path`变量中是否有指向 JDK 安装目录（尤其是其中的`bin`文件夹）的路径。若有相关变量，说明系统还保留着与 JDK 相关的配置信息。

**4. 查看注册表**

按下`Win + R`键，输入 “regedit” 并回车，打开注册表编辑器。

- 导航到`HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft`和`HKEY_CURRENT_USER\SOFTWARE\JavaSoft`，查看是否存在与 JDK 相关的键值。如果有，表明注册表中存在 JDK 的残留信息 。但操作注册表需谨慎，建议提前备份，避免误删导致系统问题。

**5. 使用搜索功能**

利用 Windows 的搜索功能，在整个电脑中搜索文件名包含 “java”“jdk” 字样的文件和文件夹，查看搜索结果中是否有来自已卸载 JDK 的残留内容。

官网 https://www.oracle.com/java/technologies/downloads/#jdk21-windows

x64 Compressed Archive：即压缩归档包，属于免安装版本。
x64 Installer：一般是可执行文件（.exe 格式）。下载后双击该文件，会弹出安装向导.
x64 MSI Installer：基于 Microsoft Windows Installer 技术，文件扩展名为.msi。双击运行后，同样会有安装向导引导用户完成安装过程，部分复杂软件使用该格式安装包，可更好地管理软件组件的添加和删除等操作。

**2 下载**

https://download.oracle.com/java/21/latest/jdk-21_windows-x64_bin.exe

![](C:\Users\DELL\Desktop\笔记\javase\java基础\img\jdk.jpg)

**3 安装  傻瓜式安装**    

Compressed Archive是免安装的但是需要配置环境变量

Installe 安装

MSI installer   精细化安装

**4 验证 java --version**

![java --version.jpg](http://yanxuan.nosdn.127.net/8aae89a66f87bdac3eb674b14726ce3a.jpg)

## HelloWorld

 **开发三步骤**

```
1.编写:
  a.创建一个文本文档,将后缀名改成.java,变成一个java文件
  b.注意:我们需要将文件的后缀名显示出来      
2.编译:
  a.命令:javac java文件名.java
  b.注意:javac会将java文件编译,生成一个.class文件(字节码文件),jvm运行只认class文件     
3.运行:
  a.命令:java class文件名(不需要带后缀名了)
```

**1 编写hellworld**

```java
public class Demo01helloworld{
 public static void main(String[]args)
{
System.out.println("helloworld");
}
}
```

**2 编译**

```
javac Demo01helloworld
```

运行出现问题报错

```
F:\DaiMa\java\01>javac Demo01HelloWorld.java
Demo01HelloWorld.java:1: 错误: 类 Demo01helloworld 是公共的, 应在名为 Demo01helloworld.java 的文件中声明
public class Demo01helloworld{
```

不着急问豆包

https://www.doubao.com/chat/

给出解释在 Java 里，当一个类被声明为 `public`（公共的）时，该类的名称必须和包含它的文件名完全一致（包含大小写）。在你的代码里，公共类名为 `Demo01helloworld`，不过文件名却是 `Demo01HelloWorld.java`，类名和文件名的大小写存在差异，从而引发了编译错误。

**3 运行**

![HelloWorld.png](http://yanxuan.nosdn.127.net/e2a892385e328e87e0dce60ad34504d9.png)

 **4文件名和类名为什么要相同**

```
一个 .java 源文件里，可以写多个 class
最多只能有一个 public 类
如果一个类被 public 修饰，它的文件名必须和类名相同。

不加 public 的类为什么可以多个？
不加 public → 包访问权限
只能在当前包内使用
文件名不需要和它们一致
不会干扰 JVM 查找主类

如果是内部类 / 静态内部类，不受此限制：
```

## 其他

### 注释

```
1.概述:对代码的解释说明
2.分类:
  a.单行注释:
    //注释内容

  b.多行注释:
    /*
      注释内容
    */

  c.文档注释:
    /**
      注释内容
    */
```

```
//单行注释 class后面的名字要和java文件名一致
public class Demo01HelloWorld{
    /*
	  多行注释:
	    main是一个方法,是程序的入口,jvm运行程序要找main当入口
		执行程序
	*/
	public static void main(String[] args){
	    /**
		  文档注释:
		    下面的语句是输出语句
		*/
		System.out.println("HelloWorld");
	}
}
```

### javadoc

**`javadoc` 命令的基本用法**

```sh
javadoc [选项] [包名] [源文件名]
```

- `-d <directory>`：指定生成的文档输出目录。
- `-author`：在生成的文档中包含作者信息。
- `-version`：在生成的文档中包含版本信息。

```java
/**
  此类是一个java的入门程序
  @author zhuxioayi
  @version v1.0
*/
public class Demo02HelloWorld{
	/**
	  main是一个方法,是程序的入口
	  jvm执行java代码,都是从main方法开始执行
	*/
	public static void main(String[] args){
		System.out.println("helloworld");
	}
}
```

```
javadoc -d a -author -version Demo02helloworld.java
```

![javadoc.jpg](http://yanxuan.nosdn.127.net/9dfbb5177eada381e798570e20f13b90.jpg)

如何执行命令报错的话

```
'javadoc' 不是内部或外部命令，也不是可运行的程序
或批处理文件。
```

问豆包该如何解决

给出的解决办法是

即使你已经安装了 JDK，但如果没有正确配置环境变量，系统仍然无法找到 `javadoc` 命令。配置一下环境变量在path下添加你jdk,bin目录的信息

### **详解**

**第一个Java程序中每一句话的解释以及注意事项**

```java
/*
  1.public class Demo03HelloWorld:定义一个类
  2.class:代表的就是类,类是java程序最基本的组成单元,所有代码都需要在类中写
  3.class后面跟的名字叫做类名,类名要和java文件名保持一致
*/
public class Demo03HelloWorld{
	/*
	  public static void main(String[] args)
	  叫做main方法,是程序的入口
	  jvm执行代码,会从main方法开始执行
	*/
	public static void main(String[] args){
		//打印语句(输出语句),会将我们想要输出的内容打印到控制台上
		System.out.println("helloworld");
	}
}
```

```java
注意事项:
  1.类名要和java文件名保持一致
  2.程序中的标点符号必须是英文的  
  3.不要将main写成mian
  4.System和String的首字母s要大写
  5.每个单词写完来个空格增强代码的可读性
  6.括号要一对一对的写
  7.代码写完语句用;代表结束了,一句话来个分号,证明这是一个单独的语句    
```

**System.out.println()**

如果是**基本类型** → 直接输出值

 如果是**String**→ 输出字符串内容

 如果是**对象** → 调用 toString () 

如果是**数组** char [] → 输出字符内容 

**其他数组** → 输出地址  

如果是 **null** → 输出字符串 "null"



**System.out.println()**

```
System.out.println(" "+ age)  " "里是输出的内容  + 是拼接 
```



### 关键字

![关键字.png](http://yanxuan.nosdn.127.net/b4398230e5c039a1d86f9f78e0a9caf5.png)

### println和print区别

```java
相同点:都是输出语句
不同点:
  a.println:输出之后自带换行效果
  b.print:输出之后不带换行效果    
```

```java
public class Demo05HelloWorld{
	public static void main(String[] args){
		//System.out.println("床前明月光");
		//System.out.println("疑是地上霜");
		//System.out.println("举头望明月");
		//System.out.println("低头思故乡");
		System.out.print("床前明月光");
		System.out.print("疑是地上霜");
		System.out.print("举头望明月");
		System.out.print("低头思故乡");
		
	}
}

```

### **编码**

```
1.编码:保存数据的过程就是编码的过程
2.解码:读数据的过程就是解码的过程
3.注意:
  a.编码和解码遵守的编码规范必须是一样的
  b.常见的两个编码规范:
    GBK:专门为我们中文所设计的编码
        ANSI代表的是GBK       
        一个中文汉字在GBK中占2个字节        
    UTF-8:一个中文汉字在UTF-8中占3个字节       
  c.dos命令窗口默认编码:GBK 
```

# 开发工具 

## idea

官网下载https://www.jetbrains.com.cn/idea/download/?section=windows

破解 https://www.exception.site/essay/how-to-free-use-intellij-idea-2019-3

```
1.概述:开发工具
2.特点:
  a.idea是java写的,所以本地上必须有正确的jdk环境(JAVA_HOME)
  b.idea自动保存
  c.不用我们打开dos命令窗口执行javac和java命令
  d.idea有强大的快捷键,生成代码,纠错等
  e.idea会自动管理我们写的代码      
3.相关开发工具:eclipse myeclipse  vscode     
4.作用:加速我们的开发
```

![idea目录结构.png](http://yanxuan.nosdn.127.net/e40e5452568e647ef1fc545a75208659.png)

快捷方法

```
1.生成main方法:输入main -> 回车
2.生成输出语句:sout -> 回车
3.将变量名放到输出语句中:
a.变量名.sout
b.变量名.soutv -> 带字符串平拼接格式的输出方式-> 输出格式好看
```

**7.1设置字体**

```java
file-settings
```

![字体设置.png](http://yanxuan.nosdn.127.net/b93f3be2ef5007871e09eefdca549564.png)

**设置提示的快捷键**

```java
file-settings
```

![快捷键.png](http://yanxuan.nosdn.127.net/d177fae64b759c342f04f837da84525a.png)

![注释.png](http://yanxuan.nosdn.127.net/3d63cd96ea9f4d3d0a382e95baee2407.png)

**快捷键**

| 快捷键               | 功能                                   |
| -------------------- | -------------------------------------- |
| `Alt+Enter`          | 导入包，自动修正代码(重中之重)         |
| `Ctrl+Y`             | 删除光标所在行                         |
| `Ctrl+D`             | 复制光标所在行的内容，插入光标位置下面 |
| `Ctrl+Alt+L`         | 格式化代码                             |
| `Ctrl+/`             | 单行注释                               |
| `Ctrl+Shift+/`       | 选中代码注释，多行注释，再按取消注释   |
| `Alt+Shift+上下箭头` | 移动当前代码行                         |

> 先写等号右边的,可以自动生成等号左边的变量
>
> <img src="F:/资料/java/尚硅谷2024新版Java基础/尚硅谷2024新版Java基础（上）/笔记/day03_idea_运算符/img/1698662119415.png" alt="1698662119415" style="zoom:80%;" />

或者

> <img src="F:/资料/java/尚硅谷2024新版Java基础/尚硅谷2024新版Java基础（上）/笔记/day03_idea_运算符/img/1698662152111.png" alt="1698662152111" style="zoom:80%;" />

**出现的问题**

```java
1.在运行代码时会出现"找不到对应的发行源"或者"不支持发行版本"或者"无效的发行源版本",证明本地jdk版本和idea中的language level不匹配
  所以要匹配版本
  file->project Structure->点击project->引入本地jdk->project level选择对应的版本
2.没有out路径的问题
  out路径是专门存放idea自动编译生成的.class文件的
  所以需要指明out路径    
3.src是灰色的,对着src,右键,选项中没有java class或者package    
4.刚使用,jdk没有配置
```

![idea.png](http://yanxuan.nosdn.127.net/deae2c5e6a3a981bafaf52d4b3f55239.png)

![idea1.png](http://yanxuan.nosdn.127.net/0d930008d713e7d974a3967a2b2d4c9a.png)

设置为中文

**插件安装（推荐）**

1. **打开设置**
   点击菜单栏 **File** > **Settings**（或使用快捷键 `Ctrl+Alt+S`）。

2. **安装中文语言包**

   - 在左侧选择 **Plugins**，进入 **Marketplace** 标签页。
   - 在搜索框输入 **Chinese**，找到官方提供的 **Chinese (Simplified) Language Pack** 插件。

   ![idea.chinese.jpg](http://yanxuan.nosdn.127.net/62a3f2bee9055dece273afe377869896.jpg)

   - 点击 **Install** 安装，完成后点击 **Restart IDE** 重启 IDEA。

## VSCod

## Trae

# 基础

## 语句

**声明语句**：定义变量或方法的数据类型与名称，分配内存空间，暂不赋值。

**赋值语句**：通过等号将具体数据存入已声明的变量，修改变量存储的值。左侧必须是单个变量，右侧可以是能够得到相应类型的值的任意表达式

**条件语句**：依据布尔判断结果，选择性执行不同分支的代码块。改变执行流程

**循环语句**：在条件成立时重复执行一段代码，条件不满足就结束循环。表达式为假时什么也不做，为真时执行代码，再次检查布尔表达式的值，任然为真，再次执行代码

**调用语句**：通过方法名传入实参，跳转执行目标方法内部代码。

**返回语句**：使用 return 终止当前方法，并把运算结果返还给方法调用处

break 立即从循环中退出

continue 立即开始下一轮循环

程序是有一系列 上述语句组成的，代码结构都是嵌套的

如果条件语句或循环语句的代码片段只有一条语句，代码的花括号可以省略不写



字符串是由一串字符组成的   字符串拼接 +

**stdout 是操作系统层面的标准输出设备（概念）**，是程序默认向外输出数据的通道。

stdout 不止 Java 能用，C/C++、Python 等所有编程语言都共用系统标准输出

System.out 本质就是 Java 封装好、绑定系统 stdout 的输出对象。

## 常量变量

### 常量

```
1.概述:在代码的运行过程中,值不会发生改变的数据
2.分类:
  整数常量:所有整数
  小数常量:所有带小数点的
          2.5  1.5  2.0
  字符常量:带单引号的 ''  单引号中必须有且只能有一个内容
          '1'(算)   '11'(不算)   ''(不算)  'a1'(不算)
          ' '(算)   '  '(两个空格不算)
          '写一个tab键'(算)      
  字符串常量:带双引号的 ""  双引号中内容随意
           ""   "helloworld"
  布尔常量:true(真) false(假) -> 这两个单词不要加双引号
          "true"(这样写属于字符串,不属于布尔常量)      
  空常量:null  代表的是数据不存在  
空常量 只能赋给引用类型
可以给：类、接口、数组、String
不能给：int char boolean 等基本类型
3. 容易混淆的 “空”
空常量：null
空字符串：""
空数组：new char[0]
数字 0：0
```

```
public class Demo01Constant{
	public static void main(String[] args){
		//整数常量
		System.out.println(1);
		System.out.println(-1);		
		//小数常量
		System.out.println(1.5);
		System.out.println(1.0);		
		//字符常量   单引号中必须有且只能有一个内容
		System.out.println('1');
		//System.out.println('11');错误
		System.out.println(' ');//一个空格算一个内容
		//System.out.println('    ');//四个空格算四个内容,所以不属于字符常量
		System.out.println('	');//tab键算一个内容		
		//字符串常量
		System.out.println("本人是尚硅谷第一帅的男人");
		System.out.println("");		
		//布尔常量
		System.out.println(true);
		System.out.println(false);		
		//空常量 不能直接使用
		//System.out.println(null);
	}
}
```

### 变量

声明 初始化   int i =1显示赋值

隐示赋值 

**++i 是前置自增**：变量先执行 +1 操作，再把新值用于运算。

**i++ 是后置自增**：先把变量当前的原值拿去运算，运算完再执行 +1。

```
int i = 5;
int a = i++; // a=5，i先赋值给a，之后i变成6

int j = 5;
int b = ++j; // b=6，j先变成6，再赋值给b000
```



| 数据类型     | 关键字         | 内存占用 | 取值范围                                                     |
| :----------- | :------------- | :------- | :----------------------------------------------------------- |
| 字节型       | byte           | 1个字节  | -128 至 127  定义byte变量时超出范围,废了                     |
| 短整型       | short          | 2个字节  | -32768 至 32767                                              |
| 整型         | int（默认）    | 4个字节  | -2^31^ 至 2^31^-1  正负21个亿<br>-2147483648——2147483647     |
| 长整型       | long           | 8个字节  | -2^63^ 至 2^63^-1   19位数字<br>-9223372036854775808到9223372036854775807 |
| 单精度浮点数 | float          | 4个字节  | 1.4013E-45 至 3.4028E+38                                     |
| 双精度浮点数 | double（默认） | 8个字节  | 4.9E-324 至 1.7977E+308                                      |
| 字符型       | char           | 2个字节  | 0 至 2^16^-1                                                 |
| 布尔类型     | boolean        | 1个字节  | true，false(可以做判断条件使用)                              |

```
1.变量的数据类型:
  基本数据类型:4类8种
      整型:byte short int long
      浮点型:float double
      字符型:char
      布尔型:boolean
          
  引用数据类型: 类 数组 接口 枚举 注解      
2.概述:在代码的运行过程中,值会随着不同的情况而随时发生改变的数据  
3.作用:一次接收一个数据
       将来定义一个变量,接收一个值,后续可能会根据不同的情况对此值进行修改,此时可以用变量    
4.定义:
  a.数据类型 变量名 = 值;
  b.数据类型 变量名;
    变量名 = 值;
  c.连续定义三个相同类型的变量
    数据类型 变量名1,变量名2,变量名3;
    变量名1 = 值;
    变量名2 = 值;
    变量名3 = 值;
    比如:int i,j,k;
         i = 10;
         j = 20; 
         k = 30;
    数据类型 变量名1 = 值,变量名2 = 值,变量名3 = 值;
    比如: int i = 10,j = 20,k = 30;
    正确读法:先看等号右边的,再看等号左边的 -> 将等号右边的数据赋值给等号左边的变量
            哪怕等号右边有运算,我们都得先将等号右边的运算算出一个值来,最后赋值给等号左边的变量     5.注意:
  a.字符串不属于基本数据类型,属于引用数据类型,用String表示
    String是一个类,只不过字符串在定义的时候可以和基本数据类型格式一样      
6.float和double的区别:
  a.float的小数位只有23位二进制,能表示的最大十进制为2的23次方(8388608),是7位数,所以float型代表的小数,小数位能表示7位      
  b.double的小数位只有52位二进制,能表示的最大十进制为(4 503 599 627 370 496),是16位数,所以double型代表的小数,小数位能表示出16位          
7.切记:将来开发不要用float或者double直接参与运算,因为直接参与运算会有精度损失问题   
8. \代表转义字符
 转义字符:  \	  
		  可以这样简单理解:
		    a.将普通字符转成具有特殊含义的字符
			b.将具有特殊含义的字符转成普通字符
   因为 \ 在 Java 字符串里是 “转义字符”，它自带特殊功能，所以要表示一个普通的 \，必须写成 \\
   表示路径时      String path = "E:\\01_javase_video\\code";
   
```

```java
public class Demo03Var{
	public static void main(String[] args){
		//byte
		byte num1 = 100;
		System.out.println(num1);
		
		//short
		short num2 = 1000;
		num2 = 1001;
		System.out.println(num2);
		
		//int  整数的默认类型
		int num3 = 10000;
		num3 = 1;
		System.out.println(num3);
		
		//long -> 定义long型的变量后面加个L
		long num4 = 10L;
		System.out.println(num4);
		
		//float -> 定义float型变量的时候后面加个F
		float num5 = 2.5F;
		System.out.println(num5);
		
		//double -> 小数的默认类型
		double num6 = 2.5;
		System.out.println(num6);
		
		//char
		char num7 = 'A';
		System.out.println(num7);
		
		//boolean
		boolean num8 = true;
		boolean num9 = false;
		
		/*
		   num9 = false
		   num8 = num9 -> 将num9的值赋值给num8 -> 相当于num8 = false
		*/
		num8 = num9;
		System.out.println(num8);
		
		//String -> 是一个引用数据类型,属于类的一种,但是定义和基本类型一致
		String name = "金莲";
		System.out.println(name);
	}
}
```

```java
public class Demo04Var{
	public static void main(String[] args){
		int num1 = 10;
		int num2 = 3;
		
		int sum = num1+num2;
		System.out.println(sum);//13
		
		int sub = num1-num2;
		System.out.println(sub);//7
		
		//System.out.println(num1*num2);//30
		int mul = num1*num2;
		System.out.println(mul);//30
		
		/*
		  由于/前后都是整数,结果取整数部分,结果还赋值给一个整数变量
		*/
		int div = num1/num2;
		System.out.println(div);//3
		
		double div1 = num1/num2;//3.0
		System.out.println(div1);
	}
}
```

```java
public class Demo05Var{
	public static void main(String[] args){
		/*
		  转义字符:  \
		  
		  可以这样简单理解:
		    a.将普通字符转成具有特殊含义的字符
			b.将具有特殊含义的字符转成普通字符
		
		*/
		
		
		/*
		   n:普通字符
		   \n:换行符
		*/
		
		System.out.print("春眠不觉晓\n");
		System.out.print("处处闻啼鸟\n");
		System.out.print("夜来风雨声\n");
		System.out.print("花落知多少\n");
		
		/*
		   t:普通字符
		   \t:制表符 -> 就是tab键
		*/
		System.out.println("本人\t是尚硅谷第一帅");
		
		
		/*
		  用String表示一个路径
		  在java中两个\\代表一个\
		*/
		
		String path = "E:\\01_javase_video\\code";
		System.out.println(path);
	}
}
```

```java
public class Demo06Var{
	public static void main(String[] args){
		float a = 10;
		float b = 3;
		
		float result = a/b;
		System.out.println(result);//3.3333333
		
		
		double c = 10;
		double d = 3;
		double result02 = c/d;
		System.out.println(result02);//3.3333333333333335
		
		
		float x = 3.55F;
		float y = 2.12F;
		
		float result03 = x-y;
		System.out.println(result03);//1.4300001
		
	}
}
```

**2.变量使用时的注意事项**

```java
1.变量不初始化(第一次赋值)不能直接使用
2.在同一个作用域(一对大括号就是一个作用域)中不能定义重名的变量 
3.不同作用域中的数据尽量不要随意互相访问
  在小作用域中能直接访问大作用域中的变量
  在大作用域中不能直接访问小作用域中的变量
```

```java
public class Demo07Var{
	public static void main(String[] args){
		int i = 10;
		System.out.println(i);
		
		int j;
		
		j = 10;
		System.out.println(j);
		
		int k = 10;
		//int k = 20;//只要是变量名前带具体的数据类型就是重新定义
		System.out.println(k);
		
		{
		   int x = 1000;
           System.out.println(k);		   
		}
		
		//System.out.println(x);	
		
	}
}
```

### **练习**

```java
定义一个人类,用变量表示 姓名 性别 年龄 身高 体重
```

```java
public class Demo08VarPerson{
	public static void main(String[] args){
		//姓名
		String name = "张三";
		//性别
		char sex = '男';
		//年龄
		int age = 20;
		//身高
		double height = 175.5;
		//体重
		double weight = 145.5;
		
		System.out.println(name);
		System.out.println(sex);
		System.out.println(age);
		System.out.println(height);
		System.out.println(weight);
	}
}
```

### 标识符

1.概述:咱们给类,方法,变量取的名字
2.注意:
  a.硬性规定(必须遵守)
    标识符可以包含"英文字母","数字","$和_"
    标识符不能以数字开头  int i1 = 100(正确)  int 1i = 100(错误)
    标识符不能是关键字  int static = 100(错误)   int public = 100(错误)
  b.软性建议(可遵守可不遵守,但是建议遵守)
    给类取名字:遵循大驼峰式 -> 每个单词首字母大写
    给方法和变量取名字:遵循小驼峰式 -> 从第二个单词开始往后首字母大写
    见名知意

## 类型转换

```
1.什么时候发生类型转换:
  a.等号左右两边类型不一致
  b.不同类型的数据做运算      
2.分类:
  a.自动类型转换
    将取值范围小的数据类型赋值给取值范围大的数据类型 -> 小自动转大
    取值范围小的数据类型和取值范围大的数据类型数据做运算 -> 小自动转大      
  b.强制类型转换
    当将取值范围大的数据类型赋值给取值范围小的数据类型 -> 需要强转      
3.基本类型中按照取值范围从小到大排序:
  byte,short,char -> int -> long -> float -> double 
```

类型转换是把一种数据类型的值，转换成另一种数据类型的过程

布尔类型 boolean 不能和任何数字类型进行类型转换。

表达式运算时，小类型会自动提升为大类型，再进行计算。

引用类型也存在类型转换，主要发生在继承和多态场景中。

###  1.自动类型转换

```java
将取值范围小的数据类型赋值给取值范围大的数据类型 -> 小自动转大。Java 自动完成，不会丢失数据。
自动转换规则：byte → short → int → long → float → double。
```

```java
public class Demo09DataType{
	public static void main(String[] args){
		/*
		  等号右边是整数,整数默认类型为int
		  等号左边是long型的变量
		  
		  将取值范围小的数据类型赋值给取值范围大的数据类型,发生了自动类型转换
		*/
		long num1 = 100;
		System.out.println(num1);		
		int i = 10;
		double b = 2.5;		
		/*
		   double = int+double
		   double  = double+double
		   int自动提升为了double,发生了自动类型转换
		*/
		double sum = i+b;
		System.out.println(sum);
	}
}
```

### 2.强制类型转换

```java
0.强制类型转换是大范围类型手动转小范围类型，强制类型转换可能丢失精度、数据溢出，使用时必须谨慎。
1.将取值范围大的数据类型赋值给取值范围小的数据类型
  取值范围小的数据类型 变量名 = 取值范围大的数据类型 -> 需要强转    
2.怎么强转:
  取值范围小的数据类型 变量名 = (取值范围小的数据类型)取值范围大的数据类型
```

```java
public class Demo10DataType{
	public static void main(String[] args){
		/*
		  等号右边的数据是小数,小数默认类型为double
		  等号左边的变量是float型		  
		  将取值范围大的赋值给取值范围小 -> 报错,需要强转
		*/
		//float num1 = 2.5;
		//float num1 = (float)2.5;
		float num1 = 2.5F;
		System.out.println(num1);		
	}
}
```

### 3.强转的注意事项

```java
1.不要随意写成强转的格式,因为会有精度损失问题以及数据溢出现象,除非没有办法
2.byte,short定义的时候如果等号右边是整数常量,如果不超出byte和short的范围,不需要我们自己强转,jvm自动转型   
  byte,short如果等号右边有变量参与,byte和short自动提升为int,然后结果再次赋值给byte或者short的变量,需要我们自己手动强转    
    
3.char类型数据如果参与运算,会自动提升为int型,如果char类型的字符提升为int型会去ASCII码表(美国标准交换代码)范围内去查询字符对应的int值,如果在ASCII码表范围内没有对应的int值,回去unicode码表(万国码)中找  
```

```java
public class Demo11DataType{
	public static void main(String[] args){
		//精度损失
		int i = (int)2.9;
		System.out.println(i);		
		/*
		  数据溢出
		  int型占内存4个字节,4个字节变成二进制是32位
		  
		  100个亿: 10 0101 0100 0000 1011 1110 0100 0000 0000 -> 34位二进制
		  
		  100个亿的二进制位比int型的二进制位多出来2位,此时干掉最前面的2位
		  
		  101 0100 0000 1011 1110 0100 0000 0000
		  
		  101 0100 0000 1011 1110 0100 0000 0000->1410065408
		*/
		int j = (int)10000000000L;
		System.out.println(j);//1410065408
	    System.out.println("=========================");
		byte b = 10;
		System.out.println(b);
		b = (byte)(b+1);
		System.out.println(b);
		System.out.println("=========================");
		char c = '中';
		System.out.println(c+0);//20013
	}
}
```



## 进制 位运算 运算符

### 进制

| 十进制 | 二进制 | 八进制 | 十六进制 |
| ------ | ------ | ------ | -------- |
| 0      | 0      | 0      | 0        |
| 1      | 1      | 1      | 1        |
| 2      | 10     | 2      | 2        |
| 3      | 11     | 3      | 3        |
| 4      | 100    | 4      | 4        |
| 5      | 101    | 5      | 5        |
| 6      | 110    | 6      | 6        |
| 7      | 111    | 7      | 7        |
| 8      | 1000   | 10     | 8        |
| 9      | 1001   | 11     | 9        |
| 10     | 1010   | 12     | a或A     |
| 11     | 1011   | 13     | b或B     |
| 12     | 1100   | 14     | c或C     |
| 13     | 1101   | 15     | d或D     |
| 14     | 1110   | 16     | e或E     |
| 15     | 1111   | 17     | f或F     |
| 16     | 10000  | 20     | 10       |

### 位运算

![位运算符表.png](http://yanxuan.nosdn.127.net/d0a1d31a4b0d6ff426b357e21d9143a7.png)

```java
1.符号的介绍:
  a. &(与) -> 有假则假
  b. |(或) -> 有真则真
  c. ~(非) -> 取反
  d. ^(异或) -> 符号前后结果一样为false,不一样为true
     true ^ true -> false
     false ^ false -> false
     true ^ false -> true
     false ^ true -> true      
2.   1代表true   0代表false        
3.我们要知道计算机在存储数据的时候都是存储的数据的补码,计算也是用的数据的补码 
  但是我们最终看到的结果是原码换算出来的    
  原码   反码   补码
      
4.正数二进制最高位为0;  负数二进制最高位为1
负数是以补码形式存储和参与运算的
5.如果是正数  原码 反码 补码 一致
  比如:5的原码  反码  补码一致:
      0000 0000 0000 0000 0000 0000 0000 0101 -> 因为是正数,二进制最高位为0
          
  如果是负数,原码 反码 补码不一样了
       反码是原码的基础上最高位不变,剩下的0和1互换
       补码是在反码的基础上+1
          
  比如:-9
       原码: 1000 0000 0000 0000 0000 0000 0000 1001
       反码: 1111 1111 1111 1111 1111 1111 1111 0110
       补码: 1111 1111 1111 1111 1111 1111 1111 0111
```

**（1）左移：<<**

​	**运算规则**：左移几位就相当于乘以2的几次方

​	**注意：**当左移的位数n超过该数据类型的总位数时，相当于左移（n-总位数）位

```java

```

```java
-2<<2  等于-8
快速算法: -2*(2的2次方)
```

**（2）右移：>>**

快速运算：类似于除以2的n次，如果不能整除，**向下取整**

```java
9>>2  等于2
    
快速算法: 9除以(2的2次方)
```

```java
-9>>2   等于-3
快速算法: -9除以(2的2次方)
```

**（3）无符号右移：>>>**

运算规则：往右移动后，左边空出来的位直接补0，不管最高位是0还是1空出来的都拿0补

正数：和右移一样

```java
9>>>2 等于2
```

负数：右边移出去几位，左边补几个0，结果变为正数

```java
-9>>>2
结果为:1073741821    
```

> 笔试题: 8>>>32位 -> 相当于没有移动还是8
>
> ​              8>>>34位 -> 相当于往右移动2位

**（4）按位与：&**

小技巧:将0看成为false  将1看成true

运算规则：对应位都是1才为1,相当于符号左右两边都为true,结果才为true

1&1 结果为1

1&0 结果为0

0&1 结果为0

0&0 结果为0

```java
5&3 结果1
```

![与.png](http://yanxuan.nosdn.127.net/9aae81bd118e357a69183899d8009668.png)

**（5）按位或：|**

运算规则：对应位只要有1即为1,相当于符号前后只要有一个为true,结果就是true

1|1    结果1

1|0    结果1

0|1    结果1

0|0    结果0

```
5|3    结果为7
```

![或.png](http://yanxuan.nosdn.127.net/be415da78e49ff86287d4d3abaf84e30.png)

**（6）按位异或：^**

​	运算规则：对应位一样的为0,不一样的为1

1^1  结果为0  false

1^0  结果为1 true

0^1  结果为1  true

0^0  结果0   false

```java
5^3   结果为6
```

![非.png](http://yanxuan.nosdn.127.net/b92d2f7580de9132ebd7b4a3c3de4220.png)

**（7）按位取反**

运算规则：~0就是1  

​			       ~1就是0

```java
~10   结果为-11
```

![取反.png](http://yanxuan.nosdn.127.net/3c86c0a49f317eb851d0d31e24a78f8b.png)

### 运算符

![运算符.png](http://yanxuan.nosdn.127.net/df533332a8288d1f6a00cda8b93d5754.png)

```
提示说明：
（1）表达式不要太复杂
（2）先算的使用(),记住,如果想让那个表达式先运行,就加小括号就可以了
i<(n*m)
```

*和/ 的优先级高与 + -  ！> && || （）优先



#### 1.算数运算符

| 符号 | 说明                                                         |
| ---- | ------------------------------------------------------------ |
| +    | 加法                                                         |
| -    | 减法                                                         |
| *    | 乘法                                                         |
| /    | 除法<br>如果符号前后都是整数,结果取整数部分<br>如果符号前后有一个为小数,结果就是正常小数 |
| %    | 模,取余数部分                                                |

```java
public class Demo01Arithmetic {
    public static void main(String[] args) {
        int i = 10;
        int j = 3;
        int add = i+j;//推荐使用
        System.out.println(add);//13
        System.out.println(i+j);//13

        int sub = i-j;
        System.out.println(sub);//7

        int mul = i*j;
        System.out.println(mul);//30

        int div = i/j;
        System.out.println(div);//3

        int mo = i%j;
        System.out.println(mo);//1
    }
}
```

```java
+:
  1.运算
  2.字符串拼接:任何类型的数据遇到字符串都会变成字符串,此时+就不再是运算了,而是字符串拼接,将内容直接往后拼接    
 
```

```java
public class Demo02Arithmetic {
    public static void main(String[] args) {
        int i = 10;
        int j = 3;
        System.out.println(i+j+"");//13
        System.out.println(i+j+""+1);//131
        System.out.println(i+""+j);//103

        System.out.println("i和j相加只和为:"+(i+j));
    }
}
```

**1.2.自增自减运算符(也算算数运算符的一种)**

```java
1.格式:
  变量++ -> 后自加
  ++变量 -> 前自加
  变量-- -> 后自减
  --变量 -> 前自减    
  自增和自减只变化1      
2.使用:
  a.单独使用: ++ -- 单独为一句,没有和其他的语句掺和使用
    i++;
    符号前在在后都是先运算       
  b.混合使用: ++ -- 和其他的语句掺和使用了(比如:输出语句,赋值语句)
    符号在前:先运算,在使用运算后的值
    符号在后:先使用原值,使用完毕之后,自身再运算
      
```

```java
public class Demo03Arithmetic {
    public static void main(String[] args) {
        int i = 10;
        //i++;
        ++i;
        System.out.println("i = " + i);

        System.out.println("==================");

        int j = 100;
        int result01 = ++j;
        System.out.println("result01 = " + result01);//101
        System.out.println(j);//101

        System.out.println("==================");
        int k = 10;
        int result02 = k++;
        System.out.println("result02 = " + result02);
        System.out.println(k);

        System.out.println("==================");

        int z = 100;
        System.out.println(z++);
        System.out.println(z);

        System.out.println("==================");

        int x = 10;
        int y = 20;

        /*
           10+19 = 29
           29+12 = 41

           以后开发肯定不会这么写
         */

        int result03 = x++ + --y + ++x;
        System.out.println("result03 = " + result03);

        System.out.println("=======================");

        int c = 10;
        c = c++;
        System.out.println(c);//10
        System.out.println(c);//10

    }
}
```

#### 2.赋值运算符

```java
1.基本赋值运算符:
  =  -> 先看等号右边的,再将右边的数据赋值给等号左边的变量     
2.复合赋值运算符:
  +=:
    int i = 10;
    i+=2 -> i = i+2
       
        
  -=
  *=
  /=  : 取整数部分
  %=  : 取余数部分  
      
3.注意:byte short 遇到复合赋值运算符,jvm会自动转型      
```

```java
public class Demo01Assignment {
    public static void main(String[] args) {
        int i = 10;
        i+=2;//i = i+2
        System.out.println(i);
    }
}
```

```java
public class Demo02Assignment {
    public static void main(String[] args) {
        byte b = 10;
        //b = (byte)(b + 1);
        b+=1;//b = b+1
        System.out.println(b);
    }
}
```

#### **3.关系运算符(比较运算符)**

```java
1.结果:boolean型 -> 要么是true,要么是false
2.作用:做条件判断使用    
```

| 符号 | 说明                                                       |
| ---- | ---------------------------------------------------------- |
| ==   | 如果符号前后相等为true;否则为false                         |
| >    | 如果符号前的数据大于符号后的数据为true,否则为false         |
| <    | 如果符号前的数据小于符号后的数据为true,否则为false         |
| >=   | 如果符号前的数据大于或者等于符号后的数据为true,否则为false |
| <=   | 如果符号前的数据小于或者等于符号后的数据为true,否则为false |
| !=   | 如果符号前后不相等为true;否则为false                       |

```java
public class Demo01Compare {
    public static void main(String[] args) {
        int i = 10;
        int j = 20;

        boolean result01 = i == j;
        System.out.println("result01 = " + result01);//false
        System.out.println(i>j);//false
        System.out.println(i<j);//true
        System.out.println(i>=j);//false
        System.out.println(i<=j);//true
        System.out.println(i!=j);//true

    }
}
```

#### 4.逻辑运算符

```java
 1.作用:连接多个boolean结果的
 2.结果:boolean型结果    
```

| 符号        | 说明                                                         |
| ----------- | ------------------------------------------------------------ |
| &&(与,并且) | 有假则假,符号前后有一个结果为false,整体就是false             |
| \|\|(或者)  | 有真则真,符号前后有一个结果为true,整体就是true               |
| !(非,取反)  | 不是true,就是false;不是false,就是true                        |
| ^(异或)     | 符号前后结果一样为false;不一样为true<br>true^true -> false<br>true^false -> true<br>false^true -> true<br>false^false -> false |

```java
public class Demo01Logic {
    public static void main(String[] args) {
        int i = 10;
        int j = 20;
        int k = 10;

        boolean result01 = (i>j) && (i==k);
        System.out.println("result01 = " + result01);//false

        boolean result02 = (i>j) || (i==k);
        System.out.println("result02 = " + result02);//true

        boolean result03 = (i>j) ^ (i==k);//false ^ true
        System.out.println("result03 = " + result03);//true

        boolean result04 = !(i>j) ;
        System.out.println("result04 = " + result04);//true
    }
}

```

> | 符号 | 说明                                                         |
> | ---- | ------------------------------------------------------------ |
> | &    | 1.单与,如果前后都是布尔型,有假则假,但是如果符号前为false,符号后的判断会继续执行<br>2.如果该符号前后都是数字,看做是位运算符 |
> | &&   | 1.双与,有假则假,但是有短路效果,如果符号前为false,符号后的判断就不会执行了 |
> | \|   | 1.单或,如果前后都是布尔型,有真则真,但是如果符号前为true,符号后的判断会继续执行<br>2.如果该符号前后都是数字,看做是位运算符 |
> | \|\| | 1.双或,有真则真,但是有短路效果,如果符号前为true,符号后的判断就不会执行了 |
>
> ```java
> public class Demo02Logic {
>  public static void main(String[] args) {
>      int a = 10;
>      int b = 20;
>      //boolean result01 = (++a>100)&(++b>10);
>      //boolean result01 = (++a > 100) && (++b > 10);
> 
>      //boolean result01 = (++a<100)|(++b>10);
>      boolean result01 = (++a<100)||(++b>10);
>      System.out.println("result01 = " + result01);
>      System.out.println("a = " + a);
>      System.out.println("b = " + b);
>  }
> }
> ```
>
> 问题:定义一个变量(a),随意给一个值,判断这个变量接收的值是否在1-100之间
>
> 1<=a<=100 -> 错误,这是数学写法
>
> i>=1 && i<=100 -> java写法,用逻辑运算符拼接多个判断

#### 5.三元运算符

```java
1.格式:
  boolean表达式?表达式1:表达式2     
2.执行流程:
  先判断,如果是true,就走?后面的表达式1,否则就走:后面的表达式2
```

### 表达式

**运算符**是 Java 中表示运算的符号，比如 +、-、*、/、=、>、<。

**表达式**是由运算符、常量、变量组合而成，能计算出一个确定结果的式子。

运算符是动作符号，表达式是运算整体；运算符在表达式里工作，表达式靠运算符产生结果

## 流程控制

**键盘录入_Scanner**

```java
1.概述:是java定义好的一个类
2.作用:将数据通过键盘录入的形式放到代码中参与运行 
3.位置:java.util
4.使用:
  a.导包:通过导包找到要使用的类 -> 导包位置:类上
    import java.util.Scanner -> 导入的是哪个包下的哪个类      
  b.创建对象
    Scanner 变量名 = new Scanner(System.in);
  c.调用方法,实现键盘录入
    变量名.nextInt() 输入整数int型的
    变量名.next() 输入字符串  String型的  
```

```java
public class Demo01Scanner {
    public static void main(String[] args) {
        //创建对象
        Scanner sc = new Scanner(System.in);
        //录入int型整数
        int data1 = sc.nextInt();
        System.out.println("data1 = " + data1);
        //录入String型字符串
        String data2 = sc.next();
        System.out.println("data2 = " + data2);
    }
}

====================================================
public class Demo02Scanner {
    public static void main(String[] args) {
        //创建对象
        Scanner sc = new Scanner(System.in);
        //录入int型整数
        int old1 = sc.nextInt();
        int old2 = sc.nextInt();
        int old3 = sc.nextInt();
        int temp = old1>old2?old1:old2;
        int max = temp>old3?temp:old3;
        System.out.println(max);
    }
}
    
```

> ```java
> 变量名.next():录入字符串 -> 遇到空格和回车就结束录入了
> 变量名.nextLine():录入字符串 -> 遇到回车就结束录入了
> ```
>
> ```java
> public class Demo03Scanner {
>  public static void main(String[] args) {
>      Scanner sc = new Scanner(System.in);
>      String data1 = sc.next();
>      String data2 = sc.nextLine();
>      System.out.println(data1);
>      System.out.println(data2);
>  }
> }
> ```
>
> ```java
> Exception in thread "main" java.util.InputMismatchException -> 输入类型不匹配异常
> 	at java.base/java.util.Scanner.throwFor(Scanner.java:939)
> 	at java.base/java.util.Scanner.next(Scanner.java:1594)
> 	at java.base/java.util.Scanner.nextInt(Scanner.java:2258)
> 	at java.base/java.util.Scanner.nextInt(Scanner.java:2212)
> 	at com.atguigu.a_scanner.Demo04Scanner.main(Demo04Scanner.java:8)
> 原因:录入的数据和要求的数据类型不一致    
> ```

**Random随机数**

> 学习Random和学习Scanner方式方法一样

```java
1.概述:java自带的一个类
2.作用:可以在指定的范围内随机一个整数
3.位置:java.util
4.使用:
  a.导包:import java.util.Random
  b.创建对象:
    Random 变量名 = new Random()
  c.调用方法,生成随机数:
    变量名.nextInt() -> 在int的取值范围内随机一个整数
```

```java
public class Demo01Random {
    public static void main(String[] args) {
        //创建对象
        Random rd = new Random();
        int data = rd.nextInt();
        System.out.println("data = " + data);
    }
}
```

```java
在指定范围内随机一个数:
nextInt(int bound) -> 在0-(bound-1)    
a.nextInt(10) -> 0-9
b.在1-10之间随机一个数: nextInt(10)+1 -> (0-9)+1 -> 1-10
c.在1-100之间随机一个数:nextInt(100)+1 -> (0-99)+1 -> 1-100
d.在100-999之间随机一个数: nextInt(900)+100 -> (0-899)+100 -> 100-999
```

```java
public class Demo02Random {
    public static void main(String[] args) {
        //创建对象
        Random rd = new Random();
        //在1-100之间随机
        int data1 = rd.nextInt(100)+1;
        System.out.println("data1 = " + data1);

        System.out.println("=====================");

        //在100-999之间随机一个数
        int data2 = rd.nextInt(900)+100;
        System.out.println("data2 = " + data2);
    }
}

```

### switch

```java
学switch  if  循环必须要先掌握定义格式,然后掌握执行流程(带一个数进去,根据执行流程观察值的变化)
```

**1.switch基本使用**

```java
1.格式:
  switch(变量){
      case 常量值1:
          执行语句1;
          break;
          
      case 常量值2:
          执行语句2;
          break;
          
      case 常量值3:
          执行语句3;
          break;
          
      case 常量值4:
          执行语句4;
          break;
          ...
      default:
          执行语句n;
          break;
  }

2.执行流程:
  用变量接收的值和下面case后面的常量值匹配,匹配上哪个case就执行哪个case对应的执行语句
  如果以上所有case都没有匹配上,就走default对应的执行语句n      
3.break关键字:代表的是结束switch语句     
4.注意:switch能匹配什么类型的数据:
  byte short int char 枚举类型 String类型  
   
```

```java
public class Demo01Switch {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("请您输入一个整数:");
        int data = sc.nextInt();
        switch (data){
            case 1:
                System.out.println("鹅鹅鹅");
                break;
            case 2:
                System.out.println("曲项向天歌");
                break;
            case 3:
                System.out.println("白毛浮绿水");
                break;
            case 4:
                System.out.println("红掌拨清波");
                break;
            default:
                System.out.println("下面没有了");
                break;

        }

    }
}
```

**2.case的穿透性**

```java
1.如果没有break,就会出现case的穿透性,程序就一直往下穿透执行,直到遇到了break或者switch代码执行完毕了,就停止了
```

```java
public class Demo02Switch {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int data = sc.nextInt();
        switch (data){
            case 1:
                System.out.println("鹅鹅鹅");

            case 2:
                System.out.println("曲项向天歌");

            case 3:
                System.out.println("白毛浮绿水");

            case 4:
                System.out.println("红掌拨清波");
                break;
            default:
                System.out.println("下面没有了");
                break;

        }
    }
}

```

<img src="F:/资料/java/尚硅谷2024新版Java基础/尚硅谷2024新版Java基础（上）/笔记/day04_流程控制/img/1698890564643.png" alt="1698890564643" style="zoom:80%;" />

```java
public class Demo03Switch {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int month = sc.nextInt();
        switch(month){
            case 12:
                System.out.println("冬季");
                break;
            case 1:
                System.out.println("冬季");
                break;
            case 2:
                System.out.println("冬季");
                break;
            case 3:
                System.out.println("春季");
                break;
            case 4:
                System.out.println("春季");
                break;
            case 5:
                System.out.println("春季");
                break;
            case 6:
                System.out.println("夏季");
                break;
            case 7:
                System.out.println("夏季");
                break;
            case 8:
                System.out.println("夏季");
                break;
            case 9:
                System.out.println("秋季");
                break;
            case 10:
                System.out.println("秋季");
                break;
            case 11:
                System.out.println("秋季");
                break;
            default:
                System.out.println("什么情况,你家有这个月份?");
        }
    }
}
```

### if

**1.if的第一种格式**

```java
1.格式:
  if(boolean表达式){
      执行语句;
  }
2.执行流程:
  先走if后面的boolean表达式,如果是true,就走if后面大括号中的执行语句,否则就不走      
3.注意:
  if后面跟的是boolean表达式,只要是结果为boolean型的,都可以放在小括号中,哪怕直接写一个true或者false
```

```java
public class Demo01If {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int data1 = sc.nextInt();
        int data2 = sc.nextInt();
        if (data1==data2){
            System.out.println("两个整数相等");
        }
    }
}
```

**2.if的第二种格式**

```java
1.格式:
  if(boolean表达式){
      执行语句1;
  }else{
      执行语句2;
  }
2.执行流程:
  a.先走if后面的boolean表达式,如果是true,就走if后面的执行语句1
  b.否则就走else后面的执行语句2    
```

```java
public class Demo02IfElse {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int data1 = sc.nextInt();
        int data2 = sc.nextInt();
        if (data1==data2){
            System.out.println("两个整数相等");
        }else{
            System.out.println("两个整数不相等");
        }
    }
}
```

**2.1 练习**

```java
任意给出一个整数，请用程序实现判断该整数是奇数还是偶数，并在控制台输出该整数是奇数还是偶数
```

```java
public class Demo03IfElse {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int data = sc.nextInt();
        if (data%2==0){
            System.out.println("偶数");
        }else{
            System.out.println("奇数");
        }
    }
}
```

**2.2练习**

```java
需求.利用if  else 求出两个数的较大值
```

```java
public class Demo04IfElse {
    public static void main(String[] args) {
        int i = 10;
        int j = 20;
        if (i>j){
            System.out.println(i);
        }else{
            System.out.println(j);
        }
    }
}

```

```java
public class Demo05IfElse {
    public static void main(String[] args) {
        int i = 10;
        int j = 20;
        int k = 30;

        //定义临时变量,接收前两个数的较大值
        int temp = 0;

        if (i>j){
            temp = i;
        }else{
            temp = j;
        }

        if (temp>k){
            System.out.println(temp);
        }else{
            System.out.println(k);
        }
    }
}
```

**2.3练习**

```java
案例：从键盘输入年份，请输出该年的2月份的总天数。闰年2月份29天，平年28天。
闰年:
 a.能被4整除,但是不能被100整除  year%4==0 && year%100!=0
 b.或者能直接被400整除  year%400==0
步骤:
  1.创建Scanner对象,调用nextInt键盘录入一个年份 year
  2.判断(year%4==0 && year%100!=0) || (year%400==0)
  3.如果条件成立,就输出闰年2月29天,否则输出平年2月28天    
```

```java
public class Demo06IfElse {
    public static void main(String[] args) {
        //1.创建Scanner对象,调用nextInt键盘录入一个年份 year
        Scanner scanner = new Scanner(System.in);
        int year = scanner.nextInt();
        //2.判断(year%4==0 && year%100!=0) || (year%400==0)
        if ((year%4==0 && year%100!=0) || (year%400==0)){
        //3.如果条件成立,就输出闰年2月29天,否则输出平年2月28天
            System.out.println("闰年2月29天");
        }else{
            System.out.println("平年2月28天");
        }
    }
}
```

**2.4练习**

```java
public class Demo07IfElse {
    public static void main(String[] args) {
        boolean num1 = false;
        boolean num2 = true;

        int i = 1;

        /*
           num1 = false
           num2 = true
           num1 = num2 -> 相当于将num2的true赋值给了num1
         */
        if (num1=num2){
            i++;
            System.out.println(i);//2
        }

        if (false){
            --i;
            System.out.println(i);
        }
    }
}

```

**3.if的第三种格式**

```java
1.格式:
  if(boolean表达式){
      执行语句1
  }else if(boolean表达式){
      执行语句2
  }else if(boolean表达式){
      执行语句3
  }...else{
      执行语句n
  }

2.执行流程:
  从if开始往下挨个判断,哪个if判断结果为true,就走哪个if对应的执行语句,如果以上所有的判断都是false,就走else对应的执行语句n
      
3.使用场景:2种情况以上的判断      
```

```java
public class Demo08ElseIf {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int data1 = sc.nextInt();
        int data2 = sc.nextInt();
       /* if (data1>data2){
            System.out.println("data1大于data2");
        }else if(data1<data2){
            System.out.println("data1小于data2");
        }else{
            System.out.println("data1等于data2");
        }*/

        if (data1>data2){
            System.out.println("data1大于data2");
        }else if(data1<data2){
            System.out.println("data1小于data2");
        }else if (data1==data2){
            System.out.println("data1等于data2");
        }
    }
}

```

```java
注意:最后一种情况,不一定非得用else,但是必须要保证所有的情况都判断了
```

**3.1.练习**

```java
需求:
 键盘录入一个星期数(1,2,...7)，输出对应的星期一，星期二，...星期日
输入  1      输出	星期一
输入  2      输出	星期二
输入  3      输出	星期三
输入  4      输出	星期四
输入  5      输出	星期五
输入  6      输出	星期六
输入  7      输出	星期日
输入  其它数字   输出      数字有误

```

```java
public class Demo09ElseIf {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int week = sc.nextInt();
        /*if (week==1){
            System.out.println("周一");
        }else if (week==2){
            System.out.println("周二");
        }else if (week==3){
            System.out.println("周三");
        }else if (week==4){
            System.out.println("周四");
        }else if (week==5){
            System.out.println("周五");
        }else if (week==6){
            System.out.println("周六");
        }else if (week==7){
            System.out.println("周日");
        }else{
            System.out.println("是不是有点大病,没有这个星期!");
        }*/

        if (week<1 || week>7){
            System.out.println("是不是有点大病,没有这个星期!");
        }else{
            if (week==1){
                System.out.println("周一");
            }else if (week==2){
                System.out.println("周二");
            }else if (week==3){
                System.out.println("周三");
            }else if (week==4){
                System.out.println("周四");
            }else if (week==5){
                System.out.println("周五");
            }else if (week==6){
                System.out.println("周六");
            }else if (week==7){
                System.out.println("周日");
            }
        }
    }
}
```

**3.2练习**

```java
根据最新的年龄段划分标准:
  0-6岁为婴幼儿
  7-12岁为少儿
  13-17岁为青少年
  18-45岁为青年
  46-69岁为中年
  69岁以上为老年
请键盘录入一个年龄,判断属于什么年龄段  
```

```java
public class Demo10ElseIf {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int age = sc.nextInt();
       /* if (age>=0 && age<=6){
            System.out.println("婴幼儿");
        }else if (age>=7 && age<=12){
            System.out.println("少儿");
        }else if (age>=13 && age<=17){
            System.out.println("青少年");
        }else if(age>=18 && age<=45){
            System.out.println("青年");
        }else if(age>=46 && age<=69){
            System.out.println("中年");
        }else if (age>69 && age<=130){
            System.out.println("老年");
        }else {
            System.out.println("年龄不太符合实际");
        }*/

        if (age<0 || age>130){
            System.out.println("年龄不太符合实际");
        }else{
            if (age>=0 && age<=6){
                System.out.println("婴幼儿");
            }else if (age>=7 && age<=12){
                System.out.println("少儿");
            }else if (age>=13 && age<=17){
                System.out.println("青少年");
            }else if(age>=18 && age<=45){
                System.out.println("青年");
            }else if(age>=46 && age<=69){
                System.out.println("中年");
            }else if (age>69 && age<=130){
                System.out.println("老年");
            }
        }
    }
}

```

> switch和if的区别:debug
>
> 1.switch:会直接跳到相匹配的case
>
> 2.if:从上到下挨个判断 -> 实际开发主要用if做判断,灵活  

### for循环

```java
什么时候使用循环语句:
  当我们发现一件事或者一段代码在反复执行,我们就可以考虑使用循环语句了
```

```java
1.格式:
  for(初始化变量;比较;步进表达式){
      循环语句 -> 哪段代码循环执行,就将哪段代码放到此处
  }
2.执行流程:
  a.先走初始化变量
  b.比较,如果是true,走循环语句,走步进表达式(初始化的变量的值进行变化) 
  c.再比较,如果还是true,继续走循环语句,走步进表达式
  d.再比较,直到比较为false,循环结束了
```

```java
public class Demo01For {
    public static void main(String[] args) {
        for(int i = 0;i<3;i++){
            System.out.println("我爱java");
        }
    }
}

```

> 快捷键: 次数.fori

**1.1练习**

```java
for循环:求1-3之间的数据和,并把求和结果输出到控制台上
1+2+3    
步骤:
  1.定义一个变量,用来接受两个数的和  sum
  2.利用for循环将1-3表示出来
  3.在循环的过程中,两两相加,将结果赋值给sum
  4.输出sum    
```

```java
public class Demo02For {
    public static void main(String[] args) {
        //1.定义一个变量,用来接受两个数的和  sum
        int sum = 0;
        //2.利用for循环将1-3表示出来
        for (int i = 1; i <= 3; i++) {
        //3.在循环的过程中,两两相加,将结果赋值给sum
            sum+=i;//sum = sum+i;
        }
        //4.输出sum
        System.out.println("sum = " + sum);
    }
}

```

**1.2练习**

```java
需求:求出1-100的偶数和
步骤:
  1.定义一个变量sum,接受两个偶数的和
  2.利用for循环将1-100表示出来
  3.判断,如果是偶数,相加,将加的结果赋值给sum
  4.输出sum    
```

```java
public class Demo03For {
    public static void main(String[] args) {
        //1.定义一个变量sum,接受两个偶数的和
        int sum = 0;
        //2.利用for循环将1-100表示出来
        for (int i = 1; i <= 100; i++) {
            //3.判断,如果是偶数,相加,将加的结果赋值给sum
            if (i % 2 == 0) {
                sum += i;
            }
        }
        //4.输出sum
        System.out.println("sum = " + sum);
    }
}

```

**1.3练习**

```java
统计一下1-100之间的偶数个数
步骤:
  1.定义一个变量count,用来计数
  2.利用for循环将1-100表示出来
  3.判断,如果是偶数,count++
  4.输出count    
```

```java
public class Demo04For {
    public static void main(String[] args) {
        //1.定义一个变量count,用来计数
        int count = 0;
        //2.利用for循环将1-100表示出来
        for (int i = 1; i <= 100; i++) {
            //3.判断,如果是偶数,count++
            if (i % 2 == 0) {
                count++;
            }
        }
        //4.输出count
        System.out.println("count = " + count);
    }
}
```

### while循环

```java
1.格式:
  初始化变量;
  while(比较){
      循环语句;
      步进表达式
  }

2.执行流程:
  a.初始化变量
  b.比较,如果是true,就走循环语句,走步进表达式
  c.再比较,如果还是true,继续走循环语句,继续走步进表达式
  d.再比较,直到比较为false,循环结束
```

```java
public class Demo01While {
    public static void main(String[] args) {
        int i = 0;
        while(i<5){
            System.out.println("我爱java,我更爱钱");
            i++;
        }
    }
}
```

```java
public class Demo02While {
    public static void main(String[] args) {
        int sum = 0;
        int i = 1;
        while(i<=3){
           sum+=i;
           i++;
        }
        System.out.println("sum = " + sum);
    }
}
```

```java
public class Demo03While {
    public static void main(String[] args) {
        int sum = 0;
        int i = 1;
        while (i <= 100) {
            if (i % 2 == 0) {
                sum += i;
            }
            i++;
        }
        System.out.println("sum = " + sum);
    }
}
```

```java
public class Demo04While {
    public static void main(String[] args) {
        int count = 0;
        int i = 1;
        while (i <= 100) {
            if (i % 2 == 0) {
                count++;
            }
            i++;
        }
        System.out.println("count = " + count);
    }
}
```

**1.1while练习**

```java
需求：世界最高山峰是珠穆朗玛峰(8844.43米=8844430毫米)，假如我有一张足够大的纸，它的厚度是0.1毫米。请问，我折叠多少次，可以折成珠穆朗玛峰的高度? 27

步骤:
  1.定义一个变量表示山峰的高度  mountain
  2.定义一个变量表示纸的厚度    paper
  3.定义一个变量表示折纸的次数  count
  4.利用while循环循环比较,如果paper<mountain 就循环对折
    paper = paper*2;
    count++;
  5.输出count
```

```java
public class Demo05While {
    public static void main(String[] args) {
        //1.定义一个变量表示山峰的高度  mountain
        int mountain = 8844430;
        //2.定义一个变量表示纸的厚度    paper
        double paper = 0.1;
        //3.定义一个变量表示折纸的次数  count
        int count = 0;
        /*4.利用while循环循环比较,如果paper<mountain 就循环对折
          paper = paper*2;
          count++;*/
        while(paper<mountain){
            paper*=2;
            count++;
        }
        //5.输出count
        System.out.println("count = " + count);
    }
}
```

#### do...while

```java
1.格式:
  初始化变量;
  do{
      循环语句;
      步进表达式
  }while(比较);

2.执行流程:
  a.初始化变量
  b.走循环语句
  c.走步进表达式
  d.判断,如果是true,继续循环,直到比较为false,循环结束
      
3.特点:
  至少循环一次
```

```java
public class Demo01DoWhile {
    public static void main(String[] args) {
        int i = 0;
        do{
            System.out.println("我爱java");
            i++;
        }while(i<5);
    }
}
```

### 循环控制关键字

```java
1.break:
  a.在switch中代表结束switch语句
  b.在循环中代表结束循环 
      
2.continue:
  结束当前本次循环,直接进入下一次循环,直到条件为false为止
```

```java
public class Demo01BreakAndContinue {
    public static void main(String[] args) {
        for (int i = 1; i <= 5; i++) {
            if (i==3){
                //结束循环
                //break;
                //结束本次循环,进入下一次循环
                continue;
            }
            System.out.println("我爱java"+i);
        }
    }
}
```

### **死循环**

```java
1.概述:
  一直循环
2.什么条件下一直循环:
  比较条件一直是true
  
```

```java
public class Demo01Endless {
    public static void main(String[] args) {
        int count = 0;
        for (int i = 0; i < 10;) {
            count++;
            System.out.println("我爱java"+count);
        }


       /* while(true){
            count++;
            System.out.println("我爱java"+count);
        }*/


    }
}
```

### 嵌套循环

```java
1.概述:循环中还有循环
2.执行流程:
  先执行外层循环,再进入内层循环,内层循环就一直循环,直到内层循环结束,外层循环进入下一次循环,直到外层循环都结束了,整体结束
```

```java
public class Demo02Nest {
    public static void main(String[] args) {
        for (int fen = 0; fen < 60; fen++) {
            for (int miao = 0; miao < 60; miao++) {
                System.out.println(fen+"分"+miao+"秒");
            }
        }
    }
}
```

```java
练习:打印矩形
```

```java
public class Demo03Nest {
    public static void main(String[] args) {
        //外层循环控制行
        for (int i = 0; i < 5; i++) {
            //内层循环控制列
            for (int j = 0; j < 5; j++) {
                System.out.print("* ");
            }
            System.out.println();
        }
    }
}
```

```java
练习:打印直角三角形
```

```java
* 
* * 
* * * 
* * * *   
```

```java
public class Demo04Nest {
    public static void main(String[] args) {
        for (int i = 1; i < 5; i++) {
            for (int j = 0;j<i;j++){
                System.out.print("* ");
            }
            System.out.println();
        }
    }
}

```

### 练习

**猜数字小游戏**

```java
1.创建Scanner和Random对象
2.调用Random中的nextInt(100)+1在1-100之间随机一个数  rdNumber
3.调用Scanner中的nextInt()方法 键盘录入一个要猜的数   scNumber
4.如果scNumber大于rdNumber,证明猜大了
5.如果scNumber小于rdNumber,证明猜小了
6.如果scNumber等于rdNumber,证明猜中了
```

```java
public class Demo01Exam {
    public static void main(String[] args) {
        //1.创建Scanner和Random对象
        Scanner sc = new Scanner(System.in);
        Random rd = new Random();
        //2.调用Random中的nextInt(100)+1在1-100之间随机一个数  rdNumber
        int rdNumber = rd.nextInt(100) + 1;
        while(true){
            //3.调用Scanner中的nextInt()方法 键盘录入一个要猜的数   scNumber
            System.out.println("请您猜一个数:");
            int scNumber = sc.nextInt();
            //4.如果scNumber大于rdNumber,证明猜大了
            if (scNumber>rdNumber){
                System.out.println("对不起,您猜大了!");
            }else if (scNumber<rdNumber){
                //5.如果scNumber小于rdNumber,证明猜小了
                System.out.println("对不起,您猜小了!");
            }else{
                //6.如果scNumber等于rdNumber,证明猜中了
                System.out.println("恭喜您,猜中了!");
                break;
            }
        }

    }
}
```

