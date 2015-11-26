# Database: aon_master
# Version: Actualizacion de la version 4.4.0 a la version 4.5.0.
# Created by: atellitu
# Creation Date: 20/10/2009 10:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



DROP TABLE `offer_term`;

CREATE TABLE `offer_term` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `offer` int(4) NOT NULL COMMENT 'Identificador del Presupuesto',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Nombre de la Condicion Comercial',
  `description` text collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Condicion Comercial',
  `term_general` tinyint(1) default '0' COMMENT 'Indica si la Condicion es particular o general',
  PRIMARY KEY (`id`),
  KEY `IDX_OFFER_TERM_OFFER` (`offer`),
  CONSTRAINT `FK_OFFER_TERM_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Condiciones del Presupuesto';

CREATE TABLE `commercial_term` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Nombre de la Condicion Comercial',
  `description` text collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Condicion Comercial',
  `term_general` tinyint(1) default '0' COMMENT 'Indica si la Condición es particular o general',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Condiciones Comerciales';


UPDATE `db_version` SET `version_number` = '4.5.0';

COMMIT;
