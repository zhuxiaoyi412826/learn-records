# Spring Cloud Alibaba 微服务企业级 CI/CD 全链路落地指南

> 技术基线:Spring Cloud Alibaba(Nacos / Sentinel / Seata / RocketMQ / Gateway / OpenFeign)+ Spring Boot
> 工具链:GitLab MR 评审 → Jenkins → Nexus Maven 私服 → SonarQube → 安全扫描四件套 → Harbor → Helm + Argo CD → Vault / Sealed Secrets → Terraform / Ansible → Prometheus / Grafana / Loki / SkyWalking / Alertmanager

---

## 1. 总体架构

### 1.1 业务侧技术栈基线

CI/CD 工具链的每个环节都围绕 Spring Cloud Alibaba 的运行时特性设计:Nacos 既是注册中心也是配置中心,意味着"配置发布"是流水线的一个独立环节;Sentinel 规则、Seata 事务组、RocketMQ 消费组都存放在 Nacos,环境隔离靠 namespace 实现;SkyWalking Agent 以旁挂方式注入 JVM,要求镜像构建阶段预置 agent 目录。

典型工程结构(Maven 多模块):

```
mall-platform
├── mall-gateway          # Spring Cloud Gateway 网关
├── mall-user             # 用户服务(仅依赖 Nacos)
├── mall-order            # 订单服务(依赖 Seata、RocketMQ)
├── mall-stock            # 库存服务
├── mall-common           # 公共 DTO / 工具,不参与部署
└── pom.xml               # 父 POM,统一依赖版本管理
```

可部署单元是 gateway / user / order / stock 四个模块,每个模块独立构建镜像、独立 Helm Chart、独立发布节奏——这是后文所有工具配置的出发点。

### 1.2 全链路流程总览

```
开发者 ──push──▶ GitLab(分支保护 / MR 评审 / CODEOWNERS)
                    │ merge to master 或打 tag
                    ▼
              GitLab Webhook ──▶ Jenkins(Master 调度 + K8s 动态 Agent)
                    │
                    ├─① Gitleaks / TruffleHog 密钥泄露扫描
                    ├─② Maven 编译 + JUnit 单测 + JaCoCo 覆盖率(依赖走 Nexus 私服)
                    ├─③ SonarQube 静态扫描 + 质量门禁(失败即中止)
                    ├─④ OWASP Dependency-Check / Trivy fs / Snyk 依赖漏洞扫描
                    ├─⑤ Docker 多阶段构建镜像
                    ├─⑥ Trivy 镜像扫描(高危阻断)
                    ├─⑦ 推送 Harbor(机器人账号 + 镜像签名)
                    └─⑧ 修改 GitOps 仓库 Helm values(镜像 tag 由流水线写入)
                              │
                              ▼
                    Argo CD 监听 GitOps 仓库 → dev/test 环境自动同步
                              │ 生产环境:人工审批(Argo CD manual sync / Sync Window)
                              ▼
                    Helm 渲染 → K8s 发布(金丝雀 / 蓝绿 / 滚动)
                              │
                              ▼
                    Prometheus + Grafana / Loki / SkyWalking 采集验证
                              │ 指标异常
                              ▼
                    Alertmanager → 飞书 / 钉钉 / 企微 Webhook 告警 → 自动回滚
```

整条链路有两条不变的纪律:**一次构建,处处部署**(dev/test/prod 使用同一个镜像 tag,只通过 values 差异化配置);**制品不可变**(镜像推送到 Harbor 后设置不可变 tag,回滚等价于 git revert values 变更)。

---

## 2. 版本控制与代码评审(GitLab)

### 2.1 分支模型

高频发布的微服务团队采用"主干开发 + release 分支"而非完整 Git Flow:

| 分支 | 用途 | 保护规则 | 是否直接部署 |
|---|---|---|---|
| `master` | 日常集成分支 | 禁止 push,仅 MR 合入 | 部署 dev |
| `release/x.y` | 版本稳定分支 | 禁止 push,仅 cherry-pick | 部署 staging |
| `hotfix/*` | 生产缺陷修复 | MR 合入 master + release | 紧急通道 |
| `feature/*` | 功能开发 | 无 | 否 |

tag 命名 `v{版本}-{服务名}-{构建号}`,例如 `v1.4.0-mall-order-142`,该值同时用作镜像 tag 与 Helm values 的 `image.tag`,三处一致保证可追溯。

### 2.2 MR 评审与保护分支

GitLab 项目设置(Repository → Protected Branches / Settings → Merge Requests):

- `master` 设置 Merge request approvals **≥ 2**,批准规则绑定 CODEOWNERS;
- 开启 **Pipelines must succeed**(质量门禁前置到 MR 阶段);
- 开启 **All threads resolved**(评审意见必须处理完);
- 合并策略选 Merge commit with semi-linear history,保持历史可读。

`CODEOWNERS` 放在仓库根目录或 `.gitlab/` 下:

```
# CODEOWNERS
*                          @mall-platform/dev-team
mall-order/**              @mall-platform/order-owners
mall-common/**             @mall-platform/architects
build/**                   @mall-platform/devops
```

改动订单服务代码时自动追加订单负责人审批,DevOps 配置(build/ 目录)必须平台组确认,避免流水线被随意篡改。

### 2.3 MR 阶段流水线(GitLab CI 承担轻量检查)

Jenkins 负责重量级构建,GitLab CI 只做"提交即反馈"的轻检查(约 2 分钟内出结果),`.gitlab-ci.yml`:

```yaml
stages: [lint, secret]

variables:
  MAVEN_OPTS: "-Dmaven.repo.local=$CI_PROJECT_DIR/.m2/repository"

gitleaks:
  stage: secret
  image:
    name: zricethezav/gitleaks:latest
    entrypoint: [""]
  script:
    - gitleaks detect --source . --redact -v
  rules:
    - if: $CI_PIPELINE_SOURCE == "merge_request_event"

compile-check:
  stage: lint
  image: maven:3.9-eclipse-temurin-17
  cache:
    key: maven-$CI_COMMIT_REF_SLUG
    paths: [.m2/repository]
  script:
    - mvn -B -s settings.xml compile -DskipTests
  rules:
    - if: $CI_PIPELINE_SOURCE == "merge_request_event"
```

MR 合入 `master` 后,由 GitLab Webhook(Settings → Webhooks,触发 Merge request events / Tag push events)通知 Jenkins 启动完整构建。这样评审阶段反馈快,合入后重型流水线不再重复轻检查。

---

## 3. CI 引擎:Jenkins 流水线

### 3.1 Jenkins 架构

