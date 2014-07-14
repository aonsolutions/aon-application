# Version: Upgrade from version 7.37.0 to 7.37.1
# Created by: ecastellano
# Creation Date: 10/07/2014 


ALTER TABLE `fs_model200` ADD `fiscal_group` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de grupo fiscal' AFTER `period_end`;
ALTER TABLE `fs_model200` ADD `dominant_document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF de la entidad dominante' AFTER `fiscal_group`;
ALTER TABLE `fs_model200` ADD `secretary_document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF del secretario' AFTER `dominant_document`;
ALTER TABLE `fs_model200` ADD `secretary_name` varchar(25) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF del secretario' AFTER `secretary_document`;
ALTER TABLE `fs_model200` ADD `irnr` date DEFAULT NULL COMMENT 'Fecha IRNR' AFTER `secretary_name`;

ALTER TABLE `fs_model200_registry` ADD `notary` varchar(20) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Notaria';
ALTER TABLE `fs_model200_registry` ADD `notary_date` date DEFAULT NULL COMMENT 'Fecha Notaria';

UPDATE `db_version` SET `version_number` = '7.37.1';



