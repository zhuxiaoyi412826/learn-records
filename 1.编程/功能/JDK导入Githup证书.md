# 完整全流程（含导出证书 + 导入 JDK，一步不漏）

> ⚠️第一步：**完全关闭 SteamTools 代理 + Windows 系统代理**，否则拿到的是代理伪造证书，链条是假的。

## ① 浏览器导出 Sectigo 根证书（重点）

1. Edge 打开 `https://github.com`
2. 点地址栏🔒锁图标 → **连接安全** → **证书 (有效)**
3. 切换【详细信息】→【复制到文件】，格式选`Base‑64编码X.509(.CER)`，保存到 `D:\github‑tmp.crt`，关闭浏览器证书弹窗。
4. **去 D 盘，双击 `github‑tmp.crt` 文件（磁盘上的文件！不是浏览器弹窗）**，此时弹出系统证书窗口，出现【证书层次结构】标签。
5. 在层次结构选中**最顶层：`Sectigo Public Server Authentication Root E46`**
6. 点【查看证书】→【详细信息】→【复制到文件】
7. 格式：`Base‑64编码X.509(.CER)`，保存到： `D:\Sectigo Public Server Authentication Root E46.crt`

> ✅这个才是十几年有效期的根证书；前面的`github‑tmp.crt`是 github 叶子证书，直接删掉不要用。

## ② 管理员 PowerShell，执行导入命令

```
& "D:\software\jdk\jdk17\bin\keytool.exe" -importcert -alias sectigo_root_e46 -file "D:\Sectigo Public Server Authentication Root E46.crt" -keystore "D:\software\jdk\jdk17\lib\security\cacerts" -storepass changeit
```

输入 `y` 回车，输出**证书已添加到密钥库中**。

## ③ 校验导入是否成功

```
& "D:\software\jdk\jdk17\bin\keytool.exe" -list -keystore "D:\software\jdk\jdk17\lib\security\cacerts" -storepass changeit | findstr "sectigo_root_e46"
```

能打印别名，代表写入 cacerts 成功。

## ④ 重启 SpringBoot 项目

> JVM 启动才加载 cacerts，不重启等于没导入。

## ⑤ 测试现象

1. 不再报`PKIX path building failed`：证书握手成功。
2. 报连接超时：证书没问题，国内网络直连 github 不通，需要打开代理。

> ⚠️一旦打开 SteamTools 代理，**这个 github 根证书就失效，依旧报 PKIX，要改用 SteamTools 自己的根证书**。

## 卸载证书（备用）

```
& "D:\software\jdk\jdk17\bin\keytool.exe" -delete -alias sectigo_root_e46 -keystore "D:\software\jdk\jdk17\lib\security\cacerts" -storepass changeit
```

### 踩坑速记

1. ❌浏览器弹窗里直接导出，拿到的是叶子证书（2 个月过期），不能导入 JDK。
2. ✅必须**双击磁盘上的 crt 文件**，才能看到完整证书层次，导出顶层根证书。
3. ❌开代理导入 github 官方根证书，完全无效。
4. ❌普通 PowerShell，权限不足修改 cacerts 失败。
5. ❌导入完不重启 SpringBoot，配置不生效。