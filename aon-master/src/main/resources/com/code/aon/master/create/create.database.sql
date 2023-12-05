# Database : aon_master
# Version: 9.23.0
# Created by: rtrepiana
# Creation Date: 03/09/2022


SET FOREIGN_KEY_CHECKS=0;

CREATE DATABASE `aon_master`
    CHARACTER SET 'latin1'
    COLLATE 'latin1_spanish_ci';

USE `aon_master`;

#
# Table structure for table `absence`
#

CREATE TABLE `absence` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `course_alumn` int NOT NULL COMMENT 'Identificador del CursoAlumno',
  `absence_date` date DEFAULT NULL COMMENT 'Fecha de la Ausencia',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Ausencia',
  `evaluation` tinyint DEFAULT NULL COMMENT 'Numero de Evaluacion en que se produjo la Ausencia',
  PRIMARY KEY (`id`),
  KEY `IDX_ABSENCE_COURSE_ALUMN` (`course_alumn`),
  KEY `IDX_ABSENCE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ABSENCE_COURSE_ALUMN` FOREIGN KEY (`course_alumn`) REFERENCES `course_alumn` (`id`),
  CONSTRAINT `FK_ABSENCE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ausencias';

#
# Table structure for table `academic_skill`
#

CREATE TABLE `academic_skill` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Aptitud Academica',
  `domain` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `code` char(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo de la Aptitud Academica',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de la Aptitud Academica',
  PRIMARY KEY (`id`),
  KEY `IDX_ACADEMIC_SKILL_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACADEMIC_SKILL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Aptitudes Academicas';

#
# Table structure for table `academic_year`
#

CREATE TABLE `academic_year` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Año Academico',
  `domain` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `description` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Año Academico',
  PRIMARY KEY (`id`),
  KEY `IDX_ACADEMIC_YEAR_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACADEMIC_YEAR_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Año Academico';

#
# Table structure for table `account`
#

CREATE TABLE `account` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `code` char(12) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo Cuenta Contable',
  `description` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Cuenta',
  `alias` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias de la Cuenta',
  `entryEnabled` tinyint DEFAULT '0' COMMENT 'Indica si la Cuenta permite o no Apuntes',
  `level` tinyint NOT NULL DEFAULT '0' COMMENT 'Nivel de la Cuenta',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si la Cuenta esta activo o no',
  `cost_center` char(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Centro de Costo',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_ACCOUNT_DOMAIN_CODE` (`domain`,`code`),
  KEY `IDX_ACCOUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables';

#
# Table structure for table `account_entry`
#

CREATE TABLE `account_entry` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Asiento',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `account_period` int NOT NULL COMMENT 'Ejercicio Contable del Asiento',
  `activity` int DEFAULT NULL COMMENT 'Identificador de la Actividad',
  `entry_date` date DEFAULT NULL COMMENT 'Fecha del Asiento',
  `entry_type` tinyint DEFAULT NULL COMMENT 'Tipo de Asiento',
  `journal` int DEFAULT NULL COMMENT 'Numero de diario del Asiento',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad del Asiento',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios del Asiento',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_ACCOUNT_ENTRY_ACCOUNT_PERIOD` (`account_period`),
  KEY `IDX_ACCOUNT_ENTRY_DOMAIN` (`domain`),
  KEY `IDX_ACCOUNT_ENTRY_ACTIVITY` (`activity`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_ACCOUNT_PERIOD` FOREIGN KEY (`account_period`) REFERENCES `account_period` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_ACTIVITY` FOREIGN KEY (`activity`) REFERENCES `enterprise_activity` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Asientos Contables';

#
# Table structure for table `account_entry_bank_statement`
#

CREATE TABLE `account_entry_bank_statement` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `account_entry` int NOT NULL COMMENT 'Identificador de Asiento',
  `bank_statement` int NOT NULL COMMENT 'Identificador de Extracto bancario',
  PRIMARY KEY (`id`),
  KEY `IDX_ACC_ENTRY_BANK_STATEMENT_ACC_ENTRY` (`account_entry`),
  KEY `IDX_ACC_ENTRY_BANK_STATEMENT_BANK_STATEMENT` (`bank_statement`),
  KEY `IDX_ACCOUNT_ENTRY_BANK_STATEMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACC_ENTRY_BANK_STATEMENT_ACC_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_ACC_ENTRY_BANK_STATEMENT_BANK_STATEMENT` FOREIGN KEY (`bank_statement`) REFERENCES `bank_statement` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_BANK_STATEMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Asientos Contables y Extractos bancarios';

#
# Table structure for table `account_entry_detail`
#

CREATE TABLE `account_entry_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Apunte',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `account_entry` int NOT NULL COMMENT 'Identificador del Asiento',
  `line` int unsigned NOT NULL COMMENT 'Numero de linea del Apunte dentro del Asiento',
  `account` int NOT NULL COMMENT 'Cuenta Contable del Apunte',
  `concept` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `balancing_account` int DEFAULT NULL COMMENT 'Contrapartida del Apunte',
  `debit` decimal(15,4) DEFAULT '0' COMMENT 'Debe del Apunte',
  `credit` decimal(15,4) DEFAULT '0' COMMENT 'Haber del Apunte',
  `document_number` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de documento asociado',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_ACCOUNT_ENTRY_DETAIL_ACCOUNT` (`account`),
  KEY `IDX_ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY` (`account_entry`),
  KEY `IDX_ACCOUNT_ENTRY_DETAIL_BALANCING_ACCOUNT` (`balancing_account`),
  KEY `IDX_ACCOUNT_ENTRY_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_DETAIL_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_DETAIL_BALANCING_ACCOUNT` FOREIGN KEY (`balancing_account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Apuntes Contables';

#
# Table structure for table `account_entry_fbatch`
#

CREATE TABLE `account_entry_fbatch` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `account_entry` int NOT NULL COMMENT 'Identificador de Asiento Contable',
  `fbatch` int NOT NULL COMMENT 'Identificador de Remesa',
  PRIMARY KEY (`id`),
  KEY `IDX_ACCOUNT_ENTRY_FBATCH_ACCOUNT_ENTRY` (`account_entry`),
  KEY `IDX_ACCOUNT_ENTRY_FBATCH_FBATCH` (`fbatch`),
  KEY `IDX_ACCOUNT_ENTRY_FBATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_FBATCH_ACCOUNT_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_FBATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_FBATCH_FBATCH` FOREIGN KEY (`fbatch`) REFERENCES `fbatch` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Asientos Contables y Remesas';

#
# Table structure for table `account_entry_finance_tracking`
#

CREATE TABLE `account_entry_finance_tracking` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `account_entry` int NOT NULL COMMENT 'Identificador de Asiento Contable',
  `finance_tracking` int NOT NULL COMMENT 'Identificador de Seguimiento de Vencimientos',
  PRIMARY KEY (`id`),
  KEY `IDX_ACCOUNT_ENTRY_FINANCE_TRACKING_ACCOUNT_ENTRY` (`account_entry`),
  KEY `IDX_ACCOUNT_ENTRY_FINANCE_TRACKING_FINANCE_TRACKING` (`finance_tracking`),
  KEY `IDX_ACCOUNT_ENTRY_FINANCE_TRACKING_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_FINANCE_TRACKING_ACCOUNT_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_FINANCE_TRACKING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_FINANCE_TRACKING_FINANCE_TRACKING` FOREIGN KEY (`finance_tracking`) REFERENCES `finance_tracking` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Seguimiento de Vencimientos y Asientos Contab';

#
# Table structure for table `account_entry_invoice`
#

CREATE TABLE `account_entry_invoice` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de Relacion',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `account_entry` int NOT NULL COMMENT 'Identificador de Asiento',
  `invoice` int NOT NULL COMMENT 'Identificador de Factura',
  PRIMARY KEY (`id`),
  KEY `IDX_ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY` (`account_entry`),
  KEY `IDX_ACCOUNT_ENTRY_INVOICE_INVOICE` (`invoice`),
  KEY `IDX_ACCOUNT_ENTRY_INVOICE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_INVOICE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_INVOICE_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Asientos Contables y Facturas';

#
# Table structure for table `account_period`
#

CREATE TABLE `account_period` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` char(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del periodo',
  `initiation_date` date NOT NULL COMMENT 'Fecha de inicio del Ejercicio',
  `deadline` date NOT NULL COMMENT 'Fecha final del Ejercicio',
  `status` tinyint DEFAULT '0' COMMENT 'Estado del Ejercicio',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_ACCOUNT_PERIOD_DOMAIN_NAME` (`domain`,`name`),
  KEY `IDX_ACCOUNT_PERIOD_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_PERIOD_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ejercicios Contables';

#
# Table structure for table `action`
#

CREATE TABLE `action` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `menu` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si la Accion esta o no dentro del menu',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Nombre de la Accion',
  `application` int NOT NULL COMMENT 'Aplicacion a la que pertenece la Accion',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_ACTION_NAME_APPLICATION` (`name`,`application`),
  KEY `IDX_ACTION_NAME` (`name`),
  KEY `IDX_ACTION_APPLICATION` (`application`),
  CONSTRAINT `FK_ACTION_APPLICATION` FOREIGN KEY (`application`) REFERENCES `application` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Acciones de una Applicacion';

#
# Table structure for table `action_denied`
#

CREATE TABLE `action_denied` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `action_id` int NOT NULL DEFAULT '0' COMMENT 'Identificador de la Accion',
  `user_id` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Usuario',
  PRIMARY KEY (`id`),
  KEY `IDX_ACTION_DENIED_ACTION` (`action_id`),
  KEY `IDX_ACTION_DENIED_USER` (`user_id`),
  KEY `IDX_ACTION_DENIED_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACTION_DENIED_ACTION` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `FK_ACTION_DENIED_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACTION_DENIED_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Accion no permitida para el Usuario';

#
# Table structure for table `action_entry`
#

CREATE TABLE `action_entry` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `executionDate` datetime NOT NULL COMMENT 'Fecha de ejecucion',
  `action_id` int NOT NULL DEFAULT '0' COMMENT 'Identificador de la Accion',
  `session_id` int NOT NULL DEFAULT '0' COMMENT 'Identificador de la Sesion',
  PRIMARY KEY (`id`),
  KEY `IDX_ACTION_ENTRY_ACTION` (`action_id`),
  KEY `IDX_ACTION_ENTRY_SESSION` (`session_id`),
  KEY `IDX_ACTION_ENTRY_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACTION_ENTRY_ACTION` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `FK_ACTION_ENTRY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACTION_ENTRY_SESSION` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Entrada de la ejecucion de una Accion';

#
# Table structure for table `action_favorite`
#

CREATE TABLE `action_favorite` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `position` int NOT NULL COMMENT 'Posicion dentro de las Acciones Favoritas',
  `action_id` int NOT NULL DEFAULT '0' COMMENT 'Identificador de la Accion',
  `user_id` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Usuario',
  PRIMARY KEY (`id`),
  KEY `IDX_ACTION_FAVORITE_ACTION` (`action_id`),
  KEY `IDX_ACTION_FAVORITE_USER` (`user_id`),
  KEY `IDX_ACTION_FAVORITE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACTION_FAVORITE_ACTION` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `FK_ACTION_FAVORITE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACTION_FAVORITE_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Accion Favorita del Usuario';

#
# Table structure for table `activity_type`
#

CREATE TABLE `activity_type` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Tipo de Actividad',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Actividad',
  `project_type` int DEFAULT NULL COMMENT 'Tipo de Proyecto',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Activo si o no',
  PRIMARY KEY (`id`),
  KEY `IDX_ACTIVITY_TYPE_PROJECT_TYPE` (`project_type`),
  KEY `IDX_ACTIVITY_TYPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACTIVITY_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACTIVITY_TYPE_PROJECT_TYPE` FOREIGN KEY (`project_type`) REFERENCES `project_type` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Actividades';

#
# Table structure for table `agreement`
#

CREATE TABLE `agreement` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `calendar` int DEFAULT NULL COMMENT 'Calendario',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `ss_number` varchar(20) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `owner` tinyint DEFAULT '0',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `creation_date` date DEFAULT NULL,
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `modification_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_AGREEMENT_CALENDAR` (`calendar`),
  KEY `IDX_AGREEMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_AGREEMENT_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_AGREEMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Convenios';

#
# Table structure for table `agreement_data`
#

CREATE TABLE `agreement_data` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `agreement` int NOT NULL COMMENT 'Convenio',
  `expression` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  PRIMARY KEY (`id`),
  KEY `IDX_AGREEMENT_DATA_AGREEMENT` (`agreement`),
  KEY `IDX_AGREEMENT_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_AGREEMENT_DATA_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`),
  CONSTRAINT `FK_AGREEMENT_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto del convenio';

#
# Table structure for table `agreement_extra`
#

CREATE TABLE `agreement_extra` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `agreement` int NOT NULL COMMENT 'Convenio',
  `agreement_payment` int DEFAULT NULL COMMENT 'Concepto',
  `start_date` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Fecha de inicio dd mm [year offset]',
  `end_date` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Fecha de finalizacion dd mm [year offset]',
  `issue_date` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Fecha de emision dd mm',
  PRIMARY KEY (`id`),
  KEY `IDX_AGREEMENT_EXTRA_AGREEMENT` (`agreement`),
  KEY `IDX_AGREEMENT_EXTRA_AGREEMENT_PAYMENT` (`agreement_payment`),
  KEY `IDX_AGREEMENT_EXTRA_DOMAIN` (`domain`),
  CONSTRAINT `FK_AGREEMENT_EXTRA_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`),
  CONSTRAINT `FK_AGREEMENT_EXTRA_AGREEMENT_PAYMENT` FOREIGN KEY (`agreement_payment`) REFERENCES `agreement_payment` (`id`),
  CONSTRAINT `FK_AGREEMENT_EXTRA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Pagas extras';

#
# Table structure for table `agreement_level`
#

CREATE TABLE `agreement_level` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `agreement` int NOT NULL COMMENT 'Convenio',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  PRIMARY KEY (`id`),
  KEY `IDX_AGREEMENT_LEVEL_AGREEMENT` (`agreement`),
  KEY `IDX_AGREEMENT_LEVEL_DOMAIN` (`domain`),
  CONSTRAINT `FK_AGREEMENT_LEVEL_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`),
  CONSTRAINT `FK_AGREEMENT_LEVEL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Niveles retributivos';

#
# Table structure for table `agreement_level_category`
#

CREATE TABLE `agreement_level_category` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `agreement_level` int NOT NULL COMMENT 'Nivel retributivo',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  PRIMARY KEY (`id`),
  KEY `IDX_AGREEMENT_LEVEL_CATEGORY_AGREEMENT_LEVEL` (`agreement_level`),
  KEY `IDX_AGREEMENT_LEVEL_CATEGORY_DOMAIN` (`domain`),
  CONSTRAINT `FK_AGREEMENT_LEVEL_CATEGORY_AGREEMENT_LEVEL` FOREIGN KEY (`agreement_level`) REFERENCES `agreement_level` (`id`),
  CONSTRAINT `FK_AGREEMENT_LEVEL_CATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias profesionales';

#
# Table structure for table `agreement_level_data`
#

CREATE TABLE `agreement_level_data` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `agreement_level` int NOT NULL COMMENT 'Nivel retributivo',
  `expression` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `creation_date` date DEFAULT NULL,
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `modification_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_AGREEMENT_LEVEL_DATA_AGREEMENT_LEVEL` (`agreement_level`),
  KEY `IDX_AGREEMENT_LEVEL_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_AGREEMENT_LEVEL_DATA_AGREEMENT_LEVEL` FOREIGN KEY (`agreement_level`) REFERENCES `agreement_level` (`id`),
  CONSTRAINT `FK_AGREEMENT_LEVEL_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto del convenio';

#
# Table structure for table `agreement_payment`
#

CREATE TABLE `agreement_payment` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `agreement` int NOT NULL COMMENT 'Convenio',
  `payment_concept` int DEFAULT NULL COMMENT 'Identificador unico del concepto',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de complemento Salarial',
  `expression` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `month` tinyint DEFAULT NULL COMMENT 'Mes de la percepcion',
  `salary_type` tinyint DEFAULT NULL COMMENT 'Tipo de Nomina/Recibo',
  `description_decorable` tinyint NOT NULL DEFAULT '0',
  `irpf_expression` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe tributable',
  `quote_expression` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe cotizable',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `creation_date` date DEFAULT NULL,
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `modification_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_AGREEMENT_PAYMENT_PAYMENT_CONCEPT` (`payment_concept`),
  KEY `IDX_AGREEMENT_PAYMENT_AGREEMENT` (`agreement`),
  KEY `IDX_AGREEMENT_PAYMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_AGREEMENT_PAYMENT_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`),
  CONSTRAINT `FK_AGREEMENT_PAYMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_AGREEMENT_PAYMENT_PAYMENT_CONCEPT` FOREIGN KEY (`payment_concept`) REFERENCES `payment_concept` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Percepciones';

#
# Table structure for table `alarm`
#

CREATE TABLE `alarm` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Alarma',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `description` text CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Alarma',
  `alarm_date` datetime NOT NULL COMMENT 'Fecha y hora de ejecucion de la Alarma',
  `status` tinyint DEFAULT NULL COMMENT 'Estado de la Alarma',
  `source` tinyint NOT NULL COMMENT 'Origen de la Alarma',
  `source_id` int DEFAULT NULL COMMENT 'Identificador del origen de la Alarma',
  `user_id` int DEFAULT NULL COMMENT 'Identificador del Usuario asociado a la Alarma',
  `priority` tinyint NOT NULL COMMENT 'Prioridad de la Alarma',
  PRIMARY KEY (`id`),
  KEY `IDX_ALARM_USER` (`user_id`),
  KEY `IDX_ALARM_DOMAIN` (`domain`),
  CONSTRAINT `FK_ALARM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ALARM_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Alarmas';

#
# Table structure for table `alcatraz`
#

CREATE TABLE `alcatraz` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Id Dominio',
  `fs_model` int DEFAULT NULL COMMENT 'Id Modelo fiscal',
  `invoice` int DEFAULT NULL COMMENT 'Id Factura',
  `salary` int DEFAULT NULL COMMENT 'Id Nomina',
  `finance` int DEFAULT NULL COMMENT 'Id Vto',
  `finance_tracking` int DEFAULT NULL COMMENT 'Id seguimiento Vto',
  PRIMARY KEY (`id`),
  KEY `IDX_ALCATRAZ_DOMAIN` (`domain`),
  KEY `IDX_ALCATRAZ_FS_MODEL` (`fs_model`),
  KEY `IDX_ALCATRAZ_INVOICE` (`invoice`),
  KEY `IDX_ALCATRAZ_SALARY` (`salary`),
  KEY `IDX_ALCATRAZ_FINANCE` (`finance`),
  KEY `IDX_ALCATRAZ_FINANCE_TRACKING` (`finance_tracking`),
  CONSTRAINT `FK_ALCATRAZ_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ALCATRAZ_FS_MODEL` FOREIGN KEY (`fs_model`) REFERENCES `fs_model` (`id`),
  CONSTRAINT `FK_ALCATRAZ_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`),
  CONSTRAINT `FK_ALCATRAZ_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`),
  CONSTRAINT `FK_ALCATRAZ_FINANCE` FOREIGN KEY (`finance`) REFERENCES `finance` (`id`),
  CONSTRAINT `FK_ALCATRAZ_FINANCE_TRACKING` FOREIGN KEY (`finance_tracking`) REFERENCES `finance_tracking` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bloqueo de entidades';

#
# Table structure for table `allotment`
#

CREATE TABLE `allotment` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `hotel` int NOT NULL COMMENT 'Identificador del Hotel',
  `rate_code` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de Cupo',
  `agency` int DEFAULT NULL COMMENT 'Identificador de la agencia de viajes',
  `agency_group` int DEFAULT NULL COMMENT 'Identificador del Grupo de agencias',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio del Cupo',
  `end_date` date NOT NULL COMMENT 'Fecha de fin del Cupo',
  `quantity` int DEFAULT '0' COMMENT 'Cupo de Habitaciones',
  `remarks` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si el Cupo esta activo o no',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_ALLOTMENT_DOMAIN` (`domain`),
  KEY `IDX_ALLOTMENT_HOTEL` (`hotel`),
  KEY `IDX_ALLOTMENT_AGENCY` (`agency`),
  KEY `IDX_ALLOTMENT_AGENCY_GROUP` (`agency_group`),
  CONSTRAINT `FK_ALLOTMENT_AGENCY` FOREIGN KEY (`agency`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_ALLOTMENT_AGENCY_GROUP` FOREIGN KEY (`agency_group`) REFERENCES `invoicing_group` (`id`),
  CONSTRAINT `FK_ALLOTMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ALLOTMENT_HOTEL` FOREIGN KEY (`hotel`) REFERENCES `hotel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cupos de seguridad';

#
# Table structure for table `allotment_item`
#

CREATE TABLE `allotment_item` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `allotment` int NOT NULL COMMENT 'Identificador del Cupo de seguridad',
  `item` int NOT NULL COMMENT 'Identificador del Tipo de Habitacion',
  PRIMARY KEY (`id`),
  KEY `IDX_ALLOTMENT_ITEM_DOMAIN` (`domain`),
  KEY `IDX_ALLOTMENT_ITEM_ALLOTMENT` (`allotment`),
  KEY `IDX_ALLOTMENT_ITEM_ITEM` (`item`),
  CONSTRAINT `FK_ALLOTMENT_ITEM_ALLOTMENT` FOREIGN KEY (`allotment`) REFERENCES `allotment` (`id`),
  CONSTRAINT `FK_ALLOTMENT_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ALLOTMENT_ITEM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Habitacion por Cupo';

#
# Table structure for table `allotment_tariff`
#

CREATE TABLE `allotment_tariff` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `allotment` int NOT NULL COMMENT 'Identificador del Cupo de seguridad',
  `tariff` int NOT NULL COMMENT 'Identificador de la Tarifa',
  PRIMARY KEY (`id`),
  KEY `IDX_ALLOTMENT_TARIFF_DOMAIN` (`domain`),
  KEY `IDX_ALLOTMENT_TARIFF_ALLOTMENT` (`allotment`),
  KEY `IDX_ALLOTMENT_TARIFF_TARIFF` (`tariff`),
  CONSTRAINT `FK_ALLOTMENT_TARIFF_ALLOTMENT` FOREIGN KEY (`allotment`) REFERENCES `allotment` (`id`),
  CONSTRAINT `FK_ALLOTMENT_TARIFF_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ALLOTMENT_TARIFF_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas por Cupo';

#
# Table structure for table `alumn_loan`
#

CREATE TABLE `alumn_loan` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Prestamo',
  `domain` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `customer` int NOT NULL COMMENT 'Alumno al que se le realizo el Prestamo',
  `material` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Material prestado',
  `loan_date` date NOT NULL COMMENT 'Fecha del Prestamo',
  `end_date` date DEFAULT NULL COMMENT 'Fecha devolucion del material',
  `comments` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Observaciones',
  PRIMARY KEY (`id`),
  KEY `IDX_ALUMN_LOAN_CUSTOMER` (`customer`),
  KEY `IDX_ALUMN_LOAN_DOMAIN` (`domain`),
  CONSTRAINT `FK_ALUMN_LOAN_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_ALUMN_LOAN_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Prestamos a Alumnos';

#
# Table structure for table `amortization`
#

CREATE TABLE `amortization` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del inmovilizado',
  `initial_date` date NOT NULL COMMENT 'Fecha de inicio de la Amortizacion',
  `deadline` date DEFAULT NULL COMMENT 'Fecha de baja de la Amortizacion',
  `amount` decimal(15,4) NOT NULL DEFAULT '0' COMMENT 'Importe a amortizar.',
  `fee_period` tinyint NOT NULL DEFAULT '0' COMMENT 'Periodo de las cuotas de Amortizacion',
  `sale_amount` decimal(15,4) DEFAULT NULL COMMENT 'Importe de la venta',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  `invest_asset` int DEFAULT NULL COMMENT 'Identificador del Bien afecto',
  `fixed_asset_account` int NOT NULL COMMENT 'Cuenta de inmovilizado',
  `accumulated_account` int NOT NULL COMMENT 'Cuenta de Amortizacion acumulada',
  `allocation_account` int NOT NULL COMMENT 'Cuenta para la dotacion de la Amortizacion',
  `percentage` decimal(15,4) DEFAULT '0' COMMENT 'Porcentaje de Amortizacion',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad',
  PRIMARY KEY (`id`),
  KEY `IDX_AMORTIZATION_FIXED_ASSET_ACCOUNT` (`fixed_asset_account`),
  KEY `IDX_AMORTIZATION_ACCUMULATED_ACCOUNT` (`accumulated_account`),
  KEY `IDX_AMORTIZATION_ALLOCATION_ACCOUNT` (`allocation_account`),
  KEY `IDX_AMORTIZATION_DOMAIN` (`domain`),
  KEY `IDX_AMORTIZATION_INVEST_ASSET` (`invest_asset`),
  CONSTRAINT `FK_AMORTIZATION_ACCUMULATED_ACCOUNT` FOREIGN KEY (`accumulated_account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_AMORTIZATION_ALLOCATION_ACCOUNT` FOREIGN KEY (`allocation_account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_AMORTIZATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_AMORTIZATION_FIXED_ASSET_ACCOUNT` FOREIGN KEY (`fixed_asset_account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_AMORTIZATION_INVEST_ASSET` FOREIGN KEY (`invest_asset`) REFERENCES `invest_asset` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Fichas de Amortizacion Contables';

#
# Table structure for table `amortization_detail`
#

CREATE TABLE `amortization_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `amortization` int NOT NULL COMMENT 'Ficha de Amortizacion',
  `from_date` date NOT NULL COMMENT 'Desde fecha',
  `to_date` date NOT NULL COMMENT 'Hasta fecha',
  `coefficient` decimal(15,3) NOT NULL COMMENT 'Coeficiente de Amortizacion',
  `allocation` decimal(15,3) NOT NULL COMMENT 'Dotacion de la Amortizacion',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Estatus del Detalle de Amortizacion',
  `account_entry` int DEFAULT NULL COMMENT 'Posicion del Apunte Contable',
  `fiscal_allocation` decimal(15,3) DEFAULT '0.000' COMMENT 'Dotacion fiscal',
  PRIMARY KEY (`id`),
  KEY `IDX_AMORTIZATION_DETAIL_AMORTIZATION` (`amortization`),
  KEY `IDX_AMORTIZATION_DETAIL_ACCOUNT_ENTRY` (`account_entry`),
  KEY `IDX_AMORTIZATION_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_AMORTIZATION_DETAIL_ACCOUNT_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_AMORTIZATION_DETAIL_AMORTIZATION` FOREIGN KEY (`amortization`) REFERENCES `amortization` (`id`),
  CONSTRAINT `FK_AMORTIZATION_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de la Ficha de Amortizacion Contable';

#
# Table structure for table `amortization_invoice`
#

CREATE TABLE `amortization_invoice` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `amortization` int NOT NULL COMMENT 'Identificador de la Ficha de Amortizacion',
  `invoice` int NOT NULL COMMENT 'Identificador de la Factura',
  `sales` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si es venta de inmovilizado o no',
  PRIMARY KEY (`id`),
  KEY `IDX_AMORTIZATION_INVOICE_DOMAIN` (`domain`),
  KEY `IDX_AMORTIZATION_INVOICE_AMORTIZATION` (`amortization`),
  KEY `IDX_AMORTIZATION_INVOICE_INVOICE` (`invoice`),
  CONSTRAINT `FK_AMORTIZATION_INVOICE_AMORTIZATION` FOREIGN KEY (`amortization`) REFERENCES `amortization` (`id`),
  CONSTRAINT `FK_AMORTIZATION_INVOICE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_AMORTIZATION_INVOICE_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Vinculo entr la Ficha de Amortizacion y la factura.';

#
# Table structure for table `amortization_type`
#

CREATE TABLE `amortization_type` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `fixed_asset_account` varchar(4) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Cuenta de inmovilizado',
  `accumulated_account` varchar(4) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Cuenta de amortizacion acumulada',
  `allocation_account` varchar(4) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Cuenta para la dotacion de la amortizacion',
  `percentage` decimal(15,4) DEFAULT '0' COMMENT 'Porcentaje de amortizacion',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Amortizacion',
  PRIMARY KEY (`id`),
  KEY `IDX_AMORTIZATION_TYPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_AMORTIZATION_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Amortizacion';

#
# Table structure for table `app_param`
#

CREATE TABLE `app_param` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Parametro',
  `value` varchar(96) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor del Parametro',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_APP_PARAM_DOMAIN_NAME` (`domain`,`name`),
  KEY `IDX_APP_PARAM_DOMAIN` (`domain`),
  CONSTRAINT `FK_APP_PARAM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Parametros de la Aplicacion';

#
# Table structure for table `application`
#

CREATE TABLE `application` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Aplicacion',
  `description` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Aplicacion',
  `contratable` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si la Aplicacion es contratable o no',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Aplicaciones';

#
# Table structure for table `application_role`
#

CREATE TABLE `application_role` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `application` int NOT NULL COMMENT 'Identificador de la Aplicacion',
  `role` int NOT NULL COMMENT 'Identificador del Role',
  PRIMARY KEY (`id`),
  KEY `IDX_APPLICATION_ROLE_APPLICATION` (`application`),
  KEY `IDX_APPLICATION_ROLE_ROLE` (`role`),
  CONSTRAINT `FK_APPLICATION_ROLE_APPLICATION` FOREIGN KEY (`application`) REFERENCES `application` (`id`),
  CONSTRAINT `FK_APPLICATION_ROLE_ROLE` FOREIGN KEY (`role`) REFERENCES `role` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Roles de la Aplicacion';

#
# Table structure for table `application_user`
#

CREATE TABLE `application_user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `user_id` int NOT NULL COMMENT 'Identificador del Usuario',
  `domain_application` int NOT NULL COMMENT 'Identificador de la Aplicacion del Dominio',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si la Aplicacion del Dominio esta activo o no',
  PRIMARY KEY (`id`),
  KEY `IDX_APPLICATION_USER_USER` (`user_id`),
  KEY `IDX_APPLICATION_USER_DOMAIN_APPLICATION` (`domain_application`),
  KEY `IDX_APPLICATION_USER_DOMAIN` (`domain`),
  CONSTRAINT `FK_APPLICATION_USER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_APPLICATION_USER_DOMAIN_APPLICATION` FOREIGN KEY (`domain_application`) REFERENCES `domain_application` (`id`),
  CONSTRAINT `FK_APPLICATION_USER_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Usuarios de las Aplicaciones del Dominio';

#
# Table structure for table `application_user_profile`
#

CREATE TABLE `application_user_profile` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `application_user` int NOT NULL COMMENT 'Identificador del Usuario de la Aplicacion',
  `profile` int NOT NULL COMMENT 'Identificador del Perfil',
  PRIMARY KEY (`id`),
  KEY `IDX_APPLICATION_USER_PROFILE_PROFILE` (`profile`),
  KEY `IDX_APPLICATION_USER_PROFILE_APPLICATION_USER` (`application_user`),
  KEY `IDX_APPLICATION_USER_PROFILE_DOMAIN` (`domain`),
  CONSTRAINT `FK_APPLICATION_USER_PROFILE_APPLICATION_USER` FOREIGN KEY (`application_user`) REFERENCES `application_user` (`id`),
  CONSTRAINT `FK_APPLICATION_USER_PROFILE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_APPLICATION_USER_PROFILE_PROFILE` FOREIGN KEY (`profile`) REFERENCES `profile` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Perfiles del Usuario para la Aplicacion';

#
# Table structure for table `asset`
#

CREATE TABLE `asset` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Activo',
  `name` varchar(10) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre corto del Activo',
  PRIMARY KEY (`id`),
  KEY `IDX_ASSET_DOMAIN` (`domain`),
  CONSTRAINT `FK_ASSET_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Activos';

#
# Table structure for table `asset_activity`
#

CREATE TABLE `asset_activity` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `asset` int NOT NULL COMMENT 'Identificador del Activo',
  `date` date NOT NULL COMMENT 'Fecha de la Actividad',
  `from_time` datetime NOT NULL COMMENT 'Hora de inicio de la Actividad',
  `to_time` datetime NOT NULL COMMENT 'Hora final de la Actividad',
  `who` varchar(20) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Quien solicita el Activo',
  `why` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Motivo de solicitud del Activo',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Estado de la Solicitud',
  PRIMARY KEY (`id`),
  KEY `IDX_ASSET_ACTIVITY_ASSET` (`asset`),
  KEY `IDX_ASSET_ACTIVITY_DOMAIN` (`domain`),
  KEY `IDX_ASSET_ACTIVITY_DATE` (`date`),
  CONSTRAINT `FK_ASSET_ACTIVITY_ASSET` FOREIGN KEY (`asset`) REFERENCES `asset` (`id`),
  CONSTRAINT `FK_ASSET_ACTIVITY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades sobre el Activo';

#
# Table structure for table `asset_feature`
#

CREATE TABLE `asset_feature` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `asset` int NOT NULL COMMENT 'Identificador del Activo',
  `feature` int NOT NULL COMMENT 'Identificador de la Caracteristica',
  PRIMARY KEY (`id`),
  KEY `IDX_ASSET_FEATURE_ASSET` (`asset`),
  KEY `IDX_ASSET_FEATURE_FEATURE` (`feature`),
  KEY `IDX_ASSET_FEATURE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ASSET_FEATURE_ASSET` FOREIGN KEY (`asset`) REFERENCES `asset` (`id`),
  CONSTRAINT `FK_ASSET_FEATURE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ASSET_FEATURE_FEATURE` FOREIGN KEY (`feature`) REFERENCES `feature` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Caracteristicas por Activo';

#
# Table structure for table `auth`
#

CREATE TABLE `auth` (
  `id` binary(16) NOT NULL COMMENT 'Identificador unico',
  `email` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Identificador del Usuario (Email)',
  `password` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Contrasena del Usuario',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `surname` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `document` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `phone` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Autenticacion';

#
# Table structure for table `auth_attach`
#

CREATE TABLE `auth_attach` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `auth` binary(16) NOT NULL COMMENT 'Identificador de Auth',
  `mimeType` tinyint DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Archivo Adjunto',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ficheros de Auth';

#
# Table structure for table `auth_device`
#

CREATE TABLE `auth_device` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',
  `auth` binary(16) NOT NULL COMMENT 'Identificador unico de Auth',
  `device_type` tinyint NOT NULL COMMENT 'Tipo del dispositivo',
  `device_token` varchar(255) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `last_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Dispositivos vinculados a auth';

#
# Table structure for table `auto_concept`
#

CREATE TABLE `auto_concept` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Concepto Automatico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `description` char(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Concepto Automatico',
  PRIMARY KEY (`id`),
  KEY `IDX_AUTO_CONCEPT_DOMAIN` (`domain`),
  CONSTRAINT `FK_AUTO_CONCEPT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos Automaticos';

#
# Table structure for table `balance`
#

CREATE TABLE `balance` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Nombre del Balance',
  `removable` tinyint(1) DEFAULT '0' COMMENT 'Indica se puede ser borrado por el usuario',
  `type` tinyint DEFAULT '0' COMMENT 'Tipo de Balance',
  PRIMARY KEY (`id`),
  KEY `IDX_BALANCE_DOMAIN` (`domain`),
  CONSTRAINT `FK_BALANCE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Balances';

#
# Table structure for table `balance_detail`
#

CREATE TABLE `balance_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `balance` int NOT NULL COMMENT 'Identificador del Balance',
  `code` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del Detalle en el Balance',
  `description` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripción del detalle de balance',
  `accounts` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Cuentas separadas por comas, que forman el acumulado.',
  `sortKey` int DEFAULT '0' COMMENT 'Orden el que aparecera en el listado.',
  `notes` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Notas en el Balance',
  `title` tinyint(1) NOT NULL DEFAULT '0',
  `internal_calculation` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si es un calculo interno, es decir si el contenido de accounts son referencias a la columna -code- de esta tabla',
  `visible` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Si aparece o no en la impresion.',
  `zeroFlag` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Flag que se activa cuando la cuenta o cuentas tienen valor 0.',
  `creditNature` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Si es verdadero se hace una haber menos debe de las cuentas indicadas',
  PRIMARY KEY (`id`),
  KEY `IDX_BALANCE_DETAIL_BALANCE` (`balance`),
  KEY `IDX_BALANCE_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_BALANCE_DETAIL_BALANCE` FOREIGN KEY (`balance`) REFERENCES `balance` (`id`),
  CONSTRAINT `FK_BALANCE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Balace';

#
# Table structure for table `bank_concept`
#

CREATE TABLE `bank_concept` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Concepto',
  `account` int DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY (`id`),
  KEY `IDX_BANK_CONCEPT_DOMAIN` (`domain`),
  KEY `IDX_BANK_CONCEPT_ACCOUNT` (`account`),
  CONSTRAINT `FK_BANK_CONCEPT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_BANK_CONCEPT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos bancarios';

#
# Table structure for table `bank_statement`
#

CREATE TABLE `bank_statement` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `rbank` int NOT NULL COMMENT 'Identificador de Banco de la Compaia',
  `lot_number` int NOT NULL DEFAULT '0' COMMENT 'Numero de lote',
  `operation_date` date NOT NULL COMMENT 'Fecha de operacion',
  `common_concept` tinyint NOT NULL DEFAULT '0' COMMENT 'Concepto comun',
  `own_concept` varchar(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Concepto propio',
  `payment` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si es un pago',
  `amount` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT 'Importe',
  `document` varchar(10) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de documento',
  `reference1` varchar(12) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Referencia 1',
  `reference2` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Referencia 2',
  `description` varchar(80) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `reliability` tinyint DEFAULT '0' COMMENT 'Fiabilidad del punteo',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad',
  `status` tinyint DEFAULT '0' COMMENT 'Estado',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  PRIMARY KEY (`id`),
  KEY `IDX_BANK_STATEMENT_RBANK` (`rbank`),
  KEY `IDX_BANK_STATEMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_BANK_STATEMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_BANK_STATEMENT_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Extractos bancarios';

#
# Table structure for table `bank_statement_link`
#

CREATE TABLE `bank_statement_link` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `bank_statement` int NOT NULL COMMENT 'Identificador de Extracto bancario',
  `source` tinyint NOT NULL DEFAULT '0' COMMENT 'Origen',
  `source_id` int NOT NULL DEFAULT '0' COMMENT 'Identificador del origen',
  `source_date` date DEFAULT NULL COMMENT 'Fecha del origen',
  `amount` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT 'Importe',
  `status` tinyint DEFAULT '0' COMMENT 'Estado',
  `linked_bank_statement_link` int DEFAULT NULL COMMENT 'Identificador del Enlace de Extracto bancario asociado',
  PRIMARY KEY (`id`),
  KEY `IDX_BANK_STATEMENT_LINK_BANK_STATEMENT` (`bank_statement`),
  KEY `IDX_BANK_STATEMENT_LINK_BANK_STATEMENT_LINK` (`linked_bank_statement_link`),
  KEY `IDX_BANK_STATEMENT_LINK_DOMAIN` (`domain`),
  CONSTRAINT `FK_BANK_STATEMENT_LINK_BANK_STATEMENT` FOREIGN KEY (`bank_statement`) REFERENCES `bank_statement` (`id`),
  CONSTRAINT `FK_BANK_STATEMENT_LINK_BANK_STATEMENT_LINK` FOREIGN KEY (`linked_bank_statement_link`) REFERENCES `bank_statement_link` (`id`),
  CONSTRAINT `FK_BANK_STATEMENT_LINK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Enlaces del Extracto bancario';

#
# Table structure for table `bonus_concept`
#

CREATE TABLE `bonus_concept` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `expression` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe',
  `description` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Bonificacion Salarial',
  PRIMARY KEY (`id`),
  KEY `IDX_BONUS_CONCEPT_DOMAIN` (`domain`),
  CONSTRAINT `FK_BONUS_CONCEPT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos de bonficaciones y/o reducciones';

#
# Table structure for table `booking`
#

CREATE TABLE `booking` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation_room` int NOT NULL COMMENT 'Identificador de la Habitacion de la Reserva',
  `hotel` int NOT NULL COMMENT 'Identificador del Hotel',
  `agency` int DEFAULT NULL COMMENT 'Identificador de la Agencia',
  `item` int NOT NULL COMMENT 'Identificador del Producto',
  `tariff` int NOT NULL COMMENT 'Identificador de la Tarifa',
  `stay_date` date NOT NULL COMMENT 'Fecha de estancia',
  `stay_type` tinyint NOT NULL DEFAULT '0' COMMENT 'Indica si es una entrada, una salida o una permanencia',
  `guests` int NOT NULL DEFAULT '0' COMMENT 'Numero de Huespedes',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_BOOKING_ROOM_DATE` (`project_reservation_room`,`stay_date`,`stay_type`),
  KEY `IDX_BOOKING_DOMAIN` (`domain`),
  KEY `IDX_BOOKING_PROJECT_RESERVATION_ROOM` (`project_reservation_room`),
  KEY `IDX_BOOKING_HOTEL` (`hotel`),
  KEY `IDX_BOOKING_AGENCY` (`agency`),
  KEY `IDX_BOOKING_ITEM` (`item`),
  KEY `IDX_BOOKING_TARIFF` (`tariff`),
  CONSTRAINT `FK_BOOKING_AGENCY` FOREIGN KEY (`agency`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_BOOKING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_BOOKING_HOTEL` FOREIGN KEY (`hotel`) REFERENCES `hotel` (`id`),
  CONSTRAINT `FK_BOOKING_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_BOOKING_PROJECT_RESERVATION_ROOM` FOREIGN KEY (`project_reservation_room`) REFERENCES `project_reservation_room` (`id`),
  CONSTRAINT `FK_BOOKING_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Booking de Hoteles';

#
# Table structure for table `brand`
#

CREATE TABLE `brand` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Marca Comercial',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Marca Comercial',
  PRIMARY KEY (`id`),
  KEY `IDX_BRAND_DOMAIN` (`domain`),
  CONSTRAINT `FK_BRAND_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Marcas Comerciales';

#
# Table structure for table `calendar`
#

CREATE TABLE `calendar` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `holiday` int DEFAULT NULL COMMENT 'Identificador de Festivos',
  `anual_hours` decimal(15,4) DEFAULT '0' COMMENT 'Horas anuales del Calendario',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Calendario',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  `monday` tinyint DEFAULT '0' COMMENT 'Tipo de dia',
  `monday_hours` decimal(15,4) DEFAULT '0' COMMENT 'Numero de horas laborables',
  `tuesday` tinyint DEFAULT '0' COMMENT 'Tipo de dia',
  `tuesday_hours` decimal(15,4) DEFAULT '0' COMMENT 'Numero de horas laborables',
  `wednesday` tinyint DEFAULT '0' COMMENT 'Tipo de dia',
  `wednesday_hours` decimal(15,4) DEFAULT '0' COMMENT 'Numero de horas laborables',
  `thursday` tinyint DEFAULT '0' COMMENT 'Tipo de dia',
  `thursday_hours` decimal(15,4) DEFAULT '0' COMMENT 'Numero de horas laborables',
  `friday` tinyint DEFAULT '0' COMMENT 'Tipo de dia',
  `friday_hours` decimal(15,4) DEFAULT '0' COMMENT 'Numero de horas laborables',
  `saturday` tinyint DEFAULT '1' COMMENT 'Tipo de dia',
  `saturday_hours` decimal(15,4) DEFAULT '0' COMMENT 'Numero de horas laborables',
  `sunday` tinyint DEFAULT '1' COMMENT 'Tipo de dia',
  `sunday_hours` decimal(15,4) DEFAULT '0' COMMENT 'Numero de horas laborables',
  `generic` tinyint(1) DEFAULT '1' COMMENT 'Indica si es editable o no',
  `calendar` int DEFAULT NULL COMMENT 'Calendario del que se hereda',
  PRIMARY KEY (`id`),
  KEY `IDX_CALENDAR_HOLIDAY` (`holiday`),
  KEY `IDX_CALENDAR_CALENDAR` (`calendar`),
  KEY `IDX_CALENDAR_DOMAIN` (`domain`),
  CONSTRAINT `FK_CALENDAR_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_CALENDAR_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CALENDAR_HOLIDAY` FOREIGN KEY (`holiday`) REFERENCES `holiday` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Calendarios Laborales';

#
# Table structure for table `calendar_holiday`
#

CREATE TABLE `calendar_holiday` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `calendar` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Calendario',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Festivo',
  `date` date DEFAULT NULL COMMENT 'Fecha del festivo',
  `day_type` tinyint DEFAULT '0' COMMENT 'Tipo de dia',
  `hours` decimal(15,4) DEFAULT '0' COMMENT 'Numero de horas laborables',
  PRIMARY KEY (`id`),
  KEY `IDX_CALENDAR_HOLIDAY_CALENDAR` (`calendar`),
  KEY `IDX_CALENDAR_HOLIDAY_DOMAIN` (`domain`),
  CONSTRAINT `FK_CALENDAR_HOLIDAY_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_CALENDAR_HOLIDAY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Festivos de Calendarios';

#
# Table structure for table `calendar_period`
#

CREATE TABLE `calendar_period` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `calendar` int NOT NULL COMMENT 'Identificador del Calendario',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Periodo',
  `month` tinyint DEFAULT '0' COMMENT 'Mes del periodo',
  `start_day` tinyint DEFAULT '0' COMMENT 'Dia inicio del periodo',
  `end_day` tinyint DEFAULT '0' COMMENT 'Dia fin del periodo',
  `monday` tinyint DEFAULT '0' COMMENT 'Tipo de dia',
  `monday_hours` decimal(15,4) DEFAULT '0' COMMENT 'Numero de horas laborables',
  `tuesday` tinyint DEFAULT '0' COMMENT 'Tipo de dia',
  `tuesday_hours` decimal(15,4) DEFAULT '0' COMMENT 'Numero de horas laborables',
  `wednesday` tinyint DEFAULT '0' COMMENT 'Tipo de dia',
  `wednesday_hours` decimal(15,4) DEFAULT '0' COMMENT 'Numero de horas laborables',
  `thursday` tinyint DEFAULT '0' COMMENT 'Tipo de dia',
  `thursday_hours` decimal(15,4) DEFAULT '0' COMMENT 'Numero de horas laborables',
  `friday` tinyint DEFAULT '0' COMMENT 'Tipo de dia',
  `friday_hours` decimal(15,4) DEFAULT '0' COMMENT 'Numero de horas laborables',
  `saturday` tinyint DEFAULT '0' COMMENT 'Tipo de dia',
  `saturday_hours` decimal(15,4) DEFAULT '0' COMMENT 'Numero de horas laborables',
  `sunday` tinyint DEFAULT '0' COMMENT 'Tipo de dia',
  `sunday_hours` decimal(15,4) DEFAULT '0' COMMENT 'Numero de horas laborables',
  PRIMARY KEY (`id`),
  KEY `IDX_CALENDAR_PERIOD_CALENDAR` (`calendar`),
  KEY `IDX_CALENDAR_PERIOD_DOMAIN` (`domain`),
  CONSTRAINT `FK_CALENDAR_PERIOD_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_CALENDAR_PERIOD_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Periodos de Calendarios';

#
# Table structure for table `campaign`
#

CREATE TABLE `campaign` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Campaña',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `campaign_type` int DEFAULT NULL COMMENT 'Identificador del Tipo de Campaña',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Campaña',
  `process` int NOT NULL COMMENT 'Identificador del Proceso',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio de la Campaña',
  `end_date` date NOT NULL COMMENT 'Fecha de finalizacion de la Campaña',
  `workgroup` int NOT NULL COMMENT 'Grupo de Trabajo supervisor de la Campaña',
  `manual` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Tipo de Campaña',
  `status` tinyint DEFAULT NULL COMMENT 'Estado de la Campaña',
  PRIMARY KEY (`id`),
  KEY `IDX_CAMPAIGN_CAMPAIGN_TYPE` (`campaign_type`),
  KEY `IDX_CAMPAIGN_PROCESS` (`process`),
  KEY `IDX_CAMPAIGN_WORKGROUP` (`workgroup`),
  KEY `IDX_CAMPAIGN_DOMAIN` (`domain`),
  CONSTRAINT `FK_CAMPAIGN_CAMPAIGN_TYPE` FOREIGN KEY (`campaign_type`) REFERENCES `campaign_type` (`id`),
  CONSTRAINT `FK_CAMPAIGN_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CAMPAIGN_PROCESS` FOREIGN KEY (`process`) REFERENCES `process` (`id`),
  CONSTRAINT `FK_CAMPAIGN_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Campañas';

#
# Table structure for table `campaign_project`
#

CREATE TABLE `campaign_project` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Relacion de Campañas y Expedientes',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `campaign` int NOT NULL COMMENT 'Identificador de la Campaña',
  `project` int NOT NULL COMMENT 'Identificador del Expediente',
  PRIMARY KEY (`id`),
  KEY `IDX_CAMPAIGN_PROJECT_CAMPAIGN` (`campaign`),
  KEY `IDX_CAMPAIGN_PROJECT_PROJECT` (`project`),
  KEY `IDX_CAMPAIGN_PROJECT_DOMAIN` (`domain`),
  CONSTRAINT `FK_CAMPAIGN_PROJECT_CAMPAIGN` FOREIGN KEY (`campaign`) REFERENCES `campaign` (`id`),
  CONSTRAINT `FK_CAMPAIGN_PROJECT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CAMPAIGN_PROJECT_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Campañas y Expedientes';

#
# Table structure for table `campaign_type`
#

CREATE TABLE `campaign_type` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion',
  `active` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Activo si o no',
  PRIMARY KEY (`id`),
  KEY `IDX_CAMPAIGN_TYPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_CAMPAIGN_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Campañas';

#
# Table structure for table `carrier`
#

CREATE TABLE `carrier` (
  `registry` int NOT NULL COMMENT 'Registro de la Agencia de Transporte',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `scope` int NOT NULL COMMENT 'Identificador del Ambito',
  `status` tinyint DEFAULT NULL COMMENT 'Estado de la Agencia de Transporte',
  PRIMARY KEY (`registry`),
  KEY `IDX_CARRIER_DOMAIN` (`domain`),
  KEY `IDX_CARRIER_SCOPE` (`scope`),
  CONSTRAINT `FK_CARRIER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CARRIER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_CARRIER_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Agencias de Transporte';

#
# Table structure for table `carrier_packing`
#

CREATE TABLE `carrier_packing` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la hoja de ruta',
  `domain` int NOT NULL COMMENT 'Identificador del dominio',
  `series` char(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie de la hoja de ruta',
  `number` int NOT NULL DEFAULT '0' COMMENT 'Numero de la hoja de ruta',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT 'Indica el tipo de la hoja de ruta',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Indica el estado de la hoja de ruta',
  `issue_date` datetime DEFAULT NULL COMMENT 'Fecha de emision',
  `carrier` int NOT NULL COMMENT 'Identificador de la agencia de transporte',
  `delivery_date` datetime DEFAULT NULL COMMENT 'Fecha de entrega',
  `carrier_reference` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Referencia de la agencia de transporte',
  `number_plate` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de matricula del vehiculo',
  `driver_name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del conductor',
  `driver_document` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de documento del conductor',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Observaciones carrier packing',
  `gross` decimal(15,4) DEFAULT NULL COMMENT 'Bruto',
  `tare` decimal(15,4) DEFAULT NULL COMMENT 'Tara',
  `additional_tare` decimal(15,4) DEFAULT NULL COMMENT 'Tara Adicional',
  `net` decimal(15,4) DEFAULT NULL COMMENT 'Neto',
  `reception_start_date` datetime DEFAULT NULL COMMENT 'Fecha entrada transporte, recepcion',
  `reception_end_date` datetime DEFAULT NULL COMMENT 'Fecha salida transporte, recepcion',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_CARRIER_PACKING_DOMAIN` (`domain`),
  KEY `IDX_CARRIER_PACKING_CARRIER` (`carrier`),
  CONSTRAINT `FK_CARRIER_PACKING_CARRIER` FOREIGN KEY (`carrier`) REFERENCES `carrier` (`registry`),
  CONSTRAINT `FK_CARRIER_PACKING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Hojas de ruta';

#
# Table structure for table `cashflow_forecast`
#

CREATE TABLE `cashflow_forecast` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `payment` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si es un pago o un cobro',
  `description` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio de aplicacion',
  `due_date` date DEFAULT NULL COMMENT 'Fecha final de aplicacion',
  `rbank` int DEFAULT NULL COMMENT 'Identificador de Banco de la Compaia',
  `amount` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT 'Importe',
  `payment_day` decimal(15,2) NOT NULL DEFAULT '1.00' COMMENT 'Dia de pago',
  `january` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Aplicable en enero',
  `february` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Aplicable en febrero',
  `march` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Aplicable en marzo',
  `april` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Aplicable en abril',
  `may` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Aplicable en mayo',
  `june` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Aplicable en junio',
  `july` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Aplicable en julio',
  `august` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Aplicable en agosto',
  `september` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Aplicable en septiembre',
  `october` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Aplicable en octubre',
  `november` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Aplicable en noviembre',
  `december` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Aplicable en diciembre',
  PRIMARY KEY (`id`),
  KEY `IDX_CASHFLOW_FORECAST_RBANK` (`rbank`),
  KEY `IDX_CASHFLOW_FORECAST_DOMAIN` (`domain`),
  CONSTRAINT `FK_CASHFLOW_FORECAST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CASHFLOW_FORECAST_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Prevision de tesoreria';

#
# Table structure for table `catalogue`
#

CREATE TABLE `catalogue` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Catalogo',
  `purchase` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si se trata de un Catalogo de Compras o Ventas',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio del Catalogo',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de fin del Catalogo',
  PRIMARY KEY (`id`),
  KEY `IDX_CATALOGUE_DOMAIN` (`domain`),
  CONSTRAINT `FK_CATALOGUE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Catalogos';

#
# Table structure for table `catalogue_category`
#

CREATE TABLE `catalogue_category` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `catalogue` int NOT NULL COMMENT 'Identificador del Catalogo',
  `category` int NOT NULL COMMENT 'Identificador de la Categoria',
  `quantity` decimal(15,4) DEFAULT '0' COMMENT 'Cantidad a partir de la cual se aplica el descuento',
  `discount` decimal(6,2) DEFAULT '0.00' COMMENT 'Descuento de la Categoria en el Catalogo',
  PRIMARY KEY (`id`),
  KEY `IDX_CATALOGUE_CATEGORY_CATALOGUE` (`catalogue`),
  KEY `IDX_CATALOGUE_CATEGORY_PCATEGORY` (`category`),
  KEY `IDX_CATALOGUE_CATEGORY_DOMAIN` (`domain`),
  CONSTRAINT `FK_CATALOGUE_CATEGORY_CATALOGUE` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`),
  CONSTRAINT `FK_CATALOGUE_CATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CATALOGUE_CATEGORY_PCATEGORY` FOREIGN KEY (`category`) REFERENCES `pcategory` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias del Catalogo';

#
# Table structure for table `catalogue_item`
#

CREATE TABLE `catalogue_item` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `catalogue` int NOT NULL COMMENT 'Identificador del Catalogo',
  `product` int NOT NULL COMMENT 'Identificador del Producto',
  `item` int DEFAULT NULL COMMENT 'Identificador del Articulo',
  `quantity` decimal(15,4) DEFAULT '0' COMMENT 'Cantidad a partir de la cual se aplica el precio o descuento',
  `price` decimal(15,4) DEFAULT '0' COMMENT 'Precio del Articulo en el Catalogo',
  `discount` decimal(6,2) DEFAULT '0.00' COMMENT 'Descuento del Articulo en el Catalogo',
  PRIMARY KEY (`id`),
  KEY `IDX_CATALOGUE_ITEM_CATALOGUE` (`catalogue`),
  KEY `IDX_CATALOGUE_ITEM_ITEM` (`item`),
  KEY `IDX_CATALOGUE_ITEM_DOMAIN` (`domain`),
  KEY `IDX_CATALOGUE_ITEM_PRODUCT` (`product`),
  CONSTRAINT `FK_CATALOGUE_ITEM_CATALOGUE` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`),
  CONSTRAINT `FK_CATALOGUE_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CATALOGUE_ITEM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_CATALOGUE_ITEM_PRODUCT` FOREIGN KEY (`product`) REFERENCES `product` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulos del Catalogo';

#
# Table structure for table `category`
#

CREATE TABLE `category` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Categoria',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Categoria',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT 'Tipo de la Categoria',
  `scope` int DEFAULT NULL COMMENT 'Identificador del Ambito',
  `description` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de la Categoria',
  `url` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Url de la Categoria',
  `rattach` int DEFAULT NULL COMMENT 'Identificador del Archivo Adjunto',
  PRIMARY KEY (`id`),
  KEY `IDX_CATEGORY_DOMAIN` (`domain`),
  KEY `IDX_CATEGORY_RATTACH` (`rattach`),
  KEY `IDX_CATEGORY_SCOPE` (`scope`),
  CONSTRAINT `FK_CATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CATEGORY_RATTACH` FOREIGN KEY (`rattach`) REFERENCES `rattach` (`id`),
  CONSTRAINT `FK_CATEGORY_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias';

#
# Table structure for table `certifica2_batch`
#

CREATE TABLE `certifica2_batch` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del certificado de empresa de la remesa',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int NOT NULL COMMENT 'Identificador unico del certificado de empresa de la empresa',
  `date` datetime DEFAULT NULL COMMENT 'Fecha de la remesa',
  `status` int DEFAULT NULL COMMENT 'Estado de la remesa',
  `sign` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Huella digital obtenida de la respuesta',
  `communication_id` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Identificador resultante de la comunicacion',
  `income_file` mediumblob COMMENT 'Archivo respuesta en binario',
  `income_file_date` datetime DEFAULT NULL COMMENT 'Fecha de respuesta',
  `outcome_file` mediumblob COMMENT 'Archivo Adjunto en binario',
  `outcome_file_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  PRIMARY KEY (`id`),
  KEY `IDX_CERTIFICA2_BATCH_ENTERPRISE` (`enterprise`),
  KEY `IDX_CERTIFICA2_BATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas de certificados de empresa';

#
# Table structure for table `certifica2_batch_detail`
#

CREATE TABLE `certifica2_batch_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del certificado de empresa de la remesa',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `certifica2_batch` int NOT NULL COMMENT 'Identificador unico del certificado de empresa',
  `contract` int NOT NULL COMMENT 'Identificador unico del contrato de empleado',
  `suspension_cause_code` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo causa suspension',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Estado de la linea de la remesa',
  `ere_number` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_CERTIFICA2_BATCH_DETAIL_CERTIFICA2_BATCH` (`certifica2_batch`),
  KEY `IDX_CERTIFICA2_BATCH_DETAIL_CONTRACT` (`contract`),
  KEY `IDX_CERTIFICA2_BATCH_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_DETAIL_CERTIFICA2_BATCH` FOREIGN KEY (`certifica2_batch`) REFERENCES `certifica2_batch` (`id`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_DETAIL_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las remesas de certificados de empresa';

#
# Table structure for table `cnae`
#

CREATE TABLE `cnae` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `code` varchar(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo del CNAE',
  `title` varchar(255) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Titulo del CNAE',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='CNAE';

#
# Table structure for table `cnae2009`
#

CREATE TABLE `cnae2009` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `code` varchar(4) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo del CNAE',
  `title` varchar(255) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Titulo del CNAE',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='CNAE 2009. Clasificación Nacional de Actividades Económicas ';

#
# Table structure for table `cnae2009_rate`
#

CREATE TABLE `cnae2009_rate` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador único',
  `cnae2009` int NOT NULL COMMENT 'CNAE',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `it_amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe por Incapacidad Temporal (I.T.)',
  `ims_amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe por Incapacidad Permanente, Muerte y Supervivencia (I.M.S.)',
  PRIMARY KEY (`id`),
  KEY `IDX_CNAE2009_RATE_CNAE2009` (`cnae2009`),
  CONSTRAINT `FK_CNAE2009_RATE_CNAE2009` FOREIGN KEY (`cnae2009`) REFERENCES `cnae2009` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas de primas para I.T e I.M.S';

#
# Table structure for table `cno`
#

CREATE TABLE `cno` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `code` varchar(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo del CNO',
  `title` varchar(255) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Titulo del CNO',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='CNO';

#
# Table structure for table `commercial_activity`
#

CREATE TABLE `commercial_activity` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Actividad Comercial',
  `probability` int DEFAULT NULL COMMENT 'Probabilidad de la Actividad',
  `survey` int DEFAULT NULL COMMENT 'Identificador del Cuestionario',
  PRIMARY KEY (`id`),
  KEY `IDX_COMMERCIAL_ACTIVITY_DOMAIN` (`domain`),
  KEY `IDX_COMMERCIAL_ACTIVITY_SURVEY` (`survey`),
  CONSTRAINT `FK_COMMERCIAL_ACTIVITY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_COMMERCIAL_ACTIVITY_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades Comerciales';

#
# Table structure for table `commercial_term`
#

CREATE TABLE `commercial_term` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `line` smallint DEFAULT '1' COMMENT 'Numero de linea de Condicion',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Nombre de la Condicion Comercial',
  `description` text CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Condicion Comercial',
  `term_general` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Condición es particular o general',
  PRIMARY KEY (`id`),
  KEY `IDX_COMMERCIAL_TERM_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMMERCIAL_TERM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Condiciones Comerciales';

#
# Table structure for table `commercial_tracking`
#

CREATE TABLE `commercial_tracking` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `date` datetime NOT NULL COMMENT 'Fecha del Seguimiento Comercial',
  `seller` int NOT NULL COMMENT 'Identificador del Comercial',
  `project_commercial` int NOT NULL COMMENT 'Identificador del Proyecto',
  `activity` int NOT NULL COMMENT 'Identificador de la Actividad Comercial',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios del Seguimiento Comercial',
  `status` tinyint NOT NULL COMMENT 'Estado del Seguimiento Comercial',
  `next_commercial_tracking` int DEFAULT NULL COMMENT 'Identificador del siguiente Seguimiento Comercial',
  `end_date` datetime DEFAULT NULL COMMENT 'Fecha de cierre del Seguimiento Comercial',
  `offer` int DEFAULT NULL COMMENT 'Identificador del Presupuesto',
  `allDay` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Seguimiento Comercial dura todo el dia',
  `location` varchar(255) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Ubicacion del Seguimiento Comercial',
  `eventId` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_COMMERCIAL_TRACKING_SELLER` (`seller`),
  KEY `IDX_COMMERCIAL_TRACKING_ACTIVITY` (`activity`),
  KEY `IDX_COMMERCIAL_TRACKING_OFFER` (`offer`),
  KEY `IDX_COMMERCIAL_TRACKING_NEXT_COMMERCIAL_TRACKING` (`next_commercial_tracking`),
  KEY `IDX_COMMERCIAL_TRACKING_DOMAIN` (`domain`),
  KEY `IDX_COMMERCIAL_TRACKING_PROJECT_COMMERCIAL` (`project_commercial`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_ACTIVITY` FOREIGN KEY (`activity`) REFERENCES `commercial_activity` (`id`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_NEXT_COMMERCIAL_TRACKING` FOREIGN KEY (`next_commercial_tracking`) REFERENCES `commercial_tracking` (`id`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_PROJECT_COMMERCIAL` FOREIGN KEY (`project_commercial`) REFERENCES `project_commercial` (`project`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Seguimientos Comerciales';

#
# Table structure for table `commission`
#

CREATE TABLE `commission` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Comision',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio de la Comision',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de fin de la Comision',
  PRIMARY KEY (`id`),
  KEY `IDX_COMMISSION_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMMISSION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones';

#
# Table structure for table `commission_category`
#

CREATE TABLE `commission_category` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `commission` int NOT NULL COMMENT 'Identificador de la Comision',
  `category` int NOT NULL COMMENT 'Identificador de la Categoria',
  `quantity` decimal(15,4) DEFAULT '0' COMMENT 'Cantidad a partir de la cual se aplica la Comision',
  `rate` decimal(6,2) DEFAULT '0.00' COMMENT 'Porcentaje de Comision',
  PRIMARY KEY (`id`),
  KEY `IDX_COMMISSION_CATEGORY_COMMISSION` (`commission`),
  KEY `IDX_COMMISSION_CATEGORY_CATEGORY` (`category`),
  KEY `IDX_COMMISSION_CATEGORY_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMMISSION_CATEGORY_CATEGORY` FOREIGN KEY (`category`) REFERENCES `pcategory` (`id`),
  CONSTRAINT `FK_COMMISSION_CATEGORY_COMMISSION` FOREIGN KEY (`commission`) REFERENCES `commission` (`id`),
  CONSTRAINT `FK_COMMISSION_CATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones por Categoria';

#
# Table structure for table `commission_item`
#

CREATE TABLE `commission_item` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `commission` int NOT NULL COMMENT 'Identificador de la Comision',
  `item` int NOT NULL COMMENT 'Identificador del Articulo',
  `quantity` decimal(15,4) DEFAULT '0' COMMENT 'Cantidad a partir de la cual se aplica la Comision',
  `amount` decimal(15,4) DEFAULT '0' COMMENT 'Importe de la Comision',
  `rate` decimal(6,2) DEFAULT '0.00' COMMENT 'Porcentaje de Comision',
  PRIMARY KEY (`id`),
  KEY `IDX_COMMISSION_ITEM_COMMISSION` (`commission`),
  KEY `IDX_COMMISSION_ITEM_ITEM` (`item`),
  KEY `IDX_COMMISSION_ITEM_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMMISSION_ITEM_COMMISSION` FOREIGN KEY (`commission`) REFERENCES `commission` (`id`),
  CONSTRAINT `FK_COMMISSION_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_COMMISSION_ITEM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones por Articulo';

#
# Table structure for table `commission_type`
#

CREATE TABLE `commission_type` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Comision',
  `rate` decimal(6,2) DEFAULT '0.00' COMMENT 'Porcentaje de Comision',
  PRIMARY KEY (`id`),
  KEY `IDX_COMMISSION_TYPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMMISSION_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Comisiones';

#
# Table structure for table `commission_type_commission`
#

CREATE TABLE `commission_type_commission` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `commission_type` int NOT NULL COMMENT 'Identificador del Tipo de Comision',
  `commission` int NOT NULL COMMENT 'Identificador de la Comision',
  PRIMARY KEY (`id`),
  KEY `IDX_COMMISSION_TYPE_COMMISSION_COMMISSION_TYPE` (`commission_type`),
  KEY `IDX_COMMISSION_TYPE_COMMISSION_COMMISSION` (`commission`),
  KEY `IDX_COMMISSION_TYPE_COMMISSION_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMMISSION_TYPE_COMMISSION_COMMISSION` FOREIGN KEY (`commission`) REFERENCES `commission` (`id`),
  CONSTRAINT `FK_COMMISSION_TYPE_COMMISSION_COMMISSION_TYPE` FOREIGN KEY (`commission_type`) REFERENCES `commission_type` (`id`),
  CONSTRAINT `FK_COMMISSION_TYPE_COMMISSION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones por Tipo de Comision';

#
# Table structure for table `company`
#

CREATE TABLE `company` (
  `registry` int NOT NULL DEFAULT '1' COMMENT 'Registro de la Compaia',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `active` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Compañia es activa o inactiva',
  `surcharge` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Compañia tiene de recargo de equivalencia',
  `withholding` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Compañia aplica retencion de impuestos',
  `vat_accrual_payment` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Compañia esta acogida al Regimen Especial de Criterio de Caja',
  `e_invoice` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Compañia desea emitir Facturas electronicas',
  PRIMARY KEY (`registry`),
  UNIQUE KEY `IDX_UNQ_COMPANY_DOMAIN` (`domain`),
  KEY `IDX_COMPANY_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMPANY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_COMPANY_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos Corporativos';

#
# Table structure for table `contact`
#

CREATE TABLE `contact` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `user_id` int DEFAULT NULL COMMENT 'Identificador del Usuario',
  `displayName` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Mostrar Como',
  `contact_data` int DEFAULT NULL COMMENT 'Identificador de la Informacin del Contacto',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTACT_CONTACT_DATA` (`contact_data`),
  KEY `IDX_CONTACT_USER` (`user_id`),
  KEY `IDX_CONTACT_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTACT_CONTACT_DATA` FOREIGN KEY (`contact_data`) REFERENCES `contact_data` (`id`),
  CONSTRAINT `FK_CONTACT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CONTACT_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Grupo de Contactos';

#
# Table structure for table `contact_data`
#

CREATE TABLE `contact_data` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `surname` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Apellido',
  `address` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion',
  `postalCode` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo postal',
  `city` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Localidad',
  `contactState` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Estado',
  `country` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Pais',
  `phone` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono',
  `cellularPhone` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Movil',
  `fax` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Fax',
  `email` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Email',
  `note` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Nota',
  `organization` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Organizacin',
  `title` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Cargo',
  `organizationAddress` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccin de la Organizacin',
  `organizationPostalCode` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo postal de la Organizacin',
  `organizationCity` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Localidad de la Organizacin',
  `organizationState` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Estado de la Organizacin',
  `organizationPhone` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono de la Organizacin',
  `organizationFax` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Fax de la Organizacin',
  `web` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Web',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTACT_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTACT_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contactos';

#
# Table structure for table `contact_detail`
#

CREATE TABLE `contact_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `contact_group` int NOT NULL COMMENT 'Identificador del Grupo de Contactos',
  `contact` int NOT NULL COMMENT 'Identificador del Contacto',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTACT_DETAIL_CONTACT` (`contact`),
  KEY `IDX_CONTACT_DETAIL_CONTACT_GROUP` (`contact_group`),
  KEY `IDX_CONTACT_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTACT_DETAIL_CONTACT` FOREIGN KEY (`contact`) REFERENCES `contact` (`id`),
  CONSTRAINT `FK_CONTACT_DETAIL_CONTACT_GROUP` FOREIGN KEY (`contact_group`) REFERENCES `contact` (`id`),
  CONSTRAINT `FK_CONTACT_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Grupo de Contactos';

#
# Table structure for table `contract`
#

CREATE TABLE `contract` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `person` int NOT NULL COMMENT 'Identificador de la Persona',
  `workplace` int NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `enterprise_ccc` int DEFAULT NULL COMMENT 'CCC',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio del Contrato',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion del Contrato',
  `calendar` int DEFAULT NULL COMMENT 'Calendario',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `sepe_status` tinyint DEFAULT '0' COMMENT 'Estado de notificacion del contrato al SEPE',
  `registration` int DEFAULT NULL COMMENT 'Número libro de matricula',
  `seniority_date` date DEFAULT NULL COMMENT 'Fecha de antiguedad',
  `enterprise_activity` int DEFAULT NULL COMMENT 'Actividad',
  `ss_regime` tinyint NOT NULL DEFAULT '0' COMMENT 'Regimen de la Seguridad Social',
  `model` tinyint DEFAULT NULL COMMENT 'Indica el modelo de documento del contrato',
  `category_description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Categoria o grupo profesional',
  `ss_status` tinyint DEFAULT '0' COMMENT 'Estado de notificacion del contrato a la Seguridad Social',
  `agreement_level` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_PERSON` (`person`),
  KEY `IDX_CONTRACT_WORKPLACE` (`workplace`),
  KEY `IDX_CONTRACT_CALENDAR` (`calendar`),
  KEY `IDX_CONTRACT_ENTERPRISE_CCC` (`enterprise_ccc`),
  KEY `IDX_CONTRACT_ENTERPRISE_ACTIVITY` (`enterprise_activity`),
  KEY `IDX_CONTRACT_DOMAIN` (`domain`),
  KEY `FK_CONTRACT_AGREEMENT_LEVEL` (`agreement_level`),
  CONSTRAINT `FK_CONTRACT_AGREEMENT_LEVEL` FOREIGN KEY (`agreement_level`) REFERENCES `agreement_level` (`id`),
  CONSTRAINT `FK_CONTRACT_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_CONTRACT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CONTRACT_ENTERPRISE_ACTIVITY` FOREIGN KEY (`enterprise_activity`) REFERENCES `enterprise_activity` (`id`),
  CONSTRAINT `FK_CONTRACT_ENTERPRISE_CCC` FOREIGN KEY (`enterprise_ccc`) REFERENCES `enterprise_ccc` (`id`),
  CONSTRAINT `FK_CONTRACT_PERSON` FOREIGN KEY (`person`) REFERENCES `person` (`registry`),
  CONSTRAINT `FK_CONTRACT_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contratos';

#
# Table structure for table `contract_attach`
#

CREATE TABLE `contract_attach` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Archivo Adjunto del contrato',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Registro del contrato',
  `mimeType` tinyint DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Archivo Adjunto',
  `scope` int DEFAULT NULL COMMENT 'Ambito del Archivo Adjunto',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad del Archivo Adjunto',
  `attach_date` datetime DEFAULT NULL COMMENT 'Fecha del Archivo Adjunto',
  `driveId` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_ATTACH_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_ATTACH_SCOPE` (`scope`),
  KEY `IDX_CONTRACT_ATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_ATTACH_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CONTRACT_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de contratos';

#
# Table structure for table `contract_doc`
#

CREATE TABLE `contract_doc` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Documento del contrato',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Contrato',
  `mimeType` tinyint DEFAULT '0' COMMENT 'Mime Type del Documento',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Documento',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Documento (TA, IDC...)',
  `scope` int DEFAULT NULL COMMENT 'Ambito del Documento',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad del Documento',
  `attach_date` datetime DEFAULT NULL COMMENT 'Fecha del Documento',
  `s3_key` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Amazon S3 Object key',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_DOC_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_DOC_SCOPE` (`scope`),
  KEY `IDX_CONTRACT_DOC_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_DOC_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_DOC_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CONTRACT_DOC_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Documentos del contrato';

#
# Table structure for table `contract_batch`
#

CREATE TABLE `contract_batch` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la remesa de contratos',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `outcome_file_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `communication_id` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Identificador resultante de la comunicacion',
  `income_file_date` datetime DEFAULT NULL COMMENT 'Fecha de respuesta',
  `red_response_id` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Identificador de la respuesta',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Indica el estado de la remesa',
  `outcome_file` mediumblob COMMENT 'Archivo Adjunto en binario',
  `income_file` mediumblob COMMENT 'Archivo respuesta en binario',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_BATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas de contratos';

#
# Table structure for table `contract_batch_detail`
#

CREATE TABLE `contract_batch_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del detalle de la remesa',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `contract_batch` int NOT NULL COMMENT 'Identificador unico de la remesa de contratos',
  `contract` int NOT NULL COMMENT 'Identificador unico del contrato',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Estado de la linea de la remesa',
  `action_type` varchar(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Tipo de accion a realizar',
  `leave_type` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo de baja',
  `real_date` datetime DEFAULT NULL COMMENT 'Fecha real de la accion',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_BATCH_DETAIL_CONTRACT_BATCH` (`contract_batch`),
  KEY `IDX_CONTRACT_BATCH_DETAIL_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_BATCH_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_BATCH_DETAIL_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_BATCH_DETAIL_CONTRACT_BATCH` FOREIGN KEY (`contract_batch`) REFERENCES `contract_batch` (`id`),
  CONSTRAINT `FK_CONTRACT_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las remesas de contratos';

#
# Table structure for table `contract_bonus`
#

CREATE TABLE `contract_bonus` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int NOT NULL COMMENT 'Contrato',
  `description` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `expression` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Fórmula',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `bonus_concept` int DEFAULT NULL COMMENT 'Concepto de bonificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_BONUS_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_BONUS_BONUS_CONCEPT` (`bonus_concept`),
  KEY `IDX_CONTRACT_BONUS_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_BONUS_BONUS_CONCEPT` FOREIGN KEY (`bonus_concept`) REFERENCES `bonus_concept` (`id`),
  CONSTRAINT `FK_CONTRACT_BONUS_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_BONUS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bonificaciones';

#
# Table structure for table `contract_calendar_event`
#

CREATE TABLE `contract_calendar_event` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int NOT NULL COMMENT 'Identificador del Contrato',
  `date` date NOT NULL COMMENT 'Fecha de la incidencia',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de incidencia',
  `duration` decimal(15,4) DEFAULT NULL COMMENT 'Duracion de la incidencia',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_CALENDAR_EVENT_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_CALENDAR_EVENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_CALENDAR_EVENT_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_CALENDAR_EVENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Incidencias de calendario en Contratos';

#
# Table structure for table `contract_clause`
#

CREATE TABLE `contract_clause` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int DEFAULT NULL COMMENT 'Identificador del Contrato',
  `line` smallint DEFAULT '1' COMMENT 'Numero de linea de la Clausula del Contrato',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Titulo de la Clausula de Contrato',
  `description` text CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Clausula de Contrato',
  `general` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Clausula es particular o general',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_CLAUSE_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_CLAUSE_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_CLAUSE_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_CLAUSE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Clausulas de contrato';

#
# Table structure for table `contract_cost`
#

CREATE TABLE `contract_cost` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int NOT NULL COMMENT 'Contrato',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Coste',
  `code` varchar(25) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo',
  `expression` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Formula',
  `description` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_COST_DOMAIN` (`domain`),
  KEY `IDX_CONTRACT_COST_CONTRACT` (`contract`),
  CONSTRAINT `FK_CONTRACT_COST_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_COST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Costes';

#
# Table structure for table `contract_data`
#

CREATE TABLE `contract_data` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `contract` int NOT NULL COMMENT 'Contrato',
  `expression` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_DATA_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_DATA_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto del contrato';

#
# Table structure for table `contract_deduction`
#

CREATE TABLE `contract_deduction` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Deducción',
  `deduction_concept` int DEFAULT NULL COMMENT 'Identificador unico del concepto',
  `contract` int NOT NULL COMMENT 'Contrato',
  `description` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `description_decorable` tinyint NOT NULL DEFAULT '0',
  `expression` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Fórmula',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `month` tinyint DEFAULT NULL COMMENT 'Mes de la percepcion',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_DEDUCTION_DEDUCTION_CONCEPT` (`deduction_concept`),
  KEY `IDX_CONTRACT_DEDUCTION_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_DEDUCTION_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_DEDUCTION_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_DEDUCTION_DEDUCTION_CONCEPT` FOREIGN KEY (`deduction_concept`) REFERENCES `deduction_concept` (`id`),
  CONSTRAINT `FK_CONTRACT_DEDUCTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Deducciones';

#
# Table structure for table `contract_embargo`
#

CREATE TABLE `contract_embargo` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int NOT NULL COMMENT 'Contrato',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe',
  `expression` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Formula',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_EMBARGO_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_EMBARGO_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_EMBARGO_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_EMBARGO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Embargos';

#
# Table structure for table `contract_extra`
#

CREATE TABLE `contract_extra` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int NOT NULL COMMENT 'Contrato',
  `contract_payment` int DEFAULT NULL COMMENT 'Concepto',
  `start_date` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Fecha de inicio dd mm [year offset]',
  `end_date` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Fecha de finalizacion dd mm [year offset]',
  `issue_date` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Fecha de emision dd mm',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_EXTRA_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_EXTRA_CONTRACT_PAYMENT` (`contract_payment`),
  KEY `IDX_CONTRACT_EXTRA_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_EXTRA_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_EXTRA_CONTRACT_PAYMENT` FOREIGN KEY (`contract_payment`) REFERENCES `contract_payment` (`id`),
  CONSTRAINT `FK_CONTRACT_EXTRA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Costes';

#
# Table structure for table `contract_info`
#

CREATE TABLE `contract_info` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int DEFAULT NULL COMMENT 'Identificador del Contrato',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `expression` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_INFO_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_INFO_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_INFO_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_INFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Informacion temporal del Contrato';

#
# Table structure for table `contract_leave`
#

CREATE TABLE `contract_leave` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Baja',
  `contract` int NOT NULL COMMENT 'Contrato',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `daily_cgc_base` decimal(15,3) DEFAULT NULL COMMENT 'Base de cotizacion por contingencias comunes',
  `daily_cgp_base` decimal(15,3) DEFAULT NULL COMMENT 'Base de cotizacion por contingencias profesionales',
  `parent` int DEFAULT NULL COMMENT 'Baja origen, si es recaida',
  `daily_reg_base` decimal(15,3) DEFAULT NULL COMMENT 'Base reguladora',
  `discharge_cause` tinyint DEFAULT NULL COMMENT 'Causa del alta',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_LEAVE_CONTRACT_LEAVE` (`parent`),
  KEY `IDX_CONTRACT_LEAVE_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_LEAVE_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_LEAVE_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_LEAVE_CONTRACT_LEAVE` FOREIGN KEY (`parent`) REFERENCES `contract_leave` (`id`),
  CONSTRAINT `FK_CONTRACT_LEAVE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bajas';

#
# Table structure for table `contract_leave_detail`
#

CREATE TABLE `contract_leave_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT 'Tipo de parte',
  `contract_leave` int NOT NULL COMMENT 'Contrato',
  `college_number` varchar(8) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de colegiado',
  `confirm_order` tinyint DEFAULT NULL COMMENT 'Numero de orden del parte de confirmacion',
  `cias` varchar(11) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'codigo identificacion area sanitaria',
  `date` date NOT NULL COMMENT 'Fecha del parte',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Indica el estado',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE` (`contract_leave`),
  KEY `IDX_CONTRACT_LEAVE_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE` FOREIGN KEY (`contract_leave`) REFERENCES `contract_leave` (`id`),
  CONSTRAINT `FK_CONTRACT_LEAVE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las bajas';

#
# Table structure for table `contract_payment`
#

CREATE TABLE `contract_payment` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Percepción Salarial',
  `contract` int NOT NULL COMMENT 'Contrato',
  `payment_concept` int DEFAULT NULL COMMENT 'Identificador unico del concepto',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `description_decorable` tinyint NOT NULL DEFAULT '0',
  `expression` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe',
  `irpf_expression` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe tributable',
  `quote_expression` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe cotizable',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `month` tinyint DEFAULT NULL COMMENT 'Mes de la percepcion',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `salary_type` tinyint DEFAULT NULL COMMENT 'Tipo de Nomina/Recibo',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_PAYMENT_PAYMENT_CONCEPT` (`payment_concept`),
  KEY `IDX_CONTRACT_PAYMENT_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_PAYMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_PAYMENT_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_PAYMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CONTRACT_PAYMENT_PAYMENT_CONCEPT` FOREIGN KEY (`payment_concept`) REFERENCES `payment_concept` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Percepciones Salariales';

#
# Table structure for table `contrata_batch`
#

CREATE TABLE `contrata_batch` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `date` datetime DEFAULT NULL COMMENT 'Fecha de la Remesa',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Indica el estado de la Remesa',
  `type` tinyint DEFAULT '0' COMMENT 'Tipo de remesa a comunicar al SEPE',
  `communication_id` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Identificador resultante de la comunicacion',
  `outcome_file` mediumblob COMMENT 'Archivo Adjunto en binario',
  `outcome_file_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `income_file` mediumblob COMMENT 'Archivo respuesta en binario',
  `income_file_date` datetime DEFAULT NULL COMMENT 'Fecha de respuesta',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRATA_BATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRATA_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas de altas de Contratos';

#
# Table structure for table `contrata_batch_detail`
#

CREATE TABLE `contrata_batch_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `contrata_batch` int NOT NULL COMMENT 'Identificador de la Remesa',
  `contract` int NOT NULL COMMENT 'Identificador del Contrato',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Estado de la linea de la remesa',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRATA_BATCH_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_CONTRATA_BATCH_DETAIL_CONTRACT` (`contract`),
  KEY `IDX_CONTRATA_BATCH_DETAIL_CONTRATA_BATCH` (`contrata_batch`),
  CONSTRAINT `FK_CONTRATA_BATCH_DETAIL_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRATA_BATCH_DETAIL_CONTRATA_BATCH` FOREIGN KEY (`contrata_batch`) REFERENCES `contrata_batch` (`id`),
  CONSTRAINT `FK_CONTRATA_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de Remesas de altas de Contratos';

#
# Table structure for table `cost_profile`
#

CREATE TABLE `cost_profile` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion',
  `cost` decimal(15,4) NOT NULL DEFAULT '0' COMMENT 'Costo por hora',
  PRIMARY KEY (`id`),
  KEY `IDX_COST_PROFILE_DOMAIN` (`domain`),
  CONSTRAINT `FK_COST_PROFILE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Perfiles de Costos para Usuarios';

#
# Table structure for table `course`
#

CREATE TABLE `course` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Curso',
  `domain` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `code` varchar(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias del Curso',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Curso',
  `start_date` date NOT NULL COMMENT 'Fecha inicio del Curso',
  `end_date` date NOT NULL COMMENT 'Fecha fin del Curso',
  `academic_year` int NOT NULL COMMENT 'Año Academico del Curso',
  `subject` int NOT NULL COMMENT 'Materia del Curso',
  `level` int NOT NULL COMMENT 'Nivel del Curso',
  `workplace` int NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `alumn_limit` smallint DEFAULT NULL COMMENT 'Limite de Alumnos del Curso',
  `status` tinyint DEFAULT NULL COMMENT 'Estado del Curso',
  `comments` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Comentarios sobre el Curso',
  PRIMARY KEY (`id`),
  KEY `IDX_COURSE_ACADEMIC_YEAR` (`academic_year`),
  KEY `IDX_COURSE_LEVEL` (`level`),
  KEY `IDX_COURSE_SUBJECT` (`subject`),
  KEY `IDX_COURSE_WORKPLACE` (`workplace`),
  KEY `IDX_COURSE_DOMAIN` (`domain`),
  CONSTRAINT `FK_COURSE_ACADEMIC_YEAR` FOREIGN KEY (`academic_year`) REFERENCES `academic_year` (`id`),
  CONSTRAINT `FK_COURSE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_COURSE_LEVEL` FOREIGN KEY (`level`) REFERENCES `course_level` (`id`),
  CONSTRAINT `FK_COURSE_SUBJECT` FOREIGN KEY (`subject`) REFERENCES `course_subject` (`id`),
  CONSTRAINT `FK_COURSE_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cursos';

#
# Table structure for table `course_academicskill`
#

CREATE TABLE `course_academicskill` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador Unico',
  `domain` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `course` int NOT NULL COMMENT 'Curso',
  `academic_skill` int NOT NULL COMMENT 'Aptitud Academica',
  `weight` int NOT NULL DEFAULT '1' COMMENT 'Peso de la Aptitud para calcular la Nota media',
  PRIMARY KEY (`id`),
  KEY `IDX_COURSE_ACADEMIC_SKILL_COURSE` (`course`),
  KEY `IDX_COURSE_ACADEMIC_SKILL_ACADEMIC_SKILL` (`academic_skill`),
  KEY `IDX_COURSE_ACADEMIC_SKILL_DOMAIN` (`domain`),
  CONSTRAINT `FK_COURSE_ACADEMIC_SKILL_ACADEMIC_SKILL` FOREIGN KEY (`academic_skill`) REFERENCES `academic_skill` (`id`),
  CONSTRAINT `FK_COURSE_ACADEMIC_SKILL_COURSE` FOREIGN KEY (`course`) REFERENCES `course` (`id`),
  CONSTRAINT `FK_COURSE_ACADEMIC_SKILL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Aptitudes Academicas por Curso';

#
# Table structure for table `course_alumn`
#

CREATE TABLE `course_alumn` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `course` int NOT NULL COMMENT 'Identificador del Curso',
  `customer` int NOT NULL COMMENT 'Identificador del Alumno',
  `status` tinyint DEFAULT NULL COMMENT 'Estado del alumno en el curso',
  PRIMARY KEY (`id`),
  KEY `IDX_COURSE_ALUMN_COURSE` (`course`),
  KEY `IDX_COURSE_ALUMN_CUSTOMER` (`customer`),
  KEY `IDX_COURSE_ALUMN_DOMAIN` (`domain`),
  CONSTRAINT `FK_COURSE_ALUMN_COURSE` FOREIGN KEY (`course`) REFERENCES `course` (`id`),
  CONSTRAINT `FK_COURSE_ALUMN_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_COURSE_ALUMN_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Alumnos por Curso';

#
# Table structure for table `course_evaluation`
#

CREATE TABLE `course_evaluation` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `course` int NOT NULL COMMENT 'Identificador de Curso',
  `quality_skill` int NOT NULL COMMENT 'Identificador de Aptitudes Calidad',
  `evaluation` decimal(15,3) DEFAULT '0.000' COMMENT 'Evaluaciones',
  `quantity` int DEFAULT '0' COMMENT 'Cantidad',
  PRIMARY KEY (`id`),
  KEY `IDX_COURSE_EVALUATION_COURSE` (`course`),
  KEY `IDX_COURSE_EVALUATION_QUALITY_SKILL` (`quality_skill`),
  KEY `IDX_COURSE_EVALUATION_DOMAIN` (`domain`),
  CONSTRAINT `FK_COURSE_EVALUATION_COURSE` FOREIGN KEY (`course`) REFERENCES `course` (`id`),
  CONSTRAINT `FK_COURSE_EVALUATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_COURSE_EVALUATION_QUALITY_SKILL` FOREIGN KEY (`quality_skill`) REFERENCES `quality_skill` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Evaluaciones por Curso';

#
# Table structure for table `course_instructor`
#

CREATE TABLE `course_instructor` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `course` int NOT NULL COMMENT 'Identificador del Curso',
  `task_holder` int NOT NULL COMMENT 'Identificador del Profesor',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Profesor',
  PRIMARY KEY (`id`),
  KEY `IDX_COURSE_INSTRUCTOR_COURSE` (`course`),
  KEY `IDX_COURSE_INSTRUCTOR_DOMAIN` (`domain`),
  KEY `IDX_COURSE_INSTRUCTOR_TASK_HOLDER` (`task_holder`),
  CONSTRAINT `FK_COURSE_INSTRUCTOR_COURSE` FOREIGN KEY (`course`) REFERENCES `course` (`id`),
  CONSTRAINT `FK_COURSE_INSTRUCTOR_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_COURSE_INSTRUCTOR_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Profesores por Curso';

#
# Table structure for table `course_level`
#

CREATE TABLE `course_level` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Nivel',
  `domain` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Nivel',
  PRIMARY KEY (`id`),
  KEY `IDX_COURSE_LEVEL_DOMAIN` (`domain`),
  CONSTRAINT `FK_COURSE_LEVEL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Niveles de Cursos';

#
# Table structure for table `course_observation`
#

CREATE TABLE `course_observation` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `course` int NOT NULL COMMENT 'Identificador de Curso',
  `observation` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Observaciones',
  PRIMARY KEY (`id`),
  KEY `IDX_COURSE_OBSERVATION_COURSE` (`course`),
  KEY `IDX_COURSE_OBSERVATION_DOMAIN` (`domain`),
  CONSTRAINT `FK_COURSE_OBSERVATION_COURSE` FOREIGN KEY (`course`) REFERENCES `course` (`id`),
  CONSTRAINT `FK_COURSE_OBSERVATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Observaciones por Curso';

#
# Table structure for table `course_schedule`
#

CREATE TABLE `course_schedule` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Horario',
  `domain` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `course` int NOT NULL COMMENT 'Identificador del Curso',
  `day_of_week` tinyint NOT NULL COMMENT 'Dia de la semana',
  `start_time` time NOT NULL COMMENT 'Hora de comienzo',
  `end_time` time NOT NULL COMMENT 'Hora de fin',
  PRIMARY KEY (`id`),
  KEY `IDX_COURSE_SCHEDULE_COURSE` (`course`),
  KEY `IDX_COURSE_SCHEDULE_DOMAIN` (`domain`),
  CONSTRAINT `FK_COURSE_SCHEDULE_COURSE` FOREIGN KEY (`course`) REFERENCES `course` (`id`),
  CONSTRAINT `FK_COURSE_SCHEDULE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Horarios de Cursos';

#
# Table structure for table `course_subject`
#

CREATE TABLE `course_subject` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Materia',
  `domain` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Materia',
  PRIMARY KEY (`id`),
  KEY `IDX_COURSE_SUBJECT_DOMAIN` (`domain`),
  CONSTRAINT `FK_COURSE_SUBJECT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Materias de Cursos';

#
# Table structure for table `cra_batch`
#

CREATE TABLE `cra_batch` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Indica el estado de la Remesa',
  `communication_id` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Identificador resultante de la comunicacion',
  `income_file` mediumblob COMMENT 'Archivo respuesta en binario',
  `income_file_date` datetime DEFAULT NULL COMMENT 'Fecha de respuesta',
  `outcome_file` mediumblob COMMENT 'Archivo Adjunto en binario',
  `outcome_file_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  PRIMARY KEY (`id`),
  KEY `IDX_CRA_BATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_CRA_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas del fichero cra';

#
# Table structure for table `cra_batch_detail`
#

CREATE TABLE `cra_batch_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `cra_batch` int NOT NULL COMMENT 'Identificador unico de la Remesa',
  `enterprise_ccc` int NOT NULL COMMENT 'Identificador unico del ccc',
  PRIMARY KEY (`id`),
  KEY `IDX_CRA_BATCH_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_CRA_BATCH_DETAIL_CRA_BATCH` (`cra_batch`),
  KEY `IDX_CRA_BATCH_DETAIL_ENTERPRISE_CCC` (`enterprise_ccc`),
  CONSTRAINT `FK_CRA_BATCH_DETAIL_CRA_BATCH` FOREIGN KEY (`cra_batch`) REFERENCES `cra_batch` (`id`),
  CONSTRAINT `FK_CRA_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CRA_BATCH_DETAIL_ENTERPRISE_CCC` FOREIGN KEY (`enterprise_ccc`) REFERENCES `enterprise_ccc` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las Remesas del fichero cra';

#
# Table structure for table `creditor`
#

CREATE TABLE `creditor` (
  `registry` int NOT NULL DEFAULT '0' COMMENT 'Registro del Acreedor',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `withholding` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Acreedor aplica retencion de impuestos',
  `vat_accrual_payment` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Acreedor esta acogido al Regimen Especial de Criterio de Caja',
  `transaction` tinyint DEFAULT '0' COMMENT 'Tipo de transacciones del Acreedor',
  `status` tinyint DEFAULT NULL COMMENT 'Estado del Acreedor',
  `scope` int NOT NULL COMMENT 'Identificador del Ambito',
  `account` int DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`registry`),
  KEY `IDX_CREDITOR_SCOPE` (`scope`),
  KEY `IDX_CREDITOR_DOMAIN` (`domain`),
  KEY `IDX_CREDITOR_ACCOUNT` (`account`),
  CONSTRAINT `FK_CREDITOR_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_CREDITOR_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CREDITOR_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_CREDITOR_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Acreedores';

#
# Table structure for table `customer`
#

CREATE TABLE `customer` (
  `registry` int NOT NULL DEFAULT '0' COMMENT 'Registro del Cliente',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `tariff` int DEFAULT NULL COMMENT 'Tarifa asociada al Cliente',
  `surcharge` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Cliente tiene recargo de equivalencia',
  `withholding` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Cliente aplica retencion de impuestos',
  `transaction` tinyint DEFAULT '0' COMMENT 'Tipo de transacciones del Cliente',
  `status` tinyint DEFAULT NULL COMMENT 'Estado del Cliente',
  `scope` int NOT NULL COMMENT 'Identificador del Ambito',
  `e_invoice` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Cliente desea recibir Facturas electronicas',
  `invoicing_group` int DEFAULT NULL COMMENT 'Identificador de Grupo de Facturacion',
  `project_grouped` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Cliente desea agrupar Proyectos en una sola Factura',
  `delivery_grouped` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Cliente desea agrupar Albaranes en una sola Factura',
  `delivery_valuated` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Cliente desea imprimir el Albaran valorado',
  `account` int DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`registry`),
  KEY `IDX_CUSTOMER_TARIFF` (`tariff`),
  KEY `IDX_CUSTOMER_SCOPE` (`scope`),
  KEY `IDX_CUSTOMER_DOMAIN` (`domain`),
  KEY `IDX_CUSTOMER_ACCOUNT` (`account`),
  KEY `IDX_CUSTOMER_INVOICING_GROUP` (`invoicing_group`),
  CONSTRAINT `FK_CUSTOMER_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_CUSTOMER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CUSTOMER_INVOICING_GROUP` FOREIGN KEY (`invoicing_group`) REFERENCES `invoicing_group` (`id`),
  CONSTRAINT `FK_CUSTOMER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_CUSTOMER_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_CUSTOMER_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Clientes';

#
# Table structure for table `customer_fee`
#

CREATE TABLE `customer_fee` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Cuota del Cliente',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `project` int DEFAULT NULL COMMENT 'Identificador del Proyecto',
  `customer` int DEFAULT NULL COMMENT 'Identificador del Cliente',
  `line` smallint DEFAULT '1' COMMENT 'Numero de linea de Cuota',
  `item` int NOT NULL COMMENT 'Identificador del Articulo',
  `description` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de la Cuota',
  `quantity` decimal(15,3) DEFAULT '0.000' COMMENT 'Cantidad de la Cuota',
  `price` decimal(15,4) DEFAULT '0' COMMENT 'Precio de la Cuota',
  `discount_expr` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descuentos de la Cuota',
  `initial_date` date DEFAULT NULL COMMENT 'Fecha de inicio de la Cuota',
  `final_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion de la Cuota',
  `billing_date` date DEFAULT NULL COMMENT 'Proxima fecha de facturación de la Cuota',
  `period` smallint DEFAULT '1' COMMENT 'Periodo de facturacion en meses de la Cuota',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad de la Cuota',
  `invoicing_group` int DEFAULT NULL COMMENT 'Identificador de Grupo de Facturacion',
  `seller` int DEFAULT NULL COMMENT 'Identificador de Agente Comercial',
  `workplace` int NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  PRIMARY KEY (`id`),
  KEY `IDX_CUSTOMER_FEE_CUSTOMER` (`customer`),
  KEY `IDX_CUSTOMER_FEE_ITEM` (`item`),
  KEY `IDX_CUSTOMER_FEE_WORKPLACE` (`workplace`),
  KEY `IDX_CUSTOMER_FEE_DOMAIN` (`domain`),
  KEY `IDX_CUSTOMER_FEE_INVOICING_GROUP` (`invoicing_group`),
  KEY `IDX_CUSTOMER_FEE_PROJECT` (`project`),
  KEY `IDX_CUSTOMER_FEE_SELLER` (`seller`),
  CONSTRAINT `FK_CUSTOMER_FEE_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_CUSTOMER_FEE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CUSTOMER_FEE_INVOICING_GROUP` FOREIGN KEY (`invoicing_group`) REFERENCES `invoicing_group` (`id`),
  CONSTRAINT `FK_CUSTOMER_FEE_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_CUSTOMER_FEE_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_CUSTOMER_FEE_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`),
  CONSTRAINT `FK_CUSTOMER_FEE_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuotas de Clientes';

#
# Table structure for table `daily_tracking`
#

CREATE TABLE `daily_tracking` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Parte',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `task_holder` int NOT NULL COMMENT 'Identificador del Usuario que realiza el Parte',
  `tracking_date` date NOT NULL COMMENT 'Fecha del Parte',
  `tracking_duration` double NOT NULL DEFAULT '0' COMMENT 'Tiempo invertido en el Parte',
  `job_type` int NOT NULL COMMENT 'Tipo de Trabajo realizado en el Parte',
  `registry` int DEFAULT NULL COMMENT 'Identificador del Cliente asociado al Parte',
  `project` int DEFAULT NULL COMMENT 'Identificador del Expediente asociado al Parte',
  `activity_type` int DEFAULT NULL COMMENT 'Identificador de la Actividad asociada al Parte',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios del Parte',
  `task` int DEFAULT NULL COMMENT 'Identificador de la Tarea que provoca el Parte',
  `cost` decimal(15,4) DEFAULT '0' COMMENT 'Costo',
  PRIMARY KEY (`id`),
  KEY `IDX_DAILY_TRACKING_ACTIVITY_TYPE` (`activity_type`),
  KEY `IDX_DAILY_TRACKING_JOB_TYPE` (`job_type`),
  KEY `IDX_DAILY_TRACKING_PROJECT` (`project`),
  KEY `IDX_DAILY_TRACKING_REGISTRY` (`registry`),
  KEY `IDX_DAILY_TRACKING_TASK` (`task`),
  KEY `IDX_DAILY_TRACKING_TASK_HOLDER` (`task_holder`),
  KEY `IDX_DAILY_TRACKING_DOMAIN` (`domain`),
  CONSTRAINT `FK_DAILY_TRACKING_ACTIVITY_TYPE` FOREIGN KEY (`activity_type`) REFERENCES `activity_type` (`id`),
  CONSTRAINT `FK_DAILY_TRACKING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_DAILY_TRACKING_JOB_TYPE` FOREIGN KEY (`job_type`) REFERENCES `job_type` (`id`),
  CONSTRAINT `FK_DAILY_TRACKING_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_DAILY_TRACKING_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_DAILY_TRACKING_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`),
  CONSTRAINT `FK_DAILY_TRACKING_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Parte Diario de Trabajo';

#
# Table structure for table `data_attach`
#

CREATE TABLE `data_attach` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `source` tinyint NOT NULL COMMENT 'Origen',
  `source_id` int DEFAULT NULL COMMENT 'Identificador del origen',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Archivo Adjunto',
  `mimeType` tinyint DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
  `drive_id` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Identificador de Google Drive',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_DATA_ATTACH_DOMAIN` (`domain`),
  KEY `IDX_DATA_ATTACH_SOURCE` (`source_id`,`source`),
  CONSTRAINT `FK_DATA_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='DATA ATTACH';

#
# Table structure for table `data_request`
#

CREATE TABLE `data_request` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',
  `domain` int NOT NULL DEFAULT '0' COMMENT 'Dominio',
  `date` datetime DEFAULT NULL COMMENT 'Fecha',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT 'Tipo',
  `black_box` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Información necesaria para replicar la petición',
  `md5` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Hash md5',
  PRIMARY KEY (`id`),
  KEY `IDX_DATA_REQUEST_DOMAIN` (`domain`),
  CONSTRAINT `FK_DATA_REQUEST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Data Request';

#
# Table structure for table `data_response`
#

CREATE TABLE `data_response` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `code` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo de referencia',
  `response_date` date DEFAULT NULL COMMENT 'Fecha',
  `source` tinyint NOT NULL COMMENT 'Origen',
  `source_id` int DEFAULT NULL COMMENT 'Identificador del Origen',
  `data_request` int DEFAULT NULL COMMENT 'identificador de data_request',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_DATA_RESPONSE_DOMAIN` (`domain`),
  KEY `IDX_DATA_RESPONSE_DATA_REQUEST` (`data_request`),
  CONSTRAINT `FK_DATA_RESPONSE_DATA_REQUEST` FOREIGN KEY (`data_request`) REFERENCES `data_request` (`id`),
  CONSTRAINT `FK_DATA_RESPONSE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='DATA RESPONSE';

#
# Table structure for table `data_response_detail`
#

CREATE TABLE `data_response_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `data_response` int NOT NULL DEFAULT '0' COMMENT 'Identificador de data_response',
  `data_variable` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo de la variable',
  `data_value` text CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Valor de la variable',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_DATA_RESPONSE_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_DATA_RESPONSE_DETAIL_DATA_RESPONSE` (`data_response`),
  CONSTRAINT `FK_DATA_RESPONSE_DETAIL_DATA_RESPONSE` FOREIGN KEY (`data_response`) REFERENCES `data_response` (`id`),
  CONSTRAINT `FK_DATA_RESPONSE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='DATA RESPONSE DETAIL';

#
# Table structure for table `db_version`
#

CREATE TABLE `db_version` (
  `version_number` varchar(10) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Numero de Version de la Base de Datos',
  PRIMARY KEY (`version_number`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Version de la Base de Datos';

#
# Table structure for table `deduction_concept`
#

CREATE TABLE `deduction_concept` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `code` varchar(25) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo',
  `description` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Deduccion Salarial',
  `description_decorable` tinyint DEFAULT '0',
  `expression` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe',
  PRIMARY KEY (`id`),
  KEY `IDX_DEDUCTION_CONCEPT_DOMAIN` (`domain`),
  CONSTRAINT `FK_DEDUCTION_CONCEPT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos de deducciones';

#
# Table structure for table `delivery`
#

CREATE TABLE `delivery` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Albaran de Venta',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `project` int DEFAULT NULL COMMENT 'Identificador del Proyecto',
  `series` char(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie del Albaran',
  `number` int NOT NULL DEFAULT '0' COMMENT 'Numero del Albaran',
  `customer` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Cliente',
  `address` int DEFAULT NULL COMMENT 'Identificador de la Direccion de envio del Albaran',
  `issue_time` datetime DEFAULT NULL COMMENT 'Fecha de emision del Albaran',
  `pay_method` int DEFAULT NULL COMMENT 'Identificador de la Forma de Pago',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad del Albaran',
  `status` tinyint DEFAULT '0' COMMENT 'Estado del Albaran',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios del Albaran',
  `remarks` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Observaciones del Albaran',
  `workplace` int DEFAULT NULL COMMENT 'Identificador del Centro de Trabajo',
  `scope` int NOT NULL DEFAULT '1' COMMENT 'Ambito del Albaran',
  `number_of_pymnts` smallint DEFAULT '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint DEFAULT '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint DEFAULT '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Dias de pago',
  `bank_account` varchar(34) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'IBAN - Numero de Cuenta Bancaria Internacional',
  `bank_alias` varchar(25) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias del Banco',
  `bic` varchar(11) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'BIC - Codigo Identificador del Banco',
  `carrier` int DEFAULT NULL COMMENT 'Identificador de la agencia de transporte',
  `carrier_packing` int DEFAULT NULL COMMENT 'Identificador de la Hoja de ruta',
  `number_plate` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de matricula',
  `driver` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del conductor',
  `driver_document` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Documento del conductor',
  `total_packages` decimal(15,3) DEFAULT '0.000' COMMENT 'Numero total de bultos',
  `total_weight` decimal(15,3) DEFAULT '0.000' COMMENT 'Peso total',
  `shipping_alternative_address` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Primera parte de la Direccion de entrega',
  `shipping_alternative_address2` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Segunda parte de la Direccion de entrega',
  `shipping_alternative_zip` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo Postal de entrega',
  `shipping_alternative_city` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Localidad de entrega',
  `shipping_alternative_phone` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono de contacto de la entrega',
  `shipping_alternative_recipient` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Destinatario de la entrega',
  `shipping_contact` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del contacto para la entrega',
  `shipping_period` tinyint DEFAULT '0' COMMENT 'Tipo de periodo de entrega',
  `tracking_number` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de expedicion',
  `shipping_status` tinyint DEFAULT '0' COMMENT 'Estado de la entrega',
  `status_modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion del estado',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_DELIVERY_DOMAIN_SERIES_NUMBER` (`domain`,`series`,`number`),
  KEY `IDX_DELIVERY_WORKPLACE` (`workplace`),
  KEY `IDX_DELIVERY_SCOPE` (`scope`),
  KEY `IDX_DELIVERY_PROJECT` (`project`),
  KEY `IDX_DELIVERY_ISSUE_TIME` (`issue_time`),
  KEY `IDX_DELIVERY_CUSTOMER` (`customer`),
  KEY `IDX_DELIVERY_RADDRESS` (`address`),
  KEY `IDX_DELIVERY_PAY_METHOD` (`pay_method`),
  KEY `IDX_DELIVERY_DOMAIN` (`domain`),
  KEY `IDX_DELIVERY_CARRIER` (`carrier`),
  CONSTRAINT `FK_DELIVERY_CARRIER` FOREIGN KEY (`carrier`) REFERENCES `carrier` (`registry`),
  CONSTRAINT `FK_DELIVERY_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_DELIVERY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_DELIVERY_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_DELIVERY_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_DELIVERY_RADDRESS` FOREIGN KEY (`address`) REFERENCES `raddress` (`id`),
  CONSTRAINT `FK_DELIVERY_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_DELIVERY_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Albaranes de Venta';

#
# Table structure for table `delivery_detail`
#

CREATE TABLE `delivery_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Detalle del Albaran de Venta',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `delivery` int NOT NULL COMMENT 'Identificador del Albaran de Venta',
  `line` smallint DEFAULT '1' COMMENT 'Numero de linea del Detalle dentro del Albaran',
  `item` int NOT NULL COMMENT 'Identificador del Articulo del Detalle de Albaran',
  `description` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Detalle de Albaran',
  `warehouse` int NOT NULL COMMENT 'Identificador del Almacen',
  `quantity` decimal(15,3) DEFAULT NULL COMMENT 'Cantidad del Detalle de Albaran',
  `price` decimal(15,4) DEFAULT '0' COMMENT 'Precio del Detalle de Albaran',
  `discount_expr` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descuentos del Detalle de Albaran',
  `sales_detail` int DEFAULT NULL COMMENT 'Identificador del Detalle del Pedido de Venta asociado',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_DELIVERY_DETAIL_DELIVERY` (`delivery`),
  KEY `IDX_DELIVERY_DETAIL_WAREHOUSE` (`warehouse`),
  KEY `IDX_DELIVERY_DETAIL_ITEM` (`item`),
  KEY `IDX_DELIVERY_DETAIL_SALES_DETAIL` (`sales_detail`),
  KEY `IDX_DELIVERY_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_DELIVERY_DETAIL_DELIVERY` FOREIGN KEY (`delivery`) REFERENCES `delivery` (`id`),
  CONSTRAINT `FK_DELIVERY_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_DELIVERY_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_DELIVERY_DETAIL_SALES_DETAIL` FOREIGN KEY (`sales_detail`) REFERENCES `sales_detail` (`id`),
  CONSTRAINT `FK_DELIVERY_DETAIL_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Albaran de Venta';


#
# Structure for the `delivery_packaging` table :
#

CREATE TABLE `delivery_packaging` (
	`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',
	`domain` int(4) NOT NULL COMMENT 'Dominio',
	`delivery` int(4) NOT NULL COMMENT 'Identificador del albaran',
	`item` int(4) NOT NULL COMMENT 'Articulo del Envasado',
    `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
    `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
    `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
    `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
	PRIMARY KEY (`id`),
	KEY `IDX_DELIVERY_PACKAGING_DOMAIN` (`domain`),
	KEY `IDX_DELIVERY_PACKAGING_DELIVERY` (`delivery`),
	KEY `IDX_DELIVERY_PACKAGING_ITEM` (`item`),
	CONSTRAINT `FK_DELIVERY_PACKAGING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
	CONSTRAINT `FK_DELIVERY_PACKAGING_DELIVERY` FOREIGN KEY (`delivery`) REFERENCES `delivery` (`id`),
	CONSTRAINT `FK_DELIVERY_PACKAGING_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Envasado del albaran';

#
# Table structure for table `delivery_info`
#
CREATE TABLE `delivery_info` (
	`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
	`domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
	`delivery` int(4) NOT NULL COMMENT 'Identificador del Albaran de Venta',
	`type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de Comunicacion',
	`status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Estado de la Comunicacion',
	PRIMARY KEY (`id`),
	KEY `IDX_DELIVERY_INFO_DOMAIN` (`domain`),
	KEY `IDX_DELIVERY_INFO_DELIVERY` (`delivery`),
	CONSTRAINT `FK_DELIVERY_INFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
	CONSTRAINT `FK_DELIVERY_INFO_DELIVERY` FOREIGN KEY (`delivery`) REFERENCES `delivery` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Estado Comunicaciones de Albaranes de Venta';


#
# Table structure for table `department`
#

CREATE TABLE `department` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del Departamento',
  PRIMARY KEY (`id`),
  KEY `IDX_DEPARTMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_DEPARTMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Departamentos';

#
# Table structure for table `domain`
#

CREATE TABLE `domain` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `name` varchar(253) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Dominio',
  `description` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Dominio',
  `parent` int DEFAULT NULL COMMENT 'Identificador del Dominio padre',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT 'Tipo de Dominio',
  `scope` int DEFAULT NULL COMMENT 'Identificador del Ambito',
  `subDomainSuffix` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Sufijo de los Dominio Hijo',
  `enableHeredity` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el Dominio tiene deshabilitado la herencia de registros o no',
  `domainManagement` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el Dominio tiene capacidad de MultiDominio o no',
  `disableDomainManagement` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el Dominio tiene deshabilitado el mantenimiento de Dominios o no',
  `maxDocumentSize` int DEFAULT NULL COMMENT 'Tamao Maximo de los Documentos',
  `maxTotalDocumentSize` int DEFAULT NULL COMMENT 'Almacenamiento Documental Contratado',
  `maxDefinedUsers` int DEFAULT NULL COMMENT 'Numero Maximo de Usuarios',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si el Dominio esta activo o no',
  `owner` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Emails del creador del Dominio',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  `expirationDate` date DEFAULT NULL COMMENT 'Fecha de Expiracion del Dominio',
  `lastAccess_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de ultimo acceso',
  `lastAccess_date` datetime DEFAULT NULL COMMENT 'Fecha de ultimo acceso',
  `aonStatus` tinyint NOT NULL DEFAULT '0' COMMENT 'Estado del customer en Aon',
  `aonCustomer` int DEFAULT NULL COMMENT 'Referencia al customer en Aon',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_DOMAIN_NAME` (`name`),
  KEY `IDX_DOMAIN_PARENT` (`parent`),
  KEY `IDX_DOMAIN_SCOPE` (`scope`),
  CONSTRAINT `FK_DOMAIN_PARENT` FOREIGN KEY (`parent`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_DOMAIN_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Dominios';

#
# Table structure for table `domain_app`
#

CREATE TABLE `domain_app` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',
  `domain` int NOT NULL DEFAULT '0' COMMENT 'Dominio',
  `app` tinyint NOT NULL DEFAULT '0' COMMENT 'App',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si la Aplicacion del Dominio esta activa o no',
  PRIMARY KEY (`id`),
  KEY `IDX_DOMAIN_APP_DOMAIN` (`domain`),
  CONSTRAINT `FK_DOMAIN_APP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Aplicacion web';

#
# Table structure for table `domain_application`
#

CREATE TABLE `domain_application` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `application` int NOT NULL COMMENT 'Identificador de la Aplicacion',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si la Aplicacion del Dominio esta activa o no',
  `audit_level` tinyint NOT NULL DEFAULT '0' COMMENT 'Nivel de auditoria',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_DOMAIN_APPLICATION` (`domain`,`application`),
  KEY `IDX_APPLICATION_DOMAIN` (`domain`),
  KEY `IDX_DOMAIN_APPLICATION_APPLICATION` (`application`),
  CONSTRAINT `FK_APPLICATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_DOMAIN_APPLICATION_APPLICATION` FOREIGN KEY (`application`) REFERENCES `application` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Aplicacion web';

#
# Table structure for table `domain_application_module`
#

CREATE TABLE `domain_application_module` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `domain_application` int NOT NULL COMMENT 'Identificador de la Aplicacion del Dominio',
  `module` tinyint NOT NULL COMMENT 'Modulo de la Aplicacion del Dominio',
  PRIMARY KEY (`id`),
  KEY `IDX_DOMAIN_APPLICATION_MODULE_DOMAIN_APPLICATION` (`domain_application`),
  KEY `IDX_DOMAIN_APPLICATION_MODULE_DOMAIN` (`domain`),
  CONSTRAINT `FK_DOMAIN_APPLICATION_MODULE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_DOMAIN_APPLICATION_MODULE_DOMAIN_APPLICATION` FOREIGN KEY (`domain_application`) REFERENCES `domain_application` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Modulos de las Aplicaciones del Dominio';

#
# Table structure for table `domain_gserviceaccount`
#

CREATE TABLE `domain_gserviceaccount` (
  `client_id` varchar(100) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL,
  `email_address` varchar(100) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `public_key` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `private_key` mediumblob,
  `client_secret` mediumblob,
  `domain` int DEFAULT NULL,
  `size` decimal(15,4) DEFAULT '0',
  `limit` decimal(15,4) DEFAULT '0',
  `google_account` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`client_id`),
  KEY `FK_DOMAIN_GSERVICEACCOUNT_DOMAIN_IDX` (`domain`),
  CONSTRAINT `FK_DOMAIN_GSERVICEACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci;

#
# Table structure for table `elaboration`
#

CREATE TABLE `elaboration` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la elaboracion',
  `domain` int NOT NULL COMMENT 'Identificador del dominio',
  `series` char(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie de la elaboracion',
  `number` int NOT NULL DEFAULT '0' COMMENT 'Numero de la elaboracion',
  `date` datetime DEFAULT NULL COMMENT 'Fecha de elaboracion',
  `item` int NOT NULL DEFAULT '0' COMMENT 'Identificador del articulo base a elaborar',
  `description` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `warehouse` int DEFAULT NULL COMMENT 'Identificador del almacen',
  `quantity` decimal(15,3) DEFAULT '0.000' COMMENT 'Cantidad a elaborar',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Indica el estado de la elaboracion',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  `remarks` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Observaciones',
  `source` tinyint DEFAULT '0' COMMENT 'Origen',
  `source_id` int DEFAULT '0' COMMENT 'Identificador del origen',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_ELABORATION_DOMAIN` (`domain`),
  KEY `IDX_ELABORATION_ITEM` (`item`),
  KEY `IDX_ELABORATION_WAREHOUSE` (`warehouse`),
  CONSTRAINT `FK_ELABORATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ELABORATION_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ELABORATION_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ordenes de elaboracion';

#
# Table structure for table `elaboration_detail`
#

CREATE TABLE `elaboration_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del detalle',
  `domain` int NOT NULL COMMENT 'Identificador del dominio',
  `elaboration` int NOT NULL COMMENT 'Identificador de la elaboracion',
  `type` tinyint DEFAULT '0' COMMENT 'Tipo de detalle',
  `date` datetime DEFAULT NULL COMMENT 'Fecha de elaboracion',
  `item` int NOT NULL DEFAULT '0' COMMENT 'Identificador del articulo no base elaborado',
  `quantity` decimal(15,3) DEFAULT '0.000' COMMENT 'Cantidad elaborado',
  `warehouse` int DEFAULT NULL COMMENT 'Identificador del almacen',
  `add_info` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Informacion de uso interno',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_ELABORATION_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_ELABORATION_DETAIL_ELABORATION` (`elaboration`),
  KEY `IDX_ELABORATION_DETAIL_ITEM` (`item`),
  KEY `IDX_ELABORATION_DETAIL_WAREHOUSE` (`warehouse`),
  CONSTRAINT `FK_ELABORATION_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ELABORATION_DETAIL_ELABORATION` FOREIGN KEY (`elaboration`) REFERENCES `elaboration` (`id`),
  CONSTRAINT `FK_ELABORATION_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ELABORATION_DETAIL_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de ordenes de elaboracion';

#
# Table structure for table `elaboration_detail_composition`
#

CREATE TABLE `elaboration_detail_composition` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la composicion',
  `domain` int NOT NULL COMMENT 'Identificador del dominio',
  `elaboration_detail` int NOT NULL COMMENT 'Identificador del lote elaborado',
  `item` int NOT NULL DEFAULT '0' COMMENT 'Identificador del articulo no base utilizado',
  `quantity` decimal(15,3) DEFAULT '0.000' COMMENT 'Cantidad utilizada',
  `warehouse` int DEFAULT NULL COMMENT 'Identificador del almacen',
  `add_info` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Informacion de uso interno',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_ELABORATION_DETAIL_COMPOSITION_DOMAIN` (`domain`),
  KEY `IDX_ELABORATION_DETAIL_COMPOSITION_ELABORATION_DETAIL` (`elaboration_detail`),
  KEY `IDX_ELABORATION_DETAIL_COMPOSITION_ITEM` (`item`),
  KEY `IDX_ELABORATION_DETAIL_COMPOSITION_WAREHOUSE` (`warehouse`),
  CONSTRAINT `FK_ELABORATION_DETAIL_COMPOSITION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ELABORATION_DETAIL_COMPOSITION_ELABORATION_DETAIL` FOREIGN KEY (`elaboration_detail`) REFERENCES `elaboration_detail` (`id`),
  CONSTRAINT `FK_ELABORATION_DETAIL_COMPOSITION_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ELABORATION_DETAIL_COMPOSITION_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Composicion de ordenes de elaboracion';

#
# Table structure for table `enterprise`
#

CREATE TABLE `enterprise` (
  `registry` int NOT NULL DEFAULT '1' COMMENT 'Registro de la Empresa',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `scope` int NOT NULL COMMENT 'Identificador del Ambito',
  `calendar` int DEFAULT NULL COMMENT 'Calendario',
  PRIMARY KEY (`registry`),
  KEY `IDX_ENTERPRISE_SCOPE` (`scope`),
  KEY `IDX_ENTERPRISE_CALENDAR` (`calendar`),
  KEY `IDX_ENTERPRISE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ENTERPRISE_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_ENTERPRISE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ENTERPRISE_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_ENTERPRISE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Empresa';

#
# Table structure for table `enterprise_activity`
#

CREATE TABLE `enterprise_activity` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Actividad de la Empresa',
  `enterprise` int NOT NULL COMMENT 'Identificador de la Empresa',
  `iae` int DEFAULT NULL COMMENT 'Epigrafe IAE',
  `cnae` int DEFAULT NULL COMMENT 'Identificador del CNAE',
  `type` tinyint NOT NULL COMMENT 'Tipo de Actividad de la Empresa',
  `cnae2009` int DEFAULT NULL COMMENT 'Identificador del CNAE 2009',
  `surcharge` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Actividad tiene de recargo de equivalencia',
  `vat_tax` int DEFAULT NULL COMMENT 'Identificador del IVA por defecto',
  `retention_tax` int DEFAULT NULL COMMENT 'Identificador del IRPF por defecto',
  `vat_regime` tinyint DEFAULT NULL COMMENT 'Regimen de IVA',
  `retention_regime` tinyint DEFAULT NULL COMMENT 'Regimen de IRPF',
  `start_date` date DEFAULT NULL COMMENT 'Fecha de inicio',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de fin',
  `prorata` decimal(5,2) DEFAULT '100.00' COMMENT 'Porcentaje de prorrata',
  `prorata_type` tinyint(1) DEFAULT '0' COMMENT 'Indica el tipo de prorrata',
  `principal` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si es la Actividad principal',
  PRIMARY KEY (`id`),
  KEY `IDX_ENTERPRISE_ACTIVITY_ENTERPRISE` (`enterprise`),
  KEY `IDX_ENTERPRISE_ACTIVITY_CNAE` (`cnae`),
  KEY `IDX_ENTERPRISE_ACTIVITY_CNAE2009` (`cnae2009`),
  KEY `IDX_ENTERPRISE_ACTIVITY_DOMAIN` (`domain`),
  KEY `IDX_ENTERPRISE_ACTIVITY_IAE` (`iae`),
  KEY `IDX_ENTERPRISE_ACTIVITY_TAX_VAT` (`vat_tax`),
  KEY `IDX_ENTERPRISE_ACTIVITY_TAX_RETENTION` (`retention_tax`),
  CONSTRAINT `FK_ENTERPRISE_ACTIVITY_CNAE` FOREIGN KEY (`cnae`) REFERENCES `cnae` (`id`),
  CONSTRAINT `FK_ENTERPRISE_ACTIVITY_CNAE2009` FOREIGN KEY (`cnae2009`) REFERENCES `cnae2009` (`id`),
  CONSTRAINT `FK_ENTERPRISE_ACTIVITY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ENTERPRISE_ACTIVITY_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`),
  CONSTRAINT `FK_ENTERPRISE_ACTIVITY_IAE` FOREIGN KEY (`iae`) REFERENCES `iae` (`id`),
  CONSTRAINT `FK_ENTERPRISE_ACTIVITY_TAX_RETENTION` FOREIGN KEY (`retention_tax`) REFERENCES `tax` (`id`),
  CONSTRAINT `FK_ENTERPRISE_ACTIVITY_TAX_VAT` FOREIGN KEY (`vat_tax`) REFERENCES `tax` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades de Empresas';

#
# Table structure for table `enterprise_ccc`
#

CREATE TABLE `enterprise_ccc` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `ccc` char(11) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor del Codigo Cuenta Cotizacion',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT 'Tipo de Cuenta Cotizacion',
  `enterprise_activity` int NOT NULL COMMENT 'Identificador de la Actividad de Empresa',
  `geozone` int DEFAULT NULL COMMENT 'Identificador de la Zona Geografica',
  PRIMARY KEY (`id`),
  KEY `IDX_ENTERPRISE_CCC_ENTERPRISE_ACTIVITY` (`enterprise_activity`),
  KEY `IDX_ENTERPRISE_CCC_GEOZONE` (`geozone`),
  KEY `IDX_ENTERPRISE_CCC_DOMAIN` (`domain`),
  CONSTRAINT `FK_ENTERPRISE_CCC_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ENTERPRISE_CCC_ENTERPRISE_ACTIVITY` FOREIGN KEY (`enterprise_activity`) REFERENCES `enterprise_activity` (`id`),
  CONSTRAINT `FK_ENTERPRISE_CCC_GEOZONE` FOREIGN KEY (`geozone`) REFERENCES `geozone` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Codigo Cuenta Cotizacion';

#
# Table structure for table `enterprise_data`
#

CREATE TABLE `enterprise_data` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int NOT NULL COMMENT 'Identificador de Empresa',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `expression` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Expresion',
  `start_date` date DEFAULT NULL COMMENT 'Fecha de inicio',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  PRIMARY KEY (`id`),
  KEY `IDX_ENTERPRISE_DATA_ENTERPRISE` (`enterprise`),
  KEY `IDX_ENTERPRISE_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_ENTERPRISE_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ENTERPRISE_DATA_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto de la Empresa';

#
# Table structure for table `evaluation_observation`
#

CREATE TABLE `evaluation_observation` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `alumn` int NOT NULL COMMENT 'Identificador de Alumno',
  `evaluation` tinyint NOT NULL COMMENT 'Numero de Evaluacion',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  PRIMARY KEY (`id`),
  KEY `IDX_EVALUATION_OBSERVATION_ALUMN` (`alumn`),
  KEY `IDX_EVALUATION_OBSERVATION_DOMAIN` (`domain`),
  CONSTRAINT `FK_EVALUATION_OBSERVATION_ALUMN` FOREIGN KEY (`alumn`) REFERENCES `course_alumn` (`id`),
  CONSTRAINT `FK_EVALUATION_OBSERVATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Observaciones por Evaluacion';

#
# Table structure for table `fan_batch`
#

CREATE TABLE `fan_batch` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la remesa',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Indica el estado de la remesa',
  `liquidation_type` tinyint NOT NULL DEFAULT '0' COMMENT 'Indica el tipo de liquidacion',
  `communication_id` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Identificador resultante de la comunicacion',
  `income_file` mediumblob COMMENT 'Archivo respuesta en binario',
  `income_file_date` datetime DEFAULT NULL COMMENT 'Fecha de respuesta',
  `outcome_file` mediumblob COMMENT 'Archivo Adjunto en binario',
  `outcome_file_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  PRIMARY KEY (`id`),
  KEY `IDX_FAN_BATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_FAN_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas del fichero fan';

#
# Table structure for table `fan_batch_detail`
#

CREATE TABLE `fan_batch_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del detalle',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `fan_batch` int NOT NULL COMMENT 'Identificador unico de la remesa',
  `enterprise_ccc` int NOT NULL COMMENT 'Identificador unico del ccc',
  PRIMARY KEY (`id`),
  KEY `IDX_FAN_BATCH_DETAIL_FAN_BATCH` (`fan_batch`),
  KEY `IDX_FAN_BATCH_DETAIL_ENTERPRISE_CCC` (`enterprise_ccc`),
  KEY `IDX_FAN_BATCH_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_FAN_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FAN_BATCH_DETAIL_ENTERPRISE_CCC` FOREIGN KEY (`enterprise_ccc`) REFERENCES `enterprise_ccc` (`id`),
  CONSTRAINT `FK_FAN_BATCH_DETAIL_FAN_BATCH` FOREIGN KEY (`fan_batch`) REFERENCES `fan_batch` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las remesas del fichero fan';

#
# Table structure for table `favorite`
#

CREATE TABLE `favorite` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de Favorito',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `favorite_category` int NOT NULL COMMENT 'Categoria a la que pertenece el Favorito',
  `description` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Favorito',
  `url` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Url del Favorito',
  `user_id` int NOT NULL COMMENT 'Usuario al que pertenece el Favorito',
  PRIMARY KEY (`id`),
  KEY `IDX_FAVORITE_FAVORITE_CATEGORY` (`favorite_category`),
  KEY `IDX_FAVORITE_USER` (`user_id`),
  KEY `IDX_FAVORITE_DOMAIN` (`domain`),
  CONSTRAINT `FK_FAVORITE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FAVORITE_FAVORITE_CATEGORY` FOREIGN KEY (`favorite_category`) REFERENCES `favorite_category` (`id`),
  CONSTRAINT `FK_FAVORITE_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Favoritos de Usuario';

#
# Table structure for table `favorite_category`
#

CREATE TABLE `favorite_category` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Categoria',
  `user_id` int NOT NULL COMMENT 'Usuario al que pertenece la Categoria',
  PRIMARY KEY (`id`),
  KEY `IDX_FAVORITE_CATEGORY_USER` (`user_id`),
  KEY `IDX_FAVORITE_CATEGORY_DOMAIN` (`domain`),
  CONSTRAINT `FK_FAVORITE_CATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FAVORITE_CATEGORY_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias de Favoritos de Usuario';

#
# Table structure for table `fbatch`
#

CREATE TABLE `fbatch` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Remesa',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de la Remesa',
  `issue_date` date DEFAULT NULL COMMENT 'Fecha de emision de la Remesa',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Remesa',
  `status` tinyint DEFAULT NULL COMMENT 'Estado de la Remesa',
  `rbank` int DEFAULT NULL COMMENT 'Banco de la Compañia utilizado en la Remesa',
  `bank_statement_link` int DEFAULT NULL COMMENT 'Identificador de la Linea del Extracto bancario',
  `payment` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si es un pago o un cobro',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad',
  `rattach` int DEFAULT NULL COMMENT 'Identificador del Archivo Adjunto',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_FBATCH_BANK_STATEMENT_LINK` (`bank_statement_link`),
  KEY `IDX_FBATCH_RBANK` (`rbank`),
  KEY `IDX_FBATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_FBATCH_BANK_STATEMENT_LINK` FOREIGN KEY (`bank_statement_link`) REFERENCES `bank_statement_link` (`id`),
  CONSTRAINT `FK_FBATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FBATCH_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas';

#
# Table structure for table `fbatch_detail`
#

CREATE TABLE `fbatch_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Detalle de la Remesa',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `fbatch` int NOT NULL COMMENT 'Identificador de la Remesa',
  `finance` int NOT NULL COMMENT 'Identificador del Vencimiento',
  `amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe del Detalle de la Remesa',
  `status` tinyint DEFAULT NULL COMMENT 'Estado del Detalle de la Remesa',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_FBATCH_DETAIL_FINANCE` (`finance`),
  KEY `IDX_FBATCH_DETAIL_FBATCH` (`fbatch`),
  KEY `IDX_FBATCH_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_FBATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FBATCH_DETAIL_FBATCH` FOREIGN KEY (`fbatch`) REFERENCES `fbatch` (`id`),
  CONSTRAINT `FK_FBATCH_DETAIL_FINANCE` FOREIGN KEY (`finance`) REFERENCES `finance` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de la Remesa';

#
# Table structure for table `feature`
#

CREATE TABLE `feature` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre de la Caracteristica',
  PRIMARY KEY (`id`),
  KEY `IDX_FEATURE_DOMAIN` (`domain`),
  CONSTRAINT `FK_FEATURE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Caracteristicas';

#
# Table structure for table `finance`
#

CREATE TABLE `finance` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Vencimiento',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `payment` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si es un pago o un cobro',
  `registry` int DEFAULT NULL COMMENT 'Identificador del Cliente o Proveedor',
  `rdocument` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Documento del Cliente o Proveedor',
  `rdocument_type` tinyint DEFAULT '0' COMMENT 'Tipo de documento (NIF, CIF...)',
  `rdocument_country` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT 'ES' COMMENT 'Pais del documento',
  `rname` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre completo del Cliente o Proveedor',
  `amount` decimal(15,4) DEFAULT '0' COMMENT 'Importe del Vencimiento',
  `expenses` decimal(15,3) DEFAULT '0.000' COMMENT 'Gastos asociados al Vencimiento',
  `concept` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Concepto del Vencimiento',
  `invoice` int DEFAULT NULL COMMENT 'Identificador de la Factura',
  `due_date` date DEFAULT NULL COMMENT 'Fecha de Vencimiento',
  `pay_method` int DEFAULT NULL COMMENT 'Identificador de la Forma de Pago',
  `bank_account` varchar(34) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'IBAN - Numero de Cuenta Bancaria Internacional',
  `bank_alias` varchar(25) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias del Banco',
  `bic` varchar(11) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'BIC - Codigo Identificador del Banco',
  `cheque_number` varchar(24) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de cheque',
  `status` tinyint DEFAULT '0' COMMENT 'Estado del Vencimiento',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad del Vencimiento',
  `remarks` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Observaciones del Vencimiento',
  `scope` int NOT NULL DEFAULT '1' COMMENT 'Ambito del Vencimiento',
  `manual` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Vencimiento es manual',
  `advance` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Vencimiento es un anticipo',
  `payroll` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Vencimiento es de Nominas',
  `prepayment` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Vencimiento es un Suplido',
  `source_id` int DEFAULT NULL COMMENT 'Identificador del Origen del Vencimiento',
  `finance_group` int DEFAULT NULL COMMENT 'Identificador unico del Vencimiento agrupador',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_FINANCE_SCOPE` (`scope`),
  KEY `IDX_FINANCE_DUE_DATE` (`due_date`),
  KEY `IDX_FINANCE_REGISTRY` (`registry`),
  KEY `IDX_FINANCE_PAY_METHOD` (`pay_method`),
  KEY `IDX_FINANCE_INVOICE` (`invoice`),
  KEY `IDX_FINANCE_DOMAIN` (`domain`),
  KEY `IDX_FINANCE_FINANCE` (`finance_group`),
  CONSTRAINT `FK_FINANCE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FINANCE_FINANCE` FOREIGN KEY (`finance_group`) REFERENCES `finance` (`id`),
  CONSTRAINT `FK_FINANCE_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`),
  CONSTRAINT `FK_FINANCE_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_FINANCE_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_FINANCE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Vencimientos';

#
# Table structure for table `finance_pos`
#

CREATE TABLE `finance_pos` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `finance` int NOT NULL COMMENT 'Identificador de Vencimiento',
  `pos` int NOT NULL COMMENT 'Identificador del TPV',
  `code` char(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo de autorizacion',
  `xml_response` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'XML de respuesta',
  PRIMARY KEY (`id`),
  KEY `IDX_FINANCE_POS_DOMAIN` (`domain`),
  KEY `IDX_FINANCE_POS_FINANCE` (`finance`),
  KEY `IDX_FINANCE_POS_POS` (`pos`),
  CONSTRAINT `FK_FINANCE_POS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FINANCE_POS_FINANCE` FOREIGN KEY (`finance`) REFERENCES `finance` (`id`),
  CONSTRAINT `FK_FINANCE_POS_POS` FOREIGN KEY (`pos`) REFERENCES `pos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Vencimientos de TPV';

#
# Table structure for table `finance_tracking`
#

CREATE TABLE `finance_tracking` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `finance` int NOT NULL COMMENT 'Identificador de Vencimiento',
  `tracking_date` date NOT NULL COMMENT 'Fecha de Seguimiento',
  `type` tinyint NOT NULL COMMENT 'Tipo de Seguimiento',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Seguimiento',
  `pm_type_detail` int DEFAULT NULL COMMENT 'Identificador del Detalle por Tipo de Forma de Pago',
  `rbank` int DEFAULT NULL COMMENT 'Identificador de la Cuenta Bancaria de la Compaia',
  `bank_statement_link` int DEFAULT NULL COMMENT 'Identificador de la Linea del Extracto bancario',
  `amount` decimal(15,3) DEFAULT NULL COMMENT 'Importe del Seguimiento',
  `recorded` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si esta contabilizado o no',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_FINANCE_TRACKING_RBANK` (`rbank`),
  KEY `IDX_FINANCE_TRACKING_PM_TYPE_DETAIL` (`pm_type_detail`),
  KEY `IDX_FINANCE_TRACKING_BANK_STATEMENT_LINK` (`bank_statement_link`),
  KEY `IDX_FINANCE_TRACKING_FINANCE` (`finance`),
  KEY `IDX_FINANCE_TRACKING_DOMAIN` (`domain`),
  CONSTRAINT `FK_FINANCE_TRACKING_BANK_STATEMENT_LINK` FOREIGN KEY (`bank_statement_link`) REFERENCES `bank_statement_link` (`id`),
  CONSTRAINT `FK_FINANCE_TRACKING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FINANCE_TRACKING_FINANCE` FOREIGN KEY (`finance`) REFERENCES `finance` (`id`),
  CONSTRAINT `FK_FINANCE_TRACKING_PM_TYPE_DETAIL` FOREIGN KEY (`pm_type_detail`) REFERENCES `pm_type_detail` (`id`),
  CONSTRAINT `FK_FINANCE_TRACKING_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Seguimiento de Vencimientos';

#
# Table structure for table `fs_activity`
#

CREATE TABLE `fs_activity` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `year` int NOT NULL COMMENT 'Ejercicio del Lote',
  `activity` int DEFAULT NULL COMMENT 'Identificador de la Actividad',
  `epigraph` varchar(7) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Epigrafe IAE',
  `description` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del epigrafe',
  `farmer` tinyint(1) DEFAULT '0' COMMENT 'Actividad agricola',
  `max_person` decimal(15,3) DEFAULT '0.000' COMMENT 'Valor maximo de personas',
  `max_import` decimal(15,3) DEFAULT '0.000' COMMENT 'Valor maximo de importe',
  `vat_percent` decimal(15,3) DEFAULT '0.000' COMMENT 'IVA - porcentaje aplicable',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_ACTIVITY_DOMAIN` (`domain`),
  KEY `IDX_FS_ACTIVITY_ACTIVITY` (`activity`),
  CONSTRAINT `FK_FS_ACTIVITY_ACTIVITY` FOREIGN KEY (`activity`) REFERENCES `enterprise_activity` (`id`),
  CONSTRAINT `FK_FS_ACTIVITY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Fiscal. Datos previos de Modulos';

#
# Table structure for table `fs_activity_info`
#

CREATE TABLE `fs_activity_info` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `fs_activity` int NOT NULL COMMENT 'Actividad fiscal',
  `info_key` varchar(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Clave de informacion',
  `line` int NOT NULL COMMENT 'Numero de linea',
  `type` tinyint NOT NULL COMMENT 'Tipo de linea',
  `value` varchar(25) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Valor de la informacion',
  `factor` decimal(15,3) DEFAULT '0.000' COMMENT 'Factor',
  `base` decimal(15,3) DEFAULT '0.000' COMMENT 'Rendimiento neto',
  `unit` varchar(25) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Unidades',
  `min_value` decimal(15,3) DEFAULT '0.000' COMMENT 'Valor minimo',
  `max_value` decimal(15,3) DEFAULT '0.000' COMMENT 'Valor maximo',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_ACTIVITY_INFO_DOMAIN` (`domain`),
  KEY `IDX_FS_ACTIVITY_INFO_FS_ACTIVITY` (`fs_activity`),
  CONSTRAINT `FK_FS_ACTIVITY_INFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_ACTIVITY_INFO_FS_ACTIVITY` FOREIGN KEY (`fs_activity`) REFERENCES `fs_activity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Fiscal. Informacion de los Datos previos de Modulos';

#
# Table structure for table `fs_mod347`
#

CREATE TABLE `fs_mod347` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `year` int NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `administration` tinyint DEFAULT '0' COMMENT 'Administracion',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `status` tinyint DEFAULT '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad',
  `complementary` tinyint(1) DEFAULT '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) DEFAULT '0' COMMENT 'Declaracion sustitutiva',
  `number` varchar(13) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion',
  `replaced_number` varchar(13) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion anterior',
  `document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF',
  `name` varchar(40) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `contact_phone` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono Persona de Contacto',
  `contact_person` varchar(40) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Persona de Contacto',
  `contact_mail` varchar(50) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mail Persona de Contacto',
  `representative_document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante Legal',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MOD347_DOMAIN` (`domain`),
  CONSTRAINT `FK_FS_MOD347_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaracion de Model 347';

#
# Table structure for table `fs_mod347_detail`
#

CREATE TABLE `fs_mod347_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `fs_mod347` int NOT NULL DEFAULT '0' COMMENT 'Identificador de la Declaracion',
  `sheet` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL DEFAULT 'D' COMMENT 'Tipo de hoja (Declarado o Inmueble)',
  `type` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Clave de operacion',
  `document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF del declarado',
  `representative_document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante Legal',
  `registry` int DEFAULT '0' COMMENT 'Identificador del Declarado',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Apellidos  y Nombre del declarado',
  `province` int DEFAULT '0' COMMENT 'Provincia del declarado',
  `country` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Pais del declarado',
  `amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones',
  `first_quarter_amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones primer trimestre',
  `second_quarter_amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones segundo trimestre',
  `third_quarter_amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones tercer trimestre',
  `fourth_quarter_amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones cuarto trimestre',
  `asset_amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones de inmuebles',
  `asset_first_quarter_amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones de inmuebles del primer trimestre',
  `asset_second_quarter_amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones de inmuebles del segundo trimestre',
  `asset_third_quarter_amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones de inmuebles del tercer trimestre',
  `asset_fourth_quarter_amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones de inmuebles del cuarto trimestre',
  `cash_amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones en metalico',
  `cash_year` int DEFAULT NULL COMMENT 'Ejercicio en el que se hubieran declarado las operaciones en metalico.',
  `insurance_operation` tinyint(1) DEFAULT '0' COMMENT 'Las Entidades Aseguradoras maracaran este campo para identificar las operaciones de seguros',
  `business_premise_rental` tinyint(1) DEFAULT '0' COMMENT 'Se marcara este campo para operaciones de arrendamiento de locales de negocio,',
  `asset_location` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Situacion del inmueble',
  `cadasdral_reference` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Refercia catastral',
  `asset_street_type` varchar(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo de via',
  `asset_street` varchar(50) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre de la via',
  `asset_street_number_type` varchar(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo de numero de via',
  `asset_street_number` varchar(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de via',
  `asset_street_number_suffix` varchar(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Calificador del numero de via',
  `asset_street_block` varchar(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Bloque.',
  `asset_street_hall` varchar(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Portal.',
  `asset_street_stair` varchar(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Escalera.',
  `asset_street_floor` varchar(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Planta.',
  `asset_street_door` varchar(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Puerta.',
  `asset_street_complement` varchar(40) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Complemento.',
  `asset_street_city` varchar(30) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Complemento.',
  `asset_street_town` varchar(30) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Complemento.',
  `asset_street_town_code` varchar(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Complemento.',
  `asset_street_province` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Complemento.',
  `asset_street_zip` varchar(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Complemento.',
  `operator_nif` varchar(17) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF del operador intracomunitario',
  `vat_accrual` tinyint(1) DEFAULT '0' COMMENT 'Regimen de critrio de caja',
  `isp` tinyint(1) DEFAULT '0' COMMENT 'Inversion de sujeto pasivo',
  `deposit_regime` tinyint(1) DEFAULT '0' COMMENT 'Regimen de deposito',
  `vat_accrual_amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones en reg, caja',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MOD347_DETAIL_FS_MOD347` (`fs_mod347`),
  KEY `IDX_FS_MOD347_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_FS_MOD347_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MOD347_DETAIL_FS_MOD347` FOREIGN KEY (`fs_mod347`) REFERENCES `fs_mod347` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de la Declaracion 347';

#
# Table structure for table `fs_mod349`
#

CREATE TABLE `fs_mod349` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `year` int NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `period` tinyint DEFAULT '0' COMMENT 'Periodo de la Declaracion',
  `administration` tinyint DEFAULT '0' COMMENT 'Administracion',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `status` tinyint DEFAULT '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad',
  `complementary` tinyint(1) DEFAULT '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) DEFAULT '0' COMMENT 'Declaracion sustitutiva',
  `number` varchar(13) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion',
  `replaced_number` varchar(13) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion anterior',
  `document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF',
  `name` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `representative_document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante Legal',
  `contact_phone` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono Persona de Contacto',
  `contact_mail` varchar(50) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mail Persona de Contacto',
  `contact_person` varchar(100) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Persona de Contacto',
  `periodicity_change` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indicador Cambio Periodicidad',
  `diff_enabled` tinyint(1) DEFAULT '1' COMMENT 'Declaracion por diferencia',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  `fs_model` int DEFAULT NULL COMMENT 'Identificador de fs_model',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MOD349_DOMAIN` (`domain`),
  KEY `IDX_FS_MOD349_FS_MODEL` (`fs_model`),
  CONSTRAINT `FK_FS_MOD349_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MOD349_FS_MODEL` FOREIGN KEY (`fs_model`) REFERENCES `fs_model` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaracion de Modelo 349';

#
# Table structure for table `fs_mod349_detail`
#

CREATE TABLE `fs_mod349_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `fs_mod349` int NOT NULL DEFAULT '0' COMMENT 'Identificador de la Declaracion',
  `rectification` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Rectificacion',
  `type` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Clave de operacion',
  `document` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Documento del operador',
  `registry` int DEFAULT '0' COMMENT 'Identificador del Declarado',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Apellidos y Nombre del Declarado',
  `country` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Pais del Declarado',
  `accumulated` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe acumulado de las operaciones',
  `declared` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe declarado de las operaciones',
  `amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones',
  `rectified_year` int DEFAULT NULL COMMENT 'Ejercicio de la Declaracion del importe rectificado',
  `rectified_period` tinyint DEFAULT NULL COMMENT 'Periodo de la Declaracion del importe rectificado',
  `rectified_amount` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Importe rectificado',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MOD349_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_FS_MOD349_DETAIL_FS_MOD349` (`fs_mod349`),
  CONSTRAINT `FK_FS_MOD349_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MOD349_DETAIL_FS_MOD349` FOREIGN KEY (`fs_mod349`) REFERENCES `fs_mod349` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de la Declaracion 349';

#
# Table structure for table `fs_model`
#

CREATE TABLE `fs_model` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `year` int NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `period` tinyint NOT NULL DEFAULT '0' COMMENT 'Periodo de la Declaracion',
  `administration` tinyint NOT NULL COMMENT 'Administracion',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad',
  `complementary` tinyint(1) DEFAULT '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) DEFAULT '0' COMMENT 'Declaracion sustitutiva',
  `withoutActivity` tinyint(1) DEFAULT '0' COMMENT 'Sin Actividad',
  `model` varchar(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Tipo de modelo',
  `number` varchar(13) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion',
  `replaced_number` varchar(13) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion complementada o sustituida',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `finance` int DEFAULT NULL COMMENT 'Identificador de Vencimiento',
  `document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF',
  `surname` varchar(30) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Apellidos',
  `name` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `street_initial` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Sigla via',
  `street_name` varchar(17) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre de la via publica',
  `street_number` varchar(4) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de la via publica',
  `street_stair` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Escalera',
  `street_floor` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Piso',
  `street_door` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Puerta',
  `phone` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono',
  `town` varchar(20) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Municipio',
  `province` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Provincia',
  `zip` varchar(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo Postal',
  `admon_aeat` varchar(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de Administracion AEAT',
  `contact_person` varchar(100) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Persona de Contacto',
  `contact_phone` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telf. Fijo',
  `contact_cellular` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telf. Movil',
  `contact_email` varchar(100) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Email',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  `account_entry` int DEFAULT NULL COMMENT 'identificador del apunte',
  `result` decimal(15,3) DEFAULT NULL COMMENT 'Resultado',
  `declaration_type` tinyint DEFAULT NULL COMMENT 'Tipo de resultado',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL_FINANCE` (`finance`),
  KEY `IDX_FS_MODEL_ACCOUNT_ENTRY` (`account_entry`),
  CONSTRAINT `FK_FS_MODEL_ACCOUNT_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_FS_MODEL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL_FINANCE` FOREIGN KEY (`finance`) REFERENCES `finance` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaraciones Fiscales';

#
# Table structure for table `fs_model180`
#

CREATE TABLE `fs_model180` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int NOT NULL COMMENT 'Identificador de la Empresa',
  `year` int NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `administration` tinyint NOT NULL COMMENT 'Administracion',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad',
  `document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF',
  `name` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `contact_person` varchar(100) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Persona de Contacto',
  `contact_phone` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telf. Fijo de Contacto',
  `contact_mail` varchar(50) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mail Persona de Contacto',
  `complementary` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion sustitutiva',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `receipt` varchar(13) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion',
  `replaced_receipt` varchar(13) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion sustituida',
  `receiver_count_total` int NOT NULL DEFAULT '0' COMMENT 'Numero total de perceptores',
  `receipt_total` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Importe declarado',
  `retention_total` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Importe declarado',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL180_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL180_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_FS_MODEL180_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL180_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaraciones 180';

#
# Table structure for table `fs_model180_detail`
#

CREATE TABLE `fs_model180_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `fs_model180` int NOT NULL COMMENT 'Identificador del modelo 180',
  `document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Perceptor',
  `name` varchar(40) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `representative_document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante',
  `province` int NOT NULL DEFAULT '0' COMMENT 'Provincia',
  `inKind` tinyint(1) NOT NULL COMMENT 'Percepcion en especie',
  `perception` decimal(15,3) NOT NULL DEFAULT '0.000',
  `percentage` decimal(15,3) DEFAULT '0.000' COMMENT 'Porcentaje de retencion',
  `retention` decimal(15,3) NOT NULL DEFAULT '0.000',
  `accrual_year` int NOT NULL DEFAULT '0',
  `location` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Situacion del inmueble',
  `cadasdral_reference` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Refercia catastral',
  `street_type` varchar(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo de via',
  `street_name` varchar(50) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre de la via',
  `number_type` varchar(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo de numero de via',
  `number` varchar(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de via',
  `number_suffix` varchar(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Calificador del numero de via',
  `block` varchar(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Bloque.',
  `hall` varchar(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Portal.',
  `stair` varchar(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Escalera.',
  `floor` varchar(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Planta.',
  `door` varchar(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Puerta.',
  `complement` varchar(40) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Complemento.',
  `city` varchar(30) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Localidad o poblacion.',
  `town` varchar(30) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Municipio.',
  `town_code` varchar(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de municipio..',
  `province_code` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de provincia.',
  `zip` varchar(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo postal.',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL180_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL180_DETAIL_FS_MODEL180` (`fs_model180`),
  CONSTRAINT `FK_FS_MODEL180_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL180_DETAIL_FS_MODEL180` FOREIGN KEY (`fs_model180`) REFERENCES `fs_model180` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Declaraciones 180';

#
# Table structure for table `fs_model184`
#

CREATE TABLE `fs_model184` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int NOT NULL COMMENT 'Identificador de la Empresa',
  `year` int NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `administration` tinyint NOT NULL COMMENT 'Administracion',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad',
  `document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF',
  `name` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `contact_person` varchar(100) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Persona de Contacto',
  `contact_phone` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telf. Fijo de Contacto',
  `contact_mail` varchar(50) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mail Persona de Contacto',
  `complementary` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion sustitutiva',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `receipt` varchar(13) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion',
  `replaced_receipt` varchar(13) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion sustituida',
  `entity_type` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo de entidad nacional',
  `main_activity` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Actividad principal',
  `foreign_entity_type` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo de entidad extranjera',
  `foreign_object` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Objeto',
  `country` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Pais',
  `resident_percent` decimal(15,3) DEFAULT '0.000' COMMENT 'Porcentaje de renta atrib. a miembros residentes',
  `tax_is` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Tributacion en regimen del Impuesto sobre Sociedades',
  `net_sales_amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe neto cifra de negocios',
  `lrdocument` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante',
  `lrname` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre Representante',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL184_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL184_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_FS_MODEL184_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL184_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaraciones 184';

#
# Table structure for table `fs_model184_detail`
#

CREATE TABLE `fs_model184_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `fs_model184` int NOT NULL COMMENT 'Identificador del modelo 184',
  `type` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL DEFAULT 'I' COMMENT 'Tipo de detalle',
  `document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Perceptor',
  `name` varchar(40) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `representative_document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante',
  `key` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Clave Percepcion',
  `subkey` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Subclave Percepcion',
  `province` int NOT NULL DEFAULT '0' COMMENT 'Provincia',
  `country` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Pais',
  `regime` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Regimen de determinacion de rendimientos',
  `activity_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Tipo de actividad',
  `epigraph` int DEFAULT '0' COMMENT 'Epigrafe',
  `grantee_document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF persona o entidad cesionaria',
  `grantee_name` varchar(40) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre persona o entidad cesionaria',
  `adq_date` date DEFAULT NULL COMMENT 'Fecha adquisicion accion/participacion',
  `increase` decimal(15,3) DEFAULT NULL COMMENT 'Ajustes: Aumentos',
  `decrease` decimal(15,3) DEFAULT NULL COMMENT 'Ajustes: Disminuciones',
  `accounting_result` decimal(15,3) DEFAULT NULL COMMENT 'Resultado contable',
  `expenses` decimal(15,3) DEFAULT NULL COMMENT 'Gastos',
  `net_yield` decimal(15,3) DEFAULT NULL COMMENT 'Renta atribuible / Rend. Neto atribuible',
  `reduction_percent` decimal(15,3) DEFAULT NULL COMMENT 'Porc. Reduccion',
  `deduction_right_rent` decimal(15,3) DEFAULT NULL COMMENT 'Renta atrib. con drcho. deduccion',
  `result` decimal(15,3) DEFAULT NULL COMMENT 'Ganancias / Perdidas',
  `deduction_base` decimal(15,3) DEFAULT NULL COMMENT 'Base de la deduccion / Importe',
  `retention` decimal(15,3) DEFAULT NULL COMMENT 'Retenciones e ingresos a cuenta',
  `part_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Clave tipo de participe',
  `member_end_of_year` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Miembro a 31 diciembre',
  `member_days` int DEFAULT '0' COMMENT 'Numero dias miembro',
  `part_percent` decimal(15,3) DEFAULT NULL COMMENT 'Porcentaje de participacion',
  `amount` decimal(15,3) DEFAULT NULL COMMENT 'Importe (rendimiento / retencion / deduccion)',
  `reduction` decimal(15,3) DEFAULT NULL COMMENT 'Reduccion',
  `address` varchar(60) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion',
  `location` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Situacion del inmueble',
  `cadasdral_reference` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Referencia catastral',
  `staff_expenses` decimal(15,3) DEFAULT '0.000' COMMENT 'Gastos de personal',
  `asset_acquisition` decimal(15,3) DEFAULT '0.000' COMMENT 'Adquisicion a terceros de bienes y servicios',
  `tax_deduction` decimal(15,3) DEFAULT '0.000' COMMENT 'Tributos fiscalmente deducibles y gastos financieros',
  `other_tax_deduction` decimal(15,3) DEFAULT '0.000' COMMENT 'Otros gastos fiscalmente deducibles',
  `vat_accrual_payment` tinyint(1) DEFAULT '0' COMMENT 'Regimen Especial de Criterio de Caja',
  `declared_key` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Clave del declarado',
  `nature` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Naturaleza del inmueble',
  `asset_percent` decimal(15,3) DEFAULT '0.000' COMMENT 'Porc. titularidad inmueble',
  `consumos_explotacion` decimal(15,4) DEFAULT '0',
  `arrendamientos_canones` decimal(15,4) DEFAULT '0',
  `reparacion_conservacion` decimal(15,4) DEFAULT '0',
  `serv_prof_indep` decimal(15,4) DEFAULT '0',
  `suministros` decimal(15,4) DEFAULT '0',
  `gastos_financieros` decimal(15,4) DEFAULT '0',
  `amortizaciones` decimal(15,4) DEFAULT '0',
  `provisiones` decimal(15,4) DEFAULT '0',
  `inm_int_fin` decimal(15,4) DEFAULT '0',
  `inm_rep_con` decimal(15,4) DEFAULT '0',
  `inm_gas_rep_con` decimal(15,4) DEFAULT '0',
  `inm_trib_rec` decimal(15,4) DEFAULT '0',
  `inm_sald_dud_cobr` decimal(15,4) DEFAULT '0',
  `inm_cant_dev_ter` decimal(15,4) DEFAULT '0',
  `inm_prim_seg` decimal(15,4) DEFAULT '0',
  `inm_amort` decimal(15,4) DEFAULT '0',
  `inm_amort_mueb` decimal(15,4) DEFAULT '0',
  `inm_otr_gas_ded` decimal(15,4) DEFAULT '0',
  `inm_num_dias_arr` int DEFAULT '0',
  `asset_days` int DEFAULT '0' COMMENT 'Numero dias inmueble',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL184_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL184_DETAIL_FS_MODEL184` (`fs_model184`),
  CONSTRAINT `FK_FS_MODEL184_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL184_DETAIL_FS_MODEL184` FOREIGN KEY (`fs_model184`) REFERENCES `fs_model184` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Declaraciones 184';

#
# Table structure for table `fs_model190`
#

CREATE TABLE `fs_model190` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int NOT NULL COMMENT 'Identificador de la Empresa',
  `year` int NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `administration` tinyint NOT NULL COMMENT 'Administracion',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad',
  `document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF',
  `name` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `contact_person` varchar(100) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Persona de Contacto',
  `contact_phone` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telf. Fijo de Contacto',
  `contact_mail` varchar(50) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mail Persona de Contacto',
  `complementary` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion sustitutiva',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `receipt` varchar(13) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion',
  `replaced_receipt` varchar(13) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion sustituida',
  `receiver_count_total` int NOT NULL DEFAULT '0' COMMENT 'Numero total de perceptores',
  `receipt_total` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Importe declarado',
  `retention_total` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Importe declarado',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL190_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL190_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_FS_MODEL190_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL190_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaraciones 190';

#
# Table structure for table `fs_model190_detail`
#

CREATE TABLE `fs_model190_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `fs_model190` int NOT NULL COMMENT 'Identificador del modelo 190',
  `document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Perceptor',
  `name` varchar(40) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `representative_document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante',
  `province` int NOT NULL DEFAULT '0' COMMENT 'Provincia',
  `key` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Clave Percepcion',
  `subkey` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Subclave Percepcion',
  `perception` decimal(15,3) NOT NULL DEFAULT '0.000',
  `retention` decimal(15,3) NOT NULL DEFAULT '0.000',
  `in_kind_perception` decimal(15,3) NOT NULL DEFAULT '0.000',
  `in_kind_deposit` decimal(15,3) NOT NULL DEFAULT '0.000',
  `in_kind_output_deposit` decimal(15,3) NOT NULL DEFAULT '0.000',
  `accrual_year` int NOT NULL DEFAULT '0',
  `ceuta_melilla` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Los datos anteriores corresponden a rendimientos obtenidos en Ceuta o Melilla',
  `birth_year` int NOT NULL DEFAULT '0',
  `family_situation` tinyint NOT NULL DEFAULT '0' COMMENT 'Situacion familiar',
  `spouse_document` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Documento del conyuge',
  `disability` tinyint NOT NULL DEFAULT '0' COMMENT 'Grado de discapacidad',
  `contract` tinyint NOT NULL DEFAULT '0' COMMENT 'Contrato o relacion',
  `labour_prolongation` tinyint(1) DEFAULT '0' COMMENT 'Prolongacion de la actividad laboral',
  `geographic_mobility` tinyint(1) DEFAULT '0' COMMENT 'Movilidad geografica',
  `applicable_reduction` decimal(15,3) DEFAULT NULL COMMENT 'Reducciones aplicables',
  `deducible_expenses` decimal(15,3) DEFAULT NULL COMMENT 'Gastos deducibles ',
  `spousal_support` decimal(15,3) DEFAULT NULL COMMENT 'Pension compensatoria a favor del cónyuge.',
  `food_annuity` decimal(15,3) DEFAULT NULL COMMENT 'Anualidades por alimentos en favor de los hijos.',
  `less_than_3_descendent` tinyint(1) DEFAULT '0',
  `less_than_3_descendent_ratio` tinyint(1) DEFAULT '0',
  `other_descendent` tinyint(1) DEFAULT '0',
  `other_descendent_ratio` tinyint(1) DEFAULT '0',
  `disability_descendent_33` tinyint(1) DEFAULT '0',
  `disability_descendent_33_ratio` tinyint(1) DEFAULT '0',
  `disability_descendent_dependence` tinyint(1) DEFAULT '0',
  `disability_descendent_dependence_ratio` tinyint(1) DEFAULT '0',
  `disability_descendent_65` tinyint(1) DEFAULT '0',
  `disability_descendent_65_ratio` tinyint(1) DEFAULT '0',
  `less_than_75_ascendant` tinyint(1) DEFAULT '0',
  `less_than_75_ascendant_ratio` tinyint(1) DEFAULT '0',
  `ascendant` tinyint(1) DEFAULT '0',
  `ascendant_ratio` tinyint(1) DEFAULT '0',
  `disability_ascendant_33` tinyint(1) DEFAULT '0',
  `disability_ascendant_33_ratio` tinyint(1) DEFAULT '0',
  `disability_ascendant_dependence` tinyint(1) DEFAULT '0',
  `disability_ascendant_dependence_ratio` tinyint(1) DEFAULT '0',
  `disability_ascendant_65` tinyint(1) DEFAULT '0',
  `disability_ascendant_65_Ratio` tinyint(1) DEFAULT '0',
  `first_child_calculation` tinyint(1) DEFAULT '0',
  `second_child_calculation` tinyint(1) DEFAULT '0',
  `third_child_calculation` tinyint(1) DEFAULT '0',
  `home_loan_communnication` tinyint(1) DEFAULT '0',
  `perception_il` decimal(15,3) DEFAULT '0.000' COMMENT 'Percepción Integra/valoracion derivada de incapacidad laboral',
  `retention_il` decimal(15,3) DEFAULT '0.000' COMMENT 'Retenciones practicadas/ingresos a cuenta efectuados derivadas de incapacidad laboral',
  `output_retention_il` decimal(15,3) DEFAULT '0.000' COMMENT 'Ingresos a cuenta repercutidos derivados de incapacidad laboral',
  `in_kind_perception_il` decimal(15,3) NOT NULL DEFAULT '0.000',
  `in_kind_deposit_il` decimal(15,3) NOT NULL DEFAULT '0.000',
  `in_kind_output_deposit_il` decimal(15,3) NOT NULL DEFAULT '0.000',
  `tit_convivencia` tinyint(1) DEFAULT '0' Comment 'Tit. unidad convivencia',
  `comp_infancia` tinyint(1) DEFAULT '0' Comment 'Compl. ayuda infancia',  
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL190_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL190_DETAIL_FS_MODEL190` (`fs_model190`),
  CONSTRAINT `FK_FS_MODEL190_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL190_DETAIL_FS_MODEL190` FOREIGN KEY (`fs_model190`) REFERENCES `fs_model190` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Declaraciones 190';

#
# Table structure for table `fs_model193`
#

CREATE TABLE `fs_model193` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int NOT NULL COMMENT 'Identificador de la Empresa',
  `year` int NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `administration` tinyint NOT NULL COMMENT 'Administracion',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad',
  `document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF',
  `name` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `contact_person` varchar(100) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Persona de Contacto',
  `contact_phone` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telf. Fijo de Contacto',
  `contact_mail` varchar(50) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mail Persona de Contacto',
  `complementary` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion sustitutiva',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `receipt` varchar(13) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion',
  `replaced_receipt` varchar(13) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion sustituida',
  `receiver_count_total` int NOT NULL DEFAULT '0' COMMENT 'Numero total de perceptores',
  `retention_base_total` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Base de retenciones e ingresos a cuenta',
  `retention_total` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Retenciones e ingresos a cuenta',
  `deposit_retention_total` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Retenciones e ingresos a cuenta ingresados',
  `expenses_total` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Gastos',
  `nature` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Naturaleza del declarante',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL193_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL193_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_FS_MODEL193_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL193_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaraciones 193';

#
# Table structure for table `fs_model193_detail`
#

CREATE TABLE `fs_model193_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `fs_model193` int NOT NULL COMMENT 'Identificador del modelo 193',
  `type` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Tipo de Registro P (perceptor) o G (Gastos), ',
  `document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Perceptor',
  `name` varchar(40) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `representative_document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante',
  `intermediary_payment` tinyint(1) DEFAULT NULL COMMENT 'Pago a un Mediador',
  `province` int NOT NULL DEFAULT '0' COMMENT 'Provincia',
  `key_code` tinyint(1) DEFAULT NULL COMMENT 'Clave Codigo',
  `issuing_code` varchar(12) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo emisor',
  `key` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Clave de percepcion',
  `nature` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Naturaleza de la percepcion',
  `payment` tinyint(1) DEFAULT NULL COMMENT 'Pago',
  `code_type` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo Codigo',
  `lender_amount` decimal(15,3) NOT NULL DEFAULT '0.000',
  `account_code` varchar(20) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de cuenta,Numero id. prestamo',
  `pending` tinyint(1) DEFAULT NULL COMMENT 'Pendiente',
  `accrual_year` int NOT NULL DEFAULT '0',
  `in_kind` tinyint(1) DEFAULT NULL COMMENT 'Percepcion en especie',
  `perception` decimal(15,3) NOT NULL DEFAULT '0.000',
  `reduction` decimal(15,3) NOT NULL DEFAULT '0.000',
  `retention_base` decimal(15,3) NOT NULL DEFAULT '0.000',
  `percent` decimal(5,3) NOT NULL DEFAULT '0.000',
  `retention` decimal(15,3) NOT NULL DEFAULT '0.000',
  `deponent_nature` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Naturaleza del declarante',
  `loan_start_date` date DEFAULT NULL COMMENT 'Fecha de inicio del prestamo',
  `loan_due_date` date DEFAULT NULL COMMENT 'Fecha de vencimiento del prestamo',
  `compensation` decimal(15,3) NOT NULL DEFAULT '0.000',
  `guarantee` decimal(15,3) NOT NULL DEFAULT '0.000',
  `expenses` decimal(15,3) NOT NULL DEFAULT '0.000',
  `penalization` decimal(15,3) DEFAULT '0.000' COMMENT 'Penalizaciones',
  `declarant_nature` tinyint(1) DEFAULT '0' COMMENT 'Naturaleza del declarante',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL193_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL193_DETAIL_FS_MODEL193` (`fs_model193`),
  CONSTRAINT `FK_FS_MODEL193_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL193_DETAIL_FS_MODEL193` FOREIGN KEY (`fs_model193`) REFERENCES `fs_model193` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Declaraciones 193';

#
# Table structure for table `fs_model200`
#

CREATE TABLE `fs_model200` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int NOT NULL COMMENT 'Identificador de la Empresa',
  `year` int NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `administration` tinyint NOT NULL COMMENT 'Administracion',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Estado de la Declaracion',
  `document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF',
  `name` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `phone1` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono 1',
  `phone2` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono 2',
  `complementary` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion complementaria',
  `receipt` varchar(13) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion',
  `complementary_receipt` varchar(13) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion sustituida',
  `cnae` varchar(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo CNAE',
  `period_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Tipo de periodo',
  `period_start` date NOT NULL COMMENT 'Inicio periodo',
  `period_end` date NOT NULL COMMENT 'Fin periodo',
  `fiscal_group` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de grupo fiscal',
  `dominant_document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF de la entidad dominante',
  `secretary_document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF del secretario',
  `secretary_name` varchar(25) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF del secretario',
  `irnr` date DEFAULT NULL COMMENT 'Fecha IRNR',
  `result_type` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT '(D) Devolucion, (I) Ingreso, (C) Cuota cero',
  `dev_type` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT '(R) Renuncia, (T) Transferencia',
  `pay_type` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT '(E) Efectivo, (A) Adeudo',
  `amount` decimal(15,3) DEFAULT NULL COMMENT 'Importe',
  `iban` varchar(34) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'IBAN',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `nrs_anexoIII` varchar(30) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NRS anexo III',
  `just_canarias` varchar(30) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de justificante Canarias',
  `nrs_anexoIV` varchar(30) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NRS anexo IV',
  `nrs_anexoV` varchar(30) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NRS anexo V',
  `bic` char(11) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'BIC - Codigo Identificador del Banco',
  `just_activos` varchar(30) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de justificante activos',
  `ultimate_document` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `ultimate_document_country` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `ultimate_name` varchar(40) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `ultimate_country` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `nrs_anexoV_ric` varchar(22) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  `fs_model` int DEFAULT NULL COMMENT 'Identificador de fs_model',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL200_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL200_ENTERPRISE` (`enterprise`),
  KEY `IDX_FS_MODEL200_FS_MODEL` (`fs_model`),
  CONSTRAINT `FK_FS_MODEL200_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL200_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`),
  CONSTRAINT `FK_FS_MODEL200_FS_MODEL` FOREIGN KEY (`fs_model`) REFERENCES `fs_model` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Impuesto sobre sociedades';

#
# Table structure for table `fs_model200_detail`
#

CREATE TABLE `fs_model200_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `fs_model200` int NOT NULL COMMENT 'Identificador del modelo 200',
  `key` varchar(7) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Clave Casilla',
  `value` decimal(15,3) DEFAULT NULL COMMENT 'Valor de la casilla',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL200_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL200_DETAIL_FS_MODEL200` (`fs_model200`),
  CONSTRAINT `FK_FS_MODEL200_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL200_DETAIL_FS_MODEL200` FOREIGN KEY (`fs_model200`) REFERENCES `fs_model200` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles Impuesto sobre sociedades';

#
# Table structure for table `fs_model200_registry`
#

CREATE TABLE `fs_model200_registry` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `fs_model200` int NOT NULL COMMENT 'Identificador del modelo 200',
  `document` varchar(20) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `name` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `province` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Provincia',
  `country` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Pais',
  `residence` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Residencia',
  `representative` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Representante',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Tipo. Administrador/participacion ',
  `percent` decimal(15,3) DEFAULT NULL COMMENT 'Porcentaje',
  `nominal_value` decimal(15,3) DEFAULT NULL COMMENT 'Valor Nominal',
  `book_value` decimal(15,3) DEFAULT NULL COMMENT 'Valor en libros',
  `incomes` decimal(15,3) DEFAULT NULL COMMENT 'Ingresos por dividendos',
  `a_value` decimal(15,3) DEFAULT NULL COMMENT 'Correccion de valor',
  `b_value` decimal(15,3) DEFAULT NULL COMMENT 'Reversion por perdidas',
  `c_value` decimal(15,3) DEFAULT NULL COMMENT 'Efecto de la correccion',
  `d_value` decimal(15,3) DEFAULT NULL COMMENT 'Saldo de correcciones',
  `capital` decimal(15,3) DEFAULT NULL COMMENT 'Capital',
  `reserve` decimal(15,3) DEFAULT NULL COMMENT 'Reservas',
  `other_amounts` decimal(15,3) DEFAULT NULL COMMENT 'Otras partidas',
  `result` decimal(15,3) DEFAULT NULL COMMENT 'Resultado del ultimo ejercicio',
  `notary` varchar(20) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Notaria',
  `notary_date` date DEFAULT NULL COMMENT 'Fecha Notaria',
  `cc_value` decimal(15,3) DEFAULT NULL COMMENT 'Eliminacion del deterioro contable',
  `dd_value` decimal(15,4) DEFAULT NULL,
  `e_value` decimal(15,4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL200_REGISTRY_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL200_REGISTRY_FS_MODEL200` (`fs_model200`),
  CONSTRAINT `FK_FS_MODEL200_REGISTRY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL200_REGISTRY_FS_MODEL200` FOREIGN KEY (`fs_model200`) REFERENCES `fs_model200` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Adminitradores/participaciones del Impuesto sobre sociedades';

#
# Table structure for table `fs_model390`
#

CREATE TABLE `fs_model390` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int NOT NULL COMMENT 'Identificador de la Empresa',
  `year` int NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `administration` tinyint NOT NULL COMMENT 'Administracion',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad',
  `document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF',
  `name` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `complementary` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion sustitutiva',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `receipt` varchar(13) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion',
  `replaced_receipt` varchar(13) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion sustituida',
  `model` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Modelo XML de la Declaracion',
  `response` varchar(30) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Respuesta AEAT',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL390_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL390_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_FS_MODEL390_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL390_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaraciones 390';

#
# Table structure for table `fs_model_detail`
#

CREATE TABLE `fs_model_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `fs_model` int NOT NULL COMMENT 'Identificador de la Declaracion',
  `type` varchar(10) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Clave de la Declaracion',
  `description` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `acu_amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe acumulado',
  `dec_amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe declarado',
  `res_amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe resultado',
  `adj_amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe ajustado',
  `amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL_DETAIL_FS_MODEL` (`fs_model`),
  CONSTRAINT `FK_FS_MODEL_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL_DETAIL_FS_MODEL` FOREIGN KEY (`fs_model`) REFERENCES `fs_model` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de Declaraciones Fiscales';

#
# Table structure for table `fs_vat`
#

CREATE TABLE `fs_vat` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `year` int NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `period` tinyint DEFAULT '0' COMMENT 'Periodo de la Declaracion',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `status` tinyint DEFAULT '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad',
  `complementary` tinyint(1) DEFAULT '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) DEFAULT '0' COMMENT 'Declaracion sustitutiva',
  `tax_refund_registry` tinyint(1) DEFAULT '0' COMMENT 'Inscrito en registro de devolucion',
  `number` int DEFAULT '0' COMMENT 'Numero de Decl. complementaria o sustitutiva',
  `prorata` decimal(5,2) DEFAULT '100.00' COMMENT 'Porcentaje de prorrata',
  `replaced_number` varchar(13) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion complementada o sustituida',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_VAT_DOMAIN` (`domain`),
  CONSTRAINT `FK_FS_VAT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaracion de Iva';

#
# Table structure for table `fs_vat_declaration`
#

CREATE TABLE `fs_vat_declaration` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `fs_vat` int NOT NULL DEFAULT '0' COMMENT 'Identificador de la Declaracion',
  `without_activity` tinyint(1) DEFAULT '0' COMMENT 'Sin actividad',
  `administration` tinyint DEFAULT '0' COMMENT 'Administracion',
  `percent` decimal(15,4) DEFAULT '0' COMMENT 'Porcentaje atribuible',
  `operations_volume` decimal(15,3) DEFAULT '0.000' COMMENT 'Volumen de operaciones',
  `quota` decimal(15,3) DEFAULT '0.000' COMMENT 'Cuota atribuible',
  `prev_year_compensate_quota` decimal(15,3) DEFAULT '0.000' COMMENT 'Cuota a compensar de ejerc. anteriores',
  `done_deposits` decimal(15,3) DEFAULT '0.000' COMMENT 'Ingresos efectuados',
  `done_refunds` decimal(15,3) DEFAULT '0.000' COMMENT 'Devoluciones practicadas',
  `extra_charge` decimal(15,3) DEFAULT '0.000' COMMENT 'Recargo',
  `delay_interest` decimal(15,3) DEFAULT '0.000' COMMENT 'Intereses de demora',
  `compensate` decimal(15,3) DEFAULT '0.000' COMMENT 'A compensar',
  `pay_back` decimal(15,3) DEFAULT '0.000' COMMENT 'A devolver',
  `deposit` decimal(15,3) DEFAULT '0.000' COMMENT 'A ingresar',
  `prev_deposit` decimal(15,3) DEFAULT '0.000' COMMENT 'Ingresado anteriormente',
  `prev_pay_back` decimal(15,3) DEFAULT '0.000' COMMENT 'Devuelto anteriormente',
  `total_tax_debt` decimal(15,3) DEFAULT '0.000' COMMENT 'Total deuda tributaria',
  `rbank` int DEFAULT NULL COMMENT 'Banco de la Compaia',
  `compensable` tinyint(1) DEFAULT '0' COMMENT 'Compensar o devolver',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Estado de la Declaracion',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_VAT_DECLARATION_FS_VAT` (`fs_vat`),
  KEY `IDX_FS_VAT_DECLARATION_RBANK` (`rbank`),
  KEY `IDX_FS_VAT_DECLARATION_DOMAIN` (`domain`),
  CONSTRAINT `FK_FS_VAT_DECLARATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_VAT_DECLARATION_FS_VAT` FOREIGN KEY (`fs_vat`) REFERENCES `fs_vat` (`id`),
  CONSTRAINT `FK_FS_VAT_DECLARATION_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Resultado de la Declaracion de Iva';

#
# Table structure for table `fs_vat_detail`
#

CREATE TABLE `fs_vat_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `fs_vat` int NOT NULL DEFAULT '0' COMMENT 'Identificador de la Declaracion',
  `vat_key` varchar(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Clave de la Declaracion',
  `percent` decimal(15,4) DEFAULT '0' COMMENT 'Porcentaje de Iva',
  `taxable_base` decimal(15,3) DEFAULT '0.000' COMMENT 'Base imponible',
  `quota` decimal(15,3) DEFAULT '0.000' COMMENT 'Cuota',
  `deductible_quota` decimal(15,3) DEFAULT '0.000' COMMENT 'Cuota deducible',
  `adj_taxable_base` decimal(15,3) DEFAULT '0.000' COMMENT 'Base imponible ajustada',
  `adj_quota` decimal(15,3) DEFAULT '0.000' COMMENT 'Cuota ajustada',
  `adj_deductible_quota` decimal(15,3) DEFAULT '0.000' COMMENT 'Cuota deducible ajustada',
  `acu_taxable_base` decimal(15,3) DEFAULT '0.000' COMMENT 'Base imponible acumulada',
  `acu_quota` decimal(15,3) DEFAULT '0.000' COMMENT 'Cuota acumulada',
  `acu_deductible_quota` decimal(15,3) DEFAULT '0.000' COMMENT 'Cuota deducible acumulada',
  `dec_taxable_base` decimal(15,3) DEFAULT '0.000' COMMENT 'Base imponible declarado',
  `dec_quota` decimal(15,3) DEFAULT '0.000' COMMENT 'Cuota declarado',
  `dec_deductible_quota` decimal(15,3) DEFAULT '0.000' COMMENT 'Cuota deducible declarado',
  `res_taxable_base` decimal(15,3) DEFAULT '0.000' COMMENT 'Base imponible resultado',
  `res_quota` decimal(15,3) DEFAULT '0.000' COMMENT 'Cuota resultado',
  `res_deductible_quota` decimal(15,3) DEFAULT '0.000' COMMENT 'Cuota deducible resultado',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_VAT_DETAIL_FS_VAT` (`fs_vat`),
  KEY `IDX_FS_VAT_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_FS_VAT_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_VAT_DETAIL_FS_VAT` FOREIGN KEY (`fs_vat`) REFERENCES `fs_vat` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de Declaracion de Iva';

#
# Table structure for table `geotree`
#

CREATE TABLE `geotree` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `parent` int DEFAULT NULL COMMENT 'Identificador de la Zona Geografica Padre',
  `child` int NOT NULL COMMENT 'Identificador de la Zona Geografica Hijo',
  PRIMARY KEY (`id`),
  KEY `IDX_GEOTREE_PARENT_GEOZONE` (`parent`),
  KEY `IDX_GEOTREE_CHILD_GEOZONE` (`child`),
  KEY `IDX_GEOTREE_DOMAIN` (`domain`),
  CONSTRAINT `FK_GEOTREE_CHILD_GEOZONE` FOREIGN KEY (`child`) REFERENCES `geozone` (`id`),
  CONSTRAINT `FK_GEOTREE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_GEOTREE_PARENT_GEOZONE` FOREIGN KEY (`parent`) REFERENCES `geozone` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Jerarquia de Zonas Geograficas';

#
# Table structure for table `geozone`
#

CREATE TABLE `geozone` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Zona Geografica',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Zona Geografica',
  `code` varchar(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de la Zona Geografica',
  `system` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si es una Zona Geografica del sistema',
  PRIMARY KEY (`id`),
  KEY `IDX_GEOZONE_DOMAIN` (`domain`),
  CONSTRAINT `FK_GEOZONE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Zonas Geograficas';

#
# Table structure for table `geozone_irpf`
#

CREATE TABLE `geozone_irpf` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `geozone_code` varchar(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo de la Zona Geografica',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe rendimiento anual',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tabla de tramos del IRPF';

#
# Table structure for table `geozone_irpf_descendant`
#

CREATE TABLE `geozone_irpf_descendant` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `geozone_irpf` int DEFAULT NULL COMMENT 'Identificador del tramo de IRPF',
  `descendant` tinyint DEFAULT '0' COMMENT 'Descendientes',
  `percent` decimal(15,2) DEFAULT '0.00' COMMENT 'Porcentaje',
  PRIMARY KEY (`id`),
  KEY `IDX_GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF` (`geozone_irpf`),
  CONSTRAINT `FK_GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF` FOREIGN KEY (`geozone_irpf`) REFERENCES `geozone_irpf` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tabla de porcentajes IRPF segun descendientes';

#
# Table structure for table `geozone_irpf_handicap`
#

CREATE TABLE `geozone_irpf_handicap` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `geozone_irpf` int DEFAULT NULL COMMENT 'Identificador del tramo de IRPF',
  `handicap` tinyint DEFAULT '0' COMMENT 'Grado Minusvalia',
  `percent` decimal(15,2) DEFAULT '0.00' COMMENT 'Porcentaje',
  PRIMARY KEY (`id`),
  KEY `IDX_GEOZONE_IRPF_HANDICAP_GEOZONE_IRPF` (`geozone_irpf`),
  CONSTRAINT `FK_GEOZONE_IRPF_HANDICAP_GEOZONE_IRPF` FOREIGN KEY (`geozone_irpf`) REFERENCES `geozone_irpf` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tabla de  ';

#
# Table structure for table `holiday`
#

CREATE TABLE `holiday` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de la Festividad',
  `holiday` int DEFAULT NULL COMMENT 'Identificador de Festividad',
  `editable` tinyint(1) DEFAULT '0' COMMENT 'Indica si es editable o no',
  PRIMARY KEY (`id`),
  KEY `IDX_HOLIDAY_HOLIDAY` (`holiday`),
  KEY `IDX_HOLIDAY_DOMAIN` (`domain`),
  CONSTRAINT `FK_HOLIDAY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_HOLIDAY_HOLIDAY` FOREIGN KEY (`holiday`) REFERENCES `holiday` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Festividades';

#
# Table structure for table `holiday_detail`
#

CREATE TABLE `holiday_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `holiday` int NOT NULL COMMENT 'Identificador de Festividad',
  `date` date NOT NULL COMMENT 'Fecha Festiva',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de Festividad',
  PRIMARY KEY (`id`),
  KEY `IDX_HOLIDAY_DETAIL_HOLIDAY` (`holiday`),
  KEY `IDX_HOLIDAY_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_HOLIDAY_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_HOLIDAY_DETAIL_HOLIDAY` FOREIGN KEY (`holiday`) REFERENCES `holiday` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de Festividades';

#
# Table structure for table `hotel`
#

CREATE TABLE `hotel` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `code` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo del Hotel',
  `scope` int NOT NULL COMMENT 'Identificador del Ambito',
  `workplace` int NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `phone` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono del Hotel',
  `fax` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Fax del Hotel',
  `email` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Email del Hotel',
  `web` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Web del Hotel',
  `service_catalogue` int DEFAULT NULL COMMENT 'Identificador del Catalogo de Servicios',
  `sheet_changing` tinyint DEFAULT NULL COMMENT 'Dias entre cambio de sabanas',
  `police_code` varchar(10) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del Hotel para la policia',
  `police_counter` int DEFAULT '0' COMMENT 'Contador para envio de ficheros a la policia',
  `tourist_tax` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Hotel aplica Tasa turistica o no',
  `active` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Hotel esta activo o no',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_HOTEL_CODE` (`code`),
  KEY `IDX_HOTEL_SCOPE` (`scope`),
  KEY `IDX_HOTEL_WORKPLACE` (`workplace`),
  KEY `IDX_HOTEL_DOMAIN` (`domain`),
  KEY `IDX_HOTEL_SERVICE_CATALOGUE` (`service_catalogue`),
  CONSTRAINT `FK_HOTEL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_HOTEL_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_HOTEL_SERVICE_CATALOGUE` FOREIGN KEY (`service_catalogue`) REFERENCES `catalogue` (`id`),
  CONSTRAINT `FK_HOTEL_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Hoteles';

#
# Table structure for table `iae`
#

CREATE TABLE `iae` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `section` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Seccion',
  `epigraph` varchar(8) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Epigrafe',
  `title` varchar(255) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Titulo',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='IAE';

#
# Table structure for table `iattach`
#

CREATE TABLE `iattach` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Archivo Adjunto del Articulo',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `item` int NOT NULL DEFAULT '0' COMMENT 'Identificador de Articulo',
  `mimeType` tinyint DEFAULT NULL COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Archivo Adjunto',
  `driveId` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_IATTACH_ITEM` (`item`),
  KEY `IDX_IATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_IATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_IATTACH_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Articulos';

#
# Table structure for table `income`
#

CREATE TABLE `income` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Albaran de Compra',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `project` int DEFAULT NULL COMMENT 'Identificador del Proyecto',
  `reference_code` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo de referencia del Albaran',
  `supplier` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Proveedor',
  `address` int DEFAULT NULL COMMENT 'Identificador de la Direccion del Proveedor',
  `issue_time` date DEFAULT NULL COMMENT 'Fecha de emision del Albaran',
  `pay_method` int DEFAULT NULL COMMENT 'Identificador de la Forma de Pago',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad del Albaran',
  `status` tinyint DEFAULT '0' COMMENT 'Estado del Albaran',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios del Albaran',
  `remarks` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Observaciones del Albaran',
  `workplace` int DEFAULT NULL COMMENT 'Identificador del Centro de Trabajo',
  `scope` int NOT NULL DEFAULT '1' COMMENT 'Ambito del Albaran',
  `number_of_pymnts` smallint DEFAULT '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint DEFAULT '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint DEFAULT '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Dias de pago',
  `bank_account` varchar(34) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'IBAN - Numero de Cuenta Bancaria Internacional',
  `bank_alias` varchar(25) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias del Banco',
  `bic` varchar(11) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'BIC - Codigo Identificador del Banco',
  `carrier_packing` int DEFAULT NULL COMMENT 'Identificador de la Hoja de ruta',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_INCOME_DOMAIN_SUPPLIER_REFERENCE_CODE` (`domain`,`supplier`,`reference_code`),
  KEY `IDX_INCOME_WORKPLACE` (`workplace`),
  KEY `IDX_INCOME_SCOPE` (`scope`),
  KEY `IDX_INCOME_PROJECT` (`project`),
  KEY `IDX_INCOME_SUPPLIER` (`supplier`),
  KEY `IDX_INCOME_RADDRESS` (`address`),
  KEY `IDX_INCOME_PAY_METHOD` (`pay_method`),
  KEY `IDX_INCOME_DOMAIN` (`domain`),
  CONSTRAINT `FK_INCOME_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INCOME_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_INCOME_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_INCOME_RADDRESS` FOREIGN KEY (`address`) REFERENCES `raddress` (`id`),
  CONSTRAINT `FK_INCOME_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_INCOME_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`),
  CONSTRAINT `FK_INCOME_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Albaranes de Compra';

#
# Table structure for table `income_detail`
#

CREATE TABLE `income_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Detalle del Albaran de Compra',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `income` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Albaran de Compra',
  `project` int DEFAULT NULL COMMENT 'Identificador del Proyecto',
  `line` smallint DEFAULT '1' COMMENT 'Numero de linea del Detalle dentro del Albaran',
  `item` int NOT NULL COMMENT 'Identificador del Articulo del Detalle de Albaran',
  `description` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Detalle de Albaran',
  `warehouse` int NOT NULL COMMENT 'Identificador del Almacen',
  `quantity` decimal(15,3) DEFAULT NULL COMMENT 'Cantidad del Detalle de Albaran',
  `price` decimal(15,4) DEFAULT '0' COMMENT 'Precio del Detalle de Albaran',
  `discount_expr` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descuentos del Detalle de Albaran',
  `purchase_detail` int DEFAULT NULL COMMENT 'Identificador del Detalle del Pedido de Compra asociado',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_INCOME_DETAIL_PROJECT` (`project`),
  KEY `IDX_INCOME_DETAIL_ITEM` (`item`),
  KEY `IDX_INCOME_DETAIL_WAREHOUSE` (`warehouse`),
  KEY `IDX_INCOME_DETAIL_PURCHASE_DETAIL` (`purchase_detail`),
  KEY `IDX_INCOME_DETAIL_INCOME` (`income`),
  KEY `IDX_INCOME_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_INCOME_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INCOME_DETAIL_INCOME` FOREIGN KEY (`income`) REFERENCES `income` (`id`),
  CONSTRAINT `FK_INCOME_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_INCOME_DETAIL_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_INCOME_DETAIL_PURCHASE_DETAIL` FOREIGN KEY (`purchase_detail`) REFERENCES `purchase_detail` (`id`),
  CONSTRAINT `FK_INCOME_DETAIL_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Albaran de Compra';

#
# Table structure for table `inventory`
#

CREATE TABLE `inventory` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Inventario',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `inventory_date` date NOT NULL DEFAULT '0000-00-00' COMMENT 'Fecha de Inventario',
  `warehouse` int NOT NULL DEFAULT '0' COMMENT 'Almacen Inventariado',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Inventario',
  `status` tinyint DEFAULT '0' COMMENT 'Estado del Inventario',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_INVENTORY_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVENTORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Inventarios de Almacenes';

#
# Table structure for table `inventory_detail`
#

CREATE TABLE `inventory_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Detalle del Inventario',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `inventory` int NOT NULL DEFAULT '0' COMMENT 'Identificador de Inventario',
  `item` int NOT NULL DEFAULT '0' COMMENT 'Articulo Inventariado',
  `actual_quantity` decimal(15,3) DEFAULT '0.000' COMMENT 'Cantidad actual del Articulo Inventariado',
  `real_quantity` decimal(15,3) DEFAULT '0.000' COMMENT 'Cantidad real del Articulo Inventariado',
  `cost` decimal(15,3) DEFAULT '0.000' COMMENT 'Coste del Articulo Inventariado',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_INVENTORY_DETAIL_INVENTORY` (`inventory`),
  KEY `IDX_INVENTORY_DETAIL_ITEM` (`item`),
  KEY `IDX_INVENTORY_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVENTORY_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVENTORY_DETAIL_INVENTORY` FOREIGN KEY (`inventory`) REFERENCES `inventory` (`id`),
  CONSTRAINT `FK_INVENTORY_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Inventarios de Almacenes';

#
# Table structure for table `invest_asset`
#

CREATE TABLE `invest_asset` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `activity` int DEFAULT NULL COMMENT 'Identificador de la Actividad',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Bien',
  `type` tinyint NOT NULL COMMENT 'Tipo de Bien',
  `regime` tinyint NOT NULL COMMENT 'Regimen',
  `start_date` date DEFAULT NULL COMMENT 'Fecha de alta',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de baja',
  `vat_percent` decimal(15,4) DEFAULT '0' COMMENT 'Porcentaje de afectacion de IVA',
  `retention_percent` decimal(15,4) DEFAULT '0' COMMENT 'Porcentaje de afectacion de imposicion directa',
  `properties` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'Propiedades de los Bienes Afecto o de Inversion' CHECK (json_valid(`properties`)),
  PRIMARY KEY (`id`),
  KEY `IDX_INVEST_ASSET_DOMAIN` (`domain`),
  KEY `IDX_INVEST_ASSET_ACTIVITY` (`activity`),
  CONSTRAINT `FK_INVEST_ASSET_ACTIVITY` FOREIGN KEY (`activity`) REFERENCES `enterprise_activity` (`id`),
  CONSTRAINT `FK_INVEST_ASSET_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bienes afectos o de inversion';

#
# Table structure for table `invoice`
#

CREATE TABLE `invoice` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Factura',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `activity` int DEFAULT NULL COMMENT 'Identificador de la Actividad',
  `invest_asset` int DEFAULT NULL COMMENT 'Identificador del Bien afecto',
  `project` int DEFAULT NULL COMMENT 'Identificador del Proyecto',
  `series` char(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie de la Factura',
  `number` int NOT NULL DEFAULT '0' COMMENT 'Numero de la Factura',
  `reference_code` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de referencia de la Factura',
  `registry` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Cliente o Proveedor',
  `rdocument` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Documento del Cliente o Proveedor',
  `rdocument_type` tinyint DEFAULT '0' COMMENT 'Tipo de documento (NIF, CIF...)',
  `rdocument_country` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT 'ES' COMMENT 'Pais del documento',
  `rname` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre completo del Cliente o Proveedor',
  `raddress` int DEFAULT NULL COMMENT 'Identificador de la Direccion de envio de la Factura',
  `issue_date` date DEFAULT NULL COMMENT 'Fecha de emision de la Factura',
  `tax_date` date DEFAULT NULL COMMENT 'Fecha de Impuestos de la Factura',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad de la Factura',
  `status` tinyint DEFAULT '0' COMMENT 'Estado de la Factura',
  `type` tinyint DEFAULT '0' COMMENT 'Tipo de Factura (Compra o Venta)',
  `surcharge` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Factura tiene recargo de equivalencia',
  `withholding` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Factura aplica retencion de impuestos',
  `withholding_farmer` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Factura aplica retencion de Regimen Especial de Agricultura y Pesca',
  `vat_accrual_payment` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Factura se incluye en el Regimen Especial de Criterio de Caja',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Factura',
  `remarks` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Observaciones de la Factura',
  `investment` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Factura es una inversion',
  `transaction` tinyint DEFAULT '0' COMMENT 'Tipo de transaccion',
  `signed` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Factura esta firmada electronicamente',
  `scope` int NOT NULL DEFAULT '1' COMMENT 'Ambito de la Factura',
  `service` tinyint(1) DEFAULT '0' COMMENT 'Indica si es una Factura de servicios',
  `rectification_type` tinyint DEFAULT '0' COMMENT 'Tipo de rectificacion (Normal o Especial)',
  `rectification_invoice` int DEFAULT NULL COMMENT 'Relacion de rectificacion de Facturas',
  `advance` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Factura es un anticipo',
  `pos_shift` int DEFAULT NULL COMMENT 'Identificador del Turno de trabajo',
  `seller` int DEFAULT NULL COMMENT 'Identificador de Agente Comercial',
  `taxable_base` decimal(15,4) DEFAULT '0' COMMENT 'Base Imponible de la Factura',
  `vat_quota` decimal(15,4) DEFAULT '0' COMMENT 'Cuota de IVA de la Factura',
  `retention_quota` decimal(15,4) DEFAULT '0' COMMENT 'Cuota de IRPF de la Factura',
  `total` decimal(15,4) DEFAULT '0' COMMENT 'Total Factura',
# `annulled` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Factura esta anulada',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_INVOICE_DOMAIN_SERIES_NUMBER_TYPE` (`domain`,`series`,`number`,`type`),
  KEY `IDX_INVOICE_SCOPE` (`scope`),
  KEY `IDX_INVOICE_INVOICE` (`rectification_invoice`),
  KEY `IDX_INVOICE_PROJECT` (`project`),
  KEY `IDX_INVOICE_ISSUE_DATE` (`issue_date`),
  KEY `IDX_INVOICE_TAX_DATE` (`tax_date`),
  KEY `IDX_INVOICE_REGISTRY` (`registry`),
  KEY `IDX_INVOICE_RADDRESS` (`raddress`),
  KEY `IDX_INVOICE_DOMAIN` (`domain`),
  KEY `IDX_INVOICE_SELLER` (`seller`),
  KEY `IDX_INVOICE_POS_SHIFT` (`pos_shift`),
  KEY `IDX_INVOICE_REFERENCE_CODE` (`reference_code`),
  KEY `IDX_INVOICE_ACTIVITY` (`activity`),
  KEY `IDX_INVOICE_INVEST_ASSET` (`invest_asset`),
  CONSTRAINT `FK_INVOICE_ACTIVITY` FOREIGN KEY (`activity`) REFERENCES `enterprise_activity` (`id`),
  CONSTRAINT `FK_INVOICE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_INVEST_ASSET` FOREIGN KEY (`invest_asset`) REFERENCES `invest_asset` (`id`),
  CONSTRAINT `FK_INVOICE_INVOICE` FOREIGN KEY (`rectification_invoice`) REFERENCES `invoice` (`id`),
  CONSTRAINT `FK_INVOICE_POS_SHIFT` FOREIGN KEY (`pos_shift`) REFERENCES `pos_shift` (`id`),
  CONSTRAINT `FK_INVOICE_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_INVOICE_RADDRESS` FOREIGN KEY (`raddress`) REFERENCES `raddress` (`id`),
  CONSTRAINT `FK_INVOICE_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_INVOICE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_INVOICE_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Facturas';

#
# Table structure for table `invoice_address`
#

CREATE TABLE `invoice_address` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `invoice` int NOT NULL COMMENT 'Identificador de la Factura',
  `street_type` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT 'CL' COMMENT 'Tipo de via',
  `address` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Primera parte de la Direccion',
  `number` varchar(12) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero',
  `address2` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Segunda parte de la Direccion',
  `zip` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo Postal',
  `city` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Localidad',
  `province` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Provincia',
  `geozone` int DEFAULT NULL COMMENT 'Identificador de la Zona Geografica',
  PRIMARY KEY (`id`),
  KEY `IDX_INVOICE_ADDRESS_INVOICE` (`invoice`),
  KEY `IDX_INVOICE_ADDRESS_GEOZONE` (`geozone`),
  KEY `IDX_INVOICE_ADDRESS_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICE_ADDRESS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_ADDRESS_GEOZONE` FOREIGN KEY (`geozone`) REFERENCES `geozone` (`id`),
  CONSTRAINT `FK_INVOICE_ADDRESS_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Direcciones de la Factura';

#
# Table structure for table `invoice_attach`
#

CREATE TABLE `invoice_attach` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `invoice` int NOT NULL COMMENT 'Identificador de la Factura',
  `mimeType` tinyint DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint DEFAULT '0' COMMENT 'Tipo de Archivo Adjunto',
  `attach_date` date DEFAULT NULL COMMENT 'Fecha del Archivo Adjunto',
  `driveId` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_INVOICE_ATTACH_INVOICE` (`invoice`),
  KEY `IDX_INVOICE_ATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICE_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_ATTACH_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Facturas';

#
# Table structure for table `invoice_batch`
#

CREATE TABLE `invoice_batch` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `date` datetime NOT NULL COMMENT 'Fecha de comunicacion',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT 'Tipo de Comunicacion',
  `operation` tinyint NOT NULL DEFAULT '0' COMMENT 'Tipo de Operación',
  `data_response` int NOT NULL COMMENT 'Envio de la comunicacion',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  PRIMARY KEY (`id`),
  KEY `IDX_INVOICE_BATCH_DOMAIN` (`domain`),
  KEY `IDX_INVOICE_BATCH_DATA_RESPONSE` (`data_response`),
  CONSTRAINT `FK_INVOICE_BATCH_DATA_RESPONSE` FOREIGN KEY (`data_response`) REFERENCES `data_response` (`id`),
  CONSTRAINT `FK_INVOICE_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comunicacion Lote Facturas';

#
# Table structure for table `invoice_batch_detail`
#

CREATE TABLE `invoice_batch_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `invoice` int NOT NULL COMMENT 'Identificador de la factura',
  `invoice_batch` int NOT NULL COMMENT 'Identificador de invoice batch',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Estado de la comunicacion',
  PRIMARY KEY (`id`),
  KEY `IDX_INVOICE_BATCH_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_INVOICE_BATCH_DETAIL_INVOICE` (`invoice`),
  KEY `IDX_INVOICE_BATCH_DETAIL_INVOICE_BATCH` (`invoice_batch`),
  CONSTRAINT `FK_INVOICE_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_BATCH_DETAIL_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`),
  CONSTRAINT `FK_INVOICE_BATCH_DETAIL_INVOICE_BATCH` FOREIGN KEY (`invoice_batch`) REFERENCES `invoice_batch` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle Comunicacion Lote Facturas';

#
# Table structure for table `invoice_detail`
#

CREATE TABLE `invoice_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Detalle de la Factura',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `invoice` int NOT NULL DEFAULT '0' COMMENT 'Identificador de la Factura',
  `invest_asset` int DEFAULT NULL COMMENT 'Identificador del Bien afecto',
  `project` int DEFAULT NULL COMMENT 'Identificador del Proyecto',
  `line` smallint DEFAULT '1' COMMENT 'Numero de linea del Detalle dentro de la Factura',
  `item` int DEFAULT NULL COMMENT 'Identificador del Articulo del Detalle de Factura',
  `description` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Detalle de Factura',
  `quantity` decimal(15,3) DEFAULT '0.000' COMMENT 'Cantidad del Detalle de Factura',
  `price` decimal(15,4) DEFAULT '0' COMMENT 'Precio del Detalle de Factura',
  `discount_expr` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descuentos del Detalle de Factura',
  `source` tinyint DEFAULT '0' COMMENT 'Origen del Detalle de la Factura',
  `source_id` int DEFAULT NULL COMMENT 'Identificador del Origen del Detalle de la Factura',
  `taxable_base` decimal(15,4) DEFAULT '0.0000' COMMENT 'Base Imponible del Detalle de Factura',
  `taxes` decimal(15,3) DEFAULT '0.000' COMMENT 'Tasas del Detalle de Factura',
  `prepayment` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Detalle de Factura es un Suplido',
  `seller` int DEFAULT NULL COMMENT 'Identificador de Agente Comercial',
  `workplace` int NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `warehouse` int DEFAULT NULL COMMENT 'Identificador del Almacen',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_INVOICE_DETAIL_PROJECT` (`project`),
  KEY `IDX_INVOICE_DETAIL_WAREHOUSE` (`warehouse`),
  KEY `IDX_INVOICE_DETAIL_SOURCE_ID` (`source_id`),
  KEY `IDX_INVOICE_DETAIL_INVOICE` (`invoice`),
  KEY `IDX_INVOICE_DETAIL_ITEM` (`item`),
  KEY `IDX_INVOICE_DETAIL_WORKPLACE` (`workplace`),
  KEY `IDX_INVOICE_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_INVOICE_DETAIL_SELLER` (`seller`),
  KEY `IDX_INVOICE_DETAIL_INVEST_ASSET` (`invest_asset`),
  CONSTRAINT `FK_INVOICE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_DETAIL_INVEST_ASSET` FOREIGN KEY (`invest_asset`) REFERENCES `invest_asset` (`id`),
  CONSTRAINT `FK_INVOICE_DETAIL_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`),
  CONSTRAINT `FK_INVOICE_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_INVOICE_DETAIL_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_INVOICE_DETAIL_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`),
  CONSTRAINT `FK_INVOICE_DETAIL_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`),
  CONSTRAINT `FK_INVOICE_DETAIL_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de la Factura';

#
# Table structure for table `invoice_detail_account`
#

CREATE TABLE `invoice_detail_account` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `invoice_detail` int NOT NULL DEFAULT '0' COMMENT 'Identificador de la Linea de Factura',
  `account` int NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY (`id`),
  KEY `IDX_INVOICE_DETAIL_ACCOUNT_ACCOUNT` (`account`),
  KEY `IDX_INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL` (`invoice_detail`),
  KEY `IDX_INVOICE_DETAIL_ACCOUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICE_DETAIL_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_INVOICE_DETAIL_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL` FOREIGN KEY (`invoice_detail`) REFERENCES `invoice_detail` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables asociadas a Lineas de Facturas';

#
# Table structure for table `invoice_detail_commission`
#

CREATE TABLE `invoice_detail_commission` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `invoice_detail` int NOT NULL DEFAULT '0' COMMENT 'Identificador de la Linea de Factura',
  `commission` decimal(15,4) DEFAULT '0' COMMENT 'Porcentaje de Comision',
  `amount` decimal(15,4) DEFAULT '0' COMMENT 'Importe de la Comision',
  `status` tinyint DEFAULT '0' COMMENT 'Estado de la Comision',
  `pay_date` date DEFAULT NULL COMMENT 'Fecha de liquidacion',
  PRIMARY KEY (`id`),
  KEY `IDX_INVOICE_DETAIL_COMMISSION_INVOICE_DETAIL` (`invoice_detail`),
  KEY `IDX_INVOICE_DETAIL_COMMISSION_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICE_DETAIL_COMMISSION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_DETAIL_COMMISSION_INVOICE_DETAIL` FOREIGN KEY (`invoice_detail`) REFERENCES `invoice_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones asociadas a Lineas de Facturas';

#
# Table structure for table `invoice_dua`
#

CREATE TABLE `invoice_dua` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',
  `domain` int NOT NULL DEFAULT '0' COMMENT 'Dominio',
  `invoice_national` int NOT NULL DEFAULT '0' COMMENT 'ID de la factura nacional',
  `invoice_import` int NOT NULL DEFAULT '0' COMMENT 'ID de la factura de importacion',
  `code` varchar(20) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo DUA',
  `price` decimal(15,3) DEFAULT '0.000' COMMENT 'Precio del articulo',
  `adjust` decimal(15,3) DEFAULT '0.000' COMMENT 'Ajuste',
  `statistical_value` decimal(15,3) DEFAULT '0.000' COMMENT 'Valor estadistico',
  `duty_account` int NOT NULL COMMENT 'ID cuenta para aranceles',
  `duty_base` decimal(15,3) DEFAULT '0.000' COMMENT 'Base aranceles',
  `duty_percent` decimal(15,3) DEFAULT '0.000' COMMENT 'Porcentaje aranceles',
  `duty_total` decimal(15,3) DEFAULT '0.000' COMMENT 'Cuota aranceles',
  `vat_account` int NOT NULL COMMENT 'ID cuenta para IVA',
  PRIMARY KEY (`id`),
  KEY `IDX_INVOICE_DUA_NATIONAL` (`invoice_national`),
  KEY `IDX_INVOICE_DUA_IMPORT` (`invoice_import`),
  KEY `IDX_INVOICE_DUA_DOMAIN` (`domain`),
  KEY `IDX_INVOICE_DUA_DUTY_ACCOUNT` (`duty_account`),
  KEY `IDX_INVOICE_DUA_VAT_ACCOUNT` (`vat_account`),
  CONSTRAINT `FK_INVOICE_DUA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_DUA_DUTY_ACCOUNT` FOREIGN KEY (`duty_account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_INVOICE_DUA_INVOICE_IMPORT` FOREIGN KEY (`invoice_import`) REFERENCES `invoice` (`id`),
  CONSTRAINT `FK_INVOICE_DUA_INVOICE_NATIONAL` FOREIGN KEY (`invoice_national`) REFERENCES `invoice` (`id`),
  CONSTRAINT `FK_INVOICE_DUA_VAT_ACCOUNT` FOREIGN KEY (`vat_account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Vinculo Factura DUA';

#
# Table structure for table `invoice_fiscal`
#

CREATE TABLE `invoice_fiscal` (
  `invoice` int NOT NULL DEFAULT '0' COMMENT 'Identificador de la Factura',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `issue_date` date DEFAULT NULL COMMENT 'Fecha de emision de la Factura',
  `tax_date` date DEFAULT NULL COMMENT 'Fecha de Impuestos de la Factura',
  `exp_date` date DEFAULT NULL COMMENT 'Fecha de expedicion de la Factura para Ticket Bai',
  `vat_general` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Regimen General',
  `vat_simplified` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Regimen especial simplificado',
  `vat_surcharge` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Regimen especial recargo de equivalencia',
  `vat_accrual_payment` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Regimen especial del criterio de caja',
  `vat_rebu_operation` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Regimen especial bienes usados por operacion',
  `vat_rebu_profit` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Regimen especial bienes usados, objetos de arte. Beneficio global',
  `vat_travel_agency` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Regimen especial agencias de viajes',
  `vat_agriculture` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Regimen especial agricultura, ganaderia y pesca',
  `vat_gold` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Regimen especial oro de inversion, realizacion de operaciones que puedan tributar por este regimen',
  `vat_union_external` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Regimen exterior a la Union',
  `vat_union` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Regimen de la Union',
  `vat_importation` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Regimen de importacion',
  PRIMARY KEY (`invoice`),
  KEY `IDX_INVOICE_FISCAL_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICE_FISCAL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_FISCAL_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Info fiscal de facturas';

#
# Table structure for table `invoice_info`
#

CREATE TABLE `invoice_info` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `invoice` int NOT NULL COMMENT 'Identificador de la Factura',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT 'Tipo de Comunicacion',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Estado de la Comunicacion',
  PRIMARY KEY (`id`),
  KEY `IDX_INVOICE_STATUS_DOMAIN` (`domain`),
  KEY `IDX_INVOICE_STATUS_INVOICE` (`invoice`),
  CONSTRAINT `FK_INVOICE_STATUS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_STATUS_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Estado Comunicaciones de Facturas';

#
# Table structure for table `invoice_tax`
#

CREATE TABLE `invoice_tax` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Impuesto de la Factura',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `invoice_detail` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Detalle de la Factura',
  `tax_type` tinyint DEFAULT '0' COMMENT 'Tipo de Impuesto del Detalle de la Factura',
  `base` decimal(15,4) DEFAULT '0.0000' COMMENT 'Base Imponible de Impuesto del Detalle de la Factura',
  `percentage` decimal(15,3) DEFAULT '0.000' COMMENT 'Porcentaje de Impuesto del Detalle de la Factura',
  `surcharge` decimal(15,3) DEFAULT '0.000' COMMENT 'Porcentaje del recargo de equivalencia del Detalle de la Factura',
  `quota` decimal(15,4) DEFAULT '0' COMMENT 'Cuota de Impuesto del Detalle de la Factura',
  `surcharge_quota` decimal(15,4) DEFAULT '0' COMMENT 'Cuota de recargo de equivalencia del Detalle de la Factura',
  `vat_deduction_type` tinyint DEFAULT '0' COMMENT 'Tipo de deduccion del IVA',
  `withholding_type` tinyint DEFAULT '0' COMMENT 'Tipo de retencion',
  `deductible_percent` decimal(15,4) DEFAULT '0' COMMENT 'Porcentaje de deducibilidad',
  `deductible_quota` decimal(15,4) DEFAULT '0' COMMENT 'Cuota deducible',
  PRIMARY KEY (`id`),
  KEY `IDX_INVOICE_TAX_INVOICE_DETAIL` (`invoice_detail`),
  KEY `IDX_INVOICE_TAX_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICE_TAX_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_TAX_INVOICE_DETAIL` FOREIGN KEY (`invoice_detail`) REFERENCES `invoice_detail` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Impuestos del Detalle de la Factura';

#
# Table structure for table `invoice_tax_account`
#

CREATE TABLE `invoice_tax_account` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `invoice_tax` int NOT NULL DEFAULT '0' COMMENT 'Identificador de la Linea de Impuesto',
  `account` int NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY (`id`),
  KEY `IDX_INVOICE_TAX_ACCOUNT_ACCOUNT` (`account`),
  KEY `IDX_INVOICE_TAX_ACCOUNT_INVOICE_TAX` (`invoice_tax`),
  KEY `IDX_INVOICE_TAX_ACCOUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICE_TAX_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_INVOICE_TAX_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_TAX_ACCOUNT_INVOICE_TAX` FOREIGN KEY (`invoice_tax`) REFERENCES `invoice_tax` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables asociadas a Impuestos de Facturas';

#
# Table structure for table `invoicing_group`
#

CREATE TABLE `invoicing_group` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `customer` int NOT NULL COMMENT 'Identificador del Cliente',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Grupo de Facturacion',
  `customer_grouped` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Grupo de Facturacion agrupa los Clientes en una sola Factura',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_INVOICING_GROUP_DOMAIN` (`domain`),
  KEY `IDX_INVOICING_GROUP_CUSTOMER` (`customer`),
  CONSTRAINT `FK_INVOICING_GROUP_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_INVOICING_GROUP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Grupos de Facturacion';

#
# Table structure for table `irpf_data`
#

CREATE TABLE `irpf_data` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int NOT NULL COMMENT 'Identificador del contrato',
  `family_situation` tinyint DEFAULT '0' COMMENT 'Situacion familiar',
  `spouse_document` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Documento del conyuge',
  `disability_level` tinyint DEFAULT '0' COMMENT 'Grado de discapacidad',
  `dependence` tinyint(1) DEFAULT '0' COMMENT 'Dependencia de terceras personas',
  `moving_date` date DEFAULT NULL COMMENT 'Fecha de movilidad geografica',
  `labour_prolongation` tinyint(1) DEFAULT '0' COMMENT 'Prolongacion de la actividad laboral',
  `descendient_count` tinyint DEFAULT NULL COMMENT 'Numero de hijos',
  `start_date` date DEFAULT NULL COMMENT 'Fecha inicio del modelo',
  `end_date` date DEFAULT NULL COMMENT 'Fecha fin del modelo',
  `fiscal_exclusion` tinyint(1) DEFAULT '0' COMMENT 'Exclusion a la obligacion de tributar',
  `issue_date` date NOT NULL COMMENT 'Fecha de emisión',
  `annual_remuneration` decimal(15,3) DEFAULT NULL COMMENT 'Retribuciones totales (dinerarias y en especie). Importe íntegro',
  `irregular_18_2_reduction` decimal(15,3) DEFAULT NULL COMMENT 'Reducciones por irregularidad ( Atr. 18.2 LIRPF)',
  `irregular_18_3_reduction` decimal(15,3) DEFAULT NULL COMMENT 'Reducciones por irregularidad ( Atr. 18.3: Disposiciones transitorias 11ª y 12 ª de la LIRPF)',
  `deduccibles_expenses` decimal(15,3) DEFAULT NULL COMMENT 'Gastos deducibles ( Atr 19.2, letras a, b y c de la LINRPF: Seguridad Social, Mutualidades ...)',
  `spousal_support` decimal(15,3) DEFAULT NULL COMMENT 'Pension compensatoria a favor del cónyuge. Importe fijado judicialmente',
  `food_annuity` decimal(15,3) DEFAULT NULL COMMENT 'Anualidades por alimentos en favor de los hijos. Importe fijado judicialmente',
  `deduct_home_loan` tinyint DEFAULT NULL COMMENT 'Comunicación de pagos por la adquisión o rehabilitación de la vivienda habitual utilizando financiación ajena',
  `request_irpf` decimal(15,2) DEFAULT NULL COMMENT 'Tipo de retención solicitado',
  `contract_type` tinyint NOT NULL DEFAULT '0' COMMENT 'Contrato o relación',
  `ceuta_melilla` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Los datos anteriores corresponden a rendimientos obtenidos en Ceuta o Melilla',
  PRIMARY KEY (`id`),
  KEY `IDX_IRPF_DATA_CONTRACT` (`contract`),
  KEY `IDX_IRPF_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_IRPF_DATA_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_IRPF_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Dator de irpf';

#
# Table structure for table `irpf_data_ascendants`
#

CREATE TABLE `irpf_data_ascendants` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `irpf_data` int NOT NULL COMMENT 'Identificador del irpf',
  `birth_year` int DEFAULT NULL COMMENT 'Anio de nacimiento',
  `disability_level` tinyint DEFAULT '0' COMMENT 'Grado de discapacidad',
  `dependence` tinyint(1) DEFAULT '0' COMMENT 'Dependencia de terceras personas',
  `another_descendient` tinyint DEFAULT '0' COMMENT 'Convivencia con otros descendientes',
  PRIMARY KEY (`id`),
  KEY `IDX_IRPF_DATA_ASCENDANTS_IRPF_DATA` (`irpf_data`),
  KEY `IDX_IRPF_DATA_ASCENDANTS_DOMAIN` (`domain`),
  CONSTRAINT `FK_IRPF_DATA_ASCENDANTS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_IRPF_DATA_ASCENDANTS_IRPF_DATA` FOREIGN KEY (`irpf_data`) REFERENCES `irpf_data` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ascendientes del modelo 145';

#
# Table structure for table `irpf_data_descendients`
#

CREATE TABLE `irpf_data_descendients` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `irpf_data` int NOT NULL COMMENT 'Identificador del irpf',
  `birth_year` int DEFAULT NULL COMMENT 'Anio de nacimiento',
  `adoption_year` int DEFAULT NULL COMMENT 'Anio de adopcion',
  `disability_level` tinyint DEFAULT '0' COMMENT 'Grado de discapacidad',
  `dependence` tinyint(1) DEFAULT '0' COMMENT 'Dependencia de terceras personas',
  `unique_parent` tinyint(1) DEFAULT '0' COMMENT 'Computo por entero de hijos o descendientes',
  PRIMARY KEY (`id`),
  KEY `IDX_IRPF_DATA_DESCENDIENTS_IRPF_DATA` (`irpf_data`),
  KEY `IDX_IRPF_DATA_DESCENDIENTS_DOMAIN` (`domain`),
  CONSTRAINT `FK_IRPF_DATA_DESCENDIENTS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_IRPF_DATA_DESCENDIENTS_IRPF_DATA` FOREIGN KEY (`irpf_data`) REFERENCES `irpf_data` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Descendientes del modelo 145';

#
# Table structure for table `irpf_regularization`
#

CREATE TABLE `irpf_regularization` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int NOT NULL COMMENT 'Identificador del contrato',
  `reason` tinyint DEFAULT NULL COMMENT 'Causa de regularización',
  `effective_date` date NOT NULL COMMENT 'Fecha de entrada en vigor',
  `paid_irpf` decimal(15,3) DEFAULT NULL COMMENT 'Retenciones practicadas con anterioridad a la regularización.',
  `paid_remuneration` decimal(15,3) DEFAULT NULL COMMENT 'Retribuciones ya satisfechas con anterioridad a la regularización.',
  `prior_annual_irpf` decimal(15,3) DEFAULT NULL COMMENT 'Retenciones anuales anteriores a la regularización.',
  `prior_annual_remuneration` decimal(15,3) DEFAULT NULL COMMENT 'Retribucines anulaes consideradas con anterioridad a la regularización.',
  `prior_base_irpf` decimal(15,3) DEFAULT NULL COMMENT 'Base para calcular el tipo de retención determinado antes de la regularización.',
  `prior_irpf` decimal(15,2) DEFAULT NULL COMMENT 'Tipo de retención aplicado antes de la regularización.',
  `prior_in_ceuta_melilla` tinyint(1) DEFAULT NULL COMMENT 'Los rendimientos anteriores a la regularización fueron obtenidos en Ceuta o Melilla',
  `prior_minimun_personal_family` decimal(15,3) DEFAULT NULL COMMENT 'Mínimo personal y familiar para calcular el tipo de retención determinado antes de la regularización.',
  `prior_deduct_home_loan` tinyint DEFAULT NULL COMMENT 'En algún momento antes de la regularización se aplico la minoración por pagos por la adquisión o rehabilitación de la vivienda',
  `prior_deduct_home_loan_amount` decimal(15,3) DEFAULT NULL COMMENT 'Importe de la minoración por pagos por la adquisión o rehabilitación de la vivienda antes de la regularización',
  PRIMARY KEY (`id`),
  KEY `IDX_IRPF_REGULARIZATION_CONTRACT` (`contract`),
  KEY `IDX_IRPF_REGULARIZATION_DOMAIN` (`domain`),
  CONSTRAINT `FK_IRPF_REGULARIZATION_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_IRPF_REGULARIZATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos regularizacion IRPF';

#
# Table structure for table `irpf_result`
#

CREATE TABLE `irpf_result` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int NOT NULL COMMENT 'Identificador del contrato',
  `effective_date` date NOT NULL COMMENT 'Fecha de entrada en vigor',
  `base_irpf` decimal(15,3) DEFAULT NULL COMMENT 'Base para calcular el tipo de retención',
  `minimun_personal_family` decimal(15,3) DEFAULT NULL COMMENT 'Mínimo personal y familiar para calcular el tipo de retención',
  `deduct_home_loan_amount` decimal(15,3) DEFAULT NULL COMMENT 'Minoración por pagos de préstamo para vivienda habitual',
  `deduct_80_bis` decimal(15,3) DEFAULT NULL COMMENT 'Deduccion Arttículo 80 bis LIRPF',
  `irpf` decimal(15,2) DEFAULT NULL COMMENT 'Tipo retención apliclabe ',
  `annual_irpf` decimal(15,3) DEFAULT NULL COMMENT 'Importe anual de las retenciones e ingresos a cuenta',
  `annual_remuneration` decimal(15,3) DEFAULT NULL COMMENT 'Retribuciones anuales. Importe íntegro',
  `irregular_18_2_reduction` decimal(15,3) DEFAULT NULL COMMENT 'Reducciones por irregularidad ( Art. 18.2 LIRPF). Importe',
  `irregular_18_3_reduction` decimal(15,3) DEFAULT NULL COMMENT 'Reducciones por irregularidad ( Art. 18.3: DD.TT 11ª y 12 ª de la LIRPF). Importe',
  `deduccibles_expenses` decimal(15,3) DEFAULT NULL COMMENT 'Gastos deducibles. Importe anual',
  `work_remuneration_reduction` decimal(15,3) DEFAULT NULL COMMENT 'Reducciones por rendimiento del trabajo ',
  `work_prolongation_reduction` decimal(15,3) DEFAULT NULL COMMENT 'Reducciones por prolongación de la actividad ',
  `work_moving_reduction` decimal(15,3) DEFAULT NULL COMMENT 'Reducciones por movilidad geografica ',
  `work_disability_reduction` decimal(15,3) DEFAULT NULL COMMENT 'Reducciones por discapacidad ',
  `social_security_pensioner` decimal(15,3) DEFAULT NULL COMMENT 'Por ser pensionista de la s. social/cl. Pasivas o desempleado',
  `two_or_more_descendents_min` decimal(15,3) DEFAULT NULL COMMENT 'Por tener más de dos descendientes con derecho a mínimo',
  `spousal_support` decimal(15,3) DEFAULT NULL COMMENT 'Pension compensatoria a favor del cónyuge. Importe anual',
  `food_annuity` decimal(15,3) DEFAULT NULL COMMENT 'Anualidades por alimentos en favor de los hijos. Importe anual',
  `minimun_personal` decimal(15,3) DEFAULT NULL COMMENT 'Mínimo personal',
  `minimun_ascendents` decimal(15,3) DEFAULT NULL COMMENT 'Mínimo por descendientes',
  `minimun_descendents` decimal(15,3) DEFAULT NULL COMMENT 'Mínimo por descendientes',
  `minimun_disability` decimal(15,3) DEFAULT NULL COMMENT 'Mínimo por discapacidad',
  `descendents_minor_3_total` tinyint DEFAULT NULL COMMENT 'Descendientes computados menores de tres años. Total',
  `descendents_minor_3_entirely` tinyint DEFAULT NULL COMMENT 'Descendientes computados menores de tres años. Por entero',
  `descendents_remainder_total` tinyint DEFAULT NULL COMMENT 'Resto de descendientes computados . Total',
  `descendents_remainder_entirely` tinyint DEFAULT NULL COMMENT 'Resto de descendientes computados . Por entero',
  `descendents_33_65_total` tinyint DEFAULT NULL COMMENT 'Descendientes con discapacidad >= 33% y < 65%. Total',
  `descendents_33_65_entirely` tinyint DEFAULT NULL COMMENT 'Descendientes con discapacidad >= 33% y < 65%. Por entero',
  `descendents_moving_total` tinyint DEFAULT NULL COMMENT 'Descendientes con discapacidad, movilidad reducida. Total',
  `descendents_moving_entirely` tinyint DEFAULT NULL COMMENT 'Descendientes con discapacidad, movilidad reducida. Por entero',
  `descendents_65_total` tinyint DEFAULT NULL COMMENT 'Descendientes con discapacidad > 65%. Total',
  `descendents_65_entirely` tinyint DEFAULT NULL COMMENT 'Descendientes con discapacidad > 65%. Por entero',
  `descendents_first` tinyint DEFAULT NULL COMMENT 'Detalle del cómputo de descendientes. Hijo 1º',
  `descendents_second` tinyint DEFAULT NULL COMMENT 'Detalle del cómputo de descendientes. Hijo 2º',
  `descendents_third` tinyint DEFAULT NULL COMMENT 'Detalle del cómputo de descendientes. Hijo 3º',
  `descendents_fourth_subsequent_total` tinyint DEFAULT NULL COMMENT 'Detalle del cómputo de descendientes. Hijo 4º y sucesivos. Total',
  `descendents_fourth_subsequent_entirely` tinyint DEFAULT NULL COMMENT 'Detalle del cómputo de descendientes. Hijo 4º y sucesivos. Por entero',
  `ascendents_minor_75_total` tinyint DEFAULT NULL COMMENT 'Ascendientes computados menores de 75 años. Total',
  `ascendents_minor_75_entirely` tinyint DEFAULT NULL COMMENT 'Ascendientes computados menores de 75 años. Por entero',
  `ascendents_mayor_75_total` tinyint DEFAULT NULL COMMENT 'Ascendientes computados mayores de 75 años. Total',
  `ascendents_mayor_75_entirely` tinyint DEFAULT NULL COMMENT 'Ascendientes computados mayores de 75 años. Por entero',
  `ascendents_33_65_total` tinyint DEFAULT NULL COMMENT 'Ascendientes con discapacidad >= 33% y < 65%. Total',
  `ascendents_33_65_entirely` tinyint DEFAULT NULL COMMENT 'Ascendientes con discapacidad >= 33% y < 65%. Por entero',
  `ascendents_moving_total` tinyint DEFAULT NULL COMMENT 'Ascendientes con discapacidad, movilidad reducida. Total',
  `ascendents_moving_entirely` tinyint DEFAULT NULL COMMENT 'Ascendientes con discapacidad, movilidad reducida. Por entero',
  `ascendents_65_total` tinyint DEFAULT NULL COMMENT 'Ascendientes con discapacidad > 65%. Total',
  `ascendents_65_entirely` tinyint DEFAULT NULL COMMENT 'Ascendientes con discapacidad > 65%. Por entero',
  PRIMARY KEY (`id`),
  KEY `IDX_IRPF_RESULT_CONTRACT` (`contract`),
  KEY `IDX_IRPF_RESULT_DOMAIN` (`domain`),
  CONSTRAINT `FK_IRPF_RESULT_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_IRPF_RESULT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Resultados IRPF';

#
# Table structure for table `item`
#

CREATE TABLE `item` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Articulo',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `product` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Producto',
  `detail` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Detalle del Articulo',
  `detail2` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Detalle 2 del Articulo',
  `detail3` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Detalle 3 del Articulo',
  `description` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Descripcion del Articulo',
  `serial_number` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de serie',
  `serial_date` date DEFAULT NULL COMMENT 'Fecha de serializacion',
  `expire_date` date DEFAULT NULL Comment 'Fecha de caducidad del articulo',
  `price` decimal(15,4) DEFAULT '0' COMMENT 'Precio del Articulo',
  `status` tinyint DEFAULT '0' COMMENT 'Estado del Articulo',
  `expenses_percent` decimal(15,4) DEFAULT '0' COMMENT 'Gastos porcentuales del Articulo',
  `expenses_fixed` decimal(15,4) DEFAULT '0' COMMENT 'Gastos fijos del Articulo',
  `profit_percent` decimal(15,4) DEFAULT '0' COMMENT 'Porcentaje de beneficio del Articulo',
  `purchase_price` decimal(15,4) DEFAULT '0' COMMENT 'Precio de compra del Articulo',
  `internet` tinyint(1) DEFAULT '0' COMMENT 'Visible en internet',
  `barcode` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de barras del Articulo',
  `pack_format_tag` int DEFAULT NULL COMMENT 'Identificador de la Etiqueta de formato',
  `pack_units` int DEFAULT '0' COMMENT 'Numero de unidades por formato',
  `pack_units_tag` int DEFAULT NULL COMMENT 'Identificador de la Etiqueta de unidad de envase',
  `pack_measurement` decimal(15,4) DEFAULT '0' COMMENT 'Medida envasada',
  `pack_measurement_tag` int DEFAULT NULL COMMENT 'Identificador de la Etiqueta de unidad de medida',
  `stock_unit_tag` int DEFAULT NULL COMMENT 'Identificador de la Etiqueta de unidad de Stock',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_ITEM_DOMAIN_BARCODE` (`domain`,`barcode`),
  KEY `IDX_ITEM_PRODUCT` (`product`),
  KEY `IDX_ITEM_DOMAIN` (`domain`),
  KEY `IDX_ITEM_TAG_PACK_FORMAT` (`pack_format_tag`),
  KEY `IDX_ITEM_TAG_PACK_UNITS` (`pack_units_tag`),
  KEY `IDX_ITEM_TAG_PACK_MEASUREMENT` (`pack_measurement_tag`),
  KEY `IDX_ITEM_TAG_STOCK_UNIT` (`stock_unit_tag`),
  CONSTRAINT `FK_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ITEM_PRODUCT` FOREIGN KEY (`product`) REFERENCES `product` (`id`),
  CONSTRAINT `FK_ITEM_TAG_PACK_FORMAT` FOREIGN KEY (`pack_format_tag`) REFERENCES `tag` (`id`),
  CONSTRAINT `FK_ITEM_TAG_PACK_MEASUREMENT` FOREIGN KEY (`pack_measurement_tag`) REFERENCES `tag` (`id`),
  CONSTRAINT `FK_ITEM_TAG_PACK_UNITS` FOREIGN KEY (`pack_units_tag`) REFERENCES `tag` (`id`),
  CONSTRAINT `FK_ITEM_TAG_STOCK_UNIT` FOREIGN KEY (`stock_unit_tag`) REFERENCES `tag` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulos';

#
# Table structure for table `item_addinfo`
#

CREATE TABLE `item_addinfo` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `product` int NOT NULL COMMENT 'Identificador del Producto',
  `item` int NOT NULL COMMENT 'Identificador de Articulo',
  `attribute` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Atributo adicional',
  `value` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Valor del atributo adicional',
  `value_date` date NOT NULL COMMENT 'Fecha del valor del atributo',
  PRIMARY KEY (`id`),
  KEY `IDX_ITEM_ADDINFO_DOMAIN` (`domain`),
  KEY `IDX_ITEM_ADDINFO_ITEM` (`item`),
  KEY `IDX_ITEM_ADDINFO_PRODUCT` (`product`),
  CONSTRAINT `FK_ITEM_ADDINFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ITEM_ADDINFO_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_ADDINFO_PRODUCT` FOREIGN KEY (`product`) REFERENCES `product` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Informacion adicional del Articulo';

#
# Table structure for table `item_alternative`
#

CREATE TABLE `item_alternative` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `item` int NOT NULL DEFAULT '0' COMMENT 'Identificador de Articulo',
  `alternative_item` int NOT NULL DEFAULT '0' COMMENT 'Identificador de Articulo Alternativo',
  `priority` tinyint DEFAULT '0' COMMENT 'Prioridad del Articulo Alternativo',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_ITEM_ALTERNATIVE` (`item`,`alternative_item`),
  KEY `IDX_ITEM_ALTERNATIVE_ITEM` (`item`),
  KEY `IDX_ITEM_ALTERNATIVE_ALTERNATIVE` (`alternative_item`),
  KEY `IDX_ITEM_ALTERNATIVE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ITEM_ALTERNATIVE_ALTERNATIVE` FOREIGN KEY (`alternative_item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_ALTERNATIVE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ITEM_ALTERNATIVE_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulo Alternativos';

#
# Table structure for table `item_composition`
#

CREATE TABLE `item_composition` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `item` int NOT NULL COMMENT 'Identificador del Articulo compuesto',
  `composition_item` int NOT NULL COMMENT 'Identificador del Articulo componente',
  `sequence` smallint DEFAULT '0' COMMENT 'Numero de secuencia dentro de la Composicion',
  `description` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del componente',
  `quantity` decimal(15,3) DEFAULT '0.000' COMMENT 'Cantidad del componente',
  `discount_expr` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descuentos del componente',
  PRIMARY KEY (`id`),
  KEY `IDX_ITEM_COMPOSITION_ITEM` (`item`),
  KEY `IDX_ITEM_COMPOSITION_COMPOSITION` (`composition_item`),
  KEY `IDX_ITEM_COMPOSITION_DOMAIN` (`domain`),
  CONSTRAINT `FK_ITEM_COMPOSITION_COMPOSITION` FOREIGN KEY (`composition_item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_COMPOSITION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ITEM_COMPOSITION_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Composicion de Articulos';

#
# Table structure for table `item_tariff`
#

CREATE TABLE `item_tariff` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `item` int NOT NULL DEFAULT '0' COMMENT 'Identificador de Articulo',
  `tariff` int NOT NULL DEFAULT '0' COMMENT 'Identificador de Tarifa',
  `type` tinyint DEFAULT '0' COMMENT 'Tipo de Tarifa',
  `profit_percent` decimal(15,4) DEFAULT '0' COMMENT 'Porcentaje de beneficio',
  `price` decimal(15,4) DEFAULT '0' COMMENT 'Precio de Venta',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_ITEM_TARIFF` (`item`,`tariff`),
  KEY `IDX_ITEM_TARIFF_TARIFF` (`tariff`),
  KEY `IDX_ITEM_TARIFF_ITEM` (`item`),
  KEY `IDX_ITEM_TARIFF_DOMAIN` (`domain`),
  CONSTRAINT `FK_ITEM_TARIFF_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ITEM_TARIFF_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_TARIFF_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas de Articulos';

#
# Table structure for table `item_warehouse`
#

CREATE TABLE `item_warehouse` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `item` int NOT NULL DEFAULT '0' COMMENT 'Identificador de Articulo',
  `warehouse` int NOT NULL DEFAULT '0' COMMENT 'Identificador de Almacen',
  `stock_max` decimal(15,3) DEFAULT '0.000' COMMENT 'Stock maximo del Articulo en el Almacen',
  `stock_min` decimal(15,3) DEFAULT '0.000' COMMENT 'Stock minimo del Articulo en el Almacen',
  `location` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Localizacion del Articulo en el Almacen',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_ITEM_WAREHOUSE` (`item`,`warehouse`),
  KEY `IDX_ITEM_WAREHOUSE_ITEM` (`item`),
  KEY `IDX_ITEM_WAREHOUSE_WAREHOUSE` (`warehouse`),
  KEY `IDX_ITEM_WAREHOUSE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ITEM_WAREHOUSE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ITEM_WAREHOUSE_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_WAREHOUSE_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos del Articulo por Almacen';

#
# Table structure for table `job_type`
#

CREATE TABLE `job_type` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Tipo de Trabajo',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Trabajo',
  PRIMARY KEY (`id`),
  KEY `IDX_JOB_TYPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_JOB_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Trabajos';

#
# Table structure for table `leave_batch`
#

CREATE TABLE `leave_batch` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `date` datetime DEFAULT NULL COMMENT 'Fecha de la Remesa',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT 'Indica el estado de la remesa',
  `communication_id` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Identificador resultante de la comunicacion',
  `income_file` mediumblob COMMENT 'Archivo respuesta en binario',
  `income_file_date` datetime DEFAULT NULL COMMENT 'Fecha de respuesta',
  `outcome_file` mediumblob COMMENT 'Archivo Adjunto en binario',
  `outcome_file_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  PRIMARY KEY (`id`),
  KEY `IDX_LEAVE_BATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_LEAVE_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas de partes IT';

#
# Table structure for table `leave_batch_detail`
#

CREATE TABLE `leave_batch_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `leave_batch` int NOT NULL COMMENT 'Identificador unico de la remesa',
  `contract_leave_detail` int NOT NULL COMMENT 'Identificador unico del parte',
  PRIMARY KEY (`id`),
  KEY `IDX_LEAVE_BATCH_DETAIL_LEAVE_BATCH` (`leave_batch`),
  KEY `IDX_LEAVE_BATCH_DETAIL_CONTRACT_LEAVE_DETAIL` (`contract_leave_detail`),
  KEY `IDX_LEAVE_BATCH_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_LEAVE_BATCH_DETAIL_CONTRACT_LEAVE_DETAIL` FOREIGN KEY (`contract_leave_detail`) REFERENCES `contract_leave_detail` (`id`),
  CONSTRAINT `FK_LEAVE_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_LEAVE_BATCH_DETAIL_LEAVE_BATCH` FOREIGN KEY (`leave_batch`) REFERENCES `leave_batch` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de remesas de partes IT';

#
# Table structure for table `loan`
#

CREATE TABLE `loan` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador Unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Prestamo',
  `loan_date` date NOT NULL COMMENT 'Fecha de Concesion del Prestamo',
  `term` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Plazo',
  `interest` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Interes',
  `review` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Revision',
  `amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe',
  `expenses` decimal(15,3) DEFAULT '0.000' COMMENT 'Gastos asociados al Prestamo',
  `rbank` int NOT NULL COMMENT 'Banco por el que se paga el Prestamo',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de Seguridad',
  `fee_amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe de la cuota',
  `recurrence` int DEFAULT '0' COMMENT 'Periodicidad',
  `pay_day` int DEFAULT '1' COMMENT 'Dia de Pago',
  `status` tinyint DEFAULT '0' COMMENT 'Estado',
  `account` int DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY (`id`),
  KEY `IDX_LOAN_RBANK` (`rbank`),
  KEY `IDX_LOAN_DOMAIN` (`domain`),
  KEY `IDX_LOAN_ACCOUNT` (`account`),
  CONSTRAINT `FK_LOAN_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_LOAN_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_LOAN_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Prestamos';

#
# Table structure for table `location`
#

CREATE TABLE `location` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',
  `domain` int NOT NULL COMMENT 'Dominio',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripción de la Ubicación',
  `radio` int DEFAULT '50' COMMENT 'Radio de la Ubicación',
  `latitude` double DEFAULT NULL COMMENT 'Latitud de las coordenadas.',
  `longitude` double DEFAULT NULL COMMENT 'Longitud de las coordenadas.',
  PRIMARY KEY (`id`),
  KEY `IDX_LOCATION_DOMAIN` (`domain`),
  CONSTRAINT `FK_LOCATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ubicación';

#
# Table structure for table `mail_account`
#

CREATE TABLE `mail_account` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Cuenta de Correo',
  `email` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Cuenta de correo',
  `replyto_mail` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Email de Respuesta',
  `incoming_host` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Host del correo entrante',
  `protocol` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Protocolo utilizado (IMAP)',
  `incoming_port` int DEFAULT NULL COMMENT 'Puerto del correo entrante',
  `incoming_security` tinyint NOT NULL DEFAULT '0' COMMENT 'Seguridad de conexin del correo entrante',
  `outgoing_verification` tinyint(1) DEFAULT '1' COMMENT 'Indica si hay autentificacion en el correo saliente',
  `outgoing_host` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Host del servidor de correo saliente',
  `outgoing_port` int DEFAULT NULL COMMENT 'Puerto del servidor de correo saliente',
  `outgoing_security` tinyint NOT NULL DEFAULT '0' COMMENT 'Seguridad de conexin del correo saliente',
  `mail_username` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del usuario',
  `password` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Clave del usuario',
  `default_account` tinyint(1) DEFAULT '0' COMMENT 'Indica si es la cuenta de correo por defecto',
  `draft_folder` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Ruta de Borrador',
  `sent_folder` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Ruta de Enviados',
  `trash_folder` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Ruta de Papelera',
  `spam_folder` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Ruta de Spam',
  `display_name` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mostrar como',
  `signature` int DEFAULT NULL COMMENT 'Identificador de la Firma',
  `user_id` int DEFAULT NULL COMMENT 'Identificador del Usuario',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT 'Tipo de la Cuenta de Correo',
  PRIMARY KEY (`id`),
  KEY `IDX_MAIL_ACCOUNT_SIGNATURE` (`signature`),
  KEY `IDX_MAIL_ACCOUNT_DOMAIN` (`domain`),
  KEY `IDX_MAIL_ACCOUNT_USER` (`user_id`),
  CONSTRAINT `FK_MAIL_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_MAIL_ACCOUNT_SIGNATURE` FOREIGN KEY (`signature`) REFERENCES `signature` (`id`),
  CONSTRAINT `FK_MAIL_ACCOUNT_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas de Correo Electronico';

#
# Table structure for table `make`
#

CREATE TABLE `make` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Fabricante',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Fabricante',
  PRIMARY KEY (`id`),
  KEY `IDX_MAKE_DOMAIN` (`domain`),
  CONSTRAINT `FK_MAKE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Fabricantes';

#
# Table structure for table `mark`
#

CREATE TABLE `mark` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `subject` int NOT NULL COMMENT 'Identificador de Asignatura',
  `alumn` int NOT NULL COMMENT 'Identificador de Alumno',
  `evaluation` tinyint NOT NULL COMMENT 'Numero de evaluacion',
  `mark` decimal(15,3) DEFAULT '0.000' COMMENT 'Nota',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_MARK_SUBJECT_ALUMN_EVALUATION` (`subject`,`alumn`,`evaluation`),
  KEY `IDX_MARK_ALUMN` (`alumn`),
  KEY `IDX_MARK_SUBJECT` (`subject`),
  KEY `IDX_MARK_DOMAIN` (`domain`),
  CONSTRAINT `FK_MARK_ALUMN` FOREIGN KEY (`alumn`) REFERENCES `course_alumn` (`id`),
  CONSTRAINT `FK_MARK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_MARK_SUBJECT` FOREIGN KEY (`subject`) REFERENCES `course_academicskill` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Notas de Alumnos';

#
# Table structure for table `mk_action`
#

CREATE TABLE `mk_action` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `campaign` int NOT NULL COMMENT 'Identificador de la Campaña',
  `media_type` int NOT NULL COMMENT 'Tipo de contacto de la Accion',
  `start_date` datetime NOT NULL COMMENT 'Fecha de inicio',
  `end_date` datetime DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `survey` int DEFAULT NULL COMMENT 'Identificador del Cuestionario',
  `newsletter` int DEFAULT NULL COMMENT 'Identificador del Boletin',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de la Accion',
  `news` int DEFAULT NULL COMMENT 'Identificador de la Noticia',
  PRIMARY KEY (`id`),
  KEY `IDX_MK_ACTION_MK_CAMPAIGN` (`campaign`),
  KEY `IDX_MK_ACTION_SURVEY` (`survey`),
  KEY `IDX_MK_ACTION_DOMAIN` (`domain`),
  KEY `IDX_MK_ACTION_NEWSLETTER` (`newsletter`),
  KEY `IDX_MK_ACTION_NEWS` (`news`),
  CONSTRAINT `FK_MK_ACTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_MK_ACTION_MK_CAMPAIGN` FOREIGN KEY (`campaign`) REFERENCES `mk_campaign` (`id`),
  CONSTRAINT `FK_MK_ACTION_NEWS` FOREIGN KEY (`news`) REFERENCES `news` (`id`),
  CONSTRAINT `FK_MK_ACTION_NEWSLETTER` FOREIGN KEY (`newsletter`) REFERENCES `newsletter` (`id`),
  CONSTRAINT `FK_MK_ACTION_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Acciones de Marketing';

#
# Table structure for table `mk_action_target`
#

CREATE TABLE `mk_action_target` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `action` int NOT NULL COMMENT 'Identificador de la Accion',
  `target` int NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `status` tinyint NOT NULL COMMENT 'Estado del Cliente Potencial de la Accion de Campaña',
  `survey_response` int DEFAULT NULL COMMENT 'Identificador de la Respuesta de Cuestionario',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  `user` int DEFAULT NULL COMMENT 'Identificador del Usuario',
  PRIMARY KEY (`id`),
  KEY `IDX_MK_ACTION_TARGET_USER` (`user`),
  KEY `IDX_MK_ACTION_TARGET_SURVEY_RESPONSE` (`survey_response`),
  KEY `IDX_MK_ACTION_TARGET_MK_ACTION` (`action`),
  KEY `IDX_MK_ACTION_TARGET_TARGET` (`target`),
  KEY `IDX_MK_ACTION_TARGET_DOMAIN` (`domain`),
  CONSTRAINT `FK_MK_ACTION_TARGET_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_MK_ACTION_TARGET_MK_ACTION` FOREIGN KEY (`action`) REFERENCES `mk_action` (`id`),
  CONSTRAINT `FK_MK_ACTION_TARGET_SURVEY_RESPONSE` FOREIGN KEY (`survey_response`) REFERENCES `survey_response` (`id`),
  CONSTRAINT `FK_MK_ACTION_TARGET_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_MK_ACTION_TARGET_USER` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Clientes Potenciales de la Accion de Marketing';

#
# Table structure for table `mk_campaign`
#

CREATE TABLE `mk_campaign` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Campaña esta activa o no',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Campaña',
  `scope` int NOT NULL COMMENT 'Identificador del Ambito',
  PRIMARY KEY (`id`),
  KEY `IDX_MK_CAMPAIGN_DOMAIN` (`domain`),
  KEY `IDX_MK_CAMPAIGN_SCOPE` (`scope`),
  CONSTRAINT `FK_MK_CAMPAIGN_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_MK_CAMPAIGN_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Campañas de Marketing';

#
# Table structure for table `mk_template`
#

CREATE TABLE `mk_template` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `scope` int NOT NULL COMMENT 'Identificador del Ambito',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Plantilla',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Plantilla esta activa o no',
  `creationDate` datetime NOT NULL COMMENT 'Fecha de la creacion en el sistema de la Plantilla',
  `subject` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Asunto de la Plantilla',
  `width` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Ancho de la Plantilla',
  `title_color` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Color del titulo de la Plantilla',
  `background_color` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Color de fondo de la Plantilla',
  `header_template` int DEFAULT NULL COMMENT 'Identificador de la Plantilla de Cabecera',
  `footer_template` int DEFAULT NULL COMMENT 'Identificador de la Plantilla de Pie de Pagina',
  PRIMARY KEY (`id`),
  KEY `IDX_MK_TEMPLATE_DOMAIN` (`domain`),
  KEY `IDX_MK_TEMPLATE_SCOPE` (`scope`),
  KEY `IDX_MK_TEMPLATE_HEADER_TEMPLATE` (`header_template`),
  KEY `IDX_MK_TEMPLATE_FOOTER_TEMPLATE` (`footer_template`),
  CONSTRAINT `FK_MK_TEMPLATE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_MK_TEMPLATE_FOOTER_TEMPLATE` FOREIGN KEY (`footer_template`) REFERENCES `rattach` (`id`),
  CONSTRAINT `FK_MK_TEMPLATE_HEADER_TEMPLATE` FOREIGN KEY (`header_template`) REFERENCES `rattach` (`id`),
  CONSTRAINT `FK_MK_TEMPLATE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Plantilla de Marketing';

#
# Table structure for table `model`
#

CREATE TABLE `model` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Modelo',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `make` int NOT NULL COMMENT 'Identificador del Fabricante',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Modelo',
  PRIMARY KEY (`id`),
  KEY `IDX_MODEL_MAKE` (`make`),
  KEY `IDX_MODEL_DOMAIN` (`domain`),
  CONSTRAINT `FK_MODEL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_MODEL_MAKE` FOREIGN KEY (`make`) REFERENCES `make` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Modelos';

#
# Table structure for table `news`
#

CREATE TABLE `news` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `title` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Titulo de la Noticia',
  `description` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de la Noticia',
  `content` text CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Contenido de la Noticia',
  `url` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Url de la Noticia',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si la Noticia esta activa o no',
  `rss` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si la Noticia se va a publicar en rss o no',
  `init_date` datetime DEFAULT NULL COMMENT 'Fecha Noticia',
  `end_date` datetime DEFAULT NULL COMMENT 'Fecha fin Noticia',
  `category` int DEFAULT NULL COMMENT 'Categoria de la Noticia',
  `rattach` int DEFAULT NULL COMMENT 'Identificador del Archivo Adjunto',
  `scope` int NOT NULL COMMENT 'Identificador del Ambito',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT 'Tipo de Contenido',
  `template` int DEFAULT NULL COMMENT 'Identificador de la Plantilla de Marketing',
  PRIMARY KEY (`id`),
  KEY `IDX_NEWS_DOMAIN` (`domain`),
  KEY `IDX_NEWS_CATEGORY` (`category`),
  KEY `IDX_NEWS_RATTACH` (`rattach`),
  KEY `IDX_NEWS_SCOPE` (`scope`),
  KEY `IDX_NEWS_MK_TEMPLATE` (`template`),
  CONSTRAINT `FK_NEWS_CATEGORY` FOREIGN KEY (`category`) REFERENCES `category` (`id`),
  CONSTRAINT `FK_NEWS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_NEWS_MK_TEMPLATE` FOREIGN KEY (`template`) REFERENCES `mk_template` (`id`),
  CONSTRAINT `FK_NEWS_RATTACH` FOREIGN KEY (`rattach`) REFERENCES `rattach` (`id`),
  CONSTRAINT `FK_NEWS_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Noticias';

#
# Table structure for table `newsletter`
#

CREATE TABLE `newsletter` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Boletin',
  `date` datetime NOT NULL COMMENT 'Fecha del Boletin',
  `layout` tinyint NOT NULL DEFAULT '0' COMMENT 'Disposicion del Boletin',
  `active` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Boletin esta activo o no',
  `subject` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Asunto del Boletin',
  `scope` int NOT NULL COMMENT 'Identificador del Ambito',
  `highlightFirst` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Boletin destaca la primera noticia o no',
  `template` int DEFAULT NULL COMMENT 'Identificador de la Plantilla de Marketing',
  `newsSeparator` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Boletin incluye un separador entre noticias',
  PRIMARY KEY (`id`),
  KEY `IDX_NEWSLETTER_DOMAIN` (`domain`),
  KEY `IDX_NEWSLETTER_SCOPE` (`scope`),
  KEY `IDX_NEWSLETTER_MK_TEMPLATE` (`template`),
  CONSTRAINT `FK_NEWSLETTER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_NEWSLETTER_MK_TEMPLATE` FOREIGN KEY (`template`) REFERENCES `mk_template` (`id`),
  CONSTRAINT `FK_NEWSLETTER_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Boletin';

#
# Table structure for table `newsletter_detail`
#

CREATE TABLE `newsletter_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `news` int NOT NULL DEFAULT '0' COMMENT 'Identificador de la Noticia',
  `newsletter` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Boletin',
  `position` int NOT NULL COMMENT 'Posicion dentro del Boletin',
  PRIMARY KEY (`id`),
  KEY `IDX_NEWSLETTER_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_NEWSLETTER_DETAIL_NEWS` (`news`),
  KEY `IDX_NEWSLETTER_DETAIL_NEWSLETTER` (`newsletter`),
  CONSTRAINT `FK_NEWSLETTER_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_NEWSLETTER_DETAIL_NEWS` FOREIGN KEY (`news`) REFERENCES `news` (`id`),
  CONSTRAINT `FK_NEWSLETTER_DETAIL_NEWSLETTER` FOREIGN KEY (`newsletter`) REFERENCES `newsletter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Noticias del Boletin';

#
# Table structure for table `note`
#

CREATE TABLE `note` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Nota',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `subject` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion corta de la Nota',
  `date` datetime NOT NULL COMMENT 'Fecha de la Nota',
  `owner` int DEFAULT NULL COMMENT 'Destinatario de la Nota',
  `note` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Texto de la Nota',
  `archive` tinyint(4) DEFAULT 0 COMMENT 'Indica si la nota esta o no archivada',
  `tag` int(11) DEFAULT NULL COMMENT 'Identificador de la etiqueta',
  PRIMARY KEY (`id`),
  KEY `IDX_NOTE_USER` (`owner`),
  KEY `IDX_NOTE_DOMAIN` (`domain`),
  KEY `IDX_NOTE_TAG` (`tag`),
  CONSTRAINT `FK_NOTE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_NOTE_TAG` FOREIGN KEY (`tag`) REFERENCES `tag` (`id`),
  CONSTRAINT `FK_NOTE_USER` FOREIGN KEY (`owner`) REFERENCES `user` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Notas';

#
# Table structure for table `notice`
#

CREATE TABLE `notice` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Aviso',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `date` datetime NOT NULL COMMENT 'Fecha y hora en la que se produjo el Aviso',
  `sender` int NOT NULL COMMENT 'Remitente del Aviso',
  `work_group` int DEFAULT NULL COMMENT 'Grupo de Trabajo al que va dirigida el Aviso',
  `recipient` int DEFAULT NULL COMMENT 'Destinatario del Aviso',
  `source` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Origen del Aviso',
  `company` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Empresa para la que trabaja el origen del Aviso',
  `phone` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono para contactar con el origen del Aviso',
  `subject` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Asunto del Aviso',
  `status` tinyint NOT NULL COMMENT 'Estado del Aviso',
  `type` tinyint NOT NULL COMMENT 'Tipo de Aviso',
  `priority` tinyint NOT NULL COMMENT 'Prioridad del Aviso',
  `notice` int DEFAULT NULL COMMENT 'Aviso al que referencia',
  PRIMARY KEY (`id`),
  KEY `IDX_NOTICE_USER_SENDER` (`sender`),
  KEY `IDX_NOTICE_USER_RECIPIENT` (`recipient`),
  KEY `IDX_NOTICE_WORKGROUP` (`work_group`),
  KEY `IDX_NOTICE_DOMAIN` (`domain`),
  CONSTRAINT `FK_NOTICE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_NOTICE_USER_RECIPIENT` FOREIGN KEY (`recipient`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_NOTICE_USER_SENDER` FOREIGN KEY (`sender`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_NOTICE_WORKGROUP` FOREIGN KEY (`work_group`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Avisos';

#
# Table structure for table `notice_tag`
#

CREATE TABLE `notice_tag` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `notice` int NOT NULL COMMENT 'Identificador del Aviso',
  `tag` int NOT NULL COMMENT 'Identificador de la Etiqueta',
  `start_date` datetime NOT NULL COMMENT 'Fecha y hora de la apertura del Aviso',
  `end_date` datetime DEFAULT NULL COMMENT 'Fecha y hora de cierre del Aviso',
  `user` int DEFAULT NULL COMMENT 'Identificador del Usuario',
  PRIMARY KEY (`id`),
  KEY `IDX_NOTICE_TAG_NOTICE` (`notice`),
  KEY `IDX_NOTICE_TAG_TAG` (`tag`),
  KEY `IDX_NOTICE_TAG_USER` (`user`),
  CONSTRAINT `FK_NOTICE_TAG_NOTICE` FOREIGN KEY (`notice`) REFERENCES `notice` (`id`),
  CONSTRAINT `FK_NOTICE_TAG_TAG` FOREIGN KEY (`tag`) REFERENCES `tag` (`id`),
  CONSTRAINT `FK_NOTICE_TAG_USER` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Avisos y Etiquetas';

#
# Table structure for table `notification`
#

CREATE TABLE `notification` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',
  `domain` int NOT NULL COMMENT 'Dominio',
  `date` datetime DEFAULT NULL COMMENT 'Fecha de la notificacion',
  `title` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Titulo de la notificacion',
  `body` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Mensaje de la notificacion',
  `source` tinyint DEFAULT NULL COMMENT 'Tipo de proceso',
  `source_id` int DEFAULT NULL COMMENT 'ID del proceso',
  `sender` binary(16) DEFAULT NULL COMMENT 'ID de AUTH',
  `priority` tinyint DEFAULT '0' COMMENT 'Prioridad de la notificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_NOTIFICATION_DOMAIN` (`domain`),
  CONSTRAINT `FK_NOTIFICATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Notification';

#
# Table structure for table `notification_receiver`
#

CREATE TABLE `notification_receiver` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',
  `domain` int NOT NULL COMMENT 'Dominio',
  `notification` int NOT NULL COMMENT 'ID unico de notification',
  `auth` binary(16) DEFAULT NULL COMMENT 'ID de AUTH',
  `status` tinyint DEFAULT '0' COMMENT 'Estado de la notificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_NOTIFICATION_RECEIVER_NOTIFICATION` (`notification`),
  KEY `IDX_NOTIFICATION_RECEIVER_DOMAIN` (`domain`),
  CONSTRAINT `FK_NOTIFICATION_RECEIVER` FOREIGN KEY (`notification`) REFERENCES `notification` (`id`),
  CONSTRAINT `FK_NOTIFICATION_RECEIVER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Notification receiver';

#
# Table structure for table `observation`
#

CREATE TABLE `observation` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Observacion',
  `domain` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `description` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Observacion',
  PRIMARY KEY (`id`),
  KEY `IDX_OBSERVATION_DOMAIN` (`domain`),
  CONSTRAINT `FK_OBSERVATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Observaciones';

#
# Table structure for table `offer`
#

CREATE TABLE `offer` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Presupuesto',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `project` int DEFAULT NULL COMMENT 'Identificador del Proyecto',
  `target` int NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `series` char(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie del Presupuesto',
  `number` int NOT NULL COMMENT 'Numero del Presupuesto',
  `version` smallint NOT NULL DEFAULT '0' COMMENT 'Numero de version de Presupuesto',
  `address` int DEFAULT NULL COMMENT 'Identificador de la Direccion de envio del Presupuesto',
  `seller` int DEFAULT NULL COMMENT 'Agente Comercial del Presupuesto',
  `supplier` int DEFAULT NULL COMMENT 'Identificador del Proveedor',
  `discount_expr` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descuentos del Presupuesto',
  `issue_date` date DEFAULT NULL COMMENT 'Fecha de emision del Presupuesto',
  `pay_method` int DEFAULT NULL COMMENT 'Forma de Pago del Presupuesto',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad del Presupuesto',
  `status` tinyint DEFAULT '0' COMMENT 'Estado del Presupuesto',
  `type` tinyint DEFAULT '0' COMMENT 'Tipo de Presupuesto',
  `workplace` int NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `scope` int NOT NULL DEFAULT '1' COMMENT 'Ambito del Presupuesto',
  `number_of_pymnts` smallint DEFAULT '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint DEFAULT '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint DEFAULT '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Dias de pago',
  `bank_account` varchar(34) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'IBAN - Numero de Cuenta Bancaria Internacional',
  `bank_alias` varchar(25) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias del Banco',
  `bic` varchar(11) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'BIC - Codigo Identificador del Banco',
  `signed` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Presupuesto esta firmada electronicamente',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios del Presupuesto',
  `remarks` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Observaciones del Presupuesto',
  `external_reference` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Referencia externa',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_OFFER_DOMAIN_SERIES_NUMBER_VERSION` (`domain`,`series`,`number`,`version`),
  KEY `IDX_OFFER_SCOPE` (`scope`),
  KEY `IDX_OFFER_SUPPLIER` (`supplier`),
  KEY `IDX_OFFER_RADDRESS` (`address`),
  KEY `IDX_OFFER_PROJECT` (`project`),
  KEY `IDX_OFFER_ISSUE_DATE` (`issue_date`),
  KEY `IDX_OFFER_TARGET` (`target`),
  KEY `IDX_OFFER_SELLER` (`seller`),
  KEY `IDX_OFFER_PAY_METHOD` (`pay_method`),
  KEY `IDX_OFFER_WORKPLACE` (`workplace`),
  KEY `IDX_OFFER_DOMAIN` (`domain`),
  CONSTRAINT `FK_OFFER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_OFFER_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_OFFER_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_OFFER_RADDRESS` FOREIGN KEY (`address`) REFERENCES `raddress` (`id`),
  CONSTRAINT `FK_OFFER_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_OFFER_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`),
  CONSTRAINT `FK_OFFER_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`),
  CONSTRAINT `FK_OFFER_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_OFFER_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Presupuestos';

#
# Table structure for table `offer_attach`
#

CREATE TABLE `offer_attach` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `offer` int NOT NULL COMMENT 'Identificador del Presupuesto',
  `mimeType` tinyint DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `driveId` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_OFFER_ATTACH_OFFER` (`offer`),
  KEY `IDX_OFFER_ATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_OFFER_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_OFFER_ATTACH_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Presupuestos';

#
# Table structure for table `offer_detail`
#

CREATE TABLE `offer_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Detalle de Presupuesto',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `offer` int NOT NULL COMMENT 'Identificador del Presupuesto',
  `line` smallint DEFAULT '1' COMMENT 'Numero de linea del Detalle dentro del Presupuesto',
  `item` int DEFAULT NULL COMMENT 'Identificador del Articulo',
  `description` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripción del Articulo',
  `quantity` decimal(15,3) DEFAULT '0.000' COMMENT 'Cantidad del Articulo',
  `price` decimal(15,4) DEFAULT '0' COMMENT 'Precio del Articulo',
  `discount_expr` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Descuentos del Articulo',
  `status` tinyint DEFAULT '0' COMMENT 'Estado del Detalle del Presupuesto',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_OFFER_DETAIL_OFFER` (`offer`),
  KEY `IDX_OFFER_DETAIL_ITEM` (`item`),
  KEY `IDX_OFFER_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_OFFER_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_OFFER_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_OFFER_DETAIL_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Presupuesto';

#
# Table structure for table `offer_detail_commission`
#

CREATE TABLE `offer_detail_commission` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `offer_detail` int NOT NULL DEFAULT '0' COMMENT 'Identificador de la Linea de Presupuesto',
  `commission` decimal(15,4) DEFAULT '0' COMMENT 'Porcentaje de Comision',
  `amount` decimal(15,4) DEFAULT '0' COMMENT 'Importe de la Comision',
  `status` tinyint DEFAULT '0' COMMENT 'Estado de la Comision',
  `pay_date` date DEFAULT NULL COMMENT 'Fecha de liquidacion',
  PRIMARY KEY (`id`),
  KEY `IDX_OFFER_DETAIL_COMMISSION_OFFER_DETAIL` (`offer_detail`),
  KEY `IDX_OFFER_DETAIL_COMMISSION_DOMAIN` (`domain`),
  CONSTRAINT `FK_OFFER_DETAIL_COMMISSION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_OFFER_DETAIL_COMMISSION_OFFER_DETAIL` FOREIGN KEY (`offer_detail`) REFERENCES `offer_detail` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones asociadas a Lineas de Presupuestos';

#
# Table structure for table `offer_term`
#

CREATE TABLE `offer_term` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `offer` int NOT NULL COMMENT 'Identificador del Presupuesto',
  `line` smallint DEFAULT '1' COMMENT 'Numero de linea de la Condicion del Presupuesto',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Nombre de la Condicion Comercial',
  `description` text CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Condicion Comercial',
  `term_general` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Condicion es particular o general',
  PRIMARY KEY (`id`),
  KEY `IDX_OFFER_TERM_OFFER` (`offer`),
  KEY `IDX_OFFER_TERM_DOMAIN` (`domain`),
  CONSTRAINT `FK_OFFER_TERM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_OFFER_TERM_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Condiciones del Presupuesto';

#
# Table structure for table `pay_method`
#

CREATE TABLE `pay_method` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Forma de Pago',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de Forma de Pago',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT 'Tipo de Forma de Pago',
  PRIMARY KEY (`id`),
  KEY `IDX_PAY_METHOD_DOMAIN` (`domain`),
  CONSTRAINT `FK_PAY_METHOD_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Formas de Pago';

#
# Table structure for table `payment_concept`
#

CREATE TABLE `payment_concept` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `code` varchar(25) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo',
  `description` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Percepcion Salarial',
  `description_decorable` tinyint NOT NULL DEFAULT '0',
  `expression` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe',
  `irpf_expression` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe tributable',
  `quote_expression` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe cotizable',
  PRIMARY KEY (`id`),
  KEY `IDX_PAYMENT_CONCEPT_DOMAIN` (`domain`),
  CONSTRAINT `FK_PAYMENT_CONCEPT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos de devengos';

#
# Table structure for table `payroll_batch_attach`
#

CREATE TABLE `payroll_batch_attach` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `source_batch` int NOT NULL DEFAULT '0' COMMENT 'Identificador de la Remesa',
  `source_type` tinyint DEFAULT NULL COMMENT 'Tipo de la Remesa',
  `mimeType` tinyint DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Archivo Adjunto',
  `scope` int DEFAULT NULL COMMENT 'Ambito del Archivo Adjunto',
  `attach_date` date DEFAULT NULL COMMENT 'Fecha del Archivo Adjunto',
  `driveId` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_PAYROLL_BATCH_ATTACH_DOMAIN` (`domain`),
  KEY `IDX_PAYROLL_BATCH_ATTACH_SCOPE` (`scope`),
  CONSTRAINT `FK_PAYROLL_BATCH_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PAYROLL_BATCH_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Remesas de Laboral';

#
# Table structure for table `payroll_workplace`
#

CREATE TABLE `payroll_workplace` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `workplace` int NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `agreement` int DEFAULT NULL COMMENT 'Identificador del Convenio',
  `enterprise_activity` int DEFAULT NULL COMMENT 'Identificador de la Actividad',
  `calendar` int DEFAULT NULL COMMENT 'Identificador del Calendario',
  PRIMARY KEY (`id`),
  KEY `IDX_PAYROLL_WORKPLACE_WORKPLACE` (`workplace`),
  KEY `IDX_PAYROLL_WORKPLACE_CALENDAR` (`calendar`),
  KEY `IDX_PAYROLL_WORKPLACE_ENTERPRISE_ACTIVITY` (`enterprise_activity`),
  KEY `IDX_PAYROLL_WORKPLACE_AGREEMENT` (`agreement`),
  KEY `IDX_PAYROLL_WORKPLACE_DOMAIN` (`domain`),
  CONSTRAINT `FK_PAYROLL_WORKPLACE_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`),
  CONSTRAINT `FK_PAYROLL_WORKPLACE_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_PAYROLL_WORKPLACE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PAYROLL_WORKPLACE_ENTERPRISE_ACTIVITY` FOREIGN KEY (`enterprise_activity`) REFERENCES `enterprise_activity` (`id`),
  CONSTRAINT `FK_PAYROLL_WORKPLACE_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos laborales del Centro de Trabajo';

#
# Table structure for table `pcategory`
#

CREATE TABLE `pcategory` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Categoria',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Categoria',
  `detail` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del detalle de los Articulos',
  `detail2` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del detalle 2 de los Articulos',
  `detail3` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del detalle 3 de los Articulos',
  PRIMARY KEY (`id`),
  KEY `IDX_PCATEGORY_DOMAIN` (`domain`),
  CONSTRAINT `FK_PCATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias de Productos';

#
# Table structure for table `person`
#

CREATE TABLE `person` (
  `registry` int NOT NULL DEFAULT '0' COMMENT 'Registro de la Persona',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `birth_date` date DEFAULT NULL COMMENT 'Fecha de nacimiento de la Persona',
  `gender` tinyint NOT NULL DEFAULT '0' COMMENT 'Sexo de la Persona',
  `marital_status` tinyint NOT NULL DEFAULT '0' COMMENT 'Estado civil de la Persona',
  `social_security_num` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Seguridad Social de la Persona',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `first_surname` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Primer Apellido ',
  `second_surname` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Segundo Apellido',
  PRIMARY KEY (`registry`),
  KEY `IDX_PERSON_DOMAIN` (`domain`),
  CONSTRAINT `FK_PERSON_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PERSON_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Personas';

#
# Table structure for table `pm_type_detail`
#

CREATE TABLE `pm_type_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT 'Tipo de Forma de Pago',
  `description` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del detalle',
  `account` int DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY (`id`),
  KEY `IDX_PM_TYPE_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_PM_TYPE_DETAIL_ACCOUNT` (`account`),
  CONSTRAINT `FK_PM_TYPE_DETAIL_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_PM_TYPE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles por Tipo de Forma de Pago';

#
# Table structure for table `pos`
#

CREATE TABLE `pos` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `department` int DEFAULT NULL COMMENT 'Identificador del Departamento',
  `workplace` int NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre',
  `series` char(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie del POS',
  `customer` int DEFAULT NULL COMMENT 'Identificador del Cliente',
  `price_editable` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el precio es editable',
  `discount_editable` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el descuento es editable',
  `invoiceable` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el Punto de Venta es facturable',
  `item_invoice` int DEFAULT NULL COMMENT 'Identificador del Articulo facturable',
  `display_mode` tinyint NOT NULL DEFAULT '0' COMMENT 'Modo de visualizacion en pantalla',
  `num_rows` int DEFAULT '0' COMMENT 'Numero de filas en la vista tipo Hosteleria',
  `num_cols` int DEFAULT '0' COMMENT 'Numero de columnas en la vista tipo Hosteleria',
  `pin_pad` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indicador de si es un Pin Pad',
  `commerce` varchar(20) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Clave de firma del comercio',
  `signature_password` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Clave de firma del comercio',
  `terminal` varchar(4) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de terminal',
  `port_configuration` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Configuracion de puerto',
  `pos_version` varchar(8) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Version actual',
  `active` tinyint(1) DEFAULT '1' COMMENT 'Indica si el TPV esta activo o no',
  PRIMARY KEY (`id`),
  KEY `IDX_POS_WORKPLACE` (`workplace`),
  KEY `IDX_POS_DOMAIN` (`domain`),
  KEY `IDX_POS_DEPARTMENT` (`department`),
  KEY `IDX_POS_CUSTOMER` (`customer`),
  KEY `IDX_POS_ITEM_INVOICE` (`item_invoice`),
  CONSTRAINT `FK_POS_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_POS_DEPARTMENT` FOREIGN KEY (`department`) REFERENCES `department` (`id`),
  CONSTRAINT `FK_POS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_POS_ITEM_INVOICE` FOREIGN KEY (`item_invoice`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_POS_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='TPV';

#
# Table structure for table `pos_catalogue`
#

CREATE TABLE `pos_catalogue` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `pos` int NOT NULL COMMENT 'Identificador del TPV',
  `catalogue` int NOT NULL COMMENT 'Identificador del Catalogo',
  PRIMARY KEY (`id`),
  KEY `IDX_POS_CATALOGUE_CATALOGUE` (`catalogue`),
  KEY `IDX_POS_CATALOGUE_DOMAIN` (`domain`),
  KEY `IDX_POS_CATALOGUE_POS` (`pos`),
  CONSTRAINT `FK_POS_CATALOGUE_CATALOGUE` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`),
  CONSTRAINT `FK_POS_CATALOGUE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_POS_CATALOGUE_POS` FOREIGN KEY (`pos`) REFERENCES `pos` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Catalogos asociados al TPV';

#
# Table structure for table `pos_shift`
#

CREATE TABLE `pos_shift` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `pos` int NOT NULL COMMENT 'Identificador del TPV',
  `shift` tinyint NOT NULL DEFAULT '0' COMMENT 'Turno de trabajo',
  `username` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Usuario del Turno',
  `start_time` datetime NOT NULL COMMENT 'Fecha-hora de apertura',
  `end_time` datetime DEFAULT NULL COMMENT 'Fecha-hora de cierre',
  `initial_amount` decimal(15,2) DEFAULT '0.00' COMMENT 'Efectivo inicial',
  `imbalance` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si existen descuadres en el Turno',
  `remarks` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Observaciones del turno',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_POS_SHIFT_POS` (`pos`),
  KEY `IDX_POS_SHIFT_DOMAIN` (`domain`),
  CONSTRAINT `FK_POS_SHIFT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_POS_SHIFT_POS` FOREIGN KEY (`pos`) REFERENCES `pos` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Turno de trabajo del TPV';

#
# Table structure for table `pos_shift_count`
#

CREATE TABLE `pos_shift_count` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `pos_shift` int NOT NULL COMMENT 'Identificador del Turno de trabajo',
  `pay_method` int DEFAULT NULL COMMENT 'Identificador de la Forma de pago',
  `amount` decimal(15,2) DEFAULT '0.00' COMMENT 'Total efectivo',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_POS_SHIFT_COUNT_POS_SHIFT` (`pos_shift`),
  KEY `IDX_POS_SHIFT_COUNT_PAY_METHOD` (`pay_method`),
  KEY `IDX_POS_SHIFT_COUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_POS_SHIFT_COUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_POS_SHIFT_COUNT_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_POS_SHIFT_COUNT_POS_SHIFT` FOREIGN KEY (`pos_shift`) REFERENCES `pos_shift` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Arqueo del TPV';

#
# Table structure for table `prepayment`
#

CREATE TABLE `prepayment` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `creditor` int NOT NULL COMMENT 'Identificador del Acreedor',
  `customer` int NOT NULL COMMENT 'Identificador del Cliente',
  `finance` int NOT NULL COMMENT 'Identificador del Vencimiento',
  `collect` tinyint DEFAULT '0' COMMENT 'Localizacion del cobro del Suplido',
  `collect_id` int DEFAULT NULL COMMENT 'Identificador del cobro del Suplido',
  PRIMARY KEY (`id`),
  KEY `IDX_PREPAYMENT_DOMAIN` (`domain`),
  KEY `IDX_PREPAYMENT_CREDITOR` (`creditor`),
  KEY `IDX_PREPAYMENT_CUSTOMER` (`customer`),
  KEY `IDX_PREPAYMENT_FINANCE` (`finance`),
  CONSTRAINT `FK_PREPAYMENT_CREDITOR` FOREIGN KEY (`creditor`) REFERENCES `creditor` (`registry`),
  CONSTRAINT `FK_PREPAYMENT_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_PREPAYMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PREPAYMENT_FINANCE` FOREIGN KEY (`finance`) REFERENCES `finance` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Suplidos';

#
# Table structure for table `process`
#

CREATE TABLE `process` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Proceso',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(30) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Proceso.',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Activo si o no',
  PRIMARY KEY (`id`),
  KEY `IDX_PROCESS_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROCESS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Procesos';

#
# Table structure for table `process_detail`
#

CREATE TABLE `process_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Detalle de Proceso',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `process` int NOT NULL COMMENT 'Identificador del Proceso',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Detalle de Proceso',
  `position` int NOT NULL COMMENT 'Orden de ejecucion del Detalle dentro del Proceso',
  `date_reference` tinyint DEFAULT NULL COMMENT 'Referencia para el calculo de la fecha de vencimiento de la Tarea',
  `days` int DEFAULT NULL COMMENT 'Numero de dias asociado a la referencia para el calculo de la fecha de vencimiento de la Tarea',
  `alert_days` int DEFAULT NULL COMMENT 'Numero de dias, previos a la fecha de vencimiento de la Tarea, para el calculo de la fecha de generacion de la Alarma',
  `workgroup` int DEFAULT NULL COMMENT 'Identificador del Grupo de Trabajo',
  `priority` tinyint DEFAULT '0' COMMENT 'Prioridad de la Tarea',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Activo si o no',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios del Detalle de Proceso',
  PRIMARY KEY (`id`),
  KEY `IDX_PROCESS_DETAIL_PROCESS` (`process`),
  KEY `IDX_PROCESS_DETAIL_WORKGROUP` (`workgroup`),
  KEY `IDX_PROCESS_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROCESS_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROCESS_DETAIL_PROCESS` FOREIGN KEY (`process`) REFERENCES `process` (`id`),
  CONSTRAINT `FK_PROCESS_DETAIL_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Procesos';

#
# Table structure for table `process_detail_transition`
#

CREATE TABLE `process_detail_transition` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `process_detail` int NOT NULL COMMENT 'Identificador del Detalle del Proceso.',
  `process_transition_type` int NOT NULL COMMENT 'Identificador del Tipo de Transicion.',
  `next_process_detail` int NOT NULL COMMENT 'Identificador del siguiente Detalle del Proceso.',
  PRIMARY KEY (`id`),
  KEY `IDX_PROCESS_DETAIL_TRANSITION_PROCESS_DETAIL` (`process_detail`),
  KEY `IDX_PROCESS_DETAIL_TRANSITION_NEXT_PROCESS_DETAIL` (`next_process_detail`),
  KEY `IDX_PROCESS_DETAIL_TRANSITION_PROCESS_TRANSITION_TYPE` (`process_transition_type`),
  KEY `IDX_PROCESS_DETAIL_TRANSITION_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROCESS_DETAIL_TRANSITION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROCESS_DETAIL_TRANSITION_NEXT_PROCESS_DETAIL` FOREIGN KEY (`next_process_detail`) REFERENCES `process_detail` (`id`),
  CONSTRAINT `FK_PROCESS_DETAIL_TRANSITION_PROCESS_DETAIL` FOREIGN KEY (`process_detail`) REFERENCES `process_detail` (`id`),
  CONSTRAINT `FK_PROCESS_DETAIL_TRANSITION_PROCESS_TRANSITION_TYPE` FOREIGN KEY (`process_transition_type`) REFERENCES `process_transition_type` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Transiciones entre Detalles de Procesos';

#
# Table structure for table `process_task`
#

CREATE TABLE `process_task` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Relacion entre Campañas, Actividades y Tareas',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `campaign` int DEFAULT NULL COMMENT 'Identificador de la Campaña',
  `process_detail` int NOT NULL COMMENT 'Identificador del Detalle de Proceso',
  `task` int NOT NULL COMMENT 'Identificador de la Tarea',
  PRIMARY KEY (`id`),
  KEY `campaign` (`campaign`),
  KEY `IDX_PROCESS_TASK_CAMPAIGN` (`campaign`),
  KEY `IDX_PROCESS_TASK_TASK` (`task`),
  KEY `IDX_PROCESS_TASK_PROCESS_DETAIL` (`process_detail`),
  KEY `IDX_PROCESS_TASK_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROCESS_TASK_CAMPAIGN` FOREIGN KEY (`campaign`) REFERENCES `campaign` (`id`),
  CONSTRAINT `FK_PROCESS_TASK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROCESS_TASK_PROCESS_DETAIL` FOREIGN KEY (`process_detail`) REFERENCES `process_detail` (`id`),
  CONSTRAINT `FK_PROCESS_TASK_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Campañas, Actividades y Tareas';

#
# Table structure for table `process_transition_type`
#

CREATE TABLE `process_transition_type` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Tipo de Transicion',
  PRIMARY KEY (`id`),
  KEY `IDX_PROCESS_TRANSITION_TYPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROCESS_TRANSITION_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Transiciones entre Detalles de Procesos';

#
# Table structure for table `product`
#

CREATE TABLE `product` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Producto',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Producto',
  `code` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo del Producto',
  `kind` tinyint NOT NULL DEFAULT '0' COMMENT 'Clase de Producto',
  `brand` int DEFAULT NULL COMMENT 'Marca Comercial del Producto',
  `category` int DEFAULT NULL COMMENT 'Categoria del Producto',
  `inventoriable` tinyint(1) DEFAULT NULL COMMENT 'Indica si el Producto es inventariable',
  `serializable` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Producto es serializable',
  `lotable` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Producto es loteable',
  `status` tinyint DEFAULT '0' COMMENT 'Estado del Producto',
  `vat` int DEFAULT NULL COMMENT 'IVA del Producto',
  `retention` int DEFAULT NULL COMMENT 'Retencion del Producto',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Producto',
  `manufactured` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Producto es elaborado',
  `composition` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Producto es una Composicion',
  `composition_price` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Precio lo determina la Composicion',
  `packaged` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Producto es envasado',
  `sales_account` int DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable de Ventas',
  `purchase_account` int DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable de Compras',
  `perishable` tinyint(1) DEFAULT '0' COMMENT 'Indica si el producto es perecedero',
  `days_to_expire` int DEFAULT '0' Comment 'Numero de dias en los que expira el producto',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_PRODUCT_DOMAIN_CODE` (`domain`,`code`),
  KEY `IDX_PRODUCT_NAME` (`name`),
  KEY `IDX_PRODUCT_TAX_RETENTION` (`retention`),
  KEY `IDX_PRODUCT_PCATEGORY` (`category`),
  KEY `IDX_PRODUCT_BRAND` (`brand`),
  KEY `IDX_PRODUCT_TAX_VAT` (`vat`),
  KEY `IDX_PRODUCT_DOMAIN` (`domain`),
  KEY `IDX_PRODUCT_ACCOUNT_SALES` (`sales_account`),
  KEY `IDX_PRODUCT_ACCOUNT_PURCHASE` (`purchase_account`),
  CONSTRAINT `FK_PRODUCT_ACCOUNT_PURCHASE` FOREIGN KEY (`purchase_account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_PRODUCT_ACCOUNT_SALES` FOREIGN KEY (`sales_account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_PRODUCT_BRAND` FOREIGN KEY (`brand`) REFERENCES `brand` (`id`),
  CONSTRAINT `FK_PRODUCT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PRODUCT_PCATEGORY` FOREIGN KEY (`category`) REFERENCES `pcategory` (`id`),
  CONSTRAINT `FK_PRODUCT_TAX_RETENTION` FOREIGN KEY (`retention`) REFERENCES `tax` (`id`),
  CONSTRAINT `FK_PRODUCT_TAX_VAT` FOREIGN KEY (`vat`) REFERENCES `tax` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Productos';

#
# Table structure for table `product_tag`
#

CREATE TABLE `product_tag` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `product` int NOT NULL COMMENT 'Identificador del Producto',
  `tag` int NOT NULL COMMENT 'Identificador de la Etiqueta',
  PRIMARY KEY (`id`),
  KEY `IDX_PRODUCT_TAG_DOMAIN` (`domain`),
  KEY `IDX_PRODUCT_TAG_PRODUCT` (`product`),
  KEY `IDX_PRODUCT_TAG_TAG` (`tag`),
  CONSTRAINT `FK_PRODUCT_TAG_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PRODUCT_TAG_PRODUCT` FOREIGN KEY (`product`) REFERENCES `product` (`id`),
  CONSTRAINT `FK_PRODUCT_TAG_TAG` FOREIGN KEY (`tag`) REFERENCES `tag` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Productos y Etiquetas';

#
# Table structure for table `profile`
#

CREATE TABLE `profile` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Perfil',
  `application` int NOT NULL COMMENT 'Identificador de la Aplicacion',
  `domain` int DEFAULT NULL COMMENT 'Identificador del Dominio',
  PRIMARY KEY (`id`),
  KEY `IDX_PROFILE_APPLICATION` (`application`),
  KEY `IDX_PROFILE_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROFILE_APPLICATION` FOREIGN KEY (`application`) REFERENCES `application` (`id`),
  CONSTRAINT `FK_PROFILE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Perfiles';

#
# Table structure for table `profile_action_denied`
#

CREATE TABLE `profile_action_denied` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `profile` int NOT NULL COMMENT 'Identificador del Perfil',
  `action_id` int NOT NULL COMMENT 'Identificador de la Accion',
  PRIMARY KEY (`id`),
  KEY `IDX_PROFILE_ACTION_DENIED_DOMAIN` (`domain`),
  KEY `IDX_PROFILE_ACTION_DENIED_PROFILE` (`profile`),
  KEY `IDX_PROFILE_ACTION_DENIED_ACTION` (`action_id`),
  CONSTRAINT `FK_PROFILE_ACTION_DENIED_ACTION` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `FK_PROFILE_ACTION_DENIED_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROFILE_ACTION_DENIED_PROFILE` FOREIGN KEY (`profile`) REFERENCES `profile` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Acciones Inhabilitadas en el Perfil';

#
# Table structure for table `profile_module_denied`
#

CREATE TABLE `profile_module_denied` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int DEFAULT NULL COMMENT 'Identificador del Dominio',
  `profile` int NOT NULL COMMENT 'Identificador del Perfil',
  `module` tinyint NOT NULL COMMENT 'Modulo Inhabilitado para el Perfil',
  PRIMARY KEY (`id`),
  KEY `IDX_PROFILE_MODULE_DENIED_PROFILE` (`profile`),
  KEY `IDX_PROFILE_MODULE_DENIED_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROFILE_MODULE_DENIED_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROFILE_MODULE_DENIED_PROFILE` FOREIGN KEY (`profile`) REFERENCES `profile` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Modulos Inhabilitados en el Perfil';

#
# Table structure for table `profile_role`
#

CREATE TABLE `profile_role` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int DEFAULT NULL COMMENT 'Identificador del Dominio',
  `profile` int NOT NULL COMMENT 'Identificador del Perfil',
  `application_role` int NOT NULL COMMENT 'Identificador del Role de la Aplicacion',
  PRIMARY KEY (`id`),
  KEY `IDX_PROFILE_ROLE_PROFILE` (`profile`),
  KEY `IDX_PROFILE_ROLE_APPLICATION_ROLE` (`application_role`),
  KEY `IDX_PROFILE_ROLE_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROFILE_ROLE_APPLICATION_ROLE` FOREIGN KEY (`application_role`) REFERENCES `application_role` (`id`),
  CONSTRAINT `FK_PROFILE_ROLE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROFILE_ROLE_PROFILE` FOREIGN KEY (`profile`) REFERENCES `profile` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Roles del Perfil';

#
# Table structure for table `project`
#

CREATE TABLE `project` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Proyecto',
  `alias` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias del Proyecto',
  `registry` int NOT NULL COMMENT 'Identificador del Cliente (Potencial) asociado',
  `date` date NOT NULL COMMENT 'Fecha del Proyecto',
  `project_type` int DEFAULT NULL COMMENT 'Tipo de Proyecto',
  `tas` tinyint(1) DEFAULT '0' COMMENT 'Indica si se trata de una Orden de Reparacion o Fabricacion',
  `commercial` tinyint(1) DEFAULT '0' COMMENT 'Indica si se trata de una Operacion Comercial',
  `reservation` tinyint(1) DEFAULT '0' COMMENT 'Indica si se trata de una Reserva',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si el Proyecto esta activo o no',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_REGISTRY` (`registry`),
  KEY `IDX_PROJECT_PROJECT_TYPE` (`project_type`),
  KEY `IDX_PROJECT_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_PROJECT_TYPE` FOREIGN KEY (`project_type`) REFERENCES `project_type` (`id`),
  CONSTRAINT `FK_PROJECT_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Proyectos';

#
# Table structure for table `project_activity`
#

CREATE TABLE `project_activity` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Actividad',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `project` int NOT NULL COMMENT 'Identificador del Expendiente',
  `activity_type` int NOT NULL COMMENT 'Tipo de Actividad',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Activo, si o no',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_ACTIVITY_ACTIVITY_TYPE` (`activity_type`),
  KEY `IDX_PROJECT_ACTIVITY_PROJECT` (`project`),
  KEY `IDX_PROJECT_ACTIVITY_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_ACTIVITY_ACTIVITY_TYPE` FOREIGN KEY (`activity_type`) REFERENCES `activity_type` (`id`),
  CONSTRAINT `FK_PROJECT_ACTIVITY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_ACTIVITY_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades';

#
# Table structure for table `project_attach`
#

CREATE TABLE `project_attach` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `project` int NOT NULL COMMENT 'Identificador del Proyecto',
  `mimeType` tinyint DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad del Archivo Adjunto',
  `attach_date` date DEFAULT NULL COMMENT 'Fecha del Archivo Adjunto',
  `attach_type` tinyint DEFAULT '0' COMMENT 'Tipo de Archivo Adjunto',
  `driveId` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_ATTACH_DOMAIN` (`domain`),
  KEY `IDX_PROJECT_ATTACH_PROJECT` (`project`),
  CONSTRAINT `FK_PROJECT_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_ATTACH_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Proyectos';

#
# Table structure for table `project_commercial`
#

CREATE TABLE `project_commercial` (
  `project` int NOT NULL COMMENT 'Identificador del Proyecto',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `target` int NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `seller` int DEFAULT NULL COMMENT 'Identificador del Comercial',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  `source` tinyint NOT NULL COMMENT 'Origen del Proyecto',
  `status` tinyint NOT NULL COMMENT 'Estado del Proyecto',
  `status_date` date DEFAULT NULL COMMENT 'Fecha del Estado del Proyecto',
  `probability` int DEFAULT NULL COMMENT 'Probabilidad del Proyecto',
  PRIMARY KEY (`project`),
  KEY `IDX_PROJECT_COMMERCIAL_TARGET` (`target`),
  KEY `IDX_PROJECT_COMMERCIAL_SELLER` (`seller`),
  KEY `IDX_PROJECT_COMMERCIAL_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_COMMERCIAL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_COMMERCIAL_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PROJECT_COMMERCIAL_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`),
  CONSTRAINT `FK_PROJECT_COMMERCIAL_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Operaciones Comerciales';

#
# Table structure for table `project_holder`
#

CREATE TABLE `project_holder` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `project` int NOT NULL COMMENT 'Identificador del Proyecto',
  `start_date` datetime DEFAULT NULL COMMENT 'Fecha Inicio',
  `end_date` datetime DEFAULT NULL COMMENT 'Fecha Fin',
  `workgroup` int DEFAULT NULL COMMENT 'Identificador del Grupo de Trabajo',
  `task_holder` int DEFAULT NULL COMMENT 'Identificador del Operario',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_HOLDER_DOMAIN` (`domain`),
  KEY `IDX_PROJECT_HOLDER_PROJECT` (`project`),
  KEY `IDX_PROJECT_HOLDER_WORKGROUP` (`workgroup`),
  KEY `IDX_PROJECT_HOLDER_TASK_HOLDER` (`task_holder`),
  CONSTRAINT `FK_PROJECT_HOLDER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_HOLDER_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PROJECT_HOLDER_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`),
  CONSTRAINT `FK_PROJECT_HOLDER_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Project y Project Holder';

#
# Table structure for table `project_reservation`
#

CREATE TABLE `project_reservation` (
  `project` int NOT NULL COMMENT 'Identificador del Proyecto',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `hotel` int NOT NULL COMMENT 'Identificador del Hotel de Produccion',
  `hotel_reservation` int NOT NULL COMMENT 'Identificador del Hotel de la Reserva',
  `code` varchar(48) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Localizador de la Reserva',
  `start_date` date NOT NULL COMMENT 'Fecha de entrada',
  `start_time` datetime NOT NULL COMMENT 'Hora de entrada',
  `end_date` date NOT NULL COMMENT 'Fecha de salida',
  `end_time` datetime NOT NULL COMMENT 'Hora de salida',
  `seller` int DEFAULT NULL COMMENT 'Identificador del canal de venta',
  `agency` int DEFAULT NULL COMMENT 'Identificador de la agencia de viajes',
  `agency_commission_percent` decimal(5,2) DEFAULT '0.00' COMMENT 'Porcentaje de comision de la agencia',
  `agency_commission_amount` decimal(15,2) DEFAULT '0.00' COMMENT 'Importe de comision de la agencia',
  `agency_rebate` tinyint(1) NOT NULL COMMENT 'Indica si la agencia trabaja en modo descuento o no',
  `company` int DEFAULT NULL COMMENT 'Identificador de la empresa',
  `discount_percent` decimal(5,2) DEFAULT '0.00' COMMENT 'Porcentaje de descuento',
  `discount_amount` decimal(15,2) DEFAULT '0.00' COMMENT 'Importe de descuento',
  `booking_holder` tinyint NOT NULL COMMENT 'Titular de la Reserva',
  `taxable_base` decimal(15,2) DEFAULT '0.00' COMMENT 'Base imponible',
  `vat_quota` decimal(15,2) DEFAULT '0.00' COMMENT 'Cuota de IVA',
  `other_tax_quota` decimal(15,2) DEFAULT '0.00' COMMENT 'Cuota de otros Impuestos',
  `total` decimal(15,2) DEFAULT '0.00' COMMENT 'Importe Total',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  `remarks` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Observaciones',
  `source` tinyint NOT NULL DEFAULT '0' COMMENT 'Origen de la Reserva',
  `crs_code` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de la Reserva en el CRS',
  `advance` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT 'Anticipo',
  `advance_invoiced` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el anticipo esta Facturado',
  `early_check_out` tinyint(1) DEFAULT '0' COMMENT 'Indica si se ha producido una salida anticipada',
  `prepay` tinyint(1) DEFAULT '0' COMMENT 'Indica si es un prepago',
  `bank_transaction` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de transaccion bancaria',
  `credit_card_number` varchar(4) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Ultimos 4 numeros de la tarjeta de credito',
  `credit_card_expiration_month` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mes de expiracion de la tarjeta de credito',
  `credit_card_expiration_year` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Año de expiracion de la tarjeta de credito',
  `credit_card_type` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo de tarjeta de credito',
  `token` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Token de preautorizacion de cobro',
  `penalty_value` varchar(4) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor de penalizacion (patron)',
  `penalty_amount` decimal(15,2) DEFAULT '0.00' COMMENT 'Importe de penalizacion',
  `penalty_date` datetime DEFAULT NULL COMMENT 'Fecha de penalizacion',
  `tourist_tax_free` tinyint DEFAULT NULL COMMENT 'Tipo de exencion de la Tasa turistica',
  `check_status` tinyint NOT NULL COMMENT 'Estado de registro en el Hotel',
  `status` tinyint NOT NULL COMMENT 'Estado de la Reserva',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  `cancellation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de cancelacion',
  `cancellation_date` datetime DEFAULT NULL COMMENT 'Fecha de cancelacion',
  PRIMARY KEY (`project`),
  KEY `IDX_PROJECT_RESERVATION_CODE` (`code`),
  KEY `IDX_PROJECT_RESERVATION_HOTEL` (`hotel`),
  KEY `IDX_PROJECT_RESERVATION_SELLER` (`seller`),
  KEY `IDX_PROJECT_RESERVATION_AGENCY` (`agency`),
  KEY `IDX_PROJECT_RESERVATION_COMPANY` (`company`),
  KEY `IDX_PROJECT_RESERVATION_DOMAIN` (`domain`),
  KEY `IDX_PROJECT_RESERVATION_HOTEL_RESERVATION` (`hotel_reservation`),
  KEY `IDX_PROJECT_RESERVATION_CRS_CODE` (`crs_code`),
  KEY `IDX_PROJECT_RESERVATION_START_DATE` (`start_date`),
  CONSTRAINT `FK_PROJECT_RESERVATION_AGENCY` FOREIGN KEY (`agency`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_PROJECT_RESERVATION_COMPANY` FOREIGN KEY (`company`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_PROJECT_RESERVATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_HOTEL` FOREIGN KEY (`hotel`) REFERENCES `hotel` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_HOTEL_RESERVATION` FOREIGN KEY (`hotel_reservation`) REFERENCES `hotel` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Reservas de Hotel';

#
# Table structure for table `project_reservation_divert`
#

CREATE TABLE `project_reservation_divert` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation` int NOT NULL COMMENT 'Identificador de la Reserva',
  `request_hotel` int NOT NULL COMMENT 'Identificador del Hotel solicitante',
  `divert_hotel` int NOT NULL COMMENT 'Identificador del Hotel destino',
  `divert_date` date NOT NULL COMMENT 'Fecha de Desvio',
  `status` tinyint DEFAULT '0' COMMENT 'Estado del Desvio',
  `request_user` int DEFAULT NULL COMMENT 'Identificador del Usuario solicitante',
  `response_user` int DEFAULT NULL COMMENT 'Identificador del Usuario de respuesta',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_RESERVATION_DIVERT_DOMAIN` (`domain`),
  KEY `IDX_PROJECT_RESERVATION_DIVERT_PROJECT_RESERVATION` (`project_reservation`),
  KEY `IDX_PROJECT_RESERVATION_DIVERT_REQUEST_HOTEL` (`request_hotel`),
  KEY `IDX_PROJECT_RESERVATION_DIVERT_DIVERT_HOTEL` (`divert_hotel`),
  KEY `IDX_PROJECT_RESERVATION_DIVERT_REQUEST_USER` (`request_user`),
  KEY `IDX_PROJECT_RESERVATION_DIVERT_RESPONSE_USER` (`response_user`),
  CONSTRAINT `FK_PROJECT_RESERVATION_DIVERT_DIVERT_HOTEL` FOREIGN KEY (`divert_hotel`) REFERENCES `hotel` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_DIVERT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_DIVERT_PROJECT_RESERVATION` FOREIGN KEY (`project_reservation`) REFERENCES `project_reservation` (`project`),
  CONSTRAINT `FK_PROJECT_RESERVATION_DIVERT_REQUEST_HOTEL` FOREIGN KEY (`request_hotel`) REFERENCES `hotel` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_DIVERT_REQUEST_USER` FOREIGN KEY (`request_user`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_DIVERT_RESPONSE_USER` FOREIGN KEY (`response_user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Desvio de Reservas';

#
# Table structure for table `project_reservation_guest`
#

CREATE TABLE `project_reservation_guest` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation` int NOT NULL COMMENT 'Identificador de la Reserva',
  `guest_index` tinyint NOT NULL COMMENT 'Numero de Huesped',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre',
  `surname` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Apellido 1',
  `surname2` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Apellido 2',
  `treatment` varchar(4) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tratamiento',
  `document` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de documento de identificacion',
  `document_type` tinyint DEFAULT '0' COMMENT 'Tipo de documento',
  `document_country` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT 'ES' COMMENT 'Pais del documento',
  `document_exp_date` date DEFAULT NULL COMMENT 'Fecha de expiracion del documento',
  `birth_date` date DEFAULT NULL COMMENT 'Fecha de nacimiento',
  `email` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Email',
  `phone` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono',
  `address` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion',
  `number` varchar(12) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero',
  `address2` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Segunda parte de la Direccion',
  `zip` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo postal',
  `city` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Ciudad',
  `province` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Provincia',
  `country` varchar(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Pais',
  `barcode` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de pulsera',
  `person` int DEFAULT NULL COMMENT 'Identificador de la Persona',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION` (`project_reservation`),
  KEY `IDX_PROJECT_RESERVATION_GUEST_DOMAIN` (`domain`),
  KEY `IDX_PROJECT_RESERVATION_GUEST_PERSON` (`person`),
  CONSTRAINT `FK_PROJECT_RESERVATION_GUEST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_GUEST_PERSON` FOREIGN KEY (`person`) REFERENCES `person` (`registry`),
  CONSTRAINT `FK_PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION` FOREIGN KEY (`project_reservation`) REFERENCES `project_reservation` (`project`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Huespedes por Reserva';

#
# Table structure for table `project_reservation_room`
#

CREATE TABLE `project_reservation_room` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation` int NOT NULL COMMENT 'Identificador de la Reserva',
  `room_index` tinyint NOT NULL COMMENT 'Numero de Habitacion',
  `room_code` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de Habitacion en origen',
  `item` int NOT NULL COMMENT 'Identificador del Tipo de Habitacion',
  `allotment_rate_code` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de Cupo',
  `rate_plan` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de Tarifa en origen',
  `tariff` int NOT NULL COMMENT 'Identificador de la Tarifa',
  `adults` smallint DEFAULT '0' COMMENT 'Numero de adultos',
  `children` smallint DEFAULT '0' COMMENT 'Numero de niños',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION` (`project_reservation`),
  KEY `IDX_PROJECT_RESERVATION_ROOM_ITEM` (`item`),
  KEY `IDX_PROJECT_RESERVATION_ROOM_DOMAIN` (`domain`),
  KEY `IDX_PROJECT_RESERVATION_ROOM_TARIFF` (`tariff`),
  CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION` FOREIGN KEY (`project_reservation`) REFERENCES `project_reservation` (`project`),
  CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Habitaciones por Reserva';

#
# Table structure for table `project_reservation_room_detail`
#

CREATE TABLE `project_reservation_room_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation_room` int NOT NULL COMMENT 'Identificador de la Habitacion de la Reserva',
  `asset_activity` int NOT NULL COMMENT 'Identificador de la Actividad de la Habitacion',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM` (`project_reservation_room`),
  KEY `IDX_PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY` (`asset_activity`),
  KEY `IDX_PROJECT_RESERVATION_ROOM_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY` FOREIGN KEY (`asset_activity`) REFERENCES `asset_activity` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM` FOREIGN KEY (`project_reservation_room`) REFERENCES `project_reservation_room` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Habitacion por Reserva';

#
# Table structure for table `project_reservation_service`
#

CREATE TABLE `project_reservation_service` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation` int NOT NULL COMMENT 'Identificador de la Reserva',
  `service_index` tinyint NOT NULL COMMENT 'Numero de Servicio',
  `service_code` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del Servicio en origen',
  `item` int NOT NULL COMMENT 'Identificador del Servicio',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `meal_plan` varchar(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Regimen',
  `project_reservation_room` int DEFAULT NULL COMMENT 'Identificador de la Habitacion de la Reserva',
  `extra` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si se trata de un Servicio extra',
  `removed` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Servicio borrado',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION` (`project_reservation`),
  KEY `IDX_PROJECT_RESERVATION_SERVICE_ITEM` (`item`),
  KEY `IDX_PROJECT_RESERVATION_SERVICE_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_RESERVATION_SERVICE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_SERVICE_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION` FOREIGN KEY (`project_reservation`) REFERENCES `project_reservation` (`project`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Servicios por Reserva';

#
# Table structure for table `project_reservation_service_detail`
#

CREATE TABLE `project_reservation_service_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation_service` int NOT NULL COMMENT 'Identificador del Servicio de la Reserva',
  `project_reservation_room_detail` int DEFAULT NULL COMMENT 'Identificador del Detalle de Habitacion de la Reserva',
  `effective_date` date NOT NULL COMMENT 'Fecha de efecto',
  `quantity` decimal(15,2) DEFAULT '0.00' COMMENT 'Cantidad',
  `price` decimal(15,4) DEFAULT '0.0000' COMMENT 'Precio',
  `taxable_base` decimal(15,4) DEFAULT '0.0000' COMMENT 'Base imponible',
  `taxable_base_production` decimal(15,4) DEFAULT '0.0000' COMMENT 'Base imponible de Produccion',
  `total_production` decimal(15,4) DEFAULT '0.0000' COMMENT 'Total Produccion acumulado',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_RESERVATION_SERVICE_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_PRJ_RESERVATION_SERVICE_DETAIL_PRJ_RESERVATION_SERVICE` (`project_reservation_service`),
  KEY `IDX_PRJ_RESERVATION_SERVICE_DETAIL_PRJ_RESERVATION_ROOM_DETAIL` (`project_reservation_room_detail`),
  CONSTRAINT `FK_PRJ_RESERVATION_SERVICE_DETAIL_PRJ_RESERVATION_ROOM_DETAIL` FOREIGN KEY (`project_reservation_room_detail`) REFERENCES `project_reservation_room_detail` (`id`),
  CONSTRAINT `FK_PRJ_RESERVATION_SERVICE_DETAIL_PRJ_RESERVATION_SERVICE` FOREIGN KEY (`project_reservation_service`) REFERENCES `project_reservation_service` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_SERVICE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Servicio por Reserva';

#
# Table structure for table `project_tas`
#

CREATE TABLE `project_tas` (
  `project` int NOT NULL COMMENT 'Identificador del Proyecto',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `series` char(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie de la Orden de Reparacion',
  `number` int NOT NULL DEFAULT '0' COMMENT 'Numero de la Orden de Reparacion',
  `target` int NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `tas_item` int NOT NULL COMMENT 'Identificador del Articulo susceptible de Asistencia Tecnica',
  `counter` decimal(15,3) DEFAULT '0.000' COMMENT 'Contador del Articulo de la Orden de Reparacion (p.e. Kilometraje)',
  `task_holder` int DEFAULT NULL COMMENT 'Identificador del Empleado que ejecuta la Orden',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  `status` tinyint NOT NULL COMMENT 'Estado de la Orden de Reparacion',
  `status_date` date DEFAULT NULL COMMENT 'Fecha del Estado de la Orden de Reparacion',
  `workplace` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Centro de Trabajo',
  PRIMARY KEY (`project`),
  KEY `IDX_PROJECT_TAS_TARGET` (`target`),
  KEY `IDX_PROJECT_TAS_TAS_ITEM` (`tas_item`),
  KEY `IDX_PROJECT_TAS_TASK_HOLDER` (`task_holder`),
  KEY `IDX_PROJECT_TAS_WORKPLACE` (`workplace`),
  KEY `IDX_PROJECT_TAS_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_TAS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_TAS_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PROJECT_TAS_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_PROJECT_TAS_TAS_ITEM` FOREIGN KEY (`tas_item`) REFERENCES `tas_item` (`id`),
  CONSTRAINT `FK_PROJECT_TAS_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`),
  CONSTRAINT `FK_PROJECT_TAS_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ordenes de Reparacion o Fabricacion';

#
# Table structure for table `project_type`
#

CREATE TABLE `project_type` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Tipo de Expediente',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Expediente',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Activo si o no',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_TYPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Expedientes';

#
# Table structure for table `proposal`
#

CREATE TABLE `proposal` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `issue_date` date DEFAULT NULL COMMENT 'Fecha de emision de la Propuesta',
  `department` int DEFAULT NULL COMMENT 'Identificador del Departamento',
  `workplace` int NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `warehouse` int DEFAULT NULL COMMENT 'Identificador del Almacen',
  `scope` int NOT NULL COMMENT 'Identificador del Ambito',
  `remarks` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Observaciones de la Propuesta',
  `item_return` tinyint(1) DEFAULT '0' COMMENT 'Indica si es una devolucion',
  `status` tinyint DEFAULT '0' COMMENT 'Estado de la Propuesta',
  `transfer_status` tinyint DEFAULT '0' COMMENT 'Indica si es un traspaso y su estado',
  `transfer_proposal` int DEFAULT NULL COMMENT 'Identificador de la Solicitud de traspaso vinculada',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_PROPOSAL_DOMAIN` (`domain`),
  KEY `IDX_PROPOSAL_SCOPE` (`scope`),
  KEY `IDX_PROPOSAL_WORKPLACE` (`workplace`),
  KEY `IDX_PROPOSAL_DEPARTMENT` (`department`),
  KEY `IDX_PROPOSAL_TRANSFER_PROPOSAL` (`transfer_proposal`),
  KEY `IDX_PROPOSAL_WAREHOUSE` (`warehouse`),
  CONSTRAINT `FK_PROPOSAL_DEPARTMENT` FOREIGN KEY (`department`) REFERENCES `department` (`id`),
  CONSTRAINT `FK_PROPOSAL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROPOSAL_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_PROPOSAL_TRANSFER_PROPOSAL` FOREIGN KEY (`transfer_proposal`) REFERENCES `proposal` (`id`),
  CONSTRAINT `FK_PROPOSAL_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`),
  CONSTRAINT `FK_PROPOSAL_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Propuestas de Compra';

#
# Table structure for table `proposal_detail`
#

CREATE TABLE `proposal_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `proposal` int NOT NULL DEFAULT '0' COMMENT 'Identificador de la Propuesta de Compra',
  `item` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Articulo',
  `description` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `quantity` decimal(15,3) DEFAULT '0.000' COMMENT 'Cantidad',
  `price` decimal(15,4) DEFAULT '0' COMMENT 'Precio',
  `discount_expr` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descuentos',
  `status` tinyint DEFAULT '0' COMMENT 'Estado del Detalle de la Propuesta',
  `supplier` int DEFAULT NULL COMMENT 'Identificador de Proveedor',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_PROPOSAL_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_PROPOSAL_DETAIL_PROPOSAL` (`proposal`),
  KEY `IDX_PROPOSAL_DETAIL_ITEM` (`item`),
  KEY `IDX_PROPOSAL_DETAIL_SUPPLIER` (`supplier`),
  CONSTRAINT `FK_PROPOSAL_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROPOSAL_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_PROPOSAL_DETAIL_PROPOSAL` FOREIGN KEY (`proposal`) REFERENCES `proposal` (`id`),
  CONSTRAINT `FK_PROPOSAL_DETAIL_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de la Propuesta de Compra';

#
# Table structure for table `purchase`
#

CREATE TABLE `purchase` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Pedido de Compra',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `project` int DEFAULT NULL COMMENT 'Identificador del Proyecto',
  `supplier` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Proveedor',
  `series` char(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie del Pedido',
  `number` int NOT NULL DEFAULT '0' COMMENT 'Numero del Pedido',
  `purchase_reference` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de referencia del Pedido de Compra',
  `address` int DEFAULT NULL COMMENT 'Identificador de la Direccion del Proveedor',
  `discount_expr` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descuentos del Pedido',
  `issue_date` date DEFAULT NULL COMMENT 'Fecha de emision del Pedido',
  `pay_method` int DEFAULT NULL COMMENT 'Identificador de la Forma de Pago',
  `document_type` tinyint DEFAULT '1' COMMENT 'Tipo de Pedido',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad del Pedido',
  `status` tinyint DEFAULT '0' COMMENT 'Estado del Pedido',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios del Pedido',
  `remarks` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Observaciones del Pedido',
  `workplace` int NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `warehouse` int DEFAULT NULL COMMENT 'Identificador del Almacen',
  `scope` int NOT NULL DEFAULT '1' COMMENT 'Ambito del Pedido',
  `number_of_pymnts` smallint DEFAULT '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint DEFAULT '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint DEFAULT '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Dias de pago',
  `bank_account` varchar(34) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'IBAN - Numero de Cuenta Bancaria Internacional',
  `bank_alias` varchar(25) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias del Banco',
  `bic` varchar(11) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'BIC - Codigo Identificador del Banco',
  `email_communication` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si se ha comunicado a traves de email',
  `delivery_date` date DEFAULT NULL COMMENT 'Fecha de entrega',
  `carrier` int DEFAULT NULL COMMENT 'Identificador de la Agencia de Transporte',
  `carrier_packing` int DEFAULT NULL COMMENT 'Identificador de la Hoja de ruta',
  `shipping_alternative_address` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Primera parte de la Direccion de entrega',
  `shipping_alternative_address2` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Segunda parte de la Direccion de entrega',
  `shipping_alternative_zip` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo Postal de entrega',
  `shipping_alternative_city` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Localidad de entrega',
  `shipping_alternative_phone` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono de contacto de la entrega',
  `shipping_alternative_recipient` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Destinatario de la entrega',
  `shipping_contact` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del contacto para la entrega',
  `shipping_period` tinyint DEFAULT '0' COMMENT 'Tipo de periodo de entrega',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_PURCHASE_DOMAIN_SUPPLIER_SERIES_NUMBER` (`domain`,`supplier`,`series`,`number`),
  KEY `IDX_PURCHASE_SCOPE` (`scope`),
  KEY `IDX_PURCHASE_PROJECT` (`project`),
  KEY `IDX_PURCHASE_SUPPLIER` (`supplier`),
  KEY `IDX_PURCHASE_PAY_METHOD` (`pay_method`),
  KEY `IDX_PURCHASE_WORKPLACE` (`workplace`),
  KEY `IDX_PURCHASE_DOMAIN` (`domain`),
  KEY `IDX_PURCHASE_RADDRESS` (`address`),
  KEY `IDX_PURCHASE_CARRIER` (`carrier`),
  KEY `IDX_PURCHASE_WAREHOUSE` (`warehouse`),
  CONSTRAINT `FK_PURCHASE_CARRIER` FOREIGN KEY (`carrier`) REFERENCES `carrier` (`registry`),
  CONSTRAINT `FK_PURCHASE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PURCHASE_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_PURCHASE_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PURCHASE_RADDRESS` FOREIGN KEY (`address`) REFERENCES `raddress` (`id`),
  CONSTRAINT `FK_PURCHASE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_PURCHASE_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`),
  CONSTRAINT `FK_PURCHASE_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`),
  CONSTRAINT `FK_PURCHASE_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Pedidos de Compra';

#
# Table structure for table `purchase_detail`
#

CREATE TABLE `purchase_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Detalle del Pedido de Compra',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `purchase` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Pedido de Compra',
  `project` int DEFAULT NULL COMMENT 'Identificador del Proyecto',
  `line` smallint DEFAULT '1' COMMENT 'Numero de linea del Detalle dentro del Pedido',
  `item` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Articulo del Detalle de Pedido',
  `description` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Detalle de Pedido',
  `quantity` decimal(15,3) DEFAULT '0.000' COMMENT 'Cantidad del Detalle de Pedido',
  `price` decimal(15,4) DEFAULT '0' COMMENT 'Precio del Detalle de Pedido',
  `discount_expr` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descuentos del Detalle de Pedido',
  `taxes` decimal(15,3) DEFAULT '0.000' COMMENT 'Tasas del Detalle de Pedido',
  `status` tinyint DEFAULT NULL COMMENT 'Estado del Detalle de Pedido',
  `source` tinyint DEFAULT '0' COMMENT 'Origen del Detalle de la Compra',
  `source_id` int DEFAULT NULL COMMENT 'Identificador del Origen del Detalle de la Compra',
  `proposal_detail` int DEFAULT NULL COMMENT 'Identificador del Detalle de Solicitud',
  `delivered` decimal(15,4) DEFAULT '0' COMMENT 'Cantidad entregada del Detalle de Pedido',
  `delivery_date` date DEFAULT NULL COMMENT 'Fecha de entrega',
  `carrier` int DEFAULT NULL COMMENT 'Identificador de la Agencia de Transporte',
  `carrier_packing` int DEFAULT NULL COMMENT 'Identificador de la Hoja de ruta',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_PURCHASE_DETAIL_PROJECT` (`project`),
  KEY `IDX_PURCHASE_DETAIL_PURCHASE` (`purchase`),
  KEY `IDX_PURCHASE_DETAIL_ITEM` (`item`),
  KEY `IDX_PURCHASE_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_PURCHASE_DETAIL_PROPOSAL_DETAIL` (`proposal_detail`),
  KEY `IDX_PURCHASE_DETAIL_CARRIER` (`carrier`),
  KEY `IDX_PURCHASE_DETAIL_CARRIER_PACKING` (`carrier_packing`),
  CONSTRAINT `FK_PURCHASE_DETAIL_CARRIER` FOREIGN KEY (`carrier`) REFERENCES `carrier` (`registry`),
  CONSTRAINT `FK_PURCHASE_DETAIL_CARRIER_PACKING` FOREIGN KEY (`carrier_packing`) REFERENCES `carrier_packing` (`id`),
  CONSTRAINT `FK_PURCHASE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PURCHASE_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_PURCHASE_DETAIL_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PURCHASE_DETAIL_PROPOSAL_DETAIL` FOREIGN KEY (`proposal_detail`) REFERENCES `proposal_detail` (`id`),
  CONSTRAINT `FK_PURCHASE_DETAIL_PURCHASE` FOREIGN KEY (`purchase`) REFERENCES `purchase` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Pedido de Compra';

#
# Table structure for table `qualification`
#

CREATE TABLE `qualification` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Calificacion',
  `domain` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `code` char(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo de la Calificacion',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de la Calificacion',
  `min_value` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Limite inferior de la Calificacion',
  `max_value` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Limite superior de la Calificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_QUALIFICATION_DOMAIN` (`domain`),
  CONSTRAINT `FK_QUALIFICATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Calificaciones';

#
# Table structure for table `quality_skill`
#

CREATE TABLE `quality_skill` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Aptitud Calidad',
  `domain` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `code` char(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo de la Aptitud Calidad',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de la Aptitud Calidad',
  PRIMARY KEY (`id`),
  KEY `IDX_QUALITY_SKILL_DOMAIN` (`domain`),
  CONSTRAINT `FK_QUALITY_SKILL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Aptitudes Calidad';

#
# Table structure for table `question`
#

CREATE TABLE `question` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Pregunta esta activa o no',
  `question_text` varchar(255) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Texto de la Pregunta',
  `type` tinyint NOT NULL COMMENT 'Tipo de Pregunta',
  `argument` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Argumentacion de la Pregunta',
  `alias` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias de la Pregunta',
  PRIMARY KEY (`id`),
  KEY `IDX_QUESTION_DOMAIN` (`domain`),
  CONSTRAINT `FK_QUESTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Preguntas';

#
# Table structure for table `question_value`
#

CREATE TABLE `question_value` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `question` int NOT NULL COMMENT 'Identificador de la Pregunta',
  `value_text` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor de tipo texto',
  `value_number` decimal(15,3) DEFAULT NULL COMMENT 'Valor de tipo numerico',
  `value_date` datetime DEFAULT NULL COMMENT 'Valor de tipo fecha',
  PRIMARY KEY (`id`),
  KEY `IDX_QUESTION_VALUE_QUESTION` (`question`),
  KEY `IDX_QUESTION_VALUE_DOMAIN` (`domain`),
  CONSTRAINT `FK_QUESTION_VALUE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_QUESTION_VALUE_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Valores de Preguntas';

#
# Table structure for table `raddinfo`
#

CREATE TABLE `raddinfo` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int NOT NULL COMMENT 'Identificador de la Persona o Empresa',
  `attribute` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Atributo adicional',
  `value` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Valor del atributo adicional',
  `value_date` date NOT NULL COMMENT 'Fecha del valor del atributo',
  PRIMARY KEY (`id`),
  KEY `IDX_RADDINFO_REGISTRY` (`registry`),
  KEY `IDX_RADDINFO_DOMAIN` (`domain`),
  CONSTRAINT `FK_RADDINFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RADDINFO_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Informacion adicional de la Persona o Empresa';

#
# Table structure for table `raddress`
#

CREATE TABLE `raddress` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Direccion de la Persona o Empresa',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Registro de la Persona o Empresa',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT 'Tipo de Direccion',
  `recipient` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Destinatario',
  `street_type` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT 'CL' COMMENT 'Tipo de via',
  `address` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Primera parte de la Direccion',
  `number` varchar(12) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero',
  `address2` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Segunda parte de la Direccion',
  `address3` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tercera parte de la Direccion',
  `zip` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo Postal',
  `city` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Localidad',
  `geozone` int DEFAULT NULL COMMENT 'Identificador de la Zona Geografica',
  `alias` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias',
  `municipality_code` varchar(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del municipio',
  PRIMARY KEY (`id`),
  KEY `IDX_RADDRESS_REGISTRY` (`registry`),
  KEY `IDX_RADDRESS_GEOZONE` (`geozone`),
  KEY `IDX_RADDRESS_DOMAIN` (`domain`),
  CONSTRAINT `FK_RADDRESS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RADDRESS_GEOZONE` FOREIGN KEY (`geozone`) REFERENCES `geozone` (`id`),
  CONSTRAINT `FK_RADDRESS_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Direcciones de Personas o Empresas';

#
# Table structure for table `rattach`
#

CREATE TABLE `rattach` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Archivo Adjunto de la Persona o Empresa',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Registro de la Persona o Empresa',
  `category` int DEFAULT NULL COMMENT 'Categoria del Archivo Adjunto',
  `mimeType` tinyint DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Archivo Adjunto',
  `scope` int DEFAULT NULL COMMENT 'Ambito del Archivo Adjunto',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad del Archivo Adjunto',
  `attach_date` date DEFAULT NULL COMMENT 'Fecha del Archivo Adjunto',
  `drive_id` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `dparent_id` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_RATTACH_SCOPE` (`scope`),
  KEY `IDX_RATTACH_CATEGORY` (`category`),
  KEY `IDX_RATTACH_REGISTRY` (`registry`),
  KEY `IDX_RATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_RATTACH_CATEGORY` FOREIGN KEY (`category`) REFERENCES `category` (`id`),
  CONSTRAINT `FK_RATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RATTACH_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_RATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Personas o Empresas';

#
# Table structure for table `rattach_tag`
#

CREATE TABLE `rattach_tag` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `rattach` int NOT NULL COMMENT 'Identificador del Archivo Adjunto',
  `tag` int NOT NULL COMMENT 'Identificador de la Etiqueta',
  PRIMARY KEY (`id`),
  KEY `IDX_RATTACH_TAG_DOMAIN` (`domain`),
  KEY `IDX_RATTACH_TAG_RATTACH` (`rattach`),
  KEY `IDX_RATTACH_TAG_TAG` (`tag`),
  CONSTRAINT `FK_RATTACH_TAG_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RATTACH_TAG_RATTACH` FOREIGN KEY (`rattach`) REFERENCES `rattach` (`id`),
  CONSTRAINT `FK_RATTACH_TAG_TAG` FOREIGN KEY (`tag`) REFERENCES `tag` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Archivos Adjuntos y Etiquetas';

#
# Table structure for table `rawdoc`
#

CREATE TABLE `rawdoc` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del documento',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `nature` tinyint DEFAULT '0' COMMENT 'Naturaleza del documento (Factura, Nomina, etc )',
  `type` tinyint DEFAULT '0' COMMENT 'Tipo de Documento (Recibido o Emitido)',
  `status` tinyint DEFAULT '0' COMMENT 'Estado del Documento (Inbox, Rechazado, Papelera)',
  `json` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Documento en formato JSON',
  `log` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Documento en formato JSON',
  `mime_type` tinyint DEFAULT '0' COMMENT 'MIME Type',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_RAW_DOCUMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_RAW_DOCUMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Documentos a procesar';

#
# Table structure for table `rbank`
#

CREATE TABLE `rbank` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Cuenta Bancaria de la Persona o Empresa',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Registro de la Persona o Empresa',
  `bank_account` char(34) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'IBAN - Numero de Cuenta Bancaria Internacional',
  `bic` char(11) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'BIC - Codigo Identificador del Banco',
  `sufix` char(3) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Sufijo de Cuenta Bancaria para Remesas',
  `alias` varchar(25) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias de la Cuenta Bancaria',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si la Cuenta Bancaria esta activa o no',
  `account` int DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable',
  `requisition` varchar(50) COLLATE latin1_spanish_ci DEFAULT NULL,
  `sepa_mandate_ref` char(20) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mandate reference',
  `balance` decimal(15,4) DEFAULT 0.0000 COMMENT 'Saldo banco',
  `available_balance` decimal(15,4) DEFAULT 0.0000 COMMENT 'Saldo disponible banco',
  `balance_date` datetime DEFAULT NULL COMMENT 'Fecha actualizacion saldo',
  PRIMARY KEY (`id`),
  KEY `IDX_RBANK_REGISTRY` (`registry`),
  KEY `IDX_RBANK_DOMAIN` (`domain`),
  KEY `IDX_RBANK_ACCOUNT` (`account`),
  CONSTRAINT `FK_RBANK_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_RBANK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RBANK_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos de Cuentas Bancarias de Personas o Empresas';

#
# Table structure for table `rdir_staff`
#

CREATE TABLE `rdir_staff` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Relacion entre Empresas y sus Directivos',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int NOT NULL COMMENT 'Identificador de la Empresa',
  `document` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Numero de Documento del Directivo',
  `name` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Directivo',
  `shareholder` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el Directivo es socio',
  `representative` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el Directivo es representante legal',
  `director` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el Directivo es administrador',
  `percent_share` decimal(15,4) DEFAULT '0' COMMENT 'Porcentaje de acciones (solo para socios)',
  `share_number` int DEFAULT '0' COMMENT 'Numero de Acciones',
  `nominal_value` decimal(15,3) DEFAULT '0.000' COMMENT 'Valor Nominal',
  `due_date` date DEFAULT NULL COMMENT 'Fecha de vencimiento del cargo',
  `representative_labor` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el Directivo es representante laboral',
  `charge_description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del cargo de Directivo',
  PRIMARY KEY (`id`),
  KEY `IDX_RDIR_STAFF_REGISTRY` (`registry`),
  KEY `IDX_RDIR_STAFF_DOMAIN` (`domain`),
  CONSTRAINT `FK_RDIR_STAFF_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RDIR_STAFF_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Empresas y sus Directivos';

#
# Table structure for table `record_data`
#

CREATE TABLE `record_data` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Dato Registral',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int NOT NULL COMMENT 'Registro de la Empresa',
  `creation_date` date DEFAULT NULL COMMENT 'Fecha de creacion del Dato Registral',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Dato Registral',
  `notary` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Notario del Dato Registral',
  `number` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero del Dato Registral',
  `record_date` date DEFAULT NULL COMMENT 'Fecha de registro del Dato Registral',
  `volume` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tomo del Dato Registral',
  `section` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Seccion del Dato Registral',
  `page` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Folio del Dato Registral',
  `sheet` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Hoja del Dato Registral',
  `registration` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Inscripcion del Dato Registral',
  `attach` int DEFAULT NULL COMMENT 'Archivo adjunto',
  PRIMARY KEY (`id`),
  KEY `IDX_RECORD_DATA_RATTACH` (`attach`),
  KEY `IDX_RECORD_DATA_REGISTRY` (`registry`),
  KEY `IDX_RECORD_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_RECORD_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RECORD_DATA_RATTACH` FOREIGN KEY (`attach`) REFERENCES `rattach` (`id`),
  CONSTRAINT `FK_RECORD_DATA_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos Registrales';

#
# Table structure for table `registry`
#

CREATE TABLE `registry` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Persona o Empresa',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `document` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Documento de la Persona o Empresa',
  `document_type` tinyint DEFAULT '0' COMMENT 'Tipo de documento (NIF, CIF...)',
  `document_country` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT 'ES' COMMENT 'Pais del documento',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre de la Persona o Empresa',
  `alias` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias de la Persona o Empresa',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo (Persona o Empresa)',
  `nationality` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT 'ES' COMMENT 'Nacionalidad',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad',
  PRIMARY KEY (`id`),
  KEY `IDX_REGISTRY_NAME` (`name`),
  KEY `IDX_REGISTRY_DOCUMENT` (`document`),
  KEY `IDX_REGISTRY_DOMAIN` (`domain`),
  CONSTRAINT `FK_REGISTRY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Registro de Personas o Empresas';

#
# Table structure for table `relationship`
#

CREATE TABLE `relationship` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Tipo de Relacion',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Relacion',
  PRIMARY KEY (`id`),
  KEY `IDX_RELATIONSHIP_DOMAIN` (`domain`),
  CONSTRAINT `FK_RELATIONSHIP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Relaciones entre Personas y/o Empresas';

#
# Table structure for table `reservation_request`
#

CREATE TABLE `reservation_request` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `hotel` int NOT NULL COMMENT 'Identificador del Hotel',
  `code` varchar(48) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Localizador',
  `start_date` date NOT NULL COMMENT 'Fecha de entrada',
  `end_date` date NOT NULL COMMENT 'Fecha de salida',
  `agency` int DEFAULT NULL COMMENT 'Identificador de la agencia de viajes',
  `company` int DEFAULT NULL COMMENT 'Identificador de la empresa',
  `booking_holder` tinyint NOT NULL COMMENT 'Titular',
  `request_counter` smallint NOT NULL COMMENT 'Numero de solicitudes enviadas',
  `remarks` varchar(166) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Observaciones',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Solicitud esta activa o no',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_RESERVATION_REQUEST_DOMAIN` (`domain`),
  KEY `IDX_RESERVATION_REQUEST_HOTEL` (`hotel`),
  KEY `IDX_RESERVATION_REQUEST_AGENCY` (`agency`),
  KEY `IDX_RESERVATION_REQUEST_COMPANY` (`company`),
  CONSTRAINT `FK_RESERVATION_REQUEST_AGENCY` FOREIGN KEY (`agency`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_RESERVATION_REQUEST_COMPANY` FOREIGN KEY (`company`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_RESERVATION_REQUEST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RESERVATION_REQUEST_HOTEL` FOREIGN KEY (`hotel`) REFERENCES `hotel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Solicitud de Reservas';

#
# Table structure for table `reservation_request_guest`
#

CREATE TABLE `reservation_request_guest` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `reservation_request` int NOT NULL COMMENT 'Identificador de la Solicitud de Reserva',
  `guest_index` tinyint NOT NULL COMMENT 'Numero de Huesped',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre',
  `surname` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Apellidos',
  `email` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Email',
  `phone` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono',
  `address` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion',
  `number` varchar(12) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero',
  `address2` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Segunda parte de la Direccion',
  `zip` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo postal',
  `city` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Ciudad',
  `province` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Provincia',
  `country` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Pais',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_RESERVATION_REQUEST_GUEST_DOMAIN` (`domain`),
  KEY `IDX_RESERVATION_REQUEST_GUEST_RESERVATION_REQUEST` (`reservation_request`),
  CONSTRAINT `FK_RESERVATION_REQUEST_GUEST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RESERVATION_REQUEST_GUEST_RESERVATION_REQUEST` FOREIGN KEY (`reservation_request`) REFERENCES `reservation_request` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Huespedes por Solicitud de Reserva';

#
# Table structure for table `reservation_request_room`
#

CREATE TABLE `reservation_request_room` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `reservation_request` int NOT NULL COMMENT 'Identificador de la Solicitud de Reserva',
  `room_index` tinyint NOT NULL COMMENT 'Numero de Habitacion',
  `units` tinyint NOT NULL COMMENT 'Numero de Habitaciones',
  `item` int NOT NULL COMMENT 'Identificador del Tipo de Habitacion',
  `adults` smallint DEFAULT '0' COMMENT 'Numero de adultos',
  `children` smallint DEFAULT '0' COMMENT 'Numero de nios',
  `babies` smallint DEFAULT '0' COMMENT 'Numero de bebes',
  `crs_code` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de Reserva en CRS',
  `tariff_code` varchar(8) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de Tarifa',
  `tariff_description` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de Tarifa',
  `inventory_code` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de Servicio',
  `room_code` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de Habitacion',
  `room_description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de Habitacion',
  `meal_plan` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo de regimen',
  `daily_price` decimal(15,2) DEFAULT '0.00' COMMENT 'Importe Diario',
  `total_price` decimal(15,2) DEFAULT '0.00' COMMENT 'Importe Total',
  `agreed_price` decimal(15,2) DEFAULT '0.00' COMMENT 'Importe Pactado',
  `cancel_penalty` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Penalizaciones por cancelacion',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_RESERVATION_REQUEST_ROOM_DOMAIN` (`domain`),
  KEY `IDX_RESERVATION_REQUEST_ROOM_RESERVATION_REQUEST` (`reservation_request`),
  KEY `IDX_RESERVATION_REQUEST_ROOM_ITEM` (`item`),
  KEY `IDX_RESERVATION_REQUEST_ROOM_CRS_CODE` (`crs_code`),
  CONSTRAINT `FK_RESERVATION_REQUEST_ROOM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RESERVATION_REQUEST_ROOM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_RESERVATION_REQUEST_ROOM_RESERVATION_REQUEST` FOREIGN KEY (`reservation_request`) REFERENCES `reservation_request` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Habitaciones por Solicitud de Reserva';

#
# Table structure for table `ritem`
#

CREATE TABLE `ritem` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int NOT NULL COMMENT 'Identificador de Persona o Empresa',
  `item` int NOT NULL COMMENT 'Identificador del Articulo',
  `type` tinyint DEFAULT '0' COMMENT 'Tipo de relacion',
  `code` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del Producto',
  `edi_sales_code` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo EAN de ventas para EDI',
  `price` decimal(15,4) DEFAULT '0' COMMENT 'Precio del Producto',
  `discount_expr` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT '0.0' COMMENT 'Descuentos del Producto',
  `priority` tinyint DEFAULT '0' COMMENT 'Prioridad del Producto',
  `workplace` int DEFAULT NULL COMMENT 'Identificador del Centro de Trabajo',
  `status` tinyint NOT NULL COMMENT 'Estado',
  `quantity` varchar(30) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Cantidad',
  `start_date` date DEFAULT NULL COMMENT 'Fecha de inicio',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de fin',
  `creation_date` datetime DEFAULT NULL COMMENT 'Momento de creaci�n',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario creador',
  `modification_date` datetime DEFAULT NULL COMMENT 'Momento de modificaci�n',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario modificador',
  PRIMARY KEY (`id`),
  KEY `IDX_RITEM_DOMAIN` (`domain`),
  KEY `IDX_RITEM_ITEM` (`item`),
  KEY `IDX_RITEM_REGISTRY` (`registry`),
  KEY `IDX_RITEM_WORKPLACE` (`workplace`),
  CONSTRAINT `FK_RITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RITEM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_RITEM_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_RITEM_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulos interesados por Personas o Empresas';

#
# Table structure for table `rmedia`
#

CREATE TABLE `rmedia` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Medio de Contacto de la Persona o Empresa',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Registro de la Persona o Empresa',
  `media` tinyint NOT NULL DEFAULT '0' COMMENT 'Tipo de Medio de Contacto de la Persona o Empresa',
  `value` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor del Medio de Contacto de la Persona o Empresa',
  `comment` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Comentarios acerca del Medio de Contacto de la Persona o Empresa',
  `administrative` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Contacto es de caracter administrativo',
  `commercial` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Contacto es de caracter comercial',
  `technical` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Contacto es de caracter tecnico',
  `raddress` int DEFAULT NULL COMMENT 'Direccion del contacto',
  PRIMARY KEY (`id`),
  KEY `IDX_RMEDIA_RADDRESS` (`raddress`),
  KEY `IDX_RMEDIA_REGISTRY` (`registry`),
  KEY `IDX_RMEDIA_DOMAIN` (`domain`),
  CONSTRAINT `FK_RMEDIA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RMEDIA_RADDRESS` FOREIGN KEY (`raddress`) REFERENCES `raddress` (`id`),
  CONSTRAINT `FK_RMEDIA_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Medios de Contacto de Personas o Empresas';

#
# Table structure for table `rnote`
#

CREATE TABLE `rnote` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Nota de la Persona o Empresa',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Registro de la Persona o Empresa',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Nota',
  `note_date` date DEFAULT NULL COMMENT 'Fecha de la Nota',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Nota',
  `note_type` tinyint DEFAULT NULL,
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad de la Nota',
  PRIMARY KEY (`id`),
  KEY `IDX_RNOTE_REGISTRY` (`registry`),
  KEY `IDX_RNOTE_DOMAIN` (`domain`),
  CONSTRAINT `FK_RNOTE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RNOTE_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Notas de Personas o Empresas';

#
# Table structure for table `role`
#

CREATE TABLE `role` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Role',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Roles';

#
# Table structure for table `room`
#

CREATE TABLE `room` (
  `asset` int NOT NULL COMMENT 'Identificador del Activo',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `hotel` int NOT NULL COMMENT 'Identificador del Hotel',
  `item` int NOT NULL COMMENT 'Identificador del Producto',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT 'Estado de la Habitacion',
  `last_cleaning_date` datetime DEFAULT NULL COMMENT 'Ultima fecha de limpieza',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si la Habitacion esta activa o no',
  PRIMARY KEY (`asset`),
  KEY `IDX_ROOM_DOMAIN` (`domain`),
  KEY `IDX_ROOM_HOTEL` (`hotel`),
  KEY `IDX_ROOM_ITEM` (`item`),
  CONSTRAINT `FK_ROOM_ASSET` FOREIGN KEY (`asset`) REFERENCES `asset` (`id`),
  CONSTRAINT `FK_ROOM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ROOM_HOTEL` FOREIGN KEY (`hotel`) REFERENCES `hotel` (`id`),
  CONSTRAINT `FK_ROOM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Habitaciones de Hotel';

#
# Table structure for table `rpaymethod`
#

CREATE TABLE `rpaymethod` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Forma de Pago de la Persona o Empresa',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int NOT NULL COMMENT 'Identificador del Registro de la Persona o Empresa',
  `pay_method` int NOT NULL COMMENT 'Identificador de la Forma de Pago',
  `rbank` int DEFAULT NULL COMMENT 'Identificador de la Entidad Bancaria',
  `number_of_pymnts` smallint DEFAULT '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint DEFAULT '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint DEFAULT '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Dias de pago',
  PRIMARY KEY (`id`),
  KEY `IDX_RPAYMETHOD_PAY_METHOD` (`pay_method`),
  KEY `IDX_RPAYMETHOD_RBANK` (`rbank`),
  KEY `IDX_RPAYMETHOD_REGISTRY` (`registry`),
  KEY `IDX_RPAYMETHOD_DOMAIN` (`domain`),
  CONSTRAINT `FK_RPAYMETHOD_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RPAYMETHOD_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_RPAYMETHOD_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`),
  CONSTRAINT `FK_RPAYMETHOD_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos de la Forma de Pago de la Persona o Empresa';

#
# Table structure for table `rprofile`
#

CREATE TABLE `rprofile` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int NOT NULL COMMENT 'Identificador de Persona o Empresa',
  `last_update` datetime NOT NULL COMMENT 'Fecha de la ultima modificacion del Perfil del Cliente Potencial',
  `question` int NOT NULL COMMENT 'Identificador de la Pregunta',
  `value_text` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor de tipo texto',
  `value_number` decimal(15,3) DEFAULT NULL COMMENT 'Valor de tipo numerico',
  `value_date` datetime DEFAULT NULL COMMENT 'Valor de tipo fecha',
  PRIMARY KEY (`id`),
  KEY `IDX_RPROFILE_DOMAIN` (`domain`),
  KEY `IDX_RPROFILE_QUESTION` (`question`),
  KEY `IDX_RPROFILE_REGISTRY` (`registry`),
  CONSTRAINT `FK_RPROFILE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RPROFILE_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`),
  CONSTRAINT `FK_RPROFILE_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Perfiles de Personas o Empresas';

#
# Table structure for table `rrelationship`
#

CREATE TABLE `rrelationship` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Relacion',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int NOT NULL COMMENT 'Identificador de la Persona o Empresa que tiene la Relacion',
  `related_registry` int NOT NULL COMMENT 'Identificador de la Persona o Empresa relacionada',
  `relationship` int NOT NULL COMMENT 'Identificador del Tipo de Relación',
  `comments` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Comentarios de la Relacion',
  PRIMARY KEY (`id`),
  KEY `IDX_RRELATIONSHIP_REGISTRY` (`registry`),
  KEY `IDX_RRELATIONSHIP_RELATED_REGISTRY` (`related_registry`),
  KEY `IDX_RRELATIONSHIP_RELATIONSHIP` (`relationship`),
  KEY `IDX_RRELATIONSHIP_DOMAIN` (`domain`),
  CONSTRAINT `FK_RRELATIONSHIP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RRELATIONSHIP_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_RRELATIONSHIP_RELATED_REGISTRY` FOREIGN KEY (`related_registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_RRELATIONSHIP_RELATIONSHIP` FOREIGN KEY (`relationship`) REFERENCES `relationship` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relaciones entre Personas y/o Empresas';

#
# Table structure for table `rsegment`
#

CREATE TABLE `rsegment` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int NOT NULL COMMENT 'Identificador de Persona o Empresa',
  `segment` int NOT NULL COMMENT 'Identificador del Segmento',
  PRIMARY KEY (`id`),
  KEY `IDX_RSEGMENT_REGISTRY` (`registry`),
  KEY `IDX_RSEGMENT_SEGMENT` (`segment`),
  KEY `IDX_RSEGMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_RSEGMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RSEGMENT_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_RSEGMENT_SEGMENT` FOREIGN KEY (`segment`) REFERENCES `segment` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Segmentos de Personas o Empresas';

#
# Table structure for table `rseller`
#

CREATE TABLE `rseller` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int NOT NULL COMMENT 'Identificador de Persona o Empresa',
  `seller` int NOT NULL COMMENT 'Identificador del Comercial',
  `start_date` date NOT NULL COMMENT 'Fecha de Inicio',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de Fin',
  `status` tinyint NOT NULL COMMENT 'Estado',
  `type` tinyint DEFAULT 0 NOT NULL COMMENT 'Tipo',
  PRIMARY KEY (`id`),
  KEY `IDX_RSELLER_DOMAIN` (`domain`),
  KEY `IDX_RSELLER_SELLER` (`seller`),
  KEY `IDX_RSELLER_REGISTRY` (`registry`),
  CONSTRAINT `FK_RSELLER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RSELLER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_RSELLER_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comerciales relacionados con Personas o Empresas';

#
# Table structure for table `rsupplier`
#

CREATE TABLE `rsupplier` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int NOT NULL COMMENT 'Identificador de Persona o Empresa',
  `supplier` int NOT NULL COMMENT 'Identificador del Proveedor',
  `target_external_code` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del Cliente Potencial para el Proveedor',
  `tariff` int DEFAULT NULL COMMENT 'Identificador de Tarifa',
  `pay_method` int DEFAULT NULL COMMENT 'Identificador de la Forma de Pago',
  `number_of_pymnts` smallint DEFAULT '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint DEFAULT '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint DEFAULT '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Dias de pago',
  `bank_account` varchar(34) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'IBAN - Numero de Cuenta Bancaria Internacional',
  `bank_alias` varchar(25) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias del Banco',
  `bic` varchar(11) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'BIC - Codigo Identificador del Banco',
  PRIMARY KEY (`id`),
  KEY `IDX_RSUPPLIER_DOMAIN` (`domain`),
  KEY `IDX_RSUPPLIER_PAY_METHOD` (`pay_method`),
  KEY `IDX_RSUPPLIER_REGISTRY` (`registry`),
  KEY `IDX_RSUPPLIER_SUPPLIER` (`supplier`),
  KEY `IDX_RSUPPLIER_TARIFF` (`tariff`),
  CONSTRAINT `FK_RSUPPLIER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RSUPPLIER_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_RSUPPLIER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_RSUPPLIER_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`),
  CONSTRAINT `FK_RSUPPLIER_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Proveedores relacionados con Personas o Empresas';

#
# Table structure for table `rtax`
#

CREATE TABLE `rtax` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int NOT NULL COMMENT 'Identificador de Persona o Empresa',
  `tax` int NOT NULL COMMENT 'Identificador del Impuesto',
  `percentage` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Porcentaje de recargo actual',
  `surcharge` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Porcentaje de recargo de equivalencia actual',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio de vigencia',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de fin de vigencia',
  PRIMARY KEY (`id`),
  KEY `IDX_RTAX_DOMAIN` (`domain`),
  KEY `IDX_RTAX_REGISTRY` (`registry`),
  KEY `IDX_RTAX_TAX` (`tax`),
  CONSTRAINT `FK_RTAX_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RTAX_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_RTAX_TAX` FOREIGN KEY (`tax`) REFERENCES `tax` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Impuestos redefinidos para Personas o Empresas';

#
# Table structure for table `salary`
#

CREATE TABLE `salary` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Nomina',
  `contract` int NOT NULL COMMENT 'Contrato',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio liquidación',
  `end_date` date NOT NULL COMMENT 'Fecha de finalizacion liquidación',
  `enterprise_name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre de la empresa',
  `enterprise_address` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Domicilio de la empresa',
  `enterprise_document` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Documento de la Empresa',
  `ccc` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `ss_regime` tinyint NOT NULL DEFAULT '0' COMMENT 'Regimen de la Seguridad Social',
  `employee_name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del trabajador',
  `social_security_number` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de la seguridad social',
  `employee_document` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Documento de la Persona',
  `seniority_date` date DEFAULT NULL COMMENT 'Fecha de antiguedad',
  `quote_group` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Grupo de Cotización',
  `category` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Categoria o grupo profesional',
  `registration` int NOT NULL COMMENT 'Número libro de matricula',
  `time_units` int NOT NULL COMMENT 'total dias/horas',
  `total_payment` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Total devengado',
  `total_deduction` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Total a deducir',
  `total_liquid` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Liquido total a percibir',
  `total_enterprise` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Cuota total de la empresa',
  `issue_date` date NOT NULL COMMENT 'Fecha de emisión',
  `remuneration` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Remuneración mensual',
  `pro_ext_base` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Base prorraterreada de pagas extras',
  `it_base` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Base de IT',
  `raw_cgc_base` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Base efectiva de cotizacion por contingencias comunes ',
  `cgc_base` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Base de cotizacion por contingencias comunes',
  `hextra_base` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Base de cotizacion adicional por horas extraordinarias estructurales',
  `non_hextra_base` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Base de cotizacion adicional por horas extraordinarias no estructurales',
  `cgp_base` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Base de cotizacion por contingencias profesionales',
  `money_irpf_base` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Salario en dinero sujeto a retención I.R.P.F',
  `inkind_irpf_base` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Salario en especie sujeto a retención I.R.P.F',
  `irpf_base` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Base sujeta a retención I.R.P.F',
  `social_security_contributions` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Aportaciones a la Seguridad Social',
  `total_irpf` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Total retenciÃ³n aplicada ',
  `charge_date` date NOT NULL COMMENT 'Fecha de cobro',
  PRIMARY KEY (`id`),
  KEY `IDX_SALARY_CONTRACT` (`contract`),
  KEY `IDX_SALARY_DOMAIN` (`domain`),
  KEY `IDX_SALARY_SOCIAL_SECURITY_NUMBER` (`social_security_number`),
  CONSTRAINT `FK_SALARY_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_SALARY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Recibo del pago de salarios';

#
# Table structure for table `salary_bonus`
#

CREATE TABLE `salary_bonus` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `salary` int NOT NULL COMMENT 'Recibo del pago de salarios',
  `bonus_concept` varchar(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del concepto',
  `amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  PRIMARY KEY (`id`),
  KEY `IDX_SALARY_BONUS_SALARY` (`salary`),
  KEY `IDX_SALARY_BONUS_DOMAIN` (`domain`),
  CONSTRAINT `FK_SALARY_BONUS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALARY_BONUS_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bonificaciones';

#
# Table structure for table `salary_cost`
#

CREATE TABLE `salary_cost` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `salary` int NOT NULL COMMENT 'Recibo del pago de salarios',
  `amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de deduccion Salarial',
  `cost_concept` varchar(25) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del concepto',
  PRIMARY KEY (`id`),
  KEY `IDX_SALARY_COST_SALARY` (`salary`),
  KEY `IDX_SALARY_COST_DOMAIN` (`domain`),
  CONSTRAINT `FK_SALARY_COST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALARY_COST_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Costos';

#
# Table structure for table `salary_data`
#

CREATE TABLE `salary_data` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `expression` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `salary` int NOT NULL COMMENT 'Recibo del pago de salarios',
  PRIMARY KEY (`id`),
  KEY `IDX_SALARY_DATA_DOMAIN` (`domain`),
  KEY `IDX_SALARY_DATA_SALARY` (`salary`),
  CONSTRAINT `FK_SALARY_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALARY_DATA_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos de la nomina';

#
# Table structure for table `salary_deduction`
#

CREATE TABLE `salary_deduction` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `salary` int NOT NULL COMMENT 'Recibo del pago de salarios',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de deducción Salarial',
  `deduction_concept` varchar(25) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del concepto',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `expression` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Fórmula',
  `amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe',
  PRIMARY KEY (`id`),
  KEY `IDX_SALARY_DEDUCTION_SALARY` (`salary`),
  KEY `IDX_SALARY_DEDUCTION_DOMAIN` (`domain`),
  CONSTRAINT `FK_SALARY_DEDUCTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALARY_DEDUCTION_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Deducciones';

#
# Table structure for table `salary_embargo`
#

CREATE TABLE `salary_embargo` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `salary` int NOT NULL COMMENT 'Recibo del pago de salarios',
  `contract_embargo` int DEFAULT NULL COMMENT 'Embargo',
  `amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  PRIMARY KEY (`id`),
  KEY `IDX_SALARY_EMBARGO_CONTRACT_EMBARGO` (`contract_embargo`),
  KEY `IDX_SALARY_EMBARGO_SALARY` (`salary`),
  KEY `IDX_SALARY_EMBARGO_DOMAIN` (`domain`),
  CONSTRAINT `FK_SALARY_EMBARGO_CONTRACT_EMBARGO` FOREIGN KEY (`contract_embargo`) REFERENCES `contract_embargo` (`id`),
  CONSTRAINT `FK_SALARY_EMBARGO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALARY_EMBARGO_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Embargos';

#
# Table structure for table `salary_payment`
#

CREATE TABLE `salary_payment` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `salary` int NOT NULL COMMENT 'Recibo del pago de salarios',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Percepción Salarial',
  `payment_concept` varchar(25) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo',
  `description` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `expression` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Fórmula',
  `amount` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe',
  `irpf` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe I.R.P.F',
  `quote` decimal(15,3) DEFAULT '0.000' COMMENT 'Importe Cotizable',
  PRIMARY KEY (`id`),
  KEY `IDX_SALARY_PAYMENT_SALARY` (`salary`),
  KEY `IDX_SALARY_PAYMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_SALARY_PAYMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALARY_PAYMENT_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Percepciones salariales';

#
# Table structure for table `sales`
#

CREATE TABLE `sales` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Pedido de Venta',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `project` int DEFAULT NULL COMMENT 'Identificador del Proyecto',
  `customer` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Cliente',
  `series` char(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie del Pedido',
  `number` int NOT NULL DEFAULT '0' COMMENT 'Numero del Pedido',
  `purchase_reference` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de referencia del Pedido de Compra',
  `shipping_address` int DEFAULT NULL COMMENT 'Identificador de la Direccion de envio del Pedido',
  `seller` int DEFAULT NULL COMMENT 'Identificador del Agente Comercial',
  `discount_expr` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descuentos del Pedido',
  `issue_date` date DEFAULT NULL COMMENT 'Fecha de emision del Pedido',
  `pay_method` int DEFAULT NULL COMMENT 'Identificador de la Forma de Pago',
  `document_type` tinyint DEFAULT '0' COMMENT 'Tipo de Pedido',
  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad del Pedido',
  `status` tinyint DEFAULT '0' COMMENT 'Estado del Pedido',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios del Pedido',
  `remarks` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Observaciones del Pedido',
  `workplace` int NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `scope` int NOT NULL DEFAULT '1' COMMENT 'Ambito del Pedido',
  `number_of_pymnts` smallint DEFAULT '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint DEFAULT '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint DEFAULT '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Dias de pago',
  `bank_account` varchar(34) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'IBAN - Numero de Cuenta Bancaria Internacional',
  `bank_alias` varchar(25) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias del Banco',
  `bic` varchar(11) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'BIC - Codigo Identificador del Banco',
  `purchase_generated` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si se han generado los Pedidos de Compra derivados',
  `delivery_date` date DEFAULT NULL COMMENT 'Fecha de entrega',
  `carrier` int DEFAULT NULL COMMENT 'Identificador de la Agencia de Transporte',
  `carrier_packing` int DEFAULT NULL COMMENT 'Identificador de la Hoja de ruta',
  `shipping_alternative_address` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Primera parte de la Direccion de entrega',
  `shipping_alternative_address2` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Segunda parte de la Direccion de entrega',
  `shipping_alternative_zip` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo Postal de entrega',
  `shipping_alternative_city` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Localidad de entrega',
  `shipping_alternative_phone` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono de contacto de la entrega',
  `shipping_alternative_recipient` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Destinatario de la entrega',
  `shipping_contact` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del contacto para la entrega',
  `shipping_period` tinyint DEFAULT '0' COMMENT 'Tipo de periodo de entrega',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_SALES_DOMAIN_SERIES_NUMBER` (`domain`,`series`,`number`),
  KEY `IDX_SALES_SCOPE` (`scope`),
  KEY `IDX_SALES_PROJECT` (`project`),
  KEY `IDX_SALES_ISSUE_DATE` (`issue_date`),
  KEY `IDX_SALES_SELLER` (`seller`),
  KEY `IDX_SALES_CUSTOMER` (`customer`),
  KEY `IDX_SALES_RADDRESS` (`shipping_address`),
  KEY `IDX_SALES_PAY_METHOD` (`pay_method`),
  KEY `IDX_SALES_WORKPLACE` (`workplace`),
  KEY `IDX_SALES_DOMAIN` (`domain`),
  KEY `IDX_SALES_CARRIER` (`carrier`),
  CONSTRAINT `FK_SALES_CARRIER` FOREIGN KEY (`carrier`) REFERENCES `carrier` (`registry`),
  CONSTRAINT `FK_SALES_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_SALES_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALES_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_SALES_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_SALES_RADDRESS` FOREIGN KEY (`shipping_address`) REFERENCES `raddress` (`id`),
  CONSTRAINT `FK_SALES_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_SALES_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`),
  CONSTRAINT `FK_SALES_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Pedidos de Venta';

#
# Table structure for table `sales_detail`
#

CREATE TABLE `sales_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Detalle del Pedido de Venta',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `sales` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Pedido de Venta',
  `line` smallint DEFAULT '1' COMMENT 'Numero de linea del Detalle dentro del Pedido',
  `item` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Articulo del Detalle de Pedido',
  `description` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Detalle de Pedido',
  `quantity` decimal(15,3) DEFAULT '0.000' COMMENT 'Cantidad del Detalle de Pedido',
  `price` decimal(15,4) DEFAULT '0' COMMENT 'Precio del Detalle de Pedido',
  `discount_expr` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Descuentos del Detalle de Pedido',
  `taxes` decimal(15,3) DEFAULT '0.000' COMMENT 'Tasas del Detalle de Pedido',
  `status` tinyint DEFAULT '0' COMMENT 'Estado del Detalle de Pedido',
  `offer_detail` int DEFAULT NULL COMMENT 'Identificador del Detalle del Presupuesto Origen',
  `delivered` decimal(15,4) DEFAULT '0' COMMENT 'Cantidad entregada del Detalle de Pedido',
  `delivery_date` date DEFAULT NULL COMMENT 'Fecha de entrega',
  `carrier` int DEFAULT NULL COMMENT 'Identificador de la Agencia de Transporte',
  `carrier_packing` int DEFAULT NULL COMMENT 'Identificador de la Hoja de ruta',
  `delivery` int DEFAULT NULL COMMENT 'Identificador del Albaran de Venta',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_SALES_DETAIL_OFFER_DETAIL` (`offer_detail`),
  KEY `IDX_SALES_DETAIL_SALES` (`sales`),
  KEY `IDX_SALES_DETAIL_ITEM` (`item`),
  KEY `IDX_SALES_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_SALES_DETAIL_CARRIER` (`carrier`),
  KEY `IDX_SALES_DETAIL_CARRIER_PACKING` (`carrier_packing`),
  KEY `IDX_SALES_DETAIL_DELIVERY` (`delivery`),
  CONSTRAINT `FK_SALES_DETAIL_CARRIER` FOREIGN KEY (`carrier`) REFERENCES `carrier` (`registry`),
  CONSTRAINT `FK_SALES_DETAIL_CARRIER_PACKING` FOREIGN KEY (`carrier_packing`) REFERENCES `carrier_packing` (`id`),
  CONSTRAINT `FK_SALES_DETAIL_DELIVERY` FOREIGN KEY (`delivery`) REFERENCES `delivery` (`id`),
  CONSTRAINT `FK_SALES_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALES_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_SALES_DETAIL_OFFER_DETAIL` FOREIGN KEY (`offer_detail`) REFERENCES `offer_detail` (`id`),
  CONSTRAINT `FK_SALES_DETAIL_SALES` FOREIGN KEY (`sales`) REFERENCES `sales` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Pedido de Venta';


#
# Table structure for table `sales_info`
#
CREATE TABLE `sales_info` (
	`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
	`domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
	`sales` int(4) NOT NULL COMMENT 'Identificador del Pedido',
	`type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de Comunicacion',
	`status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Estado de la Comunicacion',
	PRIMARY KEY (`id`),
	KEY `IDX_SALES_INFO_DOMAIN` (`domain`),
	KEY `IDX_SALES_INFO_SALES` (`sales`),
	CONSTRAINT `FK_SALES_INFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
	CONSTRAINT `FK_SALES_INFO_SALES` FOREIGN KEY (`sales`) REFERENCES `sales` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Estado Comunicaciones de Pedidos';

#
# Table structure for table `scope`
#

CREATE TABLE `scope` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Ambito',
  PRIMARY KEY (`id`),
  KEY `IDX_SCOPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_SCOPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ambitos';

#
# Table structure for table `segment`
#

CREATE TABLE `segment` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Segmento',
  PRIMARY KEY (`id`),
  KEY `IDX_SEGMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_SEGMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Segmentos Comerciales';

#
# Table structure for table `seller`
#

CREATE TABLE `seller` (
  `registry` int NOT NULL DEFAULT '0' COMMENT 'Registro del Agente Comercial',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `commission_type` int DEFAULT NULL COMMENT 'Identificador del Tipo de Comision',
  `status` tinyint DEFAULT '0' COMMENT 'Estado del Agente Comercial',
  `scope` int NOT NULL COMMENT 'Identificador del Ambito',
  PRIMARY KEY (`registry`),
  KEY `IDX_SELLER_COMMISSION_TYPE` (`commission_type`),
  KEY `IDX_SELLER_DOMAIN` (`domain`),
  KEY `IDX_SELLER_SCOPE` (`scope`),
  CONSTRAINT `FK_SELLER_COMMISSION_TYPE` FOREIGN KEY (`commission_type`) REFERENCES `commission_type` (`id`),
  CONSTRAINT `FK_SELLER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SELLER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_SELLER_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Agentes Comerciales';

#
# Table structure for table `sepe_batch_attach`
#

CREATE TABLE `sepe_batch_attach` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Archivo Adjunto',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `source_batch` int NOT NULL DEFAULT '0' COMMENT 'Identificador de la remesa',
  `source_type` tinyint DEFAULT NULL COMMENT 'Tipo de la remesa',
  `mimeType` tinyint DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Archivo Adjunto',
  `scope` int DEFAULT NULL COMMENT 'Ambito del Archivo Adjunto',
  `attach_date` date DEFAULT NULL COMMENT 'Fecha del Archivo Adjunto',
  `driveId` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_SEPE_BATCH_ATTACH_SCOPE` (`scope`),
  KEY `IDX_SEPE_BATCH_ATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_SEPE_BATCH_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SEPE_BATCH_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de remesas de SEPE';

#
# Table structure for table `series`
#

CREATE TABLE `series` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `code` varchar(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Serie',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `scope` int NOT NULL COMMENT 'Identificador del Ambito',
  `description` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Serie',
  `tas` tinyint(1) DEFAULT '0' COMMENT 'Indica si es una Serie para Ordenes de Reparacion',
  `offer` tinyint(1) DEFAULT '0' COMMENT 'Indica si es una Serie para Presupuestos',
  `sales` tinyint(1) DEFAULT '0' COMMENT 'Indica si es una Serie para Pedidos',
  `delivery` tinyint(1) DEFAULT '0' COMMENT 'Indica si es una Serie para Albaranes',
  `invoice` tinyint(1) DEFAULT '0' COMMENT 'Indica si es una Serie para Facturas',
  `rectification` tinyint(1) DEFAULT '0' COMMENT 'Indica si es una Serie para Facturas rectificativas',
  `pos` tinyint(1) DEFAULT '0' COMMENT 'Indica si es una Serie para TPV',
  `security_level` tinyint NOT NULL COMMENT 'Nivel de seguridad de la Serie',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Serie esta activa o no',
  PRIMARY KEY (`id`),
  KEY `IDX_SERIES_DOMAIN` (`domain`),
  KEY `IDX_SERIES_SCOPE` (`scope`),
  CONSTRAINT `FK_SERIES_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SERIES_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Series';

#
# Table structure for table `session`
#

CREATE TABLE `session` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `endDate` datetime DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `remote_address` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'IP remota',
  `remote_host` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Equipo remoto',
  `session_id` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificador web de la sesión',
  `startDate` datetime NOT NULL COMMENT 'Fecha de inicio',
  `application` int NOT NULL COMMENT 'Identificador de la Aplicacion',
  `user_id` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Usuario',
  PRIMARY KEY (`id`),
  KEY `IDX_SESSION_APPLICATION` (`application`),
  KEY `IDX_SESSION_USER` (`user_id`),
  KEY `IDX_SESSION_DOMAIN` (`domain`),
  CONSTRAINT `FK_SESSION_APPLICATION` FOREIGN KEY (`application`) REFERENCES `application` (`id`),
  CONSTRAINT `FK_SESSION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SESSION_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Sesion web';

#
# Table structure for table `signature`
#

CREATE TABLE `signature` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Firma',
  `signature` text CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Texto de la Firma de la Cuenta de Correo',
  `user_id` int DEFAULT NULL COMMENT 'Identificador del Usuario',
  PRIMARY KEY (`id`),
  KEY `IDX_SIGNATURE_DOMAIN` (`domain`),
  KEY `IDX_SIGNATURE_USER` (`user_id`),
  CONSTRAINT `FK_SIGNATURE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SIGNATURE_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Firmas de Cuentas de Correo Electronico';

#
# Table structure for table `stock`
#

CREATE TABLE `stock` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Stock',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `warehouse` int DEFAULT NULL COMMENT 'Identificador del Almacen',
  `item` int DEFAULT NULL COMMENT 'Identificador del Articulo',
  `quantity` decimal(15,3) DEFAULT '0.000' COMMENT 'Cantidad del Articulo en el Almacen',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_STOCK_WAREHOUSE_ITEM` (`warehouse`,`item`),
  KEY `IDX_STOCK_WAREHOUSE` (`warehouse`),
  KEY `IDX_STOCK_ITEM` (`item`),
  KEY `IDX_STOCK_DOMAIN` (`domain`),
  CONSTRAINT `FK_STOCK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_STOCK_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_STOCK_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Stocks de Almacenes';

#
# Table structure for table `stop_sales`
#

CREATE TABLE `stop_sales` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `hotel` int NOT NULL COMMENT 'Identificador del Hotel',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio del Paro',
  `end_date` date NOT NULL COMMENT 'Fecha de fin del Paro',
  `remarks` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si el Paro esta activo o no',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_STOP_SALES_DOMAIN` (`domain`),
  KEY `IDX_STOP_SALES_HOTEL` (`hotel`),
  CONSTRAINT `FK_STOP_SALES_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_STOP_SALES_HOTEL` FOREIGN KEY (`hotel`) REFERENCES `hotel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Paros de venta';

#
# Table structure for table `stop_sales_item`
#

CREATE TABLE `stop_sales_item` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `stop_sales` int NOT NULL COMMENT 'Identificador del Paro de ventas',
  `item` int NOT NULL COMMENT 'Identificador del Tipo de Habitacion',
  PRIMARY KEY (`id`),
  KEY `IDX_STOP_SALES_ITEM_DOMAIN` (`domain`),
  KEY `IDX_STOP_SALES_ITEM_STOP_SALES` (`stop_sales`),
  KEY `IDX_STOP_SALES_ITEM_ITEM` (`item`),
  CONSTRAINT `FK_STOP_SALES_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_STOP_SALES_ITEM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_STOP_SALES_ITEM_STOP_SALES` FOREIGN KEY (`stop_sales`) REFERENCES `stop_sales` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Habitacion por Paro';

#
# Table structure for table `stop_sales_tariff`
#

CREATE TABLE `stop_sales_tariff` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `stop_sales` int NOT NULL COMMENT 'Identificador del Paro de ventas',
  `tariff` int NOT NULL COMMENT 'Identificador de la Tarifa',
  PRIMARY KEY (`id`),
  KEY `IDX_STOP_SALES_TARIFF_DOMAIN` (`domain`),
  KEY `IDX_STOP_SALES_TARIFF_STOP_SALES` (`stop_sales`),
  KEY `IDX_STOP_SALES_TARIFF_TARIFF` (`tariff`),
  CONSTRAINT `FK_STOP_SALES_TARIFF_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_STOP_SALES_TARIFF_STOP_SALES` FOREIGN KEY (`stop_sales`) REFERENCES `stop_sales` (`id`),
  CONSTRAINT `FK_STOP_SALES_TARIFF_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas por Paro';

#
# Table structure for table `supplier`
#

CREATE TABLE `supplier` (
  `registry` int NOT NULL DEFAULT '0' COMMENT 'Registro del Proveedor',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `tariff` int DEFAULT NULL COMMENT 'Tarifa asociada al Proveedor',
  `withholding` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Proveedor aplica retencion de impuestos',
  `withholding_farmer` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Proveedor pertenece al Regimen Especial de Agricultura y Pesca',
  `vat_accrual_payment` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Proveedor esta acogido al Regimen Especial de Criterio de Caja',
  `transaction` tinyint DEFAULT '0' COMMENT 'Tipo de transacciones del Proveedor',
  `status` tinyint DEFAULT NULL COMMENT 'Estado del Proveedor',
  `scope` int NOT NULL COMMENT 'Identificador del Ambito',
  `purchase_valuated` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Pedido se imprime valorado segun el Proveedor',
  `account` int DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`registry`),
  KEY `IDX_SUPPLIER_SCOPE` (`scope`),
  KEY `IDX_SUPPLIER_DOMAIN` (`domain`),
  KEY `IDX_SUPPLIER_ACCOUNT` (`account`),
  KEY `IDX_SUPPLIER_TARIFF` (`tariff`),
  CONSTRAINT `FK_SUPPLIER_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_SUPPLIER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SUPPLIER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_SUPPLIER_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_SUPPLIER_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Proveedores';

#
# Table structure for table `survey`
#

CREATE TABLE `survey` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `scope` int NOT NULL COMMENT 'Identificador del Ambito',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si el Cuestionario esta activa o no',
  `creationDate` datetime NOT NULL COMMENT 'Fecha de creacion del Cuestionario',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Cuestionario',
  PRIMARY KEY (`id`),
  KEY `IDX_SURVEY_DOMAIN` (`domain`),
  KEY `IDX_SURVEY_SCOPE` (`scope`),
  CONSTRAINT `FK_SURVEY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SURVEY_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuestionarios';

#
# Table structure for table `survey_question`
#

CREATE TABLE `survey_question` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `survey` int NOT NULL COMMENT 'Identificador del Cuestionario',
  `question` int NOT NULL COMMENT 'Identificador de la Pregunta',
  `position` int DEFAULT NULL COMMENT 'Posicion de la Pregunta dentro del Cuestionario',
  PRIMARY KEY (`id`),
  KEY `IDX_SURVEY_QUESTION_QUESTION` (`question`),
  KEY `IDX_SURVEY_QUESTION_SURVEY` (`survey`),
  KEY `IDX_SURVEY_QUESTION_DOMAIN` (`domain`),
  CONSTRAINT `FK_SURVEY_QUESTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SURVEY_QUESTION_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`),
  CONSTRAINT `FK_SURVEY_QUESTION_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Preguntas de Cuestionarios';

#
# Table structure for table `survey_response`
#

CREATE TABLE `survey_response` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `creationDate` datetime NOT NULL COMMENT 'Fecha de la creacion en el sistema de la Respuesta del Cuestionario',
  `response_date` datetime NOT NULL COMMENT 'Fecha de la Respuesta del Cuestionario',
  `survey` int NOT NULL COMMENT 'Identificador del Cuestionario',
  `registry` int NOT NULL COMMENT 'Identificador del Registro que responde al Cuestionario',
  `user` int DEFAULT NULL COMMENT 'Identificador del Usuario',
  `campaign_action` int DEFAULT NULL COMMENT 'Identificador de la Accion de la Campaña',
  PRIMARY KEY (`id`),
  KEY `IDX_SURVEY_RESPONSE_MK_ACTION` (`campaign_action`),
  KEY `IDX_SURVEY_RESPONSE_SURVEY` (`survey`),
  KEY `IDX_SURVEY_RESPONSE_USER` (`user`),
  KEY `IDX_SURVEY_RESPONSE_DOMAIN` (`domain`),
  KEY `IDX_SURVEY_RESPONSE_REGISTRY` (`registry`),
  CONSTRAINT `FK_SURVEY_RESPONSE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SURVEY_RESPONSE_MK_ACTION` FOREIGN KEY (`campaign_action`) REFERENCES `mk_action` (`id`),
  CONSTRAINT `FK_SURVEY_RESPONSE_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_SURVEY_RESPONSE_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`),
  CONSTRAINT `FK_SURVEY_RESPONSE_USER` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Respuestas de Cuestionarios';

#
# Table structure for table `survey_response_detail`
#

CREATE TABLE `survey_response_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `value_text` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor de tipo texto',
  `value_number` decimal(15,3) DEFAULT NULL COMMENT 'Valor de tipo numerico',
  `value_date` datetime DEFAULT NULL COMMENT 'Valor de tipo fecha',
  `question` int NOT NULL COMMENT 'Identificador de la Pregunta',
  `surveyResponse` int NOT NULL COMMENT 'Identificador de la Respuesta del Cuestionario',
  PRIMARY KEY (`id`),
  KEY `IDX_SURVEY_RESPONSE_DETAIL_QUESTION` (`question`),
  KEY `IDX_SURVEY_RESPONSE_DETAIL_SURVEY_RESPONSE` (`surveyResponse`),
  KEY `IDX_SURVEY_RESPONSE_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_SURVEY_RESPONSE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SURVEY_RESPONSE_DETAIL_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`),
  CONSTRAINT `FK_SURVEY_RESPONSE_DETAIL_SURVEY_RESPONSE` FOREIGN KEY (`surveyResponse`) REFERENCES `survey_response` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Respuestas de Cuestionarios';

#
# Table structure for table `survey_workflow`
#

CREATE TABLE `survey_workflow` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `questionValue` int DEFAULT NULL COMMENT 'Identificador del Valor de la Pregunta',
  `surveyQuestion` int NOT NULL COMMENT 'Identificador de la Pregunta del Cuestionario',
  `nextSurveyQuestion` int NOT NULL COMMENT 'Identificador de la siguiente Pregunta del Cuestionario',
  `operator` tinyint DEFAULT NULL COMMENT 'Operador a utilizar con el Valor',
  `value_text` varchar(1024) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor de tipo texto',
  `value_number` decimal(15,3) DEFAULT NULL COMMENT 'Valor de tipo numerico',
  `value_date` datetime DEFAULT NULL COMMENT 'Valor de tipo fecha',
  PRIMARY KEY (`id`),
  KEY `IDX_SURVEY_WORKFLOW_NEXT_SURVEY_QUESTION` (`nextSurveyQuestion`),
  KEY `IDX_SURVEY_WORKFLOW_QUESTION_VALUE` (`questionValue`),
  KEY `IDX_SURVEY_WORKFLOW_SURVEY_QUESTION` (`surveyQuestion`),
  KEY `IDX_SURVEY_WORKFLOW_DOMAIN` (`domain`),
  CONSTRAINT `FK_SURVEY_WORKFLOW_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SURVEY_WORKFLOW_NEXT_SURVEY_QUESTION` FOREIGN KEY (`nextSurveyQuestion`) REFERENCES `survey_question` (`id`),
  CONSTRAINT `FK_SURVEY_WORKFLOW_QUESTION_VALUE` FOREIGN KEY (`questionValue`) REFERENCES `question_value` (`id`),
  CONSTRAINT `FK_SURVEY_WORKFLOW_SURVEY_QUESTION` FOREIGN KEY (`surveyQuestion`) REFERENCES `survey_question` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Secuencias de Cuestionarios';

#
# Table structure for table `system_cost`
#

CREATE TABLE `system_cost` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `expression` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Expresion',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Costo',
  `code` varchar(25) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Código',
  PRIMARY KEY (`id`),
  KEY `IDX_SYSTEM_COST_DOMAIN` (`domain`),
  CONSTRAINT `FK_SYSTEM_COST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Costos';

#
# Table structure for table `system_data`
#

CREATE TABLE `system_data` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `expression` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `read_only` tinyint(1) DEFAULT NULL COMMENT 'Modificable',
  `comments` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Comentario de ayuda',
  PRIMARY KEY (`id`),
  KEY `IDX_SYSTEM_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_SYSTEM_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto de las funciones';

#
# Table structure for table `system_deduction`
#

CREATE TABLE `system_deduction` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Deducción',
  `deduction_concept` int DEFAULT NULL COMMENT 'Identificador unico del concepto',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `description_decorable` tinyint NOT NULL DEFAULT '0',
  `expression` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Fórmula',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `month` tinyint DEFAULT NULL COMMENT 'Mes de la deducción',
  PRIMARY KEY (`id`),
  KEY `IDX_SYSTEM_DEDUCTION_DEDUCTION_CONCEPT` (`deduction_concept`),
  KEY `IDX_SYSTEM_DEDUCTION_DOMAIN` (`domain`),
  CONSTRAINT `FK_SYSTEM_DEDUCTION_DEDUCTION_CONCEPT` FOREIGN KEY (`deduction_concept`) REFERENCES `deduction_concept` (`id`),
  CONSTRAINT `FK_SYSTEM_DEDUCTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Deducciones';

#
# Table structure for table `system_payment`
#

CREATE TABLE `system_payment` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo de Percepción Salarial',
  `payment_concept` int DEFAULT NULL COMMENT 'Identificador unico del concepto',
  `description` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `description_decorable` tinyint NOT NULL DEFAULT '0',
  `expression` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `irpf_expression` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe tributable',
  `quote_expression` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe cotizable',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `month` tinyint DEFAULT NULL COMMENT 'Mes de la percepcion',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `salary_type` tinyint DEFAULT NULL COMMENT 'Tipo de Nomina/Recibo',
  PRIMARY KEY (`id`),
  KEY `IDX_SYSTEM_PAYMENT_PAYMENT_CONCEPT` (`payment_concept`),
  KEY `IDX_SYSTEM_PAYMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_SYSTEM_PAYMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SYSTEM_PAYMENT_PAYMENT_CONCEPT` FOREIGN KEY (`payment_concept`) REFERENCES `payment_concept` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Percepciones Salariales';

#
# Table structure for table `tag`
#

CREATE TABLE `tag` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Etiqueta',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT 'Tipo de Etiqueta',
  `color` varchar(24) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Color de la Etiqueta',
  PRIMARY KEY (`id`),
  KEY `IDX_TAG_DOMAIN` (`domain`),
  CONSTRAINT `FK_TAG_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Etiquetas';

#
# Table structure for table `target`
#

CREATE TABLE `target` (
  `registry` int NOT NULL DEFAULT '0' COMMENT 'Registro del Cliente Potencial',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `tariff` int DEFAULT NULL COMMENT 'Tarifa asociada al Cliente Potencial',
  `advertising` tinyint NOT NULL DEFAULT '0' COMMENT 'Admision de Publicidad',
  `surcharge` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Cliente Potencial tiene recargo de equivalencia',
  `withholding` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Cliente Potencial aplica retencion de impuestos',
  `transaction` tinyint DEFAULT '0' COMMENT 'Tipo de transacciones del Cliente Potencial',
  `status` tinyint DEFAULT '0' COMMENT 'Estado del Cliente Potencial',
  `scope` int NOT NULL DEFAULT '1' COMMENT 'Identificador del Ambito',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`registry`),
  KEY `IDX_TARGET_TARIFF` (`tariff`),
  KEY `IDX_TARGET_SCOPE` (`scope`),
  KEY `IDX_TARGET_DOMAIN` (`domain`),
  CONSTRAINT `FK_TARGET_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TARGET_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_TARGET_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_TARGET_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Clientes Potenciales';

#
# Table structure for table `tariff`
#

CREATE TABLE `tariff` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Tarifa',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `code` varchar(8) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de la Tarifa',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Tarifa',
  `purchase` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si se trata de una Tarifa de Compras o Ventas',
  `discount` decimal(6,2) DEFAULT '0.00' COMMENT 'Descuento general de la Tarifa',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si la Tarifa esta activa o no',
  PRIMARY KEY (`id`),
  KEY `IDX_TARIFF_DOMAIN` (`domain`),
  CONSTRAINT `FK_TARIFF_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas';

#
# Table structure for table `tariff_addinfo`
#

CREATE TABLE `tariff_addinfo` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `tariff` int NOT NULL COMMENT 'Identificador de la Tarifa',
  `attribute` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Atributo adicional',
  `value` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Valor del atributo adicional',
  `value_date` date NOT NULL COMMENT 'Fecha del valor del atributo',
  PRIMARY KEY (`id`),
  KEY `IDX_TARIFF_ADDINFO_DOMAIN` (`domain`),
  KEY `IDX_TARIFF_ADDINFO_TARIFF` (`tariff`),
  CONSTRAINT `FK_TARIFF_ADDINFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TARIFF_ADDINFO_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Informacion adicional de la Tarifa';

#
# Table structure for table `tariff_catalogue`
#

CREATE TABLE `tariff_catalogue` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `tariff` int NOT NULL COMMENT 'Identificador de la Tarifa',
  `catalogue` int NOT NULL COMMENT 'Identificador del Catalogo',
  PRIMARY KEY (`id`),
  KEY `IDX_TARIFF_CATALOGUE_TARIFF` (`tariff`),
  KEY `IDX_TARIFF_CATALOGUE_CATALOGUE` (`catalogue`),
  KEY `IDX_TARIFF_CATALOGUE_DOMAIN` (`domain`),
  CONSTRAINT `FK_TARIFF_CATALOGUE_CATALOGUE` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`),
  CONSTRAINT `FK_TARIFF_CATALOGUE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TARIFF_CATALOGUE_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas por Catalogo';

#
# Table structure for table `tas_item`
#

CREATE TABLE `tas_item` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Articulo',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `model` int NOT NULL COMMENT 'Identificador del Modelo',
  `publicCode` varchar(25) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo publico del Articulo',
  `privateCode` varchar(25) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo privado del Articulo',
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Articulo',
  `add_info` varchar(255) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Informacion adicional del Articulo',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_TAS_ITEM_DOMAIN_PUBLIC_CODE` (`domain`,`publicCode`),
  UNIQUE KEY `IDX_UNQ_TAS_ITEM_DOMAIN_PRIVATE_CODE` (`domain`,`privateCode`),
  KEY `IDX_TAS_ITEM_MODEL` (`model`),
  KEY `IDX_TAS_ITEM_DOMAIN` (`domain`),
  CONSTRAINT `FK_TAS_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TAS_ITEM_MODEL` FOREIGN KEY (`model`) REFERENCES `model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulo susceptible de Asistencia Tecnica';

#
# Table structure for table `task`
#

CREATE TABLE `task` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Tarea',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `number` int DEFAULT NULL COMMENT 'Numero de la Tarea',
  `description` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Tarea',
  `start_date` datetime NOT NULL COMMENT 'Fecha de inicio de la Tarea',
  `end_date` datetime DEFAULT NULL COMMENT 'Fecha de finalizacion de la Tarea',
  `due_date` datetime DEFAULT NULL COMMENT 'Fecha de vencimiento de la Tarea',
  `priority` tinyint DEFAULT '0' COMMENT 'Prioridad de la Tarea',
  `status` tinyint DEFAULT '0' COMMENT 'Estado de la Tarea',
  `percent` tinyint DEFAULT '0' COMMENT 'Porcentaje de realizacion de la Tarea',
  `task_holder` int DEFAULT NULL COMMENT 'Identificador del Usuario asociado a la Tarea',
  `workgroup` int DEFAULT NULL COMMENT 'Identificador del Grupo de Trabajo asociado a la Tarea',
  `source` tinyint DEFAULT NULL COMMENT 'Origen de la Tarea',
  `source_id` int DEFAULT NULL COMMENT 'Identificador del source',
  `project` int DEFAULT NULL COMMENT 'Identificador del Expediente',
  `registry` int DEFAULT NULL,
  `activity_type` int DEFAULT NULL COMMENT 'Identificador de la Actividad',
  `sender` int DEFAULT NULL COMMENT 'Remitente de la Tarea',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Tarea',
  `repeat_period` tinyint DEFAULT '0' COMMENT 'Periodo de repeticion de la Tarea',
  `gtask_id` varchar(100) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `gtasklist_id` varchar(100) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `parent` int DEFAULT NULL COMMENT 'Task parent',
  `evaluation` tinyint DEFAULT NULL COMMENT 'Calificacion',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion de la Tarea',
  PRIMARY KEY (`id`),
  KEY `IDX_TASK_ACTIVITY_TYPE` (`activity_type`),
  KEY `IDX_TASK_PROJECT` (`project`),
  KEY `IDX_TASK_REGISTRY` (`registry`),
  KEY `IDX_TASK_SENDER` (`sender`),
  KEY `IDX_TASK_TASK_HOLDER` (`task_holder`),
  KEY `IDX_TASK_WORKGROUP` (`workgroup`),
  KEY `IDX_TASK_DOMAIN` (`domain`),
  CONSTRAINT `FK_TASK_ACTIVITY_TYPE` FOREIGN KEY (`activity_type`) REFERENCES `activity_type` (`id`),
  CONSTRAINT `FK_TASK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TASK_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_TASK_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_TASK_SENDER` FOREIGN KEY (`sender`) REFERENCES `task_holder` (`registry`),
  CONSTRAINT `FK_TASK_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`),
  CONSTRAINT `FK_TASK_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tareas';

#
# Table structure for table `task_attach`
#

CREATE TABLE `task_attach` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `task` int NOT NULL COMMENT 'Identificador de la tarea',
  `task_workflow` int DEFAULT NULL COMMENT 'Identificador del Flujo de Tareas',
  `mimeType` tinyint DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  PRIMARY KEY (`id`),
  KEY `IDX_TASK_ATTACH_DOMAIN` (`domain`),
  KEY `IDX_TASK_ATTACH_TASK` (`task`),
  KEY `IDX_TASK_ATTACH_TASK_WORKFLOW` (`task_workflow`),
  CONSTRAINT `FK_TASK_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TASK_ATTACH_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`),
  CONSTRAINT `FK_TASK_ATTACH_TASK_WORKFLOW` FOREIGN KEY (`task_workflow`) REFERENCES `task_workflow` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Tareas, Flujo de Tareas y Archivos adjuntos';

#
# Table structure for table `task_comment`
#

CREATE TABLE `task_comment` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `task` int NOT NULL COMMENT 'Identificador de la tarea',
  `comment` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentario de la Tarea',
  `source` int DEFAULT NULL COMMENT 'Origen del comentario',
  `source_id` int DEFAULT NULL COMMENT 'Identificador del origen',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_TASK_COMMENT_DOMAIN` (`domain`),
  KEY `IDX_TASK_COMMENT_TASK` (`task`),
  CONSTRAINT `FK_TASK_COMMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TASK_COMMENT_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Tareas y Comentarios';

#
# Table structure for table `task_event`
#

CREATE TABLE `task_event` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `task` int NOT NULL COMMENT 'Identificador de la tarea',
  `event` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Evento de la Tarea',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_TASK_EVENT_DOMAIN` (`domain`),
  KEY `IDX_TASK_EVENT_TASK` (`task`),
  CONSTRAINT `FK_TASK_EVENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TASK_EVENT_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Tareas y Eventos';

#
# Table structure for table `task_holder`
#

CREATE TABLE `task_holder` (
  `registry` int NOT NULL DEFAULT '0' COMMENT 'Registro de la Entidad susceptible de Recibir Tareas',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint DEFAULT '0' COMMENT 'Tipo de Entidad susceptible de Recibir Tareas',
  `active` tinyint(1) DEFAULT '1' COMMENT 'Indica si dicha Entidad esta activa o no',
  `user_id` int DEFAULT NULL COMMENT 'Identificador del Usuario',
  `cost_profile` int DEFAULT NULL COMMENT 'Identificador del Perfil de Costos',
  PRIMARY KEY (`registry`),
  KEY `IDX_TASK_HOLDER_USER` (`user_id`),
  KEY `IDX_TASK_HOLDER_COST_PROFILE` (`cost_profile`),
  KEY `IDX_TASK_HOLDER_DOMAIN` (`domain`),
  CONSTRAINT `FK_TASK_HOLDER_COST_PROFILE` FOREIGN KEY (`cost_profile`) REFERENCES `cost_profile` (`id`),
  CONSTRAINT `FK_TASK_HOLDER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TASK_HOLDER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_TASK_HOLDER_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Titulares de Tareas';

#
# Table structure for table `task_holder_workgroup`
#

CREATE TABLE `task_holder_workgroup` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `task_holder` int NOT NULL COMMENT 'Identificador del Responsable de la Tarea',
  `workgroup` int NOT NULL COMMENT 'Identificador del Grupo de Trabajo',
  PRIMARY KEY (`id`),
  KEY `IDX_TASK_HOLDER_WORKGROUP_TASK_HOLDER` (`task_holder`),
  KEY `IDX_TASK_HOLDER_WORKGROUP_WORKGROUP` (`workgroup`),
  KEY `IDX_TASK_HOLDER_WORKGROUP_DOMAIN` (`domain`),
  CONSTRAINT `FK_TASK_HOLDER_WORKGROUP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TASK_HOLDER_WORKGROUP_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`),
  CONSTRAINT `FK_TASK_HOLDER_WORKGROUP_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Usuarios y Grupos de Trabajo';

#
# Table structure for table `task_tag`
#

CREATE TABLE `task_tag` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `task` int NOT NULL COMMENT 'Identificador de la tarea',
  `tag` int NOT NULL COMMENT 'Identificador de la Etiqueta',
  PRIMARY KEY (`id`),
  KEY `IDX_TASK_TAG_DOMAIN` (`domain`),
  KEY `IDX_TASK_TAG_TASK` (`task`),
  KEY `IDX_TASK_TAG_TAG` (`tag`),
  CONSTRAINT `FK_TASK_TAG_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TASK_TAG_TAG` FOREIGN KEY (`tag`) REFERENCES `tag` (`id`),
  CONSTRAINT `FK_TASK_TAG_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Tareas y Etiquetas';

#
# Table structure for table `task_workflow`
#

CREATE TABLE `task_workflow` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `task` int NOT NULL COMMENT 'Identificador de la tarea',
  `task_holder` int DEFAULT NULL COMMENT 'Identificador del Operario',
  `email` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Email del emisor',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo del flujo de Tareas',
  `comment` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentario de la Tarea',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  `notification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `notification_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `IDX_TASK_WORKFLOW_DOMAIN` (`domain`),
  KEY `IDX_TASK_WORKFLOW_TASK` (`task`),
  KEY `IDX_TASK_WORKFLOW_TASK_HOLDER` (`task_holder`),
  CONSTRAINT `FK_TASK_WORKFLOW_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TASK_WORKFLOW_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`),
  CONSTRAINT `FK_TASK_WORKFLOW_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Tareas y Flujo de Tareas';

#
# Table structure for table `tax`
#

CREATE TABLE `tax` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Impuesto',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(30) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Impuesto',
  `tax_type` tinyint NOT NULL DEFAULT '0' COMMENT 'Tipo de Impuesto',
  `percentage` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Porcentaje de recargo actual',
  `surcharge` decimal(15,3) DEFAULT '0.000' COMMENT 'Porcentaje de recargo de equivalencia actual',
  `start_date` date DEFAULT NULL COMMENT 'Fecha de inicio de vigencia',
  `vat_deduction_type` tinyint DEFAULT '0' COMMENT 'Tipo de deduccion del IVA',
  `withholding_type` tinyint DEFAULT '0' COMMENT 'Tipo de retencion',
  `sales_account` int DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable de Ventas',
  `purchase_account` int DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable de Compras',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_TAX_DOMAIN` (`domain`),
  KEY `IDX_TAX_ACCOUNT_SALES` (`sales_account`),
  KEY `IDX_TAX_ACCOUNT_PURCHASE` (`purchase_account`),
  CONSTRAINT `FK_TAX_ACCOUNT_PURCHASE` FOREIGN KEY (`purchase_account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_TAX_ACCOUNT_SALES` FOREIGN KEY (`sales_account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_TAX_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Impuestos';

#
# Table structure for table `tax_detail`
#

CREATE TABLE `tax_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Historico de Impuestos',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `tax` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Impuesto',
  `start_date` date DEFAULT NULL COMMENT 'Fecha de inicio de vigencia',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de fin de vigencia',
  `value` decimal(15,3) DEFAULT NULL COMMENT 'Porcentaje de recargo',
  `surcharge` decimal(15,3) DEFAULT NULL COMMENT 'Porcentaje de recargo de equivalencia',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_TAX_DETAIL_TAX` (`tax`),
  KEY `IDX_TAX_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_TAX_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TAX_DETAIL_TAX` FOREIGN KEY (`tax`) REFERENCES `tax` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Historico de Impuestos';

#
# Table structure for table `timecontrol`
#

CREATE TABLE `timecontrol` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',
  `domain` int NOT NULL COMMENT 'Dominio',
  `task_holder` int NOT NULL COMMENT 'Identificador del operario',
  `status` tinyint NOT NULL COMMENT 'Estado del control de horario',
  `date` datetime NOT NULL COMMENT 'Fecha del control de horario',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios del control de horario',
  `location` int DEFAULT NULL COMMENT 'Ubicación del Operario',
  `latitude` double DEFAULT NULL COMMENT 'Latitud de las coordenadas.',
  `longitude` double DEFAULT NULL COMMENT 'Longitud de las coordenadas.',
  `cause` tinyint DEFAULT '0' COMMENT 'causa de fichaje',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  `modificated_timecontrol` int DEFAULT NULL COMMENT 'id timecontrol modificado',
  PRIMARY KEY (`id`),
  KEY `IDX_TIMECONTROL_DOMAIN` (`domain`),
  KEY `IDX_TIMECONTROL_TASK_HOLDER` (`task_holder`),
  KEY `IDX_TIMECONTROL_LOCATION` (`location`),
  CONSTRAINT `FK_TIMECONTROL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TIMECONTROL_LOCATION` FOREIGN KEY (`location`) REFERENCES `location` (`id`),
  CONSTRAINT `FK_TIMECONTROL_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Control de Horario';

#
# Table structure for table `training_center`
#

CREATE TABLE `training_center` (
  `registry` int NOT NULL COMMENT 'Registro del Centro Formativo',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `code` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del centro formativo',
  PRIMARY KEY (`registry`),
  KEY `IDX_TRAINING_CENTER_DOMAIN` (`domain`),
  CONSTRAINT `FK_TRAINING_CENTER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TRAINING_CENTER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Centros Formativos acreditados';

#
# Table structure for table `training_course`
#

CREATE TABLE `training_course` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `training_center` int NOT NULL COMMENT 'Identificador del Centro Formativo',
  `code` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del Curso Formativo',
  `certification_name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Denominacion de la certificacion',
  `cno` int DEFAULT NULL COMMENT 'CNO',
  `modality` tinyint NOT NULL DEFAULT '0' COMMENT 'Modalidad del Curso Formativo',
  `fp_title_name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Titulo de formacion profesional',
  `occupation_name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre de la ocupacion',
  `professional_certificate` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Certificado de profesionalidad',
  `fp_title` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Titulo de formacion profesional',
  `center_available` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Centro disponible',
  PRIMARY KEY (`id`),
  KEY `IDX_TRAINING_COURSE_CNO` (`cno`),
  KEY `IDX_TRAINING_COURSE_DOMAIN` (`domain`),
  KEY `IDX_TRAINING_COURSE_TRAINING_CENTER` (`training_center`),
  CONSTRAINT `FK_TRAINING_COURSE_CNO` FOREIGN KEY (`cno`) REFERENCES `cno` (`id`),
  CONSTRAINT `FK_TRAINING_COURSE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TRAINING_COURSE_TRAINING_CENTER` FOREIGN KEY (`training_center`) REFERENCES `training_center` (`registry`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cursos de los Centros Formativos';

#
# Table structure for table `user`
#

CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint NOT NULL DEFAULT '0',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Usuario',
  `login` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Login del Usuario',
  `enterprise` int DEFAULT NULL COMMENT 'Identificador de la Empresa',
  `registry` int DEFAULT NULL COMMENT 'Identificador del Registry',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si el Usuario esta activo o no',
  `allowConcurrent` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Usuario admite Sesiones concurrentes',
  `password` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Contraseña del Usuario',
  `passwordExpiration` date DEFAULT NULL COMMENT 'Fecha de Expiracion de la Contrasea',
  `toolbar` tinyint NOT NULL DEFAULT '0' COMMENT 'Tipo de Barra de Herramientas del Usuario',
  `locale` varchar(8) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Locale del Usuario',
  `pageLimit` int DEFAULT NULL COMMENT 'Limite de filas en pantalla del Usuario',
  `linesPageLimit` int DEFAULT NULL COMMENT 'Limite de filas en pantalla en lineas del Usuario',
  `initAction` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre la acicn de inicio del Usuario',
  `lastAccess` datetime DEFAULT NULL COMMENT 'Fecha del ultimo acceso del Usuario',
  `auth` binary(16) DEFAULT NULL,
  `shared` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_USER_DOMAIN_LOGIN` (`domain`,`login`),
  KEY `IDX_USER_ENTERPRISE` (`enterprise`),
  KEY `IDX_USER_REGISTRY` (`registry`),
  KEY `IDX_USER_DOMAIN` (`domain`),
  CONSTRAINT `FK_USER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_USER_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`),
  CONSTRAINT `FK_USER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Usuarios';

#
# Table structure for table `user_app_role`
#

CREATE TABLE `user_app_role` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',
  `domain` int NOT NULL DEFAULT '0' COMMENT 'Dominio',
  `app` tinyint NOT NULL DEFAULT '0' COMMENT 'App',
  `user_id` int NOT NULL DEFAULT '0' COMMENT 'Identificador del Usuario',
  `role` tinyint NOT NULL DEFAULT '0' COMMENT 'role',
  PRIMARY KEY (`id`),
  KEY `IDX_USER_APP_ROLE_DOMAIN` (`domain`),
  KEY `IDX_USER_APP_ROLE_USER` (`user_id`),
  CONSTRAINT `FK_USER_APP_ROLE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_USER_APP_ROLE_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Rol del usuario en una aplicacion';

#
# Table structure for table `user_scope`
#

CREATE TABLE `user_scope` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `user_id` int NOT NULL COMMENT 'Identificador del Usuario',
  `scope` int NOT NULL COMMENT 'Identificador del Ambito',
  PRIMARY KEY (`id`),
  KEY `IDX_USER_SCOPE_SCOPE` (`scope`),
  KEY `IDX_USER_SCOPE_USER` (`user_id`),
  KEY `IDX_USER_SCOPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_USER_SCOPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_USER_SCOPE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_USER_SCOPE_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ambitos de Usuario';

#
# Table structure for table `user_workgroup`
#

CREATE TABLE `user_workgroup` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `user_id` int NOT NULL COMMENT 'Identificador del Usuario',
  `workgroup` int NOT NULL COMMENT 'Identificador del Grupo de Trabajo',
  PRIMARY KEY (`id`),
  KEY `IDX_USER_WORKGROUP_USER` (`user_id`),
  KEY `IDX_USER_WORKGROUP_WORKGROUP` (`workgroup`),
  KEY `IDX_USER_WORKGROUP_DOMAIN` (`domain`),
  CONSTRAINT `FK_USER_WORKGROUP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_USER_WORKGROUP_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_USER_WORKGROUP_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Usuarios y Grupos de Trabajo';

#
# Table structure for table `warehouse`
#

CREATE TABLE `warehouse` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Almacen',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Almacen',
  `workplace` int DEFAULT NULL COMMENT 'Identificador del Centro de Trabajo',
  `department` int DEFAULT NULL COMMENT 'Identificador del Departamento',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si el Almacen esta activo o no',
  PRIMARY KEY (`id`),
  KEY `IDX_WAREHOUSE_WORKPLACE` (`workplace`),
  KEY `IDX_WAREHOUSE_DOMAIN` (`domain`),
  KEY `IDX_WAREHOUSE_DEPARTMENT` (`department`),
  CONSTRAINT `FK_WAREHOUSE_DEPARTMENT` FOREIGN KEY (`department`) REFERENCES `department` (`id`),
  CONSTRAINT `FK_WAREHOUSE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_WAREHOUSE_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Almacenes';

#
# Table structure for table `warehouse_transfer`
#

CREATE TABLE `warehouse_transfer` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `series` char(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie del Traspaso',
  `number` int NOT NULL DEFAULT '0' COMMENT 'Numero del Traspaso',
  `issue_time` datetime NOT NULL COMMENT 'Fecha de emision del Traspaso',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios del Traspaso',
  `source_warehouse` int DEFAULT NULL COMMENT 'Identificador del Almacen Origen',
  `target_warehouse` int DEFAULT NULL COMMENT 'Identificador del Almacen Destino',
  `inventory` int DEFAULT NULL COMMENT 'Identificador del Inventario',
  `source` tinyint NOT NULL DEFAULT '0' COMMENT 'Origen',
  `source_id` int DEFAULT NULL COMMENT 'Identificador del origen',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_WAREHOUSE_TRANSFER_DOMAIN_SERIES_NUMBER` (`domain`,`series`,`number`),
  KEY `IDX_WAREHOUSE_TRANSFER_ISSUE_TIME` (`issue_time`),
  KEY `IDX_WAREHOUSE_TRANSFER_SOURCE_WAREHOUSE` (`source_warehouse`),
  KEY `IDX_WAREHOUSE_TRANSFER_TARGET_WAREHOUSE` (`target_warehouse`),
  KEY `IDX_WAREHOUSE_TRANSFER_DOMAIN` (`domain`),
  KEY `IDX_WAREHOUSE_TRANSFER_INVENTORY` (`inventory`),
  CONSTRAINT `FK_WAREHOUSE_TRANSFER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_WAREHOUSE_TRANSFER_INVENTORY` FOREIGN KEY (`inventory`) REFERENCES `inventory` (`id`),
  CONSTRAINT `FK_WAREHOUSE_TRANSFER_SOURCE_WAREHOUSE` FOREIGN KEY (`source_warehouse`) REFERENCES `warehouse` (`id`),
  CONSTRAINT `FK_WAREHOUSE_TRANSFER_TARGET_WAREHOUSE` FOREIGN KEY (`target_warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Traspasos entre Almacenes';

#
# Table structure for table `warehouse_transfer_detail`
#

CREATE TABLE `warehouse_transfer_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `warehouse_transfer` int NOT NULL COMMENT 'Identificador del Traspaso',
  `item` int NOT NULL COMMENT 'Identificador del Articulo del Detalle de Traspaso',
  `quantity` decimal(15,3) DEFAULT NULL COMMENT 'Cantidad del Detalle de Traspaso',
  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_WAREHOUSE_TRANSFER_DETAIL_ITEM` (`item`),
  KEY `IDX_WAREHOUSE_TRANSFER_DETAIL_WAREHOUSE_TRANSFER` (`warehouse_transfer`),
  KEY `IDX_WAREHOUSE_TRANSFER_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_WAREHOUSE_TRANSFER_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_WAREHOUSE_TRANSFER_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_WAREHOUSE_TRANSFER_DETAIL_WAREHOUSE_TRANSFER` FOREIGN KEY (`warehouse_transfer`) REFERENCES `warehouse_transfer` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Traspasos entre Almacenes';

#
# Table structure for table `web_info`
#

CREATE TABLE `web_info` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador Unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `company` int NOT NULL COMMENT 'Empresa',
  `commercial_description` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Descripcion comercial',
  `schedule` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Horario',
  `slogan` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Slogan',
  PRIMARY KEY (`id`),
  KEY `IDX_WEB_INFO_COMPANY` (`company`),
  KEY `IDX_WEB_INFO_DOMAIN` (`domain`),
  CONSTRAINT `FK_WEB_INFO_COMPANY` FOREIGN KEY (`company`) REFERENCES `company` (`registry`),
  CONSTRAINT `FK_WEB_INFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Informacion de la empresa que se mostrara en la ficha web';

#
# Table structure for table `web_info_page`
#

CREATE TABLE `web_info_page` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Codigo de la Pagina',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre de la Pagina.',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT 'Tipo de Pagina',
  `position` tinyint DEFAULT NULL COMMENT 'Posicion de la Pagina en el menu',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si la Pagina esta activa o no',
  PRIMARY KEY (`id`),
  KEY `IDX_WEB_INFO_PAGE_DOMAIN` (`domain`),
  CONSTRAINT `FK_WEB_INFO_PAGE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COMMENT='Paginas pertenecientes a la ficha web';

#
# Table structure for table `web_info_page_detail`
#

CREATE TABLE `web_info_page_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Codigo del Detalle de la Pagina',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `web_info_page` int NOT NULL COMMENT 'Identificador de la Pagina a la que corresponde el detalle',
  `title` varchar(255) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Titulo del contenido de la Pagina',
  `layout` int DEFAULT NULL COMMENT 'Tipo de plantilla',
  `content` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Texto del contenido de la Pagina',
  `extra` varchar(255) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Campo reservado a otros datos de la Pagina',
  PRIMARY KEY (`id`),
  KEY `IDX_WEB_INFO_PAGE_DETAIL_WEB_INFO_PAGE` (`web_info_page`),
  KEY `IDX_WEB_INFO_PAGE_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_WEB_INFO_PAGE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_WEB_INFO_PAGE_DETAIL_WEB_INFO_PAGE` FOREIGN KEY (`web_info_page`) REFERENCES `web_info_page` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COMMENT='Detalles de la pagina perteneciente a la ficha web';

#
# Table structure for table `web_info_page_resource`
#

CREATE TABLE `web_info_page_resource` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Codigo del Recurso de la Pagina',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `web_info_page` int NOT NULL COMMENT 'Codigo de la Pagina',
  `rattach` int NOT NULL COMMENT 'Identificador del Archivo Adjunto calificado como Recurso',
  `content` varchar(255) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Texto del Recurso',
  PRIMARY KEY (`id`),
  KEY `IDX_WEB_INFO_PAGE_RESOURCE_WEB_INFO_PAGE` (`web_info_page`),
  KEY `IDX_WEB_INFO_PAGE_RESOURCE_RATTACH` (`rattach`),
  KEY `IDX_WEB_INFO_PAGE_RESOURCE_DOMAIN` (`domain`),
  CONSTRAINT `FK_WEB_INFO_PAGE_RESOURCE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_WEB_INFO_PAGE_RESOURCE_RATTACH` FOREIGN KEY (`rattach`) REFERENCES `rattach` (`id`),
  CONSTRAINT `FK_WEB_INFO_PAGE_RESOURCE_WEB_INFO_PAGE` FOREIGN KEY (`web_info_page`) REFERENCES `web_info_page` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COMMENT='Recursos de la pagina perteneciente a la ficha web';

#
# Table structure for table `web_info_style`
#

CREATE TABLE `web_info_style` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Codigo del Estilo de la Pagina',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `variable` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la variable del Estilo',
  `value` varchar(255) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor de la variable del Estilo',
  PRIMARY KEY (`id`),
  KEY `IDX_WEB_INFO_STYLE_DOMAIN` (`domain`),
  CONSTRAINT `FK_WEB_INFO_STYLE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COMMENT='Estilos a utilizar en las plantillas para generar ficha web';

#
# Table structure for table `workgroup`
#

CREATE TABLE `workgroup` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Grupo de Trabajo',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Grupo de Trabajo',
  `status` tinyint DEFAULT NULL COMMENT 'Estado del grupo de Trabajo',
  PRIMARY KEY (`id`),
  KEY `IDX_WORKGROUP_DOMAIN` (`domain`),
  CONSTRAINT `FK_WORKGROUP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Grupos de Trabajo';

#
# Table structure for table `workplace`
#

CREATE TABLE `workplace` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Centro de Trabajo',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int NOT NULL DEFAULT '1' COMMENT 'Empresa asociada al Centro de Trabajo',
  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Centro de Trabajo',
  `address` int NOT NULL COMMENT 'Identificador de la Direccion',
  `customer` int DEFAULT NULL COMMENT 'Identificador del Cliente',
  `scope` int NOT NULL COMMENT 'Identificador del Ambito',
  `economicAgreement` tinyint DEFAULT '0' COMMENT 'Concierto Economico del Centro de Trabajo',
  `active` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Centro de Trabajo esta activo o no',
  PRIMARY KEY (`id`),
  KEY `IDX_WORKPLACE_ENTERPRISE` (`enterprise`),
  KEY `IDX_WORKPLACE_RADDRESS` (`address`),
  KEY `IDX_WORKPLACE_SCOPE` (`scope`),
  KEY `IDX_WORKPLACE_DOMAIN` (`domain`),
  KEY `IDX_WORKPLACE_CUSTOMER` (`customer`),
  CONSTRAINT `FK_WORKPLACE_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_WORKPLACE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_WORKPLACE_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`),
  CONSTRAINT `FK_WORKPLACE_RADDRESS` FOREIGN KEY (`address`) REFERENCES `raddress` (`id`),
  CONSTRAINT `FK_WORKPLACE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Centros de Trabajo';

#
# Table structure for table `workplace_department`
#

CREATE TABLE `workplace_department` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
  `workplace` int NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `department` int NOT NULL COMMENT 'Identificador del Departamento',
  `warehouse` int DEFAULT NULL COMMENT 'Identificador del Almacen',
  `catalogue` int DEFAULT NULL COMMENT 'Identificador del Catalogo',
  `active` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Departamento esta activo o no',
  PRIMARY KEY (`id`),
  KEY `IDX_WORKPLACE_DEPARTMENT_DOMAIN` (`domain`),
  KEY `IDX_WORKPLACE_DEPARTMENT_WORKPLACE` (`workplace`),
  KEY `IDX_WORKPLACE_DEPARTMENT_DEPARTMENT` (`department`),
  KEY `IDX_WORKPLACE_DEPARTMENT_CATALOGUE` (`catalogue`),
  KEY `IDX_WORKPLACE_DEPARTMENT_WAREHOUSE` (`warehouse`),
  CONSTRAINT `FK_WORKPLACE_DEPARTMENT_CATALOGUE` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`),
  CONSTRAINT `FK_WORKPLACE_DEPARTMENT_DEPARTMENT` FOREIGN KEY (`department`) REFERENCES `department` (`id`),
  CONSTRAINT `FK_WORKPLACE_DEPARTMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_WORKPLACE_DEPARTMENT_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`),
  CONSTRAINT `FK_WORKPLACE_DEPARTMENT_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Departamentos del Centro de Trabajo';

INSERT INTO `db_version` (`version_number`) VALUES ('9.23.4');

COMMIT;


SET FOREIGN_KEY_CHECKS=1;
