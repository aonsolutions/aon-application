# Database: aon_master
# Version: Actualizacion de la version 6.18.9 a la version 6.19.0.
# Created by: girazu
# Creation Date: 30/11/2011 19:05
# Comentarios: ACTUALIZACION DE IDS DE ACCOUNT, ACCOUNT_PERIOD Y APP_PARAM.


BEGIN;

ALTER TABLE `account_entry_detail` 
	DROP FOREIGN KEY `account_entry_detail_ibfk_2`,
	DROP FOREIGN KEY `account_entry_detail_ibfk_3`,
	DROP KEY `account_entry_detail_FKIndex4`,
	DROP KEY `account_entry_detail_FKIndex3`;

ALTER TABLE `account_helper` 
	DROP FOREIGN KEY `FK_ACCOUNT_HELPER_ACCOUNT`,
	DROP FOREIGN KEY `FK_ACCOUNT_HELPER_BALANCING_ACCOUNT`,
	DROP KEY `IDX_ACCOUNT_HELPER_ACCOUNT`,
	DROP KEY `IDX_ACCOUNT_HELPER_BALANCING_ACCOUNT`;

ALTER TABLE `account_summary` 
	DROP FOREIGN KEY `account_summary_ibfk_2`, 
	DROP KEY `account_summary_account_idx`;

ALTER TABLE `amortization_type` 
	DROP FOREIGN KEY `fk_amortization_type_account1`,
	DROP FOREIGN KEY `fk_amortization_type_account2`,
	DROP FOREIGN KEY `fk_amortization_type_account3`,
	DROP KEY `fixed_asset_account`,
	DROP KEY `allocation_account`,
	DROP KEY `accumulated_account`;

ALTER TABLE `amortization` 
	DROP FOREIGN KEY `FK_AMORTIZATION_ACCUMULATED_ACCOUNT`,
	DROP FOREIGN KEY `FK_AMORTIZATION_ALLOCATION_ACCOUNT`,
	DROP FOREIGN KEY `FK_AMORTIZATION_FIXED_ASSET_ACCOUNT`,
	DROP KEY `IDX_AMORTIZATION_FIXED_ASSET_ACCOUNT`,
	DROP KEY `IDX_AMORTIZATION_ALLOCATION_ACCOUNT`,
	DROP KEY `IDX_AMORTIZATION_ACCUMULATED_ACCOUNT`;

ALTER TABLE `bank_concept_account` 
	DROP FOREIGN KEY `FK_BANK_CONCEPT_ACCOUNT_ACCOUNT`,
	DROP KEY `IDX_BANK_CONCEPT_ACCOUNT_ACCOUNT`;

ALTER TABLE `creditor_account` 
	DROP FOREIGN KEY `creditor_account_ibfk_2`,
	DROP KEY `account`;

ALTER TABLE `customer_account` 
	DROP FOREIGN KEY `customer_account_ibfk_2`,
	DROP KEY `account`;

ALTER TABLE `invoice_detail_account` 
	DROP FOREIGN KEY `invoice_detail_account_ibfk_2`,
	DROP KEY `account`;

ALTER TABLE `invoice_tax_account` 
	DROP FOREIGN KEY `invoice_tax_account_ibfk_2`,
	DROP KEY `account`;

ALTER TABLE `leasing` 
	DROP FOREIGN KEY `leasing_fk1`,
	DROP KEY `fixed_asset_account`;

ALTER TABLE `leasing_account` 
	DROP FOREIGN KEY `leasing_account_fk1`,
	DROP KEY `account`;

ALTER TABLE `loan_account` 
	DROP FOREIGN KEY `loan_account_fk1`,
	DROP KEY `account`;

ALTER TABLE `pm_type_detail_account` 
	DROP FOREIGN KEY `FK_PM_TYPE_DETAIL_ACCOUNT_ACCOUNT`,
	DROP KEY `IDX_PM_TYPE_DETAIL_ACCOUNT_ACCOUNT`;

