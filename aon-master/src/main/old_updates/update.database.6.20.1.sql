# Database: aon_master
# Version: Actualizacion de la version 6.20.1 a la version 6.20.2.
# Created by: girazu
# Creation Date: 09/01/2012 13:15
# Comentarios: Ninguno


BEGIN;

ALTER TABLE `registry` DROP KEY `idx_rgty_name`;
ALTER TABLE `registry` ADD KEY `IDX_REGISTRY_NAME` (`name`);
ALTER TABLE `registry` DROP KEY `idx_rgty_document`;
ALTER TABLE `registry` ADD KEY `IDX_REGISTRY_DOCUMENT` (`document`);

ALTER TABLE `rmedia` DROP FOREIGN KEY `rmedia_ibfk_1`;
ALTER TABLE `rmedia` DROP KEY `idx_rmed_rgty`;
ALTER TABLE `rmedia` ADD KEY `IDX_RMEDIA_REGISTRY` (`registry`);
ALTER TABLE `rmedia` ADD CONSTRAINT `FK_RMEDIA_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);

ALTER TABLE `rnote` DROP FOREIGN KEY `rnote_fk_1`;
ALTER TABLE `rnote` DROP KEY `registry`;
ALTER TABLE `rnote` ADD KEY `IDX_RNOTE_REGISTRY` (`registry`);
ALTER TABLE `rnote` ADD CONSTRAINT `FK_RNOTE_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);

ALTER TABLE `rpaymethod` DROP FOREIGN KEY `rpaymethod_ibfk_1`;
ALTER TABLE `rpaymethod` DROP KEY `pay_method`;
ALTER TABLE `rpaymethod` ADD KEY `IDX_RPAYMETHOD_PAY_METHOD` (`pay_method`);
ALTER TABLE `rpaymethod` ADD CONSTRAINT `FK_RPAYMETHOD_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`);
ALTER TABLE `rpaymethod` DROP FOREIGN KEY `rpaymethod_ibfk_2`;
ALTER TABLE `rpaymethod` DROP KEY `rbank`;
ALTER TABLE `rpaymethod` ADD KEY `IDX_RPAYMETHOD_RBANK` (`rbank`);
ALTER TABLE `rpaymethod` ADD CONSTRAINT `FK_RPAYMETHOD_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`);
ALTER TABLE `rpaymethod` DROP FOREIGN KEY `rpaymethod_ibfk_3`;
ALTER TABLE `rpaymethod` DROP KEY `registry`;
ALTER TABLE `rpaymethod` ADD KEY `IDX_RPAYMETHOD_REGISTRY` (`registry`);
ALTER TABLE `rpaymethod` ADD CONSTRAINT `FK_RPAYMETHOD_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);

ALTER TABLE `rrelationship` DROP FOREIGN KEY `rrelationship_fk`;
ALTER TABLE `rrelationship` DROP KEY `registry`;
ALTER TABLE `rrelationship` ADD KEY `IDX_RRELATIONSHIP_REGISTRY` (`registry`);
ALTER TABLE `rrelationship` ADD CONSTRAINT `FK_RRELATIONSHIP_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);
ALTER TABLE `rrelationship` DROP FOREIGN KEY `rrelationship_fk1`;
ALTER TABLE `rrelationship` DROP KEY `related_registry`;
ALTER TABLE `rrelationship` ADD KEY `IDX_RRELATIONSHIP_RELATED_REGISTRY` (`related_registry`);
ALTER TABLE `rrelationship` ADD CONSTRAINT `FK_RRELATIONSHIP_RELATED_REGISTRY` FOREIGN KEY (`related_registry`) REFERENCES `registry` (`id`);
ALTER TABLE `rrelationship` ADD KEY `IDX_RRELATIONSHIP_RELATIONSHIP` (`relationship`);
ALTER TABLE `rrelationship` ADD CONSTRAINT `FK_RRELATIONSHIP_RELATIONSHIP` FOREIGN KEY (`relationship`) REFERENCES `relationship` (`id`);

