# Database: aon_master
# Version: Actualizacion de la version 7.0.6 a la version 7.0.7.
# Created by: girazu
# Creation Date: 10/02/2012 10:10
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `project_reservation_service_detail` MODIFY `price` double(15,4) default '0.0000' COMMENT 'Precio';
ALTER TABLE `project_reservation_service_detail` MODIFY `taxable_base` double(15,4) default '0.0000' COMMENT 'Base imponible';

ALTER TABLE `project_reservation` ADD `start_time` datetime default NULL COMMENT 'Hora de entrada' AFTER `start_date`;
ALTER TABLE `project_reservation` ADD `end_time` datetime default NULL COMMENT 'Hora de salida' AFTER `end_date`;
ALTER TABLE `project_reservation` ADD `comments` text collate latin1_spanish_ci COMMENT 'Comentarios' AFTER `total`;
ALTER TABLE `project_reservation` ADD `crs_code` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo de la Reserva en el CRS' AFTER `crs`;

UPDATE `project_reservation` set `start_time` = DATE_ADD(`start_date`, INTERVAL 14 HOUR);
UPDATE `project_reservation` set `end_time` = DATE_ADD(`end_date`, INTERVAL 12 HOUR);
ALTER TABLE `project_reservation` MODIFY `start_time` datetime NOT NULL COMMENT 'Hora de entrada';
ALTER TABLE `project_reservation` MODIFY `end_time` datetime NOT NULL COMMENT 'Hora de salida';

ALTER TABLE `proposal_detail` ADD `status` tinyint(2) default '0' COMMENT 'Estado del Detalle de la Propuesta';
ALTER TABLE `proposal_detail` ADD `supplier` int(4) default null COMMENT 'Identificador de Proveedor';
ALTER TABLE `proposal_detail` ADD KEY `IDX_PROPOSAL_DETAIL_SUPPLIER` (`supplier`);
ALTER TABLE `proposal_detail` ADD CONSTRAINT `FK_PROPOSAL_DETAIL_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`);


UPDATE `db_version` SET `version_number` = '7.0.7';

COMMIT;
