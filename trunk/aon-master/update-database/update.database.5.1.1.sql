# Database: aon_master
# Version: Actualizacion de la version 5.1.1 a la version 5.2.0.
# Created by: girazu
# Creation Date: 13/05/2010 11:29
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

CREATE TABLE `offer_detail_commission` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `offer_detail` int(4) NOT NULL default '0' COMMENT 'Identificador de la Linea de Presupuesto',
  `commission` double default '0' COMMENT 'Porcentaje de Comision',
  `amount` double default '0' COMMENT 'Importe de la Comision',
  `status` tinyint(2) default '0' COMMENT 'Estado de la Comision',
  `pay_date` date default NULL COMMENT 'Fecha de liquidacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_OFFER_DETAIL_COMMISSION_OFFER_DETAIL` (`offer_detail`),
  CONSTRAINT `FK_OFFER_DETAIL_COMMISSION_OFFER_DETAIL` FOREIGN KEY (`offer_detail`) REFERENCES `offer_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comisiones asociadas a Lineas de Presupuestos'; 


UPDATE `db_version` SET `version_number` = '5.2.0';

COMMIT;
