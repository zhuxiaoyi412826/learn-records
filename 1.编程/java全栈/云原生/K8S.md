需要看到文档

https://www.macrozheng.com/k8s/k8s_start.html#%E5%85%AC%E5%BC%80%E6%9A%B4%E9%9C%B2%E5%BA%94%E7%94%A8

https://www.macrozheng.com/k8s/k3s_start.html#%E6%80%BB%E7%BB%93

https://www.macrozheng.com/k8s/rancher_desktop_start.html#%E5%8F%AF%E8%A7%86%E5%8C%96%E7%AE%A1%E7%90%86

***我在线上的所有操作所有变更 可以通过配置文件详细的做记录，方便后续去做溯源****

**声明式API** 

BFF

https://zhuanlan.zhihu.com/p/463196408

SFF

https://developer.aliyun.com/learning/course/847

# 云原生

## 什么是云原生

1 在公有云，私有云，混合云，基于容器，服务网格，微服务，不可变的基础设施和**声明式API**构建可弹性的应用

2 基于自动化技术构建具备高容错性，易管理和便于观察的松耦合系统

3 构建一个统一的开源云技术生态，能和云厂商提供的服务解耦

CNCF  云原生生态  https://landscape.cncf.io/

https://www.bilibili.com/video/BV1uL4y1c7HA/?spm_id_from=333.337.search-card.all.click&vd_source=8e232ecca082f1beea092de8718f15c6

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221208111226.png)



![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zha25de8844db5b0da3caceb0c3d4cb7f.jpg)

就好比 spirngboot 用yaml 配置 只用改配置不用改动代码 就可以出现不同的运行效果 

镜像一般是不会变得，容器是可变的 通过不同的参数启动不同的容器

一个镜像几百兆 

HELM  仓库就好比 yum install keepalived haproxy psmisc -y  下载安装应用

阿里云的云原生交付平台

云原生4C安全模型

