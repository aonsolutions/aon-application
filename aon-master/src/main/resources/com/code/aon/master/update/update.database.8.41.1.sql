
# Database: aon_master
# Version: Actualizacion de la version 8.41.0 a la version 8.41.1.
# Created by: rtrepiana

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;


BEGIN;

DELETE FROM `system_data`
WHERE  `domain`="0" AND `name`="SMI" AND `start_date`="2016-01-01";

UPDATE `system_data` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="0" AND `name`="SMI" AND `start_date`="2015-01-01" AND `end_date` IS NULL;

INSERT INTO `system_data` 
(`domain`
,`name`
,`expression`
,`start_date`
,`end_date`
,`read_only`
,`comments`) VALUES 
("0"
,"SMI"
,"655.20"
,"2016-01-01"
,NULL
,"0"
,"SMI");

-- 0 GENERAL

DELETE FROM `system_data`
WHERE  `domain`="0" AND `name`="BASE_CGC_MIN" AND `start_date`="2016-01-01";

UPDATE `system_data` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="0" AND `name`="BASE_CGC_MIN" AND `start_date`="2015-01-01" AND `end_date` IS NULL;

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
 \"01\":(TIEMPO_COMPLETO ? 1056.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 6.37 * HORAS_NOMINA)
,\"02\":(TIEMPO_COMPLETO ? 885.30 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.33 * HORAS_NOMINA)
,\"03\":(TIEMPO_COMPLETO ? 770.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.64 * HORAS_NOMINA)
,\"04\":(TIEMPO_COMPLETO ? 764.40 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.60 * HORAS_NOMINA)
,\"05\":(TIEMPO_COMPLETO ? 764.40 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.60 * HORAS_NOMINA)
,\"06\":(TIEMPO_COMPLETO ? 764.40 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.60 * HORAS_NOMINA)
,\"07\":(TIEMPO_COMPLETO ? 764.40 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.60 * HORAS_NOMINA)
,\"08\":(TIEMPO_COMPLETO ? 25.48 * DIAS_NOMINA : 4.60 * HORAS_NOMINA)
,\"09\":(TIEMPO_COMPLETO ? 25.48 * DIAS_NOMINA : 4.60 * HORAS_NOMINA)
,\"10\":(TIEMPO_COMPLETO ? 25.48 * DIAS_NOMINA : 4.60 * HORAS_NOMINA)
,\"11\":(TIEMPO_COMPLETO ? 25.48 * DIAS_NOMINA : 4.60 * HORAS_NOMINA)]
[GRUPO_COTIZACION]
"
,"2016-01-01"
,NULL
,"1"
,"Bases mínimas");


DELETE FROM `system_data`
WHERE  `domain`="0" AND `name`="BASE_CGC_MAX" AND `start_date`="2016-01-01";

UPDATE `system_data` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="0" AND `name`="BASE_CGC_MAX" AND `start_date`="2015-01-01" AND `end_date` IS NULL;

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
 \"01\":(3642.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))
,\"02\":(3642.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))
,\"03\":(3642.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))
,\"04\":(3642.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))
,\"05\":(3642.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))
,\"06\":(3642.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))
,\"07\":(3642.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))
,\"08\":(121.40 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA))
,\"09\":(121.40 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA))
,\"10\":(121.40 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA))
,\"11\":(121.40 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA))]
[GRUPO_COTIZACION]
"
,"2016-01-01"
,NULL
,"1"
,"Bases Máximas");

DELETE FROM `system_data`
WHERE `domain`="0" AND `name`="BASE_CGP_MAX" AND `start_date`="2016-01-01";

UPDATE `system_data` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="0" AND `name`="BASE_CGP_MAX" AND `start_date`="2015-01-01" AND `end_date` IS NULL;

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
"3642.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)",
"2016-01-01",
NULL,
"0",
"Tope Máximo de cotización para Accidentes de Trabajo y Enfermedades Profesionales");

DELETE FROM `system_data`
WHERE `domain`="0" AND `name`="BASE_CGP_MIN" AND `start_date`="2016-01-01";

UPDATE `system_data` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="0" AND `name`="BASE_CGP_MIN" AND `start_date`="2015-01-01" AND `end_date` IS NULL;

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
"TIEMPO_COMPLETO ? 764.40 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.60 * HORAS_NOMINA ",
"2016-01-01",
NULL,
"0",
"Tope Mínimo de cotización para Accidentes de Trabajo y Enfermedades Profesionales");

-- 106 HOME EMPLOYEE

DELETE FROM `system_data`
WHERE  `domain`="-106" AND `name`="PORCENTAJE_CGC" AND `start_date`="2016-01-01";

UPDATE `system_data` 
SET `end_date` = "2014-12-31" 
WHERE `domain`="-106" AND `name`="PORCENTAJE_CGC" AND `start_date`="2014-01-01" AND `end_date` IS NULL;

UPDATE `system_data` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="-106" AND `name`="PORCENTAJE_CGC" AND `start_date`="2015-01-01" AND `end_date` IS NULL;

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
"4.25",
"2016-01-01",
NULL,
"1",
NULL);

