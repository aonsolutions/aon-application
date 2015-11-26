# Database: aon_master
# Version: Actualizacion de la version 6.20.0 a la version 6.20.1.
# Created by: girazu
# Creation Date: 09/01/2012 13:10
# Comentarios: Ninguno


BEGIN;

ALTER TABLE `inventory_detail` DROP FOREIGN KEY `inventory_detail_ibfk_1`;
ALTER TABLE `inventory_detail` DROP KEY `idx_invn_invn`;
ALTER TABLE `inventory_detail` ADD KEY `IDX_INVENTORY_DETAIL_INVENTORY` (`inventory`);
ALTER TABLE `inventory_detail` ADD CONSTRAINT `FK_INVENTORY_DETAIL_INVENTORY` FOREIGN KEY (`inventory`) REFERENCES `inventory` (`id`);
ALTER TABLE `inventory_detail` DROP FOREIGN KEY `inventory_detail_ibfk_2`;
ALTER TABLE `inventory_detail` DROP KEY `idx_invn_item`;
ALTER TABLE `inventory_detail` ADD KEY `IDX_INVENTORY_DETAIL_ITEM` (`item`);
ALTER TABLE `inventory_detail` ADD CONSTRAINT `FK_INVENTORY_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`);

ALTER TABLE `invoice` DROP KEY `series`;
ALTER TABLE `invoice` ADD UNIQUE KEY `IDX_INVOICE_SERIES_NUMBER_TYPE` (`series`,`number`,`type`);
ALTER TABLE `invoice` DROP KEY `series_number`;
ALTER TABLE `invoice` DROP KEY `idx_invc_date`;
ALTER TABLE `invoice` ADD KEY `IDX_INVOICE_ISSUE_DATE` (`issue_date`);
ALTER TABLE `invoice` DROP KEY `idx_invc_tax_date`;
ALTER TABLE `invoice` ADD KEY `IDX_INVOICE_TAX_DATE` (`tax_date`);
ALTER TABLE `invoice` DROP FOREIGN KEY `invoice_ibfk_3`;
ALTER TABLE `invoice` DROP KEY `registry`;
ALTER TABLE `invoice` ADD KEY `IDX_INVOICE_REGISTRY` (`registry`);
ALTER TABLE `invoice` ADD CONSTRAINT `FK_INVOICE_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);
ALTER TABLE `invoice` DROP FOREIGN KEY `invoice_ibfk_4`;
ALTER TABLE `invoice` DROP KEY `idx_invc_radr`;
ALTER TABLE `invoice` ADD KEY `IDX_INVOICE_RADDRESS` (`raddress`);
ALTER TABLE `invoice` ADD CONSTRAINT `FK_INVOICE_RADDRESS` FOREIGN KEY (`raddress`) REFERENCES `raddress` (`id`);

ALTER TABLE `invoice_address` DROP FOREIGN KEY `invoice_address_fk`;
ALTER TABLE `invoice_address` DROP KEY `invoice`;
ALTER TABLE `invoice_address` ADD KEY `IDX_INVOICE_ADDRESS_INVOICE` (`invoice`);
ALTER TABLE `invoice_address` ADD CONSTRAINT `FK_INVOICE_ADDRESS_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`);
ALTER TABLE `invoice_address` DROP FOREIGN KEY `invoice_address_fk1`;
ALTER TABLE `invoice_address` DROP KEY `geozone`;
ALTER TABLE `invoice_address` ADD KEY `IDX_INVOICE_ADDRESS_GEOZONE` (`geozone`);
ALTER TABLE `invoice_address` ADD CONSTRAINT `FK_INVOICE_ADDRESS_GEOZONE` FOREIGN KEY (`geozone`) REFERENCES `geozone` (`id`);

ALTER TABLE `invoice_detail` DROP KEY `idx_invd_dlvd`;
ALTER TABLE `invoice_detail` ADD KEY `IDX_INVOICE_DETAIL_SOURCE_ID` (`source_id`);
ALTER TABLE `invoice_detail` DROP FOREIGN KEY `invoice_detail_ibfk_1`;
ALTER TABLE `invoice_detail` DROP KEY `idx_invd_invc`;
ALTER TABLE `invoice_detail` ADD KEY `IDX_INVOICE_DETAIL_INVOICE` (`invoice`);
ALTER TABLE `invoice_detail` ADD CONSTRAINT `FK_INVOICE_DETAIL_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`);
ALTER TABLE `invoice_detail` DROP FOREIGN KEY `invoice_detail_ibfk_2`;
ALTER TABLE `invoice_detail` DROP KEY `idx_invd_item`;
ALTER TABLE `invoice_detail` ADD KEY `IDX_INVOICE_DETAIL_ITEM` (`item`);
ALTER TABLE `invoice_detail` ADD CONSTRAINT `FK_INVOICE_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`);
ALTER TABLE `invoice_detail` DROP FOREIGN KEY `invoice_detail_ibfk_3`;
ALTER TABLE `invoice_detail` DROP KEY `workplace`;
ALTER TABLE `invoice_detail` ADD KEY `IDX_INVOICE_DETAIL_WORKPLACE` (`workplace`);
ALTER TABLE `invoice_detail` ADD CONSTRAINT `FK_INVOICE_DETAIL_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`);

ALTER TABLE `invoice_detail_account` DROP FOREIGN KEY `invoice_detail_account_ibfk_1`;
ALTER TABLE `invoice_detail_account` DROP KEY `invoice_detail`;
ALTER TABLE `invoice_detail_account` ADD KEY `IDX_INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL` (`invoice_detail`);
ALTER TABLE `invoice_detail_account` ADD CONSTRAINT `FK_INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL` FOREIGN KEY (`invoice_detail`) REFERENCES `invoice_detail` (`id`);

ALTER TABLE `invoice_tax` DROP FOREIGN KEY `invoice_tax_ibfk_1`;
ALTER TABLE `invoice_tax` DROP KEY `invoice_detail`;
ALTER TABLE `invoice_tax` ADD KEY `IDX_INVOICE_TAX_INVOICE_DETAIL` (`invoice_detail`);
ALTER TABLE `invoice_tax` ADD CONSTRAINT `FK_INVOICE_TAX_INVOICE_DETAIL` FOREIGN KEY (`invoice_detail`) REFERENCES `invoice_detail` (`id`);

ALTER TABLE `invoice_tax_account` DROP FOREIGN KEY `invoice_tax_account_ibfk_1`;
ALTER TABLE `invoice_tax_account` DROP KEY `invoice_tax`;
ALTER TABLE `invoice_tax_account` ADD KEY `IDX_INVOICE_TAX_ACCOUNT_INVOICE_TAX` (`invoice_tax`);
ALTER TABLE `invoice_tax_account` ADD CONSTRAINT `FK_INVOICE_TAX_ACCOUNT_INVOICE_TAX` FOREIGN KEY (`invoice_tax`) REFERENCES `invoice_tax` (`id`);

ALTER TABLE `invoicing_group` DROP FOREIGN KEY `invoicing_group_fk`;
ALTER TABLE `invoicing_group` DROP KEY `parent_unique`;
ALTER TABLE `invoicing_group` ADD UNIQUE KEY `IDX_INVOICING_GROUP_REGISTRY` (`parent`);
ALTER TABLE `invoicing_group` ADD CONSTRAINT `FK_INVOICING_GROUP_REGISTRY` FOREIGN KEY (`parent`) REFERENCES `registry` (`id`);

ALTER TABLE `invoicing_group_detail` DROP FOREIGN KEY `invoicing_group_detail_fk`;
ALTER TABLE `invoicing_group_detail` DROP KEY `invoicing_group`;
ALTER TABLE `invoicing_group_detail` ADD KEY `IDX_INVOICING_GROUP_DETAIL_INVOICING_GROUP` (`invoicing_group`);
ALTER TABLE `invoicing_group_detail` ADD CONSTRAINT `FK_INVOICING_GROUP_DETAIL_INVOICING_GROUP` FOREIGN KEY (`invoicing_group`) REFERENCES `invoicing_group` (`id`);
ALTER TABLE `invoicing_group_detail` DROP FOREIGN KEY `invoicing_group_detail_fk1`;
ALTER TABLE `invoicing_group_detail` DROP KEY `child_unique`;
ALTER TABLE `invoicing_group_detail` ADD UNIQUE KEY `IDX_INVOICING_GROUP_DETAIL_REGISTRY` (`child`);
ALTER TABLE `invoicing_group_detail` ADD CONSTRAINT `FK_INVOICING_GROUP_DETAIL_REGISTRY` FOREIGN KEY (`child`) REFERENCES `registry` (`id`);

ALTER TABLE `irpf_data_ascendants` DROP FOREIGN KEY `FK_IRPF_DATA_ASCENDIENTS_IRPF_DATA`;
ALTER TABLE `irpf_data_ascendants` DROP KEY `IDX_IRPF_DATA_ASCENDIENTS_IRPF_DATA`;
ALTER TABLE `irpf_data_ascendants` ADD KEY `IDX_IRPF_DATA_ASCENDANTS_IRPF_DATA` (`irpf_data`);
ALTER TABLE `irpf_data_ascendants` ADD CONSTRAINT `FK_IRPF_DATA_ASCENDANTS_IRPF_DATA` FOREIGN KEY (`irpf_data`) REFERENCES `irpf_data` (`id`);

ALTER TABLE `item` DROP FOREIGN KEY `item_ibfk_1`;
ALTER TABLE `item` DROP KEY `idx_item_prdt`;
ALTER TABLE `item` ADD KEY `IDX_ITEM_PRODUCT` (`product`);
ALTER TABLE `item` ADD CONSTRAINT `FK_ITEM_PRODUCT` FOREIGN KEY (`product`) REFERENCES `product` (`id`);

ALTER TABLE `item_tariff` ADD UNIQUE KEY `IDX_UNQ_ITEM_TARIFF` (`item`,`tariff`);

ALTER TABLE `leave_batch_attach` DROP FOREIGN KEY `FK_LEAVE_BATCH_ATTACH_LEAVE_BATCH`;
ALTER TABLE `leave_batch_attach` DROP KEY `IDX_LEAVE_BATCH_ATTACH_CONTRACT`;
ALTER TABLE `leave_batch_attach` ADD KEY `IDX_LEAVE_BATCH_ATTACH_LEAVE_BATCH` (`leave_batch`);
ALTER TABLE `leave_batch_attach` ADD CONSTRAINT `FK_LEAVE_BATCH_ATTACH_LEAVE_BATCH` FOREIGN KEY (`leave_batch`) REFERENCES `leave_batch` (`id`);

ALTER TABLE `loan` DROP FOREIGN KEY `loan_fk`;
ALTER TABLE `loan` DROP KEY `rbank`;
ALTER TABLE `loan` ADD KEY `IDX_LOAN_RBANK` (`rbank`);
ALTER TABLE `loan` ADD CONSTRAINT `FK_LOAN_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`);

ALTER TABLE `loan_account` DROP FOREIGN KEY `loan_account_fk`;
ALTER TABLE `loan_account` DROP KEY `loan`;
ALTER TABLE `loan_account` ADD KEY `IDX_LOAN_ACCOUNT_LOAN` (`loan`);
ALTER TABLE `loan_account` ADD CONSTRAINT `FK_LOAN_ACCOUNT_LOAN` FOREIGN KEY (`loan`) REFERENCES `loan` (`id`);

ALTER TABLE `message_log` DROP FOREIGN KEY `message_log_fk1`;
ALTER TABLE `message_log` DROP KEY `message_content`;
ALTER TABLE `message_log` ADD KEY `IDX_MESSAGE_LOG_MESSAGE_CONTENT` (`message_content`);
ALTER TABLE `message_log` ADD CONSTRAINT `FK_MESSAGE_LOG_MESSAGE_CONTENT` FOREIGN KEY (`message_content`) REFERENCES `message_content` (`id`);

ALTER TABLE `mk_action` DROP FOREIGN KEY `FK_MK_ACTION_MK_CAMPAIGN`;
ALTER TABLE `mk_action` DROP KEY `campaign`;
ALTER TABLE `mk_action` ADD KEY `IDX_MK_ACTION_MK_CAMPAIGN` (`campaign`);
ALTER TABLE `mk_action` ADD CONSTRAINT `FK_MK_ACTION_MK_CAMPAIGN` FOREIGN KEY (`campaign`) REFERENCES `mk_campaign` (`id`);
ALTER TABLE `mk_action` DROP FOREIGN KEY `FK_MK_ACTION_SURVEY`;
ALTER TABLE `mk_action` DROP KEY `survey`;
ALTER TABLE `mk_action` ADD KEY `IDX_MK_ACTION_SURVEY` (`survey`);
ALTER TABLE `mk_action` ADD CONSTRAINT `FK_MK_ACTION_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`);

ALTER TABLE `mk_action_target` DROP FOREIGN KEY `FK_ACTION_TARGET_SURVERY_RESPONSE`;
ALTER TABLE `mk_action_target` DROP KEY `survey_response`;
ALTER TABLE `mk_action_target` ADD KEY `IDX_MK_ACTION_TARGET_SURVEY_RESPONSE` (`survey_response`);
ALTER TABLE `mk_action_target` ADD CONSTRAINT `FK_MK_ACTION_TARGET_SURVEY_RESPONSE` FOREIGN KEY (`survey_response`) REFERENCES `survey_response` (`id`);
ALTER TABLE `mk_action_target` DROP FOREIGN KEY `MK_ACTION_TARGET_MK_ACTION`;
ALTER TABLE `mk_action_target` DROP KEY `action`;
ALTER TABLE `mk_action_target` ADD KEY `IDX_MK_ACTION_TARGET_MK_ACTION` (`action`);
ALTER TABLE `mk_action_target` ADD CONSTRAINT `FK_MK_ACTION_TARGET_MK_ACTION` FOREIGN KEY (`action`) REFERENCES `mk_action` (`id`);
ALTER TABLE `mk_action_target` DROP FOREIGN KEY `MK_ACTION_TARGET_TARGET`;
ALTER TABLE `mk_action_target` DROP KEY `target`;
ALTER TABLE `mk_action_target` ADD KEY `IDX_MK_ACTION_TARGET_TARGET` (`target`);
ALTER TABLE `mk_action_target` ADD CONSTRAINT `FK_MK_ACTION_TARGET_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`);

ALTER TABLE `model` DROP FOREIGN KEY `model_ibfk_1`;
ALTER TABLE `model` DROP KEY `make`;
ALTER TABLE `model` ADD KEY `IDX_MODEL_MAKE` (`make`);
ALTER TABLE `model` ADD CONSTRAINT `FK_MODEL_MAKE` FOREIGN KEY (`make`) REFERENCES `make` (`id`);

ALTER TABLE `note` DROP FOREIGN KEY `note_fk`;
ALTER TABLE `note` DROP KEY `owner`;
ALTER TABLE `note` ADD KEY `IDX_NOTE_USER` (`owner`);
ALTER TABLE `note` ADD CONSTRAINT `FK_NOTE_USER` FOREIGN KEY (`owner`) REFERENCES `user` (`id`);

ALTER TABLE `notice` DROP FOREIGN KEY `notice_fk`;
ALTER TABLE `notice` DROP KEY `sender`;
ALTER TABLE `notice` ADD KEY `IDX_NOTICE_USER_SENDER` (`sender`);
ALTER TABLE `notice` ADD CONSTRAINT `FK_NOTICE_USER_SENDER` FOREIGN KEY (`sender`) REFERENCES `user` (`id`);
ALTER TABLE `notice` DROP FOREIGN KEY `notice_fk1`;
ALTER TABLE `notice` DROP KEY `recipient`;
ALTER TABLE `notice` ADD KEY `IDX_NOTICE_USER_RECIPIENT` (`recipient`);
ALTER TABLE `notice` ADD CONSTRAINT `FK_NOTICE_USER_RECIPIENT` FOREIGN KEY (`recipient`) REFERENCES `user` (`id`);
ALTER TABLE `notice` DROP FOREIGN KEY `notice_fk2`;
ALTER TABLE `notice` DROP KEY `work_group`;
ALTER TABLE `notice` ADD KEY `IDX_NOTICE_WORKGROUP` (`work_group`);
ALTER TABLE `notice` ADD CONSTRAINT `FK_NOTICE_WORKGROUP` FOREIGN KEY (`work_group`) REFERENCES `workgroup` (`id`);

ALTER TABLE `offer` DROP KEY `series`;
ALTER TABLE `offer` ADD UNIQUE KEY `IDX_OFFER_SERIES_NUMBER_VERSION` (`series`,`number`,`version`);
ALTER TABLE `offer` ADD KEY `IDX_OFFER_ISSUE_DATE` (`issue_date`);
ALTER TABLE `offer` DROP FOREIGN KEY `offer_ibfk_1`;
ALTER TABLE `offer` DROP KEY `target`;
ALTER TABLE `offer` ADD KEY `IDX_OFFER_TARGET` (`target`);
ALTER TABLE `offer` ADD CONSTRAINT `FK_OFFER_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`);
ALTER TABLE `offer` DROP FOREIGN KEY `offer_ibfk_2`;
ALTER TABLE `offer` DROP KEY `seller`;
ALTER TABLE `offer` ADD KEY `IDX_OFFER_SELLER` (`seller`);
ALTER TABLE `offer` ADD CONSTRAINT `FK_OFFER_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`);
ALTER TABLE `offer` DROP FOREIGN KEY `offer_ibfk_3`;
ALTER TABLE `offer` DROP KEY `pay_method`;
ALTER TABLE `offer` ADD KEY `IDX_OFFER_PAY_METHOD` (`pay_method`);
ALTER TABLE `offer` ADD CONSTRAINT `FK_OFFER_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`);
ALTER TABLE `offer` DROP FOREIGN KEY `offer_ibfk_4`;
ALTER TABLE `offer` DROP KEY `workplace`;
ALTER TABLE `offer` ADD KEY `IDX_OFFER_WORKPLACE` (`workplace`);
ALTER TABLE `offer` ADD CONSTRAINT `FK_OFFER_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`);
ALTER TABLE `offer` DROP FOREIGN KEY `offer_ibfk_5`;
ALTER TABLE `offer` DROP KEY `tariff`;
ALTER TABLE `offer` ADD KEY `IDX_OFFER_TARIFF` (`tariff`);
ALTER TABLE `offer` ADD CONSTRAINT `FK_OFFER_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`);

ALTER TABLE `offer_detail` DROP FOREIGN KEY `offer_detail_ibfk_1`;
ALTER TABLE `offer_detail` DROP KEY `offer`;
ALTER TABLE `offer_detail` ADD KEY `IDX_OFFER_DETAIL_OFFER` (`offer`);
ALTER TABLE `offer_detail` ADD CONSTRAINT `FK_OFFER_DETAIL_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`);
ALTER TABLE `offer_detail` DROP FOREIGN KEY `offer_detail_ibfk_2`;
ALTER TABLE `offer_detail` DROP KEY `item`;
ALTER TABLE `offer_detail` ADD KEY `IDX_OFFER_DETAIL_ITEM` (`item`);
ALTER TABLE `offer_detail` ADD CONSTRAINT `FK_OFFER_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`);

ALTER TABLE `pcategory` DROP FOREIGN KEY `pcategory_fk_1`;
ALTER TABLE `pcategory` DROP KEY `pcategory_group`;
ALTER TABLE `pcategory` ADD KEY `IDX_PCATEGORY_PCATEGORY_GROUP` (`pcategory_group`);
ALTER TABLE `pcategory` ADD CONSTRAINT `FK_PCATEGORY_PCATEGORY_GROUP` FOREIGN KEY (`pcategory_group`) REFERENCES `pcategory_group` (`id`);

ALTER TABLE `pcategory_tree` DROP FOREIGN KEY `pcategory_tree_ibfk_1`;
ALTER TABLE `pcategory_tree` DROP KEY `parent`;
ALTER TABLE `pcategory_tree` ADD KEY `IDX_PCATEGORY_TREE_PARENT_PCATEGORY` (`parent`);
ALTER TABLE `pcategory_tree` ADD CONSTRAINT `FK_PCATEGORY_TREE_PARENT_PCATEGORY` FOREIGN KEY (`parent`) REFERENCES `pcategory` (`id`);
ALTER TABLE `pcategory_tree` DROP FOREIGN KEY `pcategory_tree_ibfk_2`;
ALTER TABLE `pcategory_tree` DROP KEY `child`;
ALTER TABLE `pcategory_tree` ADD KEY `IDX_PCATEGORY_TREE_CHILD_PCATEGORY` (`child`);
ALTER TABLE `pcategory_tree` ADD CONSTRAINT `FK_PCATEGORY_TREE_CHILD_PCATEGORY` FOREIGN KEY (`child`) REFERENCES `pcategory` (`id`);

ALTER TABLE `person` DROP FOREIGN KEY `person_ibfk_1`;
ALTER TABLE `person` ADD CONSTRAINT `FK_PERSON_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);

