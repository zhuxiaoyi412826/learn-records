一个AI助手要知道一下内容

1 角色 他是什么领域的专家

2 任务 他要完成什么任务

3 工具 他可以调用什么工具

4 约束 应该遵循什么规则

5 输出 结果是什么样子的

# 体验

## token和token计算

## 1 快速体验

1 创建maven项目  jdk版本选择17

2 导入maven文件

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>java-ai-langchain4j</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <spring-boot.version>3.2.6</spring-boot.version>
        <knife4j.version>4.3.0</knife4j.version>
        <langchain4j.version>1.0.0-beta3</langchain4j.version>
        <mybatis-plus.version>3.5.11</mybatis-plus.version>
    </properties>
    <dependencies>
        <!-- web应用程序核心依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- 编写和运行测试用例 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!-- 前后端分离中的后端接口测试工具 -->
        <dependency>
            <groupId>com.github.xiaoymin</groupId>
            <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
            <version>${knife4j.version}</version>
        </dependency>

        <!-- 基于open-ai的langchain4j接口：ChatGPT、deepseek都是open-ai标准下的大模型 -->
        <dependency>
            <groupId>dev.langchain4j</groupId>
            <artifactId>langchain4j-open-ai</artifactId>

        </dependency>
    </dependencies>
    <dependencyManagement>
        <dependencies>
            <!--引入SpringBoot依赖管理清单-->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!--引入langchain4j依赖管理清单-->
            <dependency>
                <groupId>dev.langchain4j</groupId>
                <artifactId>langchain4j-bom</artifactId>
                <version>${langchain4j.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

        </dependencies>
    </dependencyManagement>
</project>
```

3 编写springboot启动类

```
package com.zxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class XiaoZhiApp {
    public static void main(String[] args) {
        SpringApplication.run(XiaoZhiApp.class, args);

    }
}
```

4 编写测试类

这个是测试的，可以换成你自己的来体验

```
package com.zxy;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LLMTest {
  @Test
    public void testGPTDemo() {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();
        System.out.println("GPT模型初始化完成: " + model);
        String answer = model.chat("java面试题");
        System.out.println(answer);
    }

}
```

5 启动访问测试 

访问 http://localhost:8080/doc.html 能不能正常访问

![image-20250701104312108](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20250701104312108.png)

查看控制台能不能打印出问题

![image-20250630213325956](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20250630213325956.png)

6 解决bug 问题

```
WARNING: A Java agent has been loaded dynamically (C:\Users\DELL\.m2\repository\net\bytebuddy\byte-buddy-agent\1.14.16\byte-buddy-agent-1.14.16.jar)
WARNING: If a serviceability tool is in use, please run with -XX:+EnableDynamicAgentLoading to hide this warning
WARNING: If a serviceability tool is not in use, please run with -Djdk.instrument.traceUsage for more information
WARNING: Dynamic loading of agents will be disallowed by default in a future release
Java HotSpot(TM) 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended

警告：已动态加载 Java 代理（C:\Users\DELL.m2\repository\net\bytebuddy\byte-buddy-agent\1.14.16\byte-buddy-agent-1.14.16.jar）
警告：如果正在使用可维护性工具，请使用 -XX:+EnableDynamicAgentLoading 运行以隐藏此警告
警告：如果未使用可维护性工具，请使用 -Djdk.instrument.traceUsage 运行以获取更多信息
警告：未来版本将默认禁止动态加载代理
技术背景：
Java 虚拟机（JVM）检测到项目使用了 ByteBuddy 代理库（用于动态修改字节码，如 Lombok、Spring AOP 等场景）。
由于 Java 9 + 对动态代理加载机制的安全限制，旧版本库可能触发此警告，未来版本可能默认禁止该行为。
```

7 解决不了，如果测试类解决不了，不用测试文件写

8 @PostConstruct // Bean初始化后自动执行

```
@Component // 声明为Spring组件
public class runGptDemoRunner {

    @PostConstruct // Bean初始化后自动执行
    public void runGptDemo() {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")             
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();
        System.out.println("GPT模型初始化完成: " + model);
        String answer = model.chat("请你给出一道java面试题");
        System.out.println(answer);
    }
}
```

8. springboot 配置

applicantion.yaml 配置  这样就不用硬编码了  而且api key需要设置在环境变量

![image-20250701114904153](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20250701114904153.png)

```
#langchain4j.open-ai.chat-model.base-url=https://api.deepseek.com
##langchain4j.open-ai.chat-model.api-key=${DEP_SEEK_API_KEY}
#langchain4j.open-ai.chat-model.model-name=deepseek-chat
#langchain4j.open-ai.chat-model.log-requests=true
#langchain4j.open-ai.chat-model.log-responses=true
logging.level.root=debug
```

```
/**
 * 整合SpringBoot
 */
 @Autowired
 private OpenAiChatModel openAiChatModel;
 @Test
 public void testSpringBoot() {
 //向模型提问
String answer = openAiChatModel.chat("你好");
 //输出结果
System.out.println(answer);
 }
```



## 2 体验其他大模型

**1 DeepSeek**

https://platform.deepseek.com/api_keys

![image-20250630214127050](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20250630214127050.png)

\* **`deepseek-chat` 模型指向 DeepSeek-V3-0324，** 通过指定 `model='deepseek-chat'` 调用。

\* **`deepseek-reasoner` 模型指向 DeepSeek-R1-0528，** 通过指定 `model='deepseek-reasoner'` 调用。

因为open和deepseeek使用的是同一个  OpenAiChatModel 只需要更改如下配置即可

```
langchain4j.open-ai.chat-model.base-url=https://api.deepseek.com
langchain4j.open-ai.chat-model.api-key=${DEP_SEEK_API_KEY}
langchain4j.open-ai.chat-model.model-name=deepseek-chat
```

 阿里云百炼 [大模型服务平台百炼控制台](https://bailian.console.aliyun.com/?spm=5176.29597918.0.0.3d987b08qaM1EQ&tab=model#/model-market)

**2 通义千问**

![image-20250701002254115](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20250701002254115.png)

https://bailian.console.aliyun.com/?spm=5176.29597918.J_SEsSjsNv72yRuRFS2VknO.2.22757b08h9fqa5&tab=api#/api/?type=model&url=https%3A%2F%2Fhelp.aliyun.com%2Fdocument_detail%2F2712576.html

具体调用方式

使用SDK调用时需配置的base_url：`https://dashscope.aliyuncs.com/compatible-mode/v1`

使用HTTP方式调用时需配置的endpoint：`POST https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions`

但是下载SDK了不需要配置这个base_url 也能使用

通义千问 文本

配置模型参数

```
#阿里百炼平台
langchain4j.community.dashscope.chat-model.api-key=${ALI_BABA_API_KEY}
langchain4j.community.dashscope.chat-model.model-name=qwen-max
```

pom文件

```
<!-- 接入阿里云百炼平台 -->
 <dependency>
 <groupId>dev.langchain4j</groupId>
 <artifactId>langchain4j-community-dashscope-spring-boot-starter</artifactId>
 </dependency>
 
  <!--引入百炼依赖管理清单-->
 <dependency>
 <groupId>dev.langchain4j</groupId>
 <artifactId>langchain4j-community-bom</artifactId>
 <version>${langchain4j.version}</version>
 <type>pom</type>
 <scope>import</scope>
 </dependency>

```

```
 @PostConstruct // Bean初始化后自动执行
    public void runGptDemo() {
       QwenChatModel qwenModel = QwenChatModel.builder()
                .baseUrl("https://dashscope.aliyuncs.com/api/v1")
                .apiKey()
                .modelName("qwen-plus")
                .build();
        System.out.println("GPT模型初始化完成: " + qwenModel);
        String answer = qwenModel.chat("请你给出一道java面试题");
        System.out.println(answer);
    }
}
```

```
  @Autowired
     private QwenChatModel qwenChatModel;
    @Test
    public void testDashScopeQwen() {
        //向模型提问
        String answer = qwenChatModel.chat("请你给出一道前端面试题");
        //输出结果
        System.out.println(answer);
    }

```

```
当然可以。这里有一道关于JavaScript的前端面试题，它不仅考察了应聘者对于闭包的理解，还涉及到函数作用域和变量生命周期的知识点：
**题目：**
请解释下面这段代码的执行结果，并说明原因。
​```javascript
function createFunctions() {
    var result = [];
    
    for (var i = 0; i < 10; i++) {
        result[i] = function() {
            return i;
        };
    }    
    return result;
}

var functions = createFunctions();
for (var j = 0; j < functions.length; j++) {
    console.log(functions[j]());
}
​```
**预期答案：**
这段代码会打印出十个 `10` 而不是从 `0` 到 `9` 的数字序列。这是因为所有通过循环创建的函数都共享同一个外部变量 `i`。当这些函数被调用时，它们访问的是最后一次循环迭代后 `i` 的值（即 `10`）。这是因为 JavaScript 中的变量提升特性以及闭包的作用范围导致的。在每次循环中，实际上并没有为每个函数创建独立的 `i` 副本；相反，所有的函数都引用了同一个全局变量 `i`。
**改进方案：**
为了得到正确的输出（即从 `0` 到 `9`），可以通过立即调用的函数表达式（IIFE）来为每个迭代创建一个新的作用域，从而为每个函数提供一个独立的 `i` 值副本。修改后的代码如下所示：

​```javascript
function createFunctions() {
    var result = [];
    
    for (var i = 0; i < 10; i++) {
        (function(x) {
            result.push(function() { return x; });
        })(i);
    }   
    return result;
}
var functions = createFunctions();
for (var j = 0; j < functions.length; j++) {
    console.log(functions[j]());
}
​```
在这个版本中，我们使用了一个额外的匿名函数来包裹住原有的函数定义，并将当前的 `i` 值作为参数传递给这个匿名函数。这样就确保了每一个函数都有自己的 `x` 值，而不会受到后续循环的影响。
```

**3 万象图片** 

文生图片

```
      WanxImageModel wanxImageModel = WanxImageModel.builder()
//                .baseUrl("https://dashscope.aliyuncs.com/api/v1")
                .apiKey("sk-cab858e34f5b4933914b88f435878066")
                .modelName("wanx2.1-t2i-turbo")
                .build();
        Response<Image> response = wanxImageModel.generate("奇幻森林精灵：在一片弥漫着轻柔薄雾的 古老森林深处，阳光透过茂密枝叶洒下金色光斑。一位身材娇小、长着透明薄翼的精灵少女站在一朵硕大的蘑菇上。她 有着海藻般的绿色长发，发间点缀着蓝色的小花，皮肤泛着珍珠般的微光。身上穿着由翠绿树叶和白色藤蔓编织而成的 连衣裙，手中捧着一颗散发着柔和光芒的水晶球，周围环绕着五彩斑斓的蝴蝶，脚下是铺满苔藓的地面，蘑菇和蕨类植 物丛生，营造出神秘而梦幻的氛围。");
                System.out.println(response.content().url());
```

```
https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/1d/40/20250701/7557d0da/917c4cb7-7cf2-479f-9965-0fab51239f1d4139439501.png?Expires=1751426699&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=ZOinFUOLr4DM4u2qu3rasKodJ0E%3D
```

![b1eafd87-3947-451d-ad20-8f9b6fa09c1e1604096052](D:\edge下载\b1eafd87-3947-451d-ad20-8f9b6fa09c1e1604096052.png)

4 语音模型

5 视频模型

## 实战

DeepSeek 简单调用实战

![image-20250701225230437](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20250701225230437.png)

```
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DeepSeek AI 对话助手</title>
    <!-- 引入 marked.js 用于 Markdown 渲染 -->
    <script src="https://cdn.jsdelivr.net/npm/marked@9.1.6/marked.min.js"></script>
    <!-- 备用 CDN -->
    <script>
        // 检查 marked 是否加载成功，如果失败则使用备用 CDN
        if (typeof marked === 'undefined') {
            document.write('<script src="https://unpkg.com/marked@9.1.6/marked.min.js"><\/script>');
        }
    </script>
    <!-- 引入 highlight.js 用于代码高亮 -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/default.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js"></script>
    <!-- 备用 highlight.js CDN -->
    <script>
        // 检查 hljs 是否加载成功，如果失败则使用备用 CDN
        if (typeof hljs === 'undefined') {
            document.write('<link rel="stylesheet" href="https://unpkg.com/@highlightjs/cdn-assets@11.9.0/styles/default.min.css">');
            document.write('<script src="https://unpkg.com/@highlightjs/cdn-assets@11.9.0/highlight.min.js"><\/script>');
        }
    </script>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .chat-container {
            width: 90%;
            max-width: 800px;
            height: 80vh;
            background: rgba(255, 255, 255, 0.95);
            border-radius: 20px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
            display: flex;
            flex-direction: column;
            overflow: hidden;
            backdrop-filter: blur(10px);
        }

        .chat-header {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
            padding: 20px;
            text-align: center;
            font-size: 24px;
            font-weight: bold;
            text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
        }

        .chat-messages {
            flex: 1;
            padding: 20px;
            overflow-y: auto;
            background: #f8f9fa;
        }

        .message {
            margin-bottom: 15px;
            display: flex;
            align-items: flex-start;
        }

        .message.user {
            justify-content: flex-end;
        }

        .message-content {
            max-width: 70%;
            padding: 12px 18px;
            border-radius: 18px;
            word-wrap: break-word;
            line-height: 1.4;
        }

        .message.user .message-content {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-bottom-right-radius: 5px;
        }

        .message.ai .message-content {
            background: white;
            color: #333;
            border: 1px solid #e0e0e0;
            border-bottom-left-radius: 5px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }

        .message-avatar {
            width: 35px;
            height: 35px;
            border-radius: 50%;
            margin: 0 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            color: white;
            font-size: 14px;
        }

        .message.user .message-avatar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            order: 2;
        }

        .message.ai .message-avatar {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        .chat-input {
            padding: 20px;
            background: white;
            border-top: 1px solid #e0e0e0;
        }

        .input-container {
            display: flex;
            gap: 10px;
            align-items: center;
        }

        .input-field {
            flex: 1;
            padding: 12px 18px;
            border: 2px solid #e0e0e0;
            border-radius: 25px;
            font-size: 16px;
            outline: none;
            transition: border-color 0.3s ease;
        }

        .input-field:focus {
            border-color: #667eea;
        }

        .send-button {
            padding: 12px 24px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 25px;
            cursor: pointer;
            font-size: 16px;
            font-weight: bold;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }

        .send-button:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }

        .send-button:disabled {
            opacity: 0.6;
            cursor: not-allowed;
            transform: none;
        }

        .loading {
            display: none;
            text-align: center;
            padding: 10px;
            color: #666;
        }

        .loading.show {
            display: block;
        }

        .typing-indicator {
            display: none;
            padding: 10px 18px;
            background: white;
            border-radius: 18px;
            border-bottom-left-radius: 5px;
            margin-bottom: 15px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
            max-width: 70%;
        }

        .typing-indicator.show {
            display: flex;
        }

        .typing-dots {
            display: flex;
            gap: 4px;
        }

        .typing-dot {
            width: 8px;
            height: 8px;
            background: #666;
            border-radius: 50%;
            animation: typing 1.4s infinite ease-in-out;
        }

        .typing-dot:nth-child(1) { animation-delay: -0.32s; }
        .typing-dot:nth-child(2) { animation-delay: -0.16s; }
        .typing-dot:nth-child(3) { animation-delay: 0s; }

        @keyframes typing {
            0%, 80%, 100% {
                transform: scale(0.8);
                opacity: 0.5;
            }
            40% {
                transform: scale(1);
                opacity: 1;
            }
        }

        /* 流式输出打字光标样式 */
        .typing-cursor {
            color: #007bff;
            animation: blink 1s infinite;
            font-weight: bold;
        }

        @keyframes blink {
            0%, 50% {
                opacity: 1;
            }
            51%, 100% {
                opacity: 0;
            }
        }

        /* Markdown 样式 */
        .message-content h1, .message-content h2, .message-content h3,
        .message-content h4, .message-content h5, .message-content h6 {
            margin: 16px 0 8px 0;
            font-weight: bold;
            line-height: 1.4;
        }
        
        .message-content h1 { font-size: 1.5em; }
        .message-content h2 { font-size: 1.3em; }
        .message-content h3 { font-size: 1.1em; }
        
        .message-content p {
            margin: 8px 0;
            line-height: 1.6;
        }
        
        .message-content ul, .message-content ol {
            margin: 8px 0;
            padding-left: 20px;
        }
        
        .message-content li {
            margin: 4px 0;
            line-height: 1.5;
        }
        
        .message-content blockquote {
            margin: 12px 0;
            padding: 8px 16px;
            border-left: 4px solid #ddd;
            background-color: #f9f9f9;
            font-style: italic;
        }
        
        .message-content code {
            background-color: #f1f1f1;
            padding: 2px 4px;
            border-radius: 3px;
            font-family: 'Courier New', monospace;
            font-size: 0.9em;
        }
        
        .message-content pre {
            background-color: #f8f8f8;
            border: 1px solid #e1e1e1;
            border-radius: 6px;
            padding: 12px;
            margin: 12px 0;
            overflow-x: auto;
            font-family: 'Courier New', monospace;
            font-size: 0.9em;
            line-height: 1.4;
        }
        
        .message-content pre code {
            background: none;
            padding: 0;
            border-radius: 0;
        }
        
        .message-content strong {
            font-weight: bold;
        }
        
        .message-content em {
            font-style: italic;
        }
        
        .message-content table {
            border-collapse: collapse;
            margin: 12px 0;
            width: 100%;
        }
        
        .message-content th, .message-content td {
            border: 1px solid #ddd;
            padding: 8px 12px;
            text-align: left;
        }
        
        .message-content th {
            background-color: #f5f5f5;
            font-weight: bold;
        }

        /* 滚动条样式 */
        .chat-messages::-webkit-scrollbar {
            width: 6px;
        }

        .chat-messages::-webkit-scrollbar-track {
            background: #f1f1f1;
            border-radius: 3px;
        }

        .chat-messages::-webkit-scrollbar-thumb {
            background: #c1c1c1;
            border-radius: 3px;
        }

        .chat-messages::-webkit-scrollbar-thumb:hover {
            background: #a8a8a8;
        }

        /* 响应式设计 */
        @media (max-width: 768px) {
            .chat-container {
                width: 95%;
                height: 90vh;
                border-radius: 15px;
            }

            .chat-header {
                font-size: 20px;
                padding: 15px;
            }

            .message-content {
                max-width: 85%;
            }

            .input-field {
                font-size: 14px;
            }

            .send-button {
                font-size: 14px;
                padding: 10px 20px;
            }
        }
    </style>
</head>
<body>
    <div class="chat-container">
        <div class="chat-header">
            🤖 DeepSeek AI 对话助手
        </div>
        
        <div class="chat-messages" id="chatMessages">
            <div class="message ai">
                <div class="message-avatar">AI</div>
                <div class="message-content">
                    你好！我是 DeepSeek AI 助手，有什么可以帮助你的吗？
                </div>
            </div>
        </div>
        
        <div class="typing-indicator" id="typingIndicator">
            <div class="typing-dots">
                <div class="typing-dot"></div>
                <div class="typing-dot"></div>
                <div class="typing-dot"></div>
            </div>
        </div>
        
        <div class="chat-input">
            <div class="input-container">
                <input type="text" class="input-field" id="messageInput" placeholder="请输入你的问题..." maxlength="1000">
                <button class="send-button" id="sendButton" onclick="sendMessage()">发送</button>
            </div>
        </div>
    </div>

    <script>
        // 开发模式控制（生产环境设为 false）
        const DEBUG_MODE = false;
        
        // 全局变量：对话历史管理
        let conversationHistory = [
            {
                role: 'system',
                content: '你是一个有用的AI助手，请用中文回答问题。你的回答支持Markdown格式，可以使用代码块、列表、粗体、斜体等格式来美化回复内容。'
            }
        ];
        const MAX_HISTORY_TURNS = 10; // 最大保持10轮对话
        
        /**
         * 添加消息到对话历史
         * @param {string} role - 角色 ('user' 或 'assistant')
         * @param {string} content - 消息内容
         */
        function addToHistory(role, content) {
            conversationHistory.push({ role, content });
            
            // 保持最大轮数限制（系统消息 + 用户消息 + 助手回复 = 2条/轮）
            // 保留系统消息，只限制用户和助手的对话轮数
            const systemMessages = conversationHistory.filter(msg => msg.role === 'system');
            const userAssistantMessages = conversationHistory.filter(msg => msg.role !== 'system');
            
            if (userAssistantMessages.length > MAX_HISTORY_TURNS * 2) {
                // 移除最早的一轮对话（用户消息 + 助手回复）
                userAssistantMessages.splice(0, 2);
                conversationHistory = [...systemMessages, ...userAssistantMessages];
            }
        }
        
        /**
         * 获取当前对话历史
         * @returns {Array} 对话历史数组
         */
        function getConversationHistory() {
            return conversationHistory;
        }
        
        /**
         * 安全的 Markdown 渲染函数
         * @param {string} text - 要渲染的文本
         * @returns {string} 渲染后的 HTML 或原始文本
         */
        function safeMarkdownRender(text) {
            try {
                if (typeof marked !== 'undefined' && marked.parse) {
                    return marked.parse(text);
                } else {
                    console.warn('marked.js 未加载，使用纯文本显示');
                    return text.replace(/\n/g, '<br>');
                }
            } catch (error) {
                console.error('Markdown 渲染失败:', error);
                return text.replace(/\n/g, '<br>');
            }
        }
        
        /**
         * 安全的代码高亮函数
         * @param {HTMLElement} element - 包含代码的元素
         */
        function safeHighlightCode(element) {
            try {
                if (typeof hljs !== 'undefined' && hljs.highlightElement) {
                    element.querySelectorAll('pre code').forEach((block) => {
                        hljs.highlightElement(block);
                    });
                }
            } catch (error) {
                console.warn('代码高亮失败:', error);
            }
        }
        
        /**
         * 发送消息到DeepSeek AI (支持流式输出)
         * @description 处理用户输入，调用 DeepSeek API，并实时显示 AI 回复
         * @async
         * @function sendMessage
         * @returns {Promise<void>} 无返回值
         * @throws {Error} 当 API 调用失败时抛出错误
         */
        async function sendMessage() {
            const input = document.getElementById('messageInput');
            const message = input.value.trim();
            
            if (!message) return;
            
            // 添加用户消息到聊天界面和历史记录
            addMessage(message, 'user');
            addToHistory('user', message);
            input.value = '';
            
            // 显示加载状态
            showTypingIndicator();
            disableSendButton();
            
            // 创建AI消息容器用于流式输出
            const aiMessageElement = addStreamingMessage('ai');
            
            try {
                // 隐藏加载状态，开始流式输出
                hideTypingIndicator();
                
                let fullResponse = '';
                
                // 调用DeepSeek API，传入流式更新回调
                await callDeepSeekAPI((chunk) => {
                    fullResponse += chunk;
                    updateStreamingMessage(aiMessageElement, chunk);
                });
                
                // 流式输出完成，移除打字光标并添加到历史记录
                finishStreamingMessage(aiMessageElement);
                addToHistory('assistant', fullResponse);
                // 开发调试信息
                if (DEBUG_MODE) {
                    console.log('流式输出完成，完整回复:', fullResponse);
                }
                
            } catch (error) {
                hideTypingIndicator();
                // 如果流式输出过程中出错，更新消息内容为错误信息
                if (aiMessageElement) {
                    const contentElement = aiMessageElement.querySelector('.message-content');
                    if (contentElement) {
                        contentElement.textContent = '抱歉，发生了错误：' + error.message;
                    }
                } else {
                    addMessage('抱歉，发生了错误：' + error.message, 'ai');
                }
                console.error('API调用错误:', error);
            } finally {
                enableSendButton();
            }
        }
        
        /**
         * 调用DeepSeek API进行流式对话
         * @description 使用 Server-Sent Events (SSE) 实现流式输出，实时接收 AI 回复
         * @async
         * @function callDeepSeekAPI
         * @param {Function} onChunk - 处理每个数据块的回调函数，接收累积的完整内容
         * @returns {Promise<string>} 完整的回复内容
         * @throws {Error} 当 API 调用失败或网络错误时抛出错误
         * @example
         * // 调用示例
         * const response = await callDeepSeekAPI((content) => {
         *     updateStreamingMessage(messageElement, content);
         * });
         */
        async function callDeepSeekAPI(onChunk) {
            // 请将下面的 YOUR_DEEPSEEK_API_KEY_HERE 替换为你的实际 DeepSeek API 密钥
            const apiKey = 'sk-1f67fdca0f824542934475224467f00f';
            
            const response = await fetch('https://api.deepseek.com/chat/completions', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${apiKey}`
                },
                body: JSON.stringify({
                    model: 'deepseek-chat',
                    messages: getConversationHistory(), // 使用完整的对话历史
                    max_tokens: 1000,
                    temperature: 0.7,
                    stream: true  // 启用流式输出
                })
            });
            
            if (!response.ok) {
                throw new Error(`API请求失败: ${response.status} ${response.statusText}`);
            }
            
            const reader = response.body.getReader();
            const decoder = new TextDecoder();
            let fullContent = '';
            
            try {
                while (true) {
                    const { done, value } = await reader.read();
                    
                    if (done) break;
                    
                    const chunk = decoder.decode(value, { stream: true });
                    const lines = chunk.split('\n');
                    
                    for (const line of lines) {
                        if (line.startsWith('data: ')) {
                            const data = line.slice(6).trim();
                            
                            if (data === '[DONE]') {
                                return fullContent;
                            }
                            
                            try {
                                const parsed = JSON.parse(data);
                                const content = parsed.choices?.[0]?.delta?.content;
                                
                                if (content) {
                                    fullContent += content;
                                    if (onChunk) {
                                        onChunk(fullContent); // 传递完整内容而不是单个块
                                    }
                                }
                            } catch (e) {
                                // 忽略解析错误的数据块
                                if (DEBUG_MODE) {
                                    console.warn('解析SSE数据失败:', e);
                                }
                            }
                        }
                    }
                }
            } finally {
                reader.releaseLock();
            }
            
            return fullContent;
        }
        
        /**
         * 添加消息到聊天界面
         * @param {string} message - 消息内容
         * @param {string} type - 消息类型 ('user' 或 'ai')
         */
        function addMessage(message, type) {
            const chatMessages = document.getElementById('chatMessages');
            const messageDiv = document.createElement('div');
            messageDiv.className = `message ${type}`;
            
            const avatar = document.createElement('div');
            avatar.className = 'message-avatar';
            avatar.textContent = type === 'user' ? '我' : 'AI';
            
            const content = document.createElement('div');
            content.className = 'message-content';
            
            if (type === 'ai') {
                // AI 消息支持 Markdown 渲染
                content.innerHTML = safeMarkdownRender(message);
                // 代码高亮
                safeHighlightCode(content);
            } else {
                // 用户消息保持纯文本
                content.textContent = message;
            }
            
            messageDiv.appendChild(avatar);
            messageDiv.appendChild(content);
            chatMessages.appendChild(messageDiv);
            
            // 滚动到底部
            chatMessages.scrollTop = chatMessages.scrollHeight;
        }
        
        /**
         * 创建流式输出消息容器
         * @param {string} type - 消息类型 ('user' 或 'ai')
         * @returns {HTMLElement} 消息容器元素
         */
        function addStreamingMessage(type) {
            const chatMessages = document.getElementById('chatMessages');
            const messageDiv = document.createElement('div');
            messageDiv.className = `message ${type}`;
            
            const avatar = document.createElement('div');
            avatar.className = 'message-avatar';
            avatar.textContent = type === 'user' ? '我' : 'AI';
            
            const content = document.createElement('div');
            content.className = 'message-content';
            content.textContent = ''; // 初始为空，等待流式内容
            
            // 添加打字光标效果
            const cursor = document.createElement('span');
            cursor.className = 'typing-cursor';
            cursor.textContent = '▋';
            content.appendChild(cursor);
            
            messageDiv.appendChild(avatar);
            messageDiv.appendChild(content);
            chatMessages.appendChild(messageDiv);
            
            // 滚动到底部
            chatMessages.scrollTop = chatMessages.scrollHeight;
            
            return messageDiv;
        }
        
        /**
         * 更新流式输出消息内容（支持 Markdown 渲染）
         * @param {HTMLElement} messageElement - 消息容器元素
         * @param {string} fullContent - 完整的内容（用于 Markdown 渲染）
         */
        function updateStreamingMessage(messageElement, fullContent) {
            const contentElement = messageElement.querySelector('.message-content');
            
            // 渲染 Markdown 内容并添加打字光标
            const renderedContent = safeMarkdownRender(fullContent);
            contentElement.innerHTML = renderedContent + '<span class="typing-cursor">▋</span>';
            
            // 代码高亮
            safeHighlightCode(contentElement);
            
            // 滚动到底部
            const chatMessages = document.getElementById('chatMessages');
            chatMessages.scrollTop = chatMessages.scrollHeight;
        }
        
        /**
         * 完成流式输出，移除打字光标并最终渲染 Markdown
         * @param {HTMLElement} messageElement - 消息容器元素
         */
        function finishStreamingMessage(messageElement) {
            const contentElement = messageElement.querySelector('.message-content');
            const cursor = contentElement.querySelector('.typing-cursor');
            
            if (cursor) {
                cursor.remove();
            }
            
            // 确保代码高亮正确应用
            safeHighlightCode(contentElement);
        }
        
        /**
         * 显示打字指示器
         */
        function showTypingIndicator() {
            const indicator = document.getElementById('typingIndicator');
            indicator.classList.add('show');
            
            // 滚动到底部
            const chatMessages = document.getElementById('chatMessages');
            chatMessages.scrollTop = chatMessages.scrollHeight;
        }
        
        /**
         * 隐藏打字指示器
         */
        function hideTypingIndicator() {
            const indicator = document.getElementById('typingIndicator');
            indicator.classList.remove('show');
        }
        
        /**
         * 禁用发送按钮
         */
        function disableSendButton() {
            const button = document.getElementById('sendButton');
            button.disabled = true;
            button.textContent = '发送中...';
        }
        
        /**
         * 启用发送按钮
         */
        function enableSendButton() {
            const button = document.getElementById('sendButton');
            button.disabled = false;
            button.textContent = '发送';
        }
        
        // 监听回车键发送消息
        document.getElementById('messageInput').addEventListener('keypress', function(e) {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                sendMessage();
            }
        });
        
        // 页面加载完成后聚焦输入框
        window.addEventListener('load', function() {
            document.getElementById('messageInput').focus();
        });
    </script>
