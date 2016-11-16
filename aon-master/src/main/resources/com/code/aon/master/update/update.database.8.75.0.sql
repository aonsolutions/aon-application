# Database: aon_master
# Version: Actualizacion de la version 8.75.0 a la version 8.75.1.
# Created by: rtrepiana
# Creation Date: 16/11/2016

BEGIN;

SET @MTNAD=(SELECT `id` FROM `payment_concept` WHERE `domain`=0 AND `code`='MTNAD');

UPDATE `system_payment`
 SET `quote_expression`= REPLACE(`expression`, '0.00', 'BASE_REGULADORA') 
 WHERE `domain` = 0 AND `payment_concept`= @MTNAD;

UPDATE `db_version` SET `version_number` = '8.75.1';

COMMIT;