![](https://kuboard.cn/assets/img/4c.0dc8b914.png)





![](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20230105161031935.png)

## 容器

### containerd

什么是containerd  containerd和docker的区别

https://www.cnblogs.com/yangmeichong/p/16661444.html





https://www.jianshu.com/p/f6ed4b440a0c

https://mdnice.com/writing/c57c45c557bf46b0835958134892abc4

安装步骤

unbantu安装

https://www.cnblogs.com/punchlinux/archive/2022/07/19/16496094.html

https://blog.csdn.net/weixin_46476452/article/details/127670046

cnetosos安装

https://blog.csdn.net/qq_34777982/article/details/124906096

### **docker**  

ubuntu 安装 https://blog.csdn.net/u012563853/article/details/125295985

0 或者使用脚本文件安装

vim docker.sh

```
#!/bin/bash
yum install -y yum-utils
yum-config-manager \
    --add-repo \
    https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
mkdir -p /etc/docker && touch /etc/docker/daemon.json
cat > /etc/docker/daemon.json <<END
{
  "registry-mirrors": ["https://3sf1ht53.mirror.aliyuncs.com"],
   "exec-opts": ["native.cgroupdriver=systemd"]
}                                                                                    
END
yum install docker-ce docker-ce-cli containerd.io
systemctl start docker
yum -y install conntrack socat ebtables ipset
```

chmod +x docker.sh  && ./docker.sh

1、使用uname命令验证 内核版本  

```
[root@localhost docker]# uname -r
3.10.0-1127.el7.x86_64
```

2、卸载已安装的Docker

如果已经安装过Docker，请先卸载，再重新安装，来确保整体的环境是一致的。

```
yum remove docker 
docker-client 
docker-client-latest 
docker-common 
docker-latest 
docker-latest-logrotate 
docker-logrotate docker-engine
```

3、安装yum工具包和存储驱动

```
yum install -y yum-utils && yum-config-manager \
    --add-repo \
    https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
```

4、安装docker

注意 : docker-ce 社区版 而ee是企业版。这里我们使用社区版即可。

```
yum install docker-ce docker-ce-cli containerd.io
为了防止k8s和docker兼容的问题 可以安装指定的版本
yum install -y docker-ce-19.03.14 docker-ce-cli-19.03.14
```

6、设置镜像加速

1、 编辑文件/etc/docker/daemon.json  这个需要自己创建

```text
# 执行如下命令： 
mkdir -p /etc/docker && vi /etc/docker/daemon.json 
```

2、在文件中加入下面内容

```text
{
  "registry-mirrors": ["https://3sf1ht53.mirror.aliyuncs.com"],
  "exec-opts": ["native.cgroupdriver=systemd"]
}                                                                                    
```

7、启动docker

```
systemctl start docker
```

8、设置开机启动

```
systemctl enable docker
```

如果遇到了这样的问题

```
Created symlink from /etc/systemd/system/multi-user.target.wants/docker.service to /usr/lib/systemd/system/docker.service.
```

```
systemctl stop firewalld.service
systemctl enable docker.service
```

# 理论

学习k8s就跟用word上，95%的人只会5%，而5%的知识可以看95%的事情，所以不要觉的k8s难  

学习路线陡峭

## 1 教程 

Kubernetes 简称 k8s。是用于自动部署，扩展和管理容器化应用程序的开源系统。

阿里巴巴和CNCF https://developer.aliyun.com/learning/course/572/detail/7781   推荐

阿里巴巴云原生实践课  https://developer.aliyun.com/learning/course/698?spm=a2c6h.21258778.0.0.4ab01a5fG46DGs&scm=20140722.ID_community@@course@@698.R_SeoRelatedItem2.ID_community@@course@@698-OR_rec-V_1

 中文官网：https://kubernetes.io/zh/ 

中文社区：https://www.kubernetes.org.cn/ 

官方文档：https://kubernetes.io/zh/docs/home/   需要基础

社区文档：http://docs.kubernetes.org.cn

KubeShere：https://kubesphere.com.cn/docs/v3.3/introduction/what-is-kubesphere/

minkube:  https://kubernetes.io/zh-cn/docs/tutorials/hello-minikube/    自己学习用的可以在线编程

K8S 原理   https://baijiahao.baidu.com/s?id=1721040222341192801

**kubord教程**  https://kuboard.cn/learning/k8s-practice/micro-service/kuboard-view-of-k8s.html#devops%E5%B9%B3%E5%8F%B0  

尚硅谷        https://www.yuque.com/leifengyang/oncloud/ghnb83

尚硅谷和云原生  https://www.bilibili.com/video/BV15g411F7pj/?spm_id_from=333.999.0.0&vd_source=8e232ecca082f1beea092de8718f15c6

https://www.bilibili.com/video/BV1rD4y1c7r1/?spm_id_from=333.999.0.0&vd_source=8e232ecca082f1beea092de8718f15c6

老男孩  https://www.bilibili.com/video/BV1QV411H7Gg?p=35&vd_source=8e232ecca082f1beea092de8718f15c6

**文章**

https://icloudnative.io/posts/what-happens-when-k8s/

kubectl 命令发生了什么  pod的启动过程  

## 2 什么是K8S

kubernets  k8s就是一个分布式资源调度的，进行容器编排云原生的操作系统 

 愿景是打造一个以 [Kubernetes](https://kubernetes.io/zh/) 为内核的 **云原生分布式操作系统**

“一切皆为资源”的设计是 Kubernetes 能够顺利施行声明式 API 的必要前提

对k8s集群管理就是管理k8的资源    K8S中所有的内容都抽象为资源，对资源进行增删查 改

资源实例化之后叫做对象。在K8S中，一般适用yaml格式的文件来创建符合我们预期的Pod，这样的yaml文件一般称为资源清单

**我们通过 kubernetes 的 API 来操作整个集群。 可以通过 kubectl、ui、curl 最终发送 http+json/yaml 方式的请求给 API Server，然后控制 k8s 集群。k8s 里的所有的资源对象都可以采用 yaml 或 JSON 格式的文件定义或描述**

管理K8S核心资源的三种基本方式

1 陈述式管理方式- 主要依赖命令行CLI工具进行管理  Kubectl

2 声明式管理方式 -主要依赖统一资源配置清单（manifest）进行管理

3 GUI管理方式图形界面

k8s和docker的区别

```
https://www.toutiao.com/article/6884006604748882435/?log_from=e3e089e5b989f_1669540015644
```

## 3 K8S的概念

![](https://kuboard.cn/assets/img/image-20190731221630097.21159851.png)

### 1 名称

概念

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

Node 是资源的提供者，（计算资源 存储资源 和网络资源）Pod 是资源的使用者，调度是将两者进行恰当的撮合。

### 2 组件

- 控制平面组件（Control Plane Components）
- kube-apiserver提供了资源操作的唯一入口，并提供认证、授权、访问控制、API注册和发现等机制；你可以运行 `kube-apiserver` 的多个实例
- controller manager负责维护集群的状态，比如故障检测、自动扩展、滚动更新等 是[控制平面](https://kubernetes.io/zh-cn/docs/reference/glossary/?all=true#term-control-plane)的组件， 负责运行[控制器](https://kubernetes.io/zh-cn/docs/concepts/architecture/controller/)进程。；
- kuube- scheduler负责资源的调度，按照预定的调度策略将Pod调度到相应的机器上 进行算法的调度 很重要；
- kubelet负责维护容器的生命周期，同时也负责Volume（CVI）和网络（CNI）的管理  干活用的就是干活的  获取某个pod的运行状态    定时汇报当前节点的状态   镜像和容器清理
- Container runtime负责镜像管理以及Pod和容器的真正运行（CRI）；
- kube-proxy负责为Service提供cluster内部的服务发现和负载均衡，在每个节点上运行网络的代理   serice资源的载体 建立了pod 网络和集群网络的关系 常用的流量调度模式  Ipvs 推荐 更高效   
- etcd  高可用的键值对数据库    用作 Kubernetes 的所有集群数据的后台数据库
- DNS：一个可选的DNS服务，用于为每个Service对象创建DNS记录，这样所有的Pod就可以通过DNS访问服务了。

除了核心组件，还有一些推荐的Add-ons：

- kube-dns负责为整个集群提供DNS服务
- Ingress Controller为服务提供外网入口
- Heapster提供资源监控
- Dashboard提供GUI
- Federation提供跨可用区的集群
- Fluentd-elasticsearch提供集群日志采集、存储与查
- coredns：可以为集群中的SVC创建一个域名IP的对应关系解析
- dashboard：给 K8S 集群提供一个 B/S 结构访问体系
- ingress controller：官方只能实现四层代理，INGRESS 可以实现七层代理
- federation：提供一个可以跨集群中心多K8S统一管理功能
- prometheus ：提供K8S集群的监控能力
- elk：提供 K8S 集群日志统一分析介入平台   

插件

- 核心插件  
- CNI网络插件    flannel/calico
- 服务发现用插件   coredns
- 服务暴露用插件     treefik
- Gui 管理插件     Dashboard

有状态应用（Stateful Application）与无状态应用（Stateless Application）说的是应用程序是否要自己持有其运行所需的数据，如果程序每次运行都跟首次运行一样，不会依赖之前任何操作所遗留下来的痕迹，那它就是无状态的；反之，如果程序推倒重来之后，用户能察觉到该应用已经发生变化，那它就是有状态的  需不需要数据持久化

### 3 核心概念

### **pod/Pod 控制器**

**pod**

pod 是可以在 Kubernetes 中创建和管理的、最小的可部署的计算单元，其中包含一个或多个应用容器。 这些容器相对紧密地耦合在一起。同一个Pod里的容器之间仅需通过localhost就能互相通信。







pod控制器 

Kubernetes 通过引入 Controller（控制器）的概念来管理 Pod 实例。在 Kubernetes 中，您应该始终通过创建 Controller 控制器来创建 Pod，而不是直接创建 Pod。控制器可以提供如下特性

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zha649f178ba7fb65e27d0f22be48f647.jpg)

当你创建一个pod你就是在告知 Kubernetes 系统，你想要的集群工作负载状态看起来应是什么样子的， 这就是 Kubernetes 集群所谓的 **期望状态（Desired State）**会朝着这个方向而且努力工作的

控制器类型 **Deployment**和**ReplicaSets**是为了无状态服务而设计的

- **self-healing（故障恢复）** 例如：当一个节点出现故障，控制器可以自动地在另一个节点调度一个配置完全一样的 Pod，以替换故障节点上的 Pod。

**Pod 容器组**代表了 Kubernetes 中一个独立的应用程序运行实例，该实例可能由单个容器或者几个紧耦合在一起的容器组成。

**pod网络**

一个pod都会有一个ip，集群中任意一个pod都可以通过pod分配的ip进行访问，但这只是在集群内部，集群外部是访问不同的 

  **Pod 内**的容器可以使用 `localhost` 互相通信

边车模式由一个`主应用程序（即Web应用程序）以及一个辅助容器组成`，该容器对您的应用程序是必不可少的，但不一定是应用程序本身的一部分。当你的容器启动的时候，这个边车容器会创建好你的网络和存储

**pod存储**

一个 Pod 可以设置一组共享的存储[卷](https://kubernetes.io/zh-cn/docs/concepts/storage/volumes/)。 Pod 中的所有容器都可以访问该共享卷，从而允许这些容器共享数据。 卷还允许 Pod 中的持久数据保留下来，即使其中的容器需要重新启动。

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221126184028.png)

**pod状态**

![](https://pics4.baidu.com/feed/8644ebf81a4c510fe6f690aa79058b24d42aa531.png@f_auto?token=9326285943177111ef2b0d0334c6498d)

**pod控制器**  

pod控制器 是pod启动的一种模板  用来保证k8s里启动的pod  按照预期的运行状态 

[工作负载](https://kubernetes.io/zh-cn/docs/concepts/workloads/)资源的控制器通常使用 **Pod 模板（Pod Template）** 来替你创建 Pod 并管理它们。

Pod 模板是包含在工作负载对象中的规范，用来创建 Pod。这类负载资源包括 [Deployment](https://kubernetes.io/zh-cn/docs/concepts/workloads/controllers/deployment/)、 [Job](https://kubernetes.io/zh-cn/docs/concepts/workloads/controllers/job/) 和 [DaemonSet](https://kubernetes.io/zh-cn/docs/concepts/workloads/controllers/daemonset/) 等。

修改 Pod 模板或者切换到新的 Pod 模板都不会对已经存在的 Pod 直接起作用。 如果改变工作负载资源的 Pod 模板，工作负载资源需要使用更新后的模板来创建 Pod， 并使用新创建的 Pod 替换旧的 Pod。

副本数 生命周期 健康状态

![](https://pics2.baidu.com/feed/5366d0160924ab18bbda3e6f2ea648c47a890b7b.jpeg@f_auto?token=ef2985b2bbd9a90cc32f4c9204759f53)

- pod工作流程

- 用户提交创建Pod的请求，可以通过API Server的REST API ，也可用Kubectl命令行工具，支持Json和Yaml两种格式；

- API Server 处理用户请求，存储Pod数据到Etcd；

- Schedule通过和 API Server的watch机制，查看到新的pod，尝试为Pod绑定Node。调度器用一组规则过滤掉不符合要求的主机，比如Pod指定了所需要的资源，那么就要过滤掉资源不够的主机，对上一步筛选出的符合要求的主机进行打分，在主机打分阶段，调度器会考虑一些整体优化策略进行调度；选择打分最高的主机，进行binding操作，结果存储到Etcd中；

- kubelet根据调度结果执行Pod创建操作。绑定成功后，会启动container。scheduler会调用API Server的API在etcd中创建一个bound pod对象，描述在一个工作节点上绑定运行的所有pod信息。运行在每个工作节点上的kubelet也会定期与etcd同步bound pod信息。

  

  一个pod yml文件解析

  ```
apiVersion: apps/v1
  kind: Deployment #该配置的类型，我们使用的是 Deployment
  metadata: #译名为元数据，即 Deployment 的一些基本属性和信息
  name: nginx-deployment #Deployment 的名称
  labels:
  app: nginx
  spec: #这是关于该Deployment的描述，可以理解为你期待该Deployment在k8s中如何使用
  replicas: 1 #使用该Deployment创建一个应用程序实例
  selector: #标签选择器，与上面的标签共同作用，目前不需要理解
  matchLabels: #选择包含标签app:nginx的资源
  app: nginx
  template: #这是选择或创建的Pod的模板
  metadata: #Pod的元数据
  labels: #Pod的标签，上面的selector即选择包含标签app:nginx的Pod
  app: nginx
  spec:
  containers:
  - name: nginx
  image: nginx:1.7.9
  ```

**pod生命周期**

Pod 在其生命周期中只会被[调度](https://kubernetes.io/zh-cn/docs/concepts/scheduling-eviction/)一次。 一旦 Pod 被调度（分派）到某个节点，Pod 会一直在该节点运行，直到 Pod 停止或者被终止

**init容器**

本页提供了 Init 容器的概览。Init 容器是一种特殊容器，在 [Pod](https://kubernetes.io/zh-cn/docs/concepts/workloads/pods/) 内的应用容器启动之前运行。Init 容器可以包括一些应用镜像中不存在的实用工具和安装脚本。

你可以在 Pod 的规约中与用来描述应用容器的 `containers` 数组平行的位置指定 Init 容器。

Init 容器与普通的容器非常像，除了如下两点：

- 它们总是运行到完成。
- 每个都必须在下一个启动之前成功完成。

如果 Pod 的 Init 容器失败，kubelet 会不断地重启该 Init 容器直到该容器成功为止



#### **Namespace**

一般有  名称空间级资源、集群级资源、元数据型资源

- 工作负载型资源： Pod、 ReplicaSet（ReplicationController在v1.11版本废弃）、Deployment、StatefulSet、DaemonSet、Job、CronJob
- 服务发现及负载均衡型资源：Service、Ingress等
- 配置与存储型资源：Volume、CSI（容器存储接口，可以扩展各种各样的第三方存储卷）
- 特殊类型的存储卷：ConfigMap（当配置中心来使用的资源类型）、Secret（保存敏感数据）、DownwardAPI（把外部环境中的信息输出给容器）

使用 资源来定义每一种逻辑概念（功能）  api版本  类别  元数据  定义清单 状态  

Namespace     一种能隔离k8s内部各种资源的方法  这就是名称空间  也可以被称为分组  组内不能有重名

default 默认的    kube-system  kube-public    也可以自定义   查询k8s里特定资源要带上想应的名称空间

#### **Labl  /labl**

**标签选择器**

Labl

标签是k8s特色的管理方式  ，便于分类管理资源对象

一个标签可以对应多个资源，多个资源也可以有多个标签

标签的组成  key=value  

与标签差不多的是注解    不过标签名称跟严格

Labl选择器 过滤标签  

基于等值关系   集合关系

#### **Service /Ingress**

Servic 简称svc

pod 服务发现与负载均衡   k8s把一组pod 公开为网络服务的  通过标签来选择的

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

#### **kube-poxy**

服务代理

Kubernetes 集群中的每个节点都运行了一个 `kube-proxy`，负责为 Service（ExternalName 类型的除外）提供虚拟 IP 访问。 Kubernetes 支持三种 proxy 

**网络模型**

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221125151606.png)

#### **存储**

Gllusterfs     NFS   k8s进行抽取一一组进行存储



![](https://pics0.baidu.com/feed/f31fbe096b63f624ca3fd1a29c1845f11b4ca393.jpeg@f_auto?token=775b654597ce447cdd49e56cbc131bb2)

- 一个容器组可以包含多个数据卷、多个容器
- 一个容器通过挂载点（volumnMount）决定某一个数据卷（Volumn）被挂载到容器中的什么路径 不同类型的数据卷对应不同的存储介质（图中仅列出了 nfs、PVC、ConfigMap 三种存储介质）

#### **配置集**

configmap

**存储卷**

pvc

#### **K8S网络**

**IP地址**

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

https://blog.csdn.net/liangzhiyang/article/details/106349195

### 4 k8s逻辑架构图

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh3cfad3b4a547fd5a75a3d9a356faf7b.jpg)

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

### 5 部署

![](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20221130150637788.png)

### 6 K8S 发布策略

https://blog.csdn.net/qq_42494960/article/details/119385952

## 7 k8s解决微服务四大问题

为什么说 k8s完美解决了 四大问题   三大指标 集群搭建

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221214200521.png)

但是并不够完美  Service Mesh **服务网格** 边车代理  istio

在pod 中注入一个通信代理服务器 pod起来之前先启动通信代理服务器

![](https://icyfenix.cn/assets/img/sidecar.4174a72d.png)

# k8s 安装

## 0 免费体验

**或者是在k8s官网上学习**

https://kubernetes.io/zh-cn/docs/tutorials/hello-minikube/

**阿里云体验快速入门**

https://developer.aliyun.com/adc/scenarioSeries/c14b6e6d2df2454dadc4263f166fa16b?spm=a2c6h.13788135.J_2488678810.10.35402444FW6HjH

**或者是在阿里云 开通ACK**



## 1 minikube 

单节点的k8s

这个没安装成功

**主要启动minikube不要用root用户**

https://www.macrozheng.com/k8s/k8s_start.html#kubernetes%E7%AE%80%E4%BB%8B

https://icyfenix.cn/appendix/deployment-env-setup/setup-kubernetes/setup-minikube.html

https://copyfuture.com/blogs-details/20210519093852152E

http://t.zoukankan.com/tssc-p-15119756.html

https://www.jeeinn.com/2022/07/1715/

https://www.jeeinn.com/2022/07/1715/

1 下载minikube

```
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
```

2 安装

```
install minikube-linux-amd64 /usr/local/bin/minikube
```

3 创建一个用户

```
# 创建用户
useradd -u 1024 -g docker minikube
# 设置用户密码
passwd macro
# 切换用户
su minikube
```

4 启动minikube

```
minikube start
```

5 下载kubectl

```
minikube version
```

如果下载不了

```
1添加yum源
cat > /etc/yum.repos.d/kubernetes.repo << EOF
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=0
repo_gpgcheck=0
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg
https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
EOF
2 yum -y install kubectl
```

6 复制kubectl命令

```
# 查找kubectl命令的位置
find / -name kubectl
# 找到之后复制到/bin目录下
cp /mydata/docker/volumes/minikube/_data/lib/minikube/binaries/v1.20.0/kubectl /bin/
# 直接使用kubectl命令
kubectl version
```

7 kubect version 

报错

```
The connection to the server localhost:8080 was refused - did you specify the right host or port?

```









这个是k8s单节点

[minkube (k8s.io)](https://minikube.sigs.k8s.io/docs/start/)

从官网上容易启动不起来   直接指定到阿里云上下载 

```
minikube start --image-repository=registry.cn-hangzhou.aliyuncs.com/google_containers--force
```

1  下载并安装

```
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
sudo install minikube-linux-amd64 /usr/local/bin/minikube
```

2 启动集群

 minikube start 

默认minikube 是不能以root用户登录的  如果让root用户登录需要加  --force

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221126140214.png)

 minikube start --force

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221126150059.png)

这里要等一会  不要动

3 下载kubectl

```shell
minikube kubectl -- get po -A
```

查看是否安装成功

kubectl version --client --output=yaml  

4 alias kubectl="minikube kubectl --"

5 minikube dashboard

如果minkube 安装不成功

## 2 kubeadm

kubeadm 是官方社区推出的一个用于快速部署 kubernetes 集群的工具。 这个工具能通过两条指令完成一个 kubernetes 集群的部署： 

 $ kubeadm init   创建一个master节点

$ kubeadm join  将一个 Node 节点加入到当前集群中 

这些都是参考文档

https://blog.csdn.net/qq_41822345/article/details/126679925

https://icyfenix.cn/appendix/deployment-env-setup/setup-kubernetes/setup-kubeadm.html

https://blog.csdn.net/tiny_du/article/details/123823093

https://developer.aliyun.com/article/1004968

是kubenetes 官方提供的 一个快速安装的工具   

https://blog.csdn.net/tiny_du/article/details/123823093

https://shimo.im/docs/gO3oxnybjbFBg9qD/read 

### **0 前置**

https://shimo.im/docs/gO3oxnybjbFBg9qD/read     主推荐这个

参考这个文档和  我之前自己安装的过程  是在公网上进行的  集群版本 1.23.1   和1.22.12

所有节点上安装 docker   kubeadman   kubelet、kubectl

docker  运行时容器  kubeadman 快速安装k8s的 kubectl  命令行操作节点 

kubelet    node节点的代理   来进行干活创建pod 管理网络 

修改主机名  master就用master   node就用node 

```
sudo hostnamectl set-hostname master 
bash
hostnamectl 检测 
```

如果节点不修改名称 可能会 

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221202125527.png)

### 1master主节点  

1 修改hosts文件   这里的ip 都要可以ping通  最好用这个内网ip  要和etho的那个保持一致

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221210160139.png)

```
vim /etc/hosts 
110.42.149.60 master
42.193.254.253 node
169.165.74.251 node
```

2 关闭swap分区   但是在云服务器上就不需要了

```
swapoff -a
```

```
swapoff -ased -i '/swap/s/^\(.*\)$/#\1/g' /etc/fstab
```

3 配置iptables的ACCEPT规则  云服务也是不需要

```
iptables -F && iptables -X && iptables -F -t nat && iptables -X -t nat && iptables -P FORWARD ACCEPT
```

4  设置系统参数   云服务器不需要  

```
vim /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
net.ipv4.ip_forward = 1
```

sysctl --system

### **2 安装docker**  

一键安装

ubuntu 安装 https://blog.csdn.net/u012563853/article/details/125295985

或者使用脚本文件安装

touch docker.sh

```
#!/bin/bash
yum install -y yum-utils
yum-config-manager \
    --add-repo \
    https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
mkdir -p /etc/docker && touch /etc/docker/daemon.json
cat > /etc/docker/daemon.json <<END
{
  "registry-mirrors": ["https://3sf1ht53.mirror.aliyuncs.com"]，
  "exec-opts": ["native.cgroupdriver=systemd"]
}                                                                                    
END
yum install docker-ce docker-ce-cli containerd.io
systemctl start docker
```

chmod +x docker.yml  && ./docker.yml

或者使用下面这个步骤进行安装

1、使用uname命令验证 内核版本  

```
[root@localhost docker]# uname -r
3.10.0-1127.el7.x86_64
```

2、卸载已安装的Docker

如果已经安装过Docker，请先卸载，再重新安装，来确保整体的环境是一致的。

```
yum remove docker 
docker-client 
docker-client-latest 
docker-common 
docker-latest 
docker-latest-logrotate 
docker-logrotate docker-engine
```

3、安装yum工具包和存储驱动

```
yum install -y yum-utils
```

4、设置镜像的仓库

```
用国内的，阿里云docker镜像
yum-config-manager \
    --add-repo \
    https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
```

5、安装docker

注意 : docker-ce 社区版 而ee是企业版。这里我们使用社区版即可。

```
yum install docker-ce docker-ce-cli containerd.io
为了防止k8s和docker兼容的问题 可以安装指定的版本
yum install -y docker-ce-19.03.14 docker-ce-cli-19.03.14
```

6、设置镜像加速

1、 编辑文件/etc/docker/daemon.json  这个需要自己创建

```text
# 执行如下命令： 
mkdir -p /etc/docker && touch /etc/docker/daemon.json
vi /etc/docker/daemon.json 
```

2、在文件中加入下面内容

```text
{
  "registry-mirrors": ["https://3sf1ht53.mirror.aliyuncs.com"]，
  "exec-opts": ["native.cgroupdriver=systemd"]
}
```

更改docker驱动 如果不更改docker的驱动 有可能kubelet启动不起来

7、启动docker

```
systemctl start docker
```

如果设置docker开机自启动

```
systemctl enable docker
```

```
Created symlink from /etc/systemd/system/multi-user.target.wants/docker.service to /usr/lib/systemd/system/docker.service.
```

```
systemctl stop firewalld.service && systemctl enable docker.service
```

### **3 添加yum 源** 

```
cat > /etc/yum.repos.d/kubernetes.repo << EOF
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=0
repo_gpgcheck=0
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg
https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
EOF
```

### **4 安装**

kubeadman kubelet、kubectl  指定版本 最好版本统一 

要注意docker 的版本和 组件的版本是否合适

```
yum install -y kubelet-1.17.3 kubeadm-1.17.3 kubectl-1.17.3  最好选择这一个  其他的两个也可以
yum install -y kubelet-1.23.1-0 kubeadm-1.23.1-0 kubectl-1.23.1-0
yum install -y kubelet-1.22.12-0 kubeadm-1.22.12-0 kubectl-1.22.12-0
```

```
kubectl version
kubelet --version
kubeadm version
```

### **5 设置开机自启**

systemctl restart kubelet

 开机启动

### **6 设置master**

如果网络不好  需要先下载镜像 然后在执行这个命令  否则可能镜像下载不下来

如果之前开始下载 可能因为网络  有的下载不下来  所以先下载镜像 

编写一个脚本文件   自己先下载

```
  vim  master_images.sh
```

```
#!/bin/bash
images=(
	kube-apiserver:v1.17.3
    kube-proxy:v1.17.3
	kube-controller-manager:v1.17.3
	kube-scheduler:v1.17.3
	coredns:1.6.5
	etcd:3.4.3-0
    pause:3.1
)
for imageName in ${images[@]} ; do
    docker pull registry.cn-hangzhou.aliyuncs.com/google_containers/$imageName
#   docker tag registry.cn-hangzhou.aliyuncs.com/google_containers/$imageName  k8s.gcr.io/$imageName
done

```

```
chmod 700 master_images.sh   赋予权限 
./master_images.sh  开始执行
```

下载好的镜像

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221120090433.png)

开始初始化master节点

```
kubeadm init \
  --apiserver-advertise-address=120.77.82.244 \
  --image-repository registry.cn-hangzhou.aliyuncs.com/google_containers \
  --kubernetes-version v1.23.1-0 \
  --service-cidr=10.96.0.0/16 \
  --pod-network-cidr=192.168.0.0/16 
```

```
kubeadm init \   设置主节点  的ip就是你的公网ip需要写的hosts文件里 
--kubernetes-version=1.23.1  指定k8s的版本
--apiserver-advertise-address=自己的ip     因为k8s的所有操作命令都要经过 apiserver   并设置主节点的地址 
--kubernetes-version 指定版本
--image-repository  默认是从k8s.io  现在改成阿里云的
service-cidr=10.96.0.0/16 \    对 不同节点  pod 之间网络的访问 ，集群内部虚拟网络，Pod统一访问入口
--pod-network-cidr=10.244.0.0/1   所在的ip  pod和 pod 之间  与下面部署的CNI网络组件yaml中保持一致
```

使用阿里云搭建k8s时，在主节点执行kubeadm init时候卡在

```
Waiting for the kubelet to boot up the control plane as static Pods from directory "/etc/kubernetes/manifests". This can take up to 4m0
```


这是因为kubeadm init 指定了"--apiserver-advertise-address"为公网ip，但是阿里云的机器是vpc网络，使用ifconfig时候，可以看到网卡上显示的是内网ip，并没有公网ip，这就会导致etcd无法启动，etcd 启动不了 kubelet也就启动不了 解决办法为去掉--apiserver-advertise-address参数。

如果初始化失败

```
echo "1" > /proc/sys/net/ipv4/ip_forward
nmcli c reload
https://www.cnblogs.com/fufengyuan/p/16382182.html
```

初始化成功

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221202130524.png)

跟着步骤进行操作   需要先复制 最后一行的  kubeadm的join 这是别的节点加入你的集群  有效时间2h

```
mkdir -p $HOME/.kube
cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
chown $(id -u):$(id -g) $HOME/.kube/config
```

```
vim /etc/profile
export KUBECONFIG="/etc/kubernetes/admin.conf"
source /etc/profile
```

join命令，需要加上token，如果忘记了，在主节点使用kubeadm token list命令查看。

K8s集群创建的时候，在主节点使用kubeadm init命令，如果第一次失败了，再次执行此命令发现提示端口已占用，文件已存在，怎么办？使用kubeadm reset 命令清空，然后重新init



**踩坑**

kubelet启动不起来

查看docker的驱动是否匹配

拷贝证书  和其他

1、K8s集群创建的时候，在主节点使用kubeadm init命令，如果第一次失败了，再次执行此命令发现提示端口已占用，文件已存在，怎么办？使用kubeadm reset 命令清空，然后重新init；非主节点上使用kubeadm join 主节点失败，使用kubeadm reset -f 命令清空之前的安装，重新执行init 命令。

2、node节点上执行 kubeadm join 主节点的时候，总是提示timeout；正常情况下是不显示Initial timeout of 40s passed，随后也不会提示 timed out waiting for the condition；错误提示显示使用 systemctl status kubelet -l查看详情，发现提示user:anonymous forbidden，node节点找不到；然后重新检查了hostname设置，/etc/hosts文件，发现配置没有问题，未果，关机；第二天又尝试解决这个问题，发现主节点开机就报内存不足的错误了，然后重新分配2G内存；没有在node节点上重新join，竟然妥妥的很顺利的成功了。

3、

### 7设置网络插件

下面两种插件选择一种就好了

**1 用fanl**

```
kubectl apply -f \ https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
```

2 如果fanl下载不了

3 用手动方式进行下载

4 配置fanl 

kubectl apply -f kube-flannel.yml    

如果此时报错 

```
The connection to the server localhost:8080 was refused - did you specify the right host or port?
```

解决方法

```
具体根据情况，此处记录linux设置该环境变量
方式一：编辑文件设置
	   vim /etc/profile
	   在底部增加新的环境变量 export KUBECONFIG=/etc/kubernetes/admin.conf
方式二:直接追加文件内容
	echo "export KUBECONFIG=/etc/kubernetes/admin.conf" >> /etc/profile

```

在次运行  kubectl apply -f kube-flannel.yml    

**下载calico网络插件**

```
kubectl create -f https://docs.projectcalico.org/manifests/calico.yaml
```

进行验证 不过这个是实时变化的

```
kubectl get pods --all-namespaces -w
```

验证pod健康

```
# 验证podkubectl get pods -n kube-system# 健康检查（不要怀疑，就是healthz）curl -k https://localhost:6443/healthz
```

这一步不知道 不过应该不需要 

```
## 开启内核支持
vim /etc/sysctl.conf 
net.ipv4.ip_forward = 1
net.bridge.bridge-nf-call-iptables = 1
net.bridge.bridge-nf-call-ip6tables = 1
EOF## 执行生效sysctl -p
## 开启ipvs支持
vim /etc/sysconfig/modules/ipvs.modules
modprobe -- ip_vs
modprobe -- ip_vs_rr
modprobe -- ip_vs_wrr
modprobe -- ip_vs_sh
modprobe -- nf_conntrack_ipv4
```

参考博客

```
https://blog.51cto.com/u_15080021/4000120

```

### 8 node 加入master

需要 docker   安装好 kubeadman kubelet、kubectl  指定版本 最好版本统一      网络通畅

这是我自己的tokn 你要加入你自己的就是之前生成的tokn

```
 kubeadm join 120.78.214.226:6443 --token fdw4nd.e1vakp1bxzut6gyk \--discovery-token-ca-cert-hash sha256:9b4f9c9c1c94dd77a1d331ddd19b80e10e4a25c5f439922b7aac9e9bf2949cee 
```

加入成功

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221202124654.png)

如果tokn过期了

需要自己在master节点上重新生成一个

```
1.通过下面的命令可以创建一个不过期的token
# kubeadm token create --ttl 0
p4rynu.uj4jaxnzk2s0y9vi						#这个值就是Token
把这个tokn进行替换即可
2.查看可用的token列表
# kubeadm token list
TOKEN                     TTL         EXPIRES   USAGES   
```

### 9 检测是否加入集群成功

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221202124252.png)

### 10远程操控

如果我想在node节点上操作k8s该怎么操作

如果直接使用kubect 会报错

```
　出现这个问题的原因是kubectl命令需要使用kubernetes-admin的身份来运行，在“kubeadm int”启动集群的步骤中就生成了“/etc/kubernetes/admin.conf”。
因此，解决方法如下，将主节点中的【/etc/kubernetes/admin.conf】文件拷贝到工作节点相同目录下：
用ssh登录或者用xftp 进行文件传输  
然后分别在工作节点上配置环境变量：
#设置kubeconfig文件
export KUBECONFIG=/etc/kubernetes/admin.conf
echo "export KUBECONFIG=/etc/kubernetes/admin.conf" >> ~/.bash_profile
刷新环境变量  source/etc/profile
然后输入密码即可
```

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221202135406.png)

