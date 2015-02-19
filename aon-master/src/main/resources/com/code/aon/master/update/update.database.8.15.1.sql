# Database: aon_master
# Version: Actualizacion de la version 8.15.1 a la version 8.15.2.
# Created by: ecastellano
# Creation Date: 19/02/2015 

BEGIN;

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
  `resident_percent` double(15,3) DEFAULT '0' COMMENT 'Porcentaje de renta atrib. a miembros residentes',
  `tax_is` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Tributacion en regimen del Impuesto sobre Sociedades',
  `net_sales_amount` double(15,3) DEFAULT '0' COMMENT 'Importe neto cifra de negocios',
  `lrdocument` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante',
  `lrname` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre Representante',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL184_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL184_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_FS_MODEL184_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL184_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaraciones 184';

CREATE TABLE `fs_model184_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fs_model184` int(4) NOT NULL COMMENT 'Identificador del modelo 184',
  `type` varchar(1) COLLATE latin1_spanish_ci NOT NULL DEFAULT 'I' COMMENT 'Tipo de detalle',
  `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Perceptor',
  `name` varchar(40) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `representative_document` varchar(9) collate latin1_spanish_ci default NULL COMMENT 'NIF Representante',
  `key` varchar(1) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Clave Percepcion',
  `subkey` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Subclave Percepcion',
  `province` int(4) NOT NULL default '0' COMMENT 'Provincia',
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
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL184_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL184_DETAIL_FS_MODEL184` (`fs_model184`),
  CONSTRAINT `FK_FS_MODEL184_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL184_DETAIL_FS_MODEL184` FOREIGN KEY (`fs_model184`) REFERENCES `fs_model184` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Declaraciones 184';

UPDATE `db_version` SET `version_number` = '8.15.2';

COMMIT;

