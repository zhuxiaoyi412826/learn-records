# js

## 1 什么是js

弹窗的三种方式

alert() 弹窗

prompt() 信息输入框

confirm() 信息确认框  

console.log()在控制台上打印

### 1JS起源

![js.png](http://yanxuan.nosdn.127.net/8bf01ef1e12aa140e1c120422de2379a.png)

Javascript是一种由Netscape(网景)的LiveScript发展而来的原型化继承的面向对象的动态类型的区分大小写的`客户端脚本语言`JS是一种运行于浏览器端上的小脚本语句,可以实现网页如文本内容动,数据动态变化和动画特效等</font>,JS有 如下特点

+ 脚本语言
  + JavaScript是一种解释型的脚本语言。不同于C、C++、Java等语言先编译后执行,	JavaScript不会产生编译出来的字节码文件，而是在程序的运行过程中对源文件逐行进行解释。

+ 基于对象
  + JavaScript是一种基于对象的脚本语言，它不仅可以创建对象，也能使用现有的对象。但是面向对象的三大特性：『封装』、『继承』、『多态』中，JavaScript能够实现封装，可以模拟继承，不支持多态，所以它不是一门面向对象的编程语言。
+ 弱类型
  + JavaScript中也有明确的数据类型，但是声明一个变量后它可以接收任何类型的数据，并且会在程序执行过程中根据上下文自动转换类型。
+ 事件驱动
  + JavaScript是一种采用事件驱动的脚本语言，它不需要经过Web服务器就可以对用户的输入做出响应。
+ 跨平台性
  + JavaScript脚本语言不依赖于操作系统，仅需要浏览器的支持。因此一个JavaScript脚本在编写后可以带到任意机器上使用，前提是机器上的浏览器支持JavaScript脚本语言。目前JavaScript已被大多数的浏览器所支持。

### 2 JS 组成部分

![js核心.png](http://yanxuan.nosdn.127.net/cfc47ee3faa07e7dcc86598f1ccd213c.png)

**ECMA** 

+ 是一种由欧洲计算机制造商协会（ECMA）通过ECMA-262标准化的脚本程序语言,ECMAScript描述了语法、类型、语句、关键字、保留字、运算符和对象。它就是定义了脚本语言的所有属性、方法和对象。
+ ECMA-262第6版俗称ES6、ES2015或ES Harmony（和谐版），于2015年6月发布。**`这一版包含了大概这个规范有史以来最重要的一批增强特性。`**ES6正式支持了类、模块、迭代器、生成器、箭头函数、期约、反射、代理和众多新的数据类型。但是并不是所有的浏览器都全面支持了ES6,很多情况下我们需要将ES6的语法通过工具转换成5以后运行

**BOM编程**

+ BOM是Browser Object Model的简写，即浏览器对象模型。

+ BOM有一系列对象组成，是访问、控制、修改浏览器的属性和方法

+ BOM没有统一的标准(每种客户端都可以自定标准)。

+ BOM编程是将浏览器窗口的各个组成部分抽象成各个对象,通过各个对象的API操作组件行为的一种编程

+ BOM编程的对象结构如下

  + window 顶级对象,代表整个浏览器窗口
    + location对象        window对象的属性之一,代表浏览器的地址栏
    + history对象          window对象的属性之一,代表浏览器的访问历史
    + screen对象           window对象的属性之一,代表屏幕
    + navigator对象      window对象的属性之一,代表浏览器软件本身
    + document对象     window对象的属性之一,代表浏览器窗口目前解析的html文档

  <img src="C:\Users\DELL\Desktop\javaweb\img\js\BOM.png" style="zoom:67%;" />

**DOM编程**

+ 简单来说:DOM编程就是使用document对象的API完成对网页HTML文档进行动态修改,以实现网页数据和样式动态变化效果的编程.
+ document对象代表整个html文档，可用来访问页面中的所有元素，是最复杂的一个dom对象，可以说是学习好dom编程的关键所在。
+ 根据HTML代码结构特点,document对象本身是一种树形结构的文档对象。



![document1.png](http://yanxuan.nosdn.127.net/fb58d48c75c504780e36890eb7fc155e.png)

+ 上面的代码生成的树如下

![document2.png](http://yanxuan.nosdn.127.net/a1db66cf4f897e62fbb09ad388503c7e.png)

+ DOM编程其实就是用window对象的document属性的相关API完成对页面元素的控制的编程

### 3 JS的引入方式

**内部脚本方式引入**

+ 说明

  + 在页面中,通过一对script标签引入JS代码
  + script代码放置位置具备一定的随意性,一般放在head标签中居多

  js如何声明函数 function suprise(){}

  onclick 单击执行

  alert弹窗提示

+ 代码

``` html
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>小标题</title>
        <style>
            /* 通过选择器确定样式的作用范围 */
            .btn1 {
                display: block;
                width: 150px; 
                height: 40px; 
                background-color: rgb(245, 241, 129); 
                color: rgb(238, 31, 31);
                border: 3px solid rgb(238, 23, 66);
                font-size: 22px;
                font-family: '隶书';
                line-height: 30px;
                border-radius: 5px;
            }
        </style>
        <script>
            function suprise(){
                alert("Hello,我是惊喜")
            }
        </script>
    </head>
    <body>
        <button class="btn1" onclick="suprise()">点我有惊喜</button>
    </body>
</html>
```

+ 效果

![js01.gif](http://yanxuan.nosdn.127.net/807130ceb6cbc33ab731437454a45c43.gif)



**外部脚本方式引入**

+ 说明
  + 内部脚本仅能在当前页面上使用,代码复用度不高
  + 可以将脚本放在独立的js文件中,通过script标签引入外部脚本文件
  + 一对script标签要么用于定义内部脚本,要么用于引入外部js文件,不能混用
  + 一个html文档中,可以有多个script标签 
  + 
+ 抽取脚本代码到独立的js文件中

<img src="F:/资料/java/尚硅谷2023JavaWeb图书配套资料/笔记/images/1681270974917.png" alt="1681270974917" style="zoom:80%;" />

+ 在html文件中,通过script标签引入外部脚本文件 src资源路径 type文件类型

``` html
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>小标题</title>
        <style>
            /* 通过选择器确定样式的作用范围 */
            .btn1 {
                display: block;
                width: 150px; 
                height: 40px; 
                background-color: rgb(245, 241, 129); 
                color: rgb(238, 31, 31);
                border: 3px solid rgb(238, 23, 66);
                font-size: 22px;
                font-family: '隶书';
                line-height: 30px;
                border-radius: 5px;
            }
        </style>
        <script src="js/button.js" type="text/javascript"></script>
    </head>

    <body>
        <button class="btn1" onclick="suprise()">点我有惊喜</button>
    </body>
</html>
```

## 2 JS的数据类型和运算符

### 1 JS的数据类型

> 数值类型

+ 数值类型统一为 number 小数,不区分整数和浮点数

> 字符串类型

+ 字符串类型为 string 和JAVA中的String相似,JS中不严格区分单双引号,都可以用于表示字符串

> 布尔类型

+ 布尔类型为boolean 和Java中的boolean相似,但是在JS的if语句中,非空字符串会被转换为'真',非零数字也会被认为是'真'

> 引用数据类型

+ 引用数据类型对象是Object类型, 各种对象和数组在JS中都是Object类型

> function类型

+ JS中的各种函数属于function数据类型

> 命名未赋值

+ js为弱类型语言,统一使用 var 声明对象和变量,在赋值时才确定真正的数据类型,变量如果只声明没有赋值的话,数据类型为undefined

> 赋予NULL值

+ 在JS中,如果给一个变量赋值为null,其数据类型是Object, 可以通过typeof关键字判断数据类型

console.log(typeof(i)) 查看变量的类型 typeof

### 2 JS的变量

> JS中的变量具有如下特征

+ 1 弱类型变量,可以统一声明成var

+ js是弱类型的，不是没有类型，是变量在声明时不声明类型赋值时才指定类型

+ 2 var声明的变量可以再次声明

+ 3 变量可以使用不同的数据类型多次赋值

+ 4 JS的语句可以以; 结尾,也可以不用;结尾

+ 5 变量标识符严格区分大小写

+ 6 标识符的命名规则参照JAVA

+ 7 如果使用了 一个没有声明的变量,那么运行时会报

  ```
  uncaught ReferenceError: *** is not defined   at index.html:行号:列号
  ```

+ 8 如果一个变量只声明,没赋值,那么值是undefined

### 3 JS的运算符

>  算数运算符  + - * /  %

+ 其中需要注意的是 / 和 % 
  + / 在除0时,结果是Infinity ,而不是报错
  + %在模0时,结果是NaN,意思为 not a number ,而不是报错

> 复合算数运算符 ++ --  += -= *= /= %=

+ 符合算数运算符基本和JAVA一致,同样需要注意 /=和%=
  + 在/=0时,结果是Infinity ,而不是报错
  + 在%=0时,结果是NaN,意思为 not a number ,而不是报错

> 关系运算符  >   <  >= <= == === !=

+ 需要注意的是 == 和 === 差别
  + == 符号,如果两端的数据类型不一致,会尝试将两端的数据转换成number,再对比number大小
    + '123'  这种字符串可以转换成数字
    + true会被转换成1 false会被转换成0
  + ===  符号,如果两端数据类型不一致,直接返回false,数据类型一致在比较是否相同

> 逻辑运算符  || &&    

+ 几乎和JAVA中的一样,需要注意的是,这里直接就是短路的逻辑运算符,单个的 |   和 &  以及 ^ 是位运算符

> 条件运算符  条件? 值1  : 值2

+ 几乎和JAVA中的一样

> 位运算符  |  &  ^  <<  >>  >>>

+ 和 java中的类似(了解)

## 3 JS的流程控制和函数

### 1 JS分支结构

**if结构**

+ 这里的if结构几乎和JAVA中的一样,需要注意的是
  + if()中的非空字符串会被认为是true
  + if()中的非零数字会被认为是true

+ 代码

``` javascript
if('false'){// 非空字符串 if判断为true
    console.log(true)
}else{
    console.log(false)
}
if(''){// 长度为0字符串 if判断为false
    console.log(true)
}else{
    console.log(false)
}
if(1){// 非零数字 if判断为true
    console.log(true)
}else{
    console.log(false)
}
if(0){
    console.log(true)
}else{
    console.log(false)
}
```

+ 结果

![ifelse.png](http://yanxuan.nosdn.127.net/4e8a0cb2f98910cd0407fe37198ba8f2.png)

**switch结构**

+ 几乎和JAVA的语法一致
+ 代码
+ prompt是弹窗  返回结果是用户在窗口输入的值以string类型返回的

``` javascript
var monthStr=prompt("请输入月份","例如:10 ");
var month= Number.parseInt(monthStr)
switch(month){
    case 3:
    case 4:
    case 5:
        console.log("春季");
        break;
    case 6:
    case 7:
    case 8:
        console.log("夏季");
        break;
    case 9:
    case 10:
    case 11:
        console.log("秋季");
        break;
    case 1:
    case 2:
    case 12:
        console.log("冬季");
        break;
    default :
        console.log("月份有误")
}
```

+ 效果

![Switch.gif](http://yanxuan.nosdn.127.net/3a5d8f52cf4d4f6999060fcaff12ec5c.gif)



### 2 JS循环结构

**while结构**

+ 几乎和JAVA一致
+ 代码
+ document.write("张三");  在浏览器窗口上打印

```  javascript
/* 打印99 乘法表 */
var i = 1;
while(i <= 9){
    var j = 1;
    while(j <= i){
        document.write(j+"*"+i+"="+i*j+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        j++;
    }
    document.write("<hr/>");
    i++;
}
```

+ 效果

![99乘法表.png](http://yanxuan.nosdn.127.net/2942cd66aaf60f06ddc47f6437037f5f.png)

**for循环**

+ 几乎和JAVA一致

+ 代码

``` javascript
/* 打印99 乘法表 */
for(  var i = 1;i <= 9; i++){
    for(var j = 1;j <= i;j++){
        document.write(j+"*"+i+"="+i*j+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    }
    document.write("<hr/>");
}
```

+ 效果

![for99.png](http://yanxuan.nosdn.127.net/97756f1e3485ffd3371ffae0b8dc4e49.png)

**foreach循环**

+ 迭代数组时,和java不一样
  + 括号中的临时变量表示的是元素的索引,不是元素的值,
  + ()中也不在使用: 分隔,而是使用 in 关键字
+ var cities =["北京","上海","深圳","武汉","西安","成都"]

+ 代码

``` javascript
var cities =["北京","上海","深圳","武汉","西安","成都"]
document.write("<ul>")
for(var index in  cities){
    document.write("<li>"+cities[index]+"</li>")
}
document.write("</ul>")
```

+ 效果

![foreach.png](http://yanxuan.nosdn.127.net/b91869a831ea91b09d1e0790f6919a75.png)

## 4 JS函数声明

> JS中的方法,多称为函数,函数的声明语法和JAVA中有较大区别

+ 函数说明
  + 函数没有权限控制符
  + 不用声明函数的返回值类型,需要返回在函数体中直接return即可,也无需void关键字
  + 参数列表中,无需数据类型
  + 调用函数时,实参和形参的个数可以不一致，在方法内部可以通过arguments获得调用的实参
  + 声明函数时需要用function关键字，function 函数名() {} 2 var 函数名=function () {}
  + J函数没有异常列表
  + 没有返回值类型，如果有值要返回，直接return
+ 代码

``` javascript
/* 
语法1 
    function 函数名 (参数列表){函数体}
            */
function sum(a, b){
    return a+b;
}
var result =sum(10,20);
console.log(result)

/* 
语法2
    var 函数名 = function (参数列表){函数体}
            */
var add = function(a, b){
    return a+b;
}
var result = add(1,2);
console.log(result);
```

+ 调用测试

## 5 JS的对象和

### 1 JS声明对象的语法

**语法0 对象的定义**

在 JavaScript 中，对象是王。如果您理解了对象，就理解了 JavaScript。

在 JavaScript 中，几乎“所有事物”都是对象。

- 布尔是对象（如果用 *new* 关键词定义）
- 数字是对象（如果用 *new* 关键词定义）
- 字符串是对象（如果用 *new* 关键词定义）
- 日期永远都是对象
- 算术永远都是对象
- 正则表达式永远都是对象
- 数组永远都是对象
- 函数永远都是对象
- 对象永远都是对象

所有 JavaScript 值，除了原始值，都是对象

avaScript 定义了 5 种原始数据类型：

- string
- number
- boolean
- null
- undefined

**语法1 通过new Object()直接创建对象**

+ 代码

```javascript
//添加对象
var person =new Object();
// 给对象添加属性并赋值
person.name="张小明";
person.age=10;
person.foods=["苹果","橘子","香蕉","葡萄"];
// 给对象添加功能函数
person.eat= function (){
    console.log(this.age+"岁的"+this.name+"喜欢吃:")
    for(var i = 0;i<this.foods.length;i++){
        console.log(this.foods[i])
    } 
}
//获得对象属性值
console.log(person.name)
console.log(person.age)
//调用对象方法
person.eat();
```

+ 效果

![js对象语法1.png](http://yanxuan.nosdn.127.net/ed7e0a5679aeba1a1d8277797cc95ed2.png)



**语法2 通过 {}形式创建对象**

+ 语法为  var person ={"属性名":"属性值","属性名","属性值","函数名":函数}
+ 对象也是一个变量，但对象可以包含多个值（多个变量），每个值以 **name:value** 对呈现。
+ 代码

``` javascript
var person ={
    "name":"张小明",
    "age":10,
    "foods":["苹果","香蕉","橘子","葡萄"],
    //对象的属性可以定义为数组  
    "eat":function (){
        console.log(this.age+"岁的"+this.name+"喜欢吃:")
        for(var i = 0;i<this.foods.length;i++){
            console.log(this.foods[i])
        } 
    }
}
//获得对象属性值
console.log(person.name)
console.log(person.age)
//调用对象方法
person.eat();
```

+ 效果

![js对象语法1.png](http://yanxuan.nosdn.127.net/ed7e0a5679aeba1a1d8277797cc95ed2.png)

**3 遍历对象的属性**

```javascript
<p id="demo"></p>

<script>
var myObj, x;
myObj = {"name":"Bill Gates", "age":62, "car":null};
for (x in myObj) {
  document.getElementById("demo").innerHTML += x + "<br>";
  /*
  document.getElementById("demo")：借助 document 对象的 getElementById 方法，查找 id 为 "demo" 的 HTML 元素。
innerHTML 属性：用于获取或设置元素内部的 HTML 内容。
+= 是复合赋值运算符，其作用是把 x + "<br>" 的结果追加到 id 为 "demo" 的元素的现有 HTML 内容之后。
x 是当前循环中 myObj 对象的属性名。
  */
}
</script>
```

**4 delet删除对象属性**

```
delete myObj.car3;
```

### 2 JS常见对象

**1 数组**

> 创建数组的四种方式

+ new Array()                                                   创建空数组
+ new Array(5)                                                 创建数组时给定长度
+ new Array(ele1,ele2,ele3,... ... ,elen);          创建数组时指定元素值
+ [ele1,ele2,ele3,... ... ,elen];                           相当于第三种语法的简写

> 数组的常见API

+ 在JS中,数组属于Object类型,其长度是可以变化的,更像JAVA中的集合

**2 Boolean对象**

> boolean对象的方法比较简单

| 方法                                                         | 描述                               |
| :----------------------------------------------------------- | :--------------------------------- |
| [toString()](https://www.runoob.com/jsref/jsref-tostring-boolean.html) | 把布尔值转换为字符串，并返回结果。 |
| [valueOf()](https://www.runoob.com/jsref/jsref-valueof-boolean.html) | 返回 Boolean 对象的原始值。        |

**3 Date对象**

> 和JAVA中的Date类比较类似

**4 Math**

>  和JAVA中的Math类比较类似

**5 Number**

> Number中准备了一些基础的数据处理函数

**6 String**

> 和JAVA中的String类似

## 6 事件的绑定

### 1 什么是事件

>  HTML 事件可以是浏览器行为，也可以是用户行为。 当这些一些行为发生时,可以自动触发对应的JS函数的运行,我们称之为事件发生.JS的事件驱动指的就是行为触发代码运行的这种特点

### 2 常见事件

**1 鼠标事件**

| 属性                                                         | 描述                                   |
| :----------------------------------------------------------- | :------------------------------------- |
| [onclick](https://www.runoob.com/jsref/event-onclick.html)   | 当用户点击某个对象时调用的事件句柄。   |
| [oncontextmenu](https://www.runoob.com/jsref/event-oncontextmenu.html) | 在用户点击鼠标右键打开上下文菜单时触发 |
| [ondblclick](https://www.runoob.com/jsref/event-ondblclick.html) | 当用户双击某个对象时调用的事件句柄。   |
| [onmousedown](https://www.runoob.com/jsref/event-onmousedown.html) | 鼠标按钮被按下。                       |
| [onmouseenter](https://www.runoob.com/jsref/event-onmouseenter.html) | 当鼠标指针移动到元素上时触发。         |
| [onmouseleave](https://www.runoob.com/jsref/event-onmouseleave.html) | 当鼠标指针移出元素时触发               |
| [onmousemove](https://www.runoob.com/jsref/event-onmousemove.html) | 鼠标被移动。                           |
| [onmouseover](https://www.runoob.com/jsref/event-onmouseover.html) | 鼠标移到某元素之上。                   |
| [onmouseout](https://www.runoob.com/jsref/event-onmouseout.html) | 鼠标从某元素移开。                     |
| [onmouseup](https://www.runoob.com/jsref/event-onmouseup.html) | 鼠标按键被松开。                       |

**2 键盘事件**

| 属性                                                         | 描述                       |
| :----------------------------------------------------------- | :------------------------- |
| [onkeydown](https://www.runoob.com/jsref/event-onkeydown.html) | 某个键盘按键被按下。       |
| [onkeypress](https://www.runoob.com/jsref/event-onkeypress.html) | 某个键盘按键被按下并松开。 |
| [onkeyup](https://www.runoob.com/jsref/event-onkeyup.html)   | 某个键盘按键被松开。       |

**3 表单事件**

| 属性                                                         | 描述                                                         |
| :----------------------------------------------------------- | :----------------------------------------------------------- |
| [onblur](https://www.runoob.com/jsref/event-onblur.html)     | 元素失去焦点时触发                                           |
| [onchange](https://www.runoob.com/jsref/event-onchange.html) | 该事件在表单元素的内容改变时触发( <input>, <keygen>, <select>, 和 <textarea>) |
| [onfocus](https://www.runoob.com/jsref/event-onfocus.html)   | 元素获取焦点时触发                                           |
| [onfocusin](https://www.runoob.com/jsref/event-onfocusin.html) | 元素即将获取焦点时触发                                       |
| [onfocusout](https://www.runoob.com/jsref/event-onfocusout.html) | 元素即将失去焦点时触发                                       |
| [oninput](https://www.runoob.com/jsref/event-oninput.html)   | 元素获取用户输入时触发                                       |
| [onreset](https://www.runoob.com/jsref/event-onreset.html)   | 表单重置时触发                                               |
| [onsearch](https://www.runoob.com/jsref/event-onsearch.html) | 用户向搜索域输入文本时触发 ( <input="search">)               |
| [onselect](https://www.runoob.com/jsref/event-onselect.html) | 用户选取文本时触发 ( <input> 和 <textarea>)                  |
| [onsubmit](https://www.runoob.com/jsref/event-onsubmit.html) | 表单提交时触发                                               |

### 3 事件的绑定

**1 通过属性绑定**

+ 通过事件属性绑定函数,在行为发生时会自动执行函数
+ 一个事件可以同时绑定多个函数 用逗号隔开  onclick="fun1(),fun2()" 
+ 一个元素可以同时绑定多个事件
+ 方法中可以传入 this对象,代表当前元素

1. 键盘事件

```js
<script>
        function fun1(){
            alert("单机了");
        }  function fun2(){
            alert("单机了2");
        }
        function fun7(){
            console.log("按下了键盘");
        }
        function fun8(){
            console.log("抬起了键盘");
        }
    </script>
    <input type="button" value="按钮" onclick="fun1(),fun2()">
    <input type="text" onkeydown="fun7()" onkeyup="fun8()">
```

2. 鼠标事件

``` js
    <head>
        <meta charset="UTF-8">
        <title>小标题</title>
      
        <script>
            function testDown1(){
                console.log("down1")
            }
            function testDown2(){
                console.log("down2")
            }
            function testFocus(){
                console.log("获得焦点")
            }

            function testBlur(){
                console.log("失去焦点")
            }
            function testChange(input){
                console.log("内容改变")
                console.log(input.value);
            }
            function testMouseOver(){
                console.log("鼠标悬停")
            }
            function testMouseLeave(){
                console.log("鼠标离开")
            }
            function testMouseMove(){
                console.log("鼠标移动")
            }
        </script>
    </head>

    <body>
        <input type="text" 
        onkeydown="testDown1(),testDown2()"
        onfocus="testFocus()" 
        onblur="testBlur()" 
        onchange="testChange(this)"
        onmouseover="testMouseOver()" 
        onmouseleave="testMouseLeave()" 
        onmousemove="testMouseMove()" 
         />
    </body>
```

3. 表单事件

```js
<script>
        function fun1(){
            alert("单机了");
        }  function fun2(){
            alert("单机了2");
        }
        function fun7(){
            console.log("按下了键盘");
        }
        function fun8(){
            console.log("抬起了键盘");
        }
// onfocus 获得焦点  元素获得焦点时触发  常用于表单验证
// onblur 失去焦点  元素失去焦点时触发  常用于表单验证
// onchange 内容发生改变  失去焦点并内容发生改变才会触发  在下拉框也会触发
// onchange="testChange(this.value) 获取改变的值
// onsubmit 表单提交  点击提交按钮时触发  常用于表单验证
// onreset 表单重置  点击重置按钮时触发  常用于表单验证

        function testFocus(){
            console.log("获得焦点");
        }
        function testBlur(){
            console.log("失去焦点");
        }
        function testChange(value){
              console.log("内容改变"+value);
        }
        function testChange2(value){
            console.log("内容改变为"+value);
        }
        function testSubmit(){
            // alert("表单发送提交了");
            // 在这里我们有机会阻止表单的提交
            // confirm("确定要提交表单吗") 
            console.log("表单发送提交了");
            // 有返回值的confirm 如果确认提交
            var flag =confirm("确定要提交表单吗");
            // if(!flag){
            // event.preventDefault();//阻止组件的默认行为 表单的提交
            // }
            // 第二种方式return true 或者 false 阻止表单的提交 在表单绑定的事件上也要加上return
            if(!flag){
                return false;//阻止组件的默认行为 表单的提交
            }
            return true;
        }
        function testReset(){
            alert("表单重置了")
        }
    </script>
    <body>
     <form action="test.html" method="get"  onSubmit="return testSubmit()" onreset="testReset()">
        用户昵称:<input type="text" name="username" 
        onfocus="testFocus()"
        onblur="testBlur()"
        onchange="testChange(this.value)"
        <!-- this.value 获得改变的值 -->
        ><br>
        密码:<input type="password" name="password"><br>

        <select onchange="testChange2(this.value)">
            <option value="1">北京</option>
            <option value="2">上海</option>
            <option value="3">广州</option>

        </select>
        <input type="submit" value="注册">
        <input type="reset" value="清空">
    </form>
    </body>
```

**2 通过DOM编程绑定**

// onload 页面加载完毕事件 

```
<script>
        function ready(){
            alert("页面加载完毕")    
        // 通过dom获取元素
     var btn = document.getElementById("btn")
    //  绑定一个单击事件
     btn.onclick = function(){
         alert("按钮被单击了")}
     }
    </script>
    <body onload=" ready()">
    <button id="btn">按钮</button> 
</body>
```

可以使用window.onload 就不用在body里添加onload了

```
<script>      
        window.onload = function(){
        // 通过dom获取元素
     var btn = document.getElementById("btn")
    //  绑定一个单击事件
     btn.onclick = function(){
         alert("按钮被单击了")}
        }     
    </script>
</head>
<body >
    <button id="btn">按钮</button>  
</body>
```

### 4 事件的触发

**行为触发**

+ 发生行为时触发,演示:略

**DOM编程触发**

+ 通过DOM编程,用代码触发,执行某些代码相当于发生了某些行为

+ 代码

动态触发事件

点击按钮切换颜色  

``` html
    <script>
        window.onload = function(){
// 为div添加一个单击事件
var div1 =document.getElementById("btn1")
 var isRed = false; // 用于记录 div 的颜色状态
        div1.onclick=function(){
           if (isRed) {
                    this.style.backgroundColor = "yellow";
                } else {
                    this.style.backgroundColor = "red";
                }
                isRed = !isRed; // 切换颜色状态
        }
        // 通过dom获取元素
     var btn = document.getElementById("btn")
    //  绑定一个单击事件
     btn.onclick = function()
     {
         alert("按钮被单击了")
        //  通过dom编程触发事件 
        // 通过dom编程触发div的单机事件
        div1.onclick()
        }
        }   
    </script>
    <style>
        .div1{
            width: 100px;
            height: 100px;
            background-color: yellow;
        }
    </style>
</head>
<body >
   <div id="btn1"class="div1"></div>
   <hr>
    <button id="btn">按钮</button>
</body>
</html>
```

## 7 BOM编程

替换文本内容

```
<!DOCTYPE html>
<html>
<body>
  <p id="demo">原始文本</p>
  <button onclick="changeText()">修改文本</button>
  <script>
    function changeText() {
      document.getElementById("demo").innerHTML = "新的文本内容";
    }
    /*
     1. document 对象
在浏览器环境中，document 对象代表整个 HTML 文档。它是文档对象模型（DOM）的入口，借助 document 对象，你能够访问和操控 HTML 文档中的所有元素。
2. getElementById() 方法
这是 document 对象的一个方法，其功能是通过元素的 id 属性来查找 HTML 文档里的元素。
具体而言，getElementById("demo") 会查找 id 属性值为 "demo" 的元素。每个 HTML 文档里的 id 应当是唯一的，所以这个方法只会返回一个元素节点。
3. innerHTML 属性
它属于元素节点的属性，可用于获取或者设置元素内部的 HTML 内容。当使用赋值语句 innerHTML = ... 时，就会把元素内部原有的 HTML 内容替换成新的值。 
    */
  </scrit>
</body>
</html>
```

### 1 什么是BOM

+ BOM是Browser Object Model的简写，即浏览器对象模型。

+ BOM由一系列对象组成，是访问、控制、修改浏览器的属性和方法(通过window对象及属性的一系列方法 控制浏览器行为的一种编程)

+ BOM没有统一的标准(每种客户端都可以自定标准)。

+ BOM编程是将浏览器窗口的各个组成部分抽象成各个对象,通过各个对象的API操作组件行为的一种编程

+ BOM编程的对象结构如下

  + window 顶级对象,代表整个浏览器窗口
    + location对象                 window对象的属性之一,代表浏览器的地址栏
    + history对象                   window对象的属性之一,代表浏览器的访问历史
    + screen对象                    window对象的属性之一,代表屏幕
    + navigator对象               window对象的属性之一,代表浏览器软件本身
    + document对象              window对象的属性之一,代表浏览器窗口目前解析的html文档
    + console对象                  window对象的属性之一,代表浏览器开发者工具的控制台
    + localStorage对象          window对象的属性之一,代表浏览器的本地数据持久化存储
    + sessionStorage对象      window对象的属性之一,代表浏览器的本地数据会话级存储

  ![BOM.png](http://yanxuan.nosdn.127.net/a8777f37837873c0df6b24ac3764fad2.png)

###  2 window对象的常见属性(了解)

| 属性                                                         | 描述                                                         |
| :----------------------------------------------------------- | :----------------------------------------------------------- |
| [closed](https://www.runoob.com/jsref/prop-win-closed.html)  | 返回窗口是否已被关闭。                                       |
| [defaultStatus](https://www.runoob.com/jsref/prop-win-defaultstatus.html) | 设置或返回窗口状态栏中的默认文本。                           |
| [document](https://www.runoob.com/jsref/dom-obj-document.html) | 对 Document 对象的只读引用。(请参阅[对象](https://www.runoob.com/jsref/dom-obj-document.html)) |
| [frames](https://www.runoob.com/jsref/prop-win-frames.html)  | 返回窗口中所有命名的框架。该集合是 Window 对象的数组，每个 Window 对象在窗口中含有一个框架。 |
| [history](https://www.runoob.com/jsref/obj-history.html)     | 对 History 对象的只读引用。请参数 [History 对象](https://www.runoob.com/jsref/obj-history.html)。 |
| [innerHeight](https://www.runoob.com/jsref/prop-win-innerheight.html) | 返回窗口的文档显示区的高度。                                 |
| [innerWidth](https://www.runoob.com/jsref/prop-win-innerheight.html) | 返回窗口的文档显示区的宽度。                                 |
| [localStorage](https://www.runoob.com/jsref/prop-win-localstorage.html) | 在浏览器中存储 key/value 对。没有过期时间。                  |
| [length](https://www.runoob.com/jsref/prop-win-length.html)  | 设置或返回窗口中的框架数量。                                 |
| [location](https://www.runoob.com/jsref/obj-location.html)   | 用于窗口或框架的 Location 对象。请参阅 [Location 对象](https://www.runoob.com/jsref/obj-location.html)。 |
| [name](https://www.runoob.com/jsref/prop-win-name.html)      | 设置或返回窗口的名称。                                       |
| [navigator](https://www.runoob.com/jsref/obj-navigator.html) | 对 Navigator 对象的只读引用。请参数 [Navigator 对象](https://www.runoob.com/jsref/obj-navigator.html)。 |
| [opener](https://www.runoob.com/jsref/prop-win-opener.html)  | 返回对创建此窗口的窗口的引用。                               |
| [outerHeight](https://www.runoob.com/jsref/prop-win-outerheight.html) | 返回窗口的外部高度，包含工具条与滚动条。                     |
| [outerWidth](https://www.runoob.com/jsref/prop-win-outerheight.html) | 返回窗口的外部宽度，包含工具条与滚动条。                     |
| [pageXOffset](https://www.runoob.com/jsref/prop-win-pagexoffset.html) | 设置或返回当前页面相对于窗口显示区左上角的 X 位置。          |
| [pageYOffset](https://www.runoob.com/jsref/prop-win-pagexoffset.html) | 设置或返回当前页面相对于窗口显示区左上角的 Y 位置。          |
| [parent](https://www.runoob.com/jsref/prop-win-parent.html)  | 返回父窗口。                                                 |
| [screen](https://www.runoob.com/jsref/obj-screen.html)       | 对 Screen 对象的只读引用。请参数 [Screen 对象](https://www.runoob.com/jsref/obj-screen.html)。 |
| [screenLeft](https://www.runoob.com/jsref/prop-win-screenleft.html) | 返回相对于屏幕窗口的x坐标                                    |
| [screenTop](https://www.runoob.com/jsref/prop-win-screenleft.html) | 返回相对于屏幕窗口的y坐标                                    |
| [screenX](https://www.runoob.com/jsref/prop-win-screenx.html) | 返回相对于屏幕窗口的x坐标                                    |
| [sessionStorage](https://www.runoob.com/jsref/prop-win-sessionstorage.html) | 在浏览器中存储 key/value 对。 在关闭窗口或标签页之后将会删除这些数据。 |
| [screenY](https://www.runoob.com/jsref/prop-win-screenx.html) | 返回相对于屏幕窗口的y坐标                                    |
| [self](https://www.runoob.com/jsref/prop-win-self.html)      | 返回对当前窗口的引用。等价于 Window 属性。                   |
| [status](https://www.runoob.com/jsref/prop-win-status.html)  | 设置窗口状态栏的文本。                                       |
| [top](https://www.runoob.com/jsref/prop-win-top.html)        | 返回最顶层的父窗口。                                         |

### 3 window对象的常见方法(了解)

| 方法                                                         | 描述                                                         |
| :----------------------------------------------------------- | :----------------------------------------------------------- |
| [alert()](https://www.runoob.com/jsref/met-win-alert.html)   | 显示带有一段消息和一个确认按钮的警告框。                     |
| [atob()](https://www.runoob.com/jsref/met-win-atob.html)     | 解码一个 base-64 编码的字符串。                              |
| [btoa()](https://www.runoob.com/jsref/met-win-btoa.html)     | 创建一个 base-64 编码的字符串。                              |
| [blur()](https://www.runoob.com/jsref/met-win-blur.html)     | 把键盘焦点从顶层窗口移开。                                   |
| [clearInterval()](https://www.runoob.com/jsref/met-win-clearinterval.html) | 取消由 setInterval() 设置的 timeout。                        |
| [clearTimeout()](https://www.runoob.com/jsref/met-win-cleartimeout.html) | 取消由 setTimeout() 方法设置的 timeout。                     |
| [close()](https://www.runoob.com/jsref/met-win-close.html)   | 关闭浏览器窗口。                                             |
| [confirm()](https://www.runoob.com/jsref/met-win-confirm.html) | 显示带有一段消息以及确认按钮和取消按钮的对话框。             |
| [createPopup()](https://www.runoob.com/jsref/met-win-createpopup.html) | 创建一个 pop-up 窗口。                                       |
| [focus()](https://www.runoob.com/jsref/met-win-focus.html)   | 把键盘焦点给予一个窗口。                                     |
| [getSelection](https://www.runoob.com/jsref/met-win-getselection.html)() | 返回一个 Selection 对象，表示用户选择的文本范围或光标的当前位置。 |
| [getComputedStyle()](https://www.runoob.com/jsref/jsref-getcomputedstyle.html) | 获取指定元素的 CSS 样式。                                    |
| [matchMedia()](https://www.runoob.com/jsref/met-win-matchmedia.html) | 该方法用来检查 media query 语句，它返回一个 MediaQueryList对象。 |
| [moveBy()](https://www.runoob.com/jsref/met-win-moveby.html) | 可相对窗口的当前坐标把它移动指定的像素。                     |
| [moveTo()](https://www.runoob.com/jsref/met-win-moveto.html) | 把窗口的左上角移动到一个指定的坐标。                         |
| [open()](https://www.runoob.com/jsref/met-win-open.html)     | 打开一个新的浏览器窗口或查找一个已命名的窗口。               |
| [print()](https://www.runoob.com/jsref/met-win-print.html)   | 打印当前窗口的内容。                                         |
| [prompt()](https://www.runoob.com/jsref/met-win-prompt.html) | 显示可提示用户输入的对话框。                                 |
| [resizeBy()](https://www.runoob.com/jsref/met-win-resizeby.html) | 按照指定的像素调整窗口的大小。                               |
| [resizeTo()](https://www.runoob.com/jsref/met-win-resizeto.html) | 把窗口的大小调整到指定的宽度和高度。                         |
| scroll()                                                     | 已废弃。 该方法已经使用了 [scrollTo()](https://www.runoob.com/jsref/met-win-scrollto.html) 方法来替代。 |
| [scrollBy()](https://www.runoob.com/jsref/met-win-scrollby.html) | 按照指定的像素值来滚动内容。                                 |
| [scrollTo()](https://www.runoob.com/jsref/met-win-scrollto.html) | 把内容滚动到指定的坐标。                                     |
| [setInterval()](https://www.runoob.com/jsref/met-win-setinterval.html) | 按照指定的周期（以毫秒计）来调用函数或计算表达式。           |
| [setTimeout()](https://www.runoob.com/jsref/met-win-settimeout.html) | 在指定的毫秒数后调用函数或计算表达式。                       |
| [stop()](https://www.runoob.com/jsref/met-win-stop.html)     | 停止页面载入。                                               |
| [postMessage()](https://www.runoob.com/jsref/met-win-postmessage.html) | 安全地实现跨源通信。                                         |

### 4 通过BOM编程控制浏览器行为演示

window对象是有浏览器提供给我们使用的，无需自己new

Window，可以省略不写

>  三种弹窗方式

``` html
    <head>
        <meta charset="UTF-8">
        <title>小标题</title>
      
        <script>
           function testAlert(){
                //普通信息提示框
                window.alert("提示信息");
           }
           function testConfirm(){
                //确认框
                var con =confirm("确定要删除吗?");
                if(con){
                    alert("点击了确定")
                }else{
                    alert("点击了取消")
                }
           }
           function testPrompt(){
                //信息输入对话框
                var res =prompt("请输入昵称","例如:张三");
                alert("您输入的是:"+res)
           }
        </script>
    </head>

    <body>
        <input type="button" value="提示框" onclick="testAlert()"/> <br>
        <input type="button" value="确认框" onclick="testConfirm()"/> <br>
        <input type="button" value="对话框" onclick="testPrompt()"/> <br>
    </body>
```

>  历史跳转

```
<script>
function funA(){
   history.back() 
}
//向前翻页
function funB(){ 
history.forward()//
history.go(1) \\数字是几就向前后几页
}
//向后翻页
</script>
<body>
<button onclick="funA()">上一页</button>
    <button onclick="funB()">下一页</button>
    <a href="https://www.baidu.com">百度</a>
    <body>
```

页面跳转

```
function funC(){
    location.href="https://www.baidu.com";\\窗口访问历史
}
```

``` html
    <head>
        <meta charset="UTF-8">
        <title>小标题</title>
      
        <script>
           function goAtguigu(){
                var flag =confirm("即将跳转到尚硅谷官网,本页信息即将丢失,确定吗?")
                if(flag){
                    // 通过BOM编程地址栏url切换
                    window.location.href="http://www.atguigu.com"
                }
           }
          
        </script>
    </head>

    <body>
        <input type="button" value="跳转到尚硅谷" onclick="goAtguigu()"/> <br>
    </body>
```

### 5 通过BOM编程实现会话级和持久级数据存储

+ 会话级数据 : 内存型数据,是浏览器在内存上临时存储的数据,浏览器关闭后,数据失去,通过window的sessionStorge属性实现
+ 持久级数据 : 磁盘型数据,是浏览器在磁盘上持久存储的数据,浏览器关闭后,数据仍在,通过window的localStorge实现
+ 可以用于将来存储一些服务端响应回来的数据,比如:token令牌,或者一些其他功能数据,根据数据的业务范围我们可以选择数据存储的会话/持久 级别

``` html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <script>
        function saveItem(){
            // 让浏览器存储一些会话级数据
            window.sessionStorage.setItem("sessionMsg","sessionValue")
            // 让浏览器存储一些持久级数据
            window.localStorage.setItem("localMsg","localValue")

            console.log("haha")
        }

        function removeItem(){
            // 删除数据
            sessionStorage.removeItem("sessionMsg")
            localStorage.removeItem("localMsg")
        }

        function readItem(){
            console.log("read")
            // 读取数据
            console.log("session:"+sessionStorage.getItem("sessionMsg"))
            console.log("local:"+localStorage.getItem("localMsg"))
        }
    </script>
</head>
<body>

    <button onclick="saveItem()">存储数据</button>
    <button onclick="removeItem()">删除数据</button>
    <button onclick="readItem()">读取数据</button>

</body>
</html>
```

+ 测试,存储数据后,再读取数据,然后关闭浏览器,获取数据,发现sessionStorge的数据没有了,localStorge的数据还在
+ 通过removeItem可以将这些数据直接删除
+ 在F12开发者工具的应用程序栏,可以查看数据的状态

![BOM数据存储.png](http://yanxuan.nosdn.127.net/f39bdfcb49b09b68644c242e90486bf9.png)

## 8 DOM编程

### 1 什么是DOM编程

简单来说:DOM(Document Object Model)编程就是使用document对象的API完成对网页HTML文档进行动态修改,以实现网页数据和样式动态变化效果的编程.

+ document对象代表整个html文档，可用来访问页面中的所有元素，是最复杂的一个dom对象，可以说是学习好dom编程的关键所在。
+ 根据HTML代码结构特点,document对象本身是一种树形结构的文档对象。

![document1.png](http://yanxuan.nosdn.127.net/fb58d48c75c504780e36890eb7fc155e.png)

+ 上面的代码生成的树如下

![document2.png](http://yanxuan.nosdn.127.net/a1db66cf4f897e62fbb09ad388503c7e.png)

+ DOM编程其实就是用window对象的document属性的相关API完成对页面元素的控制的编程

  ![dom1.png](http://yanxuan.nosdn.127.net/6aa31eeb5a29eeb6bedb86ddee506bce.png)

+ dom树中节点的类型

  + node  节点,所有结点的父类型
    + element   元素节点,node的子类型之一,代表一个完整标签
    + attribute  属性节点,node的子类型之一,代表元素的属性
    + text          文本节点,node的子类型之一,代表双标签中间的文本

### 2 获取页面元素的几种方式

**1 在整个文档范围内查找元素结点**

| 功能               | API                                     | 返回值           |
| ------------------ | --------------------------------------- | ---------------- |
| 根据id值查询       | document.getElementById(“id值”)         | 一个具体的元素节 |
| 根据标签名查询     | document.getElementsByTagName(“标签名”) | 元素节点数组     |
| 根据name属性值查询 | document.getElementsByName(“name值”)    | 元素节点数组     |
| 根据类名查询       | document.getElementsByClassName("类名") | 元素节点数组     |

**2 在具体元素节点范围内查找子节点**

| 功能               | API                       | 返回值         |
| ------------------ | ------------------------- | -------------- |
| 查找子标签         | element.children          | 返回子标签数组 |
| 查找第一个子标签   | element.firstElementChild | 标签对象       |
| 查找最后一个子标签 | element.lastElementChild  | 节点对象       |

**3 查找指定子元素节点的父节点**

| 功能                     | API                   | 返回值   |
| ------------------------ | --------------------- | -------- |
| 查找指定元素节点的父标签 | element.parentElement | 标签对象 |



**4  查找指定元素节点的兄弟节点**

| 功能               | API                         | 返回值   |
| ------------------ | --------------------------- | -------- |
| 查找前一个兄弟标签 | node.previousElementSibling | 标签对象 |
| 查找后一个兄弟标签 | node.nextElementSibling     | 标签对象 |

``` html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
   <script>
    /* 
    1 获得document  dom树
        window.document
    2 从document中获取要操作的元素
        1. 直接获取
            var el1 =document.getElementById("username") // 根据元素的id值获取页面上唯一的一个元素
            var els =document.getElementsByTagName("input") // 根据元素的标签名获取多个同名元素
            var els =document.getElementsByName("aaa") // 根据元素的name属性值获得多个元素
            var els =document.getElementsByClassName("a") // 根据元素的class属性值获得多个元素
        2. 间接获取
            var cs=div01.children // 通过父元素获取全部的子元素
            var firstChild =div01.firstElementChild  // 通过父元素获取第一个子元素
            var lastChild = div01.lastElementChild   // 通过父元素获取最后一个子元素
            var parent = pinput.parentElement  // 通过子元素获取父元素
            var pElement = pinput.previousElementSibling // 获取前面的第一个元素
            var nElement = pinput.nextElementSibling // 获取后面的第一个元素
    3 对元素进行操作
        1. 操作元素的属性
        2. 操作元素的样式
        3. 操作元素的文本
        4. 增删元素   
    */
   function fun1(){
        //1 获得document
        //2 通过document获得元素
        var el1 =document.getElementById("username") // 根据元素的id值获取页面上唯一的一个元素
        console.log(el1)
   }
   function fun2(){
        var els =document.getElementsByTagName("input") // 根据元素的标签名获取多个同名元素
        for(var i = 0 ;i<els.length;i++){
            console.log(els[i])
        }
   }
   function fun3(){
        var els =document.getElementsByName("aaa") // 根据元素的name属性值获得多个元素
        console.log(els)
        for(var i =0;i< els.length;i++){
            console.log(els[i])
        }
   }

   function fun4(){
    var els =document.getElementsByClassName("a") // 根据元素的class属性值获得多个元素
    for(var i =0;i< els.length;i++){
            console.log(els[i])
        }
   }

   function fun5(){
    // 先获取父元素
     var div01 = document.getElementById("div01")
     // 获取所有子元素
     var cs=div01.children // 通过父元素获取全部的子元素
     for(var i =0;i< cs.length;i++){
            console.log(cs[i])
     }

     console.log(div01.firstElementChild)  // 通过父元素获取第一个子元素
     console.log(div01.lastElementChild)   // 通过父元素获取最后一个子元素
   }

   function fun6(){
        // 获取子元素
        var pinput =document.getElementById("password")
        console.log(pinput.parentElement) // 通过子元素获取父元素
   }

   function fun7(){
        // 获取子元素
        var pinput =document.getElementById("password")
        console.log(pinput.previousElementSibling) // 获取前面的第一个元素
        console.log(pinput.nextElementSibling) // 获取后面的第一个元素
   }
   </script>
</head>
<body>
    <div id="div01">
        <input type="text" class="a" id="username" name="aaa"/>
        <input type="text" class="b" id="password" name="aaa"/>
        <input type="text" class="a" id="email"/>
        <input type="text" class="b" id="address"/>
    </div>
    <input type="text" class="a"/><br>

    <hr>
    <input type="button" value="通过父元素获取子元素" onclick="fun5()" id="btn05"/>
    <input type="button" value="通过子元素获取父元素" onclick="fun6()" id="btn06"/>
    <input type="button" value="通过当前元素获取兄弟元素" onclick="fun7()" id="btn07"/>
    <hr>

    <input type="button" value="根据id获取指定元素" onclick="fun1()" id="btn01"/>
    <input type="button" value="根据标签名获取多个元素" onclick="fun2()" id="btn02"/>
    <input type="button" value="根据name属性值获取多个元素" onclick="fun3()" id="btn03"/>
    <input type="button" value="根据class属性值获得多个元素" onclick="fun4()" id="btn04"/>
    
</body>
</html>
```

通过上下键选择多行——ctrl +alt + 上/下 

### 3 操作元素属性值

**7.3.1 属性操作**

| 需求       | 操作方式                   |
| ---------- | -------------------------- |
| 读取属性值 | 元素对象.属性名            |
| 修改属性值 | 元素对象.属性名=新的属性值 |

**7.3.2 内部文本操作**

| 需求                         | 操作方式          |
| ---------------------------- | ----------------- |
| 获取或者设置标签体的文本内容 | element.innerText |
| 获取或者设置标签体的内容     | element.innerHTML |

``` html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
   <script>
    /* 
    1 获得document  dom树
        window.document
    2 从document中获取要操作的元素
        1. 直接获取
            var el1 =document.getElementById("username") // 根据元素的id值获取页面上唯一的一个元素
            var els =document.getElementsByTagName("input") // 根据元素的标签名获取多个同名元素
            var els =document.getElementsByName("aaa") // 根据元素的name属性值获得多个元素
            var els =document.getElementsByClassName("a") // 根据元素的class属性值获得多个元素
        2. 间接获取
            var cs=div01.children // 通过父元素获取全部的子元素
            var firstChild =div01.firstElementChild  // 通过父元素获取第一个子元素
            var lastChild = div01.lastElementChild   // 通过父元素获取最后一个子元素
            var parent = pinput.parentElement  // 通过子元素获取父元素
            var pElement = pinput.previousElementSibling // 获取前面的第一个元素
            var nElement = pinput.nextElementSibling // 获取后面的第一个元素
    3 对元素进行操作
        1. 操作元素的属性   元素名.属性名=""
        2. 操作元素的样式   元素名.style.样式名=""  样式名"-" 要进行驼峰转换
        3. 操作元素的文本   元素名.innerText   只识别文本
                           元素名.innerHTML   同时可以识别html代码 
        4. 增删元素   
    */
   function changeAttribute(){
        var in1 =document.getElementById("in1")
        // 语法 元素.属性名=""
        // 获得属性值
        console.log(in1.type)
        console.log(in1.value)
        // 修改属性值
        in1.type="button"
        in1.value="嗨"
   }
   function changeStyle(){
        var in1 =document.getElementById("in1")
        // 语法  元素.style.样式名=""   原始样式名中的"-"符号 要转换驼峰式  background-color > backgroundColor
        in1.style.color="green"
        in1.style.borderRadius="5px"
        
   }
   function changeText(){
        var div01 =document.getElementById("div01")
        /* 
        语法  元素名.innerText   只识别文本
              元素名.innerHTML   同时可以识别html代码
        */
        console.log(div01.innerText)
        div01.innerHTML="<h1>嗨</h1>"
   }

   </script>
   <style>
    #in1{
        color: red;
    }
   </style>
</head>
<body>
    <input id="in1" type="text" value="hello">
    <div id="div01">
        hello
    </div>

    <hr>
    <button onclick="changeAttribute()">操作属性</button>
    <button onclick="changeStyle()">操作样式</button>
    <button onclick="changeText()">操作文本</button>
    
</body>
</html>
```

### 4 增删元素

**1 对页面的元素进行增删操作**

| API                                      | 功能                                       |
| ---------------------------------------- | ------------------------------------------ |
| document.createElement(“标签名”)         | 创建元素节点并返回，但不会自动添加到文档中 |
| document.createTextNode(“文本值”)        | 创建文本节点并返回，但不会自动添加到文档中 |
| element.appendChild(ele)                 | 将ele添加到element所有子节点后面           |
| parentEle.insertBefore(newEle,targetEle) | 将newEle插入到targetEle前面                |
| parentEle.replaceChild(newEle, oldEle)   | 用新节点替换原有的旧子节点                 |
| element.remove()                         | 删除某个标签                               |

``` html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
   <script>
    /* 
    1 获得document  dom树
        window.document
    2 从document中获取要操作的元素
        1. 直接获取
            var el1 =document.getElementById("username") // 根据元素的id值获取页面上唯一的一个元素
            var els =document.getElementsByTagName("input") // 根据元素的标签名获取多个同名元素
            var els =document.getElementsByName("aaa") // 根据元素的name属性值获得多个元素
            var els =document.getElementsByClassName("a") // 根据元素的class属性值获得多个元素
        2. 间接获取
            var cs=div01.children // 通过父元素获取全部的子元素
            var firstChild =div01.firstElementChild  // 通过父元素获取第一个子元素
            var lastChild = div01.lastElementChild   // 通过父元素获取最后一个子元素
            var parent = pinput.parentElement  // 通过子元素获取父元素
            var pElement = pinput.previousElementSibling // 获取前面的第一个元素
            var nElement = pinput.nextElementSibling // 获取后面的第一个元素
    3 对元素进行操作
        1. 操作元素的属性   元素名.属性名=""
        2. 操作元素的样式   元素名.style.样式名=""  样式名"-" 要进行驼峰转换
        3. 操作元素的文本   元素名.innerText   只识别文本
                           元素名.innerHTML   同时可以识别html代码 
        4. 增删元素
            var element =document.createElement("元素名") // 创建元素
            父元素.appendChild(子元素)               // 在父元素中追加子元素
            父元素.insertBefore(新元素,参照元素)     // 在某个元素前增加元素
            父元素.replaceChild(新元素,被替换的元素) // 用新的元素替换某个子子元素
            元素.remove()                            // 删除当前元素
    */
   function addCs(){
        // 创建一个新的元素
        // 创建元素
        var csli =document.createElement("li") // <li></li>
        // 设置子元素的属性和文本 <li id="cs">长沙</li>
        csli.id="cs"
        csli.innerText="长沙"
        // 将子元素放入父元素中
        var cityul =document.getElementById("city")
        // 在父元素中追加子元素
        cityul.appendChild(csli)
   }
   function addCsBeforeSz(){
        // 创建一个新的元素
        // 创建元素
        var csli =document.createElement("li") // <li></li>
        // 设置子元素的属性和文本 <li id="cs">长沙</li>
        csli.id="cs"
        csli.innerText="长沙"
        // 将子元素放入父元素中
        var cityul =document.getElementById("city")
        // 在父元素中追加子元素
        //cityul.insertBefore(新元素,参照元素)
        var szli =document.getElementById("sz")
        cityul.insertBefore(csli,szli)
   }

   function replaceSz(){
        // 创建一个新的元素
        // 创建元素
        var csli =document.createElement("li") // <li></li>
        // 设置子元素的属性和文本 <li id="cs">长沙</li>
        csli.id="cs"
        csli.innerText="长沙"
        // 将子元素放入父元素中
        var cityul =document.getElementById("city")
        // 在父元素中追加子元素
        //cityul.replaceChild(新元素,被替换的元素)
        var szli =document.getElementById("sz")
        cityul.replaceChild(csli,szli)
   }

   function removeSz(){
        var szli =document.getElementById("sz")
        // 哪个元素调用了remove该元素就会从dom树中移除
        szli.remove()
   }

   function clearCity(){
        
        var cityul =document.getElementById("city")

        /* var fc =cityul.firstChild
        while(fc != null ){
            fc.remove()
            fc =cityul.firstChild
        } */
        cityul.innerHTML=""
        //cityul.remove()
        
   }
   
   </script>
   
</head>
<body>
    <ul id="city">
        <li id="bj">北京</li>
        <li id="sh">上海</li>
        <li id="sz">深圳</li>
        <li id="gz">广州</li>
    </ul>

    <hr>
    <!-- 目标1 在城市列表的最后添加一个子标签  <li id="cs">长沙</li>  -->
    <button onclick="addCs()">增加长沙</button>
    <!-- 目标2 在城市列表的深圳前添加一个子标签  <li id="cs">长沙</li>  -->
    <button onclick="addCsBeforeSz()">在深圳前插入长沙</button>
    <!-- 目标3  将城市列表的深圳替换为  <li id="cs">长沙</li>  -->
    <button onclick="replaceSz()">替换深圳</button>
    <!-- 目标4  将城市列表删除深圳  -->
    <button onclick="removeSz()">删除深圳</button>
    <!-- 目标5  清空城市列表  -->
    <button onclick="clearCity()">清空</button>
    
</body>
</html>
```

## 9 正则表达式

### 1 正则表达式简介

> 正则表达式是描述字符模式的对象。正则表达式用于对字符串模式匹配及检索替换，是对字符串执行模式匹配的强大工具。

+ 语法 

``` javascript
var patt=new RegExp(pattern,modifiers);
或者更简单的方式:
var patt=/pattern/modifiers; 
```

> 修饰符

| 修饰符                                             | 描述                                                     |
| :------------------------------------------------- | :------------------------------------------------------- |
| [i](https://www.runoob.com/js/jsref-regexp-i.html) | 执行对大小写不敏感的匹配。                               |
| [g](https://www.runoob.com/js/jsref-regexp-g.html) | 执行全局匹配（查找所有匹配而非在找到第一个匹配后停止）。 |
| m                                                  | 执行多行匹配。                                           |
| ^                                                  | 是否以此为开头                                           |
| $                                                  | 是否以此为结尾                                           |
| []                                                 | 一位的表达式                                             |

> 方括号

| 表达式                                                       | 描述                               |
| :----------------------------------------------------------- | :--------------------------------- |
| [[abc\]](https://www.runoob.com/jsref/jsref-regexp-charset.html) | 查找方括号之间的任何字符。         |
| [[^abc\]](https://www.runoob.com/jsref/jsref-regexp-charset-not.html) | 查找任何不在方括号之间的字符。     |
| [0-9]                                                        | 查找任何从 0 至 9 的数字。         |
| [a-z]                                                        | 查找任何从小写 a 到小写 z 的字符。 |
| [A-Z]                                                        | 查找任何从大写 A 到大写 Z 的字符。 |
| [A-z]                                                        | 查找任何从大写 A 到小写 z 的字符。 |
| [adgk]                                                       | 查找给定集合内的任何字符。         |
| [^adgk]                                                      | 查找给定集合外的任何字符。         |
| (red\|blue\|green)                                           | 查找任何指定的选项。               |

> 元字符

| 元字符                                                       | 描述                                        |
| :----------------------------------------------------------- | :------------------------------------------ |
| [.](https://www.runoob.com/jsref/jsref-regexp-dot.html)      | 查找单个字符，除了换行和行结束符。          |
| [\w](https://www.runoob.com/jsref/jsref-regexp-wordchar.html) | 查找数字、字母及下划线。                    |
| [\W](https://www.runoob.com/jsref/jsref-regexp-wordchar-non.html) | 查找非单词字符。                            |
| [\d](https://www.runoob.com/jsref/jsref-regexp-digit.html)   | 查找数字。                                  |
| [\D](https://www.runoob.com/jsref/jsref-regexp-digit-non.html) | 查找非数字字符。                            |
| [\s](https://www.runoob.com/jsref/jsref-regexp-whitespace.html) | 查找空白字符。                              |
| [\S](https://www.runoob.com/jsref/jsref-regexp-whitespace-non.html) | 查找非空白字符。                            |
| [\b](https://www.runoob.com/jsref/jsref-regexp-begin.html)   | 匹配单词边界。                              |
| [\B](https://www.runoob.com/jsref/jsref-regexp-begin-not.html) | 匹配非单词边界。                            |
| \0                                                           | 查找 NULL 字符。                            |
| [\n](https://www.runoob.com/jsref/jsref-regexp-newline.html) | 查找换行符。                                |
| \f                                                           | 查找换页符。                                |
| \r                                                           | 查找回车符。                                |
| \t                                                           | 查找制表符。                                |
| \v                                                           | 查找垂直制表符。                            |
| [\xxx](https://www.runoob.com/jsref/jsref-regexp-octal.html) | 查找以八进制数 xxx 规定的字符。             |
| [\xdd](https://www.runoob.com/jsref/jsref-regexp-hex.html)   | 查找以十六进制数 dd 规定的字符。            |
| [\uxxxx](https://www.runoob.com/jsref/jsref-regexp-unicode-hex.html) | 查找以十六进制数 xxxx 规定的 Unicode 字符。 |

> 量词

| 量词                                                         | 描述                                                         |
| :----------------------------------------------------------- | :----------------------------------------------------------- |
| [n+](https://www.runoob.com/jsref/jsref-regexp-onemore.html) | 匹配任何包含至少一个 n 的字符串。例如，/a+/ 匹配 "candy" 中的 "a"，"caaaaaaandy" 中所有的 "a"。 |
| [n*](https://www.runoob.com/jsref/jsref-regexp-zeromore.html) | 匹配任何包含零个或多个 n 的字符串。例如，/bo*/ 匹配 "A ghost booooed" 中的 "boooo"，"A bird warbled" 中的 "b"，但是不匹配 "A goat grunted"。 |
| [n?](https://www.runoob.com/jsref/jsref-regexp-zeroone.html) | 匹配任何包含零个或一个 n 的字符串。例如，/e?le?/ 匹配 "angel" 中的 "el"，"angle" 中的 "le"。 |
| [n{X}](https://www.runoob.com/jsref/jsref-regexp-nx.html)    | 匹配包含 X 个 n 的序列的字符串。例如，/a{2}/ 不匹配 "candy," 中的 "a"，但是匹配 "caandy," 中的两个 "a"，且匹配 "caaandy." 中的前两个 "a"。 |
| [n{X,}](https://www.runoob.com/jsref/jsref-regexp-nxcomma.html) | X 是一个正整数。前面的模式 n 连续出现至少 X 次时匹配。例如，/a{2,}/ 不匹配 "candy" 中的 "a"，但是匹配 "caandy" 和 "caaaaaaandy." 中所有的 "a"。 |
| [n{X,Y}](https://www.runoob.com/jsref/jsref-regexp-nxy.html) | X 和 Y 为正整数。前面的模式 n 连续出现至少 X 次，至多 Y 次时匹配。例如，/a{1,3}/ 不匹配 "cndy"，匹配 "candy," 中的 "a"，"caandy," 中的两个 "a"，匹配 "caaaaaaandy" 中的前面三个 "a"。注意，当匹配 "caaaaaaandy" 时，即使原始字符串拥有更多的 "a"，匹配项也是 "aaa"。 |
| [n$](https://www.runoob.com/jsref/jsref-regexp-ndollar.html) | 匹配任何结尾为 n 的字符串。                                  |
| [^n](https://www.runoob.com/jsref/jsref-regexp-ncaret.html)  | 匹配任何开头为 n 的字符串。                                  |
| [?=n](https://www.runoob.com/jsref/jsref-regexp-nfollow.html) | 匹配任何其后紧接指定字符串 n 的字符串。                      |
| [?!n](https://www.runoob.com/jsref/jsref-regexp-nfollow-not.html) | 匹配任何其后没有紧接指定字符串 n 的字符串。                  |

> RegExp对象方法

| 方法                                                         | 描述                                               |
| :----------------------------------------------------------- | :------------------------------------------------- |
| [compile](https://www.runoob.com/jsref/jsref-regexp-compile.html) | 在 1.5 版本中已废弃。 编译正则表达式。             |
| [exec](https://www.runoob.com/jsref/jsref-exec-regexp.html)  | 检索字符串中指定的值。返回找到的值，并确定其位置。 |
| [test](https://www.runoob.com/jsref/jsref-test-regexp.html)  | 检索字符串中指定的值。返回 true 或 false。         |
| [toString](https://www.runoob.com/jsref/jsref-regexp-tostring.html) | 返回正则表达式的字符串。                           |

> 支持正则的String的方法

| 方法                                                    | 描述                             |
| :------------------------------------------------------ | :------------------------------- |
| [search](https://www.runoob.com/js/jsref-search.html)   | 检索与正则表达式相匹配的值。     |
| [match](https://www.runoob.com/js/jsref-match.html)     | 找到一个或多个正则表达式的匹配。 |
| [replace](https://www.runoob.com/js/jsref-replace.html) | 替换与正则表达式匹配的子串。     |
| [split](https://www.runoob.com/js/jsref-split.html)     | 把字符串分割为字符串数组。       |

### 2 正则表达式体验

**1 验证**

**注意**：这里是使用**正则表达式对象**来**调用**方法。

```javascript
// 创建一个最简单的正则表达式对象
var reg = /o/;
// 创建一个字符串对象作为目标字符串
var str = 'Hello World!';
console.log(reg.test(str)) 
// 调用正则表达式对象的test()方法验证目标字符串是否满足我们指定的这个模式，返回结果true
console.log("/o/.test('Hello World!')="+reg.test(str));
```

**2 匹配**

```javascript
// 创建一个最简单的正则表达式对象
var reg = /o/;
var reg = /o/g 全文匹配
// 创建一个字符串对象作为目标字符串
var str = 'Hello World!';
// 在目标字符串中查找匹配的字符，返回匹配结果组成的数组
var resultArr = str.match(reg);
// 数组长度为1
console.log("resultArr.length="+resultArr.length);

// 数组内容是o
console.log("resultArr[0]="+resultArr[0]);
```

**3 替换**

**注意**：这里是使用**字符串对象**来**调用**方法。

```javascript
// 创建一个最简单的正则表达式对象
var reg = /o/;
var reg = /o/gi 全文匹配忽略大小写
// 创建一个字符串对象作为目标字符串
var str = 'Hello World!';
var newStr = str.replace(reg,'@');
// 只有第一个o被替换了，说明我们这个正则表达式只能匹配第一个满足的字符串
console.log("str.replace(reg)="+newStr);//Hell@ World!

// 原字符串并没有变化，只是返回了一个新字符串
console.log("str="+str);//str=Hello World!


```

**4  全文查找**

如果不使用g对正则表达式对象进行修饰，则使用正则表达式进行查找时，仅返回第一个匹配；使用g后，返回所有匹配。

```javascript
// 目标字符串
var targetStr = 'Hello World!';

// 没有使用全局匹配的正则表达式
var reg = /[A-Z]/;
// 获取全部匹配
var resultArr = targetStr.match(reg);
// 数组长度为1
console.log("resultArr.length="+resultArr.length);
// 遍历数组，发现只能得到'H'
for(var i = 0; i < resultArr.length; i++){
  console.log("resultArr["+i+"]="+resultArr[i]);
}
```

对比

```javascript
// 目标字符串
var targetStr = 'Hello World!';
// 使用了全局匹配的正则表达式
var reg = /[A-Z]/g;
// 获取全部匹配
var resultArr = targetStr.match(reg);
// 数组长度为2
console.log("resultArr.length="+resultArr.length);
// 遍历数组，发现可以获取到“H”和“W”
for(var i = 0; i < resultArr.length; i++){
  console.log("resultArr["+i+"]="+resultArr[i]);
}
```

**5 忽略大小写**

```javascript
//目标字符串
var targetStr = 'Hello WORLD!';

//没有使用忽略大小写的正则表达式
var reg = /o/g;
//获取全部匹配
var resultArr = targetStr.match(reg);
//数组长度为1
console.log("resultArr.length="+resultArr.length);
//遍历数组，仅得到'o'
for(var i = 0; i < resultArr.length; i++){
  console.log("resultArr["+i+"]="+resultArr[i]);
}
```

对比

```javascript
//目标字符串
var targetStr = 'Hello WORLD!';
//使用了忽略大小写的正则表达式
var reg = /o/gi;
//获取全部匹配
var resultArr = targetStr.match(reg);
//数组长度为2
console.log("resultArr.length="+resultArr.length);
//遍历数组，得到'o'和'O'
for(var i = 0; i < resultArr.length; i++){
  console.log("resultArr["+i+"]="+resultArr[i]);
}
```

**6 元字符使用**

```javascript
var str01 = 'I love Java';
var str02 = 'Java love me';
// 匹配以Java开头
var reg = /^Java/g;
console.log('reg.test(str01)='+reg.test(str01)); // false
console.log("<br />");
console.log('reg.test(str02)='+reg.test(str02)); // true
```

```javascript
var str01 = 'I love Java';
var str02 = 'Java love me';
// 匹配以Java结尾
var reg = /Java$/g;
console.log('reg.test(str01)='+reg.test(str01)); // true
console.log("<br />");
console.log('reg.test(str02)='+reg.test(str02)); // false
```

**7 字符集合的使用**

```javascript
//n位数字的正则
var targetStr="123456789";
var reg=/^[0-9]{0,}$/;
//或者 ： var reg=/^\d*$/;
var b = reg.test(targetStr);//true
```

```javascript
//数字+字母+下划线，6-16位
var targetStr="HelloWorld";
var reg=/^[a-z0-9A-Z_]{6,16}$/;
var b = reg.test(targetStr);//true
```

### **3 常用正则表达式**

| 需求                                   | 正则表达式                                                   |
| -------------------------------------- | ------------------------------------------------------------ |
| 用户名                                 | /^\[a-zA-Z ]\[a-zA-Z-0-9]{5,9}\$/                            |
| 密码                                   | /^\[a-zA-Z0-9 \_-@#& \*]{6,12}\$/                            |
| 密码8-14位大小写特殊符合数字至少有一位 | /^(?=.*[a - z])(?=.*[A - Z])(?=.*\d)(?=.*[@$!%*?&])[A - Za - z\d@$!%*?&]{8,14}$/ |
| 前后空格                               | /^\s+\|\s+\$/g                                               |
| 电子邮箱                               | /^\[a-zA-Z0-9 \_.-]+@(\[a-zA-Z0-9-]+\[.]{1})+\[a-zA-Z]+\$/   |
| 手机号                                 | /^1[3-9]\d{9}$/                                              |

## 10 JSON格式

### 1 json对象

>  JSON（JavaScript Object Notation, JS对象简谱）是一种轻量级的数据交换格式,JSON 就是一种字符串格式,这种格式无论是在前端还是在后端,都可以很容易的转换成对象,所以常用于前后端数据传递

+ 说明

  + JSON的语法

    ​		var obj='{"属性名":"属性值","属性名":{"属性名":"属性值"},"属性名":["值1","值1","值3"]}'

    属性名一定要用双引号，属性值如果是字符串一定要用双引号，是数字可以不处理 多个之间用，隔开，属性值可以是数组数组的元素也可以是对象 ，也可以是对象

  + JSON字符串一般用于传递数据,所以字符串中的函数就显得没有意义,在此不做研究

  + 通过JSON.parse()方法可以将一个JSON串转换成对象

  + 通过JSON.stringify()方法可以将一个对象转换成一个JSON格式的字符串

  + 前后端传递数据

  + 当数据在浏览器与服务器之间进行交换时，这些数据只能是文本。

    JSON 属于文本，并且我们能够把任何 JavaScript 对象转换为 JSON，然后将 JSON 发送到服务器。

    我们也能把从服务器接收到的任何 JSON 转换为 JavaScript 对象。

    以这样的方式，我们能够把数据作为 JavaScript 对象来处理，无需复杂的解析和转译。

    ![json数据传递.png](http://yanxuan.nosdn.127.net/1ece69553a9a0bc6d410685e9efed9a2.png)

+ json.parse()   json转对象

``` javascript
/* 定义一个JSON串 */
var personStr ='{"name":"张小明","age":20,"girlFriend":{"name":"铁铃","age":23},"foods":["苹果","香蕉","橘子","葡萄"],"pets":[{"petName":"大黄","petType":"dog"},{"petName":"小花","petType":"cat"}]}'
console.log(personStr)
console.log(typeof personStr)
/* 将一个JSON串转换为对象 */
var person =JSON.parse(personStr);
console.log(person)
console.log(typeof person)
/* 获取对象属性值 */
console.log(person.name)
console.log(person.age)
console.log(person.girlFriend.name)
console.log(person.foods[1])
console.log(person.pets[1].petName)
console.log(person.pets[1].petType)
```

JSON.stringify(对象)  对象转字符串 

``` javascript
/* 定义一个对象 */
var person={
    'name':'张小明',
    'age':20,
    'girlFriend':{
        'name':'铁铃',
        'age':23
    },
    'foods':['苹果','香蕉','橘子','葡萄'],
    'pets':[
        {
            'petName':'大黄',
            'petType':'dog'
        },
        {
            'petName':'小花',
            'petType':'cat'
        }
    ]
}

/* 获取对象属性值 */
console.log(person.name)
console.log(person.age)
console.log(person.girlFriend.name)
console.log(person.foods[1])
console.log(person.pets[1].petName)
console.log(person.pets[1].petType)
/* 将对象转换成JSON字符串 */
var personStr =JSON.stringify(person)
console.log(personStr)
console.log(typeof personStr)
```

### 2 json数组

```
[  "Porsche", "BMW", "Volvo" ]
```

```
SON 中的数组几乎与 JavaScript 中的数组相同。
在 JSON 中，数组值的类型必须属于字符串、数字、对象、数组、布尔或 null。
在 JavaScript 中，数组值可以是以上所有类型，外加任何其他有效的 JavaScript 表达式，包括函数、日期和 undefined
```

遍历数组

```
for (i in myObj.cars) {
     x  += myObj.cars[i];
}
```

## 11 ES6

### 11.1. es6的介绍

> ECMAScript 6，简称ES6，是**JavaScript**语言的一次重大更新于**2015**年发布，ES6带来了大量的新特性，包括箭头函数、模板字符串、let和const关键字、解构、默认参数值、模块系统等等.

1.  更加简洁：ES6引入了一些新的语法，如箭头函数、类和模板字符串等，使代码更加简洁易懂。
2.  更强大的功能：ES6引入了一些新的API、解构语法和迭代器等功能，从而使得JavaScript更加强大。
3.  更好的适用性：ES6引入的模块化功能为JavaScript代码的组织和管理提供了更好的方式，不仅提高了程序的可维护性，还让JavaScript更方便地应用于大型的应用程序。

**历史版本：**

### 11.2 es6的变量和模板字符串

> ES6 新增了`let`和`const`，用来声明变量,使用的细节上也存在诸多差异

+ let 和var的差别

  1、let 不能重复声明

  2、let有块级作用域，非函数的花括号遇见let会有块级作用域，也就是只能在花括号里面访问。

  3、let不会预解析进行变量提升

  4、let 定义的全局变量不会作为window的属性

  5、let在es6中推荐优先使用

```html
<script>
    //1. let只有在当前代码块有效代码块. 代码块、函数、全局
    {
      let a = 1
      var b = 2
    }   
    console.log(a);  // a is not defined   花括号外面无法访问
    console.log(b);  // 可以正常输出

    //2. 不能重复声明
    let name = '天真'
    let name = '无邪'

    //3. 不存在变量提升（先声明，在使用）
    console.log(test) //可以     但是值为undefined
    var test = 'test'
    console.log(test1) //不可以  let命令改变了语法行为，它所声明的变量一定要在声明后使用，否则报错。
    let test1 = 'test1' 


    //4、不会成为window的属性   
    var a = 100
    console.log(window.a) //100
    let b = 200
    console.log(window.b) //undefined

    //5. 循环中推荐使用
    for (let i = 0; i < 10; i++) {
      // ...
    }
    console.log(i);
</script>
```

+ const和var的差异

  1、新增const和let类似，只是const定义的变量不能修改

  2、并不是变量的值不得改动，而是引用地址值不可改变。

```html
<script>
    //声明场景语法,建议变量名大写区分
    const PI = 3.1415926;

    //1.常量声明必须有初始化值
    //const A ; //报错

    //2.常量值不可以改动
    //const A  = 'atguigu'
    //A  = 'xx' //不可改动

    //3.和let一样，块儿作用域
    {
        const A = 'atguigu'
        console.log(A);
    }
    //console.log(A);

    //4.对应数组和对象元素修改，不算常量修改，修改值，不修改地址
    const TEAM = ['刘德华','张学友','郭富城'];
    TEAM.push('黎明');
    TEAM=[] // 报错
    console.log(TEAM)
</script>
```

> 模板字符串（template string）是增强版的字符串，用反引号（`）标识  

1、字符串中可以出现换行符

2、可以使用 ${xxx} 形式输出变量和拼接变量

``` html
<script>
    // 1 多行普通字符串
    let ulStr =
        '<ul>'+
        '<li>JAVA</li>'+
        '<li>html</li>'+
        '<li>VUE</li>'+
        '</ul>'
    console.log(ulStr)    
    // 2 多行模板字符串
    let ulStr2 = `
        <ul>
        	<li>JAVA</li>
        	<li>html</li>
        	<li>VUE</li>
        </ul>`
    console.log(ulStr2)        
    // 3  普通字符串拼接
    let name ='张小明'
    let infoStr =name+'被评为本年级优秀学员'  
    console.log(infoStr)
    // 4  模板字符串拼接
    let infoStr2 =`${name}被评为本年级优秀学员`
    console.log(infoStr2)
</script>
```

### 11.3 es6的解构表达式

> ES6 的解构赋值是一种方便的语法，可以快速将数组或对象中的值拆分并赋值给变量。解构赋值的语法使用花括号 `{}` 表示对象，方括号 `[]` 表示数组。通过解构赋值，函数更方便进行参数接受等！

> **数组解构赋值**

+ 可以通过数组解构将数组中的值赋值给变量，语法为：

```javascript
let [a, b, c] = [1, 2, 3]; //新增变量名任意合法即可，本质是按照顺序进行初始化变量的值
console.log(a); // 1
console.log(b); // 2
console.log(c); // 3
```

+ 该语句将数组 \[1, 2, 3] 中的第一个值赋值给 a 变量，第二个值赋值给 b 变量，第三个值赋值给 c 变量。
  可以使用默认值为变量提供备选值，在数组中缺失对应位置的值时使用该默认值。例如：

```javascript
let [a, b, c, d = 4] = [1, 2, 3];
console.log(d); // 4
```

> **对象解构赋值**

+ 可以通过对象解构将对象中的值赋值给变量，语法为：

```javascript
let {a, b} = {a: 1, b: 2};
//新增变量名必须和属性名相同，本质是初始化变量的值为对象中同名属性的值
//等价于 let a = 对象.a  let b = 对象.b
  
console.log(a); // 1
console.log(b); // 2
```

+ 该语句将对象 {a: 1, b: 2} 中的 a 属性值赋值给 a 变量，b 属性值赋值给 b 变量。
  可以为标识符分配不同的变量名称，使用 : 操作符指定新的变量名。例如：

```javascript
let {a: x, b: y} = {a: 1, b: 2};
console.log(x); // 1
console.log(y); // 2
```

> **函数参数解构赋值**

+ 解构赋值也可以用于函数参数。例如：

```javascript
function add([x, y]) {
  return x + y;
}
add([1, 2]); // 3
```

+ 该函数接受一个数组作为参数，将其中的第一个值赋给 x，第二个值赋给 y，然后返回它们的和。

+ ES6 解构赋值让变量的初始化更加简单和便捷。通过解构赋值，我们可以访问到对象中的属性，并将其赋值给对应的变量，从而提高代码的可读性和可维护性。

### 11.4 es6的箭头函数

> ES6 允许使用“箭头” 义函数。语法类似Java中的Lambda表达式

#### 11.4.1 声明和特点

```html
<script>

    //ES6 允许使用“箭头”（=>）定义函数。
    //1. 函数声明
    let fn1 = function(){}
    let fn2 = ()=>{} //箭头函数,此处不需要书写function关键字
    let fn3 = x =>{} //单参数可以省略(),多参数无参数不可以!
    let fn4 = x => console.log(x) //只有一行方法体可以省略{};
    let fun5 = x => x + 1 //当函数体只有一句返回值时，可以省略花括号和 return 语句
    //2. 使用特点 箭头函数this关键字
    // 在 JavaScript 中，this 关键字通常用来引用函数所在的对象，
    // 或者在函数本身作为构造函数时，来引用新对象的实例。
    // 但是在箭头函数中，this 的含义与常规函数定义中的含义不同，
    // 并且是由箭头函数定义时的上下文来决定的，而不是由函数调用时的上下文来决定的。
    // 箭头函数没有自己的this，this指向的是外层上下文环境的thisWindow对象
    
    let person ={
        name:"张三",
        showName:function (){
            console.log(this) //  这里的this是person
            console.log(this.name)
        },
        viewName: () =>{
            console.log(this) //  这里的this是window
            console.log(this.name)
        }
    }
    person.showName()
    person.viewName()
 
    //this应用
    function Counter() {
        this.count = 0;
        setInterval(() => {
            // 这里的 this 是上一层作用域中的 this，即 Counter实例化对象
            this.count++;
            console.log(this.count);
        }, 1000);
    }
    let counter = new Counter();

</script>
```

#### 2.4.2 实践和应用场景

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Document</title>
    <style>
        #xdd{
            display: inline-block;
            width: 200px;
            height: 200px;
            background-color: red;
        }
    </style>
</head>
<body>
    <div id="xdd"></div>
    <script>
       let xdd = document.getElementById("xdd");
       // 方案1 
       xdd.onclick = function(){
            console.log(this)
            let _this= this;  //this 是xdd
            //开启定时器
            setTimeout(function(){
                console.log(this)
                //变粉色
                _this.style.backgroundColor = 'pink';
            },2000);
        }
        // 方案2
        xdd.onclick = function(){
            console.log(this)
            //开启定时器
            setTimeout(()=>{
                console.log(this)// 使用setTimeout() 方法所在环境时的this对象
                //变粉色
                this.style.backgroundColor = 'pink';
            },2000);
        }
    </script>
</body>
</html>
```

#### 2.4.3 rest和spread

> rest参数,在形参上使用 和JAVA中的可变参数几乎一样

``` html
<script>
    
    //   rest 剩余的，解决剩余的参数接收问题
    let fun1 =(a,b,c,d,... arr)=>{
        console.log(a,b,c,d);
        console.log(arr);
    }
fun1(1,2,3,4,6,7,8,[1,2,3])
    
    
    // 1 参数列表中多个普通参数  普通函数和箭头函数中都支持
    let fun1 = function (a,b,c,d=10){console.log(a,b,c,d)}
    let fun2 = (a,b,c,d=10) =>{console.log(a,b,c,d)}
    fun1(1,2,3)
    fun2(1,2,3,4)
    // 2 ...作为参数列表,称之为rest参数 普通函数和箭头函数中都支持 ,因为箭头函数中无法使用arguments,rest是一种解决方案
    let fun3 = function (...args){console.log(args)}
    let fun4 = (...args) =>{console.log(args)}
    fun3(1,2,3)
    fun4(1,2,3,4)
    // rest参数在一个参数列表中的最后一个只,这也就无形之中要求一个参数列表中只能有一个rest参数
    //let fun5 =  (...args,...args2) =>{} // 这里报错
</script>
```

> spread参数,在实参上使用rest

```html
<script>
    let arr =[1,2,3]
    //let arrSpread = ...arr;// 这样不可以,...arr必须在调用方法时作为实参使用
    let fun1 =(a,b,c) =>{
        console.log(a,b,c)
    }
    // 调用方法时,对arr进行转换 转换为1,2,3 
    fun1(...arr)
    //应用场景1 合并数组
    let arr1=[1,2,3]
    let arr2=[4,5,6]
    let arr3=[...arr1,...arr2]
    console.log(arr3)
    //应用场景2 合并对象属性
    let p1={name:"张三"}
    let p2={age:10}
    let p3={gender:"boy"}
    let person ={...p1,...p2,...p3}
    console.log(person)

</script>
```

### 11.5 es6的对象创建和拷贝

#### 11.5.1 对象创建的语法糖

> ES6中新增了对象创建的语法糖,支持了class extends constructor等关键字,让ES6的语法和面向对象的语法更加接近

#n 私有的属性但是要注意 #n 和n是两个属性

``` javascript
class Person{
      // 属性
      #n;
      age;
      get name(){
          return this.n;
      }
      set name(n){
          this.n =n;
      }
      // 实例方法
      eat(food){
          console.log(this.age+"岁的"+this.n+"用筷子吃"+food)
      }
      // 静态方法
      static sum(a,b){
          return a+b;
      }
      // 构造器
      constructor(name,age){
          this.n=name;
          this.age = age;

      }
  }
  let person =new Person("张三",10);
  // 访问对象属性
  // 调用对象方法
  console.log(person.name)
  console.log(person.n)
  person.name="小明"
  console.log(person.age)
  person.eat("火锅")
  console.log(Person.sum(1,2))

  class Student extends  Person{
      grade ;
      score ;
      study(){

      }
      constructor(name,age ) {
          super(name,age);
      }
  }

  let stu =new Student("学生小李",18);
  stu.eat("面条")
```

#### 11.5.2  对象的深拷贝和浅拷贝

> 对象的拷贝,快速获得一个和已有对象相同的对象的方式

+ 浅拷贝

``` html
<script>
    let arr  =['java','c','python']
    let person ={
        name:'张三',
        language:arr
    }
    // 浅拷贝,person2和person指向相同的内存
    let person2 = person;
    person2.name="小黑"
    console.log(person.name)
</script>
```

+ 深拷贝

``` js
<script>
    let arr  =['java','c','python']
    let arr2 =[...arr]
    let person ={
        name:'张三',
        language:arr
    }
    // 深拷贝,通过JSON和字符串的转换形成一个新的对象
    let person2 = JSON.parse(JSON.stringify(person))
    person2.name="小黑"
    console.log(person.name)
    console.log(person2.name) 
</script>
```

### 11.6 es6的模块化处理

#### 11.6.1模块化介绍

> 模块化是一种组织和管理前端代码的方式，将代码拆分成小的模块单元，使得代码更易于维护、扩展和复用。它包括了定义、导出、导入以及管理模块的方法和规范。前端模块化的主要优势如下：

1.  提高代码可维护性：通过将代码拆分为小的模块单元，使得代码结构更为清晰，可读性更高，便于开发者阅读和维护。
2.  提高代码可复用性：通过将重复使用的代码变成可复用的模块，减少代码重复率，降低开发成本。
3.  提高代码可扩展性：通过模块化来实现代码的松耦合，便于更改和替换模块，从而方便地扩展功能。

+ ES6模块化的几种暴露和导入方式
  1. 分别导出
  2. 统一导出
  3. 默认导出
+ `ES6中无论以何种方式导出,导出的都是一个对象,导出的内容都可以理解为是向这个对象中添加属性或者方法`

#### 2.6.2 分别导出

+ module.js 向外分别暴露成员

``` javascript
//1.分别暴露
// 模块想对外导出,添加export关键字即可!
// 导出一个变量
export const PI = 3.14
// 导出一个函数
export function sum(a, b) {
  return a + b;
}
// 导出一个类
export class Person {
  constructor(name, age) {
    this.name = name;
    this.age = age;
  }
  sayHello() {
    console.log(`Hello, my name is ${this.name}, I'm ${this.age} years old.`);
  }
}
```

+ app.js 导入module.js中的成员

``` javascript
/* 
    *代表module.js中的所有成员
    m1代表所有成员所属的对象
*/
import * as m1 from './module.js'
// 使用暴露的属性
console.log(m1.PI)
// 调用暴露的方法
let result =m1.sum(10,20)
console.log(result)
// 使用暴露的Person类
let person =new m1.Person('张三',10)
person.sayHello()
```

+ index.html作为程序启动的入口  导入 app.js  

``` html
<!-- 导入JS文件 添加type='module' 属性,否则不支持ES6的模块化 -->
<script src="./app.js" type="module" /> 
```

#### 2.6.3 统一导出

+ module.js向外统一导出成员

``` javascript
//2.统一暴露
// 模块想对外导出,export统一暴露想暴露的内容!
// 定义一个常量
const PI = 3.14
// 定义一个函数
function sum(a, b) {
  return a + b;
}
// 定义一个类
class Person {
  constructor(name, age) {
    this.name = name;
    this.age = age;
  }
  sayHello() {
    console.log(`Hello, my name is ${this.name}, I'm ${this.age} years old.`);
  }
}
// 统一对外导出(暴露)
export {
	PI,
    sum,
    Person
}
```

+ app.js导入module.js中的成员

``` javascript
/* 
    {}中导入要使用的来自于module.js中的成员
    {}中导入的名称要和module.js中导出的一致,也可以在此处起别名
    {}中如果定义了别名,那么在当前模块中就只能使用别名
    {}中导入成员的顺序可以不是暴露的顺序
    一个模块中可以同时有多个import
    多个import可以导入多个不同的模块,也可以是同一个模块
*/
//import {PI ,Person ,sum }  from './module.js'
//import {PI as pi,Person as People,sum as add}  from './module.js'
import {PI ,Person ,sum,PI as pi,Person as People,sum as add}  from './module.js'
// 使用暴露的属性
console.log(PI)
console.log(pi)
// 调用暴露的方法
let result1 =sum(10,20)
console.log(result1)
let result2 =add(10,20)
console.log(result2)
// 使用暴露的Person类
let person1 =new Person('张三',10)
person1.sayHello()
let person2 =new People('李四',11)
person2.sayHello()
```

#### 2.6.4 默认导出

+ app.js 的default和其他导入写法混用

``` javascript
/* 
    *代表module.js中的所有成员
    m1代表所有成员所属的对象
*/
import * as m1 from './module.js'
import {default as add} from './module.js' // 用的少
import add2 from './module.js' // 等效于 import {default as add2} from './module.js'

// 调用暴露的方法
let result =m1.default(10,20)
console.log(result)
let result2 =add(10,20)
console.log(result2)
let result3 =add2(10,20)
console.log(result3)

// 引入其他方式暴露的内容
import {PI,Person} from './module.js'
// 使用暴露的Person类
let person =new Person('张三',10)
person.sayHello()
// 使用暴露的属性
console.log(PI)
```



#### 2.6.5 混合导出

+ modules混合向外导出

``` javascript
// 3默认和混合暴露
/* 
    默认暴露语法  export default sum
    默认暴露相当于是在暴露的对象中增加了一个名字为default的属性
    三种暴露方式可以在一个module中混合使用

*/
export const PI = 3.14
// 导出一个函数
function sum(a, b) {
  return a + b;
}
// 导出一个类
class Person {
  constructor(name, age) {
    this.name = name;
    this.age = age;
  }
  sayHello() {
    console.log(`Hello, my name is ${this.name}, I'm ${this.age} years old.`);
  }
}

// 导出默认
export default sum
// 统一导出
export {
   Person
}

```

# 12 其他特性

1. canvas 
2. Three.js  
3. movy.js   
4.   CodeMirror编辑器 Monaco Editor