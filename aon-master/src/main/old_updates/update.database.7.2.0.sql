# Database: aon_master
# Version: Actualizacion de la version 7.2.0 a la version 7.2.1.
# Created by: ecastellano
# Creation Date: 26/11/2012 20:20
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `account` ADD `cost_center` char(32) collate latin1_spanish_ci default NULL COMMENT 'Centro de Costo';

UPDATE `db_version` SET `version_number` = '7.2.1';

COMMIT;
