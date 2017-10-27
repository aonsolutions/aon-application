# Database: aon_master
# Version: Actualizacion de la version 9.11.0 a la version 9.11.1
# Created by: girazu
# Creation Date: 27/10/2017 14:30

BEGIN;

INSERT INTO `profile_role` (`domain`, `profile`, `application_role`) SELECT `domain`, `id`, 228 FROM `profile` WHERE `domain` IS NULL AND `application` = 28 AND `name` = 'Portal Gestion';
INSERT INTO `profile_role` (`domain`, `profile`, `application_role`) SELECT `domain`, `id`, 235 FROM `profile` WHERE `domain` IS NULL AND `application` = 28 AND `name` = 'Portal Gestion';


UPDATE `db_version` SET `version_number` = '9.11.1';

COMMIT;
