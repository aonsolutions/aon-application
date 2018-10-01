-- MySQL dump 10.16  Distrib 10.1.36-MariaDB, for Linux (x86_64)
--
-- Host: 172.17.0.1    Database: test-aonsolutions-org
-- ------------------------------------------------------
-- Server version	10.0.31-MariaDB-1~jessie

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `test-aonsolutions-org`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `test-aonsolutions-org` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `test-aonsolutions-org`;

--
-- Table structure for table `absence`
--

DROP TABLE IF EXISTS `absence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `absence` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `course_alumn` int(4) NOT NULL COMMENT 'Identificador del CursoAlumno',
  `absence_date` date DEFAULT NULL COMMENT 'Fecha de la Ausencia',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Ausencia',
  `evaluation` tinyint(2) DEFAULT NULL COMMENT 'Numero de Evaluacion en que se produjo la Ausencia',
  PRIMARY KEY (`id`),
  KEY `IDX_ABSENCE_COURSE_ALUMN` (`course_alumn`),
  KEY `IDX_ABSENCE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ABSENCE_COURSE_ALUMN` FOREIGN KEY (`course_alumn`) REFERENCES `course_alumn` (`id`),
  CONSTRAINT `FK_ABSENCE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ausencias';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `academic_skill`
--

DROP TABLE IF EXISTS `academic_skill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `academic_skill` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Aptitud Academica',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `code` char(5) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo de la Aptitud Academica',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de la Aptitud Academica',
  PRIMARY KEY (`id`),
  KEY `IDX_ACADEMIC_SKILL_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACADEMIC_SKILL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Aptitudes Academicas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `academic_year`
--

DROP TABLE IF EXISTS `academic_year`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `academic_year` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Año Academico',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `description` varchar(9) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Año Academico',
  PRIMARY KEY (`id`),
  KEY `IDX_ACADEMIC_YEAR_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACADEMIC_YEAR_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Año Academico';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `code` char(12) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo Cuenta Contable',
  `description` varchar(128) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Cuenta',
  `alias` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias de la Cuenta',
  `entryEnabled` tinyint(2) DEFAULT '0' COMMENT 'Indica si la Cuenta permite o no Apuntes',
  `level` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Nivel de la Cuenta',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si la Cuenta esta activo o no',
  `cost_center` char(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Centro de Costo',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_ACCOUNT_DOMAIN_CODE` (`domain`,`code`),
  KEY `IDX_ACCOUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22014770 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `account_entry`
--

DROP TABLE IF EXISTS `account_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_entry` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Asiento',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `account_period` int(4) NOT NULL COMMENT 'Ejercicio Contable del Asiento',
  `activity` int(4) DEFAULT NULL COMMENT 'Identificador de la Actividad',
  `entry_date` date DEFAULT NULL COMMENT 'Fecha del Asiento',
  `entry_type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Asiento',
  `journal` int(4) DEFAULT NULL COMMENT 'Numero de diario del Asiento',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad del Asiento',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios del Asiento',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_ACCOUNT_ENTRY_ACCOUNT_PERIOD` (`account_period`),
  KEY `IDX_ACCOUNT_ENTRY_DOMAIN` (`domain`),
  KEY `IDX_ACCOUNT_ENTRY_ACTIVITY` (`activity`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_ACCOUNT_PERIOD` FOREIGN KEY (`account_period`) REFERENCES `account_period` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_ACTIVITY` FOREIGN KEY (`activity`) REFERENCES `enterprise_activity` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Asientos Contables';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `account_entry_bank_statement`
--

DROP TABLE IF EXISTS `account_entry_bank_statement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_entry_bank_statement` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `account_entry` int(4) NOT NULL COMMENT 'Identificador de Asiento',
  `bank_statement` int(4) NOT NULL COMMENT 'Identificador de Extracto bancario',
  PRIMARY KEY (`id`),
  KEY `IDX_ACC_ENTRY_BANK_STATEMENT_ACC_ENTRY` (`account_entry`),
  KEY `IDX_ACC_ENTRY_BANK_STATEMENT_BANK_STATEMENT` (`bank_statement`),
  KEY `IDX_ACCOUNT_ENTRY_BANK_STATEMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_BANK_STATEMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACC_ENTRY_BANK_STATEMENT_ACC_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_ACC_ENTRY_BANK_STATEMENT_BANK_STATEMENT` FOREIGN KEY (`bank_statement`) REFERENCES `bank_statement` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Asientos Contables y Extractos bancarios';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `account_entry_detail`
--

DROP TABLE IF EXISTS `account_entry_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_entry_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Apunte',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `account_entry` int(4) NOT NULL COMMENT 'Identificador del Asiento',
  `line` int(4) unsigned NOT NULL COMMENT 'Numero de linea del Apunte dentro del Asiento',
  `account` int(4) NOT NULL COMMENT 'Cuenta Contable del Apunte',
  `concept` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Concepto del Apunte',
  `balancing_account` int(4) DEFAULT NULL COMMENT 'Contrapartida del Apunte',
  `debit` double DEFAULT '0' COMMENT 'Debe del Apunte',
  `credit` double DEFAULT '0' COMMENT 'Haber del Apunte',
  `document_number` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de documento asociado',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Apuntes Contables';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `account_entry_fbatch`
--

DROP TABLE IF EXISTS `account_entry_fbatch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_entry_fbatch` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `account_entry` int(4) NOT NULL COMMENT 'Identificador de Asiento Contable',
  `fbatch` int(4) NOT NULL COMMENT 'Identificador de Remesa',
  PRIMARY KEY (`id`),
  KEY `IDX_ACCOUNT_ENTRY_FBATCH_ACCOUNT_ENTRY` (`account_entry`),
  KEY `IDX_ACCOUNT_ENTRY_FBATCH_FBATCH` (`fbatch`),
  KEY `IDX_ACCOUNT_ENTRY_FBATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_FBATCH_ACCOUNT_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_FBATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_FBATCH_FBATCH` FOREIGN KEY (`fbatch`) REFERENCES `fbatch` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Asientos Contables y Remesas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `account_entry_finance_tracking`
--

DROP TABLE IF EXISTS `account_entry_finance_tracking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_entry_finance_tracking` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `account_entry` int(4) NOT NULL COMMENT 'Identificador de Asiento Contable',
  `finance_tracking` int(4) NOT NULL COMMENT 'Identificador de Seguimiento de Vencimientos',
  PRIMARY KEY (`id`),
  KEY `IDX_ACCOUNT_ENTRY_FINANCE_TRACKING_ACCOUNT_ENTRY` (`account_entry`),
  KEY `IDX_ACCOUNT_ENTRY_FINANCE_TRACKING_FINANCE_TRACKING` (`finance_tracking`),
  KEY `IDX_ACCOUNT_ENTRY_FINANCE_TRACKING_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_FINANCE_TRACKING_ACCOUNT_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_FINANCE_TRACKING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_FINANCE_TRACKING_FINANCE_TRACKING` FOREIGN KEY (`finance_tracking`) REFERENCES `finance_tracking` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Seguimiento de Vencimientos y Asientos Contab';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `account_entry_invoice`
--

DROP TABLE IF EXISTS `account_entry_invoice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_entry_invoice` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de Relacion',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `account_entry` int(4) NOT NULL COMMENT 'Identificador de Asiento',
  `invoice` int(4) NOT NULL COMMENT 'Identificador de Factura',
  PRIMARY KEY (`id`),
  KEY `IDX_ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY` (`account_entry`),
  KEY `IDX_ACCOUNT_ENTRY_INVOICE_INVOICE` (`invoice`),
  KEY `IDX_ACCOUNT_ENTRY_INVOICE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_INVOICE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_INVOICE_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Asientos Contables y Facturas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `account_helper`
--

DROP TABLE IF EXISTS `account_helper`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_helper` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `counter` int(4) NOT NULL DEFAULT '0' COMMENT 'Contador, veces que se ha usado',
  `account` int(4) NOT NULL COMMENT 'Cuenta Contable',
  `balancing_account` int(4) NOT NULL COMMENT 'Contrapartida',
  PRIMARY KEY (`id`),
  KEY `IDX_ACCOUNT_HELPER_ACCOUNT` (`account`),
  KEY `IDX_ACCOUNT_HELPER_BAL_ACCOUNT` (`balancing_account`),
  KEY `IDX_ACCOUNT_HELPER_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_HELPER_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_ACCOUNT_HELPER_BAL_ACCOUNT` FOREIGN KEY (`balancing_account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_ACCOUNT_HELPER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ayuda a la introduccion de apuntes';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `account_period`
--

DROP TABLE IF EXISTS `account_period`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_period` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` char(16) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del periodo',
  `initiation_date` date NOT NULL COMMENT 'Fecha de inicio del Ejercicio',
  `deadline` date NOT NULL COMMENT 'Fecha final del Ejercicio',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado del Ejercicio',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_ACCOUNT_PERIOD_DOMAIN_NAME` (`domain`,`name`),
  KEY `IDX_ACCOUNT_PERIOD_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_PERIOD_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ejercicios Contables';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `action`
--

DROP TABLE IF EXISTS `action`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `action` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `menu` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si la Accion esta o no dentro del menu',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Nombre de la Accion',
  `application` int(4) NOT NULL COMMENT 'Aplicacion a la que pertenece la Accion',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_ACTION_NAME_APPLICATION` (`name`,`application`),
  KEY `IDX_ACTION_NAME` (`name`),
  KEY `IDX_ACTION_APPLICATION` (`application`),
  CONSTRAINT `FK_ACTION_APPLICATION` FOREIGN KEY (`application`) REFERENCES `application` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7459 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Acciones de una Applicacion';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `action_denied`
--

DROP TABLE IF EXISTS `action_denied`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `action_denied` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `action_id` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Accion',
  `user_id` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Usuario',
  PRIMARY KEY (`id`),
  KEY `IDX_ACTION_DENIED_ACTION` (`action_id`),
  KEY `IDX_ACTION_DENIED_USER` (`user_id`),
  KEY `IDX_ACTION_DENIED_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACTION_DENIED_ACTION` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `FK_ACTION_DENIED_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACTION_DENIED_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Accion no permitida para el Usuario';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `action_entry`
--

DROP TABLE IF EXISTS `action_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `action_entry` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `executionDate` datetime NOT NULL COMMENT 'Fecha de ejecucion',
  `action_id` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Accion',
  `session_id` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Sesion',
  PRIMARY KEY (`id`),
  KEY `IDX_ACTION_ENTRY_ACTION` (`action_id`),
  KEY `IDX_ACTION_ENTRY_SESSION` (`session_id`),
  KEY `IDX_ACTION_ENTRY_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACTION_ENTRY_ACTION` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `FK_ACTION_ENTRY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACTION_ENTRY_SESSION` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Entrada de la ejecucion de una Accion';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `action_favorite`
--

DROP TABLE IF EXISTS `action_favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `action_favorite` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `position` int(4) NOT NULL COMMENT 'Posicion dentro de las Acciones Favoritas',
  `action_id` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Accion',
  `user_id` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Usuario',
  PRIMARY KEY (`id`),
  KEY `IDX_ACTION_FAVORITE_ACTION` (`action_id`),
  KEY `IDX_ACTION_FAVORITE_USER` (`user_id`),
  KEY `IDX_ACTION_FAVORITE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACTION_FAVORITE_ACTION` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `FK_ACTION_FAVORITE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACTION_FAVORITE_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Accion Favorita del Usuario';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `activity_type`
--

DROP TABLE IF EXISTS `activity_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activity_type` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Tipo de Actividad',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Actividad',
  `project_type` int(4) DEFAULT NULL COMMENT 'Tipo de Proyecto',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Activo si o no',
  PRIMARY KEY (`id`),
  KEY `IDX_ACTIVITY_TYPE_PROJECT_TYPE` (`project_type`),
  KEY `IDX_ACTIVITY_TYPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACTIVITY_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACTIVITY_TYPE_PROJECT_TYPE` FOREIGN KEY (`project_type`) REFERENCES `project_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Actividades';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `agreement`
--

DROP TABLE IF EXISTS `agreement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `agreement` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `calendar` int(4) DEFAULT NULL COMMENT 'Calendario',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  PRIMARY KEY (`id`),
  KEY `IDX_AGREEMENT_CALENDAR` (`calendar`),
  KEY `IDX_AGREEMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_AGREEMENT_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_AGREEMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1204 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Convenios';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `agreement_data`
--

DROP TABLE IF EXISTS `agreement_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `agreement_data` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `agreement` int(4) NOT NULL COMMENT 'Convenio',
  `expression` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  PRIMARY KEY (`id`),
  KEY `IDX_AGREEMENT_DATA_AGREEMENT` (`agreement`),
  KEY `IDX_AGREEMENT_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_AGREEMENT_DATA_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`),
  CONSTRAINT `FK_AGREEMENT_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2166 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto del convenio';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `agreement_extra`
--

DROP TABLE IF EXISTS `agreement_extra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `agreement_extra` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `agreement` int(4) NOT NULL COMMENT 'Convenio',
  `agreement_payment` int(4) DEFAULT NULL COMMENT 'Concepto',
  `start_date` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Fecha de inicio dd mm [year offset]',
  `end_date` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Fecha de finalizacion dd mm [year offset]',
  `issue_date` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Fecha de emision dd mm',
  PRIMARY KEY (`id`),
  KEY `IDX_AGREEMENT_EXTRA_AGREEMENT` (`agreement`),
  KEY `IDX_AGREEMENT_EXTRA_AGREEMENT_PAYMENT` (`agreement_payment`),
  KEY `IDX_AGREEMENT_EXTRA_DOMAIN` (`domain`),
  CONSTRAINT `FK_AGREEMENT_EXTRA_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`),
  CONSTRAINT `FK_AGREEMENT_EXTRA_AGREEMENT_PAYMENT` FOREIGN KEY (`agreement_payment`) REFERENCES `agreement_payment` (`id`),
  CONSTRAINT `FK_AGREEMENT_EXTRA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1125 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Pagas extras';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `agreement_level`
--

DROP TABLE IF EXISTS `agreement_level`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `agreement_level` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `agreement` int(4) NOT NULL COMMENT 'Convenio',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  PRIMARY KEY (`id`),
  KEY `IDX_AGREEMENT_LEVEL_AGREEMENT` (`agreement`),
  KEY `IDX_AGREEMENT_LEVEL_DOMAIN` (`domain`),
  CONSTRAINT `FK_AGREEMENT_LEVEL_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`),
  CONSTRAINT `FK_AGREEMENT_LEVEL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6117 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Niveles retributivos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `agreement_level_category`
--

DROP TABLE IF EXISTS `agreement_level_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `agreement_level_category` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `agreement_level` int(4) NOT NULL COMMENT 'Nivel retributivo',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  PRIMARY KEY (`id`),
  KEY `IDX_AGREEMENT_LEVEL_CATEGORY_AGREEMENT_LEVEL` (`agreement_level`),
  KEY `IDX_AGREEMENT_LEVEL_CATEGORY_DOMAIN` (`domain`),
  CONSTRAINT `FK_AGREEMENT_LEVEL_CATEGORY_AGREEMENT_LEVEL` FOREIGN KEY (`agreement_level`) REFERENCES `agreement_level` (`id`),
  CONSTRAINT `FK_AGREEMENT_LEVEL_CATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8610 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias profesionales';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `agreement_level_data`
--

DROP TABLE IF EXISTS `agreement_level_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `agreement_level_data` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `agreement_level` int(4) NOT NULL COMMENT 'Nivel retributivo',
  `expression` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  PRIMARY KEY (`id`),
  KEY `IDX_AGREEMENT_LEVEL_DATA_AGREEMENT_LEVEL` (`agreement_level`),
  KEY `IDX_AGREEMENT_LEVEL_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_AGREEMENT_LEVEL_DATA_AGREEMENT_LEVEL` FOREIGN KEY (`agreement_level`) REFERENCES `agreement_level` (`id`),
  CONSTRAINT `FK_AGREEMENT_LEVEL_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16074 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto del convenio';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `agreement_payment`
--

DROP TABLE IF EXISTS `agreement_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `agreement_payment` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `agreement` int(4) NOT NULL COMMENT 'Convenio',
  `payment_concept` int(4) DEFAULT NULL COMMENT 'Identificador unico del concepto',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de complemento Salarial',
  `expression` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe',
  `description` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `month` tinyint(2) DEFAULT NULL COMMENT 'Mes de la percepcion',
  `salary_type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Nomina/Recibo',
  `description_decorable` tinyint(2) NOT NULL DEFAULT '0',
  `irpf_expression` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe tributable',
  `quote_expression` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe cotizable',
  PRIMARY KEY (`id`),
  KEY `IDX_AGREEMENT_PAYMENT_PAYMENT_CONCEPT` (`payment_concept`),
  KEY `IDX_AGREEMENT_PAYMENT_AGREEMENT` (`agreement`),
  KEY `IDX_AGREEMENT_PAYMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_AGREEMENT_PAYMENT_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`),
  CONSTRAINT `FK_AGREEMENT_PAYMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_AGREEMENT_PAYMENT_PAYMENT_CONCEPT` FOREIGN KEY (`payment_concept`) REFERENCES `payment_concept` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=168 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Percepciones';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alarm`
--

DROP TABLE IF EXISTS `alarm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alarm` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Alarma',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` text COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Alarma',
  `alarm_date` datetime NOT NULL COMMENT 'Fecha y hora de ejecucion de la Alarma',
  `status` tinyint(2) DEFAULT NULL COMMENT 'Estado de la Alarma',
  `source` tinyint(2) NOT NULL COMMENT 'Origen de la Alarma',
  `source_id` int(4) DEFAULT NULL COMMENT 'Identificador del origen de la Alarma',
  `user_id` int(4) DEFAULT NULL COMMENT 'Identificador del Usuario asociado a la Alarma',
  `priority` tinyint(2) NOT NULL COMMENT 'Prioridad de la Alarma',
  PRIMARY KEY (`id`),
  KEY `IDX_ALARM_USER` (`user_id`),
  KEY `IDX_ALARM_DOMAIN` (`domain`),
  CONSTRAINT `FK_ALARM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ALARM_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Alarmas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `allotment`
--

DROP TABLE IF EXISTS `allotment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `allotment` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `hotel` int(4) NOT NULL COMMENT 'Identificador del Hotel',
  `rate_code` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de Cupo',
  `agency` int(4) DEFAULT NULL COMMENT 'Identificador de la agencia de viajes',
  `agency_group` int(4) DEFAULT NULL COMMENT 'Identificador del Grupo de agencias',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio del Cupo',
  `end_date` date NOT NULL COMMENT 'Fecha de fin del Cupo',
  `quantity` int(4) DEFAULT '0' COMMENT 'Cupo de Habitaciones',
  `remarks` text COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si el Cupo esta activo o no',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `allotment_item`
--

DROP TABLE IF EXISTS `allotment_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `allotment_item` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `allotment` int(4) NOT NULL COMMENT 'Identificador del Cupo de seguridad',
  `item` int(4) NOT NULL COMMENT 'Identificador del Tipo de Habitacion',
  PRIMARY KEY (`id`),
  KEY `IDX_ALLOTMENT_ITEM_DOMAIN` (`domain`),
  KEY `IDX_ALLOTMENT_ITEM_ALLOTMENT` (`allotment`),
  KEY `IDX_ALLOTMENT_ITEM_ITEM` (`item`),
  CONSTRAINT `FK_ALLOTMENT_ITEM_ALLOTMENT` FOREIGN KEY (`allotment`) REFERENCES `allotment` (`id`),
  CONSTRAINT `FK_ALLOTMENT_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ALLOTMENT_ITEM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Habitacion por Cupo';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `allotment_tariff`
--

DROP TABLE IF EXISTS `allotment_tariff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `allotment_tariff` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `allotment` int(4) NOT NULL COMMENT 'Identificador del Cupo de seguridad',
  `tariff` int(4) NOT NULL COMMENT 'Identificador de la Tarifa',
  PRIMARY KEY (`id`),
  KEY `IDX_ALLOTMENT_TARIFF_DOMAIN` (`domain`),
  KEY `IDX_ALLOTMENT_TARIFF_ALLOTMENT` (`allotment`),
  KEY `IDX_ALLOTMENT_TARIFF_TARIFF` (`tariff`),
  CONSTRAINT `FK_ALLOTMENT_TARIFF_ALLOTMENT` FOREIGN KEY (`allotment`) REFERENCES `allotment` (`id`),
  CONSTRAINT `FK_ALLOTMENT_TARIFF_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ALLOTMENT_TARIFF_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas por Cupo';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alumn_loan`
--

DROP TABLE IF EXISTS `alumn_loan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alumn_loan` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Prestamo',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `customer` int(4) NOT NULL COMMENT 'Alumno al que se le realizo el Prestamo',
  `material` varchar(16) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Material prestado',
  `loan_date` date NOT NULL COMMENT 'Fecha del Prestamo',
  `end_date` date DEFAULT NULL COMMENT 'Fecha devolucion del material',
  `comments` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Observaciones',
  PRIMARY KEY (`id`),
  KEY `IDX_ALUMN_LOAN_CUSTOMER` (`customer`),
  KEY `IDX_ALUMN_LOAN_DOMAIN` (`domain`),
  CONSTRAINT `FK_ALUMN_LOAN_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_ALUMN_LOAN_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Prestamos a Alumnos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `amortization`
--

DROP TABLE IF EXISTS `amortization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `amortization` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del inmovilizado',
  `initial_date` date NOT NULL COMMENT 'Fecha de inicio de la Amortizacion',
  `deadline` date DEFAULT NULL COMMENT 'Fecha de baja de la Amortizacion',
  `amount` double NOT NULL DEFAULT '0' COMMENT 'Importe a amortizar.',
  `fee_period` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Periodo de las cuotas de Amortizacion',
  `sale_amount` double DEFAULT NULL COMMENT 'Importe de la venta',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  `invest_asset` int(4) DEFAULT NULL COMMENT 'Identificador del Bien afecto',
  `fixed_asset_account` int(4) NOT NULL COMMENT 'Cuenta de inmovilizado',
  `accumulated_account` int(4) NOT NULL COMMENT 'Cuenta de Amortizacion acumulada',
  `allocation_account` int(4) NOT NULL COMMENT 'Cuenta para la dotacion de la Amortizacion',
  `percentage` double DEFAULT '0' COMMENT 'Porcentaje de Amortizacion',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Fichas de Amortizacion Contables';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `amortization_detail`
--

DROP TABLE IF EXISTS `amortization_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `amortization_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `amortization` int(4) NOT NULL COMMENT 'Ficha de Amortizacion',
  `from_date` date NOT NULL COMMENT 'Desde fecha',
  `to_date` date NOT NULL COMMENT 'Hasta fecha',
  `coefficient` double(15,3) NOT NULL COMMENT 'Coeficiente de Amortizacion',
  `allocation` double(15,3) NOT NULL COMMENT 'Dotacion de la Amortizacion',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Estatus del Detalle de Amortizacion',
  `account_entry` int(4) DEFAULT NULL COMMENT 'Posicion del Apunte Contable',
  `fiscal_allocation` double(15,3) DEFAULT '0.000' COMMENT 'Dotacion fiscal',
  PRIMARY KEY (`id`),
  KEY `IDX_AMORTIZATION_DETAIL_AMORTIZATION` (`amortization`),
  KEY `IDX_AMORTIZATION_DETAIL_ACCOUNT_ENTRY` (`account_entry`),
  KEY `IDX_AMORTIZATION_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_AMORTIZATION_DETAIL_ACCOUNT_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_AMORTIZATION_DETAIL_AMORTIZATION` FOREIGN KEY (`amortization`) REFERENCES `amortization` (`id`),
  CONSTRAINT `FK_AMORTIZATION_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de la Ficha de Amortizacion Contable';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `amortization_invoice`
--

DROP TABLE IF EXISTS `amortization_invoice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `amortization_invoice` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `amortization` int(4) NOT NULL COMMENT 'Identificador de la Ficha de Amortizacion',
  `invoice` int(4) NOT NULL COMMENT 'Identificador de la Factura',
  `sales` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si es venta de inmovilizado o no',
  PRIMARY KEY (`id`),
  KEY `IDX_AMORTIZATION_INVOICE_DOMAIN` (`domain`),
  KEY `IDX_AMORTIZATION_INVOICE_AMORTIZATION` (`amortization`),
  KEY `IDX_AMORTIZATION_INVOICE_INVOICE` (`invoice`),
  CONSTRAINT `FK_AMORTIZATION_INVOICE_AMORTIZATION` FOREIGN KEY (`amortization`) REFERENCES `amortization` (`id`),
  CONSTRAINT `FK_AMORTIZATION_INVOICE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_AMORTIZATION_INVOICE_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Vinculo entr la Ficha de Amortizacion y la factura.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `amortization_type`
--

DROP TABLE IF EXISTS `amortization_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `amortization_type` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fixed_asset_account` varchar(4) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Cuenta de inmovilizado',
  `accumulated_account` varchar(4) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Cuenta de amortizacion acumulada',
  `allocation_account` varchar(4) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Cuenta para la dotacion de la amortizacion',
  `percentage` double DEFAULT '0' COMMENT 'Porcentaje de amortizacion',
  `description` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Amortizacion',
  PRIMARY KEY (`id`),
  KEY `IDX_AMORTIZATION_TYPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_AMORTIZATION_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Amortizacion';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `app_param`
--

DROP TABLE IF EXISTS `app_param`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `app_param` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Parametro',
  `value` varchar(96) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor del Parametro',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_APP_PARAM_DOMAIN_NAME` (`domain`,`name`),
  KEY `IDX_APP_PARAM_DOMAIN` (`domain`),
  CONSTRAINT `FK_APP_PARAM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1760579 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Parametros de la Aplicacion';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `application`
--

DROP TABLE IF EXISTS `application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Aplicacion',
  `description` varchar(128) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Aplicacion',
  `contratable` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si la Aplicacion es contratable o no',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Aplicaciones';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `application_role`
--

DROP TABLE IF EXISTS `application_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application_role` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `application` int(4) NOT NULL COMMENT 'Identificador de la Aplicacion',
  `role` int(4) NOT NULL COMMENT 'Identificador del Role',
  PRIMARY KEY (`id`),
  KEY `IDX_APPLICATION_ROLE_APPLICATION` (`application`),
  KEY `IDX_APPLICATION_ROLE_ROLE` (`role`),
  CONSTRAINT `FK_APPLICATION_ROLE_APPLICATION` FOREIGN KEY (`application`) REFERENCES `application` (`id`),
  CONSTRAINT `FK_APPLICATION_ROLE_ROLE` FOREIGN KEY (`role`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=239 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Roles de la Aplicacion';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `application_user`
--

DROP TABLE IF EXISTS `application_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application_user` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `user_id` int(4) NOT NULL COMMENT 'Identificador del Usuario',
  `domain_application` int(4) NOT NULL COMMENT 'Identificador de la Aplicacion del Dominio',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si la Aplicacion del Dominio esta activo o no',
  PRIMARY KEY (`id`),
  KEY `IDX_APPLICATION_USER_USER` (`user_id`),
  KEY `IDX_APPLICATION_USER_DOMAIN_APPLICATION` (`domain_application`),
  KEY `IDX_APPLICATION_USER_DOMAIN` (`domain`),
  CONSTRAINT `FK_APPLICATION_USER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_APPLICATION_USER_DOMAIN_APPLICATION` FOREIGN KEY (`domain_application`) REFERENCES `domain_application` (`id`),
  CONSTRAINT `FK_APPLICATION_USER_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8476 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Usuarios de las Aplicaciones del Dominio';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `application_user_profile`
--

DROP TABLE IF EXISTS `application_user_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application_user_profile` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `application_user` int(4) NOT NULL COMMENT 'Identificador del Usuario de la Aplicacion',
  `profile` int(4) NOT NULL COMMENT 'Identificador del Perfil',
  PRIMARY KEY (`id`),
  KEY `IDX_APPLICATION_USER_PROFILE_PROFILE` (`profile`),
  KEY `IDX_APPLICATION_USER_PROFILE_APPLICATION_USER` (`application_user`),
  KEY `IDX_APPLICATION_USER_PROFILE_DOMAIN` (`domain`),
  CONSTRAINT `FK_APPLICATION_USER_PROFILE_APPLICATION_USER` FOREIGN KEY (`application_user`) REFERENCES `application_user` (`id`),
  CONSTRAINT `FK_APPLICATION_USER_PROFILE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_APPLICATION_USER_PROFILE_PROFILE` FOREIGN KEY (`profile`) REFERENCES `profile` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7891 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Perfiles del Usuario para la Aplicacion';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asset`
--

DROP TABLE IF EXISTS `asset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `asset` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Activo',
  `name` varchar(10) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre corto del Activo',
  PRIMARY KEY (`id`),
  KEY `IDX_ASSET_DOMAIN` (`domain`),
  CONSTRAINT `FK_ASSET_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Activos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asset_activity`
--

DROP TABLE IF EXISTS `asset_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `asset_activity` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `asset` int(4) NOT NULL COMMENT 'Identificador del Activo',
  `date` date NOT NULL COMMENT 'Fecha de la Actividad',
  `from_time` datetime NOT NULL COMMENT 'Hora de inicio de la Actividad',
  `to_time` datetime NOT NULL COMMENT 'Hora final de la Actividad',
  `who` varchar(20) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Quien solicita el Activo',
  `why` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Motivo de solicitud del Activo',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Estado de la Solicitud',
  PRIMARY KEY (`id`),
  KEY `IDX_ASSET_ACTIVITY_ASSET` (`asset`),
  KEY `IDX_ASSET_ACTIVITY_DOMAIN` (`domain`),
  KEY `IDX_ASSET_ACTIVITY_DATE` (`date`),
  CONSTRAINT `FK_ASSET_ACTIVITY_ASSET` FOREIGN KEY (`asset`) REFERENCES `asset` (`id`),
  CONSTRAINT `FK_ASSET_ACTIVITY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades sobre el Activo';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asset_feature`
--

DROP TABLE IF EXISTS `asset_feature`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `asset_feature` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `asset` int(4) NOT NULL COMMENT 'Identificador del Activo',
  `feature` int(4) NOT NULL COMMENT 'Identificador de la Caracteristica',
  PRIMARY KEY (`id`),
  KEY `IDX_ASSET_FEATURE_ASSET` (`asset`),
  KEY `IDX_ASSET_FEATURE_FEATURE` (`feature`),
  KEY `IDX_ASSET_FEATURE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ASSET_FEATURE_ASSET` FOREIGN KEY (`asset`) REFERENCES `asset` (`id`),
  CONSTRAINT `FK_ASSET_FEATURE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ASSET_FEATURE_FEATURE` FOREIGN KEY (`feature`) REFERENCES `feature` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Caracteristicas por Activo';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `auto_concept`
--

DROP TABLE IF EXISTS `auto_concept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `auto_concept` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Concepto Automatico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` char(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Concepto Automatico',
  PRIMARY KEY (`id`),
  KEY `IDX_AUTO_CONCEPT_DOMAIN` (`domain`),
  CONSTRAINT `FK_AUTO_CONCEPT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos Automaticos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `balance`
--

DROP TABLE IF EXISTS `balance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `balance` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Nombre del Balance',
  `removable` tinyint(1) DEFAULT '0' COMMENT 'Indica se puede ser borrado por el usuario',
  `type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de Balance',
  PRIMARY KEY (`id`),
  KEY `IDX_BALANCE_DOMAIN` (`domain`),
  CONSTRAINT `FK_BALANCE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12623 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Balances';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `balance_detail`
--

DROP TABLE IF EXISTS `balance_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `balance_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `balance` int(4) NOT NULL COMMENT 'Identificador del Balance',
  `code` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del Detalle en el Balance',
  `description` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripción del detalle de balance',
  `accounts` text COLLATE latin1_spanish_ci COMMENT 'Cuentas separadas por comas, que forman el acumulado.',
  `sortKey` int(4) DEFAULT '0' COMMENT 'Orden el que aparecera en el listado.',
  `notes` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Notas en el Balance',
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
) ENGINE=InnoDB AUTO_INCREMENT=907097 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Balace';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bank_concept`
--

DROP TABLE IF EXISTS `bank_concept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bank_concept` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Concepto',
  `account` int(4) DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY (`id`),
  KEY `IDX_BANK_CONCEPT_DOMAIN` (`domain`),
  KEY `IDX_BANK_CONCEPT_ACCOUNT` (`account`),
  CONSTRAINT `FK_BANK_CONCEPT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_BANK_CONCEPT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos bancarios';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bank_statement`
--

DROP TABLE IF EXISTS `bank_statement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bank_statement` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `rbank` int(4) NOT NULL COMMENT 'Identificador de Banco de la Compañia',
  `lot_number` int(4) NOT NULL DEFAULT '0' COMMENT 'Numero de lote',
  `operation_date` date NOT NULL COMMENT 'Fecha de operacion',
  `common_concept` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Concepto comun',
  `own_concept` varchar(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Concepto propio',
  `payment` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si es un pago',
  `amount` double(15,2) NOT NULL DEFAULT '0.00' COMMENT 'Importe',
  `document` varchar(10) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de documento',
  `reference1` varchar(12) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Referencia 1',
  `reference2` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Referencia 2',
  `description` varchar(80) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `reliability` tinyint(2) DEFAULT '0' COMMENT 'Fiabilidad del punteo',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  PRIMARY KEY (`id`),
  KEY `IDX_BANK_STATEMENT_RBANK` (`rbank`),
  KEY `IDX_BANK_STATEMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_BANK_STATEMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_BANK_STATEMENT_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Extractos bancarios';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bank_statement_link`
--

DROP TABLE IF EXISTS `bank_statement_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bank_statement_link` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `bank_statement` int(4) NOT NULL COMMENT 'Identificador de Extracto bancario',
  `source` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Origen',
  `source_id` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del origen',
  `source_date` date DEFAULT NULL COMMENT 'Fecha del origen',
  `amount` double(15,2) NOT NULL DEFAULT '0.00' COMMENT 'Importe',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado',
  `linked_bank_statement_link` int(4) DEFAULT NULL COMMENT 'Identificador del Enlace de Extracto bancario asociado',
  PRIMARY KEY (`id`),
  KEY `IDX_BANK_STATEMENT_LINK_BANK_STATEMENT` (`bank_statement`),
  KEY `IDX_BANK_STATEMENT_LINK_BANK_STATEMENT_LINK` (`linked_bank_statement_link`),
  KEY `IDX_BANK_STATEMENT_LINK_DOMAIN` (`domain`),
  CONSTRAINT `FK_BANK_STATEMENT_LINK_BANK_STATEMENT` FOREIGN KEY (`bank_statement`) REFERENCES `bank_statement` (`id`),
  CONSTRAINT `FK_BANK_STATEMENT_LINK_BANK_STATEMENT_LINK` FOREIGN KEY (`linked_bank_statement_link`) REFERENCES `bank_statement_link` (`id`),
  CONSTRAINT `FK_BANK_STATEMENT_LINK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Enlaces del Extracto bancario';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bonus_concept`
--

DROP TABLE IF EXISTS `bonus_concept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bonus_concept` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `expression` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe',
  `description` varchar(256) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Bonificacion Salarial',
  PRIMARY KEY (`id`),
  KEY `IDX_BONUS_CONCEPT_DOMAIN` (`domain`),
  CONSTRAINT `FK_BONUS_CONCEPT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3270 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos de bonficaciones y/o reducciones';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `booking`
--

DROP TABLE IF EXISTS `booking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `booking` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation_room` int(4) NOT NULL COMMENT 'Identificador de la Habitacion de la Reserva',
  `hotel` int(4) NOT NULL COMMENT 'Identificador del Hotel',
  `agency` int(4) DEFAULT NULL COMMENT 'Identificador de la Agencia',
  `item` int(4) NOT NULL COMMENT 'Identificador del Producto',
  `tariff` int(4) NOT NULL COMMENT 'Identificador de la Tarifa',
  `stay_date` date NOT NULL COMMENT 'Fecha de estancia',
  `stay_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Indica si es una entrada, una salida o una permanencia',
  `guests` int(4) NOT NULL DEFAULT '0' COMMENT 'Numero de Huespedes',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `brand`
--

DROP TABLE IF EXISTS `brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `brand` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Marca Comercial',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Marca Comercial',
  PRIMARY KEY (`id`),
  KEY `IDX_BRAND_DOMAIN` (`domain`),
  CONSTRAINT `FK_BRAND_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Marcas Comerciales';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `calendar`
--

DROP TABLE IF EXISTS `calendar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `calendar` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `holiday` int(4) DEFAULT NULL COMMENT 'Identificador de Festivos',
  `anual_hours` double DEFAULT '0' COMMENT 'Horas anuales del Calendario',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Calendario',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  `monday` tinyint(2) DEFAULT '0' COMMENT 'Tipo de dia',
  `monday_hours` double DEFAULT '0' COMMENT 'Numero de horas laborables',
  `tuesday` tinyint(2) DEFAULT '0' COMMENT 'Tipo de dia',
  `tuesday_hours` double DEFAULT '0' COMMENT 'Numero de horas laborables',
  `wednesday` tinyint(2) DEFAULT '0' COMMENT 'Tipo de dia',
  `wednesday_hours` double DEFAULT '0' COMMENT 'Numero de horas laborables',
  `thursday` tinyint(2) DEFAULT '0' COMMENT 'Tipo de dia',
  `thursday_hours` double DEFAULT '0' COMMENT 'Numero de horas laborables',
  `friday` tinyint(2) DEFAULT '0' COMMENT 'Tipo de dia',
  `friday_hours` double DEFAULT '0' COMMENT 'Numero de horas laborables',
  `saturday` tinyint(2) DEFAULT '1' COMMENT 'Tipo de dia',
  `saturday_hours` double DEFAULT '0' COMMENT 'Numero de horas laborables',
  `sunday` tinyint(2) DEFAULT '1' COMMENT 'Tipo de dia',
  `sunday_hours` double DEFAULT '0' COMMENT 'Numero de horas laborables',
  `generic` tinyint(1) DEFAULT '1' COMMENT 'Indica si es editable o no',
  `calendar` int(4) DEFAULT NULL COMMENT 'Calendario del que se hereda',
  PRIMARY KEY (`id`),
  KEY `IDX_CALENDAR_HOLIDAY` (`holiday`),
  KEY `IDX_CALENDAR_CALENDAR` (`calendar`),
  KEY `IDX_CALENDAR_DOMAIN` (`domain`),
  CONSTRAINT `FK_CALENDAR_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_CALENDAR_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CALENDAR_HOLIDAY` FOREIGN KEY (`holiday`) REFERENCES `holiday` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Calendarios Laborales';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `calendar_holiday`
--

DROP TABLE IF EXISTS `calendar_holiday`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `calendar_holiday` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `calendar` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Calendario',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Festivo',
  `date` date DEFAULT NULL COMMENT 'Fecha del festivo',
  `day_type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de dia',
  `hours` double DEFAULT '0' COMMENT 'Numero de horas laborables',
  PRIMARY KEY (`id`),
  KEY `IDX_CALENDAR_HOLIDAY_CALENDAR` (`calendar`),
  KEY `IDX_CALENDAR_HOLIDAY_DOMAIN` (`domain`),
  CONSTRAINT `FK_CALENDAR_HOLIDAY_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_CALENDAR_HOLIDAY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Festivos de Calendarios';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `calendar_period`
--

DROP TABLE IF EXISTS `calendar_period`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `calendar_period` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `calendar` int(4) NOT NULL COMMENT 'Identificador del Calendario',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Periodo',
  `month` tinyint(2) DEFAULT '0' COMMENT 'Mes del periodo',
  `start_day` tinyint(2) DEFAULT '0' COMMENT 'Dia inicio del periodo',
  `end_day` tinyint(2) DEFAULT '0' COMMENT 'Dia fin del periodo',
  `monday` tinyint(2) DEFAULT '0' COMMENT 'Tipo de dia',
  `monday_hours` double DEFAULT '0' COMMENT 'Numero de horas laborables',
  `tuesday` tinyint(2) DEFAULT '0' COMMENT 'Tipo de dia',
  `tuesday_hours` double DEFAULT '0' COMMENT 'Numero de horas laborables',
  `wednesday` tinyint(2) DEFAULT '0' COMMENT 'Tipo de dia',
  `wednesday_hours` double DEFAULT '0' COMMENT 'Numero de horas laborables',
  `thursday` tinyint(2) DEFAULT '0' COMMENT 'Tipo de dia',
  `thursday_hours` double DEFAULT '0' COMMENT 'Numero de horas laborables',
  `friday` tinyint(2) DEFAULT '0' COMMENT 'Tipo de dia',
  `friday_hours` double DEFAULT '0' COMMENT 'Numero de horas laborables',
  `saturday` tinyint(2) DEFAULT '0' COMMENT 'Tipo de dia',
  `saturday_hours` double DEFAULT '0' COMMENT 'Numero de horas laborables',
  `sunday` tinyint(2) DEFAULT '0' COMMENT 'Tipo de dia',
  `sunday_hours` double DEFAULT '0' COMMENT 'Numero de horas laborables',
  PRIMARY KEY (`id`),
  KEY `IDX_CALENDAR_PERIOD_CALENDAR` (`calendar`),
  KEY `IDX_CALENDAR_PERIOD_DOMAIN` (`domain`),
  CONSTRAINT `FK_CALENDAR_PERIOD_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_CALENDAR_PERIOD_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Periodos de Calendarios';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `campaign`
--

DROP TABLE IF EXISTS `campaign`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `campaign` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Campaña',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `campaign_type` int(4) DEFAULT NULL COMMENT 'Identificador del Tipo de Campaña',
  `description` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Campaña',
  `process` int(4) NOT NULL COMMENT 'Identificador del Proceso',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio de la Campaña',
  `end_date` date NOT NULL COMMENT 'Fecha de finalizacion de la Campaña',
  `workgroup` int(4) NOT NULL COMMENT 'Grupo de Trabajo supervisor de la Campaña',
  `manual` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Tipo de Campaña',
  `status` tinyint(2) DEFAULT NULL COMMENT 'Estado de la Campaña',
  PRIMARY KEY (`id`),
  KEY `IDX_CAMPAIGN_CAMPAIGN_TYPE` (`campaign_type`),
  KEY `IDX_CAMPAIGN_PROCESS` (`process`),
  KEY `IDX_CAMPAIGN_WORKGROUP` (`workgroup`),
  KEY `IDX_CAMPAIGN_DOMAIN` (`domain`),
  CONSTRAINT `FK_CAMPAIGN_CAMPAIGN_TYPE` FOREIGN KEY (`campaign_type`) REFERENCES `campaign_type` (`id`),
  CONSTRAINT `FK_CAMPAIGN_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CAMPAIGN_PROCESS` FOREIGN KEY (`process`) REFERENCES `process` (`id`),
  CONSTRAINT `FK_CAMPAIGN_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Campañas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `campaign_project`
--

DROP TABLE IF EXISTS `campaign_project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `campaign_project` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Relacion de Campañas y Expedientes',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `campaign` int(4) NOT NULL COMMENT 'Identificador de la Campaña',
  `project` int(4) NOT NULL COMMENT 'Identificador del Expediente',
  PRIMARY KEY (`id`),
  KEY `IDX_CAMPAIGN_PROJECT_CAMPAIGN` (`campaign`),
  KEY `IDX_CAMPAIGN_PROJECT_PROJECT` (`project`),
  KEY `IDX_CAMPAIGN_PROJECT_DOMAIN` (`domain`),
  CONSTRAINT `FK_CAMPAIGN_PROJECT_CAMPAIGN` FOREIGN KEY (`campaign`) REFERENCES `campaign` (`id`),
  CONSTRAINT `FK_CAMPAIGN_PROJECT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CAMPAIGN_PROJECT_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Campañas y Expedientes';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `campaign_type`
--

DROP TABLE IF EXISTS `campaign_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `campaign_type` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion',
  `active` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Activo si o no',
  PRIMARY KEY (`id`),
  KEY `IDX_CAMPAIGN_TYPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_CAMPAIGN_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Campañas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `carrier`
--

DROP TABLE IF EXISTS `carrier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `carrier` (
  `registry` int(4) NOT NULL COMMENT 'Registro de la Agencia de Transporte',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `status` tinyint(2) DEFAULT NULL COMMENT 'Estado de la Agencia de Transporte',
  PRIMARY KEY (`registry`),
  KEY `IDX_CARRIER_DOMAIN` (`domain`),
  KEY `IDX_CARRIER_SCOPE` (`scope`),
  CONSTRAINT `FK_CARRIER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CARRIER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_CARRIER_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Agencias de Transporte';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `carrier_packing`
--

DROP TABLE IF EXISTS `carrier_packing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `carrier_packing` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la hoja de ruta',
  `domain` int(4) NOT NULL COMMENT 'Identificador del dominio',
  `series` char(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie de la hoja de ruta',
  `number` int(4) NOT NULL DEFAULT '0' COMMENT 'Numero de la hoja de ruta',
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Indica el tipo de la hoja de ruta',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Indica el estado de la hoja de ruta',
  `issue_date` datetime DEFAULT NULL COMMENT 'Fecha de emision',
  `carrier` int(4) NOT NULL COMMENT 'Identificador de la agencia de transporte',
  `delivery_date` datetime DEFAULT NULL COMMENT 'Fecha de entrega',
  `carrier_reference` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Referencia de la agencia de transporte',
  `number_plate` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de matricula del vehiculo',
  `driver_name` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del conductor',
  `driver_document` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de documento del conductor',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Observaciones carrier packing',
  `gross` double DEFAULT NULL COMMENT 'Bruto',
  `tare` double DEFAULT NULL COMMENT 'Tara',
  `net` double DEFAULT NULL COMMENT 'Neto',
  `reception_start_date` datetime DEFAULT NULL COMMENT 'Fecha entrada transporte, recepcion',
  `reception_end_date` datetime DEFAULT NULL COMMENT 'Fecha salida transporte, recepcion',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_CARRIER_PACKING_DOMAIN` (`domain`),
  KEY `IDX_CARRIER_PACKING_CARRIER` (`carrier`),
  CONSTRAINT `FK_CARRIER_PACKING_CARRIER` FOREIGN KEY (`carrier`) REFERENCES `carrier` (`registry`),
  CONSTRAINT `FK_CARRIER_PACKING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Hojas de ruta';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cashflow_forecast`
--

DROP TABLE IF EXISTS `cashflow_forecast`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cashflow_forecast` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `payment` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si es un pago o un cobro',
  `description` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio de aplicacion',
  `due_date` date DEFAULT NULL COMMENT 'Fecha final de aplicacion',
  `rbank` int(4) DEFAULT NULL COMMENT 'Identificador de Banco de la Compañia',
  `amount` double(15,2) NOT NULL DEFAULT '0.00' COMMENT 'Importe',
  `payment_day` double(15,2) NOT NULL DEFAULT '1.00' COMMENT 'Dia de pago',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Prevision de tesoreria';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `catalogue`
--

DROP TABLE IF EXISTS `catalogue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `catalogue` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Catalogo',
  `purchase` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si se trata de un Catalogo de Compras o Ventas',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio del Catalogo',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de fin del Catalogo',
  PRIMARY KEY (`id`),
  KEY `IDX_CATALOGUE_DOMAIN` (`domain`),
  CONSTRAINT `FK_CATALOGUE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Catalogos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `catalogue_category`
--

DROP TABLE IF EXISTS `catalogue_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `catalogue_category` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `catalogue` int(4) NOT NULL COMMENT 'Identificador del Catalogo',
  `category` int(4) NOT NULL COMMENT 'Identificador de la Categoria',
  `quantity` double DEFAULT '0' COMMENT 'Cantidad a partir de la cual se aplica el descuento',
  `discount` double(6,2) DEFAULT '0.00' COMMENT 'Descuento de la Categoria en el Catalogo',
  PRIMARY KEY (`id`),
  KEY `IDX_CATALOGUE_CATEGORY_CATALOGUE` (`catalogue`),
  KEY `IDX_CATALOGUE_CATEGORY_PCATEGORY` (`category`),
  KEY `IDX_CATALOGUE_CATEGORY_DOMAIN` (`domain`),
  CONSTRAINT `FK_CATALOGUE_CATEGORY_CATALOGUE` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`),
  CONSTRAINT `FK_CATALOGUE_CATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CATALOGUE_CATEGORY_PCATEGORY` FOREIGN KEY (`category`) REFERENCES `pcategory` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias del Catalogo';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `catalogue_item`
--

DROP TABLE IF EXISTS `catalogue_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `catalogue_item` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `catalogue` int(4) NOT NULL COMMENT 'Identificador del Catalogo',
  `product` int(4) NOT NULL COMMENT 'Identificador del Producto',
  `item` int(4) DEFAULT NULL COMMENT 'Identificador del Articulo',
  `quantity` double DEFAULT '0' COMMENT 'Cantidad a partir de la cual se aplica el precio o descuento',
  `price` double DEFAULT '0' COMMENT 'Precio del Articulo en el Catalogo',
  `discount` double(6,2) DEFAULT '0.00' COMMENT 'Descuento del Articulo en el Catalogo',
  PRIMARY KEY (`id`),
  KEY `IDX_CATALOGUE_ITEM_CATALOGUE` (`catalogue`),
  KEY `IDX_CATALOGUE_ITEM_ITEM` (`item`),
  KEY `IDX_CATALOGUE_ITEM_DOMAIN` (`domain`),
  KEY `IDX_CATALOGUE_ITEM_PRODUCT` (`product`),
  CONSTRAINT `FK_CATALOGUE_ITEM_CATALOGUE` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`),
  CONSTRAINT `FK_CATALOGUE_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CATALOGUE_ITEM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_CATALOGUE_ITEM_PRODUCT` FOREIGN KEY (`product`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulos del Catalogo';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Categoria',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Categoria',
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de la Categoria',
  `scope` int(4) DEFAULT NULL COMMENT 'Identificador del Ambito',
  `description` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de la Categoria',
  `url` varchar(256) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Url de la Categoria',
  `rattach` int(4) DEFAULT NULL COMMENT 'Identificador del Archivo Adjunto',
  PRIMARY KEY (`id`),
  KEY `IDX_CATEGORY_DOMAIN` (`domain`),
  KEY `IDX_CATEGORY_RATTACH` (`rattach`),
  KEY `IDX_CATEGORY_SCOPE` (`scope`),
  CONSTRAINT `FK_CATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CATEGORY_RATTACH` FOREIGN KEY (`rattach`) REFERENCES `rattach` (`id`),
  CONSTRAINT `FK_CATEGORY_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `certifica2_batch`
--

DROP TABLE IF EXISTS `certifica2_batch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `certifica2_batch` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del certificado de empresa de la remesa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador unico del certificado de empresa de la empresa',
  `date` datetime DEFAULT NULL COMMENT 'Fecha de la remesa',
  `status` int(4) DEFAULT NULL COMMENT 'Estado de la remesa',
  `sign` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Huella digital obtenida de la respuesta',
  `communication_id` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Identificador resultante de la comunicacion',
  `income_file` mediumblob COMMENT 'Archivo respuesta en binario',
  `income_file_date` datetime DEFAULT NULL COMMENT 'Fecha de respuesta',
  `outcome_file` mediumblob COMMENT 'Archivo Adjunto en binario',
  `outcome_file_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  PRIMARY KEY (`id`),
  KEY `IDX_CERTIFICA2_BATCH_ENTERPRISE` (`enterprise`),
  KEY `IDX_CERTIFICA2_BATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas de certificados de empresa';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `certifica2_batch_detail`
--

DROP TABLE IF EXISTS `certifica2_batch_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `certifica2_batch_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del certificado de empresa de la remesa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `certifica2_batch` int(4) NOT NULL COMMENT 'Identificador unico del certificado de empresa',
  `contract` int(4) NOT NULL COMMENT 'Identificador unico del contrato de empleado',
  `suspension_cause_code` varchar(2) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo causa suspension',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Estado de la linea de la remesa',
  PRIMARY KEY (`id`),
  KEY `IDX_CERTIFICA2_BATCH_DETAIL_CERTIFICA2_BATCH` (`certifica2_batch`),
  KEY `IDX_CERTIFICA2_BATCH_DETAIL_CONTRACT` (`contract`),
  KEY `IDX_CERTIFICA2_BATCH_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_DETAIL_CERTIFICA2_BATCH` FOREIGN KEY (`certifica2_batch`) REFERENCES `certifica2_batch` (`id`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_DETAIL_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las remesas de certificados de empresa';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cnae`
--

DROP TABLE IF EXISTS `cnae`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cnae` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `code` varchar(5) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo del CNAE',
  `title` varchar(255) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Titulo del CNAE',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9901 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='CNAE';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cnae2009`
--

DROP TABLE IF EXISTS `cnae2009`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cnae2009` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `code` varchar(4) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo del CNAE',
  `title` varchar(255) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Titulo del CNAE',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000991 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='CNAE 2009. Clasificación Nacional de Actividades Económicas ';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cnae2009_rate`
--

DROP TABLE IF EXISTS `cnae2009_rate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cnae2009_rate` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador único',
  `cnae2009` int(4) NOT NULL COMMENT 'CNAE',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `it_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe por Incapacidad Temporal (I.T.)',
  `ims_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe por Incapacidad Permanente, Muerte y Supervivencia (I.M.S.)',
  PRIMARY KEY (`id`),
  KEY `IDX_CNAE2009_RATE_CNAE2009` (`cnae2009`),
  CONSTRAINT `FK_CNAE2009_RATE_CNAE2009` FOREIGN KEY (`cnae2009`) REFERENCES `cnae2009` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=456 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas de primas para I.T e I.M.S';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cno`
--

DROP TABLE IF EXISTS `cno`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cno` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `code` varchar(5) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo del CNO',
  `title` varchar(255) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Titulo del CNO',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9821 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='CNO';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `commercial_activity`
--

DROP TABLE IF EXISTS `commercial_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commercial_activity` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Actividad Comercial',
  `probability` int(4) DEFAULT NULL COMMENT 'Probabilidad de la Actividad',
  `survey` int(4) DEFAULT NULL COMMENT 'Identificador del Cuestionario',
  PRIMARY KEY (`id`),
  KEY `IDX_COMMERCIAL_ACTIVITY_DOMAIN` (`domain`),
  KEY `IDX_COMMERCIAL_ACTIVITY_SURVEY` (`survey`),
  CONSTRAINT `FK_COMMERCIAL_ACTIVITY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_COMMERCIAL_ACTIVITY_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades Comerciales';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `commercial_term`
--

DROP TABLE IF EXISTS `commercial_term`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commercial_term` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `line` smallint(2) DEFAULT '1' COMMENT 'Numero de linea de Condicion',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Nombre de la Condicion Comercial',
  `description` text COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Condicion Comercial',
  `term_general` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Condición es particular o general',
  PRIMARY KEY (`id`),
  KEY `IDX_COMMERCIAL_TERM_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMMERCIAL_TERM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Condiciones Comerciales';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `commercial_tracking`
--

DROP TABLE IF EXISTS `commercial_tracking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commercial_tracking` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `date` datetime NOT NULL COMMENT 'Fecha del Seguimiento Comercial',
  `seller` int(4) NOT NULL COMMENT 'Identificador del Comercial',
  `project_commercial` int(4) NOT NULL COMMENT 'Identificador del Proyecto',
  `activity` int(4) NOT NULL COMMENT 'Identificador de la Actividad Comercial',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios del Seguimiento Comercial',
  `status` tinyint(2) NOT NULL COMMENT 'Estado del Seguimiento Comercial',
  `next_commercial_tracking` int(4) DEFAULT NULL COMMENT 'Identificador del siguiente Seguimiento Comercial',
  `end_date` datetime DEFAULT NULL COMMENT 'Fecha de cierre del Seguimiento Comercial',
  `offer` int(4) DEFAULT NULL COMMENT 'Identificador del Presupuesto',
  `allDay` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Seguimiento Comercial dura todo el dia',
  `location` varchar(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Ubicacion del Seguimiento Comercial',
  `eventId` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Seguimientos Comerciales';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `commission`
--

DROP TABLE IF EXISTS `commission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commission` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Comision',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio de la Comision',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de fin de la Comision',
  PRIMARY KEY (`id`),
  KEY `IDX_COMMISSION_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMMISSION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `commission_category`
--

DROP TABLE IF EXISTS `commission_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commission_category` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `commission` int(4) NOT NULL COMMENT 'Identificador de la Comision',
  `category` int(4) NOT NULL COMMENT 'Identificador de la Categoria',
  `quantity` double DEFAULT '0' COMMENT 'Cantidad a partir de la cual se aplica la Comision',
  `rate` double(6,2) DEFAULT '0.00' COMMENT 'Porcentaje de Comision',
  PRIMARY KEY (`id`),
  KEY `IDX_COMMISSION_CATEGORY_COMMISSION` (`commission`),
  KEY `IDX_COMMISSION_CATEGORY_CATEGORY` (`category`),
  KEY `IDX_COMMISSION_CATEGORY_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMMISSION_CATEGORY_CATEGORY` FOREIGN KEY (`category`) REFERENCES `pcategory` (`id`),
  CONSTRAINT `FK_COMMISSION_CATEGORY_COMMISSION` FOREIGN KEY (`commission`) REFERENCES `commission` (`id`),
  CONSTRAINT `FK_COMMISSION_CATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones por Categoria';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `commission_item`
--

DROP TABLE IF EXISTS `commission_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commission_item` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `commission` int(4) NOT NULL COMMENT 'Identificador de la Comision',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo',
  `quantity` double DEFAULT '0' COMMENT 'Cantidad a partir de la cual se aplica la Comision',
  `amount` double DEFAULT '0' COMMENT 'Importe de la Comision',
  `rate` double(6,2) DEFAULT '0.00' COMMENT 'Porcentaje de Comision',
  PRIMARY KEY (`id`),
  KEY `IDX_COMMISSION_ITEM_COMMISSION` (`commission`),
  KEY `IDX_COMMISSION_ITEM_ITEM` (`item`),
  KEY `IDX_COMMISSION_ITEM_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMMISSION_ITEM_COMMISSION` FOREIGN KEY (`commission`) REFERENCES `commission` (`id`),
  CONSTRAINT `FK_COMMISSION_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_COMMISSION_ITEM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones por Articulo';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `commission_type`
--

DROP TABLE IF EXISTS `commission_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commission_type` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Comision',
  `rate` double(6,2) DEFAULT '0.00' COMMENT 'Porcentaje de Comision',
  PRIMARY KEY (`id`),
  KEY `IDX_COMMISSION_TYPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMMISSION_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Comisiones';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `commission_type_commission`
--

DROP TABLE IF EXISTS `commission_type_commission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commission_type_commission` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `commission_type` int(4) NOT NULL COMMENT 'Identificador del Tipo de Comision',
  `commission` int(4) NOT NULL COMMENT 'Identificador de la Comision',
  PRIMARY KEY (`id`),
  KEY `IDX_COMMISSION_TYPE_COMMISSION_COMMISSION_TYPE` (`commission_type`),
  KEY `IDX_COMMISSION_TYPE_COMMISSION_COMMISSION` (`commission`),
  KEY `IDX_COMMISSION_TYPE_COMMISSION_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMMISSION_TYPE_COMMISSION_COMMISSION` FOREIGN KEY (`commission`) REFERENCES `commission` (`id`),
  CONSTRAINT `FK_COMMISSION_TYPE_COMMISSION_COMMISSION_TYPE` FOREIGN KEY (`commission_type`) REFERENCES `commission_type` (`id`),
  CONSTRAINT `FK_COMMISSION_TYPE_COMMISSION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones por Tipo de Comision';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `company`
--

DROP TABLE IF EXISTS `company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company` (
  `registry` int(4) NOT NULL DEFAULT '1' COMMENT 'Registro de la Compañia',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contact`
--

DROP TABLE IF EXISTS `contact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contact` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `user_id` int(4) DEFAULT NULL COMMENT 'Identificador del Usuario',
  `displayName` varchar(128) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Mostrar Como',
  `contact_data` int(4) DEFAULT NULL COMMENT 'Identificador de la Informacin del Contacto',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTACT_CONTACT_DATA` (`contact_data`),
  KEY `IDX_CONTACT_USER` (`user_id`),
  KEY `IDX_CONTACT_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTACT_CONTACT_DATA` FOREIGN KEY (`contact_data`) REFERENCES `contact_data` (`id`),
  CONSTRAINT `FK_CONTACT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CONTACT_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Grupo de Contactos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contact_data`
--

DROP TABLE IF EXISTS `contact_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contact_data` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `surname` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Apellido',
  `address` varchar(256) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion',
  `postalCode` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo postal',
  `city` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Localidad',
  `contactState` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Estado',
  `country` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Pais',
  `phone` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono',
  `cellularPhone` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Movil',
  `fax` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Fax',
  `email` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Email',
  `note` text COLLATE latin1_spanish_ci COMMENT 'Nota',
  `organization` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Organizacin',
  `title` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Cargo',
  `organizationAddress` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccin de la Organizacin',
  `organizationPostalCode` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo postal de la Organizacin',
  `organizationCity` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Localidad de la Organizacin',
  `organizationState` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Estado de la Organizacin',
  `organizationPhone` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono de la Organizacin',
  `organizationFax` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Fax de la Organizacin',
  `web` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Web',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTACT_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTACT_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contactos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contact_detail`
--

DROP TABLE IF EXISTS `contact_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contact_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `contact_group` int(4) NOT NULL COMMENT 'Identificador del Grupo de Contactos',
  `contact` int(4) NOT NULL COMMENT 'Identificador del Contacto',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTACT_DETAIL_CONTACT` (`contact`),
  KEY `IDX_CONTACT_DETAIL_CONTACT_GROUP` (`contact_group`),
  KEY `IDX_CONTACT_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTACT_DETAIL_CONTACT` FOREIGN KEY (`contact`) REFERENCES `contact` (`id`),
  CONSTRAINT `FK_CONTACT_DETAIL_CONTACT_GROUP` FOREIGN KEY (`contact_group`) REFERENCES `contact` (`id`),
  CONSTRAINT `FK_CONTACT_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Grupo de Contactos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract`
--

DROP TABLE IF EXISTS `contract`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `person` int(4) NOT NULL COMMENT 'Identificador de la Persona',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `enterprise_ccc` int(4) DEFAULT NULL COMMENT 'CCC',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio del Contrato',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion del Contrato',
  `calendar` int(4) DEFAULT NULL COMMENT 'Calendario',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `sepe_status` tinyint(2) DEFAULT '0' COMMENT 'Estado de notificacion del contrato al SEPE',
  `registration` int(4) DEFAULT NULL COMMENT 'Número libro de matricula',
  `seniority_date` date DEFAULT NULL COMMENT 'Fecha de antiguedad',
  `enterprise_activity` int(4) DEFAULT NULL COMMENT 'Actividad',
  `ss_regime` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Regimen de la Seguridad Social',
  `model` tinyint(2) DEFAULT NULL COMMENT 'Indica el modelo de documento del contrato',
  `category_description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Categoria o grupo profesional',
  `ss_status` tinyint(2) DEFAULT '0' COMMENT 'Estado de notificacion del contrato a la Seguridad Social',
  `agreement_level` int(4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_PERSON` (`person`),
  KEY `IDX_CONTRACT_WORKPLACE` (`workplace`),
  KEY `IDX_CONTRACT_CALENDAR` (`calendar`),
  KEY `IDX_CONTRACT_ENTERPRISE_CCC` (`enterprise_ccc`),
  KEY `IDX_CONTRACT_ENTERPRISE_ACTIVITY` (`enterprise_activity`),
  KEY `IDX_CONTRACT_DOMAIN` (`domain`),
  KEY `FK_CONTRACT_AGREEMEN_LEVEL` (`agreement_level`),
  CONSTRAINT `FK_CONTRACT_AGREEMEN_LEVEL` FOREIGN KEY (`agreement_level`) REFERENCES `agreement_level` (`id`),
  CONSTRAINT `FK_CONTRACT_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_CONTRACT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CONTRACT_ENTERPRISE_ACTIVITY` FOREIGN KEY (`enterprise_activity`) REFERENCES `enterprise_activity` (`id`),
  CONSTRAINT `FK_CONTRACT_ENTERPRISE_CCC` FOREIGN KEY (`enterprise_ccc`) REFERENCES `enterprise_ccc` (`id`),
  CONSTRAINT `FK_CONTRACT_PERSON` FOREIGN KEY (`person`) REFERENCES `person` (`registry`),
  CONSTRAINT `FK_CONTRACT_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5955 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contratos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_attach`
--

DROP TABLE IF EXISTS `contract_attach`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_attach` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Archivo Adjunto del contrato',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Registro del contrato',
  `mimeType` tinyint(2) DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Archivo Adjunto',
  `scope` int(4) DEFAULT NULL COMMENT 'Ambito del Archivo Adjunto',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad del Archivo Adjunto',
  `attach_date` datetime DEFAULT NULL COMMENT 'Fecha del Archivo Adjunto',
  `driveId` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_ATTACH_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_ATTACH_SCOPE` (`scope`),
  KEY `IDX_CONTRACT_ATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_ATTACH_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CONTRACT_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de contratos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_batch`
--

DROP TABLE IF EXISTS `contract_batch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_batch` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la remesa de contratos',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `outcome_file_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `communication_id` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Identificador resultante de la comunicacion',
  `income_file_date` datetime DEFAULT NULL COMMENT 'Fecha de respuesta',
  `red_response_id` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Identificador de la respuesta',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Indica el estado de la remesa',
  `outcome_file` mediumblob COMMENT 'Archivo Adjunto en binario',
  `income_file` mediumblob COMMENT 'Archivo respuesta en binario',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_BATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas de contratos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_batch_detail`
--

DROP TABLE IF EXISTS `contract_batch_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_batch_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del detalle de la remesa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `contract_batch` int(4) NOT NULL COMMENT 'Identificador unico de la remesa de contratos',
  `contract` int(4) NOT NULL COMMENT 'Identificador unico del contrato',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Estado de la linea de la remesa',
  `action_type` varchar(3) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Tipo de accion a realizar',
  `leave_type` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo de baja',
  `real_date` datetime DEFAULT NULL COMMENT 'Fecha real de la accion',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_BATCH_DETAIL_CONTRACT_BATCH` (`contract_batch`),
  KEY `IDX_CONTRACT_BATCH_DETAIL_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_BATCH_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_BATCH_DETAIL_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_BATCH_DETAIL_CONTRACT_BATCH` FOREIGN KEY (`contract_batch`) REFERENCES `contract_batch` (`id`),
  CONSTRAINT `FK_CONTRACT_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las remesas de contratos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_bonus`
--

DROP TABLE IF EXISTS `contract_bonus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_bonus` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `expression` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Fórmula',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `bonus_concept` int(4) DEFAULT NULL COMMENT 'Concepto de bonificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_BONUS_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_BONUS_BONUS_CONCEPT` (`bonus_concept`),
  KEY `IDX_CONTRACT_BONUS_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_BONUS_BONUS_CONCEPT` FOREIGN KEY (`bonus_concept`) REFERENCES `bonus_concept` (`id`),
  CONSTRAINT `FK_CONTRACT_BONUS_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_BONUS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1121 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bonificaciones';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_calendar_event`
--

DROP TABLE IF EXISTS `contract_calendar_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_calendar_event` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int(4) NOT NULL COMMENT 'Identificador del Contrato',
  `date` date NOT NULL COMMENT 'Fecha de la incidencia',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de incidencia',
  `duration` double DEFAULT NULL COMMENT 'Duracion de la incidencia',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_CALENDAR_EVENT_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_CALENDAR_EVENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_CALENDAR_EVENT_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_CALENDAR_EVENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Incidencias de calendario en Contratos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_clause`
--

DROP TABLE IF EXISTS `contract_clause`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_clause` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int(4) DEFAULT NULL COMMENT 'Identificador del Contrato',
  `line` smallint(2) DEFAULT '1' COMMENT 'Numero de linea de la Clausula del Contrato',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Titulo de la Clausula de Contrato',
  `description` text COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Clausula de Contrato',
  `general` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Clausula es particular o general',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_CLAUSE_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_CLAUSE_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_CLAUSE_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_CLAUSE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Clausulas de contrato';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_data`
--

DROP TABLE IF EXISTS `contract_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_data` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `expression` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_DATA_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_DATA_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=56018 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto del contrato';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_deduction`
--

DROP TABLE IF EXISTS `contract_deduction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_deduction` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Deducción',
  `deduction_concept` int(4) DEFAULT NULL COMMENT 'Identificador unico del concepto',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `description_decorable` tinyint(2) NOT NULL DEFAULT '0',
  `expression` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Fórmula',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `month` tinyint(2) DEFAULT NULL COMMENT 'Mes de la percepcion',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_DEDUCTION_DEDUCTION_CONCEPT` (`deduction_concept`),
  KEY `IDX_CONTRACT_DEDUCTION_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_DEDUCTION_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_DEDUCTION_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_DEDUCTION_DEDUCTION_CONCEPT` FOREIGN KEY (`deduction_concept`) REFERENCES `deduction_concept` (`id`),
  CONSTRAINT `FK_CONTRACT_DEDUCTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1865 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Deducciones';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_embargo`
--

DROP TABLE IF EXISTS `contract_embargo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_embargo` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe',
  `expression` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Formula',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_EMBARGO_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_EMBARGO_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_EMBARGO_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_EMBARGO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Embargos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_info`
--

DROP TABLE IF EXISTS `contract_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_info` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int(4) DEFAULT NULL COMMENT 'Identificador del Contrato',
  `name` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `expression` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_INFO_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_INFO_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_INFO_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_INFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=48109 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Informacion temporal del Contrato';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_leave`
--

DROP TABLE IF EXISTS `contract_leave`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_leave` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Baja',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `daily_cgc_base` double(15,3) DEFAULT NULL COMMENT 'Base de cotizacion por contingencias comunes',
  `daily_cgp_base` double(15,3) DEFAULT NULL COMMENT 'Base de cotizacion por contingencias profesionales',
  `parent` int(4) DEFAULT NULL COMMENT 'Baja origen, si es recaida',
  `daily_reg_base` double(15,3) DEFAULT NULL COMMENT 'Base reguladora',
  `discharge_cause` tinyint(2) DEFAULT NULL COMMENT 'Causa del alta',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_LEAVE_CONTRACT_LEAVE` (`parent`),
  KEY `IDX_CONTRACT_LEAVE_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_LEAVE_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_LEAVE_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_LEAVE_CONTRACT_LEAVE` FOREIGN KEY (`parent`) REFERENCES `contract_leave` (`id`),
  CONSTRAINT `FK_CONTRACT_LEAVE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=911 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bajas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_leave_detail`
--

DROP TABLE IF EXISTS `contract_leave_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_leave_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de parte',
  `contract_leave` int(4) NOT NULL COMMENT 'Contrato',
  `college_number` varchar(8) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de colegiado',
  `confirm_order` tinyint(2) DEFAULT NULL COMMENT 'Numero de orden del parte de confirmacion',
  `cias` varchar(11) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'codigo identificacion area sanitaria',
  `date` date NOT NULL COMMENT 'Fecha del parte',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Indica el estado',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE` (`contract_leave`),
  KEY `IDX_CONTRACT_LEAVE_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE` FOREIGN KEY (`contract_leave`) REFERENCES `contract_leave` (`id`),
  CONSTRAINT `FK_CONTRACT_LEAVE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las bajas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_payment`
--

DROP TABLE IF EXISTS `contract_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_payment` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Percepción Salarial',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `payment_concept` int(4) DEFAULT NULL COMMENT 'Identificador unico del concepto',
  `description` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `description_decorable` tinyint(2) NOT NULL DEFAULT '0',
  `expression` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe',
  `irpf_expression` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe tributable',
  `quote_expression` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe cotizable',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `month` tinyint(2) DEFAULT NULL COMMENT 'Mes de la percepcion',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `salary_type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Nomina/Recibo',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRACT_PAYMENT_PAYMENT_CONCEPT` (`payment_concept`),
  KEY `IDX_CONTRACT_PAYMENT_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_PAYMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_PAYMENT_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_PAYMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CONTRACT_PAYMENT_PAYMENT_CONCEPT` FOREIGN KEY (`payment_concept`) REFERENCES `payment_concept` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29002 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Percepciones Salariales';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contrata_batch`
--

DROP TABLE IF EXISTS `contrata_batch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contrata_batch` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `date` datetime DEFAULT NULL COMMENT 'Fecha de la Remesa',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Indica el estado de la Remesa',
  `type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de remesa a comunicar al SEPE',
  `communication_id` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Identificador resultante de la comunicacion',
  `outcome_file` mediumblob COMMENT 'Archivo Adjunto en binario',
  `outcome_file_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `income_file` mediumblob COMMENT 'Archivo respuesta en binario',
  `income_file_date` datetime DEFAULT NULL COMMENT 'Fecha de respuesta',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRATA_BATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRATA_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas de altas de Contratos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contrata_batch_detail`
--

DROP TABLE IF EXISTS `contrata_batch_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contrata_batch_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `contrata_batch` int(4) NOT NULL COMMENT 'Identificador de la Remesa',
  `contract` int(4) NOT NULL COMMENT 'Identificador del Contrato',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Estado de la linea de la remesa',
  PRIMARY KEY (`id`),
  KEY `IDX_CONTRATA_BATCH_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_CONTRATA_BATCH_DETAIL_CONTRACT` (`contract`),
  KEY `IDX_CONTRATA_BATCH_DETAIL_CONTRATA_BATCH` (`contrata_batch`),
  CONSTRAINT `FK_CONTRATA_BATCH_DETAIL_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRATA_BATCH_DETAIL_CONTRATA_BATCH` FOREIGN KEY (`contrata_batch`) REFERENCES `contrata_batch` (`id`),
  CONSTRAINT `FK_CONTRATA_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de Remesas de altas de Contratos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cost_profile`
--

DROP TABLE IF EXISTS `cost_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cost_profile` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion',
  `cost` double NOT NULL DEFAULT '0' COMMENT 'Costo por hora',
  PRIMARY KEY (`id`),
  KEY `IDX_COST_PROFILE_DOMAIN` (`domain`),
  CONSTRAINT `FK_COST_PROFILE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Perfiles de Costos para Usuarios';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `course` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Curso',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `code` varchar(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias del Curso',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Curso',
  `start_date` date NOT NULL COMMENT 'Fecha inicio del Curso',
  `end_date` date NOT NULL COMMENT 'Fecha fin del Curso',
  `academic_year` int(4) NOT NULL COMMENT 'Año Academico del Curso',
  `subject` int(4) NOT NULL COMMENT 'Materia del Curso',
  `level` int(4) NOT NULL COMMENT 'Nivel del Curso',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `alumn_limit` smallint(2) DEFAULT NULL COMMENT 'Limite de Alumnos del Curso',
  `status` tinyint(2) DEFAULT NULL COMMENT 'Estado del Curso',
  `comments` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Comentarios sobre el Curso',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `course_academicskill`
--

DROP TABLE IF EXISTS `course_academicskill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `course_academicskill` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador Unico',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `course` int(4) NOT NULL COMMENT 'Curso',
  `academic_skill` int(4) NOT NULL COMMENT 'Aptitud Academica',
  `weight` int(4) NOT NULL DEFAULT '1' COMMENT 'Peso de la Aptitud para calcular la Nota media',
  PRIMARY KEY (`id`),
  KEY `IDX_COURSE_ACADEMIC_SKILL_COURSE` (`course`),
  KEY `IDX_COURSE_ACADEMIC_SKILL_ACADEMIC_SKILL` (`academic_skill`),
  KEY `IDX_COURSE_ACADEMIC_SKILL_DOMAIN` (`domain`),
  CONSTRAINT `FK_COURSE_ACADEMIC_SKILL_ACADEMIC_SKILL` FOREIGN KEY (`academic_skill`) REFERENCES `academic_skill` (`id`),
  CONSTRAINT `FK_COURSE_ACADEMIC_SKILL_COURSE` FOREIGN KEY (`course`) REFERENCES `course` (`id`),
  CONSTRAINT `FK_COURSE_ACADEMIC_SKILL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Aptitudes Academicas por Curso';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `course_alumn`
--

DROP TABLE IF EXISTS `course_alumn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `course_alumn` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `course` int(4) NOT NULL COMMENT 'Identificador del Curso',
  `customer` int(4) NOT NULL COMMENT 'Identificador del Alumno',
  `status` tinyint(2) DEFAULT NULL COMMENT 'Estado del alumno en el curso',
  PRIMARY KEY (`id`),
  KEY `IDX_COURSE_ALUMN_COURSE` (`course`),
  KEY `IDX_COURSE_ALUMN_CUSTOMER` (`customer`),
  KEY `IDX_COURSE_ALUMN_DOMAIN` (`domain`),
  CONSTRAINT `FK_COURSE_ALUMN_COURSE` FOREIGN KEY (`course`) REFERENCES `course` (`id`),
  CONSTRAINT `FK_COURSE_ALUMN_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_COURSE_ALUMN_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Alumnos por Curso';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `course_evaluation`
--

DROP TABLE IF EXISTS `course_evaluation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `course_evaluation` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `course` int(4) NOT NULL COMMENT 'Identificador de Curso',
  `quality_skill` int(4) NOT NULL COMMENT 'Identificador de Aptitudes Calidad',
  `evaluation` double(15,3) DEFAULT '0.000' COMMENT 'Evaluaciones',
  `quantity` int(4) DEFAULT '0' COMMENT 'Cantidad',
  PRIMARY KEY (`id`),
  KEY `IDX_COURSE_EVALUATION_COURSE` (`course`),
  KEY `IDX_COURSE_EVALUATION_QUALITY_SKILL` (`quality_skill`),
  KEY `IDX_COURSE_EVALUATION_DOMAIN` (`domain`),
  CONSTRAINT `FK_COURSE_EVALUATION_COURSE` FOREIGN KEY (`course`) REFERENCES `course` (`id`),
  CONSTRAINT `FK_COURSE_EVALUATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_COURSE_EVALUATION_QUALITY_SKILL` FOREIGN KEY (`quality_skill`) REFERENCES `quality_skill` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Evaluaciones por Curso';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `course_instructor`
--

DROP TABLE IF EXISTS `course_instructor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `course_instructor` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `course` int(4) NOT NULL COMMENT 'Identificador del Curso',
  `task_holder` int(4) NOT NULL COMMENT 'Identificador del Profesor',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Profesor',
  PRIMARY KEY (`id`),
  KEY `IDX_COURSE_INSTRUCTOR_COURSE` (`course`),
  KEY `IDX_COURSE_INSTRUCTOR_DOMAIN` (`domain`),
  KEY `IDX_COURSE_INSTRUCTOR_TASK_HOLDER` (`task_holder`),
  CONSTRAINT `FK_COURSE_INSTRUCTOR_COURSE` FOREIGN KEY (`course`) REFERENCES `course` (`id`),
  CONSTRAINT `FK_COURSE_INSTRUCTOR_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_COURSE_INSTRUCTOR_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Profesores por Curso';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `course_level`
--

DROP TABLE IF EXISTS `course_level`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `course_level` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Nivel',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `description` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Nivel',
  PRIMARY KEY (`id`),
  KEY `IDX_COURSE_LEVEL_DOMAIN` (`domain`),
  CONSTRAINT `FK_COURSE_LEVEL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Niveles de Cursos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `course_observation`
--

DROP TABLE IF EXISTS `course_observation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `course_observation` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `course` int(4) NOT NULL COMMENT 'Identificador de Curso',
  `observation` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Observaciones',
  PRIMARY KEY (`id`),
  KEY `IDX_COURSE_OBSERVATION_COURSE` (`course`),
  KEY `IDX_COURSE_OBSERVATION_DOMAIN` (`domain`),
  CONSTRAINT `FK_COURSE_OBSERVATION_COURSE` FOREIGN KEY (`course`) REFERENCES `course` (`id`),
  CONSTRAINT `FK_COURSE_OBSERVATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Observaciones por Curso';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `course_schedule`
--

DROP TABLE IF EXISTS `course_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `course_schedule` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Horario',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `course` int(4) NOT NULL COMMENT 'Identificador del Curso',
  `day_of_week` tinyint(2) NOT NULL COMMENT 'Dia de la semana',
  `start_time` time NOT NULL COMMENT 'Hora de comienzo',
  `end_time` time NOT NULL COMMENT 'Hora de fin',
  PRIMARY KEY (`id`),
  KEY `IDX_COURSE_SCHEDULE_COURSE` (`course`),
  KEY `IDX_COURSE_SCHEDULE_DOMAIN` (`domain`),
  CONSTRAINT `FK_COURSE_SCHEDULE_COURSE` FOREIGN KEY (`course`) REFERENCES `course` (`id`),
  CONSTRAINT `FK_COURSE_SCHEDULE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Horarios de Cursos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `course_subject`
--

DROP TABLE IF EXISTS `course_subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `course_subject` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Materia',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `description` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Materia',
  PRIMARY KEY (`id`),
  KEY `IDX_COURSE_SUBJECT_DOMAIN` (`domain`),
  CONSTRAINT `FK_COURSE_SUBJECT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Materias de Cursos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cra_batch`
--

DROP TABLE IF EXISTS `cra_batch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cra_batch` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Indica el estado de la Remesa',
  `communication_id` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Identificador resultante de la comunicacion',
  `income_file` mediumblob COMMENT 'Archivo respuesta en binario',
  `income_file_date` datetime DEFAULT NULL COMMENT 'Fecha de respuesta',
  `outcome_file` mediumblob COMMENT 'Archivo Adjunto en binario',
  `outcome_file_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  PRIMARY KEY (`id`),
  KEY `IDX_CRA_BATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_CRA_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas del fichero cra';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cra_batch_detail`
--

DROP TABLE IF EXISTS `cra_batch_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cra_batch_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `cra_batch` int(4) NOT NULL COMMENT 'Identificador unico de la Remesa',
  `enterprise_ccc` int(4) NOT NULL COMMENT 'Identificador unico del ccc',
  PRIMARY KEY (`id`),
  KEY `IDX_CRA_BATCH_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_CRA_BATCH_DETAIL_CRA_BATCH` (`cra_batch`),
  KEY `IDX_CRA_BATCH_DETAIL_ENTERPRISE_CCC` (`enterprise_ccc`),
  CONSTRAINT `FK_CRA_BATCH_DETAIL_CRA_BATCH` FOREIGN KEY (`cra_batch`) REFERENCES `cra_batch` (`id`),
  CONSTRAINT `FK_CRA_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CRA_BATCH_DETAIL_ENTERPRISE_CCC` FOREIGN KEY (`enterprise_ccc`) REFERENCES `enterprise_ccc` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las Remesas del fichero cra';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `creditor`
--

DROP TABLE IF EXISTS `creditor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `creditor` (
  `registry` int(4) NOT NULL DEFAULT '0' COMMENT 'Registro del Acreedor',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `withholding` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Acreedor aplica retencion de impuestos',
  `vat_accrual_payment` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Acreedor esta acogido al Regimen Especial de Criterio de Caja',
  `transaction` tinyint(2) DEFAULT '0' COMMENT 'Tipo de transacciones del Acreedor',
  `status` tinyint(2) DEFAULT NULL COMMENT 'Estado del Acreedor',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `account` int(4) DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer` (
  `registry` int(4) NOT NULL DEFAULT '0' COMMENT 'Registro del Cliente',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `tariff` int(4) DEFAULT NULL COMMENT 'Tarifa asociada al Cliente',
  `surcharge` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Cliente tiene recargo de equivalencia',
  `withholding` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Cliente aplica retencion de impuestos',
  `transaction` tinyint(2) DEFAULT '0' COMMENT 'Tipo de transacciones del Cliente',
  `status` tinyint(2) DEFAULT NULL COMMENT 'Estado del Cliente',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `e_invoice` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Cliente desea recibir Facturas electronicas',
  `invoicing_group` int(4) DEFAULT NULL COMMENT 'Identificador de Grupo de Facturacion',
  `project_grouped` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Cliente desea agrupar Proyectos en una sola Factura',
  `delivery_grouped` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Cliente desea agrupar Albaranes en una sola Factura',
  `delivery_valuated` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Cliente desea imprimir el Albaran valorado',
  `account` int(4) DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `customer_fee`
--

DROP TABLE IF EXISTS `customer_fee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer_fee` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Cuota del Cliente',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project` int(4) DEFAULT NULL COMMENT 'Identificador del Proyecto',
  `customer` int(4) DEFAULT NULL COMMENT 'Identificador del Cliente',
  `line` smallint(2) DEFAULT '1' COMMENT 'Numero de linea de Cuota',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo',
  `description` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de la Cuota',
  `quantity` double(15,3) DEFAULT '0.000' COMMENT 'Cantidad de la Cuota',
  `price` double DEFAULT '0' COMMENT 'Precio de la Cuota',
  `discount_expr` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descuentos de la Cuota',
  `initial_date` date DEFAULT NULL COMMENT 'Fecha de inicio de la Cuota',
  `final_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion de la Cuota',
  `billing_date` date DEFAULT NULL COMMENT 'Proxima fecha de facturación de la Cuota',
  `period` smallint(2) DEFAULT '1' COMMENT 'Periodo de facturacion en meses de la Cuota',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad de la Cuota',
  `invoicing_group` int(4) DEFAULT NULL COMMENT 'Identificador de Grupo de Facturacion',
  `seller` int(4) DEFAULT NULL COMMENT 'Identificador de Agente Comercial',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuotas de Clientes';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `daily_tracking`
--

DROP TABLE IF EXISTS `daily_tracking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `daily_tracking` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Parte',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `task_holder` int(4) NOT NULL COMMENT 'Identificador del Usuario que realiza el Parte',
  `tracking_date` date NOT NULL COMMENT 'Fecha del Parte',
  `tracking_duration` double NOT NULL DEFAULT '0' COMMENT 'Tiempo invertido en el Parte',
  `job_type` int(4) NOT NULL COMMENT 'Tipo de Trabajo realizado en el Parte',
  `registry` int(4) DEFAULT NULL COMMENT 'Identificador del Cliente asociado al Parte',
  `project` int(4) DEFAULT NULL COMMENT 'Identificador del Expediente asociado al Parte',
  `activity_type` int(4) DEFAULT NULL COMMENT 'Identificador de la Actividad asociada al Parte',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios del Parte',
  `task` int(4) DEFAULT NULL COMMENT 'Identificador de la Tarea que provoca el Parte',
  `cost` double DEFAULT '0' COMMENT 'Costo',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Parte Diario de Trabajo';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `data_attach`
--

DROP TABLE IF EXISTS `data_attach`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_attach` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `source` tinyint(2) NOT NULL COMMENT 'Origen',
  `source_id` int(4) DEFAULT NULL COMMENT 'Identificador del origen',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Archivo Adjunto',
  `mimeType` tinyint(2) DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
  `drive_id` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Identificador de Google Drive',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_DATA_ATTACH_DOMAIN` (`domain`),
  KEY `IDX_DATA_ATTACH_SOURCE` (`source_id`,`source`),
  CONSTRAINT `FK_DATA_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='DATA ATTACH';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `data_response`
--

DROP TABLE IF EXISTS `data_response`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_response` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `code` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo de referencia',
  `response_date` date DEFAULT NULL COMMENT 'Fecha',
  `source` tinyint(2) NOT NULL COMMENT 'Origen',
  `source_id` int(4) DEFAULT NULL COMMENT 'Identificador del Origen',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_DATA_RESPONSE_DOMAIN` (`domain`),
  CONSTRAINT `FK_DATA_RESPONSE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='DATA RESPONSE';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `data_response_detail`
--

DROP TABLE IF EXISTS `data_response_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_response_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `data_response` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de data_response',
  `data_variable` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo de la variable',
  `data_value` text COLLATE latin1_spanish_ci NOT NULL COMMENT 'Valor de la variable',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_DATA_RESPONSE_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_DATA_RESPONSE_DETAIL_DATA_RESPONSE` (`data_response`),
  CONSTRAINT `FK_DATA_RESPONSE_DETAIL_DATA_RESPONSE` FOREIGN KEY (`data_response`) REFERENCES `data_response` (`id`),
  CONSTRAINT `FK_DATA_RESPONSE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='DATA RESPONSE DETAIL';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `db_version`
--

DROP TABLE IF EXISTS `db_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `db_version` (
  `version_number` varchar(10) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Numero de Version de la Base de Datos',
  PRIMARY KEY (`version_number`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Version de la Base de Datos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `deduction_concept`
--

DROP TABLE IF EXISTS `deduction_concept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `deduction_concept` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `code` varchar(25) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Deduccion Salarial',
  `description_decorable` tinyint(2) DEFAULT '0',
  `expression` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe',
  PRIMARY KEY (`id`),
  KEY `IDX_DEDUCTION_CONCEPT_DOMAIN` (`domain`),
  CONSTRAINT `FK_DEDUCTION_CONCEPT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6309 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos de deducciones';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `delivery`
--

DROP TABLE IF EXISTS `delivery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `delivery` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Albaran de Venta',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project` int(4) DEFAULT NULL COMMENT 'Identificador del Proyecto',
  `series` char(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie del Albaran',
  `number` int(4) NOT NULL DEFAULT '0' COMMENT 'Numero del Albaran',
  `customer` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Cliente',
  `address` int(4) DEFAULT NULL COMMENT 'Identificador de la Direccion de envio del Albaran',
  `issue_time` datetime DEFAULT NULL COMMENT 'Fecha de emision del Albaran',
  `pay_method` int(4) DEFAULT NULL COMMENT 'Identificador de la Forma de Pago',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad del Albaran',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado del Albaran',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios del Albaran',
  `remarks` text COLLATE latin1_spanish_ci COMMENT 'Observaciones del Albaran',
  `workplace` int(4) DEFAULT NULL COMMENT 'Identificador del Centro de Trabajo',
  `scope` int(4) NOT NULL DEFAULT '1' COMMENT 'Ambito del Albaran',
  `number_of_pymnts` smallint(2) DEFAULT '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint(2) DEFAULT '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint(2) DEFAULT '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Dias de pago',
  `bank_account` varchar(34) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'IBAN - Numero de Cuenta Bancaria Internacional',
  `bank_alias` varchar(25) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias del Banco',
  `bic` varchar(11) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'BIC - Codigo Identificador del Banco',
  `carrier` int(4) DEFAULT NULL COMMENT 'Identificador de la agencia de transporte',
  `carrier_packing` int(4) DEFAULT NULL COMMENT 'Identificador de la Hoja de ruta',
  `number_plate` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de matricula',
  `driver` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del conductor',
  `driver_document` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Documento del conductor',
  `total_packages` double(15,3) DEFAULT '0.000' COMMENT 'Numero total de bultos',
  `total_weight` double(15,3) DEFAULT '0.000' COMMENT 'Peso total',
  `shipping_alternative_address` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Primera parte de la Direccion de entrega',
  `shipping_alternative_address2` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Segunda parte de la Direccion de entrega',
  `shipping_alternative_zip` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo Postal de entrega',
  `shipping_alternative_city` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Localidad de entrega',
  `shipping_alternative_phone` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono de contacto de la entrega',
  `shipping_alternative_recipient` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Destinatario de la entrega',
  `shipping_contact` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del contacto para la entrega',
  `shipping_period` tinyint(2) DEFAULT '0' COMMENT 'Tipo de periodo de entrega',
  `tracking_number` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de expedicion',
  `shipping_status` tinyint(2) DEFAULT '0' COMMENT 'Estado de la entrega',
  `status_modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion del estado',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Albaranes de Venta';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `delivery_detail`
--

DROP TABLE IF EXISTS `delivery_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `delivery_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Detalle del Albaran de Venta',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `delivery` int(4) NOT NULL COMMENT 'Identificador del Albaran de Venta',
  `line` smallint(2) DEFAULT '1' COMMENT 'Numero de linea del Detalle dentro del Albaran',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo del Detalle de Albaran',
  `description` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Detalle de Albaran',
  `warehouse` int(4) NOT NULL COMMENT 'Identificador del Almacen',
  `quantity` double(15,3) DEFAULT NULL COMMENT 'Cantidad del Detalle de Albaran',
  `price` double DEFAULT '0' COMMENT 'Precio del Detalle de Albaran',
  `discount_expr` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descuentos del Detalle de Albaran',
  `sales_detail` int(4) DEFAULT NULL COMMENT 'Identificador del Detalle del Pedido de Venta asociado',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Albaran de Venta';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `department`
--

DROP TABLE IF EXISTS `department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `department` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del Departamento',
  PRIMARY KEY (`id`),
  KEY `IDX_DEPARTMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_DEPARTMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Departamentos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `domain`
--

DROP TABLE IF EXISTS `domain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `name` varchar(253) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Dominio',
  `description` varchar(128) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Dominio',
  `parent` int(4) DEFAULT NULL COMMENT 'Identificador del Dominio padre',
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de Dominio',
  `scope` int(4) DEFAULT NULL COMMENT 'Identificador del Ambito',
  `subDomainSuffix` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Sufijo de los Dominio Hijo',
  `enableHeredity` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el Dominio tiene deshabilitado la herencia de registros o no',
  `domainManagement` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el Dominio tiene capacidad de MultiDominio o no',
  `disableDomainManagement` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el Dominio tiene deshabilitado el mantenimiento de Dominios o no',
  `maxDocumentSize` int(4) DEFAULT NULL COMMENT 'Tamao Maximo de los Documentos',
  `maxTotalDocumentSize` int(4) DEFAULT NULL COMMENT 'Almacenamiento Documental Contratado',
  `maxDefinedUsers` int(4) DEFAULT NULL COMMENT 'Numero Maximo de Usuarios',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si el Dominio esta activo o no',
  `owner` varchar(256) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Emails del creador del Dominio',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  `expirationDate` date DEFAULT NULL COMMENT 'Fecha de Expiracion del Dominio',
  `lastAccess_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de ultimo acceso',
  `lastAccess_date` datetime DEFAULT NULL COMMENT 'Fecha de ultimo acceso',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_DOMAIN_NAME` (`name`),
  KEY `IDX_DOMAIN_PARENT` (`parent`),
  KEY `IDX_DOMAIN_SCOPE` (`scope`),
  CONSTRAINT `FK_DOMAIN_PARENT` FOREIGN KEY (`parent`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_DOMAIN_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8791 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Dominios';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `domain_application`
--

DROP TABLE IF EXISTS `domain_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain_application` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `application` int(4) NOT NULL COMMENT 'Identificador de la Aplicacion',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si la Aplicacion del Dominio esta activa o no',
  `audit_level` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Nivel de auditoria',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_DOMAIN_APPLICATION` (`domain`,`application`),
  KEY `IDX_APPLICATION_DOMAIN` (`domain`),
  KEY `IDX_DOMAIN_APPLICATION_APPLICATION` (`application`),
  CONSTRAINT `FK_APPLICATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_DOMAIN_APPLICATION_APPLICATION` FOREIGN KEY (`application`) REFERENCES `application` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10053 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Aplicacion web';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `domain_application_module`
--

DROP TABLE IF EXISTS `domain_application_module`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain_application_module` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `domain_application` int(4) NOT NULL COMMENT 'Identificador de la Aplicacion del Dominio',
  `module` tinyint(2) NOT NULL COMMENT 'Modulo de la Aplicacion del Dominio',
  PRIMARY KEY (`id`),
  KEY `IDX_DOMAIN_APPLICATION_MODULE_DOMAIN_APPLICATION` (`domain_application`),
  KEY `IDX_DOMAIN_APPLICATION_MODULE_DOMAIN` (`domain`),
  CONSTRAINT `FK_DOMAIN_APPLICATION_MODULE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_DOMAIN_APPLICATION_MODULE_DOMAIN_APPLICATION` FOREIGN KEY (`domain_application`) REFERENCES `domain_application` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32074 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Modulos de las Aplicaciones del Dominio';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `domain_gserviceaccount`
--

DROP TABLE IF EXISTS `domain_gserviceaccount`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain_gserviceaccount` (
  `client_id` varchar(100) COLLATE latin1_spanish_ci NOT NULL,
  `email_address` varchar(100) COLLATE latin1_spanish_ci DEFAULT NULL,
  `public_key` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL,
  `private_key` mediumblob,
  `client_secret` mediumblob,
  `domain` int(4) DEFAULT NULL,
  `size` double DEFAULT '0',
  `limit` double DEFAULT '0',
  `google_account` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`client_id`),
  KEY `FK_DOMAIN_GSERVICEACCOUNT_DOMAIN_IDX` (`domain`),
  CONSTRAINT `FK_DOMAIN_GSERVICEACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `elaboration`
--

DROP TABLE IF EXISTS `elaboration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `elaboration` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la elaboracion',
  `domain` int(4) NOT NULL COMMENT 'Identificador del dominio',
  `series` char(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie de la elaboracion',
  `number` int(4) NOT NULL DEFAULT '0' COMMENT 'Numero de la elaboracion',
  `date` datetime DEFAULT NULL COMMENT 'Fecha de elaboracion',
  `item` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del articulo base a elaborar',
  `description` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `warehouse` int(4) DEFAULT NULL COMMENT 'Identificador del almacen',
  `quantity` double(15,3) DEFAULT '0.000' COMMENT 'Cantidad a elaborar',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Indica el estado de la elaboracion',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  `remarks` text COLLATE latin1_spanish_ci COMMENT 'Observaciones',
  `source` tinyint(2) DEFAULT '0' COMMENT 'Origen',
  `source_id` int(4) DEFAULT '0' COMMENT 'Identificador del origen',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_ELABORATION_DOMAIN` (`domain`),
  KEY `IDX_ELABORATION_ITEM` (`item`),
  KEY `IDX_ELABORATION_WAREHOUSE` (`warehouse`),
  CONSTRAINT `FK_ELABORATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ELABORATION_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ELABORATION_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ordenes de elaboracion';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `elaboration_detail`
--

DROP TABLE IF EXISTS `elaboration_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `elaboration_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del detalle',
  `domain` int(4) NOT NULL COMMENT 'Identificador del dominio',
  `elaboration` int(4) NOT NULL COMMENT 'Identificador de la elaboracion',
  `date` datetime DEFAULT NULL COMMENT 'Fecha de elaboracion',
  `item` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del articulo no base elaborado',
  `quantity` double(15,3) DEFAULT '0.000' COMMENT 'Cantidad elaborado',
  `warehouse` int(4) DEFAULT NULL COMMENT 'Identificador del almacen',
  `add_info` text COLLATE latin1_spanish_ci COMMENT 'Informacion de uso interno',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `elaboration_detail_composition`
--

DROP TABLE IF EXISTS `elaboration_detail_composition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `elaboration_detail_composition` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la composicion',
  `domain` int(4) NOT NULL COMMENT 'Identificador del dominio',
  `elaboration_detail` int(4) NOT NULL COMMENT 'Identificador del lote elaborado',
  `item` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del articulo no base utilizado',
  `quantity` double(15,3) DEFAULT '0.000' COMMENT 'Cantidad utilizada',
  `warehouse` int(4) DEFAULT NULL COMMENT 'Identificador del almacen',
  `add_info` text COLLATE latin1_spanish_ci COMMENT 'Informacion de uso interno',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `enterprise`
--

DROP TABLE IF EXISTS `enterprise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `enterprise` (
  `registry` int(4) NOT NULL DEFAULT '1' COMMENT 'Registro de la Empresa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `calendar` int(4) DEFAULT NULL COMMENT 'Calendario',
  PRIMARY KEY (`registry`),
  KEY `IDX_ENTERPRISE_SCOPE` (`scope`),
  KEY `IDX_ENTERPRISE_CALENDAR` (`calendar`),
  KEY `IDX_ENTERPRISE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ENTERPRISE_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_ENTERPRISE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ENTERPRISE_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_ENTERPRISE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Empresa';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `enterprise_activity`
--

DROP TABLE IF EXISTS `enterprise_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `enterprise_activity` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Actividad de la Empresa',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `iae` int(4) DEFAULT NULL COMMENT 'Epigrafe IAE',
  `cnae` int(4) DEFAULT NULL COMMENT 'Identificador del CNAE',
  `type` tinyint(2) NOT NULL COMMENT 'Tipo de Actividad de la Empresa',
  `cnae2009` int(4) DEFAULT NULL COMMENT 'Identificador del CNAE 2009',
  `surcharge` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Actividad tiene de recargo de equivalencia',
  `vat_tax` int(4) DEFAULT NULL COMMENT 'Identificador del IVA por defecto',
  `retention_tax` int(4) DEFAULT NULL COMMENT 'Identificador del IRPF por defecto',
  `vat_regime` tinyint(2) DEFAULT NULL COMMENT 'Regimen de IVA',
  `retention_regime` tinyint(2) DEFAULT NULL COMMENT 'Regimen de IRPF',
  `start_date` date DEFAULT NULL COMMENT 'Fecha de inicio',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de fin',
  `prorata` double(5,2) DEFAULT '100.00' COMMENT 'Porcentaje de prorrata',
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
) ENGINE=InnoDB AUTO_INCREMENT=1460 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades de Empresas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `enterprise_ccc`
--

DROP TABLE IF EXISTS `enterprise_ccc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `enterprise_ccc` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `ccc` char(11) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor del Codigo Cuenta Cotizacion',
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de Cuenta Cotizacion',
  `enterprise_activity` int(4) NOT NULL COMMENT 'Identificador de la Actividad de Empresa',
  `geozone` int(4) DEFAULT NULL COMMENT 'Identificador de la Zona Geografica',
  PRIMARY KEY (`id`),
  KEY `IDX_ENTERPRISE_CCC_ENTERPRISE_ACTIVITY` (`enterprise_activity`),
  KEY `IDX_ENTERPRISE_CCC_GEOZONE` (`geozone`),
  KEY `IDX_ENTERPRISE_CCC_DOMAIN` (`domain`),
  CONSTRAINT `FK_ENTERPRISE_CCC_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ENTERPRISE_CCC_ENTERPRISE_ACTIVITY` FOREIGN KEY (`enterprise_activity`) REFERENCES `enterprise_activity` (`id`),
  CONSTRAINT `FK_ENTERPRISE_CCC_GEOZONE` FOREIGN KEY (`geozone`) REFERENCES `geozone` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1179 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Codigo Cuenta Cotizacion';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `enterprise_data`
--

DROP TABLE IF EXISTS `enterprise_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `enterprise_data` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de Empresa',
  `name` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `expression` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Expresion',
  `start_date` date DEFAULT NULL COMMENT 'Fecha de inicio',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  PRIMARY KEY (`id`),
  KEY `IDX_ENTERPRISE_DATA_ENTERPRISE` (`enterprise`),
  KEY `IDX_ENTERPRISE_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_ENTERPRISE_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ENTERPRISE_DATA_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto de la Empresa';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `evaluation_observation`
--

DROP TABLE IF EXISTS `evaluation_observation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `evaluation_observation` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `alumn` int(4) NOT NULL COMMENT 'Identificador de Alumno',
  `evaluation` tinyint(2) NOT NULL COMMENT 'Numero de Evaluacion',
  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  PRIMARY KEY (`id`),
  KEY `IDX_EVALUATION_OBSERVATION_ALUMN` (`alumn`),
  KEY `IDX_EVALUATION_OBSERVATION_DOMAIN` (`domain`),
  CONSTRAINT `FK_EVALUATION_OBSERVATION_ALUMN` FOREIGN KEY (`alumn`) REFERENCES `course_alumn` (`id`),
  CONSTRAINT `FK_EVALUATION_OBSERVATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Observaciones por Evaluacion';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fan_batch`
--

DROP TABLE IF EXISTS `fan_batch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fan_batch` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la remesa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Indica el estado de la remesa',
  `liquidation_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Indica el tipo de liquidacion',
  `communication_id` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Identificador resultante de la comunicacion',
  `income_file` mediumblob COMMENT 'Archivo respuesta en binario',
  `income_file_date` datetime DEFAULT NULL COMMENT 'Fecha de respuesta',
  `outcome_file` mediumblob COMMENT 'Archivo Adjunto en binario',
  `outcome_file_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  PRIMARY KEY (`id`),
  KEY `IDX_FAN_BATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_FAN_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas del fichero fan';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fan_batch_detail`
--

DROP TABLE IF EXISTS `fan_batch_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fan_batch_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del detalle',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fan_batch` int(4) NOT NULL COMMENT 'Identificador unico de la remesa',
  `enterprise_ccc` int(4) NOT NULL COMMENT 'Identificador unico del ccc',
  PRIMARY KEY (`id`),
  KEY `IDX_FAN_BATCH_DETAIL_FAN_BATCH` (`fan_batch`),
  KEY `IDX_FAN_BATCH_DETAIL_ENTERPRISE_CCC` (`enterprise_ccc`),
  KEY `IDX_FAN_BATCH_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_FAN_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FAN_BATCH_DETAIL_ENTERPRISE_CCC` FOREIGN KEY (`enterprise_ccc`) REFERENCES `enterprise_ccc` (`id`),
  CONSTRAINT `FK_FAN_BATCH_DETAIL_FAN_BATCH` FOREIGN KEY (`fan_batch`) REFERENCES `fan_batch` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las remesas del fichero fan';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `favorite`
--

DROP TABLE IF EXISTS `favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `favorite` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de Favorito',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `favorite_category` int(4) NOT NULL COMMENT 'Categoria a la que pertenece el Favorito',
  `description` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Favorito',
  `url` varchar(128) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Url del Favorito',
  `user_id` int(4) NOT NULL COMMENT 'Usuario al que pertenece el Favorito',
  PRIMARY KEY (`id`),
  KEY `IDX_FAVORITE_FAVORITE_CATEGORY` (`favorite_category`),
  KEY `IDX_FAVORITE_USER` (`user_id`),
  KEY `IDX_FAVORITE_DOMAIN` (`domain`),
  CONSTRAINT `FK_FAVORITE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FAVORITE_FAVORITE_CATEGORY` FOREIGN KEY (`favorite_category`) REFERENCES `favorite_category` (`id`),
  CONSTRAINT `FK_FAVORITE_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Favoritos de Usuario';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `favorite_category`
--

DROP TABLE IF EXISTS `favorite_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `favorite_category` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Categoria',
  `user_id` int(4) NOT NULL COMMENT 'Usuario al que pertenece la Categoria',
  PRIMARY KEY (`id`),
  KEY `IDX_FAVORITE_CATEGORY_USER` (`user_id`),
  KEY `IDX_FAVORITE_CATEGORY_DOMAIN` (`domain`),
  CONSTRAINT `FK_FAVORITE_CATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FAVORITE_CATEGORY_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias de Favoritos de Usuario';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fbatch`
--

DROP TABLE IF EXISTS `fbatch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fbatch` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Remesa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de la Remesa',
  `issue_date` date DEFAULT NULL COMMENT 'Fecha de emision de la Remesa',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Remesa',
  `status` tinyint(2) DEFAULT NULL COMMENT 'Estado de la Remesa',
  `rbank` int(4) DEFAULT NULL COMMENT 'Banco de la Compañia utilizado en la Remesa',
  `bank_statement_link` int(4) DEFAULT NULL COMMENT 'Identificador de la Linea del Extracto bancario',
  `payment` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si es un pago o un cobro',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad',
  `rattach` int(4) DEFAULT NULL COMMENT 'Identificador del Archivo Adjunto',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_FBATCH_BANK_STATEMENT_LINK` (`bank_statement_link`),
  KEY `IDX_FBATCH_RBANK` (`rbank`),
  KEY `IDX_FBATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_FBATCH_BANK_STATEMENT_LINK` FOREIGN KEY (`bank_statement_link`) REFERENCES `bank_statement_link` (`id`),
  CONSTRAINT `FK_FBATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FBATCH_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fbatch_detail`
--

DROP TABLE IF EXISTS `fbatch_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fbatch_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Detalle de la Remesa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fbatch` int(4) NOT NULL COMMENT 'Identificador de la Remesa',
  `finance` int(4) NOT NULL COMMENT 'Identificador del Vencimiento',
  `amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe del Detalle de la Remesa',
  `status` tinyint(2) DEFAULT NULL COMMENT 'Estado del Detalle de la Remesa',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_FBATCH_DETAIL_FINANCE` (`finance`),
  KEY `IDX_FBATCH_DETAIL_FBATCH` (`fbatch`),
  KEY `IDX_FBATCH_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_FBATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FBATCH_DETAIL_FBATCH` FOREIGN KEY (`fbatch`) REFERENCES `fbatch` (`id`),
  CONSTRAINT `FK_FBATCH_DETAIL_FINANCE` FOREIGN KEY (`finance`) REFERENCES `finance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de la Remesa';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `feature`
--

DROP TABLE IF EXISTS `feature`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `feature` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre de la Caracteristica',
  PRIMARY KEY (`id`),
  KEY `IDX_FEATURE_DOMAIN` (`domain`),
  CONSTRAINT `FK_FEATURE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Caracteristicas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `finance`
--

DROP TABLE IF EXISTS `finance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `finance` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Vencimiento',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `payment` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si es un pago o un cobro',
  `registry` int(4) DEFAULT NULL COMMENT 'Identificador del Cliente o Proveedor',
  `rdocument` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Documento del Cliente o Proveedor',
  `rdocument_type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de documento (NIF, CIF...)',
  `rdocument_country` varchar(2) COLLATE latin1_spanish_ci DEFAULT 'ES' COMMENT 'Pais del documento',
  `rname` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre completo del Cliente o Proveedor',
  `amount` double DEFAULT '0' COMMENT 'Importe del Vencimiento',
  `expenses` double(15,3) DEFAULT '0.000' COMMENT 'Gastos asociados al Vencimiento',
  `concept` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Concepto del Vencimiento',
  `invoice` int(4) DEFAULT NULL COMMENT 'Identificador de la Factura',
  `due_date` date DEFAULT NULL COMMENT 'Fecha de Vencimiento',
  `pay_method` int(4) DEFAULT NULL COMMENT 'Identificador de la Forma de Pago',
  `bank_account` varchar(34) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'IBAN - Numero de Cuenta Bancaria Internacional',
  `bank_alias` varchar(25) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias del Banco',
  `bic` varchar(11) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'BIC - Codigo Identificador del Banco',
  `cheque_number` varchar(24) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de cheque',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado del Vencimiento',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad del Vencimiento',
  `remarks` text COLLATE latin1_spanish_ci COMMENT 'Observaciones del Vencimiento',
  `scope` int(4) NOT NULL DEFAULT '1' COMMENT 'Ambito del Vencimiento',
  `manual` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Vencimiento es manual',
  `advance` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Vencimiento es un anticipo',
  `payroll` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Vencimiento es de Nominas',
  `prepayment` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Vencimiento es un Suplido',
  `source_id` int(4) DEFAULT NULL COMMENT 'Identificador del Origen del Vencimiento',
  `finance_group` int(4) DEFAULT NULL COMMENT 'Identificador unico del Vencimiento agrupador',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Vencimientos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `finance_pos`
--

DROP TABLE IF EXISTS `finance_pos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `finance_pos` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `finance` int(4) NOT NULL COMMENT 'Identificador de Vencimiento',
  `pos` int(4) NOT NULL COMMENT 'Identificador del TPV',
  `code` char(16) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo de autorizacion',
  `xml_response` text COLLATE latin1_spanish_ci COMMENT 'XML de respuesta',
  PRIMARY KEY (`id`),
  KEY `IDX_FINANCE_POS_DOMAIN` (`domain`),
  KEY `IDX_FINANCE_POS_FINANCE` (`finance`),
  KEY `IDX_FINANCE_POS_POS` (`pos`),
  CONSTRAINT `FK_FINANCE_POS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FINANCE_POS_FINANCE` FOREIGN KEY (`finance`) REFERENCES `finance` (`id`),
  CONSTRAINT `FK_FINANCE_POS_POS` FOREIGN KEY (`pos`) REFERENCES `pos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Vencimientos de TPV';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `finance_tracking`
--

DROP TABLE IF EXISTS `finance_tracking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `finance_tracking` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `finance` int(4) NOT NULL COMMENT 'Identificador de Vencimiento',
  `tracking_date` date NOT NULL COMMENT 'Fecha de Seguimiento',
  `type` tinyint(4) NOT NULL COMMENT 'Tipo de Seguimiento',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Seguimiento',
  `pm_type_detail` int(4) DEFAULT NULL COMMENT 'Identificador del Detalle por Tipo de Forma de Pago',
  `rbank` int(4) DEFAULT NULL COMMENT 'Identificador de la Cuenta Bancaria de la Compañia',
  `bank_statement_link` int(4) DEFAULT NULL COMMENT 'Identificador de la Linea del Extracto bancario',
  `amount` double(15,3) DEFAULT NULL COMMENT 'Importe del Seguimiento',
  `recorded` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si esta contabilizado o no',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Seguimiento de Vencimientos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_activity`
--

DROP TABLE IF EXISTS `fs_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_activity` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `year` int(4) NOT NULL COMMENT 'Ejercicio del Lote',
  `activity` int(4) DEFAULT NULL COMMENT 'Identificador de la Actividad',
  `epigraph` varchar(7) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Epigrafe IAE',
  `description` varchar(128) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del epigrafe',
  `farmer` tinyint(1) DEFAULT '0' COMMENT 'Actividad agricola',
  `max_person` double(15,3) DEFAULT '0.000' COMMENT 'Valor maximo de personas',
  `max_import` double(15,3) DEFAULT '0.000' COMMENT 'Valor maximo de importe',
  `vat_percent` double(15,3) DEFAULT '0.000' COMMENT 'IVA - porcentaje aplicable',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_ACTIVITY_DOMAIN` (`domain`),
  KEY `IDX_FS_ACTIVITY_ACTIVITY` (`activity`),
  CONSTRAINT `FK_FS_ACTIVITY_ACTIVITY` FOREIGN KEY (`activity`) REFERENCES `enterprise_activity` (`id`),
  CONSTRAINT `FK_FS_ACTIVITY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Fiscal. Datos previos de Modulos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_activity_info`
--

DROP TABLE IF EXISTS `fs_activity_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_activity_info` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fs_activity` int(4) NOT NULL COMMENT 'Actividad fiscal',
  `info_key` varchar(5) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Clave de informacion',
  `line` int(4) NOT NULL COMMENT 'Numero de linea',
  `type` tinyint(2) NOT NULL COMMENT 'Tipo de linea',
  `value` varchar(25) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Valor de la informacion',
  `factor` double(15,3) DEFAULT '0.000' COMMENT 'Factor',
  `base` double(15,3) DEFAULT '0.000' COMMENT 'Rendimiento neto',
  `unit` varchar(25) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Unidades',
  `min_value` double(15,3) DEFAULT '0.000' COMMENT 'Valor minimo',
  `max_value` double(15,3) DEFAULT '0.000' COMMENT 'Valor maximo',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_ACTIVITY_INFO_DOMAIN` (`domain`),
  KEY `IDX_FS_ACTIVITY_INFO_FS_ACTIVITY` (`fs_activity`),
  CONSTRAINT `FK_FS_ACTIVITY_INFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_ACTIVITY_INFO_FS_ACTIVITY` FOREIGN KEY (`fs_activity`) REFERENCES `fs_activity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Fiscal. Informacion de los Datos previos de Modulos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_mod347`
--

DROP TABLE IF EXISTS `fs_mod347`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_mod347` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `administration` tinyint(2) DEFAULT '0' COMMENT 'Administracion',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad',
  `complementary` tinyint(1) DEFAULT '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) DEFAULT '0' COMMENT 'Declaracion sustitutiva',
  `number` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion',
  `replaced_number` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion anterior',
  `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF',
  `name` varchar(40) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `contact_phone` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono Persona de Contacto',
  `contact_person` varchar(40) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Persona de Contacto',
  `contact_mail` varchar(50) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mail Persona de Contacto',
  `representative_document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante Legal',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MOD347_DOMAIN` (`domain`),
  CONSTRAINT `FK_FS_MOD347_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaracion de Model 347';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_mod347_detail`
--

DROP TABLE IF EXISTS `fs_mod347_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_mod347_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fs_mod347` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Declaracion',
  `sheet` varchar(1) COLLATE latin1_spanish_ci NOT NULL DEFAULT 'D' COMMENT 'Tipo de hoja (Declarado o Inmueble)',
  `type` varchar(1) COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Clave de operacion',
  `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF del declarado',
  `representative_document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante Legal',
  `registry` int(4) DEFAULT '0' COMMENT 'Identificador del Declarado',
  `name` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Apellidos  y Nombre del declarado',
  `province` int(4) DEFAULT '0' COMMENT 'Provincia del declarado',
  `country` varchar(2) COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Pais del declarado',
  `amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones',
  `first_quarter_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones primer trimestre',
  `second_quarter_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones segundo trimestre',
  `third_quarter_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones tercer trimestre',
  `fourth_quarter_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones cuarto trimestre',
  `asset_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones de inmuebles',
  `asset_first_quarter_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones de inmuebles del primer trimestre',
  `asset_second_quarter_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones de inmuebles del segundo trimestre',
  `asset_third_quarter_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones de inmuebles del tercer trimestre',
  `asset_fourth_quarter_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones de inmuebles del cuarto trimestre',
  `cash_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones en metalico',
  `cash_year` int(11) DEFAULT NULL COMMENT 'Ejercicio en el que se hubieran declarado las operaciones en metalico.',
  `insurance_operation` tinyint(1) DEFAULT '0' COMMENT 'Las Entidades Aseguradoras maracaran este campo para identificar las operaciones de seguros',
  `business_premise_rental` tinyint(1) DEFAULT '0' COMMENT 'Se marcara este campo para operaciones de arrendamiento de locales de negocio,',
  `asset_location` varchar(1) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Situacion del inmueble',
  `cadasdral_reference` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Refercia catastral',
  `asset_street_type` varchar(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo de via',
  `asset_street` varchar(50) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre de la via',
  `asset_street_number_type` varchar(3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo de numero de via',
  `asset_street_number` varchar(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de via',
  `asset_street_number_suffix` varchar(3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Calificador del numero de via',
  `asset_street_block` varchar(3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Bloque.',
  `asset_street_hall` varchar(3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Portal.',
  `asset_street_stair` varchar(3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Escalera.',
  `asset_street_floor` varchar(3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Planta.',
  `asset_street_door` varchar(3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Puerta.',
  `asset_street_complement` varchar(40) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Complemento.',
  `asset_street_city` varchar(30) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Complemento.',
  `asset_street_town` varchar(30) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Complemento.',
  `asset_street_town_code` varchar(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Complemento.',
  `asset_street_province` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Complemento.',
  `asset_street_zip` varchar(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Complemento.',
  `operator_nif` varchar(17) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF del operador intracomunitario',
  `vat_accrual` tinyint(1) DEFAULT '0' COMMENT 'Regimen de critrio de caja',
  `isp` tinyint(1) DEFAULT '0' COMMENT 'Inversion de sujeto pasivo',
  `deposit_regime` tinyint(1) DEFAULT '0' COMMENT 'Regimen de deposito',
  `vat_accrual_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones en reg, caja',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MOD347_DETAIL_FS_MOD347` (`fs_mod347`),
  KEY `IDX_FS_MOD347_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_FS_MOD347_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MOD347_DETAIL_FS_MOD347` FOREIGN KEY (`fs_mod347`) REFERENCES `fs_mod347` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de la Declaracion 347';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_mod349`
--

DROP TABLE IF EXISTS `fs_mod349`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_mod349` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `period` tinyint(2) DEFAULT '0' COMMENT 'Periodo de la Declaracion',
  `administration` tinyint(2) DEFAULT '0' COMMENT 'Administracion',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad',
  `complementary` tinyint(1) DEFAULT '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) DEFAULT '0' COMMENT 'Declaracion sustitutiva',
  `number` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion',
  `replaced_number` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion anterior',
  `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF',
  `name` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `representative_document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante Legal',
  `contact_phone` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono Persona de Contacto',
  `contact_mail` varchar(50) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mail Persona de Contacto',
  `contact_person` varchar(100) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Persona de Contacto',
  `periodicity_change` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indicador Cambio Periodicidad',
  `diff_enabled` tinyint(1) DEFAULT '1' COMMENT 'Declaracion por diferencia',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MOD349_DOMAIN` (`domain`),
  CONSTRAINT `FK_FS_MOD349_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaracion de Modelo 349';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_mod349_detail`
--

DROP TABLE IF EXISTS `fs_mod349_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_mod349_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fs_mod349` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Declaracion',
  `rectification` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Rectificacion',
  `type` varchar(1) COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Clave de operacion',
  `document` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Documento del operador',
  `registry` int(4) DEFAULT '0' COMMENT 'Identificador del Declarado',
  `name` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Apellidos y Nombre del Declarado',
  `country` varchar(2) COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Pais del Declarado',
  `accumulated` double(15,3) DEFAULT '0.000' COMMENT 'Importe acumulado de las operaciones',
  `declared` double(15,3) DEFAULT '0.000' COMMENT 'Importe declarado de las operaciones',
  `amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones',
  `rectified_year` int(4) DEFAULT NULL COMMENT 'Ejercicio de la Declaracion del importe rectificado',
  `rectified_period` tinyint(2) DEFAULT NULL COMMENT 'Periodo de la Declaracion del importe rectificado',
  `rectified_amount` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Importe rectificado',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MOD349_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_FS_MOD349_DETAIL_FS_MOD349` (`fs_mod349`),
  CONSTRAINT `FK_FS_MOD349_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MOD349_DETAIL_FS_MOD349` FOREIGN KEY (`fs_mod349`) REFERENCES `fs_mod349` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de la Declaracion 349';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_model`
--

DROP TABLE IF EXISTS `fs_model`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_model` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `period` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Periodo de la Declaracion',
  `administration` tinyint(2) NOT NULL COMMENT 'Administracion',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad',
  `complementary` tinyint(1) DEFAULT '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) DEFAULT '0' COMMENT 'Declaracion sustitutiva',
  `withoutActivity` tinyint(1) DEFAULT '0' COMMENT 'Sin Actividad',
  `model` varchar(3) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Tipo de modelo',
  `number` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion',
  `replaced_number` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion complementada o sustituida',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `finance` int(4) DEFAULT NULL COMMENT 'Identificador de Vencimiento',
  `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF',
  `surname` varchar(30) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Apellidos',
  `name` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `street_initial` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Sigla via',
  `street_name` varchar(17) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre de la via publica',
  `street_number` varchar(4) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de la via publica',
  `street_stair` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Escalera',
  `street_floor` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Piso',
  `street_door` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Puerta',
  `phone` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono',
  `town` varchar(20) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Municipio',
  `province` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Provincia',
  `zip` varchar(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo Postal',
  `admon_aeat` varchar(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de Administracion AEAT',
  `contact_person` varchar(100) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Persona de Contacto',
  `contact_phone` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telf. Fijo',
  `contact_cellular` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telf. Movil',
  `contact_email` varchar(100) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Email',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL_FINANCE` (`finance`),
  CONSTRAINT `FK_FS_MODEL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL_FINANCE` FOREIGN KEY (`finance`) REFERENCES `finance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaraciones Fiscales';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_model180`
--

DROP TABLE IF EXISTS `fs_model180`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_model180` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `administration` tinyint(2) NOT NULL COMMENT 'Administracion',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad',
  `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF',
  `name` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `contact_person` varchar(100) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Persona de Contacto',
  `contact_phone` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telf. Fijo de Contacto',
  `contact_mail` varchar(50) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mail Persona de Contacto',
  `complementary` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion sustitutiva',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `receipt` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion',
  `replaced_receipt` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion sustituida',
  `receiver_count_total` int(4) NOT NULL DEFAULT '0' COMMENT 'Numero total de perceptores',
  `receipt_total` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Importe declarado',
  `retention_total` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Importe declarado',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL180_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL180_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_FS_MODEL180_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL180_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaraciones 180';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_model180_detail`
--

DROP TABLE IF EXISTS `fs_model180_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_model180_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fs_model180` int(4) NOT NULL COMMENT 'Identificador del modelo 180',
  `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Perceptor',
  `name` varchar(40) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `representative_document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante',
  `province` int(4) NOT NULL DEFAULT '0' COMMENT 'Provincia',
  `inKind` tinyint(1) NOT NULL COMMENT 'Percepcion en especie',
  `perception` double(15,3) NOT NULL DEFAULT '0.000',
  `percentage` double(15,3) DEFAULT '0.000' COMMENT 'Porcentaje de retencion',
  `retention` double(15,3) NOT NULL DEFAULT '0.000',
  `accrual_year` int(4) NOT NULL DEFAULT '0',
  `location` varchar(1) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Situacion del inmueble',
  `cadasdral_reference` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Refercia catastral',
  `street_type` varchar(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo de via',
  `street_name` varchar(50) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre de la via',
  `number_type` varchar(3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo de numero de via',
  `number` varchar(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de via',
  `number_suffix` varchar(3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Calificador del numero de via',
  `block` varchar(3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Bloque.',
  `hall` varchar(3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Portal.',
  `stair` varchar(3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Escalera.',
  `floor` varchar(3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Planta.',
  `door` varchar(3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Puerta.',
  `complement` varchar(40) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion. Complemento.',
  `city` varchar(30) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Localidad o poblacion.',
  `town` varchar(30) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Municipio.',
  `town_code` varchar(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de municipio..',
  `province_code` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de provincia.',
  `zip` varchar(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo postal.',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL180_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL180_DETAIL_FS_MODEL180` (`fs_model180`),
  CONSTRAINT `FK_FS_MODEL180_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL180_DETAIL_FS_MODEL180` FOREIGN KEY (`fs_model180`) REFERENCES `fs_model180` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Declaraciones 180';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_model184`
--

DROP TABLE IF EXISTS `fs_model184`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_model184` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `administration` tinyint(2) NOT NULL COMMENT 'Administracion',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad',
  `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF',
  `name` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `contact_person` varchar(100) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Persona de Contacto',
  `contact_phone` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telf. Fijo de Contacto',
  `contact_mail` varchar(50) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mail Persona de Contacto',
  `complementary` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion sustitutiva',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `receipt` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion',
  `replaced_receipt` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion sustituida',
  `entity_type` varchar(1) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo de entidad nacional',
  `main_activity` varchar(1) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Actividad principal',
  `foreign_entity_type` varchar(1) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo de entidad extranjera',
  `foreign_object` varchar(1) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Objeto',
  `country` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Pais',
  `resident_percent` double(15,3) DEFAULT '0.000' COMMENT 'Porcentaje de renta atrib. a miembros residentes',
  `tax_is` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Tributacion en regimen del Impuesto sobre Sociedades',
  `net_sales_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe neto cifra de negocios',
  `lrdocument` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante',
  `lrname` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre Representante',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL184_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL184_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_FS_MODEL184_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL184_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaraciones 184';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_model184_detail`
--

DROP TABLE IF EXISTS `fs_model184_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_model184_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fs_model184` int(4) NOT NULL COMMENT 'Identificador del modelo 184',
  `type` varchar(1) COLLATE latin1_spanish_ci NOT NULL DEFAULT 'I' COMMENT 'Tipo de detalle',
  `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Perceptor',
  `name` varchar(40) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `representative_document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante',
  `key` varchar(1) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Clave Percepcion',
  `subkey` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Subclave Percepcion',
  `province` int(4) NOT NULL DEFAULT '0' COMMENT 'Provincia',
  `country` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Pais',
  `regime` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Regimen de determinacion de rendimientos',
  `activity_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Tipo de actividad',
  `epigraph` int(4) DEFAULT '0' COMMENT 'Epigrafe',
  `grantee_document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF persona o entidad cesionaria',
  `grantee_name` varchar(40) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre persona o entidad cesionaria',
  `adq_date` date DEFAULT NULL COMMENT 'Fecha adquisicion accion/participacion',
  `increase` double(15,3) DEFAULT NULL COMMENT 'Ajustes: Aumentos',
  `decrease` double(15,3) DEFAULT NULL COMMENT 'Ajustes: Disminuciones',
  `accounting_result` double(15,3) DEFAULT NULL COMMENT 'Resultado contable',
  `expenses` double(15,3) DEFAULT NULL COMMENT 'Gastos',
  `net_yield` double(15,3) DEFAULT NULL COMMENT 'Renta atribuible / Rend. Neto atribuible',
  `reduction_percent` double(15,3) DEFAULT NULL COMMENT 'Porc. Reduccion',
  `deduction_right_rent` double(15,3) DEFAULT NULL COMMENT 'Renta atrib. con drcho. deduccion',
  `result` double(15,3) DEFAULT NULL COMMENT 'Ganancias / Perdidas',
  `deduction_base` double(15,3) DEFAULT NULL COMMENT 'Base de la deduccion / Importe',
  `retention` double(15,3) DEFAULT NULL COMMENT 'Retenciones e ingresos a cuenta',
  `part_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Clave tipo de participe',
  `member_end_of_year` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Miembro a 31 diciembre',
  `member_days` int(4) DEFAULT '0' COMMENT 'Numero dias miembro',
  `part_percent` double(15,3) DEFAULT NULL COMMENT 'Porcentaje de participacion',
  `amount` double(15,3) DEFAULT NULL COMMENT 'Importe (rendimiento / retencion / deduccion)',
  `reduction` double(15,3) DEFAULT NULL COMMENT 'Reduccion',
  `address` varchar(60) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion',
  `location` varchar(1) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Situacion del inmueble',
  `cadasdral_reference` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Referencia catastral',
  `staff_expenses` double(15,3) DEFAULT '0.000' COMMENT 'Gastos de personal',
  `asset_acquisition` double(15,3) DEFAULT '0.000' COMMENT 'Adquisicion a terceros de bienes y servicios',
  `tax_deduction` double(15,3) DEFAULT '0.000' COMMENT 'Tributos fiscalmente deducibles y gastos financieros',
  `other_tax_deduction` double(15,3) DEFAULT '0.000' COMMENT 'Otros gastos fiscalmente deducibles',
  `vat_accrual_payment` tinyint(1) DEFAULT '0' COMMENT 'Regimen Especial de Criterio de Caja',
  `declared_key` varchar(1) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Clave del declarado',
  `nature` varchar(1) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Naturaleza del inmueble',
  `asset_percent` double(15,3) DEFAULT '0.000' COMMENT 'Porc. titularidad inmueble',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL184_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL184_DETAIL_FS_MODEL184` (`fs_model184`),
  CONSTRAINT `FK_FS_MODEL184_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL184_DETAIL_FS_MODEL184` FOREIGN KEY (`fs_model184`) REFERENCES `fs_model184` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Declaraciones 184';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_model190`
--

DROP TABLE IF EXISTS `fs_model190`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_model190` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `administration` tinyint(2) NOT NULL COMMENT 'Administracion',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad',
  `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF',
  `name` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `contact_person` varchar(100) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Persona de Contacto',
  `contact_phone` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telf. Fijo de Contacto',
  `contact_mail` varchar(50) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mail Persona de Contacto',
  `complementary` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion sustitutiva',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `receipt` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion',
  `replaced_receipt` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion sustituida',
  `receiver_count_total` int(4) NOT NULL DEFAULT '0' COMMENT 'Numero total de perceptores',
  `receipt_total` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Importe declarado',
  `retention_total` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Importe declarado',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL190_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL190_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_FS_MODEL190_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL190_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaraciones 190';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_model190_detail`
--

DROP TABLE IF EXISTS `fs_model190_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_model190_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fs_model190` int(4) NOT NULL COMMENT 'Identificador del modelo 190',
  `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Perceptor',
  `name` varchar(40) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `representative_document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante',
  `province` int(4) NOT NULL DEFAULT '0' COMMENT 'Provincia',
  `key` varchar(1) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Clave Percepcion',
  `subkey` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Subclave Percepcion',
  `perception` double(15,3) NOT NULL DEFAULT '0.000',
  `retention` double(15,3) NOT NULL DEFAULT '0.000',
  `in_kind_perception` double(15,3) NOT NULL DEFAULT '0.000',
  `in_kind_deposit` double(15,3) NOT NULL DEFAULT '0.000',
  `in_kind_output_deposit` double(15,3) NOT NULL DEFAULT '0.000',
  `accrual_year` int(4) NOT NULL DEFAULT '0',
  `ceuta_melilla` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Los datos anteriores corresponden a rendimientos obtenidos en Ceuta o Melilla',
  `birth_year` int(4) NOT NULL DEFAULT '0',
  `family_situation` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Situacion familiar',
  `spouse_document` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Documento del conyuge',
  `disability` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Grado de discapacidad',
  `contract` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Contrato o relacion',
  `labour_prolongation` tinyint(1) DEFAULT '0' COMMENT 'Prolongacion de la actividad laboral',
  `geographic_mobility` tinyint(1) DEFAULT '0' COMMENT 'Movilidad geografica',
  `applicable_reduction` double(15,3) DEFAULT NULL COMMENT 'Reducciones aplicables',
  `deducible_expenses` double(15,3) DEFAULT NULL COMMENT 'Gastos deducibles ',
  `spousal_support` double(15,3) DEFAULT NULL COMMENT 'Pension compensatoria a favor del cónyuge.',
  `food_annuity` double(15,3) DEFAULT NULL COMMENT 'Anualidades por alimentos en favor de los hijos.',
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
  `perception_il` double(15,3) DEFAULT '0.000' COMMENT 'Percepci�n Integra/valoracion derivada de incapacidad laboral',
  `retention_il` double(15,3) DEFAULT '0.000' COMMENT 'Retenciones practicadas/ingresos a cuenta efectuados derivadas de incapacidad laboral',
  `output_retention_il` double(15,3) DEFAULT '0.000' COMMENT 'Ingresos a cuenta repercutidos derivados de incapacidad laboral',
  `in_kind_perception_il` double(15,3) NOT NULL DEFAULT '0.000',
  `in_kind_deposit_il` double(15,3) NOT NULL DEFAULT '0.000',
  `in_kind_output_deposit_il` double(15,3) NOT NULL DEFAULT '0.000',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL190_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL190_DETAIL_FS_MODEL190` (`fs_model190`),
  CONSTRAINT `FK_FS_MODEL190_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL190_DETAIL_FS_MODEL190` FOREIGN KEY (`fs_model190`) REFERENCES `fs_model190` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Declaraciones 190';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_model193`
--

DROP TABLE IF EXISTS `fs_model193`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_model193` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `administration` tinyint(2) NOT NULL COMMENT 'Administracion',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad',
  `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF',
  `name` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `contact_person` varchar(100) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Persona de Contacto',
  `contact_phone` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telf. Fijo de Contacto',
  `contact_mail` varchar(50) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mail Persona de Contacto',
  `complementary` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion sustitutiva',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `receipt` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion',
  `replaced_receipt` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion sustituida',
  `receiver_count_total` int(4) NOT NULL DEFAULT '0' COMMENT 'Numero total de perceptores',
  `retention_base_total` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Base de retenciones e ingresos a cuenta',
  `retention_total` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Retenciones e ingresos a cuenta',
  `deposit_retention_total` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Retenciones e ingresos a cuenta ingresados',
  `expenses_total` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Gastos',
  `nature` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Naturaleza del declarante',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL193_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL193_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_FS_MODEL193_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL193_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaraciones 193';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_model193_detail`
--

DROP TABLE IF EXISTS `fs_model193_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_model193_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fs_model193` int(4) NOT NULL COMMENT 'Identificador del modelo 193',
  `type` varchar(1) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Tipo de Registro P (perceptor) o G (Gastos), ',
  `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Perceptor',
  `name` varchar(40) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `representative_document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante',
  `intermediary_payment` tinyint(1) DEFAULT NULL COMMENT 'Pago a un Mediador',
  `province` int(4) NOT NULL DEFAULT '0' COMMENT 'Provincia',
  `key_code` tinyint(1) DEFAULT NULL COMMENT 'Clave Codigo',
  `issuing_code` varchar(12) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo emisor',
  `key` varchar(1) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Clave de percepcion',
  `nature` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Naturaleza de la percepcion',
  `payment` tinyint(1) DEFAULT NULL COMMENT 'Pago',
  `code_type` varchar(1) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo Codigo',
  `lender_amount` double(15,3) NOT NULL DEFAULT '0.000',
  `account_code` varchar(20) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de cuenta,Numero id. prestamo',
  `pending` tinyint(1) DEFAULT NULL COMMENT 'Pendiente',
  `accrual_year` int(4) NOT NULL DEFAULT '0',
  `in_kind` tinyint(1) DEFAULT NULL COMMENT 'Percepcion en especie',
  `perception` double(15,3) NOT NULL DEFAULT '0.000',
  `reduction` double(15,3) NOT NULL DEFAULT '0.000',
  `retention_base` double(15,3) NOT NULL DEFAULT '0.000',
  `percent` double(5,3) NOT NULL DEFAULT '0.000',
  `retention` double(15,3) NOT NULL DEFAULT '0.000',
  `deponent_nature` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Naturaleza del declarante',
  `loan_start_date` date DEFAULT NULL COMMENT 'Fecha de inicio del prestamo',
  `loan_due_date` date DEFAULT NULL COMMENT 'Fecha de vencimiento del prestamo',
  `compensation` double(15,3) NOT NULL DEFAULT '0.000',
  `guarantee` double(15,3) NOT NULL DEFAULT '0.000',
  `expenses` double(15,3) NOT NULL DEFAULT '0.000',
  `penalization` double(15,3) DEFAULT '0.000' COMMENT 'Penalizaciones',
  `declarant_nature` tinyint(1) DEFAULT '0' COMMENT 'Naturaleza del declarante',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL193_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL193_DETAIL_FS_MODEL193` (`fs_model193`),
  CONSTRAINT `FK_FS_MODEL193_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL193_DETAIL_FS_MODEL193` FOREIGN KEY (`fs_model193`) REFERENCES `fs_model193` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Declaraciones 193';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_model200`
--

DROP TABLE IF EXISTS `fs_model200`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_model200` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `administration` tinyint(2) NOT NULL COMMENT 'Administracion',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Estado de la Declaracion',
  `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF',
  `name` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `phone1` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono 1',
  `phone2` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono 2',
  `complementary` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion complementaria',
  `receipt` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion',
  `complementary_receipt` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion sustituida',
  `cnae` varchar(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo CNAE',
  `period_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Tipo de periodo',
  `period_start` date NOT NULL COMMENT 'Inicio periodo',
  `period_end` date NOT NULL COMMENT 'Fin periodo',
  `fiscal_group` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de grupo fiscal',
  `dominant_document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF de la entidad dominante',
  `secretary_document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF del secretario',
  `secretary_name` varchar(25) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF del secretario',
  `irnr` date DEFAULT NULL COMMENT 'Fecha IRNR',
  `result_type` varchar(1) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT '(D) Devolucion, (I) Ingreso, (C) Cuota cero',
  `dev_type` varchar(1) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT '(R) Renuncia, (T) Transferencia',
  `pay_type` varchar(1) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT '(E) Efectivo, (A) Adeudo',
  `amount` double(15,3) DEFAULT NULL COMMENT 'Importe',
  `iban` varchar(34) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'IBAN',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `nrs_anexoIII` varchar(30) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NRS anexo III',
  `just_canarias` varchar(30) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de justificante Canarias',
  `nrs_anexoIV` varchar(30) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NRS anexo IV',
  `nrs_anexoV` varchar(30) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NRS anexo V',
  `bic` char(11) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'BIC - Codigo Identificador del Banco',
  `just_activos` varchar(30) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de justificante activos',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL200_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL200_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_FS_MODEL200_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL200_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Impuesto sobre sociedades';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_model200_detail`
--

DROP TABLE IF EXISTS `fs_model200_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_model200_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fs_model200` int(4) NOT NULL COMMENT 'Identificador del modelo 200',
  `key` varchar(7) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Clave Casilla',
  `value` double(15,3) DEFAULT NULL COMMENT 'Valor de la casilla',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL200_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL200_DETAIL_FS_MODEL200` (`fs_model200`),
  CONSTRAINT `FK_FS_MODEL200_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL200_DETAIL_FS_MODEL200` FOREIGN KEY (`fs_model200`) REFERENCES `fs_model200` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles Impuesto sobre sociedades';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_model200_registry`
--

DROP TABLE IF EXISTS `fs_model200_registry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_model200_registry` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fs_model200` int(4) NOT NULL COMMENT 'Identificador del modelo 200',
  `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF',
  `name` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `province` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Provincia',
  `country` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Pais',
  `residence` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Residencia',
  `representative` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Representante',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Tipo. Administrador/participacion ',
  `percent` double(15,3) DEFAULT NULL COMMENT 'Porcentaje',
  `nominal_value` double(15,3) DEFAULT NULL COMMENT 'Valor Nominal',
  `book_value` double(15,3) DEFAULT NULL COMMENT 'Valor en libros',
  `incomes` double(15,3) DEFAULT NULL COMMENT 'Ingresos por dividendos',
  `a_value` double(15,3) DEFAULT NULL COMMENT 'Correccion de valor',
  `b_value` double(15,3) DEFAULT NULL COMMENT 'Reversion por perdidas',
  `c_value` double(15,3) DEFAULT NULL COMMENT 'Efecto de la correccion',
  `d_value` double(15,3) DEFAULT NULL COMMENT 'Saldo de correcciones',
  `capital` double(15,3) DEFAULT NULL COMMENT 'Capital',
  `reserve` double(15,3) DEFAULT NULL COMMENT 'Reservas',
  `other_amounts` double(15,3) DEFAULT NULL COMMENT 'Otras partidas',
  `result` double(15,3) DEFAULT NULL COMMENT 'Resultado del ultimo ejercicio',
  `notary` varchar(20) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Notaria',
  `notary_date` date DEFAULT NULL COMMENT 'Fecha Notaria',
  `cc_value` double(15,3) DEFAULT NULL COMMENT 'Eliminacion del deterioro contable',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL200_REGISTRY_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL200_REGISTRY_FS_MODEL200` (`fs_model200`),
  CONSTRAINT `FK_FS_MODEL200_REGISTRY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL200_REGISTRY_FS_MODEL200` FOREIGN KEY (`fs_model200`) REFERENCES `fs_model200` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Adminitradores/participaciones del Impuesto sobre sociedades';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_model390`
--

DROP TABLE IF EXISTS `fs_model390`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_model390` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `administration` tinyint(2) NOT NULL COMMENT 'Administracion',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad',
  `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF',
  `name` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `complementary` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion sustitutiva',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `receipt` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion',
  `replaced_receipt` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion sustituida',
  `model` text COLLATE latin1_spanish_ci COMMENT 'Modelo XML de la Declaracion',
  `response` varchar(30) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Respuesta AEAT',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL390_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL390_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_FS_MODEL390_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL390_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaraciones 390';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_model_detail`
--

DROP TABLE IF EXISTS `fs_model_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_model_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fs_model` int(4) NOT NULL COMMENT 'Identificador de la Declaracion',
  `type` varchar(10) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Clave de la Declaracion',
  `description` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `acu_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe acumulado',
  `dec_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe declarado',
  `res_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe resultado',
  `adj_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe ajustado',
  `amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL_DETAIL_FS_MODEL` (`fs_model`),
  CONSTRAINT `FK_FS_MODEL_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL_DETAIL_FS_MODEL` FOREIGN KEY (`fs_model`) REFERENCES `fs_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de Declaraciones Fiscales';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_vat`
--

DROP TABLE IF EXISTS `fs_vat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_vat` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `period` tinyint(2) DEFAULT '0' COMMENT 'Periodo de la Declaracion',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad',
  `complementary` tinyint(1) DEFAULT '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) DEFAULT '0' COMMENT 'Declaracion sustitutiva',
  `tax_refund_registry` tinyint(1) DEFAULT '0' COMMENT 'Inscrito en registro de devolucion',
  `number` int(4) DEFAULT '0' COMMENT 'Numero de Decl. complementaria o sustitutiva',
  `prorata` double(5,2) DEFAULT '100.00' COMMENT 'Porcentaje de prorrata',
  `replaced_number` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion complementada o sustituida',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_VAT_DOMAIN` (`domain`),
  CONSTRAINT `FK_FS_VAT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaracion de Iva';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_vat_declaration`
--

DROP TABLE IF EXISTS `fs_vat_declaration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_vat_declaration` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fs_vat` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Declaracion',
  `without_activity` tinyint(1) DEFAULT '0' COMMENT 'Sin actividad',
  `administration` tinyint(2) DEFAULT '0' COMMENT 'Administracion',
  `percent` double DEFAULT '0' COMMENT 'Porcentaje atribuible',
  `operations_volume` double(15,3) DEFAULT '0.000' COMMENT 'Volumen de operaciones',
  `quota` double(15,3) DEFAULT '0.000' COMMENT 'Cuota atribuible',
  `prev_year_compensate_quota` double(15,3) DEFAULT '0.000' COMMENT 'Cuota a compensar de ejerc. anteriores',
  `done_deposits` double(15,3) DEFAULT '0.000' COMMENT 'Ingresos efectuados',
  `done_refunds` double(15,3) DEFAULT '0.000' COMMENT 'Devoluciones practicadas',
  `extra_charge` double(15,3) DEFAULT '0.000' COMMENT 'Recargo',
  `delay_interest` double(15,3) DEFAULT '0.000' COMMENT 'Intereses de demora',
  `compensate` double(15,3) DEFAULT '0.000' COMMENT 'A compensar',
  `pay_back` double(15,3) DEFAULT '0.000' COMMENT 'A devolver',
  `deposit` double(15,3) DEFAULT '0.000' COMMENT 'A ingresar',
  `prev_deposit` double(15,3) DEFAULT '0.000' COMMENT 'Ingresado anteriormente',
  `prev_pay_back` double(15,3) DEFAULT '0.000' COMMENT 'Devuelto anteriormente',
  `total_tax_debt` double(15,3) DEFAULT '0.000' COMMENT 'Total deuda tributaria',
  `rbank` int(4) DEFAULT NULL COMMENT 'Banco de la Compañia',
  `compensable` tinyint(1) DEFAULT '0' COMMENT 'Compensar o devolver',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Estado de la Declaracion',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_VAT_DECLARATION_FS_VAT` (`fs_vat`),
  KEY `IDX_FS_VAT_DECLARATION_RBANK` (`rbank`),
  KEY `IDX_FS_VAT_DECLARATION_DOMAIN` (`domain`),
  CONSTRAINT `FK_FS_VAT_DECLARATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_VAT_DECLARATION_FS_VAT` FOREIGN KEY (`fs_vat`) REFERENCES `fs_vat` (`id`),
  CONSTRAINT `FK_FS_VAT_DECLARATION_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Resultado de la Declaracion de Iva';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fs_vat_detail`
--

DROP TABLE IF EXISTS `fs_vat_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_vat_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fs_vat` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Declaracion',
  `vat_key` varchar(3) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Clave de la Declaracion',
  `percent` double DEFAULT '0' COMMENT 'Porcentaje de Iva',
  `taxable_base` double(15,3) DEFAULT '0.000' COMMENT 'Base imponible',
  `quota` double(15,3) DEFAULT '0.000' COMMENT 'Cuota',
  `deductible_quota` double(15,3) DEFAULT '0.000' COMMENT 'Cuota deducible',
  `adj_taxable_base` double(15,3) DEFAULT '0.000' COMMENT 'Base imponible ajustada',
  `adj_quota` double(15,3) DEFAULT '0.000' COMMENT 'Cuota ajustada',
  `adj_deductible_quota` double(15,3) DEFAULT '0.000' COMMENT 'Cuota deducible ajustada',
  `acu_taxable_base` double(15,3) DEFAULT '0.000' COMMENT 'Base imponible acumulada',
  `acu_quota` double(15,3) DEFAULT '0.000' COMMENT 'Cuota acumulada',
  `acu_deductible_quota` double(15,3) DEFAULT '0.000' COMMENT 'Cuota deducible acumulada',
  `dec_taxable_base` double(15,3) DEFAULT '0.000' COMMENT 'Base imponible declarado',
  `dec_quota` double(15,3) DEFAULT '0.000' COMMENT 'Cuota declarado',
  `dec_deductible_quota` double(15,3) DEFAULT '0.000' COMMENT 'Cuota deducible declarado',
  `res_taxable_base` double(15,3) DEFAULT '0.000' COMMENT 'Base imponible resultado',
  `res_quota` double(15,3) DEFAULT '0.000' COMMENT 'Cuota resultado',
  `res_deductible_quota` double(15,3) DEFAULT '0.000' COMMENT 'Cuota deducible resultado',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_VAT_DETAIL_FS_VAT` (`fs_vat`),
  KEY `IDX_FS_VAT_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_FS_VAT_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_VAT_DETAIL_FS_VAT` FOREIGN KEY (`fs_vat`) REFERENCES `fs_vat` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de Declaracion de Iva';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `geotree`
--

DROP TABLE IF EXISTS `geotree`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `geotree` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `parent` int(4) DEFAULT NULL COMMENT 'Identificador de la Zona Geografica Padre',
  `child` int(4) NOT NULL COMMENT 'Identificador de la Zona Geografica Hijo',
  PRIMARY KEY (`id`),
  KEY `IDX_GEOTREE_PARENT_GEOZONE` (`parent`),
  KEY `IDX_GEOTREE_CHILD_GEOZONE` (`child`),
  KEY `IDX_GEOTREE_DOMAIN` (`domain`),
  CONSTRAINT `FK_GEOTREE_CHILD_GEOZONE` FOREIGN KEY (`child`) REFERENCES `geozone` (`id`),
  CONSTRAINT `FK_GEOTREE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_GEOTREE_PARENT_GEOZONE` FOREIGN KEY (`parent`) REFERENCES `geozone` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=133196 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Jerarquia de Zonas Geograficas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `geozone`
--

DROP TABLE IF EXISTS `geozone`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `geozone` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Zona Geografica',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Zona Geografica',
  `code` varchar(3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de la Zona Geografica',
  `system` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si es una Zona Geografica del sistema',
  PRIMARY KEY (`id`),
  KEY `IDX_GEOZONE_DOMAIN` (`domain`),
  CONSTRAINT `FK_GEOZONE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=134049 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Zonas Geograficas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `geozone_irpf`
--

DROP TABLE IF EXISTS `geozone_irpf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `geozone_irpf` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `geozone_code` varchar(3) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo de la Zona Geografica',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe rendimiento anual',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=853 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tabla de tramos del IRPF';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `geozone_irpf_descendant`
--

DROP TABLE IF EXISTS `geozone_irpf_descendant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `geozone_irpf_descendant` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `geozone_irpf` int(4) DEFAULT NULL COMMENT 'Identificador del tramo de IRPF',
  `descendant` tinyint(2) DEFAULT '0' COMMENT 'Descendientes',
  `percent` double(15,2) DEFAULT '0.00' COMMENT 'Porcentaje',
  PRIMARY KEY (`id`),
  KEY `IDX_GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF` (`geozone_irpf`),
  CONSTRAINT `FK_GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF` FOREIGN KEY (`geozone_irpf`) REFERENCES `geozone_irpf` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12362 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tabla de porcentajes IRPF segun descendientes';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `geozone_irpf_handicap`
--

DROP TABLE IF EXISTS `geozone_irpf_handicap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `geozone_irpf_handicap` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `geozone_irpf` int(4) DEFAULT NULL COMMENT 'Identificador del tramo de IRPF',
  `handicap` tinyint(2) DEFAULT '0' COMMENT 'Grado Minusvalia',
  `percent` double(15,2) DEFAULT '0.00' COMMENT 'Porcentaje',
  PRIMARY KEY (`id`),
  KEY `IDX_GEOZONE_IRPF_HANDICAP_GEOZONE_IRPF` (`geozone_irpf`),
  CONSTRAINT `FK_GEOZONE_IRPF_HANDICAP_GEOZONE_IRPF` FOREIGN KEY (`geozone_irpf`) REFERENCES `geozone_irpf` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3358 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tabla de  ';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `holiday`
--

DROP TABLE IF EXISTS `holiday`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `holiday` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de la Festividad',
  `holiday` int(4) DEFAULT NULL COMMENT 'Identificador de Festividad',
  `editable` tinyint(1) DEFAULT '0' COMMENT 'Indica si es editable o no',
  PRIMARY KEY (`id`),
  KEY `IDX_HOLIDAY_HOLIDAY` (`holiday`),
  KEY `IDX_HOLIDAY_DOMAIN` (`domain`),
  CONSTRAINT `FK_HOLIDAY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_HOLIDAY_HOLIDAY` FOREIGN KEY (`holiday`) REFERENCES `holiday` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=96 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Festividades';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `holiday_detail`
--

DROP TABLE IF EXISTS `holiday_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `holiday_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `holiday` int(4) NOT NULL COMMENT 'Identificador de Festividad',
  `date` date NOT NULL COMMENT 'Fecha Festiva',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de Festividad',
  PRIMARY KEY (`id`),
  KEY `IDX_HOLIDAY_DETAIL_HOLIDAY` (`holiday`),
  KEY `IDX_HOLIDAY_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_HOLIDAY_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_HOLIDAY_DETAIL_HOLIDAY` FOREIGN KEY (`holiday`) REFERENCES `holiday` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=870 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de Festividades';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hotel`
--

DROP TABLE IF EXISTS `hotel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hotel` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `code` varchar(16) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo del Hotel',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `phone` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono del Hotel',
  `fax` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Fax del Hotel',
  `email` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Email del Hotel',
  `web` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Web del Hotel',
  `service_catalogue` int(4) DEFAULT NULL COMMENT 'Identificador del Catalogo de Servicios',
  `sheet_changing` tinyint(2) DEFAULT NULL COMMENT 'Dias entre cambio de sabanas',
  `police_code` varchar(10) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del Hotel para la policia',
  `police_counter` int(4) DEFAULT '0' COMMENT 'Contador para envio de ficheros a la policia',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `iae`
--

DROP TABLE IF EXISTS `iae`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iae` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `section` varchar(1) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Seccion',
  `epigraph` varchar(8) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Epigrafe',
  `title` varchar(255) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Titulo',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1070 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='IAE';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `iattach`
--

DROP TABLE IF EXISTS `iattach`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iattach` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Archivo Adjunto del Articulo',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `item` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de Articulo',
  `mimeType` tinyint(2) DEFAULT NULL COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Archivo Adjunto',
  `driveId` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_IATTACH_ITEM` (`item`),
  KEY `IDX_IATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_IATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_IATTACH_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Articulos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `income`
--

DROP TABLE IF EXISTS `income`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `income` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Albaran de Compra',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project` int(4) DEFAULT NULL COMMENT 'Identificador del Proyecto',
  `reference_code` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo de referencia del Albaran',
  `supplier` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Proveedor',
  `address` int(4) DEFAULT NULL COMMENT 'Identificador de la Direccion del Proveedor',
  `issue_time` date DEFAULT NULL COMMENT 'Fecha de emision del Albaran',
  `pay_method` int(4) DEFAULT NULL COMMENT 'Identificador de la Forma de Pago',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad del Albaran',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado del Albaran',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios del Albaran',
  `remarks` text COLLATE latin1_spanish_ci COMMENT 'Observaciones del Albaran',
  `workplace` int(4) DEFAULT NULL COMMENT 'Identificador del Centro de Trabajo',
  `scope` int(4) NOT NULL DEFAULT '1' COMMENT 'Ambito del Albaran',
  `number_of_pymnts` smallint(2) DEFAULT '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint(2) DEFAULT '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint(2) DEFAULT '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Dias de pago',
  `bank_account` varchar(34) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'IBAN - Numero de Cuenta Bancaria Internacional',
  `bank_alias` varchar(25) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias del Banco',
  `bic` varchar(11) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'BIC - Codigo Identificador del Banco',
  `carrier_packing` int(4) DEFAULT NULL COMMENT 'Identificador de la Hoja de ruta',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Albaranes de Compra';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `income_detail`
--

DROP TABLE IF EXISTS `income_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `income_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Detalle del Albaran de Compra',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `income` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Albaran de Compra',
  `project` int(4) DEFAULT NULL COMMENT 'Identificador del Proyecto',
  `line` smallint(2) DEFAULT '1' COMMENT 'Numero de linea del Detalle dentro del Albaran',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo del Detalle de Albaran',
  `description` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Detalle de Albaran',
  `warehouse` int(4) NOT NULL COMMENT 'Identificador del Almacen',
  `quantity` double(15,3) DEFAULT NULL COMMENT 'Cantidad del Detalle de Albaran',
  `price` double DEFAULT '0' COMMENT 'Precio del Detalle de Albaran',
  `discount_expr` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descuentos del Detalle de Albaran',
  `purchase_detail` int(4) DEFAULT NULL COMMENT 'Identificador del Detalle del Pedido de Compra asociado',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Albaran de Compra';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `inventory` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Inventario',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `inventory_date` date NOT NULL DEFAULT '0000-00-00' COMMENT 'Fecha de Inventario',
  `warehouse` int(4) NOT NULL DEFAULT '0' COMMENT 'Almacen Inventariado',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Inventario',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado del Inventario',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_INVENTORY_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVENTORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Inventarios de Almacenes';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inventory_detail`
--

DROP TABLE IF EXISTS `inventory_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `inventory_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Detalle del Inventario',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `inventory` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de Inventario',
  `item` int(4) NOT NULL DEFAULT '0' COMMENT 'Articulo Inventariado',
  `actual_quantity` double(15,3) DEFAULT '0.000' COMMENT 'Cantidad actual del Articulo Inventariado',
  `real_quantity` double(15,3) DEFAULT '0.000' COMMENT 'Cantidad real del Articulo Inventariado',
  `cost` double(15,3) DEFAULT '0.000' COMMENT 'Coste del Articulo Inventariado',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_INVENTORY_DETAIL_INVENTORY` (`inventory`),
  KEY `IDX_INVENTORY_DETAIL_ITEM` (`item`),
  KEY `IDX_INVENTORY_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVENTORY_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVENTORY_DETAIL_INVENTORY` FOREIGN KEY (`inventory`) REFERENCES `inventory` (`id`),
  CONSTRAINT `FK_INVENTORY_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Inventarios de Almacenes';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invest_asset`
--

DROP TABLE IF EXISTS `invest_asset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invest_asset` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `activity` int(4) DEFAULT NULL COMMENT 'Identificador de la Actividad',
  `description` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Bien',
  `type` tinyint(2) NOT NULL COMMENT 'Tipo de Bien',
  `regime` tinyint(2) NOT NULL COMMENT 'Regimen',
  `start_date` date DEFAULT NULL COMMENT 'Fecha de alta',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de baja',
  `vat_percent` double DEFAULT '0' COMMENT 'Porcentaje de afectacion de IVA',
  `retention_percent` double DEFAULT '0' COMMENT 'Porcentaje de afectacion de imposicion directa',
  PRIMARY KEY (`id`),
  KEY `IDX_INVEST_ASSET_DOMAIN` (`domain`),
  KEY `IDX_INVEST_ASSET_ACTIVITY` (`activity`),
  CONSTRAINT `FK_INVEST_ASSET_ACTIVITY` FOREIGN KEY (`activity`) REFERENCES `enterprise_activity` (`id`),
  CONSTRAINT `FK_INVEST_ASSET_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bienes afectos o de inversion';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invoice`
--

DROP TABLE IF EXISTS `invoice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Factura',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `activity` int(4) DEFAULT NULL COMMENT 'Identificador de la Actividad',
  `invest_asset` int(4) DEFAULT NULL COMMENT 'Identificador del Bien afecto',
  `project` int(4) DEFAULT NULL COMMENT 'Identificador del Proyecto',
  `series` char(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie de la Factura',
  `number` int(4) NOT NULL DEFAULT '0' COMMENT 'Numero de la Factura',
  `reference_code` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de referencia de la Factura',
  `registry` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Cliente o Proveedor',
  `rdocument` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Documento del Cliente o Proveedor',
  `rdocument_type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de documento (NIF, CIF...)',
  `rdocument_country` varchar(2) COLLATE latin1_spanish_ci DEFAULT 'ES' COMMENT 'Pais del documento',
  `rname` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre completo del Cliente o Proveedor',
  `raddress` int(4) DEFAULT NULL COMMENT 'Identificador de la Direccion de envio de la Factura',
  `issue_date` date DEFAULT NULL COMMENT 'Fecha de emision de la Factura',
  `tax_date` date DEFAULT NULL COMMENT 'Fecha de Impuestos de la Factura',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad de la Factura',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado de la Factura',
  `type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de Factura (Compra o Venta)',
  `surcharge` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Factura tiene recargo de equivalencia',
  `withholding` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Factura aplica retencion de impuestos',
  `withholding_farmer` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Factura aplica retencion de Regimen Especial de Agricultura y Pesca',
  `vat_accrual_payment` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Factura se incluye en el Regimen Especial de Criterio de Caja',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Factura',
  `remarks` text COLLATE latin1_spanish_ci COMMENT 'Observaciones de la Factura',
  `investment` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Factura es una inversion',
  `transaction` tinyint(2) DEFAULT '0' COMMENT 'Tipo de transaccion',
  `signed` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Factura esta firmada electronicamente',
  `scope` int(4) NOT NULL DEFAULT '1' COMMENT 'Ambito de la Factura',
  `service` tinyint(1) DEFAULT '0' COMMENT 'Indica si es una Factura de servicios',
  `rectification_type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de rectificacion (Normal o Especial)',
  `rectification_invoice` int(4) DEFAULT NULL COMMENT 'Relacion de rectificacion de Facturas',
  `advance` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Factura es un anticipo',
  `pos_shift` int(4) DEFAULT NULL COMMENT 'Identificador del Turno de trabajo',
  `seller` int(4) DEFAULT NULL COMMENT 'Identificador de Agente Comercial',
  `taxable_base` double DEFAULT '0' COMMENT 'Base Imponible de la Factura',
  `vat_quota` double DEFAULT '0' COMMENT 'Cuota de IVA de la Factura',
  `retention_quota` double DEFAULT '0' COMMENT 'Cuota de IRPF de la Factura',
  `total` double DEFAULT '0' COMMENT 'Total Factura',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Facturas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invoice_address`
--

DROP TABLE IF EXISTS `invoice_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice_address` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `invoice` int(4) NOT NULL COMMENT 'Identificador de la Factura',
  `street_type` varchar(2) COLLATE latin1_spanish_ci DEFAULT 'CL' COMMENT 'Tipo de via',
  `address` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Primera parte de la Direccion',
  `number` varchar(12) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero',
  `address2` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Segunda parte de la Direccion',
  `zip` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo Postal',
  `city` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Localidad',
  `province` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Provincia',
  `geozone` int(4) DEFAULT NULL COMMENT 'Identificador de la Zona Geografica',
  PRIMARY KEY (`id`),
  KEY `IDX_INVOICE_ADDRESS_INVOICE` (`invoice`),
  KEY `IDX_INVOICE_ADDRESS_GEOZONE` (`geozone`),
  KEY `IDX_INVOICE_ADDRESS_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICE_ADDRESS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_ADDRESS_GEOZONE` FOREIGN KEY (`geozone`) REFERENCES `geozone` (`id`),
  CONSTRAINT `FK_INVOICE_ADDRESS_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Direcciones de la Factura';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invoice_attach`
--

DROP TABLE IF EXISTS `invoice_attach`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice_attach` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `invoice` int(4) NOT NULL COMMENT 'Identificador de la Factura',
  `mimeType` tinyint(2) DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de Archivo Adjunto',
  `attach_date` date DEFAULT NULL COMMENT 'Fecha del Archivo Adjunto',
  `driveId` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_INVOICE_ATTACH_INVOICE` (`invoice`),
  KEY `IDX_INVOICE_ATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICE_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_ATTACH_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Facturas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invoice_detail`
--

DROP TABLE IF EXISTS `invoice_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Detalle de la Factura',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `invoice` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Factura',
  `invest_asset` int(4) DEFAULT NULL COMMENT 'Identificador del Bien afecto',
  `project` int(4) DEFAULT NULL COMMENT 'Identificador del Proyecto',
  `line` smallint(2) DEFAULT '1' COMMENT 'Numero de linea del Detalle dentro de la Factura',
  `item` int(4) DEFAULT NULL COMMENT 'Identificador del Articulo del Detalle de Factura',
  `description` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Detalle de Factura',
  `quantity` double(15,3) DEFAULT '0.000' COMMENT 'Cantidad del Detalle de Factura',
  `price` double DEFAULT '0' COMMENT 'Precio del Detalle de Factura',
  `discount_expr` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descuentos del Detalle de Factura',
  `source` tinyint(2) DEFAULT '0' COMMENT 'Origen del Detalle de la Factura',
  `source_id` int(4) DEFAULT NULL COMMENT 'Identificador del Origen del Detalle de la Factura',
  `taxable_base` double(15,4) DEFAULT '0.0000' COMMENT 'Base Imponible del Detalle de Factura',
  `taxes` double(15,3) DEFAULT '0.000' COMMENT 'Tasas del Detalle de Factura',
  `prepayment` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Detalle de Factura es un Suplido',
  `seller` int(4) DEFAULT NULL COMMENT 'Identificador de Agente Comercial',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `warehouse` int(4) DEFAULT NULL COMMENT 'Identificador del Almacen',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de la Factura';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invoice_detail_account`
--

DROP TABLE IF EXISTS `invoice_detail_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice_detail_account` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `invoice_detail` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Linea de Factura',
  `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY (`id`),
  KEY `IDX_INVOICE_DETAIL_ACCOUNT_ACCOUNT` (`account`),
  KEY `IDX_INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL` (`invoice_detail`),
  KEY `IDX_INVOICE_DETAIL_ACCOUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICE_DETAIL_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_INVOICE_DETAIL_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL` FOREIGN KEY (`invoice_detail`) REFERENCES `invoice_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables asociadas a Lineas de Facturas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invoice_detail_commission`
--

DROP TABLE IF EXISTS `invoice_detail_commission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice_detail_commission` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `invoice_detail` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Linea de Factura',
  `commission` double DEFAULT '0' COMMENT 'Porcentaje de Comision',
  `amount` double DEFAULT '0' COMMENT 'Importe de la Comision',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado de la Comision',
  `pay_date` date DEFAULT NULL COMMENT 'Fecha de liquidacion',
  PRIMARY KEY (`id`),
  KEY `IDX_INVOICE_DETAIL_COMMISSION_INVOICE_DETAIL` (`invoice_detail`),
  KEY `IDX_INVOICE_DETAIL_COMMISSION_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICE_DETAIL_COMMISSION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_DETAIL_COMMISSION_INVOICE_DETAIL` FOREIGN KEY (`invoice_detail`) REFERENCES `invoice_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones asociadas a Lineas de Facturas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invoice_tax`
--

DROP TABLE IF EXISTS `invoice_tax`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice_tax` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Impuesto de la Factura',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `invoice_detail` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Detalle de la Factura',
  `tax_type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de Impuesto del Detalle de la Factura',
  `base` double(15,4) DEFAULT '0.0000' COMMENT 'Base Imponible de Impuesto del Detalle de la Factura',
  `percentage` double(15,3) DEFAULT '0.000' COMMENT 'Porcentaje de Impuesto del Detalle de la Factura',
  `surcharge` double(15,3) DEFAULT '0.000' COMMENT 'Porcentaje del recargo de equivalencia del Detalle de la Factura',
  `quota` double DEFAULT '0' COMMENT 'Cuota de Impuesto del Detalle de la Factura',
  `surcharge_quota` double DEFAULT '0' COMMENT 'Cuota de recargo de equivalencia del Detalle de la Factura',
  `vat_deduction_type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de deduccion del IVA',
  `withholding_type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de retencion',
  `deductible_percent` double DEFAULT '0' COMMENT 'Porcentaje de deducibilidad',
  `deductible_quota` double DEFAULT '0' COMMENT 'Cuota deducible',
  PRIMARY KEY (`id`),
  KEY `IDX_INVOICE_TAX_INVOICE_DETAIL` (`invoice_detail`),
  KEY `IDX_INVOICE_TAX_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICE_TAX_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_TAX_INVOICE_DETAIL` FOREIGN KEY (`invoice_detail`) REFERENCES `invoice_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Impuestos del Detalle de la Factura';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invoice_tax_account`
--

DROP TABLE IF EXISTS `invoice_tax_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice_tax_account` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `invoice_tax` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Linea de Impuesto',
  `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY (`id`),
  KEY `IDX_INVOICE_TAX_ACCOUNT_ACCOUNT` (`account`),
  KEY `IDX_INVOICE_TAX_ACCOUNT_INVOICE_TAX` (`invoice_tax`),
  KEY `IDX_INVOICE_TAX_ACCOUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICE_TAX_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_INVOICE_TAX_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_TAX_ACCOUNT_INVOICE_TAX` FOREIGN KEY (`invoice_tax`) REFERENCES `invoice_tax` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables asociadas a Impuestos de Facturas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invoicing_group`
--

DROP TABLE IF EXISTS `invoicing_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoicing_group` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `customer` int(4) NOT NULL COMMENT 'Identificador del Cliente',
  `description` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Grupo de Facturacion',
  `customer_grouped` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Grupo de Facturacion agrupa los Clientes en una sola Factura',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_INVOICING_GROUP_DOMAIN` (`domain`),
  KEY `IDX_INVOICING_GROUP_CUSTOMER` (`customer`),
  CONSTRAINT `FK_INVOICING_GROUP_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_INVOICING_GROUP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Grupos de Facturacion';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `irpf_data`
--

DROP TABLE IF EXISTS `irpf_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `irpf_data` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int(4) NOT NULL COMMENT 'Identificador del contrato',
  `family_situation` tinyint(2) DEFAULT '0' COMMENT 'Situacion familiar',
  `spouse_document` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Documento del conyuge',
  `disability_level` tinyint(2) DEFAULT '0' COMMENT 'Grado de discapacidad',
  `dependence` tinyint(1) DEFAULT '0' COMMENT 'Dependencia de terceras personas',
  `moving_date` date DEFAULT NULL COMMENT 'Fecha de movilidad geografica',
  `labour_prolongation` tinyint(1) DEFAULT '0' COMMENT 'Prolongacion de la actividad laboral',
  `descendient_count` tinyint(2) DEFAULT NULL COMMENT 'Numero de hijos',
  `start_date` date DEFAULT NULL COMMENT 'Fecha inicio del modelo',
  `end_date` date DEFAULT NULL COMMENT 'Fecha fin del modelo',
  `fiscal_exclusion` tinyint(1) DEFAULT '0' COMMENT 'Exclusion a la obligacion de tributar',
  `issue_date` date NOT NULL COMMENT 'Fecha de emisión',
  `annual_remuneration` double(15,3) DEFAULT NULL COMMENT 'Retribuciones totales (dinerarias y en especie). Importe íntegro',
  `irregular_18_2_reduction` double(15,3) DEFAULT NULL COMMENT 'Reducciones por irregularidad ( Atr. 18.2 LIRPF)',
  `irregular_18_3_reduction` double(15,3) DEFAULT NULL COMMENT 'Reducciones por irregularidad ( Atr. 18.3: Disposiciones transitorias 11ª y 12 ª de la LIRPF)',
  `deduccibles_expenses` double(15,3) DEFAULT NULL COMMENT 'Gastos deducibles ( Atr 19.2, letras a, b y c de la LINRPF: Seguridad Social, Mutualidades ...)',
  `spousal_support` double(15,3) DEFAULT NULL COMMENT 'Pension compensatoria a favor del cónyuge. Importe fijado judicialmente',
  `food_annuity` double(15,3) DEFAULT NULL COMMENT 'Anualidades por alimentos en favor de los hijos. Importe fijado judicialmente',
  `deduct_home_loan` tinyint(2) DEFAULT NULL COMMENT 'Comunicación de pagos por la adquisión o rehabilitación de la vivienda habitual utilizando financiación ajena',
  `request_irpf` double(15,2) DEFAULT NULL COMMENT 'Tipo de retención solicitado',
  `contract_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Contrato o relación',
  `ceuta_melilla` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Los datos anteriores corresponden a rendimientos obtenidos en Ceuta o Melilla',
  PRIMARY KEY (`id`),
  KEY `IDX_IRPF_DATA_CONTRACT` (`contract`),
  KEY `IDX_IRPF_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_IRPF_DATA_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_IRPF_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Dator de irpf';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `irpf_data_ascendants`
--

DROP TABLE IF EXISTS `irpf_data_ascendants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `irpf_data_ascendants` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `irpf_data` int(4) NOT NULL COMMENT 'Identificador del irpf',
  `birth_year` int(4) DEFAULT NULL COMMENT 'Anio de nacimiento',
  `disability_level` tinyint(2) DEFAULT '0' COMMENT 'Grado de discapacidad',
  `dependence` tinyint(1) DEFAULT '0' COMMENT 'Dependencia de terceras personas',
  `another_descendient` tinyint(2) DEFAULT '0' COMMENT 'Convivencia con otros descendientes',
  PRIMARY KEY (`id`),
  KEY `IDX_IRPF_DATA_ASCENDANTS_IRPF_DATA` (`irpf_data`),
  KEY `IDX_IRPF_DATA_ASCENDANTS_DOMAIN` (`domain`),
  CONSTRAINT `FK_IRPF_DATA_ASCENDANTS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_IRPF_DATA_ASCENDANTS_IRPF_DATA` FOREIGN KEY (`irpf_data`) REFERENCES `irpf_data` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ascendientes del modelo 145';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `irpf_data_descendients`
--

DROP TABLE IF EXISTS `irpf_data_descendients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `irpf_data_descendients` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `irpf_data` int(4) NOT NULL COMMENT 'Identificador del irpf',
  `birth_year` int(4) DEFAULT NULL COMMENT 'Anio de nacimiento',
  `adoption_year` int(4) DEFAULT NULL COMMENT 'Anio de adopcion',
  `disability_level` tinyint(2) DEFAULT '0' COMMENT 'Grado de discapacidad',
  `dependence` tinyint(1) DEFAULT '0' COMMENT 'Dependencia de terceras personas',
  `unique_parent` tinyint(1) DEFAULT '0' COMMENT 'Computo por entero de hijos o descendientes',
  PRIMARY KEY (`id`),
  KEY `IDX_IRPF_DATA_DESCENDIENTS_IRPF_DATA` (`irpf_data`),
  KEY `IDX_IRPF_DATA_DESCENDIENTS_DOMAIN` (`domain`),
  CONSTRAINT `FK_IRPF_DATA_DESCENDIENTS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_IRPF_DATA_DESCENDIENTS_IRPF_DATA` FOREIGN KEY (`irpf_data`) REFERENCES `irpf_data` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Descendientes del modelo 145';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `irpf_regularization`
--

DROP TABLE IF EXISTS `irpf_regularization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `irpf_regularization` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int(4) NOT NULL COMMENT 'Identificador del contrato',
  `reason` tinyint(2) DEFAULT NULL COMMENT 'Causa de regularización',
  `effective_date` date NOT NULL COMMENT 'Fecha de entrada en vigor',
  `paid_irpf` double(15,3) DEFAULT NULL COMMENT 'Retenciones practicadas con anterioridad a la regularización.',
  `paid_remuneration` double(15,3) DEFAULT NULL COMMENT 'Retribuciones ya satisfechas con anterioridad a la regularización.',
  `prior_annual_irpf` double(15,3) DEFAULT NULL COMMENT 'Retenciones anuales anteriores a la regularización.',
  `prior_annual_remuneration` double(15,3) DEFAULT NULL COMMENT 'Retribucines anulaes consideradas con anterioridad a la regularización.',
  `prior_base_irpf` double(15,3) DEFAULT NULL COMMENT 'Base para calcular el tipo de retención determinado antes de la regularización.',
  `prior_irpf` double(15,2) DEFAULT NULL COMMENT 'Tipo de retención aplicado antes de la regularización.',
  `prior_in_ceuta_melilla` tinyint(1) DEFAULT NULL COMMENT 'Los rendimientos anteriores a la regularización fueron obtenidos en Ceuta o Melilla',
  `prior_minimun_personal_family` double(15,3) DEFAULT NULL COMMENT 'Mínimo personal y familiar para calcular el tipo de retención determinado antes de la regularización.',
  `prior_deduct_home_loan` tinyint(2) DEFAULT NULL COMMENT 'En algún momento antes de la regularización se aplico la minoración por pagos por la adquisión o rehabilitación de la vivienda',
  `prior_deduct_home_loan_amount` double(15,3) DEFAULT NULL COMMENT 'Importe de la minoración por pagos por la adquisión o rehabilitación de la vivienda antes de la regularización',
  PRIMARY KEY (`id`),
  KEY `IDX_IRPF_REGULARIZATION_CONTRACT` (`contract`),
  KEY `IDX_IRPF_REGULARIZATION_DOMAIN` (`domain`),
  CONSTRAINT `FK_IRPF_REGULARIZATION_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_IRPF_REGULARIZATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos regularizacion IRPF';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `irpf_result`
--

DROP TABLE IF EXISTS `irpf_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `irpf_result` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int(4) NOT NULL COMMENT 'Identificador del contrato',
  `effective_date` date NOT NULL COMMENT 'Fecha de entrada en vigor',
  `base_irpf` double(15,3) DEFAULT NULL COMMENT 'Base para calcular el tipo de retención',
  `minimun_personal_family` double(15,3) DEFAULT NULL COMMENT 'Mínimo personal y familiar para calcular el tipo de retención',
  `deduct_home_loan_amount` double(15,3) DEFAULT NULL COMMENT 'Minoración por pagos de préstamo para vivienda habitual',
  `deduct_80_bis` double(15,3) DEFAULT NULL COMMENT 'Deduccion Arttículo 80 bis LIRPF',
  `irpf` double(15,2) DEFAULT NULL COMMENT 'Tipo retención apliclabe ',
  `annual_irpf` double(15,3) DEFAULT NULL COMMENT 'Importe anual de las retenciones e ingresos a cuenta',
  `annual_remuneration` double(15,3) DEFAULT NULL COMMENT 'Retribuciones anuales. Importe íntegro',
  `irregular_18_2_reduction` double(15,3) DEFAULT NULL COMMENT 'Reducciones por irregularidad ( Art. 18.2 LIRPF). Importe',
  `irregular_18_3_reduction` double(15,3) DEFAULT NULL COMMENT 'Reducciones por irregularidad ( Art. 18.3: DD.TT 11ª y 12 ª de la LIRPF). Importe',
  `deduccibles_expenses` double(15,3) DEFAULT NULL COMMENT 'Gastos deducibles. Importe anual',
  `work_remuneration_reduction` double(15,3) DEFAULT NULL COMMENT 'Reducciones por rendimiento del trabajo ',
  `work_prolongation_reduction` double(15,3) DEFAULT NULL COMMENT 'Reducciones por prolongación de la actividad ',
  `work_moving_reduction` double(15,3) DEFAULT NULL COMMENT 'Reducciones por movilidad geografica ',
  `work_disability_reduction` double(15,3) DEFAULT NULL COMMENT 'Reducciones por discapacidad ',
  `social_security_pensioner` double(15,3) DEFAULT NULL COMMENT 'Por ser pensionista de la s. social/cl. Pasivas o desempleado',
  `two_or_more_descendents_min` double(15,3) DEFAULT NULL COMMENT 'Por tener más de dos descendientes con derecho a mínimo',
  `spousal_support` double(15,3) DEFAULT NULL COMMENT 'Pension compensatoria a favor del cónyuge. Importe anual',
  `food_annuity` double(15,3) DEFAULT NULL COMMENT 'Anualidades por alimentos en favor de los hijos. Importe anual',
  `minimun_personal` double(15,3) DEFAULT NULL COMMENT 'Mínimo personal',
  `minimun_ascendents` double(15,3) DEFAULT NULL COMMENT 'Mínimo por descendientes',
  `minimun_descendents` double(15,3) DEFAULT NULL COMMENT 'Mínimo por descendientes',
  `minimun_disability` double(15,3) DEFAULT NULL COMMENT 'Mínimo por discapacidad',
  `descendents_minor_3_total` tinyint(2) DEFAULT NULL COMMENT 'Descendientes computados menores de tres años. Total',
  `descendents_minor_3_entirely` tinyint(2) DEFAULT NULL COMMENT 'Descendientes computados menores de tres años. Por entero',
  `descendents_remainder_total` tinyint(2) DEFAULT NULL COMMENT 'Resto de descendientes computados . Total',
  `descendents_remainder_entirely` tinyint(2) DEFAULT NULL COMMENT 'Resto de descendientes computados . Por entero',
  `descendents_33_65_total` tinyint(2) DEFAULT NULL COMMENT 'Descendientes con discapacidad >= 33% y < 65%. Total',
  `descendents_33_65_entirely` tinyint(2) DEFAULT NULL COMMENT 'Descendientes con discapacidad >= 33% y < 65%. Por entero',
  `descendents_moving_total` tinyint(2) DEFAULT NULL COMMENT 'Descendientes con discapacidad, movilidad reducida. Total',
  `descendents_moving_entirely` tinyint(2) DEFAULT NULL COMMENT 'Descendientes con discapacidad, movilidad reducida. Por entero',
  `descendents_65_total` tinyint(2) DEFAULT NULL COMMENT 'Descendientes con discapacidad > 65%. Total',
  `descendents_65_entirely` tinyint(2) DEFAULT NULL COMMENT 'Descendientes con discapacidad > 65%. Por entero',
  `descendents_first` tinyint(2) DEFAULT NULL COMMENT 'Detalle del cómputo de descendientes. Hijo 1º',
  `descendents_second` tinyint(2) DEFAULT NULL COMMENT 'Detalle del cómputo de descendientes. Hijo 2º',
  `descendents_third` tinyint(2) DEFAULT NULL COMMENT 'Detalle del cómputo de descendientes. Hijo 3º',
  `descendents_fourth_subsequent_total` tinyint(2) DEFAULT NULL COMMENT 'Detalle del cómputo de descendientes. Hijo 4º y sucesivos. Total',
  `descendents_fourth_subsequent_entirely` tinyint(2) DEFAULT NULL COMMENT 'Detalle del cómputo de descendientes. Hijo 4º y sucesivos. Por entero',
  `ascendents_minor_75_total` tinyint(2) DEFAULT NULL COMMENT 'Ascendientes computados menores de 75 años. Total',
  `ascendents_minor_75_entirely` tinyint(2) DEFAULT NULL COMMENT 'Ascendientes computados menores de 75 años. Por entero',
  `ascendents_mayor_75_total` tinyint(2) DEFAULT NULL COMMENT 'Ascendientes computados mayores de 75 años. Total',
  `ascendents_mayor_75_entirely` tinyint(2) DEFAULT NULL COMMENT 'Ascendientes computados mayores de 75 años. Por entero',
  `ascendents_33_65_total` tinyint(2) DEFAULT NULL COMMENT 'Ascendientes con discapacidad >= 33% y < 65%. Total',
  `ascendents_33_65_entirely` tinyint(2) DEFAULT NULL COMMENT 'Ascendientes con discapacidad >= 33% y < 65%. Por entero',
  `ascendents_moving_total` tinyint(2) DEFAULT NULL COMMENT 'Ascendientes con discapacidad, movilidad reducida. Total',
  `ascendents_moving_entirely` tinyint(2) DEFAULT NULL COMMENT 'Ascendientes con discapacidad, movilidad reducida. Por entero',
  `ascendents_65_total` tinyint(2) DEFAULT NULL COMMENT 'Ascendientes con discapacidad > 65%. Total',
  `ascendents_65_entirely` tinyint(2) DEFAULT NULL COMMENT 'Ascendientes con discapacidad > 65%. Por entero',
  PRIMARY KEY (`id`),
  KEY `IDX_IRPF_RESULT_CONTRACT` (`contract`),
  KEY `IDX_IRPF_RESULT_DOMAIN` (`domain`),
  CONSTRAINT `FK_IRPF_RESULT_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_IRPF_RESULT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Resultados IRPF';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `item` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Articulo',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `product` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Producto',
  `detail` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Detalle del Articulo',
  `detail2` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Detalle 2 del Articulo',
  `detail3` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Detalle 3 del Articulo',
  `description` text COLLATE latin1_spanish_ci COMMENT 'Descripcion del Articulo',
  `serial_number` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de serie',
  `serial_date` date DEFAULT NULL COMMENT 'Fecha de serializacion',
  `price` double DEFAULT '0' COMMENT 'Precio del Articulo',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado del Articulo',
  `expenses_percent` double DEFAULT '0' COMMENT 'Gastos porcentuales del Articulo',
  `expenses_fixed` double DEFAULT '0' COMMENT 'Gastos fijos del Articulo',
  `profit_percent` double DEFAULT '0' COMMENT 'Porcentaje de beneficio del Articulo',
  `purchase_price` double DEFAULT '0' COMMENT 'Precio de compra del Articulo',
  `internet` tinyint(1) DEFAULT '0' COMMENT 'Visible en internet',
  `barcode` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de barras del Articulo',
  `pack_format_tag` int(4) DEFAULT NULL COMMENT 'Identificador de la Etiqueta de formato',
  `pack_units` int(4) DEFAULT '0' COMMENT 'Numero de unidades por formato',
  `pack_units_tag` int(4) DEFAULT NULL COMMENT 'Identificador de la Etiqueta de unidad de envase',
  `pack_measurement` double DEFAULT '0' COMMENT 'Medida envasada',
  `pack_measurement_tag` int(4) DEFAULT NULL COMMENT 'Identificador de la Etiqueta de unidad de medida',
  `stock_unit_tag` int(4) DEFAULT NULL COMMENT 'Identificador de la Etiqueta de unidad de Stock',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `item_addinfo`
--

DROP TABLE IF EXISTS `item_addinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `item_addinfo` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `product` int(4) NOT NULL COMMENT 'Identificador del Producto',
  `item` int(4) NOT NULL COMMENT 'Identificador de Articulo',
  `attribute` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Atributo adicional',
  `value` varchar(128) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Valor del atributo adicional',
  `value_date` date NOT NULL COMMENT 'Fecha del valor del atributo',
  PRIMARY KEY (`id`),
  KEY `IDX_ITEM_ADDINFO_DOMAIN` (`domain`),
  KEY `IDX_ITEM_ADDINFO_ITEM` (`item`),
  KEY `IDX_ITEM_ADDINFO_PRODUCT` (`product`),
  CONSTRAINT `FK_ITEM_ADDINFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ITEM_ADDINFO_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_ADDINFO_PRODUCT` FOREIGN KEY (`product`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Informacion adicional del Articulo';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `item_alternative`
--

DROP TABLE IF EXISTS `item_alternative`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `item_alternative` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `item` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de Articulo',
  `alternative_item` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de Articulo Alternativo',
  `priority` tinyint(2) DEFAULT '0' COMMENT 'Prioridad del Articulo Alternativo',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_ITEM_ALTERNATIVE` (`item`,`alternative_item`),
  KEY `IDX_ITEM_ALTERNATIVE_ITEM` (`item`),
  KEY `IDX_ITEM_ALTERNATIVE_ALTERNATIVE` (`alternative_item`),
  KEY `IDX_ITEM_ALTERNATIVE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ITEM_ALTERNATIVE_ALTERNATIVE` FOREIGN KEY (`alternative_item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_ALTERNATIVE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ITEM_ALTERNATIVE_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulo Alternativos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `item_composition`
--

DROP TABLE IF EXISTS `item_composition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `item_composition` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo compuesto',
  `composition_item` int(4) NOT NULL COMMENT 'Identificador del Articulo componente',
  `sequence` smallint(2) DEFAULT '0' COMMENT 'Numero de secuencia dentro de la Composicion',
  `description` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del componente',
  `quantity` double(15,3) DEFAULT '0.000' COMMENT 'Cantidad del componente',
  `discount_expr` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descuentos del componente',
  PRIMARY KEY (`id`),
  KEY `IDX_ITEM_COMPOSITION_ITEM` (`item`),
  KEY `IDX_ITEM_COMPOSITION_COMPOSITION` (`composition_item`),
  KEY `IDX_ITEM_COMPOSITION_DOMAIN` (`domain`),
  CONSTRAINT `FK_ITEM_COMPOSITION_COMPOSITION` FOREIGN KEY (`composition_item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_COMPOSITION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ITEM_COMPOSITION_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Composicion de Articulos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `item_tariff`
--

DROP TABLE IF EXISTS `item_tariff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `item_tariff` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `item` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de Articulo',
  `tariff` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de Tarifa',
  `type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de Tarifa',
  `profit_percent` double DEFAULT '0' COMMENT 'Porcentaje de beneficio',
  `price` double DEFAULT '0' COMMENT 'Precio de Venta',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_ITEM_TARIFF` (`item`,`tariff`),
  KEY `IDX_ITEM_TARIFF_TARIFF` (`tariff`),
  KEY `IDX_ITEM_TARIFF_ITEM` (`item`),
  KEY `IDX_ITEM_TARIFF_DOMAIN` (`domain`),
  CONSTRAINT `FK_ITEM_TARIFF_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ITEM_TARIFF_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_TARIFF_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas de Articulos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `item_warehouse`
--

DROP TABLE IF EXISTS `item_warehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `item_warehouse` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `item` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de Articulo',
  `warehouse` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de Almacen',
  `stock_max` double(15,3) DEFAULT '0.000' COMMENT 'Stock maximo del Articulo en el Almacen',
  `stock_min` double(15,3) DEFAULT '0.000' COMMENT 'Stock minimo del Articulo en el Almacen',
  `location` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Localizacion del Articulo en el Almacen',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_ITEM_WAREHOUSE` (`item`,`warehouse`),
  KEY `IDX_ITEM_WAREHOUSE_ITEM` (`item`),
  KEY `IDX_ITEM_WAREHOUSE_WAREHOUSE` (`warehouse`),
  KEY `IDX_ITEM_WAREHOUSE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ITEM_WAREHOUSE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ITEM_WAREHOUSE_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_WAREHOUSE_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos del Articulo por Almacen';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `job_type`
--

DROP TABLE IF EXISTS `job_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `job_type` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Tipo de Trabajo',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Trabajo',
  PRIMARY KEY (`id`),
  KEY `IDX_JOB_TYPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_JOB_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Trabajos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `leave_batch`
--

DROP TABLE IF EXISTS `leave_batch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `leave_batch` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `date` datetime DEFAULT NULL COMMENT 'Fecha de la Remesa',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Indica el estado de la remesa',
  `communication_id` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Identificador resultante de la comunicacion',
  `income_file` mediumblob COMMENT 'Archivo respuesta en binario',
  `income_file_date` datetime DEFAULT NULL COMMENT 'Fecha de respuesta',
  `outcome_file` mediumblob COMMENT 'Archivo Adjunto en binario',
  `outcome_file_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  PRIMARY KEY (`id`),
  KEY `IDX_LEAVE_BATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_LEAVE_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas de partes IT';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `leave_batch_detail`
--

DROP TABLE IF EXISTS `leave_batch_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `leave_batch_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `leave_batch` int(4) NOT NULL COMMENT 'Identificador unico de la remesa',
  `contract_leave_detail` int(4) NOT NULL COMMENT 'Identificador unico del parte',
  PRIMARY KEY (`id`),
  KEY `IDX_LEAVE_BATCH_DETAIL_LEAVE_BATCH` (`leave_batch`),
  KEY `IDX_LEAVE_BATCH_DETAIL_CONTRACT_LEAVE_DETAIL` (`contract_leave_detail`),
  KEY `IDX_LEAVE_BATCH_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_LEAVE_BATCH_DETAIL_CONTRACT_LEAVE_DETAIL` FOREIGN KEY (`contract_leave_detail`) REFERENCES `contract_leave_detail` (`id`),
  CONSTRAINT `FK_LEAVE_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_LEAVE_BATCH_DETAIL_LEAVE_BATCH` FOREIGN KEY (`leave_batch`) REFERENCES `leave_batch` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de remesas de partes IT';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `loan`
--

DROP TABLE IF EXISTS `loan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `loan` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador Unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Prestamo',
  `loan_date` date NOT NULL COMMENT 'Fecha de Concesion del Prestamo',
  `term` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Plazo',
  `interest` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Interes',
  `review` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Revision',
  `amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe',
  `expenses` double(15,3) DEFAULT '0.000' COMMENT 'Gastos asociados al Prestamo',
  `rbank` int(4) NOT NULL COMMENT 'Banco por el que se paga el Prestamo',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de Seguridad',
  `fee_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe de la cuota',
  `recurrence` int(4) DEFAULT '0' COMMENT 'Periodicidad',
  `pay_day` int(4) DEFAULT '1' COMMENT 'Dia de Pago',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado',
  `account` int(4) DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY (`id`),
  KEY `IDX_LOAN_RBANK` (`rbank`),
  KEY `IDX_LOAN_DOMAIN` (`domain`),
  KEY `IDX_LOAN_ACCOUNT` (`account`),
  CONSTRAINT `FK_LOAN_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_LOAN_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_LOAN_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Prestamos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mail_account`
--

DROP TABLE IF EXISTS `mail_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mail_account` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Cuenta de Correo',
  `email` varchar(256) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Cuenta de correo',
  `replyto_mail` varchar(256) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Email de Respuesta',
  `incoming_host` varchar(256) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Host del correo entrante',
  `protocol` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Protocolo utilizado (IMAP)',
  `incoming_port` int(4) DEFAULT NULL COMMENT 'Puerto del correo entrante',
  `incoming_security` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Seguridad de conexin del correo entrante',
  `outgoing_verification` tinyint(1) DEFAULT '1' COMMENT 'Indica si hay autentificacion en el correo saliente',
  `outgoing_host` varchar(256) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Host del servidor de correo saliente',
  `outgoing_port` int(4) DEFAULT NULL COMMENT 'Puerto del servidor de correo saliente',
  `outgoing_security` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Seguridad de conexin del correo saliente',
  `mail_username` varchar(256) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del usuario',
  `password` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Clave del usuario',
  `default_account` tinyint(1) DEFAULT '0' COMMENT 'Indica si es la cuenta de correo por defecto',
  `draft_folder` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Ruta de Borrador',
  `sent_folder` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Ruta de Enviados',
  `trash_folder` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Ruta de Papelera',
  `spam_folder` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Ruta de Spam',
  `display_name` varchar(256) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mostrar como',
  `signature` int(4) DEFAULT NULL COMMENT 'Identificador de la Firma',
  `user_id` int(4) DEFAULT NULL COMMENT 'Identificador del Usuario',
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de la Cuenta de Correo',
  PRIMARY KEY (`id`),
  KEY `IDX_MAIL_ACCOUNT_SIGNATURE` (`signature`),
  KEY `IDX_MAIL_ACCOUNT_DOMAIN` (`domain`),
  KEY `IDX_MAIL_ACCOUNT_USER` (`user_id`),
  CONSTRAINT `FK_MAIL_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_MAIL_ACCOUNT_SIGNATURE` FOREIGN KEY (`signature`) REFERENCES `signature` (`id`),
  CONSTRAINT `FK_MAIL_ACCOUNT_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas de Correo Electronico';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `make`
--

DROP TABLE IF EXISTS `make`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `make` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Fabricante',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Fabricante',
  PRIMARY KEY (`id`),
  KEY `IDX_MAKE_DOMAIN` (`domain`),
  CONSTRAINT `FK_MAKE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Fabricantes';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mark`
--

DROP TABLE IF EXISTS `mark`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mark` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `subject` int(4) NOT NULL COMMENT 'Identificador de Asignatura',
  `alumn` int(4) NOT NULL COMMENT 'Identificador de Alumno',
  `evaluation` tinyint(2) NOT NULL COMMENT 'Numero de evaluacion',
  `mark` double(15,3) DEFAULT '0.000' COMMENT 'Nota',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_MARK_SUBJECT_ALUMN_EVALUATION` (`subject`,`alumn`,`evaluation`),
  KEY `IDX_MARK_ALUMN` (`alumn`),
  KEY `IDX_MARK_SUBJECT` (`subject`),
  KEY `IDX_MARK_DOMAIN` (`domain`),
  CONSTRAINT `FK_MARK_ALUMN` FOREIGN KEY (`alumn`) REFERENCES `course_alumn` (`id`),
  CONSTRAINT `FK_MARK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_MARK_SUBJECT` FOREIGN KEY (`subject`) REFERENCES `course_academicskill` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Notas de Alumnos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mk_action`
--

DROP TABLE IF EXISTS `mk_action`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mk_action` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `campaign` int(11) NOT NULL COMMENT 'Identificador de la Campaña',
  `media_type` int(4) NOT NULL COMMENT 'Tipo de contacto de la Accion',
  `start_date` datetime NOT NULL COMMENT 'Fecha de inicio',
  `end_date` datetime DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `survey` int(4) DEFAULT NULL COMMENT 'Identificador del Cuestionario',
  `newsletter` int(4) DEFAULT NULL COMMENT 'Identificador del Boletin',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de la Accion',
  `news` int(4) DEFAULT NULL COMMENT 'Identificador de la Noticia',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Acciones de Marketing';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mk_action_target`
--

DROP TABLE IF EXISTS `mk_action_target`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mk_action_target` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `action` int(4) NOT NULL COMMENT 'Identificador de la Accion',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `status` tinyint(2) NOT NULL COMMENT 'Estado del Cliente Potencial de la Accion de Campaña',
  `survey_response` int(4) DEFAULT NULL COMMENT 'Identificador de la Respuesta de Cuestionario',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  `user` int(4) DEFAULT NULL COMMENT 'Identificador del Usuario',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Clientes Potenciales de la Accion de Marketing';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mk_campaign`
--

DROP TABLE IF EXISTS `mk_campaign`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mk_campaign` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Campaña esta activa o no',
  `description` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Campaña',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  PRIMARY KEY (`id`),
  KEY `IDX_MK_CAMPAIGN_DOMAIN` (`domain`),
  KEY `IDX_MK_CAMPAIGN_SCOPE` (`scope`),
  CONSTRAINT `FK_MK_CAMPAIGN_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_MK_CAMPAIGN_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Campañas de Marketing';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mk_template`
--

DROP TABLE IF EXISTS `mk_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mk_template` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Plantilla',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Plantilla esta activa o no',
  `creationDate` datetime NOT NULL COMMENT 'Fecha de la creacion en el sistema de la Plantilla',
  `subject` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Asunto de la Plantilla',
  `width` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Ancho de la Plantilla',
  `title_color` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Color del titulo de la Plantilla',
  `background_color` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Color de fondo de la Plantilla',
  `header_template` int(4) DEFAULT NULL COMMENT 'Identificador de la Plantilla de Cabecera',
  `footer_template` int(4) DEFAULT NULL COMMENT 'Identificador de la Plantilla de Pie de Pagina',
  PRIMARY KEY (`id`),
  KEY `IDX_MK_TEMPLATE_DOMAIN` (`domain`),
  KEY `IDX_MK_TEMPLATE_SCOPE` (`scope`),
  KEY `IDX_MK_TEMPLATE_HEADER_TEMPLATE` (`header_template`),
  KEY `IDX_MK_TEMPLATE_FOOTER_TEMPLATE` (`footer_template`),
  CONSTRAINT `FK_MK_TEMPLATE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_MK_TEMPLATE_FOOTER_TEMPLATE` FOREIGN KEY (`footer_template`) REFERENCES `rattach` (`id`),
  CONSTRAINT `FK_MK_TEMPLATE_HEADER_TEMPLATE` FOREIGN KEY (`header_template`) REFERENCES `rattach` (`id`),
  CONSTRAINT `FK_MK_TEMPLATE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Plantilla de Marketing';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `model`
--

DROP TABLE IF EXISTS `model`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `model` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Modelo',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `make` int(4) NOT NULL COMMENT 'Identificador del Fabricante',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Modelo',
  PRIMARY KEY (`id`),
  KEY `IDX_MODEL_MAKE` (`make`),
  KEY `IDX_MODEL_DOMAIN` (`domain`),
  CONSTRAINT `FK_MODEL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_MODEL_MAKE` FOREIGN KEY (`make`) REFERENCES `make` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Modelos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `news`
--

DROP TABLE IF EXISTS `news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `news` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `title` varchar(128) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Titulo de la Noticia',
  `description` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de la Noticia',
  `content` text COLLATE latin1_spanish_ci NOT NULL COMMENT 'Contenido de la Noticia',
  `url` varchar(256) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Url de la Noticia',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si la Noticia esta activa o no',
  `rss` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si la Noticia se va a publicar en rss o no',
  `init_date` datetime DEFAULT NULL COMMENT 'Fecha Noticia',
  `end_date` datetime DEFAULT NULL COMMENT 'Fecha fin Noticia',
  `category` int(4) DEFAULT NULL COMMENT 'Categoria de la Noticia',
  `rattach` int(4) DEFAULT NULL COMMENT 'Identificador del Archivo Adjunto',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de Contenido',
  `template` int(4) DEFAULT NULL COMMENT 'Identificador de la Plantilla de Marketing',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Noticias';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `newsletter`
--

DROP TABLE IF EXISTS `newsletter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `newsletter` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `name` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Boletin',
  `date` datetime NOT NULL COMMENT 'Fecha del Boletin',
  `layout` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Disposicion del Boletin',
  `active` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Boletin esta activo o no',
  `subject` varchar(128) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Asunto del Boletin',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `highlightFirst` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Boletin destaca la primera noticia o no',
  `template` int(4) DEFAULT NULL COMMENT 'Identificador de la Plantilla de Marketing',
  `newsSeparator` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Boletin incluye un separador entre noticias',
  PRIMARY KEY (`id`),
  KEY `IDX_NEWSLETTER_DOMAIN` (`domain`),
  KEY `IDX_NEWSLETTER_SCOPE` (`scope`),
  KEY `IDX_NEWSLETTER_MK_TEMPLATE` (`template`),
  CONSTRAINT `FK_NEWSLETTER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_NEWSLETTER_MK_TEMPLATE` FOREIGN KEY (`template`) REFERENCES `mk_template` (`id`),
  CONSTRAINT `FK_NEWSLETTER_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Boletin';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `newsletter_detail`
--

DROP TABLE IF EXISTS `newsletter_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `newsletter_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `news` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Noticia',
  `newsletter` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Boletin',
  `position` int(4) NOT NULL COMMENT 'Posicion dentro del Boletin',
  PRIMARY KEY (`id`),
  KEY `IDX_NEWSLETTER_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_NEWSLETTER_DETAIL_NEWS` (`news`),
  KEY `IDX_NEWSLETTER_DETAIL_NEWSLETTER` (`newsletter`),
  CONSTRAINT `FK_NEWSLETTER_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_NEWSLETTER_DETAIL_NEWS` FOREIGN KEY (`news`) REFERENCES `news` (`id`),
  CONSTRAINT `FK_NEWSLETTER_DETAIL_NEWSLETTER` FOREIGN KEY (`newsletter`) REFERENCES `newsletter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Noticias del Boletin';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `note`
--

DROP TABLE IF EXISTS `note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `note` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Nota',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `subject` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion corta de la Nota',
  `date` datetime NOT NULL COMMENT 'Fecha de la Nota',
  `owner` int(4) DEFAULT NULL COMMENT 'Destinatario de la Nota',
  `note` text COLLATE latin1_spanish_ci COMMENT 'Texto de la Nota',
  PRIMARY KEY (`id`),
  KEY `IDX_NOTE_USER` (`owner`),
  KEY `IDX_NOTE_DOMAIN` (`domain`),
  CONSTRAINT `FK_NOTE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_NOTE_USER` FOREIGN KEY (`owner`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Notas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notice`
--

DROP TABLE IF EXISTS `notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notice` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Aviso',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `date` datetime NOT NULL COMMENT 'Fecha y hora en la que se produjo el Aviso',
  `sender` int(4) NOT NULL COMMENT 'Remitente del Aviso',
  `work_group` int(4) DEFAULT NULL COMMENT 'Grupo de Trabajo al que va dirigida el Aviso',
  `recipient` int(4) DEFAULT NULL COMMENT 'Destinatario del Aviso',
  `source` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Origen del Aviso',
  `company` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Empresa para la que trabaja el origen del Aviso',
  `phone` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono para contactar con el origen del Aviso',
  `subject` text COLLATE latin1_spanish_ci COMMENT 'Asunto del Aviso',
  `status` tinyint(2) NOT NULL COMMENT 'Estado del Aviso',
  `type` tinyint(2) NOT NULL COMMENT 'Tipo de Aviso',
  `priority` tinyint(2) NOT NULL COMMENT 'Prioridad del Aviso',
  `notice` int(4) DEFAULT NULL COMMENT 'Aviso al que referencia',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notice_tag`
--

DROP TABLE IF EXISTS `notice_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notice_tag` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `notice` int(4) NOT NULL COMMENT 'Identificador del Aviso',
  `tag` int(4) NOT NULL COMMENT 'Identificador de la Etiqueta',
  `start_date` datetime NOT NULL COMMENT 'Fecha y hora de la apertura del Aviso',
  `end_date` datetime DEFAULT NULL COMMENT 'Fecha y hora de cierre del Aviso',
  `user` int(4) DEFAULT NULL COMMENT 'Identificador del Usuario',
  PRIMARY KEY (`id`),
  KEY `IDX_NOTICE_TAG_NOTICE` (`notice`),
  KEY `IDX_NOTICE_TAG_TAG` (`tag`),
  KEY `IDX_NOTICE_TAG_USER` (`user`),
  CONSTRAINT `FK_NOTICE_TAG_NOTICE` FOREIGN KEY (`notice`) REFERENCES `notice` (`id`),
  CONSTRAINT `FK_NOTICE_TAG_TAG` FOREIGN KEY (`tag`) REFERENCES `tag` (`id`),
  CONSTRAINT `FK_NOTICE_TAG_USER` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Avisos y Etiquetas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `observation`
--

DROP TABLE IF EXISTS `observation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `observation` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Observacion',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `description` varchar(256) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Observacion',
  PRIMARY KEY (`id`),
  KEY `IDX_OBSERVATION_DOMAIN` (`domain`),
  CONSTRAINT `FK_OBSERVATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Observaciones';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `offer`
--

DROP TABLE IF EXISTS `offer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `offer` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Presupuesto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project` int(4) DEFAULT NULL COMMENT 'Identificador del Proyecto',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `series` char(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie del Presupuesto',
  `number` int(4) NOT NULL COMMENT 'Numero del Presupuesto',
  `version` smallint(2) NOT NULL DEFAULT '0' COMMENT 'Numero de version de Presupuesto',
  `address` int(4) DEFAULT NULL COMMENT 'Identificador de la Direccion de envio del Presupuesto',
  `seller` int(4) DEFAULT NULL COMMENT 'Agente Comercial del Presupuesto',
  `supplier` int(4) DEFAULT NULL COMMENT 'Identificador del Proveedor',
  `discount_expr` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descuentos del Presupuesto',
  `issue_date` date DEFAULT NULL COMMENT 'Fecha de emision del Presupuesto',
  `pay_method` int(4) DEFAULT NULL COMMENT 'Forma de Pago del Presupuesto',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad del Presupuesto',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado del Presupuesto',
  `type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de Presupuesto',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `scope` int(4) NOT NULL DEFAULT '1' COMMENT 'Ambito del Presupuesto',
  `number_of_pymnts` smallint(2) DEFAULT '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint(2) DEFAULT '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint(2) DEFAULT '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Dias de pago',
  `bank_account` varchar(34) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'IBAN - Numero de Cuenta Bancaria Internacional',
  `bank_alias` varchar(25) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias del Banco',
  `bic` varchar(11) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'BIC - Codigo Identificador del Banco',
  `signed` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Presupuesto esta firmada electronicamente',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios del Presupuesto',
  `remarks` text COLLATE latin1_spanish_ci COMMENT 'Observaciones del Presupuesto',
  `external_reference` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Referencia externa',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Presupuestos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `offer_attach`
--

DROP TABLE IF EXISTS `offer_attach`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `offer_attach` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `offer` int(4) NOT NULL COMMENT 'Identificador del Presupuesto',
  `mimeType` tinyint(2) DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `driveId` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_OFFER_ATTACH_OFFER` (`offer`),
  KEY `IDX_OFFER_ATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_OFFER_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_OFFER_ATTACH_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Presupuestos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `offer_detail`
--

DROP TABLE IF EXISTS `offer_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `offer_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Detalle de Presupuesto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `offer` int(4) NOT NULL COMMENT 'Identificador del Presupuesto',
  `line` smallint(2) DEFAULT '1' COMMENT 'Numero de linea del Detalle dentro del Presupuesto',
  `item` int(4) DEFAULT NULL COMMENT 'Identificador del Articulo',
  `description` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripción del Articulo',
  `quantity` double(15,3) DEFAULT '0.000' COMMENT 'Cantidad del Articulo',
  `price` double DEFAULT '0' COMMENT 'Precio del Articulo',
  `discount_expr` varchar(32) COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Descuentos del Articulo',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado del Detalle del Presupuesto',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_OFFER_DETAIL_OFFER` (`offer`),
  KEY `IDX_OFFER_DETAIL_ITEM` (`item`),
  KEY `IDX_OFFER_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_OFFER_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_OFFER_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_OFFER_DETAIL_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Presupuesto';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `offer_detail_commission`
--

DROP TABLE IF EXISTS `offer_detail_commission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `offer_detail_commission` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `offer_detail` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Linea de Presupuesto',
  `commission` double DEFAULT '0' COMMENT 'Porcentaje de Comision',
  `amount` double DEFAULT '0' COMMENT 'Importe de la Comision',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado de la Comision',
  `pay_date` date DEFAULT NULL COMMENT 'Fecha de liquidacion',
  PRIMARY KEY (`id`),
  KEY `IDX_OFFER_DETAIL_COMMISSION_OFFER_DETAIL` (`offer_detail`),
  KEY `IDX_OFFER_DETAIL_COMMISSION_DOMAIN` (`domain`),
  CONSTRAINT `FK_OFFER_DETAIL_COMMISSION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_OFFER_DETAIL_COMMISSION_OFFER_DETAIL` FOREIGN KEY (`offer_detail`) REFERENCES `offer_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones asociadas a Lineas de Presupuestos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `offer_term`
--

DROP TABLE IF EXISTS `offer_term`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `offer_term` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `offer` int(4) NOT NULL COMMENT 'Identificador del Presupuesto',
  `line` smallint(2) DEFAULT '1' COMMENT 'Numero de linea de la Condicion del Presupuesto',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Nombre de la Condicion Comercial',
  `description` text COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Condicion Comercial',
  `term_general` tinyint(1) DEFAULT '0' COMMENT 'Indica si la Condicion es particular o general',
  PRIMARY KEY (`id`),
  KEY `IDX_OFFER_TERM_OFFER` (`offer`),
  KEY `IDX_OFFER_TERM_DOMAIN` (`domain`),
  CONSTRAINT `FK_OFFER_TERM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_OFFER_TERM_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Condiciones del Presupuesto';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pay_method`
--

DROP TABLE IF EXISTS `pay_method`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pay_method` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Forma de Pago',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de Forma de Pago',
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de Forma de Pago',
  PRIMARY KEY (`id`),
  KEY `IDX_PAY_METHOD_DOMAIN` (`domain`),
  CONSTRAINT `FK_PAY_METHOD_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17427 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Formas de Pago';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `payment_concept`
--

DROP TABLE IF EXISTS `payment_concept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `payment_concept` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `code` varchar(25) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo',
  `description` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Percepcion Salarial',
  `description_decorable` tinyint(2) NOT NULL DEFAULT '0',
  `expression` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe',
  `irpf_expression` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe tributable',
  `quote_expression` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe cotizable',
  PRIMARY KEY (`id`),
  KEY `IDX_PAYMENT_CONCEPT_DOMAIN` (`domain`),
  CONSTRAINT `FK_PAYMENT_CONCEPT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6283 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos de devengos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `payroll_batch_attach`
--

DROP TABLE IF EXISTS `payroll_batch_attach`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `payroll_batch_attach` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `source_batch` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Remesa',
  `source_type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de la Remesa',
  `mimeType` tinyint(2) DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Archivo Adjunto',
  `scope` int(4) DEFAULT NULL COMMENT 'Ambito del Archivo Adjunto',
  `attach_date` date DEFAULT NULL COMMENT 'Fecha del Archivo Adjunto',
  `driveId` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_PAYROLL_BATCH_ATTACH_DOMAIN` (`domain`),
  KEY `IDX_PAYROLL_BATCH_ATTACH_SCOPE` (`scope`),
  CONSTRAINT `FK_PAYROLL_BATCH_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PAYROLL_BATCH_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Remesas de Laboral';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `payroll_workplace`
--

DROP TABLE IF EXISTS `payroll_workplace`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `payroll_workplace` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `agreement` int(4) DEFAULT NULL COMMENT 'Identificador del Convenio',
  `enterprise_activity` int(4) DEFAULT NULL COMMENT 'Identificador de la Actividad',
  `calendar` int(4) DEFAULT NULL COMMENT 'Identificador del Calendario',
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
) ENGINE=InnoDB AUTO_INCREMENT=3586 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos laborales del Centro de Trabajo';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pcategory`
--

DROP TABLE IF EXISTS `pcategory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pcategory` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Categoria',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Categoria',
  `detail` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del detalle de los Articulos',
  `detail2` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del detalle 2 de los Articulos',
  `detail3` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del detalle 3 de los Articulos',
  PRIMARY KEY (`id`),
  KEY `IDX_PCATEGORY_DOMAIN` (`domain`),
  CONSTRAINT `FK_PCATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10949 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias de Productos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `person`
--

DROP TABLE IF EXISTS `person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person` (
  `registry` int(4) NOT NULL DEFAULT '0' COMMENT 'Registro de la Persona',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `birth_date` date DEFAULT NULL COMMENT 'Fecha de nacimiento de la Persona',
  `gender` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Sexo de la Persona',
  `marital_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Estado civil de la Persona',
  `social_security_num` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Seguridad Social de la Persona',
  `name` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `first_surname` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Primer Apellido ',
  `second_surname` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Segundo Apellido',
  PRIMARY KEY (`registry`),
  KEY `IDX_PERSON_DOMAIN` (`domain`),
  CONSTRAINT `FK_PERSON_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PERSON_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Personas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pm_type_detail`
--

DROP TABLE IF EXISTS `pm_type_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pm_type_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de Forma de Pago',
  `description` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del detalle',
  `account` int(4) DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY (`id`),
  KEY `IDX_PM_TYPE_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_PM_TYPE_DETAIL_ACCOUNT` (`account`),
  CONSTRAINT `FK_PM_TYPE_DETAIL_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_PM_TYPE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles por Tipo de Forma de Pago';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pos`
--

DROP TABLE IF EXISTS `pos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `department` int(4) DEFAULT NULL COMMENT 'Identificador del Departamento',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `name` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre',
  `series` char(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie del POS',
  `customer` int(4) DEFAULT NULL COMMENT 'Identificador del Cliente',
  `price_editable` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el precio es editable',
  `discount_editable` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el descuento es editable',
  `invoiceable` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el Punto de Venta es facturable',
  `item_invoice` int(4) DEFAULT NULL COMMENT 'Identificador del Articulo facturable',
  `display_mode` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Modo de visualizacion en pantalla',
  `num_rows` int(4) DEFAULT '0' COMMENT 'Numero de filas en la vista tipo Hosteleria',
  `num_cols` int(4) DEFAULT '0' COMMENT 'Numero de columnas en la vista tipo Hosteleria',
  `pin_pad` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indicador de si es un Pin Pad',
  `commerce` varchar(20) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Clave de firma del comercio',
  `signature_password` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Clave de firma del comercio',
  `terminal` varchar(4) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de terminal',
  `port_configuration` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Configuracion de puerto',
  `pos_version` varchar(8) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Version actual',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='TPV';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pos_catalogue`
--

DROP TABLE IF EXISTS `pos_catalogue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_catalogue` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `pos` int(4) NOT NULL COMMENT 'Identificador del TPV',
  `catalogue` int(4) NOT NULL COMMENT 'Identificador del Catalogo',
  PRIMARY KEY (`id`),
  KEY `IDX_POS_CATALOGUE_CATALOGUE` (`catalogue`),
  KEY `IDX_POS_CATALOGUE_DOMAIN` (`domain`),
  KEY `IDX_POS_CATALOGUE_POS` (`pos`),
  CONSTRAINT `FK_POS_CATALOGUE_CATALOGUE` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`),
  CONSTRAINT `FK_POS_CATALOGUE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_POS_CATALOGUE_POS` FOREIGN KEY (`pos`) REFERENCES `pos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Catalogos asociados al TPV';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pos_shift`
--

DROP TABLE IF EXISTS `pos_shift`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_shift` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `pos` int(4) NOT NULL COMMENT 'Identificador del TPV',
  `shift` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Turno de trabajo',
  `username` varchar(16) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Usuario del Turno',
  `start_time` datetime NOT NULL COMMENT 'Fecha-hora de apertura',
  `end_time` datetime DEFAULT NULL COMMENT 'Fecha-hora de cierre',
  `initial_amount` double(15,2) DEFAULT '0.00' COMMENT 'Efectivo inicial',
  `imbalance` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si existen descuadres en el Turno',
  `remarks` text COLLATE latin1_spanish_ci COMMENT 'Observaciones del turno',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_POS_SHIFT_POS` (`pos`),
  KEY `IDX_POS_SHIFT_DOMAIN` (`domain`),
  CONSTRAINT `FK_POS_SHIFT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_POS_SHIFT_POS` FOREIGN KEY (`pos`) REFERENCES `pos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Turno de trabajo del TPV';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pos_shift_count`
--

DROP TABLE IF EXISTS `pos_shift_count`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_shift_count` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `pos_shift` int(4) NOT NULL COMMENT 'Identificador del Turno de trabajo',
  `pay_method` int(4) DEFAULT NULL COMMENT 'Identificador de la Forma de pago',
  `amount` double(15,2) DEFAULT '0.00' COMMENT 'Total efectivo',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_POS_SHIFT_COUNT_POS_SHIFT` (`pos_shift`),
  KEY `IDX_POS_SHIFT_COUNT_PAY_METHOD` (`pay_method`),
  KEY `IDX_POS_SHIFT_COUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_POS_SHIFT_COUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_POS_SHIFT_COUNT_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_POS_SHIFT_COUNT_POS_SHIFT` FOREIGN KEY (`pos_shift`) REFERENCES `pos_shift` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Arqueo del TPV';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `prepayment`
--

DROP TABLE IF EXISTS `prepayment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `prepayment` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `creditor` int(4) NOT NULL COMMENT 'Identificador del Acreedor',
  `customer` int(4) NOT NULL COMMENT 'Identificador del Cliente',
  `finance` int(4) NOT NULL COMMENT 'Identificador del Vencimiento',
  `collect` tinyint(2) DEFAULT '0' COMMENT 'Localizacion del cobro del Suplido',
  `collect_id` int(4) DEFAULT NULL COMMENT 'Identificador del cobro del Suplido',
  PRIMARY KEY (`id`),
  KEY `IDX_PREPAYMENT_DOMAIN` (`domain`),
  KEY `IDX_PREPAYMENT_CREDITOR` (`creditor`),
  KEY `IDX_PREPAYMENT_CUSTOMER` (`customer`),
  KEY `IDX_PREPAYMENT_FINANCE` (`finance`),
  CONSTRAINT `FK_PREPAYMENT_CREDITOR` FOREIGN KEY (`creditor`) REFERENCES `creditor` (`registry`),
  CONSTRAINT `FK_PREPAYMENT_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_PREPAYMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PREPAYMENT_FINANCE` FOREIGN KEY (`finance`) REFERENCES `finance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Suplidos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `process`
--

DROP TABLE IF EXISTS `process`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `process` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Proceso',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(30) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Proceso.',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Activo si o no',
  PRIMARY KEY (`id`),
  KEY `IDX_PROCESS_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROCESS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Procesos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `process_detail`
--

DROP TABLE IF EXISTS `process_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `process_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Detalle de Proceso',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `process` int(4) NOT NULL COMMENT 'Identificador del Proceso',
  `description` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Detalle de Proceso',
  `position` int(4) NOT NULL COMMENT 'Orden de ejecucion del Detalle dentro del Proceso',
  `date_reference` tinyint(2) DEFAULT NULL COMMENT 'Referencia para el calculo de la fecha de vencimiento de la Tarea',
  `days` int(4) DEFAULT NULL COMMENT 'Numero de dias asociado a la referencia para el calculo de la fecha de vencimiento de la Tarea',
  `alert_days` int(4) DEFAULT NULL COMMENT 'Numero de dias, previos a la fecha de vencimiento de la Tarea, para el calculo de la fecha de generacion de la Alarma',
  `workgroup` int(4) DEFAULT NULL COMMENT 'Identificador del Grupo de Trabajo',
  `priority` tinyint(2) DEFAULT '0' COMMENT 'Prioridad de la Tarea',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Activo si o no',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios del Detalle de Proceso',
  PRIMARY KEY (`id`),
  KEY `IDX_PROCESS_DETAIL_PROCESS` (`process`),
  KEY `IDX_PROCESS_DETAIL_WORKGROUP` (`workgroup`),
  KEY `IDX_PROCESS_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROCESS_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROCESS_DETAIL_PROCESS` FOREIGN KEY (`process`) REFERENCES `process` (`id`),
  CONSTRAINT `FK_PROCESS_DETAIL_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Procesos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `process_detail_transition`
--

DROP TABLE IF EXISTS `process_detail_transition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `process_detail_transition` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `process_detail` int(11) NOT NULL COMMENT 'Identificador del Detalle del Proceso.',
  `process_transition_type` int(11) NOT NULL COMMENT 'Identificador del Tipo de Transicion.',
  `next_process_detail` int(11) NOT NULL COMMENT 'Identificador del siguiente Detalle del Proceso.',
  PRIMARY KEY (`id`),
  KEY `IDX_PROCESS_DETAIL_TRANSITION_PROCESS_DETAIL` (`process_detail`),
  KEY `IDX_PROCESS_DETAIL_TRANSITION_NEXT_PROCESS_DETAIL` (`next_process_detail`),
  KEY `IDX_PROCESS_DETAIL_TRANSITION_PROCESS_TRANSITION_TYPE` (`process_transition_type`),
  KEY `IDX_PROCESS_DETAIL_TRANSITION_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROCESS_DETAIL_TRANSITION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROCESS_DETAIL_TRANSITION_NEXT_PROCESS_DETAIL` FOREIGN KEY (`next_process_detail`) REFERENCES `process_detail` (`id`),
  CONSTRAINT `FK_PROCESS_DETAIL_TRANSITION_PROCESS_DETAIL` FOREIGN KEY (`process_detail`) REFERENCES `process_detail` (`id`),
  CONSTRAINT `FK_PROCESS_DETAIL_TRANSITION_PROCESS_TRANSITION_TYPE` FOREIGN KEY (`process_transition_type`) REFERENCES `process_transition_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Transiciones entre Detalles de Procesos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `process_task`
--

DROP TABLE IF EXISTS `process_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `process_task` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Relacion entre Campañas, Actividades y Tareas',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `campaign` int(4) DEFAULT NULL COMMENT 'Identificador de la Campaña',
  `process_detail` int(4) NOT NULL COMMENT 'Identificador del Detalle de Proceso',
  `task` int(4) NOT NULL COMMENT 'Identificador de la Tarea',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Campañas, Actividades y Tareas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `process_transition_type`
--

DROP TABLE IF EXISTS `process_transition_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `process_transition_type` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Tipo de Transicion',
  PRIMARY KEY (`id`),
  KEY `IDX_PROCESS_TRANSITION_TYPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROCESS_TRANSITION_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Transiciones entre Detalles de Procesos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Producto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Producto',
  `code` varchar(15) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo del Producto',
  `kind` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Clase de Producto',
  `brand` int(4) DEFAULT NULL COMMENT 'Marca Comercial del Producto',
  `category` int(4) DEFAULT NULL COMMENT 'Categoria del Producto',
  `inventoriable` tinyint(1) DEFAULT NULL COMMENT 'Indica si el Producto es inventariable',
  `serializable` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Producto es serializable',
  `lotable` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Producto es loteable',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado del Producto',
  `vat` int(4) DEFAULT NULL COMMENT 'IVA del Producto',
  `retention` int(4) DEFAULT NULL COMMENT 'Retencion del Producto',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Producto',
  `manufactured` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Producto es elaborado',
  `composition` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Producto es una Composicion',
  `composition_price` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Precio lo determina la Composicion',
  `packaged` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Producto es envasado',
  `sales_account` int(4) DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable de Ventas',
  `purchase_account` int(4) DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable de Compras',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Productos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product_tag`
--

DROP TABLE IF EXISTS `product_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_tag` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `product` int(4) NOT NULL COMMENT 'Identificador del Producto',
  `tag` int(4) NOT NULL COMMENT 'Identificador de la Etiqueta',
  PRIMARY KEY (`id`),
  KEY `IDX_PRODUCT_TAG_DOMAIN` (`domain`),
  KEY `IDX_PRODUCT_TAG_PRODUCT` (`product`),
  KEY `IDX_PRODUCT_TAG_TAG` (`tag`),
  CONSTRAINT `FK_PRODUCT_TAG_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PRODUCT_TAG_PRODUCT` FOREIGN KEY (`product`) REFERENCES `product` (`id`),
  CONSTRAINT `FK_PRODUCT_TAG_TAG` FOREIGN KEY (`tag`) REFERENCES `tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Productos y Etiquetas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `profile`
--

DROP TABLE IF EXISTS `profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profile` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Perfil',
  `application` int(4) NOT NULL COMMENT 'Identificador de la Aplicacion',
  `domain` int(4) DEFAULT NULL COMMENT 'Identificador del Dominio',
  PRIMARY KEY (`id`),
  KEY `IDX_PROFILE_APPLICATION` (`application`),
  KEY `IDX_PROFILE_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROFILE_APPLICATION` FOREIGN KEY (`application`) REFERENCES `application` (`id`),
  CONSTRAINT `FK_PROFILE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1266 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Perfiles';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `profile_action_denied`
--

DROP TABLE IF EXISTS `profile_action_denied`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profile_action_denied` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `profile` int(4) NOT NULL COMMENT 'Identificador del Perfil',
  `action_id` int(4) NOT NULL COMMENT 'Identificador de la Accion',
  PRIMARY KEY (`id`),
  KEY `IDX_PROFILE_ACTION_DENIED_DOMAIN` (`domain`),
  KEY `IDX_PROFILE_ACTION_DENIED_PROFILE` (`profile`),
  KEY `IDX_PROFILE_ACTION_DENIED_ACTION` (`action_id`),
  CONSTRAINT `FK_PROFILE_ACTION_DENIED_ACTION` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `FK_PROFILE_ACTION_DENIED_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROFILE_ACTION_DENIED_PROFILE` FOREIGN KEY (`profile`) REFERENCES `profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Acciones Inhabilitadas en el Perfil';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `profile_module_denied`
--

DROP TABLE IF EXISTS `profile_module_denied`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profile_module_denied` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) DEFAULT NULL COMMENT 'Identificador del Dominio',
  `profile` int(4) NOT NULL COMMENT 'Identificador del Perfil',
  `module` tinyint(2) NOT NULL COMMENT 'Modulo Inhabilitado para el Perfil',
  PRIMARY KEY (`id`),
  KEY `IDX_PROFILE_MODULE_DENIED_PROFILE` (`profile`),
  KEY `IDX_PROFILE_MODULE_DENIED_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROFILE_MODULE_DENIED_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROFILE_MODULE_DENIED_PROFILE` FOREIGN KEY (`profile`) REFERENCES `profile` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1613 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Modulos Inhabilitados en el Perfil';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `profile_role`
--

DROP TABLE IF EXISTS `profile_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profile_role` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) DEFAULT NULL COMMENT 'Identificador del Dominio',
  `profile` int(4) NOT NULL COMMENT 'Identificador del Perfil',
  `application_role` int(4) NOT NULL COMMENT 'Identificador del Role de la Aplicacion',
  PRIMARY KEY (`id`),
  KEY `IDX_PROFILE_ROLE_PROFILE` (`profile`),
  KEY `IDX_PROFILE_ROLE_APPLICATION_ROLE` (`application_role`),
  KEY `IDX_PROFILE_ROLE_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROFILE_ROLE_APPLICATION_ROLE` FOREIGN KEY (`application_role`) REFERENCES `application_role` (`id`),
  CONSTRAINT `FK_PROFILE_ROLE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROFILE_ROLE_PROFILE` FOREIGN KEY (`profile`) REFERENCES `profile` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4878 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Roles del Perfil';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Proyecto',
  `alias` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias del Proyecto',
  `registry` int(4) NOT NULL COMMENT 'Identificador del Cliente (Potencial) asociado',
  `date` date NOT NULL COMMENT 'Fecha del Proyecto',
  `project_type` int(4) DEFAULT NULL COMMENT 'Tipo de Proyecto',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Proyectos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_activity`
--

DROP TABLE IF EXISTS `project_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_activity` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Actividad',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project` int(4) NOT NULL COMMENT 'Identificador del Expendiente',
  `activity_type` int(4) NOT NULL COMMENT 'Tipo de Actividad',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Activo, si o no',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_ACTIVITY_ACTIVITY_TYPE` (`activity_type`),
  KEY `IDX_PROJECT_ACTIVITY_PROJECT` (`project`),
  KEY `IDX_PROJECT_ACTIVITY_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_ACTIVITY_ACTIVITY_TYPE` FOREIGN KEY (`activity_type`) REFERENCES `activity_type` (`id`),
  CONSTRAINT `FK_PROJECT_ACTIVITY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_ACTIVITY_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_attach`
--

DROP TABLE IF EXISTS `project_attach`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_attach` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project` int(4) NOT NULL COMMENT 'Identificador del Proyecto',
  `mimeType` tinyint(2) DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad del Archivo Adjunto',
  `attach_date` date DEFAULT NULL COMMENT 'Fecha del Archivo Adjunto',
  `attach_type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de Archivo Adjunto',
  `driveId` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL,
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_ATTACH_DOMAIN` (`domain`),
  KEY `IDX_PROJECT_ATTACH_PROJECT` (`project`),
  CONSTRAINT `FK_PROJECT_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_ATTACH_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Proyectos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_commercial`
--

DROP TABLE IF EXISTS `project_commercial`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_commercial` (
  `project` int(4) NOT NULL COMMENT 'Identificador del Proyecto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `seller` int(4) DEFAULT NULL COMMENT 'Identificador del Comercial',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  `source` tinyint(2) NOT NULL COMMENT 'Origen del Proyecto',
  `status` tinyint(2) NOT NULL COMMENT 'Estado del Proyecto',
  `status_date` date DEFAULT NULL COMMENT 'Fecha del Estado del Proyecto',
  `probability` int(4) DEFAULT NULL COMMENT 'Probabilidad del Proyecto',
  PRIMARY KEY (`project`),
  KEY `IDX_PROJECT_COMMERCIAL_TARGET` (`target`),
  KEY `IDX_PROJECT_COMMERCIAL_SELLER` (`seller`),
  KEY `IDX_PROJECT_COMMERCIAL_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_COMMERCIAL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_COMMERCIAL_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PROJECT_COMMERCIAL_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`),
  CONSTRAINT `FK_PROJECT_COMMERCIAL_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Operaciones Comerciales';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_reservation`
--

DROP TABLE IF EXISTS `project_reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_reservation` (
  `project` int(4) NOT NULL COMMENT 'Identificador del Proyecto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `hotel` int(4) NOT NULL COMMENT 'Identificador del Hotel de Produccion',
  `hotel_reservation` int(4) NOT NULL COMMENT 'Identificador del Hotel de la Reserva',
  `code` varchar(48) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Localizador de la Reserva',
  `start_date` date NOT NULL COMMENT 'Fecha de entrada',
  `start_time` datetime NOT NULL COMMENT 'Hora de entrada',
  `end_date` date NOT NULL COMMENT 'Fecha de salida',
  `end_time` datetime NOT NULL COMMENT 'Hora de salida',
  `seller` int(4) DEFAULT NULL COMMENT 'Identificador del canal de venta',
  `agency` int(4) DEFAULT NULL COMMENT 'Identificador de la agencia de viajes',
  `agency_commission_percent` double(5,2) DEFAULT '0.00' COMMENT 'Porcentaje de comision de la agencia',
  `agency_commission_amount` double(15,2) DEFAULT '0.00' COMMENT 'Importe de comision de la agencia',
  `agency_rebate` tinyint(1) NOT NULL COMMENT 'Indica si la agencia trabaja en modo descuento o no',
  `company` int(4) DEFAULT NULL COMMENT 'Identificador de la empresa',
  `discount_percent` double(5,2) DEFAULT '0.00' COMMENT 'Porcentaje de descuento',
  `discount_amount` double(15,2) DEFAULT '0.00' COMMENT 'Importe de descuento',
  `booking_holder` tinyint(2) NOT NULL COMMENT 'Titular de la Reserva',
  `taxable_base` double(15,2) DEFAULT '0.00' COMMENT 'Base imponible',
  `vat_quota` double(15,2) DEFAULT '0.00' COMMENT 'Cuota de IVA',
  `other_tax_quota` double(15,2) DEFAULT '0.00' COMMENT 'Cuota de otros Impuestos',
  `total` double(15,2) DEFAULT '0.00' COMMENT 'Importe Total',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  `remarks` text COLLATE latin1_spanish_ci COMMENT 'Observaciones',
  `source` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Origen de la Reserva',
  `crs_code` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de la Reserva en el CRS',
  `advance` double(15,2) NOT NULL DEFAULT '0.00' COMMENT 'Anticipo',
  `advance_invoiced` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el anticipo esta Facturado',
  `early_check_out` tinyint(1) DEFAULT '0' COMMENT 'Indica si se ha producido una salida anticipada',
  `prepay` tinyint(1) DEFAULT '0' COMMENT 'Indica si es un prepago',
  `bank_transaction` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de transaccion bancaria',
  `credit_card_number` varchar(4) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Ultimos 4 numeros de la tarjeta de credito',
  `credit_card_expiration_month` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mes de expiracion de la tarjeta de credito',
  `credit_card_expiration_year` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Año de expiracion de la tarjeta de credito',
  `credit_card_type` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo de tarjeta de credito',
  `token` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Token de preautorizacion de cobro',
  `penalty_value` varchar(4) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor de penalizacion (patron)',
  `penalty_amount` double(15,2) DEFAULT '0.00' COMMENT 'Importe de penalizacion',
  `penalty_date` datetime DEFAULT NULL COMMENT 'Fecha de penalizacion',
  `tourist_tax_free` tinyint(2) DEFAULT NULL COMMENT 'Tipo de exencion de la Tasa turistica',
  `check_status` tinyint(2) NOT NULL COMMENT 'Estado de registro en el Hotel',
  `status` tinyint(2) NOT NULL COMMENT 'Estado de la Reserva',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  `cancellation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de cancelacion',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_reservation_divert`
--

DROP TABLE IF EXISTS `project_reservation_divert`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_reservation_divert` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation` int(4) NOT NULL COMMENT 'Identificador de la Reserva',
  `request_hotel` int(4) NOT NULL COMMENT 'Identificador del Hotel solicitante',
  `divert_hotel` int(4) NOT NULL COMMENT 'Identificador del Hotel destino',
  `divert_date` date NOT NULL COMMENT 'Fecha de Desvio',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado del Desvio',
  `request_user` int(4) DEFAULT NULL COMMENT 'Identificador del Usuario solicitante',
  `response_user` int(4) DEFAULT NULL COMMENT 'Identificador del Usuario de respuesta',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_reservation_guest`
--

DROP TABLE IF EXISTS `project_reservation_guest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_reservation_guest` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation` int(4) NOT NULL COMMENT 'Identificador de la Reserva',
  `guest_index` tinyint(2) NOT NULL COMMENT 'Numero de Huesped',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre',
  `surname` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Apellido 1',
  `surname2` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Apellido 2',
  `treatment` varchar(4) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tratamiento',
  `document` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de documento de identificacion',
  `document_type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de documento',
  `document_country` varchar(2) COLLATE latin1_spanish_ci DEFAULT 'ES' COMMENT 'Pais del documento',
  `document_exp_date` date DEFAULT NULL COMMENT 'Fecha de expiracion del documento',
  `birth_date` date DEFAULT NULL COMMENT 'Fecha de nacimiento',
  `email` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Email',
  `phone` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono',
  `address` varchar(256) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion',
  `number` varchar(12) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero',
  `address2` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Segunda parte de la Direccion',
  `zip` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo postal',
  `city` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Ciudad',
  `province` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Provincia',
  `country` varchar(3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Pais',
  `barcode` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de pulsera',
  `person` int(4) DEFAULT NULL COMMENT 'Identificador de la Persona',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION` (`project_reservation`),
  KEY `IDX_PROJECT_RESERVATION_GUEST_DOMAIN` (`domain`),
  KEY `IDX_PROJECT_RESERVATION_GUEST_PERSON` (`person`),
  CONSTRAINT `FK_PROJECT_RESERVATION_GUEST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_GUEST_PERSON` FOREIGN KEY (`person`) REFERENCES `person` (`registry`),
  CONSTRAINT `FK_PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION` FOREIGN KEY (`project_reservation`) REFERENCES `project_reservation` (`project`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Huespedes por Reserva';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_reservation_room`
--

DROP TABLE IF EXISTS `project_reservation_room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_reservation_room` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation` int(4) NOT NULL COMMENT 'Identificador de la Reserva',
  `room_index` tinyint(2) NOT NULL COMMENT 'Numero de Habitacion',
  `room_code` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de Habitacion en origen',
  `item` int(4) NOT NULL COMMENT 'Identificador del Tipo de Habitacion',
  `allotment_rate_code` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de Cupo',
  `rate_plan` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de Tarifa en origen',
  `tariff` int(4) NOT NULL COMMENT 'Identificador de la Tarifa',
  `adults` smallint(2) DEFAULT '0' COMMENT 'Numero de adultos',
  `children` smallint(2) DEFAULT '0' COMMENT 'Numero de nios',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_reservation_room_detail`
--

DROP TABLE IF EXISTS `project_reservation_room_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_reservation_room_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation_room` int(4) NOT NULL COMMENT 'Identificador de la Habitacion de la Reserva',
  `asset_activity` int(4) NOT NULL COMMENT 'Identificador de la Actividad de la Habitacion',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM` (`project_reservation_room`),
  KEY `IDX_PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY` (`asset_activity`),
  KEY `IDX_PROJECT_RESERVATION_ROOM_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY` FOREIGN KEY (`asset_activity`) REFERENCES `asset_activity` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM` FOREIGN KEY (`project_reservation_room`) REFERENCES `project_reservation_room` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Habitacion por Reserva';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_reservation_service`
--

DROP TABLE IF EXISTS `project_reservation_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_reservation_service` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation` int(4) NOT NULL COMMENT 'Identificador de la Reserva',
  `service_index` tinyint(2) NOT NULL COMMENT 'Numero de Servicio',
  `service_code` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del Servicio en origen',
  `item` int(4) NOT NULL COMMENT 'Identificador del Servicio',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `meal_plan` varchar(3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Regimen',
  `project_reservation_room` int(4) DEFAULT NULL COMMENT 'Identificador de la Habitacion de la Reserva',
  `extra` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si se trata de un Servicio extra',
  `removed` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Servicio borrado',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION` (`project_reservation`),
  KEY `IDX_PROJECT_RESERVATION_SERVICE_ITEM` (`item`),
  KEY `IDX_PROJECT_RESERVATION_SERVICE_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_RESERVATION_SERVICE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_SERVICE_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION` FOREIGN KEY (`project_reservation`) REFERENCES `project_reservation` (`project`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Servicios por Reserva';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_reservation_service_detail`
--

DROP TABLE IF EXISTS `project_reservation_service_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_reservation_service_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation_service` int(4) NOT NULL COMMENT 'Identificador del Servicio de la Reserva',
  `project_reservation_room_detail` int(4) DEFAULT NULL COMMENT 'Identificador del Detalle de Habitacion de la Reserva',
  `effective_date` date NOT NULL COMMENT 'Fecha de efecto',
  `quantity` double(15,2) DEFAULT '0.00' COMMENT 'Cantidad',
  `price` double(15,4) DEFAULT '0.0000' COMMENT 'Precio',
  `taxable_base` double(15,4) DEFAULT '0.0000' COMMENT 'Base imponible',
  `taxable_base_production` double(15,4) DEFAULT '0.0000' COMMENT 'Base imponible de Produccion',
  `total_production` double(15,4) DEFAULT '0.0000' COMMENT 'Total Produccion acumulado',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_RESERVATION_SERVICE_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_PRJ_RESERVATION_SERVICE_DETAIL_PRJ_RESERVATION_SERVICE` (`project_reservation_service`),
  KEY `IDX_PRJ_RESERVATION_SERVICE_DETAIL_PRJ_RESERVATION_ROOM_DETAIL` (`project_reservation_room_detail`),
  CONSTRAINT `FK_PRJ_RESERVATION_SERVICE_DETAIL_PRJ_RESERVATION_ROOM_DETAIL` FOREIGN KEY (`project_reservation_room_detail`) REFERENCES `project_reservation_room_detail` (`id`),
  CONSTRAINT `FK_PRJ_RESERVATION_SERVICE_DETAIL_PRJ_RESERVATION_SERVICE` FOREIGN KEY (`project_reservation_service`) REFERENCES `project_reservation_service` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_SERVICE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Servicio por Reserva';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_tas`
--

DROP TABLE IF EXISTS `project_tas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_tas` (
  `project` int(4) NOT NULL COMMENT 'Identificador del Proyecto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `series` char(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie de la Orden de Reparacion',
  `number` int(4) NOT NULL DEFAULT '0' COMMENT 'Numero de la Orden de Reparacion',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `tas_item` int(4) NOT NULL COMMENT 'Identificador del Articulo susceptible de Asistencia Tecnica',
  `counter` double(15,3) DEFAULT '0.000' COMMENT 'Contador del Articulo de la Orden de Reparacion (p.e. Kilometraje)',
  `task_holder` int(4) DEFAULT NULL COMMENT 'Identificador del Empleado que ejecuta la Orden',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  `status` tinyint(2) NOT NULL COMMENT 'Estado de la Orden de Reparacion',
  `status_date` date DEFAULT NULL COMMENT 'Fecha del Estado de la Orden de Reparacion',
  `workplace` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Centro de Trabajo',
  PRIMARY KEY (`project`),
  KEY `IDX_PROJECT_TAS_TARGET` (`target`),
  KEY `IDX_PROJECT_TAS_TAS_ITEM` (`tas_item`),
  KEY `IDX_PROJECT_TAS_TASK_HOLDER` (`task_holder`),
  KEY `IDX_PROJECT_TAS_WORKPLACE` (`workplace`),
  KEY `IDX_PROJECT_TAS_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_TAS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_TAS_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PROJECT_TAS_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_PROJECT_TAS_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`),
  CONSTRAINT `FK_PROJECT_TAS_TAS_ITEM` FOREIGN KEY (`tas_item`) REFERENCES `tas_item` (`id`),
  CONSTRAINT `FK_PROJECT_TAS_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ordenes de Reparacion o Fabricacion';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_type`
--

DROP TABLE IF EXISTS `project_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_type` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Tipo de Expediente',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Expediente',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Activo si o no',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_TYPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Expedientes';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `proposal`
--

DROP TABLE IF EXISTS `proposal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `proposal` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `issue_date` date DEFAULT NULL COMMENT 'Fecha de emision de la Propuesta',
  `department` int(4) DEFAULT NULL COMMENT 'Identificador del Departamento',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `warehouse` int(4) DEFAULT NULL COMMENT 'Identificador del Almacen',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `remarks` text COLLATE latin1_spanish_ci COMMENT 'Observaciones de la Propuesta',
  `item_return` tinyint(1) DEFAULT '0' COMMENT 'Indica si es una devolucion',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado de la Propuesta',
  `transfer_status` tinyint(2) DEFAULT '0' COMMENT 'Indica si es un traspaso y su estado',
  `transfer_proposal` int(4) DEFAULT NULL COMMENT 'Identificador de la Solicitud de traspaso vinculada',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `proposal_detail`
--

DROP TABLE IF EXISTS `proposal_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `proposal_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `proposal` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Propuesta de Compra',
  `item` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Articulo',
  `description` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `quantity` double(15,3) DEFAULT '0.000' COMMENT 'Cantidad',
  `price` double DEFAULT '0' COMMENT 'Precio',
  `discount_expr` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descuentos',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado del Detalle de la Propuesta',
  `supplier` int(4) DEFAULT NULL COMMENT 'Identificador de Proveedor',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `purchase`
--

DROP TABLE IF EXISTS `purchase`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `purchase` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Pedido de Compra',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project` int(4) DEFAULT NULL COMMENT 'Identificador del Proyecto',
  `supplier` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Proveedor',
  `series` char(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie del Pedido',
  `number` int(4) NOT NULL DEFAULT '0' COMMENT 'Numero del Pedido',
  `purchase_reference` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de referencia del Pedido de Compra',
  `address` int(4) DEFAULT NULL COMMENT 'Identificador de la Direccion del Proveedor',
  `discount_expr` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descuentos del Pedido',
  `issue_date` date DEFAULT NULL COMMENT 'Fecha de emision del Pedido',
  `pay_method` int(4) DEFAULT NULL COMMENT 'Identificador de la Forma de Pago',
  `document_type` tinyint(2) DEFAULT '1' COMMENT 'Tipo de Pedido',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad del Pedido',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado del Pedido',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios del Pedido',
  `remarks` text COLLATE latin1_spanish_ci COMMENT 'Observaciones del Pedido',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `warehouse` int(4) DEFAULT NULL COMMENT 'Identificador del Almacen',
  `scope` int(4) NOT NULL DEFAULT '1' COMMENT 'Ambito del Pedido',
  `number_of_pymnts` smallint(2) DEFAULT '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint(2) DEFAULT '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint(2) DEFAULT '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Dias de pago',
  `bank_account` varchar(34) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'IBAN - Numero de Cuenta Bancaria Internacional',
  `bank_alias` varchar(25) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias del Banco',
  `bic` varchar(11) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'BIC - Codigo Identificador del Banco',
  `email_communication` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si se ha comunicado a traves de email',
  `delivery_date` date DEFAULT NULL COMMENT 'Fecha de entrega',
  `carrier` int(4) DEFAULT NULL COMMENT 'Identificador de la Agencia de Transporte',
  `carrier_packing` int(4) DEFAULT NULL COMMENT 'Identificador de la Hoja de ruta',
  `shipping_alternative_address` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Primera parte de la Direccion de entrega',
  `shipping_alternative_address2` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Segunda parte de la Direccion de entrega',
  `shipping_alternative_zip` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo Postal de entrega',
  `shipping_alternative_city` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Localidad de entrega',
  `shipping_alternative_phone` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono de contacto de la entrega',
  `shipping_alternative_recipient` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Destinatario de la entrega',
  `shipping_contact` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del contacto para la entrega',
  `shipping_period` tinyint(2) DEFAULT '0' COMMENT 'Tipo de periodo de entrega',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Pedidos de Compra';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `purchase_detail`
--

DROP TABLE IF EXISTS `purchase_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `purchase_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Detalle del Pedido de Compra',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `purchase` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Pedido de Compra',
  `project` int(4) DEFAULT NULL COMMENT 'Identificador del Proyecto',
  `line` smallint(2) DEFAULT '1' COMMENT 'Numero de linea del Detalle dentro del Pedido',
  `item` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Articulo del Detalle de Pedido',
  `description` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Detalle de Pedido',
  `quantity` double(15,3) DEFAULT '0.000' COMMENT 'Cantidad del Detalle de Pedido',
  `price` double DEFAULT '0' COMMENT 'Precio del Detalle de Pedido',
  `discount_expr` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descuentos del Detalle de Pedido',
  `taxes` double(15,3) DEFAULT '0.000' COMMENT 'Tasas del Detalle de Pedido',
  `status` tinyint(2) DEFAULT NULL COMMENT 'Estado del Detalle de Pedido',
  `source` tinyint(2) DEFAULT '0' COMMENT 'Origen del Detalle de la Compra',
  `source_id` int(4) DEFAULT NULL COMMENT 'Identificador del Origen del Detalle de la Compra',
  `proposal_detail` int(4) DEFAULT NULL COMMENT 'Identificador del Detalle de Solicitud',
  `delivered` double DEFAULT '0' COMMENT 'Cantidad entregada del Detalle de Pedido',
  `delivery_date` date DEFAULT NULL COMMENT 'Fecha de entrega',
  `carrier` int(4) DEFAULT NULL COMMENT 'Identificador de la Agencia de Transporte',
  `carrier_packing` int(4) DEFAULT NULL COMMENT 'Identificador de la Hoja de ruta',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Pedido de Compra';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qualification`
--

DROP TABLE IF EXISTS `qualification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualification` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Calificacion',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `code` char(5) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo de la Calificacion',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de la Calificacion',
  `min_value` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Limite inferior de la Calificacion',
  `max_value` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Limite superior de la Calificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_QUALIFICATION_DOMAIN` (`domain`),
  CONSTRAINT `FK_QUALIFICATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Calificaciones';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quality_skill`
--

DROP TABLE IF EXISTS `quality_skill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quality_skill` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Aptitud Calidad',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `code` char(5) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo de la Aptitud Calidad',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de la Aptitud Calidad',
  PRIMARY KEY (`id`),
  KEY `IDX_QUALITY_SKILL_DOMAIN` (`domain`),
  CONSTRAINT `FK_QUALITY_SKILL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Aptitudes Calidad';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `question` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Pregunta esta activa o no',
  `question_text` varchar(255) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Texto de la Pregunta',
  `type` tinyint(2) NOT NULL COMMENT 'Tipo de Pregunta',
  `argument` text COLLATE latin1_spanish_ci COMMENT 'Argumentacion de la Pregunta',
  `alias` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias de la Pregunta',
  PRIMARY KEY (`id`),
  KEY `IDX_QUESTION_DOMAIN` (`domain`),
  CONSTRAINT `FK_QUESTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Preguntas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `question_value`
--

DROP TABLE IF EXISTS `question_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `question_value` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `value_text` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor de tipo texto',
  `value_number` double(15,3) DEFAULT NULL COMMENT 'Valor de tipo numerico',
  `value_date` datetime DEFAULT NULL COMMENT 'Valor de tipo fecha',
  PRIMARY KEY (`id`),
  KEY `IDX_QUESTION_VALUE_QUESTION` (`question`),
  KEY `IDX_QUESTION_VALUE_DOMAIN` (`domain`),
  CONSTRAINT `FK_QUESTION_VALUE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_QUESTION_VALUE_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Valores de Preguntas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `raddinfo`
--

DROP TABLE IF EXISTS `raddinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `raddinfo` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL COMMENT 'Identificador de la Persona o Empresa',
  `attribute` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Atributo adicional',
  `value` varchar(128) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Valor del atributo adicional',
  `value_date` date NOT NULL COMMENT 'Fecha del valor del atributo',
  PRIMARY KEY (`id`),
  KEY `IDX_RADDINFO_REGISTRY` (`registry`),
  KEY `IDX_RADDINFO_DOMAIN` (`domain`),
  CONSTRAINT `FK_RADDINFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RADDINFO_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Informacion adicional de la Persona o Empresa';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `raddress`
--

DROP TABLE IF EXISTS `raddress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `raddress` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Direccion de la Persona o Empresa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Registro de la Persona o Empresa',
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de Direccion',
  `recipient` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Destinatario',
  `street_type` varchar(2) COLLATE latin1_spanish_ci DEFAULT 'CL' COMMENT 'Tipo de via',
  `address` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Primera parte de la Direccion',
  `number` varchar(12) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero',
  `address2` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Segunda parte de la Direccion',
  `address3` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tercera parte de la Direccion',
  `zip` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo Postal',
  `city` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Localidad',
  `geozone` int(4) DEFAULT NULL COMMENT 'Identificador de la Zona Geografica',
  `alias` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias',
  `municipality_code` varchar(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del municipio',
  PRIMARY KEY (`id`),
  KEY `IDX_RADDRESS_REGISTRY` (`registry`),
  KEY `IDX_RADDRESS_GEOZONE` (`geozone`),
  KEY `IDX_RADDRESS_DOMAIN` (`domain`),
  CONSTRAINT `FK_RADDRESS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RADDRESS_GEOZONE` FOREIGN KEY (`geozone`) REFERENCES `geozone` (`id`),
  CONSTRAINT `FK_RADDRESS_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7730586 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Direcciones de Personas o Empresas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rattach`
--

DROP TABLE IF EXISTS `rattach`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rattach` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Archivo Adjunto de la Persona o Empresa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Registro de la Persona o Empresa',
  `category` int(4) DEFAULT NULL COMMENT 'Categoria del Archivo Adjunto',
  `mimeType` tinyint(2) DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Archivo Adjunto',
  `scope` int(4) DEFAULT NULL COMMENT 'Ambito del Archivo Adjunto',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad del Archivo Adjunto',
  `attach_date` date DEFAULT NULL COMMENT 'Fecha del Archivo Adjunto',
  `drive_id` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL,
  `dparent_id` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL,
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
) ENGINE=InnoDB AUTO_INCREMENT=68234 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Personas o Empresas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rattach_tag`
--

DROP TABLE IF EXISTS `rattach_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rattach_tag` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `rattach` int(4) NOT NULL COMMENT 'Identificador del Archivo Adjunto',
  `tag` int(4) NOT NULL COMMENT 'Identificador de la Etiqueta',
  PRIMARY KEY (`id`),
  KEY `IDX_RATTACH_TAG_DOMAIN` (`domain`),
  KEY `IDX_RATTACH_TAG_RATTACH` (`rattach`),
  KEY `IDX_RATTACH_TAG_TAG` (`tag`),
  CONSTRAINT `FK_RATTACH_TAG_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RATTACH_TAG_RATTACH` FOREIGN KEY (`rattach`) REFERENCES `rattach` (`id`),
  CONSTRAINT `FK_RATTACH_TAG_TAG` FOREIGN KEY (`tag`) REFERENCES `tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Archivos Adjuntos y Etiquetas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rbank`
--

DROP TABLE IF EXISTS `rbank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rbank` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Cuenta Bancaria de la Persona o Empresa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Registro de la Persona o Empresa',
  `bank_account` char(34) COLLATE latin1_spanish_ci NOT NULL COMMENT 'IBAN - Numero de Cuenta Bancaria Internacional',
  `bic` char(11) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'BIC - Codigo Identificador del Banco',
  `sufix` char(3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Sufijo de Cuenta Bancaria para Remesas',
  `alias` varchar(25) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias de la Cuenta Bancaria',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si la Cuenta Bancaria esta activa o no',
  `account` int(4) DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY (`id`),
  KEY `IDX_RBANK_REGISTRY` (`registry`),
  KEY `IDX_RBANK_DOMAIN` (`domain`),
  KEY `IDX_RBANK_ACCOUNT` (`account`),
  CONSTRAINT `FK_RBANK_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_RBANK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RBANK_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos de Cuentas Bancarias de Personas o Empresas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rdir_staff`
--

DROP TABLE IF EXISTS `rdir_staff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rdir_staff` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Relacion entre Empresas y sus Directivos',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `document` varchar(16) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Numero de Documento del Directivo',
  `name` varchar(128) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Directivo',
  `shareholder` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el Directivo es socio',
  `representative` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el Directivo es representante legal',
  `director` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el Directivo es administrador',
  `percent_share` double DEFAULT '0' COMMENT 'Porcentaje de acciones (solo para socios)',
  `share_number` int(4) DEFAULT '0' COMMENT 'Numero de Acciones',
  `nominal_value` double(15,3) DEFAULT '0.000' COMMENT 'Valor Nominal',
  `due_date` date DEFAULT NULL COMMENT 'Fecha de vencimiento del cargo',
  `representative_labor` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si el Directivo es representante laboral',
  `charge_description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del cargo de Directivo',
  PRIMARY KEY (`id`),
  KEY `IDX_RDIR_STAFF_REGISTRY` (`registry`),
  KEY `IDX_RDIR_STAFF_DOMAIN` (`domain`),
  CONSTRAINT `FK_RDIR_STAFF_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RDIR_STAFF_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Empresas y sus Directivos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `record_data`
--

DROP TABLE IF EXISTS `record_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `record_data` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Dato Registral',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL COMMENT 'Registro de la Empresa',
  `creation_date` date DEFAULT NULL COMMENT 'Fecha de creacion del Dato Registral',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Dato Registral',
  `notary` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Notario del Dato Registral',
  `number` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero del Dato Registral',
  `record_date` date DEFAULT NULL COMMENT 'Fecha de registro del Dato Registral',
  `volume` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tomo del Dato Registral',
  `section` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Seccion del Dato Registral',
  `page` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Folio del Dato Registral',
  `sheet` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Hoja del Dato Registral',
  `registration` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Inscripcion del Dato Registral',
  `attach` int(4) DEFAULT NULL COMMENT 'Archivo adjunto',
  PRIMARY KEY (`id`),
  KEY `IDX_RECORD_DATA_RATTACH` (`attach`),
  KEY `IDX_RECORD_DATA_REGISTRY` (`registry`),
  KEY `IDX_RECORD_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_RECORD_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RECORD_DATA_RATTACH` FOREIGN KEY (`attach`) REFERENCES `rattach` (`id`),
  CONSTRAINT `FK_RECORD_DATA_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos Registrales';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `registry`
--

DROP TABLE IF EXISTS `registry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `registry` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Persona o Empresa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `document` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Documento de la Persona o Empresa',
  `document_type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de documento (NIF, CIF...)',
  `document_country` varchar(2) COLLATE latin1_spanish_ci DEFAULT 'ES' COMMENT 'Pais del documento',
  `name` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre de la Persona o Empresa',
  `alias` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias de la Persona o Empresa',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo (Persona o Empresa)',
  `nationality` varchar(2) COLLATE latin1_spanish_ci DEFAULT 'ES' COMMENT 'Nacionalidad',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad',
  PRIMARY KEY (`id`),
  KEY `IDX_REGISTRY_NAME` (`name`),
  KEY `IDX_REGISTRY_DOCUMENT` (`document`),
  KEY `IDX_REGISTRY_DOMAIN` (`domain`),
  CONSTRAINT `FK_REGISTRY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8628602 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Registro de Personas o Empresas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `relationship`
--

DROP TABLE IF EXISTS `relationship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `relationship` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Tipo de Relacion',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Relacion',
  PRIMARY KEY (`id`),
  KEY `IDX_RELATIONSHIP_DOMAIN` (`domain`),
  CONSTRAINT `FK_RELATIONSHIP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Relaciones entre Personas y/o Empresas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reservation_request`
--

DROP TABLE IF EXISTS `reservation_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reservation_request` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `hotel` int(4) NOT NULL COMMENT 'Identificador del Hotel',
  `code` varchar(48) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Localizador',
  `start_date` date NOT NULL COMMENT 'Fecha de entrada',
  `end_date` date NOT NULL COMMENT 'Fecha de salida',
  `agency` int(4) DEFAULT NULL COMMENT 'Identificador de la agencia de viajes',
  `company` int(4) DEFAULT NULL COMMENT 'Identificador de la empresa',
  `booking_holder` tinyint(2) NOT NULL COMMENT 'Titular',
  `request_counter` smallint(6) NOT NULL COMMENT 'Numero de solicitudes enviadas',
  `remarks` varchar(166) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Observaciones',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Solicitud esta activa o no',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reservation_request_guest`
--

DROP TABLE IF EXISTS `reservation_request_guest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reservation_request_guest` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `reservation_request` int(4) NOT NULL COMMENT 'Identificador de la Solicitud de Reserva',
  `guest_index` tinyint(2) NOT NULL COMMENT 'Numero de Huesped',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre',
  `surname` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Apellidos',
  `email` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Email',
  `phone` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono',
  `address` varchar(256) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion',
  `number` varchar(12) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero',
  `address2` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Segunda parte de la Direccion',
  `zip` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo postal',
  `city` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Ciudad',
  `province` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Provincia',
  `country` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Pais',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_RESERVATION_REQUEST_GUEST_DOMAIN` (`domain`),
  KEY `IDX_RESERVATION_REQUEST_GUEST_RESERVATION_REQUEST` (`reservation_request`),
  CONSTRAINT `FK_RESERVATION_REQUEST_GUEST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RESERVATION_REQUEST_GUEST_RESERVATION_REQUEST` FOREIGN KEY (`reservation_request`) REFERENCES `reservation_request` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Huespedes por Solicitud de Reserva';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reservation_request_room`
--

DROP TABLE IF EXISTS `reservation_request_room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reservation_request_room` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `reservation_request` int(4) NOT NULL COMMENT 'Identificador de la Solicitud de Reserva',
  `room_index` tinyint(2) NOT NULL COMMENT 'Numero de Habitacion',
  `units` tinyint(2) NOT NULL COMMENT 'Numero de Habitaciones',
  `item` int(4) NOT NULL COMMENT 'Identificador del Tipo de Habitacion',
  `adults` smallint(2) DEFAULT '0' COMMENT 'Numero de adultos',
  `children` smallint(2) DEFAULT '0' COMMENT 'Numero de nios',
  `babies` smallint(2) DEFAULT '0' COMMENT 'Numero de bebes',
  `crs_code` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de Reserva en CRS',
  `tariff_code` varchar(8) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de Tarifa',
  `tariff_description` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de Tarifa',
  `inventory_code` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de Servicio',
  `room_code` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de Habitacion',
  `room_description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de Habitacion',
  `meal_plan` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo de regimen',
  `daily_price` double(15,2) DEFAULT '0.00' COMMENT 'Importe Diario',
  `total_price` double(15,2) DEFAULT '0.00' COMMENT 'Importe Total',
  `agreed_price` double(15,2) DEFAULT '0.00' COMMENT 'Importe Pactado',
  `cancel_penalty` varchar(256) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Penalizaciones por cancelacion',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ritem`
--

DROP TABLE IF EXISTS `ritem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ritem` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL COMMENT 'Identificador de Persona o Empresa',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo',
  `type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de relacion',
  `code` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del Producto',
  `edi_sales_code` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo EAN de ventas para EDI',
  `price` double DEFAULT '0' COMMENT 'Precio del Producto',
  `discount_expr` varchar(32) COLLATE latin1_spanish_ci DEFAULT '0.0' COMMENT 'Descuentos del Producto',
  `priority` tinyint(2) DEFAULT '0' COMMENT 'Prioridad del Producto',
  `workplace` int(4) DEFAULT NULL COMMENT 'Identificador del Centro de Trabajo',
  `status` tinyint(2) NOT NULL COMMENT 'Estado',
  PRIMARY KEY (`id`),
  KEY `IDX_RITEM_DOMAIN` (`domain`),
  KEY `IDX_RITEM_ITEM` (`item`),
  KEY `IDX_RITEM_REGISTRY` (`registry`),
  KEY `IDX_RITEM_WORKPLACE` (`workplace`),
  CONSTRAINT `FK_RITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RITEM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_RITEM_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_RITEM_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulos interesados por Personas o Empresas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rmedia`
--

DROP TABLE IF EXISTS `rmedia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rmedia` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Medio de Contacto de la Persona o Empresa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Registro de la Persona o Empresa',
  `media` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de Medio de Contacto de la Persona o Empresa',
  `value` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor del Medio de Contacto de la Persona o Empresa',
  `comment` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Comentarios acerca del Medio de Contacto de la Persona o Empresa',
  `administrative` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Contacto es de caracter administrativo',
  `commercial` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Contacto es de caracter comercial',
  `technical` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Contacto es de caracter tecnico',
  `raddress` int(4) DEFAULT NULL COMMENT 'Direccion del contacto',
  PRIMARY KEY (`id`),
  KEY `IDX_RMEDIA_RADDRESS` (`raddress`),
  KEY `IDX_RMEDIA_REGISTRY` (`registry`),
  KEY `IDX_RMEDIA_DOMAIN` (`domain`),
  CONSTRAINT `FK_RMEDIA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RMEDIA_RADDRESS` FOREIGN KEY (`raddress`) REFERENCES `raddress` (`id`),
  CONSTRAINT `FK_RMEDIA_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9235492 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Medios de Contacto de Personas o Empresas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rnote`
--

DROP TABLE IF EXISTS `rnote`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rnote` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Nota de la Persona o Empresa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Registro de la Persona o Empresa',
  `description` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Nota',
  `note_date` date DEFAULT NULL COMMENT 'Fecha de la Nota',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Nota',
  `note_type` tinyint(2) DEFAULT NULL,
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad de la Nota',
  PRIMARY KEY (`id`),
  KEY `IDX_RNOTE_REGISTRY` (`registry`),
  KEY `IDX_RNOTE_DOMAIN` (`domain`),
  CONSTRAINT `FK_RNOTE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RNOTE_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Notas de Personas o Empresas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Role',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1059 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Roles';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `room` (
  `asset` int(4) NOT NULL COMMENT 'Identificador del Activo',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `hotel` int(4) NOT NULL COMMENT 'Identificador del Hotel',
  `item` int(4) NOT NULL COMMENT 'Identificador del Producto',
  `status` tinyint(2) NOT NULL DEFAULT '1' COMMENT 'Estado de la Habitacion',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rpaymethod`
--

DROP TABLE IF EXISTS `rpaymethod`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rpaymethod` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Forma de Pago de la Persona o Empresa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL COMMENT 'Identificador del Registro de la Persona o Empresa',
  `pay_method` int(4) NOT NULL COMMENT 'Identificador de la Forma de Pago',
  `rbank` int(4) DEFAULT NULL COMMENT 'Identificador de la Entidad Bancaria',
  `number_of_pymnts` smallint(2) DEFAULT '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint(2) DEFAULT '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint(2) DEFAULT '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Dias de pago',
  PRIMARY KEY (`id`),
  KEY `IDX_RPAYMETHOD_PAY_METHOD` (`pay_method`),
  KEY `IDX_RPAYMETHOD_RBANK` (`rbank`),
  KEY `IDX_RPAYMETHOD_REGISTRY` (`registry`),
  KEY `IDX_RPAYMETHOD_DOMAIN` (`domain`),
  CONSTRAINT `FK_RPAYMETHOD_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RPAYMETHOD_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_RPAYMETHOD_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`),
  CONSTRAINT `FK_RPAYMETHOD_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos de la Forma de Pago de la Persona o Empresa';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rprofile`
--

DROP TABLE IF EXISTS `rprofile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rprofile` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL COMMENT 'Identificador de Persona o Empresa',
  `last_update` datetime NOT NULL COMMENT 'Fecha de la ultima modificacion del Perfil del Cliente Potencial',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `value_text` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor de tipo texto',
  `value_number` double(15,3) DEFAULT NULL COMMENT 'Valor de tipo numerico',
  `value_date` datetime DEFAULT NULL COMMENT 'Valor de tipo fecha',
  PRIMARY KEY (`id`),
  KEY `IDX_RPROFILE_DOMAIN` (`domain`),
  KEY `IDX_RPROFILE_QUESTION` (`question`),
  KEY `IDX_RPROFILE_REGISTRY` (`registry`),
  CONSTRAINT `FK_RPROFILE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RPROFILE_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`),
  CONSTRAINT `FK_RPROFILE_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Perfiles de Personas o Empresas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rrelationship`
--

DROP TABLE IF EXISTS `rrelationship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rrelationship` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Relacion',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL COMMENT 'Identificador de la Persona o Empresa que tiene la Relacion',
  `related_registry` int(4) NOT NULL COMMENT 'Identificador de la Persona o Empresa relacionada',
  `relationship` int(4) NOT NULL COMMENT 'Identificador del Tipo de Relación',
  `comments` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Comentarios de la Relacion',
  PRIMARY KEY (`id`),
  KEY `IDX_RRELATIONSHIP_REGISTRY` (`registry`),
  KEY `IDX_RRELATIONSHIP_RELATED_REGISTRY` (`related_registry`),
  KEY `IDX_RRELATIONSHIP_RELATIONSHIP` (`relationship`),
  KEY `IDX_RRELATIONSHIP_DOMAIN` (`domain`),
  CONSTRAINT `FK_RRELATIONSHIP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RRELATIONSHIP_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_RRELATIONSHIP_RELATED_REGISTRY` FOREIGN KEY (`related_registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_RRELATIONSHIP_RELATIONSHIP` FOREIGN KEY (`relationship`) REFERENCES `relationship` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relaciones entre Personas y/o Empresas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rsegment`
--

DROP TABLE IF EXISTS `rsegment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rsegment` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL COMMENT 'Identificador de Persona o Empresa',
  `segment` int(4) NOT NULL COMMENT 'Identificador del Segmento',
  PRIMARY KEY (`id`),
  KEY `IDX_RSEGMENT_REGISTRY` (`registry`),
  KEY `IDX_RSEGMENT_SEGMENT` (`segment`),
  KEY `IDX_RSEGMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_RSEGMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RSEGMENT_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_RSEGMENT_SEGMENT` FOREIGN KEY (`segment`) REFERENCES `segment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Segmentos de Personas o Empresas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rseller`
--

DROP TABLE IF EXISTS `rseller`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rseller` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL COMMENT 'Identificador de Persona o Empresa',
  `seller` int(4) NOT NULL COMMENT 'Identificador del Comercial',
  `start_date` date NOT NULL COMMENT 'Fecha de Inicio',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de Fin',
  `status` tinyint(2) NOT NULL COMMENT 'Estado',
  PRIMARY KEY (`id`),
  KEY `IDX_RSELLER_DOMAIN` (`domain`),
  KEY `IDX_RSELLER_SELLER` (`seller`),
  KEY `IDX_RSELLER_REGISTRY` (`registry`),
  CONSTRAINT `FK_RSELLER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RSELLER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_RSELLER_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comerciales relacionados con Personas o Empresas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rsupplier`
--

DROP TABLE IF EXISTS `rsupplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rsupplier` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL COMMENT 'Identificador de Persona o Empresa',
  `supplier` int(4) NOT NULL COMMENT 'Identificador del Proveedor',
  `target_external_code` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del Cliente Potencial para el Proveedor',
  `tariff` int(4) DEFAULT NULL COMMENT 'Identificador de Tarifa',
  `pay_method` int(4) DEFAULT NULL COMMENT 'Identificador de la Forma de Pago',
  `number_of_pymnts` smallint(2) DEFAULT '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint(2) DEFAULT '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint(2) DEFAULT '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Dias de pago',
  `bank_account` varchar(34) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'IBAN - Numero de Cuenta Bancaria Internacional',
  `bank_alias` varchar(25) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias del Banco',
  `bic` varchar(11) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'BIC - Codigo Identificador del Banco',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Proveedores relacionados con Personas o Empresas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rtax`
--

DROP TABLE IF EXISTS `rtax`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rtax` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL COMMENT 'Identificador de Persona o Empresa',
  `tax` int(4) NOT NULL COMMENT 'Identificador del Impuesto',
  `percentage` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Porcentaje de recargo actual',
  `surcharge` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Porcentaje de recargo de equivalencia actual',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio de vigencia',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de fin de vigencia',
  PRIMARY KEY (`id`),
  KEY `IDX_RTAX_DOMAIN` (`domain`),
  KEY `IDX_RTAX_REGISTRY` (`registry`),
  KEY `IDX_RTAX_TAX` (`tax`),
  CONSTRAINT `FK_RTAX_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RTAX_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_RTAX_TAX` FOREIGN KEY (`tax`) REFERENCES `tax` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Impuestos redefinidos para Personas o Empresas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `salary`
--

DROP TABLE IF EXISTS `salary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `salary` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Nomina',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio liquidación',
  `end_date` date NOT NULL COMMENT 'Fecha de finalizacion liquidación',
  `enterprise_name` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre de la empresa',
  `enterprise_address` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Domicilio de la empresa',
  `enterprise_document` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Documento de la Empresa',
  `ccc` char(11) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor del Codigo Cuenta Cotizacion',
  `ss_regime` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Regimen de la Seguridad Social',
  `employee_name` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del trabajador',
  `social_security_number` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de la seguridad social',
  `employee_document` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Documento de la Persona',
  `seniority_date` date DEFAULT NULL COMMENT 'Fecha de antiguedad',
  `quote_group` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Grupo de Cotización',
  `category` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Categoria o grupo profesional',
  `registration` int(4) NOT NULL COMMENT 'Número libro de matricula',
  `time_units` int(4) NOT NULL COMMENT 'total dias/horas',
  `total_payment` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Total devengado',
  `total_deduction` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Total a deducir',
  `total_liquid` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Liquido total a percibir',
  `total_enterprise` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Cuota total de la empresa',
  `issue_date` date NOT NULL COMMENT 'Fecha de emisión',
  `remuneration` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Remuneración mensual',
  `pro_ext_base` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Base prorraterreada de pagas extras',
  `it_base` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Base de IT',
  `raw_cgc_base` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Base efectiva de cotizacion por contingencias comunes ',
  `cgc_base` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Base de cotizacion por contingencias comunes',
  `hextra_base` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Base de cotizacion adicional por horas extraordinarias estructurales',
  `non_hextra_base` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Base de cotizacion adicional por horas extraordinarias no estructurales',
  `cgp_base` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Base de cotizacion por contingencias profesionales',
  `money_irpf_base` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Salario en dinero sujeto a retención I.R.P.F',
  `inkind_irpf_base` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Salario en especie sujeto a retención I.R.P.F',
  `irpf_base` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Base sujeta a retención I.R.P.F',
  `social_security_contributions` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Aportaciones a la Seguridad Social',
  `total_irpf` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Total retención aplicada ',
  `charge_date` date NOT NULL COMMENT 'Fecha de cobro',
  PRIMARY KEY (`id`),
  KEY `IDX_SALARY_CONTRACT` (`contract`),
  KEY `IDX_SALARY_DOMAIN` (`domain`),
  CONSTRAINT `FK_SALARY_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_SALARY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=373 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Recibo del pago de salarios';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `salary_bonus`
--

DROP TABLE IF EXISTS `salary_bonus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `salary_bonus` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios',
  `bonus_concept` varchar(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del concepto',
  `amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  PRIMARY KEY (`id`),
  KEY `IDX_SALARY_BONUS_SALARY` (`salary`),
  KEY `IDX_SALARY_BONUS_DOMAIN` (`domain`),
  CONSTRAINT `FK_SALARY_BONUS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALARY_BONUS_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bonificaciones';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `salary_cost`
--

DROP TABLE IF EXISTS `salary_cost`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `salary_cost` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios',
  `amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de deduccion Salarial',
  `cost_concept` varchar(10) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del concepto',
  PRIMARY KEY (`id`),
  KEY `IDX_SALARY_COST_SALARY` (`salary`),
  KEY `IDX_SALARY_COST_DOMAIN` (`domain`),
  CONSTRAINT `FK_SALARY_COST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALARY_COST_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=785461 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Costos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `salary_data`
--

DROP TABLE IF EXISTS `salary_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `salary_data` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `expression` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios',
  PRIMARY KEY (`id`),
  KEY `IDX_SALARY_DATA_DOMAIN` (`domain`),
  KEY `IDX_SALARY_DATA_SALARY` (`salary`),
  CONSTRAINT `FK_SALARY_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALARY_DATA_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=998212 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos de la nomina';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `salary_deduction`
--

DROP TABLE IF EXISTS `salary_deduction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `salary_deduction` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de deducción Salarial',
  `deduction_concept` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del concepto',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `expression` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Fórmula',
  `amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe',
  PRIMARY KEY (`id`),
  KEY `IDX_SALARY_DEDUCTION_SALARY` (`salary`),
  KEY `IDX_SALARY_DEDUCTION_DOMAIN` (`domain`),
  CONSTRAINT `FK_SALARY_DEDUCTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALARY_DEDUCTION_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=208884 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Deducciones';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `salary_embargo`
--

DROP TABLE IF EXISTS `salary_embargo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `salary_embargo` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios',
  `contract_embargo` int(4) NOT NULL COMMENT 'Embargo',
  `amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  PRIMARY KEY (`id`),
  KEY `IDX_SALARY_EMBARGO_CONTRACT_EMBARGO` (`contract_embargo`),
  KEY `IDX_SALARY_EMBARGO_SALARY` (`salary`),
  KEY `IDX_SALARY_EMBARGO_DOMAIN` (`domain`),
  CONSTRAINT `FK_SALARY_EMBARGO_CONTRACT_EMBARGO` FOREIGN KEY (`contract_embargo`) REFERENCES `contract_embargo` (`id`),
  CONSTRAINT `FK_SALARY_EMBARGO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALARY_EMBARGO_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Embargos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `salary_payment`
--

DROP TABLE IF EXISTS `salary_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `salary_payment` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Percepción Salarial',
  `payment_concept` varchar(25) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo',
  `description` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `expression` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Fórmula',
  `amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe',
  `irpf` double(15,3) DEFAULT '0.000' COMMENT 'Importe I.R.P.F',
  `quote` double(15,3) DEFAULT '0.000' COMMENT 'Importe Cotizable',
  PRIMARY KEY (`id`),
  KEY `IDX_SALARY_PAYMENT_SALARY` (`salary`),
  KEY `IDX_SALARY_PAYMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_SALARY_PAYMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALARY_PAYMENT_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=225458 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Percepciones salariales';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sales`
--

DROP TABLE IF EXISTS `sales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sales` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Pedido de Venta',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project` int(4) DEFAULT NULL COMMENT 'Identificador del Proyecto',
  `customer` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Cliente',
  `series` char(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie del Pedido',
  `number` int(4) NOT NULL DEFAULT '0' COMMENT 'Numero del Pedido',
  `purchase_reference` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de referencia del Pedido de Compra',
  `shipping_address` int(4) DEFAULT NULL COMMENT 'Identificador de la Direccion de envio del Pedido',
  `seller` int(4) DEFAULT NULL COMMENT 'Identificador del Agente Comercial',
  `discount_expr` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descuentos del Pedido',
  `issue_date` date DEFAULT NULL COMMENT 'Fecha de emision del Pedido',
  `pay_method` int(4) DEFAULT NULL COMMENT 'Identificador de la Forma de Pago',
  `document_type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de Pedido',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad del Pedido',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado del Pedido',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios del Pedido',
  `remarks` text COLLATE latin1_spanish_ci COMMENT 'Observaciones del Pedido',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `scope` int(4) NOT NULL DEFAULT '1' COMMENT 'Ambito del Pedido',
  `number_of_pymnts` smallint(2) DEFAULT '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint(2) DEFAULT '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint(2) DEFAULT '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Dias de pago',
  `bank_account` varchar(34) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'IBAN - Numero de Cuenta Bancaria Internacional',
  `bank_alias` varchar(25) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias del Banco',
  `bic` varchar(11) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'BIC - Codigo Identificador del Banco',
  `purchase_generated` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si se han generado los Pedidos de Compra derivados',
  `delivery_date` date DEFAULT NULL COMMENT 'Fecha de entrega',
  `carrier` int(4) DEFAULT NULL COMMENT 'Identificador de la Agencia de Transporte',
  `carrier_packing` int(4) DEFAULT NULL COMMENT 'Identificador de la Hoja de ruta',
  `shipping_alternative_address` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Primera parte de la Direccion de entrega',
  `shipping_alternative_address2` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Segunda parte de la Direccion de entrega',
  `shipping_alternative_zip` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo Postal de entrega',
  `shipping_alternative_city` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Localidad de entrega',
  `shipping_alternative_phone` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono de contacto de la entrega',
  `shipping_alternative_recipient` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Destinatario de la entrega',
  `shipping_contact` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del contacto para la entrega',
  `shipping_period` tinyint(2) DEFAULT '0' COMMENT 'Tipo de periodo de entrega',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Pedidos de Venta';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sales_detail`
--

DROP TABLE IF EXISTS `sales_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sales_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Detalle del Pedido de Venta',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `sales` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Pedido de Venta',
  `line` smallint(2) DEFAULT '1' COMMENT 'Numero de linea del Detalle dentro del Pedido',
  `item` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Articulo del Detalle de Pedido',
  `description` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Detalle de Pedido',
  `quantity` double(15,3) DEFAULT '0.000' COMMENT 'Cantidad del Detalle de Pedido',
  `price` double DEFAULT '0' COMMENT 'Precio del Detalle de Pedido',
  `discount_expr` varchar(32) COLLATE latin1_spanish_ci DEFAULT '0' COMMENT 'Descuentos del Detalle de Pedido',
  `taxes` double(15,3) DEFAULT '0.000' COMMENT 'Tasas del Detalle de Pedido',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado del Detalle de Pedido',
  `offer_detail` int(4) DEFAULT NULL COMMENT 'Identificador del Detalle del Presupuesto Origen',
  `delivered` double DEFAULT '0' COMMENT 'Cantidad entregada del Detalle de Pedido',
  `delivery_date` date DEFAULT NULL COMMENT 'Fecha de entrega',
  `carrier` int(4) DEFAULT NULL COMMENT 'Identificador de la Agencia de Transporte',
  `carrier_packing` int(4) DEFAULT NULL COMMENT 'Identificador de la Hoja de ruta',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_SALES_DETAIL_OFFER_DETAIL` (`offer_detail`),
  KEY `IDX_SALES_DETAIL_SALES` (`sales`),
  KEY `IDX_SALES_DETAIL_ITEM` (`item`),
  KEY `IDX_SALES_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_SALES_DETAIL_CARRIER` (`carrier`),
  KEY `IDX_SALES_DETAIL_CARRIER_PACKING` (`carrier_packing`),
  CONSTRAINT `FK_SALES_DETAIL_CARRIER` FOREIGN KEY (`carrier`) REFERENCES `carrier` (`registry`),
  CONSTRAINT `FK_SALES_DETAIL_CARRIER_PACKING` FOREIGN KEY (`carrier_packing`) REFERENCES `carrier_packing` (`id`),
  CONSTRAINT `FK_SALES_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALES_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_SALES_DETAIL_OFFER_DETAIL` FOREIGN KEY (`offer_detail`) REFERENCES `offer_detail` (`id`),
  CONSTRAINT `FK_SALES_DETAIL_SALES` FOREIGN KEY (`sales`) REFERENCES `sales` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Pedido de Venta';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scope`
--

DROP TABLE IF EXISTS `scope`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scope` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(16) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Ambito',
  PRIMARY KEY (`id`),
  KEY `IDX_SCOPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_SCOPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3540 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ambitos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `segment`
--

DROP TABLE IF EXISTS `segment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `segment` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Segmento',
  PRIMARY KEY (`id`),
  KEY `IDX_SEGMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_SEGMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Segmentos Comerciales';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `seller`
--

DROP TABLE IF EXISTS `seller`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `seller` (
  `registry` int(4) NOT NULL DEFAULT '0' COMMENT 'Registro del Agente Comercial',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `commission_type` int(4) DEFAULT NULL COMMENT 'Identificador del Tipo de Comision',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado del Agente Comercial',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  PRIMARY KEY (`registry`),
  KEY `IDX_SELLER_COMMISSION_TYPE` (`commission_type`),
  KEY `IDX_SELLER_DOMAIN` (`domain`),
  KEY `IDX_SELLER_SCOPE` (`scope`),
  CONSTRAINT `FK_SELLER_COMMISSION_TYPE` FOREIGN KEY (`commission_type`) REFERENCES `commission_type` (`id`),
  CONSTRAINT `FK_SELLER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SELLER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_SELLER_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Agentes Comerciales';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sepe_batch_attach`
--

DROP TABLE IF EXISTS `sepe_batch_attach`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sepe_batch_attach` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Archivo Adjunto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `source_batch` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la remesa',
  `source_type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de la remesa',
  `mimeType` tinyint(2) DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Archivo Adjunto',
  `scope` int(4) DEFAULT NULL COMMENT 'Ambito del Archivo Adjunto',
  `attach_date` date DEFAULT NULL COMMENT 'Fecha del Archivo Adjunto',
  `driveId` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_SEPE_BATCH_ATTACH_SCOPE` (`scope`),
  KEY `IDX_SEPE_BATCH_ATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_SEPE_BATCH_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SEPE_BATCH_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de remesas de SEPE';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `series`
--

DROP TABLE IF EXISTS `series`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `series` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `code` varchar(5) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Serie',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `description` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Serie',
  `tas` tinyint(1) DEFAULT '0' COMMENT 'Indica si es una Serie para Ordenes de Reparacion',
  `offer` tinyint(1) DEFAULT '0' COMMENT 'Indica si es una Serie para Presupuestos',
  `sales` tinyint(1) DEFAULT '0' COMMENT 'Indica si es una Serie para Pedidos',
  `delivery` tinyint(1) DEFAULT '0' COMMENT 'Indica si es una Serie para Albaranes',
  `invoice` tinyint(1) DEFAULT '0' COMMENT 'Indica si es una Serie para Facturas',
  `rectification` tinyint(1) DEFAULT '0' COMMENT 'Indica si es una Serie para Facturas rectificativas',
  `pos` tinyint(1) DEFAULT '0' COMMENT 'Indica si es una Serie para TPV',
  `security_level` tinyint(2) NOT NULL COMMENT 'Nivel de seguridad de la Serie',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Serie esta activa o no',
  PRIMARY KEY (`id`),
  KEY `IDX_SERIES_DOMAIN` (`domain`),
  KEY `IDX_SERIES_SCOPE` (`scope`),
  CONSTRAINT `FK_SERIES_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SERIES_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Series';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `session`
--

DROP TABLE IF EXISTS `session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `session` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `endDate` datetime DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `remote_address` varchar(15) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'IP remota',
  `remote_host` varchar(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Equipo remoto',
  `session_id` varchar(128) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificador web de la sesión',
  `startDate` datetime NOT NULL COMMENT 'Fecha de inicio',
  `application` int(4) NOT NULL COMMENT 'Identificador de la Aplicacion',
  `user_id` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Usuario',
  PRIMARY KEY (`id`),
  KEY `IDX_SESSION_APPLICATION` (`application`),
  KEY `IDX_SESSION_USER` (`user_id`),
  KEY `IDX_SESSION_DOMAIN` (`domain`),
  CONSTRAINT `FK_SESSION_APPLICATION` FOREIGN KEY (`application`) REFERENCES `application` (`id`),
  CONSTRAINT `FK_SESSION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SESSION_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Sesion web';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `signature`
--

DROP TABLE IF EXISTS `signature`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `signature` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Firma',
  `signature` text COLLATE latin1_spanish_ci NOT NULL COMMENT 'Texto de la Firma de la Cuenta de Correo',
  `user_id` int(4) DEFAULT NULL COMMENT 'Identificador del Usuario',
  PRIMARY KEY (`id`),
  KEY `IDX_SIGNATURE_DOMAIN` (`domain`),
  KEY `IDX_SIGNATURE_USER` (`user_id`),
  CONSTRAINT `FK_SIGNATURE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SIGNATURE_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Firmas de Cuentas de Correo Electronico';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stock`
--

DROP TABLE IF EXISTS `stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stock` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Stock',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `warehouse` int(4) DEFAULT NULL COMMENT 'Identificador del Almacen',
  `item` int(4) DEFAULT NULL COMMENT 'Identificador del Articulo',
  `quantity` double(15,3) DEFAULT '0.000' COMMENT 'Cantidad del Articulo en el Almacen',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_STOCK_WAREHOUSE_ITEM` (`warehouse`,`item`),
  KEY `IDX_STOCK_WAREHOUSE` (`warehouse`),
  KEY `IDX_STOCK_ITEM` (`item`),
  KEY `IDX_STOCK_DOMAIN` (`domain`),
  CONSTRAINT `FK_STOCK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_STOCK_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_STOCK_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Stocks de Almacenes';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stop_sales`
--

DROP TABLE IF EXISTS `stop_sales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stop_sales` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `hotel` int(4) NOT NULL COMMENT 'Identificador del Hotel',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio del Paro',
  `end_date` date NOT NULL COMMENT 'Fecha de fin del Paro',
  `remarks` text COLLATE latin1_spanish_ci COMMENT 'Comentarios',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si el Paro esta activo o no',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_STOP_SALES_DOMAIN` (`domain`),
  KEY `IDX_STOP_SALES_HOTEL` (`hotel`),
  CONSTRAINT `FK_STOP_SALES_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_STOP_SALES_HOTEL` FOREIGN KEY (`hotel`) REFERENCES `hotel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Paros de venta';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stop_sales_item`
--

DROP TABLE IF EXISTS `stop_sales_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stop_sales_item` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `stop_sales` int(4) NOT NULL COMMENT 'Identificador del Paro de ventas',
  `item` int(4) NOT NULL COMMENT 'Identificador del Tipo de Habitacion',
  PRIMARY KEY (`id`),
  KEY `IDX_STOP_SALES_ITEM_DOMAIN` (`domain`),
  KEY `IDX_STOP_SALES_ITEM_STOP_SALES` (`stop_sales`),
  KEY `IDX_STOP_SALES_ITEM_ITEM` (`item`),
  CONSTRAINT `FK_STOP_SALES_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_STOP_SALES_ITEM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_STOP_SALES_ITEM_STOP_SALES` FOREIGN KEY (`stop_sales`) REFERENCES `stop_sales` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Habitacion por Paro';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stop_sales_tariff`
--

DROP TABLE IF EXISTS `stop_sales_tariff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stop_sales_tariff` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `stop_sales` int(4) NOT NULL COMMENT 'Identificador del Paro de ventas',
  `tariff` int(4) NOT NULL COMMENT 'Identificador de la Tarifa',
  PRIMARY KEY (`id`),
  KEY `IDX_STOP_SALES_TARIFF_DOMAIN` (`domain`),
  KEY `IDX_STOP_SALES_TARIFF_STOP_SALES` (`stop_sales`),
  KEY `IDX_STOP_SALES_TARIFF_TARIFF` (`tariff`),
  CONSTRAINT `FK_STOP_SALES_TARIFF_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_STOP_SALES_TARIFF_STOP_SALES` FOREIGN KEY (`stop_sales`) REFERENCES `stop_sales` (`id`),
  CONSTRAINT `FK_STOP_SALES_TARIFF_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas por Paro';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `supplier`
--

DROP TABLE IF EXISTS `supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `supplier` (
  `registry` int(4) NOT NULL DEFAULT '0' COMMENT 'Registro del Proveedor',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `tariff` int(4) DEFAULT NULL COMMENT 'Tarifa asociada al Proveedor',
  `withholding` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Proveedor aplica retencion de impuestos',
  `withholding_farmer` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Proveedor pertenece al Regimen Especial de Agricultura y Pesca',
  `vat_accrual_payment` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Proveedor esta acogido al Regimen Especial de Criterio de Caja',
  `transaction` tinyint(2) DEFAULT '0' COMMENT 'Tipo de transacciones del Proveedor',
  `status` tinyint(2) DEFAULT NULL COMMENT 'Estado del Proveedor',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `purchase_valuated` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Pedido se imprime valorado segun el Proveedor',
  `account` int(4) DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `survey`
--

DROP TABLE IF EXISTS `survey`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si el Cuestionario esta activa o no',
  `creationDate` datetime NOT NULL COMMENT 'Fecha de creacion del Cuestionario',
  `description` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Cuestionario',
  PRIMARY KEY (`id`),
  KEY `IDX_SURVEY_DOMAIN` (`domain`),
  KEY `IDX_SURVEY_SCOPE` (`scope`),
  CONSTRAINT `FK_SURVEY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SURVEY_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuestionarios';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `survey_question`
--

DROP TABLE IF EXISTS `survey_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_question` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `survey` int(4) NOT NULL COMMENT 'Identificador del Cuestionario',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `position` int(11) DEFAULT NULL COMMENT 'Posicion de la Pregunta dentro del Cuestionario',
  PRIMARY KEY (`id`),
  KEY `IDX_SURVEY_QUESTION_QUESTION` (`question`),
  KEY `IDX_SURVEY_QUESTION_SURVEY` (`survey`),
  KEY `IDX_SURVEY_QUESTION_DOMAIN` (`domain`),
  CONSTRAINT `FK_SURVEY_QUESTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SURVEY_QUESTION_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`),
  CONSTRAINT `FK_SURVEY_QUESTION_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Preguntas de Cuestionarios';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `survey_response`
--

DROP TABLE IF EXISTS `survey_response`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_response` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `creationDate` datetime NOT NULL COMMENT 'Fecha de la creacion en el sistema de la Respuesta del Cuestionario',
  `response_date` datetime NOT NULL COMMENT 'Fecha de la Respuesta del Cuestionario',
  `survey` int(4) NOT NULL COMMENT 'Identificador del Cuestionario',
  `registry` int(4) NOT NULL COMMENT 'Identificador del Registro que responde al Cuestionario',
  `user` int(4) DEFAULT NULL COMMENT 'Identificador del Usuario',
  `campaign_action` int(4) DEFAULT NULL COMMENT 'Identificador de la Accion de la Campaña',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Respuestas de Cuestionarios';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `survey_response_detail`
--

DROP TABLE IF EXISTS `survey_response_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_response_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `value_text` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor de tipo texto',
  `value_number` double(15,3) DEFAULT NULL COMMENT 'Valor de tipo numerico',
  `value_date` datetime DEFAULT NULL COMMENT 'Valor de tipo fecha',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `surveyResponse` int(4) NOT NULL COMMENT 'Identificador de la Respuesta del Cuestionario',
  PRIMARY KEY (`id`),
  KEY `IDX_SURVEY_RESPONSE_DETAIL_QUESTION` (`question`),
  KEY `IDX_SURVEY_RESPONSE_DETAIL_SURVEY_RESPONSE` (`surveyResponse`),
  KEY `IDX_SURVEY_RESPONSE_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_SURVEY_RESPONSE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SURVEY_RESPONSE_DETAIL_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`),
  CONSTRAINT `FK_SURVEY_RESPONSE_DETAIL_SURVEY_RESPONSE` FOREIGN KEY (`surveyResponse`) REFERENCES `survey_response` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Respuestas de Cuestionarios';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `survey_workflow`
--

DROP TABLE IF EXISTS `survey_workflow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_workflow` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `questionValue` int(4) DEFAULT NULL COMMENT 'Identificador del Valor de la Pregunta',
  `surveyQuestion` int(4) NOT NULL COMMENT 'Identificador de la Pregunta del Cuestionario',
  `nextSurveyQuestion` int(4) NOT NULL COMMENT 'Identificador de la siguiente Pregunta del Cuestionario',
  `operator` tinyint(2) DEFAULT NULL COMMENT 'Operador a utilizar con el Valor',
  `value_text` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor de tipo texto',
  `value_number` double(15,3) DEFAULT NULL COMMENT 'Valor de tipo numerico',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Secuencias de Cuestionarios';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_cost`
--

DROP TABLE IF EXISTS `system_cost`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_cost` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `expression` text COLLATE latin1_spanish_ci COMMENT 'Expresion',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Costo',
  `code` varchar(10) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Código',
  PRIMARY KEY (`id`),
  KEY `IDX_SYSTEM_COST_DOMAIN` (`domain`),
  CONSTRAINT `FK_SYSTEM_COST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9755 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Costos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_data`
--

DROP TABLE IF EXISTS `system_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_data` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `expression` text COLLATE latin1_spanish_ci COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `read_only` tinyint(1) DEFAULT NULL COMMENT 'Modificable',
  `comments` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Comentario de ayuda',
  PRIMARY KEY (`id`),
  KEY `IDX_SYSTEM_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_SYSTEM_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24857 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto de las funciones';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_deduction`
--

DROP TABLE IF EXISTS `system_deduction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_deduction` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Deducción',
  `deduction_concept` int(4) DEFAULT NULL COMMENT 'Identificador unico del concepto',
  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `description_decorable` tinyint(2) NOT NULL DEFAULT '0',
  `expression` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Fórmula',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `month` tinyint(2) DEFAULT NULL COMMENT 'Mes de la deducción',
  PRIMARY KEY (`id`),
  KEY `IDX_SYSTEM_DEDUCTION_DEDUCTION_CONCEPT` (`deduction_concept`),
  KEY `IDX_SYSTEM_DEDUCTION_DOMAIN` (`domain`),
  CONSTRAINT `FK_SYSTEM_DEDUCTION_DEDUCTION_CONCEPT` FOREIGN KEY (`deduction_concept`) REFERENCES `deduction_concept` (`id`),
  CONSTRAINT `FK_SYSTEM_DEDUCTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5349 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Deducciones';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_payment`
--

DROP TABLE IF EXISTS `system_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_payment` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Percepción Salarial',
  `payment_concept` int(4) DEFAULT NULL COMMENT 'Identificador unico del concepto',
  `description` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  `description_decorable` tinyint(2) NOT NULL DEFAULT '0',
  `expression` varchar(256) COLLATE latin1_spanish_ci DEFAULT NULL,
  `irpf_expression` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe tributable',
  `quote_expression` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe cotizable',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio',
  `month` tinyint(2) DEFAULT NULL COMMENT 'Mes de la percepcion',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `salary_type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Nomina/Recibo',
  PRIMARY KEY (`id`),
  KEY `IDX_SYSTEM_PAYMENT_PAYMENT_CONCEPT` (`payment_concept`),
  KEY `IDX_SYSTEM_PAYMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_SYSTEM_PAYMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SYSTEM_PAYMENT_PAYMENT_CONCEPT` FOREIGN KEY (`payment_concept`) REFERENCES `payment_concept` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5903 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Percepciones Salariales';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tag` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Etiqueta',
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de Etiqueta',
  `color` varchar(24) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Color de la Etiqueta',
  PRIMARY KEY (`id`),
  KEY `IDX_TAG_DOMAIN` (`domain`),
  CONSTRAINT `FK_TAG_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2403 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Etiquetas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `target`
--

DROP TABLE IF EXISTS `target`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `target` (
  `registry` int(4) NOT NULL DEFAULT '0' COMMENT 'Registro del Cliente Potencial',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `tariff` int(4) DEFAULT NULL COMMENT 'Tarifa asociada al Cliente Potencial',
  `advertising` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Admision de Publicidad',
  `surcharge` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Cliente Potencial tiene recargo de equivalencia',
  `withholding` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Cliente Potencial aplica retencion de impuestos',
  `transaction` tinyint(2) DEFAULT '0' COMMENT 'Tipo de transacciones del Cliente Potencial',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado del Cliente Potencial',
  `scope` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Ambito',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tariff`
--

DROP TABLE IF EXISTS `tariff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tariff` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Tarifa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `code` varchar(8) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de la Tarifa',
  `name` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Tarifa',
  `purchase` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si se trata de una Tarifa de Compras o Ventas',
  `discount` double(6,2) DEFAULT '0.00' COMMENT 'Descuento general de la Tarifa',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si la Tarifa esta activa o no',
  PRIMARY KEY (`id`),
  KEY `IDX_TARIFF_DOMAIN` (`domain`),
  CONSTRAINT `FK_TARIFF_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tariff_addinfo`
--

DROP TABLE IF EXISTS `tariff_addinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tariff_addinfo` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `tariff` int(4) NOT NULL COMMENT 'Identificador de la Tarifa',
  `attribute` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Atributo adicional',
  `value` varchar(128) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Valor del atributo adicional',
  `value_date` date NOT NULL COMMENT 'Fecha del valor del atributo',
  PRIMARY KEY (`id`),
  KEY `IDX_TARIFF_ADDINFO_DOMAIN` (`domain`),
  KEY `IDX_TARIFF_ADDINFO_TARIFF` (`tariff`),
  CONSTRAINT `FK_TARIFF_ADDINFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TARIFF_ADDINFO_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Informacion adicional de la Tarifa';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tariff_catalogue`
--

DROP TABLE IF EXISTS `tariff_catalogue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tariff_catalogue` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `tariff` int(4) NOT NULL COMMENT 'Identificador de la Tarifa',
  `catalogue` int(4) NOT NULL COMMENT 'Identificador del Catalogo',
  PRIMARY KEY (`id`),
  KEY `IDX_TARIFF_CATALOGUE_TARIFF` (`tariff`),
  KEY `IDX_TARIFF_CATALOGUE_CATALOGUE` (`catalogue`),
  KEY `IDX_TARIFF_CATALOGUE_DOMAIN` (`domain`),
  CONSTRAINT `FK_TARIFF_CATALOGUE_CATALOGUE` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`),
  CONSTRAINT `FK_TARIFF_CATALOGUE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TARIFF_CATALOGUE_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas por Catalogo';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tas_item`
--

DROP TABLE IF EXISTS `tas_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tas_item` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Articulo',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `model` int(4) NOT NULL COMMENT 'Identificador del Modelo',
  `publicCode` varchar(25) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo publico del Articulo',
  `privateCode` varchar(25) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo privado del Articulo',
  `description` varchar(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Articulo',
  `add_info` varchar(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Informacion adicional del Articulo',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_TAS_ITEM_DOMAIN_PUBLIC_CODE` (`domain`,`publicCode`),
  UNIQUE KEY `IDX_UNQ_TAS_ITEM_DOMAIN_PRIVATE_CODE` (`domain`,`privateCode`),
  KEY `IDX_TAS_ITEM_MODEL` (`model`),
  KEY `IDX_TAS_ITEM_DOMAIN` (`domain`),
  CONSTRAINT `FK_TAS_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TAS_ITEM_MODEL` FOREIGN KEY (`model`) REFERENCES `model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulo susceptible de Asistencia Tecnica';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task`
--

DROP TABLE IF EXISTS `task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Tarea',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `number` int(4) DEFAULT NULL COMMENT 'Numero de la Tarea',
  `description` varchar(128) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Tarea',
  `start_date` datetime NOT NULL COMMENT 'Fecha de inicio de la Tarea',
  `end_date` datetime DEFAULT NULL COMMENT 'Fecha de finalizacion de la Tarea',
  `due_date` datetime DEFAULT NULL COMMENT 'Fecha de vencimiento de la Tarea',
  `priority` tinyint(2) DEFAULT '0' COMMENT 'Prioridad de la Tarea',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado de la Tarea',
  `percent` tinyint(2) DEFAULT '0' COMMENT 'Porcentaje de realizacion de la Tarea',
  `task_holder` int(4) DEFAULT NULL COMMENT 'Identificador del Usuario asociado a la Tarea',
  `workgroup` int(4) DEFAULT NULL COMMENT 'Identificador del Grupo de Trabajo asociado a la Tarea',
  `source` tinyint(2) DEFAULT NULL COMMENT 'Origen de la Tarea',
  `source_id` int(4) DEFAULT NULL COMMENT 'Identificador del source',
  `project` int(4) DEFAULT NULL COMMENT 'Identificador del Expediente',
  `registry` int(4) DEFAULT NULL,
  `activity_type` int(4) DEFAULT NULL COMMENT 'Identificador de la Actividad',
  `sender` int(4) DEFAULT NULL COMMENT 'Remitente de la Tarea',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Tarea',
  `repeat_period` tinyint(2) DEFAULT '0' COMMENT 'Periodo de repeticion de la Tarea',
  `gtask_id` varchar(100) COLLATE latin1_spanish_ci DEFAULT NULL,
  `gtasklist_id` varchar(100) COLLATE latin1_spanish_ci DEFAULT NULL,
  `parent` int(4) DEFAULT NULL COMMENT 'Task parent',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tareas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_comment`
--

DROP TABLE IF EXISTS `task_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_comment` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `task` int(4) NOT NULL COMMENT 'Identificador de la tarea',
  `comment` text COLLATE latin1_spanish_ci COMMENT 'Comentario de la Tarea',
  `source` int(4) DEFAULT NULL COMMENT 'Origen del comentario',
  `source_id` int(4) DEFAULT NULL COMMENT 'Identificador del origen',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_TASK_COMMENT_DOMAIN` (`domain`),
  KEY `IDX_TASK_COMMENT_TASK` (`task`),
  CONSTRAINT `FK_TASK_COMMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TASK_COMMENT_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Tareas y Comentarios';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_event`
--

DROP TABLE IF EXISTS `task_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_event` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `task` int(4) NOT NULL COMMENT 'Identificador de la tarea',
  `event` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Evento de la Tarea',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_TASK_EVENT_DOMAIN` (`domain`),
  KEY `IDX_TASK_EVENT_TASK` (`task`),
  CONSTRAINT `FK_TASK_EVENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TASK_EVENT_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Tareas y Eventos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_holder`
--

DROP TABLE IF EXISTS `task_holder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_holder` (
  `registry` int(4) NOT NULL DEFAULT '0' COMMENT 'Registro de la Entidad susceptible de Recibir Tareas',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de Entidad susceptible de Recibir Tareas',
  `active` tinyint(1) DEFAULT '1' COMMENT 'Indica si dicha Entidad esta activa o no',
  `user_id` int(4) DEFAULT NULL COMMENT 'Identificador del Usuario',
  `cost_profile` int(4) DEFAULT NULL COMMENT 'Identificador del Perfil de Costos',
  PRIMARY KEY (`registry`),
  KEY `IDX_TASK_HOLDER_COST_PROFILE` (`cost_profile`),
  KEY `IDX_TASK_HOLDER_USER` (`user_id`),
  KEY `IDX_TASK_HOLDER_DOMAIN` (`domain`),
  CONSTRAINT `FK_TASK_HOLDER_COST_PROFILE` FOREIGN KEY (`cost_profile`) REFERENCES `cost_profile` (`id`),
  CONSTRAINT `FK_TASK_HOLDER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TASK_HOLDER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_TASK_HOLDER_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Titulares de Tareas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_holder_workgroup`
--

DROP TABLE IF EXISTS `task_holder_workgroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_holder_workgroup` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `task_holder` int(4) NOT NULL COMMENT 'Identificador del Responsable de la Tarea',
  `workgroup` int(4) NOT NULL COMMENT 'Identificador del Grupo de Trabajo',
  PRIMARY KEY (`id`),
  KEY `IDX_TASK_HOLDER_WORKGROUP_TASK_HOLDER` (`task_holder`),
  KEY `IDX_TASK_HOLDER_WORKGROUP_WORKGROUP` (`workgroup`),
  KEY `IDX_TASK_HOLDER_WORKGROUP_DOMAIN` (`domain`),
  CONSTRAINT `FK_TASK_HOLDER_WORKGROUP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TASK_HOLDER_WORKGROUP_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`),
  CONSTRAINT `FK_TASK_HOLDER_WORKGROUP_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Usuarios y Grupos de Trabajo';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_tag`
--

DROP TABLE IF EXISTS `task_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_tag` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `task` int(4) NOT NULL COMMENT 'Identificador de la tarea',
  `tag` int(4) NOT NULL COMMENT 'Identificador de la Etiqueta',
  PRIMARY KEY (`id`),
  KEY `IDX_TASK_TAG_DOMAIN` (`domain`),
  KEY `IDX_TASK_TAG_TASK` (`task`),
  KEY `IDX_TASK_TAG_TAG` (`tag`),
  CONSTRAINT `FK_TASK_TAG_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TASK_TAG_TAG` FOREIGN KEY (`tag`) REFERENCES `tag` (`id`),
  CONSTRAINT `FK_TASK_TAG_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Tareas y Etiquetas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tax`
--

DROP TABLE IF EXISTS `tax`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tax` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Impuesto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(30) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Impuesto',
  `tax_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de Impuesto',
  `percentage` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Porcentaje de recargo actual',
  `surcharge` double(15,3) DEFAULT '0.000' COMMENT 'Porcentaje de recargo de equivalencia actual',
  `start_date` date DEFAULT NULL COMMENT 'Fecha de inicio de vigencia',
  `vat_deduction_type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de deduccion del IVA',
  `withholding_type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de retencion',
  `sales_account` int(4) DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable de Ventas',
  `purchase_account` int(4) DEFAULT NULL COMMENT 'Identificador de la Cuenta Contable de Compras',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_TAX_DOMAIN` (`domain`),
  KEY `IDX_TAX_ACCOUNT_SALES` (`sales_account`),
  KEY `IDX_TAX_ACCOUNT_PURCHASE` (`purchase_account`),
  CONSTRAINT `FK_TAX_ACCOUNT_PURCHASE` FOREIGN KEY (`purchase_account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_TAX_ACCOUNT_SALES` FOREIGN KEY (`sales_account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_TAX_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15357 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Impuestos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tax_detail`
--

DROP TABLE IF EXISTS `tax_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tax_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Historico de Impuestos',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `tax` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Impuesto',
  `start_date` date DEFAULT NULL COMMENT 'Fecha de inicio de vigencia',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de fin de vigencia',
  `value` double(15,3) DEFAULT NULL COMMENT 'Porcentaje de recargo',
  `surcharge` double(15,3) DEFAULT NULL COMMENT 'Porcentaje de recargo de equivalencia',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_TAX_DETAIL_TAX` (`tax`),
  KEY `IDX_TAX_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_TAX_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TAX_DETAIL_TAX` FOREIGN KEY (`tax`) REFERENCES `tax` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15157 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Historico de Impuestos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `training_center`
--

DROP TABLE IF EXISTS `training_center`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `training_center` (
  `registry` int(4) NOT NULL COMMENT 'Registro del Centro Formativo',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `code` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del centro formativo',
  PRIMARY KEY (`registry`),
  KEY `IDX_TRAINING_CENTER_DOMAIN` (`domain`),
  CONSTRAINT `FK_TRAINING_CENTER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TRAINING_CENTER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Centros Formativos acreditados';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `training_course`
--

DROP TABLE IF EXISTS `training_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `training_course` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `training_center` int(4) NOT NULL COMMENT 'Identificador del Centro Formativo',
  `code` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del Curso Formativo',
  `certification_name` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Denominacion de la certificacion',
  `cno` int(4) DEFAULT NULL COMMENT 'CNO',
  `modality` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Modalidad del Curso Formativo',
  `fp_title_name` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Titulo de formacion profesional',
  `occupation_name` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre de la ocupacion',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cursos de los Centros Formativos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Usuario',
  `login` varchar(16) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Login del Usuario',
  `enterprise` int(4) DEFAULT NULL COMMENT 'Identificador de la Empresa',
  `registry` int(4) DEFAULT NULL COMMENT 'Identificador del Registry',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si el Usuario esta activo o no',
  `allowConcurrent` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Usuario admite Sesiones concurrentes',
  `password` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Contraseña del Usuario',
  `passwordExpiration` date DEFAULT NULL COMMENT 'Fecha de Expiracion de la Contrasea',
  `toolbar` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de Barra de Herramientas del Usuario',
  `locale` varchar(8) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Locale del Usuario',
  `pageLimit` int(4) DEFAULT NULL COMMENT 'Limite de filas en pantalla del Usuario',
  `linesPageLimit` int(4) DEFAULT NULL COMMENT 'Limite de filas en pantalla en lineas del Usuario',
  `initAction` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre la acicn de inicio del Usuario',
  `lastAccess` datetime DEFAULT NULL COMMENT 'Fecha del ultimo acceso del Usuario',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNQ_USER_DOMAIN_LOGIN` (`domain`,`login`),
  KEY `IDX_USER_ENTERPRISE` (`enterprise`),
  KEY `IDX_USER_REGISTRY` (`registry`),
  KEY `IDX_USER_DOMAIN` (`domain`),
  CONSTRAINT `FK_USER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_USER_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`),
  CONSTRAINT `FK_USER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10249 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Usuarios';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_scope`
--

DROP TABLE IF EXISTS `user_scope`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_scope` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `user_id` int(4) NOT NULL COMMENT 'Identificador del Usuario',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  PRIMARY KEY (`id`),
  KEY `IDX_USER_SCOPE_SCOPE` (`scope`),
  KEY `IDX_USER_SCOPE_USER` (`user_id`),
  KEY `IDX_USER_SCOPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_USER_SCOPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_USER_SCOPE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_USER_SCOPE_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12186 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ambitos de Usuario';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_workgroup`
--

DROP TABLE IF EXISTS `user_workgroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_workgroup` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `user_id` int(4) NOT NULL COMMENT 'Identificador del Usuario',
  `workgroup` int(4) NOT NULL COMMENT 'Identificador del Grupo de Trabajo',
  PRIMARY KEY (`id`),
  KEY `IDX_USER_WORKGROUP_USER` (`user_id`),
  KEY `IDX_USER_WORKGROUP_WORKGROUP` (`workgroup`),
  KEY `IDX_USER_WORKGROUP_DOMAIN` (`domain`),
  CONSTRAINT `FK_USER_WORKGROUP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_USER_WORKGROUP_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_USER_WORKGROUP_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5251 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Usuarios y Grupos de Trabajo';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `warehouse`
--

DROP TABLE IF EXISTS `warehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `warehouse` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Almacen',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Almacen',
  `workplace` int(4) DEFAULT NULL COMMENT 'Identificador del Centro de Trabajo',
  `department` int(4) DEFAULT NULL COMMENT 'Identificador del Departamento',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si el Almacen esta activo o no',
  PRIMARY KEY (`id`),
  KEY `IDX_WAREHOUSE_WORKPLACE` (`workplace`),
  KEY `IDX_WAREHOUSE_DOMAIN` (`domain`),
  KEY `IDX_WAREHOUSE_DEPARTMENT` (`department`),
  CONSTRAINT `FK_WAREHOUSE_DEPARTMENT` FOREIGN KEY (`department`) REFERENCES `department` (`id`),
  CONSTRAINT `FK_WAREHOUSE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_WAREHOUSE_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2365 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Almacenes';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `warehouse_transfer`
--

DROP TABLE IF EXISTS `warehouse_transfer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `warehouse_transfer` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `series` char(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie del Traspaso',
  `number` int(4) NOT NULL DEFAULT '0' COMMENT 'Numero del Traspaso',
  `issue_time` datetime NOT NULL COMMENT 'Fecha de emision del Traspaso',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios del Traspaso',
  `source_warehouse` int(4) DEFAULT NULL COMMENT 'Identificador del Almacen Origen',
  `target_warehouse` int(4) DEFAULT NULL COMMENT 'Identificador del Almacen Destino',
  `inventory` int(4) DEFAULT NULL COMMENT 'Identificador del Inventario',
  `source` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Origen',
  `source_id` int(4) DEFAULT NULL COMMENT 'Identificador del origen',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Traspasos entre Almacenes';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `warehouse_transfer_detail`
--

DROP TABLE IF EXISTS `warehouse_transfer_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `warehouse_transfer_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `warehouse_transfer` int(4) NOT NULL COMMENT 'Identificador del Traspaso',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo del Detalle de Traspaso',
  `quantity` double(15,3) DEFAULT NULL COMMENT 'Cantidad del Detalle de Traspaso',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_WAREHOUSE_TRANSFER_DETAIL_ITEM` (`item`),
  KEY `IDX_WAREHOUSE_TRANSFER_DETAIL_WAREHOUSE_TRANSFER` (`warehouse_transfer`),
  KEY `IDX_WAREHOUSE_TRANSFER_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_WAREHOUSE_TRANSFER_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_WAREHOUSE_TRANSFER_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_WAREHOUSE_TRANSFER_DETAIL_WAREHOUSE_TRANSFER` FOREIGN KEY (`warehouse_transfer`) REFERENCES `warehouse_transfer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Traspasos entre Almacenes';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `web_info`
--

DROP TABLE IF EXISTS `web_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_info` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador Unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `company` int(4) NOT NULL COMMENT 'Empresa',
  `commercial_description` text COLLATE latin1_spanish_ci COMMENT 'Descripcion comercial',
  `schedule` text COLLATE latin1_spanish_ci COMMENT 'Horario',
  `slogan` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Slogan',
  PRIMARY KEY (`id`),
  KEY `IDX_WEB_INFO_COMPANY` (`company`),
  KEY `IDX_WEB_INFO_DOMAIN` (`domain`),
  CONSTRAINT `FK_WEB_INFO_COMPANY` FOREIGN KEY (`company`) REFERENCES `company` (`registry`),
  CONSTRAINT `FK_WEB_INFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Informacion de la empresa que se mostrara en la ficha web';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `web_info_page`
--

DROP TABLE IF EXISTS `web_info_page`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_info_page` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Codigo de la Pagina',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre de la Pagina.',
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de Pagina',
  `position` tinyint(2) DEFAULT NULL COMMENT 'Posicion de la Pagina en el menu',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si la Pagina esta activa o no',
  PRIMARY KEY (`id`),
  KEY `IDX_WEB_INFO_PAGE_DOMAIN` (`domain`),
  CONSTRAINT `FK_WEB_INFO_PAGE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Paginas pertenecientes a la ficha web';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `web_info_page_detail`
--

DROP TABLE IF EXISTS `web_info_page_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_info_page_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Codigo del Detalle de la Pagina',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `web_info_page` int(4) NOT NULL COMMENT 'Identificador de la Pagina a la que corresponde el detalle',
  `title` varchar(255) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Titulo del contenido de la Pagina',
  `layout` int(2) DEFAULT NULL COMMENT 'Tipo de plantilla',
  `content` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Texto del contenido de la Pagina',
  `extra` varchar(255) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Campo reservado a otros datos de la Pagina',
  PRIMARY KEY (`id`),
  KEY `IDX_WEB_INFO_PAGE_DETAIL_WEB_INFO_PAGE` (`web_info_page`),
  KEY `IDX_WEB_INFO_PAGE_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_WEB_INFO_PAGE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_WEB_INFO_PAGE_DETAIL_WEB_INFO_PAGE` FOREIGN KEY (`web_info_page`) REFERENCES `web_info_page` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Detalles de la pagina perteneciente a la ficha web';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `web_info_page_resource`
--

DROP TABLE IF EXISTS `web_info_page_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_info_page_resource` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Codigo del Recurso de la Pagina',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `web_info_page` int(4) NOT NULL COMMENT 'Codigo de la Pagina',
  `rattach` int(4) NOT NULL COMMENT 'Identificador del Archivo Adjunto calificado como Recurso',
  `content` varchar(255) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Texto del Recurso',
  PRIMARY KEY (`id`),
  KEY `IDX_WEB_INFO_PAGE_RESOURCE_WEB_INFO_PAGE` (`web_info_page`),
  KEY `IDX_WEB_INFO_PAGE_RESOURCE_RATTACH` (`rattach`),
  KEY `IDX_WEB_INFO_PAGE_RESOURCE_DOMAIN` (`domain`),
  CONSTRAINT `FK_WEB_INFO_PAGE_RESOURCE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_WEB_INFO_PAGE_RESOURCE_RATTACH` FOREIGN KEY (`rattach`) REFERENCES `rattach` (`id`),
  CONSTRAINT `FK_WEB_INFO_PAGE_RESOURCE_WEB_INFO_PAGE` FOREIGN KEY (`web_info_page`) REFERENCES `web_info_page` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Recursos de la pagina perteneciente a la ficha web';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `web_info_style`
--

DROP TABLE IF EXISTS `web_info_style`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_info_style` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Codigo del Estilo de la Pagina',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `variable` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre de la variable del Estilo',
  `value` varchar(255) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor de la variable del Estilo',
  PRIMARY KEY (`id`),
  KEY `IDX_WEB_INFO_STYLE_DOMAIN` (`domain`),
  CONSTRAINT `FK_WEB_INFO_STYLE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Estilos a utilizar en las plantillas para generar ficha web';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `workgroup`
--

DROP TABLE IF EXISTS `workgroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `workgroup` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Grupo de Trabajo',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Grupo de Trabajo',
  `status` tinyint(2) DEFAULT NULL COMMENT 'Estado del grupo de Trabajo',
  PRIMARY KEY (`id`),
  KEY `IDX_WORKGROUP_DOMAIN` (`domain`),
  CONSTRAINT `FK_WORKGROUP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3206 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Grupos de Trabajo';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `workplace`
--

DROP TABLE IF EXISTS `workplace`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `workplace` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Centro de Trabajo',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int(4) NOT NULL DEFAULT '1' COMMENT 'Empresa asociada al Centro de Trabajo',
  `description` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Centro de Trabajo',
  `address` int(4) NOT NULL COMMENT 'Identificador de la Direccion',
  `customer` int(4) DEFAULT NULL COMMENT 'Identificador del Cliente',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `economicAgreement` tinyint(2) DEFAULT '0' COMMENT 'Concierto Economico del Centro de Trabajo',
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
) ENGINE=InnoDB AUTO_INCREMENT=8406 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Centros de Trabajo';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `workplace_department`
--

DROP TABLE IF EXISTS `workplace_department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `workplace_department` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `department` int(4) NOT NULL COMMENT 'Identificador del Departamento',
  `warehouse` int(4) DEFAULT NULL COMMENT 'Identificador del Almacen',
  `catalogue` int(4) DEFAULT NULL COMMENT 'Identificador del Catalogo',
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
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-10-01 14:59:07
