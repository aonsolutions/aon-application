# Database: aon_master
# Version: Actualizacion de la version 6.3.0 a la version 6.3.1.
# Created by: girazu
# Creation Date: 18/03/2011 14:06
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;


INSERT INTO system_data 
( name			, expression	, start_date	, end_date	, read_only	, comments ) 
VALUES 
('COTIZACION_IT', '"MENSUAL"'	, '2010-01-01'	, NULL		, 0			, 'Prorrateo de la cotización por incapacidad temporal' );

UPDATE payment_concept 
	SET code="ECEMP"
	, irpf_expression='ECEMP' 
	, expression='DIAS_ENFERMEDAD_COMUN_4_15 * BASE_REGULADORA * 0.60' 
	, quote_expression='( COTIZACION_IT == "MENSUAL" ? 30 - ( DIAS_MES - DIAS_ENFERMEDAD_COMUN) : DIAS_ENFERMEDAD_COMUN ) * BASE_REGULADORA'
WHERE id=1;

UPDATE payment_concept 
	SET code="ECSS"
	, irpf_expression='ECSS' 
	, expression='DIAS_ENFERMEDAD_COMUN_16_20 * BASE_REGULADORA * 0.60 + DIAS_ENFERMEDAD_COMUN_21 * BASE_REGULADORA * 0.75' 
	, quote_expression=NULL
WHERE id=2;

UPDATE payment_concept 
	SET quote_expression='( COTIZACION_IT == "MENSUAL" ? 30 - ( DIAS_MES - DIAS_MATERNIDAD) : DIAS_MATERNIDAD ) * BASE_REGULADORA' 
WHERE id=3;

UPDATE payment_concept 
	SET irpf_expression='ATEP'
	, expression='DIAS_ENFERMEDAD_PROFESIONAL * BASE_REGULADORA * 0.75' 
	, quote_expression='( COTIZACION_IT == "MENSUAL" ? 30 - ( DIAS_MES - DIAS_ENFERMEDAD_PROFESIONAL) : DIAS_ENFERMEDAD_PROFESIONAL ) * BASE_REGULADORA'
WHERE id=4;

INSERT INTO `payment_concept` 
 (id	, code	, type	, expression, quote_expression	, irpf_expression	, description							, description_decorable) 
VALUES 
 (5		,'GTZDO', 1		,'GARANTIZADO*DIAS_GARANTIZADOS/DIAS_MES>TOTAL_PRESTACIONES_IT?GARANTIZADO*DIAS_GARANTIZADOS/DIAS_MES-TOTAL_PRESTACIONES_IT:0.00'		
 									, NULL				, 'GTZDO'			, 'GARANTIZADO EMPRESA SITUACION I.T.'	,1);

INSERT INTO `system_payment` (payment_concept, type, expression, irpf_expression, quote_expression,  description, description_decorable, start_date, end_date, month,  salary_type) VALUES 
  (5,NULL,NULL,NULL,NULL, NULL,0,'2010-01-01',NULL,NULL,0);

ALTER TABLE `system_data` MODIFY `name` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre';
ALTER TABLE `contract_data` MODIFY `name` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre';
ALTER TABLE `agreement_level_data` MODIFY `name` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre';

ALTER TABLE `system_data` MODIFY `expression` TEXT collate latin1_spanish_ci COMMENT 'Expresion';

UPDATE system_data SET
end_date='2010-12-31' ,
expression = '[	"01":"TIEMPO_COMPLETO ? 1031.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 6.22 * HORAS_NOMINA", 
				"02":"TIEMPO_COMPLETO ? 855.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.16 * HORAS_NOMINA", 
				"03":"TIEMPO_COMPLETO ? 744.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.49 * HORAS_NOMINA", 
				"04":"TIEMPO_COMPLETO ? 738.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.45 * HORAS_NOMINA", 
				"05":"TIEMPO_COMPLETO ? 738.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.45 * HORAS_NOMINA", 
				"06":"TIEMPO_COMPLETO ? 738.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.45 * HORAS_NOMINA", 
				"07":"TIEMPO_COMPLETO ? 738.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.45 * HORAS_NOMINA",
				"08":"TIEMPO_COMPLETO ? 24.63 * DIAS_NOMINA : 4.45 * HORAS_NOMINA", 
				"09":"TIEMPO_COMPLETO ? 24.63 * DIAS_NOMINA : 4.45 * HORAS_NOMINA", 
				"10":"TIEMPO_COMPLETO ? 24.63 * DIAS_NOMINA : 4.45 * HORAS_NOMINA", 
				"11":"TIEMPO_COMPLETO ? 24.63 * DIAS_NOMINA : 4.45 * HORAS_NOMINA"]'
