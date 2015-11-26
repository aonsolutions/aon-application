# Database: aon_master
# Version: Actualizacion de la version 7.36.6 a la version 7.37.0.
# Created by: girazu
# Creation Date: 08/07/2014 14:30

BEGIN;

ALTER TABLE `pos` ADD `invoiceable` tinyint(1) NOT NULL default '0' COMMENT 'Indica si el Punto de Venta es facturable' AFTER `customer`;
ALTER TABLE `pos` ADD `item_invoice` int(4) default NULL COMMENT 'Identificador del Articulo facturable' AFTER `invoiceable`;
ALTER TABLE `pos` ADD KEY `IDX_POS_ITEM_INVOICE` (`item_invoice`);
ALTER TABLE `pos` ADD CONSTRAINT `FK_POS_ITEM_INVOICE` FOREIGN KEY (`item_invoice`) REFERENCES `item` (`id`);


UPDATE `db_version` SET `version_number` = '7.37.0';

COMMIT;
