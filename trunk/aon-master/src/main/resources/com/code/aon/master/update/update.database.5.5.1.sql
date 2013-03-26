# Database: aon_master
# Version: Actualizacion de la version 5.5.1 a la version 5.5.2.
# Created by: girazu
# Creation Date: 19/10/2010 17:16
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

CREATE TABLE `fs_renting` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `period` tinyint(2) default '0' COMMENT 'Periodo de la Declaracion',
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
  `rbank` int(4) default NULL COMMENT 'Banco de la Compañia',
  PRIMARY KEY  (`id`),
  KEY `IDX_FS_RENTING_RBANK` (`rbank`),
  CONSTRAINT `FK_FS_RENTING_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaracion de IRPF';

CREATE TABLE `fs_renting_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `fs_renting` int(4) NOT NULL default '0' COMMENT 'Identificador de la Declaracion',
  `type` tinyint(2) default '0' COMMENT 'Modalidad',
  `document` varchar(9) collate latin1_spanish_ci default NULL COMMENT 'NIF',
  `name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Apellidos  y Nombre',
  `paid_returns` double(15,3) default '0.000' COMMENT 'Rendimientos satisfechos',
  `account_deposit` double(15,3) default '0.000' COMMENT 'Ingresos a cuenta',
  `address` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Direccion',
  `city` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Municipio',
  `province` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Provincia',
  PRIMARY KEY  (`id`),
  KEY `IDX_FS_RENTING_DETAIL_FS_RENTING` (`fs_renting`),
  CONSTRAINT `FK_FS_RENTING_DETAIL_FS_RENTING` FOREIGN KEY (`fs_renting`) REFERENCES `fs_renting` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de la Declaracion de IRPF'; 

UPDATE `db_version` SET `version_number` = '5.5.2';

COMMIT;
