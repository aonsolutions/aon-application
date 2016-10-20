# Database: aon_master
# Version: Actualizacion de la version 8.72.0 a la version 8.72.1.
# Created by: girazu
# Creation Date: 05/10/2016 10:50

BEGIN;

ALTER TABLE `item` ADD `stock_unit_tag` int(4) DEFAULT NULL COMMENT 'Identificador de la Etiqueta de unidad de Stock' AFTER `pack_measurement_tag`;
ALTER TABLE `item` ADD KEY `IDX_ITEM_TAG_STOCK_UNIT` (`stock_unit_tag`);
ALTER TABLE `item` ADD CONSTRAINT `FK_ITEM_TAG_STOCK_UNIT` FOREIGN KEY (`stock_unit_tag`) REFERENCES `tag` (`id`);
UPDATE `item` SET `stock_unit_tag` = `pack_format_tag` WHERE `pack_format_tag` IS NOT NULL;

ALTER TABLE `purchase_detail` CHANGE `source` `source` tinyint(2) DEFAULT '0' COMMENT 'Origen del Detalle de la Compra' AFTER `status`;
ALTER TABLE `purchase_detail` CHANGE `source_id` `source_id` int(4) DEFAULT NULL COMMENT 'Identificador del Origen del Detalle de la Compra' AFTER `source`;


UPDATE `db_version` SET `version_number` = '8.72.1';

COMMIT;

