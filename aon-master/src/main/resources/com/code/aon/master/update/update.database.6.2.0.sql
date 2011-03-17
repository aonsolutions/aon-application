# Database: aon_master
# Version: Actualizacion de la version 6.2.0 a la version 6.2.1.
# Created by: girazu
# Creation Date: 17/03/2011 15:41
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

UPDATE `registry` SET `document_type` = 2 WHERE SUBSTR(`document`, 1, 1) IN ('X','Y','Z') AND `document_type` != 2;

UPDATE `registry` SET `document_type` = 1 WHERE (SUBSTR(`document`, 1, 1) BETWEEN 'A' AND 'J' OR SUBSTR(`document`, 1, 1) BETWEEN 'N' AND 'W') AND `document_type` != 1;

UPDATE `registry` SET `document_type` = 0 WHERE (SUBSTR(`document`, 1, 1) BETWEEN '0' AND '9' OR SUBSTR(`document`, 1, 1) BETWEEN 'K' AND 'M') AND `document_type` != 0;

UPDATE `registry` SET `type` = 0 WHERE `document_type` != 1; 

UPDATE `registry` SET `type` = 1 WHERE `document_type` = 1; 

ALTER TABLE `bank_statement` ADD `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad' AFTER `reliability`;

ALTER TABLE `contract` ADD `agreement_level_category` int(4) default NULL COMMENT 'Identificador unico de la Categoria Profesional';

ALTER TABLE `contract` ADD KEY `IDX_CONTRACT_AGREEMENT_LEVEL_CATEGORY` (`agreement_level_category`);

ALTER TABLE `contract` ADD CONSTRAINT `FK_CONTRACT_AGREEMENT_LEVEL_CATEGORY` FOREIGN KEY (`agreement_level_category`) REFERENCES `agreement_level_category` (`id`);


UPDATE `db_version` SET `version_number` = '6.2.1';

COMMIT;
