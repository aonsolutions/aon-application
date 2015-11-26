# Database: aon_master
# Version: Actualizacion de la version 7.0.1 a la version 7.0.2.
# Created by: girazu
# Creation Date: 19/01/2012 11:40
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `survey_response_detail` DROP FOREIGN KEY `FK_SURVEY_RESPONSE_DETAIL_QUESTION`;
ALTER TABLE `survey_response_detail` DROP KEY `question`;
ALTER TABLE `survey_response_detail` DROP FOREIGN KEY `FK_SURVEY_RESPONSE_DETAIL_SURVEY_RESPONSE`;
ALTER TABLE `survey_response_detail` DROP KEY `surveyResponse`;
ALTER TABLE `survey_response_detail` ADD KEY `IDX_SURVEY_RESPONSE_DETAIL_QUESTION` (`question`);
ALTER TABLE `survey_response_detail` ADD CONSTRAINT `FK_SURVEY_RESPONSE_DETAIL_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`);
ALTER TABLE `survey_response_detail` ADD KEY `IDX_SURVEY_RESPONSE_DETAIL_SURVEY_RESPONSE` (`surveyResponse`);
ALTER TABLE `survey_response_detail` ADD CONSTRAINT `FK_SURVEY_RESPONSE_DETAIL_SURVEY_RESPONSE` FOREIGN KEY (`surveyResponse`) REFERENCES `survey_response` (`id`);

ALTER TABLE `survey_response_detail` ADD `domain` int(4) NOT NULL default '1' COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `survey_response_detail` ADD KEY `IDX_SURVEY_RESPONSE_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `survey_response_detail` ADD CONSTRAINT `FK_SURVEY_RESPONSE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `survey_response_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';

ALTER TABLE `asset` MODIFY `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Activo';

CREATE TABLE `pos` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre',
  `invoiceable` tinyint(1) NOT NULL default '0' COMMENT 'Indicador de si es facturable',
  `item` int(4) default NULL COMMENT 'Identificador del Producto',
  PRIMARY KEY  (`id`),
  KEY `IDX_POS_WORKPLACE` (`workplace`),
  KEY `IDX_POS_DOMAIN` (`domain`),
  KEY `IDX_POS_ITEM` (`item`),
  CONSTRAINT `FK_POS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_POS_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_POS_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='TPV';

CREATE TABLE `pos_shift` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `pos` int(4) NOT NULL COMMENT 'Identificador del TPV',
  `shift` tinyint(2) NOT NULL default '0' COMMENT 'Turno de trabajo',
  `user` int(4) NOT NULL COMMENT 'Identificador del Usuario',
  `start_time` datetime NOT NULL COMMENT 'Fecha-hora de apertura',
  `end_time` datetime default NULL COMMENT 'Fecha-hora de cierre',
  `initial_amount` double(15,2) default '0.00' COMMENT 'Efectivo inicial',
  PRIMARY KEY  (`id`),
  KEY `IDX_POS_SHIFT_POS` (`pos`),
  KEY `IDX_POS_SHIFT_USER` (`user`),
  KEY `IDX_POS_SHIFT_DOMAIN` (`domain`),
  CONSTRAINT `FK_POS_SHIFT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_POS_SHIFT_POS` FOREIGN KEY (`pos`) REFERENCES `pos` (`id`),
  CONSTRAINT `FK_POS_SHIFT_USER` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Turno de trabajo del TPV';

CREATE TABLE `pos_shift_count` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `pos_shift` int(4) NOT NULL COMMENT 'Identificador del Turno de trabajo',
  `pay_method` int(4) default NULL COMMENT 'Identificador de la Forma de pago',
  `amount` double(15,2) default '0.00' COMMENT 'Total efectivo',
  PRIMARY KEY  (`id`),
  KEY `IDX_POS_SHIFT_COUNT_POS_SHIFT` (`pos_shift`),
  KEY `IDX_POS_SHIFT_COUNT_PAY_METHOD` (`pay_method`),
  KEY `IDX_POS_SHIFT_COUNT_DOMAIN` (`domain`),
  CONSTRAINT `FK_POS_SHIFT_COUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_POS_SHIFT_COUNT_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_POS_SHIFT_COUNT_POS_SHIFT` FOREIGN KEY (`pos_shift`) REFERENCES `pos_shift` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Arqueo del TPV';

ALTER TABLE `cnae` DROP FOREIGN KEY `FK_CNAE_DOMAIN`;
ALTER TABLE `cnae` DROP KEY `IDX_CNAE_DOMAIN`;
ALTER TABLE `cnae` DROP `domain`;

ALTER TABLE `cnae2009` DROP FOREIGN KEY `FK_CNAE2009_DOMAIN`;
ALTER TABLE `cnae2009` DROP KEY `IDX_CNAE2009_DOMAIN`;
ALTER TABLE `cnae2009` DROP `domain`;

ALTER TABLE `cnae2009_rate` DROP FOREIGN KEY `FK_CNAE2009_RATE_DOMAIN`;
ALTER TABLE `cnae2009_rate` DROP KEY `IDX_CNAE2009_RATE_DOMAIN`;
ALTER TABLE `cnae2009_rate` DROP `domain`;

ALTER TABLE `cno` DROP FOREIGN KEY `FK_CNO_DOMAIN`;
ALTER TABLE `cno` DROP KEY `IDX_CNO_DOMAIN`;
ALTER TABLE `cno` DROP `domain`;

ALTER TABLE `geozone_irpf` DROP FOREIGN KEY `FK_GEOZONE_IRPF_DOMAIN`;
ALTER TABLE `geozone_irpf` DROP KEY `IDX_GEOZONE_IRPF_DOMAIN`;
ALTER TABLE `geozone_irpf` DROP `domain`;
ALTER TABLE `geozone_irpf` DROP FOREIGN KEY `FK_GEOZONE_IRPF_GEOZONE`;
ALTER TABLE `geozone_irpf` DROP KEY `IDX_GEOZONE_IRPF_GEOZONE`;
ALTER TABLE `geozone_irpf` ADD `geozone_code` varchar(3) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo de la Zona Geografica' AFTER `geozone`;
UPDATE `geozone_irpf` SET `geozone_code` = (SELECT `code` FROM `geozone` WHERE `id` = `geozone_irpf`.`geozone`);
ALTER TABLE `geozone_irpf` DROP `geozone`;

ALTER TABLE `geozone_irpf_descendant` DROP FOREIGN KEY `FK_GEOZONE_IRPF_DESCENDANT_DOMAIN`;
ALTER TABLE `geozone_irpf_descendant` DROP KEY `IDX_GEOZONE_IRPF_DESCENDANT_DOMAIN`;
ALTER TABLE `geozone_irpf_descendant` DROP `domain`;

ALTER TABLE `geozone_irpf_handicap` DROP FOREIGN KEY `FK_GEOZONE_IRPF_HANDICAP_DOMAIN`;
ALTER TABLE `geozone_irpf_handicap` DROP KEY `IDX_GEOZONE_IRPF_HANDICAP_DOMAIN`;
ALTER TABLE `geozone_irpf_handicap` DROP `domain`;


UPDATE `db_version` SET `version_number` = '7.0.2';

COMMIT;
