# Database: aon_master
# Version: Actualizacion de la version 1.2.3 a la version 1.2.4
# Created by: girazu
# Creation Date: 19/11/2007 15:05
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `qualification` MODIFY `min_value` double(15,3) NOT NULL default '0.000' COMMENT 'Limite inferior de la Calificacion';

ALTER TABLE `qualification` MODIFY `max_value` double(15,3) NOT NULL default '0.000' COMMENT 'Limite superior de la Calificacion';


UPDATE `db_version` SET `version_number` = '1.2.4';

COMMIT;
