# Database: aon_master
# Version: Actualizacion de la version 7.10.4 a la version 7.10.5.
# Created by: girazu
# Creation Date: 13/02/2013 16:00
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `invoice` ADD `pos` int(4) DEFAULT NULL COMMENT 'Identificador del TPV' AFTER `advance`;
ALTER TABLE `invoice` ADD KEY `IDX_INVOICE_POS` (`pos`);
ALTER TABLE `invoice` ADD CONSTRAINT `FK_INVOICE_POS` FOREIGN KEY (`pos`) REFERENCES `pos` (`id`);

ALTER TABLE `pcategory` DROP `detail_pattern`;

ALTER TABLE `pcategory` ADD `detail` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre del detalle de los Articulos';
ALTER TABLE `pcategory` ADD `detail2` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre del detalle 2 de los Articulos';

ALTER TABLE `item` MODIFY `detail` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Detalle del Articulo';
ALTER TABLE `item` ADD `detail2` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Detalle 2 del Articulo' AFTER `detail`;


UPDATE `db_version` SET `version_number` = '7.10.5';

COMMIT;
