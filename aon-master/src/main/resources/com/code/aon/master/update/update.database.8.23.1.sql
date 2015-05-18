# Database: aon_master
# Version: Actualizacion de la version 8.23.1 a la version 8.23.2.
# Created by: rtrepiana

/*!40101 SET NAMES latin1 */;

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

SET @ERE=(SELECT IFNULL(
	(SELECT `id` FROM `payment_concept` WHERE `code`='ERE' AND `domain`=0),
	(SELECT max(`id`) + 1 FROM `payment_concept`)
	)
);

INSERT IGNORE INTO `payment_concept` 
(`id`	,`domain`	,`code`	,`type` )  VALUES
(@ERE	,0		,'ERE'	,1	);


DELETE FROM `system_payment`  
WHERE `domain`=0 AND `payment_concept`=@ERE;

INSERT INTO `system_payment` 
(`start_date`	, `payment_concept`	,`domain`	,`type`	,`salary_type`	, `quote_expression`		, `description`  )  VALUES
('2010-01-01'	,@ERE			,0		,1	, 1		,'DIAS_ERE * BASE_REGULADORA'	, 'EXPDTE. REGULARIZACIÓN DE EMPLEO');

UPDATE `system_cost` 
SET `expression`='( BASE_CGC_E=( BASE_CGC + ( isdef BASE_MTNAD ? BASE_MTNAD : 0.00 ) + ( isdef BASE_ERE ? BASE_ERE : 0.00 ) ) ) * PORCENTAJE_CGC_E/100'  
WHERE `domain` = 0 AND `code`='CGC_E';

UPDATE `db_version` SET `version_number` = '8.23.2';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
