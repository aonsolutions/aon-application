# Database: aon_master
# Version: Actualizacion de la version 1.10.4 a la version 1.10.5
# Created by: girazu
# Creation Date: 26/11/2008 11:04
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



UPDATE `web_info_page_resource` SET `rattach` = NULL WHERE `rattach` NOT IN (SELECT `id` FROM `rattach`);

ALTER TABLE `web_info_page_resource` ADD KEY `rattach` (`rattach`);

ALTER TABLE `web_info_page_resource` ADD CONSTRAINT `web_info_page_resource_fk2` FOREIGN KEY (`rattach`) REFERENCES `rattach` (`id`);


UPDATE `db_version` SET `version_number` = '1.10.5';

COMMIT;
