# Database: aon_master
# Version: Actualizacion de la version 4.3.0 a la version 4.4.0.
# Created by: girazu
# Creation Date: 07/10/2009 16:41
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



INSERT IGNORE INTO `account` (`id`, `description`, `alias`, `entryEnabled`, `level`) VALUES ('641000000','Indemnizaciones.',NULL,1,5);

UPDATE `app_param` SET `value` = '641000000' WHERE `value` = '461000000';

DELETE FROM `account` WHERE `id` LIKE '461%';

ALTER TABLE `question_value` MODIFY `value_text` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Valor de tipo texto';

ALTER TABLE `survey_response_detail` MODIFY `value_text` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Valor de tipo texto';

ALTER TABLE `survey_workflow` MODIFY `value_text` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Valor de tipo texto';

ALTER TABLE `target_profile` MODIFY `value_text` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Valor de tipo texto';

ALTER TABLE `offer` ADD `signed` tinyint(1) default '0' COMMENT 'Indica si el Presupuesto esta firmada electronicamente';

ALTER TABLE `offer` ADD `comments` text collate latin1_spanish_ci COMMENT 'Comentarios del Presupuesto';

CREATE TABLE `offer_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `offer` int(4) NOT NULL COMMENT 'Identificador del Presupuesto',
  `mimeType` tinyint(2) DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  PRIMARY KEY (`id`),
  KEY `IDX_OFFER_ATTACH_OFFER` (`offer`),
  CONSTRAINT `FK_OFFER_ATTACH_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Presupuestos';

CREATE TABLE `offer_term` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `offer` int(4) NOT NULL COMMENT 'Identificador del Presupuesto',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Nombre del Presupuesto',
  `description` text collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Archivo Adjunto',
  `particular` tinyint(1) default '0' COMMENT 'Indica si el Presupuesto es particular o general',
  PRIMARY KEY (`id`),
  KEY `IDX_OFFER_TERM_OFFER` (`offer`),
  CONSTRAINT `FK_OFFER_TERM_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Condiciones del Presupuesto';


UPDATE `db_version` SET `version_number` = '4.4.0';

COMMIT;
