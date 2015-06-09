# Database: aon_master
# Version: Actualizacion de la version 8.24.0 a la version 8.25.0.
# Created by: girazu
# Creation Date: 09/06/2015 18:35

BEGIN;

ALTER TABLE `asset_activity` ADD KEY `IDX_ASSET_ACTIVITY_DATE` (`date`);


UPDATE `db_version` SET `version_number` = '8.25.0';

COMMIT;
