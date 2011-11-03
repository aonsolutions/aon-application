# Database : aon_master
# Version: 6.18.7
# Created by: girazu
# Creation Date: 03/11/2011 12:12


SET FOREIGN_KEY_CHECKS=0;

CREATE DATABASE `aon_master`
    CHARACTER SET 'latin1'
    COLLATE 'latin1_spanish_ci';

USE `aon_master`;

#
# Structure for the `course_subject` table : 
#

CREATE TABLE `course_subject` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Materia',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Materia',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Materias de Cursos';

#
# Structure for the `course_level` table : 
#

CREATE TABLE `course_level` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Nivel',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Nivel',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Niveles de Cursos';

#
# Structure for the `academic_year` table : 
#

CREATE TABLE `academic_year` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Año Academico',
  `description` varchar(9) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Año Academico',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Año Academico';

#
# Structure for the `holiday` table : 
#

CREATE TABLE `holiday` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de la Festividad',
  `holiday` int(4) default NULL COMMENT 'Identificador de Festividad',
  `editable` tinyint(1) default '0' COMMENT 'Indica si es editable o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_HOLIDAY_HOLIDAY` (`holiday`),
  CONSTRAINT `FK_HOLIDAY_HOLIDAY` FOREIGN KEY (`holiday`) REFERENCES `holiday` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Festividades';

#
# Structure for the `calendar` table : 
#

CREATE TABLE `calendar` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `holiday` int(4) default NULL COMMENT 'Identificador de Festivos',
  `anual_hours` double default '0' COMMENT 'Horas anuales del Calendario',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Calendario',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios',
  `monday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `monday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `tuesday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `tuesday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `wednesday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `wednesday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `thursday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `thursday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `friday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `friday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `saturday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `saturday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `sunday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `sunday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `generic` tinyint(1) default '1' COMMENT 'Indica si es editable o no',
  `calendar` int(4) default NULL COMMENT 'Calendario del que se hereda',
  PRIMARY KEY  (`id`),
  KEY `IDX_CALENDAR_HOLIDAY` (`holiday`),
  KEY `IDX_CALENDAR_CALENDAR` (`calendar`),
  CONSTRAINT `FK_CALENDAR_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_CALENDAR_HOLIDAY` FOREIGN KEY (`holiday`) REFERENCES `holiday` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Calendarios Laborales';

#
# Structure for the `registry` table : 
#

CREATE TABLE `registry` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Persona o Empresa',
  `document` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Numero de Documento de la Persona o Empresa',
  `document_type` tinyint(2) default '0' COMMENT 'Tipo de documento (NIF, CIF...)',
  `document_country` varchar(2) collate latin1_spanish_ci default 'ES' COMMENT 'Pais del documento',
  `name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Nombre de la Persona o Empresa',
  `alias` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Alias de la Persona o Empresa',
  `type` tinyint(2) default NULL COMMENT 'Tipo (Persona o Empresa)',
  `nationality` varchar(2) collate latin1_spanish_ci default 'ES' COMMENT 'Nacionalidad',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad',
  PRIMARY KEY  (`id`),
  KEY `idx_rgty_name` (`name`),
  KEY `idx_rgty_document` (`document`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Registro de Personas o Empresas';

#
# Structure for the `scope` table : 
#

CREATE TABLE `scope` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `description` varchar(16) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Ambito',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ambitos';

#
# Structure for the `enterprise` table : 
#

CREATE TABLE `enterprise` (
  `registry` int(4) NOT NULL default '1' COMMENT 'Registro de la Empresa',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `calendar` int(4) default NULL COMMENT 'Calendario',
  PRIMARY KEY  (`registry`),
  KEY `IDX_ENTERPRISE_SCOPE` (`scope`),
  KEY `IDX_ENTERPRISE_CALENDAR` (`calendar`),
  CONSTRAINT `FK_ENTERPRISE_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_ENTERPRISE_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_ENTERPRISE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Empresa';

#
# Structure for the `geozone` table : 
#

CREATE TABLE `geozone` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Zona Geografica',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Zona Geografica',
  `code` varchar(3) collate latin1_spanish_ci default NULL COMMENT 'Codigo de la Zona Geografica',
  `system` tinyint(1) NOT NULL default '0' COMMENT 'Indica si es una Zona Geografica del sistema',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Zonas Geograficas';

#
# Structure for the `raddress` table : 
#

CREATE TABLE `raddress` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Direccion de la Persona o Empresa',
  `registry` int(4) NOT NULL default '0' COMMENT 'Identificador del Registro de la Persona o Empresa',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Direccion',
  `recipient` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Destinatario',
  `street_type` varchar(2) collate latin1_spanish_ci default 'CL' COMMENT 'Tipo de via',
  `address` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Primera parte de la Direccion',
  `number` varchar(12) collate latin1_spanish_ci default NULL COMMENT 'Numero',
  `address2` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Segunda parte de la Direccion',
  `address3` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Tercera parte de la Direccion',
  `zip` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo Postal',
  `city` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Localidad',
  `geozone` int(4) default NULL COMMENT 'Identificador de la Zona Geografica',
  `alias` varchar(15) collate latin1_spanish_ci default NULL COMMENT 'Alias',
  PRIMARY KEY  (`id`),
  KEY `idx_radr_rgty` (`registry`),
  KEY `idx_radr_gzne` (`geozone`),
  CONSTRAINT `raddress_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `raddress_ibfk_2` FOREIGN KEY (`geozone`) REFERENCES `geozone` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Direcciones de Personas o Empresas';

#
# Structure for the `workplace` table : 
#

CREATE TABLE `workplace` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Centro de Trabajo',
  `enterprise` int(4) NOT NULL default '1' COMMENT 'Empresa asociada al Centro de Trabajo',
  `description` varchar(64) NOT NULL COMMENT 'Descripcion del Centro de Trabajo',
  `address` int(4) NOT NULL COMMENT 'Identificador de la Direccion',
  `economicAgreement` tinyint(2) default '0' COMMENT 'Concierto Economico del Centro de Trabajo',
  `active` tinyint(1) default '1' COMMENT 'Indica si el Centro de Trabajo esta activo o no',
  PRIMARY KEY  (`id`),
  KEY `address` (`address`),
  KEY `IDX_WORKPLACE_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_WORKPLACE_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`),
  CONSTRAINT `workplace_ibfk_1` FOREIGN KEY (`address`) REFERENCES `raddress` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Centros de Trabajo';

#
# Structure for the `course` table : 
#

CREATE TABLE `course` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Curso',
  `code` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Alias del Curso',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Curso',
  `start_date` date NOT NULL COMMENT 'Fecha inicio del Curso',
  `end_date` date NOT NULL COMMENT 'Fecha fin del Curso',
  `academic_year` int(4) NOT NULL COMMENT 'Año Academico del Curso',
  `subject` int(4) NOT NULL COMMENT 'Materia del Curso',
  `level` int(4) NOT NULL COMMENT 'Nivel del Curso',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `alumn_limit` smallint(2) default NULL COMMENT 'Limite de Alumnos del Curso',
  `status` tinyint(2) default NULL COMMENT 'Estado del Curso',
  `comments` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Comentarios sobre el Curso',
  PRIMARY KEY  (`id`),
  KEY `subject` (`subject`),
  KEY `level` (`level`),
  KEY `academic_year` (`academic_year`),
  KEY `workplace` (`workplace`),
  CONSTRAINT `course_fk` FOREIGN KEY (`subject`) REFERENCES `course_subject` (`id`),
  CONSTRAINT `course_fk1` FOREIGN KEY (`level`) REFERENCES `course_level` (`id`),
  CONSTRAINT `course_fk2` FOREIGN KEY (`academic_year`) REFERENCES `academic_year` (`id`),
  CONSTRAINT `course_fk3` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cursos';

#
# Structure for the `customer_segment` table : 
#

CREATE TABLE `customer_segment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Segmento',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Segmento',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Segmentaciones de Clientes';

#
# Structure for the `tariff` table : 
#

CREATE TABLE `tariff` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Tarifa',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Tarifa',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas';

#
# Structure for the `customer` table : 
#

CREATE TABLE `customer` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro del Cliente',
  `tariff` int(4) default NULL COMMENT 'Tarifa asociada al Cliente',
  `taxfree` tinyint(1) default '0' COMMENT 'Indica si el Cliente esta exento de Impuestos',
  `surcharge` tinyint(1) default '0' COMMENT 'Indica si el Cliente tiene recargo de equivalencia',
  `withholding` tinyint(1) default '0' COMMENT 'Indica si el Cliente aplica retencion de impuestos',
  `transaction` tinyint(2) default '0' COMMENT 'Tipo de transacciones del Cliente',
  `status` tinyint(2) default NULL COMMENT 'Estado del Cliente',
  `segment` int(4) default NULL COMMENT 'Segmento del Cliente',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `e_invoice` tinyint(1) default '0' COMMENT 'Indica si el Cliente desea recibir Facturas electronicas',
  `delivery_grouped` tinyint(1) default '1' COMMENT 'Indica si el Cliente desea agrupar Albaranes en una sola Factura',
  `delivery_valuated` tinyint(1) default '1' COMMENT 'Indica si el Cliente desea imprimir el Albaran valorado',
  PRIMARY KEY  (`registry`),
  KEY `idx_ctmr_trff` (`tariff`),
  KEY `segment` (`segment`),
  KEY `scope` (`scope`),
  CONSTRAINT `customer_fk` FOREIGN KEY (`segment`) REFERENCES `customer_segment` (`id`),
  CONSTRAINT `customer_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `customer_ibfk_2` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`),
  CONSTRAINT `customer_ibfk_3` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Clientes';

#
# Structure for the `course_alumn` table : 
#

CREATE TABLE `course_alumn` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `course` int(4) NOT NULL COMMENT 'Identificador del Curso',
  `customer` int(4) NOT NULL COMMENT 'Identificador del Alumno',
  `status` tinyint(2) default NULL COMMENT 'Estado del alumno en el curso',
  PRIMARY KEY  (`id`),
  KEY `course` (`course`),
  KEY `customer` (`customer`),
  CONSTRAINT `course_alumns_fk` FOREIGN KEY (`course`) REFERENCES `course` (`id`),
  CONSTRAINT `course_alumns_fk1` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Alumnos por Curso';

#
# Structure for the `absence` table : 
#

CREATE TABLE `absence` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `course_alumn` int(4) NOT NULL COMMENT 'Identificador del CursoAlumno',
  `absence_date` date default NULL COMMENT 'Fecha de la Ausencia',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios de la Ausencia',
  `evaluation` tinyint(2) default NULL COMMENT 'Numero de Evaluacion en que se produjo la Ausencia',
  PRIMARY KEY  (`id`),
  KEY `course_alumn` (`course_alumn`),
  CONSTRAINT `absence_fk_1` FOREIGN KEY (`course_alumn`) REFERENCES `course_alumn` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ausencias';

#
# Structure for the `academic_skill` table : 
#

CREATE TABLE `academic_skill` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Aptitud Academica',
  `code` char(5) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo de la Aptitud Academica',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de la Aptitud Academica',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Aptitudes Academicas';

#
# Structure for the `account` table : 
#

