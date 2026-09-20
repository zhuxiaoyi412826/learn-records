# JavaWeb 面试题库大全

> 收录范围：原生 JavaWeb（HTTP 协议、Tomcat、Servlet、Request/Response、Cookie/Session、JSP、Filter/Listener、JDBC、Ajax、XML、编码、MVC、Web 安全、场景设计）
> 说明：只含问题，不含答案；不包含 Spring、SpringMVC、Spring Boot、MyBatis、Struts、Hibernate 等框架相关内容

## 一、Web 基础与 HTTP 协议

1. 什么是 JavaWeb？它与传统桌面应用程序有什么区别？
2. B/S 架构与 C/S 架构分别是什么？各有哪些优缺点？
3. 什么是 Web 服务器？Web 服务器与应用服务器有什么区别？
4. 什么是 HTTP 协议？它有哪些主要特点？
5. HTTP 请求报文由哪几部分组成？请说明各部分的作用。
6. HTTP 响应报文由哪几部分组成？
7. HTTP/1.0、HTTP/1.1、HTTP/2.0、HTTP/3.0 之间有哪些区别？
8. HTTP 常见的请求方法有哪些？各自的使用场景是什么？
9. GET 与 POST 的区别有哪些？（至少说出 5 点）
10. PUT 与 POST 的区别是什么？
11. DELETE、HEAD、OPTIONS、TRACE 方法的作用分别是什么？
12. 什么是幂等性？哪些请求方法是幂等的？
13. 常见的 HTTP 状态码有哪些？分别代表什么含义？
14. 301、302、303、307、308 状态码的区别？永久重定向与临时重定向有什么不同？
15. 什么情况下返回 304？强缓存与协商缓存的区别是什么？
16. 403 与 404 的区别是什么？500、502、503、504 分别代表什么？
17. 常见的 HTTP 请求头有哪些？各自的作用是什么？
18. Referer 与 User-Agent 请求头的作用是什么？如何基于 Referer 实现图片防盗链？
19. 常见的 HTTP 响应头有哪些？各自的作用是什么？
20. Content-Type 有哪些常见取值？分别表示什么含义？
21. 什么是 MIME 类型？常见的 MIME 类型有哪些？
22. 如何理解 HTTP 是"无状态"协议？无状态带来了什么问题？
23. 什么是持久连接（Keep-Alive）？它解决了什么问题？
24. 长连接与短连接的区别是什么？服务器如何应对大量长连接？
25. HTTP 与 HTTPS 的区别是什么？默认端口分别是多少？
26. HTTPS 的完整握手过程是怎样的？
27. HTTPS 中对称加密与非对称加密是如何配合使用的？为什么不全部使用非对称加密？
28. 什么是数字证书？CA 证书的作用是什么？
29. 在浏览器地址栏输入 URL 到页面渲染完成，中间经历了哪些过程？
30. 什么是 DNS？域名解析的完整流程是怎样的？递归查询与迭代查询的区别是什么？
31. 什么是 TCP 三次握手？为什么不能是两次？
32. 什么是 TCP 四次挥手？为什么挥手比握手多一次？
33. OSI 七层模型与 TCP/IP 四层模型分别是什么？HTTP、TCP、IP 分别工作在哪一层？
34. 什么是跨域？浏览器的同源策略是什么？"同源"指的是哪三个要素相同？
35. 解决跨域问题有哪些方案？后端层面如何实现？
36. 什么是 CORS？简单请求与非简单请求的区别是什么？预检请求（OPTIONS）是如何触发的？
37. 什么是 RESTful 风格？设计 RESTful 接口有哪些规范？
38. URL 的完整组成部分是什么？什么是 URL 编码？为什么需要对 URL 编码？
39. 什么是正向代理与反向代理？
40. 什么是 WebSocket？它与 HTTP 轮询、长轮询有什么区别？

## 二、Tomcat 与 Web 服务器

