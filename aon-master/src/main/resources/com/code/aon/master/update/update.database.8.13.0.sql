# Database: aon_master
# Version: Actualizacion de la version 8.13.0 a la version 8.13.1.
# Created by: girazu
# Creation Date: 20/01/2015 12:15

BEGIN;

ALTER TABLE `tax` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `tax` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `tax` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `tax` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `tax_detail` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `tax_detail` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `tax_detail` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `tax_detail` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `account_period` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `account_period` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `account_period` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `account_period` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';


UPDATE `db_version` SET `version_number` = '8.13.1';

COMMIT;
