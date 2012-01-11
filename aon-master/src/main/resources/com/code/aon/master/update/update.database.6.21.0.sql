# Database: aon_master
# Version: Actualizacion de la version 6.21.0 a la version 6.21.1.
# Created by: girazu
# Creation Date: 09/01/2012 13:30
# Comentarios: CREACION DE LA TABLA DOMAIN Y EL CAMPO DOMAIN EN TODAS LAS TABLAS.


BEGIN;

CREATE TABLE `domain` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Dominio',
  `parent` int(4) default NULL COMMENT 'Identificador del Dominio padre',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indica si el Dominio esta activo o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_DOMAIN_PARENT` (`parent`),
  CONSTRAINT `FK_DOMAIN_PARENT` FOREIGN KEY (`parent`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Dominios';

INSERT IGNORE INTO `domain` VALUES (1, 'DOMINIO PRINCIPAL', NULL, 1);
UPDATE `domain` SET `name` = (SELECT `name` FROM `registry` WHERE `id` IN (SELECT `registry` FROM `company`));

ALTER TABLE `account` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `account` ADD KEY `IDX_ACCOUNT_DOMAIN` (`domain`);
ALTER TABLE `account` ADD CONSTRAINT `FK_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `account` DROP KEY `IDX_ACCOUNT_CODE`;
ALTER TABLE `account` ADD UNIQUE KEY `IDX_UNQ_ACCOUNT_DOMAIN_CODE` (`domain`,`code`);

ALTER TABLE `account_entry` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `account_entry` ADD KEY `IDX_ACCOUNT_ENTRY_DOMAIN` (`domain`);
ALTER TABLE `account_entry` ADD CONSTRAINT `FK_ACCOUNT_ENTRY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `account_entry_bank_statement` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `account_entry_bank_statement` ADD KEY `IDX_ACCOUNT_ENTRY_BANK_STATEMENT_DOMAIN` (`domain`);
ALTER TABLE `account_entry_bank_statement` ADD CONSTRAINT `FK_ACCOUNT_ENTRY_BANK_STATEMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `account_entry_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `account_entry_detail` ADD KEY `IDX_ACCOUNT_ENTRY_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `account_entry_detail` ADD CONSTRAINT `FK_ACCOUNT_ENTRY_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `account_entry_fbatch` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `account_entry_fbatch` ADD KEY `IDX_ACCOUNT_ENTRY_FBATCH_DOMAIN` (`domain`);
ALTER TABLE `account_entry_fbatch` ADD CONSTRAINT `FK_ACCOUNT_ENTRY_FBATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `account_entry_finance_tracking` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `account_entry_finance_tracking` ADD KEY `IDX_ACCOUNT_ENTRY_FINANCE_TRACKING_DOMAIN` (`domain`);
ALTER TABLE `account_entry_finance_tracking` ADD CONSTRAINT `FK_ACCOUNT_ENTRY_FINANCE_TRACKING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `account_entry_invoice` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `account_entry_invoice` ADD KEY `IDX_ACCOUNT_ENTRY_INVOICE_DOMAIN` (`domain`);
ALTER TABLE `account_entry_invoice` ADD CONSTRAINT `FK_ACCOUNT_ENTRY_INVOICE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `account_helper` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `account_helper` ADD KEY `IDX_ACCOUNT_HELPER_DOMAIN` (`domain`);
ALTER TABLE `account_helper` ADD CONSTRAINT `FK_ACCOUNT_HELPER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `account_period` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `account_period` ADD KEY `IDX_ACCOUNT_PERIOD_DOMAIN` (`domain`);
ALTER TABLE `account_period` ADD CONSTRAINT `FK_ACCOUNT_PERIOD_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `account_period` DROP KEY `IDX_ACCOUNT_PERIOD_NAME`;
ALTER TABLE `account_period` ADD UNIQUE KEY `IDX_UNQ_ACCOUNT_PERIOD_DOMAIN_NAME` (`domain`,`name`);

