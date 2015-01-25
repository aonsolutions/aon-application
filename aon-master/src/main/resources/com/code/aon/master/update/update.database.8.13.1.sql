# Database: aon_master
# Version: Actualizacion de la version 8.13.1 a la version 8.13.2.
# Created by: ecastellano
# Creation Date: 23/01/2015 12:55

BEGIN;

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
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL193_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL193_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_FS_MODEL193_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL193_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaraciones 193';

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
  `loan_start_date` date default NULL COMMENT 'Fecha de inicio del prestamo',
  `loan_due_date` date default NULL COMMENT 'Fecha de vencimiento del prestamo',
  `compensation` double(15,3) NOT NULL DEFAULT '0.000',
  `guarantee` double(15,3) NOT NULL DEFAULT '0.000',
  `expenses` double(15,3) NOT NULL DEFAULT '0.000',  
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL193_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL193_DETAIL_FS_MODEL193` (`fs_model193`),
  CONSTRAINT `FK_FS_MODEL193_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL193_DETAIL_FS_MODEL193` FOREIGN KEY (`fs_model193`) REFERENCES `fs_model193` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Declaraciones 193';

UPDATE `db_version` SET `version_number` = '8.13.2';

COMMIT;

