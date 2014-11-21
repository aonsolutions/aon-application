# Database: aon_master
# Version: Actualizacion de la version 8.9.0 a la version 8.9.1.
# Created by: girazu
# Creation Date: 21/11/2014 09:45

BEGIN;

ALTER TABLE `invoice_detail` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `invoice_detail` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `invoice_detail` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `invoice_detail` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `fbatch` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `fbatch` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `fbatch` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `fbatch` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `fbatch_detail` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `fbatch_detail` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `fbatch_detail` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `fbatch_detail` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';


UPDATE `db_version` SET `version_number` = '8.9.1';

COMMIT;
