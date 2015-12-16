# Database: aon_master
# Version: Actualizacion de la version 8.35.0 a la version 8.36.0.
# Created by: girazu
# Creation Date: 11/12/2015 13:15

BEGIN;

ALTER TABLE `reservation_request_guest` ADD `number` varchar(12) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero' AFTER `address`;
ALTER TABLE `reservation_request_guest` ADD `address2` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Segunda parte de la Direccion' AFTER `number`;


UPDATE `db_version` SET `version_number` = '8.36.0';

COMMIT;