41. Tomcat 是什么？为什么说它是 Servlet 容器？
42. Tomcat 的目录结构是怎样的？bin、conf、lib、logs、temp、webapps、work 目录分别存放什么？
43. Tomcat 的整体架构是怎样的？Server、Service、Connector、Engine、Host、Context 之间是什么关系？
44. 一个 HTTP 请求在 Tomcat 内部的完整处理流程是怎样的？
45. Connector 与 Container 各自的职责是什么？
46. Tomcat 支持哪几种 I/O 模型？BIO、NIO、AIO、APR 有什么区别？
47. Tomcat 的类加载机制与 JVM 双亲委派模型有什么不同？为什么要打破双亲委派？
48. 一个 Web 应用的标准目录结构是什么？WEB-INF 目录下的文件为什么不能被客户端直接访问？
49. web.xml 的作用是什么？其中有哪些常见配置项？
50. Tomcat 的默认端口号是多少？如何修改？
51. 如何在 Tomcat 中部署 Web 应用？有哪些部署方式？
52. 什么是虚拟目录与虚拟主机？如何配置？
53. Tomcat 常见的性能优化手段有哪些？
54. Tomcat 的 maxConnections、maxThreads、acceptCount 参数分别代表什么？
55. Tomcat 出现内存溢出（OOM）应如何排查？
56. Tomcat 与 Nginx 的区别是什么？生产环境为什么常用 Nginx + Tomcat 的组合？
57. 如何在一台服务器上运行多个 Tomcat 实例？需要注意什么？

## 三、Servlet 核心

58. 什么是 Servlet？它在 JavaWeb 体系中扮演什么角色？
59. Servlet 接口中定义了哪些方法？
60. Servlet 的生命周期是怎样的？每个阶段的方法各执行几次？
61. Servlet 是单例还是多例？什么情况下会出现多个实例？
62. Servlet 存在线程安全问题吗？产生的根本原因是什么？
63. 如何解决 Servlet 的线程安全问题？
64. init()、service()、destroy() 的调用时机和作用分别是什么？
65. doGet() 与 doPost() 有什么区别？分别适用于什么场景？
66. service() 方法是如何决定调用 doGet() 还是 doPost() 的？
67. GenericServlet 与 HttpServlet 的区别是什么？
68. 开发中为什么继承 HttpServlet 而不直接实现 Servlet 接口？
69. Servlet 的 url-pattern 有哪些匹配规则？优先级如何？
70. 缺省 Servlet 是什么？url-pattern 配置为 "/" 会有什么效果？
71. load-on-startup 配置的作用是什么？数值大小代表什么含义？
72. ServletConfig 的作用是什么？如何获取 Servlet 的初始化参数？
73. ServletContext 的作用是什么？它能实现哪些功能？
74. ServletConfig 与 ServletContext 有什么区别？
75. 一个 Servlet 可以配置多个 url-pattern 吗？多个 Servlet 能映射到同一个 url-pattern 吗？
76. @WebServlet 注解如何使用？与 web.xml 配置方式相比各有什么优劣？
77. Servlet 3.0 引入了哪些新特性？
78. 什么是异步 Servlet？它解决了什么问题？适用于什么场景？
79. request 和 response 对象由谁创建？它们的生命周期是什么？
80. Servlet 与普通 Java 类（如 JavaBean）的区别是什么？

## 四、Request 与 Response

81. HttpServletRequest 有哪些常用方法？
82. HttpServletResponse 有哪些常用方法？
83. request.getParameter() 与 request.getAttribute() 的区别是什么？
84. 如何获取请求头信息？如何获取客户端的真实 IP？
85. 表单中多个同名参数如何获取？getParameterValues() 的使用场景是什么？
86. 请求转发与重定向的区别有哪些？（至少说出 5 点）
87. 转发和重定向分别产生几次请求？浏览器地址栏是否会变化？
88. 转发和重定向能否跳转到外部网站？为什么？
89. 重定向之后还能从 request 域中取到数据吗？为什么？
90. forward() 执行后，其后的代码还会执行吗？编写时需要注意什么？
91. getWriter() 与 getOutputStream() 的区别是什么？两者能否同时使用？
92. 如何设置响应的内容类型与字符编码？setContentType() 与 setCharacterEncoding() 有什么区别？
93. 如何用 Servlet 实现文件下载？需要设置哪些响应头？
94. Content-Disposition 在文件下载中的作用是什么？如何指定下载文件名？
95. 如何向浏览器写入 Cookie？如何读取请求中携带的 Cookie？
96. Servlet 中如何读取 Web 应用内的资源文件？getRealPath() 与 getResourceAsStream() 的区别是什么？

## 五、会话跟踪：Cookie 与 Session

