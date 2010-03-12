# Database: aon_master
# Version: Actualizacion de la version 4.9.0 a la version 5.0.0.
# Created by: girazu
# Creation Date: 11/03/2010 16:59
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `session` MODIFY `session_id` varchar(128) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Identificador web de la sesión';

ALTER TABLE `customer_fee` ADD `line` smallint(2) default '1' COMMENT 'Numero de linea de Cuota' AFTER `customer`;

ALTER TABLE `customer_fee` MODIFY `description` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de la Cuota';

ALTER TABLE `offer_term` ADD `line` smallint(2) default '1' COMMENT 'Numero de linea de la Condicion del Presupuesto' AFTER `offer`;

UPDATE `product` SET `type` = 2 WHERE `type` IS NULL;

UPDATE `finance` SET `registry` = (SELECT `registry` FROM `invoice` WHERE `invoice`.`id` = `finance`.`invoice`) WHERE `registry` <> (SELECT `registry` FROM `invoice` WHERE `invoice`.`id` = `finance`.`invoice`);


UPDATE `db_version` SET `version_number` = '5.0.0';

COMMIT;
