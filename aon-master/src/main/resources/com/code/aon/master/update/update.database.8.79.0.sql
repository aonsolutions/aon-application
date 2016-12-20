# Database: aon_master
# Version: Actualizacion de la version 8.79.0 a la version 8.81.0.
# Created by: girazu
# Creation Date: 11/11/2016 12:30

BEGIN;

ALTER TABLE `project_reservation` CHANGE `penalty_days` `penalty_value` varchar(4) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor de penalizacion (patron)';


UPDATE `db_version` SET `version_number` = '8.81.0';

COMMIT;