ALTER TABLE `rsegment` DROP FOREIGN KEY `FK_REGISTRY_SEGMENT_REGISTRY`;
ALTER TABLE `rsegment` DROP KEY `IDX_REGISTRY_SEGMENT_REGISTRY`;
ALTER TABLE `rsegment` ADD KEY `IDX_RSEGMENT_REGISTRY` (`registry`);
ALTER TABLE `rsegment` ADD CONSTRAINT `FK_RSEGMENT_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);
ALTER TABLE `rsegment` DROP FOREIGN KEY `FK_REGISTRY_SEGMENT_SEGMENT`;
ALTER TABLE `rsegment` DROP KEY `IDX_REGISTRY_SEGMENT_SEGMENT`;
ALTER TABLE `rsegment` ADD KEY `IDX_RSEGMENT_SEGMENT` (`segment`);
ALTER TABLE `rsegment` ADD CONSTRAINT `FK_RSEGMENT_SEGMENT` FOREIGN KEY (`segment`) REFERENCES `segment` (`id`);

ALTER TABLE `salary` DROP FOREIGN KEY `FK_SALARY_RECCEIPT_CONTRACT`;
ALTER TABLE `salary` DROP KEY `IDX_SALARY_RECCEIPT_CONTRACT`;
ALTER TABLE `salary` ADD KEY `IDX_SALARY_CONTRACT` (`contract`);
ALTER TABLE `salary` ADD CONSTRAINT `FK_SALARY_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`);

ALTER TABLE `salary_bonus` DROP FOREIGN KEY `FK_BONUS_SALARY`;
ALTER TABLE `salary_bonus` DROP KEY `IDX_BONUS_SALARY`;
ALTER TABLE `salary_bonus` ADD KEY `IDX_SALARY_BONUS_SALARY` (`salary`);
ALTER TABLE `salary_bonus` ADD CONSTRAINT `FK_SALARY_BONUS_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`);

ALTER TABLE `salary_cost` DROP FOREIGN KEY `FK_COST_SALARY`;
ALTER TABLE `salary_cost` DROP KEY `IDX_COST_SALARY`;
ALTER TABLE `salary_cost` ADD KEY `IDX_SALARY_COST_SALARY` (`salary`);
ALTER TABLE `salary_cost` ADD CONSTRAINT `FK_SALARY_COST_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`);

ALTER TABLE `salary_deduction` DROP FOREIGN KEY `FK_DEDUCTION_SALARY`;
ALTER TABLE `salary_deduction` DROP KEY `IDX_DEDUCTION_SALARY`;
ALTER TABLE `salary_deduction` ADD KEY `IDX_SALARY_DEDUCTION_SALARY` (`salary`);
ALTER TABLE `salary_deduction` ADD CONSTRAINT `FK_SALARY_DEDUCTION_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`);

ALTER TABLE `salary_embargo` DROP FOREIGN KEY `FK_EMBARGO_SALARY`;
ALTER TABLE `salary_embargo` DROP KEY `IDX_EMBARGO_SALARY`;
ALTER TABLE `salary_embargo` ADD KEY `IDX_SALARY_EMBARGO_SALARY` (`salary`);
ALTER TABLE `salary_embargo` ADD CONSTRAINT `FK_SALARY_EMBARGO_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`);

ALTER TABLE `salary_payment` DROP FOREIGN KEY `FK_PAYMENT_SALARY`;
ALTER TABLE `salary_payment` DROP KEY `IDX_PAYMENT_SALARY`;
ALTER TABLE `salary_payment` ADD KEY `IDX_SALARY_PAYMENT_SALARY` (`salary`);
ALTER TABLE `salary_payment` ADD CONSTRAINT `FK_SALARY_PAYMENT_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`);

ALTER TABLE `sales` DROP KEY `series`;
ALTER TABLE `sales` ADD UNIQUE KEY `IDX_SALES_SERIES_NUMBER` (`series`,`number`);
ALTER TABLE `sales` DROP KEY `idx_sale_date`;
ALTER TABLE `sales` ADD KEY `IDX_SALES_ISSUE_DATE` (`issue_date`);
ALTER TABLE `sales` DROP FOREIGN KEY `sales_ibfk_1`;
ALTER TABLE `sales` DROP KEY `idx_sale_sllr`;
ALTER TABLE `sales` ADD KEY `IDX_SALES_SELLER` (`seller`);
ALTER TABLE `sales` ADD CONSTRAINT `FK_SALES_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`);
ALTER TABLE `sales` DROP FOREIGN KEY `sales_ibfk_2`;
ALTER TABLE `sales` DROP KEY `idx_sale_ctmr`;
ALTER TABLE `sales` ADD KEY `IDX_SALES_CUSTOMER` (`customer`);
ALTER TABLE `sales` ADD CONSTRAINT `FK_SALES_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`);
ALTER TABLE `sales` DROP FOREIGN KEY `sales_ibfk_3`;
ALTER TABLE `sales` DROP KEY `idx_saler_radr`;
ALTER TABLE `sales` ADD KEY `IDX_SALES_RADDRESS` (`shipping_address`);
ALTER TABLE `sales` ADD CONSTRAINT `FK_SALES_RADDRESS` FOREIGN KEY (`shipping_address`) REFERENCES `raddress` (`id`);
ALTER TABLE `sales` DROP FOREIGN KEY `sales_ibfk_4`;
ALTER TABLE `sales` DROP KEY `idx_sale_pymt`;
ALTER TABLE `sales` ADD KEY `IDX_SALES_PAY_METHOD` (`pay_method`);
ALTER TABLE `sales` ADD CONSTRAINT `FK_SALES_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`);
ALTER TABLE `sales` DROP FOREIGN KEY `sales_ibfk_6`;
ALTER TABLE `sales` DROP KEY `workplace`;
ALTER TABLE `sales` ADD KEY `IDX_SALES_WORKPLACE` (`workplace`);
ALTER TABLE `sales` ADD CONSTRAINT `FK_SALES_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`);

ALTER TABLE `sales_detail` DROP FOREIGN KEY `sales_detail_ibfk_1`;
ALTER TABLE `sales_detail` DROP KEY `idx_sldt_sales`;
ALTER TABLE `sales_detail` ADD KEY `IDX_SALES_DETAIL_SALES` (`sales`);
ALTER TABLE `sales_detail` ADD CONSTRAINT `FK_SALES_DETAIL_SALES` FOREIGN KEY (`sales`) REFERENCES `sales` (`id`);
ALTER TABLE `sales_detail` DROP FOREIGN KEY `sales_detail_ibfk_2`;
ALTER TABLE `sales_detail` DROP KEY `idx_sldt_item`;
ALTER TABLE `sales_detail` ADD KEY `IDX_SALES_DETAIL_ITEM` (`item`);
ALTER TABLE `sales_detail` ADD CONSTRAINT `FK_SALES_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`);

ALTER TABLE `seller` DROP FOREIGN KEY `seller_ibfk_1`;
ALTER TABLE `seller` ADD CONSTRAINT `FK_SELLER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);

