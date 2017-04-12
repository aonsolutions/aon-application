# Database: aon_master
# Version: Actualizacion de la version 8.98.1 a la version 8.98.2.
# Created by: aibanez
# Creation Date: 12/04/2017 

BEGIN;

CREATE TABLE `data_response` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `number` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Numero de referencia',
  `issue_date` date DEFAULT NULL COMMENT 'Fecha',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_DATA_RESPONSE_DOMAIN` (`domain`),
  CONSTRAINT `FK_DATA_RESPONSE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='DATA RESPONSE';

CREATE TABLE `data_response_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `data_response` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de data_response',
  `data_variable` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Codigo de la variable',
  `value` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Valor de la variable',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_DATA_RESPONSE_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_DATA_RESPONSE_DETAIL_DATA_RESPONSE` (`data_response`),
  CONSTRAINT `FK_DATA_RESPONSE_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_DATA_RESPONSE_DETAIL_DATA_RESPONSE` FOREIGN KEY (`data_response`) REFERENCES `data_response` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='DATA RESPONSE DETAIL';

CREATE TABLE `data_attach` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `source` tinyint(2) DEFAULT NULL COMMENT 'Origen',
  `source_id` int(4) DEFAULT NULL COMMENT 'Identificador del origen',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Archivo Adjunto',
  `mimeType` tinyint(2) DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
  `drive_id` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Identificador de Google Drive',
  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY (`id`),
  KEY `IDX_DATA_ATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_DATA_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='DATA ATTACH';


UPDATE `db_version` SET `version_number` = '8.98.2';

COMMIT;
