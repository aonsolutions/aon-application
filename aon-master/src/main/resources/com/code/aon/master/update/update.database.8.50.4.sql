# Database: aon_master
# Version: Actualizacion de la version 8.50.4 a la version 8.50.5.
# Created by: eagirrezabal

BEGIN;

ALTER TABLE `contract_batch_detail` ADD `action_type` varchar(2) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Tipo de accion a realizar';

ALTER TABLE `contract_batch_detail` ADD `leave_type` varchar(2) collate latin1_spanish_ci default NULL COMMENT 'Tipo de baja';

UPDATE `db_version` SET `version_number` = '8.50.5';

COMMIT;
