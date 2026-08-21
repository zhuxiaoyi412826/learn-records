# jdk

验证jdk是否清理干净

**1. 检查 JDK 默认安装目录**

JDK 通常安装在`C:\Program Files\Java` 或 `C:\Program Files (x86)\Java`目录。打开文件资源管理器，进入这两个路径，查看是否存在 JDK 相关文件夹 。如果有，说明可能存在残留文件。

**2. 查看用户目录下的相关文件**

JDK 和基于它运行的程序可能会在用户目录留下配置文件或缓存。比如，Maven 等构建工具会在`C:\Users\你的用户名\.m2`仓库中存放 Java 包。可进入用户目录，搜索包含 “java” 关键词的文件或文件夹，查看是否有与 JDK 相关的残留。

**3. 检查环境变量**

- 右键点击 “此电脑”，选择 “属性”，再点击 “高级系统设置”，然后在弹出窗口中点击 “环境变量”。
- 在 “系统变量” 和 “用户变量” 中，查找与 Java 相关的变量，如`JAVA_HOME`（若存在，其值通常指向 JDK 安装路径） 以及`Path`变量中是否有指向 JDK 安装目录（尤其是其中的`bin`文件夹）的路径。若有相关变量，说明系统还保留着与 JDK 相关的配置信息。

**4. 查看注册表**

按下`Win + R`键，输入 “regedit” 并回车，打开注册表编辑器。

- 导航到`HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft`和`HKEY_CURRENT_USER\SOFTWARE\JavaSoft`，查看是否存在与 JDK 相关的键值。如果有，表明注册表中存在 JDK 的残留信息 。但操作注册表需谨慎，建议提前备份，避免误删导致系统问题。

**5. 使用搜索功能**

利用 Windows 的搜索功能，在整个电脑中搜索文件名包含 “java”“jdk” 字样的文件和文件夹，查看搜索结果中是否有来自已卸载 JDK 的残留内容。



官网 https://www.oracle.com/java/technologies/downloads/#jdk21-windows

x64 Compressed Archive：即压缩归档包，属于免安装版本。
x64 Installer：一般是可执行文件（.exe 格式）。下载后双击该文件，会弹出安装向导.
x64 MSI Installer：基于 Microsoft Windows Installer 技术，文件扩展名为.msi。双击运行后，同样会有安装向导引导用户完成安装过程，部分复杂软件使用该格式安装包，可更好地管理软件组件的添加和删除等操作。

下载

https://download.oracle.com/java/21/latest/jdk-21_windows-x64_bin.exe

![](C:\Users\DELL\Desktop\win安装软件\img\jdk.jpg)

安装  傻瓜式安装

验证 java --version

![](C:\Users\DELL\Desktop\win安装软件\img\java --version.jpg)



# MySQL

**1. 检查系统服务**

- **操作方法**：按下 “Win + R” 组合键，在弹出的运行框中输入 “services.msc” 并回车，打开系统服务窗口。在服务列表中查找是否有与 MySQL 相关的服务，如 “mysql” 或 “mysql80” 等。
- **判断依据**：如果存在相关服务，说明系统中可能有 MySQL 残留。即便之前已卸载 MySQL，但服务仍有可能残留在系统服务中。
- **清理**    `sc delete mysql`，这里的 “服务名称” 要替换为实际在系统服务中显示的 MySQL 服务名（例如 mysql80 ）

**2. 检查环境变量**

- **操作方法**：右键点击 “此电脑”，选择 “属性”，点击 “高级系统设置”，在弹出的系统属性窗口中点击 “环境变量”。在 “系统变量” 和 “用户变量” 中，查找是否有与 MySQL 相关的路径，比如 “C:\Program Files\MySQL\MySQL Server 8.0\bin” 等。
- **判断依据**：若找到相关路径，表明系统环境变量中存在 MySQL 残留信息。

**3. 检查文件系统**

- **常见安装目录**：打开文件资源管理器，查看常见的 MySQL 安装目录，如 “C:\Program Files\MySQL” 或 “C:\Program Files (x86)\MySQL” ，看是否存在 MySQL 文件夹。
- **数据目录**：MySQL 数据目录（通常为隐藏目录）“C:\ProgramData\MySQL” 也需检查 。若要查看隐藏目录，可在文件资源管理器中点击 “查看” 选项卡，勾选 “隐藏的项目”。
- **用户目录**：进入 “C:\Users\ 你的用户名 \AppData” 目录，在其各个子文件夹中搜索是否有与 MySQL 相关的文件或文件夹。
- **判断依据**：若上述目录中存在相关文件或文件夹，说明有 MySQL 残留。

**4. 检查注册表**

- 操作方法

  ：按下 “Win + R” 组合键，在运行框中输入 “regedit” 并回车，打开注册表编辑器。在注册表编辑器中，可使用 “编辑” 菜单中的 “查找” 功能，输入 “mysql” 进行搜索；或者手动依次展开以下可能的路径查看：

  - HKEY_LOCAL_MACHINE\SOFTWARE\MySQL AB\MySQL Server
  - HKEY_LOCAL_MACHINE\SYSTEM\ControlSet001\Services\Eventlog\Application\MySQL
  - HKEY_LOCAL_MACHINE\SYSTEM\ControlSet002\Services\Eventlog\Application\MySQL
  - HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Services\Eventlog\Application\MySQL

