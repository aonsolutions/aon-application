# Database: aon_master
# Version: Actualizacion de la version 8.8.0 a la version 8.9.0.
# Created by: girazu
# Creation Date: 20/11/2014 18:50

BEGIN;

ALTER TABLE `account_entry` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `account_entry` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `account_entry` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `account_entry` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `account_entry_detail` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `account_entry_detail` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `account_entry_detail` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `account_entry_detail` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';


UPDATE `db_version` SET `version_number` = '8.9.0';

COMMIT;
