# Database: aon_master
# Version: Actualizacion de la version 1.2.6 a la version 1.3.0
# Created by: girazu
# Creation Date: 15/01/2008 17:53
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `invoice` ADD `comments` text collate latin1_spanish_ci COMMENT 'Comentarios de la Factura';

ALTER TABLE `tas_item` ADD `add_info` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Informacion adicional del Articulo';

ALTER TABLE `support_order` ADD `levelti` varchar(8) collate latin1_spanish_ci default NULL COMMENT 'Nivel del Articulo de la Orden de Reparacion (p.e. Gasolina)' AFTER `counterti`;

CREATE TABLE `appraiser` (
  `registry` int(4) NOT NULL default '0' COMMENT 'Registro del Perito',
  PRIMARY KEY  (`registry`),
  CONSTRAINT `appraiser_ibfk_1` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Peritos';

CREATE TABLE `support_order_insurance` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `support_order` int(4) NOT NULL COMMENT 'Identificador de la Orden de Reparacion',
  `insurance` int(4) NOT NULL COMMENT 'Identificador de la Compañia de Seguros',
  `appraiser` int(4) default NULL COMMENT 'Identificador del Perito',
  `claim_number` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Numero de siniestro o reclamacion',
  `policy_type` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Tipo de poliza',
  `franchise` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Franquicia',
  PRIMARY KEY  (`id`),
  KEY `support_order` (`support_order`),
  KEY `insurance` (`insurance`),
  KEY `appraiser` (`appraiser`),
  CONSTRAINT `support_order_insurance_fk1` FOREIGN KEY (`support_order`) REFERENCES `support_order` (`id`),
  CONSTRAINT `support_order_insurance_fk2` FOREIGN KEY (`insurance`) REFERENCES `customer` (`registry`),
  CONSTRAINT `support_order_insurance_fk3` FOREIGN KEY (`appraiser`) REFERENCES `appraiser` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Compañias de Seguros asociadas a Ordenes de Reparacion';

CREATE TABLE `delivery_detail_labour` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `delivery_detail` int(4) NOT NULL COMMENT 'Identificador de la Linea de Albaran',
  `employee` int(4) default NULL COMMENT 'Identificador del Empleado',
  `quantity` double(15,3) default NULL COMMENT 'Numero de horas de mano de obra',
  PRIMARY KEY  (`id`),
  KEY `delivery_detail` (`delivery_detail`),
  KEY `employee` (`employee`),
  CONSTRAINT `delivery_detail_labour_fk1` FOREIGN KEY (`delivery_detail`) REFERENCES `delivery_detail` (`id`),
  CONSTRAINT `delivery_detail_labour_fk2` FOREIGN KEY (`employee`) REFERENCES `employee` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Horas de mano de obra asociadas a una Linea de Albaran';


UPDATE `db_version` SET `version_number` = '1.3.0';

COMMIT;
