# Version: Upgrade from version 7.36.0 to 7.36.1.
# Created by: ecastellano@esferalia.com
# Creation Date: 13/06/2014 

ALTER TABLE `task` ADD COLUMN `gtask_id` VARCHAR(100) NULL DEFAULT NULL  AFTER `repeat_period` , ADD COLUMN `gtasklist_id` VARCHAR(100) NULL  AFTER `gtask_id` ;

UPDATE `db_version` SET `version_number` = '7.36.2';