任意一台机器只要安装了kubelet 都可以   按照上述操作即可进行远程操控我的k8s集群 

### 11如果已经是一个k8s想加入一个集群如何操作

不能直接添加否则会报错  你的kubelet正在运行

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221202124859.png)

### 12 如何用win远程连接k8s

```
https://blog.csdn.net/u010197332/article/details/125447978
```

### 后续

虽然安装成功了但是还有很多问题  比如docker和k8s版本的问题 如果安装新版本的docker或者是k8s 会不会出现不兼容问题。还有就是containerd 与docker的关系

## 3 kubeke安装

### 1 kubernetes集群

1 安装docker 

2   设置自己的主机名  为master  或者是node

设置的这个ip 需要 node 和master都可以ping通 也是eth0 网卡里的那个

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221210122008.png)

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221210121115.png)

3    安装kubekey    

```
安装依赖
yum -y install conntrack socat ebtables ipset
export KKZONE=cn && curl -sfL https://get-kk.kubesphere.io | VERSION=v3.0.2 sh -
```

4 生成 yml文件  config-sample.yaml  默认是这个文件 

```
./kk create config --with-kubernetes v1.22.12  --with-kubesphere v3.3.0
```

./kk create config --with-kubesphere v3.3.0

5 修改yml 文件

修改节点 ip 账号 密码  ssh登录   注意ip之间要可以互相通信  这个ip就是eth0网卡里的

 然后在修改 etcd 是在那个节点安装的，work节点是在哪里工作的      

