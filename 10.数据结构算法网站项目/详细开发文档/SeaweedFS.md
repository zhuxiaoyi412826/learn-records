两者区别

| 对比维度   | SeaweedFS                                                    | RustFS                                                       |
| ---------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 核心架构   | Haystack 架构（Master+Volume+Filer），元数据下沉到卷服务器，天生为小文件优化 | 类 MinIO 架构，自研 LSM 元数据引擎，原生纠删码，去中心化设计 |
| 开发语言   | Go 为主，部分存储模块已 Rust 化                              | 纯 Rust，无 GC，内存安全，无垃圾回收抖动                     |
| 小文件性能 | **极强**，单文件元数据仅 40 字节磁盘开销，O (1) 次磁盘读取，海量小文件场景碾压 | 优秀，4KB 文件吞吐为旧 MinIO 的 2.3 倍，极致小文件略逊于 SeaweedFS |
| 内存占用   | 中等                                                         | **极低**，空闲仅十几 MB，约为旧 MinIO 的 1/3                 |
| 生产成熟度 | 10+ 年历史，国内大量企业生产落地，久经考验，踩坑资料丰富     | 快速崛起新星，1.0 处于 RC 阶段，生产案例快速增长，但沉淀时间更短 |
| S3 兼容性  | 通过 S3 Gateway 网关兼容，核心功能齐全，高级策略持续完善     | 原生 S3 兼容，高度对齐 MinIO，桶策略、版本控制、生命周期完整 |
| Web 控制台 | 有，偏运维管理风格，交互偏技术向                             | 有，体验高度接近旧 MinIO，可视化友好，新手易上手             |
| 扩容方式   | 新增节点即扩容，无需强制数据重平衡                           | 按纠删码池扩容，需对称扩盘，逻辑同 MinIO                     |
| 开源协议   | Apache-2.0，商用无风险                                       | Apache-2.0，商用无风险                                       |
| 单机部署   | Docker 一键启动，单进程包含全套组件                          | Docker 一键启动，用法与旧 MinIO 几乎一致                     |
| 特色能力   | FUSE 挂载、POSIX 目录、冷热分层、内置 Iceberg 数据湖目录     | 国密算法原生支持、信创全栈适配、多协议（S3/Swift/WebDAV/FTP） |

# RustFS

https://github.com/rustfs/rustfs

> RustFS 完全兼容 S3 协议，SpringBoot 直接使用 **AWS‑SDK‑V2**，**不需要第三方 starter**，和 SeaweedFS/MinIO 代码几乎一样，仅改配置。 Windows 开发两种方式：①Docker（推荐，最简单）；②Windows 原生 exe 二进制包（免 Docker）。

## 方式一：Windows Docker 部署（开发首选）

> Windows 先开启 Docker Desktop。

```
docker run -d --name rustfs ^
-p 9000:9000 ^
-p 9001:9001 ^
-v D:\rustfs-data:/data ^
-e RUSTFS_ACCESS_KEY=admin ^
-e RUSTFS_SECRET_KEY=Admin@123456 ^
rustfs/rustfs:latest
```

- `9000`：S3 API 端口，SpringBoot 代码连接
- `9001`：Web 控制台，浏览器访问 `http://127.0.0.1:9001`
- `-v D:\rustfs-data:/data`：持久化，数据存在 D 盘，删除容器数据不丢失
- 登录 Web 控制台：AK=`admin`，SK=`Admin@123456`

> ⚠️Windows Docker 挂载目录权限：如果报权限写入失败，WSL2 模式下优先放在 WSL 内部目录，不要直接映射 Windows 磁盘。

## 方式二：Windows 原生 exe（免 Docker，适合不想装 Docker）

1. 下载 windows 二进制包： https://dl.rustfs.com/artifacts/rustfs/release/rustfs-windows-x86_64-latest.zip博客园
2. 解压到`D:\rustfs`
3. PowerShell 管理员执行解除文件锁定：

```
cd D:\rustfs
Unblock‑File rustfs.exe
```

1. 直接命令行启动：

```
.\rustfs.exe server --data‑dir "D:\rustfs‑data" --address ":9000" --console‑address ":9001" --access‑key "admin" --secret‑key "Admin@123456"
```

浏览器访问：`http://127.0.0.1:9001`稀土掘金

## SpringBoot 接入 RustFS

### 1、pom.xml 依赖（AWS SDK V2，不要用第三方 rustfs‑starter）

```
<!-- AWS S3 SDK v2 核心，兼容所有S3存储：RustFS/SeaweedFS/MinIO/阿里云OSS -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>2.29.20</version>
</dependency>
<!-- http客户端 -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>apache‑http‑client</artifactId>
    <version>2.29.20</version>
</dependency>
<!-- lombok、web根据项目已有 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring‑boot‑starter‑web</artifactId>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

### 2、application.yml 配置

```
rustfs:
  endpoint: http://127.0.0.1:9000
  access‑key: admin
  secret‑key: Admin@123456
  region: us‑east‑1
  bucket‑name: api‑log‑bucket

spring:
  servlet:
    multipart:
      max‑file‑size: 100MB
      max‑request‑size: 100MB
