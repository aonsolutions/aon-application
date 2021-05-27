# Database: aon_master
# Version: Actualizacion de la version 8.13.4 a la version 8.13.5.
# Created by: ecastellano
# Creation Date: 05/02/2015 

BEGIN;

#
# Columnas nuevas para dar soporte a los cambios en el modelo 347 ejercicio 2014.
#

ALTER TABLE `fs_mod347_detail` ADD `operator_nif` varchar(17) collate latin1_spanish_ci default NULL COMMENT 'NIF del operador intracomunitario';
ALTER TABLE `fs_mod347_detail` ADD `vat_accrual` tinyint(1) default '0' COMMENT 'Regimen de critrio de caja';
ALTER TABLE `fs_mod347_detail` ADD `isp` tinyint(1) default '0' COMMENT 'Inversion de sujeto pasivo';
ALTER TABLE `fs_mod347_detail` ADD `deposit_regime` tinyint(1) default '0' COMMENT 'Regimen de deposito';
ALTER TABLE `fs_mod347_detail` ADD `vat_accrual_amount` double(15,3) default '0.000' COMMENT 'Importe de las operaciones en reg, caja';

UPDATE `db_version` SET `version_number` = '8.13.5';

COMMIT;
