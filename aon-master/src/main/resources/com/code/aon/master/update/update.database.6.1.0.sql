# Database: aon_master
# Version: Actualizacion de la version 6.1.0 a la version 6.1.1.
# Created by: girazu
# Creation Date: 25/02/2011 10:36
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `invoice` ADD `rdocument_type` tinyint(2) collate latin1_spanish_ci DEFAULT '0' COMMENT 'Tipo de documento (NIF, CIF...)' AFTER `rdocument`;

UPDATE `invoice` SET `rdocument_type` = 2 WHERE SUBSTR(`rdocument`, 1, 1) IN ('X','Y','Z');

UPDATE `invoice` SET `rdocument_type` = 1 WHERE SUBSTR(`rdocument`, 1, 1) BETWEEN 'A' AND 'J' OR SUBSTR(`rdocument`, 1, 1) BETWEEN 'N' AND 'W';

ALTER TABLE `invoice` ADD `rdocument_country` varchar(2) collate latin1_spanish_ci default 'ES' COMMENT 'Pais del documento' AFTER `rdocument_type`;

ALTER TABLE `finance` ADD `rdocument_type` tinyint(2) collate latin1_spanish_ci DEFAULT '0' COMMENT 'Tipo de documento (NIF, CIF...)' AFTER `rdocument`;

UPDATE `finance` SET `rdocument_type` = 2 WHERE SUBSTR(`rdocument`, 1, 1) IN ('X','Y','Z');

UPDATE `finance` SET `rdocument_type` = 1 WHERE SUBSTR(`rdocument`, 1, 1) BETWEEN 'A' AND 'J' OR SUBSTR(`rdocument`, 1, 1) BETWEEN 'N' AND 'W';

ALTER TABLE `finance` ADD `rdocument_country` varchar(2) collate latin1_spanish_ci DEFAULT 'ES' COMMENT 'Pais del documento' AFTER `rdocument_type`;

ALTER TABLE `invoice_address` ADD `street_type` varchar(2) collate latin1_spanish_ci default NULL COMMENT 'Tipo de via' AFTER `invoice`;

ALTER TABLE `invoice_address` MODIFY `street_type` varchar(2) collate latin1_spanish_ci default 'CL' COMMENT 'Tipo de via';

ALTER TABLE `invoice_address` ADD `number` varchar(12) collate latin1_spanish_ci DEFAULT NULL COMMENT 'Numero' AFTER `address`;

CREATE TABLE `fs_mod347` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
  `administration` tinyint(2) default '0' COMMENT 'Administracion',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
  `status` tinyint(2) default '0' COMMENT 'Estado de la Declaracion',
  `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad',
  `complementary` tinyint(1) default '0' COMMENT 'Declaracion complementaria',
  `replacement` tinyint(1) default '0' COMMENT 'Declaracion sustitutiva',
  `number` int(4) default '0' COMMENT 'Numero de Decl.',
  `replaced_number` int(4) default '0' COMMENT 'Numero de Decl. complementada o sustituida',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaracion de Model 347';

CREATE TABLE `fs_mod347_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `fs_mod347` int(4) NOT NULL default '0' COMMENT 'Identificador de la Declaracion',
  `type` varchar(1) collate latin1_spanish_ci default '0' COMMENT 'Clave de operacion',
  `document` varchar(9) collate latin1_spanish_ci default NULL COMMENT 'NIF del declarado',
  `registry` int(4) default '0' COMMENT 'Identificador del Declarado',
  `name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Apellidos  y Nombre del declarado',
  `province` int(4) default '0' COMMENT 'Provincia del declarado',
  `country` varchar(2) collate latin1_spanish_ci default '0' COMMENT 'Pais del declarado',
  `amount` double(15,3) default '0.000' COMMENT 'Importe de las operaciones',
  PRIMARY KEY  (`id`),
  KEY `IDX_FS_MOD347_DETAIL_FS_MOD347` (`fs_mod347`),
  CONSTRAINT `FK_FS_MOD347_DETAIL_FS_MOD347` FOREIGN KEY (`fs_mod347`) REFERENCES `fs_mod347` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de la Declaracion 347';

ALTER TABLE `fs_vat_detail` MODIFY `vat_key` varchar(3) NOT NULL COMMENT 'Clave de la Declaracion';

ALTER TABLE `contract` MODIFY `registration` int(4) default NULL COMMENT 'Número libro de matricula';

ALTER TABLE `contract` MODIFY `seniority_date` date default NULL COMMENT 'Fecha de antiguedad';


UPDATE `db_version` SET `version_number` = '6.1.1';

COMMIT;
