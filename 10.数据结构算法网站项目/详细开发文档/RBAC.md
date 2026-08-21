# Sa‑Token

```
超级管理员（最高，仅限运维应急）
├─ 一级管理员
│   ├─【权限分配子管理员】（仅分配二级管理员角色，无业务权限）
│   ├─ 内容发布管理员
│   │   └─ 二级管理员
│   │       ├─ 数据结构发布管理员
│   │       ├─ 算法发布管理员
│   │       └─ 通用子管理员
│   ├─ 内容审核管理员
│   │   ├─ 算法内容审核
│   │   ├─ 面试题目管理员
│   │   ├─ 题目校验测试管理员【可选】
│   │   └─ 关键词屏蔽管理员
│   ├─ EFK日志分析管理员
│   │   ├─ 用户行为日志（Umami分析）
│   │   ├─ 业务日志
│   │   └─ 系统日志
│   ├─【数据报表只读管理员】【可选】（只看统计报表，不能操作日志引擎）
│   ├─ 订单分析管理员
│   │   ├─ 商品管理员
│   │   └─ 金币管理员
│   ├─【运营活动管理员】【可选】（活动、签到、优惠券配置）
│   ├─【财务管理员】（对账、退款审核、发票，不能修改金币）
│   └─【客服管理员】（用户申诉、禁言解封、题解评论处理）
└─【安全审计管理员】（只读，审计所有管理员操作日志，不可修改数据）

业务外部角色【可选】
    └─外部出题人：仅编辑自己提交题目，提交后需要审核

普通用户
├─普通用户
├─VIP用户
├─SVIP用户
├─限时体验VIP
└─封禁用户
用户权限维度：评论权限、题解发布权限、题目可见权限、购买解锁权限

```



> 1. `sys_admin`：后台管理员（Sa‑Token RBAC 权限体系）
>2. `oj_user`：刷题 C 端用户（**不参与 RBAC，独立登录体系**） 仓库：https://github.com/dromara/sa-token 文档官网：https://sa-token.cc
> 3. 两套账号隔离：

## 一、Maven 依赖引入

```
<!-- sa-token springboot3 启动器 -->
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-spring-boot-starter</artifactId>
    <version>1.45.0</version>
</dependency>
```

application.yml 基础配置

```
sa-token:
  # token名称 (同时也是cookie名称)
  token-name: Admin-Token
  # token有效期，单位s  7天
  timeout: 604800
  # 多账号体系key，区分后台管理员与刷题用户
  multi-auth:
    admin:
      token-name: Admin-Token
      timeout: 604800
    oj:
      token-name: Oj-Token
      timeout: 2592000
  # 是否打印日志
  is-log: true
```

> 重点：使用**多账号体系**，`admin`代表后台管理员登录，`oj`代表刷题学生登录，两套 token 互不干扰。

## 二、数据库表设计

### RBAC 五张核心表 + 操作审计日志表

```
-- 1.后台管理员表 sys_admin
CREATE TABLE `sys_admin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员id',
  `username` varchar(64) NOT NULL COMMENT '登录账号',
  `password` varchar(128) NOT NULL COMMENT 'BCrypt加密密码',
  `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
  `email` varchar(128) DEFAULT NULL,
  `phone` varchar(32) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0禁用 1正常',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台管理员账号';

-- 2.角色表 sys_role
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_code` varchar(64) NOT NULL COMMENT '角色编码 super_admin / user_admin / problem_admin / problem_reviewer / keyword_reviewer',
  `role_name` varchar(64) NOT NULL COMMENT '角色名称',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 3.管理员角色关联 sys_admin_role