ALTER TABLE `action` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `action` ADD KEY `IDX_ACTION_DOMAIN` (`domain`);
ALTER TABLE `action` ADD CONSTRAINT `FK_ACTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `action` DROP KEY `IDX_ACTION`;
ALTER TABLE `action` ADD KEY `IDX_ACTION_NAME_APPLICATION` (`name`,`application_id`);

ALTER TABLE `action_denied` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `action_denied` ADD KEY `IDX_ACTION_DENIED_DOMAIN` (`domain`);
ALTER TABLE `action_denied` ADD CONSTRAINT `FK_ACTION_DENIED_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `action_entry` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `action_entry` ADD KEY `IDX_ACTION_ENTRY_DOMAIN` (`domain`);
ALTER TABLE `action_entry` ADD CONSTRAINT `FK_ACTION_ENTRY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `action_favorite` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `action_favorite` ADD KEY `IDX_ACTION_FAVORITE_DOMAIN` (`domain`);
ALTER TABLE `action_favorite` ADD CONSTRAINT `FK_ACTION_FAVORITE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `activity_type` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `activity_type` ADD KEY `IDX_ACTIVITY_TYPE_DOMAIN` (`domain`);
ALTER TABLE `activity_type` ADD CONSTRAINT `FK_ACTIVITY_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `agreement` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `agreement` ADD KEY `IDX_AGREEMENT_DOMAIN` (`domain`);
ALTER TABLE `agreement` ADD CONSTRAINT `FK_AGREEMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `agreement_data` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `agreement_data` ADD KEY `IDX_AGREEMENT_DATA_DOMAIN` (`domain`);
ALTER TABLE `agreement_data` ADD CONSTRAINT `FK_AGREEMENT_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `agreement_extra` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `agreement_extra` ADD KEY `IDX_AGREEMENT_EXTRA_DOMAIN` (`domain`);
ALTER TABLE `agreement_extra` ADD CONSTRAINT `FK_AGREEMENT_EXTRA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `agreement_level` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `agreement_level` ADD KEY `IDX_AGREEMENT_LEVEL_DOMAIN` (`domain`);
ALTER TABLE `agreement_level` ADD CONSTRAINT `FK_AGREEMENT_LEVEL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `agreement_level_category` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `agreement_level_category` ADD KEY `IDX_AGREEMENT_LEVEL_CATEGORY_DOMAIN` (`domain`);
ALTER TABLE `agreement_level_category` ADD CONSTRAINT `FK_AGREEMENT_LEVEL_CATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `agreement_level_data` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `agreement_level_data` ADD KEY `IDX_AGREEMENT_LEVEL_DATA_DOMAIN` (`domain`);
ALTER TABLE `agreement_level_data` ADD CONSTRAINT `FK_AGREEMENT_LEVEL_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `agreement_payment` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `agreement_payment` ADD KEY `IDX_AGREEMENT_PAYMENT_DOMAIN` (`domain`);
ALTER TABLE `agreement_payment` ADD CONSTRAINT `FK_AGREEMENT_PAYMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `alarm` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `alarm` ADD KEY `IDX_ALARM_DOMAIN` (`domain`);
ALTER TABLE `alarm` ADD CONSTRAINT `FK_ALARM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `amortization` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `amortization` ADD KEY `IDX_AMORTIZATION_DOMAIN` (`domain`);
ALTER TABLE `amortization` ADD CONSTRAINT `FK_AMORTIZATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `amortization_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `amortization_detail` ADD KEY `IDX_AMORTIZATION_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `amortization_detail` ADD CONSTRAINT `FK_AMORTIZATION_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `amortization_type` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `amortization_type` ADD KEY `IDX_AMORTIZATION_TYPE_DOMAIN` (`domain`);
ALTER TABLE `amortization_type` ADD CONSTRAINT `FK_AMORTIZATION_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `app_param` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `app_param` ADD KEY `IDX_APP_PARAM_DOMAIN` (`domain`);
ALTER TABLE `app_param` ADD CONSTRAINT `FK_APP_PARAM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `app_param` DROP KEY `IDX_APP_PARAM_NAME`;
ALTER TABLE `app_param` ADD UNIQUE KEY `IDX_APP_PARAM_DOMAIN_NAME` (`domain`,`name`);

ALTER TABLE `application` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `application` ADD KEY `IDX_APPLICATION_DOMAIN` (`domain`);
ALTER TABLE `application` ADD CONSTRAINT `FK_APPLICATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `application` DROP KEY `name`;
ALTER TABLE `application` ADD UNIQUE KEY `IDX_UNQ_APPLICATION_DOMAIN_NAME` (`domain`,`name`);

