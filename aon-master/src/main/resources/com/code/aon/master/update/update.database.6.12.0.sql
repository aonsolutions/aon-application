# Database: aon_master
# Version: Actualizacion de la version 6.12.0 a la version 6.13.0.
# Created by: rtrepiana
# Creation Date: 24/09/2011
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE enterprise_activity MODIFY `cnae` int(4) default  NULL COMMENT 'Identificador del CNAE';


UPDATE `db_version` SET `version_number` = '6.13.0';

COMMIT;
