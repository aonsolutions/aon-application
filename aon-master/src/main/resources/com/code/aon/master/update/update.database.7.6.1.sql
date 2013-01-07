# Database: aon_master
# Version: Actualizacion de la version 7.6.1 a la version 7.7.0.
# Created by: rtrepiana
# Creation Date: 07/01/2013 06:40
# Comentarios: No se si, esta actualizaci�n no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `deduction_concept` modify `code` VARCHAR(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo'; 
ALTER TABLE `payment_concept` modify `code` VARCHAR(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo'; 

INSERT INTO `system_data` ( domain, name, expression, start_date, end_date, read_only, comments) (SELECT domain, 'SMI', '645.30', '2013-01-01', NULL, 0, 'Salario minimo interprofesional'  FROM `system_data` WHERE name = 'SMI' AND start_date = '2011-01-01' );

UPDATE  `system_data` SET end_date = '2012-12-31' WHERE name = 'BASE_CGC_MAX' AND start_date = '2011-01-01';
INSERT INTO `system_data` ( domain, name, expression, start_date, end_date, read_only, comments) (SELECT domain, 'BASE_CGC_MAX','[\"01\": \"3262.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\", \"02\": \"3262.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\", \"03\": \"3262.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\", \"04\": \"3262.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\", \"05\": \"3262.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\", \"06\": \"3262.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\", \"07\": \"3262.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\", \"08\": \"108.75 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)\", \"09\": \"108.75 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)\", \"10\": \"108.75 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)\", \"11\": \"108.75 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)\"]','2013-01-01',NULL,1,'Bases máximas'  FROM `system_data` WHERE name = 'BASE_CGC_MAX' AND start_date = '2011-01-01' );
UPDATE  `system_data` SET end_date = '2012-12-31' WHERE name = 'BASE_CGP_MAX' AND start_date = '2011-01-01'
INSERT INTO `system_data` ( domain, name, expression, start_date, end_date, read_only, comments) (SELECT domain, 'BASE_CGP_MAX','\"3262.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\"','2013-01-01',NULL,0,'Tope Máximo de cotización para Accidentes de Trabajo y Enfermedades Profesionales'  FROM `system_data` WHERE name = 'BASE_CGC_MAX' AND start_date = '2011-01-01' );

UPDATE `db_version` SET `version_number` = '7.7.0';

COMMIT;
