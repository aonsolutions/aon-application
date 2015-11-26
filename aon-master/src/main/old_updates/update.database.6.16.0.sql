# Database: aon_master
# Version: Actualizacion de la version 6.16.0 a la version 6.17.0.
# Created by: rtrepiana
# Creation Date: 08/09/2011 11:00
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `irpf_data`
	ADD `issue_date` date NOT NULL COMMENT 'Fecha de emisión' ;
ALTER TABLE `irpf_data`
	ADD `annual_remuneration` double(15,3) default NULL COMMENT 'Retribuciones totales (dinerarias y en especie). Importe íntegro';
ALTER TABLE `irpf_data`
	ADD `irregular_18_2_reduction` double(15,3) default NULL COMMENT 'Reducciones por irregularidad ( Atr. 18.2 LIRPF)';
ALTER TABLE `irpf_data`
	ADD `irregular_18_3_reduction` double(15,3) default NULL COMMENT 'Reducciones por irregularidad ( Atr. 18.3: Disposiciones transitorias 11ª y 12 ª de la LIRPF)';
ALTER TABLE `irpf_data`
	ADD `deduccibles_expenses` double(15,3) default NULL COMMENT 'Gastos deducibles ( Atr 19.2, letras a, b y c de la LINRPF: Seguridad Social, Mutualidades ...)';
ALTER TABLE `irpf_data`
	ADD `spousal_support` double(15,3) default NULL COMMENT 'Pension compensatoria a favor del cónyuge. Importe fijado judicialmente';
ALTER TABLE `irpf_data`
	ADD `food_annuity` double(15,3) default NULL COMMENT 'Anualidades por alimentos en favor de los hijos. Importe fijado judicialmente';
ALTER TABLE `irpf_data`
	ADD `deduct_home_loan` tinyint(2) default NULL  COMMENT 'Comunicación de pagos por la adquisión o rehabilitación de la vivienda habitual utilizando financiación ajena';