</body>
</html>
```

# AIService

AIService使用面向接口和动态代理的方式完成程序的编写，更灵活的实现高级功能。

AIService可处理最常见的操作： 为大语言模型格式化输入内容 解析大语言模型的输出结果 

它们还支持更高级的功能：

 聊天记忆 Chat memory 

工具 Tools 

检索增强生成 RAG

## 创建AIService

1. 引入依赖

```
<!--langchain4j高级功能-->
 <dependency>
 <groupId>dev.langchain4j</groupId>
 <artifactId>langchain4j-spring-boot-starter</artifactId>
 </dependency>
```

2. 创建接口

```
 public interface Assistant {
 String chat(String userMessage);
 }

```

3. 测试用例 

   测试类似加上 

   ```
   @Component
   @SpringBootTest
   ```

```
@SpringBootTest
 public class AIServiceTest {
 @Autowired
 private QwenChatModel qwenChatModel;
 }
 @Test
 public void testChat() {
 //创建AIService
 Assistant assistant = AiServices.create(Assistant.class, qwenChatModel);
 //调用service的接口
String answer = assistant.chat("Hello");
 System.out.println(answer);
 }
```

4@AiService

使用springboot可以通过注入的方式

```
 //因为我们在配置文件中同时配置了多个大语言模型，所以需要在这里明确指定（EXPLICIT）模型的beanName
