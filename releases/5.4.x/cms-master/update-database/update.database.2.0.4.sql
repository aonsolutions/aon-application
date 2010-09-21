# Database: aon_cms_master
# Version: Actualizacion de la version 2.0.4 a la version 2.0.5
# Created by: atellitu
# Creation Date: 02/09/2009 10:00

ALTER TABLE `diary` ADD `section` INTEGER(4) DEFAULT NULL COMMENT 'Seccion';

ALTER TABLE `diary` ADD KEY `section` (`section`);

ALTER TABLE `diary` ADD CONSTRAINT `FK_SECTION_SECTION` FOREIGN KEY (`section`) REFERENCES `section` (`id`);

ALTER TABLE `diary` ADD `elementSection` INTEGER(4) DEFAULT NULL COMMENT 'Seccion para los Elementos';

ALTER TABLE `diary` ADD KEY `elementSection` (`elementSection`);

ALTER TABLE `diary` ADD CONSTRAINT `FK_SECTION_ELEMENT_SECTION` FOREIGN KEY (`elementSection`) REFERENCES `section` (`id`);

ALTER TABLE `menu_option` MODIFY `type` TINYINT(2) NOT NULL COMMENT 'Tipo de pagina (Generica, Articulo, Album, etc)';

ALTER TABLE `activity` ADD `section` INTEGER(4) DEFAULT NULL COMMENT 'Seccion';

ALTER TABLE `activity` ADD KEY `section` (`section`);

ALTER TABLE `activity` ADD CONSTRAINT `FK_ACTIVITY_SECTION` FOREIGN KEY (`section`) REFERENCES `section` (`id`);

#
# Structure for the `article_config` table : 
#

CREATE TABLE `activity_config` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del config de actividad',
  `section` int(4) default NULL COMMENT 'Seccion',
  PRIMARY KEY  (`id`),
  KEY `section` (`section`),
  CONSTRAINT `activity_config_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

ALTER TABLE `modular_page_option_i18n` DROP FOREIGN KEY `modular_page_option_i18n_fk`;

ALTER TABLE `modular_page_option_i18n` ADD CONSTRAINT `modular_page_option_i18n_fk` FOREIGN KEY (`modular_page_option`) REFERENCES `modular_page_option` (`id`);

UPDATE `db_version` SET `version_number` = '2.0.5';

COMMIT;
