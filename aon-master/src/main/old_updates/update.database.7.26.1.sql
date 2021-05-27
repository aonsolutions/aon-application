# Database: aon_master
# Version: Actualizacion de la version 7.26.1 a la version 7.26.2.
# Created by: girazu
# Creation Date: 18/12/2013 13:45
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `category` ADD `scope` int(4) default NULL COMMENT 'Identificador del Ambito' AFTER `type`;
ALTER TABLE `category` ADD KEY `IDX_CATEGORY_SCOPE` (`scope`);
ALTER TABLE `category` ADD CONSTRAINT `FK_CATEGORY_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);

ALTER TABLE `newsletter` ADD `newsSeparator` tinyint(1) default '0' COMMENT 'Indica si el Boletin incluye un separador entre noticias'; 

ALTER TABLE `company` ADD `vat_accrual_payment` tinyint(1) default '0' COMMENT 'Indica si la Compañia esta acogida al Regimen Especial de Criterio de Caja' AFTER `withholding`;

ALTER TABLE `supplier` ADD `vat_accrual_payment` tinyint(1) default '0' COMMENT 'Indica si el Proveedor esta acogido al Regimen Especial de Criterio de Caja' AFTER `withholding_farmer`;

ALTER TABLE `creditor` ADD `vat_accrual_payment` tinyint(1) default '0' COMMENT 'Indica si el Acreedor esta acogido al Regimen Especial de Criterio de Caja' AFTER `withholding`;

ALTER TABLE `invoice` ADD `vat_accrual_payment` tinyint(1) default '0' COMMENT 'Indica si la Factura se incluye en el Regimen Especial de Criterio de Caja' AFTER `withholding_farmer`;

ALTER TABLE `finance` ADD `cheque_number` varchar(24) collate latin1_spanish_ci default NULL COMMENT 'Numero de cheque' AFTER `bic`;

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
   `complementary` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion complementaria',
   `replacement` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion sustitutiva',
   `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
   `receipt` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion',
   `replaced_receipt` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion sustituida',
   `receiver_count_total` int(4) NOT NULL DEFAULT '0' COMMENT 'Numero total de perceptores',
   `receipt_total` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Importe declarado',
   `retention_total` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Importe declarado',
   PRIMARY KEY (`id`),
   KEY `IDX_FS_MODEL180_DOMAIN` (`domain`),
   KEY `IDX_FS_MODEL180_ENTERPRISE` (`enterprise`),
   CONSTRAINT `FK_FS_MODEL180_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
   CONSTRAINT `FK_FS_MODEL180_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaraciones 180';

