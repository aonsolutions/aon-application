# Database: aon_master
# Version: Actualizacion de la version 7.10.2 a la version 7.10.3.
# Created by: girazu
# Creation Date: 08/02/2013 13:05
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `seller` ADD `scope` int(4) default NULL COMMENT 'Identificador del Ambito';
UPDATE `seller` SET `scope` = (SELECT MIN(`id`) FROM `scope` WHERE `domain` = `seller`.`domain`);
ALTER TABLE `seller` MODIFY `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito';
ALTER TABLE `seller` ADD KEY `IDX_SELLER_SCOPE` (`scope`);
ALTER TABLE `seller` ADD CONSTRAINT `FK_SELLER_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);


UPDATE `db_version` SET `version_number` = '7.10.3';

COMMIT;
