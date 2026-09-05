# 内容

1. HTTP 基础报文 → 请求行 / 响应行 + 各类 Header 含义
2. 会话体系：Cookie→Session→Token（Bearer）
3. 工程落地：Sa-Token 这类权限框架怎么读取 Header/Cookie 里的 token
4. 进阶：分块传输、跨域、链路追踪
5. 实战：抓包排查接口问题（你截图这种日常工作场景）



RESTful标准请求和写法

```
GET    /api/announcements      → 查询通知列表
GET    /api/announcements/1    → 查询id=1的单条通知
POST   /api/announcements      → 新增通知
PUT    /api/announcements/1    → 全量更新id=1通知
PATCH  /api/announcements/1    → 局部更新（只改状态）
DELETE /api/announcements/1    → 删除id=1通知
```



curl http://localhost:5000/api/admin/solutions/comments?keyword=&auditStatus=&page=1&pageSize=20