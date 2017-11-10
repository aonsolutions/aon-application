# Database: aon_master
# Version: Actualizacion de la version 9.13.0 a la version 9.13.1.
# Created by: ecastellano
# Creation Date: 10/11/2017 14:00

BEGIN;

ALTER TABLE `fs_mod349` ADD `representative_document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante Legal' AFTER `name`; 

UPDATE `db_version` SET `version_number` = '9.13.1';

COMMIT;