WHERE id=1;

UPDATE system_data SET
end_date='2010-12-31' ,
expression = '[	"01": "3198.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) ", 
				"02": "3198.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)", 
				"03": "3198.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)", 
				"04": "3198.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)", 
				"05": "3198.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)", 
				"06": "3198.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)", 
				"07": "3198.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)", 
				"08": "106.60 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)", 
				"09": "106.60 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)", 
				"10": "106.60 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)", 
				"11": "106.60 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)"]'
WHERE id=2;


INSERT INTO system_data 
( name			, expression				, start_date	, end_date	, read_only	, comments ) 
VALUES 
('BASE_CGC_MIN',  '["01": "TIEMPO_COMPLETO ? 1045.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 6.30 * HORAS_NOMINA", 
					"02": "TIEMPO_COMPLETO ? 867.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.22 * HORAS_NOMINA", 
					"03": "TIEMPO_COMPLETO ? 754.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.55 * HORAS_NOMINA", 
					"04": "TIEMPO_COMPLETO ? 748.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.51 * HORAS_NOMINA", 
					"05": "TIEMPO_COMPLETO ? 748.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.51 * HORAS_NOMINA", 
					"06": "TIEMPO_COMPLETO ? 748.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.51 * HORAS_NOMINA ",
					"07": "TIEMPO_COMPLETO ? 748.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.51 * HORAS_NOMINA",
					"08": "TIEMPO_COMPLETO ? 24.94 * DIAS_NOMINA : 4.51 * HORAS_NOMINA", 
					"09": "TIEMPO_COMPLETO ? 24.94 * DIAS_NOMINA : 4.51 * HORAS_NOMINA", 
					"10": "TIEMPO_COMPLETO ? 24.94 * DIAS_NOMINA : 4.51 * HORAS_NOMINA", 
					"11": "TIEMPO_COMPLETO ? 24.94 * DIAS_NOMINA : 4.51 * HORAS_NOMINA"]'            
											,'2011-01-01' 	, null 		, 1 		, 'Bases mínimas');
INSERT INTO system_data 
( name			, expression				, start_date	, end_date	, read_only	, comments ) 
VALUES 
('BASE_CGC_MAX',  '["01": "3230.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)", 
					"02": "3230.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)", 
					"03": "3230.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)", 
					"04": "3230.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)", 
					"05": "3230.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)", 
					"06": "3230.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)", 
					"07": "3230.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)",
					"08": "107.67 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)", 
					"09": "107.67 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)", 
					"10": "107.67 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)", 
					"11": "107.67 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)"]'            
											,'2011-01-01' 	, null 		, 1 		, 'Bases máximas');

INSERT INTO system_data 
( name			, expression	, start_date	, end_date		, read_only	, comments ) 
VALUES 
('BASE_CGP_MAX'	, '"3198.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)"'
								, '2010-01-01'	, '2010-12-31'	, 0			, 'Tope Mínimo de cotización para Accidentes de Trabajo y Enfermedades Profesionales' );

INSERT INTO system_data 
( name			, expression	, start_date	, end_date		, read_only	, comments ) 
VALUES 
('BASE_CGP_MIN'	, '"TIEMPO_COMPLETO ? 738.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.45 * HORAS_NOMINA"'
								, '2010-01-01'	, '2010-12-31'	, 0			, 'Tope Máximo de cotización para Accidentes de Trabajo y Enfermedades Profesionales' );

INSERT INTO system_data 
( name			, expression	, start_date	, end_date	, read_only	, comments ) 
VALUES 
('BASE_CGP_MAX'	, '"3230.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)"'
								, '2011-01-01'	, NULL		, 0			, 'Tope Mínimo de cotización para Accidentes de Trabajo y Enfermedades Profesionales' );

INSERT INTO system_data 
( name			, expression, start_date	, end_date	, read_only	, comments ) 
VALUES 
('BASE_CGP_MIN'	, '"TIEMPO_COMPLETO ? 748.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.51 * HORAS_NOMINA "'
							, '2011-01-01'	, NULL		, 0			, 'Tope Máximo de cotización para Accidentes de Trabajo y Enfermedades Profesionales' );

