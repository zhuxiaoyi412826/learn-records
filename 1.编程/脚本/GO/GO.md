国内镜像下载页：https://studygolang.com/dl 选择：`go1.23.1.windows‑amd64.zip`（**zip 免安装，不要 msi 安装包**）

```
go version
```

国内代理

```
go env -w GOPROXY=https://goproxy.cn,direct
```

## 📝 Demo1：Hello World（main.go）

> Go 文件后缀是 `.go`，**不需要 g++/gcc**，go 自带编译器。

新建 `main.go`

```
package main

import "fmt"

func main() {
    fmt.Println("Hello Go 1.23.1")
}
```

### 两种运行方式

#### 方式 1：直接运行（开发调试用）

```
go run main.go
```

输出：

```
Hello Go 1.23.1
```

#### 方式 2：编译生成 exe（和 C++ 一样，生成独立可执行文件）

```
go build -o main.exe main.go
```

运行 exe：

```
main.exe
```

> ✅ Go 编译默认**静态链接**，生成的 exe 不需要任何 dll，直接双击也可以运行。