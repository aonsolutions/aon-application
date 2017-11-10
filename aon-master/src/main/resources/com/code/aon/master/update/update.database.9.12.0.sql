# Database: aon_master
# Version: Actualizacion de la version 9.12.0 a la version 9.13.0.
# Created by: aibanez
# Creation Date: 10/11/2017 10:00

BEGIN;

UPDATE data_response_detail set data_value = '23' where data_variable = 'ufqdp1' and data_value='21';
UPDATE data_response_detail set data_value = '22' where data_variable = 'ufqdp1' and data_value='20';
UPDATE data_response_detail set data_value = '21' where data_variable = 'ufqdp1' and data_value='19';
UPDATE data_response_detail set data_value = '20' where data_variable = 'ufqdp1' and data_value='18';
UPDATE data_response_detail set data_value = '19' where data_variable = 'ufqdp1' and data_value='17';
UPDATE data_response_detail set data_value = '18' where data_variable = 'ufqdp1' and data_value='16';
UPDATE data_response_detail set data_value = '17' where data_variable = 'ufqdp1' and data_value='15';
UPDATE data_response_detail set data_value = '16' where data_variable = 'ufqdp1' and data_value='14';
UPDATE data_response_detail set data_value = '15' where data_variable = 'ufqdp1' and data_value='13';
UPDATE data_response_detail set data_value = '14' where data_variable = 'ufqdp1' and data_value='12';
UPDATE data_response_detail set data_value = '13' where data_variable = 'ufqdp1' and data_value='11';
UPDATE data_response_detail set data_value = '11' where data_variable = 'ufqdp1' and data_value='10';
UPDATE data_response_detail set data_value = '10' where data_variable = 'ufqdp1' and data_value='9';
UPDATE data_response_detail set data_value = '9' where data_variable = 'ufqdp1' and data_value='8';
UPDATE data_response_detail set data_value = '8' where data_variable = 'ufqdp1' and data_value='7';
UPDATE data_response_detail set data_value = '7' where data_variable = 'ufqdp1' and data_value='6';
UPDATE data_response_detail set data_value = '6' where data_variable = 'ufqdp1' and data_value='5';

UPDATE `db_version` SET `version_number` = '9.13.0';

COMMIT;