DELETE FROM `system_data`
WHERE `domain`="-106" AND `name`="PORCENTAJE_CGC_E" AND `start_date`="2016-01-01";

UPDATE `system_data` 
SET `end_date` = "2014-12-31" 
WHERE `domain`="-106" AND `name`="PORCENTAJE_CGC_E" AND `start_date`="2014-01-01" AND `end_date` IS NULL;

UPDATE `system_data` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="-106" AND `name`="PORCENTAJE_CGC_E" AND `start_date`="2015-01-01" AND `end_date` IS NULL;

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
"21.35",
"2016-01-01",
NULL,
"1",
NULL);

DELETE FROM `system_data`
WHERE `domain`="-106" AND `name`="BASE_CGC_MIN" AND `start_date`="2016-01-01";

UPDATE `system_data` 
SET `end_date` = "2014-12-31" 
WHERE `domain`="-106" AND `name`="BASE_CGC_MIN" AND `start_date`="2014-01-01" AND `end_date` IS NULL;

UPDATE `system_data` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="-106" AND `name`="BASE_CGC_MIN" AND `start_date`="2015-01-01" AND `end_date` IS NULL;

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
 [174.64,149.34],
 [272.80,247.07],
 [371.10,344.81],
 [469.30,442.56],
 [567.50,540.30],
 [665.00,638.05],
 [764.40,764.40], [Double.MAX_VALUE,798.56] ] if $[0] >= BASE_CGC )[0][1]",
"2016-01-01",
NULL,
"1",
NULL);

-- 107 AGRICULTURAL
DELETE FROM `system_data`
WHERE `domain`="-107" AND `name`="BASE_CGC_MIN_MES" AND `start_date`="2016-01-01";

UPDATE `system_data` 
SET `end_date` = "2015-12-31" 
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
 \"01\":1067.40
,\"02\":885.30
,\"03\":770.10
,\"04\":764.40
,\"05\":764.40
,\"06\":764.40
,\"07\":764.40
,\"08\":764.40
,\"09\":764.40
,\"10\":764.40
,\"11\":764.40][GRUPO_COTIZACION] * ( DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30 )",
"2016-01-01",
NULL,
"1",
NULL);

DELETE FROM `system_data`
WHERE `domain`="-107" AND `name`="BASE_CGC_MAX_MES" AND `start_date`="2016-01-01";

UPDATE `system_data` 
SET `end_date` = "2015-12-31" 
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
"3642.00 * ( DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30 )",
"2016-01-01",
NULL,
"1",
NULL);

DELETE FROM `system_data`
WHERE  `domain`="-107" AND `name`="BASE_CGC_MIN_DIA" AND `start_date`="2016-01-01";

UPDATE `system_data` 
SET `end_date` = "2015-12-31" 
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
 \"01\":46.41,
 \"02\":38.49,
 \"03\":33.48,
 \"04\":33.23,
 \"05\":33.23,
 \"06\":33.23,
 \"07\":33.23,
 \"08\":33.23,
 \"09\":33.23,
 \"10\":33.23,
 \"11\":33.23][GRUPO_COTIZACION]  * JORNADAS_REALES",
"2016-01-01",
NULL,
"1",
NULL);

DELETE FROM `system_data`
WHERE  `domain`="-107" AND `name`="BASE_CGC_MAX_DIA" AND `start_date`="2016-01-01";

UPDATE `system_data` 
SET `end_date` = "2015-12-31" 
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
"158.35 * JORNADAS_REALES",
"2016-01-01",
NULL,
"1",
NULL);

DELETE FROM `system_data`
WHERE `domain`="-107" AND `name`="REDUCCION_CGC_E_01" AND `start_date`="2016-01-01";

UPDATE `system_data` 
SET `end_date` = "2015-12-31" 
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
,"2016-01-01"
,NULL
,"1"
,NULL);

DELETE FROM `system_data`
WHERE `domain`="-107" AND `name`="REDUCCION_CGC_E_02" AND `start_date`="2016-01-01";

UPDATE `system_data` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="-107" AND `name`="REDUCCION_CGC_E_02" AND `start_date`="2015-01-01" AND `end_date` IS NULL;

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
,"COTIZACION_MENSUAL ? ((BASE_CGC <= 986.70) ? 6.83 : ((BASE_CGC <= 3642.00) ? (6.83 * ( 1 + (BASE_CGC - 986.70)/ BASE_CGC * 2.52 * 6.15 / 6.83)) : 0.00)) : ((BASE_CGC / JORNADAS_REALES <= 42.90) ? 6.83 : ((BASE_CGC / JORNADAS_REALES <= 158.35)? (6.83 * ( 1 + (BASE_CGC / JORNADAS_REALES - 42.90) / ( BASE_CGC / JORNADAS_REALES ) * 2.52 * 6.15 / 6.83)) : 0.00))"
,"2016-01-01"
,NULL
,"1"
,NULL);

