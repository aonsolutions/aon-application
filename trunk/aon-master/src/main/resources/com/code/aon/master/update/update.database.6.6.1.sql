# Database: aon_master
# Version: Actualizacion de la version 6.6.1 a la version 6.7.0.
# Created by: girazu
# Creation Date: 30/06/2011 14:42
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

UPDATE `geozone` SET `name` = 'ARABA/ALAVA' WHERE `id` = 1 AND code = '01' AND `name` = 'ALAVA';

UPDATE `geozone` SET `name` = 'GIPUZKOA' WHERE `id` = 20 AND code = '20' AND `name` = 'GUIPUZCOA';

UPDATE `geozone` SET `name` = 'BIZKAIA' WHERE `id` = 48 AND code = '48' AND `name` = 'VIZCAYA';

CREATE TABLE `task_holder` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro de la Entidad susceptible de Recibir Tareas',
  `type` tinyint(2) default '0' COMMENT 'Tipo de Entidad susceptible de Recibir Tareas',
  `active` tinyint(1) default '1' COMMENT 'Indica si dicha Entidad esta activa o no',
  PRIMARY KEY  (`registry`),
  CONSTRAINT `FK_TASK_HOLDER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Titulares de Tareas';

CREATE TABLE `project_tas` (
  `project` int(4) NOT NULL COMMENT 'Identificador del Proyecto',
  `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie de la Orden de Reparacion',
  `number` int(4) NOT NULL default '0' COMMENT 'Numero de la Orden de Reparacion',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `tas_item` int(4) NOT NULL COMMENT 'Identificador del Articulo susceptible de Asistencia Tecnica',
  `counter` double(15,3) default '0.000' COMMENT 'Contador del Articulo de la Orden de Reparacion (p.e. Kilometraje)',
  `task_holder` int(4) NOT NULL COMMENT 'Identificador del Articulo susceptible de Asistencia Tecnica',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios',
  `external_reference` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Referencia externa',
  `status` tinyint(2) NOT NULL COMMENT 'Estado de la Orden de Reparacion',
  `status_date` date default NULL COMMENT 'Fecha del Estado de la Orden de Reparacion',
  PRIMARY KEY  (`project`),
  KEY `IDX_PROJECT_TAS_TARGET` (`target`),
  CONSTRAINT `FK_PROJECT_TAS_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PROJECT_TAS_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ordenes de Reparacion o Fabricacion';

CREATE TABLE `project_commercial` (
  `project` int(4) NOT NULL COMMENT 'Identificador del Proyecto',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `seller` int(4) default NULL COMMENT 'Identificador del Comercial',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios',
  `source` tinyint(2) NOT NULL COMMENT 'Origen del Proyecto',
  `status` tinyint(2) NOT NULL COMMENT 'Estado del Proyecto',
  `status_date` date default NULL COMMENT 'Fecha del Estado del Proyecto',
  `probability` int(4) default NULL COMMENT 'Probabilidad del Proyecto',
  PRIMARY KEY  (`project`),
  KEY `IDX_PROJECT_COMMERCIAL_TARGET` (`target`),
  KEY `IDX_PROJECT_COMMERCIAL_SELLER` (`seller`),
  CONSTRAINT `FK_PROJECT_COMMERCIAL_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PROJECT_COMMERCIAL_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_PROJECT_COMMERCIAL_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Operaciones Comerciales';

INSERT INTO `project_commercial` (`project`,`target`,`seller`,`comments`,`source`,`status`,`status_date`,`probability`)
	SELECT `id`,`target`,`seller`,`comments`,`source`,`status`,`status_date`,`probability`	FROM `project`;

ALTER TABLE `project` DROP FOREIGN KEY `FK_PROJECT_TARGET`;

ALTER TABLE `project` DROP FOREIGN KEY `FK_PROJECT_SELLER`;

ALTER TABLE `project` DROP INDEX `IDX_PROJECT_TARGET`;

ALTER TABLE `project` DROP INDEX `IDX_PROJECT_SELLER`;

ALTER TABLE `project` DROP `target`;

ALTER TABLE `project` DROP `seller`;

ALTER TABLE `project` DROP `comments`;

ALTER TABLE `project` DROP `source`;

ALTER TABLE `project` DROP `status`;

ALTER TABLE `project` DROP `status_date`;

ALTER TABLE `project` DROP `probability`;

ALTER TABLE `project` ADD `alias` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Alias del Proyecto' AFTER `name`;

ALTER TABLE `project` ADD `tas` tinyint(1) default '0' COMMENT 'Indica si se trata de una Orden de Reparacion o Fabricacion';

ALTER TABLE `project` ADD `commercial` tinyint(1) default '0' COMMENT 'Indica si se trata de una Operacion Comercial';

ALTER TABLE `project` ADD `dossier` tinyint(1) default '0' COMMENT 'Indica si se trata de un Expediente de Cliente';

UPDATE `project` SET `commercial` = 1;


UPDATE `db_version` SET `version_number` = '6.7.0';

COMMIT;
