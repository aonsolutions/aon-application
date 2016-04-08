# Database: aon_master
# Version: Actualizacion de la version 8.47.1 a la version 8.48.0
# Created by: girazu
# Creation Date: 08/04/2016 17:45

BEGIN;

INSERT INTO `tag` (`domain`, `name`, `type`) VALUES (0, "FAQ", 6);


UPDATE `db_version` SET `version_number` = '8.48.0';

COMMIT;
