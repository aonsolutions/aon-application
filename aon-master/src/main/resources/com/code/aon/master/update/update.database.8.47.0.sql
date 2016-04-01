# Database: aon_master
# Version: Actualizacion de la version 8.47.0 a la version 8.47.1
# Created by: girazu
# Creation Date: 01/04/2016 14:05

BEGIN;

ALTER TABLE `hotel` ADD `police_code` varchar(10) COLLATE latin1_spanish_ci default NULL COMMENT 'Codigo del Hotel para la policia' AFTER `sheet_changing`;
ALTER TABLE `hotel` ADD `police_counter` int(4) default '0' COMMENT 'Contador para envio de ficheros a la policia' AFTER `police_code`;

ALTER TABLE `project_reservation_guest` ADD `document_exp_date` date default NULL COMMENT 'Fecha de expiracion del documento' AFTER `document_country`;

UPDATE `registry` SET `security_level` = 0 WHERE `security_level` IS NULL;


UPDATE `db_version` SET `version_number` = '8.47.1';

COMMIT;
