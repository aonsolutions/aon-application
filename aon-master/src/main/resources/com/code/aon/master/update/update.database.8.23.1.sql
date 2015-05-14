# Database: aon_master
# Version: Actualizacion de la version 8.23.0 a la version 8.23.1.
# Created by: rtrepiana

/*!40101 SET NAMES latin1 */;

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

SET @MTNAD=(SELECT IFNULL(
	(SELECT `id` FROM `payment_concept` WHERE `code`='MTNAD' AND `domain`=0),
	(SELECT max(`id`) + 1 FROM `payment_concept`)
	)
);

INSERT IGNORE INTO `payment_concept` 
(`id`	,`domain`	,`code`	,`type` )  VALUES
(@MTNAD	,0		,'MTNAD',1	);


UPDATE `system_payment` SET `payment_concept`=@MTNAD 
WHERE `domain`=0 AND `quote_expression` LIKE '%DIAS_%NIDAD%*%BASE_REGULADORA%';

UPDATE `db_version` SET `version_number` = '8.23.1';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
