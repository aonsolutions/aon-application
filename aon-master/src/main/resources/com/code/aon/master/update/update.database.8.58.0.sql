# Database: aon_master
# Version: Actualizacion de la version 8.58.0 a la version 8.59.0.
# Created by: girazu
# Creation Date: 22/06/2016 15:30

BEGIN;

ALTER TABLE `hotel` ADD `tourist_tax` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Hotel aplica Tasa turistica o no' AFTER `police_counter`;

ALTER TABLE `project_reservation` ADD `tourist_tax_free` tinyint(2) DEFAULT NULL COMMENT 'Tipo de exencion de la Tasa turistica' AFTER `penalty_days`;


UPDATE `db_version` SET `version_number` = '8.59.0';

COMMIT;

