CREATE TABLE `empresa` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `razao` varchar(255) NOT NULL DEFAULT '',
  `fantasia` varchar(255) NOT NULL DEFAULT '',
  `email` varchar(255) NOT NULL DEFAULT '',
  `document` varchar(50) DEFAULT NULL,
  `celular` varchar(20) DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

INSERT INTO awrepasse.empresa
(id, razao, fantasia, email, document, celular, status, created_at, updated_at)
VALUES(1, 'Hospital das Clínicas Fernandópolis', 'HOSPITAL DAS CLÍNICAS', 'hospitaldasclinicas@terra.com.br', '0', '1734421844', 1, '2024-11-23 20:45:10',null);