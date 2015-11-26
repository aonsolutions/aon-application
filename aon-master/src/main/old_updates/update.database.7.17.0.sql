# Database: aon_master
# Version: Actualizacion de la version 7.17.0 a la version 7.17.1.
# Created by: girazu
# Creation Date: 27/05/2013 13:15
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `newsletter` DROP `width`;
ALTER TABLE `newsletter` DROP `title_color`;
ALTER TABLE `newsletter` DROP `background_color`;
ALTER TABLE `newsletter` DROP FOREIGN KEY `FK_NEWSLETTER_HEADER_TEMPLATE`;
ALTER TABLE `newsletter` DROP KEY `IDX_NEWSLETTER_HEADER_TEMPLATE`;
ALTER TABLE `newsletter` DROP `header_template`;
ALTER TABLE `newsletter` DROP FOREIGN KEY `FK_NEWSLETTER_FOOTER_TEMPLATE`;
ALTER TABLE `newsletter` DROP KEY `IDX_NEWSLETTER_FOOTER_TEMPLATE`;
ALTER TABLE `newsletter` DROP `footer_template`;
ALTER TABLE `newsletter` ADD `template` int(4) DEFAULT NULL COMMENT 'Identificador de la Plantilla de Marketing';
ALTER TABLE `newsletter` ADD KEY `IDX_NEWSLETTER_MK_TEMPLATE` (`template`);
ALTER TABLE `newsletter` ADD CONSTRAINT `FK_NEWSLETTER_MK_TEMPLATE` FOREIGN KEY (`template`) REFERENCES `mk_template` (`id`);

ALTER TABLE `news` MODIFY `category` int(4) DEFAULT NULL COMMENT 'Categoria de la Noticia';
ALTER TABLE `news` ADD `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de Contenido';
ALTER TABLE `news` ADD `template` int(4) DEFAULT NULL COMMENT 'Identificador de la Plantilla de Marketing';
ALTER TABLE `news` ADD KEY `IDX_NEWS_MK_TEMPLATE` (`template`);
ALTER TABLE `news` ADD CONSTRAINT `FK_NEWS_MK_TEMPLATE` FOREIGN KEY (`template`) REFERENCES `mk_template` (`id`);

INSERT INTO `news` (domain,type,scope,title,description,content,active,init_date,rattach)
SELECT domain,1,scope,name,IFNULL(subject,name),data,active,creationDate,rattach FROM `mk_template`;

UPDATE `mk_action` SET `template` = NULL;

DELETE FROM `mk_template`;

ALTER TABLE `mk_template` DROP `append_signature`;
ALTER TABLE `mk_template` DROP `data`;
ALTER TABLE `mk_template` DROP FOREIGN KEY `FK_MK_TEMPLATE_RATTACH`;
ALTER TABLE `mk_template` DROP KEY `IDX_MK_TEMPLATE_RATTACH`;
ALTER TABLE `mk_template` DROP `rattach`;
ALTER TABLE `mk_template` ADD `width` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Ancho de la Plantilla';
ALTER TABLE `mk_template` ADD `title_color` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Color del titulo de la Plantilla';
ALTER TABLE `mk_template` ADD `background_color` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Color de fondo de la Plantilla';
ALTER TABLE `mk_template` ADD `header_template` int(4) DEFAULT NULL COMMENT 'Identificador de la Plantilla de Cabecera';
ALTER TABLE `mk_template` ADD KEY `IDX_MK_TEMPLATE_HEADER_TEMPLATE` (`header_template`);
ALTER TABLE `mk_template` ADD CONSTRAINT `FK_MK_TEMPLATE_HEADER_TEMPLATE` FOREIGN KEY (`header_template`) REFERENCES `rattach` (`id`);
ALTER TABLE `mk_template` ADD `footer_template` int(4) DEFAULT NULL COMMENT 'Identificador de la Plantilla de Pie de Pagina';
ALTER TABLE `mk_template` ADD KEY `IDX_MK_TEMPLATE_FOOTER_TEMPLATE` (`footer_template`);
ALTER TABLE `mk_template` ADD CONSTRAINT `FK_MK_TEMPLATE_FOOTER_TEMPLATE` FOREIGN KEY (`footer_template`) REFERENCES `rattach` (`id`); 


UPDATE `db_version` SET `version_number` = '7.17.1';

COMMIT;
