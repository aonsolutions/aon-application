# Database: aon_master
# Version: Actualizacion de la version 8.12.0 a la version 8.12.1.
# Created by: girazu
# Creation Date: 09/01/2015 12:45

BEGIN;

INSERT INTO `tax_detail` (`domain`, `tax`, `start_date`, `end_date`, `value`, `surcharge`)
	SELECT `domain`, `id`, `start_date`, '2014-12-31', `percentage`, 0
    	FROM `tax`
	    WHERE `tax_type` = 2
    	AND `withholding_type` = 0
	    AND `percentage` = 21
	    AND `start_date` < '2015-01-01';

UPDATE `tax` SET `percentage` = 19, `start_date` = '2015-01-01'
    WHERE `tax_type` = 2
    AND `withholding_type` = 0
    AND `percentage` = 21
    AND `start_date` < '2015-01-01';

INSERT INTO `tax_detail` (`domain`, `tax`, `start_date`, `end_date`, `value`, `surcharge`)
	SELECT `domain`, `id`, `start_date`, '2014-12-31', `percentage`, 0
    	FROM `tax`
	    WHERE `tax_type` = 2
    	AND `withholding_type` = 1
	    AND `percentage` = 21
	    AND `start_date` < '2015-01-01';

UPDATE `tax` SET `percentage` = 20, `start_date` = '2015-01-01'
    WHERE `tax_type` = 2
    AND `withholding_type` = 1
    AND `percentage` = 21
    AND `start_date` < '2015-01-01';


UPDATE `db_version` SET `version_number` = '8.12.1';

COMMIT;
