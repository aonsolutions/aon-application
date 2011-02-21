# Database: aon_master
# Version: Actualizacion de la version 5.9.0 a la version 6.0.0.
# Created by: rtrepiana
# Creation Date: 04/10/2010 

BEGIN;

ALTER TABLE `calendar` DROP COLUMN `source`;
ALTER TABLE `calendar` DROP COLUMN `source_id`;
ALTER TABLE `calendar` ADD COLUMN `generic` tinyint(1) default '1' COMMENT 'Indica si es editable o no';
ALTER TABLE `calendar` ADD COLUMN `calendar` int(4) default NULL COMMENT 'Calendario del que se hereda';
ALTER TABLE `calendar` ADD KEY `IDX_CALENDAR_CALENDAR` (`calendar`);
ALTER TABLE `calendar` ADD CONSTRAINT `FK_CALENDAR_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`);

ALTER TABLE `enterprise` ADD COLUMN `calendar` int(4) default NULL COMMENT 'Calendario';
ALTER TABLE `enterprise` ADD KEY `IDX_ENTERPRISE_CALENDAR` (`calendar`);
ALTER TABLE `enterprise` ADD CONSTRAINT `FK_ENTERPRISE_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`);
ALTER TABLE `workplace` ADD COLUMN `calendar` int(4) default NULL COMMENT 'Calendario';
ALTER TABLE `workplace` ADD KEY `IDX_WORKPLACE_CALENDAR` (`calendar`);
ALTER TABLE `workplace` ADD CONSTRAINT `FK_WORKPLACE_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`);

CREATE TABLE `contract_calendar_event` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `contract` int(4) NOT NULL COMMENT 'Identificador del Contrato',
  `date` date NOT NULL COMMENT 'Fecha de la incidencia',
  `type` tinyint(2) default NULL COMMENT 'Tipo de incidencia',
  `duration` double default NULL COMMENT 'Duracion de la incidencia',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_CALENDAR_EVENT_CONTRACT` (`contract`),
  CONSTRAINT `FK_CONTRACT_CALENDAR_EVENT_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Incidencias de calendario en Contratos';

ALTER TABLE calendar_holiday ADD `day_type` tinyint(2) default '0' COMMENT 'Tipo de dia';
ALTER TABLE calendar_holiday ADD `hours` double default '0' COMMENT 'Numero de horas laborables';

ALTER TABLE `workplace` ADD COLUMN `enterprise_activity` int(4) DEFAULT NULL COMMENT 'Actividad';
ALTER TABLE `workplace` ADD KEY `IDX_WORKPLACE_ENTERPRISE_ACTIVITY` (`enterprise_activity`);
ALTER TABLE `workplace` ADD CONSTRAINT `FK_WORKPLACE_ENTERPRISE_ACTIVITY` FOREIGN KEY (`enterprise_activity`) REFERENCES `enterprise_activity` (`id`);

ALTER TABLE `enterprise_ccc` MODIFY `ccc` char(11) collate latin1_spanish_ci DEFAULT NULL COMMENT 'Valor del Codigo Cuenta Cotizacion';
ALTER TABLE `enterprise_ccc` MODIFY `geozone` int(4) DEFAULT NULL COMMENT 'Identificador de la Zona Geografica';

ALTER TABLE `enterprise_activity` MODIFY `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Actividad de la Empresa';

ALTER TABLE `rdir_staff` ADD COLUMN `representative_labor` tinyint(1) NOT NULL default '0' COMMENT 'Indica si el Directivo es representante laboral';

ALTER TABLE `person` ADD COLUMN `name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Nombre';
ALTER TABLE `person` ADD COLUMN `first_surname` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Primer Apellido ';
ALTER TABLE `person` ADD COLUMN `second_surname` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Segundo Apellido';

UPDATE `registry` SET `name` = `surname`, `surname` = '' WHERE `name` IS NULL OR TRIM(`name`) = '';
UPDATE `registry` SET `name` = CONCAT(RTRIM(`surname`),', ',RTRIM(`name`)) WHERE `surname` IS NOT NULL AND TRIM(`surname`) != '';
ALTER TABLE `registry` DROP COLUMN `surname` ;
ALTER TABLE `registry` ADD COLUMN `document_type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de documento (NIF, CIF...)' AFTER `document`;
ALTER TABLE `registry` ADD COLUMN `document_country` varchar(2) DEFAULT NULL COMMENT 'Pais del documento' AFTER `document_type`;
ALTER TABLE `registry` ADD COLUMN `nationality` varchar(2) DEFAULT NULL COMMENT 'Nacionalidad' ;

ALTER TABLE `raddress` ADD COLUMN `number` varchar(12) DEFAULT NULL COMMENT 'Numero' AFTER `address`;
ALTER TABLE `raddress` ADD COLUMN `alias`  varchar(15) DEFAULT NULL COMMENT 'Alias';
ALTER TABLE `raddress` MODIFY `street_type` VARCHAR(2) NULL DEFAULT 'CL' COMMENT 'Tipo de via';
UPDATE `raddress` set `street_type` = 'ZZ' WHERE `street_type` = 0;
UPDATE `raddress` set `street_type` = 'AV' WHERE `street_type` = 1;
UPDATE `raddress` set `street_type` = 'BD' WHERE `street_type` = 2;
UPDATE `raddress` set `street_type` = 'BD' WHERE `street_type` = 3;
UPDATE `raddress` set `street_type` = 'BL' WHERE `street_type` = 4;
UPDATE `raddress` set `street_type` = 'CL' WHERE `street_type` = 5;
UPDATE `raddress` set `street_type` = 'CM' WHERE `street_type` = 6;
UPDATE `raddress` set `street_type` = 'CO' WHERE `street_type` = 7;
UPDATE `raddress` set `street_type` = 'CT' WHERE `street_type` = 8;
UPDATE `raddress` set `street_type` = 'ED' WHERE `street_type` = 9;
UPDATE `raddress` set `street_type` = 'RD' WHERE `street_type` = 10;
UPDATE `raddress` set `street_type` = 'PJ' WHERE `street_type` = 11;
UPDATE `raddress` set `street_type` = 'PG' WHERE `street_type` = 12;
UPDATE `raddress` set `street_type` = 'PQ' WHERE `street_type` = 13;
UPDATE `raddress` set `street_type` = 'TR' WHERE `street_type` = 14;
UPDATE `raddress` set `street_type` = 'PZ' WHERE `street_type` = 15;
UPDATE `raddress` set `street_type` = 'AV' WHERE `street_type` = 16;
UPDATE `raddress` set `street_type` = 'UR' WHERE `street_type` = 17;
UPDATE `raddress` set `street_type` = 'CT' WHERE `street_type` = 18;
UPDATE `raddress` set `street_type` = 'ZZ' WHERE `street_type` = 19;
UPDATE `raddress` set `street_type` = null WHERE `street_type` < 'A';

ALTER TABLE `rmedia` ADD COLUMN `raddress` int(4) DEFAULT NULL COMMENT 'Direccion del contacto' ;
ALTER TABLE `rmedia` ADD KEY `IDX_RMEDIA_RADDRESS`(`raddress`);
ALTER TABLE `rmedia` ADD CONSTRAINT `FK_RMEDIA_RADDRESS` FOREIGN KEY (`raddress`) REFERENCES `raddress` (`id`);

ALTER TABLE `contract` 	DROP FOREIGN KEY `FK_CONTRACT_TYPE`;
ALTER TABLE `contract`  DROP COLUMN `type`;
DROP TABLE `contract_type`;
DROP TABLE `contract_tracking`;

ALTER TABLE `contract` ADD COLUMN `calendar` int(4) default NULL COMMENT 'Calendario';
ALTER TABLE `contract` ADD KEY `IDX_CONTRACT_CALENDAR` (`calendar`);
ALTER TABLE `contract` ADD CONSTRAINT `FK_CONTRACT_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`);
ALTER TABLE `contract` ADD COLUMN `document` mediumblob COMMENT 'Impreso (.pdf) del contrato.';
ALTER TABLE `contract` ADD COLUMN `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion';
ALTER TABLE `contract` ADD COLUMN `status` tinyint(2) DEFAULT '0' COMMENT 'Estado de notificacion del contrato';
ALTER TABLE `contract` ADD COLUMN `registration` int(4)   NOT NULL COMMENT 'Número libro de matricula';
ALTER TABLE `contract` ADD COLUMN `seniority_date` date NOT NULL COMMENT 'Fecha de antiguedad ';
ALTER TABLE `contract` ADD COLUMN `enterprise_activity` int(4) default NULL COMMENT 'Actividad';
ALTER TABLE `contract` ADD KEY `IDX_CONTRACT_ENTERPRISE_ACTIVITY` (`calendar`);
ALTER TABLE `contract` ADD CONSTRAINT `FK_CONTRACT_ENTERPRISE_ACTIVITY` FOREIGN KEY (`enterprise_activity`) REFERENCES `enterprise_activity` (`id`);
ALTER TABLE `contract` DROP FOREIGN KEY `FK_CONTRACT_CCC`;
ALTER TABLE `contract` CHANGE COLUMN `ccc` `enterprise_ccc` int(4) default NULL COMMENT 'CCC';
ALTER TABLE `contract` ADD KEY `IDX_CONTRACT_ENTERPRISE_CCC` (`enterprise_ccc`);
ALTER TABLE `contract` ADD CONSTRAINT `FK_CONTRACT_ENTERPRISE_CCC` FOREIGN KEY (`enterprise_ccc`) REFERENCES `enterprise_ccc` (`id`);
ALTER TABLE `contract` ADD COLUMN `ss_regime` tinyint(2) NOT NULL  DEFAULT '0' COMMENT 'Regimen de la Seguridad Social';

