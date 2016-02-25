# Database: aon_master
# Version: Actualizacion de la version 8.43.0 a la version 8.43.1.
# Created by: girazu
# Creation Date: 25/02/2016 12:35

BEGIN;

UPDATE `invoice_tax` SET `deductible_quota` = ROUND(`deductible_quota`, 2) 
	WHERE `deductible_quota` != 0 AND `deductible_quota` != ROUND(`deductible_quota`, 2) AND `quota` = ROUND(`quota`, 2);


UPDATE `db_version` SET `version_number` = '8.43.1';

COMMIT;
