# Database: aon_master
# Version: Actualizacion de la version 9.23.3 a la version 9.23.4
# Created by: anderibz
# Creation Date: 05/04/2018 13:00

BEGIN;

ALTER TABLE `data_response_detail` MODIFY `data_value` text COLLATE latin1_spanish_ci NOT NULL COMMENT 'Valor de la variable';

UPDATE `db_version` SET `version_number` = '9.23.4';

COMMIT;
