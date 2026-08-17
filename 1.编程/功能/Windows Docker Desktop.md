# Windows Docker Desktop 保姆级完整教程（WSL2 主流方案，Win10/Win11 通用）

## 一、前置硬件 & 系统要求

1. CPU 开启虚拟化

   重启电脑进 BIOS，开启 

   ```
Intel VT-x
   ```
   
    / 

   ```
AMD-V
   ```
   
   ；

   

   验证：任务管理器 → 性能 → CPU，右侧显示

   虚拟化：已启用

   。

2. 系统版本

   - Win11：全部版本支持
   - Win10：专业 / 企业 / 教育版，版本 ≥ 2004（22H2 最佳）；家庭版仅支持 WSL2 后端

3. 磁盘预留：至少 10GB 空间（镜像、WSL 系统占用）

## 二、第一步：安装 WSL2（Docker 依赖核心）

以**管理员身份**打开 PowerShell，逐条执行：

powershell







```
# 1. 启用WSL子系统与虚拟机平台
dism.exe /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart
dism.exe /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart

# 2. 重启电脑
shutdown /r /t 0
```

重启后再次打开管理员 PowerShell：

powershell







```
# 3. 更新WSL内核
wsl --update
# 4. 设置WSL2为默认版本
wsl --set-default-version 2
# 5. 安装Ubuntu发行版（Docker运行依赖）
wsl --install -d Ubuntu
```

弹出 Ubuntu 窗口，设置用户名、密码（记住，后续 WSL 操作要用）。

## 三、第二步：下载安装 Docker Desktop

### 1. 官方下载地址

