# Database: aon_master
# Version: Actualizacion de la version 6.15.0 a la version 6.16.0.
# Created by: eagirrezabal
# Creation Date: 03/09/2011 11:00
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

CREATE TABLE `fan_batch` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico de la remesa',
  `date` date NOT NULL COMMENT 'Fecha de la ultima remesa en la que fue incluido',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas del fichero fan';

CREATE TABLE `fan_batch_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del detalle',
  `fan_batch` int(4) NOT NULL COMMENT 'Identificador unico de la remesa',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador unico de la empresa',
  PRIMARY KEY  (`id`),
  KEY `IDX_FAN_BATCH_DETAIL_FAN_BATCH` (`fan_batch`),
  KEY `IDX_FAN_BATCH_DETAIL_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_FAN_BATCH_DETAIL_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`id`),
  CONSTRAINT `FK_FAN_BATCH_DETAIL_FAN_BATCH` FOREIGN KEY (`fan_batch`) REFERENCES `fan_batch` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las remesas del fichero fan';

ALTER TABLE irpf_data
ADD `fiscal_exclusion` tinyint(1) default '0' COMMENT 'Exclusion a la obligacion de tributar';


UPDATE `db_version` SET `version_number` = '6.16.0';

COMMIT;
