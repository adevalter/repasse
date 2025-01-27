CREATE TABLE `banco`(
 `id` bigint unsigned NOT NULL AUTO_INCREMENT,
 `user_id` bigint unsigned DEFAULT NULL,
 `empresa_id` bigint unsigned DEFAULT NULL,
 `descricao` varchar(255) default null,
 `status` int(11) NOT NULL DEFAULT '1',
 `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
 `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 primary key (id),
 KEY `banco_user_id` (`user_id`),
 KEY `banco_empresa_id` (`empresa_id`),
 CONSTRAINT `banco_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
 CONSTRAINT `banco_empresa_id` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
)