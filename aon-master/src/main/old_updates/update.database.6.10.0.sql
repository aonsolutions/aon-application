# Database: aon_master
# Version: Actualizacion de la version 6.10.0 a la version 6.11.0.
# Created by: girazu
# Creation Date: 18/08/2011 12:28
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

CREATE TABLE `irpf_data` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `contract` int(4) NOT NULL COMMENT 'Identificador del contrato',
  `date` date default NULL COMMENT 'Fecha del modelo',
  `family_situation` tinyint(2) default '0' COMMENT 'Situacion familiar',
  `spouse_document` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Numero de Documento del conyuge',
  `disability_level` tinyint(2) default '0' COMMENT 'Grado de discapacidad',
  `dependence` boolean default false COMMENT 'Dependencia de terceras personas',
  `moving_date` date default NULL COMMENT 'Fecha de movilidad geografica',
  `labour_prolongation` boolean default false COMMENT 'Prolongacion de la actividad laboral',
  `descendient_count` tinyint(2) default NULL COMMENT 'Numero de hijos',
  PRIMARY KEY  (`id`),
  KEY `IDX_IRPF_DATA_CONTRACT` (`contract`),
  CONSTRAINT `FK_IRPF_DATA_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Dator de irpf';

CREATE TABLE `irpf_data_descendients` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `irpf_data` int(4) NOT NULL COMMENT 'Identificador del irpf',
  `birth_year` int(4) default NULL COMMENT 'Anio de nacimiento',
  `adoption_year` int(4) default NULL COMMENT 'Anio de adopcion',
  `disability_level` tinyint(2) default '0' COMMENT 'Grado de discapacidad',
  `dependence` boolean default false COMMENT 'Dependencia de terceras personas',
  `unique_parent` boolean default false COMMENT 'Computo por entero de hijos o descendientes',
  PRIMARY KEY  (`id`),
  KEY `IDX_IRPF_DATA_DESCENDIENTS_IRPF_DATA` (`irpf_data`),
  CONSTRAINT `FK_IRPF_DATA_DESCENDIENTS_IRPF_DATA` FOREIGN KEY (`irpf_data`) REFERENCES `irpf_data` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Descendientes del modelo 145';

CREATE TABLE `irpf_data_ascendants` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `irpf_data` int(4) NOT NULL COMMENT 'Identificador del irpf',
  `birth_year` int(4) default NULL COMMENT 'Anio de nacimiento',
  `disability_level` tinyint(2) default '0' COMMENT 'Grado de discapacidad',
  `dependence` boolean default false COMMENT 'Dependencia de terceras personas',
  `another_descendient` boolean default false COMMENT 'Convivencia con otros descendientes',
  PRIMARY KEY  (`id`),
  KEY `IDX_IRPF_DATA_ASCENDIENTS_IRPF_DATA` (`irpf_data`),
  CONSTRAINT `FK_IRPF_DATA_ASCENDIENTS_IRPF_DATA` FOREIGN KEY (`irpf_data`) REFERENCES `irpf_data` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ascendientes del modelo 145';

UPDATE `db_version` SET `version_number` = '6.11.0';

COMMIT;
