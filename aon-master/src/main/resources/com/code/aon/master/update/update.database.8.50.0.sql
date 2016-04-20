# Database: aon_master
# Version: Actualizacion de la version 8.50.0 a la version 8.50.1
# Created by: rtrepiana

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

UPDATE `system_cost` 
SET `description` = IFNULL((SELECT `description` FROM `deduction_concept` WHERE `code` = REPLACE(`system_cost`.`code`, '_E', '')), REPLACE(`system_cost`.`code`, '_E', ' '))
WHERE `description` IS NULL OR `description` = '';

UPDATE `db_version` SET `version_number` = '8.50.1';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
