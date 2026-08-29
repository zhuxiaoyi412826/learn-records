# 企业级 CI/CD 工具与组件全景

企业级 CI/CD 不是单一工具，而是一条覆盖**代码 → 构建 → 测试 → 制品 → 部署 → 监控**的完整工具链。下面按组件分类整理。

## 一、核心组件分层

| 层级 | 组件           | 作用                         |
| :--- | :------------- | :--------------------------- |
| 1    | 版本控制 (SCM) | 代码托管、分支策略、代码评审 |
| 2    | CI 引擎        | 流水线编排、自动构建与测试   |
| 3    | 制品仓库       | 组件/镜像的版本化管理        |
| 4    | 容器与编排     | 标准化交付物与运行平台       |
| 5    | CD/GitOps      | 自动化部署与环境同步         |
| 6    | 质量与安全     | 质量门禁、漏洞扫描           |
| 7    | 密钥管理       | 凭证集中管理与注入           |
| 8    | 可观测性       | 部署后监控、告警、回滚依据   |

## 二、各层工具选型

### 1. 版本控制与协作

- **托管平台**：GitLab（自建首选）、GitHub、Gitee、Bitbucket
- **分支策略**：Git Flow / Trunk-Based Development（企业高频率发布推荐主干开发）
- **代码评审**：GitLab MR、GitHub PR、Gerrit（强评审流程场景）

### 2. CI 引擎（流水线核心）

| 工具                    | 特点                              | 适用场景           |
| :---------------------- | :-------------------------------- | :----------------- |
| Jenkins                 | 插件生态最大、Jenkinsfile 即代码  | 自建、异构技术栈   |
| GitLab CI               | 与 Git 仓库一体、`.gitlab-ci.yml` | 已用 GitLab 的团队 |
| GitHub Actions          | Marketplace 丰富、YAML            | 开源/云原生项目    |
| Tekton                  | K8s 原生 CRD                      | 深度 K8s 环境      |
| TeamCity / Azure DevOps | 商业支持完善                      | .NET/微软系企业    |

### 3. 制品管理

- **通用制品**：Nexus Repository OSS、JFrog Artifactory（商业级）
- **容器镜像**：Harbor（企业事实标准：镜像扫描、签名、RBAC、复制策略）、阿里云 ACR
- **包管理**：Maven 私服（Nexus/Artifactory 代理中央仓库）

### 4. 容器与编排

- Docker / Podman（构建）
- **Kubernetes**（运行平台标准）
- **Helm / Kustomize**（应用打包与环境差异化配置）

### 5. CD 与部署

| 工具                    | 模式          | 说明                                       |
| :---------------------- | :------------ | :----------------------------------------- |
| Argo CD                 | GitOps 声明式 | 集群状态与 Git 自动同步，回滚即 git revert |
| Flux CD                 | GitOps        | 轻量级，CNCF 毕业项目                      |
| Spinnaker               | 多云流水线    | 大规模多云部署                             |
| Argo Rollouts / Flagger | 发布策略      | 金丝雀、蓝绿、渐进式交付                   |

### 6. 质量与安全门禁（DevSecOps）

- **静态代码**：SonarQube（质量门禁拦截失败构建）
- **依赖漏洞**：OWASP Dependency-Check、Trivy、Snyk
- **镜像扫描**：Trivy、Harbor 集成 Clair
- **密钥泄露**：Gitleaks、TruffleHog
- **动态扫描 (DAST)**：OWASP ZAP
- **测试**：JUnit/pytest + 覆盖率（JaCoCo/coverage.py，集成质量门禁）

### 7. 密钥与配置

- HashiCorp Vault（动态凭证、审计）
- Kubernetes Secrets + **Sealed Secrets / SOPS**（Git 可安全存储）
- 流水线凭证：Jenkins Credentials、GitLab Variables（masked/protected）

### 8. 基础设施即代码（IaC）

- Terraform / Pulumi（资源编排）
- Ansible（主机配置、中间件初始化）

### 9. 可观测性（CD 闭环的最后一块）

- 指标：Prometheus + Grafana
- 日志：ELK / Loki
- 链路：Jaeger / SkyWalking
- 告警：Alertmanager → 飞书/钉钉/企业微信 Webhook

## 三、典型流水线（以 Java/Spring Boot 为例）

Plain Text



```
提交代码 → MR 触发
  ├─ 单元测试 + 覆盖率 → SonarQube 质量门禁
  ├─ 依赖漏洞扫描 (Dependency-Check)
  ├─ 构建 Jar → 推送 Nexus
  ├─ 构建 Docker 镜像 → Trivy 扫描 → 推送 Harbor（签名）
  └─ 生成 Helm values → 更新 GitOps 仓库
       └─ Argo CD 检测变更 → 同步 staging → 自动化验收
            └─ 人工审批 → 生产发布（金丝雀）→ 监控指标达标 → 全量
```

## 四、企业级落地三套常见组合

| 方案          | 组成                                              | 特点                         |
| :------------ | :------------------------------------------------ | :--------------------------- |
| 经典自建      | GitLab + Jenkins + Nexus + Harbor + K8s + Argo CD | 灵活、组件可替换，运维成本高 |
| GitLab 一体化 | GitLab + Harbor + K8s + Argo CD                   | 统一权限与体验，链路短       |
| 云原生轻量    | GitHub + Actions + Argo CD + Argo Rollouts        | 云端托管，适合快速起步       |

## 五、工具之外的企业级必备能力

- **权限与审计**：统一 RBAC、全链路审计日志（合规要求）
- **环境管理**：dev / test / staging / prod 隔离，配置外置（Nacos/Apollo）
- **发布策略**：蓝绿、金丝雀 + 自动回滚（基于 Prometheus 指标判断）
- **流程规范**：分支保护、强制评审、不可变制品（一次构建多处部署）
- **审批流**：生产环境人工卡点（Jenkins input、GitLab 受保护环境、Argo CD 同步审批）