# Database: aon_master
# Version: Actualizacion de la version 8.67.0 a la version 8.68.0.
# Created by: aibanez
# Creation Date: 30/09/2016 

BEGIN;

ALTER TABLE `task_comment` DROP FOREIGN KEY `FK_TASK_COMMENT_USER`;

ALTER TABLE `task_comment` DROP  KEY `IDX_TASK_COMMENT_USER`;

ALTER TABLE `task_comment` DROP `user`;

ALTER TABLE `task_event` DROP FOREIGN KEY `FK_TASK_EVENT_USER`;

ALTER TABLE `task_event` DROP  KEY `IDX_TASK_EVENT_USER`;

ALTER TABLE `task_event` DROP `user`;

ALTER TABLE `task` DROP FOREIGN KEY `FK_TASK_USER`;

ALTER TABLE `task` DROP  KEY `IDX_TASK_USER`;

ALTER TABLE `task` DROP `user`;

ALTER TABLE `task_event` ADD `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion' AFTER `event`;

ALTER TABLE `task_event` CHANGE  `create_date` `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion' AFTER `creation_user`;

ALTER TABLE `task_event` ADD `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion' AFTER `creation_date`;

ALTER TABLE `task_event` ADD `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion' AFTER `modification_user`;


ALTER TABLE `task_comment` ADD `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion' AFTER `comment`;

ALTER TABLE `task_comment` CHANGE  `create_date` `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion' AFTER `creation_user`;

ALTER TABLE `task_comment` ADD `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion' AFTER `creation_date`;

ALTER TABLE `task_comment` CHANGE  `update_date` `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion' AFTER `modification_user`;



ALTER TABLE `task` ADD `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion' AFTER `gtasklist_id`;

ALTER TABLE `task` ADD `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion'  AFTER `creation_user`;

ALTER TABLE `task` ADD `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion' AFTER `creation_date`;

ALTER TABLE `task` CHANGE  `update_date` `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion de la Tarea' AFTER `modification_user`;

UPDATE `db_version` SET `version_number` = '8.68.0';

COMMIT;

