# Database: aon_master
# Version: Actualizacion de la version 8.109.0 a la version 8.114.0.
# Created by: aibanez
# Creation Date: 04/08/2017 12:00

BEGIN;

UPDATE data_response_detail set data_value = '12' where data_variable = 'ufqdp1' and data_value='11';

UPDATE `db_version` SET `version_number` = '8.114.0';

COMMIT;