```

### 3、配置类 RustFsS3Config.java

> RustFS**不需要 forcePathStyle=true**（这点和 SeaweedFS 不一样！）

```
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Data
@Configuration
@ConfigurationProperties(prefix = "rustfs")
public class RustFsS3Config {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucketName;

    @Bean(destroyMethod = "close")
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                // RustFS不需要 forcePathStyle(true)！SeaweedFS才需要打开这个
                .build();
    }

    @Bean(destroyMethod = "close")
    public S3Presigner s3Presigner() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Presigner.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
```

### 4、业务 Service（上传、下载、删除、预签名 URL）

适配你的业务：存储 api‑log req/resp json 报文、图片、文档

```
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RustFsFileService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final RustFsS3Config config;

    /**
     * 项目启动自动创建桶，不存在则新建
     */
    @jakarta.annotation.PostConstruct
    public void initBucket() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(config.getBucketName()).build());
        } catch (NoSuchBucketException e) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(config.getBucketName()).build());
        }
    }

    /**
     * 通用上传
     * @param key 对象路径，如 req/20260822/abc123.json
     * @param inputStream 文件流
     * @param contentType mime类型
     */
    public void upload(String key, InputStream inputStream, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(config.getBucketName())
                .key(key)
                .contentType(contentType)
                .build();
        s3Client.putObject(request, RequestBody.fromInputStream(inputStream, -1));
    }

    // MultipartFile快捷上传
    public String uploadMultipart(MultipartFile file) throws IOException {
        String key = "uploads/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        upload(key, file.getInputStream(), file.getContentType());
        return key;
    }

    // 获取文件流
    public InputStream download(String key) {
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(config.getBucketName())
                .key(key)
                .build();
        return s3Client.getObject(getReq);
    }

    // 删除文件
    public void delete(String key) {
        DeleteObjectRequest delReq = DeleteObjectRequest.builder()
                .bucket(config.getBucketName())
                .key(key)
                .build();
        s3Client.deleteObject(delReq);
    }

    // 获取临时下载预签名URL（1小时有效期）
    public String getPresignedDownloadUrl(String key) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(GetObjectRequest.builder().bucket(config.getBucketName()).key(key).build())
                .signatureDuration(Duration.ofHours(1))
                .build();
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}
```

### 5、简单 Controller 测试

```
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {
    private final RustFsFileService rustFsFileService;

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws IOException {
        String key = rustFsFileService.uploadMultipart(file);
        return "file key：" + key;
    }

    @GetMapping("/url/{key}")
    public String getTempUrl(@PathVariable String key) {
        return rustFsFileService.getPresignedDownloadUrl(key);
    }
}
```

## 业务结合你的 OJ api‑log 场景

> MongoDB 只保存元数据，报文 json 存入 RustFS：

```
{
  "_id":"log_xxx",
  "user_id":"u001",
  "create_time":"2026‑08‑22T10:00:00Z",
  "api_path":"/api/submit",
  "status_code":200,
  "req_file_key":"req/20260822/abc123.json",
  "resp_file_key":"resp/20260822/def456.json"
}
```

查看详情：使用`req_file_key`调用服务生成临时预签名 URL，下载 json 报文。

## ⚠️重点避坑（RustFS vs SeaweedFS）

表格

| 项目                 | 是否开启 forcePathStyle |
| -------------------- | ----------------------- |
| RustFS               | **不需要**              |
| SeaweedFS S3‑Gateway | **必须开启 true**       |

> 切换存储只改 yml 配置，Java 代码完全不用改。

## Windows 开发环境常见问题

1. Docker 挂载 Windows 磁盘权限报错：RustFS 容器内部用户 uid=10001，直接映射 Windows 目录会权限不足，开发环境建议使用 WSL2 内部目录挂载。
2. 端口占用：9000 端口被占用，修改 docker 映射端口。
3. Web 控制台打不开：确认 RUSTFS_CONSOLE_ENABLE=true，默认 docker 镜像已经开启。

## 选型回顾

- 大量 KB 级小 json 报文，生产案例多：优先 **SeaweedFS**
- 怀念 MinIO 操作习惯、内存占用低：选 **RustFS**（目前 RC 阶段，生产需要评估风险）

如果你需要，我可以给一份 docker‑compose.yml 开发环境配置，同时对比 SeaweedFS 与 RustFS 两套 yml 切换模板。

# SpringBoot 接入 SeaweedFS（S3 兼容模式）

> SeaweedFS**优先使用 S3 网关模式对接 SpringBoot**，不要用原生 filer java-client。原生 Java 客户端是操作 filer 文件系统；**S3 模式和 MinIO、阿里云 OSS 接口完全兼容，复用 AWS‑SDK‑V2，不需要第三方 starter**CSDN博...。

> GitHub 官方 wiki：https://github.com/seaweedfs/seaweedfs/wiki/S3-Gateway

## 1、启动 SeaweedFS（docker）

开启 S3 网关，端口 `8333`（S3 API），8888 是 Web UI 控制台。

```
docker run -d \
  --name seaweedfs \
  -p 8333:8333 \
  -p 8888:8888 \
  -v ./seaweed_data:/data \
  chrislusf/seaweedfs \
  server -s3 -dir=/data
