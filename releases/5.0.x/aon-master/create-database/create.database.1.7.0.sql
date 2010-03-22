# Database : aon_master
# Version: 1.7.0
# Created by: girazu
# Creation Date: 11/06/2008 12:13


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
# Structure for the `registry` table : 
#

CREATE TABLE `registry` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Persona o Empresa',
  `document` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Numero de Documento de la Persona o Empresa',
  `name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Nombre de la Persona o Empresa',
  `surname` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Apellido de la Persona o Empresa',
  `alias` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Alias de la Persona o Empresa',
  `type` tinyint(2) default NULL COMMENT 'Tipo (Persona o Empresa)',
  PRIMARY KEY  (`id`),
  KEY `idx_rgty_name` (`name`,`surname`),
  KEY `idx_rgty_document` (`document`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Registro de Personas o Empresas';

#
# Structure for the `geozone` table : 
#

CREATE TABLE `geozone` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Zona Geografica',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Zona Geografica',
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
  `street_type` tinyint(2) default '0' COMMENT 'Tipo de via',
  `address` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Primera parte de la Direccion',
  `address2` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Segunda parte de la Direccion',
  `address3` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Tercera parte de la Direccion',
  `zip` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo Postal',
  `city` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Localidad',
  `geozone` int(4) default NULL COMMENT 'Identificador de la Zona Geografica',
  PRIMARY KEY  (`id`),
  KEY `idx_radr_rgty` (`registry`),
  KEY `idx_radr_gzne` (`geozone`),
  CONSTRAINT `raddress_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `raddress_ibfk_2` FOREIGN KEY (`geozone`) REFERENCES `geozone` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Direcciones de Personas o Empresas';

#
# Structure for the `calendar` table : 
#

CREATE TABLE `calendar` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Calendario',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Calendario',
  `data` blob COMMENT 'Calendario Laboral en binario',
  `allow_spread` tinyint(1) default '1' COMMENT 'Indica si el Calendario permite propagar eventos',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Calendarios Laborales';

#
# Structure for the `workplace` table : 
#

CREATE TABLE `workplace` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Centro de Trabajo',
  `description` varchar(64) NOT NULL COMMENT 'Descripcion del Centro de Trabajo',
  `address` int(4) NOT NULL COMMENT 'Identificador de la Direccion',
  `calendar` int(4) default NULL COMMENT 'Identificador del Calendario Laboral',
  `active` tinyint(1) default '1' COMMENT 'Indica si el Centro de Trabajo esta activo o no',
  PRIMARY KEY  (`id`),
  KEY `address` (`address`),
  KEY `calendar` (`calendar`),
  CONSTRAINT `workplace_ibfk_1` FOREIGN KEY (`address`) REFERENCES `raddress` (`id`),
  CONSTRAINT `workplace_ibfk_2` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`)
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
# Structure for the `scope` table : 
#

CREATE TABLE `scope` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `description` varchar(16) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Ambito',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ambitos';

#
# Structure for the `customer` table : 
#

CREATE TABLE `customer` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro del Cliente',
  `tariff` int(4) default NULL COMMENT 'Tarifa asociada al Cliente',
  `taxfree` tinyint(1) default NULL COMMENT 'Indica si el Cliente esta exento de Impuestos',
  `surcharge` tinyint(1) default NULL COMMENT 'Indica si el Cliente tiene recargo de equivalencia',
  `withholding` tinyint(1) default '0' COMMENT 'Indica si el Cliente aplica retencion de impuestos',
  `status` tinyint(2) default NULL COMMENT 'Estado del Cliente',
  `segment` int(4) default NULL COMMENT 'Segmento del Cliente',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
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
  `alias` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Alias de la Cuenta',
  `entryEnabled` tinyint(2) default '0' COMMENT 'Indica si la Cuenta permite o no Apuntes',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables';

#
# Structure for the `account_period` table : 
#

CREATE TABLE `account_period` (
  `id` char(4) collate latin1_spanish_ci NOT NULL COMMENT 'Código del Ejercicio',
  `initiation_date` date NOT NULL COMMENT 'Fecha de inicio del Ejercicio',
  `deadline` date NOT NULL COMMENT 'Fecha final del Ejercicio',
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
  PRIMARY KEY  (`id`),
  KEY `account_entry_account_period_idx` (`account_period`),
  CONSTRAINT `account_entry_ibfk_1` FOREIGN KEY (`account_period`) REFERENCES `account_period` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Asientos Contables';

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
  PRIMARY KEY  (`id`),
  KEY `account_entry_detail_FKIndex3` (`account`),
  KEY `account_entry_detail_FKIndex4` (`balancing_account`),
  KEY `account_entry` (`account_entry`),
  CONSTRAINT `account_entry_detail_ibfk_2` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `account_entry_detail_ibfk_3` FOREIGN KEY (`balancing_account`) REFERENCES `account` (`id`),
  CONSTRAINT `account_entry_detail_ibfk_4` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Apuntes Contables';

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
  PRIMARY KEY  (`id`),
  KEY `idx_rbnk_rgty` (`registry`),
  KEY `idx_rbnk_bank` (`bank`),
  CONSTRAINT `rbank_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `rbank_ibfk_2` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos de Cuentas Bancarias de Personas o Empresas';

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
  `payment` tinyint(1) NOT NULL default '0' COMMENT 'Indica si es un pago o un cobro',
  PRIMARY KEY  (`id`),
  KEY `rbank` (`rbank`),
  CONSTRAINT `fbatch_fk_1` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
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
# Structure for the `invoice` table : 
#

