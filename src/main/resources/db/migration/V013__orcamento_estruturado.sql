ALTER TABLE `orcamento`
  ADD COLUMN `tipo` varchar(20) NULL DEFAULT NULL AFTER `celular`,
  ADD COLUMN `procedimento_cirurgico` varchar(255) NOT NULL DEFAULT '' AFTER `tipo`,
  ADD COLUMN `equipe_medica` varchar(255) NOT NULL DEFAULT '' AFTER `procedimento_cirurgico`,
  ADD COLUMN `obs_pagamento` varchar(510) NOT NULL DEFAULT '' AFTER `equipe_medica`,
  ADD COLUMN `incluso` text NULL AFTER `obs_pagamento`,
  ADD COLUMN `nao_incluso` text NULL AFTER `incluso`,
  ADD COLUMN `complicacoes` text NULL AFTER `nao_incluso`,
  ADD COLUMN `observacoes` text NULL AFTER `complicacoes`,
  MODIFY COLUMN `descricao` text NULL;

CREATE TABLE `orcamento_item` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `orcamento_id` bigint unsigned NOT NULL,
  `secao` varchar(20) NOT NULL,
  `descricao` varchar(255) NOT NULL DEFAULT '',
  `valor` decimal(10,2) NOT NULL DEFAULT 0,
  `ordem` int NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `orcamento_item_orcamento_id` (`orcamento_id`),
  CONSTRAINT `orcamento_item_orcamento_id` FOREIGN KEY (`orcamento_id`) REFERENCES `orcamento` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB DEFAULT CHARSET = utf8;
