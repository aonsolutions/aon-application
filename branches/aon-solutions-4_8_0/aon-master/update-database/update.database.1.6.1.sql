# Database: aon_master
# Version: Actualizacion de la version 1.6.1 a la version 1.6.2
# Created by: girazu
# Creation Date: 29/05/2008 12:33
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `course_evaluation` DROP INDEX `course_qualityskill`;


UPDATE `db_version` SET `version_number` = '1.6.2';

COMMIT;
