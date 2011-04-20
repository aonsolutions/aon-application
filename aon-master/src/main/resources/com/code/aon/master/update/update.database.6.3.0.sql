# Database: aon_master
# Version: Actualizacion de la version 6.3.0 a la version 6.3.1.
# Created by: girazu
# Creation Date: 14/04/2011 13:11
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

DROP TABLE `cv_evaluate`;

DROP TABLE `cv_evaluate_summary`;

DROP TABLE `cv_evaluate_type`;

DROP TABLE `cv_knowledge`;

DROP TABLE `cv_languages`;

DROP TABLE `cv_studies`;

DROP TABLE `cv_workexperience`;

DROP TABLE `lh_contract`;

DROP TABLE `lh_course`;

DROP TABLE `lh_position`;

DROP TABLE `lh_work`;

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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Embargos';

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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Embargos';

CREATE TABLE `system_cost` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Formula',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Costos';

CREATE TABLE `salary_cost` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios',
  `amount` double(15,3) default '0.000' COMMENT 'Importe',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_COST_SALARY` (`salary`),
  CONSTRAINT `FK_COST_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Costos';


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

CREATE TABLE `cno` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `code` varchar(5) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo del CNO',
  `title` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Titulo del CNO',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='CNO';

INSERT INTO cno (id, code, title) VALUES(0011, '0011', 'Oficiales de las fuerzas armadas');
INSERT INTO cno (id, code, title) VALUES(0012, '0012', 'Suboficiales de las fuerzas armadas');
INSERT INTO cno (id, code, title) VALUES(0020, '0020', 'Tropa y marinería de las fuerzas armadas');
INSERT INTO cno (id, code, title) VALUES(1111, '1111', 'Miembros del poder ejecutivo (nacional, autonómico y local) y del poder legislativo');
INSERT INTO cno (id, code, title) VALUES(1112, '1112', 'Personal directivo de la Administración Pública');
INSERT INTO cno (id, code, title) VALUES(1113, '1113', 'Directores de organizaciones de interés social');
INSERT INTO cno (id, code, title) VALUES(1120, '1120', 'Directores generales y presidentes ejecutivos');
INSERT INTO cno (id, code, title) VALUES(1211, '1211', 'Directores financieros');
INSERT INTO cno (id, code, title) VALUES(1212, '1212', 'Directores de recursos humanos');
INSERT INTO cno (id, code, title) VALUES(1219, '1219', 'Directores de políticas y planificación y de otros departamentos administrativos no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(1221, '1221', 'Directores comerciales y de ventas');
INSERT INTO cno (id, code, title) VALUES(1222, '1222', 'Directores de publicidad y relaciones públicas');
INSERT INTO cno (id, code, title) VALUES(1223, '1223', 'Directores de investigación y desarrollo');
INSERT INTO cno (id, code, title) VALUES(1311, '1311', 'Directores de producción de explotaciones agropecuarias y forestales');
INSERT INTO cno (id, code, title) VALUES(1312, '1312', 'Directores de producción de explotaciones pesqueras y acuícolas');
INSERT INTO cno (id, code, title) VALUES(1313, '1313', 'Directores de industrias manufactureras');
INSERT INTO cno (id, code, title) VALUES(1314, '1314', 'Directores de explotaciones mineras');
INSERT INTO cno (id, code, title) VALUES(1315, '1315', 'Directores de empresas de abastecimiento, transporte, distribución y afines');
INSERT INTO cno (id, code, title) VALUES(1316, '1316', 'Directores de empresas de construcción');
INSERT INTO cno (id, code, title) VALUES(1321, '1321', 'Directores de servicios de tecnologías de la información y las comunicaciones (TIC)');
INSERT INTO cno (id, code, title) VALUES(1322, '1322', 'Directores de servicios sociales para niños');
INSERT INTO cno (id, code, title) VALUES(1323, '1323', 'Directores-gerentes de centros sanitarios');
INSERT INTO cno (id, code, title) VALUES(1324, '1324', 'Directores de servicios sociales para personas mayores');
INSERT INTO cno (id, code, title) VALUES(1325, '1325', 'Directores de otros servicios sociales');
INSERT INTO cno (id, code, title) VALUES(1326, '1326', 'Directores de servicios de educación');
INSERT INTO cno (id, code, title) VALUES(1327, '1327', 'Directores de sucursales de bancos, de servicios financieros y de seguros');
INSERT INTO cno (id, code, title) VALUES(1329, '1329', 'Directores de otras empresas de servicios profesionales no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(1411, '1411', 'Directores y gerentes de hoteles');
INSERT INTO cno (id, code, title) VALUES(1419, '1419', 'Directores y gerentes de otras empresas de servicios de alojamiento');
INSERT INTO cno (id, code, title) VALUES(1421, '1421', 'Directores y gerentes de restaurantes');
INSERT INTO cno (id, code, title) VALUES(1422, '1422', 'Directores y gerentes de bares, cafeterías y similares');
INSERT INTO cno (id, code, title) VALUES(1429, '1429', 'Directores y gerentes de empresas de catering y otras empresas de restauración');
INSERT INTO cno (id, code, title) VALUES(1431, '1431', 'Directores y gerentes de empresas de comercio al por mayor');
INSERT INTO cno (id, code, title) VALUES(1432, '1432', 'Directores y gerentes de empresas de comercio al por menor');
INSERT INTO cno (id, code, title) VALUES(1501, '1501', 'Directores y gerentes de empresas de actividades recreativas, culturales y deportivas');
INSERT INTO cno (id, code, title) VALUES(1509, '1509', 'Directores y gerentes de empresas de gestión de residuos y de otras empresas de servicios no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(2111, '2111', 'Médicos de familia');
INSERT INTO cno (id, code, title) VALUES(2112, '2112', 'Otros médicos especialistas');
INSERT INTO cno (id, code, title) VALUES(2121, '2121', 'Enfermeros no especializados');
INSERT INTO cno (id, code, title) VALUES(2122, '2122', 'Enfermeros especializados (excepto matronos)');
INSERT INTO cno (id, code, title) VALUES(2123, '2123', 'Matronos');
INSERT INTO cno (id, code, title) VALUES(2130, '2130', 'Veterinarios');
INSERT INTO cno (id, code, title) VALUES(2140, '2140', 'Farmacéuticos');
INSERT INTO cno (id, code, title) VALUES(2151, '2151', 'Odontólogos y estomatólogos');
INSERT INTO cno (id, code, title) VALUES(2152, '2152', 'Fisioterapeutas');
INSERT INTO cno (id, code, title) VALUES(2153, '2153', 'Dietistas y nutricionistas');
INSERT INTO cno (id, code, title) VALUES(2154, '2154', 'Logopedas');
INSERT INTO cno (id, code, title) VALUES(2155, '2155', 'Ópticos-optometristas');
INSERT INTO cno (id, code, title) VALUES(2156, '2156', 'Terapeutas ocupacionales');
INSERT INTO cno (id, code, title) VALUES(2157, '2157', 'Podólogos');
INSERT INTO cno (id, code, title) VALUES(2158, '2158', 'Profesionales de la salud y la higiene laboral y ambiental');
INSERT INTO cno (id, code, title) VALUES(2159, '2159', 'Profesionales de la salud no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(2210, '2210', 'Profesores de universidades y otra enseñanza superior (excepto formación profesional)');
INSERT INTO cno (id, code, title) VALUES(2220, '2220', 'Profesores de formación profesional (materias específicas)');
INSERT INTO cno (id, code, title) VALUES(2230, '2230', 'Profesores de enseñanza secundaria (excepto materias específicas de formación profesional)');
INSERT INTO cno (id, code, title) VALUES(2240, '2240', 'Profesores de enseñanza primaria');
INSERT INTO cno (id, code, title) VALUES(2251, '2251', 'Maestros de educación infantil');
INSERT INTO cno (id, code, title) VALUES(2252, '2252', 'Técnicos en educación infantil');
INSERT INTO cno (id, code, title) VALUES(2311, '2311', 'Profesores de educación especial');
INSERT INTO cno (id, code, title) VALUES(2312, '2312', 'Técnicos educadores de educación especial');
INSERT INTO cno (id, code, title) VALUES(2321, '2321', 'Especialistas en métodos didácticos y pedagógicos');
INSERT INTO cno (id, code, title) VALUES(2322, '2322', 'Profesores de enseñanza no reglada de idiomas');
INSERT INTO cno (id, code, title) VALUES(2323, '2323', 'Profesores de enseñanza no reglada de música y danza');
INSERT INTO cno (id, code, title) VALUES(2324, '2324', 'Profesores de enseñanza no reglada de artes ');
INSERT INTO cno (id, code, title) VALUES(2325, '2325', 'Instructores en tecnologías de la información en enseñanza no reglada');
INSERT INTO cno (id, code, title) VALUES(2326, '2326', 'Profesionales de la educación ambiental');
INSERT INTO cno (id, code, title) VALUES(2329, '2329', 'Profesores y profesionales de la enseñanza no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(2411, '2411', 'Físicos y astrónomos');
INSERT INTO cno (id, code, title) VALUES(2412, '2412', 'Meteorólogos');
INSERT INTO cno (id, code, title) VALUES(2413, '2413', 'Químicos');
INSERT INTO cno (id, code, title) VALUES(2414, '2414', 'Geólogos y geofísicos');
INSERT INTO cno (id, code, title) VALUES(2415, '2415', 'Matemáticos y actuarios');
INSERT INTO cno (id, code, title) VALUES(2416, '2416', 'Estadísticos');
INSERT INTO cno (id, code, title) VALUES(2421, '2421', 'Biólogos, botánicos, zoólogos y afines');
INSERT INTO cno (id, code, title) VALUES(2422, '2422', 'Ingenieros agrónomos');
INSERT INTO cno (id, code, title) VALUES(2423, '2423', 'Ingenieros de montes');
INSERT INTO cno (id, code, title) VALUES(2424, '2424', 'Ingenieros técnicos agrícolas');
INSERT INTO cno (id, code, title) VALUES(2425, '2425', 'Ingenieros técnicos forestales y del medio natural');
INSERT INTO cno (id, code, title) VALUES(2426, '2426', 'Profesionales de la protección ambiental');
INSERT INTO cno (id, code, title) VALUES(2427, '2427', 'Enólogos');
INSERT INTO cno (id, code, title) VALUES(2431, '2431', 'Ingenieros industriales y de producción');
INSERT INTO cno (id, code, title) VALUES(2432, '2432', 'Ingenieros en construcción y obra civil');
INSERT INTO cno (id, code, title) VALUES(2433, '2433', 'Ingenieros mecánicos');
INSERT INTO cno (id, code, title) VALUES(2434, '2434', 'Ingenieros aeronáuticos');
INSERT INTO cno (id, code, title) VALUES(2435, '2435', 'Ingenieros químicos');
INSERT INTO cno (id, code, title) VALUES(2436, '2436', 'Ingenieros de minas, metalúrgicos y afines');
INSERT INTO cno (id, code, title) VALUES(2437, '2437', 'Ingenieros ambientales');
INSERT INTO cno (id, code, title) VALUES(2439, '2439', 'Ingenieros no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(2441, '2441', 'Ingenieros en electricidad');
INSERT INTO cno (id, code, title) VALUES(2442, '2442', 'Ingenieros electrónicos');
INSERT INTO cno (id, code, title) VALUES(2443, '2443', 'Ingenieros en telecomunicaciones');
INSERT INTO cno (id, code, title) VALUES(2451, '2451', 'Arquitectos (excepto arquitectos paisajistas y urbanistas)');
INSERT INTO cno (id, code, title) VALUES(2452, '2452', 'Arquitectos paisajistas');
INSERT INTO cno (id, code, title) VALUES(2453, '2453', 'Urbanistas e ingenieros de tráfico');
INSERT INTO cno (id, code, title) VALUES(2454, '2454', 'Ingenieros geógrafos y cartógrafos');
INSERT INTO cno (id, code, title) VALUES(2461, '2461', 'Ingenieros técnicos industriales y de producción');
INSERT INTO cno (id, code, title) VALUES(2462, '2462', 'Ingenieros técnicos de obras públicas');
INSERT INTO cno (id, code, title) VALUES(2463, '2463', 'Ingenieros técnicos mecánicos');
INSERT INTO cno (id, code, title) VALUES(2464, '2464', 'Ingenieros técnicos aeronáuticos');
INSERT INTO cno (id, code, title) VALUES(2465, '2465', 'Ingenieros técnicos químicos');
INSERT INTO cno (id, code, title) VALUES(2466, '2466', 'Ingenieros técnicos de minas, metalúrgicos y afines');
INSERT INTO cno (id, code, title) VALUES(2469, '2469', 'Ingenieros técnicos no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(2471, '2471', 'Ingenieros técnicos en electricidad');
INSERT INTO cno (id, code, title) VALUES(2472, '2472', 'Ingenieros técnicos en electrónica');
INSERT INTO cno (id, code, title) VALUES(2473, '2473', 'Ingenieros técnicos en telecomunicaciones');
INSERT INTO cno (id, code, title) VALUES(2481, '2481', 'Arquitectos técnicos y técnicos urbanistas');
INSERT INTO cno (id, code, title) VALUES(2482, '2482', 'Diseñadores de productos y de prendas');
INSERT INTO cno (id, code, title) VALUES(2483, '2483', 'Ingenieros técnicos en topografía');
INSERT INTO cno (id, code, title) VALUES(2484, '2484', 'Diseñadores gráficos y multimedia');
INSERT INTO cno (id, code, title) VALUES(2511, '2511', 'Abogados');
INSERT INTO cno (id, code, title) VALUES(2512, '2512', 'Fiscales');
INSERT INTO cno (id, code, title) VALUES(2513, '2513', 'Jueces y magistrados');
INSERT INTO cno (id, code, title) VALUES(2591, '2591', 'Notarios y registradores');
INSERT INTO cno (id, code, title) VALUES(2592, '2592', 'Procuradores');
INSERT INTO cno (id, code, title) VALUES(2599, '2599', 'Profesionales del derecho no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(2611, '2611', 'Especialistas en contabilidad');
INSERT INTO cno (id, code, title) VALUES(2612, '2612', 'Asesores financieros y en inversiones');
INSERT INTO cno (id, code, title) VALUES(2613, '2613', 'Analistas financieros');
INSERT INTO cno (id, code, title) VALUES(2621, '2621', 'Analistas de gestión y organización');
INSERT INTO cno (id, code, title) VALUES(2622, '2622', 'Especialistas en administración de política de empresas');
INSERT INTO cno (id, code, title) VALUES(2623, '2623', 'Especialistas de la Administración Pública');
INSERT INTO cno (id, code, title) VALUES(2624, '2624', 'Especialistas en políticas y servicios de personal y afines');
INSERT INTO cno (id, code, title) VALUES(2625, '2625', 'Especialistas en formación de personal');
INSERT INTO cno (id, code, title) VALUES(2630, '2630', 'Técnicos de empresas y actividades turísticas');
INSERT INTO cno (id, code, title) VALUES(2640, '2640', 'Profesionales de ventas técnicas y médicas (excepto las TIC)');
INSERT INTO cno (id, code, title) VALUES(2651, '2651', 'Profesionales de la publicidad y la comercialización');
INSERT INTO cno (id, code, title) VALUES(2652, '2652', 'Profesionales de relaciones públicas');
INSERT INTO cno (id, code, title) VALUES(2653, '2653', 'Profesionales de la venta de tecnologías de la información y las comunicaciones');
INSERT INTO cno (id, code, title) VALUES(2711, '2711', 'Analistas de sistemas');
INSERT INTO cno (id, code, title) VALUES(2712, '2712', 'Analistas y diseñadores de software');
INSERT INTO cno (id, code, title) VALUES(2713, '2713', 'Analistas, programadores y diseñadores Web y multimedia');
INSERT INTO cno (id, code, title) VALUES(2719, '2719', 'Analistas y diseñadores de software y multimedia no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(2721, '2721', 'Diseñadores y administradores de bases de datos');
INSERT INTO cno (id, code, title) VALUES(2722, '2722', 'Administradores de sistemas y redes');
INSERT INTO cno (id, code, title) VALUES(2723, '2723', 'Analistas de redes informáticas');
INSERT INTO cno (id, code, title) VALUES(2729, '2729', 'Especialistas en bases de datos y en redes informáticas no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(2810, '2810', 'Economistas');
INSERT INTO cno (id, code, title) VALUES(2821, '2821', 'Sociólogos, geógrafos, antropólogos, arqueólogos y afines');
INSERT INTO cno (id, code, title) VALUES(2822, '2822', 'Filósofos, historiadores y profesionales en ciencias políticas');
INSERT INTO cno (id, code, title) VALUES(2823, '2823', 'Psicólogos');
INSERT INTO cno (id, code, title) VALUES(2824, '2824', 'Profesionales del trabajo y la educación social');
INSERT INTO cno (id, code, title) VALUES(2825, '2825', 'Agentes de igualdad de oportunidades entre mujeres y hombres');
INSERT INTO cno (id, code, title) VALUES(2830, '2830', 'Sacerdotes de las distintas religiones');
INSERT INTO cno (id, code, title) VALUES(2911, '2911', 'Archivistas y conservadores de museos');
INSERT INTO cno (id, code, title) VALUES(2912, '2912', 'Bibliotecarios, documentalistas y afines');
INSERT INTO cno (id, code, title) VALUES(2921, '2921', 'Escritores');
INSERT INTO cno (id, code, title) VALUES(2922, '2922', 'Periodistas');
INSERT INTO cno (id, code, title) VALUES(2923, '2923', 'Filólogos, intérpretes y traductores');
INSERT INTO cno (id, code, title) VALUES(2931, '2931', 'Artistas de artes plásticas y visuales');
INSERT INTO cno (id, code, title) VALUES(2932, '2932', 'Compositores, músicos y cantantes');
INSERT INTO cno (id, code, title) VALUES(2933, '2933', 'Coreógrafos y bailarines');
INSERT INTO cno (id, code, title) VALUES(2934, '2934', 'Directores de cine, de teatro y afines');
INSERT INTO cno (id, code, title) VALUES(2935, '2935', 'Actores');
INSERT INTO cno (id, code, title) VALUES(2936, '2936', 'Locutores de radio, televisión y otros presentadores');
INSERT INTO cno (id, code, title) VALUES(2937, '2937', 'Profesionales de espectáculos taurinos');
INSERT INTO cno (id, code, title) VALUES(2939, '2939', 'Artistas creativos e interpretativos no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(3110, '3110', 'Delineantes y dibujantes técnicos');
INSERT INTO cno (id, code, title) VALUES(3121, '3121', 'Técnicos en ciencias físicas y químicas');
INSERT INTO cno (id, code, title) VALUES(3122, '3122', 'Técnicos en construcción');
INSERT INTO cno (id, code, title) VALUES(3123, '3123', 'Técnicos en electricidad');
INSERT INTO cno (id, code, title) VALUES(3124, '3124', 'Técnicos en electrónica (excepto electro medicina)');
INSERT INTO cno (id, code, title) VALUES(3125, '3125', 'Técnicos en electrónica, especialidad en electro medicina');
INSERT INTO cno (id, code, title) VALUES(3126, '3126', 'Técnicos en mecánica');
INSERT INTO cno (id, code, title) VALUES(3127, '3127', 'Técnicos y analistas de laboratorio en química industrial');
INSERT INTO cno (id, code, title) VALUES(3128, '3128', 'Técnicos en metalurgia y minas');
INSERT INTO cno (id, code, title) VALUES(3129, '3129', 'Otros técnicos de las ciencias físicas, químicas, medioambientales y de las ingenierías');
INSERT INTO cno (id, code, title) VALUES(3131, '3131', 'Técnicos en instalaciones de producción de energía');
INSERT INTO cno (id, code, title) VALUES(3132, '3132', 'Técnicos en instalaciones de tratamiento de residuos, de aguas y otros operadores en plantas similares');
INSERT INTO cno (id, code, title) VALUES(3133, '3133', 'Técnicos en control de instalaciones de procesamiento de productos químicos');
INSERT INTO cno (id, code, title) VALUES(3134, '3134', 'Técnicos de refinerías de petróleo y gas natural');
INSERT INTO cno (id, code, title) VALUES(3135, '3135', 'Técnicos en control de procesos de producción de metales');
INSERT INTO cno (id, code, title) VALUES(3139, '3139', 'Técnicos en control de procesos no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(3141, '3141', 'Técnicos en ciencias biológicas (excepto en áreas sanitarias)');
INSERT INTO cno (id, code, title) VALUES(3142, '3142', 'Técnicos agropecuarios');
INSERT INTO cno (id, code, title) VALUES(3143, '3143', 'Técnicos forestales y del medio natural');
INSERT INTO cno (id, code, title) VALUES(3151, '3151', 'Jefes y oficiales de máquinas');
INSERT INTO cno (id, code, title) VALUES(3152, '3152', 'Capitanes y oficiales de puente');
INSERT INTO cno (id, code, title) VALUES(3153, '3153', 'Pilotos de aviación y profesionales afines');
INSERT INTO cno (id, code, title) VALUES(3154, '3154', 'Controladores de tráfico aéreo');
INSERT INTO cno (id, code, title) VALUES(3155, '3155', 'Técnicos en seguridad aeronáutica');
INSERT INTO cno (id, code, title) VALUES(3160, '3160', 'Técnicos de control de calidad de las ciencias físicas, químicas y de las ingenierías');
INSERT INTO cno (id, code, title) VALUES(3201, '3201', 'Supervisores en ingeniería de minas');
INSERT INTO cno (id, code, title) VALUES(3202, '3202', 'Supervisores de la construcción');
INSERT INTO cno (id, code, title) VALUES(3203, '3203', 'Supervisores de industrias alimenticias y del tabaco');
INSERT INTO cno (id, code, title) VALUES(3204, '3204', 'Supervisores de industrias química y farmacéutica');
INSERT INTO cno (id, code, title) VALUES(3205, '3205', 'Supervisores de industrias de transformación de plásticos, caucho y resinas naturales');
INSERT INTO cno (id, code, title) VALUES(3206, '3206', 'Supervisores de industrias de la madera y pastero papeleras');
INSERT INTO cno (id, code, title) VALUES(3207, '3207', 'Supervisores de la producción en industrias de artes gráficas y en la fabricación de productos de papel');
INSERT INTO cno (id, code, title) VALUES(3209, '3209', 'Supervisores de otras industrias manufactureras');
INSERT INTO cno (id, code, title) VALUES(3311, '3311', 'Técnicos en radioterapia');
INSERT INTO cno (id, code, title) VALUES(3312, '3312', 'Técnicos en imagen para el diagnóstico');
INSERT INTO cno (id, code, title) VALUES(3313, '3313', 'Técnicos en anatomía patológica y citología');
INSERT INTO cno (id, code, title) VALUES(3314, '3314', 'Técnicos en laboratorio de diagnóstico clínico');
INSERT INTO cno (id, code, title) VALUES(3315, '3315', 'Técnicos en ortoprótesis');
INSERT INTO cno (id, code, title) VALUES(3316, '3316', 'Técnicos en prótesis dentales');
INSERT INTO cno (id, code, title) VALUES(3317, '3317', 'Técnicos en audioprótesis');
INSERT INTO cno (id, code, title) VALUES(3321, '3321', 'Técnicos superiores en higiene bucodental');
INSERT INTO cno (id, code, title) VALUES(3322, '3322', 'Técnicos superiores en documentación sanitaria');
INSERT INTO cno (id, code, title) VALUES(3323, '3323', 'Técnicos superiores en dietética');
INSERT INTO cno (id, code, title) VALUES(3324, '3324', 'Técnicos en optometría');
INSERT INTO cno (id, code, title) VALUES(3325, '3325', 'Ayudantes fisioterapeutas');
INSERT INTO cno (id, code, title) VALUES(3326, '3326', 'Técnicos en prevención de riesgos laborales y salud ambiental');
INSERT INTO cno (id, code, title) VALUES(3327, '3327', 'Ayudantes de veterinaria');
INSERT INTO cno (id, code, title) VALUES(3329, '3329', 'Técnicos de la sanidad no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(3331, '3331', 'Profesionales de la acupuntura, la naturopatía, la homeopatía, la medicina tradicional china y la ayurveda');
INSERT INTO cno (id, code, title) VALUES(3339, '3339', 'Otros profesionales de las terapias alternativas');
INSERT INTO cno (id, code, title) VALUES(3401, '3401', 'Profesionales de apoyo e intermediarios de cambio, bolsa y finanzas');
INSERT INTO cno (id, code, title) VALUES(3402, '3402', 'Comerciales de préstamos y créditos');
INSERT INTO cno (id, code, title) VALUES(3403, '3403', 'Tenedores de libros');
INSERT INTO cno (id, code, title) VALUES(3404, '3404', 'Profesionales de apoyo en servicios estadísticos, matemáticos y afines');
INSERT INTO cno (id, code, title) VALUES(3405, '3405', 'Tasadores');
INSERT INTO cno (id, code, title) VALUES(3510, '3510', 'Agentes y representantes comerciales');
INSERT INTO cno (id, code, title) VALUES(3521, '3521', 'Mediadores y agentes de seguros');
INSERT INTO cno (id, code, title) VALUES(3522, '3522', 'Agentes de compras');
INSERT INTO cno (id, code, title) VALUES(3523, '3523', 'Consignatarios');
INSERT INTO cno (id, code, title) VALUES(3531, '3531', 'Representantes de aduanas');
INSERT INTO cno (id, code, title) VALUES(3532, '3532', 'Organizadores de conferencias y eventos');
INSERT INTO cno (id, code, title) VALUES(3533, '3533', 'Agentes o intermediarios en la contratación de la mano de obra (excepto representantes de espectáculos)');
INSERT INTO cno (id, code, title) VALUES(3534, '3534', 'Agentes y administradores de la propiedad inmobiliaria');
INSERT INTO cno (id, code, title) VALUES(3535, '3535', 'Portavoces y agentes de relaciones públicas');
INSERT INTO cno (id, code, title) VALUES(3539, '3539', 'Representantes artísticos y deportivos y otros agentes de servicios comerciales no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(3611, '3611', 'Supervisores de secretaría');
INSERT INTO cno (id, code, title) VALUES(3612, '3612', 'Asistentes jurídico-legales');
INSERT INTO cno (id, code, title) VALUES(3613, '3613', 'Asistentes de dirección y administrativos');
INSERT INTO cno (id, code, title) VALUES(3614, '3614', 'Secretarios de centros médicos o clínicas');
INSERT INTO cno (id, code, title) VALUES(3621, '3621', 'Profesionales de apoyo de la Administración Pública de tributos');
INSERT INTO cno (id, code, title) VALUES(3622, '3622', 'Profesionales de apoyo de la Administración Pública de servicios sociales');
INSERT INTO cno (id, code, title) VALUES(3623, '3623', 'Profesionales de apoyo de la Administración Pública de servicios de expedición de licencias');
INSERT INTO cno (id, code, title) VALUES(3629, '3629', 'Otros profesionales de apoyo de la Administración Pública para tareas de inspección y control y tareas similares');
INSERT INTO cno (id, code, title) VALUES(3631, '3631', 'Técnicos de la policía nacional, autonómica y local');
INSERT INTO cno (id, code, title) VALUES(3632, '3632', 'Suboficiales de la guardia civil');
INSERT INTO cno (id, code, title) VALUES(3711, '3711', 'Profesionales de apoyo de servicios jurídicos y servicios similares');
INSERT INTO cno (id, code, title) VALUES(3712, '3712', 'Detectives privados');
INSERT INTO cno (id, code, title) VALUES(3713, '3713', 'Profesionales de apoyo al trabajo y a la educación social');
INSERT INTO cno (id, code, title) VALUES(3714, '3714', 'Promotores de igualdad de oportunidades entre mujeres y hombres');
INSERT INTO cno (id, code, title) VALUES(3715, '3715', 'Animadores comunitarios');
INSERT INTO cno (id, code, title) VALUES(3716, '3716', 'Auxiliares laicos de las religiones');
INSERT INTO cno (id, code, title) VALUES(3721, '3721', 'Atletas y deportistas');
INSERT INTO cno (id, code, title) VALUES(3722, '3722', 'Entrenadores y árbitros de actividades deportivas');
INSERT INTO cno (id, code, title) VALUES(3723, '3723', 'Instructores de actividades deportivas');
INSERT INTO cno (id, code, title) VALUES(3724, '3724', 'Monitores de actividades recreativas y de entretenimiento');
INSERT INTO cno (id, code, title) VALUES(3731, '3731', 'Fotógrafos');
INSERT INTO cno (id, code, title) VALUES(3732, '3732', 'Diseñadores y decoradores de interior');
INSERT INTO cno (id, code, title) VALUES(3733, '3733', 'Técnicos en galerías de arte, museos y bibliotecas');
INSERT INTO cno (id, code, title) VALUES(3734, '3734', 'Chefs');
INSERT INTO cno (id, code, title) VALUES(3739, '3739', 'Otros técnicos y profesionales de apoyo de actividades culturales y artísticas');
INSERT INTO cno (id, code, title) VALUES(3811, '3811', 'Técnicos en operaciones de sistemas informáticos');
INSERT INTO cno (id, code, title) VALUES(3812, '3812', 'Técnicos en asistencia al usuario de tecnologías de la información');
INSERT INTO cno (id, code, title) VALUES(3813, '3813', 'Técnicos en redes');
INSERT INTO cno (id, code, title) VALUES(3814, '3814', 'Técnicos de la Web');
INSERT INTO cno (id, code, title) VALUES(3820, '3820', 'Programadores informáticos');
INSERT INTO cno (id, code, title) VALUES(3831, '3831', 'Técnicos de grabación audiovisual');
INSERT INTO cno (id, code, title) VALUES(3832, '3832', 'Técnicos de radiodifusión');
INSERT INTO cno (id, code, title) VALUES(3833, '3833', 'Técnicos de ingeniería de las telecomunicaciones');
INSERT INTO cno (id, code, title) VALUES(4111, '4111', 'Empleados de contabilidad');
INSERT INTO cno (id, code, title) VALUES(4112, '4112', 'Empleados de control de personal y nóminas');
INSERT INTO cno (id, code, title) VALUES(4113, '4113', 'Empleados de oficina de servicios estadísticos, financieros y bancarios');
INSERT INTO cno (id, code, title) VALUES(4121, '4121', 'Empleados de control de abastecimientos e inventario');
INSERT INTO cno (id, code, title) VALUES(4122, '4122', 'Empleados de oficina de servicios de apoyo a la producción');
INSERT INTO cno (id, code, title) VALUES(4123, '4123', 'Empleados de logística y transporte de pasajeros y mercancías');
INSERT INTO cno (id, code, title) VALUES(4210, '4210', 'Empleados de bibliotecas y archivos');
INSERT INTO cno (id, code, title) VALUES(4221, '4221', 'Empleados de servicios de correos (excepto empleados de mostrador)');
INSERT INTO cno (id, code, title) VALUES(4222, '4222', 'Codificadores y correctores de imprenta');
INSERT INTO cno (id, code, title) VALUES(4223, '4223', 'Empleados de servicio de personal');
INSERT INTO cno (id, code, title) VALUES(4301, '4301', 'Grabadores de datos');
INSERT INTO cno (id, code, title) VALUES(4309, '4309', 'Empleados administrativos sin tareas de atención al público no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(4411, '4411', 'Empleados de información al usuario');
INSERT INTO cno (id, code, title) VALUES(4412, '4412', 'Recepcionistas (excepto de hoteles)');
INSERT INTO cno (id, code, title) VALUES(4421, '4421', 'Empleados de agencias de viajes');
INSERT INTO cno (id, code, title) VALUES(4422, '4422', 'Recepcionistas de hoteles');
INSERT INTO cno (id, code, title) VALUES(4423, '4423', 'Telefonistas');
INSERT INTO cno (id, code, title) VALUES(4424, '4424', 'Teleoperadores');
INSERT INTO cno (id, code, title) VALUES(4430, '4430', 'Agentes de encuestas');
INSERT INTO cno (id, code, title) VALUES(4441, '4441', 'Cajeros de bancos y afines');
INSERT INTO cno (id, code, title) VALUES(4442, '4442', 'Empleados de venta de apuestas');
INSERT INTO cno (id, code, title) VALUES(4443, '4443', 'Empleados de sala de juegos y afines');
INSERT INTO cno (id, code, title) VALUES(4444, '4444', 'Empleados de casas de empeño y de préstamos');
INSERT INTO cno (id, code, title) VALUES(4445, '4445', 'Cobradores de facturas, deudas y empleados afines');
INSERT INTO cno (id, code, title) VALUES(4446, '4446', 'Empleados de mostrador de correos');
INSERT INTO cno (id, code, title) VALUES(4500, '4500', 'Empleados administrativos con tareas de atención al público no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(5000, '5000', 'Camareros y cocineros propietarios');
INSERT INTO cno (id, code, title) VALUES(5110, '5110', 'Cocineros asalariados');
INSERT INTO cno (id, code, title) VALUES(5120, '5120', 'Camareros asalariados');
INSERT INTO cno (id, code, title) VALUES(5210, '5210', 'Jefes de sección de tiendas y almacenes');
INSERT INTO cno (id, code, title) VALUES(5220, '5220', 'Vendedores en tiendas y almacenes');
INSERT INTO cno (id, code, title) VALUES(5300, '5300', 'Comerciantes propietarios de tiendas');
INSERT INTO cno (id, code, title) VALUES(5411, '5411', 'Vendedores en quioscos');
INSERT INTO cno (id, code, title) VALUES(5412, '5412', 'Vendedores en mercados ocasionales y mercadillos');
INSERT INTO cno (id, code, title) VALUES(5420, '5420', 'Operadores de telemarketing');
INSERT INTO cno (id, code, title) VALUES(5430, '5430', 'Expendedores de gasolineras');
INSERT INTO cno (id, code, title) VALUES(5491, '5491', 'Vendedores a domicilio');
INSERT INTO cno (id, code, title) VALUES(5492, '5492', 'Promotores de venta');
INSERT INTO cno (id, code, title) VALUES(5493, '5493', 'Modelos de moda, arte y publicidad');
INSERT INTO cno (id, code, title) VALUES(5499, '5499', 'Vendedores no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(5500, '5500', 'Cajeros y taquilleros (excepto bancos)');
INSERT INTO cno (id, code, title) VALUES(5611, '5611', 'Auxiliares de enfermería hospitalaria');
INSERT INTO cno (id, code, title) VALUES(5612, '5612', 'Auxiliares de enfermería de atención primaria');
INSERT INTO cno (id, code, title) VALUES(5621, '5621', 'Técnicos auxiliares de farmacia');
INSERT INTO cno (id, code, title) VALUES(5622, '5622', 'Técnicos de emergencias sanitarias');
INSERT INTO cno (id, code, title) VALUES(5629, '5629', 'Trabajadores de los cuidados a las personas en servicios de salud no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(5710, '5710', 'Trabajadores de los cuidados personales a domicilio');
INSERT INTO cno (id, code, title) VALUES(5721, '5721', 'Cuidadores de niños en guarderías y centros educativos');
INSERT INTO cno (id, code, title) VALUES(5722, '5722', 'Cuidadores de niños en domicilios');
INSERT INTO cno (id, code, title) VALUES(5811, '5811', 'Peluqueros');
INSERT INTO cno (id, code, title) VALUES(5812, '5812', 'Especialistas en tratamientos de estética, bienestar y afines');
INSERT INTO cno (id, code, title) VALUES(5821, '5821', 'Auxiliares de vuelo y camareros de avión, barco y tren');
INSERT INTO cno (id, code, title) VALUES(5822, '5822', 'Revisores y cobradores de transporte terrestre');
INSERT INTO cno (id, code, title) VALUES(5823, '5823', 'Acompañantes turísticos');
INSERT INTO cno (id, code, title) VALUES(5824, '5824', 'Azafatos de tierra');
INSERT INTO cno (id, code, title) VALUES(5825, '5825', 'Guías de turismo');
INSERT INTO cno (id, code, title) VALUES(5831, '5831', 'Supervisores de mantenimiento y limpieza en oficinas, hoteles y otros establecimientos');
INSERT INTO cno (id, code, title) VALUES(5832, '5832', 'Mayordomos del servicio doméstico');
INSERT INTO cno (id, code, title) VALUES(5833, '5833', 'Conserjes de edificios');
INSERT INTO cno (id, code, title) VALUES(5840, '5840', 'Trabajadores propietarios de pequeños alojamientos');
INSERT INTO cno (id, code, title) VALUES(5891, '5891', 'Asistentes personales o personas de compañía');
INSERT INTO cno (id, code, title) VALUES(5892, '5892', 'Empleados de pompas fúnebres y embalsamadores');
INSERT INTO cno (id, code, title) VALUES(5893, '5893', 'Cuidadores de animales y adiestradores');
INSERT INTO cno (id, code, title) VALUES(5894, '5894', 'Instructores de autoescuela');
INSERT INTO cno (id, code, title) VALUES(5895, '5895', 'Astrólogos, adivinadores y afines');
INSERT INTO cno (id, code, title) VALUES(5899, '5899', 'Trabajadores de servicios personales no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(5910, '5910', 'Guardias civiles');
INSERT INTO cno (id, code, title) VALUES(5921, '5921', 'Policías nacionales');
INSERT INTO cno (id, code, title) VALUES(5922, '5922', 'Policías autonómicos');
INSERT INTO cno (id, code, title) VALUES(5923, '5923', 'Policías locales');
INSERT INTO cno (id, code, title) VALUES(5931, '5931', 'Bomberos (excepto forestales)');
INSERT INTO cno (id, code, title) VALUES(5932, '5932', 'Bomberos forestales');
INSERT INTO cno (id, code, title) VALUES(5941, '5941', 'Vigilantes de seguridad y similares habilitados para ir armados');
INSERT INTO cno (id, code, title) VALUES(5942, '5942', 'Auxiliares de vigilante de seguridad y similares no habilitados para ir armados');
INSERT INTO cno (id, code, title) VALUES(5991, '5991', 'Vigilantes de prisiones');
INSERT INTO cno (id, code, title) VALUES(5992, '5992', 'Bañistas-socorristas');
INSERT INTO cno (id, code, title) VALUES(5993, '5993', 'Agentes forestales y medioambientales');
INSERT INTO cno (id, code, title) VALUES(5999, '5999', 'Trabajadores de los servicios de protección y seguridad no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(6110, '6110', 'Trabajadores cualificados en actividades agrícolas (excepto en huertas, invernaderos, viveros y jardines)');
INSERT INTO cno (id, code, title) VALUES(6120, '6120', 'Trabajadores cualificados en huertas, invernaderos, viveros y jardines');
INSERT INTO cno (id, code, title) VALUES(6201, '6201', 'Trabajadores cualificados en actividades ganaderas de vacuno');
INSERT INTO cno (id, code, title) VALUES(6202, '6202', 'Trabajadores cualificados en actividades ganaderas de ovino y caprino');
INSERT INTO cno (id, code, title) VALUES(6203, '6203', 'Trabajadores cualificados en actividades ganaderas de porcino');
INSERT INTO cno (id, code, title) VALUES(6204, '6204', 'Trabajadores cualificados en apicultura y sericicultura');
INSERT INTO cno (id, code, title) VALUES(6205, '6205', 'Trabajadores cualificados en la avicultura y la cunicultura');
INSERT INTO cno (id, code, title) VALUES(6209, '6209', 'Trabajadores cualificados en actividades ganaderas no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(6300, '6300', 'Trabajadores cualificados en actividades agropecuarias mixtas');
INSERT INTO cno (id, code, title) VALUES(6410, '6410', 'Trabajadores cualificados en actividades forestales y del medio natural');
INSERT INTO cno (id, code, title) VALUES(6421, '6421', 'Trabajadores cualificados en la acuicultura');
INSERT INTO cno (id, code, title) VALUES(6422, '6422', 'Pescadores de aguas costeras y aguas dulces');
INSERT INTO cno (id, code, title) VALUES(6423, '6423', 'Pescadores de altura');
INSERT INTO cno (id, code, title) VALUES(6430, '6430', 'Trabajadores cualificados en actividades cinegéticas');
INSERT INTO cno (id, code, title) VALUES(7111, '7111', 'Encofradores y operarios de puesta en obra de hormigón');
INSERT INTO cno (id, code, title) VALUES(7112, '7112', 'Montadores de prefabricados estructurales (sólo hormigón)');
INSERT INTO cno (id, code, title) VALUES(7121, '7121', 'Albañiles');
INSERT INTO cno (id, code, title) VALUES(7122, '7122', 'Canteros, tronzadores, labrantes y grabadores de piedras');
INSERT INTO cno (id, code, title) VALUES(7131, '7131', 'Carpinteros (excepto ebanistas)');
INSERT INTO cno (id, code, title) VALUES(7132, '7132', 'Instaladores de cerramientos metálicos y carpinteros metálicos (excepto montadores de estructuras metálicas)');
INSERT INTO cno (id, code, title) VALUES(7191, '7191', 'Mantenedores de edificios');
INSERT INTO cno (id, code, title) VALUES(7192, '7192', 'Instaladores de fachadas técnicas');
INSERT INTO cno (id, code, title) VALUES(7193, '7193', 'Instaladores de sistemas de impermeabilización en edificios');
INSERT INTO cno (id, code, title) VALUES(7199, '7199', 'Otros trabajadores de las obras estructurales de construcción no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(7211, '7211', 'Escayolistas');
INSERT INTO cno (id, code, title) VALUES(7212, '7212', 'Aplicadores de revestimientos de pasta y mortero');
INSERT INTO cno (id, code, title) VALUES(7221, '7221', 'Fontaneros');
INSERT INTO cno (id, code, title) VALUES(7222, '7222', 'Montadores-instaladores de gas en edificios');
INSERT INTO cno (id, code, title) VALUES(7223, '7223', 'Instaladores de conductos en obra pública');
INSERT INTO cno (id, code, title) VALUES(7231, '7231', 'Pintores y empapeladores');
INSERT INTO cno (id, code, title) VALUES(7232, '7232', 'Pintores en las industrias manufactureras');
INSERT INTO cno (id, code, title) VALUES(7240, '7240', 'Soladores, colocadores de parquet y afines');
INSERT INTO cno (id, code, title) VALUES(7250, '7250', 'Mecánicos-instaladores de refrigeración y climatización');
INSERT INTO cno (id, code, title) VALUES(7291, '7291', 'Montadores de cubiertas');
INSERT INTO cno (id, code, title) VALUES(7292, '7292', 'Instaladores de material aislante térmico y de insonorización');
INSERT INTO cno (id, code, title) VALUES(7293, '7293', 'Cristaleros');
INSERT INTO cno (id, code, title) VALUES(7294, '7294', 'Montadores-instaladores de placas de energía solar');
INSERT INTO cno (id, code, title) VALUES(7295, '7295', 'Personal de limpieza de fachadas de edificios y chimeneas');
INSERT INTO cno (id, code, title) VALUES(7311, '7311', 'Moldeadores y macheros');
INSERT INTO cno (id, code, title) VALUES(7312, '7312', 'Soldadores y oxicortadores');
INSERT INTO cno (id, code, title) VALUES(7313, '7313', 'Chapistas y caldereros');
INSERT INTO cno (id, code, title) VALUES(7314, '7314', 'Montadores de estructuras metálicas');
INSERT INTO cno (id, code, title) VALUES(7315, '7315', 'Montadores de estructuras cableadas y empalmadores de cables');
INSERT INTO cno (id, code, title) VALUES(7321, '7321', 'Herreros y forjadores');
INSERT INTO cno (id, code, title) VALUES(7322, '7322', 'Trabajadores de la fabricación de herramientas, mecánico-ajustadores, modelistas, matriceros y afines');
INSERT INTO cno (id, code, title) VALUES(7323, '7323', 'Ajustadores y operadores de máquinas-herramienta');
INSERT INTO cno (id, code, title) VALUES(7324, '7324', 'Pulidores de metales y afiladores de herramientas');
INSERT INTO cno (id, code, title) VALUES(7401, '7401', 'Mecánicos y ajustadores de vehículos de motor');
INSERT INTO cno (id, code, title) VALUES(7402, '7402', 'Mecánicos y ajustadores de motores de avión');
INSERT INTO cno (id, code, title) VALUES(7403, '7403', 'Mecánicos y ajustadores de maquinaria agrícola e industrial');
INSERT INTO cno (id, code, title) VALUES(7404, '7404', 'Mecánicos y ajustadores de maquinaria naval y ferroviaria');
INSERT INTO cno (id, code, title) VALUES(7405, '7405', 'Reparadores de bicicletas y afines');
INSERT INTO cno (id, code, title) VALUES(7510, '7510', 'Electricistas de la construcción y afines');
INSERT INTO cno (id, code, title) VALUES(7521, '7521', 'Mecánicos y reparadores de equipos eléctricos');
INSERT INTO cno (id, code, title) VALUES(7522, '7522', 'Instaladores y reparadores de líneas eléctricas');
INSERT INTO cno (id, code, title) VALUES(7531, '7531', 'Mecánicos y reparadores de equipos electrónicos');
INSERT INTO cno (id, code, title) VALUES(7532, '7532', 'Instaladores y reparadores en electro medicina');
INSERT INTO cno (id, code, title) VALUES(7533, '7533', 'Instaladores y reparadores en tecnologías de la información y las comunicaciones');
INSERT INTO cno (id, code, title) VALUES(7611, '7611', 'Relojeros y mecánicos de instrumentos de precisión');
INSERT INTO cno (id, code, title) VALUES(7612, '7612', 'Lutieres y similares; afinadores de instrumentos musicales');
INSERT INTO cno (id, code, title) VALUES(7613, '7613', 'Joyeros, orfebres y plateros');
INSERT INTO cno (id, code, title) VALUES(7614, '7614', 'Trabajadores de la cerámica, alfareros y afines');
INSERT INTO cno (id, code, title) VALUES(7615, '7615', 'Sopladores, modeladores, laminadores, cortadores y pulidores de vidrio');
INSERT INTO cno (id, code, title) VALUES(7616, '7616', 'Rotulistas, grabadores de vidrio, pintores decorativos de artículos diversos');
INSERT INTO cno (id, code, title) VALUES(7617, '7617', 'Artesanos en madera y materiales similares; cesteros, bruceros y trabajadores afines');
INSERT INTO cno (id, code, title) VALUES(7618, '7618', 'Artesanos en tejidos, cueros y materiales similares, preparadores de fibra y tejedores con telares artesanos o de tejidos de punto y afines');
INSERT INTO cno (id, code, title) VALUES(7619, '7619', 'Artesanos no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(7621, '7621', 'Trabajadores de procesos de preimpresión');
INSERT INTO cno (id, code, title) VALUES(7622, '7622', 'Trabajadores de procesos de impresión');
INSERT INTO cno (id, code, title) VALUES(7623, '7623', 'Trabajadores de procesos de encuadernación');
INSERT INTO cno (id, code, title) VALUES(7701, '7701', 'Matarifes y trabajadores de las industrias cárnicas');
INSERT INTO cno (id, code, title) VALUES(7702, '7702', 'Trabajadores de las industrias del pescado');
INSERT INTO cno (id, code, title) VALUES(7703, '7703', 'Panaderos, pasteleros y confiteros');
INSERT INTO cno (id, code, title) VALUES(7704, '7704', 'Trabajadores del tratamiento de la leche y elaboración de productos lácteos (incluidos helados)');
INSERT INTO cno (id, code, title) VALUES(7705, '7705', 'Trabajadores conserveros de frutas y hortalizas y trabajadores de la elaboración de bebidas no alcohólicas');
INSERT INTO cno (id, code, title) VALUES(7706, '7706', 'Trabajadores de la elaboración de bebidas alcohólicas distintas del vino');
INSERT INTO cno (id, code, title) VALUES(7707, '7707', 'Trabajadores de la elaboración del vino');
INSERT INTO cno (id, code, title) VALUES(7708, '7708', 'Preparadores y elaboradores del tabaco y sus productos');
INSERT INTO cno (id, code, title) VALUES(7709, '7709', 'Catadores y clasificadores de alimentos y bebidas');
INSERT INTO cno (id, code, title) VALUES(7811, '7811', 'Trabajadores del tratamiento de la madera');
INSERT INTO cno (id, code, title) VALUES(7812, '7812', 'Ajustadores y operadores de máquinas para trabajar la madera');
INSERT INTO cno (id, code, title) VALUES(7820, '7820', 'Ebanistas y trabajadores afines');
INSERT INTO cno (id, code, title) VALUES(7831, '7831', 'Sastres, modistos, peleteros y sombrereros');
INSERT INTO cno (id, code, title) VALUES(7832, '7832', 'Patronistas para productos en textil y piel');
INSERT INTO cno (id, code, title) VALUES(7833, '7833', 'Cortadores de tejidos, cuero, piel y otros materiales');
INSERT INTO cno (id, code, title) VALUES(7834, '7834', 'Costureros a mano, bordadores y afines');
INSERT INTO cno (id, code, title) VALUES(7835, '7835', 'Tapiceros, colchoneros y afines');
INSERT INTO cno (id, code, title) VALUES(7836, '7836', 'Curtidores y preparadores de pieles');
INSERT INTO cno (id, code, title) VALUES(7837, '7837', 'Zapateros y afines');
INSERT INTO cno (id, code, title) VALUES(7891, '7891', 'Buceadores');
INSERT INTO cno (id, code, title) VALUES(7892, '7892', 'Pegadores');
INSERT INTO cno (id, code, title) VALUES(7893, '7893', 'Clasificadores y probadores de productos (excepto alimentos, bebidas y tabaco)');
INSERT INTO cno (id, code, title) VALUES(7894, '7894', 'Fumigadores y otros controladores de plagas y malas hierbas');
INSERT INTO cno (id, code, title) VALUES(7899, '7899', 'Oficiales, operarios y artesanos de otros oficios no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(8111, '8111', 'Mineros y otros operadores en instalaciones mineras');
INSERT INTO cno (id, code, title) VALUES(8112, '8112', 'Operadores en instalaciones para la preparación de minerales y rocas');
INSERT INTO cno (id, code, title) VALUES(8113, '8113', 'Sondistas y trabajadores afines');
INSERT INTO cno (id, code, title) VALUES(8114, '8114', 'Operadores de maquinaria para fabricar productos derivados de minerales no metálicos');
INSERT INTO cno (id, code, title) VALUES(8121, '8121', 'Operadores en instalaciones para la obtención y transformación de metales');
INSERT INTO cno (id, code, title) VALUES(8122, '8122', 'Operadores de máquinas pulidoras, galvanizadoras y recubridoras de metales');
INSERT INTO cno (id, code, title) VALUES(8131, '8131', 'Operadores en plantas industriales químicas');
INSERT INTO cno (id, code, title) VALUES(8132, '8132', 'Operadores de máquinas para fabricar productos farmacéuticos, cosméticos y afines');
INSERT INTO cno (id, code, title) VALUES(8133, '8133', 'Operadores de laboratorios fotográficos y afines');
INSERT INTO cno (id, code, title) VALUES(8141, '8141', 'Operadores de máquinas para fabricar productos de caucho y derivados de resinas naturales');
INSERT INTO cno (id, code, title) VALUES(8142, '8142', 'Operadores de máquinas para fabricar productos de material plástico');
INSERT INTO cno (id, code, title) VALUES(8143, '8143', 'Operadores de máquinas para fabricar productos de papel y cartón');
INSERT INTO cno (id, code, title) VALUES(8144, '8144', 'Operadores de serrerías, de máquinas de fabricación de tableros y de instalaciones afines para el tratamiento de la madera y el corcho');
INSERT INTO cno (id, code, title) VALUES(8145, '8145', 'Operadores en instalaciones para la preparación de pasta de papel y fabricación de papel');
INSERT INTO cno (id, code, title) VALUES(8151, '8151', 'Operadores de máquinas para preparar fibras, hilar y devanar');
INSERT INTO cno (id, code, title) VALUES(8152, '8152', 'Operadores de telares y otras máquinas tejedoras');
INSERT INTO cno (id, code, title) VALUES(8153, '8153', 'Operadores de máquinas de coser y bordar');
INSERT INTO cno (id, code, title) VALUES(8154, '8154', 'Operadores de máquinas de blanquear, teñir, estampar y acabar textiles');
INSERT INTO cno (id, code, title) VALUES(8155, '8155', 'Operadores de máquinas para tratar pieles y cuero');
INSERT INTO cno (id, code, title) VALUES(8156, '8156', 'Operadores de máquinas para la fabricación del calzado, marroquinería y guantería de piel');
INSERT INTO cno (id, code, title) VALUES(8159, '8159', 'Operadores de máquinas para fabricar productos textiles no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(8160, '8160', 'Operadores de máquinas para elaborar productos alimenticios, bebidas y tabaco');
INSERT INTO cno (id, code, title) VALUES(8170, '8170', 'Operadores de máquinas de lavandería y tintorería');
INSERT INTO cno (id, code, title) VALUES(8191, '8191', 'Operadores de hornos e instalaciones de vidriería y cerámica');
INSERT INTO cno (id, code, title) VALUES(8192, '8192', 'Operadores de calderas y máquinas de vapor');
INSERT INTO cno (id, code, title) VALUES(8193, '8193', 'Operadores de máquinas de embalaje, embotellamiento y etiquetado');
INSERT INTO cno (id, code, title) VALUES(8199, '8199', 'Operadores de instalaciones y maquinaria fijas no clasificados bajo otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(8201, '8201', 'Ensambladores de maquinaria mecánica');
INSERT INTO cno (id, code, title) VALUES(8202, '8202', 'Ensambladores de equipos eléctricos y electrónicos');
INSERT INTO cno (id, code, title) VALUES(8209, '8209', 'Montadores y ensambladores no clasificados en otros epígrafes');
INSERT INTO cno (id, code, title) VALUES(8311, '8311', 'Maquinistas de locomotoras');
INSERT INTO cno (id, code, title) VALUES(8312, '8312', 'Agentes de maniobras ferroviarias');
INSERT INTO cno (id, code, title) VALUES(8321, '8321', 'Operadores de maquinaria agrícola móvil');
INSERT INTO cno (id, code, title) VALUES(8322, '8322', 'Operadores de maquinaria forestal móvil');
INSERT INTO cno (id, code, title) VALUES(8331, '8331', 'Operadores de maquinaria de movimientos de tierras y equipos similares');
INSERT INTO cno (id, code, title) VALUES(8332, '8332', 'Operadores de grúas, montacargas y de maquinaria similar de movimiento de materiales');
INSERT INTO cno (id, code, title) VALUES(8333, '8333', 'Operadores de carretillas elevadoras');
INSERT INTO cno (id, code, title) VALUES(8340, '8340', 'Marineros de puente, marineros de máquinas y afines');
INSERT INTO cno (id, code, title) VALUES(8411, '8411', 'Conductores propietarios de automóviles, taxis y furgonetas');
INSERT INTO cno (id, code, title) VALUES(8412, '8412', 'Conductores asalariados de automóviles, taxis y furgonetas');
INSERT INTO cno (id, code, title) VALUES(8420, '8420', 'Conductores de autobuses y tranvías');
INSERT INTO cno (id, code, title) VALUES(8431, '8431', 'Conductores propietarios de camiones');
INSERT INTO cno (id, code, title) VALUES(8432, '8432', 'Conductores asalariados de camiones');
INSERT INTO cno (id, code, title) VALUES(8440, '8440', 'Conductores de motocicletas y ciclomotores');
INSERT INTO cno (id, code, title) VALUES(9100, '9100', 'Empleados domésticos');
INSERT INTO cno (id, code, title) VALUES(9210, '9210', 'Personal de limpieza de oficinas, hoteles y otros establecimientos similares');
INSERT INTO cno (id, code, title) VALUES(9221, '9221', 'Limpiadores en seco a mano y afines');
INSERT INTO cno (id, code, title) VALUES(9222, '9222', 'Limpiadores de vehículos');
INSERT INTO cno (id, code, title) VALUES(9223, '9223', 'Limpiadores de ventanas');
INSERT INTO cno (id, code, title) VALUES(9229, '9229', 'Otro personal de limpieza');
INSERT INTO cno (id, code, title) VALUES(9310, '9310', 'Ayudantes de cocina');
INSERT INTO cno (id, code, title) VALUES(9320, '9320', 'Preparadores de comidas rápidas');
INSERT INTO cno (id, code, title) VALUES(9410, '9410', 'Vendedores callejeros');
INSERT INTO cno (id, code, title) VALUES(9420, '9420', 'Repartidores de publicidad, limpiabotas y otros trabajadores de oficios callejeros');
INSERT INTO cno (id, code, title) VALUES(9431, '9431', 'Ordenanzas');
INSERT INTO cno (id, code, title) VALUES(9432, '9432', 'Mozos de equipaje y afines');
INSERT INTO cno (id, code, title) VALUES(9433, '9433', 'Repartidores, recadistas y mensajeros a pie');
INSERT INTO cno (id, code, title) VALUES(9434, '9434', 'Lectores de contadores y recaudadores de máquinas recreativas y expendedoras');
INSERT INTO cno (id, code, title) VALUES(9441, '9441', 'Recogedores de residuos');
INSERT INTO cno (id, code, title) VALUES(9442, '9442', 'Clasificadores de desechos, operarios de punto limpio y recogedores de chatarra');
INSERT INTO cno (id, code, title) VALUES(9443, '9443', 'Barrenderos y afines');
INSERT INTO cno (id, code, title) VALUES(9490, '9490', 'Otras ocupaciones elementales');
INSERT INTO cno (id, code, title) VALUES(9511, '9511', 'Peones agrícolas (excepto en huertas, invernaderos, viveros y jardines)');
INSERT INTO cno (id, code, title) VALUES(9512, '9512', 'Peones agrícolas en huertas, invernaderos, viveros y jardines');
INSERT INTO cno (id, code, title) VALUES(9520, '9520', 'Peones ganaderos');
INSERT INTO cno (id, code, title) VALUES(9530, '9530', 'Peones agropecuarios');
INSERT INTO cno (id, code, title) VALUES(9541, '9541', 'Peones de la pesca');
INSERT INTO cno (id, code, title) VALUES(9542, '9542', 'Peones de la acuicultura');
INSERT INTO cno (id, code, title) VALUES(9543, '9543', 'Peones forestales y de la caza');
INSERT INTO cno (id, code, title) VALUES(9601, '9601', 'Peones de obras públicas');
INSERT INTO cno (id, code, title) VALUES(9602, '9602', 'Peones de la construcción de edificios');
INSERT INTO cno (id, code, title) VALUES(9603, '9603', 'Peones de la minería, canteras y otras industrias extractivas');
INSERT INTO cno (id, code, title) VALUES(9700, '9700', 'Peones de las industrias manufactureras');
INSERT INTO cno (id, code, title) VALUES(9811, '9811', 'Peones del transporte de mercancías y descargadores');
INSERT INTO cno (id, code, title) VALUES(9812, '9812', 'Conductores de vehículos de tracción animal para el transporte de personas y similares');
INSERT INTO cno (id, code, title) VALUES(9820, '9820', 'Reponedores');

CREATE TABLE `contract_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Archivo Adjunto del contrato',
  `contract` int(4) NOT NULL default '0' COMMENT 'Identificador del Registro del contrato',
  `mimeType` tinyint(2) default '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Archivo Adjunto',
  `scope` int(4) default NULL COMMENT 'Ambito del Archivo Adjunto',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Archivo Adjunto',
  `attach_date` date default NULL COMMENT 'Fecha del Archivo Adjunto',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_ATTACH_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_ATTACH_SCOPE` (`scope`),
  CONSTRAINT `FK_CONTRACT_ATTACH_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de contratos';

DROP TABLE `enterprise_certificate_detail`;

DROP TABLE `enterprise_certificate`;

CREATE TABLE `certifica2_batch` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del certificado de empresa de la remesa',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador unico del certificado de empresa de la empresa',
  `date` date NOT NULL COMMENT 'Fecha de la ultima remesa en la que fue incluido',
  `status` int(4) default NULL COMMENT 'Estado del certificado correspondiente a la ultima respuesta',
  `sign` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Estado del certificado correspondiente a la ultima respuesta',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas de certificados de empresa';

CREATE TABLE `certifica2_batch_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del certificado de empresa de la remesa',
  `certifica2_batch` int(4) NOT NULL COMMENT 'Identificador unico del certificado de empresa',
  `contract` int(4) NOT NULL COMMENT 'Identificador unico del contrato de empleado',
  `enterprise_nif` varchar(9) collate latin1_spanish_ci NOT NULL COMMENT 'NIF de la empresa',
  `ccc` varchar(9) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo cuenta cotizacion',
  `document` varchar(9) collate latin1_spanish_ci NOT NULL COMMENT 'Documento de identidad',
  `name` varchar(15) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del trabajador',
  `first_surname` varchar(20) collate latin1_spanish_ci NOT NULL COMMENT 'Primer apellido',
  `second_surname` varchar(20) collate latin1_spanish_ci default NULL COMMENT 'Segundo apellido',
  `ss_number` varchar(20) collate latin1_spanish_ci NOT NULL COMMENT 'Numero seguridad social',
  `quote_group` varchar(2) collate latin1_spanish_ci default NULL COMMENT 'Grupo de CotizaciÃ³n',
  `contract_type` varchar(3) collate latin1_spanish_ci NOT NULL COMMENT 'Tipo de contrato',
  `contract_duration` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Duracion contrato',
  `contract_duration_indicator` varchar(1) collate latin1_spanish_ci default NULL COMMENT 'Indicador duracion contrato',
  `occupation_code` varchar(7) collate latin1_spanish_ci NOT NULL COMMENT 'Codidgo de profesion',
  `public_association_charge` varchar(2) collate latin1_spanish_ci default NULL COMMENT 'Cargo publico sindical',
  `dedication_percent` varchar(4) collate latin1_spanish_ci default NULL COMMENT 'Porcentual dedicacion',
  `enterprise_start_date` date NOT NULL COMMENT 'Fecha alta empresa',
  `suspension_cause_code` varchar(2) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo causa suspension',
  `expire_date` date NOT NULL COMMENT 'Fecha suspension extincion',
  `expire_end_date` date default NULL COMMENT 'Fecha suspension extincion',
  `ere` varchar(27) collate latin1_spanish_ci default NULL COMMENT 'ERE',
  `ere_reduction_percent` varchar(4) collate latin1_spanish_ci default NULL COMMENT 'Porcentual reduccion ERE',
  `other_reduction_percent` varchar(4) collate latin1_spanish_ci default NULL COMMENT 'Porcentual reduccion otros',
  `reduction_cause_code` varchar(2) collate latin1_spanish_ci default NULL COMMENT 'Codigo causa porcentaje reduccion',
  `salary_period_start_date` date default NULL COMMENT 'Fecha desde periodo salarios',
  `salary_period_end_date` date default NULL COMMENT 'Fecha hasta periodo salarios',
  `salary_processing_days` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Dias salario tramitacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_CERTIFICA2_BATCH_DETAIL_CERTIFICA2_BATCH` (`certifica2_batch`),
  KEY `IDX_CERTIFICA2_BATCH_DETAIL_CONTRACT` (`contract`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_DETAIL_CERTIFICA2_BATCH` FOREIGN KEY (`certifica2_batch`) REFERENCES `certifica2_batch` (`id`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_DETAIL_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las remesas de certificados de empresa';

CREATE TABLE `certifica2_batch_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de los datos de cotizacion del certificado',
  `certifica2_batch_detail` int(4) NOT NULL COMMENT 'Identificador unico del certificado de empresa de la remesa',
  `year` int(4) NOT NULL COMMENT 'Anio',
  `month` int(2) NOT NULL COMMENT 'Mes',
  `contribution_days` int(2) NOT NULL COMMENT 'Numero de dias cotizados',
  `cgc_contribution_base` double(15,3) default '0.000' COMMENT 'Base de cotizacion de contingencias comunes',
  `unemployment_contribution_base` double(15,3) NOT NULL default '0.000' COMMENT 'Base de cotizacion por desempleo',
  `comments` varchar(50) collate latin1_spanish_ci default NULL COMMENT 'Observaciones',
  PRIMARY KEY  (`id`),
  KEY `IDX_CERTIFICA2_BATCH_DATA_CERTIFICA2_BATCH_DETAIL` (`certifica2_batch_detail`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_DATA_CERTIFICA2_BATCH_DETAIL` FOREIGN KEY (`certifica2_batch_detail`) REFERENCES `certifica2_batch_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos cotizacion de empleados de certificados de empresa';


UPDATE `db_version` SET `version_number` = '6.3.1';

COMMIT;
