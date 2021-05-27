
# Database: aon_master
# Version: Actualizacion de la version 8.44.1 a la version 8.45.0.
# Created by: girazu
# Creation Date: 22/01/2016 14:30

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

UPDATE `system_data` 
SET `expression` = REPLACE(expression, 'BASE_CGC', 'BASE_CGC_E') 
WHERE `domain` = -107 AND `name` = 'REDUCCION_CGC_E_02' AND `expression` NOT LIKE '%BASE_CGC_E%'; 

UPDATE `db_version` SET `version_number` = '8.45.0';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
