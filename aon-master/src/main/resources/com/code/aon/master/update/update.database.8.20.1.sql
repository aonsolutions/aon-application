# Database: aon_master
# Version: Actualizacion de la version 8.20.1 a la version 8.21.0.
# Created by: rtrepiana

BEGIN;


UPDATE  `bonus_concept` 
SET `expression`='MAX(0.00,CGC_E - (( COEFICIENTE_PARCIALIDAD == 1 ? 100 : (COEFICIENTE_PARCIALIDAD >= 0.75 ? 75 : (COEFICIENTE_PARCIALIDAD >= 0.5 ? 50 : 0.00))) / 30 * DIAS_BONIFICACION))' 
WHERE type=15 AND domain=0;


UPDATE `db_version` SET `version_number` = '8.21.0';

COMMIT;
