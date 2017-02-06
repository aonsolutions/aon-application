# Database: aon_master
# Version: Actualizacion de la version 8.88.0 a la version 8.88.1.
# Created by: ecastellano
# Creation Date: 06/02/2017 12:50

BEGIN;

ALTER TABLE `fs_model184_detail` ADD `vat_accrual_payment` tinyint(1) DEFAULT '0' COMMENT 'Regimen Especial de Criterio de Caja';
ALTER TABLE `fs_model184_detail` ADD `declared_key` varchar(1) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Clave del declarado';
ALTER TABLE `fs_model184_detail` ADD `nature` varchar(1) NOT NULL DEFAULT '0' COMMENT 'Naturaleza del inmueble';
ALTER TABLE `fs_model184_detail` ADD `asset_percent` double(15,3) DEFAULT 0 COMMENT 'Porc. titularidad inmueble';

UPDATE `db_version` SET `version_number` = '8.88.1';

COMMIT;
