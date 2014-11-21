# Database: aon_master
# Version: Actualizacion de la version 8.9.2 a la version 8.9.3.
# Created by: girazu
# Creation Date: 21/11/2014 09:50

BEGIN;

ALTER TABLE `offer` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `offer` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `offer` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `offer` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `offer_detail` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `offer_detail` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `offer_detail` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `offer_detail` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `sales` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `sales` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `sales` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `sales` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `sales_detail` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `sales_detail` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `sales_detail` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `sales_detail` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `purchase` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `purchase` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `purchase` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `purchase` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `purchase_detail` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `purchase_detail` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `purchase_detail` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `purchase_detail` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `delivery` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `delivery` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `delivery` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `delivery` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `delivery_detail` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `delivery_detail` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `delivery_detail` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `delivery_detail` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `income` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `income` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `income` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `income` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `income_detail` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `income_detail` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `income_detail` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `income_detail` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';


UPDATE `db_version` SET `version_number` = '8.9.3';

COMMIT;
