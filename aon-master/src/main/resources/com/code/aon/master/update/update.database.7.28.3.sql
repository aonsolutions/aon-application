# Database: aon_master
# Version: Actualizacion de la version 7.28.3 a la version 7.29.0.
# Created by: girazu
# Creation Date: 31/01/2014 11:45
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `allotment` MODIFY `agency` int(4) default NULL COMMENT 'Identificador de la agencia de viajes';
ALTER TABLE `allotment` ADD `agency_group` int(4) default NULL COMMENT 'Identificador del Grupo de agencias' AFTER `agency`;
ALTER TABLE `allotment` ADD KEY `IDX_ALLOTMENT_AGENCY_GROUP` (`agency_group`);
ALTER TABLE `allotment` ADD CONSTRAINT `FK_ALLOTMENT_AGENCY_GROUP` FOREIGN KEY (`agency_group`) REFERENCES `invoicing_group` (`id`);


UPDATE `db_version` SET `version_number` = '7.29.0';

COMMIT;
