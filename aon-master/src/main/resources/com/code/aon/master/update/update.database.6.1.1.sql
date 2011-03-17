# Database: aon_master
# Version: Actualizacion de la version 6.1.0 a la version 6.1.1.
# Created by: rtrepiana
# Creation Date: 25/02/2011 10:36
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `function_constant` RENAME `system_data`;

ALTER TABLE `contract_leave` ADD `daily_reg_base` double(15,3) DEFAULT NULL COMMENT 'Base reguladora';

ALTER TABLE `payment_concept` ADD `description_decorable` tinyint(2) NOT NULL DEFAULT '0' COMMENT '';
ALTER TABLE `payment_concept` ADD `expression` varchar(128) collate latin1_spanish_ci DEFAULT NULL COMMENT 'Importe';
ALTER TABLE `payment_concept` ADD `irpf_expression` varchar(128) collate latin1_spanish_ci DEFAULT NULL COMMENT 'Importe tributable';
ALTER TABLE `payment_concept` ADD `quote_expression` varchar(128) collate latin1_spanish_ci DEFAULT NULL COMMENT 'Importe cotizable';

ALTER TABLE `deduction_concept` ADD `description_decorable` tinyint(2) default '0' COMMENT '';
ALTER TABLE `deduction_concept` ADD `expression` varchar(128) collate latin1_spanish_ci DEFAULT NULL COMMENT 'Importe';

UPDATE `deduction_concept` SET description='@{CGC/BASE_CGC*100} %', expression='BASE_CGC * 4.70/100', description_decorable='1' WHERE id = 1; 
UPDATE `deduction_concept` SET description='@{DESMP/BASE_CGP*100} %', expression='BASE_CGP * (INDEFINIDO ? 1.55 : 1.60 )/100' , description_decorable='1' WHERE id = 3; 
UPDATE `deduction_concept` SET description='@{FP/BASE_CGP*100} %', expression='BASE_CGP * 0.10/100', description_decorable='1' WHERE id = 4; 
UPDATE `deduction_concept` SET description='@{ESTR/BASE_ESTR*100} %', expression='BASE_ESTR * 2.00/100', description_decorable='1' WHERE id = 5; 
UPDATE `deduction_concept` SET description='@{NESTR/BASE_NESTR*100} %', expression='BASE_NESTR * 4.70/100', description_decorable='1' WHERE id = 6; 
UPDATE `deduction_concept` SET description='@{IRPF/BASE_IRPF*100} %', expression='BASE_IRPF * PORCENTAJE_IRPF/100', description_decorable='1' WHERE id = 7; 

ALTER TABLE `contract_payment` MODIFY `description_decorable` tinyint(2) NOT NULL DEFAULT '0' COMMENT '';
ALTER TABLE `contract_deduction` MODIFY `description_decorable` tinyint(2) NOT NULL DEFAULT '0' COMMENT '';
ALTER TABLE `system_deduction` MODIFY `description_decorable` tinyint(2) NOT NULL DEFAULT '0' COMMENT '';

UPDATE `system_deduction` SET description=NULL, expression=NULL, description_decorable=1;

INSERT INTO `payment_concept` (id, code, type, expression, quote_expression, irpf_expression, description, description_decorable) VALUES 
  (1,'ITEMP',7,'IT.DIAS(4,16) * IT.BASE_REG * 0.60','ITEMP','ITEMP','PREST. IT A CARGO DE LA EMPRESA',1),
  (2,'ITSS',7,'IT.DIAS(17,20) * IT.BASE_REG * 0.60 + IT.DIAS(21) * IT.BASE_REG * 0.75','ITSS','ITSS6','PREST. IT A CARGO DEL INSS',1),
  (3,'MTNAD',7,'0','MTNDAD.DIAS * MTNDAD.BR','0','PREST. MATERNIDAD Y/O R.E',1),
  (4,'ATEP',7,'ATEP.DIAS * ATEP.BASE_REG * 0.75','ATEP','ATEP','PREST. A.T. y E.P. ',1);

ALTER TABLE `agreement_level_payment` ADD `start_date` date NOT NULL COMMENT 'Fecha de inicio';
ALTER TABLE `agreement_level_payment` ADD `end_date` date default NULL COMMENT 'Fecha de finalizacion';
ALTER TABLE `agreement_level_payment` ADD `month` tinyint(2) default null COMMENT 'Mes de la percepcion';
ALTER TABLE `agreement_level_payment` ADD `payment_concept` int(4) COMMENT 'Identificador unico del concepto';
ALTER TABLE `agreement_level_payment` ADD `salary_type` tinyint(2) COMMENT 'Tipo de Nomina/Recibo';
ALTER TABLE `agreement_level_payment` ADD `description_decorable` tinyint(2) NOT NULL DEFAULT '0' COMMENT '';
ALTER TABLE `agreement_level_payment` ADD `irpf_expression` varchar(128) collate latin1_spanish_ci DEFAULT NULL COMMENT 'Importe tributable';
ALTER TABLE `agreement_level_payment` ADD `quote_expression` varchar(128) collate latin1_spanish_ci DEFAULT NULL COMMENT 'Importe cotizable';
ALTER TABLE `agreement_level_payment` ADD KEY `IDX_AGREEMENT_LEVEL_PAYMENT_PAYMENT_CONCEPT` (`payment_concept`);
ALTER TABLE `agreement_level_payment` ADD CONSTRAINT `FK_AGREEMENT_LEVEL_PAYMENT_PAYMENT_CONCEPT` FOREIGN KEY (`payment_concept`) REFERENCES `payment_concept` (`id`);

CREATE TABLE `agreement_level_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Nombre',
  `agreement_level` int(4) NOT NULL COMMENT 'Nivel retributivo',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  KEY `IDX_AGREEMENT_LEVEL_DATA_AGREEMENT_LEVEL` (`agreement_level`),
  CONSTRAINT `FK_AGREEMENT_LEVEL_DATA_AGREEMENT_LEVEL` FOREIGN KEY (`agreement_level`) REFERENCES `agreement_level` (`id`),
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto del convenio';

CREATE TABLE `system_payment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `type` tinyint(2) COMMENT 'Tipo de Percepción Salarial',
  `payment_concept` int(4) COMMENT 'Identificador unico del concepto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `description_decorable` tinyint(2) NOT NULL default '0' COMMENT '',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe',
  `irpf_expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe tributable',
  `quote_expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe cotizable',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio',
  `month` tinyint(2) default null COMMENT 'Mes de la percepcion',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `salary_type` tinyint(2) COMMENT 'Tipo de Nomina/Recibo',
  PRIMARY KEY  (`id`),
  KEY `IDX_SYSTEM_PAYMENT_PAYMENT_CONCEPT` (`payment_concept`),
  CONSTRAINT `FK_SYSTEM_PAYMENT_PAYMENT_CONCEPT` FOREIGN KEY (`payment_concept`) REFERENCES `payment_concept` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Percepciones Salariales';

INSERT INTO `system_payment` (payment_concept, type, expression, irpf_expression, quote_expression,  description, description_decorable, start_date, end_date, month,  salary_type) VALUES 
  (1,NULL,NULL,NULL,NULL, NULL,0,'2010-01-01',NULL,NULL,0),
  (2,NULL,NULL,NULL,NULL, NULL,0,'2010-01-01',NULL,NULL,0),
  (3,NULL,NULL,NULL,NULL, NULL,0,'2010-01-01',NULL,NULL,0),
  (4,NULL,NULL,NULL,NULL, NULL,0,'2010-01-01',NULL,NULL,0);


UPDATE `db_version` SET `version_number` = '6.2.0';

COMMIT;
