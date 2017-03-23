# Database: aon_master
# Version: Actualizacion de la version 8.94.1 a la version 8.96.0.
# Created by: girazu
# Creation Date: 16/03/2017 15:50

BEGIN;

ALTER TABLE `project_reservation` DROP `credit_card_holder`;
ALTER TABLE `project_reservation` DROP `credit_card_cvv`;
UPDATE `project_reservation` SET `credit_card_number` = NULL;
ALTER TABLE `project_reservation` MODIFY `credit_card_number` varchar(4) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Ultimos 4 numeros de la tarjeta de credito';
UPDATE `project_reservation` SET `credit_card_expiration_month` = NULL;
ALTER TABLE `project_reservation` MODIFY `credit_card_expiration_month` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mes de expiracion de la tarjeta de credito';
UPDATE `project_reservation` SET `credit_card_expiration_year` = NULL;
ALTER TABLE `project_reservation` MODIFY `credit_card_expiration_year` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Año de expiracion de la tarjeta de credito';


UPDATE `db_version` SET `version_number` = '8.96.0';

COMMIT;
