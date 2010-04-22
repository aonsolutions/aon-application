# Database: aon_master
# Version: Actualizacion de la version 1.6.3 a la version 1.7.0
# Created by: girazu
# Creation Date: 06/06/2008 13:17
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



CREATE TABLE `message_content` (
  `id` int(4) NOT NULL COMMENT 'Identificador unico',
  `content` text collate latin1_spanish_ci NOT NULL COMMENT 'Contenido del Mensaje',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contenido de Mensajes';

CREATE TABLE `message_log` (
  `id` int(4) NOT NULL COMMENT 'Identificador unico',
  `message_id` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador del Mensaje para el servidor de Esendex',
  `message_content` int(4) default NULL COMMENT 'Identificador del Contenido del Mensaje',
  `recipient` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Destinatario del Mensaje',
  `type` varchar(10) collate latin1_spanish_ci default NULL COMMENT 'Tipo de Mensaje',
  `sent_date` datetime NOT NULL COMMENT 'Fecha y hora de envio del Mensaje',
  `message_parts` tinyint(2) NOT NULL default '1' COMMENT 'Numero de partes que componen el Mensaje',
  `username` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Usuario que envia el mensaje',
  PRIMARY KEY  (`id`),
  KEY `message_content` (`message_content`),
  CONSTRAINT `message_log_fk1` FOREIGN KEY (`message_content`) REFERENCES `message_content` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Log de Mensajes';

DROP TABLE `signature`;

DROP TABLE `mail_account`;


UPDATE `db_version` SET `version_number` = '1.7.0';

COMMIT;
