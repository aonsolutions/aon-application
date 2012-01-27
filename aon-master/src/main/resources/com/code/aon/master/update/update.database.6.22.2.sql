# Database: aon_master
# Version: Actualizacion de la version 6.22.2 a la version 7.0.0.
# Created by: girazu
# Creation Date: 10/01/2012 14:00
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `account` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `account_entry` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `account_entry_bank_statement` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `account_entry_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `account_entry_fbatch` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `account_entry_finance_tracking` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `account_entry_invoice` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `account_helper` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `account_period` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `action` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `action_denied` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `action_entry` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `action_favorite` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `activity_type` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `agreement` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `agreement_data` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `agreement_extra` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `agreement_level` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `agreement_level_category` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `agreement_level_data` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `agreement_payment` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `alarm` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `amortization` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `amortization_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `amortization_type` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `app_param` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `application` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `asset` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `asset_activity` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `asset_feature` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `auto_concept` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `balance` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `balance_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `bank` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `bank_concept` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `bank_concept_account` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `bank_statement` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `bank_statement_link` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `bonus_concept` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `brand` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `calendar` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `calendar_holiday` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `calendar_period` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `campaign` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';
ALTER TABLE `campaign` DROP FOREIGN KEY `FK_CAMPAIGN_ENTERPRISE`;
ALTER TABLE `campaign` DROP KEY `IDX_CAMPAIGN_ENTERPRISE`;
ALTER TABLE `campaign` DROP `enterprise`;

ALTER TABLE `campaign_project` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `campaign_type` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';
ALTER TABLE `campaign_type` DROP FOREIGN KEY `FK_CAMPAIGN_TYPE_ENTERPRISE`;
ALTER TABLE `campaign_type` DROP KEY `IDX_CAMPAIGN_TYPE_ENTERPRISE`;
ALTER TABLE `campaign_type` DROP `enterprise`;

ALTER TABLE `cashflow_forecast` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `catalogue` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `catalogue_category` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `catalogue_item` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `category` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `certifica2_batch` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `certifica2_batch_attach` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `certifica2_batch_data` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `certifica2_batch_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `cnae` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `cnae2009` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `cnae2009_rate` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `cno` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `commercial_activity` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `commercial_term` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `commercial_tracking` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `commission` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `commission_category` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `commission_item` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `commission_type` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `commission_type_commission` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `company` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `contract` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `contract_attach` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `contract_batch` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `contract_batch_attach` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `contract_batch_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `contract_bonus` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `contract_calendar_event` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `contract_data` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `contract_deduction` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `contract_embargo` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `contract_leave` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `contract_leave_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `contract_payment` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `cost_profile` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';
ALTER TABLE `cost_profile` DROP `enterprise`;

ALTER TABLE `creditor` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `creditor_account` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `customer` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `customer_account` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `customer_fee` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `daily_tracking` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';
ALTER TABLE `daily_tracking` DROP FOREIGN KEY `FK_DT_ENTERPRISE`;
ALTER TABLE `daily_tracking` DROP KEY `IDX_DT_ENTERPRISE`;
ALTER TABLE `daily_tracking` DROP `enterprise`;

