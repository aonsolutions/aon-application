# Database: aon_master
# Version: Actualizacion de la version 1.9.0 a la version 1.10.0
# Created by: girazu
# Creation Date: 24/09/2008 15:45
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



CREATE TABLE `question` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Pregunta esta activa o no',
  `question_text` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Texto de la Pregunta',
  `type` tinyint(2) NOT NULL COMMENT 'Tipo de Pregunta',
  `argument` tinytext collate latin1_spanish_ci COMMENT 'Argumentacion de la Pregunta',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Preguntas';

CREATE TABLE `question_value` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `value_text` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Valor de tipo texto',
  `value_number` double(15,3) default NULL COMMENT 'Valor de tipo numerico',
  `value_date` datetime default NULL COMMENT 'Valor de tipo fecha',
  PRIMARY KEY (`id`),
  KEY `question` (`question`),
  CONSTRAINT `FK_QUESTION_VALUE_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Valores de Preguntas';

CREATE TABLE `survey` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si el Cuestionario esta activa o no',
  `creationDate` datetime NOT NULL COMMENT 'Fecha de creacion del Cuestionario',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Cuestionario',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuestionarios';

CREATE TABLE `survey_question` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `survey` int(4) NOT NULL COMMENT 'Identificador del Cuestionario',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `position` int(11) default NULL COMMENT 'Posicion de la Pregunta dentro del Cuestionario',
  PRIMARY KEY (`id`),
  KEY `survey` (`survey`),
  KEY `question` (`question`),
  CONSTRAINT `FK_SURVEY_QUESTION_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`),
  CONSTRAINT `FK_SURVEY_QUESTION_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Preguntas de Cuestionarios';

CREATE TABLE `survey_workflow` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `questionValue` int(4) default NULL COMMENT 'Identificador del Valor de la Pregunta',
  `surveyQuestion` int(4) NOT NULL COMMENT 'Identificador de la Pregunta del Cuestionario',
  `nextSurveyQuestion` int(4) NOT NULL COMMENT 'Identificador de la siguiente Pregunta del Cuestionario',
  PRIMARY KEY (`id`),
  KEY `questionValue` (`questionValue`),
  KEY `surveyQuestion` (`surveyQuestion`),
  KEY `nextSurveyQuestion` (`nextSurveyQuestion`),
  CONSTRAINT `FK_SURVEY_WORKFLOW_NEXT_SURVEY_QUESTION` FOREIGN KEY (`nextSurveyQuestion`) REFERENCES `survey_question` (`id`),
  CONSTRAINT `FK_SURVEY_WORKFLOW_QUESTION_VALUE` FOREIGN KEY (`questionValue`) REFERENCES `question_value` (`id`),
  CONSTRAINT `FK_SURVEY_WORKFLOW_SURVEY_QUESTION` FOREIGN KEY (`surveyQuestion`) REFERENCES `survey_question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Secuencias de Cuestionarios';

CREATE TABLE `survey_response` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `creationDate` datetime NOT NULL COMMENT 'Fecha de la creacion en el sistema de la Respuesta del Cuestionario',
  `response_date` datetime NOT NULL COMMENT 'Fecha de la Respuesta del Cuestionario',
  `survey` int(4) NOT NULL COMMENT 'Identificador del Cuestionario',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `user` int(4) NOT NULL COMMENT 'Identificador del Usuario',
  PRIMARY KEY (`id`),
  KEY `survey` (`survey`),
  KEY `target` (`target`),
  KEY `user` (`user`),
  CONSTRAINT `FK_SURVEY_RESPONSE_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`),
  CONSTRAINT `FK_SURVEY_RESPONSE_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_SURVEY_RESPONSE_USER` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Respuestas de Cuestionarios';

CREATE TABLE `survey_response_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `value_text` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Valor de tipo texto',
  `value_number` double(15,3) default NULL COMMENT 'Valor de tipo numerico',
  `value_date` datetime default NULL COMMENT 'Valor de tipo fecha',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `surveyResponse` int(4) NOT NULL COMMENT 'Identificador de la Respuesta del Cuestionario',
  PRIMARY KEY (`id`),
  KEY `question` (`question`),
  KEY `surveyResponse` (`surveyResponse`),
  CONSTRAINT `FK_SURVEY_RESPONSE_DETAIL_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`),
  CONSTRAINT `FK_SURVEY_RESPONSE_DETAIL_SURVEY_RESPONSE` FOREIGN KEY (`surveyResponse`) REFERENCES `survey_response` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Respuestas de Cuestionarios';

CREATE TABLE `target_profile` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `last_update` datetime NOT NULL COMMENT 'Fecha de la ultima modificacion del Perfil del Cliente Potencial',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `value_text` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Valor de tipo texto',
  `value_number` double(15,3) default NULL COMMENT 'Valor de tipo numerico',
  `value_date` datetime default NULL COMMENT 'Valor de tipo fecha',
  PRIMARY KEY (`id`),
  KEY `question` (`question`),
  KEY `target` (`target`),
  CONSTRAINT `FK_TARGET_PROFILE_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_TARGET_PROFILE_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Perfiles de Clientes Potenciales';

CREATE TABLE `mk_campaign` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Campaña esta activa o no',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Campaña',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Campañas de Marketing';

CREATE TABLE `mk_action` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `campaign` int(11) NOT NULL COMMENT 'Identificador de la Campaña',
  `media_type` int(4) NOT NULL COMMENT 'Tipo de contacto de la Accion',
  `start_date` datetime NOT NULL COMMENT 'Fecha de inicio',
  `end_date` datetime default NULL COMMENT 'Fecha de finalizacion',
  `survey` int(4) default NULL COMMENT 'Identificador del Cuestionario',
  PRIMARY KEY (`id`),
  KEY `survey` (`survey`),
  KEY `campaign` (`campaign`),
  CONSTRAINT `FK_MK_ACTION_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`),
  CONSTRAINT `FK_MK_ACTION_MK_CAMPAIGN` FOREIGN KEY (`campaign`) REFERENCES `mk_campaign` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Acciones de Marketing';

CREATE TABLE `mk_action_target` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `action` int(4) NOT NULL COMMENT 'Identificador de la Accion',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  PRIMARY KEY (`id`),
  KEY `action` (`action`),
  KEY `target` (`target`),
  CONSTRAINT `MK_ACTION_TARGET_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `MK_ACTION_TARGET_MK_ACTION` FOREIGN KEY (`action`) REFERENCES `mk_action` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Clientes Potenciales de la Accion de Marketing';

CREATE TABLE `mk_action_target_log` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `action` int(4) NOT NULL COMMENT 'Identificador de la Accion',
  `target` int(11) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `status` tinyint(2) NOT NULL COMMENT 'Estado del Log de la Accion sobre el Cliente Potencial',
  `survey_response` int(4) default NULL COMMENT 'Identificador de la Respuesta del Cuestionario',
  PRIMARY KEY (`id`),
  KEY `action` (`action`),
  KEY `target` (`target`),
  KEY `survey_response` (`survey_response`),
  CONSTRAINT `MK_ACTION_TARGET_LOG_SURVEY_RESPONSE` FOREIGN KEY (`survey_response`) REFERENCES `survey_response` (`id`),
  CONSTRAINT `MK_ACTION_TARGET_LOG_MK_ACTION` FOREIGN KEY (`action`) REFERENCES `mk_action` (`id`),
  CONSTRAINT `MK_ACTION_TARGET_LOG_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Log de Acciones sobre Clientes Potenciales';


UPDATE `db_version` SET `version_number` = '1.10.0';

COMMIT;
