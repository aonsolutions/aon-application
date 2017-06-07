# Database: aon_master
# Version: Actualizacion de la version 8.105.0 a la version 8.107.0.
# Created by: girazu
# Creation Date: 07/06/2017 12:10

BEGIN;

ALTER TABLE `data_attach` ADD KEY `IDX_DATA_ATTACH_SOURCE` (`source_id`, `source`);


UPDATE `db_version` SET `version_number` = '8.107.0';

COMMIT;
