# Spring Boot 3 集成 Knife4j 接口文档完整教程

> 本文档记录了在 Simple Mall 商城项目（Spring Boot 3.2.0 + Java 17 + MyBatis-Plus）中集成 Knife4j 4.5.0 接口文档的完整过程。

## 一、Knife4j 简介

### 1.1 什么是 Knife4j

Knife4j 是为 Java MVC 框架集成 Swagger 生成 Api 文档的增强解决方案，前身是 swagger-bootstrap-ui。取名 Knife4j 是希望它像一把匕首一样小巧、轻量、且功能强大。

### 1.2 Knife4j vs Swagger

| 特性       | Swagger UI | Knife4j |
| ---------- | ---------- | ------- |
| 界面美观度 | 一般       | 优秀    |
| 离线文档   | 不支持     | 支持    |
| 参数缓存   | 不支持     | 支持    |
| 接口调试   | 基础       | 增强    |
| 在线搜索   | 不支持     | 支持    |
| 全局参数   | 不支持     | 支持    |
| OpenAPI 3  | 支持       | 支持    |

### 1.3 版本选择

- **Spring Boot 2.x**: 使用 `knife4j-spring-boot-starter` 3.x 版本（基于 Swagger 2）
- **Spring Boot 3.x**: 使用 `knife4j-openapi3-jakarta-spring-boot-starter` 4.x 版本（基于 OpenAPI 3 / SpringDoc）

本项目使用 Spring Boot 3.2.0，因此选择 **knife4j-openapi3-jakarta-spring-boot-starter 4.5.0**。

---

## 二、集成步骤

### 2.1 添加 Maven 依赖

在 `pom.xml` 的 `<properties>` 中定义版本号：

```xml
<properties>
    <java.version>17</java.version>
    <mybatis-plus.version>3.5.5</mybatis-plus.version>
    <knife4j.version>4.5.0</knife4j.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <maven.compiler.encoding>UTF-8</maven.compiler.encoding>
</properties>
```

在 `<dependencies>` 中添加 Knife4j 依赖：

```xml
<!-- Knife4j OpenAPI3 接口文档 -->
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
    <version>${knife4j.version}</version>
</dependency>
```

> **注意**：Spring Boot 3.x 必须使用 `jakarta` 后缀的 starter，因为 Spring Boot 3 使用了 Jakarta EE 9+ 规范（`jakarta.servlet.*` 而非 `javax.servlet.*`）。

### 2.2 创建配置类

新建 `Knife4jConfig.java` 配置类：

```java
package com.example.mall.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j 接口文档配置
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Simple Mall 简单商城 API 文档")
                        .version("1.0.0")
                        .description("基于 Vue + Spring Boot + MyBatis-Plus 的电商商城系统接口文档")
                        .contact(new Contact()
                                .name("Simple Mall")
                                .email("admin@example.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
```

### 2.3 配置 application.yml

在 `application.yml` 中添加 SpringDoc 和 Knife4j 配置：

```yaml
# SpringDoc 配置
springdoc:
  swagger-ui:
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: alpha
  api-docs:
    path: /v3/api-docs
  group-configs:
    - group: default
      paths-to-match: /**
      packages-to-scan: com.example.mall.controller

# Knife4j 增强配置
knife4j:
  enable: true
  setting:
    language: zh_cn
    enable-version: true
    enable-reload-cache-parameter: true
    enable-after-script: true
    enable-request-cache: true
    enable-host: false
    enable-host-text: localhost:8080
    enable-home-custom: false
    enable-search: false
    enable-footer: false
    enable-footer-custom: true
    footer-custom-content: Apache License 2.0 | Copyright 2026 Simple Mall
    enable-dynamic-parameter: false
    enable-debug: true
    enable-open-api: false
    enable-group: true
    enable-cors: false
    enable-document-manage: true
```

> **重要提醒**：YAML 文件中不允许出现重复的 key！之前因为 `enable-after-script` 写了两次导致启动报错 `DuplicateKeyException`。

### 2.4 给 Controller 添加 Swagger 注解

使用 OpenAPI 3 的注解为接口添加文档说明：

