# Version: Upgrade from version 7.37.4 to 7.37.5
# Created by: rtrepiana
# Creation Date: 14/07/2014 

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;


INSERT INTO `system_deduction` (`domain`, `type`, `deduction_concept`, `description`, `expression`, `start_date`, `end_date` ) 
SELECT -3,  D.`type`, D.`deduction_concept`, D.`description`, D.`expression`, D.`start_date`, D.`end_date`   FROM `system_deduction` AS D INNER JOIN `deduction_concept` AS C  ON ( D.`deduction_concept` = C.`id` ) WHERE C.`type`=6 AND D.`domain` = 0;

UPDATE `db_version` SET `version_number` = '7.37.5';




COMMIT;

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=1;
