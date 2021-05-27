# Database: aon_master
# Version: Actualizacion de la version 7.17.1 a la version 7.17.2.
# Created by: girazu
# Creation Date: 29/05/2013 13:10
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `news` MODIFY `description` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion de la Noticia'; 


UPDATE `db_version` SET `version_number` = '7.17.2';

COMMIT;