（qwenChatModel） 在接口上添加注解
@AiService(wiringMode = EXPLICIT, chatModel = "qwenChatModel")
```

测试用例中，我们可以直接注入Assistant对象

```
@Autowired
 private Assistant assistant;
 @Test
 public void testAssistant() {
 }
 String answer = assistant.chat("Hello");
 System.out.println(answer);
```

![image-20250707150924724](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20250707150924724.png)

5  工作原理

 AiServices会组装Assistant接口以及其他组件，并使用反射机制创建一个实现Assistant接口的代理对象。 这个代理对象会处理输入和输出的所有转换工作。在这个例子中，chat方法的输入是一个字符串，但是大 模型需要一个 UserMessage 对象。所以，代理对象将这个字符串转换为 UserMessage ，并调用聊天语 言模型。chat方法的输出类型也是字符串，但是大模型返回的是  AiMessage 对象，代理对象会将其转换 为字符串。

#  Chat memory

1. **测试模型是否有记忆功能**

```
  String answer = qwenChatModel.chat("我就AAA我们开始聊天吧");
        //输出结果
        System.out.println(answer);
        String answer1 = qwenChatModel.chat("我的名字是什么");
        //输出结果
        System.out.println(answer1);
```

![image-20250707144959732](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20250707144959732.png)

可以得出他没有记忆功能

2. **通过把二次的提问和第一次的提问和回答进行封装**

   测试类上加上 

   ```
   @Component
   @SpringBootTest
   ```

```
@Autowired
 private QwenChatModel qwenChatModel;
 @Test
 public void testChatMemory2() {
 //第一轮对话
UserMessage userMessage1 = UserMessage.userMessage("我是环环");
 ChatResponse chatResponse1 = qwenChatModel.chat(userMessage1);
 AiMessage aiMessage1 = chatResponse1.aiMessage();
 //输出大语言模型的回复
System.out.println(aiMessage1.text());
 //第二轮对话
UserMessage userMessage2 = UserMessage.userMessage("你知道我是谁吗");
 ChatResponse chatResponse2 = qwenChatModel.chat(Arrays.asList(userMessage1, 
aiMessage1, userMessage2));
 AiMessage aiMessage2 = chatResponse2.aiMessage();
 //输出大语言模型的回复
System.out.println(aiMessage2.text());
 }