ALTER TABLE `pm_type_detail_account` DROP FOREIGN KEY `FK_PM_TYPE_DETAIL_ACCOUNT_DETAIL`;
ALTER TABLE `pm_type_detail_account` DROP KEY `IDX_PM_TYPE_DETAIL_ACCOUNT_DETAIL`;
ALTER TABLE `pm_type_detail_account` ADD KEY `IDX_PM_TYPE_DETAIL_ACCOUNT_PM_TYPE_DETAIL` (`pm_type_detail`);
ALTER TABLE `pm_type_detail_account` ADD CONSTRAINT `FK_PM_TYPE_DETAIL_ACCOUNT_PM_TYPE_DETAIL` FOREIGN KEY (`pm_type_detail`) REFERENCES `pm_type_detail` (`id`);

ALTER TABLE `process_detail` DROP FOREIGN KEY `process_detail_ibfk_1`;
ALTER TABLE `process_detail` DROP KEY `process`;
ALTER TABLE `process_detail` ADD KEY `IDX_PROCESS_DETAIL_PROCESS` (`process`);
ALTER TABLE `process_detail` ADD CONSTRAINT `FK_PROCESS_DETAIL_PROCESS` FOREIGN KEY (`process`) REFERENCES `process` (`id`);
ALTER TABLE `process_detail` DROP FOREIGN KEY `process_detail_fk_2`;
ALTER TABLE `process_detail` DROP KEY `workgroup`;
ALTER TABLE `process_detail` ADD KEY `IDX_PROCESS_DETAIL_WORKGROUP` (`workgroup`);
ALTER TABLE `process_detail` ADD CONSTRAINT `FK_PROCESS_DETAIL_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`);

