# Database: aon_master
# Version: Actualizacion de la version 8.31.0 a la version 8.32.0.
# Created by: aibanez
# Creation Date: 19/10/2015 16:56

BEGIN;

ALTER TABLE `app_param` CHANGE COLUMN `value` `value` VARCHAR(86) CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci' NULL DEFAULT NULL COMMENT 'Valor del Parametro';

UPDATE `db_version` SET `version_number` = '8.32.0';

COMMIT;