ALTER TABLE `product_account` 
	DROP FOREIGN KEY `product_account_ibfk_2`,
	DROP KEY `account`;

ALTER TABLE `rbank_account` 
	DROP FOREIGN KEY `rbank_account_ibfk_2`,
	DROP KEY `account`;

ALTER TABLE `supplier_account` 
	DROP FOREIGN KEY `supplier_account_ibfk_2`,
	DROP KEY `account`;

ALTER TABLE `tax_account` 
	DROP FOREIGN KEY `tax_account_ibfk_2`,
	DROP KEY `account`;

ALTER TABLE `account` DROP PRIMARY KEY;
ALTER TABLE `account` CHANGE `id` `code` int(4) NOT NULL COMMENT 'Codigo Cuenta Contable';
ALTER TABLE `account` ADD `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico' FIRST, ADD PRIMARY KEY (`id`);
ALTER TABLE `account` ADD UNIQUE KEY `IDX_ACCOUNT_CODE` (`code`);

ALTER TABLE `account_entry_detail` 
	MODIFY `account` int(4) NOT NULL COMMENT 'Cuenta Contable del Apunte',
	MODIFY `balancing_account` int(4) default NULL COMMENT 'Contrapartida del Apunte';
UPDATE `account_entry_detail` SET `account` = (SELECT `id` FROM `account` WHERE `code` = `account_entry_detail`.`account`);
UPDATE `account_entry_detail` SET `balancing_account` = (SELECT `id` FROM `account` WHERE `code` = `account_entry_detail`.`balancing_account`);
ALTER TABLE `account_entry_detail` 
	ADD KEY `IDX_ACCOUNT_ENTRY_DETAIL_ACCOUNT` (`account`),
	ADD CONSTRAINT `FK_ACCOUNT_ENTRY_DETAIL_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
	ADD KEY `IDX_ACCOUNT_ENTRY_DETAIL_BAL_ACCOUNT` (`balancing_account`),
	ADD CONSTRAINT `FK_ACCOUNT_ENTRY_DETAIL_BAL_ACCOUNT` FOREIGN KEY (`balancing_account`) REFERENCES `account` (`id`);

DELETE FROM `account_helper`;
ALTER TABLE `account_helper` 
	MODIFY `account` int(4) NOT NULL COMMENT 'Cuenta Contable',
	MODIFY `balancing_account` int(4) NOT NULL COMMENT 'Contrapartida';