```
 - {name: master, address: 172.29.202.90, internalAddress: 172.29.202.90, user: root, password: "412826zxyZXY"}
  - {name: node1, address: 172.29.202.89, internalAddress: 172.29.202.89, user: root, password: "412826zxyZXY"}
  - {name: node2, address: 172.29.202.88, internalAddress: 172.29.202.88, user: root, password: "412826zxyZXY"}
  
 roleGroups:
    etcd:
    - master
    control-plane:
    - master
    worker:
    - node1
    - node2
```

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221210121716.png)

6 然后应用yml 文件

```
 ./kk create cluster -f
```

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221210121306.png)

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221210121224.png)

**踩坑**

会报错etcd启动不起来

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221209201041.png)

systemctl start etcd  如果启动etcd

```
Job for etcd.service failed because the control process exited with error code. See "systemctl status etcd.service" and "journalctl -xe" for details.
```

解决方法 

1    把daocker镜像加速设置的这个给去掉

```
 "exec-opts": ["native.cgroupdriver=systemd"]  
```

2  在网卡etho 中设置自己的公网ip地址  或者新增加一个网卡

如果不设置也可以在 用ip通信的时候要使用内网通信

```
vim /etc/sysconfig/network-scripts/ifcfg-eth0                         
EOFBOOTPROTO=static                                                                  
DEVICE=eth0:1
IPADDR=110.42.149.60
PREFIX=32
TYPE=EthernetUSERCTL=no
ONBOOT=yes
```

