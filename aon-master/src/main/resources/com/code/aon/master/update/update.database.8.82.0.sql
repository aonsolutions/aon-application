# Database: aon_master
# Version: Actualizacion de la version 8.82.0 a la version 8.83.0.
# Created by: eagirrezabal
# Creation Date: 05/01/2017 14:00

BEGIN;

CREATE TABLE `elaboration` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la elaboracion',
  `domain` int(4) NOT NULL COMMENT 'Identificador del dominio',
  `series` char(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie de la elaboracion',
  `number` int(4) NOT NULL DEFAULT '0' COMMENT 'Numero de la elaboracion',
  `date` datetime DEFAULT NULL COMMENT 'Fecha de elaboracion',
  `item` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del articulo base a elaborar',
  `warehouse` int(4) DEFAULT NULL COMMENT 'Identificador del almacen',
  `quantity` double(15,3) DEFAULT '0.000' COMMENT 'Cantidad a elaborar',
  `status` tinyint(2) NOT NULL default '0' COMMENT 'Indica el estado de la elaboracion',
  `comments` varchar(128) collate latin1_spanish_ci DEFAULT NULL COMMENT 'Comentarios',
  `source` tinyint(2) DEFAULT NULL default '0' COMMENT 'Origen',
  `source_id` int(4) DEFAULT NULL default '0' COMMENT 'Identificador del origen',
  `creation_user` varchar(16) collate latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) collate latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_ELABORATION_DOMAIN` (`domain`),
  KEY `IDX_ELABORATION_ITEM` (`item`),
  KEY `IDX_ELABORATION_WAREHOUSE` (`warehouse`),
  CONSTRAINT `FK_ELABORATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ELABORATION_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ELABORATION_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ordenes de elaboracion';

CREATE TABLE `elaboration_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del detalle',
  `domain` int(4) NOT NULL COMMENT 'Identificador del dominio',
  `elaboration` int(4) NOT NULL COMMENT 'Identificador de la elaboracion',
  `date` datetime DEFAULT NULL COMMENT 'Fecha de elaboracion',
  `item` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del articulo no base elaborado',
  `quantity` double(15,3) DEFAULT '0.000' COMMENT 'Cantidad elaborado',
  `warehouse` int(4) DEFAULT NULL COMMENT 'Identificador del almacen',
  `add_info` text COLLATE latin1_spanish_ci COMMENT 'Informacion de uso interno',
  `creation_user` varchar(16) collate latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) collate latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_ELABORATION_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_ELABORATION_DETAIL_ELABORATION` (`elaboration`),
  KEY `IDX_ELABORATION_DETAIL_ITEM` (`item`),
  KEY `IDX_ELABORATION_DETAIL_WAREHOUSE` (`warehouse`),
  CONSTRAINT `FK_ELABORATION_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ELABORATION_DETAIL_ELABORATION` FOREIGN KEY (`elaboration`) REFERENCES `elaboration` (`id`),
  CONSTRAINT `FK_ELABORATION_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ELABORATION_DETAIL_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de ordenes de elaboracion';

CREATE TABLE `elaboration_detail_composition` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la composicion',
  `domain` int(4) NOT NULL COMMENT 'Identificador del dominio',
  `elaboration_detail` int(4) NOT NULL COMMENT 'Identificador del lote elaborado',
  `item` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del articulo no base utilizado',
  `quantity` double(15,3) DEFAULT '0.000' COMMENT 'Cantidad utilizada',
  `warehouse` int(4) DEFAULT NULL COMMENT 'Identificador del almacen',
  `add_info` text COLLATE latin1_spanish_ci COMMENT 'Informacion de uso interno',
  `creation_user` varchar(16) collate latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) collate latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_ELABORATION_DETAIL_COMPOSITION_DOMAIN` (`domain`),
  KEY `IDX_ELABORATION_DETAIL_COMPOSITION_ELABORATION_DETAIL` (`elaboration_detail`),
  KEY `IDX_ELABORATION_DETAIL_COMPOSITION_ITEM` (`item`),
  KEY `IDX_ELABORATION_DETAIL_COMPOSITION_WAREHOUSE` (`warehouse`),
  CONSTRAINT `FK_ELABORATION_DETAIL_COMPOSITION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ELABORATION_DETAIL_COMPOSITION_ELABORATION_DETAIL` FOREIGN KEY (`elaboration_detail`) REFERENCES `elaboration_detail` (`id`),
  CONSTRAINT `FK_ELABORATION_DETAIL_COMPOSITION_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ELABORATION_DETAIL_COMPOSITION_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Composicion de ordenes de elaboracion';


UPDATE `db_version` SET `version_number` = '8.83.0';

COMMIT;
