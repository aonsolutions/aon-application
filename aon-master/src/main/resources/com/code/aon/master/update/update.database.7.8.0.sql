# Database: aon_master
# Version: Actualizacion de la version 7.8.0 a la version 7.9.0.
# Created by: girazu
# Creation Date: 01/02/2013 10:40
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `domain` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `domain` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `domain` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `domain` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

UPDATE `finance` SET `registry` = (SELECT `registry` FROM `invoice` WHERE `id` = `finance`.`invoice`) WHERE `registry` IS NULL AND `invoice` IS NOT NULL;


UPDATE `db_version` SET `version_number` = '7.9.0';

COMMIT;
