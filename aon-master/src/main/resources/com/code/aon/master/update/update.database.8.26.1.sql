# Database: aon_master
# Version: Actualizacion de la version 8.26.1 a la version 8.26.2.
# Created by: ecastellano
# Creation Date: 30/06/2015 18:05

BEGIN;

ALTER TABLE `fs_model200_registry` ADD `country` varchar(2) DEFAULT NULL COMMENT 'Pais' AFTER `province`;

UPDATE `db_version` SET `version_number` = '8.26.2';

COMMIT;
