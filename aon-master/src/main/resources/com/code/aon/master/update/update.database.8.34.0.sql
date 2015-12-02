# Database: aon_master
# Version: Actualizacion de la version 8.34.0 a la version 8.35.0.
# Created by: girazu
# Creation Date: 26/11/2015 11:35

BEGIN;

ALTER TABLE `item_addinfo` ADD `product` int(4) default NULL COMMENT 'Identificador del Producto' AFTER `domain`;
UPDATE `item_addinfo` SET `product` = (SELECT `product` FROM `item` WHERE `id` = `item_addinfo`.`item`);
ALTER TABLE `item_addinfo` MODIFY `product` int(4) NOT NULL COMMENT 'Identificador del Producto';
ALTER TABLE `item_addinfo` ADD KEY `IDX_ITEM_ADDINFO_PRODUCT` (`product`);
ALTER TABLE `item_addinfo` ADD CONSTRAINT `FK_ITEM_ADDINFO_PRODUCT` FOREIGN KEY (`product`) REFERENCES `product` (`id`);

ALTER TABLE `tariff` ADD `active` tinyint(1) NOT NULL default '1' COMMENT 'Indica si la Tarifa esta activa o no';

ALTER TABLE `enterprise_activity` ADD `principal` tinyint(1) NOT NULL default '0' COMMENT 'Indica si es la Actividad principal';

ALTER TABLE `invoice` ADD `activity` int(4) DEFAULT NULL COMMENT 'Identificador de la Actividad' AFTER `domain`;
ALTER TABLE `invoice` ADD KEY `IDX_INVOICE_ACTIVITY` (`activity`);
ALTER TABLE `invoice` ADD CONSTRAINT `FK_INVOICE_ACTIVITY` FOREIGN KEY (`activity`) REFERENCES `enterprise_activity` (`id`);


UPDATE `db_version` SET `version_number` = '8.35.0';

COMMIT;