CREATE TABLE `agreement` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `calendar` int(4) default NULL COMMENT 'Calendario',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_AGREEMENT_CALENDAR` (`calendar`),
  CONSTRAINT `FK_AGREEMENT_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Convenios';

ALTER TABLE `workplace` ADD COLUMN `agreement` int(4) DEFAULT NULL COMMENT 'Convenio';
ALTER TABLE `workplace` ADD KEY `IDX_WORKPLACE_AGREEMENT` (`agreement`);
ALTER TABLE `workplace` ADD CONSTRAINT `FK_WORKPLACE_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`);
ALTER TABLE `enterprise` ADD COLUMN `agreement` int(4) DEFAULT NULL COMMENT 'Convenio';
ALTER TABLE `enterprise` ADD KEY `IDX_ENTERPRISE_AGREEMENT` (`agreement`);
ALTER TABLE `enterprise` ADD CONSTRAINT `FK_ENTERPRISE_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`);

CREATE TABLE `agreement_level` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `agreement` int(4) NOT NULL COMMENT 'Convenio',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_LEVEL_AGREEMENT` (`agreement`),
  CONSTRAINT `FK_LEVEL_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Niveles retributivos';

CREATE TABLE `agreement_level_category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `agreement_level` int(4) NOT NULL COMMENT 'Nivel retributivo',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_CATEGORY_AGREEMENT_LEVEL` (`agreement_level`),
  CONSTRAINT `FK_CATEGORY_AGREEMENT_LEVEL` FOREIGN KEY (`agreement_level`) REFERENCES `agreement_level` (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias profesionales';

CREATE TABLE `agreement_level_payment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `agreement_level` int(4) NOT NULL COMMENT 'Nivel retributivo',
  `type` tinyint(2) COMMENT 'Tipo de Percepción Salarial',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Fórmula',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_PAYMENT_AGREEMENT_LEVEL` (`agreement_level`),
  CONSTRAINT `FK_PAYMENT_AGREEMENT_LEVEL` FOREIGN KEY (`agreement_level`) REFERENCES `agreement_level` (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Percepciones';

CREATE TABLE `payment_concept` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `code` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Codigo',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Percepcion Salarial',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos de devengos';

CREATE TABLE `deduction_concept` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `code` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Codigo',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Deduccion Salarial',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos de deducciones';

INSERT INTO `deduction_concept` 
	(id	,code	,description					,type ) 
VALUES
	(1, 'CGC'	,"(BASE_CGC/CGC*100)+'%'"		,0),
	(2, 'CGP'	,"(BASE_CGP/CGP*100)+'%'"		,1),
	(3, 'DESMP'	,"(BASE_CGP/DESMP*100)+'%'"		,2),
	(4, 'FP'	,"(BASE_CGP/FP*100)+'%'"		,3),
	(5, 'NESTR'	,"(BASE_NESTR/NESTR*100)+'%'"	,4),
	(6, 'ESTR'	,"(BASE_ESTR/ESTR*100)+'%'"		,5),
	(7, 'IRPF'	,"(BASE_IRPF/IRPF*100)+'%'"		,6);

CREATE TABLE `system_deduction` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `type` tinyint(2) COMMENT 'Tipo de Deducción',
  `deduction_concept` int(4) COMMENT 'Identificador unico del concepto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `description_decorable` tinyint(2) default '0' COMMENT '',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Fórmula',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `month` tinyint(2) default null COMMENT 'Mes de la deducción',
  PRIMARY KEY  (`id`),
  KEY `IDX_SYSTEM_DEDUCTION_DEDUCTION_CONCEPT` (`deduction_concept`),
  CONSTRAINT `FK_SYSTEM_DEDUCTION_DEDUCTION_CONCEPT` FOREIGN KEY (`deduction_concept`) REFERENCES `deduction_concept` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Deducciones';

INSERT INTO `system_deduction` 
	(type	,deduction_concept	,expression							,start_date		,description_decorable	 )
VALUES
	(0		,1					,"BASE_CGC * 4.70/100"				,'2010-01-01'	,1), 
	(2		,3					,"BASE_CGP * (INDEFINIDO ? 1.55 : 1,60 )/100"
																	,'2010-01-01'	,1), 
	(3		,4					,"BASE_CGP * 0.10/100"				,'2010-01-01'	,1),  
	(4		,5					,"BASE_NESTR * 4.70/100"			,'2010-01-01'	,1), 
	(5		,6					,"BASE_ESTR * 2.00/100"				,'2010-01-01'	,1),
	(6		,7					,"BASE_IRPF * PORCENTAJE_IRPF/100"	,'2010-01-01'	,1);



CREATE TABLE `contract_bonus` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Fórmula',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_BONUS_CONTRACT` (`contract`),
  CONSTRAINT `FK_BONUS_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bonificaciones';

CREATE TABLE `contract_payment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `type` tinyint(2) COMMENT 'Tipo de Percepción Salarial',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `payment_concept` int(4) COMMENT 'Identificador unico del concepto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `description_decorable` tinyint(2) default '0' COMMENT '',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe',
  `irpf_expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe tributable',
  `quote_expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe cotizable',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `month` tinyint(2) default null COMMENT 'Mes de la percepcion',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `salary_type` tinyint(2) COMMENT 'Tipo de Nomina/Recibo',
  PRIMARY KEY  (`id`),
  KEY `IDX_PAYMENT_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_PAYMENT_PAYMENT_CONCEPT` (`payment_concept`),
  CONSTRAINT `FK_PAYMENT_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_PAYMENT_PAYMENT_CONCEPT` FOREIGN KEY (`payment_concept`) REFERENCES `payment_concept` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Percepciones Salariales';

CREATE TABLE `contract_deduction` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `type` tinyint(2) COMMENT 'Tipo de Deducción',
  `deduction_concept` int(4) COMMENT 'Identificador unico del concepto',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `description_decorable` tinyint(2) default '0' COMMENT '',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Fórmula',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `month` tinyint(2) default null COMMENT 'Mes de la percepcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_DEDUCTION_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_DEDUCTION_DEDUCTION_CONCEPT` (`deduction_concept`),
  CONSTRAINT `FK_DEDUCTION_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_DEDUCTION_DEDUCTION_CONCEPT` FOREIGN KEY (`deduction_concept`) REFERENCES `deduction_concept` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Deducciones';

CREATE TABLE `contract_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Nombre',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  KEY `IDX_CONTRACT_CONSTANT_CONTRACT` (`contract`),
  CONSTRAINT `FK_CONTRACT_CONSTANT_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto del contrato';


CREATE TABLE `contract_leave` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `type` tinyint(2) COMMENT 'Tipo de Baja',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `relapse` tinyint(1) DEFAULT 0 COMMENT 'Recaida',
  `description` varchar(64) collate latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `daily_cgc_base` double(15,3) DEFAULT NULL COMMENT 'Base de cotizacion por contingencias comunes',
  `daily_cgp_base` double(15,3) DEFAULT NULL COMMENT 'Base de cotizacion por contingencias profesionales',
  PRIMARY KEY  (`id`),
  KEY `IDX_LEAVE_CONTRACT` (`contract`),
  CONSTRAINT `FK_LEAVE_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bajas';

CREATE TABLE `salary` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `type` tinyint(2) COMMENT 'Tipo de Nomina',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio liquidación',
  `end_date` date NOT NULL COMMENT 'Fecha de finalizacion liquidación',
  `enterprise_name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Nombre de la empresa',
  `enterprise_address` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Domicilio de la empresa',
  `enterprise_document` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Numero de Documento de la Empresa',
  `ccc` char(11) collate latin1_spanish_ci default NULL COMMENT 'Valor del Codigo Cuenta Cotizacion',
  `employee_name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Nombre del trabajador',
  `social_security_number` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Numero de la seguridad social',
  `employee_document` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Numero de Documento de la Persona',
  `seniority_date` date DEFAULT NULL COMMENT 'Fecha de antiguedad',
  `quote_group` varchar(2) collate latin1_spanish_ci default NULL COMMENT 'Grupo de Cotización',
  `category` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Categoria o grupo profesional',
  `registration` int(4)   NOT NULL COMMENT 'Número libro de matricula',
  `time_units` int(4) NOT NULL COMMENT 'total dias/horas',
  `total_payment` double(15,3) NOT NULL default '0.000'  COMMENT 'Total devengado',
  `total_deduction` double(15,3)NOT NULL  default '0.000'  COMMENT 'Total a deducir',
  `total_liquid` double(15,3) NOT NULL default '0.000'  COMMENT 'Liquido total a percibir',
  `total_enterprise` double(15,3) NOT NULL default '0.000'  COMMENT 'Cuota total de la empresa',
  `issue_date` date NOT NULL COMMENT 'Fecha de emisión',
  `remuneration` double(15,3) NOT NULL default '0.000'  COMMENT 'Remuneración mensual',
  `pro_ext_base` double(15,3) NOT NULL default '0.000'  COMMENT 'Base prorraterreada de pagas extras',
  `it_base` double(15,3) NOT NULL default '0.000'  COMMENT 'Base de IT',
  `raw_cgc_base` double(15,3) NOT NULL default '0.000'  COMMENT 'Base efectiva de cotizacion por contingencias comunes ',
  `cgc_base` double(15,3) NOT NULL default '0.000'  COMMENT 'Base de cotizacion por contingencias comunes',
  `hextra_base` double(15,3) NOT NULL default '0.000'  COMMENT 'Base de cotizacion adicional por horas extraordinarias estructurales',
  `non_hextra_base` double(15,3) NOT NULL default '0.000'  COMMENT 'Base de cotizacion adicional por horas extraordinarias no estructurales',
  `cgp_base` double(15,3) NOT NULL default '0.000'  COMMENT 'Base de cotizacion por contingencias profesionales',
  `money_irpf_base` double(15,3) NOT NULL default '0.000'  COMMENT 'Salario en dinero sujeto a retención I.R.P.F',
  `inkind_irpf_base` double(15,3) NOT NULL default '0.000'  COMMENT 'Salario en especie sujeto a retención I.R.P.F',
  `irpf_base` double(15,3) NOT NULL default '0.000'  COMMENT 'Base sujeta a retención I.R.P.F',
  `social_security_contributions` double(15,3) NOT NULL default '0.000'  COMMENT 'Aportaciones a la Seguridad Social',
  PRIMARY KEY  (`id`),
  KEY `IDX_SALARY_RECCEIPT_CONTRACT` (`contract`),
  CONSTRAINT `FK_SALARY_RECCEIPT_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Recibo del pago de salarios';

CREATE TABLE `salary_payment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios',
  `type` tinyint(2) COMMENT 'Tipo de Percepción Salarial',
  `payment_concept` varchar(5) COMMENT 'Codigo del concepto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Fórmula',
  `amount` double(15,3) default '0.000'  COMMENT 'Importe',  
  PRIMARY KEY  (`id`),
  KEY `IDX_PAYMENT_SALARY` (`salary`),
  CONSTRAINT `FK_PAYMENT_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Percepciones salariales';

CREATE TABLE `salary_deduction` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios',
  `type` tinyint(2) COMMENT 'Tipo de deducción Salarial',
  `deduction_concept` int(4) COMMENT 'Identificador unico del concepto',  
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Fórmula',
  `amount` double(15,3) default '0.000'  COMMENT 'Importe',
  PRIMARY KEY  (`id`),
  KEY `IDX_DEDUCTION_SALARY` (`salary`),
  CONSTRAINT `FK_DEDUCTION_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Deducciones';

CREATE TABLE `contract_batch` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la remesa de contratos',
  `date` date NOT NULL COMMENT 'Fecha de la ultima remesa en la que fue incluido',
  `red_notify_date` date default NULL COMMENT 'Fecha de notificacion al sistema red',
  `red_notify_id` date default NULL COMMENT 'Identificador de la notificacion',
  `red_response_date` date default NULL COMMENT 'Fecha de respuesta del sistema red',
  `red_response_id` date default NULL COMMENT 'Identificador de la respuesta',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas de contratos';


CREATE TABLE `contract_batch_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del detalle de la remesa',
  `contract_batch` int(4) NOT NULL COMMENT 'Identificador unico de la remesa de contratos',
  `contract` int(4) NOT NULL COMMENT 'Identificador unico del contrato',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_BATCH_DETAIL_CONTRACT_BATCH` (`contract_batch`),
  KEY `IDX_CONTRACT_BATCH_DETAIL_CONTRACT` (`contract`),
  CONSTRAINT `FK_CONTRACT_BATCH_DETAIL_CONTRACT_BATCH` FOREIGN KEY (`contract_batch`) REFERENCES `contract_batch` (`id`),
  CONSTRAINT `FK_CONTRACT_BATCH_DETAIL_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las remesas de contratos';


CREATE TABLE `enterprise_certificate` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del certificado de empresa de la remesa',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador unico de la empresa',
  `date` date NOT NULL COMMENT 'Fecha de la ultima remesa en la que fue incluido',
  `status` int(4) default NULL COMMENT 'Estado del certificado correspondiente a la ultima respuesta',
  `sign` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Estado del certificado correspondiente a la ultima respuesta',
  PRIMARY KEY  (`id`),
  KEY `IDX_ENTERPRISE_CERTIFICATE_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_ENTERPRISE_CERTIFICATE_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas de certificados de empresa';

CREATE TABLE `enterprise_certificate_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del certificado de empresa de la remesa',
  `enterprise_certificate` int(4) NOT NULL COMMENT 'Identificador unico del certificado de empresa',
  `contract` int(4) NOT NULL COMMENT 'Identificador unico del contrato de empleado',
  `expire_date` date default NULL COMMENT 'Fecha de baja del empleado',
  `suspension_cause` varchar(2) collate latin1_spanish_ci NOT NULL COMMENT 'Causa de la suspension del empleado',
  PRIMARY KEY  (`id`),
  KEY `IDX_ENTERPRISE_CERTIFICATE_DETAIL_ENTERPRISE_CERTIFICATE` (`enterprise_certificate`),
  KEY `IDX_ENTERPRISE_CERTIFICATE_DETAIL_CONTRACT` (`contract`),
  CONSTRAINT `FK_ENTERPRISE_CERTIFICATE_DETAIL_ENTERPRISE_CERTIFICATE` FOREIGN KEY (`enterprise_certificate`) REFERENCES `enterprise_certificate` (`id`),
  CONSTRAINT `FK_ENTERPRISE_CERTIFICATE_DETAIL_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las remesas de certificados de empresa';

CREATE TABLE `function_constant` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Nombre',
  `expression` varchar(512) collate latin1_spanish_ci default NULL COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `read_only` TINYINT(1) NULL COMMENT 'Modificable',
  `comments` VARCHAR(128) NULL COMMENT 'Comentario de ayuda',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto de las funciones';

INSERT INTO `function_constant` 
	(name			,expression					,start_date	,end_date		,read_only	,comments		)
VALUES
	('BASE_CGC_MIN'	,"[	1 : 1031.70,
						2 : 855.90, 
						3 : 744.60, 
						4 : 3198.00, 
						5 : 738.90, 
						6 : 738.90,
						7 : 738.90,
						8 : 24.63*DIAS_MES, 
						9 : 24.63*DIAS_MES,
						10: 24.63*DIAS_MES,
						11: 24.63*DIAS_MES]"
												,'2010-01-01',NULL			,1			,'Bases minimas'), 
	('BASE_CGC_MAX'	,"[	1 : 3198.00,
						2 : 3198.00, 
						3 : 3198.00, 
						4 : 3198.00, 
						5 : 3198.00, 
						6 : 3198.00,
						7 : 3198.00,
						8 : 106.60*DIAS_MES, 
						9 : 106.60*DIAS_MES,
						10: 106.60*DIAS_MES,
						11: 106.60*DIAS_MES]"
												,'2010-01-01',NULL			,1			,'Bases maximas'),
	('IPREM'		,"516.90"					,'2008-01-01','2008-12-31'	,1			,'Indicador Público de Renta de Efectos Múltiples (IPREM) '), 
	('IPREM'		,"527.24"					,'2009-01-01','2009-12-31'	,1			,'Indicador Público de Renta de Efectos Múltiples (IPREM) '), 
	('IPREM'		,"531.51"					,'2010-01-01','2010-12-31'	,1			,'Indicador Público de Renta de Efectos Múltiples (IPREM) '), 
	('IPREM'		,"531.51"					,'2011-01-01',NULL			,1			,'Indicador Público de Renta de Efectos Múltiples (IPREM) ');


