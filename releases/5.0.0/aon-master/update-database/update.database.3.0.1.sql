# Database: aon_master
# Version: Actualizacion de la version 3.0.1 a la version 3.1.0.
# Created by: girazu
# Creation Date: 12/03/2009 10:54
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



CREATE TABLE `asset` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Activo',
  `name` varchar(10) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre corto del Activo',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Activos';

CREATE TABLE `asset_activity` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `asset` int(4) NOT NULL COMMENT 'Identificador del Activo',
  `date` date NOT NULL COMMENT 'Fecha de la Actividad',
  `from_time` datetime NOT NULL COMMENT 'Hora de inicio de la Actividad',
  `to_time` datetime NOT NULL COMMENT 'Hora final de la Actividad',
  `who` varchar(20) collate latin1_spanish_ci default NULL COMMENT 'Quien solicita el Activo',
  `why` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Motivo de solicitud del Activo',
  PRIMARY KEY  (`id`),
  KEY `asset` (`asset`),
  CONSTRAINT `asset_activity_fk1` FOREIGN KEY (`asset`) REFERENCES `asset` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades sobre el Activo';


UPDATE `db_version` SET `version_number` = '3.1.0';

COMMIT;