ALTER TABLE `session` DROP FOREIGN KEY `FK_SESSION_APPLICATION_ID`;
ALTER TABLE `session` DROP KEY `IDX_SESSION_APPLICATION_ID`;
ALTER TABLE `session` ADD KEY `IDX_SESSION_APPLICATION` (`application_id`);
ALTER TABLE `session` ADD CONSTRAINT `FK_SESSION_APPLICATION` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`);
ALTER TABLE `session` DROP FOREIGN KEY `FK_SESSION_USER_ID`;
ALTER TABLE `session` DROP KEY `IDX_SESSION_USER_ID`;
ALTER TABLE `session` ADD KEY `IDX_SESSION_USER` (`user_id`);
ALTER TABLE `session` ADD CONSTRAINT `FK_SESSION_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

ALTER TABLE `stock` DROP KEY `warehouse_item`;
ALTER TABLE `stock` ADD UNIQUE KEY `IDX_STOCK_WAREHOUSE_ITEM` (`warehouse`,`item`);
ALTER TABLE `stock` DROP FOREIGN KEY `stock_ibfk_1`;
ALTER TABLE `stock` DROP KEY `warehouse`;
ALTER TABLE `stock` ADD KEY `IDX_STOCK_WAREHOUSE` (`warehouse`);
ALTER TABLE `stock` ADD CONSTRAINT `FK_STOCK_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`);
ALTER TABLE `stock` DROP FOREIGN KEY `stock_ibfk_2`;
ALTER TABLE `stock` DROP KEY `item`;
ALTER TABLE `stock` ADD KEY `IDX_STOCK_ITEM` (`item`);
ALTER TABLE `stock` ADD CONSTRAINT `FK_STOCK_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`);

