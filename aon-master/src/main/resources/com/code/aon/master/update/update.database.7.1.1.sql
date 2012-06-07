# Database: aon_master
# Version: Actualizacion de la version 7.1.1 a la version 7.1.2.
# Created by: rtrepiana
# Creation Date: 30/05/2012 18:50
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;


# SALARIO MÍNIMO INTERPROFESIONAL(SMI)  
# 
# UPDATE system_data SET end_date='2011-12-31' WHERE start_date='2011-01-01' AND name='SMI';
# INSERT INTO system_data 
# ( name	, expression, start_date	, end_date	, read_only	, comments ) 
# VALUES 
# ('SMI'	, '641.40'	, '2012-01-01'	, NULL		, 0			, 'Salario mínimo interprofesional' );


# INDICADOR PÚBLICO DE RENTAS DE EFECTOS MÚLTIPLES (IPREM) 
#
UPDATE system_data SET end_date='2011-12-31' WHERE start_date='2011-01-01' AND name='IPREM';

INSERT INTO system_data 
( domain, name	, expression, start_date	, end_date	, read_only	, comments ) 
SELECT  
 id, 'IPREM'	, '532.51'	, '2012-01-01'	, NULL		, 1			, 'Indicador Público de Renta de Efectos Múltiples (IPREM)' 
FROM domain;


# BASES DE COTIZACIÓN CONTINGENCIAS COMUNES
#
# Bases mínimas 
#
# UPDATE system_data SET end_date='2011-12-31' WHERE start_date='2011-01-01' AND name='BASE_CGC_MIN';
#
# INSERT INTO system_data 
# ( name			, expression				, start_date	, end_date	, read_only	, comments ) 
# VALUES 
# ('BASE_CGC_MIN',  '["01": "TIEMPO_COMPLETO ? 1045.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 6.30 * HORAS_NOMINA", 
# 					"02": "TIEMPO_COMPLETO ? 867.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.22 * HORAS_NOMINA", 
# 					"03": "TIEMPO_COMPLETO ? 754.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.54 * HORAS_NOMINA", 
# 					"04": "TIEMPO_COMPLETO ? 748.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.51 * HORAS_NOMINA", 
# 					"05": "TIEMPO_COMPLETO ? 748.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.51 * HORAS_NOMINA", 
# 					"06": "TIEMPO_COMPLETO ? 748.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.51 * HORAS_NOMINA ",
# 					"07": "TIEMPO_COMPLETO ? 748.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.51 * HORAS_NOMINA",
# 					"08": "TIEMPO_COMPLETO ? 24.94 * DIAS_NOMINA : 4.51 * HORAS_NOMINA", 
# 					"09": "TIEMPO_COMPLETO ? 24.94 * DIAS_NOMINA : 4.51 * HORAS_NOMINA", 
# 					"10": "TIEMPO_COMPLETO ? 24.94 * DIAS_NOMINA : 4.51 * HORAS_NOMINA", 
# 					"11": "TIEMPO_COMPLETO ? 24.94 * DIAS_NOMINA : 4.51 * HORAS_NOMINA"]'            
# 											,'2012-01-01' 	, null 		, 1 		, 'Bases mínimas');


# Bases máximas  
#
UPDATE system_data SET end_date='2011-12-31' WHERE start_date='2011-01-01' AND name='BASE_CGC_MAX';

INSERT INTO system_data 
( domain, name			, expression				, start_date	, end_date	, read_only	, comments ) 
SELECT  
id, 'BASE_CGC_MAX',  '["01": "3262.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)", 
					"02": "3262.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)", 
					"03": "3262.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)", 
					"04": "3262.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)", 
					"05": "3262.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)", 
					"06": "3262.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)", 
					"07": "3262.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)",
					"08": "108.75 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)", 
					"09": "108.75 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)", 
					"10": "108.75 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)", 
					"11": "108.75 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)"]'            
											,'2012-01-01' 	, null 		, 1 		, 'Bases máximas'
FROM domain;


# TOPES COTIZACIÓN DE ACCIDENTES DE TRABAJO Y ENFERMEDADES PROFESIONALES MÁXIMO
#								

# Mínimo
#
# UPDATE system_data SET end_date='2011-12-31' WHERE start_date='2011-01-01' AND name='BASE_CGP_MIN';

# INSERT INTO system_data 
# ( name			, expression, start_date	, end_date	, read_only	, comments ) 
# VALUES 
# ('BASE_CGP_MIN'	, '"TIEMPO_COMPLETO ? 748.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.51 * HORAS_NOMINA "'
# 							, '2012-01-01'	, NULL		, 1			, 'Tope Máximo de cotización para Accidentes de Trabajo y Enfermedades Profesionales' );

							
# Máximos
#
UPDATE system_data SET end_date='2011-12-31' WHERE start_date='2011-01-01' AND name='BASE_CGP_MAX';

INSERT INTO system_data 
( domain, name			, expression	, start_date	, end_date	, read_only	, comments ) 
SELECT 
id, 'BASE_CGP_MAX'	, '"3262.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)"'
								, '2012-01-01'	, NULL		, 1			, 'Tope Mínimo de cotización para Accidentes de Trabajo y Enfermedades Profesionales' 
FROM domain;


				
								
								
# set @ARABA ='01'; 
# set '20'='20'; 
# set '31'='31'; 
# set '48'='48'; 

