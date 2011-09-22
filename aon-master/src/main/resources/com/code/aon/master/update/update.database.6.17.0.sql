# Database: aon_master
# Version: Actualizacion de la version 6.17.0 a la version 6.17.1.
# Created by: girazu
# Creation Date: 21/09/2011 17:50
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `invoice` ADD `remarks` text collate latin1_spanish_ci COMMENT 'Observaciones de la Factura' AFTER `comments`;

ALTER TABLE `delivery` ADD `comments` text collate latin1_spanish_ci COMMENT 'Comentarios del Albaran' AFTER `status`;

ALTER TABLE `delivery` ADD `remarks` text collate latin1_spanish_ci COMMENT 'Observaciones del Albaran' AFTER `comments`;

ALTER TABLE `income` ADD `comments` text collate latin1_spanish_ci COMMENT 'Comentarios del Albaran' AFTER `status`;

ALTER TABLE `income` ADD `remarks` text collate latin1_spanish_ci COMMENT 'Observaciones del Albaran' AFTER `comments`;

ALTER TABLE `sales` ADD `comments` text collate latin1_spanish_ci COMMENT 'Comentarios del Pedido' AFTER `status`;

ALTER TABLE `sales` ADD `remarks` text collate latin1_spanish_ci COMMENT 'Observaciones del Pedido' AFTER `comments`;

ALTER TABLE `purchase` ADD `comments` text collate latin1_spanish_ci COMMENT 'Comentarios del Pedido' AFTER `status`;

ALTER TABLE `purchase` ADD `remarks` text collate latin1_spanish_ci COMMENT 'Observaciones del Pedido' AFTER `comments`;

ALTER TABLE `offer` ADD `remarks` text collate latin1_spanish_ci COMMENT 'Observaciones del Presupuesto' AFTER `comments`;


UPDATE `db_version` SET `version_number` = '6.17.1';

COMMIT;
