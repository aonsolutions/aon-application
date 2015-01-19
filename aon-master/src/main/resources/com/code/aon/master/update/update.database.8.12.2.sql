# Database: aon_master
# Version: Actualizacion de la version 8.12.2 a la version 8.13.0.
# Created by: girazu
# Creation Date: 14/01/2015 12:55

BEGIN;

ALTER TABLE `finance` ADD `manual` tinyint(1) DEFAULT '0' COMMENT 'Indica si el Vencimiento es manual' AFTER `scope`;

ALTER TABLE `user` ADD `lastAccess` datetime DEFAULT NULL COMMENT 'Fecha del ultimo acceso del Usuario';
ALTER TABLE `domain` ADD `lastAccess_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de ultimo acceso';
ALTER TABLE `domain` ADD `lastAccess_date` datetime DEFAULT NULL COMMENT 'Fecha de ultimo acceso';


UPDATE `db_version` SET `version_number` = '8.13.0';

COMMIT;
