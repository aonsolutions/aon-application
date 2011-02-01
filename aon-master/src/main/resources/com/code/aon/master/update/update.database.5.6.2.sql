# Database: aon_master
# Version: Actualizacion de la version 5.6.2 a la version 5.7.0.
# Created by: girazu
# Creation Date: 25/11/2010 12:53
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `bank_statement` ADD `reliability` tinyint(2) default '0' COMMENT 'Fiabilidad del punteo' AFTER `description`;

ALTER TABLE `bank_statement_link` DROP `reliability`;


UPDATE `db_version` SET `version_number` = '5.7.0';

COMMIT;
