# Database: aon_master
# Version: Actualizacion de la version 8.75.1 a la version 8.75.2.
# Created by: rtrepiana
# Creation Date: 16/11/2016

BEGIN;

SET @MTNAD=(SELECT `id` FROM `payment_concept` WHERE `domain`=0 AND `code`='MTNAD');

UPDATE `system_payment`
 SET `quote_expression`= REPLACE(`quote_expression`,'DIAS_MATERNIDAD','DIAS_COTIZADOS * (isdef COEFICIENTE_MATERNIDAD ? COEFICIENTE_MATERNIDAD : 1.00)')
 WHERE `domain` = 0 AND `payment_concept`= @MTNAD;

UPDATE `system_payment`
 SET `quote_expression`= REPLACE(`quote_expression`,'DIAS_PATERNIDAD','DIAS_COTIZADOS * (isdef COEFICIENTE_PATERNIDAD ? COEFICIENTE_PATERNIDAD : 1.00)')
 WHERE `domain` = 0 AND `payment_concept`= @MTNAD;

UPDATE `db_version` SET `version_number` = '8.75.2';

COMMIT;
