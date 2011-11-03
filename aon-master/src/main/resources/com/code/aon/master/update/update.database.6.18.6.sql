# Database: aon_master
# Version: Actualizacion de la version 6.18.6 a la version 6.18.7.
# Created by: eagirrezabal
# Creation Date: 03/11/2011 10:07
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `fan_batch_detail` DROP FOREIGN KEY `FK_FAN_BATCH_DETAIL_ENTERPRISE`;
ALTER TABLE `fan_batch_detail` DROP INDEX `IDX_FAN_BATCH_DETAIL_ENTERPRISE`;

ALTER TABLE `fan_batch_detail` CHANGE `ccc` `enterprise_ccc` int(4) NOT NULL COMMENT 'Identificador unico del ccc';

ALTER TABLE `fan_batch_detail` ADD CONSTRAINT `FK_FAN_BATCH_DETAIL_ENTERPRISE_CCC` FOREIGN KEY (`enterprise_ccc`) REFERENCES `enterprise_ccc` (`id`);
ALTER TABLE `fan_batch_detail` ADD KEY `IDX_FAN_BATCH_DETAIL_ENTERPRISE_CCC` (`enterprise_ccc`);

ALTER TABLE `fan_batch` ADD `liquidation_type` tinyint(2) NOT NULL default '0' COMMENT 'Indica el tipo de liquidacion';

UPDATE `db_version` SET `version_number` = '6.18.7';

COMMIT;
