# Database: aon_master
# Version: Actualizacion de la version 8.37.1 a la version 8.37.2.
# Created by: rtrepiana
# Creation Date: 30/12/2015

BEGIN;

INSERT INTO `holiday` (`domain`, `description`, `holiday`, `editable`) (SELECT  `domain`, CONCAT( IFNULL(`description`,''), '(', `id` , ')'), `holiday`, 0  from `calendar` WHERE `id` IN ( SELECT `calendar` FROM `calendar_holiday` ));

INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description` ) (SELECT `domain`, (SELECT `id` FROM `holiday` WHERE description LIKE CONCAT('%(',`calendar`,')')) AS holiday, `date`, `description`  FROM `calendar_holiday` WHERE `day_type` = 2);

UPDATE `calendar` SET `holiday` = (SELECT `id` FROM `holiday` WHERE description LIKE CONCAT('%(',`calendar`.`id`,')')) WHERE `id` IN (SELECT `calendar` FROM `calendar_holiday`);

UPDATE `db_version` SET `version_number` = '8.37.2';

COMMIT;
