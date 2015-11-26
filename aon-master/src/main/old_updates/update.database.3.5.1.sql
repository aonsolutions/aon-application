# Database: aon_master
# Version: Actualizacion de la version 3.5.1 a la version 4.0.0.
# Created by: girazu
# Creation Date: 14/07/2009 09:53
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `invoice_detail` MODIFY `description` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Detalle de Factura';

ALTER TABLE `invoice_tax` MODIFY `percentage` double(15,3) default '0.000' COMMENT 'Porcentaje de Impuesto del Detalle de la Factura';

ALTER TABLE `invoice_tax` ADD `quota` double default '0' COMMENT 'Cuota de Impuesto del Detalle de la Factura';

ALTER TABLE `invoice_tax` ADD `surcharge_quota` double default '0' COMMENT 'Cuota de recargo de equivalencia del Detalle de la Factura';

ALTER TABLE `iattach` ADD `type` tinyint(2) default NULL COMMENT 'Tipo de Archivo Adjunto';

ALTER TABLE `iattach` MODIFY `data` mediumblob COMMENT 'Archivo Adjunto en binario';

ALTER TABLE amortization ADD `fixed_asset_account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta de inmovilizado';

ALTER TABLE amortization ADD `accumulated_account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta de Amortizacion acumulada';

ALTER TABLE amortization ADD `allocation_account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta para la dotacion de la Amortizacion';

ALTER TABLE amortization ADD `percentage` double default '0' COMMENT 'Porcentaje de Amortizacion';

ALTER TABLE amortization ADD KEY `IDX_AMORTIZATION_FIXED_ASSET_ACCOUNT` (`fixed_asset_account`);

ALTER TABLE amortization ADD KEY `IDX_AMORTIZATION_ALLOCATION_ACCOUNT` (`allocation_account`);

ALTER TABLE amortization ADD KEY `IDX_AMORTIZATION_ACCUMULATED_ACCOUNT` (`accumulated_account`);

ALTER TABLE amortization ADD CONSTRAINT `FK_AMORTIZATION_FIXED_ASSET_ACCOUNT` FOREIGN KEY (`fixed_asset_account`) REFERENCES `account` (`id`);

ALTER TABLE amortization ADD CONSTRAINT `FK_AMORTIZATION_ALLOCATION_ACCOUNT` FOREIGN KEY (`accumulated_account`) REFERENCES `account` (`id`);

ALTER TABLE amortization ADD CONSTRAINT `FK_AMORTIZATION_ACCUMULATED_ACCOUNT` FOREIGN KEY (`allocation_account`) REFERENCES `account` (`id`);

ALTER TABLE amortization_detail ADD `fiscal_allocation` double(15,3) default '0.000' COMMENT 'Dotacion fiscal';

CREATE TABLE `account_helper` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `counter` int(4) NOT NULL default '0' COMMENT 'Contador, veces que se ha usado',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta contable',
  `balancing_account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Contrapartida',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACCOUNT_HELPER_ACCOUNT` (`account`),
  KEY `IDX_ACCOUNT_HELPER_BALANCING_ACCOUNT` (`balancing_account`),
  CONSTRAINT `FK_ACCOUNT_HELPER_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_ACCOUNT_HELPER_BALANCING_ACCOUNT` FOREIGN KEY (`balancing_account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ayuda a la introduccion de apuntes';

ALTER TABLE `customer` ADD `e_invoice` tinyint(1) default '0' COMMENT 'Indica si el Cliente desea recibir Facturas electronicas';

ALTER TABLE `company` ADD `e_invoice` tinyint(1) default '0' COMMENT 'Indica si la Compañia desea emitir Facturas electronicas';

ALTER TABLE `invoice` ADD `signed` tinyint(1) default '0' COMMENT 'Indica si la Factura esta firmada electronicamente';


UPDATE `db_version` SET `version_number` = '4.0.0';

COMMIT;
