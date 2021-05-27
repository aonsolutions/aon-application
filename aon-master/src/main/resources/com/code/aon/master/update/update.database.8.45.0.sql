# Database: aon_master
# Version: Actualizacion de la version 8.45.0 a la version 8.46.0
# Created by: girazu
# Creation Date: 16/03/2016 16:55

BEGIN;

ALTER TABLE `product` ADD `packaged` tinyint(1) default '0' COMMENT 'Indica si el Producto es envasado' AFTER `composition_price`;

ALTER TABLE `item` ADD `pack_format_tag` int(4) default NULL COMMENT 'Identificador de la Etiqueta de formato' AFTER `barcode`;
ALTER TABLE `item` ADD `pack_units` int(4) default '0' COMMENT 'Numero de unidades por formato' AFTER `pack_format_tag`;
ALTER TABLE `item` ADD `pack_units_tag` int(4) default NULL COMMENT 'Identificador de la Etiqueta de unidad de envase' AFTER `pack_units`;
ALTER TABLE `item` ADD `pack_measurement` double default '0' COMMENT 'Medida envasada' AFTER `pack_units_tag`;
ALTER TABLE `item` ADD `pack_measurement_tag` int(4) default NULL COMMENT 'Identificador de la Etiqueta de unidad de medida' AFTER `pack_measurement`;
ALTER TABLE `item` ADD KEY `IDX_ITEM_TAG_PACK_FORMAT` (`pack_format_tag`);
ALTER TABLE `item` ADD CONSTRAINT `FK_ITEM_TAG_PACK_FORMAT` FOREIGN KEY (`pack_format_tag`) REFERENCES `tag` (`id`);
ALTER TABLE `item` ADD KEY `IDX_ITEM_TAG_PACK_UNITS` (`pack_units_tag`);
ALTER TABLE `item` ADD CONSTRAINT `FK_ITEM_TAG_PACK_UNITS` FOREIGN KEY (`pack_units_tag`) REFERENCES `tag` (`id`);
ALTER TABLE `item` ADD KEY `IDX_ITEM_TAG_PACK_MEASUREMENT` (`pack_measurement_tag`);
ALTER TABLE `item` ADD CONSTRAINT `FK_ITEM_TAG_PACK_MEASUREMENT` FOREIGN KEY (`pack_measurement_tag`) REFERENCES `tag` (`id`);


UPDATE `db_version` SET `version_number` = '8.46.0';

COMMIT;
