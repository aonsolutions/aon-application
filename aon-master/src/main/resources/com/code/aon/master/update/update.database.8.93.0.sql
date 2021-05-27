# Database: aon_master
# Version: Actualizacion de la version 8.93.0 a la version 8.93.1.
# Created by: girazu
# Creation Date: 03/03/2017 12:15

BEGIN;

UPDATE `project_reservation` SET `bank_transaction` = SUBSTRING_INDEX(SUBSTRING_INDEX(`remarks`, 'bank transaction-', -1), '_', 1) 
	WHERE (`bank_transaction` IS NULL OR `bank_transaction` = '') AND `remarks` LIKE '%bank transaction-%';
UPDATE `project_reservation` SET `bank_transaction` = null WHERE `bank_transaction` = '';


UPDATE `db_version` SET `version_number` = '8.93.1';

COMMIT;
