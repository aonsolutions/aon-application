# Database: aon_master
# Version: Actualizacion de la version 7.5.0 a la version 7.6.0.
# Created by: girazu
# Creation Date: 13/12/2012 19:20
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `domain` CHANGE `userManagement` `enableHeredity` tinyint(1) NOT NULL default '0' COMMENT 'Indica si el Dominio tiene deshabilitado la herencia de registros o no';
UPDATE `domain` SET `enableHeredity` = 0;

CREATE TABLE `fs_model` (
   `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
   `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
   `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
   `period` tinyint(2) NOT NULL default '0' COMMENT 'Periodo de la Declaracion',
   `administration` tinyint(2) NOT NULL COMMENT 'Administracion',
   `status` tinyint(2) NOT NULL default '0' COMMENT 'Estado de la Declaracion',
   `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad',
   `complementary` tinyint(1) default '0' COMMENT 'Declaracion complementaria',
   `replacement` tinyint(1) default '0' COMMENT 'Declaracion sustitutiva',
   `model` varchar(3) collate latin1_spanish_ci NOT NULL COMMENT 'Tipo de modelo',
   `number` int(4) default '0' COMMENT 'Numero de Declaracion',
   `replaced_number` int(4) default '0' COMMENT 'Numero de Declaracion complementada o sustituida',
   `comments` text collate latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
   PRIMARY KEY  (`id`),
   KEY `IDX_FS_MODEL_DOMAIN` (`domain`),
   CONSTRAINT `FK_FS_MODEL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaraciones Fiscales';

CREATE TABLE `fs_model_detail` (
   `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
   `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
   `fs_model` int(4) NOT NULL COMMENT 'Identificador de la Declaracion',
   `type` varchar(10) collate latin1_spanish_ci NOT NULL COMMENT 'Clave de la Declaracion',
   `acu_amount` double(15,3) default '0.000' COMMENT 'Importe acumulado',
   `dec_amount` double(15,3) default '0.000' COMMENT 'Importe declarado',
   `res_amount` double(15,3) default '0.000' COMMENT 'Importe resultado',
   `adj_amount` double(15,3) default '0.000' COMMENT 'Importe ajustado',
   `amount` double(15,3) default '0.000' COMMENT 'Importe',
   PRIMARY KEY  (`id`),
   KEY `IDX_FS_MODEL_DETAIL_DOMAIN` (`domain`),
   KEY `IDX_FS_MODEL_DETAIL_FS_MODEL` (`fs_model`),
   CONSTRAINT `FK_FS_MODEL_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
   CONSTRAINT `FK_FS_MODEL_DETAIL_FS_MODEL` FOREIGN KEY (`fs_model`) REFERENCES `fs_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de Declaraciones Fiscales';

DROP TABLE `project_dossier`;

DROP TABLE `workactivity`;


UPDATE `db_version` SET `version_number` = '7.6.0';

COMMIT;
