# Database: aon_master
# Version: Actualizacion de la version 6.21.2 a la version 6.21.3.
# Created by: girazu
# Creation Date: 09/01/2012 15:40
# Comentarios: CREACION DE LA TABLA DOMAIN Y EL CAMPO DOMAIN EN TODAS LAS TABLAS.


BEGIN;

ALTER TABLE `system_cost` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `system_cost` ADD KEY `IDX_SYSTEM_COST_DOMAIN` (`domain`);
ALTER TABLE `system_cost` ADD CONSTRAINT `FK_SYSTEM_COST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `system_data` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `system_data` ADD KEY `IDX_SYSTEM_DATA_DOMAIN` (`domain`);
ALTER TABLE `system_data` ADD CONSTRAINT `FK_SYSTEM_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `system_deduction` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `system_deduction` ADD KEY `IDX_SYSTEM_DEDUCTION_DOMAIN` (`domain`);
ALTER TABLE `system_deduction` ADD CONSTRAINT `FK_SYSTEM_DEDUCTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `system_payment` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `system_payment` ADD KEY `IDX_SYSTEM_PAYMENT_DOMAIN` (`domain`);
ALTER TABLE `system_payment` ADD CONSTRAINT `FK_SYSTEM_PAYMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `target` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `registry`;
ALTER TABLE `target` ADD KEY `IDX_TARGET_DOMAIN` (`domain`);
ALTER TABLE `target` ADD CONSTRAINT `FK_TARGET_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `target_item` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `target_item` ADD KEY `IDX_TARGET_ITEM_DOMAIN` (`domain`);
ALTER TABLE `target_item` ADD CONSTRAINT `FK_TARGET_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `target_profile` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `target_profile` ADD KEY `IDX_TARGET_PROFILE_DOMAIN` (`domain`);
ALTER TABLE `target_profile` ADD CONSTRAINT `FK_TARGET_PROFILE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `target_seller` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `target_seller` ADD KEY `IDX_TARGET_SELLER_DOMAIN` (`domain`);
ALTER TABLE `target_seller` ADD CONSTRAINT `FK_TARGET_SELLER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `target_supplier` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `target_supplier` ADD KEY `IDX_TARGET_SUPPLIER_DOMAIN` (`domain`);
ALTER TABLE `target_supplier` ADD CONSTRAINT `FK_TARGET_SUPPLIER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `tariff` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `tariff` ADD KEY `IDX_TARIFF_DOMAIN` (`domain`);
ALTER TABLE `tariff` ADD CONSTRAINT `FK_TARIFF_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `tariff_catalogue` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `tariff_catalogue` ADD KEY `IDX_TARIFF_CATALOGUE_DOMAIN` (`domain`);
ALTER TABLE `tariff_catalogue` ADD CONSTRAINT `FK_TARIFF_CATALOGUE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `tas_item` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `tas_item` ADD KEY `IDX_TAS_ITEM_DOMAIN` (`domain`);
ALTER TABLE `tas_item` ADD CONSTRAINT `FK_TAS_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `tas_item` DROP KEY `IDX_TAS_ITEM_PUBLIC_CODE`;
ALTER TABLE `tas_item` ADD UNIQUE KEY `IDX_UNQ_TAS_ITEM_DOMAIN_PUBLIC_CODE` (`domain`,`publicCode`);
ALTER TABLE `tas_item` DROP KEY `IDX_TAS_ITEM_PRIVATE_CODE`;
ALTER TABLE `tas_item` ADD UNIQUE KEY `IDX_UNQ_TAS_ITEM_DOMAIN_PRIVATE_CODE` (`domain`,`privateCode`);