ALTER TABLE `asset` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `asset` ADD KEY `IDX_ASSET_DOMAIN` (`domain`);
ALTER TABLE `asset` ADD CONSTRAINT `FK_ASSET_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `asset_activity` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `asset_activity` ADD KEY `IDX_ASSET_ACTIVITY_DOMAIN` (`domain`);
ALTER TABLE `asset_activity` ADD CONSTRAINT `FK_ASSET_ACTIVITY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `asset_feature` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `asset_feature` ADD KEY `IDX_ASSET_FEATURE_DOMAIN` (`domain`);
ALTER TABLE `asset_feature` ADD CONSTRAINT `FK_ASSET_FEATURE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `auto_concept` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `auto_concept` ADD KEY `IDX_AUTO_CONCEPT_DOMAIN` (`domain`);
ALTER TABLE `auto_concept` ADD CONSTRAINT `FK_AUTO_CONCEPT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `balance` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `balance` ADD KEY `IDX_BALANCE_DOMAIN` (`domain`);
ALTER TABLE `balance` ADD CONSTRAINT `FK_BALANCE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `balance_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `balance_detail` ADD KEY `IDX_BALANCE_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `balance_detail` ADD CONSTRAINT `FK_BALANCE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `bank` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `bank` ADD KEY `IDX_BANK_DOMAIN` (`domain`);
ALTER TABLE `bank` ADD CONSTRAINT `FK_BANK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `bank_concept` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `bank_concept` ADD KEY `IDX_BANK_CONCEPT_DOMAIN` (`domain`);
ALTER TABLE `bank_concept` ADD CONSTRAINT `FK_BANK_CONCEPT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `bank_concept_account` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `bank_concept_account` ADD KEY `IDX_BANK_CONCEPT_ACCOUNT_DOMAIN` (`domain`);
ALTER TABLE `bank_concept_account` ADD CONSTRAINT `FK_BANK_CONCEPT_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `bank_statement` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `bank_statement` ADD KEY `IDX_BANK_STATEMENT_DOMAIN` (`domain`);
ALTER TABLE `bank_statement` ADD CONSTRAINT `FK_BANK_STATEMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `bank_statement_link` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `bank_statement_link` ADD KEY `IDX_BANK_STATEMENT_LINK_DOMAIN` (`domain`);
ALTER TABLE `bank_statement_link` ADD CONSTRAINT `FK_BANK_STATEMENT_LINK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `bonus_concept` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `bonus_concept` ADD KEY `IDX_BONUS_CONCEPT_DOMAIN` (`domain`);
ALTER TABLE `bonus_concept` ADD CONSTRAINT `FK_BONUS_CONCEPT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `brand` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `brand` ADD KEY `IDX_BRAND_DOMAIN` (`domain`);
ALTER TABLE `brand` ADD CONSTRAINT `FK_BRAND_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `calendar` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `calendar` ADD KEY `IDX_CALENDAR_DOMAIN` (`domain`);
ALTER TABLE `calendar` ADD CONSTRAINT `FK_CALENDAR_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `calendar_holiday` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `calendar_holiday` ADD KEY `IDX_CALENDAR_HOLIDAY_DOMAIN` (`domain`);
ALTER TABLE `calendar_holiday` ADD CONSTRAINT `FK_CALENDAR_HOLIDAY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `calendar_period` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `calendar_period` ADD KEY `IDX_CALENDAR_PERIOD_DOMAIN` (`domain`);
ALTER TABLE `calendar_period` ADD CONSTRAINT `FK_CALENDAR_PERIOD_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `campaign` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `campaign` ADD KEY `IDX_CAMPAIGN_DOMAIN` (`domain`);
ALTER TABLE `campaign` ADD CONSTRAINT `FK_CAMPAIGN_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `campaign_project` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `campaign_project` ADD KEY `IDX_CAMPAIGN_PROJECT_DOMAIN` (`domain`);
ALTER TABLE `campaign_project` ADD CONSTRAINT `FK_CAMPAIGN_PROJECT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `campaign_type` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `campaign_type` ADD KEY `IDX_CAMPAIGN_TYPE_DOMAIN` (`domain`);
ALTER TABLE `campaign_type` ADD CONSTRAINT `FK_CAMPAIGN_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `cashflow_forecast` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `cashflow_forecast` ADD KEY `IDX_CASHFLOW_FORECAST_DOMAIN` (`domain`);
ALTER TABLE `cashflow_forecast` ADD CONSTRAINT `FK_CASHFLOW_FORECAST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `catalogue` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `catalogue` ADD KEY `IDX_CATALOGUE_DOMAIN` (`domain`);
ALTER TABLE `catalogue` ADD CONSTRAINT `FK_CATALOGUE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `catalogue_category` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `catalogue_category` ADD KEY `IDX_CATALOGUE_CATEGORY_DOMAIN` (`domain`);
ALTER TABLE `catalogue_category` ADD CONSTRAINT `FK_CATALOGUE_CATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `catalogue_item` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `catalogue_item` ADD KEY `IDX_CATALOGUE_ITEM_DOMAIN` (`domain`);
ALTER TABLE `catalogue_item` ADD CONSTRAINT `FK_CATALOGUE_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `category` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `category` ADD KEY `IDX_CATEGORY_DOMAIN` (`domain`);
ALTER TABLE `category` ADD CONSTRAINT `FK_CATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `certifica2_batch` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `certifica2_batch` ADD KEY `IDX_CERTIFICA2_BATCH_DOMAIN` (`domain`);
ALTER TABLE `certifica2_batch` ADD CONSTRAINT `FK_CERTIFICA2_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `certifica2_batch_attach` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `certifica2_batch_attach` ADD KEY `IDX_CERTIFICA2_BATCH_ATTACH_DOMAIN` (`domain`);
ALTER TABLE `certifica2_batch_attach` ADD CONSTRAINT `FK_CERTIFICA2_BATCH_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `certifica2_batch_data` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `certifica2_batch_data` ADD KEY `IDX_CERTIFICA2_BATCH_DATA_DOMAIN` (`domain`);
ALTER TABLE `certifica2_batch_data` ADD CONSTRAINT `FK_CERTIFICA2_BATCH_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `certifica2_batch_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `certifica2_batch_detail` ADD KEY `IDX_CERTIFICA2_BATCH_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `certifica2_batch_detail` ADD CONSTRAINT `FK_CERTIFICA2_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `cnae` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `cnae` ADD KEY `IDX_CNAE_DOMAIN` (`domain`);
ALTER TABLE `cnae` ADD CONSTRAINT `FK_CNAE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `cnae2009` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `cnae2009` ADD KEY `IDX_CNAE2009_DOMAIN` (`domain`);
ALTER TABLE `cnae2009` ADD CONSTRAINT `FK_CNAE2009_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `cnae2009_rate` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `cnae2009_rate` ADD KEY `IDX_CNAE2009_RATE_DOMAIN` (`domain`);
ALTER TABLE `cnae2009_rate` ADD CONSTRAINT `FK_CNAE2009_RATE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `cno` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `cno` ADD KEY `IDX_CNO_DOMAIN` (`domain`);
ALTER TABLE `cno` ADD CONSTRAINT `FK_CNO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `commercial_activity` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `commercial_activity` ADD KEY `IDX_COMMERCIAL_ACTIVITY_DOMAIN` (`domain`);
ALTER TABLE `commercial_activity` ADD CONSTRAINT `FK_COMMERCIAL_ACTIVITY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `commercial_term` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `commercial_term` ADD KEY `IDX_COMMERCIAL_TERM_DOMAIN` (`domain`);
ALTER TABLE `commercial_term` ADD CONSTRAINT `FK_COMMERCIAL_TERM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `commercial_tracking` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `commercial_tracking` ADD KEY `IDX_COMMERCIAL_TRACKING_DOMAIN` (`domain`);
ALTER TABLE `commercial_tracking` ADD CONSTRAINT `FK_COMMERCIAL_TRACKING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `commission` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `commission` ADD KEY `IDX_COMMISSION_DOMAIN` (`domain`);
ALTER TABLE `commission` ADD CONSTRAINT `FK_COMMISSION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `commission_category` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `commission_category` ADD KEY `IDX_COMMISSION_CATEGORY_DOMAIN` (`domain`);
ALTER TABLE `commission_category` ADD CONSTRAINT `FK_COMMISSION_CATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `commission_item` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `commission_item` ADD KEY `IDX_COMMISSION_ITEM_DOMAIN` (`domain`);
ALTER TABLE `commission_item` ADD CONSTRAINT `FK_COMMISSION_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `commission_type` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `commission_type` ADD KEY `IDX_COMMISSION_TYPE_DOMAIN` (`domain`);
ALTER TABLE `commission_type` ADD CONSTRAINT `FK_COMMISSION_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `commission_type_commission` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `commission_type_commission` ADD KEY `IDX_COMMISSION_TYPE_COMMISSION_DOMAIN` (`domain`);
ALTER TABLE `commission_type_commission` ADD CONSTRAINT `FK_COMMISSION_TYPE_COMMISSION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `company` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `registry`;
ALTER TABLE `company` ADD KEY `IDX_COMPANY_DOMAIN` (`domain`);
ALTER TABLE `company` ADD CONSTRAINT `FK_COMPANY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `contract` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `contract` ADD KEY `IDX_CONTRACT_DOMAIN` (`domain`);
ALTER TABLE `contract` ADD CONSTRAINT `FK_CONTRACT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `contract_attach` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `contract_attach` ADD KEY `IDX_CONTRACT_ATTACH_DOMAIN` (`domain`);
ALTER TABLE `contract_attach` ADD CONSTRAINT `FK_CONTRACT_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `contract_batch` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `contract_batch` ADD KEY `IDX_CONTRACT_BATCH_DOMAIN` (`domain`);
ALTER TABLE `contract_batch` ADD CONSTRAINT `FK_CONTRACT_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `contract_batch_attach` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `contract_batch_attach` ADD KEY `IDX_CONTRACT_BATCH_ATTACH_DOMAIN` (`domain`);
ALTER TABLE `contract_batch_attach` ADD CONSTRAINT `FK_CONTRACT_BATCH_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `contract_batch_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `contract_batch_detail` ADD KEY `IDX_CONTRACT_BATCH_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `contract_batch_detail` ADD CONSTRAINT `FK_CONTRACT_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `contract_bonus` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `contract_bonus` ADD KEY `IDX_CONTRACT_BONUS_DOMAIN` (`domain`);
ALTER TABLE `contract_bonus` ADD CONSTRAINT `FK_CONTRACT_BONUS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `contract_calendar_event` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `contract_calendar_event` ADD KEY `IDX_CONTRACT_CALENDAR_EVENT_DOMAIN` (`domain`);
ALTER TABLE `contract_calendar_event` ADD CONSTRAINT `FK_CONTRACT_CALENDAR_EVENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `contract_data` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `contract_data` ADD KEY `IDX_CONTRACT_DATA_DOMAIN` (`domain`);
ALTER TABLE `contract_data` ADD CONSTRAINT `FK_CONTRACT_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `contract_deduction` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `contract_deduction` ADD KEY `IDX_CONTRACT_DEDUCTION_DOMAIN` (`domain`);
ALTER TABLE `contract_deduction` ADD CONSTRAINT `FK_CONTRACT_DEDUCTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `contract_embargo` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `contract_embargo` ADD KEY `IDX_CONTRACT_EMBARGO_DOMAIN` (`domain`);
ALTER TABLE `contract_embargo` ADD CONSTRAINT `FK_CONTRACT_EMBARGO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `contract_leave` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `contract_leave` ADD KEY `IDX_CONTRACT_LEAVE_DOMAIN` (`domain`);
ALTER TABLE `contract_leave` ADD CONSTRAINT `FK_CONTRACT_LEAVE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `contract_leave_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `contract_leave_detail` ADD KEY `IDX_CONTRACT_LEAVE_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `contract_leave_detail` ADD CONSTRAINT `FK_CONTRACT_LEAVE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `contract_payment` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `contract_payment` ADD KEY `IDX_CONTRACT_PAYMENT_DOMAIN` (`domain`);
ALTER TABLE `contract_payment` ADD CONSTRAINT `FK_CONTRACT_PAYMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `cost_profile` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `cost_profile` ADD KEY `IDX_COST_PROFILE_DOMAIN` (`domain`);
ALTER TABLE `cost_profile` ADD CONSTRAINT `FK_COST_PROFILE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `creditor` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `registry`;
ALTER TABLE `creditor` ADD KEY `IDX_CREDITOR_DOMAIN` (`domain`);
ALTER TABLE `creditor` ADD CONSTRAINT `FK_CREDITOR_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `creditor_account` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `creditor_account` ADD KEY `IDX_CREDITOR_ACCOUNT_DOMAIN` (`domain`);
ALTER TABLE `creditor_account` ADD CONSTRAINT `FK_CREDITOR_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `customer` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `registry`;
ALTER TABLE `customer` ADD KEY `IDX_CUSTOMER_DOMAIN` (`domain`);
ALTER TABLE `customer` ADD CONSTRAINT `FK_CUSTOMER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `customer_account` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `customer_account` ADD KEY `IDX_CUSTOMER_ACCOUNT_DOMAIN` (`domain`);
ALTER TABLE `customer_account` ADD CONSTRAINT `FK_CUSTOMER_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `customer_fee` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `customer_fee` ADD KEY `IDX_CUSTOMER_FEE_DOMAIN` (`domain`);
ALTER TABLE `customer_fee` ADD CONSTRAINT `FK_CUSTOMER_FEE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `daily_tracking` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `daily_tracking` ADD KEY `IDX_DAILY_TRACKING_DOMAIN` (`domain`);
ALTER TABLE `daily_tracking` ADD CONSTRAINT `FK_DAILY_TRACKING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `deduction_concept` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `deduction_concept` ADD KEY `IDX_DEDUCTION_CONCEPT_DOMAIN` (`domain`);
ALTER TABLE `deduction_concept` ADD CONSTRAINT `FK_DEDUCTION_CONCEPT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `delivery` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `delivery` ADD KEY `IDX_DELIVERY_DOMAIN` (`domain`);
ALTER TABLE `delivery` ADD CONSTRAINT `FK_DELIVERY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `delivery` DROP KEY `series`;
ALTER TABLE `delivery` ADD UNIQUE KEY `IDX_UNQ_DELIVERY_DOMAIN_SERIES_NUMBER` (`domain`,`series`,`number`);

ALTER TABLE `delivery_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `delivery_detail` ADD KEY `IDX_DELIVERY_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `delivery_detail` ADD CONSTRAINT `FK_DELIVERY_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `enterprise` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `registry`;
ALTER TABLE `enterprise` ADD KEY `IDX_ENTERPRISE_DOMAIN` (`domain`);
ALTER TABLE `enterprise` ADD CONSTRAINT `FK_ENTERPRISE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `enterprise_activity` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `enterprise_activity` ADD KEY `IDX_ENTERPRISE_ACTIVITY_DOMAIN` (`domain`);
ALTER TABLE `enterprise_activity` ADD CONSTRAINT `FK_ENTERPRISE_ACTIVITY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `enterprise_agreement` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `enterprise_agreement` ADD KEY `IDX_ENTERPRISE_AGREEMENT_DOMAIN` (`domain`);
ALTER TABLE `enterprise_agreement` ADD CONSTRAINT `FK_ENTERPRISE_AGREEMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `enterprise_ccc` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `enterprise_ccc` ADD KEY `IDX_ENTERPRISE_CCC_DOMAIN` (`domain`);
ALTER TABLE `enterprise_ccc` ADD CONSTRAINT `FK_ENTERPRISE_CCC_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `enterprise_data` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `enterprise_data` ADD KEY `IDX_ENTERPRISE_DATA_DOMAIN` (`domain`);
ALTER TABLE `enterprise_data` ADD CONSTRAINT `FK_ENTERPRISE_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `fan_batch` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `fan_batch` ADD KEY `IDX_FAN_BATCH_DOMAIN` (`domain`);
ALTER TABLE `fan_batch` ADD CONSTRAINT `FK_FAN_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `fan_batch_attach` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `fan_batch_attach` ADD KEY `IDX_FAN_BATCH_ATTACH_DOMAIN` (`domain`);
ALTER TABLE `fan_batch_attach` ADD CONSTRAINT `FK_FAN_BATCH_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `fan_batch_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `fan_batch_detail` ADD KEY `IDX_FAN_BATCH_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `fan_batch_detail` ADD CONSTRAINT `FK_FAN_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `favorite` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `favorite` ADD KEY `IDX_FAVORITE_DOMAIN` (`domain`);
ALTER TABLE `favorite` ADD CONSTRAINT `FK_FAVORITE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `favorite_category` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `favorite_category` ADD KEY `IDX_FAVORITE_CATEGORY_DOMAIN` (`domain`);
ALTER TABLE `favorite_category` ADD CONSTRAINT `FK_FAVORITE_CATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `fbatch` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `fbatch` ADD KEY `IDX_FBATCH_DOMAIN` (`domain`);
ALTER TABLE `fbatch` ADD CONSTRAINT `FK_FBATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `fbatch_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `fbatch_detail` ADD KEY `IDX_FBATCH_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `fbatch_detail` ADD CONSTRAINT `FK_FBATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `feature` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `feature` ADD KEY `IDX_FEATURE_DOMAIN` (`domain`);
ALTER TABLE `feature` ADD CONSTRAINT `FK_FEATURE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `finance` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `finance` ADD KEY `IDX_FINANCE_DOMAIN` (`domain`);
ALTER TABLE `finance` ADD CONSTRAINT `FK_FINANCE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `finance_tracking` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `finance_tracking` ADD KEY `IDX_FINANCE_TRACKING_DOMAIN` (`domain`);
ALTER TABLE `finance_tracking` ADD CONSTRAINT `FK_FINANCE_TRACKING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `fs_mod347` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `fs_mod347` ADD KEY `IDX_FS_MOD347_DOMAIN` (`domain`);
ALTER TABLE `fs_mod347` ADD CONSTRAINT `FK_FS_MOD347_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `fs_mod347_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `fs_mod347_detail` ADD KEY `IDX_FS_MOD347_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `fs_mod347_detail` ADD CONSTRAINT `FK_FS_MOD347_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `fs_prof_retention` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `fs_prof_retention` ADD KEY `IDX_FS_PROF_RETENTION_DOMAIN` (`domain`);
ALTER TABLE `fs_prof_retention` ADD CONSTRAINT `FK_FS_PROF_RETENTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `fs_renting` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `fs_renting` ADD KEY `IDX_FS_RENTING_DOMAIN` (`domain`);
ALTER TABLE `fs_renting` ADD CONSTRAINT `FK_FS_RENTING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `fs_renting_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `fs_renting_detail` ADD KEY `IDX_FS_RENTING_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `fs_renting_detail` ADD CONSTRAINT `FK_FS_RENTING_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `fs_vat` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `fs_vat` ADD KEY `IDX_FS_VAT_DOMAIN` (`domain`);
ALTER TABLE `fs_vat` ADD CONSTRAINT `FK_FS_VAT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `fs_vat_declaration` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `fs_vat_declaration` ADD KEY `IDX_FS_VAT_DECLARATION_DOMAIN` (`domain`);
ALTER TABLE `fs_vat_declaration` ADD CONSTRAINT `FK_FS_VAT_DECLARATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `fs_vat_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `fs_vat_detail` ADD KEY `IDX_FS_VAT_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `fs_vat_detail` ADD CONSTRAINT `FK_FS_VAT_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `geotree` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `geotree` ADD KEY `IDX_GEOTREE_DOMAIN` (`domain`);
ALTER TABLE `geotree` ADD CONSTRAINT `FK_GEOTREE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `geozone` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `geozone` ADD KEY `IDX_GEOZONE_DOMAIN` (`domain`);
ALTER TABLE `geozone` ADD CONSTRAINT `FK_GEOZONE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `geozone_irpf` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `geozone_irpf` ADD KEY `IDX_GEOZONE_IRPF_DOMAIN` (`domain`);
ALTER TABLE `geozone_irpf` ADD CONSTRAINT `FK_GEOZONE_IRPF_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `geozone_irpf_descendant` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `geozone_irpf_descendant` ADD KEY `IDX_GEOZONE_IRPF_DESCENDANT_DOMAIN` (`domain`);
ALTER TABLE `geozone_irpf_descendant` ADD CONSTRAINT `FK_GEOZONE_IRPF_DESCENDANT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `geozone_irpf_handicap` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `geozone_irpf_handicap` ADD KEY `IDX_GEOZONE_IRPF_HANDICAP_DOMAIN` (`domain`);
ALTER TABLE `geozone_irpf_handicap` ADD CONSTRAINT `FK_GEOZONE_IRPF_HANDICAP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);


UPDATE `db_version` SET `version_number` = '6.21.1';

COMMIT;
