# Database: aon_master
# Version: Actualizacion de la version 1.10.2 a la version 1.10.3
# Created by: girazu
# Creation Date: 11/11/2008 13:02
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `observation` MODIFY `description` varchar(256) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Observacion';


UPDATE `db_version` SET `version_number` = '1.10.3';

COMMIT;
