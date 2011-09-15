# Database: aon_master
# Version: Actualizacion de la version 6.16.0 a la version 6.16.1.
# Created by: eagirrezabal
# Creation Date: 15/09/2011 17:44
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto del convenio';

ALTER TABLE `irpf_data` 
ADD `issue_date` date NOT NULL COMMENT 'Fecha de emisin';

ALTER TABLE `irpf_data` 
ADD `annual_remuneration` double(15,3) default NULL COMMENT 'Retribuciones totales (dinerarias y en especie). Importe integro';

ALTER TABLE `irpf_data` 
ADD `irregular_18_2_reduction` double(15,3) default NULL COMMENT 'Reducciones por irregularidad ( Atr. 18.2 LIRPF)';

ALTER TABLE `irpf_data` 
ADD `irregular_18_3_reduction` double(15,3) default NULL COMMENT 'Reducciones por irregularidad ( Atr. 18.3: Disposiciones transitorias 11 y 12 de la LIRPF)';

ALTER TABLE `irpf_data` 
ADD `deduccibles_expenses` double(15,3) default NULL COMMENT 'Gastos deducibles ( Atr 19.2, letras a, b y c de la LINRPF: Seguridad Social, Mutualidades ...)';

ALTER TABLE `irpf_data` 
ADD `spousal_support` double(15,3) default NULL COMMENT 'Pension compensatoria a favor del cnyuge. Importe fijado judicialmente';

ALTER TABLE `irpf_data` 
ADD `food_annuity` double(15,3) default NULL COMMENT 'Anualidades por alimentos en favor de los hijos. Importe fijado judicialmente';

ALTER TABLE `irpf_data` 
ADD `deduct_home_loan` tinyint(2) default NULL COMMENT 'Comunicacin de pagos por la adquisin o rehabilitacin de la vivienda habitual utilizando financiacin ajena';

