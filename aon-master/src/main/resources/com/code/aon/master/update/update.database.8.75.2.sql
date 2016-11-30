# Database: aon_master
# Version: Actualizacion de la version 8.75.2 a la version 8.78.0.
# Created by: aibanez
# Creation Date: 30/11/2016

BEGIN;

ALTER TABLE `task` ADD `source_id` int DEFAULT NULL COMMENT 'Identificador del source' AFTER `source`;

UPDATE `db_version` SET `version_number` = '8.78.0';

COMMIT;
