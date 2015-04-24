# Database: aon_master
# Version: Actualizacion de la version 8.21.0 a la version 8.21.1.
# Created by: rtrepiana

BEGIN;


UPDATE `geozone_irpf` SET `end_date`="2015-12-31" WHERE `end_date`="2014-12-31" AND `geozone_code`="31";


UPDATE `db_version` SET `version_number` = '8.21.1';

COMMIT;
