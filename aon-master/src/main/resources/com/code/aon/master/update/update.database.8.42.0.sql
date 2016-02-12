# Database: aon_master
# Version: Actualizacion de la version 8.41.2 a la version 8.42.0.
# Created by: girazu
# Creation Date: 02/02/2016 13:25

BEGIN;

ALTER TABLE `invoice_tax` MODIFY `deductible_percent` double DEFAULT '0' COMMENT 'Porcentaje de deducibilidad';
UPDATE `invoice_tax` SET `deductible_percent` = 100, `deductible_quota` = `quota` WHERE `tax_type` = 1;
UPDATE `invoice_tax` SET `deductible_percent` = 0, `deductible_quota` = 0 WHERE `tax_type` != 1;

ALTER TABLE notice_tag ADD `user` int(4) default NULL COMMENT 'Identificador del Usuario';
ALTER TABLE notice_tag ADD KEY `IDX_NOTICE_TAG_USER` (`user`);
ALTER TABLE notice_tag ADD CONSTRAINT `FK_NOTICE_TAG_USER` FOREIGN KEY (`user`) REFERENCES `user` (`id`);

UPDATE `system_data` SET `expression` = '(GRUPO_COTIZACION == \"01\") ? (23.60 - REDUCCION_CGC_E_01) : (17.75 - REDUCCION_CGC_E_02)' 
	WHERE `domain` = '-107' AND `name` = 'PORCENTAJE_CGC_E' 
	AND `start_date` = '2016-01-01' AND `end_date` IS NULL;


UPDATE `db_version` SET `version_number` = '8.42.0';

COMMIT;
