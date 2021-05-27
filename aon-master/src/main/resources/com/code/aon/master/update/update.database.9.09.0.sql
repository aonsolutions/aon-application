# Database: aon_master
# Version: Actualizacion de la version 9.09.0 a la version 9.10.0
# Created by: girazu
# Creation Date: 20/10/2017 13:50

BEGIN;

DELETE FROM `profile_role` 
	WHERE `profile` IN (SELECT `id` FROM `profile` WHERE `domain` IS NULL AND `application` = 28 AND `name` = 'Portal Laboral') 
	AND `application_role` IN (SELECT `id` FROM `application_role` WHERE `application` = 28 AND `role` = 2);

INSERT INTO `profile` (`domain`, `name`, `application`) VALUES (null, 'Portal Documental', 28);
INSERT INTO `profile_role` (`domain`, `profile`, `application_role`) SELECT `domain`, `id`, 233 FROM `profile` WHERE `domain` IS NULL AND `application` = 28 AND `name` = 'Portal Documental';
INSERT INTO `profile_module_denied` (`domain`, `profile`, `module`) SELECT `domain`, `id`, 18 FROM `profile` WHERE `domain` IS NULL AND `application` = 28 AND `name` = 'Portal Documental';

INSERT INTO `profile` (`domain`, `name`, `application`) VALUES (null, 'Portal Gestion', 28);
INSERT INTO `profile_role` (`domain`, `profile`, `application_role`) SELECT `domain`, `id`, 220 FROM `profile` WHERE `domain` IS NULL AND `application` = 28 AND `name` = 'Portal Gestion';
INSERT INTO `profile_role` (`domain`, `profile`, `application_role`) SELECT `domain`, `id`, 223 FROM `profile` WHERE `domain` IS NULL AND `application` = 28 AND `name` = 'Portal Gestion';
INSERT INTO `profile_role` (`domain`, `profile`, `application_role`) SELECT `domain`, `id`, 225 FROM `profile` WHERE `domain` IS NULL AND `application` = 28 AND `name` = 'Portal Gestion';
INSERT INTO `profile_role` (`domain`, `profile`, `application_role`) SELECT `domain`, `id`, 226 FROM `profile` WHERE `domain` IS NULL AND `application` = 28 AND `name` = 'Portal Gestion';
INSERT INTO `profile_role` (`domain`, `profile`, `application_role`) SELECT `domain`, `id`, 229 FROM `profile` WHERE `domain` IS NULL AND `application` = 28 AND `name` = 'Portal Gestion';
INSERT INTO `profile_role` (`domain`, `profile`, `application_role`) SELECT `domain`, `id`, 230 FROM `profile` WHERE `domain` IS NULL AND `application` = 28 AND `name` = 'Portal Gestion';
INSERT INTO `profile_role` (`domain`, `profile`, `application_role`) SELECT `domain`, `id`, 232 FROM `profile` WHERE `domain` IS NULL AND `application` = 28 AND `name` = 'Portal Gestion';
INSERT INTO `profile_module_denied` (`domain`, `profile`, `module`) SELECT `domain`, `id`, 18 FROM `profile` WHERE `domain` IS NULL AND `application` = 28 AND `name` = 'Portal Gestion';


UPDATE `db_version` SET `version_number` = '9.10.0';

COMMIT;
