CREATE DATABASE IF NOT EXISTS invest DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

GRANT ALL PRIVILEGES ON *.* TO xing@localhost IDENTIFIED BY 'zhangxing@123$%^' WITH GRANT OPTION;

-- 5.7版本才支持2列同为 TIMESTAMP
CREATE TABLE `zhishu` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`name` varchar(64) NOT NULL DEFAULT '' COMMENT '指数名称',
`created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:未删除, 1:已删除',
PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='指数表';

CREATE TABLE `zhishu_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `zhishuId` bigint(20) NOT NULL DEFAULT '0' COMMENT '指数ID',
  `pe` DECIMAL(10,3) NOT NULL DEFAULT '0' COMMENT '市盈率',
  `pb` DECIMAL(10,3) NOT NULL DEFAULT '0' COMMENT '市净率',
  `shoupan` DECIMAL(10,3) NOT NULL DEFAULT '0' COMMENT '收盘指数',
  `dataDate` VARCHAR(32) NOT NULL COMMENT '日期',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:未删除, 1:已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_zhishu_date`(`zhishuId`, `dataDate`)
) ENGINE=InnoDB COMMENT='指数数据表';