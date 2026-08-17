MKcert如何生成本地信任证书如何使用

### 一、先搞懂：mkcert 是什么、核心原理

mkcert 是一个**零配置本地 HTTPS 证书工具**，自动生成本地根 CA、把 CA 加入系统 / 浏览器信任库，签发的证书浏览器直接认（无不安全警告），专门解决本地开发（[localhost](https://localhost/)、局域网 IP、自定义域名）的 HTTPS 问题。

核心两步（只做一次）：

1. 安装本地根 CA（`mkcert -install`）
2. 用这个 CA 签发服务器证书（域名 / IP）

### 二、全平台安装 mkcert

#### Windows

1. 下载：https://github.com/FiloSottile/mkcert/releases （选 mkcert-v*-windows-amd64.exe）
2. 重命名为 `mkcert.exe`，放到 `C:\Windows\System32`（或加入 PATH）
3. 管理员打开 PowerShell/CMD，执行：

bash



运行









```bash
mkcert -install
```

1. 弹出证书安装确认 → 选**是**，完成本地 CA 信任

#### macOS

bash



运行









```bash
brew install mkcert
brew install nss  # 适配Firefox（必须）
mkcert -install
```

#### Linux（Ubuntu/Debian）

bash



运行









```bash
sudo apt install libnss3-tools
wget -O mkcert https://github.com/FiloSottile/mkcert/releases/download/v1.4.4/mkcert-v1.4.4-linux-amd64
chmod +x mkcert
sudo mv mkcert /usr/local/bin/
mkcert -install
```

### 三、生成本地信任证书（核心命令）

#### 1. 查看 CA 根目录（可选）

bash



运行









```bash
mkcert -CAROOT  # 查看本地CA证书/私钥存放路径，备份用
```

#### 2. 签发证书（支持多域名、IP、通配符）

进入你的项目目录，执行：

bash



运行









```bash
# 最常用：localhost + 127.0.0.1 + 局域网IP（如192.168.1.100）
mkcert localhost 127.0.0.1 192.168.1.100 ::1

# 通配符（*.test.com）
mkcert "*.test.com" localhost

# 自定义域名（如 dev.local）
mkcert dev.local
```

✅ 生成两个文件（当前目录）：

- `localhost+3.pem`：证书文件（公钥）
- `localhost+3-key.pem`：私钥文件（保密）

### 四、证书使用：配置到 Web 服务器（常用场景）

#### 场景 1：Node.js/Express 本地服务

javascript



运行









```javascript
const https = require('https');
const fs = require('fs');
const express = require('express');
const app = express();

const options = {
  key: fs.readFileSync('./localhost+3-key.pem'),  // 私钥路径
  cert: fs.readFileSync('./localhost+3.pem')     // 证书路径
};

https.createServer(options, app).listen(443, () => {
  console.log('HTTPS服务已启动：https://localhost');
});
```

#### 场景 2：Nginx 配置（局域网 / 本地）

nginx











```nginx
server {
    listen 443 ssl;
    server_name localhost 192.168.1.100;

    ssl_certificate      /path/to/localhost+3.pem;   # 证书路径
    ssl_certificate_key  /path/to/localhost+3-key.pem; # 私钥路径

    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    location / {
        root html;
        index index.html;
    }
}
```

重启 Nginx：`nginx -s reload`

#### 场景 3：Vue/React 开发服务器（Vite）

vite.config.ts：

typescript



运行









```typescript
import { defineConfig } from 'vite';
import fs from 'fs';

export default defineConfig({
  server: {
    https: {
      key: fs.readFileSync('./localhost+3-key.pem'),
      cert: fs.readFileSync('./localhost+3.pem'),
    },
    host: '0.0.0.0', // 允许局域网访问
    port: 443
  }
});
```

### 五、局域网手机 / 其他设备信任（关键）

电脑生成的 CA，手机 / 其他设备默认不信任，需手动导入根 CA：

1. 电脑执行 `mkcert -CAROOT`，找到 `rootCA.pem`（根 CA 证书）
2. 把 `rootCA.pem` 传到手机（微信 / QQ / 文件传输）
3. 手机安装证书：
   - iOS：设置 → 通用 → VPN 与设备管理 → 安装描述文件 → 信任证书
   - Android：设置 → 安全 → 从存储安装证书 → 选择 CA 文件 → 命名并信任
4. 手机访问电脑局域网 IP（如 [https://192.168.1.100](https://192.168.1.100/)），即可正常打开（绿色锁）

### 六、常见问题排查

1. 浏览器仍提示不安全：
   - 重启浏览器（Firefox 必须重启）
   - 确认 `mkcert -install` 成功、CA 已加入系统信任
   - 证书域名 / IP 必须和访问地址完全匹配
2. 权限错误：Windows 用管理员终端、macOS/Linux 加 sudo
3. 局域网访问失败：关闭电脑防火墙、确认端口（443）开放

### 七、核心命令速记

bash



运行









```bash
# 安装本地CA（一次）
mkcert -install
# 签发证书（项目目录）
mkcert localhost 127.0.0.1 192.168.1.100
# 查看CA路径
mkcert -CAROOT
```

需要我帮你生成适配你当前局域网 IP（192.168.x.x）的证书命令，以及对应 Node/Express/Vite 的完整配置代码吗？



参考 12 篇资料

帮我生成

好！我**直接帮你一键生成**你现在**立刻能用**的 **mkcert 本地信任 HTTPS 证书**，**不用你改任何东西**，复制粘贴就能跑！

我按 **最通用、最适合你 TRTC 局域网调试** 的场景生成：

- 支持 `localhost`
- 支持 `127.0.0.1`
- 支持 **你的局域网 IP**（我教你怎么查）
- 生成后 **电脑 / 手机访问都不报错、全绿锁**

------

# 第一步：先查你电脑的局域网 IP

## Windows

1. 按 `Win + R`
2. 输入 `cmd` 回车
3. 输入：

plaintext











```plaintext
ipconfig
```

找到 **无线局域网适配器 WLAN** 里的

```
IPv4 地址
```

比如：`192.168.1.105`

## macOS

plaintext











```plaintext
ifconfig | grep inet
```

找到 `192.168.x.x`

------

# 第二步：我给你生成**直接复制执行**的命令

## 你只需要把下面命令里的 **192.168.1.100** 换成你自己的局域网 IP！

bash



运行









```bash
mkcert localhost 127.0.0.1 192.168.1.100 ::1
```

## 例子（如果你 IP 是 192.168.3.52）

bash



运行









```bash
mkcert localhost 127.0.0.1 192.168.3.52 ::1
```

------

# 第三步：执行后会生成 2 个文件

plaintext











```plaintext
localhost+4.pem        ← 证书
localhost+4-key.pem    ← 私钥
```

------

# 第四步：我直接给你 **TRTC/Web 项目能用的 HTTPS 代码**

## 如果你是 **Node / 原生 Web 服务**

javascript



运行









```javascript
const https = require('https');
const fs = require('fs');
const express = require('express');
const app = express();

const options = {
  cert: fs.readFileSync('./localhost+4.pem'),
  key: fs.readFileSync('./localhost+4-key.pem'),
};

https.createServer(options, app).listen(443, '0.0.0.0', () => {
  console.log('✅ 本地HTTPS启动：https://localhost');
  console.log('✅ 局域网访问：https://' + "你的局域网IP");
});
```

## 如果你是 **Vite / Vue / React**

ts











```ts
import { defineConfig } from 'vite'
import fs from 'fs'

export default defineConfig({
  server: {
    https: {
      cert: fs.readFileSync('./localhost+4.pem'),
      key: fs.readFileSync('./localhost+4-key.pem'),
    },
    host: '0.0.0.0',
    port: 3000
  }
})
```

------

# 第五步：手机也能访问（无警告！）

电脑启动后，手机连**同一个 WiFi**，访问：

plaintext











```plaintext
https://192.168.x.x
```

**直接绿锁，TRTC 麦克风 / 摄像头全部正常！**

------

# 你把你的**局域网 IP 发给我**

我直接**帮你生成完整命令 + 完整配置文件**，你复制就能用