CREATE TABLE `irpf_regularization` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `contract` int(4) NOT NULL COMMENT 'Identificador del contrato',
  `reason` tinyint(2) default NULL  COMMENT 'Causa de regularización',
  `effective_date` date NOT NULL COMMENT 'Fecha de entrada en vigor',
  `paid_irpf` double(15,3) default NULL COMMENT 'Retenciones practicadas con anterioridad a la regularización.',
  `paid_remuneration` double(15,3) default NULL COMMENT 'Retribuciones ya satisfechas con anterioridad a la regularización.',
  `prior_annual_irpf` double(15,3) default NULL COMMENT 'Retenciones anuales anteriores a la regularización.',
  `prior_annual_remuneration` double(15,3) default NULL COMMENT 'Retribucines anulaes consideradas con anterioridad a la regularización.',
  `prior_base_irpf` double(15,3) default NULL COMMENT 'Base para calcular el tipo de retención determinado antes de la regularización.',
  `prior_irpf` double(15,2) default NULL COMMENT 'Tipo de retención aplicado antes de la regularización.',
  `prior_in_ceuta_melilla` tinyint(1) default NULL COMMENT 'Los rendimientos anteriores a la regularización fueron obtenidos en Ceuta o Melilla',
  `prior_minimun_personal_family` double(15,3) default NULL COMMENT 'Mínimo personal y familiar para calcular el tipo de retención determinado antes de la regularización.',
  `prior_deduct_home_loan` tinyint(2) default NULL COMMENT 'En algún momento antes de la regularización se aplico la minoración por pagos por la adquisión o rehabilitación de la vivienda',
  `prior_deduct_home_loan_amount` double(15,3) default NULL COMMENT 'Importe de la minoración por pagos por la adquisión o rehabilitación de la vivienda antes de la regularización',
  PRIMARY KEY  (`id`),
  KEY `IDX_IRPF_REGULARIZATION_CONTRACT` (`contract`),
  CONSTRAINT `FK_IRPF_REGULARIZATION_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos regularizacion IRPF' ;

CREATE TABLE `irpf_result` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `contract` int(4) NOT NULL COMMENT 'Identificador del contrato',
  `effective_date` date NOT NULL COMMENT 'Fecha de entrada en vigor',
  
  `base_irpf` double(15,3) default NULL COMMENT 'Base para calcular el tipo de retención',
  `minimun_personal_family` double(15,3) default NULL COMMENT 'Mínimo personal y familiar para calcular el tipo de retención',
  `deduct_home_loan_amount` double(15,3) default NULL COMMENT 'Minoración por pagos de préstamo para vivienda habitual',
  `deduct_80_bis` double(15,3) default NULL COMMENT 'Deduccion Arttículo 80 bis LIRPF',
  `irpf` double(15,2) default NULL COMMENT 'Tipo retención apliclabe ',
  `annual_irpf` double(15,3) default NULL COMMENT 'Importe anual de las retenciones e ingresos a cuenta',

  `annual_remuneration` double(15,3) default NULL COMMENT 'Retribuciones anuales. Importe íntegro',
  `irregular_18_2_reduction` double(15,3) default NULL COMMENT 'Reducciones por irregularidad ( Art. 18.2 LIRPF). Importe',
  `irregular_18_3_reduction` double(15,3) default NULL COMMENT 'Reducciones por irregularidad ( Art. 18.3: DD.TT 11ª y 12 ª de la LIRPF). Importe',
  `deduccibles_expenses` double(15,3) default NULL COMMENT 'Gastos deducibles. Importe anual',
  `work_remuneration_reduction` double(15,3) default NULL COMMENT 'Reducciones por rendimiento del trabajo ',
  `work_prolongation_reduction` double(15,3) default NULL COMMENT 'Reducciones por prolongación de la actividad ',
  `work_moving_reduction` double(15,3) default NULL COMMENT 'Reducciones por movilidad geografica ',
  `work_disability_reduction` double(15,3) default NULL COMMENT 'Reducciones por discapacidad ',
  
  `social_security_pensioner` double(15,3) default NULL COMMENT 'Por ser pensionista de la s. social/cl. Pasivas o desempleado',
  `two_or_more_descendents_min` double(15,3) default NULL COMMENT 'Por tener más de dos descendientes con derecho a mínimo',
  `spousal_support` double(15,3) default NULL COMMENT 'Pension compensatoria a favor del cónyuge. Importe anual',
  `food_annuity` double(15,3) default NULL COMMENT 'Anualidades por alimentos en favor de los hijos. Importe anual',
  
  `minimun_personal` double(15,3) default NULL COMMENT 'Mínimo personal',
  `minimun_ascendents` double(15,3) default NULL COMMENT 'Mínimo por descendientes',
  `minimun_descendents` double(15,3) default NULL COMMENT 'Mínimo por descendientes',
  `minimun_disability` double(15,3) default NULL COMMENT 'Mínimo por discapacidad',
  
  `descendents_minor_3_total` tinyint(2) default NULL COMMENT 'Descendientes computados menores de tres años. Total',
  `descendents_minor_3_entirely` tinyint(2) default NULL COMMENT 'Descendientes computados menores de tres años. Por entero',
  `descendents_remainder_total` tinyint(2) default NULL COMMENT 'Resto de descendientes computados . Total',
  `descendents_remainder_entirely` tinyint(2) default NULL COMMENT 'Resto de descendientes computados . Por entero',
  `descendents_33_65_total` tinyint(2) default NULL COMMENT 'Descendientes con discapacidad >= 33% y < 65%. Total',
  `descendents_33_65_entirely` tinyint(2) default NULL COMMENT 'Descendientes con discapacidad >= 33% y < 65%. Por entero',
  `descendents_moving_total` tinyint(2) default NULL COMMENT 'Descendientes con discapacidad, movilidad reducida. Total',
  `descendents_moving_entirely` tinyint(2) default NULL COMMENT 'Descendientes con discapacidad, movilidad reducida. Por entero',
  `descendents_65_total` tinyint(2) default NULL COMMENT 'Descendientes con discapacidad > 65%. Total',
  `descendents_65_entirely` tinyint(2) default NULL COMMENT 'Descendientes con discapacidad > 65%. Por entero',
  `descendents_first` tinyint(2) default NULL COMMENT 'Detalle del cómputo de descendientes. Hijo 1º',
  `descendents_second` tinyint(2) default NULL COMMENT 'Detalle del cómputo de descendientes. Hijo 2º',
  `descendents_third` tinyint(2) default NULL COMMENT 'Detalle del cómputo de descendientes. Hijo 3º',
  `descendents_fourth_subsequent_total` tinyint(2) default NULL COMMENT 'Detalle del cómputo de descendientes. Hijo 4º y sucesivos. Total',
  `descendents_fourth_subsequent_entirely` tinyint(2) default NULL COMMENT 'Detalle del cómputo de descendientes. Hijo 4º y sucesivos. Por entero',
  

  `ascendents_minor_75_total` tinyint(2) default NULL COMMENT 'Ascendientes computados menores de 75 años. Total',
  `ascendents_minor_75_entirely` tinyint(2) default NULL COMMENT 'Ascendientes computados menores de 75 años. Por entero',
  `ascendents_mayor_75_total` tinyint(2) default NULL COMMENT 'Ascendientes computados mayores de 75 años. Total',
  `ascendents_mayor_75_entirely` tinyint(2) default NULL COMMENT 'Ascendientes computados mayores de 75 años. Por entero',
  `ascendents_33_65_total` tinyint(2) default NULL COMMENT 'Ascendientes con discapacidad >= 33% y < 65%. Total',
  `ascendents_33_65_entirely` tinyint(2) default NULL COMMENT 'Ascendientes con discapacidad >= 33% y < 65%. Por entero',
  `ascendents_moving_total` tinyint(2) default NULL COMMENT 'Ascendientes con discapacidad, movilidad reducida. Total',
  `ascendents_moving_entirely` tinyint(2) default NULL COMMENT 'Ascendientes con discapacidad, movilidad reducida. Por entero',
  `ascendents_65_total` tinyint(2) default NULL COMMENT 'Ascendientes con discapacidad > 65%. Total',
  `ascendents_65_entirely` tinyint(2) default NULL COMMENT 'Ascendientes con discapacidad > 65%. Por entero',
  
  PRIMARY KEY  (`id`),
  KEY `IDX_IRPF_RESULT_CONTRACT` (`contract`),
  CONSTRAINT `FK_IRPF_RESULT_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Resultados IRPF' ;
	
ALTER TABLE irpf_data_ascendants
	DROP COLUMN `another_descendient`;

ALTER TABLE irpf_data_ascendants
	ADD `another_descendient` tinyint(2) default '0' COMMENT 'Convivencia con otros descendientes';

CREATE TABLE `agreement_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre',
  `agreement` int(4) NOT NULL COMMENT 'Convenio',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_AGREEMENT_DATA_AGREEMENT` (`agreement`),
  CONSTRAINT `FK_AGREEMENT_DATA_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto del convenio' ;

	
UPDATE `db_version` SET `version_number` = '6.17.0';

COMMIT;
