# Database: aon_master
# Version: Actualizacion de la version 8.64.0 a la version 8.65.0.
# Created by: girazu
# Creation Date: 08/08/2016 12:00

BEGIN;

ALTER TABLE `project_reservation_room` ADD `allotment_rate_code` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de Cupo' AFTER `item`;

ALTER TABLE `allotment` ADD `rate_code` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de Cupo' AFTER `hotel`;


UPDATE `db_version` SET `version_number` = '8.65.0';

COMMIT;

