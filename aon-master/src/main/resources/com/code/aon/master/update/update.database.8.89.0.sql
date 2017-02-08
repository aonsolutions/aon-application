
# Database: aon_master
# Version: Actualizacion de la version 8.85.0 a la version 8.85.1.
# Created by: rtrepiana

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

# 101 & 105 TRAINING & FELLOWS


DELETE FROM `system_deduction` 
WHERE `domain`="-101" AND `start_date`="2017-01-01" AND `deduction_concept` IS NULL;

DELETE FROM `system_deduction` 
WHERE `domain`="-105" AND `start_date`="2017-01-01" AND `deduction_concept` IS NULL;

SET @CGC_CONCEPT=(SELECT `id` FROM`deduction_concept` WHERE `code`="CGC");

DELETE FROM `system_deduction` 
WHERE `domain`="-101" AND `start_date`="2017-01-01" AND `deduction_concept` = @CGC_CONCEPT;

UPDATE `system_deduction` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-101" AND `start_date`="2016-01-01" AND `end_date` IS NULL AND `deduction_concept` = @CGC_CONCEPT;

INSERT INTO `system_deduction` 
(`domain`
,`type`
,`deduction_concept`
,`description`
,`description_decorable`
,`expression`
,`start_date`
,`end_date`
,`month`) VALUES 
("-101"
,NULL
,@CGC_CONCEPT
,NULL
,"0"
,"SI(BASE_CGC > 0.00, 6.67,0.00)"
,"2017-01-01"
,NULL
,NULL);

DELETE FROM `system_deduction` 
WHERE `domain`="-105" AND `start_date`="2017-01-01" AND `deduction_concept` = @CGC_CONCEPT;

UPDATE `system_deduction` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-105" AND `start_date`="2016-01-01" AND `end_date` IS NULL AND `deduction_concept` = @CGC_CONCEPT;

INSERT INTO `system_deduction` 
(`domain`
,`type`
,`deduction_concept`
,`description`
,`description_decorable`
,`expression`
,`start_date`
,`end_date`
,`month`)
VALUES (
"-105"
,NULL
,@CGC_CONCEPT
,NULL
,"0"
,"SI(BASE_CGC > 0.00, 6.67,0.00)"
,"2017-01-01"
,NULL
,NULL);

SET @DESMPL_CONCEPT=(SELECT `id` FROM`deduction_concept` WHERE `code`="DESMPL");

DELETE FROM `system_deduction` 
WHERE `domain`="-101" AND `start_date`="2017-01-01" AND `deduction_concept` = @DESMPL_CONCEPT;

# 
# UPDATE `system_deduction` 
# SET `end_date` = "2016-12-31" 
# WHERE `domain`="-101" AND `start_date`="2016-01-01" AND `end_date` IS NULL AND `deduction_concept` = @DESMPL_CONCEPT;

# INSERT INTO `system_deduction` 
# (`domain`
# ,`type`
# ,`deduction_concept`
# ,`description`
# ,`description_decorable`
# ,`expression`
# ,`start_date`
# ,`end_date`
# ,`month`)
# VALUES 
# ("-101"
# ,NULL
# ,@DESMPL_CONCEPT
# ,NULL
# ,"0"
# ,"1.55/100.00 * BASE_CGP_MIN"
# ,"2017-01-01"
# ,NULL
# ,NULL);

SET @FP_CONCEPT=(SELECT `id` FROM`deduction_concept` WHERE `code`="FP");

DELETE FROM `system_deduction` 
WHERE `domain`="-101" AND `start_date`="2017-01-01" AND `deduction_concept` = @FP_CONCEPT;

DELETE FROM `system_deduction` 
WHERE `domain`="-101" AND `start_date`="2014-01-01" AND `deduction_concept` = @FP_CONCEPT 
AND `expression` IN ('0.15', '0.16');

UPDATE `system_deduction` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-101" AND `start_date` < "2017-01-01" AND `end_date` IS NULL AND `deduction_concept` = @FP_CONCEPT;


