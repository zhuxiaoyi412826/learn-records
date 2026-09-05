# HTML

## 1 HTMLCSS JS

> HTML 主要用于网页主体结构的搭建

![1681115424967](F:/资料/java/3.javaweb/尚硅谷2023JavaWeb图书配套资料/笔记/images/1681115424967.png)





> CSS 主要用于页面元素美化

<img src="F:/资料/java/3.javaweb/尚硅谷2023JavaWeb图书配套资料/笔记/images/1681115460825.png" alt="1681115460825" style="zoom: 67%;" />

> JavaScript 主要用于页面元素的动态处理

<img src="F:/资料/java/3.javaweb/尚硅谷2023JavaWeb图书配套资料/笔记/images/js.gif" alt="js" style="zoom:67%;" />



TypeScript 是微软开发的 JavaScript 超集，静态类型

## 2 什么是HML

1 https://www.w3school.com.cn/html/index.asp 官方文档

https://www.runoob.com/html/html-tutorial.html 菜鸟教程

2 什么是HTML

```
HTML是Hyper Text Markup Language的写，意是文本。它的作用是网页结构，
在网页上展示。HTML5在 2008 式发，在 2012 成了定的本，2014 10 28
，W3C发了HTML5的终版
```

3 什么是超文本

```
HTML文件本质上是文本文件，普通的文本文件只能示。通过其他网页、、
、等各种体资源入到前网页中，网页有了非常的现方式，就是文本的：本身是文本，但是呈现出来的最终效果超越了文本。
```

4 什么是标记语言

```
HTML是一种标记语言，是为它不是像Java的编程，为它是由一系标签成
的，有常、、程、常处理、IO等等些功能。HTML很简单，每个固定的标签都有它
定的和定的页面示效果。
```

5 HTML基础结构

```
1文档明：HTML文件中一行的，用来浏览器前HTML文档的本信以及HTML文
档的。<!DOCTYPE html>。
2根标签：<html></html>是个文档的根，所有其他都必须在对面。
3头部元素：<head></head>是<html></html>一个一级,用于定文档的部，
其他部都在head。部title、script、style、link、
meta等等。
4主体：<body></body>是<html></html>个一级,用于定网页的主
体，在浏览器口示的都定到body。
```

6 HTML入门程序

```
<!DOCTYPE html>
 <html lang="en">
    <head>
 <meta charset="UTF-8">
 <title>我的第一个网页</title>
    </head>
    <body>
 hello html!!!
    </body>
 </html>
```

