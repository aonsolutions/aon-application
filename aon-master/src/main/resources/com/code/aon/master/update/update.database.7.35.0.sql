# Version: Upgrade from version 7.35.0 to 7.36.0.
# Created by: rtrepiana@esferalia.com
# Creation Date: 02/06/2014 

SET FOREIGN_KEY_CHECKS=0;


CREATE TABLE `domain_gserviceaccount` (
  `client_id` varchar(100) COLLATE latin1_spanish_ci NOT NULL,
  `email_address` varchar(100) COLLATE latin1_spanish_ci DEFAULT NULL,
  `public_key` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL,
  `private_key` mediumblob,
  `client_secret` mediumblob,
  `domain` int(4) DEFAULT NULL,
  PRIMARY KEY (`client_id`),
  KEY `FK_DOMAIN_GSERVICEACCOUNT_DOMAIN_IDX` (`domain`),
  CONSTRAINT `FK_DOMAIN_GSERVICEACCOUNT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci;


ALTER TABLE `rattach` ADD COLUMN `drive_id` VARCHAR(45) NULL  AFTER `attach_date` , ADD COLUMN `dparent_id` VARCHAR(45) NULL  AFTER `drive_id` ;

UPDATE `db_version` SET `version_number` = '7.36.0';

SET FOREIGN_KEY_CHECKS=1;
