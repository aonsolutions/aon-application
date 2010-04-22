# Database: aon_master
# Version: Actualizacion de la version 5.0.0 a la version 5.1.0.
# Created by: girazu
# Creation Date: 22/03/2010 11:07
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

UPDATE `invoice` SET `reference_code` = CONCAT(`series`, "/", LPAD(`number`, 6, '0')) WHERE `type` = 1 AND `number` < 999999 AND `series` IS NOT NULL AND TRIM(`series`) <> "";

UPDATE `invoice` SET `reference_code` = LPAD(`number`, 6, '0') WHERE `type` = 1 AND `number` < 999999 AND (`series` IS NULL OR TRIM(`series`) = "");

UPDATE `customer_fee` SET `security_level` = 0 WHERE `security_level` IS NULL;

UPDATE `invoice` SET `security_level` = 0 WHERE `security_level` IS NULL;

UPDATE `delivery` SET `security_level` = 0 WHERE `security_level` IS NULL;

UPDATE `income` SET `security_level` = 0 WHERE `security_level` IS NULL;

UPDATE `sales` SET `security_level` = 0 WHERE `security_level` IS NULL;

UPDATE `purchase` SET `security_level` = 0 WHERE `security_level` IS NULL;

UPDATE `offer` SET `security_level` = 0 WHERE `security_level` IS NULL;

UPDATE `finance` SET `security_level` = 0 WHERE `security_level` IS NULL;

UPDATE `series` SET `security_level` = 0 WHERE `security_level` IS NULL;

ALTER TABLE `offer` ADD `third_party` int(4) default NULL COMMENT 'Identificador del Cliente Potencial representado';

ALTER TABLE `offer` ADD KEY `IDX_OFFER_THIRD_PARTY` (`third_party`);

ALTER TABLE `offer` ADD CONSTRAINT `FK_OFFER_THIRD_PARTY` FOREIGN KEY (`third_party`) REFERENCES `target` (`registry`);

CREATE TABLE `target_third_party` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `third_party` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial representado',
  `target_external_code` varchar(15) collate latin1_spanish_ci default NULL COMMENT 'Codigo del Cliente Potencial para el representado',
  `tariff` int(4) default NULL COMMENT 'Identificador de Tarifa',
  `pay_method` int(4) default NULL COMMENT 'Identificador de la Forma de Pago',
  `number_of_pymnts` smallint(2) default '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint(2) default '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint(2) default '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) collate latin1_spanish_ci default '0' COMMENT 'Dias de pago',
  `bank` int(4) default NULL COMMENT 'Identificador de la Entidad Bancaria',
  `bank_account` varchar(30) collate latin1_spanish_ci default NULL COMMENT 'Numero de cuenta en la Entidad Bancaria',  PRIMARY KEY  (`id`),
  KEY `IDX_TARGET_THIRD_PARTY_TARGET` (`target`),
  KEY `IDX_TARGET_THIRD_PARTY_THIRD_PARTY` (`third_party`),
  KEY `IDX_TARGET_THIRD_PARTY_TARIFF` (`tariff`),
  KEY `IDX_TARGET_THIRD_PARTY_PAY_METHOD` (`pay_method`),
  KEY `IDX_TARGET_THIRD_PARTY_BANK` (`bank`),
  CONSTRAINT `FK_TARGET_THIRD_PARTY_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_TARGET_THIRD_PARTY_THIRD_PARTY` FOREIGN KEY (`third_party`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_TARGET_THIRD_PARTY_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`),
  CONSTRAINT `FK_TARGET_THIRD_PARTY_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_TARGET_THIRD_PARTY_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion de Clientes potenciales con representados';

ALTER TABLE `loan` ADD `status` tinyint(2) default '0' COMMENT 'Estado';

CREATE TABLE `commission` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Comision',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio de la Comision',
  `end_date` date default NULL COMMENT 'Fecha de fin de la Comision',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones';

CREATE TABLE `commission_category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `commission` int(4) NOT NULL COMMENT 'Identificador de la Comision',
  `category` int(4) NOT NULL COMMENT 'Identificador de la Categoria',
  `quantity` double default '0' COMMENT 'Cantidad a partir de la cual se aplica la Comision',
  `discount` double(6,2) default '0.00' COMMENT 'Porcentaje de Comision',
  PRIMARY KEY  (`id`),
  KEY `IDX_COMMISSION_CATEGORY_COMMISSION` (`commission`),
  KEY `IDX_COMMISSION_CATEGORY_CATEGORY` (`category`),
  CONSTRAINT `FK_COMMISSION_CATEGORY_COMMISSION` FOREIGN KEY (`commission`) REFERENCES `commission` (`id`),
  CONSTRAINT `FK_COMMISSION_CATEGORY_CATEGORY` FOREIGN KEY (`category`) REFERENCES `pcategory` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones por Categoria';

CREATE TABLE `commission_item` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `commission` int(4) NOT NULL COMMENT 'Identificador de la Comision',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo',
  `quantity` double default '0' COMMENT 'Cantidad a partir de la cual se aplica la Comision',
  `price` double default '0' COMMENT 'Importe de la Comision',
  `discount` double(6,2) default '0.00' COMMENT 'Porcentaje de Comision',
  PRIMARY KEY  (`id`),
  KEY `IDX_COMMISSION_ITEM_COMMISSION` (`commission`),
  KEY `IDX_COMMISSION_ITEM_ITEM` (`item`),
  CONSTRAINT `FK_COMMISSION_ITEM_COMMISSION` FOREIGN KEY (`commission`) REFERENCES `commission` (`id`),
  CONSTRAINT `FK_COMMISSION_ITEM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones por Articulo';


UPDATE `db_version` SET `version_number` = '5.1.0';

COMMIT;