97. 为什么需要会话跟踪技术？
98. 常见的会话跟踪技术有哪几种？
99. Cookie 的工作原理是什么？
100. Cookie 有哪些常用属性？maxAge、path、domain、secure、httpOnly 分别代表什么？
101. Cookie 的 maxAge 取正数、零、负数分别代表什么含义？
102. 如何删除浏览器中的 Cookie？为什么服务器无法直接删除 Cookie？
103. httpOnly 与 secure 属性分别有什么安全作用？
104. Session 的工作原理是什么？服务器如何识别来自同一浏览器的多次请求？
105. Session 在什么时候创建？什么时候销毁？
106. Session 的默认超时时间是多久？有哪些方式可以配置？
107. session.invalidate() 与 session.removeAttribute() 的区别是什么？
108. getSession(true) 与 getSession(false) 的区别是什么？
109. Cookie 与 Session 的区别有哪些？（从存储位置、容量、安全性、生命周期等角度）
110. Session 中的数据存放在服务器的什么位置？大量用户同时在线会有什么风险？
111. 浏览器禁用 Cookie 后 Session 还能正常工作吗？如何解决？
112. 什么是 URL 重写？如何通过 URL 重写传递 SessionID？
113. 什么是 Session 的钝化与活化？Tomcat 是如何实现的？
114. 关闭浏览器后，服务器端的 Session 立即销毁了吗？为什么？
115. 集群、分布式环境下 Session 共享有哪些方案？
116. 什么是会话固定攻击（Session Fixation）？如何防范？
117. 如何利用 Session 与 Listener 统计网站在线人数？
118. 同一浏览器的多个标签页共享同一个 Session 吗？为什么？

## 七、Filter 与 Listener

145. 什么是 Filter？它有哪些典型应用场景？
146. Filter 的生命周期是怎样的？各方法的调用时机是什么？
147. FilterChain 的执行流程是怎样的？
148. 多个 Filter 的执行顺序如何确定？
149. 一个请求先经过 Filter 还是 Servlet？响应返回时又经过谁？
150. Filter 的 url-pattern 支持哪些匹配方式？
151. 如何用 Filter 统一解决全站中文乱码问题？
152. 如何用 Filter 实现登录校验与权限拦截？
153. doFilter() 中如果不调用 chain.doFilter() 会发生什么？
154. Filter 中能否修改 request 的参数？如何通过包装 request 实现参数过滤（如 XSS 转义）？
155. 什么是 Listener？它的作用是什么？
156. Servlet 规范中的监听器分为哪几大类？
157. ServletContextListener、HttpSessionListener、ServletRequestListener 分别监听什么事件？各自的典型用途是什么？
158. HttpSessionBindingListener 与 HttpSessionAttributeListener 的区别是什么？
159. 监听器有哪些典型应用场景？
160. Servlet、Filter、Listener 的加载与初始化顺序是怎样的？

## 八、JDBC 与数据库访问

161. 什么是 JDBC？JDBC API 主要由哪些核心接口组成？
162. 使用 JDBC 访问数据库的完整步骤是什么？
163. Class.forName("com.mysql.cj.jdbc.Driver") 的作用是什么？为什么新版本驱动可以不写？
164. JDBC URL 的格式是什么？各部分分别代表什么含义？
165. Connection、Statement、ResultSet 分别代表什么？
166. Statement、PreparedStatement、CallableStatement 的区别与适用场景是什么？
167. PreparedStatement 为什么能防止 SQL 注入？它的预编译机制是什么？
168. executeQuery()、executeUpdate()、execute()、executeBatch() 的区别是什么？
169. 什么是 SQL 注入？如何在登录场景中演示？如何防范？
170. ResultSet 的 next() 方法有什么作用？如何遍历结果集？
171. JDBC 中如何管理事务？调用 setAutoCommit(false) 之后还需要做什么？
172. 事务的 ACID 特性分别指什么？
173. 并发事务会带来哪些问题？脏读、不可重复读、幻读分别是什么？
174. 事务的四种隔离级别分别是什么？分别能防止哪些问题？
175. MySQL 的默认事务隔离级别是什么？
176. 什么是数据库连接池？为什么需要连接池？
177. 连接池有哪些核心参数？各自的作用是什么？
178. 使用连接池后，调用 close() 的语义发生了什么变化？
179. 如何手写一个简单的数据库连接池？需要考虑哪些问题？
180. 事务控制应该放在业务层还是 DAO 层？为什么？
181. 什么是 DAO 模式？为什么需要单独的 DAO 层？
182. JDBC 如何读写 BLOB、CLOB 类型的大字段？
183. JDBC 如何进行批量插入？有哪些提升批处理效率的思路？
184. Class.forName 加载驱动后，DriverManager 是如何找到具体驱动的？（SPI 机制）

## 九、Ajax、JSON 与前后端交互

