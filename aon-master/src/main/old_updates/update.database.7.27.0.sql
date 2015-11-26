# Database: aon_master
# Version: Actualizacion de la version 7.27.0 a la version 7.27.1.
# Created by: girazu
# Creation Date: 14/01/2014 16:35
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

CREATE TABLE `allotment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `hotel` int(4) NOT NULL COMMENT 'Identificador del Hotel',
  `agency` int(4) NOT NULL COMMENT 'Identificador de la agencia de viajes',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio del Cupo',
  `end_date` date NOT NULL COMMENT 'Fecha de fin del Cupo',
  `quantity` int(4) default 0 COMMENT 'Cupo de Habitaciones',
  `remarks` text collate latin1_spanish_ci COMMENT 'Comentarios',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si el Cupo esta activo o no',
  `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime default NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime default NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_ALLOTMENT_DOMAIN` (`domain`),
  KEY `IDX_ALLOTMENT_HOTEL` (`hotel`),
  KEY `IDX_ALLOTMENT_AGENCY` (`agency`),
  CONSTRAINT `FK_ALLOTMENT_AGENCY` FOREIGN KEY (`agency`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_ALLOTMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ALLOTMENT_HOTEL` FOREIGN KEY (`hotel`) REFERENCES `hotel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cupos de seguridad';

CREATE TABLE `allotment_item` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `allotment` int(4) NOT NULL COMMENT 'Identificador del Cupo de seguridad',
  `item` int(4) NOT NULL COMMENT 'Identificador del Tipo de Habitacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_ALLOTMENT_ITEM_DOMAIN` (`domain`),
  KEY `IDX_ALLOTMENT_ITEM_ALLOTMENT` (`allotment`),
  KEY `IDX_ALLOTMENT_ITEM_ITEM` (`item`),
  CONSTRAINT `FK_ALLOTMENT_ITEM_ALLOTMENT` FOREIGN KEY (`allotment`) REFERENCES `allotment` (`id`),
  CONSTRAINT `FK_ALLOTMENT_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ALLOTMENT_ITEM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Habitacion por Cupo';

CREATE TABLE `stop_sales` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `hotel` int(4) NOT NULL COMMENT 'Identificador del Hotel',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio del Paro',
  `end_date` date NOT NULL COMMENT 'Fecha de fin del Paro',
  `remarks` text collate latin1_spanish_ci COMMENT 'Comentarios',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si el Paro esta activo o no',
  `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime default NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime default NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_STOP_SALES_DOMAIN` (`domain`),
  KEY `IDX_STOP_SALES_HOTEL` (`hotel`),
  CONSTRAINT `FK_STOP_SALES_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_STOP_SALES_HOTEL` FOREIGN KEY (`hotel`) REFERENCES `hotel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Paros de venta';

CREATE TABLE `stop_sales_item` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `stop_sales` int(4) NOT NULL COMMENT 'Identificador del Paro de ventas',
  `item` int(4) NOT NULL COMMENT 'Identificador del Tipo de Habitacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_STOP_SALES_ITEM_DOMAIN` (`domain`),
  KEY `IDX_STOP_SALES_ITEM_STOP_SALES` (`stop_sales`),
  KEY `IDX_STOP_SALES_ITEM_ITEM` (`item`),
  CONSTRAINT `FK_STOP_SALES_ITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_STOP_SALES_ITEM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_STOP_SALES_ITEM_STOP_SALES` FOREIGN KEY (`stop_sales`) REFERENCES `stop_sales` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Habitacion por Paro';

CREATE TABLE `fs_model390` (
   `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
   `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
   `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
   `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
   `administration` tinyint(2) NOT NULL COMMENT 'Administracion',
   `status` tinyint(2) NOT NULL default '0' COMMENT 'Estado de la Declaracion',
   `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad',
   `document` varchar(9) collate latin1_spanish_ci default NULL COMMENT 'NIF',
   `name` varchar(45) collate latin1_spanish_ci default NULL COMMENT 'Nombre',
   `complementary` tinyint(1) NOT NULL default '0' COMMENT 'Declaracion complementaria',
   `replacement` tinyint(1) NOT NULL default '0' COMMENT 'Declaracion sustitutiva',
   `comments` text collate latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
   `receipt` varchar(13) collate latin1_spanish_ci default NULL COMMENT 'Numero de Declaracion',
   `replaced_receipt` varchar(13) collate latin1_spanish_ci default NULL COMMENT 'Numero de Declaracion sustituida',
   `model` text collate latin1_spanish_ci COMMENT 'Modelo XML de la Declaracion',
   `response` varchar(30) collate latin1_spanish_ci default NULL COMMENT 'Respuesta AEAT',
   PRIMARY KEY  (`id`),
   KEY `IDX_FS_MODEL390_DOMAIN` (`domain`),
   KEY `IDX_FS_MODEL390_ENTERPRISE` (`enterprise`),
   CONSTRAINT `FK_FS_MODEL390_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
   CONSTRAINT `FK_FS_MODEL390_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaraciones 390';


UPDATE `db_version` SET `version_number` = '7.27.1';

COMMIT;