INSERT INTO system_data 
( name					, expression, start_date	, end_date	, read_only	, comments ) 
VALUES 
('INGRESO_AC_EMPRESA'	,'false'	, '1979-01-01'	, NULL		, 0			, 'Ingreso a cuenta a cargo de la empresa' );

							UPDATE deduction_concept 
SET expression = 'ASIMILADO_REGIMEN_GRAL ? 0 : BASE_CGP * (INDEFINIDO ? 1.55 : 1.60 )/100' 
WHERE id=3;

# 
# Embargos
# 

CREATE TABLE `contract_embargo` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `amount` double(15,3) default '0.000' COMMENT 'Importe',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Formula',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_EMBARGO_CONTRACT` (`contract`),
  CONSTRAINT `FK_EMBARGO_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=822 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Embargos';

CREATE TABLE `salary_embargo` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios',
  `contract_embargo` int(4) NOT NULL COMMENT 'Embargo',
  `amount` double(15,3) default '0.000' COMMENT 'Importe',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_EMBARGO_SALARY` (`salary`),
  CONSTRAINT `FK_EMBARGO_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`),
  KEY `IDX_SALARY_EMBARGO_CONTRACT_EMBARGO` (`contract_embargo`),
  CONSTRAINT `FK_SALARY_EMBARGO_CONTRACT_EMBARGO` FOREIGN KEY (`contract_embargo`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=63432 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Embargos';

# 
# Costos
# 

CREATE TABLE `system_cost` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Formula',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Costos';

CREATE TABLE `salary_cost` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios',
  `amount` double(15,3) default '0.000' COMMENT 'Importe',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_COST_SALARY` (`salary`),
  CONSTRAINT `FK_COST_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=63432 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Costos';


INSERT INTO system_data 
( name	, expression, start_date	, end_date	, read_only	, comments ) 
VALUES 
('SMI'	, '633.30'	, '2010-01-01'	, NULL		, 0			, 'Salario mínimo interprofesional' );

INSERT INTO system_data 
( name	, expression, start_date	, end_date	, read_only	, comments ) 
VALUES 
('SMI'	, '641.40'	, '2011-01-01'	, NULL		, 0			, 'Salario mínimo interprofesional' );

INSERT INTO system_data 
( name				, expression, start_date	, end_date	, read_only	, comments ) 
VALUES 
('MAX_EMBARGABLE'	, '"((TOTAL_LIQUIDO > SMI) ? ( TOTAL_LIQUIDO - SMI ) * 0.30 : 0.00) 
					+ ((TOTAL_LIQUIDO > 2 * SMI) ? ( TOTAL_LIQUIDO - 2 * SMI ) * 0.20 : 0.00) 
					+ ((TOTAL_LIQUIDO > 3 * SMI) ? ( TOTAL_LIQUIDO - 3 * SMI ) * 0.10 : 0.00) 
					+ ((TOTAL_LIQUIDO > 4 * SMI) ? ( TOTAL_LIQUIDO - 4 * SMI ) * 0.15 : 0.00) 
					+ ((TOTAL_LIQUIDO > 5 * SMI) ? ( TOTAL_LIQUIDO - 5 * SMI ) * 0.15 : 0.00) - EMBARGADO"'	
								, '2000-01-01'	, NULL		, 0			, 'Máximo embargable' );

INSERT INTO system_data 
( name			, expression	, start_date	, end_date	, read_only	,comments ) 
VALUES 
('BASE_IPREM'	, '0.00'       	,'2011-01-01' 	, null 		,1 			,'Base para los conceptos exentos de cotizacion (IPREM) ');

INSERT INTO system_data 
( name			, expression	, start_date	, end_date	, read_only	,comments ) 
VALUES 
('BIPREM'		, '0.00'       	,'2011-01-01' 	, null 		,1 			,'Base para los conceptos exentos de cotizacion (IPREM) ');

UPDATE  deduction_concept SET expression='(TIEMPO_COMPLETO && ASIMILADO_REGIMEN_GRAL ) ? 0 : BASE_CGP * (INDEFINIDO ? 1.55 : 1.60 )/100' WHERE id=3;

UPDATE `db_version` SET `version_number` = '6.3.1';

COMMIT;
