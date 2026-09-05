# springAI

Spring AI 是一个用于 AI 工程的应用框架。 其目标是将 Spring 生态系统设计原则应用于 AI 领域，如可移植性和模块化设计，并推广将 POJO 作为应用构建模块到人工智能领域的应用。

https://spring.io/projects/spring-ai 官方文档



## 功能

- 支持所有主要[的人工智能模型提供商](https://docs.spring.io/spring-ai/reference/api/index.html)，如Anthropic、OpenAI、Microsoft、亚马逊、谷歌和Ollama。支持的模型类型包括：

  - [聊天完成](https://docs.spring.io/spring-ai/reference/api/chatmodel.html)
  - [嵌入](https://docs.spring.io/spring-ai/reference/api/embeddings.html)
  - [文本转图像](https://docs.spring.io/spring-ai/reference/api/imageclient.html)
  - [音频转录](https://docs.spring.io/spring-ai/reference/api/audio/transcriptions.html)
  - [文本转语音](https://docs.spring.io/spring-ai/reference/api/audio/speech.html)
  - [调节](https://docs.spring.io/spring-ai/reference/api/index.html#api/moderation)

  ## 入门案例

  1. 创建一个带有 Spring AI OpenAI 启动启动依赖的 Spring Boot Web 应用。这个 [Spring Initializr 链接](https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.3.4&packaging=jar&jvmVersion=17&groupId=spring.ai.example&artifactId=spring-ai-demo&name=spring-ai-demo&description=Spring AI %2C getting started example%2C using Open AI&packageName=spring.ai.example.spring-ai-demo&dependencies=web,spring-ai-openai)可以帮助你启动应用程序。 （*有了 [start.spring.io](https://start.spring.io/)，你可以选择任何你想在新应用中使用的AI模型或向量存储*。）

  2. 将您的OpenAI密钥添加到：`application.properties`

     ```
     收到spring.ai.openai.api-key=<YOUR OPENAI KEY>
     ```

  3. 给你的课堂添加以下片段：`SpringAiDemoApplication`

     ```java
     收到@Bean
     public CommandLineRunner runner(ChatClient.Builder builder) {
         return args -> {
             ChatClient chatClient = builder.build();
             String response = chatClient.prompt("Tell me a joke").call().content();							
             System.out.println(response);
         };
     }
     ```

  4. 运行应用程序：

     ```console
     收到./mvnw spring-boot:run
     ```



原理

以程图展示了 Spring AI 如何处理聊天模型的配置和执行，结合启动和运行时选项

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/curl发送GET请求.png)

**版本** 

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20260607063251.png)

# 快速入门

## 0.准备

[DeepSeek 聊天 ：： 春季 AI 参考](https://docs.spring.io/spring-ai/reference/api/chat/deepseek-chat.html) 

[DeepSeek 开放平台](https://platform.deepseek.com/sign_in) 获取 API KEY



## 1. 环境准备

确保已安装：

- JDK 21+
- Maven 3.6+
- Spring AI 1.0.



## 2 安装依赖

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/依赖要求.png)

```
   <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
        </dependency>
```

## 3.创建启动类

```
package com.example.springai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiApplication.class, args);
    }

}

```

## 4.controller

```
package com.example.springai.controller;

import com.example.springai.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

/**
 * 聊天控制器类，处理与聊天相关的HTTP请求
 * 使用@RestController注解标记这是一个RESTful控制器
 * @RequestMapping("/api/chat")指定了该控制器的请求路径前缀
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    /**
     * 聊天服务接口，用于处理聊天相关的业务逻辑
     * 使用private final确保该字段不可变，通过构造函数注入
     */
    private final ChatService chatService;

    /**
     * 构造函数，使用@Autowired注解实现依赖注入
     * @param chatService 聊天服务实例
     */
/**
 * 聊天控制器的构造函数，使用依赖注入方式注入ChatService服务
 * @param chatService 聊天服务接口，用于处理聊天相关的业务逻辑
 */
    @Autowired
    public ChatController(ChatService chatService) {
    // 将注入的ChatService实例赋值给类的成员变量
        this.chatService = chatService;
    }
/**
 * 聊天接口
 * 接收用户发送的消息，调用聊天服务进行处理，并返回响应结果。
* 使用@GetMapping注解标记该方法处理GET请求，指定响应的媒体类型为JSON格式，并设置字符集为UTF-8。
 * 
 * @param message 用户输入的聊天消息内容
 * @return 聊天服务处理后的回复内容（JSON格式字符串）
 */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public String chat(@RequestParam String message) {
        return chatService.chat(message);
    }

    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8",
        produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"
    )
    public String chatPost(@RequestBody ChatRequest request) {
        return chatService.chatWithPrompt(request.getMessage());
    }

/**
 * 聊天请求的静态内部类，用于封装聊天相关的请求信息
 */
    public static class ChatRequest {
    // 存储聊天消息内容的私有成员变量
        private String message;

    /**
     * 获取聊天消息内容的方法
     * @return 返回存储的聊天消息字符串
     */
        public String getMessage() {
            return message;
        }

    /**
     * 设置聊天消息内容的方法
     * @param message 要设置的聊天消息字符串
     */
        public void setMessage(String message) {
            this.message = message;
        }
    }
}

```

## 5.service

```
package com.example.springai.service;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatModel chatModel;

    @Autowired
    public ChatService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * 聊天方法，接收用户消息并返回模型回复
     * @param message 用户输入的消息内容
     * @return 模型处理后的回复内容
     */
    public String chat(String message) {
        // 调用chatModel的call方法处理输入消息并返回结果
        return chatModel.call(message);
    }
/**
     * 与大语言模型进行对话（使用 Prompt 方式）
     * 将用户输入的消息封装为 Prompt 对象，调用大模型获取响应，并提取纯文本回复内容。
     *
     * @param message 用户输入的对话消息内容
     * @return 大语言模型生成的纯文本回复内容
     */
    public String chatWithPrompt(String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getContent();
    }
}

```

## 6.yml配置

```
spring:
  application:
    name: spring-ai-demo          # 应用名称
  ai:
    openai:
      api-key: sk-...            # DeepSeek API Key
      base-url: https://api.deepseek.com  # API 地址
      chat:
        options:
          model: deepseek-chat   # 模型名称
          temperature: 0.7       # 创造性 (0-2)
  http:
    encoding:
      charset: UTF-8             # 请求编码
      enabled: true
      force: true

server:
  port: 8080                     # 服务端口
  servlet:
    encoding:
      charset: UTF-8             # 响应编码
      enabled: true
      force: true
```

## 7.测试

启动 mvn spring-boot:run

### **方式 1：浏览器测试（推荐）**

http://localhost:8080/api/chat?message=你好

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/浏览器测试.png)

### **方式 2：PowerShell 测试**

**get**

Invoke-WebRequest -Uri "http://localhost:8080/api/chat?message=你好" -Method GET | Select-Object -ExpandProperty Content

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20260607054155.png)





 **POST 请求**

(@{ message = "讲一个笑话" } | ConvertTo-Json) | Invoke-WebRequest -Uri "http://localhost:8080/api/chat" -Method POST -ContentType "application/json; charset=utf-8" | Select-Object -ExpandProperty Content

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20260607054309.png)



### **方式 3：curl 测试**

```
curl "http://localhost:8080/api/chat?message=你好"
```

发送测试 get hi

![image-20260607052254907](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20260607052254907.png)

cmd下测试不能直接发送中文 

![image-20260607052726805](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20260607052726805.png)

## 8.启动流程

### GET 请求

**完整流程示意图**

```
用户发送请求

  ↓

浏览器/终端 → http://localhost:8080/api/chat?message=你好

  ↓

Tomcat 接收请求

  ↓

DispatcherServlet 分发

  ↓

ChatController.chat() 方法

  ↓

ChatService.chat() 方法

  ↓

ChatModel.call() 方法 (Spring AI)

  ↓

HTTP 请求 → DeepSeek API (https://api.deepseek.com)

  ↓

DeepSeek 处理并返回响应

  ↓

ChatModel 解析响应

  ↓

ChatService 返回结果

  ↓

ChatController 返回字符串

  ↓

Tomcat 发送响应

  ↓

用户收到结果
```



### **POST 请求流程**

**请求格式**

```
​```json

{

 "message": "讲一个笑话"

}

\```
```

**处理流程**

```
POST /api/chat

  ↓

ChatController.chatPost()

  ↓

@RequestBody 反序列化为 ChatRequest 对象

  ↓

ChatRequest.getMessage() 获取消息

  ↓

ChatService.chatWithPrompt()

  ↓

构建 Prompt 对象

  ↓

ChatModel.call(Prompt)

  ↓

DeepSeek API

  ↓

返回结果
```

# ChatModel

ChatModel接口作为核心，定义了与AI模型交互的基本方法。它继承自Model<Prompt, ChatResponse>，提供了两个重载的call方法：

```java
public interface ChatModel extends Model<Prompt, ChatResponse> {
    default String call(String message) {...}
    @Override
    ChatResponse call(Prompt prompt);
}
```

在ChatModel接口中，带有String参数的call()方法简化了实际的使用，避免了更复杂的Prompt和 ChatResponse类的复杂性。但是在实际应用程序中，更常见的是使用ChatResponse call()方法，该方法采用Prompt实例并返回ChatResponse。

我们使用的ChatClient底层是使用ChatModel作为属性的，在初始化ChatClient的时候可以指定ChatModel，这里我们直接看底层源码：

```java
//ChatClient（部分构造器代码）
static ChatClient create(ChatModel chatModel) {
    return create(chatModel, ObservationRegistry.NOOP);
}
```



## 简单对话

### 1 需求

用户输入设置用户消息的内容，通过SpringBoot AI封装的方法向 AI 模型发送请求，以字符串形式返回 AI 模型的响应。

### 2 编写Controller方法

```java
package com.example.springai.controller;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat1")
public class ChatDeepSeekController {
    private final ChatModel chatModel;

    @Autowired
    public ChatDeepSeekController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping(
        value = "/chat",
        produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"
    )
    public String chat(@RequestParam(value = "msg", defaultValue = "给我讲个笑话") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }
}

```

### 3 测试结果

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20260607064227.png)

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20260607065059.png)

### 4 总结

```text
ChatClient 接口提供了构建和配置聊天客户端对象的灵活性，以及发起和处理聊天请求的能力。用户可以通过 ChatClient.Builder 来定制客户端的行为，然后使用 prompt() 和 prompt(Prompt prompt) 方法设置请求规范，最后通过 call() 方法发起聊天请求。
```

## 角色预设

### 配置

设置角色为奥特曼

```
 @GetMapping(
        value = "/ai",
        produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"
    )
    public String chatAsUltraman(@RequestParam(value = "msg", defaultValue = "你是谁") String message) {
        List<Message> messages = new ArrayList<>();
        if ("你是谁".equals(message)) {
            messages.add(new SystemMessage("我是奥特曼"));
        }
        messages.add(new UserMessage(message));
        Prompt prompt = new Prompt(messages);
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }
}

```

### 测试

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20260607065749.png)

![image-20260607065853631](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20260607065853631.png)

## 流式输出

（1）非流式输出 call：等待大模型把回答结果全部生成后输出给用户；

（2）流式输出stream：逐个字符输出，一方面符合大模型生成方式的本质，另一方面当模型推理效率不是很高时，流式输出比起全部生成后再输出大大提高用户体验。

### 代码

```
    @GetMapping(
        value = "/stream",
        produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"
    )
    public Flux<String> chatStream(@RequestParam(value = "msg", defaultValue = "你好") String message) {
        List<Message> messages = new ArrayList<>();
        messages.add(new UserMessage(message));
        Prompt prompt = new Prompt(messages);
        return chatModel.stream(prompt)
                .map(response -> response.getResult().getOutput().getContent());
    }
```



### 测试

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20260607070811.png)

