# Database: aon_master
# Version: Actualizacion de la version 5.4.0 a la version 5.4.1.
# Created by: girazu
# Creation Date: 30/07/2010 14:45
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

CREATE TABLE `holiday` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de la Festividad',
  `holiday` int(4) default NULL COMMENT 'Identificador de Festividad',
  `editable` tinyint(1) default '0' COMMENT 'Indica si es editable o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_HOLIDAY_HOLIDAY` (`holiday`),
  CONSTRAINT `FK_HOLIDAY_HOLIDAY` FOREIGN KEY (`holiday`) REFERENCES `holiday` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Festividades';

CREATE TABLE `holiday_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `holiday` int(4) NOT NULL COMMENT 'Identificador de Festividad',
  `date` date NOT NULL COMMENT 'Fecha Festiva',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de Festividad',
  PRIMARY KEY  (`id`),
  KEY `IDX_HOLIDAY_DETAIL_HOLIDAY` (`holiday`),
  CONSTRAINT `FK_HOLIDAY_DETAIL_HOLIDAY` FOREIGN KEY (`holiday`) REFERENCES `holiday` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de Festividades';

ALTER TABLE `lh_position` DROP FOREIGN KEY `lh_position_ibfk_5`;

DROP TABLE `calendar_period`;

DROP TABLE `calendar_holiday`;

DROP TABLE `calendar`;

CREATE TABLE `calendar` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `holiday` int(4) default NULL COMMENT 'Identificador de Festivos',
  `source` tinyint(2) default '0' COMMENT 'Origen',
  `source_id` int(4) NOT NULL COMMENT 'Identificador unico de la fuente',
  `anual_hours` double default '0' COMMENT 'Horas anuales del Calendario',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Calendario',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios',
  `monday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `monday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `tuesday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `tuesday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `wednesday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `wednesday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `thursday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `thursday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `friday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `friday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `saturday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `saturday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `sunday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `sunday_hours` double default '0' COMMENT 'Numero de horas laborables',
  PRIMARY KEY  (`id`),
  KEY `IDX_CALENDAR_HOLIDAY` (`holiday`),
  CONSTRAINT `FK_CALENDAR_HOLIDAY` FOREIGN KEY (`holiday`) REFERENCES `holiday` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Calendarios Laborales';

CREATE TABLE `calendar_holiday` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `calendar` int(4) NOT NULL default '0' COMMENT 'Identificador del Calendario',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Festivo',
  `date` date default NULL COMMENT 'Fecha del festivo',
  PRIMARY KEY  (`id`),
  KEY `IDX_CALENDAR_HOLIDAY_CALENDAR` (`calendar`),
  CONSTRAINT `FK_CALENDAR_HOLIDAY_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Festivos de Calendarios';

CREATE TABLE `calendar_period` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `calendar` int(4) NOT NULL COMMENT 'Identificador del Calendario',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Periodo',
  `month` tinyint(2) default '0' COMMENT 'Mes del periodo',
  `start_day` tinyint(2) default '0' COMMENT 'Dia inicio del periodo',
  `end_day` tinyint(2) default '0' COMMENT 'Dia fin del periodo',
  `monday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `monday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `tuesday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `tuesday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `wednesday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `wednesday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `thursday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `thursday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `friday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `friday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `saturday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `saturday_hours` double default '0' COMMENT 'Numero de horas laborables',
  `sunday` tinyint(2) default '0' COMMENT 'Tipo de dia',
  `sunday_hours` double default '0' COMMENT 'Numero de horas laborables',
  PRIMARY KEY  (`id`),
  KEY `IDX_CALENDAR_PERIOD_CALENDAR` (`calendar`),
  CONSTRAINT `FK_CALENDAR_PERIOD_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Periodos de Calendarios';

UPDATE `lh_position` SET calendar = NULL;

ALTER TABLE `lh_position` ADD CONSTRAINT `lh_position_ibfk_5` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`);

ALTER TABLE `enterprise` ADD `scope` int(4) default 1 COMMENT 'Identificador del Ambito';

ALTER TABLE `enterprise` MODIFY `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito';

ALTER TABLE `enterprise` ADD KEY `IDX_ENTERPRISE_SCOPE` (`scope`);

ALTER TABLE `enterprise` ADD CONSTRAINT `FK_ENTERPRISE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);


UPDATE `db_version` SET `version_number` = '5.4.1';

COMMIT;
