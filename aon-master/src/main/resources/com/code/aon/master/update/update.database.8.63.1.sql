# Database: aon_master
# Version: Actualizacion de la version 8.63.1 a la version 8.64.0.
# Created by: aibanez
# Creation Date: 29/08/2016 

BEGIN;

CREATE TABLE `task_tag` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `task` int(4) NOT NULL COMMENT 'Identificador de la tarea',
  `tag` int(4) NOT NULL COMMENT 'Identificador de la Etiqueta',
  PRIMARY KEY (`id`),
  KEY `IDX_TASK_TAG_DOMAIN` (`domain`),
  KEY `IDX_TASK_TAG_TASK` (`task`),
  KEY `IDX_TASK_TAG_TAG` (`tag`),
  CONSTRAINT `FK_TASK_TAG_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TASK_TAG_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`),
  CONSTRAINT `FK_TASK_TAG_TAG` FOREIGN KEY (`tag`) REFERENCES `tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Tareas y Etiquetas';

CREATE TABLE `task_comment` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `task` int(4) NOT NULL COMMENT 'Identificador de la tarea',
  `registry` int(4) NOT NULL COMMENT 'Identificador de registry',
  `comment` text COLLATE latin1_spanish_ci COMMENT 'Comentario de la Tarea',
  `create_date` date NOT NULL COMMENT 'Fecha de creacion',
  `update_date` date DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_TASK_COMMENT_DOMAIN` (`domain`),
  KEY `IDX_TASK_COMMENT_TASK` (`task`),
  KEY `IDX_TASK_COMMENT_REGISTRY` (`registry`),
  CONSTRAINT `FK_TASK_COMMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TASK_COMMENT_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`),
  CONSTRAINT `FK_TASK_COMMENT_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Tareas y Comentarios';

CREATE TABLE `task_event` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `task` int(4) NOT NULL COMMENT 'Identificador de la tarea',
  `registry` int(4) NOT NULL COMMENT 'Identificador de registry',
  `event` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Evento de la Tarea',
  `create_date` date NOT NULL COMMENT 'Fecha de creacion',
  PRIMARY KEY (`id`),
  KEY `IDX_TASK_EVENT_DOMAIN` (`domain`),
  KEY `IDX_TASK_EVENT_TASK` (`task`),
  KEY `IDX_TASK_EVENT_REGISTRY` (`registry`),
  CONSTRAINT `FK_TASK_EVENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_TASK_EVENT_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`),
  CONSTRAINT `FK_TASK_EVENT_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Tareas y Eventos';


ALTER TABLE `task` ADD `number` int(4) DEFAULT NULL COMMENT 'Numero de la Tarea' AFTER `domain`;

ALTER TABLE `task` ADD `update_date` date DEFAULT NULL COMMENT 'Fecha de modificacion de la Tarea' AFTER `due_date`;

UPDATE `db_version` SET `version_number` = '8.64.0';

COMMIT;


