# Database: aon_master
# Version: Actualizacion de la version 8.29.0 a la version 8.31.0.
# Created by: girazu
# Creation Date: 06/10/2015 17:25

BEGIN;

ALTER TABLE `catalogue_item` MODIFY `item` int(4) default NULL COMMENT 'Identificador del Articulo';
ALTER TABLE `catalogue_item` ADD `product` int(4) default NULL COMMENT 'Identificador del Producto' AFTER `catalogue`;
UPDATE `catalogue_item` SET `product` = (SELECT `product` FROM `item` WHERE `id` = `catalogue_item`.`item`);
ALTER TABLE `catalogue_item` MODIFY `product` int(4) NOT NULL COMMENT 'Identificador del Producto';
ALTER TABLE `catalogue_item` ADD KEY `IDX_CATALOGUE_ITEM_PRODUCT` (`product`);
ALTER TABLE `catalogue_item` ADD CONSTRAINT `FK_CATALOGUE_ITEM_PRODUCT` FOREIGN KEY (`product`) REFERENCES `product` (`id`);


UPDATE `db_version` SET `version_number` = '8.31.0';

COMMIT;
