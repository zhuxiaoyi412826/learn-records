# githup CodeQL

## 命令注入

![image-20260830100948634](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260830100948634.png)

| 漏洞                                 | 是否可利用 | 说明                                                         |
| ------------------------------------ | ---------- | ------------------------------------------------------------ |
| 用户代码读取本地文件                 | ✅已复现    | 子 JVM 继承管理员权限，**英文绝对路径可以读任意文件**（win.ini 读取成功） |
| 用户代码执行系统命令                 | ✅已复现    | `ProcessBuilder`执行 whoami 成功，拿到 administrator 身份    |
| CodeQL 告警：后端 className 命令注入 | ⚠️静态风险  | 当前接口外部无法传入 className；但代码必须加正则校验，防止后续代码改动变成可利用漏洞 |

**危险代码**

非受控命令行

该命令行依赖于用户提供的价值.
该命令行依赖于用户提供的价值.



将用户输入直接传递给 或其他执行命令的库例程的代码，允许用户执行恶意代码。`Runtime.exec`

### ✅**文件读取漏洞**

**run_file.json**

```
{
    "code":"import java.nio.file.*;\npublic class Main {\n    public static void main(String[] args) throws Exception {\n        System.out.println(Files.readString(Paths.get(\"C:/Windows/win.ini\")));\n    }\n}",
    "language":"java",
    "input":""
}
```

```
curl.exe -b cookies.txt -H "Content-Type: application/json" -d "@run_file.json" http://127.0.0.1/api/code/run -o resp.json
```

```
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   425    0   174  100   251    169    244  0:00:01  0:00:01 --:--:--   413

D:\daima\XiangMu\算法数据结构可视化\AlgoVize\houduan>type resp.json
{"output":"; for 16-bit app support\n[fonts]\n[extensions]\n[mci extensions]\n[files]\n[Mail]\nMAPI=1","memory":0,"success":true,"time":146,"message":null,"status":"success"}
```

用户提交的恶意 Java 代码，成功读取到服务器本地 `C:/Windows/win.ini` 文件内容并完整输出返回。

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260830101241.png)

### ✅**命令执行漏洞**

run_cmd.json

```
{
    "code":"public class Main {\n    public static void main(String[] args) throws Exception {\n        String[] cmd = {\"cmd\",\"/c\",\"whoami\"};\n        Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();\n        System.out.print(new String(p.getInputStream().readAllBytes()));\n    }\n}",
    "language":"java",
    "input":""
}

```

执行命令

```
curl.exe -b cookies.txt -H "Content-Type: application/json" -d "@run_cmd.json" http://127.0.0.1/api/code/run -o resp.json
```

```
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   465    0   114  100   351    109    336  0:00:01  0:00:01 --:--:--   446

D:\daima\XiangMu\算法数据结构可视化\AlgoVize\houduan>type resp.json
{"output":"bf-202607031052\\administrator","memory":0,"success":true,"time":173,"message":null,"status":"success"}
D:\daima\XiangMu\算法数据结构可视化\AlgoVize\houduan>
```

### 解决

证通过。完整解决方案如下。

| #    | 漏洞                                     | 危害                                                         |
| :--- | :--------------------------------------- | :----------------------------------------------------------- |
| 1    | 用户代码可读服务器任意文件               | 子 JVM 以管理员权限运行，`Files.readString("C:/Windows/win.ini")` 成功 |
| 2    | 用户代码可执行系统命令                   | `ProcessBuilder("cmd","/c","whoami")` 成功，拿到 administrator |
| 3    | extractClassName 解析缺陷（CodeQL 告警） | `Main extends Object` 被吞进进程参数，潜在命令注入面         |

#### 解决方案：三层防护

**第一层：静态代码扫描（编译前拦截，快速反馈）**

在 `runJavaCode` 编译前扫描源码，命中危险 API 直接返回 CE：

```java
private static final String[] FORBIDDEN_API_KEYWORDS = {
        "ProcessBuilder", "Runtime", "getRuntime", "exec(", "System.load",
        "Class.forName", "URLClassLoader", "Socket", "ServerSocket", "URLConnection",
        "HttpURLConnection", "new File(", "FileInputStream", "FileOutputStream",
        "FileWriter", "FileReader", "Files."
};

// runJavaCode 内
for (String keyword : FORBIDDEN_API_KEYWORDS) {
    if (code.contains(keyword)) {
        result.put("status", "ce");
        result.put("message", "编译错误: 检测到禁止使用的 API \"" + keyword + "\"...");
        return result;
    }
}
```

