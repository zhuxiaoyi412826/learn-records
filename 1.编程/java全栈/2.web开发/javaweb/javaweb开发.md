# javaweb

## 什么是Javaweb

**1 什么是javaweb**

用java技术来解决相关 web互联网领域的技术栈，使用 JAVAEE 技术体系开发企业级互联网项目。

**2 javaweb技术栈** 

1. 客户端-前端部分：
   HTML CSS Bootstrap JavaScript  Nodejs npm vite vue3 router pinia  axios json  jQuery element-plus

1：HTML 负责搭建网页结构，CSS 用于美化页面样式，Bootstrap 是前端框架可快速构建界面；JavaScript 为网页添加交互性，jQuery 是简化 JS 操作的库，VU3，ElementPlus 是 Vue3 的组件库，它们共同打造出丰富多样的前端页面。
2：Node.js 是服务器端运行环境，npm 是其包管理工具，Vite 是前端构建工具能提升开发效率；Vue3 是流行的前端框架，Vue Router 用于实现路由功能，Pinia 是状态管理库，Axios 用于前后端数据交互，JSON 是数据交换格式，它们助力构建功能强大的全栈应用。

服务端-后端部分：
HTTP xml Tomcat  Servlet   Request   Response  Cookie  Sesssion  Filter Listener MySQL JDBC  Druid  Jackson lombok jwt maven Postman Swagger

1：HTTP 是用于传输超文本的协议，XML 可用于存储和传输数据；Tomcat 是 Servlet 容器，Servlet 是 Java 编写的服务器端程序，Request 和 Response 对象分别封装客户端请求和服务器响应信息，它们构成了 Web 通信和服务端处理的基础。
2：Cookie 和 Session 用于管理用户状态，Filter 可对请求和响应进行过滤处理，Listener 能监听 Servlet 容器的各种事件；MySQL 是常用的关系型数据库，JDBC 是 Java 操作数据库的接口，Druid 是数据库连接池，Jackson 用于处理 JSON 数据，Lombok 简化 Java 代码，JWT 用于身份验证和授权，这些技术保障了 Web 应用的状态管理、数据存储与安全。
3：Maven 是 Java 项目的管理和构建工具，Postman 则是强大的 API 开发与测试工具，它们为 Java Web 项目的开发、管理和测试提供了便利。swagger为 RESTful API 提供设计、构建、文档生成的开源框架

**3 javaweb交互模式** 

客户端          请求        服务端   返回  客户端

CS模式 客户端和服务器端进行通信

