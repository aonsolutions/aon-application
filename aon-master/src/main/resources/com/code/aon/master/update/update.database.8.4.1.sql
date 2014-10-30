# Database: aon_master
# Version: Actualizacion de la version 8.4.1 a la version 8.5.0.
# Created by: girazu
# Creation Date: 30/10/2014 12:00

BEGIN;

ALTER TABLE `project` DROP `dossier`;


UPDATE `db_version` SET `version_number` = '8.5.0';

COMMIT;
