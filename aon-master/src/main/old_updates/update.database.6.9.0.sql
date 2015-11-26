# Database: aon_master
# Version: Actualizacion de la version 6.9.0 a la version 6.10.0.
# Created by: girazu
# Creation Date: 16/08/2011 09:55
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

CREATE TABLE `mod145` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `contract` int(4) NOT NULL COMMENT 'Identificador del contrato',
  `date` date default NULL COMMENT 'Fecha emision del modelo',
  `family_situation` tinyint(2) default '0' COMMENT 'Situacion familiar',
  `spouse_document` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Numero de Documento del conyuge',
  `disability_level` tinyint(2) default '0' COMMENT 'Grado de discapacidad',
  `dependence` boolean default false COMMENT 'Dependencia de terceras personas',
  `moving_date` date default NULL COMMENT 'Fecha de movilidad geografica',
  `labour_prolongation` boolean default false COMMENT 'Prolongacion de la actividad laboral',
  `descendient_count` tinyint(2) default NULL COMMENT 'Numero de hijos',
  PRIMARY KEY  (`id`),
  KEY `IDX_MOD145_CONTRACT` (`contract`),
  CONSTRAINT `FK_MOD145_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comunicacion del modelo 145';

CREATE TABLE `mod145_descendients` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `mod145` int(4) NOT NULL COMMENT 'Identificador del modelo 145',
  `birth_year` int(4) default NULL COMMENT 'Anio de nacimiento',
  `adoption_year` int(4) default NULL COMMENT 'Anio de adopcion',
  `disability_level` tinyint(2) default '0' COMMENT 'Grado de discapacidad',
  `dependence` boolean default false COMMENT 'Dependencia de terceras personas',
  `unique_parent` boolean default false COMMENT 'Computo por entero de hijos o descendientes',
  PRIMARY KEY  (`id`),
  KEY `IDX_MOD145_DESCENDIENTS_MOD145` (`mod145`),
  CONSTRAINT `FK_MOD145_DESCENDIENTS_MOD145` FOREIGN KEY (`mod145`) REFERENCES `mod145` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Descendientes del modelo 145';

CREATE TABLE `mod145_ascendants` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `mod145` int(4) NOT NULL COMMENT 'Identificador del modelo 145',
  `birth_year` int(4) default NULL COMMENT 'Anio de nacimiento',
  `disability_level` tinyint(2) default '0' COMMENT 'Grado de discapacidad',
  `dependence` boolean default false COMMENT 'Dependencia de terceras personas',
  `another_descendient` boolean default false COMMENT 'Convivencia con otros descendientes',
  PRIMARY KEY  (`id`),
  KEY `IDX_MOD145_ASCENDIENTS_MOD145` (`mod145`),
  CONSTRAINT `FK_MOD145_ASCENDIENTS_MOD145` FOREIGN KEY (`mod145`) REFERENCES `mod145` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ascendientes del modelo 145';

UPDATE system_data SET start_date='2010-01-01' WHERE name='OCUPACION_IT';

UPDATE system_data SET start_date='2010-01-01' WHERE name='OCUPACION_IMS';

UPDATE `db_version` SET `version_number` = '6.10.0';

COMMIT;
