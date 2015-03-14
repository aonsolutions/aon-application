# Database: aon_master
# Version: Actualizacion de la version 8.17.0 a la version 8.17.1.
# Created by: rtrepiana
# Creation Date: 21/11/2014 09:50

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;


DELETE FROM `system_data` WHERE  domain = -8;
DELETE FROM `system_data` WHERE  domain = -9;
DELETE FROM `system_deduction` WHERE  domain = -8;
DELETE FROM `system_deduction` WHERE  domain = -9;


INSERT INTO system_data 
( domain, name			, expression	, start_date	, end_date		, read_only , comments )  VALUES 
(-8	, 'PORCENTAJE_ISFAS'	, '1.69'	, '2015-01-01'	, NULL			, 1			, NULL ),
(-8	, 'PORCENTAJE_DCHOS'	, '3.86'	, '2015-01-01'	, NULL			, 1			, NULL )
;

INSERT INTO system_data 
( domain, name				, expression	, start_date	, end_date		, read_only , comments )  VALUES 
(-9	, 'PORCENTAJE_MUFACE'	, '1.69'		, '2015-01-01'	, NULL			, 1			, NULL ),
(-9	, 'PORCENTAJE_DCHOS'	, '3.86'		, '2015-01-01'	, NULL			, 1			, NULL )
;

INSERT INTO `system_data` (`domain`,`name`,`expression`,`start_date`,`end_date`,`read_only`,`comments`)
VALUES ("-8",
	"BASE_CGC_MIN",
	"[
	\"01\":2839.07, 
	\"02\":2234.42, 
	\"03\":1956.60, 
	\"04\":1716.07, 
	\"05\":1357.70, 
	\"06\":1157.55
	][GRUPO_COTIZACION]",
	"2015-01-01",
	NULL,
	"1",
	NULL);

INSERT INTO system_data 
( domain, name			, expression	, start_date	, end_date	, read_only 	, comments )  VALUES 
(-8	, 'BASE_CGC_MAX'	, 'BASE_CGC_MIN', '2015-01-01'	, NULL		, 1		, NULL )
;

INSERT INTO system_data 
( domain, name			, expression	, start_date	, end_date	, read_only 	, comments )  VALUES 
(-8	, 'BASE_CGP_MIN'	, 'BASE_CGC_MIN', '2015-01-01'	, NULL		, 1		, NULL )
;

INSERT INTO system_data 
( domain, name			, expression	, start_date	, end_date	, read_only 	, comments )  VALUES 
(-8	, 'BASE_CGP_MAX'	, 'BASE_CGC_MIN', '2015-01-01'	, NULL		, 1		, NULL )
;


INSERT INTO `system_data` (`domain`,`name`,`expression`,`start_date`,`end_date`,`read_only`,`comments`)
VALUES ("-9",
	"BASE_CGC_MIN",
	"[
	\"01\":2839.07, 
	\"02\":2234.42, 
	\"03\":1956.60, 
	\"04\":1716.07, 
	\"05\":1357.70, 
	\"06\":1157.55
	][GRUPO_COTIZACION]",
	"2015-01-01",
	NULL,
	"1",
	NULL);

INSERT INTO system_data 
( domain, name			, expression	, start_date	, end_date		, read_only , comments )  VALUES 
(-9	, 'BASE_CGC_MAX'	, 'BASE_CGC_MIN', '2015-01-01'	, NULL			, 1			, NULL )
;

INSERT INTO system_data 
( domain, name			, expression	, start_date	, end_date	, read_only 	, comments )  VALUES 
(-8	, 'BASE_CGP_MIN'	, 'BASE_CGC_MIN', '2015-01-01'	, NULL		, 1		, NULL )
;

INSERT INTO system_data 
( domain, name			, expression	, start_date	, end_date	, read_only 	, comments )  VALUES 
(-8	, 'BASE_CGP_MAX'	, 'BASE_CGC_MIN', '2015-01-01'	, NULL		, 1		, NULL )
;


INSERT INTO `system_deduction` 
(`domain`	,`type`	,`deduction_concept`	,`description`	,`description_decorable`,`expression`	,`start_date`	,`end_date`	,`month`) VALUES 
("-8"		,10		,NULL		,"Instituto Social de las Fuerzas Armadas (ISFAS)"	
								,"0"			,"BASE_CGC * PORCENTAJE_ISFAS / 100.00"	
													,"2015-01-01"	,NULL		,NULL);

INSERT INTO `system_deduction` 
(`domain`	,`type`	,`deduction_concept`	,`description`	,`description_decorable`,`expression`	,`start_date`	,`end_date`	,`month`) VALUES 
("-9"		,10		,NULL		,"Administración Civil del Estado (MUFACE)"	
								,"0"			,"BASE_CGC * PORCENTAJE_ISFAS / 100.00"	
													,"2015-01-01"	,NULL		,NULL);

INSERT INTO `system_deduction` 
(`domain`	,`type`	,`deduction_concept`	,`description`		,`description_decorable`,`expression`	,`start_date`	,`end_date`	,`month`) VALUES 
("-8"		,10		,NULL		,"Cuota de Derechos Pasivos"		
									,"0"			,"BASE_CGC * PORCENTAJE_DCHOS / 100.00"	
																		,"2015-01-01"	,NULL		,NULL);

INSERT INTO `system_deduction` 
(`domain`	,`type`	,`deduction_concept`	,`description`		,`description_decorable`,`expression`	,`start_date`	,`end_date`	,`month`) VALUES 
("-9"		,10		,NULL		,"Cuota de Derechos Pasivos"
									,"0"			,"BASE_CGC * PORCENTAJE_DCHOS / 100.00"	
																		,"2015-01-01"	,NULL		,NULL);
INSERT INTO `system_deduction` 
(`domain`	, `type`, `deduction_concept`	, `description`		, `description_decorable`	, `expression`	, `start_date`, `end_date`, `month`) VALUES 
(-8		,NULL	,6305			,'@{PORCENTAJE_IRPF} %'	,0				,'BASE_IRPF * PORCENTAJE_IRPF/100','2010-01-01',NULL,NULL);

INSERT INTO `system_deduction` 
(`domain`	, `type`, `deduction_concept`	, `description`		, `description_decorable`	, `expression`	, `start_date`, `end_date`, `month`) VALUES 
(-9		,NULL	,6305			,'@{PORCENTAJE_IRPF} %'	,0				,'BASE_IRPF * PORCENTAJE_IRPF/100','2010-01-01',NULL,NULL);

UPDATE `db_version` SET `version_number` = '8.17.1';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

