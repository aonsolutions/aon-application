# Database: aon_master
# Version: Actualizacion de la version 7.0.7 a la version 7.0.8.
# Created by: girazu
# Creation Date: 23/02/2012 10:05
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `project_reservation` MODIFY `code` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Localizador de la Reserva';

ALTER TABLE `purchase_detail` ADD `proposal_detail` int(4) default NULL COMMENT 'Identificador del Detalle de Solicitud' AFTER `status`;
ALTER TABLE `purchase_detail` ADD KEY `IDX_PURCHASE_DETAIL_PROPOSAL_DETAIL` (`proposal_detail`);
ALTER TABLE `purchase_detail` ADD CONSTRAINT `FK_PURCHASE_DETAIL_PROPOSAL_DETAIL` FOREIGN KEY (`proposal_detail`) REFERENCES `proposal_detail` (`id`);


UPDATE `db_version` SET `version_number` = '7.0.8';

COMMIT;
