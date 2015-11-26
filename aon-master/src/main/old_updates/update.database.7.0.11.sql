# Database: aon_master
# Version: Actualizacion de la version 7.0.11 a la version 7.0.12.
# Created by: girazu
# Creation Date: 12/04/2012 09:40
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `reservation_request` MODIFY `request_counter` smallint(6) NOT NULL COMMENT 'Numero de solicitudes enviadas';
ALTER TABLE `reservation_request` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `reservation_request` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `reservation_request` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `reservation_request` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `reservation_request_guest` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `reservation_request_guest` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `reservation_request_guest` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `reservation_request_guest` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `reservation_request_room` DROP FOREIGN KEY `FK_RESERVATION_REQUEST_ROOM_TARIFF`;
ALTER TABLE `reservation_request_room` DROP KEY `IDX_RESERVATION_REQUEST_ROOM_TARIFF`;
ALTER TABLE `reservation_request_room` DROP `tariff`;
ALTER TABLE `reservation_request_room` ADD `crs_code` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo de Reserva en CRS';
ALTER TABLE `reservation_request_room` ADD `tariff_code` varchar(8) collate latin1_spanish_ci default NULL COMMENT 'Codigo de Tarifa';
ALTER TABLE `reservation_request_room` ADD `tariff_description` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de Tarifa';
ALTER TABLE `reservation_request_room` ADD `inventory_code` varchar(15) collate latin1_spanish_ci default NULL COMMENT 'Codigo de Servicio';
ALTER TABLE `reservation_request_room` ADD `room_code` varchar(15) collate latin1_spanish_ci default NULL COMMENT 'Codigo de Habitacion';
ALTER TABLE `reservation_request_room` ADD `room_description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de Habitacion';
ALTER TABLE `reservation_request_room` ADD `meal_plan` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Tipo de regimen';
ALTER TABLE `reservation_request_room` ADD `daily_price` double(15,2) default '0.00' COMMENT 'Importe Diario';
ALTER TABLE `reservation_request_room` ADD `total_price` double(15,2) default '0.00' COMMENT 'Importe Total';
ALTER TABLE `reservation_request_room` ADD `cancel_penalty` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Penalizaciones por cancelacion';
ALTER TABLE `reservation_request_room` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `reservation_request_room` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `reservation_request_room` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `reservation_request_room` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `project_reservation_divert` ADD `request_user` int(4) default NULL COMMENT 'Identificador del Usuario solicitante';
ALTER TABLE `project_reservation_divert` ADD KEY `IDX_PROJECT_RESERVATION_DIVERT_REQUEST_USER` (`request_user`);
ALTER TABLE `project_reservation_divert` ADD CONSTRAINT `FK_PROJECT_RESERVATION_DIVERT_REQUEST_USER` FOREIGN KEY (`request_user`) REFERENCES `user` (`id`);
ALTER TABLE `project_reservation_divert` ADD `response_user` int(4) default NULL COMMENT 'Identificador del Usuario de respuesta';
ALTER TABLE `project_reservation_divert` ADD KEY `IDX_PROJECT_RESERVATION_DIVERT_RESPONSE_USER` (`response_user`);
ALTER TABLE `project_reservation_divert` ADD CONSTRAINT `FK_PROJECT_RESERVATION_DIVERT_RESPONSE_USER` FOREIGN KEY (`response_user`) REFERENCES `user` (`id`);

ALTER TABLE `mail_account` DROP `source`;
ALTER TABLE `mail_account` DROP `source_id`;
ALTER TABLE `mail_account` ADD `user_id` int(4) default NULL COMMENT 'Identificador del Usuario';
ALTER TABLE `mail_account` ADD KEY `IDX_MAIL_ACCOUNT_USER` (`user_id`);
ALTER TABLE `mail_account` ADD CONSTRAINT `FK_MAIL_ACCOUNT_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);  

ALTER TABLE `signature` DROP `source`;
ALTER TABLE `signature` DROP `source_id`;
ALTER TABLE `signature` ADD `user_id` int(4) default NULL COMMENT 'Identificador del Usuario';
ALTER TABLE `signature` ADD KEY `IDX_SIGNATURE_USER` (`user_id`);
ALTER TABLE `signature` ADD CONSTRAINT `FK_SIGNATURE_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);  


UPDATE `db_version` SET `version_number` = '7.0.12';

COMMIT;
