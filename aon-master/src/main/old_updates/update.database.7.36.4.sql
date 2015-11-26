# Version: Upgrade from version 7.36.3 to 7.36.4
# Created by: rtrepiana@esferalia.com
# Creation Date: 03/07/2014 

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

INSERT INTO `payment_concept` (`domain`, `type`, `description`, `expression`, `irpf_expression`)
VALUES (0, 55, 'MEJORAS PREST.SS.ENFERMEDAD COMÚM', 'isdef DIAS_ENFERMEDAD_COMUN ?/*user*/GTZDO(SALARIO_BASE,1,3)/**/: REMOVE()', '_P');
INSERT INTO `payment_concept` (`domain`, `type`, `description`, `expression`, `irpf_expression`)
VALUES (0, 55, 'MEJORAS PREST.SS.ENFERMEDAD PROFESIONAL', 'isdef DIAS_ENFERMEDAD_PROFESIONAL ?/*user*/GTZDO(SALARIO_BASE,1,3)/**/: REMOVE()', '_P');


# New PREST_IT payment concept. Very usefull for guarantees 
INSERT INTO `payment_concept` (`domain`, `code`, `type`) VALUES (0, 'PREST_IT', 1);
SET @PREST_IT=(SELECT `id` FROM `payment_concept` WHERE `code`='PREST_IT' );
UPDATE `system_payment` SET `payment_concept`=@PREST_IT, `type`=NULL WHERE `description` LIKE 'PREST.%';
UPDATE `system_payment` SET `expression`='DIAS_PATERNIDAD * 0.00' WHERE `description` = 'PREST. POR PATERNIDAD';
UPDATE `system_payment` SET `expression`='DIAS_MATERNIDAD * 0.00' WHERE `description` LIKE 'PREST. POR MATERNIDAD%';


DELETE FROM `deduction_concept` WHERE `code` IN ('CGC', 'ATEP', 'DESMPL', 'FP', 'ESTR', 'NESTR', 'IRPF' );
INSERT INTO `deduction_concept` (`domain`, `code`, `description`, type) VALUES (0, 'CGC', 'Contingencias Comunes', 0);
SET @CGC=(SELECT `id` FROM `deduction_concept` WHERE `code`='CGC' );
INSERT INTO `deduction_concept` (`domain`, `code`, `description`, type) VALUES (0, 'ATEP', 'Accidentes de Trabajo y Enfermedades Profesionales', 1);
SET @ATEP=(SELECT `id` FROM `deduction_concept` WHERE `code`='ATEP' );
INSERT INTO `deduction_concept` (`domain`, `code`, `description`, type) VALUES (0, 'DESMPL', 'Desempleo',2);
SET @DESMPL=(SELECT `id` FROM `deduction_concept` WHERE `code`='DESMPL' );
INSERT INTO `deduction_concept` (`domain`, `code`, `description`, type) VALUES (0, 'FP', 'Formación Profesional',3);
SET @FP=(SELECT `id` FROM `deduction_concept` WHERE `code`='FP' );
INSERT INTO `deduction_concept` (`domain`, `code`, `description`, type) VALUES (0, 'ESTR', 'Horas extraordinarias fuerza mayor', 4);
SET @ESTR=(SELECT `id` FROM `deduction_concept` WHERE `code`='ESTR' );
INSERT INTO `deduction_concept` (`domain`, `code`, `description`, type) VALUES (0, 'NESTR', 'Resto horas extraordinarias', 5);
SET @NESTR=(SELECT `id` FROM `deduction_concept` WHERE `code`='NESTR' );
INSERT INTO `deduction_concept` (`domain`, `code`, `description`, type) VALUES (0, 'IRPF', 'IRPF', 6);
SET @IRPF=(SELECT `id` FROM `deduction_concept` WHERE `code`='IRPF' );

UPDATE `system_deduction` SET `deduction_concept`=@CGC, `type`=NULL WHERE `type`=0;
UPDATE `system_deduction` SET `deduction_concept`=@DESMPL, `type`=NULL WHERE `type`=2;
UPDATE `system_deduction` SET `deduction_concept`=@FP, `type`=NULL WHERE `type`=3;
UPDATE `system_deduction` SET `deduction_concept`=@ESTR, `type`=NULL WHERE `type`=4;
UPDATE `system_deduction` SET `deduction_concept`=@NESTR, `type`=NULL WHERE `type`=5;
UPDATE `system_deduction` SET `deduction_concept`=@IRPF, `type`=NULL WHERE `type`=6;



#CCCType.TRAINING:
SET @TRAINING=-101;
INSERT INTO `system_deduction` (`domain`, `deduction_concept`, `expression`, `start_date`) VALUES ( @TRAINING, @CGC, '6.09', '2014-01-01' );
INSERT INTO `system_deduction` (`domain`, `deduction_concept`, `expression`, `start_date`) VALUES ( @TRAINING, @DESMPL, '11.67', '2014-01-01' );
INSERT INTO `system_deduction` (`domain`, `deduction_concept`, `expression`, `start_date`) VALUES ( @TRAINING, @FP, '0.15', '2014-01-01' );

INSERT INTO `system_cost` (`domain`, `type`, `code`, `expression`, `start_date`) VALUES ( @TRAINING, 0, 'CGC_E', '30.52', '2014-01-01' );
INSERT INTO `system_cost` (`domain`, `type`, `code`, `expression`, `start_date`) VALUES ( @TRAINING, 1, 'IT_E', '2.35', '2014-01-01' );
INSERT INTO `system_cost` (`domain`, `type`, `code`, `expression`, `start_date`) VALUES ( @TRAINING, 1, 'IMS_E', '1.85', '2014-01-01' );
INSERT INTO `system_cost` (`domain`, `type`, `code`, `expression`, `start_date`) VALUES ( @TRAINING, 2, 'DESMPL_E', '41.42', '2014-01-01' );
INSERT INTO `system_cost` (`domain`, `type`, `code`, `expression`, `start_date`) VALUES ( @TRAINING, 3, 'FP_E', '1.12', '2014-01-01' );
INSERT INTO `system_cost` (`domain`, `type`, `code`, `expression`, `start_date`) VALUES ( @TRAINING, 10, 'FOGASA_E', '2.32', '2014-01-01' );


UPDATE `db_version` SET `version_number` = '7.36.5';

COMMIT;

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=1;
