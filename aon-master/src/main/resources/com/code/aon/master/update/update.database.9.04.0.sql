# Database: aon_master
# Version: Actualizacion de la version 9.04.0 a la version 9.04.1.
# Created by: aibanez
# Creation Date: 12/09/2017 17:30

BEGIN;

UPDATE carrier_packing set status = 3 where status = 1;
UPDATE carrier_packing set status = 1 where status = 2;

UPDATE `db_version` SET `version_number` = '9.04.1';

COMMIT;
