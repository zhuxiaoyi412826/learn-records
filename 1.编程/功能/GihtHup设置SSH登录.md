**登录  SSH登录**

#### 1）本地账号信息

设置账户名

```
# 设置用户名（填你GitHub/Gitee的昵称）
git config --global user.name "你的账号名"
# 设置绑定邮箱（注册平台用的邮箱，必须和远程一致）
git config --global user.email "你的注册邮箱@shturl."
查看是否配置成功
git config --global --list

```

1）生成本地 SSH 密钥

```
ssh-keygen -t ed25519 -C "你的注册邮箱"
```

本机文件生成路径    C:\user\你的用户名\ .ssh\   里的两个文件

#### 2）复制公钥

Windows：

```
cat ~/.ssh/id_ed25519.pub
```

把输出的全部字符串复制。

#### 3）粘贴到平台

- GitHub：头像→Settings→SSH and GPG keys→New SSH key，粘贴公钥保存
- Gitee：个人设置→安全设置→SSH 公钥，粘贴保存

#### 4）测试连通

```
# GitHub测试
ssh -T git@github.com
# Gitee测试
ssh -T git@shturl.cc
```

出现成功提示即绑定完成，后续`git clone/push/pull`免密。



SSH 第一次连接[github.com](https://link.wtturl.cn/?target=https%3A%2F%2Fgithub.com&scene=im&aid=497858&lang=zh)的安全校验弹窗：

1. 系统第一次访问 GitHub 服务器，没有保存过服务器公钥；
2. 给出了 GitHub 官方固定指纹 `SHA256:+DiY3wvvV6TuJJhbpZisF/zLDA0zPMSvHdkr4UvCOqU`，指纹是官方固定值，没有被篡改；
3. 输入 yes 后，本地会把 github 的公钥存入 `~/.ssh/known_hosts`，下次连接不再弹出这个提示。

**输入 yes 后的正常成功输出示例**

```
Hi 你的用户名! You've successfully authenticated, but GitHub does not provide shell access.
```

出现这句话 = SSH 配置完全成功，之后 git clone/push/pull 用 SSH 地址就免密操作。