3 ifconfig 来进行检查  看是否有自己的公网ip

### 2 第二种方式

1 安装docker  配置yum源

2 安装 依赖

```
yum -y install conntrack socat ebtables ipset
```

3  下载kubekey

```
export KKZONE=cn && curl -sfL https://get-kk.kubesphere.io | VERSION=v3.0.2 sh -
```

4 下载kubenetes

```
./kk create cluster --with-kubernetes v1.23.1
./kk create cluster --with-kubernetes v1.22.12
```

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221215125641.png)

当出现这个界面就代码kubernetes 安装成功

5 安装kubeshere  

**安装方式一**

```
./kk create config  --with-kubesphere v3.3.0
```

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221120153909.png)

6 访问测试 http://120.78.214.226:30880/login

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221209160648.png)

**安装方式二**

在k8s集群上安装kubeshere

前置条件  需要安装    conntrack  socat ebtables  ipsat  

docker   然后需要有默认存储类  kubectl get sc 

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221203150029.png)

如果没有需要自己创建  

vim a.yml     yml 内容如下

```yaml
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: standard
provisioner: kubernetes.io/aws-ebs
parameters:
  type: gp2
reclaimPolicy: Retain
allowVolumeExpansion: true
mountOptions:
  - debug
volumeBindingMode: Immediate
```

 kubectl apply -f a.yml    应用

https://kubernetes.io/zh-cn/docs/concepts/storage/storage-classes/

kubectl ges sc   查看存储类

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221215134347.png)

把创建的存储类刷新为默认的

 来跟新为默认存储类

```
kubectl patch storageclass 你的存储类名字 -p '{"metadata": {"annotations":{"storageclass.kubernetes.io/is-default-class":"true"}}}'
  
```

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221203161608.png)

安装kubeshere 

```
kubectl apply -f https://github.com/kubesphere/ks-installer/releases/download/v3.3.1/kubesphere-installer.yaml
   
kubectl apply -f https://github.com/kubesphere/ks-installer/releases/download/v3.3.1/cluster-configuration.yaml
容器启动需要时间
检查安装
kubectl logs -n kubesphere-system $(kubectl get pod -n kubesphere-system -l 'app in (ks-install, ks-installer)' -o jsonpath='{.items[0].metadata.name}') -f


```

1. 确保在安全组中打开了端口 `30880`，并通过 NodePort `(IP:30880)` 使用默认帐户和密码 `(admin/P@88w0rd)` 访问 Web 控制台。

**踩坑**

 不知道为什么kubeshere这个容器起不来   

解决 容器启动的时间要长一些  

![](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20221215135225099.png)

### 3 kubernetes+kubersher

这种方法有可能会出现超时  一直卡在哪里  也有可能会报错  如果安装超过10分钟 还没有安装成功 那就应该是安装失败了

这种方法不建议使用 也会报错

![](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20221215131257058.png)

1 安装docker  配置yum源

2 安装 依赖

yum -y install conntrack socat ebtables ipset

3  下载kubekey

```
export KKZONE=cn && curl -sfL https://get-kk.kubesphere.io | VERSION=v3.0.2 sh -
```

4 下载kubenetes

```
chmod +x kk
./kk create cluster --with-kubernetes v1.22.12 --with-kubesphere v3.3.0
```

https://kubesphere.com.cn/docs/v3.3/quick-start/all-in-one-on-linux/

在用kubershere安装k8s的时候docker的版本要>20

https://kubesphere.com.cn/docs/v3.3/quick-start/all-in-one-on-linux/

```
检验
kubectl logs -n kubesphere-system $(kubectl get pod -n kubesphere-system -l 'app in (ks-install, ks-installer)' -o jsonpath='{.items[0].metadata.name}') -f  
```

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221120153909.png)

开始访问 注意上面的这个ip 要换成你自己的   http://120.46.214.226:30880/login

如果要忘了密码怎么办

```
kubectl patch users <username> -p '{"spec":{"password":"<password>"}}' --type='merge' && kubectl annotate users <username> iam.kubesphere.io/password-encrypted-
```

<username> 就是你的账号名

<password>  就是你的密码

安装后的镜像  k8s+kubeshere

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221127171800.png)

想这个就是有个组件超时失败了  也就是kubeshere容器启动不成功

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221127171549.png)



### 4 kubeshere 高可用安装

高可用安装和集群安装都差不多 只不过

```
开启内置高可用模式，需要将 internalLoadbalancer 字段取消注释。
config-sample.yaml 文件中的 address 和 port 应缩进两个空格。
负载均衡器默认的内部访问域名是 lb.kubesphere.local。
```

```
 hosts:
  - {name: master1, address: 172.29.202.104, internalAddress: 172.29.202.104, user: root, password: "412826zxyZXY"}
  - {name: master2, address: 172.29.202.102, internalAddress: 172.29.202.102, user: root, password: "412826zxyZXY"}
  - {name: node1, address: 172.29.202.100, internalAddress: 172.29.202.100, user: root, password: "412826zxyZXY"}
  - {name: node2, address: 172.29.202.101, internalAddress: 172.29.202.101, user: root, password: "412826zxyZXY"}
  roleGroups:
    etcd:
    - master1
    - master2
    control-plane:
    - master1
    - master2
    worker:
    - node1
    - node2
  controlPlaneEndpoint:
    ## Internal loadbalancer for apiservers 
     internalLoadbalancer: haproxy
     domain: lb.kubesphere.local
     address: ""
     port: 6443
```

## 4 Rancher 安装

官网

https://ranchermanager.docs.rancher.com/zh/pages-for-subheaders/install-upgrade-on-a-kubernetes-cluster

```
[root@k8s-master01 ~]# docker run --privileged -d --name rancher --restart=unless-stopped -p 80:80 -p 443:443 -v /opt/rancher:/var/lib/rancher rancher/rancher:v2.5.11
[root@k8s-master01 ~]# docker ps | grep rancher
```

https://www.bilibili.com/video/BV1sA4y1f7y8/?spm_id_from=333.999.0.0&vd_source=8e232ecca082f1beea092de8718f15c6

## 5 Kuboard-Spray

图形化安装

https://kuboard.cn/install/install-k8s.html

https://docs.rancher.cn/docs/rancher2.5/cluster-provisioning/node-requirements/_index

要注意它的docker版本要在20.0版本以上    **注意 Kuboard-Spray**和k8s不能安装到同一台服务器里面 

**而且安装k8s的机器里面是干净的没有安装过docker和k8s  而且 Kuboard-Spray和安装k8s的机器只能进行内网通信。通过公网会找不到ip**

还有这个kuboard-spray的版本就选择在1.1.0太高或者太低都会有问题 提升你更新忽略就行

```
docker run -d \
  --privileged \
  --restart=unless-stopped \
  --name=kuboard-spray \
  -p 88:80/tcp \
  -e TZ=Asia/Shanghai \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /kuboard-spray-data:/data \
  eipwork/kuboard-spray:v1.1.0-amd64
```

访问的话就是本机ip    账号admin 密码 Kuboard123

根据界面进行一步一步的安装

注意点 就是



![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221203130854.png)

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221215133214.png)

## 6 二进制安装

难  一个知名讲师  对于一个熟练的讲师来说  是需要一整天      

必须要这样 不然 你出现问题你该怎么办     

https://www.kubernetes.org.cn/3096.html

https://blog.stanley.wang/2019/01/18/%E5%AE%9E%E9%AA%8C%E6%96%87%E6%A1%A31%EF%BC%9A%E8%B7%9F%E6%88%91%E4%B8%80%E6%AD%A5%E6%AD%A5%E5%AE%89%E8%A3%85%E9%83%A8%E7%BD%B2kubernetes%E9%9B%86%E7%BE%A4/

