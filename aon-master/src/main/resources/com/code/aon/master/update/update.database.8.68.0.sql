# Database: aon_master
# Version: Actualizacion de la version 8.67.0 a la version 8.68.0.
# Created by: rtrepiana
# Creation Date: 02/10/2016 

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

SET @AGREEMENT=IFNULL((SELECT MAX(`id`) + 1 FROM `agreement`),1);

# CONVENIO COLECTIVO PARA EL COMERCIO DEL METAL MADRID
INSERT INTO `agreement` (`id`, `domain`, `calendar`, `description`) VALUES (@AGREEMENT,0,NULL,'CONVENIO COLECTIVO PARA EL COMERCIO DEL METAL MADRID');

SET @AGREEMENT_LEVEL=IFNULL((SELECT MAX(`id`) FROM `agreement_level`),0);
INSERT INTO `agreement_level` (`id`,`domain`, `agreement`, `description`) VALUES ((@AGREEMENT_LEVEL+1),0,@AGREEMENT,'1');
INSERT INTO `agreement_level` (`id`,`domain`, `agreement`, `description`) VALUES ((@AGREEMENT_LEVEL+2),0,@AGREEMENT,'2.1');
INSERT INTO `agreement_level` (`id`,`domain`, `agreement`, `description`) VALUES ((@AGREEMENT_LEVEL+3),0,@AGREEMENT,'2.2');
INSERT INTO `agreement_level` (`id`,`domain`, `agreement`, `description`) VALUES ((@AGREEMENT_LEVEL+4),0,@AGREEMENT,'2.3');
INSERT INTO `agreement_level` (`id`,`domain`, `agreement`, `description`) VALUES ((@AGREEMENT_LEVEL+5),0,@AGREEMENT,'3.1');
INSERT INTO `agreement_level` (`id`,`domain`, `agreement`, `description`) VALUES ((@AGREEMENT_LEVEL+6),0,@AGREEMENT,'3.2');
INSERT INTO `agreement_level` (`id`,`domain`, `agreement`, `description`) VALUES ((@AGREEMENT_LEVEL+7),0,@AGREEMENT,'4.1');
INSERT INTO `agreement_level` (`id`,`domain`, `agreement`, `description`) VALUES ((@AGREEMENT_LEVEL+8),0,@AGREEMENT,'4.2');
INSERT INTO `agreement_level` (`id`,`domain`, `agreement`, `description`) VALUES ((@AGREEMENT_LEVEL+9),0,@AGREEMENT,'5.1');
INSERT INTO `agreement_level` (`id`,`domain`, `agreement`, `description`) VALUES ((@AGREEMENT_LEVEL+10),0,@AGREEMENT,'5.2');
INSERT INTO `agreement_level` (`id`,`domain`, `agreement`, `description`) VALUES ((@AGREEMENT_LEVEL+11),0,@AGREEMENT,'6.1');
INSERT INTO `agreement_level` (`id`,`domain`, `agreement`, `description`) VALUES ((@AGREEMENT_LEVEL+12),0,@AGREEMENT,'6.2');
INSERT INTO `agreement_level` (`id`,`domain`, `agreement`, `description`) VALUES ((@AGREEMENT_LEVEL+13),0,@AGREEMENT,'7.1');
INSERT INTO `agreement_level` (`id`,`domain`, `agreement`, `description`) VALUES ((@AGREEMENT_LEVEL+14),0,@AGREEMENT,'7.2');
INSERT INTO `agreement_level` (`id`,`domain`, `agreement`, `description`) VALUES ((@AGREEMENT_LEVEL+15),0,@AGREEMENT,'8');

INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+1),'Analista de Sistemas');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+1),'Licenciados');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+1),'Jefe de Personal');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+1),'Jefe de Compras');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+1),'Grado Universitario');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+1),'Jefe de Ventas');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+1),'Ingenieros');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+1),'Encargado General');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+2),'Jefe 1ª Administrativo');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+3),'Jefe de Almacén');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+3),'Jefe de Sucursal');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+3),'Dibujante');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+3),'Analista de Aplicaciones');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+3),'Ayudantes Técnicos');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+3),'Proyectista');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+3),'Programador de Sistemas');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+4),'Delineante');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+4),'Escaparatista');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+4),'Rotulista');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+5),'Jefe de Grupo');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+5),'Jefe de Sección Admvo');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+5),'Programador de Aplicaciones');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+5),'Jefe de Taller');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+6),'Visitador');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+7),'Dependiente Mayor');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+7),'Viajante');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+7),'Oficial de 1ª Admvo');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+7),'Contable');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+7),'Cajero');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+7),'Operador de Ordenador');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+7),'Jefe de Sección');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+7),'Practicante / ATS');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+8),'Corredor de Plaza');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+8),'Dependiente (22 años)');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+8),'Conductor \"E');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+8),'Oficial de 2ª Admvo');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+8),'Conductor \"D');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+8),'Engarcado de Establecimiento');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+9),'Cobrador de Ventas C?edito');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+9),'Ofical 1');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+9),'Conductor \"C');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+10),'Capataz');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+10),'Conserje');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+10),'Cobrador');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+10),'Mozo Especializado');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+10),'Auxiliares');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+10),'Conductor \"A1');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+10),'Oficial 2');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+10),'Personal de Limpieza a Tiemplo Completo');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+10),'Telefonista');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+10),'Conductor \"B');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+10),'Oficial 3');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+11),'Contrato para la Formación Primer Año');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+12),'Contrato para la Formación Segundo Año');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+13),'Aspirante hasta 18 años');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+14),'Aspirante de 16 y 17 años');
INSERT INTO `agreement_level_category` (`domain`, `agreement_level`, `description`) VALUES (0,(@AGREEMENT_LEVEL+15),'Personal de Limpieza Tiempo Parcial');

INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'SALARIO_MENSUAL',(@AGREEMENT_LEVEL+5),'ROUND(1272.57 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'SALARIO_MENSUAL',(@AGREEMENT_LEVEL+6),'ROUND(1216.34 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'SALARIO_MENSUAL',(@AGREEMENT_LEVEL+7),'ROUND(1144.94 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'SALARIO_MENSUAL',(@AGREEMENT_LEVEL+8),'ROUND(1066.63 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'SALARIO_MENSUAL',(@AGREEMENT_LEVEL+2),'ROUND(1565.06 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'SALARIO_MENSUAL',(@AGREEMENT_LEVEL+9),'ROUND(992.89 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'SALARIO_MENSUAL',(@AGREEMENT_LEVEL+3),'ROUND(1449.00 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'SALARIO_MENSUAL',(@AGREEMENT_LEVEL+10),'ROUND(918.28 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'SALARIO_MENSUAL',(@AGREEMENT_LEVEL+4),'ROUND(1336.63 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'SALARIO_MENSUAL',(@AGREEMENT_LEVEL+11),'ROUND(611.54 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'SALARIO_MENSUAL',(@AGREEMENT_LEVEL+12),'ROUND(648.54 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'SALARIO_MENSUAL',(@AGREEMENT_LEVEL+13),'ROUND(704.55 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'SALARIO_MENSUAL',(@AGREEMENT_LEVEL+14),'ROUND(593.46 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'SALARIO_HORA',(@AGREEMENT_LEVEL+15),'ROUND(6.46 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'SALARIO_MENSUAL',(@AGREEMENT_LEVEL+1),'ROUND(1740.65 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'VALOR_CUATRIENIO_MENSUAL',(@AGREEMENT_LEVEL+12),'ROUND(0.00 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'VALOR_CUATRIENIO_MENSUAL',(@AGREEMENT_LEVEL+13),'ROUND(0.00 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'VALOR_CUATRIENIO_MENSUAL',(@AGREEMENT_LEVEL+14),'ROUND(0.00 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'VALOR_CUATRIENIO_MENSUAL',(@AGREEMENT_LEVEL+15),'ROUND(0.00 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'VALOR_CUATRIENIO_MENSUAL',(@AGREEMENT_LEVEL+11),'ROUND(0.00 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'VALOR_CUATRIENIO_MENSUAL',(@AGREEMENT_LEVEL+5),'ROUND(59.13 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'VALOR_CUATRIENIO_MENSUAL',(@AGREEMENT_LEVEL+6),'ROUND(59.13 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'VALOR_CUATRIENIO_MENSUAL',(@AGREEMENT_LEVEL+7),'ROUND(51.68 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'VALOR_CUATRIENIO_MENSUAL',(@AGREEMENT_LEVEL+1),'ROUND(81.66 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'VALOR_CUATRIENIO_MENSUAL',(@AGREEMENT_LEVEL+8),'ROUND(51.68 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'VALOR_CUATRIENIO_MENSUAL',(@AGREEMENT_LEVEL+2),'ROUND(67.35 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'VALOR_CUATRIENIO_MENSUAL',(@AGREEMENT_LEVEL+9),'ROUND(43.46 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'VALOR_CUATRIENIO_MENSUAL',(@AGREEMENT_LEVEL+3),'ROUND(67.35 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'VALOR_CUATRIENIO_MENSUAL',(@AGREEMENT_LEVEL+10),'ROUND(43.46 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'VALOR_CUATRIENIO_MENSUAL',(@AGREEMENT_LEVEL+4),'ROUND(67.35 * INCREMENTO, 2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'PLUS_TRANSPORTE_URBANO',(@AGREEMENT_LEVEL+15),'ROUND( 0.00 * INCREMENTO,2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'PLUS_TRANSPORTE_URBANO',(@AGREEMENT_LEVEL+12),'ROUND( 55.71 * INCREMENTO,2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'PLUS_TRANSPORTE_URBANO',(@AGREEMENT_LEVEL+13),'ROUND( 55.71 * INCREMENTO,2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'PLUS_TRANSPORTE_URBANO',(@AGREEMENT_LEVEL+14),'ROUND( 55.71 * INCREMENTO,2)','2015-04-01',NULL);
INSERT INTO `agreement_level_data` (`domain`, `name`, `agreement_level`, `expression`, `start_date`, `end_date`) VALUES (0,'PLUS_TRANSPORTE_URBANO',(@AGREEMENT_LEVEL+11),'ROUND( 55.71 * INCREMENTO,2)','2015-04-01',NULL);



INSERT INTO `agreement_data` (`domain`, `name`, `agreement`, `expression`, `start_date`, `end_date`) VALUES (0,'INCREMENTO',@AGREEMENT,'1.00','2015-04-01','2016-03-31');
INSERT INTO `agreement_data` (`domain`, `name`, `agreement`, `expression`, `start_date`, `end_date`) VALUES (0,'INCREMENTO',@AGREEMENT,'1.012','2016-04-01','2017-03-31');
INSERT INTO `agreement_data` (`domain`, `name`, `agreement`, `expression`, `start_date`, `end_date`) VALUES (0,'INCREMENTO',@AGREEMENT,'1.042','2017-04-01',NULL);
INSERT INTO `agreement_data` (`domain`, `name`, `agreement`, `expression`, `start_date`, `end_date`) VALUES (0,'PLUS_TRANSPORTE_URBANO',@AGREEMENT,'ROUND( 90.77 * INCREMENTO,2)','2015-04-01',NULL);
INSERT INTO `agreement_data` (`domain`, `name`, `agreement`, `expression`, `start_date`, `end_date`) VALUES (0,'MEDIA_DIETA',@AGREEMENT,'ROUND(10.44 * INCREMENTO,2)','2015-04-01',NULL);
INSERT INTO `agreement_data` (`domain`, `name`, `agreement`, `expression`, `start_date`, `end_date`) VALUES (0,'DIETA_COMPLETA',@AGREEMENT,'ROUND(43.33 * INCREMENTO,2)','2015-04-01',NULL);
INSERT INTO `agreement_data` (`domain`, `name`, `agreement`, `expression`, `start_date`, `end_date`) VALUES (0,'IMPORTE_HORA_EXTRA',@AGREEMENT,'ROUND(SALARIO_MENSUAL * 14 /1778 * 1.75,2) ','2015-04-01',NULL);

SET @SALARIO_MENSUAL=( SELECT `id` FROM `payment_concept` WHERE `domain` = 0 AND `id` > 0 AND `code`='SALARIO_BASE' AND `expression` LIKE '%SALARIO_MENSUAL%');
INSERT INTO `agreement_payment` (`domain`, `agreement`, `payment_concept`, `type`, `expression`, `description`, `start_date`, `end_date`, `month`, `salary_type`, `description_decorable`, `irpf_expression`, `quote_expression`) VALUES (0,@AGREEMENT,@SALARIO_MENSUAL,NULL,'/*user*/SALARIO_MENSUAL/**/ * DIAS_TRABAJADOS / DIAS_MES ','SALARIO BASE MENSUAL','2015-04-01',NULL,NULL,0,0,NULL,NULL);

SET @SALARIO_HORA=( SELECT `id` FROM `payment_concept` WHERE `domain` = 0 AND `id` > 0 AND `code`='SALARIO_BASE' AND `expression` LIKE '%SALARIO_HORA%' LIMIT 1);
INSERT INTO `agreement_payment` (`domain`, `agreement`, `payment_concept`, `type`, `expression`, `description`, `start_date`, `end_date`, `month`, `salary_type`, `description_decorable`, `irpf_expression`, `quote_expression`) VALUES (0,@AGREEMENT,@SALARIO_HORA,NULL,'( !TIEMPO_COMPLETO  && isdef SALARIO_HORA ) ? /*user*/SALARIO_HORA * HORAS_TRABAJADAS/**/: REMOVE()','SALARIO BASE ( @{SALARIO_HORA} € X @{HORAS_TRABAJADAS} HORAS )','2015-04-01',NULL,NULL,0,0,NULL,NULL);

SET @PAGA_EXTRA=( SELECT `id` FROM `payment_concept` WHERE `domain` = 0 AND `id` > 0 AND `code`='PAGA_EXTRA');
SET @EXTRA_DICIEMBRE=(SELECT MAX(`id`) + 1 FROM `agreement_payment`);
INSERT INTO `agreement_payment` (`id`, `domain`, `agreement`, `payment_concept`, `type`, `expression`, `description`, `start_date`, `end_date`, `month`, `salary_type`, `description_decorable`, `irpf_expression`, `quote_expression`) VALUES (@EXTRA_DICIEMBRE,0,@AGREEMENT,@PAGA_EXTRA,NULL,'INPUT(\"/*user*/SALARIO_BASE + ANTIGUEDAD + PAGA_BENEFICIOS/**/\",PAGA_EXTRA_HELP)','PAGA EXTRAORDINARIA DICIEMBRE','2015-04-01',NULL,11,1,0,'_P','(SALARIO_BASE + ANTIGUEDAD)/12');
SET @EXTRA_JULIO=(SELECT MAX(`id`) + 1 FROM `agreement_payment`);
INSERT INTO `agreement_payment` (`id`, `domain`, `agreement`, `payment_concept`, `type`, `expression`, `description`, `start_date`, `end_date`, `month`, `salary_type`, `description_decorable`, `irpf_expression`, `quote_expression`) VALUES (@EXTRA_JULIO,0,@AGREEMENT,@PAGA_EXTRA,NULL,'INPUT(\"/*user*/SALARIO_BASE + ANTIGUEDAD + PAGA_BENEFICIOS/**/\",PAGA_EXTRA_HELP)','PAGA EXTRAORDINARIA JULIO','2015-04-01',NULL,6,1,0,'_P','(SALARIO_BASE + ANTIGUEDAD)/12');

SET @ANTIGUEDAD=( SELECT `id` FROM `payment_concept` WHERE `domain` = 0 AND `id` > 0 AND `code`='ANTIGUEDAD');
INSERT INTO `agreement_payment` (`domain`, `agreement`, `payment_concept`, `type`, `expression`, `description`, `start_date`, `end_date`, `month`, `salary_type`, `description_decorable`, `irpf_expression`, `quote_expression`) VALUES (0,@AGREEMENT,@ANTIGUEDAD,NULL,'INPUT(\"/*user*/ANTIGÜEDAD(VALOR_CUATRIENIO_MENSUAL * DIAS_TRABAJADOS / DIAS_MES, CUATRIENIO) /**/\",ANTIGUEDAD_HELP)','AUMENTO PERIÓDICO POR TIEMPO DE SERVICIO','2015-04-01',NULL,NULL,0,0,NULL,NULL);

SET @PLUS_EXTRA_SALARIAL=( SELECT `id` FROM `payment_concept` WHERE `domain` = 0 AND `id` > 0 AND `code`='PLUS_EXTRA_SALARIAL');
INSERT INTO `agreement_payment` (`domain`, `agreement`, `payment_concept`, `type`, `expression`, `description`, `start_date`, `end_date`, `month`, `salary_type`, `description_decorable`, `irpf_expression`, `quote_expression`) VALUES (0,@AGREEMENT,@PLUS_EXTRA_SALARIAL,NULL,'PLUS_TRANSPORTE_URBANO * DIAS_TRABAJADOS / DIAS_MES','PLUS EXTRA SALARIAL DE TRANSPORTE URBANO','2015-04-01',NULL,NULL,0,0,NULL,NULL);

SET @PERNOCTAS=( SELECT `id` FROM `payment_concept` WHERE `domain` = 0 AND `id` > 0 AND `type` = 43);
INSERT INTO `agreement_payment` (`domain`, `agreement`, `payment_concept`, `type`, `expression`, `description`, `start_date`, `end_date`, `month`, `salary_type`, `description_decorable`, `irpf_expression`, `quote_expression`) VALUES (0,@AGREEMENT,@PERNOCTAS,NULL,'DIETA_COMPLETA * DIAS_DIETA_COMPLETA','DIETAS Y VIAJES','2015-04-01',NULL,NULL,0,0,'EXCESO(53.34 * DIAS_DIETA_COMPLETA)','EXCESO(53.34 * DIAS_DIETA_COMPLETA)');

SET @PAYMENT_CONCEPT=(SELECT MIN(`id`) - 1 FROM `payment_concept`);
INSERT INTO `payment_concept` (`id`, `domain`, `code`, `description`, `type`, `description_decorable`, `expression`, `irpf_expression`, `quote_expression`) VALUES (@PAYMENT_CONCEPT,0,'__65','COMPLEMENTO POR MEJORA DE PRODUCTIVIDAD',1,0,'CHECK((DIAS_MES - DIAS_TRABAJADOS) <= 1.00, \"AUSENCIAS POR MÁS DE OCHO HORAS\");  SALARIO_BASE * 1/100','_P','_P');
INSERT INTO `agreement_payment` (`domain`, `agreement`, `payment_concept`, `type`, `expression`, `description`, `start_date`, `end_date`, `month`, `salary_type`, `description_decorable`, `irpf_expression`, `quote_expression`) VALUES (0,@AGREEMENT,@PAYMENT_CONCEPT,NULL,NULL,NULL,'2015-04-01',NULL,NULL,0,0,NULL,NULL);

SET @PAYMENT_CONCEPT=(SELECT MIN(`id`) - 1 FROM `payment_concept`);
INSERT INTO `payment_concept` (`id`, `domain`, `code`, `description`, `type`, `description_decorable`, `expression`, `irpf_expression`, `quote_expression`) VALUES (@PAYMENT_CONCEPT,0,'PAGA_BENEFICIOS','A CUENTA PAGA DE VENTAS O BENEFICIOS',4,0,'( SALARIO_BASE + ANTIGUEDAD ) / 14','_P','( SALARIO_BASE + ANTIGUEDAD ) / 12');
INSERT INTO `agreement_payment` (`domain`, `agreement`, `payment_concept`, `type`, `expression`, `description`, `start_date`, `end_date`, `month`, `salary_type`, `description_decorable`, `irpf_expression`, `quote_expression`) VALUES (0,@AGREEMENT,@PAYMENT_CONCEPT,NULL,NULL,NULL,'2015-04-01',NULL,NULL,0,0,NULL,NULL);

SET @PAYMENT_CONCEPT=(SELECT MIN(`id`) - 1 FROM `payment_concept`);
INSERT INTO `payment_concept` (`id`, `domain`, `code`, `description`, `type`, `description_decorable`, `expression`, `irpf_expression`, `quote_expression`) VALUES (@PAYMENT_CONCEPT,0,'__67','1/2 DIETAS Y VIAJES',45,0,'MEDIA_DIETA * DIAS_MEDIA_DIETA','_P','_P');
INSERT INTO `agreement_payment` (`domain`, `agreement`, `payment_concept`, `type`, `expression`, `description`, `start_date`, `end_date`, `month`, `salary_type`, `description_decorable`, `irpf_expression`, `quote_expression`) VALUES (0,@AGREEMENT,@PAYMENT_CONCEPT,NULL,NULL,NULL,'2015-04-01',NULL,NULL,0,0,NULL,NULL);

SET @HORAS_EXTRAS=( SELECT `id` FROM `payment_concept` WHERE `domain` = 0 AND `id` > 0 AND `type` = 3);
INSERT INTO `agreement_payment` (`domain`, `agreement`, `payment_concept`, `type`, `expression`, `description`, `start_date`, `end_date`, `month`, `salary_type`, `description_decorable`, `irpf_expression`, `quote_expression`) VALUES (0,@AGREEMENT,@HORAS_EXTRAS,NULL,'IMPORTE_HORA_EXTRA * HORAS_EXTRAS','HORAS EXTRAORDINARIAS','2015-04-01',NULL,NULL,0,0,NULL,NULL);
INSERT INTO `agreement_payment` (`domain`, `agreement`, `payment_concept`, `type`, `expression`, `description`, `start_date`, `end_date`, `month`, `salary_type`, `description_decorable`, `irpf_expression`, `quote_expression`) VALUES (0,@AGREEMENT,@HORAS_EXTRAS,NULL,'IMPORTE_HORA_EXTRA * 2 * HORAS_EXTRAS_FESTIVOS','HORAS EXTRAORDINARIAS ( DOMINGOS y FESTIVOS )','2015-04-01',NULL,NULL,0,0,NULL,NULL);

INSERT INTO `agreement_extra` (`domain`, `agreement`, `agreement_payment`, `start_date`, `end_date`, `issue_date`) VALUES (0,@AGREEMENT,@EXTRA_JULIO,'1/1','30/6','15/7');
INSERT INTO `agreement_extra` (`domain`, `agreement`, `agreement_payment`, `start_date`, `end_date`, `issue_date`) VALUES (0,@AGREEMENT,@EXTRA_DICIEMBRE,'1/7','31/12','15/12');

INSERT INTO `app_param` (`domain`, `name`, `value`) VALUES (0,'PAY_SYSTEM_AGREEMENT',@AGREEMENT);

SET @AON_AIO=(SELECT `id` FROM `domain_application` WHERE `application` IN (SELECT `id` FROM `application` WHERE `name`='aon-aio') AND `domain`=0);
SET @AIO_PAYROLL=IFNULL((SELECT `id` FROM `domain_application_module` WHERE `domain_application`=@AON_AIO AND `module`=33), (SELECT MAX(`id`) + 1 FROM `domain_application_module`));
INSERT IGNORE `domain_application_module` ( `id`, `domain`, `domain_application`, `module`) VALUES (@AIO_PAYROLL, 0, @AON_AIO, 8);

UPDATE `db_version` SET `version_number` = '8.69.0';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
