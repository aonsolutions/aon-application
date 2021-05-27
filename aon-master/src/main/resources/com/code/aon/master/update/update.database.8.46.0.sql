
# Database: aon_master
# Version: Actualizacion de la version 8.46.0 a la version 8.47.0
# Created by: rtrepiana

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

DELETE FROM `system_data` WHERE `name` = "BASE_CGC_MIN" AND `start_date` = "2016-01-01" ;

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
 \"01\":(TIEMPO_COMPLETO ? 1067.40 * (DIAS_COTIZADOS == DIAS_MES ? 1 : DIAS_COTIZADOS/30) : 6.37 * HORAS_NOMINA)
,\"02\":(TIEMPO_COMPLETO ? 885.30 * (DIAS_COTIZADOS == DIAS_MES ? 1 : DIAS_COTIZADOS/30) : 5.33 * HORAS_NOMINA)
,\"03\":(TIEMPO_COMPLETO ? 770.10 * (DIAS_COTIZADOS == DIAS_MES ? 1 : DIAS_COTIZADOS/30) : 4.64 * HORAS_NOMINA)
,\"04\":(TIEMPO_COMPLETO ? 764.40 * (DIAS_COTIZADOS == DIAS_MES ? 1 : DIAS_COTIZADOS/30) : 4.60 * HORAS_NOMINA)
,\"05\":(TIEMPO_COMPLETO ? 764.40 * (DIAS_COTIZADOS == DIAS_MES ? 1 : DIAS_COTIZADOS/30) : 4.60 * HORAS_NOMINA)
,\"06\":(TIEMPO_COMPLETO ? 764.40 * (DIAS_COTIZADOS == DIAS_MES ? 1 : DIAS_COTIZADOS/30) : 4.60 * HORAS_NOMINA)
,\"07\":(TIEMPO_COMPLETO ? 764.40 * (DIAS_COTIZADOS == DIAS_MES ? 1 : DIAS_COTIZADOS/30) : 4.60 * HORAS_NOMINA)
,\"08\":(TIEMPO_COMPLETO ? 25.48 * DIAS_COTIZADOS : 4.60 * HORAS_NOMINA)
,\"09\":(TIEMPO_COMPLETO ? 25.48 * DIAS_COTIZADOS : 4.60 * HORAS_NOMINA)
,\"10\":(TIEMPO_COMPLETO ? 25.48 * DIAS_COTIZADOS : 4.60 * HORAS_NOMINA)
,\"11\":(TIEMPO_COMPLETO ? 25.48 * DIAS_COTIZADOS : 4.60 * HORAS_NOMINA)]
[GRUPO_COTIZACION]
"
,"2016-01-01"
,NULL
,"1"
,"Bases mínimas");

DELETE FROM `system_data` WHERE `name` = "BASE_CGP_MIN" AND `start_date` = "2016-01-01" ;

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
"TIEMPO_COMPLETO ? 764.40 * (DIAS_COTIZADOS == DIAS_MES ? 1 : DIAS_COTIZADOS/30) : 4.60 * HORAS_NOMINA ",
"2016-01-01",
NULL,
"0",
"Tope Mínimo de cotización para Accidentes de Trabajo y Enfermedades Profesionales");




UPDATE `db_version` SET `version_number` = '8.47.0';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
