# Spring

**2002–2006 轻量级容器时代**：打败 EJB，IoC/AOP 成型，XML 为主

**2007–2013 注解 & SSM 时代**：注解普及、JavaConfig、传统单体 Web 主流

**2014–2017 微服务萌芽**：Spring Boot 简化开发，Spring Cloud 诞生

**2018–2022 响应式微服务**：Spring5/WebFlux，Boot2+Cloud 大规模落地

**2022–至今 云原生时代**：Spring6/Boot3，JDK17、GraalVM、K8s、原生镜像

Spring 5 唯一长期支持（LTS）版本：**Spring Framework 5.3.x**

https://github.com/spring-projects/spring-framework.git  5.3X源码

现在是spring

## 什么是Spring

Spring是一款轻量级且功能强大的框架 他的优势是在简化开发和框架整合上

学习目标

(1)IOC,(2)整合Mybatis(IOC的具体应用)，(3)AOP,(4)声明式事务(AOP的具体应用)

spring架构图

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20230206123302.png)

(1)核心层 Core Container:核心容器，这个模块是Spring最核心的模块，其他的都需要依赖该模块

 (2)AOP层 AOP:面向切面编程，它依赖核心层容器，目的是在不改变原有代码的前提下对其进行功能增强 Aspects:AOP是思想,Aspects是对AOP思想的具体实现

 (3)数据层 Data Access:数据访问，Spring全家桶中有对数据访问的具体实现技术 Data Integration:数据集成，Spring支持整合其他的数据层解决方案，比如Mybatis Transactions:事务，Spring中事务管理是Spring AOP的一个具体实现，也是后期学习的 重点内容 (4)Web层 这一层的内容将在SpringMVC框架具体学习 

5)Test层 Spring主要整合了Junit来完成单元测试和集成测试

**IOC**

使用对象时，由主动new产生对象转换为由外部提供对象，此过程中对象创建控制权由程序转移到 外部，此思想称为控制反转

**IOC容器**

Spring技术对IOC思想进行了实现 Spring提供了一个容器，称为IOC容器，用来充当IOC思想中的"外部"

**Bean**

IOC容器负责对象的创建、初始化等一系列工作，其中包含了数据层和业务层的类对象 被创建或被管理的对象在IOC容器中统称为Bean IOC容器中放的就是一个个的Bean对象

**DI** 

像这种在容器中建立对象与对象之间的绑定关系就要用到DI

在容器中建立bean与bean之间的依赖关系的整个过程，称为依赖注入

applicationCentext.xml    

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd">
  
</beans>
```



## Demo

IOC   入门案例

1 引入jar包

```
<dependencies>
<dependency>
<groupId>org.springframework</groupId>
<artifactId>spring-context</artifactId>
<version>5.2.10.RELEASE</version>
</dependency>
<dependency>
<groupId>junit</groupId>
<artifactId>junit</artifactId>
<version>4.12</version>
<scope>test</scope>
</dependency>
</dependencies>
```

2 创建所需要的类

创建BookService,BookServiceImpl，BookDao和BookDaoImpl四个类

```
public interface BookDao {
public void save();
}
public class BookDaoImpl implements BookDao {
public void save() {
System.out.println("book dao save ...");
}
}
public interface BookService {
public void save();
}
public class BookServiceImpl implements BookService {
private BookDao bookDao = new BookDaoImpl();
public void save() {
System.out.println("book service save ...");
bookDao.save();
}
}
```

3 resources下添加spring配置文件applicationContext.xml，并完成bean的配置

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://www.springframework.org/schema/beans
http://www.springframework.org/schema/beans/spring-beans.xsd">
<!--bean标签标示配置bean
id属性标示给bean起名字
class属性表示给bean定义类型
-->
<bean id="bookDao" class="com.itheima.dao.impl.BookDaoImpl"/>
<bean id="bookService" class="com.itheima.service.impl.BookServiceImpl"/>

</beans>
```

4 获取IOC容器

```
ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
```

5 开始测试

```
BookService bookService = (BookService) ctx.getBean("bookService");
bookService.save();
```

DI入门案例

1.删除业务层中使用new的方式创建的dao对象

