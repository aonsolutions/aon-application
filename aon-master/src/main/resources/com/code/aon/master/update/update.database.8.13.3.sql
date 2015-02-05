# Database: aon_master
# Version: Actualizacion de la version 8.13.3 a la version 8.13.4.
# Created by: rtrepiana
# Creation Date: 03/02/2015 

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;


# SS percents for Agricultural. 

# No se aplicará en este Sistema Especial:
#
# La cotización adicional por horas extraordinarias.
# El incremento de la cuota empresarial por contingencias comunes en los contratos temporales de duración efectiva inferior a 7 días.
#

INSERT INTO system_data 
( domain, name				, expression	, start_date	, end_date		, read_only 	, comments )  VALUES 
(-107	, 'PORCENTAJE_EXTR'		, 'REMOVE()'	, '2014-01-01'	, NULL			, 1		, NULL ),
(-107	, 'PORCENTAJE_NEXTR'		, 'REMOVE()'	, '2014-01-01'	, NULL			, 1		, NULL ),
(-107	, 'PORCENTAJE_EXTR_E'		, 'REMOVE()'	, '2014-01-01'	, NULL			, 1		, NULL ),
(-107	, 'PORCENTAJE_NEXTR_E'		, 'REMOVE()'	, '2014-01-01'	, NULL			, 1		, NULL ),
(-107	, 'PORCENTAJE_CORTA_DURACION'	, 'REMOVE()'	, '2014-01-01'	, NULL			, 1		, NULL )
;


# ----------------------------------------------------------------------------------------		
# 				BASES DE COTIZACION MENSUALES.
# ----------------------------------------------------------------------------------------		
# Grupo	Categorias Profesionales				 Minimas	 Maximas	
# ----------------------------------------------------------------------------------------		
# 1 	Ingenieros y Licenciados...				1.051,50 	2.595,60
# ----------------------------------------------------------------------------------------		
# 2 	Ingenieros Técnicos, Peritos y Ayudantes Titulados 	  872,10	2.595,60
# ----------------------------------------------------------------------------------------		
# 3 	Jefes Administrativos y de Taller 			  758,70 	2.595,60
# ----------------------------------------------------------------------------------------		
# 4 	Ayudantes no Titulados 					  753,00 	2.595,60
# ----------------------------------------------------------------------------------------		
# 5 	Oficiales Administrativos 				  753,00 	2.595,60
# ----------------------------------------------------------------------------------------		
# 6 	Subalternos 						  753,00 	2.595,60
# ----------------------------------------------------------------------------------------		
# 7 	Auxiliares Administrativos 				  753,00 	2.595,60
# ----------------------------------------------------------------------------------------		
# 8 	Oficiales de primera y segunda 				  753,00 	2.595,60
# ----------------------------------------------------------------------------------------		
# 9 	Oficiales de tercera y Especialistas 			  753,00 	2.595,60
# ----------------------------------------------------------------------------------------		
# 10 	Peones 							  753,00 	2.595,60
# ----------------------------------------------------------------------------------------		
# 11 	Trabajadores menores de dieciocho años 			  753,00 	2.595,60
# ----------------------------------------------------------------------------------------		

# ----------------------------------------------------------------------------------------		
# 				BASES DE COTIZACION MENSUALES.
# ----------------------------------------------------------------------------------------		
# 1 	Ingenieros y Licenciados...				   45,72 	  112,85
# ----------------------------------------------------------------------------------------		
# 2 	Ingenieros Técnicos, Peritos y Ayudantes Titulados 	   37,92 	  112,85
# ----------------------------------------------------------------------------------------		
# 3 	Jefes Administrativos y de Taller 			   32,99 	  112,85
# ----------------------------------------------------------------------------------------		
# 4 	Ayudantes no Titulados 					   32,74 	  112,85
# ----------------------------------------------------------------------------------------		
# 5 	Oficiales Administrativos 				   32,74 	  112,85
# ----------------------------------------------------------------------------------------		
# 6 	Subalternos 						   32,74 	  112,85
# ----------------------------------------------------------------------------------------		
# 7 	Auxiliares Administrativos 				   32,74 	  112,85
# ----------------------------------------------------------------------------------------		
# 8 	Oficiales de primera y segunda 				   32,74 	  112,85
# ----------------------------------------------------------------------------------------		
# 9 	Oficiales de tercera y Especialistas 			   32,74 	  112,85
# ----------------------------------------------------------------------------------------		
# 10 	Peones 							   32,74 	  112,85
# ----------------------------------------------------------------------------------------		
# 11 	Trabajadores menores de dieciocho años 			   32,74 	  112,85
# ----------------------------------------------------------------------------------------		

