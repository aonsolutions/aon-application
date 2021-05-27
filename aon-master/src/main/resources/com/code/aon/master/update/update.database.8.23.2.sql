# Database: aon_master
# Version: Actualizacion de la version 8.23.2 a la version 8.23.3.
# Created by: girazu
# Creation Date: 18/05/2015 18:25

BEGIN;

ALTER TABLE `survey_response` DROP FOREIGN KEY `FK_SURVEY_RESPONSE_TARGET`;
ALTER TABLE `survey_response` DROP KEY `IDX_SURVEY_RESPONSE_TARGET`;
ALTER TABLE `survey_response` CHANGE `target` `registry` int(4) NOT NULL COMMENT 'Identificador del Registro que responde al Cuestionario';
ALTER TABLE `survey_response` ADD KEY `IDX_SURVEY_RESPONSE_REGISTRY` (`registry`);
ALTER TABLE `survey_response` ADD CONSTRAINT `FK_SURVEY_RESPONSE_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);


UPDATE `db_version` SET `version_number` = '8.23.3';

COMMIT;