CREATE TABLE `account` (
  `id` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador unico de la Cuenta',
  `description` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Cuenta',
  `alias` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Alias de la Cuenta',
  `entryEnabled` tinyint(2) default '0' COMMENT 'Indica si la Cuenta permite o no Apuntes',
  `level` tinyint(2) NOT NULL default '0' COMMENT 'Nivel de la Cuenta',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables';

#
# Structure for the `account_period` table : 
#

CREATE TABLE `account_period` (
  `id` char(4) collate latin1_spanish_ci NOT NULL COMMENT 'Código del Ejercicio',
  `initiation_date` date NOT NULL COMMENT 'Fecha de inicio del Ejercicio',
  `deadline` date NOT NULL COMMENT 'Fecha final del Ejercicio',
  `status` tinyint(2) default '0' COMMENT 'Estado del Ejercicio',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ejercicios Contables';

#
# Structure for the `account_entry` table : 
#

CREATE TABLE `account_entry` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Asiento',
  `account_period` char(4) collate latin1_spanish_ci NOT NULL COMMENT 'Ejercicio Contable del Asiento',
  `entry_date` date default NULL COMMENT 'Fecha del Asiento',
  `entry_type` tinyint(2) default NULL COMMENT 'Tipo de Asiento',
  `journal` int(4) default NULL COMMENT 'Numero de diario del Asiento',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Asiento',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios del Asiento',
  PRIMARY KEY  (`id`),
  KEY `account_entry_account_period_idx` (`account_period`),
  CONSTRAINT `account_entry_ibfk_1` FOREIGN KEY (`account_period`) REFERENCES `account_period` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Asientos Contables';

#
# Structure for the `bank` table : 
#

CREATE TABLE `bank` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Entidad Bancaria',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Entidad Bancaria',
  `code` varchar(4) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo de la Entidad Bancaria',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Entidades Bancarias';

#
# Structure for the `rbank` table : 
#

CREATE TABLE `rbank` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Cuenta Bancaria de la Persona o Empresa',
  `registry` int(4) NOT NULL default '0' COMMENT 'Identificador del Registro de la Persona o Empresa',
  `bank` int(4) NOT NULL default '0' COMMENT 'Identificador de la Entidad Bancaria',
  `bank_account` char(30) collate latin1_spanish_ci default NULL COMMENT 'Numero de Cuenta Bancaria de la Persona o Empresa',
  `sufix` char(3) collate latin1_spanish_ci default NULL COMMENT 'Sufijo de Cuenta Bancaria para Remesas',
  `alias` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Alias de la Cuenta Bancaria',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indica si la Cuenta Bancaria esta activa o no',
  PRIMARY KEY  (`id`),
  KEY `idx_rbnk_rgty` (`registry`),
  KEY `idx_rbnk_bank` (`bank`),
  CONSTRAINT `rbank_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `rbank_ibfk_2` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos de Cuentas Bancarias de Personas o Empresas';

#
# Structure for the `bank_statement` table : 
#

CREATE TABLE `bank_statement` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `rbank` int(4) NOT NULL COMMENT 'Identificador de Banco de la Compañia',
  `lot_number` int(4) NOT NULL default '0' COMMENT 'Numero de lote',
  `operation_date` date NOT NULL COMMENT 'Fecha de operacion',
  `common_concept` tinyint(2) NOT NULL default '0' COMMENT 'Concepto comun',
  `own_concept` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Concepto propio',
  `payment` tinyint(1) NOT NULL default '0' COMMENT 'Indica si es un pago',
  `amount` double(15,2) NOT NULL default '0.00' COMMENT 'Importe',
  `document` int(4) default '0' COMMENT 'Numero de documento',
  `reference1` varchar(12) collate latin1_spanish_ci default NULL COMMENT 'Referencia 1',
  `reference2` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Referencia 2',
  `description` varchar(80) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `reliability` tinyint(2) default '0' COMMENT 'Fiabilidad del punteo',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad',
  `status` tinyint(2) default '0' COMMENT 'Estado',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios',
  PRIMARY KEY  (`id`),
  KEY `IDX_BANK_STATEMENT_RBANK` (`rbank`),
  CONSTRAINT `FK_BANK_STATEMENT_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Extractos bancarios';

#
# Structure for the `account_entry_bank_statement` table : 
#

CREATE TABLE `account_entry_bank_statement` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `account_entry` int(4) NOT NULL COMMENT 'Identificador de Asiento',
  `bank_statement` int(4) NOT NULL COMMENT 'Identificador de Extracto bancario',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACC_ENTRY_BANK_STATEMENT_ACC_ENTRY` (`account_entry`),
  KEY `IDX_ACC_ENTRY_BANK_STATEMENT_BANK_STATEMENT` (`bank_statement`),
  CONSTRAINT `FK_ACC_ENTRY_BANK_STATEMENT_ACC_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_ACC_ENTRY_BANK_STATEMENT_BANK_STATEMENT` FOREIGN KEY (`bank_statement`) REFERENCES `bank_statement` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Asientos Contables y Extractos bancarios';

#
# Structure for the `account_entry_detail` table : 
#

CREATE TABLE `account_entry_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Apunte',
  `account_entry` int(4) NOT NULL COMMENT 'Identificador del Asiento',
  `line` int(4) unsigned NOT NULL COMMENT 'Numero de linea del Apunte dentro del Asiento',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta Contable del Apunte',
  `concept` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Concepto del Apunte',
  `balancing_account` char(12) collate latin1_spanish_ci default NULL COMMENT 'Contrapartida del Apunte',
  `debit` double default '0' COMMENT 'Debe del Apunte',
  `credit` double default '0' COMMENT 'Haber del Apunte',
  `document_number` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Numero de documento asociado',
  PRIMARY KEY  (`id`),
  KEY `account_entry_detail_FKIndex3` (`account`),
  KEY `account_entry_detail_FKIndex4` (`balancing_account`),
  KEY `account_entry` (`account_entry`),
  CONSTRAINT `account_entry_detail_ibfk_2` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `account_entry_detail_ibfk_3` FOREIGN KEY (`balancing_account`) REFERENCES `account` (`id`),
  CONSTRAINT `account_entry_detail_ibfk_4` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Apuntes Contables';

#
# Structure for the `bank_statement_link` table : 
#

CREATE TABLE `bank_statement_link` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `bank_statement` int(4) NOT NULL COMMENT 'Identificador de Extracto bancario',
  `source` tinyint(2) NOT NULL default '0' COMMENT 'Origen',
  `source_id` int(4) NOT NULL default '0' COMMENT 'Identificador del origen',
  `source_date` date default NULL COMMENT 'Fecha del origen',
  `amount` double(15,2) NOT NULL default '0.00' COMMENT 'Importe',
  `status` tinyint(2) default '0' COMMENT 'Estado',
  `linked_bank_statement_link` int(4) default NULL COMMENT 'Identificador del Enlace de Extracto bancario asociado',
  PRIMARY KEY  (`id`),
  KEY `IDX_BANK_STATEMENT_LINK_BANK_STATEMENT` (`bank_statement`),
  KEY `IDX_BANK_STATEMENT_LINK_BANK_STATEMENT_LINK` (`linked_bank_statement_link`),
  CONSTRAINT `FK_BANK_STATEMENT_LINK_BANK_STATEMENT` FOREIGN KEY (`bank_statement`) REFERENCES `bank_statement` (`id`),
  CONSTRAINT `FK_BANK_STATEMENT_LINK_BANK_STATEMENT_LINK` FOREIGN KEY (`linked_bank_statement_link`) REFERENCES `bank_statement_link` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Enlaces del Extracto bancario';

#
# Structure for the `fbatch` table : 
#

CREATE TABLE `fbatch` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Remesa',
  `description` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de la Remesa',
  `issue_date` date default NULL COMMENT 'Fecha de emision de la Remesa',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Remesa',
  `status` tinyint(2) default NULL COMMENT 'Estado de la Remesa',
  `rbank` int(4) default NULL COMMENT 'Banco de la Compañia utilizado en la Remesa',
  `bank_statement_link` int(4) default NULL COMMENT 'Identificador de la Linea del Extracto bancario',
  `payment` tinyint(1) NOT NULL default '0' COMMENT 'Indica si es un pago o un cobro',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad',
  PRIMARY KEY  (`id`),
  KEY `rbank` (`rbank`),
  KEY `IDX_FBATCH_BANK_STATEMENT_LINK` (`bank_statement_link`),
  CONSTRAINT `fbatch_fk_1` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`),
  CONSTRAINT `FK_FBATCH_BANK_STATEMENT_LINK` FOREIGN KEY (`bank_statement_link`) REFERENCES `bank_statement_link` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas';

#
# Structure for the `account_entry_fbatch` table : 
#

CREATE TABLE `account_entry_fbatch` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `account_entry` int(4) NOT NULL COMMENT 'Identificador de Asiento Contable',
  `fbatch` int(4) NOT NULL COMMENT 'Identificador de Remesa',
  PRIMARY KEY  (`id`),
  KEY `account_entry` (`account_entry`),
  KEY `fbatch` (`fbatch`),
  CONSTRAINT `account_entry_fbatch_fk` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `account_entry_fbatch_fk1` FOREIGN KEY (`fbatch`) REFERENCES `fbatch` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Asientos Contables y Remesas';

#
# Structure for the `pay_method` table : 
#

CREATE TABLE `pay_method` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Forma de Pago',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de Forma de Pago',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Forma de Pago',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Formas de Pago';

#
# Structure for the `project_type` table : 
#

CREATE TABLE `project_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Tipo de Expediente',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Expediente',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Activo si o no',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Expedientes';

#
# Structure for the `project` table : 
#

CREATE TABLE `project` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Proyecto',
  `alias` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Alias del Proyecto',
  `registry` int(4) NOT NULL COMMENT 'Identificador del Cliente (Potencial) asociado',
  `date` date NOT NULL COMMENT 'Fecha del Proyecto',
  `project_type` int(4) default NULL COMMENT 'Tipo de Proyecto',
  `tas` tinyint(1) default '0' COMMENT 'Indica si se trata de una Orden de Reparacion o Fabricacion',
  `commercial` tinyint(1) default '0' COMMENT 'Indica si se trata de una Operacion Comercial',
  `dossier` tinyint(1) default '0' COMMENT 'Indica si se trata de un Expediente de Cliente',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indica si el Proyecto esta activo o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROJECT_REGISTRY` (`registry`),
  KEY `IDX_PROJECT_ENTERPRISE` (`enterprise`),
  KEY `IDX_PROJECT_PROJECT_TYPE` (`project_type`),
  CONSTRAINT `FK_PROJECT_PROJECT_TYPE` FOREIGN KEY (`project_type`) REFERENCES `project_type` (`id`),
  CONSTRAINT `FK_PROJECT_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`),
  CONSTRAINT `FK_PROJECT_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Proyectos';

#
# Structure for the `invoice` table : 
#

CREATE TABLE `invoice` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Factura',
  `project` int(4) default NULL COMMENT 'Identificador del Proyecto',
  `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie de la Factura',
  `number` int(4) NOT NULL default '0' COMMENT 'Numero de la Factura',
  `reference_code` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Codigo de referencia de la Factura',
  `registry` int(4) NOT NULL default '0' COMMENT 'Identificador del Cliente o Proveedor',
  `rdocument` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Numero de Documento del Cliente o Proveedor',
  `rdocument_type` tinyint(2) default '0' COMMENT 'Tipo de documento (NIF, CIF...)',
  `rdocument_country` varchar(2) collate latin1_spanish_ci default 'ES' COMMENT 'Pais del documento',
  `rname` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Nombre completo del Cliente o Proveedor',
  `raddress` int(4) default NULL COMMENT 'Identificador de la Direccion de envio de la Factura',
  `issue_date` date default NULL COMMENT 'Fecha de emision de la Factura',
  `tax_date` date default NULL COMMENT 'Fecha de Impuestos de la Factura',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad de la Factura',
  `status` tinyint(2) default '0' COMMENT 'Estado de la Factura',
  `type` tinyint(2) default '0' COMMENT 'Tipo de Factura (Compra o Venta)',
  `taxFree` tinyint(1) default '0' COMMENT 'Indica si la Factura esta exenta de Impuestos',
  `surcharge` tinyint(1) default '0' COMMENT 'Indica si la Factura tiene recargo de equivalencia',
  `withholding` tinyint(1) default '0' COMMENT 'Indica si la Factura aplica retencion de impuestos',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios de la Factura',
  `remarks` text collate latin1_spanish_ci COMMENT 'Observaciones de la Factura',
  `investment` tinyint(1) default '0' COMMENT 'Indica si la Factura es una inversion',
  `transaction` tinyint(2) default '0' COMMENT 'Tipo de transaccion',
  `signed` tinyint(1) default '0' COMMENT 'Indica si la Factura esta firmada electronicamente',
  `scope` int(4) NOT NULL default '1' COMMENT 'Ambito de la Factura',
  `service` tinyint(1) default '0' COMMENT 'Indica si es una Factura de servicios',
  `rectification_type` tinyint(2) default '0' COMMENT 'Tipo de rectificacion (Normal o Especial)',
  `rectification_invoice` int(4) default NULL COMMENT 'Relacion de rectificacion de Facturas',
  `taxable_base` double default '0' COMMENT 'Base Imponible de la Factura',
  `vat_quota` double default '0' COMMENT 'Cuota de IVA de la Factura',
  `retention_quota` double default '0' COMMENT 'Cuota de IRPF de la Factura',
  `total` double default '0' COMMENT 'Total Factura',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `series` (`series`,`number`,`type`),
  KEY `idx_invc_radr` (`raddress`),
  KEY `idx_invc_date` (`issue_date`),
  KEY `registry` (`registry`),
  KEY `series_number` (`series`,`number`),
  KEY `idx_invc_tax_date` (`tax_date`),
  KEY `IDX_INVOICE_SCOPE` (`scope`),
  KEY `IDX_INVOICE_INVOICE` (`rectification_invoice`),
  KEY `IDX_INVOICE_PROJECT` (`project`),
  CONSTRAINT `FK_INVOICE_INVOICE` FOREIGN KEY (`rectification_invoice`) REFERENCES `invoice` (`id`),
  CONSTRAINT `FK_INVOICE_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_INVOICE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `invoice_ibfk_3` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `invoice_ibfk_4` FOREIGN KEY (`raddress`) REFERENCES `raddress` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Facturas';

#
# Structure for the `finance` table : 
#

CREATE TABLE `finance` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Vencimiento',
  `payment` tinyint(1) NOT NULL default '0' COMMENT 'Indica si es un pago o un cobro',
  `registry` int(4) default NULL COMMENT 'Identificador del Cliente o Proveedor',
  `rdocument` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Numero de Documento del Cliente o Proveedor',
  `rdocument_type` tinyint(2) default '0' COMMENT 'Tipo de documento (NIF, CIF...)',
  `rdocument_country` varchar(2) collate latin1_spanish_ci default 'ES' COMMENT 'Pais del documento',
  `rname` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Nombre completo del Cliente o Proveedor',
  `amount` double default '0' COMMENT 'Importe del Vencimiento',
  `expenses` double(15,3) default '0.000' COMMENT 'Gastos asociados al Vencimiento',
  `concept` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Concepto del Vencimiento',
  `invoice` int(4) default NULL COMMENT 'Identificador de la Factura',
  `due_date` date default NULL COMMENT 'Fecha de Vencimiento',
  `pay_method` int(4) default NULL COMMENT 'Identificador de la Forma de Pago',
  `bank` int(4) default NULL COMMENT 'Identificador de la Entidad Bancaria del Vencimiento',
  `bank_account` varchar(30) collate latin1_spanish_ci default NULL COMMENT 'Numero de cuenta en la Entidad Bancaria del Vencimiento',
  `status` tinyint(2) default '0' COMMENT 'Estado del Vencimiento',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Vencimiento',
  `scope` int(4) NOT NULL default '1' COMMENT 'Ambito del Vencimiento',
  PRIMARY KEY  (`id`),
  KEY `idx_finc_rgty` (`registry`),
  KEY `idx_finc_pymt` (`pay_method`),
  KEY `idx_finc_bank` (`bank`),
  KEY `idx_finc_dtty` (`due_date`),
  KEY `invoice` (`invoice`),
  KEY `IDX_FINANCE_SCOPE` (`scope`),
  CONSTRAINT `finance_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `finance_ibfk_2` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `finance_ibfk_3` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`),
  CONSTRAINT `finance_ibfk_4` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`),
  CONSTRAINT `FK_FINANCE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Vencimientos';

#
# Structure for the `pm_type_detail` table : 
#

CREATE TABLE `pm_type_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Forma de Pago',
  `description` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del detalle',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles por Tipo de Forma de Pago';

#
# Structure for the `finance_tracking` table : 
#

CREATE TABLE `finance_tracking` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `finance` int(4) NOT NULL COMMENT 'Identificador de Vencimiento',
  `tracking_date` date NOT NULL COMMENT 'Fecha de Seguimiento',
  `type` tinyint(4) NOT NULL COMMENT 'Tipo de Seguimiento',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Seguimiento',
  `pm_type_detail` int(4) default NULL COMMENT 'Identificador del Detalle por Tipo de Forma de Pago',
  `rbank` int(4) default NULL COMMENT 'Identificador de la Cuenta Bancaria de la Compañia',
  `bank_statement_link` int(4) default NULL COMMENT 'Identificador de la Linea del Extracto bancario',
  `amount` double(15,3) default NULL COMMENT 'Importe del Seguimiento',
  `recorded` tinyint(1) NOT NULL default '0' COMMENT 'Indica si esta contabilizado o no',
  PRIMARY KEY  (`id`),
  KEY `finance` (`finance`),
  KEY `IDX_FINANCE_TRACKING_RBANK` (`rbank`),
  KEY `IDX_FINANCE_TRACKING_PM_TYPE_DETAIL` (`pm_type_detail`),
  KEY `IDX_FINANCE_TRACKING_BANK_STATEMENT_LINK` (`bank_statement_link`),
  CONSTRAINT `finance_tracking_fk` FOREIGN KEY (`finance`) REFERENCES `finance` (`id`),
  CONSTRAINT `FK_FINANCE_TRACKING_BANK_STATEMENT_LINK` FOREIGN KEY (`bank_statement_link`) REFERENCES `bank_statement_link` (`id`),
  CONSTRAINT `FK_FINANCE_TRACKING_PM_TYPE_DETAIL` FOREIGN KEY (`pm_type_detail`) REFERENCES `pm_type_detail` (`id`),
  CONSTRAINT `FK_FINANCE_TRACKING_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Seguimiento de Vencimientos';

#
# Structure for the `account_entry_finance_tracking` table : 
#

CREATE TABLE `account_entry_finance_tracking` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `account_entry` int(4) NOT NULL COMMENT 'Identificador de Asiento Contable',
  `finance_tracking` int(4) NOT NULL COMMENT 'Identificador de Seguimiento de Vencimientos',
  PRIMARY KEY  (`id`),
  KEY `account_entry` (`account_entry`),
  KEY `finance_tracking` (`finance_tracking`),
  CONSTRAINT `account_entry_finance_tracking_fk` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `account_entry_finance_tracking_fk1` FOREIGN KEY (`finance_tracking`) REFERENCES `finance_tracking` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Seguimiento de Vencimientos y Asientos Contab';

#
# Structure for the `account_entry_invoice` table : 
#

CREATE TABLE `account_entry_invoice` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de Relacion',
  `account_entry` int(4) NOT NULL COMMENT 'Identificador de Asiento',
  `invoice` int(4) NOT NULL COMMENT 'Identificador de Factura',
  PRIMARY KEY  (`id`),
  KEY `account_entry` (`account_entry`),
  KEY `invoice` (`invoice`),
  CONSTRAINT `account_entry_invoice_fk_2` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`),
  CONSTRAINT `account_entry_invoice_ibfk_1` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Asientos Contables y Facturas';

#
# Structure for the `account_helper` table : 
#

CREATE TABLE `account_helper` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `counter` int(4) NOT NULL default '0' COMMENT 'Contador, veces que se ha usado',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta contable',
  `balancing_account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Contrapartida',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACCOUNT_HELPER_ACCOUNT` (`account`),
  KEY `IDX_ACCOUNT_HELPER_BALANCING_ACCOUNT` (`balancing_account`),
  CONSTRAINT `FK_ACCOUNT_HELPER_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_ACCOUNT_HELPER_BALANCING_ACCOUNT` FOREIGN KEY (`balancing_account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ayuda a la introduccion de apuntes';

#
# Structure for the `account_summary` table : 
#

CREATE TABLE `account_summary` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Acumulado',
  `account_period` char(4) collate latin1_spanish_ci NOT NULL COMMENT 'Ejercicio Contable del Acumulado',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta Contable del Acumulado',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Acumulado',
  `entry_date` date default NULL COMMENT 'Fecha del Acumulado',
  `debit` double default '0' COMMENT 'Debe del Acumulado',
  `credit` double default '0' COMMENT 'Haber del Acumulado',
  PRIMARY KEY  (`id`),
  KEY `account_summary_account_period_idx` (`account_period`),
  KEY `account_summary_account_idx` (`account`),
  CONSTRAINT `account_summary_ibfk_1` FOREIGN KEY (`account_period`) REFERENCES `account_period` (`id`),
  CONSTRAINT `account_summary_ibfk_2` FOREIGN KEY (`account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Acumulado de Cuentas Contables';

#
# Structure for the `application` table : 
#

CREATE TABLE `application` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `audit_level` tinyint(2) NOT NULL default '0' COMMENT 'Nivel de auditoria',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Nombre de la Aplicacion',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Aplicacion web';

#
# Structure for the `action` table : 
#

CREATE TABLE `action` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `menu` tinyint(1) NOT NULL default '0' COMMENT 'Indica si la Accion esta o no dentro del menu',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Nombre de la Accion',
  `application_id` int(4) NOT NULL COMMENT 'Aplicacion a la que pertenece la Accion',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACTION_NAME` (`name`),
  KEY `IDX_ACTION` (`name`,`application_id`),
  KEY `IDX_ACTION_APPLICATION` (`application_id`),
  CONSTRAINT `FK_ACTION_APPLICATION` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Acciones de una Applicacion';

#
# Structure for the `user` table : 
#

CREATE TABLE `user` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Usuario',
  `login` varchar(16) collate latin1_spanish_ci NOT NULL COMMENT 'Login del Usuario',
  `enterprise` int(4) default NULL COMMENT 'Identificador de la Empresa',
  `registry` int(4) default NULL COMMENT 'Identificador del Registry',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indica si el Usuario esta activo o no',
  `password` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Contraseña del Usuario',
  PRIMARY KEY  (`id`),
  KEY `IDX_USER_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_USER_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Usuarios';

#
# Structure for the `action_denied` table : 
#

CREATE TABLE `action_denied` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `action_id` int(4) NOT NULL default '0' COMMENT 'Identificador de la Accion',
  `user_id` int(4) NOT NULL default '0' COMMENT 'Identificador del Usuario',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACTION_DENIED_USER_ID` (`user_id`),
  KEY `IDX_ACTION_DENIED_ACTION_ID` (`action_id`),
  CONSTRAINT `FK_ACTION_DENIED_ACTION_ID` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `FK_ACTION_DENIED_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Accion no permitida para el Usuario';

#
# Structure for the `session` table : 
#

CREATE TABLE `session` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `endDate` datetime default NULL COMMENT 'Fecha de finalizacion',
  `remote_address` varchar(15) collate latin1_spanish_ci NOT NULL default '' COMMENT 'IP remota',
  `remote_host` varchar(64) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Equipo remoto',
  `session_id` varchar(128) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Identificador web de la sesión',
  `startDate` datetime NOT NULL COMMENT 'Fecha de inicio',
  `application_id` int(4) NOT NULL default '0' COMMENT 'Identificador de la Aplicacion',
  `user_id` int(4) NOT NULL default '0' COMMENT 'Identificador del Usuario',
  PRIMARY KEY  (`id`),
  KEY `IDX_SESSION_USER_ID` (`user_id`),
  KEY `IDX_SESSION_APPLICATION_ID` (`application_id`),
  CONSTRAINT `FK_SESSION_APPLICATION_ID` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `FK_SESSION_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Sesion web';

#
# Structure for the `action_entry` table : 
#

CREATE TABLE `action_entry` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `executionDate` datetime NOT NULL COMMENT 'Fecha de ejecucion',
  `action_id` int(4) NOT NULL default '0' COMMENT 'Identificador de la Accion',
  `session_id` int(4) NOT NULL default '0' COMMENT 'Identificador de la Sesion',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACTION_ENTRY_SESSION_ID` (`session_id`),
  KEY `IDX_ACTION_ENTRY_ACTION_ID` (`action_id`),
  CONSTRAINT `FK_ACTION_ENTRY_ACTION_ID` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `FK_ACTION_ENTRY_SESSION_ID` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Entrada de la ejecucion de una Accion';

#
# Structure for the `action_favorite` table : 
#

CREATE TABLE `action_favorite` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `position` int(4) NOT NULL COMMENT 'Posicion dentro de las Acciones Favoritas',
  `action_id` int(4) NOT NULL default '0' COMMENT 'Identificador de la Accion',
  `user_id` int(4) NOT NULL default '0' COMMENT 'Identificador del Usuario',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACTION_FAVORITE_USER_ID` (`user_id`),
  KEY `IDX_ACTION_FAVORITE_ACTION_ID` (`action_id`),
  CONSTRAINT `FK_ACTION_FAVORITE_ACTION_ID` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `FK_ACTION_FAVORITE_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Accion Favorita del Usuario';

#
# Structure for the `activity_type` table : 
#

CREATE TABLE `activity_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Tipo de Actividad',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Actividad',
  `project_type` int(4) default NULL COMMENT 'Tipo de Proyecto',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Activo si o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACT_TYPE_PROJECT_TYPE` (`project_type`),
  CONSTRAINT `FK_ACT_TYPE_PROJECT_TYPE` FOREIGN KEY (`project_type`) REFERENCES `project_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Actividades';

#
# Structure for the `agreement` table : 
#

CREATE TABLE `agreement` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `calendar` int(4) default NULL COMMENT 'Calendario',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_AGREEMENT_CALENDAR` (`calendar`),
  CONSTRAINT `FK_AGREEMENT_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Convenios';

#
# Structure for the `agreement_data` table : 
#

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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto del convenio';

#
# Structure for the `payment_concept` table : 
#

CREATE TABLE `payment_concept` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `code` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Codigo',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Percepcion Salarial',
  `description_decorable` tinyint(2) NOT NULL default '0',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe',
  `irpf_expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe tributable',
  `quote_expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe cotizable',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos de devengos';

#
# Structure for the `agreement_payment` table : 
#

CREATE TABLE `agreement_payment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `agreement` int(4) NOT NULL COMMENT 'Convenio',
  `payment_concept` int(4) default NULL COMMENT 'Identificador unico del concepto',
  `type` tinyint(2) default NULL COMMENT 'Tipo de complemento Salarial',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Script',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `month` tinyint(2) default NULL COMMENT 'Mes de la percepcion',
  `salary_type` tinyint(2) default NULL COMMENT 'Tipo de Nomina/Recibo',
  `description_decorable` tinyint(2) NOT NULL default '0',
  `irpf_expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe tributable',
  `quote_expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe cotizable',
  PRIMARY KEY  (`id`),
  KEY `IDX_PAYMENT_AGREEMENT` (`agreement`),
  KEY `IDX_AGREEMENT_PAYMENT_PAYMENT_CONCEPT` (`payment_concept`),
  CONSTRAINT `FK_AGREEMENT_PAYMENT_PAYMENT_CONCEPT` FOREIGN KEY (`payment_concept`) REFERENCES `payment_concept` (`id`),
  CONSTRAINT `FK_PAYMENT_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Percepciones';

#
# Structure for the `agreement_extra` table : 
#

CREATE TABLE `agreement_extra` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `agreement` int(4) NOT NULL COMMENT 'Convenio',
  `agreement_payment` int(4) default NULL COMMENT 'Concepto',
  `start_date` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Fecha de inicio dd mm [year offset]',
  `end_date` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Fecha de finalizacion dd mm [year offset]',
  `issue_date` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Fecha de emision dd mm',
  PRIMARY KEY  (`id`),
  KEY `IDX_AGREEMENT_EXTRA_AGREEMENT` (`agreement`),
  KEY `IDX_AGREEMENT_EXTRA_AGREEMENT_PAYMENT` (`agreement_payment`),
  CONSTRAINT `FK_AGREEMENT_EXTRA_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`),
  CONSTRAINT `FK_AGREEMENT_EXTRA_AGREEMENT_PAYMENT` FOREIGN KEY (`agreement_payment`) REFERENCES `agreement_payment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Pagas extras';

#
# Structure for the `agreement_level` table : 
#

CREATE TABLE `agreement_level` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `agreement` int(4) NOT NULL COMMENT 'Convenio',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_LEVEL_AGREEMENT` (`agreement`),
  CONSTRAINT `FK_LEVEL_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Niveles retributivos';

#
# Structure for the `agreement_level_category` table : 
#

CREATE TABLE `agreement_level_category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `agreement_level` int(4) NOT NULL COMMENT 'Nivel retributivo',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_CATEGORY_AGREEMENT_LEVEL` (`agreement_level`),
  CONSTRAINT `FK_CATEGORY_AGREEMENT_LEVEL` FOREIGN KEY (`agreement_level`) REFERENCES `agreement_level` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias profesionales';

#
# Structure for the `agreement_level_data` table : 
#

CREATE TABLE `agreement_level_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre',
  `agreement_level` int(4) NOT NULL COMMENT 'Nivel retributivo',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_AGREEMENT_LEVEL_DATA_AGREEMENT_LEVEL` (`agreement_level`),
  CONSTRAINT `FK_AGREEMENT_LEVEL_DATA_AGREEMENT_LEVEL` FOREIGN KEY (`agreement_level`) REFERENCES `agreement_level` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto del convenio';

#
# Structure for the `alarm` table : 
#

CREATE TABLE `alarm` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Alarma',
  `description` text collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Alarma',
  `alarm_date` datetime NOT NULL COMMENT 'Fecha y hora de ejecucion de la Alarma',
  `status` tinyint(2) default NULL COMMENT 'Estado de la Alarma',
  `source` tinyint(2) NOT NULL COMMENT 'Origen de la Alarma',
  `source_id` int(4) default NULL COMMENT 'Identificador del origen de la Alarma',
  `user_id` int(4) default NULL COMMENT 'Identificador del Usuario asociado a la Alarma',
  `priority` tinyint(2) NOT NULL COMMENT 'Prioridad de la Alarma',
  PRIMARY KEY  (`id`),
  KEY `user` (`user_id`),
  CONSTRAINT `alarm_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Alarmas';

#
# Structure for the `alumn_loan` table : 
#

CREATE TABLE `alumn_loan` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Prestamo',
  `customer` int(4) NOT NULL COMMENT 'Alumno al que se le realizo el Prestamo',
  `material` varchar(16) collate latin1_spanish_ci NOT NULL COMMENT 'Material prestado',
  `loan_date` date NOT NULL COMMENT 'Fecha del Prestamo',
  `end_date` date default NULL COMMENT 'Fecha devolucion del material',
  `comments` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Observaciones',
  PRIMARY KEY  (`id`),
  KEY `customer` (`customer`),
  CONSTRAINT `alumn_loan_fk1` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Prestamos a Alumnos';

#
# Structure for the `amortization_type` table : 
#

CREATE TABLE `amortization_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `fixed_asset_account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta de inmovilizado',
  `accumulated_account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta de amortizacion acumulada',
  `allocation_account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta para la dotacion de la amortizacion',
  `percentage` double default '0' COMMENT 'Porcentaje de amortizacion',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Amortizacion',
  PRIMARY KEY  (`id`),
  KEY `fixed_asset_account` (`fixed_asset_account`),
  KEY `allocation_account` (`allocation_account`),
  KEY `accumulated_account` (`accumulated_account`),
  CONSTRAINT `fk_amortization_type_account1` FOREIGN KEY (`fixed_asset_account`) REFERENCES `account` (`id`),
  CONSTRAINT `fk_amortization_type_account2` FOREIGN KEY (`accumulated_account`) REFERENCES `account` (`id`),
  CONSTRAINT `fk_amortization_type_account3` FOREIGN KEY (`allocation_account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Amortizacion';

#
# Structure for the `amortization` table : 
#

CREATE TABLE `amortization` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del inmovilizado',
  `amortization_type` int(4) NOT NULL COMMENT 'Tipo de Amortizacion',
  `initial_date` date NOT NULL COMMENT 'Fecha de inicio de la Amortizacion',
  `deadline` date default NULL COMMENT 'Fecha de baja de la Amortizacion',
  `amount` double NOT NULL default '0' COMMENT 'Importe a amortizar.',
  `fee_period` tinyint(2) NOT NULL default '0' COMMENT 'Periodo de las cuotas de Amortizacion',
  `sale_amount` double default NULL COMMENT 'Importe de la venta',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios',
  `fixed_asset_account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta de inmovilizado',
  `accumulated_account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta de Amortizacion acumulada',
  `allocation_account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta para la dotacion de la Amortizacion',
  `percentage` double default '0' COMMENT 'Porcentaje de Amortizacion',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad',
  PRIMARY KEY  (`id`),
  KEY `amortization_type` (`amortization_type`),
  KEY `IDX_AMORTIZATION_FIXED_ASSET_ACCOUNT` (`fixed_asset_account`),
  KEY `IDX_AMORTIZATION_ALLOCATION_ACCOUNT` (`allocation_account`),
  KEY `IDX_AMORTIZATION_ACCUMULATED_ACCOUNT` (`accumulated_account`),
  CONSTRAINT `FK_AMORTIZATION_ACCUMULATED_ACCOUNT` FOREIGN KEY (`allocation_account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_AMORTIZATION_ALLOCATION_ACCOUNT` FOREIGN KEY (`accumulated_account`) REFERENCES `account` (`id`),
  CONSTRAINT `fk_amortization_amortization_type` FOREIGN KEY (`amortization_type`) REFERENCES `amortization_type` (`id`),
  CONSTRAINT `FK_AMORTIZATION_FIXED_ASSET_ACCOUNT` FOREIGN KEY (`fixed_asset_account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Fichas de Amortizacion Contables';

#
# Structure for the `amortization_detail` table : 
#

CREATE TABLE `amortization_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `amortization` int(4) NOT NULL COMMENT 'Ficha de Amortizacion',
  `from_date` date NOT NULL COMMENT 'Desde fecha',
  `to_date` date NOT NULL COMMENT 'Hasta fecha',
  `coefficient` double(15,3) NOT NULL COMMENT 'Coeficiente de Amortizacion',
  `allocation` double(15,3) NOT NULL COMMENT 'Dotacion de la Amortizacion',
  `status` tinyint(2) NOT NULL default '0' COMMENT 'Estatus del Detalle de Amortizacion',
  `account_entry` int(4) default NULL COMMENT 'Posicion del Apunte Contable',
  `fiscal_allocation` double(15,3) default '0.000' COMMENT 'Dotacion fiscal',
  PRIMARY KEY  (`id`),
  KEY `amortization` (`amortization`),
  KEY `account_entry` (`account_entry`),
  CONSTRAINT `fk_amortization_detail_account_entry` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `fk_amortization_detail_amortization` FOREIGN KEY (`amortization`) REFERENCES `amortization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de la Ficha de Amortizacion Contable';

#
# Structure for the `app_param` table : 
#

CREATE TABLE `app_param` (
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Parametro',
  `value` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Valor del Parametro',
  PRIMARY KEY  (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Parametros de la Aplicacion';

#
# Structure for the `appraiser` table : 
#

CREATE TABLE `appraiser` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro del Perito',
  PRIMARY KEY  (`registry`),
  CONSTRAINT `appraiser_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Peritos';

#
# Structure for the `asset` table : 
#

CREATE TABLE `asset` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Activo',
  `name` varchar(10) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre corto del Activo',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Activos';

#
# Structure for the `asset_activity` table : 
#

CREATE TABLE `asset_activity` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `asset` int(4) NOT NULL COMMENT 'Identificador del Activo',
  `date` date NOT NULL COMMENT 'Fecha de la Actividad',
  `from_time` datetime NOT NULL COMMENT 'Hora de inicio de la Actividad',
  `to_time` datetime NOT NULL COMMENT 'Hora final de la Actividad',
  `who` varchar(20) collate latin1_spanish_ci default NULL COMMENT 'Quien solicita el Activo',
  `why` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Motivo de solicitud del Activo',
  `status` tinyint(2) NOT NULL default '0' COMMENT 'Estado de la Solicitud',
  PRIMARY KEY  (`id`),
  KEY `asset` (`asset`),
  CONSTRAINT `asset_activity_fk1` FOREIGN KEY (`asset`) REFERENCES `asset` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades sobre el Activo';

#
# Structure for the `auto_concept` table : 
#

CREATE TABLE `auto_concept` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Concepto Automatico',
  `description` char(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Concepto Automatico',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos Automaticos';

#
# Structure for the `balance` table : 
#

CREATE TABLE `balance` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Nombre del Balance',
  `removable` tinyint(1) default '0' COMMENT 'Indica se puede ser borrado por el usuario',
  `type` tinyint(2) default '0' COMMENT 'Tipo de Balance',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Balances';

#
# Structure for the `balance_detail` table : 
#

CREATE TABLE `balance_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `balance` int(4) NOT NULL COMMENT 'Identificador del Balance',
  `code` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo del Detalle en el Balance',
  `description` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Descripción del detalle de balance',
  `accounts` text collate latin1_spanish_ci COMMENT 'Cuentas separadas por comas, que forman el acumulado.',
  `sortKey` int(4) default '0' COMMENT 'Orden el que aparecera en el listado.',
  `title` tinyint(1) NOT NULL default '0',
  `internal_calculation` tinyint(1) NOT NULL default '0' COMMENT 'Indica si es un calculo interno, es decir si el contenido\r\n                de accounts son referencias a la columna -code- de esta tabla',
  `visible` tinyint(1) NOT NULL default '1' COMMENT 'Si aparece o no en la impresion.',
  `zeroFlag` tinyint(1) NOT NULL default '0' COMMENT 'Flag que se activa cuando la cuenta o cuentas tienen valor 0.',
  `creditNature` tinyint(1) NOT NULL default '0' COMMENT 'Si es verdadero se hace una haber menos debe de las cuentas indicadas',
  PRIMARY KEY  (`id`),
  KEY `idx_balance` (`balance`),
  CONSTRAINT `fk_balance_detail_balance` FOREIGN KEY (`balance`) REFERENCES `balance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Balace';

#
# Structure for the `bank_concept` table : 
#

CREATE TABLE `bank_concept` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Concepto',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos bancarios';

#
# Structure for the `bank_concept_account` table : 
#

CREATE TABLE `bank_concept_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `bank_concept` int(4) NOT NULL default '0' COMMENT 'Identificador del Concepto bancario',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `IDX_BANK_CONCEPT_ACCOUNT_BANK_CONCEPT` (`bank_concept`),
  KEY `IDX_BANK_CONCEPT_ACCOUNT_ACCOUNT` (`account`),
  CONSTRAINT `FK_BANK_CONCEPT_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_BANK_CONCEPT_ACCOUNT_BANK_CONCEPT` FOREIGN KEY (`bank_concept`) REFERENCES `bank_concept` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Conceptos bancarios';

#
# Structure for the `bonus_concept` table : 
#

CREATE TABLE `bonus_concept` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos de bonficaciones y/o reducciones';

#
# Structure for the `brand` table : 
#

CREATE TABLE `brand` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Marca Comercial',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Marca Comercial',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Marcas Comerciales';

#
# Structure for the `calendar_holiday` table : 
#

CREATE TABLE `calendar_holiday` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `calendar` int(4) NOT NULL default '0' COMMENT 'Identificador del Calendario',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Festivo',
  `date` date default NULL COMMENT 'Fecha del festivo',
  `day_type` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `hours` double default '0' COMMENT 'Numero de horas laborables',
  PRIMARY KEY  (`id`),
  KEY `IDX_CALENDAR_HOLIDAY_CALENDAR` (`calendar`),
  CONSTRAINT `FK_CALENDAR_HOLIDAY_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Festivos de Calendarios';

#
# Structure for the `calendar_period` table : 
#

CREATE TABLE `calendar_period` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `calendar` int(4) NOT NULL COMMENT 'Identificador del Calendario',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Periodo',
  `month` tinyint(2) default '0' COMMENT 'Mes del periodo',
  `start_day` tinyint(2) default '0' COMMENT 'Dia inicio del periodo',
  `end_day` tinyint(2) default '0' COMMENT 'Dia fin del periodo',
  `monday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `monday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `tuesday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `tuesday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `wednesday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `wednesday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `thursday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `thursday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `friday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `friday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `saturday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `saturday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `sunday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `sunday_hours` double default '0' COMMENT 'Numero de horas laborables',
  PRIMARY KEY  (`id`),
  KEY `IDX_CALENDAR_PERIOD_CALENDAR` (`calendar`),
  CONSTRAINT `FK_CALENDAR_PERIOD_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Periodos de Calendarios';

#
# Structure for the `campaign_type` table : 
#

CREATE TABLE `campaign_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `description` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion',
  `active` tinyint(1) NOT NULL default '0' COMMENT 'Activo si o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_CAMPAIGN_TYPE_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_CAMPAIGN_TYPE_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Campañas';

#
# Structure for the `process` table : 
#

CREATE TABLE `process` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Proceso',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `description` varchar(30) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Proceso.',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Activo si o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROCESS_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_PROCESS_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Procesos';

#
# Structure for the `workgroup` table : 
#

CREATE TABLE `workgroup` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Grupo de Trabajo',
  `enterprise` int(4) default NULL COMMENT 'Identificador de la Empresa',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Grupo de Trabajo',
  `status` tinyint(2) default NULL COMMENT 'Estado del grupo de Trabajo',
  PRIMARY KEY  (`id`),
  KEY `IDX_WORKGROUP_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_WORKGROUP_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Grupos de Trabajo';

#
# Structure for the `campaign` table : 
#

CREATE TABLE `campaign` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Campaña',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `campaign_type` int(4) default NULL COMMENT 'Identificador del Tipo de Campaña',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Campaña',
  `process` int(4) NOT NULL COMMENT 'Identificador del Proceso',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio de la Campaña',
  `end_date` date NOT NULL COMMENT 'Fecha de finalizacion de la Campaña',
  `workgroup` int(4) NOT NULL COMMENT 'Grupo de Trabajo supervisor de la Campaña',
  `manual` tinyint(1) NOT NULL default '0' COMMENT 'Tipo de Campaña',
  `status` tinyint(2) default NULL COMMENT 'Estado de la Campaña',
  PRIMARY KEY  (`id`),
  KEY `process` (`process`),
  KEY `workgroup` (`workgroup`),
  KEY `IDX_CAMPAIGN_ENTERPRISE` (`enterprise`),
  KEY `IDX_CAMPAIGN_CAMPAIGN_TYPE` (`campaign_type`),
  CONSTRAINT `FK_CAMPAIGN_CAMPAIGN_TYPE` FOREIGN KEY (`campaign_type`) REFERENCES `campaign_type` (`id`),
  CONSTRAINT `campaign_ibfk_1` FOREIGN KEY (`process`) REFERENCES `process` (`id`),
  CONSTRAINT `campaign_ibfk_3` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`),
  CONSTRAINT `FK_CAMPAIGN_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Campañas';

#
# Structure for the `campaign_project` table : 
#

CREATE TABLE `campaign_project` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Relacion de Campañas y Expedientes',
  `campaign` int(4) NOT NULL COMMENT 'Identificador de la Campaña',
  `project` int(4) NOT NULL COMMENT 'Identificador del Expediente',
  PRIMARY KEY  (`id`),
  KEY `IDX_CMP_PRJ_CAMPAIGN` (`campaign`),
  KEY `IDX_CMP_PRJ_PROJECT` (`project`),
  CONSTRAINT `FK_CMP_PRJ_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_CMP_PRJ_CAMPAIGN` FOREIGN KEY (`campaign`) REFERENCES `campaign` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Campañas y Expedientes';

#
# Structure for the `cashflow_forecast` table : 
#

CREATE TABLE `cashflow_forecast` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `payment` tinyint(1) NOT NULL default '0' COMMENT 'Indica si es un pago o un cobro',
  `description` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio de aplicacion',
  `due_date` date default NULL COMMENT 'Fecha final de aplicacion',
  `rbank` int(4) default NULL COMMENT 'Identificador de Banco de la Compañia',
  `amount` double(15,2) NOT NULL default '0.00' COMMENT 'Importe',
  `payment_day` double(15,2) NOT NULL default '1.00' COMMENT 'Dia de pago',
  `january` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en enero',
  `february` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en febrero',
  `march` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en marzo',
  `april` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en abril',
  `may` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en mayo',
  `june` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en junio',
  `july` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en julio',
  `august` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en agosto',
  `september` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en septiembre',
  `october` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en octubre',
  `november` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en noviembre',
  `december` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en diciembre',
  PRIMARY KEY  (`id`),
  KEY `IDX_CASHFLOW_FORECAST_RBANK` (`rbank`),
  CONSTRAINT `FK_CASHFLOW_FORECAST_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Prevision de tesoreria';

#
# Structure for the `catalogue` table : 
#

CREATE TABLE `catalogue` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Catalogo',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio del Catalogo',
  `end_date` date default NULL COMMENT 'Fecha de fin del Catalogo',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Catalogos';

#
# Structure for the `pcategory_group` table : 
#

CREATE TABLE `pcategory_group` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Grupo de Categorias',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Grupo de Categorias',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Grupos de Categorias de Productos';

#
# Structure for the `pcategory` table : 
#

CREATE TABLE `pcategory` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Categoria',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Categoria',
  `detail_pattern` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Patron para los detalles de Articulos',
  `pcategory_group` int(4) default NULL COMMENT 'Identificador del Grupo de Categorias',
  PRIMARY KEY  (`id`),
  KEY `pcategory_group` (`pcategory_group`),
  CONSTRAINT `pcategory_fk_1` FOREIGN KEY (`pcategory_group`) REFERENCES `pcategory_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias de Productos';

#
# Structure for the `catalogue_category` table : 
#

CREATE TABLE `catalogue_category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `catalogue` int(4) NOT NULL COMMENT 'Identificador del Catalogo',
  `category` int(4) NOT NULL COMMENT 'Identificador de la Categoria',
  `quantity` double default '0' COMMENT 'Cantidad a partir de la cual se aplica el descuento',
  `discount` double(6,2) default '0.00' COMMENT 'Descuento de la Categoria en el Catalogo',
  PRIMARY KEY  (`id`),
  KEY `catalogue` (`catalogue`),
  KEY `category` (`category`),
  CONSTRAINT `catalogue_category_fk1` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`),
  CONSTRAINT `catalogue_category_fk2` FOREIGN KEY (`category`) REFERENCES `pcategory` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias del Catalogo';

#
# Structure for the `tax` table : 
#

CREATE TABLE `tax` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Impuesto',
  `name` varchar(30) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Impuesto',
  `tax_type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Impuesto',
  `percentage` double(15,3) NOT NULL default '0.000' COMMENT 'Porcentaje de recargo actual',
  `surcharge` double(15,3) default '0.000' COMMENT 'Porcentaje de recargo de equivalencia actual',
  `start_date` date default NULL COMMENT 'Fecha de inicio de vigencia',
  `vat_deduction_type` tinyint(2) default '0' COMMENT 'Tipo de deduccion del IVA',
  `withholding_type` tinyint(2) default '0' COMMENT 'Tipo de retencion',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Impuestos';

#
# Structure for the `product` table : 
#

CREATE TABLE `product` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Producto',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Producto',
  `code` varchar(15) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo del Producto',
  `brand` int(4) default NULL COMMENT 'Marca Comercial del Producto',
  `category` int(4) default NULL COMMENT 'Categoria del Producto',
  `inventoriable` tinyint(1) default NULL COMMENT 'Indica si el Producto es inventariable',
  `status` tinyint(2) default '0' COMMENT 'Estado del Producto',
  `vat` int(4) default NULL COMMENT 'IVA del Producto',
  `retention` int(4) default NULL COMMENT 'Retencion del Producto',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Producto',
  `composition` tinyint(1) default '0' COMMENT 'Indica si el Producto es una Composicion',
  `composition_price` tinyint(1) default '0' COMMENT 'Indica si el Precio lo determina la Composicion',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_prdt_code` (`code`),
  KEY `idx_prdt_name` (`name`),
  KEY `idx_prdt_ctgy` (`category`),
  KEY `idx_prdt_brand` (`brand`),
  KEY `vat` (`vat`),
  KEY `retention` (`retention`),
  CONSTRAINT `product_fk` FOREIGN KEY (`retention`) REFERENCES `tax` (`id`),
  CONSTRAINT `product_ibfk_1` FOREIGN KEY (`category`) REFERENCES `pcategory` (`id`),
  CONSTRAINT `product_ibfk_2` FOREIGN KEY (`brand`) REFERENCES `brand` (`id`),
  CONSTRAINT `product_ibfk_3` FOREIGN KEY (`vat`) REFERENCES `tax` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Productos';

#
# Structure for the `item` table : 
#

CREATE TABLE `item` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Articulo',
  `product` int(4) NOT NULL default '0' COMMENT 'Identificador del Producto',
  `detail` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Detalle del Articulo',
  `description` text collate latin1_spanish_ci COMMENT 'Descripcion del Articulo',
  `price` double default '0' COMMENT 'Precio del Articulo',
  `status` tinyint(2) default '0' COMMENT 'Estado del Articulo',
  `expenses_percent` double default '0' COMMENT 'Gastos porcentuales del Articulo',
  `expenses_fixed` double default '0' COMMENT 'Gastos fijos del Articulo',
  `profit_percent` double default '0' COMMENT 'Porcentaje de beneficio del Articulo',
  `purchase_price` double default '0' COMMENT 'Precio de compra del Articulo',
  `internet` tinyint(1) default '0' COMMENT 'Visible en internet',
  `barcode` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Codigo de barras del Articulo',
  PRIMARY KEY  (`id`),
  KEY `idx_item_prdt` (`product`),
  CONSTRAINT `item_ibfk_1` FOREIGN KEY (`product`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulos';

#
# Structure for the `catalogue_item` table : 
#

CREATE TABLE `catalogue_item` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `catalogue` int(4) NOT NULL COMMENT 'Identificador del Catalogo',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo',
  `quantity` double default '0' COMMENT 'Cantidad a partir de la cual se aplica el precio o descuento',
  `price` double default '0' COMMENT 'Precio del Articulo en el Catalogo',
  `discount` double(6,2) default '0.00' COMMENT 'Descuento del Articulo en el Catalogo',
  PRIMARY KEY  (`id`),
  KEY `catalogue` (`catalogue`),
  KEY `item` (`item`),
  CONSTRAINT `catalogue_item_fk1` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`),
  CONSTRAINT `catalogue_item_fk2` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulos del Catalogo';

#
# Structure for the `category` table : 
#

CREATE TABLE `category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Categoria',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Categoria',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias';

#
# Structure for the `certifica2_batch` table : 
#

CREATE TABLE `certifica2_batch` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del certificado de empresa de la remesa',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador unico del certificado de empresa de la empresa',
  `date` date NOT NULL COMMENT 'Fecha de la ultima remesa en la que fue incluido',
  `status` int(4) default NULL COMMENT 'Estado del certificado correspondiente a la ultima respuesta',
  `sign` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Estado del certificado correspondiente a la ultima respuesta',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas de certificados de empresa';

#
# Structure for the `certifica2_batch_attach` table : 
#

CREATE TABLE `certifica2_batch_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Archivo Adjunto',
  `certifica2_batch` int(4) NOT NULL default '0' COMMENT 'Identificador de la remesa',
  `mimeType` tinyint(2) default '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Archivo Adjunto',
  `scope` int(4) default NULL COMMENT 'Ambito del Archivo Adjunto',
  `attach_date` date default NULL COMMENT 'Fecha del Archivo Adjunto',
  PRIMARY KEY  (`id`),
  KEY `IDX_CERTIFICA2_BATCH_ATTACH_CERTIFICA2_BATCH` (`certifica2_batch`),
  KEY `IDX_CERTIFICA2_BATCH_ATTACH_SCOPE` (`scope`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_ATTACH_CERTIFICA2_BATCH` FOREIGN KEY (`certifica2_batch`) REFERENCES `certifica2_batch` (`id`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de remesas de certificados de empresa';

#
# Structure for the `cnae` table : 
#

CREATE TABLE `cnae` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `code` varchar(5) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo del CNAE',
  `title` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Titulo del CNAE',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='CNAE';

#
# Structure for the `cnae2009` table : 
#

CREATE TABLE `cnae2009` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `code` varchar(4) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo del CNAE',
  `title` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Titulo del CNAE',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='CNAE 2009. Clasificación Nacional de Actividades Económicas ';

#
# Structure for the `enterprise_activity` table : 
#

CREATE TABLE `enterprise_activity` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Actividad de la Empresa',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `cnae` int(4) default NULL COMMENT 'Identificador del CNAE',
  `type` tinyint(2) NOT NULL COMMENT 'Tipo de Actividad de la Empresa',
  `cnae2009` int(4) default NULL COMMENT 'Identificador del CNAE 2009',
  PRIMARY KEY  (`id`),
  KEY `IDX_ENTERPRISE_ACTIVITY_ENTERPRISE` (`enterprise`),
  KEY `IDX_ENTERPRISE_ACTIVITY_CNAE` (`cnae`),
  KEY `IDX_ENTERPRISE_ACTIVITY_CNAE2009` (`cnae2009`),
  CONSTRAINT `FK_ENTERPRISE_ACTIVITY_CNAE` FOREIGN KEY (`cnae`) REFERENCES `cnae` (`id`),
  CONSTRAINT `FK_ENTERPRISE_ACTIVITY_CNAE2009` FOREIGN KEY (`cnae2009`) REFERENCES `cnae2009` (`id`),
  CONSTRAINT `FK_ENTERPRISE_ACTIVITY_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades de Empresas';

#
# Structure for the `enterprise_ccc` table : 
#

CREATE TABLE `enterprise_ccc` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `ccc` char(11) collate latin1_spanish_ci default NULL COMMENT 'Valor del Codigo Cuenta Cotizacion',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Cuenta Cotizacion',
  `enterprise_activity` int(4) NOT NULL COMMENT 'Identificador de la Actividad de Empresa',
  `geozone` int(4) default NULL COMMENT 'Identificador de la Zona Geografica',
  PRIMARY KEY  (`id`),
  KEY `IDX_ENTERPRISE_CCC_ENTERPRISE_ACTIVITY` (`enterprise_activity`),
  KEY `IDX_ENTERPRISE_CCC_GEOZONE` (`geozone`),
  CONSTRAINT `FK_ENTERPRISE_CCC_ENTERPRISE_ACTIVITY` FOREIGN KEY (`enterprise_activity`) REFERENCES `enterprise_activity` (`id`),
  CONSTRAINT `FK_ENTERPRISE_CCC_GEOZONE` FOREIGN KEY (`geozone`) REFERENCES `geozone` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Codigo Cuenta Cotizacion';

#
# Structure for the `person` table : 
#

CREATE TABLE `person` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro de la Persona',
  `birth_date` date default NULL COMMENT 'Fecha de nacimiento de la Persona',
  `gender` tinyint(2) NOT NULL default '0' COMMENT 'Sexo de la Persona',
  `marital_status` tinyint(2) NOT NULL default '0' COMMENT 'Estado civil de la Persona',
  `social_security_num` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Numero de Seguridad Social de la Persona',
  `name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Nombre',
  `first_surname` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Primer Apellido ',
  `second_surname` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Segundo Apellido',
  PRIMARY KEY  (`registry`),
  CONSTRAINT `person_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Personas';

#
# Structure for the `contract` table : 
#

CREATE TABLE `contract` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `person` int(4) NOT NULL COMMENT 'Identificador de la Persona',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `enterprise_ccc` int(4) default NULL COMMENT 'CCC',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio del Contrato',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion del Contrato',
  `calendar` int(4) default NULL COMMENT 'Calendario',
  `document` mediumblob COMMENT 'Impreso (.pdf) del contrato.',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `status` tinyint(2) default '0' COMMENT 'Estado de notificacion del contrato',
  `registration` int(4) default NULL COMMENT 'Número libro de matricula',
  `seniority_date` date default NULL COMMENT 'Fecha de antiguedad',
  `enterprise_activity` int(4) default NULL COMMENT 'Actividad',
  `ss_regime` tinyint(2) NOT NULL default '0' COMMENT 'Regimen de la Seguridad Social',
  `agreement_level_category` int(4) default NULL COMMENT 'Identificador unico de la Categoria Profesional',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_PERSON` (`person`),
  KEY `IDX_CONTRACT_WORKPLACE` (`workplace`),
  KEY `IDX_CONTRACT_CCC` (`enterprise_ccc`),
  KEY `IDX_CONTRACT_CALENDAR` (`calendar`),
  KEY `IDX_CONTRACT_ENTERPRISE_ACTIVITY` (`calendar`),
  KEY `FK_CONTRACT_ENTERPRISE_ACTIVITY` (`enterprise_activity`),
  KEY `IDX_CONTRACT_ENTERPRISE_CCC` (`enterprise_ccc`),
  KEY `IDX_CONTRACT_AGREEMENT_LEVEL_CATEGORY` (`agreement_level_category`),
  CONSTRAINT `FK_CONTRACT_AGREEMENT_LEVEL_CATEGORY` FOREIGN KEY (`agreement_level_category`) REFERENCES `agreement_level_category` (`id`),
  CONSTRAINT `FK_CONTRACT_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_CONTRACT_ENTERPRISE_ACTIVITY` FOREIGN KEY (`enterprise_activity`) REFERENCES `enterprise_activity` (`id`),
  CONSTRAINT `FK_CONTRACT_ENTERPRISE_CCC` FOREIGN KEY (`enterprise_ccc`) REFERENCES `enterprise_ccc` (`id`),
  CONSTRAINT `FK_CONTRACT_PERSON` FOREIGN KEY (`person`) REFERENCES `person` (`registry`),
  CONSTRAINT `FK_CONTRACT_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contratos';

#
# Structure for the `certifica2_batch_detail` table : 
#

CREATE TABLE `certifica2_batch_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del certificado de empresa de la remesa',
  `certifica2_batch` int(4) NOT NULL COMMENT 'Identificador unico del certificado de empresa',
  `contract` int(4) NOT NULL COMMENT 'Identificador unico del contrato de empleado',
  `enterprise_nif` varchar(9) collate latin1_spanish_ci NOT NULL COMMENT 'NIF de la empresa',
  `ccc` varchar(15) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo cuenta cotizacion',
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

#
# Structure for the `certifica2_batch_data` table : 
#

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

#
# Structure for the `cnae2009_rate` table : 
#

CREATE TABLE `cnae2009_rate` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador único',
  `cnae2009` int(4) NOT NULL COMMENT 'CNAE',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `it_amount` double(15,3) default '0.000' COMMENT 'Importe por Incapacidad Temporal (I.T.)',
  `ims_amount` double(15,3) default '0.000' COMMENT 'Importe por Incapacidad Permanente, Muerte y Supervivencia (I.M.S.)',
  PRIMARY KEY  (`id`),
  KEY `IDX_RATE_CNAE2009` (`cnae2009`),
  CONSTRAINT `FK_RATE_CNAE2009` FOREIGN KEY (`cnae2009`) REFERENCES `cnae2009` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas de primas para I.T e I.M.S';

#
# Structure for the `cno` table : 
#

CREATE TABLE `cno` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `code` varchar(5) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo del CNO',
  `title` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Titulo del CNO',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='CNO';

#
# Structure for the `commercial_activity` table : 
#

CREATE TABLE `commercial_activity` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Actividad Comercial',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades Comerciales';

#
# Structure for the `commercial_term` table : 
#

CREATE TABLE `commercial_term` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `line` smallint(2) default '1' COMMENT 'Numero de linea de Condicion',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Nombre de la Condicion Comercial',
  `description` text collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Condicion Comercial',
  `term_general` tinyint(1) default '0' COMMENT 'Indica si la Condición es particular o general',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Condiciones Comerciales';

#
# Structure for the `supplier_segment` table : 
#

CREATE TABLE `supplier_segment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Segmento',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Segmento',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Segmentacion de Proveedores';

#
# Structure for the `supplier` table : 
#

CREATE TABLE `supplier` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro del Proveedor',
  `withholding` tinyint(1) default '0' COMMENT 'Indica si el Proveedor aplica retencion de impuestos',
  `transaction` tinyint(2) default '0' COMMENT 'Tipo de transacciones del Proveedor',
  `status` tinyint(2) default NULL COMMENT 'Estado del Proveedor',
  `segment` int(4) default NULL COMMENT 'Segmento del Proveedor',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  PRIMARY KEY  (`registry`),
  KEY `segment` (`segment`),
  KEY `scope` (`scope`),
  CONSTRAINT `supplier_fk` FOREIGN KEY (`segment`) REFERENCES `supplier_segment` (`id`),
  CONSTRAINT `supplier_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `supplier_ibfk_2` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Proveedores';

#
# Structure for the `target` table : 
#

CREATE TABLE `target` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro del Cliente Potencial',
  `tariff` int(4) default NULL COMMENT 'Tarifa asociada al Cliente Potencial',
  `advertising` tinyint(2) NOT NULL default '0' COMMENT 'Admision de Publicidad',
  `surcharge` tinyint(1) default '0' COMMENT 'Indica si el Cliente Potencial tiene recargo de equivalencia',
  `withholding` tinyint(1) default '0' COMMENT 'Indica si el Cliente Potencial aplica retencion de impuestos',
  `transaction` tinyint(2) default '0' COMMENT 'Tipo de transacciones del Cliente Potencial',
  `status` tinyint(2) default '0' COMMENT 'Estado del Cliente Potencial',
  `scope` int(4) NOT NULL default '1' COMMENT 'Identificador del Ambito',
  PRIMARY KEY  (`registry`),
  KEY `IDX_TARGET_TARIFF` (`tariff`),
  KEY `IDX_TARGET_SCOPE` (`scope`),
  CONSTRAINT `FK_TARGET_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_TARGET_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`),
  CONSTRAINT `target_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Clientes Potenciales';

#
# Structure for the `commission_type` table : 
#

CREATE TABLE `commission_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Comision',
  `rate` double(6,2) default '0.00' COMMENT 'Porcentaje de Comision',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Comisiones';

#
# Structure for the `seller` table : 
#

CREATE TABLE `seller` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro del Agente Comercial',
  `commission_type` int(4) default NULL COMMENT 'Identificador del Tipo de Comision',
  `status` tinyint(2) default '0' COMMENT 'Estado del Agente Comercial',
  PRIMARY KEY  (`registry`),
  KEY `IDX_SELLER_COMMISSION_TYPE` (`commission_type`),
  CONSTRAINT `FK_SELLER_COMMISSION_TYPE` FOREIGN KEY (`commission_type`) REFERENCES `commission_type` (`id`),
  CONSTRAINT `seller_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Agentes Comerciales';

#
# Structure for the `offer` table : 
#

CREATE TABLE `offer` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Presupuesto',
  `project` int(4) default NULL COMMENT 'Identificador del Proyecto',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie del Presupuesto',
  `number` int(4) NOT NULL COMMENT 'Numero del Presupuesto',
  `version` smallint(2) NOT NULL default '0' COMMENT 'Numero de version de Presupuesto',
  `address` int(4) default NULL COMMENT 'Identificador de la Direccion de envio del Presupuesto',
  `tariff` int(4) default NULL COMMENT 'Identificador de la Tarifa del Presupuesto',
  `seller` int(4) default NULL COMMENT 'Agente Comercial del Presupuesto',
  `supplier` int(4) default NULL COMMENT 'Identificador del Proveedor',
  `discount_expr` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descuentos del Presupuesto',
  `issue_date` date default NULL COMMENT 'Fecha de emision del Presupuesto',
  `pay_method` int(4) default NULL COMMENT 'Forma de Pago del Presupuesto',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Presupuesto',
  `status` tinyint(2) default '0' COMMENT 'Estado del Presupuesto',
  `type` tinyint(2) default '0' COMMENT 'Tipo de Presupuesto',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `scope` int(4) NOT NULL default '1' COMMENT 'Ambito del Presupuesto',
  `number_of_pymnts` smallint(2) default '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint(2) default '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint(2) default '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) collate latin1_spanish_ci default '0' COMMENT 'Dias de pago',
  `bank` int(4) default NULL COMMENT 'Identificador de la Entidad Bancaria',
  `bank_account` varchar(30) collate latin1_spanish_ci default NULL COMMENT 'Numero de cuenta en la Entidad Bancaria',
  `signed` tinyint(1) default '0' COMMENT 'Indica si el Presupuesto esta firmada electronicamente',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios del Presupuesto',
  `remarks` text collate latin1_spanish_ci COMMENT 'Observaciones del Presupuesto',
  `external_reference` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Referencia externa',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `series` (`series`,`number`,`version`),
  KEY `target` (`target`),
  KEY `seller` (`seller`),
  KEY `pay_method` (`pay_method`),
  KEY `workplace` (`workplace`),
  KEY `tariff` (`tariff`),
  KEY `IDX_OFFER_SCOPE` (`scope`),
  KEY `IDX_OFFER_BANK` (`bank`),
  KEY `IDX_OFFER_SUPPLIER` (`supplier`),
  KEY `IDX_OFFER_RADDRESS` (`address`),
  KEY `IDX_OFFER_PROJECT` (`project`),
  CONSTRAINT `FK_OFFER_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`),
  CONSTRAINT `FK_OFFER_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_OFFER_RADDRESS` FOREIGN KEY (`address`) REFERENCES `raddress` (`id`),
  CONSTRAINT `FK_OFFER_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_OFFER_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`),
  CONSTRAINT `offer_ibfk_1` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `offer_ibfk_2` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`),
  CONSTRAINT `offer_ibfk_3` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `offer_ibfk_4` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`),
  CONSTRAINT `offer_ibfk_5` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Presupuestos';

