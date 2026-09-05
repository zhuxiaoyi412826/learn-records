# Trivy 完整总结

## 1. 是什么

**Trivy**：开源轻量安全扫描工具，Aqua Security 开发。

> 作用：扫描项目的**第三方依赖漏洞 (SCA)**、**密钥 / 敏感信息泄露 (Secret)**、容器镜像漏洞。 不需要安装数据库，开箱即用，支持本地、CI 流水线 (GitHub Actions/GitLab CI/Jenkins)。

- 项目地址：https://github.com/aquasecurity/trivy
- 授权：Apache‑2.0 开源免费

> 对比：SonarQube/SonarQube 主要扫**自己写的代码 bug、代码规范**；**Trivy 专门扫第三方依赖、密钥泄露**，二者互补。

## 2. 为什么要用（解决什么问题）

1. **依赖漏洞（SCA）**：pom.xml/maven 引入的 jar 包存在 CVE 高危漏洞，自己代码没问题，但引入的库有安全风险。
2. **密钥泄露扫描**：检测代码、编译产物中硬编码私钥、密码、token（比如你刚才遇到的 `.pem` 私钥打进 target 目录）。
3. **容器镜像扫描**：扫描 docker 镜像里面系统包、应用依赖漏洞。
4. **CI 流水线集成**：可以在构建阶段自动检测安全风险，阻止有高危漏洞的代码合并 / 发布。

> 你遇到的两个典型场景：
>
> - ① `target/classes/cert/apiclient_key.pem`：检测到私钥被打进编译产物，密钥泄露风险
> - ② pom.xml 76 个漏洞：netty、jackson、logback 等第三方 jar 包存在 CVE 漏洞

## 3. 怎么使用（分本地开发、CI 流水线）

### ① Windows 本地使用（免安装绿色版）

1. 下载：GitHub Releases 下载 `trivy_xxx_windows‑64bit.zip`，解压得到 `trivy.exe`
2. 放到项目目录，或者配置系统 PATH，全局调用。

#### 常用命令

```
# 扫描当前项目，检测依赖漏洞+密钥（maven项目）
trivy fs . --severity CRITICAL,HIGH --ignore-unfixed
#南京大学镜像 
trivy fs . `
--db-repository ghcr.nju.edu.cn/aquasecurity/trivy-db:2 `
--java-db-repository ghcr.nju.edu.cn/aquasecurity/trivy-java-db:1 `
--severity CRITICAL,HIGH `
--ignore-unfixed
# 扫描结构输出txt文件和json文件
trivy fs . --severity CRITICAL,HIGH --ignore-unfixed --scanners vuln -f json -o trivy-report.json && trivy fs . --severity CRITICAL,HIGH --ignore-unfixed --scanners vuln > trivy-report.txt

# 只扫描依赖漏洞，关闭密钥扫描（速度更快）
trivy fs . --severity CRITICAL,HIGH --ignore-unfixed --scanners vuln

# 发现高危漏洞时返回错误码（CI用）
trivy fs . --severity CRITICAL,HIGH --ignore-unfixed --exit-code 1
```

参数说明：

- `fs`：文件系统扫描（扫描本地项目目录，maven/pom）
- `--severity CRITICAL,HIGH`：只看严重、高危漏洞，过滤低危
- `--ignore-unfixed`：忽略**还没有修复补丁**的漏洞；已经有修复版本的依然会报
- `--exit-code 1`：发现高危漏洞，命令返回非 0，CI 会构建失败
- `--scanners vuln`：只扫描依赖漏洞，关闭 secret 密钥扫描，加快速度

#### 忽略规则

项目根目录新建 `.trivyignore`，填写需要忽略的文件 / CVE：

```
target/classes/cert/apiclient_key.pem
```

### ② CI 流水线（GitHub Actions 示例）

```
- name: Trivy扫描依赖
  uses: aquasecurity/trivy-action@v0.36.0
  with:
    scan-type: fs
    scan-ref: .
    severity: CRITICAL,HIGH
    ignore-unfixed: true
    # 学习项目建议去掉exit-code，不要阻断构建；生产环境开启
    # exit-code: "1"
    format: sarif
    output: trivy-result.sarif
```

