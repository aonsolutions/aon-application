# Database: aon_master
# Version: Actualizacion de la version 6.4.4 a la version 6.4.5.
# Created by: girazu
# Creation Date: 07/06/2011 12:29
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `question` MODIFY `argument` TEXT collate latin1_spanish_ci COMMENT 'Argumentacion de la Pregunta';

CREATE TABLE `project` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Proyecto',
  `date` datetime NOT NULL COMMENT 'Fecha del Proyecto',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `seller` int(4) DEFAULT NULL COMMENT 'Identificador del Comercial',    
  `status` tinyint(2) NOT NULL COMMENT 'Estado del Proyecto',
  `status_date` datetime NOT NULL COMMENT 'Fecha del Estado del Proyecto',
  `probability` int(4) default NULL COMMENT 'Probabilidad del Proyecto',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROJECT_TARGET` (`target`),
  KEY `IDX_PROJECT_SELLER` (`seller`),
  CONSTRAINT `FK_PROJECT_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_PROJECT_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Proyectos';

DROP TABLE `commercial_tracking`;

CREATE TABLE `commercial_tracking` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `date` datetime NOT NULL COMMENT 'Fecha del Seguimiento Comercial',
  `seller` int(4) NOT NULL COMMENT 'Identificador del Comercial',
  `project` int(4) NOT NULL COMMENT 'Identificador del Proyecto',
  `activity` int(4) NOT NULL COMMENT 'Identificador de la Actividad Comercial',
  `comments` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Comentarios del Seguimiento Comercial',
  `status` tinyint(2) NOT NULL COMMENT 'Estado del Seguimiento Comercial',
  `next_commercial_tracking` int(4) default NULL COMMENT 'Identificador del siguiente Seguimiento Comercial',
  `end_date` datetime default NULL COMMENT 'Fecha de cierre del Seguimiento Comercial',
  `offer` int(4) default NULL COMMENT 'Identificador del Presupuesto',
  `allDay` tinyint(1) default '0' COMMENT 'Indica si el Seguimiento Comercial dura todo el dia',
  `location` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Ubicacion del Seguimiento Comercial',
  PRIMARY KEY  (`id`),
  KEY `IDX_COMMERCIAL_TRACKING_SELLER` (`seller`),
  KEY `IDX_COMMERCIAL_TRACKING_PROJECT` (`project`),
  KEY `IDX_COMMERCIAL_TRACKING_ACTIVITY` (`activity`),
  KEY `IDX_COMMERCIAL_TRACKING_NEXT` (`next_commercial_tracking`),
  KEY `IDX_COMMERCIAL_TRACKING_OFFER` (`offer`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_ACTIVITY` FOREIGN KEY (`activity`) REFERENCES `commercial_activity` (`id`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_NEXT` FOREIGN KEY (`next_commercial_tracking`) REFERENCES `commercial_tracking` (`id`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Seguimientos Comerciales';


UPDATE `db_version` SET `version_number` = '6.4.5';

COMMIT;
