# Database: aon_master
# Version: Actualizacion de la version 6.3.1 a la version 6.4.0.
# Created by: girazu
# Creation Date: 10/05/2011 10:02
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `invoice` ADD `service` tinyint(1) default '0' COMMENT 'Indica si es una Factura de servicios' AFTER `scope`;

ALTER TABLE `invoice` ADD `rectification_type` tinyint(2) default '0' COMMENT 'Tipo de rectificacion (Normal o Especial)' AFTER `service`;

ALTER TABLE `invoice` ADD `rectification_invoice` int(4) default NULL COMMENT 'Relacion de rectificacion de Facturas' AFTER `rectification_type`;

ALTER TABLE `invoice` ADD KEY `IDX_INVOICE_INVOICE` (`rectification_invoice`);

ALTER TABLE `invoice` ADD CONSTRAINT `FK_INVOICE_INVOICE` FOREIGN KEY (`rectification_invoice`) REFERENCES `invoice` (`id`);

ALTER TABLE `series` ADD `offer` tinyint(1) default '1' COMMENT 'Indica si es una Serie para Presupuestos' AFTER `workplace`;

ALTER TABLE `series` ADD `sales` tinyint(1) default '1' COMMENT 'Indica si es una Serie para Pedidos' AFTER `offer`;

ALTER TABLE `series` ADD `delivery` tinyint(1) default '1' COMMENT 'Indica si es una Serie para Albaranes' AFTER `sales`;

ALTER TABLE `series` ADD `invoice` tinyint(1) default '1' COMMENT 'Indica si es una Serie para Facturas' AFTER `delivery`;

ALTER TABLE `series` ADD `rectification` tinyint(1) default '0' COMMENT 'Indica si es una Serie para Facturas rectificativas' AFTER `invoice`;


UPDATE `db_version` SET `version_number` = '6.4.0';

COMMIT;