INSERT INTO `system_deduction` 
(`domain`
,`type`
,`deduction_concept`
,`description`
,`description_decorable`
,`expression`
,`start_date`
,`end_date`
,`month`)
VALUES 
("-101"
,NULL
,@FP_CONCEPT
,NULL
,"0"
,"SI(BASE_CGP > 0.00, 0.16,0.00)"
,"2017-01-01"
,NULL
,NULL);

DELETE FROM `system_cost` 
WHERE `domain`="-101" AND `code`="CGC_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-101" AND `code`="CGC_E" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-101","2017-01-01",NULL,"Contingencias Comunes","SI(BASE_CGC > 0.00, 33.46,0.00)","0","CGC_E");

DELETE FROM `system_cost` 
WHERE `domain`="-101" AND `code`="IT_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = NULL 
WHERE `domain`="-101" AND `code`="IT_E" AND `start_date`="2016-01-01" AND `end_date` = "2016-12-31";


DELETE FROM `system_cost` 
WHERE `domain`="-101" AND `code`="IMS_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = NULL 
WHERE `domain`="-101" AND `code`="IMS_E" AND `start_date`="2016-01-01" AND `end_date` = "2016-12-31";


DELETE FROM `system_cost` 
WHERE `domain`="-101" AND `code`="DESMPL_E" AND `start_date`="2017-01-01" ;

# UPDATE `system_cost` 
# SET `end_date` = "2016-12-31" 
# WHERE `domain`="-101" AND `code`="DESMPL_E" AND `start_date`="2016-01-01" AND `end_date` IS NULL;
# 
# INSERT INTO `system_cost` 
# (`domain`
# ,`start_date`
# ,`end_date`
# ,`description`
# ,`expression`
# ,`type`
# ,`code`) 
# VALUES ("-101","2017-01-01",NULL,NULL,"5.50/100.00 * 825.60","2","DESMPL_E");

DELETE FROM `system_cost` 
WHERE `domain`="-101" AND `code`="FOGASA_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-101" AND `code`="FOGASA_E" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-101","2017-01-01",NULL,"FOGASA","SI(BASE_CGP > 0.00, 2.54,0.00)","10","FOGASA_E");

DELETE FROM `system_cost` 
WHERE `domain`="-101" AND `code`="FP_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-101" AND `code`="FP_E" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-101","2017-01-01",NULL,"Formación Profesional","SI(BASE_CGP > 0.00, 1.23,0.00)","3","FP_E");

DELETE FROM `system_cost` 
WHERE `domain`="-105" AND `code`="CGC_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = "2016-12-31" 
WHERE `domain`="-105" AND `code`="CGC_E" AND `start_date`="2016-01-01" AND `end_date` IS NULL;

INSERT INTO `system_cost` 
(`domain`
,`start_date`
,`end_date`
,`description`
,`expression`
,`type`
,`code`) 
VALUES ("-105","2017-01-01",NULL,"Contingencias Comunes","SI(BASE_CGC > 0.00, 33.46,0.00)","0","CGC_E");

DELETE FROM `system_cost` 
WHERE `domain`="-105" AND `code`="IT_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = NULL
WHERE `domain`="-105" AND `code`="IT_E" AND `start_date`="2016-01-01" AND `end_date` = "2016-12-31" ;

DELETE FROM `system_cost` 
WHERE `domain`="-105" AND `code`="IMS_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = NULL
WHERE `domain`="-105" AND `code`="IMS_E" AND `start_date`="2016-01-01" AND `end_date` = "2016-12-31" ;

DELETE FROM `system_data` WHERE `domain`=-101 AND `name` = 'BASE_CGP_MIN' AND `start_date` = '2017-01-01';

INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date` ) 
VALUES ( -101, 'BASE_CGP_MIN', '825.60', '2017-01-01' ); 

DELETE FROM `system_data` WHERE `domain`=-105 AND `name` = 'BASE_CGP_MIN' AND `start_date` = '2017-01-01';

INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date` ) 
VALUES ( -105, 'BASE_CGP_MIN', '825.60', '2017-01-01' ); 


UPDATE `db_version` SET `version_number` = '8.89.1';

COMMIT;



SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

