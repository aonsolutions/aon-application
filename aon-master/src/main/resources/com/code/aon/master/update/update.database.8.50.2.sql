
# Database: aon_master
# Version: Actualizacion de la version 8.50.2 a la version 8.50.3.
# Created by: rtrepiana

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

ALTER TABLE `contract_payment` MODIFY `expression` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe';

ALTER TABLE `agreement_payment` MODIFY `expression` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe';

UPDATE `db_version` SET `version_number` = '8.50.3';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
