CREATE TABLE `repasse_item`(
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `repasse_id` bigint unsigned DEFAULT NULL,
  `procedimento_id` bigint unsigned DEFAULT NULL,
  `user_id` bigint unsigned DEFAULT NULL,
  `valor` decimal(8,2) not null default 0,
  `status` int not null default 1,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY(id),
  key `repasseitem_repasse_id` (`repasse_id`),
  key `repasseitem_procedimento_id` (`procedimento_id`),
  key `repasseitem_user_id` (`user_id`),
  CONSTRAINT `repasseitem_repasse_id` FOREIGN KEY (`repasse_id`) REFERENCES `repasse` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION, 
  CONSTRAINT `repasseitem_procedimento_id` FOREIGN KEY (`procedimento_id`) REFERENCES `procedimento` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `repasseitem_user_id` FOREIGN KEY (`user_id`) REFERENCES users (`id`) ON DELETE CASCADE ON UPDATE NO ACTION 
)