DELETE FROM `system_data`
WHERE `domain`="-107" AND `name`="PORCENTAJE_CGC_E" AND `start_date`="2016-01-01";

UPDATE `system_data` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="-107" AND `name`="PORCENTAJE_CGC_E" AND `start_date`="2015-01-01" AND `end_date` IS NULL;

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
,"2016-01-01"
,NULL
,"1"
,NULL);

-- 101 & 105 TRAINING & FELLOWS

SET @CGC_CONCEPT=(SELECT `id` FROM`deduction_concept` WHERE `code`="CGC");

DELETE FROM `system_deduction` 
WHERE `domain`="-101" AND `start_date`="2016-01-01" AND `deduction_concept` = @CGC_CONCEPT;

UPDATE `system_deduction` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="-101" AND `start_date`="2015-01-01" AND `end_date` IS NULL AND `deduction_concept` = @CGC_CONCEPT;

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
,"6.18"
,"2016-01-01"
,NULL
,NULL);

DELETE FROM `system_deduction` 
WHERE `domain`="-105" AND `start_date`="2016-01-01" AND `deduction_concept` = @CGC_CONCEPT;

UPDATE `system_deduction` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="-105" AND `start_date`="2015-01-01" AND `end_date` IS NULL AND `deduction_concept` = @CGC_CONCEPT;

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
,"6.18"
,"2016-01-01"
,NULL
,NULL);

SET @DESMPL_CONCEPT=(SELECT `id` FROM`deduction_concept` WHERE `code`="DESMPL");

DELETE FROM `system_deduction` 
WHERE `domain`="-101" AND `start_date`="2016-01-01" AND `deduction_concept` = @DESMPL_CONCEPT;

UPDATE `system_deduction` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="-101" AND `start_date`="2015-01-01" AND `end_date` IS NULL AND `deduction_concept` = @DESMPL_CONCEPT;

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
,"11.85"
,"2016-01-01"
,NULL
,NULL);

SET @FP_CONCEPT=(SELECT `id` FROM`deduction_concept` WHERE `code`="FP");

DELETE FROM `system_deduction` 
WHERE `domain`="-101" AND `start_date`="2016-01-01" AND `deduction_concept` = @FP_CONCEPT;

UPDATE `system_deduction` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="-101" AND `start_date`="2015-01-01" AND `end_date` IS NULL AND `deduction_concept` = @FP_CONCEPT;

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
,"0.15"
,"2014-01-01"
,NULL
,NULL);

DELETE FROM `system_cost` 
WHERE `domain`="-101" AND `code`="CGC_E" AND `start_date`="2016-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="-101" AND `code`="CGC_E" AND `start_date`="2015-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-101","2016-01-01",NULL,NULL,"30.98","0","CGC_E");

DELETE FROM `system_cost` 
WHERE `domain`="-101" AND `code`="IT_E" AND `start_date`="2016-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="-101" AND `code`="IT_E" AND `start_date`="2015-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-101","2016-01-01",NULL,NULL,"2.38","1","IT_E");

DELETE FROM `system_cost` 
WHERE `domain`="-101" AND `code`="IMS_E" AND `start_date`="2016-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="-101" AND `code`="IMS_E" AND `start_date`="2015-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-101","2016-01-01",NULL,NULL,"1.88","1","IMS_E");

DELETE FROM `system_cost` 
WHERE `domain`="-101" AND `code`="DESMPL_E" AND `start_date`="2016-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="-101" AND `code`="DESMPL_E" AND `start_date`="2015-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-101","2016-01-01",NULL,NULL,"42.04","2","DESMPL_E");

DELETE FROM `system_cost` 
WHERE `domain`="-101" AND `code`="FOGASA_E" AND `start_date`="2016-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="-101" AND `code`="FOGASA_E" AND `start_date`="2015-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-101","2016-01-01",NULL,NULL,"2.35","10","FOGASA_E");

DELETE FROM `system_cost` 
WHERE `domain`="-101" AND `code`="FP_E" AND `start_date`="2016-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="-101" AND `code`="FP_E" AND `start_date`="2015-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-101","2016-01-01",NULL,NULL,"1.14","3","FP_E");

DELETE FROM `system_cost` 
WHERE `domain`="-105" AND `code`="CGC_E" AND `start_date`="2016-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="-105" AND `code`="CGC_E" AND `start_date`="2015-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-105","2016-01-01",NULL,NULL,"30.98","0","CGC_E");

DELETE FROM `system_cost` 
WHERE `domain`="-105" AND `code`="IT_E" AND `start_date`="2016-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="-105" AND `code`="IT_E" AND `start_date`="2015-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-105","2016-01-01",NULL,NULL,"2.38","1","IT_E");

DELETE FROM `system_cost` 
WHERE `domain`="-105" AND `code`="IMS_E" AND `start_date`="2016-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2015-12-31" 
WHERE `domain`="-105" AND `code`="IMS_E" AND `start_date`="2015-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-105","2016-01-01",NULL,NULL,"1.88","1","IMS_E");


UPDATE `db_version` SET `version_number` = '8.41.2';

COMMIT;



SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

