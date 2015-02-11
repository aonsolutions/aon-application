# Database: aon_master
# Version: Actualizacion de la version 8.15.0 a la version 8.15.1.
# Created by: rtrepiana
# Creation Date: 11/02/2015 

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;


INSERT INTO `system_data` (`domain`,`name`,`expression`,`start_date`,`end_date`,`read_only`,`comments`) 
VALUES ("0",
	"BASE_CGC_MIN",
	"[
	\"01\":(TIEMPO_COMPLETO ? 1056.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 6.37 * HORAS_NOMINA), 
	\"02\":(TIEMPO_COMPLETO ? 876.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.28 * HORAS_NOMINA),
	\"03\":(TIEMPO_COMPLETO ? 762.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.59 * HORAS_NOMINA),
	\"04\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),
	\"05\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA), 
	\"06\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),
	\"07\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),
	\"08\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA),
	\"09\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA),
	\"10\":(TIEMPO_COMPLETO ? 25.22* DIAS_NOMINA : 4.56 * HORAS_NOMINA),
	\"11\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA)
	]
	[GRUPO_COTIZACION]",
	"2015-01-01",
	NULL,
	"1",
	"Bases mínimas");

INSERT INTO `system_data` (`domain`,`name`,`expression`,`start_date`,`end_date`,`read_only`,`comments`) 
VALUES ("0",
	"BASE_CGC_MAX",
	"[
	\"01\":(3606.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)),
	\"02\":(3606.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)),
	\"03\":(3606.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)), 
	\"04\":(3606.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)), 
	\"05\":(3606.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)), 
	\"06\":(3606.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)), 
	\"07\":(3606.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)), 
	\"08\":(120.20 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)), 
	\"09\":(120.20 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)), 
	\"10\":(120.20 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)), 
	\"11\":(120.20 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA))
	]
	[GRUPO_COTIZACION]",
	"2015-01-01",
	NULL,
	"1",
	"Bases Máximas");

INSERT INTO `system_data` (`domain`,`name`,`expression`,`start_date`,`end_date`,`read_only`,`comments`) 
VALUES ("0",
	"BASE_CGP_MAX",
	"3606.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)",
	"2015-01-01",
	NULL,
	"0",
	"Tope Máximo de cotización para Accidentes de Trabajo y Enfermedades Profesionales");

INSERT INTO `system_data` (`domain`,`name`,`expression`,`start_date`,`end_date`,`read_only`,`comments`) 
VALUES ("0",
	"BASE_CGP_MIN",
	"TIEMPO_COMPLETO ? 756.6000 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA ",
	"2015-01-01",
	NULL,
	"0",
	"Tope Mínimo de cotización para Accidentes de Trabajo y Enfermedades Profesionales");

INSERT INTO `system_data` (`domain`,`name`,`expression`,`start_date`,`end_date`,`read_only`,`comments`) 
VALUES ("-106",
	"PORCENTAJE_CGC",
	"4.10",
	"2015-01-01",
	NULL,
	"1",
	NULL);

INSERT INTO `system_data` (`domain`,`name`,`expression`,`start_date`,`end_date`,`read_only`,`comments`) 
VALUES ("-106",
	"PORCENTAJE_CGC_E",
	"20.60",
	"2015-01-01",
	NULL,
	"1",
	NULL);

INSERT INTO `system_data` (`domain`,`name`,`expression`,`start_date`,`end_date`,`read_only`,`comments`) 
VALUES ("-106",
	"BASE_CGC_MIN",
	"($ in [
	[172.91,148.60],
	[270.10,245.84],
	[367.40,343.10],
	[464.70,440.36],
	[561.90,537.63],
	[658.40,634.89],
	[756.60,756.60],
	[Double.MAX_VALUE,794.60]
      	] if $[0] >= BASE_CGC )[0][1]",
	"2015-01-01",
	NULL,
	"1",
	NULL);

INSERT INTO `system_data` (`domain`,`name`,`expression`,`start_date`,`end_date`,`read_only`,`comments`) 
VALUES ("-107",
	"BASE_CGC_MIN_MES",
	"[
	\"01\":1056.90, 
	\"02\":876.60, 
	\"03\":762.60, 
	\"04\":756.60, 
	\"05\":756.60, 
	\"06\":756.60, 
	\"07\":756.60, 
	\"08\":756.60, 
	\"09\":756.60, 
	\"10\":756.60, 
	\"11\":756.60
	][GRUPO_COTIZACION] * ( DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30 )",
	"2015-01-01",
	NULL,
	"1",
	NULL);

INSERT INTO `system_data` (`domain`,`name`,`expression`,`start_date`,`end_date`,`read_only`,`comments`) 
VALUES ("-107",
	"BASE_CGC_MAX_MES",
	"3063.30 * ( DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30 )",
	"2015-01-01",
	NULL,
	"1",
	NULL);

INSERT INTO `system_data` (`domain`,`name`,`expression`,`start_date`,`end_date`,`read_only`,`comments`)
VALUES ("-107",
	"BASE_CGC_MIN_DIA",
	"[
	\"01\":45.95, 
	\"02\":38.11, 
	\"03\":33.16, 
	\"04\":32.90, 
	\"05\":32.90, 
	\"06\":32.90, 
	\"07\":32.90, 
	\"08\":32.90, 
	\"09\":32.90, 
	\"10\":32.90, 
	\"11\":32.90
	][GRUPO_COTIZACION]  * JORNADAS_REALES",
	"2015-01-01",
	NULL,
	"1",
	NULL);

INSERT INTO `system_data` (`domain`,`name`,`expression`,`start_date`,`end_date`,`read_only`,`comments`) 
VALUES ("-107",
	"BASE_CGC_MAX_DIA",
	"133.19 * JORNADAS_REALES",
	"2015-01-01",
	NULL,
	"1",
	NULL);

UPDATE `db_version` SET `version_number` = '8.15.1';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
