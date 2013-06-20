# Database: aon_master
# Version: Actualizacion de la version 7.14.0 a la version 7.15.0.
# Created by: girazu
# Creation Date: 08/04/2013 18:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

CREATE TABLE `news` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `title` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Titulo de la Noticia',
  `description` varchar(1024) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Noticia',
  `content` text COLLATE latin1_spanish_ci NOT NULL COMMENT 'Contenido de la Noticia',
  `url` varchar(256) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Url de la Noticia',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si la Noticia esta activa o no',
  `rss` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si la Noticia se va a publicar en rss o no',
  `init_date` DATE DEFAULT NULL COMMENT 'Fecha Noticia',
  `end_date` DATE DEFAULT NULL COMMENT 'Fecha fin Noticia',
  `category` int(4) NOT NULL COMMENT 'Categoria de la Noticia',
  `rattach` int(4) DEFAULT NULL COMMENT 'Identificador del Archivo Adjunto',
  PRIMARY KEY (`id`),
  KEY `IDX_NEWS_DOMAIN` (`domain`),
  KEY `IDX_NEWS_CATEGORY` (`category`),
  KEY `IDX_NEWS_RATTACH` (`rattach`),
  CONSTRAINT `FK_NEWS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_NEWS_CATEGORY` FOREIGN KEY (`category`) REFERENCES `category` (`id`),
  CONSTRAINT `FK_NEWS_RATTACH` FOREIGN KEY (`rattach`) REFERENCES `rattach` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Noticias';

CREATE TABLE `newsletter` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `name` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Boletin',
  `date` date NOT NULL COMMENT 'Fecha del Boletin',
  `layout` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Disposicion del Boletin',
  `background_color` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Color de fondo del Boletin',
  `title_color` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Color del titulo del Boletin',
  `header_template` int(4) DEFAULT NULL COMMENT 'Identificador de la Plantilla de Cabecera',
  `footer_template` int(4) DEFAULT NULL COMMENT 'Identificador de la Plantilla de Pie de Pagina',
  PRIMARY KEY (`id`),
  KEY `IDX_NEWSLETTER_DOMAIN` (`domain`),
  KEY `IDX_NEWSLETTER_HEADER_TEMPLATE` (`header_template`),
  KEY `IDX_NEWSLETTER_FOOTER_TEMPLATE` (`footer_template`),
  CONSTRAINT `FK_NEWSLETTER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_NEWSLETTER_HEADER_TEMPLATE` FOREIGN KEY (`header_template`) REFERENCES `mk_template` (`id`),
  CONSTRAINT `FK_NEWSLETTER_FOOTER_TEMPLATE` FOREIGN KEY (`footer_template`) REFERENCES `mk_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Boletin';

CREATE TABLE `newsletter_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL DEFAULT '1' COMMENT 'Identificador del Dominio',
  `news` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Noticia',
  `newsletter` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Boletin',
  `position` int(4) NOT NULL COMMENT 'Posicion dentro del Boletin',
  PRIMARY KEY (`id`),
  KEY `IDX_NEWSLETTER_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_NEWSLETTER_DETAIL_NEWS` (`news`),
  KEY `IDX_NEWSLETTER_DETAIL_NEWSLETTER` (`newsletter`),
  CONSTRAINT `FK_NEWSLETTER_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_NEWSLETTER_DETAIL_NEWS` FOREIGN KEY (`news`) REFERENCES `news` (`id`),
  CONSTRAINT `FK_NEWSLETTER_DETAIL_NEWSLETTER` FOREIGN KEY (`newsletter`) REFERENCES `newsletter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Noticias del Boletin';

ALTER TABLE `category` ADD `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de la Categoria';
ALTER TABLE `category` ADD `description` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de la Categoria';
ALTER TABLE `category` ADD `url` varchar(256) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Url de la Categoria';

ALTER TABLE `mk_action` ADD `newsletter` int(4) DEFAULT NULL COMMENT 'Identificador del Boletin' AFTER `template`;
ALTER TABLE `mk_action` ADD KEY `IDX_MK_ACTION_NEWSLETTER` (`newsletter`);
ALTER TABLE `mk_action` ADD CONSTRAINT `FK_MK_ACTION_NEWSLETTER` FOREIGN KEY (`newsletter`) REFERENCES `newsletter` (`id`);


UPDATE `db_version` SET `version_number` = '7.15.0';

COMMIT;
