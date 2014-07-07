# Version: Upgrade from version 7.36.4 to 7.36.5
# Created by: aibanez
# Creation Date: 07/07/2014 




ALTER TABLE `commercial_tracking` ADD COLUMN `eventId` VARCHAR(45) NULL  AFTER `location` ;

UPDATE `db_version` SET `version_number` = '7.36.6';



