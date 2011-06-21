# Database: aon_master
# Version: Actualizacion de la version 6.4.8 a la version 6.5.0.
# Created by: girazu
# Creation Date: 16/06/2011 18:08
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `item_composition` DROP `price`;

DROP TABLE `item_tariff`;

CREATE TABLE `item_tariff` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador de Articulo',
  `tariff` int(4) NOT NULL default '0' COMMENT 'Identificador de Tarifa',
  `type` tinyint(2) default '0' COMMENT 'Tipo de Tarifa',
  `profit_percent` double default '0' COMMENT 'Porcentaje de beneficio',
  `price` double default '0' COMMENT 'Precio de Venta',
  PRIMARY KEY  (`id`),
  KEY `IDX_ITEM_TARIFF_TARIFF` (`tariff`),
  KEY `IDX_ITEM_TARIFF_ITEM` (`item`),
  CONSTRAINT `FK_ITEM_TARIFF_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`),
  CONSTRAINT `FK_ITEM_TARIFF_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas de Articulos';

ALTER TABLE `rbank` ADD `alias` varchar(16) default NULL; 

ALTER TABLE `rbank` ADD `active` tinyint(1) NOT NULL default '1';


UPDATE `db_version` SET `version_number` = '6.5.0';

COMMIT;
