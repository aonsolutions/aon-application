# Database: aon_master
# Version: Actualizacion de la version 5.5.0 a la version 5.5.1.
# Created by: girazu
# Creation Date: 14/10/2010 11:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `company` MODIFY `registry` int(4) NOT NULL default '1' COMMENT 'Registro de la Compañia';

ALTER TABLE `supplier` MODIFY `registry` int(4) NOT NULL default '0' COMMENT 'Registro del Proveedor';

ALTER TABLE `creditor` MODIFY `registry` int(4) NOT NULL default '0' COMMENT 'Registro del Acreedor';

DROP TABLE `fs_vat_detail`;

DROP TABLE `fs_vat`;

CREATE TABLE `fs_vat` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `period` tinyint(2) default '0' COMMENT 'Periodo de la Declaracion',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `status` tinyint(2) default '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad',
  `complementary` tinyint(1) default '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) default '0' COMMENT 'Declaracion sustitutiva',
  `tax_refund_registry` tinyint(1) default '0' COMMENT 'Inscrito en registro de devolucion',
  `number` int(4) default '0' COMMENT 'Numero de Decl. complementaria o sustitutiva',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaracion de Iva';

CREATE TABLE `fs_vat_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `fs_vat` int(4) NOT NULL default '0' COMMENT 'Identificador de la Declaracion',
  `vat_key` tinyint(2) default '0' COMMENT 'Clave de la Declaracion',
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
  KEY `IDX_FS_DETAIL_FS_VAT` (`fs_vat`),
  CONSTRAINT `FK_FS_DETAIL_FS_VAT` FOREIGN KEY (`fs_vat`) REFERENCES `fs_vat` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de Declaracion de Iva';

CREATE TABLE `fs_vat_declaration` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
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
  `rbank` int(4) default NULL COMMENT 'Banco de la Compañia',
  `compensable` tinyint(1) default '0' COMMENT 'Compensar o devolver',
  `status` tinyint(2) NOT NULL default '0' COMMENT 'Estado de la Declaracion',
  PRIMARY KEY  (`id`),
  KEY `IDX_FS_VAT_DECLARATION_FS_VAT` (`fs_vat`),
  KEY `IDX_FS_VAT_DECLARATION_RBANK` (`rbank`),
  CONSTRAINT `FK_FS_VAT_DECLARATION_FS_VAT` FOREIGN KEY (`fs_vat`) REFERENCES `fs_vat` (`id`),
  CONSTRAINT `FK_FS_VAT_DECLARATION_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Resultado de la Declaracion de Iva';


UPDATE `db_version` SET `version_number` = '5.5.1';

COMMIT;
