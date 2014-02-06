# Database: aon_master
# Version: Actualizacion de la version 7.29.0 a la version 7.29.1.
# Created by: girazu
# Creation Date: 05/02/2014 15:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

UPDATE `project_reservation_room` SET `tariff` = (
	SELECT `id` FROM `tariff` WHERE `code` = (
		SELECT `value` FROM `app_param` WHERE `domain` = `tariff`.`domain` AND `name` = 'PMS_UNDEFINED_TARIFF'))
	WHERE `tariff` IS NULL;

ALTER TABLE `project_reservation_room` MODIFY `tariff` int(4) NOT NULL COMMENT 'Identificador de la Tarifa';

ALTER TABLE `booking` ADD `tariff` int(4) default NULL COMMENT 'Identificador de la Tarifa' AFTER `item`;
UPDATE `booking` SET `tariff` = (
	SELECT `tariff` FROM `project_reservation_room` WHERE `id` = `booking`.`project_reservation_room`);
ALTER TABLE `booking` MODIFY `tariff` int(4) NOT NULL COMMENT 'Identificador de la Tarifa';
ALTER TABLE `booking` ADD KEY `IDX_BOOKING_TARIFF` (`tariff`);
ALTER TABLE `booking` ADD CONSTRAINT `FK_BOOKING_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`);


UPDATE `db_version` SET `version_number` = '7.29.1';

COMMIT;
