# Database: aon_master
# Version: Actualizacion de la version 8.71.0 a la version 8.72.0.
# Created by: eagirrezabal
# Creation Date: 19/10/2016 

BEGIN;

ALTER TABLE `purchase_detail` ADD `source` tinyint(2) DEFAULT '0' COMMENT 'Origen del Detalle de la Compra';
ALTER TABLE `purchase_detail` ADD `source_id` int(4) DEFAULT NULL COMMENT 'Identificador del Origen del Detalle de la Compra';

UPDATE `db_version` SET `version_number` = '8.72.0';

COMMIT;
