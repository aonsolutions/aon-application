# Database: aon_master
# Version: Actualizacion de la version 8.9.3 a la version 8.9.4.
# Created by: girazu
# Creation Date: 21/11/2014 09:50

BEGIN;

ALTER TABLE `customer` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `customer` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `customer` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `customer` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `supplier` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `supplier` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `supplier` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `supplier` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `creditor` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `creditor` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `creditor` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `creditor` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `target` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `target` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `target` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `target` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `invoicing_group` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `invoicing_group` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `invoicing_group` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `invoicing_group` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `product` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `product` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `product` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `product` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `item` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `item` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `item` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `item` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';


UPDATE `db_version` SET `version_number` = '8.9.4';

COMMIT;
