# Database: aon_master
# Version: Actualizacion de la version 8.65.0 a la version 8.66.0.
# Created by: aibanez
# Creation Date: 06/09/2016 

BEGIN;

ALTER TABLE `task` MODIFY `update_date` date DEFAULT NULL COMMENT 'Fecha de modificacion de la Tarea';

ALTER TABLE `task` ADD `user` int(4) DEFAULT NULL COMMENT 'Relación entre user y task' AFTER `registry`;

ALTER TABLE `task` ADD KEY `IDX_TASK_USER` (`user`);

ALTER TABLE `task` ADD CONSTRAINT `FK_TASK_USER` FOREIGN KEY (`user`) REFERENCES `user` (`id`);

UPDATE `task` SET `update_date` = NULL;

ALTER TABLE `task_event` DROP FOREIGN KEY `FK_TASK_EVENT_REGISTRY`;

ALTER TABLE `task_event` DROP  KEY `IDX_TASK_EVENT_REGISTRY`;

ALTER TABLE `task_event` DROP `registry`;

ALTER TABLE `task_event` ADD `user` int(4) DEFAULT NULL COMMENT 'Relación entre user y task event' AFTER `task`;

ALTER TABLE `task_event` ADD KEY `IDX_TASK_EVENT_USER` (`user`);

ALTER TABLE `task_event` ADD CONSTRAINT `FK_TASK_EVENT_USER` FOREIGN KEY (`user`) REFERENCES `user` (`id`);

ALTER TABLE `task_comment` DROP FOREIGN KEY `FK_TASK_COMMENT_REGISTRY`;

ALTER TABLE `task_comment` DROP  KEY `IDX_TASK_COMMENT_REGISTRY`;

ALTER TABLE `task_comment` DROP `registry`;

ALTER TABLE `task_comment` ADD `user` int(4) DEFAULT NULL COMMENT 'Relación entre user y task event' AFTER `task`;

ALTER TABLE `task_comment` ADD KEY `IDX_TASK_COMMENT_USER` (`user`);

ALTER TABLE `task_comment` ADD CONSTRAINT `FK_TASK_COMMENT_USER` FOREIGN KEY (`user`) REFERENCES `user` (`id`);

UPDATE `db_version` SET `version_number` = '8.66.0';

COMMIT;

