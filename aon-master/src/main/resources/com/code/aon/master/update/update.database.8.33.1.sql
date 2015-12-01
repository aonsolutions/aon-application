# Database: aon_master
# Version: Actualizacion de la version 8.33.1 a la version 8.34.0.
# Created by: rtrepiana
# Creation Date: 01/12/2015 

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;


BEGIN;


# Bases for Fellows. 
DELETE FROM `system_data` WHERE `domain`=-105 AND `start_date`="2015-01-01";

INSERT INTO system_data 
( domain, name                  , expression    , start_date    , end_date              , read_only , comments )  VALUES 
(-105   , 'BASE_CGC_MIN'        , 'BASE_CGP_MIN', '2015-01-01'  , NULL                  , 1         , NULL ),
(-105   , 'BASE_CGC_MAX'        , 'BASE_CGP_MIN', '2015-01-01'  , NULL                  , 1         , NULL ),
(-105   , 'BASE_CGP_MAX'        , 'BASE_CGP_MIN', '2015-01-01'  , NULL                  , 1         , NULL )
;


DELETE FROM `system_cost` WHERE `domain`=-105 AND `start_date`="2015-01-01";

INSERT INTO `system_cost` 
(`domain`	,`start_date`	,`end_date`	,`description`	,`expression`	,`type`	,`code`) VALUES 
("-105"		,"2015-01-01"	,NULL		,NULL,		"30.67"		,"0"	,"CGC_E");

INSERT INTO `system_cost` 
(`domain`	,`start_date`	,`end_date`	,`description`	,`expression`	,`type`	,`code`) VALUES 
("-105"		,"2015-01-01"	,NULL		,NULL,		"2.36"		,"1"	,"IT_E");

INSERT INTO `system_cost` 
(`domain`	,`start_date`	,`end_date`	,`description`	,`expression`	,`type`	,`code`) VALUES 
("-105"		,"2015-01-01"	,NULL		,NULL,		"1.86"		,"1"	,"IMS_E");

INSERT INTO `system_cost` 
(`domain`	,`start_date`	,`end_date`	,`description`	,`expression`	,`type`	,`code`) VALUES 
("-105"		,"2015-01-01"	,NULL		,NULL,		"REMOVE()"		,"0"	,"CGC_E_TEMP");

INSERT INTO `system_cost` 
(`domain`	,`start_date`	,`end_date`	,`description`	,`expression`	,`type`	,`code`) VALUES 
("-105"		,"2015-01-01"	,NULL		,NULL,		"REMOVE()"		,"2"	,"DESMPL_E");

INSERT INTO `system_cost` 
(`domain`	,`start_date`	,`end_date`	,`description`	,`expression`	,`type`	,`code`) VALUES 
("-105"		,"2015-01-01"	,NULL		,NULL		,"REMOVE()"		,"3"	,"FP_E");

INSERT INTO `system_cost` 
(`domain`	,`start_date`	,`end_date`	,`description`	,`expression`	,`type`	,`code`) VALUES 
("-105"		,"2015-01-01"	,NULL		,NULL		,"REMOVE()"		,"3"	,"FOGASA_E");


DELETE FROM `system_deduction` WHERE `domain`=-105 AND `start_date`="2015-01-01";

SET @CGC=(SELECT id FROM deduction_concept WHERE code='CGC');
INSERT INTO `system_deduction` 
(`domain`,`type`,`deduction_concept`	,`description`	,`description_decorable`,`expression`	,`start_date`	,`end_date`	,`month`) VALUES 
("-105"	 ,NULL	,@CGC			,NULL		,"0"			,"6.12"		,"2015-01-01"	,NULL		,NULL);

SET @DESMPL=(SELECT id FROM deduction_concept WHERE code='DESMPL');
INSERT INTO `system_deduction` 
(`domain`,`type`,`deduction_concept`	,`description`	,`description_decorable`,`expression`	,`start_date`	,`end_date`	,`month`) VALUES 
("-105"	 ,NULL	,@DESMPL		,NULL		,"0"			,"REMOVE()"	,"2015-01-01"	,NULL		,NULL);

SET @FP=(SELECT id FROM deduction_concept WHERE code='FP');
INSERT INTO `system_deduction` 
(`domain`,`type`,`deduction_concept`	,`description`	,`description_decorable`,`expression`	,`start_date`	,`end_date`	,`month`) VALUES 
("-105"	 ,NULL	,@FP			,NULL		,"0"			,"REMOVE()"	,"2015-01-01"	,NULL		,NULL);


# Fix Issue 561 'Gastos manuntencion sin pernocta extranjero'

UPDATE `payment_concept` 
SET 
 `irpf_expression`=REPLACE(`irpf_expression`, 'DIAS_MANUTENCIO)', 'DIAS_MANUTENCION)')
WHERE `type`=45 AND `domain` = 0 ; 


# Fix Issue 549 'Gastos manuntencion sin pernocta extranjero'

UPDATE `payment_concept` 
SET 
 `irpf_expression`=REPLACE(`irpf_expression`, '91.35', '48.08'),
 `quote_expression`=REPLACE(`quote_expression`, '91.35', '48.08')
WHERE `type`=46 AND `domain` = 0; 



UPDATE `db_version` SET `version_number` = '8.34.0';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

