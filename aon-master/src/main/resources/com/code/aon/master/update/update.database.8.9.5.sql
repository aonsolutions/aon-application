# Database: aon_master
# Version: Actualizacion de la version 8.9.5 a la version 8.10.0.
# Created by: girazu
# Creation Date: 21/11/2014 09:50

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;


# SS percents for Home Employees. 
# Common contingency : 23,80% , 19,85% enterprise (cost) and 3,95% employee (deduction).
INSERT INTO system_data 
( domain, name					, expression	, start_date	, end_date		, read_only , comments )  VALUES 
(-106	, 'PORCENTAJE_CGC'		, '3.95'		, '2014-01-01'	, NULL			, 1			, NULL ),
(-106	, 'PORCENTAJE_DESMPL'	, 'REMOVE()'	, '2014-01-01'	, NULL			, 1			, NULL ),
(-106	, 'PORCENTAJE_FP'		, 'REMOVE()'	, '2014-01-01'	, NULL			, 1			, NULL ),
(-106	, 'PORCENTAJE_EXTR'		, 'REMOVE()'	, '2014-01-01'	, NULL			, 1			, NULL ),
(-106	, 'PORCENTAJE_NEXTR'	, 'REMOVE()'	, '2014-01-01'	, NULL			, 1			, NULL ),
(-106	, 'PORCENTAJE_CGC_E'	, '19.85'		, '2014-01-01'	, NULL			, 1			, NULL ),
(-106	, 'PORCENTAJE_IT'		, 'TARIFA_IT'	, '2014-01-01'	, NULL			, 1			, NULL ),
(-106	, 'PORCENTAJE_IMS'		, 'TARIFA_IMS'	, '2014-01-01'	, NULL			, 1			, NULL ),
(-106	, 'PORCENTAJE_FOGASA'	, 'REMOVE()'	, '2014-01-01'	, NULL			, 1			, NULL ),
(-106	, 'PORCENTAJE_DESMPL_E'	, 'REMOVE()'	, '2014-01-01'	, NULL			, 1			, NULL ),
(-106	, 'PORCENTAJE_FP_E'		, 'REMOVE()'	, '2014-01-01'	, NULL			, 1			, NULL ),
(-106	, 'PORCENTAJE_EXTR_E'	, 'REMOVE()'	, '2014-01-01'	, NULL			, 1			, NULL ),
(-106	, 'PORCENTAJE_NEXTR_E'	, 'REMOVE()'	, '2014-01-01'	, NULL			, 1			, NULL )
;

# SS bases for Home Employees. 
INSERT INTO system_data 
( domain, name				, expression	, start_date	, end_date		, read_only , comments )  VALUES 
(-106	, 'BASE_CGC_MIN'	, '($ in [[172.05,147.86],[268.80,244.62],[365.60,341.40],[462.40,438.17],[559.10,534.95],[655.90,631.73],[753.00,753.00],[Double.MAX_VALUE,790.65]] if $[0] >= BASE_CGC )[0][1]'			
											, '2014-01-01'	, NULL			, 1			, NULL ),
(-106	, 'BASE_CGC_MAX'	, 'BASE_CGC_MIN', '2014-01-01'	, NULL			, 1			, NULL ),
(-106	, 'BASE_CGP_MIN'	, 'BASE_CGC_MIN', '2014-01-01'	, NULL			, 1			, NULL ),
(-106	, 'BASE_CGP_MAX'	, 'BASE_CGC_MIN', '2014-01-01'	, NULL			, 1			, NULL )
;


INSERT IGNORE INTO `app_param` (`domain`, `name`, `value`) 
	SELECT `domain`, 'PMS_SIMPLE_AVAILABILITY_URL', NULL FROM `app_param` WHERE `name` = 'PMS_SOAP_SERVER_URL';
UPDATE `app_param` SET `name` = 'PMS_BOOKING_URL' WHERE `name` = 'PMS_SOAP_SERVER_URL';



UPDATE `db_version` SET `version_number` = '8.10.0';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
