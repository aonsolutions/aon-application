# Database: aon_master
# Version: Actualizacion de la version 8.78.0 a la version 8.78.1.
# Created by: aibanez
# Creation Date: 30/11/2016

BEGIN;

ALTER TABLE `task_comment` ADD `source` int DEFAULT NULL COMMENT 'Origen del comentario' AFTER `comment`;

ALTER TABLE `task_comment` ADD `source_id` int DEFAULT NULL COMMENT 'Identificador del origen' AFTER `source`;

UPDATE `db_version` SET `version_number` = '8.78.1';

COMMIT;
