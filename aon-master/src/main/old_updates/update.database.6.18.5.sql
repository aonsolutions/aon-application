# Database: aon_master
# Version: Actualizacion de la version 6.18.5 a la version 6.18.6.
# Created by: eagirrezabal
# Creation Date: 21/10/2011 11:22
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

CREATE TABLE `certifica2_batch_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Archivo Adjunto',
  `certifica2_batch` int(4) NOT NULL default '0' COMMENT 'Identificador de la remesa',
  `mimeType` tinyint(2) default '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Archivo Adjunto',
  `scope` int(4) default NULL COMMENT 'Ambito del Archivo Adjunto',
  `attach_date` date default NULL COMMENT 'Fecha del Archivo Adjunto',
  PRIMARY KEY  (`id`),
  KEY `IDX_CERTIFICA2_BATCH_ATTACH_CERTIFICA2_BATCH` (`certifica2_batch`),
  KEY `IDX_CERTIFICA2_BATCH_ATTACH_SCOPE` (`scope`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_ATTACH_CERTIFICA2_BATCH` FOREIGN KEY (`certifica2_batch`) REFERENCES `certifica2_batch` (`id`),
  CONSTRAINT `FK_CERTIFICA2_BATCH_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de remesas de certificados de empresa';


CREATE TABLE `fan_batch_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Archivo Adjunto',
  `fan_batch` int(4) NOT NULL default '0' COMMENT 'Identificador de la remesa',
  `mimeType` tinyint(2) default '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Archivo Adjunto',
  `scope` int(4) default NULL COMMENT 'Ambito del Archivo Adjunto',
  `attach_date` date default NULL COMMENT 'Fecha del Archivo Adjunto',
  PRIMARY KEY  (`id`),
  KEY `IDX_FAN_BATCH_ATTACH_FAN_BATCH` (`fan_batch`),
  KEY `IDX_FAN_BATCH_ATTACH_SCOPE` (`scope`),
  CONSTRAINT `FK_FAN_BATCH_ATTACH_FAN_BATCH` FOREIGN KEY (`fan_batch`) REFERENCES `fan_batch` (`id`),
  CONSTRAINT `FK_FAN_BATCH_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de remesas de mensajes fan de la s.s.';

CREATE TABLE `contract_batch_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Archivo Adjunto',
  `contract_batch` int(4) NOT NULL default '0' COMMENT 'Identificador de la remesa',
  `mimeType` tinyint(2) default '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Archivo Adjunto',
  `scope` int(4) default NULL COMMENT 'Ambito del Archivo Adjunto',
  `attach_date` date default NULL COMMENT 'Fecha del Archivo Adjunto',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_BATCH_ATTACH_CONTRACT_BATCH` (`contract_batch`),
  KEY `IDX_CONTRACT_BATCH_ATTACH_SCOPE` (`scope`),
  CONSTRAINT `FK_CONTRACT_BATCH_ATTACH_CONTRACT_BATCH` FOREIGN KEY (`contract_batch`) REFERENCES `contract_batch` (`id`),
  CONSTRAINT `FK_CONTRACT_BATCH_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de remesas de mensajes afi de la s.s.';

ALTER TABLE `fan_batch`
ADD `status` tinyint(2) NOT NULL default '0' COMMENT 'Indica el estado de la remesa';

ALTER TABLE `contract_batch`
ADD `status` tinyint(2) NOT NULL default '0' COMMENT 'Indica el estado de la remesa';

ALTER TABLE `leave_batch`
ADD `status` tinyint(2) NOT NULL default '0' COMMENT 'Indica el estado de la remesa';

UPDATE `db_version` SET `version_number` = '6.18.6';

COMMIT;
