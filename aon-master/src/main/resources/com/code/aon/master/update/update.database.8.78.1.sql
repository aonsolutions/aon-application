# Database: aon_master
# Version: Actualizacion de la version 8.78.1 a la version 8.78.2.
# Created by: girazu
# Creation Date: 01/12/2016 12:55
# Dar tamaño a los ints creados en los 2 ultimos updates.

BEGIN;

ALTER TABLE `task` MODIFY `source_id` int(4) DEFAULT NULL COMMENT 'Identificador del source';

ALTER TABLE `task_comment` MODIFY `source` int(4) DEFAULT NULL COMMENT 'Origen del comentario';

ALTER TABLE `task_comment` MODIFY `source_id` int(4) DEFAULT NULL COMMENT 'Identificador del origen';


UPDATE `db_version` SET `version_number` = '8.78.2';

COMMIT;
