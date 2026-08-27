# MSVC vs GCC 区别

> MSVC：微软的 C/C++ 编译器（Microsoft Visual C++），Windows 平台； GCC：GNU Compiler Collection，跨平台编译器（Linux/macOS/Windows MinGW‑w64）。

## 1. 平台与生态

表格

| 项目       | MSVC          | GCC(MinGW‑w64/GCC)                          |
| ---------- | ------------- | ------------------------------------------- |
| 原生平台   | **Windows**   | Linux、macOS、FreeBSD，Windows 用 MinGW‑w64 |
| IDE        | Visual Studio | VS Code、CLion、Qt Creator                  |
| 标准库     | `MSVC STL`    | `libstdc++`(GCC) / `libc++`(Clang)          |
| 二进制格式 | PE（exe/dll） | PE(MinGW) / ELF(Linux)                      |

## 2. C/C++ 标准支持

- **MSVC**：新版本 VS 对 C++17/20/23 支持很好，但旧版本落后；**C 标准支持相对保守**。
- **GCC**：对 C/C++ 新标准跟进非常快，C23、C++23 特性实现完整。

> 注意：两者都不完全 100% 标准，会有扩展语法。

## 3. 编译器扩展语法（很容易踩坑）

### MSVC 特有扩展

- `__declspec(dllexport/dllimport)` 导出 DLL
- `__try/__except` Windows 结构化异常 SEH
- `__forceinline`、`__unaligned`
- 支持 Windows 原生 API 直接调用

### GCC 特有扩展

- `__attribute__((xxx))` 属性（`__attribute__((packed))`、`__attribute__((weak))`）
- 嵌套函数、语句表达式`({ ... })`
- `__builtin_xxx`内置函数
- 支持`-fPIC`位置无关代码

> ⚠️ 代码用了对方的扩展，就无法跨编译器编译。

## 4. 链接器差异（最容易出问题）

1. DLL 导出
   - MSVC：`__declspec(dllexport)`，生成`.lib`导入库
   - MinGW‑GCC：可以用`__declspec(dllexport)`，也可以用 def 文件，**不生成 msvc 的.lib**，生成.dll.a 导入库。
2. **运行时库**

- MSVC：`msvcrt.dll`、`vcruntime140.dll`，分静态 / 动态 CRT；
- GCC(MinGW)：`libgcc_s_seh‑1.dll`、`libstdc++‑6.dll`，程序依赖这两个 dll。

1. **符号修饰** MSVC 和 GCC 对 C++ 名字改编（name mangling）**完全不一样**，不能直接互相链接目标文件。

> 简单说：MSVC 编译的.obj 不能直接给 GCC 链接，反之亦然。C 语言可以 extern "C" 规避。

## 5. 优化与性能

- **MSVC**：针对 x86/x64 Windows 优化优秀，适合 Windows 桌面、游戏；对现代 CPU 指令集（AVX‑512）支持完善。
- **GCC**：跨架构优化强（x86、ARM、RISC‑V）；Linux 服务器首选；在部分场景代码体积、速度有优势。

> 现代两者差距已经缩小，不能简单说谁更快，要看代码、编译选项。

## 6. 调试信息

- MSVC：`.pdb`调试文件，VS 调试器使用。
- GCC：`‑g`生成 DWARF 调试信息，gdb/lldb 调试。

## 7. 头文件与系统 API

- MSVC：直接使用 Windows SDK 头文件，Win32 API。
- MinGW‑GCC：提供 Windows API 头文件，但部分 Windows 特有 API 支持不全。

## 8. 常见踩坑总结

1. 写跨平台代码：**不要用编译器专属扩展**，尽量用标准 C/C++。
2. 想要 MSVC 与 GCC 互相调用 DLL：只能用**extern "C"**导出 C 接口，不能导出 C++ 类。
3. 编译 Windows 程序：
   - 开发 Windows 软件：优先 MSVC (VS)
   - 想要 GCC 在 Windows：MinGW‑w64（MSYS2）
4. Linux 开发：只能 GCC/Clang，没有 MSVC。

## 快速选型建议

1. Windows 下写 GUI、游戏、DirectX、使用 VS 生态 → **MSVC**
2. Linux 开发、嵌入式、RISC‑V/ARM、开源项目 → **GCC**
3. Windows 下想使用 GCC 工具链 → MinGW‑w64 (MSYS2)