ALTER TABLE `process_detail_transition` DROP FOREIGN KEY `fk_process_detail_transition_process_detail`;
ALTER TABLE `process_detail_transition` DROP KEY `process_detail`;
ALTER TABLE `process_detail_transition` ADD KEY `IDX_PROCESS_DETAIL_TRANSITION_PROCESS_DETAIL` (`process_detail`);
ALTER TABLE `process_detail_transition` ADD CONSTRAINT `FK_PROCESS_DETAIL_TRANSITION_PROCESS_DETAIL` FOREIGN KEY (`process_detail`) REFERENCES `process_detail` (`id`);
ALTER TABLE `process_detail_transition` DROP FOREIGN KEY `fk_process_detail_transition_next_process_detail`;
ALTER TABLE `process_detail_transition` DROP KEY `next_process_detail`;
ALTER TABLE `process_detail_transition` ADD KEY `IDX_PROCESS_DETAIL_TRANSITION_NEXT_PROCESS_DETAIL` (`next_process_detail`);
ALTER TABLE `process_detail_transition` ADD CONSTRAINT `FK_PROCESS_DETAIL_TRANSITION_NEXT_PROCESS_DETAIL` FOREIGN KEY (`next_process_detail`) REFERENCES `process_detail` (`id`);
ALTER TABLE `process_detail_transition` DROP FOREIGN KEY `fk_process_detail_transition_process_transition_type`;
ALTER TABLE `process_detail_transition` DROP KEY `process_transition_type`;
ALTER TABLE `process_detail_transition` ADD KEY `IDX_PROCESS_DETAIL_TRANSITION_PROCESS_TRANSITION_TYPE` (`process_transition_type`);
ALTER TABLE `process_detail_transition` ADD CONSTRAINT `FK_PROCESS_DETAIL_TRANSITION_PROCESS_TRANSITION_TYPE` FOREIGN KEY (`process_transition_type`) REFERENCES `process_transition_type` (`id`);

