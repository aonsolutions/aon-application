# Database: aon_master
# Version: Actualizacion de la version 1.7.3 a la version 1.7.4
# Created by: girazu
# Creation Date: 25/06/2008 16:00
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `task` ADD `repeat_period` tinyint(2) default '0' COMMENT 'Periodo de repeticion de la Tarea';

DROP TABLE `periodical_task`;


UPDATE `db_version` SET `version_number` = '1.7.4';

COMMIT;
