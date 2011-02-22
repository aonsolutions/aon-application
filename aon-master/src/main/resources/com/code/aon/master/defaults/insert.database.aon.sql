# MySQL dump 10.13  Distrib 5.1.52, for redhat-linux-gnu (x86_64)
#
# Host: 127.0.0.1    Database: aon_master
# ------------------------------------------------------
# Server version	5.0.77
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

#
# Not dumping tablespaces as no INFORMATION_SCHEMA.FILES table on this server
#

#
# Current Database: `aon_master`
#

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `aon_master` /*!40100 DEFAULT CHARACTER SET latin1 COLLATE latin1_spanish_ci */;

USE `aon_master`;

#
# Dumping data for table `absence`
#


#
# Dumping data for table `academic_skill`
#


#
# Dumping data for table `academic_year`
#


#
# Dumping data for table `account`
#

INSERT INTO `account` VALUES ('1','FINANCIACIÓN BÁSICA',NULL,0,1);
INSERT INTO `account` VALUES ('10','CAPITAL.',NULL,0,2);
INSERT INTO `account` VALUES ('100','Capital social.',NULL,0,3);
INSERT INTO `account` VALUES ('1000','Capital social.',NULL,0,4);
INSERT INTO `account` VALUES ('101','Fondo social.',NULL,0,3);
INSERT INTO `account` VALUES ('1010','Fondo social.',NULL,0,4);
INSERT INTO `account` VALUES ('102','Capital.',NULL,0,3);
INSERT INTO `account` VALUES ('1020','Capital.',NULL,0,4);
INSERT INTO `account` VALUES ('103','Socios por desembolsos no exigidos..',NULL,0,3);
INSERT INTO `account` VALUES ('1030','Socios por desembolsos no exigidos, capital social.',NULL,0,4);
INSERT INTO `account` VALUES ('1034','Socios por desembolsos no exigidos, capital pendiente de inscripción.',NULL,0,4);
INSERT INTO `account` VALUES ('104','Socios por aportaciones no dinerarias pendientes..',NULL,0,3);
INSERT INTO `account` VALUES ('1040','Socios por aportaciones no dinerarias pendientes, capital social.',NULL,0,4);
INSERT INTO `account` VALUES ('1044','Socios por aportaciones no dinerarias pendientes, capital pendiente de inscripción.',NULL,0,4);
INSERT INTO `account` VALUES ('108','Acciones o participaciones propias en situaciones especiales.',NULL,0,3);
INSERT INTO `account` VALUES ('1080','Acciones o participaciones propias en situaciones especiales.',NULL,0,4);
INSERT INTO `account` VALUES ('109','Acciones o participaciones propias para reducción de capital.',NULL,0,3);
INSERT INTO `account` VALUES ('1090','Acciones o participaciones propias para reducción de capital.',NULL,0,4);
INSERT INTO `account` VALUES ('11','RESERVAS.',NULL,0,2);
INSERT INTO `account` VALUES ('110','Prima de emisión o asunción.',NULL,0,3);
INSERT INTO `account` VALUES ('1100','Prima de emisión o asunción.',NULL,0,4);
INSERT INTO `account` VALUES ('111','Patrimonio neto por emisión de instrumentos financieros compuestos.',NULL,0,3);
INSERT INTO `account` VALUES ('1110','Patrimonio neto por emisión de instrumentos financieros compuestos.',NULL,0,4);
INSERT INTO `account` VALUES ('1111','Resto de instrumentos de patrimonio neto.',NULL,0,4);
INSERT INTO `account` VALUES ('112','Reserva legal.',NULL,0,3);
INSERT INTO `account` VALUES ('1120','Reserva legal.',NULL,0,4);
INSERT INTO `account` VALUES ('113','Reservas voluntarias.',NULL,0,3);
INSERT INTO `account` VALUES ('1130','Reservas voluntarias.',NULL,0,4);
INSERT INTO `account` VALUES ('114','Reservas especiales.',NULL,0,3);
INSERT INTO `account` VALUES ('1140','Reservas para acciones o participaciones de la sociedad dominante.',NULL,0,4);
INSERT INTO `account` VALUES ('1141','Reservas estatutarias.',NULL,0,4);
INSERT INTO `account` VALUES ('1142','Reserva por capital amortizado.',NULL,0,4);
INSERT INTO `account` VALUES ('1143','Reserva por fondo de comercio.',NULL,0,4);
INSERT INTO `account` VALUES ('1144','Reservas por acciones propias aceptadas en garantía.',NULL,0,4);
INSERT INTO `account` VALUES ('115','Reservas por pérdidas y ganancias actuariales y otros ajustes.',NULL,0,3);
INSERT INTO `account` VALUES ('1150','Reservas por pérdidas y ganancias actuariales y otros ajustes.',NULL,0,4);
INSERT INTO `account` VALUES ('118','Aportaciones de socios o propietarios.',NULL,0,3);
INSERT INTO `account` VALUES ('1180','Aportaciones de socios o propietarios.',NULL,0,4);
INSERT INTO `account` VALUES ('119','Diferencias por ajuste del capital a euros.',NULL,0,3);
INSERT INTO `account` VALUES ('1190','Diferencias por ajuste del capital a euros.',NULL,0,4);
INSERT INTO `account` VALUES ('12','RESULTADOS PENDIENTES DE APLICACIÓN.',NULL,0,2);
INSERT INTO `account` VALUES ('120','Remanente.',NULL,0,3);
INSERT INTO `account` VALUES ('1200','Remanente.',NULL,0,4);
INSERT INTO `account` VALUES ('121','Resultados negativos de ejercicios anteriores.',NULL,0,3);
INSERT INTO `account` VALUES ('1210','Resultados negativos de ejercicios anteriores.',NULL,0,4);
INSERT INTO `account` VALUES ('129','Resultados del ejercicio.',NULL,0,3);
INSERT INTO `account` VALUES ('1290','Resultados del ejercicio.',NULL,0,4);
INSERT INTO `account` VALUES ('129000000','Resultados del ejercicio.',NULL,1,5);
INSERT INTO `account` VALUES ('13','SUBVENCIONES, DONACIONES Y AJUSTES POR CAMBIOS DE VALOR.',NULL,0,2);
INSERT INTO `account` VALUES ('130','Subvenciones oficiales de capital.',NULL,0,3);
INSERT INTO `account` VALUES ('1300','Subvenciones oficiales de capital.',NULL,0,4);
INSERT INTO `account` VALUES ('131','Donaciones y legados de capital.',NULL,0,3);
INSERT INTO `account` VALUES ('1310','Donaciones y legados de capital.',NULL,0,4);
INSERT INTO `account` VALUES ('132','Otras subvenciones, donaciones y legados.',NULL,0,3);
INSERT INTO `account` VALUES ('1320','Otras subvenciones, donaciones y legados.',NULL,0,4);
INSERT INTO `account` VALUES ('133','Ajustes por valoración en instrumentos financieros.',NULL,0,3);
INSERT INTO `account` VALUES ('1330','Ajustes por valoración en instrumentos financieros.',NULL,0,4);
INSERT INTO `account` VALUES ('134','Operaciones de cobertura.',NULL,0,3);
INSERT INTO `account` VALUES ('1340','Cobertura de flujos de efectivo.',NULL,0,4);
INSERT INTO `account` VALUES ('1341','Cobertura de una inversión neta en un negocio en el extranjero.',NULL,0,4);
INSERT INTO `account` VALUES ('135','Diferencias de conversión.',NULL,0,3);
INSERT INTO `account` VALUES ('1350','Diferencias de conversión.',NULL,0,4);
INSERT INTO `account` VALUES ('136','Ajustes por valoración en activos no corrientes y grupos enajenables de elementos mantenidos para la venta.',NULL,0,3);
INSERT INTO `account` VALUES ('1360','Ajustes por valoración en activos no corrientes y grupos enajenables de elementos mantenidos para la venta.',NULL,0,4);
INSERT INTO `account` VALUES ('137','Ingresos fiscales a distribuir en varios ejercicios.',NULL,0,3);
INSERT INTO `account` VALUES ('1370','Ingresos fiscales por diferencias permanentes a distribuir en varios ejercicios.',NULL,0,4);
INSERT INTO `account` VALUES ('1371','Ingresos fiscales por deducciones y bonificaciones a distribuir en varios ejercicios.',NULL,0,4);
INSERT INTO `account` VALUES ('14','PROVISIONES.',NULL,0,2);
INSERT INTO `account` VALUES ('140','Provisión para retribuciones y otras prestaciones al personal.',NULL,0,3);
INSERT INTO `account` VALUES ('1400','Provisión para retribuciones y otras prestaciones al personal.',NULL,0,4);
INSERT INTO `account` VALUES ('141','Provisión para impuestos.',NULL,0,3);
INSERT INTO `account` VALUES ('1410','Provisión para impuestos.',NULL,0,4);
INSERT INTO `account` VALUES ('142','Provisión para otras responsabilidades.',NULL,0,3);
INSERT INTO `account` VALUES ('1420','Provisión para otras responsabilidades.',NULL,0,4);
INSERT INTO `account` VALUES ('143','Provisión por desmantelamiento, retiro o rehabilitación del inmovilizado.',NULL,0,3);
INSERT INTO `account` VALUES ('1430','Provisión por desmantelamiento, retiro o rehabilitación del inmovilizado.',NULL,0,4);
INSERT INTO `account` VALUES ('145','Provisión para actuaciones medioambientales.',NULL,0,3);
INSERT INTO `account` VALUES ('1450','Provisión para actuaciones medioambientales.',NULL,0,4);
INSERT INTO `account` VALUES ('146','Provisión para reestructuraciones.',NULL,0,3);
INSERT INTO `account` VALUES ('1460','Provisión para reestructuraciones.',NULL,0,4);
INSERT INTO `account` VALUES ('147','Provisiones por transacciones con pagos basados en instrumentos de patrimonio.',NULL,0,3);
INSERT INTO `account` VALUES ('1470','Provisiones por transacciones con pagos basados en instrumentos de patrimonio.',NULL,0,4);
INSERT INTO `account` VALUES ('15','DEUDAS A LARGO PLAZO CON CARACTERÍSTICAS ESPECIALES.',NULL,0,2);
INSERT INTO `account` VALUES ('150','Acciones o participaciones a largo plazo contabilizadas como pasivo.',NULL,0,3);
INSERT INTO `account` VALUES ('1500','Acciones o participaciones a largo plazo contabilizadas como pasivo.',NULL,0,4);
INSERT INTO `account` VALUES ('153','Desembolsos no exigidos por acciones o participaciones contabilizadas como pasivo.',NULL,0,3);
INSERT INTO `account` VALUES ('1530','Desembolsos no exigidos por acciones o participaciones contabilizadas como pasivo.',NULL,0,4);
INSERT INTO `account` VALUES ('154','Aportaciones no dinerarias pendientes por acciones o participaciones contabilizadas como pasivo.',NULL,0,3);
INSERT INTO `account` VALUES ('1540','Aportaciones no dinerarias pendientes por acciones o participaciones contabilizadas como pasivo.',NULL,0,4);
INSERT INTO `account` VALUES ('16','DEUDAS A LARGO PLAZO CON PARTES VINCULADAS.',NULL,0,2);
INSERT INTO `account` VALUES ('160','Deudas a largo plazo con entidades de crédito vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('1600','Deudas a largo plazo con entidades de crédito vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('161','Proveedores de inmovilizado a largo plazo, partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('1610','Proveedores de inmovilizado a largo plazo, partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('162','Otras deudas a largo plazo con partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('1620','Otras deudas a largo plazo con partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('163','Otras deudas a largo plazo con partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('1633','Otras deudas a largo plazo empresas del grupo.',NULL,0,4);
INSERT INTO `account` VALUES ('1634','Otras deudas a largo plazo empresas asociadas.',NULL,0,4);
INSERT INTO `account` VALUES ('1635','Otras deudas a largo plazo con otras partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('17','DEUDAS A LARGO PLAZO POR PRÉSTAMOS RECIBIDOS Y OTROS CONCEPTOS.',NULL,0,2);
INSERT INTO `account` VALUES ('170','Deudas a largo plazo con entidades de crédito.',NULL,0,3);
INSERT INTO `account` VALUES ('1700','Deudas a largo plazo con entidades de crédito.',NULL,0,4);
INSERT INTO `account` VALUES ('171','Deudas a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('1710','Deudas a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('172','Deudas a largo plazo transformables en subvenciones, donaciones y legados.',NULL,0,3);
INSERT INTO `account` VALUES ('1720','Deudas a largo plazo transformables en subvenciones, donaciones y legados.',NULL,0,4);
INSERT INTO `account` VALUES ('173','Proveedores de inmovilizado a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('1730','Proveedores de inmovilizado a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('174','Acreedores de arrendamiento financiero a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('1740','Acreedores de arrendamiento financiero a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('175','Efectos a pagar a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('1750','Efectos a pagar a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('176','Pasivos por derivados financieros a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('1765','Pasivos por derivados financieros a largo plazo, cartera de negociación.',NULL,0,4);
INSERT INTO `account` VALUES ('1768','Pasivos por derivados financieros a largo plazo, instrumentos de cobertura.',NULL,0,4);
INSERT INTO `account` VALUES ('177','Obligaciones y bonos.',NULL,0,3);
INSERT INTO `account` VALUES ('1770','Obligaciones y bonos.',NULL,0,4);
INSERT INTO `account` VALUES ('178','Obligaciones y bonos convertibles.',NULL,0,3);
INSERT INTO `account` VALUES ('1780','Obligaciones y bonos convertibles.',NULL,0,4);
INSERT INTO `account` VALUES ('179','Deudas representadas en otros valores negociables.',NULL,0,3);
INSERT INTO `account` VALUES ('1790','Deudas representadas en otros valores negociables.',NULL,0,4);
INSERT INTO `account` VALUES ('18','PASIVOS POR FIANZAS Y GARANTÍAS Y OTROS CONCEPTOS A LARGO PLAZO.',NULL,0,2);
INSERT INTO `account` VALUES ('180','Fianzas recibidas a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('1800','Fianzas recibidas a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('181','Anticipos recibidos por ventas o prestaciones de servicios a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('1810','Anticipos recibidos por ventas o prestaciones de servicios a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('185','Depósitos recibidos a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('1850','Depósitos recibidos a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('189','Garantías financieras a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('1890','Garantías financieras a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('19','SITUACIONES TRANSITORIAS DE FINANCIACIÓN.',NULL,0,2);
INSERT INTO `account` VALUES ('190','Acciones o participaciones emitidas.',NULL,0,3);
INSERT INTO `account` VALUES ('1900','Acciones o participaciones emitidas.',NULL,0,4);
INSERT INTO `account` VALUES ('192','Suscriptores de acciones.',NULL,0,3);
INSERT INTO `account` VALUES ('1920','Suscriptores de acciones.',NULL,0,4);
INSERT INTO `account` VALUES ('194','Capital emitido pendiente de inscripción.',NULL,0,3);
INSERT INTO `account` VALUES ('1940','Capital emitido pendiente de inscripción.',NULL,0,4);
INSERT INTO `account` VALUES ('195','Acciones o participaciones emitidas consideradas como pasivos financieros.',NULL,0,3);
INSERT INTO `account` VALUES ('1950','Acciones o participaciones emitidas consideradas como pasivos financieros.',NULL,0,4);
INSERT INTO `account` VALUES ('197','Suscriptores de acciones consideradas como pasivos financieros.',NULL,0,3);
INSERT INTO `account` VALUES ('1970','Suscriptores de acciones consideradas como pasivos financieros.',NULL,0,4);
INSERT INTO `account` VALUES ('199','Acciones o participaciones emitidas consideradas como pasivos financieros pendientes de inscripción.',NULL,0,3);
INSERT INTO `account` VALUES ('1990','Acciones o participaciones emitidas consideradas como pasivos financieros pendientes de inscripción.',NULL,0,4);
INSERT INTO `account` VALUES ('2','ACTIVO NO CORRIENTE',NULL,0,1);
INSERT INTO `account` VALUES ('20','INMOVILIZACIONES INTANGIBLES.',NULL,0,2);
INSERT INTO `account` VALUES ('200','Gastos de investigación.',NULL,0,3);
INSERT INTO `account` VALUES ('2000','Gastos de investigación.',NULL,0,4);
INSERT INTO `account` VALUES ('201','Desarrollo.',NULL,0,3);
INSERT INTO `account` VALUES ('2010','Desarrollo.',NULL,0,4);
INSERT INTO `account` VALUES ('202','Concesiones administrativas.',NULL,0,3);
INSERT INTO `account` VALUES ('2020','Concesiones administrativas.',NULL,0,4);
INSERT INTO `account` VALUES ('203','Propiedad industrial.',NULL,0,3);
INSERT INTO `account` VALUES ('2030','Propiedad industrial.',NULL,0,4);
INSERT INTO `account` VALUES ('204','Fondo de comercio.',NULL,0,3);
INSERT INTO `account` VALUES ('2040','Fondo de comercio.',NULL,0,4);
INSERT INTO `account` VALUES ('205','Derechos de traspaso.',NULL,0,3);
INSERT INTO `account` VALUES ('2050','Derechos de traspaso.',NULL,0,4);
INSERT INTO `account` VALUES ('206','Aplicaciones informáticas.',NULL,0,3);
INSERT INTO `account` VALUES ('2060','Aplicaciones informáticas.',NULL,0,4);
INSERT INTO `account` VALUES ('209','Anticipos para inmovilizaciones intangibles.',NULL,0,3);
INSERT INTO `account` VALUES ('2090','Anticipos para inmovilizaciones intangibles.',NULL,0,4);
INSERT INTO `account` VALUES ('21','INMOVILIZACIONES MATERIALES.',NULL,0,2);
INSERT INTO `account` VALUES ('210','Terrenos y bienes naturales.',NULL,0,3);
INSERT INTO `account` VALUES ('2100','Terrenos y bienes naturales.',NULL,0,4);
INSERT INTO `account` VALUES ('211','Construcciones .',NULL,0,3);
INSERT INTO `account` VALUES ('2110','Construcciones .',NULL,0,4);
INSERT INTO `account` VALUES ('212','Instalaciones técnicas.',NULL,0,3);
INSERT INTO `account` VALUES ('2120','Instalaciones técnicas.',NULL,0,4);
INSERT INTO `account` VALUES ('213','Maquinaria.',NULL,0,3);
INSERT INTO `account` VALUES ('2130','Maquinaria.',NULL,0,4);
INSERT INTO `account` VALUES ('214','Utillaje.',NULL,0,3);
INSERT INTO `account` VALUES ('2140','Utillaje.',NULL,0,4);
INSERT INTO `account` VALUES ('215','Otras instalaciones.',NULL,0,3);
INSERT INTO `account` VALUES ('2150','Otras instalaciones.',NULL,0,4);
INSERT INTO `account` VALUES ('216','Mobiliario.',NULL,0,3);
INSERT INTO `account` VALUES ('2160','Mobiliario.',NULL,0,4);
INSERT INTO `account` VALUES ('217','Equipos para procesos de información.',NULL,0,3);
INSERT INTO `account` VALUES ('2170','Equipos para procesos de información.',NULL,0,4);
INSERT INTO `account` VALUES ('218','Elementos de transporte.',NULL,0,3);
INSERT INTO `account` VALUES ('2180','Elementos de transporte.',NULL,0,4);
INSERT INTO `account` VALUES ('219','Otro inmovilizado material.',NULL,0,3);
INSERT INTO `account` VALUES ('2190','Otro inmovilizado material.',NULL,0,4);
INSERT INTO `account` VALUES ('22','INVERSIONES INMOBILIARIAS.',NULL,0,2);
INSERT INTO `account` VALUES ('220','Inversiones en terrenos y bienes naturales.',NULL,0,3);
INSERT INTO `account` VALUES ('2200','Inversiones en terrenos y bienes naturales.',NULL,0,4);
INSERT INTO `account` VALUES ('221','Inversiones en construcciones.',NULL,0,3);
INSERT INTO `account` VALUES ('2210','Inversiones en construcciones.',NULL,0,4);
INSERT INTO `account` VALUES ('23','INMOVILIZACIONES MATERIALES EN CURSO.',NULL,0,2);
INSERT INTO `account` VALUES ('230','Adaptación de terrenos y bienes naturales.',NULL,0,3);
INSERT INTO `account` VALUES ('2300','Adaptación de terrenos y bienes naturales.',NULL,0,4);
INSERT INTO `account` VALUES ('231','Construcciones en curso.',NULL,0,3);
INSERT INTO `account` VALUES ('2310','Construcciones en curso.',NULL,0,4);
INSERT INTO `account` VALUES ('232','Instalaciones técnicas en montaje.',NULL,0,3);
INSERT INTO `account` VALUES ('2320','Instalaciones técnicas en montaje.',NULL,0,4);
INSERT INTO `account` VALUES ('233','Maquinaria en montaje.',NULL,0,3);
INSERT INTO `account` VALUES ('2330','Maquinaria en montaje.',NULL,0,4);
INSERT INTO `account` VALUES ('237','Equipos para procesos de información en montaje.',NULL,0,3);
INSERT INTO `account` VALUES ('2370','Equipos para procesos de información en montaje.',NULL,0,4);
INSERT INTO `account` VALUES ('239','Anticipos para inmovilizaciones materiales.',NULL,0,3);
INSERT INTO `account` VALUES ('2390','Anticipos para inmovilizaciones materiales.',NULL,0,4);
INSERT INTO `account` VALUES ('24','INVERSIONES FINANCIERAS A LARGO PLAZO EN PARTES VINCULADAS.',NULL,0,2);
INSERT INTO `account` VALUES ('240','Participaciones a largo plazo en partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('2400','Participaciones a largo plazo en partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('241','Valores representativos de deuda a largo plazo de partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('2410','Valores representativos de deuda a largo plazo de partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('242','Créditos a largo plazo a partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('2420','Créditos a largo plazo a partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('249','Desembolsos pendientes sobre participaciones a largo plazo en partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('2490','Desembolsos pendientes sobre participaciones a largo plazo en partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('25','OTRAS INVERSIONES FINANCIERAS A LARGO PLAZO.',NULL,0,2);
INSERT INTO `account` VALUES ('250','Inversiones financieras a largo plazo en instrumentos de patrimonio.',NULL,0,3);
INSERT INTO `account` VALUES ('2500','Inversiones financieras a largo plazo en instrumentos de patrimonio.',NULL,0,4);
INSERT INTO `account` VALUES ('251','Valores representativos de deuda a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('2510','Valores representativos de deuda a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('252','Créditos a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('2520','Créditos a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('253','Créditos a largo plazo por enajenación de inmovilizado.',NULL,0,3);
INSERT INTO `account` VALUES ('2530','Créditos a largo plazo por enajenación de inmovilizado.',NULL,0,4);
INSERT INTO `account` VALUES ('254','Créditos a largo plazo al personal.',NULL,0,3);
INSERT INTO `account` VALUES ('2540','Créditos a largo plazo al personal.',NULL,0,4);
INSERT INTO `account` VALUES ('255','Activos por derivados financieros a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('2550','Activos por derivados financieros a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('257','Activos por retribuciones a largo plazo de prestación definida.',NULL,0,3);
INSERT INTO `account` VALUES ('2570','Activos por retribuciones a largo plazo de prestación definida.',NULL,0,4);
INSERT INTO `account` VALUES ('258','Imposiciones a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('2580','Imposiciones a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('259','Desembolsos pendientes sobre participaciones en el patrimonio neto a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('2590','Desembolsos pendientes sobre participaciones en el patrimonio neto a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('26','FIANZAS Y DEPÓSITOS CONSTITUIDOS A LARGO PLAZO.',NULL,0,2);
INSERT INTO `account` VALUES ('260','Fianzas constituidas a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('2600','Fianzas constituidas a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('265','Depósitos constituidos a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('2650','Depósitos constituidos a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('28','AMORTIZACIÓN ACUMULADA DEL INMOVILIZADO.',NULL,0,2);
INSERT INTO `account` VALUES ('280','Amortización acumulada del inmovilizado intangible.',NULL,0,3);
INSERT INTO `account` VALUES ('2800','Amortización acumulada del inmovilizado intangible.',NULL,0,4);
INSERT INTO `account` VALUES ('2801','Amortización acumulada de investigación',NULL,0,4);
INSERT INTO `account` VALUES ('2802','Amortización acumulada de desarrollo',NULL,0,4);
INSERT INTO `account` VALUES ('2803','Amortización acumulada de concesiones administrativas',NULL,0,4);
INSERT INTO `account` VALUES ('2804','Amortización acumulada de propiedad industrial',NULL,0,4);
INSERT INTO `account` VALUES ('2805','Amortización acumulada de derechos de traspaso',NULL,0,4);
INSERT INTO `account` VALUES ('2806','Amortización acumulada de aplicaciones informáticas',NULL,0,4);
INSERT INTO `account` VALUES ('281','Amortización acumulada del inmovilizado material.',NULL,0,3);
INSERT INTO `account` VALUES ('2810','Amortización acumulada del inmovilizado material.',NULL,0,4);
INSERT INTO `account` VALUES ('2811','Amortización acumulada de construcciones',NULL,0,4);
INSERT INTO `account` VALUES ('2812','Amortización acumulada de instalaciones técnicas',NULL,0,4);
INSERT INTO `account` VALUES ('2813','Amortización acumulada de maquinaria',NULL,0,4);
INSERT INTO `account` VALUES ('2814','Amortización acumulada de utillaje',NULL,0,4);
INSERT INTO `account` VALUES ('2815','Amortización acumulada de otras instalaciones',NULL,0,4);
INSERT INTO `account` VALUES ('2816','Amortización acumulada de mobiliario',NULL,0,4);
INSERT INTO `account` VALUES ('2817','Amortización acumulada de equipos para el proceso de información',NULL,0,4);
INSERT INTO `account` VALUES ('2818','Amortización acumulada de elementos de transporte',NULL,0,4);
INSERT INTO `account` VALUES ('2819','Amortización acumulada de otro inmovilizado material',NULL,0,4);
INSERT INTO `account` VALUES ('282','Amortización acumulada de las inversiones inmobiliarias.',NULL,0,3);
INSERT INTO `account` VALUES ('2820','Amortización acumulada de las inversiones inmobiliarias.',NULL,0,4);
INSERT INTO `account` VALUES ('29','DETERIORO DE VALOR DE ACTIVOS NO CORRIENTES.',NULL,0,2);
INSERT INTO `account` VALUES ('290','Deterioro de valor del inmovilizado intangible.',NULL,0,3);
INSERT INTO `account` VALUES ('2900','Deterioro de valor del inmovilizado intangible.',NULL,0,4);
INSERT INTO `account` VALUES ('291','Deterioro de valor del inmovilizado material.',NULL,0,3);
INSERT INTO `account` VALUES ('2910','Deterioro de valor del inmovilizado material.',NULL,0,4);
INSERT INTO `account` VALUES ('292','Deterioro de valor de las inversiones inmobiliarias.',NULL,0,3);
INSERT INTO `account` VALUES ('2920','Deterioro de valor de las inversiones inmobiliarias.',NULL,0,4);
INSERT INTO `account` VALUES ('293','Deterioro de valor de participaciones a largo plazo en partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('2930','Deterioro de valor de participaciones a largo plazo en partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('294','Deterioro de valor de valores representativos de deuda a largo plazo de partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('2940','Deterioro de valor de valores representativos de deuda a largo plazo de partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('295','Deterioro de valor de créditos a largo plazo a partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('2950','Deterioro de valor de créditos a largo plazo a partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('297','Deterioro de valor de valores representativos de deuda a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('2970','Deterioro de valor de valores representativos de deuda a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('298','Deterioro de valor de créditos a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('2980','Deterioro de valor de créditos a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('3','EXISTENCIAS',NULL,0,1);
INSERT INTO `account` VALUES ('30','COMERCIALES.',NULL,0,2);
INSERT INTO `account` VALUES ('300','Mercaderías A.',NULL,0,3);
INSERT INTO `account` VALUES ('3000','Mercaderías A.',NULL,0,4);
INSERT INTO `account` VALUES ('301','Mercaderías B.',NULL,0,3);
INSERT INTO `account` VALUES ('3010','Mercaderías B.',NULL,0,4);
INSERT INTO `account` VALUES ('31','MATERIAS PRIMAS.',NULL,0,2);
INSERT INTO `account` VALUES ('310','Materias primas A.',NULL,0,3);
INSERT INTO `account` VALUES ('3100','Materias primas A.',NULL,0,4);
INSERT INTO `account` VALUES ('311','Materias primas B.',NULL,0,3);
INSERT INTO `account` VALUES ('3110','Materias primas B.',NULL,0,4);
INSERT INTO `account` VALUES ('32','OTROS APROVISIONAMIENTOS.',NULL,0,2);
INSERT INTO `account` VALUES ('320','Elementos conjuntos incorporables.',NULL,0,3);
INSERT INTO `account` VALUES ('3200','Elementos conjuntos incorporables.',NULL,0,4);
INSERT INTO `account` VALUES ('321','Combustibles.',NULL,0,3);
INSERT INTO `account` VALUES ('3210','Combustibles.',NULL,0,4);
INSERT INTO `account` VALUES ('322','Repuestos.',NULL,0,3);
INSERT INTO `account` VALUES ('3220','Repuestos.',NULL,0,4);
INSERT INTO `account` VALUES ('325','Materiales diversos.',NULL,0,3);
INSERT INTO `account` VALUES ('3250','Materiales diversos.',NULL,0,4);
INSERT INTO `account` VALUES ('326','Embalajes.',NULL,0,3);
INSERT INTO `account` VALUES ('3260','Embalajes.',NULL,0,4);
INSERT INTO `account` VALUES ('327','Envases.',NULL,0,3);
INSERT INTO `account` VALUES ('3270','Envases.',NULL,0,4);
INSERT INTO `account` VALUES ('328','Material de oficina.',NULL,0,3);
INSERT INTO `account` VALUES ('3280','Material de oficina.',NULL,0,4);
INSERT INTO `account` VALUES ('33','PRODUCTOS EN CURSO.',NULL,0,2);
INSERT INTO `account` VALUES ('330','Productos en curso A.',NULL,0,3);
INSERT INTO `account` VALUES ('3300','Productos en curso A.',NULL,0,4);
INSERT INTO `account` VALUES ('331','Productos en curso B.',NULL,0,3);
INSERT INTO `account` VALUES ('3310','Productos en curso B.',NULL,0,4);
INSERT INTO `account` VALUES ('34','PRODUCTOS SEMITERMINADOS.',NULL,0,2);
INSERT INTO `account` VALUES ('340','Productos semiterminados A.',NULL,0,3);
INSERT INTO `account` VALUES ('3400','Productos semiterminados A.',NULL,0,4);
INSERT INTO `account` VALUES ('341','Productos semiterminados B.',NULL,0,3);
INSERT INTO `account` VALUES ('3410','Productos semiterminados B.',NULL,0,4);
INSERT INTO `account` VALUES ('35','PRODUCTOS TERMINADOS.',NULL,0,2);
INSERT INTO `account` VALUES ('350','Productos terminados A.',NULL,0,3);
INSERT INTO `account` VALUES ('3500','Productos terminados A.',NULL,0,4);
INSERT INTO `account` VALUES ('351','Productos terminados B.',NULL,0,3);
INSERT INTO `account` VALUES ('3510','Productos terminados B.',NULL,0,4);
INSERT INTO `account` VALUES ('36','SUBPRODUCTOS, RESIDUOS Y MATERIALES RECUPERADOS.',NULL,0,2);
INSERT INTO `account` VALUES ('360','Subproductos A.',NULL,0,3);
INSERT INTO `account` VALUES ('3600','Subproductos A.',NULL,0,4);
INSERT INTO `account` VALUES ('361','Subproductos B.',NULL,0,3);
INSERT INTO `account` VALUES ('3610','Subproductos B.',NULL,0,4);
INSERT INTO `account` VALUES ('365','Residuos A.',NULL,0,3);
INSERT INTO `account` VALUES ('3650','Residuos A.',NULL,0,4);
INSERT INTO `account` VALUES ('366','Residuos B.',NULL,0,3);
INSERT INTO `account` VALUES ('3660','Residuos B.',NULL,0,4);
INSERT INTO `account` VALUES ('368','Materiales recuperados A.',NULL,0,3);
INSERT INTO `account` VALUES ('3680','Materiales recuperados A.',NULL,0,4);
INSERT INTO `account` VALUES ('369','Materiales recuperados B.',NULL,0,3);
INSERT INTO `account` VALUES ('3690','Materiales recuperados B.',NULL,0,4);
INSERT INTO `account` VALUES ('39','DETERIORO DE VALOR DE LAS EXISTENCIAS.',NULL,0,2);
INSERT INTO `account` VALUES ('390','Deterioro de valor de las mercaderías.',NULL,0,3);
INSERT INTO `account` VALUES ('3900','Deterioro de valor de las mercaderías.',NULL,0,4);
INSERT INTO `account` VALUES ('391','Deterioro de valor de las materias primas.',NULL,0,3);
INSERT INTO `account` VALUES ('3910','Deterioro de valor de las materias primas.',NULL,0,4);
INSERT INTO `account` VALUES ('392','Deterioro de valor de otros aprovisionamientos.',NULL,0,3);
INSERT INTO `account` VALUES ('3920','Deterioro de valor de otros aprovisionamientos.',NULL,0,4);
INSERT INTO `account` VALUES ('393','Deterioro de valor de los productos en curso.',NULL,0,3);
INSERT INTO `account` VALUES ('3930','Deterioro de valor de los productos en curso.',NULL,0,4);
INSERT INTO `account` VALUES ('394','Deterioro de valor de los productos semiterminados.',NULL,0,3);
INSERT INTO `account` VALUES ('3940','Deterioro de valor de los productos semiterminados.',NULL,0,4);
INSERT INTO `account` VALUES ('395','Deterioro de valor de los productos terminados.',NULL,0,3);
INSERT INTO `account` VALUES ('3950','Deterioro de valor de los productos terminados.',NULL,0,4);
INSERT INTO `account` VALUES ('396','Deterioro de valor de los subproductos, residuos y materiales recuperados.',NULL,0,3);
INSERT INTO `account` VALUES ('3960','Deterioro de valor de los subproductos, residuos y materiales recuperados.',NULL,0,4);
INSERT INTO `account` VALUES ('4','ACREEDORES Y DEUDORES POR OPERACIONES COMERCIALES',NULL,0,1);
INSERT INTO `account` VALUES ('40','PROVEEDORES.',NULL,0,2);
INSERT INTO `account` VALUES ('400','Proveedores.',NULL,0,3);
INSERT INTO `account` VALUES ('4000','Proveedores.',NULL,0,4);
INSERT INTO `account` VALUES ('401','Proveedores, efectos comerciales a pagar.',NULL,0,3);
INSERT INTO `account` VALUES ('4010','Proveedores, efectos comerciales a pagar.',NULL,0,4);
INSERT INTO `account` VALUES ('403','Proveedores, empresas del grupo.',NULL,0,3);
INSERT INTO `account` VALUES ('4030','Proveedores, empresas del grupo.',NULL,0,4);
INSERT INTO `account` VALUES ('404','Proveedores, empresas asociadas.',NULL,0,3);
INSERT INTO `account` VALUES ('4040','Proveedores, empresas asociadas.',NULL,0,4);
INSERT INTO `account` VALUES ('405','Proveedores, otras partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('4050','Proveedores, otras partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('406','Envases y embalajes a devolver a proveedores.',NULL,0,3);
INSERT INTO `account` VALUES ('4060','Envases y embalajes a devolver a proveedores.',NULL,0,4);
INSERT INTO `account` VALUES ('407','Anticipos a proveedores.',NULL,0,3);
INSERT INTO `account` VALUES ('4070','Anticipos a proveedores.',NULL,0,4);
INSERT INTO `account` VALUES ('41','ACREEDORES VARIOS.',NULL,0,2);
INSERT INTO `account` VALUES ('410','Acreedores por prestaciones de servicios.',NULL,0,3);
INSERT INTO `account` VALUES ('4100','Acreedores por prestaciones de servicios.',NULL,0,4);
INSERT INTO `account` VALUES ('411','Acreedores, efectos comerciales a pagar.',NULL,0,3);
INSERT INTO `account` VALUES ('4110','Acreedores, efectos comerciales a pagar.',NULL,0,4);
INSERT INTO `account` VALUES ('419','Acreedores por operaciones en común.',NULL,0,3);
INSERT INTO `account` VALUES ('4190','Acreedores por operaciones en común.',NULL,0,4);
INSERT INTO `account` VALUES ('43','CLIENTES.',NULL,0,2);
INSERT INTO `account` VALUES ('430','Clientes.',NULL,0,3);
INSERT INTO `account` VALUES ('4300','Clientes.',NULL,0,4);
INSERT INTO `account` VALUES ('431','Clientes, efectos comerciales a cobrar.',NULL,0,3);
INSERT INTO `account` VALUES ('4310','Clientes, efectos comerciales a cobrar.',NULL,0,4);
INSERT INTO `account` VALUES ('432','Clientes operaciones de factoring.',NULL,0,3);
INSERT INTO `account` VALUES ('4320','Clientes operaciones de factoring.',NULL,0,4);
INSERT INTO `account` VALUES ('433','Clientes, empresas del grupo.',NULL,0,3);
INSERT INTO `account` VALUES ('4330','Clientes, empresas del grupo.',NULL,0,4);
INSERT INTO `account` VALUES ('434','Clientes, empresas asociadas.',NULL,0,3);
INSERT INTO `account` VALUES ('4340','Clientes, empresas asociadas.',NULL,0,4);
INSERT INTO `account` VALUES ('435','Clientes, otras partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('4350','Clientes, otras partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('436','Clientes de dudoso cobro.',NULL,0,3);
INSERT INTO `account` VALUES ('4360','Clientes de dudoso cobro.',NULL,0,4);
INSERT INTO `account` VALUES ('437','Envases y embalajes a devolver por clientes.',NULL,0,3);
INSERT INTO `account` VALUES ('4370','Envases y embalajes a devolver por clientes.',NULL,0,4);
INSERT INTO `account` VALUES ('438','Anticipos de clientes.',NULL,0,3);
INSERT INTO `account` VALUES ('4380','Anticipos de clientes.',NULL,0,4);
INSERT INTO `account` VALUES ('44','DEUDORES VARIOS.',NULL,0,2);
INSERT INTO `account` VALUES ('440','Deudores.',NULL,0,3);
INSERT INTO `account` VALUES ('4400','Deudores.',NULL,0,4);
INSERT INTO `account` VALUES ('441','Deudores, efectos comerciales a cobrar.',NULL,0,3);
INSERT INTO `account` VALUES ('4410','Deudores, efectos comerciales a cobrar.',NULL,0,4);
INSERT INTO `account` VALUES ('446','Deudores de dudoso cobro.',NULL,0,3);
INSERT INTO `account` VALUES ('4460','Deudores de dudoso cobro.',NULL,0,4);
INSERT INTO `account` VALUES ('449','Deudores por operaciones en común.',NULL,0,3);
INSERT INTO `account` VALUES ('4490','Deudores por operaciones en común.',NULL,0,4);
INSERT INTO `account` VALUES ('46','PERSONAL.',NULL,0,2);
INSERT INTO `account` VALUES ('460','Anticipos de remuneraciones.',NULL,0,3);
INSERT INTO `account` VALUES ('4600','Anticipos de remuneraciones.',NULL,0,4);
INSERT INTO `account` VALUES ('465','Remuneraciones pendientes de pago.',NULL,0,3);
INSERT INTO `account` VALUES ('4650','Remuneraciones pendientes de pago.',NULL,0,4);
INSERT INTO `account` VALUES ('465000000','Remuneraciones pendientes de pago.',NULL,1,5);
INSERT INTO `account` VALUES ('466','Remuneraciones mediante sistemas de aportación definida pendientes de pago.',NULL,0,3);
INSERT INTO `account` VALUES ('4660','Remuneraciones mediante sistemas de aportación definida pendientes de pago.',NULL,0,4);
INSERT INTO `account` VALUES ('47','ADMINISTRACIONES PÚBLICAS.',NULL,0,2);
INSERT INTO `account` VALUES ('470','Hacienda Pública deudora por diversos conceptos.',NULL,0,3);
INSERT INTO `account` VALUES ('4700','Hacienda Pública, deudora por IVA.',NULL,0,4);
INSERT INTO `account` VALUES ('4708','Hacienda Pública, deudora por subvenciones concedidas.',NULL,0,4);
INSERT INTO `account` VALUES ('4709','Hacienda Pública, deudora por devolución de impuestos.',NULL,0,4);
INSERT INTO `account` VALUES ('471','Organismos de la Seguridad Social, deudores.',NULL,0,3);
INSERT INTO `account` VALUES ('4710','Organismos de la Seguridad Social, deudores.',NULL,0,4);
INSERT INTO `account` VALUES ('472','Hacienda Pública, IVA soportado.',NULL,0,3);
INSERT INTO `account` VALUES ('4720','Hacienda Pública, IVA soportado.',NULL,0,4);
INSERT INTO `account` VALUES ('472000000','Hacienda Pública, IVA soportado.',NULL,1,5);
INSERT INTO `account` VALUES ('473','Hacienda Pública, retenciones y pagos a cuenta.',NULL,0,3);
INSERT INTO `account` VALUES ('4730','Hacienda Pública, retenciones y pagos a cuenta.',NULL,0,4);
INSERT INTO `account` VALUES ('473000000','Hacienda Pública, retenciones y pagos a cuenta.',NULL,1,5);
INSERT INTO `account` VALUES ('474','Activos por impuesto diferido.',NULL,0,3);
INSERT INTO `account` VALUES ('4740','Activos por diferencias temporarias deducibles.',NULL,0,4);
INSERT INTO `account` VALUES ('4742','Derechos por deducciones y bonificaciones pendientes de aplicar.',NULL,0,4);
INSERT INTO `account` VALUES ('4745','Crédito por pérdidas a compensar del ejercicio.',NULL,0,4);
INSERT INTO `account` VALUES ('475','Hacienda Pública acreedora por conceptos fiscales.',NULL,0,3);
INSERT INTO `account` VALUES ('4750','Hacienda Pública, acreedora por IVA.',NULL,0,4);
INSERT INTO `account` VALUES ('4751','Hacienda Pública, acreedora por retenciones practicadas.',NULL,0,4);
INSERT INTO `account` VALUES ('475100000','Hacienda Pública, acreedora por retenciones practicadas.',NULL,1,5);
INSERT INTO `account` VALUES ('4752','Hacienda Pública, acreedora por impuesto sobre sociedades.',NULL,0,4);
INSERT INTO `account` VALUES ('4758','Hacienda Pública, acreedora por subvenciones a reintegrar.',NULL,0,4);
INSERT INTO `account` VALUES ('476','Organismos de la Seguridad Social, acreedores.',NULL,0,3);
INSERT INTO `account` VALUES ('4760','Organismos de la Seguridad Social, acreedores.',NULL,0,4);
INSERT INTO `account` VALUES ('476000000','Organismos de la Seguridad Social, acreedores.',NULL,1,5);
INSERT INTO `account` VALUES ('477','Hacienda Pública, IVA repercutido.',NULL,0,3);
INSERT INTO `account` VALUES ('4770','Hacienda Pública, IVA repercutido.',NULL,0,4);
INSERT INTO `account` VALUES ('477000000','Hacienda Pública, IVA repercutido.',NULL,1,5);
INSERT INTO `account` VALUES ('479','Pasivos por diferencias temporarias imponibles.',NULL,0,3);
INSERT INTO `account` VALUES ('4790','Pasivos por diferencias temporarias imponibles.',NULL,0,4);
INSERT INTO `account` VALUES ('48','AJUSTES POR PERIODIFICACIÓN.',NULL,0,2);
INSERT INTO `account` VALUES ('480','Gastos anticipados.',NULL,0,3);
INSERT INTO `account` VALUES ('4800','Gastos anticipados.',NULL,0,4);
INSERT INTO `account` VALUES ('485','Ingresos anticipados.',NULL,0,3);
INSERT INTO `account` VALUES ('4850','Ingresos anticipados.',NULL,0,4);
INSERT INTO `account` VALUES ('49','DETERIORO DE VALOR DE CRÉDITOS COMERCIALES Y PROVISIONES A CORTO PLAZO.',NULL,0,2);
INSERT INTO `account` VALUES ('490','Deterioro de valor de créditos por operaciones comerciales.',NULL,0,3);
INSERT INTO `account` VALUES ('4900','Deterioro de valor de créditos por operaciones comerciales.',NULL,0,4);
INSERT INTO `account` VALUES ('493','Deterioro de valor de créditos por operaciones comerciales con partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('4930','Deterioro de valor de créditos por operaciones comerciales con partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('499','Provisiones por operaciones comerciales.',NULL,0,3);
INSERT INTO `account` VALUES ('4994','Provisión para contratos onerosos.',NULL,0,4);
INSERT INTO `account` VALUES ('4999','Provisión para otras operaciones comerciales.',NULL,0,4);
INSERT INTO `account` VALUES ('5','CUENTAS FINANCIERAS',NULL,0,1);
INSERT INTO `account` VALUES ('50','EMPRÉSTITOS, DEUDAS CON CARACTERÍSTICAS ESPECIALES Y OTRAS EMISIONES ANÁLOGAS A CORTO PLAZO.',NULL,0,2);
INSERT INTO `account` VALUES ('500','Obligaciones y bonos a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('5000','Obligaciones y bonos a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('501','Obligaciones y bonos convertibles a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('5010','Obligaciones y bonos convertibles a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('502','Acciones o participaciones a corto plazo contabilizadas como pasivo.',NULL,0,3);
INSERT INTO `account` VALUES ('5020','Acciones o participaciones a corto plazo contabilizadas como pasivo.',NULL,0,4);
INSERT INTO `account` VALUES ('505','Deudas representadas en otros valores negociables a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('5050','Deudas representadas en otros valores negociables a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('506','Intereses de empréstitos y otras emisiones análogas.',NULL,0,3);
INSERT INTO `account` VALUES ('5060','Intereses de empréstitos y otras emisiones análogas.',NULL,0,4);
INSERT INTO `account` VALUES ('507','Dividendos de emisiones contabilizadas como pasivo.',NULL,0,3);
INSERT INTO `account` VALUES ('5070','Dividendos de emisiones contabilizadas como pasivo.',NULL,0,4);
INSERT INTO `account` VALUES ('509','Valores negociables amortizados.',NULL,0,3);
INSERT INTO `account` VALUES ('5090','Valores negociables amortizados.',NULL,0,4);
INSERT INTO `account` VALUES ('51','DEUDAS A CORTO PLAZO CON PARTES VINCULADAS.',NULL,0,2);
INSERT INTO `account` VALUES ('510','Deudas a corto plazo con entidades de crédito vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('5100','Deudas a corto plazo con entidades de crédito vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('511','Proveedores de inmovilizado a corto plazo, partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('5110','Proveedores de inmovilizado a corto plazo, partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('512','Acreedores por arrendamiento financiero a corto plazo, partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('5120','Acreedores por arrendamiento financiero a corto plazo, partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('513','Otras deudas a corto plazo con partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('5130','Otras deudas a corto plazo con partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('514','Intereses a corto plazo de deudas con partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('5140','Intereses a corto plazo de deudas con partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('52','DEUDAS A CORTO PLAZO POR PRÉSTAMOS RECIBIDOS Y OTROS CONCEPTOS.',NULL,0,2);
INSERT INTO `account` VALUES ('520','Deudas a corto plazo con entidades de crédito.',NULL,0,3);
INSERT INTO `account` VALUES ('5200','Préstamos a corto plazo de entidades de crédito.',NULL,0,4);
INSERT INTO `account` VALUES ('5201','Deudas a corto plazo por crédito dispuesto.',NULL,0,4);
INSERT INTO `account` VALUES ('5208','Deudas por efectos descontados.',NULL,0,4);
INSERT INTO `account` VALUES ('5209','Deudas por operaciones de factoring.',NULL,0,4);
INSERT INTO `account` VALUES ('521','Deudas a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('5210','Deudas a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('522','Deudas a corto plazo transformables en subvenciones, donaciones y legados.',NULL,0,3);
INSERT INTO `account` VALUES ('5220','Deudas a corto plazo transformables en subvenciones, donaciones y legados.',NULL,0,4);
INSERT INTO `account` VALUES ('523','Proveedores de inmovilizado a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('5230','Proveedores de inmovilizado a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('525','Efectos a pagar a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('5250','Efectos a pagar a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('526','Dividendo activo a pagar.',NULL,0,3);
INSERT INTO `account` VALUES ('5260','Dividendo activo a pagar.',NULL,0,4);
INSERT INTO `account` VALUES ('527','Intereses a corto plazo de deudas con entidades de crédito.',NULL,0,3);
INSERT INTO `account` VALUES ('5270','Intereses a corto plazo de deudas con entidades de crédito.',NULL,0,4);
INSERT INTO `account` VALUES ('528','Intereses a corto plazo de deudas.',NULL,0,3);
INSERT INTO `account` VALUES ('5280','Intereses a corto plazo de deudas.',NULL,0,4);
INSERT INTO `account` VALUES ('529','Provisiones a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('5290','Provisiones a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('53','INVERSIONES FINANCIERAS A CORTO PLAZO EN PARTES VINCULADAS.',NULL,0,2);
INSERT INTO `account` VALUES ('530','Participaciones a corto plazo en partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('5303','Participaciones a corto plazo en empresas del grupo.',NULL,0,4);
INSERT INTO `account` VALUES ('5304','Participaciones a corto plazo en empresas del asociadas.',NULL,0,4);
INSERT INTO `account` VALUES ('5305','Participaciones a corto plazo, en otras partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('531','Valores representativos de deuda a corto plazo de partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('5310','Valores representativos de deuda a corto plazo de partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('532','Créditos a corto plazo a partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('5320','Créditos a corto plazo a partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('533','Intereses a corto plazo de inversiones financieras en partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('5330','Intereses a corto plazo de inversiones financieras en partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('534','Intereses a corto plazo de créditos a partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('5340','Intereses a corto plazo de créditos a partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('535','Dividendo a cobrar de inversiones financieras en partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('5350','Dividendo a cobrar de inversiones financieras en partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('539','Desembolsos pendientes sobre participaciones a corto plazo de partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('5390','Desembolsos pendientes sobre participaciones a corto plazo de partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('54','OTRAS INVERSIONES FINANCIERAS A CORTO PLAZO.',NULL,0,2);
INSERT INTO `account` VALUES ('540','Inversiones financieras temporales en instrumentos de patrimonio.',NULL,0,3);
INSERT INTO `account` VALUES ('5400','Inversiones financieras temporales en instrumentos de patrimonio.',NULL,0,4);
INSERT INTO `account` VALUES ('541','Valores representativos de deuda a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('5410','Valores representativos de deuda a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('542','Créditos a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('5420','Créditos a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('543','Créditos a corto plazo por enajenación de inmovilizado.',NULL,0,3);
INSERT INTO `account` VALUES ('5430','Créditos a corto plazo por enajenación de inmovilizado.',NULL,0,4);
INSERT INTO `account` VALUES ('544','Créditos a corto plazo al personal.',NULL,0,3);
INSERT INTO `account` VALUES ('5440','Créditos a corto plazo al personal.',NULL,0,4);
INSERT INTO `account` VALUES ('545','Dividendo a cobrar.',NULL,0,3);
INSERT INTO `account` VALUES ('5450','Dividendo a cobrar.',NULL,0,4);
INSERT INTO `account` VALUES ('546','Intereses a corto plazo de valores representativos de deuda.',NULL,0,3);
INSERT INTO `account` VALUES ('5460','Intereses a corto plazo de valores representativos de deuda.',NULL,0,4);
INSERT INTO `account` VALUES ('547','Intereses a corto plazo de créditos.',NULL,0,3);
INSERT INTO `account` VALUES ('5470','Intereses a corto plazo de créditos.',NULL,0,4);
INSERT INTO `account` VALUES ('548','Imposiciones a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('5480','Imposiciones a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('549','Desembolsos pendientes sobre instrumentos de patrimonio a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('5490','Desembolsos pendientes sobre instrumentos de patrimonio a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('55','OTRAS CUENTAS NO BANCARIAS.',NULL,0,2);
INSERT INTO `account` VALUES ('550','Titular de la explotación.',NULL,0,3);
INSERT INTO `account` VALUES ('5500','Titular de la explotación.',NULL,0,4);
INSERT INTO `account` VALUES ('551','Cuenta corriente con socios administradores.',NULL,0,3);
INSERT INTO `account` VALUES ('5510','Cuenta corriente con socios administradores.',NULL,0,4);
INSERT INTO `account` VALUES ('552','Cuenta corriente con otras personas y entidades vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('5520','Cuenta corriente con otras personas y entidades vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('553','Cuentas corrientes en fusiones y escisiones.',NULL,0,3);
INSERT INTO `account` VALUES ('5530','Socios de sociedad disuelta.',NULL,0,4);
INSERT INTO `account` VALUES ('5531','Socios, cuenta de fusión.',NULL,0,4);
INSERT INTO `account` VALUES ('5532','Socios de sociedad escindida.',NULL,0,4);
INSERT INTO `account` VALUES ('5533','Socios, cuenta de escisión.',NULL,0,4);
INSERT INTO `account` VALUES ('554','Cuenta corriente con uniones temporales de empresas y comunidades de bienes.',NULL,0,3);
INSERT INTO `account` VALUES ('5540','Cuenta corriente con uniones temporales de empresas y comunidades de bienes.',NULL,0,4);
INSERT INTO `account` VALUES ('555','Partidas pendientes de aplicación.',NULL,0,3);
INSERT INTO `account` VALUES ('5550','Partidas pendientes de aplicación.',NULL,0,4);
INSERT INTO `account` VALUES ('556','Desembolsos exigidos sobre participaciones en el patrimonio neto.',NULL,0,3);
INSERT INTO `account` VALUES ('5560','Desembolsos exigidos sobre participaciones en el patrimonio neto.',NULL,0,4);
INSERT INTO `account` VALUES ('557','Dividendo activo a cuenta.',NULL,0,3);
INSERT INTO `account` VALUES ('5570','Dividendo activo a cuenta.',NULL,0,4);
INSERT INTO `account` VALUES ('558','Socios por desembolsos exigidos.',NULL,0,3);
INSERT INTO `account` VALUES ('5580','Socios por desembolsos exigidos sobre acciones o participaciones ordinarias.',NULL,0,4);
INSERT INTO `account` VALUES ('5585','Socios por desembolsos exigidos sobre acciones o participaciones consideradas como pasivos financieros.',NULL,0,4);
INSERT INTO `account` VALUES ('559','Derivados financieros a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('5590','Activos por derivados financieros a corto plazo, cartera de negociación.',NULL,0,4);
INSERT INTO `account` VALUES ('5593','Activos de derivados financieros a corto plazo, instrumentos de cobertura.',NULL,0,4);
INSERT INTO `account` VALUES ('5595','Pasivos por derivados financieros a corto plazo, cartera de negociación.',NULL,0,4);
INSERT INTO `account` VALUES ('5598','Pasivos por derivados financieros a corto plazo, instrumentos de cobertura.',NULL,0,4);
INSERT INTO `account` VALUES ('56','FIANZAS Y DEPÓSITOS RECIBIDOS Y CONSTITUIDOS A CORTO PLAZO Y AJUSTES POR PERIODIFICACIÓN.',NULL,0,2);
INSERT INTO `account` VALUES ('560','Fianzas recibidas a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('5600','Fianzas recibidas a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('561','Depósitos recibidos a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('5610','Depósitos recibidos a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('565','Fianzas constituidas a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('5650','Fianzas constituidas a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('566','Depósitos constituidos a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('5660','Depósitos constituidos a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('567','Intereses pagados por anticipado.',NULL,0,3);
INSERT INTO `account` VALUES ('5670','Intereses pagados por anticipado.',NULL,0,4);
INSERT INTO `account` VALUES ('568','Intereses cobrados por anticipado.',NULL,0,3);
INSERT INTO `account` VALUES ('5680','Intereses cobrados por anticipado.',NULL,0,4);
INSERT INTO `account` VALUES ('569','Garantías financieras a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('5690','Garantías financieras a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('57','TESORERÍA.',NULL,0,2);
INSERT INTO `account` VALUES ('570','Caja, euros.',NULL,0,3);
INSERT INTO `account` VALUES ('5700','Caja, euros.',NULL,0,4);
INSERT INTO `account` VALUES ('570000000','Caja, euros.',NULL,1,5);
INSERT INTO `account` VALUES ('571','Caja, moneda extranjera.',NULL,0,3);
INSERT INTO `account` VALUES ('5710','Caja, moneda extranjera.',NULL,0,4);
INSERT INTO `account` VALUES ('572','Bancos e instituciones de crédito c/c vista, euros.',NULL,0,3);
INSERT INTO `account` VALUES ('5720','Bancos e instituciones de crédito c/c vista, euros.',NULL,0,4);
INSERT INTO `account` VALUES ('573','Bancos e instituciones de crédito c/c vista, moneda extranjera.',NULL,0,3);
INSERT INTO `account` VALUES ('5730','Bancos e instituciones de crédito c/c vista, moneda extranjera.',NULL,0,4);
INSERT INTO `account` VALUES ('574','Bancos e instituciones de crédito, cuentas de ahorro, euros.',NULL,0,3);
INSERT INTO `account` VALUES ('5740','Bancos e instituciones de crédito, cuentas de ahorro, euros.',NULL,0,4);
INSERT INTO `account` VALUES ('575','Bancos e instituciones de crédito, cuentas de ahorro, moneda extranjera.',NULL,0,3);
INSERT INTO `account` VALUES ('5750','Bancos e instituciones de crédito, cuentas de ahorro, moneda extranjera.',NULL,0,4);
INSERT INTO `account` VALUES ('576','Inversiones a corto plazo de gran liquidez.',NULL,0,3);
INSERT INTO `account` VALUES ('5760','Inversiones a corto plazo de gran liquidez.',NULL,0,4);
INSERT INTO `account` VALUES ('58','ACTIVOS NO CORRIENTES MANTENIDOS PARA LA VENTA Y ACTIVOS Y PASIVOS ASOCIADOS.',NULL,0,2);
INSERT INTO `account` VALUES ('580','Inmovilizado.',NULL,0,3);
INSERT INTO `account` VALUES ('5800','Inmovilizado.',NULL,0,4);
INSERT INTO `account` VALUES ('581','Inversiones con personas y entidades vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('5810','Inversiones con personas y entidades vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('582','Inversiones financieras.',NULL,0,3);
INSERT INTO `account` VALUES ('5820','Inversiones financieras.',NULL,0,4);
INSERT INTO `account` VALUES ('583','Existencias, deudores comerciales y otras cuentas a cobrar.',NULL,0,3);
INSERT INTO `account` VALUES ('5830','Existencias, deudores comerciales y otras cuentas a cobrar.',NULL,0,4);
INSERT INTO `account` VALUES ('584','Otros activos.',NULL,0,3);
INSERT INTO `account` VALUES ('5840','Otros activos.',NULL,0,4);
INSERT INTO `account` VALUES ('585','Provisiones.',NULL,0,3);
INSERT INTO `account` VALUES ('5850','Provisiones.',NULL,0,4);
INSERT INTO `account` VALUES ('586','Deudas con características especiales.',NULL,0,3);
INSERT INTO `account` VALUES ('5860','Deudas con características especiales.',NULL,0,4);
INSERT INTO `account` VALUES ('587','Deudas con personas y entidades vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('5870','Deudas con personas y entidades vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('588','Acreedores comerciales y otras cuentas a pagar.',NULL,0,3);
INSERT INTO `account` VALUES ('5880','Acreedores comerciales y otras cuentas a pagar.',NULL,0,4);
INSERT INTO `account` VALUES ('589','Otros pasivos.',NULL,0,3);
INSERT INTO `account` VALUES ('5890','Otros pasivos.',NULL,0,4);
INSERT INTO `account` VALUES ('59','DETERIORO DEL VALOR DE INVERSIONES FINANCIERAS A CORTO PLAZO Y DE ACTIVOS NO CORRIENTES MANTENIDOS PARA LA VENTA.',NULL,0,2);
INSERT INTO `account` VALUES ('593','Deterioro de valor de participaciones a corto plazo en partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('5930','Deterioro de valor de participaciones a corto plazo en partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('594','Deterioro del valor de valores representativos de deuda a corto plazo de partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('5940','Deterioro del valor de valores representativos de deuda a corto plazo de partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('595','Deterioro del valor de créditos a corto plazo a partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('5950','Deterioro del valor de créditos a corto plazo a partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('597','Deterioro de valor de valores representativos de deuda a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('5970','Deterioro de valor de valores representativos de deuda a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('598','Deterioro de valor de créditos a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('5980','Deterioro de valor de créditos a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('599','Deterioro de valor de activos no corrientes mantenidos para la venta.',NULL,0,3);
INSERT INTO `account` VALUES ('5990','Deterioro de valor de activos no corrientes mantenidos para la venta.',NULL,0,4);
INSERT INTO `account` VALUES ('6','COMPRAS Y GASTOS',NULL,0,1);
INSERT INTO `account` VALUES ('60','COMPRAS.',NULL,0,2);
INSERT INTO `account` VALUES ('600','Compras de mercaderías.',NULL,0,3);
INSERT INTO `account` VALUES ('6000','Compras de mercaderías.',NULL,0,4);
INSERT INTO `account` VALUES ('600000000','Compras de mercaderías.',NULL,1,5);
INSERT INTO `account` VALUES ('601','Compras de materias primas.',NULL,0,3);
INSERT INTO `account` VALUES ('6010','Compras de materias primas.',NULL,0,4);
INSERT INTO `account` VALUES ('602','Compras de otros aprovisionamientos.',NULL,0,3);
INSERT INTO `account` VALUES ('6020','Compras de otros aprovisionamientos.',NULL,0,4);
INSERT INTO `account` VALUES ('606','Descuentos sobre compras por pronto pago.',NULL,0,3);
INSERT INTO `account` VALUES ('6060','Descuentos sobre compras por pronto pago.',NULL,0,4);
INSERT INTO `account` VALUES ('607','Trabajos realizados por otras empresas.',NULL,0,3);
INSERT INTO `account` VALUES ('6070','Trabajos realizados por otras empresas.',NULL,0,4);
INSERT INTO `account` VALUES ('608','Devoluciones de compras y operaciones similares.',NULL,0,3);
INSERT INTO `account` VALUES ('6080','Devoluciones de compras y operaciones similares.',NULL,0,4);
INSERT INTO `account` VALUES ('609','Rappels por compras.',NULL,0,3);
INSERT INTO `account` VALUES ('6090','Rappels por compras.',NULL,0,4);
INSERT INTO `account` VALUES ('61','VARIACIÓN DE EXISTENCIAS.',NULL,0,2);
INSERT INTO `account` VALUES ('610','Variación de existencias de mercaderías.',NULL,0,3);
INSERT INTO `account` VALUES ('6100','Variación de existencias de mercaderías.',NULL,0,4);
INSERT INTO `account` VALUES ('611','Variación de existencias de materias primas.',NULL,0,3);
INSERT INTO `account` VALUES ('6110','Variación de existencias de materias primas.',NULL,0,4);
INSERT INTO `account` VALUES ('612','Variación de existencias de otros aprovisionamientos.',NULL,0,3);
INSERT INTO `account` VALUES ('6120','Variación de existencias de otros aprovisionamientos.',NULL,0,4);
INSERT INTO `account` VALUES ('62','SERVICIOS EXTERIORES.',NULL,0,2);
INSERT INTO `account` VALUES ('620','Gastos en investigación y desarrollo del ejercicio.',NULL,0,3);
INSERT INTO `account` VALUES ('6200','Gastos en investigación y desarrollo del ejercicio.',NULL,0,4);
INSERT INTO `account` VALUES ('621','Arrendamientos y cánones.',NULL,0,3);
INSERT INTO `account` VALUES ('6210','Arrendamientos y cánones.',NULL,0,4);
INSERT INTO `account` VALUES ('622','Reparaciones y conservación.',NULL,0,3);
INSERT INTO `account` VALUES ('6220','Reparaciones y conservación.',NULL,0,4);
INSERT INTO `account` VALUES ('623','Servicios de profesionales independientes.',NULL,0,3);
INSERT INTO `account` VALUES ('6230','Servicios de profesionales independientes.',NULL,0,4);
INSERT INTO `account` VALUES ('624','Transportes.',NULL,0,3);
INSERT INTO `account` VALUES ('6240','Transportes.',NULL,0,4);
INSERT INTO `account` VALUES ('625','Primas de seguros.',NULL,0,3);
INSERT INTO `account` VALUES ('6250','Primas de seguros.',NULL,0,4);
INSERT INTO `account` VALUES ('626','Servicios bancarios y similares.',NULL,0,3);
INSERT INTO `account` VALUES ('6260','Servicios bancarios y similares.',NULL,0,4);
INSERT INTO `account` VALUES ('627','Publicidad, propaganda y relaciones públicas.',NULL,0,3);
INSERT INTO `account` VALUES ('6270','Publicidad, propaganda y relaciones públicas.',NULL,0,4);
INSERT INTO `account` VALUES ('628','Suministros.',NULL,0,3);
INSERT INTO `account` VALUES ('6280','Suministros.',NULL,0,4);
INSERT INTO `account` VALUES ('629','Otros servicios.',NULL,0,3);
INSERT INTO `account` VALUES ('6290','Otros servicios.',NULL,0,4);
INSERT INTO `account` VALUES ('63','TRIBUTOS.',NULL,0,2);
INSERT INTO `account` VALUES ('630','Impuesto sobre beneficios.',NULL,0,3);
INSERT INTO `account` VALUES ('6300','Impuesto corriente.',NULL,0,4);
INSERT INTO `account` VALUES ('6301','Impuesto diferido.',NULL,0,4);
INSERT INTO `account` VALUES ('631','Otros tributos.',NULL,0,3);
INSERT INTO `account` VALUES ('6310','Otros tributos.',NULL,0,4);
INSERT INTO `account` VALUES ('633','Ajustes negativos en la imposición sobre beneficios.',NULL,0,3);
INSERT INTO `account` VALUES ('6330','Ajustes negativos en la imposición sobre beneficios.',NULL,0,4);
INSERT INTO `account` VALUES ('634','Ajustes negativos en la imposición indirecta.',NULL,0,3);
INSERT INTO `account` VALUES ('6340','Ajustes negativos en la imposición indirecta.',NULL,0,4);
INSERT INTO `account` VALUES ('636','Devolución de impuestos.',NULL,0,3);
INSERT INTO `account` VALUES ('6360','Devolución de impuestos.',NULL,0,4);
INSERT INTO `account` VALUES ('638','Ajustes positivos en la imposición sobre beneficios.',NULL,0,3);
INSERT INTO `account` VALUES ('6380','Ajustes positivos en la imposición sobre beneficios.',NULL,0,4);
INSERT INTO `account` VALUES ('639','Ajustes positivos en la imposición indirecta.',NULL,0,3);
INSERT INTO `account` VALUES ('6390','Ajustes positivos en la imposición indirecta.',NULL,0,4);
INSERT INTO `account` VALUES ('64','GASTOS DE PERSONAL.',NULL,0,2);
INSERT INTO `account` VALUES ('640','Sueldos y salarios.',NULL,0,3);
INSERT INTO `account` VALUES ('6400','Sueldos y salarios.',NULL,0,4);
INSERT INTO `account` VALUES ('640000000','Sueldos y salarios.',NULL,1,5);
INSERT INTO `account` VALUES ('641','Indemnizaciones.',NULL,0,3);
INSERT INTO `account` VALUES ('6410','Indemnizaciones.',NULL,0,4);
INSERT INTO `account` VALUES ('641000000','Indemnizaciones.',NULL,1,5);
INSERT INTO `account` VALUES ('642','Seguridad Social a cargo de la empresa.',NULL,0,3);
INSERT INTO `account` VALUES ('6420','Seguridad Social a cargo de la empresa.',NULL,0,4);
INSERT INTO `account` VALUES ('642000000','Seguridad Social a cargo de la empresa.',NULL,1,5);
INSERT INTO `account` VALUES ('643','Retribuciones a largo plazo mediante sistemas de aportación definida.',NULL,0,3);
INSERT INTO `account` VALUES ('6430','Retribuciones a largo plazo mediante sistemas de aportación definida.',NULL,0,4);
INSERT INTO `account` VALUES ('644','Retribuciones a largo plazo mediante sistemas de prestación definida.',NULL,0,3);
INSERT INTO `account` VALUES ('6440','Contribuciones anuales.',NULL,0,4);
INSERT INTO `account` VALUES ('6442','Otros costes.',NULL,0,4);
INSERT INTO `account` VALUES ('645','Retribuciones al personal mediante instrumentos de patrimonio.',NULL,0,3);
INSERT INTO `account` VALUES ('6450','Retribuciones al personal mediante instrumentos de patrimonio.',NULL,0,4);
INSERT INTO `account` VALUES ('649','Otros gastos sociales.',NULL,0,3);
INSERT INTO `account` VALUES ('6490','Otros gastos sociales.',NULL,0,4);
INSERT INTO `account` VALUES ('65','OTROS GASTOS DE GESTIÓN.',NULL,0,2);
INSERT INTO `account` VALUES ('650','Pérdidas de créditos comerciales incobrables.',NULL,0,3);
INSERT INTO `account` VALUES ('6500','Pérdidas de créditos comerciales incobrables.',NULL,0,4);
INSERT INTO `account` VALUES ('651','Resultados de operaciones en común.',NULL,0,3);
INSERT INTO `account` VALUES ('6510','Beneficio transferido (gestor).',NULL,0,4);
INSERT INTO `account` VALUES ('6511','Pérdida soportada (partícipe o asociado no gestor).',NULL,0,4);
INSERT INTO `account` VALUES ('659','Otras pérdidas en gestión corriente.',NULL,0,3);
INSERT INTO `account` VALUES ('6590','Otras pérdidas en gestión corriente.',NULL,0,4);
INSERT INTO `account` VALUES ('66','GASTOS FINANCIEROS.',NULL,0,2);
INSERT INTO `account` VALUES ('660','Gastos financieros por actualización de provisiones.',NULL,0,3);
INSERT INTO `account` VALUES ('6600','Gastos financieros por actualización de provisiones.',NULL,0,4);
INSERT INTO `account` VALUES ('661','Intereses de obligaciones y bonos.',NULL,0,3);
INSERT INTO `account` VALUES ('6610','Intereses de obligaciones y bonos.',NULL,0,4);
INSERT INTO `account` VALUES ('662','Intereses de deudas.',NULL,0,3);
INSERT INTO `account` VALUES ('6620','Intereses de deudas.',NULL,0,4);
INSERT INTO `account` VALUES ('662000000','Intereses de deudas.',NULL,1,5);
INSERT INTO `account` VALUES ('663','Pérdidas por valoración de instrumentos financieros por su valor razonable.',NULL,0,3);
INSERT INTO `account` VALUES ('6630','Pérdidas de cartera de negociación.',NULL,0,4);
INSERT INTO `account` VALUES ('6631','Pérdidas de designados por la empresa.',NULL,0,4);
INSERT INTO `account` VALUES ('6632','Pérdidas de disponibles para la venta.',NULL,0,4);
INSERT INTO `account` VALUES ('6633','Pérdidas de instrumentos de cobertura.',NULL,0,4);
INSERT INTO `account` VALUES ('664','Dividendos de acciones o participaciones contabilizadas como pasivo.',NULL,0,3);
INSERT INTO `account` VALUES ('6640','Dividendos de acciones o participaciones contabilizadas como pasivo.',NULL,0,4);
INSERT INTO `account` VALUES ('665','Intereses por descuento de efectos y operaciones de factoring.',NULL,0,3);
INSERT INTO `account` VALUES ('6650','Intereses por descuento de efectos y operaciones de factoring.',NULL,0,4);
INSERT INTO `account` VALUES ('666','Pérdidas en participaciones y valores representativos de deuda.',NULL,0,3);
INSERT INTO `account` VALUES ('6660','Pérdidas en participaciones y valores representativos de deuda.',NULL,0,4);
INSERT INTO `account` VALUES ('667','Pérdidas de créditos no comerciales.',NULL,0,3);
INSERT INTO `account` VALUES ('6670','Pérdidas de créditos no comerciales.',NULL,0,4);
INSERT INTO `account` VALUES ('668','Diferencias negativas de cambio.',NULL,0,3);
INSERT INTO `account` VALUES ('6680','Diferencias negativas de cambio.',NULL,0,4);
INSERT INTO `account` VALUES ('669','Otros gastos financieros.',NULL,0,3);
INSERT INTO `account` VALUES ('6690','Otros gastos financieros.',NULL,0,4);
INSERT INTO `account` VALUES ('669000000','Otros gastos financieros.',NULL,1,5);
INSERT INTO `account` VALUES ('67','PÉRDIDAS PROCEDENTES DE ACTIVOS NO CORRIENTES Y GASTOS EXCEPCIONALES.',NULL,0,2);
INSERT INTO `account` VALUES ('670','Pérdidas procedentes del inmovilizado intangible.',NULL,0,3);
INSERT INTO `account` VALUES ('6700','Pérdidas procedentes del inmovilizado intangible.',NULL,0,4);
INSERT INTO `account` VALUES ('671','Pérdidas procedentes del inmovilizado material.',NULL,0,3);
INSERT INTO `account` VALUES ('6710','Pérdidas procedentes del inmovilizado material.',NULL,0,4);
INSERT INTO `account` VALUES ('672','Pérdidas procedentes de las inversiones inmobiliarias.',NULL,0,3);
INSERT INTO `account` VALUES ('6720','Pérdidas procedentes de las inversiones inmobiliarias.',NULL,0,4);
INSERT INTO `account` VALUES ('673','Pérdidas procedentes de participaciones a largo plazo en partes vinculadas.',NULL,0,3);
INSERT INTO `account` VALUES ('6730','Pérdidas procedentes de participaciones a largo plazo en partes vinculadas.',NULL,0,4);
INSERT INTO `account` VALUES ('675','Pérdidas por operaciones con obligaciones propias.',NULL,0,3);
INSERT INTO `account` VALUES ('6750','Pérdidas por operaciones con obligaciones propias.',NULL,0,4);
INSERT INTO `account` VALUES ('678','Gastos excepcionales.',NULL,0,3);
INSERT INTO `account` VALUES ('6780','Gastos excepcionales.',NULL,0,4);
INSERT INTO `account` VALUES ('68','DOTACIONES PARA AMORTIZACIONES.',NULL,0,2);
INSERT INTO `account` VALUES ('680','Amortización del inmovilizado intangible.',NULL,0,3);
INSERT INTO `account` VALUES ('6800','Amortización del inmovilizado intangible.',NULL,0,4);
INSERT INTO `account` VALUES ('6801','Amortización de investigación',NULL,0,4);
INSERT INTO `account` VALUES ('6802','Amortización de desarrollo',NULL,0,4);
INSERT INTO `account` VALUES ('6803','Amortización de concesiones administrativas',NULL,0,4);
INSERT INTO `account` VALUES ('6804','Amortización de propiedad industrial',NULL,0,4);
INSERT INTO `account` VALUES ('6805','Amortización de derechos de traspaso',NULL,0,4);
INSERT INTO `account` VALUES ('6806','Amortización de aplicaciones informáticas',NULL,0,4);
INSERT INTO `account` VALUES ('681','Amortización del inmovilizado material.',NULL,0,3);
INSERT INTO `account` VALUES ('6810','Amortización del inmovilizado material.',NULL,0,4);
INSERT INTO `account` VALUES ('6811','Amortización de construcciones',NULL,0,4);
INSERT INTO `account` VALUES ('6812','Amortización de instalaciones técnicas',NULL,0,4);
INSERT INTO `account` VALUES ('6813','Amortización de maquinaria',NULL,0,4);
INSERT INTO `account` VALUES ('6814','Amortización de utillaje',NULL,0,4);
INSERT INTO `account` VALUES ('6815','Amortización de otras instalaciones',NULL,0,4);
INSERT INTO `account` VALUES ('6816','Amortización de mobiliario',NULL,0,4);
INSERT INTO `account` VALUES ('6817','Amortización de equipos para el proceso de información',NULL,0,4);
INSERT INTO `account` VALUES ('6818','Amortización de elementos de transporte',NULL,0,4);
INSERT INTO `account` VALUES ('6819','Amortización de otro inmovilizado material',NULL,0,4);
INSERT INTO `account` VALUES ('682','Amortización de las inversiones inmobiliarias.',NULL,0,3);
INSERT INTO `account` VALUES ('6820','Amortización de las inversiones inmobiliarias.',NULL,0,4);
INSERT INTO `account` VALUES ('69','PÉRDIDAS POR DETERIORO Y OTRAS DOTACIONES.',NULL,0,2);
INSERT INTO `account` VALUES ('690','Pérdidas por deterioro del inmovilizado intangible.',NULL,0,3);
INSERT INTO `account` VALUES ('6900','Pérdidas por deterioro del inmovilizado intangible.',NULL,0,4);
INSERT INTO `account` VALUES ('691','Pérdidas por deterioro del inmovilizado material.',NULL,0,3);
INSERT INTO `account` VALUES ('6910','Pérdidas por deterioro del inmovilizado material.',NULL,0,4);
INSERT INTO `account` VALUES ('692','Pérdidas por deterioro de las inversiones inmobiliarias.',NULL,0,3);
INSERT INTO `account` VALUES ('6920','Pérdidas por deterioro de las inversiones inmobiliarias.',NULL,0,4);
INSERT INTO `account` VALUES ('693','Pérdidas por deterioro de existencias.',NULL,0,3);
INSERT INTO `account` VALUES ('6930','Pérdidas por deterioro de existencias.',NULL,0,4);
INSERT INTO `account` VALUES ('694','Pérdidas por deterioro de créditos comerciales.',NULL,0,3);
INSERT INTO `account` VALUES ('6940','Pérdidas por deterioro de créditos comerciales.',NULL,0,4);
INSERT INTO `account` VALUES ('695','Dotación a la provisión por operaciones comerciales.',NULL,0,3);
INSERT INTO `account` VALUES ('6954','Dotación a la provisión por contratos onerosos.',NULL,0,4);
INSERT INTO `account` VALUES ('6959','Dotación a la provisión para otras operaciones comerciales.',NULL,0,4);
INSERT INTO `account` VALUES ('696','Pérdidas por deterioro de participaciones y valores representativos de deuda a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('6960','Pérdidas por deterioro de participaciones y valores representativos de deuda a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('697','Pérdidas por deterioro de créditos a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('6970','Pérdidas por deterioro de créditos a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('698','Pérdidas por deterioro de participaciones y valores representativos de deuda a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('6980','Pérdidas por deterioro de participaciones y valores representativos de deuda a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('699','Pérdidas por deterioro de créditos a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('6990','Pérdidas por deterioro de créditos a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('7','VENTAS E INGRESOS',NULL,0,1);
INSERT INTO `account` VALUES ('70','VENTAS DE MERCADERÍAS, DE PRODUCCIÓN PROPIA, DE SERVICIOS, ETC.',NULL,0,2);
INSERT INTO `account` VALUES ('700','Ventas de mercaderías.',NULL,0,3);
INSERT INTO `account` VALUES ('7000','Ventas de mercaderías.',NULL,0,4);
INSERT INTO `account` VALUES ('700000000','Ventas de mercaderías.',NULL,1,5);
INSERT INTO `account` VALUES ('701','Ventas de productos terminados.',NULL,0,3);
INSERT INTO `account` VALUES ('7010','Ventas de productos terminados.',NULL,0,4);
INSERT INTO `account` VALUES ('702','Ventas de productos semiterminados.',NULL,0,3);
INSERT INTO `account` VALUES ('7020','Ventas de productos semiterminados.',NULL,0,4);
INSERT INTO `account` VALUES ('703','Ventas de subproductos y residuos.',NULL,0,3);
INSERT INTO `account` VALUES ('7030','Ventas de subproductos y residuos.',NULL,0,4);
INSERT INTO `account` VALUES ('704','Ventas de envases y embalajes.',NULL,0,3);
INSERT INTO `account` VALUES ('7040','Ventas de envases y embalajes.',NULL,0,4);
INSERT INTO `account` VALUES ('705','Prestación de servicios.',NULL,0,3);
INSERT INTO `account` VALUES ('7050','Prestación de servicios.',NULL,0,4);
INSERT INTO `account` VALUES ('706','Descuentos sobre ventas por pronto pago.',NULL,0,3);
INSERT INTO `account` VALUES ('7060','Descuentos sobre ventas por pronto pago.',NULL,0,4);
INSERT INTO `account` VALUES ('708','Devoluciones de ventas y operaciones similares.',NULL,0,3);
INSERT INTO `account` VALUES ('7080','Devoluciones de ventas y operaciones similares.',NULL,0,4);
INSERT INTO `account` VALUES ('709','Rappels sobre ventas.',NULL,0,3);
INSERT INTO `account` VALUES ('7090','Rappels sobre ventas.',NULL,0,4);
INSERT INTO `account` VALUES ('71','VARIACIÓN DE EXISTENCIAS.',NULL,0,2);
INSERT INTO `account` VALUES ('710','Variación de existencias de productos en curso.',NULL,0,3);
INSERT INTO `account` VALUES ('7100','Variación de existencias de productos en curso.',NULL,0,4);
INSERT INTO `account` VALUES ('711','Variación de existencias de productos semiterminados.',NULL,0,3);
INSERT INTO `account` VALUES ('7110','Variación de existencias de productos semiterminados.',NULL,0,4);
INSERT INTO `account` VALUES ('712','Variación de existencias de productos terminados.',NULL,0,3);
INSERT INTO `account` VALUES ('7120','Variación de existencias de productos terminados.',NULL,0,4);
INSERT INTO `account` VALUES ('713','Variación de existencias de subproductos, residuos y materiales recuperados.',NULL,0,3);
INSERT INTO `account` VALUES ('7130','Variación de existencias de subproductos, residuos y materiales recuperados.',NULL,0,4);
INSERT INTO `account` VALUES ('73','TRABAJOS REALIZADOS PARA LA EMPRESA.',NULL,0,2);
INSERT INTO `account` VALUES ('730','Trabajos realizados para el inmovilizado intangible.',NULL,0,3);
INSERT INTO `account` VALUES ('7300','Trabajos realizados para el inmovilizado intangible.',NULL,0,4);
INSERT INTO `account` VALUES ('731','Trabajos realizados para el inmovilizado material.',NULL,0,3);
INSERT INTO `account` VALUES ('7310','Trabajos realizados para el inmovilizado material.',NULL,0,4);
INSERT INTO `account` VALUES ('732','Trabajos realizados en inversiones inmobiliarias.',NULL,0,3);
INSERT INTO `account` VALUES ('7320','Trabajos realizados en inversiones inmobiliarias.',NULL,0,4);
INSERT INTO `account` VALUES ('733','Trabajos realizados para el inmovilizado en curso.',NULL,0,3);
INSERT INTO `account` VALUES ('7330','Trabajos realizados para el inmovilizado en curso.',NULL,0,4);
INSERT INTO `account` VALUES ('74','SUBVENCIONES, DONACIONES Y LEGADOS.',NULL,0,2);
INSERT INTO `account` VALUES ('740','Subvenciones, donaciones y legados a la explotación.',NULL,0,3);
INSERT INTO `account` VALUES ('7400','Subvenciones, donaciones y legados a la explotación.',NULL,0,4);
INSERT INTO `account` VALUES ('746','Subvenciones, donaciones y legados de capital transferidos al resultado del ejercicio.',NULL,0,3);
INSERT INTO `account` VALUES ('7460','Subvenciones, donaciones y legados de capital transferidos al resultado del ejercicio.',NULL,0,4);
INSERT INTO `account` VALUES ('747','Otras subvenciones, donaciones y legados transferidos al resultado del ejercicio.',NULL,0,3);
INSERT INTO `account` VALUES ('7470','Otras subvenciones, donaciones y legados transferidos al resultado del ejercicio.',NULL,0,4);
INSERT INTO `account` VALUES ('75','OTROS INGRESOS DE GESTIÓN.',NULL,0,2);
INSERT INTO `account` VALUES ('751','Resultados de operaciones en común.',NULL,0,3);
INSERT INTO `account` VALUES ('7510','Pérdida transferida (gestor).',NULL,0,4);
INSERT INTO `account` VALUES ('7511','Beneficio atribuido (partícipe o asociado no gestor).',NULL,0,4);
INSERT INTO `account` VALUES ('752','Ingresos por arrendamientos.',NULL,0,3);
INSERT INTO `account` VALUES ('7520','Ingresos por arrendamientos.',NULL,0,4);
INSERT INTO `account` VALUES ('753','Ingresos de propiedad industrial cedida en explotación.',NULL,0,3);
INSERT INTO `account` VALUES ('7530','Ingresos de propiedad industrial cedida en explotación.',NULL,0,4);
INSERT INTO `account` VALUES ('754','Ingresos por comisiones.',NULL,0,3);
INSERT INTO `account` VALUES ('7540','Ingresos por comisiones.',NULL,0,4);
INSERT INTO `account` VALUES ('755','Ingresos por servicios al personal.',NULL,0,3);
INSERT INTO `account` VALUES ('7550','Ingresos por servicios al personal.',NULL,0,4);
INSERT INTO `account` VALUES ('759','Ingresos por servicios diversos.',NULL,0,3);
INSERT INTO `account` VALUES ('7590','Ingresos por servicios diversos.',NULL,0,4);
INSERT INTO `account` VALUES ('76','INGRESOS FINANCIEROS.',NULL,0,2);
INSERT INTO `account` VALUES ('760','Ingresos de participaciones en instrumentos de patrimonio.',NULL,0,3);
INSERT INTO `account` VALUES ('7600','Ingresos de participaciones en instrumentos de patrimonio.',NULL,0,4);
INSERT INTO `account` VALUES ('761','Ingresos de valores representativos de deuda.',NULL,0,3);
INSERT INTO `account` VALUES ('7610','Ingresos de valores representativos de deuda.',NULL,0,4);
INSERT INTO `account` VALUES ('762','Ingresos de créditos.',NULL,0,3);
INSERT INTO `account` VALUES ('7620','Ingresos de créditos.',NULL,0,4);
INSERT INTO `account` VALUES ('763','Beneficios por valoración de instrumentos financieros por su valor razonable.',NULL,0,3);
INSERT INTO `account` VALUES ('7630','Beneficios de cartera de negociación.',NULL,0,4);
INSERT INTO `account` VALUES ('7631','Beneficios de designados por la empresa.',NULL,0,4);
INSERT INTO `account` VALUES ('7632','Beneficios de disponibles para la venta.',NULL,0,4);
INSERT INTO `account` VALUES ('7633','Beneficios de instrumentos de cobertura.',NULL,0,4);
INSERT INTO `account` VALUES ('766','Beneficios en participaciones y valores representativos de deuda.',NULL,0,3);
INSERT INTO `account` VALUES ('7660','Beneficios en participaciones y valores representativos de deuda.',NULL,0,4);
INSERT INTO `account` VALUES ('767','Ingresos de activos afectos y de derechos de reembolso relativos a retribuciones a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('7670','Ingresos de activos afectos y de derechos de reembolso relativos a retribuciones a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('768','Diferencias positivas de cambio.',NULL,0,3);
INSERT INTO `account` VALUES ('7680','Diferencias positivas de cambio.',NULL,0,4);
INSERT INTO `account` VALUES ('769','Otros ingresos financieros.',NULL,0,3);
INSERT INTO `account` VALUES ('7690','Otros ingresos financieros.',NULL,0,4);
INSERT INTO `account` VALUES ('77','BENEFICIOS PROCEDENTES DE ACTIVOS NO CORRIENTES E INGRESOS EXCEPCIONALES.',NULL,0,2);
INSERT INTO `account` VALUES ('770','Beneficios procedentes del inmovilizado intangible.',NULL,0,3);
INSERT INTO `account` VALUES ('7700','Beneficios procedentes del inmovilizado intangible.',NULL,0,4);
INSERT INTO `account` VALUES ('771','Beneficios procedentes del inmovilizado material.',NULL,0,3);
INSERT INTO `account` VALUES ('7710','Beneficios procedentes del inmovilizado material.',NULL,0,4);
INSERT INTO `account` VALUES ('772','Beneficios procedentes de las inversiones inmobiliarias.',NULL,0,3);
INSERT INTO `account` VALUES ('7720','Beneficios procedentes de las inversiones inmobiliarias.',NULL,0,4);
INSERT INTO `account` VALUES ('773','Beneficios procedentes de participaciones..',NULL,0,3);
INSERT INTO `account` VALUES ('7730','Beneficios procedentes de participaciones..',NULL,0,4);
INSERT INTO `account` VALUES ('774','Diferencia negativa en combinaciones de negocios.',NULL,0,3);
INSERT INTO `account` VALUES ('7740','Diferencia negativa en combinaciones de negocios.',NULL,0,4);
INSERT INTO `account` VALUES ('775','Beneficios por operaciones con obligaciones propias.',NULL,0,3);
INSERT INTO `account` VALUES ('7750','Beneficios por operaciones con obligaciones propias.',NULL,0,4);
INSERT INTO `account` VALUES ('778','Ingresos excepcionales.',NULL,0,3);
INSERT INTO `account` VALUES ('7780','Ingresos excepcionales.',NULL,0,4);
INSERT INTO `account` VALUES ('79','EXCESOS Y APLICACIONES DE PROVISIONES Y DE PÉRDIDAS POR DETERIORO.',NULL,0,2);
INSERT INTO `account` VALUES ('790','Reversión del deterioro del inmovilizado intangible.',NULL,0,3);
INSERT INTO `account` VALUES ('7900','Reversión del deterioro del inmovilizado intangible.',NULL,0,4);
INSERT INTO `account` VALUES ('791','Reversión del deterioro del inmovilizado material.',NULL,0,3);
INSERT INTO `account` VALUES ('7910','Reversión del deterioro del inmovilizado material.',NULL,0,4);
INSERT INTO `account` VALUES ('792','Reversión del deterioro de las inversiones inmobiliarias.',NULL,0,3);
INSERT INTO `account` VALUES ('7920','Reversión del deterioro de las inversiones inmobiliarias.',NULL,0,4);
INSERT INTO `account` VALUES ('793','Reversión del deterioro de existencias.',NULL,0,3);
INSERT INTO `account` VALUES ('7930','Reversión del deterioro de existencias.',NULL,0,4);
INSERT INTO `account` VALUES ('794','Reversión del deterioro de créditos por operaciones comerciales.',NULL,0,3);
INSERT INTO `account` VALUES ('7940','Reversión del deterioro de créditos por operaciones comerciales.',NULL,0,4);
INSERT INTO `account` VALUES ('795','Exceso de provisiones.',NULL,0,3);
INSERT INTO `account` VALUES ('7950','Exceso de provisiones.',NULL,0,4);
INSERT INTO `account` VALUES ('796','Reversión del deterioro de participaciones y valores representativos de deuda a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('7960','Reversión del deterioro de participaciones y valores representativos de deuda a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('797','Reversión del deterioro de créditos a largo plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('7970','Reversión del deterioro de créditos a largo plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('798','Reversión del deterioro de participaciones y valores representativos de deuda a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('7980','Reversión del deterioro de participaciones y valores representativos de deuda a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('799','Reversión del deterioro de créditos a corto plazo.',NULL,0,3);
INSERT INTO `account` VALUES ('7990','Reversión del deterioro de créditos a corto plazo.',NULL,0,4);
INSERT INTO `account` VALUES ('8','GASTOS IMPUTADOS AL PATRIMONIO NETO',NULL,0,1);
INSERT INTO `account` VALUES ('80','GASTOS FINANCIEROS POR VALORACIÓN DE ACTIVOS FINANCIEROS.',NULL,0,2);
INSERT INTO `account` VALUES ('800','Pérdidas en activos financieros disponibles para la venta.',NULL,0,3);
INSERT INTO `account` VALUES ('8000','Pérdidas en activos financieros disponibles para la venta.',NULL,0,4);
INSERT INTO `account` VALUES ('802','Transferencia de beneficios en activos financieros disponibles para la venta.',NULL,0,3);
INSERT INTO `account` VALUES ('8020','Transferencia de beneficios en activos financieros disponibles para la venta.',NULL,0,4);
INSERT INTO `account` VALUES ('81','GASTOS EN OPERACIONES DE COBERTURA.',NULL,0,2);
INSERT INTO `account` VALUES ('810','Pérdidas por coberturas de flujos de efectivo.',NULL,0,3);
INSERT INTO `account` VALUES ('8100','Pérdidas por coberturas de flujos de efectivo.',NULL,0,4);
INSERT INTO `account` VALUES ('811','Pérdidas por coberturas de inversiones netas en un negocio en el extranjero.',NULL,0,3);
INSERT INTO `account` VALUES ('8110','Pérdidas por coberturas de inversiones netas en un negocio en el extranjero.',NULL,0,4);
INSERT INTO `account` VALUES ('812','Transferencia de beneficios por coberturas de flujos de efectivo.',NULL,0,3);
INSERT INTO `account` VALUES ('8120','Transferencia de beneficios por coberturas de flujos de efectivo.',NULL,0,4);
INSERT INTO `account` VALUES ('813','Transferencia de beneficios por coberturas de inversiones netas en un negocio en el extranjero.',NULL,0,3);
INSERT INTO `account` VALUES ('8130','Transferencia de beneficios por coberturas de inversiones netas en un negocio en el extranjero.',NULL,0,4);
INSERT INTO `account` VALUES ('82','GASTOS POR DIFERENCIAS EN CONVERSIÓN.',NULL,0,2);
INSERT INTO `account` VALUES ('820','Diferencias de conversión negativas.',NULL,0,3);
INSERT INTO `account` VALUES ('8200','Diferencias de conversión negativas.',NULL,0,4);
INSERT INTO `account` VALUES ('821','Transferencia de diferencias de conversión positivas.',NULL,0,3);
INSERT INTO `account` VALUES ('8210','Transferencia de diferencias de conversión positivas.',NULL,0,4);
INSERT INTO `account` VALUES ('83','IMPUESTOS SOBRE BENEFICIOS.',NULL,0,2);
INSERT INTO `account` VALUES ('830','Impuestos sobre beneficios.',NULL,0,3);
INSERT INTO `account` VALUES ('8300','Impuesto corriente.',NULL,0,4);
INSERT INTO `account` VALUES ('8301','Impuesto diferido.',NULL,0,4);
INSERT INTO `account` VALUES ('833','Ajustes negativos en la imposición sobre beneficios.',NULL,0,3);
INSERT INTO `account` VALUES ('8330','Ajustes negativos en la imposición sobre beneficios.',NULL,0,4);
INSERT INTO `account` VALUES ('834','Ingresos fiscales por diferencias permanentes.',NULL,0,3);
INSERT INTO `account` VALUES ('8340','Ingresos fiscales por diferencias permanentes.',NULL,0,4);
INSERT INTO `account` VALUES ('835','Ingresos fiscales por deducciones y bonificaciones.',NULL,0,3);
INSERT INTO `account` VALUES ('8350','Ingresos fiscales por deducciones y bonificaciones.',NULL,0,4);
INSERT INTO `account` VALUES ('836','Transferencia de diferencias permanentes.',NULL,0,3);
INSERT INTO `account` VALUES ('8360','Transferencia de diferencias permanentes.',NULL,0,4);
INSERT INTO `account` VALUES ('837','Transferencia de deducciones y bonificaciones.',NULL,0,3);
INSERT INTO `account` VALUES ('8370','Transferencia de deducciones y bonificaciones.',NULL,0,4);
INSERT INTO `account` VALUES ('838','Ajustes positivos en la imposición sobre beneficios.',NULL,0,3);
INSERT INTO `account` VALUES ('8380','Ajustes positivos en la imposición sobre beneficios.',NULL,0,4);
INSERT INTO `account` VALUES ('84','TRANSFERENCIAS DE SUBVENCIONES, DONACIONES Y LEGADOS.',NULL,0,2);
INSERT INTO `account` VALUES ('840','Transferencia de subvenciones oficiales de capital.',NULL,0,3);
INSERT INTO `account` VALUES ('8400','Transferencia de subvenciones oficiales de capital.',NULL,0,4);
INSERT INTO `account` VALUES ('841','Transferencia de donaciones y legados de capital.',NULL,0,3);
INSERT INTO `account` VALUES ('8410','Transferencia de donaciones y legados de capital.',NULL,0,4);
INSERT INTO `account` VALUES ('842','Transferencia de otras subvenciones, donaciones y legados.',NULL,0,3);
INSERT INTO `account` VALUES ('8420','Transferencia de otras subvenciones, donaciones y legados.',NULL,0,4);
INSERT INTO `account` VALUES ('85','GASTOS POR PÉRDIDAS ACTUARIALES Y AJUSTES EN LOS ACTIVOS POR RETRIBUCIONES A LARGO PLAZO DE PRESTACIÓN DEFINIDA.',NULL,0,2);
INSERT INTO `account` VALUES ('850','Pérdidas actuariales.',NULL,0,3);
INSERT INTO `account` VALUES ('8500','Pérdidas actuariales.',NULL,0,4);
INSERT INTO `account` VALUES ('851','Ajustes negativos en activos por retribuciones a largo plazo de prestación definida.',NULL,0,3);
INSERT INTO `account` VALUES ('8510','Ajustes negativos en activos por retribuciones a largo plazo de prestación definida.',NULL,0,4);
INSERT INTO `account` VALUES ('86','GASTOS POR ACTIVOS NO CORRIENTES EN VENTA.',NULL,0,2);
INSERT INTO `account` VALUES ('860','Pérdidas en activos no corrientes y grupos enajenables de elementos mantenidos para la venta.',NULL,0,3);
INSERT INTO `account` VALUES ('8600','Pérdidas en activos no corrientes y grupos enajenables de elementos mantenidos para la venta.',NULL,0,4);
INSERT INTO `account` VALUES ('862','Transferencia de beneficios en activos no corrientes y grupos enajenables de elementos mantenidos para la venta.',NULL,0,3);
INSERT INTO `account` VALUES ('8620','Transferencia de beneficios en activos no corrientes y grupos enajenables de elementos mantenidos para la venta.',NULL,0,4);
INSERT INTO `account` VALUES ('89','GASTOS DE PARTICIPACIONES EN EMPRESAS DEL GRUPO O ASOCIADAS CON AJUSTES VALORATIVOS POSITIVOS PREVIOS.',NULL,0,2);
INSERT INTO `account` VALUES ('891','Deterioro de participaciones en el patrimonio y valores representativos de deuda de empresas del grupo.',NULL,0,3);
INSERT INTO `account` VALUES ('8910','Deterioro de participaciones en el patrimonio y valores representativos de deuda de empresas del grupo.',NULL,0,4);
INSERT INTO `account` VALUES ('892','Deterioro de participaciones en el patrimonio y valores representativos de deuda de empresas asociadas.',NULL,0,3);
INSERT INTO `account` VALUES ('8920','Deterioro de participaciones en el patrimonio y valores representativos de deuda de empresas asociadas.',NULL,0,4);
INSERT INTO `account` VALUES ('9','INGRESOS IMPUTADOS AL PATRIMONIO NETO',NULL,0,1);
INSERT INTO `account` VALUES ('90','INGRESOS FINANCIEROS POR VALORACIÓN DE ACTIVOS FINANCIEROS.',NULL,0,2);
INSERT INTO `account` VALUES ('900','Beneficios en activos financieros disponibles para la venta.',NULL,0,3);
INSERT INTO `account` VALUES ('9000','Beneficios en activos financieros disponibles para la venta.',NULL,0,4);
INSERT INTO `account` VALUES ('902','Transferencia de pérdidas de activos financieros disponibles para la venta.',NULL,0,3);
INSERT INTO `account` VALUES ('9020','Transferencia de pérdidas de activos financieros disponibles para la venta.',NULL,0,4);
INSERT INTO `account` VALUES ('91','INGRESOS EN OPERACIONES DE COBERTURA.',NULL,0,2);
INSERT INTO `account` VALUES ('910','Beneficios por coberturas de flujos de efectivo.',NULL,0,3);
INSERT INTO `account` VALUES ('9100','Beneficios por coberturas de flujos de efectivo.',NULL,0,4);
INSERT INTO `account` VALUES ('911','Beneficios por coberturas de una inversión neta en un negocio en el extranjero.',NULL,0,3);
INSERT INTO `account` VALUES ('9110','Beneficios por coberturas de una inversión neta en un negocio en el extranjero.',NULL,0,4);
INSERT INTO `account` VALUES ('912','Transferencia de pérdidas por coberturas de flujos de efectivo.',NULL,0,3);
INSERT INTO `account` VALUES ('9120','Transferencia de pérdidas por coberturas de flujos de efectivo.',NULL,0,4);
INSERT INTO `account` VALUES ('913','Transferencia de pérdidas por coberturas de una inversión neta en un negocio en el extranjero.',NULL,0,3);
INSERT INTO `account` VALUES ('9130','Transferencia de pérdidas por coberturas de una inversión neta en un negocio en el extranjero.',NULL,0,4);
INSERT INTO `account` VALUES ('92','INGRESOS POR DIFERENCIAS DE CONVERSIÓN.',NULL,0,2);
INSERT INTO `account` VALUES ('920','Diferencias de conversión positivas.',NULL,0,3);
INSERT INTO `account` VALUES ('9200','Diferencias de conversión positivas.',NULL,0,4);
INSERT INTO `account` VALUES ('921','Transferencia de diferencias de conversión negativas.',NULL,0,3);
INSERT INTO `account` VALUES ('9210','Transferencia de diferencias de conversión negativas.',NULL,0,4);
INSERT INTO `account` VALUES ('94','INGRESOS POR SUBVENCIONES, DONACIONES Y LEGADOS.',NULL,0,2);
INSERT INTO `account` VALUES ('940','Ingresos de subvenciones oficiales de capital.',NULL,0,3);
INSERT INTO `account` VALUES ('9400','Ingresos de subvenciones oficiales de capital.',NULL,0,4);
INSERT INTO `account` VALUES ('941','Ingresos de donaciones y legados de capital.',NULL,0,3);
INSERT INTO `account` VALUES ('9410','Ingresos de donaciones y legados de capital.',NULL,0,4);
INSERT INTO `account` VALUES ('942','Ingresos de otras subvenciones, donaciones y legados.',NULL,0,3);
INSERT INTO `account` VALUES ('9420','Ingresos de otras subvenciones, donaciones y legados.',NULL,0,4);
INSERT INTO `account` VALUES ('95','INGRESOS POR GANANCIAS ACTUARIALES Y AJUSTES EN LOS ACTIVOS POR RETRIBUCIONES A LARGO PLAZO DE PRESTACIÓN DEFINIDA.',NULL,0,2);
INSERT INTO `account` VALUES ('950','Ganancias actuariales.',NULL,0,3);
INSERT INTO `account` VALUES ('9500','Ganancias actuariales.',NULL,0,4);
INSERT INTO `account` VALUES ('951','Ajustes positivos en activos por retribuciones a largo plazo de prestación definida.',NULL,0,3);
INSERT INTO `account` VALUES ('9510','Ajustes positivos en activos por retribuciones a largo plazo de prestación definida.',NULL,0,4);
INSERT INTO `account` VALUES ('96','INGRESOS POR ACTIVOS NO CORRIENTES EN VENTA.',NULL,0,2);
INSERT INTO `account` VALUES ('960','Beneficios en activos no corrientes y grupos enajenables de elementos mantenidos para la venta.',NULL,0,3);
INSERT INTO `account` VALUES ('9600','Beneficios en activos no corrientes y grupos enajenables de elementos mantenidos para la venta.',NULL,0,4);
INSERT INTO `account` VALUES ('962','Transferencia de pérdidas en activos no corrientes y grupos enajenables de elementos mantenidos para la venta.',NULL,0,3);
INSERT INTO `account` VALUES ('9620','Transferencia de pérdidas en activos no corrientes y grupos enajenables de elementos mantenidos para la venta.',NULL,0,4);
INSERT INTO `account` VALUES ('99','INGRESOS DE PARTICIPACIONES EN EL PATRIMONIO DE EMPRESAS DEL GRUPO O ASOCIADAS CON AJUSTES VALORATIVOS NEGATIVOS PREVIOS.',NULL,0,2);
INSERT INTO `account` VALUES ('991','Recuperación de ajustes valorativos negativos previos, empresas del grupo.',NULL,0,3);
INSERT INTO `account` VALUES ('9910','Recuperación de ajustes valorativos negativos previos, empresas del grupo.',NULL,0,4);
INSERT INTO `account` VALUES ('992','Recuperación de ajustes valorativos negativos previos, empresas asociadas.',NULL,0,3);
INSERT INTO `account` VALUES ('9920','Recuperación de ajustes valorativos negativos previos, empresas asociadas.',NULL,0,4);
INSERT INTO `account` VALUES ('993','Transferencia por deterioro de ajustes valorativos negativos previos, empresas del grupo.',NULL,0,3);
INSERT INTO `account` VALUES ('9930','Transferencia por deterioro de ajustes valorativos negativos previos, empresas del grupo.',NULL,0,4);
INSERT INTO `account` VALUES ('994','Transferencia por deterioro de ajustes valorativos negativos previos, empresas asociadas.',NULL,0,3);
INSERT INTO `account` VALUES ('9940','Transferencia por deterioro de ajustes valorativos negativos previos, empresas asociadas.',NULL,0,4);

#
# Dumping data for table `account_budget`
#


#
# Dumping data for table `account_budget_detail`
#


#
# Dumping data for table `account_entry`
#


#
# Dumping data for table `account_entry_bank_statement`
#


#
# Dumping data for table `account_entry_detail`
#


#
# Dumping data for table `account_entry_fbatch`
#


#
# Dumping data for table `account_entry_finance_tracking`
#


#
# Dumping data for table `account_entry_invoice`
#


#
# Dumping data for table `account_entry_link`
#


#
# Dumping data for table `account_helper`
#


#
# Dumping data for table `account_period`
#


#
# Dumping data for table `account_summary`
#


#
# Dumping data for table `action`
#


#
# Dumping data for table `action_denied`
#


#
# Dumping data for table `action_entry`
#


#
# Dumping data for table `action_favorite`
#


#
# Dumping data for table `activity`
#


#
# Dumping data for table `activity_process`
#


#
# Dumping data for table `activity_type`
#


#
# Dumping data for table `agreement`
#


#
# Dumping data for table `agreement_level`
#


#
# Dumping data for table `agreement_level_category`
#


#
# Dumping data for table `agreement_level_payment`
#


#
# Dumping data for table `alarm`
#


#
# Dumping data for table `alumn_loan`
#


#
# Dumping data for table `amortization`
#


#
# Dumping data for table `amortization_detail`
#


#
# Dumping data for table `amortization_type`
#


#
# Dumping data for table `annual_report`
#


#
# Dumping data for table `annual_report_detail`
#


#
# Dumping data for table `app_param`
#

INSERT INTO `app_param` VALUES ('ACC_DEFAULT_CASH_ACC','570000000');
INSERT INTO `app_param` VALUES ('ACC_DEFAULT_CHARGED_RET_ACC','475100000');
INSERT INTO `app_param` VALUES ('ACC_DEFAULT_CHARGED_VAT_ACC','477000000');
INSERT INTO `app_param` VALUES ('ACC_DEFAULT_COMPANY_SOC_INS_ACC','642000000');
INSERT INTO `app_param` VALUES ('ACC_DEFAULT_DEBT_INTEREST_ACC','662000000');
INSERT INTO `app_param` VALUES ('ACC_DEFAULT_FINAN_EXPENSES_ACC','669000000');
INSERT INTO `app_param` VALUES ('ACC_DEFAULT_PAID_RET_ACC','473000000');
INSERT INTO `app_param` VALUES ('ACC_DEFAULT_PAID_VAT_ACC','472000000');
INSERT INTO `app_param` VALUES ('ACC_DEFAULT_PENDING_SALARY_ACC','465000000');
INSERT INTO `app_param` VALUES ('ACC_DEFAULT_PURCHASE_ACC','600000000');
INSERT INTO `app_param` VALUES ('ACC_DEFAULT_SALARY_ACC','640000000');
INSERT INTO `app_param` VALUES ('ACC_DEFAULT_SALES_ACC','700000000');
INSERT INTO `app_param` VALUES ('ACC_DEFAULT_SOCIAL_INSURANCE_ACC','476000000');
INSERT INTO `app_param` VALUES ('APP_PRINT_HEADER_PARAM','false');
INSERT INTO `app_param` VALUES ('APP_PRINT_RECORD_DATA_PARAM','false');

#
# Dumping data for table `application`
#


#
# Dumping data for table `appraiser`
#


#
# Dumping data for table `asset`
#


#
# Dumping data for table `asset_activity`
#


#
# Dumping data for table `auto_concept`
#


#
# Dumping data for table `balance`
#

INSERT INTO `balance` VALUES (1,'BALANCE DE SITUACIÓN',0,0);
INSERT INTO `balance` VALUES (2,'CUENTA DE EXPLOTACIÓN',0,1);
INSERT INTO `balance` VALUES (3,'BALANCE DE SITUACIÓN (ABREVIADO)',0,0);
INSERT INTO `balance` VALUES (4,'CUENTA DE EXPLOTACIÓN (ABREVIADA)',0,1);
INSERT INTO `balance` VALUES (5,'ESTADO DE CAMBIOS EN EL PATRIMONIO NETO (ABREVIADO)',0,2);

#
# Dumping data for table `balance_detail`
#

INSERT INTO `balance_detail` VALUES (1,1,'A0','ACTIVO','',0,1,0,1,0,0);
INSERT INTO `balance_detail` VALUES (2,1,'A','A) ACTIVO NO CORRIENTE','A.I,A.II,A.III,A.IV,A.V,A.VI',1,1,1,1,0,0);
INSERT INTO `balance_detail` VALUES (3,1,'A.I','I. Inmovilizado intangible.','A.I.1,A.I.2,A.I.3,A.I.4,A.I.5,A.I.6',2,1,1,1,0,0);
INSERT INTO `balance_detail` VALUES (4,1,'A.I.1','1. Desarrollo.','201,(2801),(2901)',3,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (5,1,'A.I.2','2. Concesiones.','202,(2802),(2902)',4,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (6,1,'A.I.3','3. Patentes, licencias, marcas y similares.','203,(2803),(2903)',5,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (7,1,'A.I.4','4. Fondo de comercio.','204',6,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (8,1,'A.I.5','5. Aplicaciones informáticas.','206,(2806),(2906)',7,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (9,1,'A.I.6','6. Otro inmovilizado intangible.','205,209,(2805),(2905)',8,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (10,1,'A.II','II. Inmovilizado material.','A.II.1,A.II.2,A.II.3',9,1,1,1,0,0);
INSERT INTO `balance_detail` VALUES (11,1,'A.II.1','1. Terrenos y construcciones.','210,211,(2811),(2910),(2911)',10,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (12,1,'A.II.2','2. Instalaciones técnicas, y otro inmovilizado material.','212,213,214,215,216,217,218,219,(2812),(2813),(2814),(2815),(2816),(2817),(2818),(2819),(2912),(2913),(2914),(2915),(2916),(2917),(2918),(2919)',11,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (13,1,'A.II.3','3. Inmovilizado en curso y anticipos.','23',12,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (14,1,'A.III','III. Inversiones inmobiliarias.','A.III.1,A.III.2',13,1,1,1,0,0);
INSERT INTO `balance_detail` VALUES (15,1,'A.III.1','1. Terrenos.','220,(2920)',14,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (16,1,'A.III.2','2. Construcciones.','221,(282),(2921)',15,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (17,1,'A.IV','IV. Inversiones en empresas del grupo y asociadas a largo plazo.','A.IV.1,A.IV.2,A.IV.3,A.IV.4,A.IV.5',16,1,1,1,0,0);
INSERT INTO `balance_detail` VALUES (18,1,'A.IV.1','1. Instrumentos de patrimonio.','2403,2404,(2493),(2494),(293)',17,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (19,1,'A.IV.2','2. Créditos a empresas.','2423,2424,(2953),(2954)',18,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (20,1,'A.IV.3','3. Valores representativos de deuda.','2413,2414,(2943),(2944)',19,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (21,1,'A.IV.4','4. Derivados.','',20,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (22,1,'A.IV.5','5. Otros activos financieros.','',21,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (23,1,'A.V','V. Inversiones financieras a largo plazo.','A.V.1,A.V.2,A.V.3,A.V.4,A.V.5',22,1,1,1,0,0);
INSERT INTO `balance_detail` VALUES (24,1,'A.V.1','1. Instrumentos de patrimonio.','2405,(2495),250,(259)',23,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (25,1,'A.V.2','2. Créditos a terceros','2425,252,253,254,(2955),(298)',24,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (26,1,'A.V.3','3. Valores representativos de deuda','2415,251,(2945),(297)',25,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (27,1,'A.V.4','4. Derivados.','255',26,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (28,1,'A.V.5','5. Otros activos financieros.','258,26',27,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (29,1,'A.VI','VI. Activos por impuesto diferido.','474',28,1,0,1,0,0);
INSERT INTO `balance_detail` VALUES (30,1,'','','',29,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (31,1,'B','B) ACTIVO CORRIENTE','B.I,B.II,B.III,B.IV,B.V,B.VI,B.VII',30,1,1,1,0,0);
INSERT INTO `balance_detail` VALUES (32,1,'B.I','I. Activos no corrientes mantenidos para la venta.','580,581,582,583,584,(599)',31,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (33,1,'B.II','II. Existencias.','B.II.1,B.II.2,B.II.3,B.II.4,B.II.5,B.II.6',32,1,1,1,0,0);
INSERT INTO `balance_detail` VALUES (34,1,'B.II.1','1. Comerciales.','30,(390)',33,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (35,1,'B.II.2','2. Materias primas y otros aprovisionamientos.','31,32,(391),(392)',34,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (36,1,'B.II.3','3. Productos en curso.','33,34,(393),(394)',35,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (37,1,'B.II.4','4. Productos terminados.','35,(395)',36,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (38,1,'B.II.5','5. Subproductos, residuos y materiales recuperados.','36,(396)',37,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (39,1,'B.II.6','6. Anticipos a proveedores','407',38,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (40,1,'B.III','III. Deudores comerciales y otras cuentas a cobrar.','B.III.1,B.III.2,B.III.3,B.III.4,B.III.5,B.III.6,B.III.7',39,1,1,1,0,0);
INSERT INTO `balance_detail` VALUES (41,1,'B.III.1','1. Clientes por ventas y prestaciones de servicios.','430,431,432,435,436,(437),(490),(4935)',40,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (42,1,'B.III.2','2. Clientes, empresas del grupo y asociadas.','433,434,(4933),(4934)',41,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (43,1,'B.III.3','3. Deudores varios.','44,5531,5533',42,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (44,1,'B.III.4','4. Personal.','460,544',43,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (45,1,'B.III.5','5. Activos por impuesto corriente.','4709,4703',44,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (46,1,'B.III.6','6. Otros créditos con las Administraciones Públicas.','4700,4708,471,472,473',45,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (47,1,'B.III.7','7. Accionistas (socios) por desembolsos exigidos','5580',46,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (48,1,'B.IV','IV. Inversiones en empresas del grupo y asociadas a corto plazo.','B.IV.1,B.IV.2,B.IV.3,B.IV.4,B.IV.5',47,1,1,1,0,0);
INSERT INTO `balance_detail` VALUES (49,1,'B.IV.1','1. Instrumentos de patrimonio.','5303,5304,(5393),(5394),(593)',48,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (50,1,'B.IV.2','2. Créditos a empresas.','5323,5324,5343,5344,(5953),(5954)',49,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (51,1,'B.IV.3','3. Valores representativos de deuda.','5313,5314,5333,5334,(5943),(5944)',50,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (52,1,'B.IV.4','4. Derivados.','',51,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (53,1,'B.IV.5','5. Otros activos financieros.','5353,5354,5523,5524',52,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (54,1,'B.V','V. Inversiones financieras a corto plazo.','B.V.1,B.V.2,B.V.3,B.V.4,B.V.5',53,1,1,1,0,0);
INSERT INTO `balance_detail` VALUES (55,1,'B.V.1','1. Instrumentos de patrimonio.','5305,540,(5395),(549)',54,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (56,1,'B.V.2','2. Créditos a empresas','5325,5345,542,543,547,(5955),(598)',55,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (57,1,'B.V.3','3. Valores representativos de deuda.','5315,5335,541,546,(5945),(597)',56,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (58,1,'B.V.4','4. Derivados.','5590,5593',57,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (59,1,'B.V.5','5. Otros activos financieros.','5355,545,548,?551,5525,565,566',58,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (60,1,'B.VI','VI. Periodificaciones a corto plazo.','480,567',59,1,0,1,0,0);
INSERT INTO `balance_detail` VALUES (61,1,'B.VII','VII. Efectivo y otros activos líquidos equivalentes.','B.VII.1,B.VII.2',60,1,1,1,0,0);
INSERT INTO `balance_detail` VALUES (62,1,'B.VII.1','1. Tesorería.','570,571,572,573,574,575',61,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (63,1,'B.VII.2','2. Otros activos líquidos equivalentes.','576',62,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (64,1,'','','',63,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (65,1,'AB','TOTAL ACTIVO (A + B)','A,B',64,1,1,1,0,0);
INSERT INTO `balance_detail` VALUES (66,1,'','','',65,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (67,1,'','','',66,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (68,1,'A0','PATRIMONIO NETO Y PASIVO','',67,1,0,1,0,0);
INSERT INTO `balance_detail` VALUES (69,1,'','','',68,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (70,1,'PA','A) PATRIMONIO NETO','PA.A-1,PA.A-2,PA.A-3',69,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (71,1,'PA.A-1','A-1) Fondos propios.','PA.A-1.I,PA.A-1.II,PA.A-1.III,PA.A-1.IV,PA.A-1.V,PA.A-1.VI,PA.A-1.VII,PA.A-1.VIII,PA.A-1.IX',70,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (72,1,'PA.A-1.I','I. Capital.','PA.A-1.I.1,PA.A-1.I.2',71,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (73,1,'PA.A-1.I.1','1. Capital escriturado.','100,101,102',72,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (74,1,'PA.A-1.I.2','2. (Capital no exigido).','(1030),(1040)',73,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (75,1,'PA.A-1.II','II. Prima de emisión.','110',74,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (76,1,'PA.A-1.III','III. Reservas.','PA.A-1.III.1,PA.A-1.III.2',75,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (77,1,'PA.A-1.III.1','1. Legal y estatutarias.','112,1141',76,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (78,1,'PA.A-1.III.2','2. Otras reservas.','113,1140,1142,1143,1144,115,119',77,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (79,1,'PA.A-1.IV','IV. (Acciones y participaciones en patrimonio propias).','(108),(109)',78,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (80,1,'PA.A-1.V','V. Resultados de ejercicios anteriores.','PA.A-1.V.1,PA.A-1.V.2',79,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (81,1,'PA.A-1.V.1','1. Remanente.','120',80,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (82,1,'PA.A-1.V.2','2. (Resultados negativos de ejercicios anteriores).','(121)',81,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (83,1,'PA.A-1.VI','VI. Otras aportaciones de socios.','118',82,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (84,1,'PA.A-1.VII','VII. Resultado del ejercicio.','129,6,7',83,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (85,1,'PA.A-1.VIII','VIII. (Dividendo a cuenta).','(557)',84,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (86,1,'PA.A-1.IX','IX. Otros instrumentos de patrimonio neto.','111',85,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (87,1,'PA.A-2','A-2) Ajustes por cambios de valor.','PA.A-2.I,PA.A-2.II,PA.A-2.III',86,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (88,1,'PA.A-2.I','I. Activos financieros disponibles para la venta.','133',87,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (89,1,'PA.A-2.II','II. Operaciones de cobertura.','1340',88,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (90,1,'PA.A-2.III','III. Otros.','137,139',89,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (91,1,'PA.A-3','A-3) Subvenciones, donaciones y legados recibidos.','130,131,132',90,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (92,1,'','','',91,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (93,1,'PB','B) PASIVO NO CORRIENTE','PB.I,PB.II,PB.III,PB.IV,PB.V',92,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (94,1,'PB.I','I. Provisiones a largo plazo.','PB.I.1,PB.I.2,PB.I.3,PB.I.4',93,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (95,1,'PB.I.1','1. Obligaciones por prestaciones a largo plazo al personal.','140',94,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (96,1,'PB.I.2','2. Actuaciones medioambientales.','145',95,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (97,1,'PB.I.3','3. Provisiones por reestructuración.','146',96,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (98,1,'PB.I.4','4. Otras provisiones.','141,142,143,147',97,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (99,1,'PB.II','II Deudas a largo plazo.','PB.II.1,PB.II.2,PB.II.3,PB.II.4,PB.II.5',98,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (100,1,'PB.II.1','1. Obligaciones y otros valores negociables.','177,178,179',99,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (101,1,'PB.II.2','2. Deudas con entidades de crédito.','1605,170',100,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (102,1,'PB.II.3','3. Acreedores por arrendamiento financiero.','1625,174',101,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (103,1,'PB.II.4','4. Derivados.','176',102,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (104,1,'PB.II.5','5. Otros pasivos financieros.','1615,1635,171,172,173,175,180,185,189',103,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (105,1,'PB.III','III. Deudas con empresas del grupo y asociadas a largo plazo.','1603,1604,1613,1614,1623,1624,1633,1634',104,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (106,1,'PB.IV','IV. Pasivos por impuesto diferido.','479',105,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (107,1,'PB.V','V. Periodificaciones a largo plazo.','181',106,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (108,1,'','','',107,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (109,1,'PC','C) PASIVO CORRIENTE','PC.I,PC.II,PC.III,PC.IV,PC.V,PC.VI',108,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (110,1,'PC.I','I. Pasivos vinculados con activos no corrientes mantenidos para la venta.','585,586,587,588,589',109,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (111,1,'PC.II','II. Provisiones a corto plazo.','499,529',110,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (112,1,'PC.III','III. Deudas a corto plazo.','PC.III.1,PC.III.2,PC.III.3,PC.III.4,PC.III.5',111,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (113,1,'PC.III.1','1. Obligaciones y otros valores negociables.','500,501,505,506',112,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (114,1,'PC.III.2','2. Deudas con entidades de crédito.','5105,520,527',113,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (115,1,'PC.III.3','3. Acreedores por arrendamiento financiero.','5125,524',114,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (116,1,'PC.III.4','4. Derivados.','5595,5598',115,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (117,1,'PC.III.5','5. Otros pasivos financieros.','(1034),(1044),(190),(192),194,509,5115,5135,5145,521,522,523,525,526,528,?551,5525,5530,5532,555,5565,5566,560,561,569',116,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (118,1,'PC.IV','IV. Deudas con empresas del grupo y asociadas a corto plazo.','5103,5104,5113,5114,5123,5124,5133,5134,5143,5144,5523,5524,5563,5564',117,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (119,1,'PC.V','V. Acreedores comerciales y otras cuentas a pagar.','PC.V.1,PC.V.2,PC.V.3,PC.V.4,PC.V.5,PC.V.6,PC.V.7',118,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (120,1,'PC.V.1','1. Proveedores','400,401,405,(406)',119,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (121,1,'PC.V.2','2. Proveedores, empresas del grupo y asociadas.','403,404',120,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (122,1,'PC.V.3','3. Acreedores varios.','41',121,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (123,1,'PC.V.4','4. Personal (remuneraciones pendientes de pago).','465,466',122,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (124,1,'PC.V.5','5. Pasivos por impuesto corriente.','4752',123,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (125,1,'PC.V.6','6. Otras deudas con las Administraciones Públicas.','4750,4751,4758,476,477',124,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (126,1,'PC.V.7','7. Anticipos de clientes.','438',125,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (127,1,'PC.VI','VI. Periodificaciones a corto plazo.','485,568',126,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (128,1,'','','',127,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (129,1,'PABC','TOTAL PATRIMONIO NETO Y PASIVO (A + B + C)','PA,PB,PC',128,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (130,1,'','','',129,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (131,1,'DIF','DIFERENCIA (ACTIVO-PATRIMONIO NETO Y PASIVO)','AB,(PABC)',130,1,1,1,1,0);
INSERT INTO `balance_detail` VALUES (132,2,'A','A OPERACIONES CONTINUADAS','A.T4',0,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (133,2,'A.1','1. Importe neto de la cifra de negocios.','A.1.A,A.1.B',1,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (134,2,'A.1.A','a) Ventas.','700,701,702,703,704,(706),(708),(709)',2,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (135,2,'A.1.B','b) Prestaciones de servicios.','705',3,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (136,2,'A.2','2. Variación de existencias de productos terminados y en curso de fabricación.','(6930),71,7930',4,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (137,2,'A.3','3. Trabajos realizados por la empresa para su activo.','73',5,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (138,2,'A.4','4. Aprovisionamientos.','A.4.A,A.4.B,A.4.C,A.4.D',6,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (139,2,'A.4.A','a) Consumo de mercaderías.','(600),6060,6080,6090,610',7,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (140,2,'A.4.B','b) Consumo de materias primas y otras materias consumibles.','(601),(602),6061,6062,6081,6082,6091,6092,611,612',8,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (141,2,'A.4.C','c) Trabajos realizados por otras empresas.','(607)',9,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (142,2,'A.4.D','d) Deterioro de mercaderías, materias primas y otros aprovisionamientos.','(6931),(6932),(6933),7931,7932,7933',10,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (143,2,'A.5','5. Otros ingresos de explotación.','A.5.A,A.5.B',11,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (144,2,'A.5.A','a) Ingresos accesorios y otros de gestión corriente.','75',12,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (145,2,'A.5.B','b) Subvenciones de explotación incorporadas al resultado del ejercicio.','740,747',13,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (146,2,'A.6','6. Gastos de personal.','A.6.A,A.6.B,A.6.C',14,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (147,2,'A.6.A','a) Sueldos, salarios y asimilados.','(640),(641),(6450)',15,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (148,2,'A.6.B','b) Cargas sociales.','(642),(643),(649)',16,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (149,2,'A.6.C','c) Provisiones.','(644),(6457),7950,7957',17,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (150,2,'A.7','7. Otros gastos de explotación.','A.7.A,A.7.B,A.7.C,A.7.D',18,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (151,2,'A.7.A','a) Servicios exteriores.','(62)',19,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (152,2,'A.7.B','b) Tributos.','(631),(634),636,639',20,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (153,2,'A.7.C','c) Pérdidas, deterioro y variación de provisiones por operaciones comerciales.','(650),(694),(695),794,7954',21,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (154,2,'A.7.D','d) Otros gastos de gestión corriente.','(651),(659)',22,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (155,2,'A.8','8. Amortización del inmovilizado.','(68)',23,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (156,2,'A.9','9. Imputación de subvenciones de inmovilizado no financiero y otras.','746',24,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (157,2,'A.10','10. Excesos de provisiones.','7951,7952,7955,7956',25,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (158,2,'A.11','11. Deterioro y resultado por enajenaciones del inmovilizado.','A.11.A,A.11.B',26,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (159,2,'A.11.A','a) Deterioros y pérdidas.','(690),(691),(692),790,791,792',27,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (160,2,'A.11.B','b) Resultados por enajenaciones y otras.','(670),(671),(672),770,771,772',28,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (161,2,'A.X','Otros Resultados.','678,778',29,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (162,2,'','','',30,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (163,2,'A.T1','A.1) RESULTADO DE EXPLOTACIÓN (1+2+3+4+5+6+7+8+9+10+11+12)','A.1,A.2,A.3,A.4,A.5,A.6,A.7,A.8,A.9,A.10,A.11,A.X',31,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (164,2,'','','',32,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (165,2,'A.12','12. Ingresos financieros.','A.12.A,A.12.B',33,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (166,2,'A.12.A','a) De participaciones en instrumentos de patrimonio.','A.12.A.1,A.12.A.2',34,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (167,2,'A.12.A.1','a1) En empresas del grupo y asociadas.','7600,7601',35,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (168,2,'A.12.A.2','a2) En terceros.','7602,7603',36,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (169,2,'A.12.B','b) De valores negociables y otros instrumentos financieros.','A.12.B.1,A.12.B.2',37,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (170,2,'A.12.B.1','b1) De empresas del grupo y asociadas.','7610,7611,76200,76201,76210,76211',38,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (171,2,'A.12.B.2','b2) De terceros.','7612,7613,76202,76203,76212,76213,767,769',39,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (172,2,'A.13','13. Gastos financieros.','A.13.A,A.13.B,A.13.C',40,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (173,2,'A.13.A','a) Por deudas con empresas del grupo y asociadas.','(6610),(6611),(6615),(6616),(6620),(6621),(6640),(6641),(6650),(6651),(6654),(6655)',41,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (174,2,'A.13.B','b) Por deudas con terceros.','(6612),(6613),(6617),(6618),(6622),(6623),(6624),(6642),(6643),(6652),(6653),(6656),(6657),(669)',42,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (175,2,'A.13.C','c) Por actualización de provisiones.','(660)',43,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (176,2,'A.14','14. Variación de valor razonable en instrumentos financieros.','A.14.A,A.14.B',44,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (177,2,'A.14.A','a) Cartera de negociación y otros.','(6630),(6631),(6633),7630,7631,7633',45,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (178,2,'A.14.B','b) Imputación al resultado del ejercicio por activos financieros disponibles para la venta.','(6632),7632',46,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (179,2,'A.15','15. Diferencias de cambio.','(668),768',47,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (180,2,'A.16','16. Deterioro y resultado por enajenaciones de instrumentos financieros.','A.16.A,A.16.B',48,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (181,2,'A.16.A','a) Deterioros y pérdidas.','(696),(697),(698),(699),796,797,798,799',49,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (182,2,'A.16.B','b) Resultados por enajenaciones y otras.','(666),(667),(673),(675),766,773,775',50,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (183,2,'','','',51,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (184,2,'A.T2','A.2) RESULTADO FINANCIERO (12+13+14+15+16)','A.12,A.13,A.14,A.15,A.16',52,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (185,2,'','','',53,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (186,2,'A.T3','A.3) RESULTADO ANTES DE IMPUESTOS (A.1+A.2)','A.T1,A.T2',54,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (187,2,'','','',55,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (188,2,'A.17','17. Impuestos sobre beneficios.','(6300),6301,(633),638',56,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (189,2,'','','',57,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (190,2,'A.T4','A.4) RESULTADO DEL EJ. PROCEDENTE DE OP. CONTINUADAS (A.3+17)','A.T3,A.17',58,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (191,2,'','','',59,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (192,2,'B','B) OPERACIONES INTERRUMPIDAS','B.18',60,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (193,2,'B.18','18. Resultado del ejercicio procedente de operaciones interrumpidas neto de impuestos.','',61,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (194,2,'','','',62,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (195,2,'A.5','A.5) RESULTADO DEL EJERCICIO (A.4+18)','A.T4,B.18',63,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (196,3,'','ACTIVO','',0,1,0,1,0,0);
INSERT INTO `balance_detail` VALUES (197,3,'A','A) ACTIVO NO CORRIENTE','A.I,A.II,A.III,A.IV,A.V,A.VI',1,1,1,1,0,0);
INSERT INTO `balance_detail` VALUES (198,3,'A.I','I. Inmovilizado intangible','20,(280),(290)',2,1,0,1,0,0);
INSERT INTO `balance_detail` VALUES (199,3,'A.II','II. Inmovilizado material','21,(281),(291),23',3,1,0,1,0,0);
INSERT INTO `balance_detail` VALUES (200,3,'A.III','III. Inversiones inmobiliarias','22,(282),(292)',4,1,0,1,0,0);
INSERT INTO `balance_detail` VALUES (201,3,'A.IV','IV. Inversiones en empresas del grupo y asociadas L/P','2403,2404,2413,2414,2423,2424,2493,2494,293,(2943),(2944),(2953),(2954)',5,1,0,1,0,0);
INSERT INTO `balance_detail` VALUES (202,3,'A.V','V. Inversiones financieras a largo plazo','2405,2415,2425,(2495),250,251,252,253,254,255,257,258,259,26,2945,2955,297,298',6,1,0,1,0,0);
INSERT INTO `balance_detail` VALUES (203,3,'A.VI','VI. Activos por Impuesto diferido','474',7,1,0,1,0,0);
INSERT INTO `balance_detail` VALUES (204,3,'','','',8,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (205,3,'B','B) ACTIVO CORRIENTE','B.I,B.II,B.III,B.IV,B.V,B.VI,B.VII',9,1,1,1,0,0);
INSERT INTO `balance_detail` VALUES (206,3,'B.I','I. Activos no corrientes mantenidos para la venta','580,581,582,583,584,(599)',10,1,0,1,0,0);
INSERT INTO `balance_detail` VALUES (207,3,'B.II','II. Existencias','30,31,32,33,34,35,36,(39),407',11,1,0,1,0,0);
INSERT INTO `balance_detail` VALUES (208,3,'B.III','III. Deudores comerciales y otras cuentas a cobrar','B.III.1,B.III.2,B.III.3',12,1,1,1,0,0);
INSERT INTO `balance_detail` VALUES (209,3,'B.III.1','1. Clientes por ventas y prestaciones de servicios','430,431,432,433,434,435,436,(437),(490),(493)',13,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (210,3,'B.III.2','2. Accionistas (socios) por desembolsos exigidos','5580',14,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (211,3,'B.III.3','3. Otros deudores','44,460,470,471,472,473,5531,5533,544',15,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (212,3,'B.IV','IV. Inversiones en empresas del grupo y asociadas C/P','5303,5304,5313,5314,5323,5324,5333,5334,5343,5344,5353,5354,(5393),(5394),5523,5524,(593),(5943),(5944),(5953),(5954)',16,1,0,1,0,0);
INSERT INTO `balance_detail` VALUES (213,3,'B.V','V. Inversiones financieras a corto plazo','5305,5315,5325,5335,5345,5355,(5395),540,541,542,543,545,546,547,548,(549),?551,5525,5590,5593,565,566,(5945),(5955),(597),(598)',17,1,0,1,0,0);
INSERT INTO `balance_detail` VALUES (214,3,'B.VI','VI. Periodificaciones a corto plazo','480,567',18,1,0,1,0,0);
INSERT INTO `balance_detail` VALUES (215,3,'B.VII','VII. Efectivo y otros activos líquidos equivalentes','57',19,1,0,1,0,0);
INSERT INTO `balance_detail` VALUES (216,3,'','','',20,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (217,3,'AB','TOTAL ACTIVO (A+B)','A,B',21,1,1,1,0,0);
INSERT INTO `balance_detail` VALUES (218,3,'','','',22,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (219,3,'','PATRIMONIO NETO Y PASIVO','',23,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (220,3,'PA','A) PATRIMONIO NETO','PA.1,PA.2,PA.3',24,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (221,3,'PA.1','A-1) Fondos propios','PA.1.I,PA.1.II,PA.1.III,PA.1.IV,PA.1.V,PA.1.VI,PA.1.VII,PA.1.VIII,PA.1.IX',25,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (222,3,'PA.1.I','I. Capital','PA.1.I.1,PA.1.I.2',26,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (223,3,'PA.1.I.1','1. Capital escriturado','100,101,102',27,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (224,3,'PA.1.I.2','2. (Capital no exigido)','(1030),(1040)',28,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (225,3,'PA.1.II','II. Prima de emisión','110',29,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (226,3,'PA.1.III','III. Reservas','112,113,114,115,119,108,109',30,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (227,3,'PA.1.IV','IV. (Acciones y participaciones en patrimonio propias)','(108),(109)',31,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (228,3,'PA.1.V','V. Resultado de ejercicios anteriores','120,(121)',32,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (229,3,'PA.1.VI','VI. Otras aportaciones de socios','118',33,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (230,3,'PA.1.VII','VII. Resultado del ejercicio','129,6,7',34,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (231,3,'PA.1.VIII','VIII. (Dividendo a cuenta )','(557)',35,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (232,3,'PA.1.IX','IX. Otros instrumentos de patrimonio neto','111',36,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (233,3,'PA.2','A-2) Ajustes por cambios de valor','133,1340,137',37,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (234,3,'PA.3','A-3) Subvenciones, donaciones y legados recibidos','130,131,132',38,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (235,3,'','','',39,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (236,3,'PB','B) PASIVO NO CORRIENTE','PB.I,PB.II,PB.III,PB.IV,PB.V',40,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (237,3,'PB.I','I. Provisiones a largo plazo','14',41,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (238,3,'PB.II','II. Deudas a largo plazo','PB.II.1,PB.II.2,PB.II.3',42,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (239,3,'PB.II.1','1. Deudas con entidades de crédito','1605,170',43,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (240,3,'PB.II.2','2. Acreedores por arrendamiento financiero','1625,174',44,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (241,3,'PB.II.3','3. Otras deudas a largo plazo','1615,1635,171,172,173,175,176,177,178,179,180,185,189',45,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (242,3,'PB.III','III .Deudas con empresas del grupo y asociadas L/P','1603,1604,1613,1614,1623,1624,1633,1634',46,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (243,3,'PB.IV','IV. Pasivos por impuesto diferido','479',47,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (244,3,'PB.V','V. Periodificaciones a largo plazo','181',48,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (245,3,'','','',49,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (246,3,'PC','C) PASIVO CORRIENTE','PC.I,PC.II,PC.III,PC.IV,PC.V,PC.VI',50,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (247,3,'PC.I','I. Pasivos vinculados con activos no corrientes mantenidos para la venta','585,586,587,588,589',51,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (248,3,'PC.II','II. Provisiones a corto plazo','499,529',52,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (249,3,'PC.III','III. Deudas a corto plazo','PC.III.1,PC.III.2,PC.III.3',53,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (250,3,'PC.III.1','1. Deudas con entidades de crédito','5105,520,527',54,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (251,3,'PC.III.2','2. Acreedores por arrendamiento financiero','5125,524',55,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (252,3,'PC.III.3','3. Otras deudas a corto plazo','(1034),(1044),(190),(192),194,500,501,505,509,5115,5135,5145,521,522,523,525,526,528,?551,5525,5530,5532,555,5565,5566,5595,5598,560,561,569',56,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (253,3,'PC.IV','IV. Deudas con empresas del grupo y asociadas C/P','5103,5104,5113,5114,5123,5124,5133,5134,5143,5144,5523,5524,5563,5564',57,1,0,0,0,1);
INSERT INTO `balance_detail` VALUES (254,3,'PC.V','V. Acreedores comerciales y otras cuentas a pagar','PC.V.1,PC.V.2',58,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (255,3,'PC.V.1','1. Proveedores','400,401,403,404,405,(406)',59,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (256,3,'PC.V.2','2. Otros acreedores','41,438,465,466,475,476,477',60,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (257,3,'PC.VI','VI. Periodificaciones a corto plazo','485,568',61,1,0,1,0,1);
INSERT INTO `balance_detail` VALUES (258,3,'','','',62,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (259,3,'P.T','TOTAL PATRIMONIO NETO Y PASIVO (A+B+C)','PA,PB,PC',63,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (260,3,'DIF','DIFERENCIA (ACTIVO-PATRIMONIO NETO Y PASIVO)','AB,(P.T)',64,1,1,1,1,0);
INSERT INTO `balance_detail` VALUES (261,4,'','','',0,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (262,4,'1','1. Importe neto de la cifra de negocios','700,701,702,703,704,705,(706),(708),(709)',1,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (263,4,'2','2. Variación de existencias de productos terminados y en curso de fabricación','(6930),71,7930',2,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (264,4,'3','3. Trabajos realizados por la empresa para su activo','73',3,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (265,4,'4','4. Aprovisionamientos','(600),(601),(602),(606),(607),608,609,61,(693),(6932),(6933),7931,7932,7933',4,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (266,4,'5','5. Otros ingresos de explotación','740,747,75',5,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (267,4,'6','6. Gastos de personal','(64),7950,7957',6,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (268,4,'7','7. Otros gastos de explotación','(62),(631),(634),636,639,(65),(694),(695),794,7954',7,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (269,4,'8','8. Amortización de inmovilizado','(68)',8,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (270,4,'9','9. Imputación de subvenciones de inmovilizado no financiero y otras','746',9,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (271,4,'10','10. Excesos de provisiones','7951,7952,7955,7956',10,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (272,4,'11','11. Deterioro y resultado por enejenaciones del inmovilizado','(670),(671),(672),(690),(691),(692),770,771,772,790,791,792',11,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (273,4,'12','12. Otros Resultados','(678),778',12,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (274,4,'','','',13,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (275,4,'A','A) RESULTADO DE EXPLOTACIÓN (1+2+3+4+5+6+7+8+9+10+11+12)','1,2,3,4,5,6,7,8,9,10,11,12',14,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (276,4,'','','',15,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (277,4,'13','13. Ingresos financieros','760,761,762,767,769',16,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (278,4,'14','14. Gastos financieros','(660),(661),(662),(664),(665),(669)',17,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (279,4,'15','15. Variación de valor razonable en instrumentos financieros','(663),763',18,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (280,4,'16','16. Diferencias de cambio','(668),768',19,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (281,4,'17','17. Deterioro y resultado por enajenaciones de instrum','(666),(667),(673),(675),(696),(697),(698),(699),766,773,775,796,797,798,799',20,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (282,4,'','','',21,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (283,4,'B','B) RESULTADO FINANCIERO (13+14+15+16+17)','13,14,15,16,17',22,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (284,4,'','','',23,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (285,4,'C','C) RESULTADO ANTES DE IMPUESTOS (A+B)','A,B',24,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (286,4,'','','',25,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (287,4,'18','18. Impuestos sobre beneficios','(6300),(6301),(633),638',26,0,0,1,0,1);
INSERT INTO `balance_detail` VALUES (288,4,'','','',27,0,0,0,0,1);
INSERT INTO `balance_detail` VALUES (289,4,'D','D) RESULTADO DEL EJERCICIO (C+18)','C,18',28,1,1,1,0,1);
INSERT INTO `balance_detail` VALUES (290,5,'A','A) Resultado de la cuenta de pérdidas y ganancias','',0,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (291,5,'','','',1,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (292,5,'1','Ingresos y gastos imputados directamente al patrimonio neto','1.I,1.II,1.III,1.IV,1.V',2,1,0,1,0,0);
INSERT INTO `balance_detail` VALUES (293,5,'1.I','I. Por valoración de instrumentos financieros','800,89,900,991,992',3,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (294,5,'1.II','II. Por coberturas de flujos de efectivo','810,910',4,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (295,5,'1.III','III. Subvenciones',' donaciones y legados recibidos',13,0,0,0,1,0);
INSERT INTO `balance_detail` VALUES (296,5,'1.IV','IV. Por ganancias y pérdidas actuariales y otros ajust','85,95',6,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (297,5,'1.V','V. Efecto impositivo','8300,8301,833,834,835,838',7,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (298,5,'B','B) Total Ingresos y gastos imputados directamente al patrimonio neto (I+II+III+IV+V)','1.I,1.II,1.III,1.IV,1.V',8,1,1,1,0,0);
INSERT INTO `balance_detail` VALUES (299,5,'','','',9,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (300,5,'2','Transferencias a la cuenta de P Y G','2.VI,2.VII,2.VIII,2.IX',10,1,0,1,0,0);
INSERT INTO `balance_detail` VALUES (301,5,'2.VI','VI. Por valoración de instrumentos financieros','802,902,993,994',11,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (302,5,'2.VII','VII. Por coberturas de flujos de efectivo','812,912',12,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (303,5,'2.VIII','VIII. Subvenciones',' donaciones y legados recibidos',18,0,0,0,1,0);
INSERT INTO `balance_detail` VALUES (304,5,'2.IX','IX. Efecto impositivo','8301,836,837',14,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (305,5,'C','C) Total transferencias a la cuentad de P Y G (VI+VII+VIII+IX)','2.VI,2.VII,2.VIII,2.IX',15,1,1,1,0,0);
INSERT INTO `balance_detail` VALUES (306,5,'','','',16,0,0,1,0,0);
INSERT INTO `balance_detail` VALUES (307,5,'D','TOTAL DE INGRESOS Y GASTOS RECONOCIDOS (A+B+C)','A,B,C',17,1,1,1,0,0);

#
# Dumping data for table `bank`
#

INSERT INTO `bank` VALUES (1,'ALLFUNDS BANK, S.A.','0011');
INSERT INTO `bank` VALUES (2,'ALTAE BANCO, S.A.','0099');
INSERT INTO `bank` VALUES (3,'BANCA MARCH, S.A.','0061');
INSERT INTO `bank` VALUES (4,'BANCA PUEYO, S.A.','0078');
INSERT INTO `bank` VALUES (5,'BANCO ALCALA, S.A.','0188');
INSERT INTO `bank` VALUES (6,'BANCO ALICANTINO DE COMERCIO, S.A.','0083');
INSERT INTO `bank` VALUES (7,'BANCO ARABE ESPAÑOL, S.A.','0136');
INSERT INTO `bank` VALUES (8,'BANCO BANIF, S.A.','0086');
INSERT INTO `bank` VALUES (9,'BANCO BILBAO VIZCAYA ARGENTARIA, S.A.','0182');
INSERT INTO `bank` VALUES (10,'BANCO CAIXA GERAL, S.A.','0130');
INSERT INTO `bank` VALUES (11,'BANCO CETELEM, S.A.','0225');
INSERT INTO `bank` VALUES (12,'BANCO CONDAL, S.A.','0021');
INSERT INTO `bank` VALUES (13,'BANCO COOPERATIVO ESPAÑOL, S.A.','0198');
INSERT INTO `bank` VALUES (14,'BANCO DE ALBACETE, S.A.','0091');
INSERT INTO `bank` VALUES (15,'BANCO DE ANDALUCIA, S.A.','0004');
INSERT INTO `bank` VALUES (16,'BANCO DE CASTILLA, S.A.','0082');
INSERT INTO `bank` VALUES (17,'BANCO DE CREDITO BALEAR, S.A.','0024');
INSERT INTO `bank` VALUES (18,'BANCO DE CREDITO LOCAL DE ESPAÑA, S.A.','1004');
INSERT INTO `bank` VALUES (19,'BANCO DE DEPOSITOS, S.A.','0003');
INSERT INTO `bank` VALUES (20,'BANCO DE EUROPA, S.A.','0133');
INSERT INTO `bank` VALUES (21,'BANCO DE FINANZAS E INVERSIONES, S.A.','0186');
INSERT INTO `bank` VALUES (22,'BANCO DE GALICIA, S.A.','0097');
INSERT INTO `bank` VALUES (23,'BANCO DE LA PEQUEÑA Y MEDIANA EMPRESA, S.A.','0142');
INSERT INTO `bank` VALUES (24,'BANCO DE MADRID, S.A.','0059');
INSERT INTO `bank` VALUES (25,'BANCO DE PROMOCION DE NEGOCIOS, S.A. (PROMOBANC)','0132');
INSERT INTO `bank` VALUES (26,'BANCO DE SABADELL, S.A.','0081');
INSERT INTO `bank` VALUES (27,'BANCO DE VALENCIA, S.A.','0093');
INSERT INTO `bank` VALUES (28,'BANCO DE VASCONIA, S.A.','0095');
INSERT INTO `bank` VALUES (29,'BANCO DEPOSITARIO BBVA, S.A.','0057');
INSERT INTO `bank` VALUES (30,'BANCO ESPAÑOL DE CREDITO, S.A.','0030');
INSERT INTO `bank` VALUES (31,'BANCO ETCHEVERRIA, S.A.','0031');
INSERT INTO `bank` VALUES (32,'BANCO EUROPEO DE FINANZAS, S.A.','0184');
INSERT INTO `bank` VALUES (33,'BANCO EXELBANK, S.A.','0228');
INSERT INTO `bank` VALUES (34,'BANCO FINANTIA SOFINLOC, S.A.','0220');
INSERT INTO `bank` VALUES (35,'BANCO GALLEGO, S.A.','0046');
INSERT INTO `bank` VALUES (36,'BANCO GUIPUZCOANO, S.A.','0042');
INSERT INTO `bank` VALUES (37,'BANCO HALIFAX HISPANIA, S.A.','0217');
INSERT INTO `bank` VALUES (38,'BANCO INDUSTRIAL DE BILBAO, S.A.','0113');
INSERT INTO `bank` VALUES (39,'BANCO INVERSIS NET, S.A.','0232');
INSERT INTO `bank` VALUES (40,'BANCO LIBERTA, S.A.','0115');
INSERT INTO `bank` VALUES (41,'BANCO OCCIDENTAL, S.A.','0121');
INSERT INTO `bank` VALUES (42,'BANCO PASTOR, S.A.','0072');
INSERT INTO `bank` VALUES (43,'BANCO POPULAR ESPAÑOL, S.A.','0075');
INSERT INTO `bank` VALUES (44,'BANCO POPULAR HIPOTECARIO, S.A.','0216');
INSERT INTO `bank` VALUES (45,'BANCO SANTANDER CENTRAL HISPANO, S.A.','0049');
INSERT INTO `bank` VALUES (46,'BANCO SERVICIOS FINANCIEROS CAJA MADRID-MAPFRE,SA','0063');
INSERT INTO `bank` VALUES (47,'BANCO URQUIJO SABADELL BANCA PRIVADA, S.A.','0185');
INSERT INTO `bank` VALUES (48,'BANCOFAR, S.A.','0125');
INSERT INTO `bank` VALUES (49,'BANCOPOPULAR-E, S.A.','0229');
INSERT INTO `bank` VALUES (50,'BANESTO BANCO DE EMISIONES, S.A.','0038');
INSERT INTO `bank` VALUES (51,'BANKINTER, S.A.','0128');
INSERT INTO `bank` VALUES (52,'BANKOA, S.A.','0138');
INSERT INTO `bank` VALUES (53,'BANQUE MAROCAINE COMMERCE EXTERIEUR INTERNAT.,S.A.','0219');
INSERT INTO `bank` VALUES (54,'BARCLAYS BANK, S.A.','0065');
INSERT INTO `bank` VALUES (55,'BBVA BANCO DE FINANCIACION, S.A.','0129');
INSERT INTO `bank` VALUES (56,'BNP PARIBAS ESPAÑA, S.A.','0058');
INSERT INTO `bank` VALUES (57,'CITIBANK ESPAÑA, S.A.','0122');
INSERT INTO `bank` VALUES (58,'DEUTSCHE BANK CREDIT, S.A.','0205');
INSERT INTO `bank` VALUES (59,'DEUTSCHE BANK, S.A.E.','0019');
INSERT INTO `bank` VALUES (60,'DEXIA SABADELL BANCO LOCAL, S.A.','0231');
INSERT INTO `bank` VALUES (61,'EBN BANCO DE NEGOCIOS, S.A.','0211');
INSERT INTO `bank` VALUES (62,'FINANZIA, BANCO DE CREDITO, S.A.','0009');
INSERT INTO `bank` VALUES (63,'GENERAL ELECTRIC CAPITAL BANK, S.A.','0223');
INSERT INTO `bank` VALUES (64,'OPEN BANK SANTANDER CONSUMER, S.A.','0073');
INSERT INTO `bank` VALUES (65,'POPULAR BANCA PRIVADA, S.A.','0233');
INSERT INTO `bank` VALUES (66,'PRIVAT BANK, S.A.','0200');
INSERT INTO `bank` VALUES (67,'RBC DEXIA INVESTOR SERVICES ESPAÑA, S.A.','0094');
INSERT INTO `bank` VALUES (68,'SANTANDER CONSUMER FINANCE, S.A.','0224');
INSERT INTO `bank` VALUES (69,'SANTANDER INVESTMENT, S.A.','0036');
INSERT INTO `bank` VALUES (70,'UBS ESPAÑA, S.A.','0226');
INSERT INTO `bank` VALUES (71,'UNOE BANK, S.A.','0227');
INSERT INTO `bank` VALUES (72,'BILBAO BIZKAIA KUTXA,AURREZKI KUTXA ETA BAHITETXEA','2095');
INSERT INTO `bank` VALUES (73,'C.A.M.P. CIRCULO CATOLICO DE OBREROS DE BURGOS','2017');
INSERT INTO `bank` VALUES (74,'CAIXA AFORROS VIGO,OURENSE E PONTEVEDRA(CAIXANOVA)','2080');
INSERT INTO `bank` VALUES (75,'CAIXA D\'ESTALVIS COMARCAL DE MANLLEU','2040');
INSERT INTO `bank` VALUES (76,'CAIXA D\'ESTALVIS DE CATALUNYA','2013');
INSERT INTO `bank` VALUES (77,'CAIXA D\'ESTALVIS DE GIRONA','2030');
INSERT INTO `bank` VALUES (78,'CAIXA D\'ESTALVIS DE MANRESA','2041');
INSERT INTO `bank` VALUES (79,'CAIXA D\'ESTALVIS DE SABADELL','2059');
INSERT INTO `bank` VALUES (80,'CAIXA D\'ESTALVIS DE TARRAGONA','2073');
INSERT INTO `bank` VALUES (81,'CAIXA D\'ESTALVIS DE TERRASSA','2074');
INSERT INTO `bank` VALUES (82,'CAIXA D\'ESTALVIS DEL PENEDES','2081');
INSERT INTO `bank` VALUES (83,'CAIXA D\'ESTALVIS LAIETANA','2042');
INSERT INTO `bank` VALUES (84,'CAJA AH. VALENCIA, CASTELLON Y ALICANTE, BANCAJA','2077');
INSERT INTO `bank` VALUES (85,'CAJA DE AH. PROV. SAN FERNANDO DE SEVILLA Y JEREZ','2071');
INSERT INTO `bank` VALUES (86,'CAJA DE AHORRO PROVINCIAL DE GUADALAJARA','2032');
INSERT INTO `bank` VALUES (87,'CAJA DE AHORROS DE ASTURIAS','2048');
INSERT INTO `bank` VALUES (88,'CAJA DE AHORROS DE CASTILLA-LA MANCHA','2105');
INSERT INTO `bank` VALUES (89,'CAJA DE AHORROS DE GALICIA','2091');
INSERT INTO `bank` VALUES (90,'CAJA DE AHORROS DE LA INMACULADA DE ARAGON','2086');
INSERT INTO `bank` VALUES (91,'CAJA DE AHORROS DE LA RIOJA','2037');
INSERT INTO `bank` VALUES (92,'CAJA DE AHORROS DE MURCIA','2043');
INSERT INTO `bank` VALUES (93,'CAJA DE AHORROS DE SALAMANCA Y SORIA','2104');
INSERT INTO `bank` VALUES (94,'CAJA DE AHORROS DE SANTANDER Y CANTABRIA','2066');
INSERT INTO `bank` VALUES (95,'CAJA DE AHORROS DE VITORIA Y ALAVA','2097');
INSERT INTO `bank` VALUES (96,'CAJA DE AHORROS DEL MEDITERRANEO','2090');
INSERT INTO `bank` VALUES (97,'CAJA DE AHORROS MUNICIPAL DE BURGOS','2018');
INSERT INTO `bank` VALUES (98,'CAJA DE AHORROS Y M.P. DE AVILA','2094');
INSERT INTO `bank` VALUES (99,'CAJA DE AHORROS Y M.P. DE CORDOBA','2024');
INSERT INTO `bank` VALUES (100,'CAJA DE AHORROS Y M.P. DE EXTREMADURA','2099');
INSERT INTO `bank` VALUES (101,'CAJA DE AHORROS Y M.P. DE GIPUZKOA Y SAN SEBASTIAN','2101');
INSERT INTO `bank` VALUES (102,'CAJA DE AHORROS Y M.P. DE LAS BALEARES','2051');
INSERT INTO `bank` VALUES (103,'CAJA DE AHORROS Y M.P. DE MADRID','2038');
INSERT INTO `bank` VALUES (104,'CAJA DE AHORROS Y M.P. DE NAVARRA','2054');
INSERT INTO `bank` VALUES (105,'CAJA DE AHORROS Y M.P. DE ONTINYENT','2045');
INSERT INTO `bank` VALUES (106,'CAJA DE AHORROS Y M.P. DE SEGOVIA','2069');
INSERT INTO `bank` VALUES (107,'CAJA DE AHORROS Y M.P. DE ZARAGOZA, ARAGON Y RIOJA','2085');
INSERT INTO `bank` VALUES (108,'CAJA DE AHORROS Y PENSIONES DE BARCELONA','2100');
INSERT INTO `bank` VALUES (109,'CAJA ESPAÑA DE INVERSIONES, CAJA DE AHORROS Y M.P.','2096');
INSERT INTO `bank` VALUES (110,'CAJA GENERAL DE AHORROS DE CANARIAS','2065');
INSERT INTO `bank` VALUES (111,'CAJA GENERAL DE AHORROS DE GRANADA','2031');
INSERT INTO `bank` VALUES (112,'CAJA INSULAR DE AHORROS DE CANARIAS','2052');
INSERT INTO `bank` VALUES (113,'CAJA PROVINCIAL DE AHORROS DE JAEN','2092');
INSERT INTO `bank` VALUES (114,'COLONYA - CAIXA D\'ESTALVIS DE POLLENSA','2056');
INSERT INTO `bank` VALUES (115,'CONFEDERACION ESPAÑOLA DE CAJAS DE AHORROS','2000');
INSERT INTO `bank` VALUES (116,'M.P. Y CAJA DE AHORROS DE HUELVA Y SEVILLA','2098');
INSERT INTO `bank` VALUES (117,'M.P. Y CAJA GENERAL AHORROS DE BADAJOZ','2010');
INSERT INTO `bank` VALUES (118,'M.P.C.A. RONDA, CADIZ, ALMERIA, MALAGA Y ANTEQUERA','2103');
INSERT INTO `bank` VALUES (119,'CAIXA DE C. DELS ENGINYERS-C.C. INGENIEROS, S.C.C','3025');
INSERT INTO `bank` VALUES (120,'CAIXA DELS ADVOCATS-CAJA DE LOS ABOGADOS, S.C.C.','3171');
INSERT INTO `bank` VALUES (121,'CAIXA POPULAR-CAIXA RURAL, S.C.C.V.','3159');
INSERT INTO `bank` VALUES (122,'CAIXA R. ALBALAT DELS SORELLS, C.C.V.','3186');
INSERT INTO `bank` VALUES (123,'CAIXA R. ALTEA, C.C.V.','3045');
INSERT INTO `bank` VALUES (124,'CAIXA R. BENICARLO, S.C.C.V.','3162');
INSERT INTO `bank` VALUES (125,'CAIXA R. D\'ALGEMESI, S.C.V.C.','3117');
INSERT INTO `bank` VALUES (126,'CAIXA R. DE BALEARS, S.C.C.','3147');
INSERT INTO `bank` VALUES (127,'CAIXA R. DE CALLOSA D\'EN SARRIA, C.C.V.','3105');
INSERT INTO `bank` VALUES (128,'CAIXA R. DE L\'ALCUDIA, S.C.V.C.','3096');
INSERT INTO `bank` VALUES (129,'CAIXA R. DE TURIS, C.C.V.','3123');
INSERT INTO `bank` VALUES (130,'CAIXA R. GALEGA, S.C.C.L.G.','3070');
INSERT INTO `bank` VALUES (131,'CAIXA R. LA VALL \'S. ISIDRO\', S.C.C.V.','3111');
INSERT INTO `bank` VALUES (132,'CAIXA R. S. VICENT FERRER DE LA VALL D\'UIXO,C.C.V.','3102');
INSERT INTO `bank` VALUES (133,'CAIXA R. VINAROS, S.C.C.V.','3174');
INSERT INTO `bank` VALUES (134,'CAIXA R.S.JOSEP DE VILAVELLA, S.C.C.V.','3160');
INSERT INTO `bank` VALUES (135,'CAIXA RURAL LES COVES DE VINROMA, S.C.C.V.','3166');
INSERT INTO `bank` VALUES (136,'CAIXA RURAL TORRENT C.C.V.','3118');
INSERT INTO `bank` VALUES (137,'CAJA CAMINOS, S.C.C.','3172');
INSERT INTO `bank` VALUES (138,'CAJA CAMPO, CAJA RURAL, S.C.C.','3094');
INSERT INTO `bank` VALUES (139,'CAJA DE ARQUITECTOS S.C.C.','3183');
INSERT INTO `bank` VALUES (140,'CAJA DE CREDITO DE ALCOY, C.C.V. (EN LIQUIDACION)','3184');
INSERT INTO `bank` VALUES (141,'CAJA DE CREDITO DE PETREL, CAJA RURAL, C.C.V.','3029');
INSERT INTO `bank` VALUES (142,'CAJA ESCOLAR DE FOMENTO, S.C.C.','3146');
INSERT INTO `bank` VALUES (143,'CAJA LABORAL POPULAR C.C.','3035');
INSERT INTO `bank` VALUES (144,'CAJA R. \'NUESTRA MADRE DEL SOL\', S.C.A.C.','3115');
INSERT INTO `bank` VALUES (145,'CAJA R. ARAGONESA Y DE LOS PIRINEOS, S.C.C.','3189');
INSERT INTO `bank` VALUES (146,'CAJA R. CASTELLON S. ISIDRO, S.C.C.V.','3114');
INSERT INTO `bank` VALUES (147,'CAJA R. CATOLICO AGRARIA, S.C.C.V.','3110');
INSERT INTO `bank` VALUES (148,'CAJA R. CENTRAL, S.C.C.','3005');
INSERT INTO `bank` VALUES (149,'CAJA R. COMARCAL DE MOTA DEL CUERVO, S.C.L.C.A.','3116');
INSERT INTO `bank` VALUES (150,'CAJA R. DE ALBACETE, S.C.C.','3056');
INSERT INTO `bank` VALUES (151,'CAJA R. DE ALBAL, C.C.V.','3150');
INSERT INTO `bank` VALUES (152,'CAJA R. DE ALGINET, S.C.C.V.','3179');
INSERT INTO `bank` VALUES (153,'CAJA R. DE ALMENDRALEJO, S.C.C.','3001');
INSERT INTO `bank` VALUES (154,'CAJA R. DE ARAGON, S.C.C.','3021');
INSERT INTO `bank` VALUES (155,'CAJA R. DE ASTURIAS, S.C.C.','3059');
INSERT INTO `bank` VALUES (156,'CAJA R. DE BETXI, S.C.C.V.','3138');
INSERT INTO `bank` VALUES (157,'CAJA R. DE BURGOS,S.C.C.','3060');
INSERT INTO `bank` VALUES (158,'CAJA R. DE CANARIAS, S.C.C.','3177');
INSERT INTO `bank` VALUES (159,'CAJA R. DE CASAS IBAÑEZ, S.C. - C.C.A.,','3127');
INSERT INTO `bank` VALUES (160,'CAJA R. DE CASINOS S.C.C.V.','3137');
INSERT INTO `bank` VALUES (161,'CAJA R. DE CAÑETE TORRES NTRA.SRA.DEL CAMPO,S.C.A.','3104');
INSERT INTO `bank` VALUES (162,'CAJA R. DE CHESTE, S.C.C.','3121');
INSERT INTO `bank` VALUES (163,'CAJA R. DE CIUDAD REAL, S.C.C.','3062');
INSERT INTO `bank` VALUES (164,'CAJA R. DE CORDOBA, S.C.C.','3063');
INSERT INTO `bank` VALUES (165,'CAJA R. DE CUENCA, S.C.C.','3064');
INSERT INTO `bank` VALUES (166,'CAJA R. DE EXTREMADURA, S.C.C.','3009');
INSERT INTO `bank` VALUES (167,'CAJA R. DE FUENTEPELAYO, S.C.C.','3022');
INSERT INTO `bank` VALUES (168,'CAJA R. DE GIJON, C.C','3007');
INSERT INTO `bank` VALUES (169,'CAJA R. DE GRANADA, S.C.C.','3023');
INSERT INTO `bank` VALUES (170,'CAJA R. DE GUISSONA, S.C.C.','3140');
INSERT INTO `bank` VALUES (171,'CAJA R. DE JAEN, S.C.C.','3067');
INSERT INTO `bank` VALUES (172,'CAJA R. DE LA CARLOTA, S.C.A.C.L. (EN LIQUIDACION)','3154');
INSERT INTO `bank` VALUES (173,'CAJA R. DE LA RODA, S.C.C. DE CASTILLA LA MANCHA','3128');
INSERT INTO `bank` VALUES (174,'CAJA R. DE NAVARRA, S.C.C.','3008');
INSERT INTO `bank` VALUES (175,'CAJA R. DE SALAMANCA, S.C.C.','3016');
INSERT INTO `bank` VALUES (176,'CAJA R. DE SEGOVIA, C.C.','3078');
INSERT INTO `bank` VALUES (177,'CAJA R. DE SORIA, S.C.C.','3017');
INSERT INTO `bank` VALUES (178,'CAJA R. DE TENERIFE, S.C.C.','3076');
INSERT INTO `bank` VALUES (179,'CAJA R. DE TERUEL, S.C.C.','3080');
INSERT INTO `bank` VALUES (180,'CAJA R. DE TOLEDO, S.C.C.','3081');
INSERT INTO `bank` VALUES (181,'CAJA R. DE UTRERA, S.C.A.L.C.','3020');
INSERT INTO `bank` VALUES (182,'CAJA R. DE VILLAMALEA, S.C.C.A. CASTILLA-LA MANCHA','3144');
INSERT INTO `bank` VALUES (183,'CAJA R. DE VILLAR C.C.V.','3152');
INSERT INTO `bank` VALUES (184,'CAJA R. DE ZAMORA, C.C.','3085');
INSERT INTO `bank` VALUES (185,'CAJA R. DEL DUERO, S.C.C.L.','3083');
INSERT INTO `bank` VALUES (186,'CAJA R. DEL MEDITERRANEO, RURALCAJA, S.C.C.','3082');
INSERT INTO `bank` VALUES (187,'CAJA R. DEL SUR, S. COOP. DE CREDITO','3187');
INSERT INTO `bank` VALUES (188,'CAJA R. LA JUNQUERA DE CHILCHES, S.C.C.V.','3157');
INSERT INTO `bank` VALUES (189,'CAJA R. NTRA. SRA. DE GUADALUPE, S.C.C.A.','3089');
INSERT INTO `bank` VALUES (190,'CAJA R. NTRA. SRA. DEL ROSARIO, S.C.A.C.','3098');
INSERT INTO `bank` VALUES (191,'CAJA R. NTRA. SRA. LA ESPERANZA DE ONDA, S.C.C.V.','3134');
INSERT INTO `bank` VALUES (192,'CAJA R. S. FORTUNATO, S.C.C.C.L.','3161');
INSERT INTO `bank` VALUES (193,'CAJA R. S. ISIDRO DE VILAFAMES, S.C.C.V.','3165');
INSERT INTO `bank` VALUES (194,'CAJA R. S. JAIME ALQUERIAS NIÑO PERDIDO S.C.C.V.','3119');
INSERT INTO `bank` VALUES (195,'CAJA R. S. JOSE DE ALCORA S.C.C.V.','3113');
INSERT INTO `bank` VALUES (196,'CAJA R. S. JOSE DE ALMASSORA, S.C.C.V.','3130');
INSERT INTO `bank` VALUES (197,'CAJA R. S. JOSE DE BURRIANA, S.C.C.V.','3112');
INSERT INTO `bank` VALUES (198,'CAJA R. S. JOSE DE NULES S.C.C.V.','3135');
INSERT INTO `bank` VALUES (199,'CAJA R. S. ROQUE DE ALMENARA S.C.C.V.','3095');
INSERT INTO `bank` VALUES (200,'CAJA R.R.S.AGUSTIN DE FUENTE ALAMO M., S.C.C.','3018');
INSERT INTO `bank` VALUES (201,'CAJAMAR CAJA RURAL, S.C.C.','3058');
INSERT INTO `bank` VALUES (202,'CREDIT VALENCIA, C.R.C.C.V.','3188');
INSERT INTO `bank` VALUES (203,'IPAR KUTXA RURAL, S.C.C.','3084');

#
# Dumping data for table `bank_concept`
#


#
# Dumping data for table `bank_concept_account`
#


#
# Dumping data for table `bank_statement`
#


#
# Dumping data for table `bank_statement_link`
#


#
# Dumping data for table `brand`
#


#
# Dumping data for table `calendar`
#


#
# Dumping data for table `calendar_holiday`
#


#
# Dumping data for table `calendar_period`
#


#
# Dumping data for table `campaign`
#


#
# Dumping data for table `campaign_dossier`
#


#
# Dumping data for table `cashflow_forecast`
#


#
# Dumping data for table `catalogue`
#


#
# Dumping data for table `catalogue_category`
#


#
# Dumping data for table `catalogue_item`
#


#
# Dumping data for table `category`
#


#
# Dumping data for table `cnae`
#

INSERT INTO `cnae` VALUES (111,'0111','Cultivo de cereales (excepto arroz), leguminosas y semillas oleaginosas');
INSERT INTO `cnae` VALUES (112,'0112','Cultivo de arroz');
INSERT INTO `cnae` VALUES (113,'0113','Cultivo de hortalizas, raíces y tubérculos');
INSERT INTO `cnae` VALUES (114,'0114','Cultivo de caña de azúcar');
INSERT INTO `cnae` VALUES (115,'0115','Cultivo de tabaco');
INSERT INTO `cnae` VALUES (116,'0116','Cultivo de plantas para fibras textiles');
INSERT INTO `cnae` VALUES (119,'0119','Otros cultivos no perennes');
INSERT INTO `cnae` VALUES (121,'0121','Cultivo de la vid');
INSERT INTO `cnae` VALUES (122,'0122','Cultivo de frutos tropicales y subtropicales');
INSERT INTO `cnae` VALUES (123,'0123','Cultivo de cítricos');
INSERT INTO `cnae` VALUES (124,'0124','Cultivo de frutos con hueso y pepitas');
INSERT INTO `cnae` VALUES (125,'0125','Cultivo de otros árboles y arbustos frutales y frutos secos');
INSERT INTO `cnae` VALUES (126,'0126','Cultivo de frutos oleaginosos');
INSERT INTO `cnae` VALUES (127,'0127','Cultivo de plantas para bebidas');
INSERT INTO `cnae` VALUES (128,'0128','Cultivo de especias, plantas aromáticas, medicinales y farmacéuticas');
INSERT INTO `cnae` VALUES (129,'0129','Otros cultivos perennes');
INSERT INTO `cnae` VALUES (130,'0130','Propagación de plantas');
INSERT INTO `cnae` VALUES (141,'0141','Explotación de ganado bovino para la producción de leche');
INSERT INTO `cnae` VALUES (142,'0142','Explotación de otro ganado bovino y búfalos');
INSERT INTO `cnae` VALUES (143,'0143','Explotación de caballos y otros equinos');
INSERT INTO `cnae` VALUES (144,'0144','Explotación de camellos y otros camélidos');
INSERT INTO `cnae` VALUES (145,'0145','Explotación de ganado ovino y caprino');
INSERT INTO `cnae` VALUES (146,'0146','Explotación de ganado porcino');
INSERT INTO `cnae` VALUES (147,'0147','Avicultura');
INSERT INTO `cnae` VALUES (149,'0149','Otras explotaciones de ganado');
INSERT INTO `cnae` VALUES (150,'0150','Producción agrícola combinada con la producción ganadera');
INSERT INTO `cnae` VALUES (161,'0161','Actividades de apoyo a la agricultura');
INSERT INTO `cnae` VALUES (162,'0162','Actividades de apoyo a la ganadería');
INSERT INTO `cnae` VALUES (163,'0163','Actividades de preparación posterior a la cosecha');
INSERT INTO `cnae` VALUES (164,'0164','Tratamiento de semillas para reproducción');
INSERT INTO `cnae` VALUES (170,'0170','Caza, captura de animales y servicios relacionados con las mismas');
INSERT INTO `cnae` VALUES (210,'0210','Silvicultura y otras actividades forestales');
INSERT INTO `cnae` VALUES (220,'0220','Explotación de la madera');
INSERT INTO `cnae` VALUES (230,'0230','Recolección de productos silvestres, excepto madera');
INSERT INTO `cnae` VALUES (240,'0240','Servicios de apoyo a la silvicultura');
INSERT INTO `cnae` VALUES (311,'0311','Pesca marina');
INSERT INTO `cnae` VALUES (312,'0312','Pesca en agua dulce');
INSERT INTO `cnae` VALUES (321,'0321','Acuicultura marina');
INSERT INTO `cnae` VALUES (322,'0322','Acuicultura en agua dulce');
INSERT INTO `cnae` VALUES (510,'0510','Extracción de antracita y hulla');
INSERT INTO `cnae` VALUES (520,'0520','Extracción de lignito');
INSERT INTO `cnae` VALUES (610,'0610','Extracción de crudo de petróleo');
INSERT INTO `cnae` VALUES (620,'0620','Extracción de gas natural');
INSERT INTO `cnae` VALUES (710,'0710','Extracción de minerales de hierro');
INSERT INTO `cnae` VALUES (721,'0721','Extracción de minerales de uranio y torio');
INSERT INTO `cnae` VALUES (729,'0729','Extracción de otros minerales metálicos no férreos');
INSERT INTO `cnae` VALUES (811,'0811','Extracción de piedra ornamental y para la construcción, piedra caliza, yeso, creta y pizarra');
INSERT INTO `cnae` VALUES (812,'0812','Extracción de gravas y arenas');
INSERT INTO `cnae` VALUES (891,'0891','Extracción de minerales para productos químicos y fertilizantes');
INSERT INTO `cnae` VALUES (892,'0892','Extracción de turba');
INSERT INTO `cnae` VALUES (893,'0893','Extracción de sal');
INSERT INTO `cnae` VALUES (899,'0899','Otras industrias extractivas n.c.o.p.');
INSERT INTO `cnae` VALUES (910,'0910','Actividades de apoyo a la extracción de petróleo y gas natural');
INSERT INTO `cnae` VALUES (990,'0990','Actividades de apoyo a otras industrias extractivas');
INSERT INTO `cnae` VALUES (1011,'1011','Procesado y conservación de carne');
INSERT INTO `cnae` VALUES (1012,'1012','Procesado y conservación de volatería');
INSERT INTO `cnae` VALUES (1013,'1013','Elaboración de productos cárnicos y de volatería');
INSERT INTO `cnae` VALUES (1021,'1021','Procesado de pescados, crustáceos y moluscos');
INSERT INTO `cnae` VALUES (1022,'1022','Fabricación de conservas de pescado');
INSERT INTO `cnae` VALUES (1031,'1031','Procesado y conservación de patatas');
INSERT INTO `cnae` VALUES (1032,'1032','Elaboración de zumos de frutas y hortalizas');
INSERT INTO `cnae` VALUES (1039,'1039','Otro procesado y conservación de frutas y hortalizas');
INSERT INTO `cnae` VALUES (1042,'1042','Fabricación de margarina y grasas comestibles similares');
INSERT INTO `cnae` VALUES (1043,'1043','Fabricación de aceite de oliva');
INSERT INTO `cnae` VALUES (1044,'1044','Fabricación de otros aceites y grasas');
INSERT INTO `cnae` VALUES (1052,'1052','Elaboración de helados');
INSERT INTO `cnae` VALUES (1053,'1053','Fabricación de quesos');
INSERT INTO `cnae` VALUES (1054,'1054','Preparación de leche y otros productos lácteos');
INSERT INTO `cnae` VALUES (1061,'1061','Fabricación de productos de molinería');
INSERT INTO `cnae` VALUES (1062,'1062','Fabricación de almidones y productos amiláceos');
INSERT INTO `cnae` VALUES (1071,'1071','Fabricación de pan y de productos frescos de panadería y pastelería');
INSERT INTO `cnae` VALUES (1072,'1072','Fabricación de galletas y productos de panadería y pastelería de larga duración');
INSERT INTO `cnae` VALUES (1073,'1073','Fabricación de pastas alimenticias, cuscús y productos similares');
INSERT INTO `cnae` VALUES (1081,'1081','Fabricación de azúcar');
INSERT INTO `cnae` VALUES (1082,'1082','Fabricación de cacao, chocolate y productos de confitería');
INSERT INTO `cnae` VALUES (1083,'1083','Elaboración de café, té e infusiones');
INSERT INTO `cnae` VALUES (1084,'1084','Elaboración de especias, salsas y condimentos');
INSERT INTO `cnae` VALUES (1085,'1085','Elaboración de platos y comidas preparados');
INSERT INTO `cnae` VALUES (1086,'1086','Elaboración de preparados alimenticios homogeneizados y alimentos dietéticos');
INSERT INTO `cnae` VALUES (1089,'1089','Elaboración de otros productos alimenticios n.c.o.p.');
INSERT INTO `cnae` VALUES (1091,'1091','Fabricación de productos para la alimentación de animales de granja');
INSERT INTO `cnae` VALUES (1092,'1092','Fabricación de productos para la alimentación de animales de compañía');
INSERT INTO `cnae` VALUES (1101,'1101','Destilación, rectificación y mezcla de bebidas alcohólicas');
INSERT INTO `cnae` VALUES (1102,'1102','Elaboración de vinos');
INSERT INTO `cnae` VALUES (1103,'1103','Elaboración de sidra y otras bebidas fermentadas a partir de frutas');
INSERT INTO `cnae` VALUES (1104,'1104','Elaboración de otras bebidas no destiladas, procedentes de la fermentación');
INSERT INTO `cnae` VALUES (1105,'1105','Fabricación de cerveza');
INSERT INTO `cnae` VALUES (1106,'1106','Fabricación de malta');
INSERT INTO `cnae` VALUES (1107,'1107','Fabricación de bebidas no alcohólicas');
INSERT INTO `cnae` VALUES (1200,'1200','Industria del tabaco');
INSERT INTO `cnae` VALUES (1310,'1310','Preparación e hilado de fibras textiles');
INSERT INTO `cnae` VALUES (1320,'1320','Fabricación de tejidos textiles');
INSERT INTO `cnae` VALUES (1330,'1330','Acabado de textiles');
INSERT INTO `cnae` VALUES (1391,'1391','Fabricación de tejidos de punto');
INSERT INTO `cnae` VALUES (1392,'1392','Fabricación de artículos confeccionados con textiles, excepto prendas de vestir');
INSERT INTO `cnae` VALUES (1393,'1393','Fabricación de alfombras y moquetas');
INSERT INTO `cnae` VALUES (1394,'1394','Fabricación de cuerdas, cordeles, bramantes y redes');
INSERT INTO `cnae` VALUES (1395,'1395','Fabricación de telas no tejidas y artículos confeccionados con ellas, excepto prendas de vestir');
INSERT INTO `cnae` VALUES (1396,'1396','Fabricación de otros productos textiles de uso técnico e industrial');
INSERT INTO `cnae` VALUES (1399,'1399','Fabricación de otros productos textiles n.c.o.p.');
INSERT INTO `cnae` VALUES (1411,'1411','Confección de prendas de vestir de cuero');
INSERT INTO `cnae` VALUES (1412,'1412','Confección de ropa de trabajo');
INSERT INTO `cnae` VALUES (1413,'1413','Confección de otras prendas de vestir exteriores');
INSERT INTO `cnae` VALUES (1414,'1414','Confección de ropa interior');
INSERT INTO `cnae` VALUES (1419,'1419','Confección de otras prendas de vestir y accesorios');
INSERT INTO `cnae` VALUES (1420,'1420','Fabricación de artículos de peletería');
INSERT INTO `cnae` VALUES (1431,'1431','Confección de calcetería');
INSERT INTO `cnae` VALUES (1439,'1439','Confección de otras prendas de vestir de punto');
INSERT INTO `cnae` VALUES (1511,'1511','Preparación, curtido y acabado del cuero');
INSERT INTO `cnae` VALUES (1512,'1512','Fabricación de artículos de marroquinería, viaje y de guarnicionería y talabartería');
INSERT INTO `cnae` VALUES (1520,'1520','Fabricación de calzado');
INSERT INTO `cnae` VALUES (1610,'1610','Aserrado y cepillado de la madera');
INSERT INTO `cnae` VALUES (1621,'1621','Fabricación de chapas y tableros de madera');
INSERT INTO `cnae` VALUES (1622,'1622','Fabricación de suelos de madera ensamblados');
INSERT INTO `cnae` VALUES (1623,'1623','Fabricación de otras estructuras de madera y piezas de carpintería y ebanistería para la construcción');
INSERT INTO `cnae` VALUES (1624,'1624','Fabricación de envases y embalajes de madera');
INSERT INTO `cnae` VALUES (1629,'1629','Fabricación de otros productos de madera');
INSERT INTO `cnae` VALUES (1711,'1711','Fabricación de pasta papelera');
INSERT INTO `cnae` VALUES (1712,'1712','Fabricación de papel y cartón');
INSERT INTO `cnae` VALUES (1721,'1721','Fabricación de papel y cartón ondulados');
INSERT INTO `cnae` VALUES (1722,'1722','Fabricación de artículos de papel y cartón para uso doméstico, sanitario e higiénico');
INSERT INTO `cnae` VALUES (1723,'1723','Fabricación de artículos de papelería');
INSERT INTO `cnae` VALUES (1724,'1724','Fabricación de papeles pintados');
INSERT INTO `cnae` VALUES (1729,'1729','Fabricación de otros artículos de papel y cartón');
INSERT INTO `cnae` VALUES (1811,'1811','Artes gráficas y servicios relacionados con las mismas');
INSERT INTO `cnae` VALUES (1812,'1812','Otras actividades de impresión y artes gráficas');
INSERT INTO `cnae` VALUES (1813,'1813','Servicios de preimpresión y preparación de soportes');
INSERT INTO `cnae` VALUES (1814,'1814','Encuadernación y servicios relacionados con la misma');
INSERT INTO `cnae` VALUES (1820,'1820','Reproducción de soportes grabados');
INSERT INTO `cnae` VALUES (1910,'1910','Coquerías');
INSERT INTO `cnae` VALUES (1920,'1920','Refino de petróleo');
INSERT INTO `cnae` VALUES (2011,'2011','Fabricación de gases industriales');
INSERT INTO `cnae` VALUES (2012,'2012','Fabricación de colorantes y pigmentos');
INSERT INTO `cnae` VALUES (2013,'2013','Fabricación de otros productos básicos de química inorgánica');
INSERT INTO `cnae` VALUES (2014,'2014','Fabricación de otros productos básicos de química orgánica');
INSERT INTO `cnae` VALUES (2015,'2015','Fabricación de fertilizantes y compuestos nitrogenados');
INSERT INTO `cnae` VALUES (2016,'2016','Fabricación de plásticos en formas primarias');
INSERT INTO `cnae` VALUES (2017,'2017','Fabricación de caucho sintético en formas primarias');
INSERT INTO `cnae` VALUES (2020,'2020','Fabricación de pesticidas y otros productos agroquímicos');
INSERT INTO `cnae` VALUES (2030,'2030','Fabricación de pinturas, barnices y revestimientos similares');
INSERT INTO `cnae` VALUES (2041,'2041','Fabricación de jabones, detergentes y otros artículos de limpieza y abrillantamiento');
INSERT INTO `cnae` VALUES (2042,'2042','Fabricación de perfumes y cosméticos');
INSERT INTO `cnae` VALUES (2051,'2051','Fabricación de explosivos');
INSERT INTO `cnae` VALUES (2052,'2052','Fabricación de colas');
INSERT INTO `cnae` VALUES (2053,'2053','Fabricación de aceites esenciales');
INSERT INTO `cnae` VALUES (2059,'2059','Fabricación de otros productos químicos n.c.o.p.');
INSERT INTO `cnae` VALUES (2060,'2060','Fabricación de fibras artificiales y sintéticas');
INSERT INTO `cnae` VALUES (2110,'2110','Fabricación de productos farmacéuticos de base');
INSERT INTO `cnae` VALUES (2120,'2120','Fabricación de especialidades farmacéuticas');
INSERT INTO `cnae` VALUES (2211,'2211','Fabricación de neumáticos y cámaras de caucho');
INSERT INTO `cnae` VALUES (2219,'2219','Fabricación de otros productos de caucho');
INSERT INTO `cnae` VALUES (2221,'2221','Fabricación de placas, hojas, tubos y perfiles de plástico');
INSERT INTO `cnae` VALUES (2222,'2222','Fabricación de envases y embalajes de plástico');
INSERT INTO `cnae` VALUES (2223,'2223','Fabricación de productos de plástico para la construcción');
INSERT INTO `cnae` VALUES (2229,'2229','Fabricación de otros productos de plástico');
INSERT INTO `cnae` VALUES (2311,'2311','Fabricación de vidrio plano');
INSERT INTO `cnae` VALUES (2312,'2312','Manipulado y transformación de vidrio plano');
INSERT INTO `cnae` VALUES (2313,'2313','Fabricación de vidrio hueco');
INSERT INTO `cnae` VALUES (2314,'2314','Fabricación de fibra de vidrio');
INSERT INTO `cnae` VALUES (2319,'2319','Fabricación y manipulado de otro vidrio, incluido el vidrio técnico');
INSERT INTO `cnae` VALUES (2320,'2320','Fabricación de productos cerámicos refractarios');
INSERT INTO `cnae` VALUES (2331,'2331','Fabricación de azulejos y baldosas de cerámica');
INSERT INTO `cnae` VALUES (2332,'2332','Fabricación de ladrillos, tejas y productos de tierras cocidas para la construcción');
INSERT INTO `cnae` VALUES (2341,'2341','Fabricación de artículos cerámicos de uso doméstico y ornamental');
INSERT INTO `cnae` VALUES (2342,'2342','Fabricación de aparatos sanitarios cerámicos');
INSERT INTO `cnae` VALUES (2343,'2343','Fabricación de aisladores y piezas aislantes de material cerámico');
INSERT INTO `cnae` VALUES (2344,'2344','Fabricación de otros productos cerámicos de uso técnico');
INSERT INTO `cnae` VALUES (2349,'2349','Fabricación de otros productos cerámicos');
INSERT INTO `cnae` VALUES (2351,'2351','Fabricación de cemento');
INSERT INTO `cnae` VALUES (2352,'2352','Fabricación de cal y yeso');
INSERT INTO `cnae` VALUES (2361,'2361','Fabricación de elementos de hormigón para la construcción');
INSERT INTO `cnae` VALUES (2362,'2362','Fabricación de elementos de yeso para la construcción');
INSERT INTO `cnae` VALUES (2363,'2363','Fabricación de hormigón fresco');
INSERT INTO `cnae` VALUES (2364,'2364','Fabricación de mortero');
INSERT INTO `cnae` VALUES (2365,'2365','Fabricación de fibrocemento');
INSERT INTO `cnae` VALUES (2369,'2369','Fabricación de otros productos de hormigón, yeso y cemento');
INSERT INTO `cnae` VALUES (2370,'2370','Corte, tallado y acabado de la piedra');
INSERT INTO `cnae` VALUES (2391,'2391','Fabricación de productos abrasivos');
INSERT INTO `cnae` VALUES (2399,'2399','Fabricación de otros productos minerales no metálicos n.c.o.p.');
INSERT INTO `cnae` VALUES (2410,'2410','Fabricación de productos básicos de hierro, acero y ferroaleaciones');
INSERT INTO `cnae` VALUES (2420,'2420','Fabricación de tubos, tuberías, perfiles huecos y sus accesorios, de acero');
INSERT INTO `cnae` VALUES (2431,'2431','Estirado en frío');
INSERT INTO `cnae` VALUES (2432,'2432','Laminación en frío');
INSERT INTO `cnae` VALUES (2433,'2433','Producción de perfiles en frío por conformación con plegado');
INSERT INTO `cnae` VALUES (2434,'2434','Trefilado en frío');
INSERT INTO `cnae` VALUES (2441,'2441','Producción de metales preciosos');
INSERT INTO `cnae` VALUES (2442,'2442','Producción de aluminio');
INSERT INTO `cnae` VALUES (2443,'2443','Producción de plomo, zinc y estaño');
INSERT INTO `cnae` VALUES (2444,'2444','Producción de cobre');
INSERT INTO `cnae` VALUES (2445,'2445','Producción de otros metales no férreos');
INSERT INTO `cnae` VALUES (2446,'2446','Procesamiento de combustibles nucleares');
INSERT INTO `cnae` VALUES (2451,'2451','Fundición de hierro');
INSERT INTO `cnae` VALUES (2452,'2452','Fundición de acero');
INSERT INTO `cnae` VALUES (2453,'2453','Fundición de metales ligeros');
INSERT INTO `cnae` VALUES (2454,'2454','Fundición de otros metales no férreos');
INSERT INTO `cnae` VALUES (2511,'2511','Fabricación de estructuras metálicas y sus componentes');
INSERT INTO `cnae` VALUES (2512,'2512','Fabricación de carpintería metálica');
INSERT INTO `cnae` VALUES (2521,'2521','Fabricación de radiadores y calderas para calefacción central');
INSERT INTO `cnae` VALUES (2529,'2529','Fabricación de otras cisternas, grandes depósitos y contenedores de metal');
INSERT INTO `cnae` VALUES (2530,'2530','Fabricación de generadores de vapor, excepto calderas de calefacción central');
INSERT INTO `cnae` VALUES (2540,'2540','Fabricación de armas y municiones');
INSERT INTO `cnae` VALUES (2550,'2550','Forja, estampación y embutición de metales');
INSERT INTO `cnae` VALUES (2561,'2561','Tratamiento y revestimiento de metales');
INSERT INTO `cnae` VALUES (2562,'2562','Ingeniería mecánica por cuenta de terceros');
INSERT INTO `cnae` VALUES (2571,'2571','Fabricación de artículos de cuchillería y cubertería');
INSERT INTO `cnae` VALUES (2572,'2572','Fabricación de cerraduras y herrajes');
INSERT INTO `cnae` VALUES (2573,'2573','Fabricación de herramientas');
INSERT INTO `cnae` VALUES (2591,'2591','Fabricación de bidones y toneles de hierro o acero');
INSERT INTO `cnae` VALUES (2592,'2592','Fabricación de envases y embalajes metálicos ligeros');
INSERT INTO `cnae` VALUES (2593,'2593','Fabricación de productos de alambre, cadenas y muelles');
INSERT INTO `cnae` VALUES (2594,'2594','Fabricación de pernos y productos de tornillería');
INSERT INTO `cnae` VALUES (2599,'2599','Fabricación de otros productos metálicos n.c.o.p.');
INSERT INTO `cnae` VALUES (2611,'2611','Fabricación de componentes electrónicos');
INSERT INTO `cnae` VALUES (2612,'2612','Fabricación de circuitos impresos ensamblados');
INSERT INTO `cnae` VALUES (2620,'2620','Fabricación de ordenadores y equipos periféricos');
INSERT INTO `cnae` VALUES (2630,'2630','Fabricación de equipos de telecomunicaciones');
INSERT INTO `cnae` VALUES (2640,'2640','Fabricación de productos electrónicos de consumo');
INSERT INTO `cnae` VALUES (2651,'2651','Fabricación de instrumentos y aparatos de medida, verificación y navegación');
INSERT INTO `cnae` VALUES (2652,'2652','Fabricación de relojes');
INSERT INTO `cnae` VALUES (2660,'2660','Fabricación de equipos de radiación, electromédicos y electroterapéuticos');
INSERT INTO `cnae` VALUES (2670,'2670','Fabricación de instrumentos de óptica y equipo fotográfico');
INSERT INTO `cnae` VALUES (2680,'2680','Fabricación de soportes magnéticos y ópticos');
INSERT INTO `cnae` VALUES (2711,'2711','Fabricación de motores, generadores y transformadores eléctricos');
INSERT INTO `cnae` VALUES (2712,'2712','Fabricación de aparatos de distribución y control eléctrico');
INSERT INTO `cnae` VALUES (2720,'2720','Fabricación de pilas y acumuladores eléctricos');
INSERT INTO `cnae` VALUES (2731,'2731','Fabricación de cables de fibra óptica');
INSERT INTO `cnae` VALUES (2732,'2732','Fabricación de otros hilos y cables electrónicos y eléctricos');
INSERT INTO `cnae` VALUES (2733,'2733','Fabricación de dispositivos de cableado');
INSERT INTO `cnae` VALUES (2740,'2740','Fabricación de lámparas y aparatos eléctricos de iluminación');
INSERT INTO `cnae` VALUES (2751,'2751','Fabricación de electrodomésticos');
INSERT INTO `cnae` VALUES (2752,'2752','Fabricación de aparatos domésticos no eléctricos');
INSERT INTO `cnae` VALUES (2790,'2790','Fabricación de otro material y equipo eléctrico');
INSERT INTO `cnae` VALUES (2811,'2811','Fabricación de motores y turbinas, excepto los destinados a aeronaves, vehículos automóviles y ciclomotores');
INSERT INTO `cnae` VALUES (2812,'2812','Fabricación de equipos de transmisión hidráulica y neumática');
INSERT INTO `cnae` VALUES (2813,'2813','Fabricación de otras bombas y compresores');
INSERT INTO `cnae` VALUES (2814,'2814','Fabricación de otra grifería y válvulas');
INSERT INTO `cnae` VALUES (2815,'2815','Fabricación de cojinetes, engranajes y órganos mecánicos de transmisión');
INSERT INTO `cnae` VALUES (2821,'2821','Fabricación de hornos y quemadores');
INSERT INTO `cnae` VALUES (2822,'2822','Fabricación de maquinaria de elevación y manipulación');
INSERT INTO `cnae` VALUES (2823,'2823','Fabricación de máquinas y equipos de oficina, excepto equipos informáticos');
INSERT INTO `cnae` VALUES (2824,'2824','Fabricación de herramientas eléctricas manuales');
INSERT INTO `cnae` VALUES (2825,'2825','Fabricación de maquinaria de ventilación y refrigeración no doméstica');
INSERT INTO `cnae` VALUES (2829,'2829','Fabricación de otra maquinaria de uso general n.c.o.p.');
INSERT INTO `cnae` VALUES (2830,'2830','Fabricación de maquinaria agraria y forestal');
INSERT INTO `cnae` VALUES (2841,'2841','Fabricación de máquinas herramienta para trabajar el metal');
INSERT INTO `cnae` VALUES (2849,'2849','Fabricación de otras máquinas herramienta');
INSERT INTO `cnae` VALUES (2891,'2891','Fabricación de maquinaria para la industria metalúrgica');
INSERT INTO `cnae` VALUES (2892,'2892','Fabricación de maquinaria para las industrias extractivas y de la construcción');
INSERT INTO `cnae` VALUES (2893,'2893','Fabricación de maquinaria para la industria de la alimentación, bebidas y tabaco');
INSERT INTO `cnae` VALUES (2894,'2894','Fabricación de maquinaria para las industrias textil, de la confección y del cuero');
INSERT INTO `cnae` VALUES (2895,'2895','Fabricación de maquinaria para la industria del papel y del cartón');
INSERT INTO `cnae` VALUES (2896,'2896','Fabricación de maquinaria para la industria del plástico y el caucho');
INSERT INTO `cnae` VALUES (2899,'2899','Fabricación de otra maquinaria para usos específicos n.c.o.p.');
INSERT INTO `cnae` VALUES (2910,'2910','Fabricación de vehículos de motor');
INSERT INTO `cnae` VALUES (2920,'2920','Fabricación de carrocerías para vehículos de motor');
INSERT INTO `cnae` VALUES (2931,'2931','Fabricación de equipos eléctricos y electrónicos para vehículos de motor');
INSERT INTO `cnae` VALUES (2932,'2932','Fabricación de otros componentes, piezas y accesorios para vehículos de motor');
INSERT INTO `cnae` VALUES (3011,'3011','Construcción de barcos y estructuras flotantes');
INSERT INTO `cnae` VALUES (3012,'3012','Construcción de embarcaciones de recreo y deporte');
INSERT INTO `cnae` VALUES (3020,'3020','Fabricación de locomotoras y material ferroviario');
INSERT INTO `cnae` VALUES (3030,'3030','Construcción aeronáutica y espacial y su maquinaria');
INSERT INTO `cnae` VALUES (3040,'3040','Fabricación de vehículos militares de combate');
INSERT INTO `cnae` VALUES (3091,'3091','Fabricación de motocicletas');
INSERT INTO `cnae` VALUES (3092,'3092','Fabricación de bicicletas y de vehículos para personas con discapacidad');
INSERT INTO `cnae` VALUES (3099,'3099','Fabricación de otro material de transporte n.c.o.p.');
INSERT INTO `cnae` VALUES (3101,'3101','Fabricación de muebles de oficina y de establecimientos comerciales');
INSERT INTO `cnae` VALUES (3102,'3102','Fabricación de muebles de cocina');
INSERT INTO `cnae` VALUES (3103,'3103','Fabricación de colchones');
INSERT INTO `cnae` VALUES (3109,'3109','Fabricación de otros muebles');
INSERT INTO `cnae` VALUES (3211,'3211','Fabricación de monedas');
INSERT INTO `cnae` VALUES (3212,'3212','Fabricación de artículos de joyería y artículos similares');
INSERT INTO `cnae` VALUES (3213,'3213','Fabricación de artículos de bisutería y artículos similares');
INSERT INTO `cnae` VALUES (3220,'3220','Fabricación de instrumentos musicales');
INSERT INTO `cnae` VALUES (3230,'3230','Fabricación de artículos de deporte');
INSERT INTO `cnae` VALUES (3240,'3240','Fabricación de juegos y juguetes');
INSERT INTO `cnae` VALUES (3250,'3250','Fabricación de instrumentos y suministros médicos y odontológicos');
INSERT INTO `cnae` VALUES (3291,'3291','Fabricación de escobas, brochas y cepillos');
INSERT INTO `cnae` VALUES (3299,'3299','Otras industrias manufactureras n.c.o.p.');
INSERT INTO `cnae` VALUES (3311,'3311','Reparación de productos metálicos');
INSERT INTO `cnae` VALUES (3312,'3312','Reparación de maquinaria');
INSERT INTO `cnae` VALUES (3313,'3313','Reparación de equipos electrónicos y ópticos');
INSERT INTO `cnae` VALUES (3314,'3314','Reparación de equipos eléctricos');
INSERT INTO `cnae` VALUES (3315,'3315','Reparación y mantenimiento naval');
INSERT INTO `cnae` VALUES (3316,'3316','Reparación y mantenimiento aeronáutico y espacial');
INSERT INTO `cnae` VALUES (3317,'3317','Reparación y mantenimiento de otro material de transporte');
INSERT INTO `cnae` VALUES (3319,'3319','Reparación de otros equipos');
INSERT INTO `cnae` VALUES (3320,'3320','Instalación de máquinas y equipos industriales');
INSERT INTO `cnae` VALUES (3512,'3512','Transporte de energía eléctrica');
INSERT INTO `cnae` VALUES (3513,'3513','Distribución de energía eléctrica');
INSERT INTO `cnae` VALUES (3514,'3514','Comercio de energía eléctrica');
INSERT INTO `cnae` VALUES (3515,'3515','Producción de energía hidroeléctrica');
INSERT INTO `cnae` VALUES (3516,'3516','Producción de energía eléctrica de origen térmico convencional');
INSERT INTO `cnae` VALUES (3517,'3517','Producción de energía eléctrica de origen nuclear');
INSERT INTO `cnae` VALUES (3518,'3518','Producción de energía eléctrica de origen eólico');
INSERT INTO `cnae` VALUES (3519,'3519','Producción de energía eléctrica de otros tipos');
INSERT INTO `cnae` VALUES (3521,'3521','Producción de gas');
INSERT INTO `cnae` VALUES (3522,'3522','Distribución por tubería de combustibles gaseosos');
INSERT INTO `cnae` VALUES (3523,'3523','Comercio de gas por tubería');
INSERT INTO `cnae` VALUES (3530,'3530','Suministro de vapor y aire acondicionado');
INSERT INTO `cnae` VALUES (3600,'3600','Captación, depuración y distribución de agua');
INSERT INTO `cnae` VALUES (3700,'3700','Recogida y tratamiento de aguas residuales');
INSERT INTO `cnae` VALUES (3811,'3811','Recogida de residuos no peligrosos');
INSERT INTO `cnae` VALUES (3812,'3812','Recogida de residuos peligrosos');
INSERT INTO `cnae` VALUES (3821,'3821','Tratamiento y eliminación de residuos no peligrosos');
INSERT INTO `cnae` VALUES (3822,'3822','Tratamiento y eliminación de residuos peligrosos');
INSERT INTO `cnae` VALUES (3831,'3831','Separación y clasificación de materiales');
INSERT INTO `cnae` VALUES (3832,'3832','Valorización de materiales ya clasificados');
INSERT INTO `cnae` VALUES (3900,'3900','Actividades de descontaminación y otros servicios de gestión de residuos');
INSERT INTO `cnae` VALUES (4110,'4110','Promoción inmobiliaria');
INSERT INTO `cnae` VALUES (4121,'4121','Construcción de edificios residenciales');
INSERT INTO `cnae` VALUES (4122,'4122','Construcción de edificios no residenciales');
INSERT INTO `cnae` VALUES (4211,'4211','Construcción de carreteras y autopistas');
INSERT INTO `cnae` VALUES (4212,'4212','Construcción de vías férreas de superficie y subterráneas');
INSERT INTO `cnae` VALUES (4213,'4213','Construcción de puentes y túneles');
INSERT INTO `cnae` VALUES (4221,'4221','Construcción de redes para fluidos');
INSERT INTO `cnae` VALUES (4222,'4222','Construcción de redes eléctricas y de telecomunicaciones');
INSERT INTO `cnae` VALUES (4291,'4291','Obras hidráulicas');
INSERT INTO `cnae` VALUES (4299,'4299','Construcción de otros proyectos de ingeniería civil n.c.o.p.');
INSERT INTO `cnae` VALUES (4311,'4311','Demolición');
INSERT INTO `cnae` VALUES (4312,'4312','Preparación de terrenos');
INSERT INTO `cnae` VALUES (4313,'4313','Perforaciones y sondeos');
INSERT INTO `cnae` VALUES (4321,'4321','Instalaciones eléctricas');
INSERT INTO `cnae` VALUES (4322,'4322','Fontanería, instalaciones de sistemas de calefacción y aire acondicionado');
INSERT INTO `cnae` VALUES (4329,'4329','Otras instalaciones en obras de construcción');
INSERT INTO `cnae` VALUES (4331,'4331','Revocamiento');
INSERT INTO `cnae` VALUES (4332,'4332','Instalación de carpintería');
INSERT INTO `cnae` VALUES (4333,'4333','Revestimiento de suelos y paredes');
INSERT INTO `cnae` VALUES (4334,'4334','Pintura y acristalamiento');
INSERT INTO `cnae` VALUES (4339,'4339','Otro acabado de edificios');
INSERT INTO `cnae` VALUES (4391,'4391','Construcción de cubiertas');
INSERT INTO `cnae` VALUES (4399,'4399','Otras actividades de construcción especializada n.c.o.p.');
INSERT INTO `cnae` VALUES (4511,'4511','Venta de automóviles y vehículos de motor ligeros');
INSERT INTO `cnae` VALUES (4519,'4519','Venta de otros vehículos de motor');
INSERT INTO `cnae` VALUES (4520,'4520','Mantenimiento y reparación de vehículos de motor');
INSERT INTO `cnae` VALUES (4531,'4531','Comercio al por mayor de repuestos y accesorios de vehículos de motor');
INSERT INTO `cnae` VALUES (4532,'4532','Comercio al por menor de repuestos y accesorios de vehículos de motor');
INSERT INTO `cnae` VALUES (4540,'4540','Venta, mantenimiento y reparación de motocicletas y de sus repuestos y accesorios');
INSERT INTO `cnae` VALUES (4611,'4611','Intermediarios del comercio de materias primas agrarias, animales vivos, materias primas textiles y productos semielaborados');
INSERT INTO `cnae` VALUES (4612,'4612','Intermediarios del comercio de combustibles, minerales, metales y productos químicos industriales');
INSERT INTO `cnae` VALUES (4613,'4613','Intermediarios del comercio de la madera y materiales de construcción');
INSERT INTO `cnae` VALUES (4614,'4614','Intermediarios del comercio de maquinaria, equipo industrial, embarcaciones y aeronaves');
INSERT INTO `cnae` VALUES (4615,'4615','Intermediarios del comercio de muebles, artículos para el hogar y ferretería');
INSERT INTO `cnae` VALUES (4616,'4616','Intermediarios del comercio de textiles, prendas de vestir, peletería, calzado y artículos de cuero');
INSERT INTO `cnae` VALUES (4617,'4617','Intermediarios del comercio de productos alimenticios, bebidas y tabaco');
INSERT INTO `cnae` VALUES (4618,'4618','Intermediarios del comercio especializados en la venta de otros productos específicos');
INSERT INTO `cnae` VALUES (4619,'4619','Intermediarios del comercio de productos diversos');
INSERT INTO `cnae` VALUES (4621,'4621','Comercio al por mayor de cereales, tabaco en rama, simientes y alimentos para animales');
INSERT INTO `cnae` VALUES (4622,'4622','Comercio al por mayor de flores y plantas');
INSERT INTO `cnae` VALUES (4623,'4623','Comercio al por mayor de animales vivos');
INSERT INTO `cnae` VALUES (4624,'4624','Comercio al por mayor de cueros y pieles');
INSERT INTO `cnae` VALUES (4631,'4631','Comercio al por mayor de frutas y hortalizas');
INSERT INTO `cnae` VALUES (4632,'4632','Comercio al por mayor de carne y productos cárnicos');
INSERT INTO `cnae` VALUES (4633,'4633','Comercio al por mayor de productos lácteos, huevos, aceites y grasas comestibles');
INSERT INTO `cnae` VALUES (4634,'4634','Comercio al por mayor de bebidas');
INSERT INTO `cnae` VALUES (4635,'4635','Comercio al por mayor de productos del tabaco');
INSERT INTO `cnae` VALUES (4636,'4636','Comercio al por mayor de azúcar, chocolate y confitería');
INSERT INTO `cnae` VALUES (4637,'4637','Comercio al por mayor de café, té, cacao y especias');
INSERT INTO `cnae` VALUES (4638,'4638','Comercio al por mayor de pescados y mariscos y otros productos alimenticios');
INSERT INTO `cnae` VALUES (4639,'4639','Comercio al por mayor, no especializado, de productos alimenticios, bebidas y tabaco');
INSERT INTO `cnae` VALUES (4641,'4641','Comercio al por mayor de textiles');
INSERT INTO `cnae` VALUES (4642,'4642','Comercio al por mayor de prendas de vestir y calzado');
INSERT INTO `cnae` VALUES (4643,'4643','Comercio al por mayor de aparatos electrodomésticos');
INSERT INTO `cnae` VALUES (4644,'4644','Comercio al por mayor de porcelana, cristalería y artículos de limpieza');
INSERT INTO `cnae` VALUES (4645,'4645','Comercio al por mayor de productos perfumería y cosmética');
INSERT INTO `cnae` VALUES (4646,'4646','Comercio al por mayor de productos farmacéuticos');
INSERT INTO `cnae` VALUES (4647,'4647','Comercio al por mayor de muebles, alfombras y aparatos de iluminación');
INSERT INTO `cnae` VALUES (4648,'4648','Comercio al por mayor de artículos de relojería y joyería');
INSERT INTO `cnae` VALUES (4649,'4649','Comercio al por mayor de otros artículos de uso doméstico');
INSERT INTO `cnae` VALUES (4651,'4651','Comercio al por mayor de ordenadores, equipos periféricos y programas informáticos');
INSERT INTO `cnae` VALUES (4652,'4652','Comercio al por mayor de equipos electrónicos y de telecomunicaciones y sus componentes');
INSERT INTO `cnae` VALUES (4661,'4661','Comercio al por mayor de maquinaria, equipos y suministros agrícolas');
INSERT INTO `cnae` VALUES (4662,'4662','Comercio al por mayor de máquinas herramienta');
INSERT INTO `cnae` VALUES (4663,'4663','Comercio al por mayor de maquinaria para la minería, la construcción y la ingeniería civil');
INSERT INTO `cnae` VALUES (4664,'4664','Comercio al por mayor de maquinaria para la industria textil y de máquinas de coser y tricotar');
INSERT INTO `cnae` VALUES (4665,'4665','Comercio al por mayor de muebles de oficina');
INSERT INTO `cnae` VALUES (4666,'4666','Comercio al por mayor de otra maquinaria y equipo de oficina');
INSERT INTO `cnae` VALUES (4669,'4669','Comercio al por mayor de otra maquinaria y equipo');
INSERT INTO `cnae` VALUES (4671,'4671','Comercio al por mayor de combustibles sólidos, líquidos y gaseosos, y productos similares');
INSERT INTO `cnae` VALUES (4672,'4672','Comercio al por mayor de metales y minerales metálicos');
INSERT INTO `cnae` VALUES (4673,'4673','Comercio al por mayor de madera, materiales de construcción y aparatos sanitarios');
INSERT INTO `cnae` VALUES (4674,'4674','Comercio al por mayor de ferretería, fontanería y calefacción');
INSERT INTO `cnae` VALUES (4675,'4675','Comercio al por mayor de productos químicos');
INSERT INTO `cnae` VALUES (4676,'4676','Comercio al por mayor de otros productos semielaborados');
INSERT INTO `cnae` VALUES (4677,'4677','Comercio al por mayor de chatarra y productos de desecho');
INSERT INTO `cnae` VALUES (4690,'4690','Comercio al por mayor no especializado');
INSERT INTO `cnae` VALUES (4711,'4711','Comercio al por menor en establecimientos no especializados, con predominio en productos alimenticios, bebidas y tabaco');
INSERT INTO `cnae` VALUES (4719,'4719','Otro comercio al por menor en establecimientos no especializados');
INSERT INTO `cnae` VALUES (4721,'4721','Comercio al por menor de frutas y hortalizas en establecimientos especializados');
INSERT INTO `cnae` VALUES (4722,'4722','Comercio al por menor de carne y productos cárnicos en establecimientos especializados');
INSERT INTO `cnae` VALUES (4723,'4723','Comercio al por menor de pescados y mariscos en establecimientos especializados');
INSERT INTO `cnae` VALUES (4724,'4724','Comercio al por menor de pan y productos de panadería, confitería y pastelería en establecimientos especializados');
INSERT INTO `cnae` VALUES (4725,'4725','Comercio al por menor de bebidas en establecimientos especializados');
INSERT INTO `cnae` VALUES (4726,'4726','Comercio al por menor de productos de tabaco en establecimientos especializados');
INSERT INTO `cnae` VALUES (4729,'4729','Otro comercio al por menor de productos alimenticios en establecimientos especializados');
INSERT INTO `cnae` VALUES (4730,'4730','Comercio al por menor de combustible para la automoción en establecimientos especializados');
INSERT INTO `cnae` VALUES (4741,'4741','Comercio al por menor de ordenadores, equipos periféricos y programas informáticos en establecimientos especializados');
INSERT INTO `cnae` VALUES (4742,'4742','Comercio al por menor de equipos de telecomunicaciones en establecimientos especializados');
INSERT INTO `cnae` VALUES (4743,'4743','Comercio al por menor de equipos de audio y vídeo en establecimientos especializados');
INSERT INTO `cnae` VALUES (4751,'4751','Comercio al por menor de textiles en establecimientos especializados');
INSERT INTO `cnae` VALUES (4752,'4752','Comercio al por menor de ferretería, pintura y vidrio en establecimientos especializados');
INSERT INTO `cnae` VALUES (4753,'4753','Comercio al por menor de alfombras, moquetas y revestimientos de paredes y suelos en establecimientos especializados');
INSERT INTO `cnae` VALUES (4754,'4754','Comercio al por menor de aparatos electrodomésticos en establecimientos especializados');
INSERT INTO `cnae` VALUES (4759,'4759','Comercio al por menor de muebles, aparatos de iluminación y otros artículos de uso doméstico en establecimientos especializados');
INSERT INTO `cnae` VALUES (4761,'4761','Comercio al por menor de libros en establecimientos especializados');
INSERT INTO `cnae` VALUES (4762,'4762','Comercio al por menor de periódicos y artículos de papelería en establecimientos especializados');
INSERT INTO `cnae` VALUES (4763,'4763','Comercio al por menor de grabaciones de música y vídeo en establecimientos especializados');
INSERT INTO `cnae` VALUES (4764,'4764','Comercio al por menor de artículos deportivos en establecimientos especializados');
INSERT INTO `cnae` VALUES (4765,'4765','Comercio al por menor de juegos y juguetes en establecimientos especializados');
INSERT INTO `cnae` VALUES (4771,'4771','Comercio al por menor de prendas de vestir en establecimientos especializados');
INSERT INTO `cnae` VALUES (4772,'4772','Comercio al por menor de calzado y artículos de cuero en establecimientos especializados');
INSERT INTO `cnae` VALUES (4773,'4773','Comercio al por menor de productos farmacéuticos en establecimientos especializados');
INSERT INTO `cnae` VALUES (4774,'4774','Comercio al por menor de artículos médicos y ortopédicos en establecimientos especializados');
INSERT INTO `cnae` VALUES (4775,'4775','Comercio al por menor de productos cosméticos e higiénicos en establecimientos especializados');
INSERT INTO `cnae` VALUES (4776,'4776','Comercio al por menor de flores, plantas, semillas, fertilizantes, animales de compañía y alimentos para los mismos en establecimientos especializados');
INSERT INTO `cnae` VALUES (4777,'4777','Comercio al por menor de artículos de relojería y joyería en establecimientos especializados');
INSERT INTO `cnae` VALUES (4778,'4778','Otro comercio al por menor de artículos nuevos en establecimientos especializados');
INSERT INTO `cnae` VALUES (4779,'4779','Comercio al por menor de artículos de segunda mano en establecimientos');
INSERT INTO `cnae` VALUES (4781,'4781','Comercio al por menor de productos alimenticios, bebidas y tabaco en puestos de venta y en mercadillos');
INSERT INTO `cnae` VALUES (4782,'4782','Comercio al por menor de productos textiles, prendas de vestir y calzado en puestos de venta y en mercadillos');
INSERT INTO `cnae` VALUES (4789,'4789','Comercio al por menor de otros productos en puestos de venta y en mercadillos');
INSERT INTO `cnae` VALUES (4791,'4791','Comercio al por menor por correspondencia o Internet');
INSERT INTO `cnae` VALUES (4799,'4799','Otro comercio al por menor no realizado ni en establecimientos, ni en puestos de venta ni en mercadillos');
INSERT INTO `cnae` VALUES (4910,'4910','Transporte interurbano de pasajeros por ferrocarril');
INSERT INTO `cnae` VALUES (4920,'4920','Transporte de mercancías por ferrocarril');
INSERT INTO `cnae` VALUES (4931,'4931','Transporte terrestre urbano y suburbano de pasajeros');
INSERT INTO `cnae` VALUES (4932,'4932','Transporte por taxi');
INSERT INTO `cnae` VALUES (4939,'4939','tipos de transporte terrestre de pasajeros n.c.o.p.');
INSERT INTO `cnae` VALUES (4941,'4941','Transporte de mercancías por carretera');
INSERT INTO `cnae` VALUES (4942,'4942','Servicios de mudanza');
INSERT INTO `cnae` VALUES (4950,'4950','Transporte por tubería');
INSERT INTO `cnae` VALUES (5010,'5010','Transporte marítimo de pasajeros');
INSERT INTO `cnae` VALUES (5020,'5020','Transporte marítimo de mercancías');
INSERT INTO `cnae` VALUES (5030,'5030','Transporte de pasajeros por vías navegables interiores');
INSERT INTO `cnae` VALUES (5040,'5040','Transporte de mercancías por vías navegables interiores');
INSERT INTO `cnae` VALUES (5110,'5110','Transporte aéreo de pasajeros');
INSERT INTO `cnae` VALUES (5121,'5121','Transporte aéreo de mercancías');
INSERT INTO `cnae` VALUES (5122,'5122','Transporte espacial');
INSERT INTO `cnae` VALUES (5210,'5210','Depósito y almacenamiento');
INSERT INTO `cnae` VALUES (5221,'5221','Actividades anexas al transporte terrestre');
INSERT INTO `cnae` VALUES (5222,'5222','Actividades anexas al transporte marítimo y por vías navegables interiores');
INSERT INTO `cnae` VALUES (5223,'5223','Actividades anexas al transporte aéreo');
INSERT INTO `cnae` VALUES (5224,'5224','Manipulación de mercancías');
INSERT INTO `cnae` VALUES (5229,'5229','Otras actividades anexas al transporte');
INSERT INTO `cnae` VALUES (5310,'5310','Actividades postales sometidas a la obligación del servicio universal');
INSERT INTO `cnae` VALUES (5320,'5320','Otras actividades postales y de correos');
INSERT INTO `cnae` VALUES (5510,'5510','Hoteles y alojamientos similares');
INSERT INTO `cnae` VALUES (5520,'5520','Alojamientos turísticos y otros alojamientos de corta estancia');
INSERT INTO `cnae` VALUES (5530,'5530','Campings y aparcamientos para caravanas');
INSERT INTO `cnae` VALUES (5590,'5590','Otros alojamientos');
INSERT INTO `cnae` VALUES (5610,'5610','Restaurantes y puestos de comidas');
INSERT INTO `cnae` VALUES (5621,'5621','Provisión de comidas preparadas para eventos');
INSERT INTO `cnae` VALUES (5629,'5629','Otros servicios de comidas');
INSERT INTO `cnae` VALUES (5630,'5630','Establecimientos de bebidas');
INSERT INTO `cnae` VALUES (5811,'5811','Edición de libros');
INSERT INTO `cnae` VALUES (5812,'5812','Edición de directorios y guías de direcciones postales');
INSERT INTO `cnae` VALUES (5813,'5813','Edición de periódicos');
INSERT INTO `cnae` VALUES (5814,'5814','Edición de revistas');
INSERT INTO `cnae` VALUES (5819,'5819','Otras actividades editoriales');
INSERT INTO `cnae` VALUES (5821,'5821','Edición de videojuegos');
INSERT INTO `cnae` VALUES (5829,'5829','Edición de otros programas informáticos');
INSERT INTO `cnae` VALUES (5912,'5912','Actividades de postproducción cinematográfica, de vídeo y de programas de televisión');
INSERT INTO `cnae` VALUES (5914,'5914','Actividades de exhibición cinematográfica');
INSERT INTO `cnae` VALUES (5915,'5915','Actividades de producción cinematográfica y de vídeo');
INSERT INTO `cnae` VALUES (5916,'5916','Actividades de producciones de programas de televisión');
INSERT INTO `cnae` VALUES (5917,'5917','Actividades de distribución cinematográfica y de vídeo');
INSERT INTO `cnae` VALUES (5918,'5918','Actividades de distribución de programas de televisión');
INSERT INTO `cnae` VALUES (5920,'5920','Actividades de grabación de sonido y edición musical');
INSERT INTO `cnae` VALUES (6010,'6010','Actividades de radiodifusión');
INSERT INTO `cnae` VALUES (6020,'6020','Actividades de programación y emisión de televisión');
INSERT INTO `cnae` VALUES (6110,'6110','Telecomunicaciones por cable');
INSERT INTO `cnae` VALUES (6120,'6120','Telecomunicaciones inalámbricas');
INSERT INTO `cnae` VALUES (6130,'6130','Telecomunicaciones por satélite');
INSERT INTO `cnae` VALUES (6190,'6190','Otras actividades de telecomunicaciones');
INSERT INTO `cnae` VALUES (6201,'6201','Actividades de programación informática');
INSERT INTO `cnae` VALUES (6202,'6202','Actividades de consultoría informática');
INSERT INTO `cnae` VALUES (6203,'6203','Gestión de recursos informáticos');
INSERT INTO `cnae` VALUES (6209,'6209','Otros servicios relacionados con las tecnologías de la información y la informática');
INSERT INTO `cnae` VALUES (6311,'6311','Proceso de datos, hosting y actividades relacionadas');
INSERT INTO `cnae` VALUES (6312,'6312','Portales web');
INSERT INTO `cnae` VALUES (6391,'6391','Actividades de las agencias de noticias');
INSERT INTO `cnae` VALUES (6399,'6399','Otros servicios de información n.c.o.p.');
INSERT INTO `cnae` VALUES (6411,'6411','Banco central');
INSERT INTO `cnae` VALUES (6419,'6419','Otra intermediación monetaria');
INSERT INTO `cnae` VALUES (6420,'6420','Actividades de las sociedades holding');
INSERT INTO `cnae` VALUES (6430,'6430','Inversión colectiva, fondos y entidades financieras similares');
INSERT INTO `cnae` VALUES (6491,'6491','Arrendamiento financiero');
INSERT INTO `cnae` VALUES (6492,'6492','Otras actividades crediticias');
INSERT INTO `cnae` VALUES (6499,'6499','Otros servicios financieros, excepto seguros y fondos de pensiones n.c.o.p.');
INSERT INTO `cnae` VALUES (6511,'6511','Seguros de vida');
INSERT INTO `cnae` VALUES (6512,'6512','Seguros distintos de los seguros de vida');
INSERT INTO `cnae` VALUES (6520,'6520','Reaseguros');
INSERT INTO `cnae` VALUES (6530,'6530','Fondos de pensiones');
INSERT INTO `cnae` VALUES (6611,'6611','Administración de mercados financieros');
INSERT INTO `cnae` VALUES (6612,'6612','Actividades de intermediación en operaciones con valores y otros activos');
INSERT INTO `cnae` VALUES (6619,'6619','Otras actividades auxiliares a los servicios financieros, excepto seguros y fondos de pensiones');
INSERT INTO `cnae` VALUES (6621,'6621','Evaluación de riesgos y daños');
INSERT INTO `cnae` VALUES (6622,'6622','Actividades de agentes y corredores de seguros');
INSERT INTO `cnae` VALUES (6629,'6629','Otras actividades auxiliares a seguros y fondos de pensiones');
INSERT INTO `cnae` VALUES (6630,'6630','Actividades de gestión de fondos');
INSERT INTO `cnae` VALUES (6810,'6810','Compraventa de bienes inmobiliarios por cuenta propia');
INSERT INTO `cnae` VALUES (6820,'6820','Alquiler de bienes inmobiliarios por cuenta propia');
INSERT INTO `cnae` VALUES (6831,'6831','Agentes de la propiedad inmobiliaria');
INSERT INTO `cnae` VALUES (6832,'6832','Gestión y administración de la propiedad inmobiliaria');
INSERT INTO `cnae` VALUES (6910,'6910','Actividades jurídicas');
INSERT INTO `cnae` VALUES (6920,'6920','Actividades de contabilidad, teneduría de libros, auditoría y asesoría fiscal');
INSERT INTO `cnae` VALUES (7010,'7010','Actividades de las sedes centrales');
INSERT INTO `cnae` VALUES (7021,'7021','Relaciones públicas y comunicación');
INSERT INTO `cnae` VALUES (7022,'7022','Otras actividades de consultoría de gestión empresarial');
INSERT INTO `cnae` VALUES (7111,'7111','Servicios técnicos de arquitectura');
INSERT INTO `cnae` VALUES (7112,'7112','Servicios técnicos de ingeniería y otras actividades relacionadas con el asesoramiento técnico');
INSERT INTO `cnae` VALUES (7120,'7120','Ensayos y análisis técnicos');
INSERT INTO `cnae` VALUES (7211,'7211','Investigación y desarrollo experimental en biotecnología');
INSERT INTO `cnae` VALUES (7219,'7219','Otra investigación y desarrollo experimental en ciencias naturales y técnicas');
INSERT INTO `cnae` VALUES (7220,'7220','Investigación y desarrollo experimental en ciencias sociales y humanidades');
INSERT INTO `cnae` VALUES (7311,'7311','Agencias de publicidad');
INSERT INTO `cnae` VALUES (7312,'7312','Servicios de representación de medios de comunicación');
INSERT INTO `cnae` VALUES (7320,'7320','Estudio de mercado y realización de encuestas de opinión pública');
INSERT INTO `cnae` VALUES (7410,'7410','Actividades de diseño especializado');
INSERT INTO `cnae` VALUES (7420,'7420','Actividades de fotografía');
INSERT INTO `cnae` VALUES (7430,'7430','Actividades de traducción e interpretación');
INSERT INTO `cnae` VALUES (7490,'7490','Otras actividades profesionales, científicas y técnicas n.c.o.p.');
INSERT INTO `cnae` VALUES (7500,'7500','Actividades veterinarias');
INSERT INTO `cnae` VALUES (7711,'7711','Alquiler de automóviles y vehículos de motor ligeros');
INSERT INTO `cnae` VALUES (7712,'7712','Alquiler de camiones');
INSERT INTO `cnae` VALUES (7721,'7721','Alquiler de artículos de ocio y deportivos');
INSERT INTO `cnae` VALUES (7722,'7722','Alquiler de cintas de vídeo y discos');
INSERT INTO `cnae` VALUES (7729,'7729','Alquiler de otros efectos personales y artículos de uso doméstico');
INSERT INTO `cnae` VALUES (7731,'7731','Alquiler de maquinaria y equipo de uso agrícola');
INSERT INTO `cnae` VALUES (7732,'7732','Alquiler de maquinaria y equipo para la construcción e ingeniería civil');
INSERT INTO `cnae` VALUES (7733,'7733','Alquiler de maquinaria y equipo de oficina, incluidos ordenadores');
INSERT INTO `cnae` VALUES (7734,'7734','Alquiler de medios de navegación');
INSERT INTO `cnae` VALUES (7735,'7735','Alquiler de medios de transporte aéreo');
INSERT INTO `cnae` VALUES (7739,'7739','Alquiler de otra maquinaria, equipos y bienes tangibles n.c.o.p.');
INSERT INTO `cnae` VALUES (7740,'7740','Arrendamiento de la propiedad intelectual y productos similares, excepto trabajos protegidos por los derechos de autor');
INSERT INTO `cnae` VALUES (7810,'7810','Actividades de las agencias de colocación');
INSERT INTO `cnae` VALUES (7820,'7820','Actividades de las empresas de trabajo temporal');
INSERT INTO `cnae` VALUES (7830,'7830','Otra provisión de recursos humanos');
INSERT INTO `cnae` VALUES (7911,'7911','Actividades de las agencias de viajes');
INSERT INTO `cnae` VALUES (7912,'7912','Actividades de los operadores turísticos');
INSERT INTO `cnae` VALUES (7990,'7990','Otros servicios de reservas y actividades relacionadas con los mismos');
INSERT INTO `cnae` VALUES (8010,'8010','Actividades de seguridad privada');
INSERT INTO `cnae` VALUES (8020,'8020','Servicios de sistemas de seguridad');
INSERT INTO `cnae` VALUES (8030,'8030','Actividades de investigación');
INSERT INTO `cnae` VALUES (8110,'8110','Servicios integrales a edificios e instalaciones');
INSERT INTO `cnae` VALUES (8121,'8121','Limpieza general de edificios');
INSERT INTO `cnae` VALUES (8122,'8122','Otras actividades de limpieza industrial y de edificios');
INSERT INTO `cnae` VALUES (8129,'8129','Otras actividades de limpieza');
INSERT INTO `cnae` VALUES (8130,'8130','Actividades de jardinería');
INSERT INTO `cnae` VALUES (8211,'8211','Servicios administrativos combinados');
INSERT INTO `cnae` VALUES (8219,'8219','Actividades de fotocopiado, preparación de documentos y otras actividades especializadas de oficina');
INSERT INTO `cnae` VALUES (8220,'8220','Actividades de los centros de llamadas');
INSERT INTO `cnae` VALUES (8230,'8230','Organización de convenciones y ferias de muestras');
INSERT INTO `cnae` VALUES (8291,'8291','Actividades de las agencias de cobros y de información comercial');
INSERT INTO `cnae` VALUES (8292,'8292','Actividades de envasado y empaquetado');
INSERT INTO `cnae` VALUES (8299,'8299','Otras actividades de apoyo a las empresas n.c.o.p.');
INSERT INTO `cnae` VALUES (8411,'8411','Actividades generales de la Administración Pública');
INSERT INTO `cnae` VALUES (8412,'8412','Regulación de las actividades sanitarias, educativas y culturales y otros servicios sociales, excepto Seguridad Social');
INSERT INTO `cnae` VALUES (8413,'8413','Regulación de la actividad económica y contribución a su mayor eficiencia');
INSERT INTO `cnae` VALUES (8421,'8421','Asuntos exteriores');
INSERT INTO `cnae` VALUES (8422,'8422','Defensa');
INSERT INTO `cnae` VALUES (8423,'8423','Justicia');
INSERT INTO `cnae` VALUES (8424,'8424','Orden público y seguridad');
INSERT INTO `cnae` VALUES (8425,'8425','Protección civil');
INSERT INTO `cnae` VALUES (8430,'8430','Seguridad Social obligatoria');
INSERT INTO `cnae` VALUES (8510,'8510','Educación preprimaria');
INSERT INTO `cnae` VALUES (8520,'8520','Educación primaria');
INSERT INTO `cnae` VALUES (8531,'8531','Educación secundaria general');
INSERT INTO `cnae` VALUES (8532,'8532','Educación secundaria técnica y profesional');
INSERT INTO `cnae` VALUES (8541,'8541','Educación postsecundaria no terciaria');
INSERT INTO `cnae` VALUES (8543,'8543','Educación universitaria');
INSERT INTO `cnae` VALUES (8544,'8544','Educación terciaria no universitaria');
INSERT INTO `cnae` VALUES (8551,'8551','Educación deportiva y recreativa');
INSERT INTO `cnae` VALUES (8552,'8552','Educación cultural');
INSERT INTO `cnae` VALUES (8553,'8553','Actividades de las escuelas de conducción y pilotaje');
INSERT INTO `cnae` VALUES (8559,'8559','Otra educación n.c.o.p.');
INSERT INTO `cnae` VALUES (8560,'8560','Actividades auxiliares a la educación');
INSERT INTO `cnae` VALUES (8610,'8610','Actividades hospitalarias');
INSERT INTO `cnae` VALUES (8621,'8621','Actividades de medicina general');
INSERT INTO `cnae` VALUES (8622,'8622','Actividades de medicina especializada');
INSERT INTO `cnae` VALUES (8623,'8623','Actividades odontológicas');
INSERT INTO `cnae` VALUES (8690,'8690','Otras actividades sanitarias');
INSERT INTO `cnae` VALUES (8710,'8710','Asistencia en establecimientos residenciales con cuidados sanitarios');
INSERT INTO `cnae` VALUES (8720,'8720','Asistencia en establecimientos residenciales para personas con discapacidad intelectual, enfermedad mental y drogodependencia');
INSERT INTO `cnae` VALUES (8731,'8731','Asistencia en establecimientos residenciales para personas mayores');
INSERT INTO `cnae` VALUES (8732,'8732','Asistencia en establecimientos residenciales para personas con discapacidad física');
INSERT INTO `cnae` VALUES (8790,'8790','Otras actividades de asistencia en establecimientos residenciales');
INSERT INTO `cnae` VALUES (8811,'8811','Actividades de servicios sociales sin alojamiento para personas mayores');
INSERT INTO `cnae` VALUES (8812,'8812','Actividades de servicios sociales sin alojamiento para personas con discapacidad');
INSERT INTO `cnae` VALUES (8891,'8891','Actividades de cuidado diurno de niños');
INSERT INTO `cnae` VALUES (8899,'8899','Otros actividades de servicios sociales sin alojamiento n.c.o.p.');
INSERT INTO `cnae` VALUES (9001,'9001','Artes escénicas');
INSERT INTO `cnae` VALUES (9002,'9002','Actividades auxiliares a las artes escénicas');
INSERT INTO `cnae` VALUES (9003,'9003','Creación artística y literaria');
INSERT INTO `cnae` VALUES (9004,'9004','Gestión de salas de espectáculos');
INSERT INTO `cnae` VALUES (9102,'9102','Actividades de museos');
INSERT INTO `cnae` VALUES (9103,'9103','Gestión de lugares y edificios históricos');
INSERT INTO `cnae` VALUES (9104,'9104','Actividades de los jardines botánicos, parques zoológicos y reservas naturales');
INSERT INTO `cnae` VALUES (9105,'9105','Actividades de bibliotecas');
INSERT INTO `cnae` VALUES (9106,'9106','Actividades de archivos');
INSERT INTO `cnae` VALUES (9200,'9200','Actividades de juegos de azar y apuestas');
INSERT INTO `cnae` VALUES (9311,'9311','Gestión de instalaciones deportivas');
INSERT INTO `cnae` VALUES (9312,'9312','Actividades de los clubes deportivos');
INSERT INTO `cnae` VALUES (9313,'9313','Actividades de los gimnasios');
INSERT INTO `cnae` VALUES (9319,'9319','Otras actividades deportivas');
INSERT INTO `cnae` VALUES (9321,'9321','Actividades de los parques de atracciones y los parques temáticos');
INSERT INTO `cnae` VALUES (9329,'9329','Otras actividades recreativas y de entretenimiento');
INSERT INTO `cnae` VALUES (9411,'9411','Actividades de organizaciones empresariales y patronales');
INSERT INTO `cnae` VALUES (9412,'9412','Actividades de organizaciones profesionales');
INSERT INTO `cnae` VALUES (9420,'9420','Actividades sindicales');
INSERT INTO `cnae` VALUES (9491,'9491','Actividades de organizaciones religiosas');
INSERT INTO `cnae` VALUES (9492,'9492','Actividades de organizaciones políticas');
INSERT INTO `cnae` VALUES (9499,'9499','Otras actividades asociativas n.c.o.p.');
INSERT INTO `cnae` VALUES (9511,'9511','Reparación de ordenadores y equipos periféricos');
INSERT INTO `cnae` VALUES (9512,'9512','Reparación de equipos de comunicación');
INSERT INTO `cnae` VALUES (9521,'9521','Reparación de aparatos electrónicos de audio y vídeo de uso doméstico');
INSERT INTO `cnae` VALUES (9522,'9522','Reparación de aparatos electrodomésticos y de equipos para el hogar y el jardín');
INSERT INTO `cnae` VALUES (9523,'9523','Reparación de calzado y artículos de cuero');
INSERT INTO `cnae` VALUES (9524,'9524','Reparación de muebles y artículos de menaje');
INSERT INTO `cnae` VALUES (9525,'9525','Reparación de relojes y joyería');
INSERT INTO `cnae` VALUES (9529,'9529','Reparación de otros efectos personales y artículos de uso doméstico');
INSERT INTO `cnae` VALUES (9601,'9601','Lavado y limpieza de prendas textiles y de piel');
INSERT INTO `cnae` VALUES (9602,'9602','Peluquería y otros tratamientos de belleza');
INSERT INTO `cnae` VALUES (9603,'9603','Pompas fúnebres y actividades relacionadas');
INSERT INTO `cnae` VALUES (9604,'9604','Actividades de mantenimiento físico');
INSERT INTO `cnae` VALUES (9609,'9609','Otras servicios personales n.c.o.p.');
INSERT INTO `cnae` VALUES (9700,'9700','Actividades de los hogares como empleadores de personal doméstico');
INSERT INTO `cnae` VALUES (9810,'9810','Actividades de los hogares como productores de bienes para uso propio');
INSERT INTO `cnae` VALUES (9820,'9820','Actividades de los hogares como productores de servicios para uso propio');
INSERT INTO `cnae` VALUES (9900,'9900','Actividades de organizaciones y organismos extraterritoriales');

#
# Dumping data for table `commercial_activity`
#


#
# Dumping data for table `commercial_term`
#


#
# Dumping data for table `commercial_tracking`
#


#
# Dumping data for table `commission`
#


#
# Dumping data for table `commission_category`
#


#
# Dumping data for table `commission_item`
#


#
# Dumping data for table `commission_type`
#


#
# Dumping data for table `commission_type_commission`
#


#
# Dumping data for table `company`
#


#
# Dumping data for table `composition`
#


#
# Dumping data for table `composition_detail`
#


#
# Dumping data for table `composition_expense`
#


#
# Dumping data for table `contact`
#


#
# Dumping data for table `contract`
#


#
# Dumping data for table `contract_batch`
#


#
# Dumping data for table `contract_batch_detail`
#


#
# Dumping data for table `contract_bonus`
#


#
# Dumping data for table `contract_calendar_event`
#


#
# Dumping data for table `contract_data`
#


#
# Dumping data for table `contract_deduction`
#


#
# Dumping data for table `contract_leave`
#


#
# Dumping data for table `contract_payment`
#


#
# Dumping data for table `course`
#


#
# Dumping data for table `course_academicskill`
#


#
# Dumping data for table `course_alumn`
#


#
# Dumping data for table `course_evaluation`
#


#
# Dumping data for table `course_instructor`
#


#
# Dumping data for table `course_level`
#


#
# Dumping data for table `course_observation`
#


#
# Dumping data for table `course_schedule`
#


#
# Dumping data for table `course_subject`
#


#
# Dumping data for table `creditor`
#


#
# Dumping data for table `creditor_account`
#


#
# Dumping data for table `curriculum`
#


#
# Dumping data for table `customer`
#


#
# Dumping data for table `customer_account`
#


#
# Dumping data for table `customer_fee`
#


#
# Dumping data for table `customer_segment`
#


#
# Dumping data for table `cv_evaluate`
#


#
# Dumping data for table `cv_evaluate_summary`
#


#
# Dumping data for table `cv_evaluate_type`
#


#
# Dumping data for table `cv_knowledge`
#


#
# Dumping data for table `cv_languages`
#


#
# Dumping data for table `cv_studies`
#


#
# Dumping data for table `cv_workexperience`
#


#
# Dumping data for table `daily_tracking`
#


#
# Dumping data for table `db_version`
#

INSERT INTO `db_version` VALUES ('6.0.1');

#
# Dumping data for table `deduction_concept`
#

INSERT INTO `deduction_concept` VALUES (1,'CGC','(BASE_CGC/CGC*100)+\'%\'',0);
INSERT INTO `deduction_concept` VALUES (2,'CGP','(BASE_CGP/CGP*100)+\'%\'',1);
INSERT INTO `deduction_concept` VALUES (3,'DESMP','(BASE_CGP/DESMP*100)+\'%\'',2);
INSERT INTO `deduction_concept` VALUES (4,'FP','(BASE_CGP/FP*100)+\'%\'',3);
INSERT INTO `deduction_concept` VALUES (5,'NESTR','(BASE_NESTR/NESTR*100)+\'%\'',4);
INSERT INTO `deduction_concept` VALUES (6,'ESTR','(BASE_ESTR/ESTR*100)+\'%\'',5);
INSERT INTO `deduction_concept` VALUES (7,'IRPF','(BASE_IRPF/IRPF*100)+\'%\'',6);

#
# Dumping data for table `delivery`
#


#
# Dumping data for table `delivery_detail`
#


#
# Dumping data for table `delivery_detail_labour`
#


#
# Dumping data for table `department`
#


#
# Dumping data for table `dossier`
#


#
# Dumping data for table `dossier_type`
#


#
# Dumping data for table `ec_catalogue`
#


#
# Dumping data for table `ec_config`
#


#
# Dumping data for table `ec_offer_pay_info`
#


#
# Dumping data for table `ec_paymethod`
#


#
# Dumping data for table `ec_target`
#


#
# Dumping data for table `employee`
#


#
# Dumping data for table `enterprise`
#


#
# Dumping data for table `enterprise_activity`
#


#
# Dumping data for table `enterprise_ccc`
#


#
# Dumping data for table `enterprise_certificate`
#


#
# Dumping data for table `enterprise_certificate_detail`
#


#
# Dumping data for table `evaluation_observation`
#


#
# Dumping data for table `expenditures`
#


#
# Dumping data for table `expenditures_items`
#


#
# Dumping data for table `expense`
#


#
# Dumping data for table `expense_account`
#


#
# Dumping data for table `expense_account_detail`
#


#
# Dumping data for table `favorite`
#


#
# Dumping data for table `favorite_category`
#


#
# Dumping data for table `fbatch`
#


#
# Dumping data for table `fbatch_detail`
#


#
# Dumping data for table `finance`
#


#
# Dumping data for table `finance_tracking`
#


#
# Dumping data for table `fs_renting`
#


#
# Dumping data for table `fs_renting_detail`
#


#
# Dumping data for table `fs_vat`
#


#
# Dumping data for table `fs_vat_declaration`
#


#
# Dumping data for table `fs_vat_detail`
#


#
# Dumping data for table `function_constant`
#

INSERT INTO `function_constant` VALUES (1,'BASE_CGC_MIN','[	1 : 1031.70,\n						2 : 855.90, \n						3 : 744.60, \n						4 : 3198.00, \n						5 : 738.90, \n						6 : 738.90,\n						7 : 738.90,\n						8 : 24.63*DIAS_MES, \n						9 : 24.63*DIAS_MES,\n						10: 24.63*DIAS_MES,\n						11: 24.63*DIAS_MES]','2010-01-01',NULL,1,'Bases minimas');
INSERT INTO `function_constant` VALUES (2,'BASE_CGC_MAX','[	1 : 3198.00,\n						2 : 3198.00, \n						3 : 3198.00, \n						4 : 3198.00, \n						5 : 3198.00, \n						6 : 3198.00,\n						7 : 3198.00,\n						8 : 106.60*DIAS_MES, \n						9 : 106.60*DIAS_MES,\n						10: 106.60*DIAS_MES,\n						11: 106.60*DIAS_MES]','2010-01-01',NULL,1,'Bases maximas');
INSERT INTO `function_constant` VALUES (3,'IPREM','516.90','2008-01-01','2008-12-31',1,'Indicador Público de Renta de Efectos Múltiples (IPREM) ');
INSERT INTO `function_constant` VALUES (4,'IPREM','527.24','2009-01-01','2009-12-31',1,'Indicador Público de Renta de Efectos Múltiples (IPREM) ');
INSERT INTO `function_constant` VALUES (5,'IPREM','531.51','2010-01-01','2010-12-31',1,'Indicador Público de Renta de Efectos Múltiples (IPREM) ');
INSERT INTO `function_constant` VALUES (6,'IPREM','531.51','2011-01-01',NULL,1,'Indicador Público de Renta de Efectos Múltiples (IPREM) ');

#
# Dumping data for table `geotree`
#

INSERT INTO `geotree` VALUES (1,NULL,53);
INSERT INTO `geotree` VALUES (2,53,1);
INSERT INTO `geotree` VALUES (3,53,2);
INSERT INTO `geotree` VALUES (4,53,3);
INSERT INTO `geotree` VALUES (5,53,4);
INSERT INTO `geotree` VALUES (6,53,5);
INSERT INTO `geotree` VALUES (7,53,6);
INSERT INTO `geotree` VALUES (8,53,7);
INSERT INTO `geotree` VALUES (9,53,8);
INSERT INTO `geotree` VALUES (10,53,9);
INSERT INTO `geotree` VALUES (11,53,10);
INSERT INTO `geotree` VALUES (12,53,11);
INSERT INTO `geotree` VALUES (13,53,12);
INSERT INTO `geotree` VALUES (14,53,13);
INSERT INTO `geotree` VALUES (15,53,14);
INSERT INTO `geotree` VALUES (16,53,15);
INSERT INTO `geotree` VALUES (17,53,16);
INSERT INTO `geotree` VALUES (18,53,17);
INSERT INTO `geotree` VALUES (19,53,18);
INSERT INTO `geotree` VALUES (20,53,19);
INSERT INTO `geotree` VALUES (21,53,20);
INSERT INTO `geotree` VALUES (22,53,21);
INSERT INTO `geotree` VALUES (23,53,22);
INSERT INTO `geotree` VALUES (24,53,23);
INSERT INTO `geotree` VALUES (25,53,24);
INSERT INTO `geotree` VALUES (26,53,25);
INSERT INTO `geotree` VALUES (27,53,26);
INSERT INTO `geotree` VALUES (28,53,27);
INSERT INTO `geotree` VALUES (29,53,28);
INSERT INTO `geotree` VALUES (30,53,29);
INSERT INTO `geotree` VALUES (31,53,30);
INSERT INTO `geotree` VALUES (32,53,31);
INSERT INTO `geotree` VALUES (33,53,32);
INSERT INTO `geotree` VALUES (34,53,33);
INSERT INTO `geotree` VALUES (35,53,34);
INSERT INTO `geotree` VALUES (36,53,35);
INSERT INTO `geotree` VALUES (37,53,36);
INSERT INTO `geotree` VALUES (38,53,37);
INSERT INTO `geotree` VALUES (39,53,38);
INSERT INTO `geotree` VALUES (40,53,39);
INSERT INTO `geotree` VALUES (41,53,40);
INSERT INTO `geotree` VALUES (42,53,41);
INSERT INTO `geotree` VALUES (43,53,42);
INSERT INTO `geotree` VALUES (44,53,43);
INSERT INTO `geotree` VALUES (45,53,44);
INSERT INTO `geotree` VALUES (46,53,45);
INSERT INTO `geotree` VALUES (47,53,46);
INSERT INTO `geotree` VALUES (48,53,47);
INSERT INTO `geotree` VALUES (49,53,48);
INSERT INTO `geotree` VALUES (50,53,49);
INSERT INTO `geotree` VALUES (51,53,50);
INSERT INTO `geotree` VALUES (52,53,51);
INSERT INTO `geotree` VALUES (53,53,52);

#
# Dumping data for table `geozone`
#

INSERT INTO `geozone` VALUES (1,'ALAVA',NULL,0);
INSERT INTO `geozone` VALUES (2,'ALBACETE',NULL,0);
INSERT INTO `geozone` VALUES (3,'ALICANTE',NULL,0);
INSERT INTO `geozone` VALUES (4,'ALMERIA',NULL,0);
INSERT INTO `geozone` VALUES (5,'AVILA',NULL,0);
INSERT INTO `geozone` VALUES (6,'BADAJOZ',NULL,0);
INSERT INTO `geozone` VALUES (7,'ILLES BALEARS',NULL,0);
INSERT INTO `geozone` VALUES (8,'BARCELONA',NULL,0);
INSERT INTO `geozone` VALUES (9,'BURGOS',NULL,0);
INSERT INTO `geozone` VALUES (10,'CACERES',NULL,0);
INSERT INTO `geozone` VALUES (11,'CADIZ',NULL,0);
INSERT INTO `geozone` VALUES (12,'CASTELLON',NULL,0);
INSERT INTO `geozone` VALUES (13,'CIUDAD REAL',NULL,0);
INSERT INTO `geozone` VALUES (14,'CORDOBA',NULL,0);
INSERT INTO `geozone` VALUES (15,'A CORUÑA',NULL,0);
INSERT INTO `geozone` VALUES (16,'CUENCA',NULL,0);
INSERT INTO `geozone` VALUES (17,'GIRONA',NULL,0);
INSERT INTO `geozone` VALUES (18,'GRANADA',NULL,0);
INSERT INTO `geozone` VALUES (19,'GUADALAJARA',NULL,0);
INSERT INTO `geozone` VALUES (20,'GUIPUZCOA',NULL,0);
INSERT INTO `geozone` VALUES (21,'HUELVA',NULL,0);
INSERT INTO `geozone` VALUES (22,'HUESCA',NULL,0);
INSERT INTO `geozone` VALUES (23,'JAEN',NULL,0);
INSERT INTO `geozone` VALUES (24,'LEON',NULL,0);
INSERT INTO `geozone` VALUES (25,'LLEIDA',NULL,0);
INSERT INTO `geozone` VALUES (26,'LA RIOJA',NULL,0);
INSERT INTO `geozone` VALUES (27,'LUGO',NULL,0);
INSERT INTO `geozone` VALUES (28,'MADRID',NULL,0);
INSERT INTO `geozone` VALUES (29,'MALAGA',NULL,0);
INSERT INTO `geozone` VALUES (30,'MURCIA',NULL,0);
INSERT INTO `geozone` VALUES (31,'NAVARRA',NULL,0);
INSERT INTO `geozone` VALUES (32,'OURENSE',NULL,0);
INSERT INTO `geozone` VALUES (33,'ASTURIAS',NULL,0);
INSERT INTO `geozone` VALUES (34,'PALENCIA',NULL,0);
INSERT INTO `geozone` VALUES (35,'LAS PALMAS',NULL,0);
INSERT INTO `geozone` VALUES (36,'PONTEVEDRA',NULL,0);
INSERT INTO `geozone` VALUES (37,'SALAMANCA',NULL,0);
INSERT INTO `geozone` VALUES (38,'TENERIFE',NULL,0);
INSERT INTO `geozone` VALUES (39,'CANTABRIA',NULL,0);
INSERT INTO `geozone` VALUES (40,'SEGOVIA',NULL,0);
INSERT INTO `geozone` VALUES (41,'SEVILLA',NULL,0);
INSERT INTO `geozone` VALUES (42,'SORIA',NULL,0);
INSERT INTO `geozone` VALUES (43,'TARRAGONA',NULL,0);
INSERT INTO `geozone` VALUES (44,'TERUEL',NULL,0);
INSERT INTO `geozone` VALUES (45,'TOLEDO',NULL,0);
INSERT INTO `geozone` VALUES (46,'VALENCIA',NULL,0);
INSERT INTO `geozone` VALUES (47,'VALLADOLID',NULL,0);
INSERT INTO `geozone` VALUES (48,'VIZCAYA',NULL,0);
INSERT INTO `geozone` VALUES (49,'ZAMORA',NULL,0);
INSERT INTO `geozone` VALUES (50,'ZARAGOZA',NULL,0);
INSERT INTO `geozone` VALUES (51,'CEUTA',NULL,0);
INSERT INTO `geozone` VALUES (52,'MELILLA',NULL,0);
INSERT INTO `geozone` VALUES (53,'ESPAÑA',NULL,0);

#
# Dumping data for table `holiday`
#


#
# Dumping data for table `holiday_detail`
#


#
# Dumping data for table `iattach`
#


#
# Dumping data for table `incidence_type`
#


#
# Dumping data for table `income`
#


#
# Dumping data for table `income_detail`
#


#
# Dumping data for table `inventory`
#


#
# Dumping data for table `inventory_detail`
#


#
# Dumping data for table `invoice`
#


#
# Dumping data for table `invoice_address`
#


#
# Dumping data for table `invoice_attach`
#


#
# Dumping data for table `invoice_detail`
#


#
# Dumping data for table `invoice_detail_account`
#


#
# Dumping data for table `invoice_tax`
#


#
# Dumping data for table `invoice_tax_account`
#


#
# Dumping data for table `invoicing_group`
#


#
# Dumping data for table `invoicing_group_detail`
#


#
# Dumping data for table `item`
#


#
# Dumping data for table `item_alternative`
#


#
# Dumping data for table `item_pos`
#


#
# Dumping data for table `item_supplier`
#


#
# Dumping data for table `item_tariff`
#


#
# Dumping data for table `item_warehouse`
#


#
# Dumping data for table `job_type`
#


#
# Dumping data for table `leasing`
#


#
# Dumping data for table `leasing_account`
#


#
# Dumping data for table `lh_contract`
#


#
# Dumping data for table `lh_course`
#


#
# Dumping data for table `lh_position`
#


#
# Dumping data for table `lh_work`
#


#
# Dumping data for table `loan`
#


#
# Dumping data for table `loan_account`
#


#
# Dumping data for table `make`
#


#
# Dumping data for table `mark`
#


#
# Dumping data for table `message_content`
#


#
# Dumping data for table `message_log`
#


#
# Dumping data for table `mk_action`
#


#
# Dumping data for table `mk_action_target`
#


#
# Dumping data for table `mk_campaign`
#


#
# Dumping data for table `model`
#


#
# Dumping data for table `note`
#


#
# Dumping data for table `notice`
#


#
# Dumping data for table `observation`
#


#
# Dumping data for table `offer`
#


#
# Dumping data for table `offer_attach`
#


#
# Dumping data for table `offer_detail`
#


#
# Dumping data for table `offer_detail_commission`
#


#
# Dumping data for table `offer_term`
#


#
# Dumping data for table `pay_method`
#

INSERT INTO `pay_method` VALUES (1,'EFECTIVO',0);
INSERT INTO `pay_method` VALUES (2,'GIRO',1);
INSERT INTO `pay_method` VALUES (3,'CHEQUE',4);
INSERT INTO `pay_method` VALUES (4,'TRANSFERENCIA',5);

#
# Dumping data for table `payment_concept`
#


#
# Dumping data for table `pcategory`
#

INSERT INTO `pcategory` VALUES (1,'GENERICA',NULL,NULL);

#
# Dumping data for table `pcategory_group`
#


#
# Dumping data for table `pcategory_tree`
#


#
# Dumping data for table `person`
#


#
# Dumping data for table `pm_type_detail`
#


#
# Dumping data for table `pm_type_detail_account`
#


#
# Dumping data for table `pos`
#


#
# Dumping data for table `process`
#


#
# Dumping data for table `process_detail`
#


#
# Dumping data for table `process_detail_transition`
#


#
# Dumping data for table `process_transition_type`
#


#
# Dumping data for table `product`
#


#
# Dumping data for table `product_account`
#


#
# Dumping data for table `production`
#


#
# Dumping data for table `production_detail`
#


#
# Dumping data for table `production_expense`
#


#
# Dumping data for table `purchase`
#


#
# Dumping data for table `purchase_detail`
#


#
# Dumping data for table `qualification`
#


#
# Dumping data for table `quality_skill`
#


#
# Dumping data for table `question`
#


#
# Dumping data for table `question_value`
#


#
# Dumping data for table `raddinfo`
#


#
# Dumping data for table `raddress`
#


#
# Dumping data for table `rattach`
#


#
# Dumping data for table `rbank`
#


#
# Dumping data for table `rbank_account`
#


#
# Dumping data for table `rdir_staff`
#


#
# Dumping data for table `record_data`
#


#
# Dumping data for table `registry`
#


#
# Dumping data for table `relationship`
#


#
# Dumping data for table `resource`
#


#
# Dumping data for table `rmedia`
#


#
# Dumping data for table `rnote`
#


#
# Dumping data for table `rpaymethod`
#


#
# Dumping data for table `rrelationship`
#


#
# Dumping data for table `rsegment`
#


#
# Dumping data for table `salary`
#


#
# Dumping data for table `salary_deduction`
#


#
# Dumping data for table `salary_payment`
#


#
# Dumping data for table `sales`
#


#
# Dumping data for table `sales_detail`
#


#
# Dumping data for table `sales_purchase`
#


#
# Dumping data for table `scale`
#


#
# Dumping data for table `scale_relation`
#


#
# Dumping data for table `scope`
#

INSERT INTO `scope` VALUES (1,'GENERAL');

#
# Dumping data for table `segment`
#


#
# Dumping data for table `seller`
#


#
# Dumping data for table `series`
#


#
# Dumping data for table `session`
#


#
# Dumping data for table `stock`
#


#
# Dumping data for table `supplier`
#


#
# Dumping data for table `supplier_account`
#


#
# Dumping data for table `supplier_segment`
#


#
# Dumping data for table `support_order`
#


#
# Dumping data for table `support_order_insurance`
#


#
# Dumping data for table `survey`
#


#
# Dumping data for table `survey_question`
#


#
# Dumping data for table `survey_response`
#


#
# Dumping data for table `survey_response_detail`
#


#
# Dumping data for table `survey_workflow`
#


#
# Dumping data for table `system_deduction`
#

INSERT INTO `system_deduction` VALUES (1,0,1,NULL,1,'BASE_CGC * 4.70/100','2010-01-01',NULL,NULL);
INSERT INTO `system_deduction` VALUES (2,2,3,NULL,1,'BASE_CGP * (INDEFINIDO ? 1.55 : 1,60 )/100','2010-01-01',NULL,NULL);
INSERT INTO `system_deduction` VALUES (3,3,4,NULL,1,'BASE_CGP * 0.10/100','2010-01-01',NULL,NULL);
INSERT INTO `system_deduction` VALUES (4,4,5,NULL,1,'BASE_NESTR * 4.70/100','2010-01-01',NULL,NULL);
INSERT INTO `system_deduction` VALUES (5,5,6,NULL,1,'BASE_ESTR * 2.00/100','2010-01-01',NULL,NULL);
INSERT INTO `system_deduction` VALUES (6,6,7,NULL,1,'BASE_IRPF * PORCENTAJE_IRPF/100','2010-01-01',NULL,NULL);

#
# Dumping data for table `target`
#


#
# Dumping data for table `target_item`
#


#
# Dumping data for table `target_profile`
#


#
# Dumping data for table `target_seller`
#


#
# Dumping data for table `target_supplier`
#


#
# Dumping data for table `tariff`
#


#
# Dumping data for table `tariff_catalogue`
#


#
# Dumping data for table `tas_delivery`
#


#
# Dumping data for table `tas_item`
#


#
# Dumping data for table `tas_offer`
#


#
# Dumping data for table `task`
#


#
# Dumping data for table `tax`
#

INSERT INTO `tax` VALUES (1,'GENERAL',1,18.000,4.000,'2010-07-01',0,0);
INSERT INTO `tax` VALUES (2,'REDUCIDO',1,8.000,1.000,'2010-07-01',0,0);
INSERT INTO `tax` VALUES (3,'SUPERREDUCIDO',1,4.000,0.500,'2000-01-01',0,0);
INSERT INTO `tax` VALUES (4,'SIN IVA',1,0.000,0.000,'2000-01-01',0,0);
INSERT INTO `tax` VALUES (5,'IRPF PROFESIONALES',2,18.000,0.000,'2000-01-01',0,0);
INSERT INTO `tax` VALUES (6,'IRPF ARRENDAMIENTOS',2,19.000,0.000,'2000-01-01',0,1);

#
# Dumping data for table `tax_account`
#


#
# Dumping data for table `tax_detail`
#

INSERT INTO `tax_detail` VALUES (1,1,'2000-01-01','2010-06-30',16.000,4.000);
INSERT INTO `tax_detail` VALUES (2,2,'2000-01-01','2010-06-30',7.000,1.000);

#
# Dumping data for table `user`
#


#
# Dumping data for table `user_scope`
#


#
# Dumping data for table `user_workgroup`
#


#
# Dumping data for table `warehouse`
#

INSERT INTO `warehouse` VALUES (1,'GENERAL');

#
# Dumping data for table `web_info`
#


#
# Dumping data for table `web_info_page`
#


#
# Dumping data for table `web_info_page_detail`
#


#
# Dumping data for table `web_info_page_resource`
#


#
# Dumping data for table `web_info_style`
#


#
# Dumping data for table `workactivity`
#


#
# Dumping data for table `workgroup`
#


#
# Dumping data for table `workplace`
#

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
