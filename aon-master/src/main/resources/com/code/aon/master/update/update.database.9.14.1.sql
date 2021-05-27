# Database: aon_master
# Version: Actualizacion de la version 9.14.1 a la version 9.15.0
# Created by: ecastellano
# Creation Date: 13/12/2017 10:30

BEGIN;

SET FOREIGN_KEY_CHECKS=0;

UPDATE `contract` SET `category_description` = ( SELECT `description` FROM `agreement_level_category` WHERE `id` = `contract`.`agreement_level_category` )  WHERE IFNULL(`category_description`, '') = '';
ALTER TABLE contract ADD COLUMN agreement_level INT(4) DEFAULT NULL, ADD FOREIGN KEY `FK_CONTRACT_AGREEMEN_LEVEL`(agreement_level) REFERENCES agreement_level(id);
UPDATE `contract` SET `agreement_level` = ( SELECT `agreement_level` FROM `agreement_level_category` WHERE `id` = `contract`.`agreement_level_category` )  WHERE IFNULL(`agreement_level`, '') = '';
ALTER TABLE contract DROP FOREIGN KEY `FK_CONTRACT_AGREEMENT_LEVEL_CATEGORY`;
ALTER TABLE contract DROP COLUMN agreement_level_category;

SET FOREIGN_KEY_CHECKS=1;

UPDATE `db_version` SET `version_number` = '9.15.0';

COMMIT;