ALTER TABLE `process_task` DROP FOREIGN KEY `FK_PROCESS_TASK_PRC_DET`;
ALTER TABLE `process_task` DROP KEY `IDX_PROCESS_TASK_PRC_DET`;
ALTER TABLE `process_task` ADD KEY `IDX_PROCESS_TASK_PROCESS_DETAIL` (`process_detail`);
ALTER TABLE `process_task` ADD CONSTRAINT `FK_PROCESS_TASK_PROCESS_DETAIL` FOREIGN KEY (`process_detail`) REFERENCES `process_detail` (`id`);

ALTER TABLE `product` DROP KEY `idx_prdt_code`;
ALTER TABLE `product` ADD UNIQUE KEY `IDX_PRODUCT_CODE` (`code`);
ALTER TABLE `product` DROP KEY `idx_prdt_name`;
ALTER TABLE `product` ADD KEY `IDX_PRODUCT_NAME` (`name`);
ALTER TABLE `product` DROP FOREIGN KEY `product_fk`;
ALTER TABLE `product` DROP KEY `retention`;
ALTER TABLE `product` ADD KEY `IDX_PRODUCT_TAX_RETENTION` (`retention`);
ALTER TABLE `product` ADD CONSTRAINT `FK_PRODUCT_TAX_RETENTION` FOREIGN KEY (`retention`) REFERENCES `tax` (`id`);
ALTER TABLE `product` DROP FOREIGN KEY `product_ibfk_1`;
ALTER TABLE `product` DROP KEY `idx_prdt_ctgy`;
ALTER TABLE `product` ADD KEY `IDX_PRODUCT_PCATEGORY` (`category`);
ALTER TABLE `product` ADD CONSTRAINT `FK_PRODUCT_PCATEGORY` FOREIGN KEY (`category`) REFERENCES `pcategory` (`id`);
ALTER TABLE `product` DROP FOREIGN KEY `product_ibfk_2`;
ALTER TABLE `product` DROP KEY `idx_prdt_brand`;
ALTER TABLE `product` ADD KEY `IDX_PRODUCT_BRAND` (`brand`);
ALTER TABLE `product` ADD CONSTRAINT `FK_PRODUCT_BRAND` FOREIGN KEY (`brand`) REFERENCES `brand` (`id`);
ALTER TABLE `product` DROP FOREIGN KEY `product_ibfk_3`;
ALTER TABLE `product` DROP KEY `vat`;
ALTER TABLE `product` ADD KEY `IDX_PRODUCT_TAX_VAT` (`vat`);
ALTER TABLE `product` ADD CONSTRAINT `FK_PRODUCT_TAX_VAT` FOREIGN KEY (`vat`) REFERENCES `tax` (`id`);

