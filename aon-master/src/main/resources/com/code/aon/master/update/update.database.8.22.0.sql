# Database: aon_master
# Version: Actualizacion de la version 8.22.0 a la version 8.23.0.
# Created by: girazu
# Creation Date: 07/05/2015 12:05

BEGIN;

ALTER TABLE `project_reservation_guest` CHANGE `surname` `surname` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Apellido 1';
ALTER TABLE `project_reservation_guest` ADD `surname2` varchar(64) COLLATE latin1_spanish_ci default NULL COMMENT 'Apellido 2' AFTER `surname`;
ALTER TABLE `project_reservation_guest` ADD `birth_date` date DEFAULT NULL COMMENT 'Fecha de nacimiento' AFTER `document_country`;
ALTER TABLE `project_reservation_guest` ADD `number` varchar(12) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero' AFTER `address`;
ALTER TABLE `project_reservation_guest` ADD `address2` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Segunda parte de la Direccion' AFTER `number`;
ALTER TABLE `project_reservation_guest` CHANGE `country` `country` varchar(3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Pais';
ALTER TABLE `project_reservation_guest` ADD `person` int(4) default NULL COMMENT 'Identificador de la Persona';
ALTER TABLE `project_reservation_guest` ADD KEY `IDX_PROJECT_RESERVATION_GUEST_PERSON` (`person`);
ALTER TABLE `project_reservation_guest` ADD CONSTRAINT `FK_PROJECT_RESERVATION_GUEST_PERSON` FOREIGN KEY (`person`) REFERENCES `person` (`registry`);

UPDATE `product` SET `status` = 0 WHERE `status` IS NULL;


UPDATE `db_version` SET `version_number` = '8.23.0';

COMMIT;
