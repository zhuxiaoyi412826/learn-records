# JWT 完整总结

> JWT（JSON Web Token）由三部分组成：`Header.Base64URL(Header).Payload.Base64URL(Payload).Signature` ⚠️ 注意：**Header、Payload 只是 Base64URL 编码，不是加密，任何人都可以读取；签名才是防篡改的核心** 算法：`HS256` 对称加密，**编码、校验解码必须使用同一个密钥**

## 一、JWT 编码（生成 Token，服务端执行）

1. **构造 Header JSON**：声明签名算法与 token 类型

```
{
  "alg": "HS256",
  "typ": "JWT"
}
```

1. **构造 Payload 载荷**：存放业务数据，**禁止存放密码等敏感信息**

```
{
  "userId": 1001,
  "username": "zhangsan",
  "iat": 1795510000, //签发时间
  "exp": 1795513600  //过期时间，秒级时间戳，1小时后过期
}
```

1. Base64URL 编码

   ：把 Header、Payload 分别转为 Base64URL 字符串

   - Base64URL：替换`+`→`-`，`/`→`_`，去掉末尾填充`=`

2. **生成签名 Signature**：使用密钥，对拼接字符串`headerB64.payloadB64`做 HMAC‑SHA256 运算，结果再做 Base64URL 编码

3. **拼接三部分**：`headerB64 . payloadB64 . signatureB64`，得到完整 JWT Token，返回给前端。

## 二、JWT 解码两种模式

### 模式 1：无密钥解析（仅读取，不校验签名，不安全）

> 仅做 Base64URL 解码，**不校验签名，不能用于权限判断**

1. 将 token 按`.`分割为三段：`[headerB64, payloadB64, signature]`
2. 对第 1、2 段 Base64URL 解码，还原 Header、Payload JSON
3. ❗缺陷：攻击者篡改 payload 后依然可以解析出修改后的内容，**数据不可信**

### 模式 2：带密钥完整校验解码（业务后端标准用法）

> 必须传入密钥，校验签名 + 过期时间，鉴权使用

1. 将 token 按`.`分割为三段：`[headerB64, payloadB64, signature]`
2. 拼接前两段：`headerB64.payloadB64`
3. 使用**相同密钥**重新计算 HS256 签名
4. 对比新计算签名与 token 自带签名：
   - 不一致：token 被篡改，直接拒绝访问
   - 一致：校验过期时间`exp`，时间过期则拒绝访问
5. 全部校验通过，返回 Payload 业务数据。

------

# 完整可运行实例（Node.js）

> 依赖：`npm install jsonwebtoken`

```
const jwt = require('jsonwebtoken');
const crypto = require('crypto');

// 1.生成JWT密钥（32字节，HS256安全密钥）
const SECRET_KEY = crypto.randomBytes(32).toString('hex');
console.log("生成密钥：", SECRET_KEY);

// ==========【JWT编码：生成Token】==========
const payload = {
  userId: 1001,
  username: "zhangsan",
  exp: Math.floor(Date.now() / 1000) + 3600 //1小时过期
};

// 编码生成token
const token = jwt.sign(payload, SECRET_KEY, { algorithm: "HS256" });
console.log("\n=====生成JWT Token=====");
console.log(token);

// ==========【情况1：无密钥解析，仅读取，不校验签名】==========
function parseJwtNoSecret(token) {
  const [headerB64, payloadB64] = token.split('.');
  const header = JSON.parse(Buffer.from(headerB64, 'base64url').toString());
  const payloadData = JSON.parse(Buffer.from(payloadB64, 'base64url').toString());
  return { header, payload: payloadData };
}

console.log("\n=====无密钥解析结果（仅读取，不校验）=====");
const resNoSecret = parseJwtNoSecret(token);
console.log(resNoSecret);

// ==========【情况2：带密钥校验解码，业务鉴权使用】==========
console.log("\n=====带密钥校验解码=====");
try {
  const verifyData = jwt.verify(token, SECRET_KEY);
  console.log("token合法，解析载荷：", verifyData);
} catch (err) {
  console.log("token无效/过期/被篡改：", err.message);
}

// ==========测试篡改token（演示无密钥解析的漏洞）==========
console.log("\n=====测试篡改token=====");
const fakeToken = token.replace("1001","9999"); //篡改userId
console.log("篡改后的token：", fakeToken);

console.log("无密钥解析篡改token：", parseJwtNoSecret(fakeToken));
try {
  jwt.verify(fakeToken, SECRET_KEY);
} catch(e) {
  console.log("带密钥校验：篡改token直接报错：", e.message);
}
```

## 运行输出说明

1. 编码输出完整 JWT 字符串；
2. **无密钥解析**：就算 token 被篡改，依然可以解析出篡改后的数据；
3. **带密钥校验**：一旦 token 被修改，直接抛出异常，拒绝访问。

## 关键要点总结

表格

| 项目         | 无密钥解析                  | 带密钥校验解码                 |
| ------------ | --------------------------- | ------------------------------ |
| 是否需要密钥 | ❌不需要                     | ✅必须传入密钥                  |
| 作用         | 仅读取 Header、Payload 数据 | 校验签名、校验过期时间，鉴权   |
| 安全性       | ❌不安全，不能做权限判断     | ✅安全，后端接口鉴权标准        |
| 适用场景     | 前端展示过期时间、用户名 UI | 后端登录鉴权，判断接口访问权限 |

> ⚠️重要提醒
>
> 1. Payload 只是 Base64URL 编码，**不是加密，不要存放密码**；
> 2. 密钥只保存在后端，绝对不能暴露到前端；
> 3. 后端做权限判断，**必须使用带密钥校验解码，禁止使用无密钥解析**；
> 4. HS256 对称算法：编码与校验必须使用完全一致密钥。

------

## 流程简图

```
【编码】
Header JSON → Base64URL → headerB64
Payload JSON → Base64URL → payloadB64
headerB64.payloadB64 +密钥 → HMAC‑SHA256 → Signature
Token = headerB64 . payloadB64 . Signature

【解码‑无密钥】
Token分割 → headerB64/payloadB64 → Base64URL还原JSON
👉 不校验签名，篡改后依旧可以解析

【解码‑带密钥校验】
Token分割 → headerB64.payloadB64
用密钥重新计算签名
  ├─签名不一致 → 篡改，拒绝
  └─签名一致 → 校验exp过期时间
        ├─过期 →拒绝
        └─未过期 →返回Payload
```