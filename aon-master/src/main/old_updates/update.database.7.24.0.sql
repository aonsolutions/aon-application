# Database: aon_master
# Version: Actualizacion de la version 7.24.0 a la version 7.25.0.
# Created by: girazu
# Creation Date: 06/11/2013 17:35
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `domain` ADD `scope` int(4) default NULL COMMENT 'Identificador del Ambito' AFTER `type`;
ALTER TABLE `domain` ADD KEY `IDX_DOMAIN_SCOPE` (`scope`);
ALTER TABLE `domain` ADD CONSTRAINT `FK_DOMAIN_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);


UPDATE `db_version` SET `version_number` = '7.25.0';

COMMIT;
