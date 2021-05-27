# Database: aon_master
# Version: Actualizacion de la version 9.02.0 a la version 9.04.0.
# Created by: aibanez
# Creation Date: 08/09/2017 16:00

BEGIN;

UPDATE data_response_detail set data_value = '19' where data_variable = 'ufqdp1' and data_value='18';
UPDATE data_response_detail set data_value = '20' where data_variable = 'ufqdp1' and data_value='19';
UPDATE data_response_detail set data_value = '21' where data_variable = 'ufqdp1' and data_value='20';

ALTER TABLE `carrier_packing` ADD `additional_tare` double DEFAULT NULL COMMENT 'Tara Adicional' AFTER `tare`;

UPDATE `db_version` SET `version_number` = '9.04.0';

COMMIT;