```

![image-20250707150636684](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20250707150636684.png)

3.  **使用ChatMemory实现聊天记忆**

```
   @Test
    public void testChatMemory3() {
        //创建chatMemory
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
//创建AIService
        Assistant assistant = AiServices
                .builder(Assistant.class)
                .chatLanguageModel(qwenChatModel)
                .chatMemory(chatMemory)
                .build();
        //调用service的接口
        String answer1 = assistant.chat("我叫AAA");
        System.out.println(answer1);
        String answer2 = assistant.chat("我的名字是什么");
        System.out.println(answer2);
        String answer3 = assistant.chat("你觉得我的名字怎么样");
        System.out.println(answer3);
        String answer4 = assistant.chat("有什么要修改的吗");
        System.out.println(answer4);
        String answer5 = assistant.chat("帮我在起个名字吧");
        System.out.println(answer5);
    }
```

![image-20250707151451158](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20250707151451158.png)

4. **使用AIService来实现聊天记忆**

AIService

```
package com.zxy.springai.assistant;

import dev.langchain4j.service.spring.AiService;
import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;
@AiService(
        wiringMode =EXPLICIT, chatModel = "qwenChatModel", chatMemory = "chatMemory"
)
public interface Assistant2 {

    String chat(String userMessage);
}

```

$$

$$

配置ChatMemory

```
package com.zxy.springai.config;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemoryChatAssistantConfig {
    @Bean
    public ChatMemory chatMemory() {
        return  MessageWindowChatMemory.withMaxMessages(10);
    }
}

