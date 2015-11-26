# Database: aon_master
# Version: Actualizacion de la version 7.0.14 a la version 7.1.0.
# Created by: girazu
# Creation Date: 16/05/2012 17:00
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE purchase ADD `email_communication` tinyint(1) NOT NULL default '0' COMMENT 'Indica si se ha comunicado a traves de email';

ALTER TABLE `user` ADD `toolbar` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Barra de Herramientas del Usuario';

ALTER TABLE `mail_account` ADD `incoming_security` tinyint(2) NOT NULL default '0' COMMENT 'Seguridad de conexión del correo entrante' AFTER `incoming_ssl`;
ALTER TABLE `mail_account` ADD `outgoing_security` tinyint(2) NOT NULL default '0' COMMENT 'Seguridad de conexión del correo saliente' AFTER `outgoing_ssl`;

UPDATE `mail_account` SET `incoming_security` = 1 WHERE `incoming_ssl` = 1;
UPDATE `mail_account` SET `outgoing_security` = 1 WHERE `outgoing_ssl` = 1;

ALTER TABLE `mail_account` DROP `incoming_ssl`;
ALTER TABLE `mail_account` DROP `outgoing_ssl`;
ALTER TABLE `mail_account` DROP `incoming_host`;
ALTER TABLE `mail_account` CHANGE `host` `incoming_host` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Host del correo entrante';


UPDATE `db_version` SET `version_number` = '7.1.0';

COMMIT;
