# Database: aon_master
# Version: Actualizacion de la version 8.28.2 a la version 8.29.0.
# Created by: rtrepiana
# Creation Date: 27/08/2015 10:00

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

UPDATE `system_payment` 
SET `quote_expression` = 'BASE_REGULADORA * ( DIAS_MES - (DIAS_NATURALES_MES - DIAS_MATERNIDAD))' 
WHERE `domain`=0 AND `quote_expression` RLIKE '[[:space:]]*DIAS_MATERNIDAD[[:space:]]*\\*[[:space:]]*BASE_REGULADORA[[:space:]]*'; 

UPDATE `system_payment` 
SET `quote_expression` = 'BASE_REGULADORA * ( DIAS_MES - (DIAS_NATURALES_MES - DIAS_PATERNIDAD))' 
WHERE `domain`=0 AND `quote_expression` RLIKE '[[:space:]]*DIAS_PATERNIDAD[[:space:]]*\\*[[:space:]]*BASE_REGULADORA[[:space:]]*';


UPDATE `system_cost` 
SET `expression` = 'BASE_CGC_E * PORCENTAJE_CGC_E/100' 
WHERE code = 'CGC_E';

UPDATE `system_cost` 
SET `expression` = 'BASE_CGP_E * (isdef PORCENTAJE_IT ? PORCENTAJE_IT : (PORCENTAJE_IT=( isdef OCUPACION ? OCUPACION_IT[OCUPACION] : TARIFA_IT)))/100' 
WHERE `code` = 'IT_E'; 

INSERT INTO `system_data` 
(`domain`	,`name`		,`expression`	,`start_date`	,`end_date`	,`read_only`) VALUES 
( 0			, 'DIAS_MES', '[ "01":30, "02":30, "03":30, "04":30, "05":30, "06":30, "07":30, "08": DIAS_NATURALES_MES, "09": DIAS_NATURALES_MES, "10": DIAS_NATURALES_MES, "11": DIAS_NATURALES_MES][GRUPO_COTIZACION]'
										, '2015-01-01', null, 0 );
INSERT INTO `system_data` 
(`domain`	,`name`		,`expression`	,`start_date`	,`end_date`	,`read_only`) VALUES 
( 0			, 'DIAS_PAGA', '[ "01":30, "02":30, "03":30, "04":30, "05":30, "06":30, "07":30, "08": DIAS_NATURALES_MES, "09": DIAS_NATURALES_MES, "10": DIAS_NATURALES_MES, "11": DIAS_NATURALES_MES][GRUPO_COTIZACION]'
										, '2015-01-01', null, 0 );

UPDATE `db_version` SET `version_number` = '8.29.0';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
