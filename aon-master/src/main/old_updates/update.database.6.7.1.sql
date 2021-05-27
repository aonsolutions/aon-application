# Database: aon_master
# Version: Actualizacion de la version 6.7.1 a la version 6.7.2.
# Created by: girazu
# Creation Date: 07/07/2011 17:56
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `offer` ADD `project` int(4) default NULL COMMENT 'Identificador del Proyecto' AFTER `id`;

ALTER TABLE `offer` ADD KEY `IDX_OFFR_PROJECT` (`project`);

ALTER TABLE `offer` ADD CONSTRAINT `FK_OFFER_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`);


UPDATE `db_version` SET `version_number` = '6.7.2';

COMMIT;
