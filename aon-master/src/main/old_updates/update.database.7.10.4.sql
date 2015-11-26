# Database: aon_master
# Version: Actualizacion de la version 7.10.4 a la version 7.11.0.
# Created by: girazu
# Creation Date: 13/02/2013 16:00
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `invoice` ADD `pos` int(4) DEFAULT NULL COMMENT 'Identificador del TPV' AFTER `advance`;
ALTER TABLE `invoice` ADD KEY `IDX_INVOICE_POS` (`pos`);
ALTER TABLE `invoice` ADD CONSTRAINT `FK_INVOICE_POS` FOREIGN KEY (`pos`) REFERENCES `pos` (`id`);

ALTER TABLE `pcategory` DROP `detail_pattern`;

ALTER TABLE `pcategory` ADD `detail` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre del detalle de los Articulos';
ALTER TABLE `pcategory` ADD `detail2` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre del detalle 2 de los Articulos';

ALTER TABLE `item` MODIFY `detail` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Detalle del Articulo';
ALTER TABLE `item` ADD `detail2` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Detalle 2 del Articulo' AFTER `detail`;

CREATE TABLE `training_center` (
  `registry` int(4) NOT NULL COMMENT 'Registro del Centro Formativo',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `code` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo del centro formativo',
  PRIMARY KEY  (`registry`),
  KEY `IDX_TRAINING_CENTER_DOMAIN` (`domain`),
  CONSTRAINT `FK_TRAINING_CENTER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TRAINING_CENTER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Centros Formativos acreditados';

CREATE TABLE `training_course` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `training_center` int(4) NOT NULL COMMENT 'Identificador del Centro Formativo',
  `code` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo del Curso Formativo',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Nombre del Curso Formativo',
  `cno` int(4) default NULL COMMENT 'CNO',
  `modality` tinyint(1) default NULL COMMENT 'Modalidad del Curso Formativo',
  PRIMARY KEY  (`id`),
  KEY `IDX_TRAINING_COURSE_CNO` (`cno`),
  KEY `IDX_TRAINING_COURSE_DOMAIN` (`domain`),
  KEY `IDX_TRAINING_COURSE_TRAINING_CENTER` (`training_center`),
  CONSTRAINT `FK_TRAINING_COURSE_CNO` FOREIGN KEY (`cno`) REFERENCES `cno` (`id`),
  CONSTRAINT `FK_TRAINING_COURSE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TRAINING_COURSE_TRAINING_CENTER` FOREIGN KEY (`training_center`) REFERENCES `training_center` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cursos de los Centros Formativos'; 


UPDATE `db_version` SET `version_number` = '7.11.0';

COMMIT;
