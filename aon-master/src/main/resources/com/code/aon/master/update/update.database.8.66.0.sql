# Database: aon_master
# Version: Actualizacion de la version 8.66.0 a la version 8.67.0.
# Created by: aibanez
# Creation Date: 27/09/2016 

BEGIN;

ALTER TABLE `task_comment` MODIFY `update_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion';
ALTER TABLE `task_comment` MODIFY `create_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion';

ALTER TABLE `task_event` MODIFY `create_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion';


ALTER TABLE `task` MODIFY `update_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion de la Tarea';
ALTER TABLE `task` MODIFY `start_date` datetime NOT NULL COMMENT 'Fecha de inicio de la Tarea';
ALTER TABLE `task` MODIFY `end_date` datetime DEFAULT NULL COMMENT 'Fecha de finalizacion de la Tarea';
ALTER TABLE `task` MODIFY `due_date` datetime DEFAULT NULL COMMENT 'Fecha de vencimiento de la Tarea';

UPDATE `db_version` SET `version_number` = '8.67.0';

COMMIT;

