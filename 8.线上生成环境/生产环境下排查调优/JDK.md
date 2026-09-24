# CMD 临时指定 JDK

（只当前窗口生效，关闭 cmd 就失效）

> 不需要改系统环境变量，**直接在 cmd 里执行这两行**，之后 java 就用你指定的 jdk

```
set JAVA_HOME=D:\jdk1.8.0_301
set PATH=%JAVA_HOME%\bin;%PATH%
```

### 完整示例（复制粘贴到 cmd）

```
:: 设置你的jdk实际路径，改成你自己的
set JAVA_HOME=D:\jdk1.8.0_301
set PATH=%JAVA_HOME%\bin;%PATH%
:: 验证是否生效
java -version
:: 然后执行你的启动命令，例如
java -jar demo.jar
```

⚠️关键点：

1. `%JAVA_HOME%\bin`写在**% PATH% 前面**，优先使用这个 jdk
2. **只对当前这个 cmd 窗口有效**，新开 cmd 会恢复系统默认 jdk
3. 路径不要带空格；如果路径有空格，要加引号：

```
set JAVA_HOME="D:\Program Files\jdk1.8.0_301"
set PATH=%JAVA_HOME%\bin;%PATH%
```

------

## 更简单：直接调用 java 绝对路径

不需要设置 JAVA_HOME，直接写完整 java.exe 路径，完全不受系统环境影响：

```
D:\jdk1.8.0_301\bin\java.exe -jar demo.jar
```

> 这个方式最简单，不会污染 PATH，直接指定 jdk 的 java 程序。