#
# Structure for the `commercial_tracking` table : 
#

CREATE TABLE `commercial_tracking` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `date` datetime NOT NULL COMMENT 'Fecha del Seguimiento Comercial',
  `seller` int(4) NOT NULL COMMENT 'Identificador del Comercial',
  `project` int(4) NOT NULL COMMENT 'Identificador del Proyecto',
  `activity` int(4) NOT NULL COMMENT 'Identificador de la Actividad Comercial',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios del Seguimiento Comercial',
  `status` tinyint(2) NOT NULL COMMENT 'Estado del Seguimiento Comercial',
  `next_commercial_tracking` int(4) default NULL COMMENT 'Identificador del siguiente Seguimiento Comercial',
  `end_date` datetime default NULL COMMENT 'Fecha de cierre del Seguimiento Comercial',
  `offer` int(4) default NULL COMMENT 'Identificador del Presupuesto',
  `allDay` tinyint(1) default '0' COMMENT 'Indica si el Seguimiento Comercial dura todo el dia',
  `location` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Ubicacion del Seguimiento Comercial',
  PRIMARY KEY  (`id`),
  KEY `IDX_COMMERCIAL_TRACKING_SELLER` (`seller`),
  KEY `IDX_COMMERCIAL_TRACKING_PROJECT` (`project`),
  KEY `IDX_COMMERCIAL_TRACKING_ACTIVITY` (`activity`),
  KEY `IDX_COMMERCIAL_TRACKING_NEXT` (`next_commercial_tracking`),
  KEY `IDX_COMMERCIAL_TRACKING_OFFER` (`offer`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_ACTIVITY` FOREIGN KEY (`activity`) REFERENCES `commercial_activity` (`id`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_NEXT` FOREIGN KEY (`next_commercial_tracking`) REFERENCES `commercial_tracking` (`id`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Seguimientos Comerciales';

#
# Structure for the `commission` table : 
#

CREATE TABLE `commission` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Comision',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio de la Comision',
  `end_date` date default NULL COMMENT 'Fecha de fin de la Comision',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones';

#
# Structure for the `commission_category` table : 
#

CREATE TABLE `commission_category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `commission` int(4) NOT NULL COMMENT 'Identificador de la Comision',
  `category` int(4) NOT NULL COMMENT 'Identificador de la Categoria',
  `quantity` double default '0' COMMENT 'Cantidad a partir de la cual se aplica la Comision',
  `rate` double(6,2) default '0.00' COMMENT 'Porcentaje de Comision',
  PRIMARY KEY  (`id`),
  KEY `IDX_COMMISSION_CATEGORY_COMMISSION` (`commission`),
  KEY `IDX_COMMISSION_CATEGORY_CATEGORY` (`category`),
  CONSTRAINT `FK_COMMISSION_CATEGORY_CATEGORY` FOREIGN KEY (`category`) REFERENCES `pcategory` (`id`),
  CONSTRAINT `FK_COMMISSION_CATEGORY_COMMISSION` FOREIGN KEY (`commission`) REFERENCES `commission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones por Categoria';

#
# Structure for the `commission_item` table : 
#

CREATE TABLE `commission_item` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `commission` int(4) NOT NULL COMMENT 'Identificador de la Comision',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo',
  `quantity` double default '0' COMMENT 'Cantidad a partir de la cual se aplica la Comision',
  `amount` double default '0' COMMENT 'Importe de la Comision',
  `rate` double(6,2) default '0.00' COMMENT 'Porcentaje de Comision',
  PRIMARY KEY  (`id`),
  KEY `IDX_COMMISSION_ITEM_COMMISSION` (`commission`),
  KEY `IDX_COMMISSION_ITEM_ITEM` (`item`),
  CONSTRAINT `FK_COMMISSION_ITEM_COMMISSION` FOREIGN KEY (`commission`) REFERENCES `commission` (`id`),
  CONSTRAINT `FK_COMMISSION_ITEM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones por Articulo';

#
# Structure for the `commission_type_commission` table : 
#

CREATE TABLE `commission_type_commission` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `commission_type` int(4) NOT NULL COMMENT 'Identificador del Tipo de Comision',
  `commission` int(4) NOT NULL COMMENT 'Identificador de la Comision',
  PRIMARY KEY  (`id`),
  KEY `IDX_COMMISSION_TYPE_COMMISSION_COMMISSION_TYPE` (`commission_type`),
  KEY `IDX_COMMISSION_TYPE_COMMISSION_COMMISSION` (`commission`),
  CONSTRAINT `FK_COMMISSION_TYPE_COMMISSION_COMMISSION` FOREIGN KEY (`commission`) REFERENCES `commission` (`id`),
  CONSTRAINT `FK_COMMISSION_TYPE_COMMISSION_COMMISSION_TYPE` FOREIGN KEY (`commission_type`) REFERENCES `commission_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones por Tipo de Comision';

#
# Structure for the `company` table : 
#

CREATE TABLE `company` (
  `registry` int(4) NOT NULL default '1' COMMENT 'Registro de la Compañia',
  `active` tinyint(1) default '0' COMMENT 'Indica si la Compañia es activa o inactiva',
  `surcharge` tinyint(1) default '0' COMMENT 'Indica si la Compañia tiene de recargo de equivalencia',
  `withholding` tinyint(1) default '0' COMMENT 'Indica si la Compañia aplica retencion de impuestos',
  `e_invoice` tinyint(1) default '0' COMMENT 'Indica si la Compañia desea emitir Facturas electronicas',
  PRIMARY KEY  (`registry`),
  CONSTRAINT `fk_comp_rgty` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos Corporativos';

#
# Structure for the `composition` table : 
#

CREATE TABLE `composition` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Composicion',
  `type` tinyint(2) default '0' COMMENT 'Tipo de Composicion',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de la Composicion',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador del Articulo a componer',
  `quantity` double(11,3) default '0.000' COMMENT 'Cantidad de Articulo a componer',
  `price` double(11,2) default '0.00' COMMENT 'Precio de la Composicion',
  `expenses_percent` double(11,2) default '0.00' COMMENT 'Gastos porcentuales de la Composicion',
  `expenses_fixed` double(11,3) default '0.000' COMMENT 'Gastos fijos de la Composicion',
  `price_in_details` tinyint(1) default '0' COMMENT 'Indica si el precio lo forman la suma de los detalles de la Composicion',
  PRIMARY KEY  (`id`),
  KEY `idx_cpst_item` (`item`),
  CONSTRAINT `composition_ibfk_1` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Composicion de Articulos';

#
# Structure for the `composition_detail` table : 
#

CREATE TABLE `composition_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle de la Composicion',
  `composition` int(4) NOT NULL default '0' COMMENT 'Identificador de la Composicion',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador del Articulo subproducto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Articulo subproducto',
  `quantity` double(11,3) default '0.000' COMMENT 'Cantidad de Articulo subproducto',
  `price` double(11,2) default '0.00' COMMENT 'Precio del Articulo subproducto',
  PRIMARY KEY  (`id`),
  KEY `idx_cpsd_cpst` (`composition`),
  KEY `idx_cpsd_item` (`item`),
  CONSTRAINT `composition_detail_ibfk_1` FOREIGN KEY (`composition`) REFERENCES `composition` (`id`) ON DELETE CASCADE,
  CONSTRAINT `composition_detail_ibfk_2` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de la Composicion de Articulos';

#
# Structure for the `composition_expense` table : 
#

CREATE TABLE `composition_expense` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Gasto de la Composicion',
  `composition` int(4) NOT NULL default '0' COMMENT 'Identificador de la Composicion',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Gasto',
  `quantity` double(11,3) default '0.000' COMMENT 'Cantidad del Gasto',
  `price` double(11,2) default '0.00' COMMENT 'Importe del Gasto',
  PRIMARY KEY  (`id`),
  KEY `idx_cpse_cpst` (`composition`),
  CONSTRAINT `composition_expense_ibfk_1` FOREIGN KEY (`composition`) REFERENCES `composition` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Gastos de la Composicion de Articulos';

#
# Structure for the `contact` table : 
#

CREATE TABLE `contact` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Contacto',
  `user` int(4) NOT NULL COMMENT 'Usuario al que pertenece el Contacto',
  `name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Nombre del Contacto',
  `organization` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Organizacion a la que pertenece el Contacto',
  `phone` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Telefono del Contacto',
  `cellular_phone` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Telefono movil del Contacto',
  `fax` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Fax del Contacto',
  `email` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Correo electronico del Contacto',
  `address` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Direccion del Contacto',
  `note` text collate latin1_spanish_ci COMMENT 'Notas sobre el Contacto',
  PRIMARY KEY  (`id`),
  KEY `contact_fk` (`user`),
  CONSTRAINT `contact_fk` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contactos de Usuario';

#
# Structure for the `contract_attach` table : 
#

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

#
# Structure for the `contract_batch` table : 
#

CREATE TABLE `contract_batch` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la remesa de contratos',
  `date` date NOT NULL COMMENT 'Fecha de la ultima remesa en la que fue incluido',
  `red_notify_date` date default NULL COMMENT 'Fecha de notificacion al sistema red',
  `red_notify_id` date default NULL COMMENT 'Identificador de la notificacion',
  `red_response_date` date default NULL COMMENT 'Fecha de respuesta del sistema red',
  `red_response_id` date default NULL COMMENT 'Identificador de la respuesta',
  `status` tinyint(2) NOT NULL default '0' COMMENT 'Indica el estado de la remesa',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas de contratos';

#
# Structure for the `contract_batch_attach` table : 
#

CREATE TABLE `contract_batch_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Archivo Adjunto',
  `contract_batch` int(4) NOT NULL default '0' COMMENT 'Identificador de la remesa',
  `mimeType` tinyint(2) default '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Archivo Adjunto',
  `scope` int(4) default NULL COMMENT 'Ambito del Archivo Adjunto',
  `attach_date` date default NULL COMMENT 'Fecha del Archivo Adjunto',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_BATCH_ATTACH_CONTRACT_BATCH` (`contract_batch`),
  KEY `IDX_CONTRACT_BATCH_ATTACH_SCOPE` (`scope`),
  CONSTRAINT `FK_CONTRACT_BATCH_ATTACH_CONTRACT_BATCH` FOREIGN KEY (`contract_batch`) REFERENCES `contract_batch` (`id`),
  CONSTRAINT `FK_CONTRACT_BATCH_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de remesas de mensajes afi de la s.s.';

#
# Structure for the `contract_batch_detail` table : 
#

CREATE TABLE `contract_batch_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del detalle de la remesa',
  `contract_batch` int(4) NOT NULL COMMENT 'Identificador unico de la remesa de contratos',
  `contract` int(4) NOT NULL COMMENT 'Identificador unico del contrato',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_BATCH_DETAIL_CONTRACT_BATCH` (`contract_batch`),
  KEY `IDX_CONTRACT_BATCH_DETAIL_CONTRACT` (`contract`),
  CONSTRAINT `FK_CONTRACT_BATCH_DETAIL_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_BATCH_DETAIL_CONTRACT_BATCH` FOREIGN KEY (`contract_batch`) REFERENCES `contract_batch` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las remesas de contratos';

#
# Structure for the `contract_bonus` table : 
#

CREATE TABLE `contract_bonus` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Fórmula',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `bonus_concept` int(4) default NULL COMMENT 'Concepto de bonificacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_BONUS_CONTRACT` (`contract`),
  KEY `IDX_BONUS_CONCEPT` (`bonus_concept`),
  CONSTRAINT `FK_BONUS_CONCEPT` FOREIGN KEY (`bonus_concept`) REFERENCES `bonus_concept` (`id`),
  CONSTRAINT `FK_BONUS_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bonificaciones';

#
# Structure for the `contract_calendar_event` table : 
#

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

#
# Structure for the `contract_data` table : 
#

CREATE TABLE `contract_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_CONSTANT_CONTRACT` (`contract`),
  CONSTRAINT `FK_CONTRACT_CONSTANT_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto del contrato';

#
# Structure for the `deduction_concept` table : 
#

CREATE TABLE `deduction_concept` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `code` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Codigo',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Deduccion Salarial',
  `description_decorable` tinyint(2) default '0',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos de deducciones';

#
# Structure for the `contract_deduction` table : 
#

CREATE TABLE `contract_deduction` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Deducción',
  `deduction_concept` int(4) default NULL COMMENT 'Identificador unico del concepto',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `description_decorable` tinyint(2) NOT NULL default '0',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Fórmula',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `month` tinyint(2) default NULL COMMENT 'Mes de la percepcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_DEDUCTION_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_DEDUCTION_DEDUCTION_CONCEPT` (`deduction_concept`),
  CONSTRAINT `FK_CONTRACT_DEDUCTION_DEDUCTION_CONCEPT` FOREIGN KEY (`deduction_concept`) REFERENCES `deduction_concept` (`id`),
  CONSTRAINT `FK_DEDUCTION_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Deducciones';

#
# Structure for the `contract_embargo` table : 
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Embargos';

#
# Structure for the `contract_leave` table : 
#

CREATE TABLE `contract_leave` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Baja',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `daily_cgc_base` double(15,3) default NULL COMMENT 'Base de cotizacion por contingencias comunes',
  `daily_cgp_base` double(15,3) default NULL COMMENT 'Base de cotizacion por contingencias profesionales',
  `parent` int(4) default NULL COMMENT 'Baja origen, si es recaida',
  `daily_reg_base` double(15,3) default NULL COMMENT 'Base reguladora',
  `discharge_cause` tinyint(2) default NULL COMMENT 'Causa del alta',
  PRIMARY KEY  (`id`),
  KEY `IDX_LEAVE_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_LEAVE_CONTRACT_LEAVE` (`parent`),
  CONSTRAINT `FK_CONTRACT_LEAVE_CONTRACT_LEAVE` FOREIGN KEY (`parent`) REFERENCES `contract_leave` (`id`),
  CONSTRAINT `FK_LEAVE_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bajas';

#
# Structure for the `contract_leave_detail` table : 
#

CREATE TABLE `contract_leave_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de parte',
  `contract_leave` int(4) NOT NULL COMMENT 'Contrato',
  `college_number` varchar(8) collate latin1_spanish_ci default NULL COMMENT 'Numero de colegiado',
  `confirm_order` tinyint(2) default NULL COMMENT 'Numero de orden del parte de confirmacion',
  `cias` varchar(11) collate latin1_spanish_ci default NULL COMMENT 'codigo identificacion area sanitaria',
  `date` date NOT NULL COMMENT 'Fecha del parte',
  `status` tinyint(2) NOT NULL default '0' COMMENT 'Indica el estado',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE` (`contract_leave`),
  CONSTRAINT `FK_CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE` FOREIGN KEY (`contract_leave`) REFERENCES `contract_leave` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las bajas';

#
# Structure for the `contract_payment` table : 
#

CREATE TABLE `contract_payment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Percepción Salarial',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `payment_concept` int(4) default NULL COMMENT 'Identificador unico del concepto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `description_decorable` tinyint(2) NOT NULL default '0',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe',
  `irpf_expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe tributable',
  `quote_expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe cotizable',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `month` tinyint(2) default NULL COMMENT 'Mes de la percepcion',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `salary_type` tinyint(2) default NULL COMMENT 'Tipo de Nomina/Recibo',
  PRIMARY KEY  (`id`),
  KEY `IDX_PAYMENT_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_PAYMENT_PAYMENT_CONCEPT` (`payment_concept`),
  CONSTRAINT `FK_CONTRACT_PAYMENT_PAYMENT_CONCEPT` FOREIGN KEY (`payment_concept`) REFERENCES `payment_concept` (`id`),
  CONSTRAINT `FK_PAYMENT_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Percepciones Salariales';

#
# Structure for the `cost_profile` table : 
#

CREATE TABLE `cost_profile` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `description` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion',
  `cost` double NOT NULL default '0' COMMENT 'Costo por hora',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Perfiles de Costos para Usuarios';

#
# Structure for the `course_academicskill` table : 
#

CREATE TABLE `course_academicskill` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `course` int(4) NOT NULL COMMENT 'Curso',
  `academic_skill` int(4) NOT NULL COMMENT 'Aptitud Academica',
  `weight` int(4) NOT NULL default '1' COMMENT 'Peso de la Aptitud para calcular la Nota media',
  PRIMARY KEY  (`id`),
  KEY `course` (`course`),
  KEY `academic_skill` (`academic_skill`),
  CONSTRAINT `course_academic_skill_fk_1` FOREIGN KEY (`academic_skill`) REFERENCES `academic_skill` (`id`),
  CONSTRAINT `course_academic_skill_fk_2` FOREIGN KEY (`course`) REFERENCES `course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Aptitudes Academicas por Curso';

#
# Structure for the `quality_skill` table : 
#

CREATE TABLE `quality_skill` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Aptitud Calidad',
  `code` char(5) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo de la Aptitud Calidad',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de la Aptitud Calidad',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Aptitudes Calidad';

#
# Structure for the `course_evaluation` table : 
#

CREATE TABLE `course_evaluation` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `course` int(4) NOT NULL COMMENT 'Identificador de Curso',
  `quality_skill` int(4) NOT NULL COMMENT 'Identificador de Aptitudes Calidad',
  `evaluation` double(15,3) default '0.000' COMMENT 'Evaluaciones',
  `quantity` int(4) default '0' COMMENT 'Cantidad',
  PRIMARY KEY  (`id`),
  KEY `course` (`course`),
  KEY `quality_skill` (`quality_skill`),
  CONSTRAINT `course_evaluation_skill_fk_1` FOREIGN KEY (`quality_skill`) REFERENCES `quality_skill` (`id`),
  CONSTRAINT `course_evaluation_skill_fk_2` FOREIGN KEY (`course`) REFERENCES `course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Evaluaciones por Curso';

#
# Structure for the `workactivity` table : 
#

CREATE TABLE `workactivity` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Actividad',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Actividad',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `enterpriseCCC` int(4) NOT NULL COMMENT 'Cuenta de Cotización asociada a la Actividad',
  `active` tinyint(1) default '1' COMMENT 'Indica si la Actividad esta activa o no',
  PRIMARY KEY  (`id`),
  KEY `workplace` (`workplace`),
  KEY `IDX_WORKACTIVITY_ENTERPRISECCC` (`enterpriseCCC`),
  CONSTRAINT `FK_WORKACTIVITY_ENTERPRISECCC` FOREIGN KEY (`enterpriseCCC`) REFERENCES `enterprise_ccc` (`id`),
  CONSTRAINT `workactivity_ibfk_1` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades del Centro de Trabajo';

#
# Structure for the `employee` table : 
#

CREATE TABLE `employee` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro del Empleado',
  `workactivity` int(4) default NULL COMMENT 'Identificador de Actividad',
  `social_security_num` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Número de Seguridad Social del Empleado',
  `agreement_time` int(4) default '0' COMMENT 'Horas del Convenio',
  `active` tinyint(1) default NULL COMMENT 'Indica si el Empleado sigue vinculado a la Empresa o no',
  PRIMARY KEY  (`registry`),
  UNIQUE KEY `social_security_num` (`social_security_num`),
  KEY `IDX_EMPLOYEE_WORKACTIVITY` (`workactivity`),
  CONSTRAINT `employee_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_EMPLOYEE_WORKACTIVITY` FOREIGN KEY (`workactivity`) REFERENCES `workactivity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Empleados';

#
# Structure for the `course_instructor` table : 
#

CREATE TABLE `course_instructor` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `course` int(4) NOT NULL COMMENT 'Identificador del Curso',
  `employee` int(4) NOT NULL COMMENT 'Identificador del Profesor',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Profesor',
  PRIMARY KEY  (`id`),
  KEY `course` (`course`),
  KEY `employee` (`employee`),
  CONSTRAINT `course-instructor_fk` FOREIGN KEY (`course`) REFERENCES `course` (`id`),
  CONSTRAINT `course-instructor_fk1` FOREIGN KEY (`employee`) REFERENCES `employee` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Profesores por Curso';

#
# Structure for the `course_observation` table : 
#

CREATE TABLE `course_observation` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `course` int(4) NOT NULL COMMENT 'Identificador de Curso',
  `observation` varchar(64) character set latin1 collate latin1_spanish_ci default NULL COMMENT 'Observaciones',
  PRIMARY KEY  (`id`),
  KEY `course` (`course`),
  CONSTRAINT `course_observation_skill_fk_1` FOREIGN KEY (`course`) REFERENCES `course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Observaciones por Curso';

#
# Structure for the `course_schedule` table : 
#

CREATE TABLE `course_schedule` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Horario',
  `course` int(4) NOT NULL COMMENT 'Identificador del Curso',
  `day_of_week` tinyint(2) NOT NULL COMMENT 'Dia de la semana',
  `start_time` time NOT NULL COMMENT 'Hora de comienzo',
  `end_time` time NOT NULL COMMENT 'Hora de fin',
  PRIMARY KEY  (`id`),
  KEY `course` (`course`),
  CONSTRAINT `course_schedule_fk` FOREIGN KEY (`course`) REFERENCES `course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Horarios de Cursos';

#
# Structure for the `creditor` table : 
#

CREATE TABLE `creditor` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro del Acreedor',
  `withholding` tinyint(1) default '0' COMMENT 'Indica si el Acreedor aplica retencion de impuestos',
  `transaction` tinyint(2) default '0' COMMENT 'Tipo de transacciones del Acreedor',
  `status` tinyint(2) default NULL COMMENT 'Estado del Acreedor',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  PRIMARY KEY  (`registry`),
  KEY `scope` (`scope`),
  CONSTRAINT `creditor_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `creditor_ibfk_2` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Acreedores';

#
# Structure for the `creditor_account` table : 
#

CREATE TABLE `creditor_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Cuenta Contable del Acreedor',
  `creditor` int(4) NOT NULL default '0' COMMENT 'Identificador del Acreedor',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `creditor` (`creditor`),
  KEY `account` (`account`),
  CONSTRAINT `creditor_account_ibfk_1` FOREIGN KEY (`creditor`) REFERENCES `creditor` (`registry`),
  CONSTRAINT `creditor_account_ibfk_2` FOREIGN KEY (`account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Acreedores';

#
# Structure for the `curriculum` table : 
#

CREATE TABLE `curriculum` (
  `registry` int(4) NOT NULL COMMENT 'Registro de la Persona',
  `entrydate` date NOT NULL COMMENT 'Fecha de registro del Curriculum',
  `birthdate` date default NULL COMMENT 'Fecha de nacimiento de la Persona',
  `birthplace` char(3) collate latin1_spanish_ci default NULL COMMENT 'Lugar de nacimiento de la Persona',
  `residenceplace` char(3) collate latin1_spanish_ci default NULL COMMENT 'Lugar de residencia de la Persona',
  `geozone` int(4) NOT NULL COMMENT 'Zona Geografica de residencia de la Persona',
  `city` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Ciudad de residencia de la Persona',
  `zip` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo postal de residencia de la Persona',
  `address` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Direccion de residencia de la Persona',
  `phone` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Telefono de la Persona',
  `driver_licenses` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Permiso de conducir de la Persona',
  `driver_license_date` date default NULL COMMENT 'Fecha de expedicion del Permiso de Conducir de la Persona',
  `gender` tinyint(2) default '0' COMMENT 'Sexo de la Persona',
  `postcategory` tinyint(2) default NULL COMMENT 'Rol del Curriculum dentro de la Empresa',
  PRIMARY KEY  (`registry`),
  KEY `geozone` (`geozone`),
  CONSTRAINT `curriculum_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `curriculum_ibfk_2` FOREIGN KEY (`geozone`) REFERENCES `geozone` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Curricula Vitae';

#
# Structure for the `customer_account` table : 
#

CREATE TABLE `customer_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Cuenta Contable del Cliente',
  `customer` int(4) NOT NULL default '0' COMMENT 'Identificador del Cliente',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `customer` (`customer`),
  KEY `account` (`account`),
  CONSTRAINT `customer_account_ibfk_1` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `customer_account_ibfk_2` FOREIGN KEY (`account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Clientes';

#
# Structure for the `customer_fee` table : 
#

CREATE TABLE `customer_fee` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Cuota del Cliente',
  `customer` int(4) default NULL COMMENT 'Identificador del Cliente',
  `line` smallint(2) default '1' COMMENT 'Numero de linea de Cuota',
  `item` int(4) default NULL COMMENT 'Identificador del Articulo',
  `description` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de la Cuota',
  `quantity` double(15,3) default '0.000' COMMENT 'Cantidad de la Cuota',
  `price` double default '0' COMMENT 'Precio de la Cuota',
  `discount_expr` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descuentos de la Cuota',
  `initial_date` date default NULL COMMENT 'Fecha de inicio de la Cuota',
  `final_date` date default NULL COMMENT 'Fecha de finalizacion de la Cuota',
  `billing_date` date default NULL COMMENT 'Proxima fecha de facturación de la Cuota',
  `period` smallint(2) default '1' COMMENT 'Periodo de facturacion en meses de la Cuota',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad de la Cuota',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  PRIMARY KEY  (`id`),
  KEY `customer` (`customer`),
  KEY `item` (`item`),
  KEY `workplace` (`workplace`),
  CONSTRAINT `customer_fee_ibfk_1` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `customer_fee_ibfk_2` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `customer_fee_ibfk_3` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuotas de Clientes';

#
# Structure for the `job_type` table : 
#

CREATE TABLE `job_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Tipo de Trabajo',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Trabajo',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Trabajos';

#
# Structure for the `task_holder` table : 
#

CREATE TABLE `task_holder` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro de la Entidad susceptible de Recibir Tareas',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `type` tinyint(2) default '0' COMMENT 'Tipo de Entidad susceptible de Recibir Tareas',
  `active` tinyint(1) default '1' COMMENT 'Indica si dicha Entidad esta activa o no',
  `user_id` int(4) default NULL COMMENT 'Identificador del Usuario',
  `cost_profile` int(4) default NULL COMMENT 'Identificador del Perfil de Costos',
  PRIMARY KEY  (`registry`),
  KEY `IDX_TASK_HOLDER_COST_PROFILE` (`cost_profile`),
  KEY `IDX_TASK_HOLDER_ENTERPRISE` (`enterprise`),
  KEY `IDX_TASK_HOLDER_USER` (`user_id`),
  CONSTRAINT `FK_TASK_HOLDER_COST_PROFILE` FOREIGN KEY (`cost_profile`) REFERENCES `cost_profile` (`id`),
  CONSTRAINT `FK_TASK_HOLDER_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`),
  CONSTRAINT `FK_TASK_HOLDER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_TASK_HOLDER_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Titulares de Tareas';

#
# Structure for the `task` table : 
#

CREATE TABLE `task` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Tarea',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `description` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Tarea',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio de la Tarea',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion de la Tarea',
  `due_date` date NOT NULL COMMENT 'Fecha de vencimiento de la Tarea',
  `priority` tinyint(2) default '0' COMMENT 'Prioridad de la Tarea',
  `status` tinyint(2) default '0' COMMENT 'Estado de la Tarea',
  `percent` tinyint(2) default '0' COMMENT 'Porcentaje de realizacion de la Tarea',
  `task_holder` int(4) default NULL COMMENT 'Identificador del Usuario asociado a la Tarea',
  `workgroup` int(4) default NULL COMMENT 'Identificador del Grupo de Trabajo asociado a la Tarea',
  `source` tinyint(2) default NULL COMMENT 'Origen de la Tarea',
  `project` int(4) default NULL COMMENT 'Identificador del Expediente',
  `registry` int(4) default NULL,
  `activity_type` int(4) default NULL COMMENT 'Identificador de la Actividad',
  `sender` int(4) default NULL COMMENT 'Remitente de la Tarea',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios de la Tarea',
  `repeat_period` tinyint(2) default '0' COMMENT 'Periodo de repeticion de la Tarea',
  PRIMARY KEY  (`id`),
  KEY `IDX_TASK_ACTIVITY_TYPE` (`activity_type`),
  KEY `IDX_TASK_ENTERPRISE` (`enterprise`),
  KEY `IDX_TASK_PROJECT` (`project`),
  KEY `IDX_TASK_REGISTRY` (`registry`),
  KEY `IDX_TASK_SENDER` (`sender`),
  KEY `IDX_TASK_TASK_HOLDER` (`task_holder`),
  KEY `IDX_TASK_WORKGROUP` (`workgroup`),
  CONSTRAINT `FK_TASK_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`),
  CONSTRAINT `FK_TASK_ACTIVITY_TYPE` FOREIGN KEY (`activity_type`) REFERENCES `activity_type` (`id`),
  CONSTRAINT `FK_TASK_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`),
  CONSTRAINT `FK_TASK_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_TASK_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_TASK_SENDER` FOREIGN KEY (`sender`) REFERENCES `task_holder` (`registry`),
  CONSTRAINT `FK_TASK_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tareas';

#
# Structure for the `daily_tracking` table : 
#

CREATE TABLE `daily_tracking` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Parte',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `task_holder` int(4) NOT NULL COMMENT 'Identificador del Usuario que realiza el Parte',
  `tracking_date` date NOT NULL COMMENT 'Fecha del Parte',
  `tracking_duration` double NOT NULL default '0' COMMENT 'Tiempo invertido en el Parte',
  `job_type` int(4) NOT NULL COMMENT 'Tipo de Trabajo realizado en el Parte',
  `registry` int(4) default NULL COMMENT 'Identificador del Cliente asociado al Parte',
  `project` int(4) default NULL COMMENT 'Identificador del Expediente asociado al Parte',
  `activity_type` int(4) default NULL COMMENT 'Identificador de la Actividad asociada al Parte',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios del Parte',
  `task` int(4) default NULL COMMENT 'Identificador de la Tarea que provoca el Parte',
  `cost` double default '0' COMMENT 'Costo',
  PRIMARY KEY  (`id`),
  KEY `IDX_DT_ACT_TYPE` (`activity_type`),
  KEY `IDX_DT_JOB_TYPE` (`job_type`),
  KEY `IDX_DT_PROJECT` (`project`),
  KEY `IDX_DT_REGISTRY` (`registry`),
  KEY `IDX_DT_TASK` (`task`),
  KEY `IDX_DT_TASK_HOLDER` (`task_holder`),
  KEY `IDX_DT_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_DT_ACT_TYPE` FOREIGN KEY (`activity_type`) REFERENCES `activity_type` (`id`),
  CONSTRAINT `FK_DT_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`),
  CONSTRAINT `FK_DT_JOB_TYPE` FOREIGN KEY (`job_type`) REFERENCES `job_type` (`id`),
  CONSTRAINT `FK_DT_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_DT_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_DT_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`),
  CONSTRAINT `FK_DT_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Parte Diario de Trabajo';

#
# Structure for the `db_version` table : 
#

CREATE TABLE `db_version` (
  `version_number` varchar(10) collate latin1_spanish_ci NOT NULL COMMENT 'Numero de Version de la Base de Datos',
  PRIMARY KEY  (`version_number`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Version de la Base de Datos';

#
# Structure for the `delivery` table : 
#

CREATE TABLE `delivery` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Albaran de Venta',
  `project` int(4) default NULL COMMENT 'Identificador del Proyecto',
  `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie del Albaran',
  `number` int(4) NOT NULL default '0' COMMENT 'Número del Albaran',
  `customer` int(4) NOT NULL default '0' COMMENT 'Identificador del Cliente',
  `address` int(4) default NULL COMMENT 'Identificador de la Direccion de envio del Albaran',
  `issue_time` datetime default NULL COMMENT 'Fecha de emision del Albaran',
  `pay_method` int(4) default NULL COMMENT 'Identificador de la Forma de Pago',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Albaran',
  `status` tinyint(2) default '0' COMMENT 'Estado del Albaran',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios del Albaran',
  `remarks` text collate latin1_spanish_ci COMMENT 'Observaciones del Albaran',
  `workplace` int(4) default NULL COMMENT 'Identificador del Centro de Trabajo',
  `scope` int(4) NOT NULL default '1' COMMENT 'Ambito del Albaran',
  `number_of_pymnts` smallint(2) default '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint(2) default '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint(2) default '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) collate latin1_spanish_ci default '0' COMMENT 'Dias de pago',
  `bank` int(4) default NULL COMMENT 'Identificador de la Entidad Bancaria',
  `bank_account` varchar(30) collate latin1_spanish_ci default NULL COMMENT 'Numero de cuenta en la Entidad Bancaria',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `series` (`series`,`number`),
  KEY `idx_dlvy_ctmr` (`customer`),
  KEY `idx_dlvy_radr` (`address`),
  KEY `idx_dlvy_date` (`issue_time`),
  KEY `IDX_DELIVERY_WORKPLACE` (`workplace`),
  KEY `IDX_DELIVERY_SCOPE` (`scope`),
  KEY `IDX_DELIVERY_BANK` (`bank`),
  KEY `IDX_DELIVERY_PROJECT` (`project`),
  CONSTRAINT `delivery_ibfk_1` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `delivery_ibfk_2` FOREIGN KEY (`address`) REFERENCES `raddress` (`id`),
  CONSTRAINT `FK_DELIVERY_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`),
  CONSTRAINT `FK_DELIVERY_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_DELIVERY_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_DELIVERY_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Albaranes de Venta';

#
# Structure for the `warehouse` table : 
#

CREATE TABLE `warehouse` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Almacen',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Almacen',
  `workplace` int(4) default NULL COMMENT 'Identificador del Centro de Trabajo',
  PRIMARY KEY  (`id`),
  KEY `IDX_WAREHOUSE_WORKPLACE` (`workplace`),
  CONSTRAINT `FK_WAREHOUSE_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Almacenes';

#
# Structure for the `offer_detail` table : 
#

CREATE TABLE `offer_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle de Presupuesto',
  `offer` int(4) NOT NULL COMMENT 'Identificador del Presupuesto',
  `line` smallint(2) default '1' COMMENT 'Numero de línea del Detalle dentro del Presupuesto',
  `item` int(4) default NULL COMMENT 'Identificador del Articulo',
  `description` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Descripción del Articulo',
  `quantity` double(15,3) default '0.000' COMMENT 'Cantidad del Articulo',
  `price` double default '0' COMMENT 'Precio del Articulo',
  `discount_expr` varchar(32) collate latin1_spanish_ci default '0' COMMENT 'Descuentos del Articulo',
  `status` tinyint(2) default '0' COMMENT 'Estado del Detalle del Presupuesto',
  PRIMARY KEY  (`id`),
  KEY `offer` (`offer`),
  KEY `item` (`item`),
  CONSTRAINT `offer_detail_ibfk_1` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`),
  CONSTRAINT `offer_detail_ibfk_2` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Presupuesto';

#
# Structure for the `pos` table : 
#

CREATE TABLE `pos` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Centro de Venta',
  `description` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Centro de Venta',
  `raddress` int(4) NOT NULL COMMENT 'Identificador de la Direccion asociada al Centro de Venta',
  PRIMARY KEY  (`id`),
  KEY `raddress` (`raddress`),
  CONSTRAINT `pos_ibfk_1` FOREIGN KEY (`raddress`) REFERENCES `raddress` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Centros de Venta';

#
# Structure for the `sales` table : 
#

CREATE TABLE `sales` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Pedido de Venta',
  `project` int(4) default NULL COMMENT 'Identificador del Proyecto',
  `customer` int(4) NOT NULL default '0' COMMENT 'Identificador del Cliente',
  `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie del Pedido',
  `number` int(4) NOT NULL default '0' COMMENT 'Numero del Pedido',
  `shipping_address` int(4) default NULL COMMENT 'Identificador de la Direccion de envio del Pedido',
  `seller` int(4) default NULL COMMENT 'Identificador del Agente Comercial',
  `discount_expr` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descuentos del Pedido',
  `issue_date` date default NULL COMMENT 'Fecha de emision del Pedido',
  `pay_method` int(4) default NULL COMMENT 'Identificador de la Forma de Pago',
  `document_type` tinyint(2) default '0' COMMENT 'Tipo de Pedido',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Pedido',
  `status` tinyint(2) default '0' COMMENT 'Estado del Pedido',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios del Pedido',
  `remarks` text collate latin1_spanish_ci COMMENT 'Observaciones del Pedido',
  `pos` int(4) default NULL COMMENT 'Identificador del Centro de Venta que realizo el Pedido',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `scope` int(4) NOT NULL default '1' COMMENT 'Ambito del Pedido',
  `number_of_pymnts` smallint(2) default '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint(2) default '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint(2) default '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) collate latin1_spanish_ci default '0' COMMENT 'Dias de pago',
  `bank` int(4) default NULL COMMENT 'Identificador de la Entidad Bancaria',
  `bank_account` varchar(30) collate latin1_spanish_ci default NULL COMMENT 'Numero de cuenta en la Entidad Bancaria',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `series` (`series`,`number`),
  KEY `idx_sale_ctmr` (`customer`),
  KEY `idx_sale_date` (`issue_date`),
  KEY `idx_saler_radr` (`shipping_address`),
  KEY `idx_sale_pymt` (`pay_method`),
  KEY `idx_sale_sllr` (`seller`),
  KEY `pos` (`pos`),
  KEY `workplace` (`workplace`),
  KEY `IDX_SALES_SCOPE` (`scope`),
  KEY `IDX_SALES_BANK` (`bank`),
  KEY `IDX_SALES_PROJECT` (`project`),
  CONSTRAINT `FK_SALES_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`),
  CONSTRAINT `FK_SALES_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_SALES_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `sales_ibfk_1` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`),
  CONSTRAINT `sales_ibfk_2` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `sales_ibfk_3` FOREIGN KEY (`shipping_address`) REFERENCES `raddress` (`id`),
  CONSTRAINT `sales_ibfk_4` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `sales_ibfk_5` FOREIGN KEY (`pos`) REFERENCES `pos` (`id`),
  CONSTRAINT `sales_ibfk_6` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Pedidos de Venta';

#
# Structure for the `sales_detail` table : 
#

CREATE TABLE `sales_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle del Pedido de Venta',
  `sales` int(4) NOT NULL default '0' COMMENT 'Identificador del Pedido de Venta',
  `line` smallint(2) default '1' COMMENT 'Numero de línea del Detalle dentro del Pedido',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador del Articulo del Detalle de Pedido',
  `description` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Detalle de Pedido',
  `quantity` double(15,3) default '0.000' COMMENT 'Cantidad del Detalle de Pedido',
  `price` double default '0' COMMENT 'Precio del Detalle de Pedido',
  `discount_expr` varchar(32) collate latin1_spanish_ci default '0' COMMENT 'Descuentos del Detalle de Pedido',
  `taxes` double(15,3) default '0.000' COMMENT 'Tasas del Detalle de Pedido',
  `status` tinyint(2) default '0' COMMENT 'Estado del Detalle de Pedido',
  `offer_detail` int(4) default NULL COMMENT 'Identificador del Detalle del Presupuesto Origen',
  `delivered` double default '0' COMMENT 'Cantidad entregada del Detalle de Pedido',
  PRIMARY KEY  (`id`),
  KEY `idx_sldt_item` (`item`),
  KEY `idx_sldt_sales` (`sales`),
  KEY `IDX_SALES_DETAIL_OFFER_DETAIL` (`offer_detail`),
  CONSTRAINT `FK_SALES_DETAIL_OFFER_DETAIL` FOREIGN KEY (`offer_detail`) REFERENCES `offer_detail` (`id`),
  CONSTRAINT `sales_detail_ibfk_1` FOREIGN KEY (`sales`) REFERENCES `sales` (`id`),
  CONSTRAINT `sales_detail_ibfk_2` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Pedido de Venta';

#
# Structure for the `delivery_detail` table : 
#

CREATE TABLE `delivery_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle del Albaran de Venta',
  `delivery` int(4) NOT NULL COMMENT 'Identificador del Albaran de Venta',
  `line` smallint(2) default '0' COMMENT 'Numero de linea del Detalle dentro del Albaran',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo del Detalle de Albaran',
  `description` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Detalle de Albaran',
  `warehouse` int(4) NOT NULL COMMENT 'Identificador del Almacen',
  `quantity` double(15,3) default NULL COMMENT 'Cantidad del Detalle de Albaran',
  `price` double default '0' COMMENT 'Precio del Detalle de Albaran',
  `discount_expr` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descuentos del Detalle de Albaran',
  `sales_detail` int(4) default NULL COMMENT 'Identificador del Detalle del Pedido de Venta asociado',
  PRIMARY KEY  (`id`),
  KEY `idx_dlvd_dlvy` (`delivery`),
  KEY `idx_dlvd_item` (`item`),
  KEY `idx_dlvd_wrhs` (`warehouse`),
  KEY `idx_dlvd_sldt` (`sales_detail`),
  CONSTRAINT `delivery_detail_ibfk_1` FOREIGN KEY (`delivery`) REFERENCES `delivery` (`id`),
  CONSTRAINT `delivery_detail_ibfk_2` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`),
  CONSTRAINT `delivery_detail_ibfk_3` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `delivery_detail_ibfk_4` FOREIGN KEY (`sales_detail`) REFERENCES `sales_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Albaran de Venta';

#
# Structure for the `delivery_detail_labour` table : 
#

CREATE TABLE `delivery_detail_labour` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `delivery_detail` int(4) NOT NULL COMMENT 'Identificador de la Linea de Albaran',
  `employee` int(4) default NULL COMMENT 'Identificador del Empleado',
  `quantity` double(15,3) default NULL COMMENT 'Numero de horas de mano de obra',
  PRIMARY KEY  (`id`),
  KEY `delivery_detail` (`delivery_detail`),
  KEY `employee` (`employee`),
  CONSTRAINT `delivery_detail_labour_fk1` FOREIGN KEY (`delivery_detail`) REFERENCES `delivery_detail` (`id`),
  CONSTRAINT `delivery_detail_labour_fk2` FOREIGN KEY (`employee`) REFERENCES `employee` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Horas de mano de obra asociadas a una Linea de Albaran';

#
# Structure for the `department` table : 
#

CREATE TABLE `department` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Departamento',
  `parent` int(4) default NULL COMMENT 'Identificador del Departamento padre',
  `description` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Departamento',
  PRIMARY KEY  (`id`),
  KEY `parent` (`parent`),
  CONSTRAINT `department_ibfk_1` FOREIGN KEY (`parent`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Departamentos';

#
# Structure for the `ec_catalogue` table : 
#

CREATE TABLE `ec_catalogue` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `catalogue` int(4) NOT NULL COMMENT 'Identificador del Catalogo',
  `catalogue_img` mediumblob COMMENT 'Imagen para el Catalogo',
  `catalogue_icon` blob COMMENT 'Icono del Catalogo',
  `type` tinyint(2) default '0' COMMENT 'Tipo de Catalogo',
  `visible` tinyint(1) default '0' COMMENT 'Indica si es visible en internet',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_ECCATALOGUE_CATALOGUE` (`catalogue`),
  CONSTRAINT `FK_ECCATALOGUE_CATALOGUE` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Catalogos del ECommerce';

#
# Structure for the `ec_config` table : 
#

CREATE TABLE `ec_config` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `active` tinyint(1) default '0' COMMENT 'Indica si es la configuracion activa',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Nombre del Catalogo de internet',
  `skin` tinyint(2) NOT NULL COMMENT 'Tipo de skin a utilizar',
  `header_img` mediumblob COMMENT 'Imagen de cabecera',
  `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie de los presupuestos que se van a grabar',
  `commerce` tinyint(1) default '0' COMMENT 'Indica si el ECommerce permite grabar un presupuesto',
  `show_login` tinyint(2) default '0' COMMENT 'Indica la forma de autenticarse en el ECommerce',
  `price` tinyint(2) default '0' COMMENT 'Indica la forma de mostrar los precios en el Catalogo',
  `tax_in_price` tinyint(2) default '0' COMMENT 'Indica si los precios van a mostrarse con Impuestos incluidos',
  `discount` tinyint(2) default '0' COMMENT 'Indica si adicionalmente se va a mostrar el precio original del Producto',
  `bank_transfer` int(4) default NULL COMMENT 'Forma de pago por transferencia bancaria',
  `cash_on_delivery` int(4) default NULL COMMENT 'Forma de pago por contrarreembolso',
  `visa` int(4) default NULL COMMENT 'Forma de pago con tarjeta',
  `paypal` int(4) default NULL COMMENT 'Forma de pago por paypal',
  `bank_draft` int(4) default NULL COMMENT 'Forma de pago por giro bancario',
  `legal_note1` text collate latin1_spanish_ci COMMENT 'Politica de privacidad',
  `legal_note2` text collate latin1_spanish_ci COMMENT 'Nota legal',
  `legal_note3` text collate latin1_spanish_ci COMMENT 'Proteccion de datos',
  `tariff` int(4) default NULL COMMENT 'Identificador de Tarifa para Ecommerce',
  `header_color` varchar(8) collate latin1_spanish_ci default '' COMMENT 'Color del background del header',
  `telephone` varchar(12) collate latin1_spanish_ci default '' COMMENT 'Telefono de contacto',
  `row_items` tinyint(2) default '2' COMMENT 'Numero de articulos por fila',
  `left_banner` mediumblob COMMENT 'Banner de la izquierda',
  `right_banner` mediumblob COMMENT 'Banner de la derecha',
  `welcome_banner` mediumblob COMMENT 'Banner de bienvenida',
  `ecommerce_status` tinyint(2) default '0' COMMENT 'Estado del comercio electronico',
  `shipping_costs` double(15,2) default NULL COMMENT 'Gastos de envio',
  `free_shipping` double(15,2) default NULL COMMENT 'Gastos de envio gratis a partir de esta cantidad',
  `title_note1` varchar(64) collate latin1_spanish_ci default 'Titulo de la nota legal 1',
  `title_note2` varchar(64) collate latin1_spanish_ci default 'Titulo de la nota legal 2',
  `title_note3` varchar(64) collate latin1_spanish_ci default 'Titulo de la nota legal 3',
  `email` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Email de contacto',
  PRIMARY KEY  (`id`),
  KEY `IDX_ECCONFIG_BANK_TRANSFER` (`bank_transfer`),
  KEY `IDX_ECCONFIG_PAYPAL` (`paypal`),
  KEY `IDX_ECCONFIG_BANK_DRAFT` (`bank_draft`),
  KEY `IDX_ECCONFIG_CASH_ON_DELIVERY` (`cash_on_delivery`),
  KEY `IDX_ECCONFIG_VISA` (`visa`),
  KEY `IDX_ECCONFIG_TARIFF` (`tariff`),
  CONSTRAINT `FK_ECCONFIG_BANK_DRAFT` FOREIGN KEY (`bank_draft`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_ECCONFIG_BANK_TRANSFER` FOREIGN KEY (`bank_transfer`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_ECCONFIG_CASH_ON_DELIVERY` FOREIGN KEY (`cash_on_delivery`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_ECCONFIG_PAYPAL` FOREIGN KEY (`paypal`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_ECCONFIG_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`),
  CONSTRAINT `FK_ECCONFIG_VISA` FOREIGN KEY (`visa`) REFERENCES `pay_method` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Configuracion del ECommerce';

#
# Structure for the `ec_offer_pay_info` table : 
#

CREATE TABLE `ec_offer_pay_info` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `offer` int(4) NOT NULL COMMENT 'Identificador del Presupuesto',
  `payment_status` tinyint(2) default NULL COMMENT 'Estado del pago',
  `authorization_number` int(4) default NULL COMMENT 'Numero de autorizacion',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_EC_OFFER_PAY_INFO_OFFER` (`offer`),
  CONSTRAINT `FK_EC_OFFER_PAY_INFO_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Informacion acerca de los Pagos en el ECommerce ';

#
# Structure for the `ec_paymethod` table : 
#

CREATE TABLE `ec_paymethod` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `pay_method` int(4) NOT NULL COMMENT 'Identificador de la Forma de Pago',
  `user_name` varchar(64) collate latin1_spanish_ci default 'Null' COMMENT 'Nombre de Usuario',
  `password` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Contraseña para la pasarela de pago',
  `signature` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Identificador unico de la empresa para pasarela',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_ECPAYMETHOD_PAYMETHOD` (`pay_method`),
  CONSTRAINT `FK_ECPAYMETHOD_PAYMETHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Formas de Pago del ECommerce';

#
# Structure for the `ec_target` table : 
#

CREATE TABLE `ec_target` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `login` varchar(48) collate latin1_spanish_ci NOT NULL COMMENT 'Login del Cliente Potencial',
  `password` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Password del Cliente Potencial',
  `type` tinyint(2) default '0' COMMENT 'Tipo de conexion',
  `last_access` date default NULL COMMENT 'Ultima fecha de conexion',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_ECTARGET_LOGIN` (`login`),
  KEY `IDX_ECTARGET_TARGET` (`target`),
  CONSTRAINT `FK_ECTARGET_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Clientes Potenciales del ECommerce';

#
# Structure for the `enterprise_agreement` table : 
#

CREATE TABLE `enterprise_agreement` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `agreement` int(4) NOT NULL COMMENT 'Identificador del Convenio',
  PRIMARY KEY  (`id`),
  KEY `IDX_ENTERPRISE_AGREEMENT_ENTERPRISE` (`enterprise`),
  KEY `IDX_ENTERPRISE_AGREEMENT_AGREEMENT` (`agreement`),
  CONSTRAINT `FK_ENTERPRISE_AGREEMENT_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`),
  CONSTRAINT `FK_ENTERPRISE_AGREEMENT_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Convenio de la Empresa';

#
# Structure for the `enterprise_data` table : 
#

CREATE TABLE `enterprise_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de Empresa',
  `name` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Expresion',
  `start_date` date default NULL COMMENT 'Fecha de inicio',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_ENTERPRISE_DATA_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_ENTERPRISE_DATA_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto de la Empresa';

#
# Structure for the `evaluation_observation` table : 
#

CREATE TABLE `evaluation_observation` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `alumn` int(4) NOT NULL COMMENT 'Identificador de Alumno',
  `evaluation` tinyint(2) NOT NULL COMMENT 'Numero de Evaluacion',
  `comments` text character set latin1 collate latin1_spanish_ci COMMENT 'Comentarios',
  PRIMARY KEY  (`id`),
  KEY `alumn` (`alumn`),
  CONSTRAINT `evaluation_observation_ibfk_1` FOREIGN KEY (`alumn`) REFERENCES `course_alumn` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Observaciones por Evaluacion';

#
# Structure for the `expenditures_items` table : 
#

CREATE TABLE `expenditures_items` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Tipo de Coste',
  `name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Nombre del Tipo de Coste',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='Tipos de Costes por Empleado';

#
# Structure for the `resource` table : 
#

CREATE TABLE `resource` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Recurso',
  `employee` int(4) NOT NULL COMMENT 'Identificador del Empleado',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `workactivity` int(4) default NULL COMMENT 'Identificador de la Actividad',
  `startingdate` date NOT NULL COMMENT 'Fecha inicial del Recurso',
  `endingdate` date default NULL COMMENT 'Fecha final del Recurso',
  PRIMARY KEY  (`id`),
  KEY `employee` (`employee`),
  KEY `workplace` (`workplace`),
  KEY `workactivity` (`workactivity`),
  KEY `resource_idx` (`employee`,`endingdate`),
  CONSTRAINT `resource_ibfk_1` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`),
  CONSTRAINT `resource_ibfk_2` FOREIGN KEY (`workactivity`) REFERENCES `workactivity` (`id`),
  CONSTRAINT `resource_ibfk_3` FOREIGN KEY (`employee`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Recursos de Empresa';

#
# Structure for the `expenditures` table : 
#

CREATE TABLE `expenditures` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Coste',
  `resource` int(4) default NULL COMMENT 'Identificador del Recurso de Empresa',
  `expenditures_item` int(4) default NULL COMMENT 'Identificador del Tipo de Coste',
  `date` date default NULL COMMENT 'Fecha del Coste',
  `amount` double(15,3) default NULL COMMENT 'Importe del Coste',
  PRIMARY KEY  (`id`),
  KEY `expenditures_item` (`expenditures_item`),
  KEY `resource` (`resource`),
  CONSTRAINT `expenditures_ibfk_1` FOREIGN KEY (`expenditures_item`) REFERENCES `expenditures_items` (`id`),
  CONSTRAINT `expenditures_ibfk_2` FOREIGN KEY (`resource`) REFERENCES `resource` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=FIXED COMMENT='Costes por Empleado';

#
# Structure for the `fan_batch` table : 
#

CREATE TABLE `fan_batch` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la remesa',
  `date` date NOT NULL COMMENT 'Fecha de la ultima remesa en la que fue incluido',
  `status` tinyint(2) NOT NULL default '0' COMMENT 'Indica el estado de la remesa',
  `liquidation_type` tinyint(2) NOT NULL default '0' COMMENT 'Indica el tipo de liquidacion',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas del fichero fan';

#
# Structure for the `fan_batch_attach` table : 
#

CREATE TABLE `fan_batch_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Archivo Adjunto',
  `fan_batch` int(4) NOT NULL default '0' COMMENT 'Identificador de la remesa',
  `mimeType` tinyint(2) default '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Archivo Adjunto',
  `scope` int(4) default NULL COMMENT 'Ambito del Archivo Adjunto',
  `attach_date` date default NULL COMMENT 'Fecha del Archivo Adjunto',
  PRIMARY KEY  (`id`),
  KEY `IDX_FAN_BATCH_ATTACH_FAN_BATCH` (`fan_batch`),
  KEY `IDX_FAN_BATCH_ATTACH_SCOPE` (`scope`),
  CONSTRAINT `FK_FAN_BATCH_ATTACH_FAN_BATCH` FOREIGN KEY (`fan_batch`) REFERENCES `fan_batch` (`id`),
  CONSTRAINT `FK_FAN_BATCH_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de remesas de mensajes fan de la s.s.';

#
# Structure for the `fan_batch_detail` table : 
#

CREATE TABLE `fan_batch_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del detalle',
  `fan_batch` int(4) NOT NULL COMMENT 'Identificador unico de la remesa',
  `enterprise_ccc` int(4) NOT NULL COMMENT 'Identificador unico del ccc',
  PRIMARY KEY  (`id`),
  KEY `IDX_FAN_BATCH_DETAIL_FAN_BATCH` (`fan_batch`),
  KEY `IDX_FAN_BATCH_DETAIL_ENTERPRISE_CCC` (`enterprise_ccc`),
  CONSTRAINT `FK_FAN_BATCH_DETAIL_ENTERPRISE_CCC` FOREIGN KEY (`enterprise_ccc`) REFERENCES `enterprise_ccc` (`id`),
  CONSTRAINT `FK_FAN_BATCH_DETAIL_FAN_BATCH` FOREIGN KEY (`fan_batch`) REFERENCES `fan_batch` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las remesas del fichero fan';

#
# Structure for the `favorite_category` table : 
#

CREATE TABLE `favorite_category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Categoria',
  `user_id` int(4) NOT NULL COMMENT 'Usuario al que pertenece la Categoria',
  PRIMARY KEY  (`id`),
  KEY `user` (`user_id`),
  CONSTRAINT `favorite_category_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias de Favoritos de Usuario';

#
# Structure for the `favorite` table : 
#

CREATE TABLE `favorite` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de Favorito',
  `favorite_category` int(4) NOT NULL COMMENT 'Categoria a la que pertenece el Favorito',
  `description` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Favorito',
  `url` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Url del Favorito',
  `user_id` int(4) NOT NULL COMMENT 'Usuario al que pertenece el Favorito',
  PRIMARY KEY  (`id`),
  KEY `favorite_category` (`favorite_category`),
  KEY `user` (`user_id`),
  CONSTRAINT `favorite_fk` FOREIGN KEY (`favorite_category`) REFERENCES `favorite_category` (`id`),
  CONSTRAINT `favorite_fk1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Favoritos de Usuario';

#
# Structure for the `fbatch_detail` table : 
#

CREATE TABLE `fbatch_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle de la Remesa',
  `fbatch` int(4) NOT NULL COMMENT 'Identificador de la Remesa',
  `finance` int(4) NOT NULL COMMENT 'Identificador del Vencimiento',
  `amount` double(15,3) default '0.000' COMMENT 'Importe del Detalle de la Remesa',
  `status` tinyint(2) default NULL COMMENT 'Estado del Detalle de la Remesa',
  PRIMARY KEY  (`id`),
  KEY `finance` (`finance`),
  KEY `fbatch` (`fbatch`),
  CONSTRAINT `fbatch_detail_ibfk_1` FOREIGN KEY (`finance`) REFERENCES `finance` (`id`),
  CONSTRAINT `fbatch_detail_ibfk_2` FOREIGN KEY (`fbatch`) REFERENCES `fbatch` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de la Remesa';

#
# Structure for the `fs_mod347` table : 
#

CREATE TABLE `fs_mod347` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `administration` tinyint(2) default '0' COMMENT 'Administracion',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `status` tinyint(2) default '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad',
  `complementary` tinyint(1) default '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) default '0' COMMENT 'Declaracion sustitutiva',
  `number` int(4) default '0' COMMENT 'Numero de Decl.',
  `replaced_number` int(4) default '0' COMMENT 'Numero de Decl. complementada o sustituida',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaracion de Model 347';

#
# Structure for the `fs_mod347_detail` table : 
#

CREATE TABLE `fs_mod347_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `fs_mod347` int(4) NOT NULL default '0' COMMENT 'Identificador de la Declaracion',
  `type` varchar(1) collate latin1_spanish_ci default '0' COMMENT 'Clave de operacion',
  `document` varchar(9) collate latin1_spanish_ci default NULL COMMENT 'NIF del declarado',
  `registry` int(4) default '0' COMMENT 'Identificador del Declarado',
  `name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Apellidos  y Nombre del declarado',
  `province` int(4) default '0' COMMENT 'Provincia del declarado',
  `country` varchar(2) collate latin1_spanish_ci default '0' COMMENT 'Pais del declarado',
  `amount` double(15,3) default '0.000' COMMENT 'Importe de las operaciones',
  PRIMARY KEY  (`id`),
  KEY `IDX_FS_MOD347_DETAIL_FS_MOD347` (`fs_mod347`),
  CONSTRAINT `FK_FS_MOD347_DETAIL_FS_MOD347` FOREIGN KEY (`fs_mod347`) REFERENCES `fs_mod347` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de la Declaracion 347';

#
# Structure for the `fs_prof_retention` table : 
#

CREATE TABLE `fs_prof_retention` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `enterprise` int(4) NOT NULL default '0' COMMENT 'Identificador de Empresa',
  `payment_date` date NOT NULL COMMENT 'Fecha de Pago',
  `document` varchar(16) collate latin1_spanish_ci NOT NULL COMMENT 'Numero de Documento del Profesional',
  `document_type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de documento (NIF, CIF...) del Profesional',
  `document_country` varchar(2) collate latin1_spanish_ci NOT NULL default 'ES' COMMENT 'Pais del documento del Profesional',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre completo del Profesional',
  `concept` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Concepto',
  `taxable_base` double default '0' COMMENT 'Base Imponible',
  `percent` double(15,3) default '0.000' COMMENT 'Porcentaje de  retencion',
  `quota` double default '0' COMMENT 'Cuota de retencion',
  `in_kind` tinyint(1) default '0' COMMENT 'Indica si el importe es en especie (1) o dinerario (0)',
  `withholding_key` varchar(2) collate latin1_spanish_ci NOT NULL COMMENT 'Clave de retencion',
  `withholding_subkey` varchar(3) collate latin1_spanish_ci default NULL COMMENT 'Subclave de retencion',
  PRIMARY KEY  (`id`),
  KEY `IDX_FS_PROF_RET_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_FS_PROF_RET_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Retenciones de profesionales';

#
# Structure for the `fs_renting` table : 
#

CREATE TABLE `fs_renting` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `period` tinyint(2) default '0' COMMENT 'Periodo de la Declaracion',
  `administration` tinyint(2) default '0' COMMENT 'Administracion',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `status` tinyint(2) default '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad',
  `complementary` tinyint(1) default '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) default '0' COMMENT 'Declaracion sustitutiva',
  `lessor_count_accumulated` double(15,3) default '0.000' COMMENT 'Numero de arrendadores acumulado',
  `lessor_count_declared` double(15,3) default '0.000' COMMENT 'Numero de arrendadores declarado',
  `lessor_count_result` double(15,3) default '0.000' COMMENT 'Numero de arrendadores resultado',
  `lessor_count_adjust` double(15,3) default '0.000' COMMENT 'Numero de arrendadores ajuste',
  `lessor_count` double(15,3) default '0.000' COMMENT 'Numero de arrendadores',
  `renting_amount_accumulated` double(15,3) default '0.000' COMMENT 'Importe de los arrendamientos acumulado',
  `renting_amount_declared` double(15,3) default '0.000' COMMENT 'Importe de los arrendamientos declarado',
  `renting_amount_result` double(15,3) default '0.000' COMMENT 'Importe de los arrendamientos resultado',
  `renting_amount_adjust` double(15,3) default '0.000' COMMENT 'Importe de los arrendamientos ajuste',
  `renting_amount` double(15,3) default '0.000' COMMENT 'Importe de los arrendamientos',
  `retention_accumulated` double(15,3) default '0.000' COMMENT 'Importe de la retencion acumulado',
  `retention_declared` double(15,3) default '0.000' COMMENT 'Importe de la retencion declarado',
  `retention_result` double(15,3) default '0.000' COMMENT 'Importe de la retencion resultado',
  `retention_adjust` double(15,3) default '0.000' COMMENT 'Importe de la retencion ajuste',
  `retention` double(15,3) default '0.000' COMMENT 'Importe de la retencion',
  `lessor_count_in_kind_accumulated` double(15,3) default '0.000' COMMENT 'Numero de arrendadores (especie) acumulado',
  `lessor_count_in_kind_declared` double(15,3) default '0.000' COMMENT 'Numero de arrendadores (especie) declarado',
  `lessor_count_in_kind_result` double(15,3) default '0.000' COMMENT 'Numero de arrendadores (especie) resultado',
  `lessor_count_in_kind_adjust` double(15,3) default '0.000' COMMENT 'Numero de arrendadores (especie) ajuste',
  `lessor_count_in_kind` double(15,3) default '0.000' COMMENT 'Numero de arrendadores (especie)',
  `remuneration_in_kind_accumulated` double(15,3) default '0.000' COMMENT 'Retribucion en especie acumulado',
  `remuneration_in_kind_declared` double(15,3) default '0.000' COMMENT 'Retribucion en especie declarado',
  `remuneration_in_kind_result` double(15,3) default '0.000' COMMENT 'Retribucion en especie resultado',
  `remuneration_in_kind_adjust` double(15,3) default '0.000' COMMENT 'Retribucion en especie ajuste',
  `remuneration_in_kind` double(15,3) default '0.000' COMMENT 'Retribucion en especie',
  `account_deposit_accumulated` double(15,3) default '0.000' COMMENT 'Ingresos a cuenta acumulado',
  `account_deposit_declared` double(15,3) default '0.000' COMMENT 'Ingresos a cuenta declarado',
  `account_deposit_result` double(15,3) default '0.000' COMMENT 'Ingresos a cuenta resultado',
  `account_deposit_adjust` double(15,3) default '0.000' COMMENT 'Ingresos a cuenta ajuste',
  `account_deposit` double(15,3) default '0.000' COMMENT 'Ingresos a cuenta',
  `extra_charge` double(15,3) default '0.000' COMMENT 'Recargo',
  `delay_interest` double(15,3) default '0.000' COMMENT 'Intereses de demora',
  `total_tax_debt` double(15,3) default '0.000' COMMENT 'Total deuda tributaria',
  `rbank` int(4) default NULL COMMENT 'Banco de la Compañia',
  PRIMARY KEY  (`id`),
  KEY `IDX_FS_RENTING_RBANK` (`rbank`),
  CONSTRAINT `FK_FS_RENTING_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaracion de IRPF';

#
# Structure for the `fs_renting_detail` table : 
#

CREATE TABLE `fs_renting_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `fs_renting` int(4) NOT NULL default '0' COMMENT 'Identificador de la Declaracion',
  `type` tinyint(2) default '0' COMMENT 'Modalidad',
  `document` varchar(9) collate latin1_spanish_ci default NULL COMMENT 'NIF',
  `name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Apellidos  y Nombre',
  `paid_returns` double(15,3) default '0.000' COMMENT 'Rendimientos satisfechos',
  `percent` double(15,3) default '0.000' COMMENT 'Porcentaje de retencion',
  `account_deposit` double(15,3) default '0.000' COMMENT 'Ingresos a cuenta',
  `accrual_period` int(4) default '0' COMMENT 'Periodo de devengo',
  `address` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Direccion',
  `city` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Municipio',
  `province` varchar(2) collate latin1_spanish_ci default NULL COMMENT 'Provincia',
  PRIMARY KEY  (`id`),
  KEY `IDX_FS_RENTING_DETAIL_FS_RENTING` (`fs_renting`),
  CONSTRAINT `FK_FS_RENTING_DETAIL_FS_RENTING` FOREIGN KEY (`fs_renting`) REFERENCES `fs_renting` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de la Declaracion de IRPF';

#
# Structure for the `fs_vat` table : 
#

CREATE TABLE `fs_vat` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `period` tinyint(2) default '0' COMMENT 'Periodo de la Declaracion',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `status` tinyint(2) default '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad',
  `complementary` tinyint(1) default '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) default '0' COMMENT 'Declaracion sustitutiva',
  `tax_refund_registry` tinyint(1) default '0' COMMENT 'Inscrito en registro de devolucion',
  `number` int(4) default '0' COMMENT 'Numero de Decl. complementaria o sustitutiva',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaracion de Iva';

#
# Structure for the `fs_vat_declaration` table : 
#

CREATE TABLE `fs_vat_declaration` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `fs_vat` int(4) NOT NULL default '0' COMMENT 'Identificador de la Declaracion',
  `without_activity` tinyint(1) default '0' COMMENT 'Sin actividad',
  `administration` tinyint(2) default '0' COMMENT 'Administracion',
  `percent` double default '0' COMMENT 'Porcentaje atribuible',
  `operations_volume` double(15,3) default '0.000' COMMENT 'Volumen de operaciones',
  `quota` double(15,3) default '0.000' COMMENT 'Cuota atribuible',
  `prev_year_compensate_quota` double(15,3) default '0.000' COMMENT 'Cuota a compensar de ejerc. anteriores',
  `done_deposits` double(15,3) default '0.000' COMMENT 'Ingresos efectuados',
  `done_refunds` double(15,3) default '0.000' COMMENT 'Devoluciones practicadas',
  `extra_charge` double(15,3) default '0.000' COMMENT 'Recargo',
  `delay_interest` double(15,3) default '0.000' COMMENT 'Intereses de demora',
  `compensate` double(15,3) default '0.000' COMMENT 'A compensar',
  `pay_back` double(15,3) default '0.000' COMMENT 'A devolver',
  `deposit` double(15,3) default '0.000' COMMENT 'A ingresar',
  `prev_deposit` double(15,3) default '0.000' COMMENT 'Ingresado anteriormente',
  `prev_pay_back` double(15,3) default '0.000' COMMENT 'Devuelto anteriormente',
  `total_tax_debt` double(15,3) default '0.000' COMMENT 'Total deuda tributaria',
  `rbank` int(4) default NULL COMMENT 'Banco de la Compañia',
  `compensable` tinyint(1) default '0' COMMENT 'Compensar o devolver',
  `status` tinyint(2) NOT NULL default '0' COMMENT 'Estado de la Declaracion',
  PRIMARY KEY  (`id`),
  KEY `IDX_FS_VAT_DECLARATION_FS_VAT` (`fs_vat`),
  KEY `IDX_FS_VAT_DECLARATION_RBANK` (`rbank`),
  CONSTRAINT `FK_FS_VAT_DECLARATION_FS_VAT` FOREIGN KEY (`fs_vat`) REFERENCES `fs_vat` (`id`),
  CONSTRAINT `FK_FS_VAT_DECLARATION_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Resultado de la Declaracion de Iva';

#
# Structure for the `fs_vat_detail` table : 
#

CREATE TABLE `fs_vat_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `fs_vat` int(4) NOT NULL default '0' COMMENT 'Identificador de la Declaracion',
  `vat_key` varchar(3) collate latin1_spanish_ci NOT NULL COMMENT 'Clave de la Declaracion',
  `percent` double default '0' COMMENT 'Porcentaje de Iva',
  `taxable_base` double(15,3) default '0.000' COMMENT 'Base imponible',
  `quota` double(15,3) default '0.000' COMMENT 'Cuota',
  `deductible_quota` double(15,3) default '0.000' COMMENT 'Cuota deducible',
  `adj_taxable_base` double(15,3) default '0.000' COMMENT 'Base imponible ajustada',
  `adj_quota` double(15,3) default '0.000' COMMENT 'Cuota ajustada',
  `adj_deductible_quota` double(15,3) default '0.000' COMMENT 'Cuota deducible ajustada',
  `acu_taxable_base` double(15,3) default '0.000' COMMENT 'Base imponible acumulada',
  `acu_quota` double(15,3) default '0.000' COMMENT 'Cuota acumulada',
  `acu_deductible_quota` double(15,3) default '0.000' COMMENT 'Cuota deducible acumulada',
  `dec_taxable_base` double(15,3) default '0.000' COMMENT 'Base imponible declarado',
  `dec_quota` double(15,3) default '0.000' COMMENT 'Cuota declarado',
  `dec_deductible_quota` double(15,3) default '0.000' COMMENT 'Cuota deducible declarado',
  `res_taxable_base` double(15,3) default '0.000' COMMENT 'Base imponible resultado',
  `res_quota` double(15,3) default '0.000' COMMENT 'Cuota resultado',
  `res_deductible_quota` double(15,3) default '0.000' COMMENT 'Cuota deducible resultado',
  PRIMARY KEY  (`id`),
  KEY `IDX_FS_DETAIL_FS_VAT` (`fs_vat`),
  CONSTRAINT `FK_FS_DETAIL_FS_VAT` FOREIGN KEY (`fs_vat`) REFERENCES `fs_vat` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de Declaracion de Iva';

#
# Structure for the `geotree` table : 
#

CREATE TABLE `geotree` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `parent` int(4) default NULL COMMENT 'Identificador de la Zona Geografica Padre',
  `child` int(4) NOT NULL COMMENT 'Identificador de la Zona Geografica Hijo',
  PRIMARY KEY  (`id`),
  KEY `parent` (`parent`),
  KEY `child` (`child`),
  CONSTRAINT `geotree_fk1` FOREIGN KEY (`parent`) REFERENCES `geozone` (`id`),
  CONSTRAINT `geotree_fk2` FOREIGN KEY (`child`) REFERENCES `geozone` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Jerarquia de Zonas Geograficas';

#
# Structure for the `geozone_irpf` table : 
#

CREATE TABLE `geozone_irpf` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `geozone` int(4) NOT NULL COMMENT 'Identificador de la Zona Geografica',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `amount` double(15,3) default '0.000' COMMENT 'Importe rendimiento anual',
  PRIMARY KEY  (`id`),
  KEY `IDX_GEOZONE_IRPF_GEOZONE` (`geozone`),
  CONSTRAINT `FK_GEOZONE_IRPF_GEOZONE` FOREIGN KEY (`geozone`) REFERENCES `geozone` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tabla de tramos del IRPF';

#
# Structure for the `geozone_irpf_descendant` table : 
#

CREATE TABLE `geozone_irpf_descendant` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `geozone_irpf` int(4) default NULL COMMENT 'Identificador del tramo de IRPF',
  `descendant` tinyint(2) default '0' COMMENT 'Descendientes',
  `percent` double(15,2) default '0.00' COMMENT 'Porcentaje',
  PRIMARY KEY  (`id`),
  KEY `IDX_GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF` (`geozone_irpf`),
  CONSTRAINT `FK_GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF` FOREIGN KEY (`geozone_irpf`) REFERENCES `geozone_irpf` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tabla de porcentajes IRPF segun descendientes';

#
# Structure for the `geozone_irpf_handicap` table : 
#

CREATE TABLE `geozone_irpf_handicap` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `geozone_irpf` int(4) default NULL COMMENT 'Identificador del tramo de IRPF',
  `handicap` tinyint(2) default '0' COMMENT 'Grado Minusvalia',
  `percent` double(15,2) default '0.00' COMMENT 'Porcentaje',
  PRIMARY KEY  (`id`),
  KEY `IDX_GEOZONE_IRPF_PERCENT_GEOZONE_IRPF` (`geozone_irpf`),
  CONSTRAINT `FK_GEOZONE_IRPF_PERCENT_GEOZONE_IRPF` FOREIGN KEY (`geozone_irpf`) REFERENCES `geozone_irpf` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tabla de  ';

#
# Structure for the `holiday_detail` table : 
#

CREATE TABLE `holiday_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `holiday` int(4) NOT NULL COMMENT 'Identificador de Festividad',
  `date` date NOT NULL COMMENT 'Fecha Festiva',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de Festividad',
  PRIMARY KEY  (`id`),
  KEY `IDX_HOLIDAY_DETAIL_HOLIDAY` (`holiday`),
  CONSTRAINT `FK_HOLIDAY_DETAIL_HOLIDAY` FOREIGN KEY (`holiday`) REFERENCES `holiday` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de Festividades';

#
# Structure for the `iattach` table : 
#

CREATE TABLE `iattach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Archivo Adjunto del Articulo',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador de Articulo',
  `mimeType` tinyint(2) default NULL COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Archivo Adjunto',
  PRIMARY KEY  (`id`),
  KEY `item` (`item`),
  CONSTRAINT `iattach_ibfk_1` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Articulos';

#
# Structure for the `incidence_type` table : 
#

CREATE TABLE `incidence_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de Tipo de Incidencia',
  `alias` varchar(3) collate latin1_spanish_ci NOT NULL COMMENT 'Alias del Tipo de Incidencia',
  `description` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripción del Tipo de Incidencia',
  `compute` tinyint(1) default '0' COMMENT 'Indica la forma de computar las horas de la Incidencia',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Incidencias Laborales';

#
# Structure for the `income` table : 
#

CREATE TABLE `income` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Albaran de Compra',
  `project` int(4) default NULL COMMENT 'Identificador del Proyecto',
  `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie del Albaran',
  `number` int(4) NOT NULL default '0' COMMENT 'Numero del Albaran',
  `supplier` int(4) NOT NULL default '0' COMMENT 'Identificador del Proveedor',
  `address` int(4) default NULL COMMENT 'Identificador de la Direccion del Proveedor',
  `issue_time` date default NULL COMMENT 'Fecha de emision del Albaran',
  `pay_method` int(4) default NULL COMMENT 'Identificador de la Forma de Pago',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Albaran',
  `status` tinyint(2) default '0' COMMENT 'Estado del Albaran',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios del Albaran',
  `remarks` text collate latin1_spanish_ci COMMENT 'Observaciones del Albaran',
  `workplace` int(4) default NULL COMMENT 'Identificador del Centro de Trabajo',
  `scope` int(4) NOT NULL default '1' COMMENT 'Ambito del Albaran',
  `number_of_pymnts` smallint(2) default '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint(2) default '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint(2) default '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) collate latin1_spanish_ci default '0' COMMENT 'Dias de pago',
  `bank` int(4) default NULL COMMENT 'Identificador de la Entidad Bancaria',
  `bank_account` varchar(30) collate latin1_spanish_ci default NULL COMMENT 'Numero de cuenta en la Entidad Bancaria',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_supplier` (`supplier`,`series`,`number`),
  KEY `supplier` (`supplier`),
  KEY `address` (`address`),
  KEY `IDX_INCOME_WORKPLACE` (`workplace`),
  KEY `IDX_INCOME_SCOPE` (`scope`),
  KEY `IDX_INCOME_BANK` (`bank`),
  KEY `IDX_INCOME_PROJECT` (`project`),
  CONSTRAINT `FK_INCOME_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`),
  CONSTRAINT `FK_INCOME_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_INCOME_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_INCOME_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`),
  CONSTRAINT `income_ibfk_1` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`),
  CONSTRAINT `income_ibfk_2` FOREIGN KEY (`address`) REFERENCES `raddress` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Albaranes de Compra';

#
# Structure for the `purchase` table : 
#

CREATE TABLE `purchase` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Pedido de Compra',
  `project` int(4) default NULL COMMENT 'Identificador del Proyecto',
  `supplier` int(4) NOT NULL default '0' COMMENT 'Identificador del Proveedor',
  `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie del Pedido',
  `number` int(4) NOT NULL default '0' COMMENT 'Numero del Pedido',
  `address` int(4) default NULL COMMENT 'Identificador de la Direccion del Proveedor',
  `discount_expr` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descuentos del Pedido',
  `issue_date` date default NULL COMMENT 'Fecha de emision del Pedido',
  `pay_method` int(4) default NULL COMMENT 'Identificador de la Forma de Pago',
  `document_type` tinyint(2) default '1' COMMENT 'Tipo de Pedido',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Pedido',
  `status` tinyint(2) default '0' COMMENT 'Estado del Pedido',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios del Pedido',
  `remarks` text collate latin1_spanish_ci COMMENT 'Observaciones del Pedido',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `scope` int(4) NOT NULL default '1' COMMENT 'Ambito del Pedido',
  `number_of_pymnts` smallint(2) default '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint(2) default '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint(2) default '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) collate latin1_spanish_ci default '0' COMMENT 'Dias de pago',
  `bank` int(4) default NULL COMMENT 'Identificador de la Entidad Bancaria',
  `bank_account` varchar(30) collate latin1_spanish_ci default NULL COMMENT 'Numero de cuenta en la Entidad Bancaria',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_supplier` (`supplier`,`series`,`number`),
  KEY `supplier` (`supplier`),
  KEY `pay_method` (`pay_method`),
  KEY `workplace` (`workplace`),
  KEY `IDX_PURCHASE_SCOPE` (`scope`),
  KEY `IDX_PURCHASE_BANK` (`bank`),
  KEY `IDX_PURCHASE_PROJECT` (`project`),
  CONSTRAINT `FK_PURCHASE_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`),
  CONSTRAINT `FK_PURCHASE_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PURCHASE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `purchase_ibfk_1` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`),
  CONSTRAINT `purchase_ibfk_2` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `purchase_ibfk_3` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Pedidos de Compra';

#
# Structure for the `purchase_detail` table : 
#

CREATE TABLE `purchase_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle del Pedido de Compra',
  `purchase` int(4) NOT NULL default '0' COMMENT 'Identificador del Pedido de Compra',
  `project` int(4) default NULL COMMENT 'Identificador del Proyecto',
  `line` smallint(2) default '1' COMMENT 'Numero de linea del Detalle dentro del Pedido',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador del Articulo del Detalle de Pedido',
  `description` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Detalle de Pedido',
  `quantity` double(15,3) default '0.000' COMMENT 'Cantidad del Detalle de Pedido',
  `price` double default '0' COMMENT 'Precio del Detalle de Pedido',
  `discount_expr` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descuentos del Detalle de Pedido',
  `taxes` double(15,3) default '0.000' COMMENT 'Tasas del Detalle de Pedido',
  `status` tinyint(2) default NULL COMMENT 'Estado del Detalle de Pedido',
  `delivered` double default '0' COMMENT 'Cantidad entregada del Detalle de Pedido',
  PRIMARY KEY  (`id`),
  KEY `purchase` (`purchase`),
  KEY `item` (`item`),
  KEY `IDX_PURCHASE_DETAIL_PROJECT` (`project`),
  CONSTRAINT `FK_PURCHASE_DETAIL_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `purchase_detail_ibfk_1` FOREIGN KEY (`purchase`) REFERENCES `purchase` (`id`),
  CONSTRAINT `purchase_detail_ibfk_2` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Pedido de Compra';

#
# Structure for the `income_detail` table : 
#

CREATE TABLE `income_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle del Albaran de Compra',
  `income` int(4) NOT NULL default '0' COMMENT 'Identificador del Albaran de Compra',
  `project` int(4) default NULL COMMENT 'Identificador del Proyecto',
  `line` smallint(2) default '0' COMMENT 'Numero de linea del Detalle dentro del Albaran',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo del Detalle de Albaran',
  `description` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Detalle de Albaran',
  `warehouse` int(4) NOT NULL COMMENT 'Identificador del Almacen',
  `quantity` double(15,3) default NULL COMMENT 'Cantidad del Detalle de Albaran',
  `price` double default '0' COMMENT 'Precio del Detalle de Albaran',
  `discount_expr` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descuentos del Detalle de Albaran',
  `purchase_detail` int(4) default NULL COMMENT 'Identificador del Detalle del Pedido de Compra asociado',
  PRIMARY KEY  (`id`),
  KEY `item` (`item`),
  KEY `warehouse` (`warehouse`),
  KEY `purchase_detail` (`purchase_detail`),
  KEY `income` (`income`),
  KEY `IDX_INCOME_DETAIL_PROJECT` (`project`),
  CONSTRAINT `FK_INCOME_DETAIL_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `income_detail_ibfk_2` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `income_detail_ibfk_3` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`),
  CONSTRAINT `income_detail_ibfk_4` FOREIGN KEY (`purchase_detail`) REFERENCES `purchase_detail` (`id`),
  CONSTRAINT `income_detail_ibfk_5` FOREIGN KEY (`income`) REFERENCES `income` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Albaran de Compra';

#
# Structure for the `inventory` table : 
#

CREATE TABLE `inventory` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Inventario',
  `inventory_date` date NOT NULL default '0000-00-00' COMMENT 'Fecha de Inventario',
  `warehouse` int(4) NOT NULL default '0' COMMENT 'Almacen Inventariado',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Inventario',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Inventarios de Almacenes';

#
# Structure for the `inventory_detail` table : 
#

CREATE TABLE `inventory_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle del Inventario',
  `inventory` int(4) NOT NULL default '0' COMMENT 'Identificador de Inventario',
  `item` int(4) NOT NULL default '0' COMMENT 'Articulo Inventariado',
  `actual_quantity` double(15,3) default '0.000' COMMENT 'Cantidad actual del Articulo Inventariado',
  `real_quantity` double(15,3) default '0.000' COMMENT 'Cantidad real del Articulo Inventariado',
  `cost` double(15,3) default '0.000' COMMENT 'Coste del Articulo Inventariado',
  PRIMARY KEY  (`id`),
  KEY `idx_invn_invn` (`inventory`),
  KEY `idx_invn_item` (`item`),
  CONSTRAINT `inventory_detail_ibfk_1` FOREIGN KEY (`inventory`) REFERENCES `inventory` (`id`),
  CONSTRAINT `inventory_detail_ibfk_2` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Inventarios de Almacenes';

#
# Structure for the `invoice_address` table : 
#

CREATE TABLE `invoice_address` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `invoice` int(4) NOT NULL COMMENT 'Identificador de la Factura',
  `street_type` varchar(2) collate latin1_spanish_ci default 'CL' COMMENT 'Tipo de via',
  `address` varchar(45) collate latin1_spanish_ci default NULL COMMENT 'Primera parte de la Direccion',
  `number` varchar(12) collate latin1_spanish_ci default NULL COMMENT 'Numero',
  `address2` varchar(45) collate latin1_spanish_ci default NULL COMMENT 'Segunda parte de la Direccion',
  `zip` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo Postal',
  `city` varchar(45) collate latin1_spanish_ci default NULL COMMENT 'Localidad',
  `geozone` int(4) NOT NULL COMMENT 'Identificador de la Zona Geografica',
  PRIMARY KEY  (`id`),
  KEY `invoice` (`invoice`),
  KEY `geozone` (`geozone`),
  CONSTRAINT `invoice_address_fk` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`),
  CONSTRAINT `invoice_address_fk1` FOREIGN KEY (`geozone`) REFERENCES `geozone` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Direcciones de la Factura';

