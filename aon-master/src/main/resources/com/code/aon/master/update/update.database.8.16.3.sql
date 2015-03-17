# Database: aon_master
# Version: Actualizacion de la version 8.16.3 a la version 8.17.0.
# Created by: girazu
# Creation Date: 12/03/2015 10:15

BEGIN;

ALTER TABLE `pos` ADD `price_editable` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el precio es editable' AFTER `customer`;
ALTER TABLE `pos` ADD `discount_editable` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el descuento es editable' AFTER `price_editable`;
UPDATE `pos` SET `discount_editable` = 1;


UPDATE `db_version` SET `version_number` = '8.17.0';

COMMIT;