```

> 默认 access‑key=`weed`，secret‑key=`weed`；访问 Web 控制台：`http://127.0.0.1:8888`。

## 2、Maven 依赖（pom.xml）

使用 AWS SDK V2，不需要 SeaweedFS 专属 SDK。

```
<!-- AWS S3 SDK v2 核心 -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>2.29.20</version>
</dependency>
<!-- http客户端 -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>apache‑http‑client</artifactId>
    <version>2.29.20</version>
</dependency>
```

## 3、application.yml 配置

```
seaweedfs:
  s3:
    endpoint: http://127.0.0.1:8333
    access‑key: weed
    secret‑key: weed
    region: us‑east‑1
    bucket‑name: api‑log‑bucket
```

## 4、SpringBoot 配置类 S3Config.java

> **forcePathStyle (true) 必须开启！SeaweedFS S3 必须路径访问，否则报错**。

```
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Data
@Configuration
@ConfigurationProperties(prefix = "seaweedfs.s3")
public class SeaweedS3Config {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucketName;

    @Bean(destroyMethod = "close")
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .forcePathStyle(true) // SeaweedFS S3必须开启
                .build();
    }

    // 生成预签名URL（前端直传/临时下载）
    @Bean(destroyMethod = "close")
    public S3Presigner s3Presigner() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Presigner.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
```

## 5、业务 Service 示例（上传、下载、删除、预签名 URL）

适配你的业务场景：存储 api_log 的 req_body/resp_body json 报文、图片、文章。

```
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeaweedFileService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final SeaweedS3Config config;

    /**
     * 上传文件
     * @param key 文件路径，例如 req/20260822/xxx.json
     * @param inputStream 文件流
     * @param contentType mime类型
     */
    public void upload(String key, InputStream inputStream, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(config.getBucketName())
                .key(key)
                .contentType(contentType)
                .build();
        s3Client.putObject(request, RequestBody.fromInputStream(inputStream, -1));
    }

    // MultipartFile快捷上传
    public String uploadMultipart(MultipartFile file) throws IOException {
        String key = "uploads/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        upload(key, file.getInputStream(), file.getContentType());
        return key;
    }

    // 下载文件流
    public InputStream download(String key) {
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(config.getBucketName())
                .key(key)
                .build();
        return s3Client.getObject(getReq);
    }

    // 删除文件
    public void delete(String key) {
        DeleteObjectRequest delReq = DeleteObjectRequest.builder()
                .bucket(config.getBucketName())
                .key(key)
                .build();
        s3Client.deleteObject(delReq);
    }

    // 获取临时下载URL（有效期1小时）
    public String getPresignedDownloadUrl(String key) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(GetObjectRequest.builder().bucket(config.getBucketName()).key(key).build())
                .signatureDuration(Duration.ofHours(1))
                .build();
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    // 桶不存在自动创建（项目启动调用一次）
    public void createBucketIfNotExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(config.getBucketName()).build());
        } catch (NoSuchBucketException e) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(config.getBucketName()).build());
        }
    }
}
```

## 6、Controller 简单测试

```
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final SeaweedFileService seaweedFileService;

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws IOException {
        String key = seaweedFileService.uploadMultipart(file);
        return "file key: " + key;
    }

    @GetMapping("/url/{key}")
    public String getUrl(@PathVariable String key) {
        return seaweedFileService.getPresignedDownloadUrl(key);
    }
}
```

## 7、业务结合你的 api_log 场景

> MongoDB 只保存元数据，报文存入 SeaweedFS：

```
{
  "_id":"log_xxx",
  "user_id":"u001",
  "create_time":"2026‑08‑22T10:00:00Z",
  "api_path":"/api/submit",
  "status_code":200,
  "req_file_key":"req/20260822/abc123.json",
  "resp_file_key":"resp/20260822/def456.json"
}
```

前端查看详情：拿到`req_file_key`调用服务生成临时下载 url，拉取 json 报文。

## ⚠️重要注意事项

1. **`forcePathStyle(true)`必须开启，否则 SeaweedFS S3 访问报错**。
2. SeaweedFS S3 网关默认`access‑key=weed`，`secret‑key=weed`，生产环境修改`s3.json`配置自定义密钥。
3. 生命周期：可以配置桶生命周期自动删除旧文件，对应日志过期清理。
4. 对比 MinIO：**业务代码几乎不用改动，只改 endpoint、ak/sk，无缝切换**。
5. 不要使用网上第三方 seaweedfs‑starter，优先原生 AWS‑SDK V2，减少第三方依赖风险。

## 官方文档地址

- GitHub：https://github.com/seaweedfs/seaweedfs
- S3‑Gateway 文档：https://github.com/seaweedfs/seaweedfs/wiki/S3-Gateway
- Java 示例：https://github.com/seaweedfs/seaweedfs/tree/master/other/java/examples

如果你需要，我可以给一份 docker‑compose.yml 生产部署配置，包含 S3、filer、volume 完整集群。