[https://www.docker.com/products/docker-desktop/](https://link.wtturl.cn/?target=https%3A%2F%2Fwww.docker.com%2Fproducts%2Fdocker-desktop%2F&scene=im&aid=497858&lang=zh)

下载 `Docker Desktop Installer.exe` 安装包

### 2. 安装步骤

1. 右键安装包 → **以管理员身份运行**

2. 安装界面勾选：

   

   ✅ Use WSL 2 instead of Hyper-V（使用 WSL2 替代 Hyper-V，必选）

   

   ✅ Install required Windows components for WSL 2

3. 点击 OK，等待解压完成，提示**注销电脑**，保存文件后注销重登。

### 3. 首次启动配置

1. 开始菜单打开 Docker Desktop，接受服务协议
2. 登录 Docker 账号（免费注册，不登录无法拉取官方镜像）
3. 任务栏右下角出现**鲸鱼图标**，转圈代表启动中；静止无动画 = 引擎就绪

### 4. 验证安装成功

打开 CMD / PowerShell，执行测试命令：

powershell







```
# 查看Docker版本
docker --version
docker compose version

# 运行测试容器（输出Hello World即成功）
docker run hello-world
```

## 四、关键优化配置（必做，解决 C 盘爆满、下载慢）

点击 Docker 桌面右上角 **⚙️ Settings 设置**

### 1. Resources → WSL Integration（WSL 集成）

勾选你的 Ubuntu 发行版 → Apply & Restart，让 WSL 内终端也能使用 docker 命令。

### 2. Resources → Advanced（资源限制，防止内存爆）

- CPU：根据 CPU 核心减半（4 核填 2，8 核填 4）
- Memory：8G 电脑分配 3G；16G 分配 6G；32G 分配 12G
- Swap：内存一半即可

### 3. Resources → Disk（迁移镜像存储，释放 C 盘）

默认镜像存在 C 盘 WSL 内，C 盘不足在这里修改镜像存储目录到 D/E 盘。

### 4. Docker Engine → 配置国内镜像加速器（解决拉取超时）

把右侧 JSON 全部替换为下面内容（阿里云镜像源，免费）：

json







```
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://hub-mirror.c.163.com"
  ],
  "builder": {
    "gc": {
      "defaultKeepStorage": "20GB"
    }
  },
  "experimental": false
}
```

点击 Apply & Restart 重启 Docker 生效。

## 五、Docker 核心常用命令（Windows 终端 / WSL 通用）

### 1. 镜像操作（拉取、查看、删除）

powershell







```
# 拉取指定版本镜像，示例nginx、mysql、nacos、elasticsearch
docker pull nginx
docker pull nacos/nacos-server:v2.4.3
docker pull docker.elastic.co/elasticsearch/elasticsearch:9.4.3

# 查看本地全部镜像
docker images

# 删除镜像（镜像名:版本）
docker rmi nginx
# 清理所有无用悬空镜像
docker image prune -a
```

### 2. 容器运行管理（最常用）

powershell







```
# 示例1：后台运行Nginx，主机8080映射容器80端口
docker run -d --name my-nginx -p 8080:80 nginx

# 参数解释
# -d 后台守护运行
# --name 自定义容器名称
# -p 宿主机端口:容器内部端口 端口映射
# -v 宿主机目录:容器目录 数据持久化挂载
# -e 设置环境变量（数据库密码、JVM参数等）

# 查看正在运行容器
docker ps
# 查看所有容器（包含已停止）
docker ps -a

# 启停容器
docker stop my-nginx
docker start my-nginx
docker restart my-nginx

# 实时查看容器日志
docker logs -f my-nginx

# 进入容器内部终端
docker exec -it my-nginx bash

# 删除容器（必须先停止）
docker rm my-nginx
# 一键清理所有停止的容器
docker container prune
```

### 3. docker-compose 批量编排（一键启动多组件，如 ES+Nacos+MySQL）

1. 项目目录新建 `docker-compose.yml`
2. 启动全部服务

powershell







```
docker compose up -d
```

1. 停止并删除容器（保留镜像）

powershell







```
docker compose down
```

## 六、实战示例 1：单机部署 Nacos（你之前用到的组件）

powershell







```
# 一键启动nacos单机
docker run -d \
--name nacos \
-p 8848:8848 \
-e MODE=standalone \
nacos/nacos-server:v2.4.3
```

访问地址：[http://localhost:8848/nacos](https://link.wtturl.cn/?target=http%3A%2F%2Flocalhost%3A8848%2Fnacos&scene=im&aid=497858&lang=zh)

## 七、实战示例 2：部署 ES9.4.3（设置 JVM 内存 512M）

powershell







```
docker run -d \
--name es9 \
-p 9200:9200 -p 9300:9300 \
-e discovery.type=single-node \
-e "ES_JAVA_OPTS=-Xms512m -Xmx512m" \
docker.elastic.co/elasticsearch/elasticsearch:9.4.3
```

访问：[http://127.0.0.1:9200](https://link.wtturl.cn/?target=http%3A%2F%2F127.0.0.1%3A9200&scene=im&aid=497858&lang=zh)

## 八、常见报错 & 排坑

1. WSL 安装失败 0x800701bc

   

   执行 

   ```
   wsl --update
   ```

    更新内核，重启电脑重试。

2. Docker 启动卡住、引擎无法启动

   

   管理员 PowerShell 执行：

   powershell

   

   

   

   ```
   bcdedit /set hypervisorlaunchtype auto
   ```

   重启电脑。

3. 拉取镜像超时、time out

   

   检查镜像加速器配置，重启 Docker Desktop。

4. 端口被占用

   

   Windows 查询端口占用 PID：

   powershell

   

   

   

   ```
   netstat -ano | findstr "8848"
   taskkill /F /PID 进程号
   ```

5. C 盘空间越来越小

   

   Settings → Resources → Disk 迁移镜像目录到其他磁盘；定期执行 

   ```
   docker system prune -a
   ```

    清理无用镜像容器。

6. WSL 内 docker 命令找不到

   

   打开 Settings → Resources → WSL Integration，勾选 Ubuntu 后重启 Docker。

## 九、卸载 Docker（如需）

1. Docker Desktop 右上角设置 → Uninstall 卸载程序
2. PowerShell 卸载 WSL 发行版

powershell







```
wsl --unregister Ubuntu
```