ALTER TABLE `supplier` DROP FOREIGN KEY `supplier_ibfk_1`;
ALTER TABLE `supplier` ADD CONSTRAINT `FK_SUPPLIER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);
ALTER TABLE `supplier` DROP FOREIGN KEY `supplier_ibfk_2`;
ALTER TABLE `supplier` DROP KEY `scope`;
ALTER TABLE `supplier` ADD KEY `IDX_SUPPLIER_SCOPE` (`scope`);
ALTER TABLE `supplier` ADD CONSTRAINT `FK_SUPPLIER_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);

ALTER TABLE `supplier_account` DROP FOREIGN KEY `supplier_account_ibfk_1`;
ALTER TABLE `supplier_account` DROP KEY `supplier`;
ALTER TABLE `supplier_account` ADD KEY `IDX_SUPPLIER_ACCOUNT_SUPPLIER` (`supplier`);
ALTER TABLE `supplier_account` ADD CONSTRAINT `FK_SUPPLIER_ACCOUNT_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`);

ALTER TABLE `survey_question` DROP FOREIGN KEY `FK_SURVEY_QUESTION_QUESTION`;
ALTER TABLE `survey_question` DROP KEY `question`;
ALTER TABLE `survey_question` ADD KEY `IDX_SURVEY_QUESTION_QUESTION` (`question`);
ALTER TABLE `survey_question` ADD CONSTRAINT `FK_SURVEY_QUESTION_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`);
ALTER TABLE `survey_question` DROP FOREIGN KEY `FK_SURVEY_QUESTION_SURVEY`;
ALTER TABLE `survey_question` DROP KEY `survey`;
ALTER TABLE `survey_question` ADD KEY `IDX_SURVEY_QUESTION_SURVEY` (`survey`);
ALTER TABLE `survey_question` ADD CONSTRAINT `FK_SURVEY_QUESTION_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`);

ALTER TABLE `survey_response` DROP FOREIGN KEY `FK_SURVERY_RESPONSE_MK_ACTION`;
ALTER TABLE `survey_response` DROP KEY `campaign_action`;
ALTER TABLE `survey_response` ADD KEY `IDX_SURVEY_RESPONSE_MK_ACTION` (`campaign_action`);
ALTER TABLE `survey_response` ADD CONSTRAINT `FK_SURVEY_RESPONSE_MK_ACTION` FOREIGN KEY (`campaign_action`) REFERENCES `mk_action` (`id`);
ALTER TABLE `survey_response` DROP FOREIGN KEY `FK_SURVEY_RESPONSE_SURVEY`;
ALTER TABLE `survey_response` DROP KEY `survey`;
ALTER TABLE `survey_response` ADD KEY `IDX_SURVEY_RESPONSE_SURVEY` (`survey`);
ALTER TABLE `survey_response` ADD CONSTRAINT `FK_SURVEY_RESPONSE_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`);
ALTER TABLE `survey_response` DROP FOREIGN KEY `FK_SURVEY_RESPONSE_TARGET`;
ALTER TABLE `survey_response` DROP KEY `target`;
ALTER TABLE `survey_response` ADD KEY `IDX_SURVEY_RESPONSE_TARGET` (`target`);
ALTER TABLE `survey_response` ADD CONSTRAINT `FK_SURVEY_RESPONSE_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`);
ALTER TABLE `survey_response` DROP FOREIGN KEY `FK_SURVEY_RESPONSE_USER`;
ALTER TABLE `survey_response` DROP KEY `user`;
ALTER TABLE `survey_response` ADD KEY `IDX_SURVEY_RESPONSE_USER` (`user`);
ALTER TABLE `survey_response` ADD CONSTRAINT `FK_SURVEY_RESPONSE_USER` FOREIGN KEY (`user`) REFERENCES `user` (`id`);

