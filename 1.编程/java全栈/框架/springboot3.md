# 学习目标

1. Spring Boot 是什么、为什么学习，怎么学习

2. **项目创建**（官网 / IDEA / CLI）掌握环境搭建：Maven/Gradle 创建工程

3. **理解** 自动装配、内置服务器、一键依赖管理    理解自动配置原理  

4. **熟练配置文件体系**：application.yml/application.properties、多环境配置、配置优先级、自定义配置参数读取，

5. **注解大全**（`@Controller` `@Service` `@Autowired` 等） 核心注解吃透：启动注解、组件注册、依赖注入、请求映射、条件注解等常用注解作用场景

6. **SpringBoot核心原理** 自动装配原理：SPI 机制、META-INF 配置文件、@EnableAutoConfiguration 流程

   定制化组件 选场景  写配置 分析组件 修改配置文件                                                                                     Starter 启动器机制：理解官方 starter，能手写自定义 starter

   Bean 生命周期、Bean 作用域、循环依赖解决机制

   条件注解：@Conditional 系列，容器按需加载 Bean

   配置绑定原理：@ConfigurationProperties 底层源码逻辑

   SpringBoot 启动完整流程：资源加载、环境准备、容器创建、自动装配、容器刷新

7. **接口开发**（GET / POST / 传参）数据库增删改查 分页、条件查询

8. **持久层整合目标**（业务开发核心) 整合 MyBatis/MyBatis-Plus：XML 映射、注解 CRUD、分页、条件构造器 整合 JdbcTemplate、原生 JDBC、H2 内存数据库用于测试 数据源配置：单数据源、多数据源、Druid 连接池监控与参数调优事务管理：声明式事务 @Transactional、事务传播机制、隔离级别、事务失效场景

9. **整合SSM** 

10. **Redis 整合**：缓存读写、序列化、分布式缓存、缓存击穿 / 雪崩简单处理

11. 简单 CRUD 接口开发：GET/POST/PUT/DELETE 请求、参数接收、统一返回格式、页面跳转 统一返回结果 + 全局异常处理

12. 内置 Web 容器使用：Tomcat/Jetty/Undertow 切换，容器参数调优 ，切WEB容器

13. **Thymeleaf**

14. Web 高级开发目标（企业接口标准）请求参数全套处理：路径变量、表单、JSON、文件上传下载、日期转换、枚举转换

    全局统一处理：全局异常处理器、全局跨域配置、全局日期格式化、统一响应封装

    拦截器、过滤器、监听器三者区别与实际使用场景

    参数校验 JSR303：普通校验、分组校验、自定义校验注解

    RESTful 接口规范设计，区分接口状态码与业务自定义编码

    Swagger/Knife4j 接口文档自动生成，接口调试

15. **项目实战:** 网上交友项目实战开发

16. **运维与监控目标:**                                                                                                                                        SpringBoot Actuator 监控端点：健康检查、线程、内存、日志、请求指标

    SpringBoot Admin 可视化监控面板部署使用

    日志体系：Logback 配置、日志分级、日志文件分割、日志脱敏

    打包部署：jar 包打包、外部配置文件分离、Linux 后台运行、启动脚本

    容器化基础：Docker 打包 SpringBoot 镜像、简单容器启动命令

17. **扩展中间件整合目标**消息队列：RabbitMQ/Kafka 消息发送、消费者监听、可靠消息基础方案

    定时任务：@Scheduled 定时任务、线程池配置、分布式定时任务基础认知

    Elasticsearch 简单整合：文档增删改查、条件检索

    邮件、OSS 文件存储、短信第三方工具集成

    分布式锁：Redis 实现分布式锁解决并发超卖                 

核心目标

1. **能快速搭建各种 Spring Boot 项目 理解自动配置 选场景 定制化组件 导入配置 测试** 

2. **能做登录、权限、文件上传、分页、异常处理 、token 权限管理 、支付等各种业务**

3. **面试能讲清自动装配、事务、Bean 生命周期等底层原理；**

4. **具备整合各类中间件、配置各种数据库、部署上线、简单性能调优能力；**

5. **可独立开发中小型单体后端接口项目；**

6. **能看懂企业项目结构，并增加，修改功能**

7. **独立搭建一套标准后台管理接口项目，包含用户、角色、权限基础模块**

   **规范分层架构：Controller / Service / Mapper / Entity / DTO / VO 分层规范，会使用API接口测试**

   **掌握开发规范：常量类、工具类封装、枚举统一管理、代码复用抽取**

   **能排查常见报错：循环依赖、事务失效、跨域、配置读取失败、连接池耗尽等线上问题**

   **性能优化方向：连接池调参、接口分页、缓存减少 DB 查询、异步处理耗时任务**

# springboot













官方文档 https://docs.springframework.org.cn/spring-boot/index.html

中文文档 https://www.spring-doc.cn/spring-boot/3.2.11-SNAPSHOT/getting-started.html

环境要求

| 组件名称           | Spring Boot 2.x 版本要求                                     | Spring Boot 3.x 版本要求                                     | 关键补充说明                                                 |
| ------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| JDK 版本           | 最低 JDK 8，最高兼容 JDK 17（2.7.x 为 2.x 最终稳定版，支持 JDK 8-17） | 最低 JDK 17，最高兼容 JDK 21（3.0-3.1 支持 JDK 17-20，3.2+ 新增 JDK 21 支持） | 必须安装完整 JDK，仅 JRE 无法完成开发；需正确配置 `JAVA_HOME` 环境变量 |
| Maven 版本         | 最低 3.6.0，推荐 3.8.x 稳定版                                | 最低 3.6.3，推荐 3.9.x 稳定版                                | 3.x 对 Maven 最低版本要求更高，低版本会出现构建兼容性问题    |
| Tomcat 版本        | 最低 8.5.x，默认内嵌 9.0.x 系列最高兼容 9.0.x 最终版         | 最低 10.0.x，默认内嵌 10.1.x 系列最高兼容 10.1.x 最新版      | 2.x 不可使用 Tomcat 10+，3.x 不可使用 Tomcat 9-，核心原因是 3.x 全面切换为 Jakarta EE 包名，与旧版 Tomcat 不兼容 |
| Servlet 版本       | 最低 3.1（Tomcat 8.5），默认 4.0（Tomcat 9.0）               | 最低 5.0（Tomcat 10.0），默认 6.0（Tomcat 10.1）             | Servlet 版本与 Tomcat 版本强绑定，3.x 全面适配 Jakarta Servlet 规范，与 2.x 的 javax.servlet 包不兼容 |
| IntelliJ IDEA 版本 | 最低 2018.3，推荐 2020.3 及以上2.7.x 建议 2021.1+ 获得完整支持 | 最低 2022.1，推荐 2023.2 及以上                              | 3.x 必须使用 2022.1 及以上版本，低版本 IDEA 不支持 Jakarta EE 9+ 规范，会出现代码识别、自动配置失效问题 |

## 1是什么

SpringBoot 帮我们简单、快速地创建⼀个独⽴的、⽣产级别的 Spring 应⽤（说明：SpringBoot
底层是Spring）
⼤多数 SpringBoot 应⽤只需要编写少量配置即可快速整合 Spring 平台以及第三⽅技术
特性：
快速创建独⽴ Spring 应⽤
 SSM：导包、写配置、启动运⾏
直接嵌⼊Tomcat、Jetty or Undertow（⽆需部署 war 包）【Servlet容器】

 linux  java tomcat mysql： war 放到 tomcat 的 webapps下 jar： java环境；  java -jar
