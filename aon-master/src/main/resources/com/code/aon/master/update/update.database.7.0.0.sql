# Database: aon_master
# Version: Actualizacion de la version 7.0.0 a la version 7.0.1.
# Created by: girazu
# Creation Date: 12/01/2012 17:00
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `tariff` ADD `code` varchar(8) collate latin1_spanish_ci default NULL COMMENT 'Codigo de la Tarifa' AFTER `domain`;

ALTER TABLE `project_reservation` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion' AFTER `creation_date`;
ALTER TABLE `project_reservation` ADD `crs` tinyint(1) NOT NULL default '0' COMMENT 'Indica si el origen de la Reserva es un CRS' AFTER `remarks`;
ALTER TABLE `project_reservation` DROP FOREIGN KEY `FK_PROJECT_RESERVATION_TARIFF`;
ALTER TABLE `project_reservation` DROP KEY `IDX_PROJECT_RESERVATION_TARIFF`;
ALTER TABLE `project_reservation` DROP `tariff`;

ALTER TABLE `project_reservation_room` ADD `tariff` int(4) default NULL COMMENT 'Identificador de la Tarifa';
ALTER TABLE `project_reservation_room` ADD KEY `IDX_PROJECT_RESERVATION_ROOM_TARIFF` (`tariff`);
ALTER TABLE `project_reservation_room` ADD CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`);
ALTER TABLE `project_reservation_room` ADD `adults` smallint(2) default '0' COMMENT 'Numero de adultos';
ALTER TABLE `project_reservation_room` ADD `children` smallint(2) default '0' COMMENT 'Numero de niños';

ALTER TABLE `project_reservation_service` DROP `effective_date`;
ALTER TABLE `project_reservation_service` DROP `quantity`;
ALTER TABLE `project_reservation_service` DROP `price`;
ALTER TABLE `project_reservation_service` DROP `taxable_base`;

CREATE TABLE `project_reservation_service_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation_service` int(4) NOT NULL COMMENT 'Identificador del Servicio de la Reserva',
  `project_reservation_room_detail` int(4) default NULL COMMENT 'Identificador del Detalle de Habitacion de la Reserva',
  `effective_date` date NOT NULL COMMENT 'Fecha de efecto',
  `quantity` double(15,2) default '0.00' COMMENT 'Cantidad',
  `price` double(15,2) default '0.00' COMMENT 'Precio',
  `taxable_base` double(15,2) default '0.00' COMMENT 'Base imponible',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROJECT_RESERVATION_SERVICE_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_PRJ_RESERVATION_SERVICE_DETAIL_PRJ_RESERVATION_SERVICE` (`project_reservation_service`),
  KEY `IDX_PRJ_RESERVATION_SERVICE_DETAIL_PRJ_RESERVATION_ROOM_DETAIL` (`project_reservation_room_detail`),
  CONSTRAINT `FK_PROJECT_RESERVATION_SERVICE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PRJ_RESERVATION_SERVICE_DETAIL_PRJ_RESERVATION_SERVICE` FOREIGN KEY (`project_reservation_service`) REFERENCES `project_reservation_service` (`id`),
  CONSTRAINT `FK_PRJ_RESERVATION_SERVICE_DETAIL_PRJ_RESERVATION_ROOM_DETAIL` FOREIGN KEY (`project_reservation_room_detail`) REFERENCES `project_reservation_room_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Servicio por Reserva';


DROP TABLE `room`;
CREATE TABLE `room` (
  `asset` int(4) NOT NULL COMMENT 'Identificador del Activo',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `hotel` int(4) NOT NULL COMMENT 'Identificador del Hotel',
  `item` int(4) NOT NULL COMMENT 'Identificador del Producto',
  PRIMARY KEY  (`asset`),
  KEY `IDX_ROOM_DOMAIN` (`domain`),
  KEY `IDX_ROOM_HOTEL` (`hotel`),
  KEY `IDX_ROOM_ITEM` (`item`),
  CONSTRAINT `FK_ROOM_ASSET` FOREIGN KEY (`asset`) REFERENCES `asset` (`id`),
  CONSTRAINT `FK_ROOM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ROOM_HOTEL` FOREIGN KEY (`hotel`) REFERENCES `hotel` (`id`),
  CONSTRAINT `FK_ROOM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Habitaciones de Hotel';

ALTER TABLE `series` CHANGE `id` `code` varchar(5) COLLATE 'latin1_spanish_ci' NOT NULL COMMENT 'Serie';
ALTER TABLE `series` DROP PRIMARY KEY;
ALTER TABLE `series` ADD `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico' FIRST, ADD PRIMARY KEY (`id`);
ALTER TABLE `series` ADD `scope` int(4) default NULL COMMENT 'Identificador del Ambito' AFTER `domain`;
UPDATE `series` SET `scope` = (SELECT MIN(`id`) FROM `scope`);
ALTER TABLE `series` MODIFY `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito';
ALTER TABLE `series` ADD INDEX `IDX_SERIES_SCOPE` (`scope`);
ALTER TABLE `series` ADD CONSTRAINT `FK_SERIES_SCOPE` FOREIGN KEY (`scope`)  REFERENCES `scope` (`id`);


UPDATE `db_version` SET `version_number` = '7.0.1';

COMMIT;
