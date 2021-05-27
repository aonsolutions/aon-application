# Database: aon_master
# Version: Actualizacion de la version 8.21.1 a la version 8.21.2.
# Created by: rtrepiana

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

SET @DESCRIPTION=(SELECT description FROM `system_cost` WHERE code='ECSS_E' LIMIT 1);

DELETE FROM `system_cost` WHERE code='ECSS_E';

INSERT INTO `system_cost` ( `domain`, `code`, `type`, `expression`, `description`, `start_date` ) 
VALUES (0, 'ECSS_E', 8, '-1 * DIAS_ENFERMEDAD_COMUN_16_20 * BASE_REGULADORA * 0.60 ', @DESCRIPTION, '2010-01-01');

INSERT INTO `system_cost` ( `domain`, `code`, `type`, `expression`, `description`, `start_date` ) 
VALUES (0, 'ECSS_E', 8, '-1 * DIAS_ENFERMEDAD_COMUN_21 * BASE_REGULADORA * 0.75 ', @DESCRIPTION, '2010-01-01');


UPDATE `system_cost` 
SET expression='-1 * DIAS_ENFERMEDAD_PROFESIONAL * BASE_REGULADORA * 0.75',
type=8
WHERE `code`='ATEP_E';


UPDATE `db_version` SET `version_number` = '8.21.2';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
