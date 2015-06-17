# Database: aon_master
# Version: Actualizacion de la version 8.25.0 a la version 8.25.1.
# Created by: girazu
# Creation Date: 16/06/2015 18:05

BEGIN;

ALTER TABLE `warehouse` ADD `department` int(4) default NULL COMMENT 'Identificador del Departamento';
ALTER TABLE `warehouse` ADD KEY `IDX_WAREHOUSE_DEPARTMENT` (`department`);
ALTER TABLE `warehouse` ADD CONSTRAINT `FK_WAREHOUSE_DEPARTMENT` FOREIGN KEY (`department`) REFERENCES `department` (`id`);
ALTER TABLE `warehouse` ADD `active` tinyint(1) NOT NULL default '1' COMMENT 'Indica si el Almacen esta activo o no';

ALTER TABLE `workplace_department` ADD `warehouse` int(4) default NULL COMMENT 'Identificador del Almacen' AFTER `department`;
ALTER TABLE `workplace_department` ADD KEY `IDX_WORKPLACE_DEPARTMENT_WAREHOUSE` (`warehouse`);
ALTER TABLE `workplace_department` ADD CONSTRAINT `FK_WORKPLACE_DEPARTMENT_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`);

ALTER TABLE `proposal` ADD `warehouse` int(4) default NULL COMMENT 'Identificador del Almacen' AFTER `workplace`;
ALTER TABLE `proposal` ADD KEY `IDX_PROPOSAL_WAREHOUSE` (`warehouse`);
ALTER TABLE `proposal` ADD CONSTRAINT `FK_PROPOSAL_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`);

ALTER TABLE `purchase` ADD `warehouse` int(4) default NULL COMMENT 'Identificador del Almacen' AFTER `workplace`;
ALTER TABLE `purchase` ADD KEY `IDX_PURCHASE_WAREHOUSE` (`warehouse`);
ALTER TABLE `purchase` ADD CONSTRAINT `FK_PURCHASE_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`);

ALTER TABLE `project_reservation_service` MODIFY `meal_plan` varchar(3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Regimen';
UPDATE `project_reservation_service` SET `meal_plan` = NULL;
UPDATE `project_reservation_service` SET `meal_plan` = (
	SELECT `detail` FROM `item` 
	WHERE `item`.`id` = `project_reservation_service`.`item`
    AND `item`.detail IN ('SA','AD','MP','PC','TI')
);

UPDATE `project_reservation_service` SET `project_reservation_room` = NULL WHERE `project_reservation_room` NOT IN (
	SELECT `id` FROM `project_reservation_room` 
);
UPDATE `project_reservation_service` SET `project_reservation_room` = (
	SELECT MIN(`id`) FROM `project_reservation_room`
	WHERE `project_reservation` = `project_reservation_service`.`project_reservation`
    )
WHERE `project_reservation_room` IS NULL
AND 1 = (
	SELECT COUNT(*) FROM `project_reservation_room` 
	WHERE `project_reservation` = `project_reservation_service`.`project_reservation`
);


UPDATE `db_version` SET `version_number` = '8.25.1';

COMMIT;
