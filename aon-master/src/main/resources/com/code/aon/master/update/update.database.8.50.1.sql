
# Database: aon_master
# Version: Actualizacion de la version 8.50.1 a la version 8.50.2.
# Created by: rtrepiana

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

DELETE FROM `system_data` 
WHERE `name` = 'BASE_CGP_MIN' 
AND `start_date` = '2016-01-01'
AND `domain` IN (-101, -105);

INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date` ) 
VALUES ( -101, 'BASE_CGP_MIN', '764.40', '2016-01-01' ); 
INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date` ) 
VALUES ( -105, 'BASE_CGP_MIN', '764.40', '2016-01-01' ); 

UPDATE `payment_concept` SET `description` = UPPER(`description`);

UPDATE `db_version` SET `version_number` = '8.50.2';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