ALTER TABLE `survey_workflow` DROP FOREIGN KEY `FK_SURVEY_WORKFLOW_NEXT_SURVEY_QUESTION`;
ALTER TABLE `survey_workflow` DROP KEY `nextSurveyQuestion`;
ALTER TABLE `survey_workflow` ADD KEY `IDX_SURVEY_WORKFLOW_NEXT_SURVEY_QUESTION` (`nextSurveyQuestion`);
ALTER TABLE `survey_workflow` ADD CONSTRAINT `FK_SURVEY_WORKFLOW_NEXT_SURVEY_QUESTION` FOREIGN KEY (`nextSurveyQuestion`) REFERENCES `survey_question` (`id`);
ALTER TABLE `survey_workflow` DROP FOREIGN KEY `FK_SURVEY_WORKFLOW_QUESTION_VALUE`;
ALTER TABLE `survey_workflow` DROP KEY `questionValue`;
ALTER TABLE `survey_workflow` ADD KEY `IDX_SURVEY_WORKFLOW_QUESTION_VALUE` (`questionValue`);
ALTER TABLE `survey_workflow` ADD CONSTRAINT `FK_SURVEY_WORKFLOW_QUESTION_VALUE` FOREIGN KEY (`questionValue`) REFERENCES `question_value` (`id`);
ALTER TABLE `survey_workflow` DROP FOREIGN KEY `FK_SURVEY_WORKFLOW_SURVEY_QUESTION`;
ALTER TABLE `survey_workflow` DROP KEY `surveyQuestion`;
ALTER TABLE `survey_workflow` ADD KEY `IDX_SURVEY_WORKFLOW_SURVEY_QUESTION` (`surveyQuestion`);
ALTER TABLE `survey_workflow` ADD CONSTRAINT `FK_SURVEY_WORKFLOW_SURVEY_QUESTION` FOREIGN KEY (`surveyQuestion`) REFERENCES `survey_question` (`id`);

ALTER TABLE `target` DROP FOREIGN KEY `target_ibfk_1`;
ALTER TABLE `target` ADD CONSTRAINT `FK_TARGET_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);

ALTER TABLE `target_item` DROP FOREIGN KEY `target_item_fk`;
ALTER TABLE `target_item` DROP KEY `target`;
ALTER TABLE `target_item` ADD KEY `IDX_TARGET_ITEM_TARGET` (`target`);
ALTER TABLE `target_item` ADD CONSTRAINT `FK_TARGET_ITEM_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`);
ALTER TABLE `target_item` DROP FOREIGN KEY `target_item_fk1`;
ALTER TABLE `target_item` DROP KEY `item`;
ALTER TABLE `target_item` ADD KEY `IDX_TARGET_ITEM_ITEM` (`item`);
ALTER TABLE `target_item` ADD CONSTRAINT `FK_TARGET_ITEM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`);

ALTER TABLE `target_profile` DROP FOREIGN KEY `FK_TARGET_PROFILE_QUESTION`;
ALTER TABLE `target_profile` DROP KEY `question`;
ALTER TABLE `target_profile` ADD KEY `IDX_TARGET_PROFILE_QUESTION` (`question`);
ALTER TABLE `target_profile` ADD CONSTRAINT `FK_TARGET_PROFILE_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`);
ALTER TABLE `target_profile` DROP FOREIGN KEY `FK_TARGET_PROFILE_TARGET`;
ALTER TABLE `target_profile` DROP KEY `target`;
ALTER TABLE `target_profile` ADD KEY `IDX_TARGET_PROFILE_TARGET` (`target`);
ALTER TABLE `target_profile` ADD CONSTRAINT `FK_TARGET_PROFILE_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`);

ALTER TABLE `target_seller` DROP FOREIGN KEY `target_seller_fk`;
ALTER TABLE `target_seller` DROP KEY `target`;
ALTER TABLE `target_seller` ADD KEY `IDX_TARGET_SELLER_TARGET` (`target`);
ALTER TABLE `target_seller` ADD CONSTRAINT `FK_TARGET_SELLER_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`);
ALTER TABLE `target_seller` DROP FOREIGN KEY `target_seller_fk1`;
ALTER TABLE `target_seller` DROP KEY `seller`;
ALTER TABLE `target_seller` ADD KEY `IDX_TARGET_SELLER_SELLER` (`seller`);
ALTER TABLE `target_seller` ADD CONSTRAINT `FK_TARGET_SELLER_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`);

ALTER TABLE `tariff_catalogue` DROP FOREIGN KEY `tariff_catalogue_fk1`;
ALTER TABLE `tariff_catalogue` DROP KEY `tariff`;
ALTER TABLE `tariff_catalogue` ADD KEY `IDX_TARIFF_CATALOGUE_TARIFF` (`tariff`);
ALTER TABLE `tariff_catalogue` ADD CONSTRAINT `FK_TARIFF_CATALOGUE_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`);
ALTER TABLE `tariff_catalogue` DROP FOREIGN KEY `tariff_catalogue_fk2`;
ALTER TABLE `tariff_catalogue` DROP KEY `catalogue`;
ALTER TABLE `tariff_catalogue` ADD KEY `IDX_TARIFF_CATALOGUE_CATALOGUE` (`catalogue`);
ALTER TABLE `tariff_catalogue` ADD CONSTRAINT `FK_TARIFF_CATALOGUE_CATALOGUE` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`);

