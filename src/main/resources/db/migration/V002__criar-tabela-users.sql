CREATE TABLE `users` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `empresa_id` bigint unsigned DEFAULT NULL,
  `first_name` varchar(255) NOT NULL DEFAULT '',
  `last_name` varchar(255) NOT NULL DEFAULT '',
  `email` varchar(255) NOT NULL DEFAULT '',
  `password` varchar(255) NOT NULL DEFAULT '',
  `forget` varchar(255) DEFAULT NULL,
  `document` varchar(50) DEFAULT NULL,
  `celular` varchar(20) DEFAULT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'registered' COMMENT 'registered, confirmed',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_empresa_id` (`empresa_id`),
  CONSTRAINT `user_empresa_id` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;