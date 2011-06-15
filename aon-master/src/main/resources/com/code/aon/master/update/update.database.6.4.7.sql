# Database: aon_master
# Version: Actualizacion de la version 6.4.7 a la version 6.4.8.
# Created by: girazu
# Creation Date: 15/06/2011 15:48
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

DROP TABLE `expense_account_detail`;

DROP TABLE `expense_account`;

DROP TABLE `expense`;

ALTER TABLE `item_composition` ADD `sequence` smallint(2) default '0' COMMENT 'Numero de secuencia dentro de la Composicion' AFTER `composition_item`;


UPDATE `db_version` SET `version_number` = '6.4.8';

COMMIT;