ALTER TABLE `tas_item` DROP KEY `idx_tas_item_public`;
ALTER TABLE `tas_item` ADD UNIQUE KEY `IDX_TAS_ITEM_PUBLIC_CODE` (`publicCode`);
ALTER TABLE `tas_item` DROP KEY `idx_tas_item_private`;
ALTER TABLE `tas_item` ADD UNIQUE KEY `IDX_TAS_ITEM_PRIVATE_CODE` (`privateCode`);
ALTER TABLE `tas_item` DROP FOREIGN KEY `tas_item_ibfk_1`;
ALTER TABLE `tas_item` DROP KEY `model`;
ALTER TABLE `tas_item` ADD KEY `IDX_TAS_ITEM_MODEL` (`model`);
ALTER TABLE `tas_item` ADD CONSTRAINT `FK_TAS_ITEM_MODEL` FOREIGN KEY (`model`) REFERENCES `model` (`id`);

ALTER TABLE `tax_account` DROP FOREIGN KEY `tax_account_ibfk_1`;
ALTER TABLE `tax_account` DROP KEY `tax`;
ALTER TABLE `tax_account` ADD KEY `IDX_TAX_ACCOUNT_TAX` (`tax`);
ALTER TABLE `tax_account` ADD CONSTRAINT `FK_TAX_ACCOUNT_TAX` FOREIGN KEY (`tax`) REFERENCES `tax` (`id`);

ALTER TABLE `tax_detail` DROP FOREIGN KEY `tax_detail_ibfk_1`;
ALTER TABLE `tax_detail` DROP KEY `tax`;
ALTER TABLE `tax_detail` ADD KEY `IDX_TAX_DETAIL_TAX` (`tax`);
ALTER TABLE `tax_detail` ADD CONSTRAINT `FK_TAX_DETAIL_TAX` FOREIGN KEY (`tax`) REFERENCES `tax` (`id`);

ALTER TABLE `user` ADD KEY `IDX_USER_REGISTRY` (`registry`);
ALTER TABLE `user` ADD CONSTRAINT `FK_USER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);

ALTER TABLE `user_scope` DROP FOREIGN KEY `user_scope_fk1`;
ALTER TABLE `user_scope` DROP KEY `scope`;
ALTER TABLE `user_scope` ADD KEY `IDX_USER_SCOPE_SCOPE` (`scope`);
ALTER TABLE `user_scope` ADD CONSTRAINT `FK_USER_SCOPE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);
ALTER TABLE `user_scope` DROP FOREIGN KEY `user_scope_fk`;
ALTER TABLE `user_scope` DROP KEY `user`;
ALTER TABLE `user_scope` ADD KEY `IDX_USER_SCOPE_USER` (`user_id`);
ALTER TABLE `user_scope` ADD CONSTRAINT `FK_USER_SCOPE_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

ALTER TABLE `user_workgroup` DROP FOREIGN KEY `user_workgroup_fk_1`;
ALTER TABLE `user_workgroup` DROP KEY `user`;
ALTER TABLE `user_workgroup` ADD KEY `IDX_USER_WORKGROUP_USER` (`user_id`);
ALTER TABLE `user_workgroup` ADD CONSTRAINT `FK_USER_WORKGROUP_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);
ALTER TABLE `user_workgroup` DROP FOREIGN KEY `user_workgroup_fk_2`;
ALTER TABLE `user_workgroup` DROP KEY `workgroup`;
ALTER TABLE `user_workgroup` ADD KEY `IDX_USER_WORKGROUP_WORKGROUP` (`workgroup`);
ALTER TABLE `user_workgroup` ADD CONSTRAINT `FK_USER_WORKGROUP_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`);

