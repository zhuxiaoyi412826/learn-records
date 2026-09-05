# javaweb

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