# Database: aon_master
# Version: Actualizacion de la version 8.16.2 a la version 8.16.3.
# Created by: aibanez
# Creation Date: 25/02/2015 

BEGIN;


ALTER TABLE `domain_gserviceaccount` ADD COLUMN `google_account` VARCHAR(45) NULL  AFTER `limit` ;


UPDATE `db_version` SET `version_number` = '8.16.3';

COMMIT;



