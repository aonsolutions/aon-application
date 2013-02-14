# Database: aon_master
# Version: Actualizacion de la version 7.0.5 a la version 7.0.6.
# Created by: girazu
# Creation Date: 30/01/2012 17:20
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `hotel` ADD `service_catalogue` int(4) default NULL COMMENT 'Identificador del Catalogo de Servicios' AFTER `customer`;
ALTER TABLE `hotel` ADD KEY `IDX_HOTEL_SERVICE_CATALOGUE` (`service_catalogue`);
ALTER TABLE `hotel` ADD CONSTRAINT `FK_HOTEL_SERVICE_CATALOGUE` FOREIGN KEY (`service_catalogue`) REFERENCES `catalogue` (`id`);

ALTER TABLE `project_reservation_guest` ADD `document` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Numero de documento de identificacion' AFTER `treatment`;
ALTER TABLE `project_reservation_guest` ADD `document_type` tinyint(2) default '0' COMMENT 'Tipo de documento' AFTER `document`; 
ALTER TABLE `project_reservation_guest` ADD `document_country` varchar(2) collate latin1_spanish_ci default 'ES' COMMENT 'Pais del documento' AFTER `document_type`;

ALTER TABLE `project_reservation_service` ADD `extra` tinyint(1) NOT NULL default '0' COMMENT 'Indica si se trata de un Servicio extra';


UPDATE `db_version` SET `version_number` = '7.0.6';

COMMIT;
