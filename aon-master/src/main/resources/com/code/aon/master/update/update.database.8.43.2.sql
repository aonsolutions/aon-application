# Database: aon_master
# Version: Actualizacion de la version 8.43.2 a la version 8.44.0
# Created by: girazu
# Creation Date: 03/03/2016 17:50

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

UPDATE `invoice_detail` SET `discount_expr` = "0.0" WHERE `discount_expr` IS NULL;

INSERT INTO `tag` (`domain`, `name`, `type`) VALUES (0, "MODIFIED", 6);


UPDATE `db_version` SET `version_number` = '8.44.0';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
