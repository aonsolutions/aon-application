# Database: aon_master
# Version: Actualizacion de la version 1.2.2 a la version 1.2.3
# Created by: girazu
# Creation Date: 09/11/2007 11:45
# Comentarios: este script unicamente realiza los cambios necesarios en la estructura de la base de datos Aon.
#              Luego depende de cada uno el ir apañando los datos de sus aplicaciones a la nueva estructura.
#	       Cambios invalidantes: se ha añadido un indice unico en la tabla employee por la columna social_security_num.



ALTER TABLE `employee` ADD UNIQUE KEY `social_security_num` (`social_security_num`);

ALTER TABLE `web_info` MODIFY `schedule` text collate latin1_spanish_ci default NULL COMMENT 'Horario';

ALTER TABLE `web_info` ADD `title` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Titulo de la ultima pestaña';

ALTER TABLE `web_info` ADD `content` text collate latin1_spanish_ci COMMENT 'Contenido de la ultima pestaña';

ALTER TABLE `web_info` COMMENT 'Informacion de la empresa que se mostrara en la ficha web';

DROP TABLE `email`;

ALTER TABLE `mail_account` MODIFY `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico';

ALTER TABLE `mail_account` MODIFY `email` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta de correo';

ALTER TABLE `mail_account` MODIFY `protocol` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Protocolo utilizado (IMAP)';

ALTER TABLE `mail_account` MODIFY `host` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Host del servidor de correo';

ALTER TABLE `mail_account` MODIFY `incoming_host` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Host del correo entrante';

ALTER TABLE `mail_account` MODIFY `incoming_port` int(4) default NULL COMMENT 'Puerto del correo entrante';

ALTER TABLE `mail_account` MODIFY `incoming_ssl` bit(1) default NULL COMMENT 'Indica si tiene SSL el correo entrante';

ALTER TABLE `mail_account` MODIFY `outgoing_verification` bit(1) default NULL COMMENT 'Indica si hay autentificacion en el correo saliente';

ALTER TABLE `mail_account` MODIFY `outgoing_host` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Host del servidor de correo saliente';

ALTER TABLE `mail_account` MODIFY `outgoing_port` int(4) default NULL COMMENT 'Puerto del servidor de correo saliente';

ALTER TABLE `mail_account` MODIFY `outgoing_ssl` bit(1) default NULL COMMENT 'Indica si tiene SSL el correo saliente';

ALTER TABLE `mail_account` MODIFY `mail_username` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Nombre del usuario';

ALTER TABLE `mail_account` MODIFY `password` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Clave del usuario';

ALTER TABLE `mail_account` MODIFY `user` int(4) NOT NULL COMMENT 'Identificador de Usuario';

ALTER TABLE `mail_account` MODIFY `status` tinyint(2) default NULL COMMENT 'Indica si es el servidor de correo principal del usuario o no';

ALTER TABLE `mail_account` ADD `black_list` varchar(255) collate latin1_spanish_ci default NULL COMMENT 'Ruta del fichero de gestion de black/white list';

ALTER TABLE `mail_account` COMMENT 'Cuentas de Correo Electronico';

ALTER TABLE `signature` MODIFY `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico';

ALTER TABLE `signature` MODIFY `mail_account` int(4) NOT NULL COMMENT 'mail_account' COMMENT 'Identificador de la Cuenta de Correo';

ALTER TABLE `signature` MODIFY `signature` text collate latin1_spanish_ci COMMENT 'firma' COMMENT 'Texto de la Firma de la Cuenta de Correo';

ALTER TABLE `signature` MODIFY `active` tinyint(2) default NULL COMMENT 'Indica si la Firma esta activa o no';

ALTER TABLE `signature` MODIFY `name` varchar(60) collate latin1_spanish_ci default NULL COMMENT 'Nombre de la Firma';

ALTER TABLE `signature` COMMENT 'Firmas de Cuentas de Correo Electronico';


UPDATE `db_version` SET `version_number` = '1.2.3';

COMMIT;
