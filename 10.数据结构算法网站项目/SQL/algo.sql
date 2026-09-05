/*
SQLyog Community v13.3.1 (64 bit)
MySQL - 8.4.10 : Database - algoviz
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`algoviz` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `algoviz`;

/*Table structure for table `ai_prompt` */

DROP TABLE IF EXISTS `ai_prompt`;

CREATE TABLE `ai_prompt` (
  `id` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `category` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `usage_count` int DEFAULT '0',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'enabled',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_ai_prompt_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI提示词表';

/*Table structure for table `algorithm` */

DROP TABLE IF EXISTS `algorithm`;

CREATE TABLE `algorithm` (
  `id` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `category` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `time_complexity` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `space_complexity` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pseudocode` mediumtext COLLATE utf8mb4_unicode_ci,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'enabled',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_algorithm_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='算法配置表';

/*Table structure for table `announcement` */

DROP TABLE IF EXISTS `announcement`;

CREATE TABLE `announcement` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'notice',
  `is_top` tinyint(1) DEFAULT '0',
  `sort_order` int DEFAULT '0',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'draft',
  `publish_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_announcement_status` (`status`),
  KEY `idx_announcement_sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

/*Table structure for table `api_log` */

DROP TABLE IF EXISTS `api_log`;

CREATE TABLE `api_log` (
  `id` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `api_path` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `http_method` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status_code` int DEFAULT '200',
  `response_time` int DEFAULT '0' COMMENT '单位 ms',
  `client_ip` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `request_body` mediumtext COLLATE utf8mb4_unicode_ci,
  `response_body` mediumtext COLLATE utf8mb4_unicode_ci,
  `error_message` text COLLATE utf8mb4_unicode_ci,
  `user_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_api_log_path` (`api_path`),
  KEY `idx_api_log_create_time` (`create_time`),
  KEY `idx_userid_createtime` (`user_id`,`create_time` DESC),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='API日志表';

/*Table structure for table `changelog` */

DROP TABLE IF EXISTS `changelog`;

CREATE TABLE `changelog` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `version` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '版本号，如 v1.2.3',
  `type` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'new' COMMENT '更新类型：new=新增 optimize=优化 fix=修复 urgent=紧急更新',
  `summary` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '更新摘要（卡片顶部的一句话）',
  `release_date` date NOT NULL COMMENT '发布日期（YYYY-MM-DD）',
  `modules` text COLLATE utf8mb4_unicode_ci COMMENT '功能模块标签，JSON 字符串数组：["用户中心","商品页面"]',
  `details` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '完整更新说明，换行 \n 分隔（前端拆成 <li>）',
  `known_issues` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '已知问题 / 注意事项说明',
  `issues_title` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '已知问题' COMMENT '红色框标题：已知问题 / 注意事项',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '发布状态：0=草稿(不显示) 1=已发布',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_version` (`version`),
  KEY `idx_type_release_date` (`type`,`release_date`),
  KEY `idx_status_release_date` (`status`,`release_date`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='更新日志表';

/*Table structure for table `coin_product` */

DROP TABLE IF EXISTS `coin_product`;

CREATE TABLE `coin_product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍟嗗搧缂栧彿',
  `product_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍟嗗搧鍚嶇О',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '鍟嗗搧鎻忚堪',
  `coin_price` int NOT NULL COMMENT '纭竵浠锋牸',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'coin' COMMENT '鍒嗙被',
  `icon` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '馃獧' COMMENT '鍥炬爣',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE' COMMENT 'ACTIVE=涓婃灦 INACTIVE=涓嬫灦',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_coin_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='纭竵鍟嗗搧琛�';

/*Table structure for table `coin_purchase` */

DROP TABLE IF EXISTS `coin_purchase`;

CREATE TABLE `coin_purchase` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '鐢ㄦ埛ID',
  `username` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '鍐椾綑鐢ㄦ埛鍚�',
  `product_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍟嗗搧缂栧彿',
  `product_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍟嗗搧鍚嶇О',
  `coin_price` int NOT NULL COMMENT '娑堣�楃‖甯佹暟',
  `coin_before` int DEFAULT NULL COMMENT '璐拱鍓嶄綑棰�',
  `coin_after` int DEFAULT NULL COMMENT '璐拱鍚庝綑棰�',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'SUCCESS' COMMENT 'SUCCESS=鎴愬姛',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_coin_purchase_user_id` (`user_id`),
  KEY `idx_coin_purchase_product_id` (`product_id`),
  KEY `idx_coin_purchase_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=14547 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='纭竵璐拱璁板綍琛�';

/*Table structure for table `content_audit_record` */

DROP TABLE IF EXISTS `content_audit_record`;

CREATE TABLE `content_audit_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `submit_id` varchar(64) NOT NULL COMMENT '??????',
  `user_id` bigint DEFAULT NULL COMMENT '????ID',
  `problem_id` bigint DEFAULT NULL COMMENT '????ID',
  `problem_no` varchar(64) DEFAULT NULL COMMENT '??????',
  `content_type` varchar(32) DEFAULT NULL COMMENT '????: QUESTION/CODE/COMMENT',
  `language` varchar(32) DEFAULT NULL,
  `risk_level` varchar(16) DEFAULT NULL COMMENT 'HIGH/MEDIUM/LOW/NONE',
  `hit_details` json DEFAULT NULL COMMENT '????',
  `total_score` int DEFAULT '0',
  `content_snapshot` text COMMENT '????????',
  `pre_check_status` varchar(16) DEFAULT NULL COMMENT 'BLOCK/PASS',
  `audit_status` varchar(16) DEFAULT NULL COMMENT 'pending/pass/reject/blocked/logonly',
  `audit_remark` varchar(512) DEFAULT NULL,
  `auditor_id` varchar(64) DEFAULT NULL,
  `audit_time` datetime DEFAULT NULL,
  `es_doc_id` varchar(64) DEFAULT NULL COMMENT '?? ES ??ID',
  `submit_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_submit_id` (`submit_id`),
  KEY `idx_audit_status` (`audit_status`),
  KEY `idx_submit_time` (`submit_time`)
) ENGINE=InnoDB AUTO_INCREMENT=207 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='??????';

/*Table structure for table `dangerous_code_rule` */

DROP TABLE IF EXISTS `dangerous_code_rule`;

CREATE TABLE `dangerous_code_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `rule_code` varchar(64) NOT NULL COMMENT '????',
  `rule_name` varchar(128) DEFAULT NULL COMMENT '????',
  `language` varchar(32) NOT NULL DEFAULT 'ALL' COMMENT '????: ALL/JAVA/PYTHON/JS/CPP',
  `rule_type` varchar(32) NOT NULL DEFAULT 'REGEX' COMMENT '????: REGEX/KEYWORD',
  `rule_content` text NOT NULL COMMENT '????????????',
  `risk_level` varchar(16) NOT NULL DEFAULT 'HIGH' COMMENT '????: HIGH/MEDIUM/LOW',
  `score` int NOT NULL DEFAULT '80' COMMENT '????',
  `enabled` tinyint NOT NULL DEFAULT '1',
  `description` varchar(256) DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rule_code` (`rule_code`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='????????';

/*Table structure for table `data_structure` */

DROP TABLE IF EXISTS `data_structure`;

CREATE TABLE `data_structure` (
  `id` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'enabled',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据结构配置表';

/*Table structure for table `feedback` */

DROP TABLE IF EXISTS `feedback`;

CREATE TABLE `feedback` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `username` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_nickname` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'other',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `images` mediumblob,
  `image_count` int DEFAULT '0',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'pending',
  `reply` text COLLATE utf8mb4_unicode_ci,
  `reply_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_feedback_user_id` (`user_id`),
  KEY `idx_feedback_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反馈表';

/*Table structure for table `file_storage` */

DROP TABLE IF EXISTS `file_storage`;

CREATE TABLE `file_storage` (
  `id` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `original_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `file_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `file_size` bigint DEFAULT '0',
  `file_path` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `storage_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'local',
  `bucket_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `download_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `uploader_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `uploader_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_file_uploader` (`uploader_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件存储表';

/*Table structure for table `interview_favorite` */

DROP TABLE IF EXISTS `interview_favorite`;

CREATE TABLE `interview_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联 user.id）',
  `problem_id` bigint NOT NULL COMMENT '题目ID（关联 interview_problem.id）',
  `problem_no` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '冗余：题目编号（方便列表查询免关联）',
  `collect_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_problem` (`user_id`,`problem_id`),
  KEY `idx_favorite_user_collect` (`user_id`,`collect_time`),
  KEY `idx_favorite_problem_id` (`problem_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试题目收藏表';

/*Table structure for table `interview_history` */

DROP TABLE IF EXISTS `interview_history`;

CREATE TABLE `interview_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联 user.id）',
  `problem_id` bigint NOT NULL COMMENT '题目ID（关联 interview_problem.id）',
  `view_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_history_user_view` (`user_id`,`view_time`),
  KEY `idx_history_problem_id` (`problem_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试题目浏览历史表';

/*Table structure for table `interview_like` */

DROP TABLE IF EXISTS `interview_like`;

CREATE TABLE `interview_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联 user.id）',
  `problem_id` bigint NOT NULL COMMENT '题目ID（关联 interview_problem.id）',
  `type` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型：like=点赞 / dislike=点踩',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_problem` (`user_id`,`problem_id`),
  KEY `idx_like_problem_id` (`problem_id`),
  KEY `idx_like_user_type` (`user_id`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试题目点赞点踩表';

/*Table structure for table `interview_problem` */

DROP TABLE IF EXISTS `interview_problem`;

CREATE TABLE `interview_problem` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `problem_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题目编号，唯一（如 MS001）',
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题目标题',
  `difficulty` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'medium' COMMENT '难度：easy / medium / hard',
  `tags` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签，逗号分隔（如：数组,哈希表,双指针）',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类：数组/链表/树/图/动态规划/数学/其他',
  `description` mediumtext COLLATE utf8mb4_unicode_ci COMMENT '题目描述（Markdown）',
  `input_format` text COLLATE utf8mb4_unicode_ci COMMENT '输入格式（Markdown）',
  `output_format` text COLLATE utf8mb4_unicode_ci COMMENT '输出格式（Markdown）',
  `solution` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参考答案/题解（Markdown）',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE=上线 / INACTIVE=下线',
  `is_frequent` tinyint(1) DEFAULT '0' COMMENT '是否高频：1=是 0=否',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除：1=已删除 0=正常',
  `view_count` int DEFAULT '0' COMMENT '累计浏览数（阅读量）',
  `like_count` int DEFAULT '0' COMMENT '点赞数（冗余计数）',
  `dislike_count` int DEFAULT '0' COMMENT '点踩数（冗余计数）',
  `created_by` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人（管理员ID）',
  `updated_by` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后更新人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_interview_problem_no` (`problem_no`),
  KEY `idx_interview_filter` (`status`,`category`,`difficulty`,`is_deleted`),
  KEY `idx_interview_difficulty` (`difficulty`),
  KEY `idx_interview_is_frequent` (`is_frequent`),
  KEY `idx_interview_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=12741 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试题目表';

/*Table structure for table `interview_tag` */

DROP TABLE IF EXISTS `interview_tag`;

CREATE TABLE `interview_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名（唯一）',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签分类',
  `use_count` int DEFAULT '0' COMMENT '使用次数（题目关联时自增）',
  `sort_order` int DEFAULT '0' COMMENT '排序值（小在前）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3958 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试题目标签字典';

/*Table structure for table `login_log` */

DROP TABLE IF EXISTS `login_log`;

CREATE TABLE `login_log` (
  `id` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ip` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `device` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `location` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `login_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'success',
  `fail_reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_login_log_user_id` (`user_id`),
  KEY `idx_login_log_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';

/*Table structure for table `oj_problem` */

DROP TABLE IF EXISTS `oj_problem`;

CREATE TABLE `oj_problem` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `problem_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `difficulty` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'easy',
  `tags` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `template` mediumtext COLLATE utf8mb4_unicode_ci,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE',
  `submission_count` int DEFAULT '0',
  `ac_rate` decimal(5,2) DEFAULT '0.00',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_oj_problem_no` (`problem_no`),
  KEY `idx_oj_problem_difficulty` (`difficulty`),
  KEY `idx_oj_problem_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=867 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OJ题目表';

/*Table structure for table `oj_solution` */

DROP TABLE IF EXISTS `oj_solution`;

CREATE TABLE `oj_solution` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `problem_id` bigint NOT NULL COMMENT '题目ID',
  `problem_title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '题目标题冗余',
  `user_id` bigint NOT NULL COMMENT '发布用户ID',
  `username` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户名冗余',
  `avatar` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像冗余',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题解标题',
  `format` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '解题格式（双指针/DP/回溯等）',
  `idea` mediumtext COLLATE utf8mb4_unicode_ci COMMENT '思路',
  `process` mediumtext COLLATE utf8mb4_unicode_ci COMMENT '解题过程',
  `complexity` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '复杂度分析',
  `code_lang` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'java' COMMENT '代码语言',
  `code` mediumtext COLLATE utf8mb4_unicode_ci COMMENT '题解代码',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `view_count` int DEFAULT '0' COMMENT '观看数',
  `comment_count` int DEFAULT '0' COMMENT '评论数',
  `is_passed` tinyint(1) DEFAULT '0' COMMENT '是否已AC该题',
  `is_featured` tinyint(1) DEFAULT '0' COMMENT '精选置顶',
  `audit_status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'none' COMMENT '审核状态 none/pending/blocked/passed/rejected',
  `risk_level` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT 'NONE' COMMENT '风险等级 NONE/LOW/MEDIUM/HIGH',
  `detect_summary` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '检测命中摘要',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'PUBLISHED' COMMENT 'PUBLISHED/HIDDEN/DELETED',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_solution_problem` (`problem_id`,`status`,`created_at` DESC),
  KEY `idx_solution_user` (`user_id`,`created_at` DESC),
  KEY `idx_sol_problem_sort` (`problem_id`,`status`,`audit_status`,`is_featured`,`like_count`,`created_at`,`id`),
  KEY `idx_solution_audit` (`audit_status`,`created_at` DESC,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2812 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OJ用户题解表';

/*Table structure for table `oj_solution_comment` */

DROP TABLE IF EXISTS `oj_solution_comment`;

CREATE TABLE `oj_solution_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `solution_id` bigint NOT NULL COMMENT '题解ID',
  `problem_id` bigint DEFAULT NULL COMMENT '题目ID冗余（后台按题目筛选）',
  `user_id` bigint NOT NULL COMMENT '评论用户ID',
  `username` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户名冗余',
  `avatar` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像冗余',
  `parent_id` bigint DEFAULT '0' COMMENT '父评论ID，0=顶层评论',
  `root_id` bigint DEFAULT '0' COMMENT '顶层评论ID（子评论同属一个root）',
  `reply_to_user_id` bigint DEFAULT NULL COMMENT '回复目标用户ID',
  `reply_to_username` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '回复目标用户名',
  `content` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论正文',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `audit_status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'none' COMMENT '审核状态',
  `risk_level` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT 'NONE' COMMENT '风险等级',
  `detect_summary` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '检测命中摘要',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'PUBLISHED' COMMENT 'PUBLISHED/HIDDEN/DELETED',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_comment_solution` (`solution_id`,`root_id`,`created_at`),
  KEY `idx_comment_problem` (`problem_id`),
  KEY `idx_comment_user` (`user_id`),
  KEY `idx_cmt_sol_top_sort` (`solution_id`,`parent_id`,`status`,`audit_status`,`like_count`,`created_at`,`id`),
  KEY `idx_cmt_root_reply_sort` (`root_id`,`parent_id`,`status`,`audit_status`,`created_at`,`id`),
  KEY `idx_comment_audit` (`audit_status`,`created_at` DESC,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=46635 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OJ题解评论表';

/*Table structure for table `oj_solution_like` */

DROP TABLE IF EXISTS `oj_solution_like`;

CREATE TABLE `oj_solution_like` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '点赞用户ID',
  `target_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'SOLUTION/COMMENT',
  `target_id` bigint NOT NULL COMMENT '目标ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`,`target_type`,`target_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OJ题解/评论点赞去重表';

/*Table structure for table `operation_log` */

DROP TABLE IF EXISTS `operation_log`;

CREATE TABLE `operation_log` (
  `id` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `module` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `action` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `detail` text COLLATE utf8mb4_unicode_ci,
  `ip` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_op_log_user_id` (`user_id`),
  KEY `idx_op_log_module` (`module`),
  KEY `idx_op_log_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

/*Table structure for table `orders` */

DROP TABLE IF EXISTS `orders`;

CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `order_no` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `product_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `product_id_ref` bigint DEFAULT NULL COMMENT '关联 product.id',
  `product_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `amount` int NOT NULL COMMENT '单位：分',
  `price` decimal(10,2) DEFAULT NULL,
  `quantity` int DEFAULT '1',
  `user_id` bigint DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING',
  `wechat_trade_no` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wechat_transaction_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `transaction_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `refund_status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'NONE',
  `refund_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `refund_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `pay_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_orders_order_id` (`order_id`),
  UNIQUE KEY `uk_orders_order_no` (`order_no`),
  KEY `idx_orders_user_id` (`user_id`),
  KEY `idx_orders_status` (`status`),
  KEY `idx_orders_refund_status` (`refund_status`),
  KEY `idx_orders_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

/*Table structure for table `payment_record` */

DROP TABLE IF EXISTS `payment_record`;

CREATE TABLE `payment_record` (
  `id` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `order_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `product_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `product_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `amount` int NOT NULL COMMENT '单位：分',
  `payment_method` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'wechat',
  `transaction_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'pending',
  `refund_status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'none',
  `refund_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `pay_time` datetime DEFAULT NULL,
  `refund_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_payment_order_id` (`order_id`),
  KEY `idx_payment_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

/*Table structure for table `product` */

DROP TABLE IF EXISTS `product`;

CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `product_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `price` int NOT NULL COMMENT '单位：分',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `icon` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

/*Table structure for table `sensitive_word` */

DROP TABLE IF EXISTS `sensitive_word`;

CREATE TABLE `sensitive_word` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `word` varchar(128) NOT NULL COMMENT '???',
  `category` varchar(32) NOT NULL DEFAULT 'ABUSE' COMMENT '??: ABUSE??/POLITICS??/ADVERTISING??/PORN??/OTHER',
  `level` varchar(16) NOT NULL DEFAULT 'MEDIUM' COMMENT '??: HIGH??/MEDIUM??/LOW???',
  `match_mode` varchar(16) NOT NULL DEFAULT 'EXACT' COMMENT '????: EXACT/FUZZY',
  `enabled` tinyint NOT NULL DEFAULT '1' COMMENT '????',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_word` (`word`)
) ENGINE=InnoDB AUTO_INCREMENT=41857 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='?????????';

/*Table structure for table `sensitive_word_version` */

DROP TABLE IF EXISTS `sensitive_word_version`;

CREATE TABLE `sensitive_word_version` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `version_no` int NOT NULL COMMENT '???????',
  `word_count` int NOT NULL DEFAULT '0' COMMENT '??????',
  `snapshot_json` longtext COMMENT '???? JSON [{word,category,level}]',
  `remark` varchar(256) DEFAULT NULL COMMENT '????',
  `created_by` varchar(64) DEFAULT NULL COMMENT '???',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_version` (`version_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='???????';

/*Table structure for table `statistics` */

DROP TABLE IF EXISTS `statistics`;

CREATE TABLE `statistics` (
  `id` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date` date NOT NULL,
  `dau` int DEFAULT '0',
  `wau` int DEFAULT '0',
  `mau` int DEFAULT '0',
  `ds_visits` int DEFAULT '0',
  `algo_visits` int DEFAULT '0',
  `oj_submissions` int DEFAULT '0',
  `oj_ac_rate` decimal(5,2) DEFAULT '0.00',
  `ai_dialogues` int DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_statistics_date` (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统计表';

/*Table structure for table `submission` */

DROP TABLE IF EXISTS `submission`;

CREATE TABLE `submission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `submission_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `username` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `problem_id` bigint DEFAULT NULL,
  `problem_title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `code` mediumtext COLLATE utf8mb4_unicode_ci,
  `language` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `result` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING',
  `runtime` int DEFAULT NULL COMMENT '单位 ms',
  `memory` int DEFAULT NULL COMMENT '单位 KB',
  `error_message` text COLLATE utf8mb4_unicode_ci,
  `judge_log` text COLLATE utf8mb4_unicode_ci,
  `create_time` datetime DEFAULT NULL,
  `submit_time` datetime DEFAULT NULL,
  `judge_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_submission_submission_id` (`submission_id`),
  KEY `idx_submission_problem_id` (`problem_id`),
  KEY `idx_submission_user_id` (`user_id`),
  KEY `idx_submission_status` (`status`),
  KEY `idx_submission_submit_time` (`submit_time`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提交记录表';

/*Table structure for table `sys_audit_log` */

DROP TABLE IF EXISTS `sys_audit_log`;

CREATE TABLE `sys_audit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `username` varchar(64) NOT NULL,
  `role_name` varchar(64) DEFAULT NULL,
  `module` varchar(64) NOT NULL,
  `operation` varchar(128) NOT NULL,
  `method` varchar(10) DEFAULT NULL,
  `request_url` varchar(512) DEFAULT NULL,
  `request_params` text,
  `response_code` int DEFAULT NULL,
  `ip` varchar(45) NOT NULL,
  `location` varchar(128) DEFAULT NULL,
  `cost_time` bigint DEFAULT NULL,
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_module` (`module`),
  KEY `idx_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审计日志表';

/*Table structure for table `sys_login_log` */

DROP TABLE IF EXISTS `sys_login_log`;

CREATE TABLE `sys_login_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `username` varchar(64) NOT NULL,
  `login_type` tinyint NOT NULL COMMENT '1-登录 2-登出',
  `status` tinyint NOT NULL COMMENT '0-失败 1-成功',
  `ip` varchar(45) NOT NULL,
  `location` varchar(128) DEFAULT NULL,
  `browser` varchar(64) DEFAULT NULL,
  `os` varchar(64) DEFAULT NULL,
  `message` varchar(256) DEFAULT NULL,
  `login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='登录日志表';

/*Table structure for table `sys_menu` */

DROP TABLE IF EXISTS `sys_menu`;

CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父菜单 ID，0=根节点',
  `menu_name` varchar(64) NOT NULL,
  `menu_type` tinyint NOT NULL COMMENT '1-目录 2-菜单 3-按钮',
  `path` varchar(128) DEFAULT NULL,
  `component` varchar(128) DEFAULT NULL,
  `perms` varchar(128) DEFAULT NULL COMMENT '权限标识如 content:ds:list',
  `icon` varchar(64) DEFAULT NULL,
  `sort_order` int NOT NULL DEFAULT '0',
  `visible` tinyint NOT NULL DEFAULT '1' COMMENT '0-隐藏 1-显示',
  `status` tinyint NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_parent` (`parent_id`),
  KEY `idx_type` (`menu_type`),
  KEY `idx_sort` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单权限表';

/*Table structure for table `sys_operation_log` */

DROP TABLE IF EXISTS `sys_operation_log`;

CREATE TABLE `sys_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `username` varchar(64) NOT NULL,
  `business_type` varchar(64) NOT NULL,
  `operation_type` varchar(32) NOT NULL,
  `target_id` bigint DEFAULT NULL,
  `target_name` varchar(128) DEFAULT NULL,
  `before_data` text,
  `after_data` text,
  `remark` varchar(256) DEFAULT NULL,
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_business` (`business_type`),
  KEY `idx_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='业务操作日志表';

/*Table structure for table `sys_permission` */

DROP TABLE IF EXISTS `sys_permission`;

CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `menu_id` bigint NOT NULL COMMENT '所属菜单 ID',
  `perm_code` varchar(128) NOT NULL COMMENT '权限编码如 content:ds:add',
  `perm_name` varchar(64) NOT NULL,
  `perm_type` tinyint NOT NULL COMMENT '1-新增 2-编辑 3-删除 4-导出 5-审核 6-其他',
  `api_method` varchar(10) DEFAULT NULL,
  `api_path` varchar(256) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_perm_code` (`perm_code`),
  KEY `idx_menu` (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限点表';

/*Table structure for table `sys_role` */

DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_code` varchar(64) NOT NULL COMMENT '角色编码',
  `role_name` varchar(64) NOT NULL COMMENT '角色名称',
  `role_level` tinyint NOT NULL COMMENT '角色层级: 1-超级 2-一级 3-二级',
  `parent_role_id` bigint DEFAULT NULL COMMENT '父角色 ID（NULL=顶级）',
  `data_scope` tinyint NOT NULL DEFAULT '1' COMMENT '数据范围: 1-全部 2-本部门 3-本人',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '0-禁用 1-正常',
  `is_system` tinyint NOT NULL DEFAULT '0' COMMENT '0-否 1-系统内置',
  `sort_order` int NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`),
  KEY `idx_role_level` (`role_level`),
  KEY `idx_parent_role` (`parent_role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';

/*Table structure for table `sys_role_menu` */

DROP TABLE IF EXISTS `sys_role_menu`;

CREATE TABLE `sys_role_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `menu_id` bigint NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`,`menu_id`),
  KEY `idx_role` (`role_id`),
  KEY `idx_menu` (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色菜单关联表';

/*Table structure for table `sys_role_permission` */

DROP TABLE IF EXISTS `sys_role_permission`;

CREATE TABLE `sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_perm` (`role_id`,`permission_id`),
  KEY `idx_role` (`role_id`),
  KEY `idx_perm` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关联表';

/*Table structure for table `sys_user` */

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL COMMENT '登录账号',
  `password` varchar(128) NOT NULL COMMENT '密码：超级管理员Argon2id(≤128)，普通管理员BCrypt(≤60)',
  `real_name` varchar(64) NOT NULL DEFAULT '未设置' COMMENT '真实姓名',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(256) DEFAULT NULL COMMENT '头像 URL',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 0-禁用 1-正常 2-封禁',
  `account_type` tinyint NOT NULL DEFAULT '1' COMMENT '账号类型: 1-内部管理员 2-外部出题人',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(45) DEFAULT NULL COMMENT '最后登录 IP',
  `created_by` bigint DEFAULT NULL COMMENT '创建人 ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '软删除: 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`),
  KEY `idx_account_type` (`account_type`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='后台用户表';

/*Table structure for table `sys_user_role` */

DROP TABLE IF EXISTS `sys_user_role`;

CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_role` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';

/*Table structure for table `system_config` */

DROP TABLE IF EXISTS `system_config`;

CREATE TABLE `system_config` (
  `key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `value` text COLLATE utf8mb4_unicode_ci,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'string',
  `label` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `config_group` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'basic',
  PRIMARY KEY (`key`),
  KEY `idx_system_config_group` (`config_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

/*Table structure for table `test_case` */

DROP TABLE IF EXISTS `test_case`;

CREATE TABLE `test_case` (
  `id` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `problem_id` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `input` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `output` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `score` int DEFAULT '100',
  `is_sample` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_test_case_problem_id` (`problem_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OJ测试用例表';

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `age` int DEFAULT NULL,
  `gender` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT '未知',
  `nickname` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `avatar_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `login_status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'offline',
  `status` tinyint DEFAULT '1' COMMENT '1:正常 0:封禁',
  `coins` int NOT NULL DEFAULT '1000' COMMENT '用户金额',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_login_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `ai_dialogues` int DEFAULT '0' COMMENT 'AI对话次数',
  `ds_visits` int DEFAULT '0' COMMENT '数据结构访问次数',
  `algo_visits` int DEFAULT '0' COMMENT '算法访问次数',
  `oj_visits` int DEFAULT '0' COMMENT 'OJ访问次数',
  `last_visit_time` datetime DEFAULT NULL COMMENT '最后访问时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`),
  UNIQUE KEY `uk_user_email` (`email`),
  KEY `idx_user_nickname` (`nickname`),
  KEY `idx_user_created_at` (`created_at`),
  KEY `idx_user_status_created` (`status`,`created_at`),
  KEY `idx_user_gender_created` (`gender`,`created_at`),
  KEY `idx_user_login_created` (`login_status`,`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=942009 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
