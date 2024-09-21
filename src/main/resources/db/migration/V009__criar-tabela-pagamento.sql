
CREATE TABLE `pagamento`(
 `id` bigint unsigned NOT NULL AUTO_INCREMENT,
 `repasse_id` bigint unsigned DEFAULT NULL,
 `user_id` bigint unsigned DEFAULT NULL,
 `total` decimal(8,2) not null default 0,
 `banco_pagamento` varchar(255) default null,
 `data_deposito` datetime NULL DEFAULT null,
 `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
 `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 primary key (id),
 key `pagamento_repasse_id` (`repasse_id`),
 key `pagamento_user_id` (`user_id`),
 CONSTRAINT `pagamento_repasse_id`  FOREIGN KEY (`repasse_id`) REFERENCES `repasse` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION, 
 CONSTRAINT `pagamento_user_id`  FOREIGN KEY (`user_id`) REFERENCES users (`id`) ON DELETE CASCADE ON UPDATE NO ACTION 
)