# Database: aon_master
# Version: Actualizacion de la version 1.2.1 a la version 1.2.2
# Created by: girazu
# Creation Date: 18/10/2007 10:01
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `alarm` MODIFY `description` text collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Alarma';


UPDATE `db_version` SET `version_number` = '1.2.2';

COMMIT;