```

测试

```
    private Assistant2 assistant2;
    @Test
    public void testChatMemory4() {

        //调用service的接口
        String answer1 = assistant2.chat("我叫朱啸毅");
        System.out.println(answer1);
        String answer2 = assistant2.chat("我的名字是什么");
        System.out.println(answer2);
        String answer3 = assistant2.chat("你觉得我的名字怎么样");
        System.out.println(answer3);
        String answer4 = assistant2.chat("有什么要修改的吗");
        System.out.println(answer4);
        String answer5 = assistant2.chat("帮我在起个名字吧");
        System.out.println(answer5);
        String answer6 = assistant2.chat("改个英文名字，性别男，象征着力量");
        System.out.println(answer6);
        String answer7 = assistant2.chat("在优化一下这个名字");
        System.out.println(answer7);
    }
```

```
你好，朱啸毅！很高兴认识你。有什么我可以帮助你的吗？
你的名字是朱啸毅。
你的名字“朱啸毅”非常有特色和力量感。“朱”是一个常见的中国姓氏，而“啸”字让人联想到豪迈、自由不羁的精神，“毅”则意味着坚强、毅力。整体来看，这个名字既有文化底蕴，又富有个性，很好地体现了积极向上的精神面貌。你觉得这个名字怎么样呢？有没有什么特别的意义或者故事？
你的名字“朱啸毅”已经非常有特色和含义了，但如果出于某些个人原因你想要做一些调整，这里有一些建议：