starter，简化应⽤整合
场景启动器（starter）：web、json、邮件、oss（对象存储）、异步、定时任务、缓
存...
导包⼀堆，控制好版本。为每⼀种场景准备了⼀个依赖； web-starter。mybatis-starter
按需⾃动配置 Spring 以及 第三⽅库
如果这些场景我要使⽤（⽣效）。这个场景的所有配置都会⾃动配置好。
约定⼤于配置：每个场景都有很多默认配置。
⾃定义：配置⽂件中修改⼏项就可以
提供
⽣产级特性：如 监控指标、健康检查、外部化配置等
监控指标、健康检查（k8s）、外部化配置
⽆代码⽣成、
⽆xml
总结：简化开发，简化配置，简化整合，简化部署，简化监控，简化运维。

## 2 快速体验

**1 创键maven项目**

**2 导入依赖**

```
 <!--    所有springboot项⽬都必须继承⾃spring-boot-starter-parent -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.0.5</version>
    </parent>
```

**3 导入场景启动器** 

```
<dependencies>
        <!--        web 开发的场景启动器    -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
```

**4 编写主程序** 

`@SpringBootApplication` 是 Spring Boot 应用的**核心入口**，通过组合 `@SpringBootConfiguration`、`@EnableAutoConfiguration` 和 `@ComponentScan`，极大简化了 Spring 应用的初始化过程。它体现了 Spring Boot 的**约定大于配置**理念，让开发者可以专注于业务逻辑而非基础配置，是构建现代 Java 应用（尤其是微服务）的首选方式。

```
@SpringBootApplication //这是⼀个 SpringBoot 应⽤
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class,args);
    }
}
```

**5 编写配置文件 applicantion.yml  访问端口8081**

```
server:
  port: 8081
```

**6 编写业务类**

`@RestController` 是 `@Controller` 和 `@ResponseBody` 的组合注解，其主要功能：

1. **声明控制器**：将类标记为 Spring MVC 控制器，处理 HTTP 请求。
2. **返回 JSON/XML**：方法返回值自动序列化为 JSON/XML（通过 `@ResponseBody` 实现），无需手动处理视图解析。

```
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello(){
        return "Hello,Spring Boot 3!";
    }

}
```

7 启动主程序

mvn spring-boot:run

8 访问测试 