![UTOOLS1744043659221.png](http://yanxuan.nosdn.127.net/5c9d8c29252adbdb01916f41fc0cdc77.png)

BS模式  浏览器端和服务器端进行通信

![BS.jpg](http://yanxuan.nosdn.127.net/fae48140effa8ff16eb953407107f870.jpg)

**4 前后端分离**

```
1 开发分离：后端程序员只要按照接口文档去编写后端代码，无需编写或者关系前端代码，前后端
程序员压力都降低。
2 部署分离：前端使用单独的页面动态技术，通过VUE等框架，工程化项目，前端项目可以部署到
独立的服务器上。
```

**5 前端部分**

- **选择前端框架**：可以选用 Vue.js、React.js 或 Angular 等流行的前端框架。这些框架能够帮助开发者高效地构建用户界面，实现组件化开发和数据绑定。例如，使用 Vue.js 时，可利用其组件化特性将页面拆分成多个小的组件，便于维护和复用。
- **前端项目搭建**：借助前端构建工具（如 Vue CLI、Create React App 等）来快速搭建项目结构。这些工具会自动生成项目的基本目录结构和配置文件，开发者可以在此基础上进行开发。
- **前端开发**：运用 HTML、CSS 和 JavaScript 进行页面设计和交互逻辑的实现。前端开发者专注于用户界面的呈现和交互效果，通过调用后端提供的 API 获取数据并展示在页面上。

**6 后端部分**

- **选择后端框架**：常见的 Java 后端框架有 Spring Boot、Spring MVC 等。Spring Boot 能够简化 Spring 应用的开发和配置，快速搭建独立的、生产级别的 Java 应用。
- **后端项目搭建**：使用 Maven 或 Gradle 等项目管理工具来管理项目的依赖和构建。在项目中添加所需的依赖，如 Spring Boot Starter Web 等，以支持 Web 开发。
- **后端开发**：编写后端接口，处理业务逻辑和数据持久化。后端开发者使用 Java 语言和数据库（如 MySQL、Oracle 等）来实现业务功能，并将数据以 JSON 或 XML 等格式返回给前端。

**7 前后端交互**

- **API 设计**：前后端开发者共同设计 API 接口，明确接口的 URL、请求方法（GET、POST、PUT、DELETE 等）、请求参数和响应格式。API 设计应遵循 RESTful 风格，使接口具有良好的可读性和可维护性。
- **数据交互**：前端通过 AJAX（Asynchronous JavaScript and XML）或 Fetch API 等技术异步调用后端 API，获取数据并更新页面。后端接收到请求后，处理业务逻辑并返回相应的数据。例如，前端使用 Axios 库发送 HTTP 请求：

- **跨域问题处理**：由于前后端项目通常运行在不同的域名或端口上，会出现跨域问题。可以在后端配置跨域请求的支持，如在 Spring Boot 中使用 `@CrossOrigin` 注解或配置跨域过滤器：

**8 部署与测试**

- **前端部署**：将前端项目打包成静态文件（如 HTML、CSS、JavaScript 等），并部署到静态文件服务器（如 Nginx、Apache 等）上。
- **后端部署**：将后端项目打包成可执行的 JAR 或 WAR 文件，部署到应用服务器（如 Tomcat、Jetty 等）上。
- **测试**：进行前后端的集成测试，确保前后端交互正常，业务功能能够正确实现。可以使用 Postman 等工具对后端 API 进行测试，使用浏览器或前端测试工具对前端页面进行测试。

**9 前后端如何通信**

前后端分离如何通信 

1. 前端负责呈现用户界面与交互逻辑，通过 API 接口（如 RESTful API 或 GraphQL）向后端发起数据请求。
2. 后端专注业务逻辑处理与数据存储，接收前端请求后进行相应的处理，如查询数据库、调用其他服务等。
3. 后端处理完请求后，将结果以标准数据格式（如 JSON 或 XML）返回给前端。
4. 前端接收到后端返回的数据后，进行解析并更新页面展示，实现数据的动态呈现与交互效果。

**10 举例**

1.前端发起请求：前端应用在用户操作或页面加载等情况下，通过 HTTP 客户端（如浏览器的 Fetch API 或 Axios 库）向后端服务器发送 HTTP 请求。请求中包含要执行的操作信息（如获取数据、创建记录等）以及相关参数。例如，在一个图书管理系统中，前端可能会发送一个 GET 请求到/api/books来获取所有图书的列表，或者发送一个 POST 请求到/api/books来添加一本新书。
2.后端接收并处理请求：后端服务器接收到前端发送的请求后，根据请求的 URL、HTTP 方法（GET、POST、PUT、DELETE 等）和请求体中的数据来确定要执行的操作。后端通常会使用框架（如 Spring Boot、Express.js 等）来路由请求，并调用相应的业务逻辑处理函数。在图书管理系统中，后端可能会根据请求来查询数据库以获取图书信息，或者将新书的信息插入到数据库中。
3.后端返回响应：后端处理完请求后，将结果封装成 JSON 格式（也可以是其他格式，如 XML，但 JSON 是最常用的），并作为 HTTP 响应发送回前端。响应中包含状态码（如 200 表示成功，404 表示未找到资源，500 表示服务器内部错误等）和响应体（即处理结果数据）。例如，成功获取图书列表后，后端会返回状态码 200 和包含图书数据的 JSON 数组。
4.前端接收并处理响应：前端收到后端的响应后，首先检查状态码以确定请求是否成功。如果成功，前端会解析响应体中的 JSON 数据，并根据需要更新页面内容或执行其他操作。如果请求失败，前端会根据状态码和响应体中的错误信息来向用户显示相应的错误提示。比如，若后端返回 404 状态码，前端可能会在页面上显示 “未找到相关图书” 的提示信息。

# HTTP

**HTTP**：Hyper Text Transfer Protocol(超文本传输协议)，规定了浏览器与服务器之间数据传输的规则。

- http是互联网上应用最为广泛的一种网络协议 
- http协议要求：浏览器在向服务器发送请求数据时，或是服务器在向浏览器发送响应数据时，都必须按照固定的格式进行数据传输



浏览器向服务器进行请求时，服务器按照固定的格式进行解析：

服务器向浏览器进行响应时，浏览器按照固定的格式进行解析：

## HTTP请求协议

- **请求**协议：**浏览器将数据以请求格式发送到服务器。包括：**请求行、请求头 、请求体
- httpservletrequset
- 响应协议：服务器将数据以响应格式返回给浏览器。包括：**响应行** **、响应头 、响应体**
- HTTPServletResponse

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=Zjg4ZDk4ZjAwOGM1MDI1M2Q1Zjc2NjM0MDRiMDJjYzRfeTBLd0RZRFE5YzFzeUdKR2w5QThzNHhNdUhVb2t6dk1fVG9rZW46RjRCbGJWMVA2b1V0TmR4ODVHWmNEbktabktlXzE3ODk2NTY3ODI6MTc4OTY2MDM4Ml9WNA&add_watermark=true&scene_type=CCM)

## 请求 响应

| 模块                   | 发送方                 | 核心作用                     | 存什么内容                                                   | 补充特点                        |
| ---------------------- | ---------------------- | ---------------------------- | ------------------------------------------------------------ | ------------------------------- |
| 请求头 Request Header  | 客户端（浏览器 / APP） | 告诉服务器本次请求的附加信息 | 身份 Token、Cookie、数据格式、浏览器标识、跨域来源、缓存策略 | 所有请求（GET/POST）都有        |
| 请求体 Request Body    | 客户端                 | 提交给服务端的业务数据       | JSON 参数、表单、上传文件                                    | GET 一般无请求体；POST/PUT 才有 |
| 响应头 Response Header | 服务器                 | 告诉客户端返回数据的附加规则 | 返回数据类型、Cookie、跨域配置、缓存、文件大小               | 所有响应都存在                  |
| 响应体 Response Body   | 服务器                 | 接口返回的真实结果           | JSON 数据、HTML 页面、图片、文件流                           | 大部分接口核心返回内容          |



**MOCK** 不依赖后端，提前解锁测试与开发

**runner**

在 Postman、Apifox 里全称 **Collection Runner（集合运行器）**，核心作用：**批量、自动按顺序执行一整套接口，做自动化回归测试**。

**用DeepSeek快速解读文档与设计测试点**

**并生成测试脚本**

## GET vs POST 区别

### 1. 请求参数传递方式

特性 GET POST 参数位置 URL 查询字符串 请求体 (Body) 示例 

?message=你好

 {"message":"你好"} 

可见性 参数在 URL 中可见 参数在请求体中不可见

### 2. Controller 代码差异

GET 请求：

```
@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
public String chat(@RequestParam String message) {
    return chatService.chat(message);
}
```

POST 请求：

```
@PostMapping(
    consumes = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8",
    produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"
)
public String chatPost(@RequestBody ChatRequest request) {
    return chatService.chatWithPrompt(request.getMessage());
}
```

### 3. 数据绑定差异

GET POST 

@RequestParam 直接绑定 String 

@RequestBody 反序列化为对象 简单类型绑定

 对象绑定 无需额外类 需要 ChatRequest 类

### 4. 处理流程差异

GET 流程：

```
URL → @RequestParam → String → chatService.chat()
```

POST 流程：

```
请求体 → @RequestBody → ChatRequest对象 → getMessage() → chatService.chatWithPrompt()
```

### 5. ChatService 方法差异

GET 调用的方法：

```
public String chat(String message) {
    return chatModel.call(message);  // 直接传字符串
}
```

POST 调用的方法：

```
public String chatWithPrompt(String message) {
    Prompt prompt = new Prompt(new UserMessage(message));  // 构建 Prompt 对象
    ChatResponse response = chatModel.call(prompt);
    return response.getResult().getOutput().getContent();
}
```

### 6. 测试命令对比

GET 测试：

```
# 浏览器
http://localhost:8080/api/chat?message=你好

# PowerShell
Invoke-WebRequest -Uri "http://localhost:8080/api/chat?message=你好" -Method GET
```

POST 测试：

```
# PowerShell
$body = @{ message = "你好" } | ConvertTo-Json
Invoke-WebRequest -Uri "http://localhost:8080/api/chat" -Method POST -Body $body -ContentType "application/json"

# curl
curl -X POST -H "Content-Type: application/json" -d "{\"message\":\"你好\"}" http://localhost:8080/api/chat
```

### 7. 适用场景

GET POST 简单查询、测试 复杂数据、长文本 参数较少 参数多或包含特殊字符 浏览器直接访问 需要 JSON 格式 缓存友好 不缓存

### 8. 编码处理

GET POST URL 编码（需注意中文） 请求体 UTF-8 编码 浏览器/PS 自动处理 Content-Type 指定 charset

### 9.总结

主要区别：

1. 参数位置 ：GET 在 URL，POST 在 Body
2. 数据绑定 ：GET 简单 String，POST 对象反序列化
3. Service 方法 ：POST 使用 Prompt 对象，更灵活
4. 测试方式 ：GET 可直接浏览器访问，POST 需要 JSON 格式

# API请求

API接口管理

API 接口是一套**预先定义好的规则、地址、参数**，规定：

1. 怎么向另一套程序发请求；
2. 需要传什么数据（参数）；
3. 对方会返回什么格式结果；
4. 允许做哪些操作（查数据、新增、修改、删除等）。

前端 后端   ①请求 API接口 ②响应 

 接口是软件组件之间相互通信的协议和桥梁。

 前端（如浏览器）通过调用接口来获取数据或执行操作。 

服务端 后端服务 登录 搜索 支付 注册 购物车 评论 数据库 黑马程序员·AI测试 • 后端处理请求并通过接口返回结果。

## RESTFUL风格

述性状态转移，是一套**设计 API 接口的规范思想**。

遵循这套规范写出来的接口，就叫 **RESTful API**。

核心思想：**把互联网里所有资源都当成唯一的 “资源”，用 URL 标识，用 HTTP 动词描述操作**。

URL 定位资源，HTTP 动词操作资源，HTTP 状态码描述结果，JSON 传输数据。

| 请求方式 | 作用                         | 示例接口                                             |
| -------- | ---------------------------- | ---------------------------------------------------- |
| GET      | 查询资源（只读，不修改数据） | GET /users 查询所有用户GET /users/1 查询 id=1 的用户 |
| POST     | 新建资源                     | POST /users 创建一个新用户                           |
| PUT      | 全量更新资源（完整替换）     | PUT /users/1 完整修改 id=1 用户                      |
| PATCH    | 局部更新（只改部分字段）     | PATCH /users/1 只改用户姓名                          |
| DELETE   | 删除资源                     | DELETE /users/1 删除 id=1 用户                       |

## GrapQL

单一请求端点，前端自己声明需要哪些返回字段；后端按需返回；支持关联查询

## URL

| 组成部分       | 示例片段          | 作用说明                                         | 是否可选       |
| -------------- | ----------------- | ------------------------------------------------ | -------------- |
| 协议 Protocol  | `https://`        | 规定网络传输规则，常见 http/https/ws/ftp         | 不可省略       |
| 认证信息 Auth  | `admin:123456@`   | 主机账号密码明文认证，项目极少使用               | 可选           |
| 主机域名 Host  | `api.xxx.com`     | 服务器地址，域名 / IP 均可                       | 不可省略       |
| 端口 Port      | `:8080`           | 服务端口；http 默认 80、https 默认 443 可省略    | 可选           |
| 资源路径 Path  | `/user/list`      | 后端接口地址，标识服务内资源                     | 可选（可为 /） |
| 查询参数 Query | `?page=1&size=10` | GET 请求传递筛选、分页参数，? 开头，& 分隔多参数 | 可选           |
| 锚点 Hash      | `#title`          | 页面内滚动定位，仅前端生效，不传给后端           | 可选           |

请求方法

```
GET：
• POST：
• PUT：
• DELETE：-> 查看/获取数据（SELECT）。-> 提交/创建数据（CREATE）。-> 更新/替换数据
```

**状态码**

| 分类           | 状态码 | 英文释义              | 中文说明       | 业务场景                          |
| -------------- | ------ | --------------------- | -------------- | --------------------------------- |
| 1xx 信息       | 100    | Continue              | 继续           | 客户端可发送请求剩余内容          |
| 1xx 信息       | 101    | Switching Protocols   | 切换协议       | HTTP 升级 WebSocket               |
| 2xx 成功       | 200    | OK                    | 请求成功       | 查询、修改接口正常返回数据        |
| 2xx 成功       | 201    | Created               | 创建成功       | POST 新增订单 / 用户（REST 规范） |
| 2xx 成功       | 204    | No Content            | 无返回内容     | 删除操作，无需返回数据            |
| 3xx 重定向     | 301    | Moved Permanently     | 永久重定向     | 域名永久更换                      |
| 3xx 重定向     | 302    | Found                 | 临时重定向     | 未登录跳转登录页                  |
| 3xx 重定向     | 304    | Not Modified          | 资源未修改     | 读取浏览器缓存，不返回资源        |
| 4xx 客户端错误 | 400    | Bad Request           | 请求错误       | 参数缺失、格式错误                |
| 4xx 客户端错误 | 401    | Unauthorized          | 未认证         | Token 过期、未登录                |
| 4xx 客户端错误 | 403    | Forbidden             | 权限不足       | 已登录但无操作权限                |
| 4xx 客户端错误 | 404    | Not Found             | 资源不存在     | 接口地址写错                      |
| 4xx 客户端错误 | 405    | Method Not Allowed    | 请求方法不允许 | GET 接口使用 POST 调用            |
| 4xx 客户端错误 | 409    | Conflict              | 资源冲突       | 重复创建相同数据                  |
| 5xx 服务端错误 | 500    | Internal Server Error | 服务器内部错误 | 代码报错、数据库异常              |
| 5xx 服务端错误 | 502    | Bad Gateway           | 网关错误       | Nginx 无法连接后端服务            |
| 5xx 服务端错误 | 503    | Service Unavailable   | 服务不可用     | 服务下线、限流、宕机              |
| 5xx 服务端错误 | 504    | Gateway Timeout       | 网关超时       | 接口请求长时间无响应              |

# Servlet

> Servlet 是 Java EE 的服务端组件，运行在 Tomcat 这类 Servlet 容器里，用来接收 HTTP 请求、处理业务、返回 HTTP 响应，是 Java Web 的底层原生 API，SpringMVC 底层就是基于 Servlet 实现。
>
> 是运行在服务端(tomcat)的Java小程序，是sun公司提供一套定义动态资源规范; 从代码层面上来讲Servlet就是一个接口
>
> 用来接收、处理客户端请求、响应给浏览器的动态资源。在整个Web应用中，Servlet主要负责接收处理请求、协同调度功能以及响应数据。我们可以把Servlet称为Web应用中的**控制器**

+ 不是所有的JAVA类都能用于处理客户端请求,能处理客户端请求并做出响应的一套技术标准就是Servlet
+ Servlet是运行在服务端的,所以 Servlet必须在WEB项目中开发且在Tomcat这样的服务容器中运行

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/1681699577344.png)



