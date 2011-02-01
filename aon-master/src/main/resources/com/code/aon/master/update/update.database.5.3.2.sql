# Database: aon_master
# Version: Actualizacion de la version 5.3.2 a la version 5.4.0.
# Created by: girazu
# Creation Date: 20/07/2010 14:22
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `person` ADD `social_security_num` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Numero de Seguridad Social de la Persona';

CREATE TABLE `contract_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Tipo de Contrato',
  `duration` tinyint(2) NOT NULL COMMENT 'Duracion',
  `working_day` tinyint(2) NOT NULL COMMENT 'Jornada Laboral',
  `ccc_type` tinyint(2) NOT NULL COMMENT 'Tipo de CCC',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Contratos';

CREATE TABLE `contract` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `person` int(4) NOT NULL COMMENT 'Identificador de la Persona',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `ccc` int(4) NOT NULL COMMENT 'Identificador de la Cuota de Cotizacion',
  `type` int(4) NOT NULL COMMENT 'Identificador del Tipo de Contrato',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio del Contrato',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion del Contrato',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_PERSON` (`person`),
  KEY `IDX_CONTRACT_WORKPLACE` (`workplace`),
  KEY `IDX_CONTRACT_CCC` (`ccc`),
  KEY `IDX_CONTRACT_TYPE` (`type`),
  CONSTRAINT `FK_CONTRACT_PERSON` FOREIGN KEY (`person`) REFERENCES `person` (`registry`),
  CONSTRAINT `FK_CONTRACT_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`),
  CONSTRAINT `FK_CONTRACT_CCC` FOREIGN KEY (`ccc`) REFERENCES `enterprise_ccc` (`id`),
  CONSTRAINT `FK_CONTRACT_TYPE` FOREIGN KEY (`type`) REFERENCES `contract_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contratos';

CREATE TABLE `cnae` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `code` varchar(5) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo del CNAE',
  `title` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Titulo del CNAE',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='CNAE';

CREATE TABLE `enterprise_activity` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `description` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Actividad de la Empresa',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `cnae` int(4) NOT NULL COMMENT 'Identificador del CNAE',
  `type` tinyint(2) NOT NULL COMMENT 'Tipo de Actividad de la Empresa',
  PRIMARY KEY  (`id`),
  KEY `IDX_ENTERPRISE_ACTIVITY_ENTERPRISE` (`enterprise`),
  KEY `IDX_ENTERPRISE_ACTIVITY_CNAE` (`cnae`),
  CONSTRAINT `FK_ENTERPRISE_ACTIVITY_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`),
  CONSTRAINT `FK_ENTERPRISE_ACTIVITY_CNAE` FOREIGN KEY (`cnae`) REFERENCES `cnae` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Actividades de Empresas';


UPDATE `lh_position` SET `workactivity` = NULL;

UPDATE `resource` SET `workactivity` = NULL;

UPDATE `employee` SET `workactivity` = NULL;

DELETE FROM `workactivity`;

DELETE FROM `enterprise_ccc`;

ALTER TABLE `enterprise_ccc` DROP FOREIGN KEY `FK_ENTERPRISE_CCC_ENTERPRISE`;

ALTER TABLE `enterprise_ccc` DROP INDEX `enterprise`;

ALTER TABLE `enterprise_ccc` DROP `enterprise`;

ALTER TABLE `enterprise_ccc` ADD `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Cuenta Cotizacion';

ALTER TABLE `enterprise_ccc` ADD `enterprise_activity` int(4) NOT NULL COMMENT 'Identificador de la Actividad de Empresa';

ALTER TABLE `enterprise_ccc` ADD KEY `IDX_ENTERPRISE_CCC_ENTERPRISE_ACTIVITY` (`enterprise_activity`);

ALTER TABLE `enterprise_ccc` ADD CONSTRAINT `FK_ENTERPRISE_CCC_ENTERPRISE_ACTIVITY` FOREIGN KEY (`enterprise_activity`) REFERENCES `enterprise_activity` (`id`);

