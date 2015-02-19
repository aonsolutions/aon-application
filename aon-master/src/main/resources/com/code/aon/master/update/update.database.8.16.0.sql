

# Database: aon_master
# Version: Actualizacion de la version 8.16.0 a la version 8.16.1.
# Created by: rtrepiana
# Creation Date: 19/02/2015 

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;



INSERT INTO `system_cost` 
(`domain`	,`start_date`	,`end_date`	,`description`	,`expression`	,`type`	,`code`) VALUES 
("-101"		,"2015-01-01"	,NULL		,NULL		,"30.67"	,"0"	,"CGC_E");
UPDATE `system_cost` SET `end_date`="2014-12-31" WHERE `domain`=-101 AND `code`="CGC_E" AND `start_date`="2014-01-01" ; 

INSERT INTO `system_cost` 
(`domain`	,`start_date`	,`end_date`	,`description`	,`expression`	,`type`	,`code`) VALUES 
("-101"		,"2015-01-01"	,NULL		,NULL		,"2.36"		,"1"	,"IT_E");
UPDATE `system_cost` SET `end_date`="2014-12-31" WHERE `domain`=-101 AND `code`="IT_E" AND `start_date`="2014-01-01"; 

INSERT INTO `system_cost` 
(`domain`	,`start_date`	,`end_date`	,`description`	,`expression`	,`type`	,`code`) VALUES 
("-101"		,"2015-01-01"	,NULL		,NULL		,"1.86"		,"1"	,"IMS_E");
UPDATE `system_cost` SET `end_date`="2014-12-31" WHERE `domain`=-101 AND `code`="IMS_E" AND `start_date`="2014-01-01"; 

INSERT INTO `system_cost` 
(`domain`	,`start_date`	,`end_date`	,`description`	,`expression`	,`type`	,`code`) VALUES 
("-101"		,"2015-01-01"	,NULL		,NULL		,"2.33"		,"10"	,"FOGASA_E");
UPDATE `system_cost` SET `end_date`="2014-12-31" WHERE `domain`=-101 AND `code`="FOGASA_E" AND `start_date`="2014-01-01"; 

INSERT INTO `system_deduction` 
(`domain`	,`type`	,`deduction_concept`	,`description`	,`description_decorable`,`expression`	,`start_date`	,`end_date`	,`month`) VALUES 
("-101"		,NULL	,"6299"			,NULL		,"0"			,"6.12"		,"2015-01-01"	,NULL		,NULL);
UPDATE `system_deduction` SET `end_date`="2014-12-31" WHERE `domain`=-101 AND `deduction_concept`="6299" AND `start_date`="2014-01-01";

UPDATE `db_version` SET `version_number` = '8.16.1';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

