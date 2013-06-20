# Database: aon_master
# Version: Actualizacion de la version 7.15.2 a la version 7.16.0.
# Created by: girazu
# Creation Date: 02/04/2013 10:45
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `customer` ADD `invoicing_group` int(4) DEFAULT NULL COMMENT 'Identificador de Grupo de Facturacion' AFTER `e_invoice`;
ALTER TABLE `customer` ADD KEY `IDX_CUSTOMER_INVOICING_GROUP` (`invoicing_group`);
ALTER TABLE `customer` ADD CONSTRAINT `FK_CUSTOMER_INVOICING_GROUP` FOREIGN KEY (`invoicing_group`) REFERENCES `invoicing_group` (`id`);
UPDATE `customer` SET `invoicing_group` = (SELECT `invoicing_group` FROM `invoicing_group_detail` WHERE `child` = `customer`.`registry`) WHERE `invoicing_group` IS NULL;
UPDATE `customer` SET `invoicing_group` = (SELECT `id` FROM `invoicing_group` WHERE `parent` = `customer`.`registry`) WHERE `invoicing_group` IS NULL;

ALTER TABLE `customer` ADD `project_grouped` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Cliente desea agrupar Proyectos en una sola Factura' AFTER `invoicing_group`;

ALTER TABLE `customer_fee` ADD `invoicing_group` int(4) DEFAULT NULL COMMENT 'Identificador de Grupo de Facturacion' AFTER `security_level`;
ALTER TABLE `customer_fee` ADD KEY `IDX_CUSTOMER_FEE_INVOICING_GROUP` (`invoicing_group`);
ALTER TABLE `customer_fee` ADD CONSTRAINT `FK_CUSTOMER_FEE_INVOICING_GROUP` FOREIGN KEY (`invoicing_group`) REFERENCES `invoicing_group` (`id`);
UPDATE `customer_fee` SET `invoicing_group` = (SELECT `invoicing_group` FROM `customer` WHERE `registry` = `customer_fee`.`customer`);

ALTER TABLE `customer_fee` ADD `project` int(4) DEFAULT NULL COMMENT 'Identificador del Proyecto' AFTER `domain`;
ALTER TABLE `customer_fee` ADD KEY `IDX_CUSTOMER_FEE_PROJECT` (`project`);
ALTER TABLE `customer_fee` ADD CONSTRAINT `FK_CUSTOMER_FEE_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`);

ALTER TABLE `customer_fee` ADD `seller` int(4) DEFAULT NULL COMMENT 'Identificador de Agente Comercial' AFTER `invoicing_group`;
ALTER TABLE `customer_fee` ADD KEY `IDX_CUSTOMER_FEE_SELLER` (`seller`);
ALTER TABLE `customer_fee` ADD CONSTRAINT `FK_CUSTOMER_FEE_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`);

ALTER TABLE `invoice_detail` ADD `seller` int(4) DEFAULT NULL COMMENT 'Identificador de Agente Comercial' AFTER `taxes`;
ALTER TABLE `invoice_detail` ADD KEY `IDX_INVOICE_DETAIL_SELLER` (`seller`);
ALTER TABLE `invoice_detail` ADD CONSTRAINT `FK_INVOICE_DETAIL_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`);

ALTER TABLE `invoicing_group` DROP FOREIGN KEY `FK_INVOICING_GROUP_REGISTRY`;
ALTER TABLE `invoicing_group` DROP KEY `IDX_INVOICING_GROUP_REGISTRY`;
ALTER TABLE `invoicing_group` CHANGE `parent` `customer` int(4) NOT NULL COMMENT 'Identificador del Cliente';
ALTER TABLE `invoicing_group` ADD KEY `IDX_INVOICING_GROUP_CUSTOMER` (`customer`);
ALTER TABLE `invoicing_group` ADD CONSTRAINT `FK_INVOICING_GROUP_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`);
ALTER TABLE `invoicing_group` ADD `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Grupo de Facturacion';
UPDATE `invoicing_group` SET `description` = (SELECT `name` FROM `registry` WHERE `id` = `invoicing_group`.`customer`);
ALTER TABLE `invoicing_group` MODIFY `description` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Grupo de Facturacion';
ALTER TABLE `invoicing_group` ADD `customer_grouped` tinyint(1) DEFAULT '1' COMMENT 'Indica si el Grupo de Facturacion agrupa los Clientes en una sola Factura';

DROP TABLE `invoicing_group_detail`;


UPDATE `db_version` SET `version_number` = '7.16.0';

COMMIT;
