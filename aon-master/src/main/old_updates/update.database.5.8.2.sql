# Database: aon_master
# Version: Actualizacion de la version 5.8.2 a la version 5.9.0.
# Created by: girazu
# Creation Date: 28/01/2011 16:00
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `rattach` ADD `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Archivo Adjunto';

ALTER TABLE `rattach` ADD `attach_date` date default NULL COMMENT 'Fecha del Archivo Adjunto';


UPDATE `db_version` SET `version_number` = '5.9.0';

COMMIT;
