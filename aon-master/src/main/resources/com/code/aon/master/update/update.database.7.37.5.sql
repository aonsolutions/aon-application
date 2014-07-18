# Version: Upgrade from version 7.37.4 to 7.37.5
# Created by: rtrepiana
# Creation Date: 14/07/2014 

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

SET @concept=0;

UPDATE `agreement_payment` SET `payment_concept`=@concept:=@concept-1  WHERE `payment_concept` IS NULL;

INSERT INTO `payment_concept` (`id`, `domain`, `code`, `type`, `description`, `expression`, `irpf_expression`, `quote_expression` ) 
( SELECT `payment_concept`, `domain`, CONCAT('__',id),`type`, `description`, `expression`, `irpf_expression`, `quote_expression` FROM `agreement_payment` WHERE `payment_concept` < 0 );

UPDATE `agreement_payment` SET `type`=NULL, `description`=NULL,  `expression`=NULL, `irpf_expression`=NULL, `quote_expression`=NULL  WHERE `payment_concept` < 0 ;

UPDATE `db_version` SET `version_number` = '7.37.6';

COMMIT;

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=1;