### ③ 扫描 docker 镜像

```
trivy image myapp:latest --severity CRITICAL,HIGH
```

## 4. 项目实践建议（你的 SpringBoot 项目）

1. **本地开发**：执行 trivy 扫描，发现依赖漏洞、密钥泄露，及时处理；
2. **个人学习演示项目**：不开启`--exit-code 1`，只做风险提醒，不阻断构建；
3. **生产环境**：开启`--exit-code 1`，发现 CRITICAL/HIGH 直接阻断发布；
4. **配合 SonarQube**：Sonar 扫代码质量，Trivy 扫第三方依赖 + 密钥，两者一起使用。

## 5.Trivy 输出扫描结果到文件

**方式 1：文本格式（人类可读，和控制台输出一模一样）**

```
trivy fs . `
--db-repository ghcr.nju.edu.cn/aquasecurity/trivy-db:2 `
--java-db-repository ghcr.nju.edu.cn/aquasecurity/trivy-java-db:1 `
--severity CRITICAL,HIGH `
--ignore-unfixed `
--scanners vuln `
> trivy-scan-result.txt
```

> `>` 把全部输出重定向写入 `trivy-scan-result.txt`，会**覆盖旧文件**。 如果想追加内容用 `>>`。

**方式 2：JSON 格式（机器解析，CI 工具读取，推荐）**

```
trivy fs . `
--db-repository ghcr.nju.edu.cn/aquasecurity/trivy-db:2 `
--java-db-repository ghcr.nju.edu.cn/aquasecurity/trivy-java-db:1 `
--severity CRITICAL,HIGH `
--ignore-unfixed `
--scanners vuln `
-f json -o trivy-scan-result.json
```

- `-f json`：指定输出格式为 json
- `-o 文件名`：输出到指定文件

**方式 3：SARIF 格式（GitHub Actions 使用，可在 GitHub 页面展示漏洞）**

```
trivy fs . `
--db-repository ghcr.nju.edu.cn/aquasecurity/trivy-db:2 `
--java-db-repository ghcr.nju.edu.cn/aquasecurity/trivy-java-db:1 `
--severity CRITICAL,HIGH `
--ignore-unfixed `
--scanners vuln `
-f sarif -o trivy-result.sarif
```

------

**完整可直接复制（本地，输出 txt，只扫依赖漏洞）**

```
trivy fs . `
--db-repository ghcr.nju.edu.cn/aquasecurity/trivy-db:2 `
--java-db-repository ghcr.nju.edu.cn/aquasecurity/trivy-java-db:1 `
--severity CRITICAL,HIGH `
--ignore-unfixed `
--scanners vuln `
> trivy-scan-result.txt
```

执行完后，项目根目录生成 `trivy-scan-result.txt`，可以用记事本打开查看全部漏洞报告。

### 小提示

1. 如果加上 `--exit-code 1`，有高危漏洞时命令会返回错误码，**文件依然会生成**，只是 powershell 会报报错。
2. 如果你想同时在控制台打印 + 写入文件：PowerShell 可以用 `tee`

```
trivy fs . `
--db-repository ghcr.nju.edu.cn/aquasecurity/trivy-db:2 `
--java-db-repository ghcr.nju.edu.cn/aquasecurity/trivy-java-db:1 `
--severity CRITICAL,HIGH `
--ignore-unfixed `
--scanners vuln `
| tee trivy-scan-result.txt
```

> `tee`：屏幕输出同时保存到文件。

## 6. 常见坑

1. 国内网络首次运行下载漏洞数据库超时：使用国内镜像 `--db-repository ghcr.nju.edu.cn/aquasecurity/trivy-db:2`
2. `--ignore-unfixed` 不会忽略已经有修复版本的漏洞；**不要用`.trivyignore`去忽略 CVE 漏洞，优先升级依赖版本**。
3. 密钥文件不要放在`src/main/resources`，避免 maven 编译打进 jar 包。

如果你需要，我可以帮你整理一份简短笔记版，方便复制到你的项目文档。