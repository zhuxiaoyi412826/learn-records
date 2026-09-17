这是一个从零手写的「餐厅在线点餐系统」——纯原生 Java Web（Servlet 4.0），不用 Spring、不用 MyBatis、不用 Redis，全部依赖只有 Servlet API + MySQL 驱动 + MinIO SDK。

一句话
前端静态页面 + 原生 Servlet 提供 JSON API + JDBC 直连 MySQL + MinIO 存图片，实现一个包含「浏览菜品 → 加购物车 → 下单支付 → 后台管理」的完整闭环。