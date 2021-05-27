# Database: aon_master
# Version: Actualizacion de la version 8.114.0 a la version 9.02.0.
# Created by: aibanez
# Creation Date: 24/08/2017 14:00

BEGIN;

UPDATE data_response_detail set data_value = '4' where data_variable = 'ufqdp1' and data_value='2';
UPDATE data_response_detail set data_value = '5' where data_variable = 'ufqdp1' and data_value='3';
UPDATE data_response_detail set data_value = '7' where data_variable = 'ufqdp1' and data_value='4';
UPDATE data_response_detail set data_value = '8' where data_variable = 'ufqdp1' and data_value='5';
UPDATE data_response_detail set data_value = '10' where data_variable = 'ufqdp1' and data_value='6';
UPDATE data_response_detail set data_value = '9' where data_variable = 'ufqdp1' and data_value='7';
UPDATE data_response_detail set data_value = '17' where data_variable = 'ufqdp1' and data_value='8';
UPDATE data_response_detail set data_value = '12' where data_variable = 'ufqdp1' and data_value='9';
UPDATE data_response_detail set data_value = '14' where data_variable = 'ufqdp1' and data_value='10';
UPDATE data_response_detail set data_value = '15' where data_variable = 'ufqdp1' and data_value='11';
UPDATE data_response_detail set data_value = '20' where data_variable = 'ufqdp1' and data_value='12';

UPDATE `db_version` SET `version_number` = '9.02.0';

COMMIT;
