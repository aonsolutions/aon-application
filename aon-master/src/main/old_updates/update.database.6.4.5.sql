# Database: aon_master
# Version: Actualizacion de la version 6.4.5 a la version 6.4.6.
# Created by: girazu
# Creation Date: 08/06/2011 17:00
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

DROP TABLE `commercial_tracking`;

DROP TABLE `project`;

CREATE TABLE `project` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Proyecto',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios',
  `date` date NOT NULL COMMENT 'Fecha del Proyecto',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `seller` int(4) DEFAULT NULL COMMENT 'Identificador del Comercial',
  `source` tinyint(2) NOT NULL COMMENT 'Origen del Proyecto',    
  `status` tinyint(2) NOT NULL COMMENT 'Estado del Proyecto',
  `status_date` date default NULL COMMENT 'Fecha del Estado del Proyecto',
  `probability` int(4) default NULL COMMENT 'Probabilidad del Proyecto',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROJECT_TARGET` (`target`),
  KEY `IDX_PROJECT_SELLER` (`seller`),
  CONSTRAINT `FK_PROJECT_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_PROJECT_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Proyectos';

CREATE TABLE `commercial_tracking` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `date` datetime NOT NULL COMMENT 'Fecha del Seguimiento Comercial',
  `seller` int(4) NOT NULL COMMENT 'Identificador del Comercial',
  `project` int(4) NOT NULL COMMENT 'Identificador del Proyecto',
  `activity` int(4) NOT NULL COMMENT 'Identificador de la Actividad Comercial',
  `comments` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Comentarios del Seguimiento Comercial',
  `status` tinyint(2) NOT NULL COMMENT 'Estado del Seguimiento Comercial',
  `next_commercial_tracking` int(4) default NULL COMMENT 'Identificador del siguiente Seguimiento Comercial',
  `end_date` datetime default NULL COMMENT 'Fecha de cierre del Seguimiento Comercial',
  `offer` int(4) default NULL COMMENT 'Identificador del Presupuesto',
  `allDay` tinyint(1) default '0' COMMENT 'Indica si el Seguimiento Comercial dura todo el dia',
  `location` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Ubicacion del Seguimiento Comercial',
  PRIMARY KEY  (`id`),
  KEY `IDX_COMMERCIAL_TRACKING_SELLER` (`seller`),
  KEY `IDX_COMMERCIAL_TRACKING_PROJECT` (`project`),
  KEY `IDX_COMMERCIAL_TRACKING_ACTIVITY` (`activity`),
  KEY `IDX_COMMERCIAL_TRACKING_NEXT` (`next_commercial_tracking`),
  KEY `IDX_COMMERCIAL_TRACKING_OFFER` (`offer`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_ACTIVITY` FOREIGN KEY (`activity`) REFERENCES `commercial_activity` (`id`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_NEXT` FOREIGN KEY (`next_commercial_tracking`) REFERENCES `commercial_tracking` (`id`),
  CONSTRAINT `FK_COMMERCIAL_TRACKING_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Seguimientos Comerciales';

CREATE TABLE `warehouse_transfer` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie del Traspaso',
  `number` int(4) NOT NULL default '0' COMMENT 'Numero del Traspaso',
  `issue_time` datetime NOT NULL COMMENT 'Fecha de emision del Traspaso',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios del Traspaso',
  `source_warehouse` int(4) default NULL COMMENT 'Identificador del Almacen Origen',
  `target_warehouse` int(4) default NULL COMMENT 'Identificador del Almacen Destino',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `series` (`series`,`number`),
  KEY `IDX_WT_ISSUE_TIME` (`issue_time`),
  KEY `IDX_WT_SOURCE` (`source_warehouse`),
  KEY `IDX_WT_TARGET` (`target_warehouse`),
  CONSTRAINT `FK_WT_SOURCE` FOREIGN KEY (`source_warehouse`) REFERENCES `warehouse` (`id`),
  CONSTRAINT `FK_WT_TARGET` FOREIGN KEY (`target_warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Traspasos entre Almacenes';

CREATE TABLE `warehouse_transfer_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `warehouse_transfer` int(4) NOT NULL COMMENT 'Identificador del Traspaso',
  `item` int(4) NOT NULL COMMENT 'Identificador del Articulo del Detalle de Traspaso',
  `quantity` double(15,3) default NULL COMMENT 'Cantidad del Detalle de Traspaso',
  PRIMARY KEY  (`id`),
  KEY `IDX_WTD_WT` (`warehouse_transfer`),
  KEY `IDX_WTD_ITEM` (`item`),
  CONSTRAINT `FK_WTD_WT` FOREIGN KEY (`warehouse_transfer`) REFERENCES `warehouse_transfer` (`id`),
  CONSTRAINT `FK_WTD_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Traspasos entre Almacenes';


UPDATE `db_version` SET `version_number` = '6.4.6';

COMMIT;
