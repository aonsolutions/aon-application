# Database: aon_master
# Version: Actualizacion de la version 6.18.2 a la version 6.18.3.
# Created by: eagirrezabal
# Creation Date: 17/10/2011 12:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE contract_leave_detail 
CHANGE `processed` `status` tinyint(2) NOT NULL default '0' COMMENT 'Indica el estado';

UPDATE `db_version` SET `version_number` = '6.18.3';

COMMIT;