CREATE TABLE `invoice` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Factura',
  `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie de la Factura',
  `number` int(4) NOT NULL default '0' COMMENT 'Numero de la Factura',
  `reference_code` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Codigo de referencia de la Factura',
  `registry` int(4) NOT NULL default '0' COMMENT 'Identificador del Cliente o Proveedor',
  `rdocument` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Numero de Documento del Cliente o Proveedor',
  `rname` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Nombre completo del Cliente o Proveedor',
  `raddress` int(4) default NULL COMMENT 'Identificador de la Direccion de envio de la Factura',
  `issue_date` date default NULL COMMENT 'Fecha de emision de la Factura',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad de la Factura',
  `status` tinyint(2) default '0' COMMENT 'Estado de la Factura',
  `type` tinyint(2) default '0' COMMENT 'Tipo de Factura (Compra o Venta)',
  `taxFree` tinyint(1) default '0' COMMENT 'Indica si la Factura esta exenta de Impuestos',
  `surcharge` tinyint(1) default '0' COMMENT 'Indica si la Factura tiene recargo de equivalencia',
  `withholding` tinyint(1) default '0' COMMENT 'Indica si la Factura aplica retencion de impuestos',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios de la Factura',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `series` (`series`,`number`,`type`),
  KEY `idx_invc_radr` (`raddress`),
  KEY `idx_invc_date` (`issue_date`),
  KEY `registry` (`registry`),
  KEY `series_number` (`series`,`number`),
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
  `amount` double default '0' COMMENT 'Importe del Vencimiento',
  `expenses` double(15,3) default '0.000' COMMENT 'Gastos asociados al Vencimiento',
  `concept` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Concepto del Vencimiento',
  `invoice` int(4) default NULL COMMENT 'Identificador de la Factura',
  `due_date` date default NULL COMMENT 'Fecha de Vencimiento',
  `pay_method` int(4) default NULL COMMENT 'Identificador de la Forma de Pago',
  `bank` int(4) default NULL COMMENT 'Identificador de la Entidad Bancaria del Vencimiento',
  `bank_account` varchar(30) collate latin1_spanish_ci default NULL COMMENT 'Numero de cuenta en la Entidad Bancaria del Vencimiento',
  `status` tinyint(2) default '0' COMMENT 'Estado del Vencimiento',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Vencimiento',
  PRIMARY KEY  (`id`),
  KEY `idx_finc_rgty` (`registry`),
  KEY `idx_finc_pymt` (`pay_method`),
  KEY `idx_finc_bank` (`bank`),
  KEY `idx_finc_dtty` (`due_date`),
  KEY `invoice` (`invoice`),
  CONSTRAINT `finance_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `finance_ibfk_2` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `finance_ibfk_3` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`),
  CONSTRAINT `finance_ibfk_4` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Vencimientos';

#
# Structure for the `finance_tracking` table : 
#

CREATE TABLE `finance_tracking` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `finance` int(4) NOT NULL COMMENT 'Identificador de Vencimiento',
  `tracking_date` date NOT NULL COMMENT 'Fecha de Seguimiento',
  `type` tinyint(4) NOT NULL COMMENT 'Tipo de Seguimiento',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Seguimiento',
  `amount` double(15,3) default NULL COMMENT 'Importe del Seguimiento',
  PRIMARY KEY  (`id`),
  KEY `finance` (`finance`),
  CONSTRAINT `finance_tracking_fk` FOREIGN KEY (`finance`) REFERENCES `finance` (`id`)
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
# Structure for the `workgroup` table : 
#

CREATE TABLE `workgroup` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Grupo de Trabajo',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Grupo de Trabajo',
  `status` tinyint(2) default NULL COMMENT 'Estado del grupo de Trabajo',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Grupos de Trabajo';

#
# Structure for the `dossier_type` table : 
#

CREATE TABLE `dossier_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Tipo de Expediente',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Expediente',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Expedientes';

#
# Structure for the `activity_type` table : 
#

CREATE TABLE `activity_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Tipo de Actividad',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Actividad',
  `dossier_type` int(4) NOT NULL COMMENT 'Tipo de Dossier',
  PRIMARY KEY  (`id`),
  KEY `dossier_type` (`dossier_type`),
  CONSTRAINT `activity_type_fk` FOREIGN KEY (`dossier_type`) REFERENCES `dossier_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Actividades';

#
# Structure for the `dossier` table : 
#

CREATE TABLE `dossier` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Expediente',
  `customer` int(4) NOT NULL COMMENT 'Identificador del Cliente',
  `dossier_type` int(4) NOT NULL COMMENT 'Tipo de Expediente',
  `number` varchar(16) collate latin1_spanish_ci NOT NULL COMMENT 'Numero de Expediente',
  `location` varchar(20) collate latin1_spanish_ci default NULL COMMENT 'Ubicacion del Expediente',
  `status` tinyint(2) NOT NULL COMMENT 'Estado del Expediente',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `number` (`number`),
  KEY `customer` (`customer`),
  KEY `dossier_type` (`dossier_type`),
  CONSTRAINT `dossier_ibfk_2` FOREIGN KEY (`dossier_type`) REFERENCES `dossier_type` (`id`),
  CONSTRAINT `dossier_ibfk_1` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Expedientes';

#
# Structure for the `activity` table : 
#

CREATE TABLE `activity` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Actividad',
  `dossier` int(4) NOT NULL COMMENT 'Identificador del Expendiente',
  `activity_type` int(4) NOT NULL COMMENT 'Tipo de Actividad',
  `workgroup` int(4) NOT NULL COMMENT 'Identificador del Grupo de Trabajo',
  PRIMARY KEY  (`id`),
  KEY `activity_type` (`activity_type`),
  KEY `dossier` (`dossier`),
  KEY `workgroup` (`workgroup`),
  CONSTRAINT `activity_fk_3` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`),
  CONSTRAINT `activity_ibfk_1` FOREIGN KEY (`activity_type`) REFERENCES `activity_type` (`id`),
  CONSTRAINT `activity_ibfk_2` FOREIGN KEY (`dossier`) REFERENCES `dossier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades';

#
# Structure for the `process` table : 
#

CREATE TABLE `process` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Proceso',
  `description` varchar(30) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Proceso.',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Procesos';

#
# Structure for the `campaign` table : 
#

