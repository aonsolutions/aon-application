# Database: aon_master
# Version: Actualizacion de la version 3.2.1 a la version 3.3.0.
# Created by: girazu
# Creation Date: 11/05/2009 13:33
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `invoice` ADD `tax_date` date default NULL COMMENT 'Fecha de Impuestos de la Factura' AFTER `issue_date`;

UPDATE `invoice` SET `tax_date` = `issue_date`;

ALTER TABLE `invoice` ADD KEY `idx_invc_tax_date` (`tax_date`);

ALTER TABLE `account_budget` DROP COLUMN `credit`;

ALTER TABLE `account_budget` DROP COLUMN `debit`;

ALTER TABLE `account_budget` DROP COLUMN `entry_date`;

CREATE TABLE `account_budget_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `account_budget` int(4) NOT NULL COMMENT 'Identificador del Presupuesto',
  `account_period` char(4) collate latin1_spanish_ci NOT NULL COMMENT 'Ejercicio Contable del Presupuesto',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta Contable del Presupuesto',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Presupuesto',
  `entry_date` date default NULL COMMENT 'Fecha del Presupuesto',
  `debit` double default '0' COMMENT 'Debe del Presupuesto',
  `credit` double default '0' COMMENT 'Haber del Presupuesto',
  PRIMARY KEY  (`id`),
  KEY `account_budget_account_idx` (`account`),
  KEY `account_budget_account_period_idx` (`account_period`),
  KEY `account_budget` (`account_budget`),
  CONSTRAINT `fk_account_budget_detail_account_budget` FOREIGN KEY (`account_budget`) REFERENCES `account_budget` (`id`),
  CONSTRAINT `fk_account_budget_detail_account_detail` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `fk_account_budget_detail_period_detail` FOREIGN KEY (`account_period`) REFERENCES `account_period` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Presupuesto de Cuentas Contables';


UPDATE `db_version` SET `version_number` = '3.3.0';

COMMIT;
