
# Database: aon_master
# Version: Actualizacion de la version 8.41.0 a la version 8.41.1.
# Created by: rtrepiana

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;


BEGIN;

-- Saturdays & Sundays are both non working days by default.
UPDATE `calendar` SET `saturday` = '1', `sunday` = '1';

ALTER TABLE `calendar`ALTER `saturday` SET DEFAULT '1', ALTER `sunday` SET DEFAULT '1';

UPDATE `db_version` SET `version_number` = '8.42.0';

COMMIT;



SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

