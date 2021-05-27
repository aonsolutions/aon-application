
# Database: aon_master
# Version: Actualizacion de la version 8.84.1 a la version 8.85.0.
# Created by: rtrepiana

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;


BEGIN;

-- 0 GENERAL

DELETE FROM `system_data`
WHERE  `domain`="0" AND `name`="BASE_CGC_MIN" AND `start_date`="2017-01-01";

UPDATE `system_data` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="0" AND `name`="BASE_CGC_MIN" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_data` 
(`domain`
,`name`
,`expression`
,`start_date`
,`end_date`
,`read_only`
,`comments`) VALUES 
("0"
,"BASE_CGC_MIN"
,"[
 \"01\":(TIEMPO_COMPLETO ? 1152.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : ( 6.37 * 1.08 ) * HORAS_NOMINA)
,\"02\":(TIEMPO_COMPLETO ?  956.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : ( 5.33 * 1.08 ) * HORAS_NOMINA)
,\"03\":(TIEMPO_COMPLETO ?  831.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : ( 4.64 * 1.08 ) * HORAS_NOMINA)
,\"04\":(TIEMPO_COMPLETO ?  825.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : ( 4.60 * 1.08 ) * HORAS_NOMINA)
,\"05\":(TIEMPO_COMPLETO ?  825.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : ( 4.60 * 1.08 ) * HORAS_NOMINA)
,\"06\":(TIEMPO_COMPLETO ?  825.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : ( 4.60 * 1.08 ) * HORAS_NOMINA)
,\"07\":(TIEMPO_COMPLETO ?  825.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : ( 4.60 * 1.08 ) * HORAS_NOMINA)
,\"08\":(TIEMPO_COMPLETO ?   27.52 * DIAS_NOMINA : ( 4.60 * 1.08 ) * HORAS_NOMINA)
,\"09\":(TIEMPO_COMPLETO ?   27.52 * DIAS_NOMINA : ( 4.60 * 1.08 ) * HORAS_NOMINA)
,\"10\":(TIEMPO_COMPLETO ?   27.52 * DIAS_NOMINA : ( 4.60 * 1.08 ) * HORAS_NOMINA)
,\"11\":(TIEMPO_COMPLETO ?   27.52 * DIAS_NOMINA : ( 4.60 * 1.08 ) * HORAS_NOMINA)]
[GRUPO_COTIZACION]
"
,"2017-01-01"
,NULL
,"1"
,"Bases mínimas");


DELETE FROM `system_data`
WHERE  `domain`="0" AND `name`="BASE_CGC_MAX" AND `start_date`="2017-01-01";

UPDATE `system_data` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="0" AND `name`="BASE_CGC_MAX" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_data` 
(`domain`
,`name`
,`expression`
,`start_date`
,`end_date`
,`read_only`
,`comments`) 
VALUES 
("0"
,"BASE_CGC_MAX"
,"[
 \"01\":(3751.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))
,\"02\":(3751.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))
,\"03\":(3751.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))
,\"04\":(3751.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))
,\"05\":(3751.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))
,\"06\":(3751.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))
,\"07\":(3751.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))
,\"08\":(125.04 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA))
,\"09\":(125.04 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA))
,\"10\":(125.04 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA))
,\"11\":(125.04 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA))]
[GRUPO_COTIZACION]
"
,"2017-01-01"
,NULL
,"1"
,"Bases Máximas");

DELETE FROM `system_data`
WHERE `domain`="0" AND `name`="BASE_CGP_MAX" AND `start_date`="2017-01-01";

UPDATE `system_data` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="0" AND `name`="BASE_CGP_MAX" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_data` 
(`domain`,
`name`,
`expression`,
`start_date`,
`end_date`,
`read_only`,
`comments`) 
VALUES 
("0",
"BASE_CGP_MAX",
"3751.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)",
"2017-01-01",
NULL,
"0",
"Tope Máximo de cotización para Accidentes de Trabajo y Enfermedades Profesionales");

DELETE FROM `system_data`
WHERE `domain`="0" AND `name`="BASE_CGP_MIN" AND `start_date`="2017-01-01";

UPDATE `system_data` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="0" AND `name`="BASE_CGP_MIN" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_data` 
(`domain`,
`name`,
`expression`,
`start_date`,
`end_date`,
`read_only`,
`comments`) 
VALUES 
("0",
"BASE_CGP_MIN",
"TIEMPO_COMPLETO ? 825.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : ( 4.60 * 1.08 ) * HORAS_NOMINA ",
"2017-01-01",
NULL,
"0",
"Tope Mínimo de cotización para Accidentes de Trabajo y Enfermedades Profesionales");

-- 106 HOME EMPLOYEE

