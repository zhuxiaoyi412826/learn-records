闲来无事，想弄一个图床，不然写文章，还要一个个的上传图片

**1 问豆包**

1 https://www.doubao.com/chat/ 问问他该怎么做

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20250315151053.png)

根据他的回答开始一步一步的操作

以下是将阿里云 OSS 配置为本地图床的详细步骤：

# 一、前提条件

1. 已注册阿里云账号并完成实名认证

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20250315151346.png)

1. 已开通 OSS 服务（对象存储）

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20250315151659.png)

# 二、创建存储空间（Bucket）

1. 登录阿里云 OSS 控制台（[https://oss.console.aliyun.com](https://oss.console.aliyun.com/)）

2. 创建 Bucket：

   - 名称：建议使用唯一标识（如 yourname-picbed）
   - 地域：选择离你最近的节点（如杭州 / 深圳）
   - 存储类型：标准存储

   - 访问权限：设置为「公共读」

   ![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20250315151755.png)

   ![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20250315151917.png)

# 三、配置访问凭证

1. 获取 AccessKey：

   - 进入阿里云 RAM 控制台（[https://ram.console.aliyun.com](https://ram.console.aliyun.com/)）
   - 使用子用户创建 AccessKey ID 和 AccessKey Secret（妥善保存，勿泄露）

   ![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20250315152119.png)

访问方式勾选Accesskey

创建完成后及时保存key

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20250315152406.png)

在右侧添加权限

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20250315152204.png)

权限添加OSS管理权限

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20250315152556.png)

# 四、上传图片到 OSS

## 服务器上传

这个我没弄

####  1：命令行工具（ossutil）

1. 下载 ossutil：https://help.aliyun.com/document_detail/50452.html

2. 配置凭证：

   bash

   ```bash
   ./ossutil64 config -e oss-cn-hangzhou.aliyuncs.com -i <AccessKey ID> -k <AccessKey Secret>
   ```

3. 上传文件：

   bash

   ```bash
   ./ossutil64 cp /本地路径/图片.jpg oss://your-bucket-name/图片.jpg
   ```

#### 2：图形化工具（ossbrowser）

1. 下载安装 ossbrowser：https://help.aliyun.com/document_detail/28942.html
2. 使用 AccessKey 登录
3. 拖拽文件到目标 Bucket 即可

### 3、生成图片链接

公共读 Bucket 的图片 URL 格式：

plaintext

```plaintext
http://<BucketName>.<Endpoint>/<ObjectName>
```

示例：

plaintext

```plaintext
http://yourname-picbed.oss-cn-hangzhou.aliyuncs.com/2025/03/15/123.jpg
```

## 本地上传

### 六、本地工具配置（以 PicGo 为例）

1. 安装 PicGo：https://github.com/Molunerfinn/PicGo

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20250315153008.png)

下载安装即可，打开之后是这样的

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20250315153050.png)

2. 设置信息打开 PicGo 设置 → 插件设置 → 安装 `picgo-plugin-aliyun-oss`

https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20250315153158.png

3. 配置参数：按照你们自己的来配置

- AccessKey ID
- AccessKey Secret

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20250315153300.png)

- Bucket 名称

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20250315153419.png)

- 存储区域（如 `oss-cn-hangzhou`）

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20250315153508.png)

- 自定义域名（可选）

腾讯云配置





# 五、验证与使用

通过 OSS 控制台检查文件是否上传成功

![](https://zhuxiaoyi1.oss-cn-shenzhen.aliyuncs.com/img/20250315153629.png)

复制图片 URL 到浏览器测试

在 Markdown 或博客中使用图片链接

# 六、注意事项

1. 公共读 Bucket 会产生流量费用，建议开启 CDN 加速
2. 重要文件建议使用「私有读」并配置签名 URL
3. 定期清理无效文件，避免存储费用累积

通过以上步骤，你可以快速搭建基于阿里云 OSS 的稳定图床服务。如果需要更高级的功能（如图片处理、防盗链），可以进一步配置 OSS 的图片处理服务和 Bucket 策略。

**真的是太贴心了**

# picGo上传 到腾讯云

## picgo设置

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260723064314.png)

## 每一项说明 + 获取途径

### 1. 设定 SecretId 【必填】

### 2. 设定 SecretKey 【必填】

获取路径：

腾讯云控制台 → 顶部搜索【访问管理】→【访问密钥】→【API 密钥管理】

> ✅推荐做法：新建**子用户密钥**（只授予 COS 相关权限，安全！）；不建议直接使用主账号密钥。
>
> 生成后复制保存这两段字符串，粘贴进对应框。
>
> ⚠️SecretKey 只显示一次，丢失只能重新创建！

### 3. 设定 APPID 【必填】

获取路径：

1. 右上角头像 →【账号信息】页面直接看到 APPID（一串纯数字）
2. 或者 COS 存储桶名称格式：`名称-123456789`，`-`后面数字就是 APPID

### 4. 设定存储空间名（Bucket）【必填】

打开你的 COS 存储桶【概览】页面

存储桶全名格式：`xxx-123456789`

👉**完整全部复制填入**（名称 + 横杠 + APPID）

### 5. 确认存储区域 【必填】

存储桶概览页面查看「所属地域」，填写**标准地域编码**（不要填中文！）

常用地区代码参考：

- 广州：`ap-guangzhou`
- 上海：`ap-shanghai`
- 北京：`ap-beijing`
- 成都：`ap-chengdu`

> 注意区分：v5 版本必须填写`ap-xxx`长格式；v4 才使用 tj/gz 这种简写。

### 6. 指定存储路径【选填，推荐填写】

示例：`img/`

作用：所有上传图片自动放进桶内 `img` 文件夹，方便管理。

**末尾必须带上斜杠 /**，不填则图片直接放在桶根目录。

### 7. 设定自定义域名【选填】

如果你给 COS 绑定了自己域名（CDN 加速域名），在这里填写完整地址：

示例 `shturl.cc/UwJHyW96i`

👉填写之后，PicGo 生成的图片链接会使用你的自定义域名；**没有绑定域名直接留空！**

------

## 最简操作流程总结

1. 腾讯云创建 COS 存储桶，记下：Bucket 全名、地域代码、APPID
2. 访问管理创建 API 密钥，复制 SecretId、SecretKey
3. PicGo 选择 v5 版本，把上面信息依次粘贴到 5 个必填框
4. 存储路径填 `img/`（可选），自定义域名无则空着
5. 点击【确定】，可以点击【设为默认图床】

## ⚠️常见踩坑提醒

1. 地域写成中文、简写（比如写广州、gz）→上传失败！必须写 `ap-guangzhou`
2. Bucket 只填前面名字，漏掉 `-APPID` →报错
3. COS 版本选错 v4（新存储桶一律 v5）
4. 存储桶访问权限是私有，图片链接别人打不开 → 设置为**公有读**
5. 密钥泄露风险：不要把 SecretKey 分享给任何人