185. 什么是 Ajax？它解决了什么问题？
186. 原生 Ajax 的实现步骤是怎样的？
187. XMLHttpRequest 对象有哪些常用方法和属性？
188. readyState 的五个取值分别代表什么？
189. 如何判断 Ajax 请求成功？仅判断 status == 200 够吗？
190. open() 的第三个参数代表什么？同步请求与异步请求的区别是什么？
191. Ajax 发送 GET 请求时参数如何拼接？中文参数如何编码？
192. Ajax 发送 POST 请求时需要额外设置什么请求头？
193. 什么是 JSON？它支持哪些数据类型？
194. JSON 与 XML 相比有哪些优势？
195. 服务端如何将 Java 对象转换为 JSON 字符串返回？
196. 前端如何解析 JSON 字符串？JSON.parse() 与 eval() 的区别是什么？
197. 什么是 JSONP？它的原理是什么？有什么局限性？
198. JSONP 与 CORS 的区别是什么？
199. 跨域的 POST 请求为什么会先发送 OPTIONS 请求？

## 十、XML 与配置文件

200. 什么是 XML？它有哪些典型应用场景？
201. XML 文档由哪些部分组成？声明、元素、属性、注释、CDATA 区分别是什么？
202. XML 有哪几种解析方式？
203. DOM 解析与 SAX 解析的区别是什么？各自的适用场景是什么？
204. DOM4J 等第三方解析库与原生 DOM、SAX 的关系是什么？
205. 什么是 XPath？如何用它查询 XML 节点？
206. DTD 与 Schema 约束的作用是什么？web.xml 顶部那串声明是什么意思？
207. XML 的转义规则是什么？`&lt;`、`&gt;`、`&amp;` 分别代表什么？

## 十一、编码与中文乱码

208. 常见字符集 ISO-8859-1、GB2312、GBK、UTF-8、UTF-16 有什么区别？
209. 产生乱码的根本原因是什么？
210. POST 请求的中文乱码如何解决？为什么 setCharacterEncoding() 必须在 getParameter() 之前调用？
211. GET 请求的中文乱码如何解决？为什么它的解决方式与 POST 不同？
212. 响应内容的中文乱码如何解决？
213. 文件下载时中文文件名乱码如何处理？
214. 数据库读写中文乱码可能由哪些环节引起？如何逐层排查？

## 十二、MVC 与分层架构

215. 什么是 MVC？Model、View、Controller 各自的职责是什么？
216. 什么是 Model1 与 Model2 模式？两者有什么区别？
217. 三层架构（表现层、业务层、持久层）与 MVC 的区别和联系是什么？
218. 为什么要分层开发？分层带来哪些好处与代价？
219. 在纯 Servlet + JSP 技术栈中，M、V、C 分别由什么承担？

## 十三、Web 安全

220. 什么是 XSS？存储型、反射型、DOM 型 XSS 有什么区别？
221. 服务端如何防范 XSS？如何对 HTML 特殊字符进行转义？
222. 什么是 CSRF？攻击流程是怎样的？
223. 如何防范 CSRF？Token 校验、SameSite Cookie 各是什么思路？
224. SQL 注入的成因是什么？在 Web 层与 DAO 层分别如何防范？
225. 密码为什么不能明文存储？MD5 加盐与 BCrypt 的区别是什么？
226. 什么是接口幂等性？如何设计幂等接口？
227. 如何防止表单重复提交？Token 机制如何实现？
228. 如何限制恶意爬虫与接口刷量？

## 十四、综合场景设计题

229. 使用原生 JavaWeb 实现完整的用户注册与登录功能，整体思路是什么？
230. 如何设计并实现"记住用户名"与"七天免登录"功能？
231. 如何用 Filter + Session 实现统一的未登录拦截？
232. 图形验证码的生成与校验流程如何实现？
233. 文件上传如何实现？enctype="multipart/form-data" 的作用是什么？
234. 上传的文件如何重命名存储？如何校验文件类型、防范恶意文件？
235. 购物车功能如何设计？基于 Cookie 与基于 Session 各怎么实现？
236. 商品列表的分页查询如何实现？分页 SQL 怎么编写？
237. 如何实现统一的异常处理与友好错误页面？
238. 如何统计网站的总访问量与当前在线人数？
239. 不使用任何框架，如何手写一个简易 MVC（请求分发器）？核心思路是什么？
240. 高并发场景下，Servlet 与 JSP 层面可以做哪些优化？