# Tablas de retención de IRPF 2012 de Euskadi.
#
INSERT INTO geozone_irpf (geozone_code, start_date, end_date, amount ) VALUES
('01', '2012-01-01', '2012-12-31', 0.00 ),('20', '2012-01-01', '2012-12-31', 0.00 ), ('48', '2012-01-01', '2012-12-31', 0.00 ),
('01', '2012-01-01', '2012-12-31', 11560.01 ),('20', '2012-01-01', '2012-12-31', 11560.01 ), ('48', '2012-01-01', '2012-12-31', 11560.01 ),
('01', '2012-01-01', '2012-12-31', 12020.01 ),('20', '2012-01-01', '2012-12-31', 12020.01 ), ('48', '2012-01-01', '2012-12-31', 12020.01 ),
('01', '2012-01-01', '2012-12-31', 12250.01 ),('20', '2012-01-01', '2012-12-31', 12250.01 ), ('48', '2012-01-01', '2012-12-31', 12250.01 ),
('01', '2012-01-01', '2012-12-31', 13070.01 ),('20', '2012-01-01', '2012-12-31', 13070.01 ), ('48', '2012-01-01', '2012-12-31', 13070.01 ),
('01', '2012-01-01', '2012-12-31', 13660.01 ),('20', '2012-01-01', '2012-12-31', 13660.01 ), ('48', '2012-01-01', '2012-12-31', 13660.01 ),
('01', '2012-01-01', '2012-12-31', 14310.01 ),('20', '2012-01-01', '2012-12-31', 14310.01 ), ('48', '2012-01-01', '2012-12-31', 14310.01 ),
('01', '2012-01-01', '2012-12-31', 15030.01 ),('20', '2012-01-01', '2012-12-31', 15030.01 ), ('48', '2012-01-01', '2012-12-31', 15030.01 ),
('01', '2012-01-01', '2012-12-31', 15820.01 ),('20', '2012-01-01', '2012-12-31', 15820.01 ), ('48', '2012-01-01', '2012-12-31', 15820.01 ),
('01', '2012-01-01', '2012-12-31', 16950.01 ),('20', '2012-01-01', '2012-12-31', 16950.01 ), ('48', '2012-01-01', '2012-12-31', 16950.01 ),
('01', '2012-01-01', '2012-12-31', 18330.01 ),('20', '2012-01-01', '2012-12-31', 18330.01 ), ('48', '2012-01-01', '2012-12-31', 18330.01 ),
('01', '2012-01-01', '2012-12-31', 19690.01 ),('20', '2012-01-01', '2012-12-31', 19690.01 ), ('48', '2012-01-01', '2012-12-31', 19690.01 ),
('01', '2012-01-01', '2012-12-31', 21180.01 ),('20', '2012-01-01', '2012-12-31', 21180.01 ), ('48', '2012-01-01', '2012-12-31', 21180.01 ),
('01', '2012-01-01', '2012-12-31', 22650.01 ),('20', '2012-01-01', '2012-12-31', 22650.01 ), ('48', '2012-01-01', '2012-12-31', 22650.01 ),
('01', '2012-01-01', '2012-12-31', 24020.01 ),('20', '2012-01-01', '2012-12-31', 24020.01 ), ('48', '2012-01-01', '2012-12-31', 24020.01 ),
('01', '2012-01-01', '2012-12-31', 26190.01 ),('20', '2012-01-01', '2012-12-31', 26190.01 ), ('48', '2012-01-01', '2012-12-31', 26190.01 ),
('01', '2012-01-01', '2012-12-31', 28650.01 ),('20', '2012-01-01', '2012-12-31', 28650.01 ), ('48', '2012-01-01', '2012-12-31', 28650.01 ),
('01', '2012-01-01', '2012-12-31', 31630.01 ),('20', '2012-01-01', '2012-12-31', 31630.01 ), ('48', '2012-01-01', '2012-12-31', 31630.01 ),
('01', '2012-01-01', '2012-12-31', 35290.01 ),('20', '2012-01-01', '2012-12-31', 35290.01 ), ('48', '2012-01-01', '2012-12-31', 35290.01 ),
('01', '2012-01-01', '2012-12-31', 38410.01 ),('20', '2012-01-01', '2012-12-31', 38410.01 ), ('48', '2012-01-01', '2012-12-31', 38410.01 ),
('01', '2012-01-01', '2012-12-31', 41030.01 ),('20', '2012-01-01', '2012-12-31', 41030.01 ), ('48', '2012-01-01', '2012-12-31', 41030.01 ),
('01', '2012-01-01', '2012-12-31', 43930.01 ),('20', '2012-01-01', '2012-12-31', 43930.01 ), ('48', '2012-01-01', '2012-12-31', 43930.01 ),
('01', '2012-01-01', '2012-12-31', 47290.01 ),('20', '2012-01-01', '2012-12-31', 47290.01 ), ('48', '2012-01-01', '2012-12-31', 47290.01 ),
('01', '2012-01-01', '2012-12-31', 51240.01 ),('20', '2012-01-01', '2012-12-31', 51240.01 ), ('48', '2012-01-01', '2012-12-31', 51240.01 ),
('01', '2012-01-01', '2012-12-31', 54580.01 ),('20', '2012-01-01', '2012-12-31', 54580.01 ), ('48', '2012-01-01', '2012-12-31', 54580.01 ),
('01', '2012-01-01', '2012-12-31', 58100.01 ),('20', '2012-01-01', '2012-12-31', 58100.01 ), ('48', '2012-01-01', '2012-12-31', 58100.01 ),
('01', '2012-01-01', '2012-12-31', 62210.01 ),('20', '2012-01-01', '2012-12-31', 62210.01 ), ('48', '2012-01-01', '2012-12-31', 62210.01 ),
('01', '2012-01-01', '2012-12-31', 66970.01 ),('20', '2012-01-01', '2012-12-31', 66970.01 ), ('48', '2012-01-01', '2012-12-31', 66970.01 ),
('01', '2012-01-01', '2012-12-31', 72270.01 ),('20', '2012-01-01', '2012-12-31', 72270.01 ), ('48', '2012-01-01', '2012-12-31', 72270.01 ),
('01', '2012-01-01', '2012-12-31', 76780.01 ),('20', '2012-01-01', '2012-12-31', 76780.01 ), ('48', '2012-01-01', '2012-12-31', 76780.01 ),
('01', '2012-01-01', '2012-12-31', 81880.01 ),('20', '2012-01-01', '2012-12-31', 81880.01 ), ('48', '2012-01-01', '2012-12-31', 81880.01 ),
('01', '2012-01-01', '2012-12-31', 87530.01 ),('20', '2012-01-01', '2012-12-31', 87530.01 ), ('48', '2012-01-01', '2012-12-31', 87530.01 ),
('01', '2012-01-01', '2012-12-31', 94210.01 ),('20', '2012-01-01', '2012-12-31', 94210.01 ), ('48', '2012-01-01', '2012-12-31', 94210.01 ),
('01', '2012-01-01', '2012-12-31', 102000.01 ),('20', '2012-01-01', '2012-12-31', 102000.01 ), ('48', '2012-01-01', '2012-12-31', 102000.01 ),
('01', '2012-01-01', '2012-12-31', 111180.01 ),('20', '2012-01-01', '2012-12-31', 111180.01 ), ('48', '2012-01-01', '2012-12-31', 111180.01 ),
('01', '2012-01-01', '2012-12-31', 122160.01 ),('20', '2012-01-01', '2012-12-31', 122160.01 ), ('48', '2012-01-01', '2012-12-31', 122160.01 ),
('01', '2012-01-01', '2012-12-31', 135540.01 ),('20', '2012-01-01', '2012-12-31', 135540.01 ), ('48', '2012-01-01', '2012-12-31', 135540.01 ),
('01', '2012-01-01', '2012-12-31', 152070.01 ),('20', '2012-01-01', '2012-12-31', 152070.01 ), ('48', '2012-01-01', '2012-12-31', 152070.01 ),
('01', '2012-01-01', '2012-12-31', 172340.01 ),('20', '2012-01-01', '2012-12-31', 172340.01 ), ('48', '2012-01-01', '2012-12-31', 172340.01 ),
('01', '2012-01-01', '2012-12-31', 198860.01 ),('20', '2012-01-01', '2012-12-31', 198860.01 ), ('48', '2012-01-01', '2012-12-31', 198860.01 ),
('01', '2012-01-01', '2012-12-31', 235010.01 ),('20', '2012-01-01', '2012-12-31', 235010.01 ), ('48', '2012-01-01', '2012-12-31', 235010.01 );
								
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=11560.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 1),(@IRPF, 1, 0),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=11560.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 1),(@IRPF, 1, 0),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=11560.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 1),(@IRPF, 1, 0),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=12020.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 2),(@IRPF, 1, 0),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=12020.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 2),(@IRPF, 1, 0),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=12020.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 2),(@IRPF, 1, 0),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=12250.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 3),(@IRPF, 1, 1),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=12250.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 3),(@IRPF, 1, 1),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=12250.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 3),(@IRPF, 1, 1),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=13070.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 4),(@IRPF, 1, 2),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=13070.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 4),(@IRPF, 1, 2),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=13070.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 4),(@IRPF, 1, 2),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=13660.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 5),(@IRPF, 1, 3),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=13660.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 5),(@IRPF, 1, 3),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=13660.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 5),(@IRPF, 1, 3),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=14310.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 6),(@IRPF, 1, 4),(@IRPF, 2, 2),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=14310.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 6),(@IRPF, 1, 4),(@IRPF, 2, 2),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=14310.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 6),(@IRPF, 1, 4),(@IRPF, 2, 2),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=15030.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 7),(@IRPF, 1, 5),(@IRPF, 2, 3),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=15030.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 7),(@IRPF, 1, 5),(@IRPF, 2, 3),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=15030.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 7),(@IRPF, 1, 5),(@IRPF, 2, 3),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=15820.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 8),(@IRPF, 1, 6),(@IRPF, 2, 4),(@IRPF, 3, 1),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=15820.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 8),(@IRPF, 1, 6),(@IRPF, 2, 4),(@IRPF, 3, 1),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=15820.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 8),(@IRPF, 1, 6),(@IRPF, 2, 4),(@IRPF, 3, 1),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=16950.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 9),(@IRPF, 1, 7),(@IRPF, 2, 5),(@IRPF, 3, 2),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=16950.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 9),(@IRPF, 1, 7),(@IRPF, 2, 5),(@IRPF, 3, 2),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=16950.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 9),(@IRPF, 1, 7),(@IRPF, 2, 5),(@IRPF, 3, 2),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=18330.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 10),(@IRPF, 1, 9),(@IRPF, 2, 7),(@IRPF, 3, 4),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=18330.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 10),(@IRPF, 1, 9),(@IRPF, 2, 7),(@IRPF, 3, 4),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=18330.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 10),(@IRPF, 1, 9),(@IRPF, 2, 7),(@IRPF, 3, 4),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=19690.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 11),(@IRPF, 1, 10),(@IRPF, 2, 8),(@IRPF, 3, 5),(@IRPF, 4, 2),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=19690.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 11),(@IRPF, 1, 10),(@IRPF, 2, 8),(@IRPF, 3, 5),(@IRPF, 4, 2),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=19690.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 11),(@IRPF, 1, 10),(@IRPF, 2, 8),(@IRPF, 3, 5),(@IRPF, 4, 2),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=21180.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 12),(@IRPF, 1, 11),(@IRPF, 2, 9),(@IRPF, 3, 6),(@IRPF, 4, 3),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=21180.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 12),(@IRPF, 1, 11),(@IRPF, 2, 9),(@IRPF, 3, 6),(@IRPF, 4, 3),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=21180.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 12),(@IRPF, 1, 11),(@IRPF, 2, 9),(@IRPF, 3, 6),(@IRPF, 4, 3),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=22650.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 13),(@IRPF, 1, 12),(@IRPF, 2, 10),(@IRPF, 3, 8),(@IRPF, 4, 4),(@IRPF, 5, 1),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=22650.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 13),(@IRPF, 1, 12),(@IRPF, 2, 10),(@IRPF, 3, 8),(@IRPF, 4, 4),(@IRPF, 5, 1),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=22650.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 13),(@IRPF, 1, 12),(@IRPF, 2, 10),(@IRPF, 3, 8),(@IRPF, 4, 4),(@IRPF, 5, 1),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=24020.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 14),(@IRPF, 1, 13),(@IRPF, 2, 11),(@IRPF, 3, 9),(@IRPF, 4, 6),(@IRPF, 5, 3),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=24020.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 14),(@IRPF, 1, 13),(@IRPF, 2, 11),(@IRPF, 3, 9),(@IRPF, 4, 6),(@IRPF, 5, 3),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=24020.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 14),(@IRPF, 1, 13),(@IRPF, 2, 11),(@IRPF, 3, 9),(@IRPF, 4, 6),(@IRPF, 5, 3),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=26190.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 15),(@IRPF, 1, 14),(@IRPF, 2, 13),(@IRPF, 3, 10),(@IRPF, 4, 8),(@IRPF, 5, 5),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=26190.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 15),(@IRPF, 1, 14),(@IRPF, 2, 13),(@IRPF, 3, 10),(@IRPF, 4, 8),(@IRPF, 5, 5),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=26190.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 15),(@IRPF, 1, 14),(@IRPF, 2, 13),(@IRPF, 3, 10),(@IRPF, 4, 8),(@IRPF, 5, 5),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=28650.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 16),(@IRPF, 1, 15),(@IRPF, 2, 14),(@IRPF, 3, 12),(@IRPF, 4, 10),(@IRPF, 5, 7),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=28650.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 16),(@IRPF, 1, 15),(@IRPF, 2, 14),(@IRPF, 3, 12),(@IRPF, 4, 10),(@IRPF, 5, 7),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=28650.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 16),(@IRPF, 1, 15),(@IRPF, 2, 14),(@IRPF, 3, 12),(@IRPF, 4, 10),(@IRPF, 5, 7),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=31630.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 17),(@IRPF, 1, 16),(@IRPF, 2, 15),(@IRPF, 3, 13),(@IRPF, 4, 11),(@IRPF, 5, 8),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=31630.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 17),(@IRPF, 1, 16),(@IRPF, 2, 15),(@IRPF, 3, 13),(@IRPF, 4, 11),(@IRPF, 5, 8),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=31630.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 17),(@IRPF, 1, 16),(@IRPF, 2, 15),(@IRPF, 3, 13),(@IRPF, 4, 11),(@IRPF, 5, 8),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=35290.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 18),(@IRPF, 1, 17),(@IRPF, 2, 16),(@IRPF, 3, 15),(@IRPF, 4, 13),(@IRPF, 5, 10),(@IRPF, 6, 3);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=35290.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 18),(@IRPF, 1, 17),(@IRPF, 2, 16),(@IRPF, 3, 15),(@IRPF, 4, 13),(@IRPF, 5, 10),(@IRPF, 6, 3);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=35290.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 18),(@IRPF, 1, 17),(@IRPF, 2, 16),(@IRPF, 3, 15),(@IRPF, 4, 13),(@IRPF, 5, 10),(@IRPF, 6, 3);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=38410.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 19),(@IRPF, 1, 18),(@IRPF, 2, 17),(@IRPF, 3, 16),(@IRPF, 4, 14),(@IRPF, 5, 12),(@IRPF, 6, 5);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=38410.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 19),(@IRPF, 1, 18),(@IRPF, 2, 17),(@IRPF, 3, 16),(@IRPF, 4, 14),(@IRPF, 5, 12),(@IRPF, 6, 5);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=38410.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 19),(@IRPF, 1, 18),(@IRPF, 2, 17),(@IRPF, 3, 16),(@IRPF, 4, 14),(@IRPF, 5, 12),(@IRPF, 6, 5);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=41030.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 20),(@IRPF, 1, 19),(@IRPF, 2, 18),(@IRPF, 3, 17),(@IRPF, 4, 15),(@IRPF, 5, 13),(@IRPF, 6, 7);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=41030.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 20),(@IRPF, 1, 19),(@IRPF, 2, 18),(@IRPF, 3, 17),(@IRPF, 4, 15),(@IRPF, 5, 13),(@IRPF, 6, 7);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=41030.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 20),(@IRPF, 1, 19),(@IRPF, 2, 18),(@IRPF, 3, 17),(@IRPF, 4, 15),(@IRPF, 5, 13),(@IRPF, 6, 7);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=43930.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 21),(@IRPF, 1, 20),(@IRPF, 2, 20),(@IRPF, 3, 18),(@IRPF, 4, 17),(@IRPF, 5, 15),(@IRPF, 6, 9);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=43930.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 21),(@IRPF, 1, 20),(@IRPF, 2, 20),(@IRPF, 3, 18),(@IRPF, 4, 17),(@IRPF, 5, 15),(@IRPF, 6, 9);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=43930.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 21),(@IRPF, 1, 20),(@IRPF, 2, 20),(@IRPF, 3, 18),(@IRPF, 4, 17),(@IRPF, 5, 15),(@IRPF, 6, 9);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=47290.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 22),(@IRPF, 1, 21),(@IRPF, 2, 21),(@IRPF, 3, 19),(@IRPF, 4, 18),(@IRPF, 5, 16),(@IRPF, 6, 11);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=47290.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 22),(@IRPF, 1, 21),(@IRPF, 2, 21),(@IRPF, 3, 19),(@IRPF, 4, 18),(@IRPF, 5, 16),(@IRPF, 6, 11);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=47290.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 22),(@IRPF, 1, 21),(@IRPF, 2, 21),(@IRPF, 3, 19),(@IRPF, 4, 18),(@IRPF, 5, 16),(@IRPF, 6, 11);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=51240.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 23),(@IRPF, 1, 22),(@IRPF, 2, 22),(@IRPF, 3, 21),(@IRPF, 4, 19),(@IRPF, 5, 18),(@IRPF, 6, 12);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=51240.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 23),(@IRPF, 1, 22),(@IRPF, 2, 22),(@IRPF, 3, 21),(@IRPF, 4, 19),(@IRPF, 5, 18),(@IRPF, 6, 12);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=51240.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 23),(@IRPF, 1, 22),(@IRPF, 2, 22),(@IRPF, 3, 21),(@IRPF, 4, 19),(@IRPF, 5, 18),(@IRPF, 6, 12);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=54580.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 24),(@IRPF, 1, 23),(@IRPF, 2, 23),(@IRPF, 3, 22),(@IRPF, 4, 21),(@IRPF, 5, 19),(@IRPF, 6, 14);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=54580.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 24),(@IRPF, 1, 23),(@IRPF, 2, 23),(@IRPF, 3, 22),(@IRPF, 4, 21),(@IRPF, 5, 19),(@IRPF, 6, 14);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=54580.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 24),(@IRPF, 1, 23),(@IRPF, 2, 23),(@IRPF, 3, 22),(@IRPF, 4, 21),(@IRPF, 5, 19),(@IRPF, 6, 14);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=58100.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 25),(@IRPF, 1, 25),(@IRPF, 2, 24),(@IRPF, 3, 23),(@IRPF, 4, 22),(@IRPF, 5, 20),(@IRPF, 6, 16);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=58100.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 25),(@IRPF, 1, 25),(@IRPF, 2, 24),(@IRPF, 3, 23),(@IRPF, 4, 22),(@IRPF, 5, 20),(@IRPF, 6, 16);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=58100.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 25),(@IRPF, 1, 25),(@IRPF, 2, 24),(@IRPF, 3, 23),(@IRPF, 4, 22),(@IRPF, 5, 20),(@IRPF, 6, 16);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=62210.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 26),(@IRPF, 1, 26),(@IRPF, 2, 25),(@IRPF, 3, 24),(@IRPF, 4, 23),(@IRPF, 5, 22),(@IRPF, 6, 17);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=62210.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 26),(@IRPF, 1, 26),(@IRPF, 2, 25),(@IRPF, 3, 24),(@IRPF, 4, 23),(@IRPF, 5, 22),(@IRPF, 6, 17);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=62210.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 26),(@IRPF, 1, 26),(@IRPF, 2, 25),(@IRPF, 3, 24),(@IRPF, 4, 23),(@IRPF, 5, 22),(@IRPF, 6, 17);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=66970.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 27),(@IRPF, 1, 27),(@IRPF, 2, 26),(@IRPF, 3, 25),(@IRPF, 4, 24),(@IRPF, 5, 23),(@IRPF, 6, 19);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=66970.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 27),(@IRPF, 1, 27),(@IRPF, 2, 26),(@IRPF, 3, 25),(@IRPF, 4, 24),(@IRPF, 5, 23),(@IRPF, 6, 19);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=66970.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 27),(@IRPF, 1, 27),(@IRPF, 2, 26),(@IRPF, 3, 25),(@IRPF, 4, 24),(@IRPF, 5, 23),(@IRPF, 6, 19);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=72270.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 28),(@IRPF, 1, 28),(@IRPF, 2, 27),(@IRPF, 3, 26),(@IRPF, 4, 25),(@IRPF, 5, 24),(@IRPF, 6, 20);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=72270.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 28),(@IRPF, 1, 28),(@IRPF, 2, 27),(@IRPF, 3, 26),(@IRPF, 4, 25),(@IRPF, 5, 24),(@IRPF, 6, 20);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=72270.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 28),(@IRPF, 1, 28),(@IRPF, 2, 27),(@IRPF, 3, 26),(@IRPF, 4, 25),(@IRPF, 5, 24),(@IRPF, 6, 20);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=76780.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 29),(@IRPF, 1, 29),(@IRPF, 2, 28),(@IRPF, 3, 27),(@IRPF, 4, 27),(@IRPF, 5, 25),(@IRPF, 6, 22);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=76780.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 29),(@IRPF, 1, 29),(@IRPF, 2, 28),(@IRPF, 3, 27),(@IRPF, 4, 27),(@IRPF, 5, 25),(@IRPF, 6, 22);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=76780.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 29),(@IRPF, 1, 29),(@IRPF, 2, 28),(@IRPF, 3, 27),(@IRPF, 4, 27),(@IRPF, 5, 25),(@IRPF, 6, 22);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=81880.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 30),(@IRPF, 1, 30),(@IRPF, 2, 29),(@IRPF, 3, 29),(@IRPF, 4, 28),(@IRPF, 5, 27),(@IRPF, 6, 23);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=81880.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 30),(@IRPF, 1, 30),(@IRPF, 2, 29),(@IRPF, 3, 29),(@IRPF, 4, 28),(@IRPF, 5, 27),(@IRPF, 6, 23);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=81880.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 30),(@IRPF, 1, 30),(@IRPF, 2, 29),(@IRPF, 3, 29),(@IRPF, 4, 28),(@IRPF, 5, 27),(@IRPF, 6, 23);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=87530.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 31),(@IRPF, 1, 31),(@IRPF, 2, 30),(@IRPF, 3, 30),(@IRPF, 4, 29),(@IRPF, 5, 28),(@IRPF, 6, 25);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=87530.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 31),(@IRPF, 1, 31),(@IRPF, 2, 30),(@IRPF, 3, 30),(@IRPF, 4, 29),(@IRPF, 5, 28),(@IRPF, 6, 25);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=87530.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 31),(@IRPF, 1, 31),(@IRPF, 2, 30),(@IRPF, 3, 30),(@IRPF, 4, 29),(@IRPF, 5, 28),(@IRPF, 6, 25);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=94210.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 32),(@IRPF, 1, 32),(@IRPF, 2, 31),(@IRPF, 3, 31),(@IRPF, 4, 30),(@IRPF, 5, 29),(@IRPF, 6, 26);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=94210.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 32),(@IRPF, 1, 32),(@IRPF, 2, 31),(@IRPF, 3, 31),(@IRPF, 4, 30),(@IRPF, 5, 29),(@IRPF, 6, 26);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=94210.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 32),(@IRPF, 1, 32),(@IRPF, 2, 31),(@IRPF, 3, 31),(@IRPF, 4, 30),(@IRPF, 5, 29),(@IRPF, 6, 26);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=102000.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 33),(@IRPF, 1, 33),(@IRPF, 2, 32),(@IRPF, 3, 32),(@IRPF, 4, 31),(@IRPF, 5, 30),(@IRPF, 6, 28);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=102000.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 33),(@IRPF, 1, 33),(@IRPF, 2, 32),(@IRPF, 3, 32),(@IRPF, 4, 31),(@IRPF, 5, 30),(@IRPF, 6, 28);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=102000.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 33),(@IRPF, 1, 33),(@IRPF, 2, 32),(@IRPF, 3, 32),(@IRPF, 4, 31),(@IRPF, 5, 30),(@IRPF, 6, 28);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=111180.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 34),(@IRPF, 1, 34),(@IRPF, 2, 33),(@IRPF, 3, 33),(@IRPF, 4, 32),(@IRPF, 5, 32),(@IRPF, 6, 29);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=111180.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 34),(@IRPF, 1, 34),(@IRPF, 2, 33),(@IRPF, 3, 33),(@IRPF, 4, 32),(@IRPF, 5, 32),(@IRPF, 6, 29);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=111180.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 34),(@IRPF, 1, 34),(@IRPF, 2, 33),(@IRPF, 3, 33),(@IRPF, 4, 32),(@IRPF, 5, 32),(@IRPF, 6, 29);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=122160.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 35),(@IRPF, 1, 35),(@IRPF, 2, 35),(@IRPF, 3, 34),(@IRPF, 4, 33),(@IRPF, 5, 33),(@IRPF, 6, 31);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=122160.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 35),(@IRPF, 1, 35),(@IRPF, 2, 35),(@IRPF, 3, 34),(@IRPF, 4, 33),(@IRPF, 5, 33),(@IRPF, 6, 31);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=122160.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 35),(@IRPF, 1, 35),(@IRPF, 2, 35),(@IRPF, 3, 34),(@IRPF, 4, 33),(@IRPF, 5, 33),(@IRPF, 6, 31);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=135540.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 36),(@IRPF, 1, 36),(@IRPF, 2, 36),(@IRPF, 3, 35),(@IRPF, 4, 35),(@IRPF, 5, 34),(@IRPF, 6, 32);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=135540.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 36),(@IRPF, 1, 36),(@IRPF, 2, 36),(@IRPF, 3, 35),(@IRPF, 4, 35),(@IRPF, 5, 34),(@IRPF, 6, 32);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=135540.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 36),(@IRPF, 1, 36),(@IRPF, 2, 36),(@IRPF, 3, 35),(@IRPF, 4, 35),(@IRPF, 5, 34),(@IRPF, 6, 32);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=152070.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 37),(@IRPF, 1, 37),(@IRPF, 2, 37),(@IRPF, 3, 36),(@IRPF, 4, 36),(@IRPF, 5, 35),(@IRPF, 6, 34);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=152070.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 37),(@IRPF, 1, 37),(@IRPF, 2, 37),(@IRPF, 3, 36),(@IRPF, 4, 36),(@IRPF, 5, 35),(@IRPF, 6, 34);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=152070.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 37),(@IRPF, 1, 37),(@IRPF, 2, 37),(@IRPF, 3, 36),(@IRPF, 4, 36),(@IRPF, 5, 35),(@IRPF, 6, 34);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=172340.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 38),(@IRPF, 1, 38),(@IRPF, 2, 38),(@IRPF, 3, 37),(@IRPF, 4, 37),(@IRPF, 5, 36),(@IRPF, 6, 35);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=172340.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 38),(@IRPF, 1, 38),(@IRPF, 2, 38),(@IRPF, 3, 37),(@IRPF, 4, 37),(@IRPF, 5, 36),(@IRPF, 6, 35);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=172340.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 38),(@IRPF, 1, 38),(@IRPF, 2, 38),(@IRPF, 3, 37),(@IRPF, 4, 37),(@IRPF, 5, 36),(@IRPF, 6, 35);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=198860.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 39),(@IRPF, 1, 39),(@IRPF, 2, 39),(@IRPF, 3, 38),(@IRPF, 4, 38),(@IRPF, 5, 38),(@IRPF, 6, 36);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=198860.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 39),(@IRPF, 1, 39),(@IRPF, 2, 39),(@IRPF, 3, 38),(@IRPF, 4, 38),(@IRPF, 5, 38),(@IRPF, 6, 36);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=198860.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 39),(@IRPF, 1, 39),(@IRPF, 2, 39),(@IRPF, 3, 38),(@IRPF, 4, 38),(@IRPF, 5, 38),(@IRPF, 6, 36);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=235010.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 40),(@IRPF, 1, 40),(@IRPF, 2, 40),(@IRPF, 3, 40),(@IRPF, 4, 39),(@IRPF, 5, 39),(@IRPF, 6, 38);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=235010.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 40),(@IRPF, 1, 40),(@IRPF, 2, 40),(@IRPF, 3, 40),(@IRPF, 4, 39),(@IRPF, 5, 39),(@IRPF, 6, 38);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=235010.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 40),(@IRPF, 1, 40),(@IRPF, 2, 40),(@IRPF, 3, 40),(@IRPF, 4, 39),(@IRPF, 5, 39),(@IRPF, 6, 38);