Master 只做调度与 UI,所有构建任务跑在 Kubernetes 动态 Agent 上(每条流水线临时拉起 Pod,结束后销毁,天然隔离)。云原生动态 Agent 通过 `Kubernetes plugin` 实现,Agent Pod 模板写在流水线内,与流水线同版本管理。

Agent Pod 需要的容器:

| 容器 | 镜像 | 用途 |
|---|---|---|
| jnlp | jenkins/inbound-agent | Jenkins 通信 |
| maven | maven:3.9-eclipse-temurin-17 | 编译 / 单测 / Sonar 扫描 |
| docker | docker:24-cli | 镜像构建(docker.sock 挂载宿主) |
| trivy | aquasec/trivy:latest | fs / 镜像扫描 |
| kubectl | bitnami/kubectl | 更新 GitOps 仓库 |

### 3.2 凭证管理(Jenkins Credentials)

进入 Manage Jenkins → Credentials,提前创建:

| ID | 类型 | 内容 |
|---|---|---|
| `gitlab-robot` | Username with password | 触发构建 / 拉代码 |
| `gitops-deploy-key` | SSH Username with private key | 写 GitOps 仓库 |
| `harbor-robot` | Username with password | Harbor 机器人账号 |
| `sonar-token` | Secret text | SonarQube 分析令牌 |
| `nvd-api-key` | Secret text | NVD 漏洞库 API Key |
| `feishu-webhook` | Secret text | 流水线结果通知 |

凭证在流水线中以 `credentials('id')` 引用,任何密码不落 Jenkinsfile——文件本身在 Git 里,明文凭证等于泄露。

### 3.3 完整 Jenkinsfile

`build/Jenkinsfile` 放在各服务仓库(或独立流水线仓库统一维护):

```groovy
pipeline {
  agent {
    kubernetes {
      yamlFile 'build/jenkins-agent-pod.yaml'
      defaultContainer 'maven'
    }
  }

  options {
    timestamps()
    timeout(time: 40, unit: 'MINUTES')
    buildDiscarder(logRotator(numToKeepStr: '50'))
    disableConcurrentBuilds()
  }

  environment {
    SERVICE     = 'mall-order'
    HARBOR      = 'harbor.mall.local'
    PROJECT     = 'mall'
    IMAGE       = "${HARBOR}/${PROJECT}/${SERVICE}"
    GITOPS_REPO = 'git@gitlab.mall.local:devops/gitops-manifests.git'
    DOCKER_CONFIG = '/kaniko/.docker'
  }

  parameters {
    choice(name: 'DEPLOY_ENV', choices: ['dev', 'test', 'staging', 'prod'],
           description: '目标环境')
    string(name: 'IMAGE_TAG', defaultValue: '', description: '留空则自动生成')
  }

  stages {

    stage('Checkout') {
      steps {
        checkout scm
        script {
          env.GIT_TAG = params.IMAGE_TAG?.trim() ?:
            "v${readMavenPom().version}-${SERVICE}-${env.BUILD_NUMBER}"
        }
      }
    }

    stage('Gitleaks Secret Scan') {
      steps {
        container('gitleaks') {
          sh 'gitleaks detect --source . --redact --exit-code 1'
        }
      }
    }

    stage('Build & Unit Test') {
      steps {
        sh 'mvn -B -s build/settings.xml -pl ${SERVICE} -am verify'
        junit '**/target/surefire-reports/*.xml'
        recordCoverage(tools: [[parser: 'JACOCO',
          pattern: "${SERVICE}/target/site/jacoco/jacoco.xml",
          qualityGates: [[threshold: 60.0, metric: 'LINE', criticality: 'FAILURE']]]])
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('mall-sonar') {
          sh '''mvn -B -s build/settings.xml -pl ${SERVICE} -am \\
                 org.sonarsource.scanner.maven:sonar-maven-plugin:3.11.0.3922:sonar \\
                 -Dsonar.projectKey=mall:${SERVICE} \\
                 -Dsonar.coverage.jacoco.xmlReportPaths=**/jacoco.xml'''
        }
      }
    }

    stage('Quality Gate') {
      steps {
        timeout(time: 10, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }

    stage('Dependency Vulnerability Scan') {
      steps {
        sh '''mvn -B -s build/settings.xml -pl ${SERVICE} -am \\
               org.owasp:dependency-check-maven:12.1.0:check \\
               -DnvdApiKey=${NVD_API_KEY} \\
               -DfailBuildOnCVSS=7 \\
               -DsuppressionFiles=build/dependency-suppression.xml'''
        archiveArtifacts artifacts: '**/dependency-check-report.html',
                          allowEmptyArchive: true
      }
    }

    stage('Docker Build') {
      steps {
        container('docker') {
          script {
            env.IMAGE_REF = "${IMAGE}:${GIT_TAG}"
            sh 'docker build -f build/Dockerfile -t ${IMAGE_REF} ${SERVICE}/'
          }
        }
      }
    }

    stage('Image Vulnerability Scan') {
      steps {
        container('trivy') {
          sh '''trivy image --exit-code 1 --severity CRITICAL,HIGH \\
                  --ignore-unfixed --ignorefile build/.trivyignore ${IMAGE_REF}'''
        }
      }
    }

    stage('Push Harbor') {
      steps {
        container('docker') {
          withCredentials([usernamePassword(credentialsId: 'harbor-robot',
              usernameVariable: 'H_USER', passwordVariable: 'H_PASS')]) {
            sh 'echo "$H_PASS" | docker login ${HARBOR} -u "$H_USER" --password-stdin'
            sh 'docker push ${IMAGE_REF}'
            sh 'docker push ${IMAGE%%:*}:${GIT_TAG%%-*} || true'  // 同时推送主版本
          }
        }
      }
    }

    stage('Update GitOps Repo') {
      when { expression { params.DEPLOY_ENV != 'none' } }
      steps {
        container('kubectl') {
          withCredentials([sshUserPrivateKey(credentialsId: 'gitops-deploy-key',
              keyFileVariable: 'SSH_KEY')]) {
            sh '''
              export GIT_SSH_COMMAND="ssh -i $SSH_KEY -o StrictHostKeyChecking=no"
              git clone --depth 1 ${GITOPS_REPO} /tmp/gitops
              cd /tmp/gitops
              yq -i '.image.tag = strenv(GIT_TAG) |
                     .image.digest = ""' \
                 env/${DEPLOY_ENV}/${SERVICE}/values.yaml
              git config user.email "jenkins@mall.local"
              git config user.name "jenkins-bot"
              git add -A && git commit -m "deploy ${SERVICE} ${GIT_TAG} to ${DEPLOY_ENV}"
              git push origin main
            '''
          }
        }
      }
    }
  }

  post {
    failure {
      sh 'curl -s -X POST -H "Content-Type: application/json" \
           -d \'{"msg_type":"text","content":{"text":"构建失败: '"${env.JOB_NAME} #${env.BUILD_NUMBER}"'"}}\' \
           $FEISHU_WEBHOOK'
    }
  }
}
```