CREATE TABLE `campaign` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Campaña',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Campaña',
  `process` int(4) NOT NULL COMMENT 'Identificador del Proceso',
  `activity_type` int(4) NOT NULL COMMENT 'Identificador del Tipo de Actividad',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio de la Campaña',
  `end_date` date NOT NULL COMMENT 'Fecha de finalizacion de la Campaña',
  `workgroup` int(4) NOT NULL COMMENT 'Grupo de Trabajo supervisor de la Campaña',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Campaña',
  `status` tinyint(2) default NULL COMMENT 'Estado de la Campaña',
  PRIMARY KEY  (`id`),
  KEY `process` (`process`),
  KEY `activity_type` (`activity_type`),
  KEY `workgroup` (`workgroup`),
  CONSTRAINT `campaign_ibfk_1` FOREIGN KEY (`process`) REFERENCES `process` (`id`),
  CONSTRAINT `campaign_ibfk_2` FOREIGN KEY (`activity_type`) REFERENCES `activity_type` (`id`),
  CONSTRAINT `campaign_ibfk_3` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Campañas';

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
  PRIMARY KEY  (`id`),
  KEY `process` (`process`),
  KEY `workgroup` (`workgroup`),
  CONSTRAINT `process_detail_fk_2` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`),
  CONSTRAINT `process_detail_ibfk_1` FOREIGN KEY (`process`) REFERENCES `process` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Procesos';

#
# Structure for the `user` table : 
#

CREATE TABLE `user` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Usuario',
  `login` varchar(16) collate latin1_spanish_ci NOT NULL COMMENT 'Login del Usuario',
  `available` tinyint(1) NOT NULL COMMENT 'Indica si el Usuario esta disponible o no',
  `validate` tinyint(1) NOT NULL default '0' COMMENT 'Indica si el Usuario requiere validacion o no de la clave hardware',
  `aon_key` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Campo alfanumerico donde se guarda la ultima clave hardware generada',
  `status` tinyint(2) default '0' COMMENT 'Estado del Usuario con respecto a su primera validacion de la clave hardware',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Usuarios';

#
# Structure for the `task` table : 
#

CREATE TABLE `task` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Tarea',
  `description` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Tarea',
  `start_date` date default NULL COMMENT 'Fecha de inicio de la Tarea',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion de la Tarea',
  `due_date` date NOT NULL COMMENT 'Fecha de vencimiento de la Tarea',
  `priority` tinyint(2) default '0' COMMENT 'Prioridad de la Tarea',
  `status` tinyint(2) default '0' COMMENT 'Estado de la Tarea',
  `percent` tinyint(2) default '0' COMMENT 'Porcentaje de realizacion de la Tarea',
  `user` int(4) default NULL COMMENT 'Identificador del Usuario asociado a la Tarea',
  `workgroup` int(4) default NULL COMMENT 'Identificador del Grupo de Trabajo asociado a la Tarea',
  `source` tinyint(2) default NULL COMMENT 'Origen de la Tarea',
  `dossier` int(4) default NULL COMMENT 'Identificador del Expediente',
  `activity` int(4) default NULL COMMENT 'Identificador de la Actividad',
  `sender` int(4) default NULL COMMENT 'Remitente de la Tarea',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios de la Tarea',
  PRIMARY KEY  (`id`),
  KEY `user` (`user`),
  KEY `workgroup` (`workgroup`),
  KEY `dossier` (`dossier`),
  KEY `sender` (`sender`),
  KEY `activity` (`activity`),
  CONSTRAINT `task_fk_1` FOREIGN KEY (`user`) REFERENCES `user` (`id`),
  CONSTRAINT `task_fk_2` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`),
  CONSTRAINT `task_fk_3` FOREIGN KEY (`dossier`) REFERENCES `dossier` (`id`),
  CONSTRAINT `task_fk_4` FOREIGN KEY (`sender`) REFERENCES `user` (`id`),
  CONSTRAINT `task_fk_5` FOREIGN KEY (`activity`) REFERENCES `activity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tareas';

#
# Structure for the `activity_process` table : 
#

CREATE TABLE `activity_process` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Relacion entre Campañas, Actividades y Tareas',
  `campaign` int(4) NOT NULL COMMENT 'Identificador de la Campaña',
  `activity` int(4) NOT NULL COMMENT 'Identificador de la Actividad',
  `process_detail` int(4) NOT NULL COMMENT 'Identificador del Detalle de Proceso',
  `task` int(4) NOT NULL COMMENT 'Identificador de la Tarea',
  PRIMARY KEY  (`id`),
  KEY `campaign` (`campaign`),
  KEY `activity` (`activity`),
  KEY `process_detail` (`process_detail`),
  KEY `task` (`task`),
  CONSTRAINT `activity_process_fk_1` FOREIGN KEY (`campaign`) REFERENCES `campaign` (`id`),
  CONSTRAINT `activity_process_fk_2` FOREIGN KEY (`activity`) REFERENCES `activity` (`id`),
  CONSTRAINT `activity_process_fk_3` FOREIGN KEY (`process_detail`) REFERENCES `process_detail` (`id`),
  CONSTRAINT `activity_process_fk_4` FOREIGN KEY (`task`) REFERENCES `task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Campañas, Actividades y Tareas';

#
# Structure for the `alarm` table : 
#

CREATE TABLE `alarm` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Alarma',
  `description` text collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Alarma',
  `alarm_date` datetime NOT NULL COMMENT 'Fecha y hora de ejecucion de la Alarma',
  `status` tinyint(2) default NULL COMMENT 'Estado de la Alarma',
  `source` tinyint(2) NOT NULL COMMENT 'Origen de la Alarma',
  `source_id` int(4) NOT NULL COMMENT 'Identificador del origen de la Alarma',
  `user` int(4) default NULL COMMENT 'Identificador del Usuario asociado a la Alarma',
  `priority` tinyint(2) NOT NULL COMMENT 'Prioridad de la Alarma',
  PRIMARY KEY  (`id`),
  KEY `user` (`user`),
  CONSTRAINT `alarm_fk` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
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
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Prestamos a Alumnos';

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
# Structure for the `auto_concept` table : 
#