INSERT INTO `cnae` ( id, code, title ) 
VALUES 
(0111,'0111','Cultivo de cereales (excepto arroz), leguminosas y semillas oleaginosas'),
(0112,'0112','Cultivo de arroz'),
(0113,'0113','Cultivo de hortalizas, raíces y tubérculos'),
(0114,'0114','Cultivo de caña de azúcar'),
(0115,'0115','Cultivo de tabaco'),
(0116,'0116','Cultivo de plantas para fibras textiles'),
(0119,'0119','Otros cultivos no perennes'),
(0121,'0121','Cultivo de la vid'),
(0122,'0122','Cultivo de frutos tropicales y subtropicales'),
(0123,'0123','Cultivo de cítricos'),
(0124,'0124','Cultivo de frutos con hueso y pepitas'),
(0125,'0125','Cultivo de otros árboles y arbustos frutales y frutos secos'),
(0126,'0126','Cultivo de frutos oleaginosos'),
(0127,'0127','Cultivo de plantas para bebidas'),
(0128,'0128','Cultivo de especias, plantas aromáticas, medicinales y farmacéuticas'),
(0129,'0129','Otros cultivos perennes'),
(0130,'0130','Propagación de plantas'),
(0141,'0141','Explotación de ganado bovino para la producción de leche'),
(0142,'0142','Explotación de otro ganado bovino y búfalos'),
(0143,'0143','Explotación de caballos y otros equinos'),
(0144,'0144','Explotación de camellos y otros camélidos'),
(0145,'0145','Explotación de ganado ovino y caprino'),
(0146,'0146','Explotación de ganado porcino'),
(0147,'0147','Avicultura'),
(0149,'0149','Otras explotaciones de ganado'),
(0150,'0150','Producción agrícola combinada con la producción ganadera'),
(0161,'0161','Actividades de apoyo a la agricultura'),
(0162,'0162','Actividades de apoyo a la ganadería'),
(0163,'0163','Actividades de preparación posterior a la cosecha'),
(0164,'0164','Tratamiento de semillas para reproducción'),
(0170,'0170','Caza, captura de animales y servicios relacionados con las mismas'),
(0210,'0210','Silvicultura y otras actividades forestales'),
(0220,'0220','Explotación de la madera'),
(0230,'0230','Recolección de productos silvestres, excepto madera'),
(0240,'0240','Servicios de apoyo a la silvicultura'),
(0311,'0311','Pesca marina'),
(0312,'0312','Pesca en agua dulce'),
(0321,'0321','Acuicultura marina'),
(0322,'0322','Acuicultura en agua dulce'),
(0510,'0510','Extracción de antracita y hulla'),
(0520,'0520','Extracción de lignito'),
(0610,'0610','Extracción de crudo de petróleo'),
(0620,'0620','Extracción de gas natural'),
(0710,'0710','Extracción de minerales de hierro'),
(0721,'0721','Extracción de minerales de uranio y torio'),
(0729,'0729','Extracción de otros minerales metálicos no férreos'),
(0811,'0811','Extracción de piedra ornamental y para la construcción, piedra caliza, yeso, creta y pizarra'),
(0812,'0812','Extracción de gravas y arenas'),
(0891,'0891','Extracción de minerales para productos químicos y fertilizantes'),
(0892,'0892','Extracción de turba'),
(0893,'0893','Extracción de sal'),
(0899,'0899','Otras industrias extractivas n.c.o.p.'),
(0910,'0910','Actividades de apoyo a la extracción de petróleo y gas natural'),
(0990,'0990','Actividades de apoyo a otras industrias extractivas'),
(1011,'1011','Procesado y conservación de carne'),
(1012,'1012','Procesado y conservación de volatería'),
(1013,'1013','Elaboración de productos cárnicos y de volatería'),
(1021,'1021','Procesado de pescados, crustáceos y moluscos'),
(1022,'1022','Fabricación de conservas de pescado'),
(1031,'1031','Procesado y conservación de patatas'),
(1032,'1032','Elaboración de zumos de frutas y hortalizas'),
(1039,'1039','Otro procesado y conservación de frutas y hortalizas'),
(1042,'1042','Fabricación de margarina y grasas comestibles similares'),
(1043,'1043','Fabricación de aceite de oliva'),
(1044,'1044','Fabricación de otros aceites y grasas'),
(1052,'1052','Elaboración de helados'),
(1053,'1053','Fabricación de quesos'),
(1054,'1054','Preparación de leche y otros productos lácteos'),
(1061,'1061','Fabricación de productos de molinería'),
(1062,'1062','Fabricación de almidones y productos amiláceos'),
(1071,'1071','Fabricación de pan y de productos frescos de panadería y pastelería'),
(1072,'1072','Fabricación de galletas y productos de panadería y pastelería de larga duración'),
(1073,'1073','Fabricación de pastas alimenticias, cuscús y productos similares'),
(1081,'1081','Fabricación de azúcar'),
(1082,'1082','Fabricación de cacao, chocolate y productos de confitería'),
(1083,'1083','Elaboración de café, té e infusiones'),
(1084,'1084','Elaboración de especias, salsas y condimentos'),
(1085,'1085','Elaboración de platos y comidas preparados'),
(1086,'1086','Elaboración de preparados alimenticios homogeneizados y alimentos dietéticos'),
(1089,'1089','Elaboración de otros productos alimenticios n.c.o.p.'),
(1091,'1091','Fabricación de productos para la alimentación de animales de granja'),
(1092,'1092','Fabricación de productos para la alimentación de animales de compañía'),
(1101,'1101','Destilación, rectificación y mezcla de bebidas alcohólicas'),
(1102,'1102','Elaboración de vinos'),
(1103,'1103','Elaboración de sidra y otras bebidas fermentadas a partir de frutas'),
(1104,'1104','Elaboración de otras bebidas no destiladas, procedentes de la fermentación'),
(1105,'1105','Fabricación de cerveza'),
(1106,'1106','Fabricación de malta'),
(1107,'1107','Fabricación de bebidas no alcohólicas'),
(1200,'1200','Industria del tabaco'),
(1310,'1310','Preparación e hilado de fibras textiles'),
(1320,'1320','Fabricación de tejidos textiles'),
(1330,'1330','Acabado de textiles'),
(1391,'1391','Fabricación de tejidos de punto'),
(1392,'1392','Fabricación de artículos confeccionados con textiles, excepto prendas de vestir'),
(1393,'1393','Fabricación de alfombras y moquetas'),
(1394,'1394','Fabricación de cuerdas, cordeles, bramantes y redes'),
(1395,'1395','Fabricación de telas no tejidas y artículos confeccionados con ellas, excepto prendas de vestir'),
(1396,'1396','Fabricación de otros productos textiles de uso técnico e industrial'),
(1399,'1399','Fabricación de otros productos textiles n.c.o.p.'),
(1411,'1411','Confección de prendas de vestir de cuero'),
(1412,'1412','Confección de ropa de trabajo'),
(1413,'1413','Confección de otras prendas de vestir exteriores'),
(1414,'1414','Confección de ropa interior'),
(1419,'1419','Confección de otras prendas de vestir y accesorios'),
(1420,'1420','Fabricación de artículos de peletería'),
(1431,'1431','Confección de calcetería'),
(1439,'1439','Confección de otras prendas de vestir de punto'),
(1511,'1511','Preparación, curtido y acabado del cuero'),
(1512,'1512','Fabricación de artículos de marroquinería, viaje y de guarnicionería y talabartería'),
(1520,'1520','Fabricación de calzado'),
(1610,'1610','Aserrado y cepillado de la madera'),
(1621,'1621','Fabricación de chapas y tableros de madera'),
(1622,'1622','Fabricación de suelos de madera ensamblados'),
(1623,'1623','Fabricación de otras estructuras de madera y piezas de carpintería y ebanistería para la construcción'),
(1624,'1624','Fabricación de envases y embalajes de madera'),
(1629,'1629','Fabricación de otros productos de madera'),
(1711,'1711','Fabricación de pasta papelera'),
(1712,'1712','Fabricación de papel y cartón'),
(1721,'1721','Fabricación de papel y cartón ondulados'),
(1722,'1722','Fabricación de artículos de papel y cartón para uso doméstico, sanitario e higiénico'),
(1723,'1723','Fabricación de artículos de papelería'),
(1724,'1724','Fabricación de papeles pintados'),
(1729,'1729','Fabricación de otros artículos de papel y cartón'),
(1811,'1811','Artes gráficas y servicios relacionados con las mismas'),
(1812,'1812','Otras actividades de impresión y artes gráficas'),
(1813,'1813','Servicios de preimpresión y preparación de soportes'),
(1814,'1814','Encuadernación y servicios relacionados con la misma'),
(1820,'1820','Reproducción de soportes grabados'),
(1910,'1910','Coquerías'),
(1920,'1920','Refino de petróleo'),
(2011,'2011','Fabricación de gases industriales'),
(2012,'2012','Fabricación de colorantes y pigmentos'),
(2013,'2013','Fabricación de otros productos básicos de química inorgánica'),
(2014,'2014','Fabricación de otros productos básicos de química orgánica'),
(2015,'2015','Fabricación de fertilizantes y compuestos nitrogenados'),
(2016,'2016','Fabricación de plásticos en formas primarias'),
(2017,'2017','Fabricación de caucho sintético en formas primarias'),
(2020,'2020','Fabricación de pesticidas y otros productos agroquímicos'),
(2030,'2030','Fabricación de pinturas, barnices y revestimientos similares'),
(2041,'2041','Fabricación de jabones, detergentes y otros artículos de limpieza y abrillantamiento'),
(2042,'2042','Fabricación de perfumes y cosméticos'),
(2051,'2051','Fabricación de explosivos'),
(2052,'2052','Fabricación de colas'),
(2053,'2053','Fabricación de aceites esenciales'),
(2059,'2059','Fabricación de otros productos químicos n.c.o.p.'),
(2060,'2060','Fabricación de fibras artificiales y sintéticas'),
(2110,'2110','Fabricación de productos farmacéuticos de base'),
(2120,'2120','Fabricación de especialidades farmacéuticas'),
(2211,'2211','Fabricación de neumáticos y cámaras de caucho'),
(2219,'2219','Fabricación de otros productos de caucho'),
(2221,'2221','Fabricación de placas, hojas, tubos y perfiles de plástico'),
(2222,'2222','Fabricación de envases y embalajes de plástico'),
(2223,'2223','Fabricación de productos de plástico para la construcción'),
(2229,'2229','Fabricación de otros productos de plástico'),
(2311,'2311','Fabricación de vidrio plano'),
(2312,'2312','Manipulado y transformación de vidrio plano'),
(2313,'2313','Fabricación de vidrio hueco'),
(2314,'2314','Fabricación de fibra de vidrio'),
(2319,'2319','Fabricación y manipulado de otro vidrio, incluido el vidrio técnico'),
(2320,'2320','Fabricación de productos cerámicos refractarios'),
(2331,'2331','Fabricación de azulejos y baldosas de cerámica'),
(2332,'2332','Fabricación de ladrillos, tejas y productos de tierras cocidas para la construcción'),
(2341,'2341','Fabricación de artículos cerámicos de uso doméstico y ornamental'),
(2342,'2342','Fabricación de aparatos sanitarios cerámicos'),
(2343,'2343','Fabricación de aisladores y piezas aislantes de material cerámico'),
(2344,'2344','Fabricación de otros productos cerámicos de uso técnico'),
(2349,'2349','Fabricación de otros productos cerámicos'),
(2351,'2351','Fabricación de cemento'),
(2352,'2352','Fabricación de cal y yeso'),
(2361,'2361','Fabricación de elementos de hormigón para la construcción'),
(2362,'2362','Fabricación de elementos de yeso para la construcción'),
(2363,'2363','Fabricación de hormigón fresco'),
(2364,'2364','Fabricación de mortero'),
(2365,'2365','Fabricación de fibrocemento'),
(2369,'2369','Fabricación de otros productos de hormigón, yeso y cemento'),
(2370,'2370','Corte, tallado y acabado de la piedra'),
(2391,'2391','Fabricación de productos abrasivos'),
(2399,'2399','Fabricación de otros productos minerales no metálicos n.c.o.p.'),
(2410,'2410','Fabricación de productos básicos de hierro, acero y ferroaleaciones'),
(2420,'2420','Fabricación de tubos, tuberías, perfiles huecos y sus accesorios, de acero'),
(2431,'2431','Estirado en frío'),
(2432,'2432','Laminación en frío'),
(2433,'2433','Producción de perfiles en frío por conformación con plegado'),
(2434,'2434','Trefilado en frío'),
(2441,'2441','Producción de metales preciosos'),
(2442,'2442','Producción de aluminio'),
(2443,'2443','Producción de plomo, zinc y estaño'),
(2444,'2444','Producción de cobre'),
(2445,'2445','Producción de otros metales no férreos'),
(2446,'2446','Procesamiento de combustibles nucleares'),
(2451,'2451','Fundición de hierro'),
(2452,'2452','Fundición de acero'),
(2453,'2453','Fundición de metales ligeros'),
(2454,'2454','Fundición de otros metales no férreos'),
(2511,'2511','Fabricación de estructuras metálicas y sus componentes'),
(2512,'2512','Fabricación de carpintería metálica'),
(2521,'2521','Fabricación de radiadores y calderas para calefacción central'),
(2529,'2529','Fabricación de otras cisternas, grandes depósitos y contenedores de metal'),
(2530,'2530','Fabricación de generadores de vapor, excepto calderas de calefacción central'),
(2540,'2540','Fabricación de armas y municiones'),
(2550,'2550','Forja, estampación y embutición de metales'),
(2561,'2561','Tratamiento y revestimiento de metales'),
(2562,'2562','Ingeniería mecánica por cuenta de terceros'),
(2571,'2571','Fabricación de artículos de cuchillería y cubertería'),
(2572,'2572','Fabricación de cerraduras y herrajes'),
(2573,'2573','Fabricación de herramientas'),
(2591,'2591','Fabricación de bidones y toneles de hierro o acero'),
(2592,'2592','Fabricación de envases y embalajes metálicos ligeros'),
(2593,'2593','Fabricación de productos de alambre, cadenas y muelles'),
(2594,'2594','Fabricación de pernos y productos de tornillería'),
(2599,'2599','Fabricación de otros productos metálicos n.c.o.p.'),
(2611,'2611','Fabricación de componentes electrónicos'),
(2612,'2612','Fabricación de circuitos impresos ensamblados'),
(2620,'2620','Fabricación de ordenadores y equipos periféricos'),
(2630,'2630','Fabricación de equipos de telecomunicaciones'),
(2640,'2640','Fabricación de productos electrónicos de consumo'),
(2651,'2651','Fabricación de instrumentos y aparatos de medida, verificación y navegación'),
(2652,'2652','Fabricación de relojes'),
(2660,'2660','Fabricación de equipos de radiación, electromédicos y electroterapéuticos'),
(2670,'2670','Fabricación de instrumentos de óptica y equipo fotográfico'),
(2680,'2680','Fabricación de soportes magnéticos y ópticos'),
(2711,'2711','Fabricación de motores, generadores y transformadores eléctricos'),
(2712,'2712','Fabricación de aparatos de distribución y control eléctrico'),
(2720,'2720','Fabricación de pilas y acumuladores eléctricos'),
(2731,'2731','Fabricación de cables de fibra óptica'),
(2732,'2732','Fabricación de otros hilos y cables electrónicos y eléctricos'),
(2733,'2733','Fabricación de dispositivos de cableado'),
(2740,'2740','Fabricación de lámparas y aparatos eléctricos de iluminación'),
(2751,'2751','Fabricación de electrodomésticos'),
(2752,'2752','Fabricación de aparatos domésticos no eléctricos'),
(2790,'2790','Fabricación de otro material y equipo eléctrico'),
(2811,'2811','Fabricación de motores y turbinas, excepto los destinados a aeronaves, vehículos automóviles y ciclomotores'),
(2812,'2812','Fabricación de equipos de transmisión hidráulica y neumática'),
(2813,'2813','Fabricación de otras bombas y compresores'),
(2814,'2814','Fabricación de otra grifería y válvulas'),
(2815,'2815','Fabricación de cojinetes, engranajes y órganos mecánicos de transmisión'),
(2821,'2821','Fabricación de hornos y quemadores'),
(2822,'2822','Fabricación de maquinaria de elevación y manipulación'),
(2823,'2823','Fabricación de máquinas y equipos de oficina, excepto equipos informáticos'),
(2824,'2824','Fabricación de herramientas eléctricas manuales'),
(2825,'2825','Fabricación de maquinaria de ventilación y refrigeración no doméstica'),
(2829,'2829','Fabricación de otra maquinaria de uso general n.c.o.p.'),
(2830,'2830','Fabricación de maquinaria agraria y forestal'),
(2841,'2841','Fabricación de máquinas herramienta para trabajar el metal'),
(2849,'2849','Fabricación de otras máquinas herramienta'),
(2891,'2891','Fabricación de maquinaria para la industria metalúrgica'),
(2892,'2892','Fabricación de maquinaria para las industrias extractivas y de la construcción'),
(2893,'2893','Fabricación de maquinaria para la industria de la alimentación, bebidas y tabaco'),
(2894,'2894','Fabricación de maquinaria para las industrias textil, de la confección y del cuero'),
(2895,'2895','Fabricación de maquinaria para la industria del papel y del cartón'),
(2896,'2896','Fabricación de maquinaria para la industria del plástico y el caucho'),
(2899,'2899','Fabricación de otra maquinaria para usos específicos n.c.o.p.'),
(2910,'2910','Fabricación de vehículos de motor'),
(2920,'2920','Fabricación de carrocerías para vehículos de motor'),
(2931,'2931','Fabricación de equipos eléctricos y electrónicos para vehículos de motor'),
(2932,'2932','Fabricación de otros componentes, piezas y accesorios para vehículos de motor'),
(3011,'3011','Construcción de barcos y estructuras flotantes'),
(3012,'3012','Construcción de embarcaciones de recreo y deporte'),
(3020,'3020','Fabricación de locomotoras y material ferroviario'),
(3030,'3030','Construcción aeronáutica y espacial y su maquinaria'),
(3040,'3040','Fabricación de vehículos militares de combate'),
(3091,'3091','Fabricación de motocicletas'),
(3092,'3092','Fabricación de bicicletas y de vehículos para personas con discapacidad'),
(3099,'3099','Fabricación de otro material de transporte n.c.o.p.'),
(3101,'3101','Fabricación de muebles de oficina y de establecimientos comerciales'),
(3102,'3102','Fabricación de muebles de cocina'),
(3103,'3103','Fabricación de colchones'),
(3109,'3109','Fabricación de otros muebles'),
(3211,'3211','Fabricación de monedas'),
(3212,'3212','Fabricación de artículos de joyería y artículos similares'),
(3213,'3213','Fabricación de artículos de bisutería y artículos similares'),
(3220,'3220','Fabricación de instrumentos musicales'),
(3230,'3230','Fabricación de artículos de deporte'),
(3240,'3240','Fabricación de juegos y juguetes'),
(3250,'3250','Fabricación de instrumentos y suministros médicos y odontológicos'),
(3291,'3291','Fabricación de escobas, brochas y cepillos'),
(3299,'3299','Otras industrias manufactureras n.c.o.p.'),
(3311,'3311','Reparación de productos metálicos'),
(3312,'3312','Reparación de maquinaria'),
(3313,'3313','Reparación de equipos electrónicos y ópticos'),
(3314,'3314','Reparación de equipos eléctricos'),
(3315,'3315','Reparación y mantenimiento naval'),
(3316,'3316','Reparación y mantenimiento aeronáutico y espacial'),
(3317,'3317','Reparación y mantenimiento de otro material de transporte'),
(3319,'3319','Reparación de otros equipos'),
(3320,'3320','Instalación de máquinas y equipos industriales'),
(3512,'3512','Transporte de energía eléctrica'),
(3513,'3513','Distribución de energía eléctrica'),
(3514,'3514','Comercio de energía eléctrica'),
(3515,'3515','Producción de energía hidroeléctrica'),
(3516,'3516','Producción de energía eléctrica de origen térmico convencional'),
(3517,'3517','Producción de energía eléctrica de origen nuclear'),
(3518,'3518','Producción de energía eléctrica de origen eólico'),
(3519,'3519','Producción de energía eléctrica de otros tipos'),
(3521,'3521','Producción de gas'),
(3522,'3522','Distribución por tubería de combustibles gaseosos'),
(3523,'3523','Comercio de gas por tubería'),
(3530,'3530','Suministro de vapor y aire acondicionado'),
(3600,'3600','Captación, depuración y distribución de agua'),
(3700,'3700','Recogida y tratamiento de aguas residuales'),
(3811,'3811','Recogida de residuos no peligrosos'),
(3812,'3812','Recogida de residuos peligrosos'),
(3821,'3821','Tratamiento y eliminación de residuos no peligrosos'),
(3822,'3822','Tratamiento y eliminación de residuos peligrosos'),
(3831,'3831','Separación y clasificación de materiales'),
(3832,'3832','Valorización de materiales ya clasificados'),
(3900,'3900','Actividades de descontaminación y otros servicios de gestión de residuos'),
(4110,'4110','Promoción inmobiliaria'),
(4121,'4121','Construcción de edificios residenciales'),
(4122,'4122','Construcción de edificios no residenciales'),
(4211,'4211','Construcción de carreteras y autopistas'),
(4212,'4212','Construcción de vías férreas de superficie y subterráneas'),
(4213,'4213','Construcción de puentes y túneles'),
(4221,'4221','Construcción de redes para fluidos'),
(4222,'4222','Construcción de redes eléctricas y de telecomunicaciones'),
(4291,'4291','Obras hidráulicas'),
(4299,'4299','Construcción de otros proyectos de ingeniería civil n.c.o.p.'),
(4311,'4311','Demolición'),
(4312,'4312','Preparación de terrenos'),
(4313,'4313','Perforaciones y sondeos'),
(4321,'4321','Instalaciones eléctricas'),
(4322,'4322','Fontanería, instalaciones de sistemas de calefacción y aire acondicionado'),
(4329,'4329','Otras instalaciones en obras de construcción'),
(4331,'4331','Revocamiento'),
(4332,'4332','Instalación de carpintería'),
(4333,'4333','Revestimiento de suelos y paredes'),
(4334,'4334','Pintura y acristalamiento'),
(4339,'4339','Otro acabado de edificios'),
(4391,'4391','Construcción de cubiertas'),
(4399,'4399','Otras actividades de construcción especializada n.c.o.p.'),
(4511,'4511','Venta de automóviles y vehículos de motor ligeros'),
(4519,'4519','Venta de otros vehículos de motor'),
(4520,'4520','Mantenimiento y reparación de vehículos de motor'),
(4531,'4531','Comercio al por mayor de repuestos y accesorios de vehículos de motor'),
(4532,'4532','Comercio al por menor de repuestos y accesorios de vehículos de motor'),
(4540,'4540','Venta, mantenimiento y reparación de motocicletas y de sus repuestos y accesorios'),
(4611,'4611','Intermediarios del comercio de materias primas agrarias, animales vivos, materias primas textiles y productos semielaborados'),
(4612,'4612','Intermediarios del comercio de combustibles, minerales, metales y productos químicos industriales'),
(4613,'4613','Intermediarios del comercio de la madera y materiales de construcción'),
(4614,'4614','Intermediarios del comercio de maquinaria, equipo industrial, embarcaciones y aeronaves'),
(4615,'4615','Intermediarios del comercio de muebles, artículos para el hogar y ferretería'),
(4616,'4616','Intermediarios del comercio de textiles, prendas de vestir, peletería, calzado y artículos de cuero'),
(4617,'4617','Intermediarios del comercio de productos alimenticios, bebidas y tabaco'),
(4618,'4618','Intermediarios del comercio especializados en la venta de otros productos específicos'),
(4619,'4619','Intermediarios del comercio de productos diversos'),
(4621,'4621','Comercio al por mayor de cereales, tabaco en rama, simientes y alimentos para animales'),
(4622,'4622','Comercio al por mayor de flores y plantas'),
(4623,'4623','Comercio al por mayor de animales vivos'),
(4624,'4624','Comercio al por mayor de cueros y pieles'),
(4631,'4631','Comercio al por mayor de frutas y hortalizas'),
(4632,'4632','Comercio al por mayor de carne y productos cárnicos'),
(4633,'4633','Comercio al por mayor de productos lácteos, huevos, aceites y grasas comestibles'),
(4634,'4634','Comercio al por mayor de bebidas'),
(4635,'4635','Comercio al por mayor de productos del tabaco'),
(4636,'4636','Comercio al por mayor de azúcar, chocolate y confitería'),
(4637,'4637','Comercio al por mayor de café, té, cacao y especias'),
(4638,'4638','Comercio al por mayor de pescados y mariscos y otros productos alimenticios'),
(4639,'4639','Comercio al por mayor, no especializado, de productos alimenticios, bebidas y tabaco'),
(4641,'4641','Comercio al por mayor de textiles'),
(4642,'4642','Comercio al por mayor de prendas de vestir y calzado'),
(4643,'4643','Comercio al por mayor de aparatos electrodomésticos'),
(4644,'4644','Comercio al por mayor de porcelana, cristalería y artículos de limpieza'),
(4645,'4645','Comercio al por mayor de productos perfumería y cosmética'),
(4646,'4646','Comercio al por mayor de productos farmacéuticos'),
(4647,'4647','Comercio al por mayor de muebles, alfombras y aparatos de iluminación'),
(4648,'4648','Comercio al por mayor de artículos de relojería y joyería'),
(4649,'4649','Comercio al por mayor de otros artículos de uso doméstico'),
(4651,'4651','Comercio al por mayor de ordenadores, equipos periféricos y programas informáticos'),
(4652,'4652','Comercio al por mayor de equipos electrónicos y de telecomunicaciones y sus componentes'),
(4661,'4661','Comercio al por mayor de maquinaria, equipos y suministros agrícolas'),
(4662,'4662','Comercio al por mayor de máquinas herramienta'),
(4663,'4663','Comercio al por mayor de maquinaria para la minería, la construcción y la ingeniería civil'),
(4664,'4664','Comercio al por mayor de maquinaria para la industria textil y de máquinas de coser y tricotar'),
(4665,'4665','Comercio al por mayor de muebles de oficina'),
(4666,'4666','Comercio al por mayor de otra maquinaria y equipo de oficina'),
(4669,'4669','Comercio al por mayor de otra maquinaria y equipo'),
(4671,'4671','Comercio al por mayor de combustibles sólidos, líquidos y gaseosos, y productos similares'),
(4672,'4672','Comercio al por mayor de metales y minerales metálicos'),
(4673,'4673','Comercio al por mayor de madera, materiales de construcción y aparatos sanitarios'),
(4674,'4674','Comercio al por mayor de ferretería, fontanería y calefacción'),
(4675,'4675','Comercio al por mayor de productos químicos'),
(4676,'4676','Comercio al por mayor de otros productos semielaborados'),
(4677,'4677','Comercio al por mayor de chatarra y productos de desecho'),
(4690,'4690','Comercio al por mayor no especializado'),
(4711,'4711','Comercio al por menor en establecimientos no especializados, con predominio en productos alimenticios, bebidas y tabaco'),
(4719,'4719','Otro comercio al por menor en establecimientos no especializados'),
(4721,'4721','Comercio al por menor de frutas y hortalizas en establecimientos especializados'),
(4722,'4722','Comercio al por menor de carne y productos cárnicos en establecimientos especializados'),
(4723,'4723','Comercio al por menor de pescados y mariscos en establecimientos especializados'),
(4724,'4724','Comercio al por menor de pan y productos de panadería, confitería y pastelería en establecimientos especializados'),
(4725,'4725','Comercio al por menor de bebidas en establecimientos especializados'),
(4726,'4726','Comercio al por menor de productos de tabaco en establecimientos especializados'),
(4729,'4729','Otro comercio al por menor de productos alimenticios en establecimientos especializados'),
(4730,'4730','Comercio al por menor de combustible para la automoción en establecimientos especializados'),
(4741,'4741','Comercio al por menor de ordenadores, equipos periféricos y programas informáticos en establecimientos especializados'),
(4742,'4742','Comercio al por menor de equipos de telecomunicaciones en establecimientos especializados'),
(4743,'4743','Comercio al por menor de equipos de audio y vídeo en establecimientos especializados'),
(4751,'4751','Comercio al por menor de textiles en establecimientos especializados'),
(4752,'4752','Comercio al por menor de ferretería, pintura y vidrio en establecimientos especializados'),
(4753,'4753','Comercio al por menor de alfombras, moquetas y revestimientos de paredes y suelos en establecimientos especializados'),
(4754,'4754','Comercio al por menor de aparatos electrodomésticos en establecimientos especializados'),
(4759,'4759','Comercio al por menor de muebles, aparatos de iluminación y otros artículos de uso doméstico en establecimientos especializados'),
(4761,'4761','Comercio al por menor de libros en establecimientos especializados'),
(4762,'4762','Comercio al por menor de periódicos y artículos de papelería en establecimientos especializados'),
(4763,'4763','Comercio al por menor de grabaciones de música y vídeo en establecimientos especializados'),
(4764,'4764','Comercio al por menor de artículos deportivos en establecimientos especializados'),
(4765,'4765','Comercio al por menor de juegos y juguetes en establecimientos especializados'),
(4771,'4771','Comercio al por menor de prendas de vestir en establecimientos especializados'),
(4772,'4772','Comercio al por menor de calzado y artículos de cuero en establecimientos especializados'),
(4773,'4773','Comercio al por menor de productos farmacéuticos en establecimientos especializados'),
(4774,'4774','Comercio al por menor de artículos médicos y ortopédicos en establecimientos especializados'),
(4775,'4775','Comercio al por menor de productos cosméticos e higiénicos en establecimientos especializados'),
(4776,'4776','Comercio al por menor de flores, plantas, semillas, fertilizantes, animales de compañía y alimentos para los mismos en establecimientos especializados'),
(4777,'4777','Comercio al por menor de artículos de relojería y joyería en establecimientos especializados'),
(4778,'4778','Otro comercio al por menor de artículos nuevos en establecimientos especializados'),
(4779,'4779','Comercio al por menor de artículos de segunda mano en establecimientos'),
(4781,'4781','Comercio al por menor de productos alimenticios, bebidas y tabaco en puestos de venta y en mercadillos'),
(4782,'4782','Comercio al por menor de productos textiles, prendas de vestir y calzado en puestos de venta y en mercadillos'),
(4789,'4789','Comercio al por menor de otros productos en puestos de venta y en mercadillos'),
(4791,'4791','Comercio al por menor por correspondencia o Internet'),
(4799,'4799','Otro comercio al por menor no realizado ni en establecimientos, ni en puestos de venta ni en mercadillos'),
(4910,'4910','Transporte interurbano de pasajeros por ferrocarril'),
(4920,'4920','Transporte de mercancías por ferrocarril'),
(4931,'4931','Transporte terrestre urbano y suburbano de pasajeros'),
(4932,'4932','Transporte por taxi'),
(4939,'4939','tipos de transporte terrestre de pasajeros n.c.o.p.'),
(4941,'4941','Transporte de mercancías por carretera'),
(4942,'4942','Servicios de mudanza'),
(4950,'4950','Transporte por tubería'),
(5010,'5010','Transporte marítimo de pasajeros'),
(5020,'5020','Transporte marítimo de mercancías'),
(5030,'5030','Transporte de pasajeros por vías navegables interiores'),
(5040,'5040','Transporte de mercancías por vías navegables interiores'),
(5110,'5110','Transporte aéreo de pasajeros'),
(5121,'5121','Transporte aéreo de mercancías'),
(5122,'5122','Transporte espacial'),
(5210,'5210','Depósito y almacenamiento'),
(5221,'5221','Actividades anexas al transporte terrestre'),
(5222,'5222','Actividades anexas al transporte marítimo y por vías navegables interiores'),
(5223,'5223','Actividades anexas al transporte aéreo'),
(5224,'5224','Manipulación de mercancías'),
(5229,'5229','Otras actividades anexas al transporte'),
(5310,'5310','Actividades postales sometidas a la obligación del servicio universal'),
(5320,'5320','Otras actividades postales y de correos'),
(5510,'5510','Hoteles y alojamientos similares'),
(5520,'5520','Alojamientos turísticos y otros alojamientos de corta estancia'),
(5530,'5530','Campings y aparcamientos para caravanas'),
(5590,'5590','Otros alojamientos'),
(5610,'5610','Restaurantes y puestos de comidas'),
(5621,'5621','Provisión de comidas preparadas para eventos'),
(5629,'5629','Otros servicios de comidas'),
(5630,'5630','Establecimientos de bebidas'),
(5811,'5811','Edición de libros'),
(5812,'5812','Edición de directorios y guías de direcciones postales'),
(5813,'5813','Edición de periódicos'),
(5814,'5814','Edición de revistas'),
(5819,'5819','Otras actividades editoriales'),
(5821,'5821','Edición de videojuegos'),
(5829,'5829','Edición de otros programas informáticos'),
(5912,'5912','Actividades de postproducción cinematográfica, de vídeo y de programas de televisión'),
(5914,'5914','Actividades de exhibición cinematográfica'),
(5915,'5915','Actividades de producción cinematográfica y de vídeo'),
(5916,'5916','Actividades de producciones de programas de televisión'),
(5917,'5917','Actividades de distribución cinematográfica y de vídeo'),
(5918,'5918','Actividades de distribución de programas de televisión'),
(5920,'5920','Actividades de grabación de sonido y edición musical'),
(6010,'6010','Actividades de radiodifusión'),
(6020,'6020','Actividades de programación y emisión de televisión'),
(6110,'6110','Telecomunicaciones por cable'),
(6120,'6120','Telecomunicaciones inalámbricas'),
(6130,'6130','Telecomunicaciones por satélite'),
(6190,'6190','Otras actividades de telecomunicaciones'),
(6201,'6201','Actividades de programación informática'),
(6202,'6202','Actividades de consultoría informática'),
(6203,'6203','Gestión de recursos informáticos'),
(6209,'6209','Otros servicios relacionados con las tecnologías de la información y la informática'),
(6311,'6311','Proceso de datos, hosting y actividades relacionadas'),
(6312,'6312','Portales web'),
(6391,'6391','Actividades de las agencias de noticias'),
(6399,'6399','Otros servicios de información n.c.o.p.'),
(6411,'6411','Banco central'),
(6419,'6419','Otra intermediación monetaria'),
(6420,'6420','Actividades de las sociedades holding'),
(6430,'6430','Inversión colectiva, fondos y entidades financieras similares'),
(6491,'6491','Arrendamiento financiero'),
(6492,'6492','Otras actividades crediticias'),
(6499,'6499','Otros servicios financieros, excepto seguros y fondos de pensiones n.c.o.p.'),
(6511,'6511','Seguros de vida'),
(6512,'6512','Seguros distintos de los seguros de vida'),
(6520,'6520','Reaseguros'),
(6530,'6530','Fondos de pensiones'),
(6611,'6611','Administración de mercados financieros'),
(6612,'6612','Actividades de intermediación en operaciones con valores y otros activos'),
(6619,'6619','Otras actividades auxiliares a los servicios financieros, excepto seguros y fondos de pensiones'),
(6621,'6621','Evaluación de riesgos y daños'),
(6622,'6622','Actividades de agentes y corredores de seguros'),
(6629,'6629','Otras actividades auxiliares a seguros y fondos de pensiones'),
(6630,'6630','Actividades de gestión de fondos'),
(6810,'6810','Compraventa de bienes inmobiliarios por cuenta propia'),
(6820,'6820','Alquiler de bienes inmobiliarios por cuenta propia'),
(6831,'6831','Agentes de la propiedad inmobiliaria'),
(6832,'6832','Gestión y administración de la propiedad inmobiliaria'),
(6910,'6910','Actividades jurídicas'),
(6920,'6920','Actividades de contabilidad, teneduría de libros, auditoría y asesoría fiscal'),
(7010,'7010','Actividades de las sedes centrales'),
(7021,'7021','Relaciones públicas y comunicación'),
(7022,'7022','Otras actividades de consultoría de gestión empresarial'),
(7111,'7111','Servicios técnicos de arquitectura'),
(7112,'7112','Servicios técnicos de ingeniería y otras actividades relacionadas con el asesoramiento técnico'),
(7120,'7120','Ensayos y análisis técnicos'),
(7211,'7211','Investigación y desarrollo experimental en biotecnología'),
(7219,'7219','Otra investigación y desarrollo experimental en ciencias naturales y técnicas'),
(7220,'7220','Investigación y desarrollo experimental en ciencias sociales y humanidades'),
(7311,'7311','Agencias de publicidad'),
(7312,'7312','Servicios de representación de medios de comunicación'),
(7320,'7320','Estudio de mercado y realización de encuestas de opinión pública'),
(7410,'7410','Actividades de diseño especializado'),
(7420,'7420','Actividades de fotografía'),
(7430,'7430','Actividades de traducción e interpretación'),
(7490,'7490','Otras actividades profesionales, científicas y técnicas n.c.o.p.'),
(7500,'7500','Actividades veterinarias'),
(7711,'7711','Alquiler de automóviles y vehículos de motor ligeros'),
(7712,'7712','Alquiler de camiones'),
(7721,'7721','Alquiler de artículos de ocio y deportivos'),
(7722,'7722','Alquiler de cintas de vídeo y discos'),
(7729,'7729','Alquiler de otros efectos personales y artículos de uso doméstico'),
(7731,'7731','Alquiler de maquinaria y equipo de uso agrícola'),
(7732,'7732','Alquiler de maquinaria y equipo para la construcción e ingeniería civil'),
(7733,'7733','Alquiler de maquinaria y equipo de oficina, incluidos ordenadores'),
(7734,'7734','Alquiler de medios de navegación'),
(7735,'7735','Alquiler de medios de transporte aéreo'),
(7739,'7739','Alquiler de otra maquinaria, equipos y bienes tangibles n.c.o.p.'),
(7740,'7740','Arrendamiento de la propiedad intelectual y productos similares, excepto trabajos protegidos por los derechos de autor'),
(7810,'7810','Actividades de las agencias de colocación'),
(7820,'7820','Actividades de las empresas de trabajo temporal'),
(7830,'7830','Otra provisión de recursos humanos'),
(7911,'7911','Actividades de las agencias de viajes'),
(7912,'7912','Actividades de los operadores turísticos'),
(7990,'7990','Otros servicios de reservas y actividades relacionadas con los mismos'),
(8010,'8010','Actividades de seguridad privada'),
(8020,'8020','Servicios de sistemas de seguridad'),
(8030,'8030','Actividades de investigación'),
(8110,'8110','Servicios integrales a edificios e instalaciones'),
(8121,'8121','Limpieza general de edificios'),
(8122,'8122','Otras actividades de limpieza industrial y de edificios'),
(8129,'8129','Otras actividades de limpieza'),
(8130,'8130','Actividades de jardinería'),
(8211,'8211','Servicios administrativos combinados'),
(8219,'8219','Actividades de fotocopiado, preparación de documentos y otras actividades especializadas de oficina'),
(8220,'8220','Actividades de los centros de llamadas'),
(8230,'8230','Organización de convenciones y ferias de muestras'),
(8291,'8291','Actividades de las agencias de cobros y de información comercial'),
(8292,'8292','Actividades de envasado y empaquetado'),
(8299,'8299','Otras actividades de apoyo a las empresas n.c.o.p.'),
(8411,'8411','Actividades generales de la Administración Pública'),
(8412,'8412','Regulación de las actividades sanitarias, educativas y culturales y otros servicios sociales, excepto Seguridad Social'),
(8413,'8413','Regulación de la actividad económica y contribución a su mayor eficiencia'),
(8421,'8421','Asuntos exteriores'),
(8422,'8422','Defensa'),
(8423,'8423','Justicia'),
(8424,'8424','Orden público y seguridad'),
(8425,'8425','Protección civil'),
(8430,'8430','Seguridad Social obligatoria'),
(8510,'8510','Educación preprimaria'),
(8520,'8520','Educación primaria'),
(8531,'8531','Educación secundaria general'),
(8532,'8532','Educación secundaria técnica y profesional'),
(8541,'8541','Educación postsecundaria no terciaria'),
(8543,'8543','Educación universitaria'),
(8544,'8544','Educación terciaria no universitaria'),
(8551,'8551','Educación deportiva y recreativa'),
(8552,'8552','Educación cultural'),
(8553,'8553','Actividades de las escuelas de conducción y pilotaje'),
(8559,'8559','Otra educación n.c.o.p.'),
(8560,'8560','Actividades auxiliares a la educación'),
(8610,'8610','Actividades hospitalarias'),
(8621,'8621','Actividades de medicina general'),
(8622,'8622','Actividades de medicina especializada'),
(8623,'8623','Actividades odontológicas'),
(8690,'8690','Otras actividades sanitarias'),
(8710,'8710','Asistencia en establecimientos residenciales con cuidados sanitarios'),
(8720,'8720','Asistencia en establecimientos residenciales para personas con discapacidad intelectual, enfermedad mental y drogodependencia'),
(8731,'8731','Asistencia en establecimientos residenciales para personas mayores'),
(8732,'8732','Asistencia en establecimientos residenciales para personas con discapacidad física'),
(8790,'8790','Otras actividades de asistencia en establecimientos residenciales'),
(8811,'8811','Actividades de servicios sociales sin alojamiento para personas mayores'),
(8812,'8812','Actividades de servicios sociales sin alojamiento para personas con discapacidad'),
(8891,'8891','Actividades de cuidado diurno de niños'),
(8899,'8899','Otros actividades de servicios sociales sin alojamiento n.c.o.p.'),
(9001,'9001','Artes escénicas'),
(9002,'9002','Actividades auxiliares a las artes escénicas'),
(9003,'9003','Creación artística y literaria'),
(9004,'9004','Gestión de salas de espectáculos'),
(9102,'9102','Actividades de museos'),
(9103,'9103','Gestión de lugares y edificios históricos'),
(9104,'9104','Actividades de los jardines botánicos, parques zoológicos y reservas naturales'),
(9105,'9105','Actividades de bibliotecas'),
(9106,'9106','Actividades de archivos'),
(9200,'9200','Actividades de juegos de azar y apuestas'),
(9311,'9311','Gestión de instalaciones deportivas'),
(9312,'9312','Actividades de los clubes deportivos'),
(9313,'9313','Actividades de los gimnasios'),
(9319,'9319','Otras actividades deportivas'),
(9321,'9321','Actividades de los parques de atracciones y los parques temáticos'),
(9329,'9329','Otras actividades recreativas y de entretenimiento'),
(9411,'9411','Actividades de organizaciones empresariales y patronales'),
(9412,'9412','Actividades de organizaciones profesionales'),
(9420,'9420','Actividades sindicales'),
(9491,'9491','Actividades de organizaciones religiosas'),
(9492,'9492','Actividades de organizaciones políticas'),
(9499,'9499','Otras actividades asociativas n.c.o.p.'),
(9511,'9511','Reparación de ordenadores y equipos periféricos'),
(9512,'9512','Reparación de equipos de comunicación'),
(9521,'9521','Reparación de aparatos electrónicos de audio y vídeo de uso doméstico'),
(9522,'9522','Reparación de aparatos electrodomésticos y de equipos para el hogar y el jardín'),
(9523,'9523','Reparación de calzado y artículos de cuero'),
(9524,'9524','Reparación de muebles y artículos de menaje'),
(9525,'9525','Reparación de relojes y joyería'),
(9529,'9529','Reparación de otros efectos personales y artículos de uso doméstico'),
(9601,'9601','Lavado y limpieza de prendas textiles y de piel'),
(9602,'9602','Peluquería y otros tratamientos de belleza'),
(9603,'9603','Pompas fúnebres y actividades relacionadas'),
(9604,'9604','Actividades de mantenimiento físico'),
(9609,'9609','Otras servicios personales n.c.o.p.'),
(9700,'9700','Actividades de los hogares como empleadores de personal doméstico'),
(9810,'9810','Actividades de los hogares como productores de bienes para uso propio'),
(9820,'9820','Actividades de los hogares como productores de servicios para uso propio'),
(9900,'9900','Actividades de organizaciones y organismos extraterritoriales');

ALTER TABLE `user` DROP COLUMN `status`;

ALTER TABLE `user` DROP COLUMN `aon_key`;

ALTER TABLE `user` DROP COLUMN `validate`;

ALTER TABLE `user` DROP COLUMN `available`;

ALTER TABLE `user` ADD `enterprise` int(4) default NULL COMMENT 'Identificador de la Empresa';

ALTER TABLE `user` ADD KEY `IDX_USER_ENTERPRISE` (`enterprise`);

ALTER TABLE `user` ADD CONSTRAINT `FK_USER_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`);

ALTER TABLE `user` ADD `registry` int(4) COMMENT 'Identificador del Registry';

ALTER TABLE `user` ADD `active` tinyint(1) NOT NULL default '1' COMMENT 'Indica si el Usuario esta activo o no';

ALTER TABLE `user` ADD `password` varchar(128) default NULL COMMENT 'Contraseña del Usuario';

UPDATE `db_version` SET `version_number` = '6.0.0';

COMMIT;
