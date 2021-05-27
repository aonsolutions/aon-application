# Database: aon_master
# Version: Actualizacion de la version 8.26.0 a la version 8.26.1.
# Created by: girazu
# Creation Date: 24/06/2015 18:05

BEGIN;

ALTER TABLE `mail_account` ADD `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de la Cuenta de Correo';

ALTER TABLE `room` ADD `status` tinyint(2) NOT NULL default '1' COMMENT 'Estado de la Habitacion' AFTER `item`;
ALTER TABLE `room` ADD `last_cleaning_date` datetime default NULL COMMENT 'Ultima fecha de limpieza' AFTER `status`;


UPDATE `db_version` SET `version_number` = '8.26.1';

COMMIT;
