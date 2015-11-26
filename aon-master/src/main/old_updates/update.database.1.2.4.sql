# Database: aon_master
# Version: Actualizacion de la version 1.2.4 a la version 1.2.5
# Created by: girazu
# Creation Date: 28/11/2007 10:15
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `series` MODIFY `workplace` int(4) COMMENT 'Centro de Trabajo para el que se define la Serie';

ALTER TABLE `qualification` ADD `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de la Calificacion' AFTER code;


UPDATE `db_version` SET `version_number` = '1.2.5';

COMMIT;
