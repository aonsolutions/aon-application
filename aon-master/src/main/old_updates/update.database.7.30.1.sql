# Database: aon_master
# Version: Actualizacion de la version 7.30.0 a la version 7.31.0.
# Created by: girazu
# Creation Date: 13/02/2014 17:35
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

CREATE TABLE `stop_sales_tariff` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `stop_sales` int(4) NOT NULL COMMENT 'Identificador del Paro de ventas',
  `tariff` int(4) NOT NULL COMMENT 'Identificador de la Tarifa',
  PRIMARY KEY (`id`),
  KEY `IDX_STOP_SALES_TARIFF_DOMAIN` (`domain`),
  KEY `IDX_STOP_SALES_TARIFF_STOP_SALES` (`stop_sales`),
  KEY `IDX_STOP_SALES_TARIFF_TARIFF` (`tariff`),
  CONSTRAINT `FK_STOP_SALES_TARIFF_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_STOP_SALES_TARIFF_STOP_SALES` FOREIGN KEY (`stop_sales`) REFERENCES `stop_sales` (`id`),
  CONSTRAINT `FK_STOP_SALES_TARIFF_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tarifas por Paro';


UPDATE `db_version` SET `version_number` = '7.31.0';

COMMIT;