ALTER TABLE `warehouse_transfer` DROP KEY `series`;
ALTER TABLE `warehouse_transfer` ADD UNIQUE KEY `IDX_WAREHOUSE_TRANSFER_SERIES_NUMBER` (`series`,`number`);
ALTER TABLE `warehouse_transfer` DROP KEY `IDX_WT_ISSUE_TIME`;
ALTER TABLE `warehouse_transfer` ADD KEY `IDX_WAREHOUSE_TRANSFER_ISSUE_TIME` (`issue_time`);
ALTER TABLE `warehouse_transfer` DROP FOREIGN KEY `FK_WT_SOURCE`;
ALTER TABLE `warehouse_transfer` DROP KEY `IDX_WT_SOURCE`;
ALTER TABLE `warehouse_transfer` ADD KEY `IDX_WAREHOUSE_TRANSFER_SOURCE_WAREHOUSE` (`source_warehouse`);
ALTER TABLE `warehouse_transfer` ADD CONSTRAINT `FK_WAREHOUSE_TRANSFER_SOURCE_WAREHOUSE` FOREIGN KEY (`source_warehouse`) REFERENCES `warehouse` (`id`);
ALTER TABLE `warehouse_transfer` DROP FOREIGN KEY `FK_WT_TARGET`;
ALTER TABLE `warehouse_transfer` DROP KEY `IDX_WT_TARGET`;
ALTER TABLE `warehouse_transfer` ADD KEY `IDX_WAREHOUSE_TRANSFER_TARGET_WAREHOUSE` (`target_warehouse`);
ALTER TABLE `warehouse_transfer` ADD CONSTRAINT `FK_WAREHOUSE_TRANSFER_TARGET_WAREHOUSE` FOREIGN KEY (`target_warehouse`) REFERENCES `warehouse` (`id`);

ALTER TABLE `warehouse_transfer_detail` DROP FOREIGN KEY `FK_WTD_ITEM`;
ALTER TABLE `warehouse_transfer_detail` DROP KEY `IDX_WTD_ITEM`;
ALTER TABLE `warehouse_transfer_detail` ADD KEY `IDX_WAREHOUSE_TRANSFER_DETAIL_ITEM` (`item`);
ALTER TABLE `warehouse_transfer_detail` ADD CONSTRAINT `FK_WAREHOUSE_TRANSFER_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`);
ALTER TABLE `warehouse_transfer_detail` DROP FOREIGN KEY `FK_WTD_WT`;
ALTER TABLE `warehouse_transfer_detail` DROP KEY `IDX_WTD_WT`;
ALTER TABLE `warehouse_transfer_detail` ADD KEY `IDX_WAREHOUSE_TRANSFER_DETAIL_WAREHOUSE_TRANSFER` (`warehouse_transfer`);
ALTER TABLE `warehouse_transfer_detail` ADD CONSTRAINT `FK_WAREHOUSE_TRANSFER_DETAIL_WAREHOUSE_TRANSFER` FOREIGN KEY (`warehouse_transfer`) REFERENCES `warehouse_transfer` (`id`);

ALTER TABLE `web_info` DROP FOREIGN KEY `web_info_fk`;
ALTER TABLE `web_info` DROP KEY `company`;
ALTER TABLE `web_info` ADD KEY `IDX_WEB_INFO_COMPANY` (`company`);
ALTER TABLE `web_info` ADD CONSTRAINT `FK_WEB_INFO_COMPANY` FOREIGN KEY (`company`) REFERENCES `company` (`registry`);

ALTER TABLE `web_info_page_detail` DROP FOREIGN KEY `web_info_page_detail_fk1`;
ALTER TABLE `web_info_page_detail` DROP KEY `web_info_page`;
ALTER TABLE `web_info_page_detail` ADD KEY `IDX_WEB_INFO_PAGE_DETAIL_WEB_INFO_PAGE` (`web_info_page`);
ALTER TABLE `web_info_page_detail` ADD CONSTRAINT `FK_WEB_INFO_PAGE_DETAIL_WEB_INFO_PAGE` FOREIGN KEY (`web_info_page`) REFERENCES `web_info_page` (`id`);