CREATE TABLE `auto_concept` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Concepto Automatico',
  `description` char(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Concepto Automatico',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos Automaticos';

#
# Structure for the `brand` table : 
#

CREATE TABLE `brand` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Marca Comercial',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Marca Comercial',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Marcas Comerciales';

#
# Structure for the `campaign_dossier` table : 
#

CREATE TABLE `campaign_dossier` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Relacion de Campañas y Expedientes',
  `campaign` int(4) NOT NULL COMMENT 'Identificador de la Campaña',
  `dossier` int(4) NOT NULL COMMENT 'Identificador del Expediente',
  PRIMARY KEY  (`id`),
  KEY `campaign` (`campaign`),
  KEY `dossier` (`dossier`),
  CONSTRAINT `campaign_dossier_fk_1` FOREIGN KEY (`campaign`) REFERENCES `campaign` (`id`),
  CONSTRAINT `campaign_dossier_fk_2` FOREIGN KEY (`dossier`) REFERENCES `dossier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Campañas y Expedientes';

#
# Structure for the `category` table : 
#

CREATE TABLE `category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Categoria',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Categoria',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias';

#
# Structure for the `company` table : 
#

CREATE TABLE `company` (
  `registry` int(4) NOT NULL auto_increment COMMENT 'Registro de la Compañia',
  `active` tinyint(1) default '0' COMMENT 'Indica si la Compañia es activa o inactiva',
  `surcharge` tinyint(1) default '0' COMMENT 'Indica si la Compañia tiene de recargo de equivalencia',
  `calendar` int(4) default NULL COMMENT 'Identificador del Calendario Laboral',
  PRIMARY KEY  (`registry`),
  KEY `calendar` (`calendar`),
  CONSTRAINT `company_ibfk_1` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `fk_comp_rgty` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos Corporativos';

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
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Impuestos';

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
  PRIMARY KEY  (`id`),
  KEY `idx_item_prdt` (`product`),
  CONSTRAINT `item_ibfk_1` FOREIGN KEY (`product`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulos';

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
# Structure for the `course_academicskill` table : 
#

CREATE TABLE `course_academicskill` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `course` int(4) NOT NULL COMMENT 'Curso',
  `academic_skill` int(4) NOT NULL COMMENT 'Academic Skill',
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
# Structure for the `employee` table : 
#

CREATE TABLE `employee` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro del Empleado',
  `social_security_num` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Número de Seguridad Social del Empleado',
  `calendar` int(4) default NULL COMMENT 'Identificador del Calendario Laboral',
  `agreement_time` int(4) default '0' COMMENT 'Horas del Convenio',
  `active` tinyint(1) default NULL COMMENT 'Indica si el Empleado sigue vinculado a la Empresa o no',
  PRIMARY KEY  (`registry`),
  UNIQUE KEY `social_security_num` (`social_security_num`),
  KEY `calendar` (`calendar`),
  CONSTRAINT `employee_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `employee_ibfk_2` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`)
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
  `registry` int(4) NOT NULL auto_increment COMMENT 'Registro del Acreedor',
  `withholding` tinyint(1) default '0' COMMENT 'Indica si el Acreedor aplica retencion de impuestos',
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
  `item` int(4) default NULL COMMENT 'Identificador del Articulo',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de la Cuota',
  `quantity` double(15,3) default '0.000' COMMENT 'Cantidad de la Cuota',
  `price` double(15,3) default '0.000' COMMENT 'Precio de la Cuota',
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
# Structure for the `cv_evaluate_type` table : 
#

CREATE TABLE `cv_evaluate_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Tipo de Evaluacion',
  `name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Nombre del Tipo de Evaluacion',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Evaluaciones del Curriculum Vitae';

#
# Structure for the `cv_evaluate` table : 
#

CREATE TABLE `cv_evaluate` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Evaluacion',
  `type` int(4) default NULL COMMENT 'Tipo de Evaluacion',
  `value` tinyint(2) default NULL COMMENT 'Valor de la Evaluacion',
  `curriculum` int(4) default NULL COMMENT 'Identificador del Curriculum Vitae',
  PRIMARY KEY  (`id`),
  KEY `curriculum` (`curriculum`),
  KEY `type` (`type`),
  CONSTRAINT `cv_evaluate_ibfk_1` FOREIGN KEY (`curriculum`) REFERENCES `curriculum` (`registry`),
  CONSTRAINT `cv_evaluate_ibfk_2` FOREIGN KEY (`type`) REFERENCES `cv_evaluate_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Evaluaciones del Curriculum Vitae';

#
# Structure for the `cv_evaluate_summary` table : 
#

CREATE TABLE `cv_evaluate_summary` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Resumen',
  `strengths` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Fortalezas',
  `weaknesses` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Debilidades',
  `profile` tinyint(2) default NULL COMMENT 'Perfil',
  `comments` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Comentarios',
  `curriculum` int(4) default NULL COMMENT 'Identificador del Curriculum Vitae',
  PRIMARY KEY  (`id`),
  KEY `curriculum` (`curriculum`),
  CONSTRAINT `cv_evaluate_summary_ibfk_1` FOREIGN KEY (`curriculum`) REFERENCES `curriculum` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Resumenes de Evaluaciones del Curriculum Vitae';

#
# Structure for the `cv_knowledge` table : 
#

CREATE TABLE `cv_knowledge` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Conocimiento',
  `name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Nombre o descripcion del Conocimiento',
  `level` tinyint(2) default NULL COMMENT 'Nivel del Conocimiento',
  `experience` tinyint(2) default NULL COMMENT 'Experiencia en el Conocimiento',
  `lastuse` tinyint(2) default NULL COMMENT 'Ultimo uso del Conocimiento',
  `curriculum` int(4) default NULL COMMENT 'Identificador del Curriculum Vitae',
  PRIMARY KEY  (`id`),
  KEY `curriculum` (`curriculum`),
  CONSTRAINT `cv_knowledge_ibfk_1` FOREIGN KEY (`curriculum`) REFERENCES `curriculum` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conocimientos del Curriculum Vitae';

#
# Structure for the `cv_languages` table : 
#

CREATE TABLE `cv_languages` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador único del Idioma',
  `language` tinyint(2) default NULL COMMENT 'Idioma',
  `spoken` tinyint(2) default NULL COMMENT 'Nivel oral del Idioma',
  `wrote` tinyint(2) default NULL COMMENT 'Nivel escrito del Idioma',
  `read_level` tinyint(2) default NULL COMMENT 'Nivel leído del Idioma',
  `curriculum` int(4) default NULL COMMENT 'Identificador del Curriculum Vitae',
  PRIMARY KEY  (`id`),
  KEY `curriculum` (`curriculum`),
  CONSTRAINT `cv_languages_ibfk_1` FOREIGN KEY (`curriculum`) REFERENCES `curriculum` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Idiomas del Curriculum Vitae';

#
# Structure for the `cv_studies` table : 
#

CREATE TABLE `cv_studies` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador único del Estudio',
  `startingdate` date default NULL COMMENT 'Fecha de inicio del Estudio',
  `endingdate` date default NULL COMMENT 'Fecha de finalización del Estudio',
  `degree` tinyint(2) default NULL COMMENT 'Nivel de Estudios',
  `speciality` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Especialidad de Estudios',
  `centre` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Centro de Estudios',
  `curriculum` int(4) NOT NULL COMMENT 'Identificador del Curriculum Vitae',
  PRIMARY KEY  (`id`),
  KEY `curriculum` (`curriculum`),
  CONSTRAINT `cv_studies_ibfk_1` FOREIGN KEY (`curriculum`) REFERENCES `curriculum` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Estudios del Curriculum Vitae';

#
# Structure for the `cv_workexperience` table : 
#

CREATE TABLE `cv_workexperience` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identifador único de la Experiencia Laboral',
  `startingdate` date default NULL COMMENT 'Fecha de inicio de la Experiencia Laboral',
  `endingdate` date default NULL COMMENT 'Fecha de finalización de la Experiencia Laboral',
  `job` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Trabajo desempeñado en la Experiencia Laboral',
  `company` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Compañía donde se desempeñó la Experiencia Laboral',
  `curriculum` int(4) default NULL COMMENT 'Identificador del Curriculum Vitae',
  PRIMARY KEY  (`id`),
  KEY `curriculum` (`curriculum`),
  CONSTRAINT `cv_workexperience_ibfk_1` FOREIGN KEY (`curriculum`) REFERENCES `curriculum` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Experiencia laboral del Curriculum Vitae';

#
# Structure for the `job_type` table : 
#

CREATE TABLE `job_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Tipo de Trabajo',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Trabajo',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Trabajos';

#
# Structure for the `daily_tracking` table : 
#

CREATE TABLE `daily_tracking` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Parte',
  `user` int(4) NOT NULL COMMENT 'Identificador del Usuario que realiza el Parte',
  `tracking_date` date NOT NULL COMMENT 'Fecha del Parte',
  `tracking_duration` double NOT NULL default '0' COMMENT 'Tiempo invertido en el Parte',
  `job_type` int(4) NOT NULL COMMENT 'Tipo de Trabajo realizado en el Parte',
  `customer` int(4) default NULL COMMENT 'Identificador del Cliente asociado al Parte',
  `dossier` int(4) default NULL COMMENT 'Identificador del Expediente asociado al Parte',
  `activity` int(4) default NULL COMMENT 'Identificador de la Actividad asociada al Parte',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios del Parte',
  PRIMARY KEY  (`id`),
  KEY `dossier` (`dossier`),
  KEY `activity` (`activity`),
  KEY `job_type` (`job_type`),
  KEY `customer` (`customer`),
  KEY `user` (`user`),
  CONSTRAINT `daily_tracking_fk_1` FOREIGN KEY (`user`) REFERENCES `user` (`id`),
  CONSTRAINT `daily_tracking_fk_2` FOREIGN KEY (`dossier`) REFERENCES `dossier` (`id`),
  CONSTRAINT `daily_tracking_fk_3` FOREIGN KEY (`activity`) REFERENCES `activity` (`id`),
  CONSTRAINT `daily_tracking_fk_4` FOREIGN KEY (`job_type`) REFERENCES `job_type` (`id`),
  CONSTRAINT `daily_tracking_fk_5` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`)
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
  `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie del Albaran',
  `number` int(4) NOT NULL default '0' COMMENT 'Número del Albaran',
  `customer` int(4) NOT NULL default '0' COMMENT 'Identificador del Cliente',
  `address` int(4) default NULL COMMENT 'Identificador de la Direccion de envio del Albaran',
  `issue_time` datetime default NULL COMMENT 'Fecha de emision del Albaran',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Albaran',
  `status` tinyint(2) default '0' COMMENT 'Estado del Albaran',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `series` (`series`,`number`),
  KEY `idx_dlvy_ctmr` (`customer`),
  KEY `idx_dlvy_radr` (`address`),
  KEY `idx_dlvy_date` (`issue_time`),
  CONSTRAINT `delivery_ibfk_1` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `delivery_ibfk_2` FOREIGN KEY (`address`) REFERENCES `raddress` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Albaranes de Venta';

#
# Structure for the `warehouse` table : 
#

CREATE TABLE `warehouse` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Almacen',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Almacen',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Almacenes';

#
# Structure for the `seller` table : 
#

CREATE TABLE `seller` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro del Agente Comercial',
  `description` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Agente Comercial',
  PRIMARY KEY  (`registry`),
  CONSTRAINT `seller_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Agentes Comerciales';

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
  `pos` int(4) NOT NULL COMMENT 'Identificador del Centro de Venta que realizo el Pedido',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `series` (`series`,`number`),
  KEY `idx_sale_ctmr` (`customer`),
  KEY `idx_sale_date` (`issue_date`),
  KEY `idx_saler_radr` (`shipping_address`),
  KEY `idx_sale_pymt` (`pay_method`),
  KEY `idx_sale_sllr` (`seller`),
  KEY `pos` (`pos`),
  KEY `workplace` (`workplace`),
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
  `line` smallint(2) NOT NULL default '0' COMMENT 'Numero de linea del Detalle dentro del Pedido',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador del Articulo del Detalle de Pedido',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Detalle de Pedido',
  `quantity` double default '0' COMMENT 'Cantidad del Detalle de Pedido',
  `price` double default '0' COMMENT 'Precio del Detalle de Pedido',
  `discount_expr` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descuentos del Detalle de Pedido',
  `taxes` double(15,3) default '0.000' COMMENT 'Tasas del Detalle de Pedido',
  `status` tinyint(2) default '0' COMMENT 'Estado del Detalle de Pedido',
  PRIMARY KEY  (`id`),
  KEY `idx_sldt_item` (`item`),
  KEY `idx_sldt_sales` (`sales`),
  CONSTRAINT `sales_detail_ibfk_1` FOREIGN KEY (`sales`) REFERENCES `sales` (`id`),
  CONSTRAINT `sales_detail_ibfk_2` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Pedido de Venta';

#
# Structure for the `delivery_detail` table : 
#

CREATE TABLE `delivery_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle del Albaran de Venta',
  `line` smallint(2) default '0' COMMENT 'Numero de linea del Detalle dentro del Albaran',
  `delivery` int(4) default NULL COMMENT 'Identificador del Albaran de Venta',
  `item` int(4) default NULL COMMENT 'Identificador del Articulo del Detalle de Albaran',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Detalle de Albaran',
  `warehouse` int(4) default NULL COMMENT 'Identificador del Almacen',
  `quantity` double(15,3) default NULL COMMENT 'Cantidad del Detalle de Albaran',
  `price` double(15,3) default NULL COMMENT 'Precio del Detalle de Albaran',
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
# Structure for the `workactivity` table : 
#

CREATE TABLE `workactivity` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Actividad',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Actividad',
  `workplace` int(4) default NULL COMMENT 'Identificador del Centro de Trabajo',
  `calendar` int(4) default NULL COMMENT 'Identificador del Calendario Laboral',
  `active` tinyint(1) default '1' COMMENT 'Indica si la Actividad esta activa o no',
  PRIMARY KEY  (`id`),
  KEY `workplace` (`workplace`),
  KEY `calendar` (`calendar`),
  CONSTRAINT `workactivity_ibfk_1` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`),
  CONSTRAINT `workactivity_ibfk_2` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades del Centro de Trabajo';

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
# Structure for the `favorite_category` table : 
#

