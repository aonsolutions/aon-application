# Database: aon_master
# Version: Actualizacion de la version 8.17.1 a la version 8.18.0.
# Created by: girazu
# Creation Date: 23/03/2015 12:15

BEGIN;

ALTER TABLE `project_reservation` ADD `early_check_out` tinyint(1) default '0' COMMENT 'Indica si se ha producido una salida anticipada' AFTER `advance_invoiced`;

UPDATE `project_reservation` SET `early_check_out` = 1 WHERE DATE_SUB(`end_date`, INTERVAL 1 DAY) > (
	SELECT MAX(`date`) from `asset_activity`
    WHERE `id` IN (
        SELECT `asset_activity` FROM `project_reservation_room_detail`
        WHERE `project_reservation_room` IN (
            SELECT `id` FROM `project_reservation_room`
            WHERE `project_reservation` = `project_reservation`.`project`
            )
        )
    );

UPDATE `project_reservation` SET `end_time` = DATE_ADD((
	SELECT MAX(`date`) from `asset_activity`
    WHERE `id` IN (
        SELECT `asset_activity` FROM `project_reservation_room_detail`
        WHERE `project_reservation_room` IN (
            SELECT `id` FROM `project_reservation_room`
            WHERE `project_reservation` = `project_reservation`.`project`
            )
        )
    ), INTERVAL CONCAT(EXTRACT(HOUR FROM end_time),':',EXTRACT(MINUTE FROM end_time)) HOUR_MINUTE)
WHERE `early_check_out` = 1;


UPDATE `db_version` SET `version_number` = '8.18.0';

COMMIT;