`build/jenkins-agent-pod.yaml`:

```yaml
apiVersion: v1
kind: Pod
metadata:
  labels:
    jenkins-build: "true"
spec:
  serviceAccountName: jenkins-agent
  containers:
    - name: jnlp
      image: jenkins/inbound-agent:latest
    - name: maven
      image: maven:3.9-eclipse-temurin-17
      command: ["sleep"]
      args: ["infinity"]
      volumeMounts:
        - { name: mvn-cache,  mountPath: /root/.m2/repository }
    - name: docker
      image: docker:24-cli
      command: ["sleep"]
      args: ["infinity"]
      securityContext: { privileged: true }
      volumeMounts:
        - { name: docker-sock, mountPath: /var/run/docker.sock }
    - name: trivy
      image: aquasec/trivy:latest
      command: ["sleep"]
      args: ["infinity"]
    - name: kubectl
      image: bitnami/kubectl:latest
      command: ["sleep"]
      args: ["infinity"]
    - name: gitleaks
      image: zricethezav/gitleaks:latest
      command: ["sleep"]
      args: ["infinity"]
  volumes:
    - name: mvn-cache
      persistentVolumeClaim: { claimName: jenkins-maven-cache }
    - name: docker-sock
      hostPath: { path: /var/run/docker.sock }
```

Maven 本地仓库挂 PVC,跨构建复用依赖缓存,首次全量下载后单服务构建时间从 8 分钟降到 90 秒左右。

---

## 4. 构建与制品管理(Maven 私服 Nexus)

### 4.1 仓库规划

Nexus 部署为独立节点(3.68.x),repo 规划:

| 仓库 | 类型 | 用途 |
|---|---|---|
| `mall-hosted` | hosted(release) | 公司内部二方包发布 |
| `mall-snapshot` | hosted(snapshot) | 内部 SNAPSHOT |
| `aliyun-proxy` | proxy | 代理阿里云 Maven 中央仓库 |
| `spring-proxy` | proxy | 代理 Spring 官方仓库 |
| `mall-group` | group | 聚合以上全部,业务工程唯一入口 |

### 4.2 settings.xml

`build/settings.xml` 随仓库提交(其中不含明文密码,部署用户密码由 Jenkins Credential 注入后覆写):

```xml
<settings>
  <mirrors>
    <mirror>
      <id>mall-group</id>
      <mirrorOf>*</mirrorOf>
      <url>https://nexus.mall.local/repository/mall-group/</url>
    </mirror>
  </mirrors>
  <servers>
    <server>
      <id>mall-group</id>
      <username>${env.NEXUS_USER}</username>
      <password>${env.NEXUS_PASS}</password>
    </server>
  </servers>
  <profiles>
    <profile>
      <id>mall</id>
      <repositories>
        <repository>
          <id>mall-group</id>
          <url>https://nexus.mall.local/repository/mall-group/</url>
          <releases><enabled>true</enabled></releases>
          <snapshots><enabled>true</enabled></snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>
  <activeProfiles><activeProfile>mall</activeProfile></activeProfiles>
</settings>
```

### 4.3 父 POM 依赖锁定

父 POM 用 `dependencyManagement` 统一 Spring Cloud Alibaba 版本,所有子模块只声明 groupId/artifactId 不写版本,消除版本漂移:

```xml
<properties>
  <spring-cloud.version>2023.0.3</spring-cloud.version>
  <spring-cloud-alibaba.version>2023.0.3.2</spring-cloud-alibaba.version>
  <spring-boot.version>3.2.9</spring-boot.version>
</properties>

<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-dependencies</artifactId>
      <version>${spring-boot.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-dependencies</artifactId>
      <version>${spring-cloud.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
    <dependency>
      <groupId>com.alibaba.cloud</groupId>
      <artifactId>spring-cloud-alibaba-dependencies</artifactId>
      <version>${spring-cloud-alibaba.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

内部二方包发布用 `mvn deploy`,SNAPSHOT 保留 30 天自动清理(Nexus Cleanup Policy),release 仓库禁止 redeploy。

---

## 5. 测试与质量门禁

### 5.1 单元测试与覆盖率(JaCoCo)

父 POM 配置 Surefire 与 JaCoCo,行覆盖率低于 60% 直接失败构建:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <configuration>
    <skipTests>false</skipTests>
    <includes><include>**/*Test.java</include></includes>
  </configuration>
</plugin>

<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.12</version>
  <executions>
    <execution>
      <id>prepare-agent</id>
      <goals><goal>prepare-agent</goal></goals>
    </execution>
    <execution>
      <id>check</id>
      <goals><goal>check</goal></goals>
      <configuration>
        <rules>
          <rule>
            <element>BUNDLE</element>
            <limits>
              <limit>
                <counter>LINE</counter>
                <value>COVEREDRATIO</value>
                <minimum>0.60</minimum>
              </limit>
            </limits>
          </rule>
        </rules>
      </configuration>
    </execution>
  </executions>
</plugin>
```

微服务场景的常见取舍:DDD 分层后只对 service / domain 层强制门禁,controller 与 mapper 层走集成测试覆盖,通过 `configuration` 的 includes/excludes 调整统计范围。

### 5.2 SonarQube 集成

SonarQube 9.9 LTS 部署于独立节点,与 PostgreSQL 配套。项目级 `sonar-project.properties`:

```properties
sonar.projectKey=mall:mall-order
sonar.sources=src/main/java
sonar.java.binaries=target/classes
sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
sonar.exclusions=**/dto/**,**/entity/**,**/*Application.java
sonar.issue.ignore.multicriteria=e1
```

质量门禁(SonarQube → Quality Gates,Mall Gate):

| 条件 | 阈值 | 失败级别 |
|---|---|---|
| 新代码覆盖率 | < 60% | Error |
| 新代码重复率 | > 3% | Error |
| 新代码 smell 评级 | < A | Error |
| 新代码安全评级 | < A | Error |
| Blocker / Critical 问题 | > 0 | Error |

Jenkins 安装 SonarQube Scanner 插件后,`waitForQualityGate` 通过 SonarQube Webhook(SonarQube → Administration → Webhooks,URL 指向 `http://jenkins.mall.local/sonarqube-webhook/`)异步回传门禁结果,失败时流水线立即中止,镜像不会进入 Harbor。

