| 模式          | 链接的系统 DLL | 说明                                           |
| ------------- | -------------- | ---------------------------------------------- |
| **UCRT 模式** | `ucrtbase.dll` | 现代 Windows，完整 C11/C17 标准，推荐新项目    |
| MSVCRT 模式   | `msvcrt.dll`   | 老旧，WinXP 兼容，标准库残缺，**新项目不要用** |

[下载链接ucrt](https://github.com/brechtsanders/winlibs_mingw/releases/tag/15.2.0posix-13.0.0-ucrt-r6)

[下载链接msvcrt](https://github.com/brechtsanders/winlibs_mingw/releases/tag/15.2.0posix-13.0.0-msvcrt-r6?f_link_type=f_linkinlinenote&flow_extra=eyJpbmxpbmVfZGlzcGxheV9wb3NpdGlvbiI6MCwiZG9jX3Bvc2l0aW9uIjowLCJkb2NfaWQiOiJiZDkyMjA0MzRiMWY0ZGFkLTU1OTZkNmZkZjM1OGFiMmYifQ%3D%3D)

# demo

查看版本

```
g++ --version
gcc --version
gdb --version
```

查看目录

```
where g++
where gdb
where gcc
```

![](https://zhuxiaoyi-1300958454.cos.ap-guangzhou.myqcloud.com/img/20260824235333.png)

```
g++ main.cpp -std=c++17 -Wall -static -o main.exe
```

把所有程序都打击包里

1:   出现乱码  chcp 65001

2：编译时让 g++ 输出 GBK（Windows cmd 原生编码，推荐刷题用）

加上参数 `-fexec-charset=GBK`，输出直接转为 GBK，不用改 cmd 编码：