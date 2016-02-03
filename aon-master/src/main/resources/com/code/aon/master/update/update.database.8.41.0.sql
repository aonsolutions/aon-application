
# Database: aon_master
# Version: Actualizacion de la version 8.41.0 a la version 8.41.1.
# Created by: rtrepiana

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;


BEGIN;

--
DELETE FROM `system_payment` 
WHERE `payment_concept` IN ( SELECT `id` FROM `payment_concept` WHERE `code` = 'PAGO_DIRECTO');

DELETE FROM `payment_concept` 
WHERE `code` = 'PAGO_DIRECTO';

--
INSERT INTO `payment_concept` 
(`domain`
,`code`	
,`description`
,`type`
,`description_decorable`
,`expression`
,`irpf_expression`
,`quote_expression`) 
VALUES
(0
,'PAGO_DIRECTO'
,NULL
,1
,0
,NULL
,NULL
,NULL);

SET @CONCEPT=(SELECT `id` FROM `payment_concept` WHERE `code` = 'PAGO_DIRECTO');

INSERT INTO `system_payment` 
(`domain`
,`type`
,`payment_concept`
,`description`
,`description_decorable`
,`expression`
,`irpf_expression`
,`quote_expression`
,`start_date`
,`month`
,`end_date`
,`salary_type`) 
VALUES 
(0
,NULL
,@CONCEPT
,'PREST. POR ENFERMEDAD COMÚN PAGO DIRECTO'
,0
,'DIAS_ENFERMEDAD_COMUN_366 * 0.00'
,NULL
,'DIAS_COTIZADOS * BASE_REGULADORA'
,'2010-01-01'
,NULL
,NULL
,0);

INSERT INTO `system_payment` 
(`domain`
,`type`
,`payment_concept`
,`description`
,`description_decorable`
,`expression`
,`irpf_expression`
,`quote_expression`
,`start_date`
,`month`
,`end_date`
,`salary_type`) 
VALUES 
(0
,NULL
,@CONCEPT
,'PREST. POR A.T Y/O E.P PAGO DIRECTO'
,0
,'DIAS_ENFERMEDAD_PROFESIONAL_366 * 0.00'
,NULL
,'DIAS_COTIZADOS * BASE_REGULADORA'
,'2010-01-01'
,NULL
,NULL
,0);

UPDATE `db_version` SET `version_number` = '8.41.1';

COMMIT;


SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

