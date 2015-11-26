# Database: aon_master
# Version: Actualizacion de la version 7.28.1 a la version 7.28.2.
# Created by: girazu
# Creation Date: 28/01/2014 12:05
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

CREATE TABLE `tariff_addinfo` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `tariff` int(4) NOT NULL COMMENT 'Identificador de la Tarifa',
  `attribute` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Atributo adicional',
  `value` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Valor del atributo adicional',
  `value_date` date NOT NULL COMMENT 'Fecha del valor del atributo',
  PRIMARY KEY  (`id`),
  KEY `IDX_TARIFF_ADDINFO_DOMAIN` (`domain`),
  KEY `IDX_TARIFF_ADDINFO_TARIFF` (`tariff`),
  CONSTRAINT `FK_TARIFF_ADDINFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TARIFF_ADDINFO_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Informacion adicional de la Tarifa';


ALTER TABLE `tariff` ADD `discount` double(6,2) default '0.00' COMMENT 'Descuento general de la Tarifa';

ALTER TABLE `project_reservation_room` ADD `rate_plan` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo de Tarifa en origen' AFTER `item`;

CREATE TABLE `allotment_tariff` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `allotment` int(4) NOT NULL COMMENT 'Identificador del Cupo de seguridad',
  `tariff` int(4) NOT NULL COMMENT 'Identificador de la Tarifa',
  PRIMARY KEY  (`id`),
  KEY `IDX_ALLOTMENT_TARIFF_DOMAIN` (`domain`),
  KEY `IDX_ALLOTMENT_TARIFF_ALLOTMENT` (`allotment`),
  KEY `IDX_ALLOTMENT_TARIFF_TARIFF` (`tariff`),
  CONSTRAINT `FK_ALLOTMENT_TARIFF_ALLOTMENT` FOREIGN KEY (`allotment`) REFERENCES `allotment` (`id`),
  CONSTRAINT `FK_ALLOTMENT_TARIFF_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ALLOTMENT_TARIFF_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas por Cupo';


UPDATE `db_version` SET `version_number` = '7.28.2';

COMMIT;
