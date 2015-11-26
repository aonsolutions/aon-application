# Database: aon_master
# Version: Actualizacion de la version 5.2.1 a la version 5.2.2.
# Created by: girazu
# Creation Date: 09/06/2010 10:04
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `commercial_term` ADD `line` smallint(2) default '1' COMMENT 'Numero de linea de Condicion' AFTER `id`;

ALTER TABLE `target` ADD `tariff` int(4) default NULL COMMENT 'Tarifa asociada al Cliente Potencial' AFTER `registry`;

ALTER TABLE `target` ADD KEY `IDX_TARGET_TARIFF` (`tariff`);

ALTER TABLE `target` ADD CONSTRAINT `FK_TARGET_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`);

ALTER TABLE `commercial_tracking` ADD `end_date` date default NULL COMMENT 'Fecha de cierre del Seguimiento Comercial';

ALTER TABLE `commercial_tracking` ADD `offer` int(4) default NULL COMMENT 'Identificador del Presupuesto';

ALTER TABLE `commercial_tracking` ADD KEY `IDX_COMMERCIAL_TRACKING_OFFER` (`offer`);

ALTER TABLE `commercial_tracking` ADD CONSTRAINT `FK_COMMERCIAL_TRACKING_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`);


UPDATE `db_version` SET `version_number` = '5.2.2';

COMMIT;
