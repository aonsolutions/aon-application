# Database: aon_master
# Version: Actualizacion de la version 8.94.0 a la version 8.94.1.
# Created by: girazu
# Creation Date: 08/03/2017 12:25

BEGIN;

ALTER TABLE `project_reservation` ADD `penalty_date` datetime DEFAULT NULL COMMENT 'Fecha de penalizacion' AFTER `penalty_amount`;


UPDATE `db_version` SET `version_number` = '8.94.1';

COMMIT;
