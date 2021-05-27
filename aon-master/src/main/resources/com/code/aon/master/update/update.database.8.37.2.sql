# Database: aon_master
# Version: Actualizacion de la version 8.37.2 a la version 8.38.0.
# Created by: rtrepiana
# Creation Date: 12/01/2015

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

UPDATE `system_payment` 
SET `quote_expression` = 'DIAS_COTIZADOS * BASE_REGULADORA'
, `expression` = 'DIAS_ENFERMEDAD_COMUN_1_3 * 0.00 * BASE_REGULADORA'
WHERE `domain`=0 AND `quote_expression` RLIKE '[[:space:]]*DIAS_ENFERMEDAD_COMUN_1_3[[:space:]]*\\*[[:space:]]*BASE_REGULADORA[[:space:]]*'; 

UPDATE `system_payment` 
SET `quote_expression` = 'DIAS_COTIZADOS * BASE_REGULADORA'
WHERE `domain`=0 AND `quote_expression` RLIKE '[[:space:]]*DIAS_ENFERMEDAD_COMUN_4_15[[:space:]]*\\*[[:space:]]*BASE_REGULADORA[[:space:]]*'; 

UPDATE `system_payment` 
SET `quote_expression` = 'DIAS_COTIZADOS * BASE_REGULADORA'
WHERE `domain`=0 AND `quote_expression` RLIKE '[[:space:]]*DIAS_ENFERMEDAD_COMUN_16_20[[:space:]]*\\*[[:space:]]*BASE_REGULADORA[[:space:]]*'; 

UPDATE `system_payment` 
SET `quote_expression` = 'DIAS_COTIZADOS * BASE_REGULADORA'
WHERE `domain`=0 AND `quote_expression` RLIKE '[[:space:]]*DIAS_ENFERMEDAD_COMUN_21[[:space:]]*\\*[[:space:]]*BASE_REGULADORA[[:space:]]*'; 

UPDATE `system_payment` 
SET `quote_expression` = 'DIAS_COTIZADOS * BASE_REGULADORA'
WHERE `domain`=0 AND `quote_expression` RLIKE '[[:space:]]*DIAS_ENFERMEDAD_PROFESIONAL[[:space:]]*\\*[[:space:]]*BASE_REGULADORA[[:space:]]*'; 


UPDATE `system_payment` 
SET `quote_expression` = 'DIAS_COTIZADOS * BASE_REGULADORA'
WHERE `domain`=0 AND `quote_expression` = 'BASE_REGULADORA * ( DIAS_MES - (DIAS_NATURALES_MES - DIAS_MATERNIDAD))'; 

UPDATE `system_payment` 
SET `quote_expression` = 'DIAS_COTIZADOS * BASE_REGULADORA'
WHERE `domain`=0 AND `quote_expression` = 'BASE_REGULADORA * ( DIAS_MES - (DIAS_NATURALES_MES - DIAS_PATERNIDAD))';



UPDATE `db_version` SET `version_number` = '8.38.0';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
