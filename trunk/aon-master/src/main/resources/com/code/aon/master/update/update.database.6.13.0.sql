# Database: aon_master
# Version: Actualizacion de la version 6.13.0 a la version 6.14.0.
# Created by: eagirrezabal
# Creation Date: 25/08/2011 14:47
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `irpf_data` DROP COLUMN `date`;

ALTER TABLE `irpf_data` ADD `start_date` date default NULL COMMENT 'Fecha inicio del modelo';
ALTER TABLE `irpf_data` ADD `end_date` date default NULL COMMENT 'Fecha fin del modelo';

UPDATE `db_version` SET `version_number` = '6.14.0';

COMMIT;