ALTER TABLE `product_account` DROP FOREIGN KEY `product_account_ibfk_1`;
ALTER TABLE `product_account` DROP KEY `product`;
ALTER TABLE `product_account` ADD KEY `IDX_PRODUCT_ACCOUNT_PRODUCT` (`product`);
ALTER TABLE `product_account` ADD CONSTRAINT `FK_PRODUCT_ACCOUNT_PRODUCT` FOREIGN KEY (`product`) REFERENCES `product` (`id`);

ALTER TABLE `project_activity` DROP FOREIGN KEY `FK_PRJ_ACT_ACTIVITY_TYPE`;
ALTER TABLE `project_activity` DROP KEY `IDX_PRJ_ACT_ACTIVITY_TYPE`;
ALTER TABLE `project_activity` ADD KEY `IDX_PROJECT_ACTIVITY_ACTIVITY_TYPE` (`activity_type`);
ALTER TABLE `project_activity` ADD CONSTRAINT `FK_PROJECT_ACTIVITY_ACTIVITY_TYPE` FOREIGN KEY (`activity_type`) REFERENCES `activity_type` (`id`);
ALTER TABLE `project_activity` DROP FOREIGN KEY `FK_PRJ_ACT_PROJECT`;
ALTER TABLE `project_activity` DROP KEY `IDX_PRJ_ACT_PROJECT`;
ALTER TABLE `project_activity` ADD KEY `IDX_PROJECT_ACTIVITY_PROJECT` (`project`);
ALTER TABLE `project_activity` ADD CONSTRAINT `FK_PROJECT_ACTIVITY_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`);

