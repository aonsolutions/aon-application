# Database: aon_master
# Version: Actualizacion de la version 5.1.0 a la version 5.1.1.
# Created by: girazu
# Creation Date: 30/03/2010 10:12
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `commission_category` CHANGE `discount` `rate` double(6,2) default '0.00' COMMENT 'Porcentaje de Comision';

ALTER TABLE `commission_item` CHANGE `price` `amount` double default '0' COMMENT 'Importe de la Comision';

ALTER TABLE `commission_item` CHANGE `discount` `rate` double(6,2) default '0.00' COMMENT 'Porcentaje de Comision';

CREATE TABLE `commission_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Comision',
  `rate` double(6,2) default '0.00' COMMENT 'Porcentaje de Comision',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Comisiones';

CREATE TABLE `commission_type_commission` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `commission_type` int(4) NOT NULL COMMENT 'Identificador del Tipo de Comision',
  `commission` int(4) NOT NULL COMMENT 'Identificador de la Comision',
  PRIMARY KEY  (`id`),
  KEY `IDX_COMMISSION_TYPE_COMMISSION_COMMISSION_TYPE` (`commission_type`),
  KEY `IDX_COMMISSION_TYPE_COMMISSION_COMMISSION` (`commission`),
  CONSTRAINT `FK_COMMISSION_TYPE_COMMISSION_COMMISSION_TYPE` FOREIGN KEY (`commission_type`) REFERENCES `commission_type` (`id`),
  CONSTRAINT `FK_COMMISSION_TYPE_COMMISSION_COMMISSION` FOREIGN KEY (`commission`) REFERENCES `commission` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones por Tipo de Comision';

ALTER TABLE `seller` ADD `commission_type` int(4) default NULL COMMENT 'Identificador del Tipo de Comision' AFTER `description`;

ALTER TABLE `seller` ADD KEY `IDX_SELLER_COMMISSION_TYPE` (`commission_type`);

ALTER TABLE `seller` ADD CONSTRAINT `FK_SELLER_COMMISSION_TYPE` FOREIGN KEY (`commission_type`) REFERENCES `commission_type` (`id`);

ALTER TABLE `seller` DROP `description`;


UPDATE `db_version` SET `version_number` = '5.1.1';

COMMIT;
