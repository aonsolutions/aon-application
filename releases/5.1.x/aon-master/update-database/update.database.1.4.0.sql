# Database: aon_master
# Version: Actualizacion de la version 1.4.0 a la version 1.5.0
# Created by: girazu
# Creation Date: 30/04/2008 08:17
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



CREATE TABLE `product_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Cuenta Contable del Producto',
  `product` int(4) NOT NULL default '0' COMMENT 'Identificador del Producto',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Cuenta Contable del Producto',
  PRIMARY KEY  (`id`),
  KEY `product` (`product`),
  KEY `account` (`account`),
  CONSTRAINT `product_account_ibfk_1` FOREIGN KEY (`product`) REFERENCES `product` (`id`),
  CONSTRAINT `product_account_ibfk_2` FOREIGN KEY (`account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Productos';


UPDATE `db_version` SET `version_number` = '1.5.0';

COMMIT;
