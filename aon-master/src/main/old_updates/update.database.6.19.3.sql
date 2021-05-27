# Database: aon_master
# Version: Actualizacion de la version 6.19.3 a la version 6.19.4.
# Created by: girazu
# Creation Date: 20/12/2011 12:30
# Comentarios: SE HABIA OLVIDADO ESTA ACTUALIZACION EN LA UPDATE A LA VERSION 6.19.0


BEGIN;

ALTER TABLE `contract_batch` MODIFY `red_notify_id` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Identificador de la notificacion';
ALTER TABLE `contract_batch` MODIFY `red_response_id` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Identificador de la respuesta';

ALTER TABLE `salary_embargo` DROP FOREIGN KEY `FK_SALARY_EMBARGO_CONTRACT_EMBARGO`;
ALTER TABLE `salary_embargo` ADD CONSTRAINT `FK_SALARY_EMBARGO_CONTRACT_EMBARGO` FOREIGN KEY (`contract_embargo`) REFERENCES `contract_embargo` (`id`);

DROP TABLE `mod145_descendients`;
DROP TABLE `mod145_ascendants`;
DROP TABLE `mod145`;


UPDATE `db_version` SET `version_number` = '6.19.4';

COMMIT;
