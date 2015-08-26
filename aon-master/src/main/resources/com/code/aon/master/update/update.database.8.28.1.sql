# Database: aon_master
# Version: Actualizacion de la version 8.28.1 a la version 8.28.2.
# Created by: eagirrezabal
# Creation Date: 26/08/2015 12:00

BEGIN;

ALTER TABLE `warehouse_transfer` ADD `source` tinyint(2) NOT NULL default '0' COMMENT 'Origen';
ALTER TABLE `warehouse_transfer` ADD `source_id` int(4) NOT NULL default '0' COMMENT 'Identificador del origen';

UPDATE `warehouse_transfer` SET source_id = inventory WHERE inventory IS NOT NULL;
UPDATE `warehouse_transfer` SET source = 0 WHERE inventory IS NOT NULL;

UPDATE `db_version` SET `version_number` = '8.28.2';

COMMIT;
