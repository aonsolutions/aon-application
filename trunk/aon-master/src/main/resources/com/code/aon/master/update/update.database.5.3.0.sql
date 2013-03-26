# Database: aon_master
# Version: Actualizacion de la version 5.3.0 a la version 5.3.1.
# Created by: girazu
# Creation Date: 30/06/2010 14:17
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

CREATE TABLE `enterprise` (
  `registry` int(4) NOT NULL default '1' COMMENT 'Registro de la Empresa',
  PRIMARY KEY  (`registry`),
  CONSTRAINT `FK_ENTERPRISE_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Empresa';

CREATE TABLE `enterprise_ccc` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `enterprise` int(4) NOT NULL default '0' COMMENT 'Identificador de la Empresa',
  `ccc` char(11) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Valor del Codigo Cuenta Cotizacion',
  PRIMARY KEY  (`id`),
  KEY `enterprise` (`enterprise`),
  CONSTRAINT `FK_ENTERPRISE_CCC_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Codigo Cuenta Cotizacion';

INSERT INTO `enterprise` (`registry`) SELECT registry FROM `company`;

ALTER TABLE `workplace` ADD `enterprise` int(4) NOT NULL DEFAULT '1' COMMENT 'Empresa asociada al Centro de Trabajo' AFTER `id`;

UPDATE `workplace` SET `enterprise` = (SELECT `registry` FROM `company`);

ALTER TABLE `workplace` ADD KEY `IDX_WORKPLACE_ENTERPRISE` (`enterprise`);

ALTER TABLE `workplace` ADD CONSTRAINT `FK_WORKPLACE_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`);

INSERT INTO `target` (`registry`, `surcharge`, `withholding`, `transaction`, `status`)
	SELECT `registry`, `surcharge`, `withholding`, `transaction`, `status` FROM `customer`
	WHERE `registry` NOT IN (SELECT `registry` FROM `target`);


UPDATE `db_version` SET `version_number` = '5.3.1';

COMMIT;
