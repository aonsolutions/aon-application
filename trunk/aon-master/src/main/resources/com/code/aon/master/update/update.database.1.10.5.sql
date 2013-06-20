# Database: aon_master
# Version: Actualizacion de la version 1.10.5 a la version 3.0.0. Compatible con AON-3.
# Created by: girazu
# Creation Date: 15/01/2009 13:28
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



CREATE TABLE `catalogue` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Catalogo',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio del Catalogo',
  `end_date` date default NULL COMMENT 'Fecha de fin del Catalogo',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Catalogos';

CREATE TABLE `catalogue_category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `catalogue` int(4) NOT NULL COMMENT 'Identificador del Catalogo',
  `category` int(4) NOT NULL COMMENT 'Identificador de la Categoria',
  `quantity` double default '0' COMMENT 'Cantidad a partir de la cual se aplica el descuento',
  `discount` double(6,2) default '0.00' COMMENT 'Descuento de la Categoria en el Catalogo',
  PRIMARY KEY  (`id`),
  KEY `catalogue` (`catalogue`),
  KEY `category` (`category`),
  CONSTRAINT `catalogue_category_fk2` FOREIGN KEY (`category`) REFERENCES `pcategory` (`id`),
  CONSTRAINT `catalogue_category_fk1` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias del Catalogo';

CREATE TABLE `catalogue_item` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `catalogue` int(4) NOT NULL COMMENT 'Identificador del Catalogo',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo',
  `quantity` double default '0' COMMENT 'Cantidad a partir de la cual se aplica el precio o descuento',
  `price` double default '0' COMMENT 'Precio del Articulo en el Catalogo',
  `discount` double(6,2) default '0.00' COMMENT 'Descuento del Articulo en el Catalogo',
  PRIMARY KEY  (`id`),
  KEY `catalogue` (`catalogue`),
  KEY `item` (`item`),
  CONSTRAINT `catalogue_item_fk1` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`),
  CONSTRAINT `catalogue_item_fk2` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulos del Catalogo';

CREATE TABLE `tariff_catalogue` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `tariff` int(4) NOT NULL COMMENT 'Identificador de la Tarifa',
  `catalogue` int(4) NOT NULL COMMENT 'Identificador del Catalogo',
  PRIMARY KEY  (`id`),
  KEY `tariff` (`tariff`),
  KEY `catalogue` (`catalogue`),
  CONSTRAINT `tariff_catalogue_fk2` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`),
  CONSTRAINT `tariff_catalogue_fk1` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas por Catalogo';

ALTER TABLE `seller` MODIFY `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Agente Comercial';

ALTER TABLE `seller` ADD `status` tinyint(2) default '0' COMMENT 'Estado del Agente Comercial';

ALTER TABLE `invoice` ADD `investment` tinyint(1) default '0' COMMENT 'Indica si la Factura es una inversion';

ALTER TABLE `invoice` ADD `transaction` tinyint(2) default '0' COMMENT 'Tipo de transaccion';

ALTER TABLE `account` ADD `level` int NOT NULL DEFAULT '0' COMMENT 'Nivel de la Cuenta';

UPDATE `account` SET `level` = LENGTH(`id`);

UPDATE `account` SET `level` = 4 WHERE `level` = 5;

UPDATE `account` SET `level` = 5 WHERE `level` > 5;

CREATE TABLE `account_budget` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `account_period` char(4) collate latin1_spanish_ci NOT NULL COMMENT 'Ejercicio Contable del Presupuesto',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta Contable del Presupuesto',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Presupuesto',
  `entry_date` date default NULL COMMENT 'Fecha del Presupuesto',
  `debit` double default '0' COMMENT 'Debe del Presupuesto',
  `credit` double default '0' COMMENT 'Haber del Presupuesto',
  PRIMARY KEY  (`id`),
  KEY `account_budget_account_idx` (`account`),
  KEY `account_budget_account_period_idx` (`account_period`),
  CONSTRAINT `fk_account_budget_account` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `fk_account_budget_period` FOREIGN KEY (`account_period`) REFERENCES `account_period` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Presupuesto de Cuentas Contables';

