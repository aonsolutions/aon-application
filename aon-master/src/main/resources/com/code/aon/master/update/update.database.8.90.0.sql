# Database: aon_master
# Version: Actualizacion de la version 8.90.0 a la version 8.91.0.
# Created by: girazu
# Creation Date: 13/02/2017 11:50

BEGIN;

ALTER TABLE `project_reservation` MODIFY `code` varchar(48) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Localizador de la Reserva';

ALTER TABLE `reservation_request` MODIFY `code` varchar(48) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Localizador';


UPDATE `db_version` SET `version_number` = '8.91.0';

COMMIT;
