# Database: aon_master
# Version: Actualizacion de la version 8.86.0 a la version 8.88.0.
# Created by: girazu
# Creation Date: 03/02/2017 12:50

BEGIN;

ALTER TABLE `sales_detail` ADD `carrier` int(4) DEFAULT NULL COMMENT 'Identificador de la Agencia de Transporte' AFTER `delivery_date`;
ALTER TABLE `sales_detail` ADD KEY `IDX_SALES_DETAIL_CARRIER` (`carrier`);
ALTER TABLE `sales_detail` ADD CONSTRAINT `FK_SALES_DETAIL_CARRIER` FOREIGN KEY (`carrier`) REFERENCES `carrier` (`registry`);

ALTER TABLE `sales_detail` ADD `carrier_packing` int(4) DEFAULT NULL COMMENT 'Identificador de la Hoja de ruta' AFTER `carrier`;
ALTER TABLE `sales_detail` ADD KEY `IDX_SALES_DETAIL_CARRIER_PACKING` (`carrier_packing`);
ALTER TABLE `sales_detail` ADD CONSTRAINT `FK_SALES_DETAIL_CARRIER_PACKING` FOREIGN KEY (`carrier_packing`) REFERENCES `carrier_packing` (`id`);

ALTER TABLE `purchase_detail` ADD `carrier` int(4) DEFAULT NULL COMMENT 'Identificador de la Agencia de Transporte' AFTER `delivery_date`;
ALTER TABLE `purchase_detail` ADD KEY `IDX_PURCHASE_DETAIL_CARRIER` (`carrier`);
ALTER TABLE `purchase_detail` ADD CONSTRAINT `FK_PURCHASE_DETAIL_CARRIER` FOREIGN KEY (`carrier`) REFERENCES `carrier` (`registry`);

ALTER TABLE `purchase_detail` ADD `carrier_packing` int(4) DEFAULT NULL COMMENT 'Identificador de la Hoja de ruta' AFTER `carrier`;
ALTER TABLE `purchase_detail` ADD KEY `IDX_PURCHASE_DETAIL_CARRIER_PACKING` (`carrier_packing`);
ALTER TABLE `purchase_detail` ADD CONSTRAINT `FK_PURCHASE_DETAIL_CARRIER_PACKING` FOREIGN KEY (`carrier_packing`) REFERENCES `carrier_packing` (`id`);


UPDATE `db_version` SET `version_number` = '8.88.0';

COMMIT;