- **判断依据**：若搜索到或在上述路径下找到与 MySQL 相关的键值或文件夹，则表示注册表中有 MySQL 残留信息。

## 安装

- 访问 MySQL 官方下载页面（https://dev.mysql.com/downloads/mysql/ ）。
- 根据系统选择合适版本（如 64 位系统通常选含 x64 字样的版本）

![](C:\Users\DELL\Desktop\win安装软件\img\MySQLxiazai.jpg)

根据上述 选择版本 选择系统  选择 安装方式

### 程序安装

**1 下载**

需要注册账号   

https://dev.mysql.com/get/Downloads/MySQL-8.4/mysql-8.4.4-winx64.msi

**2 安装程序安装（.msi 文件）**

- 下载后，双击.msi 安装包运行。
- 出现 “Welcome” 窗口，点击 “Next”。
- 选择安装类型
  - **Standard（标准）**：将 MySQL 安装在默认程序文件夹，自动选择所需安装组件。
  - **Custom（自定义）**：可自由选择安装位置和组件。若要自定义安装路径，选择该类型。比如选择 “Custom” 后，在组件列表中选中 “mysql server”，点击绿色箭头添加到安装项目，再点击 “Advanced Options” 自定义安装路径。
- 点击 “Next”，确认安装信息后点击 “Execute” 开始安装。

**3. 配置 MySQL**

**安装程序安装方式的配置**

- 安装完成后进入配置界面，点击 “Next”。
- **设置端口**：默认端口为 3306，如无冲突可保持默认；若电脑已安装多个 MySQL 或其他占用 3306 端口的服务，则需修改。
- **选择认证方式**：通常有两种，一种较安全但密码规则要求高，另一种密码验证规则简单，可按需选择。
- **设置 root 密码**：root 是 MySQL 超级管理员账号，务必牢记设置的密码。
- 点击 “Next”，确认配置信息无误后点击 “Execute” 应用配置。

**4 启动**

安装配置完成后，按提示操作启动服务；也可通过 “服务” 窗口启动，按 “Win + R” 组合键，输入 “services.msc” 回车，在服务列表中找到 MySQL 服务，右键选择 “启动” 。

**5 验证安装**

- 按 “Win + R” 组合键，输入 “cmd” 回车打开命令提示符。
- 输入`mysql -u root -p`并回车，提示输入密码，输入设置的 root 密码（压缩包安装方式若初始化时使用`--initialize-insecure`参数，初始密码为空，直接回车）。
- 若能进入 MySQL 命令行界面（显示 “mysql>”），则表示安装成功。

### 压缩包安装

**1 下载**

**2 设置环境变量**

- 下载后解压压缩包到想要安装的目录，如 D:\mysql。
- **配置环境变量**：右键 “此电脑” 选择 “属性”，点击 “高级系统设置” - “环境变量”。在 “系统变量” 中新建变量，变量名设为 “MYSQL_HOME” ，变量值为 MySQL 解压后的根目录路径（如 D:\mysql）。然后在 “系统变量” 中找到 “Path” 变量，编辑并新建一项，填入 “% MYSQL_HOME%\bin”。

**3 配置MySQL**

- 以管理员身份打开命令提示符（CMD）。
- 在 MySQL 安装目录下创建配置文件 “my.ini” ，内容示例如下，注意修改路径为实际路径：

ini

```ini
[mysqld]
#设置3306端口
port=3306
#设置mysql的安装目录
basedir="D:\mysql"
#设置mysql数据库的数据存放目录
datadir="D:\mysql\data" 
#允许最大连接数
max_connections=200 
```

- 在 CMD 中进入 MySQL 安装目录的 bin 文件夹（如 cd D:\mysql\bin），执行初始化命令：`mysqld --initialize-insecure --user=mysql` ，这会在安装目录下生成 data 文件夹。
- 执行安装服务命令：`mysqld -install` 。

**4 启动MySQL**

```
在 CMD 中执行命令net start MySQL启动服务 
```

**5 验证MySQL**

- 输入`mysql -u root -p`并回车，提示输入密码，输入设置的 root 密码（压缩包安装方式若初始化时使用`--initialize-insecure`参数，初始密码为空，直接回车）。
- 若能进入 MySQL 命令行界面（显示 “mysql>”），则表示安装成功。

 ![](C:\Users\DELL\Desktop\win安装软件\img\启动MySQL.jpg)

## SqlYong

### 安装

删除注册表项

通过删除注册表中 SQLyog 相关的记录，来重置试用期。

1. 按下 `Win+R` 组合键打开运行窗口，输入 `regedit` 并回车，打开注册表编辑器；
2. 在 `HKEY_CURRENT_USER\SOFTWARE` 路径下，找到类似 `{d58cb4b1-47f3-45cb-a209-f298d0c3f756}` 这样一串字符串的项（不一定完全一样，可找包含 `InD110、InU值` 的 ），将其删除。
3. 完成删除后，重启 SQLyog，即可再次使用，但每过一段时间（如 14 天 ）可能需要重复此操作。

### 连接

![](C:\Users\DELL\Desktop\win安装软件\img\SQL连接.jpg)