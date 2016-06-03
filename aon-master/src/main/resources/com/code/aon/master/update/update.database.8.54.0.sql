# Database: aon_master
# Version: Actualizacion de la version 8.54.0 a la version 8.56.0.
# Created by: rtrepiana

BEGIN;

UPDATE `payment_concept` SET `description` = REPLACE(`description`,'ANTIGA?EDAD','ANTIGÜEDAD') WHERE `code` = 'ANTIGUEDAD';

UPDATE `system_data` SET `expression` = REPLACE(`expression`,'ANTIGA?EDAD','ANTIG&Uuml;EDAD') WHERE `name` = 'ANTIGUEDAD_HELP';

UPDATE `system_payment` SET `expression` = REPLACE(`expression`,'(15 - DIAS_PREAVISO)', 'MAX((15 - DIAS_PREAVISO),0)')  WHERE `salary_type`= 2;

UPDATE `db_version` SET `version_number` = '8.56.0';

COMMIT;