```
public class BookServiceImpl implements BookService {
//删除业务层中使用new的方式创建的dao对象
private BookDao bookDao;
public void save() {
System.out.println("book service save ...");
bookDao.save();
}
}
```

 2.在业务层提供BookDao的setter方法 

```
public class BookServiceImpl implements BookService {
//删除业务层中使用new的方式创建的dao对象
private BookDao bookDao;
public void save() {
System.out.println("book service save ...");
bookDao.save();
}
//提供对应的set方法
public void setBookDao(BookDao bookDao) {
this.bookDao = bookDao;
}
}
```

3.在配置文件中添加依赖注入的配置 

```
<bean id="bookService" class="com.itheima.service.impl.BookServiceImpl">
<!--配置server与dao的关系-->
<!--property标签表示配置当前bean的属性
name属性表示配置哪一个具体的属性
ref属性表示参照哪一个bean
-->
<property name="bookDao" ref="bookDao"/>
</bean>
```

4.运行程序调用方法

## IOC

### bean

获取bean无论是通过id还是name获取，如果无法获取到，将抛出异常 NoSuchBeanDefinitionException

#### bean的配置

1 bean基础配置, bean的别名配置, bean的作用范围配置

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20230206125252.png)

2 bean的别名

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20230206125412.png)

3 bean作用范围scope配置

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20230206130031.png)

4 bean总结

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20230206130754.png)

#### bean实例化

**1 构造方法实例化**



**2 静态工厂实例化**

创建工厂类

```
public class OrderDaoFactory {
public static OrderDao getOrderDao(){
return new OrderDaoImpl();
}
}
```

class:工厂类的类全名 factory-mehod:具体工厂类中创建对象的方法名

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20230206132118.png)

在工厂的静态方法中，我们除了new对象还可以做其他的一些业务操作

```
public class OrderDaoFactory {
public static OrderDao getOrderDao(){
System.out.println("factory setup....");//模拟必要的业务操作
return new OrderDaoImpl();
}
}
```

**3** **实例工厂与FactoryBean**

1 创建工厂类

```
public class UserDaoFactory {
public UserDao getUserDao(){
return new UserDaoImpl();
}
}

```

2 编写配置文件

```
<bean id="userFactory" class="com.itheima.factory.UserDaoFactory"/>
<bean id="userDao" factory-method="getUserDao" factory-bean="userFactory"/>
```

factory-bean:工厂的实例对象 factory-method:工厂对象中的具体创建对象的方法名,对应关系如下

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20230206133003.png)

3 运行测试

**4** **FactoryBean**

以Spring为了简化这种配置方 式就提供了一种叫FactoryBean的方式来简化开发

Factory提供了3个方法

```
方法一:getObject()，被重写后，在方法中进行对象的创建并返回
方法二:getObjectType(),被重写后，主要返回的是被创建类的Class对象
方法三:没有被重写，因为它已经给了默认值，从方法名中可以看出其作用是设置对象是否为单例，默
认true，从意思上来看，我们猜想默认应该是单例，如何来验证呢?

```

1 创建UserDaoFactoryBean

```
public class UserDaoFactoryBean implements FactoryBean<UserDao> {
//代替原始实例工厂中创建对象的方法
public UserDao getObject() throws Exception {
return new UserDaoImpl();
}
//返回所创建类的Class对象
public Class<?> getObjectType() {
return UserDao.class;
}
}

```

2 在spring的配置文件进行配置

```
 <bean id="userDao" class="com.itheima.factory.UserDaoFactoryBean"/>
```

3 测试

#### bean的生命周期

bean对象从创建到销毁的整体过程

1 在BooDaoImpl类中添加 init和destory方法

```
//表示bean初始化对应的操作
public void init(){
System.out.println("init...");
}
//表示bean销毁前对应的操作
public void destory(){
System.out.println("destory...");
}
```

2 添加配置文件

```
<bean id="bookDao" class="com.itheima.dao.impl.BookDaoImpl" init-method="init"
destroy-method="destory"/>
```

3 添加关闭方法

ApplicationContext中没有close方法 需要将ApplicationContext更换成ClassPathXmlApplicationContext 

调用ctx的close()方法 运行程序，就能执行destroy方法的内容

```
ctx.close();
```

4 测试

**总结**

**1** bean的生命周期控制

 在配置文件中的bean标签中添加init-method和destroy-method属性 

