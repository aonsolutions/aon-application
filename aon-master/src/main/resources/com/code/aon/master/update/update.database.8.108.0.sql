# Database: aon_master
# Version: Actualizacion de la version 8.108.0 a la version 8.109.0.
# Created by: girazu
# Creation Date: 15/06/2017 13:25

BEGIN;

ALTER TABLE `project_reservation_service` ADD `removed` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Servicio borrado' AFTER `extra`;

ALTER TABLE `project_reservation_service_detail` ADD `taxable_base_production` double(15,4) DEFAULT '0.0000' COMMENT 'Base imponible de Produccion';
ALTER TABLE `project_reservation_service_detail` ADD `total_production` double(15,4) DEFAULT '0.0000' COMMENT 'Total Produccion acumulado';


UPDATE `db_version` SET `version_number` = '8.109.0';

COMMIT;
