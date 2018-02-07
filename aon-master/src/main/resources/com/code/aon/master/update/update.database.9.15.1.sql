# Database: aon_master
# Version: Actualizacion de la version 9.15.0 a la version 9.23.0
# Created by: anderibz
# Creation Date: 07/02/2018 10:30

BEGIN;

#
# Structure for the `invoice_detail_commission` table : 
#

CREATE TABLE `invoice_detail_commission` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `invoice_detail` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Linea de Factura',
  `commission` double DEFAULT '0' COMMENT 'Porcentaje de Comision',
  `amount` double DEFAULT '0' COMMENT 'Importe de la Comision',
  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado de la Comision',
  `pay_date` date DEFAULT NULL COMMENT 'Fecha de liquidacion',
  PRIMARY KEY (`id`),
  KEY `IDX_INVOICE_DETAIL_COMMISSION_INVOICE_DETAIL` (`invoice_detail`),
  KEY `IDX_INVOICE_DETAIL_COMMISSION_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVOICE_DETAIL_COMMISSION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_INVOICE_DETAIL_COMMISSION_INVOICE_DETAIL` FOREIGN KEY (`invoice_detail`) REFERENCES `invoice_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones asociadas a Lineas de Facturas';

UPDATE `db_version` SET `version_number` = '9.23.0';

COMMIT;