# Database: aon_master
# Version: Actualizacion de la version 7.7.0 a la version 7.7.1.
# Created by: girazu
# Creation Date: 08/01/2013 10:40
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `bank_statement` MODIFY `document` varchar(10) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de documento';


UPDATE `db_version` SET `version_number` = '7.7.1';

COMMIT;
