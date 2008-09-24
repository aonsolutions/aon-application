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
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Cuestionario',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si el Cuestionario esta activa o inactiva',
  `creationDate` datetime NOT NULL COMMENT 'Fecha de cracion del Cuestionario',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripción del Cuestionario',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=COMPACT COMMENT='Questionario';

CREATE TABLE `survey_question` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Pregunta del Cuestionario',
  `survey` int(4) NOT NULL COMMENT 'Identificador del Cuestionario',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `position` int(11) default NULL COMMENT 'Posicion de la Pregunta dentro del Cuestionario',
  PRIMARY KEY  (`id`),
  KEY `survey` (`survey`),
  KEY `question` (`question`),
  CONSTRAINT `FK_SURVERY_QUESTION_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`),
  CONSTRAINT `FK_SURVERY_QUESTION_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=COMPACT COMMENT='Survey Question';

CREATE TABLE `survey_workflow` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Workflow del Cuestionario',
  `questionValue` int(4) default NULL COMMENT 'Identicador del Valor de la Pregunta',
  `surveyQuestion` int(4) NOT NULL COMMENT 'Identificador de la Pregunta del Cuestionario',
  `nextSurveyQuestion` int(4) NOT NULL COMMENT 'Identificador de la siguiente Pregunta del Cuestionario',
  PRIMARY KEY  (`id`),
  KEY `questionValue` (`questionValue`),
  KEY `surveyQuestion` (`surveyQuestion`),
  KEY `nextSurveyQuestion` (`nextSurveyQuestion`),
  CONSTRAINT `FK_SURVERY_WORKFLOW_NEXT_SURVERY_QUESTION` FOREIGN KEY (`nextSurveyQuestion`) REFERENCES `survey_question` (`id`),
  CONSTRAINT `FK_SURVERY_WORKFLOW_QUESTION_VALUE` FOREIGN KEY (`questionValue`) REFERENCES `question_value` (`id`),
  CONSTRAINT `FK_SURVERY_WORKFLOW_SURVERY_QUESTION` FOREIGN KEY (`surveyQuestion`) REFERENCES `survey_question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=COMPACT COMMENT='Survey Workflow';

CREATE TABLE `survey_response` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Respuesta del Cuestionario',
  `creationDate` datetime NOT NULL COMMENT 'Fecha de la creacion en el sistema de la Respuesta del Cuestionario',
  `response_date` datetime NOT NULL COMMENT 'Fecha de la Respuesta del Cuestionario',
  `survey` int(4) NOT NULL COMMENT 'Identificador del Cuestionario',
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
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Detalle de la Respuesta del Cuestionario',
  `value_text` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Valor de tipo Texto',
  `value_date` datetime default NULL COMMENT 'Valor de tipo Fecha',
  `value_number` double(15,3) default NULL COMMENT 'Valor de tipo Numerico',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `surveyResponse` int(4) NOT NULL COMMENT 'Identificador de la Respuesta del Cuestionario',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=COMPACT COMMENT='Survy Response Detail';

CREATE TABLE `target_profile` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Perfil de Cliente Potencial',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `last_update` datetime NOT NULL COMMENT 'Fecha de la ultima modificación del Perfil del Cliente Potencial',
  `question` int(4) NOT NULL COMMENT 'Identificador de la Pregunta',
  `value_text` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Valor de tipo Texto',
  `value_number` double(15,3) default NULL COMMENT 'Valor de tipo Numerico',
  `value_date` datetime default NULL COMMENT 'Valor de tipo Fecha',
  PRIMARY KEY  (`id`),
  KEY `question` (`question`),
  KEY `target` (`target`),
  CONSTRAINT `FK_TARGET_PROFILE_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_TARGET_PROFILE_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=COMPACT COMMENT='Perfil de Cliente Potencial';

CREATE TABLE `mk_campaign` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Campaña',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Campaña esta activa o inactiva',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripción de la Campaña',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=COMPACT COMMENT='Campaña';

CREATE TABLE `mk_action` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la Acción de la Campaña',
  `campaign` int(11) NOT NULL COMMENT 'Identificador de la Campaña',
  `media_type` int(4) NOT NULL COMMENT 'Tipo de Contacto de la Acción',
  `start_date` datetime NOT NULL COMMENT 'Fecha de Inicio',
  `end_date` datetime default NULL COMMENT 'Fecha de Finalización',
  `survey` int(4) default NULL COMMENT 'Identificador del Questionario',
  PRIMARY KEY  (`id`),
  KEY `survey` (`survey`),
  KEY `campaign` (`campaign`),
  CONSTRAINT `FK_MK_ACTION_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`),
  CONSTRAINT `FK_MK_ACTION_MK_CAMPAIGN` FOREIGN KEY (`campaign`) REFERENCES `mk_campaign` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=COMPACT COMMENT='Acción de la Campaña';

CREATE TABLE `mk_action_target` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Cliente Potencial de la Acción de Campaña',
  `action` int(4) NOT NULL COMMENT 'Identificador de la Acción de Camapaña',
  `target` int(4) NOT NULL COMMENT 'Identicador del Cliente Potencial',
  PRIMARY KEY  (`id`),
  KEY `action` (`action`),
  KEY `target` (`target`),
  CONSTRAINT `MK_ACTION_TARGET_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `MK_ACTION_TARGET_MK_ACTION` FOREIGN KEY (`action`) REFERENCES `mk_action` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=COMPACT COMMENT='Cliente Potencial de la Acción de Campaña';

CREATE TABLE `mk_action_target_log` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador Unico del Log de la Acción de un Cliente Potencial',
  `action` int(4) NOT NULL COMMENT 'Identificador de la Acción de la Campaña',
  `target` int(11) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `status` tinyint(2) NOT NULL COMMENT 'Estado del Log de la Acción de un Cliente Potencial',
  `survey_response` int(4) default NULL COMMENT 'Identificador de la Respuesta de un Cuestionario',
  PRIMARY KEY  (`id`),
  KEY `action` (`action`),
  KEY `target` (`target`),
  KEY `survey_response` (`survey_response`),
  CONSTRAINT `MK_ACTION_TARGET_LOG_SURVEY_RESPONSE` FOREIGN KEY (`survey_response`) REFERENCES `survey_response` (`id`),
  CONSTRAINT `MK_ACTION_TARGET_LOG_MK_ACTION` FOREIGN KEY (`action`) REFERENCES `mk_action` (`id`),
  CONSTRAINT `MK_ACTION_TARGET_LOG_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci ROW_FORMAT=COMPACT COMMENT='Log de la Acción de un Cliente Potencial';