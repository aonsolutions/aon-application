# Database: aon_master
# Version: Actualizacion de la version 6.18.4 a la version 6.18.5.
# Created by: girazu
# Creation Date: 20/10/2011 13:25
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `project_activity` DROP FOREIGN KEY `FK_PRJ_ACT_WORKGROUP`;

ALTER TABLE `project_activity` DROP INDEX `IDX_PRJ_ACT_WORKGROUP`;

ALTER TABLE `project_activity` DROP `workgroup` ;

ALTER TABLE `target` ADD `scope` int(4) default NULL COMMENT 'Identificador del Ambito';

UPDATE `target` SET `scope` = (SELECT `scope` FROM `customer` WHERE `registry` = `target`.`registry`);

UPDATE `target` SET `scope` = 1 WHERE `scope` IS NULL OR `scope` = 0;

ALTER TABLE `target` MODIFY `scope` int(4) NOT NULL default '1' COMMENT 'Identificador del Ambito';

ALTER TABLE `target` ADD KEY `IDX_TARGET_SCOPE` (`scope`);

ALTER TABLE `target` ADD CONSTRAINT `FK_TARGET_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);


UPDATE `db_version` SET `version_number` = '6.18.5';

COMMIT;
