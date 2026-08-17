# 一、核心原理

1. 生成 SSL 证书（免费）

2. 放到项目里

3. 加 4 行配置

4. 重启 jar 包

   

   ✅ 

   https://ip:8080 就能访问

------

# 二、一步一步来（5 分钟搞定）

## 1. 生成 SSL 证书（命令行执行）

`keytool` 是 **JDK 自带的证书生成工具**

```
keytool -genkey -alias tomcat -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore ssl.p12 -validity 3650
```

执行后会让你输密码，**自己记好**，比如直接用：

plaintext

```
123456
```

一路回车到底，最后输入 `y` 确认。

执行完会生成一个文件：**ssl.p12**

------

## 2. 把 ssl.p12 放到 resources 目录

把生成的 `ssl.p12` 复制到：

plaintext

```
src/main/resources/ssl.p12
```

------

## 3. 修改 application.yml/application.properties

我给你**最精简可用配置**，直接复制！

### 如果是 application.yml

yaml

```
server:
  port: 8080
  ssl:
    enabled: true
    key-store: classpath:ssl.p12
    key-store-type: PKCS12
    key-store-password: 123456  # 你刚才设置的密码
```

### 如果是 application.properties

properties

```
server.port=8080
server.ssl.enabled=true
server.ssl.key-store=classpath:ssl.p12
server.ssl.key-store-type=PKCS12
server.ssl.key-store-password=123456
```

------

# 三、重新打包、上传、运行

本地重新打包：

```
mvn clean package -DskipTests
```

服务器运行：

```
nohup java -jar markdown-reader-1.0.0.jar > app.log 2>&1 &
```

------

# 四、现在可以访问了 ✅

plaintext

```
https://175.178.228.138:8080/
```

**注意：**

- 第一次访问浏览器会提示 “不安全”，这是正常的（自签名证书）
- 点 **高级 → 继续访问** 就可以正常打开