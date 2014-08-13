# Version: Upgrade from version 7.37.7 to 8.0.0
# Created by: aibanez
# Creation Date: 13/08/2014 


ALTER TABLE `domain_gserviceaccount` ADD COLUMN `size` DOUBLE NULL DEFAULT 0  AFTER `domain` ;

UPDATE `db_version` SET `version_number` = '8.0.1';

