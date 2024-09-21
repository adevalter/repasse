create table `repasse`(
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `medico_id` bigint unsigned DEFAULT NULL,
  `paciente_id` bigint unsigned DEFAULT NULL,
  `user_id` bigint unsigned DEFAULT NULL,
  `empresa_id` bigint unsigned DEFAULT NULL,
  `status` int NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY(`id`),
  key `repasse_medico_id` (`medico_id`),
  key `repasse_paciente_id` (`paciente_id`),
  key `repasse_user_id` (`user_id`),
  key `repasse_empresa_id` (`empresa_id`),
  CONSTRAINT `repasse_medico_id` FOREIGN KEY (`medico_id`) REFERENCES `pessoa` (`id`) ON DELETE CASCADE  ON UPDATE NO ACTION,
  CONSTRAINT `repasse_paciente_id` FOREIGN KEY (`paciente_id`)  REFERENCES `pessoa` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `repasse_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `repasse_empresa_id` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);