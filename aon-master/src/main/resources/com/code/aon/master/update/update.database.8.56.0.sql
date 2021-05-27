# Database: aon_master
# Version: Actualizacion de la version 8.56.0 a la version 8.57.0.
# Created by: rtrepiana

BEGIN;

INSERT INTO `payment_concept` 
(`code`, `domain`, `type`, `description`, `irpf_expression`, `quote_expression` ) 
(SELECT 'PREAVISO', `domain`, `type`, `description`, `irpf_expression`, `quote_expression` 
FROM `system_payment` 
WHERE `salary_type` = 2 
AND `description` = 'DIAS PREAVISO');

SET @PREAVISO=(SELECT `id` FROM `payment_concept` WHERE `code`='PREAVISO');

UPDATE `system_payment` 
SET 
`type` = NULL,
`description`= NULL,
`irpf_expression` = NULL,
`quote_expression` = NULL,
`payment_concept` = @PREAVISO
WHERE `salary_type` = 2 
AND `description` = 'DIAS PREAVISO';


UPDATE  `system_payment` 
SET `expression`= REPLACE(`expression`, 'FIN_CONTRATO', 'INICIO_CONTRATO') 
WHERE `salary_type` = 2 
AND `expression` LIKE '%CAUSA_INDEMNIZACION == FIN\_%';

UPDATE `db_version` SET `version_number` = '8.57.0';

COMMIT;
