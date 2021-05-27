# Database: aon_master
# Version: Actualizacion de la version 9.23.1 a la version 9.23.2
# Created by: eagirrezabal
# Creation Date: 26/02/2017 13:00

BEGIN;

ALTER TABLE `ritem` ADD `edi_sales_code` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo EAN de ventas para EDI' AFTER `code`;

UPDATE `db_version` SET `version_number` = '9.23.2';

COMMIT;