[localhost:8081/hello](http://localhost:8081/hello)

9 打包 

```
!--    SpringBoot 应⽤打包插件-->
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
```

 mvn clean package  把项⽬打成可执⾏的jar包 

java -jar demo.jar  启动项⽬

## 3 快速创建

[https://start.spring.io](https://start.spring.io/)       网络有问题的可以切换为               https://start.aliyun.com

**官网快速创建** 

![image-20260610084331049](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20260610084331049.png)

添加需要的依赖 

![image-20260610084616762](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20260610084616762.png)

**idea创建**

![image-20250601204935771](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20250601204935771.png)

选择需要的模块勾选上即可

![image-20250601205421802](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20250601205421802.png)

## 4自动配置

### 1依赖管理机制

1、为什么导⼊starter-web 所有相关依赖都导⼊进来？开发什么场景，导⼊什么场景启动器。
 maven依赖传递原则。A-B-C： A就拥有B和C
导⼊ 场景启动器。 场景启动器 ⾃动把这个场景的所有核⼼依赖全部导⼊进来
       2、为什么版本号都不⽤写？
每个boot项⽬都有⼀个⽗项⽬spring-boot-starter-parent  parent的⽗项⽬是
spring-boot-dependencies  ⽗项⽬ 版本仲裁中⼼，把所有常⻅的jar的依赖版本都声明好了。
       3、⾃定义版本号
利⽤maven的就近原则，直接在当前项⽬ properties 中修改标签中声明⽗项⽬⽤的版本属性的key，直接在导⼊依赖的时候声明版本

<version>版本号</version>

4、第三⽅的jar包， boot⽗项⽬没有管理的需要⾃⾏声明好

```
<!-- https://mvnrepository.com/artifact/com.alibaba/druid -->
 <dependency>
 <groupId>com.alibaba</groupId>
 <artifactId>druid</artifactId>
 <version>1.2.16</version>
 </dependency>
```

![image-20250601213915956](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20250601213915956.png)

### 2自动配置

您需要通过在其中一个 [`@Configuration`](https://docs.springframework.org.cn/spring-framework/docs/7.0.x/javadoc-api/org/springframework/context/annotation/Configuration.html) 类上添加 [`@EnableAutoConfiguration`](https://docs.springframework.org.cn/spring-boot/4.0.0/api/java/org/springframework/boot/autoconfigure/EnableAutoConfiguration.html) 或 [`@SpringBootApplication`](https://docs.springframework.org.cn/spring-boot/4.0.0/api/java/org/springframework/boot/autoconfigure/SpringBootApplication.html) 注解来选择启用自动配置。

|      | 您应该只添加一个 [`@SpringBootApplication`](https://docs.springframework.org.cn/spring-boot/4.0.0/api/java/org/springframework/boot/autoconfigure/SpringBootApplication.html) 或 [`@EnableAutoConfiguration`](https://docs.springframework.org.cn/spring-boot/4.0.0/api/java/org/springframework/boot/autoconfigure/EnableAutoConfiguration.html) 注解。我们通常建议您只将其中一个添加到您的主 [`@Configuration`](https://docs.springframework.org.cn/spring-framework/docs/7.0.x/javadoc-api/org/springframework/context/annotation/Configuration.html) 类中。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

禁用自动配置 您可以使用 [`@SpringBootApplication`](https://docs.springframework.org.cn/spring-boot/4.0.0/api/java/org/springframework/boot/autoconfigure/SpringBootApplication.html) 的 exclude 属性来禁用它们，如以下示例所示

```
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class MyApplication {

}
```



#### 自动配置

⾃动配置的 Tomcat、SpringMVC 等，导⼊场景，容器中就会⾃动配置好这个场景的核⼼组件。
以前：DispatcherServlet、ViewResolver、CharacterEncodingFilter....现在：⾃动配置好的这些组件
验证：容器中有了什么组件，就具有什么功能

```
public static void main(String[] args) {
 //java10局部变量类型的⾃动推断
var ioc = SpringApplication.run(MainApplication.class, args);
 //1、获取容器中所有组件的名字
String[] names = ioc.getBeanDefinitionNames();
 //2、挨个遍历：
// dispatcherServlet beanNameViewResolver characterEncodingFilte multipartResolver
 // SpringBoot 把以前配置的核⼼组件现在都给我们⾃动配置好了。
for (String name : names) {
 System.out.println(name);
 }
 }
```

**默认的包扫描规则**

 @SpringBootApplication  标注的类就是主程序类 SpringBoot只会扫描主程序所在的包及其下⾯的⼦包，

⾃动的component-scan功能 ⾃定义扫描路径

@SpringBootApplication(scanBasePackages = "com.zxy")

@ComponentScan("com.zxy")  直接指定扫描的路径



**配置默认值** 

配置⽂件的所有配置项是和某个类的对象值进⾏⼀⼀绑定的。 绑定了配置⽂件中每⼀项值的类： 属性类。 ⽐如： ServerProperties  绑定了所有Tomcat服务器有关的配置 MultipartProperties  绑定了所有⽂件上传相关的配置 ....参照官⽅⽂档：或者参照 绑定的  属性类。

 按需加载⾃动配置 ,导⼊场景 spring-boot-starter-web  场景启动器除了会导⼊相关功能依赖，导⼊⼀个 starter  的 spring-boot-starter  ，是所有  starter的tarter  基础核⼼starte

总结： 导⼊场景启动器、触发  spring-boot-autoconfigure  这个包的⾃动配置⽣效、容器 中就会具有相关场景的功能

spring-boot-starter  导⼊了⼀个包  都是各种场景的 spring-boot-autoconfigure  。包⾥AutoConfiguration  ⾃动配置类 虽然全场景的⾃动配置都在  spring-boot-autoconfigure  这个包，但是不是全都 开启的。  导⼊哪个场景就开启哪个⾃动配置

#### **启动流程**

![image-20250601215235588](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20250601215235588.png)

**1、导⼊ starter-web  ：导⼊了web开发场景**

1、场景启动器导⼊了相关场景的所有依赖： starter-json  、 starter-tomcat  、 sp ringmvc  

2、每个场景启动器都引⼊了⼀个 spring-boot-starter  ，核⼼场景启动器。

 3、核⼼场景启动器引⼊了 spring-boot-autoconfigure  包。

 4、 spring-boot-autoconfigure  ⾥⾯囊括了所有场景的所有配置。

 5、只要这个包下的所有类都能⽣效，那么相当于SpringBoot官⽅写好的整合功能就⽣效了。

 6、SpringBoot默认却扫描不到  spring-boot-autoconfigure  下写好的所有配置类。 （这些配置类给我们做了整合操作），默认只扫描主程序所在的包。

**2、主程序： @SpringBootApplication**

1、 @SpringBootApplication  由三个注解组成 @SpringBootConfiguration  、 @E nableAutoConfiguratio  、 @ComponentScan 

 2、SpringBoot默认只能扫描⾃⼰主程序所在的包及其下⾯的⼦包，扫描不到  spring-boo t-autoconfigure  包中官⽅写好的配置类 

3、 @EnableAutoConfiguration  ：SpringBoot 开启⾃动配置的核⼼。 

1. 是由 @Import(AutoConfigurationImportSelector.class)  提供功能：批量 给容器中导⼊组件。 

2. SpringBoot启动会默认加载 142个配置类。 

3. 这142个配置类来⾃于 spring-boot-autoconfigure  下  META-INF/spring/o rg.springframework.boot.autoconfigure.AutoConfiguration.import s  ⽂件指定的 项⽬启动的时候利⽤ @Import 批量导⼊组件机制把  autoconfigure  包下的142  xx xxAutoConfiguration  类导⼊进来（⾃动配置类） 虽然导⼊了 142  个⾃动配置类 

4、按需⽣效： 并不是这 142  个⾃动配置类都能⽣效 每⼀个⾃动配置类，都有条件注解 @ConditionalOnxxx  ，只有条件成⽴，才能⽣效

**3、 xxxxAutoConfiguration  ⾃动配置类**

1、给容器中使⽤@Bean 放⼀堆组件。

 2、每个⾃动配置类都可能有这个注解 @EnableConfigurationProperties(ServerPr operties.class)  ，⽤来把配置⽂件中配的指定前缀的属性值封装到  xxxPropertie属性类中 

3、以Tomcat为例：把服务器的所有配置都是以  server  开头的。配置都封装到了属性类中

4、给容器中放的所有组件的⼀些核⼼参数，都来⾃于 xxxProperties ，xxxProperties 都是和配置⽂件绑定。

  只需要改配置⽂件的值，核⼼组件的底层参数都能修改

#### **核⼼流程总结：**

 1、导⼊ starter 就会导⼊autoconfigure  包

2 autoconfigure  包⾥⾯ 有⼀个⽂件   META-INF/spring/org.springframework.bo ot.autoconfigure.AutoConfiguration.imports  ,⾥⾯指定的所有启动要加载的⾃动配 置类 

3、@EnableAutoConfiguration 会⾃动的把上⾯⽂件⾥⾯写的所有⾃动配置类都导⼊进来xxxAutoConfiguration 是有条件注解进⾏按需加载

4、 xxxAutoConfiguration  给容器中导⼊⼀堆组件，组件都是从  xxxProperties  中提取 属性值 

5、 xxxProperties  ⼜是和配置⽂件进⾏了绑定

导⼊ starter  、修改配置⽂件，就能修改底层⾏为。

## 5常用注解

SpringBoot 摒弃XML配置⽅式，改为全注解驱动

### **组件注解**

1. @Configuration、@SpringBootConfiguration   这是一个配置类
2. @Bean、@Scope 
3. @Controller、 @Service、@Repository、@Component
4. @Import 
5. @ComponentScan

1、@Configuration 编写⼀个配置类 

2、在配置类中，⾃定义⽅法给容器中注册组件。配合@Bean 

3、或使⽤@Import 导⼊第三⽅的组件

### **条件注解**

如 果注解指定的条件成⽴，则触发指定⾏为

1. @ConditionalOnXxx @ConditionalOnClass：如果类路径中存在这个类，则触发指定⾏为 
2. @ConditionalOnMissingClass：如果类路径中不存在这个类，则触发指定⾏为 
3. @ConditionalOnBean：如果容器中存在这个Bean（组件），则触发指定⾏为 
4. @ConditionalOnMissingBean：如果容器中不存在这个Bean（组件），则触发指定⾏为
5. @ConditionalOnBean（value=组件类型，name=组件名字）：判断容器中是否有这个类型的组 件，并且名字是指定的值

### **属性绑定**

```
@ConfigurationProperties： 声明组件的属性和配置⽂件哪些前缀开始项进⾏绑定
@EnableConfigurationProperties：快速注册注解：
场景：SpringBoot默认只扫描⾃⼰主程序所在的包。如果导⼊第三⽅包，即使组件上标注了 
@Component、@ConfigurationProperties 注解，也没⽤。因为组件都扫描不进来，此时使
⽤这个注解就可以快速进⾏属性绑定并把组件注册进容器
将容器中任意组件（Bean）的属性值和配置⽂件的配置项的值进⾏绑定
 1、给容器中注册组件（@Component、@Bean）
2、使⽤@ConfigurationProperties 声明组件和配置⽂件的哪些配置项进⾏绑定
```



## 6yaml

SpringBoot 集中化管理配置，  **application.properties**

YAML 是 "YAML Ain't a Markup Language"（YAML 不是⼀种标记语⾔）。

在开发的这种语⾔ 时，YAML 的意思其实是："Yet Another Markup Language"（是另⼀种标记语⾔）

设计⽬标，就是⽅便⼈类读写 层次分明，更适合做配置⽂件   使⽤  **.yaml  或  .yml**  作为⽂件后缀

**基本语法**

-  ⼤⼩写敏感 使⽤缩进表示层级关系，k: v，使⽤空格分割k,v
-  缩进时不允许使⽤Tab键，只允许使⽤空格。换⾏
-  缩进的空格数⽬不重要，只要相同层级的元素左侧对⻬即可 #
- #表示注释，从这个字符⼀直到⾏尾，都会被解析器忽略。 

⽀持的写法： 

- 对象：键值对的集合，如：映射（map）/ 哈希（hash） / 字典（dictionary）
-  数组：⼀组按次序排列的值，如：序列（sequence） / 列表（list） 
- 纯量：单个的、不可再分的值，如：字符串、数字、bool、⽇期

**yml**

```
person:
  name: 张三
  age: 18
  birthDay: 2010/10/12 12:12:12
  like: true
  child:
    name: 李四
    age: 12
    birthDay: 2018/10/12
    text:
      - abc
      - def
  dogs:
    - name: 小黑
      age: 3
    - name: 小白
      age: 2
  cats:
    c1:
      name: 小蓝
      age: 3
    c2:
      name: 小灰
      age: 2
```

**properties**

```
 person.name=
张三
person.age=18
 person.birthDay=2010/10/12 12:12:12
 person.like=true
 person.child.name=
李四
person.child.age=12
 person.child.birthDay=2018/10/12
 person.child.text[0]=abc
 person.child.text[1]=def
 person.dogs[0].name=
⼩⿊
person.dogs[0].age=3
 person.dogs[1].name=
⼩⽩
person.dogs[1].age=2
 person.cats.c1.name=
⼩蓝
person.cats.c1.age=3
 person.cats.c2.name=
⼩灰
person.cats.c2.age=2
```

**细节**

```
birthDay 推荐写为 birth-day
⽂本：
单引号不会转义【\n 则为普通字符串显示】
双引号会转义【\n会显示为换⾏符】
⼤⽂本
开头，⼤⽂本写在下层，保留⽂本格式，换⾏符正确显示
> 
开头，⼤⽂本写在下层，折叠换⾏符
多⽂档合并
使⽤--- 
可以把多个yaml⽂档合并在⼀个⽂档中，每个⽂档区依然认为内容独⽴
```

**lombok**

简化JavaBean 开发。⾃动⽣成构造器、getter/setter、⾃动⽣成Builder模式等

```
 <dependency>
 <groupId>org.projectlombok</groupId>
 <artifactId>lombok</artifactId>
 <scope>compile</scope>
 </dependency>
```

@Data 

## 7日志

项⽬开发不要编写  System.out.println()  ，应该⽤⽇志记录信

### **简介**

1. 简介 commons-logging作为内部⽇志，但底层⽇志实现是开放的。可对接其他⽇志框 使⽤ 架。 a.  spring5及以后 commons-logging被spring直接⾃⼰写了。 

2. ⽀持 jul，log4j2， logback。SpringBoot 提供了默认的控制台输出配置，也可以配置输出为⽂

3. logback是默认使⽤的。 

4.  虽然⽇志框架很多，但是我们不⽤担⼼，使⽤ SpringBoot 的默认配置就能⼯作的很好。

**SpringBoot怎么把⽇志默认配置好的** 

1、每个 starter  场景，都会导⼊⼀个核⼼场景 spring-boot-starter  

2、核⼼场景引⼊了⽇志的所⽤功能 spring-boot-starter-logging  

3、默认使⽤了 logback + slf4j  组合作为默认底层⽇志 

4、 ⽇志是系统⼀启动就要⽤  ， xxxAutoConfiguration  是系统启动好了以后放好的组件， 后来⽤的。 

5、⽇志是利⽤监听器机制配置好的。 ApplicationListener  。

 6、⽇志所有的配置都可以通过修改配置⽂件实现。以 logging  开始的所有配置。

### 日志格式

```
025-06-03T07:13:46.582+08:00  INFO 18220 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8081 (http)
2025-06-03T07:13:46.609+08:00  INFO 18220 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2025-06-03T07:13:46.610+08:00  INFO 18220 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.7]
```

时间和⽇期：毫秒级精度

⽇志级别： ERROR,  错误 WARN,警告  INFO, 信息  DEBUG 调试, or  TRACE 追踪

进程 ID---： 消息分割符

线程名：使⽤[]包含 

Logger名:  通常是产⽣⽇志的类名 

消息： ⽇志记录的内容 注意：

 logback 没有 FATAL级别，对应的是ERROR

**yml配置日志**

```
logging:
  pattern:
    dateformat:   yyyy-MM-dd HH:mm:ss.sss
```

```
2025-06-03 07:31:24,756  INFO 20372 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8081 (http)
2025-06-03 07:31:24,764  INFO 20372 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
```

### 日志分组

```
logging.group.tomcat=org.apache.catalina,org.apache.coyote,org.apache.tomcat
logging.level.tomcat=trace
```

springboot预定了两个组 web SQL

### ⽂件输出

SpringBoot 默认只把⽇志写在控制台，如果想额外记录到⽂件，可以在application.properties中 添加logging.file.name or logging.file.path配置项

```
 logging.file.name 文件名   生成当前项目同位置的地方
 logging.file.path 路径名
未指定未指定仅控制台输出
指定未指定my.log写⼊指定⽂件。可以加路径
未指定指定/var/log写⼊指定⽬录，⽂件名为spring.log
指定指定以logging.file.name为准
```

### **日志归档切割**

1. 每天的⽇志应该独⽴分割出来存档。如果使⽤logback（SpringBoot 默认整合），可以通过 application.properties/yaml⽂件指定⽇志滚动规则。

2. 如果是其他⽇志系统，需要⾃⾏配置（添加log4j2.xml或log4j2-spring.xml）
3. ⽀持的滚动规则设置如下

| 配置项名称                                             | 配置说明                                                     | 默认值                             |
| ------------------------------------------------------ | ------------------------------------------------------------ | ---------------------------------- |
| `logging.logback.rollingpolicy.file-name-pattern`      | 日志存档的文件名格式                                         | `${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz` |
| `logging.logback.rollingpolicy.clean-history-on-start` | 应用启动时是否清除以前存档                                   | `false`                            |
| `logging.logback.rollingpolicy.max-file-size`          | 存档前，每个日志文件的最大大小                               | `10MB`                             |
| `logging.logback.rollingpolicy.total-size-cap`         | 日志文件被删除之前，可以容纳的最大大小；设置`1GB`则磁盘存储超过 1GB 日志后就会删除旧日志文件 | `0B`                               |
| `logging.logback.rollingpolicy.max-history`            | 日志文件保存的最大天数                                       | `7`                                |

### 日志级别

由低到⾼：  ALL,TRACE, DEBUG, INFO, WARN, ERROR,FATAL,OFF  

 只会打印指定级别及以上级别的⽇志 

- ALL：打印所有⽇志 TRACE：追踪框架详细流程⽇志，⼀般不使⽤ 
- DEBUG：开发调试细节⽇志
-  INFO：关键、感兴趣信息⽇志 
- WARN：警告但不是错误的信息⽇志，⽐如：版本过时
-  ERROR：业务错误⽇志，⽐如出现各种异常
-  FATAL：致命错误⽇志，⽐如jvm系统崩溃 
- OFF：关闭所有⽇志记录 不指定级别的所有类，都使⽤root指定的级别作为默认级别
-  SpringBoot⽇志默认级别是 INFO

```
1. 在application.properties/yaml中配置logging.level.<logger-name>=<level>指定⽇志级别
2.level可取值范围：TRACE, DEBUG, INFO, WARN, ERROR, FATAL, or OFF 
定义在 LogLevel 类中
3. root 的logger-name叫root，可以配置使⽤ root 的 warn 级别
```

### 导入第三方框架

1.导⼊任何第三⽅框架，先排除它的⽇志包，因为SpringBoot底层控制好了⽇志

2.修改改 application.properties  配置⽂件，就可以调整⽇志的所有⾏为。如果不够，可以编写⽇志框架⾃⼰的配置⽂件放在类路径下就⾏，⽐如**logback-spring.xml ，j2-spring.xml log4**

3.如需对接专业⽇志系统，也只需要把 logback 记录的⽇志灌倒 kafka之类的中间件，这和
SpringBoot没关系，都是⽇志框架⾃⼰的配置，修改配置⽂件即可

4.业务中使⽤slf4j-api记录⽇志。不要再 sout 了

**5.日志门面（SLF4J）**

- 相当于**日志接口规范**，不负责打印日志
- 让代码不依赖具体日志框架，方便切换
- Spring Boot 所有日志**默认基于 SLF4J**

**日志实现（真正干活的）**

- Logback、Log4j2、JUL 都属于**实现层**



| 日志框架                    | 类型             | 特点                       | 适用场景                       | Spring Boot 是否支持 |
| --------------------------- | ---------------- | -------------------------- | ------------------------------ | -------------------- |
| **Log4j2**                  | 第三方日志实现   | 性能极高、异步强、配置灵活 | 高并发、生产环境、追求极致性能 | ✅ 官方原生支持       |
| **Logback**                 | Spring 默认      | 简单稳定、自动配置、性能好 | 绝大多数项目、新手首选         | ✅ 默认集成           |
| **SLF4J**                   | 日志门面（接口） | 统一日志 API，不单独使用   | 所有日志框架的标准接口         | ✅ 核心依赖           |
| **JUL (java.util.logging)** | JDK 自带         | 轻量、功能弱               | 简单 Java 项目、极简场景       | ✅ 支持               |
| **Reload4j**                | Log4j1 升级      | 修复安全漏洞、兼容旧版     | 老项目迁移、维护旧系统         | ✅ 支持               |

## 8 如何使用SpringBoot

SpringBoot框架的框架、底层基于Spring。能调整每⼀个场景的底层⾏为。100%项⽬⼀定会⽤到底层⾃定义

1. 理解⾃动配置原理

     导⼊starter --> ⽣效xxxxAutoConfiguration --> 组件 --> xxxProperties --> 配置 ⽂件 

2.  理解其他框架底层 a.  拦截器 

3. 可以随时定制化任何组件 a.  配置⽂件 b.  ⾃定义组件


普通开发： 导⼊ starter  ，Controller、Service、Mapper、偶尔修改配置⽂件

 ⾼级开发：⾃定义组件、⾃定义配置、⾃定义starter 核⼼：

-  这个场景⾃动配置导⼊了哪些组件，我们能不能Autowired进来使⽤ 
- 能不能通过修改配置改变组件的⼀些默认参数
-  需不需要⾃⼰完全定义这个组件
-  场景定制化



**最佳实战：**

-   选场景，导⼊到项⽬ 官⽅：starter 第三⽅：去仓库搜
-  写配置，改配置⽂件关键项 数据库参数（连接地址、账号密码...）
-  分析这个场景给我们导⼊了哪些能⽤的组件,自动装配这些组件进去后续使用
-  不满意boot提供的⾃动配好的默认组件
-  定制化 改配置 ⾃定义组件

**整合redis**

1. 选场景：  spring-boot-starter-data-redis   场景AutoConfiguration 就是这个场景的⾃动配置类 
2. 写配置：  分析到这个场景的⾃动配置类开启了哪些属性绑定关系 @EnableConfigurationProperties(RedisProperties.class)  修改redis相关的配置 
3. 分析组件：  分析到  RedisAutoConfiguration   给容器中放了  给业务代码中⾃动装配  StringRedisTemplate 
4.  定制化  修改配置⽂件 StringRedisTemplate  ⾃定义组件，⾃⼰给容器中放⼀个  StringRedisTemplate 

## 9 打包

### 1. 打包方式：jar

xml

```
<packaging>jar</packaging>
```

### 2. 必须加入 Spring Boot 打包插件（最重要）

xml

```
<build>
    <plugins>
        <!-- Spring Boot 官方打包插件，生成可执行 JAR -->
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

xml

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- 你的父依赖 -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.18 / 3.2.0</version>
        <relativePath/>
    </parent>

    <groupId>com.example</groupId>
    <artifactId>demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    
    <!-- 1. 打包方式：jar -->
    <packaging>jar</packaging>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

    <!-- 2. 必须加这个插件，才能打出可执行 JAR -->
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

```
mvn clean package -DskipTests
```

```
java -jar demo.jar
```

### 打war包

1. 打包方式改成 war

   ```
   <packaging>war</packaging>
   ```

2. 排除内嵌 Tomcat避免和外部 Tomcat 冲突

3. **添加 servlet-api 依赖**（provided）

4. **启动类继承 SpringBootServletInitializer**（必须）

#### ① Spring Boot 2.x（JDK8，javax）

xml

```
<packaging>war</packaging>

<dependencies>
    <!-- Web 依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <!-- 排除内嵌 Tomcat -->
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-tomcat</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <!-- 外部 Tomcat 需要 -->
    <dependency>
        <groupId>javax.servlet</groupId>
        <artifactId>javax.servlet-api</artifactId>
        <version>4.0.1</version>
        <scope>provided</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

------

#### ② Spring Boot 3.x（JDK17，jakarta）

xml

```
<packaging>war</packaging>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-tomcat</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <!-- 3.x 使用 jakarta -->
    <dependency>
        <groupId>jakarta.servlet</groupId>
        <artifactId>jakarta.servlet-api</artifactId>
        <scope>provided</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

------

### 启动类必须修改（继承 SpringBootServletInitializer）

```
@SpringBootApplication
public class DemoApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    // 重写 configure，支持外部 Tomcat 部署
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(DemoApplication.class);
    }
}
```

```
mvn clean package -DskipTests
```

```
target/xxx.war
```

# SpringBoot3-Web开发

pringBoot 的Web开发能⼒，由SpringMVC提供。

## WebMvcAutoConfiguration原理

### 生效条件

### 效果

###  WebMvcConfigurer接⼝

### 静态态资源规则源码

```
 1. 
规则⼀：访问： 
s/ 
下找资源.
 /webjars/** 
路径就去 
a. 
maven 导⼊依赖
b.
 2. 
规则⼆：访问： 
classpath:/META-INF/resources/webjar /** 
路径就去 
静态资源默认的四个位置找资源
 
a. classpath:/META-INF/resources/ 
b. classpath:/resources/ 
c. classpath:/static/ 
d. classpath:/public/ 
3. 
规则三：静态资源默认都有缓存规则的设置
a. 
所有缓存的设置，直接通过配置⽂件： 
spring.web 
b. 
cachePeriod： 缓存周期； 多久不⽤找服务器要新的。 默认没有，以s为单位
c. 
cacheControl： HTTP缓存控制；https://developer.mozilla.org/zh
CN/docs/Web/HTTP/Caching
 d. 
useLastModified：是否使⽤最后⼀次修改。配合HTTP Cache规则
```

###  EnableWebMvcConfiguration

### **为什么容器中放⼀个 WebMvcConfigurer  就能配置底层⾏为**

1. WebMvcAutoConfiguration 是⼀个⾃动配置类，它⾥⾯有⼀个  EnableWebMvcConfiguration 
2. EnableWebMvcConfiguration 继承与 DelegatingWebMvcConfiguration ，这两个都⽣效
3. DelegatingWebMvcConfiguration 利⽤ DI 把容器中所有WebMvcConfigurer  注⼊进来
4. 别⼈调⽤ ` DelegatingWebMvcConfiguration ` 的⽅法配置底层规则，⽽它调⽤所有 
    WebMvcConfigurer 的配置底层⽅法。

###  **WebMvcConfigurationSupport**

## Web场景

### 自动配置

**1整合web场景**

```
<dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-web</artifactId>
 </dependency>
```

**2引⼊了  autoconfigure  功能**

**3@EnableAutoConfiguration  注解使⽤@Import(AutoConfigurationImportSelector.class)批量导入组件**

**4.加载  META-INF/spring/org.springframework.boot.autoconfigure.AutoConfigura tion.imports  ⽂件中配置的所有组件**

**5.所有自动配置类**

6.绑定了配置⽂件的⼀堆配置项 

 **1、SpringMVC的所有配置  spring.mvc** 

 **2、Web场景通⽤配置  spring.web** 

 **3、⽂件上传配置  spring.servlet.multipart**

  **4、服务器的配置  server  : ⽐如：编码⽅式**



### 默认效果

默认配置

1. 包含了  ContentNegotiatingViewResolver 和  BeanNameViewResolver 组件，⽅便视图解析

2. 默认的静态资源处理机制： 静态资源放在  static ⽂件夹下即可直接访问
3. ⾃动注册了  Converter, GenericConverter, 需求 
4. ⽀持  Formatter组件，适配常⻅数据类型转换和格式化 HttpMessageConverters，可以⽅便返回 json等数据类型
5. 注册  MessageCodesResolver，⽅便国际化及错误消息处理
6.  ⽀持 静态  index.html 
7. ⾃动使⽤ ConfigurableWebBindingInitializer，实现 验等功能

## 静态资源

### 规则

**静态资源映射**

静态资源映射规则在  WebMvcAutoConfiguration 中进⾏了定义：

```
1.
 /webjars/** 的所有路径 资源都在 
classpath:/META-INF/resources/webjars/
 2.
 /** 的所有路径 资源都在 
classpath:/META-INF/resources/、
classpath:/resources/、
classpath:/static/、
classpath:/public/
 3. 
所有静态资源都定义了
⽆默认值
a.
缓存规则。【浏览器访问过⼀次，就会缓存⼀段时间】，但此功能参数
period： 缓存间隔。 默认 0S；
b.
 cacheControl：缓存控制。 默认⽆；
c.
 useLastModified：是否使⽤
lastModified头。 默认 false；
```

**静态资源缓存**

```

所有静态资源都定义了
⽆默认值
a.
缓存规则。【浏览器访问过⼀次，就会缓存⼀段时间】，但此功能参数
period： 缓存间隔。 默认 0S；
b.
 cacheControl：缓存控制。 默认⽆；
c.
 useLastModified：是否使⽤
lastModified头。 默认 false；
```

**欢迎页面**

```
. 
在静态资源⽬录下找 
index.html
 2. 
没有就在 
templates下找
index模板⻚
```

### 自定义静态资源规则

**如何配置**

```
spring.mvc 
： 静态资源访问前缀路径
spring.web 
：
●
 ●
静态资源⽬录
静态资源缓存策略
```

**代码方式配置**



## 路径匹配

以前只⽀持 AntPathMatcher 策略, 现在提供了 PathPatternParser 策略。并且可以让我们指定 到底使⽤那种策略。

**ant 语法规则**

：表示任意数量的字符。

 ?：表示任意⼀个字符。

 **：表示任意数量的⽬录。

 {}：表示⼀个命名的模式占位符。

 []：表示字符集合，例如[a-z]表示⼩写字⺟。

```
*.html 匹配任意名称，扩展名为.html的⽂件。
/folder1/*/*.java 匹配在folder1⽬录下的任意两级⽬录下的.java⽂件。
/folder2/**/*.jsp 匹配在folder2⽬录下任意⽬录深度的.jsp⽂件。
/{type}/{id}.html 匹配任意⽂件名为{id}.html，在任意命名的{type}⽬录下的⽂件。
注意：Ant ⻛格的路径模式语法中的特殊字符需要转义，如：
要匹配⽂件路径中的星号，则需要转义为\\*。
要匹配⽂件路径中的问号，则需要转义为\\?。
```

**模式切换**

AntPathMatcher 与  PathPatternParser

```
@GetMapping("/a*/b?/{p1:[a-f]+}")
 public String hello(HttpServletRequest request, 
@PathVariable("p1") String path) {
 log.info("路径变量p1： {}", path);
 //
获取请求路径
String uri = request.getRequestURI();
 return uri;
 }
```

使⽤默认的路径匹配规则，是由  PathPatternParser  提供的 

如果路径中间需要有 **，替换成ant⻛格路径

## 内容协商

⼀ 套系统适配多端数据返回

![image-20260610122503402](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20260610122503402.png)

### 多端内容适配

**适配规则**

```
SpringBoot 多端内容适配。
1.1. 基于请求头内容协商：（默认开启）
1.1.1. 客户端向服务端发送请求，携带HTTP标准的Accept请求头。
1.1.1.1. Accept: application/json 、text/xml 、text/yaml 
1.1.1.2. 服务端根据客户端请求头期望的数据类型进⾏动态返回
1.2. 基于请求参数内容协商：（需要开启）
1.2.1. 发送请求 GET /projects/spring-boot?format=json 
1.2.2. 匹配到 @GetMapping("/projects/spring-boot") 
1.2.3. 根据参数协商，优先返回 json 类型数据【需要开启参数匹配设置】
1.2.4. 发送请求 GET /projects/spring-boot?format=xml,优先返回 xml 类型数据
```

**效果测试**

引入依赖

```
<dependency>
 <groupId>com.fasterxml.jackson.dataformat</groupId>
 <artifactId>jackson-dataformat-xml</artifactId>
 </dependency>
```

标记注解

```
@JacksonXmlRootElement  // 可以写出为xml⽂档
@Data
 public class Person {
 private Long id;
 private String userName;
 private String email;
 private Integer age;
 }
```

开启基于请求参数的内容协商

```
# 开启基于请求参数的内容协商功能。 默认参数名：format。默认此功能不开启
spring.mvc.contentnegotiation.favor-parameter=true
 # 指定内容协商时使⽤的参数名。默认是format
 spring.mvc.contentnegotiation.parameter-name=type
```

 **配置协商规则与⽀持类型**

```
#
使⽤参数进⾏内容协商
spring.mvc.contentnegotiation.favor-parameter=true  
#
⾃定义参数名，默认为
format
 spring.mvc.contentnegotiation.parameter-name=myparam 
2. 
⼤多数 MediaType 都是开箱即⽤的。也可以⾃定义内容类型，如：
spring.mvc.contentnegotiation.media-types.yaml=text/yaml
```

### **自定义内容返回**

**1 增加yaml返回支持依赖**

```
<dependency>
 <groupId>com.fasterxml.jackson.dataformat</groupId>
 <artifactId>jackson-dataformat-yaml</artifactId>
 </dependency>
```

**2 对象表示yaml**

```
public static void main(String[] args) throws JsonProcessingException {
 Person person = new Person();
 person.setId(1L);
 person.setUserName("
张三
");
 person.setEmail("aaa@qq.com");
 person.setAge(18);
 YAMLFactory factory = new YAMLFactory().disable(YAMLGenerator.Feature.
 WRITE_DOC_START_MARKER);
 ObjectMapper mapper = new ObjectMapper(factory);
 String s = mapper.writeValueAsString(person);
 System.out.println(s);
 }
```

**3 编写配置**

```
#新增⼀种媒体类型
spring.mvc.contentnegotiation.media-types.yaml=text/yaml
```

增加 HttpMessageConverter  组件，专⻔负责把对象写出为yaml格式

```
@Bean
 public WebMvcConfigurer webMvcConfigurer(){
 return new WebMvcConfigurer() {
 @Override //配置⼀个能把对象转为yaml的
messageConverter
 public void configureMessageConverters(List<HttpMessageConverter<?
 >> converters) {
 converters.add(new MyYamlHttpMessageConverter());
} }; }
```

**HttpMessageConverter的示例写法**

###  内容协商原理 HttpMessageConverter



## 模板引擎

由于 SpringBoot 使⽤了嵌⼊式 Servlet 容器。所以 JSP 默认是不能使⽤的。 

如果需要服务端⻚⾯渲染，优先考虑使⽤  模板引擎

![image-20260610124356464](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20260610124356464.png)



模板引擎⻚⾯默认放在  src/main/resources/templates

springboot模板引擎自动配置

**FreeMarker** 

**Groovy** 

**Thymeleaf**

 **Mustache** 

Thymeleaf官⽹：https://www.thymeleaf.org/ 

### Thymeleaf整合

**1导入依赖**

```
<dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-thymeleaf</artifactId>
 </dependency>
```

**2⾃动配置原理** 

1.  开启了org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration  ⾃动配置

2. 属性绑定在  ThymeleafProperties 中，对应配置⽂件  spring.thymeleaf 内容 3.  所
3. 有的模板⻚⾯默认在  classpath:/templates  ⽂件夹下 
4. 默认效果 a.  所有的模板⻚⾯在  classpath:/templates/  下⾯找 b.  找后缀名为 .html  的⻚⾯

## 基础语法

1 核心用法

2 语法实例

3 属性设置

4 遍历 

5 判断

6 属性优先级

7 行内写法

8 变量选择

9 模板布局

10 devtools

## 国际化

**实现步骤**

1. Spring Boot 在类路径根下查找 messages资源绑定⽂件。⽂件名为： messages.properties 

2. 多语⾔可以定义多个消息⽂件，命名为 messages_ 区域代码 .properties  

   a. messages.properties  ：默认

    b. messages_zh_CN.properties  ：中⽂环境

    c. messages_en_US.properties  ：英语环境 

3. 在程序中可以⾃动注⼊  MessageSource  组件，获取国际化的配置项值 

4. 在⻚⾯中可以使⽤表达式  #{}  获取国际化的配置项值

## 错误机制

### 1 默认机制

错误处理的⾃动配置都在  ErrorMvcAutoConfiguration  中，两⼤核⼼机制： 

1. SpringBoot 会⾃适应处理错误，响应⻚⾯或JSON数据
2.  SpringMVC的错误处理机制依然保留，MVC处理不了，才会交给boot进⾏处理



![image-20260610125308462](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20260610125308462.png)



● 发⽣错误以后，转发给/error路径，SpringBoot在底层写好⼀个 BasicErrorController的组件，专⻔ 处理这个请求

1. 解析⼀个错误⻚

    a.  如果发⽣了500、404、503、403 这些错误 

   ​     ⅰ.  如果有模板引擎，默认在 classpath:/templates/error/ 精确码 .html

   ​      ⅱ.  如果没有模板引擎，在静态资源⽂件夹下找   精确码 .html  

​        b.  如果匹配不到 精确码 .html  这些精确的错误⻚，就去找 5xx.html  ， 4xx.html  模糊匹配 

​            ⅰ.  如果有模板引擎，默认在  classpath:/templates/error/5xx.html  

​              ⅱ.  如果没有模板引擎，在静态资源⽂件夹下找   5xx.html 

2. 如果模板引擎路径 templates  下有  error.html  ⻚⾯，就直接渲染

### 2 自定义错误响应

**1 自定义json响应机制**

```
使⽤@ControllerAdvice + @ExceptionHandler 进⾏统⼀异常处理
```

2 自定义页面响应

```
跟据boot的错误⻚⾯规则，⾃定义⻚⾯模板
```

3 最佳实战

![image-20260610125844284](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20260610125844284.png)

## 嵌入式容器

### 自动配置

Servlet 容器：管理、运⾏Servlet组件（Servlet、Filter、Listener）的环境，⼀般指服务器

**springboot默认嵌入Tomcat作为serverlet容器**



1. ServletWebServerFactoryAutoConfiguration  ⾃动配置了嵌⼊式容器场景 

2. 绑定了 ServerProperties  配置类，所有和服务器有关的配置  server  

3. ServletWebServerFactoryAutoConfiguration  导⼊了 嵌⼊式的三⼤服务器  tomcat  、 Jetty  、 Undertow  

   a.  导⼊  Tomcat  、 Jetty  、 Tomca Undertow  都有条件注解。系统中有这个类才⾏（也就是导了 包） b.  默认   Tomcat  配置⽣效。给容器中放 TomcatServletWebServerFactory

   c.  都给容器中  ServletWebServerFactory  放了⼀个 web服务器⼯⼚（造web服务器的） 

   d.  web服务器⼯⼚ 都有⼀个功能， getWebServer  获取web服务器 

   e.  TomcatServletWebServerFactory 创建了 tomcat。 

4. ServletWebServerFactory 什么时候会创建 webServer出来。

5.  ServletWebServerApplicationContext  ioc容器，启动的时候会调⽤创建web服务器

6.  Spring容器刷新（启动）的时候，会预留⼀个时机，刷新⼦容器。 

7. refresh() 容器刷新 ⼗⼆⼤步的刷新⼦容器会调⽤  onRefresh()  onRefresh()  ；



Web场景的Spring容器启动，在onRefresh的时候，会调⽤创建web服务器的⽅法。 Web服务器的创建是通过WebServerFactory搞定的。容器中⼜会根据导了什么包条件注解，启动相关 的 服务器配置，默认  EmbeddedTomcat  会给容器中放⼀个  TomcatServletWebServerFactor y  ，导致项⽬启动，⾃动创建出Tomcat。



### 自定义

```
<properties>
 <servlet-api.version>3.1.0</servlet-api.version>
 </properties>
 <dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-web</artifactId>
 <exclusions>
 <!-- Exclude the Tomcat dependency -->
 <exclusion>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-tomcat</artifactId>
 </exclusion>
 </exclusions>
 </dependency>
 <!-- Use Jetty instead -->
 <dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-jetty</artifactId>
 </dependency>
```



### 实战

修改 server  下的相关配置就可以修改服务器参数 通过给容器中放⼀个 ServletWebServerFactory  ，来禁⽤掉SpringBoot默认放的服务器⼯ ⼚，实现⾃定义嵌⼊任意服务器

##  全⾯接管SpringMVC

 SpringBoot 默认配置好了 SpringMVC 的所有常⽤特性。

 如果我们需要全⾯接管SpringMVC的所有配置并禁⽤默认配置，

仅需要编写⼀个  WebMvcConfi gurer  配置类，并标注  @EnableWebMvc 即可 全⼿动模式 ○@EnableWebMvc : 禁⽤默认配置 ○ WebMvcConfigurer  组件：定义MVC的底层⾏为

### WebMvcAutoConfiguration 到底⾃动配置了哪些规则。

###  @EnableWebMvc 禁⽤默认⾏为

 @EnableWebMvc  给容器中导⼊  DelegatingWebMvcConfiguration  组件，   他WebMvcConfigurationSupport  

2. WebMvcAutoConfiguration  有⼀个核⼼的条件注解,  @ConditionalOnMissingBean(Web MvcConfigurationSupport.class)  ，容器中没有 WebMvcConfigurationSupport  ， W ebMvcAutoConfiguration  才⽣效. 

3. @EnableWebMvc 导⼊  WebMvcConfigurationSupport  导致  WebMvcAutoConfiguratio n  失效。导致禁⽤了默认⾏为

● @EnableWebMVC 禁⽤了 Mvc的⾃动配置 

●WebMvcConfigurer 定义SpringMVC底层组件的功能类



### WebMvcConfigurer 功能

`WebMvcAutoConfiguration` 是 Spring Boot Web 的**自动配置核心**，自动装配 SpringMVC 全组件；

核心配置：**静态资源映射、视图解析器、JSON 转换器、参数解析、异常处理、文件上传**；

禁用条件：添加 `@EnableWebMvc` 或自定义 `WebMvcConfigurationSupport`；

最佳实践：用 `WebMvcConfigurer` 扩展，不破坏自动配置

**addInterceptors** → 拦截器

**addResourceHandlers** → 静态资源

**addCorsMappings** → 跨域

**addViewControllers** → 页面跳转

**configureMessageConverters** → JSON 转换

| 方法                           | 功能作用                 | 典型使用场景                                          |
| ------------------------------ | ------------------------ | ----------------------------------------------------- |
| **addInterceptors**            | **添加自定义拦截器**     | 登录校验、权限拦截、请求日志统计                      |
| **addResourceHandlers**        | **自定义静态资源映射**   | 自定义静态资源目录、映射文件上传路径、CDN 资源        |
| **addCorsMappings**            | **配置跨域 CORS**        | 解决前端后端分离跨域请求问题                          |
| **addViewControllers**         | **无业务逻辑页面跳转**   | 直接路径→视图，省去空 Controller                      |
| **configureMessageConverters** | **自定义消息转换器**     | 自定义 JSON 格式化、替换 Jackson 为 Fastjson          |
| **addFormatters**              | **添加自定义参数转换器** | String ↔ 自定义对象（如 String→枚举）、自定义日期格式 |
| **configureViewResolvers**     | **自定义视图解析器**     | 配置 Thymeleaf、FreeMarker、JSP 视图解析              |
| **addArgumentResolvers**       | **自定义参数解析器**     | 自定义注解解析参数（如 @CurrentUser 自动获取用户）    |
| **configurePathMatch**         | **路径匹配规则**         | 开启 / 关闭后缀匹配、设置路径匹配规则                 |
| **configureAsyncSupport**      | **异步请求配置**         | 配置异步请求超时时间、异步线程池                      |

## 最佳实践

## 三种方式

SpringBoot Web 开发场景 **3 种配置方式**：

1. **配置文件（简单配置）**
2. **实现 WebMvcConfigurer（推荐扩展）**
3. **@EnableWebMvc（完全接管，禁用自动配置）**

| 配置方式              | 实现形式                                                     | 特点                                                     | 适用场景                                                     |
| --------------------- | ------------------------------------------------------------ | -------------------------------------------------------- | ------------------------------------------------------------ |
| 配置文件              | application.yml / application.properties                     | 仅修改参数，**保留全部自动配置**，无需编写代码           | 简单配置：端口、静态资源路径、视图前后缀、日期格式、文件上传大小等 |
| 实现 WebMvcConfigurer | 配置类实现 `WebMvcConfigurer` 接口，加 `@Configuration`      | **保留自动配置**，仅做功能扩展，不覆盖默认规则           | 新增拦截器、跨域、自定义静态映射、视图跳转、参数转换器等常规扩展 |
| @EnableWebMvc 接管    | 配置类标注 `@EnableWebMvc`，可继承 `WebMvcConfigurationSupport` | **彻底禁用 SpringBoot MVC 自动配置**，所有组件需手动配置 | 深度定制 SpringMVC，完全自定义全套 MVC 规则（极少使用）      |

### 两种模式

前后分离模式  ：   @RestController   响应JSON数据

 前后不分离模式  ：@Controller + Thymeleaf模板引擎



## web新特性

|                | WebMvc                | WebFlux 注解版     | WebFlux 函数式版   |
| -------------- | --------------------- | ------------------ | ------------------ |
| **底层模型**   | Servlet 同步阻塞      | Reactor 异步非阻塞 | Reactor 异步非阻塞 |
| **编程风格**   | 注解式                | 注解式             | **函数式、无注解** |
| **路由方式**   | `@RequestMapping`     | `@RequestMapping`  | `RouterFunction`   |
| **请求对象**   | `HttpServletRequest`  | `ServerRequest`    | `ServerRequest`    |
| **响应对象**   | `HttpServletResponse` | `ServerResponse`   | `ServerResponse`   |
| **是否响应式** | ❌ 否                  | ✅ 是               | ✅ 是               |
| **适用场景**   | 90% 常规项目          | 微服务、高并发     | 网关、轻量化服务   |
| **能不能混用** | 独立运行              | 独立运行           | 独立运行           |

**函数式web**

函数式 Web 是 Spring 5.2+ 推出的无注解 Web 编程模型，用 RouterFunction 定义路由、HandlerFunction 处理请求，路由与业务分离，适配 WebFlux 响应式，代码更简洁、灵活、可测试。

# 整合SSM

# SpringBoot3-基础特性

# SpringBoot3 核心原理

# 场景整合

## 响应式编程

# Reactive Stack  Servlet Stack

https://docs.spring.io/spring-framework/docs/6.0.0/reference/html/web-reactive.html#webflux

Reactive Stack（响应式技术栈）是一套用于构建响应式应用的技术组合，其核心思想是基于异步、非阻塞和事件驱动的编程模型，能够更好地处理高并发、高吞吐量的场景，并提供更流畅的用户体验。以下是关于它的详细介绍

- **响应性**：系统对请求及时响应。
- **弹性**：系统在出现故障时仍能保持响应。
- **伸缩性**：系统在不同工作负载下能保持响应。
- **消息驱动**：通过异步消息传递实现组件解耦

“Servlet Stack”（Servlet 技术栈）是 Java EE（现 Jakarta EE）平台的传统 Web 开发模型，基于**Servlet API**构建，是早期 Java Web 应用的核心技术栈。它采用**同步阻塞**的编程模型，通过 Servlet 容器（如 Tomcat、Jetty）处理 HTTP 请求。以下是关于它的详细介绍：

## **一、Servlet 基础概念**

**Servlet**是 Java 中处理 Web 请求的组件，本质是实现了`javax.servlet.Servlet`接口的 Java 类。Servlet 容器（如 Tomcat）负责：



- 加载和管理 Servlet 生命周期。
- 将 HTTP 请求映射到对应的 Servlet。
- 提供线程池处理并发请求。

## **二、Servlet Stack 的核心组件**

#### **1. 核心技术**

- Servlet API

  ：

  - `HttpServlet`：处理 HTTP 请求的基类，提供`doGet()`、`doPost()`等方法。
  - `ServletContext`：代表 Web 应用的上下文，用于共享应用范围的数据。
  - `HttpSession`：管理用户会话状态。

- **JSP（JavaServer Pages）**：动态生成 HTML 的模板技术，本质是 Servlet 的语法糖。

- **Filter**：预处理请求或后处理响应（如编码过滤、权限验证）。

- **Listener**：监听 Servlet 容器中的事件（如会话创建、应用启动）。

### **2. 相关框架**

- **Spring MVC**：基于 Servlet API 构建的 Web 框架，通过`DispatcherServlet`统一处理请求。
- **Struts**：早期流行的 MVC 框架，现已逐渐被 Spring MVC 取代。
- **JSTL（JSP 标准标签库）**：简化 JSP 中的逻辑处理。

### **3. 数据库访问**

- **JDBC**：Java 数据库连接 API，用于与关系型数据库交互。
- **ORM 框架**：Hibernate、MyBatis 等，简化数据库操作。

### **4. 前端技术**

- **JSP + HTML/CSS/JavaScript**：早期主流模式，JSP 负责动态内容，前端负责静态展示。
- **AJAX**：通过 XMLHttpRequest 实现异步交互（需结合 Servlet）。

## **三、Servlet 处理请求的流程**

1. **客户端发送 HTTP 请求**到 Servlet 容器（如 Tomcat）。
2. **容器分配线程**处理请求（每个请求通常占用一个线程）。
3. **请求路由**：容器根据 URL 将请求转发到对应的 Servlet。
4. **Servlet 处理请求**：调用`doGet()`/`doPost()`等方法，处理业务逻辑。
5. **生成响应**：通过`HttpServletResponse`返回数据（如 HTML、JSON）。
6. **线程释放**：请求处理完毕后，线程返回线程池。

## **四、Servlet Stack 的特点**

#### **优势**

- **简单易用**：基于 Java EE 标准，学习曲线平缓。
- **成熟稳定**：历经多年发展，生态完善，适合中小型项目。
- **同步编程模型**：代码逻辑清晰，符合传统编程思维。

#### **局限性**

- **同步阻塞**：每个请求占用一个线程，高并发时线程资源消耗大，易导致性能瓶颈。
- **扩展性差**：难以应对海量并发（如百万级连接），需依赖硬件扩展。
- **开发效率低**：JSP 混合 Java 代码和 HTML，维护成本高；异步处理复杂（需手动管理线程）



## **五、Servlet Stack 与 Reactive Stack 的对比**

| **维度**     | **Servlet Stack（同步阻塞）**                     | **Reactive Stack（异步非阻塞）**                    |
| ------------ | ------------------------------------------------- | --------------------------------------------------- |
| **编程模型** | 同步阻塞（线程池模型）                            | 异步非阻塞（事件驱动）                              |
| **并发处理** | 线程池大小限制并发能力（如 Tomcat 默认 200 线程） | 少量线程处理大量请求（如 Netty 单线程处理万级连接） |
| **资源消耗** | 高并发时线程上下文切换开销大                      | 资源利用率高，适合 I/O 密集型场景                   |
| **异步支持** | 需要额外配置（如 Servlet 3.0 异步特性）           | 原生支持异步流（如 Spring WebFlux）                 |
| **典型场景** | 中小型 Web 应用、企业内部系统                     | 高并发 API、实时数据流处理、微服务网关              |