类实现InitializingBean与DisposableBean接口，这种方式了解下即可。

**2** ben的生命周期

 初始化容器 

  1.创建对象(内存分配) 

  2.执行构造方法 

  3.执行属性注入(set操作) 

  4.执行bean初始化方法 

**3** 使用bean 

  1.执行业务操作 

**4** 关闭/销毁容器 

  1.执行bean销毁方法 关闭容器的两种方式: ConfigurableApplicationContext是ApplicationContext的子类 close()方法 registerShutdownHook()方法

### DI

向一个类中传入数据

1 普通方法 set   2  构造方法

数据类型

引用类型 简单数据类型 

spring提供了两种注入方法

setter注入 简单类型 引用类型 

构造器注入 简单类型 引用类型

#### 1 setter注入

<!--property标签表示配置当前bean的属性
name属性表示配置哪一个具体的属性
ref属性表示参照哪一个bean
-->

注入引用数据类型

```
<bean id="bookService" class="com.itheima.service.impl.BookServiceImpl">
<property name="bookDao" ref="bookDao"/>
</bean>
```

注入简单数据类型

1 在BookDaoImpl类中声明对应的简单数据类型的属性,并提供对应的setter方法

```
private String databaseName;
private int connectionNum;
```

2 applicationContext.xml配置文件中使用property标签注入

```
<bean id="bookDao" class="com.itheima.dao.impl.BookDaoImpl">
<property name="databaseName" value="mysql"/>
<property name="connectionNum" value="10"/>
</bean>
<bean id="userDao" class="com.itheima.dao.impl.UserDaoImpl"/>
<bean id="bookService" class="com.itheima.service.impl.BookServiceImpl">
<property name="bookDao" ref="bookDao"/>
<property name="userDao" ref="userDao"/>
</bean>
```

3 进行测试

对于setter注入方式的基本使用就已经介绍完了，

 对于引用数据类型使用的是 <property name="" ref=""/>

对于简单数据类型使用的是 <property name="" value=""/>

#### 2 构造器注入

引用类型注入

1 删除set方法 

2 生产构造方法

```
public BookServiceImpl(BookDao bookDao, UserDao userDao) {
    this.bookDao = bookDao;
    this.userDao = userDao;
}
```

3 配置文件 

```
<constructor-arg name="bookDao" ref="bookDao"/>
       <constructor-arg name="userDao" ref="userDao"/>
       
```

简单类型注入

1 修改BookDaoImpl类，添加构造方法

```
private String databaseName;
private int connectionNum;
public BookDaoImpl(String databaseName, int connectionNum) {
this.databaseName = databaseName;
this.connectionNum = connectionNum;
}
```

2 修改配置文件

```
<bean id="bookDao" class="com.itheima.dao.impl.BookDaoImpl">
<constructor-arg name="databaseName" value="mysql"/>
<constructor-arg name="connectionNum" value="666"/>
</bean>
<bean id="userDao" class="com.itheima.dao.impl.UserDaoImpl"/>
<bean id="bookService" class="com.itheima.service.impl.BookServiceImpl">
<constructor-arg name="bookDao" ref="bookDao"/>
<constructor-arg name="userDao" ref="userDao"/>
</bean>
```

#### 3 自动装配

1 按类型  

```
bookservice提供set方法
(1)将<property>标签删除
(2)在<bean>标签中添加autowire属性
```

需要注入属性的类中对应属性的setter方法不能省略

 被注入的对象必须要被Spring的IOC容器管理 按照类型在Spring的IOC容器中如果找到多个对象，会报NoUniqueBeanDefinitionException 

一个类型在IOC中有多个对象，还想要注入成功，这个时候就需要按照名称注入

2 按名称

```
<bean id="bookDao"和 private BookDao bookDao 的名字要相同  
```

如果按照名称去找对应的bean对象，找不到则注入Null 当某一个类型在IOC容器中有多个对象，按照名称注入只找其指定名称对应的bean对象，不会报错

总结

```
1. 自动装配用于引用类型依赖注入，不能对简单类型进行操作
2. 使用按类型装配时（byType）必须保障容器中相同类型的bean唯一，推荐使用
3. 使用按名称装配时（byName）必须保障容器中具有指定名称的bean，因变量名与配置耦合，不推
荐使用
4. 自动装配优先级低于setter注入与构造器注入，同时出现时自动装配配置失效
```

