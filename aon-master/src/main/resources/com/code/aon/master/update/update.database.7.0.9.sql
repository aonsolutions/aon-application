# Database: aon_master
# Version: Actualizacion de la version 7.0.9 a la version 7.0.10.
# Created by: girazu
# Creation Date: 08/03/2012 12:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `project_reservation` ADD `no_show` tinyint(1) NOT NULL default '0' COMMENT 'No show' AFTER `crs_code`;
ALTER TABLE `project_reservation` ADD `advance` double(15,2) NOT NULL default '0.00' COMMENT 'Anticipo' AFTER `no_show`;
ALTER TABLE `project_reservation` ADD `advance_invoiced` tinyint(1) NOT NULL default '0' COMMENT 'Indica si el anticipo esta Facturado' AFTER `advance`;

ALTER TABLE `invoice` ADD `advance` tinyint(1) default '0' COMMENT 'Indica si la Factura es un anticipo' AFTER `rectification_invoice`;

ALTER TABLE `proposal` DROP FOREIGN KEY `FK_PROPOSAL_WORKPLACE_DEPARTMENT`;
ALTER TABLE `proposal` DROP INDEX `IDX_PROPOSAL_WORKPLACE_DEPARTMENT`;
ALTER TABLE `proposal` CHANGE `workplace_department` `department` int(4) default NULL COMMENT 'Identificador del Departamento';
UPDATE `proposal` SET `department` = (SELECT `department` FROM `workplace_department` WHERE `id` = `proposal`.`workplace_deparment`);
ALTER TABLE `proposal` ADD KEY `IDX_PROPOSAL_DEPARTMENT` (`department`);
ALTER TABLE `proposal` ADD CONSTRAINT `FK_PROPOSAL_DEPARTMENT` FOREIGN KEY (`department`) REFERENCES `department` (`id`);

ALTER TABLE `item_supplier` DROP KEY IDX_UNQ_ITEM_SUPPLIER;

CREATE TABLE `reservation_request` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `hotel` int(4) NOT NULL COMMENT 'Identificador del Hotel',
  `code` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Localizador',
  `start_date` date NOT NULL COMMENT 'Fecha de entrada',
  `end_date` date NOT NULL COMMENT 'Fecha de salida',
  `agency` int(4) default NULL COMMENT 'Identificador de la agencia de viajes',
  `company` int(4) default NULL COMMENT 'Identificador de la empresa',
  `booking_holder` tinyint(2) NOT NULL COMMENT 'Titular',
  `request_counter` tinyint(2) NOT NULL COMMENT 'Numero de solicitudes enviadas',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Solicitud esta activa o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_RESERVATION_REQUEST_DOMAIN` (`domain`),
  KEY `IDX_RESERVATION_REQUEST_HOTEL` (`hotel`),
  KEY `IDX_RESERVATION_REQUEST_AGENCY` (`agency`),
  KEY `IDX_RESERVATION_REQUEST_COMPANY` (`company`),
  CONSTRAINT `FK_RESERVATION_REQUEST_AGENCY` FOREIGN KEY (`agency`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_RESERVATION_REQUEST_COMPANY` FOREIGN KEY (`company`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_RESERVATION_REQUEST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RESERVATION_REQUEST_HOTEL` FOREIGN KEY (`hotel`) REFERENCES `hotel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Solicitud de Reservas';

CREATE TABLE `reservation_request_room` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `reservation_request` int(4) NOT NULL COMMENT 'Identificador de la Solicitud de Reserva',
  `room_index` tinyint(2) NOT NULL COMMENT 'Numero de Habitacion',
  `units` tinyint(2) NOT NULL COMMENT 'Numero de Habitaciones',
  `item` int(4) NOT NULL COMMENT 'Identificador del Tipo de Habitacion',
  `tariff` int(4) default NULL COMMENT 'Identificador de la Tarifa',
  `adults` smallint(2) default '0' COMMENT 'Numero de adultos',
  `children` smallint(2) default '0' COMMENT 'Numero de niños',
  `babies` smallint(2) default '0' COMMENT 'Numero de bebes',
  PRIMARY KEY  (`id`),
  KEY `IDX_RESERVATION_REQUEST_ROOM_DOMAIN` (`domain`),
  KEY `IDX_RESERVATION_REQUEST_ROOM_RESERVATION_REQUEST` (`reservation_request`),
  KEY `IDX_RESERVATION_REQUEST_ROOM_ITEM` (`item`),
  KEY `IDX_RESERVATION_REQUEST_ROOM_TARIFF` (`tariff`),
  CONSTRAINT `FK_RESERVATION_REQUEST_ROOM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RESERVATION_REQUEST_ROOM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_RESERVATION_REQUEST_ROOM_RESERVATION_REQUEST` FOREIGN KEY (`reservation_request`) REFERENCES `reservation_request` (`id`),
  CONSTRAINT `FK_RESERVATION_REQUEST_ROOM_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Habitaciones por Solicitud de Reserva';

CREATE TABLE `reservation_request_guest` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `reservation_request` int(4) NOT NULL COMMENT 'Identificador de la Solicitud de Reserva',
  `guest_index` tinyint(2) NOT NULL COMMENT 'Numero de Huesped',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre',
  `surname` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Apellidos',
  `email` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Email',
  `phone` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Telefono',
  `address` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Direccion',
  `zip` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo postal',
  `city` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Ciudad',
  `province` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Provincia',
  `country` varchar(2) collate latin1_spanish_ci default NULL COMMENT 'Pais',
  PRIMARY KEY  (`id`),
  KEY `IDX_RESERVATION_REQUEST_GUEST_DOMAIN` (`domain`),
  KEY `IDX_RESERVATION_REQUEST_GUEST_RESERVATION_REQUEST` (`reservation_request`),
  CONSTRAINT `FK_RESERVATION_REQUEST_GUEST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_RESERVATION_REQUEST_GUEST_RESERVATION_REQUEST` FOREIGN KEY (`reservation_request`) REFERENCES `reservation_request` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Huespedes por Solicitud de Reserva';


UPDATE `db_version` SET `version_number` = '7.0.10';

COMMIT;
