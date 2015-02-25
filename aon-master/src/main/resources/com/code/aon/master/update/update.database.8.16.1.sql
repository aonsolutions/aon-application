# Database: aon_master
# Version: Actualizacion de la version 8.16.1 a la version 8.16.2.
# Created by: rtrepiana
# Creation Date: 25/02/2015 

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;


DELETE FROM `system_cost` WHERE `domain`=-101 AND `code`="DESMPL_E" AND `start_date`="2015-01-01" ; 
INSERT INTO `system_cost` 
(`domain`	,`start_date`	,`end_date`	,`description`	,`expression`	,`type`	,`code`) VALUES 
("-101"		,"2015-01-01"	,NULL		,NULL,		"41.61"		,"2"	,"DESMPL_E");
UPDATE `system_cost` SET `end_date`="2014-12-31" WHERE `domain`=-101 AND `code`="DESMPL_E" AND `start_date`="2014-01-01" ; 

DELETE FROM `system_cost` WHERE `domain`=-101 AND `code`="FP_E" AND `start_date`="2015-01-01"; 
INSERT INTO `system_cost` 
(`domain`	,`start_date`	,`end_date`	,`description`	,`expression`	,`type`	,`code`) VALUES 
("-101"		,"2015-01-01"	,NULL		,NULL		,"1.13"		,"3"	,"FP_E");
UPDATE `system_cost` SET `end_date`="2014-12-31" WHERE `domain`=-101 AND `code`="FP_E" AND `start_date`="2014-01-01"; 


DELETE FROM `system_deduction` WHERE `domain`=-101 AND `deduction_concept`="6301" AND `start_date`="2015-01-01";
INSERT INTO `system_deduction` 
(`domain`	,`type`	,`deduction_concept`	,`description`	,`description_decorable`,`expression`	,`start_date`	,`end_date`	,`month`) VALUES 
("-101"		,NULL	,"6301"			,NULL		,"0"			,"11.73"	,"2015-01-01"	,NULL		,NULL);
UPDATE `system_deduction` SET `end_date`="2014-12-31" WHERE `domain`=-101 AND `deduction_concept`="6301" AND `start_date`="2014-01-01";



DELETE FROM `system_data` WHERE `domain`=-107 AND `name`="REDUCCION_CGC_E_02" AND `start_date`="2015-01-01";
INSERT INTO `system_data` 
(`domain`	, `name`		, `expression`		, `start_date`	,`end_date`	,`read_only`	,`comments`) VALUES 
(-107		,'REDUCCION_CGC_E_02'	,'COTIZACION_MENSUAL ? 
((BASE_CGC <= 986.70) ? 6.68 : ((BASE_CGC <= 3063.30) ? (6.68 * ( 1 + (BASE_CGC - 986.70)/ BASE_CGC * 2.52 * 6.15 / 6.68)) : 0.00)) :
((BASE_CGC / JORNADAS_REALES <= 42.90) ? 6.68 : ((BASE_CGC / JORNADAS_REALES <= 133.19 )? (6.68 * ( 1 + (BASE_CGC / JORNADAS_REALES - 42.90) / ( BASE_CGC / JORNADAS_REALES ) * 2.52 * 6.15 / 6.68)) : 0.00))'
								,'2015-01-01'	,NULL		,1		,NULL);
UPDATE `system_data` SET `end_date`="2014-12-31" WHERE `domain`=-107 AND `name`="REDUCCION_CGC_E_02" AND `start_date`="2014-01-01";

DELETE FROM `system_data` WHERE `domain`=-107 AND `name`="PORCENTAJE_CGC_E" AND `start_date`="2015-01-01";
INSERT INTO `system_data` 
(`domain`, `name`		, `expression`, `start_date`	,`end_date`	, `read_only`	, `comments`) VALUES 
(-107	,'PORCENTAJE_CGC_E'	,'(GRUPO_COTIZACION == \"01\") ? (23.60 - REDUCCION_CGC_E_01) : (17.30 -REDUCCION_CGC_E_02)'
						,'2015-01-01'	,NULL		,1		,NULL);
UPDATE `system_data` SET `end_date`="2014-12-31" WHERE `domain`=-107 AND `name`="PORCENTAJE_CGC_E" AND `start_date`="2014-01-01";




UPDATE `db_version` SET `version_number` = '8.16.2';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

