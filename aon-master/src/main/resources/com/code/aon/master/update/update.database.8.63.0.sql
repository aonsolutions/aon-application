# Database: aon_master
# Version: Actualizacion de la version 8.63.0 a la version 8.63.1
# Created by: eagirrezabal
# Creation Date: 20/08/2016 

BEGIN;

ALTER TABLE `fbatch` ADD `rattach` int(4) DEFAULT NULL COMMENT 'Identificador del Archivo Adjunto' AFTER `security_level`;

UPDATE `db_version` SET `version_number` = '8.63.1';

COMMIT;