**1 安装bind9**   

http://www.21yunwei.com/archives/4803

1 安装  

```
yum install bind -y    
```

2 查看bind版本

rpm -qa bind

**2 常用工具**

```
 yum install wget net-tools telnet tree nmap sysstat lrzsz dos2unix bind-utils -y
```

3  检测是否有问题

named-checkconf 

## 7 高可用 













两地三活

![](https://mmbiz.qpic.cn/mmbiz_jpg/A1HKVXsfHNlAFj5t564vH2pmlJCCEqpg8vLWZbWHdTicG3RX6b9vBpmOsIjIKNibPWQI2JqJWH4NNe7Rb1Wquia4Q/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

两地三中心要解决的一个重要问题就是数据一致性问题。Kubernetes使用etcd组件作为一个高可用、强一致性的服务发现存储仓库。用于配置共享和服务发现。

它作为一个受到ZooKeeper和Doozer启发而催生的项目。除了拥有他们的所有功能之外，还拥有以下4个特点：

- 简单：基于http+json的API让你用curl命令就可以轻松使用。
- 安全：可选SSL客户认证机制。
- 快速：每个实例每秒支持一千次写操作。
- 可信：使用Raft算法充分实现了分布式

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221130173609.png)

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221130173740.png)

k8s主节点 加入主节点

```
https://blog.csdn.net/weixin_42082634/article/details/126306752
```

高可用安装k8s

https://zahui.fan/posts/b86d9e9f/

https://blog.csdn.net/KW__jiaoq/article/details/123292056

需要解决主节点加入主节点 形成控制平面如何解决	

### 1 二进制高可用

### 2 kubeke 高可用安装

高可用安装和集群安装都差不多 只不过

```
开启内置高可用模式，需要将 internalLoadbalancer 字段取消注释。
config-sample.yaml 文件中的 address 和 port 应缩进两个空格。
负载均衡器默认的内部访问域名是 lb.kubesphere.local。
```

```
 hosts:
  - {name: master1, address: 172.29.202.104, internalAddress: 172.29.202.104, user: root, password: "412826zxyZXY"}
  - {name: master2, address: 172.29.202.102, internalAddress: 172.29.202.102, user: root, password: "412826zxyZXY"}
  - {name: node1, address: 172.29.202.100, internalAddress: 172.29.202.100, user: root, password: "412826zxyZXY"}
  - {name: node2, address: 172.29.202.101, internalAddress: 172.29.202.101, user: root, password: "412826zxyZXY"}
  roleGroups:
    etcd:
    - master1
    - master2
    control-plane:
    - master1
    - master2
    worker:
    - node1
    - node2
  controlPlaneEndpoint:
    ## Internal loadbalancer for apiservers 
     internalLoadbalancer: haproxy
     domain: lb.kubesphere.local
     address: ""
     port: 6443
```

架构图

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221210115533.png)

## 8 k3s

k3s官网 

https://docs.rancher.cn/docs/k3s/quick-start/_index

### 安装

#### 快速安装

```
curl -sfL https://get.k3s.io | sh -
```

```
systemctl start k3s       开启k3s服务
```

kubectl get ns

https://blog.csdn.net/llliarby/article/details/110429743  基本操作

#### 离线安装

安装步骤

https://docs.k3s.io/zh/installation/airgap

#### 多节点安装



### **参考文章**

https://blog.csdn.net/wq1205750492/article/details/124823514

## 9 k8s集群管理

### Dashboard

### Kuboard

**通过容器安装**

```
docker run -d \
  --restart=unless-stopped \
  --name=kuboard \
  -p 18001:80/tcp \
  -p 10081:10081/tcp \
  -e KUBOARD_ENDPOINT="http://43.138.137.168:18001" \
  -e KUBOARD_AGENT_SERVER_TCP_PORT="10081" \
  -v /root/kuboard-data:/data \
  eipwork/kuboard:v3
```

上面的ip填写本机的ip

访问测试  http://43.138.137.168:18001



![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221203130339.png)

然后进行连接有两种方式一种是通过

cat ~/.kube/config   命令获取你的集群配置文件  另一种方式是代理连接  

填写这些信息

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221206202525.png)

在你的集群上执行  cat ~/.kube/config 

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221206202620.png)

可以导入多个集群

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221206202736.png)

https://kuboard.cn/install/v3-upgrade.html#%E5%A6%82%E6%9E%9C%E4%BB%A5-docker-run-%E8%BF%90%E8%A1%8C-kuboard

https://kuboard-spray.cn/guide/install-k8s.html#%E8%AE%BF%E9%97%AE%E9%9B%86%E7%BE%A4

![](https://kuboard.cn/assets/img/image-20190731230110206.fbb88459.png)

**通过k8s来安装 kuborad v2**

1 官方安装教程

https://kuboard.cn/install/install-dashboard.html#%E5%9C%A8%E7%BA%BF%E4%BD%93%E9%AA%8C

2  下载安装 

```
kubectl apply -f https://kuboard.cn/install-script/kuboard.yaml
kubectl apply -f https://addons.kuboard.cn/metrics-server/0.3.7/metrics-server.yaml
```

3 检查运行结果

```
kubectl get pods -l k8s.kuboard.cn/name=kuboard -n kube-system
```

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221127083114.png)

4 获取tokn  在第一次登录的时候需要tokn

```
echo $(kubectl -n kube-system get secret $(kubectl -n kube-system get secret | grep ^kuboard-user | awk '{print $1}') -o go-template='{{.data.token}}' | base64 -d)
```

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221127083305.png)

5 访问测试

访问本机ip:32567     http://120.46.214.226:32567

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221127082823.png)

### kubershere

### Rancher

https://docs.rancher.cn/docs/rancher2.5/cluster-provisioning/node-requirements/_index

## 10 一键安装k8s集群

**sealer**

https://zhuanlan.zhihu.com/p/501145408

https://github.com/alibaba/CNStackCommunityEdition

http://events.jianshu.io/p/f02fb5f3c68f

一键安装k8s

```
https://lab.openanolis.cn/#/apply/course/detail/158?courseId=111&chapterId=158&title=%E4%BD%93%E9%AA%8C%E9%BE%99%E8%9C%A5%E5%B9%B3%E5%8F%B0%E4%B8%8B%E8%8B%B1%E7%89%B9%E5%B0%94CRI-RM
```

**Docker Engine 没有实现 [CRI](https://kubernetes.io/zh-cn/docs/concepts/architecture/cri/)， 而这是容器运行时在 Kubernetes 中工作所需要的。 为此，必须安装一个额外的服务 [cri-dockerd](https://github.com/Mirantis/cri-dockerd)。 cri-dockerd 是一个基于传统的内置 Docker 引擎支持的项目， 它在 1.24 版本从 kubelet 中[移除](https://kubernetes.io/zh-cn/dockershim)。**

**配置 cgroup 驱动程序**[ ](https://kubernetes.io/zh-cn/docs/setup/production-environment/tools/kubeadm/install-kubeadm/#configuring-a-cgroup-driver)

容器运行时和 kubelet 都具有名字为 ["cgroup driver"](https://kubernetes.io/zh-cn/docs/setup/production-environment/container-runtimes/) 的属性，该属性对于在 Linux 机器上管理 CGroups 而言非常重要。

https://iyunwei.xyz/2022/12/15/%e4%b8%80%e9%94%ae%e6%90%ad%e5%bb%bak8s%e9%9b%86%e7%be%a4/

```
bash <(curl -sL https://iyunwei.xyz/wp-content/uploads/2022/12/k8s.sh)
```

源代码

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



# 实际操作

https://www.yuque.com/leifengyang/oncloud/ghnb83

管理k8s集群就是增删改查    就是CRUD  

管理K8S核心资源的三种基本方式

1 陈述式管理方式- 主要依赖命令行CLI工具进行管理  Kubectl

2 声明式管理方式 -主要依赖统一资源配置清单（manifest）进行管理

3 GUI管理方式图形界面   

## 常用命令

cat ~/.kube/config  查看集群配置文件   

kubectl delete namespace  命名空间  kubectl 删除命名空间



## kubectl

使用以下语法从终端窗口运行 `kubectl` 命令：

```shell
kubectl [command] [TYPE] [NAME] [flags]
```

其中 `command`、`TYPE`、`NAME` 和 `flags` 分别是：

- `command`：指定要对资源进行 **创建 查找 相信信息 删除**create`、`get`、`describe`、`delete`。

- `TYPE`：指定[资源类型](https://kubernetes.io/zh-cn/docs/reference/kubectl/#resource-types)。资源类型不区分大小写， 可以指定单数、复数或缩写形式。

- `NAME`：指定资源的名称。名称区分大小写。 如果省略名称，则显示所有资源的详细信息。例如：`kubectl get pods`。

  在对多个资源执行操作时，你可以按类型和名称指定每个资源，或指定一个或多个文件：

- 要对所有类型相同的资源进行分组，请执行以下操作：`TYPE1 name1 name2 name<#>`。
  例子：`kubectl get pod example-pod1 example-pod2`

- 分别指定多个资源类型：`TYPE1/name1 TYPE1/name2 TYPE2/name3 TYPE<#>/name<#>`。
  例子：`kubectl get pod/example-pod1 replicationcontroller/example-rc1`

- 用一个或多个文件指定资源：`-f file1 -f file2 -f file<#>`

- [使用 YAML 而不是 JSON](https://kubernetes.io/zh-cn/docs/concepts/configuration/overview/#general-configuration-tips)， 因为 YAML 对用户更友好, 特别是对于配置文件。
  例子：`kubectl get -f ./pod.yaml`

- `flags`： 指定可选的参数。例如，可以使用 `-s` 或 `--server` 参数指定 Kubernetes API 服务器的地址和端口。

**1 kubectl get namespace**

1 kubectl get namespace

查看名称空间  default  默认的      namespace 可以简称ns    我们在查看资源的时候一定要带上名称空间 -n 

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221124123855.png)

kubectl get ns

2  kubectl get all -n default

查看名称空间所有的资源

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221124124316.png)

当你的资源在默认的名称空间是可以不用的

kubectl get all

3 kubectl create/delete namespace app 

创建和删除 名称空间

 ![](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20221124124800757.png)

4  Deployment   

containers:registry.cn-hangzhou.aliyuncs.com/acr-toolkit/ack-cube

```
kubectl create deployment nginx-dp --image=registry.cn-shenzhen.aliyuncs.com/zhuxiaoyi/reggie:nginx_v1 -n kube-public
创建在 kube-public 的命名空间   如果不创建就是在默认的名称空间 default
kubectl get deployment  -n kube-public   
查看Deployment的资源   如果不加-n查看的就是默认的名称空间
```

这个命令的意思是 

我在kube-public的命名空间声明创建了一个pod控制器，这个pod控制器的类型是Deployment的

 pod控制器的镜像是在阿里云仓库的reggie仓库，Nginx镜像名

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221124125459.png)

5 查看pod资源

```
kubectl get pods -n kube-public   如果不指定名称空间就是默认的
查看pods的状态
-o wide   更加相信的查看
```

![](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20221124130647890.png)

```
describe   详细查看
kubectl describe pods -n zzxt 
```

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221127091809.png)

