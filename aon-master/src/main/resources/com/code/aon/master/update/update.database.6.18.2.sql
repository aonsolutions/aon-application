# Database: aon_master
# Version: Actualizacion de la version 6.18.2 a la version 6.18.3.
# Created by: eagirrezabal
# Creation Date: 17/10/2011 12:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE contract_leave_detail 
CHANGE `processed` `status` tinyint(2) NOT NULL default '0' COMMENT 'Indica el estado';

CREATE TABLE `leave_batch_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Archivo Adjunto',
  `leave_batch` int(4) NOT NULL default '0' COMMENT 'Identificador de la remesa',
  `mimeType` tinyint(2) default '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Archivo Adjunto',
  `scope` int(4) default NULL COMMENT 'Ambito del Archivo Adjunto',
  `attach_date` date default NULL COMMENT 'Fecha del Archivo Adjunto',
  PRIMARY KEY  (`id`),
  KEY `IDX_LEAVE_BATCH_ATTACH_CONTRACT` (`leave_batch`),
  KEY `IDX_LEAVE_BATCH_ATTACH_SCOPE` (`scope`),
  CONSTRAINT `FK_LEAVE_BATCH_ATTACH_LEAVE_BATCH` FOREIGN KEY (`leave_batch`) REFERENCES `leave_batch` (`id`),
  CONSTRAINT `FK_LEAVE_BATCH_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de remesas de partes IT';

UPDATE `db_version` SET `version_number` = '6.18.3';

COMMIT;
