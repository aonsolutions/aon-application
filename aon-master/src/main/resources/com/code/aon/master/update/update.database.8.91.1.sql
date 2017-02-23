# Database: aon_master
# Version: Actualizacion de la version 8.91.1 a la version 8.92.0.
# Created by: girazu
# Creation Date: 16/02/2017 14:10

BEGIN;

ALTER TABLE `sales` ADD `delivery_date` date DEFAULT NULL COMMENT 'Fecha de entrega' AFTER `purchase_generated`;
ALTER TABLE `sales_detail` MODIFY `delivery_date` date DEFAULT NULL COMMENT 'Fecha de entrega';
UPDATE `sales` SET `delivery_date` = (
	SELECT MIN(`delivery_date`) FROM `sales_detail` WHERE `sales_detail`.`sales` = `sales`.`id` AND `delivery_date` IS NOT NULL)
WHERE `id` IN (SELECT DISTINCT `sales` FROM `sales_detail` WHERE `delivery_date` IS NOT NULL);

UPDATE `sales_detail` SET `carrier` = (
	SELECT `carrier` FROM `sales` WHERE `sales`.`id` = `sales_detail`.`sales` AND `carrier` IS NOT NULL)
WHERE `carrier` IS NULL;

ALTER TABLE `purchase` ADD `delivery_date` date DEFAULT NULL COMMENT 'Fecha de entrega' AFTER `email_communication`;
ALTER TABLE `purchase_detail` MODIFY `delivery_date` date DEFAULT NULL COMMENT 'Fecha de entrega';
UPDATE `purchase` SET `delivery_date` = (
	SELECT MIN(`delivery_date`) FROM `purchase_detail` WHERE `purchase_detail`.`purchase` = `purchase`.`id` AND `delivery_date` IS NOT NULL)
WHERE `id` IN (SELECT DISTINCT `purchase` FROM `purchase_detail` WHERE `delivery_date` IS NOT NULL);

UPDATE `purchase_detail` SET `carrier` = (
	SELECT `carrier` FROM `purchase` WHERE `purchase`.`id` = `purchase_detail`.`purchase` AND `carrier` IS NOT NULL)
WHERE `carrier` IS NULL;

ALTER TABLE `enterprise_activity` ADD `vat_tax` int(4) DEFAULT NULL COMMENT 'Identificador del IVA por defecto' AFTER `surcharge`;
ALTER TABLE `enterprise_activity` ADD KEY `IDX_ENTERPRISE_ACTIVITY_TAX_VAT` (`vat_tax`);
ALTER TABLE `enterprise_activity` ADD CONSTRAINT `FK_ENTERPRISE_ACTIVITY_TAX_VAT` FOREIGN KEY (`vat_tax`) REFERENCES `tax` (`id`);

ALTER TABLE `enterprise_activity` ADD `retention_tax` int(4) DEFAULT NULL COMMENT 'Identificador del IRPF por defecto' AFTER `vat_tax`;
ALTER TABLE `enterprise_activity` ADD KEY `IDX_ENTERPRISE_ACTIVITY_TAX_RETENTION` (`retention_tax`);
ALTER TABLE `enterprise_activity` ADD CONSTRAINT `FK_ENTERPRISE_ACTIVITY_TAX_RETENTION` FOREIGN KEY (`retention_tax`) REFERENCES `tax` (`id`);


UPDATE `db_version` SET `version_number` = '8.92.0';

COMMIT;