ALTER TABLE `purchase` DROP KEY `idx_supplier`;
ALTER TABLE `purchase` ADD UNIQUE KEY `IDX_PURCHASE_SUPPLIER_SERIES_NUMBER` (`supplier`,`series`,`number`);
ALTER TABLE `purchase` DROP FOREIGN KEY `purchase_ibfk_1`;
ALTER TABLE `purchase` DROP KEY `supplier`;
ALTER TABLE `purchase` ADD KEY `IDX_PURCHASE_SUPPLIER` (`supplier`);
ALTER TABLE `purchase` ADD CONSTRAINT `FK_PURCHASE_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`);
ALTER TABLE `purchase` DROP FOREIGN KEY `purchase_ibfk_2`;
ALTER TABLE `purchase` DROP KEY `pay_method`;
ALTER TABLE `purchase` ADD KEY `IDX_PURCHASE_PAY_METHOD` (`pay_method`);
ALTER TABLE `purchase` ADD CONSTRAINT `FK_PURCHASE_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`);
ALTER TABLE `purchase` DROP FOREIGN KEY `purchase_ibfk_3`;
ALTER TABLE `purchase` DROP KEY `workplace`;
ALTER TABLE `purchase` ADD KEY `IDX_PURCHASE_WORKPLACE` (`workplace`);
ALTER TABLE `purchase` ADD CONSTRAINT `FK_PURCHASE_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`);

