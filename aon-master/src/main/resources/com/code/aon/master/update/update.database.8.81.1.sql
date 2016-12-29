# Database: aon_master
# Version: Actualizacion de la version 8.81.1 a la version 8.82.0.
# Created by: girazu
# Creation Date: 29/12/2016 13:30

BEGIN;

DELETE FROM `customer_fee` WHERE `item` IS NULL;
ALTER TABLE `customer_fee` MODIFY `item` int(4) NOT NULL COMMENT 'Identificador del Articulo';


UPDATE `db_version` SET `version_number` = '8.82.0';

COMMIT;
