# Database: aon_master
# Version: Actualizacion de la version 5.2.2 a la version 5.3.0.
# Created by: girazu
# Creation Date: 09/06/2010 10:04
#



BEGIN;

ALTER TABLE `tax` ADD `vat_deduction_type` tinyint(2) default '0' COMMENT 'Tipo de deduccion del IVA';
ALTER TABLE `tax` ADD `withholding_type` tinyint(2) default '0' COMMENT 'Tipo de retencion';

ALTER TABLE `invoice_tax` ADD `vat_deduction_type` tinyint(2) default '0' COMMENT 'Tipo de deduccion del IVA';
ALTER TABLE `invoice_tax` ADD `withholding_type` tinyint(2) default '0' COMMENT 'Tipo de retencion';
ALTER TABLE `invoice_tax` ADD `deductible_quota` double default '0' COMMENT 'Cuota deducible';


UPDATE `db_version` SET `version_number` = '5.3.0';

COMMIT;
