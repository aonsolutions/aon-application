# Database: aon_master
# Version: Actualizacion de la version 8.83.0 a la version 8.84.0.
# Created by: girazu
# Creation Date: 11/01/2017 09:20

BEGIN;

DELETE FROM `customer_fee` WHERE `item` IS NULL;
ALTER TABLE `customer_fee` MODIFY `item` int(4) NOT NULL COMMENT 'Identificador del Articulo';


UPDATE `db_version` SET `version_number` = '8.84.0';

COMMIT;
