# 解决首页**把统计模式从「读时算」换成「写时累加」**

## 一、方案总览

加 **2 张表**，把「读时 SUM 全表」改成「写时累加」：

```
上报访问（写）
  ├─ user_visit_stat 个人行 +1        （现有，保留）
  ├─ stat_daily      当日行 +1        （新增，解决今日/昨日）
  └─ stat_total      总量行 +1        （新增，解决累计总量）

dashboard 读取（读）
  └─ 只查 stat_daily + stat_total 的主键点查，O(1)，不碰 94 万行 stat 大表
```

## 二、表结构 SQL

```sql
-- 1) 按日汇总表：dashboard 的「今日/昨日」从这里读
CREATE TABLE IF NOT EXISTS `stat_daily` (
    `stat_date`      DATE     NOT NULL COMMENT '统计日期',
    `ds_visits`      BIGINT   NOT NULL DEFAULT 0 COMMENT '数据结构当日访问次数',
    `algo_visits`    BIGINT   NOT NULL DEFAULT 0 COMMENT '算法当日访问次数',
    `oj_visits`      BIGINT   NOT NULL DEFAULT 0 COMMENT 'OJ当日访问次数',
    `ai_dialogues`   BIGINT   NOT NULL DEFAULT 0 COMMENT 'AI当日对话次数',
    `updated_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='全局按日访问汇总（写时累加）';

-- 2) 全局总量表：单行（id 固定 1）
CREATE TABLE IF NOT EXISTS `stat_total` (
    `id`             TINYINT  NOT NULL DEFAULT 1 COMMENT '固定 1，保证单行',
    `total_users`    BIGINT   NOT NULL DEFAULT 0 COMMENT '用户总数（未删除）',
    `ds_visits`      BIGINT   NOT NULL DEFAULT 0 COMMENT '数据结构访问总次数',
    `algo_visits`    BIGINT   NOT NULL DEFAULT 0 COMMENT '算法访问总次数',
    `oj_visits`      BIGINT   NOT NULL DEFAULT 0 COMMENT 'OJ访问总次数',
    `ai_dialogues`   BIGINT   NOT NULL DEFAULT 0 COMMENT 'AI对话总次数',
    `updated_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='全局累计汇总（单行）';

-- 初始化单行
INSERT INTO `stat_total` (`id`) VALUES (1) ON DUPLICATE KEY UPDATE `id` = `id`;
```

## 三、初始化回填 SQL（上线时一次性执行）

```sql
-- ① 回填 stat_total（累计值，可精确回填）
INSERT INTO `stat_total` (`id`, `total_users`, `ds_visits`, `algo_visits`, `oj_visits`, `ai_dialogues`)
SELECT 1, COUNT(*),
       IFNULL(SUM(`ds_visits`),0), IFNULL(SUM(`algo_visits`),0),
       IFNULL(SUM(`oj_visits`),0), IFNULL(SUM(`ai_dialogues`),0)
FROM `user_visit_stat`
WHERE `is_deleted` = 0
ON DUPLICATE KEY UPDATE
    `total_users`  = VALUES(`total_users`),
    `ds_visits`    = VALUES(`ds_visits`),
    `algo_visits`  = VALUES(`algo_visits`),
    `oj_visits`    = VALUES(`oj_visits`),
    `ai_dialogues` = VALUES(`ai_dialogues`);

-- ② 回填 stat_daily（⚠️ 历史只能近似：user_visit_stat 存的是「累计值」，
--    无法精确还原「某日新增」，这里按 last_visit_time 归集，仅作历史展示）
INSERT INTO `stat_daily` (`stat_date`, `ds_visits`, `algo_visits`, `oj_visits`, `ai_dialogues`)
SELECT DATE(`last_visit_time`),
       SUM(`ds_visits`), SUM(`algo_visits`), SUM(`oj_visits`), SUM(`ai_dialogues`)
FROM `user_visit_stat`
WHERE `is_deleted` = 0 AND `last_visit_time` IS NOT NULL
GROUP BY DATE(`last_visit_time`)
ON DUPLICATE KEY UPDATE
    `ds_visits`    = VALUES(`ds_visits`),
    `algo_visits`  = VALUES(`algo_visits`),
    `oj_visits`    = VALUES(`oj_visits`),
    `ai_dialogues` = VALUES(`ai_dialogues`);
```

## 四、写入时累加 SQL（上报接口改造核心）

以「数据结构访问」为例，原来只有第一句，现在加两句（**三句需在同一事务**）：

```sql
-- 个人行（现有，保留）
INSERT INTO user_visit_stat (user_id, ds_visits, last_visit_time) VALUES (#{userId}, 1, NOW())
ON DUPLICATE KEY UPDATE ds_visits = ds_visits + 1, last_visit_time = NOW();

-- 日汇总（新增）
INSERT INTO stat_daily (stat_date, ds_visits) VALUES (CURDATE(), 1)
ON DUPLICATE KEY UPDATE ds_visits = ds_visits + 1;

-- 总量（新增）
UPDATE stat_total SET ds_visits = ds_visits + 1 WHERE id = 1;
```

其余模块同理（`algo_visits` / `oj_visits` / `ai_dialogues` 替换字段名）。

**用户数维护**（`total_users` 在注册/删除时联动）：

```sql
-- 注册成功时
UPDATE stat_total SET total_users = total_users + 1 WHERE id = 1;
-- 逻辑删除时
UPDATE stat_total SET total_users = total_users - 1 WHERE id = 1;
```

## 五、dashboard 读取 SQL（改造 getUserStats）

```sql
SELECT
    (SELECT total_users  FROM stat_total WHERE id = 1) AS totalUsers,
    (SELECT ai_dialogues FROM stat_total WHERE id = 1) AS totalAIDialogues,
    (SELECT ds_visits    FROM stat_total WHERE id = 1) AS dsVisits,
    (SELECT algo_visits  FROM stat_total WHERE id = 1) AS algoVisits,
    (SELECT oj_visits    FROM stat_total WHERE id = 1) AS ojVisits,
    (SELECT ai_dialogues FROM stat_daily WHERE stat_date = CURDATE()) AS todayAIDialogues,
    (SELECT ai_dialogues FROM stat_daily
      WHERE stat_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY)) AS yesterdayAIDialogues
```

**效果**：全部主键点查，`getUserStats` 从 O(N) 全表扫（千万级要数秒）变成 O(1)（<1ms），数据量再大也不卡。

## 六、关键限制与注意

1. **「今日/昨日」从上线后才准确**：历史回填是按 `last_visit_time` 归集「累计值」，只是近似（因为 `user_visit_stat` 存的是累计值，没有每日增量）。上线后「写时累加」才是精确的当日新增。
2. **事务一致性**：上报的 3 条 SQL（个人 + 日汇总 + 总量）需包在同一 `@Transactional`，否则可能出现个人行和汇总行不一致。
3. **`total_users` 需联动维护**：注册 +1、逻辑删除 -1，注销（`status=-1`）不减（因为 `is_deleted` 仍为 0）。
4. **`user_visit_stat` 保留**：它仍是个人维度的明细/审计数据，只是 dashboard 不再扫它。

------

这就是完整方案。现在不用实现，等数据量涨到百万级时，按「建表 → 回填 → 改造上报接口 → 改造 getUserStats」四步落地即可。需要我到时候实现时，直接说一声。