![HTMLDemo.jpg](http://yanxuan.nosdn.127.net/95fc543d4c1b9c445565e83b458a8705.jpg)

7 HTML概念名词解释

+ 代码中的一个 <> 叫做一个标签,有些标签成对出现,称之为双标签,有些标签单独出现,称之为单标签

> 属性

+ 一般在开始标签中,用于定义标签的一些特征

> 文本

+ 双标签中间的文字,包含空格换行等结构

> 元素

+ 经过浏览器解析后,每一个完整的标签(标签+属性+文本)可以称之为一个元素

8 HTML语法规则

+ 1 根标签有且只能有一个

+ 2 无论是双标签还是单标签都需要正确关闭

+ 3 标签可以嵌套但不能交叉嵌套

+ 4 注释语法为<!-- -->  ,注意不能嵌套

+ 5 属性必须有值，值必须加引号,H5中属性名和值相同时可以省略属性值

+ 6 HTML中不严格区分字符串使用单双引号

+ 7 HTML标签不严格区分大小写,但是不能大小写混用

+ 8 HTML中不允许自定义标签名,强行自定义则无效

**9 VSCode**

https://code.visualstudio.com

安装过程一路next

**插件安装**

+ Auto Rename Tag 自动修改标签对插件
+ Chinese Language Pack  汉化包
+ HTML CSS Support  HTML CSS 支持
+ Intellij IDEA Keybindings IDEA快捷键支持
+ Live Server 实时加载功能的小型服务器
+ open in  browser 通过浏览器打开当前文件的插件
+ Prettier-Code formatter 代码美化格式化插件
+ Vetur   VScode中的Vue工具插件
+ vscode-icons  文件显示图标插件
+ Vue 3 snipptes 生成VUE模板插件
+ Vue language Features Vue3语言特征插件

**常见设置**

+ 设置字体:    齿轮>search>搜索    "字体大小"
+ 设置字体大小可以用滚轮控制:  齿轮>设置>搜索 "Mouse Wheel Zoom"
+ 设置左侧树缩进: 齿轮>设置>搜索 "树缩进"
+ 设置文件夹折叠:  齿轮>设置>搜索 "compact" 取消第一个勾选
+ 设置编码自动保存: 齿轮> 设置> 搜索 "Auto Save" ,选择为"afterDelay"

## 3 HTML常见标签

```html
<body style="background-color: #333">  
</body>
设置body背景颜色
```

### 1 文本标签

1 标签标题  h1到h6

```
<body>
 <h1>一级标题</h1>
 <h2>二级标题</h2>
 <h3>三级标题</h3>
 <h4>四级标题</h4>
 <h5>五级标题</h5>
 <h6>六级标题</h6>
 </body>
```

2 段落标题 <body>

```
<body>
 <p>记者从工信部了解到，近年来我国算力产业规模快速增长，年增长率近30%，算力规模排名全球第二。</p>
 <p>工信部统计显示，截至去年底，我国算力总规模达到180百亿亿次浮点运算/秒，存力总规模超过
1000EB（1万亿GB）。</p>
 <p>近年来，我国算力基础设施发展成效显著，梯次优化的算力供给体系初步构建，算力基础设施的综合能力显
著提升。</p>
 </body>
```

3换行标题 <br>

分割线<hr>

```
<body>
<br>
截至去年底，我国算力总规模达到180百亿亿次浮点运算/秒，存力总规模超过1000EB（1万亿GB）。
国家枢纽节点间的网络单向时延降低到20毫秒以内，算力核心产业规模达到1.8万亿元。<hr>
中国信息通信研究院测算，算力每投入1元，将带动3至4元的GDP经济增长。
</body>
```

4 列表标签  

有序列表<ol>  

```
<ol>
 <li>JAVA</li>
 <li>前端</li>
 <li>大数据</li>
 </ol>
```

无序列表 <ul>

```
<ul>
 <li>JAVASE</li>
 <li>JAVAEE</li>
 <li>数据库</li>
 </ul>
```

嵌套列表<li>

```
<ol>
 <li>
        JAVA
 <ul>
 <li>JAVASE</li>
 <li>JAVAEE</li>
 <li>数据库</li>
 </ul>
 </li>
 <li>前端</li>
 <li>大数据</li>
 </ol>
```

### 2 超链接标签

href 属性用于定义链接地址

href可以使用绝对路径 相对路径 完整URL

```
<body>
 <a href="01html的基本结构.html" target="_blank">相对路径本地资源连接</a> <br>
 <a href="/day01-html/01html的基本结构.html" target="_self">绝对路径本地资源连接</a> <br>
 <a href="http://www.atguigu.com" target="_blank">外部资源链接</a> <br>
 </body
```

### 3 多媒体标签 

img 图片标签在页面上引入图片

src定义图片的连接

title定义鼠标悬停是显示的文字

alt定义图片加载失败时显示的提升文字

```
<img src="img/logo.png"  title="尚硅谷" alt="尚硅谷logo" />
```

audio在页面引入声音  video在页面引入视频

src定义目标资源

autoplay控制打开页面是否自动播放

controls是否展示控制面板

loop是否进行循环播放

```
<audio src="img/music.mp3" autoplay="autoplay" controls="controls" loop="loop" />
```

```
<video src="img/movie.mp4" autoplay="autoplay" controls="controls" loop="loop" 
width="400px" />
```

### **4 表格标签**

`<table>` 标签用来定义一个表格。所有表格内容都需包含在 `<table>` 标签内。

`<tr>` 代表表格行（table row），用来定义表格中的每一行。一个 `<table>` 标签内可包含多个 `<tr>` 标签，每个 `<tr>` 标签对应表格的一行

`<td>` 表示表格数据单元格（table data cell），用来定义表格中的一个单元格。每个 `<tr>` 标签内可包含多个 `<td>` 标签，每个 `<td>` 标签对应表格中的一个单元格。

`<th>` 指表格表头单元格（table header cell），用于定义表格的表头。与 `<td>` 标签类似，但 `<th>` 标签中的内容通常会加粗居中显示，以突出表头。

- `<thead>`：用来定义表格的表头部分，通常包含 `<tr>` 和 `<th>` 标签。
- `<tbody>`：用于定义表格的主体部分，包含表格的实际数据行。
- `<tfoot>`：用来定义表格的页脚部分，一般包含汇总信息              

但是可以省略不写

![表格标签.jpg](http://yanxuan.nosdn.127.net/91c3926a69fec63f09e63f6c4c4b004c.jpg)

通过td的rowspan属性来实现上下格跨行

![单元格跨行.jpg](http://yanxuan.nosdn.127.net/86ed52a61aef2c0593bbdbcd21cd98b6.jpg)

通过td的colspan属性来实现左右跨行

![左右跨行.jpg](http://yanxuan.nosdn.127.net/ed78d65f1f1cd6f9a48004cbef85a3d8.jpg)

tr*3 >td*3   快速创建表格

### 5 表单标签

```
input[type="password"] {
  border: none;
}
input[type="text"] {
  border: none;
}
去除单选框边框
border: none; 用于去除输入框的边框；outline: none; 用于移除输入框在获得焦点时可能出现的轮廓线；background-color: transparent; 则将输入框的背景设置为透明，确保整体视觉上没有边框效果
transition: border 0.3s ease; 为边框变化添加了过渡动画，使效果更加平滑。当输入框获得焦点时，
 <div class="form-group">
                    <input type="text" placeholder="账户名/手机号/邮箱">
                </div>
                <div class="form-group">
                    <input type="password" placeholder="密码">
                </div>  
```

表单标签，可以实现让用户在界面上输入各种信息并提交的一种标签，是向服务端发送数据主要

的方式之一：

- form ：让用户输入信息向的表单标签

- action 属性：定义用户提交服务器的地址

- method属性：定义信息提交的方式


1. get  get方式提交数据会后缀到URL后，以？作为参数开始的标记，多个参数用&隔开

URL?key=value&key=value

2. 数据直接暴露在外面不安全  

3. 地址栏长度是有限制的

4. 地址栏上只能提交文字

5. 相对于post效率高一些

​    post  post数据会通过请求体发送，

- 默认不会后缀到URL后面
- 数据不会直接暴露在地址栏上，相对安全

- 数据是单独打包通过请求体发送，提交的数据量比较大

- 请求体中，可以是字符，也可以说是字节数据，可以提交文件

input定义表单项

​     type属性定义表单项类型

​                  text 文本框

​                   password密码框

​                   submit提交按钮

​                    reset重置按钮

表单项提交时需要明确value的值     value是别名的意思   name属性定义提交的参数名

```
<form action="http://www.atguigu.com" method="get">
用户名 <input type="text" name="username" vaule="1"/> <br>
密  码 <input type="password" name="password" /> <br>
<input type="submit"  value="登录" />
<input type="reset"  value="重置" />
</form>
```

### 6 表单项标签

border: none; 让边框消失

placeholder="长度6-12位"`placeholder` 属性能够在文本框内显示提示性文本，当用户开始输入时，提示文本就会自动消失。

`autocomplete="off"` 禁止浏览器自动填充文本框

1 单行文本框 

```
个性签名：<input type="text" name="signal"/><br/>
```

2 密码框

```
密码：<input type="password" name="secret" /><br/>
```

3 单选框

```
<input type="radio" name="sex" value="boy" />男
<input type="radio" name="sex" value="girl" checked="checked" />女
多个单选框使用同一个name有互斥效果   checked为默认属性    value为传输的值
```

4 复选框

```
<input type="checkbox" name="team" value="Brazil"/>巴西
<input type="checkbox" name="team" value="German" checked/>德国
<input type="checkbox" name="team" value="France"/>法国
<input type="checkbox" name="team" value="China" checked="checked"/>中国
<input type="checkbox" name="team" value="Italian"/>意大利
```

5 下拉框

select标签用来定义下来列表，而option设置列表项

name属性在select标签中设置，value属性在option标签中设置；

option标签的标签体是显示出来给用户看的，提交到服务器的是value属性的值；

通过在option标签中设置selected="selected"属性实现默认选中的效果；

```
<select name="sport">
 <option value="swimming">游泳</option>
 <option value="running">跑步</option>
 <option value="shooting" selected="selected">射击</option>
 <option value="skating">溜冰</option>
 </select>
```

6 按钮

```
<button type="button">普通按钮</button> 或    <input type="button" value="普通按钮"/>
<button type="reset">重置按钮</button>   或    <input type="reset" value="重置按钮"/>
<button type="submit">提交按钮</button> 或    <input type="submit" value="提交按钮"/>
```

普通按钮：点击后无效果，需要通过JavaScript绑定单击响应函数；

重置按钮：点击后将表单内的所有表单项都恢复为默认值；

提交按钮： 点击后提交表单；

7 隐藏域 数据不显示在页面上

```
<input type="hidden" name="userId" value="2233"/>
```

readonly 只读 提交时携带

diabled 不可用提交时,不携带

8 多行文本框

自我介绍：<textarea name="desc"></textarea>

9 选择头像：

<input type="file">

### 7 布局标签 

div标签： 俗称"块"，主要用于划分页面结构，做页面布局。

span标签： 俗称"层"，可以用于划分元素范围，配合CSS做页面元素样式的修饰。

```
 <div style="width: 500px; height: 400px;background-color: cadetblue;">
        <div style="width: 400px; height: 100px;background-color: beige;margin: 10px 
auto;">
            <span style="color: blueviolet;">页面开头部分</span>
        </div>
        <div style="width: 400px; height: 100px;background-color: 
blanchedalmond;margin: 10px auto;">
            <span style="color: blueviolet;">页面中间部分</span>
        </div>
        <div style="width: 400px; height: 100px;background-color: burlywood;margin: 
10px auto;">
            <span style="color: blueviolet;">页面结尾部分</span>
        </div>
    </div>
```

# CSS

CSS  层叠样式表，能够对网页中元素位置的排版进行像素级精确控制，支持几乎所有的字体字号样式，拥有对网页对象和模型样式编辑的能力 ,简单来说,美化页面

```
transform: scale(1.5); /* 使用 transform 属性的 scale 方法将元素整体放大 1.5 倍 */
    transform-origin: center; /* 设置缩放的中心点为元素中心，默认也是中心 */
```



## 1 CSS引入方式

1 行内式,通过元素开始标签的style属性引入, 样式语法为       style="样式名:样式值; 样式名:样式值;"

+ 代码

```html
    <input 
        type="button" 
        value="按钮"
        style="
            display: block;
            width: 60px; 
            height: 40px; 
            background-color: rgb(140, 235, 100); 
            color: white;
            border: 3px solid green;
            font-size: 22px;
            font-family: '隶书';
            line-height: 30px;
            border-radius: 5px;
    "/> 
```

+ 效果

![css按钮效果.png](http://yanxuan.nosdn.127.net/a23803d14386766f57eccca57eb6a300.png)

+ 缺点
  + html代码和css样式代码交织在一起,增加阅读难度和维护成本,影响性能
  + css样式代码仅对当前元素有效,代码重复量高,复用度低

2 内嵌式

+ 代码

``` html
<head>
    <meta charset="UTF-8">
    <style>
        /* 通过选择器确定样式的作用范围 */
        input {
            display: block;
            width: 80px; 
            height: 40px; 
            background-color: rgb(140, 235, 100); 
            color: white;
            border: 3px solid green;
            font-size: 22px;
            font-family: '隶书';
            line-height: 30px;
            border-radius: 5px;
        }
    </style>
</head>
<body>
    <input type="button" value="按钮1"/> 
    <input type="button" value="按钮2"/> 
    <input type="button" value="按钮3"/> 
    <input type="button" value="按钮4"/> 
</body>
```



+ 效果

![按钮4.png](http://yanxuan.nosdn.127.net/9a61284bbca5ffa030f99ce27735dc53.png)



+ 说明
  + 内嵌式样式需要在head标签中,通过一对style标签定义CSS样式
  + CSS样式的作用范围控制要依赖选择器
  + CSS的样式代码中注释的方式为  /*   */
  + 内嵌式虽然对样式代码做了抽取,但是CSS代码仍然在html文件中
  + 内嵌样式仅仅能作用于当前文件,代码复用度还是不够,不利于网站风格统一

3  连接式/外部样式表

+ 可以在项目单独创建css样式文件,专门用于存放CSS样式代码

+ 在head标签中,通过link标签引入外部CSS样式即可
+ 在 `<link>` 标签里，`rel="stylesheet"` 表明这是一个样式表文件，`href=""css/buttons.css"` 则指明了外部样式表文件的路径。 `type` 属性用于指定所链接资源的 MIME（多用途互联网邮件扩展）类型

``` html
<head>
    <meta charset="UTF-8">
    <link href="css/buttons.css" rel="stylesheet" type="text/css"/>
</head>
<body>
    <input type="button" value="按钮1"/> 
    <input type="button" value="按钮2"/> 
    <input type="button" value="按钮3"/> 
    <input type="button" value="按钮4"/> 
</body>
```

+ 说明
  + CSS样式代码从html文件中剥离,利于代码的维护
  + CSS样式文件可以被多个不同的html引入,利于网站风格统一

## 2 CSS选择器

1 元素选择器

   style="样式名:样式值; 样式名:样式值;"

+ 代码

``` html
<head>
    <meta charset="UTF-8">
   <style>
    input {
        display: block;
        width: 80px; 
        height: 40px; 
        background-color: rgb(140, 235, 100); 
        color: white;
        border: 3px solid green;
        font-size: 22px;
        font-family: '隶书';
        line-height: 30px;
        border-radius: 5px;
    }
   </style>
</head>
<body>
    <input type="button" value="按钮1"/> 
    <input type="button" value="按钮2"/> 
    <input type="button" value="按钮3"/> 
    <input type="button" value="按钮4"/> 
    <button>按钮5</button>
</body>
```

+ 效果

![元素选择器.png](http://yanxuan.nosdn.127.net/8b1db972ec3feb7516f09451ef49f476.png)

+ 说明
  + 根据标签名确定样式的作用范围 本页面所有input的标签都受影响
  + 语法为  元素名 {}
  + 样式只能作用到同名标签上,其他标签不可用
  + 相同的标签未必需要相同的样式,会造成样式的作用范围太大

2 id选择器

+ 代码

``` html
# id值  
谁想引用就引用这个id
<head>
    <meta charset="UTF-8">
   <style>
    #btn1 {
        display: block;
        width: 80px; 
        height: 40px; 
        background-color: rgb(140, 235, 100); 
        color: white;
        border: 3px solid green;
        font-size: 22px;
        font-family: '隶书';
        line-height: 30px;
        border-radius: 5px;
    }
   </style>
</head>
<body>
    <input id="btn1" type="button" value="按钮1"/> 
    <input id="btn2" type="button" value="按钮2"/> 
    <input id="btn3" type="button" value="按钮3"/> 
    <input id="btn4" type="button" value="按钮4"/> 
    <button id="btn5">按钮5</button>
</body>
```

+ 效果

![id原则强.png](http://yanxuan.nosdn.127.net/f46fbd1185cc659ae37e3ba7f04a43b1.png)

+ 说明
  + 根据元素id属性的值确定样式的作用范围
  + 语法为  #id值 {}
  + id属性的值在页面上具有唯一性,所有id选择器也只能影响一个元素的样式
  + 因为id属性值不够灵活,所以使用该选择器的情况较少

3 class选择器

+ 代码

``` html
<head>
    <meta charset="UTF-8">
   <style>
    .shapeClass {
        display: block;
        width: 80px; 
        height: 40px; 
        border-radius: 5px;
    }
    .colorClass{
        background-color: rgb(140, 235, 100); 
        color: white;
        border: 3px solid green;
    }
    .fontClass {
        font-size: 22px;
        font-family: '隶书';
        line-height: 30px;
    }

   </style>
</head>
<body>
    <input  class ="shapeClass colorClass fontClass"type="button" value="按钮1"/> 
    <input  class ="shapeClass colorClass" type="button" value="按钮2"/> 
    <input  class ="colorClass fontClass" type="button" value="按钮3"/> 
    <input  class ="fontClass" type="button" value="按钮4"/> 
    <button class="shapeClass colorClass fontClass" >按钮5</button>
</body>
```



+ 效果

![class选择器.png](http://yanxuan.nosdn.127.net/861449e6e677c4aa57463a6f89e808c3.png)

+ 说明
  + 根据元素class属性的值确定样式的作用范围
  + 语法为  .class值 {}
  + class属性值可以有一个,也可以有多个,多个不同的标签也可以是使用相同的class值
  + 多个选择器的样式可以在同一个元素上进行叠加
  + 因为class选择器非常灵活,所以在CSS中,使用该选择器的情况较多



## 3 CSS浮动

> CSS 的 Float（浮动）使元素脱离文档流，按照指定的方向（左或右发生移动），直到它的外边缘碰到包含框或另一个浮动框的边框为止。

+ 浮动设计的初衷为了解决文字环绕图片问题，浮动后一定不会将文字挡住，这是设计初衷。  

1 浮动的样式名:float

![浮动的样式名.png](http://yanxuan.nosdn.127.net/176e4b50d589f218b93ef436c3b31ed9.png)

用于设置元素的浮动方式。当为一个元素应用 `float: left;` 时，该元素会向左浮动，脱离正常的文档流，并且其他元素会围绕它进行排列

> 通过代码感受浮动的效果

+ 代码

``` html
<head>
    <meta charset="UTF-8">
   <style>
    .outerDiv {
        width: 500px;
        height: 300px;
        border: 1px solid green;
        background-color: rgb(230, 224, 224);
    }
    .innerDiv{
        width: 100px;
        height: 100px;
        border: 1px solid blue;
        float: left;
    }
    .d1{
        background-color: greenyellow;
       /*  float: right; */
    }
    .d2{
        background-color: rgb(79, 230, 124);
    }
    .d3{
        background-color: rgb(26, 165, 208);
    }
   </style>
</head>
<body>
   <div class="outerDiv">
        <div class="innerDiv d1">框1</div>
        <div class="innerDiv d2">框2</div>
        <div class="innerDiv d3">框3</div>
   </div> 
</body>
```

+ 效果

![浮动样式效果图.png](http://yanxuan.nosdn.127.net/8965bdc15360bf303e5b0727ef1ee61d.png)



## 4 CSS定位

> position 属性指定了元素的定位类型。

+ 这个属性定义建立元素布局所用的定位机制。任何元素都可以定位，不过绝对或固定元素会生成一个块级框，而不论该元素本身是什么类型。相对定位元素会相对于它在正常流中的默认位置偏移。

+ 元素可以使用的顶部，底部，左侧和右侧属性定位。然而，这些属性无法工作，除非是先设定position属性。他们也有不同的工作方式，这取决于定位方法。

![CSS定位.png](http://yanxuan.nosdn.127.net/2f5e39ab634e6cd4f5fc762dc6c434db.png)

 top顶端 left左端 right右端 bottom低端

**1 静态定位**

+ 说明
  + 不设置的时候的默认值就是static，静态定位，没有定位，元素出现在该出现的位置，块级元素垂直排列，行内元素水平排列
+ 代码

``` html
<head>
    <meta charset="UTF-8">
    <style>
        .innerDiv{
                width: 100px;
                height: 100px;
        }
        .d1{
            background-color: rgb(166, 247, 46);
            position: static;
        }
        .d2{
            background-color: rgb(79, 230, 124);
        }
        .d3{
            background-color: rgb(26, 165, 208);
        }
    </style>
</head>
<body>
        <div class="innerDiv d1">框1</div>
        <div class="innerDiv d2">框2</div>
        <div class="innerDiv d3">框3</div>
</body>
```

+ 效果



![静态定位.png](http://yanxuan.nosdn.127.net/a6f8263cb821c36931b773f13e9fa99d.png)

**2 绝对定位** 

+ 说明
  + absolute ,通过 top left right bottom 指定元素在页面上的固定位置
  + 定位后元素会让出原来位置,其他元素可以占用

+ 代码

``` html
<head>
    <meta charset="UTF-8">
    <style>
        .innerDiv{
                width: 100px;
                height: 100px;
        }
        .d1{
            background-color: rgb(166, 247, 46);
            position: absolute;
            left: 300px;
            top: 100px;
        }
        .d2{
            background-color: rgb(79, 230, 124);
        }
        .d3{
            background-color: rgb(26, 165, 208);
        }
    </style>
</head>
<body>
        <div class="innerDiv d1">框1</div>
        <div class="innerDiv d2">框2</div>
        <div class="innerDiv d3">框3</div>
</body>
```

+ 效果

![绝对定位.png](http://yanxuan.nosdn.127.net/04f3af292b446c6394e4d5dcbd2f3b2e.png)

**3 相对定位**

+ 说明
  + relative 相对于自己原来的位置进行地位
  + 定位后保留原来的站位,其他元素不会移动到该位置

+ 代码

``` html
<head>
    <meta charset="UTF-8">
    <style>
        .innerDiv{
                width: 100px;
                height: 100px;
        }
        .d1{
            background-color: rgb(166, 247, 46);
            position: relative;
            left: 30px;
            top: 30px;
        }
        .d2{
            background-color: rgb(79, 230, 124);
        }
        .d3{
            background-color: rgb(26, 165, 208);
        }
    </style>
</head>
<body>
        <div class="innerDiv d1">框1</div>
        <div class="innerDiv d2">框2</div>
        <div class="innerDiv d3">框3</div>
</body>
```

+ 效果

![相对定位.png](http://yanxuan.nosdn.127.net/4229b17484a6eab0ade126fd75deb01f.png)

**4 固定定位**

+ 说明
  + fixed 失踪在浏览器窗口固定位置,不会随着页面的上下移动而移动
  + 元素定位后会让出原来的位置,其他元素可以占用
+ 代码

``` html
<head>
    <meta charset="UTF-8">
    <style>
        .innerDiv{
                width: 100px;
                height: 100px;
        }
        .d1{
            background-color: rgb(166, 247, 46);
            position: fixed;
            right: 30px;
            top: 30px;
        }
        .d2{
            background-color: rgb(79, 230, 124);
        }
        .d3{
            background-color: rgb(26, 165, 208);
        }
    </style>
</head>
<body>
        <div class="innerDiv d1">框1</div>
        <div class="innerDiv d2">框2</div>
        <div class="innerDiv d3">框3</div>
        br*100+tab
</body>
```

+ 效果

![固定定位.gif](http://yanxuan.nosdn.127.net/2a56c1bc09e59c9ccef3ecdb1f79c82b.gif)

## 5 CSS盒子模型

`display: flex;` 是一种使用弹性布局模型（Flexible Box Layout Module）的布局方式，它为盒状模型提供了更加高效的方式来对元素进行排列、对齐和分配空间。下面从基本概念、特点、常用属性等方面为你详细介绍：

+ CSS盒模型本质上是一个盒子，封装周围的HTML元素，它包括：边距（margin），边框（border），填充（padding），和实际内容（content）

![盒子模型.png](http://yanxuan.nosdn.127.net/a726eec0ef84c9e1864ef1921b4a43f1.png)

+ 说明：
  + Margin(外边距) - 清除边框外的区域，外边距是透明的。
  + Border(边框) - 围绕在内边距和内容外的边框。
  + Padding(内边距) - 清除内容
  
  + Content(内容) - 盒子的内容，显示文本和图像。
  
  + /* margin-top: 10px;
  
    ​      margin-right: 20px;
  
    ​      margin-bottom: 30px;
  
    ​      margin-left: 40px;    设置元素边框的上右下左方向边框的外边距
  
    可以简写 margin: 10px 20px 30px 40px;
  
    margin: 0px auto;    上下为0 左右居中
    
    现在点击月亮会，图标会变为太阳，然后背景颜色也会变的明亮，导航栏的颜色也会变的明亮
    
    
  
+ 代码

``` html
    <head>
        <meta charset="UTF-8">
       <style>
        .outerDiv {
            width: 800px;
            height: 300px;
            border: 1px solid green;
            background-color: rgb(230, 224, 224);
            margin: 0px auto;
        }
        .innerDiv{
            width: 100px;
            height: 100px;
            border: 10px solid blue;
            float: left;
            /* margin-top: 10px;
            margin-right: 20px;
            margin-bottom: 30px;
            margin-left: 40px; */
            margin: 10px 20px 30px 40px;
           
        }
        .d1{
            background-color: greenyellow;
            /* padding-top: 10px;
            padding-right: 20px;
            padding-bottom: 30px;
            padding-left: 40px; */
            padding: 10px 20px 30px 40px;
        }
        .d2{
            background-color: rgb(79, 230, 124);
        }
        .d3{
            background-color: rgb(26, 165, 208);
        }
       </style>
    </head>
    <body>
       <div class="outerDiv">
            <div class="innerDiv d1">框1</div>
            <div class="innerDiv d2">框2</div>
            <div class="innerDiv d3">框3</div>
       </div> 
    </body>
```

+ 效果

![效果图.png](http://yanxuan.nosdn.127.net/08c3f7f11057fa3e813085be2c97130e.png)

+ 在浏览器上,通过F12工具查看盒子模型状态

![盒子模型状态.png](http://yanxuan.nosdn.127.net/91a966ac88ee9d6a13c29697f2a1231e.png)

## 6 display 

display  它用于定义元素在页面中如何显示，控制元素的布局方式和行为。

 display: flex;

`ustify-content: center;` 是 CSS 中用于弹性布局（Flexbox）和网格布局（Grid）的属性，主要用于控制子元素在主轴方向上的对齐方式

justify-content: space-between;

`space-between` 时，弹性项目会沿着主轴均匀分布，第一个项目会贴靠容器的起始边缘，最后一个项目会贴靠容器的结束边缘，剩余的空间会平均分配到各个项目之间。主轴方向默认是水平方向（`flex-direction: row`），但可以通过 `flex-direction` 属性改变。

### 块级元素相关值

- `display: block;`
  - **特点**：元素会以块级形式显示，会独占一行，并且可以设置宽度和高度。如果没有设置宽度，默认会占满父元素的宽度。
  - **示例元素**：`<div>`、`<p>`、`<h1>` - `<h6>` 等元素默认的 `display` 属性值就是 `block`。
  - **应用场景**：常用于创建页面的主要布局结构，如头部、导航栏、内容区域等。
- `display: inline-block;`
  - **特点**：元素会像行内元素一样在一行内显示，但又可以设置宽度和高度，并且可以设置内外边距。
  - **示例应用**：常用于创建水平排列的按钮组、图片列表等。
- `display: list-item;`
  - **特点**：元素会以列表项的形式显示，会在元素前面添加一个列表标记（如圆点、数字等），并且可以设置列表样式。
  - **示例元素**：`<li>` 元素默认的 `display` 属性值就是 `list-item`。
  - **应用场景**：用于创建有序列表或无序列表。

### 行内元素相关值

- `display: inline;`
  - **特点**：元素会以行内形式显示，不会独占一行，宽度和高度由内容决定，并且不能设置宽度和高度（除了 `<img>`、`<input>` 等替换元素）。
  - **示例元素**：`<span>`、`<a>`、`<em>` 等元素默认的 `display` 属性值就是 `inline`。
  - **应用场景**：常用于包裹文本中的一部分内容，进行样式设置，如改变文本颜色、添加链接等。

### 表格相关值

- `display: table;`
  - **特点**：元素会以表格的形式显示，类似于 `<table>` 元素。
  - **应用场景**：用于创建自定义的表格布局，尤其是在需要兼容旧浏览器时。
- `display: table-row;`
  - **特点**：元素会以表格行的形式显示，类似于 `<tr>` 元素。
  - **应用场景**：用于在自定义表格布局中定义行。
- `display: table-cell;`
  - **特点**：元素会以表格单元格的形式显示，类似于 `<td>` 元素。
  - **应用场景**：用于在自定义表格布局中定义单元格。

### 弹性布局相关值

- `display: flex;`
  - **特点**：元素会成为一个弹性容器，其子元素会成为弹性项目。弹性布局提供了强大的对齐和排列功能，可以轻松实现水平和垂直居中、等分布局等。
  - **应用场景**：常用于创建导航栏、卡片布局、表单布局等。
- `display: inline-flex;`
  - **特点**：元素会以行内弹性容器的形式显示，类似于 `display: inline` 和 `display: flex` 的结合。
  - **应用场景**：当需要在一行内显示多个弹性容器时使用。

### 网格布局相关值

- `display: grid;`
  - **特点**：元素会成为一个网格容器，其子元素会成为网格项目。网格布局提供了二维的布局能力，可以更精确地控制元素的位置和大小。
  - **应用场景**：常用于创建复杂的页面布局，如图库、仪表盘等。
- `display: inline-grid;`
  - **特点**：元素会以行内网格容器的形式显示，类似于 `display: inline` 和 `display: grid` 的结合。
  - **应用场景**：当需要在一行内显示多个网格容器时使用。

### 其他值

- `display: none;`
  - **特点**：元素会从文档流中移除，不会在页面中显示，也不会占据空间。
  - **应用场景**：常用于实现元素的隐藏和显示效果，如菜单的展开和收起、模态框的显示和隐藏等。
- `display: contents;`
  - **特点**：元素本身不会显示，但其子元素会被视为直接属于父元素，就好像该元素不存在一样。
  - **应用场景**：当需要忽略某个元素的结构，只关注其子元素的布局时使用。

### text-align: center

- **`justify-content: center;`**：这是 Flexbox 布局和 Grid 布局中的属性，只能应用于使用 `display: flex` 或 `display: grid` 设置的容器元素。它用于定义子元素在主轴上的对齐方式，主轴方向取决于 `flex-direction` 或 `grid-template-columns` 等属性的设置。

- **`text-align: center;`**：该属性主要用于行内元素（如 `<span>`、`<a>`）、行内块元素（`display: inline-block`）和表格单元格（`<td>`），以及块级元素内部的文本内容。它可以应用于任何块级元素，用于控制元素内部文本、行内元素或行内块元素的水平对齐方式。

  display: inline-block;  在使用块级元素时

  - `justify-content: center;`
    - **Flexbox 布局**：当你使用 Flexbox 创建一个水平或垂直的布局时，可以使用 `justify-content: center;` 让子元素在主轴上居中排列。例如，你想让导航栏中的菜单项在水平方向上居中显示。
    - **Grid 布局**：在 Grid 布局中，同样可以使用 `justify-content: center;` 让网格项在水平方向上居中对齐。比如，你创建了一个图片网格，希望图片在容器中水平居中。
  - `text-align: center;`
    - **文本居中**：最常见的用途是让段落、标题等文本内容在其所在的块级元素中水平居中。例如，你想让页面标题在页面中水平居中显示。
    - **行内元素和行内块元素居中**：如果你有一组按钮或图标等行内元素或行内块元素，希望它们在其父元素中水平居中，可以使用 `text-align: center;`。

```
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Flexbox 水平居中</title>
    <style>
        .big-div {
            display: flex;
            justify-content: center;
            /* border: 1px solid #ccc; */
            padding: 10px;
        }

        .small-div {
            width: 50px;
            height: 50px;
            background-color: lightblue;
            margin: 0 5px;
        }
    </style>
</head>

<body>
    <div class="big-div">
        <div class="small-div"></div>
        <div class="small-div"></div>
        <div class="small-div"></div>
    </div>
</body>

</html>    
```



```
/* 设置页面整体的字体、外边距和内边距 */
body {
    /* 使用 Arial 字体，如果没有则使用无衬线字体 */
    font-family: Arial, sans-serif;
    /* 去除页面整体的外边距 */
    margin: 0;
    /* 去除页面整体的内边距 */
    padding: 0;
}
/* 设置页面头部的样式 */
header {
    /* 设置头部背景颜色为白色 */
    background-color: white;
    /* 设置头部文字颜色为黑色 */
    color: black;
    /* 设置头部内边距，上下 10px，左右 20px */
    padding: 10px 20px;
    /* 头部文字居中显示 */
    text-align: center;
}
/* 设置页面主体部分的样式 */
main {
    /* 设置主体部分的内边距为 20px */
    padding: 20px;
}

/* 设置商品列表的样式 */
/* 为.product-container类设置样式 */
.product-container {
    /* 设置元素的定位方式为相对定位，元素会相对于其正常位置进行定位 */
    position: relative;
    /* 设置元素的宽度为其父元素宽度的70% */
    width: 70%;
    /* 设置元素的高度为其父元素高度的70% */
    height: 70%;
    /* 设置元素的外边距，上外边距为1像素，左右外边距自动计算实现水平居中，下外边距为0像素 */
    margin: 1px auto 0;
    /* 设置元素在Z轴上的堆叠顺序为50，值越大越靠近屏幕前方 */
    z-index: 50;
    /* 设置元素的背景颜色为白色 */
    background-color: white;
    /* 由于定位方式为相对定位，此属性让元素相对于其正常位置向右偏移150像素 */
    left: 150px;
    /* 由于定位方式为相对定位，此属性让元素相对于其正常位置向上偏移580像素 */
    bottom: 580px;
}
/* 商品展示的样式 */
/* 定义一个名为 centered-div 的类，用于设置绝对定位的居中元素样式 */
.centered-div {
    /* 设置元素的定位方式为绝对定位，相对于最近的已定位祖先元素进行定位 */
    position: absolute;
    /* 将元素的左边缘定位到其父元素宽度的 50% 处 */
    left: 50%;
    /* 设置元素的宽度为 200 像素 */
    width: 200px;
    /* 设置元素的高度为 50 像素 */
    height: 50px;
    /* 设置元素的上外边距为 -50 像素，用于垂直居中微调 */
    margin-top: -50px;
    /* 设置元素的左外边距为 -100 像素，用于水平居中微调 */
    margin-left: -100px;
    /* 设置元素的背景颜色为白色 */
    background-color: white;
    /* 设置元素的内边距为 20 像素 */
    padding: 20px;
    /* 设置元素的边框圆角为 8 像素，使边框具有圆角效果 */
    border-radius: 8px;
    /* 将元素的右边缘定位到其父元素右边缘向左 50 像素的位置，此设置与 left 可能冲突 */
    right: 50px; 
    /* 将元素的上边缘定位到其父元素上边缘向下 150 像素的位置 */
    top: 150px;
}

/* 定义一个名为 no-products 的类，用于设置没有产品时的提示信息样式 */
.no-products {
    /* 设置文本在元素内水平居中对齐 */
    text-align: center;
    /* 设置文本颜色为 #666（深灰色） */
    color: #666;
    /* 设置元素的内边距为 20 像素 */
    padding: 20px;
    /* 设置文本字体大小为 1.2 倍默认字体大小 */
    font-size: 1.2em;
}

/* 定义一个名为 product-list 的类，用于设置产品列表的样式 */
.product-list {
    /* 将元素的显示方式设置为弹性布局，子元素将按照弹性布局规则排列 */
    display: flex;
    /* 允许子元素在一行排不下时换行显示 */
    flex-wrap: wrap;
    /* 设置子元素在主轴上居中对齐 */
    justify-content: center;
    /* 设置子元素之间的间距为 20 像素 */
    gap: 20px;
    /* 设置元素的最小高度为 400 像素 */
    min-height: 400px;
    /* 此属性在 display: flex 下无效，top 需要配合 position 使用 */
    top: 50px; 
}

/* 设置单个商品项的样式 */
.product {
    /* 设置商品项的边框，1px 实线，颜色为 #ccc */
    border: 1px solid #ccc;
    /* 设置商品项的内边距为 10px */
    padding: 10px;
    /* 商品项内文字居中显示 */
    text-align: center;
    /* 设置商品项的宽度为 200px */
    width: 200px;
}

/* 设置商品分类列表的样式 */
.category-list {
    /* 去除列表项的默认符号 */
    list-style-type: none;
    /* 去除列表项的默认内边距 */
    padding: 0;
}

/* 设置商品分类列表项的样式 */
.category-list li {
    /* 设置列表项的底部外边距为 10px */
    margin-bottom: 10px;
}

/* 搜索框容器样式 */
.search-container {
    /* 使用弹性布局 */
    display: flex;
    /* 搜索框容器内元素水平居中 */
    justify-content: center;
    /* 设置搜索框容器的上下外边距为 20px */
    margin: 20px 0;
}

/* 搜索输入框样式 */
#search-input {
    /* 设置搜索输入框宽度为屏幕宽度的五分之三 */
    width: 60%; 
    /* 设置搜索输入框内边距为 10px */
    padding: 10px;
    /* 设置搜索输入框的边框，1px 实线，颜色为 #ccc */
    border: 1px solid #ccc;
    /* 去除搜索输入框右侧边框 */
    border-right: none;
    /* 设置搜索输入框左上角边框圆角为 4px */
    border-top-left-radius: 4px;
    /* 设置搜索输入框左下角边框圆角为 4px */
    border-bottom-left-radius: 4px;
}

/* 搜索按钮样式 */
#search-button {
    /* 设置搜索按钮内边距，上下 10px，左右 20px */
    padding: 10px 20px;
    /* 设置搜索按钮背景颜色为 #007BFF */
    background-color: #007BFF;
    /* 设置搜索按钮文字颜色为白色 */
    color: white;
    /* 设置搜索按钮的边框，1px 实线，颜色为 #007BFF */
    border: 1px solid #007BFF;
    /* 设置搜索按钮右上角边框圆角为 4px */
    border-top-right-radius: 4px;
    /* 设置搜索按钮右下角边框圆角为 4px */
    border-bottom-right-radius: 4px;
    /* 鼠标悬停在按钮上时显示为手型 */
    cursor: pointer;
}

/* 搜索按钮悬停样式 */
#search-button:hover {
    /* 鼠标悬停时，搜索按钮背景颜色变为 #0056b3 */
    background-color: #0056b3;
}

/* 一级菜单和二级菜单的容器样式 */
.menu-container {
    display: flex;
    position: relative;
}

/* 一级菜单样式 */
.main-menu {
    list-style-type: none;
    padding: 0;
    margin: 0;
    width: 200px;
    position: sticky;
    top: 80px;
    height: calc(100vh - 100px);
}

/* 一级菜单列表项样式 */
.main-menu li {
    /* 设置列表项的定位方式为相对定位 */
    position: relative;
}

/* 二级菜单样式 */
.sub-menu {
    display: none;
    position: absolute;
    left: 0;
    top: 100%;
    z-index: 1000;
    background: white;
    min-width: 200px;
    box-shadow: 0 3px 5px rgba(0,0,0,0.2);
    left: 0;
    top: 100%;
    background: #fff;
    box-shadow: 2px 2px 5px rgba(0,0,0,0.2);
    min-width: 120px;
    z-index: 100;
}

/* 一级菜单列表项链接样式 */
.main-menu li a {
    /* 将链接显示为块级元素 */
    display: block;
    /* 设置链接内边距为 10px */
    padding: 10px;
    /* 去除链接的下划线 */
    text-decoration: none;
    /* 设置链接文字颜色为 #333 */
    color: #333;
    /* 设置链接背景颜色为 #f4f4f4 */
    background-color: #f4f4f4;
    /* 设置链接底部边框，1px 实线，颜色为 #ddd */
    border-bottom: 1px solid #ddd;
}

/* 一级菜单列表项链接悬停样式 */
.main-menu li a:hover {
    /* 鼠标悬停时，链接背景颜色变为 #ddd */
    background-color: #ddd;
}

/* 二级菜单样式 */
.sub-menu {
    /* 默认隐藏二级菜单 */
    display: none;
    /* 设置二级菜单的定位方式为绝对定位 */
    position: absolute;
    /* 二级菜单相对于一级菜单列表项向右偏移 200px */
    left: 200px;
    /* 二级菜单与一级菜单列表项顶部对齐 */
    top: 0;
    /* 去除列表项的默认符号 */
    list-style-type: none;
    /* 去除列表项的默认内边距 */
    padding: 0;
    /* 去除列表项的默认外边距 */
    margin: 0;
    /* 设置二级菜单的宽度为 200px */
    width: 200px;
    /* 为二级菜单添加阴影效果 */
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

/* 二级菜单列表项链接样式 */
.sub-menu li a {
    /* 设置二级菜单列表项链接背景颜色为白色 */
    background-color: #fff;}
```

## 7 类选择器组合

在 CSS 里，类选择器的组合形式指的是把多个类选择器组合起来，以此精准地选中同时具备这些类名的元素。这种方式能够让你更细致地控制元素样式，下面为你详细介绍：

基本概念

类选择器通过 `.` 符号开头，后面跟着类名来选中 HTML 中具有对应类名的元素。而类选择器的组合形式就是把多个类选择器直接连写在一起，中间没有空格，从而选中同时拥有这些类名的元素。

```
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>类选择器组合示例</title>
    <link rel="stylesheet" href="styles.css">
    <style>
        .box.highlight {
    background-color: yellow;
    color: red;
}
    </style>
</head>

<body>
    <div class="box">普通盒子</div>
    <div class="box highlight">高亮盒子</div>
    <div class="highlight">仅高亮元素</div>
</body>

</html>
```

优先级

```
内联样式 > ID 选择器 > 类选择器 > 元素选择器等。但是当使用 !important 时，这个声明的优先级会变得非常高，几乎可以覆盖其他正常优先级的样式声明。
```

## 8 其他

### 1. `::before` 和 `::after`  

CSS 里，`::before` 和 `::after` 属于伪元素，它们可以在元素的内容之前或之后插入虚拟的元素，然后对这些虚拟元素进行样式设置。下面从基本概念、使用方法和应用场景几个方面详细介绍：

**基本概念**

- `::before`：能够在所选元素的内容之前插入一个虚拟的子元素。
- `::after`：可以在所选元素的内容之后插入一个虚拟的子元素。

`content` 属性通常用于在元素的 `::before` 或 `::after` 伪元素中插入内容。content` 可以插入文本、图片、计数器等。在这个例子里，它插入了一个 SVG 图标。

```
<!DOCTYPE html>
<html lang="zh-CN">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SVG Content Example</title>
    <style>
        .icon::after {
            content: url("data:image/svg+xml,%3Csvg width='20' height='20' viewBox='0 0 24 24' fill='%23bbb6b6' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M7 10l5 5 5-5H7z'/%3E%3C/svg%3E");
            margin-left: 5px;
        }
    </style>
</head>

<body>
    <span class="icon">示例文本</span>
</body>

</html>
```

![箭头.jpg](http://yanxuan.nosdn.127.net/6ee818e26287640c5215a1cac697707a.jpg)

`!important` 是 CSS 的一个声明规则，它会使该样式的优先级高于其他普通样式声明。也就是说，无论在其他地方如何设置这个元素的 `color` 属性，只要这行样式代码生效，

**`text-decoration: none !important;`**：

- `ext-decoration` 是一个 CSS 属性，用于设置文本的装饰效果，比如下划线、上划线、删除线等。

- `none` 是 `text-decoration` 的一个取值，表示不添加任何装饰效果。所以这行代码会去除元素文本的下划线、上划线等装饰。

  #### `overflow-y: auto;`

  - `overflow-y` 是一个 CSS 属性，用于控制元素在垂直方向上（`y` 轴）内容溢出时的处理方式。
- `auto` 是该属性的一个取值，表示当元素内的内容在垂直方向上超出元素的高度时，浏览器会自动显示垂直滚动条，以便用户可以滚动查看超出部分的内容；如果内容没有超出元素的高度，则不会显示滚动条。
  
  ，`div` 元素应用了 `.content` 类，当文本内容超出 `div` 的高度时，会出现垂直滚动条，同时文本有 20 像素的内边距和 1.6 倍字体大小的行高。

  ```
  
  ```
<!DOCTYPE html>
  <html lang="en">

  <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <style>
         .content {
              overflow-y: auto;
              padding: 20px;
              line-height: 1.6;
              height: 200px; /* 为了演示滚动条效果，设置一个固定高度 */
              border: 1px solid #ccc;
          }
      </style>
  </head>

  <body>
      <div class="content">
          <p>这是一段示例文本，用于演示.content 类的样式效果。当文本内容足够长时，会触发垂直滚动条。</p>
          <p>这是另一段示例文本，用于演示.content 类的样式效果。当文本内容足够长时，会触发垂直滚动条。</p>
          <p>这是又一段示例文本，用于演示.content 类的样式效果。当文本内容足够长时，会触发垂直滚动条。</p>
      </div>
  </body>

  </html>
  ```
  

# 案例开发

**日程管理第零期**




  ```

### 2 伪类选择器

- 择器 `.carousel img:first-child`

  ：

  - `.carousel` 是一个类选择器，它会选中 HTML 中所有 `class` 属性包含 `carousel` 的元素。
  - `img` 是元素选择器，代表要选中的是 `<img>` 元素。
  - `:first-child` 是一个伪类选择器，它会选中前面选择器所匹配元素集合中的第一个子元素。所以 `.carousel img:first-child` 整体的意思是选中类名为 `carousel` 的元素下的第一个 `<img>` 子元素。

- 属性 `opacity: 1;

  - `opacity` 属性用于设置元素的透明度，取值范围是从 0 到 1。
  - 当 `opacity` 的值为 0 时，元素完全透明，不可见；当值为 1 时，元素完全不透明，正常显示。因此，`opacity: 1;` 会让选中的第一张图片完全显示出来。