CREATE TABLE `favorite_category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Categoria',
  `user` int(4) NOT NULL COMMENT 'Usuario al que pertenece la Categoria',
  PRIMARY KEY  (`id`),
  KEY `user` (`user`),
  CONSTRAINT `favorite_category_fk` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias de Favoritos de Usuario';

#
# Structure for the `favorite` table : 
#

CREATE TABLE `favorite` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de Favorito',
  `favorite_category` int(4) NOT NULL COMMENT 'Categoria a la que pertenece el Favorito',
  `description` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Favorito',
  `url` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Url del Favorito',
  `user` int(4) NOT NULL COMMENT 'Usuario al que pertenece el Favorito',
  PRIMARY KEY  (`id`),
  KEY `favorite_category` (`favorite_category`),
  KEY `user` (`user`),
  CONSTRAINT `favorite_fk` FOREIGN KEY (`favorite_category`) REFERENCES `favorite_category` (`id`),
  CONSTRAINT `favorite_fk1` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Favoritos de Usuario';

#
# Structure for the `fbatch_detail` table : 
#

CREATE TABLE `fbatch_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle de la Remesa',
  `fbatch` int(4) default NULL COMMENT 'Identificador de la Remesa',
  `finance` int(4) default NULL COMMENT 'Identificador del Vencimiento',
  `amount` double(15,3) default '0.000' COMMENT 'Importe del Detalle de la Remesa',
  `status` tinyint(2) default NULL COMMENT 'Estado del Detalle de la Remesa',
  PRIMARY KEY  (`id`),
  KEY `finance` (`finance`),
  KEY `fbatch` (`fbatch`),
  CONSTRAINT `fbatch_detail_ibfk_1` FOREIGN KEY (`finance`) REFERENCES `finance` (`id`),
  CONSTRAINT `fbatch_detail_ibfk_2` FOREIGN KEY (`fbatch`) REFERENCES `fbatch` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de la Remesa';

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
# Structure for the `iattach` table : 
#

CREATE TABLE `iattach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Archivo Adjunto del Articulo',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador de Articulo',
  `mimeType` tinyint(2) default NULL COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` blob COMMENT 'Archivo Adjunto en binario',
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
  `registry` int(4) NOT NULL auto_increment COMMENT 'Registro del Proveedor',
  `withholding` tinyint(1) default '0' COMMENT 'Indica si el Proveedor aplica retencion de impuestos',
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
# Structure for the `income` table : 
#

