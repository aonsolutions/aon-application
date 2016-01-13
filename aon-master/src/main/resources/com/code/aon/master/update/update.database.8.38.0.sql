# Database: aon_master
# Version: Actualizacion de la version 8.38.0 a la version 8.38.1.
# Created by: rtrepiana
# Creation Date: 12/01/2015

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

UPDATE `geozone_irpf` SET `end_date` = '2016-12-31' WHERE end_date = '2015-12-31';

UPDATE `db_version` SET `version_number` = '8.38.1';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
