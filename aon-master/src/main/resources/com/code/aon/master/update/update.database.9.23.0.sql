# Database: aon_master
# Version: Actualizacion de la version 9.23.0 a la version 9.23.1
# Created by: girazu
# Creation Date: 16/02/2017 14:10

BEGIN;

UPDATE `domain_application_module` SET `module` = 23 WHERE `module` = 19 AND `domain` IN (SELECT `id` FROM `domain` WHERE `maxDefinedUsers` = 0);


UPDATE `db_version` SET `version_number` = '9.23.1';

COMMIT;
