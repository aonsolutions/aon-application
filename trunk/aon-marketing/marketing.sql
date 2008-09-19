CREATE TABLE `question` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Pregunta',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Pregunta esta activa o no',
  `question_text` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Texto de la Pregunta',
  `type` tinyint(2) NOT NULL COMMENT 'Tipo de Pregunta',
  `argument` tinytext collate latin1_spanish_ci COMMENT 'Argumentación de la Pregunta',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=COMPACT COMMENT='Questions';

CREATE TABLE `question_value` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Valor de Pregunta',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `value_text` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Valor de tipo Texto',
  `value_number` double(15,3) default NULL COMMENT 'Valor de tipo Numerico',
  `value_date` datetime default NULL COMMENT 'Valor de tipo Fecha',
  PRIMARY KEY  (`id`),
  KEY `question` (`question`),
  CONSTRAINT `FK_QUESTION_VALUE_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=COMPACT COMMENT='Values of the Question';

CREATE TABLE `survey` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Campaña',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Campaña esta activa o inactiva',
  `createDate` datetime NOT NULL COMMENT 'Fecha de cracion de la Campaña',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripción de la Campaña',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=COMPACT COMMENT='Survey';

CREATE TABLE `survey_question` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unicdo de la Pregunta de la Campaña',
  `survey` int(4) NOT NULL COMMENT 'Identificador de la Campaaña',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `position` int(11) default NULL COMMENT 'Posicion de la Pregunta dentro de la Campaña',
  PRIMARY KEY  (`id`),
  KEY `survey` (`survey`),
  KEY `question` (`question`),
  CONSTRAINT `FK_SURVERY_QUESTION_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`),
  CONSTRAINT `FK_SURVERY_QUESTION_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=COMPACT COMMENT='Survey Question';

CREATE TABLE `survey_workflow` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Workflow de Campaña',
  `questionValue` int(4) default NULL COMMENT 'Identicador del Valor de la Pregunta',
  `surveyQuestion` int(4) NOT NULL COMMENT 'Identificador de la Pregunta de la Campaña',
  `nextSurveyQuestion` int(4) NOT NULL COMMENT 'Identificador de la siguiente Pregunta de la Campaña',
  PRIMARY KEY  (`id`),
  KEY `questionValue` (`questionValue`),
  KEY `surveyQuestion` (`surveyQuestion`),
  KEY `nextSurveyQuestion` (`nextSurveyQuestion`),
  CONSTRAINT `FK_SURVERY_WORKFLOW_NEXT_SURVERY_QUESTION` FOREIGN KEY (`nextSurveyQuestion`) REFERENCES `survey_question` (`id`),
  CONSTRAINT `FK_SURVERY_WORKFLOW_QUESTION_VALUE` FOREIGN KEY (`questionValue`) REFERENCES `question_value` (`id`),
  CONSTRAINT `FK_SURVERY_WORKFLOW_SURVERY_QUESTION` FOREIGN KEY (`surveyQuestion`) REFERENCES `survey_question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=COMPACT COMMENT='Survey Workflow';

CREATE TABLE `survey_response` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Respuesta de la Campaña',
  `creationDate` datetime NOT NULL COMMENT 'Fecha de la creacion en el sistema de la Respuesta de la Campaña',
  `response_date` datetime NOT NULL COMMENT 'Fecha de la Respuesta de la Campaña',
  `survey` int(4) NOT NULL COMMENT 'Identificador de la Campaña',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `user` int(4) NOT NULL COMMENT 'Identificador del Usuario',
  PRIMARY KEY  (`id`),
  KEY `survey` (`survey`),
  KEY `target` (`target`),
  KEY `user` (`user`),
  CONSTRAINT `FK_SURVERY_RESPONSE_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`),
  CONSTRAINT `FK_SURVERY_RESPONSE_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_SURVERY_RESPONSE_USER` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=COMPACT COMMENT='Survey Reponse';

CREATE TABLE `survey_response_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle de la Respuesta a la Camapaña',
  `value_text` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Valor de tipo Texto',
  `value_date` datetime default NULL COMMENT 'Valor de tipo Fecha',
  `value_number` double(15,3) default NULL COMMENT 'Valor de tipo Numerico',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `surveyResponse` int(4) NOT NULL COMMENT 'Identificador de la Respuesta de la Campaña',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=COMPACT COMMENT='Survy Response Detail';