ALTER TABLE `purchase_detail` DROP FOREIGN KEY `purchase_detail_ibfk_1`;
ALTER TABLE `purchase_detail` DROP KEY `purchase`;
ALTER TABLE `purchase_detail` ADD KEY `IDX_PURCHASE_DETAIL_PURCHASE` (`purchase`);
ALTER TABLE `purchase_detail` ADD CONSTRAINT `FK_PURCHASE_DETAIL_PURCHASE` FOREIGN KEY (`purchase`) REFERENCES `purchase` (`id`);
ALTER TABLE `purchase_detail` DROP FOREIGN KEY `purchase_detail_ibfk_2`;
ALTER TABLE `purchase_detail` DROP KEY `item`;
ALTER TABLE `purchase_detail` ADD KEY `IDX_PURCHASE_DETAIL_ITEM` (`item`);
ALTER TABLE `purchase_detail` ADD CONSTRAINT `FK_PURCHASE_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`);

ALTER TABLE `question_value` DROP FOREIGN KEY `FK_QUESTION_VALUE_QUESTION`;
ALTER TABLE `question_value` DROP KEY `question`;
ALTER TABLE `question_value` ADD KEY `IDX_QUESTION_VALUE_QUESTION` (`question`);
ALTER TABLE `question_value` ADD CONSTRAINT `FK_QUESTION_VALUE_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`);

