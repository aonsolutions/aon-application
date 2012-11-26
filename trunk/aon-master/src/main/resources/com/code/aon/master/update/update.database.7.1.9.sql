# Database: aon_master
# Version: Actualizacion de la version 7.1.9 a la version 7.1.10.
# Created by: girazu
# Creation Date: 27/09/2012 13:10
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `mail_account` MODIFY `outgoing_verification` tinyint(1) default '1' COMMENT 'Indica si hay autentificacion en el correo saliente';
ALTER TABLE `mail_account` MODIFY `default_account` tinyint(1) default '0' COMMENT 'Indica si es la cuenta de correo por defecto';


UPDATE `db_version` SET `version_number` = '7.1.10';

COMMIT;