CREATE TABLE `income` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Albaran de Compra',
  `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie del Albaran',
  `number` int(4) NOT NULL default '0' COMMENT 'Numero del Albaran',
  `supplier` int(4) NOT NULL default '0' COMMENT 'Identificador del Proveedor',
  `address` int(4) default NULL COMMENT 'Identificador de la Direccion de envio del Albaran',
  `issue_time` date default NULL COMMENT 'Fecha de emision del Albaran',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Albaran',
  `status` tinyint(2) default '0' COMMENT 'Estado del Albaran',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_supplier` (`supplier`,`series`,`number`),
  KEY `supplier` (`supplier`),
  KEY `address` (`address`),
  CONSTRAINT `income_ibfk_1` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`),
  CONSTRAINT `income_ibfk_2` FOREIGN KEY (`address`) REFERENCES `raddress` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Albaranes de Compra';

#
# Structure for the `purchase` table : 
#

CREATE TABLE `purchase` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Pedido de Compra',
  `document_type` tinyint(2) default '1' COMMENT 'Tipo de Pedido',
  `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie del Pedido',
  `number` int(4) NOT NULL default '0' COMMENT 'Numero del Pedido',
  `supplier` int(4) NOT NULL default '0' COMMENT 'Identificador del Proveedor',
  `discount_expression` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descuentos del Pedido',
  `issue_date` date default NULL COMMENT 'Fecha de emision del Pedido',
  `pay_method` int(4) default NULL COMMENT 'Identificador de la Forma de Pago',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Pedido',
  `status` tinyint(2) default '0' COMMENT 'Estado del Pedido',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_supplier` (`supplier`,`series`,`number`),
  KEY `supplier` (`supplier`),
  KEY `pay_method` (`pay_method`),
  KEY `workplace` (`workplace`),
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
  `line` smallint(2) NOT NULL default '0' COMMENT 'Numero de linea del Detalle dentro del Pedido',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador del Articulo del Detalle de Pedido',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Detalle de Pedido',
  `quantity` double(15,3) NOT NULL default '0.000' COMMENT 'Cantidad del Detalle de Pedido',
  `price` double(15,3) default '0.000' COMMENT 'Precio del Detalle de Pedido',
  `discount_expr` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descuentos del Detalle de Pedido',
  `taxes` double(15,3) default '0.000' COMMENT 'Tasas del Detalle de Pedido',
  `status` tinyint(2) default NULL COMMENT 'Estado del Detalle de Pedido',
  PRIMARY KEY  (`id`),
  KEY `purchase` (`purchase`),
  KEY `item` (`item`),
  CONSTRAINT `purchase_detail_ibfk_1` FOREIGN KEY (`purchase`) REFERENCES `purchase` (`id`),
  CONSTRAINT `purchase_detail_ibfk_2` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Pedido de Compra';

#
# Structure for the `income_detail` table : 
#

CREATE TABLE `income_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle del Albaran de Compra',
  `income` int(4) NOT NULL default '0' COMMENT 'Identificador del Albaran de Compra',
  `item` int(4) default NULL COMMENT 'Identificador del Articulo del Detalle de Albaran',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Detalle de Albaran',
  `warehouse` int(4) default NULL COMMENT 'Identificador del Almacen',
  `quantity` double(15,3) NOT NULL default '0.000' COMMENT 'Cantidad del Detalle de Albaran',
  `price` double(15,3) NOT NULL default '0.000' COMMENT 'Precio del Detalle de Albaran',
  `discount_expr` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descuentos del Detalle de Albaran',
  `purchase_detail` int(4) default NULL COMMENT 'Identificador del Detalle del Pedido de Compra asociado',
  PRIMARY KEY  (`id`),
  KEY `item` (`item`),
  KEY `warehouse` (`warehouse`),
  KEY `purchase_detail` (`purchase_detail`),
  KEY `income` (`income`),
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
  `address` varchar(45) collate latin1_spanish_ci default NULL COMMENT 'Primera parte de la Direccion',
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
# Structure for the `invoice_detail` table : 
#

CREATE TABLE `invoice_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle de la Factura',
  `invoice` int(4) NOT NULL default '0' COMMENT 'Identificador de la Factura',
  `line` smallint(2) default '1' COMMENT 'Numero de línea del Detalle dentro de la Factura',
  `item` int(4) default NULL COMMENT 'Identificador del Articulo del Detalle de Factura',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Detalle de Factura',
  `quantity` double default '0' COMMENT 'Cantidad del Detalle de Factura',
  `price` double default '0' COMMENT 'Precio del Detalle de Factura',
  `discount_expr` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descuentos del Detalle de Factura',
  `source` tinyint(2) default '0' COMMENT 'Origen del Detalle de la Factura',
  `delivery_detail` int(4) default NULL COMMENT 'Identificador del Detalle del Albaran de Venta asociado',
  `taxable_base` double(15,3) default '0.000' COMMENT 'Base Imponible del Detalle de Factura',
  `taxes` double(15,3) default '0.000' COMMENT 'Tasas del Detalle de Factura',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  PRIMARY KEY  (`id`),
  KEY `idx_invd_item` (`item`),
  KEY `idx_invd_invc` (`invoice`),
  KEY `idx_invd_dlvd` (`delivery_detail`),
  KEY `workplace` (`workplace`),
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
  `percentage` double(15,3) default '0.000' COMMENT 'Porcentaje de recargo del Detalle de la Factura',
  `surcharge` double(15,3) default '0.000' COMMENT 'Porcentaje del recargo de equivalencia del Detalle de la Factura',
  PRIMARY KEY  (`id`),
  KEY `invoice_detail` (`invoice_detail`),
  CONSTRAINT `invoice_tax_ibfk_1` FOREIGN KEY (`invoice_detail`) REFERENCES `invoice_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Impuestos del Detalle de la Factura';

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
# Structure for the `item_tariff` table : 
#

