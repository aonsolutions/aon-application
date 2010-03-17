# Database: aon_cms_master
# Version: Actualizacion de la version 2.0.5 a la version 2.1.0
# Created by: atellitu
# Creation Date: 17/03/2010 10:00

ALTER TABLE `banner` DROP FOREIGN KEY `banner_fk`;

ALTER TABLE `banner` ADD CONSTRAINT `banner_fk` FOREIGN KEY (`banner_category`) REFERENCES `banner_category` (`id`);

ALTER TABLE `album` DROP FOREIGN KEY `album_fk`;

ALTER TABLE `album` ADD CONSTRAINT `album_fk` FOREIGN KEY (`album_category`) REFERENCES `album_category` (`id`);

ALTER TABLE `direct_access` DROP FOREIGN KEY `direct_access_fk`;

ALTER TABLE `direct_access` ADD CONSTRAINT `direct_access_fk` FOREIGN KEY (`direct_access_group`) REFERENCES `direct_access_group` (`id`);

ALTER TABLE `bulletin_emails` ADD `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de email activa';

ALTER TABLE `company` ADD `coordinates` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Coordinadas GPS de la Empresa';

ALTER TABLE `album` ADD `thumb_width` INTEGER(4) DEFAULT NULL COMMENT 'Ancho de los Thumbnails';

ALTER TABLE `activity` ADD `items_per_page` TINYINT(2) DEFAULT '20' COMMENT 'Items que se mostraran por pagina';

ALTER TABLE `link_category` ADD `items_per_page` TINYINT(2) DEFAULT '20' COMMENT 'Items que se mostraran por pagina';

ALTER TABLE `faq_category` ADD `items_per_page` TINYINT(2) DEFAULT '20' COMMENT 'Items que se mostraran por pagina';

ALTER TABLE `article_category` ADD `items_per_page` TINYINT(2) DEFAULT '20' COMMENT 'Items que se mostraran por pagina';

UPDATE `db_version` SET `version_number` = '2.1.0';

COMMIT;