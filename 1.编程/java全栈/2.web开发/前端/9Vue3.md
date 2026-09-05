

## 一、Vue3简介和快速体验

### 1.1 Vue3介绍

![vue3图标.png](http://yanxuan.nosdn.127.net/7e4251d67e814cbc158457aea5e3aeee.png)

> Vue 是一款用于构建用户界面的 JavaScript 框架。它基于标准 HTML、CSS 和 JavaScript 构建，并提供了一套声明式的、组件化的编程模型，帮助你高效地开发用户界面。无论是简单还是复杂的界面，Vue 都可以胜任。官网为:<https://cn.vuejs.org/>

 **Vue的两个核心功能：**

-   **声明式渲染**：Vue 基于标准 HTML 拓展了一套模板语法，使得我们可以声明式地描述最终输出的 HTML 和 JavaScript 状态之间的关系。
-   **响应性**：Vue 会自动跟踪 JavaScript 状态并在其发生变化时响应式地更新 DOM 

### 1.2 Vue3快速体验(非工程化方式)

导入vue3.js文件

  <script src="https://unpkg.com/vue@3/dist/vue.global.js"></script>

```html
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Document</title>
    </head>
    <body>
        <!-- 这里也可以用浏览器打开连接,然后将获得的文本单独保存进入一个vue.js的文件,导入vue.js文件即可 -->
        <script src="https://unpkg.com/vue@3/dist/vue.global.js"></script>
        <div id="app">
            <!-- 给style属性绑定colorStyle数据 -->
            <!-- {{插值表达式 直接将数据放在该位置}} -->
           <h1 v-bind:style="colorStyle">{{headline}}</h1>
           <!-- v-text设置双标签中的文本 -->
           <p v-text="article"></p>
           <!-- 给type属性绑定inputType数据 -->
           <input v-bind:type ="inputType" value="helloVue3"> <br>
           <!-- 给按钮单击事件绑定函数 -->
           <button  @click="sayHello()">hello</button>
        </div>

        <script>
            //组合api
            const app = Vue.createApp({
                // 在setup内部自由声明数据和方法即可!最终返回!
                setup(){
                    //定义数据
                    //在VUE中实现DOM的思路是: 通过修改修数据而影响页面元素
                    // vue3中,数据默认不是响应式的,需要加ref或者reactive处理,后面会详细讲解
                    let inputType ='text'
                    let headline ='hello vue3'
                    let article ='vue is awesome'  
                    let colorStyle ={'color':'red'}        
                    // 定义函数
                    let sayHello =()=>{
                        alert("hello Vue")
                    }
                    //在setup函数中,return返回的数据和函数可以在html使用
                    return {
                       inputType,
                       headline,
                       article,
                       colorStyle,
                       sayHello
                    }
                }
            });
            //挂载到视图
            app.mount("#app");
        </script>
    </body>
</html>
```

## 二、Vue3通过Vite实现工程化

### 2.1 Vite的介绍

![vite图标.png](http://yanxuan.nosdn.127.net/eddd5e39165b237f465b7f055e73ed77.png)

![vite1.png](http://yanxuan.nosdn.127.net/df25f0157b88358f634298e4c8f4c863.png)

+ 当我们开始构建越来越大型的应用时，需要处理的 JavaScript 代码量也呈指数级增长。
+ 包含数千个模块的大型项目相当普遍。基于 JavaScript 开发的工具就会开始遇到性能瓶颈：通常需要很长时间（甚至是几分钟！），即使使用模块热替换（HMR），文件修改后的效果也需要几秒钟才能在浏览器中反映出来。

Vite 旨在利用生态系统中的新进展解决上述问题：浏览器开始原生支持 ES 模块，且越来越多 JavaScript 工具使用编译型语言编写。https://cn.vitejs.dev/guide/why.html前端工程化的作用包括但不限于以下几个方面：

1.  快速创建项目：使用脚手架可以快速搭建项目基本框架，避免从零开始搭建项目的重复劳动和繁琐操作，从而节省时间和精力。
2.  统一的工程化规范：前端脚手架可以预设项目目录结构、代码规范、git提交规范等统一的工程化规范，让不同开发者在同一个项目上编写出风格一致的代码，提高协作效率和质量。
3.  代码模板和组件库：前端脚手架可以包含一些常用的代码模板和组件库，使开发者在实现常见功能时不再重复造轮子，避免因为轮子质量不高带来的麻烦，能够更加专注于项目的业务逻辑。
4.  自动化构建和部署：前端脚手架可以自动进行代码打包、压缩、合并、编译等常见的构建工作，可以通过集成自动化部署脚本，自动将代码部署到测试、生产环境等。

### 2.2 Vite创建Vue3工程化项目

#### 2.2.1 Vite+Vue3项目的创建、启动、停止

> 1 使用命令行创建工程

+ 在磁盘的合适位置上,创建一个空目录用于存储多个前端项目
+ 用vscode打开该目录
+ 在vocode中打开命令行运行如下命令

```shell
npm create vite@latest
```

+ 第一次使用vite时会提示下载vite,输入y回车即可,下次使用vite就不会出现了

+ 注意： 选择vue+JavaScript选项即可

> 2 安装项目所需依赖

+ cd进入刚刚创建的项目目录
+ npm install命令安装基础依赖

``` shell
cd ./vue3-demo1
npm install
```

> 3 启动项目

+ 查看项目下的package.json

```json
{
  "name": "vue3-demo1",
  "private": true,
  "version": "0.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "bootstrap": "^5.2.3",
    "sass": "^1.62.1",
    "vue": "^3.2.47"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^4.1.0",
    "vite": "^4.3.2"
  }
}
```

``` shell
npm run dev
```

> 5 停止项目

+ 命令行上 ctrl+c

#### 2.2.2 Vite+Vue3项目的目录结构

> 1.下面是 Vite 项目结构和入口的详细说明：

![vite项目结构.png](http://yanxuan.nosdn.127.net/ec5679494b1e82064ea9eee682e04712.png)

-   public/ 目录：用于存放一些公共资源，如 HTML 文件、图像、字体等，这些资源会被直接复制到构建出的目标目录中。
-   src/ 目录：存放项目的源代码，包括 JavaScript、CSS、Vue 组件、图像和字体等资源。在开发过程中，这些文件会被 Vite 实时编译和处理，并在浏览器中进行实时预览和调试。以下是src内部划分建议：
    1.  `assets/` 目录：用于存放一些项目中用到的静态资源，如图片、字体、样式文件等。
    2.  `components/` 目录：用于存放组件相关的文件。组件是代码复用的一种方式，用于抽象出一个可复用的 UI 部件，方便在不同的场景中进行重复使用。
    3.  `layouts/` 目录：用于存放布局组件的文件。布局组件通常负责整个应用程序的整体布局，如头部、底部、导航菜单等。
    4.  `pages/` 目录：用于存放页面级别的组件文件，通常是路由对应的组件文件。在这个目录下，可以创建对应的文件夹，用于存储不同的页面组件。
    5.  `plugins/` 目录：用于存放 Vite 插件相关的文件，可以按需加载不同的插件来实现不同的功能，如自动化测试、代码压缩等。
    6.  `router/` 目录：用于存放 Vue.js 的路由配置文件，负责管理视图和 URL 之间的映射关系，方便实现页面之间的跳转和数据传递。
    7.  `store/` 目录：用于存放 Vuex 状态管理相关的文件，负责管理应用程序中的数据和状态，方便统一管理和共享数据，提高开发效率。
    8.  `utils/` 目录：用于存放一些通用的工具函数，如日期处理函数、字符串操作函数等。
-   vite.config.js 文件：Vite 的配置文件，可以通过该文件配置项目的参数、插件、打包优化等。该文件可以使用 CommonJS 或 ES6 模块的语法进行配置。
-   package.json 文件：标准的 Node.js 项目配置文件，包含了项目的基本信息和依赖关系。其中可以通过 scripts 字段定义几个命令，如 dev、build、serve 等，用于启动开发、构建和启动本地服务器等操作。
-   Vite 项目的入口为 src/main.js 文件，这是 Vue.js 应用程序的启动文件，也是整个前端应用程序的入口文件。在该文件中，通常会引入 Vue.js 及其相关插件和组件，同时会创建 Vue 实例，挂载到 HTML 页面上指定的 DOM 元素中。

>  2.vite的运行界面

+ 在安装了 Vite 的项目中，可以在 npm scripts 中使用 `vite` 可执行文件，或者直接使用 `npx vite` 运行它。下面是通过脚手架创建的 Vite 项目中默认的 npm scripts：(package.json)

```json
{
  "scripts": {
    "dev": "vite", // 启动开发服务器，别名：`vite dev`，`vite serve`
    "build": "vite build", // 为生产环境构建产物
    "preview": "vite preview" // 本地预览生产构建产物
  }
}
```

+ 运行设置端口号：(vite.config.js)

```javascript
//修改vite项目配置文件 vite.config.js
export default defineConfig({
  plugins: [vue()],
  server:{
    port:3000
  }
})
```

#### 2.2.3 Vite+Vue3项目组件(SFC入门)

> 什么是VUE的组件?

+ 一个页面作为整体,是由多个部分组成的,每个部分在这里就可以理解为一个组件
+ 每个.vue文件就可以理解为一个组件,多个.vue文件可以构成一个整体页面
+ 组件化给我们带来的另一个好处就是组件的复用和维护非常的方便

![vue组件.png](http://yanxuan.nosdn.127.net/6e7e3683aee7dda1941631fad0cf4b02.png)

> 什么是.vue文件?

+ 传统的页面有.html文件.css文件和.js文件三个文件组成(多文件组件) 
+ vue将这文件合并成一个.vue文件(Single-File Component，简称 SFC,单文件组件)

+ .vue文件对js/css/html统一封装,这是VUE中的概念 该文件由三个部分组成    `<script>    <template>    <style>`
  + template标签     代表组件的html部分代码	代替传统的.html文件
  + script标签           代表组件的js代码 代替传统的.js文件
  + style标签            代表组件的css样式代码 代替传统的.css文件	

> 工程化vue项目如何组织这些组件?

+ index.html是项目的入口,其中 `<div id ='app'></div>`是用于挂载所有组建的元素
+ index.html中的script标签引入了一个main.js文件,具体的挂载过程在main.js中执行
+ main.js是vue工程中非常重要的文件,他决定这项目使用哪些依赖,导入的第一个组件
+ App.vue是vue中的核心组件,所有的其他组件都要通过该组件进行导入,该组件通过路由可以控制页面的切换

![vue如何引用组件.png](http://yanxuan.nosdn.127.net/4495bfbb84f1206546ba463b3dd27eaf.png)

#### 2.2.4 Vite+Vue3响应式入门和setup函数

> 1 使用vite创建一个 vue+JavaScript项目

```shell
npm create vite
npm install 
npm run dev
```

+ App.vue

```html
<script>
    //存储vue页面逻辑js代码
</script>

<template>
    <!-- 页面的样式的是html代码-->
</template>

<style scoped>
    /** 存储的是css代码! <style scoped> 是 Vue.js 单文件组件中用于设置组件样式的一种方式。
    它的含义是将样式局限在当前组件中，不对全局样式造成影响。 */
</style>
```

> 2 vue3响应式数据入门

```html
<script type="module">
    //存储vue页面逻辑js代码
    import {ref} from 'vue'
    export default{
        setup(){
            //非响应式数据: 修改后VUE不会更新DOM
            //响应式数据:   修改后VUE会更新DOM
            //VUE2中数据默认是响应式的
            //VUE3中数据要经过ref或者reactive处理后才是响应式的
            //ref是VUE3框架提供的一个函数,需要导入
            //let counter = 1
            //ref处理的响应式数据在js编码修改的时候需要通过.value操作
            //ref响应式数据在绑定到html上时不需要.value

            let counter = ref(1)
            function increase(){
                // 通过.value修改响应式数据
                counter.value++
            }
            function decrease(){
                counter.value--
            }
            return {
                counter,
                increase,
                decrease
            }
        }
    }
</script>
<template>
    <div>
      <button @click="decrease()">-</button>
      {{ counter }}
      <button @click="increase()">+</button>
    </div>
    
</template>

<style scoped>
    button{
        border: 1px solid red;
    }
</style>
```

> 3 vue3 setup函数和语法糖

+ 位置：src/App.vue

```vue
<script type="module" setup>
   
/* <script type="module" setup> 通过setup关键字
可以省略 export default {setup(){   return{}}}这些冗余的语法结构 */
    import {ref} from 'vue'
    // 定义响应式数据
    let counter = ref(1)
    // 定义函数
    function increase(){
        counter.value++
    }
    function decrease(){
        counter.value--
    }
    
</script>
<template>
    <div>
      <button @click="decrease()">-</button>
      {{ counter }}
      <button @click="increase()">+</button>
    </div>
    
</template>

<style scoped>
    button{
        border: 1px solid red;
    }
</style>

```

#### 2.2.5 Vite+Vue3关于样式的导入方式

1. 全局引入main.js

   ```javascript
   import './style/reset.css' //书写引入的资源的相对路径即可！
   ```

2. vue文件script代码引入

   ```javascript
   import './style/reset.css'
   ```

3. Vue文件style代码引入

   ```javascript
   @import './style/reset.css'
   ```

## 三、Vue3视图渲染技术

### 3.1 模版语法

Vue 使用一种基于 HTML 的模板语法，使我们能够声明式地将其组件实例的数据绑定到呈现的 DOM 上。所有的 Vue 模板都是语法层面合法的 HTML，可以被符合规范的浏览器和 HTML 解析器解析。

#### 3.1.1 插值表达式和文本渲染

> 插值表达式:最基本的数据绑定形式是文本插值，它使用的是“Mustache”语法 ,即双大括号`{{}}`

+ 插值表达式是将数据渲染到元素的指定位置的手段之一
+ 插值表达式不绝对依赖标签,其位置相对自由
+ 插值表达式中支持javascript的运算表达式
+ 插值表达式中也支持函数的调用

``` vue
<script setup type="module">
    
    /**
 * 插值表达式 语法 {{数据名字/函数/对象调用API}}
 * 不依赖标签 没有标签可以单独使用
 * */
  let msg ="hello vue3"
  let getMsg= ()=>{
    return 'hello vue3 message'
  }
  let age = 19
  let bee = '蜜 蜂'
  // 购物车
  const carts = [{name:'可乐',price:3,number:10},{name:'薯片',price:6,number:8}];
  //计算购物车总金额
  function compute(){
      let count = 0;
      for(let index in carts){
          count += carts[index].price*carts[index].number;
      }
      return count;
  }
</script>

<template>
  <div>
       <!-- 绑定数据 -->
     <h1>{{msg}}</h1>
     msg的值为{{ msg }} <br>
     <!-- 值表达式中是可以调用函数的，将函数的返回值绑定到元素的文本内容中 -->
      msg的值为{{ getMsg() }} <br>
      <!-- 插值表达式支持一些常见的运算符 -->
       年龄:{{ age }},是否成年:{{ age >18 ? "是":"否" }} <br>
      <!-- 插值表达式支持对象调用API -->
       <br>
       {{ bee.split(" ").reverse().join("-") }} <br>
      <!-- 购物车商品总金额 调用函数 -->
       <!-- {{ carts[0].price * carts[0].number + carts[1].price * carts[1].number }} -->
         {{ compute() }}
        <br>   
      <br>
  </div>
</template>

<style scoped>

</style>
```

> 为了渲染双标中的文本,我们也可以选择使用`v-text`和`v-html`命令

+ v-*** 这种写法的方式使用的是vue的命令
+ v-***的命令必须依赖元素,并且要写在元素的开始标签中
+ v-***指令支持ES6中的字符串模板
+ v- **支持模板字符串**
+ 插值表达式中支持javascript的运算表达式
+ 插值表达式中也支持函数的调用
+ v-text可以将数据渲染成双标签中间的文本,但是不识别html元素结构的文本
+ v-html可以将数据渲染成双标签中间的文本,识别html元素结构的文本

``` html
<script setup type="module">
  let msg ='hello vue3'
  let getMsg= ()=>{
    return msg
  }
  let age = 19
  let bee = '蜜 蜂'
  let redMsg ='<font color=\'red\'>msg</font>'
  let greenMsg =`<font color=\'green\'>${msg}</font>`
</script>

<template>
  <div>
    <span v-text='msg'></span> <br>
    <span v-text='redMsg'></span> <br>
    <span v-text='getMsg()'></span> <br>
    <span v-text='age>18?"成年":"未成年"'></span> <br>
    <span v-text='bee.split(" ").reverse().join("-")'></span> <br>
    <span v-html='msg'></span> <br>
    <span v-html='redMsg'></span> <br>
    <span v-html='greenMsg'></span> <br>
    <span v-html="`<font color='green'>${msg}</font>`"></span> <br>
  </div>
</template>

<style scoped>

</style>
```

#### 3.1.2 Attribute属性渲染

> 想要渲染一个元素的 attribute，应该使用 `v-bind`指令

+ 由于插值表达式不能直接放在标签的属性中,所有要渲染元素的属性就应该使用v-bind
+ v-bind可以用于渲染任何元素的属性,语法为 `v-bind:属性名='数据名'`, 可以简写为 `:属性名='数据名'`

``` html
<script setup type="module">
  const data = {
    name:'尚硅谷',
    url:"http://www.atguigu.com",
    logo:"http://www.atguigu.com/images/index_new/logo.png"
  }
</script>

<template>
  <div>
    <a 
      v-bind:href='data.url' 
      target="_self">
      <img 
        :src="data.logo" 
        :title="data.name">
      <br>
      <input type="button" 
             :value="`点击访问${data.name}`">
    </a>
  </div>
</template>

<style scoped>
</style>
```

#### 3.1.3 事件的绑定

> 我们可以使用 `v-on` 来监听 DOM 事件，并在事件触发时执行对应的 Vue的JavaScript代码。

+ 用法：`v-on:click="handler"` 或简写为 `@click="handler"`
+ vue中的事件名=原生事件名去掉`on` 前缀   如:`onClick --> click`
+ handler的值可以是方法事件处理器,也可以是内联事件处理器
+ 绑定事件时,可以通过一些绑定的修饰符,常见的事件修饰符如下
  + `.once：只触发一次事件。[重点]`
  + `.prevent：阻止默认事件。[重点]`
  + .stop：阻止事件冒泡。
  + .capture：使用事件捕获模式而不是冒泡模式。
  + .self：只在事件发送者自身触发时才触发事件。

``` html
<script setup type="module">
  import {ref} from 'vue'
  // 响应式数据 当发生变化时,会自动更新 dom树
  let count=ref(0)
  let addCount= ()=>{
    count.value++
  }
  let incrCount= (event)=>{
    count.value++
    // 通过事件对象阻止组件的默认行为
    event.preventDefault();
    
  }
</script>

<template>
  <div>
    <h1>count的值是:{{ count }}</h1>
    <!-- 方法事件处理器 -->
    <button v-on:click="addCount()">addCount</button> <br>
    <!-- 内联事件处理器 -->
    <button @click="count++">incrCount</button> <br>
    <!-- 事件修饰符 once 只绑定事件一次 -->
    <button @click.once="count++">addOnce</button> <br>
    <!-- 事件修饰符 prevent 阻止组件的默认行为 -->
    <a href="http://www.atguigu.com" target="_blank" @click.prevent="count++">prevent</a> <br>
    <!-- 原生js方式阻止组件默认行为 (推荐) -->
    <a href="http://www.atguigu.com" target="_blank" @click="incrCount($event)">prevent</a> <br>
  </div>
</template>

<style scoped>

</style>
```

### 3.2 响应式基础

此处的响应式是指  : 数据模型发生变化时,自动更新DOM树内容,页面上显示的内容会进行同步变化,vue3的数据模型不是自动响应式的,需要我们做一些特殊的处理

#### 3.2.1 响应式需求案例

> 需求：实现 +  - 按钮,实现数字加一减一

```html
<script type="module" setup>
    let counter = 0;
    function show(){
        alert(counter);
    }
</script>

<template>
  <div>
    <button @click="counter--">-</button> 
    {{ counter }} 
    <button @click="counter++">+</button>
    <hr>
    <!-- 此案例,我们发现counter值,会改变,但是页面不改变! 默认Vue3的数据是非响应式的!-->
    <button @click="show()">显示counter值</button>
   </div>
</template> 

<style scoped>

</style>

```

#### 3.2.2 响应式实现关键字ref

> `ref` 可以将一个基本类型的数据（如字符串，数字等）转换为一个响应式对象。 `ref` 只能包裹单一元素

```html
<script type="module" setup>
    /* 从vue中引入ref方法 */
    import {ref} from 'vue'
    let counter = ref(0);
    function show(){
        alert(counter.value);
    }
    /* 函数中要操作ref处理过的数据,需要通过.value形式 */
    let decr = () =>{
      counter.value--;
    }
    let incr = () =>{
      counter.value++;
    }
</script>

<template>
  <div>
    <button @click="counter--">-</button> 
    <button @click="decr()">-</button> 
    {{ counter }} 
    <button @click="counter++">+</button>
    <button @click="incr()">+</button> 
    <hr>
    <button @click="show()">显示counter值</button>
   </div>
</template> 

<style scoped>

</style>
```

+ 在上面的例子中，我们使用 `ref` 包裹了一个数字，在代码中给这个数字加 1 后，视图也会跟着动态更新。需要注意的是，由于使用了 `ref`，因此需要在访问该对象时使用 `.value` 来获取其实际值。

#### 3.2.3 响应式实现关键字reactive

> 我们可以使用 [reactive()](https://cn.vuejs.org/api/reactivity-core.html#reactive "reactive()") 函数创建一个响应式对象或数组：

```html
<script type="module" setup>
    /* 从vue中引入reactive方法 */
    import {ref,reactive} from 'vue'
    let data = reactive({
      counter:0
    })
    function show(){
        alert(data.counter);
    }
    /* 函数中要操作reactive处理过的数据,需要通过 对象名.属性名的方式 */
    let decr = () =>{
      data.counter--;
    }
    let incr = () =>{
      data.counter++;
    }
</script>

<template>
  <div>
    <button @click="data.counter--">-</button> 
    <button @click="decr()">-</button> 
    {{ data.counter }} 
    <button @click="data.counter++">+</button>
    <button @click="incr()">+</button> 
    <hr>
    <button @click="show()">显示counter值</button>
   </div>
</template> 

<style scoped>

</style>
```

> 对比ref和reactive:

+ 使用 `ref` 适用于以下开发场景：
  + 包装基本类型数据：`ref` 主要用于包装基本类型数据（如字符串、数字等），即只有一个值的数据，如果你想监听这个值的变化，用 `ref` 最为方便。在组件中使用时也很常见。
  + 访问方式简单：`ref` 对象在访问时与普通的基本类型值没有太大区别，只需要通过 `.value` 访问其实际值即可。

+ 使用 `reactive` 适用于以下开发场景：
  + 包装复杂对象：`reactive` 可以将一个普通对象转化为响应式对象，这样在数据变化时会自动更新界面，特别适用于处理复杂对象或者数据结构。
  + 需要递归监听的属性：使用 `reactive` 可以递归追踪所有响应式对象内部的变化，从而保证界面的自动更新。

+ 综上所述，`ref` 适用与简单情形下的数据双向绑定，对于只有一个字符等基本类型数据或自定义组件等情况，建议可以使用 `ref`；而对于对象、函数等较为复杂的数据结构，以及需要递归监听的属性变化，建议使用 `reactive`。当然，在实际项目中根据需求灵活选择也是十分必要的。

#### 3.2.4 扩展响应式关键字toRefs 和 toRef

> toRef基于reactive响应式对象上的一个属性，创建一个对应的 ref响应式数据。这样创建的 ref 与其源属性保持同步：改变源属性的值将更新 ref 的值，反之亦然。toRefs将一个响应式对象多个属性转换为一个多个ref数据，这个普通对象的每个属性都是指向源对象相应属性的 ref。每个单独的 ref 都是使用 [toRef()](https://cn.vuejs.org/api/reactivity-utilities.html#toref "toRef()") 创建的。

案例：响应显示reactive对象属性

```html
<script type="module" setup>
    /* 从vue中引入reactive方法 */
    import {ref,reactive,toRef,toRefs} from 'vue'
    let data = reactive({
      counter:0,
      name:"test"
    })

    // 将一个reactive响应式对象中的某个属性转换成一个ref响应式对象
    let ct =toRef(data,'counter');
    // 将一个reactive响应式对象中的多个属性转换成多个ref响应式对象
    let {counter,name} = toRefs(data)

    function show(){
        alert(data.counter);
        // 获取ref的响应对象,需要通过.value属性
        alert(counter.value);
        alert(name.value)
    }
    /* 函数中要操作ref处理过的数据,需要通过.value形式 */
    let decr = () =>{
      data.counter--;
    }
    let incr = () =>{
      /* ref响应式数据,要通过.value属性访问 */
      counter.value++;
    }
</script>

<template>
  <div>
    <button @click="data.counter--">-</button> 
    <button @click="decr()">-</button> 
    {{ data.counter }} 
    &amp;
    {{ ct }} 
    <button @click="data.counter++">+</button>
    <button @click="incr()">+</button> 
    <hr>
    <button @click="show()">显示counter值</button>
   </div>
</template> 

<style scoped>

</style>


```

### 3.3 条件和列表渲染

#### 3.3.1 条件渲染

> `v-if` 条件渲染

+ `v-if='表达式' `只会在指令的表达式返回真值时才被渲染

+ 也可以使用 `v-else` 为 `v-if` 添加一个“else 区块”。

+ 一个 `v-else` 元素必须跟在一个 `v-if` 元素后面，否则它将不会被识别。

+  v-else 紧跟 v-if 后面  数据为false 

  v -if 为真不会渲染后面  v-if 为假会渲染后面

```html
<script type="module" setup>
    import {ref} from 'vue'
    let awesome = ref(true)
</script>

<template>
  <div>
    <h1 v-if="awesome">Vue is awesome!</h1>
    <h1 v-else>Oh no 😢</h1>
    <button @click="awesome = !awesome">Toggle</button>
  </div>
</template> 

<style scoped>
</style>
```

> `v-show`条件渲染扩展：

+ 另一个可以用来按条件显示一个元素的指令是 `v-show`。其用法基本一样：

+ 不同之处在于 `v-show` 会在 DOM 渲染中保留该元素；`v-show` 仅切换了该元素上名为 `display` 的 CSS 属性。

+ `v-show` 不支持在 `<template>` 元素上使用，也不能和 `v-else` 搭配使用。

``` html
<script type="module" setup>
    import {ref} from 'vue'
    let awesome = ref(true)
</script>

<template>
  <div>
    <h1 id="ha"  v-show="awesome">Vue is awesome!</h1>
    <h1 id="hb"  v-if="awesome">Vue is awesome!</h1>
    <h1 id="hc"  v-else>Oh no 😢</h1>
    <button @click="awesome = !awesome">Toggle</button>
  </div>
</template> 

<style scoped>
</style>

```

> **`v-if`**    **vs** **`v-show`**

+ `v-if` 是“真实的”按条件渲染，因为它确保了在切换时，条件区块内的事件监听器和子组件都会被销毁与重建。

+ `v-if` 也是**惰性**的：如果在初次渲染时条件值为 false，则不会做任何事。条件区块只有当条件首次变为 true 时才被渲染。

+ 相比之下，`v-show` 简单许多，元素无论初始条件如何，始终会被渲染，只有 CSS `display` 属性会被切换。

+ 总的来说，`v-if` 有更高的切换开销，而 `v-show` 有更高的初始渲染开销。因此，如果需要频繁切换，则使用 `v-show` 较好；如果在运行时绑定条件很少改变，则 `v-if` 会更合适。

#### 3.3.2 列表渲染

> 我们可以使用 `v-for` 指令基于一个数组来渲染一个列表。

+ `v-for` 指令的值需要使用 `item in items` 形式的特殊语法，其中 `items` 是源数据的数组，而 `item` 是迭代项的**别名**：

+ 在 `v-for` 块中可以完整地访问父作用域内的属性和变量。`v-for` 也支持使用可选的第二个参数表示当前项的位置索引。

```html
<script type="module" setup>
    import {ref,reactive} from 'vue'
    let parentMessage= ref('产品')
    let items =reactive([
      {
        id:'item1',
        message:"薯片"
      },
      {
        id:'item2',
        message:"可乐"
      }
    ])
</script>

<template>
  <div>
    <ul>
      <!-- :key不写也可以 -->
      <li v-for='item in items' :key='item.id'>
        {{ item.message }}
      </li>
    </ul>

    <ul>
      <!-- index表示索引,当然不是非得使用index这个单词 -->
      <li v-for="(item, index) in items" :key="index">
        {{ parentMessage }} - {{ index }} - {{ item.message }}
      </li>
    </ul>
   
  </div>
</template> 

<style scoped>
</style>
```

+ 案例：实现购物车显示和删除购物项

``` html
<script type="module" setup>

    //引入模块
    import { reactive} from 'vue'
    //准备购物车数据,设置成响应数据
    const carts = reactive([{name:'可乐',price:3,number:10},{name:'薯片',price:6,number:8}])

    //计算购物车总金额
    function compute(){
      let count = 0;
      for(let index in carts){
        count += carts[index].price*carts[index].number;
      }
      return count;
    }
    //删除购物项方法
    function removeCart(index){
      carts.splice(index,1);
    }
    
</script>

<template>
    <div>
        <table>
           <thead>
               <tr>
                  <th>序号</th>
                  <th>商品名</th>
                  <th>价格</th>
                  <th>数量</th>
                  <th>小计</th>
                  <th>操作</th>
               </tr>
           </thead>
           <tbody v-if="carts.length > 0">
               <!-- 有数据显示-->
               <tr v-for="cart,index in carts" :key="index">
                  <th>{{ index+1 }}</th>
                  <th>{{ cart.name }}</th>
                  <th>{{ cart.price + '元' }}</th>
                  <th>{{ cart.number }}</th>
                  <th>{{ cart.price*cart.number  + '元'}}</th>
                  <th> <button @click="removeCart(index)">删除</button> </th>
               </tr>
           </tbody>
           <tbody v-else>
               <!-- 没有数据显示-->
               <tr>
                  <td colspan="6">购物车没有数据!</td>
               </tr>
           </tbody>
        </table>
        购物车总金额: {{ compute() }} 元
         <button @click="cleartCart()">一键清空购物车</button>
    </div>
</template> 

<style scoped>
</style>
```

### 3.4 双向绑定

> 单项绑定和双向绑定

+ 单向绑定: 响应式数据的变化会更新dom树,但是dom树上用户的操作造成的数据改变不会同步更新到响应式数据
+ 双向绑定: 响应式数据的变化会更新dom树,但是dom树上用户的操作造成的数据改变会同步更新到响应式数据
  + 用户通过表单标签才能够输入数据,所以双向绑定都是应用到表单标签上的,其他标签不行
  + v-model专门用于双向绑定表单标签的value属性,语法为 `v-model:value=''`,可以简写为 `v-model=''`
  + v-model还可以用于各种不同类型的输入，`<textarea>`、`<select>` 元素。

```html
<script type="module" setup>

  //引入模块
  import { reactive,ref} from 'vue' 
  let hbs = ref([]); //装爱好的值
  let user = reactive({username:null,password:null,introduce:null,pro:null})   
  function login(){
    alert(hbs.value);
    alert(JSON.stringify(user));
  }
  function clearx(){
    //user = {};// 这中写法会将数据变成非响应的,应该是user.username=""
    user.username=''
    user.password=''
    user.introduce=''
    user.pro=''
    hbs.value.splice(0,hbs.value.length);;
  }
</script>

<template>
  <div>
      账号： <input type="text" placeholder="请输入账号！" v-model="user.username"> <br>
      密码： <input type="text" placeholder="请输入账号！" v-model="user.password"> <br>
      爱好： 
        吃 <input type="checkbox" name="hbs" v-model="hbs" value="吃"> 
        喝 <input type="checkbox" name="hbs" v-model="hbs" value="喝">
        玩 <input type="checkbox" name="hbs" v-model="hbs" value="玩">
        乐 <input type="checkbox" name="hbs" v-model="hbs" value="乐">
      <br>
      简介:<textarea v-model="user.introduce"></textarea>
      <br>
      籍贯:
          <select v-model="user.pro">
            <option value="1">黑</option>
            <option value="2">吉</option>
            <option value="3">辽</option>
            <option value="4">京</option>
            <option value="5">津</option>
            <option value="6">冀</option>
          </select> 
      <br>
      <button @click="login()">登录</button> 
      <button @click="clearx()">重置</button>
      <hr>
      显示爱好:{{ hbs }}
      <hr>
      显示用户信息:{{ user }}
  </div> 
</template> 

<style scoped>
</style>

```

### 3.5 属性计算

> 模板中的表达式虽然方便，但也只能用来做简单的操作。如果在模板中写太多逻辑，会让模板变得臃肿，难以维护。比如说，我们有这样一个包含嵌套数组的对象：

```vue
<script type="module" setup>
  //引入模块
  import { reactive,computed} from 'vue'
  const author = reactive({
    name: 'John Doe',
    books: [
      'Vue 2 - Advanced Guide',
      'Vue 3 - Basic Guide',
      'Vue 4 - The Mystery'
    ]
  })
 
</script>

<template>
  <div>
    <p>{{author.name}} Has published books?:</p>
    <span>{{ author.books.length > 0 ? 'Yes' : 'No' }}</span>
  </div>
</template> 

<style scoped>
</style>
```

+ 这里的模板看起来有些复杂。我们必须认真看好一会儿才能明白它的计算依赖于 `author.books`。更重要的是，如果在模板中需要不止一次这样的计算，我们可不想将这样的代码在模板里重复好多遍。

> 因此我们推荐使用**计算属性**来描述依赖响应式状态的复杂逻辑。这是重构后的示例：

```html
<script type="module" setup>
  //引入模块
  import { reactive,computed} from 'vue'
  const author = reactive({
    name: 'John Doe',
    books: [
      'Vue 2 - Advanced Guide',
      'Vue 3 - Basic Guide',
      'Vue 4 - The Mystery'
    ]
  })
  // 一个计算属性 ref
  const publishedBooksMessage = computed(() => {
    console.log("publishedBooksMessage")
    return author.books.length > 0 ? 'Yes' : 'No'
  })
  // 一个函数
  let hasBooks = ()=>{
    console.log("hasBooks")
    return author.books.length > 0?'Yes':'no'
  }

</script>

<template>
  <div>
    <p>{{author.name}} Has published books?:</p>
    <span>{{ author.books.length > 0 ? 'Yes' : 'No' }}</span>
    <span>{{ hasBooks() }}</span><!-- 调用方法,每个标签都会调用一次 -->
    <span>{{ hasBooks() }}</span>

    <p>{{author.name}} Has published books?:</p>
    <span>{{ publishedBooksMessage }}</span><!-- 属性计算,属性值不变时,多个个标签只会调用一次 -->
    <span>{{ publishedBooksMessage }}</span>
  </div>
</template> 

<style scoped>
</style>

```

+ 我们在这里定义了一个计算属性 `publishedBooksMessage`。`computed()` 方法期望接收一个 getter 函数，返回值为一个**计算属性 ref**。和其他一般的 ref 类似，你可以通过 `publishedBooksMessage.value` 访问计算结果。计算属性 ref 也会在模板中自动解包，因此在模板表达式中引用时无需添加 `.value`。

+ Vue 的计算属性会自动追踪响应式依赖。它会检测到 `publishedBooksMessage` 依赖于 `author.books`，所以当 `author.books` 改变时，任何依赖于 `publishedBooksMessage` 的绑定都会同时更新。

> 计算属性缓存 vs 方法

+ 若我们将同样的函数定义为一个方法而不是计算属性，两种方式在结果上确实是完全相同的，然而，不同之处在于**计算属性值会基于其响应式依赖被缓存**。一个计算属性仅会在其响应式依赖更新时才重新计算。这意味着只要 `author.books` 不改变，无论多少次访问 `publishedBooksMessage` 都会立即返回先前的计算结果!

### 3.6 数据监听器

> 计算属性允许我们声明性地计算衍生值。然而在有些情况下，我们需要在状态变化时执行一些“副作用”：例如更改 DOM，或是根据异步操作的结果去修改另一处的状态。我们可以使用 [watch](https://cn.vuejs.org/api/reactivity-core.html#watch "watch")[ 函数](https://cn.vuejs.org/api/reactivity-core.html#watch " 函数")在每次响应式状态发生变化时触发回调函数：

+ watch主要用于以下场景：
  + 当数据发生变化时需要执行相应的操作
  + 监听数据变化，当满足一定条件时触发相应操作
  + 在异步操作前或操作后需要执行相应的操作

> 监控响应式数据（watch）：

```html
<script type="module" setup>
  //引入模块
  import { ref,reactive,watch} from 'vue'
 
  let firstname=ref('')
  let lastname=reactive({name:''})
  let fullname=ref('')

  //监听一个ref响应式数据
  watch(firstname,(newValue,oldValue)=>{
    console.log(`${oldValue}变为${newValue}`)
    fullname.value=firstname.value+lastname.name
  })
  //监听reactive响应式数据的指定属性
  watch(()=>lastname.name,(newValue,oldValue)=>{
    console.log(`${oldValue}变为${newValue}`)
    fullname.value=firstname.value+lastname.name
  })
  //监听reactive响应式数据的所有属性(深度监视,一般不推荐)
  //deep:true 深度监视
  //immediate:true 深度监视在进入页面时立即执行一次
  watch(()=>lastname,(newValue,oldValue)=>{
    // 此时的newValue和oldValue一样,都是lastname
    console.log(newValue)
    console.log(oldValue)
    fullname.value=firstname.value+lastname.name
  },{deep:true,immediate:false})
</script>

<template>
  <div>
    全名:{{fullname}} <br>
    姓氏:<input type="text" v-model="firstname"> <br>
    名字:<input type="text" v-model="lastname.name" > <br>
  </div>
</template> 

<style scoped>
</style>
```

> 监控响应式数据(watchEffect)：

+ watchEffect默认监听所有的响应式数据

```html
<script type="module" setup>
  //引入模块
  import { ref,reactive,watch, watchEffect} from 'vue'
 
  let firstname=ref('')
  let lastname=reactive({name:''})
  let fullname=ref('')

  //监听所有响应式数据
  watchEffect(()=>{
    //直接在内部使用监听属性即可！不用外部声明
    //也不需要，即时回调设置！默认初始化就加载！
    console.log(firstname.value)
    console.log(lastname.name)
    fullname.value=`${firstname.value}${lastname.name}`
  })
</script>

<template>
  <div>
    全名:{{fullname}} <br>
    姓氏:<input type="text" v-model="firstname"> <br>
    名字:<input type="text" v-model="lastname.name" > <br>
  </div>
</template> 

<style scoped>
</style>
```

> `watch` vs. `watchEffect`

+ `watch` 和 `watchEffect` 都能响应式地执行有副作用的回调。它们之间的主要区别是追踪响应式依赖的方式：
  + `watch` 只追踪明确侦听的数据源。它不会追踪任何在回调中访问到的东西。另外，仅在数据源确实改变时才会触发回调。`watch` 会避免在发生副作用时追踪依赖，因此，我们能更加精确地控制回调函数的触发时机。
  + `watchEffect`，则会在副作用发生期间追踪依赖。它会在同步执行过程中，自动追踪所有能访问到的响应式属性。这更方便，而且代码往往更简洁，但有时其响应性依赖关系会不那么明确。

### 3.7. Vue生命周期

#### 3.7.1 生命周期简介

> 每个 Vue 组件实例在创建时都需要经历一系列的初始化步骤，比如设置好数据侦听，编译模板，挂载实例到 DOM，以及在数据改变时更新 DOM。在此过程中，它也会运行被称为`生命周期钩子的函数`，让开发者有机会在特定阶段运行自己的代码!

+ 周期图解：

<img src="F:/资料/java/2.尚硅谷2023JavaWeb图书配套资料/笔记/images/image_elceCM4Wbp.png" style="zoom: 50%;" />

+ 常见钩子函数
  + onMounted()              注册一个回调函数，在组件挂载完成后执行。 
  + onUpdated()               注册一个回调函数，在组件因为响应式状态变更而更新其 DOM 树之后调用。 
  + onUnmounted()         注册一个回调函数，在组件实例被卸载之后调用。 
  + onBeforeMount()       注册一个钩子，在组件被挂载之前被调用。 
  + onBeforeUpdate()      注册一个钩子，在组件即将因为响应式状态变更而更新其 DOM 树之前调用。 
  + onBeforeUnmount()  注册一个钩子，在组件实例被卸载之前调用。 

#### 3.7.2 生命周期案例

```html
<script setup>

    import {ref,onUpdated,onMounted,onBeforeUpdate} from 'vue'
    
    let message =ref('hello')
   
    // 挂载完毕生命周期
    onMounted(()=>{
      console.log('-----------onMounted---------')
      let span1 =document.getElementById("span1")
      console.log(span1.innerText)
    })
    // 更新前生命周期
    onBeforeUpdate(()=>{
      console.log('-----------onBeforeUpdate---------')
      console.log(message.value)
      let span1 =document.getElementById("span1")
      console.log(span1.innerText)
    })
    // 更新完成生命周期
    onUpdated(()=>{
      console.log('-----------onUpdated---------')
      let span1 =document.getElementById("span1")
      console.log(span1.innerText)
    })
</script>

<template>
  <div>
    <span id="span1" v-text="message"></span> <br>
    <input type="text" v-model="message">
  </div>
  
</template>

<style scoped>
</style>
```

### 3.8 Vue组件

#### 3.8.1 组件基础

> 组件允许我们将 UI 划分为独立的、可重用的部分，并且可以对每个部分进行单独的思考。组件就是实现应用中局部功能代码和资源的集合！在实际应用中，组件常常被组织成层层嵌套的树状结构：

+ 这和我们嵌套 HTML 元素的方式类似，Vue 实现了自己的组件模型，使我们可以在每个组件内封装自定义内容与逻辑。

> 传统方式编写应用：

![传统编写前端.png](http://yanxuan.nosdn.127.net/e4715f5b8c7ba36f3d6421f8be39be38.png)

> 组件方式编写应用：

![组件编写.png](http://yanxuan.nosdn.127.net/8cea6bae2d01b9c824311e48746bcb7f.png)

+ 组件化：对js/css/html统一封装,这是VUE中的概念

+ 模块化：对js的统一封装,这是ES6中的概念
+ 组件化中,对js部分代码的处理使用ES6中的模块化

#### 3.8.2 组件化入门案例

> 案例需求： 创建一个页面，包含头部和菜单以及内容显示区域，每个区域使用独立组建！

![组件入门案例.png](http://yanxuan.nosdn.127.net/9c6eea2e17ff27af5a5b043e99373ce7.png)

> 1 准备vue项目

```shell
npm create vite
cd vite项目
npm install
```

> 2 安装相关依赖

```shell
npm install sass
npm install bootstrap
```

>  3 创建子组件 在src/components文件下 vscode需要安装Vetur插件，这样vue文件有快捷提示

+ Header.vue

```html
<script setup type="module">
</script>

<template>
    <div>
        欢迎： xx <a href="#">退出登录</a>
    </div>
</template>

<style>
</style>
```

+ Navigator.vue

```html
<script setup type="module">
</script>

<template>
    <!-- 推荐写一个根标签-->
    <div>
       <ul>
          <li>学员管理</li>
          <li>图书管理</li>
          <li>请假管理</li>
          <li>考试管理</li>
          <li>讲师管理</li>
       </ul>
    </div>
</template>

<style>
</style>
```

+ Content.vue

```html
<script setup type="module">
</script>

<template>
    <div>
        展示的主要内容！
    </div>
</template>

<style>
</style>
```

+ App.vue  入口组件App引入组件

```html
<script setup>
    import Header  from './components/Header.vue'
    import Navigator  from './components/Navigator.vue'
    import Content  from './components/Content.vue'
</script>

<template>
  <div>
     <Header class="header"></Header>
     <Navigator class="navigator"></Navigator>
     <Content class="content"></Content>
  </div>
</template>

<style scoped>
    .header{
       height: 80px;
       border: 1px solid red;
    }

    .navigator{
      width: 15%;
      height: 800px;
      display: inline-block;
      border: 1px blue solid;
      float: left;
    }

    .content{
      width: 83%;
      height: 800px;
      display: inline-block;
      border: 1px goldenrod solid;
      float: right;
    }

</style>
```

> 4 启动测试

```shell
npm run dev
```

#### 3.8.3 组件之间传递数据

1有子传父 父传子    **兄弟传参** 但是  **兄弟传参**  的过程也是从子传父 父传子

![组件传参.png](http://yanxuan.nosdn.127.net/0b3f4c01ca98f5f535bc02cab9ea65d8.png)

+ Navigator.vue: 发送数据到App.vue

```html
<script setup type="module">
    import {defineEmits} from 'vue'
    const emits = defineEmits(['sendMenu']);
    //触发事件，向父容器发送数据
    function send(data){
        emits('sendMenu',data);
    }
</script>

<template>
    <!-- 推荐写一个根标签-->
    <div>
       <ul>
          <li @click="send('学员管理')">学员管理</li>
          <li @click="send('图书管理')">图书管理</li>
          <li @click="send('请假管理')">请假管理</li>
          <li @click="send('考试管理')">考试管理</li>
          <li @click="send('讲师管理')">讲师管理</li>
       </ul>
    </div>
</template>

<style>

</style>
```

+ App.vue: 发送数据到Content.vue

```html
<script setup>
  
  import Header  from './components/Header.vue'
  import Navigator  from './components/Navigator.vue'
  import Content  from './components/Content.vue'

  import {ref} from "vue"
  //定义接受navigator传递参数
  var navigator_menu = ref('ceshi');

  const receiver = (data) =>{
    navigator_menu.value = data;
  }
</script>

<template>
  <div>
      <hr>
      {{ navigator_menu }}
      <hr>
     <Header class="header"></Header>
     <Navigator @sendMenu="receiver" class="navigator"></Navigator>
     <!-- 向子组件传递数据-->
     <Content class="content" :message="navigator_menu"></Content>
    </div>
</template>

<style scoped>
    .header{
       height: 80px;
       border: 1px solid red;
    }

    .navigator{
      width: 15%;
      height: 800px;
      display: inline-block;
      border: 1px blue solid;
      float: left;
    }

    .content{
      width: 83%;
      height: 800px;
      display: inline-block;
      border: 1px goldenrod solid;
      float: right;
    }

</style>
```

+ Content.vue

```html
<script setup type="module">
    defineProps({
        message:String
    })
</script>

<template>
    
    <div>
        展示的主要内容！
        <hr>
        {{ message }}
    </div>
</template>

<style>
</style>
```

## 四、Vue3路由机制router

### 4.1 路由简介

> 1 什么是路由？

-   定义：路由就是根据不同的 URL 地址展示不同的内容或页面。
-   通俗理解：路由就像是一个地图，我们要去不同的地方，需要通过不同的路线进行导航。

> 2 路由的作用

-   单页应用程序（SPA）中，路由可以实现不同视图之间的无刷新切换，提升用户体验；
-   路由还可以实现页面的认证和权限控制，保护用户的隐私和安全；
-   路由还可以利用浏览器的前进与后退，帮助用户更好地回到之前访问过的页面。

### 4.2 路由入门案例

> 1 案例需求分析

![路由入门案例.png](http://yanxuan.nosdn.127.net/a9c792063b86f213cd4c7443ac585657.png)

​    

> 2 创建项目和导入路由依赖


```shell
npm create vite //创建项目cd 项目文件夹 //进入项目文件夹
npm install //安装项目需求依赖
npm install vue-router@4 --save //安装全局的vue-router 4版本
npm run dev //是否能运行vue项目
```

> 3 准备页面和组件    

+ components/Home.vue

``` html
<script setup>
</script>

<template>
    <div>
        <h1>Home页面</h1>
    </div>
</template>

<style scoped>
</style>

```

+ components/List.vue

``` html
<script setup>
</script>

<template>
    <div>
        <h1>List页面</h1>
    </div>
</template>

<style scoped>
</style>
```

+ components/Add.vue

``` html
<script setup>
</script>

<template>
    <div>
        <h1>Add页面</h1>
    </div>
</template>

<style scoped>
</style>
```

+ components/Update.vue

``` html
<script setup>
</script>

<template>
    <div>
        <h1>Update页面</h1>
    </div>
</template>

<style scoped>
</style>
```

+ App.vue

``` html
<script setup>
</script>

<template>
    <div>
      <h1>App页面</h1>
      <hr/>
        <!-- 路由的连接 -->
        <router-link to="/">home页</router-link> <br>
        <router-link to="/list">list页</router-link> <br>
        <router-link to="/add">add页</router-link> <br>
        <router-link to="/update">update页</router-link> <br>
      <hr/>
      <!-- 路由连接对应视图的展示位置 -->
      <hr>
      默认展示位置:<router-view></router-view>
      <hr>
        
        
      
    </div>
</template>

<style scoped>
</style>

```

```
用name来区分不同路由展示的位置不同 一般不会这么做
Home视图展示:<router-view name="homeView"></router-view>
      <hr>
      List视图展示:<router-view name="listView"></router-view>
      <hr>
      Add视图展示:<router-view name="addView"></router-view>
      <hr>
      Update视图展示:<router-view name="updateView"></router-view>
```



> 4 准备路由配置

+ src/routers/router.js

```javascript
// 导入路由创建的相关方法
import {createRouter,createWebHashHistory} from 'vue-router'

// 导入vue组件
import Home from '../components/Home.vue'
import List from '../components/List.vue'
import Add from '../components/Add.vue'
import Update from '../components/Update.vue'

// 创建路由对象,声明路由规则
const router = createRouter({
    //createWebHashHistory() 是 Vue.js 基于 hash 模式创建路由的工厂函数。在使用这种模式下，路由信息保存在 URL 的 hash 中，
    //使用 createWebHashHistory() 方法，可以创建一个路由历史记录对象，用于管理应用程序的路由。在 Vue.js 应用中，
    //通常使用该方法来创建路由的历史记录对象。
    //就是路由中缓存历史记录的对象，vue-router提供
    history: createWebHashHistory(),
    // routes 属性用于配置路由规则，用于定义多个不同的路径和组件之间的对应关系
    routes:[
        {
               path:"/home",
               component:Home
        },
        {   
            path:"/list",
               component:List
            },
        {  
             path:"/update",
               component:Update
            },
        {  
             path:"/add",
               component:Add
            },
            {
               path:"/",
               component:Home
            }，
             {
                path:"/about",
                redirect:"/List"
            }，
                            // 路由重定向
    ]

})

// 对外暴露路由对象
export default router;
```

> 5 main.js引入router配置

+ 修改文件：main.js (入口文件)

```javascript
import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
//导入router模块
import router from './routers/router.js'
let app = createApp(App)
//绑定路由对象
app.use(router)
//挂在试图
app.mount("#app")
```

> 6 启动测试

``` shell
npm run dev
```

### 4.3 路由重定向

> 重定向的作用：将一个路由重定向到另一个路由上

+ 修改案例：访问/list和/showAll都定向到List.vue
+ router.js

``` javascript
// 导入路由创建的相关方法
import {createRouter,createWebHashHistory} from 'vue-router'

// 导入vue组件
import Home from '../components/Home.vue'
import List from '../components/List.vue'
import Add from '../components/Add.vue'
import Update from '../components/Update.vue'

// 创建路由对象,声明路由规则
const router = createRouter({
    history: createWebHashHistory(),
    routes:[
        {
            path:'/',
            components:{
                default:Home,
                homeView:Home
            }      
        },
        {
            path:'/list',
            components:{
                listView : List
            } 
        },
        {
            path:'/showAll',
            // 重定向
            redirect :'/list'
        },
        {
            path:'/add',
            components:{
                addView:Add
            } 
        },
        {
            path:'/update',
            components:{
                updateView:Update
            }  
        },
    ]

})

// 对外暴露路由对象
export default router;
```

+ App.vue

```html
<script setup>
</script>

<template>
    <div>
      <h1>App页面</h1>
      <hr/>
        <!-- 路由的连接 -->
        <router-link to="/">home页</router-link> <br>
        <router-link to="/list">list页</router-link> <br>
        <router-link to="/showAll">showAll页</router-link> <br>
        <router-link to="/add">add页</router-link> <br>
        <router-link to="/update">update页</router-link> <br>
      <hr/>
      <!-- 路由连接对应视图的展示位置 -->
      <hr>
      默认展示位置:<router-view></router-view>
      <hr>
      Home视图展示:<router-view name="homeView"></router-view>
      <hr>
      List视图展示:<router-view name="listView"></router-view>
      <hr>
      Add视图展示:<router-view name="addView"></router-view>
      <hr>
      Update视图展示:<router-view name="updateView"></router-view>
    </div>
</template>

<style scoped>
</style>
```

###  4.4 编程式路由(useRouter)

> 普通路由

+ `<router-link to="/list">list页</router-link>  `这种路由,to中的内容目前是固定的,点击后只能切换/list对象组件(声明式路由)

> 编程式路由

+ 通过useRouter,动态决定向那个组件切换的路由
+ 在 Vue 3 和 Vue Router 4 中，你可以使用 `useRouter` 来实现动态路由(编程式路由)
+ 这里的 `useRouter` 方法返回的是一个 router 对象，你可以用它来做如导航到新页面、返回上一页面等操作。

> 案例需求: 通过普通按钮配合事件绑定实现路由页面跳转,不直接使用router-link标签

创建新项目

+ App.vue

``` html
<script setup type="module">

  import {useRouter} from 'vue-router'
  import {ref} from 'vue'
  //创建动态路由对象
  let router = useRouter()
//绑定
let myPath = ref('')
function goMyPage(){
  router.push(myPath.value)
}

       
  let  routePath =ref('')
  let  showList= ()=>{
      // 编程式路由
      // 直接push一个路径
      //router.push('/list')
      // push一个带有path属性的对象
      router.push({path:'/list'})
  }
</script>

<template>
    <div>
      <h1>App页面</h1>
      <hr/>
        <!-- 路由的连接 -->
        <router-link to="/">home页</router-link> <br>
        <router-link to="/list">list页</router-link> <br>
        <router-link to="/showAll">showAll页</router-link> <br>
        <router-link to="/add">add页</router-link> <br>
        <router-link to="/update">update页</router-link> <br>
        <!-- 动态输入路径,点击按钮,触发单击事件的函数,在函数中通过编程是路由切换页面 -->
        <button @click="showList()">showList</button> <br>
        <hr
    <button @click=goMyPage()>Go My Page</button> <input type="text" v-model="myPath" placeholder="请输入路径">
      <hr/>
      <!-- 路由连接对应视图的展示位置 -->
      <hr>
      默认展示位置:<router-view></router-view>
      <hr>>
    </div>
</template>

<style scoped>
</style>
```

### 4.5 路由传参(useRoute)

> 路径参数

+ 在路径中使用一个动态字段来实现，我们称之为 **路径参数**
  + 例如： 查看数据详情  `/showDetail/1`  ,`1`就是要查看详情的id,可以动态添值！

> 键值对参数

+ 类似与get请求通过url传参,数据是键值对形式的

  + 例如:  查看数据详情`/showDetail?hid=1`,`hid=1`就是要传递的键值对参数

  + 在 Vue 3 和 Vue Router 4 中，你可以使用  `useRoute` 这个函数从 Vue 的组合式 API 中获取路由对象。
  + `useRoute` 方法返回的是当前的 route 对象，你可以用它来获取关于当前路由的信息，如当前的路径、查询参数等。

> 案例需求 : 切换到ShowDetail.vue组件时,向该组件通过路由传递参数

+ 修改App.vue文件

``` html
<script setup type="module">

  import {useRouter} from 'vue-router'

  //创建动态路由对象
  let router = useRouter()
  //动态路由路径传参方法
  let showDetail= (id,language)=>{
      // 尝试使用拼接字符串方式传递路径参数
      //router.push(`showDetail/${id}/${languange}`)
      /*路径参数,需要使用params  */
      router.push({name:"showDetail",params:{id:id,language:language}})
  }
  let showDetail2= (id,language)=>{
      /*uri键值对参数,需要使用query */
      router.push({path:"/showDetail2",query:{id:id,language:language}})
  }
</script>

<template>
    <div>
      <h1>App页面</h1>
      <hr/>
      <!-- 路径参数   -->
      <router-link to="/showDetail/1/JAVA">showDetail路径传参显示JAVA</router-link> 
      <button @click="showDetail(1,'JAVA')">showDetail动态路由路径传参显示JAVA</button>
      <hr/>
      <!-- 键值对参数 -->
      <router-link v-bind:to="{path:'/showDetail2',query:{id:1,language:'Java'}}">showDetail2键值对传参显示JAVA</router-link> 
      <button @click="showDetail2(1,'JAVA')">showDetail2动态路由键值对传参显示JAVA</button>
      <hr>
      showDetail视图展示:<router-view name="showDetailView"></router-view>
      <hr>
      showDetail2视图展示:<router-view name="showDetailView2"></router-view>
    </div>
</template>

<style scoped>
</style>

```

+ 修改router.js增加路径参数占位符

``` javascript
// 导入路由创建的相关方法
import {createRouter,createWebHashHistory} from 'vue-router'

// 导入vue组件

import ShowDetail from '../components/ShowDetail.vue'
import ShowDetail2 from '../components/ShowDetail2.vue'

// 创建路由对象,声明路由规则
const router = createRouter({
    history: createWebHashHistory(),
    routes:[
        
        {
            /* 此处:id  :language作为路径的占位符 */
            path:'/showDetail/:id/:language',
            /* 动态路由传参时,根据该名字找到该路由 */
            name:'showDetail',
            components:{
                showDetailView:ShowDetail
            }  
        },
        {
            path:'/showDetail2',
            components:{
                showDetailView2:ShowDetail2
            }  
        },
    ]

})

// 对外暴露路由对象
export default router;
```

+ ShowDetail.vue 通过useRoute获取路径参数

``` html
<script setup type="module">
    import{useRoute} from 'vue-router'
    import { onUpdated,ref } from 'vue';
    // 获取当前的route对象
    let route =useRoute()
    let languageId = ref(0)
    let languageName = ref('')
    //  借助更新时生命周期,将数据更新进入响应式对象
    onUpdated (()=>{
        // 获取对象中的参数
        languageId.value=route.params.id
        languageName.value=route.params.language
        console.log(languageId.value)
        console.log(languageName.value)
    })
    
</script>

<template>
    <div>
        <h1>ShowDetail页面</h1>
        <h3>编号{{route.params.id}}:{{route.params.language}}是世界上最好的语言</h3>
        <h3>编号{{languageId}}:{{languageName}}是世界上最好的语言</h3>
    </div>
</template>

<style scoped>
</style>

```

-   ShowDetail2.vue通过useRoute获取键值对参数

```html
<script setup type="module">
    import{useRoute} from 'vue-router'
    import { onUpdated,ref } from 'vue';
    // 获取当前的route对象
    let route =useRoute()
    let languageId = ref(0)
    let languageName = ref('')
    //  借助更新时生命周期,将数据更新进入响应式对象
    onUpdated (()=>{
        // 获取对象中的参数(通过query获取参数,此时参数是key-value形式的)
        console.log(route.query)
        console.log(languageId.value)
        console.log(languageName.value)
        languageId.value=route.query.id
        languageName.value=route.query.language
       
    })
    
</script>

<template>
    <div>
        <h1>ShowDetail2页面</h1>
        <h3>编号{{route.query.id}}:{{route.query.language}}是世界上最好的语言</h3>
        <h3>编号{{languageId}}:{{languageName}}是世界上最好的语言</h3>
    </div>
</template>

<style scoped>
</style>

```

### 4.6 路由守卫

> 在 Vue 3 中，路由守卫是用于在路由切换期间进行一些特定任务的回调函数。路由守卫可以用于许多任务，例如验证用户是否已登录、在路由切换前提供确认提示、请求数据等。Vue 3 为路由守卫提供了全面的支持，并提供了以下几种类型的路由守卫：

1.  **全局前置守卫**：在路由切换前被调用，可以用于验证用户是否已登录、中断导航、请求数据等。
2.  **全局后置守卫**：在路由切换之后被调用，可以用于处理数据、操作 DOM 、记录日志等。
3.  **守卫代码的位置**: 在router.js中

```javascript
//全局前置路由守卫
// 设置全局前置守卫 每次页面切换前都会执行beforeEach方法
// toshang to:即将进入的目标路由对象 from上以一个页面  next方法的方法 执行该方法才会放行
router.beforeEach( (to,from,next) => {
    //to 是目标地包装对象  .path属性可以获取地址
    //from 是来源地包装对象 .path属性可以获取地址
    //next是方法，不调用默认拦截！ next() 放行,直接到达目标组件
    //next('/地址')可以转发到其他地址,到达目标组件前会再次经过前置路由守卫
    console.log(to.path,from.path,next)

    //需要判断，注意避免无限重定向
    if(to.path == '/index'){
        next()
    }else{
        next('/index')
    }
    
} )

//全局后置路由守卫
router.afterEach((to, from) => {
    console.log(`Navigate from ${from.path} to ${to.path}`);
});

```

> 登录案例，登录以后才可以进入home,否则必须进入login

+ 定义Login.vue

```html
<script setup>
    import {ref} from 'vue'
    import {useRouter} from 'vue-router'
    let username =ref('')
    let password =ref('')
    let router = useRouter();
    let login = () =>{
        console.log(username.value,password.value)
        if(username.value == 'root' & password.value == '123456'){
            router.push({path:'/home',query:{'username':username.value}})
            //登录成功利用前端存储机制，存储账号！
            localStorage.setItem('username',username.value)
            //sessionStorage.setItem('username',username)
        }else{
            alert('登录失败，账号或者密码错误！');
        }
    }

</script>

<template>
    
    <div>
        账号： <input type="text" v-model="username" placeholder="请输入账号！"><br>
        密码： <input type="password" v-model="password" placeholder="请输入密码！"><br>
        <button @click="login()">登录</button>
    </div>

</template>

<style scoped>
</style>

```

+ 定义Home.vue

```html
<script setup>
 import {ref} from 'vue'
 import {useRoute,useRouter} from 'vue-router'

 let route =useRoute()
 let router = useRouter()
 //  并不是每次进入home页时,都有用户名参数传入
 //let username = route.query.username
 let username =window.localStorage.getItem('username'); 

 let logout= ()=>{
    // 清除localStorge中的username
    //window.sessionStorage.removeItem('username')
    window.localStorage.removeItem('username')
    // 动态路由到登录页
    router.push("/login")

 }

</script>

<template>
    <div>
        <h1>Home页面</h1>
        <h3>欢迎{{username}}登录</h3>
        <button @click="logout">退出登录</button>
    </div>
</template>

<style scoped>

</style>

```

+ App.vue

```html
<script setup type="module">

  
</script>

<template>
    <div>
      
      <router-view></router-view>
      
    </div>
</template>

<style scoped>
</style>

```

+ 定义routers.js

``` javascript
// 导入路由创建的相关方法
import {createRouter,createWebHashHistory} from 'vue-router'

// 导入vue组件

import Home from '../components/Home.vue'
import Login from '../components/login.vue'
// 创建路由对象,声明路由规则
const router = createRouter({
    history: createWebHashHistory(),
    routes:[
        {
            path:'/home',
            component:Home
        },
        {
            path:'/',
            redirect:"/home"
        },
        {
            path:'/login',
            component:Login
        },
    ]

})

// 设置路由的全局前置守卫
router.beforeEach((to,from,next)=>{
    /* 
    to 要去那
    from 从哪里来
    next 放行路由时需要调用的方法,不调用则不放行
    */
    console.log(`从哪里来:${from.path},到哪里去:${to.path}`)

    if(to.path == '/login'){
        //放行路由  注意放行不要形成循环  
        next()
    }else{
        //let username =window.sessionStorage.getItem('username'); 
        let username =window.localStorage.getItem('username'); 
        if(null != username){
            next()
        }else{
            next('/login')
        }

    }
})
// 设置路由的全局后置守卫
router.afterEach((to,from)=>{
    console.log(`从哪里来:${from.path},到哪里去:${to.path}`)
})

// 对外暴露路由对象
export default router;
```

+ 启动测试

```shell
npm run dev
```

## 五、Vue3数据交互axios

### 6.0 预讲知识-promise

#### 6.0.1 普通函数和回调函数

> 普通函数: 正常调用的函数,一般函数执行完毕后才会继续执行下一行代码

``` html
<script>
    let fun1 = () =>{
        console.log("fun1 invoked")
    }
    // 调用函数
    fun1()
    // 函数执行完毕,继续执行后续代码
    console.log("other code processon")
</script>
```

> 回调函数: 一些特殊的函数,表示未来才会执行的一些功能,后续代码不会等待该函数执行完毕就开始执行了

```html
<script>
    // 设置一个2000毫秒后会执行一次的定时任务
    setTimeout(function (){
        console.log("setTimeout invoked")
    },2000)
    console.log("other code processon")
</script>
```

#### 6.0.2 Promise 简介

> 前端中的异步编程技术，类似Java中的多线程+线程结果回调！

+ Promise 是异步编程的一种解决方案，比传统的解决方案——回调函数和事件——更合理和更强大。它由社区最早提出和实现，ES6将其写进了语言标准，统一了用法，原生提供了`Promise`对象。

+ 所谓`Promise`，简单说就是一个容器，里面保存着某个未来才会结束的事件（通常是一个异步操作）的结果。从语法上说，Promise 是一个对象，从它可以获取异步操作的消息。Promise 提供统一的 API，各种异步操作都可以用同样的方法进行处理。

> `Promise`对象有以下两个特点。

	（1）Promise对象代表一个异步操作，有三种状态：`Pending`（进行中）、`Resolved`（已完成，又称 Fulfilled）和`Rejected`（已失败）。只有异步操作的结果，可以决定当前是哪一种状态。
	（2）一旦状态改变，就不会再变，任何时候都可以得到这个结果。Promise对象的状态改变，只有两种可能：从`Pending`变为`Resolved`和从`Pending`变为`Rejected`。只要这两种情况发生，状态就凝固了，不会再变了，会一直保持这个结果。

#### 6.0.3 Promise 基本用法

> ES6规定，Promise对象是一个构造函数，用来生成Promise实例。

```html
    <script>
        
       /*  
        1.实例化promise对象,并且执行(类似Java创建线程对象,并且start)
        参数: resolve,reject随意命名,但是一般这么叫!
        参数: resolve,reject分别处理成功和失败的两个函数! 成功resolve(结果)  失败reject(结果)
        参数: 在function中调用这里两个方法,那么promise会处于两个不同的状态
        状态: promise有三个状态
                pending   正在运行
                resolved  内部调用了resolve方法
                rejected  内部调用了reject方法
        参数: 在第二步回调函数中就可以获取对应的结果 
        */
        let promise =new Promise(function(resolve,reject){
            console.log("promise do some code ... ...")
            //resolve("promise success")
            reject("promise fail")
        })
        console.log('other code1111 invoked')
        //2.获取回调函数结果  then在这里会等待promise中的运行结果,但是不会阻塞代码继续运行
        promise.then(
            function(value){console.log(`promise中执行了resolve:${value}`)},
            function(error){console.log(`promise中执行了reject:${error}`)}
        )
        // 3 其他代码执行   
        console.log('other code2222 invoked')
    </script>
```

#### 6.0.4 Promise catch()

> `Promise.prototype.catch`方法是`.then(null, rejection)`的别名，用于指定发生错误时的回调函数。

```html
<script>
    let promise =new Promise(function(resolve,reject){
        console.log("promise do some code ... ...")
        // 故意响应一个异常对象
        throw new Error("error message")
    })
    console.log('other code1111 invoked')
    /* 
        then中的reject()的对应方法可以在产生异常时执行,接收到的就是异常中的提示信息
        then中可以只留一个resolve()的对应方法,reject()方法可以用后续的catch替换
        then中的reject对应的回调函数被后续的catch替换后,catch中接收的数据是一个异常对象
        */
    promise.then(
        function(resolveValue){console.log(`promise中执行了resolve:${resolveValue}`)}
        //,
        //function(rejectValue){console.log(`promise中执行了reject:${rejectValue}`)}
    ).catch(
        function(error){console.log(error)} 
    )
    console.log('other code2222 invoked')
</script>
```

####  6.0.5 async和await的使用

> &#x20;async和await是ES6中用于处理异步操作的新特性。通常，异步操作会涉及到Promise对象，而async/await则是在Promise基础上提供了更加直观和易于使用的语法。

>  async 用于标识函数的

1. async标识函数后,async函数的返回值会变成一个promise对象

2. 如果函数内部返回的数据是一个非promise对象,async函数的结果会返回一个成功状态 promise对象

3. 如果函数内部返回的是一个promise对象,则async函数返回的状态与结果由该对象决定

4. 如果函数内部抛出的是一个异常,则async函数返回的是一个失败的promise对象

``` html
<script>

    /* 
        async 用于标识函数的         
             async 用户标识函数的 async函数返回的结果就是一个promise
2 方法如果正常return结果，promise状态就是resolved， return后的结果就是成功状态的返回值
3 方法中出现了异常，则返回的promise就是一个失败状态
4 async函数返回的结果如果是一个promise，则状态有内部的promise决定
        */
    	async function fun1(){
            //return 10
            //throw new Error("something wrong")
            let promise = Promise.reject("heihei")
            return promise
        }

        let promise =fun1()

        promise.then(
            function(value){
                console.log("success:"+value)
            }
        ).catch(
            function(value){
                console.log("fail:"+value)
            }
        )
</script>
```

> await

1. await右侧的表达式一般为一个promise对象,
2. 也可以是一个其他值 如果是其他值直接返回该值
3. 如果表达式是promise对象,await返回的是promise成功的值
4. await会等右边的promise对象执行结束,然后再获取结果,后续代码也会等待await的执行
5. 如果表达式是其他值,则直接返回该值
6. await必须在async函数中,但是async函数中可以没有await
7. 如果await右边的promise失败了,就会抛出异常,需要通过 try ... catch捕获处理

``` html
<script>

		async function fun1(){
            return 10
        
        }

        async function fun2(){
            try{
                
                let res = await fun1()
                //let res = await Promise.reject("something wrong")
            }catch(e){
                console.log("catch got:"+e)   
            }
            
            console.log("await got:"+res)
        }

        fun2()
</script>
```

### 6.1 Axios介绍

>  ajax

+ AJAX = Asynchronous JavaScript and XML（异步的 JavaScript 和 XML）。

+ AJAX 不是新的编程语言，而是一种使用现有标准的新方法。

+ AJAX 最大的优点是在不重新加载整个页面的情况下，可以与服务器交换数据并更新部分网页内容。

+ AJAX 不需要任何浏览器插件，但需要用户允许 JavaScript 在浏览器上执行。

+ XMLHttpRequest 只是实现 Ajax 的一种方式。

**ajax工作原理：**

![ajax工作原理.png](http://yanxuan.nosdn.127.net/b7a3d61fd8f670cad30f8555c372bec8.png)

>  原生**javascript方式进行ajax(了解):**

```html
<script>
  function loadXMLDoc(){
    var xmlhttp;
    if (window.XMLHttpRequest){
      //  IE7+, Firefox, Chrome, Opera, Safari 浏览器执行代码
      xmlhttp=new XMLHttpRequest();
    }
    else{
      // IE6, IE5 浏览器执行代码
      xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
      // 设置回调函数处理响应结果
    xmlhttp.onreadystatechange=function(){
      if (xmlhttp.readyState==4 && xmlhttp.status==200)
      {
        document.getElementById("myDiv").innerHTML=xmlhttp.responseText;
      }
    }
      // 设置请求方式和请求的资源路径
    xmlhttp.open("GET","/try/ajax/ajax_info.txt",true);
      // 发送请求
    xmlhttp.send();
  }
</script>   
```

>  什么是axios  官网介绍:https://axios-http.com/zh/docs/intro

+ Axios 是一个基于 [*promise*](https://javascript.info/promise-basics "promise") 网络请求库，作用于[node.js](https://nodejs.org/ "node.js") 和浏览器中。 它是 [*isomorphic*](https://www.lullabot.com/articles/what-is-an-isomorphic-application "isomorphic") 的(即同一套代码可以运行在浏览器和node.js中)。在服务端它使用原生 node.js `http` 模块, 而在客户端 (浏览端) 则使用 XMLHttpRequests。它有如下特性
  + 从浏览器创建 [XMLHttpRequests](https://developer.mozilla.org/en-US/docs/Web/API/XMLHttpRequest "XMLHttpRequests")
  + 从 node.js 创建 [http](http://nodejs.org/api/http.html "http") 请求
  + 支持 [Promise](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise "Promise") API
  + 拦截请求和响应
  + 转换请求和响应数据
  + 取消请求
  + 自动转换JSON数据
  + 客户端支持防御[XSRF](http://en.wikipedia.org/wiki/Cross-site_request_forgery "XSRF")

### 6.2 Axios 入门案例

> 1 案例需求:请求后台获取随机土味情话

+ 请求的url

``` http
https://api.uomg.com/api/rand.qinghua?format=json    或者使用  http://forum.atguigu.cn/api/rand.qinghua?format=json
```

+ 请求的方式

``` http
GET/POST
```

+ 数据返回的格式

```json
{"code":1,"content":"我努力不是为了你而是因为你。"}
```

> 2 准备项目

```javascript
npm create vite
npm install 
```

>  3 安装axios

```shell
npm install axios
```

> 4 设计页面（App.Vue）

```html
<script setup type="module">
  import axios from 'axios'
  import { onMounted,reactive } from 'vue';
    
  let jsonData =reactive({code:1,content:'我努力不是为了你而是因为你'})

  let getLoveMessage =()=>{
    axios({
      method:"post", // 请求方式
      url:"https://api.uomg.com/api/rand.qinghua?format=json",  // 请求的url
      data:{ // 当请求方式为post时,data下的数据以JSON串放入请求体,否则以key=value形式放url后
        username:"123456"
      }
    }).then( function (response){//响应成功时要执行的函数
      console.log(response)
      Object.assign(jsonData,response.data)
    }).catch(function (error){// 响应失败时要执行的函数
      console.log(error)
    })
  }

  /* 通过onMounted生命周期,自动加载一次 */
  onMounted(()=>{
    getLoveMessage()
  })
</script>

<template>
    <div>
      <h1>今日土味情话:{{jsonData.content}}</h1>
      <button  @click="getLoveMessage">获取今日土味情话</button>
    </div>
</template>

<style scoped>
</style>

```

>  5 启动测试

```shell
npm run dev
```

> 异步响应的数据结构

+ 响应的数据是经过包装返回的！一个请求的响应包含以下信息。

```json
{
  // `data` 由服务器提供的响应
  data: {},
  // `status` 来自服务器响应的 HTTP 状态码
  status: 200,
  // `statusText` 来自服务器响应的 HTTP 状态信息
  statusText: 'OK',
  // `headers` 是服务器响应头
  // 所有的 header 名称都是小写，而且可以使用方括号语法访问
  // 例如: `response.headers['content-type']`
  headers: {},
  // `config` 是 `axios` 请求的配置信息
  config: {},
  // `request` 是生成此响应的请求
  // 在node.js中它是最后一个ClientRequest实例 (in redirects)，
  // 在浏览器中则是 XMLHttpRequest 实例
  request: {}
}
```

+ then取值

```javascript
then(function (response) {
    console.log(response.data);
    console.log(response.status);
    console.log(response.statusText);
    console.log(response.headers);
    console.log(response.config);
});
```


> 6 通过async和await处理异步请求

```html
<script setup type="module">
  import axios from 'axios'
  import { onMounted,reactive } from 'vue';
    
  let jsonData =reactive({code:1,content:'我努力不是为了你而是因为你'})

  let getLoveWords = async ()=>{
    return await axios({
      method:"post",
      url:"https://api.uomg.com/api/rand.qinghua?format=json",
      data:{
        username:"123456"
      }
    })
  }

  let getLoveMessage =()=>{
   	 let {data}  = await getLoveWords()
     Object.assign(message,data)
  }

  /* 通过onMounted生命周期,自动加载一次 */
  onMounted(()=>{
    getLoveMessage()
  })
</script>

<template>
    <div>
      <h1>今日土味情话:{{jsonData.content}}</h1>
      <button  @click="getLoveMessage">获取今日土味情话</button>
    </div>
</template>

<style scoped>
</style>

```

>  axios在发送异步请求时的可选配置：

```json
{
  // `url` 是用于请求的服务器 URL
  url: '/user',
  // `method` 是创建请求时使用的方法
  method: 'get', // 默认值
  // `baseURL` 将自动加在 `url` 前面，除非 `url` 是一个绝对 URL。
  // 它可以通过设置一个 `baseURL` 便于为 axios 实例的方法传递相对 URL
  baseURL: 'https://some-domain.com/api/',
  // `transformRequest` 允许在向服务器发送前，修改请求数据
  // 它只能用于 'PUT', 'POST' 和 'PATCH' 这几个请求方法
  // 数组中最后一个函数必须返回一个字符串， 一个Buffer实例，ArrayBuffer，FormData，或 Stream
  // 你可以修改请求头。
  transformRequest: [function (data, headers) {
    // 对发送的 data 进行任意转换处理
    return data;
  }],
  // `transformResponse` 在传递给 then/catch 前，允许修改响应数据
  transformResponse: [function (data) {
    // 对接收的 data 进行任意转换处理
    return data;
  }],
  // 自定义请求头
  headers: {'X-Requested-With': 'XMLHttpRequest'},
  // `params` 是与请求一起发送的 URL 参数
  // 必须是一个简单对象或 URLSearchParams 对象
  params: {
    ID: 12345
  },
  // `paramsSerializer`是可选方法，主要用于序列化`params`
  // (e.g. https://www.npmjs.com/package/qs, http://api.jquery.com/jquery.param/)
  paramsSerializer: function (params) {
    return Qs.stringify(params, {arrayFormat: 'brackets'})
  },
  // `data` 是作为请求体被发送的数据
  // 仅适用 'PUT', 'POST', 'DELETE 和 'PATCH' 请求方法
  // 在没有设置 `transformRequest` 时，则必须是以下类型之一:
  // - string, plain object, ArrayBuffer, ArrayBufferView, URLSearchParams
  // - 浏览器专属: FormData, File, Blob
  // - Node 专属: Stream, Buffer
  data: {
    firstName: 'Fred'
  },
  // 发送请求体数据的可选语法
  // 请求方式 post
  // 只有 value 会被发送，key 则不会
  data: 'Country=Brasil&City=Belo Horizonte',
  // `timeout` 指定请求超时的毫秒数。
  // 如果请求时间超过 `timeout` 的值，则请求会被中断
  timeout: 1000, // 默认值是 `0` (永不超时)
  // `withCredentials` 表示跨域请求时是否需要使用凭证
  withCredentials: false, // default
  // `adapter` 允许自定义处理请求，这使测试更加容易。
  // 返回一个 promise 并提供一个有效的响应 （参见 lib/adapters/README.md）。
  adapter: function (config) {
    /* ... */
  },
  // `auth` HTTP Basic Auth
  auth: {
    username: 'janedoe',
    password: 's00pers3cret'
  },
  // `responseType` 表示浏览器将要响应的数据类型
  // 选项包括: 'arraybuffer', 'document', 'json', 'text', 'stream'
  // 浏览器专属：'blob'
  responseType: 'json', // 默认值
  // `responseEncoding` 表示用于解码响应的编码 (Node.js 专属)
  // 注意：忽略 `responseType` 的值为 'stream'，或者是客户端请求
  // Note: Ignored for `responseType` of 'stream' or client-side requests
  responseEncoding: 'utf8', // 默认值
  // `xsrfCookieName` 是 xsrf token 的值，被用作 cookie 的名称
  xsrfCookieName: 'XSRF-TOKEN', // 默认值
  // `xsrfHeaderName` 是带有 xsrf token 值的http 请求头名称
  xsrfHeaderName: 'X-XSRF-TOKEN', // 默认值
  // `onUploadProgress` 允许为上传处理进度事件
  // 浏览器专属
  onUploadProgress: function (progressEvent) {
    // 处理原生进度事件
  },
  // `onDownloadProgress` 允许为下载处理进度事件
  // 浏览器专属
  onDownloadProgress: function (progressEvent) {
    // 处理原生进度事件
  },
  // `maxContentLength` 定义了node.js中允许的HTTP响应内容的最大字节数
  maxContentLength: 2000,
  // `maxBodyLength`（仅Node）定义允许的http请求内容的最大字节数
  maxBodyLength: 2000,
  // `validateStatus` 定义了对于给定的 HTTP状态码是 resolve 还是 reject promise。
  // 如果 `validateStatus` 返回 `true` (或者设置为 `null` 或 `undefined`)，
  // 则promise 将会 resolved，否则是 rejected。
  validateStatus: function (status) {
    return status >= 200 && status < 300; // 默认值
  },
  // `maxRedirects` 定义了在node.js中要遵循的最大重定向数。
  // 如果设置为0，则不会进行重定向
  maxRedirects: 5, // 默认值
  // `socketPath` 定义了在node.js中使用的UNIX套接字。
  // e.g. '/var/run/docker.sock' 发送请求到 docker 守护进程。
  // 只能指定 `socketPath` 或 `proxy` 。
  // 若都指定，这使用 `socketPath` 。
  socketPath: null, // default
  // `httpAgent` and `httpsAgent` define a custom agent to be used when performing http
  // and https requests, respectively, in node.js. This allows options to be added like
  // `keepAlive` that are not enabled by default.
  httpAgent: new http.Agent({ keepAlive: true }),
  httpsAgent: new https.Agent({ keepAlive: true }),
  // `proxy` 定义了代理服务器的主机名，端口和协议。
  // 您可以使用常规的`http_proxy` 和 `https_proxy` 环境变量。
  // 使用 `false` 可以禁用代理功能，同时环境变量也会被忽略。
  // `auth`表示应使用HTTP Basic auth连接到代理，并且提供凭据。
  // 这将设置一个 `Proxy-Authorization` 请求头，它会覆盖 `headers` 中已存在的自定义 `Proxy-Authorization` 请求头。
  // 如果代理服务器使用 HTTPS，则必须设置 protocol 为`https`
  proxy: {
    protocol: 'https',
    host: '127.0.0.1',
    port: 9000,
    auth: {
      username: 'mikeymike',
      password: 'rapunz3l'
    }
  },
  // see https://axios-http.com/zh/docs/cancellation
  cancelToken: new CancelToken(function (cancel) {
  }),
  // `decompress` indicates whether or not the response body should be decompressed 
  // automatically. If set to `true` will also remove the 'content-encoding' header 
  // from the responses objects of all decompressed responses
  // - Node only (XHR cannot turn off decompression)
  decompress: true // 默认值
}
```

### 6.3 Axios get和post方法

> 配置添加语法

``` javascript
axios.get(url[, config])

axios.get(url,{
   上面指定配置key:配置值,
   上面指定配置key:配置值
})

axios.post(url[, data[, config]])

axios.post(url,{key:value //此位置数据，没有空对象即可{}},{
   上面指定配置key:配置值,
   上面指定配置key:配置值
})
```



> 测试get参数

``` html
<script setup type="module">
  import axios from 'axios'
  import { onMounted,ref,reactive,toRaw } from 'vue';
  let jsonData =reactive({code:1,content:'我努力不是为了你而是因为你'})

  let getLoveWords= async ()=>{
    try{
      return await axios.get(
        'https://api.uomg.com/api/rand.qinghua',
        {
          params:{// 向url后添加的键值对参数
            format:'json',
            username:'zhangsan',
            password:'123456'
          },
          headers:{// 设置请求头
            'Accept' : 'application/json, text/plain, text/html,*/*'
          }
        }
      )
    }catch (e){
      return await e
    }
  }

  let getLoveMessage =()=>{
     let {data}  = await getLoveWords()
     Object.assign(message,data)
  }
  /* 通过onMounted生命周期,自动加载一次 */
  onMounted(()=>{
    getLoveMessage()
  })
</script>

<template>
    <div>
      <h1>今日土味情话:{{jsonData.content}}</h1>
      <button  @click="getLoveMessage">获取今日土味情话</button>
    </div>
</template>

<style scoped>
</style>

```

> 测试post参数

```html
<script setup type="module">
  import axios from 'axios'
  import { onMounted,ref,reactive,toRaw } from 'vue';
  let jsonData =reactive({code:1,content:'我努力不是为了你而是因为你'})

  let getLoveWords= async ()=>{
    try{
      return await axios.post(
        'https://api.uomg.com/api/rand.qinghua',
        {//请求体中的JSON数据
            username:'zhangsan',
            password:'123456'
        },
        {// 其他参数
         	params:{// url上拼接的键值对参数
            	format:'json',
          	},
          	headers:{// 请求头
            	'Accept' : 'application/json, text/plain, text/html,*/*',
            	'X-Requested-With': 'XMLHttpRequest'
          	}
        }
      )
    }catch (e){
      return await e
    }
  }

  let getLoveMessage =()=>{
     let {data}  = await getLoveWords()
     Object.assign(message,data)
  }
  /* 通过onMounted生命周期,自动加载一次 */
  onMounted(()=>{
    getLoveMessage()
  })
</script>

<template>
    <div>
      <h1>今日土味情话:{{jsonData.content}}</h1>
      <button  @click="getLoveMessage">获取今日土味情话</button>
    </div>
</template>

<style scoped>
</style>

```

### 6.4 Axios 拦截器

> 如果想在axios发送请求之前,或者是数据响应回来在执行then方法之前做一些额外的工作,可以通过拦截器完成

```javascript
// 添加请求拦截器 请求发送之前
axios.interceptors.request.use(
  function (config) {
    // 在发送请求之前做些什么
    return config;
  }, 
  function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
  }
);

// 添加响应拦截器 数据响应回来
axios.interceptors.response.use(
  function (response) {
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么
    return response;
  }, 
  function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    return Promise.reject(error);
  }
);
```

+ 定义src/axios.js提取拦截器和配置语法

```javascript
import axios from 'axios'


//  创建instance实例
const instance = axios.create({
    baseURL:'https://api.uomg.com',
    timeout:10000
})

//  添加请求拦截
instance.interceptors.request.use(
    // 设置请求头配置信息
    config=>{
        //处理指定的请求头
        console.log("before request")
        config.headers.Accept = 'application/json, text/plain, text/html,*/*'
        return config
    },
    // 设置请求错误处理函数
    error=>{
        console.log("request error")
        return Promise.reject(error)
    }
)
// 添加响应拦截器
instance.interceptors.response.use(
    // 设置响应正确时的处理函数
    response=>{
        console.log("after success response")
        console.log(response)
        return response
    },
    // 设置响应异常时的处理函数
    error=>{
        console.log("after fail response")
        console.log(error)
        return Promise.reject(error)
    }
)
// 默认导出
export default instance
```

+ App.vue

```html
<script setup type="module">
  // 导入我们自己定义的axios.js文件,而不是导入axios依赖  
  import axios from './axios.js'
  import { onMounted,ref,reactive,toRaw } from 'vue';
  let jsonData =reactive({code:1,content:'我努力不是为了你而是因为你'})

  let getLoveWords= async ()=>{
    try{
      return await axios.post(
        'api/rand.qinghua',
        {
            username:'zhangsan',
            password:'123456'
        },//请求体中的JSON数据
        {
          params:{
            format:'json',
          }
        }// 其他键值对参数
      )
    }catch (e){
      return await e
    }
  }

  let getLoveMessage =()=>{
    // 这里返回的是一个fullfilled状态的promise
    getLoveWords().then(
        (response) =>{
          console.log("after getloveWords")
          console.log(response)
          Object.assign(jsonData,response.data)
        }
    )
  }
  /* 通过onMounted生命周期,自动加载一次 */
  onMounted(()=>{
    getLoveMessage()
  })
</script>

<template>
    <div>
      <h1>今日土味情话:{{jsonData.content}}</h1>
      <button  @click="getLoveMessage">获取今日土味情话</button>
    </div>
   
</template>

<style scoped>
</style>
```

## 六、Vue3状态管理Pinia

### 8.1 Pinia介绍

> 如何实现多个组件之间的数据传递?

+ 方式1 组件传参   

+ 方式2 路由传参  

+ 方式3 通过pinia状态管理定义共享数据

> 当我们有`多个组件共享一个共同的状态(数据源)`时，多个视图可能都依赖于同一份状态。来自不同视图的交互也可能需要更改同一份状态。虽然我们的手动状态管理解决方案（props,组件间通信,模块化）在简单的场景中已经足够了，但是在大规模的生产应用中还有很多其他事项需要考虑：

-   更强的团队协作约定
-   与 Vue DevTools 集成，包括时间轴、组件内部审查和时间旅行调试
-   模块热更新 (HMR)
-   服务端渲染支持

>  [Pinia](https://pinia.vuejs.org/zh/ "Pinia") 就是一个实现了上述需求的状态管理库，由 Vue 核心团队维护，对 Vue 2 和 Vue 3 都可用。https://pinia.vuejs.org/zh/introduction.html

### 8.2 Pinia基本用法

>  1 准备vite项目

```javascript
npm create vite
npm install 
npm install vue-router@4 --save
```

>  2 安装pinia

```javascript
npm install pinia
```

> 3 定义pinia store对象 src/store/store.js \[推荐这么命名不是强制]

```javascript
import {defineStore } from 'pinia'

//定义数据并且对外暴露
// store就是定义共享状态的包装对象
// 内部包含四个属性： id 唯一标识 state 完整类型推理，推荐使用箭头函数 存放的数据 getters 类似属性计算，存储放对数据
// 操作的方法  actions 存储数据的复杂业务逻辑方法
// 理解： store类似Java中的实体类， id就是类名， state 就是装数据值的属性  getters就是get方法，actions就是对数据操作的其他方法
export const definedPerson = defineStore(
    {
        id: 'personPinia', //必须唯一
        state:()=>{ // state中用于定义数据
            return {
                username:'张三',
                age:0,
                hobbies:['唱歌','跳舞']
            }
        },
        getters:{// 用于定义一些通过数据计算而得到结果的一些方法 一般在此处不做对数据的修改操作
                 // getters中的方法可以当做属性值方式使用
            getHobbiesCount(){
                return this.hobbies.length
            },
            getAge(){
                return this.age
            }
        },
        actions:{ // 用于定义一些对数据修改的方法
            doubleAge(){
                this.age=this.age*2
            }
        }
    }
)
```

>  4 在main.js配置pinia组件到vue中 

```javascript
import { createApp } from 'vue'
import App from './App.vue'
import router from './routers/router.js'
// 导pinia
import { createPinia } from 'pinia'
// 创建pinia对象
let pinia= createPinia()

let app =createApp(App)
app.use(router)
// app中使用pinia功能
app.use(pinia) 
app.mount('#app')
```

> 5 Operate.vue 中操作Pinia数据

```html
<script setup type="module">
    import { ref} from 'vue';
    import { definedPerson} from '../store/store';
    // 读取存储的数据
    let person= definedPerson()

    let hobby = ref('')
   
</script>

<template>
    <div>
        <h1>operate视图,用户操作Pinia中的数据</h1>
        请输入姓名:<input type="text" v-model="person.username"> <br>
        请输入年龄:<input type="text" v-model="person.age"> <br>
        请增加爱好:
        <input type="checkbox" value="吃饭"  v-model="person.hobbies"> 吃饭
        <input type="checkbox" value="睡觉"  v-model="person.hobbies"> 睡觉
        <input type="checkbox" value="打豆豆"  v-model="person.hobbies"> 打豆豆 <br>
        
        <!-- 事件中调用person的doubleAge()方法 -->
        <button @click="person.doubleAge()">年龄加倍</button> <br>
        <!-- 事件中调用pinia提供的$reset()方法恢复数据的默认值 -->
        <button @click="person.$reset()">恢复默认值</button> <br>
        <!-- 事件中调用$patch方法一次性修改多个属性值 -->
        <button @click="person.$patch({username:'奥特曼',age:100,hobbies:['晒太阳','打怪兽']})">变身奥特曼</button> <br>
		显示pinia中的person数据:{{person}}
    </div>
</template>
<style scoped>
</style>
```

> 6 List.vue中展示Pinia数据

``` html
<script setup type="module">
    import { definedPerson} from '../store/store';
    // 读取存储的数据
    let person= definedPerson()
</script>

<template>
    <div>
        <h1>List页面,展示Pinia中的数据</h1>
        读取姓名:{{person.username}} <br>
        读取年龄:{{person.age}} <br>
        通过get年龄:{{person.getAge}} <br>
        爱好数量:{{person.getHobbiesCount}} <br>
        所有的爱好:
        <ul>
            <li v-for='(hobby,index) in person.hobbies' :key="index" v-text="hobby"></li>
        </ul>
    </div>
</template>

<style scoped>
</style>

```

> 7 定义组件路由router.js

``` javascript
// 导入路由创建的相关方法
import {createRouter,createWebHashHistory} from 'vue-router'

// 导入vue组件
import List  from '../components/List.vue'
import Operate  from '../components/Operate.vue'
// 创建路由对象,声明路由规则
const router = createRouter({
    history: createWebHashHistory(),
    routes:[
        {
            path:'/opearte',
            component:Operate
        },
        
        {
            path:'/list',
            component:List
        },
    ]

})

// 对外暴露路由对象
export default router;
```

> 8 App.vue中通过路由切换组件

``` html
<script setup type="module">

  
</script>

<template>
    <div>
      <hr>
      <router-link to="/opearte">显示操作页</router-link> <br>
      <router-link to="/list">显示展示页</router-link> <br>
      <hr>
      <router-view></router-view>
    </div>
</template>

<style scoped>
</style>
```

>  9 启动测试

```javascript
npm run dev
```

### 8.3 Pinia其他细节

>  State (状态) 在大多数情况下，state 都是你的 store 的核心。人们通常会先定义能代表他们 APP 的 state。在 Pinia 中，state 被定义为一个返回初始状态的函数。

+ store.js

```javascript
import {defineStore} from 'pinia'

export const definedPerson = defineStore('personPinia',
    {
        state:()=>{
            return {
                username:'',
                age:0,
                hobbies:['唱歌','跳舞']
            }
        },
        getters:{
            getHobbiesCount(){
                return this.hobbies.length
            },
            getAge(){
                return this.age
            }
        },
        actions:{
            doubleAge(){
                this.age=this.age*2
            }
        }
    }
)
```

+ Operate.vue

``` html
<script setup type="module">
    import { ref} from 'vue';
    import { definedPerson} from '../store/store';
    // 读取存储的数据
    let person= definedPerson()

    let hobby = ref('')
    let addHobby= ()=> {
        console.log(hobby.value)
        person.hobbies.push(hobby.value)
    }
    // 监听状态
    person.$subscribe((mutation,state)=>{
        console.log('---subscribe---')
        /* 
        mutation.storeId
            person.$id一样
        mutation.payload
            传递给 cartStore.$patch() 的补丁对象。
        state 数据状态,其实是一个代理
        */
        console.log(mutation)
        console.log(mutation.type)
        console.log(mutation.payload)
        console.log(mutation.storeId)
        console.log(person.$id)
        // 数据 其实是一个代理对象
        console.log(state)
    })


</script>

<template>
    <div>
        <h1>operate视图,用户操作Pinia中的数据</h1>
        请输入姓名:<input type="text" v-model="person.username"> <br>
        请输入年龄:<input type="text" v-model="person.age"> <br>
        请增加爱好:
        <input type="checkbox" value="吃饭"  v-model="person.hobbies"> 吃饭
        <input type="checkbox" value="睡觉"  v-model="person.hobbies"> 睡觉
        <input type="checkbox" value="打豆豆"  v-model="person.hobbies"> 打豆豆 <br>
        <input type="text" @change="addHobby" v-model="hobby"> <br>  
        <!-- 事件中调用person的doubleAge()方法 -->
        <button @click="person.doubleAge()">年龄加倍</button> <br>
        <!-- 事件中调用pinia提供的$reset()方法恢复数据的默认值 -->
        <button @click="person.$reset()">恢复默认值</button> <br>
        <!-- 事件中调用$patch方法一次性修改多个属性值 -->
        <button @click="person.$patch({username:'奥特曼',age:100,hobbies:['晒太阳','打怪兽']})">变身奥特曼</button> <br>
		person:{{person}}
    </div>
</template>
<style scoped>
</style>

```

>  2 Getter 完全等同于 store 的 state 的[计算值](https://cn.vuejs.org/guide/essentials/computed.html "计算值")。可以通过 `defineStore()` 中的 `getters` 属性来定义它们。**推荐**使用箭头函数，并且它将接收 `state` 作为第一个参数：

```javascript
export const useStore = defineStore('main', {
  state: () => ({
    count: 0,
  }),
  getters: {
    doubleCount: (state) => state.count * 2,
  },
})
```

>  3  Action 相当于组件中的 [method](https://v3.vuejs.org/guide/data-methods.html#methods "method")。它们可以通过 `defineStore()` 中的 `actions` 属性来定义，**并且它们也是定义业务逻辑的完美选择。**类似 [getter](https://pinia.vuejs.org/zh/core-concepts/getters.html "getter")，action 也可通过 `this` 访问**整个 store 实例**，并支持**完整的类型标注(以及自动补全)**。不同的是，`action` 可以是异步的，你可以在它们里面 `await` 调用任何 API，以及其他 action！

```javascript
export const useCounterStore = defineStore('main', {
  state: () => ({
    count: 0,
  }),
  actions: {
    increment() {
      this.count++
    },
    randomizeCounter() {
      this.count = Math.round(100 * Math.random())
    },
  },
})
```

## 七、Element-plus组件库

### 10.1 Element-plus介绍

Element Plus 是一套基于 Vue 3 的开源 UI 组件库，是由饿了么前端团队开发的升级版本 Element UI。Element Plus 提供了丰富的 UI 组件、易于使用的 API 接口和灵活的主题定制功能，可以帮助开发者快速构建高质量的 Web 应用程序。

+ 官网https://element-plus.org/zh-CN/


### 10.2 Element-plus入门案例

>  1 准备vite项目

```shell
npm create vite  // 注意选择 vue+typeScript
npm install 
npm install vue-router@4 --save
npm install pinia
npm install axios
```

>  2 安装element-plus

```shell
npm install element-plus
```

> 3 完整引入element-plus

+ main.js

```javascript
import { createApp } from 'vue'
//导入element-plus相关内容
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import App from './App.vue'

const app = createApp(App)

app.use(ElementPlus)
app.mount('#app')
```

>  4 入门案例

+ App.vue

```html
<script setup>
  import { ref } from 'vue'

  const value = ref(true)
</script>

<template>
  <div>
    <!-- 直接使用element-plus组件即可 -->
    <el-button>按钮</el-button>
    <br>
    <el-switch
      v-model="value"
      size="large"
      active-text="Open"
      inactive-text="Close"
    />
    <br />
    <el-switch v-model="value" active-text="Open" inactive-text="Close" />
    <br />
    <el-switch
      v-model="value"
      size="small"
      active-text="Open"
      inactive-text="Close"
    />
  </div>
</template>

<style scoped>

</style>

```

> 5 启动测试

```shell
npm run dev
```

### 10.3 Element-plus常用组件