CREATE TABLE `item_tariff` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Tarifa del Articulo',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador de Articulo',
  `tariff` int(4) NOT NULL default '0' COMMENT 'Identificador de Tarifa',
  `percentage` double(6,2) default '0.00' COMMENT 'Porcentaje de descuento sobre el precio del Articulo',
  PRIMARY KEY  (`id`),
  KEY `tariff` (`tariff`),
  KEY `item` (`item`),
  CONSTRAINT `item_tariff_ibfk_1` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`),
  CONSTRAINT `item_tariff_ibfk_2` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas de Articulos';

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
# Structure for the `lh_contract` table : 
#

CREATE TABLE `lh_contract` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Contrato',
  `employee` int(4) NOT NULL COMMENT 'Identificador del Empleado',
  `startingdate` date NOT NULL COMMENT 'Fecha de inicio del Contrato',
  `endingdate` date default NULL COMMENT 'Fecha de finalizacion del Contrato',
  `contract_type` tinyint(4) NOT NULL COMMENT 'Tipo de Contrato',
  `gross_salary` double(15,2) default '0.00' COMMENT 'Salario bruto del Contrato',
  PRIMARY KEY  (`id`),
  KEY `employee` (`employee`),
  CONSTRAINT `lh_contract_ibfk_1` FOREIGN KEY (`employee`) REFERENCES `employee` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Historial Laboral (Contratos)';

#
# Structure for the `lh_course` table : 
#

CREATE TABLE `lh_course` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Curso',
  `employee` int(4) NOT NULL COMMENT 'Identificador del Empleado',
  `startingdate` date default NULL COMMENT 'Fecha de inicio del Curso',
  `endingdate` date default NULL COMMENT 'Fecha de finalizacion del Curso',
  `description` varchar(64) default NULL COMMENT 'Descripcion del Curso',
  PRIMARY KEY  (`id`),
  KEY `employee` (`employee`),
  CONSTRAINT `lh_course_ibfk_1` FOREIGN KEY (`employee`) REFERENCES `employee` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Historia Laboral (Cursos)';

#
# Structure for the `lh_position` table : 
#

CREATE TABLE `lh_position` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Cargo',
  `employee` int(4) NOT NULL COMMENT 'Identificador del Empleado',
  `startingdate` date default NULL COMMENT 'Fecha de inicio del Cargo',
  `endingdate` date default NULL COMMENT 'Fecha de finalizacion del Cargo',
  `description` varchar(64) default NULL COMMENT 'Descripcion del Cargo',
  `workplace` int(4) default NULL COMMENT 'Identificador del Centro de Trabajo',
  `workactivity` int(4) default NULL COMMENT 'Identificador de la Actividad',
  `calendar` int(4) default NULL COMMENT 'Identificador del Calendario Laboral',
  PRIMARY KEY  (`id`),
  KEY `employee` (`employee`),
  KEY `workplace` (`workplace`),
  KEY `workactivity` (`workactivity`),
  KEY `calendar` (`calendar`),
  CONSTRAINT `lh_position_ibfk_1` FOREIGN KEY (`employee`) REFERENCES `employee` (`registry`),
  CONSTRAINT `lh_position_ibfk_3` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`),
  CONSTRAINT `lh_position_ibfk_4` FOREIGN KEY (`workactivity`) REFERENCES `workactivity` (`id`),
  CONSTRAINT `lh_position_ibfk_5` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Historia Laboral (Cargos)';

#
# Structure for the `lh_work` table : 
#

CREATE TABLE `lh_work` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Trabajo',
  `employee` int(4) NOT NULL COMMENT 'Identificador del Empleado',
  `startingdate` date default NULL COMMENT 'Fecha de inicio del Trabajo',
  `endingdate` date default NULL COMMENT 'Fecha de finalizacion del Trabajo',
  `description` varchar(64) default NULL COMMENT 'Descripcion del Trabajo',
  PRIMARY KEY  (`id`),
  KEY `employee` (`employee`),
  CONSTRAINT `lh_work_ibfk_1` FOREIGN KEY (`employee`) REFERENCES `employee` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Historia Laboral (Trabajos desarrollados)';

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
  `id` int(4) NOT NULL COMMENT 'Identificador unico',
  `content` text collate latin1_spanish_ci NOT NULL COMMENT 'Contenido del Mensaje',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contenido de Mensajes';

#
# Structure for the `message_log` table : 
#

CREATE TABLE `message_log` (
  `id` int(4) NOT NULL COMMENT 'Identificador unico',
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
  `date` datetime default NULL COMMENT 'Fecha y hora en la que se produjo el Aviso',
  `sender` int(4) NOT NULL COMMENT 'Remitente del Aviso',
  `work_group` int(4) NOT NULL COMMENT 'Grupo de Trabajo al que va dirigida el Aviso',
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
  `description` varchar(80) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Observacion',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Observaciones';

#
# Structure for the `target` table : 
#

CREATE TABLE `target` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro del Cliente Potencial',
  PRIMARY KEY  (`registry`),
  CONSTRAINT `target_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Clientes Potenciales';

#
# Structure for the `offer` table : 
#

CREATE TABLE `offer` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Presupuesto',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie del Presupuesto',
  `number` int(4) NOT NULL COMMENT 'Numero del Presupuesto',
  `seller` int(4) default NULL COMMENT 'Agente Comercial del Presupuesto',
  `discount_expr` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descuentos del Presupuesto',
  `issue_date` date default NULL COMMENT 'Fecha de emision del Presupuesto',
  `pay_method` int(4) default NULL COMMENT 'Forma de Pago del Presupuesto',
  `status` tinyint(2) default '0' COMMENT 'Estado del Presupuesto',
  `type` tinyint(2) default '0' COMMENT 'Tipo de Presupuesto',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `series` (`series`,`number`),
  KEY `target` (`target`),
  KEY `seller` (`seller`),
  KEY `pay_method` (`pay_method`),
  KEY `workplace` (`workplace`),
  CONSTRAINT `offer_ibfk_1` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `offer_ibfk_2` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`),
  CONSTRAINT `offer_ibfk_3` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `offer_ibfk_4` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Presupuestos';

#
# Structure for the `offer_detail` table : 
#

CREATE TABLE `offer_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle de Presupuesto',
  `offer` int(4) NOT NULL COMMENT 'Identificador del Presupuesto',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripción del Articulo',
  `quantity` double(15,3) NOT NULL COMMENT 'Cantidad del Articulo',
  `price` double(15,3) NOT NULL COMMENT 'Precio del Articulo',
  `discount_expr` varchar(32) collate latin1_spanish_ci default '0' COMMENT 'Descuentos del Articulo',
  `status` tinyint(2) default '0' COMMENT 'Estado del Detalle del Presupuesto',
  PRIMARY KEY  (`id`),
  KEY `offer` (`offer`),
  KEY `item` (`item`),
  CONSTRAINT `offer_detail_ibfk_1` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`),
  CONSTRAINT `offer_detail_ibfk_2` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Presupuesto';

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
# Structure for the `periodical_task` table : 
#

