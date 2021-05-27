# Database: aon_master
# Version: Actualizacion de la version 8.85.1 a la version 8.86.0.
# Created by: girazu
# Creation Date: 25/01/2017 10:05

BEGIN;

ALTER TABLE `sales` MODIFY `carrier_packing` int(4) DEFAULT NULL COMMENT 'Identificador de la Hoja de ruta' AFTER `carrier`;
ALTER TABLE `purchase` MODIFY `carrier_packing` int(4) DEFAULT NULL COMMENT 'Identificador de la Hoja de ruta' AFTER `carrier`;
ALTER TABLE `delivery` MODIFY `carrier_packing` int(4) DEFAULT NULL COMMENT 'Identificador de la Hoja de ruta' AFTER `carrier`;
ALTER TABLE `income` MODIFY `carrier_packing` int(4) DEFAULT NULL COMMENT 'Identificador de la Hoja de ruta' AFTER `bic`;

ALTER TABLE `sales_detail` MODIFY `delivery_date` datetime DEFAULT NULL COMMENT 'Fecha de entrega' AFTER `delivered`;
ALTER TABLE `purchase_detail` MODIFY `delivery_date` datetime DEFAULT NULL COMMENT 'Fecha de entrega' AFTER `delivered`;


UPDATE `db_version` SET `version_number` = '8.86.0';

COMMIT;
