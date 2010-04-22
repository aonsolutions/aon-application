# Database: aon_master
# Version: Actualizacion de la version 4.8.1 a la version 4.9.0.
# Created by: girazu
# Creation Date: 18/02/2010 10:37
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

CREATE TABLE `application` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `audit_level` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Nivel de auditoria',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Nombre de la Aplicacion',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
)ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE= latin1_spanish_ci COMMENT='Aplicacion web';

CREATE TABLE `session` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `endDate` datetime DEFAULT NULL COMMENT 'Fecha de finalizacion',
  `remote_address` varchar(15) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'IP remota',
  `remote_host` varchar(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Equipo remoto',
  `session_id` varchar(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificador web de la sesion',
  `startDate` datetime NOT NULL COMMENT 'Fecha de inicio',
  `application_id` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Aplicacion',
  `user_id` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Usuario',
  PRIMARY KEY (`id`),
  KEY `IDX_SESSION_USER_ID` (`user_id`),
  KEY `IDX_SESSION_APPLICATION_ID` (`application_id`),
  CONSTRAINT `FK_SESSION_APPLICATION_ID` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `FK_SESSION_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE= latin1_spanish_ci COMMENT='Sesion web';

CREATE TABLE `action` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `menu` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si la Accion esta o no dentro del menu',
  `name` varchar(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Nombre de la Accion',
  `application_id` int(4) NOT NULL COMMENT 'Aplicacion a la que pertenece la Accion',
  PRIMARY KEY (`id`),
  KEY `IDX_ACTION_NAME` (`name`),
  KEY `IDX_ACTION` (`name`, `application_id`),
  KEY `IDX_ACTION_APPLICATION` (`application_id`),
  CONSTRAINT `FK_ACTION_APPLICATION` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE= latin1_spanish_ci COMMENT='Acciones de una Applicacion';

CREATE TABLE `action_denied` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `action_id` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Accion',
  `user_id` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Usuario',
  PRIMARY KEY (`id`),
  KEY `IDX_ACTION_DENIED_USER_ID` (`user_id`),
  KEY `IDX_ACTION_DENIED_ACTION_ID` (`action_id`),
  CONSTRAINT `FK_ACTION_DENIED_ACTION_ID` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `FK_ACTION_DENIED_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE= latin1_spanish_ci COMMENT='Accion no permitida para el Usuario';

CREATE TABLE `action_entry` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `executionDate` datetime NOT NULL COMMENT 'Fecha de ejecucion',
  `action_id` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Accion',
  `session_id` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Sesion',
  PRIMARY KEY (`id`),
  KEY `IDX_ACTION_ENTRY_SESSION_ID` (`session_id`),
  KEY `IDX_ACTION_ENTRY_ACTION_ID` (`action_id`),
  CONSTRAINT `FK_ACTION_ENTRY_ACTION_ID` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `FK_ACTION_ENTRY_SESSION_ID` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE= latin1_spanish_ci COMMENT='Entrada de la ejecucion de una Accion';

CREATE TABLE `action_favorite` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `position` int(4) NOT NULL COMMENT 'Posicion dentro de las Acciones Favoritas',
  `action_id` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Accion',
  `user_id` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Usuario',
  PRIMARY KEY (`id`),
  KEY `IDX_ACTION_FAVORITE_USER_ID` (`user_id`),
  KEY `IDX_ACTION_FAVORITE_ACTION_ID` (`action_id`),
  CONSTRAINT `FK_ACTION_FAVORITE_ACTION_ID` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `FK_ACTION_FAVORITE_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE= latin1_spanish_ci COMMENT='Accion Favorita del Usuario';

UPDATE `invoice` set `reference_code` = CONCAT(`series`, "/", LPAD(`number`, 6, '0')) WHERE `type` = 1 AND `number` < 999999;


UPDATE `db_version` SET `version_number` = '4.9.0';

COMMIT;