CREATE TABLE `periodical_task` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `next_date` date default NULL COMMENT 'Fecha en la que se generara la siguiente Tarea',
  `period` tinyint(2) NOT NULL COMMENT 'Periodicidad de la Tarea',
  `quantity` int(4) default NULL COMMENT 'Numero de periodos para obtener la fecha de la siguiente Tarea',
  `task` int(4) NOT NULL COMMENT 'Tarea que se repite',
  `owner` int(4) NOT NULL COMMENT 'Propietario de la Tarea',
  PRIMARY KEY  (`id`),
  KEY `task` (`task`),
  KEY `owner` (`owner`),
  CONSTRAINT `periodical_task_fk` FOREIGN KEY (`task`) REFERENCES `task` (`id`),
  CONSTRAINT `periodical_task_fk1` FOREIGN KEY (`owner`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tareas Periodicas';

#
# Structure for the `person` table : 
#

CREATE TABLE `person` (
  `registry` int(4) NOT NULL auto_increment COMMENT 'Registro de la Persona',
  `birth_date` date default NULL COMMENT 'Fecha de nacimiento de la Persona',
  `gender` tinyint(2) NOT NULL default '0' COMMENT 'Sexo de la Persona',
  `marital_status` tinyint(2) NOT NULL default '0' COMMENT 'Estado civil de la Persona',
  PRIMARY KEY  (`registry`),
  CONSTRAINT `person_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Personas';

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
  PRIMARY KEY  (`id`),
  KEY `registry` (`registry`),
  KEY `category` (`category`),
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
  `percent_share` double default NULL COMMENT 'Porcentaje de acciones (solo para socios)',
  `share_number` int(4) default '0' COMMENT 'Numero de Acciones',
  `nominal_value` double(15,3) default '0.000' COMMENT 'Valor Nominal',
  `due_date` date default NULL COMMENT 'Fecha de vencimiento del cargo',
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
  `raddress` int(4) default NULL COMMENT 'Identificador de la Direccion de la Persona o Empresa',
  `media` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Medio de Contacto de la Persona o Empresa',
  `value` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Valor del Medio de Contacto de la Persona o Empresa',
  `comment` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Comentarios acerca del Medio de Contacto de la Persona o Empresa',
  PRIMARY KEY  (`id`),
  KEY `idx_rmed_rgty` (`registry`),
  KEY `raddress` (`raddress`),
  CONSTRAINT `rmedia_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `rmedia_ibfk_2` FOREIGN KEY (`raddress`) REFERENCES `raddress` (`id`)
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
  PRIMARY KEY  (`id`),
  KEY `registry` (`registry`),
  CONSTRAINT `rnote_fk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Notas de Personas o Empresas';

#
# Structure for the `rpaymethod` table : 
#

CREATE TABLE `rpaymethod` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Forma de Pago de la Persona o Empresa',
  `registry` int(4) default NULL COMMENT 'Identificador del Registro de la Persona o Empresa',
  `pay_method` int(4) default NULL COMMENT 'Identificador de la Forma de Pago',
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
  `user` int(4) NOT NULL COMMENT 'Identificador del Usuario',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  PRIMARY KEY  (`id`),
  KEY `user` (`user`),
  KEY `scope` (`scope`),
  CONSTRAINT `user_scope_fk` FOREIGN KEY (`user`) REFERENCES `user` (`id`),
  CONSTRAINT `user_scope_fk1` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ambitos de Usuario';

#
# Structure for the `user_workgroup` table : 
#

CREATE TABLE `user_workgroup` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `user` int(4) NOT NULL COMMENT 'Identificado del Usuario',
  `workgroup` int(4) NOT NULL COMMENT 'Identificador del Grupo de Trabajo',
  PRIMARY KEY  (`id`),
  KEY `user` (`user`),
  KEY `workgroup` (`workgroup`),
  CONSTRAINT `user_workgroup_fk_1` FOREIGN KEY (`user`) REFERENCES `user` (`id`),
  CONSTRAINT `user_workgroup_fk_2` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Usuarios y Grupos de Trabajo';

#
# Structure for the `web_info` table : 
#

CREATE TABLE `web_info` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `company` int(4) NOT NULL COMMENT 'Empresa',
  `commercial_description` text collate latin1_spanish_ci COMMENT 'Descripcion comercial',
  `schedule` text collate latin1_spanish_ci COMMENT 'Horario',
  `slogan` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Slogan',
  `title` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Titulo de la ultima pestaña',
  `content` text collate latin1_spanish_ci COMMENT 'Contenido de la ultima pestaña',
  PRIMARY KEY  (`id`),
  KEY `company` (`company`),
  CONSTRAINT `web_info_fk` FOREIGN KEY (`company`) REFERENCES `company` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Informacion de la empresa que se mostrara en la ficha web';

#
# Definition for the `facturasCategoriaFecha` function : 
#

CREATE FUNCTION `facturasCategoriaFecha`(i DATE, f DATE, c INTEGER, t INTEGER)
    RETURNS double
    NOT DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
RETURN (SELECT IF (SUM(invoice_detail.taxable_base) IS NULL, 0,
               SUM(TRUNCATE(invoice_detail.taxable_base *
               (IF (invoice.surcharge = 0,1,(invoice_tax.percentage + invoice_tax.surcharge + 100) / 100)), 2)))
          FROM invoice_detail, invoice, item, product, pcategory, invoice_tax
         WHERE item.product = product.id
           AND product.category = pcategory.id
           AND invoice_detail.item = item.id
           AND invoice_detail.invoice = invoice.id
	       AND invoice_tax.invoice_detail = invoice_detail.id
       	   AND invoice.type = t
           AND pcategory.id = c
           AND invoice.issue_date BETWEEN i AND f);

#
# Definition for the `facturasGrupoCategoriaFecha` function : 
#

CREATE FUNCTION `facturasGrupoCategoriaFecha`(i DATE, f DATE, c INT, t INT)
    RETURNS double
    NOT DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
RETURN (SELECT IF (SUM(invoice_detail.taxable_base) IS NULL, 0,
       SUM(TRUNCATE(invoice_detail.taxable_base *
       (IF (invoice.surcharge = 0,1,(invoice_tax.percentage + invoice_tax.surcharge + 100) / 100)), 2)))
FROM invoice_detail, invoice, item, product, pcategory, pcategory_group, invoice_tax
WHERE item.product = product.id  AND product.category = pcategory.id
AND pcategory_group.id = pcategory.pcategory_group
AND invoice_detail.item = item.id  AND invoice_detail.invoice = invoice.id
AND invoice_tax.invoice_detail = invoice_detail.id  AND invoice.type = t
AND pcategory_group.id = c  AND invoice.issue_date BETWEEN i AND f);

#
# Definition for the `inventarioCategoriaFecha` function : 
#

CREATE FUNCTION `inventarioCategoriaFecha`(d DATE, c INT)
    RETURNS double
    NOT DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
RETURN (SELECT IF (SUM(inventory_detail.cost) IS NULL, 0, SUM(inventory_detail.cost * inventory_detail.real_quantity))
          FROM inventory, inventory_detail, item, product, pcategory
         WHERE item.product = product.id
           AND product.category = pcategory.id
           AND inventory_detail.item = item.id
           AND inventory.id = inventory_detail.inventory
           AND pcategory.id = c
           AND inventory.inventory_date = d);

#
# Definition for the `inventarioGrupoCategoriaFecha` function : 
#

CREATE FUNCTION `inventarioGrupoCategoriaFecha`(d DATE, c INT)
    RETURNS double
    NOT DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
RETURN (SELECT IF (SUM(inventory_detail.cost) IS NULL, 0, SUM(inventory_detail.cost * inventory_detail.real_quantity))
       FROM inventory, inventory_detail, item, product, pcategory, pcategory_group
       WHERE item.product = product.id  AND product.category = pcategory.id
       AND pcategory_group.id = pcategory.pcategory_group  AND inventory_detail.item = item.id
       AND inventory.id = inventory_detail.inventory  AND pcategory_group.id = c
       AND inventory.inventory_date = d);


INSERT INTO `db_version` (`version_number`) VALUES ('1.7.0');

COMMIT;


SET FOREIGN_KEY_CHECKS=1;
