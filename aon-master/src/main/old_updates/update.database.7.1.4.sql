# Database: aon_master
# Version: Actualizacion de la version 7.1.4 a la version 7.1.5.
# Created by: girazu
# Creation Date: 19/07/2012 09:45
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `hotel` ADD `sheet_changing` tinyint(2) default NULL COMMENT 'Dias entre cambio de sabanas' AFTER `item_penalty`;


UPDATE `db_version` SET `version_number` = '7.1.5';

COMMIT;
