# Database: aon_master
# Version: Actualizacion de la version 8.104.0 a la version 8.105.0.
# Created by: rtrepiana@aonsolutions.es
# Creation Date: 30/05/2017

BEGIN;

ALTER TABLE `salary` ADD `ss_regime` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Regimen de la Seguridad Social' AFTER `ccc`;

UPDATE `salary`, `contract` SET `salary`.`ss_regime` = `contract`.`ss_regime` WHERE `salary`.`contract` = `contract`.`id`;

UPDATE `db_version` SET `version_number` = '8.105.0';

COMMIT;