DELETE FROM `system_data`
WHERE  `domain`="-106" AND `name`="PORCENTAJE_CGC" AND `start_date`="2017-01-01";

UPDATE `system_data` 
SET `end_date` = "2014-12-31" 
WHERE `domain`="-106" AND `name`="PORCENTAJE_CGC" AND `start_date`="2014-01-01" AND `end_date` IS NULL;

UPDATE `system_data` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-106" AND `name`="PORCENTAJE_CGC" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_data` 
(`domain`,
`name`,
`expression`,
`start_date`,
`end_date`,
`read_only`,
`comments`) 
VALUES 
("-106",
"PORCENTAJE_CGC",
"4.40",
"2017-01-01",
NULL,
"1",
NULL);

DELETE FROM `system_data`
WHERE `domain`="-106" AND `name`="PORCENTAJE_CGC_E" AND `start_date`="2017-01-01";

UPDATE `system_data` 
SET `end_date` = "2014-12-31" 
WHERE `domain`="-106" AND `name`="PORCENTAJE_CGC_E" AND `start_date`="2014-01-01" AND `end_date` IS NULL;

UPDATE `system_data` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-106" AND `name`="PORCENTAJE_CGC_E" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_data` 
(`domain`,
`name`,
`expression`,
`start_date`,
`end_date`,
`read_only`,
`comments`) 
VALUES 
("-106",
"PORCENTAJE_CGC_E",
"22.10",
"2017-01-01",
NULL,
"1",
NULL);

DELETE FROM `system_data`
WHERE `domain`="-106" AND `name`="BASE_CGC_MIN" AND `start_date`="2017-01-01";

UPDATE `system_data` 
SET `end_date` = "2014-12-31" 
WHERE `domain`="-106" AND `name`="BASE_CGC_MIN" AND `start_date`="2014-01-01" AND `end_date` IS NULL;

UPDATE `system_data` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-106" AND `name`="BASE_CGC_MIN" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_data` (`domain`,
`name`,
`expression`,
`start_date`,
`end_date`,
`read_only`,
`comments`) 
VALUES 
("-106",
"BASE_CGC_MIN",
"($ in [
 [188.61,161.29],
 [294.60,266.84],
 [400.80,372.99],
 [506.80,477.96],
 [612.90,583.52],
 [718.20,689.09],
 [825.65,825.60], [Double.MAX_VALUE,862.44] ] if $[0] >= BASE_CGC )[0][1]",
"2017-01-01",
NULL,
"1",
NULL);

-- 107 AGRICULTURAL
DELETE FROM `system_data`
WHERE `domain`="-107" AND `name`="BASE_CGC_MIN_MES" AND `start_date`="2017-01-01";

UPDATE `system_data` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-107" AND `name`="BASE_CGC_MIN_MES" AND `start_date`="2014-01-01" AND `end_date` IS NULL;

INSERT INTO `system_data` 
(`domain`,
`name`,
`expression`,
`start_date`,
`end_date`,
`read_only`,
`comments`) 
VALUES 
("-107",
"BASE_CGC_MIN_MES",
" [
 \"01\":1152.90
,\"02\":956.10
,\"03\":831.60
,\"04\":825.60
,\"05\":825.60
,\"06\":825.60
,\"07\":825.60
,\"08\":825.60
,\"09\":825.60
,\"10\":825.60
,\"11\":825.60][GRUPO_COTIZACION] * ( DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30 )",
"2017-01-01",
NULL,
"1",
NULL);

DELETE FROM `system_data`
WHERE `domain`="-107" AND `name`="BASE_CGC_MAX_MES" AND `start_date`="2017-01-01";

UPDATE `system_data` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-107" AND `name`="BASE_CGC_MAX_MES" AND `start_date`="2014-01-01" AND `end_date` IS NULL;

INSERT INTO `system_data` 
(`domain`,
`name`,
`expression`,
`start_date`,
`end_date`,
`read_only`,
`comments`) 
VALUES 
("-107",
"BASE_CGC_MAX_MES",
"3751.20 * ( DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30 )",
"2017-01-01",
NULL,
"1",
NULL);

DELETE FROM `system_data`
WHERE  `domain`="-107" AND `name`="BASE_CGC_MIN_DIA" AND `start_date`="2017-01-01";

UPDATE `system_data` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-107" AND `name`="BASE_CGC_MIN_DIA" AND `start_date`="2014-01-01" AND `end_date` IS NULL;

