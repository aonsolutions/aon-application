# Database: aon_master
# Version: Actualizacion de la version 3.1.0 a la version 3.2.0.
# Created by: girazu
# Creation Date: 16/03/2009 18:41
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



CREATE TABLE `tax_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Cuenta Contable del Impuesto',
  `tax` int(4) NOT NULL default '0' COMMENT 'Identificador del Impuesto',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Cuenta Contable del Impuesto',
  PRIMARY KEY  (`id`),
  KEY `tax` (`tax`),
  KEY `account` (`account`),
  CONSTRAINT `tax_account_ibfk_1` FOREIGN KEY (`tax`) REFERENCES `tax` (`id`),
  CONSTRAINT `tax_account_ibfk_2` FOREIGN KEY (`account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Impuestos de la Factura';

CREATE TABLE `invoice_tax_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `invoice_tax` int(4) NOT NULL default '0' COMMENT 'Identificador de la Linea de Impuesto',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `invoice_tax` (`invoice_tax`),
  KEY `account` (`account`),
  CONSTRAINT `invoice_tax_account_ibfk_1` FOREIGN KEY (`invoice_tax`) REFERENCES `invoice_tax` (`id`),
  CONSTRAINT `invoice_tax_account_ibfk_2` FOREIGN KEY (`account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables asociadas a Impuestos de Facturas';


UPDATE `db_version` SET `version_number` = '3.2.0';

COMMIT;