ALTER TABLE `deduction_concept` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `delivery` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `delivery_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `enterprise` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `enterprise_activity` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `enterprise_agreement` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `enterprise_ccc` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `enterprise_data` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `fan_batch` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `fan_batch_attach` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `fan_batch_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `favorite` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `favorite_category` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `fbatch` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `fbatch_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `feature` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `finance` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `finance_tracking` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `fs_mod347` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `fs_mod347_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `fs_prof_retention` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `fs_renting` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `fs_renting_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `fs_vat` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `fs_vat_declaration` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `fs_vat_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `geotree` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `geozone` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `geozone_irpf` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `geozone_irpf_descendant` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `geozone_irpf_handicap` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `holiday` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `holiday_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `hotel` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `iattach` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `income` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `income_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `inventory` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `inventory_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `invoice` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `invoice_address` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `invoice_attach` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `invoice_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `invoice_detail_account` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `invoice_tax` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `invoice_tax_account` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `invoicing_group` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `invoicing_group_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `irpf_data` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `irpf_data_ascendants` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `irpf_data_descendients` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `irpf_regularization` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `irpf_result` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `item` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `item_alternative` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `item_composition` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `item_supplier` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `item_tariff` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `item_warehouse` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `job_type` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `leave_batch` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `leave_batch_attach` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `leave_batch_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `loan` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `loan_account` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `mail_account` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `make` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `message_content` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `message_log` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `mk_action` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `mk_action_target` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `mk_campaign` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `mk_template` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `model` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `note` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `notice` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `offer` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `offer_attach` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `offer_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `offer_detail_commission` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `offer_term` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `pay_method` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `payment_concept` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `payroll_workplace` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `pcategory` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `pcategory_group` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `pcategory_tree` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `person` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `pm_type_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `pm_type_detail_account` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `process` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';
ALTER TABLE `process` DROP FOREIGN KEY `FK_PROCESS_ENTERPRISE`;
ALTER TABLE `process` DROP KEY `IDX_PROCESS_ENTERPRISE`;
ALTER TABLE `process` DROP `enterprise`;

ALTER TABLE `process_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `process_detail_transition` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `process_task` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `process_transition_type` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `product` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `product_account` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `project` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';
ALTER TABLE `project` DROP FOREIGN KEY `FK_PROJECT_ENTERPRISE`;
ALTER TABLE `project` DROP KEY `IDX_PROJECT_ENTERPRISE`;
ALTER TABLE `project` DROP `enterprise`;

ALTER TABLE `project_activity` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `project_commercial` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `project_dossier` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `project_reservation` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `project_reservation_guest` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `project_reservation_room` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `project_reservation_room_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `project_reservation_service` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `project_tas` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `project_type` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';
ALTER TABLE `project_type` DROP `enterprise`;

ALTER TABLE `purchase` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `purchase_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `question` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `question_value` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `raddinfo` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `raddress` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `rattach` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `rbank` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `rbank_account` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `rdir_staff` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `record_data` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `registry` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `relationship` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `rmedia` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `rnote` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `room` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `rpaymethod` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `rrelationship` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `rsegment` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `salary` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `salary_bonus` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `salary_cost` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `salary_data` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `salary_deduction` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `salary_embargo` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `salary_payment` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `sales` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `sales_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `scope` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `segment` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `seller` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `series` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `session` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `signature` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `stock` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `supplier` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `supplier_account` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `survey` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `survey_question` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `survey_response` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `survey_workflow` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `system_cost` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `system_data` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `system_deduction` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `system_payment` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `target` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `target_item` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `target_profile` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `target_seller` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `target_supplier` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `tariff` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `tariff_catalogue` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `tas_item` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `task` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';
ALTER TABLE `task` DROP FOREIGN KEY `FK_TASK_ENTERPRISE`;
ALTER TABLE `task` DROP KEY `IDX_TASK_ENTERPRISE`;
ALTER TABLE `task` DROP `enterprise`;

ALTER TABLE `task_holder` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';
ALTER TABLE `task_holder` DROP FOREIGN KEY `FK_TASK_HOLDER_ENTERPRISE`;
ALTER TABLE `task_holder` DROP KEY `IDX_TASK_HOLDER_ENTERPRISE`;
ALTER TABLE `task_holder` DROP `enterprise`;

ALTER TABLE `task_holder_workgroup` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `tax` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `tax_account` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `tax_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `user` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `user_scope` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `user_workgroup` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `warehouse` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `warehouse_transfer` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `warehouse_transfer_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `web_info` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `web_info_page` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `web_info_page_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `web_info_page_resource` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `web_info_style` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `workactivity` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `workgroup` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';
ALTER TABLE `workgroup` DROP FOREIGN KEY `FK_WORKGROUP_ENTERPRISE`;
ALTER TABLE `workgroup` DROP KEY `IDX_WORKGROUP_ENTERPRISE`;
ALTER TABLE `workgroup` DROP `enterprise`;

ALTER TABLE `workplace` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';


UPDATE `db_version` SET `version_number` = '7.0.0';

COMMIT;
