
# Database: aon_master
# Version: Actualizacion de la version 8.85.0 a la version 8.85.1.
# Created by: rtrepiana

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

# 101 & 105 TRAINING & FELLOWS

DELETE FROM `system_data` WHERE `domain`=-101 AND `name` = 'BASE_CGP_MIN' AND `start_date` = '2017-01-01';
INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date` ) 
VALUES ( -101, 'BASE_CGP_MIN', '764.40 * 1.08', '2017-01-01' ); 

DELETE FROM `system_data` WHERE `domain`=-105 AND `name` = 'BASE_CGP_MIN' AND `start_date` = '2017-01-01';
INSERT INTO `system_data` ( `domain`, `name`, `expression`, `start_date` ) 
VALUES ( -105, 'BASE_CGP_MIN', '764.40 * 1.08', '2017-01-01' ); 

DELETE FROM `system_deduction` 
WHERE `domain`="-101" AND `start_date`="2017-01-01" AND `deduction_concept` IS NULL;

DELETE FROM `system_deduction` 
WHERE `domain`="-105" AND `start_date`="2017-01-01" AND `deduction_concept` IS NULL;

SET @CGC_CONCEPT=(SELECT `id` FROM`deduction_concept` WHERE `code`="CGC");

DELETE FROM `system_deduction` 
WHERE `domain`="-101" AND `start_date`="2017-01-01" AND `deduction_concept` = @CGC_CONCEPT;

UPDATE `system_deduction` 
SET `end_date` = NULL 
WHERE `domain`="-101" AND `start_date`="2016-01-01" AND `end_date`="2016-12-31" AND `deduction_concept` = @CGC_CONCEPT;

DELETE FROM `system_deduction` 
WHERE `domain`="-105" AND `start_date`="2017-01-01" AND `deduction_concept` = @CGC_CONCEPT;

UPDATE `system_deduction` 
SET `end_date` = NULL 
WHERE `domain`="-105" AND `start_date`="2016-01-01" AND `end_date` = "2016-12-31" AND `deduction_concept` = @CGC_CONCEPT;

SET @DESMPL_CONCEPT=(SELECT `id` FROM`deduction_concept` WHERE `code`="DESMPL");

DELETE FROM `system_deduction` 
WHERE `domain`="-101" AND `start_date`="2017-01-01" AND `deduction_concept` = @DESMPL_CONCEPT;

UPDATE `system_deduction` 
SET `end_date` = NULL 
WHERE `domain`="-101" AND `start_date`="2016-01-01" AND `end_date` = "2016-12-31" AND `deduction_concept` = @DESMPL_CONCEPT;

SET @FP_CONCEPT=(SELECT `id` FROM`deduction_concept` WHERE `code`="FP");

DELETE FROM `system_deduction` 
WHERE `domain`="-101" AND `start_date`="2017-01-01" AND `deduction_concept` = @FP_CONCEPT;

UPDATE `system_deduction` 
SET `end_date` = NULL 
WHERE `domain`="-101" AND `start_date`="2016-01-01" AND `end_date` = "2016-12-31" AND `deduction_concept` = @FP_CONCEPT;

DELETE FROM `system_cost` 
WHERE `domain`="-101" AND `code`="CGC_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` =  NULL
WHERE `domain`="-101" AND `code`="CGC_E" AND `start_date`="2016-01-01" AND `end_date` = "2016-12-31";

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

UPDATE `system_cost` 
SET `end_date` = NULL 
WHERE `domain`="-101" AND `code`="DESMPL_E" AND `start_date`="2016-01-01" AND `end_date` = "2016-12-31";

DELETE FROM `system_cost` 
WHERE `domain`="-101" AND `code`="FOGASA_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = NULL 
WHERE `domain`="-101" AND `code`="FOGASA_E" AND `start_date`="2016-01-01" AND `end_date` = "2016-12-31";

DELETE FROM `system_cost` 
WHERE `domain`="-101" AND `code`="FP_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = NULL 
WHERE `domain`="-101" AND `code`="FP_E" AND `start_date`="2016-01-01" AND `end_date` = "2016-12-31";

DELETE FROM `system_cost` 
WHERE `domain`="-105" AND `code`="CGC_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = NULL 
WHERE `domain`="-105" AND `code`="CGC_E" AND `start_date`="2016-01-01" AND `end_date` = "2016-12-31";

DELETE FROM `system_cost` 
WHERE `domain`="-105" AND `code`="IT_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = NULL 
WHERE `domain`="-105" AND `code`="IT_E" AND `start_date`="2016-01-01" AND `end_date` = "2016-12-31";

DELETE FROM `system_cost` 
WHERE `domain`="-105" AND `code`="IMS_E" AND `start_date`="2017-01-01" ;

UPDATE `system_cost` 
SET `end_date` = NULL 
WHERE `domain`="-105" AND `code`="IMS_E" AND `start_date`="2016-01-01" AND `end_date` = "2016-12-31";

UPDATE `db_version` SET `version_number` = '8.85.1';

COMMIT;



SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

