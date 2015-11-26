# Database: aon_master
# Version: Actualizacion de la version 7.22.1 a la version 7.23.0.
# Created by: girazu
# Creation Date: 04/10/2013 13:50
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `supplier` ADD `withholding_farmer` tinyint(1) default '0' COMMENT 'Indica si el Proveedor pertenece al Regimen Especial de Agricultura y Pesca' AFTER `withholding`;

ALTER TABLE `invoice` ADD `withholding_farmer` tinyint(1) default '0' COMMENT 'Indica si la Factura aplica retencion de Regimen Especial de Agricultura y Pesca' AFTER `withholding`;

ALTER TABLE `invoice_tax` ADD `base` double(15,4) default '0.0000' COMMENT 'Base Imponible de Impuesto del Detalle de la Factura' AFTER `tax_type`;
UPDATE `invoice_tax`, `invoice_detail` SET `base` = `taxable_base` WHERE `invoice_tax`.`invoice_detail` = `invoice_detail`.`id`;


UPDATE `db_version` SET `version_number` = '7.23.0';

COMMIT;
