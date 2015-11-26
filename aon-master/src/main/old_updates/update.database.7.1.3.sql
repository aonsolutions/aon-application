# Database: aon_master
# Version: Actualizacion de la version 7.1.3 a la version 7.1.4.
# Created by: girazu
# Creation Date: 06/07/2012 09:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `project_reservation_guest` CHANGE `codigo_barras` `barcode` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Codigo de pulsera';

ALTER TABLE `hotel` ADD `item_penalty` int(4) default NULL COMMENT 'Identificador del Producto para penalizaciones' AFTER `item_no_show`;
ALTER TABLE `hotel` ADD KEY `IDX_HOTEL_ITEM_PENALTY` (`item_penalty`);
ALTER TABLE `hotel` ADD CONSTRAINT `FK_HOTEL_ITEM_PENALTY` FOREIGN KEY (`item_penalty`) REFERENCES `item` (`id`);

CREATE TABLE `fs_mod349` (
   `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
   `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
   `year` int(4) NOT NULL COMMENT 'Ejercicio de la Declaracion',
   `period` tinyint(2) default '0' COMMENT 'Periodo de la Declaracion',
   `administration` tinyint(2) default '0' COMMENT 'Administracion',
   `comments` text collate latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
   `status` tinyint(2) default '0' COMMENT 'Estado de la Declaracion',
   `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad',
   `complementary` tinyint(1) default '0' COMMENT 'Declaracion complementaria',
   `replacement` tinyint(1) default '0' COMMENT 'Declaracion sustitutiva',
   `number` int(4) default '0' COMMENT 'Numero de Declaracion',
   `replaced_number` int(4) default '0' COMMENT 'Numero de Declaracion complementada o sustituida',
   PRIMARY KEY  (`id`),
   KEY `IDX_FS_MOD349_DOMAIN` (`domain`),
   CONSTRAINT `FK_FS_MOD349_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaracion de Modelo 349';

CREATE TABLE `fs_mod349_detail` (
   `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
   `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
   `fs_mod349` int(4) NOT NULL default '0' COMMENT 'Identificador de la Declaracion',
   `rectification` tinyint(1) NOT NULL default '0' COMMENT 'Rectificacion',
   `type` varchar(1) collate latin1_spanish_ci default '0' COMMENT 'Clave de operacion',
   `document` varchar(15) collate latin1_spanish_ci default NULL COMMENT 'Documento del operador',
   `registry` int(4) default '0' COMMENT 'Identificador del Declarado',
   `name` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Apellidos y Nombre del Declarado',
   `country` varchar(2) collate latin1_spanish_ci default '0' COMMENT 'Pais del Declarado',
   `accumulated` double(15,3) default '0.000' COMMENT 'Importe acumulado de las operaciones',
   `declared` double(15,3) default '0.000' COMMENT 'Importe declarado de las operaciones',
   `amount` double(15,3) default '0.000' COMMENT 'Importe de las operaciones',
   `rectified_year` int(4) default NULL COMMENT 'Ejercicio de la Declaracion del importe rectificado',
   `rectified_period` tinyint(2) default NULL COMMENT 'Periodo de la Declaracion del importe rectificado',
   `rectified_amount` varchar(45) collate latin1_spanish_ci default '0.000' COMMENT 'Importe rectificado',
   PRIMARY KEY  (`id`),
   KEY `IDX_FS_MOD349_DETAIL_DOMAIN` (`domain`),
   KEY `IDX_FS_MOD349_DETAIL_FS_MOD349` (`fs_mod349`),
   CONSTRAINT `FK_FS_MOD349_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
   CONSTRAINT `FK_FS_MOD349_DETAIL_FS_MOD349` FOREIGN KEY (`fs_mod349`) REFERENCES `fs_mod349` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de la Declaracion 349';


UPDATE `db_version` SET `version_number` = '7.1.4';

COMMIT;
