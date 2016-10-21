# Database: aon_master
# Version: Actualizacion de la version 8.72.1 a la version 8.72.2.
# Created by: aibanez
# Creation Date: 21/10/2016 13:00

BEGIN;

ALTER TABLE `task` ADD `parent` int(4) DEFAULT NULL COMMENT 'Task parent' AFTER `gtasklist_id`;

UPDATE `db_version` SET `version_number` = '8.72.2';

COMMIT;