这类是完整应用服务器，自带 Servlet 容器，不止 Servlet：

1. **WildFly（原 JBoss AS）**：RedHat 开源，内置 Undertow；完整 JakartaEE 实现The Eclips...
2. **GlassFish**：Oracle/Eclipse，JavaEE/JakartaEE 参考实现，内置 Grizzly
3. **WebLogic**：Oracle 商业，企业重型应用服务器
4. **WebSphere / Open Liberty**：IBM；Open Liberty 是开源社区版，内置轻量 Servlet 容器

## 核心要点

1. 属于 **Jakarta Servlet（旧名 javax.servlet）** 规范，不是独立程序，不能直接 main 启动，必须放在 Tomcat/Jetty 容器运行。
2. 核心接口：`Servlet`，常用实现类 `HttpServlet`，重写 `doGet()`、`doPost()` 处理请求。
3. 生命周期：
   - `init()`：第一次访问时**只执行一次**，初始化
   - `service()`：每次请求都会调用，分发 get/post
   - `destroy()`：容器关闭时销毁
4. 三个核心对象
   - `HttpServletRequest`：获取请求参数、请求头、Cookie
   - `HttpServletResponse`：输出响应、设置响应头、写 Cookie
   - `ServletContext`：全局上下文，整个 web 应用共享