```java
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "用户管理", description = "用户的登录、注册、查询、修改、删除接口")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Operation(summary = "用户登录", description = "根据用户名和密码进行登录认证")
    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginDTO loginDTO) {
        // ...
    }

    @Operation(summary = "根据ID查询用户", description = "根据用户ID获取用户详细信息")
    @GetMapping("/{id}")
    public Result<User> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        // ...
    }
}
```

### 2.5 给实体类添加 Schema 注解

```java
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@TableName("user")
@Schema(description = "用户信息")
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "用户ID")
    private Long id;

    @TableField("username")
    @Schema(description = "用户名")
    private String username;

    // ...
}
```

---

## 三、常用注解说明

### 3.1 类级别注解

| 注解   | 作用                 | 示例                                           |
| ------ | -------------------- | ---------------------------------------------- |
| `@Tag` | 标记 Controller 分组 | `@Tag(name = "用户管理", description = "...")` |

### 3.2 方法级别注解

| 注解         | 作用         | 示例                                                    |
| ------------ | ------------ | ------------------------------------------------------- |
| `@Operation` | 描述接口信息 | `@Operation(summary = "用户登录", description = "...")` |

### 3.3 参数级别注解

| 注解         | 作用     | 示例                                 |
| ------------ | -------- | ------------------------------------ |
| `@Parameter` | 描述参数 | `@Parameter(description = "用户ID")` |

### 3.4 实体类注解

| 注解      | 作用          | 示例                              |
| --------- | ------------- | --------------------------------- |
| `@Schema` | 描述实体/字段 | `@Schema(description = "用户名")` |

---

## 四、访问地址

启动 Spring Boot 应用后，访问以下地址：

| 功能                | 地址                                  |
| ------------------- | ------------------------------------- |
| **Knife4j 文档 UI** | http://localhost:8080/doc.html        |
| **Swagger UI**      | http://localhost:8080/swagger-ui.html |
| **OpenAPI JSON**    | http://localhost:8080/v3/api-docs     |

> 推荐使用 Knife4j 的 `/doc.html`，界面更美观、功能更强大。

---

## 五、踩坑记录

### 5.1 YAML 重复 Key 报错

**错误信息：**

```
org.yaml.snakeyaml.constructor.DuplicateKeyException: while constructing a mapping
```

**原因：** `application.yml` 中 `knife4j.setting` 下 `enable-after-script` 配置了两次。

**解决：** 删除重复的 key，确保每个配置项只出现一次。

### 5.2 端口被占用

**错误信息：**

```
Web server failed to start. Port 8080 was already in use.
```

**解决：**

```powershell
# 查找占用 8080 端口的进程
netstat -ano | findstr ":8080" | findstr "LISTENING"

# 终止进程（替换 PID）
taskkill /PID <进程ID> /F
```

### 5.3 Spring Boot 3 版本兼容

**注意：** Spring Boot 3.x 必须使用 `knife4j-openapi3-jakarta-spring-boot-starter`，不能使用旧版的 `knife4j-spring-boot-starter` 或 `knife4j-openapi3-spring-boot-starter`。

---

## 六、项目集成效果

### 接口分组

| 分组       | Controller        | 接口数 |
| ---------- | ----------------- | ------ |
| 用户管理   | UserController    | 6 个   |
| 商品管理   | ProductController | 6 个   |
| 购物车管理 | CartController    | 5 个   |
| 订单管理   | OrderController   | 4 个   |

### 文档功能

- ✅ 接口分组展示
- ✅ 在线调试接口
- ✅ 参数说明展示
- ✅ 实体类字段说明
- ✅ 中文界面
- ✅ 接口搜索

---

## 七、总结

Knife4j 4.5.0 配合 Spring Boot 3.2.0 使用，只需：

1. **添加依赖** — `knife4j-openapi3-jakarta-spring-boot-starter`
2. **配置类** — `OpenAPI` Bean 定义文档信息
3. **YAML 配置** — SpringDoc + Knife4j 增强配置
4. **添加注解** — `@Tag`、`@Operation`、`@Parameter`、`@Schema`

整个过程零代码侵入，只需添加注解即可自动生成接口文档，非常适合前后端分离项目的接口管理。