CREATE TABLE `amortization_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `fixed_asset_account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta de inmovilizado',
  `accumulated_account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta de amortizacion acumulada',
  `allocation_account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta para la dotacion de la amortizacion',
  `percentage` double default '0' COMMENT 'Porcentaje de amortizacion',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Amortizacion',
  PRIMARY KEY  (`id`),
  KEY `fixed_asset_account` (`fixed_asset_account`),
  KEY `allocation_account` (`allocation_account`),
  KEY `accumulated_account` (`accumulated_account`),
  CONSTRAINT `fk_amortization_type_account3` FOREIGN KEY (`allocation_account`) REFERENCES `account` (`id`),
  CONSTRAINT `fk_amortization_type_account1` FOREIGN KEY (`fixed_asset_account`) REFERENCES `account` (`id`),
  CONSTRAINT `fk_amortization_type_account2` FOREIGN KEY (`accumulated_account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Amortizacion';

CREATE TABLE `amortization` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del inmovilizado',
  `amortization_type` int(4) NOT NULL COMMENT 'Tipo de Amortizacion',
  `initial_date` date NOT NULL COMMENT 'Fecha de inicio de la Amortizacion',
  `deadline` date default NULL COMMENT 'Fecha de baja de la Amortizacion',
  `amount` double NOT NULL default '0' COMMENT 'Importe a amortizar.',
  `fee_period` tinyint(2) NOT NULL default '0' COMMENT 'Periodo de las cuotas de Amortizacion',
  `sale_amount` double default NULL COMMENT 'Importe de la venta',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios',
  PRIMARY KEY  (`id`),
  KEY `amortization_type` (`amortization_type`),
  CONSTRAINT `fk_amortization_amortization_type` FOREIGN KEY (`amortization_type`) REFERENCES `amortization_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Fichas de Amortizacion Contables';

CREATE TABLE `amortization_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `amortization` int(4) NOT NULL COMMENT 'Ficha de Amortizacion',
  `from_date` date NOT NULL COMMENT 'Desde fecha',
  `to_date` date NOT NULL COMMENT 'Hasta fecha',
  `coefficient` double(15,3) NOT NULL COMMENT 'Coeficiente de Amortizacion',
  `allocation` double(15,3) NOT NULL COMMENT 'Dotacion de la Amortizacion',
  `status` tinyint(2) NOT NULL default '0' COMMENT 'Estatus del Detalle de Amortizacion',
  `account_entry` int(4) default NULL COMMENT 'Posicion del Apunte Contable',
  PRIMARY KEY  (`id`),
  KEY `amortization` (`amortization`),
  KEY `account_entry` (`account_entry`),
  CONSTRAINT `fk_amortization_detail_account_entry` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `fk_amortization_detail_amortization` FOREIGN KEY (`amortization`) REFERENCES `amortization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de la Ficha de Amortizacion Contable';

ALTER TABLE `offer` ADD `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Presupuesto' AFTER `pay_method`;

ALTER TABLE `offer` ADD `address` int(4) default NULL COMMENT 'Identificador de la Direccion de envio del Presupuesto' AFTER `number`;

ALTER TABLE `offer` ADD `tariff` int(4) default NULL COMMENT 'Identificador de la Tarifa del Presupuesto' AFTER `address`;

ALTER TABLE `offer` ADD KEY `tariff` (`tariff`);

ALTER TABLE `offer` ADD CONSTRAINT `offer_ibfk_5` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`);

ALTER TABLE `sales` MODIFY `pos` int(4) default NULL COMMENT 'Identificador del Centro de Venta que realizo el Pedido';

UPDATE `sales_detail` SET `discount_expr` = '0' WHERE `discount_expr` IS NULL;

ALTER TABLE `sales_detail` MODIFY `discount_expr` varchar(32) collate latin1_spanish_ci default '0' COMMENT 'Descuentos del Detalle de Pedido';

ALTER TABLE `company` ADD `withholding` tinyint(1) default '0' COMMENT 'Indica si la Compañia aplica retencion de impuestos';


UPDATE `db_version` SET `version_number` = '3.0.0';

COMMIT;
