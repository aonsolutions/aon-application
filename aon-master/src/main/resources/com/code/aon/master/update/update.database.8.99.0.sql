# Database: aon_master
# Version: Actualizacion de la version 8.99.0 a la version 8.104.0.
# Created by: girazu
# Creation Date: 03/05/2017 16:00

BEGIN;

ALTER TABLE `data_attach` ADD `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion' AFTER `source_id`;

UPDATE `data_attach` SET `source` = 0 WHERE `source` IS NULL;
ALTER TABLE `data_attach` MODIFY `source` tinyint(2) NOT NULL COMMENT 'Origen';
UPDATE `data_response` SET `source` = 0 WHERE `source` IS NULL;
ALTER TABLE `data_response` MODIFY `source` tinyint(2) NOT NULL COMMENT 'Origen';

ALTER TABLE `project_reservation` ADD `credit_card_type` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo de tarjeta de credito' AFTER `credit_card_expiration_year`;

UPDATE `invoice` SET `rectification_type` = 0 WHERE `rectification_type` IS NULL;


UPDATE `db_version` SET `version_number` = '8.104.0';

COMMIT;