CREATE TABLE `fs_model180_detail` (
   `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
   `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
   `fs_model180` int(4) NOT NULL COMMENT 'Identificador del modelo 180',
   `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Perceptor',
   `name` varchar(40) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
   `representative_document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante',
   `province` int(4) NOT NULL DEFAULT '0' COMMENT 'Provincia',
   `inKind` tinyint(1) NOT NULL COLLATE latin1_spanish_ci COMMENT 'Percepcion en especie',
   `perception` double(15,3) NOT NULL DEFAULT '0.000' COMMENT '',
   `percentage` double(15,3) default '0.000' COMMENT 'Porcentaje de retencion',
   `retention` double(15,3) NOT NULL DEFAULT '0.000' COMMENT '',
   `accrual_year` int(4) NOT NULL DEFAULT '0' COMMENT '',
   PRIMARY KEY (`id`),
   KEY `IDX_FS_MODEL180_DETAIL_DOMAIN` (`domain`),
   KEY `IDX_FS_MODEL180_DETAIL_FS_MODEL180` (`fs_model180`),
   CONSTRAINT `FK_FS_MODEL180_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
   CONSTRAINT `FK_FS_MODEL180_DETAIL_FS_MODEL180` FOREIGN KEY (`fs_model180`) REFERENCES `fs_model180` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Declaraciones 180';

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
   `complementary` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion complementaria',
   `replacement` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion sustitutiva',
   `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
   `receipt` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion',
   `replaced_receipt` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion sustituida',
   `receiver_count_total` int(4) NOT NULL DEFAULT '0' COMMENT 'Numero total de perceptores',
   `receipt_total` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Importe declarado',
   `retention_total` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Importe declarado',
   PRIMARY KEY (`id`),
   KEY `IDX_FS_MODEL190_DOMAIN` (`domain`),
   KEY `IDX_FS_MODEL190_ENTERPRISE` (`enterprise`),
   CONSTRAINT `FK_FS_MODEL190_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
   CONSTRAINT `FK_FS_MODEL190_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaraciones 190';

CREATE TABLE `fs_model190_detail` (
   `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
   `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
   `fs_model190` int(4) NOT NULL COMMENT 'Identificador del modelo 190',
   `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Perceptor',
   `name` varchar(40) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
   `representative_document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante',
   `province` int(4) NOT NULL DEFAULT '0' COMMENT 'Provincia',
   `key` varchar(1) NOT NULL COLLATE latin1_spanish_ci COMMENT 'Clave Percepcion',
   `subkey` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Subclave Percepcion',
   `perception` double(15,3) NOT NULL DEFAULT '0.000' COMMENT '',
   `retention` double(15,3) NOT NULL DEFAULT '0.000' COMMENT '',
   `in_kind_perception` double(15,3) NOT NULL DEFAULT '0.000' COMMENT '',
   `in_kind_deposit` double(15,3) NOT NULL DEFAULT '0.000' COMMENT '',
   `in_kind_output_deposit` double(15,3) NOT NULL DEFAULT '0.000' COMMENT '',
   `accrual_year` int(4) NOT NULL DEFAULT '0' COMMENT '',
   `ceuta_melilla` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Los datos anteriores corresponden a rendimientos obtenidos en Ceuta o Melilla',
   `birth_year` int(4) NOT NULL DEFAULT '0' COMMENT '',
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
   `less_than_3_descendent` tinyint(1) DEFAULT '0' COMMENT '',
   `less_than_3_descendent_ratio` tinyint(1) DEFAULT '0' COMMENT '',
   `other_descendent` tinyint(1) DEFAULT '0' COMMENT '',
   `other_descendent_ratio` tinyint(1) DEFAULT '0' COMMENT '',
   `disability_descendent_33` tinyint(1) DEFAULT '0' COMMENT '',
   `disability_descendent_33_ratio` tinyint(1) DEFAULT '0' COMMENT '',
   `disability_descendent_dependence` tinyint(1) DEFAULT '0' COMMENT '',
   `disability_descendent_dependence_ratio` tinyint(1) DEFAULT '0' COMMENT '',
   `disability_descendent_65` tinyint(1) DEFAULT '0' COMMENT '',
   `disability_descendent_65_ratio` tinyint(1) DEFAULT '0' COMMENT '',
   `less_than_75_ascendant` tinyint(1) DEFAULT '0' COMMENT '',
   `less_than_75_ascendant_ratio` tinyint(1) DEFAULT '0' COMMENT '',
   `ascendant` tinyint(1) DEFAULT '0' COMMENT '',
   `ascendant_ratio` tinyint(1) DEFAULT '0' COMMENT '',
   `disability_ascendant_33` tinyint(1) DEFAULT '0' COMMENT '',
   `disability_ascendant_33_ratio` tinyint(1) DEFAULT '0' COMMENT '',
   `disability_ascendant_dependence` tinyint(1) DEFAULT '0' COMMENT '',
   `disability_ascendant_dependence_ratio` tinyint(1) DEFAULT '0' COMMENT '',
   `disability_ascendant_65` tinyint(1) DEFAULT '0' COMMENT '',
   `disability_ascendant_65_Ratio` tinyint(1) DEFAULT '0' COMMENT '',
   `first_child_calculation` tinyint(1) DEFAULT '0' COMMENT '',
   `second_child_calculation` tinyint(1) DEFAULT '0' COMMENT '',
   `third_child_calculation` tinyint(1) DEFAULT '0' COMMENT '',
   `home_loan_communnication` tinyint(1) DEFAULT '0' COMMENT '',
   PRIMARY KEY (`id`),
   KEY `IDX_FS_MODEL190_DETAIL_DOMAIN` (`domain`),
   KEY `IDX_FS_MODEL190_DETAIL_FS_MODEL190` (`fs_model190`),
   CONSTRAINT `FK_FS_MODEL190_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
   CONSTRAINT `FK_FS_MODEL190_DETAIL_FS_MODEL190` FOREIGN KEY (`fs_model190`) REFERENCES `fs_model190` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Declaraciones 190';


UPDATE `db_version` SET `version_number` = '7.26.2';

COMMIT;
