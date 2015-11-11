# Database: aon_master
# Version: Actualizacion de la version 8.32.3 a la version 8.33.0.
# Created by: girazu
# Creation Date: 09/11/2015 12:50

BEGIN;

ALTER TABLE `catalogue` ADD `purchase` tinyint(1) NOT NULL default '0' COMMENT 'Indica si se trata de un Catalogo de Compras o Ventas' AFTER `name`;

ALTER TABLE `tariff` ADD `purchase` tinyint(1) NOT NULL default '0' COMMENT 'Indica si se trata de una Tarifa de Compras o Ventas' AFTER `name`;


UPDATE `db_version` SET `version_number` = '8.33.0';

COMMIT;

