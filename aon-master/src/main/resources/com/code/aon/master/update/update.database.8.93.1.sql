# Database: aon_master
# Version: Actualizacion de la version 8.93.1 a la version 8.94.0.
# Created by: girazu
# Creation Date: 03/03/2017 13:35

BEGIN;

ALTER TABLE `project_attach` ADD `attach_type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de Archivo Adjunto' AFTER `attach_date`;
ALTER TABLE `project_attach` ADD `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion';
ALTER TABLE `project_attach` ADD `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion';
ALTER TABLE `project_attach` ADD `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `project_attach` ADD `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion';

UPDATE `project_attach` SET `attach_type` = 0;
UPDATE `project_attach` SET `attach_type` = 1 WHERE `description` LIKE 'CRS %';
UPDATE `project_attach` SET `attach_type` = 2 WHERE `description` LIKE 'CONEXFLOW%';
UPDATE `project_attach` SET `security_level` = 0 WHERE `attach_type` IN (1, 2);


UPDATE `db_version` SET `version_number` = '8.94.0';

COMMIT;
