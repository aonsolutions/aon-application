# MySQL dump 10.11
#
# Host: localhost    Database: payroll-esferalia-org
# ###########################
# Server version	5.0.77

SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT;
SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS;
SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION;
SET NAMES utf8;
SET @OLD_TIME_ZONE=@@TIME_ZONE;
SET TIME_ZONE='+00:00';
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO';
SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0;

#
# Current Database: `payroll-esferalia-org`
#

CREATE DATABASE `aon_master` ;

USE `aon_master`;

#
# Table structure for table `absence`
#

DROP TABLE IF EXISTS `absence`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
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
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `absence`
#

LOCK TABLES `absence` WRITE;
ALTER TABLE `absence` DISABLE KEYS;
ALTER TABLE `absence` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `academic_skill`
#

DROP TABLE IF EXISTS `academic_skill`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `academic_skill` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Aptitud Academica',
  `code` char(5) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo de la Aptitud Academica',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de la Aptitud Academica',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Aptitudes Academicas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `academic_skill`
#

LOCK TABLES `academic_skill` WRITE;
ALTER TABLE `academic_skill` DISABLE KEYS;
ALTER TABLE `academic_skill` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `academic_year`
#

DROP TABLE IF EXISTS `academic_year`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `academic_year` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Ao Academico',
  `description` varchar(9) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Ao Academico',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ao Academico';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `academic_year`
#

LOCK TABLES `academic_year` WRITE;
ALTER TABLE `academic_year` DISABLE KEYS;
ALTER TABLE `academic_year` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `account`
#

DROP TABLE IF EXISTS `account`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `code` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo Cuenta Contable',
  `description` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Cuenta',
  `alias` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Alias de la Cuenta',
  `entryEnabled` tinyint(2) default '0' COMMENT 'Indica si la Cuenta permite o no Apuntes',
  `level` tinyint(2) NOT NULL default '0' COMMENT 'Nivel de la Cuenta',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_UNQ_ACCOUNT_DOMAIN_CODE` (`domain`,`code`),
  KEY `IDX_ACCOUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1017 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `account`
#

LOCK TABLES `account` WRITE;
ALTER TABLE `account` DISABLE KEYS;
INSERT INTO `account` VALUES (1,1,'1','FINANCIACIN BSICA',NULL,0,1),(2,1,'10','CAPITAL.',NULL,0,2),(3,1,'100','Capital social.',NULL,0,3),(4,1,'1000','Capital social.',NULL,0,4),(5,1,'101','Fondo social.',NULL,0,3),(6,1,'1010','Fondo social.',NULL,0,4),(7,1,'102','Capital.',NULL,0,3),(8,1,'1020','Capital.',NULL,0,4),(9,1,'103','Socios por desembolsos no exigidos..',NULL,0,3),(10,1,'1030','Socios por desembolsos no exigidos, capital social.',NULL,0,4),(11,1,'1034','Socios por desembolsos no exigidos, capital pendiente de inscripcin.',NULL,0,4),(12,1,'104','Socios por aportaciones no dinerarias pendientes..',NULL,0,3),(13,1,'1040','Socios por aportaciones no dinerarias pendientes, capital social.',NULL,0,4),(14,1,'1044','Socios por aportaciones no dinerarias pendientes, capital pendiente de inscripcin.',NULL,0,4),(15,1,'108','Acciones o participaciones propias en situaciones especiales.',NULL,0,3),(16,1,'1080','Acciones o participaciones propias en situaciones especiales.',NULL,0,4),(17,1,'109','Acciones o participaciones propias para reduccin de capital.',NULL,0,3),(18,1,'1090','Acciones o participaciones propias para reduccin de capital.',NULL,0,4),(19,1,'11','RESERVAS.',NULL,0,2),(20,1,'110','Prima de emisin o asuncin.',NULL,0,3),(21,1,'1100','Prima de emisin o asuncin.',NULL,0,4),(22,1,'111','Patrimonio neto por emisin de instrumentos financieros compuestos.',NULL,0,3),(23,1,'1110','Patrimonio neto por emisin de instrumentos financieros compuestos.',NULL,0,4),(24,1,'1111','Resto de instrumentos de patrimonio neto.',NULL,0,4),(25,1,'112','Reserva legal.',NULL,0,3),(26,1,'1120','Reserva legal.',NULL,0,4),(27,1,'113','Reservas voluntarias.',NULL,0,3),(28,1,'1130','Reservas voluntarias.',NULL,0,4),(29,1,'114','Reservas especiales.',NULL,0,3),(30,1,'1140','Reservas para acciones o participaciones de la sociedad dominante.',NULL,0,4),(31,1,'1141','Reservas estatutarias.',NULL,0,4),(32,1,'1142','Reserva por capital amortizado.',NULL,0,4),(33,1,'1143','Reserva por fondo de comercio.',NULL,0,4),(34,1,'1144','Reservas por acciones propias aceptadas en garanta.',NULL,0,4),(35,1,'115','Reservas por prdidas y ganancias actuariales y otros ajustes.',NULL,0,3),(36,1,'1150','Reservas por prdidas y ganancias actuariales y otros ajustes.',NULL,0,4),(37,1,'118','Aportaciones de socios o propietarios.',NULL,0,3),(38,1,'1180','Aportaciones de socios o propietarios.',NULL,0,4),(39,1,'119','Diferencias por ajuste del capital a euros.',NULL,0,3),(40,1,'1190','Diferencias por ajuste del capital a euros.',NULL,0,4),(41,1,'12','RESULTADOS PENDIENTES DE APLICACIN.',NULL,0,2),(42,1,'120','Remanente.',NULL,0,3),(43,1,'1200','Remanente.',NULL,0,4),(44,1,'121','Resultados negativos de ejercicios anteriores.',NULL,0,3),(45,1,'1210','Resultados negativos de ejercicios anteriores.',NULL,0,4),(46,1,'129','Resultados del ejercicio.',NULL,0,3),(47,1,'1290','Resultados del ejercicio.',NULL,0,4),(48,1,'129000000','Resultados del ejercicio.',NULL,1,5),(49,1,'13','SUBVENCIONES, DONACIONES Y AJUSTES POR CAMBIOS DE VALOR.',NULL,0,2),(50,1,'130','Subvenciones oficiales de capital.',NULL,0,3),(51,1,'1300','Subvenciones oficiales de capital.',NULL,0,4),(52,1,'131','Donaciones y legados de capital.',NULL,0,3),(53,1,'1310','Donaciones y legados de capital.',NULL,0,4),(54,1,'132','Otras subvenciones, donaciones y legados.',NULL,0,3),(55,1,'1320','Otras subvenciones, donaciones y legados.',NULL,0,4),(56,1,'133','Ajustes por valoracin en instrumentos financieros.',NULL,0,3),(57,1,'1330','Ajustes por valoracin en instrumentos financieros.',NULL,0,4),(58,1,'134','Operaciones de cobertura.',NULL,0,3),(59,1,'1340','Cobertura de flujos de efectivo.',NULL,0,4),(60,1,'1341','Cobertura de una inversin neta en un negocio en el extranjero.',NULL,0,4),(61,1,'135','Diferencias de conversin.',NULL,0,3),(62,1,'1350','Diferencias de conversin.',NULL,0,4),(63,1,'136','Ajustes por valoracin en activos no corrientes y grupos enajenables de elementos mantenidos para la venta.',NULL,0,3),(64,1,'1360','Ajustes por valoracin en activos no corrientes y grupos enajenables de elementos mantenidos para la venta.',NULL,0,4),(65,1,'137','Ingresos fiscales a distribuir en varios ejercicios.',NULL,0,3),(66,1,'1370','Ingresos fiscales por diferencias permanentes a distribuir en varios ejercicios.',NULL,0,4),(67,1,'1371','Ingresos fiscales por deducciones y bonificaciones a distribuir en varios ejercicios.',NULL,0,4),(68,1,'14','PROVISIONES.',NULL,0,2),(69,1,'140','Provisin para retribuciones y otras prestaciones al personal.',NULL,0,3),(70,1,'1400','Provisin para retribuciones y otras prestaciones al personal.',NULL,0,4),(71,1,'141','Provisin para impuestos.',NULL,0,3),(72,1,'1410','Provisin para impuestos.',NULL,0,4),(73,1,'142','Provisin para otras responsabilidades.',NULL,0,3),(74,1,'1420','Provisin para otras responsabilidades.',NULL,0,4),(75,1,'143','Provisin por desmantelamiento, retiro o rehabilitacin del inmovilizado.',NULL,0,3),(76,1,'1430','Provisin por desmantelamiento, retiro o rehabilitacin del inmovilizado.',NULL,0,4),(77,1,'145','Provisin para actuaciones medioambientales.',NULL,0,3),(78,1,'1450','Provisin para actuaciones medioambientales.',NULL,0,4),(79,1,'146','Provisin para reestructuraciones.',NULL,0,3),(80,1,'1460','Provisin para reestructuraciones.',NULL,0,4),(81,1,'147','Provisiones por transacciones con pagos basados en instrumentos de patrimonio.',NULL,0,3),(82,1,'1470','Provisiones por transacciones con pagos basados en instrumentos de patrimonio.',NULL,0,4),(83,1,'15','DEUDAS A LARGO PLAZO CON CARACTERSTICAS ESPECIALES.',NULL,0,2),(84,1,'150','Acciones o participaciones a largo plazo contabilizadas como pasivo.',NULL,0,3),(85,1,'1500','Acciones o participaciones a largo plazo contabilizadas como pasivo.',NULL,0,4),(86,1,'153','Desembolsos no exigidos por acciones o participaciones contabilizadas como pasivo.',NULL,0,3),(87,1,'1530','Desembolsos no exigidos por acciones o participaciones contabilizadas como pasivo.',NULL,0,4),(88,1,'154','Aportaciones no dinerarias pendientes por acciones o participaciones contabilizadas como pasivo.',NULL,0,3),(89,1,'1540','Aportaciones no dinerarias pendientes por acciones o participaciones contabilizadas como pasivo.',NULL,0,4),(90,1,'16','DEUDAS A LARGO PLAZO CON PARTES VINCULADAS.',NULL,0,2),(91,1,'160','Deudas a largo plazo con entidades de crdito vinculadas.',NULL,0,3),(92,1,'1600','Deudas a largo plazo con entidades de crdito vinculadas.',NULL,0,4),(93,1,'161','Proveedores de inmovilizado a largo plazo, partes vinculadas.',NULL,0,3),(94,1,'1610','Proveedores de inmovilizado a largo plazo, partes vinculadas.',NULL,0,4),(95,1,'162','Otras deudas a largo plazo con partes vinculadas.',NULL,0,3),(96,1,'1620','Otras deudas a largo plazo con partes vinculadas.',NULL,0,4),(97,1,'163','Otras deudas a largo plazo con partes vinculadas.',NULL,0,3),(98,1,'1633','Otras deudas a largo plazo empresas del grupo.',NULL,0,4),(99,1,'1634','Otras deudas a largo plazo empresas asociadas.',NULL,0,4),(100,1,'1635','Otras deudas a largo plazo con otras partes vinculadas.',NULL,0,4),(101,1,'17','DEUDAS A LARGO PLAZO POR PRSTAMOS RECIBIDOS Y OTROS CONCEPTOS.',NULL,0,2),(102,1,'170','Deudas a largo plazo con entidades de crdito.',NULL,0,3),(103,1,'1700','Deudas a largo plazo con entidades de crdito.',NULL,0,4),(104,1,'171','Deudas a largo plazo.',NULL,0,3),(105,1,'1710','Deudas a largo plazo.',NULL,0,4),(106,1,'172','Deudas a largo plazo transformables en subvenciones, donaciones y legados.',NULL,0,3),(107,1,'1720','Deudas a largo plazo transformables en subvenciones, donaciones y legados.',NULL,0,4),(108,1,'173','Proveedores de inmovilizado a largo plazo.',NULL,0,3),(109,1,'1730','Proveedores de inmovilizado a largo plazo.',NULL,0,4),(110,1,'174','Acreedores de arrendamiento financiero a largo plazo.',NULL,0,3),(111,1,'1740','Acreedores de arrendamiento financiero a largo plazo.',NULL,0,4),(112,1,'175','Efectos a pagar a largo plazo.',NULL,0,3),(113,1,'1750','Efectos a pagar a largo plazo.',NULL,0,4),(114,1,'176','Pasivos por derivados financieros a largo plazo.',NULL,0,3),(115,1,'1765','Pasivos por derivados financieros a largo plazo, cartera de negociacin.',NULL,0,4),(116,1,'1768','Pasivos por derivados financieros a largo plazo, instrumentos de cobertura.',NULL,0,4),(117,1,'177','Obligaciones y bonos.',NULL,0,3),(118,1,'1770','Obligaciones y bonos.',NULL,0,4),(119,1,'178','Obligaciones y bonos convertibles.',NULL,0,3),(120,1,'1780','Obligaciones y bonos convertibles.',NULL,0,4),(121,1,'179','Deudas representadas en otros valores negociables.',NULL,0,3),(122,1,'1790','Deudas representadas en otros valores negociables.',NULL,0,4),(123,1,'18','PASIVOS POR FIANZAS Y GARANTAS Y OTROS CONCEPTOS A LARGO PLAZO.',NULL,0,2),(124,1,'180','Fianzas recibidas a largo plazo.',NULL,0,3),(125,1,'1800','Fianzas recibidas a largo plazo.',NULL,0,4),(126,1,'181','Anticipos recibidos por ventas o prestaciones de servicios a largo plazo.',NULL,0,3),(127,1,'1810','Anticipos recibidos por ventas o prestaciones de servicios a largo plazo.',NULL,0,4),(128,1,'185','Depsitos recibidos a largo plazo.',NULL,0,3),(129,1,'1850','Depsitos recibidos a largo plazo.',NULL,0,4),(130,1,'189','Garantas financieras a largo plazo.',NULL,0,3),(131,1,'1890','Garantas financieras a largo plazo.',NULL,0,4),(132,1,'19','SITUACIONES TRANSITORIAS DE FINANCIACIN.',NULL,0,2),(133,1,'190','Acciones o participaciones emitidas.',NULL,0,3),(134,1,'1900','Acciones o participaciones emitidas.',NULL,0,4),(135,1,'192','Suscriptores de acciones.',NULL,0,3),(136,1,'1920','Suscriptores de acciones.',NULL,0,4),(137,1,'194','Capital emitido pendiente de inscripcin.',NULL,0,3),(138,1,'1940','Capital emitido pendiente de inscripcin.',NULL,0,4),(139,1,'195','Acciones o participaciones emitidas consideradas como pasivos financieros.',NULL,0,3),(140,1,'1950','Acciones o participaciones emitidas consideradas como pasivos financieros.',NULL,0,4),(141,1,'197','Suscriptores de acciones consideradas como pasivos financieros.',NULL,0,3),(142,1,'1970','Suscriptores de acciones consideradas como pasivos financieros.',NULL,0,4),(143,1,'199','Acciones o participaciones emitidas consideradas como pasivos financieros pendientes de inscripcin.',NULL,0,3),(144,1,'1990','Acciones o participaciones emitidas consideradas como pasivos financieros pendientes de inscripcin.',NULL,0,4),(145,1,'2','ACTIVO NO CORRIENTE',NULL,0,1),(146,1,'20','INMOVILIZACIONES INTANGIBLES.',NULL,0,2),(147,1,'200','Gastos de investigacin.',NULL,0,3),(148,1,'2000','Gastos de investigacin.',NULL,0,4),(149,1,'201','Desarrollo.',NULL,0,3),(150,1,'2010','Desarrollo.',NULL,0,4),(151,1,'202','Concesiones administrativas.',NULL,0,3),(152,1,'2020','Concesiones administrativas.',NULL,0,4),(153,1,'203','Propiedad industrial.',NULL,0,3),(154,1,'2030','Propiedad industrial.',NULL,0,4),(155,1,'204','Fondo de comercio.',NULL,0,3),(156,1,'2040','Fondo de comercio.',NULL,0,4),(157,1,'205','Derechos de traspaso.',NULL,0,3),(158,1,'2050','Derechos de traspaso.',NULL,0,4),(159,1,'206','Aplicaciones informticas.',NULL,0,3),(160,1,'2060','Aplicaciones informticas.',NULL,0,4),(161,1,'209','Anticipos para inmovilizaciones intangibles.',NULL,0,3),(162,1,'2090','Anticipos para inmovilizaciones intangibles.',NULL,0,4),(163,1,'21','INMOVILIZACIONES MATERIALES.',NULL,0,2),(164,1,'210','Terrenos y bienes naturales.',NULL,0,3),(165,1,'2100','Terrenos y bienes naturales.',NULL,0,4),(166,1,'211','Construcciones .',NULL,0,3),(167,1,'2110','Construcciones .',NULL,0,4),(168,1,'212','Instalaciones tcnicas.',NULL,0,3),(169,1,'2120','Instalaciones tcnicas.',NULL,0,4),(170,1,'213','Maquinaria.',NULL,0,3),(171,1,'2130','Maquinaria.',NULL,0,4),(172,1,'214','Utillaje.',NULL,0,3),(173,1,'2140','Utillaje.',NULL,0,4),(174,1,'215','Otras instalaciones.',NULL,0,3),(175,1,'2150','Otras instalaciones.',NULL,0,4),(176,1,'216','Mobiliario.',NULL,0,3),(177,1,'2160','Mobiliario.',NULL,0,4),(178,1,'217','Equipos para procesos de informacin.',NULL,0,3),(179,1,'2170','Equipos para procesos de informacin.',NULL,0,4),(180,1,'218','Elementos de transporte.',NULL,0,3),(181,1,'2180','Elementos de transporte.',NULL,0,4),(182,1,'219','Otro inmovilizado material.',NULL,0,3),(183,1,'2190','Otro inmovilizado material.',NULL,0,4),(184,1,'22','INVERSIONES INMOBILIARIAS.',NULL,0,2),(185,1,'220','Inversiones en terrenos y bienes naturales.',NULL,0,3),(186,1,'2200','Inversiones en terrenos y bienes naturales.',NULL,0,4),(187,1,'221','Inversiones en construcciones.',NULL,0,3),(188,1,'2210','Inversiones en construcciones.',NULL,0,4),(189,1,'23','INMOVILIZACIONES MATERIALES EN CURSO.',NULL,0,2),(190,1,'230','Adaptacin de terrenos y bienes naturales.',NULL,0,3),(191,1,'2300','Adaptacin de terrenos y bienes naturales.',NULL,0,4),(192,1,'231','Construcciones en curso.',NULL,0,3),(193,1,'2310','Construcciones en curso.',NULL,0,4),(194,1,'232','Instalaciones tcnicas en montaje.',NULL,0,3),(195,1,'2320','Instalaciones tcnicas en montaje.',NULL,0,4),(196,1,'233','Maquinaria en montaje.',NULL,0,3),(197,1,'2330','Maquinaria en montaje.',NULL,0,4),(198,1,'237','Equipos para procesos de informacin en montaje.',NULL,0,3),(199,1,'2370','Equipos para procesos de informacin en montaje.',NULL,0,4),(200,1,'239','Anticipos para inmovilizaciones materiales.',NULL,0,3),(201,1,'2390','Anticipos para inmovilizaciones materiales.',NULL,0,4),(202,1,'24','INVERSIONES FINANCIERAS A LARGO PLAZO EN PARTES VINCULADAS.',NULL,0,2),(203,1,'240','Participaciones a largo plazo en partes vinculadas.',NULL,0,3),(204,1,'2400','Participaciones a largo plazo en partes vinculadas.',NULL,0,4),(205,1,'241','Valores representativos de deuda a largo plazo de partes vinculadas.',NULL,0,3),(206,1,'2410','Valores representativos de deuda a largo plazo de partes vinculadas.',NULL,0,4),(207,1,'242','Crditos a largo plazo a partes vinculadas.',NULL,0,3),(208,1,'2420','Crditos a largo plazo a partes vinculadas.',NULL,0,4),(209,1,'249','Desembolsos pendientes sobre participaciones a largo plazo en partes vinculadas.',NULL,0,3),(210,1,'2490','Desembolsos pendientes sobre participaciones a largo plazo en partes vinculadas.',NULL,0,4),(211,1,'25','OTRAS INVERSIONES FINANCIERAS A LARGO PLAZO.',NULL,0,2),(212,1,'250','Inversiones financieras a largo plazo en instrumentos de patrimonio.',NULL,0,3),(213,1,'2500','Inversiones financieras a largo plazo en instrumentos de patrimonio.',NULL,0,4),(214,1,'251','Valores representativos de deuda a largo plazo.',NULL,0,3),(215,1,'2510','Valores representativos de deuda a largo plazo.',NULL,0,4),(216,1,'252','Crditos a largo plazo.',NULL,0,3),(217,1,'2520','Crditos a largo plazo.',NULL,0,4),(218,1,'253','Crditos a largo plazo por enajenacin de inmovilizado.',NULL,0,3),(219,1,'2530','Crditos a largo plazo por enajenacin de inmovilizado.',NULL,0,4),(220,1,'254','Crditos a largo plazo al personal.',NULL,0,3),(221,1,'2540','Crditos a largo plazo al personal.',NULL,0,4),(222,1,'255','Activos por derivados financieros a largo plazo.',NULL,0,3),(223,1,'2550','Activos por derivados financieros a largo plazo.',NULL,0,4),(224,1,'257','Activos por retribuciones a largo plazo de prestacin definida.',NULL,0,3),(225,1,'2570','Activos por retribuciones a largo plazo de prestacin definida.',NULL,0,4),(226,1,'258','Imposiciones a largo plazo.',NULL,0,3),(227,1,'2580','Imposiciones a largo plazo.',NULL,0,4),(228,1,'259','Desembolsos pendientes sobre participaciones en el patrimonio neto a largo plazo.',NULL,0,3),(229,1,'2590','Desembolsos pendientes sobre participaciones en el patrimonio neto a largo plazo.',NULL,0,4),(230,1,'26','FIANZAS Y DEPSITOS CONSTITUIDOS A LARGO PLAZO.',NULL,0,2),(231,1,'260','Fianzas constituidas a largo plazo.',NULL,0,3),(232,1,'2600','Fianzas constituidas a largo plazo.',NULL,0,4),(233,1,'265','Depsitos constituidos a largo plazo.',NULL,0,3),(234,1,'2650','Depsitos constituidos a largo plazo.',NULL,0,4),(235,1,'28','AMORTIZACIN ACUMULADA DEL INMOVILIZADO.',NULL,0,2),(236,1,'280','Amortizacin acumulada del inmovilizado intangible.',NULL,0,3),(237,1,'2800','Amortizacin acumulada del inmovilizado intangible.',NULL,0,4),(238,1,'2801','Amortizacin acumulada de investigacin',NULL,0,4),(239,1,'2802','Amortizacin acumulada de desarrollo',NULL,0,4),(240,1,'2803','Amortizacin acumulada de concesiones administrativas',NULL,0,4),(241,1,'2804','Amortizacin acumulada de propiedad industrial',NULL,0,4),(242,1,'2805','Amortizacin acumulada de derechos de traspaso',NULL,0,4),(243,1,'2806','Amortizacin acumulada de aplicaciones informticas',NULL,0,4),(244,1,'281','Amortizacin acumulada del inmovilizado material.',NULL,0,3),(245,1,'2810','Amortizacin acumulada del inmovilizado material.',NULL,0,4),(246,1,'2811','Amortizacin acumulada de construcciones',NULL,0,4),(247,1,'2812','Amortizacin acumulada de instalaciones tcnicas',NULL,0,4),(248,1,'2813','Amortizacin acumulada de maquinaria',NULL,0,4),(249,1,'2814','Amortizacin acumulada de utillaje',NULL,0,4),(250,1,'2815','Amortizacin acumulada de otras instalaciones',NULL,0,4),(251,1,'2816','Amortizacin acumulada de mobiliario',NULL,0,4),(252,1,'2817','Amortizacin acumulada de equipos para el proceso de informacin',NULL,0,4),(253,1,'2818','Amortizacin acumulada de elementos de transporte',NULL,0,4),(254,1,'2819','Amortizacin acumulada de otro inmovilizado material',NULL,0,4),(255,1,'282','Amortizacin acumulada de las inversiones inmobiliarias.',NULL,0,3),(256,1,'2820','Amortizacin acumulada de las inversiones inmobiliarias.',NULL,0,4),(257,1,'29','DETERIORO DE VALOR DE ACTIVOS NO CORRIENTES.',NULL,0,2),(258,1,'290','Deterioro de valor del inmovilizado intangible.',NULL,0,3),(259,1,'2900','Deterioro de valor del inmovilizado intangible.',NULL,0,4),(260,1,'291','Deterioro de valor del inmovilizado material.',NULL,0,3),(261,1,'2910','Deterioro de valor del inmovilizado material.',NULL,0,4),(262,1,'292','Deterioro de valor de las inversiones inmobiliarias.',NULL,0,3),(263,1,'2920','Deterioro de valor de las inversiones inmobiliarias.',NULL,0,4),(264,1,'293','Deterioro de valor de participaciones a largo plazo en partes vinculadas.',NULL,0,3),(265,1,'2930','Deterioro de valor de participaciones a largo plazo en partes vinculadas.',NULL,0,4),(266,1,'294','Deterioro de valor de valores representativos de deuda a largo plazo de partes vinculadas.',NULL,0,3),(267,1,'2940','Deterioro de valor de valores representativos de deuda a largo plazo de partes vinculadas.',NULL,0,4),(268,1,'295','Deterioro de valor de crditos a largo plazo a partes vinculadas.',NULL,0,3),(269,1,'2950','Deterioro de valor de crditos a largo plazo a partes vinculadas.',NULL,0,4),(270,1,'297','Deterioro de valor de valores representativos de deuda a largo plazo.',NULL,0,3),(271,1,'2970','Deterioro de valor de valores representativos de deuda a largo plazo.',NULL,0,4),(272,1,'298','Deterioro de valor de crditos a largo plazo.',NULL,0,3),(273,1,'2980','Deterioro de valor de crditos a largo plazo.',NULL,0,4),(274,1,'3','EXISTENCIAS',NULL,0,1),(275,1,'30','COMERCIALES.',NULL,0,2),(276,1,'300','Mercaderas A.',NULL,0,3),(277,1,'3000','Mercaderas A.',NULL,0,4),(278,1,'301','Mercaderas B.',NULL,0,3),(279,1,'3010','Mercaderas B.',NULL,0,4),(280,1,'31','MATERIAS PRIMAS.',NULL,0,2),(281,1,'310','Materias primas A.',NULL,0,3),(282,1,'3100','Materias primas A.',NULL,0,4),(283,1,'311','Materias primas B.',NULL,0,3),(284,1,'3110','Materias primas B.',NULL,0,4),(285,1,'32','OTROS APROVISIONAMIENTOS.',NULL,0,2),(286,1,'320','Elementos conjuntos incorporables.',NULL,0,3),(287,1,'3200','Elementos conjuntos incorporables.',NULL,0,4),(288,1,'321','Combustibles.',NULL,0,3),(289,1,'3210','Combustibles.',NULL,0,4),(290,1,'322','Repuestos.',NULL,0,3),(291,1,'3220','Repuestos.',NULL,0,4),(292,1,'325','Materiales diversos.',NULL,0,3),(293,1,'3250','Materiales diversos.',NULL,0,4),(294,1,'326','Embalajes.',NULL,0,3),(295,1,'3260','Embalajes.',NULL,0,4),(296,1,'327','Envases.',NULL,0,3),(297,1,'3270','Envases.',NULL,0,4),(298,1,'328','Material de oficina.',NULL,0,3),(299,1,'3280','Material de oficina.',NULL,0,4),(300,1,'33','PRODUCTOS EN CURSO.',NULL,0,2),(301,1,'330','Productos en curso A.',NULL,0,3),(302,1,'3300','Productos en curso A.',NULL,0,4),(303,1,'331','Productos en curso B.',NULL,0,3),(304,1,'3310','Productos en curso B.',NULL,0,4),(305,1,'34','PRODUCTOS SEMITERMINADOS.',NULL,0,2),(306,1,'340','Productos semiterminados A.',NULL,0,3),(307,1,'3400','Productos semiterminados A.',NULL,0,4),(308,1,'341','Productos semiterminados B.',NULL,0,3),(309,1,'3410','Productos semiterminados B.',NULL,0,4),(310,1,'35','PRODUCTOS TERMINADOS.',NULL,0,2),(311,1,'350','Productos terminados A.',NULL,0,3),(312,1,'3500','Productos terminados A.',NULL,0,4),(313,1,'351','Productos terminados B.',NULL,0,3),(314,1,'3510','Productos terminados B.',NULL,0,4),(315,1,'36','SUBPRODUCTOS, RESIDUOS Y MATERIALES RECUPERADOS.',NULL,0,2),(316,1,'360','Subproductos A.',NULL,0,3),(317,1,'3600','Subproductos A.',NULL,0,4),(318,1,'361','Subproductos B.',NULL,0,3),(319,1,'3610','Subproductos B.',NULL,0,4),(320,1,'365','Residuos A.',NULL,0,3),(321,1,'3650','Residuos A.',NULL,0,4),(322,1,'366','Residuos B.',NULL,0,3),(323,1,'3660','Residuos B.',NULL,0,4),(324,1,'368','Materiales recuperados A.',NULL,0,3),(325,1,'3680','Materiales recuperados A.',NULL,0,4),(326,1,'369','Materiales recuperados B.',NULL,0,3),(327,1,'3690','Materiales recuperados B.',NULL,0,4),(328,1,'39','DETERIORO DE VALOR DE LAS EXISTENCIAS.',NULL,0,2),(329,1,'390','Deterioro de valor de las mercaderas.',NULL,0,3),(330,1,'3900','Deterioro de valor de las mercaderas.',NULL,0,4),(331,1,'391','Deterioro de valor de las materias primas.',NULL,0,3),(332,1,'3910','Deterioro de valor de las materias primas.',NULL,0,4),(333,1,'392','Deterioro de valor de otros aprovisionamientos.',NULL,0,3),(334,1,'3920','Deterioro de valor de otros aprovisionamientos.',NULL,0,4),(335,1,'393','Deterioro de valor de los productos en curso.',NULL,0,3),(336,1,'3930','Deterioro de valor de los productos en curso.',NULL,0,4),(337,1,'394','Deterioro de valor de los productos semiterminados.',NULL,0,3),(338,1,'3940','Deterioro de valor de los productos semiterminados.',NULL,0,4),(339,1,'395','Deterioro de valor de los productos terminados.',NULL,0,3),(340,1,'3950','Deterioro de valor de los productos terminados.',NULL,0,4),(341,1,'396','Deterioro de valor de los subproductos, residuos y materiales recuperados.',NULL,0,3),(342,1,'3960','Deterioro de valor de los subproductos, residuos y materiales recuperados.',NULL,0,4),(343,1,'4','ACREEDORES Y DEUDORES POR OPERACIONES COMERCIALES',NULL,0,1),(344,1,'40','PROVEEDORES.',NULL,0,2),(345,1,'400','Proveedores.',NULL,0,3),(346,1,'4000','Proveedores.',NULL,0,4),(347,1,'401','Proveedores, efectos comerciales a pagar.',NULL,0,3),(348,1,'4010','Proveedores, efectos comerciales a pagar.',NULL,0,4),(349,1,'403','Proveedores, empresas del grupo.',NULL,0,3),(350,1,'4030','Proveedores, empresas del grupo.',NULL,0,4),(351,1,'404','Proveedores, empresas asociadas.',NULL,0,3),(352,1,'4040','Proveedores, empresas asociadas.',NULL,0,4),(353,1,'405','Proveedores, otras partes vinculadas.',NULL,0,3),(354,1,'4050','Proveedores, otras partes vinculadas.',NULL,0,4),(355,1,'406','Envases y embalajes a devolver a proveedores.',NULL,0,3),(356,1,'4060','Envases y embalajes a devolver a proveedores.',NULL,0,4),(357,1,'407','Anticipos a proveedores.',NULL,0,3),(358,1,'4070','Anticipos a proveedores.',NULL,0,4),(359,1,'41','ACREEDORES VARIOS.',NULL,0,2),(360,1,'410','Acreedores por prestaciones de servicios.',NULL,0,3),(361,1,'4100','Acreedores por prestaciones de servicios.',NULL,0,4),(362,1,'411','Acreedores, efectos comerciales a pagar.',NULL,0,3),(363,1,'4110','Acreedores, efectos comerciales a pagar.',NULL,0,4),(364,1,'419','Acreedores por operaciones en comn.',NULL,0,3),(365,1,'4190','Acreedores por operaciones en comn.',NULL,0,4),(366,1,'43','CLIENTES.',NULL,0,2),(367,1,'430','Clientes.',NULL,0,3),(368,1,'4300','Clientes.',NULL,0,4),(369,1,'431','Clientes, efectos comerciales a cobrar.',NULL,0,3),(370,1,'4310','Clientes, efectos comerciales a cobrar.',NULL,0,4),(371,1,'432','Clientes operaciones de factoring.',NULL,0,3),(372,1,'4320','Clientes operaciones de factoring.',NULL,0,4),(373,1,'433','Clientes, empresas del grupo.',NULL,0,3),(374,1,'4330','Clientes, empresas del grupo.',NULL,0,4),(375,1,'434','Clientes, empresas asociadas.',NULL,0,3),(376,1,'4340','Clientes, empresas asociadas.',NULL,0,4),(377,1,'435','Clientes, otras partes vinculadas.',NULL,0,3),(378,1,'4350','Clientes, otras partes vinculadas.',NULL,0,4),(379,1,'436','Clientes de dudoso cobro.',NULL,0,3),(380,1,'4360','Clientes de dudoso cobro.',NULL,0,4),(381,1,'437','Envases y embalajes a devolver por clientes.',NULL,0,3),(382,1,'4370','Envases y embalajes a devolver por clientes.',NULL,0,4),(383,1,'438','Anticipos de clientes.',NULL,0,3),(384,1,'4380','Anticipos de clientes.',NULL,0,4),(385,1,'44','DEUDORES VARIOS.',NULL,0,2),(386,1,'440','Deudores.',NULL,0,3),(387,1,'4400','Deudores.',NULL,0,4),(388,1,'441','Deudores, efectos comerciales a cobrar.',NULL,0,3),(389,1,'4410','Deudores, efectos comerciales a cobrar.',NULL,0,4),(390,1,'446','Deudores de dudoso cobro.',NULL,0,3),(391,1,'4460','Deudores de dudoso cobro.',NULL,0,4),(392,1,'449','Deudores por operaciones en comn.',NULL,0,3),(393,1,'4490','Deudores por operaciones en comn.',NULL,0,4),(394,1,'46','PERSONAL.',NULL,0,2),(395,1,'460','Anticipos de remuneraciones.',NULL,0,3),(396,1,'4600','Anticipos de remuneraciones.',NULL,0,4),(397,1,'465','Remuneraciones pendientes de pago.',NULL,0,3),(398,1,'4650','Remuneraciones pendientes de pago.',NULL,0,4),(399,1,'465000000','Remuneraciones pendientes de pago.',NULL,1,5),(400,1,'466','Remuneraciones mediante sistemas de aportacin definida pendientes de pago.',NULL,0,3),(401,1,'4660','Remuneraciones mediante sistemas de aportacin definida pendientes de pago.',NULL,0,4),(402,1,'47','ADMINISTRACIONES PBLICAS.',NULL,0,2),(403,1,'470','Hacienda Pblica deudora por diversos conceptos.',NULL,0,3),(404,1,'4700','Hacienda Pblica, deudora por IVA.',NULL,0,4),(405,1,'4708','Hacienda Pblica, deudora por subvenciones concedidas.',NULL,0,4),(406,1,'4709','Hacienda Pblica, deudora por devolucin de impuestos.',NULL,0,4),(407,1,'471','Organismos de la Seguridad Social, deudores.',NULL,0,3),(408,1,'4710','Organismos de la Seguridad Social, deudores.',NULL,0,4),(409,1,'472','Hacienda Pblica, IVA soportado.',NULL,0,3),(410,1,'4720','Hacienda Pblica, IVA soportado.',NULL,0,4),(411,1,'472000000','Hacienda Pblica, IVA soportado.',NULL,1,5),(412,1,'473','Hacienda Pblica, retenciones y pagos a cuenta.',NULL,0,3),(413,1,'4730','Hacienda Pblica, retenciones y pagos a cuenta.',NULL,0,4),(414,1,'473000000','Hacienda Pblica, retenciones y pagos a cuenta.',NULL,1,5),(415,1,'474','Activos por impuesto diferido.',NULL,0,3),(416,1,'4740','Activos por diferencias temporarias deducibles.',NULL,0,4),(417,1,'4742','Derechos por deducciones y bonificaciones pendientes de aplicar.',NULL,0,4),(418,1,'4745','Crdito por prdidas a compensar del ejercicio.',NULL,0,4),(419,1,'475','Hacienda Pblica acreedora por conceptos fiscales.',NULL,0,3),(420,1,'4750','Hacienda Pblica, acreedora por IVA.',NULL,0,4),(421,1,'4751','Hacienda Pblica, acreedora por retenciones practicadas.',NULL,0,4),(422,1,'475100000','Hacienda Pblica, acreedora por retenciones practicadas.',NULL,1,5),(423,1,'4752','Hacienda Pblica, acreedora por impuesto sobre sociedades.',NULL,0,4),(424,1,'4758','Hacienda Pblica, acreedora por subvenciones a reintegrar.',NULL,0,4),(425,1,'476','Organismos de la Seguridad Social, acreedores.',NULL,0,3),(426,1,'4760','Organismos de la Seguridad Social, acreedores.',NULL,0,4),(427,1,'476000000','Organismos de la Seguridad Social, acreedores.',NULL,1,5),(428,1,'477','Hacienda Pblica, IVA repercutido.',NULL,0,3),(429,1,'4770','Hacienda Pblica, IVA repercutido.',NULL,0,4),(430,1,'477000000','Hacienda Pblica, IVA repercutido.',NULL,1,5),(431,1,'479','Pasivos por diferencias temporarias imponibles.',NULL,0,3),(432,1,'4790','Pasivos por diferencias temporarias imponibles.',NULL,0,4),(433,1,'48','AJUSTES POR PERIODIFICACIN.',NULL,0,2),(434,1,'480','Gastos anticipados.',NULL,0,3),(435,1,'4800','Gastos anticipados.',NULL,0,4),(436,1,'485','Ingresos anticipados.',NULL,0,3),(437,1,'4850','Ingresos anticipados.',NULL,0,4),(438,1,'49','DETERIORO DE VALOR DE CRDITOS COMERCIALES Y PROVISIONES A CORTO PLAZO.',NULL,0,2),(439,1,'490','Deterioro de valor de crditos por operaciones comerciales.',NULL,0,3),(440,1,'4900','Deterioro de valor de crditos por operaciones comerciales.',NULL,0,4),(441,1,'493','Deterioro de valor de crditos por operaciones comerciales con partes vinculadas.',NULL,0,3),(442,1,'4930','Deterioro de valor de crditos por operaciones comerciales con partes vinculadas.',NULL,0,4),(443,1,'499','Provisiones por operaciones comerciales.',NULL,0,3),(444,1,'4994','Provisin para contratos onerosos.',NULL,0,4),(445,1,'4999','Provisin para otras operaciones comerciales.',NULL,0,4),(446,1,'5','CUENTAS FINANCIERAS',NULL,0,1),(447,1,'50','EMPRSTITOS, DEUDAS CON CARACTERSTICAS ESPECIALES Y OTRAS EMISIONES ANLOGAS A CORTO PLAZO.',NULL,0,2),(448,1,'500','Obligaciones y bonos a corto plazo.',NULL,0,3),(449,1,'5000','Obligaciones y bonos a corto plazo.',NULL,0,4),(450,1,'501','Obligaciones y bonos convertibles a corto plazo.',NULL,0,3),(451,1,'5010','Obligaciones y bonos convertibles a corto plazo.',NULL,0,4),(452,1,'502','Acciones o participaciones a corto plazo contabilizadas como pasivo.',NULL,0,3),(453,1,'5020','Acciones o participaciones a corto plazo contabilizadas como pasivo.',NULL,0,4),(454,1,'505','Deudas representadas en otros valores negociables a corto plazo.',NULL,0,3),(455,1,'5050','Deudas representadas en otros valores negociables a corto plazo.',NULL,0,4),(456,1,'506','Intereses de emprstitos y otras emisiones anlogas.',NULL,0,3),(457,1,'5060','Intereses de emprstitos y otras emisiones anlogas.',NULL,0,4),(458,1,'507','Dividendos de emisiones contabilizadas como pasivo.',NULL,0,3),(459,1,'5070','Dividendos de emisiones contabilizadas como pasivo.',NULL,0,4),(460,1,'509','Valores negociables amortizados.',NULL,0,3),(461,1,'5090','Valores negociables amortizados.',NULL,0,4),(462,1,'51','DEUDAS A CORTO PLAZO CON PARTES VINCULADAS.',NULL,0,2),(463,1,'510','Deudas a corto plazo con entidades de crdito vinculadas.',NULL,0,3),(464,1,'5100','Deudas a corto plazo con entidades de crdito vinculadas.',NULL,0,4),(465,1,'511','Proveedores de inmovilizado a corto plazo, partes vinculadas.',NULL,0,3),(466,1,'5110','Proveedores de inmovilizado a corto plazo, partes vinculadas.',NULL,0,4),(467,1,'512','Acreedores por arrendamiento financiero a corto plazo, partes vinculadas.',NULL,0,3),(468,1,'5120','Acreedores por arrendamiento financiero a corto plazo, partes vinculadas.',NULL,0,4),(469,1,'513','Otras deudas a corto plazo con partes vinculadas.',NULL,0,3),(470,1,'5130','Otras deudas a corto plazo con partes vinculadas.',NULL,0,4),(471,1,'514','Intereses a corto plazo de deudas con partes vinculadas.',NULL,0,3),(472,1,'5140','Intereses a corto plazo de deudas con partes vinculadas.',NULL,0,4),(473,1,'52','DEUDAS A CORTO PLAZO POR PRSTAMOS RECIBIDOS Y OTROS CONCEPTOS.',NULL,0,2),(474,1,'520','Deudas a corto plazo con entidades de crdito.',NULL,0,3),(475,1,'5200','Prstamos a corto plazo de entidades de crdito.',NULL,0,4),(476,1,'5201','Deudas a corto plazo por crdito dispuesto.',NULL,0,4),(477,1,'5208','Deudas por efectos descontados.',NULL,0,4),(478,1,'5209','Deudas por operaciones de factoring.',NULL,0,4),(479,1,'521','Deudas a corto plazo.',NULL,0,3),(480,1,'5210','Deudas a corto plazo.',NULL,0,4),(481,1,'522','Deudas a corto plazo transformables en subvenciones, donaciones y legados.',NULL,0,3),(482,1,'5220','Deudas a corto plazo transformables en subvenciones, donaciones y legados.',NULL,0,4),(483,1,'523','Proveedores de inmovilizado a corto plazo.',NULL,0,3),(484,1,'5230','Proveedores de inmovilizado a corto plazo.',NULL,0,4),(485,1,'525','Efectos a pagar a corto plazo.',NULL,0,3),(486,1,'5250','Efectos a pagar a corto plazo.',NULL,0,4),(487,1,'526','Dividendo activo a pagar.',NULL,0,3),(488,1,'5260','Dividendo activo a pagar.',NULL,0,4),(489,1,'527','Intereses a corto plazo de deudas con entidades de crdito.',NULL,0,3),(490,1,'5270','Intereses a corto plazo de deudas con entidades de crdito.',NULL,0,4),(491,1,'528','Intereses a corto plazo de deudas.',NULL,0,3),(492,1,'5280','Intereses a corto plazo de deudas.',NULL,0,4),(493,1,'529','Provisiones a corto plazo.',NULL,0,3),(494,1,'5290','Provisiones a corto plazo.',NULL,0,4),(495,1,'53','INVERSIONES FINANCIERAS A CORTO PLAZO EN PARTES VINCULADAS.',NULL,0,2),(496,1,'530','Participaciones a corto plazo en partes vinculadas.',NULL,0,3),(497,1,'5303','Participaciones a corto plazo en empresas del grupo.',NULL,0,4),(498,1,'5304','Participaciones a corto plazo en empresas del asociadas.',NULL,0,4),(499,1,'5305','Participaciones a corto plazo, en otras partes vinculadas.',NULL,0,4),(500,1,'531','Valores representativos de deuda a corto plazo de partes vinculadas.',NULL,0,3),(501,1,'5310','Valores representativos de deuda a corto plazo de partes vinculadas.',NULL,0,4),(502,1,'532','Crditos a corto plazo a partes vinculadas.',NULL,0,3),(503,1,'5320','Crditos a corto plazo a partes vinculadas.',NULL,0,4),(504,1,'533','Intereses a corto plazo de inversiones financieras en partes vinculadas.',NULL,0,3),(505,1,'5330','Intereses a corto plazo de inversiones financieras en partes vinculadas.',NULL,0,4),(506,1,'534','Intereses a corto plazo de crditos a partes vinculadas.',NULL,0,3),(507,1,'5340','Intereses a corto plazo de crditos a partes vinculadas.',NULL,0,4),(508,1,'535','Dividendo a cobrar de inversiones financieras en partes vinculadas.',NULL,0,3),(509,1,'5350','Dividendo a cobrar de inversiones financieras en partes vinculadas.',NULL,0,4),(510,1,'539','Desembolsos pendientes sobre participaciones a corto plazo de partes vinculadas.',NULL,0,3),(511,1,'5390','Desembolsos pendientes sobre participaciones a corto plazo de partes vinculadas.',NULL,0,4),(512,1,'54','OTRAS INVERSIONES FINANCIERAS A CORTO PLAZO.',NULL,0,2),(513,1,'540','Inversiones financieras temporales en instrumentos de patrimonio.',NULL,0,3),(514,1,'5400','Inversiones financieras temporales en instrumentos de patrimonio.',NULL,0,4),(515,1,'541','Valores representativos de deuda a corto plazo.',NULL,0,3),(516,1,'5410','Valores representativos de deuda a corto plazo.',NULL,0,4),(517,1,'542','Crditos a corto plazo.',NULL,0,3),(518,1,'5420','Crditos a corto plazo.',NULL,0,4),(519,1,'543','Crditos a corto plazo por enajenacin de inmovilizado.',NULL,0,3),(520,1,'5430','Crditos a corto plazo por enajenacin de inmovilizado.',NULL,0,4),(521,1,'544','Crditos a corto plazo al personal.',NULL,0,3),(522,1,'5440','Crditos a corto plazo al personal.',NULL,0,4),(523,1,'545','Dividendo a cobrar.',NULL,0,3),(524,1,'5450','Dividendo a cobrar.',NULL,0,4),(525,1,'546','Intereses a corto plazo de valores representativos de deuda.',NULL,0,3),(526,1,'5460','Intereses a corto plazo de valores representativos de deuda.',NULL,0,4),(527,1,'547','Intereses a corto plazo de crditos.',NULL,0,3),(528,1,'5470','Intereses a corto plazo de crditos.',NULL,0,4),(529,1,'548','Imposiciones a corto plazo.',NULL,0,3),(530,1,'5480','Imposiciones a corto plazo.',NULL,0,4),(531,1,'549','Desembolsos pendientes sobre instrumentos de patrimonio a corto plazo.',NULL,0,3),(532,1,'5490','Desembolsos pendientes sobre instrumentos de patrimonio a corto plazo.',NULL,0,4),(533,1,'55','OTRAS CUENTAS NO BANCARIAS.',NULL,0,2),(534,1,'550','Titular de la explotacin.',NULL,0,3),(535,1,'5500','Titular de la explotacin.',NULL,0,4),(536,1,'551','Cuenta corriente con socios administradores.',NULL,0,3),(537,1,'5510','Cuenta corriente con socios administradores.',NULL,0,4),(538,1,'552','Cuenta corriente con otras personas y entidades vinculadas.',NULL,0,3),(539,1,'5520','Cuenta corriente con otras personas y entidades vinculadas.',NULL,0,4),(540,1,'553','Cuentas corrientes en fusiones y escisiones.',NULL,0,3),(541,1,'5530','Socios de sociedad disuelta.',NULL,0,4),(542,1,'5531','Socios, cuenta de fusin.',NULL,0,4),(543,1,'5532','Socios de sociedad escindida.',NULL,0,4),(544,1,'5533','Socios, cuenta de escisin.',NULL,0,4),(545,1,'554','Cuenta corriente con uniones temporales de empresas y comunidades de bienes.',NULL,0,3),(546,1,'5540','Cuenta corriente con uniones temporales de empresas y comunidades de bienes.',NULL,0,4),(547,1,'555','Partidas pendientes de aplicacin.',NULL,0,3),(548,1,'5550','Partidas pendientes de aplicacin.',NULL,0,4),(549,1,'556','Desembolsos exigidos sobre participaciones en el patrimonio neto.',NULL,0,3),(550,1,'5560','Desembolsos exigidos sobre participaciones en el patrimonio neto.',NULL,0,4),(551,1,'557','Dividendo activo a cuenta.',NULL,0,3),(552,1,'5570','Dividendo activo a cuenta.',NULL,0,4),(553,1,'558','Socios por desembolsos exigidos.',NULL,0,3),(554,1,'5580','Socios por desembolsos exigidos sobre acciones o participaciones ordinarias.',NULL,0,4),(555,1,'5585','Socios por desembolsos exigidos sobre acciones o participaciones consideradas como pasivos financieros.',NULL,0,4),(556,1,'559','Derivados financieros a corto plazo.',NULL,0,3),(557,1,'5590','Activos por derivados financieros a corto plazo, cartera de negociacin.',NULL,0,4),(558,1,'5593','Activos de derivados financieros a corto plazo, instrumentos de cobertura.',NULL,0,4),(559,1,'5595','Pasivos por derivados financieros a corto plazo, cartera de negociacin.',NULL,0,4),(560,1,'5598','Pasivos por derivados financieros a corto plazo, instrumentos de cobertura.',NULL,0,4),(561,1,'56','FIANZAS Y DEPSITOS RECIBIDOS Y CONSTITUIDOS A CORTO PLAZO Y AJUSTES POR PERIODIFICACIN.',NULL,0,2),(562,1,'560','Fianzas recibidas a corto plazo.',NULL,0,3),(563,1,'5600','Fianzas recibidas a corto plazo.',NULL,0,4),(564,1,'561','Depsitos recibidos a corto plazo.',NULL,0,3),(565,1,'5610','Depsitos recibidos a corto plazo.',NULL,0,4),(566,1,'565','Fianzas constituidas a corto plazo.',NULL,0,3),(567,1,'5650','Fianzas constituidas a corto plazo.',NULL,0,4),(568,1,'566','Depsitos constituidos a corto plazo.',NULL,0,3),(569,1,'5660','Depsitos constituidos a corto plazo.',NULL,0,4),(570,1,'567','Intereses pagados por anticipado.',NULL,0,3),(571,1,'5670','Intereses pagados por anticipado.',NULL,0,4),(572,1,'568','Intereses cobrados por anticipado.',NULL,0,3),(573,1,'5680','Intereses cobrados por anticipado.',NULL,0,4),(574,1,'569','Garantas financieras a corto plazo.',NULL,0,3),(575,1,'5690','Garantas financieras a corto plazo.',NULL,0,4),(576,1,'57','TESORERA.',NULL,0,2),(577,1,'570','Caja, euros.',NULL,0,3),(578,1,'5700','Caja, euros.',NULL,0,4),(579,1,'570000000','Caja, euros.',NULL,1,5),(580,1,'571','Caja, moneda extranjera.',NULL,0,3),(581,1,'5710','Caja, moneda extranjera.',NULL,0,4),(582,1,'572','Bancos e instituciones de crdito c/c vista, euros.',NULL,0,3),(583,1,'5720','Bancos e instituciones de crdito c/c vista, euros.',NULL,0,4),(584,1,'573','Bancos e instituciones de crdito c/c vista, moneda extranjera.',NULL,0,3),(585,1,'5730','Bancos e instituciones de crdito c/c vista, moneda extranjera.',NULL,0,4),(586,1,'574','Bancos e instituciones de crdito, cuentas de ahorro, euros.',NULL,0,3),(587,1,'5740','Bancos e instituciones de crdito, cuentas de ahorro, euros.',NULL,0,4),(588,1,'575','Bancos e instituciones de crdito, cuentas de ahorro, moneda extranjera.',NULL,0,3),(589,1,'5750','Bancos e instituciones de crdito, cuentas de ahorro, moneda extranjera.',NULL,0,4),(590,1,'576','Inversiones a corto plazo de gran liquidez.',NULL,0,3),(591,1,'5760','Inversiones a corto plazo de gran liquidez.',NULL,0,4),(592,1,'58','ACTIVOS NO CORRIENTES MANTENIDOS PARA LA VENTA Y ACTIVOS Y PASIVOS ASOCIADOS.',NULL,0,2),(593,1,'580','Inmovilizado.',NULL,0,3),(594,1,'5800','Inmovilizado.',NULL,0,4),(595,1,'581','Inversiones con personas y entidades vinculadas.',NULL,0,3),(596,1,'5810','Inversiones con personas y entidades vinculadas.',NULL,0,4),(597,1,'582','Inversiones financieras.',NULL,0,3),(598,1,'5820','Inversiones financieras.',NULL,0,4),(599,1,'583','Existencias, deudores comerciales y otras cuentas a cobrar.',NULL,0,3),(600,1,'5830','Existencias, deudores comerciales y otras cuentas a cobrar.',NULL,0,4),(601,1,'584','Otros activos.',NULL,0,3),(602,1,'5840','Otros activos.',NULL,0,4),(603,1,'585','Provisiones.',NULL,0,3),(604,1,'5850','Provisiones.',NULL,0,4),(605,1,'586','Deudas con caractersticas especiales.',NULL,0,3),(606,1,'5860','Deudas con caractersticas especiales.',NULL,0,4),(607,1,'587','Deudas con personas y entidades vinculadas.',NULL,0,3),(608,1,'5870','Deudas con personas y entidades vinculadas.',NULL,0,4),(609,1,'588','Acreedores comerciales y otras cuentas a pagar.',NULL,0,3),(610,1,'5880','Acreedores comerciales y otras cuentas a pagar.',NULL,0,4),(611,1,'589','Otros pasivos.',NULL,0,3),(612,1,'5890','Otros pasivos.',NULL,0,4),(613,1,'59','DETERIORO DEL VALOR DE INVERSIONES FINANCIERAS A CORTO PLAZO Y DE ACTIVOS NO CORRIENTES MANTENIDOS PARA LA VENTA.',NULL,0,2),(614,1,'593','Deterioro de valor de participaciones a corto plazo en partes vinculadas.',NULL,0,3),(615,1,'5930','Deterioro de valor de participaciones a corto plazo en partes vinculadas.',NULL,0,4),(616,1,'594','Deterioro del valor de valores representativos de deuda a corto plazo de partes vinculadas.',NULL,0,3),(617,1,'5940','Deterioro del valor de valores representativos de deuda a corto plazo de partes vinculadas.',NULL,0,4),(618,1,'595','Deterioro del valor de crditos a corto plazo a partes vinculadas.',NULL,0,3),(619,1,'5950','Deterioro del valor de crditos a corto plazo a partes vinculadas.',NULL,0,4),(620,1,'597','Deterioro de valor de valores representativos de deuda a corto plazo.',NULL,0,3),(621,1,'5970','Deterioro de valor de valores representativos de deuda a corto plazo.',NULL,0,4),(622,1,'598','Deterioro de valor de crditos a corto plazo.',NULL,0,3),(623,1,'5980','Deterioro de valor de crditos a corto plazo.',NULL,0,4),(624,1,'599','Deterioro de valor de activos no corrientes mantenidos para la venta.',NULL,0,3),(625,1,'5990','Deterioro de valor de activos no corrientes mantenidos para la venta.',NULL,0,4),(626,1,'6','COMPRAS Y GASTOS',NULL,0,1),(627,1,'60','COMPRAS.',NULL,0,2),(628,1,'600','Compras de mercaderas.',NULL,0,3),(629,1,'6000','Compras de mercaderas.',NULL,0,4),(630,1,'600000000','Compras de mercaderas.',NULL,1,5),(631,1,'601','Compras de materias primas.',NULL,0,3),(632,1,'6010','Compras de materias primas.',NULL,0,4),(633,1,'602','Compras de otros aprovisionamientos.',NULL,0,3),(634,1,'6020','Compras de otros aprovisionamientos.',NULL,0,4),(635,1,'606','Descuentos sobre compras por pronto pago.',NULL,0,3),(636,1,'6060','Descuentos sobre compras por pronto pago.',NULL,0,4),(637,1,'607','Trabajos realizados por otras empresas.',NULL,0,3),(638,1,'6070','Trabajos realizados por otras empresas.',NULL,0,4),(639,1,'608','Devoluciones de compras y operaciones similares.',NULL,0,3),(640,1,'6080','Devoluciones de compras y operaciones similares.',NULL,0,4),(641,1,'609','Rappels por compras.',NULL,0,3),(642,1,'6090','Rappels por compras.',NULL,0,4),(643,1,'61','VARIACIN DE EXISTENCIAS.',NULL,0,2),(644,1,'610','Variacin de existencias de mercaderas.',NULL,0,3),(645,1,'6100','Variacin de existencias de mercaderas.',NULL,0,4),(646,1,'611','Variacin de existencias de materias primas.',NULL,0,3),(647,1,'6110','Variacin de existencias de materias primas.',NULL,0,4),(648,1,'612','Variacin de existencias de otros aprovisionamientos.',NULL,0,3),(649,1,'6120','Variacin de existencias de otros aprovisionamientos.',NULL,0,4),(650,1,'62','SERVICIOS EXTERIORES.',NULL,0,2),(651,1,'620','Gastos en investigacin y desarrollo del ejercicio.',NULL,0,3),(652,1,'6200','Gastos en investigacin y desarrollo del ejercicio.',NULL,0,4),(653,1,'621','Arrendamientos y cnones.',NULL,0,3),(654,1,'6210','Arrendamientos y cnones.',NULL,0,4),(655,1,'622','Reparaciones y conservacin.',NULL,0,3),(656,1,'6220','Reparaciones y conservacin.',NULL,0,4),(657,1,'623','Servicios de profesionales independientes.',NULL,0,3),(658,1,'6230','Servicios de profesionales independientes.',NULL,0,4),(659,1,'624','Transportes.',NULL,0,3),(660,1,'6240','Transportes.',NULL,0,4),(661,1,'625','Primas de seguros.',NULL,0,3),(662,1,'6250','Primas de seguros.',NULL,0,4),(663,1,'626','Servicios bancarios y similares.',NULL,0,3),(664,1,'6260','Servicios bancarios y similares.',NULL,0,4),(665,1,'627','Publicidad, propaganda y relaciones pblicas.',NULL,0,3),(666,1,'6270','Publicidad, propaganda y relaciones pblicas.',NULL,0,4),(667,1,'628','Suministros.',NULL,0,3),(668,1,'6280','Suministros.',NULL,0,4),(669,1,'629','Otros servicios.',NULL,0,3),(670,1,'6290','Otros servicios.',NULL,0,4),(671,1,'63','TRIBUTOS.',NULL,0,2),(672,1,'630','Impuesto sobre beneficios.',NULL,0,3),(673,1,'6300','Impuesto corriente.',NULL,0,4),(674,1,'6301','Impuesto diferido.',NULL,0,4),(675,1,'631','Otros tributos.',NULL,0,3),(676,1,'6310','Otros tributos.',NULL,0,4),(677,1,'633','Ajustes negativos en la imposicin sobre beneficios.',NULL,0,3),(678,1,'6330','Ajustes negativos en la imposicin sobre beneficios.',NULL,0,4),(679,1,'634','Ajustes negativos en la imposicin indirecta.',NULL,0,3),(680,1,'6340','Ajustes negativos en la imposicin indirecta.',NULL,0,4),(681,1,'636','Devolucin de impuestos.',NULL,0,3),(682,1,'6360','Devolucin de impuestos.',NULL,0,4),(683,1,'638','Ajustes positivos en la imposicin sobre beneficios.',NULL,0,3),(684,1,'6380','Ajustes positivos en la imposicin sobre beneficios.',NULL,0,4),(685,1,'639','Ajustes positivos en la imposicin indirecta.',NULL,0,3),(686,1,'6390','Ajustes positivos en la imposicin indirecta.',NULL,0,4),(687,1,'64','GASTOS DE PERSONAL.',NULL,0,2),(688,1,'640','Sueldos y salarios.',NULL,0,3),(689,1,'6400','Sueldos y salarios.',NULL,0,4),(690,1,'640000000','Sueldos y salarios.',NULL,1,5),(691,1,'641','Indemnizaciones.',NULL,0,3),(692,1,'6410','Indemnizaciones.',NULL,0,4),(693,1,'641000000','Indemnizaciones.',NULL,1,5),(694,1,'642','Seguridad Social a cargo de la empresa.',NULL,0,3),(695,1,'6420','Seguridad Social a cargo de la empresa.',NULL,0,4),(696,1,'642000000','Seguridad Social a cargo de la empresa.',NULL,1,5),(697,1,'643','Retribuciones a largo plazo mediante sistemas de aportacin definida.',NULL,0,3),(698,1,'6430','Retribuciones a largo plazo mediante sistemas de aportacin definida.',NULL,0,4),(699,1,'644','Retribuciones a largo plazo mediante sistemas de prestacin definida.',NULL,0,3),(700,1,'6440','Contribuciones anuales.',NULL,0,4),(701,1,'6442','Otros costes.',NULL,0,4),(702,1,'645','Retribuciones al personal mediante instrumentos de patrimonio.',NULL,0,3),(703,1,'6450','Retribuciones al personal mediante instrumentos de patrimonio.',NULL,0,4),(704,1,'649','Otros gastos sociales.',NULL,0,3),(705,1,'6490','Otros gastos sociales.',NULL,0,4),(706,1,'65','OTROS GASTOS DE GESTIN.',NULL,0,2),(707,1,'650','Prdidas de crditos comerciales incobrables.',NULL,0,3),(708,1,'6500','Prdidas de crditos comerciales incobrables.',NULL,0,4),(709,1,'651','Resultados de operaciones en comn.',NULL,0,3),(710,1,'6510','Beneficio transferido (gestor).',NULL,0,4),(711,1,'6511','Prdida soportada (partcipe o asociado no gestor).',NULL,0,4),(712,1,'659','Otras prdidas en gestin corriente.',NULL,0,3),(713,1,'6590','Otras prdidas en gestin corriente.',NULL,0,4),(714,1,'66','GASTOS FINANCIEROS.',NULL,0,2),(715,1,'660','Gastos financieros por actualizacin de provisiones.',NULL,0,3),(716,1,'6600','Gastos financieros por actualizacin de provisiones.',NULL,0,4),(717,1,'661','Intereses de obligaciones y bonos.',NULL,0,3),(718,1,'6610','Intereses de obligaciones y bonos.',NULL,0,4),(719,1,'662','Intereses de deudas.',NULL,0,3),(720,1,'6620','Intereses de deudas.',NULL,0,4),(721,1,'662000000','Intereses de deudas.',NULL,1,5),(722,1,'663','Prdidas por valoracin de instrumentos financieros por su valor razonable.',NULL,0,3),(723,1,'6630','Prdidas de cartera de negociacin.',NULL,0,4),(724,1,'6631','Prdidas de designados por la empresa.',NULL,0,4),(725,1,'6632','Prdidas de disponibles para la venta.',NULL,0,4),(726,1,'6633','Prdidas de instrumentos de cobertura.',NULL,0,4),(727,1,'664','Dividendos de acciones o participaciones contabilizadas como pasivo.',NULL,0,3),(728,1,'6640','Dividendos de acciones o participaciones contabilizadas como pasivo.',NULL,0,4),(729,1,'665','Intereses por descuento de efectos y operaciones de factoring.',NULL,0,3),(730,1,'6650','Intereses por descuento de efectos y operaciones de factoring.',NULL,0,4),(731,1,'666','Prdidas en participaciones y valores representativos de deuda.',NULL,0,3),(732,1,'6660','Prdidas en participaciones y valores representativos de deuda.',NULL,0,4),(733,1,'667','Prdidas de crditos no comerciales.',NULL,0,3),(734,1,'6670','Prdidas de crditos no comerciales.',NULL,0,4),(735,1,'668','Diferencias negativas de cambio.',NULL,0,3),(736,1,'6680','Diferencias negativas de cambio.',NULL,0,4),(737,1,'669','Otros gastos financieros.',NULL,0,3),(738,1,'6690','Otros gastos financieros.',NULL,0,4),(739,1,'669000000','Otros gastos financieros.',NULL,1,5),(740,1,'67','PRDIDAS PROCEDENTES DE ACTIVOS NO CORRIENTES Y GASTOS EXCEPCIONALES.',NULL,0,2),(741,1,'670','Prdidas procedentes del inmovilizado intangible.',NULL,0,3),(742,1,'6700','Prdidas procedentes del inmovilizado intangible.',NULL,0,4),(743,1,'671','Prdidas procedentes del inmovilizado material.',NULL,0,3),(744,1,'6710','Prdidas procedentes del inmovilizado material.',NULL,0,4),(745,1,'672','Prdidas procedentes de las inversiones inmobiliarias.',NULL,0,3),(746,1,'6720','Prdidas procedentes de las inversiones inmobiliarias.',NULL,0,4),(747,1,'673','Prdidas procedentes de participaciones a largo plazo en partes vinculadas.',NULL,0,3),(748,1,'6730','Prdidas procedentes de participaciones a largo plazo en partes vinculadas.',NULL,0,4),(749,1,'675','Prdidas por operaciones con obligaciones propias.',NULL,0,3),(750,1,'6750','Prdidas por operaciones con obligaciones propias.',NULL,0,4),(751,1,'678','Gastos excepcionales.',NULL,0,3),(752,1,'6780','Gastos excepcionales.',NULL,0,4),(753,1,'68','DOTACIONES PARA AMORTIZACIONES.',NULL,0,2),(754,1,'680','Amortizacin del inmovilizado intangible.',NULL,0,3),(755,1,'6800','Amortizacin del inmovilizado intangible.',NULL,0,4),(756,1,'6801','Amortizacin de investigacin',NULL,0,4),(757,1,'6802','Amortizacin de desarrollo',NULL,0,4),(758,1,'6803','Amortizacin de concesiones administrativas',NULL,0,4),(759,1,'6804','Amortizacin de propiedad industrial',NULL,0,4),(760,1,'6805','Amortizacin de derechos de traspaso',NULL,0,4),(761,1,'6806','Amortizacin de aplicaciones informticas',NULL,0,4),(762,1,'681','Amortizacin del inmovilizado material.',NULL,0,3),(763,1,'6810','Amortizacin del inmovilizado material.',NULL,0,4),(764,1,'6811','Amortizacin de construcciones',NULL,0,4),(765,1,'6812','Amortizacin de instalaciones tcnicas',NULL,0,4),(766,1,'6813','Amortizacin de maquinaria',NULL,0,4),(767,1,'6814','Amortizacin de utillaje',NULL,0,4),(768,1,'6815','Amortizacin de otras instalaciones',NULL,0,4),(769,1,'6816','Amortizacin de mobiliario',NULL,0,4),(770,1,'6817','Amortizacin de equipos para el proceso de informacin',NULL,0,4),(771,1,'6818','Amortizacin de elementos de transporte',NULL,0,4),(772,1,'6819','Amortizacin de otro inmovilizado material',NULL,0,4),(773,1,'682','Amortizacin de las inversiones inmobiliarias.',NULL,0,3),(774,1,'6820','Amortizacin de las inversiones inmobiliarias.',NULL,0,4),(775,1,'69','PRDIDAS POR DETERIORO Y OTRAS DOTACIONES.',NULL,0,2),(776,1,'690','Prdidas por deterioro del inmovilizado intangible.',NULL,0,3),(777,1,'6900','Prdidas por deterioro del inmovilizado intangible.',NULL,0,4),(778,1,'691','Prdidas por deterioro del inmovilizado material.',NULL,0,3),(779,1,'6910','Prdidas por deterioro del inmovilizado material.',NULL,0,4),(780,1,'692','Prdidas por deterioro de las inversiones inmobiliarias.',NULL,0,3),(781,1,'6920','Prdidas por deterioro de las inversiones inmobiliarias.',NULL,0,4),(782,1,'693','Prdidas por deterioro de existencias.',NULL,0,3),(783,1,'6930','Prdidas por deterioro de existencias.',NULL,0,4),(784,1,'694','Prdidas por deterioro de crditos comerciales.',NULL,0,3),(785,1,'6940','Prdidas por deterioro de crditos comerciales.',NULL,0,4),(786,1,'695','Dotacin a la provisin por operaciones comerciales.',NULL,0,3),(787,1,'6954','Dotacin a la provisin por contratos onerosos.',NULL,0,4),(788,1,'6959','Dotacin a la provisin para otras operaciones comerciales.',NULL,0,4),(789,1,'696','Prdidas por deterioro de participaciones y valores representativos de deuda a largo plazo.',NULL,0,3),(790,1,'6960','Prdidas por deterioro de participaciones y valores representativos de deuda a largo plazo.',NULL,0,4),(791,1,'697','Prdidas por deterioro de crditos a largo plazo.',NULL,0,3),(792,1,'6970','Prdidas por deterioro de crditos a largo plazo.',NULL,0,4),(793,1,'698','Prdidas por deterioro de participaciones y valores representativos de deuda a corto plazo.',NULL,0,3),(794,1,'6980','Prdidas por deterioro de participaciones y valores representativos de deuda a corto plazo.',NULL,0,4),(795,1,'699','Prdidas por deterioro de crditos a corto plazo.',NULL,0,3),(796,1,'6990','Prdidas por deterioro de crditos a corto plazo.',NULL,0,4),(797,1,'7','VENTAS E INGRESOS',NULL,0,1),(798,1,'70','VENTAS DE MERCADERAS, DE PRODUCCIN PROPIA, DE SERVICIOS, ETC.',NULL,0,2),(799,1,'700','Ventas de mercaderas.',NULL,0,3),(800,1,'7000','Ventas de mercaderas.',NULL,0,4),(801,1,'700000000','Ventas de mercaderas.',NULL,1,5),(802,1,'701','Ventas de productos terminados.',NULL,0,3),(803,1,'7010','Ventas de productos terminados.',NULL,0,4),(804,1,'702','Ventas de productos semiterminados.',NULL,0,3),(805,1,'7020','Ventas de productos semiterminados.',NULL,0,4),(806,1,'703','Ventas de subproductos y residuos.',NULL,0,3),(807,1,'7030','Ventas de subproductos y residuos.',NULL,0,4),(808,1,'704','Ventas de envases y embalajes.',NULL,0,3),(809,1,'7040','Ventas de envases y embalajes.',NULL,0,4),(810,1,'705','Prestacin de servicios.',NULL,0,3),(811,1,'7050','Prestacin de servicios.',NULL,0,4),(812,1,'706','Descuentos sobre ventas por pronto pago.',NULL,0,3),(813,1,'7060','Descuentos sobre ventas por pronto pago.',NULL,0,4),(814,1,'708','Devoluciones de ventas y operaciones similares.',NULL,0,3),(815,1,'7080','Devoluciones de ventas y operaciones similares.',NULL,0,4),(816,1,'709','Rappels sobre ventas.',NULL,0,3),(817,1,'7090','Rappels sobre ventas.',NULL,0,4),(818,1,'71','VARIACIN DE EXISTENCIAS.',NULL,0,2),(819,1,'710','Variacin de existencias de productos en curso.',NULL,0,3),(820,1,'7100','Variacin de existencias de productos en curso.',NULL,0,4),(821,1,'711','Variacin de existencias de productos semiterminados.',NULL,0,3),(822,1,'7110','Variacin de existencias de productos semiterminados.',NULL,0,4),(823,1,'712','Variacin de existencias de productos terminados.',NULL,0,3),(824,1,'7120','Variacin de existencias de productos terminados.',NULL,0,4),(825,1,'713','Variacin de existencias de subproductos, residuos y materiales recuperados.',NULL,0,3),(826,1,'7130','Variacin de existencias de subproductos, residuos y materiales recuperados.',NULL,0,4),(827,1,'73','TRABAJOS REALIZADOS PARA LA EMPRESA.',NULL,0,2),(828,1,'730','Trabajos realizados para el inmovilizado intangible.',NULL,0,3),(829,1,'7300','Trabajos realizados para el inmovilizado intangible.',NULL,0,4),(830,1,'731','Trabajos realizados para el inmovilizado material.',NULL,0,3),(831,1,'7310','Trabajos realizados para el inmovilizado material.',NULL,0,4),(832,1,'732','Trabajos realizados en inversiones inmobiliarias.',NULL,0,3),(833,1,'7320','Trabajos realizados en inversiones inmobiliarias.',NULL,0,4),(834,1,'733','Trabajos realizados para el inmovilizado en curso.',NULL,0,3),(835,1,'7330','Trabajos realizados para el inmovilizado en curso.',NULL,0,4),(836,1,'74','SUBVENCIONES, DONACIONES Y LEGADOS.',NULL,0,2),(837,1,'740','Subvenciones, donaciones y legados a la explotacin.',NULL,0,3),(838,1,'7400','Subvenciones, donaciones y legados a la explotacin.',NULL,0,4),(839,1,'746','Subvenciones, donaciones y legados de capital transferidos al resultado del ejercicio.',NULL,0,3),(840,1,'7460','Subvenciones, donaciones y legados de capital transferidos al resultado del ejercicio.',NULL,0,4),(841,1,'747','Otras subvenciones, donaciones y legados transferidos al resultado del ejercicio.',NULL,0,3),(842,1,'7470','Otras subvenciones, donaciones y legados transferidos al resultado del ejercicio.',NULL,0,4),(843,1,'75','OTROS INGRESOS DE GESTIN.',NULL,0,2),(844,1,'751','Resultados de operaciones en comn.',NULL,0,3),(845,1,'7510','Prdida transferida (gestor).',NULL,0,4),(846,1,'7511','Beneficio atribuido (partcipe o asociado no gestor).',NULL,0,4),(847,1,'752','Ingresos por arrendamientos.',NULL,0,3),(848,1,'7520','Ingresos por arrendamientos.',NULL,0,4),(849,1,'753','Ingresos de propiedad industrial cedida en explotacin.',NULL,0,3),(850,1,'7530','Ingresos de propiedad industrial cedida en explotacin.',NULL,0,4),(851,1,'754','Ingresos por comisiones.',NULL,0,3),(852,1,'7540','Ingresos por comisiones.',NULL,0,4),(853,1,'755','Ingresos por servicios al personal.',NULL,0,3),(854,1,'7550','Ingresos por servicios al personal.',NULL,0,4),(855,1,'759','Ingresos por servicios diversos.',NULL,0,3),(856,1,'7590','Ingresos por servicios diversos.',NULL,0,4),(857,1,'76','INGRESOS FINANCIEROS.',NULL,0,2),(858,1,'760','Ingresos de participaciones en instrumentos de patrimonio.',NULL,0,3),(859,1,'7600','Ingresos de participaciones en instrumentos de patrimonio.',NULL,0,4),(860,1,'761','Ingresos de valores representativos de deuda.',NULL,0,3),(861,1,'7610','Ingresos de valores representativos de deuda.',NULL,0,4),(862,1,'762','Ingresos de crditos.',NULL,0,3),(863,1,'7620','Ingresos de crditos.',NULL,0,4),(864,1,'763','Beneficios por valoracin de instrumentos financieros por su valor razonable.',NULL,0,3),(865,1,'7630','Beneficios de cartera de negociacin.',NULL,0,4),(866,1,'7631','Beneficios de designados por la empresa.',NULL,0,4),(867,1,'7632','Beneficios de disponibles para la venta.',NULL,0,4),(868,1,'7633','Beneficios de instrumentos de cobertura.',NULL,0,4),(869,1,'766','Beneficios en participaciones y valores representativos de deuda.',NULL,0,3),(870,1,'7660','Beneficios en participaciones y valores representativos de deuda.',NULL,0,4),(871,1,'767','Ingresos de activos afectos y de derechos de reembolso relativos a retribuciones a largo plazo.',NULL,0,3),(872,1,'7670','Ingresos de activos afectos y de derechos de reembolso relativos a retribuciones a largo plazo.',NULL,0,4),(873,1,'768','Diferencias positivas de cambio.',NULL,0,3),(874,1,'7680','Diferencias positivas de cambio.',NULL,0,4),(875,1,'769','Otros ingresos financieros.',NULL,0,3),(876,1,'7690','Otros ingresos financieros.',NULL,0,4),(877,1,'77','BENEFICIOS PROCEDENTES DE ACTIVOS NO CORRIENTES E INGRESOS EXCEPCIONALES.',NULL,0,2),(878,1,'770','Beneficios procedentes del inmovilizado intangible.',NULL,0,3),(879,1,'7700','Beneficios procedentes del inmovilizado intangible.',NULL,0,4),(880,1,'771','Beneficios procedentes del inmovilizado material.',NULL,0,3),(881,1,'7710','Beneficios procedentes del inmovilizado material.',NULL,0,4),(882,1,'772','Beneficios procedentes de las inversiones inmobiliarias.',NULL,0,3),(883,1,'7720','Beneficios procedentes de las inversiones inmobiliarias.',NULL,0,4),(884,1,'773','Beneficios procedentes de participaciones..',NULL,0,3),(885,1,'7730','Beneficios procedentes de participaciones..',NULL,0,4),(886,1,'774','Diferencia negativa en combinaciones de negocios.',NULL,0,3),(887,1,'7740','Diferencia negativa en combinaciones de negocios.',NULL,0,4),(888,1,'775','Beneficios por operaciones con obligaciones propias.',NULL,0,3),(889,1,'7750','Beneficios por operaciones con obligaciones propias.',NULL,0,4),(890,1,'778','Ingresos excepcionales.',NULL,0,3),(891,1,'7780','Ingresos excepcionales.',NULL,0,4),(892,1,'79','EXCESOS Y APLICACIONES DE PROVISIONES Y DE PRDIDAS POR DETERIORO.',NULL,0,2),(893,1,'790','Reversin del deterioro del inmovilizado intangible.',NULL,0,3),(894,1,'7900','Reversin del deterioro del inmovilizado intangible.',NULL,0,4),(895,1,'791','Reversin del deterioro del inmovilizado material.',NULL,0,3),(896,1,'7910','Reversin del deterioro del inmovilizado material.',NULL,0,4),(897,1,'792','Reversin del deterioro de las inversiones inmobiliarias.',NULL,0,3),(898,1,'7920','Reversin del deterioro de las inversiones inmobiliarias.',NULL,0,4),(899,1,'793','Reversin del deterioro de existencias.',NULL,0,3),(900,1,'7930','Reversin del deterioro de existencias.',NULL,0,4),(901,1,'794','Reversin del deterioro de crditos por operaciones comerciales.',NULL,0,3),(902,1,'7940','Reversin del deterioro de crditos por operaciones comerciales.',NULL,0,4),(903,1,'795','Exceso de provisiones.',NULL,0,3),(904,1,'7950','Exceso de provisiones.',NULL,0,4),(905,1,'796','Reversin del deterioro de participaciones y valores representativos de deuda a largo plazo.',NULL,0,3),(906,1,'7960','Reversin del deterioro de participaciones y valores representativos de deuda a largo plazo.',NULL,0,4),(907,1,'797','Reversin del deterioro de crditos a largo plazo.',NULL,0,3),(908,1,'7970','Reversin del deterioro de crditos a largo plazo.',NULL,0,4),(909,1,'798','Reversin del deterioro de participaciones y valores representativos de deuda a corto plazo.',NULL,0,3),(910,1,'7980','Reversin del deterioro de participaciones y valores representativos de deuda a corto plazo.',NULL,0,4),(911,1,'799','Reversin del deterioro de crditos a corto plazo.',NULL,0,3),(912,1,'7990','Reversin del deterioro de crditos a corto plazo.',NULL,0,4),(913,1,'8','GASTOS IMPUTADOS AL PATRIMONIO NETO',NULL,0,1),(914,1,'80','GASTOS FINANCIEROS POR VALORACIN DE ACTIVOS FINANCIEROS.',NULL,0,2),(915,1,'800','Prdidas en activos financieros disponibles para la venta.',NULL,0,3),(916,1,'8000','Prdidas en activos financieros disponibles para la venta.',NULL,0,4),(917,1,'802','Transferencia de beneficios en activos financieros disponibles para la venta.',NULL,0,3),(918,1,'8020','Transferencia de beneficios en activos financieros disponibles para la venta.',NULL,0,4),(919,1,'81','GASTOS EN OPERACIONES DE COBERTURA.',NULL,0,2),(920,1,'810','Prdidas por coberturas de flujos de efectivo.',NULL,0,3),(921,1,'8100','Prdidas por coberturas de flujos de efectivo.',NULL,0,4),(922,1,'811','Prdidas por coberturas de inversiones netas en un negocio en el extranjero.',NULL,0,3),(923,1,'8110','Prdidas por coberturas de inversiones netas en un negocio en el extranjero.',NULL,0,4),(924,1,'812','Transferencia de beneficios por coberturas de flujos de efectivo.',NULL,0,3),(925,1,'8120','Transferencia de beneficios por coberturas de flujos de efectivo.',NULL,0,4),(926,1,'813','Transferencia de beneficios por coberturas de inversiones netas en un negocio en el extranjero.',NULL,0,3),(927,1,'8130','Transferencia de beneficios por coberturas de inversiones netas en un negocio en el extranjero.',NULL,0,4),(928,1,'82','GASTOS POR DIFERENCIAS EN CONVERSIN.',NULL,0,2),(929,1,'820','Diferencias de conversin negativas.',NULL,0,3),(930,1,'8200','Diferencias de conversin negativas.',NULL,0,4),(931,1,'821','Transferencia de diferencias de conversin positivas.',NULL,0,3),(932,1,'8210','Transferencia de diferencias de conversin positivas.',NULL,0,4),(933,1,'83','IMPUESTOS SOBRE BENEFICIOS.',NULL,0,2),(934,1,'830','Impuestos sobre beneficios.',NULL,0,3),(935,1,'8300','Impuesto corriente.',NULL,0,4),(936,1,'8301','Impuesto diferido.',NULL,0,4),(937,1,'833','Ajustes negativos en la imposicin sobre beneficios.',NULL,0,3),(938,1,'8330','Ajustes negativos en la imposicin sobre beneficios.',NULL,0,4),(939,1,'834','Ingresos fiscales por diferencias permanentes.',NULL,0,3),(940,1,'8340','Ingresos fiscales por diferencias permanentes.',NULL,0,4),(941,1,'835','Ingresos fiscales por deducciones y bonificaciones.',NULL,0,3),(942,1,'8350','Ingresos fiscales por deducciones y bonificaciones.',NULL,0,4),(943,1,'836','Transferencia de diferencias permanentes.',NULL,0,3),(944,1,'8360','Transferencia de diferencias permanentes.',NULL,0,4),(945,1,'837','Transferencia de deducciones y bonificaciones.',NULL,0,3),(946,1,'8370','Transferencia de deducciones y bonificaciones.',NULL,0,4),(947,1,'838','Ajustes positivos en la imposicin sobre beneficios.',NULL,0,3),(948,1,'8380','Ajustes positivos en la imposicin sobre beneficios.',NULL,0,4),(949,1,'84','TRANSFERENCIAS DE SUBVENCIONES, DONACIONES Y LEGADOS.',NULL,0,2),(950,1,'840','Transferencia de subvenciones oficiales de capital.',NULL,0,3),(951,1,'8400','Transferencia de subvenciones oficiales de capital.',NULL,0,4),(952,1,'841','Transferencia de donaciones y legados de capital.',NULL,0,3),(953,1,'8410','Transferencia de donaciones y legados de capital.',NULL,0,4),(954,1,'842','Transferencia de otras subvenciones, donaciones y legados.',NULL,0,3),(955,1,'8420','Transferencia de otras subvenciones, donaciones y legados.',NULL,0,4),(956,1,'85','GASTOS POR PRDIDAS ACTUARIALES Y AJUSTES EN LOS ACTIVOS POR RETRIBUCIONES A LARGO PLAZO DE PRESTACIN DEFINIDA.',NULL,0,2),(957,1,'850','Prdidas actuariales.',NULL,0,3),(958,1,'8500','Prdidas actuariales.',NULL,0,4),(959,1,'851','Ajustes negativos en activos por retribuciones a largo plazo de prestacin definida.',NULL,0,3),(960,1,'8510','Ajustes negativos en activos por retribuciones a largo plazo de prestacin definida.',NULL,0,4),(961,1,'86','GASTOS POR ACTIVOS NO CORRIENTES EN VENTA.',NULL,0,2),(962,1,'860','Prdidas en activos no corrientes y grupos enajenables de elementos mantenidos para la venta.',NULL,0,3),(963,1,'8600','Prdidas en activos no corrientes y grupos enajenables de elementos mantenidos para la venta.',NULL,0,4),(964,1,'862','Transferencia de beneficios en activos no corrientes y grupos enajenables de elementos mantenidos para la venta.',NULL,0,3),(965,1,'8620','Transferencia de beneficios en activos no corrientes y grupos enajenables de elementos mantenidos para la venta.',NULL,0,4),(966,1,'89','GASTOS DE PARTICIPACIONES EN EMPRESAS DEL GRUPO O ASOCIADAS CON AJUSTES VALORATIVOS POSITIVOS PREVIOS.',NULL,0,2),(967,1,'891','Deterioro de participaciones en el patrimonio y valores representativos de deuda de empresas del grupo.',NULL,0,3),(968,1,'8910','Deterioro de participaciones en el patrimonio y valores representativos de deuda de empresas del grupo.',NULL,0,4),(969,1,'892','Deterioro de participaciones en el patrimonio y valores representativos de deuda de empresas asociadas.',NULL,0,3),(970,1,'8920','Deterioro de participaciones en el patrimonio y valores representativos de deuda de empresas asociadas.',NULL,0,4),(971,1,'9','INGRESOS IMPUTADOS AL PATRIMONIO NETO',NULL,0,1),(972,1,'90','INGRESOS FINANCIEROS POR VALORACIN DE ACTIVOS FINANCIEROS.',NULL,0,2),(973,1,'900','Beneficios en activos financieros disponibles para la venta.',NULL,0,3),(974,1,'9000','Beneficios en activos financieros disponibles para la venta.',NULL,0,4),(975,1,'902','Transferencia de prdidas de activos financieros disponibles para la venta.',NULL,0,3),(976,1,'9020','Transferencia de prdidas de activos financieros disponibles para la venta.',NULL,0,4),(977,1,'91','INGRESOS EN OPERACIONES DE COBERTURA.',NULL,0,2),(978,1,'910','Beneficios por coberturas de flujos de efectivo.',NULL,0,3),(979,1,'9100','Beneficios por coberturas de flujos de efectivo.',NULL,0,4),(980,1,'911','Beneficios por coberturas de una inversin neta en un negocio en el extranjero.',NULL,0,3),(981,1,'9110','Beneficios por coberturas de una inversin neta en un negocio en el extranjero.',NULL,0,4),(982,1,'912','Transferencia de prdidas por coberturas de flujos de efectivo.',NULL,0,3),(983,1,'9120','Transferencia de prdidas por coberturas de flujos de efectivo.',NULL,0,4),(984,1,'913','Transferencia de prdidas por coberturas de una inversin neta en un negocio en el extranjero.',NULL,0,3),(985,1,'9130','Transferencia de prdidas por coberturas de una inversin neta en un negocio en el extranjero.',NULL,0,4),(986,1,'92','INGRESOS POR DIFERENCIAS DE CONVERSIN.',NULL,0,2),(987,1,'920','Diferencias de conversin positivas.',NULL,0,3),(988,1,'9200','Diferencias de conversin positivas.',NULL,0,4),(989,1,'921','Transferencia de diferencias de conversin negativas.',NULL,0,3),(990,1,'9210','Transferencia de diferencias de conversin negativas.',NULL,0,4),(991,1,'94','INGRESOS POR SUBVENCIONES, DONACIONES Y LEGADOS.',NULL,0,2),(992,1,'940','Ingresos de subvenciones oficiales de capital.',NULL,0,3),(993,1,'9400','Ingresos de subvenciones oficiales de capital.',NULL,0,4),(994,1,'941','Ingresos de donaciones y legados de capital.',NULL,0,3),(995,1,'9410','Ingresos de donaciones y legados de capital.',NULL,0,4),(996,1,'942','Ingresos de otras subvenciones, donaciones y legados.',NULL,0,3),(997,1,'9420','Ingresos de otras subvenciones, donaciones y legados.',NULL,0,4),(998,1,'95','INGRESOS POR GANANCIAS ACTUARIALES Y AJUSTES EN LOS ACTIVOS POR RETRIBUCIONES A LARGO PLAZO DE PRESTACIN DEFINIDA.',NULL,0,2),(999,1,'950','Ganancias actuariales.',NULL,0,3),(1000,1,'9500','Ganancias actuariales.',NULL,0,4),(1001,1,'951','Ajustes positivos en activos por retribuciones a largo plazo de prestacin definida.',NULL,0,3),(1002,1,'9510','Ajustes positivos en activos por retribuciones a largo plazo de prestacin definida.',NULL,0,4),(1003,1,'96','INGRESOS POR ACTIVOS NO CORRIENTES EN VENTA.',NULL,0,2),(1004,1,'960','Beneficios en activos no corrientes y grupos enajenables de elementos mantenidos para la venta.',NULL,0,3),(1005,1,'9600','Beneficios en activos no corrientes y grupos enajenables de elementos mantenidos para la venta.',NULL,0,4),(1006,1,'962','Transferencia de prdidas en activos no corrientes y grupos enajenables de elementos mantenidos para la venta.',NULL,0,3),(1007,1,'9620','Transferencia de prdidas en activos no corrientes y grupos enajenables de elementos mantenidos para la venta.',NULL,0,4),(1008,1,'99','INGRESOS DE PARTICIPACIONES EN EL PATRIMONIO DE EMPRESAS DEL GRUPO O ASOCIADAS CON AJUSTES VALORATIVOS NEGATIVOS PREVIOS.',NULL,0,2),(1009,1,'991','Recuperacin de ajustes valorativos negativos previos, empresas del grupo.',NULL,0,3),(1010,1,'9910','Recuperacin de ajustes valorativos negativos previos, empresas del grupo.',NULL,0,4),(1011,1,'992','Recuperacin de ajustes valorativos negativos previos, empresas asociadas.',NULL,0,3),(1012,1,'9920','Recuperacin de ajustes valorativos negativos previos, empresas asociadas.',NULL,0,4),(1013,1,'993','Transferencia por deterioro de ajustes valorativos negativos previos, empresas del grupo.',NULL,0,3),(1014,1,'9930','Transferencia por deterioro de ajustes valorativos negativos previos, empresas del grupo.',NULL,0,4),(1015,1,'994','Transferencia por deterioro de ajustes valorativos negativos previos, empresas asociadas.',NULL,0,3),(1016,1,'9940','Transferencia por deterioro de ajustes valorativos negativos previos, empresas asociadas.',NULL,0,4);
ALTER TABLE `account` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `account_entry`
#

DROP TABLE IF EXISTS `account_entry`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account_entry` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Asiento',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `account_period` int(4) NOT NULL COMMENT 'Ejercicio Contable del Asiento',
  `entry_date` date default NULL COMMENT 'Fecha del Asiento',
  `entry_type` tinyint(2) default NULL COMMENT 'Tipo de Asiento',
  `journal` int(4) default NULL COMMENT 'Numero de diario del Asiento',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Asiento',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios del Asiento',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACCOUNT_ENTRY_ACCOUNT_PERIOD` (`account_period`),
  KEY `IDX_ACCOUNT_ENTRY_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_ACCOUNT_PERIOD` FOREIGN KEY (`account_period`) REFERENCES `account_period` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Asientos Contables';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `account_entry`
#

LOCK TABLES `account_entry` WRITE;
ALTER TABLE `account_entry` DISABLE KEYS;
ALTER TABLE `account_entry` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `account_entry_bank_statement`
#

DROP TABLE IF EXISTS `account_entry_bank_statement`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account_entry_bank_statement` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `account_entry` int(4) NOT NULL COMMENT 'Identificador de Asiento',
  `bank_statement` int(4) NOT NULL COMMENT 'Identificador de Extracto bancario',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACC_ENTRY_BANK_STATEMENT_ACC_ENTRY` (`account_entry`),
  KEY `IDX_ACC_ENTRY_BANK_STATEMENT_BANK_STATEMENT` (`bank_statement`),
  KEY `IDX_ACCOUNT_ENTRY_BANK_STATEMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_BANK_STATEMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACC_ENTRY_BANK_STATEMENT_ACC_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_ACC_ENTRY_BANK_STATEMENT_BANK_STATEMENT` FOREIGN KEY (`bank_statement`) REFERENCES `bank_statement` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Asientos Contables y Extractos bancarios';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `account_entry_bank_statement`
#

LOCK TABLES `account_entry_bank_statement` WRITE;
ALTER TABLE `account_entry_bank_statement` DISABLE KEYS;
ALTER TABLE `account_entry_bank_statement` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `account_entry_detail`
#

DROP TABLE IF EXISTS `account_entry_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account_entry_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Apunte',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `account_entry` int(4) NOT NULL COMMENT 'Identificador del Asiento',
  `line` int(4) unsigned NOT NULL COMMENT 'Numero de linea del Apunte dentro del Asiento',
  `account` int(4) NOT NULL COMMENT 'Cuenta Contable del Apunte',
  `concept` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Concepto del Apunte',
  `balancing_account` int(4) default NULL COMMENT 'Contrapartida del Apunte',
  `debit` double default '0' COMMENT 'Debe del Apunte',
  `credit` double default '0' COMMENT 'Haber del Apunte',
  `document_number` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Numero de documento asociado',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACCOUNT_ENTRY_DETAIL_ACCOUNT` (`account`),
  KEY `IDX_ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY` (`account_entry`),
  KEY `IDX_ACCOUNT_ENTRY_DETAIL_BALANCING_ACCOUNT` (`balancing_account`),
  KEY `IDX_ACCOUNT_ENTRY_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_DETAIL_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_DETAIL_BALANCING_ACCOUNT` FOREIGN KEY (`balancing_account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Apuntes Contables';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `account_entry_detail`
#

LOCK TABLES `account_entry_detail` WRITE;
ALTER TABLE `account_entry_detail` DISABLE KEYS;
ALTER TABLE `account_entry_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `account_entry_fbatch`
#

DROP TABLE IF EXISTS `account_entry_fbatch`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account_entry_fbatch` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `account_entry` int(4) NOT NULL COMMENT 'Identificador de Asiento Contable',
  `fbatch` int(4) NOT NULL COMMENT 'Identificador de Remesa',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACCOUNT_ENTRY_FBATCH_ACCOUNT_ENTRY` (`account_entry`),
  KEY `IDX_ACCOUNT_ENTRY_FBATCH_FBATCH` (`fbatch`),
  KEY `IDX_ACCOUNT_ENTRY_FBATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_FBATCH_ACCOUNT_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_FBATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_FBATCH_FBATCH` FOREIGN KEY (`fbatch`) REFERENCES `fbatch` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Asientos Contables y Remesas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `account_entry_fbatch`
#

LOCK TABLES `account_entry_fbatch` WRITE;
ALTER TABLE `account_entry_fbatch` DISABLE KEYS;
ALTER TABLE `account_entry_fbatch` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `account_entry_finance_tracking`
#

DROP TABLE IF EXISTS `account_entry_finance_tracking`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account_entry_finance_tracking` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `account_entry` int(4) NOT NULL COMMENT 'Identificador de Asiento Contable',
  `finance_tracking` int(4) NOT NULL COMMENT 'Identificador de Seguimiento de Vencimientos',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACCOUNT_ENTRY_FINANCE_TRACKING_ACCOUNT_ENTRY` (`account_entry`),
  KEY `IDX_ACCOUNT_ENTRY_FINANCE_TRACKING_FINANCE_TRACKING` (`finance_tracking`),
  KEY `IDX_ACCOUNT_ENTRY_FINANCE_TRACKING_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_FINANCE_TRACKING_ACCOUNT_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_FINANCE_TRACKING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_FINANCE_TRACKING_FINANCE_TRACKING` FOREIGN KEY (`finance_tracking`) REFERENCES `finance_tracking` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Seguimiento de Vencimientos y Asientos Contab';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `account_entry_finance_tracking`
#

LOCK TABLES `account_entry_finance_tracking` WRITE;
ALTER TABLE `account_entry_finance_tracking` DISABLE KEYS;
ALTER TABLE `account_entry_finance_tracking` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `account_entry_invoice`
#

DROP TABLE IF EXISTS `account_entry_invoice`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account_entry_invoice` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de Relacion',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `account_entry` int(4) NOT NULL COMMENT 'Identificador de Asiento',
  `invoice` int(4) NOT NULL COMMENT 'Identificador de Factura',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY` (`account_entry`),
  KEY `IDX_ACCOUNT_ENTRY_INVOICE_INVOICE` (`invoice`),
  KEY `IDX_ACCOUNT_ENTRY_INVOICE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_INVOICE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_INVOICE_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Asientos Contables y Facturas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `account_entry_invoice`
#

LOCK TABLES `account_entry_invoice` WRITE;
ALTER TABLE `account_entry_invoice` DISABLE KEYS;
ALTER TABLE `account_entry_invoice` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `account_helper`
#

DROP TABLE IF EXISTS `account_helper`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account_helper` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `counter` int(4) NOT NULL default '0' COMMENT 'Contador, veces que se ha usado',
  `account` int(4) NOT NULL COMMENT 'Cuenta Contable',
  `balancing_account` int(4) NOT NULL COMMENT 'Contrapartida',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACCOUNT_HELPER_ACCOUNT` (`account`),
  KEY `IDX_ACCOUNT_HELPER_BAL_ACCOUNT` (`balancing_account`),
  KEY `IDX_ACCOUNT_HELPER_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_HELPER_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_ACCOUNT_HELPER_BAL_ACCOUNT` FOREIGN KEY (`balancing_account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_ACCOUNT_HELPER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ayuda a la introduccion de apuntes';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `account_helper`
#

LOCK TABLES `account_helper` WRITE;
ALTER TABLE `account_helper` DISABLE KEYS;
ALTER TABLE `account_helper` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `account_period`
#

DROP TABLE IF EXISTS `account_period`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account_period` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` char(16) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del periodo',
  `initiation_date` date NOT NULL COMMENT 'Fecha de inicio del Ejercicio',
  `deadline` date NOT NULL COMMENT 'Fecha final del Ejercicio',
  `status` tinyint(2) default '0' COMMENT 'Estado del Ejercicio',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_UNQ_ACCOUNT_PERIOD_DOMAIN_NAME` (`domain`,`name`),
  KEY `IDX_ACCOUNT_PERIOD_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACCOUNT_PERIOD_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ejercicios Contables';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `account_period`
#

LOCK TABLES `account_period` WRITE;
ALTER TABLE `account_period` DISABLE KEYS;
ALTER TABLE `account_period` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `action`
#

DROP TABLE IF EXISTS `action`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `action` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `menu` tinyint(1) NOT NULL default '0' COMMENT 'Indica si la Accion esta o no dentro del menu',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Nombre de la Accion',
  `application_id` int(4) NOT NULL COMMENT 'Aplicacion a la que pertenece la Accion',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACTION_NAME` (`name`),
  KEY `IDX_ACTION_APPLICATION` (`application_id`),
  KEY `IDX_ACTION_DOMAIN` (`domain`),
  KEY `IDX_ACTION_NAME_APPLICATION` (`name`,`application_id`),
  CONSTRAINT `FK_ACTION_APPLICATION` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `FK_ACTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Acciones de una Applicacion';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `action`
#

LOCK TABLES `action` WRITE;
ALTER TABLE `action` DISABLE KEYS;
INSERT INTO `action` VALUES (1,1,0,'menu_config',1),(2,1,1,'company',1);
ALTER TABLE `action` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `action_denied`
#

DROP TABLE IF EXISTS `action_denied`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `action_denied` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `action_id` int(4) NOT NULL default '0' COMMENT 'Identificador de la Accion',
  `user_id` int(4) NOT NULL default '0' COMMENT 'Identificador del Usuario',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACTION_DENIED_ACTION` (`action_id`),
  KEY `IDX_ACTION_DENIED_USER` (`user_id`),
  KEY `IDX_ACTION_DENIED_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACTION_DENIED_ACTION` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `FK_ACTION_DENIED_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACTION_DENIED_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Accion no permitida para el Usuario';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `action_denied`
#

LOCK TABLES `action_denied` WRITE;
ALTER TABLE `action_denied` DISABLE KEYS;
ALTER TABLE `action_denied` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `action_entry`
#

DROP TABLE IF EXISTS `action_entry`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `action_entry` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `executionDate` datetime NOT NULL COMMENT 'Fecha de ejecucion',
  `action_id` int(4) NOT NULL default '0' COMMENT 'Identificador de la Accion',
  `session_id` int(4) NOT NULL default '0' COMMENT 'Identificador de la Sesion',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACTION_ENTRY_ACTION` (`action_id`),
  KEY `IDX_ACTION_ENTRY_SESSION` (`session_id`),
  KEY `IDX_ACTION_ENTRY_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACTION_ENTRY_ACTION` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `FK_ACTION_ENTRY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACTION_ENTRY_SESSION` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Entrada de la ejecucion de una Accion';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `action_entry`
#

LOCK TABLES `action_entry` WRITE;
ALTER TABLE `action_entry` DISABLE KEYS;
INSERT INTO `action_entry` VALUES (1,1,'2010-10-19 05:48:14',1,1),(2,1,'2010-10-19 05:48:15',2,1),(3,1,'2010-10-19 05:58:23',2,2);
ALTER TABLE `action_entry` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `action_favorite`
#

DROP TABLE IF EXISTS `action_favorite`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `action_favorite` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `position` int(4) NOT NULL COMMENT 'Posicion dentro de las Acciones Favoritas',
  `action_id` int(4) NOT NULL default '0' COMMENT 'Identificador de la Accion',
  `user_id` int(4) NOT NULL default '0' COMMENT 'Identificador del Usuario',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACTION_FAVORITE_ACTION` (`action_id`),
  KEY `IDX_ACTION_FAVORITE_USER` (`user_id`),
  KEY `IDX_ACTION_FAVORITE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACTION_FAVORITE_ACTION` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `FK_ACTION_FAVORITE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACTION_FAVORITE_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Accion Favorita del Usuario';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `action_favorite`
#

LOCK TABLES `action_favorite` WRITE;
ALTER TABLE `action_favorite` DISABLE KEYS;
ALTER TABLE `action_favorite` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `activity_type`
#

DROP TABLE IF EXISTS `activity_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `activity_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Tipo de Actividad',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Actividad',
  `project_type` int(4) default NULL COMMENT 'Tipo de Proyecto',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Activo si o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACTIVITY_TYPE_PROJECT_TYPE` (`project_type`),
  KEY `IDX_ACTIVITY_TYPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ACTIVITY_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ACTIVITY_TYPE_PROJECT_TYPE` FOREIGN KEY (`project_type`) REFERENCES `project_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Actividades';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `activity_type`
#

LOCK TABLES `activity_type` WRITE;
ALTER TABLE `activity_type` DISABLE KEYS;
ALTER TABLE `activity_type` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `agreement`
#

DROP TABLE IF EXISTS `agreement`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `agreement` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `calendar` int(4) default NULL COMMENT 'Calendario',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_AGREEMENT_CALENDAR` (`calendar`),
  KEY `IDX_AGREEMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_AGREEMENT_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_AGREEMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Convenios';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `agreement`
#

LOCK TABLES `agreement` WRITE;
ALTER TABLE `agreement` DISABLE KEYS;
ALTER TABLE `agreement` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `agreement_data`
#

DROP TABLE IF EXISTS `agreement_data`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `agreement_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre',
  `agreement` int(4) NOT NULL COMMENT 'Convenio',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_AGREEMENT_DATA_AGREEMENT` (`agreement`),
  KEY `IDX_AGREEMENT_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_AGREEMENT_DATA_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`),
  CONSTRAINT `FK_AGREEMENT_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto del convenio';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `agreement_data`
#

LOCK TABLES `agreement_data` WRITE;
ALTER TABLE `agreement_data` DISABLE KEYS;
ALTER TABLE `agreement_data` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `agreement_extra`
#

DROP TABLE IF EXISTS `agreement_extra`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `agreement_extra` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `agreement` int(4) NOT NULL COMMENT 'Convenio',
  `agreement_payment` int(4) default NULL COMMENT 'Concepto',
  `start_date` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Fecha de inicio dd mm [year offset]',
  `end_date` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Fecha de finalizacion dd mm [year offset]',
  `issue_date` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Fecha de emision dd mm',
  PRIMARY KEY  (`id`),
  KEY `IDX_AGREEMENT_EXTRA_AGREEMENT` (`agreement`),
  KEY `IDX_AGREEMENT_EXTRA_AGREEMENT_PAYMENT` (`agreement_payment`),
  KEY `IDX_AGREEMENT_EXTRA_DOMAIN` (`domain`),
  CONSTRAINT `FK_AGREEMENT_EXTRA_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`),
  CONSTRAINT `FK_AGREEMENT_EXTRA_AGREEMENT_PAYMENT` FOREIGN KEY (`agreement_payment`) REFERENCES `agreement_payment` (`id`),
  CONSTRAINT `FK_AGREEMENT_EXTRA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Pagas extras';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `agreement_extra`
#

LOCK TABLES `agreement_extra` WRITE;
ALTER TABLE `agreement_extra` DISABLE KEYS;
ALTER TABLE `agreement_extra` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `agreement_level`
#

DROP TABLE IF EXISTS `agreement_level`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `agreement_level` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `agreement` int(4) NOT NULL COMMENT 'Convenio',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_AGREEMENT_LEVEL_AGREEMENT` (`agreement`),
  KEY `IDX_AGREEMENT_LEVEL_DOMAIN` (`domain`),
  CONSTRAINT `FK_AGREEMENT_LEVEL_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`),
  CONSTRAINT `FK_AGREEMENT_LEVEL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Niveles retributivos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `agreement_level`
#

LOCK TABLES `agreement_level` WRITE;
ALTER TABLE `agreement_level` DISABLE KEYS;
ALTER TABLE `agreement_level` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `agreement_level_category`
#

DROP TABLE IF EXISTS `agreement_level_category`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `agreement_level_category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `agreement_level` int(4) NOT NULL COMMENT 'Nivel retributivo',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_AGREEMENT_LEVEL_CATEGORY_AGREEMENT_LEVEL` (`agreement_level`),
  KEY `IDX_AGREEMENT_LEVEL_CATEGORY_DOMAIN` (`domain`),
  CONSTRAINT `FK_AGREEMENT_LEVEL_CATEGORY_AGREEMENT_LEVEL` FOREIGN KEY (`agreement_level`) REFERENCES `agreement_level` (`id`),
  CONSTRAINT `FK_AGREEMENT_LEVEL_CATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias profesionales';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `agreement_level_category`
#

LOCK TABLES `agreement_level_category` WRITE;
ALTER TABLE `agreement_level_category` DISABLE KEYS;
ALTER TABLE `agreement_level_category` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `agreement_level_data`
#

DROP TABLE IF EXISTS `agreement_level_data`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `agreement_level_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre',
  `agreement_level` int(4) NOT NULL COMMENT 'Nivel retributivo',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_AGREEMENT_LEVEL_DATA_AGREEMENT_LEVEL` (`agreement_level`),
  KEY `IDX_AGREEMENT_LEVEL_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_AGREEMENT_LEVEL_DATA_AGREEMENT_LEVEL` FOREIGN KEY (`agreement_level`) REFERENCES `agreement_level` (`id`),
  CONSTRAINT `FK_AGREEMENT_LEVEL_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto del convenio';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `agreement_level_data`
#

LOCK TABLES `agreement_level_data` WRITE;
ALTER TABLE `agreement_level_data` DISABLE KEYS;
ALTER TABLE `agreement_level_data` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `agreement_payment`
#

DROP TABLE IF EXISTS `agreement_payment`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `agreement_payment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_AGREEMENT_PAYMENT_PAYMENT_CONCEPT` (`payment_concept`),
  KEY `IDX_AGREEMENT_PAYMENT_AGREEMENT` (`agreement`),
  KEY `IDX_AGREEMENT_PAYMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_AGREEMENT_PAYMENT_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`),
  CONSTRAINT `FK_AGREEMENT_PAYMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_AGREEMENT_PAYMENT_PAYMENT_CONCEPT` FOREIGN KEY (`payment_concept`) REFERENCES `payment_concept` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Percepciones';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `agreement_payment`
#

LOCK TABLES `agreement_payment` WRITE;
ALTER TABLE `agreement_payment` DISABLE KEYS;
ALTER TABLE `agreement_payment` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `alarm`
#

DROP TABLE IF EXISTS `alarm`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `alarm` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Alarma',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` text collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Alarma',
  `alarm_date` datetime NOT NULL COMMENT 'Fecha y hora de ejecucion de la Alarma',
  `status` tinyint(2) default NULL COMMENT 'Estado de la Alarma',
  `source` tinyint(2) NOT NULL COMMENT 'Origen de la Alarma',
  `source_id` int(4) default NULL COMMENT 'Identificador del origen de la Alarma',
  `user_id` int(4) default NULL COMMENT 'Identificador del Usuario asociado a la Alarma',
  `priority` tinyint(2) NOT NULL COMMENT 'Prioridad de la Alarma',
  PRIMARY KEY  (`id`),
  KEY `IDX_ALARM_USER` (`user_id`),
  KEY `IDX_ALARM_DOMAIN` (`domain`),
  CONSTRAINT `FK_ALARM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ALARM_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Alarmas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `alarm`
#

LOCK TABLES `alarm` WRITE;
ALTER TABLE `alarm` DISABLE KEYS;
ALTER TABLE `alarm` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `alumn_loan`
#

DROP TABLE IF EXISTS `alumn_loan`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
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
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `alumn_loan`
#

LOCK TABLES `alumn_loan` WRITE;
ALTER TABLE `alumn_loan` DISABLE KEYS;
ALTER TABLE `alumn_loan` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `amortization`
#

DROP TABLE IF EXISTS `amortization`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `amortization` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del inmovilizado',
  `amortization_type` int(4) NOT NULL COMMENT 'Tipo de Amortizacion',
  `initial_date` date NOT NULL COMMENT 'Fecha de inicio de la Amortizacion',
  `deadline` date default NULL COMMENT 'Fecha de baja de la Amortizacion',
  `amount` double NOT NULL default '0' COMMENT 'Importe a amortizar.',
  `fee_period` tinyint(2) NOT NULL default '0' COMMENT 'Periodo de las cuotas de Amortizacion',
  `sale_amount` double default NULL COMMENT 'Importe de la venta',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios',
  `fixed_asset_account` int(4) NOT NULL COMMENT 'Cuenta de inmovilizado',
  `accumulated_account` int(4) NOT NULL COMMENT 'Cuenta de Amortizacion acumulada',
  `allocation_account` int(4) NOT NULL COMMENT 'Cuenta para la dotacion de la Amortizacion',
  `percentage` double default '0' COMMENT 'Porcentaje de Amortizacion',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad',
  PRIMARY KEY  (`id`),
  KEY `IDX_AMORTIZATION_AMORTIZATION_TYPE` (`amortization_type`),
  KEY `IDX_AMORTIZATION_FIXED_ASSET_ACCOUNT` (`fixed_asset_account`),
  KEY `IDX_AMORTIZATION_ACCUMULATED_ACCOUNT` (`accumulated_account`),
  KEY `IDX_AMORTIZATION_ALLOCATION_ACCOUNT` (`allocation_account`),
  KEY `IDX_AMORTIZATION_DOMAIN` (`domain`),
  CONSTRAINT `FK_AMORTIZATION_ACCUMULATED_ACCOUNT` FOREIGN KEY (`accumulated_account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_AMORTIZATION_ALLOCATION_ACCOUNT` FOREIGN KEY (`allocation_account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_AMORTIZATION_AMORTIZATION_TYPE` FOREIGN KEY (`amortization_type`) REFERENCES `amortization_type` (`id`),
  CONSTRAINT `FK_AMORTIZATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_AMORTIZATION_FIXED_ASSET_ACCOUNT` FOREIGN KEY (`fixed_asset_account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Fichas de Amortizacion Contables';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `amortization`
#

LOCK TABLES `amortization` WRITE;
ALTER TABLE `amortization` DISABLE KEYS;
ALTER TABLE `amortization` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `amortization_detail`
#

DROP TABLE IF EXISTS `amortization_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `amortization_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `amortization` int(4) NOT NULL COMMENT 'Ficha de Amortizacion',
  `from_date` date NOT NULL COMMENT 'Desde fecha',
  `to_date` date NOT NULL COMMENT 'Hasta fecha',
  `coefficient` double(15,3) NOT NULL COMMENT 'Coeficiente de Amortizacion',
  `allocation` double(15,3) NOT NULL COMMENT 'Dotacion de la Amortizacion',
  `status` tinyint(2) NOT NULL default '0' COMMENT 'Estatus del Detalle de Amortizacion',
  `account_entry` int(4) default NULL COMMENT 'Posicion del Apunte Contable',
  `fiscal_allocation` double(15,3) default '0.000' COMMENT 'Dotacion fiscal',
  PRIMARY KEY  (`id`),
  KEY `IDX_AMORTIZATION_DETAIL_AMORTIZATION` (`amortization`),
  KEY `IDX_AMORTIZATION_DETAIL_ACCOUNT_ENTRY` (`account_entry`),
  KEY `IDX_AMORTIZATION_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_AMORTIZATION_DETAIL_ACCOUNT_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_AMORTIZATION_DETAIL_AMORTIZATION` FOREIGN KEY (`amortization`) REFERENCES `amortization` (`id`),
  CONSTRAINT `FK_AMORTIZATION_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de la Ficha de Amortizacion Contable';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `amortization_detail`
#

LOCK TABLES `amortization_detail` WRITE;
ALTER TABLE `amortization_detail` DISABLE KEYS;
ALTER TABLE `amortization_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `amortization_type`
#

DROP TABLE IF EXISTS `amortization_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `amortization_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fixed_asset_account` int(4) NOT NULL COMMENT 'Cuenta de inmovilizado',
  `accumulated_account` int(4) NOT NULL COMMENT 'Cuenta de amortizacion acumulada',
  `allocation_account` int(4) NOT NULL COMMENT 'Cuenta para la dotacion de la amortizacion',
  `percentage` double default '0' COMMENT 'Porcentaje de amortizacion',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Amortizacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_AMORTIZATION_TYPE_FIXED_ASSET_ACCOUNT` (`fixed_asset_account`),
  KEY `IDX_AMORTIZATION_TYPE_ACCUMULATED_ACCOUNT` (`accumulated_account`),
  KEY `IDX_AMORTIZATION_TYPE_ALLOCATION_ACCOUNT` (`allocation_account`),
  KEY `IDX_AMORTIZATION_TYPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_AMORTIZATION_TYPE_ACCUMULATED_ACCOUNT` FOREIGN KEY (`accumulated_account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_AMORTIZATION_TYPE_ALLOCATION_ACCOUNT` FOREIGN KEY (`allocation_account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_AMORTIZATION_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_AMORTIZATION_TYPE_FIXED_ASSET_ACCOUNT` FOREIGN KEY (`fixed_asset_account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Amortizacion';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `amortization_type`
#

LOCK TABLES `amortization_type` WRITE;
ALTER TABLE `amortization_type` DISABLE KEYS;
ALTER TABLE `amortization_type` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `app_param`
#

DROP TABLE IF EXISTS `app_param`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `app_param` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Parametro',
  `value` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Valor del Parametro',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_APP_PARAM_DOMAIN_NAME` (`domain`,`name`),
  KEY `IDX_APP_PARAM_DOMAIN` (`domain`),
  CONSTRAINT `FK_APP_PARAM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Parametros de la Aplicacion';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `app_param`
#

LOCK TABLES `app_param` WRITE;
ALTER TABLE `app_param` DISABLE KEYS;
INSERT INTO `app_param` VALUES (1,1,'ACC_DEFAULT_CASH_ACC','579'),(2,1,'ACC_DEFAULT_CHARGED_RET_ACC','422'),(3,1,'ACC_DEFAULT_CHARGED_VAT_ACC','430'),(4,1,'ACC_DEFAULT_COMPANY_SOC_INS_ACC','696'),(5,1,'ACC_DEFAULT_DEBT_INTEREST_ACC','721'),(6,1,'ACC_DEFAULT_FINAN_EXPENSES_ACC','739'),(7,1,'ACC_DEFAULT_PAID_RET_ACC','414'),(8,1,'ACC_DEFAULT_PAID_VAT_ACC','411'),(9,1,'ACC_DEFAULT_PENDING_SALARY_ACC','399'),(10,1,'ACC_DEFAULT_PURCHASE_ACC','630'),(11,1,'ACC_DEFAULT_SALARY_ACC','690'),(12,1,'ACC_DEFAULT_SALES_ACC','801'),(13,1,'ACC_DEFAULT_SOCIAL_INSURANCE_ACC','427'),(14,1,'APP_PRINT_HEADER_PARAM','false'),(15,1,'APP_PRINT_RECORD_DATA_PARAM','false'),(16,1,'APP_SMART_CARD_PARAM','false'),(17,1,'APP_PRINT_LOGO_PARAM','false'),(18,1,'APP_PRINT_NAME_PARAM','0'),(19,1,'APP_PRINT_NIF_PARAM','0'),(20,1,'APP_PRINT_ADDRESS_PARAM','0');
ALTER TABLE `app_param` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `application`
#

DROP TABLE IF EXISTS `application`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `application` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `audit_level` tinyint(2) NOT NULL default '0' COMMENT 'Nivel de auditoria',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Nombre de la Aplicacion',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_UNQ_APPLICATION_DOMAIN_NAME` (`domain`,`name`),
  KEY `IDX_APPLICATION_DOMAIN` (`domain`),
  CONSTRAINT `FK_APPLICATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Aplicacion web';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `application`
#

LOCK TABLES `application` WRITE;
ALTER TABLE `application` DISABLE KEYS;
INSERT INTO `application` VALUES (1,1,2,'aon-payroll');
ALTER TABLE `application` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `asset`
#

DROP TABLE IF EXISTS `asset`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `asset` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Activo',
  `name` varchar(10) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre corto del Activo',
  PRIMARY KEY  (`id`),
  KEY `IDX_ASSET_DOMAIN` (`domain`),
  CONSTRAINT `FK_ASSET_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Activos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `asset`
#

LOCK TABLES `asset` WRITE;
ALTER TABLE `asset` DISABLE KEYS;
ALTER TABLE `asset` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `asset_activity`
#

DROP TABLE IF EXISTS `asset_activity`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `asset_activity` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `asset` int(4) NOT NULL COMMENT 'Identificador del Activo',
  `date` date NOT NULL COMMENT 'Fecha de la Actividad',
  `from_time` datetime NOT NULL COMMENT 'Hora de inicio de la Actividad',
  `to_time` datetime NOT NULL COMMENT 'Hora final de la Actividad',
  `who` varchar(20) collate latin1_spanish_ci default NULL COMMENT 'Quien solicita el Activo',
  `why` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Motivo de solicitud del Activo',
  `status` tinyint(2) NOT NULL default '0' COMMENT 'Estado de la Solicitud',
  PRIMARY KEY  (`id`),
  KEY `IDX_ASSET_ACTIVITY_ASSET` (`asset`),
  KEY `IDX_ASSET_ACTIVITY_DOMAIN` (`domain`),
  CONSTRAINT `FK_ASSET_ACTIVITY_ASSET` FOREIGN KEY (`asset`) REFERENCES `asset` (`id`),
  CONSTRAINT `FK_ASSET_ACTIVITY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades sobre el Activo';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `asset_activity`
#

LOCK TABLES `asset_activity` WRITE;
ALTER TABLE `asset_activity` DISABLE KEYS;
ALTER TABLE `asset_activity` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `asset_feature`
#

DROP TABLE IF EXISTS `asset_feature`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `asset_feature` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `asset` int(4) NOT NULL COMMENT 'Identificador del Activo',
  `feature` int(4) NOT NULL COMMENT 'Identificador de la Caracteristica',
  PRIMARY KEY  (`id`),
  KEY `IDX_ASSET_FEATURE_ASSET` (`asset`),
  KEY `IDX_ASSET_FEATURE_FEATURE` (`feature`),
  KEY `IDX_ASSET_FEATURE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ASSET_FEATURE_ASSET` FOREIGN KEY (`asset`) REFERENCES `asset` (`id`),
  CONSTRAINT `FK_ASSET_FEATURE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ASSET_FEATURE_FEATURE` FOREIGN KEY (`feature`) REFERENCES `feature` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Caracteristicas por Activo';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `asset_feature`
#

LOCK TABLES `asset_feature` WRITE;
ALTER TABLE `asset_feature` DISABLE KEYS;
ALTER TABLE `asset_feature` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `auto_concept`
#

DROP TABLE IF EXISTS `auto_concept`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `auto_concept` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Concepto Automatico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` char(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Concepto Automatico',
  PRIMARY KEY  (`id`),
  KEY `IDX_AUTO_CONCEPT_DOMAIN` (`domain`),
  CONSTRAINT `FK_AUTO_CONCEPT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos Automaticos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `auto_concept`
#

LOCK TABLES `auto_concept` WRITE;
ALTER TABLE `auto_concept` DISABLE KEYS;
ALTER TABLE `auto_concept` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `balance`
#

DROP TABLE IF EXISTS `balance`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `balance` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Nombre del Balance',
  `removable` tinyint(1) default '0' COMMENT 'Indica se puede ser borrado por el usuario',
  `type` tinyint(2) default '0' COMMENT 'Tipo de Balance',
  PRIMARY KEY  (`id`),
  KEY `IDX_BALANCE_DOMAIN` (`domain`),
  CONSTRAINT `FK_BALANCE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Balances';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `balance`
#

LOCK TABLES `balance` WRITE;
ALTER TABLE `balance` DISABLE KEYS;
INSERT INTO `balance` VALUES (1,1,'BALANCE DE SITUACIN',0,0),(2,1,'CUENTA DE EXPLOTACIN',0,1),(3,1,'BALANCE DE SITUACIN (ABREVIADO)',0,0),(4,1,'CUENTA DE EXPLOTACIN (ABREVIADA)',0,1),(5,1,'ESTADO ABRV. DE CAMBIOS EN EL PATRIMONIO NETO, INGRESOS Y GASTOS',0,2);
ALTER TABLE `balance` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `balance_detail`
#

DROP TABLE IF EXISTS `balance_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `balance_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `balance` int(4) NOT NULL COMMENT 'Identificador del Balance',
  `code` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo del Detalle en el Balance',
  `description` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Descripcin del detalle de balance',
  `accounts` text collate latin1_spanish_ci COMMENT 'Cuentas separadas por comas, que forman el acumulado.',
  `sortKey` int(4) default '0' COMMENT 'Orden el que aparecera en el listado.',
  `title` tinyint(1) NOT NULL default '0',
  `internal_calculation` tinyint(1) NOT NULL default '0' COMMENT 'Indica si es un calculo interno, es decir si el contenido de accounts son referencias a la columna -code- de esta tabla',
  `visible` tinyint(1) NOT NULL default '1' COMMENT 'Si aparece o no en la impresion.',
  `zeroFlag` tinyint(1) NOT NULL default '0' COMMENT 'Flag que se activa cuando la cuenta o cuentas tienen valor 0.',
  `creditNature` tinyint(1) NOT NULL default '0' COMMENT 'Si es verdadero se hace una haber menos debe de las cuentas indicadas',
  PRIMARY KEY  (`id`),
  KEY `IDX_BALANCE_DETAIL_BALANCE` (`balance`),
  KEY `IDX_BALANCE_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_BALANCE_DETAIL_BALANCE` FOREIGN KEY (`balance`) REFERENCES `balance` (`id`),
  CONSTRAINT `FK_BALANCE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=300 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Balace';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `balance_detail`
#

LOCK TABLES `balance_detail` WRITE;
ALTER TABLE `balance_detail` DISABLE KEYS;
INSERT INTO `balance_detail` VALUES (1,1,1,'A0',' ACTIVO','',0,1,0,1,0,1),(2,1,1,'A','A) ACTIVO NO CORRIENTE','',1,0,0,1,0,1),(3,1,1,'A.I','	I. Inmovilizado intangible.',NULL,2,0,0,1,0,0),(4,1,1,'A.I.1',' 1. Desarrollo.','201,2801,2901',3,0,0,1,0,0),(5,1,1,'A.I.2',' 2. Concesiones.','202,2802,2902',4,0,0,1,0,0),(6,1,1,'A.I.3',' 3. Patentes, licencias, marcas y similares.','203,2803,2903',5,0,0,1,0,0),(7,1,1,'A.I.4','		4. Fondo de comercio.','204',6,0,0,1,0,0),(8,1,1,'A.I.5',' 5. Aplicaciones informticas.','206,2806,2906',7,0,0,1,0,0),(9,1,1,'A.I.6',' 6. Otro inmovilizado intangible.','205,209,2805,2905',8,0,0,1,0,0),(10,1,1,'A.I.T','','A.I.1,A.I.2,A.I.3,A.I.4,A.I.5,A.I.6',9,0,1,0,0,0),(11,1,1,'A.II','	II. Inmovilizado material.',NULL,10,0,0,1,0,0),(12,1,1,'A.II.1',' 1. Terrenos y construcciones.','210,211,2811,2910,2911',11,0,0,1,0,0),(13,1,1,'A.II.2',' 2. Instalaciones tcnicas, y otro inmovilizado material.','212,213,214,215,216,217,218,219,2812,2813,2814,2815,2816,2817,2818,2819,2912,2913,2914,2915,2916,2917,2918,2919',12,0,0,1,0,0),(14,1,1,'A.II.3','		3. Inmovilizado en curso y anticipos.','23',13,0,0,1,0,0),(15,1,1,'A.II.T','','A.II.1,A.II.2,A.II.3',14,0,1,0,0,0),(16,1,1,'A.III','	III. Inversiones inmobiliarias.',NULL,15,0,0,1,0,0),(17,1,1,'A.III.1',' 1. Terrenos.','220,2920',16,0,0,1,0,0),(18,1,1,'A.III.2',' 2. Construcciones.','221,282,2921',17,0,0,1,0,0),(19,1,1,'A.III.T','','A.III.1,A.III.2',18,0,1,0,0,0),(20,1,1,'A.IV','	IV. Inversiones en empresas del grupo y asociadas a largo plazo.',NULL,19,0,0,1,0,0),(21,1,1,'A.IV.1',' 1. Instrumentos de patrimonio.','2403,2404,2493,2494,293',20,0,0,1,0,0),(22,1,1,'A.IV.2',' 2. Crditos a empresas.','2423,2424,2953,2954',21,0,0,1,0,0),(23,1,1,'A.IV.3',' 3. Valores representativos de deuda.','2413,2414,2943,2944',22,0,0,1,0,0),(24,1,1,'A.IV.4','		4. Derivados.',NULL,23,0,0,1,0,0),(25,1,1,'A.IV.5','		5. Otros activos financieros.',NULL,24,0,0,1,0,0),(26,1,1,'A.IV.T','','A.IV.1,A.IV.2,A.IV.3,A.IV.4,A.IV.5',25,0,1,0,0,0),(27,1,1,'A.V','	V. Inversiones financieras a largo plazo.',NULL,26,0,0,1,0,0),(28,1,1,'A.V.1',' 1. Instrumentos de patrimonio.','2405,2495,250,259',27,0,0,1,0,0),(29,1,1,'A.V.2',' 2. Crditos a terceros','2425,252,253,254,2955,298',28,0,0,1,0,0),(30,1,1,'A.V.3',' 3. Valores representativos de deuda','2415,251,2945,297',29,0,0,1,0,0),(31,1,1,'A.V.4','		4. Derivados.','255',30,0,0,1,0,0),(32,1,1,'A.V.5','		5. Otros activos financieros.','258,26',31,0,0,1,0,0),(33,1,1,'A.V.T','','A.V.1,A.V.2,A.V.3,A.V.4,A.V.5',32,0,1,0,0,0),(34,1,1,'A.VI','	VI. Activos por impuesto diferido.','474',33,0,0,1,0,0),(35,1,1,'A.T','Total ACTIVO NO CORRIENTE','A.I.T,A.II.T,A.III.T,A.IV.T,A.V.T',34,0,1,0,0,0),(36,1,1,'B','B) ACTIVO CORRIENTE',NULL,35,0,0,1,0,0),(37,1,1,'B.I','	I. Activos no corrientes mantenidos para la venta.','580,581,582,583,584,599',36,0,0,1,0,0),(38,1,1,'B.II','	II. Existencias.',NULL,37,0,0,1,0,0),(39,1,1,'B.II.1','		1. Comerciales.','30,390',38,0,0,1,0,0),(40,1,1,'B.II.2','		2. Materias primas y otros aprovisionamientos.','31,32,391,392',39,0,0,1,0,0),(41,1,1,'B.II.3','		3. Productos en curso.','33,34,393,394',40,0,0,1,0,0),(42,1,1,'B.II.4','		4. Productos terminados.','35,395',41,0,0,1,0,0),(43,1,1,'B.II.5','		5. Subproductos, residuos y materiales recuperados.','36,396',42,0,0,1,0,0),(44,1,1,'B.II.6','		6. Anticipos a proveedores','407',43,0,0,1,0,0),(45,1,1,'B.II.T','','B.II.1,B.II.2,B.II.3,B.II.4,B.II.5,B.II.6',44,0,1,0,0,0),(46,1,1,'B.III','	III. Deudores comerciales y otras cuentas a cobrar.',NULL,45,0,0,1,0,0),(47,1,1,'B.III.1',' 1. Clientes por ventas y prestaciones de servicios.','430,431,432,435,436,437,490,4935',46,0,0,1,0,0),(48,1,1,'B.III.2',' 2. Clientes, empresas del grupo y asociadas.','433,434,4933,4934',47,0,0,1,0,0),(49,1,1,'B.III.3',' 3. Deudores varios.','44,5531,5533',48,0,0,1,0,0),(50,1,1,'B.III.4','		4. Personal.','460,544',49,0,0,1,0,0),(51,1,1,'B.III.5',' 5. Activos por impuesto corriente.','4709,4703,473',50,0,0,1,0,0),(52,1,1,'B.III.6',' 6. Otros crditos con las Administraciones Pblicas.','4700,4708,471,472',51,0,0,1,0,0),(53,1,1,'B.III.7','		7. Accionistas (socios) por desembolsos exigidos','5580',52,0,0,1,0,0),(54,1,1,'B.III.T','','B.III.1,B.III.2,B.III.3,B.III.4,B.III.5,B.III.6,B.III.7',53,0,1,0,0,0),(55,1,1,'B.IV','	IV. Inversiones en empresas del grupo y asociadas a corto plazo.',NULL,54,0,0,1,0,0),(56,1,1,'B.IV.1',' 1. Instrumentos de patrimonio.','5303,5304,5393,5394,593',55,0,0,1,0,0),(57,1,1,'B.IV.2',' 2. Crditos a empresas.','5323,5324,5343,5344,5953,5954',56,0,0,1,0,0),(58,1,1,'B.IV.3',' 3. Valores representativos de deuda.','5313,5314,5333,5334,5943,5944',57,0,0,1,0,0),(59,1,1,'B.IV.4','		4. Derivados.',NULL,58,0,0,1,0,0),(60,1,1,'B.IV.5',' 5. Otros activos financieros.','5353,5354,5523,5524',59,0,0,1,0,0),(61,1,1,'B.IV.T','','B.IV.1,B.IV.2,B.IV.3,B.IV.4,B.IV.5',60,0,1,0,0,0),(62,1,1,'B.V','	V. Inversiones financieras a corto plazo.',NULL,61,0,0,1,0,0),(63,1,1,'B.V.1',' 1. Instrumentos de patrimonio.','5305,540,5395,549',62,0,0,1,0,0),(64,1,1,'B.V.2',' 2. Crditos a empresas','5325,5345,542,543,547,5955,598',63,0,0,1,0,0),(65,1,1,'B.V.3',' 3. Valores representativos de deuda.','5315,5335,541,546,5945,597',64,0,0,1,0,0),(66,1,1,'B.V.4',' 4. Derivados.','5590,5593',65,0,0,1,0,0),(67,1,1,'B.V.5',' 5. Otros activos financieros.','5355,545,548,551,5525,565,566',66,0,0,1,0,0),(68,1,1,'B.V.T','','B.V.1,B.V.2,B.V.3,B.V.4,B.V.5',67,0,1,0,0,0),(69,1,1,'B.VI','	VI. Periodificaciones a corto plazo.','480,567',68,0,0,1,0,0),(70,1,1,'B.VII','	VII. Efectivo y otros activos lquidos equivalentes.',NULL,69,0,0,1,0,0),(71,1,1,'B.VII.1','		1. Tesorera.','570,571,572,573,574,575',70,0,0,1,0,0),(72,1,1,'B.VII.2','		2. Otros activos lquidos equivalentes.','576',71,0,0,1,0,0),(73,1,1,'B.VII.T','','B.VII.1,B.VII.2',72,0,1,0,0,0),(74,1,1,'B.T','Total ACTIVO CORRIENTE','B.I.T,B.II.T,B.III.T,B.IV.T,B.V.T,B.VI.T,B.VII.T',73,0,1,0,0,0),(75,1,1,'AB','TOTAL ACTIVO (A + B)','A.T,B.T',74,0,1,1,0,0),(76,1,1,'A0','PATRIMONIO NETO Y PASIVO',NULL,75,1,0,1,0,0),(77,1,1,'PA','A) PATRIMONIO NETO',NULL,76,0,0,1,0,0),(78,1,1,'PA.A-1','	A-1) Fondos propios.',NULL,77,0,0,1,0,0),(79,1,1,'PA.A-1.I','		I. Capital.',NULL,78,0,0,1,0,0),(80,1,1,'PA.A-1.I.1','			1. Capital escriturado.','100,101,102',79,0,0,1,0,0),(81,1,1,'PA.A-1.I.2',' 2. (Capital no exigido).','1030,1040',80,0,0,1,0,0),(82,1,1,'PA.A-1.I.T','','PA.A-1.I.1,PA.A-1.I.2',81,0,1,0,0,0),(83,1,1,'PA.A-1.II','		II. Prima de emisin.','110',82,0,0,1,0,0),(84,1,1,'PA.A-1.III','		III. Reservas.',NULL,83,0,0,1,0,0),(85,1,1,'PA.A-1.III.1',' 1. Legal y estatutarias.','112,1141',84,0,0,1,0,0),(86,1,1,'PA.A-1.III.2',' 2. Otras reservas.','113,1140,1142,1143,1144,115,119',85,0,0,1,0,0),(87,1,1,'PA.A-1.III.T','','PA.A-1.III.1,PA.A-1.III.2',86,0,1,0,0,0),(88,1,1,'PA.A-1.IV','		IV. (Acciones y participaciones en patrimonio propias).','108,109',87,0,0,1,0,0),(89,1,1,'PA.A-1.V','		V. Resultados de ejercicios anteriores.',NULL,88,0,0,1,0,0),(90,1,1,'PA.A-1.V.1','			1. Remanente.','120',89,0,0,1,0,0),(91,1,1,'PA.A-1.V.2','			2. (Resultados negativos de ejercicios anteriores).','121',90,0,0,1,0,0),(92,1,1,'PA.A-1.V.T','','PA.A-1.V.1,PA.A-1.V.2',91,0,1,0,0,0),(93,1,1,'PA.A-1.VI','		VI. Otras aportaciones de socios.','118',92,0,0,1,0,0),(94,1,1,'PA.A-1.VII','		VII. Resultado del ejercicio.','129',93,0,0,1,0,0),(95,1,1,'PA.A-1.VIII','		VIII. (Dividendo a cuenta).','557',94,0,0,1,0,0),(96,1,1,'PA.A-1.IX','		IX. Otros instrumentos de patrimonio neto.','111',95,0,0,1,0,0),(97,1,1,'PA.A-1.T','','PA.A-1.I.T,PA.A-1.II,PA.A-1.III.T,PA.A-1.IV,PA.A-1.V.T,PA.A-1.VI,PA.A-1.VII,PA.A-1.VIII,PA.A-1.IX',96,0,1,0,0,0),(98,1,1,'PA.A-2','	A-2) Ajustes por cambios de valor.',NULL,97,0,0,1,0,0),(99,1,1,'PA.A-2.I','		I. Activos financieros disponibles para la venta.','133',98,0,0,1,0,0),(100,1,1,'PA.A-2.II',' II. Operaciones de cobertura.','1340',99,0,0,1,0,0),(101,1,1,'PA.A-2.III','		III. Otros.','137,139',100,0,0,1,0,0),(102,1,1,'PA.A-3','	A-3) Subvenciones, donaciones y legados recibidos.','130,131,132',101,0,0,1,0,0),(103,1,1,'PA.A-2.T','','PA.A-2.I,PA.A-2.II,PA.A-2.III,PA.A-3',102,0,1,0,0,0),(104,1,1,'PA.T','Total PATRIMONIO NETO','PA.A-1.T,PA.A-2.T',103,0,1,0,0,0),(105,1,1,'PB','B) PASIVO NO CORRIENTE',NULL,104,0,0,1,0,0),(106,1,1,'PB.I','	I. Provisiones a largo plazo.',NULL,105,0,0,1,0,0),(107,1,1,'PB.I.1','		1. Obligaciones por prestaciones a largo plazo al personal.','140',106,0,0,1,0,0),(108,1,1,'PB.I.2','		2. Actuaciones medioambientales.','145',107,0,0,1,0,0),(109,1,1,'PB.I.3','		3. Provisiones por reestructuracin.','146',108,0,0,1,0,0),(110,1,1,'PB.I.4','		4. Otras provisiones.','141,142,143,147',109,0,0,1,0,0),(111,1,1,'PB.I.T','','PB.I.1,PB.I.2,PB.I.3,PB.I.4',110,0,1,0,0,0),(112,1,1,'PB.II','	II Deudas a largo plazo.',NULL,111,0,0,1,0,0),(113,1,1,'PB.II.1','		1. Obligaciones y otros valores negociables.','177,178,179',112,0,0,1,0,0),(114,1,1,'PB.II.2',' 2. Deudas con entidades de crdito.','1605,170',113,0,0,1,0,0),(115,1,1,'PB.II.3',' 3. Acreedores por arrendamiento financiero.','1625,174',114,0,0,1,0,0),(116,1,1,'PB.II.4','		4. Derivados.','176',115,0,0,1,0,0),(117,1,1,'PB.II.5',' 5. Otros pasivos financieros.','1615,1635,171,172,173,175,180,185,189',116,0,0,1,0,0),(118,1,1,'PB.II.T','','PB.II.1,PB.II.2,PB.II.3,PB.II.4,PB.II.5',117,0,1,0,0,0),(119,1,1,'PB.III',' III. Deudas con empresas del grupo y asociadas a largo plazo.','1603,1604,1613,1614,1623,1624,1633,1634',118,0,0,1,0,0),(120,1,1,'PB.IV','	IV. Pasivos por impuesto diferido.','479',119,0,0,1,0,0),(121,1,1,'PB.V','	V. Periodificaciones a largo plazo.','181',120,0,0,1,0,0),(122,1,1,'PB.T','Total PASIVO NO CORRIENTE','PB.I.T,PB.II.T,PB.III,PB.IV,PB.V,',121,0,1,0,0,0),(123,1,1,'PC','C) PASIVO CORRIENTE',NULL,122,0,0,1,0,0),(124,1,1,'PC.I','	I. Pasivos vinculados con activos no corrientes mantenidos para la venta.','585,586,587,588,589',123,0,0,1,0,0),(125,1,1,'PC.II','	II. Provisiones a corto plazo.','499,529',124,0,0,1,0,0),(126,1,1,'PC.III','	III. Deudas a corto plazo.',NULL,125,0,0,1,0,0),(127,1,1,'PC.III.1','		1. Obligaciones y otros valores negociables.','500,501,505,506',126,0,0,1,0,0),(128,1,1,'PC.III.2',' 2. Deudas con entidades de crdito.','5105,520,527',127,0,0,1,0,0),(129,1,1,'PC.III.3',' 3. Acreedores por arrendamiento financiero.','5125,524',128,0,0,1,0,0),(130,1,1,'PC.III.4',' 4. Derivados.','55905,55908',129,0,0,1,0,0),(131,1,1,'PC.III.5',' 5. Otros pasivos financieros.','1034,1044,190,192,194,509,5115,5135,5145,521,522,523,525,526,528,551,5525,5530,5532,555,5565,5566,560,561,569',130,0,0,1,0,0),(132,1,1,'PC.III.T','','PC.III.1,PC.III.2,PC.III.3,PC.III.4,PC.III.5',131,0,1,0,0,0),(133,1,1,'PC.IV',' IV. Deudas con empresas del grupo y asociadas a corto plazo.','5103,5104,5113,5114,5123,5124,5133,5134,5143,5144,5523,5524,5563,5564',132,0,0,1,0,0),(134,1,1,'PC.V','	V. Acreedores comerciales y otras cuentas a pagar.',NULL,133,0,0,1,0,0),(135,1,1,'PC.V.1','		1. Proveedores','400,401,405,406',134,0,0,1,0,0),(136,1,1,'PC.V.2','		2. Proveedores, empresas del grupo y asociadas.','403,404',135,0,0,1,0,0),(137,1,1,'PC.V.3','		3. Acreedores varios.','41',136,0,0,1,0,0),(138,1,1,'PC.V.4','		4. Personal (remuneraciones pendientes de pago).','465,466',137,0,0,1,0,0),(139,1,1,'PC.V.5',' 5. Pasivos por impuesto corriente.','47502',138,0,0,1,0,0),(140,1,1,'PC.V.6',' 6. Otras deudas con las Administraciones Pblicas.','4750,4751,4758,476,477',139,0,0,1,0,0),(141,1,1,'PC.V.7','		7. Anticipos de clientes.','438',140,0,0,1,0,0),(142,1,1,'PC.V.T','','PC.V.1,PC.V.2,PC.V.3,PC.V.4,PC.V.5,PC.V.6,PC.V.7',141,0,1,0,0,0),(143,1,1,'PC.VI','	VI. Periodificaciones a corto plazo.','485,568',142,0,0,1,0,0),(144,1,1,'PC.T','Total PASIVO CORRIENTE','PC.I,PC.II,PC.III.T,PC.IV,PC.V.T,PC.VI',143,0,1,0,0,0),(145,1,1,'PABC','TOTAL PATRIMONIO NETO Y PASIVO (A + B + C)','PA.T,PB.T,PC.T',144,0,1,1,0,0),(146,1,1,'DIF','DIFERENCIA (ACTIVO-PATRIMONIO NETO Y PASIVO))','AB,PABC',145,0,1,1,1,0),(147,1,2,'A','A OPERACIONES CONTINUADAS',NULL,0,0,0,1,0,0),(148,1,2,'1','	1. Importe neto de la cifra de negocios.',NULL,1,0,0,1,0,0),(149,1,2,'1a','		a) Ventas.','700,701,702,703,704,706,708,709',2,0,0,1,0,0),(150,1,2,'1b','		b) Prestaciones de servicios.','705',3,0,0,1,0,0),(151,1,2,'2',' 2. Variacin de existencias de productos terminados y en curso de fabricacin.','6930,71,7930',4,0,0,1,0,0),(152,1,2,'3','	3. Trabajos realizados por la empresa para su activo.','73',5,0,0,1,0,0),(153,1,2,'4','	4. Aprovisionamientos.',NULL,6,0,0,1,0,0),(154,1,2,'4.a',' a) Consumo de mercaderas.','600,6060,6080,6090,610',7,0,0,1,0,0),(155,1,2,'4.b',' b) Consumo de materias primas y otras materias consumibles.','601,602,6061,6062,6081,6082,6091,6092,611,612',8,0,0,1,0,0),(156,1,2,'4.c','		c) Trabajos realizados por otras empresas.','607',9,0,0,1,0,0),(157,1,2,'4.d',' d) Deterioro de mercaderas, materias primas y otros aprovisionamientos.','6931,6932,6933,7931,7932,7933',10,0,0,1,0,0),(158,1,2,'5','	5. Otros ingresos de explotacin.',NULL,11,0,0,1,0,0),(159,1,2,'5.a','		a) Ingresos accesorios y otros de gestin corriente.','75',12,0,0,1,0,0),(160,1,2,'5.b','		b) Subvenciones de explotacin incorporadas al resultado del ejercicio.','740,747',13,0,0,1,0,0),(161,1,2,'6','	6. Gastos de personal.',NULL,14,0,0,1,0,0),(162,1,2,'6.a',' a) Sueldos, salarios y asimilados.','640,641,6450',15,0,0,1,0,0),(163,1,2,'6.b','		b) Cargas sociales.','642,643,649',16,0,0,1,0,0),(164,1,2,'6.c',' c) Provisiones.','644,6457,7950,7957',17,0,0,1,0,0),(165,1,2,'7','	7. Otros gastos de explotacin.',NULL,18,0,0,1,0,0),(166,1,2,'7.a','		a) Servicios exteriores.','62',19,0,0,1,0,0),(167,1,2,'7.b','		b) Tributos.','631,634,636,639',20,0,0,1,0,0),(168,1,2,'7.c',' c) Prdidas, deterioro y variacin de provisiones por operaciones comerciales.','650,694,695,794,7954',21,0,0,1,0,0),(169,1,2,'7.d','		d) Otros gastos de gestin corriente.','651,659',22,0,0,1,0,0),(170,1,2,'8','	8. Amortizacin del inmovilizado.','68',23,0,0,1,0,0),(171,1,2,'9','	9. Imputacin de subvenciones de inmovilizado no financiero y otras.','746',24,0,0,1,0,0),(172,1,2,'10',' 10. Excesos de provisiones.','7951,7952,7955,7956',25,0,0,1,0,0),(173,1,2,'11','	11. Deterioro y resultado por enajenaciones del inmovilizado.',NULL,26,0,0,1,0,0),(174,1,2,'11.a','		a) Deterioros y prdidas.','690,691,692,790,791,792',27,0,0,1,0,0),(175,1,2,'11.b','		b) Resultados por enajenaciones y otras.','670,671,672,770,771,772',28,0,0,1,0,0),(176,1,2,'12','	12. Otros Resultados.','678,778',29,0,0,1,0,0),(177,1,2,'A.1','A.1) RESULTADO DE EXPLOTACIN (1+2+3+4+5+6+7+8+9+10+11+12)','1a,1b,3,4.a,4.b,4.c,4.d,,5.a,5.b,6.a,6.b,6.c,7.a,7.b,7.c,7.d,8,9,10,11.a,11.b,12',30,0,1,1,0,0),(178,1,2,'12','	12. Ingresos financieros.',NULL,31,0,0,1,0,0),(179,1,2,'12.a','		a) De participaciones en instrumentos de patrimonio.',NULL,32,0,0,1,0,0),(180,1,2,'12.a1',' a1) En empresas del grupo y asociadas.','7600,7601',33,0,0,1,0,0),(181,1,2,'12.a2',' a2) En terceros.','7602,7603',34,0,0,1,0,0),(182,1,2,'12.b','		b) De valores negociables y otros instrumentos financieros.',NULL,35,0,0,1,0,0),(183,1,2,'12.b1',' b1) De empresas del grupo y asociadas.','7610,7611,76200,76201,76210,76211',36,0,0,1,0,0),(184,1,2,'12.b2',' b2) De terceros.','7612,7613,7622,7623,76212,76213,767,769',37,0,0,1,0,0),(185,1,2,'13','	13. Gastos financieros.',NULL,38,0,0,1,0,0),(186,1,2,'13.a',' a) Por deudas con empresas del grupo y asociadas.','6610,6611,6615,6616,6620,6621,6640,6641,6650,6651,6654,6655',39,0,0,1,0,0),(187,1,2,'13.b',' b) Por deudas con terceros.','6612,6613,6617,6618,6622,6623,6624,6642,6643,6652,6653,6656,6657,669',40,0,0,1,0,0),(188,1,2,'13.c','		c) Por actualizacin de provisiones.','660',41,0,0,1,0,0),(189,1,2,'14','	14. Variacin de valor razonable en instrumentos financieros.',NULL,42,0,0,1,0,0),(190,1,2,'14.a',' a) Cartera de negociacin y otros.','6630,6631,6633,7630,7631,7633',43,0,0,1,0,0),(191,1,2,'14.b',' b) Imputacin al resultado del ejercicio por activos financieros disponibles para la venta.','6632,7632',44,0,0,1,0,0),(192,1,2,'15','	15. Diferencias de cambio.','668,768',45,0,0,1,0,0),(193,1,2,'16','	16. Deterioro y resultado por enajenaciones de instrumentos financieros.',NULL,46,0,0,1,0,0),(194,1,2,'16.a','		a) Deterioros y prdidas.','696,697,698,699,796,797,798,799',47,0,0,1,0,0),(195,1,2,'16.b','		b) Resultados por enajenaciones y otras.','666,667,673,675,766,773,775',48,0,0,1,0,0),(196,1,2,'A.2','A.2) RESULTADO FINANCIERO (12+13+14+15+16)','12.a1,12.a2,12.b1,12.b2,13.a,13.b,13.c,14.a,14.b,15,16.a,16.b',49,0,1,1,0,0),(197,1,2,'A.3','A.3) RESULTADO ANTES DE IMPUESTOS (A.1+A.2)','A.1,A.2',50,0,1,1,0,0),(198,1,2,'17',' 17. Impuestos sobre beneficios.','6300,6301,633,638',51,0,0,1,0,0),(199,1,2,'A.4','A.4) RESULTADO DEL EJ. PROCEDENTE DE OP. CONTINUADAS (A.3+17)','A.3,17',52,0,1,1,0,0),(200,1,2,'B','B) OPERACIONES INTERRUMPIDAS',NULL,53,0,0,1,0,0),(201,1,2,'18','	18. Resultado del ejercicio procedente de operaciones interrumpidas neto de impuestos.',NULL,54,0,0,1,0,0),(202,1,2,'A.5','A.5) RESULTADO DEL EJERCICIO (A.4+18)','A.4,18',55,0,1,1,0,0),(203,1,3,'A','ACTIVO NO CORRIENTE','',0,1,0,1,0,0),(204,1,3,'A.I','Inmovilizado intangible','20,280,290',1,0,0,1,0,0),(205,1,3,'A.II','Inmovilizado material','21,281,291,23',2,0,0,1,0,0),(206,1,3,'A.III','Inversiones inmobiliarias','22,282,292',3,0,0,1,0,0),(207,1,3,'A.IV','Inversiones en empresas del grupo y asociadas  L/P','2403,2404,2413,2414,2423,2424,2493,2494,293,2943,2944,2953,2954',4,0,0,1,0,0),(208,1,3,'A.V','Inversiones financieras a largo plazo','2405,2415,2425,2495,250,251,252,253,254,255,257,258',5,0,0,1,0,0),(209,1,3,'A.VI','Activos por Impuesto diferido','474',6,0,0,1,0,0),(210,1,3,'A.T','','A.I,A.II,A.III,A.IV,A.V,A.VI',7,0,1,1,0,0),(211,1,3,'B','ACTIVO CORRIENTE','',8,1,0,1,0,0),(212,1,3,'B.I','Activos no corrientes mantenidos para la venta','580,581,582,583,584,599',9,0,0,1,0,0),(213,1,3,'B.II','Existencias','30,31,32,33,34,35,36,39,407',10,0,0,1,0,0),(214,1,3,'B.III','Deudores comerciales y otras cuentas a cobrar','',11,1,0,1,0,0),(215,1,3,'B.III.1','Clientes por ventas y prestaciones de servicios','430,431,432,433,434,435,436,437,490,493',12,0,0,1,0,0),(216,1,3,'B.III.2','Accionistas (socios) por desembolsos exigidos','5580',13,0,0,1,0,0),(217,1,3,'B.III.3','Otros deudores','44,460,470,471,472,5531,5533,544',14,0,0,1,0,0),(218,1,3,'B.IV','Inversiones en empresas del grupo y asociadas C/P','5303,5304,5313,5314,5323,5324,5333,5334,5343,5344,5353,5354,5393,5394,5523,5524,593,5943,5944,5953,5954',15,0,0,1,0,0),(219,1,3,'B.V','Inversiones financieras a corto plazo','5305,5315,5325,5335,5345,5355,5395,540,541,542,543,545,546,547,548,549,551,5525,5590,5593,565,566,5945,5955,597,598',16,0,0,1,0,0),(220,1,3,'B.VI','Periodificaciones a corto plazo','580,567',17,0,0,1,0,0),(221,1,3,'B.VII','Efectivo y otros activos lquidos equivalentes','57',18,0,0,1,0,0),(222,1,3,'B.T','','B.I,B.II,B.III.1,B.III.2,B.III.3,B.IV,B.V,B.VI,B.VII',19,0,1,1,0,0),(223,1,3,'AB','TOTAL ACTIVO','A.T,B.T',20,0,1,1,0,0),(224,1,3,'PA','PATRIMONIO NETO','',21,1,0,1,0,0),(225,1,3,'PA.1','Fondos propios','',22,1,0,1,0,0),(226,1,3,'PA.1.I','Capital','',23,1,0,1,0,0),(227,1,3,'PA.1.I.1','Capital escriturado','100,101,102',24,0,0,1,0,0),(228,1,3,'PA.1.I.2','Capital no exigido','1030,1040,',25,0,0,1,0,0),(229,1,3,'PA.1.II','Prima de emisin','110',26,0,0,1,0,0),(230,1,3,'PA.1.III','Reservas','112,113,114,115,119,108,109',27,0,0,1,0,0),(231,1,3,'PA.1.IV','Acciones y participaciones en patrimonio propias','108,109',28,0,0,1,0,0),(232,1,3,'PA.1.V','Resultado de ejercicios anteriores','120,121',29,0,0,1,0,0),(233,1,3,'PA.1.VI','Otras aportaciones de socios','118',30,0,0,1,0,0),(234,1,3,'PA.1.VII','Resultado del ejercicio','129',31,0,0,1,0,0),(235,1,3,'PA.1.VIII','Dividendo a cuenta ','557',32,0,0,1,0,0),(236,1,3,'PA.1.IX','Otros instrumentos de patrimonio neto','111',33,0,0,1,0,0),(237,1,3,'PA.2','Ajustes por cambios de valor','133,1340,137',34,0,0,1,0,0),(238,1,3,'PA.3','Subvenciones, donaciones y legados recibidos','130,131,132',35,0,0,1,0,0),(239,1,3,'PA.T','','PA.1.I.1,PA.1.I.2,PA.1.II,PA.1.III,PA.1.IV,PA.1.V,PA.1.VI,PA.1.VII,PA.1.VIII,PA.1.IX,PA.2,PA.3',36,0,1,1,0,0),(240,1,3,'PB','PASIVO NO CORRIENTE','',37,1,0,1,0,0),(241,1,3,'PB.I','Provisiones a largo plazo','14',38,0,0,1,0,0),(242,1,3,'PB.II','Deudas a largo plazo','',39,0,0,1,0,0),(243,1,3,'PB.II.1','Deudas con entidades de crdito','1605,170',40,0,0,1,0,0),(244,1,3,'PB.II.2','Acreedores por arrendamiento financiero','1625,174',41,0,0,1,0,0),(245,1,3,'PB.II.3','Otras deudas a largo plazo','1615,1635,171,172,173,175,176,177,178,179,180,185,189',42,0,0,1,0,0),(246,1,3,'PB.III','Deudas con empresas del grupo y asociadas L/P','1603,1604,1613,1614,1623,1624,1633,1634',43,0,0,1,0,0),(247,1,3,'PB.IV','Pasivos por impuesto diferido','479',44,0,0,1,0,0),(248,1,3,'PB.V','Periodificaciones a largo plazo','181',45,0,0,1,0,0),(249,1,3,'PB.T','','PB.I,PB.II,PB.II.1,PB.II.2,PB.II.3,PB.III,PB.IV,PB.V',46,0,1,1,0,0),(250,1,3,'PC','PASIVO CORRIENTE','',47,1,0,1,0,0),(251,1,3,'PC.I','Pasivos vinculados con activos no corrientes mantenidos para la venta','585,586,587,588,589',48,0,0,1,0,0),(252,1,3,'PC.II','Provisiones a corto plazo','499,529',49,0,0,1,0,0),(253,1,3,'PC.III','Deudas a corto plazo','',50,1,0,1,0,0),(254,1,3,'PC.III.1','Deudas con entidades de crdito','5105,520,527',51,0,0,1,0,0),(255,1,3,'PC.III.2','Acreedores por arrendamiento financiero','5125,524',52,0,0,1,0,0),(256,1,3,'PC.III.3','Otras deudas a corto plazo','1034,1044,190,192,194,500,501,505,509,5115,5135,5145,521,522,523,525,526,528,551,5525,5530,5532,555,5565,5566,5595,5598,560,561,569',53,0,0,1,0,0),(257,1,3,'PC.IV','Deudas con empresas del grupo y asociadas C/P','5103,5104,5113,5114,5123,5124,5133,5134,5143,5144,5523,5524,5563,5564',54,0,0,0,0,0),(258,1,3,'PC.V','Acreedores comerciales y otras cuentas a pagar','',55,0,0,1,0,0),(259,1,3,'PC.V.1','Proveedores','400,401,403,404,405,406',56,0,0,1,0,0),(260,1,3,'PC.V.2','Otros acreedores','41,438,465,466,475,476,477',57,0,0,1,0,0),(261,1,3,'PC.VI','Periodificaciones a corto plazo','485,568',58,0,0,1,0,0),(262,1,3,'PC.T','','PC.I,PC.II,PC.III,PC.III.1,PC.III.2,PC.III.3,PC.IV,PC.V,PC.V.1,PC.V.2,PC.VI',59,0,0,1,0,0),(263,1,3,'P.T','TOTAL PATRIMONIO NETO Y PASIVO','PA.T,PB.T,PC.T',60,0,0,1,0,0),(264,1,4,'1','Importe neto de la cifra de negocios','700,701,702,703,704,705,706,708,709',0,0,0,1,0,0),(265,1,4,'2','Variacin de existencias de productos terminados y en curso de fabricacin','6930,71,7930',1,0,0,1,0,0),(266,1,4,'3','Trabajos realizados por la empresa para su activo','73',2,0,0,1,0,0),(267,1,4,'4','Aprovisionamientos','600,601,602,606,607,608,609,61,6931,6932,6933,7931,7932,7933',3,0,0,1,0,0),(268,1,4,'5','Otros ingresos de explotacin','740,747,75',4,0,0,1,0,0),(269,1,4,'6','Gastos de personal','64,7950,7957',5,0,0,1,0,0),(270,1,4,'7','Otros gastos de explotacin','62,631,634,636,639,65694,695,794,7954',6,0,0,1,0,0),(271,1,4,'8','Amortizacin de inmovilizado','68',7,0,0,1,0,0),(272,1,4,'9','Imputacin de subvenciones de inmovilizado no financiero y otras','746',8,0,0,1,0,0),(273,1,4,'10','Excesos de provisiones','7951,7952,7955,7956',9,0,0,1,0,0),(274,1,4,'11','Deterioro y resultado por enejenaciones del inmovilizado','670,671,672,690,691,692,770,771,772,790,791,792',10,0,0,1,0,0),(275,1,4,'A','RESULTADO DE EXPLOTACIN','1,2,3,4,5,6,7,8,9,10,11',11,0,1,1,0,0),(276,1,4,'12','Ingresos financieros','760,761,762,767,769',12,0,0,1,0,0),(277,1,4,'13','Gastos financieros','660,661,662,664,665,669',13,0,0,1,0,0),(278,1,4,'14','Variacin de valor razonable en instrumentos financieros','663,763',14,0,0,1,0,0),(279,1,4,'15','Diferencias de cambio','668,768',15,0,0,1,0,0),(280,1,4,'16','Deterioro y resultado por enajenaciones de instrum','666,667,673,675,696,697,698,699,766,773,775,796,797,798,799',16,0,0,1,0,0),(281,1,4,'B','RESULTADO FINANCIERO','12,13,14,15,16',17,0,1,1,0,0),(282,1,4,'C','RESULTADO ANTES DE IMPUESTOS','A,B',18,0,1,1,0,0),(283,1,4,'17','Impuestos sobre beneficios','6300,6301,633,638',19,0,0,1,0,0),(284,1,4,'D','RESULTADO DEL EJERCICIO','C,17',20,0,1,1,0,0),(285,1,5,'A','Resultado de la cuenta de prdidas y ganancias','',0,1,0,1,0,0),(286,1,5,'1','Ingresos y gastos imputados directamente al patrimonio neto','',1,1,0,1,0,0),(287,1,5,'I','Por valoracin de instrumentos financieros','800,89,900,991,992',2,0,0,1,0,0),(288,1,5,'II','Por coberturas de flujos de efectivo','810,910',3,0,0,1,0,0),(289,1,5,'III','Subvenciones, donaciones y legados recibidos','94',4,0,0,1,0,0),(290,1,5,'IV','Por ganancias y prdidas actuariales y otros ajust','85,95',5,0,0,1,0,0),(291,1,5,'V','Efecto impositivo','8300,8301,833,834,835,838',6,0,0,1,0,0),(292,1,5,'B','Total Ingresos y gastos imputados directamente al patrimonio neto','I,II,III,IV,V',7,0,1,1,0,0),(293,1,5,'2','Transferencias a la cuenta de P Y G','',8,1,0,1,0,0),(294,1,5,'VI','Por valoracin de instrumentos financieros','802,902,993,994',9,0,0,1,0,0),(295,1,5,'VII','Por coberturas de flujos de efectivo','810,912',10,0,0,1,0,0),(296,1,5,'VIII','Subvenciones, donaciones y legados recibidos','84',11,0,0,1,0,0),(297,1,5,'IX','Efecto impositivo','8301,836,837',12,0,0,1,0,0),(298,1,5,'C','Total transferencias a la cuentad de P Y G','VI,VII,VIII,IX',13,0,1,1,0,0),(299,1,5,'D','TOTAL DE INGRESOS Y GASTOS RECONOCIDOS','A,B,C',14,0,1,1,0,0);
ALTER TABLE `balance_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `bank`
#

DROP TABLE IF EXISTS `bank`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `bank` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Entidad Bancaria',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Entidad Bancaria',
  `code` varchar(4) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo de la Entidad Bancaria',
  PRIMARY KEY  (`id`),
  KEY `IDX_BANK_DOMAIN` (`domain`),
  CONSTRAINT `FK_BANK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=204 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Entidades Bancarias';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `bank`
#

LOCK TABLES `bank` WRITE;
ALTER TABLE `bank` DISABLE KEYS;
INSERT INTO `bank` VALUES (1,1,'ALLFUNDS BANK, S.A.','0011'),(2,1,'ALTAE BANCO, S.A.','0099'),(3,1,'BANCA MARCH, S.A.','0061'),(4,1,'BANCA PUEYO, S.A.','0078'),(5,1,'BANCO ALCALA, S.A.','0188'),(6,1,'BANCO ALICANTINO DE COMERCIO, S.A.','0083'),(7,1,'BANCO ARABE ESPAOL, S.A.','0136'),(8,1,'BANCO BANIF, S.A.','0086'),(9,1,'BANCO BILBAO VIZCAYA ARGENTARIA, S.A.','0182'),(10,1,'BANCO CAIXA GERAL, S.A.','0130'),(11,1,'BANCO CETELEM, S.A.','0225'),(12,1,'BANCO CONDAL, S.A.','0021'),(13,1,'BANCO COOPERATIVO ESPAOL, S.A.','0198'),(14,1,'BANCO DE ALBACETE, S.A.','0091'),(15,1,'BANCO DE ANDALUCIA, S.A.','0004'),(16,1,'BANCO DE CASTILLA, S.A.','0082'),(17,1,'BANCO DE CREDITO BALEAR, S.A.','0024'),(18,1,'BANCO DE CREDITO LOCAL DE ESPAA, S.A.','1004'),(19,1,'BANCO DE DEPOSITOS, S.A.','0003'),(20,1,'BANCO DE EUROPA, S.A.','0133'),(21,1,'BANCO DE FINANZAS E INVERSIONES, S.A.','0186'),(22,1,'BANCO DE GALICIA, S.A.','0097'),(23,1,'BANCO DE LA PEQUEA Y MEDIANA EMPRESA, S.A.','0142'),(24,1,'BANCO DE MADRID, S.A.','0059'),(25,1,'BANCO DE PROMOCION DE NEGOCIOS, S.A. (PROMOBANC)','0132'),(26,1,'BANCO DE SABADELL, S.A.','0081'),(27,1,'BANCO DE VALENCIA, S.A.','0093'),(28,1,'BANCO DE VASCONIA, S.A.','0095'),(29,1,'BANCO DEPOSITARIO BBVA, S.A.','0057'),(30,1,'BANCO ESPAOL DE CREDITO, S.A.','0030'),(31,1,'BANCO ETCHEVERRIA, S.A.','0031'),(32,1,'BANCO EUROPEO DE FINANZAS, S.A.','0184'),(33,1,'BANCO EXELBANK, S.A.','0228'),(34,1,'BANCO FINANTIA SOFINLOC, S.A.','0220'),(35,1,'BANCO GALLEGO, S.A.','0046'),(36,1,'BANCO GUIPUZCOANO, S.A.','0042'),(37,1,'BANCO HALIFAX HISPANIA, S.A.','0217'),(38,1,'BANCO INDUSTRIAL DE BILBAO, S.A.','0113'),(39,1,'BANCO INVERSIS NET, S.A.','0232'),(40,1,'BANCO LIBERTA, S.A.','0115'),(41,1,'BANCO OCCIDENTAL, S.A.','0121'),(42,1,'BANCO PASTOR, S.A.','0072'),(43,1,'BANCO POPULAR ESPAOL, S.A.','0075'),(44,1,'BANCO POPULAR HIPOTECARIO, S.A.','0216'),(45,1,'BANCO SANTANDER CENTRAL HISPANO, S.A.','0049'),(46,1,'BANCO SERVICIOS FINANCIEROS CAJA MADRID-MAPFRE,SA','0063'),(47,1,'BANCO URQUIJO SABADELL BANCA PRIVADA, S.A.','0185'),(48,1,'BANCOFAR, S.A.','0125'),(49,1,'BANCOPOPULAR-E, S.A.','0229'),(50,1,'BANESTO BANCO DE EMISIONES, S.A.','0038'),(51,1,'BANKINTER, S.A.','0128'),(52,1,'BANKOA, S.A.','0138'),(53,1,'BANQUE MAROCAINE COMMERCE EXTERIEUR INTERNAT.,S.A.','0219'),(54,1,'BARCLAYS BANK, S.A.','0065'),(55,1,'BBVA BANCO DE FINANCIACION, S.A.','0129'),(56,1,'BNP PARIBAS ESPAA, S.A.','0058'),(57,1,'CITIBANK ESPAA, S.A.','0122'),(58,1,'DEUTSCHE BANK CREDIT, S.A.','0205'),(59,1,'DEUTSCHE BANK, S.A.E.','0019'),(60,1,'DEXIA SABADELL BANCO LOCAL, S.A.','0231'),(61,1,'EBN BANCO DE NEGOCIOS, S.A.','0211'),(62,1,'FINANZIA, BANCO DE CREDITO, S.A.','0009'),(63,1,'GENERAL ELECTRIC CAPITAL BANK, S.A.','0223'),(64,1,'OPEN BANK SANTANDER CONSUMER, S.A.','0073'),(65,1,'POPULAR BANCA PRIVADA, S.A.','0233'),(66,1,'PRIVAT BANK, S.A.','0200'),(67,1,'RBC DEXIA INVESTOR SERVICES ESPAA, S.A.','0094'),(68,1,'SANTANDER CONSUMER FINANCE, S.A.','0224'),(69,1,'SANTANDER INVESTMENT, S.A.','0036'),(70,1,'UBS ESPAA, S.A.','0226'),(71,1,'UNOE BANK, S.A.','0227'),(72,1,'BILBAO BIZKAIA KUTXA,AURREZKI KUTXA ETA BAHITETXEA','2095'),(73,1,'C.A.M.P. CIRCULO CATOLICO DE OBREROS DE BURGOS','2017'),(74,1,'CAIXA AFORROS VIGO,OURENSE E PONTEVEDRA(CAIXANOVA)','2080'),(75,1,'CAIXA D\'ESTALVIS COMARCAL DE MANLLEU','2040'),(76,1,'CAIXA D\'ESTALVIS DE CATALUNYA','2013'),(77,1,'CAIXA D\'ESTALVIS DE GIRONA','2030'),(78,1,'CAIXA D\'ESTALVIS DE MANRESA','2041'),(79,1,'CAIXA D\'ESTALVIS DE SABADELL','2059'),(80,1,'CAIXA D\'ESTALVIS DE TARRAGONA','2073'),(81,1,'CAIXA D\'ESTALVIS DE TERRASSA','2074'),(82,1,'CAIXA D\'ESTALVIS DEL PENEDES','2081'),(83,1,'CAIXA D\'ESTALVIS LAIETANA','2042'),(84,1,'CAJA AH. VALENCIA, CASTELLON Y ALICANTE, BANCAJA','2077'),(85,1,'CAJA DE AH. PROV. SAN FERNANDO DE SEVILLA Y JEREZ','2071'),(86,1,'CAJA DE AHORRO PROVINCIAL DE GUADALAJARA','2032'),(87,1,'CAJA DE AHORROS DE ASTURIAS','2048'),(88,1,'CAJA DE AHORROS DE CASTILLA-LA MANCHA','2105'),(89,1,'CAJA DE AHORROS DE GALICIA','2091'),(90,1,'CAJA DE AHORROS DE LA INMACULADA DE ARAGON','2086'),(91,1,'CAJA DE AHORROS DE LA RIOJA','2037'),(92,1,'CAJA DE AHORROS DE MURCIA','2043'),(93,1,'CAJA DE AHORROS DE SALAMANCA Y SORIA','2104'),(94,1,'CAJA DE AHORROS DE SANTANDER Y CANTABRIA','2066'),(95,1,'CAJA DE AHORROS DE VITORIA Y ALAVA','2097'),(96,1,'CAJA DE AHORROS DEL MEDITERRANEO','2090'),(97,1,'CAJA DE AHORROS MUNICIPAL DE BURGOS','2018'),(98,1,'CAJA DE AHORROS Y M.P. DE AVILA','2094'),(99,1,'CAJA DE AHORROS Y M.P. DE CORDOBA','2024'),(100,1,'CAJA DE AHORROS Y M.P. DE EXTREMADURA','2099'),(101,1,'CAJA DE AHORROS Y M.P. DE GIPUZKOA Y SAN SEBASTIAN','2101'),(102,1,'CAJA DE AHORROS Y M.P. DE LAS BALEARES','2051'),(103,1,'CAJA DE AHORROS Y M.P. DE MADRID','2038'),(104,1,'CAJA DE AHORROS Y M.P. DE NAVARRA','2054'),(105,1,'CAJA DE AHORROS Y M.P. DE ONTINYENT','2045'),(106,1,'CAJA DE AHORROS Y M.P. DE SEGOVIA','2069'),(107,1,'CAJA DE AHORROS Y M.P. DE ZARAGOZA, ARAGON Y RIOJA','2085'),(108,1,'CAJA DE AHORROS Y PENSIONES DE BARCELONA','2100'),(109,1,'CAJA ESPAA DE INVERSIONES, CAJA DE AHORROS Y M.P.','2096'),(110,1,'CAJA GENERAL DE AHORROS DE CANARIAS','2065'),(111,1,'CAJA GENERAL DE AHORROS DE GRANADA','2031'),(112,1,'CAJA INSULAR DE AHORROS DE CANARIAS','2052'),(113,1,'CAJA PROVINCIAL DE AHORROS DE JAEN','2092'),(114,1,'COLONYA - CAIXA D\'ESTALVIS DE POLLENSA','2056'),(115,1,'CONFEDERACION ESPAOLA DE CAJAS DE AHORROS','2000'),(116,1,'M.P. Y CAJA DE AHORROS DE HUELVA Y SEVILLA','2098'),(117,1,'M.P. Y CAJA GENERAL AHORROS DE BADAJOZ','2010'),(118,1,'M.P.C.A. RONDA, CADIZ, ALMERIA, MALAGA Y ANTEQUERA','2103'),(119,1,'CAIXA DE C. DELS ENGINYERS-C.C. INGENIEROS, S.C.C','3025'),(120,1,'CAIXA DELS ADVOCATS-CAJA DE LOS ABOGADOS, S.C.C.','3171'),(121,1,'CAIXA POPULAR-CAIXA RURAL, S.C.C.V.','3159'),(122,1,'CAIXA R. ALBALAT DELS SORELLS, C.C.V.','3186'),(123,1,'CAIXA R. ALTEA, C.C.V.','3045'),(124,1,'CAIXA R. BENICARLO, S.C.C.V.','3162'),(125,1,'CAIXA R. D\'ALGEMESI, S.C.V.C.','3117'),(126,1,'CAIXA R. DE BALEARS, S.C.C.','3147'),(127,1,'CAIXA R. DE CALLOSA D\'EN SARRIA, C.C.V.','3105'),(128,1,'CAIXA R. DE L\'ALCUDIA, S.C.V.C.','3096'),(129,1,'CAIXA R. DE TURIS, C.C.V.','3123'),(130,1,'CAIXA R. GALEGA, S.C.C.L.G.','3070'),(131,1,'CAIXA R. LA VALL \'S. ISIDRO\', S.C.C.V.','3111'),(132,1,'CAIXA R. S. VICENT FERRER DE LA VALL D\'UIXO,C.C.V.','3102'),(133,1,'CAIXA R. VINAROS, S.C.C.V.','3174'),(134,1,'CAIXA R.S.JOSEP DE VILAVELLA, S.C.C.V.','3160'),(135,1,'CAIXA RURAL LES COVES DE VINROMA, S.C.C.V.','3166'),(136,1,'CAIXA RURAL TORRENT C.C.V.','3118'),(137,1,'CAJA CAMINOS, S.C.C.','3172'),(138,1,'CAJA CAMPO, CAJA RURAL, S.C.C.','3094'),(139,1,'CAJA DE ARQUITECTOS S.C.C.','3183'),(140,1,'CAJA DE CREDITO DE ALCOY, C.C.V. (EN LIQUIDACION)','3184'),(141,1,'CAJA DE CREDITO DE PETREL, CAJA RURAL, C.C.V.','3029'),(142,1,'CAJA ESCOLAR DE FOMENTO, S.C.C.','3146'),(143,1,'CAJA LABORAL POPULAR C.C.','3035'),(144,1,'CAJA R. \'NUESTRA MADRE DEL SOL\', S.C.A.C.','3115'),(145,1,'CAJA R. ARAGONESA Y DE LOS PIRINEOS, S.C.C.','3189'),(146,1,'CAJA R. CASTELLON S. ISIDRO, S.C.C.V.','3114'),(147,1,'CAJA R. CATOLICO AGRARIA, S.C.C.V.','3110'),(148,1,'CAJA R. CENTRAL, S.C.C.','3005'),(149,1,'CAJA R. COMARCAL DE MOTA DEL CUERVO, S.C.L.C.A.','3116'),(150,1,'CAJA R. DE ALBACETE, S.C.C.','3056'),(151,1,'CAJA R. DE ALBAL, C.C.V.','3150'),(152,1,'CAJA R. DE ALGINET, S.C.C.V.','3179'),(153,1,'CAJA R. DE ALMENDRALEJO, S.C.C.','3001'),(154,1,'CAJA R. DE ARAGON, S.C.C.','3021'),(155,1,'CAJA R. DE ASTURIAS, S.C.C.','3059'),(156,1,'CAJA R. DE BETXI, S.C.C.V.','3138'),(157,1,'CAJA R. DE BURGOS,S.C.C.','3060'),(158,1,'CAJA R. DE CANARIAS, S.C.C.','3177'),(159,1,'CAJA R. DE CASAS IBAEZ, S.C. - C.C.A.,','3127'),(160,1,'CAJA R. DE CASINOS S.C.C.V.','3137'),(161,1,'CAJA R. DE CAETE TORRES NTRA.SRA.DEL CAMPO,S.C.A.','3104'),(162,1,'CAJA R. DE CHESTE, S.C.C.','3121'),(163,1,'CAJA R. DE CIUDAD REAL, S.C.C.','3062'),(164,1,'CAJA R. DE CORDOBA, S.C.C.','3063'),(165,1,'CAJA R. DE CUENCA, S.C.C.','3064'),(166,1,'CAJA R. DE EXTREMADURA, S.C.C.','3009'),(167,1,'CAJA R. DE FUENTEPELAYO, S.C.C.','3022'),(168,1,'CAJA R. DE GIJON, C.C','3007'),(169,1,'CAJA R. DE GRANADA, S.C.C.','3023'),(170,1,'CAJA R. DE GUISSONA, S.C.C.','3140'),(171,1,'CAJA R. DE JAEN, S.C.C.','3067'),(172,1,'CAJA R. DE LA CARLOTA, S.C.A.C.L. (EN LIQUIDACION)','3154'),(173,1,'CAJA R. DE LA RODA, S.C.C. DE CASTILLA LA MANCHA','3128'),(174,1,'CAJA R. DE NAVARRA, S.C.C.','3008'),(175,1,'CAJA R. DE SALAMANCA, S.C.C.','3016'),(176,1,'CAJA R. DE SEGOVIA, C.C.','3078'),(177,1,'CAJA R. DE SORIA, S.C.C.','3017'),(178,1,'CAJA R. DE TENERIFE, S.C.C.','3076'),(179,1,'CAJA R. DE TERUEL, S.C.C.','3080'),(180,1,'CAJA R. DE TOLEDO, S.C.C.','3081'),(181,1,'CAJA R. DE UTRERA, S.C.A.L.C.','3020'),(182,1,'CAJA R. DE VILLAMALEA, S.C.C.A. CASTILLA-LA MANCHA','3144'),(183,1,'CAJA R. DE VILLAR C.C.V.','3152'),(184,1,'CAJA R. DE ZAMORA, C.C.','3085'),(185,1,'CAJA R. DEL DUERO, S.C.C.L.','3083'),(186,1,'CAJA R. DEL MEDITERRANEO, RURALCAJA, S.C.C.','3082'),(187,1,'CAJA R. DEL SUR, S. COOP. DE CREDITO','3187'),(188,1,'CAJA R. LA JUNQUERA DE CHILCHES, S.C.C.V.','3157'),(189,1,'CAJA R. NTRA. SRA. DE GUADALUPE, S.C.C.A.','3089'),(190,1,'CAJA R. NTRA. SRA. DEL ROSARIO, S.C.A.C.','3098'),(191,1,'CAJA R. NTRA. SRA. LA ESPERANZA DE ONDA, S.C.C.V.','3134'),(192,1,'CAJA R. S. FORTUNATO, S.C.C.C.L.','3161'),(193,1,'CAJA R. S. ISIDRO DE VILAFAMES, S.C.C.V.','3165'),(194,1,'CAJA R. S. JAIME ALQUERIAS NIO PERDIDO S.C.C.V.','3119'),(195,1,'CAJA R. S. JOSE DE ALCORA S.C.C.V.','3113'),(196,1,'CAJA R. S. JOSE DE ALMASSORA, S.C.C.V.','3130'),(197,1,'CAJA R. S. JOSE DE BURRIANA, S.C.C.V.','3112'),(198,1,'CAJA R. S. JOSE DE NULES S.C.C.V.','3135'),(199,1,'CAJA R. S. ROQUE DE ALMENARA S.C.C.V.','3095'),(200,1,'CAJA R.R.S.AGUSTIN DE FUENTE ALAMO M., S.C.C.','3018'),(201,1,'CAJAMAR CAJA RURAL, S.C.C.','3058'),(202,1,'CREDIT VALENCIA, C.R.C.C.V.','3188'),(203,1,'IPAR KUTXA RURAL, S.C.C.','3084');
ALTER TABLE `bank` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `bank_concept`
#

DROP TABLE IF EXISTS `bank_concept`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `bank_concept` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Concepto',
  PRIMARY KEY  (`id`),
  KEY `IDX_BANK_CONCEPT_DOMAIN` (`domain`),
  CONSTRAINT `FK_BANK_CONCEPT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos bancarios';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `bank_concept`
#

LOCK TABLES `bank_concept` WRITE;
ALTER TABLE `bank_concept` DISABLE KEYS;
ALTER TABLE `bank_concept` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `bank_concept_account`
#

DROP TABLE IF EXISTS `bank_concept_account`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `bank_concept_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `bank_concept` int(4) NOT NULL default '0' COMMENT 'Identificador del Concepto bancario',
  `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `IDX_BANK_CONCEPT_ACCOUNT_BANK_CONCEPT` (`bank_concept`),
  KEY `IDX_BANK_CONCEPT_ACCOUNT_ACCOUNT` (`account`),
  KEY `IDX_BANK_CONCEPT_ACCOUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_BANK_CONCEPT_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_BANK_CONCEPT_ACCOUNT_BANK_CONCEPT` FOREIGN KEY (`bank_concept`) REFERENCES `bank_concept` (`id`),
  CONSTRAINT `FK_BANK_CONCEPT_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Conceptos bancarios';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `bank_concept_account`
#

LOCK TABLES `bank_concept_account` WRITE;
ALTER TABLE `bank_concept_account` DISABLE KEYS;
ALTER TABLE `bank_concept_account` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `bank_statement`
#

DROP TABLE IF EXISTS `bank_statement`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `bank_statement` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `rbank` int(4) NOT NULL COMMENT 'Identificador de Banco de la Compaia',
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
  KEY `IDX_BANK_STATEMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_BANK_STATEMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_BANK_STATEMENT_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Extractos bancarios';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `bank_statement`
#

LOCK TABLES `bank_statement` WRITE;
ALTER TABLE `bank_statement` DISABLE KEYS;
ALTER TABLE `bank_statement` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `bank_statement_link`
#

DROP TABLE IF EXISTS `bank_statement_link`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `bank_statement_link` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_BANK_STATEMENT_LINK_DOMAIN` (`domain`),
  CONSTRAINT `FK_BANK_STATEMENT_LINK_BANK_STATEMENT` FOREIGN KEY (`bank_statement`) REFERENCES `bank_statement` (`id`),
  CONSTRAINT `FK_BANK_STATEMENT_LINK_BANK_STATEMENT_LINK` FOREIGN KEY (`linked_bank_statement_link`) REFERENCES `bank_statement_link` (`id`),
  CONSTRAINT `FK_BANK_STATEMENT_LINK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Enlaces del Extracto bancario';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `bank_statement_link`
#

LOCK TABLES `bank_statement_link` WRITE;
ALTER TABLE `bank_statement_link` DISABLE KEYS;
ALTER TABLE `bank_statement_link` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `bonus_concept`
#

DROP TABLE IF EXISTS `bonus_concept`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `bonus_concept` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `expression` varchar(512) collate latin1_spanish_ci default NULL COMMENT 'Importe',
  `description` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_BONUS_CONCEPT_DOMAIN` (`domain`),
  CONSTRAINT `FK_BONUS_CONCEPT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos de bonficaciones y/o reducciones';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `bonus_concept`
#

LOCK TABLES `bonus_concept` WRITE;
ALTER TABLE `bonus_concept` DISABLE KEYS;
ALTER TABLE `bonus_concept` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `brand`
#

DROP TABLE IF EXISTS `brand`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `brand` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Marca Comercial',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Marca Comercial',
  PRIMARY KEY  (`id`),
  KEY `IDX_BRAND_DOMAIN` (`domain`),
  CONSTRAINT `FK_BRAND_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Marcas Comerciales';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `brand`
#

LOCK TABLES `brand` WRITE;
ALTER TABLE `brand` DISABLE KEYS;
ALTER TABLE `brand` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `calendar`
#

DROP TABLE IF EXISTS `calendar`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `calendar` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_CALENDAR_DOMAIN` (`domain`),
  CONSTRAINT `FK_CALENDAR_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_CALENDAR_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CALENDAR_HOLIDAY` FOREIGN KEY (`holiday`) REFERENCES `holiday` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Calendarios Laborales';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `calendar`
#

LOCK TABLES `calendar` WRITE;
ALTER TABLE `calendar` DISABLE KEYS;
ALTER TABLE `calendar` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `calendar_holiday`
#

DROP TABLE IF EXISTS `calendar_holiday`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `calendar_holiday` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `calendar` int(4) NOT NULL default '0' COMMENT 'Identificador del Calendario',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Festivo',
  `date` date default NULL COMMENT 'Fecha del festivo',
  `day_type` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `hours` double default '0' COMMENT 'Numero de horas laborables',
  PRIMARY KEY  (`id`),
  KEY `IDX_CALENDAR_HOLIDAY_CALENDAR` (`calendar`),
  KEY `IDX_CALENDAR_HOLIDAY_DOMAIN` (`domain`),
  CONSTRAINT `FK_CALENDAR_HOLIDAY_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_CALENDAR_HOLIDAY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Festivos de Calendarios';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `calendar_holiday`
#

LOCK TABLES `calendar_holiday` WRITE;
ALTER TABLE `calendar_holiday` DISABLE KEYS;
ALTER TABLE `calendar_holiday` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `calendar_period`
#

DROP TABLE IF EXISTS `calendar_period`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `calendar_period` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_CALENDAR_PERIOD_DOMAIN` (`domain`),
  CONSTRAINT `FK_CALENDAR_PERIOD_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_CALENDAR_PERIOD_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Periodos de Calendarios';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `calendar_period`
#

LOCK TABLES `calendar_period` WRITE;
ALTER TABLE `calendar_period` DISABLE KEYS;
ALTER TABLE `calendar_period` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `campaign`
#

DROP TABLE IF EXISTS `campaign`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `campaign` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Campaa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `campaign_type` int(4) default NULL COMMENT 'Identificador del Tipo de Campaa',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Campaa',
  `process` int(4) NOT NULL COMMENT 'Identificador del Proceso',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio de la Campaa',
  `end_date` date NOT NULL COMMENT 'Fecha de finalizacion de la Campaa',
  `workgroup` int(4) NOT NULL COMMENT 'Grupo de Trabajo supervisor de la Campaa',
  `manual` tinyint(1) NOT NULL default '0' COMMENT 'Tipo de Campaa',
  `status` tinyint(2) default NULL COMMENT 'Estado de la Campaa',
  PRIMARY KEY  (`id`),
  KEY `IDX_CAMPAIGN_CAMPAIGN_TYPE` (`campaign_type`),
  KEY `IDX_CAMPAIGN_PROCESS` (`process`),
  KEY `IDX_CAMPAIGN_WORKGROUP` (`workgroup`),
  KEY `IDX_CAMPAIGN_DOMAIN` (`domain`),
  CONSTRAINT `FK_CAMPAIGN_CAMPAIGN_TYPE` FOREIGN KEY (`campaign_type`) REFERENCES `campaign_type` (`id`),
  CONSTRAINT `FK_CAMPAIGN_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CAMPAIGN_PROCESS` FOREIGN KEY (`process`) REFERENCES `process` (`id`),
  CONSTRAINT `FK_CAMPAIGN_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Campaas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `campaign`
#

LOCK TABLES `campaign` WRITE;
ALTER TABLE `campaign` DISABLE KEYS;
ALTER TABLE `campaign` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `campaign_project`
#

DROP TABLE IF EXISTS `campaign_project`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `campaign_project` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Relacion de Campaas y Expedientes',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `campaign` int(4) NOT NULL COMMENT 'Identificador de la Campaa',
  `project` int(4) NOT NULL COMMENT 'Identificador del Expediente',
  PRIMARY KEY  (`id`),
  KEY `IDX_CAMPAIGN_PROJECT_CAMPAIGN` (`campaign`),
  KEY `IDX_CAMPAIGN_PROJECT_PROJECT` (`project`),
  KEY `IDX_CAMPAIGN_PROJECT_DOMAIN` (`domain`),
  CONSTRAINT `FK_CAMPAIGN_PROJECT_CAMPAIGN` FOREIGN KEY (`campaign`) REFERENCES `campaign` (`id`),
  CONSTRAINT `FK_CAMPAIGN_PROJECT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CAMPAIGN_PROJECT_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Campaas y Expedientes';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `campaign_project`
#

LOCK TABLES `campaign_project` WRITE;
ALTER TABLE `campaign_project` DISABLE KEYS;
ALTER TABLE `campaign_project` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `campaign_type`
#

DROP TABLE IF EXISTS `campaign_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `campaign_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion',
  `active` tinyint(1) NOT NULL default '0' COMMENT 'Activo si o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_CAMPAIGN_TYPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_CAMPAIGN_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Campaas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `campaign_type`
#

LOCK TABLES `campaign_type` WRITE;
ALTER TABLE `campaign_type` DISABLE KEYS;
ALTER TABLE `campaign_type` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `cashflow_forecast`
#

DROP TABLE IF EXISTS `cashflow_forecast`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cashflow_forecast` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `payment` tinyint(1) NOT NULL default '0' COMMENT 'Indica si es un pago o un cobro',
  `description` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio de aplicacion',
  `due_date` date default NULL COMMENT 'Fecha final de aplicacion',
  `rbank` int(4) default NULL COMMENT 'Identificador de Banco de la Compaia',
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
  KEY `IDX_CASHFLOW_FORECAST_DOMAIN` (`domain`),
  CONSTRAINT `FK_CASHFLOW_FORECAST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CASHFLOW_FORECAST_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Prevision de tesoreria';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `cashflow_forecast`
#

LOCK TABLES `cashflow_forecast` WRITE;
ALTER TABLE `cashflow_forecast` DISABLE KEYS;
ALTER TABLE `cashflow_forecast` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `catalogue`
#

DROP TABLE IF EXISTS `catalogue`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `catalogue` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Catalogo',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio del Catalogo',
  `end_date` date default NULL COMMENT 'Fecha de fin del Catalogo',
  PRIMARY KEY  (`id`),
  KEY `IDX_CATALOGUE_DOMAIN` (`domain`),
  CONSTRAINT `FK_CATALOGUE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Catalogos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `catalogue`
#

LOCK TABLES `catalogue` WRITE;
ALTER TABLE `catalogue` DISABLE KEYS;
ALTER TABLE `catalogue` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `catalogue_category`
#

DROP TABLE IF EXISTS `catalogue_category`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `catalogue_category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `catalogue` int(4) NOT NULL COMMENT 'Identificador del Catalogo',
  `category` int(4) NOT NULL COMMENT 'Identificador de la Categoria',
  `quantity` double default '0' COMMENT 'Cantidad a partir de la cual se aplica el descuento',
  `discount` double(6,2) default '0.00' COMMENT 'Descuento de la Categoria en el Catalogo',
  PRIMARY KEY  (`id`),
  KEY `IDX_CATALOGUE_CATEGORY_CATALOGUE` (`catalogue`),
  KEY `IDX_CATALOGUE_CATEGORY_PCATEGORY` (`category`),
  KEY `IDX_CATALOGUE_CATEGORY_DOMAIN` (`domain`),
  CONSTRAINT `FK_CATALOGUE_CATEGORY_CATALOGUE` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`),
  CONSTRAINT `FK_CATALOGUE_CATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CATALOGUE_CATEGORY_PCATEGORY` FOREIGN KEY (`category`) REFERENCES `pcategory` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias del Catalogo';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `catalogue_category`
#

LOCK TABLES `catalogue_category` WRITE;
ALTER TABLE `catalogue_category` DISABLE KEYS;
ALTER TABLE `catalogue_category` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `catalogue_item`
#

DROP TABLE IF EXISTS `catalogue_item`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `catalogue_item` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `catalogue` int(4) NOT NULL COMMENT 'Identificador del Catalogo',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo',
  `quantity` double default '0' COMMENT 'Cantidad a partir de la cual se aplica el precio o descuento',
  `price` double default '0' COMMENT 'Precio del Articulo en el Catalogo',
  `discount` double(6,2) default '0.00' COMMENT 'Descuento del Articulo en el Catalogo',
  PRIMARY KEY  (`id`),
  KEY `IDX_CATALOGUE_ITEM_CATALOGUE` (`catalogue`),
  KEY `IDX_CATALOGUE_ITEM_ITEM` (`item`),
  KEY `IDX_CATALOGUE_ITEM_DOMAIN` (`domain`),
  CONSTRAINT `FK_CATALOGUE_ITEM_CATALOGUE` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`),
  CONSTRAINT `FK_CATALOGUE_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CATALOGUE_ITEM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulos del Catalogo';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `catalogue_item`
#

LOCK TABLES `catalogue_item` WRITE;
ALTER TABLE `catalogue_item` DISABLE KEYS;
ALTER TABLE `catalogue_item` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `category`
#

DROP TABLE IF EXISTS `category`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Categoria',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Categoria',
  PRIMARY KEY  (`id`),
  KEY `IDX_CATEGORY_DOMAIN` (`domain`),
  CONSTRAINT `FK_CATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `category`
#

LOCK TABLES `category` WRITE;
ALTER TABLE `category` DISABLE KEYS;
ALTER TABLE `category` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `certifica2_batch`
#

DROP TABLE IF EXISTS `certifica2_batch`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `certifica2_batch` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del certificado de empresa de la remesa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador unico del certificado de empresa de la empresa',
  `date` date NOT NULL COMMENT 'Fecha de la ultima remesa en la que fue incluido',
  `status` int(4) default NULL COMMENT 'Estado del certificado correspondiente a la ultima respuesta',
  `sign` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Estado del certificado correspondiente a la ultima respuesta',
  PRIMARY KEY  (`id`),
  KEY `IDX_CERTIFICA2_BATCH_ENTERPRISE` (`enterprise`),
  KEY `IDX_CERTIFICA2_BATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas de certificados de empresa';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `certifica2_batch`
#

LOCK TABLES `certifica2_batch` WRITE;
ALTER TABLE `certifica2_batch` DISABLE KEYS;
ALTER TABLE `certifica2_batch` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `certifica2_batch_attach`
#

DROP TABLE IF EXISTS `certifica2_batch_attach`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `certifica2_batch_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Archivo Adjunto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_CERTIFICA2_BATCH_ATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_ATTACH_CERTIFICA2_BATCH` FOREIGN KEY (`certifica2_batch`) REFERENCES `certifica2_batch` (`id`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de remesas de certificados de empresa';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `certifica2_batch_attach`
#

LOCK TABLES `certifica2_batch_attach` WRITE;
ALTER TABLE `certifica2_batch_attach` DISABLE KEYS;
ALTER TABLE `certifica2_batch_attach` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `certifica2_batch_data`
#

DROP TABLE IF EXISTS `certifica2_batch_data`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `certifica2_batch_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de los datos de cotizacion del certificado',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `certifica2_batch_detail` int(4) NOT NULL COMMENT 'Identificador unico del certificado de empresa de la remesa',
  `year` int(4) NOT NULL COMMENT 'Anio',
  `month` int(2) NOT NULL COMMENT 'Mes',
  `contribution_days` int(2) NOT NULL COMMENT 'Numero de dias cotizados',
  `cgc_contribution_base` double(15,3) default '0.000' COMMENT 'Base de cotizacion de contingencias comunes',
  `unemployment_contribution_base` double(15,3) NOT NULL default '0.000' COMMENT 'Base de cotizacion por desempleo',
  `comments` varchar(50) collate latin1_spanish_ci default NULL COMMENT 'Observaciones',
  PRIMARY KEY  (`id`),
  KEY `IDX_CERTIFICA2_BATCH_DATA_CERTIFICA2_BATCH_DETAIL` (`certifica2_batch_detail`),
  KEY `IDX_CERTIFICA2_BATCH_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_DATA_CERTIFICA2_BATCH_DETAIL` FOREIGN KEY (`certifica2_batch_detail`) REFERENCES `certifica2_batch_detail` (`id`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos cotizacion de empleados de certificados de empresa';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `certifica2_batch_data`
#

LOCK TABLES `certifica2_batch_data` WRITE;
ALTER TABLE `certifica2_batch_data` DISABLE KEYS;
ALTER TABLE `certifica2_batch_data` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `certifica2_batch_detail`
#

DROP TABLE IF EXISTS `certifica2_batch_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `certifica2_batch_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del certificado de empresa de la remesa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `certifica2_batch` int(4) NOT NULL COMMENT 'Identificador unico del certificado de empresa',
  `contract` int(4) NOT NULL COMMENT 'Identificador unico del contrato de empleado',
  `enterprise_nif` varchar(9) collate latin1_spanish_ci NOT NULL COMMENT 'NIF de la empresa',
  `ccc` varchar(15) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo cuenta cotizacion',
  `document` varchar(9) collate latin1_spanish_ci NOT NULL COMMENT 'Documento de identidad',
  `name` varchar(15) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del trabajador',
  `first_surname` varchar(20) collate latin1_spanish_ci NOT NULL COMMENT 'Primer apellido',
  `second_surname` varchar(20) collate latin1_spanish_ci default NULL COMMENT 'Segundo apellido',
  `ss_number` varchar(20) collate latin1_spanish_ci NOT NULL COMMENT 'Numero seguridad social',
  `quote_group` varchar(2) collate latin1_spanish_ci default NULL COMMENT 'Grupo de Cotización',
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
  KEY `IDX_CERTIFICA2_BATCH_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_DETAIL_CERTIFICA2_BATCH` FOREIGN KEY (`certifica2_batch`) REFERENCES `certifica2_batch` (`id`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_DETAIL_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las remesas de certificados de empresa';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `certifica2_batch_detail`
#

LOCK TABLES `certifica2_batch_detail` WRITE;
ALTER TABLE `certifica2_batch_detail` DISABLE KEYS;
ALTER TABLE `certifica2_batch_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `cnae`
#

DROP TABLE IF EXISTS `cnae`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cnae` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `code` varchar(5) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo del CNAE',
  `title` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Titulo del CNAE',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9901 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='CNAE';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `cnae`
#

LOCK TABLES `cnae` WRITE;
ALTER TABLE `cnae` DISABLE KEYS;
INSERT INTO `cnae` VALUES (111,'0111','Cultivo de cereales (excepto arroz), leguminosas y semillas oleaginosas'),(112,'0112','Cultivo de arroz'),(113,'0113','Cultivo de hortalizas, races y tubrculos'),(114,'0114','Cultivo de caa de azcar'),(115,'0115','Cultivo de tabaco'),(116,'0116','Cultivo de plantas para fibras textiles'),(119,'0119','Otros cultivos no perennes'),(121,'0121','Cultivo de la vid'),(122,'0122','Cultivo de frutos tropicales y subtropicales'),(123,'0123','Cultivo de ctricos'),(124,'0124','Cultivo de frutos con hueso y pepitas'),(125,'0125','Cultivo de otros rboles y arbustos frutales y frutos secos'),(126,'0126','Cultivo de frutos oleaginosos'),(127,'0127','Cultivo de plantas para bebidas'),(128,'0128','Cultivo de especias, plantas aromticas, medicinales y farmacuticas'),(129,'0129','Otros cultivos perennes'),(130,'0130','Propagacin de plantas'),(141,'0141','Explotacin de ganado bovino para la produccin de leche'),(142,'0142','Explotacin de otro ganado bovino y bfalos'),(143,'0143','Explotacin de caballos y otros equinos'),(144,'0144','Explotacin de camellos y otros camlidos'),(145,'0145','Explotacin de ganado ovino y caprino'),(146,'0146','Explotacin de ganado porcino'),(147,'0147','Avicultura'),(149,'0149','Otras explotaciones de ganado'),(150,'0150','Produccin agrcola combinada con la produccin ganadera'),(161,'0161','Actividades de apoyo a la agricultura'),(162,'0162','Actividades de apoyo a la ganadera'),(163,'0163','Actividades de preparacin posterior a la cosecha'),(164,'0164','Tratamiento de semillas para reproduccin'),(170,'0170','Caza, captura de animales y servicios relacionados con las mismas'),(210,'0210','Silvicultura y otras actividades forestales'),(220,'0220','Explotacin de la madera'),(230,'0230','Recoleccin de productos silvestres, excepto madera'),(240,'0240','Servicios de apoyo a la silvicultura'),(311,'0311','Pesca marina'),(312,'0312','Pesca en agua dulce'),(321,'0321','Acuicultura marina'),(322,'0322','Acuicultura en agua dulce'),(510,'0510','Extraccin de antracita y hulla'),(520,'0520','Extraccin de lignito'),(610,'0610','Extraccin de crudo de petrleo'),(620,'0620','Extraccin de gas natural'),(710,'0710','Extraccin de minerales de hierro'),(721,'0721','Extraccin de minerales de uranio y torio'),(729,'0729','Extraccin de otros minerales metlicos no frreos'),(811,'0811','Extraccin de piedra ornamental y para la construccin, piedra caliza, yeso, creta y pizarra'),(812,'0812','Extraccin de gravas y arenas'),(891,'0891','Extraccin de minerales para productos qumicos y fertilizantes'),(892,'0892','Extraccin de turba'),(893,'0893','Extraccin de sal'),(899,'0899','Otras industrias extractivas n.c.o.p.'),(910,'0910','Actividades de apoyo a la extraccin de petrleo y gas natural'),(990,'0990','Actividades de apoyo a otras industrias extractivas'),(1011,'1011','Procesado y conservacin de carne'),(1012,'1012','Procesado y conservacin de volatera'),(1013,'1013','Elaboracin de productos crnicos y de volatera'),(1021,'1021','Procesado de pescados, crustceos y moluscos'),(1022,'1022','Fabricacin de conservas de pescado'),(1031,'1031','Procesado y conservacin de patatas'),(1032,'1032','Elaboracin de zumos de frutas y hortalizas'),(1039,'1039','Otro procesado y conservacin de frutas y hortalizas'),(1042,'1042','Fabricacin de margarina y grasas comestibles similares'),(1043,'1043','Fabricacin de aceite de oliva'),(1044,'1044','Fabricacin de otros aceites y grasas'),(1052,'1052','Elaboracin de helados'),(1053,'1053','Fabricacin de quesos'),(1054,'1054','Preparacin de leche y otros productos lcteos'),(1061,'1061','Fabricacin de productos de molinera'),(1062,'1062','Fabricacin de almidones y productos amilceos'),(1071,'1071','Fabricacin de pan y de productos frescos de panadera y pastelera'),(1072,'1072','Fabricacin de galletas y productos de panadera y pastelera de larga duracin'),(1073,'1073','Fabricacin de pastas alimenticias, cuscs y productos similares'),(1081,'1081','Fabricacin de azcar'),(1082,'1082','Fabricacin de cacao, chocolate y productos de confitera'),(1083,'1083','Elaboracin de caf, t e infusiones'),(1084,'1084','Elaboracin de especias, salsas y condimentos'),(1085,'1085','Elaboracin de platos y comidas preparados'),(1086,'1086','Elaboracin de preparados alimenticios homogeneizados y alimentos dietticos'),(1089,'1089','Elaboracin de otros productos alimenticios n.c.o.p.'),(1091,'1091','Fabricacin de productos para la alimentacin de animales de granja'),(1092,'1092','Fabricacin de productos para la alimentacin de animales de compaa'),(1101,'1101','Destilacin, rectificacin y mezcla de bebidas alcohlicas'),(1102,'1102','Elaboracin de vinos'),(1103,'1103','Elaboracin de sidra y otras bebidas fermentadas a partir de frutas'),(1104,'1104','Elaboracin de otras bebidas no destiladas, procedentes de la fermentacin'),(1105,'1105','Fabricacin de cerveza'),(1106,'1106','Fabricacin de malta'),(1107,'1107','Fabricacin de bebidas no alcohlicas'),(1200,'1200','Industria del tabaco'),(1310,'1310','Preparacin e hilado de fibras textiles'),(1320,'1320','Fabricacin de tejidos textiles'),(1330,'1330','Acabado de textiles'),(1391,'1391','Fabricacin de tejidos de punto'),(1392,'1392','Fabricacin de artculos confeccionados con textiles, excepto prendas de vestir'),(1393,'1393','Fabricacin de alfombras y moquetas'),(1394,'1394','Fabricacin de cuerdas, cordeles, bramantes y redes'),(1395,'1395','Fabricacin de telas no tejidas y artculos confeccionados con ellas, excepto prendas de vestir'),(1396,'1396','Fabricacin de otros productos textiles de uso tcnico e industrial'),(1399,'1399','Fabricacin de otros productos textiles n.c.o.p.'),(1411,'1411','Confeccin de prendas de vestir de cuero'),(1412,'1412','Confeccin de ropa de trabajo'),(1413,'1413','Confeccin de otras prendas de vestir exteriores'),(1414,'1414','Confeccin de ropa interior'),(1419,'1419','Confeccin de otras prendas de vestir y accesorios'),(1420,'1420','Fabricacin de artculos de peletera'),(1431,'1431','Confeccin de calcetera'),(1439,'1439','Confeccin de otras prendas de vestir de punto'),(1511,'1511','Preparacin, curtido y acabado del cuero'),(1512,'1512','Fabricacin de artculos de marroquinera, viaje y de guarnicionera y talabartera'),(1520,'1520','Fabricacin de calzado'),(1610,'1610','Aserrado y cepillado de la madera'),(1621,'1621','Fabricacin de chapas y tableros de madera'),(1622,'1622','Fabricacin de suelos de madera ensamblados'),(1623,'1623','Fabricacin de otras estructuras de madera y piezas de carpintera y ebanistera para la construccin'),(1624,'1624','Fabricacin de envases y embalajes de madera'),(1629,'1629','Fabricacin de otros productos de madera'),(1711,'1711','Fabricacin de pasta papelera'),(1712,'1712','Fabricacin de papel y cartn'),(1721,'1721','Fabricacin de papel y cartn ondulados'),(1722,'1722','Fabricacin de artculos de papel y cartn para uso domstico, sanitario e higinico'),(1723,'1723','Fabricacin de artculos de papelera'),(1724,'1724','Fabricacin de papeles pintados'),(1729,'1729','Fabricacin de otros artculos de papel y cartn'),(1811,'1811','Artes grficas y servicios relacionados con las mismas'),(1812,'1812','Otras actividades de impresin y artes grficas'),(1813,'1813','Servicios de preimpresin y preparacin de soportes'),(1814,'1814','Encuadernacin y servicios relacionados con la misma'),(1820,'1820','Reproduccin de soportes grabados'),(1910,'1910','Coqueras'),(1920,'1920','Refino de petrleo'),(2011,'2011','Fabricacin de gases industriales'),(2012,'2012','Fabricacin de colorantes y pigmentos'),(2013,'2013','Fabricacin de otros productos bsicos de qumica inorgnica'),(2014,'2014','Fabricacin de otros productos bsicos de qumica orgnica'),(2015,'2015','Fabricacin de fertilizantes y compuestos nitrogenados'),(2016,'2016','Fabricacin de plsticos en formas primarias'),(2017,'2017','Fabricacin de caucho sinttico en formas primarias'),(2020,'2020','Fabricacin de pesticidas y otros productos agroqumicos'),(2030,'2030','Fabricacin de pinturas, barnices y revestimientos similares'),(2041,'2041','Fabricacin de jabones, detergentes y otros artculos de limpieza y abrillantamiento'),(2042,'2042','Fabricacin de perfumes y cosmticos'),(2051,'2051','Fabricacin de explosivos'),(2052,'2052','Fabricacin de colas'),(2053,'2053','Fabricacin de aceites esenciales'),(2059,'2059','Fabricacin de otros productos qumicos n.c.o.p.'),(2060,'2060','Fabricacin de fibras artificiales y sintticas'),(2110,'2110','Fabricacin de productos farmacuticos de base'),(2120,'2120','Fabricacin de especialidades farmacuticas'),(2211,'2211','Fabricacin de neumticos y cmaras de caucho'),(2219,'2219','Fabricacin de otros productos de caucho'),(2221,'2221','Fabricacin de placas, hojas, tubos y perfiles de plstico'),(2222,'2222','Fabricacin de envases y embalajes de plstico'),(2223,'2223','Fabricacin de productos de plstico para la construccin'),(2229,'2229','Fabricacin de otros productos de plstico'),(2311,'2311','Fabricacin de vidrio plano'),(2312,'2312','Manipulado y transformacin de vidrio plano'),(2313,'2313','Fabricacin de vidrio hueco'),(2314,'2314','Fabricacin de fibra de vidrio'),(2319,'2319','Fabricacin y manipulado de otro vidrio, incluido el vidrio tcnico'),(2320,'2320','Fabricacin de productos cermicos refractarios'),(2331,'2331','Fabricacin de azulejos y baldosas de cermica'),(2332,'2332','Fabricacin de ladrillos, tejas y productos de tierras cocidas para la construccin'),(2341,'2341','Fabricacin de artculos cermicos de uso domstico y ornamental'),(2342,'2342','Fabricacin de aparatos sanitarios cermicos'),(2343,'2343','Fabricacin de aisladores y piezas aislantes de material cermico'),(2344,'2344','Fabricacin de otros productos cermicos de uso tcnico'),(2349,'2349','Fabricacin de otros productos cermicos'),(2351,'2351','Fabricacin de cemento'),(2352,'2352','Fabricacin de cal y yeso'),(2361,'2361','Fabricacin de elementos de hormign para la construccin'),(2362,'2362','Fabricacin de elementos de yeso para la construccin'),(2363,'2363','Fabricacin de hormign fresco'),(2364,'2364','Fabricacin de mortero'),(2365,'2365','Fabricacin de fibrocemento'),(2369,'2369','Fabricacin de otros productos de hormign, yeso y cemento'),(2370,'2370','Corte, tallado y acabado de la piedra'),(2391,'2391','Fabricacin de productos abrasivos'),(2399,'2399','Fabricacin de otros productos minerales no metlicos n.c.o.p.'),(2410,'2410','Fabricacin de productos bsicos de hierro, acero y ferroaleaciones'),(2420,'2420','Fabricacin de tubos, tuberas, perfiles huecos y sus accesorios, de acero'),(2431,'2431','Estirado en fro'),(2432,'2432','Laminacin en fro'),(2433,'2433','Produccin de perfiles en fro por conformacin con plegado'),(2434,'2434','Trefilado en fro'),(2441,'2441','Produccin de metales preciosos'),(2442,'2442','Produccin de aluminio'),(2443,'2443','Produccin de plomo, zinc y estao'),(2444,'2444','Produccin de cobre'),(2445,'2445','Produccin de otros metales no frreos'),(2446,'2446','Procesamiento de combustibles nucleares'),(2451,'2451','Fundicin de hierro'),(2452,'2452','Fundicin de acero'),(2453,'2453','Fundicin de metales ligeros'),(2454,'2454','Fundicin de otros metales no frreos'),(2511,'2511','Fabricacin de estructuras metlicas y sus componentes'),(2512,'2512','Fabricacin de carpintera metlica'),(2521,'2521','Fabricacin de radiadores y calderas para calefaccin central'),(2529,'2529','Fabricacin de otras cisternas, grandes depsitos y contenedores de metal'),(2530,'2530','Fabricacin de generadores de vapor, excepto calderas de calefaccin central'),(2540,'2540','Fabricacin de armas y municiones'),(2550,'2550','Forja, estampacin y embuticin de metales'),(2561,'2561','Tratamiento y revestimiento de metales'),(2562,'2562','Ingeniera mecnica por cuenta de terceros'),(2571,'2571','Fabricacin de artculos de cuchillera y cubertera'),(2572,'2572','Fabricacin de cerraduras y herrajes'),(2573,'2573','Fabricacin de herramientas'),(2591,'2591','Fabricacin de bidones y toneles de hierro o acero'),(2592,'2592','Fabricacin de envases y embalajes metlicos ligeros'),(2593,'2593','Fabricacin de productos de alambre, cadenas y muelles'),(2594,'2594','Fabricacin de pernos y productos de tornillera'),(2599,'2599','Fabricacin de otros productos metlicos n.c.o.p.'),(2611,'2611','Fabricacin de componentes electrnicos'),(2612,'2612','Fabricacin de circuitos impresos ensamblados'),(2620,'2620','Fabricacin de ordenadores y equipos perifricos'),(2630,'2630','Fabricacin de equipos de telecomunicaciones'),(2640,'2640','Fabricacin de productos electrnicos de consumo'),(2651,'2651','Fabricacin de instrumentos y aparatos de medida, verificacin y navegacin'),(2652,'2652','Fabricacin de relojes'),(2660,'2660','Fabricacin de equipos de radiacin, electromdicos y electroteraputicos'),(2670,'2670','Fabricacin de instrumentos de ptica y equipo fotogrfico'),(2680,'2680','Fabricacin de soportes magnticos y pticos'),(2711,'2711','Fabricacin de motores, generadores y transformadores elctricos'),(2712,'2712','Fabricacin de aparatos de distribucin y control elctrico'),(2720,'2720','Fabricacin de pilas y acumuladores elctricos'),(2731,'2731','Fabricacin de cables de fibra ptica'),(2732,'2732','Fabricacin de otros hilos y cables electrnicos y elctricos'),(2733,'2733','Fabricacin de dispositivos de cableado'),(2740,'2740','Fabricacin de lmparas y aparatos elctricos de iluminacin'),(2751,'2751','Fabricacin de electrodomsticos'),(2752,'2752','Fabricacin de aparatos domsticos no elctricos'),(2790,'2790','Fabricacin de otro material y equipo elctrico'),(2811,'2811','Fabricacin de motores y turbinas, excepto los destinados a aeronaves, vehculos automviles y ciclomotores'),(2812,'2812','Fabricacin de equipos de transmisin hidrulica y neumtica'),(2813,'2813','Fabricacin de otras bombas y compresores'),(2814,'2814','Fabricacin de otra grifera y vlvulas'),(2815,'2815','Fabricacin de cojinetes, engranajes y rganos mecnicos de transmisin'),(2821,'2821','Fabricacin de hornos y quemadores'),(2822,'2822','Fabricacin de maquinaria de elevacin y manipulacin'),(2823,'2823','Fabricacin de mquinas y equipos de oficina, excepto equipos informticos'),(2824,'2824','Fabricacin de herramientas elctricas manuales'),(2825,'2825','Fabricacin de maquinaria de ventilacin y refrigeracin no domstica'),(2829,'2829','Fabricacin de otra maquinaria de uso general n.c.o.p.'),(2830,'2830','Fabricacin de maquinaria agraria y forestal'),(2841,'2841','Fabricacin de mquinas herramienta para trabajar el metal'),(2849,'2849','Fabricacin de otras mquinas herramienta'),(2891,'2891','Fabricacin de maquinaria para la industria metalrgica'),(2892,'2892','Fabricacin de maquinaria para las industrias extractivas y de la construccin'),(2893,'2893','Fabricacin de maquinaria para la industria de la alimentacin, bebidas y tabaco'),(2894,'2894','Fabricacin de maquinaria para las industrias textil, de la confeccin y del cuero'),(2895,'2895','Fabricacin de maquinaria para la industria del papel y del cartn'),(2896,'2896','Fabricacin de maquinaria para la industria del plstico y el caucho'),(2899,'2899','Fabricacin de otra maquinaria para usos especficos n.c.o.p.'),(2910,'2910','Fabricacin de vehculos de motor'),(2920,'2920','Fabricacin de carroceras para vehculos de motor'),(2931,'2931','Fabricacin de equipos elctricos y electrnicos para vehculos de motor'),(2932,'2932','Fabricacin de otros componentes, piezas y accesorios para vehculos de motor'),(3011,'3011','Construccin de barcos y estructuras flotantes'),(3012,'3012','Construccin de embarcaciones de recreo y deporte'),(3020,'3020','Fabricacin de locomotoras y material ferroviario'),(3030,'3030','Construccin aeronutica y espacial y su maquinaria'),(3040,'3040','Fabricacin de vehculos militares de combate'),(3091,'3091','Fabricacin de motocicletas'),(3092,'3092','Fabricacin de bicicletas y de vehculos para personas con discapacidad'),(3099,'3099','Fabricacin de otro material de transporte n.c.o.p.'),(3101,'3101','Fabricacin de muebles de oficina y de establecimientos comerciales'),(3102,'3102','Fabricacin de muebles de cocina'),(3103,'3103','Fabricacin de colchones'),(3109,'3109','Fabricacin de otros muebles'),(3211,'3211','Fabricacin de monedas'),(3212,'3212','Fabricacin de artculos de joyera y artculos similares'),(3213,'3213','Fabricacin de artculos de bisutera y artculos similares'),(3220,'3220','Fabricacin de instrumentos musicales'),(3230,'3230','Fabricacin de artculos de deporte'),(3240,'3240','Fabricacin de juegos y juguetes'),(3250,'3250','Fabricacin de instrumentos y suministros mdicos y odontolgicos'),(3291,'3291','Fabricacin de escobas, brochas y cepillos'),(3299,'3299','Otras industrias manufactureras n.c.o.p.'),(3311,'3311','Reparacin de productos metlicos'),(3312,'3312','Reparacin de maquinaria'),(3313,'3313','Reparacin de equipos electrnicos y pticos'),(3314,'3314','Reparacin de equipos elctricos'),(3315,'3315','Reparacin y mantenimiento naval'),(3316,'3316','Reparacin y mantenimiento aeronutico y espacial'),(3317,'3317','Reparacin y mantenimiento de otro material de transporte'),(3319,'3319','Reparacin de otros equipos'),(3320,'3320','Instalacin de mquinas y equipos industriales'),(3512,'3512','Transporte de energa elctrica'),(3513,'3513','Distribucin de energa elctrica'),(3514,'3514','Comercio de energa elctrica'),(3515,'3515','Produccin de energa hidroelctrica'),(3516,'3516','Produccin de energa elctrica de origen trmico convencional'),(3517,'3517','Produccin de energa elctrica de origen nuclear'),(3518,'3518','Produccin de energa elctrica de origen elico'),(3519,'3519','Produccin de energa elctrica de otros tipos'),(3521,'3521','Produccin de gas'),(3522,'3522','Distribucin por tubera de combustibles gaseosos'),(3523,'3523','Comercio de gas por tubera'),(3530,'3530','Suministro de vapor y aire acondicionado'),(3600,'3600','Captacin, depuracin y distribucin de agua'),(3700,'3700','Recogida y tratamiento de aguas residuales'),(3811,'3811','Recogida de residuos no peligrosos'),(3812,'3812','Recogida de residuos peligrosos'),(3821,'3821','Tratamiento y eliminacin de residuos no peligrosos'),(3822,'3822','Tratamiento y eliminacin de residuos peligrosos'),(3831,'3831','Separacin y clasificacin de materiales'),(3832,'3832','Valorizacin de materiales ya clasificados'),(3900,'3900','Actividades de descontaminacin y otros servicios de gestin de residuos'),(4110,'4110','Promocin inmobiliaria'),(4121,'4121','Construccin de edificios residenciales'),(4122,'4122','Construccin de edificios no residenciales'),(4211,'4211','Construccin de carreteras y autopistas'),(4212,'4212','Construccin de vas frreas de superficie y subterrneas'),(4213,'4213','Construccin de puentes y tneles'),(4221,'4221','Construccin de redes para fluidos'),(4222,'4222','Construccin de redes elctricas y de telecomunicaciones'),(4291,'4291','Obras hidrulicas'),(4299,'4299','Construccin de otros proyectos de ingeniera civil n.c.o.p.'),(4311,'4311','Demolicin'),(4312,'4312','Preparacin de terrenos'),(4313,'4313','Perforaciones y sondeos'),(4321,'4321','Instalaciones elctricas'),(4322,'4322','Fontanera, instalaciones de sistemas de calefaccin y aire acondicionado'),(4329,'4329','Otras instalaciones en obras de construccin'),(4331,'4331','Revocamiento'),(4332,'4332','Instalacin de carpintera'),(4333,'4333','Revestimiento de suelos y paredes'),(4334,'4334','Pintura y acristalamiento'),(4339,'4339','Otro acabado de edificios'),(4391,'4391','Construccin de cubiertas'),(4399,'4399','Otras actividades de construccin especializada n.c.o.p.'),(4511,'4511','Venta de automviles y vehculos de motor ligeros'),(4519,'4519','Venta de otros vehculos de motor'),(4520,'4520','Mantenimiento y reparacin de vehculos de motor'),(4531,'4531','Comercio al por mayor de repuestos y accesorios de vehculos de motor'),(4532,'4532','Comercio al por menor de repuestos y accesorios de vehculos de motor'),(4540,'4540','Venta, mantenimiento y reparacin de motocicletas y de sus repuestos y accesorios'),(4611,'4611','Intermediarios del comercio de materias primas agrarias, animales vivos, materias primas textiles y productos semielaborados'),(4612,'4612','Intermediarios del comercio de combustibles, minerales, metales y productos qumicos industriales'),(4613,'4613','Intermediarios del comercio de la madera y materiales de construccin'),(4614,'4614','Intermediarios del comercio de maquinaria, equipo industrial, embarcaciones y aeronaves'),(4615,'4615','Intermediarios del comercio de muebles, artculos para el hogar y ferretera'),(4616,'4616','Intermediarios del comercio de textiles, prendas de vestir, peletera, calzado y artculos de cuero'),(4617,'4617','Intermediarios del comercio de productos alimenticios, bebidas y tabaco'),(4618,'4618','Intermediarios del comercio especializados en la venta de otros productos especficos'),(4619,'4619','Intermediarios del comercio de productos diversos'),(4621,'4621','Comercio al por mayor de cereales, tabaco en rama, simientes y alimentos para animales'),(4622,'4622','Comercio al por mayor de flores y plantas'),(4623,'4623','Comercio al por mayor de animales vivos'),(4624,'4624','Comercio al por mayor de cueros y pieles'),(4631,'4631','Comercio al por mayor de frutas y hortalizas'),(4632,'4632','Comercio al por mayor de carne y productos crnicos'),(4633,'4633','Comercio al por mayor de productos lcteos, huevos, aceites y grasas comestibles'),(4634,'4634','Comercio al por mayor de bebidas'),(4635,'4635','Comercio al por mayor de productos del tabaco'),(4636,'4636','Comercio al por mayor de azcar, chocolate y confitera'),(4637,'4637','Comercio al por mayor de caf, t, cacao y especias'),(4638,'4638','Comercio al por mayor de pescados y mariscos y otros productos alimenticios'),(4639,'4639','Comercio al por mayor, no especializado, de productos alimenticios, bebidas y tabaco'),(4641,'4641','Comercio al por mayor de textiles'),(4642,'4642','Comercio al por mayor de prendas de vestir y calzado'),(4643,'4643','Comercio al por mayor de aparatos electrodomsticos'),(4644,'4644','Comercio al por mayor de porcelana, cristalera y artculos de limpieza'),(4645,'4645','Comercio al por mayor de productos perfumera y cosmtica'),(4646,'4646','Comercio al por mayor de productos farmacuticos'),(4647,'4647','Comercio al por mayor de muebles, alfombras y aparatos de iluminacin'),(4648,'4648','Comercio al por mayor de artculos de relojera y joyera'),(4649,'4649','Comercio al por mayor de otros artculos de uso domstico'),(4651,'4651','Comercio al por mayor de ordenadores, equipos perifricos y programas informticos'),(4652,'4652','Comercio al por mayor de equipos electrnicos y de telecomunicaciones y sus componentes'),(4661,'4661','Comercio al por mayor de maquinaria, equipos y suministros agrcolas'),(4662,'4662','Comercio al por mayor de mquinas herramienta'),(4663,'4663','Comercio al por mayor de maquinaria para la minera, la construccin y la ingeniera civil'),(4664,'4664','Comercio al por mayor de maquinaria para la industria textil y de mquinas de coser y tricotar'),(4665,'4665','Comercio al por mayor de muebles de oficina'),(4666,'4666','Comercio al por mayor de otra maquinaria y equipo de oficina'),(4669,'4669','Comercio al por mayor de otra maquinaria y equipo'),(4671,'4671','Comercio al por mayor de combustibles slidos, lquidos y gaseosos, y productos similares'),(4672,'4672','Comercio al por mayor de metales y minerales metlicos'),(4673,'4673','Comercio al por mayor de madera, materiales de construccin y aparatos sanitarios'),(4674,'4674','Comercio al por mayor de ferretera, fontanera y calefaccin'),(4675,'4675','Comercio al por mayor de productos qumicos'),(4676,'4676','Comercio al por mayor de otros productos semielaborados'),(4677,'4677','Comercio al por mayor de chatarra y productos de desecho'),(4690,'4690','Comercio al por mayor no especializado'),(4711,'4711','Comercio al por menor en establecimientos no especializados, con predominio en productos alimenticios, bebidas y tabaco'),(4719,'4719','Otro comercio al por menor en establecimientos no especializados'),(4721,'4721','Comercio al por menor de frutas y hortalizas en establecimientos especializados'),(4722,'4722','Comercio al por menor de carne y productos crnicos en establecimientos especializados'),(4723,'4723','Comercio al por menor de pescados y mariscos en establecimientos especializados'),(4724,'4724','Comercio al por menor de pan y productos de panadera, confitera y pastelera en establecimientos especializados'),(4725,'4725','Comercio al por menor de bebidas en establecimientos especializados'),(4726,'4726','Comercio al por menor de productos de tabaco en establecimientos especializados'),(4729,'4729','Otro comercio al por menor de productos alimenticios en establecimientos especializados'),(4730,'4730','Comercio al por menor de combustible para la automocin en establecimientos especializados'),(4741,'4741','Comercio al por menor de ordenadores, equipos perifricos y programas informticos en establecimientos especializados'),(4742,'4742','Comercio al por menor de equipos de telecomunicaciones en establecimientos especializados'),(4743,'4743','Comercio al por menor de equipos de audio y vdeo en establecimientos especializados'),(4751,'4751','Comercio al por menor de textiles en establecimientos especializados'),(4752,'4752','Comercio al por menor de ferretera, pintura y vidrio en establecimientos especializados'),(4753,'4753','Comercio al por menor de alfombras, moquetas y revestimientos de paredes y suelos en establecimientos especializados'),(4754,'4754','Comercio al por menor de aparatos electrodomsticos en establecimientos especializados'),(4759,'4759','Comercio al por menor de muebles, aparatos de iluminacin y otros artculos de uso domstico en establecimientos especializados'),(4761,'4761','Comercio al por menor de libros en establecimientos especializados'),(4762,'4762','Comercio al por menor de peridicos y artculos de papelera en establecimientos especializados'),(4763,'4763','Comercio al por menor de grabaciones de msica y vdeo en establecimientos especializados'),(4764,'4764','Comercio al por menor de artculos deportivos en establecimientos especializados'),(4765,'4765','Comercio al por menor de juegos y juguetes en establecimientos especializados'),(4771,'4771','Comercio al por menor de prendas de vestir en establecimientos especializados'),(4772,'4772','Comercio al por menor de calzado y artculos de cuero en establecimientos especializados'),(4773,'4773','Comercio al por menor de productos farmacuticos en establecimientos especializados'),(4774,'4774','Comercio al por menor de artculos mdicos y ortopdicos en establecimientos especializados'),(4775,'4775','Comercio al por menor de productos cosmticos e higinicos en establecimientos especializados'),(4776,'4776','Comercio al por menor de flores, plantas, semillas, fertilizantes, animales de compaa y alimentos para los mismos en establecimientos especializados'),(4777,'4777','Comercio al por menor de artculos de relojera y joyera en establecimientos especializados'),(4778,'4778','Otro comercio al por menor de artculos nuevos en establecimientos especializados'),(4779,'4779','Comercio al por menor de artculos de segunda mano en establecimientos'),(4781,'4781','Comercio al por menor de productos alimenticios, bebidas y tabaco en puestos de venta y en mercadillos'),(4782,'4782','Comercio al por menor de productos textiles, prendas de vestir y calzado en puestos de venta y en mercadillos'),(4789,'4789','Comercio al por menor de otros productos en puestos de venta y en mercadillos'),(4791,'4791','Comercio al por menor por correspondencia o Internet'),(4799,'4799','Otro comercio al por menor no realizado ni en establecimientos, ni en puestos de venta ni en mercadillos'),(4910,'4910','Transporte interurbano de pasajeros por ferrocarril'),(4920,'4920','Transporte de mercancas por ferrocarril'),(4931,'4931','Transporte terrestre urbano y suburbano de pasajeros'),(4932,'4932','Transporte por taxi'),(4939,'4939','tipos de transporte terrestre de pasajeros n.c.o.p.'),(4941,'4941','Transporte de mercancas por carretera'),(4942,'4942','Servicios de mudanza'),(4950,'4950','Transporte por tubera'),(5010,'5010','Transporte martimo de pasajeros'),(5020,'5020','Transporte martimo de mercancas'),(5030,'5030','Transporte de pasajeros por vas navegables interiores'),(5040,'5040','Transporte de mercancas por vas navegables interiores'),(5110,'5110','Transporte areo de pasajeros'),(5121,'5121','Transporte areo de mercancas'),(5122,'5122','Transporte espacial'),(5210,'5210','Depsito y almacenamiento'),(5221,'5221','Actividades anexas al transporte terrestre'),(5222,'5222','Actividades anexas al transporte martimo y por vas navegables interiores'),(5223,'5223','Actividades anexas al transporte areo'),(5224,'5224','Manipulacin de mercancas'),(5229,'5229','Otras actividades anexas al transporte'),(5310,'5310','Actividades postales sometidas a la obligacin del servicio universal'),(5320,'5320','Otras actividades postales y de correos'),(5510,'5510','Hoteles y alojamientos similares'),(5520,'5520','Alojamientos tursticos y otros alojamientos de corta estancia'),(5530,'5530','Campings y aparcamientos para caravanas'),(5590,'5590','Otros alojamientos'),(5610,'5610','Restaurantes y puestos de comidas'),(5621,'5621','Provisin de comidas preparadas para eventos'),(5629,'5629','Otros servicios de comidas'),(5630,'5630','Establecimientos de bebidas'),(5811,'5811','Edicin de libros'),(5812,'5812','Edicin de directorios y guas de direcciones postales'),(5813,'5813','Edicin de peridicos'),(5814,'5814','Edicin de revistas'),(5819,'5819','Otras actividades editoriales'),(5821,'5821','Edicin de videojuegos'),(5829,'5829','Edicin de otros programas informticos'),(5912,'5912','Actividades de postproduccin cinematogrfica, de vdeo y de programas de televisin'),(5914,'5914','Actividades de exhibicin cinematogrfica'),(5915,'5915','Actividades de produccin cinematogrfica y de vdeo'),(5916,'5916','Actividades de producciones de programas de televisin'),(5917,'5917','Actividades de distribucin cinematogrfica y de vdeo'),(5918,'5918','Actividades de distribucin de programas de televisin'),(5920,'5920','Actividades de grabacin de sonido y edicin musical'),(6010,'6010','Actividades de radiodifusin'),(6020,'6020','Actividades de programacin y emisin de televisin'),(6110,'6110','Telecomunicaciones por cable'),(6120,'6120','Telecomunicaciones inalmbricas'),(6130,'6130','Telecomunicaciones por satlite'),(6190,'6190','Otras actividades de telecomunicaciones'),(6201,'6201','Actividades de programacin informtica'),(6202,'6202','Actividades de consultora informtica'),(6203,'6203','Gestin de recursos informticos'),(6209,'6209','Otros servicios relacionados con las tecnologas de la informacin y la informtica'),(6311,'6311','Proceso de datos, hosting y actividades relacionadas'),(6312,'6312','Portales web'),(6391,'6391','Actividades de las agencias de noticias'),(6399,'6399','Otros servicios de informacin n.c.o.p.'),(6411,'6411','Banco central'),(6419,'6419','Otra intermediacin monetaria'),(6420,'6420','Actividades de las sociedades holding'),(6430,'6430','Inversin colectiva, fondos y entidades financieras similares'),(6491,'6491','Arrendamiento financiero'),(6492,'6492','Otras actividades crediticias'),(6499,'6499','Otros servicios financieros, excepto seguros y fondos de pensiones n.c.o.p.'),(6511,'6511','Seguros de vida'),(6512,'6512','Seguros distintos de los seguros de vida'),(6520,'6520','Reaseguros'),(6530,'6530','Fondos de pensiones'),(6611,'6611','Administracin de mercados financieros'),(6612,'6612','Actividades de intermediacin en operaciones con valores y otros activos'),(6619,'6619','Otras actividades auxiliares a los servicios financieros, excepto seguros y fondos de pensiones'),(6621,'6621','Evaluacin de riesgos y daos'),(6622,'6622','Actividades de agentes y corredores de seguros'),(6629,'6629','Otras actividades auxiliares a seguros y fondos de pensiones'),(6630,'6630','Actividades de gestin de fondos'),(6810,'6810','Compraventa de bienes inmobiliarios por cuenta propia'),(6820,'6820','Alquiler de bienes inmobiliarios por cuenta propia'),(6831,'6831','Agentes de la propiedad inmobiliaria'),(6832,'6832','Gestin y administracin de la propiedad inmobiliaria'),(6910,'6910','Actividades jurdicas'),(6920,'6920','Actividades de contabilidad, tenedura de libros, auditora y asesora fiscal'),(7010,'7010','Actividades de las sedes centrales'),(7021,'7021','Relaciones pblicas y comunicacin'),(7022,'7022','Otras actividades de consultora de gestin empresarial'),(7111,'7111','Servicios tcnicos de arquitectura'),(7112,'7112','Servicios tcnicos de ingeniera y otras actividades relacionadas con el asesoramiento tcnico'),(7120,'7120','Ensayos y anlisis tcnicos'),(7211,'7211','Investigacin y desarrollo experimental en biotecnologa'),(7219,'7219','Otra investigacin y desarrollo experimental en ciencias naturales y tcnicas'),(7220,'7220','Investigacin y desarrollo experimental en ciencias sociales y humanidades'),(7311,'7311','Agencias de publicidad'),(7312,'7312','Servicios de representacin de medios de comunicacin'),(7320,'7320','Estudio de mercado y realizacin de encuestas de opinin pblica'),(7410,'7410','Actividades de diseo especializado'),(7420,'7420','Actividades de fotografa'),(7430,'7430','Actividades de traduccin e interpretacin'),(7490,'7490','Otras actividades profesionales, cientficas y tcnicas n.c.o.p.'),(7500,'7500','Actividades veterinarias'),(7711,'7711','Alquiler de automviles y vehculos de motor ligeros'),(7712,'7712','Alquiler de camiones'),(7721,'7721','Alquiler de artculos de ocio y deportivos'),(7722,'7722','Alquiler de cintas de vdeo y discos'),(7729,'7729','Alquiler de otros efectos personales y artculos de uso domstico'),(7731,'7731','Alquiler de maquinaria y equipo de uso agrcola'),(7732,'7732','Alquiler de maquinaria y equipo para la construccin e ingeniera civil'),(7733,'7733','Alquiler de maquinaria y equipo de oficina, incluidos ordenadores'),(7734,'7734','Alquiler de medios de navegacin'),(7735,'7735','Alquiler de medios de transporte areo'),(7739,'7739','Alquiler de otra maquinaria, equipos y bienes tangibles n.c.o.p.'),(7740,'7740','Arrendamiento de la propiedad intelectual y productos similares, excepto trabajos protegidos por los derechos de autor'),(7810,'7810','Actividades de las agencias de colocacin'),(7820,'7820','Actividades de las empresas de trabajo temporal'),(7830,'7830','Otra provisin de recursos humanos'),(7911,'7911','Actividades de las agencias de viajes'),(7912,'7912','Actividades de los operadores tursticos'),(7990,'7990','Otros servicios de reservas y actividades relacionadas con los mismos'),(8010,'8010','Actividades de seguridad privada'),(8020,'8020','Servicios de sistemas de seguridad'),(8030,'8030','Actividades de investigacin'),(8110,'8110','Servicios integrales a edificios e instalaciones'),(8121,'8121','Limpieza general de edificios'),(8122,'8122','Otras actividades de limpieza industrial y de edificios'),(8129,'8129','Otras actividades de limpieza'),(8130,'8130','Actividades de jardinera'),(8211,'8211','Servicios administrativos combinados'),(8219,'8219','Actividades de fotocopiado, preparacin de documentos y otras actividades especializadas de oficina'),(8220,'8220','Actividades de los centros de llamadas'),(8230,'8230','Organizacin de convenciones y ferias de muestras'),(8291,'8291','Actividades de las agencias de cobros y de informacin comercial'),(8292,'8292','Actividades de envasado y empaquetado'),(8299,'8299','Otras actividades de apoyo a las empresas n.c.o.p.'),(8411,'8411','Actividades generales de la Administracin Pblica'),(8412,'8412','Regulacin de las actividades sanitarias, educativas y culturales y otros servicios sociales, excepto Seguridad Social'),(8413,'8413','Regulacin de la actividad econmica y contribucin a su mayor eficiencia'),(8421,'8421','Asuntos exteriores'),(8422,'8422','Defensa'),(8423,'8423','Justicia'),(8424,'8424','Orden pblico y seguridad'),(8425,'8425','Proteccin civil'),(8430,'8430','Seguridad Social obligatoria'),(8510,'8510','Educacin preprimaria'),(8520,'8520','Educacin primaria'),(8531,'8531','Educacin secundaria general'),(8532,'8532','Educacin secundaria tcnica y profesional'),(8541,'8541','Educacin postsecundaria no terciaria'),(8543,'8543','Educacin universitaria'),(8544,'8544','Educacin terciaria no universitaria'),(8551,'8551','Educacin deportiva y recreativa'),(8552,'8552','Educacin cultural'),(8553,'8553','Actividades de las escuelas de conduccin y pilotaje'),(8559,'8559','Otra educacin n.c.o.p.'),(8560,'8560','Actividades auxiliares a la educacin'),(8610,'8610','Actividades hospitalarias'),(8621,'8621','Actividades de medicina general'),(8622,'8622','Actividades de medicina especializada'),(8623,'8623','Actividades odontolgicas'),(8690,'8690','Otras actividades sanitarias'),(8710,'8710','Asistencia en establecimientos residenciales con cuidados sanitarios'),(8720,'8720','Asistencia en establecimientos residenciales para personas con discapacidad intelectual, enfermedad mental y drogodependencia'),(8731,'8731','Asistencia en establecimientos residenciales para personas mayores'),(8732,'8732','Asistencia en establecimientos residenciales para personas con discapacidad fsica'),(8790,'8790','Otras actividades de asistencia en establecimientos residenciales'),(8811,'8811','Actividades de servicios sociales sin alojamiento para personas mayores'),(8812,'8812','Actividades de servicios sociales sin alojamiento para personas con discapacidad'),(8891,'8891','Actividades de cuidado diurno de nios'),(8899,'8899','Otros actividades de servicios sociales sin alojamiento n.c.o.p.'),(9001,'9001','Artes escnicas'),(9002,'9002','Actividades auxiliares a las artes escnicas'),(9003,'9003','Creacin artstica y literaria'),(9004,'9004','Gestin de salas de espectculos'),(9102,'9102','Actividades de museos'),(9103,'9103','Gestin de lugares y edificios histricos'),(9104,'9104','Actividades de los jardines botnicos, parques zoolgicos y reservas naturales'),(9105,'9105','Actividades de bibliotecas'),(9106,'9106','Actividades de archivos'),(9200,'9200','Actividades de juegos de azar y apuestas'),(9311,'9311','Gestin de instalaciones deportivas'),(9312,'9312','Actividades de los clubes deportivos'),(9313,'9313','Actividades de los gimnasios'),(9319,'9319','Otras actividades deportivas'),(9321,'9321','Actividades de los parques de atracciones y los parques temticos'),(9329,'9329','Otras actividades recreativas y de entretenimiento'),(9411,'9411','Actividades de organizaciones empresariales y patronales'),(9412,'9412','Actividades de organizaciones profesionales'),(9420,'9420','Actividades sindicales'),(9491,'9491','Actividades de organizaciones religiosas'),(9492,'9492','Actividades de organizaciones polticas'),(9499,'9499','Otras actividades asociativas n.c.o.p.'),(9511,'9511','Reparacin de ordenadores y equipos perifricos'),(9512,'9512','Reparacin de equipos de comunicacin'),(9521,'9521','Reparacin de aparatos electrnicos de audio y vdeo de uso domstico'),(9522,'9522','Reparacin de aparatos electrodomsticos y de equipos para el hogar y el jardn'),(9523,'9523','Reparacin de calzado y artculos de cuero'),(9524,'9524','Reparacin de muebles y artculos de menaje'),(9525,'9525','Reparacin de relojes y joyera'),(9529,'9529','Reparacin de otros efectos personales y artculos de uso domstico'),(9601,'9601','Lavado y limpieza de prendas textiles y de piel'),(9602,'9602','Peluquera y otros tratamientos de belleza'),(9603,'9603','Pompas fnebres y actividades relacionadas'),(9604,'9604','Actividades de mantenimiento fsico'),(9609,'9609','Otras servicios personales n.c.o.p.'),(9700,'9700','Actividades de los hogares como empleadores de personal domstico'),(9810,'9810','Actividades de los hogares como productores de bienes para uso propio'),(9820,'9820','Actividades de los hogares como productores de servicios para uso propio'),(9900,'9900','Actividades de organizaciones y organismos extraterritoriales');
ALTER TABLE `cnae` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `cnae2009`
#

DROP TABLE IF EXISTS `cnae2009`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cnae2009` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `code` varchar(4) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo del CNAE',
  `title` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Titulo del CNAE',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000991 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='CNAE 2009. Clasificacin Nacional de Actividades Econmicas ';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `cnae2009`
#

LOCK TABLES `cnae2009` WRITE;
ALTER TABLE `cnae2009` DISABLE KEYS;
INSERT INTO `cnae2009` VALUES (10,'10','Industria de la alimentacin'),(11,'11','Fabricacin de bebidas'),(12,'12','Industria del tabaco'),(13,'13','Industria textil'),(14,'14','Confeccin de prendas de vestir'),(15,'15','Industria del cuero y del calzado'),(16,'16','Industria de la madera y del corcho, excepto muebles; cestera y espartera'),(17,'17','Industria del papel'),(18,'18','Artes grficas y reproduccin de soportes grabados'),(19,'19','Coqueras y refino de petrleo'),(20,'20','Industria qumica'),(21,'21','Fabricacin de productos farmacuticos'),(22,'22','Fabricacin de productos de caucho y plsticos'),(23,'23','Fabricacin de otros productos minerales no metlicos'),(24,'24','Metalurgia; fabricacin de productos de hierro, acero y ferroaleaciones'),(25,'25','Fabricacin de productos metlicos, excepto maquinaria y equipo'),(26,'26','Fabricacin de productos informticos, electrnicos y pticos'),(27,'27','Fabricacin de material y equipo elctrico'),(28,'28','Fabricacin de maquinaria y equipo n.c.o.p.'),(29,'29','Fabricacin de vehculos de motor, remolques y semirremolques'),(30,'30','Fabricacin de otro material de transporte'),(31,'31','Fabricacin de muebles'),(32,'32','Otras industrias manufactureras'),(33,'33','Reparacin e instalacin de maquinaria y equipo'),(35,'35','Suministro de energa elctrica, gas, vapor y aire acondicionado'),(36,'36','Captacin, depuracin y distribucin de agua'),(37,'37','Recogida y tratamiento de aguas residuales'),(38,'38','Recogida, tratamiento y eliminacin de residuos; valorizacin'),(39,'39','Actividades de descontaminacin y otros servicios de gestin de residuos'),(41,'41','Construccin de edificios'),(42,'42','Ingeniera civil'),(43,'43','Actividades de construccin especializada'),(45,'45','Venta y reparacin de vehculos de motor y motocicletas'),(46,'46','Comercio al por mayor e intermediarios del comercio, excepto de vehculos de motor y motocicletas'),(47,'47','Comercio al por menor, excepto de vehculos de motor y motocicletas'),(49,'49','Transporte terrestre y por tubera'),(50,'50','Transporte martimo y por vas navegables interiores'),(51,'51','Transporte areo'),(52,'52','Almacenamiento y actividades anexas al transporte'),(53,'53','Actividades postales y de correos'),(55,'55','Servicios de alojamiento'),(56,'56','Servicios de comidas y bebidas'),(58,'58','Edicin'),(59,'59','Actividades cinematogrficas, de vdeo y de programas de televisin, grabacin de sonido y edicin musical'),(60,'60','Actividades de programacin y emisin de radio y televisin'),(61,'61','Telecomunicaciones'),(62,'62','Programacin, consultora y otras actividades relacionadas con la informtica'),(63,'63','Servicios de informacin'),(64,'64','Servicios financieros, excepto seguros y fondos de pensiones'),(65,'65','Seguros, reaseguros y fondos de pensiones, excepto Seguridad Social obligatoria'),(66,'66','Actividades auxiliares a los servicios financieros y a los seguros'),(68,'68','Actividades inmobiliarias'),(69,'69','Actividades jurdicas y de contabilidad'),(70,'70','Actividades de las sedes centrales; actividades de consultora de gestin empresarial'),(71,'71','Servicios tcnicos de arquitectura e ingeniera; ensayos y anlisis tcnicos'),(72,'72','Investigacin y desarrollo'),(73,'73','Publicidad y estudios de mercado'),(74,'74','Otras actividades profesionales, cientficas y tcnicas'),(75,'75','Actividades veterinarias'),(77,'77','Actividades de alquiler'),(78,'78','Actividades relacionadas con el empleo'),(79,'79','Actividades de agencias de viajes, operadores tursticos, servicios de reservas y actividades relacionadas con los mismos'),(80,'80','Actividades de seguridad e investigacin'),(81,'81','Servicios a edificios y actividades de jardinera'),(82,'82','Actividades administrativas de oficina y otras actividades auxiliares a las empresas'),(84,'84','Administracin Pblica y defensa; Seguridad Social obligatoria'),(85,'85','Educacin'),(86,'86','Actividades sanitarias'),(87,'87','Asistencia en establecimientos residenciales'),(88,'88','Actividades de servicios sociales sin alojamiento'),(90,'90','Actividades de creacin, artsticas y espectculos'),(91,'91','Actividades de bibliotecas, archivos, museos y otras actividades culturales'),(92,'92','Actividades de juegos de azar y apuestas'),(93,'93','Actividades deportivas, recreativas y de entretenimiento'),(94,'94','Actividades asociativas'),(95,'95','Reparacin de ordenadores, efectos personales y artculos de uso domstico'),(96,'96','Otros servicios personales'),(97,'97','Actividades de los hogares como empleadores de personal domstico'),(98,'98','Actividades de los hogares como productores de bienes y servicios para uso propio'),(99,'99','Actividades de organizaciones y organismos extraterritoriales'),(101,'101','Procesado y conservacin de carne y elaboracin de productos crnicos'),(102,'102','Procesado y conservacin de pescados, crustceos y moluscos'),(103,'103','Procesado y conservacin de frutas y hortalizas'),(104,'104','Fabricacin de aceites y grasas vegetales y animales'),(105,'105','Fabricacin de productos lcteos'),(106,'106','Fabricacin de productos de molinera, almidones y productos amilceos'),(107,'107','Fabricacin de productos de panadera y pastas alimenticias'),(108,'108','Fabricacin de otros productos alimenticios'),(109,'109','Fabricacin de productos para la alimentacin animal'),(110,'110','Fabricacin de bebidas'),(120,'120','Industria del tabaco'),(131,'131','Preparacin e hilado de fibras textiles'),(132,'132','Fabricacin de tejidos textiles'),(133,'133','Acabado de textiles'),(139,'139','Fabricacin de otros productos textiles'),(141,'141','Confeccin de prendas de vestir, excepto de peletera'),(142,'142','Fabricacin de artculos de peletera'),(143,'143','Confeccin de prendas de vestir de punto'),(151,'151','Preparacin, curtido y acabado del cuero; fabricacin de artculos de marroquinera, viaje y de guarnicionera y talabartera; preparacin y teido de pieles'),(152,'152','Fabricacin de calzado'),(161,'161','Aserrado y cepillado de la madera'),(162,'162','Fabricacin de productos de madera, corcho, cestera y espartera'),(171,'171','Fabricacin de pasta papelera, papel y cartn'),(172,'172','Fabricacin de artculos de papel y de cartn'),(181,'181','Artes grficas y servicios relacionados con las mismas'),(182,'182','Reproduccin de soportes grabados'),(191,'191','Coqueras'),(192,'192','Refino de petrleo'),(201,'201','Fabricacin de productos qumicos bsicos, compuestos nitrogenados, fertilizantes, plsticos y caucho sinttico en formas primarias'),(202,'202','Fabricacin de pesticidas y otros productos agroqumicos'),(203,'203','Fabricacin de pinturas, barnices y revestimientos similares; tintas de imprenta y masillas'),(204,'204','Fabricacin de jabones, detergentes y otros artculos de limpieza y abrillantamiento; fabricacin de perfumes y cosmticos'),(205,'205','Fabricacin de otros productos qumicos'),(206,'206','Fabricacin de fibras artificiales y sintticas'),(211,'211','Fabricacin de productos farmacuticos de base'),(212,'212','Fabricacin de especialidades farmacuticas'),(221,'221','Fabricacin de productos de caucho'),(222,'222','Fabricacin de productos de plstico'),(231,'231','Fabricacin de vidrio y productos de vidrio'),(232,'232','Fabricacin de productos cermicos refractarios'),(233,'233','Fabricacin de productos cermicos para la construccin'),(234,'234','Fabricacin de otros productos cermicos'),(235,'235','Fabricacin de cemento, cal y yeso'),(236,'236','Fabricacin de elementos de hormign, cemento y yeso'),(237,'237','Corte, tallado y acabado de la piedra'),(239,'239','Fabricacin de productos abrasivos y productos minerales no metlicos n.c.o.p.'),(241,'241','Fabricacin de productos bsicos de hierro, acero y ferroaleaciones'),(242,'242','Fabricacin de tubos, tuberas, perfiles huecos y sus accesorios, de acero'),(243,'243','Fabricacin de otros productos de primera transformacin del acero'),(244,'244','Produccin de metales preciosos y de otros metales no frreos'),(245,'245','Fundicin de metales'),(251,'251','Fabricacin de elementos metlicos para la construccin'),(252,'252','Fabricacin de cisternas, grandes depsitos y contenedores de metal'),(253,'253','Fabricacin de generadores de vapor, excepto calderas de calefaccin central'),(254,'254','Fabricacin de armas y municiones'),(255,'255','Forja, estampacin y embuticin de metales; metalurgia de polvos'),(256,'256','Tratamiento y revestimiento de metales; ingeniera mecnica por cuenta de terceros'),(257,'257','Fabricacin de artculos de cuchillera y cubertera, herramientas y ferretera'),(259,'259','Fabricacin de otros productos metlicos'),(261,'261','Fabricacin de componentes electrnicos y circuitos impresos ensamblados'),(262,'262','Fabricacin de ordenadores y equipos perifricos'),(263,'263','Fabricacin de equipos de telecomunicaciones'),(264,'264','Fabricacin de productos electrnicos de consumo'),(265,'265','Fabricacin de instrumentos y aparatos de medida, verificacin y navegacin; fabricacin de relojes'),(266,'266','Fabricacin de equipos de radiacin, electromdicos y electroteraputicos'),(267,'267','Fabricacin de instrumentos de ptica y equipo fotogrfico'),(268,'268','Fabricacin de soportes magnticos y pticos'),(271,'271','Fabricacin de motores, generadores y transformadores elctricos, y de aparatos de distribucin y control elctrico'),(272,'272','Fabricacin de pilas y acumuladores elctricos'),(273,'273','Fabricacin de cables y dispositivos de cableado'),(274,'274','Fabricacin de lmparas y aparatos elctricos de iluminacin'),(275,'275','Fabricacin de aparatos domsticos'),(279,'279','Fabricacin de otro material y equipo elctrico'),(281,'281','Fabricacin de maquinaria de uso general'),(282,'282','Fabricacin de otra maquinaria de uso general'),(283,'283','Fabricacin de maquinaria agraria y forestal'),(284,'284','Fabricacin de mquinas herramienta para trabajar el metal y otras mquinas herramienta'),(289,'289','Fabricacin de otra maquinaria para usos especficos'),(291,'291','Fabricacin de vehculos de motor'),(292,'292','Fabricacin de carroceras para vehculos de motor; fabricacin de remolques y semirremolques'),(293,'293','Fabricacin de componentes, piezas y accesorios para vehculos de motor'),(301,'301','Construccin naval'),(302,'302','Fabricacin de locomotoras y material ferroviario'),(303,'303','Construccin aeronutica y espacial y su maquinaria'),(304,'304','Fabricacin de vehculos militares de combate'),(309,'309','Fabricacin de otro material de transporte n.c.o.p.'),(310,'310','Fabricacin de muebles'),(321,'321','Fabricacin de artculos de joyera, bisutera y similares'),(322,'322','Fabricacin de instrumentos musicales'),(323,'323','Fabricacin de artculos de deporte'),(324,'324','Fabricacin de juegos y juguetes'),(325,'325','Fabricacin de instrumentos y suministros mdicos y odontolgicos'),(329,'329','Industrias manufactureras n.c.o.p.'),(331,'331','Reparacin de productos metlicos, maquinaria y equipo'),(332,'332','Instalacin de mquinas y equipos industriales'),(351,'351','Produccin, transporte y distribucin de energa elctrica'),(352,'352','Produccin de gas; distribucin por tubera de combustibles gaseosos'),(353,'353','Suministro de vapor y aire acondicionado'),(360,'360','Captacin, depuracin y distribucin de agua'),(370,'370','Recogida y tratamiento de aguas residuales'),(381,'381','Recogida de residuos'),(382,'382','Tratamiento y eliminacin de residuos'),(383,'383','Valorizacin'),(390,'390','Actividades de descontaminacin y otros servicios de gestin de residuos'),(411,'411','Promocin inmobiliaria'),(412,'412','Construccin de edificios'),(421,'421','Construccin de carreteras y vas frreas, puentes y tneles'),(422,'422','Construccin de redes'),(429,'429','Construccin de otros proyectos de ingeniera civil'),(431,'431','Demolicin y preparacin de terrenos'),(432,'432','Instalaciones elctricas, de fontanera y otras instalaciones en obras de construccin'),(433,'433','Acabado de edificios'),(439,'439','Otras actividades de construccin especializada'),(451,'451','Venta de vehculos de motor'),(452,'452','Mantenimiento y reparacin de vehculos de motor'),(453,'453','Comercio de repuestos y accesorios de vehculos de motor'),(454,'454','Venta, mantenimiento y reparacin de motocicletas y de sus repuestos y accesorios'),(461,'461','Intermediarios del comercio'),(462,'462','Comercio al por mayor de materias primas agrarias y de animales vivos'),(463,'463','Comercio al por mayor de productos alimenticios, bebidas y tabaco'),(464,'464','Comercio al por mayor de artculos de uso domstico'),(465,'465','Comercio al por mayor de equipos para las tecnologas de la informacin y las comunicaciones'),(466,'466','Comercio al por mayor de otra maquinaria, equipos y suministros'),(467,'467','Otro comercio al por mayor especializado'),(469,'469','Comercio al por mayor no especializado'),(471,'471','Comercio al por menor en establecimientos no especializados'),(472,'472','Comercio al por menor de productos alimenticios, bebidas y tabaco en establecimientos especializados'),(473,'473','Comercio al por menor de combustible para la automocin en establecimientos especializados'),(474,'474','Comercio al por menor de equipos para las tecnologas de la informacin y las comunicaciones en establecimientos especializados'),(475,'475','Comercio al por menor de otros artculos de uso domstico en establecimientos especializados'),(476,'476','Comercio al por menor de artculos culturales y recreativos en establecimientos especializados'),(477,'477','Comercio al por menor de otros artculos en establecimientos especializados'),(478,'478','Comercio al por menor en puestos de venta y en mercadillos'),(479,'479','Comercio al por menor no realizado ni en establecimientos, ni en puestos de venta ni en mercadillos'),(491,'491','Transporte interurbano de pasajeros por ferrocarril'),(492,'492','Transporte de mercancas por ferrocarril'),(493,'493','Otro transporte terrestre de pasajeros'),(494,'494','Transporte de mercancas por carretera y servicios de mudanza'),(495,'495','Transporte por tubera'),(501,'501','Transporte martimo de pasajeros'),(502,'502','Transporte martimo de mercancas'),(503,'503','Transporte de pasajeros por vas navegables interiores'),(504,'504','Transporte de mercancas por vas navegables interiores'),(511,'511','Transporte areo de pasajeros'),(512,'512','Transporte areo de mercancas y transporte espacial'),(521,'521','Depsito y almacenamiento'),(522,'522','Actividades anexas al transporte'),(531,'531','Actividades postales sometidas a la obligacin del servicio universal'),(532,'532','Otras actividades postales y de correos'),(551,'551','Hoteles y alojamientos similares'),(552,'552','Alojamientos tursticos y otros alojamientos de corta estancia'),(553,'553','Campings y aparcamientos para caravanas'),(559,'559','Otros alojamientos'),(561,'561','Restaurantes y puestos de comidas'),(562,'562','Provisin de comidas preparadas para eventos y otros servicios de comidas'),(563,'563','Establecimientos de bebidas'),(581,'581','Edicin de libros, peridicos y otras actividades editoriales'),(582,'582','Edicin de programas informticos'),(591,'591','Actividades cinematogrficas, de vdeo y de programas de televisin'),(592,'592','Actividades de grabacin de sonido y edicin musical'),(601,'601','Actividades de radiodifusin'),(602,'602','Actividades de programacin y emisin de televisin'),(611,'611','Telecomunicaciones por cable'),(612,'612','Telecomunicaciones inalmbricas'),(613,'613','Telecomunicaciones por satlite'),(619,'619','Otras actividades de telecomunicaciones'),(620,'620','Programacin, consultora y otras actividades relacionadas con la informtica'),(631,'631','Proceso de datos, hosting y actividades relacionadas; portales web'),(639,'639','Otros servicios de informacin'),(641,'641','Intermediacin monetaria'),(642,'642','Actividades de las sociedades holding'),(643,'643','Inversin colectiva, fondos y entidades financieras similares'),(649,'649','Otros servicios financieros, excepto seguros y fondos de pensiones'),(651,'651','Seguros'),(652,'652','Reaseguros'),(653,'653','Fondos de pensiones'),(661,'661','Actividades auxiliares a los servicios financieros, excepto seguros y fondos de pensiones'),(662,'662','Actividades auxiliares a seguros y fondos de pensiones'),(663,'663','Actividades de gestin de fondos'),(681,'681','Compraventa de bienes inmobiliarios por cuenta propia'),(682,'682','Alquiler de bienes inmobiliarios por cuenta propia'),(683,'683','Actividades inmobiliarias por cuenta de terceros'),(691,'691','Actividades jurdicas'),(692,'692','Actividades de contabilidad, tenedura de libros, auditora y asesora fiscal'),(701,'701','Actividades de las sedes centrales'),(702,'702','Actividades de consultora de gestin empresarial'),(711,'711','Servicios tcnicos de arquitectura e ingeniera y otras actividades relacionadas con el asesoramiento tcnico'),(712,'712','Ensayos y anlisis tcnicos'),(721,'721','Investigacin y desarrollo experimental en ciencias naturales y tcnicas'),(722,'722','Investigacin y desarrollo experimental en ciencias sociales y humanidades'),(731,'731','Publicidad'),(732,'732','Estudio de mercado y realizacin de encuestas de opinin pblica'),(741,'741','Actividades de diseo especializado'),(742,'742','Actividades de fotografa'),(743,'743','Actividades de traduccin e interpretacin'),(749,'749','Otras actividades profesionales, cientficas y tcnicas n.c.o.p.'),(750,'750','Actividades veterinarias'),(771,'771','Alquiler de vehculos de motor'),(772,'772','Alquiler de efectos personales y artculos de uso domstico'),(773,'773','Alquiler de otra maquinaria, equipos y bienes tangibles'),(774,'774','Arrendamiento de la propiedad intelectual y productos similares, excepto trabajos protegidos por los derechos de autor'),(781,'781','Actividades de las agencias de colocacin'),(782,'782','Actividades de las empresas de trabajo temporal'),(783,'783','Otra provisin de recursos humanos'),(791,'791','Actividades de agencias de viajes y operadores tursticos'),(799,'799','Otros servicios de reservas y actividades relacionadas con los mismos'),(801,'801','Actividades de seguridad privada'),(802,'802','Servicios de sistemas de seguridad'),(803,'803','Actividades de investigacin'),(811,'811','Servicios integrales a edificios e instalaciones'),(812,'812','Actividades de limpieza'),(813,'813','Actividades de jardinera'),(821,'821','Actividades administrativas y auxiliares de oficina'),(822,'822','Actividades de los centros de llamadas'),(823,'823','Organizacin de convenciones y ferias de muestras'),(829,'829','Actividades de apoyo a las empresas n.c.o.p.'),(841,'841','Administracin Pblica y de la poltica econmica y social'),(842,'842','Prestacin de servicios a la comunidad en general'),(843,'843','Seguridad Social obligatoria'),(851,'851','Educacin preprimaria'),(852,'852','Educacin primaria'),(853,'853','Educacin secundaria'),(854,'854','Educacin postsecundaria'),(855,'855','Otra educacin'),(856,'856','Actividades auxiliares a la educacin'),(861,'861','Actividades hospitalarias'),(862,'862','Actividades mdicas y odontolgicas'),(869,'869','Otras actividades sanitarias'),(871,'871','Asistencia en establecimientos residenciales con cuidados sanitarios'),(872,'872','Asistencia en establecimientos residenciales para personas con discapacidad intelectual, enfermedad mental y drogodependencia'),(873,'873','Asistencia en establecimientos residenciales para personas mayores y con discapacidad fsica'),(879,'879','Otras actividades de asistencia en establecimientos residenciales'),(881,'881','Actividades de servicios sociales sin alojamiento para personas mayores y con discapacidad'),(889,'889','Otros actividades de servicios sociales sin alojamiento'),(900,'900','Actividades de creacin, artsticas y espectculos'),(910,'910','Actividades de bibliotecas, archivos, museos y otras actividades culturales'),(920,'920','Actividades de juegos de azar y apuestas'),(931,'931','Actividades deportivas'),(932,'932','Actividades recreativas y de entretenimiento'),(941,'941','Actividades de organizaciones empresariales, profesionales y patronales'),(942,'942','Actividades sindicales'),(949,'949','Otras actividades asociativas'),(951,'951','Reparacin de ordenadores y equipos de comunicacin'),(952,'952','Reparacin de efectos personales y artculos de uso domstico'),(960,'960','Otros servicios personales'),(970,'970','Actividades de los hogares como empleadores de personal domstico'),(981,'981','Actividades de los hogares como productores de bienes para uso propio'),(982,'982','Actividades de los hogares como productores de servicios para uso propio'),(990,'990','Actividades de organizaciones y organismos extraterritoriales'),(1011,'1011','Procesado y conservacin de carne'),(1012,'1012','Procesado y conservacin de volatera'),(1013,'1013','Elaboracin de productos crnicos y de volatera'),(1021,'1021','Procesado de pescados, crustceos y moluscos'),(1022,'1022','Fabricacin de conservas de pescado'),(1031,'1031','Procesado y conservacin de patatas'),(1032,'1032','Elaboracin de zumos de frutas y hortalizas'),(1039,'1039','Otro procesado y conservacin de frutas y hortalizas'),(1042,'1042','Fabricacin de margarina y grasas comestibles similares'),(1043,'1043','Fabricacin de aceite de oliva'),(1044,'1044','Fabricacin de otros aceites y grasas'),(1052,'1052','Elaboracin de helados'),(1053,'1053','Fabricacin de quesos'),(1054,'1054','Preparacin de leche y otros productos lcteos'),(1061,'1061','Fabricacin de productos de molinera'),(1062,'1062','Fabricacin de almidones y productos amilceos'),(1071,'1071','Fabricacin de pan y de productos frescos de panadera y pastelera'),(1072,'1072','Fabricacin de galletas y productos de panadera y pastelera de larga duracin'),(1073,'1073','Fabricacin de pastas alimenticias, cuscs y productos similares'),(1081,'1081','Fabricacin de azcar'),(1082,'1082','Fabricacin de cacao, chocolate y productos de confitera'),(1083,'1083','Elaboracin de caf, t e infusiones'),(1084,'1084','Elaboracin de especias, salsas y condimentos'),(1085,'1085','Elaboracin de platos y comidas preparados'),(1086,'1086','Elaboracin de preparados alimenticios homogeneizados y alimentos dietticos'),(1089,'1089','Elaboracin de otros productos alimenticios n.c.o.p.'),(1091,'1091','Fabricacin de productos para la alimentacin de animales de granja'),(1092,'1092','Fabricacin de productos para la alimentacin de animales de compaa'),(1101,'1101','Destilacin, rectificacin y mezcla de bebidas alcohlicas'),(1102,'1102','Elaboracin de vinos'),(1103,'1103','Elaboracin de sidra y otras bebidas fermentadas a partir de frutas'),(1104,'1104','Elaboracin de otras bebidas no destiladas, procedentes de la fermentacin'),(1105,'1105','Fabricacin de cerveza'),(1106,'1106','Fabricacin de malta'),(1107,'1107','Fabricacin de bebidas no alcohlicas; produccin de aguas minerales y otras aguas embotelladas'),(1200,'1200','Industria del tabaco'),(1310,'1310','Preparacin e hilado de fibras textiles'),(1320,'1320','Fabricacin de tejidos textiles'),(1330,'1330','Acabado de textiles'),(1391,'1391','Fabricacin de tejidos de punto'),(1392,'1392','Fabricacin de artculos confeccionados con textiles, excepto prendas de vestir'),(1393,'1393','Fabricacin de alfombras y moquetas'),(1394,'1394','Fabricacin de cuerdas, cordeles, bramantes y redes'),(1395,'1395','Fabricacin de telas no tejidas y artculos confeccionados con ellas, excepto prendas de vestir'),(1396,'1396','Fabricacin de otros productos textiles de uso tcnico e industrial'),(1399,'1399','Fabricacin de otros productos textiles n.c.o.p.'),(1411,'1411','Confeccin de prendas de vestir de cuero'),(1412,'1412','Confeccin de ropa de trabajo'),(1413,'1413','Confeccin de otras prendas de vestir exteriores'),(1414,'1414','Confeccin de ropa interior'),(1419,'1419','Confeccin de otras prendas de vestir y accesorios'),(1420,'1420','Fabricacin de artculos de peletera'),(1431,'1431','Confeccin de calcetera'),(1439,'1439','Confeccin de otras prendas de vestir de punto'),(1511,'1511','Preparacin, curtido y acabado del cuero; preparacin y teido de pieles'),(1512,'1512','Fabricacin de artculos de marroquinera, viaje y de guarnicionera y talabartera'),(1520,'1520','Fabricacin de calzado'),(1610,'1610','Aserrado y cepillado de la madera'),(1621,'1621','Fabricacin de chapas y tableros de madera'),(1622,'1622','Fabricacin de suelos de madera ensamblados'),(1623,'1623','Fabricacin de otras estructuras de madera y piezas de carpintera y ebanistera para la construccin'),(1624,'1624','Fabricacin de envases y embalajes de madera'),(1629,'1629','Fabricacin de otros productos de madera; artculos de corcho, cestera y espartera'),(1711,'1711','Fabricacin de pasta papelera'),(1712,'1712','Fabricacin de papel y cartn'),(1721,'1721','Fabricacin de papel y cartn ondulados; fabricacin de envases y embalajes de papel y cartn'),(1722,'1722','Fabricacin de artculos de papel y cartn para uso domstico, sanitario e higinico'),(1723,'1723','Fabricacin de artculos de papelera'),(1724,'1724','Fabricacin de papeles pintados'),(1729,'1729','Fabricacin de otros artculos de papel y cartn'),(1811,'1811','Artes grficas y servicios relacionados con las mismas'),(1812,'1812','Otras actividades de impresin y artes grficas'),(1813,'1813','Servicios de preimpresin y preparacin de soportes'),(1814,'1814','Encuadernacin y servicios relacionados con la misma'),(1820,'1820','Reproduccin de soportes grabados'),(1910,'1910','Coqueras'),(1920,'1920','Refino de petrleo'),(2011,'2011','Fabricacin de gases industriales'),(2012,'2012','Fabricacin de colorantes y pigmentos'),(2013,'2013','Fabricacin de otros productos bsicos de qumica inorgnica'),(2014,'2014','Fabricacin de otros productos bsicos de qumica orgnica'),(2015,'2015','Fabricacin de fertilizantes y compuestos nitrogenados'),(2016,'2016','Fabricacin de plsticos en formas primarias'),(2017,'2017','Fabricacin de caucho sinttico en formas primarias'),(2020,'2020','Fabricacin de pesticidas y otros productos agroqumicos'),(2030,'2030','Fabricacin de pinturas, barnices y revestimientos similares; tintas de imprenta y masillas'),(2041,'2041','Fabricacin de jabones, detergentes y otros artculos de limpieza y abrillantamiento'),(2042,'2042','Fabricacin de perfumes y cosmticos'),(2051,'2051','Fabricacin de explosivos'),(2052,'2052','Fabricacin de colas'),(2053,'2053','Fabricacin de aceites esenciales'),(2059,'2059','Fabricacin de otros productos qumicos n.c.o.p.'),(2060,'2060','Fabricacin de fibras artificiales y sintticas'),(2110,'2110','Fabricacin de productos farmacuticos de base'),(2120,'2120','Fabricacin de especialidades farmacuticas'),(2211,'2211','Fabricacin de neumticos y cmaras de caucho; reconstruccin y recauchutado de neumticos'),(2219,'2219','Fabricacin de otros productos de caucho'),(2221,'2221','Fabricacin de placas, hojas, tubos y perfiles de plstico'),(2222,'2222','Fabricacin de envases y embalajes de plstico'),(2223,'2223','Fabricacin de productos de plstico para la construccin'),(2229,'2229','Fabricacin de otros productos de plstico'),(2311,'2311','Fabricacin de vidrio plano'),(2312,'2312','Manipulado y transformacin de vidrio plano'),(2313,'2313','Fabricacin de vidrio hueco'),(2314,'2314','Fabricacin de fibra de vidrio'),(2319,'2319','Fabricacin y manipulado de otro vidrio, incluido el vidrio tcnico'),(2320,'2320','Fabricacin de productos cermicos refractarios'),(2331,'2331','Fabricacin de azulejos y baldosas de cermica'),(2332,'2332','Fabricacin de ladrillos, tejas y productos de tierras cocidas para la construccin'),(2341,'2341','Fabricacin de artculos cermicos de uso domstico y ornamental'),(2342,'2342','Fabricacin de aparatos sanitarios cermicos'),(2343,'2343','Fabricacin de aisladores y piezas aislantes de material cermico'),(2344,'2344','Fabricacin de otros productos cermicos de uso tcnico'),(2349,'2349','Fabricacin de otros productos cermicos'),(2351,'2351','Fabricacin de cemento'),(2352,'2352','Fabricacin de cal y yeso'),(2361,'2361','Fabricacin de elementos de hormign para la construccin'),(2362,'2362','Fabricacin de elementos de yeso para la construccin'),(2363,'2363','Fabricacin de hormign fresco'),(2364,'2364','Fabricacin de mortero'),(2365,'2365','Fabricacin de fibrocemento'),(2369,'2369','Fabricacin de otros productos de hormign, yeso y cemento'),(2370,'2370','Corte, tallado y acabado de la piedra'),(2391,'2391','Fabricacin de productos abrasivos'),(2399,'2399','Fabricacin de otros productos minerales no metlicos n.c.o.p.'),(2410,'2410','Fabricacin de productos bsicos de hierro, acero y ferroaleaciones'),(2420,'2420','Fabricacin de tubos, tuberas, perfiles huecos y sus accesorios, de acero'),(2431,'2431','Estirado en fro'),(2432,'2432','Laminacin en fro'),(2433,'2433','Produccin de perfiles en fro por conformacin con plegado'),(2434,'2434','Trefilado en fro'),(2441,'2441','Produccin de metales preciosos'),(2442,'2442','Produccin de aluminio'),(2443,'2443','Produccin de plomo, zinc y estao'),(2444,'2444','Produccin de cobre'),(2445,'2445','Produccin de otros metales no frreos'),(2446,'2446','Procesamiento de combustibles nucleares'),(2451,'2451','Fundicin de hierro'),(2452,'2452','Fundicin de acero'),(2453,'2453','Fundicin de metales ligeros'),(2454,'2454','Fundicin de otros metales no frreos'),(2511,'2511','Fabricacin de estructuras metlicas y sus componentes'),(2512,'2512','Fabricacin de carpintera metlica'),(2521,'2521','Fabricacin de radiadores y calderas para calefaccin central'),(2529,'2529','Fabricacin de otras cisternas, grandes depsitos y contenedores de metal'),(2530,'2530','Fabricacin de generadores de vapor, excepto calderas de calefaccin central'),(2540,'2540','Fabricacin de armas y municiones'),(2550,'2550','Forja, estampacin y embuticin de metales; metalurgia de polvos'),(2561,'2561','Tratamiento y revestimiento de metales'),(2562,'2562','Ingeniera mecnica por cuenta de terceros'),(2571,'2571','Fabricacin de artculos de cuchillera y cubertera'),(2572,'2572','Fabricacin de cerraduras y herrajes'),(2573,'2573','Fabricacin de herramientas'),(2591,'2591','Fabricacin de bidones y toneles de hierro o acero'),(2592,'2592','Fabricacin de envases y embalajes metlicos ligeros'),(2593,'2593','Fabricacin de productos de alambre, cadenas y muelles'),(2594,'2594','Fabricacin de pernos y productos de tornillera'),(2599,'2599','Fabricacin de otros productos metlicos n.c.o.p.'),(2611,'2611','Fabricacin de componentes electrnicos'),(2612,'2612','Fabricacin de circuitos impresos ensamblados'),(2620,'2620','Fabricacin de ordenadores y equipos perifricos'),(2630,'2630','Fabricacin de equipos de telecomunicaciones'),(2640,'2640','Fabricacin de productos electrnicos de consumo'),(2651,'2651','Fabricacin de instrumentos y aparatos de medida, verificacin y navegacin'),(2652,'2652','Fabricacin de relojes'),(2660,'2660','Fabricacin de equipos de radiacin, electromdicos y electroteraputicos'),(2670,'2670','Fabricacin de instrumentos de ptica y equipo fotogrfico'),(2680,'2680','Fabricacin de soportes magnticos y pticos'),(2711,'2711','Fabricacin de motores, generadores y transformadores elctricos'),(2712,'2712','Fabricacin de aparatos de distribucin y control elctrico'),(2720,'2720','Fabricacin de pilas y acumuladores elctricos'),(2731,'2731','Fabricacin de cables de fibra ptica'),(2732,'2732','Fabricacin de otros hilos y cables electrnicos y elctricos'),(2733,'2733','Fabricacin de dispositivos de cableado'),(2740,'2740','Fabricacin de lmparas y aparatos elctricos de iluminacin'),(2751,'2751','Fabricacin de electrodomsticos'),(2752,'2752','Fabricacin de aparatos domsticos no elctricos'),(2790,'2790','Fabricacin de otro material y equipo elctrico'),(2811,'2811','Fabricacin de motores y turbinas, excepto los destinados a aeronaves, vehculos automviles y ciclomotores'),(2812,'2812','Fabricacin de equipos de transmisin hidrulica y neumtica'),(2813,'2813','Fabricacin de otras bombas y compresores'),(2814,'2814','Fabricacin de otra grifera y vlvulas'),(2815,'2815','Fabricacin de cojinetes, engranajes y rganos mecnicos de transmisin'),(2821,'2821','Fabricacin de hornos y quemadores'),(2822,'2822','Fabricacin de maquinaria de elevacin y manipulacin'),(2823,'2823','Fabricacin de mquinas y equipos de oficina, excepto equipos informticos'),(2824,'2824','Fabricacin de herramientas elctricas manuales'),(2825,'2825','Fabricacin de maquinaria de ventilacin y refrigeracin no domstica'),(2829,'2829','Fabricacin de otra maquinaria de uso general n.c.o.p.'),(2830,'2830','Fabricacin de maquinaria agraria y forestal'),(2841,'2841','Fabricacin de mquinas herramienta para trabajar el metal'),(2849,'2849','Fabricacin de otras mquinas herramienta'),(2891,'2891','Fabricacin de maquinaria para la industria metalrgica'),(2892,'2892','Fabricacin de maquinaria para las industrias extractivas y de la construccin'),(2893,'2893','Fabricacin de maquinaria para la industria de la alimentacin, bebidas y tabaco'),(2894,'2894','Fabricacin de maquinaria para las industrias textil, de la confeccin y del cuero'),(2895,'2895','Fabricacin de maquinaria para la industria del papel y del cartn'),(2896,'2896','Fabricacin de maquinaria para la industria del plstico y el caucho'),(2899,'2899','Fabricacin de otra maquinaria para usos especficos n.c.o.p.'),(2910,'2910','Fabricacin de vehculos de motor'),(2920,'2920','Fabricacin de carroceras para vehculos de motor; fabricacin de remolques y semirremolques'),(2931,'2931','Fabricacin de equipos elctricos y electrnicos para vehculos de motor'),(2932,'2932','Fabricacin de otros componentes, piezas y accesorios para vehculos de motor'),(3011,'3011','Construccin de barcos y estructuras flotantes'),(3012,'3012','Construccin de embarcaciones de recreo y deporte'),(3020,'3020','Fabricacin de locomotoras y material ferroviario'),(3030,'3030','Construccin aeronutica y espacial y su maquinaria'),(3040,'3040','Fabricacin de vehculos militares de combate'),(3091,'3091','Fabricacin de motocicletas'),(3092,'3092','Fabricacin de bicicletas y de vehculos para personas con discapacidad'),(3099,'3099','Fabricacin de otro material de transporte n.c.o.p.'),(3101,'3101','Fabricacin de muebles de oficina y de establecimientos comerciales'),(3102,'3102','Fabricacin de muebles de cocina'),(3103,'3103','Fabricacin de colchones'),(3109,'3109','Fabricacin de otros muebles'),(3211,'3211','Fabricacin de monedas'),(3212,'3212','Fabricacin de artculos de joyera y artculos similares'),(3213,'3213','Fabricacin de artculos de bisutera y artculos similares'),(3220,'3220','Fabricacin de instrumentos musicales'),(3230,'3230','Fabricacin de artculos de deporte'),(3240,'3240','Fabricacin de juegos y juguetes'),(3250,'3250','Fabricacin de instrumentos y suministros mdicos y odontolgicos'),(3291,'3291','Fabricacin de escobas, brochas y cepillos'),(3299,'3299','Otras industrias manufactureras n.c.o.p.'),(3311,'3311','Reparacin de productos metlicos'),(3312,'3312','Reparacin de maquinaria'),(3313,'3313','Reparacin de equipos electrnicos y pticos'),(3314,'3314','Reparacin de equipos elctricos'),(3315,'3315','Reparacin y mantenimiento naval'),(3316,'3316','Reparacin y mantenimiento aeronutico y espacial'),(3317,'3317','Reparacin y mantenimiento de otro material de transporte'),(3319,'3319','Reparacin de otros equipos'),(3320,'3320','Instalacin de mquinas y equipos industriales'),(3512,'3512','Transporte de energa elctrica'),(3513,'3513','Distribucin de energa elctrica'),(3514,'3514','Comercio de energa elctrica'),(3515,'3515','Produccin de energa hidroelctrica'),(3516,'3516','Produccin de energa elctrica de origen trmico convencional'),(3517,'3517','Produccin de energa elctrica de origen nuclear'),(3518,'3518','Produccin de energa elctrica de origen elico'),(3519,'3519','Produccin de energa elctrica de otros tipos'),(3521,'3521','Produccin de gas'),(3522,'3522','Distribucin por tubera de combustibles gaseosos'),(3523,'3523','Comercio de gas por tubera'),(3530,'3530','Suministro de vapor y aire acondicionado'),(3600,'3600','Captacin, depuracin y distribucin de agua'),(3700,'3700','Recogida y tratamiento de aguas residuales'),(3811,'3811','Recogida de residuos no peligrosos'),(3812,'3812','Recogida de residuos peligrosos'),(3821,'3821','Tratamiento y eliminacin de residuos no peligrosos'),(3822,'3822','Tratamiento y eliminacin de residuos peligrosos'),(3831,'3831','Separacin y clasificacin de materiales'),(3832,'3832','Valorizacin de materiales ya clasificados'),(3900,'3900','Actividades de descontaminacin y otros servicios de gestin de residuos'),(4110,'4110','Promocin inmobiliaria'),(4121,'4121','Construccin de edificios residenciales'),(4122,'4122','Construccin de edificios no residenciales'),(4211,'4211','Construccin de carreteras y autopistas'),(4212,'4212','Construccin de vas frreas de superficie y subterrneas'),(4213,'4213','Construccin de puentes y tneles'),(4221,'4221','Construccin de redes para fluidos'),(4222,'4222','Construccin de redes elctricas y de telecomunicaciones'),(4291,'4291','Obras hidrulicas'),(4299,'4299','Construccin de otros proyectos de ingeniera civil n.c.o.p.'),(4311,'4311','Demolicin'),(4312,'4312','Preparacin de terrenos'),(4313,'4313','Perforaciones y sondeos'),(4321,'4321','Instalaciones elctricas'),(4322,'4322','Fontanera, instalaciones de sistemas de calefaccin y aire acondicionado'),(4329,'4329','Otras instalaciones en obras de construccin'),(4331,'4331','Revocamiento'),(4332,'4332','Instalacin de carpintera'),(4333,'4333','Revestimiento de suelos y paredes'),(4334,'4334','Pintura y acristalamiento'),(4339,'4339','Otro acabado de edificios'),(4391,'4391','Construccin de cubiertas'),(4399,'4399','Otras actividades de construccin especializada n.c.o.p.'),(4511,'4511','Venta de automviles y vehculos de motor ligeros'),(4519,'4519','Venta de otros vehculos de motor'),(4520,'4520','Mantenimiento y reparacin de vehculos de motor'),(4531,'4531','Comercio al por mayor de repuestos y accesorios de vehculos de motor'),(4532,'4532','Comercio al por menor de repuestos y accesorios de vehculos de motor'),(4540,'4540','Venta, mantenimiento y reparacin de motocicletas y de sus repuestos y accesorios'),(4611,'4611','Intermediarios del comercio de materias primas agrarias, animales vivos, materias primas textiles y productos semielaborados'),(4612,'4612','Intermediarios del comercio de combustibles, minerales, metales y productos qumicos industriales'),(4613,'4613','Intermediarios del comercio de la madera y materiales de construccin'),(4614,'4614','Intermediarios del comercio de maquinaria, equipo industrial, embarcaciones y aeronaves'),(4615,'4615','Intermediarios del comercio de muebles, artculos para el hogar y ferretera'),(4616,'4616','Intermediarios del comercio de textiles, prendas de vestir, peletera, calzado y artculos de cuero'),(4617,'4617','Intermediarios del comercio de productos alimenticios, bebidas y tabaco'),(4618,'4618','Intermediarios del comercio especializados en la venta de otros productos especficos'),(4619,'4619','Intermediarios del comercio de productos diversos'),(4621,'4621','Comercio al por mayor de cereales, tabaco en rama, simientes y alimentos para animales'),(4622,'4622','Comercio al por mayor de flores y plantas'),(4623,'4623','Comercio al por mayor de animales vivos'),(4624,'4624','Comercio al por mayor de cueros y pieles'),(4631,'4631','Comercio al por mayor de frutas y hortalizas'),(4632,'4632','Comercio al por mayor de carne y productos crnicos'),(4633,'4633','Comercio al por mayor de productos lcteos, huevos, aceites y grasas comestibles'),(4634,'4634','Comercio al por mayor de bebidas'),(4635,'4635','Comercio al por mayor de productos del tabaco'),(4636,'4636','Comercio al por mayor de azcar, chocolate y confitera'),(4637,'4637','Comercio al por mayor de caf, t, cacao y especias'),(4638,'4638','Comercio al por mayor de pescados y mariscos y otros productos alimenticios'),(4639,'4639','Comercio al por mayor, no especializado, de productos alimenticios, bebidas y tabaco'),(4641,'4641','Comercio al por mayor de textiles'),(4642,'4642','Comercio al por mayor de prendas de vestir y calzado'),(4643,'4643','Comercio al por mayor de aparatos electrodomsticos'),(4644,'4644','Comercio al por mayor de porcelana, cristalera y artculos de limpieza'),(4645,'4645','Comercio al por mayor de productos perfumera y cosmtica'),(4646,'4646','Comercio al por mayor de productos farmacuticos'),(4647,'4647','Comercio al por mayor de muebles, alfombras y aparatos de iluminacin'),(4648,'4648','Comercio al por mayor de artculos de relojera y joyera'),(4649,'4649','Comercio al por mayor de otros artculos de uso domstico'),(4651,'4651','Comercio al por mayor de ordenadores, equipos perifricos y programas informticos'),(4652,'4652','Comercio al por mayor de equipos electrnicos y de telecomunicaciones y sus componentes'),(4661,'4661','Comercio al por mayor de maquinaria, equipos y suministros agrcolas'),(4662,'4662','Comercio al por mayor de mquinas herramienta'),(4663,'4663','Comercio al por mayor de maquinaria para la minera, la construccin y la ingeniera civil'),(4664,'4664','Comercio al por mayor de maquinaria para la industria textil y de mquinas de coser y tricotar'),(4665,'4665','Comercio al por mayor de muebles de oficina'),(4666,'4666','Comercio al por mayor de otra maquinaria y equipo de oficina'),(4669,'4669','Comercio al por mayor de otra maquinaria y equipo'),(4671,'4671','Comercio al por mayor de combustibles slidos, lquidos y gaseosos, y productos similares'),(4672,'4672','Comercio al por mayor de metales y minerales metlicos'),(4673,'4673','Comercio al por mayor de madera, materiales de construccin y aparatos sanitarios'),(4674,'4674','Comercio al por mayor de ferretera, fontanera y calefaccin'),(4675,'4675','Comercio al por mayor de productos qumicos'),(4676,'4676','Comercio al por mayor de otros productos semielaborados'),(4677,'4677','Comercio al por mayor de chatarra y productos de desecho'),(4690,'4690','Comercio al por mayor no especializado'),(4711,'4711','Comercio al por menor en establecimientos no especializados, con predominio en productos alimenticios, bebidas y tabaco'),(4719,'4719','Otro comercio al por menor en establecimientos no especializados'),(4721,'4721','Comercio al por menor de frutas y hortalizas en establecimientos especializados'),(4722,'4722','Comercio al por menor de carne y productos crnicos en establecimientos especializados'),(4723,'4723','Comercio al por menor de pescados y mariscos en establecimientos especializados'),(4724,'4724','Comercio al por menor de pan y productos de panadera, confitera y pastelera en establecimientos especializados'),(4725,'4725','Comercio al por menor de bebidas en establecimientos especializados'),(4726,'4726','Comercio al por menor de productos de tabaco en establecimientos especializados'),(4729,'4729','Otro comercio al por menor de productos alimenticios en establecimientos especializados'),(4730,'4730','Comercio al por menor de combustible para la automocin en establecimientos especializados'),(4741,'4741','Comercio al por menor de ordenadores, equipos perifricos y programas informticos en establecimientos especializados'),(4742,'4742','Comercio al por menor de equipos de telecomunicaciones en establecimientos especializados'),(4743,'4743','Comercio al por menor de equipos de audio y vdeo en establecimientos especializados'),(4751,'4751','Comercio al por menor de textiles en establecimientos especializados'),(4752,'4752','Comercio al por menor de ferretera, pintura y vidrio en establecimientos especializados'),(4753,'4753','Comercio al por menor de alfombras, moquetas y revestimientos de paredes y suelos en establecimientos especializados'),(4754,'4754','Comercio al por menor de aparatos electrodomsticos en establecimientos especializados'),(4759,'4759','Comercio al por menor de muebles, aparatos de iluminacin y otros artculos de uso domstico en establecimientos especializados'),(4761,'4761','Comercio al por menor de libros en establecimientos especializados'),(4762,'4762','Comercio al por menor de peridicos y artculos de papelera en establecimientos especializados'),(4763,'4763','Comercio al por menor de grabaciones de msica y vdeo en establecimientos especializados'),(4764,'4764','Comercio al por menor de artculos deportivos en establecimientos especializados'),(4765,'4765','Comercio al por menor de juegos y juguetes en establecimientos especializados'),(4771,'4771','Comercio al por menor de prendas de vestir en establecimientos especializados'),(4772,'4772','Comercio al por menor de calzado y artculos de cuero en establecimientos especializados'),(4773,'4773','Comercio al por menor de productos farmacuticos en establecimientos especializados'),(4774,'4774','Comercio al por menor de artculos mdicos y ortopdicos en establecimientos especializados'),(4775,'4775','Comercio al por menor de productos cosmticos e higinicos en establecimientos especializados'),(4776,'4776','Comercio al por menor de flores, plantas, semillas, fertilizantes, animales de compaa y alimentos para los mismos en establecimientos especializados'),(4777,'4777','Comercio al por menor de artculos de relojera y joyera en establecimientos especializados'),(4778,'4778','Otro comercio al por menor de artculos nuevos en establecimientos especializados'),(4779,'4779','Comercio al por menor de artculos de segunda mano en establecimientos'),(4781,'4781','Comercio al por menor de productos alimenticios, bebidas y tabaco en puestos de venta y en mercadillos'),(4782,'4782','Comercio al por menor de productos textiles, prendas de vestir y calzado en puestos de venta y en mercadillos'),(4789,'4789','Comercio al por menor de otros productos en puestos de venta y en mercadillos'),(4791,'4791','Comercio al por menor por correspondencia o Internet'),(4799,'4799','Otro comercio al por menor no realizado ni en establecimientos, ni en puestos de venta ni en mercadillos'),(4910,'4910','Transporte interurbano de pasajeros por ferrocarril'),(4920,'4920','Transporte de mercancas por ferrocarril'),(4931,'4931','Transporte terrestre urbano y suburbano de pasajeros'),(4932,'4932','Transporte por taxi'),(4939,'4939','tipos de transporte terrestre de pasajeros n.c.o.p.'),(4941,'4941','Transporte de mercancas por carretera'),(4942,'4942','Servicios de mudanza'),(4950,'4950','Transporte por tubera'),(5010,'5010','Transporte martimo de pasajeros'),(5020,'5020','Transporte martimo de mercancas'),(5030,'5030','Transporte de pasajeros por vas navegables interiores'),(5040,'5040','Transporte de mercancas por vas navegables interiores'),(5110,'5110','Transporte areo de pasajeros'),(5121,'5121','Transporte areo de mercancas'),(5122,'5122','Transporte espacial'),(5210,'5210','Depsito y almacenamiento'),(5221,'5221','Actividades anexas al transporte terrestre'),(5222,'5222','Actividades anexas al transporte martimo y por vas navegables interiores'),(5223,'5223','Actividades anexas al transporte areo'),(5224,'5224','Manipulacin de mercancas'),(5229,'5229','Otras actividades anexas al transporte'),(5310,'5310','Actividades postales sometidas a la obligacin del servicio universal'),(5320,'5320','Otras actividades postales y de correos'),(5510,'5510','Hoteles y alojamientos similares'),(5520,'5520','Alojamientos tursticos y otros alojamientos de corta estancia'),(5530,'5530','Campings y aparcamientos para caravanas'),(5590,'5590','Otros alojamientos'),(5610,'5610','Restaurantes y puestos de comidas'),(5621,'5621','Provisin de comidas preparadas para eventos'),(5629,'5629','Otros servicios de comidas'),(5630,'5630','Establecimientos de bebidas'),(5811,'5811','Edicin de libros'),(5812,'5812','Edicin de directorios y guas de direcciones postales'),(5813,'5813','Edicin de peridicos'),(5814,'5814','Edicin de revistas'),(5819,'5819','Otras actividades editoriales'),(5821,'5821','Edicin de videojuegos'),(5829,'5829','Edicin de otros programas informticos'),(5912,'5912','Actividades de postproduccin cinematogrfica, de vdeo y de programas de televisin'),(5914,'5914','Actividades de exhibicin cinematogrfica'),(5915,'5915','Actividades de produccin cinematogrfica y de vdeo'),(5916,'5916','Actividades de producciones de programas de televisin'),(5917,'5917','Actividades de distribucin cinematogrfica y de vdeo'),(5918,'5918','Actividades de distribucin de programas de televisin'),(5920,'5920','Actividades de grabacin de sonido y edicin musical'),(6010,'6010','Actividades de radiodifusin'),(6020,'6020','Actividades de programacin y emisin de televisin'),(6110,'6110','Telecomunicaciones por cable'),(6120,'6120','Telecomunicaciones inalmbricas'),(6130,'6130','Telecomunicaciones por satlite'),(6190,'6190','Otras actividades de telecomunicaciones'),(6201,'6201','Actividades de programacin informtica'),(6202,'6202','Actividades de consultora informtica'),(6203,'6203','Gestin de recursos informticos'),(6209,'6209','Otros servicios relacionados con las tecnologas de la informacin y la informtica'),(6311,'6311','Proceso de datos, hosting y actividades relacionadas'),(6312,'6312','Portales web'),(6391,'6391','Actividades de las agencias de noticias'),(6399,'6399','Otros servicios de informacin n.c.o.p.'),(6411,'6411','Banco central'),(6419,'6419','Otra intermediacin monetaria'),(6420,'6420','Actividades de las sociedades holding'),(6430,'6430','Inversin colectiva, fondos y entidades financieras similares'),(6491,'6491','Arrendamiento financiero'),(6492,'6492','Otras actividades crediticias'),(6499,'6499','Otros servicios financieros, excepto seguros y fondos de pensiones n.c.o.p.'),(6511,'6511','Seguros de vida'),(6512,'6512','Seguros distintos de los seguros de vida'),(6520,'6520','Reaseguros'),(6530,'6530','Fondos de pensiones'),(6611,'6611','Administracin de mercados financieros'),(6612,'6612','Actividades de intermediacin en operaciones con valores y otros activos'),(6619,'6619','Otras actividades auxiliares a los servicios financieros, excepto seguros y fondos de pensiones'),(6621,'6621','Evaluacin de riesgos y daos'),(6622,'6622','Actividades de agentes y corredores de seguros'),(6629,'6629','Otras actividades auxiliares a seguros y fondos de pensiones'),(6630,'6630','Actividades de gestin de fondos'),(6810,'6810','Compraventa de bienes inmobiliarios por cuenta propia'),(6820,'6820','Alquiler de bienes inmobiliarios por cuenta propia'),(6831,'6831','Agentes de la propiedad inmobiliaria'),(6832,'6832','Gestin y administracin de la propiedad inmobiliaria'),(6910,'6910','Actividades jurdicas'),(6920,'6920','Actividades de contabilidad, tenedura de libros, auditora y asesora fiscal'),(7010,'7010','Actividades de las sedes centrales'),(7021,'7021','Relaciones pblicas y comunicacin'),(7022,'7022','Otras actividades de consultora de gestin empresarial'),(7111,'7111','Servicios tcnicos de arquitectura'),(7112,'7112','Servicios tcnicos de ingeniera y otras actividades relacionadas con el asesoramiento tcnico'),(7120,'7120','Ensayos y anlisis tcnicos'),(7211,'7211','Investigacin y desarrollo experimental en biotecnologa'),(7219,'7219','Otra investigacin y desarrollo experimental en ciencias naturales y tcnicas'),(7220,'7220','Investigacin y desarrollo experimental en ciencias sociales y humanidades'),(7311,'7311','Agencias de publicidad'),(7312,'7312','Servicios de representacin de medios de comunicacin'),(7320,'7320','Estudio de mercado y realizacin de encuestas de opinin pblica'),(7410,'7410','Actividades de diseo especializado'),(7420,'7420','Actividades de fotografa'),(7430,'7430','Actividades de traduccin e interpretacin'),(7490,'7490','Otras actividades profesionales, cientficas y tcnicas n.c.o.p.'),(7500,'7500','Actividades veterinarias'),(7711,'7711','Alquiler de automviles y vehculos de motor ligeros'),(7712,'7712','Alquiler de camiones'),(7721,'7721','Alquiler de artculos de ocio y deportivos'),(7722,'7722','Alquiler de cintas de vdeo y discos'),(7729,'7729','Alquiler de otros efectos personales y artculos de uso domstico'),(7731,'7731','Alquiler de maquinaria y equipo de uso agrcola'),(7732,'7732','Alquiler de maquinaria y equipo para la construccin e ingeniera civil'),(7733,'7733','Alquiler de maquinaria y equipo de oficina, incluidos ordenadores'),(7734,'7734','Alquiler de medios de navegacin'),(7735,'7735','Alquiler de medios de transporte areo'),(7739,'7739','Alquiler de otra maquinaria, equipos y bienes tangibles n.c.o.p.'),(7740,'7740','Arrendamiento de la propiedad intelectual y productos similares, excepto trabajos protegidos por los derechos de autor'),(7810,'7810','Actividades de las agencias de colocacin'),(7820,'7820','Actividades de las empresas de trabajo temporal'),(7830,'7830','Otra provisin de recursos humanos'),(7911,'7911','Actividades de las agencias de viajes'),(7912,'7912','Actividades de los operadores tursticos'),(7990,'7990','Otros servicios de reservas y actividades relacionadas con los mismos'),(8010,'8010','Actividades de seguridad privada'),(8020,'8020','Servicios de sistemas de seguridad'),(8030,'8030','Actividades de investigacin'),(8110,'8110','Servicios integrales a edificios e instalaciones'),(8121,'8121','Limpieza general de edificios'),(8122,'8122','Otras actividades de limpieza industrial y de edificios'),(8129,'8129','Otras actividades de limpieza'),(8130,'8130','Actividades de jardinera'),(8211,'8211','Servicios administrativos combinados'),(8219,'8219','Actividades de fotocopiado, preparacin de documentos y otras actividades especializadas de oficina'),(8220,'8220','Actividades de los centros de llamadas'),(8230,'8230','Organizacin de convenciones y ferias de muestras'),(8291,'8291','Actividades de las agencias de cobros y de informacin comercial'),(8292,'8292','Actividades de envasado y empaquetado'),(8299,'8299','Otras actividades de apoyo a las empresas n.c.o.p.'),(8411,'8411','Actividades generales de la Administracin Pblica'),(8412,'8412','Regulacin de las actividades sanitarias, educativas y culturales y otros servicios sociales, excepto Seguridad Social'),(8413,'8413','Regulacin de la actividad econmica y contribucin a su mayor eficiencia'),(8421,'8421','Asuntos exteriores'),(8422,'8422','Defensa'),(8423,'8423','Justicia'),(8424,'8424','Orden pblico y seguridad'),(8425,'8425','Proteccin civil'),(8430,'8430','Seguridad Social obligatoria'),(8510,'8510','Educacin preprimaria'),(8520,'8520','Educacin primaria'),(8531,'8531','Educacin secundaria general'),(8532,'8532','Educacin secundaria tcnica y profesional'),(8541,'8541','Educacin postsecundaria no terciaria'),(8543,'8543','Educacin universitaria'),(8544,'8544','Educacin terciaria no universitaria'),(8551,'8551','Educacin deportiva y recreativa'),(8552,'8552','Educacin cultural'),(8553,'8553','Actividades de las escuelas de conduccin y pilotaje'),(8559,'8559','Otra educacin n.c.o.p.'),(8560,'8560','Actividades auxiliares a la educacin'),(8610,'8610','Actividades hospitalarias'),(8621,'8621','Actividades de medicina general'),(8622,'8622','Actividades de medicina especializada'),(8623,'8623','Actividades odontolgicas'),(8690,'8690','Otras actividades sanitarias'),(8710,'8710','Asistencia en establecimientos residenciales con cuidados sanitarios'),(8720,'8720','Asistencia en establecimientos residenciales para personas con discapacidad intelectual, enfermedad mental y drogodependencia'),(8731,'8731','Asistencia en establecimientos residenciales para personas mayores'),(8732,'8732','Asistencia en establecimientos residenciales para personas con discapacidad fsica'),(8790,'8790','Otras actividades de asistencia en establecimientos residenciales'),(8811,'8811','Actividades de servicios sociales sin alojamiento para personas mayores'),(8812,'8812','Actividades de servicios sociales sin alojamiento para personas con discapacidad'),(8891,'8891','Actividades de cuidado diurno de nios'),(8899,'8899','Otros actividades de servicios sociales sin alojamiento n.c.o.p.'),(9001,'9001','Artes escnicas'),(9002,'9002','Actividades auxiliares a las artes escnicas'),(9003,'9003','Creacin artstica y literaria'),(9004,'9004','Gestin de salas de espectculos'),(9102,'9102','Actividades de museos'),(9103,'9103','Gestin de lugares y edificios histricos'),(9104,'9104','Actividades de los jardines botnicos, parques zoolgicos y reservas naturales'),(9105,'9105','Actividades de bibliotecas'),(9106,'9106','Actividades de archivos'),(9200,'9200','Actividades de juegos de azar y apuestas'),(9311,'9311','Gestin de instalaciones deportivas'),(9312,'9312','Actividades de los clubes deportivos'),(9313,'9313','Actividades de los gimnasios'),(9319,'9319','Otras actividades deportivas'),(9321,'9321','Actividades de los parques de atracciones y los parques temticos'),(9329,'9329','Otras actividades recreativas y de entretenimiento'),(9411,'9411','Actividades de organizaciones empresariales y patronales'),(9412,'9412','Actividades de organizaciones profesionales'),(9420,'9420','Actividades sindicales'),(9491,'9491','Actividades de organizaciones religiosas'),(9492,'9492','Actividades de organizaciones polticas'),(9499,'9499','Otras actividades asociativas n.c.o.p.'),(9511,'9511','Reparacin de ordenadores y equipos perifricos'),(9512,'9512','Reparacin de equipos de comunicacin'),(9521,'9521','Reparacin de aparatos electrnicos de audio y vdeo de uso domstico'),(9522,'9522','Reparacin de aparatos electrodomsticos y de equipos para el hogar y el jardn'),(9523,'9523','Reparacin de calzado y artculos de cuero'),(9524,'9524','Reparacin de muebles y artculos de menaje'),(9525,'9525','Reparacin de relojes y joyera'),(9529,'9529','Reparacin de otros efectos personales y artculos de uso domstico'),(9601,'9601','Lavado y limpieza de prendas textiles y de piel'),(9602,'9602','Peluquera y otros tratamientos de belleza'),(9603,'9603','Pompas fnebres y actividades relacionadas'),(9604,'9604','Actividades de mantenimiento fsico'),(9609,'9609','Otras servicios personales n.c.o.p.'),(9700,'9700','Actividades de los hogares como empleadores de personal domstico'),(9810,'9810','Actividades de los hogares como productores de bienes para uso propio'),(9820,'9820','Actividades de los hogares como productores de servicios para uso propio'),(9900,'9900','Actividades de organizaciones y organismos extraterritoriales'),(100001,'01','Agricultura, ganadera, caza y servicios relacionados con las mismas'),(100002,'02','Silvicultura y explotacin forestal'),(100003,'03','Pesca y acuicultura'),(100005,'05','Extraccin de antracita, hulla y lignito'),(100006,'06','Extraccin de crudo de petrleo y gas natural'),(100007,'07','Extraccin de minerales metlicos'),(100008,'08','Otras industrias extractivas'),(100009,'09','Actividades de apoyo a las industrias extractivas'),(1000011,'011','Cultivos no perennes'),(1000012,'012','Cultivos perennes'),(1000013,'013','Propagacin de plantas'),(1000014,'014','Produccin ganadera'),(1000015,'015','Produccin agrcola combinada con la produccin ganadera'),(1000016,'016','Actividades de apoyo a la agricultura, a la ganadera y de preparacin posterior a la cosecha'),(1000017,'017','Caza, captura de animales y servicios relacionados con las mismas'),(1000021,'021','Silvicultura y otras actividades forestales'),(1000022,'022','Explotacin de la madera'),(1000023,'023','Recoleccin de productos silvestres, excepto madera'),(1000024,'024','Servicios de apoyo a la silvicultura'),(1000031,'031','Pesca'),(1000032,'032','Acuicultura'),(1000051,'051','Extraccin de antracita y hulla'),(1000052,'052','Extraccin de lignito'),(1000061,'061','Extraccin de crudo de petrleo'),(1000062,'062','Extraccin de gas natural'),(1000071,'071','Extraccin de minerales de hierro'),(1000072,'072','Extraccin de minerales metlicos no frreos'),(1000081,'081','Extraccin de piedra, arena y arcilla'),(1000089,'089','Industrias extractivas n.c.o.p.'),(1000091,'091','Actividades de apoyo a la extraccin de petrleo y gas natural'),(1000099,'099','Actividades de apoyo a otras industrias extractivas'),(10000111,'0111','Cultivo de cereales (excepto arroz), leguminosas y semillas oleaginosas'),(10000112,'0112','Cultivo de arroz'),(10000113,'0113','Cultivo de hortalizas, races y tubrculos'),(10000114,'0114','Cultivo de caa de azcar'),(10000115,'0115','Cultivo de tabaco'),(10000116,'0116','Cultivo de plantas para fibras textiles'),(10000119,'0119','Otros cultivos no perennes'),(10000121,'0121','Cultivo de la vid'),(10000122,'0122','Cultivo de frutos tropicales y subtropicales'),(10000123,'0123','Cultivo de ctricos'),(10000124,'0124','Cultivo de frutos con hueso y pepitas'),(10000125,'0125','Cultivo de otros rboles y arbustos frutales y frutos secos'),(10000126,'0126','Cultivo de frutos oleaginosos'),(10000127,'0127','Cultivo de plantas para bebidas'),(10000128,'0128','Cultivo de especias, plantas aromticas, medicinales y farmacuticas'),(10000129,'0129','Otros cultivos perennes'),(10000130,'0130','Propagacin de plantas'),(10000141,'0141','Explotacin de ganado bovino para la produccin de leche'),(10000142,'0142','Explotacin de otro ganado bovino y bfalos'),(10000143,'0143','Explotacin de caballos y otros equinos'),(10000144,'0144','Explotacin de camellos y otros camlidos'),(10000145,'0145','Explotacin de ganado ovino y caprino'),(10000146,'0146','Explotacin de ganado porcino'),(10000147,'0147','Avicultura'),(10000149,'0149','Otras explotaciones de ganado'),(10000150,'0150','Produccin agrcola combinada con la produccin ganadera'),(10000161,'0161','Actividades de apoyo a la agricultura'),(10000162,'0162','Actividades de apoyo a la ganadera'),(10000163,'0163','Actividades de preparacin posterior a la cosecha'),(10000164,'0164','Tratamiento de semillas para reproduccin'),(10000170,'0170','Caza, captura de animales y servicios relacionados con las mismas'),(10000210,'0210','Silvicultura y otras actividades forestales'),(10000220,'0220','Explotacin de la madera'),(10000230,'0230','Recoleccin de productos silvestres, excepto madera'),(10000240,'0240','Servicios de apoyo a la silvicultura'),(10000311,'0311','Pesca marina'),(10000312,'0312','Pesca en agua dulce'),(10000321,'0321','Acuicultura marina'),(10000322,'0322','Acuicultura en agua dulce'),(10000510,'0510','Extraccin de antracita y hulla'),(10000520,'0520','Extraccin de lignito'),(10000610,'0610','Extraccin de crudo de petrleo'),(10000620,'0620','Extraccin de gas natural'),(10000710,'0710','Extraccin de minerales de hierro'),(10000721,'0721','Extraccin de minerales de uranio y torio'),(10000729,'0729','Extraccin de otros minerales metlicos no frreos'),(10000811,'0811','Extraccin de piedra ornamental y para la construccin, piedra caliza, yeso, creta y pizarra'),(10000812,'0812','Extraccin de gravas y arenas; extraccin de arcilla y caoln'),(10000891,'0891','Extraccin de minerales para productos qumicos y fertilizantes'),(10000892,'0892','Extraccin de turba'),(10000893,'0893','Extraccin de sal'),(10000899,'0899','Otras industrias extractivas n.c.o.p.'),(10000910,'0910','Actividades de apoyo a la extraccin de petrleo y gas natural'),(10000990,'0990','Actividades de apoyo a otras industrias extractivas');
ALTER TABLE `cnae2009` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `cnae2009_rate`
#

DROP TABLE IF EXISTS `cnae2009_rate`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cnae2009_rate` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador nico',
  `cnae2009` int(4) NOT NULL COMMENT 'CNAE',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `it_amount` double(15,3) default '0.000' COMMENT 'Importe por Incapacidad Temporal (I.T.)',
  `ims_amount` double(15,3) default '0.000' COMMENT 'Importe por Incapacidad Permanente, Muerte y Supervivencia (I.M.S.)',
  PRIMARY KEY  (`id`),
  KEY `IDX_CNAE2009_RATE_CNAE2009` (`cnae2009`),
  CONSTRAINT `FK_CNAE2009_RATE_CNAE2009` FOREIGN KEY (`cnae2009`) REFERENCES `cnae2009` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=152 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas de primas para I.T e I.M.S';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `cnae2009_rate`
#

LOCK TABLES `cnae2009_rate` WRITE;
ALTER TABLE `cnae2009_rate` DISABLE KEYS;
INSERT INTO `cnae2009_rate` VALUES (1,100001,'2010-01-01',NULL,1.500,1.100),(2,10000113,'2010-01-01',NULL,1.150,1.100),(3,10000119,'2010-01-01',NULL,1.150,1.100),(4,10000129,'2010-01-01',NULL,2.250,2.900),(5,10000130,'2010-01-01',NULL,1.150,1.100),(6,1000014,'2010-01-01',NULL,1.800,1.500),(7,10000147,'2010-01-01',NULL,1.200,1.150),(8,1000015,'2010-01-01',NULL,1.600,1.200),(9,1000016,'2010-01-01',NULL,1.600,1.200),(10,10000164,'2010-01-01',NULL,1.150,1.100),(11,1000017,'2010-01-01',NULL,1.800,1.500),(12,100002,'2010-01-01',NULL,2.250,2.900),(13,100003,'2010-01-01',NULL,3.050,3.350),(14,10000322,'2010-01-01',NULL,3.050,3.200),(15,100005,'2010-01-01',NULL,2.300,2.900),(16,100006,'2010-01-01',NULL,2.300,2.900),(17,100007,'2010-01-01',NULL,2.300,2.900),(18,100008,'2010-01-01',NULL,2.300,2.900),(19,10000811,'2010-01-01',NULL,3.450,3.700),(20,100009,'2010-01-01',NULL,2.300,2.900),(21,10,'2010-01-01',NULL,1.600,1.600),(22,101,'2010-01-01',NULL,2.000,1.900),(23,102,'2010-01-01',NULL,1.800,1.500),(24,106,'2010-01-01',NULL,1.700,1.600),(25,107,'2010-01-01',NULL,1.000,0.850),(26,108,'2010-01-01',NULL,1.000,0.850),(27,11,'2010-01-01',NULL,1.600,1.600),(28,12,'2010-01-01',NULL,1.000,0.800),(29,13,'2010-01-01',NULL,1.000,0.850),(30,1391,'2010-01-01',NULL,0.800,0.700),(31,14,'2010-01-01',NULL,0.500,0.400),(32,1411,'2010-01-01',NULL,1.500,1.100),(33,1420,'2010-01-01',NULL,1.500,1.100),(34,143,'2010-01-01',NULL,0.800,0.700),(35,15,'2010-01-01',NULL,1.500,1.100),(36,16,'2010-01-01',NULL,2.250,2.900),(37,1624,'2010-01-01',NULL,2.100,2.000),(38,1629,'2010-01-01',NULL,2.100,2.000),(39,17,'2010-01-01',NULL,1.000,1.050),(40,171,'2010-01-01',NULL,2.000,1.500),(41,18,'2010-01-01',NULL,1.000,1.000),(42,19,'2010-01-01',NULL,1.900,2.550),(43,20,'2010-01-01',NULL,1.600,1.400),(44,204,'2010-01-01',NULL,1.500,1.200),(45,206,'2010-01-01',NULL,1.500,1.200),(46,21,'2010-01-01',NULL,1.300,1.100),(47,22,'2010-01-01',NULL,1.750,1.250),(48,23,'2010-01-01',NULL,2.100,2.000),(49,231,'2010-01-01',NULL,1.600,1.500),(50,232,'2010-01-01',NULL,1.600,1.500),(51,2331,'2010-01-01',NULL,1.600,1.500),(52,234,'2010-01-01',NULL,1.600,1.500),(53,237,'2010-01-01',NULL,2.750,3.350),(54,24,'2010-01-01',NULL,2.000,1.850),(55,25,'2010-01-01',NULL,2.000,1.850),(56,26,'2010-01-01',NULL,1.500,1.100),(57,27,'2010-01-01',NULL,1.600,1.200),(58,28,'2010-01-01',NULL,2.000,1.850),(59,29,'2010-01-01',NULL,1.600,1.200),(60,30,'2010-01-01',NULL,2.000,1.850),(61,3091,'2010-01-01',NULL,1.600,1.200),(62,3092,'2010-01-01',NULL,1.600,1.200),(63,31,'2010-01-01',NULL,2.000,1.850),(64,32,'2010-01-01',NULL,1.600,1.200),(65,321,'2010-01-01',NULL,1.000,0.850),(66,322,'2010-01-01',NULL,1.000,0.850),(67,33,'2010-01-01',NULL,2.000,1.850),(68,3313,'2010-01-01',NULL,1.500,1.100),(69,3314,'2010-01-01',NULL,1.600,1.200),(70,35,'2010-01-01',NULL,1.800,1.500),(71,36,'2010-01-01',NULL,2.100,1.600),(72,37,'2010-01-01',NULL,2.100,1.600),(73,38,'2010-01-01',NULL,2.100,1.600),(74,39,'2010-01-01',NULL,2.100,1.600),(75,41,'2010-01-01',NULL,3.350,3.350),(76,411,'2010-01-01',NULL,0.850,0.800),(77,42,'2010-01-01',NULL,3.350,3.350),(78,43,'2010-01-01',NULL,3.350,3.350),(79,45,'2010-01-01',NULL,1.000,1.000),(80,452,'2010-01-01',NULL,2.450,2.000),(81,454,'2010-01-01',NULL,1.700,1.200),(82,46,'2010-01-01',NULL,1.400,1.200),(83,4623,'2010-01-01',NULL,1.800,1.500),(84,4624,'2010-01-01',NULL,1.800,1.500),(85,4632,'2010-01-01',NULL,1.800,1.500),(86,4638,'2010-01-01',NULL,1.600,1.400),(87,4672,'2010-01-01',NULL,1.800,1.500),(88,4673,'2010-01-01',NULL,1.800,1.500),(89,4674,'2010-01-01',NULL,1.800,1.550),(90,4677,'2010-01-01',NULL,1.800,1.550),(91,4690,'2010-01-01',NULL,1.800,1.550),(92,47,'2010-01-01',NULL,0.950,0.700),(93,473,'2010-01-01',NULL,1.000,0.850),(94,49,'2010-01-01',NULL,1.800,1.500),(95,494,'2010-01-01',NULL,2.000,1.700),(96,50,'2010-01-01',NULL,2.000,1.850),(97,51,'2010-01-01',NULL,1.900,1.700),(98,52,'2010-01-01',NULL,1.800,1.500),(99,5221,'2010-01-01',NULL,1.000,1.100),(100,53,'2010-01-01',NULL,0.950,0.700),(101,55,'2010-01-01',NULL,0.750,0.500),(102,56,'2010-01-01',NULL,0.750,0.500),(103,58,'2010-01-01',NULL,0.650,1.000),(104,59,'2010-01-01',NULL,0.750,0.500),(105,60,'2010-01-01',NULL,0.750,0.500),(106,61,'2010-01-01',NULL,0.700,0.700),(107,62,'2010-01-01',NULL,0.650,1.000),(108,63,'2010-01-01',NULL,0.650,1.000),(109,6391,'2010-01-01',NULL,0.750,0.500),(110,64,'2010-01-01',NULL,0.650,0.350),(111,65,'2010-01-01',NULL,0.650,0.350),(112,66,'2010-01-01',NULL,0.650,0.350),(113,68,'2010-01-01',NULL,0.650,1.000),(114,69,'2010-01-01',NULL,0.650,1.000),(115,70,'2010-01-01',NULL,1.000,0.800),(116,71,'2010-01-01',NULL,0.650,1.000),(117,72,'2010-01-01',NULL,0.650,0.350),(118,73,'2010-01-01',NULL,0.900,0.800),(119,74,'2010-01-01',NULL,0.900,0.850),(120,742,'2010-01-01',NULL,0.500,0.400),(121,75,'2010-01-01',NULL,1.500,1.100),(122,77,'2010-01-01',NULL,1.000,1.000),(123,78,'2010-01-01',NULL,1.550,1.200),(124,781,'2010-01-01',NULL,0.950,1.000),(125,79,'2010-01-01',NULL,0.800,0.700),(126,80,'2010-01-01',NULL,1.400,2.200),(127,81,'2010-01-01',NULL,2.100,1.500),(128,811,'2010-01-01',NULL,1.000,0.850),(129,82,'2010-01-01',NULL,1.000,1.050),(130,8220,'2010-01-01',NULL,0.700,0.700),(131,8292,'2010-01-01',NULL,1.800,1.500),(132,84,'2010-01-01',NULL,0.650,1.000),(133,842,'2010-01-01',NULL,1.400,2.200),(134,85,'2010-01-01',NULL,0.650,0.350),(135,86,'2010-01-01',NULL,0.800,0.700),(136,87,'2010-01-01',NULL,0.800,0.700),(137,88,'2010-01-01',NULL,0.800,0.700),(138,90,'2010-01-01',NULL,0.750,0.500),(139,91,'2010-01-01',NULL,0.750,0.500),(140,9104,'2010-01-01',NULL,1.750,1.200),(141,92,'2010-01-01',NULL,0.750,0.500),(142,93,'2010-01-01',NULL,1.700,1.300),(143,94,'2010-01-01',NULL,0.650,1.000),(144,95,'2010-01-01',NULL,1.500,1.100),(145,9524,'2010-01-01',NULL,2.000,1.850),(146,96,'2010-01-01',NULL,0.800,0.700),(147,9602,'2010-01-01',NULL,0.650,0.450),(148,9603,'2010-01-01',NULL,1.800,1.500),(149,9609,'2010-01-01',NULL,1.500,1.100),(150,97,'2010-01-01',NULL,0.650,0.450),(151,99,'2010-01-01',NULL,1.600,1.500);
ALTER TABLE `cnae2009_rate` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `cno`
#

DROP TABLE IF EXISTS `cno`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cno` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `code` varchar(5) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo del CNO',
  `title` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Titulo del CNO',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9821 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='CNO';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `cno`
#

LOCK TABLES `cno` WRITE;
ALTER TABLE `cno` DISABLE KEYS;
INSERT INTO `cno` VALUES (11,'0011','Oficiales de las fuerzas armadas'),(12,'0012','Suboficiales de las fuerzas armadas'),(20,'0020','Tropa y marinera de las fuerzas armadas'),(1111,'1111','Miembros del poder ejecutivo (nacional, autonmico y local) y del poder legislativo'),(1112,'1112','Personal directivo de la Administracin Pblica'),(1113,'1113','Directores de organizaciones de inters social'),(1120,'1120','Directores generales y presidentes ejecutivos'),(1211,'1211','Directores financieros'),(1212,'1212','Directores de recursos humanos'),(1219,'1219','Directores de polticas y planificacin y de otros departamentos administrativos no clasificados bajo otros epgrafes'),(1221,'1221','Directores comerciales y de ventas'),(1222,'1222','Directores de publicidad y relaciones pblicas'),(1223,'1223','Directores de investigacin y desarrollo'),(1311,'1311','Directores de produccin de explotaciones agropecuarias y forestales'),(1312,'1312','Directores de produccin de explotaciones pesqueras y acucolas'),(1313,'1313','Directores de industrias manufactureras'),(1314,'1314','Directores de explotaciones mineras'),(1315,'1315','Directores de empresas de abastecimiento, transporte, distribucin y afines'),(1316,'1316','Directores de empresas de construccin'),(1321,'1321','Directores de servicios de tecnologas de la informacin y las comunicaciones (TIC)'),(1322,'1322','Directores de servicios sociales para nios'),(1323,'1323','Directores-gerentes de centros sanitarios'),(1324,'1324','Directores de servicios sociales para personas mayores'),(1325,'1325','Directores de otros servicios sociales'),(1326,'1326','Directores de servicios de educacin'),(1327,'1327','Directores de sucursales de bancos, de servicios financieros y de seguros'),(1329,'1329','Directores de otras empresas de servicios profesionales no clasificados bajo otros epgrafes'),(1411,'1411','Directores y gerentes de hoteles'),(1419,'1419','Directores y gerentes de otras empresas de servicios de alojamiento'),(1421,'1421','Directores y gerentes de restaurantes'),(1422,'1422','Directores y gerentes de bares, cafeteras y similares'),(1429,'1429','Directores y gerentes de empresas de catering y otras empresas de restauracin'),(1431,'1431','Directores y gerentes de empresas de comercio al por mayor'),(1432,'1432','Directores y gerentes de empresas de comercio al por menor'),(1501,'1501','Directores y gerentes de empresas de actividades recreativas, culturales y deportivas'),(1509,'1509','Directores y gerentes de empresas de gestin de residuos y de otras empresas de servicios no clasificados bajo otros epgrafes'),(2111,'2111','Mdicos de familia'),(2112,'2112','Otros mdicos especialistas'),(2121,'2121','Enfermeros no especializados'),(2122,'2122','Enfermeros especializados (excepto matronos)'),(2123,'2123','Matronos'),(2130,'2130','Veterinarios'),(2140,'2140','Farmacuticos'),(2151,'2151','Odontlogos y estomatlogos'),(2152,'2152','Fisioterapeutas'),(2153,'2153','Dietistas y nutricionistas'),(2154,'2154','Logopedas'),(2155,'2155','pticos-optometristas'),(2156,'2156','Terapeutas ocupacionales'),(2157,'2157','Podlogos'),(2158,'2158','Profesionales de la salud y la higiene laboral y ambiental'),(2159,'2159','Profesionales de la salud no clasificados bajo otros epgrafes'),(2210,'2210','Profesores de universidades y otra enseanza superior (excepto formacin profesional)'),(2220,'2220','Profesores de formacin profesional (materias especficas)'),(2230,'2230','Profesores de enseanza secundaria (excepto materias especficas de formacin profesional)'),(2240,'2240','Profesores de enseanza primaria'),(2251,'2251','Maestros de educacin infantil'),(2252,'2252','Tcnicos en educacin infantil'),(2311,'2311','Profesores de educacin especial'),(2312,'2312','Tcnicos educadores de educacin especial'),(2321,'2321','Especialistas en mtodos didcticos y pedaggicos'),(2322,'2322','Profesores de enseanza no reglada de idiomas'),(2323,'2323','Profesores de enseanza no reglada de msica y danza'),(2324,'2324','Profesores de enseanza no reglada de artes '),(2325,'2325','Instructores en tecnologas de la informacin en enseanza no reglada'),(2326,'2326','Profesionales de la educacin ambiental'),(2329,'2329','Profesores y profesionales de la enseanza no clasificados bajo otros epgrafes'),(2411,'2411','Fsicos y astrnomos'),(2412,'2412','Meteorlogos'),(2413,'2413','Qumicos'),(2414,'2414','Gelogos y geofsicos'),(2415,'2415','Matemticos y actuarios'),(2416,'2416','Estadsticos'),(2421,'2421','Bilogos, botnicos, zologos y afines'),(2422,'2422','Ingenieros agrnomos'),(2423,'2423','Ingenieros de montes'),(2424,'2424','Ingenieros tcnicos agrcolas'),(2425,'2425','Ingenieros tcnicos forestales y del medio natural'),(2426,'2426','Profesionales de la proteccin ambiental'),(2427,'2427','Enlogos'),(2431,'2431','Ingenieros industriales y de produccin'),(2432,'2432','Ingenieros en construccin y obra civil'),(2433,'2433','Ingenieros mecnicos'),(2434,'2434','Ingenieros aeronuticos'),(2435,'2435','Ingenieros qumicos'),(2436,'2436','Ingenieros de minas, metalrgicos y afines'),(2437,'2437','Ingenieros ambientales'),(2439,'2439','Ingenieros no clasificados bajo otros epgrafes'),(2441,'2441','Ingenieros en electricidad'),(2442,'2442','Ingenieros electrnicos'),(2443,'2443','Ingenieros en telecomunicaciones'),(2451,'2451','Arquitectos (excepto arquitectos paisajistas y urbanistas)'),(2452,'2452','Arquitectos paisajistas'),(2453,'2453','Urbanistas e ingenieros de trfico'),(2454,'2454','Ingenieros gegrafos y cartgrafos'),(2461,'2461','Ingenieros tcnicos industriales y de produccin'),(2462,'2462','Ingenieros tcnicos de obras pblicas'),(2463,'2463','Ingenieros tcnicos mecnicos'),(2464,'2464','Ingenieros tcnicos aeronuticos'),(2465,'2465','Ingenieros tcnicos qumicos'),(2466,'2466','Ingenieros tcnicos de minas, metalrgicos y afines'),(2469,'2469','Ingenieros tcnicos no clasificados bajo otros epgrafes'),(2471,'2471','Ingenieros tcnicos en electricidad'),(2472,'2472','Ingenieros tcnicos en electrnica'),(2473,'2473','Ingenieros tcnicos en telecomunicaciones'),(2481,'2481','Arquitectos tcnicos y tcnicos urbanistas'),(2482,'2482','Diseadores de productos y de prendas'),(2483,'2483','Ingenieros tcnicos en topografa'),(2484,'2484','Diseadores grficos y multimedia'),(2511,'2511','Abogados'),(2512,'2512','Fiscales'),(2513,'2513','Jueces y magistrados'),(2591,'2591','Notarios y registradores'),(2592,'2592','Procuradores'),(2599,'2599','Profesionales del derecho no clasificados bajo otros epgrafes'),(2611,'2611','Especialistas en contabilidad'),(2612,'2612','Asesores financieros y en inversiones'),(2613,'2613','Analistas financieros'),(2621,'2621','Analistas de gestin y organizacin'),(2622,'2622','Especialistas en administracin de poltica de empresas'),(2623,'2623','Especialistas de la Administracin Pblica'),(2624,'2624','Especialistas en polticas y servicios de personal y afines'),(2625,'2625','Especialistas en formacin de personal'),(2630,'2630','Tcnicos de empresas y actividades tursticas'),(2640,'2640','Profesionales de ventas tcnicas y mdicas (excepto las TIC)'),(2651,'2651','Profesionales de la publicidad y la comercializacin'),(2652,'2652','Profesionales de relaciones pblicas'),(2653,'2653','Profesionales de la venta de tecnologas de la informacin y las comunicaciones'),(2711,'2711','Analistas de sistemas'),(2712,'2712','Analistas y diseadores de software'),(2713,'2713','Analistas, programadores y diseadores Web y multimedia'),(2719,'2719','Analistas y diseadores de software y multimedia no clasificados bajo otros epgrafes'),(2721,'2721','Diseadores y administradores de bases de datos'),(2722,'2722','Administradores de sistemas y redes'),(2723,'2723','Analistas de redes informticas'),(2729,'2729','Especialistas en bases de datos y en redes informticas no clasificados bajo otros epgrafes'),(2810,'2810','Economistas'),(2821,'2821','Socilogos, gegrafos, antroplogos, arquelogos y afines'),(2822,'2822','Filsofos, historiadores y profesionales en ciencias polticas'),(2823,'2823','Psiclogos'),(2824,'2824','Profesionales del trabajo y la educacin social'),(2825,'2825','Agentes de igualdad de oportunidades entre mujeres y hombres'),(2830,'2830','Sacerdotes de las distintas religiones'),(2911,'2911','Archivistas y conservadores de museos'),(2912,'2912','Bibliotecarios, documentalistas y afines'),(2921,'2921','Escritores'),(2922,'2922','Periodistas'),(2923,'2923','Fillogos, intrpretes y traductores'),(2931,'2931','Artistas de artes plsticas y visuales'),(2932,'2932','Compositores, msicos y cantantes'),(2933,'2933','Coregrafos y bailarines'),(2934,'2934','Directores de cine, de teatro y afines'),(2935,'2935','Actores'),(2936,'2936','Locutores de radio, televisin y otros presentadores'),(2937,'2937','Profesionales de espectculos taurinos'),(2939,'2939','Artistas creativos e interpretativos no clasificados bajo otros epgrafes'),(3110,'3110','Delineantes y dibujantes tcnicos'),(3121,'3121','Tcnicos en ciencias fsicas y qumicas'),(3122,'3122','Tcnicos en construccin'),(3123,'3123','Tcnicos en electricidad'),(3124,'3124','Tcnicos en electrnica (excepto electro medicina)'),(3125,'3125','Tcnicos en electrnica, especialidad en electro medicina'),(3126,'3126','Tcnicos en mecnica'),(3127,'3127','Tcnicos y analistas de laboratorio en qumica industrial'),(3128,'3128','Tcnicos en metalurgia y minas'),(3129,'3129','Otros tcnicos de las ciencias fsicas, qumicas, medioambientales y de las ingenieras'),(3131,'3131','Tcnicos en instalaciones de produccin de energa'),(3132,'3132','Tcnicos en instalaciones de tratamiento de residuos, de aguas y otros operadores en plantas similares'),(3133,'3133','Tcnicos en control de instalaciones de procesamiento de productos qumicos'),(3134,'3134','Tcnicos de refineras de petrleo y gas natural'),(3135,'3135','Tcnicos en control de procesos de produccin de metales'),(3139,'3139','Tcnicos en control de procesos no clasificados bajo otros epgrafes'),(3141,'3141','Tcnicos en ciencias biolgicas (excepto en reas sanitarias)'),(3142,'3142','Tcnicos agropecuarios'),(3143,'3143','Tcnicos forestales y del medio natural'),(3151,'3151','Jefes y oficiales de mquinas'),(3152,'3152','Capitanes y oficiales de puente'),(3153,'3153','Pilotos de aviacin y profesionales afines'),(3154,'3154','Controladores de trfico areo'),(3155,'3155','Tcnicos en seguridad aeronutica'),(3160,'3160','Tcnicos de control de calidad de las ciencias fsicas, qumicas y de las ingenieras'),(3201,'3201','Supervisores en ingeniera de minas'),(3202,'3202','Supervisores de la construccin'),(3203,'3203','Supervisores de industrias alimenticias y del tabaco'),(3204,'3204','Supervisores de industrias qumica y farmacutica'),(3205,'3205','Supervisores de industrias de transformacin de plsticos, caucho y resinas naturales'),(3206,'3206','Supervisores de industrias de la madera y pastero papeleras'),(3207,'3207','Supervisores de la produccin en industrias de artes grficas y en la fabricacin de productos de papel'),(3209,'3209','Supervisores de otras industrias manufactureras'),(3311,'3311','Tcnicos en radioterapia'),(3312,'3312','Tcnicos en imagen para el diagnstico'),(3313,'3313','Tcnicos en anatoma patolgica y citologa'),(3314,'3314','Tcnicos en laboratorio de diagnstico clnico'),(3315,'3315','Tcnicos en ortoprtesis'),(3316,'3316','Tcnicos en prtesis dentales'),(3317,'3317','Tcnicos en audioprtesis'),(3321,'3321','Tcnicos superiores en higiene bucodental'),(3322,'3322','Tcnicos superiores en documentacin sanitaria'),(3323,'3323','Tcnicos superiores en diettica'),(3324,'3324','Tcnicos en optometra'),(3325,'3325','Ayudantes fisioterapeutas'),(3326,'3326','Tcnicos en prevencin de riesgos laborales y salud ambiental'),(3327,'3327','Ayudantes de veterinaria'),(3329,'3329','Tcnicos de la sanidad no clasificados bajo otros epgrafes'),(3331,'3331','Profesionales de la acupuntura, la naturopata, la homeopata, la medicina tradicional china y la ayurveda'),(3339,'3339','Otros profesionales de las terapias alternativas'),(3401,'3401','Profesionales de apoyo e intermediarios de cambio, bolsa y finanzas'),(3402,'3402','Comerciales de prstamos y crditos'),(3403,'3403','Tenedores de libros'),(3404,'3404','Profesionales de apoyo en servicios estadsticos, matemticos y afines'),(3405,'3405','Tasadores'),(3510,'3510','Agentes y representantes comerciales'),(3521,'3521','Mediadores y agentes de seguros'),(3522,'3522','Agentes de compras'),(3523,'3523','Consignatarios'),(3531,'3531','Representantes de aduanas'),(3532,'3532','Organizadores de conferencias y eventos'),(3533,'3533','Agentes o intermediarios en la contratacin de la mano de obra (excepto representantes de espectculos)'),(3534,'3534','Agentes y administradores de la propiedad inmobiliaria'),(3535,'3535','Portavoces y agentes de relaciones pblicas'),(3539,'3539','Representantes artsticos y deportivos y otros agentes de servicios comerciales no clasificados bajo otros epgrafes'),(3611,'3611','Supervisores de secretara'),(3612,'3612','Asistentes jurdico-legales'),(3613,'3613','Asistentes de direccin y administrativos'),(3614,'3614','Secretarios de centros mdicos o clnicas'),(3621,'3621','Profesionales de apoyo de la Administracin Pblica de tributos'),(3622,'3622','Profesionales de apoyo de la Administracin Pblica de servicios sociales'),(3623,'3623','Profesionales de apoyo de la Administracin Pblica de servicios de expedicin de licencias'),(3629,'3629','Otros profesionales de apoyo de la Administracin Pblica para tareas de inspeccin y control y tareas similares'),(3631,'3631','Tcnicos de la polica nacional, autonmica y local'),(3632,'3632','Suboficiales de la guardia civil'),(3711,'3711','Profesionales de apoyo de servicios jurdicos y servicios similares'),(3712,'3712','Detectives privados'),(3713,'3713','Profesionales de apoyo al trabajo y a la educacin social'),(3714,'3714','Promotores de igualdad de oportunidades entre mujeres y hombres'),(3715,'3715','Animadores comunitarios'),(3716,'3716','Auxiliares laicos de las religiones'),(3721,'3721','Atletas y deportistas'),(3722,'3722','Entrenadores y rbitros de actividades deportivas'),(3723,'3723','Instructores de actividades deportivas'),(3724,'3724','Monitores de actividades recreativas y de entretenimiento'),(3731,'3731','Fotgrafos'),(3732,'3732','Diseadores y decoradores de interior'),(3733,'3733','Tcnicos en galeras de arte, museos y bibliotecas'),(3734,'3734','Chefs'),(3739,'3739','Otros tcnicos y profesionales de apoyo de actividades culturales y artsticas'),(3811,'3811','Tcnicos en operaciones de sistemas informticos'),(3812,'3812','Tcnicos en asistencia al usuario de tecnologas de la informacin'),(3813,'3813','Tcnicos en redes'),(3814,'3814','Tcnicos de la Web'),(3820,'3820','Programadores informticos'),(3831,'3831','Tcnicos de grabacin audiovisual'),(3832,'3832','Tcnicos de radiodifusin'),(3833,'3833','Tcnicos de ingeniera de las telecomunicaciones'),(4111,'4111','Empleados de contabilidad'),(4112,'4112','Empleados de control de personal y nminas'),(4113,'4113','Empleados de oficina de servicios estadsticos, financieros y bancarios'),(4121,'4121','Empleados de control de abastecimientos e inventario'),(4122,'4122','Empleados de oficina de servicios de apoyo a la produccin'),(4123,'4123','Empleados de logstica y transporte de pasajeros y mercancas'),(4210,'4210','Empleados de bibliotecas y archivos'),(4221,'4221','Empleados de servicios de correos (excepto empleados de mostrador)'),(4222,'4222','Codificadores y correctores de imprenta'),(4223,'4223','Empleados de servicio de personal'),(4301,'4301','Grabadores de datos'),(4309,'4309','Empleados administrativos sin tareas de atencin al pblico no clasificados bajo otros epgrafes'),(4411,'4411','Empleados de informacin al usuario'),(4412,'4412','Recepcionistas (excepto de hoteles)'),(4421,'4421','Empleados de agencias de viajes'),(4422,'4422','Recepcionistas de hoteles'),(4423,'4423','Telefonistas'),(4424,'4424','Teleoperadores'),(4430,'4430','Agentes de encuestas'),(4441,'4441','Cajeros de bancos y afines'),(4442,'4442','Empleados de venta de apuestas'),(4443,'4443','Empleados de sala de juegos y afines'),(4444,'4444','Empleados de casas de empeo y de prstamos'),(4445,'4445','Cobradores de facturas, deudas y empleados afines'),(4446,'4446','Empleados de mostrador de correos'),(4500,'4500','Empleados administrativos con tareas de atencin al pblico no clasificados bajo otros epgrafes'),(5000,'5000','Camareros y cocineros propietarios'),(5110,'5110','Cocineros asalariados'),(5120,'5120','Camareros asalariados'),(5210,'5210','Jefes de seccin de tiendas y almacenes'),(5220,'5220','Vendedores en tiendas y almacenes'),(5300,'5300','Comerciantes propietarios de tiendas'),(5411,'5411','Vendedores en quioscos'),(5412,'5412','Vendedores en mercados ocasionales y mercadillos'),(5420,'5420','Operadores de telemarketing'),(5430,'5430','Expendedores de gasolineras'),(5491,'5491','Vendedores a domicilio'),(5492,'5492','Promotores de venta'),(5493,'5493','Modelos de moda, arte y publicidad'),(5499,'5499','Vendedores no clasificados bajo otros epgrafes'),(5500,'5500','Cajeros y taquilleros (excepto bancos)'),(5611,'5611','Auxiliares de enfermera hospitalaria'),(5612,'5612','Auxiliares de enfermera de atencin primaria'),(5621,'5621','Tcnicos auxiliares de farmacia'),(5622,'5622','Tcnicos de emergencias sanitarias'),(5629,'5629','Trabajadores de los cuidados a las personas en servicios de salud no clasificados bajo otros epgrafes'),(5710,'5710','Trabajadores de los cuidados personales a domicilio'),(5721,'5721','Cuidadores de nios en guarderas y centros educativos'),(5722,'5722','Cuidadores de nios en domicilios'),(5811,'5811','Peluqueros'),(5812,'5812','Especialistas en tratamientos de esttica, bienestar y afines'),(5821,'5821','Auxiliares de vuelo y camareros de avin, barco y tren'),(5822,'5822','Revisores y cobradores de transporte terrestre'),(5823,'5823','Acompaantes tursticos'),(5824,'5824','Azafatos de tierra'),(5825,'5825','Guas de turismo'),(5831,'5831','Supervisores de mantenimiento y limpieza en oficinas, hoteles y otros establecimientos'),(5832,'5832','Mayordomos del servicio domstico'),(5833,'5833','Conserjes de edificios'),(5840,'5840','Trabajadores propietarios de pequeos alojamientos'),(5891,'5891','Asistentes personales o personas de compaa'),(5892,'5892','Empleados de pompas fnebres y embalsamadores'),(5893,'5893','Cuidadores de animales y adiestradores'),(5894,'5894','Instructores de autoescuela'),(5895,'5895','Astrlogos, adivinadores y afines'),(5899,'5899','Trabajadores de servicios personales no clasificados bajo otros epgrafes'),(5910,'5910','Guardias civiles'),(5921,'5921','Policas nacionales'),(5922,'5922','Policas autonmicos'),(5923,'5923','Policas locales'),(5931,'5931','Bomberos (excepto forestales)'),(5932,'5932','Bomberos forestales'),(5941,'5941','Vigilantes de seguridad y similares habilitados para ir armados'),(5942,'5942','Auxiliares de vigilante de seguridad y similares no habilitados para ir armados'),(5991,'5991','Vigilantes de prisiones'),(5992,'5992','Baistas-socorristas'),(5993,'5993','Agentes forestales y medioambientales'),(5999,'5999','Trabajadores de los servicios de proteccin y seguridad no clasificados bajo otros epgrafes'),(6110,'6110','Trabajadores cualificados en actividades agrcolas (excepto en huertas, invernaderos, viveros y jardines)'),(6120,'6120','Trabajadores cualificados en huertas, invernaderos, viveros y jardines'),(6201,'6201','Trabajadores cualificados en actividades ganaderas de vacuno'),(6202,'6202','Trabajadores cualificados en actividades ganaderas de ovino y caprino'),(6203,'6203','Trabajadores cualificados en actividades ganaderas de porcino'),(6204,'6204','Trabajadores cualificados en apicultura y sericicultura'),(6205,'6205','Trabajadores cualificados en la avicultura y la cunicultura'),(6209,'6209','Trabajadores cualificados en actividades ganaderas no clasificados bajo otros epgrafes'),(6300,'6300','Trabajadores cualificados en actividades agropecuarias mixtas'),(6410,'6410','Trabajadores cualificados en actividades forestales y del medio natural'),(6421,'6421','Trabajadores cualificados en la acuicultura'),(6422,'6422','Pescadores de aguas costeras y aguas dulces'),(6423,'6423','Pescadores de altura'),(6430,'6430','Trabajadores cualificados en actividades cinegticas'),(7111,'7111','Encofradores y operarios de puesta en obra de hormign'),(7112,'7112','Montadores de prefabricados estructurales (slo hormign)'),(7121,'7121','Albailes'),(7122,'7122','Canteros, tronzadores, labrantes y grabadores de piedras'),(7131,'7131','Carpinteros (excepto ebanistas)'),(7132,'7132','Instaladores de cerramientos metlicos y carpinteros metlicos (excepto montadores de estructuras metlicas)'),(7191,'7191','Mantenedores de edificios'),(7192,'7192','Instaladores de fachadas tcnicas'),(7193,'7193','Instaladores de sistemas de impermeabilizacin en edificios'),(7199,'7199','Otros trabajadores de las obras estructurales de construccin no clasificados bajo otros epgrafes'),(7211,'7211','Escayolistas'),(7212,'7212','Aplicadores de revestimientos de pasta y mortero'),(7221,'7221','Fontaneros'),(7222,'7222','Montadores-instaladores de gas en edificios'),(7223,'7223','Instaladores de conductos en obra pblica'),(7231,'7231','Pintores y empapeladores'),(7232,'7232','Pintores en las industrias manufactureras'),(7240,'7240','Soladores, colocadores de parquet y afines'),(7250,'7250','Mecnicos-instaladores de refrigeracin y climatizacin'),(7291,'7291','Montadores de cubiertas'),(7292,'7292','Instaladores de material aislante trmico y de insonorizacin'),(7293,'7293','Cristaleros'),(7294,'7294','Montadores-instaladores de placas de energa solar'),(7295,'7295','Personal de limpieza de fachadas de edificios y chimeneas'),(7311,'7311','Moldeadores y macheros'),(7312,'7312','Soldadores y oxicortadores'),(7313,'7313','Chapistas y caldereros'),(7314,'7314','Montadores de estructuras metlicas'),(7315,'7315','Montadores de estructuras cableadas y empalmadores de cables'),(7321,'7321','Herreros y forjadores'),(7322,'7322','Trabajadores de la fabricacin de herramientas, mecnico-ajustadores, modelistas, matriceros y afines'),(7323,'7323','Ajustadores y operadores de mquinas-herramienta'),(7324,'7324','Pulidores de metales y afiladores de herramientas'),(7401,'7401','Mecnicos y ajustadores de vehculos de motor'),(7402,'7402','Mecnicos y ajustadores de motores de avin'),(7403,'7403','Mecnicos y ajustadores de maquinaria agrcola e industrial'),(7404,'7404','Mecnicos y ajustadores de maquinaria naval y ferroviaria'),(7405,'7405','Reparadores de bicicletas y afines'),(7510,'7510','Electricistas de la construccin y afines'),(7521,'7521','Mecnicos y reparadores de equipos elctricos'),(7522,'7522','Instaladores y reparadores de lneas elctricas'),(7531,'7531','Mecnicos y reparadores de equipos electrnicos'),(7532,'7532','Instaladores y reparadores en electro medicina'),(7533,'7533','Instaladores y reparadores en tecnologas de la informacin y las comunicaciones'),(7611,'7611','Relojeros y mecnicos de instrumentos de precisin'),(7612,'7612','Lutieres y similares; afinadores de instrumentos musicales'),(7613,'7613','Joyeros, orfebres y plateros'),(7614,'7614','Trabajadores de la cermica, alfareros y afines'),(7615,'7615','Sopladores, modeladores, laminadores, cortadores y pulidores de vidrio'),(7616,'7616','Rotulistas, grabadores de vidrio, pintores decorativos de artculos diversos'),(7617,'7617','Artesanos en madera y materiales similares; cesteros, bruceros y trabajadores afines'),(7618,'7618','Artesanos en tejidos, cueros y materiales similares, preparadores de fibra y tejedores con telares artesanos o de tejidos de punto y afines'),(7619,'7619','Artesanos no clasificados bajo otros epgrafes'),(7621,'7621','Trabajadores de procesos de preimpresin'),(7622,'7622','Trabajadores de procesos de impresin'),(7623,'7623','Trabajadores de procesos de encuadernacin'),(7701,'7701','Matarifes y trabajadores de las industrias crnicas'),(7702,'7702','Trabajadores de las industrias del pescado'),(7703,'7703','Panaderos, pasteleros y confiteros'),(7704,'7704','Trabajadores del tratamiento de la leche y elaboracin de productos lcteos (incluidos helados)'),(7705,'7705','Trabajadores conserveros de frutas y hortalizas y trabajadores de la elaboracin de bebidas no alcohlicas'),(7706,'7706','Trabajadores de la elaboracin de bebidas alcohlicas distintas del vino'),(7707,'7707','Trabajadores de la elaboracin del vino'),(7708,'7708','Preparadores y elaboradores del tabaco y sus productos'),(7709,'7709','Catadores y clasificadores de alimentos y bebidas'),(7811,'7811','Trabajadores del tratamiento de la madera'),(7812,'7812','Ajustadores y operadores de mquinas para trabajar la madera'),(7820,'7820','Ebanistas y trabajadores afines'),(7831,'7831','Sastres, modistos, peleteros y sombrereros'),(7832,'7832','Patronistas para productos en textil y piel'),(7833,'7833','Cortadores de tejidos, cuero, piel y otros materiales'),(7834,'7834','Costureros a mano, bordadores y afines'),(7835,'7835','Tapiceros, colchoneros y afines'),(7836,'7836','Curtidores y preparadores de pieles'),(7837,'7837','Zapateros y afines'),(7891,'7891','Buceadores'),(7892,'7892','Pegadores'),(7893,'7893','Clasificadores y probadores de productos (excepto alimentos, bebidas y tabaco)'),(7894,'7894','Fumigadores y otros controladores de plagas y malas hierbas'),(7899,'7899','Oficiales, operarios y artesanos de otros oficios no clasificados bajo otros epgrafes'),(8111,'8111','Mineros y otros operadores en instalaciones mineras'),(8112,'8112','Operadores en instalaciones para la preparacin de minerales y rocas'),(8113,'8113','Sondistas y trabajadores afines'),(8114,'8114','Operadores de maquinaria para fabricar productos derivados de minerales no metlicos'),(8121,'8121','Operadores en instalaciones para la obtencin y transformacin de metales'),(8122,'8122','Operadores de mquinas pulidoras, galvanizadoras y recubridoras de metales'),(8131,'8131','Operadores en plantas industriales qumicas'),(8132,'8132','Operadores de mquinas para fabricar productos farmacuticos, cosmticos y afines'),(8133,'8133','Operadores de laboratorios fotogrficos y afines'),(8141,'8141','Operadores de mquinas para fabricar productos de caucho y derivados de resinas naturales'),(8142,'8142','Operadores de mquinas para fabricar productos de material plstico'),(8143,'8143','Operadores de mquinas para fabricar productos de papel y cartn'),(8144,'8144','Operadores de serreras, de mquinas de fabricacin de tableros y de instalaciones afines para el tratamiento de la madera y el corcho'),(8145,'8145','Operadores en instalaciones para la preparacin de pasta de papel y fabricacin de papel'),(8151,'8151','Operadores de mquinas para preparar fibras, hilar y devanar'),(8152,'8152','Operadores de telares y otras mquinas tejedoras'),(8153,'8153','Operadores de mquinas de coser y bordar'),(8154,'8154','Operadores de mquinas de blanquear, teir, estampar y acabar textiles'),(8155,'8155','Operadores de mquinas para tratar pieles y cuero'),(8156,'8156','Operadores de mquinas para la fabricacin del calzado, marroquinera y guantera de piel'),(8159,'8159','Operadores de mquinas para fabricar productos textiles no clasificados bajo otros epgrafes'),(8160,'8160','Operadores de mquinas para elaborar productos alimenticios, bebidas y tabaco'),(8170,'8170','Operadores de mquinas de lavandera y tintorera'),(8191,'8191','Operadores de hornos e instalaciones de vidriera y cermica'),(8192,'8192','Operadores de calderas y mquinas de vapor'),(8193,'8193','Operadores de mquinas de embalaje, embotellamiento y etiquetado'),(8199,'8199','Operadores de instalaciones y maquinaria fijas no clasificados bajo otros epgrafes'),(8201,'8201','Ensambladores de maquinaria mecnica'),(8202,'8202','Ensambladores de equipos elctricos y electrnicos'),(8209,'8209','Montadores y ensambladores no clasificados en otros epgrafes'),(8311,'8311','Maquinistas de locomotoras'),(8312,'8312','Agentes de maniobras ferroviarias'),(8321,'8321','Operadores de maquinaria agrcola mvil'),(8322,'8322','Operadores de maquinaria forestal mvil'),(8331,'8331','Operadores de maquinaria de movimientos de tierras y equipos similares'),(8332,'8332','Operadores de gras, montacargas y de maquinaria similar de movimiento de materiales'),(8333,'8333','Operadores de carretillas elevadoras'),(8340,'8340','Marineros de puente, marineros de mquinas y afines'),(8411,'8411','Conductores propietarios de automviles, taxis y furgonetas'),(8412,'8412','Conductores asalariados de automviles, taxis y furgonetas'),(8420,'8420','Conductores de autobuses y tranvas'),(8431,'8431','Conductores propietarios de camiones'),(8432,'8432','Conductores asalariados de camiones'),(8440,'8440','Conductores de motocicletas y ciclomotores'),(9100,'9100','Empleados domsticos'),(9210,'9210','Personal de limpieza de oficinas, hoteles y otros establecimientos similares'),(9221,'9221','Limpiadores en seco a mano y afines'),(9222,'9222','Limpiadores de vehculos'),(9223,'9223','Limpiadores de ventanas'),(9229,'9229','Otro personal de limpieza'),(9310,'9310','Ayudantes de cocina'),(9320,'9320','Preparadores de comidas rpidas'),(9410,'9410','Vendedores callejeros'),(9420,'9420','Repartidores de publicidad, limpiabotas y otros trabajadores de oficios callejeros'),(9431,'9431','Ordenanzas'),(9432,'9432','Mozos de equipaje y afines'),(9433,'9433','Repartidores, recadistas y mensajeros a pie'),(9434,'9434','Lectores de contadores y recaudadores de mquinas recreativas y expendedoras'),(9441,'9441','Recogedores de residuos'),(9442,'9442','Clasificadores de desechos, operarios de punto limpio y recogedores de chatarra'),(9443,'9443','Barrenderos y afines'),(9490,'9490','Otras ocupaciones elementales'),(9511,'9511','Peones agrcolas (excepto en huertas, invernaderos, viveros y jardines)'),(9512,'9512','Peones agrcolas en huertas, invernaderos, viveros y jardines'),(9520,'9520','Peones ganaderos'),(9530,'9530','Peones agropecuarios'),(9541,'9541','Peones de la pesca'),(9542,'9542','Peones de la acuicultura'),(9543,'9543','Peones forestales y de la caza'),(9601,'9601','Peones de obras pblicas'),(9602,'9602','Peones de la construccin de edificios'),(9603,'9603','Peones de la minera, canteras y otras industrias extractivas'),(9700,'9700','Peones de las industrias manufactureras'),(9811,'9811','Peones del transporte de mercancas y descargadores'),(9812,'9812','Conductores de vehculos de traccin animal para el transporte de personas y similares'),(9820,'9820','Reponedores');
ALTER TABLE `cno` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `commercial_activity`
#

DROP TABLE IF EXISTS `commercial_activity`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `commercial_activity` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Actividad Comercial',
  PRIMARY KEY  (`id`),
  KEY `IDX_COMMERCIAL_ACTIVITY_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMMERCIAL_ACTIVITY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades Comerciales';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `commercial_activity`
#

LOCK TABLES `commercial_activity` WRITE;
ALTER TABLE `commercial_activity` DISABLE KEYS;
ALTER TABLE `commercial_activity` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `commercial_term`
#

DROP TABLE IF EXISTS `commercial_term`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `commercial_term` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `line` smallint(2) default '1' COMMENT 'Numero de linea de Condicion',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Nombre de la Condicion Comercial',
  `description` text collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Condicion Comercial',
  `term_general` tinyint(1) default '0' COMMENT 'Indica si la Condicin es particular o general',
  PRIMARY KEY  (`id`),
  KEY `IDX_COMMERCIAL_TERM_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMMERCIAL_TERM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Condiciones Comerciales';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `commercial_term`
#

LOCK TABLES `commercial_term` WRITE;
ALTER TABLE `commercial_term` DISABLE KEYS;
ALTER TABLE `commercial_term` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `commercial_tracking`
#

DROP TABLE IF EXISTS `commercial_tracking`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `commercial_tracking` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_COMMERCIAL_TRACKING_ACTIVITY` (`activity`),
  KEY `IDX_COMMERCIAL_TRACKING_OFFER` (`offer`),
  KEY `IDX_COMMERCIAL_TRACKING_PROJECT_COMMERCIAL` (`project`),
  KEY `IDX_COMMERCIAL_TRACKING_NEXT_COMMERCIAL_TRACKING` (`next_commercial_tracking`),
  KEY `IDX_COMMERCIAL_TRACKING_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_ACTIVITY` FOREIGN KEY (`activity`) REFERENCES `commercial_activity` (`id`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_NEXT_COMMERCIAL_TRACKING` FOREIGN KEY (`next_commercial_tracking`) REFERENCES `commercial_tracking` (`id`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_PROJECT_COMMERCIAL` FOREIGN KEY (`project`) REFERENCES `project_commercial` (`project`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Seguimientos Comerciales';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `commercial_tracking`
#

LOCK TABLES `commercial_tracking` WRITE;
ALTER TABLE `commercial_tracking` DISABLE KEYS;
ALTER TABLE `commercial_tracking` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `commission`
#

DROP TABLE IF EXISTS `commission`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `commission` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Comision',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio de la Comision',
  `end_date` date default NULL COMMENT 'Fecha de fin de la Comision',
  PRIMARY KEY  (`id`),
  KEY `IDX_COMMISSION_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMMISSION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `commission`
#

LOCK TABLES `commission` WRITE;
ALTER TABLE `commission` DISABLE KEYS;
ALTER TABLE `commission` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `commission_category`
#

DROP TABLE IF EXISTS `commission_category`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `commission_category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `commission` int(4) NOT NULL COMMENT 'Identificador de la Comision',
  `category` int(4) NOT NULL COMMENT 'Identificador de la Categoria',
  `quantity` double default '0' COMMENT 'Cantidad a partir de la cual se aplica la Comision',
  `rate` double(6,2) default '0.00' COMMENT 'Porcentaje de Comision',
  PRIMARY KEY  (`id`),
  KEY `IDX_COMMISSION_CATEGORY_COMMISSION` (`commission`),
  KEY `IDX_COMMISSION_CATEGORY_CATEGORY` (`category`),
  KEY `IDX_COMMISSION_CATEGORY_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMMISSION_CATEGORY_CATEGORY` FOREIGN KEY (`category`) REFERENCES `pcategory` (`id`),
  CONSTRAINT `FK_COMMISSION_CATEGORY_COMMISSION` FOREIGN KEY (`commission`) REFERENCES `commission` (`id`),
  CONSTRAINT `FK_COMMISSION_CATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones por Categoria';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `commission_category`
#

LOCK TABLES `commission_category` WRITE;
ALTER TABLE `commission_category` DISABLE KEYS;
ALTER TABLE `commission_category` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `commission_item`
#

DROP TABLE IF EXISTS `commission_item`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `commission_item` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `commission` int(4) NOT NULL COMMENT 'Identificador de la Comision',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo',
  `quantity` double default '0' COMMENT 'Cantidad a partir de la cual se aplica la Comision',
  `amount` double default '0' COMMENT 'Importe de la Comision',
  `rate` double(6,2) default '0.00' COMMENT 'Porcentaje de Comision',
  PRIMARY KEY  (`id`),
  KEY `IDX_COMMISSION_ITEM_COMMISSION` (`commission`),
  KEY `IDX_COMMISSION_ITEM_ITEM` (`item`),
  KEY `IDX_COMMISSION_ITEM_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMMISSION_ITEM_COMMISSION` FOREIGN KEY (`commission`) REFERENCES `commission` (`id`),
  CONSTRAINT `FK_COMMISSION_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_COMMISSION_ITEM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones por Articulo';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `commission_item`
#

LOCK TABLES `commission_item` WRITE;
ALTER TABLE `commission_item` DISABLE KEYS;
ALTER TABLE `commission_item` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `commission_type`
#

DROP TABLE IF EXISTS `commission_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `commission_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Comision',
  `rate` double(6,2) default '0.00' COMMENT 'Porcentaje de Comision',
  PRIMARY KEY  (`id`),
  KEY `IDX_COMMISSION_TYPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMMISSION_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Comisiones';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `commission_type`
#

LOCK TABLES `commission_type` WRITE;
ALTER TABLE `commission_type` DISABLE KEYS;
ALTER TABLE `commission_type` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `commission_type_commission`
#

DROP TABLE IF EXISTS `commission_type_commission`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `commission_type_commission` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `commission_type` int(4) NOT NULL COMMENT 'Identificador del Tipo de Comision',
  `commission` int(4) NOT NULL COMMENT 'Identificador de la Comision',
  PRIMARY KEY  (`id`),
  KEY `IDX_COMMISSION_TYPE_COMMISSION_COMMISSION_TYPE` (`commission_type`),
  KEY `IDX_COMMISSION_TYPE_COMMISSION_COMMISSION` (`commission`),
  KEY `IDX_COMMISSION_TYPE_COMMISSION_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMMISSION_TYPE_COMMISSION_COMMISSION` FOREIGN KEY (`commission`) REFERENCES `commission` (`id`),
  CONSTRAINT `FK_COMMISSION_TYPE_COMMISSION_COMMISSION_TYPE` FOREIGN KEY (`commission_type`) REFERENCES `commission_type` (`id`),
  CONSTRAINT `FK_COMMISSION_TYPE_COMMISSION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones por Tipo de Comision';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `commission_type_commission`
#

LOCK TABLES `commission_type_commission` WRITE;
ALTER TABLE `commission_type_commission` DISABLE KEYS;
ALTER TABLE `commission_type_commission` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `company`
#

DROP TABLE IF EXISTS `company`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `company` (
  `registry` int(4) NOT NULL default '1' COMMENT 'Registro de la Compaia',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `active` tinyint(1) default '0' COMMENT 'Indica si la Compaia es activa o inactiva',
  `surcharge` tinyint(1) default '0' COMMENT 'Indica si la Compaia tiene de recargo de equivalencia',
  `withholding` tinyint(1) default '0' COMMENT 'Indica si la Compaia aplica retencion de impuestos',
  `e_invoice` tinyint(1) default '0' COMMENT 'Indica si la Compaia desea emitir Facturas electronicas',
  PRIMARY KEY  (`registry`),
  KEY `IDX_COMPANY_DOMAIN` (`domain`),
  CONSTRAINT `FK_COMPANY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_COMPANY_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos Corporativos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `company`
#

LOCK TABLES `company` WRITE;
ALTER TABLE `company` DISABLE KEYS;
INSERT INTO `company` VALUES (1,1,0,0,0,0);
ALTER TABLE `company` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `contract`
#

DROP TABLE IF EXISTS `contract`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `contract` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `person` int(4) NOT NULL COMMENT 'Identificador de la Persona',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `enterprise_ccc` int(4) default NULL COMMENT 'CCC',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio del Contrato',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion del Contrato',
  `calendar` int(4) default NULL COMMENT 'Calendario',
  `document` mediumblob COMMENT 'Impreso (.pdf) del contrato.',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `status` tinyint(2) default '0' COMMENT 'Estado de notificacion del contrato',
  `registration` int(4) default NULL COMMENT 'Nmero libro de matricula',
  `seniority_date` date default NULL COMMENT 'Fecha de antiguedad',
  `enterprise_activity` int(4) default NULL COMMENT 'Actividad',
  `ss_regime` tinyint(2) NOT NULL default '0' COMMENT 'Regimen de la Seguridad Social',
  `agreement_level_category` int(4) default NULL COMMENT 'Identificador unico de la Categoria Profesional',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_PERSON` (`person`),
  KEY `IDX_CONTRACT_WORKPLACE` (`workplace`),
  KEY `IDX_CONTRACT_CALENDAR` (`calendar`),
  KEY `IDX_CONTRACT_ENTERPRISE_CCC` (`enterprise_ccc`),
  KEY `IDX_CONTRACT_AGREEMENT_LEVEL_CATEGORY` (`agreement_level_category`),
  KEY `IDX_CONTRACT_ENTERPRISE_ACTIVITY` (`enterprise_activity`),
  KEY `IDX_CONTRACT_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_AGREEMENT_LEVEL_CATEGORY` FOREIGN KEY (`agreement_level_category`) REFERENCES `agreement_level_category` (`id`),
  CONSTRAINT `FK_CONTRACT_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_CONTRACT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CONTRACT_ENTERPRISE_ACTIVITY` FOREIGN KEY (`enterprise_activity`) REFERENCES `enterprise_activity` (`id`),
  CONSTRAINT `FK_CONTRACT_ENTERPRISE_CCC` FOREIGN KEY (`enterprise_ccc`) REFERENCES `enterprise_ccc` (`id`),
  CONSTRAINT `FK_CONTRACT_PERSON` FOREIGN KEY (`person`) REFERENCES `person` (`registry`),
  CONSTRAINT `FK_CONTRACT_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contratos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `contract`
#

LOCK TABLES `contract` WRITE;
ALTER TABLE `contract` DISABLE KEYS;
ALTER TABLE `contract` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `contract_attach`
#

DROP TABLE IF EXISTS `contract_attach`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `contract_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Archivo Adjunto del contrato',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_CONTRACT_ATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_ATTACH_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CONTRACT_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de contratos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `contract_attach`
#

LOCK TABLES `contract_attach` WRITE;
ALTER TABLE `contract_attach` DISABLE KEYS;
ALTER TABLE `contract_attach` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `contract_batch`
#

DROP TABLE IF EXISTS `contract_batch`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `contract_batch` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la remesa de contratos',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `date` date NOT NULL COMMENT 'Fecha de la ultima remesa en la que fue incluido',
  `red_notify_date` date default NULL COMMENT 'Fecha de notificacion al sistema red',
  `red_notify_id` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Identificador de la notificacion',
  `red_response_date` date default NULL COMMENT 'Fecha de respuesta del sistema red',
  `red_response_id` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Identificador de la respuesta',
  `status` tinyint(2) NOT NULL default '0' COMMENT 'Indica el estado de la remesa',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_BATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas de contratos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `contract_batch`
#

LOCK TABLES `contract_batch` WRITE;
ALTER TABLE `contract_batch` DISABLE KEYS;
ALTER TABLE `contract_batch` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `contract_batch_attach`
#

DROP TABLE IF EXISTS `contract_batch_attach`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `contract_batch_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Archivo Adjunto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_CONTRACT_BATCH_ATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_BATCH_ATTACH_CONTRACT_BATCH` FOREIGN KEY (`contract_batch`) REFERENCES `contract_batch` (`id`),
  CONSTRAINT `FK_CONTRACT_BATCH_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CONTRACT_BATCH_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de remesas de mensajes afi de la s.s.';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `contract_batch_attach`
#

LOCK TABLES `contract_batch_attach` WRITE;
ALTER TABLE `contract_batch_attach` DISABLE KEYS;
ALTER TABLE `contract_batch_attach` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `contract_batch_detail`
#

DROP TABLE IF EXISTS `contract_batch_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `contract_batch_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del detalle de la remesa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `contract_batch` int(4) NOT NULL COMMENT 'Identificador unico de la remesa de contratos',
  `contract` int(4) NOT NULL COMMENT 'Identificador unico del contrato',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_BATCH_DETAIL_CONTRACT_BATCH` (`contract_batch`),
  KEY `IDX_CONTRACT_BATCH_DETAIL_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_BATCH_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_BATCH_DETAIL_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_BATCH_DETAIL_CONTRACT_BATCH` FOREIGN KEY (`contract_batch`) REFERENCES `contract_batch` (`id`),
  CONSTRAINT `FK_CONTRACT_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las remesas de contratos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `contract_batch_detail`
#

LOCK TABLES `contract_batch_detail` WRITE;
ALTER TABLE `contract_batch_detail` DISABLE KEYS;
ALTER TABLE `contract_batch_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `contract_bonus`
#

DROP TABLE IF EXISTS `contract_bonus`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `contract_bonus` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Frmula',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `bonus_concept` int(4) default NULL COMMENT 'Concepto de bonificacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_BONUS_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_BONUS_BONUS_CONCEPT` (`bonus_concept`),
  KEY `IDX_CONTRACT_BONUS_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_BONUS_BONUS_CONCEPT` FOREIGN KEY (`bonus_concept`) REFERENCES `bonus_concept` (`id`),
  CONSTRAINT `FK_CONTRACT_BONUS_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_BONUS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bonificaciones';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `contract_bonus`
#

LOCK TABLES `contract_bonus` WRITE;
ALTER TABLE `contract_bonus` DISABLE KEYS;
ALTER TABLE `contract_bonus` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `contract_calendar_event`
#

DROP TABLE IF EXISTS `contract_calendar_event`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `contract_calendar_event` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int(4) NOT NULL COMMENT 'Identificador del Contrato',
  `date` date NOT NULL COMMENT 'Fecha de la incidencia',
  `type` tinyint(2) default NULL COMMENT 'Tipo de incidencia',
  `duration` double default NULL COMMENT 'Duracion de la incidencia',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_CALENDAR_EVENT_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_CALENDAR_EVENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_CALENDAR_EVENT_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_CALENDAR_EVENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Incidencias de calendario en Contratos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `contract_calendar_event`
#

LOCK TABLES `contract_calendar_event` WRITE;
ALTER TABLE `contract_calendar_event` DISABLE KEYS;
ALTER TABLE `contract_calendar_event` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `contract_data`
#

DROP TABLE IF EXISTS `contract_data`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `contract_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_DATA_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_DATA_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto del contrato';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `contract_data`
#

LOCK TABLES `contract_data` WRITE;
ALTER TABLE `contract_data` DISABLE KEYS;
ALTER TABLE `contract_data` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `contract_deduction`
#

DROP TABLE IF EXISTS `contract_deduction`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `contract_deduction` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Deduccin',
  `deduction_concept` int(4) default NULL COMMENT 'Identificador unico del concepto',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `description_decorable` tinyint(2) NOT NULL default '0',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Frmula',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `month` tinyint(2) default NULL COMMENT 'Mes de la percepcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_DEDUCTION_DEDUCTION_CONCEPT` (`deduction_concept`),
  KEY `IDX_CONTRACT_DEDUCTION_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_DEDUCTION_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_DEDUCTION_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_DEDUCTION_DEDUCTION_CONCEPT` FOREIGN KEY (`deduction_concept`) REFERENCES `deduction_concept` (`id`),
  CONSTRAINT `FK_CONTRACT_DEDUCTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Deducciones';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `contract_deduction`
#

LOCK TABLES `contract_deduction` WRITE;
ALTER TABLE `contract_deduction` DISABLE KEYS;
ALTER TABLE `contract_deduction` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `contract_embargo`
#

DROP TABLE IF EXISTS `contract_embargo`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `contract_embargo` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `amount` double(15,3) default '0.000' COMMENT 'Importe',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Formula',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_EMBARGO_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_EMBARGO_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_EMBARGO_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_EMBARGO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Embargos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `contract_embargo`
#

LOCK TABLES `contract_embargo` WRITE;
ALTER TABLE `contract_embargo` DISABLE KEYS;
ALTER TABLE `contract_embargo` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `contract_leave`
#

DROP TABLE IF EXISTS `contract_leave`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `contract_leave` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_CONTRACT_LEAVE_CONTRACT_LEAVE` (`parent`),
  KEY `IDX_CONTRACT_LEAVE_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_LEAVE_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_LEAVE_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_LEAVE_CONTRACT_LEAVE` FOREIGN KEY (`parent`) REFERENCES `contract_leave` (`id`),
  CONSTRAINT `FK_CONTRACT_LEAVE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bajas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `contract_leave`
#

LOCK TABLES `contract_leave` WRITE;
ALTER TABLE `contract_leave` DISABLE KEYS;
ALTER TABLE `contract_leave` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `contract_leave_detail`
#

DROP TABLE IF EXISTS `contract_leave_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `contract_leave_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de parte',
  `contract_leave` int(4) NOT NULL COMMENT 'Contrato',
  `college_number` varchar(8) collate latin1_spanish_ci default NULL COMMENT 'Numero de colegiado',
  `confirm_order` tinyint(2) default NULL COMMENT 'Numero de orden del parte de confirmacion',
  `cias` varchar(11) collate latin1_spanish_ci default NULL COMMENT 'codigo identificacion area sanitaria',
  `date` date NOT NULL COMMENT 'Fecha del parte',
  `status` tinyint(2) NOT NULL default '0' COMMENT 'Indica el estado',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE` (`contract_leave`),
  KEY `IDX_CONTRACT_LEAVE_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE` FOREIGN KEY (`contract_leave`) REFERENCES `contract_leave` (`id`),
  CONSTRAINT `FK_CONTRACT_LEAVE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las bajas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `contract_leave_detail`
#

LOCK TABLES `contract_leave_detail` WRITE;
ALTER TABLE `contract_leave_detail` DISABLE KEYS;
ALTER TABLE `contract_leave_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `contract_payment`
#

DROP TABLE IF EXISTS `contract_payment`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `contract_payment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Percepcin Salarial',
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
  KEY `IDX_CONTRACT_PAYMENT_PAYMENT_CONCEPT` (`payment_concept`),
  KEY `IDX_CONTRACT_PAYMENT_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_PAYMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_PAYMENT_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_PAYMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CONTRACT_PAYMENT_PAYMENT_CONCEPT` FOREIGN KEY (`payment_concept`) REFERENCES `payment_concept` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Percepciones Salariales';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `contract_payment`
#

LOCK TABLES `contract_payment` WRITE;
ALTER TABLE `contract_payment` DISABLE KEYS;
ALTER TABLE `contract_payment` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `cost_profile`
#

DROP TABLE IF EXISTS `cost_profile`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cost_profile` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion',
  `cost` double NOT NULL default '0' COMMENT 'Costo por hora',
  PRIMARY KEY  (`id`),
  KEY `IDX_COST_PROFILE_DOMAIN` (`domain`),
  CONSTRAINT `FK_COST_PROFILE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Perfiles de Costos para Usuarios';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `cost_profile`
#

LOCK TABLES `cost_profile` WRITE;
ALTER TABLE `cost_profile` DISABLE KEYS;
ALTER TABLE `cost_profile` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `course`
#

DROP TABLE IF EXISTS `course`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `course` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Curso',
  `code` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Alias del Curso',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Curso',
  `start_date` date NOT NULL COMMENT 'Fecha inicio del Curso',
  `end_date` date NOT NULL COMMENT 'Fecha fin del Curso',
  `academic_year` int(4) NOT NULL COMMENT 'Ao Academico del Curso',
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
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `course`
#

LOCK TABLES `course` WRITE;
ALTER TABLE `course` DISABLE KEYS;
ALTER TABLE `course` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `course_academicskill`
#

DROP TABLE IF EXISTS `course_academicskill`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
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
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `course_academicskill`
#

LOCK TABLES `course_academicskill` WRITE;
ALTER TABLE `course_academicskill` DISABLE KEYS;
ALTER TABLE `course_academicskill` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `course_alumn`
#

DROP TABLE IF EXISTS `course_alumn`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
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
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `course_alumn`
#

LOCK TABLES `course_alumn` WRITE;
ALTER TABLE `course_alumn` DISABLE KEYS;
ALTER TABLE `course_alumn` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `course_evaluation`
#

DROP TABLE IF EXISTS `course_evaluation`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
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
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `course_evaluation`
#

LOCK TABLES `course_evaluation` WRITE;
ALTER TABLE `course_evaluation` DISABLE KEYS;
ALTER TABLE `course_evaluation` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `course_instructor`
#

DROP TABLE IF EXISTS `course_instructor`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `course_instructor` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `course` int(4) NOT NULL COMMENT 'Identificador del Curso',
  `employee` int(4) NOT NULL COMMENT 'Identificador del Profesor',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Profesor',
  PRIMARY KEY  (`id`),
  KEY `course` (`course`),
  KEY `employee` (`employee`),
  CONSTRAINT `course-instructor_fk` FOREIGN KEY (`course`) REFERENCES `course` (`id`),
  CONSTRAINT `course-instructor_fk1` FOREIGN KEY (`employee`) REFERENCES `instructor` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Profesores por Curso';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `course_instructor`
#

LOCK TABLES `course_instructor` WRITE;
ALTER TABLE `course_instructor` DISABLE KEYS;
ALTER TABLE `course_instructor` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `course_level`
#

DROP TABLE IF EXISTS `course_level`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `course_level` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Nivel',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Nivel',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Niveles de Cursos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `course_level`
#

LOCK TABLES `course_level` WRITE;
ALTER TABLE `course_level` DISABLE KEYS;
ALTER TABLE `course_level` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `course_observation`
#

DROP TABLE IF EXISTS `course_observation`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `course_observation` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `course` int(4) NOT NULL COMMENT 'Identificador de Curso',
  `observation` varchar(64) character set latin1 collate latin1_spanish_ci default NULL COMMENT 'Observaciones',
  PRIMARY KEY  (`id`),
  KEY `course` (`course`),
  CONSTRAINT `course_observation_skill_fk_1` FOREIGN KEY (`course`) REFERENCES `course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Observaciones por Curso';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `course_observation`
#

LOCK TABLES `course_observation` WRITE;
ALTER TABLE `course_observation` DISABLE KEYS;
ALTER TABLE `course_observation` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `course_schedule`
#

DROP TABLE IF EXISTS `course_schedule`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
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
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `course_schedule`
#

LOCK TABLES `course_schedule` WRITE;
ALTER TABLE `course_schedule` DISABLE KEYS;
ALTER TABLE `course_schedule` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `course_subject`
#

DROP TABLE IF EXISTS `course_subject`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `course_subject` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Materia',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Materia',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Materias de Cursos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `course_subject`
#

LOCK TABLES `course_subject` WRITE;
ALTER TABLE `course_subject` DISABLE KEYS;
ALTER TABLE `course_subject` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `creditor`
#

DROP TABLE IF EXISTS `creditor`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `creditor` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro del Acreedor',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `withholding` tinyint(1) default '0' COMMENT 'Indica si el Acreedor aplica retencion de impuestos',
  `transaction` tinyint(2) default '0' COMMENT 'Tipo de transacciones del Acreedor',
  `status` tinyint(2) default NULL COMMENT 'Estado del Acreedor',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  PRIMARY KEY  (`registry`),
  KEY `IDX_CREDITOR_SCOPE` (`scope`),
  KEY `IDX_CREDITOR_DOMAIN` (`domain`),
  CONSTRAINT `FK_CREDITOR_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CREDITOR_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_CREDITOR_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Acreedores';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `creditor`
#

LOCK TABLES `creditor` WRITE;
ALTER TABLE `creditor` DISABLE KEYS;
ALTER TABLE `creditor` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `creditor_account`
#

DROP TABLE IF EXISTS `creditor_account`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `creditor_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Cuenta Contable del Acreedor',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `creditor` int(4) NOT NULL default '0' COMMENT 'Identificador del Acreedor',
  `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `IDX_CREDITOR_ACCOUNT_ACCOUNT` (`account`),
  KEY `IDX_CREDITOR_ACCOUNT_CREDITOR` (`creditor`),
  KEY `IDX_CREDITOR_ACCOUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_CREDITOR_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_CREDITOR_ACCOUNT_CREDITOR` FOREIGN KEY (`creditor`) REFERENCES `creditor` (`registry`),
  CONSTRAINT `FK_CREDITOR_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Acreedores';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `creditor_account`
#

LOCK TABLES `creditor_account` WRITE;
ALTER TABLE `creditor_account` DISABLE KEYS;
ALTER TABLE `creditor_account` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `customer`
#

DROP TABLE IF EXISTS `customer`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro del Cliente',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `tariff` int(4) default NULL COMMENT 'Tarifa asociada al Cliente',
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
  KEY `IDX_CUSTOMER_TARIFF` (`tariff`),
  KEY `IDX_CUSTOMER_SCOPE` (`scope`),
  KEY `IDX_CUSTOMER_SEGMENT` (`segment`),
  KEY `IDX_CUSTOMER_DOMAIN` (`domain`),
  CONSTRAINT `FK_CUSTOMER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CUSTOMER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_CUSTOMER_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_CUSTOMER_SEGMENT` FOREIGN KEY (`segment`) REFERENCES `customer_segment` (`id`),
  CONSTRAINT `FK_CUSTOMER_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Clientes';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `customer`
#

LOCK TABLES `customer` WRITE;
ALTER TABLE `customer` DISABLE KEYS;
ALTER TABLE `customer` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `customer_account`
#

DROP TABLE IF EXISTS `customer_account`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Cuenta Contable del Cliente',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `customer` int(4) NOT NULL default '0' COMMENT 'Identificador del Cliente',
  `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `IDX_CUSTOMER_ACCOUNT_ACCOUNT` (`account`),
  KEY `IDX_CUSTOMER_ACCOUNT_CUSTOMER` (`customer`),
  KEY `IDX_CUSTOMER_ACCOUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_CUSTOMER_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_CUSTOMER_ACCOUNT_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_CUSTOMER_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Clientes';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `customer_account`
#

LOCK TABLES `customer_account` WRITE;
ALTER TABLE `customer_account` DISABLE KEYS;
ALTER TABLE `customer_account` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `customer_fee`
#

DROP TABLE IF EXISTS `customer_fee`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_fee` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Cuota del Cliente',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `customer` int(4) default NULL COMMENT 'Identificador del Cliente',
  `line` smallint(2) default '1' COMMENT 'Numero de linea de Cuota',
  `item` int(4) default NULL COMMENT 'Identificador del Articulo',
  `description` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de la Cuota',
  `quantity` double(15,3) default '0.000' COMMENT 'Cantidad de la Cuota',
  `price` double default '0' COMMENT 'Precio de la Cuota',
  `discount_expr` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descuentos de la Cuota',
  `initial_date` date default NULL COMMENT 'Fecha de inicio de la Cuota',
  `final_date` date default NULL COMMENT 'Fecha de finalizacion de la Cuota',
  `billing_date` date default NULL COMMENT 'Proxima fecha de facturacin de la Cuota',
  `period` smallint(2) default '1' COMMENT 'Periodo de facturacion en meses de la Cuota',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad de la Cuota',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  PRIMARY KEY  (`id`),
  KEY `IDX_CUSTOMER_FEE_CUSTOMER` (`customer`),
  KEY `IDX_CUSTOMER_FEE_ITEM` (`item`),
  KEY `IDX_CUSTOMER_FEE_WORKPLACE` (`workplace`),
  KEY `IDX_CUSTOMER_FEE_DOMAIN` (`domain`),
  CONSTRAINT `FK_CUSTOMER_FEE_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_CUSTOMER_FEE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CUSTOMER_FEE_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_CUSTOMER_FEE_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuotas de Clientes';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `customer_fee`
#

LOCK TABLES `customer_fee` WRITE;
ALTER TABLE `customer_fee` DISABLE KEYS;
ALTER TABLE `customer_fee` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `customer_segment`
#

DROP TABLE IF EXISTS `customer_segment`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_segment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Segmento',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Segmento',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Segmentaciones de Clientes';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `customer_segment`
#

LOCK TABLES `customer_segment` WRITE;
ALTER TABLE `customer_segment` DISABLE KEYS;
ALTER TABLE `customer_segment` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `daily_tracking`
#

DROP TABLE IF EXISTS `daily_tracking`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `daily_tracking` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Parte',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `daily_tracking`
#

LOCK TABLES `daily_tracking` WRITE;
ALTER TABLE `daily_tracking` DISABLE KEYS;
ALTER TABLE `daily_tracking` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `db_version`
#

DROP TABLE IF EXISTS `db_version`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `db_version` (
  `version_number` varchar(10) collate latin1_spanish_ci NOT NULL COMMENT 'Numero de Version de la Base de Datos',
  PRIMARY KEY  (`version_number`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Version de la Base de Datos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `db_version`
#

LOCK TABLES `db_version` WRITE;
ALTER TABLE `db_version` DISABLE KEYS;
INSERT INTO `db_version` VALUES ('7.0.8');
ALTER TABLE `db_version` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `deduction_concept`
#

DROP TABLE IF EXISTS `deduction_concept`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `deduction_concept` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `code` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Codigo',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Deduccion Salarial',
  `description_decorable` tinyint(2) default '0',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe',
  PRIMARY KEY  (`id`),
  KEY `IDX_DEDUCTION_CONCEPT_DOMAIN` (`domain`),
  CONSTRAINT `FK_DEDUCTION_CONCEPT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos de deducciones';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `deduction_concept`
#

LOCK TABLES `deduction_concept` WRITE;
ALTER TABLE `deduction_concept` DISABLE KEYS;
INSERT INTO `deduction_concept` VALUES (1,1,'CGC','4.70 %',0,1,'BASE_CGC * 4.70/100'),(2,1,'CGP','(BASE_CGP/CGP*100)+\'%\'',1,0,NULL),(3,1,'DESMP','@{PORCENTAJE_DESMPL} %',2,1,'( TIEMPO_COMPLETO && ASIMILADO_REGIMEN_GRAL ) ? 0 : BASE_CGP * (PORCENTAJE_DESMPL=(INDEFINIDO ? 1.55 : 1.60 ))/100'),(4,1,'FP','0.10 %',3,1,'BASE_CGP * 0.10/100'),(5,1,'NESTR','2.00 %',4,1,'BASE_ESTR * 2.00/100'),(6,1,'ESTR','4.70 %',5,1,'BASE_NESTR * 4.70/100'),(7,1,'IRPF','@{PORCENTAJE_IRPF} %',6,1,'BASE_IRPF * PORCENTAJE_IRPF/100');
ALTER TABLE `deduction_concept` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `delivery`
#

DROP TABLE IF EXISTS `delivery`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `delivery` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Albaran de Venta',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project` int(4) default NULL COMMENT 'Identificador del Proyecto',
  `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie del Albaran',
  `number` int(4) NOT NULL default '0' COMMENT 'Nmero del Albaran',
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
  UNIQUE KEY `IDX_UNQ_DELIVERY_DOMAIN_SERIES_NUMBER` (`domain`,`series`,`number`),
  KEY `IDX_DELIVERY_WORKPLACE` (`workplace`),
  KEY `IDX_DELIVERY_SCOPE` (`scope`),
  KEY `IDX_DELIVERY_BANK` (`bank`),
  KEY `IDX_DELIVERY_PROJECT` (`project`),
  KEY `IDX_DELIVERY_ISSUE_TIME` (`issue_time`),
  KEY `IDX_DELIVERY_CUSTOMER` (`customer`),
  KEY `IDX_DELIVERY_RADDRESS` (`address`),
  KEY `IDX_DELIVERY_PAY_METHOD` (`pay_method`),
  KEY `IDX_DELIVERY_DOMAIN` (`domain`),
  CONSTRAINT `FK_DELIVERY_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`),
  CONSTRAINT `FK_DELIVERY_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_DELIVERY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_DELIVERY_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_DELIVERY_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_DELIVERY_RADDRESS` FOREIGN KEY (`address`) REFERENCES `raddress` (`id`),
  CONSTRAINT `FK_DELIVERY_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_DELIVERY_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Albaranes de Venta';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `delivery`
#

LOCK TABLES `delivery` WRITE;
ALTER TABLE `delivery` DISABLE KEYS;
ALTER TABLE `delivery` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `delivery_detail`
#

DROP TABLE IF EXISTS `delivery_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `delivery_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle del Albaran de Venta',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `delivery_detail`
#

LOCK TABLES `delivery_detail` WRITE;
ALTER TABLE `delivery_detail` DISABLE KEYS;
ALTER TABLE `delivery_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `department`
#

DROP TABLE IF EXISTS `department`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `department` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre del Departamento',
  PRIMARY KEY  (`id`),
  KEY `IDX_DEPARTMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_DEPARTMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Departamentos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `department`
#

LOCK TABLES `department` WRITE;
ALTER TABLE `department` DISABLE KEYS;
ALTER TABLE `department` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `domain`
#

DROP TABLE IF EXISTS `domain`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `domain` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Dominio',
  `parent` int(4) default NULL COMMENT 'Identificador del Dominio padre',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indica si el Dominio esta activo o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_DOMAIN_PARENT` (`parent`),
  CONSTRAINT `FK_DOMAIN_PARENT` FOREIGN KEY (`parent`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Dominios';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `domain`
#

LOCK TABLES `domain` WRITE;
ALTER TABLE `domain` DISABLE KEYS;
INSERT INTO `domain` VALUES (1,'Toledo & Asociados Asesores Laborales',NULL,1);
ALTER TABLE `domain` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `ec_catalogue`
#

DROP TABLE IF EXISTS `ec_catalogue`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
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
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `ec_catalogue`
#

LOCK TABLES `ec_catalogue` WRITE;
ALTER TABLE `ec_catalogue` DISABLE KEYS;
ALTER TABLE `ec_catalogue` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `ec_config`
#

DROP TABLE IF EXISTS `ec_config`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
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
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `ec_config`
#

LOCK TABLES `ec_config` WRITE;
ALTER TABLE `ec_config` DISABLE KEYS;
ALTER TABLE `ec_config` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `ec_offer_pay_info`
#

DROP TABLE IF EXISTS `ec_offer_pay_info`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `ec_offer_pay_info` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `offer` int(4) NOT NULL COMMENT 'Identificador del Presupuesto',
  `payment_status` tinyint(2) default NULL COMMENT 'Estado del pago',
  `authorization_number` int(4) default NULL COMMENT 'Numero de autorizacion',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_EC_OFFER_PAY_INFO_OFFER` (`offer`),
  CONSTRAINT `FK_EC_OFFER_PAY_INFO_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Informacion acerca de los Pagos en el ECommerce ';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `ec_offer_pay_info`
#

LOCK TABLES `ec_offer_pay_info` WRITE;
ALTER TABLE `ec_offer_pay_info` DISABLE KEYS;
ALTER TABLE `ec_offer_pay_info` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `ec_paymethod`
#

DROP TABLE IF EXISTS `ec_paymethod`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `ec_paymethod` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `pay_method` int(4) NOT NULL COMMENT 'Identificador de la Forma de Pago',
  `user_name` varchar(64) collate latin1_spanish_ci default 'Null' COMMENT 'Nombre de Usuario',
  `password` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Contrasea para la pasarela de pago',
  `signature` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Identificador unico de la empresa para pasarela',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_ECPAYMETHOD_PAYMETHOD` (`pay_method`),
  CONSTRAINT `FK_ECPAYMETHOD_PAYMETHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Formas de Pago del ECommerce';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `ec_paymethod`
#

LOCK TABLES `ec_paymethod` WRITE;
ALTER TABLE `ec_paymethod` DISABLE KEYS;
ALTER TABLE `ec_paymethod` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `ec_target`
#

DROP TABLE IF EXISTS `ec_target`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
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
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `ec_target`
#

LOCK TABLES `ec_target` WRITE;
ALTER TABLE `ec_target` DISABLE KEYS;
ALTER TABLE `ec_target` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `enterprise`
#

DROP TABLE IF EXISTS `enterprise`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `enterprise` (
  `registry` int(4) NOT NULL default '1' COMMENT 'Registro de la Empresa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `calendar` int(4) default NULL COMMENT 'Calendario',
  PRIMARY KEY  (`registry`),
  KEY `IDX_ENTERPRISE_SCOPE` (`scope`),
  KEY `IDX_ENTERPRISE_CALENDAR` (`calendar`),
  KEY `IDX_ENTERPRISE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ENTERPRISE_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_ENTERPRISE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ENTERPRISE_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_ENTERPRISE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Empresa';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `enterprise`
#

LOCK TABLES `enterprise` WRITE;
ALTER TABLE `enterprise` DISABLE KEYS;
INSERT INTO `enterprise` VALUES (1,1,1,NULL);
ALTER TABLE `enterprise` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `enterprise_activity`
#

DROP TABLE IF EXISTS `enterprise_activity`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `enterprise_activity` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Actividad de la Empresa',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `cnae` int(4) default NULL COMMENT 'Identificador del CNAE',
  `type` tinyint(2) NOT NULL COMMENT 'Tipo de Actividad de la Empresa',
  `cnae2009` int(4) default NULL COMMENT 'Identificador del CNAE 2009',
  PRIMARY KEY  (`id`),
  KEY `IDX_ENTERPRISE_ACTIVITY_ENTERPRISE` (`enterprise`),
  KEY `IDX_ENTERPRISE_ACTIVITY_CNAE` (`cnae`),
  KEY `IDX_ENTERPRISE_ACTIVITY_CNAE2009` (`cnae2009`),
  KEY `IDX_ENTERPRISE_ACTIVITY_DOMAIN` (`domain`),
  CONSTRAINT `FK_ENTERPRISE_ACTIVITY_CNAE` FOREIGN KEY (`cnae`) REFERENCES `cnae` (`id`),
  CONSTRAINT `FK_ENTERPRISE_ACTIVITY_CNAE2009` FOREIGN KEY (`cnae2009`) REFERENCES `cnae2009` (`id`),
  CONSTRAINT `FK_ENTERPRISE_ACTIVITY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ENTERPRISE_ACTIVITY_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades de Empresas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `enterprise_activity`
#

LOCK TABLES `enterprise_activity` WRITE;
ALTER TABLE `enterprise_activity` DISABLE KEYS;
ALTER TABLE `enterprise_activity` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `enterprise_agreement`
#

DROP TABLE IF EXISTS `enterprise_agreement`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `enterprise_agreement` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `agreement` int(4) NOT NULL COMMENT 'Identificador del Convenio',
  PRIMARY KEY  (`id`),
  KEY `IDX_ENTERPRISE_AGREEMENT_ENTERPRISE` (`enterprise`),
  KEY `IDX_ENTERPRISE_AGREEMENT_AGREEMENT` (`agreement`),
  KEY `IDX_ENTERPRISE_AGREEMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_ENTERPRISE_AGREEMENT_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`),
  CONSTRAINT `FK_ENTERPRISE_AGREEMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ENTERPRISE_AGREEMENT_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Convenio de la Empresa';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `enterprise_agreement`
#

LOCK TABLES `enterprise_agreement` WRITE;
ALTER TABLE `enterprise_agreement` DISABLE KEYS;
ALTER TABLE `enterprise_agreement` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `enterprise_ccc`
#

DROP TABLE IF EXISTS `enterprise_ccc`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `enterprise_ccc` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `ccc` char(11) collate latin1_spanish_ci default NULL COMMENT 'Valor del Codigo Cuenta Cotizacion',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Cuenta Cotizacion',
  `enterprise_activity` int(4) NOT NULL COMMENT 'Identificador de la Actividad de Empresa',
  `geozone` int(4) default NULL COMMENT 'Identificador de la Zona Geografica',
  PRIMARY KEY  (`id`),
  KEY `IDX_ENTERPRISE_CCC_ENTERPRISE_ACTIVITY` (`enterprise_activity`),
  KEY `IDX_ENTERPRISE_CCC_GEOZONE` (`geozone`),
  KEY `IDX_ENTERPRISE_CCC_DOMAIN` (`domain`),
  CONSTRAINT `FK_ENTERPRISE_CCC_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ENTERPRISE_CCC_ENTERPRISE_ACTIVITY` FOREIGN KEY (`enterprise_activity`) REFERENCES `enterprise_activity` (`id`),
  CONSTRAINT `FK_ENTERPRISE_CCC_GEOZONE` FOREIGN KEY (`geozone`) REFERENCES `geozone` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Codigo Cuenta Cotizacion';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `enterprise_ccc`
#

LOCK TABLES `enterprise_ccc` WRITE;
ALTER TABLE `enterprise_ccc` DISABLE KEYS;
ALTER TABLE `enterprise_ccc` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `enterprise_data`
#

DROP TABLE IF EXISTS `enterprise_data`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `enterprise_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de Empresa',
  `name` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Expresion',
  `start_date` date default NULL COMMENT 'Fecha de inicio',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_ENTERPRISE_DATA_ENTERPRISE` (`enterprise`),
  KEY `IDX_ENTERPRISE_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_ENTERPRISE_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ENTERPRISE_DATA_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto de la Empresa';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `enterprise_data`
#

LOCK TABLES `enterprise_data` WRITE;
ALTER TABLE `enterprise_data` DISABLE KEYS;
ALTER TABLE `enterprise_data` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `evaluation_observation`
#

DROP TABLE IF EXISTS `evaluation_observation`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `evaluation_observation` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `alumn` int(4) NOT NULL COMMENT 'Identificador de Alumno',
  `evaluation` tinyint(2) NOT NULL COMMENT 'Numero de Evaluacion',
  `comments` text character set latin1 collate latin1_spanish_ci COMMENT 'Comentarios',
  PRIMARY KEY  (`id`),
  KEY `alumn` (`alumn`),
  CONSTRAINT `evaluation_observation_ibfk_1` FOREIGN KEY (`alumn`) REFERENCES `course_alumn` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Observaciones por Evaluacion';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `evaluation_observation`
#

LOCK TABLES `evaluation_observation` WRITE;
ALTER TABLE `evaluation_observation` DISABLE KEYS;
ALTER TABLE `evaluation_observation` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `fan_batch`
#

DROP TABLE IF EXISTS `fan_batch`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fan_batch` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la remesa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `date` date NOT NULL COMMENT 'Fecha de la ultima remesa en la que fue incluido',
  `status` tinyint(2) NOT NULL default '0' COMMENT 'Indica el estado de la remesa',
  `liquidation_type` tinyint(2) NOT NULL default '0' COMMENT 'Indica el tipo de liquidacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_FAN_BATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_FAN_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas del fichero fan';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `fan_batch`
#

LOCK TABLES `fan_batch` WRITE;
ALTER TABLE `fan_batch` DISABLE KEYS;
ALTER TABLE `fan_batch` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `fan_batch_attach`
#

DROP TABLE IF EXISTS `fan_batch_attach`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fan_batch_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Archivo Adjunto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_FAN_BATCH_ATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_FAN_BATCH_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FAN_BATCH_ATTACH_FAN_BATCH` FOREIGN KEY (`fan_batch`) REFERENCES `fan_batch` (`id`),
  CONSTRAINT `FK_FAN_BATCH_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de remesas de mensajes fan de la s.s.';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `fan_batch_attach`
#

LOCK TABLES `fan_batch_attach` WRITE;
ALTER TABLE `fan_batch_attach` DISABLE KEYS;
ALTER TABLE `fan_batch_attach` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `fan_batch_detail`
#

DROP TABLE IF EXISTS `fan_batch_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fan_batch_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del detalle',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fan_batch` int(4) NOT NULL COMMENT 'Identificador unico de la remesa',
  `enterprise_ccc` int(4) NOT NULL COMMENT 'Identificador unico del ccc',
  PRIMARY KEY  (`id`),
  KEY `IDX_FAN_BATCH_DETAIL_FAN_BATCH` (`fan_batch`),
  KEY `IDX_FAN_BATCH_DETAIL_ENTERPRISE_CCC` (`enterprise_ccc`),
  KEY `IDX_FAN_BATCH_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_FAN_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FAN_BATCH_DETAIL_ENTERPRISE_CCC` FOREIGN KEY (`enterprise_ccc`) REFERENCES `enterprise_ccc` (`id`),
  CONSTRAINT `FK_FAN_BATCH_DETAIL_FAN_BATCH` FOREIGN KEY (`fan_batch`) REFERENCES `fan_batch` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las remesas del fichero fan';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `fan_batch_detail`
#

LOCK TABLES `fan_batch_detail` WRITE;
ALTER TABLE `fan_batch_detail` DISABLE KEYS;
ALTER TABLE `fan_batch_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `favorite`
#

DROP TABLE IF EXISTS `favorite`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `favorite` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de Favorito',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `favorite_category` int(4) NOT NULL COMMENT 'Categoria a la que pertenece el Favorito',
  `description` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Favorito',
  `url` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Url del Favorito',
  `user_id` int(4) NOT NULL COMMENT 'Usuario al que pertenece el Favorito',
  PRIMARY KEY  (`id`),
  KEY `IDX_FAVORITE_FAVORITE_CATEGORY` (`favorite_category`),
  KEY `IDX_FAVORITE_USER` (`user_id`),
  KEY `IDX_FAVORITE_DOMAIN` (`domain`),
  CONSTRAINT `FK_FAVORITE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FAVORITE_FAVORITE_CATEGORY` FOREIGN KEY (`favorite_category`) REFERENCES `favorite_category` (`id`),
  CONSTRAINT `FK_FAVORITE_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Favoritos de Usuario';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `favorite`
#

LOCK TABLES `favorite` WRITE;
ALTER TABLE `favorite` DISABLE KEYS;
ALTER TABLE `favorite` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `favorite_category`
#

DROP TABLE IF EXISTS `favorite_category`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `favorite_category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Categoria',
  `user_id` int(4) NOT NULL COMMENT 'Usuario al que pertenece la Categoria',
  PRIMARY KEY  (`id`),
  KEY `IDX_FAVORITE_CATEGORY_USER` (`user_id`),
  KEY `IDX_FAVORITE_CATEGORY_DOMAIN` (`domain`),
  CONSTRAINT `FK_FAVORITE_CATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FAVORITE_CATEGORY_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias de Favoritos de Usuario';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `favorite_category`
#

LOCK TABLES `favorite_category` WRITE;
ALTER TABLE `favorite_category` DISABLE KEYS;
ALTER TABLE `favorite_category` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `fbatch`
#

DROP TABLE IF EXISTS `fbatch`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fbatch` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Remesa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de la Remesa',
  `issue_date` date default NULL COMMENT 'Fecha de emision de la Remesa',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Remesa',
  `status` tinyint(2) default NULL COMMENT 'Estado de la Remesa',
  `rbank` int(4) default NULL COMMENT 'Banco de la Compaia utilizado en la Remesa',
  `bank_statement_link` int(4) default NULL COMMENT 'Identificador de la Linea del Extracto bancario',
  `payment` tinyint(1) NOT NULL default '0' COMMENT 'Indica si es un pago o un cobro',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad',
  PRIMARY KEY  (`id`),
  KEY `IDX_FBATCH_BANK_STATEMENT_LINK` (`bank_statement_link`),
  KEY `IDX_FBATCH_RBANK` (`rbank`),
  KEY `IDX_FBATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_FBATCH_BANK_STATEMENT_LINK` FOREIGN KEY (`bank_statement_link`) REFERENCES `bank_statement_link` (`id`),
  CONSTRAINT `FK_FBATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FBATCH_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `fbatch`
#

LOCK TABLES `fbatch` WRITE;
ALTER TABLE `fbatch` DISABLE KEYS;
ALTER TABLE `fbatch` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `fbatch_detail`
#

DROP TABLE IF EXISTS `fbatch_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fbatch_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle de la Remesa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fbatch` int(4) NOT NULL COMMENT 'Identificador de la Remesa',
  `finance` int(4) NOT NULL COMMENT 'Identificador del Vencimiento',
  `amount` double(15,3) default '0.000' COMMENT 'Importe del Detalle de la Remesa',
  `status` tinyint(2) default NULL COMMENT 'Estado del Detalle de la Remesa',
  PRIMARY KEY  (`id`),
  KEY `IDX_FBATCH_DETAIL_FINANCE` (`finance`),
  KEY `IDX_FBATCH_DETAIL_FBATCH` (`fbatch`),
  KEY `IDX_FBATCH_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_FBATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FBATCH_DETAIL_FBATCH` FOREIGN KEY (`fbatch`) REFERENCES `fbatch` (`id`),
  CONSTRAINT `FK_FBATCH_DETAIL_FINANCE` FOREIGN KEY (`finance`) REFERENCES `finance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de la Remesa';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `fbatch_detail`
#

LOCK TABLES `fbatch_detail` WRITE;
ALTER TABLE `fbatch_detail` DISABLE KEYS;
ALTER TABLE `fbatch_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `feature`
#

DROP TABLE IF EXISTS `feature`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `feature` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre de la Caracteristica',
  PRIMARY KEY  (`id`),
  KEY `IDX_FEATURE_DOMAIN` (`domain`),
  CONSTRAINT `FK_FEATURE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Caracteristicas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `feature`
#

LOCK TABLES `feature` WRITE;
ALTER TABLE `feature` DISABLE KEYS;
ALTER TABLE `feature` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `finance`
#

DROP TABLE IF EXISTS `finance`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `finance` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Vencimiento',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_FINANCE_SCOPE` (`scope`),
  KEY `IDX_FINANCE_DUE_DATE` (`due_date`),
  KEY `IDX_FINANCE_REGISTRY` (`registry`),
  KEY `IDX_FINANCE_PAY_METHOD` (`pay_method`),
  KEY `IDX_FINANCE_BANK` (`bank`),
  KEY `IDX_FINANCE_INVOICE` (`invoice`),
  KEY `IDX_FINANCE_DOMAIN` (`domain`),
  CONSTRAINT `FK_FINANCE_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`),
  CONSTRAINT `FK_FINANCE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FINANCE_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`),
  CONSTRAINT `FK_FINANCE_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_FINANCE_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_FINANCE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Vencimientos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `finance`
#

LOCK TABLES `finance` WRITE;
ALTER TABLE `finance` DISABLE KEYS;
ALTER TABLE `finance` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `finance_tracking`
#

DROP TABLE IF EXISTS `finance_tracking`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `finance_tracking` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `finance` int(4) NOT NULL COMMENT 'Identificador de Vencimiento',
  `tracking_date` date NOT NULL COMMENT 'Fecha de Seguimiento',
  `type` tinyint(4) NOT NULL COMMENT 'Tipo de Seguimiento',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Seguimiento',
  `pm_type_detail` int(4) default NULL COMMENT 'Identificador del Detalle por Tipo de Forma de Pago',
  `rbank` int(4) default NULL COMMENT 'Identificador de la Cuenta Bancaria de la Compaia',
  `bank_statement_link` int(4) default NULL COMMENT 'Identificador de la Linea del Extracto bancario',
  `amount` double(15,3) default NULL COMMENT 'Importe del Seguimiento',
  `recorded` tinyint(1) NOT NULL default '0' COMMENT 'Indica si esta contabilizado o no',
  PRIMARY KEY  (`id`),
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
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `finance_tracking`
#

LOCK TABLES `finance_tracking` WRITE;
ALTER TABLE `finance_tracking` DISABLE KEYS;
ALTER TABLE `finance_tracking` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `fs_mod347`
#

DROP TABLE IF EXISTS `fs_mod347`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fs_mod347` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `administration` tinyint(2) default '0' COMMENT 'Administracion',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `status` tinyint(2) default '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad',
  `complementary` tinyint(1) default '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) default '0' COMMENT 'Declaracion sustitutiva',
  `number` int(4) default '0' COMMENT 'Numero de Decl.',
  `replaced_number` int(4) default '0' COMMENT 'Numero de Decl. complementada o sustituida',
  PRIMARY KEY  (`id`),
  KEY `IDX_FS_MOD347_DOMAIN` (`domain`),
  CONSTRAINT `FK_FS_MOD347_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaracion de Model 347';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `fs_mod347`
#

LOCK TABLES `fs_mod347` WRITE;
ALTER TABLE `fs_mod347` DISABLE KEYS;
ALTER TABLE `fs_mod347` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `fs_mod347_detail`
#

DROP TABLE IF EXISTS `fs_mod347_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fs_mod347_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fs_mod347` int(4) NOT NULL default '0' COMMENT 'Identificador de la Declaracion',
  `type` varchar(1) collate latin1_spanish_ci default '0' COMMENT 'Clave de operacion',
  `document` varchar(9) collate latin1_spanish_ci default NULL COMMENT 'NIF del declarado',
  `registry` int(4) default '0' COMMENT 'Identificador del Declarado',
  `name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Apellidos  y Nombre del declarado',
  `province` int(4) default '0' COMMENT 'Provincia del declarado',
  `country` varchar(2) collate latin1_spanish_ci default '0' COMMENT 'Pais del declarado',
  `amount` double(15,3) default '0.000' COMMENT 'Importe de las operaciones',
  `first_quarter_amount` double(15,3) default '0.000' COMMENT 'Importe de las operaciones primer trimestre',
  `second_quarter_amount` double(15,3) default '0.000' COMMENT 'Importe de las operaciones segundo trimestre',
  `third_quarter_amount` double(15,3) default '0.000' COMMENT 'Importe de las operaciones tercer trimestre',
  `fourth_quarter_amount` double(15,3) default '0.000' COMMENT 'Importe de las operaciones cuarto trimestre',
  PRIMARY KEY  (`id`),
  KEY `IDX_FS_MOD347_DETAIL_FS_MOD347` (`fs_mod347`),
  KEY `IDX_FS_MOD347_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_FS_MOD347_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MOD347_DETAIL_FS_MOD347` FOREIGN KEY (`fs_mod347`) REFERENCES `fs_mod347` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de la Declaracion 347';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `fs_mod347_detail`
#

LOCK TABLES `fs_mod347_detail` WRITE;
ALTER TABLE `fs_mod347_detail` DISABLE KEYS;
ALTER TABLE `fs_mod347_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `fs_prof_retention`
#

DROP TABLE IF EXISTS `fs_prof_retention`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fs_prof_retention` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_FS_PROF_RETENTION_ENTERPRISE` (`enterprise`),
  KEY `IDX_FS_PROF_RETENTION_DOMAIN` (`domain`),
  CONSTRAINT `FK_FS_PROF_RETENTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_PROF_RETENTION_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Retenciones de profesionales';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `fs_prof_retention`
#

LOCK TABLES `fs_prof_retention` WRITE;
ALTER TABLE `fs_prof_retention` DISABLE KEYS;
ALTER TABLE `fs_prof_retention` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `fs_renting`
#

DROP TABLE IF EXISTS `fs_renting`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fs_renting` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  `rbank` int(4) default NULL COMMENT 'Banco de la Compaia',
  PRIMARY KEY  (`id`),
  KEY `IDX_FS_RENTING_RBANK` (`rbank`),
  KEY `IDX_FS_RENTING_DOMAIN` (`domain`),
  CONSTRAINT `FK_FS_RENTING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_RENTING_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaracion de IRPF';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `fs_renting`
#

LOCK TABLES `fs_renting` WRITE;
ALTER TABLE `fs_renting` DISABLE KEYS;
ALTER TABLE `fs_renting` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `fs_renting_detail`
#

DROP TABLE IF EXISTS `fs_renting_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fs_renting_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_FS_RENTING_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_FS_RENTING_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_RENTING_DETAIL_FS_RENTING` FOREIGN KEY (`fs_renting`) REFERENCES `fs_renting` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de la Declaracion de IRPF';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `fs_renting_detail`
#

LOCK TABLES `fs_renting_detail` WRITE;
ALTER TABLE `fs_renting_detail` DISABLE KEYS;
ALTER TABLE `fs_renting_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `fs_vat`
#

DROP TABLE IF EXISTS `fs_vat`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fs_vat` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `period` tinyint(2) default '0' COMMENT 'Periodo de la Declaracion',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `status` tinyint(2) default '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad',
  `complementary` tinyint(1) default '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) default '0' COMMENT 'Declaracion sustitutiva',
  `tax_refund_registry` tinyint(1) default '0' COMMENT 'Inscrito en registro de devolucion',
  `number` int(4) default '0' COMMENT 'Numero de Decl. complementaria o sustitutiva',
  `prorata` double(5,2) default '100.00' COMMENT 'Porcentaje de prorrata',
  PRIMARY KEY  (`id`),
  KEY `IDX_FS_VAT_DOMAIN` (`domain`),
  CONSTRAINT `FK_FS_VAT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaracion de Iva';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `fs_vat`
#

LOCK TABLES `fs_vat` WRITE;
ALTER TABLE `fs_vat` DISABLE KEYS;
ALTER TABLE `fs_vat` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `fs_vat_declaration`
#

DROP TABLE IF EXISTS `fs_vat_declaration`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fs_vat_declaration` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  `rbank` int(4) default NULL COMMENT 'Banco de la Compaia',
  `compensable` tinyint(1) default '0' COMMENT 'Compensar o devolver',
  `status` tinyint(2) NOT NULL default '0' COMMENT 'Estado de la Declaracion',
  PRIMARY KEY  (`id`),
  KEY `IDX_FS_VAT_DECLARATION_FS_VAT` (`fs_vat`),
  KEY `IDX_FS_VAT_DECLARATION_RBANK` (`rbank`),
  KEY `IDX_FS_VAT_DECLARATION_DOMAIN` (`domain`),
  CONSTRAINT `FK_FS_VAT_DECLARATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_VAT_DECLARATION_FS_VAT` FOREIGN KEY (`fs_vat`) REFERENCES `fs_vat` (`id`),
  CONSTRAINT `FK_FS_VAT_DECLARATION_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Resultado de la Declaracion de Iva';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `fs_vat_declaration`
#

LOCK TABLES `fs_vat_declaration` WRITE;
ALTER TABLE `fs_vat_declaration` DISABLE KEYS;
ALTER TABLE `fs_vat_declaration` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `fs_vat_detail`
#

DROP TABLE IF EXISTS `fs_vat_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fs_vat_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_FS_VAT_DETAIL_FS_VAT` (`fs_vat`),
  KEY `IDX_FS_VAT_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_FS_VAT_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_VAT_DETAIL_FS_VAT` FOREIGN KEY (`fs_vat`) REFERENCES `fs_vat` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de Declaracion de Iva';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `fs_vat_detail`
#

LOCK TABLES `fs_vat_detail` WRITE;
ALTER TABLE `fs_vat_detail` DISABLE KEYS;
ALTER TABLE `fs_vat_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `geotree`
#

DROP TABLE IF EXISTS `geotree`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `geotree` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `parent` int(4) default NULL COMMENT 'Identificador de la Zona Geografica Padre',
  `child` int(4) NOT NULL COMMENT 'Identificador de la Zona Geografica Hijo',
  PRIMARY KEY  (`id`),
  KEY `IDX_GEOTREE_PARENT_GEOZONE` (`parent`),
  KEY `IDX_GEOTREE_CHILD_GEOZONE` (`child`),
  KEY `IDX_GEOTREE_DOMAIN` (`domain`),
  CONSTRAINT `FK_GEOTREE_CHILD_GEOZONE` FOREIGN KEY (`child`) REFERENCES `geozone` (`id`),
  CONSTRAINT `FK_GEOTREE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_GEOTREE_PARENT_GEOZONE` FOREIGN KEY (`parent`) REFERENCES `geozone` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Jerarquia de Zonas Geograficas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `geotree`
#

LOCK TABLES `geotree` WRITE;
ALTER TABLE `geotree` DISABLE KEYS;
INSERT INTO `geotree` VALUES (1,1,NULL,53),(2,1,53,1),(3,1,53,2),(4,1,53,3),(5,1,53,4),(6,1,53,5),(7,1,53,6),(8,1,53,7),(9,1,53,8),(10,1,53,9),(11,1,53,10),(12,1,53,11),(13,1,53,12),(14,1,53,13),(15,1,53,14),(16,1,53,15),(17,1,53,16),(18,1,53,17),(19,1,53,18),(20,1,53,19),(21,1,53,20),(22,1,53,21),(23,1,53,22),(24,1,53,23),(25,1,53,24),(26,1,53,25),(27,1,53,26),(28,1,53,27),(29,1,53,28),(30,1,53,29),(31,1,53,30),(32,1,53,31),(33,1,53,32),(34,1,53,33),(35,1,53,34),(36,1,53,35),(37,1,53,36),(38,1,53,37),(39,1,53,38),(40,1,53,39),(41,1,53,40),(42,1,53,41),(43,1,53,42),(44,1,53,43),(45,1,53,44),(46,1,53,45),(47,1,53,46),(48,1,53,47),(49,1,53,48),(50,1,53,49),(51,1,53,50),(52,1,53,51),(53,1,53,52);
ALTER TABLE `geotree` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `geozone`
#

DROP TABLE IF EXISTS `geozone`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `geozone` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Zona Geografica',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Zona Geografica',
  `code` varchar(3) collate latin1_spanish_ci default NULL COMMENT 'Codigo de la Zona Geografica',
  `system` tinyint(1) NOT NULL default '0' COMMENT 'Indica si es una Zona Geografica del sistema',
  PRIMARY KEY  (`id`),
  KEY `IDX_GEOZONE_DOMAIN` (`domain`),
  CONSTRAINT `FK_GEOZONE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Zonas Geograficas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `geozone`
#

LOCK TABLES `geozone` WRITE;
ALTER TABLE `geozone` DISABLE KEYS;
INSERT INTO `geozone` VALUES (1,1,'ARABA/ALAVA','01',1),(2,1,'ALBACETE','02',1),(3,1,'ALICANTE','03',1),(4,1,'ALMERIA','04',1),(5,1,'AVILA','05',1),(6,1,'BADAJOZ','06',1),(7,1,'ILLES BALEARS','07',1),(8,1,'BARCELONA','08',1),(9,1,'BURGOS','09',1),(10,1,'CACERES','10',1),(11,1,'CADIZ','11',1),(12,1,'CASTELLON','12',1),(13,1,'CIUDAD REAL','13',1),(14,1,'CORDOBA','14',1),(15,1,'A CORUA','15',1),(16,1,'CUENCA','16',1),(17,1,'GIRONA','17',1),(18,1,'GRANADA','18',1),(19,1,'GUADALAJARA','19',1),(20,1,'GIPUZKOA','20',1),(21,1,'HUELVA','21',1),(22,1,'HUESCA','22',1),(23,1,'JAEN','23',1),(24,1,'LEON','24',1),(25,1,'LLEIDA','25',1),(26,1,'LA RIOJA','26',1),(27,1,'LUGO','27',1),(28,1,'MADRID','28',1),(29,1,'MALAGA','29',1),(30,1,'MURCIA','30',1),(31,1,'NAVARRA','31',1),(32,1,'OURENSE','32',1),(33,1,'ASTURIAS','33',1),(34,1,'PALENCIA','34',1),(35,1,'LAS PALMAS','35',1),(36,1,'PONTEVEDRA','36',1),(37,1,'SALAMANCA','37',1),(38,1,'TENERIFE','38',1),(39,1,'CANTABRIA','39',1),(40,1,'SEGOVIA','40',1),(41,1,'SEVILLA','41',1),(42,1,'SORIA','42',1),(43,1,'TARRAGONA','43',1),(44,1,'TERUEL','44',1),(45,1,'TOLEDO','45',1),(46,1,'VALENCIA','46',1),(47,1,'VALLADOLID','47',1),(48,1,'BIZKAIA','48',1),(49,1,'ZAMORA','49',1),(50,1,'ZARAGOZA','50',1),(51,1,'CEUTA','51',1),(52,1,'MELILLA','52',1),(53,1,'ESPAA','ES',1);
ALTER TABLE `geozone` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `geozone_irpf`
#

DROP TABLE IF EXISTS `geozone_irpf`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `geozone_irpf` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `geozone_code` varchar(3) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo de la Zona Geografica',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `amount` double(15,3) default '0.000' COMMENT 'Importe rendimiento anual',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=148 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tabla de tramos del IRPF';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `geozone_irpf`
#

LOCK TABLES `geozone_irpf` WRITE;
ALTER TABLE `geozone_irpf` DISABLE KEYS;
INSERT INTO `geozone_irpf` VALUES (1,'01','2011-01-01','2011-12-31',0.000),(2,'20','2011-01-01','2011-12-31',0.000),(3,'48','2011-01-01','2011-12-31',0.000),(4,'01','2011-01-01','2011-12-31',12230.010),(5,'20','2011-01-01','2011-12-31',12230.010),(6,'48','2011-01-01','2011-12-31',12230.010),(7,'01','2011-01-01','2011-12-31',12720.010),(8,'20','2011-01-01','2011-12-31',12720.010),(9,'48','2011-01-01','2011-12-31',12720.010),(10,'01','2011-01-01','2011-12-31',13250.010),(11,'20','2011-01-01','2011-12-31',13250.010),(12,'48','2011-01-01','2011-12-31',13250.010),(13,'01','2011-01-01','2011-12-31',13820.010),(14,'20','2011-01-01','2011-12-31',13820.010),(15,'48','2011-01-01','2011-12-31',13820.010),(16,'01','2011-01-01','2011-12-31',14450.010),(17,'20','2011-01-01','2011-12-31',14450.010),(18,'48','2011-01-01','2011-12-31',14450.010),(19,'01','2011-01-01','2011-12-31',15140.010),(20,'20','2011-01-01','2011-12-31',15140.010),(21,'48','2011-01-01','2011-12-31',15140.010),(22,'01','2011-01-01','2011-12-31',15900.010),(23,'20','2011-01-01','2011-12-31',15900.010),(24,'48','2011-01-01','2011-12-31',15900.010),(25,'01','2011-01-01','2011-12-31',16970.010),(26,'20','2011-01-01','2011-12-31',16970.010),(27,'48','2011-01-01','2011-12-31',16970.010),(28,'01','2011-01-01','2011-12-31',18260.010),(29,'20','2011-01-01','2011-12-31',18260.010),(30,'48','2011-01-01','2011-12-31',18260.010),(31,'01','2011-01-01','2011-12-31',19460.010),(32,'20','2011-01-01','2011-12-31',19460.010),(33,'48','2011-01-01','2011-12-31',19460.010),(34,'01','2011-01-01','2011-12-31',20630.010),(35,'20','2011-01-01','2011-12-31',20630.010),(36,'48','2011-01-01','2011-12-31',20630.010),(37,'01','2011-01-01','2011-12-31',21560.010),(38,'20','2011-01-01','2011-12-31',21560.010),(39,'48','2011-01-01','2011-12-31',21560.010),(40,'01','2011-01-01','2011-12-31',22790.010),(41,'20','2011-01-01','2011-12-31',22790.010),(42,'48','2011-01-01','2011-12-31',22790.010),(43,'01','2011-01-01','2011-12-31',23910.010),(44,'20','2011-01-01','2011-12-31',23910.010),(45,'48','2011-01-01','2011-12-31',23910.010),(46,'01','2011-01-01','2011-12-31',25830.010),(47,'20','2011-01-01','2011-12-31',25830.010),(48,'48','2011-01-01','2011-12-31',25830.010),(49,'01','2011-01-01','2011-12-31',28260.010),(50,'20','2011-01-01','2011-12-31',28260.010),(51,'48','2011-01-01','2011-12-31',28260.010),(52,'01','2011-01-01','2011-12-31',31200.010),(53,'20','2011-01-01','2011-12-31',31200.010),(54,'48','2011-01-01','2011-12-31',31200.010),(55,'01','2011-01-01','2011-12-31',34810.010),(56,'20','2011-01-01','2011-12-31',34810.010),(57,'48','2011-01-01','2011-12-31',34810.010),(58,'01','2011-01-01','2011-12-31',37820.010),(59,'20','2011-01-01','2011-12-31',37820.010),(60,'48','2011-01-01','2011-12-31',37820.010),(61,'01','2011-01-01','2011-12-31',40490.010),(62,'20','2011-01-01','2011-12-31',40490.010),(63,'48','2011-01-01','2011-12-31',40490.010),(64,'01','2011-01-01','2011-12-31',43350.010),(65,'20','2011-01-01','2011-12-31',43350.010),(66,'48','2011-01-01','2011-12-31',43350.010),(67,'01','2011-01-01','2011-12-31',46670.010),(68,'20','2011-01-01','2011-12-31',46670.010),(69,'48','2011-01-01','2011-12-31',46670.010),(70,'01','2011-01-01','2011-12-31',50540.010),(71,'20','2011-01-01','2011-12-31',50540.010),(72,'48','2011-01-01','2011-12-31',50540.010),(73,'01','2011-01-01','2011-12-31',53700.010),(74,'20','2011-01-01','2011-12-31',53700.010),(75,'48','2011-01-01','2011-12-31',53700.010),(76,'01','2011-01-01','2011-12-31',57260.010),(77,'20','2011-01-01','2011-12-31',57260.010),(78,'48','2011-01-01','2011-12-31',57260.010),(79,'01','2011-01-01','2011-12-31',61320.010),(80,'20','2011-01-01','2011-12-31',61320.010),(81,'48','2011-01-01','2011-12-31',61320.010),(82,'01','2011-01-01','2011-12-31',66010.010),(83,'20','2011-01-01','2011-12-31',66010.010),(84,'48','2011-01-01','2011-12-31',66010.010),(85,'01','2011-01-01','2011-12-31',71060.010),(86,'20','2011-01-01','2011-12-31',71060.010),(87,'48','2011-01-01','2011-12-31',71060.010),(88,'01','2011-01-01','2011-12-31',75470.010),(89,'20','2011-01-01','2011-12-31',75470.010),(90,'48','2011-01-01','2011-12-31',75470.010),(91,'01','2011-01-01','2011-12-31',80490.010),(92,'20','2011-01-01','2011-12-31',80490.010),(93,'48','2011-01-01','2011-12-31',80490.010),(94,'01','2011-01-01','2011-12-31',86190.010),(95,'20','2011-01-01','2011-12-31',86190.010),(96,'48','2011-01-01','2011-12-31',86190.010),(97,'01','2011-01-01','2011-12-31',92780.010),(98,'20','2011-01-01','2011-12-31',92780.010),(99,'48','2011-01-01','2011-12-31',92780.010),(100,'01','2011-01-01','2011-12-31',100450.010),(101,'20','2011-01-01','2011-12-31',100450.010),(102,'48','2011-01-01','2011-12-31',100450.010),(103,'01','2011-01-01','2011-12-31',109500.010),(104,'20','2011-01-01','2011-12-31',109500.010),(105,'48','2011-01-01','2011-12-31',109500.010),(106,'01','2011-01-01','2011-12-31',120310.010),(107,'20','2011-01-01','2011-12-31',120310.010),(108,'48','2011-01-01','2011-12-31',120310.010),(109,'01','2011-01-01','2011-12-31',133500.010),(110,'20','2011-01-01','2011-12-31',133500.010),(111,'48','2011-01-01','2011-12-31',133500.010),(112,'01','2011-01-01','2011-12-31',149790.010),(113,'20','2011-01-01','2011-12-31',149790.010),(114,'48','2011-01-01','2011-12-31',149790.010),(115,'01','2011-01-01','2011-12-31',169760.010),(116,'20','2011-01-01','2011-12-31',169760.010),(117,'48','2011-01-01','2011-12-31',169760.010),(118,'01','2011-01-01','2011-12-31',195880.010),(119,'20','2011-01-01','2011-12-31',195880.010),(120,'48','2011-01-01','2011-12-31',195880.010),(121,'01','2011-01-01','2011-12-31',231490.010),(122,'20','2011-01-01','2011-12-31',231490.010),(123,'48','2011-01-01','2011-12-31',231490.010),(124,'31','2011-01-01','2011-12-31',0.000),(125,'31','2011-01-01','2011-12-31',10000.010),(126,'31','2011-01-01','2011-12-31',11250.010),(127,'31','2011-01-01','2011-12-31',12750.010),(128,'31','2011-01-01','2011-12-31',14250.010),(129,'31','2011-01-01','2011-12-31',16750.010),(130,'31','2011-01-01','2011-12-31',19750.010),(131,'31','2011-01-01','2011-12-31',23250.010),(132,'31','2011-01-01','2011-12-31',25750.010),(133,'31','2011-01-01','2011-12-31',28250.010),(134,'31','2011-01-01','2011-12-31',32250.010),(135,'31','2011-01-01','2011-12-31',35750.010),(136,'31','2011-01-01','2011-12-31',41250.010),(137,'31','2011-01-01','2011-12-31',48000.010),(138,'31','2011-01-01','2011-12-31',55000.010),(139,'31','2011-01-01','2011-12-31',62000.010),(140,'31','2011-01-01','2011-12-31',69250.010),(141,'31','2011-01-01','2011-12-31',75250.010),(142,'31','2011-01-01','2011-12-31',82250.010),(143,'31','2011-01-01','2011-12-31',94750.010),(144,'31','2011-01-01','2011-12-31',107250.010),(145,'31','2011-01-01','2011-12-31',120000.010),(146,'31','2011-01-01','2011-12-31',132750.010),(147,'31','2011-01-01','2011-12-31',146000.010);
ALTER TABLE `geozone_irpf` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `geozone_irpf_descendant`
#

DROP TABLE IF EXISTS `geozone_irpf_descendant`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `geozone_irpf_descendant` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `geozone_irpf` int(4) default NULL COMMENT 'Identificador del tramo de IRPF',
  `descendant` tinyint(2) default '0' COMMENT 'Descendientes',
  `percent` double(15,2) default '0.00' COMMENT 'Porcentaje',
  PRIMARY KEY  (`id`),
  KEY `IDX_GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF` (`geozone_irpf`),
  CONSTRAINT `FK_GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF` FOREIGN KEY (`geozone_irpf`) REFERENCES `geozone_irpf` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1126 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tabla de porcentajes IRPF segun descendientes';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `geozone_irpf_descendant`
#

LOCK TABLES `geozone_irpf_descendant` WRITE;
ALTER TABLE `geozone_irpf_descendant` DISABLE KEYS;
INSERT INTO `geozone_irpf_descendant` VALUES (1,1,0,0.00),(2,1,1,0.00),(3,1,2,0.00),(4,1,3,0.00),(5,1,4,0.00),(6,1,5,0.00),(7,1,6,0.00),(8,2,0,0.00),(9,2,1,0.00),(10,2,2,0.00),(11,2,3,0.00),(12,2,4,0.00),(13,2,5,0.00),(14,2,6,0.00),(15,3,0,0.00),(16,3,1,0.00),(17,3,2,0.00),(18,3,3,0.00),(19,3,4,0.00),(20,3,5,0.00),(21,3,6,0.00),(22,4,0,1.00),(23,4,1,0.00),(24,4,2,0.00),(25,4,3,0.00),(26,4,4,0.00),(27,4,5,0.00),(28,4,6,0.00),(29,5,0,1.00),(30,5,1,0.00),(31,5,2,0.00),(32,5,3,0.00),(33,5,4,0.00),(34,5,5,0.00),(35,5,6,0.00),(36,6,0,1.00),(37,6,1,0.00),(38,6,2,0.00),(39,6,3,0.00),(40,6,4,0.00),(41,6,5,0.00),(42,6,6,0.00),(43,7,0,2.00),(44,7,1,0.00),(45,7,2,0.00),(46,7,3,0.00),(47,7,4,0.00),(48,7,5,0.00),(49,7,6,0.00),(50,8,0,2.00),(51,8,1,0.00),(52,8,2,0.00),(53,8,3,0.00),(54,8,4,0.00),(55,8,5,0.00),(56,8,6,0.00),(57,9,0,2.00),(58,9,1,0.00),(59,9,2,0.00),(60,9,3,0.00),(61,9,4,0.00),(62,9,5,0.00),(63,9,6,0.00),(64,10,0,3.00),(65,10,1,1.00),(66,10,2,0.00),(67,10,3,0.00),(68,10,4,0.00),(69,10,5,0.00),(70,10,6,0.00),(71,11,0,3.00),(72,11,1,1.00),(73,11,2,0.00),(74,11,3,0.00),(75,11,4,0.00),(76,11,5,0.00),(77,11,6,0.00),(78,12,0,3.00),(79,12,1,1.00),(80,12,2,0.00),(81,12,3,0.00),(82,12,4,0.00),(83,12,5,0.00),(84,12,6,0.00),(85,13,0,4.00),(86,13,1,2.00),(87,13,2,0.00),(88,13,3,0.00),(89,13,4,0.00),(90,13,5,0.00),(91,13,6,0.00),(92,14,0,4.00),(93,14,1,2.00),(94,14,2,0.00),(95,14,3,0.00),(96,14,4,0.00),(97,14,5,0.00),(98,14,6,0.00),(99,15,0,4.00),(100,15,1,2.00),(101,15,2,0.00),(102,15,3,0.00),(103,15,4,0.00),(104,15,5,0.00),(105,15,6,0.00),(106,16,0,5.00),(107,16,1,3.00),(108,16,2,1.00),(109,16,3,0.00),(110,16,4,0.00),(111,16,5,0.00),(112,16,6,0.00),(113,17,0,5.00),(114,17,1,3.00),(115,17,2,1.00),(116,17,3,0.00),(117,17,4,0.00),(118,17,5,0.00),(119,17,6,0.00),(120,18,0,5.00),(121,18,1,3.00),(122,18,2,1.00),(123,18,3,0.00),(124,18,4,0.00),(125,18,5,0.00),(126,18,6,0.00),(127,19,0,6.00),(128,19,1,4.00),(129,19,2,2.00),(130,19,3,0.00),(131,19,4,0.00),(132,19,5,0.00),(133,19,6,0.00),(134,20,0,6.00),(135,20,1,4.00),(136,20,2,2.00),(137,20,3,0.00),(138,20,4,0.00),(139,20,5,0.00),(140,20,6,0.00),(141,21,0,6.00),(142,21,1,4.00),(143,21,2,2.00),(144,21,3,0.00),(145,21,4,0.00),(146,21,5,0.00),(147,21,6,0.00),(148,22,0,7.00),(149,22,1,5.00),(150,22,2,3.00),(151,22,3,0.00),(152,22,4,0.00),(153,22,5,0.00),(154,22,6,0.00),(155,23,0,7.00),(156,23,1,5.00),(157,23,2,3.00),(158,23,3,0.00),(159,23,4,0.00),(160,23,5,0.00),(161,23,6,0.00),(162,24,0,7.00),(163,24,1,5.00),(164,24,2,3.00),(165,24,3,0.00),(166,24,4,0.00),(167,24,5,0.00),(168,24,6,0.00),(169,25,0,8.00),(170,25,1,6.00),(171,25,2,5.00),(172,25,3,1.00),(173,25,4,0.00),(174,25,5,0.00),(175,25,6,0.00),(176,26,0,8.00),(177,26,1,6.00),(178,26,2,5.00),(179,26,3,1.00),(180,26,4,0.00),(181,26,5,0.00),(182,26,6,0.00),(183,27,0,8.00),(184,27,1,6.00),(185,27,2,5.00),(186,27,3,1.00),(187,27,4,0.00),(188,27,5,0.00),(189,27,6,0.00),(190,28,0,9.00),(191,28,1,8.00),(192,28,2,6.00),(193,28,3,3.00),(194,28,4,0.00),(195,28,5,0.00),(196,28,6,0.00),(197,29,0,9.00),(198,29,1,8.00),(199,29,2,6.00),(200,29,3,3.00),(201,29,4,0.00),(202,29,5,0.00),(203,29,6,0.00),(204,30,0,9.00),(205,30,1,8.00),(206,30,2,6.00),(207,30,3,3.00),(208,30,4,0.00),(209,30,5,0.00),(210,30,6,0.00),(211,31,0,10.00),(212,31,1,9.00),(213,31,2,7.00),(214,31,3,4.00),(215,31,4,1.00),(216,31,5,0.00),(217,31,6,0.00),(218,32,0,10.00),(219,32,1,9.00),(220,32,2,7.00),(221,32,3,4.00),(222,32,4,1.00),(223,32,5,0.00),(224,32,6,0.00),(225,33,0,10.00),(226,33,1,9.00),(227,33,2,7.00),(228,33,3,4.00),(229,33,4,1.00),(230,33,5,0.00),(231,33,6,0.00),(232,34,0,11.00),(233,34,1,10.00),(234,34,2,8.00),(235,34,3,5.00),(236,34,4,2.00),(237,34,5,0.00),(238,34,6,0.00),(239,35,0,11.00),(240,35,1,10.00),(241,35,2,8.00),(242,35,3,5.00),(243,35,4,2.00),(244,35,5,0.00),(245,35,6,0.00),(246,36,0,11.00),(247,36,1,10.00),(248,36,2,8.00),(249,36,3,5.00),(250,36,4,2.00),(251,36,5,0.00),(252,36,6,0.00),(253,37,0,12.00),(254,37,1,11.00),(255,37,2,9.00),(256,37,3,7.00),(257,37,4,3.00),(258,37,5,0.00),(259,37,6,0.00),(260,38,0,12.00),(261,38,1,11.00),(262,38,2,9.00),(263,38,3,7.00),(264,38,4,3.00),(265,38,5,0.00),(266,38,6,0.00),(267,39,0,12.00),(268,39,1,11.00),(269,39,2,9.00),(270,39,3,7.00),(271,39,4,3.00),(272,39,5,0.00),(273,39,6,0.00),(274,40,0,13.00),(275,40,1,12.00),(276,40,2,10.00),(277,40,3,8.00),(278,40,4,4.00),(279,40,5,1.00),(280,40,6,0.00),(281,41,0,13.00),(282,41,1,12.00),(283,41,2,10.00),(284,41,3,8.00),(285,41,4,4.00),(286,41,5,1.00),(287,41,6,0.00),(288,42,0,13.00),(289,42,1,12.00),(290,42,2,10.00),(291,42,3,8.00),(292,42,4,4.00),(293,42,5,1.00),(294,42,6,0.00),(295,43,0,14.00),(296,43,1,13.00),(297,43,2,11.00),(298,43,3,9.00),(299,43,4,6.00),(300,43,5,2.00),(301,43,6,0.00),(302,44,0,14.00),(303,44,1,13.00),(304,44,2,11.00),(305,44,3,9.00),(306,44,4,6.00),(307,44,5,2.00),(308,44,6,0.00),(309,45,0,14.00),(310,45,1,13.00),(311,45,2,11.00),(312,45,3,9.00),(313,45,4,6.00),(314,45,5,2.00),(315,45,6,0.00),(316,46,0,15.00),(317,46,1,14.00),(318,46,2,13.00),(319,46,3,10.00),(320,46,4,8.00),(321,46,5,4.00),(322,46,6,0.00),(323,47,0,15.00),(324,47,1,14.00),(325,47,2,13.00),(326,47,3,10.00),(327,47,4,8.00),(328,47,5,4.00),(329,47,6,0.00),(330,48,0,15.00),(331,48,1,14.00),(332,48,2,13.00),(333,48,3,10.00),(334,48,4,8.00),(335,48,5,4.00),(336,48,6,0.00),(337,49,0,16.00),(338,49,1,15.00),(339,49,2,14.00),(340,49,3,12.00),(341,49,4,9.00),(342,49,5,6.00),(343,49,6,0.00),(344,50,0,16.00),(345,50,1,15.00),(346,50,2,14.00),(347,50,3,12.00),(348,50,4,9.00),(349,50,5,6.00),(350,50,6,0.00),(351,51,0,16.00),(352,51,1,15.00),(353,51,2,14.00),(354,51,3,12.00),(355,51,4,9.00),(356,51,5,6.00),(357,51,6,0.00),(358,52,0,17.00),(359,52,1,16.00),(360,52,2,15.00),(361,52,3,13.00),(362,52,4,11.00),(363,52,5,8.00),(364,52,6,0.00),(365,53,0,17.00),(366,53,1,16.00),(367,53,2,15.00),(368,53,3,13.00),(369,53,4,11.00),(370,53,5,8.00),(371,53,6,0.00),(372,54,0,17.00),(373,54,1,16.00),(374,54,2,15.00),(375,54,3,13.00),(376,54,4,11.00),(377,54,5,8.00),(378,54,6,0.00),(379,55,0,18.00),(380,55,1,17.00),(381,55,2,16.00),(382,55,3,14.00),(383,55,4,12.00),(384,55,5,10.00),(385,55,6,2.00),(386,56,0,18.00),(387,56,1,17.00),(388,56,2,16.00),(389,56,3,14.00),(390,56,4,12.00),(391,56,5,10.00),(392,56,6,2.00),(393,57,0,18.00),(394,57,1,17.00),(395,57,2,16.00),(396,57,3,14.00),(397,57,4,12.00),(398,57,5,10.00),(399,57,6,2.00),(400,58,0,19.00),(401,58,1,18.00),(402,58,2,17.00),(403,58,3,16.00),(404,58,4,14.00),(405,58,5,11.00),(406,58,6,4.00),(407,59,0,19.00),(408,59,1,18.00),(409,59,2,17.00),(410,59,3,16.00),(411,59,4,14.00),(412,59,5,11.00),(413,59,6,4.00),(414,60,0,19.00),(415,60,1,18.00),(416,60,2,17.00),(417,60,3,16.00),(418,60,4,14.00),(419,60,5,11.00),(420,60,6,4.00),(421,61,0,20.00),(422,61,1,19.00),(423,61,2,19.00),(424,61,3,17.00),(425,61,4,15.00),(426,61,5,13.00),(427,61,6,7.00),(428,62,0,20.00),(429,62,1,19.00),(430,62,2,19.00),(431,62,3,17.00),(432,62,4,15.00),(433,62,5,13.00),(434,62,6,7.00),(435,63,0,20.00),(436,63,1,19.00),(437,63,2,19.00),(438,63,3,17.00),(439,63,4,15.00),(440,63,5,13.00),(441,63,6,7.00),(442,64,0,21.00),(443,64,1,20.00),(444,64,2,20.00),(445,64,3,18.00),(446,64,4,17.00),(447,64,5,15.00),(448,64,6,9.00),(449,65,0,21.00),(450,65,1,20.00),(451,65,2,20.00),(452,65,3,18.00),(453,65,4,17.00),(454,65,5,15.00),(455,65,6,9.00),(456,66,0,21.00),(457,66,1,20.00),(458,66,2,20.00),(459,66,3,18.00),(460,66,4,17.00),(461,66,5,15.00),(462,66,6,9.00),(463,67,0,22.00),(464,67,1,21.00),(465,67,2,21.00),(466,67,3,20.00),(467,67,4,18.00),(468,67,5,16.00),(469,67,6,11.00),(470,68,0,22.00),(471,68,1,21.00),(472,68,2,21.00),(473,68,3,20.00),(474,68,4,18.00),(475,68,5,16.00),(476,68,6,11.00),(477,69,0,22.00),(478,69,1,21.00),(479,69,2,21.00),(480,69,3,20.00),(481,69,4,18.00),(482,69,5,16.00),(483,69,6,11.00),(484,70,0,23.00),(485,70,1,22.00),(486,70,2,22.00),(487,70,3,21.00),(488,70,4,19.00),(489,70,5,18.00),(490,70,6,12.00),(491,71,0,23.00),(492,71,1,22.00),(493,71,2,22.00),(494,71,3,21.00),(495,71,4,19.00),(496,71,5,18.00),(497,71,6,12.00),(498,72,0,23.00),(499,72,1,22.00),(500,72,2,22.00),(501,72,3,21.00),(502,72,4,19.00),(503,72,5,18.00),(504,72,6,12.00),(505,73,0,24.00),(506,73,1,23.00),(507,73,2,23.00),(508,73,3,22.00),(509,73,4,21.00),(510,73,5,19.00),(511,73,6,14.00),(512,74,0,24.00),(513,74,1,23.00),(514,74,2,23.00),(515,74,3,22.00),(516,74,4,21.00),(517,74,5,19.00),(518,74,6,14.00),(519,75,0,24.00),(520,75,1,23.00),(521,75,2,23.00),(522,75,3,22.00),(523,75,4,21.00),(524,75,5,19.00),(525,75,6,14.00),(526,76,0,25.00),(527,76,1,25.00),(528,76,2,24.00),(529,76,3,23.00),(530,76,4,22.00),(531,76,5,20.00),(532,76,6,16.00),(533,77,0,25.00),(534,77,1,25.00),(535,77,2,24.00),(536,77,3,23.00),(537,77,4,22.00),(538,77,5,20.00),(539,77,6,16.00),(540,78,0,25.00),(541,78,1,25.00),(542,78,2,24.00),(543,78,3,23.00),(544,78,4,22.00),(545,78,5,20.00),(546,78,6,16.00),(547,79,0,26.00),(548,79,1,26.00),(549,79,2,25.00),(550,79,3,24.00),(551,79,4,23.00),(552,79,5,22.00),(553,79,6,17.00),(554,80,0,26.00),(555,80,1,26.00),(556,80,2,25.00),(557,80,3,24.00),(558,80,4,23.00),(559,80,5,22.00),(560,80,6,17.00),(561,81,0,26.00),(562,81,1,26.00),(563,81,2,25.00),(564,81,3,24.00),(565,81,4,23.00),(566,81,5,22.00),(567,81,6,17.00),(568,82,0,27.00),(569,82,1,27.00),(570,82,2,26.00),(571,82,3,25.00),(572,82,4,24.00),(573,82,5,23.00),(574,82,6,19.00),(575,83,0,27.00),(576,83,1,27.00),(577,83,2,26.00),(578,83,3,25.00),(579,83,4,24.00),(580,83,5,23.00),(581,83,6,19.00),(582,84,0,27.00),(583,84,1,27.00),(584,84,2,26.00),(585,84,3,25.00),(586,84,4,24.00),(587,84,5,23.00),(588,84,6,19.00),(589,85,0,28.00),(590,85,1,28.00),(591,85,2,27.00),(592,85,3,26.00),(593,85,4,25.00),(594,85,5,24.00),(595,85,6,20.00),(596,86,0,28.00),(597,86,1,28.00),(598,86,2,27.00),(599,86,3,26.00),(600,86,4,25.00),(601,86,5,24.00),(602,86,6,20.00),(603,87,0,28.00),(604,87,1,28.00),(605,87,2,27.00),(606,87,3,26.00),(607,87,4,25.00),(608,87,5,24.00),(609,87,6,20.00),(610,88,0,29.00),(611,88,1,29.00),(612,88,2,28.00),(613,88,3,27.00),(614,88,4,27.00),(615,88,5,25.00),(616,88,6,22.00),(617,89,0,29.00),(618,89,1,29.00),(619,89,2,28.00),(620,89,3,27.00),(621,89,4,27.00),(622,89,5,25.00),(623,89,6,22.00),(624,90,0,29.00),(625,90,1,29.00),(626,90,2,28.00),(627,90,3,27.00),(628,90,4,27.00),(629,90,5,25.00),(630,90,6,22.00),(631,91,0,30.00),(632,91,1,30.00),(633,91,2,29.00),(634,91,3,29.00),(635,91,4,28.00),(636,91,5,27.00),(637,91,6,23.00),(638,92,0,30.00),(639,92,1,30.00),(640,92,2,29.00),(641,92,3,29.00),(642,92,4,28.00),(643,92,5,27.00),(644,92,6,23.00),(645,93,0,30.00),(646,93,1,30.00),(647,93,2,29.00),(648,93,3,29.00),(649,93,4,28.00),(650,93,5,27.00),(651,93,6,23.00),(652,94,0,31.00),(653,94,1,31.00),(654,94,2,30.00),(655,94,3,30.00),(656,94,4,29.00),(657,94,5,28.00),(658,94,6,25.00),(659,95,0,31.00),(660,95,1,31.00),(661,95,2,30.00),(662,95,3,30.00),(663,95,4,29.00),(664,95,5,28.00),(665,95,6,25.00),(666,96,0,31.00),(667,96,1,31.00),(668,96,2,30.00),(669,96,3,30.00),(670,96,4,29.00),(671,96,5,28.00),(672,96,6,25.00),(673,97,0,32.00),(674,97,1,32.00),(675,97,2,31.00),(676,97,3,31.00),(677,97,4,30.00),(678,97,5,29.00),(679,97,6,26.00),(680,98,0,32.00),(681,98,1,32.00),(682,98,2,31.00),(683,98,3,31.00),(684,98,4,30.00),(685,98,5,29.00),(686,98,6,26.00),(687,99,0,32.00),(688,99,1,32.00),(689,99,2,31.00),(690,99,3,31.00),(691,99,4,30.00),(692,99,5,29.00),(693,99,6,26.00),(694,100,0,33.00),(695,100,1,33.00),(696,100,2,32.00),(697,100,3,32.00),(698,100,4,31.00),(699,100,5,30.00),(700,100,6,28.00),(701,101,0,33.00),(702,101,1,33.00),(703,101,2,32.00),(704,101,3,32.00),(705,101,4,31.00),(706,101,5,30.00),(707,101,6,28.00),(708,102,0,33.00),(709,102,1,33.00),(710,102,2,32.00),(711,102,3,32.00),(712,102,4,31.00),(713,102,5,30.00),(714,102,6,28.00),(715,103,0,34.00),(716,103,1,34.00),(717,103,2,33.00),(718,103,3,33.00),(719,103,4,32.00),(720,103,5,32.00),(721,103,6,29.00),(722,104,0,34.00),(723,104,1,34.00),(724,104,2,33.00),(725,104,3,33.00),(726,104,4,32.00),(727,104,5,32.00),(728,104,6,29.00),(729,105,0,34.00),(730,105,1,34.00),(731,105,2,33.00),(732,105,3,33.00),(733,105,4,32.00),(734,105,5,32.00),(735,105,6,29.00),(736,106,0,35.00),(737,106,1,35.00),(738,106,2,35.00),(739,106,3,34.00),(740,106,4,34.00),(741,106,5,33.00),(742,106,6,31.00),(743,107,0,35.00),(744,107,1,35.00),(745,107,2,35.00),(746,107,3,34.00),(747,107,4,34.00),(748,107,5,33.00),(749,107,6,31.00),(750,108,0,35.00),(751,108,1,35.00),(752,108,2,35.00),(753,108,3,34.00),(754,108,4,34.00),(755,108,5,33.00),(756,108,6,31.00),(757,109,0,36.00),(758,109,1,36.00),(759,109,2,36.00),(760,109,3,35.00),(761,109,4,35.00),(762,109,5,34.00),(763,109,6,32.00),(764,110,0,36.00),(765,110,1,36.00),(766,110,2,36.00),(767,110,3,35.00),(768,110,4,35.00),(769,110,5,34.00),(770,110,6,32.00),(771,111,0,36.00),(772,111,1,36.00),(773,111,2,36.00),(774,111,3,35.00),(775,111,4,35.00),(776,111,5,34.00),(777,111,6,32.00),(778,112,0,37.00),(779,112,1,37.00),(780,112,2,37.00),(781,112,3,36.00),(782,112,4,36.00),(783,112,5,35.00),(784,112,6,34.00),(785,113,0,37.00),(786,113,1,37.00),(787,113,2,37.00),(788,113,3,36.00),(789,113,4,36.00),(790,113,5,35.00),(791,113,6,34.00),(792,114,0,37.00),(793,114,1,37.00),(794,114,2,37.00),(795,114,3,36.00),(796,114,4,36.00),(797,114,5,35.00),(798,114,6,34.00),(799,115,0,38.00),(800,115,1,38.00),(801,115,2,38.00),(802,115,3,37.00),(803,115,4,37.00),(804,115,5,36.00),(805,115,6,35.00),(806,116,0,38.00),(807,116,1,38.00),(808,116,2,38.00),(809,116,3,37.00),(810,116,4,37.00),(811,116,5,36.00),(812,116,6,35.00),(813,117,0,38.00),(814,117,1,38.00),(815,117,2,38.00),(816,117,3,37.00),(817,117,4,37.00),(818,117,5,36.00),(819,117,6,35.00),(820,118,0,39.00),(821,118,1,39.00),(822,118,2,39.00),(823,118,3,38.00),(824,118,4,38.00),(825,118,5,38.00),(826,118,6,36.00),(827,119,0,39.00),(828,119,1,39.00),(829,119,2,39.00),(830,119,3,38.00),(831,119,4,38.00),(832,119,5,38.00),(833,119,6,36.00),(834,120,0,39.00),(835,120,1,39.00),(836,120,2,39.00),(837,120,3,38.00),(838,120,4,38.00),(839,120,5,38.00),(840,120,6,36.00),(841,121,0,40.00),(842,121,1,40.00),(843,121,2,40.00),(844,121,3,40.00),(845,121,4,39.00),(846,121,5,39.00),(847,121,6,38.00),(848,122,0,40.00),(849,122,1,40.00),(850,122,2,40.00),(851,122,3,40.00),(852,122,4,39.00),(853,122,5,39.00),(854,122,6,38.00),(855,123,0,40.00),(856,123,1,40.00),(857,123,2,40.00),(858,123,3,40.00),(859,123,4,39.00),(860,123,5,39.00),(861,123,6,38.00),(862,124,0,0.00),(863,124,1,0.00),(864,124,2,0.00),(865,124,3,0.00),(866,124,4,0.00),(867,124,5,0.00),(868,124,6,0.00),(869,124,7,0.00),(870,124,8,0.00),(871,124,9,0.00),(872,124,10,0.00),(873,125,0,2.00),(874,125,1,0.00),(875,125,2,0.00),(876,125,3,0.00),(877,125,4,0.00),(878,125,5,0.00),(879,125,6,0.00),(880,125,7,0.00),(881,125,8,0.00),(882,125,9,0.00),(883,125,10,0.00),(884,126,0,4.00),(885,126,1,2.00),(886,126,2,0.00),(887,126,3,0.00),(888,126,4,0.00),(889,126,5,0.00),(890,126,6,0.00),(891,126,7,0.00),(892,126,8,0.00),(893,126,9,0.00),(894,126,10,0.00),(895,127,0,6.00),(896,127,1,4.00),(897,127,2,2.00),(898,127,3,0.00),(899,127,4,0.00),(900,127,5,0.00),(901,127,6,0.00),(902,127,7,0.00),(903,127,8,0.00),(904,127,9,0.00),(905,127,10,0.00),(906,128,0,8.00),(907,128,1,6.00),(908,128,2,4.00),(909,128,3,2.00),(910,128,4,0.00),(911,128,5,0.00),(912,128,6,0.00),(913,128,7,0.00),(914,128,8,0.00),(915,128,9,0.00),(916,128,10,0.00),(917,129,0,10.00),(918,129,1,8.00),(919,129,2,6.00),(920,129,3,4.00),(921,129,4,1.00),(922,129,5,0.00),(923,129,6,0.00),(924,129,7,0.00),(925,129,8,0.00),(926,129,9,0.00),(927,129,10,0.00),(928,130,0,12.00),(929,130,1,11.00),(930,130,2,9.50),(931,130,3,8.00),(932,130,4,6.00),(933,130,5,4.00),(934,130,6,0.00),(935,130,7,0.00),(936,130,8,0.00),(937,130,9,0.00),(938,130,10,0.00),(939,131,0,13.50),(940,131,1,12.00),(941,131,2,11.50),(942,131,3,9.00),(943,131,4,8.00),(944,131,5,6.00),(945,131,6,4.00),(946,131,7,0.00),(947,131,8,0.00),(948,131,9,0.00),(949,131,10,0.00),(950,132,0,14.50),(951,132,1,13.00),(952,132,2,12.50),(953,132,3,10.00),(954,132,4,9.50),(955,132,5,8.00),(956,132,6,6.00),(957,132,7,4.00),(958,132,8,1.00),(959,132,9,0.00),(960,132,10,0.00),(961,133,0,15.50),(962,133,1,14.00),(963,133,2,13.50),(964,133,3,12.00),(965,133,4,10.50),(966,133,5,9.00),(967,133,6,8.00),(968,133,7,6.00),(969,133,8,4.00),(970,133,9,1.00),(971,133,10,0.00),(972,134,0,16.50),(973,134,1,15.00),(974,134,2,14.50),(975,134,3,13.00),(976,134,4,12.50),(977,134,5,11.00),(978,134,6,10.00),(979,134,7,8.00),(980,134,8,7.00),(981,134,9,5.00),(982,134,10,2.00),(983,135,0,17.50),(984,135,1,16.50),(985,135,2,16.50),(986,135,3,14.00),(987,135,4,13.50),(988,135,5,13.00),(989,135,6,12.00),(990,135,7,10.00),(991,135,8,9.00),(992,135,9,7.00),(993,135,10,6.00),(994,136,0,18.50),(995,136,1,17.50),(996,136,2,17.50),(997,136,3,16.00),(998,136,4,15.50),(999,136,5,15.00),(1000,136,6,13.00),(1001,136,7,12.00),(1002,136,8,11.00),(1003,136,9,10.00),(1004,136,10,9.00),(1005,137,0,20.50),(1006,137,1,20.50),(1007,137,2,20.00),(1008,137,3,18.00),(1009,137,4,17.50),(1010,137,5,17.00),(1011,137,6,16.00),(1012,137,7,15.00),(1013,137,8,14.00),(1014,137,9,13.00),(1015,137,10,12.00),(1016,138,0,23.00),(1017,138,1,22.50),(1018,138,2,22.00),(1019,138,3,20.50),(1020,138,4,20.50),(1021,138,5,20.00),(1022,138,6,19.00),(1023,138,7,18.00),(1024,138,8,17.00),(1025,138,9,16.00),(1026,138,10,14.50),(1027,139,0,25.00),(1028,139,1,24.50),(1029,139,2,23.50),(1030,139,3,23.50),(1031,139,4,22.50),(1032,139,5,22.00),(1033,139,6,21.00),(1034,139,7,20.50),(1035,139,8,19.00),(1036,139,9,18.00),(1037,139,10,16.50),(1038,140,0,27.00),(1039,140,1,26.50),(1040,140,2,26.00),(1041,140,3,25.50),(1042,140,4,24.00),(1043,140,5,24.00),(1044,140,6,23.50),(1045,140,7,22.00),(1046,140,8,21.00),(1047,140,9,19.50),(1048,140,10,18.50),(1049,141,0,28.00),(1050,141,1,28.00),(1051,141,2,27.00),(1052,141,3,26.00),(1053,141,4,26.00),(1054,141,5,25.00),(1055,141,6,25.00),(1056,141,7,23.50),(1057,141,8,22.50),(1058,141,9,21.50),(1059,141,10,20.50),(1060,142,0,29.00),(1061,142,1,29.00),(1062,142,2,29.00),(1063,142,3,28.00),(1064,142,4,28.00),(1065,142,5,27.00),(1066,142,6,26.50),(1067,142,7,25.50),(1068,142,8,24.50),(1069,142,9,23.50),(1070,142,10,23.00),(1071,143,0,30.00),(1072,143,1,30.00),(1073,143,2,30.00),(1074,143,3,29.00),(1075,143,4,29.00),(1076,143,5,29.00),(1077,143,6,28.00),(1078,143,7,27.50),(1079,143,8,26.50),(1080,143,9,25.50),(1081,143,10,24.50),(1082,144,0,32.00),(1083,144,1,32.00),(1084,144,2,32.00),(1085,144,3,31.00),(1086,144,4,30.50),(1087,144,5,30.00),(1088,144,6,29.50),(1089,144,7,28.50),(1090,144,8,28.00),(1091,144,9,27.00),(1092,144,10,26.00),(1093,145,0,33.00),(1094,145,1,33.00),(1095,145,2,32.50),(1096,145,3,32.00),(1097,145,4,31.50),(1098,145,5,31.00),(1099,145,6,30.50),(1100,145,7,29.50),(1101,145,8,29.00),(1102,145,9,28.00),(1103,145,10,27.50),(1104,146,0,33.50),(1105,146,1,33.50),(1106,146,2,33.00),(1107,146,3,32.50),(1108,146,4,32.00),(1109,146,5,31.50),(1110,146,6,31.00),(1111,146,7,30.50),(1112,146,8,30.00),(1113,146,9,29.00),(1114,146,10,28.50),(1115,147,0,34.00),(1116,147,1,34.00),(1117,147,2,34.00),(1118,147,3,33.50),(1119,147,4,33.00),(1120,147,5,32.50),(1121,147,6,32.00),(1122,147,7,31.50),(1123,147,8,31.00),(1124,147,9,30.50),(1125,147,10,30.00);
ALTER TABLE `geozone_irpf_descendant` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `geozone_irpf_handicap`
#

DROP TABLE IF EXISTS `geozone_irpf_handicap`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `geozone_irpf_handicap` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `geozone_irpf` int(4) default NULL COMMENT 'Identificador del tramo de IRPF',
  `handicap` tinyint(2) default '0' COMMENT 'Grado Minusvalia',
  `percent` double(15,2) default '0.00' COMMENT 'Porcentaje',
  PRIMARY KEY  (`id`),
  KEY `IDX_GEOZONE_IRPF_HANDICAP_GEOZONE_IRPF` (`geozone_irpf`),
  CONSTRAINT `FK_GEOZONE_IRPF_HANDICAP_GEOZONE_IRPF` FOREIGN KEY (`geozone_irpf`) REFERENCES `geozone_irpf` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tabla de  ';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `geozone_irpf_handicap`
#

LOCK TABLES `geozone_irpf_handicap` WRITE;
ALTER TABLE `geozone_irpf_handicap` DISABLE KEYS;
INSERT INTO `geozone_irpf_handicap` VALUES (1,40,0,6.00),(2,40,1,11.00),(3,40,2,11.00),(4,41,0,6.00),(5,41,1,11.00),(6,41,2,11.00),(7,42,0,6.00),(8,42,1,11.00),(9,42,2,11.00),(10,49,0,5.00),(11,49,1,10.00),(12,49,2,10.00),(13,50,0,5.00),(14,50,1,10.00),(15,50,2,10.00),(16,51,0,5.00),(17,51,1,10.00),(18,51,2,10.00),(19,61,0,4.00),(20,61,1,8.00),(21,61,2,8.00),(22,62,0,4.00),(23,62,1,8.00),(24,62,2,8.00),(25,63,0,4.00),(26,63,1,8.00),(27,63,2,8.00),(28,76,0,3.00),(29,76,1,6.00),(30,76,2,6.00),(31,77,0,3.00),(32,77,1,6.00),(33,77,2,6.00),(34,78,0,3.00),(35,78,1,6.00),(36,78,2,6.00),(37,94,0,2.00),(38,94,1,5.00),(39,94,2,5.00),(40,95,0,2.00),(41,95,1,5.00),(42,95,2,5.00),(43,96,0,2.00),(44,96,1,5.00),(45,96,2,5.00),(46,112,0,1.00),(47,112,1,3.00),(48,112,2,3.00),(49,113,0,1.00),(50,113,1,3.00),(51,113,2,3.00),(52,114,0,1.00),(53,114,1,3.00),(54,114,2,3.00),(55,124,0,0.00),(56,124,1,0.00),(57,124,2,0.00),(58,125,0,5.00),(59,125,1,5.00),(60,125,2,15.00),(61,131,0,3.00),(62,131,1,3.00),(63,131,2,15.00),(64,136,0,2.00),(65,136,1,2.00),(66,136,2,8.00),(67,143,0,2.00),(68,143,1,2.00),(69,143,2,5.00);
ALTER TABLE `geozone_irpf_handicap` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `holiday`
#

DROP TABLE IF EXISTS `holiday`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `holiday` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de la Festividad',
  `holiday` int(4) default NULL COMMENT 'Identificador de Festividad',
  `editable` tinyint(1) default '0' COMMENT 'Indica si es editable o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_HOLIDAY_HOLIDAY` (`holiday`),
  KEY `IDX_HOLIDAY_DOMAIN` (`domain`),
  CONSTRAINT `FK_HOLIDAY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_HOLIDAY_HOLIDAY` FOREIGN KEY (`holiday`) REFERENCES `holiday` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Festividades';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `holiday`
#

LOCK TABLES `holiday` WRITE;
ALTER TABLE `holiday` DISABLE KEYS;
ALTER TABLE `holiday` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `holiday_detail`
#

DROP TABLE IF EXISTS `holiday_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `holiday_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `holiday` int(4) NOT NULL COMMENT 'Identificador de Festividad',
  `date` date NOT NULL COMMENT 'Fecha Festiva',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de Festividad',
  PRIMARY KEY  (`id`),
  KEY `IDX_HOLIDAY_DETAIL_HOLIDAY` (`holiday`),
  KEY `IDX_HOLIDAY_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_HOLIDAY_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_HOLIDAY_DETAIL_HOLIDAY` FOREIGN KEY (`holiday`) REFERENCES `holiday` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de Festividades';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `holiday_detail`
#

LOCK TABLES `holiday_detail` WRITE;
ALTER TABLE `holiday_detail` DISABLE KEYS;
ALTER TABLE `holiday_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `hotel`
#

DROP TABLE IF EXISTS `hotel`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `hotel` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `code` varchar(16) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo del Hotel',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `customer` int(4) default NULL COMMENT 'Identificador del Cliente',
  `service_catalogue` int(4) default NULL COMMENT 'Identificador del Catalogo de Servicios',
  `active` tinyint(1) default '1' COMMENT 'Indica si el Hotel esta activo o no',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_UNQ_HOTEL_CODE` (`code`),
  KEY `IDX_HOTEL_SCOPE` (`scope`),
  KEY `IDX_HOTEL_WORKPLACE` (`workplace`),
  KEY `IDX_HOTEL_CUSTOMER` (`customer`),
  KEY `IDX_HOTEL_DOMAIN` (`domain`),
  KEY `IDX_HOTEL_SERVICE_CATALOGUE` (`service_catalogue`),
  CONSTRAINT `FK_HOTEL_SERVICE_CATALOGUE` FOREIGN KEY (`service_catalogue`) REFERENCES `catalogue` (`id`),
  CONSTRAINT `FK_HOTEL_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_HOTEL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_HOTEL_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_HOTEL_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Hoteles';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `hotel`
#

LOCK TABLES `hotel` WRITE;
ALTER TABLE `hotel` DISABLE KEYS;
ALTER TABLE `hotel` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `iattach`
#

DROP TABLE IF EXISTS `iattach`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `iattach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Archivo Adjunto del Articulo',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador de Articulo',
  `mimeType` tinyint(2) default NULL COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Archivo Adjunto',
  PRIMARY KEY  (`id`),
  KEY `IDX_IATTACH_ITEM` (`item`),
  KEY `IDX_IATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_IATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_IATTACH_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Articulos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `iattach`
#

LOCK TABLES `iattach` WRITE;
ALTER TABLE `iattach` DISABLE KEYS;
ALTER TABLE `iattach` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `income`
#

DROP TABLE IF EXISTS `income`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `income` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Albaran de Compra',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  UNIQUE KEY `IDX_UNQ_INCOME_DOMAIN_SUPPLIER_SERIES_NUMBER` (`domain`,`supplier`,`series`,`number`),
  KEY `IDX_INCOME_WORKPLACE` (`workplace`),
  KEY `IDX_INCOME_SCOPE` (`scope`),
  KEY `IDX_INCOME_BANK` (`bank`),
  KEY `IDX_INCOME_PROJECT` (`project`),
  KEY `IDX_INCOME_SUPPLIER` (`supplier`),
  KEY `IDX_INCOME_RADDRESS` (`address`),
  KEY `IDX_INCOME_PAY_METHOD` (`pay_method`),
  KEY `IDX_INCOME_DOMAIN` (`domain`),
  CONSTRAINT `FK_INCOME_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`),
  CONSTRAINT `FK_INCOME_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INCOME_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_INCOME_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_INCOME_RADDRESS` FOREIGN KEY (`address`) REFERENCES `raddress` (`id`),
  CONSTRAINT `FK_INCOME_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_INCOME_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`),
  CONSTRAINT `FK_INCOME_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Albaranes de Compra';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `income`
#

LOCK TABLES `income` WRITE;
ALTER TABLE `income` DISABLE KEYS;
ALTER TABLE `income` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `income_detail`
#

DROP TABLE IF EXISTS `income_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `income_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle del Albaran de Compra',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `income_detail`
#

LOCK TABLES `income_detail` WRITE;
ALTER TABLE `income_detail` DISABLE KEYS;
ALTER TABLE `income_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `instructor`
#

DROP TABLE IF EXISTS `instructor`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `instructor` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro del Empleado',
  `social_security_num` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nmero de Seguridad Social del Empleado',
  `agreement_time` int(4) default '0' COMMENT 'Horas del Convenio',
  `active` tinyint(1) default NULL COMMENT 'Indica si el Empleado sigue vinculado a la Empresa o no',
  PRIMARY KEY  (`registry`),
  UNIQUE KEY `social_security_num` (`social_security_num`),
  CONSTRAINT `instructor_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Empleados';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `instructor`
#

LOCK TABLES `instructor` WRITE;
ALTER TABLE `instructor` DISABLE KEYS;
ALTER TABLE `instructor` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `inventory`
#

DROP TABLE IF EXISTS `inventory`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `inventory` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Inventario',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `inventory_date` date NOT NULL default '0000-00-00' COMMENT 'Fecha de Inventario',
  `warehouse` int(4) NOT NULL default '0' COMMENT 'Almacen Inventariado',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Inventario',
  PRIMARY KEY  (`id`),
  KEY `IDX_INVENTORY_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVENTORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Inventarios de Almacenes';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `inventory`
#

LOCK TABLES `inventory` WRITE;
ALTER TABLE `inventory` DISABLE KEYS;
ALTER TABLE `inventory` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `inventory_detail`
#

DROP TABLE IF EXISTS `inventory_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `inventory_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle del Inventario',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `inventory` int(4) NOT NULL default '0' COMMENT 'Identificador de Inventario',
  `item` int(4) NOT NULL default '0' COMMENT 'Articulo Inventariado',
  `actual_quantity` double(15,3) default '0.000' COMMENT 'Cantidad actual del Articulo Inventariado',
  `real_quantity` double(15,3) default '0.000' COMMENT 'Cantidad real del Articulo Inventariado',
  `cost` double(15,3) default '0.000' COMMENT 'Coste del Articulo Inventariado',
  PRIMARY KEY  (`id`),
  KEY `IDX_INVENTORY_DETAIL_INVENTORY` (`inventory`),
  KEY `IDX_INVENTORY_DETAIL_ITEM` (`item`),
  KEY `IDX_INVENTORY_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVENTORY_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVENTORY_DETAIL_INVENTORY` FOREIGN KEY (`inventory`) REFERENCES `inventory` (`id`),
  CONSTRAINT `FK_INVENTORY_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Inventarios de Almacenes';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `inventory_detail`
#

LOCK TABLES `inventory_detail` WRITE;
ALTER TABLE `inventory_detail` DISABLE KEYS;
ALTER TABLE `inventory_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `invoice`
#

DROP TABLE IF EXISTS `invoice`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `invoice` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Factura',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  UNIQUE KEY `IDX_UNQ_INVOICE_DOMAIN_SERIES_NUMBER_TYPE` (`domain`,`series`,`number`,`type`),
  KEY `IDX_INVOICE_SCOPE` (`scope`),
  KEY `IDX_INVOICE_INVOICE` (`rectification_invoice`),
  KEY `IDX_INVOICE_PROJECT` (`project`),
  KEY `IDX_INVOICE_ISSUE_DATE` (`issue_date`),
  KEY `IDX_INVOICE_TAX_DATE` (`tax_date`),
  KEY `IDX_INVOICE_REGISTRY` (`registry`),
  KEY `IDX_INVOICE_RADDRESS` (`raddress`),
  KEY `IDX_INVOICE_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_INVOICE` FOREIGN KEY (`rectification_invoice`) REFERENCES `invoice` (`id`),
  CONSTRAINT `FK_INVOICE_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_INVOICE_RADDRESS` FOREIGN KEY (`raddress`) REFERENCES `raddress` (`id`),
  CONSTRAINT `FK_INVOICE_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_INVOICE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Facturas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `invoice`
#

LOCK TABLES `invoice` WRITE;
ALTER TABLE `invoice` DISABLE KEYS;
ALTER TABLE `invoice` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `invoice_address`
#

DROP TABLE IF EXISTS `invoice_address`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `invoice_address` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `invoice` int(4) NOT NULL COMMENT 'Identificador de la Factura',
  `street_type` varchar(2) collate latin1_spanish_ci default 'CL' COMMENT 'Tipo de via',
  `address` varchar(45) collate latin1_spanish_ci default NULL COMMENT 'Primera parte de la Direccion',
  `number` varchar(12) collate latin1_spanish_ci default NULL COMMENT 'Numero',
  `address2` varchar(45) collate latin1_spanish_ci default NULL COMMENT 'Segunda parte de la Direccion',
  `zip` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo Postal',
  `city` varchar(45) collate latin1_spanish_ci default NULL COMMENT 'Localidad',
  `province` varchar(45) collate latin1_spanish_ci default NULL COMMENT 'Provincia',
  `geozone` int(4) default NULL COMMENT 'Identificador de la Zona Geografica',
  PRIMARY KEY  (`id`),
  KEY `IDX_INVOICE_ADDRESS_INVOICE` (`invoice`),
  KEY `IDX_INVOICE_ADDRESS_GEOZONE` (`geozone`),
  KEY `IDX_INVOICE_ADDRESS_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICE_ADDRESS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_ADDRESS_GEOZONE` FOREIGN KEY (`geozone`) REFERENCES `geozone` (`id`),
  CONSTRAINT `FK_INVOICE_ADDRESS_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Direcciones de la Factura';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `invoice_address`
#

LOCK TABLES `invoice_address` WRITE;
ALTER TABLE `invoice_address` DISABLE KEYS;
ALTER TABLE `invoice_address` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `invoice_attach`
#

DROP TABLE IF EXISTS `invoice_attach`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `invoice_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `invoice` int(4) NOT NULL COMMENT 'Identificador de la Factura',
  `mimeType` tinyint(2) default '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  PRIMARY KEY  (`id`),
  KEY `IDX_INVOICE_ATTACH_INVOICE` (`invoice`),
  KEY `IDX_INVOICE_ATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICE_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_ATTACH_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Facturas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `invoice_attach`
#

LOCK TABLES `invoice_attach` WRITE;
ALTER TABLE `invoice_attach` DISABLE KEYS;
ALTER TABLE `invoice_attach` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `invoice_detail`
#

DROP TABLE IF EXISTS `invoice_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `invoice_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle de la Factura',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `invoice` int(4) NOT NULL default '0' COMMENT 'Identificador de la Factura',
  `project` int(4) default NULL COMMENT 'Identificador del Proyecto',
  `line` smallint(2) default '1' COMMENT 'Numero de lnea del Detalle dentro de la Factura',
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
  KEY `IDX_INVOICE_DETAIL_PROJECT` (`project`),
  KEY `IDX_INVOICE_DETAIL_WAREHOUSE` (`warehouse`),
  KEY `IDX_INVOICE_DETAIL_SOURCE_ID` (`source_id`),
  KEY `IDX_INVOICE_DETAIL_INVOICE` (`invoice`),
  KEY `IDX_INVOICE_DETAIL_ITEM` (`item`),
  KEY `IDX_INVOICE_DETAIL_WORKPLACE` (`workplace`),
  KEY `IDX_INVOICE_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_DETAIL_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`),
  CONSTRAINT `FK_INVOICE_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_INVOICE_DETAIL_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_INVOICE_DETAIL_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`),
  CONSTRAINT `FK_INVOICE_DETAIL_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de la Factura';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `invoice_detail`
#

LOCK TABLES `invoice_detail` WRITE;
ALTER TABLE `invoice_detail` DISABLE KEYS;
ALTER TABLE `invoice_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `invoice_detail_account`
#

DROP TABLE IF EXISTS `invoice_detail_account`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `invoice_detail_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `invoice_detail` int(4) NOT NULL default '0' COMMENT 'Identificador de la Linea de Factura',
  `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `IDX_INVOICE_DETAIL_ACCOUNT_ACCOUNT` (`account`),
  KEY `IDX_INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL` (`invoice_detail`),
  KEY `IDX_INVOICE_DETAIL_ACCOUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICE_DETAIL_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_INVOICE_DETAIL_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL` FOREIGN KEY (`invoice_detail`) REFERENCES `invoice_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables asociadas a Lineas de Facturas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `invoice_detail_account`
#

LOCK TABLES `invoice_detail_account` WRITE;
ALTER TABLE `invoice_detail_account` DISABLE KEYS;
ALTER TABLE `invoice_detail_account` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `invoice_tax`
#

DROP TABLE IF EXISTS `invoice_tax`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `invoice_tax` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Impuesto de la Factura',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_INVOICE_TAX_INVOICE_DETAIL` (`invoice_detail`),
  KEY `IDX_INVOICE_TAX_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICE_TAX_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_TAX_INVOICE_DETAIL` FOREIGN KEY (`invoice_detail`) REFERENCES `invoice_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Impuestos del Detalle de la Factura';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `invoice_tax`
#

LOCK TABLES `invoice_tax` WRITE;
ALTER TABLE `invoice_tax` DISABLE KEYS;
ALTER TABLE `invoice_tax` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `invoice_tax_account`
#

DROP TABLE IF EXISTS `invoice_tax_account`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `invoice_tax_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `invoice_tax` int(4) NOT NULL default '0' COMMENT 'Identificador de la Linea de Impuesto',
  `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `IDX_INVOICE_TAX_ACCOUNT_ACCOUNT` (`account`),
  KEY `IDX_INVOICE_TAX_ACCOUNT_INVOICE_TAX` (`invoice_tax`),
  KEY `IDX_INVOICE_TAX_ACCOUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICE_TAX_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_INVOICE_TAX_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_TAX_ACCOUNT_INVOICE_TAX` FOREIGN KEY (`invoice_tax`) REFERENCES `invoice_tax` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables asociadas a Impuestos de Facturas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `invoice_tax_account`
#

LOCK TABLES `invoice_tax_account` WRITE;
ALTER TABLE `invoice_tax_account` DISABLE KEYS;
ALTER TABLE `invoice_tax_account` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `invoicing_group`
#

DROP TABLE IF EXISTS `invoicing_group`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `invoicing_group` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `parent` int(4) NOT NULL COMMENT 'Grupo de Facturacion',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_INVOICING_GROUP_REGISTRY` (`parent`),
  KEY `IDX_INVOICING_GROUP_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICING_GROUP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICING_GROUP_REGISTRY` FOREIGN KEY (`parent`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Grupos de Facturacion';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `invoicing_group`
#

LOCK TABLES `invoicing_group` WRITE;
ALTER TABLE `invoicing_group` DISABLE KEYS;
ALTER TABLE `invoicing_group` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `invoicing_group_detail`
#

DROP TABLE IF EXISTS `invoicing_group_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `invoicing_group_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `invoicing_group` int(4) NOT NULL COMMENT 'Grupo de Facturacion al que pertenece',
  `child` int(4) NOT NULL COMMENT 'Componente asociado a un Grupo de Facturacion',
  `grouped` tinyint(1) default '0' COMMENT 'Indica si agrupa facturas o no',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_INVOICING_GROUP_DETAIL_REGISTRY` (`child`),
  KEY `IDX_INVOICING_GROUP_DETAIL_INVOICING_GROUP` (`invoicing_group`),
  KEY `IDX_INVOICING_GROUP_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICING_GROUP_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICING_GROUP_DETAIL_INVOICING_GROUP` FOREIGN KEY (`invoicing_group`) REFERENCES `invoicing_group` (`id`),
  CONSTRAINT `FK_INVOICING_GROUP_DETAIL_REGISTRY` FOREIGN KEY (`child`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de los Grupos de Facturacion';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `invoicing_group_detail`
#

LOCK TABLES `invoicing_group_detail` WRITE;
ALTER TABLE `invoicing_group_detail` DISABLE KEYS;
ALTER TABLE `invoicing_group_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `irpf_data`
#

DROP TABLE IF EXISTS `irpf_data`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `irpf_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  `issue_date` date NOT NULL COMMENT 'Fecha de emisin',
  `annual_remuneration` double(15,3) default NULL COMMENT 'Retribuciones totales (dinerarias y en especie). Importe ntegro',
  `irregular_18_2_reduction` double(15,3) default NULL COMMENT 'Reducciones por irregularidad ( Atr. 18.2 LIRPF)',
  `irregular_18_3_reduction` double(15,3) default NULL COMMENT 'Reducciones por irregularidad ( Atr. 18.3: Disposiciones transitorias 11 y 12  de la LIRPF)',
  `deduccibles_expenses` double(15,3) default NULL COMMENT 'Gastos deducibles ( Atr 19.2, letras a, b y c de la LINRPF: Seguridad Social, Mutualidades ...)',
  `spousal_support` double(15,3) default NULL COMMENT 'Pension compensatoria a favor del cnyuge. Importe fijado judicialmente',
  `food_annuity` double(15,3) default NULL COMMENT 'Anualidades por alimentos en favor de los hijos. Importe fijado judicialmente',
  `deduct_home_loan` tinyint(2) default NULL COMMENT 'Comunicacin de pagos por la adquisin o rehabilitacin de la vivienda habitual utilizando financiacin ajena',
  `request_irpf` double(15,2) default NULL COMMENT 'Tipo de retencin solicitado',
  `contract_type` tinyint(2) NOT NULL default '0' COMMENT 'Contrato o relacin',
  `ceuta_melilla` tinyint(1) NOT NULL default '0' COMMENT 'Los datos anteriores corresponden a rendimientos obtenidos en Ceuta o Melilla',
  PRIMARY KEY  (`id`),
  KEY `IDX_IRPF_DATA_CONTRACT` (`contract`),
  KEY `IDX_IRPF_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_IRPF_DATA_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_IRPF_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Dator de irpf';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `irpf_data`
#

LOCK TABLES `irpf_data` WRITE;
ALTER TABLE `irpf_data` DISABLE KEYS;
ALTER TABLE `irpf_data` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `irpf_data_ascendants`
#

DROP TABLE IF EXISTS `irpf_data_ascendants`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `irpf_data_ascendants` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `irpf_data` int(4) NOT NULL COMMENT 'Identificador del irpf',
  `birth_year` int(4) default NULL COMMENT 'Anio de nacimiento',
  `disability_level` tinyint(2) default '0' COMMENT 'Grado de discapacidad',
  `dependence` tinyint(1) default '0' COMMENT 'Dependencia de terceras personas',
  `another_descendient` tinyint(2) default '0' COMMENT 'Convivencia con otros descendientes',
  PRIMARY KEY  (`id`),
  KEY `IDX_IRPF_DATA_ASCENDANTS_IRPF_DATA` (`irpf_data`),
  KEY `IDX_IRPF_DATA_ASCENDANTS_DOMAIN` (`domain`),
  CONSTRAINT `FK_IRPF_DATA_ASCENDANTS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_IRPF_DATA_ASCENDANTS_IRPF_DATA` FOREIGN KEY (`irpf_data`) REFERENCES `irpf_data` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ascendientes del modelo 145';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `irpf_data_ascendants`
#

LOCK TABLES `irpf_data_ascendants` WRITE;
ALTER TABLE `irpf_data_ascendants` DISABLE KEYS;
ALTER TABLE `irpf_data_ascendants` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `irpf_data_descendients`
#

DROP TABLE IF EXISTS `irpf_data_descendients`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `irpf_data_descendients` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `irpf_data` int(4) NOT NULL COMMENT 'Identificador del irpf',
  `birth_year` int(4) default NULL COMMENT 'Anio de nacimiento',
  `adoption_year` int(4) default NULL COMMENT 'Anio de adopcion',
  `disability_level` tinyint(2) default '0' COMMENT 'Grado de discapacidad',
  `dependence` tinyint(1) default '0' COMMENT 'Dependencia de terceras personas',
  `unique_parent` tinyint(1) default '0' COMMENT 'Computo por entero de hijos o descendientes',
  PRIMARY KEY  (`id`),
  KEY `IDX_IRPF_DATA_DESCENDIENTS_IRPF_DATA` (`irpf_data`),
  KEY `IDX_IRPF_DATA_DESCENDIENTS_DOMAIN` (`domain`),
  CONSTRAINT `FK_IRPF_DATA_DESCENDIENTS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_IRPF_DATA_DESCENDIENTS_IRPF_DATA` FOREIGN KEY (`irpf_data`) REFERENCES `irpf_data` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Descendientes del modelo 145';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `irpf_data_descendients`
#

LOCK TABLES `irpf_data_descendients` WRITE;
ALTER TABLE `irpf_data_descendients` DISABLE KEYS;
ALTER TABLE `irpf_data_descendients` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `irpf_regularization`
#

DROP TABLE IF EXISTS `irpf_regularization`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `irpf_regularization` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int(4) NOT NULL COMMENT 'Identificador del contrato',
  `reason` tinyint(2) default NULL COMMENT 'Causa de regularizacin',
  `effective_date` date NOT NULL COMMENT 'Fecha de entrada en vigor',
  `paid_irpf` double(15,3) default NULL COMMENT 'Retenciones practicadas con anterioridad a la regularizacin.',
  `paid_remuneration` double(15,3) default NULL COMMENT 'Retribuciones ya satisfechas con anterioridad a la regularizacin.',
  `prior_annual_irpf` double(15,3) default NULL COMMENT 'Retenciones anuales anteriores a la regularizacin.',
  `prior_annual_remuneration` double(15,3) default NULL COMMENT 'Retribucines anulaes consideradas con anterioridad a la regularizacin.',
  `prior_base_irpf` double(15,3) default NULL COMMENT 'Base para calcular el tipo de retencin determinado antes de la regularizacin.',
  `prior_irpf` double(15,2) default NULL COMMENT 'Tipo de retencin aplicado antes de la regularizacin.',
  `prior_in_ceuta_melilla` tinyint(1) default NULL COMMENT 'Los rendimientos anteriores a la regularizacin fueron obtenidos en Ceuta o Melilla',
  `prior_minimun_personal_family` double(15,3) default NULL COMMENT 'Mnimo personal y familiar para calcular el tipo de retencin determinado antes de la regularizacin.',
  `prior_deduct_home_loan` tinyint(2) default NULL COMMENT 'En algn momento antes de la regularizacin se aplico la minoracin por pagos por la adquisin o rehabilitacin de la vivienda',
  `prior_deduct_home_loan_amount` double(15,3) default NULL COMMENT 'Importe de la minoracin por pagos por la adquisin o rehabilitacin de la vivienda antes de la regularizacin',
  PRIMARY KEY  (`id`),
  KEY `IDX_IRPF_REGULARIZATION_CONTRACT` (`contract`),
  KEY `IDX_IRPF_REGULARIZATION_DOMAIN` (`domain`),
  CONSTRAINT `FK_IRPF_REGULARIZATION_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_IRPF_REGULARIZATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos regularizacion IRPF';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `irpf_regularization`
#

LOCK TABLES `irpf_regularization` WRITE;
ALTER TABLE `irpf_regularization` DISABLE KEYS;
ALTER TABLE `irpf_regularization` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `irpf_result`
#

DROP TABLE IF EXISTS `irpf_result`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `irpf_result` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int(4) NOT NULL COMMENT 'Identificador del contrato',
  `effective_date` date NOT NULL COMMENT 'Fecha de entrada en vigor',
  `base_irpf` double(15,3) default NULL COMMENT 'Base para calcular el tipo de retencin',
  `minimun_personal_family` double(15,3) default NULL COMMENT 'Mnimo personal y familiar para calcular el tipo de retencin',
  `deduct_home_loan_amount` double(15,3) default NULL COMMENT 'Minoracin por pagos de prstamo para vivienda habitual',
  `deduct_80_bis` double(15,3) default NULL COMMENT 'Deduccion Arttculo 80 bis LIRPF',
  `irpf` double(15,2) default NULL COMMENT 'Tipo retencin apliclabe ',
  `annual_irpf` double(15,3) default NULL COMMENT 'Importe anual de las retenciones e ingresos a cuenta',
  `annual_remuneration` double(15,3) default NULL COMMENT 'Retribuciones anuales. Importe ntegro',
  `irregular_18_2_reduction` double(15,3) default NULL COMMENT 'Reducciones por irregularidad ( Art. 18.2 LIRPF). Importe',
  `irregular_18_3_reduction` double(15,3) default NULL COMMENT 'Reducciones por irregularidad ( Art. 18.3: DD.TT 11 y 12  de la LIRPF). Importe',
  `deduccibles_expenses` double(15,3) default NULL COMMENT 'Gastos deducibles. Importe anual',
  `work_remuneration_reduction` double(15,3) default NULL COMMENT 'Reducciones por rendimiento del trabajo ',
  `work_prolongation_reduction` double(15,3) default NULL COMMENT 'Reducciones por prolongacin de la actividad ',
  `work_moving_reduction` double(15,3) default NULL COMMENT 'Reducciones por movilidad geografica ',
  `work_disability_reduction` double(15,3) default NULL COMMENT 'Reducciones por discapacidad ',
  `social_security_pensioner` double(15,3) default NULL COMMENT 'Por ser pensionista de la s. social/cl. Pasivas o desempleado',
  `two_or_more_descendents_min` double(15,3) default NULL COMMENT 'Por tener ms de dos descendientes con derecho a mnimo',
  `spousal_support` double(15,3) default NULL COMMENT 'Pension compensatoria a favor del cnyuge. Importe anual',
  `food_annuity` double(15,3) default NULL COMMENT 'Anualidades por alimentos en favor de los hijos. Importe anual',
  `minimun_personal` double(15,3) default NULL COMMENT 'Mnimo personal',
  `minimun_ascendents` double(15,3) default NULL COMMENT 'Mnimo por descendientes',
  `minimun_descendents` double(15,3) default NULL COMMENT 'Mnimo por descendientes',
  `minimun_disability` double(15,3) default NULL COMMENT 'Mnimo por discapacidad',
  `descendents_minor_3_total` tinyint(2) default NULL COMMENT 'Descendientes computados menores de tres aos. Total',
  `descendents_minor_3_entirely` tinyint(2) default NULL COMMENT 'Descendientes computados menores de tres aos. Por entero',
  `descendents_remainder_total` tinyint(2) default NULL COMMENT 'Resto de descendientes computados . Total',
  `descendents_remainder_entirely` tinyint(2) default NULL COMMENT 'Resto de descendientes computados . Por entero',
  `descendents_33_65_total` tinyint(2) default NULL COMMENT 'Descendientes con discapacidad >= 33% y < 65%. Total',
  `descendents_33_65_entirely` tinyint(2) default NULL COMMENT 'Descendientes con discapacidad >= 33% y < 65%. Por entero',
  `descendents_moving_total` tinyint(2) default NULL COMMENT 'Descendientes con discapacidad, movilidad reducida. Total',
  `descendents_moving_entirely` tinyint(2) default NULL COMMENT 'Descendientes con discapacidad, movilidad reducida. Por entero',
  `descendents_65_total` tinyint(2) default NULL COMMENT 'Descendientes con discapacidad > 65%. Total',
  `descendents_65_entirely` tinyint(2) default NULL COMMENT 'Descendientes con discapacidad > 65%. Por entero',
  `descendents_first` tinyint(2) default NULL COMMENT 'Detalle del cmputo de descendientes. Hijo 1',
  `descendents_second` tinyint(2) default NULL COMMENT 'Detalle del cmputo de descendientes. Hijo 2',
  `descendents_third` tinyint(2) default NULL COMMENT 'Detalle del cmputo de descendientes. Hijo 3',
  `descendents_fourth_subsequent_total` tinyint(2) default NULL COMMENT 'Detalle del cmputo de descendientes. Hijo 4 y sucesivos. Total',
  `descendents_fourth_subsequent_entirely` tinyint(2) default NULL COMMENT 'Detalle del cmputo de descendientes. Hijo 4 y sucesivos. Por entero',
  `ascendents_minor_75_total` tinyint(2) default NULL COMMENT 'Ascendientes computados menores de 75 aos. Total',
  `ascendents_minor_75_entirely` tinyint(2) default NULL COMMENT 'Ascendientes computados menores de 75 aos. Por entero',
  `ascendents_mayor_75_total` tinyint(2) default NULL COMMENT 'Ascendientes computados mayores de 75 aos. Total',
  `ascendents_mayor_75_entirely` tinyint(2) default NULL COMMENT 'Ascendientes computados mayores de 75 aos. Por entero',
  `ascendents_33_65_total` tinyint(2) default NULL COMMENT 'Ascendientes con discapacidad >= 33% y < 65%. Total',
  `ascendents_33_65_entirely` tinyint(2) default NULL COMMENT 'Ascendientes con discapacidad >= 33% y < 65%. Por entero',
  `ascendents_moving_total` tinyint(2) default NULL COMMENT 'Ascendientes con discapacidad, movilidad reducida. Total',
  `ascendents_moving_entirely` tinyint(2) default NULL COMMENT 'Ascendientes con discapacidad, movilidad reducida. Por entero',
  `ascendents_65_total` tinyint(2) default NULL COMMENT 'Ascendientes con discapacidad > 65%. Total',
  `ascendents_65_entirely` tinyint(2) default NULL COMMENT 'Ascendientes con discapacidad > 65%. Por entero',
  PRIMARY KEY  (`id`),
  KEY `IDX_IRPF_RESULT_CONTRACT` (`contract`),
  KEY `IDX_IRPF_RESULT_DOMAIN` (`domain`),
  CONSTRAINT `FK_IRPF_RESULT_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_IRPF_RESULT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Resultados IRPF';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `irpf_result`
#

LOCK TABLES `irpf_result` WRITE;
ALTER TABLE `irpf_result` DISABLE KEYS;
ALTER TABLE `irpf_result` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `item`
#

DROP TABLE IF EXISTS `item`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `item` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Articulo',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_ITEM_PRODUCT` (`product`),
  KEY `IDX_ITEM_DOMAIN` (`domain`),
  CONSTRAINT `FK_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ITEM_PRODUCT` FOREIGN KEY (`product`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `item`
#

LOCK TABLES `item` WRITE;
ALTER TABLE `item` DISABLE KEYS;
ALTER TABLE `item` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `item_alternative`
#

DROP TABLE IF EXISTS `item_alternative`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `item_alternative` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador de Articulo',
  `alternative_item` int(4) NOT NULL default '0' COMMENT 'Identificador de Articulo Alternativo',
  `priority` tinyint(2) default '0' COMMENT 'Prioridad del Articulo Alternativo',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_UNQ_ITEM_ALTERNATIVE` (`item`,`alternative_item`),
  KEY `IDX_ITEM_ALTERNATIVE_ITEM` (`item`),
  KEY `IDX_ITEM_ALTERNATIVE_ALTERNATIVE` (`alternative_item`),
  KEY `IDX_ITEM_ALTERNATIVE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ITEM_ALTERNATIVE_ALTERNATIVE` FOREIGN KEY (`alternative_item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_ALTERNATIVE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ITEM_ALTERNATIVE_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulo Alternativos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `item_alternative`
#

LOCK TABLES `item_alternative` WRITE;
ALTER TABLE `item_alternative` DISABLE KEYS;
ALTER TABLE `item_alternative` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `item_composition`
#

DROP TABLE IF EXISTS `item_composition`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `item_composition` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo compuesto',
  `composition_item` int(4) NOT NULL COMMENT 'Identificador del Articulo componente',
  `sequence` smallint(2) default '0' COMMENT 'Numero de secuencia dentro de la Composicion',
  `description` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del componente',
  `quantity` double(15,3) default '0.000' COMMENT 'Cantidad del componente',
  `discount_expr` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descuentos del componente',
  PRIMARY KEY  (`id`),
  KEY `IDX_ITEM_COMPOSITION_ITEM` (`item`),
  KEY `IDX_ITEM_COMPOSITION_COMPOSITION` (`composition_item`),
  KEY `IDX_ITEM_COMPOSITION_DOMAIN` (`domain`),
  CONSTRAINT `FK_ITEM_COMPOSITION_COMPOSITION` FOREIGN KEY (`composition_item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_COMPOSITION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ITEM_COMPOSITION_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Composicion de Articulos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `item_composition`
#

LOCK TABLES `item_composition` WRITE;
ALTER TABLE `item_composition` DISABLE KEYS;
ALTER TABLE `item_composition` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `item_supplier`
#

DROP TABLE IF EXISTS `item_supplier`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `item_supplier` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador de Articulo',
  `supplier` int(4) NOT NULL default '0' COMMENT 'Identificador de Proveedor',
  `code` varchar(15) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo del Producto en el Proveedor',
  `priority` tinyint(2) default '0' COMMENT 'Prioridad del Proveedor',
  `workplace` int(4) default NULL COMMENT 'Identificador del Centro de Trabajo',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_UNQ_ITEM_SUPPLIER` (`item`,`supplier`),
  KEY `IDX_ITEM_SUPPLIER_ITEM` (`item`),
  KEY `IDX_ITEM_SUPPLIER_SUPPLIER` (`supplier`),
  KEY `IDX_ITEM_SUPPLIER_DOMAIN` (`domain`),
  KEY `IDX_ITEM_SUPPLIER_WORKPLACE` (`workplace`),
  CONSTRAINT `FK_ITEM_SUPPLIER_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`),
  CONSTRAINT `FK_ITEM_SUPPLIER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ITEM_SUPPLIER_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_SUPPLIER_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos del Articulo por Proveedor';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `item_supplier`
#

LOCK TABLES `item_supplier` WRITE;
ALTER TABLE `item_supplier` DISABLE KEYS;
ALTER TABLE `item_supplier` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `item_tariff`
#

DROP TABLE IF EXISTS `item_tariff`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `item_tariff` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador de Articulo',
  `tariff` int(4) NOT NULL default '0' COMMENT 'Identificador de Tarifa',
  `type` tinyint(2) default '0' COMMENT 'Tipo de Tarifa',
  `profit_percent` double default '0' COMMENT 'Porcentaje de beneficio',
  `price` double default '0' COMMENT 'Precio de Venta',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_UNQ_ITEM_TARIFF` (`item`,`tariff`),
  KEY `IDX_ITEM_TARIFF_TARIFF` (`tariff`),
  KEY `IDX_ITEM_TARIFF_ITEM` (`item`),
  KEY `IDX_ITEM_TARIFF_DOMAIN` (`domain`),
  CONSTRAINT `FK_ITEM_TARIFF_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ITEM_TARIFF_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_TARIFF_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas de Articulos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `item_tariff`
#

LOCK TABLES `item_tariff` WRITE;
ALTER TABLE `item_tariff` DISABLE KEYS;
ALTER TABLE `item_tariff` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `item_warehouse`
#

DROP TABLE IF EXISTS `item_warehouse`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `item_warehouse` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador de Articulo',
  `warehouse` int(4) NOT NULL default '0' COMMENT 'Identificador de Almacen',
  `stock_max` double(15,3) default '0.000' COMMENT 'Stock maximo del Articulo en el Almacen',
  `stock_min` double(15,3) default '0.000' COMMENT 'Stock minimo del Articulo en el Almacen',
  `location` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Localizacion del Articulo en el Almacen',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_UNQ_ITEM_WAREHOUSE` (`item`,`warehouse`),
  KEY `IDX_ITEM_WAREHOUSE_ITEM` (`item`),
  KEY `IDX_ITEM_WAREHOUSE_WAREHOUSE` (`warehouse`),
  KEY `IDX_ITEM_WAREHOUSE_DOMAIN` (`domain`),
  CONSTRAINT `FK_ITEM_WAREHOUSE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ITEM_WAREHOUSE_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_WAREHOUSE_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos del Articulo por Almacen';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `item_warehouse`
#

LOCK TABLES `item_warehouse` WRITE;
ALTER TABLE `item_warehouse` DISABLE KEYS;
ALTER TABLE `item_warehouse` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `job_type`
#

DROP TABLE IF EXISTS `job_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `job_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Tipo de Trabajo',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Trabajo',
  PRIMARY KEY  (`id`),
  KEY `IDX_JOB_TYPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_JOB_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Trabajos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `job_type`
#

LOCK TABLES `job_type` WRITE;
ALTER TABLE `job_type` DISABLE KEYS;
ALTER TABLE `job_type` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `leave_batch`
#

DROP TABLE IF EXISTS `leave_batch`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `leave_batch` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `date` datetime default NULL COMMENT 'Fecha de la Remesa',
  `status` tinyint(2) NOT NULL default '0' COMMENT 'Indica el estado de la remesa',
  PRIMARY KEY  (`id`),
  KEY `IDX_LEAVE_BATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_LEAVE_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas de partes IT';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `leave_batch`
#

LOCK TABLES `leave_batch` WRITE;
ALTER TABLE `leave_batch` DISABLE KEYS;
ALTER TABLE `leave_batch` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `leave_batch_attach`
#

DROP TABLE IF EXISTS `leave_batch_attach`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `leave_batch_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Archivo Adjunto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `leave_batch` int(4) NOT NULL default '0' COMMENT 'Identificador de la remesa',
  `mimeType` tinyint(2) default '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Archivo Adjunto',
  `scope` int(4) default NULL COMMENT 'Ambito del Archivo Adjunto',
  `attach_date` date default NULL COMMENT 'Fecha del Archivo Adjunto',
  PRIMARY KEY  (`id`),
  KEY `IDX_LEAVE_BATCH_ATTACH_SCOPE` (`scope`),
  KEY `IDX_LEAVE_BATCH_ATTACH_LEAVE_BATCH` (`leave_batch`),
  KEY `IDX_LEAVE_BATCH_ATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_LEAVE_BATCH_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_LEAVE_BATCH_ATTACH_LEAVE_BATCH` FOREIGN KEY (`leave_batch`) REFERENCES `leave_batch` (`id`),
  CONSTRAINT `FK_LEAVE_BATCH_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de remesas de partes IT';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `leave_batch_attach`
#

LOCK TABLES `leave_batch_attach` WRITE;
ALTER TABLE `leave_batch_attach` DISABLE KEYS;
ALTER TABLE `leave_batch_attach` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `leave_batch_detail`
#

DROP TABLE IF EXISTS `leave_batch_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `leave_batch_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `leave_batch` int(4) NOT NULL COMMENT 'Identificador unico de la remesa',
  `contract_leave_detail` int(4) NOT NULL COMMENT 'Identificador unico del parte',
  PRIMARY KEY  (`id`),
  KEY `IDX_LEAVE_BATCH_DETAIL_LEAVE_BATCH` (`leave_batch`),
  KEY `IDX_LEAVE_BATCH_DETAIL_CONTRACT_LEAVE_DETAIL` (`contract_leave_detail`),
  KEY `IDX_LEAVE_BATCH_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_LEAVE_BATCH_DETAIL_CONTRACT_LEAVE_DETAIL` FOREIGN KEY (`contract_leave_detail`) REFERENCES `contract_leave_detail` (`id`),
  CONSTRAINT `FK_LEAVE_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_LEAVE_BATCH_DETAIL_LEAVE_BATCH` FOREIGN KEY (`leave_batch`) REFERENCES `leave_batch` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de remesas de partes IT';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `leave_batch_detail`
#

LOCK TABLES `leave_batch_detail` WRITE;
ALTER TABLE `leave_batch_detail` DISABLE KEYS;
ALTER TABLE `leave_batch_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `loan`
#

DROP TABLE IF EXISTS `loan`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `loan` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_LOAN_RBANK` (`rbank`),
  KEY `IDX_LOAN_DOMAIN` (`domain`),
  CONSTRAINT `FK_LOAN_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_LOAN_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Prestamos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `loan`
#

LOCK TABLES `loan` WRITE;
ALTER TABLE `loan` DISABLE KEYS;
ALTER TABLE `loan` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `loan_account`
#

DROP TABLE IF EXISTS `loan_account`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `loan_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `loan` int(4) NOT NULL COMMENT 'Prestamo',
  `account` int(4) NOT NULL COMMENT 'Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `IDX_LOAN_ACCOUNT_ACCOUNT` (`account`),
  KEY `IDX_LOAN_ACCOUNT_LOAN` (`loan`),
  KEY `IDX_LOAN_ACCOUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_LOAN_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_LOAN_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_LOAN_ACCOUNT_LOAN` FOREIGN KEY (`loan`) REFERENCES `loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Prestamos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `loan_account`
#

LOCK TABLES `loan_account` WRITE;
ALTER TABLE `loan_account` DISABLE KEYS;
ALTER TABLE `loan_account` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `mail_account`
#

DROP TABLE IF EXISTS `mail_account`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `mail_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Cuenta de Correo',
  `email` varchar(256) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta de correo',
  `replyto_mail` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Email de Respuesta',
  `host` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Host del servidor de correo',
  `protocol` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Protocolo utilizado (IMAP)',
  `incoming_host` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Host del correo entrante',
  `incoming_port` int(4) default NULL COMMENT 'Puerto del correo entrante',
  `incoming_ssl` bit(1) default NULL COMMENT 'Indica si tiene SSL el correo entrante',
  `outgoing_verification` bit(1) default NULL COMMENT 'Indica si hay autentificacion en el correo saliente',
  `outgoing_host` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Host del servidor de correo saliente',
  `outgoing_port` int(4) default NULL COMMENT 'Puerto del servidor de correo saliente',
  `outgoing_ssl` bit(1) default NULL COMMENT 'Indica si tiene SSL el correo saliente',
  `mail_username` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Nombre del usuario',
  `password` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Clave del usuario',
  `default_account` bit(1) default NULL COMMENT 'Indica si es la cuenta de correo por defecto',
  `draft_folder` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Ruta de Borrador',
  `sent_folder` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Ruta de Enviados',
  `trash_folder` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Ruta de Papelera',
  `spam_folder` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Ruta de Spam',
  `display_name` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Mostrar como',
  `signature` int(4) default NULL COMMENT 'Identificador de la Firma',
  `source` tinyint(2) NOT NULL COMMENT 'Origen de la Firma',
  `source_id` int(4) NOT NULL COMMENT 'Identificador del origen de la Firma',
  PRIMARY KEY  (`id`),
  KEY `IDX_MAIL_ACCOUNT_SIGNATURE` (`signature`),
  KEY `IDX_MAIL_ACCOUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_MAIL_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_MAIL_ACCOUNT_SIGNATURE` FOREIGN KEY (`signature`) REFERENCES `signature` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas de Correo Electronico';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `mail_account`
#

LOCK TABLES `mail_account` WRITE;
ALTER TABLE `mail_account` DISABLE KEYS;
ALTER TABLE `mail_account` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `make`
#

DROP TABLE IF EXISTS `make`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `make` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Fabricante',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Fabricante',
  PRIMARY KEY  (`id`),
  KEY `IDX_MAKE_DOMAIN` (`domain`),
  CONSTRAINT `FK_MAKE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Fabricantes';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `make`
#

LOCK TABLES `make` WRITE;
ALTER TABLE `make` DISABLE KEYS;
ALTER TABLE `make` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `mark`
#

DROP TABLE IF EXISTS `mark`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
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
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `mark`
#

LOCK TABLES `mark` WRITE;
ALTER TABLE `mark` DISABLE KEYS;
ALTER TABLE `mark` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `message_content`
#

DROP TABLE IF EXISTS `message_content`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `message_content` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `content` text collate latin1_spanish_ci NOT NULL COMMENT 'Contenido del Mensaje',
  PRIMARY KEY  (`id`),
  KEY `IDX_MESSAGE_CONTENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_MESSAGE_CONTENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contenido de Mensajes';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `message_content`
#

LOCK TABLES `message_content` WRITE;
ALTER TABLE `message_content` DISABLE KEYS;
ALTER TABLE `message_content` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `message_log`
#

DROP TABLE IF EXISTS `message_log`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `message_log` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `message_id` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador del Mensaje para el servidor de Esendex',
  `message_content` int(4) default NULL COMMENT 'Identificador del Contenido del Mensaje',
  `recipient` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Destinatario del Mensaje',
  `type` varchar(10) collate latin1_spanish_ci default NULL COMMENT 'Tipo de Mensaje',
  `sent_date` datetime NOT NULL COMMENT 'Fecha y hora de envio del Mensaje',
  `message_parts` tinyint(2) NOT NULL default '1' COMMENT 'Numero de partes que componen el Mensaje',
  `username` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Usuario que envia el mensaje',
  PRIMARY KEY  (`id`),
  KEY `IDX_MESSAGE_LOG_MESSAGE_CONTENT` (`message_content`),
  KEY `IDX_MESSAGE_LOG_DOMAIN` (`domain`),
  CONSTRAINT `FK_MESSAGE_LOG_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_MESSAGE_LOG_MESSAGE_CONTENT` FOREIGN KEY (`message_content`) REFERENCES `message_content` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Log de Mensajes';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `message_log`
#

LOCK TABLES `message_log` WRITE;
ALTER TABLE `message_log` DISABLE KEYS;
ALTER TABLE `message_log` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `mk_action`
#

DROP TABLE IF EXISTS `mk_action`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `mk_action` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `campaign` int(11) NOT NULL COMMENT 'Identificador de la Campaa',
  `media_type` int(4) NOT NULL COMMENT 'Tipo de contacto de la Accion',
  `start_date` datetime NOT NULL COMMENT 'Fecha de inicio',
  `end_date` datetime default NULL COMMENT 'Fecha de finalizacion',
  `survey` int(4) default NULL COMMENT 'Identificador del Cuestionario',
  `template` int(4) default NULL COMMENT 'Identificador de la Plantilla',
  PRIMARY KEY  (`id`),
  KEY `IDX_MK_ACTION_MK_TEMPLATE` (`template`),
  KEY `IDX_MK_ACTION_MK_CAMPAIGN` (`campaign`),
  KEY `IDX_MK_ACTION_SURVEY` (`survey`),
  KEY `IDX_MK_ACTION_DOMAIN` (`domain`),
  CONSTRAINT `FK_MK_ACTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_MK_ACTION_MK_CAMPAIGN` FOREIGN KEY (`campaign`) REFERENCES `mk_campaign` (`id`),
  CONSTRAINT `FK_MK_ACTION_MK_TEMPLATE` FOREIGN KEY (`template`) REFERENCES `mk_template` (`id`),
  CONSTRAINT `FK_MK_ACTION_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Acciones de Marketing';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `mk_action`
#

LOCK TABLES `mk_action` WRITE;
ALTER TABLE `mk_action` DISABLE KEYS;
ALTER TABLE `mk_action` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `mk_action_target`
#

DROP TABLE IF EXISTS `mk_action_target`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `mk_action_target` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `action` int(4) NOT NULL COMMENT 'Identificador de la Accion',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `status` tinyint(2) NOT NULL COMMENT 'Estado del Cliente Potencial de la Accion de Campaa',
  `survey_response` int(4) default NULL COMMENT 'Identificador de la Respuesta de Cuestionario',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios',
  `user` int(4) default NULL COMMENT 'Identificador del Usuario',
  PRIMARY KEY  (`id`),
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
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `mk_action_target`
#

LOCK TABLES `mk_action_target` WRITE;
ALTER TABLE `mk_action_target` DISABLE KEYS;
ALTER TABLE `mk_action_target` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `mk_campaign`
#

DROP TABLE IF EXISTS `mk_campaign`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `mk_campaign` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Campaa esta activa o no',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Campaa',
  PRIMARY KEY  (`id`),
  KEY `IDX_MK_CAMPAIGN_DOMAIN` (`domain`),
  CONSTRAINT `FK_MK_CAMPAIGN_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Campaas de Marketing';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `mk_campaign`
#

LOCK TABLES `mk_campaign` WRITE;
ALTER TABLE `mk_campaign` DISABLE KEYS;
ALTER TABLE `mk_campaign` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `mk_template`
#

DROP TABLE IF EXISTS `mk_template`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `mk_template` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Plantilla',
  `data` mediumtext collate latin1_spanish_ci NOT NULL COMMENT 'Contenido de la Plantilla',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Plantilla esta activa o no',
  `creationDate` datetime NOT NULL COMMENT 'Fecha de la creacion en el sistema de la Plantilla',
  `subject` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Asunto de la Plantilla',
  `append_signature` tinyint(1) NOT NULL COMMENT 'Indica si la Plantilla incluye la firma o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_MK_TEMPLATE_DOMAIN` (`domain`),
  CONSTRAINT `FK_MK_TEMPLATE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Plantilla de Marketing';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `mk_template`
#

LOCK TABLES `mk_template` WRITE;
ALTER TABLE `mk_template` DISABLE KEYS;
ALTER TABLE `mk_template` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `model`
#

DROP TABLE IF EXISTS `model`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `model` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Modelo',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `make` int(4) NOT NULL COMMENT 'Identificador del Fabricante',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Modelo',
  PRIMARY KEY  (`id`),
  KEY `IDX_MODEL_MAKE` (`make`),
  KEY `IDX_MODEL_DOMAIN` (`domain`),
  CONSTRAINT `FK_MODEL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_MODEL_MAKE` FOREIGN KEY (`make`) REFERENCES `make` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Modelos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `model`
#

LOCK TABLES `model` WRITE;
ALTER TABLE `model` DISABLE KEYS;
ALTER TABLE `model` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `note`
#

DROP TABLE IF EXISTS `note`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `note` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Nota',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `subject` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion corta de la Nota',
  `date` datetime NOT NULL COMMENT 'Fecha de la Nota',
  `owner` int(4) default NULL COMMENT 'Destinatario de la Nota',
  `note` text collate latin1_spanish_ci COMMENT 'Texto de la Nota',
  PRIMARY KEY  (`id`),
  KEY `IDX_NOTE_USER` (`owner`),
  KEY `IDX_NOTE_DOMAIN` (`domain`),
  CONSTRAINT `FK_NOTE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_NOTE_USER` FOREIGN KEY (`owner`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Notas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `note`
#

LOCK TABLES `note` WRITE;
ALTER TABLE `note` DISABLE KEYS;
ALTER TABLE `note` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `notice`
#

DROP TABLE IF EXISTS `notice`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `notice` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Aviso',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_NOTICE_USER_SENDER` (`sender`),
  KEY `IDX_NOTICE_USER_RECIPIENT` (`recipient`),
  KEY `IDX_NOTICE_WORKGROUP` (`work_group`),
  KEY `IDX_NOTICE_DOMAIN` (`domain`),
  CONSTRAINT `FK_NOTICE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_NOTICE_USER_RECIPIENT` FOREIGN KEY (`recipient`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_NOTICE_USER_SENDER` FOREIGN KEY (`sender`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_NOTICE_WORKGROUP` FOREIGN KEY (`work_group`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Avisos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `notice`
#

LOCK TABLES `notice` WRITE;
ALTER TABLE `notice` DISABLE KEYS;
ALTER TABLE `notice` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `observation`
#

DROP TABLE IF EXISTS `observation`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `observation` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Observacion',
  `description` varchar(256) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Observacion',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Observaciones';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `observation`
#

LOCK TABLES `observation` WRITE;
ALTER TABLE `observation` DISABLE KEYS;
ALTER TABLE `observation` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `offer`
#

DROP TABLE IF EXISTS `offer`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `offer` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Presupuesto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  UNIQUE KEY `IDX_OFFER_DOMAIN_SERIES_NUMBER_VERSION` (`domain`,`series`,`number`,`version`),
  KEY `IDX_OFFER_SCOPE` (`scope`),
  KEY `IDX_OFFER_BANK` (`bank`),
  KEY `IDX_OFFER_SUPPLIER` (`supplier`),
  KEY `IDX_OFFER_RADDRESS` (`address`),
  KEY `IDX_OFFER_PROJECT` (`project`),
  KEY `IDX_OFFER_ISSUE_DATE` (`issue_date`),
  KEY `IDX_OFFER_TARGET` (`target`),
  KEY `IDX_OFFER_SELLER` (`seller`),
  KEY `IDX_OFFER_PAY_METHOD` (`pay_method`),
  KEY `IDX_OFFER_WORKPLACE` (`workplace`),
  KEY `IDX_OFFER_TARIFF` (`tariff`),
  KEY `IDX_OFFER_DOMAIN` (`domain`),
  CONSTRAINT `FK_OFFER_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`),
  CONSTRAINT `FK_OFFER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_OFFER_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_OFFER_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_OFFER_RADDRESS` FOREIGN KEY (`address`) REFERENCES `raddress` (`id`),
  CONSTRAINT `FK_OFFER_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_OFFER_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`),
  CONSTRAINT `FK_OFFER_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`),
  CONSTRAINT `FK_OFFER_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_OFFER_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`),
  CONSTRAINT `FK_OFFER_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Presupuestos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `offer`
#

LOCK TABLES `offer` WRITE;
ALTER TABLE `offer` DISABLE KEYS;
ALTER TABLE `offer` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `offer_attach`
#

DROP TABLE IF EXISTS `offer_attach`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `offer_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `offer` int(4) NOT NULL COMMENT 'Identificador del Presupuesto',
  `mimeType` tinyint(2) default '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  PRIMARY KEY  (`id`),
  KEY `IDX_OFFER_ATTACH_OFFER` (`offer`),
  KEY `IDX_OFFER_ATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_OFFER_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_OFFER_ATTACH_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Presupuestos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `offer_attach`
#

LOCK TABLES `offer_attach` WRITE;
ALTER TABLE `offer_attach` DISABLE KEYS;
ALTER TABLE `offer_attach` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `offer_detail`
#

DROP TABLE IF EXISTS `offer_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `offer_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle de Presupuesto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `offer` int(4) NOT NULL COMMENT 'Identificador del Presupuesto',
  `line` smallint(2) default '1' COMMENT 'Numero de lnea del Detalle dentro del Presupuesto',
  `item` int(4) default NULL COMMENT 'Identificador del Articulo',
  `description` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Descripcin del Articulo',
  `quantity` double(15,3) default '0.000' COMMENT 'Cantidad del Articulo',
  `price` double default '0' COMMENT 'Precio del Articulo',
  `discount_expr` varchar(32) collate latin1_spanish_ci default '0' COMMENT 'Descuentos del Articulo',
  `status` tinyint(2) default '0' COMMENT 'Estado del Detalle del Presupuesto',
  PRIMARY KEY  (`id`),
  KEY `IDX_OFFER_DETAIL_OFFER` (`offer`),
  KEY `IDX_OFFER_DETAIL_ITEM` (`item`),
  KEY `IDX_OFFER_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_OFFER_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_OFFER_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_OFFER_DETAIL_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Presupuesto';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `offer_detail`
#

LOCK TABLES `offer_detail` WRITE;
ALTER TABLE `offer_detail` DISABLE KEYS;
ALTER TABLE `offer_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `offer_detail_commission`
#

DROP TABLE IF EXISTS `offer_detail_commission`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `offer_detail_commission` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `offer_detail` int(4) NOT NULL default '0' COMMENT 'Identificador de la Linea de Presupuesto',
  `commission` double default '0' COMMENT 'Porcentaje de Comision',
  `amount` double default '0' COMMENT 'Importe de la Comision',
  `status` tinyint(2) default '0' COMMENT 'Estado de la Comision',
  `pay_date` date default NULL COMMENT 'Fecha de liquidacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_OFFER_DETAIL_COMMISSION_OFFER_DETAIL` (`offer_detail`),
  KEY `IDX_OFFER_DETAIL_COMMISSION_DOMAIN` (`domain`),
  CONSTRAINT `FK_OFFER_DETAIL_COMMISSION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_OFFER_DETAIL_COMMISSION_OFFER_DETAIL` FOREIGN KEY (`offer_detail`) REFERENCES `offer_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones asociadas a Lineas de Presupuestos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `offer_detail_commission`
#

LOCK TABLES `offer_detail_commission` WRITE;
ALTER TABLE `offer_detail_commission` DISABLE KEYS;
ALTER TABLE `offer_detail_commission` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `offer_term`
#

DROP TABLE IF EXISTS `offer_term`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `offer_term` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `offer` int(4) NOT NULL COMMENT 'Identificador del Presupuesto',
  `line` smallint(2) default '1' COMMENT 'Numero de linea de la Condicion del Presupuesto',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Nombre de la Condicion Comercial',
  `description` text collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Condicion Comercial',
  `term_general` tinyint(1) default '0' COMMENT 'Indica si la Condicion es particular o general',
  PRIMARY KEY  (`id`),
  KEY `IDX_OFFER_TERM_OFFER` (`offer`),
  KEY `IDX_OFFER_TERM_DOMAIN` (`domain`),
  CONSTRAINT `FK_OFFER_TERM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_OFFER_TERM_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Condiciones del Presupuesto';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `offer_term`
#

LOCK TABLES `offer_term` WRITE;
ALTER TABLE `offer_term` DISABLE KEYS;
ALTER TABLE `offer_term` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `pay_method`
#

DROP TABLE IF EXISTS `pay_method`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `pay_method` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Forma de Pago',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de Forma de Pago',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Forma de Pago',
  PRIMARY KEY  (`id`),
  KEY `IDX_PAY_METHOD_DOMAIN` (`domain`),
  CONSTRAINT `FK_PAY_METHOD_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Formas de Pago';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `pay_method`
#

LOCK TABLES `pay_method` WRITE;
ALTER TABLE `pay_method` DISABLE KEYS;
INSERT INTO `pay_method` VALUES (1,1,'EFECTIVO',1),(2,1,'GIRO',2),(3,1,'CHEQUE',5),(4,1,'TRANSFERENCIA',6);
ALTER TABLE `pay_method` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `payment_concept`
#

DROP TABLE IF EXISTS `payment_concept`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `payment_concept` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `code` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Codigo',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Percepcion Salarial',
  `description_decorable` tinyint(2) NOT NULL default '0',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe',
  `irpf_expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe tributable',
  `quote_expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe cotizable',
  PRIMARY KEY  (`id`),
  KEY `IDX_PAYMENT_CONCEPT_DOMAIN` (`domain`),
  CONSTRAINT `FK_PAYMENT_CONCEPT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos de devengos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `payment_concept`
#

LOCK TABLES `payment_concept` WRITE;
ALTER TABLE `payment_concept` DISABLE KEYS;
INSERT INTO `payment_concept` VALUES (1,1,'ECEMP','PREST. IT A CARGO DE LA EMPRESA',7,1,'DIAS_ENFERMEDAD_COMUN_4_15 * BASE_REGULADORA * 0.60','ECEMP','( COTIZACION_IT == \"MENSUAL\" ? 30 - ( DIAS_MES - DIAS_ENFERMEDAD_COMUN) : DIAS_ENFERMEDAD_COMUN ) * BASE_REGULADORA'),(2,1,'ECSS','PREST. IT A CARGO DEL INSS',7,1,'DIAS_ENFERMEDAD_COMUN_16_20 * BASE_REGULADORA * 0.60 + DIAS_ENFERMEDAD_COMUN_21 * BASE_REGULADORA * 0.75','ECSS',NULL),(3,1,'MTNAD','PREST. MATERNIDAD Y/O R.E',7,1,'0','0','( COTIZACION_IT == \"MENSUAL\" ? 30 - ( DIAS_MES - DIAS_MATERNIDAD) : DIAS_MATERNIDAD ) * BASE_REGULADORA'),(4,1,'ATEP','PREST. A.T. y E.P. ',7,1,'DIAS_ENFERMEDAD_PROFESIONAL * BASE_REGULADORA * 0.75','ATEP','( COTIZACION_IT == \"MENSUAL\" ? 30 - ( DIAS_MES - DIAS_ENFERMEDAD_PROFESIONAL) : DIAS_ENFERMEDAD_PROFESIONAL ) * BASE_REGULADORA'),(5,1,'GTZDO','GARANTIZADO EMPRESA SITUACION I.T.',1,1,'GARANTIZADO*DIAS_GARANTIZADOS/DIAS_MES>TOTAL_PRESTACIONES_IT?GARANTIZADO*DIAS_GARANTIZADOS/DIAS_MES-TOTAL_PRESTACIONES_IT:0.00','GTZDO',NULL),(6,1,'FIVAC','Vacaciones no disfrutadas',1,1,'DIAS_VACACIONES_NO_DISFRUTADAS * IMPORTE_DIA_VACACIONES','FIVAC','FIVAC'),(7,1,'INDEM','Indemnizacin',6,1,'IMPORTE_INDEMNIZACION','0.00','0.00');
ALTER TABLE `payment_concept` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `payroll_workplace`
#

DROP TABLE IF EXISTS `payroll_workplace`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `payroll_workplace` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `agreement` int(4) default NULL COMMENT 'Identificador del Convenio',
  `enterprise_activity` int(4) default NULL COMMENT 'Identificador de la Actividad',
  `calendar` int(4) default NULL COMMENT 'Identificador del Calendario',
  PRIMARY KEY  (`id`),
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos laborales del Centro de Trabajo';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `payroll_workplace`
#

LOCK TABLES `payroll_workplace` WRITE;
ALTER TABLE `payroll_workplace` DISABLE KEYS;
ALTER TABLE `payroll_workplace` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `pcategory`
#

DROP TABLE IF EXISTS `pcategory`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `pcategory` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Categoria',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Categoria',
  `detail_pattern` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Patron para los detalles de Articulos',
  `pcategory_group` int(4) default NULL COMMENT 'Identificador del Grupo de Categorias',
  PRIMARY KEY  (`id`),
  KEY `IDX_PCATEGORY_PCATEGORY_GROUP` (`pcategory_group`),
  KEY `IDX_PCATEGORY_DOMAIN` (`domain`),
  CONSTRAINT `FK_PCATEGORY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PCATEGORY_PCATEGORY_GROUP` FOREIGN KEY (`pcategory_group`) REFERENCES `pcategory_group` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias de Productos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `pcategory`
#

LOCK TABLES `pcategory` WRITE;
ALTER TABLE `pcategory` DISABLE KEYS;
INSERT INTO `pcategory` VALUES (1,1,'GENERICA',NULL,NULL);
ALTER TABLE `pcategory` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `pcategory_group`
#

DROP TABLE IF EXISTS `pcategory_group`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `pcategory_group` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Grupo de Categorias',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Grupo de Categorias',
  PRIMARY KEY  (`id`),
  KEY `IDX_PCATEGORY_GROUP_DOMAIN` (`domain`),
  CONSTRAINT `FK_PCATEGORY_GROUP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Grupos de Categorias de Productos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `pcategory_group`
#

LOCK TABLES `pcategory_group` WRITE;
ALTER TABLE `pcategory_group` DISABLE KEYS;
ALTER TABLE `pcategory_group` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `pcategory_tree`
#

DROP TABLE IF EXISTS `pcategory_tree`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `pcategory_tree` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Nodo del Arbol de Categorias',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `parent` int(4) default NULL COMMENT 'Identificador de la Categoria padre',
  `child` int(4) default NULL COMMENT 'Identificador de la Categoria hijo',
  PRIMARY KEY  (`id`),
  KEY `IDX_PCATEGORY_TREE_PARENT_PCATEGORY` (`parent`),
  KEY `IDX_PCATEGORY_TREE_CHILD_PCATEGORY` (`child`),
  KEY `IDX_PCATEGORY_TREE_DOMAIN` (`domain`),
  CONSTRAINT `FK_PCATEGORY_TREE_CHILD_PCATEGORY` FOREIGN KEY (`child`) REFERENCES `pcategory` (`id`),
  CONSTRAINT `FK_PCATEGORY_TREE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PCATEGORY_TREE_PARENT_PCATEGORY` FOREIGN KEY (`parent`) REFERENCES `pcategory` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Arbol de Categorias de Productos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `pcategory_tree`
#

LOCK TABLES `pcategory_tree` WRITE;
ALTER TABLE `pcategory_tree` DISABLE KEYS;
ALTER TABLE `pcategory_tree` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `person`
#

DROP TABLE IF EXISTS `person`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `person` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro de la Persona',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `birth_date` date default NULL COMMENT 'Fecha de nacimiento de la Persona',
  `gender` tinyint(2) NOT NULL default '0' COMMENT 'Sexo de la Persona',
  `marital_status` tinyint(2) NOT NULL default '0' COMMENT 'Estado civil de la Persona',
  `social_security_num` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Numero de Seguridad Social de la Persona',
  `name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Nombre',
  `first_surname` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Primer Apellido ',
  `second_surname` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Segundo Apellido',
  PRIMARY KEY  (`registry`),
  KEY `IDX_PERSON_DOMAIN` (`domain`),
  CONSTRAINT `FK_PERSON_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PERSON_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Personas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `person`
#

LOCK TABLES `person` WRITE;
ALTER TABLE `person` DISABLE KEYS;
ALTER TABLE `person` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `pm_type_detail`
#

DROP TABLE IF EXISTS `pm_type_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `pm_type_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Forma de Pago',
  `description` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del detalle',
  PRIMARY KEY  (`id`),
  KEY `IDX_PM_TYPE_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_PM_TYPE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles por Tipo de Forma de Pago';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `pm_type_detail`
#

LOCK TABLES `pm_type_detail` WRITE;
ALTER TABLE `pm_type_detail` DISABLE KEYS;
ALTER TABLE `pm_type_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `pm_type_detail_account`
#

DROP TABLE IF EXISTS `pm_type_detail_account`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `pm_type_detail_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `pm_type_detail` int(4) NOT NULL default '0' COMMENT 'Identificador del Detalle por Tipo de Forma de Pago',
  `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `IDX_PM_TYPE_DETAIL_ACCOUNT_ACCOUNT` (`account`),
  KEY `IDX_PM_TYPE_DETAIL_ACCOUNT_PM_TYPE_DETAIL` (`pm_type_detail`),
  KEY `IDX_PM_TYPE_DETAIL_ACCOUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_PM_TYPE_DETAIL_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_PM_TYPE_DETAIL_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PM_TYPE_DETAIL_ACCOUNT_PM_TYPE_DETAIL` FOREIGN KEY (`pm_type_detail`) REFERENCES `pm_type_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Entidades Bancarias';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `pm_type_detail_account`
#

LOCK TABLES `pm_type_detail_account` WRITE;
ALTER TABLE `pm_type_detail_account` DISABLE KEYS;
ALTER TABLE `pm_type_detail_account` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `pos`
#

DROP TABLE IF EXISTS `pos`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `pos` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre',
  `invoiceable` tinyint(1) NOT NULL default '0' COMMENT 'Indicador de si es facturable',
  `item` int(4) default NULL COMMENT 'Identificador del Producto',
  PRIMARY KEY  (`id`),
  KEY `IDX_POS_WORKPLACE` (`workplace`),
  KEY `IDX_POS_DOMAIN` (`domain`),
  KEY `IDX_POS_ITEM` (`item`),
  CONSTRAINT `FK_POS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_POS_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_POS_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='TPV';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `pos`
#

LOCK TABLES `pos` WRITE;
ALTER TABLE `pos` DISABLE KEYS;
ALTER TABLE `pos` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `pos_shift`
#

DROP TABLE IF EXISTS `pos_shift`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `pos_shift` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `pos` int(4) NOT NULL COMMENT 'Identificador del TPV',
  `shift` tinyint(2) NOT NULL default '0' COMMENT 'Turno de trabajo',
  `user` int(4) NOT NULL COMMENT 'Identificador del Usuario',
  `start_time` datetime NOT NULL COMMENT 'Fecha-hora de apertura',
  `end_time` datetime default NULL COMMENT 'Fecha-hora de cierre',
  `initial_amount` double(15,2) default '0.00' COMMENT 'Efectivo inicial',
  PRIMARY KEY  (`id`),
  KEY `IDX_POS_SHIFT_POS` (`pos`),
  KEY `IDX_POS_SHIFT_USER` (`user`),
  KEY `IDX_POS_SHIFT_DOMAIN` (`domain`),
  CONSTRAINT `FK_POS_SHIFT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_POS_SHIFT_POS` FOREIGN KEY (`pos`) REFERENCES `pos` (`id`),
  CONSTRAINT `FK_POS_SHIFT_USER` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Turno de trabajo del TPV';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `pos_shift`
#

LOCK TABLES `pos_shift` WRITE;
ALTER TABLE `pos_shift` DISABLE KEYS;
ALTER TABLE `pos_shift` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `pos_shift_count`
#

DROP TABLE IF EXISTS `pos_shift_count`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `pos_shift_count` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `pos_shift` int(4) NOT NULL COMMENT 'Identificador del Turno de trabajo',
  `pay_method` int(4) default NULL COMMENT 'Identificador de la Forma de pago',
  `amount` double(15,2) default '0.00' COMMENT 'Total efectivo',
  PRIMARY KEY  (`id`),
  KEY `IDX_POS_SHIFT_COUNT_POS_SHIFT` (`pos_shift`),
  KEY `IDX_POS_SHIFT_COUNT_PAY_METHOD` (`pay_method`),
  KEY `IDX_POS_SHIFT_COUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_POS_SHIFT_COUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_POS_SHIFT_COUNT_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_POS_SHIFT_COUNT_POS_SHIFT` FOREIGN KEY (`pos_shift`) REFERENCES `pos_shift` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Arqueo del TPV';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `pos_shift_count`
#

LOCK TABLES `pos_shift_count` WRITE;
ALTER TABLE `pos_shift_count` DISABLE KEYS;
ALTER TABLE `pos_shift_count` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `process`
#

DROP TABLE IF EXISTS `process`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `process` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Proceso',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(30) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Proceso.',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Activo si o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROCESS_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROCESS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Procesos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `process`
#

LOCK TABLES `process` WRITE;
ALTER TABLE `process` DISABLE KEYS;
ALTER TABLE `process` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `process_detail`
#

DROP TABLE IF EXISTS `process_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `process_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle de Proceso',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_PROCESS_DETAIL_PROCESS` (`process`),
  KEY `IDX_PROCESS_DETAIL_WORKGROUP` (`workgroup`),
  KEY `IDX_PROCESS_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROCESS_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROCESS_DETAIL_PROCESS` FOREIGN KEY (`process`) REFERENCES `process` (`id`),
  CONSTRAINT `FK_PROCESS_DETAIL_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Procesos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `process_detail`
#

LOCK TABLES `process_detail` WRITE;
ALTER TABLE `process_detail` DISABLE KEYS;
ALTER TABLE `process_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `process_detail_transition`
#

DROP TABLE IF EXISTS `process_detail_transition`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `process_detail_transition` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `process_detail` int(11) NOT NULL COMMENT 'Identificador del Detalle del Proceso.',
  `process_transition_type` int(11) NOT NULL COMMENT 'Identificador del Tipo de Transicion.',
  `next_process_detail` int(11) NOT NULL COMMENT 'Identificador del siguiente Detalle del Proceso.',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROCESS_DETAIL_TRANSITION_PROCESS_DETAIL` (`process_detail`),
  KEY `IDX_PROCESS_DETAIL_TRANSITION_NEXT_PROCESS_DETAIL` (`next_process_detail`),
  KEY `IDX_PROCESS_DETAIL_TRANSITION_PROCESS_TRANSITION_TYPE` (`process_transition_type`),
  KEY `IDX_PROCESS_DETAIL_TRANSITION_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROCESS_DETAIL_TRANSITION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROCESS_DETAIL_TRANSITION_NEXT_PROCESS_DETAIL` FOREIGN KEY (`next_process_detail`) REFERENCES `process_detail` (`id`),
  CONSTRAINT `FK_PROCESS_DETAIL_TRANSITION_PROCESS_DETAIL` FOREIGN KEY (`process_detail`) REFERENCES `process_detail` (`id`),
  CONSTRAINT `FK_PROCESS_DETAIL_TRANSITION_PROCESS_TRANSITION_TYPE` FOREIGN KEY (`process_transition_type`) REFERENCES `process_transition_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Transiciones entre Detalles de Procesos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `process_detail_transition`
#

LOCK TABLES `process_detail_transition` WRITE;
ALTER TABLE `process_detail_transition` DISABLE KEYS;
ALTER TABLE `process_detail_transition` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `process_task`
#

DROP TABLE IF EXISTS `process_task`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `process_task` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Relacion entre Campaas, Actividades y Tareas',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `campaign` int(4) default NULL COMMENT 'Identificador de la Campaa',
  `process_detail` int(4) NOT NULL COMMENT 'Identificador del Detalle de Proceso',
  `task` int(4) NOT NULL COMMENT 'Identificador de la Tarea',
  PRIMARY KEY  (`id`),
  KEY `campaign` (`campaign`),
  KEY `IDX_PROCESS_TASK_CAMPAIGN` (`campaign`),
  KEY `IDX_PROCESS_TASK_TASK` (`task`),
  KEY `IDX_PROCESS_TASK_PROCESS_DETAIL` (`process_detail`),
  KEY `IDX_PROCESS_TASK_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROCESS_TASK_CAMPAIGN` FOREIGN KEY (`campaign`) REFERENCES `campaign` (`id`),
  CONSTRAINT `FK_PROCESS_TASK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROCESS_TASK_PROCESS_DETAIL` FOREIGN KEY (`process_detail`) REFERENCES `process_detail` (`id`),
  CONSTRAINT `FK_PROCESS_TASK_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Campaas, Actividades y Tareas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `process_task`
#

LOCK TABLES `process_task` WRITE;
ALTER TABLE `process_task` DISABLE KEYS;
ALTER TABLE `process_task` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `process_transition_type`
#

DROP TABLE IF EXISTS `process_transition_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `process_transition_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Tipo de Transicion',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROCESS_TRANSITION_TYPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROCESS_TRANSITION_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Transiciones entre Detalles de Procesos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `process_transition_type`
#

LOCK TABLES `process_transition_type` WRITE;
ALTER TABLE `process_transition_type` DISABLE KEYS;
ALTER TABLE `process_transition_type` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `product`
#

DROP TABLE IF EXISTS `product`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `product` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Producto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  UNIQUE KEY `IDX_PRODUCT_DOMAIN_CODE` (`domain`,`code`),
  KEY `IDX_PRODUCT_NAME` (`name`),
  KEY `IDX_PRODUCT_TAX_RETENTION` (`retention`),
  KEY `IDX_PRODUCT_PCATEGORY` (`category`),
  KEY `IDX_PRODUCT_BRAND` (`brand`),
  KEY `IDX_PRODUCT_TAX_VAT` (`vat`),
  KEY `IDX_PRODUCT_DOMAIN` (`domain`),
  CONSTRAINT `FK_PRODUCT_BRAND` FOREIGN KEY (`brand`) REFERENCES `brand` (`id`),
  CONSTRAINT `FK_PRODUCT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PRODUCT_PCATEGORY` FOREIGN KEY (`category`) REFERENCES `pcategory` (`id`),
  CONSTRAINT `FK_PRODUCT_TAX_RETENTION` FOREIGN KEY (`retention`) REFERENCES `tax` (`id`),
  CONSTRAINT `FK_PRODUCT_TAX_VAT` FOREIGN KEY (`vat`) REFERENCES `tax` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Productos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `product`
#

LOCK TABLES `product` WRITE;
ALTER TABLE `product` DISABLE KEYS;
ALTER TABLE `product` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `product_account`
#

DROP TABLE IF EXISTS `product_account`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `product_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Cuenta Contable del Producto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `product` int(4) NOT NULL default '0' COMMENT 'Identificador del Producto',
  `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Cuenta Contable del Producto',
  PRIMARY KEY  (`id`),
  KEY `IDX_PRODUCT_ACCOUNT_ACCOUNT` (`account`),
  KEY `IDX_PRODUCT_ACCOUNT_PRODUCT` (`product`),
  KEY `IDX_PRODUCT_ACCOUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_PRODUCT_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_PRODUCT_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PRODUCT_ACCOUNT_PRODUCT` FOREIGN KEY (`product`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Productos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `product_account`
#

LOCK TABLES `product_account` WRITE;
ALTER TABLE `product_account` DISABLE KEYS;
ALTER TABLE `product_account` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `project`
#

DROP TABLE IF EXISTS `project`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `project` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Proyecto',
  `alias` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Alias del Proyecto',
  `registry` int(4) NOT NULL COMMENT 'Identificador del Cliente (Potencial) asociado',
  `date` date NOT NULL COMMENT 'Fecha del Proyecto',
  `project_type` int(4) default NULL COMMENT 'Tipo de Proyecto',
  `tas` tinyint(1) default '0' COMMENT 'Indica si se trata de una Orden de Reparacion o Fabricacion',
  `commercial` tinyint(1) default '0' COMMENT 'Indica si se trata de una Operacion Comercial',
  `dossier` tinyint(1) default '0' COMMENT 'Indica si se trata de un Expediente de Cliente',
  `reservation` tinyint(1) default '0' COMMENT 'Indica si se trata de una Reserva',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indica si el Proyecto esta activo o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROJECT_REGISTRY` (`registry`),
  KEY `IDX_PROJECT_PROJECT_TYPE` (`project_type`),
  KEY `IDX_PROJECT_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_PROJECT_TYPE` FOREIGN KEY (`project_type`) REFERENCES `project_type` (`id`),
  CONSTRAINT `FK_PROJECT_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Proyectos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `project`
#

LOCK TABLES `project` WRITE;
ALTER TABLE `project` DISABLE KEYS;
ALTER TABLE `project` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `project_activity`
#

DROP TABLE IF EXISTS `project_activity`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `project_activity` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Actividad',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project` int(4) NOT NULL COMMENT 'Identificador del Expendiente',
  `activity_type` int(4) NOT NULL COMMENT 'Tipo de Actividad',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Activo, si o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROJECT_ACTIVITY_ACTIVITY_TYPE` (`activity_type`),
  KEY `IDX_PROJECT_ACTIVITY_PROJECT` (`project`),
  KEY `IDX_PROJECT_ACTIVITY_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_ACTIVITY_ACTIVITY_TYPE` FOREIGN KEY (`activity_type`) REFERENCES `activity_type` (`id`),
  CONSTRAINT `FK_PROJECT_ACTIVITY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_ACTIVITY_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `project_activity`
#

LOCK TABLES `project_activity` WRITE;
ALTER TABLE `project_activity` DISABLE KEYS;
ALTER TABLE `project_activity` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `project_commercial`
#

DROP TABLE IF EXISTS `project_commercial`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `project_commercial` (
  `project` int(4) NOT NULL COMMENT 'Identificador del Proyecto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_PROJECT_COMMERCIAL_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_COMMERCIAL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_COMMERCIAL_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PROJECT_COMMERCIAL_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`),
  CONSTRAINT `FK_PROJECT_COMMERCIAL_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Operaciones Comerciales';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `project_commercial`
#

LOCK TABLES `project_commercial` WRITE;
ALTER TABLE `project_commercial` DISABLE KEYS;
ALTER TABLE `project_commercial` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `project_dossier`
#

DROP TABLE IF EXISTS `project_dossier`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `project_dossier` (
  `project` int(4) NOT NULL COMMENT 'Identificador unico del Expediente',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `customer` int(4) NOT NULL COMMENT 'Identificador del Cliente',
  `number` varchar(16) collate latin1_spanish_ci NOT NULL COMMENT 'Numero de Expediente',
  `location` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Ubicacion del Expediente',
  `status` tinyint(2) NOT NULL COMMENT 'Estado del Expediente',
  PRIMARY KEY  (`project`),
  UNIQUE KEY `IDX_UNQ_PROJECT_DOSSIER_DOMAIN_NUMBER` (`domain`,`number`),
  KEY `IDX_PROJECT_DOSSIER_DOMAIN` (`domain`),
  KEY `IDX_PROJECT_DOSSIER_CUSTOMER` (`customer`),
  CONSTRAINT `FK_PROJECT_DOSSIER_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_PROJECT_DOSSIER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_DOSSIER_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Expedientes';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `project_dossier`
#

LOCK TABLES `project_dossier` WRITE;
ALTER TABLE `project_dossier` DISABLE KEYS;
ALTER TABLE `project_dossier` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `project_reservation`
#

DROP TABLE IF EXISTS `project_reservation`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `project_reservation` (
  `project` int(4) NOT NULL COMMENT 'Identificador del Proyecto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `hotel` int(4) NOT NULL COMMENT 'Identificador del Hotel',
  `code` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Localizador de la Reserva',
  `creation_date` datetime NOT NULL COMMENT 'Fecha de creacion',
  `modification_date` datetime default NULL COMMENT 'Fecha de modificacion',
  `start_date` date NOT NULL COMMENT 'Fecha de entrada',
  `start_time` datetime NOT NULL COMMENT 'Hora de entrada',
  `end_date` date NOT NULL COMMENT 'Fecha de salida',
  `end_time` datetime NOT NULL COMMENT 'Hora de salida',
  `seller` int(4) default NULL COMMENT 'Identificador del canal de venta',
  `agency` int(4) default NULL COMMENT 'Identificador de la agencia de viajes',
  `agency_commission_percent` double(5,2) default '0.00' COMMENT 'Porcentaje de comision de la agencia',
  `agency_commission_amount` double(15,2) default '0.00' COMMENT 'Importe de comision de la agencia',
  `agency_rebate` tinyint(1) NOT NULL COMMENT 'Indica si la agencia trabaja en modo descuento o no',
  `company` int(4) default NULL COMMENT 'Identificador de la empresa',
  `discount_percent` double(5,2) default '0.00' COMMENT 'Porcentaje de descuento',
  `discount_amount` double(15,2) default '0.00' COMMENT 'Importe de descuento',
  `booking_holder` tinyint(2) NOT NULL COMMENT 'Titular de la Reserva',
  `taxable_base` double(15,2) default '0.00' COMMENT 'Base imponible',
  `vat_quota` double(15,2) default '0.00' COMMENT 'Cuota de IVA',
  `other_tax_quota` double(15,2) default '0.00' COMMENT 'Cuota de otros Impuestos',
  `total` double(15,2) default '0.00' COMMENT 'Importe Total',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios',
  `remarks` text collate latin1_spanish_ci COMMENT 'Observaciones',
  `crs` tinyint(1) NOT NULL default '0' COMMENT 'Indica si el origen de la Reserva es un CRS',
  `crs_code` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo de la Reserva en el CRS',
  `status` tinyint(2) NOT NULL COMMENT 'Estado de la Reserva',
  PRIMARY KEY  (`project`),
  KEY `IDX_PROJECT_RESERVATION_CODE` (`code`),
  KEY `IDX_PROJECT_RESERVATION_HOTEL` (`hotel`),
  KEY `IDX_PROJECT_RESERVATION_SELLER` (`seller`),
  KEY `IDX_PROJECT_RESERVATION_AGENCY` (`agency`),
  KEY `IDX_PROJECT_RESERVATION_COMPANY` (`company`),
  KEY `IDX_PROJECT_RESERVATION_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_RESERVATION_AGENCY` FOREIGN KEY (`agency`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_PROJECT_RESERVATION_COMPANY` FOREIGN KEY (`company`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_PROJECT_RESERVATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_HOTEL` FOREIGN KEY (`hotel`) REFERENCES `hotel` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Reservas de Hotel';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `project_reservation`
#

LOCK TABLES `project_reservation` WRITE;
ALTER TABLE `project_reservation` DISABLE KEYS;
ALTER TABLE `project_reservation` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `project_reservation_guest`
#

DROP TABLE IF EXISTS `project_reservation_guest`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `project_reservation_guest` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation` int(4) NOT NULL COMMENT 'Identificador de la Reserva',
  `guest_index` tinyint(2) NOT NULL COMMENT 'Numero de Huesped',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre',
  `surname` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Apellidos',
  `treatment` varchar(4) collate latin1_spanish_ci default NULL COMMENT 'Tratamiento',
  `document` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Numero de documento de identificacion',
  `document_type` tinyint(2) default '0' COMMENT 'Tipo de documento',
  `document_country` varchar(2) collate latin1_spanish_ci default 'ES' COMMENT 'Pais del documento',
  `email` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Email',
  `phone` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Telefono',
  `address` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Direccion',
  `zip` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo postal',
  `city` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Ciudad',
  `province` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Provincia',
  `country` varchar(2) collate latin1_spanish_ci default NULL COMMENT 'Pais',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION` (`project_reservation`),
  KEY `IDX_PROJECT_RESERVATION_GUEST_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_RESERVATION_GUEST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION` FOREIGN KEY (`project_reservation`) REFERENCES `project_reservation` (`project`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Huespedes por Reserva';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `project_reservation_guest`
#

LOCK TABLES `project_reservation_guest` WRITE;
ALTER TABLE `project_reservation_guest` DISABLE KEYS;
ALTER TABLE `project_reservation_guest` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `project_reservation_room`
#

DROP TABLE IF EXISTS `project_reservation_room`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `project_reservation_room` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation` int(4) NOT NULL COMMENT 'Identificador de la Reserva',
  `room_index` tinyint(2) NOT NULL COMMENT 'Numero de Habitacion',
  `item` int(4) NOT NULL COMMENT 'Identificador del Tipo de Habitacion',
  `tariff` int(4) default NULL COMMENT 'Identificador de la Tarifa',
  `adults` smallint(2) default '0' COMMENT 'Numero de adultos',
  `children` smallint(2) default '0' COMMENT 'Numero de nios',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION` (`project_reservation`),
  KEY `IDX_PROJECT_RESERVATION_ROOM_ITEM` (`item`),
  KEY `IDX_PROJECT_RESERVATION_ROOM_DOMAIN` (`domain`),
  KEY `IDX_PROJECT_RESERVATION_ROOM_TARIFF` (`tariff`),
  CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION` FOREIGN KEY (`project_reservation`) REFERENCES `project_reservation` (`project`),
  CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Habitaciones por Reserva';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `project_reservation_room`
#

LOCK TABLES `project_reservation_room` WRITE;
ALTER TABLE `project_reservation_room` DISABLE KEYS;
ALTER TABLE `project_reservation_room` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `project_reservation_room_detail`
#

DROP TABLE IF EXISTS `project_reservation_room_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `project_reservation_room_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation_room` int(4) NOT NULL COMMENT 'Identificador de la Habitacion de la Reserva',
  `asset_activity` int(4) NOT NULL COMMENT 'Identificador de la Actividad de la Habitacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM` (`project_reservation_room`),
  KEY `IDX_PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY` (`asset_activity`),
  KEY `IDX_PROJECT_RESERVATION_ROOM_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY` FOREIGN KEY (`asset_activity`) REFERENCES `asset_activity` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM` FOREIGN KEY (`project_reservation_room`) REFERENCES `project_reservation_room` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Habitacion por Reserva';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `project_reservation_room_detail`
#

LOCK TABLES `project_reservation_room_detail` WRITE;
ALTER TABLE `project_reservation_room_detail` DISABLE KEYS;
ALTER TABLE `project_reservation_room_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `project_reservation_service`
#

DROP TABLE IF EXISTS `project_reservation_service`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `project_reservation_service` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation` int(4) NOT NULL COMMENT 'Identificador de la Reserva',
  `service_index` tinyint(2) NOT NULL COMMENT 'Numero de Servicio',
  `item` int(4) NOT NULL COMMENT 'Identificador del Servicio',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `extra` tinyint(1) NOT NULL default '0' COMMENT 'Indica si se trata de un Servicio extra',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION` (`project_reservation`),
  KEY `IDX_PROJECT_RESERVATION_SERVICE_ITEM` (`item`),
  KEY `IDX_PROJECT_RESERVATION_SERVICE_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_RESERVATION_SERVICE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_SERVICE_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION` FOREIGN KEY (`project_reservation`) REFERENCES `project_reservation` (`project`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Servicios por Reserva';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `project_reservation_service`
#

LOCK TABLES `project_reservation_service` WRITE;
ALTER TABLE `project_reservation_service` DISABLE KEYS;
ALTER TABLE `project_reservation_service` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `project_reservation_service_detail`
#

DROP TABLE IF EXISTS `project_reservation_service_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `project_reservation_service_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation_service` int(4) NOT NULL COMMENT 'Identificador del Servicio de la Reserva',
  `project_reservation_room_detail` int(4) default NULL COMMENT 'Identificador del Detalle de Habitacion de la Reserva',
  `effective_date` date NOT NULL COMMENT 'Fecha de efecto',
  `quantity` double(15,2) default '0.00' COMMENT 'Cantidad',
  `price` double(15,4) default '0.0000' COMMENT 'Precio',
  `taxable_base` double(15,4) default '0.0000' COMMENT 'Base imponible',
  `invoice_detail` int(4) default NULL COMMENT 'Identificador de la Linea de Factura',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROJECT_RESERVATION_SERVICE_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_PRJ_RESERVATION_SERVICE_DETAIL_PRJ_RESERVATION_SERVICE` (`project_reservation_service`),
  KEY `IDX_PRJ_RESERVATION_SERVICE_DETAIL_PRJ_RESERVATION_ROOM_DETAIL` (`project_reservation_room_detail`),
  KEY `IDX_PROJECT_RESERVATION_SERVICE_DETAIL_INVOICE_DETAIL` (`invoice_detail`),
  CONSTRAINT `FK_PRJ_RESERVATION_SERVICE_DETAIL_PRJ_RESERVATION_ROOM_DETAIL` FOREIGN KEY (`project_reservation_room_detail`) REFERENCES `project_reservation_room_detail` (`id`),
  CONSTRAINT `FK_PRJ_RESERVATION_SERVICE_DETAIL_PRJ_RESERVATION_SERVICE` FOREIGN KEY (`project_reservation_service`) REFERENCES `project_reservation_service` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_SERVICE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_SERVICE_DETAIL_INVOICE_DETAIL` FOREIGN KEY (`invoice_detail`) REFERENCES `invoice_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Servicio por Reserva';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `project_reservation_service_detail`
#

LOCK TABLES `project_reservation_service_detail` WRITE;
ALTER TABLE `project_reservation_service_detail` DISABLE KEYS;
ALTER TABLE `project_reservation_service_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `project_tas`
#

DROP TABLE IF EXISTS `project_tas`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `project_tas` (
  `project` int(4) NOT NULL COMMENT 'Identificador del Proyecto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_PROJECT_TAS_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_TAS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_TAS_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PROJECT_TAS_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_PROJECT_TAS_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`),
  CONSTRAINT `FK_PROJECT_TAS_TAS_ITEM` FOREIGN KEY (`tas_item`) REFERENCES `tas_item` (`id`),
  CONSTRAINT `FK_PROJECT_TAS_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ordenes de Reparacion o Fabricacion';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `project_tas`
#

LOCK TABLES `project_tas` WRITE;
ALTER TABLE `project_tas` DISABLE KEYS;
ALTER TABLE `project_tas` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `project_type`
#

DROP TABLE IF EXISTS `project_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `project_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Tipo de Expediente',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Expediente',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Activo si o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROJECT_TYPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROJECT_TYPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Expedientes';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `project_type`
#

LOCK TABLES `project_type` WRITE;
ALTER TABLE `project_type` DISABLE KEYS;
ALTER TABLE `project_type` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `proposal`
#

DROP TABLE IF EXISTS `proposal`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `proposal` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `issue_date` date default NULL COMMENT 'Fecha de emision de la Propuesta',
  `workplace_department` int(4) default NULL COMMENT 'Identificador del Departamento',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `remarks` text collate latin1_spanish_ci COMMENT 'Observaciones de la Propuesta',
  `status` tinyint(2) default '0' COMMENT 'Estado de la Propuesta',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROPOSAL_DOMAIN` (`domain`),
  KEY `IDX_PROPOSAL_SCOPE` (`scope`),
  KEY `IDX_PROPOSAL_WORKPLACE` (`workplace`),
  KEY `IDX_PROPOSAL_WORKPLACE_DEPARTMENT` (`workplace_department`),
  CONSTRAINT `FK_PROPOSAL_WORKPLACE_DEPARTMENT` FOREIGN KEY (`workplace_department`) REFERENCES `workplace_department` (`id`),
  CONSTRAINT `FK_PROPOSAL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROPOSAL_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_PROPOSAL_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Propuestas de Compra';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `proposal`
#

LOCK TABLES `proposal` WRITE;
ALTER TABLE `proposal` DISABLE KEYS;
ALTER TABLE `proposal` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `proposal_detail`
#

DROP TABLE IF EXISTS `proposal_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `proposal_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `proposal` int(4) NOT NULL default '0' COMMENT 'Identificador de la Propuesta de Compra',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador del Articulo',
  `description` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `quantity` double(15,3) default '0.000' COMMENT 'Cantidad',
  `price` double default '0' COMMENT 'Precio',
  `discount_expr` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descuentos',
  `status` tinyint(2) default '0' COMMENT 'Estado del Detalle de la Propuesta',
  `supplier` int(4) default NULL COMMENT 'Identificador de Proveedor',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROPOSAL_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_PROPOSAL_DETAIL_PROPOSAL` (`proposal`),
  KEY `IDX_PROPOSAL_DETAIL_ITEM` (`item`),
  KEY `IDX_PROPOSAL_DETAIL_SUPPLIER` (`supplier`),
  CONSTRAINT `FK_PROPOSAL_DETAIL_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`),
  CONSTRAINT `FK_PROPOSAL_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROPOSAL_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_PROPOSAL_DETAIL_PROPOSAL` FOREIGN KEY (`proposal`) REFERENCES `proposal` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de la Propuesta de Compra';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `proposal_detail`
#

LOCK TABLES `proposal_detail` WRITE;
ALTER TABLE `proposal_detail` DISABLE KEYS;
ALTER TABLE `proposal_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `purchase`
#

DROP TABLE IF EXISTS `purchase`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `purchase` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Pedido de Compra',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  UNIQUE KEY `IDX_UNQ_PURCHASE_DOMAIN_SUPPLIER_SERIES_NUMBER` (`domain`,`supplier`,`series`,`number`),
  KEY `IDX_PURCHASE_SCOPE` (`scope`),
  KEY `IDX_PURCHASE_BANK` (`bank`),
  KEY `IDX_PURCHASE_PROJECT` (`project`),
  KEY `IDX_PURCHASE_SUPPLIER` (`supplier`),
  KEY `IDX_PURCHASE_PAY_METHOD` (`pay_method`),
  KEY `IDX_PURCHASE_WORKPLACE` (`workplace`),
  KEY `IDX_PURCHASE_DOMAIN` (`domain`),
  CONSTRAINT `FK_PURCHASE_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`),
  CONSTRAINT `FK_PURCHASE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PURCHASE_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_PURCHASE_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PURCHASE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_PURCHASE_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`),
  CONSTRAINT `FK_PURCHASE_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Pedidos de Compra';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `purchase`
#

LOCK TABLES `purchase` WRITE;
ALTER TABLE `purchase` DISABLE KEYS;
ALTER TABLE `purchase` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `purchase_detail`
#

DROP TABLE IF EXISTS `purchase_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `purchase_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle del Pedido de Compra',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  `proposal_detail` int(4) default NULL COMMENT 'Identificador del Detalle de Solicitud',
  `delivered` double default '0' COMMENT 'Cantidad entregada del Detalle de Pedido',
  PRIMARY KEY  (`id`),
  KEY `IDX_PURCHASE_DETAIL_PROJECT` (`project`),
  KEY `IDX_PURCHASE_DETAIL_PURCHASE` (`purchase`),
  KEY `IDX_PURCHASE_DETAIL_ITEM` (`item`),
  KEY `IDX_PURCHASE_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_PURCHASE_DETAIL_PROPOSAL_DETAIL` (`proposal_detail`),
  CONSTRAINT `FK_PURCHASE_DETAIL_PROPOSAL_DETAIL` FOREIGN KEY (`proposal_detail`) REFERENCES `proposal_detail` (`id`),
  CONSTRAINT `FK_PURCHASE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PURCHASE_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_PURCHASE_DETAIL_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PURCHASE_DETAIL_PURCHASE` FOREIGN KEY (`purchase`) REFERENCES `purchase` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Pedido de Compra';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `purchase_detail`
#

LOCK TABLES `purchase_detail` WRITE;
ALTER TABLE `purchase_detail` DISABLE KEYS;
ALTER TABLE `purchase_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `qualification`
#

DROP TABLE IF EXISTS `qualification`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `qualification` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Calificacion',
  `code` char(5) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo de la Calificacion',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de la Calificacion',
  `min_value` double(15,3) NOT NULL default '0.000' COMMENT 'Limite inferior de la Calificacion',
  `max_value` double(15,3) NOT NULL default '0.000' COMMENT 'Limite superior de la Calificacion',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Calificaciones';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `qualification`
#

LOCK TABLES `qualification` WRITE;
ALTER TABLE `qualification` DISABLE KEYS;
ALTER TABLE `qualification` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `quality_skill`
#

DROP TABLE IF EXISTS `quality_skill`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `quality_skill` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Aptitud Calidad',
  `code` char(5) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo de la Aptitud Calidad',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de la Aptitud Calidad',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Aptitudes Calidad';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `quality_skill`
#

LOCK TABLES `quality_skill` WRITE;
ALTER TABLE `quality_skill` DISABLE KEYS;
ALTER TABLE `quality_skill` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `question`
#

DROP TABLE IF EXISTS `question`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `question` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Pregunta esta activa o no',
  `question_text` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Texto de la Pregunta',
  `type` tinyint(2) NOT NULL COMMENT 'Tipo de Pregunta',
  `argument` text collate latin1_spanish_ci COMMENT 'Argumentacion de la Pregunta',
  `alias` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Alias de la Pregunta',
  PRIMARY KEY  (`id`),
  KEY `IDX_QUESTION_DOMAIN` (`domain`),
  CONSTRAINT `FK_QUESTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Preguntas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `question`
#

LOCK TABLES `question` WRITE;
ALTER TABLE `question` DISABLE KEYS;
ALTER TABLE `question` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `question_value`
#

DROP TABLE IF EXISTS `question_value`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `question_value` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `value_text` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Valor de tipo texto',
  `value_number` double(15,3) default NULL COMMENT 'Valor de tipo numerico',
  `value_date` datetime default NULL COMMENT 'Valor de tipo fecha',
  PRIMARY KEY  (`id`),
  KEY `IDX_QUESTION_VALUE_QUESTION` (`question`),
  KEY `IDX_QUESTION_VALUE_DOMAIN` (`domain`),
  CONSTRAINT `FK_QUESTION_VALUE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_QUESTION_VALUE_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Valores de Preguntas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `question_value`
#

LOCK TABLES `question_value` WRITE;
ALTER TABLE `question_value` DISABLE KEYS;
ALTER TABLE `question_value` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `raddinfo`
#

DROP TABLE IF EXISTS `raddinfo`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `raddinfo` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL COMMENT 'Identificador de la Persona o Empresa',
  `attribute` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Atributo adicional',
  `value` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Valor del atributo adicional',
  `value_date` date NOT NULL COMMENT 'Fecha del valor del atributo',
  PRIMARY KEY  (`id`),
  KEY `IDX_RADDINFO_REGISTRY` (`registry`),
  KEY `IDX_RADDINFO_DOMAIN` (`domain`),
  CONSTRAINT `FK_RADDINFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RADDINFO_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Informacion adicional de la Persona o Empresa';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `raddinfo`
#

LOCK TABLES `raddinfo` WRITE;
ALTER TABLE `raddinfo` DISABLE KEYS;
ALTER TABLE `raddinfo` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `raddress`
#

DROP TABLE IF EXISTS `raddress`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `raddress` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Direccion de la Persona o Empresa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_RADDRESS_REGISTRY` (`registry`),
  KEY `IDX_RADDRESS_GEOZONE` (`geozone`),
  KEY `IDX_RADDRESS_DOMAIN` (`domain`),
  CONSTRAINT `FK_RADDRESS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RADDRESS_GEOZONE` FOREIGN KEY (`geozone`) REFERENCES `geozone` (`id`),
  CONSTRAINT `FK_RADDRESS_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Direcciones de Personas o Empresas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `raddress`
#

LOCK TABLES `raddress` WRITE;
ALTER TABLE `raddress` DISABLE KEYS;
INSERT INTO `raddress` VALUES (1,1,1,0,NULL,NULL,'Calle Sagasta, nº 15-4º Izda. ',NULL,'','','28004','Madrid',28,NULL);
ALTER TABLE `raddress` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `rattach`
#

DROP TABLE IF EXISTS `rattach`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `rattach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Archivo Adjunto de la Persona o Empresa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_RATTACH_SCOPE` (`scope`),
  KEY `IDX_RATTACH_CATEGORY` (`category`),
  KEY `IDX_RATTACH_REGISTRY` (`registry`),
  KEY `IDX_RATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_RATTACH_CATEGORY` FOREIGN KEY (`category`) REFERENCES `category` (`id`),
  CONSTRAINT `FK_RATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RATTACH_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_RATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Personas o Empresas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `rattach`
#

LOCK TABLES `rattach` WRITE;
ALTER TABLE `rattach` DISABLE KEYS;
INSERT INTO `rattach` VALUES (1,1,1,NULL,1,'aon-logo','GIF89a�\0g\0�\0\0\0\0\0���~��[�|><<���\0f3������^idnD)))��������nqq������RRR���B�hl��{{{�ν������������)�Z��Ŧ��k9���+12���JJJZZZ���s{z��æʷxK���fff�����q��:::!!!M�q����������ï		<�c���n>���s��y���Ī�����(~S��ԯ��Q�sh��a��M�l���\ZvH����Żfff���y���·��餞�7�]���BBB���RJJ��΂�����X�uv��������������������!1�Z������#{O��� vK��P�l���u��qA��ś����ﵵ���؄�����������sssQXVMPN���333GEE���!�\0\0,\0\0\0\0�\0g\0\0��`����������������������������������������������������������������������������·J[8n>P85R=��i.8FK���C��R@3 �o<��C7`��� ��n���Xx`G{<�(+@$�D)���E�@X�\Zi(�\n�aE�%m�,�3�_!��C�Re*Zԕi2�\n#1H�K7c�OS��@d�Gcڣ����4��s`�QHY�K����P�Ψ��\0!�pƩ�NG���z\0�{n�g\'11���\0�9�Xz�W+qPXR��-�!��N&��x�rhK=DT�PS\nqKy])�u*<�i�B.�����G�*�p�C��2n�C�aO�\r��Hb��G�[���OO.|�������|�eቅ���s�}�� U: d���q�4�sD\0&�\0\Z�PQ�E�2�P�h��=P���\'c�q\0r��h3أ�H��p\0�U��`0�[W�uH9F�L\n�$$2��{��CW@��w��L0�d�I��Y�U�\Z\",P�Zr@\n��=m�a�\'�)�M=*B�@�L�d�kV� ��х����Z�d�P���P(b1��1f����c:e`y�,�p�=K��`%=l��21��d������B�c�m�S�xt��c�0C1��j�G`����:l��&U�p�Hؚa��@A`� �K����+�k-����c�z���B�u�k�\\И:h���́�>��&=� FA�X������\n��`ƭ��������}�y�K\rWJ�)`�\n2�H#}�ẃ�f)l!����q�Xg��-��#�B\"�o���%���O��GÆ\0��\\\0�����{����\'c��\"{Hp�%]��BBl@���\\PB$�&�a$9��I��XN��`TчQ\r�a�׃P�G!B<ѯ�\"/p�v�~�.?r@g�A�.HV�«�yH��q�	OO�O���}�[E�;��!�Aȇv<\03�Bڃ�S�p�\"s\0�t��;��@�BT�^��`\n<A���+�	!xS~�l�O��VG<�o����@ny�#D>8��������\0x��9��0�1cߢ7+@\0y�\Z�/�\0^�A�@����	#��\0\0�b���� ��C���9��b��\n�	�KχD��/D��у���#&8�;�`\nf�Cb79A\0qx�� L�\0D��v�B�c�E��.*#!8`	�`E�L0IA��	����py~P�4),�`���Q�G �b���v46�6D61�\\\n�j4�	b\0&H�W+���Ԓh��`�>b����9��x�!&�A1�\0v�� ��x�AN;�(\0���.w��;9Ї�\0%����x %\0Z �9�.0�d\0\0\nᖩ�� ���T�V8�	��N$�\0`TD��A8\0\0d���<�`\0��� �N3��W��Ro�*O�x�(�f�*� m�C� p�<�N��\"�W�P�Z% ���=tR0��2���E0�vYX!8��`��=4 ��. �Ж���	\\d�H��d[D iG�t:\"l�x�m/=H�vk�\r�pg��\"C-��qqp����Dd��c`�%D�\n�n�j����A��c(��G\"��vs����\n��|*�����E\0z8����9����|ǔ�������T�<�q���P��ﰐ2A�����\0 �Z@����o��p2;�L�)&Ď��O�J�ƅH��>0���,(�A��sk|��p��;�κ�\no.��;��g��Vq�t	\r�_\nv��,�p{����f�XB��rU8��\Z�>� �-���ׂ>���T��|F:`\n>3�%{�)����Ȧ��g1�\04h&�q����!�P���C��\0`L�z����ްl��`���������Lr�ۃ��`�%�\nO��1�|��:c�TB8��-(/6w\0~&�z8�	*���A�\\���*��-���pdB����c��@i0a�\rO]��_-��{B�(@�9�ËN��R�~��Ǒ�^��S�¶�8�9��r�������c=艛H�+�r�Zm�\"\"�����3D:ۄ������p����{��`���N�����hO���������p����N���\"\0;',0,NULL,0,NULL);
ALTER TABLE `rattach` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `rbank`
#

DROP TABLE IF EXISTS `rbank`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `rbank` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Cuenta Bancaria de la Persona o Empresa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL default '0' COMMENT 'Identificador del Registro de la Persona o Empresa',
  `bank` int(4) NOT NULL default '0' COMMENT 'Identificador de la Entidad Bancaria',
  `bank_account` char(30) collate latin1_spanish_ci default NULL COMMENT 'Numero de Cuenta Bancaria de la Persona o Empresa',
  `sufix` char(3) collate latin1_spanish_ci default NULL COMMENT 'Sufijo de Cuenta Bancaria para Remesas',
  `alias` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Alias de la Cuenta Bancaria',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indica si la Cuenta Bancaria esta activa o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_RBANK_REGISTRY` (`registry`),
  KEY `IDX_RBANK_BANK` (`bank`),
  KEY `IDX_RBANK_DOMAIN` (`domain`),
  CONSTRAINT `FK_RBANK_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`),
  CONSTRAINT `FK_RBANK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RBANK_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos de Cuentas Bancarias de Personas o Empresas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `rbank`
#

LOCK TABLES `rbank` WRITE;
ALTER TABLE `rbank` DISABLE KEYS;
ALTER TABLE `rbank` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `rbank_account`
#

DROP TABLE IF EXISTS `rbank_account`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `rbank_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Cuenta Contable de la Cuenta Bancaria',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `rbank` int(4) NOT NULL default '0' COMMENT 'Identificador de la Cuenta Bancaria',
  `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `IDX_RBANK_ACCOUNT_ACCOUNT` (`account`),
  KEY `IDX_RBANK_ACCOUNT_RBANK` (`rbank`),
  KEY `IDX_RBANK_ACCOUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_RBANK_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_RBANK_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RBANK_ACCOUNT_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Entidades Bancarias';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `rbank_account`
#

LOCK TABLES `rbank_account` WRITE;
ALTER TABLE `rbank_account` DISABLE KEYS;
ALTER TABLE `rbank_account` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `rdir_staff`
#

DROP TABLE IF EXISTS `rdir_staff`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `rdir_staff` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Relacion entre Empresas y sus Directivos',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_RDIR_STAFF_REGISTRY` (`registry`),
  KEY `IDX_RDIR_STAFF_DOMAIN` (`domain`),
  CONSTRAINT `FK_RDIR_STAFF_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RDIR_STAFF_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Empresas y sus Directivos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `rdir_staff`
#

LOCK TABLES `rdir_staff` WRITE;
ALTER TABLE `rdir_staff` DISABLE KEYS;
ALTER TABLE `rdir_staff` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `record_data`
#

DROP TABLE IF EXISTS `record_data`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `record_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Dato Registral',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_RECORD_DATA_RATTACH` (`attach`),
  KEY `IDX_RECORD_DATA_REGISTRY` (`registry`),
  KEY `IDX_RECORD_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_RECORD_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RECORD_DATA_RATTACH` FOREIGN KEY (`attach`) REFERENCES `rattach` (`id`),
  CONSTRAINT `FK_RECORD_DATA_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos Registrales';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `record_data`
#

LOCK TABLES `record_data` WRITE;
ALTER TABLE `record_data` DISABLE KEYS;
ALTER TABLE `record_data` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `registry`
#

DROP TABLE IF EXISTS `registry`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `registry` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Persona o Empresa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `document` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Numero de Documento de la Persona o Empresa',
  `document_type` tinyint(2) default '0' COMMENT 'Tipo de documento (NIF, CIF...)',
  `document_country` varchar(2) collate latin1_spanish_ci default 'ES' COMMENT 'Pais del documento',
  `name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Nombre de la Persona o Empresa',
  `alias` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Alias de la Persona o Empresa',
  `type` tinyint(2) default NULL COMMENT 'Tipo (Persona o Empresa)',
  `nationality` varchar(2) collate latin1_spanish_ci default 'ES' COMMENT 'Nacionalidad',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad',
  PRIMARY KEY  (`id`),
  KEY `IDX_REGISTRY_NAME` (`name`),
  KEY `IDX_REGISTRY_DOCUMENT` (`document`),
  KEY `IDX_REGISTRY_DOMAIN` (`domain`),
  CONSTRAINT `FK_REGISTRY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Registro de Personas o Empresas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `registry`
#

LOCK TABLES `registry` WRITE;
ALTER TABLE `registry` DISABLE KEYS;
INSERT INTO `registry` VALUES (1,1,'00000000',0,'ES','Toledo & Asociados Asesores Laborales','TAI',0,'ES',0);
ALTER TABLE `registry` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `relationship`
#

DROP TABLE IF EXISTS `relationship`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `relationship` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Tipo de Relacion',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Relacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_RELATIONSHIP_DOMAIN` (`domain`),
  CONSTRAINT `FK_RELATIONSHIP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Relaciones entre Personas y/o Empresas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `relationship`
#

LOCK TABLES `relationship` WRITE;
ALTER TABLE `relationship` DISABLE KEYS;
ALTER TABLE `relationship` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `rmedia`
#

DROP TABLE IF EXISTS `rmedia`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `rmedia` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Medio de Contacto de la Persona o Empresa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL default '0' COMMENT 'Identificador del Registro de la Persona o Empresa',
  `media` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Medio de Contacto de la Persona o Empresa',
  `value` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Valor del Medio de Contacto de la Persona o Empresa',
  `comment` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Comentarios acerca del Medio de Contacto de la Persona o Empresa',
  `administrative` tinyint(1) default '1' COMMENT 'Indica si el Contacto es de caracter administrativo',
  `commercial` tinyint(1) default '1' COMMENT 'Indica si el Contacto es de caracter comercial',
  `technical` tinyint(1) default '1' COMMENT 'Indica si el Contacto es de caracter tecnico',
  `raddress` int(4) default NULL COMMENT 'Direccion del contacto',
  PRIMARY KEY  (`id`),
  KEY `IDX_RMEDIA_RADDRESS` (`raddress`),
  KEY `IDX_RMEDIA_REGISTRY` (`registry`),
  KEY `IDX_RMEDIA_DOMAIN` (`domain`),
  CONSTRAINT `FK_RMEDIA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RMEDIA_RADDRESS` FOREIGN KEY (`raddress`) REFERENCES `raddress` (`id`),
  CONSTRAINT `FK_RMEDIA_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Medios de Contacto de Personas o Empresas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `rmedia`
#

LOCK TABLES `rmedia` WRITE;
ALTER TABLE `rmedia` DISABLE KEYS;
INSERT INTO `rmedia` VALUES (1,1,1,1,'91 781.97.11',NULL,0,0,0,NULL),(2,1,1,3,'91 781.97.12',NULL,0,0,0,NULL),(3,1,1,4,'info@toledoyasociados.es',NULL,0,0,0,NULL),(4,1,1,5,'http://toledoyasociados.es',NULL,0,0,0,NULL);
ALTER TABLE `rmedia` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `rnote`
#

DROP TABLE IF EXISTS `rnote`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `rnote` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Nota de la Persona o Empresa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL default '0' COMMENT 'Identificador del Registro de la Persona o Empresa',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Nota',
  `note_date` date default NULL COMMENT 'Fecha de la Nota',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios de la Nota',
  `note_type` tinyint(2) default NULL,
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad de la Nota',
  PRIMARY KEY  (`id`),
  KEY `IDX_RNOTE_REGISTRY` (`registry`),
  KEY `IDX_RNOTE_DOMAIN` (`domain`),
  CONSTRAINT `FK_RNOTE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RNOTE_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Notas de Personas o Empresas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `rnote`
#

LOCK TABLES `rnote` WRITE;
ALTER TABLE `rnote` DISABLE KEYS;
ALTER TABLE `rnote` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `room`
#

DROP TABLE IF EXISTS `room`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `room` (
  `asset` int(4) NOT NULL COMMENT 'Identificador del Activo',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `hotel` int(4) NOT NULL COMMENT 'Identificador del Hotel',
  `item` int(4) NOT NULL COMMENT 'Identificador del Producto',
  PRIMARY KEY  (`asset`),
  KEY `IDX_ROOM_DOMAIN` (`domain`),
  KEY `IDX_ROOM_HOTEL` (`hotel`),
  KEY `IDX_ROOM_ITEM` (`item`),
  CONSTRAINT `FK_ROOM_ASSET` FOREIGN KEY (`asset`) REFERENCES `asset` (`id`),
  CONSTRAINT `FK_ROOM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ROOM_HOTEL` FOREIGN KEY (`hotel`) REFERENCES `hotel` (`id`),
  CONSTRAINT `FK_ROOM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Habitaciones de Hotel';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `room`
#

LOCK TABLES `room` WRITE;
ALTER TABLE `room` DISABLE KEYS;
ALTER TABLE `room` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `rpaymethod`
#

DROP TABLE IF EXISTS `rpaymethod`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `rpaymethod` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Forma de Pago de la Persona o Empresa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL COMMENT 'Identificador del Registro de la Persona o Empresa',
  `pay_method` int(4) NOT NULL COMMENT 'Identificador de la Forma de Pago',
  `rbank` int(4) default NULL COMMENT 'Identificador de la Entidad Bancaria',
  `number_of_pymnts` smallint(2) default '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint(2) default '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint(2) default '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) collate latin1_spanish_ci default '0' COMMENT 'Dias de pago',
  PRIMARY KEY  (`id`),
  KEY `IDX_RPAYMETHOD_PAY_METHOD` (`pay_method`),
  KEY `IDX_RPAYMETHOD_RBANK` (`rbank`),
  KEY `IDX_RPAYMETHOD_REGISTRY` (`registry`),
  KEY `IDX_RPAYMETHOD_DOMAIN` (`domain`),
  CONSTRAINT `FK_RPAYMETHOD_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RPAYMETHOD_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_RPAYMETHOD_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`),
  CONSTRAINT `FK_RPAYMETHOD_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos de la Forma de Pago de la Persona o Empresa';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `rpaymethod`
#

LOCK TABLES `rpaymethod` WRITE;
ALTER TABLE `rpaymethod` DISABLE KEYS;
ALTER TABLE `rpaymethod` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `rrelationship`
#

DROP TABLE IF EXISTS `rrelationship`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `rrelationship` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Relacion',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL COMMENT 'Identificador de la Persona o Empresa que tiene la Relacion',
  `related_registry` int(4) NOT NULL COMMENT 'Identificador de la Persona o Empresa relacionada',
  `relationship` int(4) NOT NULL COMMENT 'Identificador del Tipo de Relacin',
  `comments` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Comentarios de la Relacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_RRELATIONSHIP_REGISTRY` (`registry`),
  KEY `IDX_RRELATIONSHIP_RELATED_REGISTRY` (`related_registry`),
  KEY `IDX_RRELATIONSHIP_RELATIONSHIP` (`relationship`),
  KEY `IDX_RRELATIONSHIP_DOMAIN` (`domain`),
  CONSTRAINT `FK_RRELATIONSHIP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RRELATIONSHIP_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_RRELATIONSHIP_RELATED_REGISTRY` FOREIGN KEY (`related_registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_RRELATIONSHIP_RELATIONSHIP` FOREIGN KEY (`relationship`) REFERENCES `relationship` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relaciones entre Personas y/o Empresas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `rrelationship`
#

LOCK TABLES `rrelationship` WRITE;
ALTER TABLE `rrelationship` DISABLE KEYS;
ALTER TABLE `rrelationship` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `rsegment`
#

DROP TABLE IF EXISTS `rsegment`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `rsegment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL COMMENT 'Identificador de Persona o Empresa',
  `segment` int(4) NOT NULL COMMENT 'Identificador del Segmento',
  PRIMARY KEY  (`id`),
  KEY `IDX_RSEGMENT_REGISTRY` (`registry`),
  KEY `IDX_RSEGMENT_SEGMENT` (`segment`),
  KEY `IDX_RSEGMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_RSEGMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RSEGMENT_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_RSEGMENT_SEGMENT` FOREIGN KEY (`segment`) REFERENCES `segment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Segmentos de Personas o Empresas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `rsegment`
#

LOCK TABLES `rsegment` WRITE;
ALTER TABLE `rsegment` DISABLE KEYS;
ALTER TABLE `rsegment` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `salary`
#

DROP TABLE IF EXISTS `salary`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `salary` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Nomina',
  `contract` int(4) NOT NULL COMMENT 'Contrato',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio liquidacin',
  `end_date` date NOT NULL COMMENT 'Fecha de finalizacion liquidacin',
  `enterprise_name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Nombre de la empresa',
  `enterprise_address` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Domicilio de la empresa',
  `enterprise_document` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Numero de Documento de la Empresa',
  `ccc` char(11) collate latin1_spanish_ci default NULL COMMENT 'Valor del Codigo Cuenta Cotizacion',
  `employee_name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Nombre del trabajador',
  `social_security_number` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Numero de la seguridad social',
  `employee_document` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Numero de Documento de la Persona',
  `seniority_date` date default NULL COMMENT 'Fecha de antiguedad',
  `quote_group` varchar(2) collate latin1_spanish_ci default NULL COMMENT 'Grupo de Cotizacin',
  `category` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Categoria o grupo profesional',
  `registration` int(4) NOT NULL COMMENT 'Nmero libro de matricula',
  `time_units` int(4) NOT NULL COMMENT 'total dias/horas',
  `total_payment` double(15,3) NOT NULL default '0.000' COMMENT 'Total devengado',
  `total_deduction` double(15,3) NOT NULL default '0.000' COMMENT 'Total a deducir',
  `total_liquid` double(15,3) NOT NULL default '0.000' COMMENT 'Liquido total a percibir',
  `total_enterprise` double(15,3) NOT NULL default '0.000' COMMENT 'Cuota total de la empresa',
  `issue_date` date NOT NULL COMMENT 'Fecha de emisin',
  `remuneration` double(15,3) NOT NULL default '0.000' COMMENT 'Remuneracin mensual',
  `pro_ext_base` double(15,3) NOT NULL default '0.000' COMMENT 'Base prorraterreada de pagas extras',
  `it_base` double(15,3) NOT NULL default '0.000' COMMENT 'Base de IT',
  `raw_cgc_base` double(15,3) NOT NULL default '0.000' COMMENT 'Base efectiva de cotizacion por contingencias comunes ',
  `cgc_base` double(15,3) NOT NULL default '0.000' COMMENT 'Base de cotizacion por contingencias comunes',
  `hextra_base` double(15,3) NOT NULL default '0.000' COMMENT 'Base de cotizacion adicional por horas extraordinarias estructurales',
  `non_hextra_base` double(15,3) NOT NULL default '0.000' COMMENT 'Base de cotizacion adicional por horas extraordinarias no estructurales',
  `cgp_base` double(15,3) NOT NULL default '0.000' COMMENT 'Base de cotizacion por contingencias profesionales',
  `money_irpf_base` double(15,3) NOT NULL default '0.000' COMMENT 'Salario en dinero sujeto a retencin I.R.P.F',
  `inkind_irpf_base` double(15,3) NOT NULL default '0.000' COMMENT 'Salario en especie sujeto a retencin I.R.P.F',
  `irpf_base` double(15,3) NOT NULL default '0.000' COMMENT 'Base sujeta a retencin I.R.P.F',
  `social_security_contributions` double(15,3) NOT NULL default '0.000' COMMENT 'Aportaciones a la Seguridad Social',
  `total_irpf` double(15,3) NOT NULL default '0.000' COMMENT 'Total retencin aplicada ',
  `charge_date` date NOT NULL COMMENT 'Fecha de cobro',
  PRIMARY KEY  (`id`),
  KEY `IDX_SALARY_CONTRACT` (`contract`),
  KEY `IDX_SALARY_DOMAIN` (`domain`),
  CONSTRAINT `FK_SALARY_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_SALARY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Recibo del pago de salarios';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `salary`
#

LOCK TABLES `salary` WRITE;
ALTER TABLE `salary` DISABLE KEYS;
ALTER TABLE `salary` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `salary_bonus`
#

DROP TABLE IF EXISTS `salary_bonus`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `salary_bonus` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios',
  `bonus_concept` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Codigo del concepto',
  `amount` double(15,3) default '0.000' COMMENT 'Importe',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_SALARY_BONUS_SALARY` (`salary`),
  KEY `IDX_SALARY_BONUS_DOMAIN` (`domain`),
  CONSTRAINT `FK_SALARY_BONUS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALARY_BONUS_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bonificaciones';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `salary_bonus`
#

LOCK TABLES `salary_bonus` WRITE;
ALTER TABLE `salary_bonus` DISABLE KEYS;
ALTER TABLE `salary_bonus` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `salary_cost`
#

DROP TABLE IF EXISTS `salary_cost`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `salary_cost` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios',
  `amount` double(15,3) default '0.000' COMMENT 'Importe',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `type` tinyint(2) default NULL COMMENT 'Tipo de deduccion Salarial',
  `cost_concept` varchar(10) collate latin1_spanish_ci default NULL COMMENT 'Codigo del concepto',
  PRIMARY KEY  (`id`),
  KEY `IDX_SALARY_COST_SALARY` (`salary`),
  KEY `IDX_SALARY_COST_DOMAIN` (`domain`),
  CONSTRAINT `FK_SALARY_COST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALARY_COST_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Costos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `salary_cost`
#

LOCK TABLES `salary_cost` WRITE;
ALTER TABLE `salary_cost` DISABLE KEYS;
ALTER TABLE `salary_cost` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `salary_data`
#

DROP TABLE IF EXISTS `salary_data`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `salary_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` tinyint(2) NOT NULL COMMENT 'Nombre',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Importe',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_SALARY_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_SALARY_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos de la nomina';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `salary_data`
#

LOCK TABLES `salary_data` WRITE;
ALTER TABLE `salary_data` DISABLE KEYS;
ALTER TABLE `salary_data` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `salary_deduction`
#

DROP TABLE IF EXISTS `salary_deduction`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `salary_deduction` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios',
  `type` tinyint(2) default NULL COMMENT 'Tipo de deduccin Salarial',
  `deduction_concept` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Codigo del concepto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Frmula',
  `amount` double(15,3) default '0.000' COMMENT 'Importe',
  PRIMARY KEY  (`id`),
  KEY `IDX_SALARY_DEDUCTION_SALARY` (`salary`),
  KEY `IDX_SALARY_DEDUCTION_DOMAIN` (`domain`),
  CONSTRAINT `FK_SALARY_DEDUCTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALARY_DEDUCTION_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Deducciones';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `salary_deduction`
#

LOCK TABLES `salary_deduction` WRITE;
ALTER TABLE `salary_deduction` DISABLE KEYS;
ALTER TABLE `salary_deduction` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `salary_embargo`
#

DROP TABLE IF EXISTS `salary_embargo`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `salary_embargo` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios',
  `contract_embargo` int(4) NOT NULL COMMENT 'Embargo',
  `amount` double(15,3) default '0.000' COMMENT 'Importe',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_SALARY_EMBARGO_CONTRACT_EMBARGO` (`contract_embargo`),
  KEY `IDX_SALARY_EMBARGO_SALARY` (`salary`),
  KEY `IDX_SALARY_EMBARGO_DOMAIN` (`domain`),
  CONSTRAINT `FK_SALARY_EMBARGO_CONTRACT_EMBARGO` FOREIGN KEY (`contract_embargo`) REFERENCES `contract_embargo` (`id`),
  CONSTRAINT `FK_SALARY_EMBARGO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALARY_EMBARGO_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Embargos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `salary_embargo`
#

LOCK TABLES `salary_embargo` WRITE;
ALTER TABLE `salary_embargo` DISABLE KEYS;
ALTER TABLE `salary_embargo` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `salary_payment`
#

DROP TABLE IF EXISTS `salary_payment`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `salary_payment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Percepcin Salarial',
  `payment_concept` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Codigo del concepto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Frmula',
  `amount` double(15,3) default '0.000' COMMENT 'Importe',
  PRIMARY KEY  (`id`),
  KEY `IDX_SALARY_PAYMENT_SALARY` (`salary`),
  KEY `IDX_SALARY_PAYMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_SALARY_PAYMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALARY_PAYMENT_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Percepciones salariales';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `salary_payment`
#

LOCK TABLES `salary_payment` WRITE;
ALTER TABLE `salary_payment` DISABLE KEYS;
ALTER TABLE `salary_payment` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `sales`
#

DROP TABLE IF EXISTS `sales`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `sales` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Pedido de Venta',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `scope` int(4) NOT NULL default '1' COMMENT 'Ambito del Pedido',
  `number_of_pymnts` smallint(2) default '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint(2) default '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint(2) default '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) collate latin1_spanish_ci default '0' COMMENT 'Dias de pago',
  `bank` int(4) default NULL COMMENT 'Identificador de la Entidad Bancaria',
  `bank_account` varchar(30) collate latin1_spanish_ci default NULL COMMENT 'Numero de cuenta en la Entidad Bancaria',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_UNQ_SALES_DOMAIN_SERIES_NUMBER` (`domain`,`series`,`number`),
  KEY `IDX_SALES_SCOPE` (`scope`),
  KEY `IDX_SALES_BANK` (`bank`),
  KEY `IDX_SALES_PROJECT` (`project`),
  KEY `IDX_SALES_ISSUE_DATE` (`issue_date`),
  KEY `IDX_SALES_SELLER` (`seller`),
  KEY `IDX_SALES_CUSTOMER` (`customer`),
  KEY `IDX_SALES_RADDRESS` (`shipping_address`),
  KEY `IDX_SALES_PAY_METHOD` (`pay_method`),
  KEY `IDX_SALES_WORKPLACE` (`workplace`),
  KEY `IDX_SALES_DOMAIN` (`domain`),
  CONSTRAINT `FK_SALES_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`),
  CONSTRAINT `FK_SALES_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_SALES_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALES_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_SALES_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_SALES_RADDRESS` FOREIGN KEY (`shipping_address`) REFERENCES `raddress` (`id`),
  CONSTRAINT `FK_SALES_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_SALES_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`),
  CONSTRAINT `FK_SALES_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Pedidos de Venta';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `sales`
#

LOCK TABLES `sales` WRITE;
ALTER TABLE `sales` DISABLE KEYS;
ALTER TABLE `sales` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `sales_detail`
#

DROP TABLE IF EXISTS `sales_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `sales_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle del Pedido de Venta',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `sales` int(4) NOT NULL default '0' COMMENT 'Identificador del Pedido de Venta',
  `line` smallint(2) default '1' COMMENT 'Numero de lnea del Detalle dentro del Pedido',
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
  KEY `IDX_SALES_DETAIL_OFFER_DETAIL` (`offer_detail`),
  KEY `IDX_SALES_DETAIL_SALES` (`sales`),
  KEY `IDX_SALES_DETAIL_ITEM` (`item`),
  KEY `IDX_SALES_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_SALES_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SALES_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_SALES_DETAIL_OFFER_DETAIL` FOREIGN KEY (`offer_detail`) REFERENCES `offer_detail` (`id`),
  CONSTRAINT `FK_SALES_DETAIL_SALES` FOREIGN KEY (`sales`) REFERENCES `sales` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Pedido de Venta';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `sales_detail`
#

LOCK TABLES `sales_detail` WRITE;
ALTER TABLE `sales_detail` DISABLE KEYS;
ALTER TABLE `sales_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `scope`
#

DROP TABLE IF EXISTS `scope`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `scope` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(16) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Ambito',
  PRIMARY KEY  (`id`),
  KEY `IDX_SCOPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_SCOPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ambitos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `scope`
#

LOCK TABLES `scope` WRITE;
ALTER TABLE `scope` DISABLE KEYS;
INSERT INTO `scope` VALUES (1,1,'GENERAL');
ALTER TABLE `scope` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `segment`
#

DROP TABLE IF EXISTS `segment`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `segment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Segmento',
  PRIMARY KEY  (`id`),
  KEY `IDX_SEGMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_SEGMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Segmentos Comerciales';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `segment`
#

LOCK TABLES `segment` WRITE;
ALTER TABLE `segment` DISABLE KEYS;
ALTER TABLE `segment` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `seller`
#

DROP TABLE IF EXISTS `seller`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `seller` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro del Agente Comercial',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `commission_type` int(4) default NULL COMMENT 'Identificador del Tipo de Comision',
  `status` tinyint(2) default '0' COMMENT 'Estado del Agente Comercial',
  PRIMARY KEY  (`registry`),
  KEY `IDX_SELLER_COMMISSION_TYPE` (`commission_type`),
  KEY `IDX_SELLER_DOMAIN` (`domain`),
  CONSTRAINT `FK_SELLER_COMMISSION_TYPE` FOREIGN KEY (`commission_type`) REFERENCES `commission_type` (`id`),
  CONSTRAINT `FK_SELLER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SELLER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Agentes Comerciales';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `seller`
#

LOCK TABLES `seller` WRITE;
ALTER TABLE `seller` DISABLE KEYS;
ALTER TABLE `seller` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `series`
#

DROP TABLE IF EXISTS `series`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `series` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `code` varchar(5) collate latin1_spanish_ci NOT NULL COMMENT 'Serie',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `description` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Serie',
  `tas` tinyint(1) default '0' COMMENT 'Indica si es una Serie para Ordenes de Reparacion',
  `offer` tinyint(1) default '0' COMMENT 'Indica si es una Serie para Presupuestos',
  `sales` tinyint(1) default '0' COMMENT 'Indica si es una Serie para Pedidos',
  `delivery` tinyint(1) default '0' COMMENT 'Indica si es una Serie para Albaranes',
  `invoice` tinyint(1) default '0' COMMENT 'Indica si es una Serie para Facturas',
  `rectification` tinyint(1) default '0' COMMENT 'Indica si es una Serie para Facturas rectificativas',
  `security_level` tinyint(2) NOT NULL COMMENT 'Nivel de seguridad de la Serie',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Serie esta activa o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_SERIES_DOMAIN` (`domain`),
  KEY `IDX_SERIES_SCOPE` (`scope`),
  CONSTRAINT `FK_SERIES_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_SERIES_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Series';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `series`
#

LOCK TABLES `series` WRITE;
ALTER TABLE `series` DISABLE KEYS;
ALTER TABLE `series` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `session`
#

DROP TABLE IF EXISTS `session`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `session` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `endDate` datetime default NULL COMMENT 'Fecha de finalizacion',
  `remote_address` varchar(15) collate latin1_spanish_ci NOT NULL default '' COMMENT 'IP remota',
  `remote_host` varchar(64) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Equipo remoto',
  `session_id` varchar(128) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Identificador web de la sesin',
  `startDate` datetime NOT NULL COMMENT 'Fecha de inicio',
  `application_id` int(4) NOT NULL default '0' COMMENT 'Identificador de la Aplicacion',
  `user_id` int(4) NOT NULL default '0' COMMENT 'Identificador del Usuario',
  PRIMARY KEY  (`id`),
  KEY `IDX_SESSION_APPLICATION` (`application_id`),
  KEY `IDX_SESSION_USER` (`user_id`),
  KEY `IDX_SESSION_DOMAIN` (`domain`),
  CONSTRAINT `FK_SESSION_APPLICATION` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `FK_SESSION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SESSION_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Sesion web';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `session`
#

LOCK TABLES `session` WRITE;
ALTER TABLE `session` DISABLE KEYS;
INSERT INTO `session` VALUES (1,1,'2010-10-19 05:48:21','82.130.201.96','82.130.201.96','E46E2246D3F5426E438BD26E894A464D','2010-10-19 05:48:02',1,2),(2,1,NULL,'82.130.201.96','82.130.201.96','E46E2246D3F5426E438BD26E894A464D','2010-10-19 05:48:21',1,2);
ALTER TABLE `session` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `signature`
#

DROP TABLE IF EXISTS `signature`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `signature` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Firma',
  `signature` text collate latin1_spanish_ci NOT NULL COMMENT 'Texto de la Firma de la Cuenta de Correo',
  `source` tinyint(2) NOT NULL COMMENT 'Origen de la Firma',
  `source_id` int(4) NOT NULL COMMENT 'Identificador del origen de la Firma',
  PRIMARY KEY  (`id`),
  KEY `IDX_SIGNATURE_DOMAIN` (`domain`),
  CONSTRAINT `FK_SIGNATURE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Firmas de Cuentas de Correo Electronico';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `signature`
#

LOCK TABLES `signature` WRITE;
ALTER TABLE `signature` DISABLE KEYS;
ALTER TABLE `signature` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `stock`
#

DROP TABLE IF EXISTS `stock`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `stock` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Stock',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `warehouse` int(4) default NULL COMMENT 'Identificador del Almacen',
  `item` int(4) default NULL COMMENT 'Identificador del Articulo',
  `quantity` double(15,3) default '0.000' COMMENT 'Cantidad del Articulo en el Almacen',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_UNQ_STOCK_WAREHOUSE_ITEM` (`warehouse`,`item`),
  KEY `IDX_STOCK_WAREHOUSE` (`warehouse`),
  KEY `IDX_STOCK_ITEM` (`item`),
  KEY `IDX_STOCK_DOMAIN` (`domain`),
  CONSTRAINT `FK_STOCK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_STOCK_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_STOCK_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Stocks de Almacenes';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `stock`
#

LOCK TABLES `stock` WRITE;
ALTER TABLE `stock` DISABLE KEYS;
ALTER TABLE `stock` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `supplier`
#

DROP TABLE IF EXISTS `supplier`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `supplier` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro del Proveedor',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `withholding` tinyint(1) default '0' COMMENT 'Indica si el Proveedor aplica retencion de impuestos',
  `transaction` tinyint(2) default '0' COMMENT 'Tipo de transacciones del Proveedor',
  `status` tinyint(2) default NULL COMMENT 'Estado del Proveedor',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  PRIMARY KEY  (`registry`),
  KEY `IDX_SUPPLIER_SCOPE` (`scope`),
  KEY `IDX_SUPPLIER_DOMAIN` (`domain`),
  CONSTRAINT `FK_SUPPLIER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SUPPLIER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_SUPPLIER_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Proveedores';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `supplier`
#

LOCK TABLES `supplier` WRITE;
ALTER TABLE `supplier` DISABLE KEYS;
ALTER TABLE `supplier` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `supplier_account`
#

DROP TABLE IF EXISTS `supplier_account`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `supplier_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Cuenta Contable del Proveedor',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `supplier` int(4) NOT NULL default '0' COMMENT 'Identificador del Proveedor',
  `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `IDX_SUPPLIER_ACCOUNT_ACCOUNT` (`account`),
  KEY `IDX_SUPPLIER_ACCOUNT_SUPPLIER` (`supplier`),
  KEY `IDX_SUPPLIER_ACCOUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_SUPPLIER_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_SUPPLIER_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SUPPLIER_ACCOUNT_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Proveedores';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `supplier_account`
#

LOCK TABLES `supplier_account` WRITE;
ALTER TABLE `supplier_account` DISABLE KEYS;
ALTER TABLE `supplier_account` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `survey`
#

DROP TABLE IF EXISTS `survey`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `survey` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si el Cuestionario esta activa o no',
  `creationDate` datetime NOT NULL COMMENT 'Fecha de creacion del Cuestionario',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Cuestionario',
  PRIMARY KEY  (`id`),
  KEY `IDX_SURVEY_DOMAIN` (`domain`),
  CONSTRAINT `FK_SURVEY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuestionarios';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `survey`
#

LOCK TABLES `survey` WRITE;
ALTER TABLE `survey` DISABLE KEYS;
ALTER TABLE `survey` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `survey_question`
#

DROP TABLE IF EXISTS `survey_question`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `survey_question` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `survey` int(4) NOT NULL COMMENT 'Identificador del Cuestionario',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `position` int(11) default NULL COMMENT 'Posicion de la Pregunta dentro del Cuestionario',
  PRIMARY KEY  (`id`),
  KEY `IDX_SURVEY_QUESTION_QUESTION` (`question`),
  KEY `IDX_SURVEY_QUESTION_SURVEY` (`survey`),
  KEY `IDX_SURVEY_QUESTION_DOMAIN` (`domain`),
  CONSTRAINT `FK_SURVEY_QUESTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SURVEY_QUESTION_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`),
  CONSTRAINT `FK_SURVEY_QUESTION_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Preguntas de Cuestionarios';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `survey_question`
#

LOCK TABLES `survey_question` WRITE;
ALTER TABLE `survey_question` DISABLE KEYS;
ALTER TABLE `survey_question` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `survey_response`
#

DROP TABLE IF EXISTS `survey_response`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `survey_response` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `creationDate` datetime NOT NULL COMMENT 'Fecha de la creacion en el sistema de la Respuesta del Cuestionario',
  `response_date` datetime NOT NULL COMMENT 'Fecha de la Respuesta del Cuestionario',
  `survey` int(4) NOT NULL COMMENT 'Identificador del Cuestionario',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `user` int(4) NOT NULL COMMENT 'Identificador del Usuario',
  `campaign_action` int(4) default NULL COMMENT 'Identificador de la Accion de la Campaa',
  PRIMARY KEY  (`id`),
  KEY `IDX_SURVEY_RESPONSE_MK_ACTION` (`campaign_action`),
  KEY `IDX_SURVEY_RESPONSE_SURVEY` (`survey`),
  KEY `IDX_SURVEY_RESPONSE_TARGET` (`target`),
  KEY `IDX_SURVEY_RESPONSE_USER` (`user`),
  KEY `IDX_SURVEY_RESPONSE_DOMAIN` (`domain`),
  CONSTRAINT `FK_SURVEY_RESPONSE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SURVEY_RESPONSE_MK_ACTION` FOREIGN KEY (`campaign_action`) REFERENCES `mk_action` (`id`),
  CONSTRAINT `FK_SURVEY_RESPONSE_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`),
  CONSTRAINT `FK_SURVEY_RESPONSE_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_SURVEY_RESPONSE_USER` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Respuestas de Cuestionarios';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `survey_response`
#

LOCK TABLES `survey_response` WRITE;
ALTER TABLE `survey_response` DISABLE KEYS;
ALTER TABLE `survey_response` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `survey_response_detail`
#

DROP TABLE IF EXISTS `survey_response_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `survey_response_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `value_text` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Valor de tipo texto',
  `value_number` double(15,3) default NULL COMMENT 'Valor de tipo numerico',
  `value_date` datetime default NULL COMMENT 'Valor de tipo fecha',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `surveyResponse` int(4) NOT NULL COMMENT 'Identificador de la Respuesta del Cuestionario',
  PRIMARY KEY  (`id`),
  KEY `IDX_SURVEY_RESPONSE_DETAIL_QUESTION` (`question`),
  KEY `IDX_SURVEY_RESPONSE_DETAIL_SURVEY_RESPONSE` (`surveyResponse`),
  KEY `IDX_SURVEY_RESPONSE_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_SURVEY_RESPONSE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SURVEY_RESPONSE_DETAIL_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`),
  CONSTRAINT `FK_SURVEY_RESPONSE_DETAIL_SURVEY_RESPONSE` FOREIGN KEY (`surveyResponse`) REFERENCES `survey_response` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Respuestas de Cuestionarios';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `survey_response_detail`
#

LOCK TABLES `survey_response_detail` WRITE;
ALTER TABLE `survey_response_detail` DISABLE KEYS;
ALTER TABLE `survey_response_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `survey_workflow`
#

DROP TABLE IF EXISTS `survey_workflow`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `survey_workflow` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `questionValue` int(4) default NULL COMMENT 'Identificador del Valor de la Pregunta',
  `surveyQuestion` int(4) NOT NULL COMMENT 'Identificador de la Pregunta del Cuestionario',
  `nextSurveyQuestion` int(4) NOT NULL COMMENT 'Identificador de la siguiente Pregunta del Cuestionario',
  `operator` tinyint(2) default NULL COMMENT 'Operador a utilizar con el Valor',
  `value_text` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Valor de tipo texto',
  `value_number` double(15,3) default NULL COMMENT 'Valor de tipo numerico',
  `value_date` datetime default NULL COMMENT 'Valor de tipo fecha',
  PRIMARY KEY  (`id`),
  KEY `IDX_SURVEY_WORKFLOW_NEXT_SURVEY_QUESTION` (`nextSurveyQuestion`),
  KEY `IDX_SURVEY_WORKFLOW_QUESTION_VALUE` (`questionValue`),
  KEY `IDX_SURVEY_WORKFLOW_SURVEY_QUESTION` (`surveyQuestion`),
  KEY `IDX_SURVEY_WORKFLOW_DOMAIN` (`domain`),
  CONSTRAINT `FK_SURVEY_WORKFLOW_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SURVEY_WORKFLOW_NEXT_SURVEY_QUESTION` FOREIGN KEY (`nextSurveyQuestion`) REFERENCES `survey_question` (`id`),
  CONSTRAINT `FK_SURVEY_WORKFLOW_QUESTION_VALUE` FOREIGN KEY (`questionValue`) REFERENCES `question_value` (`id`),
  CONSTRAINT `FK_SURVEY_WORKFLOW_SURVEY_QUESTION` FOREIGN KEY (`surveyQuestion`) REFERENCES `survey_question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Secuencias de Cuestionarios';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `survey_workflow`
#

LOCK TABLES `survey_workflow` WRITE;
ALTER TABLE `survey_workflow` DISABLE KEYS;
ALTER TABLE `survey_workflow` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `system_cost`
#

DROP TABLE IF EXISTS `system_cost`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `system_cost` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `expression` text collate latin1_spanish_ci COMMENT 'Expresion',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Costo',
  `code` varchar(10) collate latin1_spanish_ci default NULL COMMENT 'Cdigo',
  PRIMARY KEY  (`id`),
  KEY `IDX_SYSTEM_COST_DOMAIN` (`domain`),
  CONSTRAINT `FK_SYSTEM_COST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Costos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `system_cost`
#

LOCK TABLES `system_cost` WRITE;
ALTER TABLE `system_cost` DISABLE KEYS;
INSERT INTO `system_cost` VALUES (1,1,'2010-01-01',NULL,'23.60 %','( BASE_CGC_E=( BASE_CGC + ( isdef BASE_MTNAD ? BASE_MTNAD : 0.00 ) ) ) * 23.60/100',0,'CGC_E'),(2,1,'2010-01-01',NULL,'12.00 %','BASE_ESTR * 12.00/100',4,'EXTR_E'),(3,1,'2010-01-01',NULL,'23.60 %','BASE_NESTR * 23.60/100',5,'NEXTR_E'),(4,1,'2010-01-01',NULL,'@{PORCENTAJE_IT} %','( BASE_CGP_E=( BASE_CGP + ( isdef BASE_MTNAD ? BASE_MTNAD : 0.00 ) ) ) * (PORCENTAJE_IT=( isdef OCUPACION ? OCUPACION_IT[OCUPACION] : TARIFA_IT))/100',1,'IT_E'),(5,1,'2010-01-01',NULL,'@{PORCENTAJE_IMS} %','BASE_CGP_E * (PORCENTAJE_IMS=( isdef OCUPACION ? OCUPACION_IMS[OCUPACION] : TARIFA_IMS))/100',1,'IMS_E'),(6,1,'2010-01-01',NULL,'@{PORCENTAJE_DESMPL_E} %','BASE_CGP_E * (PORCENTAJE_DESMPL_E=(INDEFINIDO ? 5.50 : (TIEMPO_COMPLETO ? 6.70 : 7.70)))/100',2,'DESMPL_E'),(7,1,'2010-01-01',NULL,'0.20 %','BASE_CGP_E * 0.20/100',10,'FOGASA_E'),(8,1,'2010-01-01',NULL,'0.60 %','BASE_CGP_E * 0.60/100',3,'FP_E'),(9,1,'2010-01-01',NULL,'36.00 %','CONTRATO_CORTA_DURACION ? ( CGC_E * 36.00 / 100 ) : 0.00',0,'CGC_E_TEMP'),(10,1,'1970-01-01',NULL,'PREST. IT A CARGO DEL INSS','isdef ECSS ? -ECSS : 0.00',7,'ECSS_E'),(11,1,'1970-01-01',NULL,'PREST. A.T y E.P','isdef ATEP ? -ATEP : 0.00',7,'ATEP_E');
ALTER TABLE `system_cost` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `system_data`
#

DROP TABLE IF EXISTS `system_data`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `system_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre',
  `expression` text collate latin1_spanish_ci COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `read_only` tinyint(1) default NULL COMMENT 'Modificable',
  `comments` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Comentario de ayuda',
  PRIMARY KEY  (`id`),
  KEY `IDX_SYSTEM_DATA_DOMAIN` (`domain`),
  CONSTRAINT `FK_SYSTEM_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto de las funciones';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `system_data`
#

LOCK TABLES `system_data` WRITE;
ALTER TABLE `system_data` DISABLE KEYS;
INSERT INTO `system_data` VALUES (1,1,'BASE_CGC_MIN','[	\"01\":\"TIEMPO_COMPLETO ? 1031.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 6.22 * HORAS_NOMINA\", \"02\":\"TIEMPO_COMPLETO ? 855.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.16 * HORAS_NOMINA\", \"03\":\"TIEMPO_COMPLETO ? 744.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.49 * HORAS_NOMINA\", \"04\":\"TIEMPO_COMPLETO ? 738.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.45 * HORAS_NOMINA\", \"05\":\"TIEMPO_COMPLETO ? 738.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.45 * HORAS_NOMINA\", \"06\":\"TIEMPO_COMPLETO ? 738.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.45 * HORAS_NOMINA\", \"07\":\"TIEMPO_COMPLETO ? 738.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.45 * HORAS_NOMINA\", \"08\":\"TIEMPO_COMPLETO ? 24.63 * DIAS_NOMINA : 4.45 * HORAS_NOMINA\", \"09\":\"TIEMPO_COMPLETO ? 24.63 * DIAS_NOMINA : 4.45 * HORAS_NOMINA\", \"10\":\"TIEMPO_COMPLETO ? 24.63 * DIAS_NOMINA : 4.45 * HORAS_NOMINA\", \"11\":\"TIEMPO_COMPLETO ? 24.63 * DIAS_NOMINA : 4.45 * HORAS_NOMINA\"]','2010-01-01','2010-12-31',1,'Bases minimas'),(2,1,'BASE_CGC_MAX','[	\"01\": \"3198.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) \", \"02\": \"3198.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\", \"03\": \"3198.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\", \"04\": \"3198.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\", \"05\": \"3198.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\", \"06\": \"3198.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\", \"07\": \"3198.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\", \"08\": \"106.60 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)\", \"09\": \"106.60 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)\", \"10\": \"106.60 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)\", \"11\": \"106.60 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)\"]','2010-01-01','2010-12-31',1,'Bases maximas'),(3,1,'IPREM','516.90','2008-01-01','2008-12-31',1,'Indicador Pblico de Renta de Efectos Mltiples (IPREM) '),(4,1,'IPREM','527.24','2009-01-01','2009-12-31',1,'Indicador Pblico de Renta de Efectos Mltiples (IPREM) '),(5,1,'IPREM','531.51','2010-01-01','2010-12-31',1,'Indicador Pblico de Renta de Efectos Mltiples (IPREM) '),(6,1,'IPREM','531.51','2011-01-01',NULL,1,'Indicador Pblico de Renta de Efectos Mltiples (IPREM) '),(7,1,'COTIZACION_IT','\"MENSUAL\"','2010-01-01',NULL,0,'Prorrateo de la cotizacin por incapacidad temporal'),(8,1,'BASE_CGC_MIN','[\"01\": \"TIEMPO_COMPLETO ? 1045.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 6.30 * HORAS_NOMINA\", \"02\": \"TIEMPO_COMPLETO ? 867.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.22 * HORAS_NOMINA\", \"03\": \"TIEMPO_COMPLETO ? 754.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.55 * HORAS_NOMINA\", \"04\": \"TIEMPO_COMPLETO ? 748.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.51 * HORAS_NOMINA\", \"05\": \"TIEMPO_COMPLETO ? 748.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.51 * HORAS_NOMINA\", \"06\": \"TIEMPO_COMPLETO ? 748.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.51 * HORAS_NOMINA \", \"07\": \"TIEMPO_COMPLETO ? 748.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.51 * HORAS_NOMINA\", \"08\": \"TIEMPO_COMPLETO ? 24.94 * DIAS_NOMINA : 4.51 * HORAS_NOMINA\", \"09\": \"TIEMPO_COMPLETO ? 24.94 * DIAS_NOMINA : 4.51 * HORAS_NOMINA\", \"10\": \"TIEMPO_COMPLETO ? 24.94 * DIAS_NOMINA : 4.51 * HORAS_NOMINA\", \"11\": \"TIEMPO_COMPLETO ? 24.94 * DIAS_NOMINA : 4.51 * HORAS_NOMINA\"]','2011-01-01',NULL,1,'Bases mnimas'),(9,1,'BASE_CGC_MAX','[\"01\": \"3230.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\", \"02\": \"3230.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\", \"03\": \"3230.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\", \"04\": \"3230.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\", \"05\": \"3230.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\", \"06\": \"3230.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\", \"07\": \"3230.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\", \"08\": \"107.67 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)\", \"09\": \"107.67 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)\", \"10\": \"107.67 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)\", \"11\": \"107.67 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)\"]','2011-01-01',NULL,1,'Bases mximas'),(10,1,'BASE_CGP_MAX','\"3198.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\"','2010-01-01','2010-12-31',0,'Tope Mnimo de cotizacin para Accidentes de Trabajo y Enfermedades Profesionales'),(11,1,'BASE_CGP_MIN','\"TIEMPO_COMPLETO ? 738.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.45 * HORAS_NOMINA\"','2010-01-01','2010-12-31',0,'Tope Mximo de cotizacin para Accidentes de Trabajo y Enfermedades Profesionales'),(12,1,'BASE_CGP_MAX','\"3230.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)\"','2011-01-01',NULL,0,'Tope Mnimo de cotizacin para Accidentes de Trabajo y Enfermedades Profesionales'),(13,1,'BASE_CGP_MIN','\"TIEMPO_COMPLETO ? 748.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.51 * HORAS_NOMINA \"','2011-01-01',NULL,0,'Tope Mximo de cotizacin para Accidentes de Trabajo y Enfermedades Profesionales'),(14,1,'INGRESO_AC_EMPRESA','false','1979-01-01',NULL,0,'Ingreso a cuenta a cargo de la empresa'),(15,1,'SMI','633.30','2010-01-01',NULL,0,'Salario mnimo interprofesional'),(16,1,'SMI','641.40','2011-01-01',NULL,0,'Salario mnimo interprofesional'),(17,1,'MAX_EMBARGABLE','\"((TOTAL_LIQUIDO > SMI) ? ( TOTAL_LIQUIDO - SMI ) * 0.30 : 0.00) + ((TOTAL_LIQUIDO > 2 * SMI) ? ( TOTAL_LIQUIDO - 2 * SMI ) * 0.20 : 0.00) + ((TOTAL_LIQUIDO > 3 * SMI) ? ( TOTAL_LIQUIDO - 3 * SMI ) * 0.10 : 0.00) + ((TOTAL_LIQUIDO > 4 * SMI) ? ( TOTAL_LIQUIDO - 4 * SMI ) * 0.15 : 0.00) + ((TOTAL_LIQUIDO > 5 * SMI) ? ( TOTAL_LIQUIDO - 5 * SMI ) * 0.15 : 0.00) - EMBARGADO\"','2000-01-01',NULL,0,'Mximo embargable'),(18,1,'BASE_IPREM','0.00','2000-01-01',NULL,1,'Base para los conceptos exentos de cotizacion (IPREM) '),(19,1,'BIPREM','0.00','2000-01-01',NULL,1,'Base para los conceptos exentos de cotizacion (IPREM) '),(20,1,'OCUPACION_IT',' [\"a\": 0.65, \"b\": 1.00, \"d\": 3.35, \"e\": 1.80, \"f\": 3.35, \"g\": 2.10, \"h\": 1.40]','2010-01-01',NULL,1,'Tarifas de primas para I.T'),(21,1,'OCUPACION_IMS',' [\"a\": 0.35, \"b\": 1.00, \"d\": 3.35, \"e\": 1.50, \"f\": 3.35, \"g\": 1.50, \"h\": 2.20]','2010-01-01',NULL,1,'Tarifas de primas para I.M.S');
ALTER TABLE `system_data` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `system_deduction`
#

DROP TABLE IF EXISTS `system_deduction`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `system_deduction` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Deduccin',
  `deduction_concept` int(4) default NULL COMMENT 'Identificador unico del concepto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `description_decorable` tinyint(2) NOT NULL default '0',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Frmula',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `month` tinyint(2) default NULL COMMENT 'Mes de la deduccin',
  PRIMARY KEY  (`id`),
  KEY `IDX_SYSTEM_DEDUCTION_DEDUCTION_CONCEPT` (`deduction_concept`),
  KEY `IDX_SYSTEM_DEDUCTION_DOMAIN` (`domain`),
  CONSTRAINT `FK_SYSTEM_DEDUCTION_DEDUCTION_CONCEPT` FOREIGN KEY (`deduction_concept`) REFERENCES `deduction_concept` (`id`),
  CONSTRAINT `FK_SYSTEM_DEDUCTION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Deducciones';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `system_deduction`
#

LOCK TABLES `system_deduction` WRITE;
ALTER TABLE `system_deduction` DISABLE KEYS;
INSERT INTO `system_deduction` VALUES (1,1,0,1,NULL,1,NULL,'2010-01-01',NULL,NULL),(2,1,2,3,NULL,1,NULL,'2010-01-01',NULL,NULL),(3,1,3,4,NULL,1,NULL,'2010-01-01',NULL,NULL),(4,1,4,5,NULL,1,NULL,'2010-01-01',NULL,NULL),(5,1,5,6,NULL,1,NULL,'2010-01-01',NULL,NULL),(6,1,6,7,NULL,1,NULL,'2010-01-01',NULL,NULL);
ALTER TABLE `system_deduction` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `system_payment`
#

DROP TABLE IF EXISTS `system_payment`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `system_payment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Percepcin Salarial',
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
  KEY `IDX_SYSTEM_PAYMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_SYSTEM_PAYMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SYSTEM_PAYMENT_PAYMENT_CONCEPT` FOREIGN KEY (`payment_concept`) REFERENCES `payment_concept` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Percepciones Salariales';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `system_payment`
#

LOCK TABLES `system_payment` WRITE;
ALTER TABLE `system_payment` DISABLE KEYS;
INSERT INTO `system_payment` VALUES (1,1,NULL,1,NULL,0,NULL,NULL,NULL,'2010-01-01',NULL,NULL,0),(2,1,NULL,2,NULL,0,NULL,NULL,NULL,'2010-01-01',NULL,NULL,0),(3,1,NULL,3,NULL,0,NULL,NULL,NULL,'2010-01-01',NULL,NULL,0),(4,1,NULL,4,NULL,0,NULL,NULL,NULL,'2010-01-01',NULL,NULL,0),(5,1,NULL,5,NULL,0,NULL,NULL,NULL,'2010-01-01',NULL,NULL,0),(6,1,NULL,6,NULL,1,NULL,NULL,NULL,'1970-01-01',NULL,NULL,2),(7,1,NULL,7,NULL,1,NULL,NULL,NULL,'1970-01-01',NULL,NULL,2);
ALTER TABLE `system_payment` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `target`
#

DROP TABLE IF EXISTS `target`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `target` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro del Cliente Potencial',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_TARGET_DOMAIN` (`domain`),
  CONSTRAINT `FK_TARGET_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TARGET_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_TARGET_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_TARGET_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Clientes Potenciales';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `target`
#

LOCK TABLES `target` WRITE;
ALTER TABLE `target` DISABLE KEYS;
ALTER TABLE `target` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `target_item`
#

DROP TABLE IF EXISTS `target_item`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `target_item` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo',
  `status` tinyint(2) NOT NULL COMMENT 'Estado',
  PRIMARY KEY  (`id`),
  KEY `IDX_TARGET_ITEM_TARGET` (`target`),
  KEY `IDX_TARGET_ITEM_ITEM` (`item`),
  KEY `IDX_TARGET_ITEM_DOMAIN` (`domain`),
  CONSTRAINT `FK_TARGET_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TARGET_ITEM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_TARGET_ITEM_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulos interesados por Cliente Potencial';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `target_item`
#

LOCK TABLES `target_item` WRITE;
ALTER TABLE `target_item` DISABLE KEYS;
ALTER TABLE `target_item` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `target_profile`
#

DROP TABLE IF EXISTS `target_profile`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `target_profile` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `last_update` datetime NOT NULL COMMENT 'Fecha de la ultima modificacion del Perfil del Cliente Potencial',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `value_text` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Valor de tipo texto',
  `value_number` double(15,3) default NULL COMMENT 'Valor de tipo numerico',
  `value_date` datetime default NULL COMMENT 'Valor de tipo fecha',
  PRIMARY KEY  (`id`),
  KEY `IDX_TARGET_PROFILE_QUESTION` (`question`),
  KEY `IDX_TARGET_PROFILE_TARGET` (`target`),
  KEY `IDX_TARGET_PROFILE_DOMAIN` (`domain`),
  CONSTRAINT `FK_TARGET_PROFILE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TARGET_PROFILE_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`),
  CONSTRAINT `FK_TARGET_PROFILE_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Perfiles de Clientes Potenciales';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `target_profile`
#

LOCK TABLES `target_profile` WRITE;
ALTER TABLE `target_profile` DISABLE KEYS;
ALTER TABLE `target_profile` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `target_seller`
#

DROP TABLE IF EXISTS `target_seller`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `target_seller` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `seller` int(4) NOT NULL COMMENT 'Identificador del Comercial',
  `start_date` date NOT NULL COMMENT 'Fecha de Inicio',
  `end_date` date default NULL COMMENT 'Fecha de Fin',
  `status` tinyint(2) NOT NULL COMMENT 'Estado',
  PRIMARY KEY  (`id`),
  KEY `IDX_TARGET_SELLER_TARGET` (`target`),
  KEY `IDX_TARGET_SELLER_SELLER` (`seller`),
  KEY `IDX_TARGET_SELLER_DOMAIN` (`domain`),
  CONSTRAINT `FK_TARGET_SELLER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TARGET_SELLER_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`),
  CONSTRAINT `FK_TARGET_SELLER_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comerciales relacionado con Cliente Potencial';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `target_seller`
#

LOCK TABLES `target_seller` WRITE;
ALTER TABLE `target_seller` DISABLE KEYS;
ALTER TABLE `target_seller` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `target_supplier`
#

DROP TABLE IF EXISTS `target_supplier`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `target_supplier` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
  KEY `IDX_TARGET_SUPPLIER_DOMAIN` (`domain`),
  CONSTRAINT `FK_TARGET_SUPPLIER_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`),
  CONSTRAINT `FK_TARGET_SUPPLIER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TARGET_SUPPLIER_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_TARGET_SUPPLIER_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`),
  CONSTRAINT `FK_TARGET_SUPPLIER_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_TARGET_SUPPLIER_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion de Clientes Potenciales con Proveedores';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `target_supplier`
#

LOCK TABLES `target_supplier` WRITE;
ALTER TABLE `target_supplier` DISABLE KEYS;
ALTER TABLE `target_supplier` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `tariff`
#

DROP TABLE IF EXISTS `tariff`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tariff` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Tarifa',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `code` varchar(8) collate latin1_spanish_ci default NULL COMMENT 'Codigo de la Tarifa',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Tarifa',
  PRIMARY KEY  (`id`),
  KEY `IDX_TARIFF_DOMAIN` (`domain`),
  CONSTRAINT `FK_TARIFF_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `tariff`
#

LOCK TABLES `tariff` WRITE;
ALTER TABLE `tariff` DISABLE KEYS;
ALTER TABLE `tariff` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `tariff_catalogue`
#

DROP TABLE IF EXISTS `tariff_catalogue`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tariff_catalogue` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `tariff` int(4) NOT NULL COMMENT 'Identificador de la Tarifa',
  `catalogue` int(4) NOT NULL COMMENT 'Identificador del Catalogo',
  PRIMARY KEY  (`id`),
  KEY `IDX_TARIFF_CATALOGUE_TARIFF` (`tariff`),
  KEY `IDX_TARIFF_CATALOGUE_CATALOGUE` (`catalogue`),
  KEY `IDX_TARIFF_CATALOGUE_DOMAIN` (`domain`),
  CONSTRAINT `FK_TARIFF_CATALOGUE_CATALOGUE` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`),
  CONSTRAINT `FK_TARIFF_CATALOGUE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TARIFF_CATALOGUE_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas por Catalogo';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `tariff_catalogue`
#

LOCK TABLES `tariff_catalogue` WRITE;
ALTER TABLE `tariff_catalogue` DISABLE KEYS;
ALTER TABLE `tariff_catalogue` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `tas_item`
#

DROP TABLE IF EXISTS `tas_item`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tas_item` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Articulo',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `model` int(4) NOT NULL COMMENT 'Identificador del Modelo',
  `publicCode` varchar(25) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo publico del Articulo',
  `privateCode` varchar(25) collate latin1_spanish_ci default NULL COMMENT 'Codigo privado del Articulo',
  `description` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Articulo',
  `add_info` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Informacion adicional del Articulo',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_UNQ_TAS_ITEM_DOMAIN_PUBLIC_CODE` (`domain`,`publicCode`),
  UNIQUE KEY `IDX_UNQ_TAS_ITEM_DOMAIN_PRIVATE_CODE` (`domain`,`privateCode`),
  KEY `IDX_TAS_ITEM_MODEL` (`model`),
  KEY `IDX_TAS_ITEM_DOMAIN` (`domain`),
  CONSTRAINT `FK_TAS_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TAS_ITEM_MODEL` FOREIGN KEY (`model`) REFERENCES `model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulo susceptible de Asistencia Tecnica';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `tas_item`
#

LOCK TABLES `tas_item` WRITE;
ALTER TABLE `tas_item` DISABLE KEYS;
ALTER TABLE `tas_item` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `task`
#

DROP TABLE IF EXISTS `task`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `task` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Tarea',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
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
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `task`
#

LOCK TABLES `task` WRITE;
ALTER TABLE `task` DISABLE KEYS;
ALTER TABLE `task` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `task_holder`
#

DROP TABLE IF EXISTS `task_holder`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `task_holder` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro de la Entidad susceptible de Recibir Tareas',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `type` tinyint(2) default '0' COMMENT 'Tipo de Entidad susceptible de Recibir Tareas',
  `active` tinyint(1) default '1' COMMENT 'Indica si dicha Entidad esta activa o no',
  `user_id` int(4) default NULL COMMENT 'Identificador del Usuario',
  `cost_profile` int(4) default NULL COMMENT 'Identificador del Perfil de Costos',
  PRIMARY KEY  (`registry`),
  KEY `IDX_TASK_HOLDER_USER` (`user_id`),
  KEY `IDX_TASK_HOLDER_COST_PROFILE` (`cost_profile`),
  KEY `IDX_TASK_HOLDER_DOMAIN` (`domain`),
  CONSTRAINT `FK_TASK_HOLDER_COST_PROFILE` FOREIGN KEY (`cost_profile`) REFERENCES `cost_profile` (`id`),
  CONSTRAINT `FK_TASK_HOLDER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TASK_HOLDER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_TASK_HOLDER_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Titulares de Tareas';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `task_holder`
#

LOCK TABLES `task_holder` WRITE;
ALTER TABLE `task_holder` DISABLE KEYS;
ALTER TABLE `task_holder` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `task_holder_workgroup`
#

DROP TABLE IF EXISTS `task_holder_workgroup`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `task_holder_workgroup` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `task_holder` int(4) NOT NULL COMMENT 'Identificador del Responsable de la Tarea',
  `workgroup` int(4) NOT NULL COMMENT 'Identificador del Grupo de Trabajo',
  PRIMARY KEY  (`id`),
  KEY `IDX_TASK_HOLDER_WORKGROUP_TASK_HOLDER` (`task_holder`),
  KEY `IDX_TASK_HOLDER_WORKGROUP_WORKGROUP` (`workgroup`),
  KEY `IDX_TASK_HOLDER_WORKGROUP_DOMAIN` (`domain`),
  CONSTRAINT `FK_TASK_HOLDER_WORKGROUP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TASK_HOLDER_WORKGROUP_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`),
  CONSTRAINT `FK_TASK_HOLDER_WORKGROUP_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Usuarios y Grupos de Trabajo';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `task_holder_workgroup`
#

LOCK TABLES `task_holder_workgroup` WRITE;
ALTER TABLE `task_holder_workgroup` DISABLE KEYS;
ALTER TABLE `task_holder_workgroup` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `tax`
#

DROP TABLE IF EXISTS `tax`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tax` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Impuesto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(30) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Impuesto',
  `tax_type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Impuesto',
  `percentage` double(15,3) NOT NULL default '0.000' COMMENT 'Porcentaje de recargo actual',
  `surcharge` double(15,3) default '0.000' COMMENT 'Porcentaje de recargo de equivalencia actual',
  `start_date` date default NULL COMMENT 'Fecha de inicio de vigencia',
  `vat_deduction_type` tinyint(2) default '0' COMMENT 'Tipo de deduccion del IVA',
  `withholding_type` tinyint(2) default '0' COMMENT 'Tipo de retencion',
  PRIMARY KEY  (`id`),
  KEY `IDX_TAX_DOMAIN` (`domain`),
  CONSTRAINT `FK_TAX_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Impuestos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `tax`
#

LOCK TABLES `tax` WRITE;
ALTER TABLE `tax` DISABLE KEYS;
INSERT INTO `tax` VALUES (1,1,'GENERAL',1,18.000,4.000,'2010-07-01',0,0),(2,1,'REDUCIDO',1,8.000,1.000,'2010-07-01',0,0),(3,1,'SUPERREDUCIDO',1,4.000,0.500,'2000-01-01',0,0),(4,1,'SIN IVA',1,0.000,0.000,'2000-01-01',0,0),(5,1,'IRPF',2,15.000,0.000,'2000-01-01',0,0);
ALTER TABLE `tax` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `tax_account`
#

DROP TABLE IF EXISTS `tax_account`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tax_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Cuenta Contable del Impuesto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `tax` int(4) NOT NULL default '0' COMMENT 'Identificador del Impuesto',
  `account` int(4) NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Cuenta Contable del Impuesto',
  PRIMARY KEY  (`id`),
  KEY `IDX_TAX_ACCOUNT_ACCOUNT` (`account`),
  KEY `IDX_TAX_ACCOUNT_TAX` (`tax`),
  KEY `IDX_TAX_ACCOUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_TAX_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_TAX_ACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TAX_ACCOUNT_TAX` FOREIGN KEY (`tax`) REFERENCES `tax` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Impuestos de la Factura';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `tax_account`
#

LOCK TABLES `tax_account` WRITE;
ALTER TABLE `tax_account` DISABLE KEYS;
ALTER TABLE `tax_account` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `tax_detail`
#

DROP TABLE IF EXISTS `tax_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tax_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Historico de Impuestos',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `tax` int(4) NOT NULL default '0' COMMENT 'Identificador del Impuesto',
  `start_date` date default NULL COMMENT 'Fecha de inicio de vigencia',
  `end_date` date default NULL COMMENT 'Fecha de fin de vigencia',
  `value` double(15,3) default NULL COMMENT 'Porcentaje de recargo',
  `surcharge` double(15,3) default NULL COMMENT 'Porcentaje de recargo de equivalencia',
  PRIMARY KEY  (`id`),
  KEY `IDX_TAX_DETAIL_TAX` (`tax`),
  KEY `IDX_TAX_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_TAX_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TAX_DETAIL_TAX` FOREIGN KEY (`tax`) REFERENCES `tax` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Historico de Impuestos';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `tax_detail`
#

LOCK TABLES `tax_detail` WRITE;
ALTER TABLE `tax_detail` DISABLE KEYS;
INSERT INTO `tax_detail` VALUES (1,1,1,'2000-01-01','2010-06-30',16.000,4.000),(2,1,2,'2000-01-01','2010-06-30',7.000,1.000);
ALTER TABLE `tax_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `user`
#

DROP TABLE IF EXISTS `user`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `user` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Usuario',
  `login` varchar(16) collate latin1_spanish_ci NOT NULL COMMENT 'Login del Usuario',
  `enterprise` int(4) default NULL COMMENT 'Identificador de la Empresa',
  `registry` int(4) default NULL COMMENT 'Identificador del Registry',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indica si el Usuario esta activo o no',
  `password` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Contrasea del Usuario',
  PRIMARY KEY  (`id`),
  KEY `IDX_USER_ENTERPRISE` (`enterprise`),
  KEY `IDX_USER_REGISTRY` (`registry`),
  KEY `IDX_USER_DOMAIN` (`domain`),
  CONSTRAINT `FK_USER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_USER_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`),
  CONSTRAINT `FK_USER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Usuarios';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `user`
#

LOCK TABLES `user` WRITE;
ALTER TABLE `user` DISABLE KEYS;
INSERT INTO `user` VALUES (2,1,'aon PAYROLL','payroll',NULL,NULL,1,NULL);
ALTER TABLE `user` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `user_scope`
#

DROP TABLE IF EXISTS `user_scope`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `user_scope` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `user_id` int(4) NOT NULL COMMENT 'Identificador del Usuario',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  PRIMARY KEY  (`id`),
  KEY `IDX_USER_SCOPE_SCOPE` (`scope`),
  KEY `IDX_USER_SCOPE_USER` (`user_id`),
  KEY `IDX_USER_SCOPE_DOMAIN` (`domain`),
  CONSTRAINT `FK_USER_SCOPE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_USER_SCOPE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_USER_SCOPE_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ambitos de Usuario';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `user_scope`
#

LOCK TABLES `user_scope` WRITE;
ALTER TABLE `user_scope` DISABLE KEYS;
ALTER TABLE `user_scope` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `user_workgroup`
#

DROP TABLE IF EXISTS `user_workgroup`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `user_workgroup` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `user_id` int(4) NOT NULL COMMENT 'Identificador del Usuario',
  `workgroup` int(4) NOT NULL COMMENT 'Identificador del Grupo de Trabajo',
  PRIMARY KEY  (`id`),
  KEY `IDX_USER_WORKGROUP_USER` (`user_id`),
  KEY `IDX_USER_WORKGROUP_WORKGROUP` (`workgroup`),
  KEY `IDX_USER_WORKGROUP_DOMAIN` (`domain`),
  CONSTRAINT `FK_USER_WORKGROUP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_USER_WORKGROUP_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_USER_WORKGROUP_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Usuarios y Grupos de Trabajo';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `user_workgroup`
#

LOCK TABLES `user_workgroup` WRITE;
ALTER TABLE `user_workgroup` DISABLE KEYS;
ALTER TABLE `user_workgroup` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `warehouse`
#

DROP TABLE IF EXISTS `warehouse`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `warehouse` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Almacen',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Almacen',
  `workplace` int(4) default NULL COMMENT 'Identificador del Centro de Trabajo',
  PRIMARY KEY  (`id`),
  KEY `IDX_WAREHOUSE_WORKPLACE` (`workplace`),
  KEY `IDX_WAREHOUSE_DOMAIN` (`domain`),
  CONSTRAINT `FK_WAREHOUSE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_WAREHOUSE_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Almacenes';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `warehouse`
#

LOCK TABLES `warehouse` WRITE;
ALTER TABLE `warehouse` DISABLE KEYS;
ALTER TABLE `warehouse` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `warehouse_transfer`
#

DROP TABLE IF EXISTS `warehouse_transfer`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `warehouse_transfer` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie del Traspaso',
  `number` int(4) NOT NULL default '0' COMMENT 'Numero del Traspaso',
  `issue_time` datetime NOT NULL COMMENT 'Fecha de emision del Traspaso',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios del Traspaso',
  `source_warehouse` int(4) default NULL COMMENT 'Identificador del Almacen Origen',
  `target_warehouse` int(4) default NULL COMMENT 'Identificador del Almacen Destino',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_UNQ_WAREHOUSE_TRANSFER_DOMAIN_SERIES_NUMBER` (`domain`,`series`,`number`),
  KEY `IDX_WAREHOUSE_TRANSFER_ISSUE_TIME` (`issue_time`),
  KEY `IDX_WAREHOUSE_TRANSFER_SOURCE_WAREHOUSE` (`source_warehouse`),
  KEY `IDX_WAREHOUSE_TRANSFER_TARGET_WAREHOUSE` (`target_warehouse`),
  KEY `IDX_WAREHOUSE_TRANSFER_DOMAIN` (`domain`),
  CONSTRAINT `FK_WAREHOUSE_TRANSFER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_WAREHOUSE_TRANSFER_SOURCE_WAREHOUSE` FOREIGN KEY (`source_warehouse`) REFERENCES `warehouse` (`id`),
  CONSTRAINT `FK_WAREHOUSE_TRANSFER_TARGET_WAREHOUSE` FOREIGN KEY (`target_warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Traspasos entre Almacenes';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `warehouse_transfer`
#

LOCK TABLES `warehouse_transfer` WRITE;
ALTER TABLE `warehouse_transfer` DISABLE KEYS;
ALTER TABLE `warehouse_transfer` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `warehouse_transfer_detail`
#

DROP TABLE IF EXISTS `warehouse_transfer_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `warehouse_transfer_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `warehouse_transfer` int(4) NOT NULL COMMENT 'Identificador del Traspaso',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo del Detalle de Traspaso',
  `quantity` double(15,3) default NULL COMMENT 'Cantidad del Detalle de Traspaso',
  PRIMARY KEY  (`id`),
  KEY `IDX_WAREHOUSE_TRANSFER_DETAIL_ITEM` (`item`),
  KEY `IDX_WAREHOUSE_TRANSFER_DETAIL_WAREHOUSE_TRANSFER` (`warehouse_transfer`),
  KEY `IDX_WAREHOUSE_TRANSFER_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_WAREHOUSE_TRANSFER_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_WAREHOUSE_TRANSFER_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_WAREHOUSE_TRANSFER_DETAIL_WAREHOUSE_TRANSFER` FOREIGN KEY (`warehouse_transfer`) REFERENCES `warehouse_transfer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Traspasos entre Almacenes';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `warehouse_transfer_detail`
#

LOCK TABLES `warehouse_transfer_detail` WRITE;
ALTER TABLE `warehouse_transfer_detail` DISABLE KEYS;
ALTER TABLE `warehouse_transfer_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `web_info`
#

DROP TABLE IF EXISTS `web_info`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `web_info` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `company` int(4) NOT NULL COMMENT 'Empresa',
  `commercial_description` text collate latin1_spanish_ci COMMENT 'Descripcion comercial',
  `schedule` text collate latin1_spanish_ci COMMENT 'Horario',
  `slogan` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Slogan',
  PRIMARY KEY  (`id`),
  KEY `IDX_WEB_INFO_COMPANY` (`company`),
  KEY `IDX_WEB_INFO_DOMAIN` (`domain`),
  CONSTRAINT `FK_WEB_INFO_COMPANY` FOREIGN KEY (`company`) REFERENCES `company` (`registry`),
  CONSTRAINT `FK_WEB_INFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Informacion de la empresa que se mostrara en la ficha web';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `web_info`
#

LOCK TABLES `web_info` WRITE;
ALTER TABLE `web_info` DISABLE KEYS;
ALTER TABLE `web_info` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `web_info_page`
#

DROP TABLE IF EXISTS `web_info_page`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `web_info_page` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Codigo de la Pagina',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) character set latin1 collate latin1_spanish_ci default NULL COMMENT 'Nombre de la Pagina.',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Pagina',
  `position` tinyint(2) default NULL COMMENT 'Posicion de la Pagina en el menu',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indica si la Pagina esta activa o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_WEB_INFO_PAGE_DOMAIN` (`domain`),
  CONSTRAINT `FK_WEB_INFO_PAGE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Paginas pertenecientes a la ficha web';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `web_info_page`
#

LOCK TABLES `web_info_page` WRITE;
ALTER TABLE `web_info_page` DISABLE KEYS;
ALTER TABLE `web_info_page` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `web_info_page_detail`
#

DROP TABLE IF EXISTS `web_info_page_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `web_info_page_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Codigo del Detalle de la Pagina',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `web_info_page` int(4) NOT NULL COMMENT 'Identificador de la Pagina a la que corresponde el detalle',
  `title` varchar(255) character set latin1 collate latin1_spanish_ci default NULL COMMENT 'Titulo del contenido de la Pagina',
  `layout` int(2) default NULL COMMENT 'Tipo de plantilla',
  `content` text character set latin1 collate latin1_spanish_ci COMMENT 'Texto del contenido de la Pagina',
  `extra` varchar(255) character set latin1 collate latin1_spanish_ci default NULL COMMENT 'Campo reservado a otros datos de la Pagina',
  PRIMARY KEY  (`id`),
  KEY `IDX_WEB_INFO_PAGE_DETAIL_WEB_INFO_PAGE` (`web_info_page`),
  KEY `IDX_WEB_INFO_PAGE_DETAIL_DOMAIN` (`domain`),
  CONSTRAINT `FK_WEB_INFO_PAGE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_WEB_INFO_PAGE_DETAIL_WEB_INFO_PAGE` FOREIGN KEY (`web_info_page`) REFERENCES `web_info_page` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Detalles de la pagina perteneciente a la ficha web';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `web_info_page_detail`
#

LOCK TABLES `web_info_page_detail` WRITE;
ALTER TABLE `web_info_page_detail` DISABLE KEYS;
ALTER TABLE `web_info_page_detail` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `web_info_page_resource`
#

DROP TABLE IF EXISTS `web_info_page_resource`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `web_info_page_resource` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Codigo del Recurso de la Pagina',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `web_info_page` int(4) NOT NULL COMMENT 'Codigo de la Pagina',
  `rattach` int(4) NOT NULL COMMENT 'Identificador del Archivo Adjunto calificado como Recurso',
  `content` varchar(255) character set latin1 collate latin1_spanish_ci default NULL COMMENT 'Texto del Recurso',
  PRIMARY KEY  (`id`),
  KEY `IDX_WEB_INFO_PAGE_RESOURCE_WEB_INFO_PAGE` (`web_info_page`),
  KEY `IDX_WEB_INFO_PAGE_RESOURCE_RATTACH` (`rattach`),
  KEY `IDX_WEB_INFO_PAGE_RESOURCE_DOMAIN` (`domain`),
  CONSTRAINT `FK_WEB_INFO_PAGE_RESOURCE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_WEB_INFO_PAGE_RESOURCE_RATTACH` FOREIGN KEY (`rattach`) REFERENCES `rattach` (`id`),
  CONSTRAINT `FK_WEB_INFO_PAGE_RESOURCE_WEB_INFO_PAGE` FOREIGN KEY (`web_info_page`) REFERENCES `web_info_page` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Recursos de la pagina perteneciente a la ficha web';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `web_info_page_resource`
#

LOCK TABLES `web_info_page_resource` WRITE;
ALTER TABLE `web_info_page_resource` DISABLE KEYS;
ALTER TABLE `web_info_page_resource` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `web_info_style`
#

DROP TABLE IF EXISTS `web_info_style`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `web_info_style` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Codigo del Estilo de la Pagina',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `variable` varchar(128) character set latin1 collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la variable del Estilo',
  `value` varchar(255) character set latin1 collate latin1_spanish_ci default NULL COMMENT 'Valor de la variable del Estilo',
  PRIMARY KEY  (`id`),
  KEY `IDX_WEB_INFO_STYLE_DOMAIN` (`domain`),
  CONSTRAINT `FK_WEB_INFO_STYLE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Estilos a utilizar en las plantillas para generar ficha web';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `web_info_style`
#

LOCK TABLES `web_info_style` WRITE;
ALTER TABLE `web_info_style` DISABLE KEYS;
ALTER TABLE `web_info_style` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `workactivity`
#

DROP TABLE IF EXISTS `workactivity`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `workactivity` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Actividad',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Actividad',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `enterpriseCCC` int(4) NOT NULL COMMENT 'Cuenta de Cotizacin asociada a la Actividad',
  `active` tinyint(1) default '1' COMMENT 'Indica si la Actividad esta activa o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_WORKACTIVITY_ENTERPRISE_CCC` (`enterpriseCCC`),
  KEY `IDX_WORKACTIVITY_WORKPLACE` (`workplace`),
  KEY `IDX_WORKACTIVITY_DOMAIN` (`domain`),
  CONSTRAINT `FK_WORKACTIVITY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_WORKACTIVITY_ENTERPRISE_CCC` FOREIGN KEY (`enterpriseCCC`) REFERENCES `enterprise_ccc` (`id`),
  CONSTRAINT `FK_WORKACTIVITY_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades del Centro de Trabajo';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `workactivity`
#

LOCK TABLES `workactivity` WRITE;
ALTER TABLE `workactivity` DISABLE KEYS;
ALTER TABLE `workactivity` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `workgroup`
#

DROP TABLE IF EXISTS `workgroup`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `workgroup` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Grupo de Trabajo',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Grupo de Trabajo',
  `status` tinyint(2) default NULL COMMENT 'Estado del grupo de Trabajo',
  PRIMARY KEY  (`id`),
  KEY `IDX_WORKGROUP_DOMAIN` (`domain`),
  CONSTRAINT `FK_WORKGROUP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Grupos de Trabajo';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `workgroup`
#

LOCK TABLES `workgroup` WRITE;
ALTER TABLE `workgroup` DISABLE KEYS;
ALTER TABLE `workgroup` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `workplace`
#

DROP TABLE IF EXISTS `workplace`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `workplace` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Centro de Trabajo',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int(4) NOT NULL default '1' COMMENT 'Empresa asociada al Centro de Trabajo',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Centro de Trabajo',
  `address` int(4) NOT NULL COMMENT 'Identificador de la Direccion',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `economicAgreement` tinyint(2) default '0' COMMENT 'Concierto Economico del Centro de Trabajo',
  `active` tinyint(1) default '1' COMMENT 'Indica si el Centro de Trabajo esta activo o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_WORKPLACE_ENTERPRISE` (`enterprise`),
  KEY `IDX_WORKPLACE_RADDRESS` (`address`),
  KEY `IDX_WORKPLACE_SCOPE` (`scope`),
  KEY `IDX_WORKPLACE_DOMAIN` (`domain`),
  CONSTRAINT `FK_WORKPLACE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_WORKPLACE_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`),
  CONSTRAINT `FK_WORKPLACE_RADDRESS` FOREIGN KEY (`address`) REFERENCES `raddress` (`id`),
  CONSTRAINT `FK_WORKPLACE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Centros de Trabajo';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `workplace`
#

LOCK TABLES `workplace` WRITE;
ALTER TABLE `workplace` DISABLE KEYS;
INSERT INTO `workplace` VALUES (1,1,1,'Calle Sagasta, nº 15-4º Izda. ',1,1,NULL,1);
ALTER TABLE `workplace` ENABLE KEYS;
UNLOCK TABLES;

#
# Table structure for table `workplace_department`
#

DROP TABLE IF EXISTS `workplace_department`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `workplace_department` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `department` int(4) NOT NULL COMMENT 'Identificador del Departamento',
  `catalogue` int(4) default NULL COMMENT 'Identificador del Catalogo',
  `active` tinyint(1) default '1' COMMENT 'Indica si el Departamento esta activo o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_WORKPLACE_DEPARTMENT_DOMAIN` (`domain`),
  KEY `IDX_WORKPLACE_DEPARTMENT_WORKPLACE` (`workplace`),
  KEY `IDX_WORKPLACE_DEPARTMENT_DEPARTMENT` (`department`),
  KEY `IDX_WORKPLACE_DEPARTMENT_CATALOGUE` (`catalogue`),
  CONSTRAINT `FK_WORKPLACE_DEPARTMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_WORKPLACE_DEPARTMENT_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`),
  CONSTRAINT `FK_WORKPLACE_DEPARTMENT_DEPARTMENT` FOREIGN KEY (`department`) REFERENCES `department` (`id`),
  CONSTRAINT `FK_WORKPLACE_DEPARTMENT_CATALOGUE` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Departamentos del Centro de Trabajo';
SET character_set_client = @saved_cs_client;

#
# Dumping data for table `workplace_department`
#

LOCK TABLES `workplace_department` WRITE;
ALTER TABLE `workplace_department` DISABLE KEYS;
ALTER TABLE `workplace_department` ENABLE KEYS;
UNLOCK TABLES;
SET TIME_ZONE=@OLD_TIME_ZONE;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT;
SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS;
SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION;
SET SQL_NOTES=@OLD_SQL_NOTES;