# Trabajadores activos discapacitados
# 
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=0.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 9),(@IRPF, 1, 12),(@IRPF, 2, 12);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=0.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 9),(@IRPF, 1, 12),(@IRPF, 2, 12);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=0.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 9),(@IRPF, 1, 12),(@IRPF, 2, 12);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=22650.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 7),(@IRPF, 1, 12),(@IRPF, 2, 12);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=22650.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 7),(@IRPF, 1, 12),(@IRPF, 2, 12);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=22650.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 7),(@IRPF, 1, 12),(@IRPF, 2, 12);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=28650.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 6),(@IRPF, 1, 10),(@IRPF, 2, 10);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=28650.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 6),(@IRPF, 1, 10),(@IRPF, 2, 10);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=28650.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 6),(@IRPF, 1, 10),(@IRPF, 2, 10);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=41030.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 5),(@IRPF, 1, 9),(@IRPF, 2, 9);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=41030.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 5),(@IRPF, 1, 9),(@IRPF, 2, 9);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=41030.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 5),(@IRPF, 1, 9),(@IRPF, 2, 9);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=47290.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 4),(@IRPF, 1, 8),(@IRPF, 2, 8);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=47290.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 4),(@IRPF, 1, 8),(@IRPF, 2, 8);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=47290.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 4),(@IRPF, 1, 8),(@IRPF, 2, 8);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=66970.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 3),(@IRPF, 1, 6),(@IRPF, 2, 6);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=66970.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 3),(@IRPF, 1, 6),(@IRPF, 2, 6);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=66970.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 3),(@IRPF, 1, 6),(@IRPF, 2, 6);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=102000.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 2),(@IRPF, 1, 5),(@IRPF, 2, 5);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=102000.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 2),(@IRPF, 1, 5),(@IRPF, 2, 5);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=102000.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 2),(@IRPF, 1, 5),(@IRPF, 2, 5);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=172340.01 AND start_date='2012-01-01' AND geozone_code='01');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 1),(@IRPF, 1, 3),(@IRPF, 2, 3);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=172340.01 AND start_date='2012-01-01' AND geozone_code='20');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 1),(@IRPF, 1, 3),(@IRPF, 2, 3);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=172340.01 AND start_date='2012-01-01' AND geozone_code='48');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 1),(@IRPF, 1, 3),(@IRPF, 2, 3);


