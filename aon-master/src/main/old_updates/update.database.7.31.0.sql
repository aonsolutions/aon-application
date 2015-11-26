# Database: aon_master
# Version: Actualizacion de la version 7.31.0 a la version 7.31.1.
# Created by: girazu
# Creation Date: 18/03/2014 18:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

CREATE TABLE `cra_batch` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `date` datetime default NULL COMMENT 'Fecha de creacion',
  `status` tinyint(2) NOT NULL default '0' COMMENT 'Indica el estado de la Remesa',
  `communication_id` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Identificador resultante de la comunicacion',
  `income_file` mediumblob COMMENT 'Archivo respuesta en binario',
  `income_file_date` datetime default NULL COMMENT 'Fecha de respuesta',
  `outcome_file` mediumblob COMMENT 'Archivo Adjunto en binario',
  `outcome_file_date` datetime default NULL COMMENT 'Fecha de creacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_CRA_BATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_CRA_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas del fichero cra';

CREATE TABLE `cra_batch_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `cra_batch` int(4) NOT NULL COMMENT 'Identificador unico de la Remesa',
  `enterprise_ccc` int(4) NOT NULL COMMENT 'Identificador unico del ccc',
  PRIMARY KEY  (`id`),
  KEY `IDX_CRA_BATCH_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_CRA_BATCH_DETAIL_CRA_BATCH` (`cra_batch`),
  KEY `IDX_CRA_BATCH_DETAIL_ENTERPRISE_CCC` (`enterprise_ccc`),
  CONSTRAINT `FK_CRA_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CRA_BATCH_DETAIL_CRA_BATCH` FOREIGN KEY (`cra_batch`) REFERENCES `cra_batch` (`id`),
  CONSTRAINT `FK_CRA_BATCH_DETAIL_ENTERPRISE_CCC` FOREIGN KEY (`enterprise_ccc`) REFERENCES `enterprise_ccc` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las Remesas del fichero cra';


UPDATE `db_version` SET `version_number` = '7.31.1';

COMMIT;
