# Database: aon_master
# Version: Actualizacion de la version 6.21.1 a la version 6.21.2.
# Created by: girazu
# Creation Date: 09/01/2012 15:30
# Comentarios: CREACION DE LA TABLA DOMAIN Y EL CAMPO DOMAIN EN TODAS LAS TABLAS.


BEGIN;

ALTER TABLE `holiday` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `holiday` ADD KEY `IDX_HOLIDAY_DOMAIN` (`domain`);
ALTER TABLE `holiday` ADD CONSTRAINT `FK_HOLIDAY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `holiday_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `holiday_detail` ADD KEY `IDX_HOLIDAY_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `holiday_detail` ADD CONSTRAINT `FK_HOLIDAY_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `hotel` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `hotel` ADD KEY `IDX_HOTEL_DOMAIN` (`domain`);
ALTER TABLE `hotel` ADD CONSTRAINT `FK_HOTEL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `hotel` DROP KEY `IDX_HOTEL_CODE`;
ALTER TABLE `hotel` ADD UNIQUE KEY `IDX_UNQ_HOTEL_CODE` (`code`);

ALTER TABLE `iattach` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `iattach` ADD KEY `IDX_IATTACH_DOMAIN` (`domain`);
ALTER TABLE `iattach` ADD CONSTRAINT `FK_IATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `income` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `income` ADD KEY `IDX_INCOME_DOMAIN` (`domain`);
ALTER TABLE `income` ADD CONSTRAINT `FK_INCOME_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `income` DROP KEY `idx_supplier`;
ALTER TABLE `income` ADD UNIQUE KEY `IDX_UNQ_INCOME_DOMAIN_SUPPLIER_SERIES_NUMBER` (`domain`,`supplier`,`series`,`number`);

ALTER TABLE `income_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `income_detail` ADD KEY `IDX_INCOME_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `income_detail` ADD CONSTRAINT `FK_INCOME_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `inventory` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `inventory` ADD KEY `IDX_INVENTORY_DOMAIN` (`domain`);
ALTER TABLE `inventory` ADD CONSTRAINT `FK_INVENTORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `inventory_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `inventory_detail` ADD KEY `IDX_INVENTORY_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `inventory_detail` ADD CONSTRAINT `FK_INVENTORY_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `invoice` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `invoice` ADD KEY `IDX_INVOICE_DOMAIN` (`domain`);
ALTER TABLE `invoice` ADD CONSTRAINT `FK_INVOICE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `invoice` DROP KEY `IDX_INVOICE_SERIES_NUMBER_TYPE`;
ALTER TABLE `invoice` ADD UNIQUE KEY `IDX_UNQ_INVOICE_DOMAIN_SERIES_NUMBER_TYPE` (`domain`,`series`,`number`,`type`);

ALTER TABLE `invoice_address` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `invoice_address` ADD KEY `IDX_INVOICE_ADDRESS_DOMAIN` (`domain`);
ALTER TABLE `invoice_address` ADD CONSTRAINT `FK_INVOICE_ADDRESS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `invoice_attach` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `invoice_attach` ADD KEY `IDX_INVOICE_ATTACH_DOMAIN` (`domain`);
ALTER TABLE `invoice_attach` ADD CONSTRAINT `FK_INVOICE_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `invoice_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `invoice_detail` ADD KEY `IDX_INVOICE_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `invoice_detail` ADD CONSTRAINT `FK_INVOICE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `invoice_detail_account` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `invoice_detail_account` ADD KEY `IDX_INVOICE_DETAIL_ACCOUNT_DOMAIN` (`domain`);
ALTER TABLE `invoice_detail_account` ADD CONSTRAINT `FK_INVOICE_DETAIL_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `invoice_tax` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `invoice_tax` ADD KEY `IDX_INVOICE_TAX_DOMAIN` (`domain`);
ALTER TABLE `invoice_tax` ADD CONSTRAINT `FK_INVOICE_TAX_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `invoice_tax_account` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `invoice_tax_account` ADD KEY `IDX_INVOICE_TAX_ACCOUNT_DOMAIN` (`domain`);
ALTER TABLE `invoice_tax_account` ADD CONSTRAINT `FK_INVOICE_TAX_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `invoicing_group` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `invoicing_group` ADD KEY `IDX_INVOICING_GROUP_DOMAIN` (`domain`);
ALTER TABLE `invoicing_group` ADD CONSTRAINT `FK_INVOICING_GROUP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `invoicing_group_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `invoicing_group_detail` ADD KEY `IDX_INVOICING_GROUP_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `invoicing_group_detail` ADD CONSTRAINT `FK_INVOICING_GROUP_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `irpf_data` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `irpf_data` ADD KEY `IDX_IRPF_DATA_DOMAIN` (`domain`);
ALTER TABLE `irpf_data` ADD CONSTRAINT `FK_IRPF_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `irpf_data_ascendants` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `irpf_data_ascendants` ADD KEY `IDX_IRPF_DATA_ASCENDANTS_DOMAIN` (`domain`);
ALTER TABLE `irpf_data_ascendants` ADD CONSTRAINT `FK_IRPF_DATA_ASCENDANTS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `irpf_data_descendients` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `irpf_data_descendients` ADD KEY `IDX_IRPF_DATA_DESCENDIENTS_DOMAIN` (`domain`);
ALTER TABLE `irpf_data_descendients` ADD CONSTRAINT `FK_IRPF_DATA_DESCENDIENTS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `irpf_regularization` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `irpf_regularization` ADD KEY `IDX_IRPF_REGULARIZATION_DOMAIN` (`domain`);
ALTER TABLE `irpf_regularization` ADD CONSTRAINT `FK_IRPF_REGULARIZATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `irpf_result` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `irpf_result` ADD KEY `IDX_IRPF_RESULT_DOMAIN` (`domain`);
ALTER TABLE `irpf_result` ADD CONSTRAINT `FK_IRPF_RESULT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `item` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `item` ADD KEY `IDX_ITEM_DOMAIN` (`domain`);
ALTER TABLE `item` ADD CONSTRAINT `FK_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `item_alternative` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `item_alternative` ADD KEY `IDX_ITEM_ALTERNATIVE_DOMAIN` (`domain`);
ALTER TABLE `item_alternative` ADD CONSTRAINT `FK_ITEM_ALTERNATIVE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `item_composition` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `item_composition` ADD KEY `IDX_ITEM_COMPOSITION_DOMAIN` (`domain`);
ALTER TABLE `item_composition` ADD CONSTRAINT `FK_ITEM_COMPOSITION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `item_supplier` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `item_supplier` ADD KEY `IDX_ITEM_SUPPLIER_DOMAIN` (`domain`);
ALTER TABLE `item_supplier` ADD CONSTRAINT `FK_ITEM_SUPPLIER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `item_tariff` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `item_tariff` ADD KEY `IDX_ITEM_TARIFF_DOMAIN` (`domain`);
ALTER TABLE `item_tariff` ADD CONSTRAINT `FK_ITEM_TARIFF_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `item_warehouse` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `item_warehouse` ADD KEY `IDX_ITEM_WAREHOUSE_DOMAIN` (`domain`);
ALTER TABLE `item_warehouse` ADD CONSTRAINT `FK_ITEM_WAREHOUSE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `job_type` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `job_type` ADD KEY `IDX_JOB_TYPE_DOMAIN` (`domain`);
ALTER TABLE `job_type` ADD CONSTRAINT `FK_JOB_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `leave_batch` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `leave_batch` ADD KEY `IDX_LEAVE_BATCH_DOMAIN` (`domain`);
ALTER TABLE `leave_batch` ADD CONSTRAINT `FK_LEAVE_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `leave_batch_attach` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `leave_batch_attach` ADD KEY `IDX_LEAVE_BATCH_ATTACH_DOMAIN` (`domain`);
ALTER TABLE `leave_batch_attach` ADD CONSTRAINT `FK_LEAVE_BATCH_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `leave_batch_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `leave_batch_detail` ADD KEY `IDX_LEAVE_BATCH_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `leave_batch_detail` ADD CONSTRAINT `FK_LEAVE_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `loan` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `loan` ADD KEY `IDX_LOAN_DOMAIN` (`domain`);
ALTER TABLE `loan` ADD CONSTRAINT `FK_LOAN_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `loan_account` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `loan_account` ADD KEY `IDX_LOAN_ACCOUNT_DOMAIN` (`domain`);
ALTER TABLE `loan_account` ADD CONSTRAINT `FK_LOAN_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `mail_account` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `mail_account` ADD KEY `IDX_MAIL_ACCOUNT_DOMAIN` (`domain`);
ALTER TABLE `mail_account` ADD CONSTRAINT `FK_MAIL_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `make` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `make` ADD KEY `IDX_MAKE_DOMAIN` (`domain`);
ALTER TABLE `make` ADD CONSTRAINT `FK_MAKE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `message_content` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `message_content` ADD KEY `IDX_MESSAGE_CONTENT_DOMAIN` (`domain`);
ALTER TABLE `message_content` ADD CONSTRAINT `FK_MESSAGE_CONTENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `message_log` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `message_log` ADD KEY `IDX_MESSAGE_LOG_DOMAIN` (`domain`);
ALTER TABLE `message_log` ADD CONSTRAINT `FK_MESSAGE_LOG_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `mk_action` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `mk_action` ADD KEY `IDX_MK_ACTION_DOMAIN` (`domain`);
ALTER TABLE `mk_action` ADD CONSTRAINT `FK_MK_ACTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `mk_action_target` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `mk_action_target` ADD KEY `IDX_MK_ACTION_TARGET_DOMAIN` (`domain`);
ALTER TABLE `mk_action_target` ADD CONSTRAINT `FK_MK_ACTION_TARGET_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `mk_campaign` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `mk_campaign` ADD KEY `IDX_MK_CAMPAIGN_DOMAIN` (`domain`);
ALTER TABLE `mk_campaign` ADD CONSTRAINT `FK_MK_CAMPAIGN_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `mk_template` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `mk_template` ADD KEY `IDX_MK_TEMPLATE_DOMAIN` (`domain`);
ALTER TABLE `mk_template` ADD CONSTRAINT `FK_MK_TEMPLATE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `model` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `model` ADD KEY `IDX_MODEL_DOMAIN` (`domain`);
ALTER TABLE `model` ADD CONSTRAINT `FK_MODEL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `note` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `note` ADD KEY `IDX_NOTE_DOMAIN` (`domain`);
ALTER TABLE `note` ADD CONSTRAINT `FK_NOTE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `notice` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `notice` ADD KEY `IDX_NOTICE_DOMAIN` (`domain`);
ALTER TABLE `notice` ADD CONSTRAINT `FK_NOTICE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `offer` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `offer` ADD KEY `IDX_OFFER_DOMAIN` (`domain`);
ALTER TABLE `offer` ADD CONSTRAINT `FK_OFFER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `offer` DROP KEY `IDX_OFFER_SERIES_NUMBER_VERSION`;
ALTER TABLE `offer` ADD UNIQUE KEY `IDX_OFFER_DOMAIN_SERIES_NUMBER_VERSION` (`domain`,`series`,`number`,`version`);

