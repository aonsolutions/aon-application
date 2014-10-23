# Database: aon_master
# Version: Actualizacion de la version 8.4.0 a la version 8.4.1.
# Created by: girazu
# Creation Date: 23/10/2014 19:30

BEGIN;

ALTER TABLE `project` DROP `dossier`;

UPDATE `project`, `project_reservation` SET `active` = 1 
	WHERE `project_reservation`.`project` = `project`.`id` AND `project_reservation`.`status` = 0;
UPDATE `project`, `project_reservation` SET `active` = 0 
	WHERE `project_reservation`.`project` = `project`.`id` AND `project_reservation`.`status` != 0;

UPDATE `project`, `project_commercial` SET `active` = 1 
	WHERE `project_commercial`.`project` = `project`.`id` AND `project_commercial`.`status` IN (0, 1);
UPDATE `project`, `project_commercial` SET `active` = 0 
	WHERE `project_commercial`.`project` = `project`.`id` AND `project_commercial`.`status` NOT IN (0, 1);

UPDATE `project`, `project_tas` SET `active` = 1 
	WHERE `project_tas`.`project` = `project`.`id` AND `project_tas`.`status` = 0;
UPDATE `project`, `project_tas` SET `active` = 0 
	WHERE `project_tas`.`project` = `project`.`id` AND `project_tas`.`status` != 0;


UPDATE `db_version` SET `version_number` = '8.4.1';

COMMIT;