## **prompt**

* 提示词是引导大模型生成特定输出的输入，提示词的设计和措辞会极大地影响模型的响应结果
* Prompt 提示词是与模型交互的一种输入数据组织方式，本质上是一种复合结构的输入，在 prompt 我们是可以包含多组不同角色（System、User、Aissistant等）的信息。如何管理好 Prompt 是简化 AI 应用开发的关键环节。
* Spring AI 提供了 Prompt Template 提示词模板管理抽象，开发者可以预先定义好模板，并在运行时替换模板中的关键词。在 Spring AI 与大模型交互的过程中，处理提示词首先要创建包含动态内容占位符 {占位符} 的模板，然后，这些占位符会根据用户请求或应用程序中的其他代码进行替换。在提示词模板中，{占位符} 可以用 Map 中的变量动态替换。

### 代码

```
/**
 * 提示词模板接口
 * 接收主题、风格、字数，使用模板生成AI提示词，返回完整文章（非流式）
 */
@GetMapping(
    value = "/prompt",
    produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"
)
public String chatWithTemplate(
        // 文章主题，默认Java
        @RequestParam(value = "topic", defaultValue = "Java") String topic,
        // 写作风格，默认幽默
        @RequestParam(value = "style", defaultValue = "幽默") String style,
        // 文章长度，默认100字
        @RequestParam(value = "length", defaultValue = "100") String length) {

    // 1. 定义提示词模板，{变量} 会被动态替换
    String template = "请用{style}的风格，写一篇关于{topic}的文章，大约{length}字。";

    // 2. 创建参数Map，存放要替换的变量值
    Map<String, Object> model = new HashMap<>();
    model.put("topic", topic);      // 替换主题
    model.put("style", style);      // 替换风格
    model.put("length", length);    // 替换字数

    // 3. 封装成Spring AI的提示词模板对象
    PromptTemplate promptTemplate = new PromptTemplate(template);

    // 4. 根据模板和参数，生成最终发送给AI的完整Prompt
    Prompt prompt = promptTemplate.create(model);

    // 5. 调用AI模型（同步非流式），并返回生成的文本内容
    return chatModel.call(prompt).getResult().getOutput().getContent();
}
```



### 测试

```
# 示例1：写一篇关于Python的技术文章，200字
http://localhost:8080/api/chat1/prompt?topic=Python&style=技术&length=200

# 示例2：写一首关于春天的诗，浪漫风格，100字
http://localhost:8080/api/chat1/prompt?topic=春天&style=浪漫&length=100

# 示例3：写一个关于AI的笑话，幽默风格，50字
http://localhost:8080/api/chat1/prompt?topic=AI&style=幽默&length=50

# PowerShell测试
Invoke-WebRequest -Uri "http://localhost:8080/api/chat1/prompt?topic=Java&style=专业&length=150" -Method GET | Select-Object -ExpandProperty Content
```

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20260607072134.png)

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20260607072340.png)

![image-20260607072428596](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20260607072428596.png)