INSERT INTO `account_helper` (`account`, `balancing_account`, `counter`) SELECT `account`, `balancing_account`, COUNT(*) FROM `account_entry_detail` WHERE `balancing_account` IS NOT NULL GROUP BY `account`, `balancing_account`;
ALTER TABLE `account_helper` 
	ADD KEY `IDX_ACCOUNT_HELPER_ACCOUNT` (`account`),
	ADD CONSTRAINT `FK_ACCOUNT_HELPER_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
	ADD KEY `IDX_ACCOUNT_HELPER_BAL_ACCOUNT` (`balancing_account`),
	ADD CONSTRAINT `FK_ACCOUNT_HELPER_BAL_ACCOUNT` FOREIGN KEY (`balancing_account`) REFERENCES `account` (`id`);

DELETE FROM `account_summary`;
ALTER TABLE `account_summary` 
	MODIFY `account` int(4) NOT NULL COMMENT 'Cuenta Contable del Acumulado',
	ADD KEY `IDX_ACCOUNT_SUMMARY_ACCOUNT` (`account`),
	ADD CONSTRAINT `FK_ACCOUNT_SUMMARY_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`);

ALTER TABLE `amortization_type` 
	MODIFY `fixed_asset_account` int(4) NOT NULL COMMENT 'Cuenta de inmovilizado',
	MODIFY `accumulated_account` int(4) NOT NULL COMMENT 'Cuenta de amortizacion acumulada',
	MODIFY `allocation_account` int(4) NOT NULL COMMENT 'Cuenta para la dotacion de la amortizacion';
UPDATE `amortization_type` SET `fixed_asset_account` = (SELECT `id` FROM `account` WHERE `code` = `amortization_type`.`fixed_asset_account`);
UPDATE `amortization_type` SET `accumulated_account` = (SELECT `id` FROM `account` WHERE `code` = `amortization_type`.`accumulated_account`);
UPDATE `amortization_type` SET `allocation_account` = (SELECT `id` FROM `account` WHERE `code` = `amortization_type`.`allocation_account`);
ALTER TABLE `amortization_type` 
	ADD KEY `IDX_AMORTIZATION_TYPE_FIX_ACCOUNT` (`fixed_asset_account`),
	ADD CONSTRAINT `FK_AMORTIZATION_TYPE_FIX_ACCOUNT` FOREIGN KEY (`fixed_asset_account`) REFERENCES `account` (`id`),
	ADD KEY `IDX_AMORTIZATION_TYPE_ACC_ACCOUNT` (`accumulated_account`),
	ADD CONSTRAINT `FK_AMORTIZATION_TYPE_ACC_ACCOUNT` FOREIGN KEY (`accumulated_account`) REFERENCES `account` (`id`),
	ADD KEY `IDX_AMORTIZATION_TYPE_ALL_ACCOUNT` (`allocation_account`),
	ADD CONSTRAINT `FK_AMORTIZATION_TYPE_ALL_ACCOUNT` FOREIGN KEY (`allocation_account`) REFERENCES `account` (`id`);

ALTER TABLE `amortization` 
	MODIFY `fixed_asset_account` int(4) NOT NULL COMMENT 'Cuenta de inmovilizado',
	MODIFY `accumulated_account` int(4) NOT NULL COMMENT 'Cuenta de Amortizacion acumulada',
	MODIFY `allocation_account` int(4) NOT NULL COMMENT 'Cuenta para la dotacion de la Amortizacion';
UPDATE `amortization` SET `fixed_asset_account` = (SELECT `id` FROM `account` WHERE `code` = `amortization`.`fixed_asset_account`);
UPDATE `amortization` SET `accumulated_account` = (SELECT `id` FROM `account` WHERE `code` = `amortization`.`accumulated_account`);
UPDATE `amortization` SET `allocation_account` = (SELECT `id` FROM `account` WHERE `code` = `amortization`.`allocation_account`);
ALTER TABLE `amortization` 
	ADD KEY `IDX_AMORTIZATION_FIX_ACCOUNT` (`fixed_asset_account`),
	ADD CONSTRAINT `FK_AMORTIZATION_FIX_ACCOUNT` FOREIGN KEY (`fixed_asset_account`) REFERENCES `account` (`id`),
	ADD KEY `IDX_AMORTIZATION_ACC_ACCOUNT` (`accumulated_account`),
	ADD CONSTRAINT `FK_AMORTIZATION_ACC_ACCOUNT` FOREIGN KEY (`accumulated_account`) REFERENCES `account` (`id`),
	ADD KEY `IDX_AMORTIZATION_ALL_ACCOUNT` (`allocation_account`),
	ADD CONSTRAINT `FK_AMORTIZATION_ALL_ACCOUNT` FOREIGN KEY (`allocation_account`) REFERENCES `account` (`id`);

ALTER TABLE `bank_concept_account` 
	MODIFY `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable';
UPDATE `bank_concept_account` SET `account` = (SELECT `id` FROM `account` WHERE `code` = `bank_concept_account`.`account`);
ALTER TABLE `bank_concept_account` 
	ADD KEY `IDX_BANK_CONCEPT_ACCOUNT_ACCOUNT` (`account`),
	ADD CONSTRAINT `FK_BANK_CONCEPT_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`);

ALTER TABLE `creditor_account` 
	MODIFY `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable';
UPDATE `creditor_account` SET `account` = (SELECT `id` FROM `account` WHERE `code` = `creditor_account`.`account`);
ALTER TABLE `creditor_account` 
	ADD KEY `IDX_CREDITOR_ACCOUNT_ACCOUNT` (`account`),
	ADD CONSTRAINT `FK_CREDITOR_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`);

ALTER TABLE `customer_account` 
	MODIFY `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable';
