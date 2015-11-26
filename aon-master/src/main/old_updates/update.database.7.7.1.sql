# Database: aon_master
# Version: Actualizacion de la version 7.7.1 a la version 7.8.0.
# Created by: girazu
# Creation Date: 10/01/2013 13:20
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `invoice` DROP `taxFree`;

ALTER TABLE `product` ADD `sales_account` int(4) default NULL COMMENT 'Identificador de la Cuenta Contable de Ventas';
ALTER TABLE `product` ADD KEY `IDX_PRODUCT_ACCOUNT_SALES` (`sales_account`);
ALTER TABLE `product` ADD CONSTRAINT `FK_PRODUCT_ACCOUNT_SALES` FOREIGN KEY (`sales_account`) REFERENCES `account` (`id`);
ALTER TABLE `product` ADD `purchase_account` int(4) default NULL COMMENT 'Identificador de la Cuenta Contable de Compras';
ALTER TABLE `product` ADD KEY `IDX_PRODUCT_ACCOUNT_PURCHASE` (`purchase_account`);
ALTER TABLE `product` ADD CONSTRAINT `FK_PRODUCT_ACCOUNT_PURCHASE` FOREIGN KEY (`purchase_account`) REFERENCES `account` (`id`);
UPDATE `product` SET `sales_account` = (SELECT MAX(`account`) FROM `product_account` WHERE `product` = `product`.`id` AND `type` = 0);
UPDATE `product` SET `purchase_account` = (SELECT MAX(`account`) FROM `product_account` WHERE `product` = `product`.`id` AND `type` = 1);

ALTER TABLE `tax` ADD `sales_account` int(4) default NULL COMMENT 'Identificador de la Cuenta Contable de Ventas';
ALTER TABLE `tax` ADD KEY `IDX_TAX_ACCOUNT_SALES` (`sales_account`);
ALTER TABLE `tax` ADD CONSTRAINT `FK_TAX_ACCOUNT_SALES` FOREIGN KEY (`sales_account`) REFERENCES `account` (`id`);
ALTER TABLE `tax` ADD `purchase_account` int(4) default NULL COMMENT 'Identificador de la Cuenta Contable de Compras';
ALTER TABLE `tax` ADD KEY `IDX_TAX_ACCOUNT_PURCHASE` (`purchase_account`);
ALTER TABLE `tax` ADD CONSTRAINT `FK_TAX_ACCOUNT_PURCHASE` FOREIGN KEY (`purchase_account`) REFERENCES `account` (`id`);
UPDATE `tax` SET `sales_account` = (SELECT MAX(`account`) FROM `tax_account` WHERE `tax` = `tax`.`id` AND `type` = 0);
UPDATE `tax` SET `purchase_account` = (SELECT MAX(`account`) FROM `tax_account` WHERE `tax` = `tax`.`id` AND `type` = 1);

ALTER TABLE `bank_concept` ADD `account` int(4) default NULL COMMENT 'Identificador de la Cuenta Contable';
ALTER TABLE `bank_concept` ADD KEY `IDX_BANK_CONCEPT_ACCOUNT` (`account`);
ALTER TABLE `bank_concept` ADD CONSTRAINT `FK_BANK_CONCEPT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`);
UPDATE `bank_concept` SET `account` = (SELECT MAX(`account`) FROM `bank_concept_account` WHERE `bank_concept` = `bank_concept`.`id`);

DROP TABLE `product_account`;
DROP TABLE `tax_account`;
DROP TABLE `bank_concept_account`;

ALTER TABLE `salary_payment` MODIFY `payment_concept` varchar(15) collate latin1_spanish_ci default NULL COMMENT 'Codigo del concepto';
ALTER TABLE `salary_deduction` MODIFY `deduction_concept` varchar(15) collate latin1_spanish_ci default NULL COMMENT 'Codigo del concepto'; 


UPDATE `db_version` SET `version_number` = '7.8.0';

COMMIT;
