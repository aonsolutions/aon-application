# Database: aon_master
# Version: Actualizacion de la version 5.8.2 a la version 5.9.0.
# Created by: girazu
# Creation Date: 19/01/2011 17:57
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `income` ADD `reference_code` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Codigo de referencia del Albaran' AFTER `id`;

UPDATE `income` SET `reference_code` = '';

UPDATE `income` SET `reference_code` = CONCAT(`series`, '/') WHERE `series` IS NOT NULL AND `series` != '';

UPDATE `income` SET `reference_code` = CONCAT(`reference_code`, `number`);

ALTER TABLE `income` DROP `series`;

ALTER TABLE `income` DROP `number`;

ALTER TABLE `purchase` ADD `reference_code` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Codigo de referencia del Pedido' AFTER `id`;

UPDATE `purchase` SET `reference_code` = '';

UPDATE `purchase` SET `reference_code` = CONCAT(`series`, '/') WHERE `series` IS NOT NULL AND `series` != '';

UPDATE `purchase` SET `reference_code` = CONCAT(`reference_code`, `number`);

ALTER TABLE `purchase` DROP `series`;

ALTER TABLE `purchase` DROP `number`;


UPDATE `db_version` SET `version_number` = '5.9.0';

COMMIT;