UPDATE `customer_account` SET `account` = (SELECT `id` FROM `account` WHERE `code` = `customer_account`.`account`);
ALTER TABLE `customer_account` 
	ADD KEY `IDX_CUSTOMER_ACCOUNT_ACCOUNT` (`account`),
	ADD CONSTRAINT `FK_CUSTOMER_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`);

ALTER TABLE `invoice_detail_account` 
	MODIFY `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable';
UPDATE `invoice_detail_account` SET `account` = (SELECT `id` FROM `account` WHERE `code` = `invoice_detail_account`.`account`);
ALTER TABLE `invoice_detail_account` 
	ADD KEY `IDX_INVOICE_DETAIL_ACCOUNT_ACCOUNT` (`account`),
	ADD CONSTRAINT `FK_INVOICE_DETAIL_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`);

ALTER TABLE `invoice_tax_account` 
	MODIFY `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable';
UPDATE `invoice_tax_account` SET `account` = (SELECT `id` FROM `account` WHERE `code` = `invoice_tax_account`.`account`);
ALTER TABLE `invoice_tax_account` 
	ADD KEY `IDX_INVOICE_TAX_ACCOUNT_ACCOUNT` (`account`),
	ADD CONSTRAINT `FK_INVOICE_TAX_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`);

ALTER TABLE `leasing` 
	MODIFY `fixed_asset_account` int(4) NOT NULL COMMENT 'Cuenta de inmobilizado';
UPDATE `leasing` SET `fixed_asset_account` = (SELECT `id` FROM `account` WHERE `code` = `leasing`.`fixed_asset_account`);
ALTER TABLE `leasing` 
	ADD KEY `IDX_LEASING_FIX_ACCOUNT` (`fixed_asset_account`),
	ADD CONSTRAINT `FK_LEASING_FIX_ACCOUNT` FOREIGN KEY (`fixed_asset_account`) REFERENCES `account` (`id`);

ALTER TABLE `leasing_account` 
	MODIFY `account` int(4) NOT NULL COMMENT 'Cuenta Contable';
UPDATE `leasing_account` SET `account` = (SELECT `id` FROM `account` WHERE `code` = `leasing_account`.`account`);
ALTER TABLE `leasing_account` 
	ADD KEY `IDX_LEASING_ACCOUNT_ACCOUNT` (`account`),
	ADD CONSTRAINT `FK_LEASING_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`);

ALTER TABLE `loan_account` 
	MODIFY `account` int(4) NOT NULL COMMENT 'Cuenta Contable';
UPDATE `loan_account` SET `account` = (SELECT `id` FROM `account` WHERE `code` = `loan_account`.`account`);
ALTER TABLE `loan_account` 
	ADD KEY `IDX_LOAN_ACCOUNT_ACCOUNT` (`account`),
	ADD CONSTRAINT `FK_LOAN_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`);

ALTER TABLE `pm_type_detail_account` 
	MODIFY `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable';
UPDATE `pm_type_detail_account` SET `account` = (SELECT `id` FROM `account` WHERE `code` = `pm_type_detail_account`.`account`);
ALTER TABLE `pm_type_detail_account` 
	ADD KEY `IDX_PM_TYPE_DETAIL_ACCOUNT_ACCOUNT` (`account`),
	ADD CONSTRAINT `FK_PM_TYPE_DETAIL_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`);

ALTER TABLE `product_account` 
	MODIFY `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable';
UPDATE `product_account` SET `account` = (SELECT `id` FROM `account` WHERE `code` = `product_account`.`account`);
ALTER TABLE `product_account` 
	ADD KEY `IDX_PRODUCT_ACCOUNT_ACCOUNT` (`account`),
	ADD CONSTRAINT `FK_PRODUCT_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`);