1. **保持原意**：如果你喜欢名字的含义，但希望有一些小的变化，可以考虑保留“啸”和“毅”，但选择一个不同的姓氏或者中间字。例如：
   - 朱啸风
   - 朱啸轩
   - 朱啸翔

2. **简化或复杂化**：如果你觉得名字太复杂或太简单，可以适当调整。例如：
   - 简化：朱啸
   - 复杂化：朱啸毅轩

3. **增加文化元素**：如果你想让名字更有文化底蕴，可以考虑加入一些有特殊意义的字。例如：
   - 朱啸文
   - 朱啸武
   - 朱啸松（松象征坚韧不拔）

4. **个性化**：如果你希望名字更加独特，可以考虑一些不太常见的字。例如：
   - 朱啸岚
   - 朱啸瀚
   - 朱啸瑾

不过，最终的选择还是要看你个人的喜好和对名字的期望。你觉得这些建议怎么样？有没有哪个特别吸引你？
当然可以！为了更好地给你起一个合适的名字，我需要了解一些额外的信息：

1. **性别**：你是男性还是女性？
2. **喜好**：你有没有特别喜欢的字或含义？比如希望名字中包含某种品质（如智慧、勇敢、平和等）。
3. **文化背景**：你希望名字有什么样的文化背景或寓意？
4. **发音**：你有没有特别喜欢的音节或发音？

