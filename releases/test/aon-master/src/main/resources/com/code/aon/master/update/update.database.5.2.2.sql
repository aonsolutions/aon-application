# Database: aon_master
# Version: Actualizacion de la version 5.2.2 a la version 5.3.0.
# Created by: girazu
# Creation Date: 25/06/2010 14:27
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `tax` ADD `vat_deduction_type` tinyint(2) default '0' COMMENT 'Tipo de deduccion del IVA';

ALTER TABLE `tax` ADD `withholding_type` tinyint(2) default '0' COMMENT 'Tipo de retencion';

ALTER TABLE `invoice_tax` ADD `vat_deduction_type` tinyint(2) default '0' COMMENT 'Tipo de deduccion del IVA';

ALTER TABLE `invoice_tax` ADD `withholding_type` tinyint(2) default '0' COMMENT 'Tipo de retencion';

ALTER TABLE `invoice_tax` ADD `deductible_quota` double default '0' COMMENT 'Cuota deducible';

CREATE TABLE `fs_vat` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `period` tinyint(2) default '0' COMMENT 'Periodo de la Declaracion',
  `type` tinyint(2) default '0' COMMENT 'Tipo de Declaracion',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `status` tinyint(2) default '0' COMMENT 'Estado de la Declaracion',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `series` (`year`,`period`,`type`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaracion de IVA';

CREATE TABLE `fs_vat_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `fs_vat` int(4) NOT NULL default '0' COMMENT 'Identificador de la Declaracion',
  `key` tinyint(2) default '0' COMMENT 'Clave de la Declaracion',
  `percent` double default '0' COMMENT 'Porcentaje de IVA',
  `taxable_base` double(15,3) default '0.000' COMMENT 'Base Imponible',
  `quota` double(15,3) default '0.000' COMMENT 'Cuota',
  `deductible_quota` double(15,3) default '0.000' COMMENT 'Cuota Deducible',
  `adj_taxable_base` double(15,3) default '0.000' COMMENT 'Base Imponible Ajustada',
  `adj_quota` double(15,3) default '0.000' COMMENT 'Cuota Ajustada',
  `adj_deductible_quota` double(15,3) default '0.000' COMMENT 'Cuota Deducible Ajustada',
  PRIMARY KEY  (`id`),
  KEY `idx_fs_vat` (`fs_vat`),
  CONSTRAINT `fs_vat_detail_fs_vat` FOREIGN KEY (`fs_vat`) REFERENCES `fs_vat` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de Declaracion de IVA';


UPDATE `db_version` SET `version_number` = '5.3.0';

COMMIT;