ALTER TABLE `rbank_account` 
	MODIFY `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable';
UPDATE `rbank_account` SET `account` = (SELECT `id` FROM `account` WHERE `code` = `rbank_account`.`account`);
ALTER TABLE `rbank_account` 
	ADD KEY `IDX_RBANK_ACCOUNT_ACCOUNT` (`account`),
	ADD CONSTRAINT `FK_RBANK_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`);

ALTER TABLE `supplier_account` 
	MODIFY `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable';
UPDATE `supplier_account` SET `account` = (SELECT `id` FROM `account` WHERE `code` = `supplier_account`.`account`);
ALTER TABLE `supplier_account` 
	ADD KEY `IDX_SUPPLIER_ACCOUNT_ACCOUNT` (`account`),
	ADD CONSTRAINT `FK_SUPPLIER_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`);

ALTER TABLE `tax_account` 
	MODIFY `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable';
UPDATE `tax_account` SET `account` = (SELECT `id` FROM `account` WHERE `code` = `tax_account`.`account`);
ALTER TABLE `tax_account` 
	ADD KEY `IDX_TAX_ACCOUNT_ACCOUNT` (`account`),
	ADD CONSTRAINT `FK_TAX_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`);


ALTER TABLE `account_entry` 
	DROP FOREIGN KEY `account_entry_ibfk_1`,
	DROP KEY `account_entry_account_period_idx`;

ALTER TABLE `account_summary` 
	DROP FOREIGN KEY `account_summary_ibfk_1`,
	DROP KEY `account_summary_account_period_idx`;

ALTER TABLE `account` DROP KEY `IDX_ACCOUNT_CODE`;
ALTER TABLE `account` MODIFY `code` char(12) NOT NULL COMMENT 'Codigo Cuenta Contable';
ALTER TABLE `account` ADD UNIQUE KEY `IDX_ACCOUNT_CODE` (`code`);

	
ALTER TABLE `account_period` DROP PRIMARY KEY;
ALTER TABLE `account_period` CHANGE `id` `name` char(16) NOT NULL COMMENT 'Nombre del periodo';
ALTER TABLE `account_period` ADD `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico' FIRST, ADD PRIMARY KEY (`id`);
ALTER TABLE `account_period` ADD UNIQUE KEY `IDX_ACCOUNT_PERIOD_NAME` (`name`) ;

ALTER TABLE `account_entry` 
	MODIFY `account_period` int(4) NOT NULL COMMENT 'Ejercicio Contable del Asiento';
UPDATE `account_entry` SET `account_period` = (SELECT `id` FROM `account_period` WHERE `name` = `account_entry`.`account_period`);
ALTER TABLE `account_entry` 
	ADD KEY `IDX_ACCOUNT_ENTRY_ACCOUNT_PERIOD` (`account_period`),
	ADD CONSTRAINT `FK_ACCOUNT_ENTRY_ACCOUNT_PERIOD` FOREIGN KEY (`account_period`) REFERENCES `account_period` (`id`);

ALTER TABLE `account_summary` 
	MODIFY `account_period` int(4) NOT NULL COMMENT 'Ejercicio Contable del Acumulado';  
UPDATE `account_summary` SET `account_period` = (SELECT `id` FROM `account_period` WHERE `name` = `account_summary`.`account_period`);
ALTER TABLE `account_summary` 
	ADD KEY `IDX_ACCOUNT_SUMMARY_ACCOUNT_PERIOD` (`account_period`),
	ADD CONSTRAINT `FK_ACCOUNT_SUMMARY_ACCOUNT_PERIOD` FOREIGN KEY (`account_period`) REFERENCES `account_period` (`id`);