如果你能提供这些信息，我可以更有针对性地为你起一个好名字。
2025-07-07T15:37:35.712+08:00  WARN 16800 --- [           main] d.l.c.model.dashscope.QwenHelper         : The first message should be a system message or a user message, but found: AiMessage { text = "你好，朱啸毅！很高兴认识你。有什么我可以帮助你的吗？" toolExecutionRequests = null }
好的，根据你的要求，我为你挑选了一些象征力量的英文名字。这些名字不仅听起来有力，而且在文化和历史上也有一定的背景和意义：

1. **Alexander** - 意为“保护者”，源自希腊语，历史上著名的亚历山大大帝就是这个名字。
2. **Maximus** - 拉丁语，意为“最大的”，给人一种强大和威严的感觉。
3. **Titan** - 希腊神话中的巨人，象征着巨大的力量和坚韧。
4. **Thor** - 北欧神话中的雷神，象征力量和勇气。
5. **Leo** - 拉丁语，意为“狮子”，象征勇敢和力量。
6. **Atlas** - 希腊神话中支撑天空的巨人，象征着承担重任和力量。
7. **Brave** - 英语，直接意为“勇敢的”，非常直观地传达了力量和勇气。
8. **Darius** - 波斯语，意为“富有”或“强大的”，历史上有多位波斯国王使用这个名字。
9. **Hercules** - 罗马神话中的大力神，象征无与伦比的力量和勇气。
10. **Nolan** - 爱尔兰语，意为“著名的”或“勇敢的”。

你可以根据自己的喜好选择一个，或者告诉我你对这些名字的看法，我可以进一步为你推荐。
2025-07-07T15:37:44.836+08:00  WARN 16800 --- [           main] d.l.c.model.dashscope.QwenHelper         : The first message should be a system message or a user message, but found: AiMessage { text = "你的名字是朱啸毅。" toolExecutionRequests = null }
好的，根据你的要求，我将进一步优化这些名字，并提供一些新的选择。以下是几个象征力量且具有独特魅力的英文名字：