ALTER TABLE `offer_attach` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `offer_attach` ADD KEY `IDX_OFFER_ATTACH_DOMAIN` (`domain`);
ALTER TABLE `offer_attach` ADD CONSTRAINT `FK_OFFER_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `offer_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `offer_detail` ADD KEY `IDX_OFFER_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `offer_detail` ADD CONSTRAINT `FK_OFFER_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `offer_detail_commission` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `offer_detail_commission` ADD KEY `IDX_OFFER_DETAIL_COMMISSION_DOMAIN` (`domain`);
ALTER TABLE `offer_detail_commission` ADD CONSTRAINT `FK_OFFER_DETAIL_COMMISSION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `offer_term` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `offer_term` ADD KEY `IDX_OFFER_TERM_DOMAIN` (`domain`);
ALTER TABLE `offer_term` ADD CONSTRAINT `FK_OFFER_TERM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `pay_method` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `pay_method` ADD KEY `IDX_PAY_METHOD_DOMAIN` (`domain`);
ALTER TABLE `pay_method` ADD CONSTRAINT `FK_PAY_METHOD_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `payment_concept` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `payment_concept` ADD KEY `IDX_PAYMENT_CONCEPT_DOMAIN` (`domain`);
ALTER TABLE `payment_concept` ADD CONSTRAINT `FK_PAYMENT_CONCEPT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `payroll_workplace` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `payroll_workplace` ADD KEY `IDX_PAYROLL_WORKPLACE_DOMAIN` (`domain`);
ALTER TABLE `payroll_workplace` ADD CONSTRAINT `FK_PAYROLL_WORKPLACE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `pcategory` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `pcategory` ADD KEY `IDX_PCATEGORY_DOMAIN` (`domain`);
ALTER TABLE `pcategory` ADD CONSTRAINT `FK_PCATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `pcategory_group` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `pcategory_group` ADD KEY `IDX_PCATEGORY_GROUP_DOMAIN` (`domain`);
ALTER TABLE `pcategory_group` ADD CONSTRAINT `FK_PCATEGORY_GROUP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `pcategory_tree` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `pcategory_tree` ADD KEY `IDX_PCATEGORY_TREE_DOMAIN` (`domain`);
ALTER TABLE `pcategory_tree` ADD CONSTRAINT `FK_PCATEGORY_TREE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `person` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `registry`;
ALTER TABLE `person` ADD KEY `IDX_PERSON_DOMAIN` (`domain`);
ALTER TABLE `person` ADD CONSTRAINT `FK_PERSON_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `pm_type_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `pm_type_detail` ADD KEY `IDX_PM_TYPE_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `pm_type_detail` ADD CONSTRAINT `FK_PM_TYPE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `pm_type_detail_account` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `pm_type_detail_account` ADD KEY `IDX_PM_TYPE_DETAIL_ACCOUNT_DOMAIN` (`domain`);
ALTER TABLE `pm_type_detail_account` ADD CONSTRAINT `FK_PM_TYPE_DETAIL_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `process` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `process` ADD KEY `IDX_PROCESS_DOMAIN` (`domain`);
ALTER TABLE `process` ADD CONSTRAINT `FK_PROCESS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `process_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `process_detail` ADD KEY `IDX_PROCESS_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `process_detail` ADD CONSTRAINT `FK_PROCESS_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `process_detail_transition` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `process_detail_transition` ADD KEY `IDX_PROCESS_DETAIL_TRANSITION_DOMAIN` (`domain`);
ALTER TABLE `process_detail_transition` ADD CONSTRAINT `FK_PROCESS_DETAIL_TRANSITION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `process_task` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `process_task` ADD KEY `IDX_PROCESS_TASK_DOMAIN` (`domain`);
ALTER TABLE `process_task` ADD CONSTRAINT `FK_PROCESS_TASK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `process_transition_type` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `process_transition_type` ADD KEY `IDX_PROCESS_TRANSITION_TYPE_DOMAIN` (`domain`);
ALTER TABLE `process_transition_type` ADD CONSTRAINT `FK_PROCESS_TRANSITION_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `product` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `product` ADD KEY `IDX_PRODUCT_DOMAIN` (`domain`);
ALTER TABLE `product` ADD CONSTRAINT `FK_PRODUCT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `product` DROP KEY `IDX_PRODUCT_CODE`;
ALTER TABLE `product` ADD UNIQUE KEY `IDX_PRODUCT_DOMAIN_CODE` (`domain`,`code`);

ALTER TABLE `product_account` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `product_account` ADD KEY `IDX_PRODUCT_ACCOUNT_DOMAIN` (`domain`);
ALTER TABLE `product_account` ADD CONSTRAINT `FK_PRODUCT_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `project` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `project` ADD KEY `IDX_PROJECT_DOMAIN` (`domain`);
ALTER TABLE `project` ADD CONSTRAINT `FK_PROJECT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `project_activity` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `project_activity` ADD KEY `IDX_PROJECT_ACTIVITY_DOMAIN` (`domain`);
ALTER TABLE `project_activity` ADD CONSTRAINT `FK_PROJECT_ACTIVITY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `project_commercial` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `project`;
ALTER TABLE `project_commercial` ADD KEY `IDX_PROJECT_COMMERCIAL_DOMAIN` (`domain`);
ALTER TABLE `project_commercial` ADD CONSTRAINT `FK_PROJECT_COMMERCIAL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `project_dossier` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `project`;
ALTER TABLE `project_dossier` ADD KEY `IDX_PROJECT_DOSSIER_DOMAIN` (`domain`);
ALTER TABLE `project_dossier` ADD CONSTRAINT `FK_PROJECT_DOSSIER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `project_dossier` DROP KEY `IDX_PRJ_DOSSIER_NUMBER`;
ALTER TABLE `project_dossier` ADD UNIQUE KEY `IDX_UNQ_PROJECT_DOSSIER_DOMAIN_NUMBER` (`domain`,`number`);
ALTER TABLE `project_dossier` DROP FOREIGN KEY `FK_PRJ_DOSSIER_PROJECT`;
ALTER TABLE `project_dossier` DROP KEY `FK_PRJ_DOSSIER_PROJECT`;
ALTER TABLE `project_dossier` ADD CONSTRAINT `FK_PROJECT_DOSSIER_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`);
ALTER TABLE `project_dossier` DROP FOREIGN KEY `FK_PRJ_DOSSIER_CUSTOMER`;
ALTER TABLE `project_dossier` DROP KEY `IDX_PRJ_DOSSIER_CUSTOMER`;
ALTER TABLE `project_dossier` ADD KEY `IDX_PROJECT_DOSSIER_CUSTOMER` (`customer`);
ALTER TABLE `project_dossier` ADD CONSTRAINT `FK_PROJECT_DOSSIER_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`);

