# Database: aon_master
# Version: Actualizacion de la version 8.32.2 a la version 8.32.3.
# Created by: girazu
# Creation Date: 02/11/2015 12:50

BEGIN;

ALTER TABLE `product` ADD `kind` tinyint(2) NOT NULL default '0' COMMENT 'Clase de Producto' AFTER `code`;

ALTER TABLE `inventory` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `inventory` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `inventory` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `inventory` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `inventory_detail` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `inventory_detail` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `inventory_detail` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `inventory_detail` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `warehouse_transfer` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `warehouse_transfer` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `warehouse_transfer` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `warehouse_transfer` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `warehouse_transfer_detail` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `warehouse_transfer_detail` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `warehouse_transfer_detail` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `warehouse_transfer_detail` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';


UPDATE `db_version` SET `version_number` = '8.32.3';

COMMIT;

