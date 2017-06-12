# Database: aon_master
# Version: Actualizacion de la version 8.107.0 a la version 8.108.0.
# Created by: ecastellano
# Creation Date: 12/06/2017 12:10

BEGIN;

ALTER TABLE `fs_vat` ADD `replaced_number` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion complementada o sustituida';

ALTER TABLE `fs_model200` ADD `status` tinyint(2) NOT NULL DEFAULT 0 COMMENT 'Estado de la Declaracion' AFTER `administration`;
ALTER TABLE `fs_model200` ADD `just_activos` varchar(30) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de justificante activos';


UPDATE `db_version` SET `version_number` = '8.108.0';

COMMIT;
