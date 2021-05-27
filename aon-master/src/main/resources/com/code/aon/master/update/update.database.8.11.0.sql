# Database: aon_master
# Version: Actualizacion de la version 8.11.0 a la version 8.11.1.
# Created by: girazu
# Creation Date: 17/12/2014 15:55

BEGIN;

CREATE TABLE `rtax` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `registry` int(4) NOT NULL COMMENT 'Identificador de Persona o Empresa',
  `tax` int(4) NOT NULL COMMENT 'Identificador del Impuesto',
  `percentage` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Porcentaje de recargo actual',
  `surcharge` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Porcentaje de recargo de equivalencia actual',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio de vigencia',
  `end_date` date DEFAULT NULL COMMENT 'Fecha de fin de vigencia',
  PRIMARY KEY (`id`),
  KEY `IDX_RTAX_DOMAIN` (`domain`),
  KEY `IDX_RTAX_REGISTRY` (`registry`),
  KEY `IDX_RTAX_TAX` (`tax`),
  CONSTRAINT `FK_RTAX_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RTAX_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`),
  CONSTRAINT `FK_RTAX_TAX` FOREIGN KEY (`tax`) REFERENCES `tax` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Impuestos redefinidos para Personas o Empresas';


UPDATE `db_version` SET `version_number` = '8.11.1';

COMMIT;