作用：把 `ProcessBuilder`/`whoami` 等请求**在编译前就拒绝**，给用户明确提示，不消耗编译/运行资源。

**第二层：SecurityManager 运行时沙箱（真正的拦截）**

编译通过的代码也可能用反射绕过静态扫描，因此核心防线是子 JVM 的 `SecurityManager`：

Java

```java
// 子进程启动参数（关键两行）
"-Djava.security.manager",                                   // 启用安全管理器
"-Djava.security.policy=" + policyFile.toURI(),              // 加载自定义策略
```

配套的 [security.policy](file:///D:/\daima\XiangMu\算法数据结构可视化\AlgoVize\houduan\src\main\resources\security.policy)（策略 = 默认全拒绝，仅显式放行）：

properties

```properties
grant {
    # 仅临时运行目录内文件读写删（${user.dir} = 子进程工作目录 = 临时目录）
    permission java.io.FilePermission "${user.dir}/-", "read,write,delete";
    # 标准输入/输出（判题必须）
    permission java.lang.RuntimePermission "readFileDescriptor";
    permission java.lang.RuntimePermission "writeFileDescriptor";
    # 读系统属性
    permission java.util.PropertyPermission "*", "read";
    # ⚠️ 故意不授予：createProcess / SocketPermission / accessDeclaredMembers / 目录外 FilePermission
};
```

拦截效果：

| 攻击                      | 被拦的权限                                    | 结果                |
| :------------------------ | :-------------------------------------------- | :------------------ |
| `ProcessBuilder` 执行命令 | `RuntimePermission "createProcess"`（未授予） | `SecurityException` |
| 读 `C:/Windows/win.ini`   | 临时目录外 `FilePermission`（未授予）         | `SecurityException` |
| 网络连接/内网探测         | `SocketPermission`（未授予）                  | `SecurityException` |
| `System.exit()` 杀进程    | `RuntimePermission "exitVM"`（未授予）        | `SecurityException` |
| 反射 `setAccessible` 逃逸 | `suppressAccessChecks`（未授予）              | `SecurityException` |

**第三层：className 输入校验（闭环 CodeQL 告警）**

Java

```java
// 仅允许合法 Java 标识符
private static final Pattern JAVA_CLASS_NAME_PATTERN =
        Pattern.compile("^[A-Za-z_$][A-Za-z0-9_$]*$");

// extractClassName 之后校验
if (!JAVA_CLASS_NAME_PATTERN.matcher(className).matches()) {
    result.put("status", "ce");
    result.put("message", "编译错误: 非法类名 \"" + className + "\"，类名只能由字母、数字、下划线、$ 组成");
    return result;
}
```

`Main extends Object`、`Main -version` 等解析结果全部被拒，进程参数不再可能被污染。

#### 关键实现要点

- **policy 文件动态写入临时目录**：`security.policy` 放 resources，运行时用 `ClassPathResource` 拷到 `tempDir/security.policy`，子进程用绝对 URI 加载（避免 jar 内路径不可用）
- **`${user.dir}` 自动指向临时目录**：`pb.directory(tempDir)` 使子进程工作目录 = 临时目录，policy 中 `${user.dir}/-` 恰好圈定运行目录
- **一处修改覆盖两个入口**：`runCode`（调试）与 `judgeJavaCode`（判题）共用 `runJavaCode`，安全加固同时生效

**验证结果**

- 正常题（`public class Main` + 输入输出）：**正常启动运行** ✓
- whoami / 读文件攻击：第一层静态扫描拦截（返回 CE），即便绕过也触发 SecurityException ✓
- `Main extends Object`：返回"非法类名" ✓

```
{"output":null,"memory":null,"success":true,"time":null,"message":"编译错误: 检测到禁止使用的 API \"Class.forName\"，出于安全考虑不允许文件IO/进程/网络相关操作","status":"ce"}
```

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260830110726.png)

#### 局限与后续

1. **SecurityManager 在 JDK 17 已废弃**（JDK 24+ 将移除）：短期可用；长期应升级为**容器级隔离**（每提交起 `docker run --rm --network=none --memory=64m --cpus=0.5 --read-only` 子容器）。
2. **Python / C++ / JavaScript 运行接口同样无沙箱**（`os.system`、`system()`、`child_process.exec` 均可执行命令），本次仅加固 Java。需要可继续处理。
3. 静态扫描是辅助层，可能误伤极少数含 `Socket`/`Runtime` 字样字符串的题，遇到可调整关键词。