1. **Alden** - 源自古英语，意为“老朋友”或“智慧和勇气”，给人一种稳重而有力的感觉。
2. **Bryce** - 苏格兰语，意为“有力量的人”，简洁而有力。
3. **Cyrus** - 波斯语，意为“太阳”，历史上著名的波斯国王居鲁士大帝就是这个名字，象征着领导力和力量。
4. **Damon** - 希腊语，意为“驯服”或“征服”，给人一种强大而坚定的印象。
5. **Ethan** - 希伯来语，意为“坚固、持久”，象征着坚强和可靠。
6. **Finn** - 爱尔兰语，意为“公平、白皙”，在北欧神话中也有英雄的形象，象征勇气和力量。
7. **Gavin** - 盖尔语，意为“小白鹰”或“上帝是仁慈的”，给人一种勇敢和高贵的感觉。
8. **Hector** - 希腊语，意为“握住”，特洛伊战争中的英雄赫克托耳就是这个名字，象征着无畏和勇气。
9. **Jaxon** - 英语，现代形式的“Jackson”，意为“上帝的儿子”，给人一种年轻而有力的感觉。
10. **Kai** - 多种文化背景，可以是夏威夷语中的“海洋”，也可以是丹麦语中的“地球”，简洁而有力。

这些名字不仅具有象征力量的意义，还各具特色。你可以根据自己的喜好和感觉选择一个最适合你的名字。如果你有其他特定的要求或偏好，请告诉我，我可以进一步为你优化推荐。
```

当AIService由多个组件（大模型，聊天记忆，等）组成的时候，我们就可以称他为agent(智能体)

5. **隔离聊天记忆**

为每个用户的新聊天或者不同的用户区分聊天记忆  通过chatMemoryProvider来实现ChatMemoryId来进行隔离的

创建记忆隔离对话智能体 SeparateChatAssistant

```
package com.zxy.springai.assistant;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(
        wiringMode = EXPLICIT,
        chatMemory = "chatMemory",
        chatMemoryProvider = "chatMemoryProvider"
)
public interface SeparateChatAssistant {
    /**
     * 分离聊天记录
     * @param memoryId 聊天id
     * @param userMessage 用户消息
     * @return
     */
    String chat(@MemoryId int memoryId, @UserMessage String userMessage);
}

```

配置ChatMemoryProvider

```
package com.zxy.springai.config;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeparateChatAssistantConfig {

    @Bean
    ChatMemoryProvider chatMemoryProvider() {
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10)
                .build();
    }
}

```

测试对话助手  用两个不同的memoryId测试聊天记忆的隔离效果

```
  @Autowired
 private SeparateChatAssistant separateChatAssistant; 
   @Test
    public void testChatMemory5() {
        String answer1 = separateChatAssistant.chat(1,"我是BBB");
        System.out.println(answer1);
        String answer2 = separateChatAssistant.chat(1,"我是谁");
        System.out.println(answer2);
        String answer3 = separateChatAssistant.chat(2,"我是谁");
        System.out.println(answer3);
    }
```

6. **ChatMemory实现的原理**

# 持久化存储

## 1 存储选择

## 2 MongoDB

[MongoDB Compass Download (GUI) | MongoDB](https://www.mongodb.com/try/download/compass)

下载MongoDB

MongoDB

```
查看当前数据库：db
显示数据库列表：
show dbs
切换到指定数据库：
use <database_name>
执行查询操作：
db.<collection_name>.find()
 <port> 是 MongoDB 服务器的端口号。
插入文档：
db.<collection_name>.insertOne({ ... })
更新文档：
db.<collection_name>.updateOne({ ... })
删除文档：
db.<collection_name>.deleteOne({ ... })
退出 MongoDB Shell：
quit() 或者 
exit
# 插入文档
test> db.mycollection.insertOne({ name: "Alice", age: 30 })
 # 查询文档
test> db.mycollection.find()
 # 更新文档
test> db.mycollection.updateOne({ name: "Alice" }, { $set: { age: 31 } })
 # 删除文档
test> db.mycollection.deleteOne({ name: "Alice" })
 # 退出 MongoDB Shell
 test> quit()
```

整合springboot

```
<!-- Spring Boot Starter Data MongoDB -->
 <dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-data-mongodb</artifactId>
 </dependency>
```

application.yml配置

```
spring.data.mongodb.uri=mongodb://localhost:27017/chat_memory_db
```

CRUD测试

## 3 持久化

## 4 测试

# **Prompt**

@SystemMessage 设定角色，塑造AI助手的专业身份，明确助手的能力范围

**1 角色 他是什么领域的专家**

**2 任务 他要完成什么任务**

**3 工具 他可以调用什么工具**

**4 约束 应该遵循什么规则**

**5 输出 结果是什么样子的**

## 1 系统提示词模板

配置 

测试 

模板

## 2 用户提示词模板

#   Tools 

Function Calling也叫tools工具

1 工具类

2项目优化

#  RAG

## 1 什么是RAG

检索增强生成RAG（Retrieval-Augmented Generation，检索增强生成）是一种将信息检索与语言模型生成能力相结合的技术框架，旨在解决传统语言模型在处理需要实时、准确或特定领域知识时的局限性

RAG 的本质是 “让语言模型学会‘查资料’再回答”，而非仅依赖 “记忆”。它通过 “检索 + 生成” 的结合，弥补了传统大模型在知识时效性、准确性和专业领域应用中的短板，成为当前 AI 落地企业级场景（如知识管理、垂直领域服务）的核心技术框架之一

RAG和传统模型对比

| **维度**         | **传统语言模型（如 GPT）** | **RAG 框架**                               |
| ---------------- | -------------------------- | ------------------------------------------ |
| **知识来源**     | 依赖预训练数据中的历史知识 | 结合外部知识库的实时 / 专业知识            |
| **知识更新**     | 需重新训练模型才能更新知识 | 只需更新知识库，无需重新训练模型           |
| **事实准确性**   | 易产生 “幻觉”（虚构事实）  | 生成内容基于检索到的真实文档，准确性更高   |
| **应用场景**     | 通用文本生成、创意写作等   | 需实时数据、专业知识或精确引用的场景       |
| **对模型的要求** | 依赖大规模参数存储知识     | 模型只需具备理解和生成能力，知识存储在外部 |

## 2  vector search

向量搜索

## 3 RAG的过程

## 4 文档加载器 Document Loader

## 5 文档解析器 Document Parser 

## 6 文档分割器 Document Splitter

# 向量模型和向量存储

## 模型

## 存储

Pinecone

## 相似度匹配

# 流式输出

# 项目实战