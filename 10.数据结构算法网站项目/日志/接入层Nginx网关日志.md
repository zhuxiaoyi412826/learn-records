- 请求来源 IP、请求时间、http 方法、url、status 状态码、响应耗时、User‑Agent、Referer、X‑Forwarded‑For
- 用处：爬虫识别、CC 攻击排查、统计接口 QPS、外部访问溯源、4xx/5xx 统计

> 注意：不要记录请求体大报文，避免日志膨胀；敏感参数做脱敏。