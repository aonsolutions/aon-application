# Database: aon_master
# Version: Actualizacion de la version 7.11.0 a la version 7.12.0.
# Created by: girazu
# Creation Date: 22/02/2013 09:50
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

UPDATE `item` SET `barcode` = NULL WHERE TRIM(`barcode`) = '';
ALTER TABLE `item` ADD UNIQUE KEY `IDX_ITEM_DOMAIN_BARCODE` (`domain`,`barcode`);

ALTER TABLE `pcategory` ADD `detail3` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre del detalle 3 de los Articulos';

ALTER TABLE `pcategory` DROP FOREIGN KEY `FK_PCATEGORY_PCATEGORY_GROUP`;
ALTER TABLE `pcategory` DROP INDEX `IDX_PCATEGORY_PCATEGORY_GROUP`;
ALTER TABLE `pcategory` DROP COLUMN `pcategory_group`;

ALTER TABLE `tag` ADD `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de Etiqueta';

CREATE TABLE `product_tag` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `product` int(4) NOT NULL COMMENT 'Identificador del Producto',
  `tag` int(4) NOT NULL COMMENT 'Identificador de la Etiqueta',
  PRIMARY KEY (`id`),
  KEY `IDX_PRODUCT_TAG_DOMAIN` (`domain`),
  KEY `IDX_PRODUCT_TAG_PRODUCT` (`product`),
  KEY `IDX_PRODUCT_TAG_TAG` (`tag`),
  CONSTRAINT `FK_PRODUCT_TAG_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PRODUCT_TAG_PRODUCT` FOREIGN KEY (`product`) REFERENCES `product` (`id`),
  CONSTRAINT `FK_PRODUCT_TAG_TAG` FOREIGN KEY (`tag`) REFERENCES `tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Productos y Etiquetas';

DROP TABLE `pcategory_tree`;
DROP TABLE `pcategory_group`;


UPDATE `db_version` SET `version_number` = '7.12.0';

COMMIT;
