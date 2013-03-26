# Database: aon_master
# Version: Actualizacion de la version 6.5.0 a la version 6.6.0.
# Created by: girazu
# Creation Date: 27/06/2011 13:02
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `registry` ADD `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad';


UPDATE `db_version` SET `version_number` = '6.6.0';

COMMIT;
