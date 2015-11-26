# Database: aon_master
# Version: Actualizacion de la version 7.15.0 a la version 7.15.1.
# Created by: girazu
# Creation Date: 09/04/2013 17:15
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `news` MODIFY `title` varchar(128) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Titulo de la Noticia';

ALTER TABLE `newsletter` ADD `active` tinyint(1) default '1' COMMENT 'Indica si el Boletin esta activo o no';
ALTER TABLE `newsletter` ADD `width` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Ancho del Boletin';
ALTER TABLE `newsletter` ADD `subject` varchar(128) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Asunto del Boletin';

ALTER TABLE `news` ADD `scope` int(4) default NULL COMMENT 'Identificador del Ambito';
UPDATE `news` SET `scope` = (SELECT MIN(`id`) FROM `scope` WHERE `domain` = `news`.`domain`);
ALTER TABLE `news` MODIFY `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito';
ALTER TABLE `news` ADD KEY `IDX_NEWS_SCOPE` (`scope`);
ALTER TABLE `news` ADD CONSTRAINT `FK_NEWS_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);

ALTER TABLE `newsletter` ADD `scope` int(4) default NULL COMMENT 'Identificador del Ambito';
UPDATE `newsletter` SET `scope` = (SELECT MIN(`id`) FROM `scope` WHERE `domain` = `newsletter`.`domain`);
ALTER TABLE `newsletter` MODIFY `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito';
ALTER TABLE `newsletter` ADD KEY `IDX_NEWSLETTER_SCOPE` (`scope`);
ALTER TABLE `newsletter` ADD CONSTRAINT `FK_NEWSLETTER_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);

ALTER TABLE `category` ADD `rattach` int(4) DEFAULT NULL COMMENT 'Identificador del Archivo Adjunto';
ALTER TABLE `category` ADD KEY `IDX_CATEGORY_RATTACH` (`rattach`);
ALTER TABLE `category` ADD CONSTRAINT `FK_CATEGORY_RATTACH` FOREIGN KEY (`rattach`) REFERENCES `rattach` (`id`);

ALTER TABLE `training_course` MODIFY `modality` tinyint(2) NOT NULL default '0' COMMENT 'Modalidad del Curso Formativo';
ALTER TABLE `training_course` CHANGE `name` `certification_name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Denominacion de la certificacion';
ALTER TABLE `training_course` ADD `fp_title_name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Titulo de formacion profesional';
ALTER TABLE `training_course` ADD `occupation_name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Nombre de la ocupacion';
ALTER TABLE `training_course` ADD `professional_certificate` tinyint(1) NOT NULL default '0' COMMENT 'Certificado de profesionalidad';
ALTER TABLE `training_course` ADD `fp_title` tinyint(1) NOT NULL default '0' COMMENT 'Titulo de formacion profesional';
ALTER TABLE `training_course` ADD `center_available` tinyint(1) NOT NULL default '0' COMMENT 'Centro disponible'; 


UPDATE `db_version` SET `version_number` = '7.15.1';

COMMIT;
