# Database: aon_master
# Version: Actualizacion de la version 8.59.0 a la version 8.59.1.
# Created by: rtrepiana
# Creation Date: 03/07/2016 

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;


INSERT INTO system_data 
( domain, name				, expression	, start_date	, end_date		, read_only , comments )  VALUES 
(-107	, 'BASE_CGP_MIN_DIA'		, '764.40 * JORNADAS_REALES / 30'			
							,'2016-01-01'	, NULL			, 1	, NULL ),
(-107	, 'BASE_CGP_MIN_MES'		, '( TIEMPO_COMPLETO ) ? 764.40 * ((DIAS_COTIZADOS == DIAS_MES) ? 1 : DIAS_COTIZADOS/30) : 4.60 * HORAS_NOMINA'			
							,'2016-01-01'	, NULL			, 1	, NULL ),
(-107	, 'BASE_CGP_MIN'		, '( COTIZACION_MENSUAL ) ? BASE_CGP_MIN_MES : BASE_CGP_MIN_DIA'			
							,'2016-01-01'	, NULL			, 1	, NULL )
;



UPDATE `db_version` SET `version_number` = '8.59.1';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

