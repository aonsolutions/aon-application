# Database: aon_master
# Version: Actualizacion de la version 8.98.0 a la version 8.98.1.
# Created by: eagirrezabal
# Creation Date: 07/04/2017 

BEGIN;

ALTER TABLE `elaboration` ADD `description` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion' AFTER `item`;

ALTER TABLE `elaboration` ADD `remarks` text COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Observaciones' AFTER `comments`;

ALTER TABLE `elaboration` MODIFY `comments` text COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Comentarios';

ALTER TABLE `carrier` ADD `status` tinyint(2) DEFAULT NULL COMMENT 'Estado de la Agencia de Transporte';

UPDATE `db_version` SET `version_number` = '8.98.1';

COMMIT;
