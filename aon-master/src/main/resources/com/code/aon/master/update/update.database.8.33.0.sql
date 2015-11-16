# Database: aon_master
# Version: Actualizacion de la version 8.33.0 a la version 8.33.1.
# Created by: girazu
# Creation Date: 13/11/2015 13:45

BEGIN;

ALTER TABLE `supplier` ADD `tariff` int(4) DEFAULT NULL COMMENT 'Tarifa asociada al Proveedor' AFTER `domain`;
ALTER TABLE `supplier` ADD KEY `IDX_SUPPLIER_TARIFF` (`tariff`);
ALTER TABLE `supplier` ADD CONSTRAINT `FK_SUPPLIER_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`);


UPDATE `db_version` SET `version_number` = '8.33.1';

COMMIT;

