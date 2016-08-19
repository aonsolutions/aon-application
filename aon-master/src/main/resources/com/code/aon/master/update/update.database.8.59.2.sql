# Database: aon_master
# Version: Actualizacion de la version 8.59.1 a la version 8.59.2.
# Created by: rtrepiana
# Creation Date: 03/07/2016 

ALTER TABLE `bonus_concept` MODIFY `expression` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe';

BEGIN;

UPDATE `bonus_concept` 
SET `expression` = REPLACE(`expression`, 'FIN_BONIF);', 'FIN_BONIF);TRAMO(FIN_BONIF);') 
WHERE `expression` NOT LIKE '%TRAMO(%';

UPDATE `bonus_concept` 
SET `expression` = REPLACE(`expression`, 'F);', 'F);TRAMO(F);') 
WHERE `expression` NOT LIKE '%TRAMO(%';

UPDATE `db_version` SET `version_number` = '8.63.0';

COMMIT;


