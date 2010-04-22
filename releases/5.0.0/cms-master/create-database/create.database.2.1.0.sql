# SQL Manager 2007 for MySQL 4.3.4.1
# ---------------------------------------
# Host     : 192.168.2.30
# Port     : 3306
# Database : cms


SET FOREIGN_KEY_CHECKS=0;

CREATE DATABASE `cms-master`
    CHARACTER SET 'latin1'
    COLLATE 'latin1_swedish_ci';

USE `cms-master`;

#
# Structure for the `menu` table : 
#

CREATE TABLE `menu` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de menu',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificativo alfanumerico para diferenciar un menu de otro.',
  `type` TINYINT(2) NOT NULL COMMENT 'Tipo de menu (top, foot, sidebar,...).',
  `defaultMenu` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Indicador de menu por defecto. Cada tipo tendra un menu por defecto.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`, `type`)
)ENGINE=InnoDB
AUTO_INCREMENT=163 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Menus de sitio web';

#
# Structure for the `footer` table : 
#

CREATE TABLE `footer` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de pie de pagina.',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificador alfanumerico de pie de pagina',
  `menu` INTEGER(4) DEFAULT NULL COMMENT 'Menu asignado al pie de pagina.',
  `default_` TINYINT(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `menu` (`menu`),
  CONSTRAINT `footer_fk` FOREIGN KEY (`menu`) REFERENCES `menu` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=2 CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `banner_category` table : 
#

CREATE TABLE `banner_category` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la categoria del banner',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificativo alfanumerico para diferenciar una categoria de otra.',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de opcion activa',
  `position` TINYINT(2) NOT NULL COMMENT 'Posicion dentro de la lista',
  `section` INTEGER(4) DEFAULT NULL COMMENT 'seccion',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `section` (`section`),
  CONSTRAINT `banner_category_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Categorias de enlaces.';

#
# Structure for the `header` table : 
#