5. Web.XML

web.xml 是这个项目的唯一总装清单——学名叫"部署描述符（Deployment Descriptor）

Servlet 容器（Tomcat/Jetty）启动时并不知道你写了哪些 Listener、Filter、欢迎页。它会先读 WEB-INF/web.xml，按里面的声明去反射创建对象、建立 URL 映射、组装过滤器链。

一句话：Java 代码里只是"定义"了类，web.xml 才决定它们"如何被装配、何时被调用"。 因为你没用 Spring，没有任何自动配置，所以这个文件就是全部。

## 实现

### UserServlet

```
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
// @WebServlet(name = "userServlet1", urlPatterns = "/userServlet1")
// 
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        resp.getWriter().write("Hello Servlet");
    }
}
```

+ 自定义一个类,要继承HttpServlet类
+ 重写service方法,该方法主要就是用于处理用户请求的服务方法
+ HttpServletRequest 代表请求对象,是有请求报文经过tomcat转换而来的,通过该对象可以获取请求中的信息
+ HttpServletResponse 代表响应对象,该对象会被tomcat转换为响应的报文,通过该对象可以设置响应中的信息
+ Servlet对象的生命周期(创建,初始化,处理服务,销毁)是由tomcat管理的,无需我们自己new
+ HttpServletRequest HttpServletResponse 两个对象也是有tomcat负责转换,在调用service方法时传入给我们用的

### web.xml为UseServlet配置请求的映射路径

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/1681550398774.png)

```
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_5_0.xsd"
         version="5.0">

    <servlet>
        <!--给UserServlet起一个别名-->
        <servlet-name>userServlet</servlet-name>
        <servlet-class>com.atguigu.servlet.UserServlet</servlet-class>
    </servlet>


    <servlet-mapping>
        <!--关联别名和映射路径-->
        <servlet-name>userServlet</servlet-name>
        <!--可以为一个Servlet匹配多个不同的映射路径,但是不同的Servlet不能使用相同的url-pattern-->
        <url-pattern>/userServlet</url-pattern>
       <!-- <url-pattern>/userServlet2</url-pattern>-->
        <!--
            /        表示通配所有资源,不包括jsp文件
            /*       表示通配所有资源,包括jsp文件
            /a/*     匹配所有以a前缀的映射路径
            *.action 匹配所有以action为后缀的映射路径
        -->
       <!-- <url-pattern>/*</url-pattern>-->
    </servlet-mapping>

</web-app>
```

