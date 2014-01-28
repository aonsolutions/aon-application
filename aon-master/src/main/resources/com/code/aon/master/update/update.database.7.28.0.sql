# Database: aon_master
# Version: Actualizacion de la version 7.28.0 a la version 7.28.1.
# Created by: girazu
# Creation Date: 28/01/2014 10:35
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

CREATE TABLE `booking` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation_room` int(4) NOT NULL COMMENT 'Identificador de la Habitacion de la Reserva',
  `hotel` int(4) NOT NULL COMMENT 'Identificador del Hotel',
  `agency` int(4) default NULL COMMENT 'Identificador de la Agencia',
  `item` int(4) NOT NULL COMMENT 'Identificador del Producto',
  `stay_date` date NOT NULL COMMENT 'Fecha de estancia',
  `stay_type` tinyint(2) NOT NULL default '0' COMMENT 'Indica si es una entrada, una salida o una permanencia',
  `guests` int(4) NOT NULL default '0' COMMENT 'Numero de Huespedes',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_UNQ_BOOKING_ROOM_DATE` (`project_reservation_room`,`stay_date`,`stay_type`),
  KEY `IDX_BOOKING_DOMAIN` (`domain`),
  KEY `IDX_BOOKING_PROJECT_RESERVATION_ROOM` (`project_reservation_room`),
  KEY `IDX_BOOKING_HOTEL` (`hotel`),
  KEY `IDX_BOOKING_AGENCY` (`agency`),
  KEY `IDX_BOOKING_ITEM` (`item`),
  CONSTRAINT `FK_BOOKING_AGENCY` FOREIGN KEY (`agency`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_BOOKING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_BOOKING_HOTEL` FOREIGN KEY (`hotel`) REFERENCES `hotel` (`id`),
  CONSTRAINT `FK_BOOKING_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_BOOKING_PROJECT_RESERVATION_ROOM` FOREIGN KEY (`project_reservation_room`) REFERENCES `project_reservation_room` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Booking de Hoteles';


UPDATE `db_version` SET `version_number` = '7.28.1';

COMMIT;
