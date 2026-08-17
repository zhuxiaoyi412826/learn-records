官方学习文档

https://doc.xiaominfo.com/

GitHub仓库 

https://github.com/xiaoymin/knife4j

**0 knf4ij文档导入流程**

1 导入xml文档

```
<!-- Knife4j OpenApi3 文档依赖 SpringBoot3专用 -->
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
    <version>4.5.0</version>
</dependency>
```

2 yml 配置文档

```
# SpringDoc 底层配置
springdoc:
  # 后端统一输出OpenAPI文档JSON的地址（对应你流程图的 /v3/api-docs）
  api-docs:
    path: /v3/api-docs
    enabled: true
  swagger-ui:
    path: /swagger-ui.html
  # 指定扫描包，只扫描controller层，加快启动扫描速度
  group-configs:
    - group: default
      paths-to-match: /**
      packages-to-scan: com.xxx.你的项目包名.controller

# Knife4j增强配置
knife4j:
  enable: true       # 开启Knife4jUI，无需额外写@Enable注解
  setting:
    language: zh_cn  # 界面强制中文
```

3 创建 OpenAPI 全局配置类

新建`config/Knife4jConfig.java`，自定义文档标题、版本、作者等全局信息：

```
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("项目后端接口文档")
                        .version("1.0.0")
                        .description("后端所有REST接口说明，支持在线调试"));
    }
}
```

4编写 Controller，添加层级注解（和表格一一对应）

按照 **类→方法→参数→实体** 四层注解规范标注代码：

5 启动页面访问 默认地址

http://localhost:8080/doc.html



注解分为 **Controller 类级别、接口方法级别、参数级别** 三层

**1knf4ij工作原理**

```
┌─────────────────────────────────────────────────────┐
│  Spring Boot 应用启动                                │
│                                                     │
│  1. 扫描所有 @RestController 类                      │
│  2. 读取 @Tag / @Operation / @Parameter 注解        │
│  3. 生成 OpenAPI 3 规范的 JSON 文档                  │
│     → /v3/api-docs                                   │
│  4. Knife4j 读取该 JSON，渲染可视化界面              │
│     → /doc.html                                      │
└─────────────────────────────────────────────────────┘
```

**2 自动化配置流程**

Knife4j 的自动配置基于 Spring Boot 的 spring-boot-autoconfigure 机制：

| 步骤            | 说明                                                         |
| --------------- | ------------------------------------------------------------ |
| 引入依赖        | pom.xml 中加入 knife4j-openapi3-jakarta-spring-boot-starter  |
| 自动装配        | Spring Boot 启动时扫描 META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports自动注册 OpenApiAutoConfiguration |
| 扫描 Controller | SpringDocConfiguration 注册 RequestMappingHandlerMapping 的拦截器，遍历所有 @RestController / @Controller 类 |
| 解析注解        | 对每个类解析 @Tag，对每个方法解析 @Operation、@Parameter、@ApiResponse |
| 生成文档        | 组装成 OpenAPI JSON，暴露在 /v3/api-docs 端点                |
| 渲染 UI         | Knife4j 前端 (JS/CSS) 请求 /v3/api-docs 拿到 JSON，渲染成 /doc.html 页面 |

**3 注解与界面显示的中英文映射**

1 controller 级别(左侧菜单分组名)

```
@RestController
@Tag(name = "前端页面", description = "前端页面入口与静态资源访问")  // ← 这两行决定显示
public class FrontendController { }
```

| 参数          | 作用                                                         |
| ------------- | ------------------------------------------------------------ |
| `name`        | **分组名称**，文档侧边栏显示的分组标题，同一 name 的接口会收拢在同一个文件夹下 |
| `description` | 分组描述，用来备注该组接口的整体用途、业务范围               |

2 接口方法级别(分组下的接口列表)

```
@GetMapping("/")
@Operation(summary = "首页入口", description = "访问根路径时返回前端 index.html 页面")
public ResponseEntity<Resource> index() { }
```

3 参数级别设置（入参文档标注）

参数分为 3 类：**URL 查询参数、路径参数、JSON 请求体参数**，对应不同注解。

```
@RequestParam(required = false) String keyword
// 或
@Parameter(description = "搜索关键词", required = false, example = "两数之和")
String keyword
```

@ Parameter 核心属性

| 属性          | 作用                                         |
| ------------- | -------------------------------------------- |
| `description` | 参数文字说明                                 |
| `required`    | 文档标注是否必填（**仅展示，不做代码校验**） |
| `example`     | 调试面板默认示例值                           |
| `hidden`      | 隐藏该参数                                   |

4 第四层：实体类 / VO 参数设置（JSON 入参、出参）

注解：`@Schema`，标注在实体类、字段上，用来描述 JSON 对象结构。

- 类上：`@Schema(description = "用户入参VO")`

- 字段上：

  ```
  @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
  ```

  - `REQUIRED`：必填
  - `NOT_REQUIRED`：非必填

| 属性                | 作用                                                         |
| ------------------- | ------------------------------------------------------------ |
| description         | 文字描述字段 / 对象含义                                      |
| requiredMode        | 必填：`REQUIRED`必填、`NOT_REQUIRED`非必填（新版推荐，替代旧 required） |
| example             | 前端参考示例值                                               |
| hidden              | true 隐藏该字段，不在文档 JSON 展示                          |
| minimum/maximum     | 数字上下限                                                   |
| minLength/maxLength | 字符串长度限制                                               |
| allowableValues     | 限定固定枚举值                                               |
| format              | 格式：date、date-time、email、uuid 等                        |

**4完整注解参数**

| 代码注解                  | Knife4j 界面位置 | 示例值                          |
| ------------------------- | ---------------- | ------------------------------- |
| @Tag(name)                | 左侧菜单分组名   | 前端页面                        |
| @Tag(description)         | 分组描述         | 前端页面入口与静态资源访问      |
| @Operation(summary)       | 接口列表标题     | 首页入口                        |
| @Operation(description)   | 接口详情说明     | 访问根路径时返回前端 index.html |
| @Parameter(description)   | 参数说明列       | 搜索关键词                      |
| @Parameter(example)       | 参数示例值       | 两数之和                        |
| @ApiResponse(description) | 响应说明         | 成功返回题目列表                |

**5 完整demo**

```
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 1.类级别：模块分组
@Tag(name = "前端页面", description = "前端页面入口与静态资源访问")
@RestController
@RequestMapping("/page")
public class PageController {

    // 2.方法级别：接口描述、响应定义
    @GetMapping("/search")
    @Operation(summary = "页面关键词搜索", description = "根据关键词检索静态页面资源")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @ApiResponse(responseCode = "500", description = "服务器异常")
    public String pageSearch(
            //3.参数级别：接收参数 + 文档标注
            @RequestParam(required = false)
            @Parameter(description = "搜索关键词", required = false, example = "两数之和")
            String keyword
    ){
        return "检索内容："+keyword;
    }
}
```

