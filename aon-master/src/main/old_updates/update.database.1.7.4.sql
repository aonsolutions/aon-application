# Database: aon_master
# Version: Actualizacion de la version 1.7.4 a la version 1.8.0
# Created by: girazu
# Creation Date: 04/07/2008 09:21
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `web_info` DROP `title`;

ALTER TABLE `web_info` DROP `content`;

CREATE TABLE `web_info_page` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Codigo de la Pagina',
  `name` varchar(64) character set latin1 collate latin1_spanish_ci default NULL COMMENT 'Nombre de la Pagina.',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Pagina',
  `position` tinyint(2) default NULL COMMENT 'Posicion de la Pagina en el menu',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indica si la Pagina esta activa o no',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Paginas pertenecientes a la ficha web';

CREATE TABLE `web_info_page_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Codigo del Detalle de la Pagina',
  `web_info_page` int(4) NOT NULL COMMENT 'Identificador de la Pagina a la que corresponde el detalle',
  `title` varchar(255) character set latin1 collate latin1_spanish_ci default NULL COMMENT 'Titulo del contenido de la Pagina',
  `layout` int(2) default NULL COMMENT 'Tipo de plantilla',
  `content` text character set latin1 collate latin1_spanish_ci COMMENT 'Texto del contenido de la Pagina',
  `extra` varchar(255) character set latin1 collate latin1_spanish_ci default NULL COMMENT 'Campo reservado a otros datos de la Pagina',
  PRIMARY KEY  (`id`),
  KEY `web_info_page` (`web_info_page`),
  CONSTRAINT `web_info_page_detail_fk1` FOREIGN KEY (`web_info_page`) REFERENCES `web_info_page` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Detalles de la pagina perteneciente a la ficha web';

CREATE TABLE `web_info_page_resource` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Codigo del Recurso de la Pagina',
  `web_info_page` int(4) default NULL COMMENT 'Codigo de la Pagina',
  `rattach` int(4) default NULL COMMENT 'Identificador del Archivo Adjunto calificado como Recurso',
  `content` varchar(255) character set latin1 collate latin1_spanish_ci default NULL COMMENT 'Texto del Recurso',
  PRIMARY KEY  (`id`),
  KEY `web_info_page` (`web_info_page`),
  CONSTRAINT `web_info_page_resource_fk1` FOREIGN KEY (`web_info_page`) REFERENCES `web_info_page` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Recursos de la pagina perteneciente a la ficha web';

CREATE TABLE `web_info_style` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Codigo del Estilo de la Pagina',
  `variable` varchar(128) character set latin1 collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la variable del Estilo',
  `value` varchar(255) character set latin1 collate latin1_spanish_ci default NULL COMMENT 'Valor de la variable del Estilo',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Estilos a utilizar en las plantillas para generar ficha web';

ALTER TABLE `activity` MODIFY `workgroup` int(4) default NULL COMMENT 'Identificador del Grupo de Trabajo';

ALTER TABLE `activity_process` MODIFY `activity` int(4) default NULL COMMENT 'Identificador de la Actividad';

ALTER TABLE `campaign` MODIFY `activity_type` int(4) default NULL COMMENT 'Identificador del Tipo de Actividad';


UPDATE `db_version` SET `version_number` = '1.8.0';

COMMIT;