---

## 6. 安全扫描体系(DevSecOps)

### 6.1 密钥泄露:Gitleaks / TruffleHog

Gitleaks 扫描全部提交历史与工作区,配置文件 `.gitleaks.toml` 定义额外规则与白名单:

```toml
title = "mall gitleaks config"

[extend]
useDefault = true

[[rules]]
id = "nacos-config-password"
description = "Nacos config password in properties"
regex = '''spring\.cloud\.nacos\.(config|discovery)\.password\s*=\s*\S+'''

[[allowlists]]
description = "test resources are allowed"
paths = ['''src/test/resources/.*''']
regexes = ['''(password|secret)\s*=\s*\$\{.*\}''']  # 占位符允许
```

TruffleHog 作为补充工具,专攻"验证型"检测——发现疑似密钥后会调用厂商 API 验证该密钥是否仍然存活,只有真实生效的凭证才告警,大幅降低误报:

```bash
trufflehog git file://./ --only-verified \
  --exclude-paths=.trufflehog-exclude.txt
```

两个工具的分工:Gitleaks 阻断提交(快、全量规则、误报可白名单),TruffleHog 在每日定时流水线做全仓库深度审计(慢、只报已验证的活密钥)。

### 6.2 依赖漏洞:OWASP Dependency-Check / Trivy / Snyk

**OWASP Dependency-Check**(Maven 插件形式集成在 Jenkinsfile 中,见 3.3 节):匹配 NVD 与 Sonatype OSS Index 漏洞库,`-DfailBuildOnCVSS=7` 表示 CVSS ≥ 7 的漏洞直接失败构建。误报通过 suppression 文件豁免:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<suppressions xmlns="https://jeremylong.github.io/DependencyCheck/dependency-suppression.1.3.xsd">
  <suppress>
    <notes>H2 数据库仅测试范围使用,不受 CVE-2021-42392 影响</notes>
    <gav regex="true">^com\.h2database:h2:.*$</gav>
    <cve>CVE-2021-42392</cve>
  </suppress>
</suppressions>
```

注意:2023 年底后 NVD API 强制要求 API Key,免费额度限速严重,首次全量下载漏洞库可能需要 1 小时以上,应使用 `dependency-check-cache` PVC 持久化 NVD 数据库并每日定时更新。

**Trivy fs 模式**(与镜像扫描同一工具,降低工具链复杂度):

```bash
trivy fs --scanners vuln --severity CRITICAL,HIGH \
  --exit-code 1 --ignore-unfixed \
  --ignorefile build/.trivyignore .
```

**Snyk**(商业方案,AST 级可达性分析是核心卖点——它判断漏洞代码路径是否真的会被应用调用,而不是只看版本号):

```bash
snyk test --org=mall-platform --fail-on=high \
  --policy-file=build/.snyk-policy
