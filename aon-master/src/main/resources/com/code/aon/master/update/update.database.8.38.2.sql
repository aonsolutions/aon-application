# Database: aon_master
# Version: Actualizacion de la version 8.38.2 a la version 8.40.0.
# Created by: girazu
# Creation Date: 22/01/2016 14:30

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

INSERT INTO `tag` (domain, name, type) VALUES (0, 'OPEN', 6);
INSERT INTO `tag` (domain, name, type) VALUES (0, 'REOPEN', 6);
INSERT INTO `tag` (domain, name, type) VALUES (0, 'CLOSED', 6);
INSERT INTO `tag` (domain, name, type) VALUES (0, 'DUPLICATED', 6);

UPDATE `db_version` SET `version_number` = '8.40.0';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