ALTER TABLE `project_reservation` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `project`;
ALTER TABLE `project_reservation` ADD KEY `IDX_PROJECT_RESERVATION_DOMAIN` (`domain`);
ALTER TABLE `project_reservation` ADD CONSTRAINT `FK_PROJECT_RESERVATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `project_reservation_guest` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `project_reservation_guest` ADD KEY `IDX_PROJECT_RESERVATION_GUEST_DOMAIN` (`domain`);
ALTER TABLE `project_reservation_guest` ADD CONSTRAINT `FK_PROJECT_RESERVATION_GUEST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `project_reservation_room` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `project_reservation_room` ADD KEY `IDX_PROJECT_RESERVATION_ROOM_DOMAIN` (`domain`);
ALTER TABLE `project_reservation_room` ADD CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `project_reservation_room_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `project_reservation_room_detail` ADD KEY `IDX_PROJECT_RESERVATION_ROOM_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `project_reservation_room_detail` ADD CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `project_reservation_service` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `project_reservation_service` ADD KEY `IDX_PROJECT_RESERVATION_SERVICE_DOMAIN` (`domain`);
ALTER TABLE `project_reservation_service` ADD CONSTRAINT `FK_PROJECT_RESERVATION_SERVICE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `project_tas` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `project`;
ALTER TABLE `project_tas` ADD KEY `IDX_PROJECT_TAS_DOMAIN` (`domain`);
ALTER TABLE `project_tas` ADD CONSTRAINT `FK_PROJECT_TAS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `project_type` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `project_type` ADD KEY `IDX_PROJECT_TYPE_DOMAIN` (`domain`);
ALTER TABLE `project_type` ADD CONSTRAINT `FK_PROJECT_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `purchase` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `purchase` ADD KEY `IDX_PURCHASE_DOMAIN` (`domain`);
ALTER TABLE `purchase` ADD CONSTRAINT `FK_PURCHASE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `purchase` DROP KEY `IDX_PURCHASE_SUPPLIER_SERIES_NUMBER`;
ALTER TABLE `purchase` ADD UNIQUE KEY `IDX_UNQ_PURCHASE_DOMAIN_SUPPLIER_SERIES_NUMBER` (`domain`,`supplier`,`series`,`number`);

