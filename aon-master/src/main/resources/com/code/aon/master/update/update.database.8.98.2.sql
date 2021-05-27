# Database: aon_master
# Version: Actualizacion de la version 8.98.2 a la version 8.99.0.
# Created by: girazu
# Creation Date: 27/04/2017 14:50

BEGIN;

ALTER TABLE `data_response` CHANGE `number` `code` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo de referencia';
ALTER TABLE `data_response` CHANGE `issue_date` `response_date` date DEFAULT NULL COMMENT 'Fecha';
ALTER TABLE `data_response` ADD `source` tinyint(2) DEFAULT '0' COMMENT 'Origen' AFTER `response_date`;
ALTER TABLE `data_response` ADD `source_id` int(4) DEFAULT NULL COMMENT 'Identificador del Origen' AFTER `source`;

ALTER TABLE `data_response_detail` CHANGE `value` `data_value` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Valor de la variable';


UPDATE `db_version` SET `version_number` = '8.99.0';

COMMIT;