6 进到pod容器

docker exec   

kubectl   exec                           都一样  

只不过这个可以跨主机    docker ps 可以 用| 

7 describe 详细查看

```
kubectl describe deployment nginx-dp -n kube-public
```

![](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20221124142742355.png)

8 删除pod资源

删除*-*-也是重启的一种方式 你在删除会重新调度生成

delete 删除   是重启pod一种方式   

```
kubectl delet pod mytomcat-6f5f895f4f-c97vz 
kubectl delete --all pods  删除所有的pod
```

--force   --gace-period=0  强制删除

9 删除 deployment

```
$ kubectl delete deploy nginx-dp -n kube-public
```

![](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20221124145421327.png)

![](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20221124145546255.png)

10 管理Service资源 

```
kubectl expose deployment nginx-dp --port=80 -n kube-public
```

相比之前多了一个service 资源  这个ip是对集群内部使用的  虚拟网络

![](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20221124150850362.png)

查看service

```
kubectl describe svc nginx-dp -n kube-public
```

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221124151510.png)





kubectl   总结  

kubernets集群管理资源的唯一入口是通过相应的方法调用apiserver的接口

kubectl 是官方的CLI命令工具，用于apiserver进行通信，然后管理k8s各种资源的有效工具 

可以管理90% 以上的需求

命令冗长    特定场景下无法实现

增 删  查 快     改就慢

扩容操作

```
kubectl scale deployment nginx-dp --replicas=2 -n kube-public
```

**demo**

![](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20221127110804823.png)

1  



registry.cn-hangzhou.aliyuncs.com/acr-toolkit/ack-cube

## yml

资源配置清单

生命式资源管理 方式是依赖于统一资源配置清单来进行管理   

1 查看pod的资源配置清单  以yml格式展示的 

```
kubectl get pods nginx-dp-5d9dbbd8db-m7t7l -o yaml -n kube-public
```

![](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20221124153319258.png)

2 解释资源配置清单  kubectl explain  service

如果那个段落看不懂  

kubectl explain  service.metada(需要解释的段落)

给你解释的意思  也是帮助文档

3   创建资源配置清单 

vi nginx-ds-svc.yaml              创建yml文件

kubectl create -f nginx-ds-svc.yaml        创建资源配置清单

4 修改资源配置清单 

在线修改  

kubectl edit svc

离线修改

apply -f

应用配置清单

5 删除资源配置清单  

kubectl delete -f  资源文件 

学习方式 

1 多看别人写的 等读懂

2  会改别人的 

3  遇到不懂得 会用kubectl explain

4  切记不要不懂，自己憋着写

yml  

```
# yaml格式的pod定义文件完整内容：
apiVersion: v1       #必选，版本号，例如v1
kind: Pod       #必选，Pod
metadata:       #必选，元数据
  name: string       #必选，Pod名称
  namespace: string    #必选，Pod所属的命名空间
  labels:      #自定义标签
    - name: string     #自定义标签名字
  annotations:       #自定义注释列表
    - name: string
spec:         #必选，Pod中容器的详细定义
  containers:      #必选，Pod中容器列表
  - name: string     #必选，容器名称
    image: string    #必选，容器的镜像名称
    imagePullPolicy: [Always | Never | IfNotPresent] #获取镜像的策略 Alawys表示下载镜像 IfnotPresent表示优先使用本地镜像，否则下载镜像，Nerver表示仅使用本地镜像
    command: [string]    #容器的启动命令列表，如不指定，使用打包时使用的启动命令
    args: [string]     #容器的启动命令参数列表
    workingDir: string     #容器的工作目录
    volumeMounts:    #挂载到容器内部的存储卷配置
    - name: string     #引用pod定义的共享存储卷的名称，需用volumes[]部分定义的的卷名
      mountPath: string    #存储卷在容器内mount的绝对路径，应少于512字符
      readOnly: boolean    #是否为只读模式
    ports:       #需要暴露的端口库号列表
    - name: string     #端口号名称
      containerPort: int   #容器需要监听的端口号
      hostPort: int    #容器所在主机需要监听的端口号，默认与Container相同
      protocol: string     #端口协议，支持TCP和UDP，默认TCP
    env:       #容器运行前需设置的环境变量列表
    - name: string     #环境变量名称
      value: string    #环境变量的值
    resources:       #资源限制和请求的设置
      limits:      #资源限制的设置
        cpu: string    #Cpu的限制，单位为core数，将用于docker run --cpu-shares参数
        memory: string     #内存限制，单位可以为Mib/Gib，将用于docker run --memory参数
      requests:      #资源请求的设置
        cpu: string    #Cpu请求，容器启动的初始可用数量
        memory: string     #内存清楚，容器启动的初始可用数量
    livenessProbe:     #对Pod内个容器健康检查的设置，当探测无响应几次后将自动重启该容器，检查方法有exec、httpGet和tcpSocket，对一个容器只需设置其中一种方法即可
      exec:      #对Pod容器内检查方式设置为exec方式
        command: [string]  #exec方式需要制定的命令或脚本
      httpGet:       #对Pod内个容器健康检查方法设置为HttpGet，需要制定Path、port
        path: string
        port: number
        host: string
        scheme: string
        HttpHeaders:
        - name: string
          value: string
      tcpSocket:     #对Pod内个容器健康检查方式设置为tcpSocket方式
         port: number
       initialDelaySeconds: 0  #容器启动完成后首次探测的时间，单位为秒
       timeoutSeconds: 0   #对容器健康检查探测等待响应的超时时间，单位秒，默认1秒
       periodSeconds: 0    #对容器监控检查的定期探测时间设置，单位秒，默认10秒一次
       successThreshold: 0
       failureThreshold: 0
       securityContext:
         privileged:false
    restartPolicy: [Always | Never | OnFailure]#Pod的重启策略，Always表示一旦不管以何种方式终止运行，kubelet都将重启，OnFailure表示只有Pod以非0退出码退出才重启，Nerver表示不再重启该Pod
    nodeSelector: obeject  #设置NodeSelector表示将该Pod调度到包含这个label的node上，以key：value的格式指定
    imagePullSecrets:    #Pull镜像时使用的secret名称，以key：secretkey格式指定
    - name: string
    hostNetwork:false      #是否使用主机网络模式，默认为false，如果设置为true，表示使用宿主机网络
    volumes:       #在该pod上定义共享存储卷列表
    - name: string     #共享存储卷名称 （volumes类型有很多种）
      emptyDir: {}     #类型为emtyDir的存储卷，与Pod同生命周期的一个临时目录。为空值
      hostPath: string     #类型为hostPath的存储卷，表示挂载Pod所在宿主机的目录
        path: string     #Pod所在宿主机的目录，将被用于同期中mount的目录
      secret:      #类型为secret的存储卷，挂载集群与定义的secre对象到容器内部
        scretname: string  
        items:     
        - key: string
          path: string
      configMap:     #类型为configMap的存储卷，挂载预定义的configMap对象到容器内部
        name: string
        items:
        - key: string

```



# 项目实战

部署  

配置一个应用所需要考虑的东西

![](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20221130150544488.png)

什么是[KubeSphere](https://kubesphere.io/) 

部署三要素

1  应用的部署方式

2  应用的数据挂载  （数据 配置）

3  网络访问的方式



## 

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh058f3e03bca569a94a3c07b74e77495.jpg)

## 若依项目

https://gitee.com/y_project/RuoYi-Cloud

若依模块的启动顺序

