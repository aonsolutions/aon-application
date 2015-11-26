# Database: aon_master
# Version: Actualizacion de la version 7.19.0 a la version 7.20.0.
# Created by: girazu
# Creation Date: 20/06/2013 16:05
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `income` ADD `reference_code` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Codigo de referencia del Albaran' AFTER `project`;
UPDATE `income` SET `reference_code` = '';
UPDATE `income` SET `reference_code` = CONCAT(`series`, '/') WHERE `series` IS NOT NULL AND `series` != '';
UPDATE `income` SET `reference_code` = CONCAT(`reference_code`, `number`);
ALTER TABLE `income` MODIFY `reference_code` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo de referencia del Albaran';
ALTER TABLE `income` DROP KEY `IDX_UNQ_INCOME_DOMAIN_SUPPLIER_SERIES_NUMBER`;
ALTER TABLE `income` DROP `series`;
ALTER TABLE `income` DROP `number`;
ALTER TABLE `income` ADD UNIQUE KEY `IDX_UNQ_INCOME_DOMAIN_SUPPLIER_REFERENCE_CODE` (`domain`,`supplier`,`reference_code`);

ALTER TABLE `sales` ADD `purchase_reference` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Codigo de referencia del Pedido de Compra' AFTER `number`;

ALTER TABLE `purchase` ADD `carrier` int(4) default NULL COMMENT 'Identificador de la Agencia de Transporte';
ALTER TABLE `purchase` ADD `shipping_alternative_address` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Primera parte de la Direccion de entrega';
ALTER TABLE `purchase` ADD `shipping_alternative_address2` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Segunda parte de la Direccion de entrega';
ALTER TABLE `purchase` ADD `shipping_alternative_zip` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo Postal de entrega';
ALTER TABLE `purchase` ADD `shipping_alternative_city` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Localidad de entrega';
ALTER TABLE `purchase` ADD `shipping_alternative_phone` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Telefono de contacto de la entrega';
ALTER TABLE `purchase` ADD `shipping_alternative_recipient` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Destinatario de la entrega';
ALTER TABLE `purchase` ADD `shipping_contact` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Nombre del contacto para la entrega';
ALTER TABLE `purchase` ADD `shipping_period` tinyint(2) default '0' COMMENT 'Tipo de periodo de entrega';
ALTER TABLE `purchase` ADD KEY `IDX_PURCHASE_CARRIER` (`carrier`);
ALTER TABLE `purchase` ADD CONSTRAINT `FK_PURCHASE_CARRIER` FOREIGN KEY (`carrier`) REFERENCES `carrier` (`registry`);

ALTER TABLE `supplier` ADD `purchase_valuated` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Pedido se imprime valorado segun el Proveedor' AFTER `scope`;


UPDATE `db_version` SET `version_number` = '7.20.0';

COMMIT;
