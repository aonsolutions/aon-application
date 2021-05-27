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

DROP TABLE `agreement_level_payment`;

CREATE TABLE `agreement_payment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `agreement` int(4) NOT NULL COMMENT 'Convenio',
  `payment_concept` int(4) default NULL COMMENT 'Identificador unico del concepto',
  `type` tinyint(2) default NULL COMMENT 'Tipo de complemento Salarial',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Script',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `month` tinyint(2) default NULL COMMENT 'Mes de la percepcion',
  `salary_type` tinyint(2) default NULL COMMENT 'Tipo de Nomina/Recibo',
  `description_decorable` tinyint(2) NOT NULL default '0',
  `irpf_expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe tributable',
  `quote_expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe cotizable',
  PRIMARY KEY  (`id`),
  KEY `IDX_PAYMENT_AGREEMENT` (`agreement`),
  KEY `IDX_AGREEMENT_PAYMENT_PAYMENT_CONCEPT` (`payment_concept`),
  CONSTRAINT `FK_AGREEMENT_PAYMENT_PAYMENT_CONCEPT` FOREIGN KEY (`payment_concept`) REFERENCES `payment_concept` (`id`),
  CONSTRAINT `FK_PAYMENT_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Percepciones';


CREATE TABLE `agreement_extra` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `agreement` int(4) NOT NULL COMMENT 'Convenio',
  `agreement_payment` int(4) default NULL COMMENT 'Concepto',
  `start_date` varchar(32) collate latin1_spanish_ci  NOT NULL COMMENT 'Fecha de inicio dd mm [year offset]',
  `end_date` varchar(32) collate latin1_spanish_ci  NOT NULL COMMENT 'Fecha de finalizacion dd mm [year offset]',
  `issue_date` varchar(32) collate latin1_spanish_ci  NOT NULL COMMENT 'Fecha de emision dd mm',
  PRIMARY KEY  (`id`),
  KEY `IDX_AGREEMENT_EXTRA_AGREEMENT` (`agreement`),
  CONSTRAINT `FK_AGREEMENT_EXTRA_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`),
  KEY `IDX_AGREEMENT_EXTRA_AGREEMENT_PAYMENT` (`agreement_payment`),
  CONSTRAINT `FK_AGREEMENT_EXTRA_AGREEMENT_PAYMENT` FOREIGN KEY (`agreement_payment`) REFERENCES `agreement_payment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Pagas extras';



UPDATE `db_version` SET `version_number` = '6.5.0';

COMMIT;
