# Database: aon_master
# Version: Actualizacion de la version 8.96.0 a la version 8.98.0.
# Created by: aibanez
# Creation Date: 07/04/2017 13:15

BEGIN;

ALTER TABLE `carrier_packing` ADD `gross` double default NULL COMMENT 'Bruto' AFTER `comments`;

ALTER TABLE `carrier_packing` ADD `tare` double default NULL COMMENT 'Tara' AFTER `gross`;

ALTER TABLE `carrier_packing` ADD `net` double default NULL COMMENT 'Neto' AFTER `tare`;

ALTER TABLE `carrier_packing` ADD `reception_start_date` datetime default NULL COMMENT 'Fecha entrada transporte, recepcion' AFTER `net`;

ALTER TABLE `carrier_packing` ADD `reception_end_date` datetime default NULL COMMENT 'Fecha salida transporte, recepcion' AFTER `reception_start_date`;

UPDATE `db_version` SET `version_number` = '8.98.0';

COMMIT;
