# Database: aon_master
# Version: Actualizacion de la version 8.13.5 a la version 8.15.0.
# Created by: girazu
# Creation Date: 10/02/2015 17:35

BEGIN;

ALTER TABLE `reservation_request_room` ADD `agreed_price` double(15,2) DEFAULT '0.00' COMMENT 'Importe Pactado' AFTER `total_price`;


UPDATE `db_version` SET `version_number` = '8.15.0';

COMMIT;