ALTER TABLE `web_info_page_resource` DROP FOREIGN KEY `web_info_page_resource_fk1`;
ALTER TABLE `web_info_page_resource` DROP KEY `web_info_page`;
ALTER TABLE `web_info_page_resource` ADD KEY `IDX_WEB_INFO_PAGE_RESOURCE_WEB_INFO_PAGE` (`web_info_page`);
ALTER TABLE `web_info_page_resource` ADD CONSTRAINT `FK_WEB_INFO_PAGE_RESOURCE_WEB_INFO_PAGE` FOREIGN KEY (`web_info_page`) REFERENCES `web_info_page` (`id`);
ALTER TABLE `web_info_page_resource` DROP FOREIGN KEY `web_info_page_resource_fk2`;
ALTER TABLE `web_info_page_resource` DROP KEY `rattach`;
ALTER TABLE `web_info_page_resource` ADD KEY `IDX_WEB_INFO_PAGE_RESOURCE_RATTACH` (`rattach`);
ALTER TABLE `web_info_page_resource` ADD CONSTRAINT `FK_WEB_INFO_PAGE_RESOURCE_RATTACH` FOREIGN KEY (`rattach`) REFERENCES `rattach` (`id`);

ALTER TABLE `workactivity` DROP FOREIGN KEY `FK_WORKACTIVITY_ENTERPRISECCC`;
ALTER TABLE `workactivity` DROP KEY `IDX_WORKACTIVITY_ENTERPRISECCC`;
ALTER TABLE `workactivity` ADD KEY `IDX_WORKACTIVITY_ENTERPRISE_CCC` (`enterpriseCCC`);
ALTER TABLE `workactivity` ADD CONSTRAINT `FK_WORKACTIVITY_ENTERPRISE_CCC` FOREIGN KEY (`enterpriseCCC`) REFERENCES `enterprise_ccc` (`id`);
ALTER TABLE `workactivity` DROP FOREIGN KEY `workactivity_ibfk_1`;
ALTER TABLE `workactivity` DROP KEY `workplace`;
ALTER TABLE `workactivity` ADD KEY `IDX_WORKACTIVITY_WORKPLACE` (`workplace`);
ALTER TABLE `workactivity` ADD CONSTRAINT `FK_WORKACTIVITY_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`);

ALTER TABLE `workplace` DROP FOREIGN KEY `workplace_ibfk_1`;
ALTER TABLE `workplace` DROP KEY `address`;
ALTER TABLE `workplace` ADD KEY `IDX_WORKPLACE_RADDRESS` (`address`);
ALTER TABLE `workplace` ADD CONSTRAINT `FK_WORKPLACE_RADDRESS` FOREIGN KEY (`address`) REFERENCES `raddress` (`id`);


UPDATE `db_version` SET `version_number` = '6.20.2';

COMMIT;


SET @isPrint = (SELECT `value` FROM `app_param` WHERE `name` = 'APP_PRINT_HEADER_PARAM');
SET @isHeader = (SELECT COUNT(`value`) FROM `app_param` WHERE `name` = 'APP_PRINT_HEADER_PARAM' AND `value` = 'true');

INSERT INTO `app_param` (`name`, `value`) VALUES ('APP_PRINT_LOGO_PARAM',@isPrint) ON DUPLICATE KEY UPDATE `value` = `value`;
INSERT INTO `app_param` (`name`, `value`) VALUES ('APP_PRINT_NAME_PARAM',@isHeader) ON DUPLICATE KEY UPDATE `value` = `value`;
INSERT INTO `app_param` (`name`, `value`) VALUES ('APP_PRINT_NIF_PARAM',@isHeader) ON DUPLICATE KEY UPDATE `value` = `value`;
INSERT INTO `app_param` (`name`, `value`) VALUES ('APP_PRINT_ADDRESS_PARAM',@isHeader) ON DUPLICATE KEY UPDATE `value` = `value`;

COMMIT;