#### 4 集合注入

在bookdaoImpl 生产set方法

```
private int[] array;
    private List<String> list;
    private Set<String> set;
    private Map<String,String> map;
    private Properties properties;
    public void save() {
        System.out.println("book dao save ...");
        System.out.println("遍历数组:" + Arrays.toString(array));
        System.out.println("遍历List" + list);
        System.out.println("遍历Set" + set);
        System.out.println("遍历Map" + map);
        System.out.println("遍历Properties" + properties);

        System.out.println("book..dao..save....");
    }

```

在配置文件中

```
  <property name="array">
               <array>
                   <value>100</value>
                   <value>200</value>
                   <value>300</value>
               </array>
           </property>
           <property name="list">
               <list>
                   <value>itcast</value>
                   <value>itheima</value>
                   <value>boxuegu</value>
                   <value>chuanzhihui</value>
               </list>
           </property>
           <property name="set">
               <set>
                   <value>itcast</value>
                   <value>itheima</value>
                   <value>boxuegu</value>
                   <value>boxuegu</value>
               </set>
           </property>
           <property name="map">
               <map>
                   <entry key="country" value="china"/>
                   <entry key="province" value="henan"/>
                   <entry key="city" value="kaifeng"/>
               </map>
           </property>
           <property name="properties">
               <props>
                   <prop key="country">china</prop>
                   <prop key="province">henan</prop>
                   <prop key="city">kaifeng</prop>
               </props>
           </property>
```

### 管理第三方bean

**druid数据源**

1 引入第三方坐标

```
<dependency>
      <groupId>com.alibaba</groupId>
      <artifactId>druid</artifactId>
      <version>1.1.16</version>
    </dependency>
```

2 添加配置

```
<bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
              <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
              <property name="url" value="jdbc:mysql://localhost:3306/spring_db"/>
              <property name="username" value="root"/>
              <property name="password" value="412826zxyZXY"/>
       </bean>
```

3 测试

```
public static void main(String[] args) {
        ApplicationContext ctx = new
                ClassPathXmlApplicationContext("applicationContext.xml");
        DataSource dataSource = (DataSource) ctx.getBean("dataSource");
        System.out.println(dataSource);
    }
```

**c3p0 数据源**

1 引入pom文件

```
 <dependency>
      <groupId>c3p0</groupId>
      <artifactId>c3p0</artifactId>
      <version>0.9.1.2</version>
    </dependency>
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>5.1.47</version>
    </dependency>
```

2 修改配置文件

```
<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
<property name="driverClass" value="com.mysql.jdbc.Driver"/>
<property name="jdbcUrl" value="jdbc:mysql://localhost:3306/spring_db"/>
<property name="user" value="root"/>
<property name="password" value="root"/>
<property name="maxPoolSize" value="1000"/>
</bean>
```

**加载properties文件**

1 设置jdbc.properties

```
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://127.0.0.1:3306/spring_db
jdbc.username=root
jdbc.password=412826zxyZXY
```

2 修改配置文件

<context:property-placeholder location="jdbc.properties"/> 来价值properties文件

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd">
       <context:property-placeholder location="jdbc.properties"/>
<bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
<property name="driverClassName" value="${jdbc.driver}"/>
<property name="url" value="${jdbc.url}"/>
<property name="username" value="${jdbc.username}"/>
<property name="password" value="${jdbc.password}"/>
</bean>
</beans>
```

3 进行测试

4 读取单个

## 注解开发

### 半注解开发

1 把bean标签删除掉

```
<bean id="bookDao" class="com.itheima.dao.impl.BookDaoImpl"/>
```

2 在bookdao上添加注解  @Component("bookdao")

```
@Component("bookDao")
public class BookDaoImpl implements BookDao {
public void save() {
System.out.println("book dao save ..." );
}
}
```

:@Component注解不可以添加在接口上，因为接口是无法创建对象的。

3 在配置文件开启注解扫描

```
 <context:component-scan base-package="org.example"/>
