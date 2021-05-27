# Version: Upgrade from version 7.36.2 to 7.36.3.
# Created by: ecastellano@esferalia.com
# Creation Date: 22/06/2014 

CREATE TABLE `fs_model200` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `administration` tinyint(2) NOT NULL COMMENT 'Administracion',
  `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF',
  `name` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `complementary` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Declaracion complementaria',
  `receipt` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion',
  `complementary_receipt` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Declaracion sustituida',
  `cnae` varchar(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo CNAE',
  `period_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Tipo de periodo',
  `period_start` date NOT NULL COMMENT 'Inicio periodo',
  `period_end` date NOT NULL COMMENT 'Fin periodo',
  `comments` text COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL200_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL200_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_FS_MODEL200_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL200_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Impuesto sobre sociedades';

CREATE TABLE `fs_model200_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fs_model200` int(4) NOT NULL COMMENT 'Identificador del modelo 200',
  `key` varchar(4) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Clave Casilla',
  `value` double(15,3) DEFAULT NULL COMMENT 'Valor de la casilla',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL200_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL200_DETAIL_FS_MODEL200` (`fs_model200`),
  CONSTRAINT `FK_FS_MODEL200_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL200_DETAIL_FS_MODEL200` FOREIGN KEY (`fs_model200`) REFERENCES `fs_model200` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles Impuesto sobre sociedades';

CREATE TABLE `fs_model200_registry` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `fs_model200` int(4) NOT NULL COMMENT 'Identificador del modelo 200',
  `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF',
  `name` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',
  `province` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Provincia',
  `residence` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Residencia',
  `representative` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Representante',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Tipo. Administrador/participacion ',
  `percent` double(15,3) DEFAULT NULL COMMENT 'Porcentaje',
  `nominal_value` double(15,3) DEFAULT NULL COMMENT 'Valor Nominal',
  `book_value` double(15,3) DEFAULT NULL COMMENT 'Valor en libros',
  `incomes` double(15,3) DEFAULT NULL COMMENT 'Ingresos por dividendos',
  `a_value` double(15,3) DEFAULT NULL COMMENT 'Correccion de valor',
  `b_value` double(15,3) DEFAULT NULL COMMENT 'Reversion por perdidas',
  `c_value` double(15,3) DEFAULT NULL COMMENT 'Efecto de la correccion',
  `d_value` double(15,3) DEFAULT NULL COMMENT 'Saldo de correcciones',
  `capital` double(15,3) DEFAULT NULL COMMENT 'Capital',
  `reserve` double(15,3) DEFAULT NULL COMMENT 'Reservas',
  `other_amounts` double(15,3) DEFAULT NULL COMMENT 'Otras partidas',
  `result` double(15,3) DEFAULT NULL COMMENT 'Resultado del ultimo ejercicio',
  PRIMARY KEY (`id`),
  KEY `IDX_FS_MODEL200_REGISTRY_DOMAIN` (`domain`),
  KEY `IDX_FS_MODEL200_REGISTRY_FS_MODEL200` (`fs_model200`),
  CONSTRAINT `FK_FS_MODEL200_REGISTRY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FS_MODEL200_REGISTRY_FS_MODEL200` FOREIGN KEY (`fs_model200`) REFERENCES `fs_model200` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Adminitradores/participaciones del Impuesto sobre sociedades';

UPDATE `db_version` SET `version_number` = '7.36.3';