ALTER TABLE `enterprise_ccc` ADD `geozone` int(4) NOT NULL COMMENT 'Identificador de la Zona Geografica';

ALTER TABLE `enterprise_ccc` ADD KEY `IDX_ENTERPRISE_CCC_GEOZONE` (`geozone`);

ALTER TABLE `enterprise_ccc` ADD CONSTRAINT `FK_ENTERPRISE_CCC_GEOZONE` FOREIGN KEY (`geozone`) REFERENCES `geozone` (`id`);

CREATE TABLE `contract_tracking` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `contract` int(4) NOT NULL COMMENT 'Identificador del Contrato',
  `date` date NOT NULL COMMENT 'Fecha del Seguimiento',
  `type` tinyint(2) default NULL COMMENT 'Tipo de excepcion',
  `duration` double default NULL COMMENT 'Duracion de la excepcion',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_TRACKING_CONTRACT` (`contract`),
  CONSTRAINT `FK_CONTRACT_TRACKING_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Seguimiento de Contratos';

ALTER TABLE `lh_position` DROP FOREIGN KEY `lh_position_ibfk_5`;

DROP TABLE `calendar`;

CREATE TABLE `calendar` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `year` int(4) NOT NULL COMMENT 'Año del Calendario',
  `anual_hours` double default '0.000' COMMENT 'Horas anuales del Calendario',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Calendario',
  `monday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `monday_hours` double default '0.000' COMMENT 'Numero de horas laborables',
  `tuesday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `tuesday_hours` double default '0.000' COMMENT 'Numero de horas laborables',
  `wednesday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `wednesday_hours` double default '0.000' COMMENT 'Numero de horas laborables',
  `thursday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `thursday_hours` double default '0.000' COMMENT 'Numero de horas laborables',
  `friday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `friday_hours` double default '0.000' COMMENT 'Numero de horas laborables',
  `saturday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `saturday_hours` double default '0.000' COMMENT 'Numero de horas laborables',
  `sunday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `sunday_hours` double default '0.000' COMMENT 'Numero de horas laborables',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Calendarios Laborales';

UPDATE `lh_position` SET calendar = NULL;

ALTER TABLE `lh_position` ADD CONSTRAINT `lh_position_ibfk_5` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`);

CREATE TABLE `calendar_period` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `calendar` int(4) NOT NULL COMMENT 'Identificador del Calendario',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Periodo',
  `from_date` date default NULL COMMENT 'Fecha de inicio del Periodo',
  `to_date` date default NULL COMMENT 'Fecha final del Periodo',
  `monday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `monday_hours` double default '0.000' COMMENT 'Numero de horas laborables',
  `tuesday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `tuesday_hours` double default '0.000' COMMENT 'Numero de horas laborables',
  `wednesday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `wednesday_hours` double default '0.000' COMMENT 'Numero de horas laborables',
  `thursday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `thursday_hours` double default '0.000' COMMENT 'Numero de horas laborables',
  `friday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `friday_hours` double default '0.000' COMMENT 'Numero de horas laborables',
  `saturday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `saturday_hours` double default '0.000' COMMENT 'Numero de horas laborables',
  `sunday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `sunday_hours` double default '0.000' COMMENT 'Numero de horas laborables',
  PRIMARY KEY  (`id`),
  KEY `IDX_CALENDAR_PERIOD_CALENDAR` (`calendar`),
  CONSTRAINT `FK_CALENDAR_PERIOD_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Periodos de Calendarios';

CREATE TABLE `calendar_holiday` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Festivo',
  `calendar` int(4) NOT NULL default '0' COMMENT 'Identificador del Calendario',
  `type` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `date` date default NULL COMMENT 'Fecha del festivo',
  PRIMARY KEY  (`id`),
  KEY `IDX_CALENDAR_HOLIDAY_CALENDAR` (`calendar`),
  CONSTRAINT `FK_CALENDAR_HOLIDAY_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Festivos de Calendarios';


UPDATE `db_version` SET `version_number` = '5.4.0';

COMMIT;