ALTER TABLE `raddress` DROP FOREIGN KEY `raddress_ibfk_1`;
ALTER TABLE `raddress` DROP KEY `idx_radr_rgty`;
ALTER TABLE `raddress` ADD KEY `IDX_RADDRESS_REGISTRY` (`registry`);
ALTER TABLE `raddress` ADD CONSTRAINT `FK_RADDRESS_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);
ALTER TABLE `raddress` DROP FOREIGN KEY `raddress_ibfk_2`;
ALTER TABLE `raddress` DROP KEY `idx_radr_gzne`;
ALTER TABLE `raddress` ADD KEY `IDX_RADDRESS_GEOZONE` (`geozone`);
ALTER TABLE `raddress` ADD CONSTRAINT `FK_RADDRESS_GEOZONE` FOREIGN KEY (`geozone`) REFERENCES `geozone` (`id`);

ALTER TABLE `rattach` DROP FOREIGN KEY `rattach_ibfk_1`;
ALTER TABLE `rattach` DROP KEY `category`;
ALTER TABLE `rattach` ADD KEY `IDX_RATTACH_CATEGORY` (`category`);
ALTER TABLE `rattach` ADD CONSTRAINT `FK_RATTACH_CATEGORY` FOREIGN KEY (`category`) REFERENCES `category` (`id`);
ALTER TABLE `rattach` DROP FOREIGN KEY `rattach_ibfk_2`;
ALTER TABLE `rattach` DROP KEY `registry`;
ALTER TABLE `rattach` ADD KEY `IDX_RATTACH_REGISTRY` (`registry`);
ALTER TABLE `rattach` ADD CONSTRAINT `FK_RATTACH_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);

ALTER TABLE `rbank` DROP FOREIGN KEY `rbank_ibfk_1`;
ALTER TABLE `rbank` DROP KEY `idx_rbnk_rgty`;
ALTER TABLE `rbank` ADD KEY `IDX_RBANK_REGISTRY` (`registry`);
ALTER TABLE `rbank` ADD CONSTRAINT `FK_RBANK_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);
ALTER TABLE `rbank` DROP FOREIGN KEY `rbank_ibfk_2`;
ALTER TABLE `rbank` DROP KEY `idx_rbnk_bank`;
ALTER TABLE `rbank` ADD KEY `IDX_RBANK_BANK` (`bank`);
ALTER TABLE `rbank` ADD CONSTRAINT `FK_RBANK_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`);

ALTER TABLE `rbank_account` DROP FOREIGN KEY `rbank_account_ibfk_1`;
ALTER TABLE `rbank_account` DROP KEY `rbank`;
ALTER TABLE `rbank_account` ADD KEY `IDX_RBANK_ACCOUNT_RBANK` (`rbank`);
ALTER TABLE `rbank_account` ADD CONSTRAINT `FK_RBANK_ACCOUNT_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`);

ALTER TABLE `rdir_staff` DROP FOREIGN KEY `rdir_staff_fk_1`;
ALTER TABLE `rdir_staff` DROP KEY `registry`;
ALTER TABLE `rdir_staff` ADD KEY `IDX_RDIR_STAFF_REGISTRY` (`registry`);
ALTER TABLE `rdir_staff` ADD CONSTRAINT `FK_RDIR_STAFF_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);

ALTER TABLE `record_data` DROP FOREIGN KEY `record_data_fk`;
ALTER TABLE `record_data` DROP KEY `attach`;
ALTER TABLE `record_data` ADD KEY `IDX_RECORD_DATA_RATTACH` (`attach`);
ALTER TABLE `record_data` ADD CONSTRAINT `FK_RECORD_DATA_RATTACH` FOREIGN KEY (`attach`) REFERENCES `rattach` (`id`);
ALTER TABLE `record_data` DROP FOREIGN KEY `record_data_fk_1`;
ALTER TABLE `record_data` DROP KEY `registry`;
ALTER TABLE `record_data` ADD KEY `IDX_RECORD_DATA_REGISTRY` (`registry`);
ALTER TABLE `record_data` ADD CONSTRAINT `FK_RECORD_DATA_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);


UPDATE `db_version` SET `version_number` = '6.20.1';

COMMIT;