```

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20230206171535.png)

@Component注解如果不起名称，会有一个默认值就是当前类名首字母小写，所以也可以按照名称 获取

对于@Component注解，还衍生出了其他三个注解@Controller、@Service、@Repository

方便我们后期在编写类的时候能很好的区分出这个类是属于表现层、业务层还是数据层的类

### 纯注解开发

1 创建一个SpringConfig类

```
public class SpringConfig {
}
```

2 配置类上添加@Configuration替代applicationContext注解 

```
@Configuration
public class SpringConfig {
}
```

3 在配置类上添加包扫描注解@ComponentScan替换<context:component-scan base-package=""/>

4 创建一个新的运行类AppForAnnotation

5 测试



![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20230206173241.png)



![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20230209093942.png)



### 注解开发依赖注入

在BookServiceImpl类的bookDao属性上添加**@Autowired**注解  按照类型注入

普通类型注入

@Autowired可以写在属性上，也可也写在setter方法上，最简单的处理方式是写在属性上并将 setter方法删除掉 为什么setter方法可以删除呢? 自动装配基于反射设计创建对象并通过暴力反射为私有属性进行设值 普通反射只能获取public修饰的内容 暴力反射除了获取public修饰的内容还可以获取private修改的内容 所以此处无需提供setter方法

当根据类型在容器中找到多个bean,注入参数的属性名又和容器中bean的名称不一致，这个时候该如 何解决，就需要使用到**@Qualifier**来指定注入哪个名称的bean对象

**@Value**注解 简单数据类型注入

**@PropertySource** 读取properties配置文件

**@Bean**注解的作用是将方法的返回值制作为Spring管理的一个bean对象

## AOP

AOP核心概念

目标对象(Target)：原始功能去掉共性功能对应的类产生的对象，这种对象是无法直接完成最终 工作的 

代理(Proxy)：目标对象无法直接完成工作，需要对其进行功能回填，通过原始对象的代理对象实 现

SpringAOP是在不改变原有设计(代码)的前提下对其进行增强的，它的底层采用的是代理模式实现 的，所以要对原始对象进行增强，就需要对原始对象创建代理对象，在代理对象中的方法把通知







AOP(Aspect Oriented Programming)面向切面编程，一种编程范式，

AOP中核心概念分别指的是什么? 连接点 切入点 通知 通知类 切面

(1)前面一直在强调，Spring的AOP是对一个类的方法在不进行任何修改的前提下实现增强。对于上 面的案例中BookServiceImpl中有save , update , delete和select方法,这些方法我们给起了一 个名字叫**连接点**

 (2)在BookServiceImpl的四个方法中，update和delete只有打印没有计算万次执行消耗时间， 但是在运行的时候已经有该功能，那也就是说update和delete方法都已经被增强，所以对于需要增 强的方法我们给起了一个名字叫**切入点**

 (3)执行BookServiceImpl的update和delete方法的时候都被添加了一个计算万次执行消耗时间 的功能，将这个功能抽取到一个方法中，换句话说就是存放共性功能的方法，我们给起了个名字叫**通 知**

 (4)通知是要增强的内容，会有多个，切入点是需要被增强的方法，也会有多个，那哪个切入点需要添 加哪个通知，就需要提前将它们之间的关系描述清楚，那么对于通知和切入点之间的关系描述，我们 给起了个名字叫**切面** 

(5)通知是一个方法，方法不能独立存在需要被写在一个类中，这个类我们也给起了个名字叫**通知类**

**@Repository**

### demo

1.导入坐标(pom.xml) 

```
 <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>5.2.10.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.9.4</version>
        </dependency>
