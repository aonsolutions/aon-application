# Database: aon_master
# Version: Actualizacion de la version 7.16.0 a la version 7.16.1.
# Created by: girazu
# Creation Date: 02/05/2013 17:40
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

CREATE TABLE `carrier` (
  `registry` int(4) NOT NULL COMMENT 'Registro de la Agencia de Transporte',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  PRIMARY KEY  (`registry`),
  KEY `IDX_CARRIER_DOMAIN` (`domain`),
  KEY `IDX_CARRIER_SCOPE` (`scope`),
  CONSTRAINT `FK_CARRIER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CARRIER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_CARRIER_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Agencias de Transporte'; 

ALTER TABLE `sales` ADD `carrier` int(4) default NULL COMMENT 'Identificador de la Agencia de Transporte';
ALTER TABLE `sales` ADD `shipping_alternative_address` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Primera parte de la Direccion de entrega';
ALTER TABLE `sales` ADD `shipping_alternative_address2` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Segunda parte de la Direccion de entrega';
ALTER TABLE `sales` ADD `shipping_alternative_zip` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo Postal de entrega';
ALTER TABLE `sales` ADD `shipping_alternative_city` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Localidad de entrega';
ALTER TABLE `sales` ADD `shipping_alternative_phone` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Telefono de contacto de la entrega';
ALTER TABLE `sales` ADD `shipping_alternative_recipient` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Destinatario de la entrega';
ALTER TABLE `sales` ADD `shipping_contact` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Nombre del contacto para la entrega';
ALTER TABLE `sales` ADD `shipping_period` tinyint(2) default '0' COMMENT 'Tipo de periodo de entrega';
ALTER TABLE `sales` ADD KEY `IDX_SALES_CARRIER` (`carrier`);
ALTER TABLE `sales` ADD CONSTRAINT `FK_SALES_CARRIER` FOREIGN KEY (`carrier`) REFERENCES `carrier` (`registry`);

ALTER TABLE `delivery` ADD `carrier` int(4) default NULL COMMENT 'Identificador de la agencia de transporte';
ALTER TABLE `delivery` ADD `number_plate` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Numero de matricula';
ALTER TABLE `delivery` ADD `driver` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Nombre del conductor';
ALTER TABLE `delivery` ADD `driver_document` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Numero de Documento del conductor';
ALTER TABLE `delivery` ADD `total_packages` double(15,3) default '0.000' COMMENT 'Numero total de bultos';
ALTER TABLE `delivery` ADD `total_weight` double(15,3) default '0.000' COMMENT 'Peso total';
ALTER TABLE `delivery` ADD `shipping_alternative_address` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Primera parte de la Direccion de entrega';
ALTER TABLE `delivery` ADD `shipping_alternative_address2` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Segunda parte de la Direccion de entrega';
ALTER TABLE `delivery` ADD `shipping_alternative_zip` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo Postal de entrega';
ALTER TABLE `delivery` ADD `shipping_alternative_city` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Localidad de entrega';
ALTER TABLE `delivery` ADD `shipping_alternative_phone` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Telefono de contacto de la entrega';
ALTER TABLE `delivery` ADD `shipping_alternative_recipient` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Destinatario de la entrega';
ALTER TABLE `delivery` ADD `shipping_contact` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Nombre del contacto para la entrega';
ALTER TABLE `delivery` ADD `shipping_period` tinyint(2) default '0' COMMENT 'Tipo de periodo de entrega';
ALTER TABLE `delivery` ADD `tracking_number` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Numero de expedicion';
ALTER TABLE `delivery` ADD `shipping_status` tinyint(2) default '0' COMMENT 'Estado de la entrega';
ALTER TABLE `delivery` ADD `status_modification_date` datetime default NULL COMMENT 'Fecha de modificacion del estado';
ALTER TABLE `delivery` ADD KEY `IDX_DELIVERY_CARRIER` (`carrier`);
ALTER TABLE `delivery` ADD CONSTRAINT `FK_DELIVERY_CARRIER` FOREIGN KEY (`carrier`) REFERENCES `carrier` (`registry`);


UPDATE `db_version` SET `version_number` = '7.16.1';

COMMIT;