ALTER TABLE `task` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `task` ADD KEY `IDX_TASK_DOMAIN` (`domain`);
ALTER TABLE `task` ADD CONSTRAINT `FK_TASK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `task_holder` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `registry`;
ALTER TABLE `task_holder` ADD KEY `IDX_TASK_HOLDER_DOMAIN` (`domain`);
ALTER TABLE `task_holder` ADD CONSTRAINT `FK_TASK_HOLDER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `task_holder_workgroup` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `task_holder_workgroup` ADD KEY `IDX_TASK_HOLDER_WORKGROUP_DOMAIN` (`domain`);
ALTER TABLE `task_holder_workgroup` ADD CONSTRAINT `FK_TASK_HOLDER_WORKGROUP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `tax` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `tax` ADD KEY `IDX_TAX_DOMAIN` (`domain`);
ALTER TABLE `tax` ADD CONSTRAINT `FK_TAX_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `tax_account` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `tax_account` ADD KEY `IDX_TAX_ACCOUNT_DOMAIN` (`domain`);
ALTER TABLE `tax_account` ADD CONSTRAINT `FK_TAX_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `tax_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `tax_detail` ADD KEY `IDX_TAX_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `tax_detail` ADD CONSTRAINT `FK_TAX_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `user` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `user` ADD KEY `IDX_USER_DOMAIN` (`domain`);
ALTER TABLE `user` ADD CONSTRAINT `FK_USER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `user_scope` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `user_scope` ADD KEY `IDX_USER_SCOPE_DOMAIN` (`domain`);
ALTER TABLE `user_scope` ADD CONSTRAINT `FK_USER_SCOPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `user_workgroup` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `user_workgroup` ADD KEY `IDX_USER_WORKGROUP_DOMAIN` (`domain`);
ALTER TABLE `user_workgroup` ADD CONSTRAINT `FK_USER_WORKGROUP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `warehouse` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `warehouse` ADD KEY `IDX_WAREHOUSE_DOMAIN` (`domain`);
ALTER TABLE `warehouse` ADD CONSTRAINT `FK_WAREHOUSE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `warehouse_transfer` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `warehouse_transfer` ADD KEY `IDX_WAREHOUSE_TRANSFER_DOMAIN` (`domain`);
ALTER TABLE `warehouse_transfer` ADD CONSTRAINT `FK_WAREHOUSE_TRANSFER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `warehouse_transfer` DROP KEY `IDX_WAREHOUSE_TRANSFER_SERIES_NUMBER`;
ALTER TABLE `warehouse_transfer` ADD UNIQUE KEY `IDX_UNQ_WAREHOUSE_TRANSFER_DOMAIN_SERIES_NUMBER` (`domain`,`series`,`number`);

ALTER TABLE `warehouse_transfer_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `warehouse_transfer_detail` ADD KEY `IDX_WAREHOUSE_TRANSFER_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `warehouse_transfer_detail` ADD CONSTRAINT `FK_WAREHOUSE_TRANSFER_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `web_info` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `web_info` ADD KEY `IDX_WEB_INFO_DOMAIN` (`domain`);
ALTER TABLE `web_info` ADD CONSTRAINT `FK_WEB_INFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `web_info_page` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `web_info_page` ADD KEY `IDX_WEB_INFO_PAGE_DOMAIN` (`domain`);
ALTER TABLE `web_info_page` ADD CONSTRAINT `FK_WEB_INFO_PAGE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `web_info_page_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `web_info_page_detail` ADD KEY `IDX_WEB_INFO_PAGE_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `web_info_page_detail` ADD CONSTRAINT `FK_WEB_INFO_PAGE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `web_info_page_resource` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `web_info_page_resource` ADD KEY `IDX_WEB_INFO_PAGE_RESOURCE_DOMAIN` (`domain`);
ALTER TABLE `web_info_page_resource` ADD CONSTRAINT `FK_WEB_INFO_PAGE_RESOURCE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `web_info_style` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `web_info_style` ADD KEY `IDX_WEB_INFO_STYLE_DOMAIN` (`domain`);
ALTER TABLE `web_info_style` ADD CONSTRAINT `FK_WEB_INFO_STYLE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `workactivity` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `workactivity` ADD KEY `IDX_WORKACTIVITY_DOMAIN` (`domain`);
ALTER TABLE `workactivity` ADD CONSTRAINT `FK_WORKACTIVITY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `workgroup` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `workgroup` ADD KEY `IDX_WORKGROUP_DOMAIN` (`domain`);
ALTER TABLE `workgroup` ADD CONSTRAINT `FK_WORKGROUP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `workplace` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `workplace` ADD KEY `IDX_WORKPLACE_DOMAIN` (`domain`);
ALTER TABLE `workplace` ADD CONSTRAINT `FK_WORKPLACE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);


UPDATE `db_version` SET `version_number` = '6.21.3';

COMMIT;
