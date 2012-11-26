# Database: aon_master
# Version: Actualizacion de la version 1.5.0 a la version 1.6.0
# Created by: girazu
# Creation Date: 08/05/2008 12:37
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



CREATE TABLE `invoice_detail_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `invoice_detail` int(4) NOT NULL default '0' COMMENT 'Identificador de la Linea de Factura',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `invoice_detail` (`invoice_detail`),
  KEY `account` (`account`),
  CONSTRAINT `invoice_detail_account_ibfk_1` FOREIGN KEY (`invoice_detail`) REFERENCES `invoice_detail` (`id`),
  CONSTRAINT `invoice_detail_account_ibfk_2` FOREIGN KEY (`account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables asociadas a Lineas de Facturas';

ALTER TABLE `invoice` ADD `reference_code` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Codigo de referencia de la Factura' AFTER `number`;

UPDATE `invoice` SET `reference_code` = '';

UPDATE `invoice` SET `reference_code` = CONCAT(`series`, '/') WHERE `series` IS NOT NULL AND `series` != '';

UPDATE `invoice` SET `reference_code` = CONCAT(`reference_code`, `number`);

ALTER TABLE `invoice` DROP INDEX `series`;

ALTER TABLE `invoice` ADD UNIQUE KEY `series` (`series`,`number`,`type`) ;

UPDATE `account` SET `description` = REPLACE(`description`, ' null', '');

UPDATE `account` SET `description` = REPLACE(`description`, 'Cliente: ', '');

UPDATE `account` SET `description` = REPLACE(`description`, 'Proveedor: ', '');

UPDATE `account` SET `description` = REPLACE(`description`, 'Acreedor: ', '');

UPDATE `account` SET `description` = REPLACE(`description`, 'Banco: ', '');

UPDATE `account` SET `description` = REPLACE(`description`, 'Cuenta: ', '');

UPDATE `account` SET `description` = REPLACE(`description`, 'Prestamo: ', '');

UPDATE `account` SET `description` = REPLACE(`description`, 'Leasing: ', '');


UPDATE `db_version` SET `version_number` = '1.6.0';

COMMIT;