snyk monitor --org=mall-platform   # 结果上报门户,跟踪修复进度
```

三者的定位:Dependency-Check 是自建开源基线;Trivy 统一 fs 与镜像两层扫描;Snyk 在漏洞噪声大到团队无法消化时引入,用可达性分析把"300 个告警"收敛到"12 个真正可达的"。

### 6.3 镜像扫描:Trivy + Harbor 集成

除了 CI 阶段阻断,Harbor 侧再设一道准入扫描(Harbor 2.x 原生集成 Trivy adapter,替代老版本 Clair;若历史环境仍用 Clair,能力等价)。项目设置 Project → Configuration:

- **Automatically scan images on push**:开启,推送即扫描;
- **Prevent images from last scan with a severity of "High" from being pulled**:开启,高危镜像禁止拉取——即使 CI 被绕过,Ar go CD 同步时 kubelet 拉镜像失败,部署仍然被拦截;
- **Prevent vulnerable images from being pushed**:视团队容忍度,开启后 CI 推送即失败。

CI 扫描与 Harbor 扫描的差异:CI 里的 Trivy 用 `--ignorefile` 精细豁免并阻断构建;Harbor 扫描是平台级兜底,策略面向整个项目而非单条流水线。

### 6.4 动态扫描(DAST):OWASP ZAP

staging 环境部署完成后,ZAP 基线扫描通过网关对外暴露的接口做黑盒探测:

```groovy
stage('DAST - ZAP Baseline') {
  when { expression { params.DEPLOY_ENV == 'staging' || params.DEPLOY_ENV == 'dev' } }
  steps {
    container('docker') {
      sh '''
        docker run --rm -v $(pwd)/zap:/zap/wrk/:rw \
          ghcr.io/zaproxy/zaproxy:stable zap-baseline.py \
          -t https://staging-gw.mall.local/actuator/health \
          -c build/zap-rules.conf -J zap-report.json -I
      '''
      archiveArtifacts artifacts: 'zap/zap-report.json'
    }
  }
}
```

`zap-rules.conf` 自定义规则基线,例如对网关路由 `FAIL` 级别的响应头检查:

```
# ruleId  threshold  param
10015     WARN       /actuator/health       # 响应头缺失告警
10038     FAIL       /api/order/**          # CSP 头缺失失败
```

DAST 结果不阻断生产发布(全量 API 的动态扫描耗时不可控),而是作为每日定时任务在 staging 巡检,高危发现进入漏洞工单流程。

### 6.5 安全门禁阈值总表

| 扫描类型 | 工具 | 阻断阈值 | 作用阶段 |
|---|---|---|---|
| 密钥泄露 | Gitleaks | 任何命中即失败 | 提交前 + CI |
| 密钥泄露(验证) | TruffleHog | 已验证活密钥告警 | 每日定时 |
| 依赖漏洞 | Dependency-Check | CVSS ≥ 7 | CI |
| 依赖/镜像漏洞 | Trivy | CRITICAL + HIGH(未修复) | CI |
| 依赖漏洞(可达性) | Snyk | High | CI(可选) |
| 镜像准入 | Harbor 集成扫描 | HIGH 禁止拉取 | 部署时 |
| DAST | OWASP ZAP | FAIL 级规则 | staging 部署后 + 每日 |

---

## 7. 制品与镜像管理(Harbor)

### 7.1 项目规划与 RBAC

Harbor 2.x 部署在 `harbor.mall.local`,仓库规划按"团队"而非"环境"组织,环境差异交给 Argo CD 的 Application 边界:

| Harbor 项目 | 内容 | 成员模型 |
|---|---|---|
| `mall` | 业务微服务镜像 | 流水线机器人账号可推,所有人可拉 |
| `mall-thirdparty` | 中间件 / 基础镜像 | 仅 DevOps 可推 |
| `mall-archive` | 归档镜像 | 只读 |

推送账号一律使用 **Robot Account**(项目 → Robot Accounts),权限收敛为 `push/pull` 单仓库,账号密码仅存 Jenkins Credentials。人工账号不参与任何流水线,审计日志里所有 push 都能映射到某条 Jenkins 构建。

### 7.2 镜像不可变与保留策略

tag 不可变(Project → Configuration → Tag Immutability):

```
匹配规则:** 策略:不可变
```

推送后同 tag 二次覆盖被 Harbor 拒绝,从机制上保证"同一 tag = 同一二进制"。保留策略(Retention)按天数与数量组合:保留最近 30 天内 pushed 的镜像,且每条 main tag(`v1.4.0-*` 的稳定版本)永久保留,历史镜像总量稳定在可控规模。

### 7.3 多环境复制与灾备

跨数据中心用 Harbor Replication:主 Harbor → 灾备 Harbor 定时全量复制(带宽允许时用基于事件的实时复制),生产集群的 kubelet 只从本机房 Harbor 拉镜像,避免跨机房拉取导致的发布抖动。

### 7.4 镜像构建规范

`build/Dockerfile` 采用多阶段构建 + distroless 基础镜像,并预置 SkyWalking Agent:

```dockerfile
# 阶段一:构建(Jenkins Agent 已有 Maven 缓存,此阶段可跳过)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY pom.xml .
COPY mall-common ./mall-common
COPY mall-order ./mall-order
RUN mvn -B -s settings.xml -pl mall-order -am package -DskipTests

# 阶段二:运行时
FROM eclipse-temurin:17-jre-jammy
RUN apt-get update && apt-get install -y --no-install-recommends curl tini \
    && rm -rf /var/lib/apt/lists/*
COPY --from=skywalking-agent /skywalking-agent /skywalking-agent
COPY --from=build /src/mall-order/target/*.jar /app/app.jar
EXPOSE 8080 9000
ENTRYPOINT ["/usr/bin/tini", "--", "java", \
  "-javaagent:/skywalking-agent/skywalking-agent.jar", \
  "-XX:MaxRAMPercentage=75", "-XX:+UseG1GC", \
  "-jar", "/app/app.jar"]
```

SkyWalking agent 层单独 COPY,基础镜像与 agent 都不变时该层走缓存,构建耗时由镜像大小主导的推送时间而非下载时间决定。

---

## 8. 部署交付(Helm + Argo CD)

### 8.1 GitOps 仓库结构

独立仓库 `gitops-manifests`,与业务代码仓库分离,DevOps 与研发均可发起 MR 审阅变更:

```
gitops-manifests/
├── bootstrap/
│   └── root-app.yaml              # App of Apps 根应用
├── projects/
│   └── mall-project.yaml          # AppProject(白名单集群/仓库)
├── apps/
│   ├── dev/{gateway,user,order,stock}/values.yaml
│   ├── test/{gateway,user,order,stock}/values.yaml
│   ├── staging/{...}/values.yaml
│   └── prod/{...}/values.yaml
└── charts/                        # 软链或 CI 同步自业务仓库的 Chart 版本
    └── mall-service/Chart.yaml    # 每服务一份通用 Chart
```

关键设计:**通用 Chart 单份维护**,四个微服务共享 `charts/mall-service`,服务差异全部通过 values 表达。新增微服务只需在 `apps/<env>/` 下添加一个 values 文件,不必复制整套模板。

### 8.2 通用 Chart 核心模板

`charts/mall-service/values.yaml`:

```yaml
nameOverride: ""
image:
  repository: harbor.mall.local/mall/mall-order
  tag: "v1.4.0"
  pullPolicy: IfNotPresent

replicaCount: 2
resources:
  requests: { cpu: 500m, memory: 1Gi }
  limits:   { cpu: "2",  memory: 2Gi }

skywalking:
  enabled: true
  oap: skywalking-oap.observability:11800

nacos:
  serverAddr: nacos.mall-system:8848
  namespace: dev

metrics:
  serviceMonitor:
    enabled: true
    interval: 15s

probes:
  liveness:  /actuator/health/liveness
  readiness: /actuator/health/readiness

ingress:
  enabled: false
```

`templates/deployment.yaml` 核心片段:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ include "mall-service.fullname" . }}
  labels: {{- include "mall-service.labels" . | nindent 4 }}
spec:
  replicas: {{ .Values.replicaCount }}
  selector:
    matchLabels: {{- include "mall-service.selectorLabels" . | nindent 6 }}
  template:
    metadata:
      annotations:
        checksum/config: {{ .Values.nacos.namespace | sha256sum }}
    spec:
      serviceAccountName: {{ .Values.nameOverride }}
      containers:
        - name: app
          image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
          env:
            - name: SW_AGENT_NAME
              value: {{ .Release.Name }}{{ if .Values.skywalking.enabled }}{{ end }}
            - name: SW_AGENT_COLLECTOR_BACKEND_SERVICES
              value: {{ .Values.skywalking.oap | quote }}
            - name: SPRING_CLOUD_NACOS_SERVER_ADDR
              value: {{ .Values.nacos.serverAddr | quote }}
            - name: SPRING_CLOUD_NACOS_NAMESPACE
              value: {{ .Values.nacos.namespace | quote }}
          ports:
            - { name: http,   containerPort: 8080 }
            - { name: act,    containerPort: 9000 }
          readinessProbe:
            httpGet: { path: {{ .Values.probes.readiness }}, port: http }
            initialDelaySeconds: 30
          livenessProbe:
            httpGet: { path: {{ .Values.probes.liveness }}, port: http }
            initialDelaySeconds: 60
          resources: {{- toYaml .Values.resources | nindent 12 }}
```

### 8.3 Argo CD Application

`bootstrap/root-app.yaml`(App of Apps 模式,一个根应用管理所有环境应用):

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: mall-root
  namespace: argocd
spec:
  project: mall-project
  source:
    repoURL: git@gitlab.mall.local:devops/gitops-manifests.git
    targetRevision: main
    path: apps
    directory:
      recurse: true
      exclude: '{provisioning/*}'
  destination:
    server: https://kubernetes.default.svc
    namespace: mall
  syncPolicy:
    automated:
      prune: false
      selfHeal: true
```

每个微服务的 Application 由 ApplicationSet 自动生成,按环境矩阵展开:

```yaml
apiVersion: argoproj.io/v1alpha1
kind: ApplicationSet
metadata:
  name: mall-services
  namespace: argocd
spec:
  generators:
    - matrix:
        generators:
          - list:
              elements:
                - service: gateway
                - service: user
                - service: order
                - service: stock
          - list:
              elements:
                - env: dev
                  autoSync: true
                  cluster: https://kubernetes.default.svc
                - env: test
                  autoSync: true
                  cluster: https://kubernetes.default.svc
                - env: prod
                  autoSync: false          # 生产不自动同步,人工点击
                  cluster: https://kubernetes.default.svc
  template:
    metadata:
      name: 'mall-{{service}}-{{env}}'
      annotations:
        notifications.argoproj.io/subscribe.on-sync-failed.feishu: 'mall-deploy'
    spec:
      project: mall-project
      source:
        repoURL: git@gitlab.mall.local:devops/gitops-manifests.git
        targetRevision: main
        path: 'charts/mall-service'
        helm:
          valueFiles:
            - '../../apps/{{env}}/{{service}}/values.yaml'
      destination:
        server: '{{cluster}}'
        namespace: 'mall-{{env}}'
      syncPolicy:
        automated:
          prune: true
          selfHeal: '{{autoSync}}'
        syncOptions:
          - CreateNamespace=true
        retry:
          limit: 3
          backoff: { duration: 30s, factor: 2 }
```

### 8.4 环境同步策略与发布节奏

| 环境 | sync 策略 | 触发方式 | 附加动作 |
|---|---|---|---|
| dev | automated + selfHeal | Jenkins 推 values 后秒级同步 | 部署后自动跑 ZAP 冒烟 |
| test | automated | 手动或定时 | 自动化回归测试 |
| staging | manual + Sync Window | 人工点击 | 全量 ZAP 巡检 |
| prod | manual + Sync Window(工作日 10:00-18:00) | 审批后人工点击 | 金丝雀 + 指标验证 |

`selfHeal: true` 在 dev 环境意味着任何人手改集群资源都会被 Argo CD 立即还原回 Git 状态——Git 是唯一事实来源,这也让"环境漂移"问题从治理手段变成机制问题。

### 8.5 金丝雀发布(Argo Rollouts)

对订单等核心服务,Deployment 替换为 Rollout,配合 Prometheus 指标自动分析:

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Rollout
metadata:
  name: mall-order
spec:
  strategy:
    canary:
      canaryService: mall-order-canary
      stableService: mall-order-stable
      steps:
        - setWeight: 10
        - pause: { duration: 5m }
        - setWeight: 30
        - pause: { duration: 5m }
        - setWeight: 60
        - pause: { duration: 5m }
      analysis:
        templates:
          - templateName: success-rate
        startingStep: 2
---
apiVersion: argoproj.io/v1alpha1
kind: AnalysisTemplate
metadata:
  name: success-rate
spec:
  metrics:
    - name: success-rate
      interval: 1m
      successCondition: result[0] >= 0.99
      failureLimit: 2
      provider:
        prometheus:
          address: http://prometheus.observability:9090
          query: |
            sum(rate(http_server_requests_seconds_count{app="mall-order",status=~"2.."}[2m]))
            /
            sum(rate(http_server_requests_seconds_count{app="mall-order"}[2m]))
```

成功率连续 2 分钟低于 99%,Rollout 自动中止并回滚到 stable 版本,不需要人工介入。

---

## 9. 密钥与配置管理

### 9.1 三类凭证的分工

| 类型 | 方案 | 说明 |
|---|---|---|
| 流水线凭证 | Jenkins Credentials + GitLab Variables | Harbor 账号、Git token 等 CI 阶段使用 |
| 应用运行时静态密钥 | Sealed Secrets / SOPS | 数据库固定账号、第三方 API Key |
| 数据库动态凭证 | Vault | 按需生成、自动轮换、可审计 |

### 9.2 Vault 动态数据库凭证

Vault 部署后为 MySQL 配置 database secrets engine,按需创建短时账号(默认 TTL 1 小时,自动续期):

```hcl
# Vault: mysql 动态凭证
resource "vault_database_secret_backend" "mysql" {
  name = "mysql-mall"
  connection_url = "root:{{.password}}@tcp(mysql.mall-system:3306)/"
  allowed_roles = ["mall-order-role"]
}

resource "vault_database_secret_backend_role" "mall-order" {
  backend = vault_database_secret_backend.mysql.name
  name = "mall-order-role"
  db_name = vault_database_secret_backend.mysql.name
  creation_statements = [
    "CREATE USER '{{name}}'@'%' IDENTIFIED BY '{{password}}';",
    "GRANT SELECT, INSERT, UPDATE ON mall_order.* TO '{{name}}'@'%';"
  ]
  default_ttl = 3600
  max_ttl = 86400
}
```

应用侧通过 **Vault Agent Injector**(K8s mutating webhook)把凭证渲染成文件挂进 Pod,Spring Boot 用 `spring.config.import=vault://` 或直接读文件,凭证轮换对应用透明。DBA 审计时在 Vault 的 audit log 中能查到"哪个 Pod 在什么时间拿到了什么权限的账号"。

### 9.3 Sealed Secrets(Git 中安全存储)

需要放进 GitOps 仓库的静态密钥(Nacos 控制台密码、飞书 Webhook 等)用 Sealed Secrets 加密,只有集群内 controller 能解密:

```bash
# 加密(公钥,可在任何机器执行,产物可安全入 Git)
echo -n 'my-nacos-password' | kubectl create secret generic nacos-cred \
  --dry-run=client --from-file=password=/dev/stdin -o yaml \
  | kubeseal --controller-namespace=kube-system --format yaml \
  > apps/prod/order/sealed-nacos-cred.yaml
```

生成的 SealedSecret 资源在 Git 仓库里是密文,克隆仓库的任何人都无法还原。备份 controller 私钥是运维关键动作——私钥丢失意味着所有 sealed 密文不可恢复。

### 9.4 GitLab Variables 与 Jenkins Credentials 对照

| 场景 | GitLab | Jenkins |
|---|---|---|
| 类型 | CI/CD Variables(masked / protected) | Credentials(多类型) |
| 掩码 | masked 隐藏日志输出 | 日志自动脱敏 |
| 环境 | protected 限定受保护分支 | folder / domain 级隔离 |
| 建议用法 | MR 阶段轻检查用 | 完整流水线用 |

两边各自维护一套会有漂移风险,常见做法:重凭证(数据库、云厂商 AK)只进 Vault,CI/CD 变量只存"指向 Vault 的认证 token",真正取值发生在运行时。

---

## 10. 基础设施即代码(Terraform + Ansible)

### 10.1 Terraform:集群与平台资源

Terraform 管理对象是"K8s 集群层面的长生命周期资源":namespace、ResourceQuota、数据库实例、云网络。示例为每个环境创建带配额的 namespace:

```hcl
resource "kubernetes_namespace" "mall_env" {
  for_each = toset(["dev", "test", "staging", "prod"])
  metadata {
    name = "mall-${each.key}"
    labels = {
      env       = each.key
      team      = "mall-platform"
      istio-injection = "disabled"
    }
  }
}

resource "kubernetes_resource_quota" "mall_quota" {
  for_each = kubernetes_namespace.mall_env
  metadata { name = "quota"; namespace = each.value.metadata[0].name }
  spec {
    hard = {
      requests.cpu    = each.key == "prod" ? "16" : "4"
      requests.memory = each.key == "prod" ? "32Gi" : "8Gi"
      limits.cpu      = each.key == "prod" ? "32" : "8"
    }
  }
}
```

state 存放使用 GitLab Managed Terraform State(Settings → Terraform),CI 中 `gitlab-terraform plan/apply`,MR 页面直接渲染 plan 差异,基础设施变更与代码变更走同一套评审流程。

### 10.2 Ansible:节点与中间件初始化

Terraform 管资源,Ansible 管主机内状态:JDK / Docker / 时钟同步 / 内核参数 / Nexus 与 Harbor 所在虚机的目录初始化。playbook 示例:

```yaml
---
- name: 初始化 CI/CD 中间件节点
  hosts: cicd_nodes
  become: true
  roles:
    - { role: common,   tags: [base] }
    - { role: docker,   tags: [docker] }
    - { role: jdk,      tags: [jdk] }
  tasks:
    - name: 内核参数 - 容器网络转发
      ansible.posix.sysctl:
        name: net.ipv4.ip_forward
        value: "1"
        sysctl_set: true
        reload: true

    - name: 数据盘挂载 /data
      ansible.posix.mount:
        path: /data
        src: /dev/vdb
        fstype: xfs
        state: mounted

    - name: Nexus / Harbor 数据目录
      ansible.builtin.file:
        path: "{{ item }}"
        state: directory
        owner: "{{ item.split('/')[1] }}"
        mode: "0750"
      loop: [/data/nexus, /data/harbor]
```

两个工具的边界:Terraform 声明"资源存在与否",Ansible 声明"主机内配置正确与否",K8s 内部的应用资源则完全交给 Argo CD——三层各管一段,互不越界。

---

## 11. 可观测性(CD 闭环)

发布验证是 CI/CD 的最后一环:部署完成后 15 分钟内的指标决定放行或回滚,数据全部来自可观测性栈。

### 11.1 指标:Prometheus + Grafana

Prometheus Operator 部署在 `observability` 命名空间,微服务暴露 actuator + Micrometer 指标(`management.endpoints.web.exposure.include=health,prometheus`),通用 Chart 内置 ServiceMonitor:

```yaml
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: {{ include "mall-service.fullname" . }}
  labels: {{- include "mall-service.labels" . | nindent 4 }}
spec:
  selector:
    matchLabels: {{- include "mall-service.selectorLabels" . | nindent 6 }}
  endpoints:
    - port: act
      path: /actuator/prometheus
      interval: {{ .Values.metrics.serviceMonitor.interval }}
```

告警规则(PrometheusRule)覆盖发布期最敏感的信号:

```yaml
apiVersion: monitoring.coreos.com/v1
kind: PrometheusRule
metadata:
  name: mall-deploy-rules
spec:
  groups:
    - name: mall-deploy
      rules:
        - alert: PodCrashLooping
          expr: |
            increase(kube_pod_container_status_restarts_total[15m]) > 3
          for: 5m
          labels: { severity: critical }
          annotations:
            summary: "{{ $labels.pod }} 15 分钟内重启超过 3 次"
        - alert: Gateway5xxHigh
          expr: |
            sum(rate(http_server_requests_seconds_count{app="mall-gateway",status=~"5.."}[5m]))
            / sum(rate(http_server_requests_seconds_count{app="mall-gateway"}[5m])) > 0.01
          for: 3m
          labels: { severity: critical }
          annotations:
            summary: "网关 5xx 比例超过 1%"
```

Grafana 面板三层:全局总览(流量 / 错误率 / 时延)、单服务详情(JVM / GC / 慢 SQL)、发布视图(按镜像 tag 维度对比新旧版本指标,直接服务金丝雀决策)。

### 11.2 日志:ELK / Loki

规模不大(日增日志 < 100GB)时 Loki + Promtail 更省资源,无需全文索引:

```yaml
# Promtail 采集容器日志
 scrape_configs:
  - job_name: kubernetes-pods
    kubernetes_sd_configs:
      - role: pod
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_label_app]
        target_label: app
      - source_labels: [__meta_kubernetes_namespace]
        target_label: namespace
```

日志规模大、有全文检索与合规留档需求时选 ELK(Filebeat → Kafka → Logstash → ES → Kibana),代价是 ES 集群的资源成本。微服务日志格式统一 JSON,关键字段 `ts / level / service / traceId / orderId`,为下一节的链路联动打基础。

### 11.3 链路:SkyWalking

Spring Cloud Alibaba 生态优先选 SkyWalking(同为 Apache 项目,对 OpenFeign / Gateway / RocketMQ / Seata 的埋点支持开箱即用)。agent 已在镜像层预置(见 7.4),Pod 环境变量只需指定服务名与 OAP 地址。

日志关联 traceId,在日志 pattern 中输出 `%tid`,一个订单请求经过网关 → 订单 → 库存 → MySQL 的完整链路在拓扑图上一目了然,平均响应时间按 span 分解,慢调用定位到具体 SQL。

### 11.4 告警:Alertmanager → 飞书

Alertmanager 路由到飞书群机器人(经 PrometheusAlert 网关做格式转换,飞书卡片消息):

```yaml
route:
  group_by: [alertname, service]
  group_wait: 30s
  group_interval: 5m
  repeat_interval: 4h
  receiver: feishu-default
  routes:
    - matchers: [ severity = "critical" ]
      receiver: feishu-oncall
      continue: true
    - matchers: [ alertname = "Watchdog" ]
      receiver: blackhole

receivers:
  - name: feishu-oncall
    webhook_configs:
      - url: http://prometheusalert.observability:8080/prometheus/feishu
        send_resolved: true
```

值班群的告警卡片带 Grafana 面板直达链接与 Argo CD 应用链接,处置人一个回滚动作的距离保持在两次点击内。

---

## 12. 环境规范与审批流程

### 12.1 环境矩阵

| 环境 | K8s namespace | Nacos namespace | 数据库 | 发布者 |
|---|---|---|---|---|
| dev | mall-dev | dev | 独立实例(可重置) | 自动 |
| test | mall-test | test | 独立实例 | 自动 + 定时 |
| staging | mall-staging | staging | 生产同构脱敏 | 人工 |
| prod | mall-prod | prod | 生产 | 审批 + 人工 |

### 12.2 生产发布审批链

1. Jenkins 流水线全绿(质量门禁 + 三层安全扫描);
2. values 变更 MR 合入 GitOps 仓库,变更内容(git diff)进入发布评审群;
3. 值班经理在 Argo CD 界面点击 **Sync**(Action Window 限制在工作日 10:00-18:00,窗口外需要平台负责人临时开窗);
4. Rollout 金丝雀启动,AnalysisTemplate 自动盯指标;
5. 全量后 Watchdog 规则观察 30 分钟,无 critical 告警则发布闭环。

回滚路径只有一条:GitOps 仓库 `git revert` values 的镜像 tag → Argo CD 同步 → stable 版本回归。因为镜像 tag 不可变,revert 的语义精确可靠,不存在"回滚后构建产物变了"的经典事故。

### 12.3 审计合规

- GitLab 审计事件流(谁 merge 了什么)→ SIEM;
- Jenkins 构建日志保留 90 天,部署记录含镜像 digest;
- Harbor 审计日志(pull / push 事件)保留 1 年;
- Argo CD 每次同步的应用快照可追溯;
- Vault audit log 记录所有动态凭证的签发与吊销。

---

## 13. 端到端时序串讲

以订单服务一次完整发布为例,时间线如下:

```
T+00:00  开发者 push feature/order-discount 分支,创建 MR
T+00:01  GitLab CI 轻检查:gitleaks + compile-check(90 秒)
T+00:15  两位评审人批准(CODEOWNERS 追加订单负责人)
T+00:20  merge to master,Webhook 触发 Jenkins
T+00:21  动态 Agent Pod 拉起
T+00:22  Gitleaks 全量扫描(20 秒)
T+00:23  mvn verify:编译 + 300 个单测 + JaCoCo(3 分钟,Maven 缓存命中)
T+00:26  SonarQube 分析提交,waitForQualityGate 等待回传
T+00:28  质量门禁通过(覆盖率 71%,0 blocker)
T+00:29  Dependency-Check(CVSS≥7 无命中)
T+00:31  Docker 构建(multi-stage,层缓存命中,1 分钟)
T+00:32  Trivy 镜像扫描(CRITICAL/HIGH 均未修复项为 0)
T+00:33  推送 Harbor:v1.4.0-mall-order-142(Harbor 自动扫描通过)
T+00:34  yq 更新 gitops 仓库 apps/dev/order/values.yaml
T+00:35  Argo CD 检测到变更,dev 自动同步,Pod 滚动更新
T+00:38  ServiceMonitor 指标恢复,ZAP 冒烟通过
T+09:00  测试环境回归通过,values 变更推进到 prod 目录,发布评审
T+10:00  值班经理 Argo CD 点击 Sync,金丝雀 10% → 30% → 60% → 100%
T+10:25  AnalysisTemplate 成功率 99.6%,全量完成,发布闭环
```

若 T+10:15 时成功率跌破 99%:Analysis 失败 → Rollout 自动回滚 stable → Watchdog 告警进飞书群 → 值班确认回滚结果 → GitOps 仓库 revert 提交保持仓库与集群一致。全程无需登录节点、无人工执行脚本。

---

## 14. 落地路线与常见问题

### 14.1 分阶段落地(建议顺序)

| 阶段 | 周期 | 目标 | 验收标志 |
|---|---|---|---|
| 一 | 2 周 | GitLab 分支保护 + Jenkins 构建 + Nexus 私服 | 手工触发可构建镜像 |
| 二 | 2 周 | SonarQube 门禁 + 单测覆盖率 + Gitleaks | 质量不达标无法合入 |
| 三 | 2 周 | Harbor(机器人账号 + 不可变 tag)+ Trivy | 镜像全生命周期可追溯 |
| 四 | 3 周 | 通用 Helm Chart + Argo CD dev/test 自动同步 | 一条流水线四服务复用 |
| 五 | 3 周 | 生产审批 + Rollout 金丝雀 + Sync Window | 生产发布可回滚、可审计 |
| 六 | 持续 | Vault / Sealed Secrets / 可观测性联动告警 | 密钥零明文,告警触达值班群 |

### 14.2 常见问题

**构建慢**:按顺序检查 Maven 缓存是否命中(PVC + settings 走私服)、Docker 层缓存(基础镜像与 agent 层是否稳定)、是否全量构建(用 `-pl <service> -am` 只构模块及依赖)。Jenkins 动态 Agent 冷启动 20 秒属正常,超过 1 分钟检查镜像仓库位置与 Pod 调度。

**SonarQube 门禁超时**:`waitForQualityGate` 默认等待靠 Webhook 回调,若 SonarQube 侧 Webhook URL 填错,流水线会一直等到 timeout。用 SonarQube 界面的 Webhook 执行日志排查回传状态。

**Dependency-Check 首次跑极慢**:NVD 数据库下载约 1GB+,受 API 限速影响可能超 1 小时。把 `data/dependency-check-data` 目录放共享 PVC,另起每日定时任务只做数据库更新,CI 任务复用数据。

**Argo CD 同步了但 Pod 没更新**:镜像 tag 是 `latest` 或 values 里 tag 未变化时,模板 hash 不变,K8s 认为无变更。坚持每次构建新 tag,或临时加 `--force` 同步(不推荐常态化)。

**Trivy 误报阻断**:确认是否 `--ignore-unfixed` 已加(未提供修复版本的漏洞只提示不阻断),仍误报的写入 `.trivyignore` 并注明原因与跟踪工单号,豁免必须有理由。

**Jenkins 凭证被 fork 仓库利用**:Jenkinsfile 来自 fork 仓库时,不要在 pipeline 中直接用 `credentials()`。开启 "Do not allow concurrent builds" 与 folders 级凭证隔离,或把重型流水线集中到受保护的独立流水线仓库。

**Nacos 配置不在 GitOps 管控内**:这是 Spring Cloud Alibaba 场景的特殊风险——应用行为由镜像 + Nacos 配置共同决定,只管镜像时"代码没变行为变了"可能发生。将 Nacos 配置导出为 YAML 存入 Git(可用 nacos-sync 工具双向同步),配置变更走 MR 评审,闭环才算完整。

