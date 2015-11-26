# Database: aon_master
# Version: Actualizacion de la version 6.7.0 a la version 6.7.1.
# Created by: girazu
# Creation Date: 06/07/2011 11:17
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `offer` ADD `external_reference` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Referencia externa';

ALTER TABLE `project_tas` DROP `external_reference`;

ALTER TABLE `project_tas` MODIFY `task_holder` int(4) default NULL COMMENT 'Identificador del Empleado que ejecuta la Orden';

ALTER TABLE `project_tas` ADD KEY `IDX_PROJECT_TAS_TAS_ITEM` (`tas_item`);

ALTER TABLE `project_tas` ADD CONSTRAINT `FK_PROJECT_TAS_TAS_ITEM` FOREIGN KEY (`tas_item`) REFERENCES `tas_item` (`id`);

ALTER TABLE `project_tas` ADD KEY `IDX_PROJECT_TAS_TASK_HOLDER` (`task_holder`);

ALTER TABLE `project_tas` ADD CONSTRAINT `FK_PROJECT_TAS_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`);

ALTER TABLE `series` ADD `tas` tinyint(1) default '0' COMMENT 'Indica si es una Serie para Ordenes de Reparacion' AFTER `workplace`;

UPDATE `series` SET `tas` = `offer`;


UPDATE `db_version` SET `version_number` = '6.7.1';

COMMIT;
