# Database: aon_master
# Version: Actualizacion de la version 7.0.2 a la version 7.0.3.
# Created by: girazu
# Creation Date: 20/01/2012 14:00
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

CREATE TABLE `department` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `name` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre del Departamento',
  PRIMARY KEY  (`id`),
  KEY `IDX_DEPARTMENT_DOMAIN` (`domain`),
  CONSTRAINT `FK_DEPARTMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Departamentos';

CREATE TABLE `workplace_department` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `department` int(4) NOT NULL COMMENT 'Identificador del Departamento',
  `catalogue` int(4) default NULL COMMENT 'Identificador del Catalogo',
  `active` tinyint(1) default '1' COMMENT 'Indica si el Departamento esta activo o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_WORKPLACE_DEPARTMENT_DOMAIN` (`domain`),
  KEY `IDX_WORKPLACE_DEPARTMENT_WORKPLACE` (`workplace`),
  KEY `IDX_WORKPLACE_DEPARTMENT_DEPARTMENT` (`department`),
  KEY `IDX_WORKPLACE_DEPARTMENT_CATALOGUE` (`catalogue`),
  CONSTRAINT `FK_WORKPLACE_DEPARTMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_WORKPLACE_DEPARTMENT_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`),
  CONSTRAINT `FK_WORKPLACE_DEPARTMENT_DEPARTMENT` FOREIGN KEY (`department`) REFERENCES `department` (`id`),
  CONSTRAINT `FK_WORKPLACE_DEPARTMENT_CATALOGUE` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Departamentos del Centro de Trabajo';

CREATE TABLE `proposal` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `issue_date` date default NULL COMMENT 'Fecha de emision de la Propuesta',
  `department` int(4) default NULL COMMENT 'Identificador del Departamento',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `remarks` text collate latin1_spanish_ci COMMENT 'Observaciones de la Propuesta',
  `status` tinyint(2) default '0' COMMENT 'Estado de la Propuesta',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROPOSAL_DOMAIN` (`domain`),
  KEY `IDX_PROPOSAL_SCOPE` (`scope`),
  KEY `IDX_PROPOSAL_DEPARTMENT` (`department`),
  KEY `IDX_PROPOSAL_WORKPLACE` (`workplace`),
  CONSTRAINT `FK_PROPOSAL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROPOSAL_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_PROPOSAL_DEPARTMENT` FOREIGN KEY (`department`) REFERENCES `department` (`id`),
  CONSTRAINT `FK_PROPOSAL_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Propuestas de Compra';

CREATE TABLE `proposal_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `proposal` int(4) NOT NULL default '0' COMMENT 'Identificador de la Propuesta de Compra',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador del Articulo',
  `description` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `quantity` double(15,3) default '0.000' COMMENT 'Cantidad',
  `price` double default '0' COMMENT 'Precio',
  `discount_expr` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descuentos',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROPOSAL_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_PROPOSAL_DETAIL_PROPOSAL` (`proposal`),
  KEY `IDX_PROPOSAL_DETAIL_ITEM` (`item`),
  CONSTRAINT `FK_PROPOSAL_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROPOSAL_DETAIL_PROPOSAL` FOREIGN KEY (`proposal`) REFERENCES `proposal` (`id`),
  CONSTRAINT `FK_PROPOSAL_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de la Propuesta de Compra';

ALTER TABLE `item_supplier` ADD `workplace` int(4) default NULL COMMENT 'Identificador del Centro de Trabajo';
ALTER TABLE `item_supplier` ADD KEY `IDX_ITEM_SUPPLIER_WORKPLACE` (`workplace`);
ALTER TABLE `item_supplier` ADD CONSTRAINT `FK_ITEM_SUPPLIER_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`);

UPDATE `db_version` SET `version_number` = '7.0.3';

COMMIT;
