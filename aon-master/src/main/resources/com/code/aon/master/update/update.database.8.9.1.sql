# Database: aon_master
# Version: Actualizacion de la version 8.9.1 a la version 8.9.2.
# Created by: girazu
# Creation Date: 21/11/2014 09:45

BEGIN;

ALTER TABLE `finance` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `finance` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `finance` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `finance` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `finance_tracking` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `finance_tracking` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `finance_tracking` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `finance_tracking` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `proposal` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `proposal` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `proposal` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `proposal` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `proposal_detail` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `proposal_detail` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `proposal_detail` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `proposal_detail` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';


UPDATE `db_version` SET `version_number` = '8.9.2';

COMMIT;
