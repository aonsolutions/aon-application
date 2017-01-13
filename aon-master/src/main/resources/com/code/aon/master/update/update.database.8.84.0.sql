# Database: aon_master
# Version: Actualizacion de la version 8.84.0 a la version 8.84.1.
# Created by: eagirrezabal
# Creation Date: 13/01/2017 12:00

BEGIN;

CREATE TABLE `carrier_list` ( 
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la hoja de ruta',
  `domain` int(4) NOT NULL COMMENT 'Identificador del dominio',
  `series` char(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie de la hoja de ruta',
  `number` int(4) NOT NULL DEFAULT '0' COMMENT 'Numero de la hoja de ruta',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Indica el tipo de la hoja de ruta',
  `status` tinyint(2) NOT NULL default '0' COMMENT 'Indica el estado de la hoja de ruta',
  `issue_date` datetime DEFAULT NULL COMMENT 'Fecha de emision',
  `carrier` int(4) NOT NULL COMMENT 'Identificador de la agencia de transporte',
  `delivery_date` datetime DEFAULT NULL COMMENT 'Fecha de entrega',
  `carrier_reference` varchar(64) collate latin1_spanish_ci DEFAULT NULL COMMENT 'Referencia de la agencia de transporte',
  `number_plate` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de matricula del vehiculo',
  `driver_name` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del conductor',
  `driver_document` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de documento del conductor',
  `source` tinyint(2) DEFAULT NULL default '0' COMMENT 'Origen',
  `source_id` int(4) DEFAULT NULL default '0' COMMENT 'Identificador del origen',
  `creation_user` varchar(16) collate latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) collate latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_CARRIER_LIST_DOMAIN` (`domain`),
  KEY `IDX_CARRIER_LIST_CARRIER` (`carrier`),
  CONSTRAINT `FK_CARRIER_LIST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CARRIER_LIST_CARRIER` FOREIGN KEY (`carrier`) REFERENCES `carrier` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Hojas de ruta';

ALTER TABLE `sales_detail` ADD `delivery_date` datetime DEFAULT NULL COMMENT 'Fecha de entrega';
ALTER TABLE `purchase_detail` ADD `delivery_date` datetime DEFAULT NULL COMMENT 'Fecha de entrega';

UPDATE `db_version` SET `version_number` = '8.84.1';

COMMIT;
