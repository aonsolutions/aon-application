# Database: aon_master
# Version: Actualizacion de la version 8.26.2 a la version 8.28.0.
# Created by: girazu
# Creation Date: 15/07/2015 17:20

BEGIN;

ALTER TABLE `project_reservation_guest` ADD `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion';
ALTER TABLE `project_reservation_guest` ADD `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion';
ALTER TABLE `project_reservation_guest` ADD `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `project_reservation_guest` ADD `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion';


UPDATE `db_version` SET `version_number` = '8.28.0';

COMMIT;