ALTER TABLE `purchase_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `purchase_detail` ADD KEY `IDX_PURCHASE_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `purchase_detail` ADD CONSTRAINT `FK_PURCHASE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `question` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `question` ADD KEY `IDX_QUESTION_DOMAIN` (`domain`);
ALTER TABLE `question` ADD CONSTRAINT `FK_QUESTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `question_value` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `question_value` ADD KEY `IDX_QUESTION_VALUE_DOMAIN` (`domain`);
ALTER TABLE `question_value` ADD CONSTRAINT `FK_QUESTION_VALUE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `raddinfo` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `raddinfo` ADD KEY `IDX_RADDINFO_DOMAIN` (`domain`);
ALTER TABLE `raddinfo` ADD CONSTRAINT `FK_RADDINFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `raddress` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `raddress` ADD KEY `IDX_RADDRESS_DOMAIN` (`domain`);
ALTER TABLE `raddress` ADD CONSTRAINT `FK_RADDRESS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `rattach` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `rattach` ADD KEY `IDX_RATTACH_DOMAIN` (`domain`);
ALTER TABLE `rattach` ADD CONSTRAINT `FK_RATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `rbank` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `rbank` ADD KEY `IDX_RBANK_DOMAIN` (`domain`);
ALTER TABLE `rbank` ADD CONSTRAINT `FK_RBANK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `rbank_account` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `rbank_account` ADD KEY `IDX_RBANK_ACCOUNT_DOMAIN` (`domain`);
ALTER TABLE `rbank_account` ADD CONSTRAINT `FK_RBANK_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `rdir_staff` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `rdir_staff` ADD KEY `IDX_RDIR_STAFF_DOMAIN` (`domain`);
ALTER TABLE `rdir_staff` ADD CONSTRAINT `FK_RDIR_STAFF_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `record_data` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `record_data` ADD KEY `IDX_RECORD_DATA_DOMAIN` (`domain`);
ALTER TABLE `record_data` ADD CONSTRAINT `FK_RECORD_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `registry` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `registry` ADD KEY `IDX_REGISTRY_DOMAIN` (`domain`);
ALTER TABLE `registry` ADD CONSTRAINT `FK_REGISTRY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `relationship` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `relationship` ADD KEY `IDX_RELATIONSHIP_DOMAIN` (`domain`);
ALTER TABLE `relationship` ADD CONSTRAINT `FK_RELATIONSHIP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `rmedia` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `rmedia` ADD KEY `IDX_RMEDIA_DOMAIN` (`domain`);
ALTER TABLE `rmedia` ADD CONSTRAINT `FK_RMEDIA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `rnote` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `rnote` ADD KEY `IDX_RNOTE_DOMAIN` (`domain`);
ALTER TABLE `rnote` ADD CONSTRAINT `FK_RNOTE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `room` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `room` ADD KEY `IDX_ROOM_DOMAIN` (`domain`);
ALTER TABLE `room` ADD CONSTRAINT `FK_ROOM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `rpaymethod` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `rpaymethod` ADD KEY `IDX_RPAYMETHOD_DOMAIN` (`domain`);
ALTER TABLE `rpaymethod` ADD CONSTRAINT `FK_RPAYMETHOD_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `rrelationship` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `rrelationship` ADD KEY `IDX_RRELATIONSHIP_DOMAIN` (`domain`);
ALTER TABLE `rrelationship` ADD CONSTRAINT `FK_RRELATIONSHIP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `rsegment` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `rsegment` ADD KEY `IDX_RSEGMENT_DOMAIN` (`domain`);
ALTER TABLE `rsegment` ADD CONSTRAINT `FK_RSEGMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `salary` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `salary` ADD KEY `IDX_SALARY_DOMAIN` (`domain`);
ALTER TABLE `salary` ADD CONSTRAINT `FK_SALARY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `salary_bonus` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `salary_bonus` ADD KEY `IDX_SALARY_BONUS_DOMAIN` (`domain`);
ALTER TABLE `salary_bonus` ADD CONSTRAINT `FK_SALARY_BONUS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `salary_cost` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `salary_cost` ADD KEY `IDX_SALARY_COST_DOMAIN` (`domain`);
ALTER TABLE `salary_cost` ADD CONSTRAINT `FK_SALARY_COST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `salary_data` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `salary_data` ADD KEY `IDX_SALARY_DATA_DOMAIN` (`domain`);
ALTER TABLE `salary_data` ADD CONSTRAINT `FK_SALARY_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `salary_deduction` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `salary_deduction` ADD KEY `IDX_SALARY_DEDUCTION_DOMAIN` (`domain`);
ALTER TABLE `salary_deduction` ADD CONSTRAINT `FK_SALARY_DEDUCTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `salary_embargo` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `salary_embargo` ADD KEY `IDX_SALARY_EMBARGO_DOMAIN` (`domain`);
ALTER TABLE `salary_embargo` ADD CONSTRAINT `FK_SALARY_EMBARGO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `salary_payment` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `salary_payment` ADD KEY `IDX_SALARY_PAYMENT_DOMAIN` (`domain`);
ALTER TABLE `salary_payment` ADD CONSTRAINT `FK_SALARY_PAYMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `sales` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `sales` ADD KEY `IDX_SALES_DOMAIN` (`domain`);
ALTER TABLE `sales` ADD CONSTRAINT `FK_SALES_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `sales` DROP KEY `IDX_SALES_SERIES_NUMBER`;
ALTER TABLE `sales` ADD UNIQUE KEY `IDX_UNQ_SALES_DOMAIN_SERIES_NUMBER` (`domain`,`series`,`number`);

