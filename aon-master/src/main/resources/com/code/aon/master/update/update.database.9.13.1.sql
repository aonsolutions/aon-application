# Database: aon_master
# Version: Actualizacion de la version 9.13.1 a la version 9.14.0
# Created by: girazu
# Creation Date: 17/11/2017 13:10

BEGIN;

DELETE FROM `profile_role` 
	WHERE `profile` IN (
		SELECT `id` FROM `profile` WHERE `name` = 'Portal Gestion') 
	AND `application_role` IN (
		SELECT `id` FROM `application_role` WHERE `application` = 28 AND `role` IN (
			SELECT `id` FROM `role` WHERE `name` = 'Accounting'));


UPDATE `db_version` SET `version_number` = '9.14.0';

COMMIT;
