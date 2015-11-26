# Database: aon_master
# Version: Actualizacion de la version 5.8.1 a la version 5.8.2.
# Created by: girazu
# Creation Date: 13/01/2011 18:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `bank_statement` ADD `comments` text collate latin1_spanish_ci COMMENT 'Comentarios';


UPDATE `db_version` SET `version_number` = '5.8.2';

COMMIT;
