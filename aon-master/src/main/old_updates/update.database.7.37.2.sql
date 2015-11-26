# Version: Upgrade from version 7.37.1 to 7.37.2
# Created by: aibanez
# Creation Date: 14/07/2014 




ALTER TABLE `contract_attach` ADD COLUMN `driveId` VARCHAR(45) NULL DEFAULT NULL  AFTER `attach_date` ;
ALTER TABLE `iattach` ADD COLUMN `driveId` VARCHAR(45) NULL DEFAULT NULL  AFTER `type` ;
ALTER TABLE `invoice_attach` ADD COLUMN `driveId` VARCHAR(45) NULL DEFAULT NULL  AFTER `attach_date` ;
ALTER TABLE `offer_attach` ADD COLUMN `driveId` VARCHAR(45) NULL DEFAULT NULL  AFTER `data` ;
ALTER TABLE `payroll_batch_attach` ADD COLUMN `driveId` VARCHAR(45) NULL DEFAULT NULL  AFTER `attach_date` ;
ALTER TABLE `project_attach` ADD COLUMN `driveId` VARCHAR(45) NULL DEFAULT NULL  AFTER `attach_date` ;
ALTER TABLE `sepe_batch_attach` ADD COLUMN `driveId` VARCHAR(45) NULL DEFAULT NULL  AFTER `attach_date` ;


UPDATE `db_version` SET `version_number` = '7.37.3';



