# Database: aon_master
# Version: Actualizacion de la version 9.04.1 a la version 9.09.0.
# Created by: girazu
# Creation Date: 10/10/2017 18:20

BEGIN;

INSERT INTO `domain_application_module` (`domain`, `domain_application`, `module`) 
	SELECT `domain`, `domain_application`, 1 FROM `domain_application_module` AS `dam` WHERE `module` = 0 AND 1 NOT IN (
		SELECT `module` FROM `domain_application_module` WHERE `domain` = `dam`.`domain`);
DELETE FROM `domain_application_module` WHERE `module` = 0;


UPDATE `db_version` SET `version_number` = '9.09.0';

COMMIT;
