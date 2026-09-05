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

![image-20250707150924724](C:\Users\DELL\Desktop\文章\AIService\image-20250707150924724.png)

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

![image-20250707144959732](C:\Users\DELL\Desktop\文章\AIService\image-202507071449597312.png)

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

![image-20250707150636684](C:\Users\DELL\Desktop\文章\AIService\1image-20250707150636684.png)

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

![image-20250707151451158](C:\Users\DELL\Desktop\文章\AIService\image-20250707151451158.png)

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

