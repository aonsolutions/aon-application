
# Database: aon_master
# Version: Actualizacion de la version 8.44.0 a la version 8.44.1.
# Created by: rtrepiana

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

UPDATE `system_data` SET `end_date` = NULL WHERE `name`='DIAS_INDEMNIZACION_FIN' AND `start_date`='2015-01-01';

##
## COTIZACION POR CONTRATOS PARA LA FORMACIÓN Y EL APRENDIZAJE(-101), BECARIOS E INVESTIGADORES(-105)
##

## Por contingencias Comunes
##
SET @GCG=(SELECT `id` FROM `deduction_concept` WHERE `code` = 'CGC' AND `domain` = 0);
## Empresa 30.98 
UPDATE `system_cost` 
SET `expression`=CONCAT('SI(BASE_CGC > 0.00, ',expression, ',0.00)') 
WHERE `domain` IN (-101, -105 ) AND `code`='CGC_E' AND expression RLIKE '^[[:space:]]*[[:digit:]]+(\.[[:digit:]]+)?[[:space:]]*$';
## Trabajador 6.18 
UPDATE `system_deduction` 
SET `expression`=CONCAT('SI(BASE_CGC > 0.00, ',expression, ',0.00)') 
WHERE `domain` IN (-101, -105 ) AND `deduction_concept`=@GCG AND expression RLIKE '^[[:space:]]*[[:digit:]]+(\.[[:digit:]]+)?[[:space:]]*$';


## Por Formación Profesional
##
SET @FP=(SELECT id FROM deduction_concept WHERE code='FP');
## Empresa 1.14 
UPDATE `system_cost` 
SET `expression`=CONCAT('SI(BASE_CGP > 0.00, ',expression, ',0.00)')   
WHERE `domain` = -101 AND `code`='FP_E' AND expression RLIKE '^[[:space:]]*[[:digit:]]+(\.[[:digit:]]+)?[[:space:]]*$';
## Trabajador 0.15 
UPDATE `system_deduction` 
SET `expression`=CONCAT('SI(BASE_CGP > 0.00, ',expression, ',0.00)') 
WHERE `domain` = -101 AND `deduction_concept`=@FP AND expression RLIKE '^[[:space:]]*[[:digit:]]+(\.[[:digit:]]+)?[[:space:]]*$';


## Por Accidentes de Trabajo y Enfermedades Profesionales
##
## Empresa 4.26 ( IT: 2.38 , IMS:: 1.88  )
UPDATE `system_cost` 
SET `expression`=CONCAT('SI(BASE_CGP > 0.00, ',expression, ',0.00)')   
WHERE `domain` = -101 AND `code` IN ('IT_E', 'IMS_E' ) AND expression RLIKE '^[[:space:]]*[[:digit:]]+(\.[[:digit:]]+)?[[:space:]]*$';

## Fondo de Garantía Salarial
## Empresa 2.35 
UPDATE `system_cost` 
SET `expression`=CONCAT('SI(BASE_CGP > 0.00, ',expression, ',0.00)') 
WHERE `domain` = -101 AND `code` = 'FOGASA_E' AND expression RLIKE '^[[:space:]]*[[:digit:]]+(\.[[:digit:]]+)?[[:space:]]*$';

## Por Desempleo  
##
SET @DESMPL=(SELECT id FROM deduction_concept WHERE code='DESMPL');

DELETE FROM `system_deduction` 
WHERE `domain` = -101 AND `deduction_concept`=@DESMPL ;
DELETE FROM `system_cost` 
WHERE `domain` = -101 AND `code`='DESMPL_E' ;


INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date` ) 
VALUES ( -101, 'PORCENTAJE_DESMPL', '1.55', '2014-01-01' ); 
INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date` ) 
VALUES ( -101, 'PORCENTAJE_DESMPL_E', '5.50', '2014-01-01' ); 

DELETE FROM `system_data` WHERE `domain` IN ( -101, -105 ) AND `name` LIKE 'BASE_CG%' ;

## 2014 
INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date`, `end_date` ) 
VALUES ( -101, 'BASE_CGP_MIN', '753.00', '2014-01-01', '2014-12-31' ); 
INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date`, `end_date` ) 
VALUES ( -105, 'BASE_CGP_MIN', '753.00', '2014-01-01', '2014-12-31'); 

## 2015 
INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date`, `end_date` ) 
VALUES ( -101, 'BASE_CGP_MIN', '756.60', '2015-01-01', '2015-12-31' ); 
INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date`, `end_date` ) 
VALUES ( -105, 'BASE_CGP_MIN', '756.60', '2015-01-01', '2015-12-31'); 

## 2016  
INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date` ) 
VALUES ( -101, 'BASE_CGP_MIN', '764.40', '2016-01-01' ); 
INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date` ) 
VALUES ( -105, 'BASE_CGP_MIN', '764.40', '2016-01-01' ); 

INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date` ) 
VALUES ( -101, 'BASE_CGC_MIN', 'BASE_CGP_MIN', '2014-01-01' ); 
INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date` ) 
VALUES ( -105, 'BASE_CGC_MIN', 'BASE_CGP_MIN', '2014-01-01' ); 

INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date` ) 
VALUES ( -101, 'BASE_CGP_MAX', 'BASE_CGP_MIN', '2014-01-01' ); 
INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date` ) 
VALUES ( -105, 'BASE_CGP_MAX', 'BASE_CGP_MIN', '2014-01-01' ); 

INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date` ) 
VALUES ( -101, 'BASE_CGC_MAX', 'BASE_CGP_MIN', '2014-01-01' ); 
INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date` ) 
VALUES ( -105, 'BASE_CGC_MAX', 'BASE_CGP_MIN', '2014-01-01' ); 

## VACACIONES RETRIBUIDAS NO DISFRUTADAS

SET @VACACIONES=(SELECT `id` FROM `payment_concept` WHERE `type`= 6 AND `domain` = 0 );

INSERT INTO `system_payment` 
(`domain`
, `payment_concept`
, `expression`
, `quote_expression`
, `start_date`
, `salary_type`)
VALUES ( 
-101
,@VACACIONES 
,'DIAS_VACACIONES_NO_DISFRUTADOS * ( SALARIO_DIA + SALARIO_VARIABLE_DIA )'
,'0.00'
,'2010-01-01'
,2 )
,( 
-105
,@VACACIONES 
,'DIAS_VACACIONES_NO_DISFRUTADOS * ( SALARIO_DIA + SALARIO_VARIABLE_DIA )'
,'0.00'
,'2010-01-01'
,2 );
 

UPDATE `db_version` SET `version_number` = '8.44.1';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
