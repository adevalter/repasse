CREATE TABLE `empresa` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `razao` varchar(255) NOT NULL DEFAULT '',
  `fantasia` varchar(255) NOT NULL DEFAULT '',
  `email` varchar(255) NOT NULL DEFAULT '',
  `document` varchar(50) DEFAULT NULL,
  `celular` varchar(20) DEFAULT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'registered' COMMENT 'registered, confirmed',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;