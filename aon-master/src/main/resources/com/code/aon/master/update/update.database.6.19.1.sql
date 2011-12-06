# Database: aon_master
# Version: Actualizacion de la version 6.19.1 a la version 6.19.2.
# Created by: girazu
# Creation Date: 02/12/2011 11:25
# Comentarios: BORRADO DE TABLAS Y COLUMNAS OBSOLETAS.


BEGIN;

ALTER TABLE `employee` DROP FOREIGN KEY `FK_EMPLOYEE_WORKACTIVITY`;  

ALTER TABLE `employee` DROP KEY `IDX_EMPLOYEE_WORKACTIVITY`; 

ALTER TABLE `employee` DROP `workactivity`; 

ALTER TABLE `employee` RENAME `instructor`;

ALTER TABLE `series` DROP FOREIGN KEY `series_fk`;

ALTER TABLE `series` DROP KEY `workplace`;

ALTER TABLE `series` DROP `workplace`;

ALTER TABLE `customer` DROP `taxfree`;

ALTER TABLE `supplier` DROP FOREIGN KEY `supplier_fk`;

ALTER TABLE `supplier` DROP KEY `segment`;

ALTER TABLE `supplier` DROP `segment`;

ALTER TABLE `sales` DROP FOREIGN KEY `sales_ibfk_5`;

ALTER TABLE `sales` DROP KEY `pos`;

ALTER TABLE `sales` DROP `pos`;

DROP TABLE `contact`;

DROP TABLE `expenditures`;

DROP TABLE `expenditures_items`;

DROP TABLE `resource`;

DROP TABLE `curriculum`;

DROP TABLE `scale_relation`;

DROP TABLE `scale`;

DROP TABLE `composition_expense`;

DROP TABLE `composition_detail`;

DROP TABLE `composition`;

DROP TABLE `production_expense`;

DROP TABLE `production_detail`;

DROP TABLE `production`;

DROP TABLE `supplier_segment`;

DROP TABLE `account_summary`;

DROP TABLE `item_pos`;

DROP TABLE `pos`;

DROP TABLE `support_order_insurance`;

DROP TABLE `tas_offer`;

DROP TABLE `tas_delivery`;

DROP TABLE `support_order`;

DROP TABLE `appraiser`;

DROP TABLE `delivery_detail_labour`;

DROP TABLE `sales_purchase`;

DROP TABLE `leasing_account`;

DROP TABLE `leasing`;

DROP TABLE `department`;


UPDATE `db_version` SET `version_number` = '6.19.2';

COMMIT;
