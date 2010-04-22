# Database: aon_master
# Version: Actualizacion de la version 4.6.1 a la version 4.6.2.
# Created by: girazu
# Creation Date: 13/11/2009 13:08
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



UPDATE `rdir_staff` SET `percent_share` = 0 WHERE `percent_share` IS NULL;

ALTER TABLE `rdir_staff` MODIFY `percent_share` double default '0' COMMENT 'Porcentaje de acciones (solo para socios)';

ALTER TABLE `income` MODIFY `address` int(4) default NULL COMMENT 'Identificador de la Direccion del Proveedor';

ALTER TABLE `income` ADD `pay_method` int(4) default NULL COMMENT 'Identificador de la Forma de Pago' AFTER `issue_time`;

ALTER TABLE `income` ADD `workplace` int(4) default NULL COMMENT 'Identificador del Centro de Trabajo';

ALTER TABLE `income` ADD KEY `IDX_INCOME_WORKPLACE` (`workplace`);

ALTER TABLE `income` ADD CONSTRAINT `FK_INCOME_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`);

ALTER TABLE `income` ADD `scope` int(4) NOT NULL default '1' COMMENT 'Ambito del Albaran';

ALTER TABLE `income` ADD KEY `IDX_INCOME_SCOPE` (`scope`);

ALTER TABLE `income` ADD CONSTRAINT `FK_INCOME_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);

ALTER TABLE `income` ADD `number_of_pymnts` smallint(2) default '0' COMMENT 'Numero de Vencimientos';

ALTER TABLE `income` ADD `days_to_first_pymnt` smallint(2) default '0' COMMENT 'Dias al primer Vencimiento';

ALTER TABLE `income` ADD `days_between_pymnts` smallint(2) default '0' COMMENT 'Dias entre Vencimientos';

ALTER TABLE `income` ADD `pymnt_days` varchar(8) collate latin1_spanish_ci default '0' COMMENT 'Dias de pago';

ALTER TABLE `income` ADD `bank` int(4) default NULL COMMENT 'Identificador de la Entidad Bancaria';

ALTER TABLE `income` ADD KEY `IDX_INCOME_BANK` (`bank`);

ALTER TABLE `income` ADD CONSTRAINT `FK_INCOME_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`);

ALTER TABLE `income` ADD `bank_account` varchar(30) collate latin1_spanish_ci default NULL COMMENT 'Numero de cuenta en la Entidad Bancaria';

ALTER TABLE `income_detail` MODIFY `item` int(4) NOT NULL COMMENT 'Identificador del Articulo del Detalle de Albaran';

ALTER TABLE `income_detail` MODIFY `description` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Detalle de Albaran';

ALTER TABLE `income_detail` MODIFY `warehouse` int(4) NOT NULL COMMENT 'Identificador del Almacen';

ALTER TABLE `income_detail` MODIFY `quantity` double(15,3) default NULL COMMENT 'Cantidad del Detalle de Albaran';

ALTER TABLE `income_detail` MODIFY `price` double(15,3) default NULL COMMENT 'Precio del Detalle de Albaran';

ALTER TABLE `income_detail` ADD `line` smallint(2) default '0' COMMENT 'Numero de linea del Detalle dentro del Albaran' AFTER `income`;

ALTER TABLE `income_detail` ADD `type` tinyint(2) default '0' COMMENT 'Tipo de Detalle de Albaran' AFTER `discount_expr`;

ALTER TABLE `income_detail` ADD `source` tinyint(2) default '0' COMMENT 'Origen del Detalle de Albaran' AFTER `type`;

ALTER TABLE `purchase` MODIFY `supplier` int(4) NOT NULL default '0' COMMENT 'Identificador del Proveedor' AFTER `id`;

ALTER TABLE `purchase` MODIFY `document_type` tinyint(2) default '1' COMMENT 'Tipo de Pedido' AFTER `pay_method`;

ALTER TABLE `purchase` CHANGE `discount_expression` `discount_expr` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descuentos del Pedido';

ALTER TABLE `purchase` ADD `address` int(4) default NULL COMMENT 'Identificador de la Direccion del Proveedor' AFTER `number`;

ALTER TABLE `purchase` ADD `scope` int(4) NOT NULL default '1' COMMENT 'Ambito del Pedido';

ALTER TABLE `purchase` ADD KEY `IDX_PURCHASE_SCOPE` (`scope`);

ALTER TABLE `purchase` ADD CONSTRAINT `FK_PURCHASE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);

ALTER TABLE `purchase` ADD `number_of_pymnts` smallint(2) default '0' COMMENT 'Numero de Vencimientos';

ALTER TABLE `purchase` ADD `days_to_first_pymnt` smallint(2) default '0' COMMENT 'Dias al primer Vencimiento';

ALTER TABLE `purchase` ADD `days_between_pymnts` smallint(2) default '0' COMMENT 'Dias entre Vencimientos';

ALTER TABLE `purchase` ADD `pymnt_days` varchar(8) collate latin1_spanish_ci default '0' COMMENT 'Dias de pago';

ALTER TABLE `purchase` ADD `bank` int(4) default NULL COMMENT 'Identificador de la Entidad Bancaria';

ALTER TABLE `purchase` ADD KEY `IDX_PURCHASE_BANK` (`bank`);

ALTER TABLE `purchase` ADD CONSTRAINT `FK_PURCHASE_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`);

ALTER TABLE `purchase` ADD `bank_account` varchar(30) collate latin1_spanish_ci default NULL COMMENT 'Numero de cuenta en la Entidad Bancaria';

ALTER TABLE `purchase_detail` MODIFY `line` smallint(2) default '1' COMMENT 'Numero de linea del Detalle dentro del Pedido';

ALTER TABLE `purchase_detail` MODIFY `description` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Detalle de Pedido';

ALTER TABLE `purchase_detail` MODIFY `quantity` double default '0' COMMENT 'Cantidad del Detalle de Pedido';

ALTER TABLE `purchase_detail` MODIFY `price` double default '0' COMMENT 'Precio del Detalle de Pedido';

ALTER TABLE `purchase_detail` ADD `delivered` double default '0' COMMENT 'Cantidad entregada del Detalle de Pedido';

ALTER TABLE `item` ADD `barcode` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Codigo de barras del Articulo';

ALTER TABLE `item` ADD `alternative_item` int(4) default NULL COMMENT 'Identificador del Articulo alternativo';

ALTER TABLE `item` ADD KEY `IDX_ALTERNATIVE_ITEM` (`alternative_item`);

ALTER TABLE `item` ADD CONSTRAINT `FK_ALTERNATIVE_ITEM` FOREIGN KEY (`alternative_item`) REFERENCES `item` (`id`);


UPDATE `db_version` SET `version_number` = '4.6.2';

COMMIT;
