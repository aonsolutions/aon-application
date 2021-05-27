# Database: aon_master
# Version: Actualizacion de la version 8.11.1 a la version 8.12.0.
# Created by: girazu
# Creation Date: 22/12/2014 17:20

BEGIN;

ALTER TABLE `pos_shift` ADD `imbalance` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si existen descuadres en el Turno' AFTER `initial_amount`;


UPDATE `db_version` SET `version_number` = '8.12.0';

COMMIT;