#
# Structure for the `invoice_attach` table : 
#

CREATE TABLE `invoice_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `invoice` int(4) NOT NULL COMMENT 'Identificador de la Factura',
  `mimeType` tinyint(2) default '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  PRIMARY KEY  (`id`),
  KEY `IDX_INVOICE_ATTACH_INVOICE` (`invoice`),
  CONSTRAINT `FK_INVOICE_ATTACH_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Facturas';

#
# Structure for the `invoice_detail` table : 
#

CREATE TABLE `invoice_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle de la Factura',
  `invoice` int(4) NOT NULL default '0' COMMENT 'Identificador de la Factura',
  `project` int(4) default NULL COMMENT 'Identificador del Proyecto',
  `line` smallint(2) default '1' COMMENT 'Numero de línea del Detalle dentro de la Factura',
  `item` int(4) default NULL COMMENT 'Identificador del Articulo del Detalle de Factura',
  `description` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Detalle de Factura',
  `quantity` double(15,3) default '0.000' COMMENT 'Cantidad del Detalle de Factura',
  `price` double default '0' COMMENT 'Precio del Detalle de Factura',
  `discount_expr` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descuentos del Detalle de Factura',
  `source` tinyint(2) default '0' COMMENT 'Origen del Detalle de la Factura',
  `source_id` int(4) default NULL COMMENT 'Identificador del Origen del Detalle de la Factura',
  `taxable_base` double(15,4) default '0.0000' COMMENT 'Base Imponible del Detalle de Factura',
  `taxes` double(15,3) default '0.000' COMMENT 'Tasas del Detalle de Factura',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `warehouse` int(4) default NULL COMMENT 'Identificador del Almacen',
  PRIMARY KEY  (`id`),
  KEY `idx_invd_item` (`item`),
  KEY `idx_invd_invc` (`invoice`),
  KEY `idx_invd_dlvd` (`source_id`),
  KEY `workplace` (`workplace`),
  KEY `IDX_INVOICE_DETAIL_PROJECT` (`project`),
  KEY `IDX_INVOICE_DETAIL_WAREHOUSE` (`warehouse`),
  CONSTRAINT `FK_INVOICE_DETAIL_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_INVOICE_DETAIL_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`),
  CONSTRAINT `invoice_detail_ibfk_1` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`),
  CONSTRAINT `invoice_detail_ibfk_2` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `invoice_detail_ibfk_3` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de la Factura';

