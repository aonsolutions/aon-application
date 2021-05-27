# Database: aon_master
# Version: Actualizacion de la version 7.10.0 a la version 7.10.1.
# Created by: girazu
# Creation Date: 06/02/2013 11:20
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `fs_model` ADD `withoutActivity` tinyint(1) DEFAULT '0' COMMENT 'Sin Actividad' AFTER `replacement`;
ALTER TABLE `fs_model` ADD `finance` int(4) default NULL COMMENT 'Identificador de Vencimiento';
ALTER TABLE `fs_model` ADD KEY `IDX_FS_MODEL_FINANCE` (`finance`);
ALTER TABLE `fs_model` ADD CONSTRAINT `FK_FS_MODEL_FINANCE` FOREIGN KEY (`finance`) REFERENCES `finance` (`id`);

ALTER TABLE `fs_model_detail` ADD `description` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion' AFTER `type`;

DROP TABLE fs_prof_retention;

CREATE TABLE `fs_activity` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `year` int(4) NOT NULL COMMENT 'Ejercicio del Lote',
  `epigraph` varchar(7) collate latin1_spanish_ci NOT NULL COMMENT 'Epigrafe IAE',
  `description` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del epigrafe',
  `farmer` tinyint(1) DEFAULT '0' COMMENT 'Actividad agricola',
  `maxPerson` double(15,3) default '0.000' COMMENT 'Valor maximo de personas',
  `maxImport` double(15,3) default '0.000' COMMENT 'Valor maximo de importe',
  `vatPercent` double(15,3) default '0.000' COMMENT 'IVA - porcentaje aplicable',
  PRIMARY KEY  (`id`),
  KEY `IDX_FS_ACTIVITY_DOMAIN` (`domain`),
  CONSTRAINT `FK_FS_ACTIVITY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Fiscal. Datos previos de Modulos'; 

CREATE TABLE `fs_activity_info` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fs_activity` int(4) NOT NULL COMMENT 'Actividad fiscal',
  `info_key` varchar(5) collate latin1_spanish_ci NOT NULL COMMENT 'Clave de informacion',
  `line` int(4) NOT NULL COMMENT 'Numero de linea',
  `type` tinyint(2) NOT NULL COMMENT 'Tipo de linea',
  `value` varchar(25) collate latin1_spanish_ci NOT NULL COMMENT 'Valor de la informacion',
  `factor` double(15,3) default '0.000' COMMENT 'Factor',
  `base` double(15,3) default '0.000' COMMENT 'Rendimiento neto',
  `unit` varchar(25) collate latin1_spanish_ci default NULL COMMENT 'Unidades',
  `minValue` double(15,3) default '0.000' COMMENT 'Valor minimo',
  `maxValue` double(15,3) default '0.000' COMMENT 'Valor maximo',
  PRIMARY KEY  (`id`),
  KEY `IDX_FS_ACTIVITY_INFO_DOMAIN` (`domain`),
  KEY `IDX_FS_ACTIVITY_INFO_FS_ACTIVITY` (`fs_activity`),
  CONSTRAINT `FK_FS_ACTIVITY_INFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_ACTIVITY_INFO_FS_ACTIVITY` FOREIGN KEY (`fs_activity`) REFERENCES `fs_activity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Fiscal. Informacion de los Datos previos de Modulos';

ALTER TABLE `series` ADD `pos` tinyint(1) DEFAULT '0' COMMENT 'Indica si es una Serie para TPV' AFTER `rectification`;

ALTER TABLE `invoice` ADD `seller` int(4) DEFAULT NULL COMMENT 'Identificador de Agente Comercial' AFTER `advance`;
ALTER TABLE `invoice` ADD KEY `IDX_INVOICE_SELLER` (`seller`);
ALTER TABLE `invoice` ADD CONSTRAINT `FK_INVOICE_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`);


UPDATE `db_version` SET `version_number` = '7.10.1';

COMMIT;
