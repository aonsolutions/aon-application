# Database: aon_master
# Version: Actualizacion de la version 8.92.0 a la version 8.93.0.
# Created by: aibanez
# Creation Date: 24/02/2017 12:00

BEGIN;

ALTER TABLE `carrier_packing` ADD `comments` text COLLATE latin1_spanish_ci default NULL COMMENT 'Observaciones carrier packing' AFTER `driver_document`;

UPDATE `db_version` SET `version_number` = '8.93.0';

COMMIT;