# Cuando se realicen en el mes natural 23 o más jornadas reales la base de cotización 
# aplicable será la correspondiente a las bases mensuales de cotización por contingencias 
# comunes.

INSERT INTO system_data 
( domain, name				, expression	, start_date	, end_date		, read_only , comments )  VALUES 
(-107	, 'COTIZACION_MENSUAL'		, 'true'	, '2014-01-01'	, NULL			, 1		, NULL ),
(-107	, 'BASE_CGC_MIN_MES'		, ' ["01":1051.50, "02":872.10, "03":758.70, "04":753.00, "05":753.00, "06":753.00, "07":753.00, "08":753.00, "09":753.00, "10":753.00, "11":753.00][GRUPO_COTIZACION] * ( DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30 )'			
							,'2014-01-01'	, NULL	, 1	, NULL ),
(-107	, 'BASE_CGC_MIN_DIA'		, ' ["01":45.72, "02":37.92, "03":32.99, "04":32.74, "05":32.74, "06":32.74, "07":32.74, "08":32.74, "09":32.74, "10":32.74, "11":32.74][GRUPO_COTIZACION]  * JORNADAS_REALES'			
							,'2014-01-01'	, NULL	, 1	, NULL ),
(-107	, 'BASE_CGC_MIN'		, '( COTIZACION_MENSUAL ) ? BASE_CGC_MIN_MES : BASE_CGC_MIN_DIA'			
							,'2014-01-01'	, NULL	, 1	, NULL ),
(-107	, 'BASE_CGC_MAX_MES'		, '2595.60 * ( DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30 )'			
							,'2014-01-01'	, NULL	, 1	, NULL ),
(-107	, 'BASE_CGC_MAX_DIA'		, '112.85 * JORNADAS_REALES'			
							,'2014-01-01'	, NULL	, 1	, NULL ),
(-107	, 'BASE_CGC_MAX'		, '( COTIZACION_MENSUAL || JORNADAS_REALES >= 23 ) ? BASE_CGC_MAX_MES : BASE_CGC_MAX_DIA'			
							,'2014-01-01'	, NULL	, 1	, NULL )
;

# --------------------------------------------------------------		
#		CONTINGENCIAS COMUNES
# --------------------------------------------------------------		
#   GRUPO 	EMPRESA		TRABAJADOR 	TOTAL 
#				(= GENERAL)
# --------------------------------------------------------------		
#	1   	  23,60 	       4,7 	28,30
# --------------------------------------------------------------		
#  2 a 11   	  16,85 	       4,7 	21,55
# --------------------------------------------------------------		

# --------------------------------------------------------------		
# REDUCCIÓN APORTACIÓN EMPRESARIAL CONTINGENCIAS COMUNES
# --------------------------------------------------------------		
#   GRUPO 	PUNTOS PORCENTUALES 	TIPO EFECTIVO % 	
# --------------------------------------------------------------		
#	1 		       8,10		  15,50
# --------------------------------------------------------------		
#  2 a 11		       6,50 		  10,35 (1) (2)
# --------------------------------------------------------------		
# (1) Base de cotización igual o inferior a 986,70 euros mensuales ó 42,90 euros por jornada.
# (2) Base de cotización superior a 986,70 euros/mes ó 42,90 euros/jornada, y hasta 2.595,60 euros/mes ó 112,85 euros por jornada realizada
# Gupo 1 La cuota empresarial resultante no podrá ser superior a 279,00 euros al mes ó 12,13 por jornada real trabajada.
# Grupo 2 a 11.- La cuota empresarial resultante no podrá ser inferior a 60,25 euros al mes ó 2,62 euros por jornada real trabajada.

INSERT INTO system_data 
( domain, name				, expression	, start_date	, end_date		, read_only 	, comments )  VALUES 
(-107	, 'REDUCCION_CGC_E_01'		, '8.10'	, '2014-01-01'	, NULL			, 1		, NULL ),
(-107	, 'REDUCCION_CGC_E_02'		, 'COTIZACION_MENSUAL ? ((BASE_CGC <= 986.70) ? 6.50 : ((BASE_CGC <= 2595.60) ? (6.50 * ( 1 + (BASE_CGC - 986.70)/ BASE_CGC * 2.52 * 6.15 / 6.50)) : 0.00)) : ((BASE_CGC / JORNADAS_REALES <= 42.90) ? 6.50 : ((BASE_CGC / JORNADAS_REALES <= 112.85 )? (6.50 * ( 1 + (BASE_CGC / JORNADAS_REALES - 42.90) / ( BASE_CGC / JORNADAS_REALES ) * 2.52 * 6.15 / 6.50)) : 0.00))'	
							, '2014-01-01'	, NULL			, 1		, NULL ),
