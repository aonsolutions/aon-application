# Database: aon_master
# Version: Actualizacion de la version 7.1.0 a la version 7.1.1.
# Created by: girazu
# Creation Date: 30/05/2012 18:50
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `project_reservation` ADD `check_status` tinyint(2) NOT NULL COMMENT 'Estado de registro en el Hotel' AFTER `advance_invoiced`;
UPDATE `project_reservation` SET `check_status` = 3 WHERE `no_show` = 1;
ALTER TABLE `project_reservation` DROP `no_show`;

CREATE TABLE `tag` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Etiqueta',
  PRIMARY KEY  (`id`),
  KEY `IDX_TAG_DOMAIN` (`domain`),
  CONSTRAINT `FK_TAG_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Etiquetas';

CREATE TABLE `rattach_tag` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `rattach` int(4) NOT NULL COMMENT 'Identificador del Archivo Adjunto',
  `tag` int(4) NOT NULL COMMENT 'Identificador de la Etiqueta',
  PRIMARY KEY  (`id`),
  KEY `IDX_RATTACH_TAG_DOMAIN` (`domain`),
  KEY `IDX_RATTACH_TAG_RATTACH` (`rattach`),
  KEY `IDX_RATTACH_TAG_TAG` (`tag`),
  CONSTRAINT `FK_RATTACH_TAG_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RATTACH_TAG_RATTACH` FOREIGN KEY (`rattach`) REFERENCES `rattach` (`id`),
  CONSTRAINT `FK_RATTACH_TAG_TAG` FOREIGN KEY (`tag`) REFERENCES `tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Archivos Adjuntos y Etiquetas';


UPDATE `db_version` SET `version_number` = '7.1.1';

COMMIT;