```
com.ruoyi     
├── ruoyi-ui              // 前端框架 [80]
├── ruoyi-gateway         // 网关模块 [8080]
├── ruoyi-auth            // 认证中心 [9200]
├── ruoyi-api             // 接口模块
│       └── ruoyi-api-system                          // 系统接口
├── ruoyi-common          // 通用模块
│       └── ruoyi-common-core                         // 核心模块
│       └── ruoyi-common-datascope                    // 权限范围
│       └── ruoyi-common-datasource                   // 多数据源
│       └── ruoyi-common-log                          // 日志记录
│       └── ruoyi-common-redis                        // 缓存服务
│       └── ruoyi-common-seata                        // 分布式事务
│       └── ruoyi-common-security                     // 安全模块
│       └── ruoyi-common-swagger                      // 系统接口
├── ruoyi-modules         // 业务模块
│       └── ruoyi-system                              // 系统模块 [9201]
│       └── ruoyi-gen                                 // 代码生成 [9202]
│       └── ruoyi-job                                 // 定时任务 [9203]
│       └── ruoyi-file                                // 文件服务 [9300]
├── ruoyi-visual          // 图形化管理模块
│       └── ruoyi-visual-monitor                      // 监控中心 [9100]
├──pom.xml                // 公共依赖
```

若依架构图

![](https://oscimg.oschina.net/oscnet/up-82e9722ecb846786405a904bafcf19f73f3.png)

**本地启动**

1 下载 nacos

```
https://github.com/alibaba/nacos/releases/tag/2.2.0
```

修改conf文件夹下的 application.properties

```

spring.datasource.platform=mysql
db.num=1
db.url.0=jdbc:mysql://127.0.0.1:3306/nacos?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useUnicode=true&useSSL=false&serverTimezone=UTC
db.user.0=root
db.password.0=412826zxyZXY
nacos的账号密码 和连接的数据库 

```

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221215181151.png)

在MySQL中创建数据库 cacos 并导入 nacos-mysql.sql 文件

启动nacos   以单节点启动

```
startup.cmd -m standalone
```

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221215181420.png)

访问测试 http://localhost:8848/nacos/

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221215182247.png)

**nacos 查询不到数据库存储的配置**

**修改nacos配置的配置文件**

修改配置文件改为自己的数据库

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221215191006.png)

启动若依前端 

npm install  npm run dev 

访问测试

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221215192017.png)



启动getway

```
Error running 'RuoYiGatewayApplication': No JDK for module 'ruoyi-gateway'
```

解决方法 勾选这个jdk 

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221215193703.png)

然后在启动其他服务

**上云部署**

中间件 有状态          微服务 无状态 制作镜像 

网络 如何访问           配置 生产和配置分离



安装mysql  并吧数据从本地移植到云端



在k8s上启动nacos  mysql redis  

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221215205134.png)

这个是MySQL的密码 

部署中间件  nacoss mysql redis

**java微服务上云**

java微服务打jar包   制作dockerfile 打成镜像  推送镜像    应用部署 

# k8s生态

## 1 网络插件    

K8s 有三套网络   pod网络   node节点网络  service  集群网络

- 同一个Pod内的多个容器之间。这个是通过lo回路在机器内寻址   
- 当两个Pod在同一个节点主机。由网桥直接转发至对应Pod，不用经过Flannel。   
- 当两个Pod在不同的节点主机，不同Node之间的通信智能通过宿主机的物理网卡进行。

![](https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg2020.cnblogs.com%2Fblog%2F1260325%2F202004%2F1260325-20200423105221720-316781379.png&refer=http%3A%2F%2Fimg2020.cnblogs.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1671797075&t=a8e9fb631f6f3530444c5ebf217d831c)

kubernets 设计了网络模型 但却将它的实现交给了网络插件 CNI网络插件最主要的功能是实现了POD资源能够跨宿主机进行通信   常见的网络插件

容器网络是容器选择连接到其他容器、主机和外部网络（如Internet）的机制。CNI意为容器网络接口，它是一种标准的设计，为了让用户在容器创建或销毁时都能够更容易地配置容器网络。目前最流行的CNI插件是Flannel。Flannel插件既可以确保满足Kubernetes的网络要求，又能为Kubernetes集群管理员提供他们所需的某些特定的网络功能。 容器的Runtime提供了各种网络模式，每种模式都会产生不同的体验。例如，Docker默认情况下可以为容器配置以下网络：

**Flannel插件** Flannel是CoreOs团队针对kubernetes设计的一个网络规划服务，简单来说，它的功能是让集群中的不同节点主机创建的Docker容器都具有全集群唯一的虚拟IP地址。而且它还能在这些IP之间建立一个覆盖网络（Overlay Network），通过这个覆盖网络，将数据包原封不动地传递到目标容器内。Flannel监控ETCD中每个Pod的实际地址，并在内存中建立维护Pod节点路由表。



**Flannel   Calico**  **Canal**   Contiv   OpenContrail NSX-T kube-router  

1 下载 解压

https://github.com/

2  进行配置   

启动脚本  目录和用户  委托给   

Flannel的工作原理



如何升级k8s集群  

先把node 给干掉   调度器 会把这些节点给自动调度走  

如何回退集群

## 2 安全

### RBAC

认证和授权

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh6f781547e79fc3695696db937a5a87c.jpg)

![](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20221204170959128.png)

## 3 Helm

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh80c2969be15f921841286469b590200.jpg)

https://helm.sh/zh/docs/intro/quickstart/

k8s的包管理器

运行这条命令就可以进行安装了

```
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
```

https://artifacthub.io/

### AIOPS

## 4 监控

###  Prometheus

https://www.cnblogs.com/itxiaoshen/p/16578325.html  

https://www.bilibili.com/video/BV1QV411H7Gg?p=96&vd_source=8e232ecca082f1beea092de8718f15c6                                                                                        

https://cloud.tencent.com/developer/news/486219

https://zhuanlan.zhihu.com/p/256252866

https://prometheus.io/download/

prometheus 在2016年加入CNCF 

一种查询语言 一种灵活的查询语言，可以利用多维数据完成复杂查询 

基于HTTP的pull（拉取）方式采集时间序列数据（eexporter）  服务端 代理端 

​    promQL -

支持pushGateway组件收集数据 

通过服务发现或静态配置发现目标

多种图形模式及仪表盘支持

支持做为数据源接入Grafana 

时间序列数据库 TSDB 

**架构**

![](https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fstatic001.geekbang.org%2Finfoq%2F96%2F96f81cf3aee03b0884d1c8af07e6d21c.png&refer=http%3A%2F%2Fstatic001.geekbang.org&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1673615688&t=78a6a6df8b626d1297e1f029f09e4877)

Service discovery

服务自动发现  基于kubernetes 元数据

基于文件 file_sd

Prometheus 和Zabbix对比

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221214212231.png)

执行环境    Prometheus指标采集     Grafana 展示        睿象云告警

​    Prometheus     的核心是一个单独的二进制文件

​    Prometheus   基于pulll模型架构方式    

​     对于复制的情况  Prometheus 还提供了服务发现的能力动态监控目标

​    Prometheus  监控服务内部的情况  

​     Prometheus 有强大的数据模型  

​       Prometheus 强大的查询语言PromQL  

​        Prometheus  高效

​        Prometheus   可扩展      多个Prometheus  组成一个集群  

​         Prometheus  易于集成      多种语言客户端   

​          Prometheus   可视化   

​          Prometheus  开放性   

####   Prometheus 架构 

分为了 采集层   存储计算层   应用层  

#### Prometheus  安装

https://prometheus.io/download/

需要安装的组件  

prometheus  

主服务

pushgateway 

采集   通过网关

node_exporter

grafana-enterprise

altertmanager 

然后  安装 修改配置文件  

#### PromSQL

#### Prometheus 集成flink

#### Prometheus  集成 Grafana

grafana go语言   是最流行的时序栈上工具

下载地址

https://grafana.com/grafana/download?pg=get&plcmt=selfmanaged-box1-cta1

# **认证**

## CKA

https://training.linuxfoundation.cn/certificate/details/1

## CKS

https://training.linuxfoundation.cn/certificate/details/20

# 其他





































**kubectl自动补全**

1 下载kubectl

```
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
```

如果想指定版本进行安装的话，用指定的版本替换（）里面的内容

```
curl -LO https://dl.k8s.io/release/v1.25.0/bin/linux/amd64/kubectl
```

2 校验

下载校验文件  不进行校验也可以

```
curl -LO "https://dl.k8s.io/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl.sha256"
```

开始校验

```
echo "$(cat kubectl.sha256)  kubectl" | sha256sum --check
校验成功返回结果为0  
不成功其他字符串
```

3 安装

把kubectl安装到user/local/bin里面

```
install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
```

![](https://zhuxiaoyi.oss-cn-shanghai.aliyuncs.com/zh20221119165529.png)

4 查看是否安装成功

```
kubectl version --client
显示详细的信息
kubectl version --client --output=yaml  
```

5 kubectl发现并访问 Kubernetes 集群

6 kubectl 补全代码

kubectl 的 Bash 补全脚本可以用命令 `kubectl completion bash` 生成。 在 Shell 中导入（Sourcing）补全脚本，将启用 kubectl 自动补全功能

type _init_completion  验证是否安装 [**bash-completion**](https://github.com/scop/bash-completion)

yum install bash-completion 安装[**bash-completion**](https://github.com/scop/bash-completion)

刷新一下

source /usr/share/bash-completion/bash_completion

`type _init_completion` 

来验证 bash-completion 的安装状态

启动kubectl自动补全功能

```
kubectl completion bash | sudo tee /etc/bash_completion.d/kubectl > /dev/null
```

重新加载shell

exec bash

