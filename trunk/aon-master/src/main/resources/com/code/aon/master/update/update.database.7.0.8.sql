# Database: aon_master
# Version: Actualizacion de la version 7.0.8 a la version 7.0.9.
# Created by: girazu
# Creation Date: 01/03/2012 10:00
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `hotel` ADD `phone` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Telefono del Hotel' AFTER `workplace`;
ALTER TABLE `hotel` ADD `fax` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Fax del Hotel' AFTER `phone`;
ALTER TABLE `hotel` ADD `email` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Email del Hotel' AFTER `fax`;
ALTER TABLE `hotel` ADD `web` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Web del Hotel' AFTER `email`;

ALTER TABLE `pos_shift` ADD `remarks` text collate latin1_spanish_ci COMMENT 'Observaciones del turno';
ALTER TABLE `pos_shift` ADD `invoice` int(4) default NULL COMMENT 'Identificador de la Factura';
ALTER TABLE `pos_shift` ADD KEY `IDX_POS_SHIFT_INVOICE` (`invoice`);
ALTER TABLE `pos_shift` ADD CONSTRAINT `FK_POS_SHIFT_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`);

ALTER TABLE `item_supplier` ADD `price` double default '0' COMMENT 'Precio del Producto en el Proveedor' AFTER `code`;

ALTER TABLE `invoice` ADD `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion';
ALTER TABLE `invoice` ADD `creation_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `invoice` ADD `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `invoice` ADD `modification_date` datetime default NULL COMMENT 'Fecha de modificacion';

ALTER TABLE `invoice_attach` ADD `type` tinyint(2) default '0' COMMENT 'Tipo de Archivo Adjunto';
ALTER TABLE `invoice_attach` ADD `attach_date` date default NULL COMMENT 'Fecha del Archivo Adjunto';

CREATE TABLE `project_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project` int(4) NOT NULL COMMENT 'Identificador del Proyecto',
  `mimeType` tinyint(2) default '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `attach_date` date default NULL COMMENT 'Fecha del Archivo Adjunto',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROJECT_ATTACH_DOMAIN` (`domain`),
  KEY `IDX_PROJECT_ATTACH_PROJECT` (`project`),
  CONSTRAINT `FK_PROJECT_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_ATTACH_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Proyectos';


UPDATE `db_version` SET `version_number` = '7.0.9';

COMMIT;
