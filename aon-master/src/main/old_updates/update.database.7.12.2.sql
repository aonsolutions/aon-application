# Database: aon_master
# Version: Actualizacion de la version 7.12.2 a la version 7.14.0.
# Created by: girazu
# Creation Date: 12/03/2013 10:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

CREATE TABLE `action_dup` (
  `id` int(4) NOT NULL auto_increment,
  `name` varchar(64) collate latin1_spanish_ci NOT NULL default '',
  `application` int(4) NOT NULL,
  `rows` int(4),
  `id_ori` int(4),
  `id_dup` int(4),
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_UNQ_ACTION_NAME_APPLICATION` (`name`,`application`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci;

INSERT INTO `action_dup` (`name`, `application`, `rows`) SELECT `name`, `application`, COUNT(*) FROM `action` GROUP BY `name`, `application` HAVING COUNT(*) > 1;
UPDATE `action_dup` SET `id_ori` = (SELECT MIN(`id`) FROM `action` WHERE `name` = `action_dup`.`name` and `application` = `action_dup`.`application`);
UPDATE `action_dup` SET `id_dup` = (SELECT MAX(`id`) FROM `action` WHERE `name` = `action_dup`.`name` and `application` = `action_dup`.`application`);

UPDATE `action_denied` SET `action_id` = (SELECT `id_dup` FROM `action_dup` WHERE `id_ori` = `action_denied`.`action_id`) 
	WHERE `action_id` IN (SELECT `id_ori` FROM `action_dup`);    

UPDATE `action_entry` SET `action_id` = (SELECT `id_dup` FROM `action_dup` WHERE `id_ori` = `action_entry`.`action_id`) 
	WHERE `action_id` IN (SELECT `id_ori` FROM `action_dup`);    

UPDATE `action_favorite` SET `action_id` = (SELECT `id_dup` FROM `action_dup` WHERE `id_ori` = `action_favorite`.`action_id`)
	WHERE `action_id` IN (SELECT `id_ori` FROM `action_dup`);    

UPDATE `profile_action_denied` SET `action_id` = (SELECT `id_dup` FROM `action_dup` WHERE `id_ori` = `profile_action_denied`.`action_id`)
	WHERE `action_id` IN (SELECT `id_ori` FROM `action_dup`);    

DELETE FROM `action` WHERE `id` IN (SELECT `id_ori` FROM `action_dup`);

DROP TABLE `action_dup`;

ALTER TABLE `action` DROP INDEX `IDX_ACTION_NAME_APPLICATION`;
ALTER TABLE `action` ADD UNIQUE KEY `IDX_UNQ_ACTION_NAME_APPLICATION` (`name`,`application`);


UPDATE `db_version` SET `version_number` = '7.14.0';

COMMIT;