CREATE TABLE `header` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de Cabecera.',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificador alfanumerico de Cabeciera.',
  `menu` INTEGER(4) DEFAULT NULL COMMENT 'Menu recursivo asociado a la cabecera.',
  `language_menu` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Mostrar o no el menu de cambio de idiomas en la cabecera.',
  `language_menu_type` TINYINT(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de menu de idiomas. 0 = lista, 1 = drop, 2 = banderas.',
  `css` TEXT COLLATE latin1_spanish_ci COMMENT 'Codigo CSS que ira en la cabecera de la pagina.',
  `javascript` TEXT COLLATE latin1_spanish_ci COMMENT 'Codigo JavaScript que ira en al cabecera de la pagina.',
  `default_` TINYINT(1) NOT NULL DEFAULT '0',
  `banner_category` INTEGER(4) DEFAULT NULL COMMENT 'Categoria de banner asociado a la cabecera.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `menu` (`menu`),
  KEY `banner_category` (`banner_category`),
  CONSTRAINT `header_fk` FOREIGN KEY (`menu`) REFERENCES `menu` (`id`),
  CONSTRAINT `header_fk1` FOREIGN KEY (`banner_category`) REFERENCES `banner_category` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=23 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `sidebar` table : 
#

CREATE TABLE `sidebar` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de menu',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificativo alfanumerico para diferenciar un menu de otro.',
  `default_` TINYINT(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`)
)ENGINE=InnoDB
AUTO_INCREMENT=129 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Menus de sitio web.';

#
# Structure for the `section` table : 
#

CREATE TABLE `section` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la seccion.',
  `header` INTEGER(4) DEFAULT NULL COMMENT 'Cabecera',
  `sidebar` INTEGER(4) DEFAULT NULL COMMENT 'Lateral',
  `footer` INTEGER(4) DEFAULT NULL COMMENT 'Pie',
  `show_header` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Mostrar cabecera',
  `show_sidebar_left` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Mostrar Lateral',
  `show_footer` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Mostrar Pie',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '',
  `menu` INTEGER(4) DEFAULT NULL COMMENT 'Menu',
  `show_menu` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Mostrar menu',
  `default_` TINYINT(1) NOT NULL DEFAULT '0',
  `show_sidebar_right` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Mostrar Lateral',
  `menu_alt` INTEGER(4) DEFAULT NULL COMMENT 'Menu alternativo',
  `show_menu_alt` TINYINT(1) NOT NULL COMMENT 'Mostrar menu alternativo',
  `parent_` INTEGER(4) DEFAULT NULL COMMENT 'Padre',
  `parent_sidebar_right` TINYINT(1) NOT NULL DEFAULT '0',
  `parent_sidebar_left` TINYINT(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `section_fk` (`header`),
  KEY `sidebar` (`sidebar`),
  KEY `footer` (`footer`),
  KEY `menu` (`menu`),
  KEY `menu_alt` (`menu_alt`),
  KEY `parent_` (`parent_`),
  CONSTRAINT `section_fk` FOREIGN KEY (`header`) REFERENCES `header` (`id`),
  CONSTRAINT `section_fk1` FOREIGN KEY (`sidebar`) REFERENCES `sidebar` (`id`),
  CONSTRAINT `section_fk2` FOREIGN KEY (`footer`) REFERENCES `footer` (`id`),
  CONSTRAINT `section_fk3` FOREIGN KEY (`menu`) REFERENCES `menu` (`id`),
  CONSTRAINT `section_fk4` FOREIGN KEY (`menu_alt`) REFERENCES `menu` (`id`),
  CONSTRAINT `section_fk5` FOREIGN KEY (`parent_`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=118 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `activity` table : 
#

CREATE TABLE `activity` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '',
  `section` INTEGER(4) DEFAULT NULL COMMENT 'Seccion',
  `items_per_page` TINYINT(2) DEFAULT '20' COMMENT 'Items que se mostraran por pagina',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `section` (`section`),
  CONSTRAINT `FK_ACTIVITY_SECTION` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=119 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `activity_config` table : 
#

CREATE TABLE `activity_config` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del config de actividad',
  `section` INTEGER(4) DEFAULT NULL COMMENT 'Seccion',
  PRIMARY KEY (`id`),
  KEY `section` (`section`),
  CONSTRAINT `activity_config_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `language` table : 
#

CREATE TABLE `language` (
  `id` INTEGER(2) NOT NULL COMMENT 'Codigo de idioma.',
  `language` TINYINT(2) NOT NULL COMMENT 'Codigo de idioma. Enumerado Locale.',
  `description` VARCHAR(24) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Descripcion a mostrar del idioma.',
  `position` TINYINT(2) NOT NULL COMMENT 'Orden preferencia del idioma.',
  `defaultLanguage` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Indicador del idioma por defecto',
  PRIMARY KEY (`id`)
)ENGINE=InnoDB
ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Idiomas de los contenidos.';

#
# Structure for the `activity_i18n` table : 
#

CREATE TABLE `activity_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador Unico',
  `activity` INTEGER(4) NOT NULL COMMENT 'Actividad',
  `language` INTEGER(4) NOT NULL COMMENT 'Lenguaje',
  `description` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  PRIMARY KEY (`id`),
  KEY `activity` (`activity`),
  KEY `language` (`language`),
  CONSTRAINT `activity_i18n_fk` FOREIGN KEY (`activity`) REFERENCES `activity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `activity_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=184 CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Iternacionalizacion de los albums;';

#
# Structure for the `album_category` table : 
#

CREATE TABLE `album_category` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador Unico',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Alias',
  `active` TINYINT(1) NOT NULL COMMENT 'Activo',
  `position` TINYINT(2) NOT NULL COMMENT 'Posicion',
  `section` INTEGER(4) DEFAULT NULL COMMENT 'Seccion',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `album_category_fk` (`section`),
  CONSTRAINT `album_category_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=14 CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Categorias de albums;';

#
# Structure for the `album` table : 
#

CREATE TABLE `album` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador Unico',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Alias',
  `active` TINYINT(1) NOT NULL COMMENT 'Activo',
  `position` TINYINT(2) NOT NULL COMMENT 'Posicion',
  `album_category` INTEGER(4) NOT NULL COMMENT 'Categoria a la que pertenece el album',
  `publish_date` DATE DEFAULT NULL COMMENT 'Fecha de publicacion',
  `image` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Imagen asociada al album',
  `items_per_page` TINYINT(2) DEFAULT '20' COMMENT 'Items que se mostraran por pagina',
  `thumb_width` INTEGER(4) DEFAULT NULL COMMENT 'Ancho de los Thumbnails',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `album_category` (`album_category`),
  CONSTRAINT `album_fk` FOREIGN KEY (`album_category`) REFERENCES `album_category` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=24 CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Album';

#
# Structure for the `album_category_i18n` table : 
#

CREATE TABLE `album_category_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador Unico',
  `album_category` INTEGER(4) NOT NULL COMMENT 'Categoria de album',
  `language` INTEGER(4) NOT NULL COMMENT 'Lenguaje',
  `label` VARCHAR(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Label',
  PRIMARY KEY (`id`),
  KEY `album_category` (`album_category`),
  KEY `language` (`language`),
  CONSTRAINT `album_category_i18n_fk` FOREIGN KEY (`album_category`) REFERENCES `album_category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `album_category_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=25 CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Internacionalizacion de las categorias de albums;';

#
# Structure for the `album_config` table : 
#

CREATE TABLE `album_config` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del config del album',
  `section` INTEGER(4) DEFAULT NULL COMMENT 'Seccion',
  PRIMARY KEY (`id`),
  KEY `section` (`section`),
  CONSTRAINT `album_config_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=2 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `album_i18n` table : 
#

CREATE TABLE `album_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador Unico',
  `album` INTEGER(4) NOT NULL COMMENT 'Album',
  `language` INTEGER(4) NOT NULL COMMENT 'Lenguaje',
  `title` VARCHAR(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Titulo',
  `description` TEXT COLLATE latin1_spanish_ci COMMENT 'Descripcion',
  `alt` VARCHAR(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alt de la imagen asociada al album',
  PRIMARY KEY (`id`),
  KEY `album` (`album`),
  KEY `language` (`language`),
  CONSTRAINT `album_i18n_fk` FOREIGN KEY (`album`) REFERENCES `album` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `album_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=38 CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Iternacionalizacion de los albums';

#
# Structure for the `album_image` table : 
#

CREATE TABLE `album_image` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador Unico',
  `album` INTEGER(4) NOT NULL COMMENT 'Album al que pertenece la imagen',
  `active` TINYINT(1) NOT NULL COMMENT 'Activo',
  `position` TINYINT(2) NOT NULL COMMENT 'Posicion',
  `image` VARCHAR(255) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Imagen',
  `thumbnail` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Imagen en miniatura',
  PRIMARY KEY (`id`),
  KEY `album` (`album`),
  CONSTRAINT `album_image_fk` FOREIGN KEY (`album`) REFERENCES `album` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB
AUTO_INCREMENT=409 CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Cada imagen del album';

#
# Structure for the `album_image_i18n` table : 
#

CREATE TABLE `album_image_i18n` (
  `id` INTEGER(11) NOT NULL AUTO_INCREMENT COMMENT 'Identificador Unico',
  `album_image` INTEGER(4) NOT NULL COMMENT 'Imagen relacionada',
  `language` INTEGER(4) NOT NULL COMMENT 'Lenguaje',
  `title` VARCHAR(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Titulo',
  `description` TEXT COLLATE latin1_spanish_ci COMMENT 'Descripcion',
  `alt` VARCHAR(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alt asociado a la imagen',
  `alt_thumbnail` VARCHAR(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alt asociado al thumbnail',
  PRIMARY KEY (`id`),
  KEY `album_image` (`album_image`),
  KEY `language` (`language`),
  CONSTRAINT `album_image_i18n_fk` FOREIGN KEY (`album_image`) REFERENCES `album_image` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `album_image_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=535 CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Internacionalizacion de las imagenes';

#
# Structure for the `aoncms_bulletin_subscription` table : 
#

CREATE TABLE `aoncms_bulletin_subscription` (
  `id` INTEGER(11) NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(255) COLLATE latin1_spanish_ci NOT NULL DEFAULT '',
  `language` TINYINT(2) NOT NULL,
  `code` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '',
  `ip` VARCHAR(15) COLLATE latin1_spanish_ci DEFAULT NULL,
  `subscription_date` DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00',
  `valid` TINYINT(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=106 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `article_category` table : 
#

CREATE TABLE `article_category` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la categoria de articulo',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificativo alfanumerico para diferenciar una categoria de otra.',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de opcion activa',
  `position` TINYINT(2) NOT NULL COMMENT 'Posicion dentro de la lista',
  `section` INTEGER(4) DEFAULT NULL COMMENT 'Seccion',
  `elementSection` INTEGER(4) DEFAULT NULL,
  `items_per_page` TINYINT(2) DEFAULT '20' COMMENT 'Items que se mostraran por pagina',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `section` (`section`),
  KEY `elementSection` (`elementSection`),
  CONSTRAINT `article_category_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`),
  CONSTRAINT `article_category_fk1` FOREIGN KEY (`elementSection`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=100 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Categorias de articulo.';

#
# Structure for the `article` table : 
#

CREATE TABLE `article` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del articulo',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificativo alfanumerico para diferenciar un articulo de otro.',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de opcion activa',
  `position` TINYINT(2) NOT NULL COMMENT 'Posicion dentro de la lista',
  `article_category` INTEGER(4) NOT NULL COMMENT 'Categoria de articulo',
  `publish_date` DATE NOT NULL COMMENT 'Fecha publicacion',
  `expire_date` DATE DEFAULT NULL COMMENT 'Fecha expiracion',
  `type_` TINYINT(4) NOT NULL COMMENT 'Tipo de articulo',
  `image` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Imagen',
  `thumbnail` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Thumbnail',
  `init_date` DATE DEFAULT NULL COMMENT 'Fecha articulo',
  `end_date` DATE DEFAULT NULL COMMENT 'Fecha fin articulo',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `article_category` (`article_category`),
  CONSTRAINT `article_fk` FOREIGN KEY (`article_category`) REFERENCES `article_category` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=576 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='article.';

#
# Structure for the `article_category_i18n` table : 
#

CREATE TABLE `article_category_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la categoria de articulo internacionalizada.',
  `article_category` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria de articulo',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` VARCHAR(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Descripcion de la categoria de articulo.',
  PRIMARY KEY (`id`),
  KEY `article_category` (`article_category`),
  KEY `language` (`language`),
  CONSTRAINT `article_category_i18n_fk` FOREIGN KEY (`article_category`) REFERENCES `article_category` (`id`),
  CONSTRAINT `article_category_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=183 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `article_config` table : 
#

CREATE TABLE `article_config` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del config del articulo',
  `section` INTEGER(4) DEFAULT NULL COMMENT 'Seccion',
  PRIMARY KEY (`id`),
  KEY `section` (`section`),
  CONSTRAINT `article_config_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=2 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `article_document` table : 
#

CREATE TABLE `article_document` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador Unico',
  `article` INTEGER(4) NOT NULL COMMENT 'Articulo al que pertenece el documento',
  `alias` VARCHAR(255) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Alias',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `article` (`article`),
  CONSTRAINT `article_document_fk` FOREIGN KEY (`article`) REFERENCES `article` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB
AUTO_INCREMENT=358 CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Cada fichero del articulo';

#
# Structure for the `article_document_i18n` table : 
#

CREATE TABLE `article_document_i18n` (
  `id` INTEGER(11) NOT NULL AUTO_INCREMENT COMMENT 'Identificador Unico',
  `article_document` INTEGER(4) NOT NULL COMMENT 'Fichero relacionada',
  `language` INTEGER(4) NOT NULL COMMENT 'Lenguaje',
  `file` VARCHAR(255) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Fichero',
  `title` VARCHAR(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Titulo',
  `description` TEXT COLLATE latin1_spanish_ci COMMENT 'Descripcion',
  PRIMARY KEY (`id`),
  KEY `article_document` (`article_document`),
  KEY `language` (`language`),
  CONSTRAINT `article_document_i18n_fk` FOREIGN KEY (`article_document`) REFERENCES `article_document` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `article_document_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=346 CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Internacionalizacion de las ficheros';

#
# Structure for the `article_i18n` table : 
#

CREATE TABLE `article_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del texto del articulo',
  `article` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria del articulo',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma del enlace',
  `title` VARCHAR(255) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Titulo del articulo.',
  `subtitle` VARCHAR(255) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Subtitulo del articulo.',
  `content` TEXT COLLATE latin1_spanish_ci NOT NULL COMMENT 'Contenido del articulo.',
  `alt` VARCHAR(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alt de la imagen',
  `alt_thumbnail` VARCHAR(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alt del thumbnail',
  `image_info` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Pie',
  PRIMARY KEY (`id`),
  KEY `article` (`article`),
  KEY `language` (`language`),
  CONSTRAINT `article_i18n_fk` FOREIGN KEY (`article`) REFERENCES `article` (`id`),
  CONSTRAINT `article_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=897 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `article_related` table : 
#

CREATE TABLE `article_related` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador',
  `article_parent` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con el articulo',
  `article_related` INTEGER(4) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `article` (`article_parent`),
  KEY `article_related` (`article_related`),
  CONSTRAINT `article_related_fk` FOREIGN KEY (`article_parent`) REFERENCES `article` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `article_related_fk1` FOREIGN KEY (`article_related`) REFERENCES `article` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB
AUTO_INCREMENT=42 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `banner` table : 
#

CREATE TABLE `banner` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del banner',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificativo alfanumerico para diferenciar un banner de otro.',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de opcion activa',
  `position` TINYINT(2) NOT NULL COMMENT 'Posicion dentro de la lista',
  `banner_category` INTEGER(4) NOT NULL COMMENT 'Categoria de banners',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `banner_category` (`banner_category`),
  CONSTRAINT `banner_fk` FOREIGN KEY (`banner_category`) REFERENCES `banner_category` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Enlaces.';

#
# Structure for the `banner_category_i18n` table : 
#

CREATE TABLE `banner_category_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la categoria de enlace internacionalizada.',
  `banner_category` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria del banner',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` VARCHAR(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Descripcion de la categoria de banner.',
  PRIMARY KEY (`id`),
  KEY `banner_category` (`banner_category`),
  KEY `language` (`language`),
  CONSTRAINT `banner_category_i18n_fk` FOREIGN KEY (`banner_category`) REFERENCES `banner_category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `banner_category_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `banner_i18n` table : 
#

CREATE TABLE `banner_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del texto del banner',
  `banner` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria del banner',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma del banner',
  `label` VARCHAR(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Descripcion del banner.',
  `url` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Url del banner',
  `image` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Imagen del banner',
  `description` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del banner',
  PRIMARY KEY (`id`),
  KEY `banner` (`banner`),
  KEY `language` (`language`),
  CONSTRAINT `banner_i18n_fk` FOREIGN KEY (`banner`) REFERENCES `banner` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `banner_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `brand` table : 
#

CREATE TABLE `brand` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la marca',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificativo alfanumerico para diferenciar una marca de otra.',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de opcion activa',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Marcas.';

#
# Structure for the `brand_i18n` table : 
#

CREATE TABLE `brand_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la marca internacionalizada.',
  `brand` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la marca',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` VARCHAR(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Descripcion de la marca.',
  PRIMARY KEY (`id`),
  KEY `brand` (`brand`),
  KEY `language` (`language`),
  CONSTRAINT `brand_i18n_fk` FOREIGN KEY (`brand`) REFERENCES `brand` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `brand_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `bulletin` table : 
#

CREATE TABLE `bulletin` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador Unico',
  `alias` VARCHAR(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Alias',
  `publish_date` DATE NOT NULL COMMENT 'Fecha de publicacion',
  `template` VARCHAR(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Template',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  UNIQUE KEY `alias_2` (`alias`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Bulletin';

#
# Structure for the `bulletin_article` table : 
#

CREATE TABLE `bulletin_article` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del articulo',
  `bulletin` INTEGER(4) NOT NULL COMMENT 'boletin',
  `article` INTEGER(4) NOT NULL COMMENT 'articulo',
  `position` TINYINT(2) NOT NULL COMMENT 'Posicion dentro de la lista',
  PRIMARY KEY (`id`),
  UNIQUE KEY `bulletin_article` (`bulletin`, `article`),
  KEY `bulletin` (`bulletin`),
  KEY `article` (`article`),
  CONSTRAINT `bulletin_article_fk` FOREIGN KEY (`bulletin`) REFERENCES `bulletin` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `bulletin_article_fk1` FOREIGN KEY (`article`) REFERENCES `article` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `bulletin_emails` table : 
#

CREATE TABLE `bulletin_emails` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador Unico',
  `email` VARCHAR(255) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'email',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de email activa',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`, `language`),
  KEY `language` (`language`),
  CONSTRAINT `bulletin_emails_fk` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=11 CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Bulletin emails; (`language`) REFER `cms-am-org/language`';

#
# Structure for the `bulletin_i18n` table : 
#

CREATE TABLE `bulletin_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del texto del boletin',
  `bulletin` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria del boletin',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma del enlace',
  `title` VARCHAR(255) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Titulo del boletin.',
  `content` TEXT COLLATE latin1_spanish_ci COMMENT 'Contenido del boletin.',
  PRIMARY KEY (`id`),
  KEY `bulletin` (`bulletin`),
  KEY `language` (`language`),
  CONSTRAINT `bulletin_i18n_fk` FOREIGN KEY (`bulletin`) REFERENCES `bulletin` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `bulletin_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `company` table : 
#

CREATE TABLE `company` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador',
  `name` VARCHAR(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'nombre',
  `telephone` VARCHAR(20) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'telefono',
  `fax` VARCHAR(20) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'fax',
  `email` VARCHAR(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'email',
  `address` VARCHAR(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'direccion',
  `locality` VARCHAR(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'localidad',
  `province` VARCHAR(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'provincia',
  `postal_code` VARCHAR(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'codigo postal',
  `web` VARCHAR(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'web',
  `logo` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'logotipo',
  `coordinates` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Coordinadas GPS de la Empresa',
  PRIMARY KEY (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=302 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `company_activity` table : 
#

CREATE TABLE `company_activity` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador',
  `company` INTEGER(4) DEFAULT NULL COMMENT 'Compañia',
  `activity` INTEGER(4) DEFAULT NULL COMMENT 'Actividad',
  PRIMARY KEY (`id`),
  UNIQUE KEY `company_activity` (`company`, `activity`),
  KEY `company` (`company`),
  KEY `activity` (`activity`),
  CONSTRAINT `company_activity_activity_fk` FOREIGN KEY (`activity`) REFERENCES `activity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `company_activity_company_fk` FOREIGN KEY (`company`) REFERENCES `company` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB
AUTO_INCREMENT=338 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `config` table : 
#

CREATE TABLE `config` (
  `id` INTEGER(4) NOT NULL COMMENT 'Identificador de la configuracion.',
  `online` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de si la pagina esta operativa o no.',
  `domain` VARCHAR(128) COLLATE latin1_spanish_ci NOT NULL DEFAULT 'mydomain.com' COMMENT 'Dominio correspondiente a la pagina.',
  `preview_host` VARCHAR(24) COLLATE latin1_spanish_ci DEFAULT 'preview' COMMENT 'Host para la previsualizacion de la pagina.',
  `host` VARCHAR(24) COLLATE latin1_spanish_ci DEFAULT 'www' COMMENT 'Host para la publicacion de la pagina.',
  `ftp_server` VARCHAR(128) COLLATE latin1_spanish_ci DEFAULT 'ftp.mydomain.com' COMMENT 'Direccion del servidor FTP donde se publicaran las paginas web.',
  `ftp_user` VARCHAR(32) COLLATE latin1_spanish_ci DEFAULT 'user' COMMENT 'Nombre de usuario del FTP.',
  `ftp_password` CHAR(32) COLLATE latin1_spanish_ci DEFAULT 'password' COMMENT 'Password del usuario del FTP.',
  `ftp_path` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT '/mydomain.com/WEBSITES/www.mydomain.com' COMMENT 'Ruta del directorio del ftp donde guardar las paginas.',
  `smtp_server` VARCHAR(128) COLLATE latin1_spanish_ci DEFAULT 'smtp.mydomain.com' COMMENT 'Direccion del servidor SMTP a traves del cual se enviaran los mail desde la pagina.',
  `smtp_auth` TINYINT(1) DEFAULT '0' COMMENT 'Indica si tiene o no autentificacion el servidor de correo saliente.',
  `smtp_user` VARCHAR(32) COLLATE latin1_spanish_ci DEFAULT 'user' COMMENT 'Nombre de usuario del servidor de correo saliente.',
  `smtp_password` VARCHAR(32) COLLATE latin1_spanish_ci DEFAULT 'password' COMMENT 'Password de usuario del servidor de correo saliente.',
  `from_name` VARCHAR(64) COLLATE latin1_spanish_ci DEFAULT 'Administrator' COMMENT 'Nombre del remitente de correo electronico.',
  `from_email` VARCHAR(128) COLLATE latin1_spanish_ci DEFAULT 'admin@mydomain.com' COMMENT 'Direccion de correo de envio de los mensajes.',
  `template` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT 'default' COMMENT 'Plantilla con la que se va a generar el sitio.',
  `footer` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador para mostrar o no el pie de pagina.',
  `preview_ftp_server` VARCHAR(128) COLLATE latin1_spanish_ci DEFAULT 'preview.ftp.mydomain.com' COMMENT 'Direccion del servidor FTP donde se publicaran las paginas web preview.',
  `preview_ftp_user` VARCHAR(32) COLLATE latin1_spanish_ci DEFAULT 'user' COMMENT 'Nombre de usuario de FTP preview',
  `preview_ftp_password` VARCHAR(32) COLLATE latin1_spanish_ci DEFAULT 'password' COMMENT 'Password del usuario FTP preview',
  `preview_ftp_path` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT '/mydomain.com/WEBSITES/preview.mydomain.com' COMMENT 'Path del FTP preview',
  PRIMARY KEY (`id`)
)ENGINE=InnoDB
ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Tabla con datos de configuracion';

#
# Structure for the `config_i18n` table : 
#

CREATE TABLE `config_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador.',
  `config` INTEGER(4) NOT NULL COMMENT 'Identificador de config.',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma del contenido.',
  `sitename` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del sitio.',
  `offline_message` TEXT COLLATE latin1_spanish_ci COMMENT 'Mensaje que aparecera en la pagina cuando esta no este online.',
  `css` TEXT COLLATE latin1_spanish_ci COMMENT 'Codigo CSS para definir o redefinir nuevos estilos.',
  `javascript` TEXT COLLATE latin1_spanish_ci COMMENT 'Codigo JavaScript para definir en las paginas.',
  `description` TEXT COLLATE latin1_spanish_ci COMMENT 'Meta tag de descripcion del sitio.',
  `keywords` TEXT COLLATE latin1_spanish_ci COMMENT 'Meta tag de palabras claves del sitio.',
  PRIMARY KEY (`id`),
  KEY `config` (`config`),
  KEY `language` (`language`),
  CONSTRAINT `config_i18n_fk` FOREIGN KEY (`config`) REFERENCES `config` (`id`),
  CONSTRAINT `config_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=3 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `db_version` table : 
#

CREATE TABLE `db_version` (
  `version_number` VARCHAR(10) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Numero de Version de la Base de Datos',
  PRIMARY KEY (`version_number`)
)ENGINE=InnoDB
CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Version de la Base de Datos';

#
# Structure for the `diary` table : 
#

CREATE TABLE `diary` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador.',
  `is_categories` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Enlaces a categorias',
  `is_past_events` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Enlaces a categorias',
  `section` INTEGER(4) DEFAULT NULL COMMENT 'Seccion',
  `elementSection` INTEGER(4) DEFAULT NULL COMMENT 'Seccion para los Elementos',
  PRIMARY KEY (`id`),
  KEY `section` (`section`),
  KEY `elementSection` (`elementSection`),
  CONSTRAINT `FK_SECTION_ELEMENT_SECTION` FOREIGN KEY (`elementSection`) REFERENCES `section` (`id`),
  CONSTRAINT `FK_SECTION_SECTION` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=3 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `diary_i18n` table : 
#

CREATE TABLE `diary_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador.',
  `diary` INTEGER(4) NOT NULL COMMENT 'Identificador de diary.',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma del contenido.',
  `content` TEXT COLLATE latin1_spanish_ci COMMENT 'Texto a mostrar.',
  PRIMARY KEY (`id`),
  KEY `diary` (`diary`),
  KEY `language` (`language`),
  CONSTRAINT `diary_i18n_fk` FOREIGN KEY (`diary`) REFERENCES `diary` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `diary_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=3 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `direct_access_group` table : 
#

CREATE TABLE `direct_access_group` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de grupo acceso directo',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificativo alfanumerico para grupo diferenciar un acceso directo de otro.',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de activa',
  `section` INTEGER(4) DEFAULT NULL COMMENT 'seccion',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `section` (`section`),
  CONSTRAINT `direct_access_group_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=32 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Grupo accesos de sitio web.';

#
# Structure for the `direct_access` table : 
#

CREATE TABLE `direct_access` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Codigo de acceso directo directo.',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificador alfanumerico para diferenciar una acceso directo de otra.',
  `direct_access_group` INTEGER(4) NOT NULL COMMENT 'Codigo del menu al que pertenece la acceso directo.',
  `type` TINYINT(2) DEFAULT NULL COMMENT 'Tipo de pagina (Generica, Articulo, Album, etc)',
  `level` TINYINT(2) DEFAULT NULL COMMENT 'Nivel del contenido (Listado categorias, categoria, elemento, etc)',
  `ident` INTEGER(4) DEFAULT NULL COMMENT 'Identificador del elemento seleccionado a mostrar en esta opcion de menu.',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de acceso directo activa.',
  `position` TINYINT(2) NOT NULL COMMENT 'Posicion de la acceso directo dentro del menu.',
  `image` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'imagen',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `direct_access_group` (`direct_access_group`),
  CONSTRAINT `direct_access_fk` FOREIGN KEY (`direct_access_group`) REFERENCES `direct_access_group` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=73 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `direct_access_group_i18n` table : 
#

CREATE TABLE `direct_access_group_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del grupo acceso directo internacionalizada.',
  `direct_access_group` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la grupo acceso directo',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` VARCHAR(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Descripcion del grupo acceso directo de enlace.',
  PRIMARY KEY (`id`),
  KEY `direct_access_group` (`direct_access_group`),
  KEY `language` (`language`),
  CONSTRAINT `direct_access_group_i18n_fk` FOREIGN KEY (`direct_access_group`) REFERENCES `direct_access_group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `direct_access_group_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=58 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `direct_access_i18n` table : 
#

CREATE TABLE `direct_access_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de acceso directo internacionalizada.',
  `direct_access` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la acceso directo',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma de la acceso directo',
  `label` VARCHAR(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Descripcion de la acceso directo.',
  `url` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion de internet en caso de que se trate de una accion externa (http://)',
  `description` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',
  PRIMARY KEY (`id`),
  KEY `direct_access` (`direct_access`),
  KEY `language` (`language`),
  CONSTRAINT `direct_access_i18n_fk` FOREIGN KEY (`direct_access`) REFERENCES `direct_access` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `direct_access_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=138 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `download_category` table : 
#

CREATE TABLE `download_category` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador Unico',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Alias',
  `active` TINYINT(1) NOT NULL COMMENT 'Activo',
  `position` TINYINT(2) NOT NULL COMMENT 'Posicion',
  `image` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Imagen asociada a la categoria de descargas',
  `section` INTEGER(4) DEFAULT NULL COMMENT 'seccion',
  `items_per_page` TINYINT(2) DEFAULT '20' COMMENT 'Elementos por pagina',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `section` (`section`),
  CONSTRAINT `download_category_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=10 CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Categorias de descargas;';

#
# Structure for the `download` table : 
#

CREATE TABLE `download` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador Unico',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Alias',
  `active` TINYINT(1) NOT NULL COMMENT 'Activo',
  `position` TINYINT(2) NOT NULL COMMENT 'Posicion',
  `download_category` INTEGER(4) NOT NULL COMMENT 'Categoria a la que pertenece la descarga',
  `publish_date` DATE DEFAULT NULL COMMENT 'Fecha de publicacion',
  `type` VARCHAR(3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tipo de la descarga',
  `size` INTEGER(6) DEFAULT NULL COMMENT 'Tamaño del fichero',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `download_category` (`download_category`),
  CONSTRAINT `download_fk` FOREIGN KEY (`download_category`) REFERENCES `download_category` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=41 CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Descargas';

#
# Structure for the `download_category_i18n` table : 
#

CREATE TABLE `download_category_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador Unico',
  `download_category` INTEGER(4) NOT NULL COMMENT 'Categoria a la que pertenece',
  `language` INTEGER(4) NOT NULL COMMENT 'Idioma',
  `label` VARCHAR(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Label',
  PRIMARY KEY (`id`),
  KEY `download_category` (`download_category`),
  KEY `language` (`language`),
  CONSTRAINT `download_category_i18n_fk` FOREIGN KEY (`download_category`) REFERENCES `download_category` (`id`),
  CONSTRAINT `download_category_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=16 CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Internacionalizacion de las categorias de descarga;';

#
# Structure for the `download_config` table : 
#

CREATE TABLE `download_config` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del config',
  `section` INTEGER(4) DEFAULT NULL COMMENT 'Seccion',
  PRIMARY KEY (`id`),
  KEY `section` (`section`),
  CONSTRAINT `download_config_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=2 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `download_i18n` table : 
#

CREATE TABLE `download_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador Unico',
  `download` INTEGER(4) NOT NULL COMMENT 'La descarga',
  `language` INTEGER(4) NOT NULL COMMENT 'Idioma',
  `title` VARCHAR(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Titulo de la descarga',
  `description` TEXT COLLATE latin1_spanish_ci COMMENT 'Descripcion de la descarga',
  `file` VARCHAR(255) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Fichero',
  PRIMARY KEY (`id`),
  KEY `download` (`download`),
  KEY `language` (`language`),
  CONSTRAINT `download_i18n_fk` FOREIGN KEY (`download`) REFERENCES `download` (`id`),
  CONSTRAINT `download_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
DELAY_KEY_WRITE=1 AUTO_INCREMENT=52 CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Internacionalizacion de las descargas;';

#
# Structure for the `faq_category` table : 
#

CREATE TABLE `faq_category` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la categoria de faq',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificativo alfanumerico para diferenciar una categoria de otra.',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de opcion activa',
  `position` TINYINT(2) NOT NULL COMMENT 'Posicion dentro de la lista',
  `section` INTEGER(4) DEFAULT NULL COMMENT 'Seccion',
  `items_per_page` TINYINT(2) DEFAULT '20' COMMENT 'Items que se mostraran por pagina',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `section` (`section`),
  CONSTRAINT `faq_category_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Categorias de faq.';

#
# Structure for the `faq` table : 
#

CREATE TABLE `faq` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del faq',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificativo alfanumerico para diferenciar un faq de otro.',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de opcion activa',
  `position` TINYINT(2) NOT NULL COMMENT 'Posicion dentro de la lista',
  `faq_category` INTEGER(4) NOT NULL COMMENT 'Categoria de faq',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `faq_category` (`faq_category`),
  CONSTRAINT `faq_fk` FOREIGN KEY (`faq_category`) REFERENCES `faq_category` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='faq.';

#
# Structure for the `faq_category_i18n` table : 
#

CREATE TABLE `faq_category_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la categoria de faq internacionalizada.',
  `faq_category` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria de faq',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` VARCHAR(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Descripcion de la categoria de faq.',
  PRIMARY KEY (`id`),
  KEY `faq_category` (`faq_category`),
  KEY `language` (`language`),
  CONSTRAINT `faq_category_i18n_fk` FOREIGN KEY (`faq_category`) REFERENCES `faq_category` (`id`),
  CONSTRAINT `faq_category_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `faq_config` table : 
#

CREATE TABLE `faq_config` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del config del faq',
  `section` INTEGER(4) DEFAULT NULL COMMENT 'Seccion',
  PRIMARY KEY (`id`),
  KEY `section` (`section`),
  CONSTRAINT `faq_config_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `faq_i18n` table : 
#

CREATE TABLE `faq_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del texto del faq',
  `faq` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria del faq',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma del enlace',
  `question` VARCHAR(255) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Pregunta del faq.',
  `answer` TEXT COLLATE latin1_spanish_ci NOT NULL COMMENT 'Respuesta del faq.',
  PRIMARY KEY (`id`),
  KEY `faq` (`faq`),
  KEY `language` (`language`),
  CONSTRAINT `faq_i18n_fk` FOREIGN KEY (`faq`) REFERENCES `faq` (`id`),
  CONSTRAINT `faq_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `footer_banner_category` table : 
#

CREATE TABLE `footer_banner_category` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador.',
  `footer` INTEGER(4) NOT NULL COMMENT 'Identificador de Pie de pagina.',
  `banner_category` INTEGER(4) NOT NULL COMMENT 'Identificador de categoria banner.',
  PRIMARY KEY (`id`),
  KEY `footer` (`footer`),
  KEY `banner_category` (`banner_category`),
  CONSTRAINT `footer_banner_category_fk` FOREIGN KEY (`footer`) REFERENCES `footer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `footer_banner_category_fk1` FOREIGN KEY (`banner_category`) REFERENCES `banner_category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `footer_i18n` table : 
#

CREATE TABLE `footer_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador.',
  `footer` INTEGER(4) NOT NULL COMMENT 'Identificador de Pie de pagina.',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma del contenido.',
  `content` TEXT COLLATE latin1_spanish_ci COMMENT 'Texto a mostrar en el pie de pagina.',
  PRIMARY KEY (`id`),
  KEY `footer` (`footer`),
  KEY `language` (`language`),
  CONSTRAINT `footer_i18n_fk` FOREIGN KEY (`footer`) REFERENCES `footer` (`id`),
  CONSTRAINT `footer_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=3 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `generic_page` table : 
#

CREATE TABLE `generic_page` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Codigo de la pagina generica.',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificativo alfanumerico para diferenciar una pagina de otra.',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de contenido activo.',
  `create_date` DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Fecha de creacion del contenido.',
  `menu` INTEGER(4) DEFAULT '0' COMMENT 'Menu asignado a la pagina',
  `section` INTEGER(4) DEFAULT NULL COMMENT 'section',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `menu` (`menu`),
  KEY `section` (`section`),
  CONSTRAINT `generic_page_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=48 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Contenidos genericos;';

#
# Structure for the `generic_page_i18n` table : 
#

CREATE TABLE `generic_page_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de lineas de paginas genericas.',
  `generic_page` INTEGER(4) NOT NULL COMMENT 'Identificador de relacion con la pagina generica.',
  `language` INTEGER(2) NOT NULL COMMENT 'Identificador del idioma correspondiente al contenido.',
  `title` VARCHAR(255) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Titulo del contenido.',
  `content` TEXT COLLATE latin1_spanish_ci COMMENT 'Contenido de la pagina.',
  `description` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Meta Tag description para la cabecera de las paginas. Sobrescribe el definido en la cabecera por defecto.',
  `keywords` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Meta tag keywords para la cabecera de la pagina. Sobreescribe el definido en la cabecera por defecto.',
  PRIMARY KEY (`id`),
  KEY `generic_page` (`generic_page`),
  KEY `language` (`language`),
  CONSTRAINT `generic_page_i18n_fk` FOREIGN KEY (`generic_page`) REFERENCES `generic_page` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `generic_page_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=93 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Contenido generico en varios idiomas.';

#
# Structure for the `header_i18n` table : 
#

CREATE TABLE `header_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la tabla.',
  `header` INTEGER(4) NOT NULL COMMENT 'Identificador de cabecera a la que pertenece.',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma del contenido.',
  `sitename` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre del sitio. Sobreescribe el contenido de la configuracion generica.',
  `image` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Imagen asignada a la cabecera.',
  `content` TEXT COLLATE latin1_spanish_ci COMMENT 'Texto de la cabecera.',
  `alt` VARCHAR(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'alt',
  PRIMARY KEY (`id`),
  KEY `header` (`header`),
  KEY `language` (`language`),
  CONSTRAINT `header_i18n_fk` FOREIGN KEY (`header`) REFERENCES `header` (`id`),
  CONSTRAINT `header_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=46 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `hiru_config` table : 
#

CREATE TABLE `hiru_config` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del config de hiru',
  `section` INTEGER(4) DEFAULT NULL COMMENT 'Seccion',
  PRIMARY KEY (`id`),
  KEY `section` (`section`),
  CONSTRAINT `hiru_config_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=2 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `hiru_organizer_centre` table : 
#

CREATE TABLE `hiru_organizer_centre` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador',
  `name` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '',
  `telephone` VARCHAR(20) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'telefono',
  `fax` VARCHAR(20) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'fax',
  `email` VARCHAR(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'email',
  `postal_code` INTEGER(5) DEFAULT NULL COMMENT 'codigo postal',
  `web` VARCHAR(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'web',
  `address` VARCHAR(128) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'direccion',
  `locality` VARCHAR(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'localidad',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'activo',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
)ENGINE=InnoDB
AUTO_INCREMENT=2 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `hiru_course` table : 
#

CREATE TABLE `hiru_course` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador',
  `init_date` DATE DEFAULT NULL COMMENT 'Fecha inicio',
  `end_date` DATE DEFAULT NULL COMMENT 'Fecha fin',
  `hiru_organizer_centre` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el curso',
  `hiru_place` VARCHAR(128) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Codigo que relaciona el lugar',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Si esta activo',
  `subject` TINYINT(4) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `hiru_organizer_centre` (`hiru_organizer_centre`),
  CONSTRAINT `hiru_course_fk` FOREIGN KEY (`hiru_organizer_centre`) REFERENCES `hiru_organizer_centre` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=2 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `hiru_course_i18n` table : 
#

CREATE TABLE `hiru_course_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador',
  `hiru_course` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con hiru_course',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma',
  `name` VARCHAR(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Nombre.',
  `info` TEXT COLLATE latin1_spanish_ci NOT NULL COMMENT 'Info.',
  `url` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'url',
  `generic_info` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL,
  `objetives` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL,
  `contents` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL,
  `length` VARCHAR(32) COLLATE latin1_spanish_ci DEFAULT NULL,
  `employee_registration` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL,
  `not_employee_registration` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL,
  `giver_entity` VARCHAR(64) COLLATE latin1_spanish_ci DEFAULT NULL,
  `number_participant` VARCHAR(4) COLLATE latin1_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `hiru_course` (`hiru_course`),
  KEY `language` (`language`),
  CONSTRAINT `hiru_course_i18n_fk` FOREIGN KEY (`hiru_course`) REFERENCES `hiru_course` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `hiru_course_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=3 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `link_category` table : 
#

CREATE TABLE `link_category` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la categoria del enlace',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificativo alfanumerico para diferenciar una categoria de otra.',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de opcion activa',
  `position` TINYINT(2) NOT NULL COMMENT 'Posicion dentro de la lista',
  `section` INTEGER(4) DEFAULT NULL COMMENT 'seccion',
  `items_per_page` TINYINT(2) DEFAULT '20' COMMENT 'Items que se mostraran por pagina',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `section` (`section`),
  CONSTRAINT `link_category_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=15 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Categorias de enlaces.';

#
# Structure for the `link` table : 
#

CREATE TABLE `link` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del enlace',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificativo alfanumerico para diferenciar unenlace de otro.',
  `url` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion de internet del enlace.',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de opcion activa',
  `position` TINYINT(2) NOT NULL COMMENT 'Posicion dentro de la lista',
  `link_category` INTEGER(4) NOT NULL COMMENT 'Categoria de enlaces',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `link_category` (`link_category`),
  CONSTRAINT `link_fk` FOREIGN KEY (`link_category`) REFERENCES `link_category` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=90 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Enlaces.';

#
# Structure for the `link_category_i18n` table : 
#

CREATE TABLE `link_category_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la categoria de enlace internacionalizada.',
  `link_category` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria del enlace',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` VARCHAR(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Descripcion de la categoria de enlace.',
  PRIMARY KEY (`id`),
  KEY `link_category` (`link_category`),
  KEY `language` (`language`),
  CONSTRAINT `link_category_i18n_fk` FOREIGN KEY (`link_category`) REFERENCES `link_category` (`id`),
  CONSTRAINT `link_category_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=29 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `link_config` table : 
#

CREATE TABLE `link_config` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del config del link',
  `section` INTEGER(4) DEFAULT NULL COMMENT 'Seccion',
  PRIMARY KEY (`id`),
  KEY `section` (`section`),
  CONSTRAINT `link_config_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=2 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `link_i18n` table : 
#

CREATE TABLE `link_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del texto del enlace',
  `link` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria del enlace',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma del enlace',
  `label` VARCHAR(255) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Descripcion del enlace.',
  `description` TEXT COLLATE latin1_spanish_ci COMMENT 'Pequeña descripcion del link',
  PRIMARY KEY (`id`),
  KEY `link` (`link`),
  KEY `language` (`language`),
  CONSTRAINT `link_i18n_fk` FOREIGN KEY (`link`) REFERENCES `link` (`id`),
  CONSTRAINT `link_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=166 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `menu_option` table : 
#

CREATE TABLE `menu_option` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Codigo de opcion de menu.',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificador alfanumerico para diferenciar una opcion de otra.',
  `menu` INTEGER(4) NOT NULL COMMENT 'Codigo del menu al que pertenece la opcion.',
  `sep` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Tipo de opcion, separador o pagina.',
  `type` TINYINT(2) NOT NULL COMMENT 'Tipo de pagina (Generica, Articulo, Album, etc)',
  `level` TINYINT(2) DEFAULT NULL COMMENT 'Nivel del contenido (Listado categorias, categoria, elemento, etc)',
  `ident` INTEGER(4) DEFAULT NULL COMMENT 'Identificador del elemento seleccionado a mostrar en esta opcion de menu.',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de opcion activa.',
  `position` TINYINT(2) NOT NULL COMMENT 'Posicion de la opcion dentro del menu.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `menu` (`menu`),
  CONSTRAINT `menu_option_fk` FOREIGN KEY (`menu`) REFERENCES `menu` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB
AUTO_INCREMENT=1065 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `menu_option_i18n` table : 
#

CREATE TABLE `menu_option_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de opcion de menu internacionalizada.',
  `menu_option` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la opcion de menu',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` VARCHAR(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Descripcion de la opcion de menu.',
  `url` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Direccion de internet en caso de que se trate de una accion externa (http://)',
  PRIMARY KEY (`id`),
  KEY `menu_option` (`menu_option`),
  KEY `language` (`language`),
  CONSTRAINT `menu_option_i18n_fk` FOREIGN KEY (`menu_option`) REFERENCES `menu_option` (`id`),
  CONSTRAINT `menu_option_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=2002 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `modular_page` table : 
#

CREATE TABLE `modular_page` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de pagina modular',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificativo alfanumerico para diferenciar una pagina de otro.',
  `type` TINYINT(2) DEFAULT NULL COMMENT 'Tipo de pagina',
  `homepage` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de homepage.',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de opcion activa.',
  `section` INTEGER(4) DEFAULT NULL COMMENT 'Seccion',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `section` (`section`),
  CONSTRAINT `modular_page_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=59 AVG_ROW_LENGTH=1092 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Menus de sitio web.';

#
# Structure for the `modular_page_i18n` table : 
#

CREATE TABLE `modular_page_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de modular_page internacionalizada.',
  `modular_page` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la modular_page',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` VARCHAR(128) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Descripcion de la modular_page.',
  PRIMARY KEY (`id`),
  KEY `modular_page` (`modular_page`),
  KEY `language` (`language`),
  CONSTRAINT `modular_page_i18n_fk` FOREIGN KEY (`modular_page`) REFERENCES `modular_page` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `modular_page_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=97 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `modular_page_option` table : 
#

CREATE TABLE `modular_page_option` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Codigo de opcion de pag modular.',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificador alfanumerico para diferenciar una opcion de otra.',
  `modular_page` INTEGER(4) NOT NULL COMMENT 'Codigo de la pag modular al que pertenece la opcion.',
  `type` TINYINT(2) DEFAULT NULL COMMENT 'Tipo de pagina',
  `ident` INTEGER(4) DEFAULT NULL COMMENT 'Identificador del elemento seleccionado a mostrar en esta opcion de pag modular.',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de opcion activa.',
  `position` TINYINT(2) NOT NULL COMMENT 'Posicion de la opcion dentro de la pag modular.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `modular_page` (`modular_page`),
  CONSTRAINT `modular_page_option_fk` FOREIGN KEY (`modular_page`) REFERENCES `modular_page` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB
AUTO_INCREMENT=248 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `modular_page_option_i18n` table : 
#

CREATE TABLE `modular_page_option_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de opcion de modular_page internacionalizada.',
  `modular_page_option` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la opcion de modular_page',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` VARCHAR(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Descripcion de la opcion de modular_page.',
  PRIMARY KEY (`id`),
  KEY `modular_page_option` (`modular_page_option`),
  KEY `language` (`language`),
  CONSTRAINT `modular_page_option_i18n_fk` FOREIGN KEY (`modular_page_option`) REFERENCES `modular_page_option` (`id`),
  CONSTRAINT `modular_page_option_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=386 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `product_category` table : 
#

CREATE TABLE `product_category` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la categoria de producto',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificativo alfanumerico para diferenciar una categoria de producto de otra.',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de opcion activa',
  `parent` INTEGER(4) DEFAULT NULL COMMENT 'Codigo que relaciona la categoria de producto con la categoria de producto padre',
  `section` INTEGER(4) DEFAULT NULL COMMENT 'section',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `parent` (`parent`),
  KEY `section` (`section`),
  CONSTRAINT `product_category_fk` FOREIGN KEY (`parent`) REFERENCES `product_category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `product_category_fk1` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='categoria de producto.';

#
# Structure for the `product` table : 
#

CREATE TABLE `product` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador el producto',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificativo alfanumerico para diferenciar un producto de otro.',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de opcion activa',
  `price` DOUBLE(15,2) DEFAULT '0.00' COMMENT 'Precio del Articulo',
  `offer_price` DOUBLE(15,2) DEFAULT '0.00' COMMENT 'Precio del Articulo en oferta',
  `brand` INTEGER(4) DEFAULT NULL COMMENT 'codigo de marca',
  `product_category` INTEGER(4) DEFAULT NULL COMMENT 'codigo de categoria',
  `image` VARCHAR(255) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Imagen',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `brand` (`brand`),
  KEY `product_category` (`product_category`),
  CONSTRAINT `product_fk` FOREIGN KEY (`brand`) REFERENCES `brand` (`id`),
  CONSTRAINT `product_fk1` FOREIGN KEY (`product_category`) REFERENCES `product_category` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='categoria de producto.';

#
# Structure for the `product_category_config` table : 
#

CREATE TABLE `product_category_config` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del config de categoria de productos',
  `section` INTEGER(4) DEFAULT NULL COMMENT 'Seccion',
  PRIMARY KEY (`id`),
  KEY `section` (`section`),
  CONSTRAINT `product_category_config_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `product_category_i18n` table : 
#

CREATE TABLE `product_category_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la categoria de producto internacionalizada.',
  `product_category` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria de producto',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` VARCHAR(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Descripcion de la categoria de producto.',
  PRIMARY KEY (`id`),
  KEY `product_category` (`product_category`),
  KEY `language` (`language`),
  CONSTRAINT `product_category_i18n_fk` FOREIGN KEY (`product_category`) REFERENCES `product_category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `product_category_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `product_i18n` table : 
#

CREATE TABLE `product_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de el producto internacionalizada.',
  `product` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con el producto',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma de la opcion',
  `short_label` VARCHAR(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Descripcion corta del producto.',
  `label` TEXT COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del producto.',
  `alt` VARCHAR(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'alt',
  PRIMARY KEY (`id`),
  KEY `product` (`product`),
  KEY `language` (`language`),
  CONSTRAINT `product_i18n_fk` FOREIGN KEY (`product`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `product_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `sidebar_option` table : 
#

CREATE TABLE `sidebar_option` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Codigo de opcion de menu.',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificador alfanumerico para diferenciar una opcion de otra.',
  `sidebar` INTEGER(4) NOT NULL COMMENT 'Codigo del menu al que pertenece la opcion.',
  `type` TINYINT(2) DEFAULT NULL COMMENT 'Tipo de pagina (Generica, Articulo, Album, etc)',
  `level` TINYINT(2) DEFAULT NULL COMMENT 'Nivel del contenido (Listado categorias, categoria, elemento, etc)',
  `ident` INTEGER(4) DEFAULT NULL COMMENT 'Identificador del elemento seleccionado a mostrar en esta opcion de menu.',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de opcion activa.',
  `position` TINYINT(2) NOT NULL COMMENT 'Posicion de la opcion dentro del menu.',
  `side` TINYINT(2) NOT NULL DEFAULT '0' COMMENT 'Lado',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `sidebar` (`sidebar`),
  CONSTRAINT `sidebar_option_fk` FOREIGN KEY (`sidebar`) REFERENCES `sidebar` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=270 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `sidebar_option_i18n` table : 
#

CREATE TABLE `sidebar_option_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de opcion de menu internacionalizada.',
  `sidebar_option` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la opcion de menu',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` VARCHAR(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Descripcion de la opcion de menu.',
  PRIMARY KEY (`id`),
  KEY `sidebar_option` (`sidebar_option`),
  KEY `language` (`language`),
  CONSTRAINT `sidebar_option_i18n_fk` FOREIGN KEY (`sidebar_option`) REFERENCES `sidebar_option` (`id`),
  CONSTRAINT `sidebar_option_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=511 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `sport_category` table : 
#

CREATE TABLE `sport_category` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la categoria de deportes',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificativo alfanumerico para diferenciar una categoria de otra.',
  `default_` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de opcion activa',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Categorias de deportes.';

#
# Structure for the `sport_club` table : 
#

CREATE TABLE `sport_club` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del club.',
  `sport_category` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona la categoria',
  `description` VARCHAR(28) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion.',
  `stadium` VARCHAR(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Estadio.',
  `image` VARCHAR(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Foto.',
  `logo` VARCHAR(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Logo.',
  `default_` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de opcion activa',
  PRIMARY KEY (`id`),
  KEY `sport_category` (`sport_category`),
  CONSTRAINT `sport_club_fk` FOREIGN KEY (`sport_category`) REFERENCES `sport_category` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `sport_nationality` table : 
#

CREATE TABLE `sport_nationality` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la nacionalidad',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificativo alfanumerico para diferenciar una posicion de otra.',
  `image` VARCHAR(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Foto.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Nacionalidad.';

#
# Structure for the `sport_position` table : 
#

CREATE TABLE `sport_position` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la posicion',
  `alias` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Identificativo alfanumerico para diferenciar una posicion de otra.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias` (`alias`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Posiciones.';

#
# Structure for the `sport_player` table : 
#

CREATE TABLE `sport_player` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del jugador.',
  `name` VARCHAR(128) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Nombre',
  `sport_position` INTEGER(4) DEFAULT NULL COMMENT 'Posicion',
  `born_date` DATE DEFAULT NULL COMMENT 'Fecha nacimiento',
  `born_place` VARCHAR(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Lugar nacimiento',
  `weight` DOUBLE(5,2) DEFAULT NULL COMMENT 'Peso',
  `lenght` DOUBLE(5,2) DEFAULT NULL COMMENT 'Altura',
  `sport_nationality` INTEGER(4) DEFAULT NULL COMMENT 'Nacionalidad',
  `comunitary` TINYINT(1) DEFAULT '1' COMMENT 'Comunitario',
  `photo` VARCHAR(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'foto',
  `sport_club` INTEGER(4) DEFAULT NULL COMMENT 'Equipo',
  `number` INTEGER(4) DEFAULT NULL COMMENT 'Numero',
  `active` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Activo',
  PRIMARY KEY (`id`),
  KEY `sport_position` (`sport_position`),
  KEY `sport_club` (`sport_club`),
  KEY `sport_nationality` (`sport_nationality`),
  CONSTRAINT `sport_player_fk` FOREIGN KEY (`sport_position`) REFERENCES `sport_position` (`id`),
  CONSTRAINT `sport_player_fk1` FOREIGN KEY (`sport_club`) REFERENCES `sport_club` (`id`),
  CONSTRAINT `sport_player_fk3` FOREIGN KEY (`sport_nationality`) REFERENCES `sport_nationality` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `sport_career_path` table : 
#

CREATE TABLE `sport_career_path` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de plantilla.',
  `sport_player` INTEGER(4) NOT NULL COMMENT 'Equipo',
  `club` VARCHAR(64) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'equipo',
  `init_date` DATE DEFAULT NULL COMMENT 'Fecha inicio',
  `end_date` DATE DEFAULT NULL COMMENT 'Fecha fin',
  PRIMARY KEY (`id`),
  KEY `sport_player` (`sport_player`),
  KEY `sport_club` (`club`),
  CONSTRAINT `sport_career_path_fk` FOREIGN KEY (`sport_player`) REFERENCES `sport_player` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `sport_category_i18n` table : 
#

CREATE TABLE `sport_category_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la categoria de articulo internacionalizada.',
  `sport_category` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria de articulo',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma de la opcion',
  `description` VARCHAR(128) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Descripcion de la categoria de articulo.',
  PRIMARY KEY (`id`),
  KEY `sport_category` (`sport_category`),
  KEY `language` (`language`),
  CONSTRAINT `sport_category_i18n_fk` FOREIGN KEY (`sport_category`) REFERENCES `sport_category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `sport_category_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `sport_coach` table : 
#

CREATE TABLE `sport_coach` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de plantilla.',
  `sport_club` INTEGER(4) NOT NULL COMMENT 'Temporada',
  `job` VARCHAR(128) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Cargo',
  `name` VARCHAR(128) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Nombre',
  PRIMARY KEY (`id`),
  KEY `sport_club` (`sport_club`),
  CONSTRAINT `sport_coach_fk` FOREIGN KEY (`sport_club`) REFERENCES `sport_club` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `sport_config` table : 
#

CREATE TABLE `sport_config` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del config de sport',
  `section` INTEGER(4) DEFAULT NULL COMMENT 'Seccion',
  PRIMARY KEY (`id`),
  KEY `section` (`section`),
  CONSTRAINT `sport_config_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `sport_nationality_i18n` table : 
#

CREATE TABLE `sport_nationality_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la nacionalidad.',
  `sport_nationality` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la nacionalidad',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma de la opcion',
  `description` VARCHAR(28) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Descripcion de la posicion.',
  PRIMARY KEY (`id`),
  KEY `sport_nationality` (`sport_nationality`),
  KEY `language` (`language`),
  CONSTRAINT `sport_nationality_i18n_fk` FOREIGN KEY (`sport_nationality`) REFERENCES `sport_nationality` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `sport_nationality_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `sport_position_i18n` table : 
#

CREATE TABLE `sport_position_i18n` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la posicion internacionalizada.',
  `sport_position` INTEGER(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la posicion',
  `language` INTEGER(2) NOT NULL COMMENT 'Idioma de la opcion',
  `description` VARCHAR(28) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Descripcion de la posicion.',
  PRIMARY KEY (`id`),
  KEY `sport_position` (`sport_position`),
  KEY `language` (`language`),
  CONSTRAINT `sport_position_i18n_fk` FOREIGN KEY (`sport_position`) REFERENCES `sport_position` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `sport_position_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci';

#
# Structure for the `sport_season` table : 
#

CREATE TABLE `sport_season` (
  `id` INTEGER(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la temporada de deportes',
  `description` VARCHAR(32) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Descripcion.',
  `default_` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Indicador de opcion activa',
  PRIMARY KEY (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci'
COMMENT='Temporada de deportes.';

#
# Data for the `db_version` table  (LIMIT 0,500)
#

INSERT INTO `db_version` (`version_number`) VALUES 
  ('2.0.5');

COMMIT;

SET FOREIGN_KEY_CHECKS=1;