(-107	, 'PORCENTAJE_CGC_E'		, '(GRUPO_COTIZACION == "01") ? (23.260 - REDUCCION_CGC_E_01) : (16.85 - REDUCCION_CGC_E_02)'	
							, '2014-01-01'	, NULL			, 1		, NULL )
;

INSERT INTO `system_cost` 
(`domain`	, `start_date`	, `end_date`	, `description`	, `expression`	, `type`, `code`) VALUES 
(-107	 	, '2014-01-01'	,NULL		,''		,'_CUOTA=(( BASE_CGC_E=( BASE_CGC + ( isdef BASE_MTNAD ? BASE_MTNAD : 0.00 ) ) ) * PORCENTAJE_CGC_E/100); (GRUPO_COTIZACION == "01") ? (COTIZACION_MENSUAL ? MIN(_CUOTA,279.00): MIN(_CUOTA, 12.13 * JORNADAS_REALES)) : (COTIZACION_MENSUAL ? MAX(_CUOTA,60.25): MAX(_CUOTA,2.62 * JORNADAS_REALES))'
										,0	,'CGC_E')
;

# Accidentes Trabajo y Enfermedades Profesionales ( = GENERAL )


# ----------------------------------------------		
#			FOGASA
# ----------------------------------------------		
#   EMPRESA		TRABAJADOR 	TOTAL 
# ----------------------------------------------		
#     0,10 	                	 0,10
# ----------------------------------------------		

INSERT INTO system_data 
( domain, name				, expression	, start_date	, end_date		, read_only 	, comments )  VALUES 
(-107	, 'PORCENTAJE_FOGASA'		, '0.10'	, '2014-01-01'	, NULL			, 1		, NULL )
;

# ----------------------------------------------		
#		FORMACIÓN PROFESIONAL 
# ----------------------------------------------		
#   EMPRESA		TRABAJADOR 	TOTAL 
# ----------------------------------------------		
#     0,15 	              0,03	 0,18
# ----------------------------------------------		

INSERT INTO system_data 
( domain, name				, expression	, start_date	, end_date		, read_only 	, comments )  VALUES 
(-107	, 'PORCENTAJE_FP'		, '0.03'	, '2014-01-01'	, NULL			, 1		, NULL ),
(-107	, 'PORCENTAJE_FP_E'		, '0.15'	, '2014-01-01'	, NULL			, 1		, NULL )
;

# -----------------------------------------------------------------------------------------------------		
#		DESEMPLEO 				EMPRESA    	TRABAJADORES 		TOTAL
# -----------------------------------------------------------------------------------------------------		
# Trabajadores por cuenta ajena fijos 		   	   5,50 		1,55 		7,05
# -----------------------------------------------------------------------------------------------------		
# Trabajadores por cuenta ajena de carácter eventual 	   6,70 		1,60 		8,30
# -----------------------------------------------------------------------------------------------------		
# Trabajadores con contratos de duración determinada 	   5,50 		1,55 		7,05	
# o celebrados con discapacitados con un grado no 
# inferior al 33 por ciento 	
# -----------------------------------------------------------------------------------------------------		

# PERIODOS DE INACTIVIDAD ?

# Situaciones de incapacidad temporal, riesgo durante el embarazo y riesgo durante la lactancia natural, 
# asi como de maternidad y paternidad causadas durante la situación de actividad

INSERT INTO `system_cost` 
(`domain`	, `start_date`	,`end_date`	,`description`	, `expression`	, `type`, `code`) VALUES 
(-107	 	, '2014-01-01'	,NULL		,'I.T, R.E y R.L, MATERNIDAD y PATERNIDAD'		
								,'( COTIZACION_MENSUAL ? 30 - ( DIAS_MES - DIAS_IT) : DIAS_IT ) * BASE_REGULADORA * ( INDEFINIDO ? ((GRUPO_COTIZACION == "01") ? 15.50 : 2.75): PORCENTAJE_CGC_E )'
										, 0	, 'ATEP_E')
;

INSERT INTO `system_cost` 
(`domain`	, `start_date`	,`end_date`	,`description`	, `expression`	, `type`, `code`) VALUES 
(-107	 	, '2014-01-01'	,NULL		,''		,'REMOVE()'		, 0	, 'ATEP_E')
;

# PESTACIONES

INSERT INTO `system_payment` 
(`domain`, `type`, `payment_concept`, `description`, `description_decorable`, `expression`, `irpf_expression`, `quote_expression`, `start_date`, `month`, `end_date`, `salary_type`) VALUES 
(-107,NULL,114,'',0,'REMOVE()',NULL,NULL,'2014-01-01',NULL,NULL,0)
;


UPDATE `db_version` SET `version_number` = '8.13.4';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

