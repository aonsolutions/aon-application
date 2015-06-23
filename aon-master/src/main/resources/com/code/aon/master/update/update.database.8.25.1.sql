# Database: aon_master
# Version: Actualizacion de la version 8.25.1 a la version 8.25.2.
# Created by: girazu
# Creation Date: 17/06/2015 12:50

BEGIN;

ALTER TABLE `project_reservation` ADD `prepay` tinyint(1) DEFAULT '0' COMMENT 'Indica si es un prepago' AFTER `early_check_out`;
ALTER TABLE `project_reservation` ADD `bank_transaction` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de transaccion bancaria' AFTER `prepay`;
ALTER TABLE `project_reservation` ADD `cancelation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de cancelacion';
ALTER TABLE `project_reservation` ADD `cancelation_date` datetime DEFAULT NULL COMMENT 'Fecha de cancelacion';

UPDATE `db_version` SET `version_number` = '8.25.2';

COMMIT;
