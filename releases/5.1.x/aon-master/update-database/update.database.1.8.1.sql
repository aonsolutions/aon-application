# Database: aon_master
# Version: Actualizacion de la version 1.8.1 a la version 1.9.0
# Created by: girazu
# Creation Date: 05/09/2008 11:08
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



CREATE TABLE `commercial_segment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Segmento Comercial',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Segmentos Comerciales';

CREATE TABLE `commercial_activity` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Actividad Comercial',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades Comerciales';

CREATE TABLE `commercial_tracking` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `date` date NOT NULL COMMENT 'Fecha del Seguimiento Comercial',
  `seller` int(4) NOT NULL COMMENT 'Identificador del Comercial',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `activity` int(4) NOT NULL COMMENT 'Identificador de la Actividad Comercial',
  `comments` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Comentarios del Seguimiento Comercial',
  `status` tinyint(2) NOT NULL COMMENT 'Estado del Seguimiento Comercial',
  `next_commercial_tracking` int(4) default NULL COMMENT 'Identificador del siguiente Seguimiento Comercial',
  PRIMARY KEY (`id`),
  KEY `seller` (`seller`),
  KEY `target` (`target`),
  KEY `activity` (`activity`),
  KEY `next_commercial_tracking` (`next_commercial_tracking`),
  CONSTRAINT `commercial_tracking_fk` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`),
  CONSTRAINT `commercial_tracking_fk1` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `commercial_tracking_fk2` FOREIGN KEY (`activity`) REFERENCES `commercial_activity` (`id`),
  CONSTRAINT `commercial_tracking_fk3` FOREIGN KEY (`next_commercial_tracking`) REFERENCES `commercial_tracking` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Seguimientos Comerciales';

ALTER TABLE `target` ADD `advertising` tinyint(2) NOT NULL default '0' COMMENT 'Admision de Publicidad';

CREATE TABLE `target_segment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `segment` int(4) NOT NULL COMMENT 'Identificador del Segmento Comercial',
  PRIMARY KEY (`id`),
  KEY `target` (`target`),
  KEY `segment` (`segment`),
  CONSTRAINT `target_segment_fk` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `target_segment_fk1` FOREIGN KEY (`segment`) REFERENCES `commercial_segment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Segmentos interesados por Cliente Potencial';

CREATE TABLE `target_item` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo',
  `status` tinyint(2) NOT NULL COMMENT 'Estado',
  PRIMARY KEY (`id`),
  KEY `target` (`target`),
  KEY `item` (`item`),
  CONSTRAINT `target_item_fk` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `target_item_fk1` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulos interesados por Cliente Potencial';

CREATE TABLE `target_seller` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `seller` int(4) NOT NULL COMMENT 'Identificador del Comercial',
  `start_date` date NOT NULL COMMENT 'Fecha de Inicio',
  `end_date` date default NULL COMMENT 'Fecha de Fin',
  `status` tinyint(2) NOT NULL COMMENT 'Estado',
  PRIMARY KEY (`id`),
  KEY `target` (`target`),
  KEY `seller` (`seller`),
  CONSTRAINT `target_seller_fk` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `target_seller_fk1` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comerciales relacionado con Cliente Potencial';

CREATE TABLE `process_transition_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Tipo de Transicion',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Transiciones entre Detalles de Procesos';

CREATE TABLE `process_detail_transition` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `process_detail` int(11) NOT NULL COMMENT 'Identificador del Detalle del Proceso.',
  `process_transition_type` int(11) NOT NULL COMMENT 'Identificador del Tipo de Transicion.',
  `next_process_detail` int(11) NOT NULL COMMENT 'Identificador del siguiente Detalle del Proceso.',
  PRIMARY KEY (`id`),
  KEY `process_detail` (`process_detail`),
  KEY `process_transition_type` (`process_transition_type`),
  KEY `next_process_detail` (`next_process_detail`),
  CONSTRAINT `fk_process_detail_transition_next_process_detail` FOREIGN KEY (`next_process_detail`) REFERENCES `process_detail` (`id`),
  CONSTRAINT `fk_process_detail_transition_process_detail` FOREIGN KEY (`process_detail`) REFERENCES `process_detail` (`id`),
  CONSTRAINT `fk_process_detail_transition_process_transition_type` FOREIGN KEY (`process_transition_type`) REFERENCES `process_transition_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Transiciones entre Detalles de Procesos';


UPDATE `db_version` SET `version_number` = '1.9.0';

COMMIT;
