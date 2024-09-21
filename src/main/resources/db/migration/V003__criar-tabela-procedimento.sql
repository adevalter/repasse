CREATE TABLE `procedimento` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned DEFAULT NULL,
  `empresa_id` bigint unsigned DEFAULT NULL,
  `procedimento` varchar(255) NOT NULL DEFAULT '',
  `status` int(11) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `procedimento_user_id` (`user_id`),
  KEY `procedimento_empresa_id` (`empresa_id`),
  CONSTRAINT `procedimento_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `procedimento_empresa_id` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB DEFAULT CHARSET = utf8;