```

2.制作连接点(原始操作，Dao接口与实现类) 

创建BookDao和其实现类 创建spring配置类 编写App运类

```
public interface BookDao {
public void save();
public void update();
}
@Repository
public class BookDaoImpl implements BookDao {
public void save() {
System.out.println(System.currentTimeMillis());
System.out.println("book dao save ...");
}
public void update(){
System.out.println("book dao update ...");
}
}
@Configuration
@ComponentScan("com.zxy")
@EnableAspectJAutoProxy
public class SpringConfig {


ApplicationContext ctx = new
AnnotationConfigApplicationContext(SpringConfig.class);
BookDao bookDao = ctx.getBean(BookDao.class);
bookDao.save();
```

3.制作共性功能(通知类与通知) 4.定义切入点  5.绑定切入点与通知关系(切面)

```
@Component
@Aspect
public class MyAdvice {
    @Pointcut("execution(void com.zxy.dao.BookDao.update())")
    private void pt(){}
    @Before("pt()")
    public void method() {
        System.out.println(System.currentTimeMillis());
    }
}
```









@EnableAspectJAutoproxy  在spring的配置类上开启AOP功能

@Aspect 设置当前类为AOP切面类

@Pointcut 设置切入点     void 切入点方法的全路径名

```
@Pointcut("execution(void com.zxy.dao.BookDao.update())")
```

@Before    也就是说通知会在切入点方法执行之前执行

![](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20230207143442821.png)

```
 execution(public User com.itheima.service.UserService.findById(int))
```

AOP切入点表达式  分别是语法格式、通配符和书写技巧。

execution：动作关键字，描述切入点的行为动作，例如execution表示执行到指定切入点

 public:访问修饰符,还可以是public，private等，可以省略

 User：返回值，写返回值类型 com.itheima.service：

包名，多级包使用点连接 UserService:类/接口名称

 findById：方法名 

int:参数，直接写参数的类型，多个类型用逗号隔开 异常名：方法定义中抛出指定异常，可以省略

\* :单个独立的任意符号，可以独立出现，也可以作为前缀或者后缀的匹配符出现 匹配com.itheima包下的任意包中的UserService类或接口中所有find开头的带有一个参数的 方法 

..：多个连续的任意符号，可以独立出现，常用于简化包名与参数的书写 匹配com包下的任意包中的UserService类或接口中所有名称为findById的方法

 +：专用于匹配子类类型 这个使用率较低，描述子类的，咱们做JavaEE开发，继承机会就一次，使用都很慎重，所以很少 用它。*Service+，表示所有以Service结尾的接口的子类。

通知类型

(1)前置通知，追加功能到方法执行前,类似于在代码1或者代码2添加内容 

(2)后置通知,追加功能到方法执行后,不管方法执行的过程中有没有抛出异常都会执行，类似于在代 码5添加内容

 (3)返回后通知,追加功能到方法执行后，只有方法正常执行结束后才进行,类似于在代码3添加内容， 如果方法执行抛出异常，返回后通知将不会被添加

 (4)抛出异常后通知,追加功能到方法抛出异常后，只有方法执行出异常才进行,类似于在代码4添加内 容，只有方法抛出异常后才会被添加 

(5)环绕通知,环绕通知功能比较强大，它可以追加功能到方法执行的前后，这也是比较常用的方式， 它可以实现其他四种通知类型的功能，具体是如何实现的，需要我们往下学习。



1环绕通知必须依赖形参ProceedingJoinPoint才能实现对原始方法的调用，进而实现原始方法 调用前后同时添加通知 

2. 通知中如果未使用ProceedingJoinPoint对原始方法进行调用将跳过原始方法的执行 
3. 对原始方法的调用可以不接收返回值，通知方法设置成void即可，如果接收返回值，最好设定为 Object类型 
4. 原始方法的返回值如果是void类型，通知方法的返回值类型可以设置成void,也可以设置成 Object 
5. 由于无法预知原始方法运行后是否会抛出异常，因此环绕通知方法必须要处理Throwable异常



获取切入点方法的参数，所有的通知类型都可以获取参数 JoinPoint：适用于前置、后置、返回后、抛出异常后通知 ProceedingJoinPoint：适用于环绕通知 获取切入点方法返回值，前置和抛出异常后通知是没有返回值，后置通知可有可无，所以不做研究 返回后通知 环绕通知 获取切入点方法运行异常信息，前置和返回后通知是不会有，后置通知可有可无，所以不做研究 抛出异常后通知 环绕通知

## 事务管理

spring事务属性 

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20230209095232.png)

# SpringMVC

# SSM整合

# SPringAI

# SpringSecurty















# 什么是 Spring Data JPA？

用最简单的话讲：**Spring Data JPA 是 Spring 提供的一套简化数据库操作的框架，它基于 JPA 规范，让你不用写繁琐的 JDBC/MyBatis 代码，几乎只需要定义接口，就能自动实现增删改查、分页、排序等功能。**

它的核心价值：**告别重复的数据库操作代码，极大提升开发效率**。

------

## 先搞懂两个基础概念

1. **JPA**

   全称 Java Persistence API（Java 持久化 API），**是一套规范（接口 / 标准）**，不是具体实现。

   它规定了 Java 对象和数据库表映射、操作数据库的标准规则。

   常见实现：Hibernate（Spring Data JPA 默认用它）、EclipseLink。

   

2. **Spring Data**

   Spring 的一个大家族项目，统一对各种数据源（关系型数据库、Redis、MongoDB 等）提供**简化的数据访问层**。

   Spring Data JPA 就是它针对**关系型数据库 + JPA** 的子项目。

   

------

## Spring Data JPA 核心特点

1. 无需写实现类

   

   只需要定义一个

   接口

   ，继承 JpaRepository，自动拥有全套 CRUD 方法。

2. 方法名自动生成 SQL

   

   按照规则写方法名（比如 

   ```
   findByNameAndAge
   ```

   ），框架自动生成 SQL，不用手写。

3. 支持自定义 SQL

   

   复杂查询用 

   ```
   @Query
   ```

    注解直接写 JPQL 或原生 SQL。

4. 自带分页、排序、批量操作

   

   开箱即用，不用自己封装工具类。

5. 与 Spring Boot 无缝整合

   

   极少配置，直接开发。

------

## 最直观的代码示例（Spring Boot）

### 1. 引入依赖（Maven）

xml









```
<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<!-- 数据库驱动（以MySQL为例） -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 2. 实体类（映射数据库表）

用注解标注对象和表的关系：

java



运行







```
@Entity // 标识这是一个JPA实体类
@Table(name = "t_user") // 对应数据库表名
public class User {
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增
    private Long id;
    
    private String name;
    private Integer age;
    
    // getter、setter、构造器
}
```

### 3. Dao 层接口（核心！）

**只写接口，不写实现类**：

java



运行







```
// 继承 JpaRepository<实体类, 主键类型>
public interface UserRepository extends JpaRepository<User, Long> {

    // 方法名自动生成 SQL：SELECT * FROM t_user WHERE name = ?
    User findByName(String name);
    
    // 自动生成：SELECT * FROM t_user WHERE age > ?
    List<User> findByAgeGreaterThan(Integer age);
}
```

### 4. 直接使用

java



运行







```
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void test() {
        // 1. 新增/保存
        userRepository.save(new User("张三", 20));
        
        // 2. 查询所有
        List<User> all = userRepository.findAll();
        
        // 3. 根据ID查询
        User user = userRepository.findById(1L).orElse(null);
        
        // 4. 自定义方法查询
        User zhangsan = userRepository.findByName("张三");
        
        // 5. 分页查询
        Page<User> page = userRepository.findAll(PageRequest.of(0, 10));
        
        // 6. 删除
        userRepository.deleteById(1L);
    }
}
```

------

## Spring Data JPA 解决了什么痛点？

- 不用写 `JDBC` 的 `Connection`、`PreparedStatement`、结果集封装
- 不用像 MyBatis 那样写 XML 或注解 SQL（简单查询完全不用）
- 不用自己封装分页、排序、批量操作工具
- 统一数据访问层代码风格，维护成本极低

------

## 适用场景

- 快速开发中小型项目
- 单表 / 简单关联查询居多的业务
- 不想写大量重复 SQL 的场景

**不适合**：极其复杂的多表关联查询、存储过程重度使用（这种场景更适合 MyBatis）。

------

### 总结

1. **Spring Data JPA = Spring 对 JPA 的封装 + 简化数据操作**
2. 核心：**定义接口 → 继承父接口 → 直接用方法**，零实现
3. 底层默认实现是 **Hibernate**
4. 优势：开发极快、代码极简、适合快速开发



# Micrometer

**Micrometer 是 Java 应用指标采集门面框架，类比指标界的 SLF4J；SpringBoot2+ Actuator 默认集成，用来采集业务 / 系统指标，可对接 Prometheus、Datadog 等监控系统**

# Spring Cloud Alibaba

# Spring WebFlux

1. **Spring WebFlux 底层就是 Project Reactor**，接口返回 `Mono<Resp>` / `Flux<Msg>`
2. **R2DBC 响应式数据库驱动** 返回 Mono/Flux
3. SpringBoot3 响应式栈默认就是 Reactor

**Project Reactor**