INSERT INTO `system_data` 
(`domain`,
`name`,
`expression`,
`start_date`,
`end_date`,
`read_only`,
`comments`) 
VALUES 
("-107",
"BASE_CGC_MIN_DIA",
"[
 \"01\":50.13,
 \"02\":41.57,
 \"03\":36.16,
 \"04\":35.90,
 \"05\":35.90,
 \"06\":35.90,
 \"07\":35.90,
 \"08\":35.90,
 \"09\":35.90,
 \"10\":35.90,
 \"11\":35.90][GRUPO_COTIZACION]  * JORNADAS_REALES",
"2017-01-01",
NULL,
"1",
NULL);

DELETE FROM `system_data`
WHERE  `domain`="-107" AND `name`="BASE_CGC_MAX_DIA" AND `start_date`="2017-01-01";

UPDATE `system_data` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-107" AND `name`="BASE_CGC_MAX_DIA" AND `start_date`="2014-01-01" AND `end_date` IS NULL;

INSERT INTO `system_data` (`domain`,
`name`,
`expression`,
`start_date`,
`end_date`,
`read_only`,
`comments`) 
VALUES 
("-107",
"BASE_CGC_MAX_DIA",
"163.10 * JORNADAS_REALES",
"2017-01-01",
NULL,
"1",
NULL);

DELETE FROM `system_data`
WHERE `domain`="-107" AND `name`="REDUCCION_CGC_E_01" AND `start_date`="2017-01-01";

UPDATE `system_data` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-107" AND `name`="REDUCCION_CGC_E_01" AND `start_date`="2014-01-01" AND `end_date` IS NULL;

INSERT INTO `system_data` 
(`domain`
,`name`
,`expression`
,`start_date`
,`end_date`
,`read_only`
,`comments`) VALUES 
("-107"
,"REDUCCION_CGC_E_01"
,"8.10"
,"2017-01-01"
,NULL
,"1"
,NULL);

DELETE FROM `system_data`
WHERE `domain`="-107" AND `name`="REDUCCION_CGC_E_02" AND `start_date`="2017-01-01";

UPDATE `system_data` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-107" AND `name`="REDUCCION_CGC_E_02" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_data` 
(`domain`
,`name`
,`expression`
,`start_date`
,`end_date`
,`read_only`
,`comments`) VALUES 
("-107"
,"REDUCCION_CGC_E_02"
,"COTIZACION_MENSUAL ? ((BASE_CGC <= 986.70) ? 6.97 : ((BASE_CGC <= 3751.20) ? (6.97 * ( 1 + (BASE_CGC - 986.70)/ BASE_CGC * 2.52 * 6.15 / 6.97)) : 0.00)) : ((BASE_CGC / JORNADAS_REALES <= 42.90) ? 6.97 : ((BASE_CGC / JORNADAS_REALES <= 163.10)? (6.97 * ( 1 + (BASE_CGC / JORNADAS_REALES - 42.90) / ( BASE_CGC / JORNADAS_REALES ) * 2.52 * 6.15 / 6.97)) : 0.00))"
,"2017-01-01"
,NULL
,"1"
,NULL);

DELETE FROM `system_data`
WHERE `domain`="-107" AND `name`="PORCENTAJE_CGC_E" AND `start_date`="2017-01-01";

UPDATE `system_data` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-107" AND `name`="PORCENTAJE_CGC_E" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_data` 
(`domain`
,`name`
,`expression`
,`start_date`
,`end_date`
,`read_only`
,`comments`) VALUES 
("-107"
,"PORCENTAJE_CGC_E"
,"(GRUPO_COTIZACION == \"01\") ? (23.60 - REDUCCION_CGC_E_01) : (17.30 - REDUCCION_CGC_E_02)"
,"2017-01-01"
,NULL
,"1"
,NULL);

-- 101 & 105 TRAINING & FELLOWS

SET @CGC_CONCEPT=(SELECT `id` FROM`deduction_concept` WHERE `code`="CGC");

DELETE FROM `system_deduction` 
WHERE `domain`="-101" AND `start_date`="2017-01-01" AND `deduction_concept` = @CGC_CONCEPT;

UPDATE `system_deduction` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-101" AND `start_date`="2016-01-01" AND `end_date` IS NULL AND `deduction_concept` = @CGC_CONCEPT;

INSERT INTO `system_deduction` 
(`domain`
,`type`
,`deduction_concept`
,`description`
,`description_decorable`
,`expression`
,`start_date`
,`end_date`
,`month`) VALUES 
("-101"
,NULL
,@CGC_CONCEPT
,NULL
,"0"
,"6.67"
,"2017-01-01"
,NULL
,NULL);

DELETE FROM `system_deduction` 
WHERE `domain`="-105" AND `start_date`="2017-01-01" AND `deduction_concept` = @CGC_CONCEPT;

UPDATE `system_deduction` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-105" AND `start_date`="2016-01-01" AND `end_date` IS NULL AND `deduction_concept` = @CGC_CONCEPT;

