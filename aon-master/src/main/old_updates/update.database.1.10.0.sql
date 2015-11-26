# Database: aon_master
# Version: Actualizacion de la version 1.10.0 a la version 1.10.1
# Created by: atellitu
# Creation Date: 06/10/2008 16:57
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



DROP TABLE `mk_action_target_log`;

ALTER TABLE `survey_response` ADD `campaign_action` int(4) default NULL COMMENT 'Identificador de la Accion de la Campaña';

ALTER TABLE `survey_response` ADD KEY `campaign_action` (`campaign_action`);

ALTER TABLE `survey_response` ADD CONSTRAINT `FK_SURVERY_RESPONSE_MK_ACTION` FOREIGN KEY (`campaign_action`) REFERENCES `mk_action` (`id`);

ALTER TABLE `mk_action_target` ADD `status` tinyint(2) NOT NULL COMMENT 'Estado del Cliente Potencial de la Accion de Campaña';

ALTER TABLE `mk_action_target` ADD `survey_response` int(4) default NULL COMMENT 'Identificador de la Respuesta de Cuestionario';

ALTER TABLE `mk_action_target` ADD KEY `survey_response` (`survey_response`);

ALTER TABLE `mk_action_target` ADD CONSTRAINT `FK_ACTION_TARGET_SURVERY_RESPONSE` FOREIGN KEY (`survey_response`) REFERENCES `survey_response` (`id`);

ALTER TABLE `survey_workflow` ADD `operator` tinyint(2) default NULL COMMENT 'Operador a utilizar con el Valor';

ALTER TABLE `survey_workflow` ADD `value_text` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Valor de tipo texto';

ALTER TABLE `survey_workflow` ADD `value_number` double(15,3) default NULL COMMENT 'Valor de tipo numerico';

ALTER TABLE `survey_workflow` ADD `value_date` datetime default NULL COMMENT 'Valor de tipo fecha';


UPDATE `db_version` SET `version_number` = '1.10.1';

COMMIT;
