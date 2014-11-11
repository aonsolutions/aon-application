# Database: aon_master
# Version: Actualizacion de la version 8.5.0 a la version 8.8.0.
# Created by: rtrepiana
# Creation Date: 11/11/2014

BEGIN;

UPDATE `db_version` SET `version_number` = '8.8.0';

COMMIT;