INSERT INTO `system_deduction` 
(`domain`
,`type`
,`deduction_concept`
,`description`
,`description_decorable`
,`expression`
,`start_date`
,`end_date`
,`month`)
VALUES (
"-105"
,NULL
,@CGC_CONCEPT
,NULL
,"0"
,"6.67"
,"2017-01-01"
,NULL
,NULL);

SET @DESMPL_CONCEPT=(SELECT `id` FROM`deduction_concept` WHERE `code`="DESMPL");

DELETE FROM `system_deduction` 
WHERE `domain`="-101" AND `start_date`="2017-01-01" AND `deduction_concept` = @DESMPL_CONCEPT;

UPDATE `system_deduction` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-101" AND `start_date`="2016-01-01" AND `end_date` IS NULL AND `deduction_concept` = @DESMPL_CONCEPT;

INSERT INTO `system_deduction` 
(`domain`
,`type`
,`deduction_concept`
,`description`
,`description_decorable`
,`expression`
,`start_date`
,`end_date`
,`month`)
VALUES 
("-101"
,NULL
,@DESMPL_CONCEPT
,NULL
,"0"
,"1.55/100.00 * 825.60"
,"2017-01-01"
,NULL
,NULL);

SET @FP_CONCEPT=(SELECT `id` FROM`deduction_concept` WHERE `code`="FP");

DELETE FROM `system_deduction` 
WHERE `domain`="-101" AND `start_date`="2017-01-01" AND `deduction_concept` = @FP_CONCEPT;

UPDATE `system_deduction` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-101" AND `start_date`="2016-01-01" AND `end_date` IS NULL AND `deduction_concept` = @FP_CONCEPT;

INSERT INTO `system_deduction` 
(`domain`
,`type`
,`deduction_concept`
,`description`
,`description_decorable`
,`expression`
,`start_date`
,`end_date`
,`month`)
VALUES 
("-101"
,NULL
,@FP_CONCEPT
,NULL
,"0"
,"0.16"
,"2014-01-01"
,NULL
,NULL);

DELETE FROM `system_cost` 
WHERE `domain`="-101" AND `code`="CGC_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-101" AND `code`="CGC_E" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-101","2017-01-01",NULL,NULL,"33.46","0","CGC_E");

DELETE FROM `system_cost` 
WHERE `domain`="-101" AND `code`="IT_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-101" AND `code`="IT_E" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-101","2017-01-01",NULL,NULL,"2.38","1","IT_E");

DELETE FROM `system_cost` 
WHERE `domain`="-101" AND `code`="IMS_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-101" AND `code`="IMS_E" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-101","2017-01-01",NULL,NULL,"1.88","1","IMS_E");

DELETE FROM `system_cost` 
WHERE `domain`="-101" AND `code`="DESMPL_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-101" AND `code`="DESMPL_E" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-101","2017-01-01",NULL,NULL,"5.50/100.00 * 825.60","2","DESMPL_E");

DELETE FROM `system_cost` 
WHERE `domain`="-101" AND `code`="FOGASA_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-101" AND `code`="FOGASA_E" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-101","2017-01-01",NULL,NULL,"2.54","10","FOGASA_E");

DELETE FROM `system_cost` 
WHERE `domain`="-101" AND `code`="FP_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-101" AND `code`="FP_E" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-101","2017-01-01",NULL,NULL,"1.23","3","FP_E");

DELETE FROM `system_cost` 
WHERE `domain`="-105" AND `code`="CGC_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-105" AND `code`="CGC_E" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-105","2017-01-01",NULL,NULL,"33.46","0","CGC_E");

DELETE FROM `system_cost` 
WHERE `domain`="-105" AND `code`="IT_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-105" AND `code`="IT_E" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-105","2017-01-01",NULL,NULL,"2.38","1","IT_E");

DELETE FROM `system_cost` 
WHERE `domain`="-105" AND `code`="IMS_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-105" AND `code`="IMS_E" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-105","2017-01-01",NULL,NULL,"1.88","1","IMS_E");


DELETE FROM `system_data` WHERE `domain`=-101 AND `name` = 'BASE_CGP_MIN' AND `start_date` = '2017-01-01';
INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date` ) 
VALUES ( -101, 'BASE_CGP_MIN', '825.60', '2017-01-01' ); 

DELETE FROM `system_data` WHERE `domain`=-105 AND `name` = 'BASE_CGP_MIN' AND `start_date` = '2017-01-01';
INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date` ) 
VALUES ( -105, 'BASE_CGP_MIN', '825.60', '2017-01-01' ); 



UPDATE `db_version` SET `version_number` = '8.89.0';

COMMIT;



SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

