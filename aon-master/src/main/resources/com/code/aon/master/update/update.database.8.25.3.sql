# Database: aon_master
# Version: Actualizacion de la version 8.25.3 a la version 8.26.0.
# Created by: girazu
# Creation Date: 19/06/2015 11:45

BEGIN;

ALTER TABLE `project_reservation` MODIFY `credit_card_holder` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Titular de la tarjeta de credito';
ALTER TABLE `project_reservation` MODIFY `credit_card_number` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de la tarjeta de credito';
ALTER TABLE `project_reservation` CHANGE `credit_card_expiration` `credit_card_expiration_month` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mes de expiracion de la tarjeta de credito';
ALTER TABLE `project_reservation` ADD `credit_card_expiration_year` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Año de expiracion de la tarjeta de credito' AFTER `credit_card_expiration_month`;
ALTER TABLE `project_reservation` MODIFY `credit_card_cvv` varchar(24) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de seguridad de la tarjeta de credito';


UPDATE `db_version` SET `version_number` = '8.26.0';

COMMIT;
