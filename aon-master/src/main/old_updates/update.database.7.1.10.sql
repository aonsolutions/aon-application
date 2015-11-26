# Database: aon_master
# Version: Actualizacion de la version 7.1.10 a la version 7.1.11.
# Created by: ecastellano
# Creation Date: 27/10/2012 19:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

CREATE TABLE `fs_batch` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `date` date NOT NULL COMMENT 'Fecha de creacion del Lote',
  `administration` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Administracion',
  `year` int(4) NOT NULL COMMENT 'Ejercicio del Lote',
  `period` tinyint(2) DEFAULT '0' COMMENT 'Periodo del Lote',
  `security_level` tinyint(2) DEFAULT '0' COMMENT 'Nivel de seguridad',
  `type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de Declaracion/Impuesto',
  `issue_date` date DEFAULT NULL COMMENT 'Fecha de generacion del archivo',
  `data` mediumblob COMMENT 'Archivo Generado',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios del Lote',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_BATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_FS_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Lotes de declaraciones / Impuestos';

CREATE TABLE `fs_batch_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fs_batch` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador del Lote',
  `child_domain` int(4) NOT NULL COMMENT 'Identificador del Dominio de la declaracion',
  `company` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre / Razon Social de la declaracion',
  `detail_id` int(4) NOT NULL DEFAULT '0' COMMENT 'Identificador de la Declaracion',
  `year` int(4) NOT NULL COMMENT 'Ejercicio de la declaracion',
  `period` tinyint(2) DEFAULT '0' COMMENT 'Periodo de la declaracion',
  `complementary` tinyint(1) DEFAULT '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) DEFAULT '0' COMMENT 'Declaracion sustitutiva',
  `description` varchar(40) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Descripcion',
  `result` double(15,3) DEFAULT '0.000' COMMENT 'Resultado de la declaracion',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_BATCH_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_FS_BATCH_DETAIL_FS_BATCH` (`fs_batch`),
  CONSTRAINT `FK_FS_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_BATCH_DETAIL_FS_BATCH` FOREIGN KEY (`fs_batch`) REFERENCES `fs_batch` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de los lotes de declaraciones / impuestos';


UPDATE `db_version` SET `version_number` = '7.1.11';

COMMIT;
