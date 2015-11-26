# Database: aon_master
# Version: Actualizacion de la version 7.10.1 a la version 7.10.2.
# Created by: girazu
# Creation Date: 07/02/2013 12:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `fs_activity` CHANGE `maxPerson` `max_person` double(15,3) DEFAULT '0.000' COMMENT 'Valor maximo de personas';
ALTER TABLE `fs_activity` CHANGE `maxImport` `max_import` double(15,3) DEFAULT '0.000' COMMENT 'Valor maximo de importe';
ALTER TABLE `fs_activity` CHANGE `vatPercent` `vat_percent` double(15,3) DEFAULT '0.000' COMMENT 'IVA - porcentaje aplicable';

ALTER TABLE `fs_activity_info` CHANGE COLUMN `minValue` `min_value` double(15,3) DEFAULT '0.000' COMMENT 'Valor minimo';
ALTER TABLE `fs_activity_info` CHANGE COLUMN `maxValue` `max_value` double(15,3) DEFAULT '0.000' COMMENT 'Valor maximo';


UPDATE `db_version` SET `version_number` = '7.10.2';

COMMIT;
