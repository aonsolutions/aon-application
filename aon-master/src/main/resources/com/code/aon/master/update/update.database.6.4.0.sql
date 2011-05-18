# Database: aon_master
# Version: Actualizacion de la version 6.3.1 a la version 6.4.0.
# Created by: girazu
# Creation Date: 20/04/2011 12:25
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `series` MODIFY `offer` tinyint(1) default '0' COMMENT 'Indica si es una Serie para Presupuestos';

ALTER TABLE `series` MODIFY `sales` tinyint(1) default '0' COMMENT 'Indica si es una Serie para Pedidos';

ALTER TABLE `series` MODIFY `delivery` tinyint(1) default '0' COMMENT 'Indica si es una Serie para Albaranes';

ALTER TABLE `series` MODIFY `invoice` tinyint(1) default '0' COMMENT 'Indica si es una Serie para Facturas';

ALTER TABLE `product` ADD `composition_price` tinyint(1) default '0' COMMENT 'Indica si el Precio lo determina la Composicion';

CREATE TABLE `item_composition` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo compuesto',
  `composition_item` int(4) NOT NULL COMMENT 'Identificador del Articulo componente',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del componente',
  `quantity` double(15,3) default '0.000' COMMENT 'Cantidad del componente',
  `price` double default '0' COMMENT 'Precio del componente',
  `discount_expr` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descuentos del componente',
  PRIMARY KEY  (`id`),
  KEY `IDX_ITEM_COMPOSITION_ITEM` (`item`),
  KEY `IDX_ITEM_COMPOSITION_COMPOSITION` (`composition_item`),
  CONSTRAINT `FK_ITEM_COMPOSITION_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_COMPOSITION_COMPOSITION` FOREIGN KEY (`composition_item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Composicion de Articulos';


UPDATE `db_version` SET `version_number` = '6.4.1';

COMMIT;