# Tablas de retención de IRPF 2012 de Navarra ( Ley Foral 2/2012 )
#
INSERT INTO geozone_irpf (geozone_code, start_date, end_date, amount ) VALUES
('31', '2012-01-01', '2012-12-31', 0.00 ),
('31', '2012-01-01', '2012-12-31', 11250.01 ),
('31', '2012-01-01', '2012-12-31', 12750.01 ),
('31', '2012-01-01', '2012-12-31', 14250.01 ),
('31', '2012-01-01', '2012-12-31', 16750.01 ),
('31', '2012-01-01', '2012-12-31', 19750.01 ),
('31', '2012-01-01', '2012-12-31', 23250.01 ),
('31', '2012-01-01', '2012-12-31', 25750.01 ),
('31', '2012-01-01', '2012-12-31', 28250.01 ),
('31', '2012-01-01', '2012-12-31', 32250.01 ),
('31', '2012-01-01', '2012-12-31', 35750.01 ),
('31', '2012-01-01', '2012-12-31', 41250.01 ),
('31', '2012-01-01', '2012-12-31', 48000.01 ),
('31', '2012-01-01', '2012-12-31', 55000.01 ),
('31', '2012-01-01', '2012-12-31', 62000.01 ),
('31', '2012-01-01', '2012-12-31', 69250.01 ),
('31', '2012-01-01', '2012-12-31', 75250.01 ),
('31', '2012-01-01', '2012-12-31', 82250.01 ),
('31', '2012-01-01', '2012-12-31', 94750.01 ),
('31', '2012-01-01', '2012-12-31', 107250.01 ),
('31', '2012-01-01', '2012-12-31', 120000.01 ),
('31', '2012-01-01', '2012-12-31', 132750.01 ),
('31', '2012-01-01', '2012-12-31', 146000.01 );



SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=11250.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 4.10),(@IRPF, 1, 2.00),(@IRPF, 2, 0.00),(@IRPF, 3, 0.00),(@IRPF, 4, 0.00),(@IRPF, 5, 0.00),(@IRPF, 6, 0.00),(@IRPF, 7, 0.00),(@IRPF, 8, 0.00),(@IRPF, 9, 0.00),(@IRPF, 10, 0.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=12750.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 6.20),(@IRPF, 1, 4.10),(@IRPF, 2, 2.10),(@IRPF, 3, 0.10),(@IRPF, 4, 0.00),(@IRPF, 5, 0.00),(@IRPF, 6, 0.00),(@IRPF, 7, 0.00),(@IRPF, 8, 0.00),(@IRPF, 9, 0.00),(@IRPF, 10, 0.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=14250.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 8.20),(@IRPF, 1, 6.20),(@IRPF, 2, 4.10),(@IRPF, 3, 2.10),(@IRPF, 4, 0.00),(@IRPF, 5, 0.00),(@IRPF, 6, 0.00),(@IRPF, 7, 0.00),(@IRPF, 8, 0.00),(@IRPF, 9, 0.00),(@IRPF, 10, 0.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=16750.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 10.20),(@IRPF, 1, 8.20),(@IRPF, 2, 6.20),(@IRPF, 3, 4.20),(@IRPF, 4, 1.10),(@IRPF, 5, 0.10),(@IRPF, 6, 0.00),(@IRPF, 7, 0.00),(@IRPF, 8, 0.00),(@IRPF, 9, 0.00),(@IRPF, 10, 0.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=19750.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 12.30),(@IRPF, 1, 11.30),(@IRPF, 2, 9.70),(@IRPF, 3, 8.20),(@IRPF, 4, 6.20),(@IRPF, 5, 4.10),(@IRPF, 6, 0.10),(@IRPF, 7, 0.00),(@IRPF, 8, 0.00),(@IRPF, 9, 0.00),(@IRPF, 10, 0.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=23250.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 13.80),(@IRPF, 1, 12.30),(@IRPF, 2, 11.80),(@IRPF, 3, 9.20),(@IRPF, 4, 8.20),(@IRPF, 5, 6.20),(@IRPF, 6, 4.10),(@IRPF, 7, 0.10),(@IRPF, 8, 0.00),(@IRPF, 9, 0.00),(@IRPF, 10, 0.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=25750.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 14.90),(@IRPF, 1, 13.40),(@IRPF, 2, 12.80),(@IRPF, 3, 10.30),(@IRPF, 4, 9.70),(@IRPF, 5, 8.20),(@IRPF, 6, 6.20),(@IRPF, 7, 4.10),(@IRPF, 8, 1.10),(@IRPF, 9, 0.00),(@IRPF, 10, 0.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=28250.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 16.00),(@IRPF, 1, 14.50),(@IRPF, 2, 13.90),(@IRPF, 3, 12.40),(@IRPF, 4, 10.80),(@IRPF, 5, 9.20),(@IRPF, 6, 8.20),(@IRPF, 7, 6.10),(@IRPF, 8, 4.10),(@IRPF, 9, 1.10),(@IRPF, 10, 0.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=32250.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 17.10),(@IRPF, 1, 15.60),(@IRPF, 2, 15.10),(@IRPF, 3, 13.50),(@IRPF, 4, 12.90),(@IRPF, 5, 11.30),(@IRPF, 6, 10.20),(@IRPF, 7, 8.20),(@IRPF, 8, 7.20),(@IRPF, 9, 5.10),(@IRPF, 10, 2.10);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=35750.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 18.20),(@IRPF, 1, 17.20),(@IRPF, 2, 17.10),(@IRPF, 3, 14.60),(@IRPF, 4, 14.00),(@IRPF, 5, 13.40),(@IRPF, 6, 12.30),(@IRPF, 7, 10.30),(@IRPF, 8, 9.20),(@IRPF, 9, 7.20),(@IRPF, 10, 6.10);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=41250.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 19.40),(@IRPF, 1, 18.30),(@IRPF, 2, 18.30),(@IRPF, 3, 16.70),(@IRPF, 4, 16.10),(@IRPF, 5, 15.60),(@IRPF, 6, 13.50),(@IRPF, 7, 12.40),(@IRPF, 8, 11.30),(@IRPF, 9, 10.20),(@IRPF, 10, 9.20);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=48000.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 21.50),(@IRPF, 1, 21.50),(@IRPF, 2, 20.90),(@IRPF, 3, 18.90),(@IRPF, 4, 18.30),(@IRPF, 5, 17.70),(@IRPF, 6, 16.60),(@IRPF, 7, 15.60),(@IRPF, 8, 14.50),(@IRPF, 9, 13.40),(@IRPF, 10, 12.30);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=55000.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 24.10),(@IRPF, 1, 23.50),(@IRPF, 2, 23.00),(@IRPF, 3, 21.50),(@IRPF, 4, 21.50),(@IRPF, 5, 20.90),(@IRPF, 6, 19.80),(@IRPF, 7, 18.70),(@IRPF, 8, 17.70),(@IRPF, 9, 16.60),(@IRPF, 10, 15.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=62000.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 26.10),(@IRPF, 1, 25.50),(@IRPF, 2, 24.50),(@IRPF, 3, 24.50),(@IRPF, 4, 23.50),(@IRPF, 5, 22.90),(@IRPF, 6, 21.90),(@IRPF, 7, 21.40),(@IRPF, 8, 19.80),(@IRPF, 9, 18.70),(@IRPF, 10, 17.20);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=69250.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 28.30),(@IRPF, 1, 27.70),(@IRPF, 2, 27.20),(@IRPF, 3, 26.60),(@IRPF, 4, 25.10),(@IRPF, 5, 25.00),(@IRPF, 6, 24.40),(@IRPF, 7, 22.90),(@IRPF, 8, 21.80),(@IRPF, 9, 20.30),(@IRPF, 10, 19.30);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=75250.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 29.40),(@IRPF, 1, 29.40),(@IRPF, 2, 28.30),(@IRPF, 3, 27.30),(@IRPF, 4, 27.20),(@IRPF, 5, 26.20),(@IRPF, 6, 26.10),(@IRPF, 7, 24.50),(@IRPF, 8, 23.40),(@IRPF, 9, 22.30),(@IRPF, 10, 21.30);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=82250.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 30.50),(@IRPF, 1, 30.50),(@IRPF, 2, 30.50),(@IRPF, 3, 29.40),(@IRPF, 4, 29.40),(@IRPF, 5, 28.30),(@IRPF, 6, 27.70),(@IRPF, 7, 26.60),(@IRPF, 8, 25.60),(@IRPF, 9, 24.50),(@IRPF, 10, 23.90);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=94750.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 31.70),(@IRPF, 1, 31.70),(@IRPF, 2, 31.70),(@IRPF, 3, 30.60),(@IRPF, 4, 30.60),(@IRPF, 5, 30.50),(@IRPF, 6, 29.50),(@IRPF, 7, 28.90),(@IRPF, 8, 27.80),(@IRPF, 9, 26.80),(@IRPF, 10, 25.70);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=107250.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 33.90),(@IRPF, 1, 33.90),(@IRPF, 2, 33.90),(@IRPF, 3, 32.90),(@IRPF, 4, 32.30),(@IRPF, 5, 31.70),(@IRPF, 6, 31.20),(@IRPF, 7, 30.10),(@IRPF, 8, 29.50),(@IRPF, 9, 28.50),(@IRPF, 10, 27.40);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=120000.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 35.10),(@IRPF, 1, 35.10),(@IRPF, 2, 34.60),(@IRPF, 3, 34.00),(@IRPF, 4, 33.50),(@IRPF, 5, 32.90),(@IRPF, 6, 32.40),(@IRPF, 7, 31.30),(@IRPF, 8, 30.70),(@IRPF, 9, 29.70),(@IRPF, 10, 29.10);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=132750.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 35.80),(@IRPF, 1, 35.70),(@IRPF, 2, 35.20),(@IRPF, 3, 34.70),(@IRPF, 4, 34.10),(@IRPF, 5, 33.60),(@IRPF, 6, 33.00),(@IRPF, 7, 32.50),(@IRPF, 8, 31.90),(@IRPF, 9, 30.80),(@IRPF, 10, 30.30);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=146000.01 AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 36.40),(@IRPF, 1, 36.40),(@IRPF, 2, 36.40),(@IRPF, 3, 35.80),(@IRPF, 4, 35.30),(@IRPF, 5, 34.70),(@IRPF, 6, 34.20),(@IRPF, 7, 33.60),(@IRPF, 8, 33.10),(@IRPF, 9, 32.50),(@IRPF, 10, 31.90);

# trabajadores en activo discapacitados
#
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=11250.01  AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 5),(@IRPF, 1, 15),(@IRPF, 2, 15);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=23250.01  AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 3),(@IRPF, 1, 15),(@IRPF, 2, 15);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=41250.01  AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 2),(@IRPF, 1, 8),(@IRPF, 2, 8);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=94750.01  AND start_date='2012-01-01' AND geozone_code='31');
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 2),(@IRPF, 1, 2),(@IRPF, 2, 5);


UPDATE `db_version` SET `version_number` = '7.1.2';

COMMIT;
