# Database: aon_master
# Version: Actualizacion de la version 7.0.4 a la version 7.0.5.
# Created by: girazu
# Creation Date: 26/01/2012 19:40
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `project_reservation_service_detail` ADD `invoice_detail` int(4) default NULL COMMENT 'Identificador de la Linea de Factura';
ALTER TABLE `project_reservation_service_detail` ADD KEY `IDX_PROJECT_RESERVATION_SERVICE_DETAIL_INVOICE_DETAIL` (`invoice_detail`);
ALTER TABLE `project_reservation_service_detail` ADD CONSTRAINT `FK_PROJECT_RESERVATION_SERVICE_DETAIL_INVOICE_DETAIL` FOREIGN KEY (`invoice_detail`) REFERENCES `invoice_detail` (`id`);

ALTER TABLE `invoice_address` MODIFY `geozone` int(4) default NULL COMMENT 'Identificador de la Zona Geografica';
ALTER TABLE `invoice_address` ADD `province` varchar(45) collate latin1_spanish_ci default NULL COMMENT 'Provincia' AFTER `city`;

UPDATE `db_version` SET `version_number` = '7.0.5';

COMMIT;
