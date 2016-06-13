# Database: aon_master
# Version: Actualizacion de la version 8.57.1 a la version 8.58.0.
# Created by: girazu
# Creation Date: 13/06/2016 17:00

BEGIN;

ALTER TABLE `pos_shift` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `pos_shift` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `pos_shift` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `pos_shift` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `pos_shift_count` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `pos_shift_count` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `pos_shift_count` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `pos_shift_count` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';


UPDATE `db_version` SET `version_number` = '8.58.0';

COMMIT;

