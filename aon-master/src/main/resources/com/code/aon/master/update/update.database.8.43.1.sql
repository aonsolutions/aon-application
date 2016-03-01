
# Database: aon_master
# Version: Actualizacion de la version 8.43.1 a la version 8.43.2.
# Created by: girazu
# Creation Date: 22/01/2016 14:30

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

UPDATE `bonus_concept` SET expression=REPLACE(expression,'/read-only/','/*read-only*/');

DELETE FROM `system_deduction` WHERE `domain` = -101 AND `deduction_concept` IS NULL ;

INSERT INTO `system_data` (`domain`,`name`,`expression`,`start_date`) VALUES ( -101, 'DIAS_MES', '30' , '2010-01-01' );

DELETE FROM `system_deduction` WHERE `domain` = -105 AND `deduction_concept` IS NULL ;

INSERT INTO `system_data` (`domain`,`name`,`expression`,`start_date`) VALUES ( -105, 'DIAS_MES', '30' , '2010-01-01' );

UPDATE `db_version` SET `version_number` = '8.43.2';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