ALTER TABLE `app_param` DROP PRIMARY KEY;
ALTER TABLE `app_param` ADD `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico' FIRST, ADD PRIMARY KEY (`id`);
ALTER TABLE `app_param` ADD UNIQUE KEY `IDX_APP_PARAM_NAME` (`name`) ;
UPDATE `app_param` set `value`=(SELECT `id` FROM `account_period` WHERE `account_period`.`name` = `app_param`.`value`) WHERE `app_param`.`name`="ACC_DEFAULT_PERIOD";
UPDATE `app_param` set `value`=(SELECT `id` FROM `account` WHERE `account`.`code` = `app_param`.`value`) WHERE `app_param`.`name`="ACC_DEFAULT_ALLOWANCE_ACC";
UPDATE `app_param` set `value`=(SELECT `id` FROM `account` WHERE `account`.`code` = `app_param`.`value`) WHERE `app_param`.`name`="ACC_DEFAULT_CASH_ACC";
UPDATE `app_param` set `value`=(SELECT `id` FROM `account` WHERE `account`.`code` = `app_param`.`value`) WHERE `app_param`.`name`="ACC_DEFAULT_CHARGED_RET_ACC";
UPDATE `app_param` set `value`=(SELECT `id` FROM `account` WHERE `account`.`code` = `app_param`.`value`) WHERE `app_param`.`name`="ACC_DEFAULT_CHARGED_VAT_ACC";
UPDATE `app_param` set `value`=(SELECT `id` FROM `account` WHERE `account`.`code` = `app_param`.`value`) WHERE `app_param`.`name`="ACC_DEFAULT_COMPANY_SOC_INS_ACC";
UPDATE `app_param` set `value`=(SELECT `id` FROM `account` WHERE `account`.`code` = `app_param`.`value`) WHERE `app_param`.`name`="ACC_DEFAULT_COMPENSATION_ACC";
UPDATE `app_param` set `value`=(SELECT `id` FROM `account` WHERE `account`.`code` = `app_param`.`value`) WHERE `app_param`.`name`="ACC_DEFAULT_DEBT_INTEREST_ACC";
UPDATE `app_param` set `value`=(SELECT `id` FROM `account` WHERE `account`.`code` = `app_param`.`value`) WHERE `app_param`.`name`="ACC_DEFAULT_FINAN_EXPENSES_ACC";
UPDATE `app_param` set `value`=(SELECT `id` FROM `account` WHERE `account`.`code` = `app_param`.`value`) WHERE `app_param`.`name`="ACC_DEFAULT_PAID_RET_ACC";
UPDATE `app_param` set `value`=(SELECT `id` FROM `account` WHERE `account`.`code` = `app_param`.`value`) WHERE `app_param`.`name`="ACC_DEFAULT_PAID_VAT_ACC";
UPDATE `app_param` set `value`=(SELECT `id` FROM `account` WHERE `account`.`code` = `app_param`.`value`) WHERE `app_param`.`name`="ACC_DEFAULT_PENDING_SALARY_ACC";
UPDATE `app_param` set `value`=(SELECT `id` FROM `account` WHERE `account`.`code` = `app_param`.`value`) WHERE `app_param`.`name`="ACC_DEFAULT_PURCHASE_ACC";
UPDATE `app_param` set `value`=(SELECT `id` FROM `account` WHERE `account`.`code` = `app_param`.`value`) WHERE `app_param`.`name`="ACC_DEFAULT_SALARY_ACC";
UPDATE `app_param` set `value`=(SELECT `id` FROM `account` WHERE `account`.`code` = `app_param`.`value`) WHERE `app_param`.`name`="ACC_DEFAULT_SALES_ACC";
UPDATE `app_param` set `value`=(SELECT `id` FROM `account` WHERE `account`.`code` = `app_param`.`value`) WHERE `app_param`.`name`="ACC_DEFAULT_SOCIAL_INSURANCE_ACC";
UPDATE `app_param` set `value`=(SELECT `id` FROM `account` WHERE `account`.`code` = `app_param`.`value`) WHERE `app_param`.`name`="ACC_SALARY_CHARGED_RET_ACC";

ALTER TABLE `balance_detail` MODIFY `internal_calculation` tinyint(1) NOT NULL default '0' COMMENT 'Indica si es un calculo interno, es decir si el contenido de accounts son referencias a la columna -code- de esta tabla';


UPDATE `db_version` SET `version_number` = '6.19.0';

COMMIT;