#
# Structure for the `invoice_detail_account` table : 
#

CREATE TABLE `invoice_detail_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `invoice_detail` int(4) NOT NULL default '0' COMMENT 'Identificador de la Linea de Factura',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `invoice_detail` (`invoice_detail`),
  KEY `account` (`account`),
  CONSTRAINT `invoice_detail_account_ibfk_1` FOREIGN KEY (`invoice_detail`) REFERENCES `invoice_detail` (`id`),
  CONSTRAINT `invoice_detail_account_ibfk_2` FOREIGN KEY (`account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables asociadas a Lineas de Facturas';

#
# Structure for the `invoice_tax` table : 
#

CREATE TABLE `invoice_tax` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Impuesto de la Factura',
  `invoice_detail` int(4) NOT NULL default '0' COMMENT 'Identificador del Detalle de la Factura',
  `tax_type` tinyint(2) default '0' COMMENT 'Tipo de Impuesto del Detalle de la Factura',
  `percentage` double(15,3) default '0.000' COMMENT 'Porcentaje de Impuesto del Detalle de la Factura',
  `surcharge` double(15,3) default '0.000' COMMENT 'Porcentaje del recargo de equivalencia del Detalle de la Factura',
  `quota` double default '0' COMMENT 'Cuota de Impuesto del Detalle de la Factura',
  `surcharge_quota` double default '0' COMMENT 'Cuota de recargo de equivalencia del Detalle de la Factura',
  `vat_deduction_type` tinyint(2) default '0' COMMENT 'Tipo de deduccion del IVA',
  `withholding_type` tinyint(2) default '0' COMMENT 'Tipo de retencion',
  `deductible_quota` double default '0' COMMENT 'Cuota deducible',
  PRIMARY KEY  (`id`),
  KEY `invoice_detail` (`invoice_detail`),
  CONSTRAINT `invoice_tax_ibfk_1` FOREIGN KEY (`invoice_detail`) REFERENCES `invoice_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Impuestos del Detalle de la Factura';

#
# Structure for the `invoice_tax_account` table : 
#

CREATE TABLE `invoice_tax_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `invoice_tax` int(4) NOT NULL default '0' COMMENT 'Identificador de la Linea de Impuesto',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `invoice_tax` (`invoice_tax`),
  KEY `account` (`account`),
  CONSTRAINT `invoice_tax_account_ibfk_1` FOREIGN KEY (`invoice_tax`) REFERENCES `invoice_tax` (`id`),
  CONSTRAINT `invoice_tax_account_ibfk_2` FOREIGN KEY (`account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables asociadas a Impuestos de Facturas';

#
# Structure for the `invoicing_group` table : 
#

CREATE TABLE `invoicing_group` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `parent` int(4) NOT NULL COMMENT 'Grupo de Facturacion',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `parent_unique` (`parent`),
  CONSTRAINT `invoicing_group_fk` FOREIGN KEY (`parent`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Grupos de Facturacion';

#
# Structure for the `invoicing_group_detail` table : 
#

CREATE TABLE `invoicing_group_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `invoicing_group` int(4) NOT NULL COMMENT 'Grupo de Facturacion al que pertenece',
  `child` int(4) NOT NULL COMMENT 'Componente asociado a un Grupo de Facturacion',
  `grouped` tinyint(1) default '0' COMMENT 'Indica si agrupa facturas o no',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `child_unique` (`child`),
  KEY `invoicing_group` (`invoicing_group`),
  CONSTRAINT `invoicing_group_detail_fk` FOREIGN KEY (`invoicing_group`) REFERENCES `invoicing_group` (`id`),
  CONSTRAINT `invoicing_group_detail_fk1` FOREIGN KEY (`child`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de los Grupos de Facturacion';

#
# Structure for the `irpf_data` table : 
#

CREATE TABLE `irpf_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `contract` int(4) NOT NULL COMMENT 'Identificador del contrato',
  `family_situation` tinyint(2) default '0' COMMENT 'Situacion familiar',
  `spouse_document` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Numero de Documento del conyuge',
  `disability_level` tinyint(2) default '0' COMMENT 'Grado de discapacidad',
  `dependence` tinyint(1) default '0' COMMENT 'Dependencia de terceras personas',
  `moving_date` date default NULL COMMENT 'Fecha de movilidad geografica',
  `labour_prolongation` tinyint(1) default '0' COMMENT 'Prolongacion de la actividad laboral',
  `descendient_count` tinyint(2) default NULL COMMENT 'Numero de hijos',
  `start_date` date default NULL COMMENT 'Fecha inicio del modelo',
  `end_date` date default NULL COMMENT 'Fecha fin del modelo',
  `fiscal_exclusion` tinyint(1) default '0' COMMENT 'Exclusion a la obligacion de tributar',
  `issue_date` date NOT NULL COMMENT 'Fecha de emisión',
  `annual_remuneration` double(15,3) default NULL COMMENT 'Retribuciones totales (dinerarias y en especie). Importe íntegro',
  `irregular_18_2_reduction` double(15,3) default NULL COMMENT 'Reducciones por irregularidad ( Atr. 18.2 LIRPF)',
  `irregular_18_3_reduction` double(15,3) default NULL COMMENT 'Reducciones por irregularidad ( Atr. 18.3: Disposiciones transitorias 11ª y 12 ª de la LIRPF)',
  `deduccibles_expenses` double(15,3) default NULL COMMENT 'Gastos deducibles ( Atr 19.2, letras a, b y c de la LINRPF: Seguridad Social, Mutualidades ...)',
  `spousal_support` double(15,3) default NULL COMMENT 'Pension compensatoria a favor del cónyuge. Importe fijado judicialmente',
  `food_annuity` double(15,3) default NULL COMMENT 'Anualidades por alimentos en favor de los hijos. Importe fijado judicialmente',
  `deduct_home_loan` tinyint(2) default NULL COMMENT 'Comunicación de pagos por la adquisión o rehabilitación de la vivienda habitual utilizando financiación ajena',
  `request_irpf` double(15,2) default NULL COMMENT 'Tipo de retención solicitado',
  `contract_type` tinyint(2) NOT NULL default '0' COMMENT 'Contrato o relación',
  `ceuta_melilla` tinyint(1) NOT NULL default '0' COMMENT 'Los datos anteriores corresponden a rendimientos obtenidos en Ceuta o Melilla',
  PRIMARY KEY  (`id`),
  KEY `IDX_IRPF_DATA_CONTRACT` (`contract`),
  CONSTRAINT `FK_IRPF_DATA_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Dator de irpf';

#
# Structure for the `irpf_data_ascendants` table : 
#

CREATE TABLE `irpf_data_ascendants` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `irpf_data` int(4) NOT NULL COMMENT 'Identificador del irpf',
  `birth_year` int(4) default NULL COMMENT 'Anio de nacimiento',
  `disability_level` tinyint(2) default '0' COMMENT 'Grado de discapacidad',
  `dependence` tinyint(1) default '0' COMMENT 'Dependencia de terceras personas',
  `another_descendient` tinyint(2) default '0' COMMENT 'Convivencia con otros descendientes',
  PRIMARY KEY  (`id`),
  KEY `IDX_IRPF_DATA_ASCENDIENTS_IRPF_DATA` (`irpf_data`),
  CONSTRAINT `FK_IRPF_DATA_ASCENDIENTS_IRPF_DATA` FOREIGN KEY (`irpf_data`) REFERENCES `irpf_data` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ascendientes del modelo 145';

#
# Structure for the `irpf_data_descendients` table : 
#

CREATE TABLE `irpf_data_descendients` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `irpf_data` int(4) NOT NULL COMMENT 'Identificador del irpf',
  `birth_year` int(4) default NULL COMMENT 'Anio de nacimiento',
  `adoption_year` int(4) default NULL COMMENT 'Anio de adopcion',
  `disability_level` tinyint(2) default '0' COMMENT 'Grado de discapacidad',
  `dependence` tinyint(1) default '0' COMMENT 'Dependencia de terceras personas',
  `unique_parent` tinyint(1) default '0' COMMENT 'Computo por entero de hijos o descendientes',
  PRIMARY KEY  (`id`),
  KEY `IDX_IRPF_DATA_DESCENDIENTS_IRPF_DATA` (`irpf_data`),
  CONSTRAINT `FK_IRPF_DATA_DESCENDIENTS_IRPF_DATA` FOREIGN KEY (`irpf_data`) REFERENCES `irpf_data` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Descendientes del modelo 145';

#
# Structure for the `irpf_regularization` table : 
#

CREATE TABLE `irpf_regularization` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `contract` int(4) NOT NULL COMMENT 'Identificador del contrato',
  `reason` tinyint(2) default NULL COMMENT 'Causa de regularización',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos regularizacion IRPF';

#
# Structure for the `irpf_result` table : 
#

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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Resultados IRPF';

#
# Structure for the `item_alternative` table : 
#

CREATE TABLE `item_alternative` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador de Articulo',
  `alternative_item` int(4) NOT NULL default '0' COMMENT 'Identificador de Articulo Alternativo',
  `priority` tinyint(2) default '0' COMMENT 'Prioridad del Articulo Alternativo',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_UNQ_ITEM_ALTERNATIVE` (`item`,`alternative_item`),
  KEY `IDX_ITEM_ALTERNATIVE_ITEM` (`item`),
  KEY `IDX_ITEM_ALTERNATIVE_ALTERNATIVE` (`alternative_item`),
  CONSTRAINT `FK_ITEM_ALTERNATIVE_ALTERNATIVE` FOREIGN KEY (`alternative_item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_ALTERNATIVE_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulo Alternativos';

#
# Structure for the `item_composition` table : 
#

CREATE TABLE `item_composition` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo compuesto',
  `composition_item` int(4) NOT NULL COMMENT 'Identificador del Articulo componente',
  `sequence` smallint(2) default '0' COMMENT 'Numero de secuencia dentro de la Composicion',
  `description` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del componente',
  `quantity` double(15,3) default '0.000' COMMENT 'Cantidad del componente',
  `discount_expr` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descuentos del componente',
  PRIMARY KEY  (`id`),
  KEY `IDX_ITEM_COMPOSITION_ITEM` (`item`),
  KEY `IDX_ITEM_COMPOSITION_COMPOSITION` (`composition_item`),
  CONSTRAINT `FK_ITEM_COMPOSITION_COMPOSITION` FOREIGN KEY (`composition_item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_COMPOSITION_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Composicion de Articulos';

#
# Structure for the `item_pos` table : 
#

CREATE TABLE `item_pos` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador de Articulo',
  `plu` varchar(4) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo PLU del Articulo',
  `barcode` varchar(15) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo de barras del Articulo',
  `desc_short` varchar(20) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion corta del Articulo',
  `plu_product_type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Articulo en Balanza',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `item` (`item`),
  UNIQUE KEY `item_plu` (`plu`),
  CONSTRAINT `intempos_item_ibfk` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos del Articulo para el Punto de Venta';

#
# Structure for the `item_supplier` table : 
#

CREATE TABLE `item_supplier` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador de Articulo',
  `supplier` int(4) NOT NULL default '0' COMMENT 'Identificador de Proveedor',
  `code` varchar(15) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo del Producto en el Proveedor',
  `priority` tinyint(2) default '0' COMMENT 'Prioridad del Proveedor',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_UNQ_ITEM_SUPPLIER` (`item`,`supplier`),
  KEY `IDX_ITEM_SUPPLIER_ITEM` (`item`),
  KEY `IDX_ITEM_SUPPLIER_SUPPLIER` (`supplier`),
  CONSTRAINT `FK_ITEM_SUPPLIER_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_SUPPLIER_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos del Articulo por Proveedor';

#
# Structure for the `item_tariff` table : 
#

CREATE TABLE `item_tariff` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador de Articulo',
  `tariff` int(4) NOT NULL default '0' COMMENT 'Identificador de Tarifa',
  `type` tinyint(2) default '0' COMMENT 'Tipo de Tarifa',
  `profit_percent` double default '0' COMMENT 'Porcentaje de beneficio',
  `price` double default '0' COMMENT 'Precio de Venta',
  PRIMARY KEY  (`id`),
  KEY `IDX_ITEM_TARIFF_TARIFF` (`tariff`),
  KEY `IDX_ITEM_TARIFF_ITEM` (`item`),
  CONSTRAINT `FK_ITEM_TARIFF_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_TARIFF_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas de Articulos';

#
# Structure for the `item_warehouse` table : 
#

CREATE TABLE `item_warehouse` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador de Articulo',
  `warehouse` int(4) NOT NULL default '0' COMMENT 'Identificador de Almacen',
  `stock_max` double(15,3) default '0.000' COMMENT 'Stock maximo del Articulo en el Almacen',
  `stock_min` double(15,3) default '0.000' COMMENT 'Stock minimo del Articulo en el Almacen',
  `location` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Localizacion del Articulo en el Almacen',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_UNQ_ITEM_WAREHOUSE` (`item`,`warehouse`),
  KEY `IDX_ITEM_WAREHOUSE_ITEM` (`item`),
  KEY `IDX_ITEM_WAREHOUSE_WAREHOUSE` (`warehouse`),
  CONSTRAINT `FK_ITEM_WAREHOUSE_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_WAREHOUSE_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos del Articulo por Almacen';

#
# Structure for the `leasing` table : 
#

CREATE TABLE `leasing` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `leasing_date` date NOT NULL COMMENT 'Fecha de Concesion',
  `supplier_name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Razon Social del Proveedor',
  `supplier_document` varchar(16) collate latin1_spanish_ci NOT NULL COMMENT 'CIF del Proveedor',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `term` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Plazo',
  `interest_percent` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Porcentaje de Interes',
  `review` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Revision',
  `amount` double(15,3) default NULL COMMENT 'Importe del leasing',
  `rbank` int(4) NOT NULL COMMENT 'Banco por el que se paga el leasing',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de Seguridad',
  `fixed_asset_account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta de inmobilizado',
  `vat` int(4) NOT NULL COMMENT 'IVA del leasing',
  PRIMARY KEY  (`id`),
  KEY `rbank` (`rbank`),
  KEY `fixed_asset_account` (`fixed_asset_account`),
  CONSTRAINT `leasing_fk` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`),
  CONSTRAINT `leasing_fk1` FOREIGN KEY (`fixed_asset_account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Leasing';

#
# Structure for the `leasing_account` table : 
#

CREATE TABLE `leasing_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `leasing` int(4) NOT NULL COMMENT 'Leasing',
  `account` varchar(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `leasing` (`leasing`),
  KEY `account` (`account`),
  CONSTRAINT `leasing_account_fk` FOREIGN KEY (`leasing`) REFERENCES `leasing` (`id`),
  CONSTRAINT `leasing_account_fk1` FOREIGN KEY (`account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas contables de Leasings';

#
# Structure for the `leave_batch` table : 
#

CREATE TABLE `leave_batch` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `date` datetime default NULL COMMENT 'Fecha de la Remesa',
  `status` tinyint(2) NOT NULL default '0' COMMENT 'Indica el estado de la remesa',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas de partes IT';

#
# Structure for the `leave_batch_attach` table : 
#

CREATE TABLE `leave_batch_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Archivo Adjunto',
  `leave_batch` int(4) NOT NULL default '0' COMMENT 'Identificador de la remesa',
  `mimeType` tinyint(2) default '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Archivo Adjunto',
  `scope` int(4) default NULL COMMENT 'Ambito del Archivo Adjunto',
  `attach_date` date default NULL COMMENT 'Fecha del Archivo Adjunto',
  PRIMARY KEY  (`id`),
  KEY `IDX_LEAVE_BATCH_ATTACH_CONTRACT` (`leave_batch`),
  KEY `IDX_LEAVE_BATCH_ATTACH_SCOPE` (`scope`),
  CONSTRAINT `FK_LEAVE_BATCH_ATTACH_LEAVE_BATCH` FOREIGN KEY (`leave_batch`) REFERENCES `leave_batch` (`id`),
  CONSTRAINT `FK_LEAVE_BATCH_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de remesas de partes IT';

#
# Structure for the `leave_batch_detail` table : 
#

CREATE TABLE `leave_batch_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `leave_batch` int(4) NOT NULL COMMENT 'Identificador unico de la remesa',
  `contract_leave_detail` int(4) NOT NULL COMMENT 'Identificador unico del parte',
  PRIMARY KEY  (`id`),
  KEY `IDX_LEAVE_BATCH_DETAIL_LEAVE_BATCH` (`leave_batch`),
  KEY `IDX_LEAVE_BATCH_DETAIL_CONTRACT_LEAVE_DETAIL` (`contract_leave_detail`),
  CONSTRAINT `FK_LEAVE_BATCH_DETAIL_CONTRACT_LEAVE_DETAIL` FOREIGN KEY (`contract_leave_detail`) REFERENCES `contract_leave_detail` (`id`),
  CONSTRAINT `FK_LEAVE_BATCH_DETAIL_LEAVE_BATCH` FOREIGN KEY (`leave_batch`) REFERENCES `leave_batch` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de remesas de partes IT';

#
# Structure for the `loan` table : 
#

CREATE TABLE `loan` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Prestamo',
  `loan_date` date NOT NULL COMMENT 'Fecha de Concesion del Prestamo',
  `term` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Plazo',
  `interest` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Interes',
  `review` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Revision',
  `amount` double(15,3) default '0.000' COMMENT 'Importe',
  `expenses` double(15,3) default '0.000' COMMENT 'Gastos asociados al Prestamo',
  `rbank` int(4) NOT NULL COMMENT 'Banco por el que se paga el Prestamo',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de Seguridad',
  `fee_amount` double(15,3) default '0.000' COMMENT 'Importe de la cuota',
  `recurrence` int(4) default '0' COMMENT 'Periodicidad',
  `pay_day` int(4) default '1' COMMENT 'Dia de Pago',
  `status` tinyint(2) default '0' COMMENT 'Estado',
  PRIMARY KEY  (`id`),
  KEY `rbank` (`rbank`),
  CONSTRAINT `loan_fk` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Prestamos';

#
# Structure for the `loan_account` table : 
#

CREATE TABLE `loan_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `loan` int(4) NOT NULL COMMENT 'Prestamo',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `loan` (`loan`),
  KEY `account` (`account`),
  CONSTRAINT `loan_account_fk` FOREIGN KEY (`loan`) REFERENCES `loan` (`id`),
  CONSTRAINT `loan_account_fk1` FOREIGN KEY (`account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Prestamos';

#
# Structure for the `make` table : 
#

CREATE TABLE `make` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Fabricante',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Fabricante',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Fabricantes';

#
# Structure for the `mark` table : 
#

CREATE TABLE `mark` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `subject` int(4) NOT NULL COMMENT 'Identificador de Asignatura',
  `alumn` int(4) NOT NULL COMMENT 'Identificador de Alumno',
  `evaluation` tinyint(2) NOT NULL COMMENT 'Numero de evaluacion',
  `mark` double(15,3) default '0.000' COMMENT 'Nota',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `subject_customer_evaluation` (`subject`,`alumn`,`evaluation`),
  KEY `subject` (`subject`),
  KEY `customer` (`alumn`),
  CONSTRAINT `mark_ibfk_1` FOREIGN KEY (`alumn`) REFERENCES `course_alumn` (`id`),
  CONSTRAINT `mark_subject_fk` FOREIGN KEY (`subject`) REFERENCES `course_academicskill` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Notas de Alumnos';

#
# Structure for the `message_content` table : 
#

CREATE TABLE `message_content` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `content` text collate latin1_spanish_ci NOT NULL COMMENT 'Contenido del Mensaje',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contenido de Mensajes';

#
# Structure for the `message_log` table : 
#

CREATE TABLE `message_log` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `message_id` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador del Mensaje para el servidor de Esendex',
  `message_content` int(4) default NULL COMMENT 'Identificador del Contenido del Mensaje',
  `recipient` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Destinatario del Mensaje',
  `type` varchar(10) collate latin1_spanish_ci default NULL COMMENT 'Tipo de Mensaje',
  `sent_date` datetime NOT NULL COMMENT 'Fecha y hora de envio del Mensaje',
  `message_parts` tinyint(2) NOT NULL default '1' COMMENT 'Numero de partes que componen el Mensaje',
  `username` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Usuario que envia el mensaje',
  PRIMARY KEY  (`id`),
  KEY `message_content` (`message_content`),
  CONSTRAINT `message_log_fk1` FOREIGN KEY (`message_content`) REFERENCES `message_content` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Log de Mensajes';

#
# Structure for the `mk_template` table : 
#

CREATE TABLE `mk_template` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Plantilla',
  `data` mediumtext collate latin1_spanish_ci NOT NULL COMMENT 'Contenido de la Plantilla',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Plantilla esta activa o no',
  `creationDate` datetime NOT NULL COMMENT 'Fecha de la creacion en el sistema de la Plantilla',
  `subject` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Asunto de la Plantilla',
  `append_signature` tinyint(1) NOT NULL COMMENT 'Indica si la Plantilla incluye la firma o no',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Plantilla de Marketing';

#
# Structure for the `mk_campaign` table : 
#

CREATE TABLE `mk_campaign` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Campaña esta activa o no',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Campaña',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Campañas de Marketing';

#
# Structure for the `survey` table : 
#

CREATE TABLE `survey` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si el Cuestionario esta activa o no',
  `creationDate` datetime NOT NULL COMMENT 'Fecha de creacion del Cuestionario',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Cuestionario',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuestionarios';

#
# Structure for the `mk_action` table : 
#

CREATE TABLE `mk_action` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `campaign` int(11) NOT NULL COMMENT 'Identificador de la Campaña',
  `media_type` int(4) NOT NULL COMMENT 'Tipo de contacto de la Accion',
  `start_date` datetime NOT NULL COMMENT 'Fecha de inicio',
  `end_date` datetime default NULL COMMENT 'Fecha de finalizacion',
  `survey` int(4) default NULL COMMENT 'Identificador del Cuestionario',
  `template` int(4) default NULL COMMENT 'Identificador de la Plantilla',
  PRIMARY KEY  (`id`),
  KEY `survey` (`survey`),
  KEY `campaign` (`campaign`),
  KEY `IDX_MK_ACTION_MK_TEMPLATE` (`template`),
  CONSTRAINT `FK_MK_ACTION_MK_TEMPLATE` FOREIGN KEY (`template`) REFERENCES `mk_template` (`id`),
  CONSTRAINT `FK_MK_ACTION_MK_CAMPAIGN` FOREIGN KEY (`campaign`) REFERENCES `mk_campaign` (`id`),
  CONSTRAINT `FK_MK_ACTION_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Acciones de Marketing';

#
# Structure for the `survey_response` table : 
#

CREATE TABLE `survey_response` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `creationDate` datetime NOT NULL COMMENT 'Fecha de la creacion en el sistema de la Respuesta del Cuestionario',
  `response_date` datetime NOT NULL COMMENT 'Fecha de la Respuesta del Cuestionario',
  `survey` int(4) NOT NULL COMMENT 'Identificador del Cuestionario',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `user` int(4) NOT NULL COMMENT 'Identificador del Usuario',
  `campaign_action` int(4) default NULL COMMENT 'Identificador de la Accion de la Campaña',
  PRIMARY KEY  (`id`),
  KEY `survey` (`survey`),
  KEY `target` (`target`),
  KEY `user` (`user`),
  KEY `campaign_action` (`campaign_action`),
  CONSTRAINT `FK_SURVERY_RESPONSE_MK_ACTION` FOREIGN KEY (`campaign_action`) REFERENCES `mk_action` (`id`),
  CONSTRAINT `FK_SURVEY_RESPONSE_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`),
  CONSTRAINT `FK_SURVEY_RESPONSE_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_SURVEY_RESPONSE_USER` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Respuestas de Cuestionarios';

#
# Structure for the `mk_action_target` table : 
#

CREATE TABLE `mk_action_target` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `action` int(4) NOT NULL COMMENT 'Identificador de la Accion',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `status` tinyint(2) NOT NULL COMMENT 'Estado del Cliente Potencial de la Accion de Campaña',
  `survey_response` int(4) default NULL COMMENT 'Identificador de la Respuesta de Cuestionario',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios',
  `user` int(4) default NULL COMMENT 'Identificador del Usuario',
  PRIMARY KEY  (`id`),
  KEY `action` (`action`),
  KEY `target` (`target`),
  KEY `survey_response` (`survey_response`),
  KEY `IDX_MK_ACTION_TARGET_USER` (`user`),
  CONSTRAINT `FK_ACTION_TARGET_SURVERY_RESPONSE` FOREIGN KEY (`survey_response`) REFERENCES `survey_response` (`id`),
  CONSTRAINT `FK_MK_ACTION_TARGET_USER` FOREIGN KEY (`user`) REFERENCES `user` (`id`),
  CONSTRAINT `MK_ACTION_TARGET_MK_ACTION` FOREIGN KEY (`action`) REFERENCES `mk_action` (`id`),
  CONSTRAINT `MK_ACTION_TARGET_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Clientes Potenciales de la Accion de Marketing';

#
# Structure for the `mod145` table : 
#

CREATE TABLE `mod145` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `contract` int(4) NOT NULL COMMENT 'Identificador del contrato',
  `date` date default NULL COMMENT 'Fecha emision del modelo',
  `family_situation` tinyint(2) default '0' COMMENT 'Situacion familiar',
  `spouse_document` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Numero de Documento del conyuge',
  `disability_level` tinyint(2) default '0' COMMENT 'Grado de discapacidad',
  `dependence` tinyint(1) default '0' COMMENT 'Dependencia de terceras personas',
  `moving_date` date default NULL COMMENT 'Fecha de movilidad geografica',
  `labour_prolongation` tinyint(1) default '0' COMMENT 'Prolongacion de la actividad laboral',
  `descendient_count` tinyint(2) default NULL COMMENT 'Numero de hijos',
  PRIMARY KEY  (`id`),
  KEY `IDX_MOD145_CONTRACT` (`contract`),
  CONSTRAINT `FK_MOD145_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comunicacion del modelo 145';

#
# Structure for the `mod145_ascendants` table : 
#

CREATE TABLE `mod145_ascendants` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `mod145` int(4) NOT NULL COMMENT 'Identificador del modelo 145',
  `birth_year` int(4) default NULL COMMENT 'Anio de nacimiento',
  `disability_level` tinyint(2) default '0' COMMENT 'Grado de discapacidad',
  `dependence` tinyint(1) default '0' COMMENT 'Dependencia de terceras personas',
  `another_descendient` tinyint(1) default '0' COMMENT 'Convivencia con otros descendientes',
  PRIMARY KEY  (`id`),
  KEY `IDX_MOD145_ASCENDIENTS_MOD145` (`mod145`),
  CONSTRAINT `FK_MOD145_ASCENDIENTS_MOD145` FOREIGN KEY (`mod145`) REFERENCES `mod145` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ascendientes del modelo 145';

#
# Structure for the `mod145_descendients` table : 
#

CREATE TABLE `mod145_descendients` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `mod145` int(4) NOT NULL COMMENT 'Identificador del modelo 145',
  `birth_year` int(4) default NULL COMMENT 'Anio de nacimiento',
  `adoption_year` int(4) default NULL COMMENT 'Anio de adopcion',
  `disability_level` tinyint(2) default '0' COMMENT 'Grado de discapacidad',
  `dependence` tinyint(1) default '0' COMMENT 'Dependencia de terceras personas',
  `unique_parent` tinyint(1) default '0' COMMENT 'Computo por entero de hijos o descendientes',
  PRIMARY KEY  (`id`),
  KEY `IDX_MOD145_DESCENDIENTS_MOD145` (`mod145`),
  CONSTRAINT `FK_MOD145_DESCENDIENTS_MOD145` FOREIGN KEY (`mod145`) REFERENCES `mod145` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Descendientes del modelo 145';

#
# Structure for the `model` table : 
#

CREATE TABLE `model` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Modelo',
  `make` int(4) NOT NULL COMMENT 'Identificador del Fabricante',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Modelo',
  PRIMARY KEY  (`id`),
  KEY `make` (`make`),
  CONSTRAINT `model_ibfk_1` FOREIGN KEY (`make`) REFERENCES `make` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Modelos';

#
# Structure for the `note` table : 
#

CREATE TABLE `note` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Nota',
  `subject` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion corta de la Nota',
  `date` datetime NOT NULL COMMENT 'Fecha de la Nota',
  `owner` int(4) default NULL COMMENT 'Destinatario de la Nota',
  `note` text collate latin1_spanish_ci COMMENT 'Texto de la Nota',
  PRIMARY KEY  (`id`),
  KEY `owner` (`owner`),
  CONSTRAINT `note_fk` FOREIGN KEY (`owner`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Notas';

#
# Structure for the `notice` table : 
#

CREATE TABLE `notice` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Aviso',
  `date` datetime NOT NULL COMMENT 'Fecha y hora en la que se produjo el Aviso',
  `sender` int(4) NOT NULL COMMENT 'Remitente del Aviso',
  `work_group` int(4) default NULL COMMENT 'Grupo de Trabajo al que va dirigida el Aviso',
  `recipient` int(4) default NULL COMMENT 'Destinatario del Aviso',
  `source` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Origen del Aviso',
  `company` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Empresa para la que trabaja el origen del Aviso',
  `phone` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Telefono para contactar con el origen del Aviso',
  `subject` text collate latin1_spanish_ci COMMENT 'Asunto del Aviso',
  `status` tinyint(2) NOT NULL COMMENT 'Estado del Aviso',
  `type` tinyint(2) NOT NULL COMMENT 'Tipo de Aviso',
  `priority` tinyint(2) NOT NULL COMMENT 'Prioridad del Aviso',
  PRIMARY KEY  (`id`),
  KEY `work_group` (`work_group`),
  KEY `sender` (`sender`),
  KEY `recipient` (`recipient`),
  CONSTRAINT `notice_fk` FOREIGN KEY (`sender`) REFERENCES `user` (`id`),
  CONSTRAINT `notice_fk1` FOREIGN KEY (`recipient`) REFERENCES `user` (`id`),
  CONSTRAINT `notice_fk2` FOREIGN KEY (`work_group`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Avisos';

#
# Structure for the `observation` table : 
#

CREATE TABLE `observation` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Observacion',
  `description` varchar(256) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Observacion',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Observaciones';

#
# Structure for the `offer_attach` table : 
#

CREATE TABLE `offer_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `offer` int(4) NOT NULL COMMENT 'Identificador del Presupuesto',
  `mimeType` tinyint(2) default '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  PRIMARY KEY  (`id`),
  KEY `IDX_OFFER_ATTACH_OFFER` (`offer`),
  CONSTRAINT `FK_OFFER_ATTACH_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Presupuestos';

#
# Structure for the `offer_detail_commission` table : 
#

CREATE TABLE `offer_detail_commission` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `offer_detail` int(4) NOT NULL default '0' COMMENT 'Identificador de la Linea de Presupuesto',
  `commission` double default '0' COMMENT 'Porcentaje de Comision',
  `amount` double default '0' COMMENT 'Importe de la Comision',
  `status` tinyint(2) default '0' COMMENT 'Estado de la Comision',
  `pay_date` date default NULL COMMENT 'Fecha de liquidacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_OFFER_DETAIL_COMMISSION_OFFER_DETAIL` (`offer_detail`),
  CONSTRAINT `FK_OFFER_DETAIL_COMMISSION_OFFER_DETAIL` FOREIGN KEY (`offer_detail`) REFERENCES `offer_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones asociadas a Lineas de Presupuestos';

#
# Structure for the `offer_term` table : 
#

CREATE TABLE `offer_term` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `offer` int(4) NOT NULL COMMENT 'Identificador del Presupuesto',
  `line` smallint(2) default '1' COMMENT 'Numero de linea de la Condicion del Presupuesto',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Nombre de la Condicion Comercial',
  `description` text collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Condicion Comercial',
  `term_general` tinyint(1) default '0' COMMENT 'Indica si la Condicion es particular o general',
  PRIMARY KEY  (`id`),
  KEY `IDX_OFFER_TERM_OFFER` (`offer`),
  CONSTRAINT `FK_OFFER_TERM_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Condiciones del Presupuesto';

#
# Structure for the `payroll_workplace` table : 
#

CREATE TABLE `payroll_workplace` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `agreement` int(4) default NULL COMMENT 'Identificador del Convenio',
  `enterprise_activity` int(4) default NULL COMMENT 'Identificador de la Actividad',
  `calendar` int(4) default NULL COMMENT 'Identificador del Calendario',
  PRIMARY KEY  (`id`),
  KEY `IDX_PAYROLL_WORKPLACE_WORKPLACE` (`workplace`),
  KEY `IDX_PAYROLL_WORKPLACE_CALENDAR` (`calendar`),
  KEY `IDX_PAYROLL_WORKPLACE_ENTERPRISE_ACTIVITY` (`enterprise_activity`),
  KEY `IDX_PAYROLL_WORKPLACE_AGREEMENT` (`agreement`),
  CONSTRAINT `FK_PAYROLL_WORKPLACE_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`),
  CONSTRAINT `FK_PAYROLL_WORKPLACE_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_PAYROLL_WORKPLACE_ENTERPRISE_ACTIVITY` FOREIGN KEY (`enterprise_activity`) REFERENCES `enterprise_activity` (`id`),
  CONSTRAINT `FK_PAYROLL_WORKPLACE_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos laborales del Centro de Trabajo';

#
# Structure for the `pcategory_tree` table : 
#

CREATE TABLE `pcategory_tree` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Nodo del Arbol de Categorias',
  `parent` int(4) default NULL COMMENT 'Identificador de la Categoria padre',
  `child` int(4) default NULL COMMENT 'Identificador de la Categoria hijo',
  PRIMARY KEY  (`id`),
  KEY `parent` (`parent`),
  KEY `child` (`child`),
  CONSTRAINT `pcategory_tree_ibfk_1` FOREIGN KEY (`parent`) REFERENCES `pcategory` (`id`),
  CONSTRAINT `pcategory_tree_ibfk_2` FOREIGN KEY (`child`) REFERENCES `pcategory` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Arbol de Categorias de Productos';

#
# Structure for the `pm_type_detail_account` table : 
#

CREATE TABLE `pm_type_detail_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `pm_type_detail` int(4) NOT NULL default '0' COMMENT 'Identificador del Detalle por Tipo de Forma de Pago',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `IDX_PM_TYPE_DETAIL_ACCOUNT_DETAIL` (`pm_type_detail`),
  KEY `IDX_PM_TYPE_DETAIL_ACCOUNT_ACCOUNT` (`account`),
  CONSTRAINT `FK_PM_TYPE_DETAIL_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_PM_TYPE_DETAIL_ACCOUNT_DETAIL` FOREIGN KEY (`pm_type_detail`) REFERENCES `pm_type_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Entidades Bancarias';

#
# Structure for the `process_detail` table : 
#

CREATE TABLE `process_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle de Proceso',
  `process` int(4) NOT NULL COMMENT 'Identificador del Proceso',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Detalle de Proceso',
  `position` int(4) NOT NULL COMMENT 'Orden de ejecucion del Detalle dentro del Proceso',
  `date_reference` tinyint(2) default NULL COMMENT 'Referencia para el calculo de la fecha de vencimiento de la Tarea',
  `days` int(4) default NULL COMMENT 'Numero de dias asociado a la referencia para el calculo de la fecha de vencimiento de la Tarea',
  `alert_days` int(4) default NULL COMMENT 'Numero de dias, previos a la fecha de vencimiento de la Tarea, para el calculo de la fecha de generacion de la Alarma',
  `workgroup` int(4) default NULL COMMENT 'Identificador del Grupo de Trabajo',
  `priority` tinyint(2) default '0' COMMENT 'Prioridad de la Tarea',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Activo si o no',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios del Detalle de Proceso',
  PRIMARY KEY  (`id`),
  KEY `process` (`process`),
  KEY `workgroup` (`workgroup`),
  CONSTRAINT `process_detail_fk_2` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`),
  CONSTRAINT `process_detail_ibfk_1` FOREIGN KEY (`process`) REFERENCES `process` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Procesos';

#
# Structure for the `process_transition_type` table : 
#

CREATE TABLE `process_transition_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Tipo de Transicion',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Transiciones entre Detalles de Procesos';

#
# Structure for the `process_detail_transition` table : 
#

CREATE TABLE `process_detail_transition` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `process_detail` int(11) NOT NULL COMMENT 'Identificador del Detalle del Proceso.',
  `process_transition_type` int(11) NOT NULL COMMENT 'Identificador del Tipo de Transicion.',
  `next_process_detail` int(11) NOT NULL COMMENT 'Identificador del siguiente Detalle del Proceso.',
  PRIMARY KEY  (`id`),
  KEY `process_detail` (`process_detail`),
  KEY `process_transition_type` (`process_transition_type`),
  KEY `next_process_detail` (`next_process_detail`),
  CONSTRAINT `fk_process_detail_transition_next_process_detail` FOREIGN KEY (`next_process_detail`) REFERENCES `process_detail` (`id`),
  CONSTRAINT `fk_process_detail_transition_process_detail` FOREIGN KEY (`process_detail`) REFERENCES `process_detail` (`id`),
  CONSTRAINT `fk_process_detail_transition_process_transition_type` FOREIGN KEY (`process_transition_type`) REFERENCES `process_transition_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Transiciones entre Detalles de Procesos';

#
# Structure for the `process_task` table : 
#

CREATE TABLE `process_task` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Relacion entre Campañas, Actividades y Tareas',
  `campaign` int(4) default NULL COMMENT 'Identificador de la Campaña',
  `process_detail` int(4) NOT NULL COMMENT 'Identificador del Detalle de Proceso',
  `task` int(4) NOT NULL COMMENT 'Identificador de la Tarea',
  PRIMARY KEY  (`id`),
  KEY `campaign` (`campaign`),
  KEY `IDX_PROCESS_TASK_CAMPAIGN` (`campaign`),
  KEY `IDX_PROCESS_TASK_PRC_DET` (`process_detail`),
  KEY `IDX_PROCESS_TASK_TASK` (`task`),
  CONSTRAINT `FK_PROCESS_TASK_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`),
  CONSTRAINT `FK_PROCESS_TASK_CAMPAIGN` FOREIGN KEY (`campaign`) REFERENCES `campaign` (`id`),
  CONSTRAINT `FK_PROCESS_TASK_PRC_DET` FOREIGN KEY (`process_detail`) REFERENCES `process_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Campañas, Actividades y Tareas';

#
# Structure for the `product_account` table : 
#

CREATE TABLE `product_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Cuenta Contable del Producto',
  `product` int(4) NOT NULL default '0' COMMENT 'Identificador del Producto',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Cuenta Contable del Producto',
  PRIMARY KEY  (`id`),
  KEY `product` (`product`),
  KEY `account` (`account`),
  CONSTRAINT `product_account_ibfk_1` FOREIGN KEY (`product`) REFERENCES `product` (`id`),
  CONSTRAINT `product_account_ibfk_2` FOREIGN KEY (`account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Productos';

#
# Structure for the `production` table : 
#

CREATE TABLE `production` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Produccion',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de la Produccion',
  `lot_code` varchar(25) collate latin1_spanish_ci default NULL COMMENT 'Codigo de lote de la Produccion',
  `production_date` date default NULL COMMENT 'Fecha de Produccion',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador del Articulo producido',
  `initial_quantity` double(11,3) default '0.000' COMMENT 'Cantidad inicial del Articulo en la Composicion',
  `quantity` double(11,3) default '0.000' COMMENT 'Cantidad del Articulo producido',
  `price` double(11,2) default '0.00' COMMENT 'Precio del Articulo producido',
  PRIMARY KEY  (`id`),
  KEY `idx_prtn_item` (`item`),
  CONSTRAINT `production_ibfk_1` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Produccion de Articulos';

#
# Structure for the `production_detail` table : 
#

CREATE TABLE `production_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle de la Produccion',
  `production` int(4) NOT NULL default '0' COMMENT 'Identificador de la Produccion',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador del Articulo subproducto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Articulo subproducto',
  `initial_quantity` double(11,3) default '0.000' COMMENT 'Cantidad inicial del Articulo subproducto en la Composicion',
  `quantity` double(11,3) default '0.000' COMMENT 'Cantidad del Articulo subproducto',
  `price` double(11,2) default '0.00' COMMENT 'Precio del Articulo subproducto',
  PRIMARY KEY  (`id`),
  KEY `idx_prtd_prtn` (`production`),
  KEY `idx_prtd_item` (`item`),
  CONSTRAINT `production_detail_ibfk_1` FOREIGN KEY (`production`) REFERENCES `production` (`id`) ON DELETE CASCADE,
  CONSTRAINT `production_detail_ibfk_2` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de la Produccion de Articulos';

#
# Structure for the `production_expense` table : 
#

CREATE TABLE `production_expense` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Gasto de la Produccion',
  `production` int(4) NOT NULL default '0' COMMENT 'Identificador de la Produccion',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Gasto',
  `quantity` double(11,3) default '0.000' COMMENT 'Cantidad del Gasto',
  `price` double(11,2) default '0.00' COMMENT 'Precio del Gasto',
  PRIMARY KEY  (`id`),
  KEY `idx_prte_prtn` (`production`),
  CONSTRAINT `production_expense_ibfk_1` FOREIGN KEY (`production`) REFERENCES `production` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Gastos de la Produccion de Articulos';

#
# Structure for the `project_activity` table : 
#

CREATE TABLE `project_activity` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Actividad',
  `project` int(4) NOT NULL COMMENT 'Identificador del Expendiente',
  `activity_type` int(4) NOT NULL COMMENT 'Tipo de Actividad',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Activo, si o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_PRJ_ACT_ACTIVITY_TYPE` (`activity_type`),
  KEY `IDX_PRJ_ACT_PROJECT` (`project`),
  CONSTRAINT `FK_PRJ_ACT_ACTIVITY_TYPE` FOREIGN KEY (`activity_type`) REFERENCES `activity_type` (`id`),
  CONSTRAINT `FK_PRJ_ACT_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades';

#
# Structure for the `project_commercial` table : 
#

CREATE TABLE `project_commercial` (
  `project` int(4) NOT NULL COMMENT 'Identificador del Proyecto',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `seller` int(4) default NULL COMMENT 'Identificador del Comercial',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios',
  `source` tinyint(2) NOT NULL COMMENT 'Origen del Proyecto',
  `status` tinyint(2) NOT NULL COMMENT 'Estado del Proyecto',
  `status_date` date default NULL COMMENT 'Fecha del Estado del Proyecto',
  `probability` int(4) default NULL COMMENT 'Probabilidad del Proyecto',
  PRIMARY KEY  (`project`),
  KEY `IDX_PROJECT_COMMERCIAL_TARGET` (`target`),
  KEY `IDX_PROJECT_COMMERCIAL_SELLER` (`seller`),
  CONSTRAINT `FK_PROJECT_COMMERCIAL_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PROJECT_COMMERCIAL_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_PROJECT_COMMERCIAL_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Operaciones Comerciales';

#
# Structure for the `project_dossier` table : 
#

CREATE TABLE `project_dossier` (
  `project` int(4) NOT NULL COMMENT 'Identificador unico del Expediente',
  `customer` int(4) NOT NULL COMMENT 'Identificador del Cliente',
  `number` varchar(16) collate latin1_spanish_ci NOT NULL COMMENT 'Numero de Expediente',
  `location` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Ubicacion del Expediente',
  `status` tinyint(2) NOT NULL COMMENT 'Estado del Expediente',
  PRIMARY KEY  (`project`),
  UNIQUE KEY `IDX_PRJ_DOSSIER_NUMBER` (`number`),
  KEY `IDX_PRJ_DOSSIER_CUSTOMER` (`customer`),
  KEY `FK_PRJ_DOSSIER_PROJECT` (`project`),
  CONSTRAINT `FK_PRJ_DOSSIER_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PRJ_DOSSIER_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Expedientes';

#
# Structure for the `tas_item` table : 
#

CREATE TABLE `tas_item` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Articulo',
  `model` int(4) NOT NULL COMMENT 'Identificador del Modelo',
  `publicCode` varchar(25) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo publico del Articulo',
  `privateCode` varchar(25) collate latin1_spanish_ci default NULL COMMENT 'Codigo privado del Articulo',
  `description` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Articulo',
  `add_info` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Informacion adicional del Articulo',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_tas_item_public` (`publicCode`),
  UNIQUE KEY `idx_tas_item_private` (`privateCode`),
  KEY `model` (`model`),
  CONSTRAINT `tas_item_ibfk_1` FOREIGN KEY (`model`) REFERENCES `model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulo susceptible de Asistencia Tecnica';

#
# Structure for the `project_tas` table : 
#

CREATE TABLE `project_tas` (
  `project` int(4) NOT NULL COMMENT 'Identificador del Proyecto',
  `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie de la Orden de Reparacion',
  `number` int(4) NOT NULL default '0' COMMENT 'Numero de la Orden de Reparacion',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `tas_item` int(4) NOT NULL COMMENT 'Identificador del Articulo susceptible de Asistencia Tecnica',
  `counter` double(15,3) default '0.000' COMMENT 'Contador del Articulo de la Orden de Reparacion (p.e. Kilometraje)',
  `task_holder` int(4) default NULL COMMENT 'Identificador del Empleado que ejecuta la Orden',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios',
  `status` tinyint(2) NOT NULL COMMENT 'Estado de la Orden de Reparacion',
  `status_date` date default NULL COMMENT 'Fecha del Estado de la Orden de Reparacion',
  `workplace` int(4) NOT NULL default '1' COMMENT 'Identificador del Centro de Trabajo',
  PRIMARY KEY  (`project`),
  KEY `IDX_PROJECT_TAS_TARGET` (`target`),
  KEY `IDX_PROJECT_TAS_TAS_ITEM` (`tas_item`),
  KEY `IDX_PROJECT_TAS_TASK_HOLDER` (`task_holder`),
  KEY `IDX_PROJECT_TAS_WORKPLACE` (`workplace`),
  CONSTRAINT `FK_PROJECT_TAS_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`),
  CONSTRAINT `FK_PROJECT_TAS_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PROJECT_TAS_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_PROJECT_TAS_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`),
  CONSTRAINT `FK_PROJECT_TAS_TAS_ITEM` FOREIGN KEY (`tas_item`) REFERENCES `tas_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ordenes de Reparacion o Fabricacion';

#
# Structure for the `qualification` table : 
#

CREATE TABLE `qualification` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Calificacion',
  `code` char(5) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo de la Calificacion',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de la Calificacion',
  `min_value` double(15,3) NOT NULL default '0.000' COMMENT 'Limite inferior de la Calificacion',
  `max_value` double(15,3) NOT NULL default '0.000' COMMENT 'Limite superior de la Calificacion',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Calificaciones';

#
# Structure for the `question` table : 
#

CREATE TABLE `question` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Pregunta esta activa o no',
  `question_text` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Texto de la Pregunta',
  `type` tinyint(2) NOT NULL COMMENT 'Tipo de Pregunta',
  `argument` text collate latin1_spanish_ci COMMENT 'Argumentacion de la Pregunta',
  `alias` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Alias de la Pregunta',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Preguntas';

#
# Structure for the `question_value` table : 
#

CREATE TABLE `question_value` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `value_text` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Valor de tipo texto',
  `value_number` double(15,3) default NULL COMMENT 'Valor de tipo numerico',
  `value_date` datetime default NULL COMMENT 'Valor de tipo fecha',
  PRIMARY KEY  (`id`),
  KEY `question` (`question`),
  CONSTRAINT `FK_QUESTION_VALUE_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Valores de Preguntas';

#
# Structure for the `raddinfo` table : 
#

CREATE TABLE `raddinfo` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `registry` int(4) NOT NULL COMMENT 'Identificador de la Persona o Empresa',
  `attribute` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Atributo adicional',
  `value` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Valor del atributo adicional',
  `value_date` date NOT NULL COMMENT 'Fecha del valor del atributo',
  PRIMARY KEY  (`id`),
  KEY `IDX_RADDINFO_REGISTRY` (`registry`),
  CONSTRAINT `FK_RADDINFO_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Informacion adicional de la Persona o Empresa';

#
# Structure for the `rattach` table : 
#

CREATE TABLE `rattach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Archivo Adjunto de la Persona o Empresa',
  `registry` int(4) NOT NULL default '0' COMMENT 'Identificador del Registro de la Persona o Empresa',
  `category` int(4) default NULL COMMENT 'Categoria del Archivo Adjunto',
  `mimeType` tinyint(2) default '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Archivo Adjunto',
  `scope` int(4) default NULL COMMENT 'Ambito del Archivo Adjunto',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Archivo Adjunto',
  `attach_date` date default NULL COMMENT 'Fecha del Archivo Adjunto',
  PRIMARY KEY  (`id`),
  KEY `registry` (`registry`),
  KEY `category` (`category`),
  KEY `IDX_RATTACH_SCOPE` (`scope`),
  CONSTRAINT `FK_RATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `rattach_ibfk_1` FOREIGN KEY (`category`) REFERENCES `category` (`id`),
  CONSTRAINT `rattach_ibfk_2` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Personas o Empresas';

#
# Structure for the `rbank_account` table : 
#

CREATE TABLE `rbank_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Cuenta Contable de la Cuenta Bancaria',
  `rbank` int(4) NOT NULL default '0' COMMENT 'Identificador de la Cuenta Bancaria',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `rbank` (`rbank`),
  KEY `account` (`account`),
  CONSTRAINT `rbank_account_ibfk_1` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`),
  CONSTRAINT `rbank_account_ibfk_2` FOREIGN KEY (`account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Entidades Bancarias';

#
# Structure for the `rdir_staff` table : 
#

CREATE TABLE `rdir_staff` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Relacion entre Empresas y sus Directivos',
  `registry` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `document` varchar(16) collate latin1_spanish_ci NOT NULL COMMENT 'Numero de Documento del Directivo',
  `name` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Directivo',
  `shareholder` tinyint(1) NOT NULL default '0' COMMENT 'Indica si el Directivo es socio',
  `representative` tinyint(1) NOT NULL default '0' COMMENT 'Indica si el Directivo es representante legal',
  `director` tinyint(1) NOT NULL default '0' COMMENT 'Indica si el Directivo es administrador',
  `percent_share` double default '0' COMMENT 'Porcentaje de acciones (solo para socios)',
  `share_number` int(4) default '0' COMMENT 'Numero de Acciones',
  `nominal_value` double(15,3) default '0.000' COMMENT 'Valor Nominal',
  `due_date` date default NULL COMMENT 'Fecha de vencimiento del cargo',
  `representative_labor` tinyint(1) NOT NULL default '0' COMMENT 'Indica si el Directivo es representante laboral',
  PRIMARY KEY  (`id`),
  KEY `registry` (`registry`),
  CONSTRAINT `rdir_staff_fk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Empresas y sus Directivos';

#
# Structure for the `record_data` table : 
#

CREATE TABLE `record_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Dato Registral',
  `registry` int(4) NOT NULL COMMENT 'Registro de la Empresa',
  `creation_date` date default NULL COMMENT 'Fecha de creacion del Dato Registral',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Dato Registral',
  `notary` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Notario del Dato Registral',
  `number` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Numero del Dato Registral',
  `record_date` date default NULL COMMENT 'Fecha de registro del Dato Registral',
  `volume` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Tomo del Dato Registral',
  `section` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Seccion del Dato Registral',
  `page` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Folio del Dato Registral',
  `sheet` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Hoja del Dato Registral',
  `registration` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Inscripcion del Dato Registral',
  `attach` int(4) default NULL COMMENT 'Archivo adjunto',
  PRIMARY KEY  (`id`),
  KEY `registry` (`registry`),
  KEY `attach` (`attach`),
  CONSTRAINT `record_data_fk` FOREIGN KEY (`attach`) REFERENCES `rattach` (`id`),
  CONSTRAINT `record_data_fk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos Registrales';

#
# Structure for the `relationship` table : 
#

CREATE TABLE `relationship` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Tipo de Relacion',
  `description` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Relacion',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Relaciones entre Personas y/o Empresas';

#
# Structure for the `rmedia` table : 
#

CREATE TABLE `rmedia` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Medio de Contacto de la Persona o Empresa',
  `registry` int(4) NOT NULL default '0' COMMENT 'Identificador del Registro de la Persona o Empresa',
  `media` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Medio de Contacto de la Persona o Empresa',
  `value` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Valor del Medio de Contacto de la Persona o Empresa',
  `comment` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Comentarios acerca del Medio de Contacto de la Persona o Empresa',
  `administrative` tinyint(1) default '1' COMMENT 'Indica si el Contacto es de caracter administrativo',
  `commercial` tinyint(1) default '1' COMMENT 'Indica si el Contacto es de caracter comercial',
  `technical` tinyint(1) default '1' COMMENT 'Indica si el Contacto es de caracter tecnico',
  `raddress` int(4) default NULL COMMENT 'Direccion del contacto',
  PRIMARY KEY  (`id`),
  KEY `idx_rmed_rgty` (`registry`),
  KEY `IDX_RMEDIA_RADDRESS` (`raddress`),
  CONSTRAINT `FK_RMEDIA_RADDRESS` FOREIGN KEY (`raddress`) REFERENCES `raddress` (`id`),
  CONSTRAINT `rmedia_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Medios de Contacto de Personas o Empresas';

#
# Structure for the `rnote` table : 
#

CREATE TABLE `rnote` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Nota de la Persona o Empresa',
  `registry` int(4) NOT NULL default '0' COMMENT 'Identificador del Registro de la Persona o Empresa',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Nota',
  `note_date` date default NULL COMMENT 'Fecha de la Nota',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios de la Nota',
  `note_type` tinyint(2) default NULL,
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad de la Nota',
  PRIMARY KEY  (`id`),
  KEY `registry` (`registry`),
  CONSTRAINT `rnote_fk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Notas de Personas o Empresas';

#
# Structure for the `rpaymethod` table : 
#

CREATE TABLE `rpaymethod` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Forma de Pago de la Persona o Empresa',
  `registry` int(4) NOT NULL COMMENT 'Identificador del Registro de la Persona o Empresa',
  `pay_method` int(4) NOT NULL COMMENT 'Identificador de la Forma de Pago',
  `rbank` int(4) default NULL COMMENT 'Identificador de la Entidad Bancaria',
  `number_of_pymnts` smallint(2) default '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint(2) default '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint(2) default '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) collate latin1_spanish_ci default '0' COMMENT 'Dias de pago',
  PRIMARY KEY  (`id`),
  KEY `pay_method` (`pay_method`),
  KEY `rbank` (`rbank`),
  KEY `registry` (`registry`),
  CONSTRAINT `rpaymethod_ibfk_1` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `rpaymethod_ibfk_2` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`),
  CONSTRAINT `rpaymethod_ibfk_3` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos de la Forma de Pago de la Persona o Empresa';

#
# Structure for the `rrelationship` table : 
#

CREATE TABLE `rrelationship` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Relacion',
  `registry` int(4) NOT NULL COMMENT 'Identificador de la Persona o Empresa que tiene la Relacion',
  `related_registry` int(4) NOT NULL COMMENT 'Identificador de la Persona o Empresa relacionada',
  `relationship` int(4) NOT NULL COMMENT 'Identificador del Tipo de Relación',
  `comments` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Comentarios de la Relacion',
  PRIMARY KEY  (`id`),
  KEY `registry` (`registry`),
  KEY `related_registry` (`related_registry`),
  CONSTRAINT `rrelationship_fk` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `rrelationship_fk1` FOREIGN KEY (`related_registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relaciones entre Personas y/o Empresas';

#
# Structure for the `segment` table : 
#

CREATE TABLE `segment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Segmento',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Segmentos Comerciales';

#
# Structure for the `rsegment` table : 
#

CREATE TABLE `rsegment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `registry` int(4) NOT NULL COMMENT 'Identificador de Persona o Empresa',
  `segment` int(4) NOT NULL COMMENT 'Identificador del Segmento',
  PRIMARY KEY  (`id`),
  KEY `IDX_REGISTRY_SEGMENT_REGISTRY` (`registry`),
  KEY `IDX_REGISTRY_SEGMENT_SEGMENT` (`segment`),
  CONSTRAINT `FK_REGISTRY_SEGMENT_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_REGISTRY_SEGMENT_SEGMENT` FOREIGN KEY (`segment`) REFERENCES `segment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Segmentos de Personas o Empresas';

#
# Structure for the `salary` table : 
#

CREATE TABLE `salary` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Nomina',
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
  `seniority_date` date default NULL COMMENT 'Fecha de antiguedad',
  `quote_group` varchar(2) collate latin1_spanish_ci default NULL COMMENT 'Grupo de Cotización',
  `category` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Categoria o grupo profesional',
  `registration` int(4) NOT NULL COMMENT 'Número libro de matricula',
  `time_units` int(4) NOT NULL COMMENT 'total dias/horas',
  `total_payment` double(15,3) NOT NULL default '0.000' COMMENT 'Total devengado',
  `total_deduction` double(15,3) NOT NULL default '0.000' COMMENT 'Total a deducir',
  `total_liquid` double(15,3) NOT NULL default '0.000' COMMENT 'Liquido total a percibir',
  `total_enterprise` double(15,3) NOT NULL default '0.000' COMMENT 'Cuota total de la empresa',
  `issue_date` date NOT NULL COMMENT 'Fecha de emisión',
  `remuneration` double(15,3) NOT NULL default '0.000' COMMENT 'Remuneración mensual',
  `pro_ext_base` double(15,3) NOT NULL default '0.000' COMMENT 'Base prorraterreada de pagas extras',
  `it_base` double(15,3) NOT NULL default '0.000' COMMENT 'Base de IT',
  `raw_cgc_base` double(15,3) NOT NULL default '0.000' COMMENT 'Base efectiva de cotizacion por contingencias comunes ',
  `cgc_base` double(15,3) NOT NULL default '0.000' COMMENT 'Base de cotizacion por contingencias comunes',
  `hextra_base` double(15,3) NOT NULL default '0.000' COMMENT 'Base de cotizacion adicional por horas extraordinarias estructurales',
  `non_hextra_base` double(15,3) NOT NULL default '0.000' COMMENT 'Base de cotizacion adicional por horas extraordinarias no estructurales',
  `cgp_base` double(15,3) NOT NULL default '0.000' COMMENT 'Base de cotizacion por contingencias profesionales',
  `money_irpf_base` double(15,3) NOT NULL default '0.000' COMMENT 'Salario en dinero sujeto a retención I.R.P.F',
  `inkind_irpf_base` double(15,3) NOT NULL default '0.000' COMMENT 'Salario en especie sujeto a retención I.R.P.F',
  `irpf_base` double(15,3) NOT NULL default '0.000' COMMENT 'Base sujeta a retención I.R.P.F',
  `social_security_contributions` double(15,3) NOT NULL default '0.000' COMMENT 'Aportaciones a la Seguridad Social',
  `total_irpf` double(15,3) NOT NULL default '0.000' COMMENT 'Total retención aplicada ',
  PRIMARY KEY  (`id`),
  KEY `IDX_SALARY_RECCEIPT_CONTRACT` (`contract`),
  CONSTRAINT `FK_SALARY_RECCEIPT_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Recibo del pago de salarios';

#
# Structure for the `salary_bonus` table : 
#

CREATE TABLE `salary_bonus` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios',
  `bonus_concept` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Codigo del concepto',
  `amount` double(15,3) default '0.000' COMMENT 'Importe',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_BONUS_SALARY` (`salary`),
  CONSTRAINT `FK_BONUS_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bonificaciones';

#
# Structure for the `salary_cost` table : 
#

CREATE TABLE `salary_cost` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios',
  `amount` double(15,3) default '0.000' COMMENT 'Importe',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `type` tinyint(2) default NULL COMMENT 'Tipo de deduccion Salarial',
  `cost_concept` varchar(10) collate latin1_spanish_ci default NULL COMMENT 'Codigo del concepto',
  PRIMARY KEY  (`id`),
  KEY `IDX_COST_SALARY` (`salary`),
  CONSTRAINT `FK_COST_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Costos';

#
# Structure for the `salary_data` table : 
#

CREATE TABLE `salary_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` tinyint(2) NOT NULL COMMENT 'Nombre',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos de la nomina';

#
# Structure for the `salary_deduction` table : 
#

CREATE TABLE `salary_deduction` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios',
  `type` tinyint(2) default NULL COMMENT 'Tipo de deducción Salarial',
  `deduction_concept` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Codigo del concepto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Fórmula',
  `amount` double(15,3) default '0.000' COMMENT 'Importe',
  PRIMARY KEY  (`id`),
  KEY `IDX_DEDUCTION_SALARY` (`salary`),
  CONSTRAINT `FK_DEDUCTION_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Deducciones';

#
# Structure for the `salary_embargo` table : 
#

CREATE TABLE `salary_embargo` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios',
  `contract_embargo` int(4) NOT NULL COMMENT 'Embargo',
  `amount` double(15,3) default '0.000' COMMENT 'Importe',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_EMBARGO_SALARY` (`salary`),
  KEY `IDX_SALARY_EMBARGO_CONTRACT_EMBARGO` (`contract_embargo`),
  CONSTRAINT `FK_EMBARGO_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`),
  CONSTRAINT `FK_SALARY_EMBARGO_CONTRACT_EMBARGO` FOREIGN KEY (`contract_embargo`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Embargos';

#
# Structure for the `salary_payment` table : 
#

CREATE TABLE `salary_payment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Percepción Salarial',
  `payment_concept` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Codigo del concepto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Fórmula',
  `amount` double(15,3) default '0.000' COMMENT 'Importe',
  PRIMARY KEY  (`id`),
  KEY `IDX_PAYMENT_SALARY` (`salary`),
  CONSTRAINT `FK_PAYMENT_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Percepciones salariales';

#
# Structure for the `sales_purchase` table : 
#

CREATE TABLE `sales_purchase` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Relacion de Pedidos de Compra y Venta',
  `sales_detail` int(4) NOT NULL COMMENT 'Identificador del Detalle del Pedido de Venta',
  `purchase_detail` int(4) NOT NULL COMMENT 'Identificador del Detalle del Pedido de Compra',
  PRIMARY KEY  (`id`),
  KEY `sales_detail` (`sales_detail`),
  KEY `purchase_detail` (`purchase_detail`),
  CONSTRAINT `sales_purchase_ibfk_1` FOREIGN KEY (`sales_detail`) REFERENCES `sales_detail` (`id`),
  CONSTRAINT `sales_purchase_ibfk_2` FOREIGN KEY (`purchase_detail`) REFERENCES `purchase_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Pedidos de Compra y Venta';

#
# Structure for the `scale` table : 
#

CREATE TABLE `scale` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Transferencia de datos entre Balanzas y Aon',
  `scale_model` tinyint(2) default NULL COMMENT 'Modelo de Balanza',
  `program_path` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Ruta donde se encuentra la base de datos de la Balanza',
  `inidate` date default NULL COMMENT 'Fecha de inicio de Transferencia de datos',
  `enddate` date default NULL COMMENT 'Fecha de fin de Transferencia de datos',
  `serie` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie de Albaran para la importacion de albaranes',
  `code1` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Primer codigo de control de Balanza',
  `code2` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Segundo codigo de control de Balanza',
  `code3` varchar(3) collate latin1_spanish_ci default NULL COMMENT 'Tercer codigo de control de Balanza',
  `code4` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Cuarto codigo de control de Balanza',
  `code5` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Quinto codigo de control de la Balanza',
  `verified` tinyint(1) default '0' COMMENT 'Indica si esta verificado o no',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Transferencias de datos entre Balanzas y Aon';

#
# Structure for the `scale_relation` table : 
#

CREATE TABLE `scale_relation` (
  `id` int(5) NOT NULL auto_increment COMMENT 'Identificador unico de la Relacion entre tablas de Balanzas y Aon',
  `aon_id` int(4) default NULL COMMENT 'Identificador de la tabla en Aon',
  `scale_id1` varchar(10) collate latin1_spanish_ci default NULL COMMENT 'Primer identificador de la tabla en la Balanza',
  `scale_id2` varchar(10) collate latin1_spanish_ci default NULL COMMENT 'Segundo identificador de la tabla en la Balanza',
  `type` char(1) collate latin1_spanish_ci default NULL COMMENT 'Indica el tipo de tabla que se esta relacionando',
  `scale_model` tinyint(2) default NULL COMMENT 'Modelo de Balanza',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relaciones entre tablas de Balanzas y Aon';

#
# Structure for the `series` table : 
#

CREATE TABLE `series` (
  `id` varchar(5) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador unico',
  `description` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Serie',
  `workplace` int(4) default NULL COMMENT 'Centro de Trabajo para el que se define la Serie',
  `tas` tinyint(1) default '0' COMMENT 'Indica si es una Serie para Ordenes de Reparacion',
  `offer` tinyint(1) default '0' COMMENT 'Indica si es una Serie para Presupuestos',
  `sales` tinyint(1) default '0' COMMENT 'Indica si es una Serie para Pedidos',
  `delivery` tinyint(1) default '0' COMMENT 'Indica si es una Serie para Albaranes',
  `invoice` tinyint(1) default '0' COMMENT 'Indica si es una Serie para Facturas',
  `rectification` tinyint(1) default '0' COMMENT 'Indica si es una Serie para Facturas rectificativas',
  `security_level` tinyint(2) NOT NULL COMMENT 'Nivel de seguridad de la Serie',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Serie esta activa o no',
  PRIMARY KEY  (`id`),
  KEY `workplace` (`workplace`),
  CONSTRAINT `series_fk` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Series';

#
# Structure for the `stock` table : 
#

CREATE TABLE `stock` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Stock',
  `warehouse` int(4) default NULL COMMENT 'Identificador del Almacen',
  `item` int(4) default NULL COMMENT 'Identificador del Articulo',
  `quantity` double(15,3) default '0.000' COMMENT 'Cantidad del Articulo en el Almacen',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `warehouse_item` (`warehouse`,`item`),
  KEY `warehouse` (`warehouse`),
  KEY `item` (`item`),
  CONSTRAINT `stock_ibfk_1` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`),
  CONSTRAINT `stock_ibfk_2` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Stocks de Almacenes';

#
# Structure for the `supplier_account` table : 
#

CREATE TABLE `supplier_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Cuenta Contable del Proveedor',
  `supplier` int(4) NOT NULL default '0' COMMENT 'Identificador del Proveedor',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `supplier` (`supplier`),
  KEY `account` (`account`),
  CONSTRAINT `supplier_account_ibfk_1` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`),
  CONSTRAINT `supplier_account_ibfk_2` FOREIGN KEY (`account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Proveedores';

#
# Structure for the `support_order` table : 
#

CREATE TABLE `support_order` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Orden de Reparacion',
  `tas_item` int(4) NOT NULL COMMENT 'Identificador del Articulo de la Orden de Reparacion',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie de la Orden de Reparacion',
  `number` int(4) NOT NULL default '0' COMMENT 'Numero de la Orden de Reparacion',
  `description` text collate latin1_spanish_ci COMMENT 'Descripcion de la Orden de Reparacion',
  `final_date` date default NULL COMMENT 'Fecha de finalizacion de la Orden de Reparacion',
  `status` tinyint(2) default NULL COMMENT 'Estado de la Orden de Reparacion',
  `start_date` date default '0000-00-00' COMMENT 'Fecha de inicio de la Orden de Reparacion',
  `employee` int(4) default NULL COMMENT 'Identificador del Empleado de la Orden de Reparacion',
  `counterti` double(15,3) default '0.000' COMMENT 'Contador del Articulo de la Orden de Reparacion (p.e. Kilometraje)',
  `levelti` varchar(8) collate latin1_spanish_ci default NULL COMMENT 'Nivel del Articulo de la Orden de Reparacion (p.e. Gasolina)',
  `operation` tinyint(2) default '0' COMMENT 'Operacion a realizar con la Orden de Reparacion',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `series` (`series`,`number`),
  KEY `tas_item` (`tas_item`),
  KEY `target` (`target`),
  KEY `employee` (`employee`),
  KEY `workplace` (`workplace`),
  CONSTRAINT `support_order_ibfk_1` FOREIGN KEY (`tas_item`) REFERENCES `tas_item` (`id`),
  CONSTRAINT `support_order_ibfk_2` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `support_order_ibfk_3` FOREIGN KEY (`employee`) REFERENCES `employee` (`registry`),
  CONSTRAINT `support_order_ibfk_4` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ordenes de Reparacion';

#
# Structure for the `support_order_insurance` table : 
#

CREATE TABLE `support_order_insurance` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `support_order` int(4) NOT NULL COMMENT 'Identificador de la Orden de Reparacion',
  `insurance` int(4) NOT NULL COMMENT 'Identificador de la Compañia de Seguros',
  `appraiser` int(4) default NULL COMMENT 'Identificador del Perito',
  `claim_number` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Numero de siniestro o reclamacion',
  `policy_type` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Tipo de poliza',
  `franchise` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Franquicia',
  PRIMARY KEY  (`id`),
  KEY `support_order` (`support_order`),
  KEY `insurance` (`insurance`),
  KEY `appraiser` (`appraiser`),
  CONSTRAINT `support_order_insurance_fk1` FOREIGN KEY (`support_order`) REFERENCES `support_order` (`id`),
  CONSTRAINT `support_order_insurance_fk2` FOREIGN KEY (`insurance`) REFERENCES `customer` (`registry`),
  CONSTRAINT `support_order_insurance_fk3` FOREIGN KEY (`appraiser`) REFERENCES `appraiser` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Compañias de Seguros asociadas a Ordenes de Reparacion';

#
# Structure for the `survey_question` table : 
#

CREATE TABLE `survey_question` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `survey` int(4) NOT NULL COMMENT 'Identificador del Cuestionario',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `position` int(11) default NULL COMMENT 'Posicion de la Pregunta dentro del Cuestionario',
  PRIMARY KEY  (`id`),
  KEY `survey` (`survey`),
  KEY `question` (`question`),
  CONSTRAINT `FK_SURVEY_QUESTION_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`),
  CONSTRAINT `FK_SURVEY_QUESTION_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Preguntas de Cuestionarios';

#
# Structure for the `survey_response_detail` table : 
#

CREATE TABLE `survey_response_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `value_text` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Valor de tipo texto',
  `value_number` double(15,3) default NULL COMMENT 'Valor de tipo numerico',
  `value_date` datetime default NULL COMMENT 'Valor de tipo fecha',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `surveyResponse` int(4) NOT NULL COMMENT 'Identificador de la Respuesta del Cuestionario',
  PRIMARY KEY  (`id`),
  KEY `question` (`question`),
  KEY `surveyResponse` (`surveyResponse`),
  CONSTRAINT `FK_SURVEY_RESPONSE_DETAIL_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`),
  CONSTRAINT `FK_SURVEY_RESPONSE_DETAIL_SURVEY_RESPONSE` FOREIGN KEY (`surveyResponse`) REFERENCES `survey_response` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Respuestas de Cuestionarios';

#
# Structure for the `survey_workflow` table : 
#

CREATE TABLE `survey_workflow` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `questionValue` int(4) default NULL COMMENT 'Identificador del Valor de la Pregunta',
  `surveyQuestion` int(4) NOT NULL COMMENT 'Identificador de la Pregunta del Cuestionario',
  `nextSurveyQuestion` int(4) NOT NULL COMMENT 'Identificador de la siguiente Pregunta del Cuestionario',
  `operator` tinyint(2) default NULL COMMENT 'Operador a utilizar con el Valor',
  `value_text` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Valor de tipo texto',
  `value_number` double(15,3) default NULL COMMENT 'Valor de tipo numerico',
  `value_date` datetime default NULL COMMENT 'Valor de tipo fecha',
  PRIMARY KEY  (`id`),
  KEY `questionValue` (`questionValue`),
  KEY `surveyQuestion` (`surveyQuestion`),
  KEY `nextSurveyQuestion` (`nextSurveyQuestion`),
  CONSTRAINT `FK_SURVEY_WORKFLOW_NEXT_SURVEY_QUESTION` FOREIGN KEY (`nextSurveyQuestion`) REFERENCES `survey_question` (`id`),
  CONSTRAINT `FK_SURVEY_WORKFLOW_QUESTION_VALUE` FOREIGN KEY (`questionValue`) REFERENCES `question_value` (`id`),
  CONSTRAINT `FK_SURVEY_WORKFLOW_SURVEY_QUESTION` FOREIGN KEY (`surveyQuestion`) REFERENCES `survey_question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Secuencias de Cuestionarios';

#
# Structure for the `system_cost` table : 
#

CREATE TABLE `system_cost` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `expression` text collate latin1_spanish_ci COMMENT 'Expresion',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Costo',
  `code` varchar(10) collate latin1_spanish_ci default NULL COMMENT 'Código',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Costos';

#
# Structure for the `system_data` table : 
#

CREATE TABLE `system_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre',
  `expression` text collate latin1_spanish_ci COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `read_only` tinyint(1) default NULL COMMENT 'Modificable',
  `comments` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Comentario de ayuda',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto de las funciones';

#
# Structure for the `system_deduction` table : 
#

CREATE TABLE `system_deduction` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Deducción',
  `deduction_concept` int(4) default NULL COMMENT 'Identificador unico del concepto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `description_decorable` tinyint(2) NOT NULL default '0',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Fórmula',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `month` tinyint(2) default NULL COMMENT 'Mes de la deducción',
  PRIMARY KEY  (`id`),
  KEY `IDX_SYSTEM_DEDUCTION_DEDUCTION_CONCEPT` (`deduction_concept`),
  CONSTRAINT `FK_SYSTEM_DEDUCTION_DEDUCTION_CONCEPT` FOREIGN KEY (`deduction_concept`) REFERENCES `deduction_concept` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Deducciones';

#
# Structure for the `system_payment` table : 
#

CREATE TABLE `system_payment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Percepción Salarial',
  `payment_concept` int(4) default NULL COMMENT 'Identificador unico del concepto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `description_decorable` tinyint(2) NOT NULL default '0',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe',
  `irpf_expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe tributable',
  `quote_expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe cotizable',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio',
  `month` tinyint(2) default NULL COMMENT 'Mes de la percepcion',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `salary_type` tinyint(2) default NULL COMMENT 'Tipo de Nomina/Recibo',
  PRIMARY KEY  (`id`),
  KEY `IDX_SYSTEM_PAYMENT_PAYMENT_CONCEPT` (`payment_concept`),
  CONSTRAINT `FK_SYSTEM_PAYMENT_PAYMENT_CONCEPT` FOREIGN KEY (`payment_concept`) REFERENCES `payment_concept` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Percepciones Salariales';

#
# Structure for the `target_item` table : 
#

CREATE TABLE `target_item` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo',
  `status` tinyint(2) NOT NULL COMMENT 'Estado',
  PRIMARY KEY  (`id`),
  KEY `target` (`target`),
  KEY `item` (`item`),
  CONSTRAINT `target_item_fk` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `target_item_fk1` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulos interesados por Cliente Potencial';

#
# Structure for the `target_profile` table : 
#

CREATE TABLE `target_profile` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `last_update` datetime NOT NULL COMMENT 'Fecha de la ultima modificacion del Perfil del Cliente Potencial',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `value_text` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Valor de tipo texto',
  `value_number` double(15,3) default NULL COMMENT 'Valor de tipo numerico',
  `value_date` datetime default NULL COMMENT 'Valor de tipo fecha',
  PRIMARY KEY  (`id`),
  KEY `question` (`question`),
  KEY `target` (`target`),
  CONSTRAINT `FK_TARGET_PROFILE_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`),
  CONSTRAINT `FK_TARGET_PROFILE_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Perfiles de Clientes Potenciales';

#
# Structure for the `target_seller` table : 
#

CREATE TABLE `target_seller` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `seller` int(4) NOT NULL COMMENT 'Identificador del Comercial',
  `start_date` date NOT NULL COMMENT 'Fecha de Inicio',
  `end_date` date default NULL COMMENT 'Fecha de Fin',
  `status` tinyint(2) NOT NULL COMMENT 'Estado',
  PRIMARY KEY  (`id`),
  KEY `target` (`target`),
  KEY `seller` (`seller`),
  CONSTRAINT `target_seller_fk` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `target_seller_fk1` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comerciales relacionado con Cliente Potencial';

#
# Structure for the `target_supplier` table : 
#

CREATE TABLE `target_supplier` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `supplier` int(4) NOT NULL COMMENT 'Identificador del Proveedor',
  `target_external_code` varchar(15) collate latin1_spanish_ci default NULL COMMENT 'Codigo del Cliente Potencial para el Proveedor',
  `tariff` int(4) default NULL COMMENT 'Identificador de Tarifa',
  `pay_method` int(4) default NULL COMMENT 'Identificador de la Forma de Pago',
  `number_of_pymnts` smallint(2) default '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint(2) default '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint(2) default '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) collate latin1_spanish_ci default '0' COMMENT 'Dias de pago',
  `bank` int(4) default NULL COMMENT 'Identificador de la Entidad Bancaria',
  `bank_account` varchar(30) collate latin1_spanish_ci default NULL COMMENT 'Numero de cuenta en la Entidad Bancaria',
  PRIMARY KEY  (`id`),
  KEY `IDX_TARGET_SUPPLIER_TARGET` (`target`),
  KEY `IDX_TARGET_SUPPLIER_SUPPLIER` (`supplier`),
  KEY `IDX_TARGET_SUPPLIER_TARIFF` (`tariff`),
  KEY `IDX_TARGET_SUPPLIER_PAY_METHOD` (`pay_method`),
  KEY `IDX_TARGET_SUPPLIER_BANK` (`bank`),
  CONSTRAINT `FK_TARGET_SUPPLIER_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`),
  CONSTRAINT `FK_TARGET_SUPPLIER_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_TARGET_SUPPLIER_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`),
  CONSTRAINT `FK_TARGET_SUPPLIER_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_TARGET_SUPPLIER_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion de Clientes Potenciales con Proveedores';

#
# Structure for the `tariff_catalogue` table : 
#

CREATE TABLE `tariff_catalogue` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `tariff` int(4) NOT NULL COMMENT 'Identificador de la Tarifa',
  `catalogue` int(4) NOT NULL COMMENT 'Identificador del Catalogo',
  PRIMARY KEY  (`id`),
  KEY `tariff` (`tariff`),
  KEY `catalogue` (`catalogue`),
  CONSTRAINT `tariff_catalogue_fk1` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`),
  CONSTRAINT `tariff_catalogue_fk2` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas por Catalogo';

#
# Structure for the `tas_delivery` table : 
#

CREATE TABLE `tas_delivery` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Relacion de Ordenes de Reparacion y Albaranes',
  `support_order` int(4) NOT NULL COMMENT 'Identificador de la Orden de Reparacion',
  `delivery` int(4) NOT NULL COMMENT 'Identificador del Albaran',
  `offer` int(4) default NULL COMMENT 'Identificador del Presupuesto',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `delivery` (`delivery`),
  KEY `support_order` (`support_order`),
  KEY `offer` (`offer`),
  CONSTRAINT `tas_delivery_ibfk_1` FOREIGN KEY (`support_order`) REFERENCES `support_order` (`id`),
  CONSTRAINT `tas_delivery_ibfk_2` FOREIGN KEY (`delivery`) REFERENCES `delivery` (`id`),
  CONSTRAINT `tas_delivery_ibfk_3` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Ordenes de Reparacion y Albaranes';

#
# Structure for the `tas_offer` table : 
#

CREATE TABLE `tas_offer` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Relacion de Ordenes de Reparacion y Presupuestos',
  `support_order` int(4) NOT NULL COMMENT 'Identificador de la Orden de Reparacion',
  `offer` int(4) NOT NULL COMMENT 'Identificador del Presupuesto',
  PRIMARY KEY  (`id`),
  KEY `support_order` (`support_order`),
  KEY `offer` (`offer`),
  CONSTRAINT `tas_offer_ibfk_1` FOREIGN KEY (`support_order`) REFERENCES `support_order` (`id`),
  CONSTRAINT `tas_offer_ibfk_2` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Ordenes de Reparacion y Presupuestos';

#
# Structure for the `task_holder_workgroup` table : 
#

CREATE TABLE `task_holder_workgroup` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `task_holder` int(4) NOT NULL COMMENT 'Identificador del Responsable de la Tarea',
  `workgroup` int(4) NOT NULL COMMENT 'Identificador del Grupo de Trabajo',
  PRIMARY KEY  (`id`),
  KEY `IDX_TASK_HOLDER_WORKGROUP_TASK_HOLDER` (`task_holder`),
  KEY `IDX_TASK_HOLDER_WORKGROUP_WORKGROUP` (`workgroup`),
  CONSTRAINT `FK_TASK_HOLDER_WORKGROUP_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`),
  CONSTRAINT `FK_TASK_HOLDER_WORKGROUP_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Usuarios y Grupos de Trabajo';

#
# Structure for the `tax_account` table : 
#

CREATE TABLE `tax_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Cuenta Contable del Impuesto',
  `tax` int(4) NOT NULL default '0' COMMENT 'Identificador del Impuesto',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Cuenta Contable del Impuesto',
  PRIMARY KEY  (`id`),
  KEY `tax` (`tax`),
  KEY `account` (`account`),
  CONSTRAINT `tax_account_ibfk_1` FOREIGN KEY (`tax`) REFERENCES `tax` (`id`),
  CONSTRAINT `tax_account_ibfk_2` FOREIGN KEY (`account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Impuestos de la Factura';

#
# Structure for the `tax_detail` table : 
#

CREATE TABLE `tax_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Historico de Impuestos',
  `tax` int(4) NOT NULL default '0' COMMENT 'Identificador del Impuesto',
  `start_date` date default NULL COMMENT 'Fecha de inicio de vigencia',
  `end_date` date default NULL COMMENT 'Fecha de fin de vigencia',
  `value` double(15,3) default NULL COMMENT 'Porcentaje de recargo',
  `surcharge` double(15,3) default NULL COMMENT 'Porcentaje de recargo de equivalencia',
  PRIMARY KEY  (`id`),
  KEY `tax` (`tax`),
  CONSTRAINT `tax_detail_ibfk_1` FOREIGN KEY (`tax`) REFERENCES `tax` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Historico de Impuestos';

#
# Structure for the `user_scope` table : 
#

CREATE TABLE `user_scope` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `user_id` int(4) NOT NULL COMMENT 'Identificador del Usuario',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  PRIMARY KEY  (`id`),
  KEY `scope` (`scope`),
  KEY `user` (`user_id`),
  CONSTRAINT `user_scope_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `user_scope_fk1` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ambitos de Usuario';

#
# Structure for the `user_workgroup` table : 
#

CREATE TABLE `user_workgroup` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `user_id` int(4) NOT NULL COMMENT 'Identificador del Usuario',
  `workgroup` int(4) NOT NULL COMMENT 'Identificador del Grupo de Trabajo',
  PRIMARY KEY  (`id`),
  KEY `workgroup` (`workgroup`),
  KEY `user` (`user_id`),
  CONSTRAINT `user_workgroup_fk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `user_workgroup_fk_2` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Usuarios y Grupos de Trabajo';

#
# Structure for the `warehouse_transfer` table : 
#

CREATE TABLE `warehouse_transfer` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie del Traspaso',
  `number` int(4) NOT NULL default '0' COMMENT 'Numero del Traspaso',
  `issue_time` datetime NOT NULL COMMENT 'Fecha de emision del Traspaso',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios del Traspaso',
  `source_warehouse` int(4) default NULL COMMENT 'Identificador del Almacen Origen',
  `target_warehouse` int(4) default NULL COMMENT 'Identificador del Almacen Destino',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `series` (`series`,`number`),
  KEY `IDX_WT_ISSUE_TIME` (`issue_time`),
  KEY `IDX_WT_SOURCE` (`source_warehouse`),
  KEY `IDX_WT_TARGET` (`target_warehouse`),
  CONSTRAINT `FK_WT_SOURCE` FOREIGN KEY (`source_warehouse`) REFERENCES `warehouse` (`id`),
  CONSTRAINT `FK_WT_TARGET` FOREIGN KEY (`target_warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Traspasos entre Almacenes';

#
# Structure for the `warehouse_transfer_detail` table : 
#

CREATE TABLE `warehouse_transfer_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `warehouse_transfer` int(4) NOT NULL COMMENT 'Identificador del Traspaso',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo del Detalle de Traspaso',
  `quantity` double(15,3) default NULL COMMENT 'Cantidad del Detalle de Traspaso',
  PRIMARY KEY  (`id`),
  KEY `IDX_WTD_WT` (`warehouse_transfer`),
  KEY `IDX_WTD_ITEM` (`item`),
  CONSTRAINT `FK_WTD_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_WTD_WT` FOREIGN KEY (`warehouse_transfer`) REFERENCES `warehouse_transfer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Traspasos entre Almacenes';

#
# Structure for the `web_info` table : 
#

CREATE TABLE `web_info` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `company` int(4) NOT NULL COMMENT 'Empresa',
  `commercial_description` text collate latin1_spanish_ci COMMENT 'Descripcion comercial',
  `schedule` text collate latin1_spanish_ci COMMENT 'Horario',
  `slogan` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Slogan',
  PRIMARY KEY  (`id`),
  KEY `company` (`company`),
  CONSTRAINT `web_info_fk` FOREIGN KEY (`company`) REFERENCES `company` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Informacion de la empresa que se mostrara en la ficha web';

#
# Structure for the `web_info_page` table : 
#

CREATE TABLE `web_info_page` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Codigo de la Pagina',
  `name` varchar(64) character set latin1 collate latin1_spanish_ci default NULL COMMENT 'Nombre de la Pagina.',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Pagina',
  `position` tinyint(2) default NULL COMMENT 'Posicion de la Pagina en el menu',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indica si la Pagina esta activa o no',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Paginas pertenecientes a la ficha web';

#
# Structure for the `web_info_page_detail` table : 
#

CREATE TABLE `web_info_page_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Codigo del Detalle de la Pagina',
  `web_info_page` int(4) NOT NULL COMMENT 'Identificador de la Pagina a la que corresponde el detalle',
  `title` varchar(255) character set latin1 collate latin1_spanish_ci default NULL COMMENT 'Titulo del contenido de la Pagina',
  `layout` int(2) default NULL COMMENT 'Tipo de plantilla',
  `content` text character set latin1 collate latin1_spanish_ci COMMENT 'Texto del contenido de la Pagina',
  `extra` varchar(255) character set latin1 collate latin1_spanish_ci default NULL COMMENT 'Campo reservado a otros datos de la Pagina',
  PRIMARY KEY  (`id`),
  KEY `web_info_page` (`web_info_page`),
  CONSTRAINT `web_info_page_detail_fk1` FOREIGN KEY (`web_info_page`) REFERENCES `web_info_page` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Detalles de la pagina perteneciente a la ficha web';

#
# Structure for the `web_info_page_resource` table : 
#

CREATE TABLE `web_info_page_resource` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Codigo del Recurso de la Pagina',
  `web_info_page` int(4) NOT NULL COMMENT 'Codigo de la Pagina',
  `rattach` int(4) NOT NULL COMMENT 'Identificador del Archivo Adjunto calificado como Recurso',
  `content` varchar(255) character set latin1 collate latin1_spanish_ci default NULL COMMENT 'Texto del Recurso',
  PRIMARY KEY  (`id`),
  KEY `web_info_page` (`web_info_page`),
  KEY `rattach` (`rattach`),
  CONSTRAINT `web_info_page_resource_fk1` FOREIGN KEY (`web_info_page`) REFERENCES `web_info_page` (`id`),
  CONSTRAINT `web_info_page_resource_fk2` FOREIGN KEY (`rattach`) REFERENCES `rattach` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Recursos de la pagina perteneciente a la ficha web';

#
# Structure for the `web_info_style` table : 
#

CREATE TABLE `web_info_style` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Codigo del Estilo de la Pagina',
  `variable` varchar(128) character set latin1 collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la variable del Estilo',
  `value` varchar(255) character set latin1 collate latin1_spanish_ci default NULL COMMENT 'Valor de la variable del Estilo',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Estilos a utilizar en las plantillas para generar ficha web';


INSERT INTO `db_version` (`version_number`) VALUES '6.18.7');

COMMIT;


SET FOREIGN_KEY_CHECKS=1;

