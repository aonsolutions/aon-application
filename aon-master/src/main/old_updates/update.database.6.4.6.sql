# Database: aon_master
# Version: Actualizacion de la version 6.4.6 a la version 6.4.7.
# Created by: girazu
# Creation Date: 14/06/2011 18:50
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

CREATE TABLE `enterprise_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de Empresa',
  `name` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Expresion',
  `start_date` date default NULL COMMENT 'Fecha de inicio',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_ENTERPRISE_DATA_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_ENTERPRISE_DATA_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contexto de la Empresa';


UPDATE `db_version` SET `version_number` = '6.4.7';

COMMIT;
