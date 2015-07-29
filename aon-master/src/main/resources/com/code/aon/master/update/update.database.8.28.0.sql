# Database: aon_master
# Version: Actualizacion de la version 8.28.0 a la version 8.28.1.
# Created by: girazu
# Creation Date: 28/07/2015 14:00

BEGIN;

ALTER TABLE `project_reservation` ADD `penalty_days` int(4) default NULL COMMENT 'Dias de penalizacion' AFTER `credit_card_cvv`;


UPDATE `db_version` SET `version_number` = '8.28.1';

COMMIT;
