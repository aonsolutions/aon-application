# Database: aon_master
# Version: Actualizacion de la version 7.12.0 a la version 7.12.1.
# Created by: girazu
# Creation Date: 26/02/2013 15:50
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `pos` ADD `active` tinyint(1) default '1' COMMENT 'Indica si el TPV esta activo o no';


UPDATE `db_version` SET `version_number` = '7.12.1';

COMMIT;
