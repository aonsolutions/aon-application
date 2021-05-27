# Database: aon_master
# Version: Actualizacion de la version 8.23.3 a la version 8.23.4.
# Created by: girazu
# Creation Date: 21/05/2015 12:50

BEGIN;

ALTER TABLE `survey_response` MODIFY `user` int(4) default NULL COMMENT 'Identificador del Usuario';

ALTER TABLE `warehouse_transfer` ADD `inventory` int(4) default NULL COMMENT 'Identificador del Inventario';
ALTER TABLE `warehouse_transfer` ADD KEY `IDX_WAREHOUSE_TRANSFER_INVENTORY` (`inventory`);
ALTER TABLE `warehouse_transfer` ADD CONSTRAINT `FK_WAREHOUSE_TRANSFER_INVENTORY` FOREIGN KEY (`inventory`) REFERENCES `inventory` (`id`);

ALTER TABLE `inventory` ADD `status` tinyint(2) default '0' COMMENT 'Estado del Inventario';


UPDATE `db_version` SET `version_number` = '8.23.4';

COMMIT;
