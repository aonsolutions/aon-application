# Database: aon_master
# Version: Actualizacion de la version 8.25.2 a la version 8.25.3.
# Created by: girazu
# Creation Date: 18/06/2015 18:45

BEGIN;

ALTER TABLE `project_reservation` CHANGE `cancelation_user` `cancellation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de cancelacion';
ALTER TABLE `project_reservation` CHANGE `cancelation_date` `cancellation_date` datetime DEFAULT NULL COMMENT 'Fecha de cancelacion';
ALTER TABLE `project_reservation` ADD `credit_card_holder` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Titular de la tarjeta de credito' AFTER `bank_transaction`;
ALTER TABLE `project_reservation` ADD `credit_card_number` varchar(24) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de la tarjeta de credito' AFTER `credit_card_holder`;
ALTER TABLE `project_reservation` ADD `credit_card_expiration` varchar(4) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mes y año de expiracion de la tarjeta de credito' AFTER `credit_card_number`;
ALTER TABLE `project_reservation` ADD `credit_card_cvv` varchar(8) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de seguridad de la tarjeta de credito' AFTER `credit_card_expiration`;

UPDATE `db_version` SET `version_number` = '8.25.3';

COMMIT;
