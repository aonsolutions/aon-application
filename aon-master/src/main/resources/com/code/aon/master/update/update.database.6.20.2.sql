# Database: aon_master
# Version: Actualizacion de la version 6.20.2 a la version 6.21.0.
# Created by: girazu
# Creation Date: 09/01/2012 13:25
# Comentarios: Ninguno


BEGIN;

ALTER TABLE `workplace` ADD `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito' AFTER `address`;
UPDATE `workplace` SET `scope` = (SELECT `scope` FROM `enterprise` WHERE `enterprise`.`registry` = `workplace`.`enterprise`);
ALTER TABLE `workplace` ADD KEY `IDX_WORKPLACE_SCOPE` (`scope`);
ALTER TABLE `workplace` ADD CONSTRAINT `FK_WORKPLACE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);

ALTER TABLE `hotel` ADD `customer` int(4) default NULL COMMENT 'Identificador del Cliente' AFTER `workplace`;
ALTER TABLE `hotel` ADD KEY `IDX_HOTEL_CUSTOMER` (`customer`);
ALTER TABLE `hotel` ADD CONSTRAINT `FK_HOTEL_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`);

ALTER TABLE `project_reservation_room` DROP `units`;
ALTER TABLE `project_reservation_room` DROP `adults`;
ALTER TABLE `project_reservation_room` DROP `children`;

CREATE TABLE `project_reservation_room_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `project_reservation_room` int(4) NOT NULL COMMENT 'Identificador de la Habitacion de la Reserva',
  `asset_activity` int(4) NOT NULL COMMENT 'Identificador de la Actividad de la Habitacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM` (`project_reservation_room`),
  KEY `IDX_PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY` (`asset_activity`),
  CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY` FOREIGN KEY (`asset_activity`) REFERENCES `asset_activity` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM` FOREIGN KEY (`project_reservation_room`) REFERENCES `project_reservation_room` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Habitacion por Reserva';

ALTER TABLE `project` ADD `reservation` tinyint(1) default '0' COMMENT 'Indica si se trata de una Reserva' AFTER `dossier`;

CREATE TABLE `room` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `asset` int(4) NOT NULL COMMENT 'Identificador del Activo',
  `hotel` int(4) NOT NULL COMMENT 'Identificador del Hotel',
  `item` int(4) NOT NULL COMMENT 'Identificador del Producto',
  PRIMARY KEY  (`id`),
  KEY `IDX_ROOM_ASSET` (`asset`),
  KEY `IDX_ROOM_HOTEL` (`hotel`),
  KEY `IDX_ROOM_ITEM` (`item`),
  CONSTRAINT `FK_ROOM_ASSET` FOREIGN KEY (`asset`) REFERENCES `asset` (`id`),
  CONSTRAINT `FK_ROOM_HOTEL` FOREIGN KEY (`hotel`) REFERENCES `hotel` (`id`),
  CONSTRAINT `FK_ROOM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Habitaciones de Hotel';

CREATE TABLE `feature` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre de la Caracteristica',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Caracteristicas';

CREATE TABLE `asset_feature` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `asset` int(4) NOT NULL COMMENT 'Identificador del Activo',
  `feature` int(4) NOT NULL COMMENT 'Identificador de la Caracteristica',
  PRIMARY KEY  (`id`),
  KEY `IDX_ASSET_FEATURE_ASSET` (`asset`),
  KEY `IDX_ASSET_FEATURE_FEATURE` (`feature`),
  CONSTRAINT `FK_ASSET_FEATURE_ASSET` FOREIGN KEY (`asset`) REFERENCES `asset` (`id`),
  CONSTRAINT `FK_ASSET_FEATURE_FEATURE` FOREIGN KEY (`feature`) REFERENCES `feature` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Caracteristicas por Activo';

CREATE TABLE `signature` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Firma',
  `signature` text collate latin1_spanish_ci NOT NULL COMMENT 'Texto de la Firma de la Cuenta de Correo',
  `source` tinyint(2) NOT NULL COMMENT 'Origen de la Firma',
  `source_id` int(4) NOT NULL COMMENT 'Identificador del origen de la Firma',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Firmas de Cuentas de Correo Electronico';

CREATE TABLE `mail_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Cuenta de Correo',  
  `email` varchar(256) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta de correo',
  `replyto_mail` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Email de Respuesta',
  `host` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Host del servidor de correo',  
  `protocol` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Protocolo utilizado (IMAP)',
  `incoming_host` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Host del correo entrante',
  `incoming_port` int(4) default NULL COMMENT 'Puerto del correo entrante',
  `incoming_ssl` bit(1) default NULL COMMENT 'Indica si tiene SSL el correo entrante',
  `outgoing_verification` bit(1) default NULL COMMENT 'Indica si hay autentificacion en el correo saliente',
  `outgoing_host` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Host del servidor de correo saliente',
  `outgoing_port` int(4) default NULL COMMENT 'Puerto del servidor de correo saliente',
  `outgoing_ssl` bit(1) default NULL COMMENT 'Indica si tiene SSL el correo saliente',
  `mail_username` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Nombre del usuario',
  `password` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Clave del usuario',
  `default_account` bit(1) default NULL COMMENT 'Indica si es la cuenta de correo por defecto',
  `draft_folder` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Ruta de Borrador',
  `sent_folder` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Ruta de Enviados',
  `trash_folder` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Ruta de Papelera',
  `spam_folder` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Ruta de Spam',
  `display_name` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Mostrar como',
  `signature` int(4) default NULL COMMENT 'Identificador de la Firma',
  `source` tinyint(2) NOT NULL COMMENT 'Origen de la Firma',
  `source_id` int(4) NOT NULL COMMENT 'Identificador del origen de la Firma',
  PRIMARY KEY  (`id`),
  KEY `IDX_MAIL_ACCOUNT_SIGNATURE` (`signature`),
  CONSTRAINT `FK_MAIL_ACCOUNT_SIGNATURE` FOREIGN KEY (`signature`) REFERENCES `signature` (`id`)  
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas de Correo Electronico';


UPDATE `db_version` SET `version_number` = '6.21.0';

COMMIT;
