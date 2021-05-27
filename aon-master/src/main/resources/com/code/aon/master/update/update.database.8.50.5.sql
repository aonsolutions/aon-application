# Database: aon_master
# Version: Actualizacion de la version 8.50.5 a la version 8.54,0.
# Created by: rtrepiana

BEGIN;

UPDATE `payment_concept` SET `code` = 'GARANTIZADO' WHERE  `type` = 55  AND domain = 0;

UPDATE `payment_concept` SET `description` = REPLACE(`description`, 'COMÚM', 'COMÚN' ) WHERE  `type` = 55 AND `domain` = 0;

UPDATE `db_version` SET `version_number` = '8.54.0';

COMMIT;
