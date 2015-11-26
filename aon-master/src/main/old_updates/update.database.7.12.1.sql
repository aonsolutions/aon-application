# Database: aon_master
# Version: Actualizacion de la version 7.12.1 a la version 7.12.2.
# Created by: girazu
# Creation Date: 27/02/2013 17:50
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `item` ADD `detail3` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Detalle 3 del Articulo' AFTER `detail2`;


UPDATE `db_version` SET `version_number` = '7.12.2';

COMMIT;
