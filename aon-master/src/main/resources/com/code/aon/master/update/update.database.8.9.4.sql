# Database: aon_master
# Version: Actualizacion de la version 8.9.4 a la version 8.10.0.
# Created by: girazu
# Creation Date: 21/11/2014 09:50

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

# Since we use 'DeferredExpressionVariable' for load system data with Undefined data.
# We can avoid use of 'String' for SS expressions. They'll be resolved when we've all data.  

UPDATE system_data SET expression=REPLACE(expression, '"', '') WHERE name = 'BASE_CGP_MIN';
UPDATE system_data SET expression=REPLACE(expression, '"', '') WHERE name = 'BASE_CGP_MAX';

UPDATE system_data SET expression=REPLACE(expression, ': "', ':(') WHERE name LIKE 'BASE_CGC_%';
UPDATE system_data SET expression=REPLACE(expression, '",' , '),') WHERE name LIKE 'BASE_CGC_%';
UPDATE system_data SET expression=REPLACE(expression, '"]' , ')]') WHERE name LIKE 'BASE_CGC_%';
UPDATE system_data SET expression=CONCAT(expression, '[GRUPO_COTIZACION]') WHERE name LIKE 'BASE_CGC_%';

# Bases for Training & Learning contract. 
INSERT INTO system_data 
( domain, name				, expression	, start_date	, end_date		, read_only , comments )  VALUES 
(-101	, 'BASE_CGC_MIN'	, 'BASE_CGP_MIN', '2013-01-01'	, NULL			, 1			, NULL ),
(-101	, 'BASE_CGC_MAX'	, 'BASE_CGP_MIN', '2013-01-01'	, NULL			, 1			, NULL ),
(-101	, 'BASE_CGP_MAX'	, 'BASE_CGP_MIN', '2013-01-01'	, NULL			, 1			, NULL )
;

INSERT IGNORE INTO `app_param` (`domain`, `name`, `value`) 
	SELECT `domain`, 'PMS_SIMPLE_AVAILABILITY_URL', NULL FROM `app_param` WHERE `name` = 'PMS_SOAP_SERVER_URL';
UPDATE `app_param` SET `name` = 'PMS_BOOKING_URL' WHERE `name` = 'PMS_SOAP_SERVER_URL';


UPDATE `db_version` SET `version_number` = '8.10.0';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;