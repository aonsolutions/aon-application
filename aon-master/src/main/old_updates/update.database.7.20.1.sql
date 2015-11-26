# Database: aon_master
# Version: Actualizacion de la version 7.20.1 a la version 7.21.0.
# Created by: girazu
# Creation Date: 05/07/2013 11:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `project_reservation` CHANGE `crs` `source` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Origen de la Reserva';

ALTER TABLE `project_attach` ADD `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad del Archivo Adjunto' AFTER `data`;

UPDATE `project` SET `alias` = (SELECT `crs_code` FROM `project_reservation` WHERE `project_reservation`.`project` = `project`.`id`) WHERE `reservation` = 1;


UPDATE `db_version` SET `version_number` = '7.21.0';

COMMIT;
