# Database: aon_master
# Version: Actualizacion de la version 7.27.1 a la version 7.28.0.
# Created by: girazu
# Creation Date: 22/01/2014 10:25
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `room` ADD `active` tinyint(1) NOT NULL default '1' COMMENT 'Indica si la Habitacion esta activa o no';


UPDATE `db_version` SET `version_number` = '7.28.0';

COMMIT;
