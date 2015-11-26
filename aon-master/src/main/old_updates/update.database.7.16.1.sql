# Database: aon_master
# Version: Actualizacion de la version 7.16.1 a la version 7.17.0.
# Created by: girazu
# Creation Date: 24/04/2013 18:45
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `pos` DROP FOREIGN KEY `FK_POS_ITEM`;
ALTER TABLE `pos` DROP KEY `IDX_POS_ITEM`;
ALTER TABLE `pos` DROP `item`;
ALTER TABLE `pos` DROP `invoiceable`;
ALTER TABLE `pos` DROP `initial_amount`;

ALTER TABLE `pos` ADD `department` int(4) DEFAULT NULL COMMENT 'Identificador del Departamento' AFTER `domain`;
ALTER TABLE `pos` ADD KEY `IDX_POS_DEPARTMENT` (`department`);
ALTER TABLE `pos` ADD CONSTRAINT `FK_POS_DEPARTMENT` FOREIGN KEY (`department`) REFERENCES `department` (`id`);
ALTER TABLE `pos` ADD `series` char(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie del POS' AFTER `name`;
ALTER TABLE `pos` ADD `customer` int(4) DEFAULT NULL COMMENT 'Identificador del Cliente' AFTER `series`;
ALTER TABLE `pos` ADD KEY `IDX_POS_CUSTOMER` (`customer`);
ALTER TABLE `pos` ADD CONSTRAINT `FK_POS_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`);
ALTER TABLE `pos` ADD `display_mode` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Modo de visualizacion en pantalla' AFTER `customer`;

ALTER TABLE `invoice` DROP FOREIGN KEY `FK_INVOICE_POS`;
ALTER TABLE `invoice` DROP KEY `IDX_INVOICE_POS`;
ALTER TABLE `invoice` DROP `pos`;

ALTER TABLE `invoice` ADD `pos_shift` int(4) DEFAULT NULL COMMENT 'Identificador del Turno de trabajo' AFTER `advance`;
ALTER TABLE `invoice` ADD KEY `IDX_INVOICE_POS_SHIFT` (`pos_shift`);
ALTER TABLE `invoice` ADD CONSTRAINT `FK_INVOICE_POS_SHIFT` FOREIGN KEY (`pos_shift`) REFERENCES `pos_shift` (`id`);
UPDATE `invoice` SET `pos_shift` = (SELECT `id` FROM `pos_shift` WHERE `invoice` = `invoice`.`id`);

ALTER TABLE `pos_shift` DROP FOREIGN KEY `FK_POS_SHIFT_INVOICE`;
ALTER TABLE `pos_shift` DROP KEY `IDX_POS_SHIFT_INVOICE`;
ALTER TABLE `pos_shift` DROP `invoice`;

ALTER TABLE `pos_shift` DROP FOREIGN KEY `FK_POS_SHIFT_USER`;
ALTER TABLE `pos_shift` DROP KEY `IDX_POS_SHIFT_USER`;
ALTER TABLE `pos_shift` CHANGE `user` `username` varchar(16) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Usuario del Turno';
UPDATE `pos_shift` SET `username` = (SELECT `login` FROM `user` WHERE `id` = `pos_shift`.`username`);

ALTER TABLE `invoice` ADD KEY `IDX_INVOICE_REFERENCE_CODE` (`reference_code`);

UPDATE `purchase` SET `address` = NULL WHERE 0 = (SELECT COUNT(*) FROM `raddress` WHERE `id` = `purchase`.`address` AND `registry` = `purchase`.`supplier`);
UPDATE `purchase` SET `address` = (SELECT MIN(`id`) FROM `raddress` WHERE `registry` = `purchase`.`supplier`) WHERE `address` IS NULL;
ALTER TABLE `purchase` ADD KEY `IDX_PURCHASE_RADDRESS` (`address`);
ALTER TABLE `purchase` ADD CONSTRAINT `FK_PURCHASE_RADDRESS` FOREIGN KEY (`address`) REFERENCES `raddress` (`id`);


UPDATE `db_version` SET `version_number` = '7.17.0';

COMMIT;