CREATE TABLE `irpf_regularization` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `contract` int(4) NOT NULL COMMENT 'Identificador del contrato',
  `reason` tinyint(2) default NULL COMMENT 'Causa de regularizacin',
  `effective_date` date NOT NULL COMMENT 'Fecha de entrada en vigor',
  `paid_irpf` double(15,3) default NULL COMMENT 'Retenciones practicadas con anterioridad a la regularizacin.',
  `paid_remuneration` double(15,3) default NULL COMMENT 'Retribuciones ya satisfechas con anterioridad a la regularizacin',
  `prior_annual_irpf` double(15,3) default NULL COMMENT 'Retenciones anuales anteriores a la regularizacin',
  `prior_annual_remuneration` double(15,3) default NULL COMMENT 'Retribucines anulaes consideradas con anterioridad a la regularizacin',
  `prior_base_irpf` double(15,3) default NULL COMMENT 'Base para calcular el tipo de retencin determinado antes de la regularizacin',
  `prior_irpf` double(15,2) default NULL COMMENT 'Tipo de retencin aplicado antes de la regularizacin',
  `prior_in_ceuta_melilla` tinyint(1) default NULL COMMENT 'Los rendimientos anteriores a la regularizacin fueron obtenidos en Ceuta o Melilla',
  `prior_minimun_personal_family` double(15,3) default NULL COMMENT 'Mnimo personal y familiar para calcular el tipo de retencin determinado antes de la regularizacin',
  `prior_deduct_home_loan` tinyint(2) default NULL COMMENT 'En algn momento antes de la regularizacin de aplico la minoracin por pagos por la adquisin o rehabilitacin de la vivienda',
  `prior_deduct_home_loan_amount` double(15,3) default NULL COMMENT 'Importe de la minoracin por pagos por la adquisin o rehabilitacin de la vivienda antes de la regularizacin',
  PRIMARY KEY  (`id`),
  KEY `IDX_IRPF_REGULARIZATION_CONTRACT` (`contract`),
  CONSTRAINT `FK_IRPF_REGULARIZATION_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=95878 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos regularizacion IRPF';

CREATE TABLE `irpf_result` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `contract` int(4) NOT NULL COMMENT 'Identificador del contrato',
  `effective_date` date NOT NULL COMMENT 'Fecha de entrada en vigor',
  `base_irpf` double(15,3) default NULL COMMENT 'Base para calcular el tipo de retencin',
  `minimun_personal_family` double(15,3) default NULL COMMENT 'Mnimo personal y familiar para calcular el tipo de retencin',
  `deduct_home_loan_amount` double(15,3) default NULL COMMENT 'Minoracin por pagos de prstamo para vivienda habitual',
  `deduct_80_bis` double(15,3) default NULL COMMENT 'Deduccion Arttculo 80 bis LIRPF',
  `irpf` double(15,2) default NULL COMMENT 'Tipo retencin apliclabe',
  `annual_irpf` double(15,3) default NULL COMMENT 'Importe anual de las retenciones e ingresos a cuenta',
  `annual_remuneration` double(15,3) default NULL COMMENT 'Retribuciones anuales. Importe ntegro',
  `irregular_18_2_reduction` double(15,3) default NULL COMMENT 'Reducciones por irregularidad ( Art. 18.2 LIRPF). Importe',
  `irregular_18_3_reduction` double(15,3) default NULL COMMENT 'Reducciones por irregularidad ( Art. 18.3: DD.TT 11 y 12 de la LIRPF). Importe',
  `deduccibles_expenses` double(15,3) default NULL COMMENT 'Gastos deducibles. Importe anual',
  `work_remuneration_reduction` double(15,3) default NULL COMMENT 'Reducciones por rendimiento del trabajo',
  `work_prolongation_reduction` double(15,3) default NULL COMMENT 'Reducciones por prolongacin de la actividad',
  `work_moving_reduction` double(15,3) default NULL COMMENT 'Reducciones por movilidad geografica',
  `work_disability_reduction` double(15,3) default NULL COMMENT 'Reducciones por discapacidad',
  `social_security_pensioner` double(15,3) default NULL COMMENT 'Por ser pensionista de la s. social/cl. Pasivas o desempleado',
  `two_or_more_descendents_min` double(15,3) default NULL COMMENT 'Por tener ms de dos descendientes con derecho a mnimo',
  `spousal_support` double(15,3) default NULL COMMENT 'Pension compensatoria a favor del cnyuge. Importe anual',
  `food_annuity` double(15,3) default NULL COMMENT 'Anualidades por alimentos en favor de los hijos. Importe anual',
  `minimun_personal` double(15,3) default NULL COMMENT 'Mnimo personal',
  `minimun_ascendents` double(15,3) default NULL COMMENT 'MNimo por ascendientes',
  `minimun_descendents` double(15,3) default NULL COMMENT 'MNimo por descendientes',
  `minimun_disability` double(15,3) default NULL COMMENT 'Mnimo por discapacidad',
  `descendents_minor_3_total` tinyint(2) default NULL COMMENT 'Descendientes computados menores de tres aos. Total',
  `descendents_minor_3_entirely` tinyint(2) default NULL COMMENT 'Descendientes computados menores de tres aos. Por entero',
  `descendents_remainder_total` tinyint(2) default NULL COMMENT 'Resto de descendientes computados . Total',
  `descendents_remainder_entirely` tinyint(2) default NULL COMMENT 'Resto de descendientes computados . Por entero',
  `descendents_33_65_total` tinyint(2) default NULL COMMENT 'Descendientes con discapacidad >= 33% y < 65%. Total',
  `descendents_33_65_entirely` tinyint(2) default NULL COMMENT 'Descendientes con discapacidad >= 33% y < 65%. Por entero',
  `descendents_moving_total` tinyint(2) default NULL COMMENT 'Descendientes con discapacidad, movilidad reducida. Total',
  `descendents_moving_entirely` tinyint(2) default NULL COMMENT 'Descendientes con discapacidad, movilidad reducida. Por entero',
  `descendents_65_total` tinyint(2) default NULL COMMENT 'Descendientes con discapacidad > 65%. Total',
  `descendents_65_entirely` tinyint(2) default NULL COMMENT 'Descendientes con discapacidad > 65%. Por entero',
  `descendents_first` tinyint(2) default NULL COMMENT 'Detalle del cmputo de descendientes. Hijo 1',
  `descendents_second` tinyint(2) default NULL COMMENT 'Detalle del cmputo de descendientes. Hijo 2',
  `descendents_third` tinyint(2) default NULL COMMENT 'Detalle del cmputo de descendientes. Hijo 3',
  `descendents_fourth_subsequent_total` tinyint(2) default NULL COMMENT 'Detalle del cmputo de descendientes. Hijo 4 y sucesivos. Total',
  `descendents_fourth_subsequent_entirely` tinyint(2) default NULL COMMENT 'Detalle del cmputo de descendientes. Hijo 4 y sucesivos. Por entero',
  `ascendents_minor_75_total` tinyint(2) default NULL COMMENT 'Ascendientes computados menores de 75 aos. Total',
  `ascendents_minor_75_entirely` tinyint(2) default NULL COMMENT 'Ascendientes computados menores de 75 aos. Por entero',
  `ascendents_mayor_75_total` tinyint(2) default NULL COMMENT 'Ascendientes computados mayores de 75 aos. Total',
  `ascendents_mayor_75_entirely` tinyint(2) default NULL COMMENT 'Ascendientes computados mayores de 75 aos. Por entero',
  `ascendents_33_65_total` tinyint(2) default NULL COMMENT 'Ascendientes con discapacidad >= 33% y < 65%. Total',
  `ascendents_33_65_entirely` tinyint(2) default NULL COMMENT 'Ascendientes con discapacidad >= 33% y < 65%. Por entero',
  `ascendents_moving_total` tinyint(2) default NULL COMMENT 'Ascendientes con discapacidad, movilidad reducida. Total',
  `ascendents_moving_entirely` tinyint(2) default NULL COMMENT 'Ascendientes con discapacidad, movilidad reducida. Por entero',
  `ascendents_65_total` tinyint(2) default NULL COMMENT 'Ascendientes con discapacidad > 65%. Total',
  `ascendents_65_entirely` tinyint(2) default NULL COMMENT 'Ascendientes con discapacidad > 65%. Por entero',
  PRIMARY KEY  (`id`),
  KEY `IDX_IRPF_RESULT_CONTRACT` (`contract`),
  CONSTRAINT `FK_IRPF_RESULT_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=117945 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Resultados IRPF';


UPDATE `db_version` SET `version_number` = '6.16.1';

COMMIT;
