# Database: aon_master
# Version: Actualizacion de la version 9.23.2 a la version 9.23.3
# Created by: eagirrezabal
# Creation Date: 06/03/2018 12:00

BEGIN;

ALTER TABLE `contract_batch_detail` MODIFY `action_type` varchar(3) COLLATE latin1_spanish_ci NOT NULL DEFAULT '' COMMENT 'Tipo de accion a realizar';
ALTER TABLE `contract_batch_detail` ADD `real_date` datetime DEFAULT NULL COMMENT 'Fecha real de la accion';

UPDATE `db_version` SET `version_number` = '9.23.3';

COMMIT;
