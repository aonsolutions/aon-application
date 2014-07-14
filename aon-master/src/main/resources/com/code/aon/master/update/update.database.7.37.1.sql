# Version: Upgrade from version 7.37.1 to 7.37.2
# Created by: ecastellano
# Creation Date: 10/07/2014 


ALTER TABLE `fs_model200` ADD `result_type` varchar(1) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT '(D) Devolucion, (I) Ingreso, (C) Cuota cero' AFTER `irnr`;
ALTER TABLE `fs_model200` ADD `dev_type` varchar(1) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT '(R) Renuncia, (T) Transferencia' AFTER `result_type`;
ALTER TABLE `fs_model200` ADD `pay_type` varchar(1) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT '(E) Efectivo, (A) Adeudo' AFTER `dev_type`;
ALTER TABLE `fs_model200` ADD `amount` double(15,3) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe' AFTER `pay_type`;
ALTER TABLE `fs_model200` ADD `iban` varchar(34) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'IBAN' AFTER `amount`;

UPDATE `db_version` SET `version_number` = '7.37.2';



