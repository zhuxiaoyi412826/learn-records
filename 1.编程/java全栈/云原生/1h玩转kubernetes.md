学习k8s就跟学习office三件套上，95%的人只会5%，而5%的知识可以干95%的事情，所以不要觉的k8s难  

# 1 kubernetes

## 1 什么是kubernetes

Kubernetes 是一个可移植、可扩展的开源平台，一个分布式资源调度进行容器编排云原生的操作系统，用于管理容器化的工作负载和服务，可促进声明式配置和自动化。 Kubernetes 拥有一个庞大且快速增长的生态，其服务、支持和工具的使用范围相当广泛。

**Kubernetes** 这个名字源于希腊语，意为“舵手”或“飞行员”。k8s 这个缩写是因为 k 和 s 之间有八个字符的关系。 Google 在 2014 年开源了 Kubernetes 项目。 Kubernetes 建立在 [Google 大规模运行生产工作负载十几年经验](https://research.google/pubs/pub43438)的基础上， 结合了社区中最优秀的想法和实践。

## 2 kubernetes能做什么

- **服务发现和负载均衡**

  Kubernetes 可以使用 DNS 名称或自己的 IP 地址来暴露容器。 如果进入容器的流量很大， Kubernetes 可以负载均衡并分配网络流量，从而使部署稳定。

- **存储编排**

  Kubernetes 允许你自动挂载你选择的存储系统，例如本地存储、公共云提供商等。

- **自动部署和回滚**

  你可以使用 Kubernetes 描述已部署容器的所需状态， 它可以以受控的速率将实际状态更改为期望状态。 例如，你可以自动化 Kubernetes 来为你的部署创建新容器， 删除现有容器并将它们的所有资源用于新容器。

- **自动完成装箱计算**

  你为 Kubernetes 提供许多节点组成的集群，在这个集群上运行容器化的任务。 你告诉 Kubernetes 每个容器需要多少 CPU 和内存 (RAM)。 Kubernetes 可以将这些容器按实际情况调度到你的节点上，以最佳方式利用你的资源。

- **自我修复**

  Kubernetes 将重新启动失败的容器、替换容器、杀死不响应用户定义的运行状况检查的容器， 并且在准备好服务之前不将其通告给客户端。

- **密钥与配置管理**

  Kubernetes 允许你存储和管理敏感信息，例如密码、OAuth 令牌和 ssh 密钥。 你可以在不重建容器镜像的情况下部署和更新密钥和应用程序配置，也无需在堆栈配置中暴露密钥。

## 3 如何操作kubernetes

一切皆为资源”的设计是 Kubernetes 能够顺利施行声明式 API 的必要前提，对k8s集群管理就是管理k8的资源    K8S中所有的内容都抽象为资源，对资源进行增删查改

我们通过 kubernetes 的 API 来操作整个集群。 可以通过 kubectl、ui、curl 最终发送 http+json/yaml 方式的请求给 API Server，然后控制 k8s 集群。k8s 里的所有的资源对象都可以采用 yaml 或 JSON 格式的文件定义或描述

管理K8S核心资源的三种基本方式

1 陈述式管理方式- 主要依赖命令行CLI工具进行管理  Kubectl

2 声明式管理方式 -主要依赖统一资源配置清单（manifest）进行管理

3 GUI管理方式图形界面

操作kubernetes实际上就是增删改查

# 2 kubernetes名词解释

## 1 基本概念

| 集群               | Cluster                  | [集群](https://kubernetes.io/zh/docs/concepts/cluster-administration/) |
| ------------------ | ------------------------ | ------------------------------------------------------------ |
| 节点               | Node                     | [节点](https://kubernetes.io/zh/docs/concepts/architecture/nodes/) |
| 容器               | Container                | [容器](https://kubernetes.io/zh/docs/concepts/containers/)   |
| 镜像               | Image                    | [镜像](https://kubernetes.io/zh/docs/concepts/containers/images/) |
| 命名空间           | Namespace                | [命名空间](https://kubernetes.io/zh/docs/concepts/overview/working-with-objects/namespaces/) |
| 工作负载           | Workload                 | [工作负载](https://kubernetes.io/zh/docs/concepts/workloads/) |
| 容器组             | Pod                      | [Pods](https://kubernetes.io/zh/docs/concepts/workloads/pods/) |
| 无状态工作负载     | Deployment               | [Deployments](https://kubernetes.io/zh/docs/concepts/workloads/controllers/deployment/) |
| 有状态工作负载     | StatefulSet              | [StatefulSets](https://kubernetes.io/zh/docs/concepts/workloads/controllers/statefulset/) |
| 守护进程集工作负载 | DaemonSet                | [DaemonSet](https://kubernetes.io/zh/docs/concepts/workloads/controllers/daemonset/) |
| 任务               | Job                      | [Jobs](https://kubernetes.io/zh/docs/concepts/workloads/controllers/job/) |
| 定时任务           | CronJob                  | [CronJob](https://kubernetes.io/zh/docs/concepts/workloads/controllers/cron-jobs/) |
| 自定义资源         | CustomResourceDefinition | [定制资源](https://kubernetes.io/zh/docs/concepts/extend-kubernetes/api-extension/custom-resources/) |
| 服务               | Service                  | [服务](https://kubernetes.io/zh/docs/concepts/services-networking/service/) |
| 虚拟集群IP         | Cluster IP               | [服务类型](https://kubernetes.io/zh/docs/concepts/services-networking/service/#publishing-services-service-types) |
| 节点端口           | NodePort                 | [NodePort类型](https://kubernetes.io/zh/docs/concepts/services-networking/service/#nodeport) |
| 路由               | Ingress                  | [Ingress](https://kubernetes.io/zh/docs/concepts/services-networking/ingress/) |
| 标签               | Label                    | [标签和选择算符](https://kubernetes.io/zh/docs/concepts/overview/working-with-objects/labels/) |
| 配置项             | Configmap                | [ConfigMap](https://kubernetes.io/zh/docs/concepts/configuration/configmap/) |
| 保密字典           | Secret                   | [Secret](https://kubernetes.io/zh/docs/concepts/configuration/secret/) |
| 存储卷             | PersistentVolume         | [持久卷](https://kubernetes.io/zh/docs/concepts/storage/persistent-volumes/) |
| 存储声明           | PersistentVolumeClaim    | [PersistentVolumeClaims](https://kubernetes.io/zh/docs/concepts/storage/persistent-volumes/#persistentvolumeclaims) |
| 水平弹性伸缩       | HPA                      | [Pod水平自动扩缩](https://kubernetes.io/zh/docs/tasks/run-application/horizontal-pod-autoscale/) |
| 负载均衡           | LoadBalancer             | [LoadBalancer类型](https://kubernetes.io/zh/docs/concepts/services-networking/service/#loadbalancer) |
| 节点亲和性         | NodeAffinity             | [节点亲和性](https://kubernetes.io/zh/docs/concepts/scheduling-eviction/assign-pod-node/#node-affinity) |
| 应用亲和性         | PodAffinity              | [Pod间亲和性与反亲和性](https://kubernetes.io/zh/docs/concepts/scheduling-eviction/assign-pod-node/#inter-pod-affinity-and-anti-affinity) |
| 应用非亲和性       | PodAntiAffinity          | [Pod间亲和性与反亲和性](https://kubernetes.io/zh/docs/concepts/scheduling-eviction/assign-pod-node/#inter-pod-affinity-and-anti-affinity) |
| 选择器             | LabelSelector            | [标签选择算符](https://kubernetes.io/zh/docs/concepts/overview/working-with-objects/labels/#label-selectors) |
| 注解               | Annotation               | [注解](https://kubernetes.io/zh/docs/concepts/overview/working-with-objects/annotations/) |
| 触发器             | Webhook                  | [Webhook模式](https://kubernetes.io/zh/docs/reference/access-authn-authz/webhook/) |
| 端点               | Endpoint                 | [云原生服务发现](https://kubernetes.io/zh/docs/concepts/services-networking/service/#云原生服务发现) |
| 资源配额           | Resource Quota           | [资源配额](https://kubernetes.io/zh/docs/concepts/policy/resource-quotas/) |
| 资源限制           | Limit Range              | [限制范围](https://kubernetes.io/zh/docs/concepts/policy/limit-range/) |
| 模板               | Template                 | [Pod模板](https://kubernetes.io/zh/docs/concepts/workloads/controllers/deployment/#pod-template) |

## 2 组件

Kubernetes cluster consists of 一组工作机器，称为 [节点](https://kubernetes.io/zh-cn/docs/concepts/architecture/nodes/)， 会运行容器化应用程序。每个集群至少有一个工作节点。

工作节点会托管 [Pod](https://kubernetes.io/zh-cn/docs/concepts/workloads/pods/) ，而 Pod 就是作为应用负载的组件。

 [控制平面](https://kubernetes.io/zh-cn/docs/reference/glossary/?all=true#term-control-plane)管理集群中的工作节点和 Pod。 在生产环境中，控制平面通常跨多台计算机运行， 一个集群通常运行多个节点，提供容错性和高可用性。

![](https://d33wubrfki0l68.cloudfront.net/2475489eaf20163ec0f54ddc1d92aa8d4c87c96b/e7c81/images/docs/components-of-kubernetes.svg)

我们看下 k8s 集群的架构，从左到右，分为两部分，第一部分是 Master 节点（也就是图中的 Control Plane），第二部分是 Node 节点。

Master 节点一般包括四个组件，apiserver、scheduler、controller-manager、etcd，他们分别的作用是什么：

- **控制平面组件（Control Plane Components）**

​       控制平面组件会为集群做出全局决策，比如资源的调度。 以及检测和响应集群事件

1. kube-apiserver提供了资源操作的唯一入口，并提供认证、授权、访问控制、API注册和发现等机制； 该组件负责公开了 Kubernetes API，负责处理接受请求的工作你可以运行 `kube-apiserver` 的多个实例，来平衡之间的流量

2. controller manager负责维护集群的状态，比如故障检测、自动扩展、滚动更新等 是[控制平面](https://kubernetes.io/zh-cn/docs/reference/glossary/?all=true#term-control-plane)的组件， 负责运行[控制器](https://kubernetes.io/zh-cn/docs/concepts/architecture/controller/)进程。致力于将当前状态转变为期望的状态

   节点控制器（Node Controller）：用于在节点终止响应后检查云提供商以确定节点是否已被删除

   路由控制器（Route Controller）：用于在底层云基础架构中设置路由

   服务控制器（Service Controller）：用于创建、更新和删除云提供商负载均衡器

3. kuube- scheduler负责资源的调度，按照预定的调度策略将Pod调度到相应的机器上 进行算法的调度 很重要

4. etcd  高可用的键值对数据库    用作 Kubernetes 的所有集群数据的后台数据库

 **Node组件**

​              节点组件会在每个节点上运行，负责维护运行的 Pod 并提供 Kubernetes 运行环境

1. kubelet负责维护容器的生命周期，同时也负责Volume（CVI）和网络（CNI）的管理 ，就是干活的  获取某个pod的运行状态    定时汇报当前节点的状态   镜像和容器清理
2. Container runtime负责镜像管理以及Pod和容器的真正运行（CRI），具体跑应用的载体
3. kube-proxy负责为Service提供cluster内部的服务发现和负载均衡，在每个节点上运行网络的代理  ,serice资源的载体 建立了pod 网络和集群网络的关系 常用的流量调度模式  Ipvs 推荐 更高效   

**插件Add-ons**：

- DNS 一个可选的DNS服务，用于为每个Service对象创建DNS记录，这样所有的Pod就可以通过DNS访问服务了。
- Dashboard 是 Kubernetes 集群的通用的、基于 Web 的用户界面。GUI管理，它使用户可以管理集群中运行的应用程序以及集群本身， 并进行故障排除。

- CNI网络插件    flannel/calico
- 服务发现用插件   coredns
- 服务暴露用插件     treefik

其他插件

- Ingress Controller为服务提供外网入口
- Heapster提供资源监控
- Federation提供跨可用区的集群
- Fluentd-elasticsearch提供集群日志采集、存储与查
- coredns：可以为集群中的SVC创建一个域名IP的对应关系解析
- ingress controller：官方只能实现四层代理，INGRESS 可以实现七层代理
- federation：提供一个可以跨集群中心多K8S统一管理功能
- prometheus ：提供K8S集群的监控能力
- elk：提供 K8S 集群日志统一分析介入平台   

## 3 核心概念

![](https://kuboard.cn/assets/img/image-20190731221630097.21159851.png)

### **1 pod/Pod 控制器**

#### **pod**

pod 是可以在 Kubernetes 中创建和管理的、最小的可部署的计算单元，其中包含一个或多个应用容器。 这些容器相对紧密地耦合在一起。同一个Pod里的容器之间仅需通过localhost就能互相通信。

一个Pod中的应用容器共享五种资源：

- PID命名空间：Pod中的不同应用程序可以看到其他应用程序的进程ID。
- 网络命名空间：Pod中的多个容器能够访问同一个IP和端口范围。
- IPC命名空间：Pod中的多个容器能够使用SystemV IPC或POSIX消息队列进行通信。
- UTS命名空间：Pod中的多个容器共享一个主机名。
- Volumes（共享存储卷）：Pod中的各个容器可以访问在Pod级别定义的Volumes。

**pod网络**

一个pod都会有一个ip，集群中任意一个pod都可以通过pod分配的ip进行访问，但这只是在集群内部，集群外部是访问不同的 

**pod存储**

一个 Pod 可以设置一组共享的存储[卷](https://kubernetes.io/zh-cn/docs/concepts/storage/volumes/)。 Pod 中的所有容器都可以访问该共享卷，从而允许这些容器共享数据。 卷还允许 Pod 中的持久数据保留下来，即使其中的容器需要重新启动。

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221126184028.png)

**pod状态**

![](https://pics4.baidu.com/feed/8644ebf81a4c510fe6f690aa79058b24d42aa531.png@f_auto?token=9326285943177111ef2b0d0334c6498d)

**pod生命周期**

Pod 在其生命周期中只会被[调度](https://kubernetes.io/zh-cn/docs/concepts/scheduling-eviction/)一次。 一旦 Pod 被调度（分派）到某个节点，Pod 会一直在该节点运行，直到 Pod 停止或者被终止

**init容器**

Init 容器是一种特殊容器，在 [Pod](https://kubernetes.io/zh-cn/docs/concepts/workloads/pods/) 内的应用容器启动之前运行。Init 容器可以包括一些应用镜像中不存在的实用工具和安装脚本。

Init 容器与普通的容器非常像，除了如下两点：

- 它们总是运行到完成。
- 每个都必须在下一个启动之前成功完成。

如果 Pod 的 Init 容器失败，kubelet 会不断地重启该 Init 容器直到该容器成功为止

**pod工作流程**

![](https://pics2.baidu.com/feed/5366d0160924ab18bbda3e6f2ea648c47a890b7b.jpeg@f_auto?token=ef2985b2bbd9a90cc32f4c9204759f53)

pod工作流程

1. 用户提交创建Pod的请求，可以通过API Server的REST API ，也可用Kubectl命令行工具，支持Json和Yaml两种格式；
2. API Server 处理用户请求，存储Pod数据到Etcd；
3. Schedule通过和 API Server的watch机制，查看到新的pod，尝试为Pod绑定Node。调度器用一组规则过滤掉不符合要求的主机，比如Pod指定了所需要的资源，那么就要过滤掉资源不够的主机，对上一步筛选出的符合要求的主机进行打分，在主机打分阶段，调度器会考虑一些整体优化策略进行调度；选择打分最高的主机，进行binding操作，结果存储到Etcd中；
4. kubelet根据调度结果执行Pod创建操作。绑定成功后，会启动container。scheduler会调用API Server的API在etcd中创建一个bound pod对象，描述在一个工作节点上绑定运行的所有pod信息。运行在每个工作节点上的kubelet也会定期与etcd同步bound pod信息。

#### **pod控制器** 

Kubernetes 通过引入 Controller（控制器）的概念来管理 Pod 实例。在 Kubernetes 中，您应该始终通过创建 Controller 控制器来创建 Pod，而不是直接创建 Pod。

pod控制器 是pod启动的一种模板  用来保证k8s里启动的pod  按照预期的运行状态 

当你创建一个pod你就是在告知 Kubernetes 系统，你想要的集群工作负载状态看起来应是什么样子的， 这就是 Kubernetes 集群所谓的 **期望状态（Desired State）**会朝着这个方向而且努力工作的

你可以使用工作负载资源来创建和管理多个 Pod。 资源的控制器能够处理副本的管理、上线，

[工作负载](https://kubernetes.io/zh-cn/docs/concepts/workloads/)资源的控制器通常使用 **Pod 模板（Pod Template）** 来替你创建 Pod 并管理它们。

修改 Pod 模板或者切换到新的 Pod 模板都不会对已经存在的 Pod 直接起作用。 如果改变工作负载资源的 Pod 模板，工作负载资源需要使用更新后的模板来创建 Pod， 并使用新创建的 Pod 替换旧的 Pod。

最常用的工作负载类型是Deployment 

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zha649f178ba7fb65e27d0f22be48f647.jpg)

### **Namespace**

Namespace     一种能隔离k8s内部各种资源的方法  这就是名称空间  也可以被称为分组  组内不能有重名

default 默认的    kube-system  kube-public    也可以自定义   查询k8s里特定资源要带上想应的名称空间

### **Labl  /labl**

**标签选择器**

Labl

标签是k8s特色的管理方式  ，便于分类管理资源对象

一个标签可以对应多个资源，多个资源也可以有多个标签

标签的组成  key=value  

与标签差不多的是注解    不过标签名称跟严格

Labl选择器 过滤标签  

基于等值关系   集合关系

### **Service /Ingress**

Servic 简称svc   pod 服务发现与负载均衡   k8s把一组pod 公开为网络服务的  通过标签来选择的

在集群内部任意访问     也可以通过域名来进行访问  

![](https://img2.baidu.com/it/u=1985609237,388295411&fm=253&fmt=auto&app=138&f=JPEG?w=1010&h=500)

如果服务下线或者重启 都会重新加入到service中去  

这导致了一个问题： 如果一组 Pod（称为“后端”）为集群内的其他 Pod（称为“前端”）提供功能， 那么前端如何找出并跟踪要连接的 IP 地址，以便前端可以使用提供工作负载的后端部分？

进入 **Services**。

如何在暴露公网ip

入主要是解决pod的动态变化，提供统一的访问入口：

1、防止pod失联，准备找到提供同一服务的pod（服务发现）

2、定义一组Pod的访问策略（负载均衡）

Kubernetes 中 Service 是一个 API 对象，通过 kubectl + YAML 定义一个 Service，可以将符合 Service 指定条件的 Pod 作为可通过网络访问的服务提供给服务调用者。Service 是 Kubernetes 中的一种服务发现机制：

- Pod 有自己的 IP 地址
- Service 被赋予一个唯一的 dns name
- Service 通过 label selector 选定一组 Pod
- Service 实现负载均衡，可将请求均衡分发到选定这一组 Pod 

Service 每个pod都会分配一个单独的ip地址 ，pod销毁id也就销毁

如何进行流量调度

一个Service可以看作一组提供相同服务的pod的对外访问接口       

service作用于那些pod是通过标签选择器来定义的

Ingress 

Ingress Service的统一网关入口

![](https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg-blog.csdnimg.cn%2F20201222112902891.png%3Fx-oss-process%3Dimage%2Fwatermark%2Ctype_ZmFuZ3poZW5naGVpdGk%2Cshadow_10%2Ctext_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ1MDcwNTQx%2Csize_16%2Ccolor_FFFFFF%2Ct_70&refer=http%3A%2F%2Fimg-blog.csdnimg.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1671951318&t=5ef2b0d29bcd758ae9e5906e8bea79e9)

这些pod可以在同一的节点上 也可以不在同一个节点上

Service引

 k8s集群对外 暴露的接口      第七层 对外暴露的接口

Servie  只能进行 第四层网络流量调度   ip+port

Ingress 可以调度不同业务领域，不同url访问路径的业务流量

Ingress 是 Kubernetes 的一种 API 对象，将集群内部的 Service 通过 HTP/HTPS 方式暴露到集群外部，并通过规则定义 HTP/HTPS 的路由。Ingress 具备如下特性：集群外部可访问的 URL、负载均衡、SSL Termination、按域名路由（name-based virtual hosting）。Ingress 的例子如下所示：

 **节点管理**

### **kube-poxy**

服务代理

Kubernetes 集群中的每个节点都运行了一个 `kube-proxy`，负责为 Service（ExternalName 类型的除外）提供虚拟 IP 访问。 Kubernetes 支持三种 proxy 

**网络模型**

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221125151606.png)

### **存储卷**

Gllusterfs     NFS   k8s进行抽取一一组进行存储



![](https://pics0.baidu.com/feed/f31fbe096b63f624ca3fd1a29c1845f11b4ca393.jpeg@f_auto?token=775b654597ce447cdd49e56cbc131bb2)

- 一个容器组可以包含多个数据卷、多个容器
- 一个容器通过挂载点（volumnMount）决定某一个数据卷（Volumn）被挂载到容器中的什么路径 不同类型的数据卷对应不同的存储介质（图中仅列出了 nfs、PVC、ConfigMap 三种存储介质）

### **IP地址**

**0.0.0.0**

严格来说，0.0.0.0已经不是一个真正意义上的IP地址了。它表示的是这样一个集合：所有不清楚的主机和目的网络。这里的不清楚是指在本机的路由表里没有特定条目指明如何到达。作为缺省路由。

**127.0.0.1**

本机地址。

**224.0.0.1**

组播地址。如果你的主机开启了IRDP（Internet路由发现，使用组播功能），那么你的主机路由表中应该有这样一条路由。

**169.254.x.x**

使用了DHCP功能自动获取了IP的主机，DHCP服务器发生故障，或响应时间太长而超出了一个系统规定的时间，系统会为你分配这样一个IP，代表网络不能正常运行。

**10.xxx、172.16.x.x~172.31.x.x、192.168.x.x**

私有地址，大量用于企业内部。保留这样的地址是为了避免亦或是哪个接入公网时引起地址混乱。

# 3 kubernetes逻辑架构

生成环境下 master 一般是几数个     node也有多个                                                                                                            

![](https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg-blog.csdnimg.cn%2F20200417122422142.png%3Fx-oss-process%3Dimage%2Fwatermark%2Ctype_ZmFuZ3poZW5naGVpdGk%2Cshadow_10%2Ctext_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1ZJUDA5OQ%3D%3D%2Csize_16%2Ccolor_FFFFFF%2Ct_70&refer=http%3A%2F%2Fimg-blog.csdnimg.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1671363340&t=30c5ff7ea75cb250d2ff6163b4174186)

1、通过 Kubectl 提交一个创建 RC（Replication Controller）的请求，该请求通过 APIServer 被写入 etcd 中                

 2、此时 Controller Manager 通过 API Server 的监听资源变化的接口监听到此 RC 事件

 3、分析之后，发现当前集群中还没有它所对应的 Pod 实例， 

4、于是根据 RC 里的 Pod 模板定义生成一个 Pod 对象，通过 APIServer 写入 etcd

 5、此事件被 Scheduler 发现，它立即执行一个复杂的调度流程，为这个新 Pod 选定一 个落户的 Node，然后通过 API Server 讲这一结果写入到 etcd 中， 

6、目标 Node 上运行的 Kubelet 进程通过 APIServer 监测到这个“新生的”Pod，并按照它 的定义，启动该 Pod 并任劳任怨地负责它的下半生，直到 Pod 的生命结束。

 7、随后，我们通过 Kubectl 提交一个新的映射到该 Pod 的 Service 的创建请求 

8、ControllerManager 通过 Label 标签查询到关联的 Pod 实例，然后生成 Service 的 Endpoints 信息，并通过 APIServer 写入到 etcd 中，

 9、接下来，所有 Node 上运行的 Proxy 进程通过 APIServer 查询并监听 Service 对象与 其对应的 Endpoints 信息，建立一个软件方式的负载均衡器来实现 Service 访问到后端 Pod 的流量转发功能。 k8s 里的所有的资源对象都可以采用 yaml 或 JSON

# 4 安装

1 免费体验

**或者是在k8s官网上学习**

https://kubernetes.io/zh-cn/docs/tutorials/hello-minikube/

**阿里云体验快速入门**

https://developer.aliyun.com/adc/scenarioSeries/c14b6e6d2df2454dadc4263f166fa16b?spm=a2c6h.13788135.J_2488678810.10.35402444FW6HjH

2 使用阿里云的ACK

3 minikube

https://minikube.sigs.k8s.io/docs/start/

是一个工具， 能让你在本地运行 Kubernetes。 `minikube` 在你的个人计算机（包括 Windows、macOS 和 Linux PC）上运行一个一体化（all-in-one） 或多节点的本地 Kubernetes 集群，以便你来尝试 Kubernetes 或者开展每天的开发工作。

4 kubeadman

 kubeadm 是官方社区推出的一个用于快速部署 kubernetes 集群的工具。 这个工具能通过两条指令完成一个 kubernetes 集群的部署： 

5 kubeke

kubershere安装  

https://kubesphere.io/zh/docs/v3.3/quick-start/minimal-kubesphere-on-k8s/

一键安装kubernetes  

6 Kuboard-Spray

图形化安装

https://kuboard.cn/install/install-k8s.html

https://docs.rancher.cn/docs/rancher2.5/cluster-provisioning/node-requirements/_index

7 二进制安装

难  一个知名讲师  对于一个熟练的讲师来说  是需要一整天      

必须要这样 不然 你出现问题你该怎么办     

https://www.kubernetes.org.cn/3096.html

https://blog.stanley.wang/2019/01/18/%E5%AE%9E%E9%AA%8C%E6%96%87%E6%A1%A31%EF%BC%9A%E8%B7%9F%E6%88%91%E4%B8%80%E6%AD%A5%E6%AD%A5%E5%AE%89%E8%A3%85%E9%83%A8%E7%BD%B2kubernetes%E9%9B%86%E7%BE%A4/

8 一键安装

运行这个脚本文件   vim kubernetes.sh

```
#!/bin/bash
# Author: 
 
RED="\033[31m"      # Error message
GREEN="\033[32m"    # Success message
YELLOW="\033[33m"   # Warning message
BLUE="\033[36m"     # Info message
PLAIN='\033[0m'
 
colorEcho() {echo -e "${1}${@:2}${PLAIN}"
}
 
checkSystem() {result=$(id | awk '{print $1}')
    if [[ $result != "uid=0(root)" ]]; then
        colorEcho $RED "请以 root 身份执行该脚本"
        exit 1
    fi
 
    res=`which yum 2>/dev/null`
    if [[ "$?" != "0" ]]; then
        res=`which apt 2>/dev/null`
        if [[ "$?" != "0" ]]; then
            colorEcho $RED "不受支持的 Linux 系统"
            exit 1
        fi
        PMT="apt"
        CMD_INSTALL="apt install -y"
        CMD_REMOVE="apt remove -y"
        CMD_UPGRADE="apt update && apt upgrade -y; apt autoremove -y"
    else
        PMT="yum"
        CMD_INSTALL="yum install -y"
        CMD_REMOVE="yum remove -y"
        CMD_UPGRADE="yum update -y"
    fi
	if [[ $PMT == "apt"   ]];then
	        colorEcho $RED "不受支持的 Linux 系统"
            exit 1
	fi
    res=`which systemctl 2>/dev/null`
    if [[ "$?" != "0" ]]; then
        colorEcho $RED "系统版本过低，请升级到最新版本"
        exit 1
    fi
}
 
 
preinstall() {
	hostnamectl set-hostname $ROLE-$HOSTNAME
    #$PMT clean all
    [[ "$PMT" = "apt" ]] && $PMT update
    #echo $CMD_UPGRADE | bash
    echo ""colorEcho $BLUE" 安装必要软件 "if [["$PMT"="yum" ]]; then
        $CMD_INSTALL epel-release
    fi
    $CMD_INSTALL curl vim docker
	res=`which curl 2>/dev/null`
    [[ "$?" != "0" ]] && $CMD_INSTALL curl
	res=`which docker 2>/dev/null`
    [[ "$?" != "0" ]] && $CMD_INSTALL docker
	systemctl enable --now docker
 
	colorEcho $BLUE "配置服务器环境"
    if [[ -s /etc/selinux/config ]] && grep 'SELINUX=enforcing' /etc/selinux/config; then
        sed -i 's/SELINUX=enforcing/SELINUX=permissive/g' /etc/selinux/config
        setenforce 0
    fi
	echo 1 > /proc/sys/net/ipv4/ip_forward
	echo 1 > /proc/sys/net/bridge/bridge-nf-call-iptables
	echo 'net.ipv4.ip_forward = 1' >>  /etc/sysctl.conf
	sysctl -p
	swapoff -a
}
 
installk8s(){
cat <<EOF > /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64/
enabled=1
gpgcheck=0
repo_gpgcheck=0
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
EOF
	colorEcho $BLUE '安装 k8s 组件'
	$CMD_INSTALL --nogpgcheck kubelet-1.23.5-0 kubeadm-1.23.5-0 kubectl-1.23.5-0
	systemctl enable --now kubelet
 
}
 
getip(){
	colorEcho $BLUE "请选择获取本机 ip/vip 的方式："
    echo "1) 网络获取"
    echo "2) 手动输入"
    read -p "请选择获取本机 ip/vip 的方式 (默认联网获取)：" answer
	if [[ -z "$answer" ]]; then
        METHOD="network"
    else
        case $answer in
        1)
            METHOD="network"
            ;;
		2)
            METHOD="matual"
            ;;
        *)
            colorEcho $RED "无效的选择，使用默认角色"
            METHOD="network"
        esac
    fi
    echo ""colorEcho $BLUE" 获取本机 ip/vip 的方式：$METHOD"if [[ $METHOD =='network' ]];then
		ip=`curl -s icanhazip.com`
	else
		read -p "请输入本机 ip/vip：" ip
	fi
	colorEcho $YELLOW "本机 ip/vip 地址为: $ip"
	read -p '确认安装请按回车键 (CTRL+ C 退出脚本)' a
}
 
 
init(){
cat <<-EOF >/root/kubeadm_init.yaml
apiVersion: kubeadm.k8s.io/v1beta3
kind: ClusterConfiguration
kubernetesVersion: v1.23.5
apiServer:
  certSANs:
  - "$ip"
controlPlaneEndpoint: "$ip:6443"
networking:
  podSubnet: 10.244.0.0/16
imageRepository: registry.cn-hangzhou.aliyuncs.com/google_containers
EOF
	kubeadm init --config=/root/kubeadm_init.yaml|tee /root/kubeinit.log
	mkdir -p $HOME/.kube
	cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
	chown $(id -u):$(id -g) $HOME/.kube/config
	kubectl apply -f https://raw.githubusercontent.com/flannel-io/flannel/master/Documentation/kube-flannel.yml
	colorEcho $GREEN 'k8s 安装完成'
}
 
greplog(){
        colorEcho $RED "关键命令，请注意保存"
        colorEcho $BLUE "如需部署集群"
        colorEcho $BLUE "请在其他 master 节点中执行下面命令"
        echo ` grep -E  -B 2 '\\-\\-control\-plane' /root/kubeinit.log|sed 's/\\\\//g'`
        colorEcho $BLUE "请在其他 node 节点执行完脚本后执行下面命令"
        echo `grep -E  -B 2 '\\-\\-control\-plane' /root/kubeinit.log |grep -v control|sed 's/\\\\//g'`
}
 
 
menu(){
	checkSystem
	colorEcho $BLUE "请选择本机的角色："
    echo "1)master"
    echo "2)node"
    read -p "请选择 k8s 角色（默认 master）" answer
	if [[ -z "$answer" ]]; then
        ROLE="master"
    else
        case $answer in
        1)
            ROLE="master"
            ;;
		2)
            ROLE="node"
            ;;
        *)
            colorEcho $RED "无效的选择，使用默认角色"
            ROLE="master"
        esac
    fi
    echo ""colorEcho $BLUE" 当前 k8s 角色：$ROLE"if [[ $ROLE =='master' ]];then
		colorEcho $BLUE "检测到您设置的角色为 master，请选择行为："
		echo "1) 建立新集群"
		echo "2) 加入旧集群"
		read -p "请选择 master 节点行为（默认建立新集群）" answer
		if [[ -z "$answer" ]]; then
			ACTION="new"
		else
			case $answer in
			1)
				ACTION="new"
				;;
			2)
				ACTION="old"
				;;
			*)
				colorEcho $RED "无效的选择，使用默认行为"
				ACTION="new"
			esac
		fi
		echo ""colorEcho $BLUE" 当前 k8s 节点行为：$ACTION"if [[ $ACTION =="new" ]];then
			getip
			preinstall
			installk8s
			init
			greplog
		else
			preinstall
			installk8s
			colorEcho $BLUE "master 节点安装完成，请在主 master 节点日志中拿取命令加入主节点"
			
		fi
 
	else
		preinstall
		installk8s
		colorEcho $BLUE "node 节点安装完成，请在主 master 节点日志中拿取命令加入主节点"
	fi
}
 
main(){menu}
main

```

chmod +x kubernetes.sh &&  ./kubernetessh

# 5 参考

阿里巴巴和CNCF https://developer.aliyun.com/learning/course/572/detail/7781   

阿里巴巴云原生实践课  https://developer.aliyun.com/learning/course/698?spm=a2c6h.21258778.0.0.4ab01a5fG46DGs&scm=20140722.ID_community@@course@@698.R_SeoRelatedItem2.ID_community@@course@@698-OR_rec-V_1

 中文官网：https://kubernetes.io/zh/ 

中文社区：https://www.kubernetes.org.cn/ 

官方文档：https://kubernetes.io/zh/docs/home/   

githup https://github.com/kubernetes

社区文档：http://docs.kubernetes.org.cn

KubeShere：https://kubesphere.com.cn/docs/v3.3/introduction/what-is-kubesphere/

minkube:  https://kubernetes.io/zh-cn/docs/tutorials/hello-minikube/    

K8S 原理   https://baijiahao.baidu.com/s?id=1721040222341192801

**kubord教程**  https://kuboard.cn/learning/k8s-practice/micro-service/kuboard-view-of-k8s.html#devops%E5%B9%B3%E5%8F%B0  

尚硅谷      https://www.yuque.com/leifengyang/oncloud/ghnb83

尚硅谷和云原生  https://www.bilibili.com/video/BV15g411F7pj/?spm_id_from=333.999.0.0&vd_source=8e232ecca082f1beea092de8718f15c6

https://www.bilibili.com/video/BV1rD4y1c7r1/?spm_id_from=333.999.0.0&vd_source=8e232ecca082f1beea092de8718f15c6

老男孩  https://www.bilibili.com/video/BV1QV411H7Gg?p=35&vd_source=8e232ecca082f1beea092de8718f15c6