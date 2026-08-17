# Controller 分组

| 控制器名称                  | 核心职责说明                                       |
| --------------------------- | -------------------------------------------------- |
| frontend-controller         | 前端页面统一入口                                   |
| ojproblemcontroller         | OJ 题目管理：CRUD、批量导入、AI 生成题目、数据导出 |
| submission-controller       | 代码提交记录管理：在线判题、代码运行执行           |
| user-controller             | 普通用户登录、注册基础功能                         |
| user-management-controller  | 后台普通用户管理                                   |
| admin-controller            | 管理员身份认证、权限校验                           |
| admin-content-controller    | 后台内容管理模块                                   |
| admin-system-controller     | 系统全局参数配置                                   |
| admin-user-controller       | 后台管理员账号管理                                 |
| admin-payment-controller    | 订单、支付后台管理                                 |
| admin-monitor-controller    | 服务运维、运行状态监控                             |
| admin-export-controller     | 后台批量数据导出功能                               |
| admin-file-controller       | 文件上传、文件资源管理                             |
| admin-extension-controller  | 后台各类扩展附属功能                               |
| ai-chat-controller          | AI 对话聊天接口                                    |
| code-run-controller         | 代码沙盒运行、在线编译执行                         |
| payment-controller          | 对外支付基础接口                                   |
| order-management-controller | 订单业务管理                                       |
| announcement-controller     | 系统公告发布与查询                                 |
| feedback-controller         | 用户意见反馈收集与处理                             |
| login-controller            | 独立登录接口（补充登录逻辑）                       |
| wechat-controller           | 微信登录、微信生态对接集成                         |