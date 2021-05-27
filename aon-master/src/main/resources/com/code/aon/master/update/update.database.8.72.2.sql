# Database: aon_master
# Version: Actualizacion de la version 8.72.2 a la version 8.75.0.
# Created by: girazu
# Creation Date: 08/11/2016 18:30

BEGIN;

ALTER TABLE `project_reservation` ADD `token` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Token de preautorizacion de cobro' AFTER `credit_card_cvv`;
ALTER TABLE `project_reservation` ADD `penalty_amount` double(15,2) DEFAULT '0.00' COMMENT 'Importe de penalizacion' AFTER `penalty_days`;


UPDATE `db_version` SET `version_number` = '8.75.0';

COMMIT;