+ Servlet并不是文件系统中实际存在的文件或者目录,所以为了能够请求到该资源,我们需要为其配置映射路径
+ servlet的请求映射路径配置在web.xml中
+ servlet-name作为servlet的别名,可以自己随意定义,见名知意就好
+ url-pattern标签用于定义Servlet的请求映射路径
+ 一个servlet可以对应多个不同的url-pattern
+ 多个servlet不能使用相同的url-pattern
+ url-pattern中可以使用一些通配写法
  + /        表示通配所有资源,不包括jsp文件
  + /*      表示通配所有资源,包括jsp文件
  + /a/*     匹配所有以a前缀的映射路径
  + *.action 匹配所有以action为后缀的映射路径

### 步骤4 发送请求

开发一个form表单,向servlet发送一个get请求并携带username参数

```
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>Servlet 表单 Demo</title>
    <style>
        body { font-family: system-ui, "Microsoft YaHei"; padding: 40px; }
        input { padding: 6px 10px; }
        button { padding: 6px 16px; cursor: pointer; }
        .tip { color: #6b7280; margin-top: 12px; }
    </style>
</head>
<body>
<h2>用户名校验（Servlet Demo）</h2>

<!-- action 由 JS 动态拼接：自动适配 Jetty(/) 与 Tomcat(/servlet_filter_listemer_war) 两种 contextPath -->
<form id="demoForm" method="get">
    请输入用户名：<input type="text" name="username" value="atguigu" /> <br><br>
    <button type="submit">校验</button>
</form>

<p class="tip">正确用户名是 <b>atguigu</b>，输入相同显示“登录成功”，不同显示“登录失败”。</p>

<script>
    // /static/demo.html => contextPath 就是 /static/ 之前的那一段
    var ctx = location.pathname.indexOf('/static/') >= 0
        ? location.pathname.slice(0, location.pathname.indexOf('/static/'))
        : '';
    document.getElementById('demoForm').action = ctx + '/userServlet1';
</script>
</body>
</html>

```

## 注解开发

```
@WebServlet(name = "userServlet1", urlPatterns = "/userServlet1")
```

## 生命周期

### 概念

> 什么是Servlet的生命周期

-   应用程序中的对象不仅在空间上有层次结构的关系，在时间上也会因为处于程序运行过程中的不同阶段而表现出不同状态和不同行为——这就是对象的生命周期。
-   简单的叙述生命周期，就是对象在容器中从开始创建到销毁的过程。

> Servlet容器

+ Servlet对象是Servlet容器创建的，生命周期方法都是由容器(目前我们使用的是Tomcat)调用的。这一点和我们之前所编写的代码有很大不同。在今后的学习中我们会看到，越来越多的对象交给容器或框架来创建，越来越多的方法由容器或框架来调用，开发人员要尽可能多的将精力放在业务逻辑的实现上。

> Servlet主要的生命周期执行特点

| 生命周期 | 对应方法                                                 | 执行时机               | 执行次数 |
| -------- | -------------------------------------------------------- | ---------------------- | -------- |
| 构造对象 | 构造器                                                   | 第一次请求或者容器启动 | 1        |
| 初始化   | init()                                                   | 构造完毕后             | 1        |
| 处理服务 | service(HttpServletRequest req,HttpServletResponse resp) | 每次请求               | 多次     |
| 销毁     | destory()                                                | 容器关闭               | 1        |

## 生命周期总结

1. 通过生命周期测试我们发现Servlet对象在容器中是单例的
2. 容器是可以处理并发的用户请求的,每个请求在容器中都会开启一个线程
3. 多个线程可能会使用相同的Servlet对象,所以在Servlet中,我们不要轻易定义一些容易经常发生修改的成员变量
4. load-on-startup中定义的正整数表示实例化顺序,如果数字重复了,容器会自行解决实例化顺序问题,但是应该避免重复
5. Tomcat容器中,已经定义了一些随系统启动实例化的servlet,我们自定义的servlet的load-on-startup尽量不要占用数字1-5

## Servlet继承结构

###  Servlet 接口

Servlet 规范接口,所有的Servlet必须实现 

+ public void init(ServletConfig config) throws ServletException;   
  + 初始化方法,容器在构造servlet对象后,自动调用的方法,容器负责实例化一个ServletConfig对象,并在调用该方法时传入
  + ServletConfig对象可以为Servlet 提供初始化参数
+ public ServletConfig getServletConfig();
  + 获取ServletConfig对象的方法,后续可以通过该对象获取Servlet初始化参数
+ public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException;
  + 处理请求并做出响应的服务方法,每次请求产生时由容器调用
  + 容器创建一个ServletRequest对象和ServletResponse对象,容器在调用service方法时,传入这两个对象
+ public String getServletInfo();
  + 获取ServletInfo信息的方法
+ public void destroy();
  + Servlet实例在销毁之前调用的方法

### GenericServlet 抽象类

GenericServlet 抽象类是对Servlet接口一些固定功能的粗糙实现,以及对service方法的再次抽象声明,并定义了一些其他相关功能方法

+ private transient ServletConfig config; 
  + 初始化配置对象作为属性
+ public GenericServlet() { } 
  + 构造器,为了满足继承而准备
+ public void destroy() { } 
  + 销毁方法的平庸实现
+ public String getInitParameter(String name) 
  + 获取初始参数的快捷方法
+ public Enumeration<String> getInitParameterNames() 
  + 返回所有初始化参数名的方法
+ public ServletConfig getServletConfig()
  +  获取初始Servlet初始配置对象ServletConfig的方法
+ public ServletContext getServletContext()
  +  获取上下文对象ServletContext的方法
+ public String getServletInfo() 
  + 获取Servlet信息的平庸实现
+ public void init(ServletConfig config) throws ServletException() 
  + 初始化方法的实现,并在此调用了init的重载方法
+ public void init() throws ServletException 
  + 重载init方法,为了让我们自己定义初始化功能的方法
+ public void log(String msg) 
+ public void log(String message, Throwable t)
  +  打印日志的方法及重载
+ public abstract void service(ServletRequest req, ServletResponse res) throws ServletException, IOException; 
  + 服务方法再次声明
+ public String getServletName() 
  + 获取ServletName的方法

### 自定义Servlet

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/1682299663047.png)

自定义Servlet中,必须要对处理请求的方法进行重写

+ 要么重写service方法
+ 要么重写doGet/doPost方法

##  ServletConfig和ServletContext

### ServletConfig的使用

> ServletConfig是什么

+ 为Servlet提供初始配置参数的一种对象,每个Servlet都有自己独立唯一的ServletConfig对象
+ 容器会为每个Servlet实例化一个ServletConfig对象,并通过Servlet生命周期的init方法传入给Servlet作为属性

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/1682302307081.png)

> ServletConfig是一个接口,定义了如下API

``` java
package jakarta.servlet;
import java.util.Enumeration;
public interface ServletConfig {
    String getServletName();
    ServletContext getServletContext();
    String getInitParameter(String var1);
    Enumeration<String> getInitParameterNames();
}
```

| 方法名                  | 作用                                                         |
| ----------------------- | ------------------------------------------------------------ |
| getServletName()        | 获取\<servlet-name>HelloServlet\</servlet-name>定义的Servlet名称 |
| getServletContext()     | 获取ServletContext对象                                       |
| getInitParameter()      | 获取配置Servlet时设置的『初始化参数』，根据名字获取值        |
| getInitParameterNames() | 获取所有初始化参数名组成的Enumeration对象                    |

> ServletConfig怎么用,测试代码如下

+ 定义Servlet

``` java
public class ServletA extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletConfig servletConfig = this.getServletConfig();
        // 根据参数名获取单个参数
        String value = servletConfig.getInitParameter("param1");
        System.out.println("param1:"+value);
        // 获取所有参数名
        Enumeration<String> parameterNames = servletConfig.getInitParameterNames();
        // 迭代并获取参数名
        while (parameterNames.hasMoreElements()) {
            String paramaterName = parameterNames.nextElement();
            System.out.println(paramaterName+":"+servletConfig.getInitParameter(paramaterName));
        }
    }
}



public class ServletB extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletConfig servletConfig = this.getServletConfig();
        // 根据参数名获取单个参数
        String value = servletConfig.getInitParameter("param1");
        System.out.println("param1:"+value);
        // 获取所有参数名
        Enumeration<String> parameterNames = servletConfig.getInitParameterNames();
        // 迭代并获取参数名
        while (parameterNames.hasMoreElements()) {
            String paramaterName = parameterNames.nextElement();
            System.out.println(paramaterName+":"+servletConfig.getInitParameter(paramaterName));
        }
    }
}
```

+ 配置Servlet

``` xml
  <servlet>
       <servlet-name>ServletA</servlet-name>
       <servlet-class>com.atguigu.servlet.ServletA</servlet-class>
       <!--配置ServletA的初始参数-->
       <init-param>
           <param-name>param1</param-name>
           <param-value>value1</param-value>
       </init-param>
       <init-param>
           <param-name>param2</param-name>
           <param-value>value2</param-value>
       </init-param>
   </servlet>

    <servlet>
        <servlet-name>ServletB</servlet-name>
        <servlet-class>com.atguigu.servlet.ServletB</servlet-class>
        <!--配置ServletB的初始参数-->
        <init-param>
            <param-name>param3</param-name>
            <param-value>value3</param-value>
        </init-param>
        <init-param>
            <param-name>param4</param-name>
            <param-value>value4</param-value>
        </init-param>
    </servlet>

    <servlet-mapping>
        <servlet-name>ServletA</servlet-name>
        <url-pattern>/servletA</url-pattern>
    </servlet-mapping>

    <servlet-mapping>
        <servlet-name>ServletB</servlet-name>
        <url-pattern>/servletB</url-pattern>
    </servlet-mapping>
```

+ 请求Servlet测试

略

### ServletContext的使用

> ServletContext是什么

+ ServletContext对象有称呼为上下文对象,或者叫应用域对象(后面统一讲解域对象)
+ 容器会为每个app创建一个独立的唯一的ServletContext对象
+ ServletContext对象为所有的Servlet所共享
+ ServletContext可以为所有的Servlet提供初始配置参数

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/1682303205351.png)

> ServletContext怎么用

+ 配置ServletContext参数

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_5_0.xsd"
         version="5.0">

    <context-param>
        <param-name>paramA</param-name>
        <param-value>valueA</param-value>
    </context-param>
    <context-param>
        <param-name>paramB</param-name>
        <param-value>valueB</param-value>
    </context-param>
</web-app>
```

+ 在Servlet中获取ServletContext并获取参数

``` java
package com.atguigu.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Enumeration;

public class ServletA extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       
        // 从ServletContext中获取为所有的Servlet准备的参数
        ServletContext servletContext = this.getServletContext();
        String valueA = servletContext.getInitParameter("paramA");
        System.out.println("paramA:"+valueA);
        // 获取所有参数名
        Enumeration<String> initParameterNames = servletContext.getInitParameterNames();
        // 迭代并获取参数名
        while (initParameterNames.hasMoreElements()) {
            String paramaterName = initParameterNames.nextElement();
            System.out.println(paramaterName+":"+servletContext.getInitParameter(paramaterName));
        }
    }
}
```

### ServletContext其他重要API

> 获取资源的真实路径

``` java
String realPath = servletContext.getRealPath("资源在web目录中的路径");
```

> 获取项目的上下文路径

``` java
String contextPath = servletContext.getContextPath();
```

+ 项目的部署名称,也叫项目的上下文路径,在部署进入tomcat时所使用的路径,该路径是可能发生变化的,通过该API动态获取项目真实的上下文路径,可以**帮助我们解决一些后端页面渲染技术或者请求转发和响应重定向中的路径问题**

>  域对象的相关API

+ 域对象: 一些用于存储数据和传递数据的对象,传递数据不同的范围,我们称之为不同的域,不同的域对象代表不同的域,共享数据的范围也不同
+ ServletContext代表应用,所以ServletContext域也叫作应用域,是webapp中最大的域,可以在本应用内实现数据的共享和传递
+ webapp中的三大域对象,分别是应用域,会话域,请求域
+ `后续我们会将三大域对象统一进行讲解和演示`,三大域对象都具有的API如下

| API                                         | 功能解释            |
| ------------------------------------------- | ------------------- |
| void setAttribute(String key,Object value); | 向域中存储/修改数据 |
| Object getAttribute(String key);            | 获得域中的数据      |
| void removeAttribute(String key);           | 移除域中的数据      |

## HttpServletRequest

###  HttpServletRequest简介

> HttpServletRequest是什么

+ HttpServletRequest是一个接口,其父接口是ServletRequest
+ HttpServletRequest是Tomcat将请求报文转换封装而来的对象,在Tomcat调用service方法时传入
+ HttpServletRequest代表客户端发来的请求,所有请求中的信息都可以通过该对象获得

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/1681699577344.png)

### HttpServletRequest常见API

> HttpServletRequest怎么用

+ 获取请求行信息相关(方式,请求的url,协议及版本)

| API                           | 功能解释                       |
| ----------------------------- | ------------------------------ |
| StringBuffer getRequestURL(); | 获取客户端请求的url            |
| String getRequestURI();       | 获取客户端请求项目中的具体资源 |
| int getServerPort();          | 获取客户端发送请求时的端口     |
| int getLocalPort();           | 获取本应用在所在容器的端口     |
| int getRemotePort();          | 获取客户端程序的端口           |
| String getScheme();           | 获取请求协议                   |
| String getProtocol();         | 获取请求协议及版本号           |
| String getMethod();           | 获取请求方式                   |

+ 获得请求头信息相关

| API                                   | 功能解释               |
| ------------------------------------- | ---------------------- |
| String getHeader(String headerName);  | 根据头名称获取请求头   |
| Enumeration<String> getHeaderNames(); | 获取所有的请求头名字   |
| String getContentType();              | 获取content-type请求头 |

+ 获得请求参数相关

| API                                                     | 功能解释                             |
| ------------------------------------------------------- | ------------------------------------ |
| String getParameter(String parameterName);              | 根据请求参数名获取请求单个参数值     |
| String[] getParameterValues(String parameterName);      | 根据请求参数名获取请求多个参数值数组 |
| Enumeration<String> getParameterNames();                | 获取所有请求参数名                   |
| Map<String, String[]> getParameterMap();                | 获取所有请求参数的键值对集合         |
| BufferedReader getReader() throws IOException;          | 获取读取请求体的字符输入流           |
| ServletInputStream getInputStream() throws IOException; | 获取读取请求体的字节输入流           |
| int getContentLength();                                 | 获得请求体长度的字节数               |

+ 其他API

| API                                          | 功能解释                    |
| -------------------------------------------- | --------------------------- |
| String getServletPath();                     | 获取请求的Servlet的映射路径 |
| ServletContext getServletContext();          | 获取ServletContext对象      |
| Cookie[] getCookies();                       | 获取请求中的所有cookie      |
| HttpSession getSession();                    | 获取Session对象             |
| void setCharacterEncoding(String encoding) ; | 设置请求体字符集            |

## HttpServletResponse

### HttpServletResponse简介

> HttpServletResponse是什么

+ HttpServletResponse是一个接口,其父接口是ServletResponse
+ HttpServletResponse是Tomcat预先创建的,在Tomcat调用service方法时传入
+ HttpServletResponse代表对客户端的响应,该对象会被转换成响应的报文发送给客户端,通过该对象我们可以设置响应信息

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/1681699577344.png)

###  HttpServletResponse的常见API

> HttpServletRequest怎么用

+ 设置响应行相关

| API                        | 功能解释       |
| -------------------------- | -------------- |
| void setStatus(int  code); | 设置响应状态码 |


+ 设置响应头相关

| API                                                    | 功能解释                                         |
| ------------------------------------------------------ | ------------------------------------------------ |
| void setHeader(String headerName, String headerValue); | 设置/修改响应头键值对                            |
| void setContentType(String contentType);               | 设置content-type响应头及响应字符集(设置MIME类型) |

+ 设置响应体相关

| API                                                       | 功能解释                                                |
| --------------------------------------------------------- | ------------------------------------------------------- |
| PrintWriter getWriter() throws IOException;               | 获得向响应体放入信息的字符输出流                        |
| ServletOutputStream getOutputStream() throws IOException; | 获得向响应体放入信息的字节输出流                        |
| void setContentLength(int length);                        | 设置响应体的字节长度,其实就是在设置content-length响应头 |

+ 其他API

| API                                                          | 功能解释                                            |
| ------------------------------------------------------------ | --------------------------------------------------- |
| void sendError(int code, String message) throws IOException; | 向客户端响应错误信息的方法,需要指定响应码和响应信息 |
| void addCookie(Cookie cookie);                               | 向响应体中增加cookie                                |
| void setCharacterEncoding(String encoding);                  | 设置响应体字符集                                    |

> MIME类型

+ MIME类型,可以理解为文档类型,用户表示传递的数据是属于什么类型的文档
+ 浏览器可以根据MIME类型决定该用什么样的方式解析接收到的响应体数据
+ 可以这样理解: 前后端交互数据时,告诉对方发给对方的是 html/css/js/图片/声音/视频/... ...
+ tomcat/conf/web.xml中配置了常见文件的拓展名和MIMIE类型的对应关系
+ 常见的MIME类型举例如下

| 文件拓展名                  | MIME类型               |
| --------------------------- | ---------------------- |
| .html                       | text/html              |
| .css                        | text/css               |
| .js                         | application/javascript |
| .png /.jpeg/.jpg/... ...    | image/jpeg             |
| .mp3/.mpe/.mpeg/ ... ...    | audio/mpeg             |
| .mp4                        | video/mp4              |
| .m1v/.m1v/.m2v/.mpe/... ... | video/mpeg             |

# 请求转发和响应重定向

## 概述

> 什么是请求转发和响应重定向

+ 请求转发和响应重定向是web应用中间接访问项目资源的两种手段,也是Servlet控制页面跳转的两种手段

+ 请求转发通过HttpServletRequest实现,响应重定向通过HttpServletResponse实现

+ 请求转发生活举例: 张三找李四借钱,李四没有,李四找王五,让王五借给张三
+ 响应重定向生活举例:张三找李四借钱,李四没有,李四让张三去找王五,张三自己再去找王五借钱

## 9.2 请求转发

> 请求转发运行逻辑图

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/1682321228643.png)

> 请求转发特点(背诵)

+ 请求转发通过HttpServletRequest对象获取请求转发器实现
+ 请求转发是服务器内部的行为,对客户端是屏蔽的
+ 客户端只发送了一次请求,客户端地址栏不变
+ 服务端只产生了一对请求和响应对象,这一对请求和响应对象会继续传递给下一个资源
+ 因为全程只有一个HttpServletRequset对象,所以请求参数可以传递,请求域中的数据也可以传递
+ 请求转发可以转发给其他Servlet动态资源,也可以转发给一些静态资源以实现页面跳转
+ 请求转发可以转发给WEB-INF下受保护的资源
+ 请求转发不能转发到本项目以外的外部资源

> 请求转发测试代码

+ ServletA

``` java
@WebServlet("/servletA")
public class ServletA extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //  获取请求转发器
        //  转发给servlet  ok
        RequestDispatcher  requestDispatcher = req.getRequestDispatcher("servletB");
        //  转发给一个视图资源 ok
        //RequestDispatcher requestDispatcher = req.getRequestDispatcher("welcome.html");
        //  转发给WEB-INF下的资源  ok
        //RequestDispatcher requestDispatcher = req.getRequestDispatcher("WEB-INF/views/view1.html");
        //  转发给外部资源   no
        //RequestDispatcher requestDispatcher = req.getRequestDispatcher("http://www.atguigu.com");
        //  获取请求参数
        String username = req.getParameter("username");
        System.out.println(username);
        //  向请求域中添加数据
        req.setAttribute("reqKey","requestMessage");
        //  做出转发动作
        requestDispatcher.forward(req,resp);
    }
}
```

+ ServletB

``` java
@WebServlet("/servletB")
public class ServletB extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 获取请求参数
        String username = req.getParameter("username");
        System.out.println(username);
        // 获取请求域中的数据
        String reqMessage = (String)req.getAttribute("reqKey");
        System.out.println(reqMessage);
        // 做出响应
        resp.getWriter().write("servletB response");        
    }
}
```

+ 打开浏览器,输入以下url测试

``` http
http://localhost:8080/web03_war_exploded/servletA?username=atguigu
```

## 响应重定向

> 响应重定向运行逻辑图

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/1682322460011.png)

> 响应重定向特点(背诵)

+ 响应重定向通过HttpServletResponse对象的sendRedirect方法实现
+ 响应重定向是服务端通过302响应码和路径,告诉客户端自己去找其他资源,是在服务端提示下的,客户端的行为
+ 客户端至少发送了两次请求,客户端地址栏是要变化的
+ 服务端产生了多对请求和响应对象,且请求和响应对象不会传递给下一个资源
+ 因为全程产生了多个HttpServletRequset对象,所以请求参数不可以传递,请求域中的数据也不可以传递
+ 重定向可以是其他Servlet动态资源,也可以是一些静态资源以实现页面跳转
+ 重定向不可以到给WEB-INF下受保护的资源
+ 重定向可以到本项目以外的外部资源

> 响应重定向测试代码

+ ServletA

``` java
@WebServlet("/servletA")
public class ServletA extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //  获取请求参数
        String username = req.getParameter("username");
        System.out.println(username);
        //  向请求域中添加数据
        req.setAttribute("reqKey","requestMessage");
        //  响应重定向
        // 重定向到servlet动态资源 OK
        resp.sendRedirect("servletB");
        // 重定向到视图静态资源 OK
        //resp.sendRedirect("welcome.html");
        // 重定向到WEB-INF下的资源 NO
        //resp.sendRedirect("WEB-INF/views/view1");
        // 重定向到外部资源
        //resp.sendRedirect("http://www.atguigu.com");
    }
}
```

+ ServletB

``` java
@WebServlet("/servletB")
public class ServletB extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 获取请求参数
        String username = req.getParameter("username");
        System.out.println(username);
        // 获取请求域中的数据
        String reqMessage = (String)req.getAttribute("reqKey");
        System.out.println(reqMessage);
        // 做出响应
        resp.getWriter().write("servletB response");

    }
}
```

+ 打开浏览器,输入以下url测试

``` url
http://localhost:8080/web03_war_exploded/servletA?username=atguigu
```

#   乱码问题

> 乱码问题产生的根本原因是什么

1. 数据的编码和解码使用的不是同一个字符集
2. 使用了不支持某个语言文字的字符集

> 各个字符集的兼容性

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/1682326867396.png)

+ 由上图得知,上述字符集都兼容了ASCII
+ ASCII中有什么? 英文字母和一些通常使用的符号,所以这些东西无论使用什么字符集都不会乱码

###  HTML乱码问题

> 设置项目文件的字符集要使用一个支持中文的字符集

+ 查看当前文件的字符集

+ 查看项目字符集 配置,将Global Encoding 全局字符集,Project Encoding 项目字符集, Properties Files 属性配置文件字符集设置为UTF-8

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/1682326229063.png)

> 当前视图文件的字符集通过<meta charset="UTF-8"> 来告知浏览器通过什么字符集来解析当前文件

``` html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    中文
</body>
</html>
```

### Tomcat控制台乱码

> 在tomcat10.1.7这个版本中,修改 tomcat/conf/logging.properties中,所有的UTF-8为GBK即可

> sout乱码问题,设置JVM加载.class文件时使用UTF-8字符集

+ 设置虚拟机加载.class文件的字符集和编译时使用的字符集一致

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/1695189588009.png)



### 请求乱码问题

####  GET请求乱码

> GET请求方式乱码分析

+ GET方式提交参数的方式是将参数放到URL后面,如果使用的不是UTF-8,那么会对参数进行URL编码处理
+ HTML中的 <meta charset='字符集'/> 影响了GET方式提交参数的URL编码
+ tomcat10.1.7的URI编码默认为 UTF-8
+ 当GET方式提交的参数URL编码和tomcat10.1.7默认的URI编码不一致时,就会出现乱码

> GET请求方式乱码演示

+ 浏览器解析的文档的<meta charset="GBK" /> 

+ GET方式提交时,会对数据进行URL编码处理 ,是将GBK 转码为 "百分号码"

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/1682385997927.png)

+ tomcat10.1.7 默认使用UTF-8对URI进行解析,造成前后端使用的字符集不一致,出现乱码

> GET请求方式乱码解决

+ 方式1  :设置GET方式提交的编码和Tomcat10.1.7的URI默认解析编码一致即可 (推荐)

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/1682386298048.png)

+ 方式2 : 设置Tomcat10.1.7的URI解析字符集和GET请求发送时所使用URL转码时的字符集一致即可,修改conf/server.xml中 Connecter 添加 URIEncoding="GBK"  (不推荐)

#### POST方式请求乱码

> POST请求方式乱码分析

+ POST请求将参数放在请求体中进行发送
+ 请求体使用的字符集受到了<meta charset="字符集"/> 的影响
+ Tomcat10.1.7 默认使用UTF-8字符集对请求体进行解析
+ 如果请求体的URL转码和Tomcat的请求体解析编码不一致,就容易出现乱码

> POST方式乱码演示

+ POST请求请求体受到了<meta charset="字符集"/> 的影响

+ 请求体中,将GBK数据进行 URL编码

+ 后端默认使用UTF-8解析请求体,出现字符集不一致,导致乱码

> POST请求方式乱码解决

+ 方式1 : 请求时,使用UTF-8字符集提交请求体 (推荐)

+ 方式2 : 后端在获取参数前,设置解析请求体使用的字符集和请求发送时使用的字符集一致 (不推荐)

### 响应乱码问题

> 响应乱码分析

+ 在Tomcat10.1.7中,向响应体中放入的数据默认使用了工程编码 UTF-8
+ 浏览器在接收响应信息时,使用了不同的字符集或者是不支持中文的字符集就会出现乱码

> 响应乱码演示

+ 服务端通过response对象向响应体添加数据

+ 浏览器接收数据解析乱码

> 响应乱码解决

+ 方式1 : 手动设定浏览器对本次响应体解析时使用的字符集(不推荐)
  + edge和 chrome浏览器没有提供直接的比较方便的入口,不方便

+ 方式2: 后端通过设置响应体的字符集和浏览器解析响应体的默认字符集一致(不推荐)

方式3: 通过设置content-type响应头,告诉浏览器以指定的字符集解析响应体(推荐)

# MVC架构模式

```me
1
```

