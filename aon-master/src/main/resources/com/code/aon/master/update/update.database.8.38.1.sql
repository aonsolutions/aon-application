# Database: aon_master
# Version: Actualizacion de la version 8.38.1 a la version 8.38.2.
# Created by: rtrepiana
# Creation Date: 12/01/2015

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

UPDATE `system_cost` SET `expression`=CONCAT(' NOMINA ? ( ', `expression`, ' ) : REMOVE()')  WHERE  `domain`=0 AND `type`=8;

UPDATE `db_version` SET `version_number` = '8.38.2';


COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
