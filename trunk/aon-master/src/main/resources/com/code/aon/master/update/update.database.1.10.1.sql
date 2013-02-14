# Database: aon_master
# Version: Actualizacion de la version 1.10.1 a la version 1.10.2
# Created by: girazu
# Creation Date: 17/10/2008 12:44
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `notice` MODIFY `work_group` int(4) default NULL COMMENT 'Grupo de Trabajo al que va dirigida el Aviso';

ALTER TABLE `alarm` MODIFY `source_id` int(4) default NULL COMMENT 'Identificador del origen de la Alarma';


UPDATE `db_version` SET `version_number` = '1.10.2';

COMMIT;
