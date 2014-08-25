# Version: Upgrade from version 8.0.0 to 8.0.1
# Created by: aibanez
# Creation Date: 25/08/2014 


ALTER TABLE `domain_gserviceaccount` ADD COLUMN `limit` DOUBLE NULL DEFAULT 0  AFTER `size` ;

UPDATE `db_version` SET `version_number` = '8.0.2';

