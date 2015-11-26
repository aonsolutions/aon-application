# Database: aon_master
# Version: Actualizacion de la version 7.25.1 a la version 7.25.2.
# Created by: girazu
# Creation Date: 02/12/2013 18:35
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `profile_module_denied` MODIFY `domain` int(4) default NULL COMMENT 'Identificador del Dominio';

INSERT INTO `profile` (`name`,`application`) VALUES ('Portal Laboral', 28);

INSERT IGNORE INTO `application_role` (`id`, `application`, `role`) VALUES (218, 28, 2);

SET @PROFILE_ID = (SELECT LAST_INSERT_ID());

INSERT INTO `profile_role` (`profile`,`application_role`) VALUES (@PROFILE_ID, 218);
INSERT INTO `profile_role` (`profile`,`application_role`) VALUES (@PROFILE_ID, 234);

INSERT INTO `profile_module_denied` (`profile`,`module`) VALUES (@PROFILE_ID, 18); 


UPDATE `db_version` SET `version_number` = '7.25.2';

COMMIT;
