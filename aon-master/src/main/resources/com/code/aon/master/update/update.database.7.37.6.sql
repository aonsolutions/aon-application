# Version: Upgrade from version 7.37.6 to 7.37.7
# Created by: rtrepiana
# Creation Date: 14/07/2014 

ALTER TABLE `fs_model200` ADD `phone1` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono 1' AFTER `name`;
ALTER TABLE `fs_model200` ADD `phone2` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono 2' AFTER `phone1`;

UPDATE `db_version` SET `version_number` = '7.37.7';

