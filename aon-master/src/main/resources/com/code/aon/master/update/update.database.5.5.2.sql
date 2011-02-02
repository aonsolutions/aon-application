# Database: aon_master
# Version: Actualizacion de la version 5.5.2 a la version 5.5.3.
# Created by: girazu
# Creation Date: 20/10/2010 18:27
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

CREATE TABLE `annual_report` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Nombre de la Memoria',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Memoria';

CREATE TABLE `annual_report_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `annual_report` int(4) NOT NULL COMMENT 'Identificador de la Memoria',
  `sortKey` int(4) default '0' COMMENT 'Orden el que aparecera en el listado',
  `content` text collate latin1_spanish_ci COMMENT 'Contenido del parrafo',
  `style` tinyint(2) default NULL COMMENT 'Estilo',
  PRIMARY KEY  (`id`),
  KEY `IDX_ANNUAL_REPORT_DETAIL_ANNUAL_REPORT` (`annual_report`),
  CONSTRAINT `FK_ANNUAL_REPORT_DETAIL_ANNUAL_REPORT` FOREIGN KEY (`annual_report`) REFERENCES `annual_report` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de la Memoria'; 


UPDATE `db_version` SET `version_number` = '5.5.3';

COMMIT;