ALTER TABLE `sales_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `sales_detail` ADD KEY `IDX_SALES_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `sales_detail` ADD CONSTRAINT `FK_SALES_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `scope` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `scope` ADD KEY `IDX_SCOPE_DOMAIN` (`domain`);
ALTER TABLE `scope` ADD CONSTRAINT `FK_SCOPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `segment` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `segment` ADD KEY `IDX_SEGMENT_DOMAIN` (`domain`);
ALTER TABLE `segment` ADD CONSTRAINT `FK_SEGMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `seller` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `registry`;
ALTER TABLE `seller` ADD KEY `IDX_SELLER_DOMAIN` (`domain`);
ALTER TABLE `seller` ADD CONSTRAINT `FK_SELLER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `series` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `series` ADD KEY `IDX_SERIES_DOMAIN` (`domain`);
ALTER TABLE `series` ADD CONSTRAINT `FK_SERIES_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `session` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `session` ADD KEY `IDX_SESSION_DOMAIN` (`domain`);
ALTER TABLE `session` ADD CONSTRAINT `FK_SESSION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `signature` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `signature` ADD KEY `IDX_SIGNATURE_DOMAIN` (`domain`);
ALTER TABLE `signature` ADD CONSTRAINT `FK_SIGNATURE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `stock` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `stock` ADD KEY `IDX_STOCK_DOMAIN` (`domain`);
ALTER TABLE `stock` ADD CONSTRAINT `FK_STOCK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `stock` DROP KEY `IDX_STOCK_WAREHOUSE_ITEM`;
ALTER TABLE `stock` ADD UNIQUE KEY `IDX_UNQ_STOCK_WAREHOUSE_ITEM` (`warehouse`,`item`);

