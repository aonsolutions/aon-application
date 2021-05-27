# Database: aon_master
# Version: Actualizacion de la version 7.1.8 a la version 7.1.9.
# Created by: girazu
# Creation Date: 06/09/2012 18:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `project_reservation_room` ADD `room_code` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo de Habitacion en origen' AFTER `room_index`;

ALTER TABLE `project_reservation_service` ADD `service_code` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo del Servicio en origen' AFTER `service_index`;
ALTER TABLE `project_reservation_service` ADD `project_reservation_room` int(4) default NULL COMMENT 'Identificador de la Habitacion de la Reserva' AFTER `description`;

ALTER TABLE `project_reservation` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `project_reservation` MODIFY `creation_date` datetime default NULL COMMENT 'Fecha de creacion' AFTER `creation_user`;
ALTER TABLE `project_reservation` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `project_reservation` MODIFY `modification_date` datetime default NULL COMMENT 'Fecha de modificacion' AFTER `modification_user`;

ALTER TABLE `project_reservation` ADD KEY `IDX_PROJECT_RESERVATION_CRS_CODE` (`crs_code`);

ALTER TABLE `reservation_request_room` ADD KEY `IDX_RESERVATION_REQUEST_ROOM_CRS_CODE` (`crs_code`);

ALTER TABLE `proposal` ADD `item_return` tinyint(1) default '0' COMMENT 'Indica si es una devolucion' AFTER `remarks`;
ALTER TABLE `proposal` ADD `transfer_status` tinyint(2) default '0' COMMENT 'Indica si es un traspaso y su estado';
ALTER TABLE `proposal` ADD `transfer_proposal` int(4) default NULL COMMENT 'Identificador de la Solicitud de traspaso vinculada';
ALTER TABLE `proposal` ADD KEY `IDX_PROPOSAL_TRANSFER_PROPOSAL` (`transfer_proposal`);
ALTER TABLE `proposal` ADD CONSTRAINT `FK_PROPOSAL_TRANSFER_PROPOSAL` FOREIGN KEY (`transfer_proposal`) REFERENCES `proposal` (`id`);

ALTER TABLE `bonus_concept` ADD `type` tinyint(2) default NULL COMMENT 'Tipo de Bonificacion Salarial'; 

CREATE TABLE `item_addinfo` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `item` int(4) NOT NULL COMMENT 'Identificador de Articulo',
  `attribute` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Atributo adicional',
  `value` varchar(128) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Valor del atributo adicional',
  `value_date` date NOT NULL COMMENT 'Fecha del valor del atributo',
  PRIMARY KEY (`id`),
  KEY `IDX_ITEM_ADDINFO_DOMAIN` (`domain`),
  KEY `IDX_ITEM_ADDINFO_ITEM` (`item`),
  CONSTRAINT `FK_ITEM_ADDINFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_ITEM_ADDINFO_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Informacion adicional del Articulo';

DROP TABLE `ec_catalogue`;
DROP TABLE `ec_config`;
DROP TABLE `ec_offer_pay_info`;
DROP TABLE `ec_paymethod`;
DROP TABLE `ec_target`;


UPDATE `db_version` SET `version_number` = '7.1.9';

COMMIT;
