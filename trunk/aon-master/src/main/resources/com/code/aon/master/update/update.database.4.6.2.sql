# Database: aon_master
# Version: Actualizacion de la version 4.6.2 a la version 4.7.0.
# Created by: girazu
# Creation Date: 23/11/2009 18:02
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



UPDATE `invoice_attach` SET `mimeType` = 29 WHERE `mimeType` = 22;

CREATE TABLE `item_warehouse` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador de Articulo',
  `warehouse` int(4) NOT NULL default '0' COMMENT 'Identificador de Almacen',
  `stock_max` double(15,3) default '0.000' COMMENT 'Stock maximo del Articulo en el Almacen',
  `stock_min` double(15,3) default '0.000' COMMENT 'Stock minimo del Articulo en el Almacen',
  `location` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Localizacion del Articulo en el Almacen',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_UNQ_ITEM_WAREHOUSE` (`item`,`warehouse`),
  KEY `IDX_ITEM_WAREHOUSE_ITEM` (`item`),
  KEY `IDX_ITEM_WAREHOUSE_WAREHOUSE` (`warehouse`),
  CONSTRAINT `FK_ITEM_WAREHOUSE_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_WAREHOUSE_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos del Articulo por Almacen';

CREATE TABLE `item_supplier` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador de Articulo',
  `supplier` int(4) NOT NULL default '0' COMMENT 'Identificador de Proveedor',
  `code` varchar(15) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo del Producto en el Proveedor',
  `priority` tinyint(2) default '0' COMMENT 'Prioridad del Proveedor',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_UNQ_ITEM_SUPPLIER` (`item`,`supplier`),
  KEY `IDX_ITEM_SUPPLIER_ITEM` (`item`),
  KEY `IDX_ITEM_SUPPLIER_SUPPLIER` (`supplier`),
  CONSTRAINT `FK_ITEM_SUPPLIER_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_SUPPLIER_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos del Articulo por Proveedor';


UPDATE `db_version` SET `version_number` = '4.7.0';

COMMIT;
