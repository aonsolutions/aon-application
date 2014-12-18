# Database: aon_master
# Version: Actualizacion de la version 8.10.1 a la version 8.11.0.
# Created by: girazu
# Creation Date: 26/12/2014 19:35

BEGIN;

ALTER TABLE `rattach` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `rattach` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `rattach` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `rattach` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';


UPDATE `db_version` SET `version_number` = '8.11.0';

COMMIT;
