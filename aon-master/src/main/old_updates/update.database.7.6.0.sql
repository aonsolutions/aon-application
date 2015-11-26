# Database: aon_master
# Version: Actualizacion de la version 7.6.0 a la version 7.6.1.
# Created by: girazu
# Creation Date: 26/12/2012 16:15
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `user` ADD `locale` varchar(8) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Locale del Usuario';
ALTER TABLE `user` ADD `pageLimit` int(4) DEFAULT NULL COMMENT 'Limite de filas en pantalla del Usuario';
ALTER TABLE `user` ADD `linesPageLimit` int(4) DEFAULT NULL COMMENT 'Limite de filas en pantalla en lineas del Usuario';
ALTER TABLE `user` ADD `initAction` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre la acicón de inicio del Usuario';

ALTER TABLE `offer_detail` MODIFY `line` smallint(2) DEFAULT '1' COMMENT 'Numero de linea del Detalle dentro del Presupuesto';
ALTER TABLE `income_detail` MODIFY `line` smallint(2) DEFAULT '1' COMMENT 'Numero de linea del Detalle dentro del Albaran';
ALTER TABLE `sales_detail` MODIFY `line` smallint(2) DEFAULT '1' COMMENT 'Numero de linea del Detalle dentro del Pedido';
ALTER TABLE `delivery_detail` MODIFY `line` smallint(2) DEFAULT '1' COMMENT 'Numero de linea del Detalle dentro del Albaran';
ALTER TABLE `invoice_detail` MODIFY `line` smallint(2) DEFAULT '1' COMMENT 'Numero de linea del Detalle dentro de la Factura';
ALTER TABLE `delivery` MODIFY `number` int(4) NOT NULL DEFAULT '0' COMMENT 'Numero del Albaran';


UPDATE `db_version` SET `version_number` = '7.6.1';

COMMIT;
