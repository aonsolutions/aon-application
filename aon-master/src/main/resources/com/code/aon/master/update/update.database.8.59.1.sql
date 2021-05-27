# Database: aon_master
# Version: Actualizacion de la version 8.59.1 a la version 8.59.2.
# Created by: rtrepiana
# Creation Date: 03/07/2016 


BEGIN;


UPDATE `contract_deduction` 
SET `expression` = REPLACE(`expression`, ' ', '')   
WHERE `description` IN ( 'BASE_CGC' , 'BASE_CGP' );

UPDATE `contract_deduction` 
SET `expression` = REPLACE(`expression`, '(NOMINA)?(/*user*/', '')   
WHERE `description` IN ( 'BASE_CGC' , 'BASE_CGP' );


UPDATE `contract_deduction` 
SET `expression` = REPLACE(`expression`, '/**/):REMOVE()', '')   
WHERE `description` IN ( 'BASE_CGC' , 'BASE_CGP' );

UPDATE `contract_deduction` 
SET `expression` = 
REPLACE(
	REPLACE(`expression`
	,CONCAT(`description`,'=')
	,CONCAT(`description`,'=REDEFINE(\'',`description`,'\','))
,'/**/;'
,'/**/);')   
WHERE `description` IN ( 'BASE_CGC' , 'BASE_CGP' );


UPDATE `db_version` SET `version_number` = '8.59.2';

COMMIT;