ALTER TABLE `supplier` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `registry`;
ALTER TABLE `supplier` ADD KEY `IDX_SUPPLIER_DOMAIN` (`domain`);
ALTER TABLE `supplier` ADD CONSTRAINT `FK_SUPPLIER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `supplier_account` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `supplier_account` ADD KEY `IDX_SUPPLIER_ACCOUNT_DOMAIN` (`domain`);
ALTER TABLE `supplier_account` ADD CONSTRAINT `FK_SUPPLIER_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `survey` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `survey` ADD KEY `IDX_SURVEY_DOMAIN` (`domain`);
ALTER TABLE `survey` ADD CONSTRAINT `FK_SURVEY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `survey_question` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `survey_question` ADD KEY `IDX_SURVEY_QUESTION_DOMAIN` (`domain`);
ALTER TABLE `survey_question` ADD CONSTRAINT `FK_SURVEY_QUESTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `survey_response` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `survey_response` ADD KEY `IDX_SURVEY_RESPONSE_DOMAIN` (`domain`);
ALTER TABLE `survey_response` ADD CONSTRAINT `FK_SURVEY_RESPONSE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `survey_workflow` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `survey_workflow` ADD KEY `IDX_SURVEY_WORKFLOW_DOMAIN` (`domain`);
ALTER TABLE `survey_workflow` ADD CONSTRAINT `FK_SURVEY_WORKFLOW_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);


UPDATE `db_version` SET `version_number` = '6.21.2';

COMMIT;
