api_log表

```
CREATE INDEX idx_userid_createtime ON api_log (user_id, create_time DESC);
```

只创建 `idx_userid_createtime(user_id, create_time DESC)`

> 只建这一个索引，**只优化：带 user_id 过滤 + 按时间排序** 的查询。 全局直接 `order by create_time desc`（不填 user_id）**不会走这个索引，依然慢**。





