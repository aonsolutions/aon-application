# Database: aon_master
# Version: Actualizacion de la version 1.8.0 a la version 1.8.1
# Created by: girazu
# Creation Date: 09/07/2008 13:25
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `message_content` MODIFY `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico';

ALTER TABLE `message_log` MODIFY `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico';

UPDATE `invoice` SET `reference_code` = '' WHERE type = 1;

UPDATE `invoice` SET `reference_code` = CONCAT(`series`, '/') WHERE type = 1 AND `series` IS NOT NULL AND `series` != '';

UPDATE `invoice` SET `reference_code` = CONCAT(`reference_code`, `number`) WHERE type = 1;


UPDATE `db_version` SET `version_number` = '1.8.1';

COMMIT;
