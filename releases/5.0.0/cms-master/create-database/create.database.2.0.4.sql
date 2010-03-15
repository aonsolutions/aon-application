# SQL Manager 2007 for MySQL 4.2.1.1
# ---------------------------------------
# Host     : 192.168.2.4
# Port     : 3306
# Database : cms


SET FOREIGN_KEY_CHECKS=0;

CREATE DATABASE `cms-master`
    CHARACTER SET 'latin1'
    COLLATE 'latin1_swedish_ci';

USE `cms-master`;

#
# Structure for the `activity` table : 
#

CREATE TABLE `activity` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `language` table : 
#

CREATE TABLE `language` (
  `id` int(2) NOT NULL COMMENT 'Codigo de idioma.',
  `language` tinyint(2) NOT NULL COMMENT 'Codigo de idioma. Enumerado Locale.',
  `description` varchar(24) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion a mostrar del idioma.',
  `position` tinyint(2) NOT NULL COMMENT 'Orden preferencia del idioma.',
  `defaultLanguage` tinyint(1) NOT NULL default '0' COMMENT 'Indicador del idioma por defecto',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='Idiomas de los contenidos.';

#
# Structure for the `activity_i18n` table : 
#

CREATE TABLE `activity_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `activity` int(4) NOT NULL COMMENT 'Actividad',
  `language` int(4) NOT NULL COMMENT 'Lenguaje',
  `description` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `activity` (`activity`),
  KEY `language` (`language`),
  CONSTRAINT `activity_i18n_fk` FOREIGN KEY (`activity`) REFERENCES `activity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `activity_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Iternacionalizacion de los albums';

#
# Structure for the `banner_category` table : 
#

CREATE TABLE `banner_category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de la categoria del banner',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificativo alfanumerico para diferenciar una categoria de otra.',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de opcion activa',
  `position` tinyint(2) NOT NULL COMMENT 'Posicion dentro de la lista',
  `section` int(4) default NULL COMMENT 'seccion',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `section` (`section`),
  CONSTRAINT `banner_category_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='Categorias de enlaces.';

#
# Structure for the `menu` table : 
#

CREATE TABLE `menu` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de menu',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificativo alfanumerico para diferenciar un menu de otro.',
  `type` tinyint(2) NOT NULL COMMENT 'Tipo de menu (top, foot, sidebar,...).',
  `defaultMenu` tinyint(1) NOT NULL default '0' COMMENT 'Indicador de menu por defecto. Cada tipo tendra un menu por defecto.',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='Menus de sitio web.';

#
# Structure for the `header` table : 
#

CREATE TABLE `header` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de Cabecera.',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador alfanumerico de Cabeciera.',
  `menu` int(4) default NULL COMMENT 'Menu recursivo asociado a la cabecera.',
  `language_menu` tinyint(1) NOT NULL default '1' COMMENT 'Mostrar o no el menu de cambio de idiomas en la cabecera.',
  `language_menu_type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de menu de idiomas. 0 = lista, 1 = drop, 2 = banderas.',
  `css` text collate latin1_spanish_ci COMMENT 'Codigo CSS que ira en la cabecera de la pagina.',
  `javascript` text collate latin1_spanish_ci COMMENT 'Codigo JavaScript que ira en al cabecera de la pagina.',
  `default_` tinyint(1) NOT NULL default '0',
  `banner_category` int(4) default NULL COMMENT 'Categoria de banner asociado a la cabecera.',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `menu` (`menu`),
  KEY `banner_category` (`banner_category`),
  CONSTRAINT `header_fk1` FOREIGN KEY (`banner_category`) REFERENCES `banner_category` (`id`),
  CONSTRAINT `header_fk` FOREIGN KEY (`menu`) REFERENCES `menu` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `sidebar` table : 
#

CREATE TABLE `sidebar` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de menu',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificativo alfanumerico para diferenciar un menu de otro.',
  `default_` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='Menus de sitio web.';

#
# Structure for the `footer` table : 
#

CREATE TABLE `footer` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de pie de pagina.',
  `alias` varchar(32) NOT NULL COMMENT 'Identificador alfanumerico de pie de pagina',
  `menu` int(4) default NULL COMMENT 'Menu asignado al pie de pagina.',
  `default_` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `menu` (`menu`),
  CONSTRAINT `footer_fk` FOREIGN KEY (`menu`) REFERENCES `menu` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

#
# Structure for the `section` table : 
#

CREATE TABLE `section` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de la seccion.',
  `header` int(4) default NULL COMMENT 'Cabecera',
  `sidebar` int(4) default NULL COMMENT 'Lateral',
  `footer` int(4) default NULL COMMENT 'Pie',
  `show_header` tinyint(1) NOT NULL default '0' COMMENT 'Mostrar cabecera',
  `show_sidebar_left` tinyint(1) NOT NULL default '0' COMMENT 'Mostrar Lateral',
  `show_footer` tinyint(1) NOT NULL default '0' COMMENT 'Mostrar Pie',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL,
  `menu` int(4) default NULL COMMENT 'Menu',
  `show_menu` tinyint(1) NOT NULL default '0' COMMENT 'Mostrar menu',
  `default_` tinyint(1) NOT NULL default '0',
  `show_sidebar_right` tinyint(1) NOT NULL default '0' COMMENT 'Mostrar Lateral',
  `menu_alt` int(4) default NULL COMMENT 'Menu alternativo',
  `show_menu_alt` tinyint(1) NOT NULL COMMENT 'Mostrar menu alternativo',
  `parent_` int(4) default NULL COMMENT 'Padre',
  `parent_sidebar_right` tinyint(1) NOT NULL default '0',
  `parent_sidebar_left` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`),
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `album_category` table : 
#

CREATE TABLE `album_category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Alias',
  `active` tinyint(1) NOT NULL COMMENT 'Activo',
  `position` tinyint(2) NOT NULL COMMENT 'Posicion',
  `section` int(4) default NULL COMMENT 'Seccion',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `album_category_fk` (`section`),
  CONSTRAINT `album_category_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias de albums';

#
# Structure for the `album` table : 
#

CREATE TABLE `album` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Alias',
  `active` tinyint(1) NOT NULL COMMENT 'Activo',
  `position` tinyint(2) NOT NULL COMMENT 'Posicion',
  `album_category` int(4) NOT NULL COMMENT 'Categoria a la que pertenece el album',
  `publish_date` date default NULL COMMENT 'Fecha de publicacion',
  `image` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Imagen asociada al album',
  `items_per_page` tinyint(2) default '20' COMMENT 'Items que se mostraran por pagina',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `album_category` (`album_category`),
  CONSTRAINT `album_fk` FOREIGN KEY (`album_category`) REFERENCES `album_category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Album';

#
# Structure for the `album_category_i18n` table : 
#

CREATE TABLE `album_category_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `album_category` int(4) NOT NULL COMMENT 'Categoria de album',
  `language` int(4) NOT NULL COMMENT 'Lenguaje',
  `label` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Label',
  PRIMARY KEY  (`id`),
  KEY `album_category` (`album_category`),
  KEY `language` (`language`),
  CONSTRAINT `album_category_i18n_fk` FOREIGN KEY (`album_category`) REFERENCES `album_category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `album_category_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Internacionalizacion de las categorias de albums';

#
# Structure for the `album_config` table : 
#

CREATE TABLE `album_config` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del config del album',
  `section` int(4) default NULL COMMENT 'Seccion',
  PRIMARY KEY  (`id`),
  KEY `section` (`section`),
  CONSTRAINT `album_config_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `album_i18n` table : 
#

CREATE TABLE `album_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `album` int(4) NOT NULL COMMENT 'Album',
  `language` int(4) NOT NULL COMMENT 'Lenguaje',
  `title` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Titulo',
  `description` text collate latin1_spanish_ci COMMENT 'Descripcion',
  `alt` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Alt de la imagen asociada al album',
  PRIMARY KEY  (`id`),
  KEY `album` (`album`),
  KEY `language` (`language`),
  CONSTRAINT `album_i18n_fk` FOREIGN KEY (`album`) REFERENCES `album` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `album_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Iternacionalizacion de los albums';

#
# Structure for the `album_image` table : 
#

CREATE TABLE `album_image` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `album` int(4) NOT NULL COMMENT 'Album al que pertenece la imagen',
  `active` tinyint(1) NOT NULL COMMENT 'Activo',
  `position` tinyint(2) NOT NULL COMMENT 'Posicion',
  `image` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Imagen',
  `thumbnail` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Imagen en miniatura',
  PRIMARY KEY  (`id`),
  KEY `album` (`album`),
  CONSTRAINT `album_image_fk` FOREIGN KEY (`album`) REFERENCES `album` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cada imagen del album';

#
# Structure for the `album_image_i18n` table : 
#

CREATE TABLE `album_image_i18n` (
  `id` int(11) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `album_image` int(4) NOT NULL COMMENT 'Imagen relacionada',
  `language` int(4) NOT NULL COMMENT 'Lenguaje',
  `title` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Titulo',
  `description` text collate latin1_spanish_ci COMMENT 'Descripcion',
  `alt` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Alt asociado a la imagen',
  `alt_thumbnail` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Alt asociado al thumbnail',
  PRIMARY KEY  (`id`),
  KEY `album_image` (`album_image`),
  KEY `language` (`language`),
  CONSTRAINT `album_image_i18n_fk` FOREIGN KEY (`album_image`) REFERENCES `album_image` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `album_image_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Internacionalizacion de las imagenes';

#
# Structure for the `aoncms_bulletin_subscription` table : 
#

CREATE TABLE `aoncms_bulletin_subscription` (
  `id` int(11) NOT NULL auto_increment,
  `email` varchar(255) collate latin1_spanish_ci NOT NULL,
  `language` tinyint(2) NOT NULL,
  `code` varchar(32) collate latin1_spanish_ci NOT NULL,
  `ip` varchar(15) collate latin1_spanish_ci default NULL,
  `subscription_date` datetime NOT NULL default '0000-00-00 00:00:00',
  `valid` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `article_category` table : 
#

CREATE TABLE `article_category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de la categoria de articulo',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificativo alfanumerico para diferenciar una categoria de otra.',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de opcion activa',
  `position` tinyint(2) NOT NULL COMMENT 'Posicion dentro de la lista',
  `section` int(4) default NULL COMMENT 'Seccion',
  `elementSection` int(4) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `section` (`section`),
  KEY `elementSection` (`elementSection`),
  CONSTRAINT `article_category_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`),
  CONSTRAINT `article_category_fk1` FOREIGN KEY (`elementSection`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='Categorias de articulo.';

#
# Structure for the `article` table : 
#

CREATE TABLE `article` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del articulo',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificativo alfanumerico para diferenciar un articulo de otro.',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de opcion activa',
  `position` tinyint(2) NOT NULL COMMENT 'Posicion dentro de la lista',
  `article_category` int(4) NOT NULL COMMENT 'Categoria de articulo',
  `publish_date` date NOT NULL COMMENT 'Fecha publicacion',
  `expire_date` date default NULL COMMENT 'Fecha expiracion',
  `type_` tinyint(4) NOT NULL COMMENT 'Tipo de articulo',
  `image` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Imagen',
  `thumbnail` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Thumbnail',
  `init_date` date default NULL COMMENT 'Fecha articulo',
  `end_date` date default NULL COMMENT 'Fecha fin articulo',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `article_category` (`article_category`),
  CONSTRAINT `article_fk` FOREIGN KEY (`article_category`) REFERENCES `article_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='article.';

#
# Structure for the `article_category_i18n` table : 
#

CREATE TABLE `article_category_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de la categoria de articulo internacionalizada.',
  `article_category` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria de articulo',
  `language` int(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la categoria de articulo.',
  PRIMARY KEY  (`id`),
  KEY `article_category` (`article_category`),
  KEY `language` (`language`),
  CONSTRAINT `article_category_i18n_fk` FOREIGN KEY (`article_category`) REFERENCES `article_category` (`id`),
  CONSTRAINT `article_category_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `article_config` table : 
#

CREATE TABLE `article_config` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del config del articulo',
  `section` int(4) default NULL COMMENT 'Seccion',
  PRIMARY KEY  (`id`),
  KEY `section` (`section`),
  CONSTRAINT `article_config_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `article_document` table : 
#

CREATE TABLE `article_document` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `article` int(4) NOT NULL COMMENT 'Articulo al que pertenece el documento',
  `alias` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Alias',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `article` (`article`),
  CONSTRAINT `article_document_fk` FOREIGN KEY (`article`) REFERENCES `article` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cada fichero del articulo';

#
# Structure for the `article_document_i18n` table : 
#

CREATE TABLE `article_document_i18n` (
  `id` int(11) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `article_document` int(4) NOT NULL COMMENT 'Fichero relacionada',
  `language` int(4) NOT NULL COMMENT 'Lenguaje',
  `file` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Fichero',
  `title` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Titulo',
  `description` text collate latin1_spanish_ci COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `article_document` (`article_document`),
  KEY `language` (`language`),
  CONSTRAINT `article_document_i18n_fk` FOREIGN KEY (`article_document`) REFERENCES `article_document` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `article_document_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Internacionalizacion de las ficheros';

#
# Structure for the `article_i18n` table : 
#

CREATE TABLE `article_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del texto del articulo',
  `article` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria del articulo',
  `language` int(2) NOT NULL COMMENT 'Idioma del enlace',
  `title` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Titulo del articulo.',
  `subtitle` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Subtitulo del articulo.',
  `content` text collate latin1_spanish_ci NOT NULL COMMENT 'Contenido del articulo.',
  `alt` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Alt de la imagen',
  `alt_thumbnail` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Alt del thumbnail',
  `image_info` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Pie',
  PRIMARY KEY  (`id`),
  KEY `article` (`article`),
  KEY `language` (`language`),
  CONSTRAINT `article_i18n_fk` FOREIGN KEY (`article`) REFERENCES `article` (`id`),
  CONSTRAINT `article_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `article_related` table : 
#

CREATE TABLE `article_related` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador',
  `article_parent` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con el articulo',
  `article_related` int(4) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `article` (`article_parent`),
  KEY `article_related` (`article_related`),
  CONSTRAINT `article_related_fk` FOREIGN KEY (`article_parent`) REFERENCES `article` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `article_related_fk1` FOREIGN KEY (`article_related`) REFERENCES `article` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `banner` table : 
#

CREATE TABLE `banner` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del banner',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificativo alfanumerico para diferenciar un banner de otro.',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de opcion activa',
  `position` tinyint(2) NOT NULL COMMENT 'Posicion dentro de la lista',
  `banner_category` int(4) NOT NULL COMMENT 'Categoria de banners',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `banner_category` (`banner_category`),
  CONSTRAINT `banner_fk` FOREIGN KEY (`banner_category`) REFERENCES `banner_category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='Enlaces.';

#
# Structure for the `banner_category_i18n` table : 
#

CREATE TABLE `banner_category_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de la categoria de enlace internacionalizada.',
  `banner_category` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria del banner',
  `language` int(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la categoria de banner.',
  PRIMARY KEY  (`id`),
  KEY `banner_category` (`banner_category`),
  KEY `language` (`language`),
  CONSTRAINT `banner_category_i18n_fk` FOREIGN KEY (`banner_category`) REFERENCES `banner_category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `banner_category_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `banner_i18n` table : 
#

CREATE TABLE `banner_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del texto del banner',
  `banner` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria del banner',
  `language` int(2) NOT NULL COMMENT 'Idioma del banner',
  `label` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del banner.',
  `url` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Url del banner',
  `image` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Imagen del banner',
  `description` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del banner',
  PRIMARY KEY  (`id`),
  KEY `banner` (`banner`),
  KEY `language` (`language`),
  CONSTRAINT `banner_i18n_fk` FOREIGN KEY (`banner`) REFERENCES `banner` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `banner_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `brand` table : 
#

CREATE TABLE `brand` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de la marca',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificativo alfanumerico para diferenciar una marca de otra.',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de opcion activa',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='Marcas.';

#
# Structure for the `brand_i18n` table : 
#

CREATE TABLE `brand_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de la marca internacionalizada.',
  `brand` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la marca',
  `language` int(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la marca.',
  PRIMARY KEY  (`id`),
  KEY `brand` (`brand`),
  KEY `language` (`language`),
  CONSTRAINT `brand_i18n_fk` FOREIGN KEY (`brand`) REFERENCES `brand` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `brand_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `bulletin` table : 
#

CREATE TABLE `bulletin` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `alias` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Alias',
  `publish_date` date NOT NULL COMMENT 'Fecha de publicacion',
  `template` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Template',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  UNIQUE KEY `alias_2` (`alias`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bulletin';

#
# Structure for the `bulletin_article` table : 
#

CREATE TABLE `bulletin_article` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del articulo',
  `bulletin` int(4) NOT NULL COMMENT 'boletin',
  `article` int(4) NOT NULL COMMENT 'articulo',
  `position` tinyint(2) NOT NULL COMMENT 'Posicion dentro de la lista',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `bulletin_article` (`bulletin`,`article`),
  KEY `bulletin` (`bulletin`),
  KEY `article` (`article`),
  CONSTRAINT `bulletin_article_fk` FOREIGN KEY (`bulletin`) REFERENCES `bulletin` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `bulletin_article_fk1` FOREIGN KEY (`article`) REFERENCES `article` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `bulletin_emails` table : 
#

CREATE TABLE `bulletin_emails` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `email` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'email',
  `language` int(2) NOT NULL COMMENT 'Idioma',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `email` (`email`,`language`),
  KEY `language` (`language`),
  CONSTRAINT `bulletin_emails_fk` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bulletin emails';

#
# Structure for the `bulletin_i18n` table : 
#

CREATE TABLE `bulletin_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del texto del boletin',
  `bulletin` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria del boletin',
  `language` int(2) NOT NULL COMMENT 'Idioma del enlace',
  `title` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Titulo del boletin.',
  `content` text collate latin1_spanish_ci COMMENT 'Contenido del boletin.',
  PRIMARY KEY  (`id`),
  KEY `bulletin` (`bulletin`),
  KEY `language` (`language`),
  CONSTRAINT `bulletin_i18n_fk` FOREIGN KEY (`bulletin`) REFERENCES `bulletin` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `bulletin_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `company` table : 
#

CREATE TABLE `company` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'nombre',
  `telephone` varchar(20) collate latin1_spanish_ci default NULL COMMENT 'telefono',
  `fax` varchar(20) collate latin1_spanish_ci default NULL COMMENT 'fax',
  `email` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'email',
  `address` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'direccion',
  `locality` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'localidad',
  `province` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'provincia',
  `postal_code` int(5) default NULL COMMENT 'codigo postal',
  `web` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'web',
  `logo` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'logotipo',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `company_activity` table : 
#

CREATE TABLE `company_activity` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador',
  `company` int(4) default NULL COMMENT 'Compañia',
  `activity` int(4) default NULL COMMENT 'Actividad',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `company_activity` (`company`,`activity`),
  KEY `company` (`company`),
  KEY `activity` (`activity`),
  CONSTRAINT `company_activity_activity_fk` FOREIGN KEY (`activity`) REFERENCES `activity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `company_activity_company_fk` FOREIGN KEY (`company`) REFERENCES `company` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `config` table : 
#

CREATE TABLE `config` (
  `id` int(4) NOT NULL COMMENT 'Identificador de la configuracion.',
  `online` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de si la pagina esta operativa o no.',
  `domain` varchar(128) collate latin1_spanish_ci NOT NULL default 'mydomain.com' COMMENT 'Dominio correspondiente a la pagina.',
  `preview_host` varchar(24) collate latin1_spanish_ci default 'preview' COMMENT 'Host para la previsualizacion de la pagina.',
  `host` varchar(24) collate latin1_spanish_ci default 'www' COMMENT 'Host para la publicacion de la pagina.',
  `ftp_server` varchar(128) collate latin1_spanish_ci default 'ftp.mydomain.com' COMMENT 'Direccion del servidor FTP donde se publicaran las paginas web.',
  `ftp_user` varchar(32) collate latin1_spanish_ci default 'user' COMMENT 'Nombre de usuario del FTP.',
  `ftp_password` char(32) collate latin1_spanish_ci default 'password' COMMENT 'Password del usuario del FTP.',
  `ftp_path` varchar(255) collate latin1_spanish_ci default '/mydomain.com/WEBSITES/www.mydomain.com' COMMENT 'Ruta del directorio del ftp donde guardar las paginas.',
  `smtp_server` varchar(128) collate latin1_spanish_ci default 'smtp.mydomain.com' COMMENT 'Direccion del servidor SMTP a traves del cual se enviaran los mail desde la pagina.',
  `smtp_auth` tinyint(1) default '0' COMMENT 'Indica si tiene o no autentificacion el servidor de correo saliente.',
  `smtp_user` varchar(32) collate latin1_spanish_ci default 'user' COMMENT 'Nombre de usuario del servidor de correo saliente.',
  `smtp_password` varchar(32) collate latin1_spanish_ci default 'password' COMMENT 'Password de usuario del servidor de correo saliente.',
  `from_name` varchar(64) collate latin1_spanish_ci default 'Administrator' COMMENT 'Nombre del remitente de correo electronico.',
  `from_email` varchar(128) collate latin1_spanish_ci default 'admin@mydomain.com' COMMENT 'Direccion de correo de envio de los mensajes.',
  `template` varchar(32) collate latin1_spanish_ci NOT NULL default 'default' COMMENT 'Plantilla con la que se va a generar el sitio.',
  `footer` tinyint(1) NOT NULL default '1' COMMENT 'Indicador para mostrar o no el pie de pagina.',
  `preview_ftp_server` varchar(128) collate latin1_spanish_ci default 'preview.ftp.mydomain.com' COMMENT 'Direccion del servidor FTP donde se publicaran las paginas web preview.',
  `preview_ftp_user` varchar(32) collate latin1_spanish_ci default 'user' COMMENT 'Nombre de usuario de FTP preview',
  `preview_ftp_password` varchar(32) collate latin1_spanish_ci default 'password' COMMENT 'Password del usuario FTP preview',
  `preview_ftp_path` varchar(255) collate latin1_spanish_ci default '/mydomain.com/WEBSITES/preview.mydomain.com' COMMENT 'Path del FTP preview',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='Tabla con datos de configuracion';

#
# Structure for the `config_i18n` table : 
#

CREATE TABLE `config_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador.',
  `config` int(4) NOT NULL COMMENT 'Identificador de config.',
  `language` int(2) NOT NULL COMMENT 'Idioma del contenido.',
  `sitename` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Nombre del sitio.',
  `offline_message` text collate latin1_spanish_ci COMMENT 'Mensaje que aparecera en la pagina cuando esta no este online.',
  `css` text collate latin1_spanish_ci COMMENT 'Codigo CSS para definir o redefinir nuevos estilos.',
  `javascript` text collate latin1_spanish_ci COMMENT 'Codigo JavaScript para definir en las paginas.',
  `description` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Meta tag de descripcion del sitio.',
  `keywords` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Meta tag de palabras claves del sitio.',
  PRIMARY KEY  (`id`),
  KEY `config` (`config`),
  KEY `language` (`language`),
  CONSTRAINT `config_i18n_fk` FOREIGN KEY (`config`) REFERENCES `config` (`id`),
  CONSTRAINT `config_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `db_version` table : 
#

CREATE TABLE `db_version` (
  `version_number` varchar(10) collate latin1_spanish_ci NOT NULL COMMENT 'Numero de Version de la Base de Datos',
  PRIMARY KEY  (`version_number`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Version de la Base de Datos';

#
# Structure for the `diary` table : 
#

CREATE TABLE `diary` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador.',
  `is_categories` tinyint(1) NOT NULL default '1' COMMENT 'Enlaces a categorias',
  `is_past_events` tinyint(1) NOT NULL default '1' COMMENT 'Enlaces a categorias',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `diary_i18n` table : 
#

CREATE TABLE `diary_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador.',
  `diary` int(4) NOT NULL COMMENT 'Identificador de diary.',
  `language` int(2) NOT NULL COMMENT 'Idioma del contenido.',
  `content` text collate latin1_spanish_ci COMMENT 'Texto a mostrar.',
  PRIMARY KEY  (`id`),
  KEY `diary` (`diary`),
  KEY `language` (`language`),
  CONSTRAINT `diary_i18n_fk` FOREIGN KEY (`diary`) REFERENCES `diary` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `diary_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `direct_access_group` table : 
#

CREATE TABLE `direct_access_group` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de grupo acceso directo',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificativo alfanumerico para grupo diferenciar un acceso directo de otro.',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de activa',
  `section` int(4) default NULL COMMENT 'seccion',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `section` (`section`),
  CONSTRAINT `direct_access_group_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='Grupo accesos de sitio web.';

#
# Structure for the `direct_access` table : 
#

CREATE TABLE `direct_access` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Codigo de acceso directo directo.',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador alfanumerico para diferenciar una acceso directo de otra.',
  `direct_access_group` int(4) NOT NULL COMMENT 'Codigo del menu al que pertenece la acceso directo.',
  `type` tinyint(2) default NULL COMMENT 'Tipo de pagina (Generica, Articulo, Album, etc)',
  `level` tinyint(2) default NULL COMMENT 'Nivel del contenido (Listado categorias, categoria, elemento, etc)',
  `ident` int(4) default NULL COMMENT 'Identificador del elemento seleccionado a mostrar en esta opcion de menu.',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de acceso directo activa.',
  `position` tinyint(2) NOT NULL COMMENT 'Posicion de la acceso directo dentro del menu.',
  `image` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'imagen',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `direct_access_group` (`direct_access_group`),
  CONSTRAINT `direct_access_fk` FOREIGN KEY (`direct_access_group`) REFERENCES `direct_access_group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `direct_access_group_i18n` table : 
#

CREATE TABLE `direct_access_group_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del grupo acceso directo internacionalizada.',
  `direct_access_group` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la grupo acceso directo',
  `language` int(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del grupo acceso directo de enlace.',
  PRIMARY KEY  (`id`),
  KEY `direct_access_group` (`direct_access_group`),
  KEY `language` (`language`),
  CONSTRAINT `direct_access_group_i18n_fk` FOREIGN KEY (`direct_access_group`) REFERENCES `direct_access_group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `direct_access_group_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `direct_access_i18n` table : 
#

CREATE TABLE `direct_access_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de acceso directo internacionalizada.',
  `direct_access` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la acceso directo',
  `language` int(2) NOT NULL COMMENT 'Idioma de la acceso directo',
  `label` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la acceso directo.',
  `url` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Direccion de internet en caso de que se trate de una accion externa (http://)',
  `description` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  PRIMARY KEY  (`id`),
  KEY `direct_access` (`direct_access`),
  KEY `language` (`language`),
  CONSTRAINT `direct_access_i18n_fk` FOREIGN KEY (`direct_access`) REFERENCES `direct_access` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `direct_access_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `download_category` table : 
#

CREATE TABLE `download_category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Alias',
  `active` tinyint(1) NOT NULL COMMENT 'Activo',
  `position` tinyint(2) NOT NULL COMMENT 'Posicion',
  `image` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Imagen asociada a la categoria de descargas',
  `section` int(4) default NULL COMMENT 'seccion',
  `items_per_page` tinyint(2) default '20' COMMENT 'Elementos por pagina',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `section` (`section`),
  CONSTRAINT `download_category_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Categorias de descargas';

#
# Structure for the `download` table : 
#

CREATE TABLE `download` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Alias',
  `active` tinyint(1) NOT NULL COMMENT 'Activo',
  `position` tinyint(2) NOT NULL COMMENT 'Posicion',
  `download_category` int(4) NOT NULL COMMENT 'Categoria a la que pertenece la descarga',
  `publish_date` date default NULL COMMENT 'Fecha de publicacion',
  `type` varchar(3) collate latin1_spanish_ci default NULL COMMENT 'Tipo de la descarga',
  `size` int(6) default NULL COMMENT 'Tamaño del fichero',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `download_category` (`download_category`),
  CONSTRAINT `download_fk` FOREIGN KEY (`download_category`) REFERENCES `download_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Descargas';

#
# Structure for the `download_category_i18n` table : 
#

CREATE TABLE `download_category_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `download_category` int(4) NOT NULL COMMENT 'Categoria a la que pertenece',
  `language` int(4) NOT NULL COMMENT 'Idioma',
  `label` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Label',
  PRIMARY KEY  (`id`),
  KEY `download_category` (`download_category`),
  KEY `language` (`language`),
  CONSTRAINT `download_category_i18n_fk` FOREIGN KEY (`download_category`) REFERENCES `download_category` (`id`),
  CONSTRAINT `download_category_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Internacionalizacion de las categorias de descarga';

#
# Structure for the `download_config` table : 
#

CREATE TABLE `download_config` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del config',
  `section` int(4) default NULL COMMENT 'Seccion',
  PRIMARY KEY  (`id`),
  KEY `section` (`section`),
  CONSTRAINT `download_config_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `download_i18n` table : 
#

CREATE TABLE `download_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico',
  `download` int(4) NOT NULL COMMENT 'La descarga',
  `language` int(4) NOT NULL COMMENT 'Idioma',
  `title` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Titulo de la descarga',
  `description` text collate latin1_spanish_ci COMMENT 'Descripcion de la descarga',
  `file` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Fichero',
  PRIMARY KEY  (`id`),
  KEY `download` (`download`),
  KEY `language` (`language`),
  CONSTRAINT `download_i18n_fk` FOREIGN KEY (`download`) REFERENCES `download` (`id`),
  CONSTRAINT `download_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci DELAY_KEY_WRITE=1 COMMENT='Internacionalizacion de las descargas';

#
# Structure for the `faq_category` table : 
#

CREATE TABLE `faq_category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de la categoria de faq',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificativo alfanumerico para diferenciar una categoria de otra.',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de opcion activa',
  `position` tinyint(2) NOT NULL COMMENT 'Posicion dentro de la lista',
  `section` int(4) default NULL COMMENT 'Seccion',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `section` (`section`),
  CONSTRAINT `faq_category_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='Categorias de faq.';

#
# Structure for the `faq` table : 
#

CREATE TABLE `faq` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del faq',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificativo alfanumerico para diferenciar un faq de otro.',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de opcion activa',
  `position` tinyint(2) NOT NULL COMMENT 'Posicion dentro de la lista',
  `faq_category` int(4) NOT NULL COMMENT 'Categoria de faq',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `faq_category` (`faq_category`),
  CONSTRAINT `faq_fk` FOREIGN KEY (`faq_category`) REFERENCES `faq_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='faq.';

#
# Structure for the `faq_category_i18n` table : 
#

CREATE TABLE `faq_category_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de la categoria de faq internacionalizada.',
  `faq_category` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria de faq',
  `language` int(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la categoria de faq.',
  PRIMARY KEY  (`id`),
  KEY `faq_category` (`faq_category`),
  KEY `language` (`language`),
  CONSTRAINT `faq_category_i18n_fk` FOREIGN KEY (`faq_category`) REFERENCES `faq_category` (`id`),
  CONSTRAINT `faq_category_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `faq_config` table : 
#

CREATE TABLE `faq_config` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del config del faq',
  `section` int(4) default NULL COMMENT 'Seccion',
  PRIMARY KEY  (`id`),
  KEY `section` (`section`),
  CONSTRAINT `faq_config_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `faq_i18n` table : 
#

CREATE TABLE `faq_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del texto del faq',
  `faq` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria del faq',
  `language` int(2) NOT NULL COMMENT 'Idioma del enlace',
  `question` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Pregunta del faq.',
  `answer` text collate latin1_spanish_ci NOT NULL COMMENT 'Respuesta del faq.',
  PRIMARY KEY  (`id`),
  KEY `faq` (`faq`),
  KEY `language` (`language`),
  CONSTRAINT `faq_i18n_fk` FOREIGN KEY (`faq`) REFERENCES `faq` (`id`),
  CONSTRAINT `faq_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `footer_banner_category` table : 
#

CREATE TABLE `footer_banner_category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador.',
  `footer` int(4) NOT NULL COMMENT 'Identificador de Pie de pagina.',
  `banner_category` int(4) NOT NULL COMMENT 'Identificador de categoria banner.',
  PRIMARY KEY  (`id`),
  KEY `footer` (`footer`),
  KEY `banner_category` (`banner_category`),
  CONSTRAINT `footer_banner_category_fk` FOREIGN KEY (`footer`) REFERENCES `footer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `footer_banner_category_fk1` FOREIGN KEY (`banner_category`) REFERENCES `banner_category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `footer_i18n` table : 
#

CREATE TABLE `footer_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador.',
  `footer` int(4) NOT NULL COMMENT 'Identificador de Pie de pagina.',
  `language` int(2) NOT NULL COMMENT 'Idioma del contenido.',
  `content` text collate latin1_spanish_ci COMMENT 'Texto a mostrar en el pie de pagina.',
  PRIMARY KEY  (`id`),
  KEY `footer` (`footer`),
  KEY `language` (`language`),
  CONSTRAINT `footer_i18n_fk` FOREIGN KEY (`footer`) REFERENCES `footer` (`id`),
  CONSTRAINT `footer_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `generic_page` table : 
#

CREATE TABLE `generic_page` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Codigo de la pagina generica.',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificativo alfanumerico para diferenciar una pagina de otra.',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de contenido activo.',
  `create_date` datetime NOT NULL default '0000-00-00 00:00:00' COMMENT 'Fecha de creacion del contenido.',
  `menu` int(4) default '0' COMMENT 'Menu asignado a la pagina',
  `section` int(4) default NULL COMMENT 'section',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `menu` (`menu`),
  KEY `section` (`section`),
  CONSTRAINT `generic_page_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='Contenidos genericos';

#
# Structure for the `generic_page_i18n` table : 
#

CREATE TABLE `generic_page_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de lineas de paginas genericas.',
  `generic_page` int(4) NOT NULL COMMENT 'Identificador de relacion con la pagina generica.',
  `language` int(2) NOT NULL COMMENT 'Identificador del idioma correspondiente al contenido.',
  `title` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Titulo del contenido.',
  `content` text collate latin1_spanish_ci COMMENT 'Contenido de la pagina.',
  `description` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Meta Tag description para la cabecera de las paginas. Sobrescribe el definido en la cabecera por defecto.',
  `keywords` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Meta tag keywords para la cabecera de la pagina. Sobreescribe el definido en la cabecera por defecto.',
  PRIMARY KEY  (`id`),
  KEY `generic_page` (`generic_page`),
  KEY `language` (`language`),
  CONSTRAINT `generic_page_i18n_fk` FOREIGN KEY (`generic_page`) REFERENCES `generic_page` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `generic_page_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='Contenido generico en varios idiomas.';

#
# Structure for the `header_i18n` table : 
#

CREATE TABLE `header_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de la tabla.',
  `header` int(4) NOT NULL COMMENT 'Identificador de cabecera a la que pertenece.',
  `language` int(2) NOT NULL COMMENT 'Idioma del contenido.',
  `sitename` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Nombre del sitio. Sobreescribe el contenido de la configuracion generica.',
  `image` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Imagen asignada a la cabecera.',
  `content` text collate latin1_spanish_ci COMMENT 'Texto de la cabecera.',
  `alt` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'alt',
  PRIMARY KEY  (`id`),
  KEY `header` (`header`),
  KEY `language` (`language`),
  CONSTRAINT `header_i18n_fk` FOREIGN KEY (`header`) REFERENCES `header` (`id`),
  CONSTRAINT `header_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `hiru_config` table : 
#

CREATE TABLE `hiru_config` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del config de hiru',
  `section` int(4) default NULL COMMENT 'Seccion',
  PRIMARY KEY  (`id`),
  KEY `section` (`section`),
  CONSTRAINT `hiru_config_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `hiru_organizer_centre` table : 
#

CREATE TABLE `hiru_organizer_centre` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL,
  `telephone` varchar(20) collate latin1_spanish_ci default NULL COMMENT 'telefono',
  `fax` varchar(20) collate latin1_spanish_ci default NULL COMMENT 'fax',
  `email` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'email',
  `postal_code` int(5) default NULL COMMENT 'codigo postal',
  `web` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'web',
  `address` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'direccion',
  `locality` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'localidad',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'activo',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci PACK_KEYS=0 ROW_FORMAT=DYNAMIC COMMENT='InnoDB free: 5120 kB';

#
# Structure for the `hiru_course` table : 
#

CREATE TABLE `hiru_course` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador',
  `init_date` date default NULL COMMENT 'Fecha inicio',
  `end_date` date default NULL COMMENT 'Fecha fin',
  `hiru_organizer_centre` int(4) NOT NULL COMMENT 'Codigo que relaciona el curso',
  `hiru_place` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo que relaciona el lugar',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL,
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Si esta activo',
  `subject` tinyint(4) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `hiru_organizer_centre` (`hiru_organizer_centre`),
  CONSTRAINT `hiru_course_fk` FOREIGN KEY (`hiru_organizer_centre`) REFERENCES `hiru_organizer_centre` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci PACK_KEYS=0 ROW_FORMAT=DYNAMIC COMMENT='InnoDB free: 5120 kB; (`hiru_organizer_centre`) REFER `cms_v';

#
# Structure for the `hiru_course_i18n` table : 
#

CREATE TABLE `hiru_course_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador',
  `hiru_course` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con hiru_course',
  `language` int(2) NOT NULL COMMENT 'Idioma',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre.',
  `info` text collate latin1_spanish_ci NOT NULL COMMENT 'Info.',
  `url` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'url',
  `generic_info` varchar(255) collate latin1_spanish_ci default NULL,
  `objetives` varchar(255) collate latin1_spanish_ci default NULL,
  `contents` varchar(255) collate latin1_spanish_ci default NULL,
  `length` varchar(32) collate latin1_spanish_ci default NULL,
  `employee_registration` varchar(255) collate latin1_spanish_ci default NULL,
  `not_employee_registration` varchar(255) collate latin1_spanish_ci default NULL,
  `giver_entity` varchar(64) collate latin1_spanish_ci default NULL,
  `number_participant` varchar(4) collate latin1_spanish_ci default NULL,
  PRIMARY KEY  (`id`),
  KEY `hiru_course` (`hiru_course`),
  KEY `language` (`language`),
  CONSTRAINT `hiru_course_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`),
  CONSTRAINT `hiru_course_i18n_fk` FOREIGN KEY (`hiru_course`) REFERENCES `hiru_course` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci PACK_KEYS=0 ROW_FORMAT=DYNAMIC COMMENT='InnoDB free: 5120 kB; (`hiru_course`) REFER `cms_void/hiru_c';

#
# Structure for the `link_category` table : 
#

CREATE TABLE `link_category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de la categoria del enlace',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificativo alfanumerico para diferenciar una categoria de otra.',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de opcion activa',
  `position` tinyint(2) NOT NULL COMMENT 'Posicion dentro de la lista',
  `section` int(4) default NULL COMMENT 'seccion',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `section` (`section`),
  CONSTRAINT `link_category_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='Categorias de enlaces.';

#
# Structure for the `link` table : 
#

CREATE TABLE `link` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del enlace',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificativo alfanumerico para diferenciar unenlace de otro.',
  `url` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Direccion de internet del enlace.',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de opcion activa',
  `position` tinyint(2) NOT NULL COMMENT 'Posicion dentro de la lista',
  `link_category` int(4) NOT NULL COMMENT 'Categoria de enlaces',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `link_category` (`link_category`),
  CONSTRAINT `link_fk` FOREIGN KEY (`link_category`) REFERENCES `link_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='Enlaces.';

#
# Structure for the `link_category_i18n` table : 
#

CREATE TABLE `link_category_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de la categoria de enlace internacionalizada.',
  `link_category` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria del enlace',
  `language` int(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la categoria de enlace.',
  PRIMARY KEY  (`id`),
  KEY `link_category` (`link_category`),
  KEY `language` (`language`),
  CONSTRAINT `link_category_i18n_fk` FOREIGN KEY (`link_category`) REFERENCES `link_category` (`id`),
  CONSTRAINT `link_category_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `link_config` table : 
#

CREATE TABLE `link_config` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del config del link',
  `section` int(4) default NULL COMMENT 'Seccion',
  PRIMARY KEY  (`id`),
  KEY `section` (`section`),
  CONSTRAINT `link_config_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `link_i18n` table : 
#

CREATE TABLE `link_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del texto del enlace',
  `link` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria del enlace',
  `language` int(2) NOT NULL COMMENT 'Idioma del enlace',
  `label` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del enlace.',
  `description` text collate latin1_spanish_ci COMMENT 'Pequeña descripcion del link',
  PRIMARY KEY  (`id`),
  KEY `link` (`link`),
  KEY `language` (`language`),
  CONSTRAINT `link_i18n_fk` FOREIGN KEY (`link`) REFERENCES `link` (`id`),
  CONSTRAINT `link_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `menu_option` table : 
#

CREATE TABLE `menu_option` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Codigo de opcion de menu.',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador alfanumerico para diferenciar una opcion de otra.',
  `menu` int(4) NOT NULL COMMENT 'Codigo del menu al que pertenece la opcion.',
  `sep` tinyint(1) NOT NULL default '0' COMMENT 'Tipo de opcion, separador o pagina.',
  `type` tinyint(2) default NULL COMMENT 'Tipo de pagina (Generica, Articulo, Album, etc)',
  `level` tinyint(2) default NULL COMMENT 'Nivel del contenido (Listado categorias, categoria, elemento, etc)',
  `ident` int(4) default NULL COMMENT 'Identificador del elemento seleccionado a mostrar en esta opcion de menu.',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de opcion activa.',
  `position` tinyint(2) NOT NULL COMMENT 'Posicion de la opcion dentro del menu.',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `menu` (`menu`),
  CONSTRAINT `menu_option_fk` FOREIGN KEY (`menu`) REFERENCES `menu` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `menu_option_i18n` table : 
#

CREATE TABLE `menu_option_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de opcion de menu internacionalizada.',
  `menu_option` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la opcion de menu',
  `language` int(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la opcion de menu.',
  `url` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Direccion de internet en caso de que se trate de una accion externa (http://)',
  PRIMARY KEY  (`id`),
  KEY `menu_option` (`menu_option`),
  KEY `language` (`language`),
  CONSTRAINT `menu_option_i18n_fk` FOREIGN KEY (`menu_option`) REFERENCES `menu_option` (`id`),
  CONSTRAINT `menu_option_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `modular_page` table : 
#

CREATE TABLE `modular_page` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de pagina modular',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificativo alfanumerico para diferenciar una pagina de otro.',
  `type` tinyint(2) default NULL COMMENT 'Tipo de pagina',
  `homepage` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de homepage.',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de opcion activa.',
  `section` int(4) default NULL COMMENT 'Seccion',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `section` (`section`),
  CONSTRAINT `modular_page_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci AVG_ROW_LENGTH=1092 ROW_FORMAT=DYNAMIC COMMENT='Menus de sitio web.';

#
# Structure for the `modular_page_i18n` table : 
#

CREATE TABLE `modular_page_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de modular_page internacionalizada.',
  `modular_page` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la modular_page',
  `language` int(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la modular_page.',
  PRIMARY KEY  (`id`),
  KEY `modular_page` (`modular_page`),
  KEY `language` (`language`),
  CONSTRAINT `modular_page_i18n_fk` FOREIGN KEY (`modular_page`) REFERENCES `modular_page` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `modular_page_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `modular_page_option` table : 
#

CREATE TABLE `modular_page_option` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Codigo de opcion de pag modular.',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador alfanumerico para diferenciar una opcion de otra.',
  `modular_page` int(4) NOT NULL COMMENT 'Codigo de la pag modular al que pertenece la opcion.',
  `type` tinyint(2) default NULL COMMENT 'Tipo de pagina',
  `ident` int(4) default NULL COMMENT 'Identificador del elemento seleccionado a mostrar en esta opcion de pag modular.',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de opcion activa.',
  `position` tinyint(2) NOT NULL COMMENT 'Posicion de la opcion dentro de la pag modular.',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `modular_page` (`modular_page`),
  CONSTRAINT `modular_page_option_fk` FOREIGN KEY (`modular_page`) REFERENCES `modular_page` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `modular_page_option_i18n` table : 
#

CREATE TABLE `modular_page_option_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de opcion de modular_page internacionalizada.',
  `modular_page_option` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la opcion de modular_page',
  `language` int(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la opcion de modular_page.',
  PRIMARY KEY  (`id`),
  KEY `modular_page_option` (`modular_page_option`),
  KEY `language` (`language`),
  CONSTRAINT `modular_page_option_i18n_fk` FOREIGN KEY (`modular_page_option`) REFERENCES `modular_page_option_i18n` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `modular_page_option_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `product_category` table : 
#

CREATE TABLE `product_category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de la categoria de producto',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificativo alfanumerico para diferenciar una categoria de producto de otra.',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de opcion activa',
  `parent` int(4) default NULL COMMENT 'Codigo que relaciona la categoria de producto con la categoria de producto padre',
  `section` int(4) default NULL COMMENT 'section',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `parent` (`parent`),
  KEY `section` (`section`),
  CONSTRAINT `product_category_fk` FOREIGN KEY (`parent`) REFERENCES `product_category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `product_category_fk1` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='categoria de producto.';

#
# Structure for the `product` table : 
#

CREATE TABLE `product` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador el producto',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificativo alfanumerico para diferenciar un producto de otro.',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de opcion activa',
  `price` double(15,2) default '0.00' COMMENT 'Precio del Articulo',
  `offer_price` double(15,2) default '0.00' COMMENT 'Precio del Articulo en oferta',
  `brand` int(4) default NULL COMMENT 'codigo de marca',
  `product_category` int(4) default NULL COMMENT 'codigo de categoria',
  `image` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Imagen',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `brand` (`brand`),
  KEY `product_category` (`product_category`),
  CONSTRAINT `product_fk` FOREIGN KEY (`brand`) REFERENCES `brand` (`id`),
  CONSTRAINT `product_fk1` FOREIGN KEY (`product_category`) REFERENCES `product_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='categoria de producto.';

#
# Structure for the `product_category_config` table : 
#

CREATE TABLE `product_category_config` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del config de categoria de productos',
  `section` int(4) default NULL COMMENT 'Seccion',
  PRIMARY KEY  (`id`),
  KEY `section` (`section`),
  CONSTRAINT `product_category_config_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `product_category_i18n` table : 
#

CREATE TABLE `product_category_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de la categoria de producto internacionalizada.',
  `product_category` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria de producto',
  `language` int(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la categoria de producto.',
  PRIMARY KEY  (`id`),
  KEY `product_category` (`product_category`),
  KEY `language` (`language`),
  CONSTRAINT `product_category_i18n_fk` FOREIGN KEY (`product_category`) REFERENCES `product_category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `product_category_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `product_i18n` table : 
#

CREATE TABLE `product_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de el producto internacionalizada.',
  `product` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con el producto',
  `language` int(2) NOT NULL COMMENT 'Idioma de la opcion',
  `short_label` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion corta del producto.',
  `label` text collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del producto.',
  `alt` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'alt',
  PRIMARY KEY  (`id`),
  KEY `product` (`product`),
  KEY `language` (`language`),
  CONSTRAINT `product_i18n_fk` FOREIGN KEY (`product`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `product_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `sidebar_option` table : 
#

CREATE TABLE `sidebar_option` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Codigo de opcion de menu.',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador alfanumerico para diferenciar una opcion de otra.',
  `sidebar` int(4) NOT NULL COMMENT 'Codigo del menu al que pertenece la opcion.',
  `type` tinyint(2) default NULL COMMENT 'Tipo de pagina (Generica, Articulo, Album, etc)',
  `level` tinyint(2) default NULL COMMENT 'Nivel del contenido (Listado categorias, categoria, elemento, etc)',
  `ident` int(4) default NULL COMMENT 'Identificador del elemento seleccionado a mostrar en esta opcion de menu.',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de opcion activa.',
  `position` tinyint(2) NOT NULL COMMENT 'Posicion de la opcion dentro del menu.',
  `side` tinyint(2) NOT NULL default '0' COMMENT 'Lado',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`),
  KEY `sidebar` (`sidebar`),
  CONSTRAINT `sidebar_option_fk` FOREIGN KEY (`sidebar`) REFERENCES `sidebar` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `sidebar_option_i18n` table : 
#

CREATE TABLE `sidebar_option_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de opcion de menu internacionalizada.',
  `sidebar_option` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la opcion de menu',
  `language` int(2) NOT NULL COMMENT 'Idioma de la opcion',
  `label` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la opcion de menu.',
  PRIMARY KEY  (`id`),
  KEY `sidebar_option` (`sidebar_option`),
  KEY `language` (`language`),
  CONSTRAINT `sidebar_option_i18n_fk` FOREIGN KEY (`sidebar_option`) REFERENCES `sidebar_option` (`id`),
  CONSTRAINT `sidebar_option_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `sport_position` table : 
#

CREATE TABLE `sport_position` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de la posicion',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificativo alfanumerico para diferenciar una posicion de otra.',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='Posiciones.';

#
# Structure for the `sport_category` table : 
#

CREATE TABLE `sport_category` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de la categoria de deportes',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificativo alfanumerico para diferenciar una categoria de otra.',
  `default_` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de opcion activa',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='Categorias de deportes.';

#
# Structure for the `sport_club` table : 
#

CREATE TABLE `sport_club` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del club.',
  `sport_category` int(4) NOT NULL COMMENT 'Codigo que relaciona la categoria',
  `description` varchar(28) collate latin1_spanish_ci default NULL COMMENT 'Descripcion.',
  `stadium` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Estadio.',
  `image` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Foto.',
  `logo` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Logo.',
  `default_` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de opcion activa',
  PRIMARY KEY  (`id`),
  KEY `sport_category` (`sport_category`),
  CONSTRAINT `sport_club_fk` FOREIGN KEY (`sport_category`) REFERENCES `sport_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `sport_nationality` table : 
#

CREATE TABLE `sport_nationality` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de la nacionalidad',
  `alias` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Identificativo alfanumerico para diferenciar una posicion de otra.',
  `image` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Foto.',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `alias` (`alias`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='Posiciones.';

#
# Structure for the `sport_player` table : 
#

CREATE TABLE `sport_player` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del jugador.',
  `name` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre',
  `sport_position` int(4) default NULL COMMENT 'Posicion',
  `born_date` date default NULL COMMENT 'Fecha nacimiento',
  `born_place` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Lugar nacimiento',
  `weight` double(5,2) default NULL COMMENT 'Peso',
  `lenght` double(5,2) default NULL COMMENT 'Altura',
  `sport_nationality` int(4) default NULL COMMENT 'Nacionalidad',
  `comunitary` tinyint(1) default '1' COMMENT 'Comunitario',
  `photo` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'foto',
  `sport_club` int(4) default NULL COMMENT 'Equipo',
  `number` int(4) default NULL COMMENT 'Numero',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Activo',
  PRIMARY KEY  (`id`),
  KEY `sport_position` (`sport_position`),
  KEY `sport_club` (`sport_club`),
  KEY `sport_nationality` (`sport_nationality`),
  CONSTRAINT `sport_player_fk` FOREIGN KEY (`sport_position`) REFERENCES `sport_position` (`id`),
  CONSTRAINT `sport_player_fk1` FOREIGN KEY (`sport_club`) REFERENCES `sport_club` (`id`),
  CONSTRAINT `sport_player_fk3` FOREIGN KEY (`sport_nationality`) REFERENCES `sport_nationality` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `sport_career_path` table : 
#

CREATE TABLE `sport_career_path` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de plantilla.',
  `sport_player` int(4) NOT NULL COMMENT 'Equipo',
  `club` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'equipo',
  `init_date` date default NULL COMMENT 'Fecha inicio',
  `end_date` date default NULL COMMENT 'Fecha fin',
  PRIMARY KEY  (`id`),
  KEY `sport_player` (`sport_player`),
  KEY `sport_club` (`club`),
  CONSTRAINT `sport_career_path_fk` FOREIGN KEY (`sport_player`) REFERENCES `sport_player` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `sport_category_i18n` table : 
#

CREATE TABLE `sport_category_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de la categoria de articulo internacionalizada.',
  `sport_category` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la categoria de articulo',
  `language` int(2) NOT NULL COMMENT 'Idioma de la opcion',
  `description` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la categoria de articulo.',
  PRIMARY KEY  (`id`),
  KEY `sport_category` (`sport_category`),
  KEY `language` (`language`),
  CONSTRAINT `sport_category_i18n_fk` FOREIGN KEY (`sport_category`) REFERENCES `sport_category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `sport_category_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `sport_coach` table : 
#

CREATE TABLE `sport_coach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de plantilla.',
  `sport_club` int(4) NOT NULL COMMENT 'Temporada',
  `job` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Cargo',
  `name` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre',
  PRIMARY KEY  (`id`),
  KEY `sport_club` (`sport_club`),
  CONSTRAINT `sport_coach_fk` FOREIGN KEY (`sport_club`) REFERENCES `sport_club` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `sport_config` table : 
#

CREATE TABLE `sport_config` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador del config de sport',
  `section` int(4) default NULL COMMENT 'Seccion',
  PRIMARY KEY  (`id`),
  KEY `section` (`section`),
  CONSTRAINT `sport_config_fk` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `sport_nationality_i18n` table : 
#

CREATE TABLE `sport_nationality_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de la nacionalidad.',
  `sport_nationality` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la nacionalidad',
  `language` int(2) NOT NULL COMMENT 'Idioma de la opcion',
  `description` varchar(28) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la posicion.',
  PRIMARY KEY  (`id`),
  KEY `sport_nationality` (`sport_nationality`),
  KEY `language` (`language`),
  CONSTRAINT `sport_nationality_i18n_fk` FOREIGN KEY (`sport_nationality`) REFERENCES `sport_nationality` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `sport_nationality_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `sport_position_i18n` table : 
#

CREATE TABLE `sport_position_i18n` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de la posicion internacionalizada.',
  `sport_position` int(4) NOT NULL COMMENT 'Codigo que relaciona el idioma con la posicion',
  `language` int(2) NOT NULL COMMENT 'Idioma de la opcion',
  `description` varchar(28) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la posicion.',
  PRIMARY KEY  (`id`),
  KEY `sport_position` (`sport_position`),
  KEY `language` (`language`),
  CONSTRAINT `sport_position_i18n_fk` FOREIGN KEY (`sport_position`) REFERENCES `sport_position` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `sport_position_i18n_fk1` FOREIGN KEY (`language`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC;

#
# Structure for the `sport_season` table : 
#

CREATE TABLE `sport_season` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador de la temporada de deportes',
  `description` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion.',
  `default_` tinyint(1) NOT NULL default '1' COMMENT 'Indicador de opcion activa',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=DYNAMIC COMMENT='Temporada de deportes.';

#
# Data for the `db_version` table  (LIMIT 0,500)
#

INSERT INTO `db_version` (`version_number`) VALUES 
  ('2.0.4');

COMMIT;

SET FOREIGN_KEY_CHECKS=1;
