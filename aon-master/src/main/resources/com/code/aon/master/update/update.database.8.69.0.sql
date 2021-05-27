# Database: aon_master
# Version: Actualizacion de la version 8.67.0 a la version 8.68.0.
# Created by: rtrepiana
# Creation Date: 02/10/2016 

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

# CONVENIO COLECTIVO PARA EL COMERCIO DEL METAL MADRID

SET @SALARIO_HORA=( SELECT `id` FROM `payment_concept` WHERE `domain` = 0 AND `id` > 0 AND `code`='SALARIO_BASE' AND `expression` LIKE '%SALARIO_HORA%' LIMIT 1);
UPDATE `agreement_payment` 
SET `expression`='( !TIEMPO_COMPLETO  && isdef SALARIO_HORA ) ? /*user*/SALARIO_HORA * HORAS_TRABAJADAS/**/: HIDE()'
WHERE `domain`=0 AND `payment_concept`=@SALARIO_HORA;

UPDATE `db_version` SET `version_number` = '8.71.0';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
