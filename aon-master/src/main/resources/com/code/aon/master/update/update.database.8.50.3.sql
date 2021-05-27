
# Database: aon_master
# Version: Actualizacion de la version 8.50.3 a la version 8.50.4.
# Created by: rtrepiana

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;


SET @ANTIGUEDAD_ID=(SELECT `id` FROM `payment_concept` WHERE `code`='ANTIGUEDAD' AND `domain`=0);
SET @ANTIGUEDAD_HELP=(SELECT REPLACE(REPLACE(`expression`,'INPUT("/*user*//**/","','"'),'")','"') FROM `payment_concept` WHERE `id`=@ANTIGUEDAD_ID);

SET @PAGA_EXTRA_ID=(SELECT `id` FROM `payment_concept` WHERE `code`='PAGA_EXTRA' AND `domain`=0);
SET @PAGA_EXTRA_HELP=(SELECT REPLACE(REPLACE(`expression`,'INPUT("/*user*//**/","','"'),'")','"') FROM `payment_concept` WHERE `id`=@PAGA_EXTRA_ID);

INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date` ) 
VALUES ( 0, 'PAGA_EXTRA_HELP', @PAGA_EXTRA_HELP , '2010-01-01' ); 

INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date` ) 
VALUES ( 0, 'ANTIGUEDAD_HELP', @ANTIGUEDAD_HELP , '2010-01-01' ); 

UPDATE `payment_concept` 
SET `expression`=REPLACE(`expression`,@PAGA_EXTRA_HELP, 'PAGA_EXTRA_HELP');

UPDATE `agreement_payment` 
SET `expression`=REPLACE(`expression`,@PAGA_EXTRA_HELP, 'PAGA_EXTRA_HELP');

UPDATE `contract_payment` 
SET `expression`=REPLACE(`expression`,@PAGA_EXTRA_HELP, 'PAGA_EXTRA_HELP');

UPDATE `payment_concept` 
SET `expression`=REPLACE(`expression`,@ANTIGUEDAD_HELP, 'ANTIGUEDAD_HELP');

UPDATE `agreement_payment` 
SET `expression`=REPLACE(`expression`,@PAGA_EXTRA_HELP, 'ANtIGUEDAD_HELP');

UPDATE `contract_payment` 
SET `expression`=REPLACE(`expression`,@PAGA_EXTRA_HELP, 'ANTIGUEDAD_HELP');



UPDATE `db_version` SET `version_number` = '8.50.4';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
