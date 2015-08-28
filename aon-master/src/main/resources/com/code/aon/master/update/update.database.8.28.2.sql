# Database: aon_master
# Version: Actualizacion de la version 8.28.2 a la version 8.28.3.
# Created by: eagirrezabal
# Creation Date: 28/08/2015 12:00

BEGIN;

ALTER TABLE `warehouse_transfer` MODIFY `source_id` int(4) default NULL COMMENT 'Identificador del origen';

UPDATE `warehouse_transfer` SET source = 1 WHERE inventory IS NOT NULL;

UPDATE `db_version` SET `version_number` = '8.28.3';

COMMIT;