CREATE TABLE `sys_admin_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_role` (`admin_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员-角色关联';

-- 4.权限表 sys_permission
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `perm_code` varchar(128) NOT NULL COMMENT '权限标识 problem:list problem:edit',
  `perm_name` varchar(128) NOT NULL COMMENT '权限名称',
  `type` tinyint NOT NULL DEFAULT '1' COMMENT '1接口权限 2菜单权限',
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_perm_code` (`perm_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 5.角色权限关联 sys_role_permission
CREATE TABLE `sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `perm_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_perm` (`role_id`,`perm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限关联';

-- 6.管理员操作审计日志 sys_oper_log 不可物理删除
CREATE TABLE `sys_oper_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_id` bigint DEFAULT NULL COMMENT '操作管理员ID',
  `username` varchar(64) DEFAULT NULL COMMENT '管理员账号',
  `oper_module` varchar(64) DEFAULT NULL COMMENT '操作模块',
  `oper_desc` varchar(255) DEFAULT NULL COMMENT '操作描述',
  `request_url` varchar(255) DEFAULT NULL,
  `request_method` varchar(16) DEFAULT NULL,
  `ip` varchar(64) DEFAULT NULL,
  `params` text COMMENT '请求参数',
  `result` text COMMENT '返回结果',
  `oper_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员操作审计日志';
```

### 初始化角色数据

```
INSERT INTO sys_role(role_code,role_name,remark) VALUES
('super_admin','超级管理员','拥有全部权限'),
('user_admin','后台用户管理员','管理后台管理员账号'),
('problem_admin','题库管理员','增删改题目，管理测试用例，重判'),
('problem_reviewer','题目审核员','仅审核题目，不可编辑题目内容'),
('keyword_reviewer','关键词审核员','仅审核敏感关键词库');
```

### OJ 权限编码规划（sys_permission 预置数据）

表格

| perm_code           | perm_name    | 适用角色                                     |
| ------------------- | ------------ | -------------------------------------------- |
| sys:admin:list      | 管理员列表   | super_admin、user_admin                      |
| sys:admin:add       | 新增管理员   | super_admin、user_admin                      |
| sys:admin:edit      | 编辑管理员   | super_admin、user_admin                      |
| sys:admin:delete    | 删除管理员   | super_admin                                  |
| sys:role:list       | 角色查询     | super_admin                                  |
| sys:role:assignPerm | 分配权限     | super_admin                                  |
| problem:list        | 题目列表     | super_admin、problem_admin、problem_reviewer |
| problem:create      | 创建题目     | super_admin、problem_admin                   |
| problem:edit        | 编辑题目     | super_admin、problem_admin                   |
| problem:delete      | 删除题目     | super_admin、problem_admin                   |
| problem:rejudge     | 批量重判题目 | super_admin、problem_admin                   |
| problem:audit       | 审核题目     | super_admin、problem_reviewer                |
| keyword:list        | 关键词列表   | super_admin、keyword_reviewer                |
| keyword:add         | 新增关键词   | super_admin、keyword_reviewer                |
| keyword:edit        | 编辑关键词   | super_admin、keyword_reviewer                |
| keyword:delete      | 删除关键词   | super_admin、keyword_reviewer                |
| contest:manage      | 比赛管理     | super_admin、problem_admin                   |

## 三、核心业务层开发

### 3.1 管理员登录（多账号体系 admin）

> 注意：`StpUtil.admin` 代表后台管理员会话；刷题用户使用 `StpUtil.oj`，两套完全隔离。

```
/**
 * 后台管理员登录接口
 */
@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    @Autowired
    private SysAdminService sysAdminService;

    @PostMapping("/login")
    public Result login(@RequestBody AdminLoginDTO dto){
        //1 查询管理员
        SysAdmin admin = sysAdminService.getByUsername(dto.getUsername());
        if(admin == null || !BCrypt.checkpw(dto.getPassword(),admin.getPassword())){
            return Result.fail("账号密码错误");
        }
        if(admin.getStatus() == 0){
            return Result.fail("账号已被禁用");
        }
        //2 登录，写入admin会话
        StpUtil.admin.login(admin.getId());
        // 更新最后登录时间
        sysAdminService.updateLastLogin(admin.getId());
        // 返回token给前端
        return Result.ok(StpUtil.admin.getTokenValue());
    }

    /**
     * 管理员登出
     */
    @PostMapping("/logout")
    public Result logout(){
        StpUtil.admin.logout();
        return Result.ok();
    }
}
```

### 3.2 权限加载服务（Sa‑Token 需要自定义权限数据源）

> Sa‑Token 不会自动读数据库，需要实现 `StpInterface`，框架回调获取当前账号的权限、角色。

```
import cn.dev33.satoken.stp.StpInterface;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class SaStpInterfaceImpl implements StpInterface {

    @Autowired
    private SysPermissionService sysPermissionService;

    /**
     * 返回账号拥有的权限集合
     * @param loginId 账号id
     * @param loginType 登录类型 admin / oj
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if("admin".equals(loginType)){
            // 根据adminId查询该管理员所有perm_code权限字符串
            Long adminId = Long.parseLong(loginId.toString());
            return sysPermissionService.getPermCodeByAdminId(adminId);
        }
        // oj刷题用户不需要RBAC权限，返回空集合
        return List.of();
    }

    /**
     * 返回账号拥有角色编码集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if("admin".equals(loginType)){
            Long adminId = Long.parseLong(loginId.toString());
            return sysPermissionService.getRoleCodeByAdminId(adminId);
        }
        return List.of();
    }
}
```

### 3.3 Controller 接口鉴权注解使用

> `@SaCheckPermission`、`@SaCheckRole` 需要指定 `type = "admin"`，代表校验后台管理员会话。

```
@RestController
@RequestMapping("/api/admin/problem")
public class AdminProblemController {

    // 查询题目列表，需要 problem:list 权限
    @SaCheckPermission(value = "problem:list", type = "admin")
    @GetMapping("/list")
    public Result list(){
        return Result.ok();
    }

    // 创建题目
    @SaCheckPermission(value = "problem:create", type = "admin")
    @PostMapping("/create")
    public Result create(){
        return Result.ok();
    }

    // 批量重判，高危接口，严格权限
    @SaCheckPermission(value = "problem:rejudge", type = "admin")
    @PostMapping("/rejudge/batch")
    public Result batchRejudge(){
        return Result.ok();
    }

    // 题目审核接口
    @SaCheckPermission(value = "problem:audit", type = "admin")
    @PostMapping("/audit")
    public Result auditProblem(){
        return Result.ok();
    }
}
```

> 角色校验示例

```
// 仅超级管理员可以访问
@SaCheckRole(value = "super_admin", type = "admin")
@GetMapping("/sys/list")
public Result sysAdminList(){
    return Result.ok();
}
```

## 四、全局拦截与审计日志

### 4.1 全局异常处理 Sa‑Token 鉴权异常

```
/**
 * Sa‑Token鉴权异常全局捕获
 */
@RestControllerAdvice
public class SaTokenExceptionHandler {

    @ExceptionHandler(SaTokenException.class)
    public Result handlerSaTokenException(SaTokenException e){
        int code = e.getCode();
        if(code == SaTokenErrorCode.NOT_LOGIN){
            return Result.fail(401,"未登录，请重新登录");
        }else if(code == SaTokenErrorCode.PERMISSION_NOT_EXIST){
            return Result.fail(403,"没有操作权限");
        }else if(code == SaTokenErrorCode.ROLE_NOT_EXIST){
            return Result.fail(403,"角色不允许访问");
        }
        return Result.fail(e.getMessage());
    }
}
```

### 4.2 AOP 实现管理员操作审计日志

使用 AOP 拦截所有`/api/admin/**`接口，记录到`sys_oper_log`，**审计日志只针对后台管理员接口，刷题接口不记录**。

> 切面逻辑要点：
>
> 1. 判断`StpUtil.admin.isLogin()`获取当前登录 adminId；
> 2. 获取 url、请求方式、ip、请求参数；
> 3. 存入数据库，**禁止提供删除审计日志的接口**。

## 五、前端对接要点

1. 后台管理前端请求头携带 `Admin‑Token: xxx`；刷题端携带 `Oj‑Token: xxx`。
2. 前端拿到当前管理员的权限集合，做页面按钮显隐；**权限校验不能只依赖前端，后端必须注解鉴权**。
3. 角色权限分配页面：
   - 页面加载获取全部权限列表；
   - 选中角色，勾选对应权限，保存到 `sys_role_permission`。

## 六、关键业务规则约束（OJ 特有）

1. **超级管理员**：拥有全部权限，可以分配角色、管理所有管理员账号、重判、删除题目。
2. **后台用户管理员**：只能管理其他管理员账号，**不能分配超级管理员角色**。
3. **题库管理员**：题目增删改、上传测试用例、批量重判；不能管理后台账号。
4. **题目审核员**：仅审核题目，**不允许修改题目内容、删除题目、重判**。
5. **关键词审核员**：仅操作敏感关键词，看不到题目、比赛模块。
6. 高危接口：批量重判、删除题目、管理员账号删除，必须审计日志留存。

## 七、改造步骤（接入流程）

1. 执行 RBAC 全套建表 SQL，初始化角色数据；
2. 引入 sa‑token maven 依赖，配置 yml 多账号；
3. 编写 `SysAdmin、SysRole、SysPermission` 实体、mapper、service；
4. 实现`StpInterface`自定义权限数据源；
5. 开发管理员登录接口；
6. 给所有后台`/api/admin/**`接口增加`@SaCheckPermission(type="admin")`鉴权注解；
7. AOP 实现操作审计日志；
8. 开发前端页面：管理员管理、角色管理、权限分配页面；
9. 测试：不同角色账号登录，验证接口权限拦截是否生效。

## 八、踩坑清单

1. ❗忘记写 `type="admin"`，注解默认使用第一个登录体系，会出现权限校验错乱；
2. ❗不要把刷题用户 oj_user 塞入 RBAC 表，两套账号物理隔离；
3. ❗只做前端按钮隐藏，后端不加注解鉴权，会产生越权漏洞；
4. ❗批量重判接口权限严格管控，重判任务会消耗大量判题机资源；
5. ❗审计日志只允许查询，不提供删除接口，满足等保要求。

> 测试账号初始化：手动插入一条 sys_admin 记录，密码使用 BCrypt 加密。