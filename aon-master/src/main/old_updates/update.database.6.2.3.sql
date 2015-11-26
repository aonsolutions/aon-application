# Database: aon_master
# Version: Actualizacion de la version 6.2.3 a la version 6.3.0.
# Created by: girazu
# Creation Date: 30/03/2011 16:58
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

UPDATE `registry` SET `type` = 0 WHERE `document` = 0 and `type` is null;

UPDATE `rpaymethod` SET `number_of_pymnts` = 1 WHERE `number_of_pymnts` = 0;

UPDATE `offer` SET `number_of_pymnts` = 1 WHERE `number_of_pymnts` = 0;

UPDATE `sales` SET `number_of_pymnts` = 1 WHERE `number_of_pymnts` = 0;

UPDATE `delivery` SET `number_of_pymnts` = 1 WHERE `number_of_pymnts` = 0;

UPDATE `purchase` SET `number_of_pymnts` = 1 WHERE `number_of_pymnts` = 0;

UPDATE `income` SET `number_of_pymnts` = 1 WHERE `number_of_pymnts` = 0;

UPDATE `target_supplier` SET `number_of_pymnts` = 1 WHERE `number_of_pymnts` = 0;

UPDATE `sales_detail` SET `discount_expr` = 0.0 WHERE `discount_expr` IS NULL;

UPDATE `purchase_detail` SET `discount_expr` = 0.0 WHERE `discount_expr` IS NULL;

ALTER TABLE `customer_fee` MODIFY `price` double default '0' COMMENT 'Precio de la Cuota';

ALTER TABLE `offer_detail` MODIFY `price` double default '0' COMMENT 'Precio del Articulo';

ALTER TABLE `sales_detail` MODIFY `quantity` double(15,3) default '0.000' COMMENT 'Cantidad del Detalle de Pedido';

ALTER TABLE `purchase_detail` MODIFY `quantity` double(15,3) default '0.000' COMMENT 'Cantidad del Detalle de Pedido';

ALTER TABLE `delivery_detail` MODIFY `price` double default '0' COMMENT 'Precio del Detalle de Albaran';

ALTER TABLE `income_detail` MODIFY `price` double default '0' COMMENT 'Precio del Detalle de Albaran';

ALTER TABLE `invoice_detail` MODIFY `quantity` double(15,3) default '0.000' COMMENT 'Cantidad del Detalle de Factura';

ALTER TABLE `invoice_detail` MODIFY `taxable_base` double(15,4) default '0.0000' COMMENT 'Base Imponible del Detalle de Factura';

ALTER TABLE `invoice` ADD `taxable_base` double default '0' COMMENT 'Base Imponible de la Factura';

ALTER TABLE `invoice` ADD `vat_quota` double default '0' COMMENT 'Cuota de IVA de la Factura';

ALTER TABLE `invoice` ADD `retention_quota` double default '0' COMMENT 'Cuota de IRPF de la Factura';

ALTER TABLE `invoice` ADD `total` double default '0' COMMENT 'Total Factura';

DROP TABLE `account_entry_link`;

DROP TABLE `account_budget_detail`;

DROP TABLE `account_budget`;

UPDATE `offer` SET `address` = NULL WHERE `address` NOT IN (SELECT `id` FROM `raddress`);

ALTER TABLE `offer` ADD KEY `IDX_OFFER_RADDRESS` (`address`);

ALTER TABLE `offer` ADD CONSTRAINT `FK_OFFER_RADDRESS` FOREIGN KEY (`address`) REFERENCES `raddress` (`id`);


UPDATE `db_version` SET `version_number` = '6.3.0';

COMMIT;
