# Database: aon_master
# Version: Actualizacion de la version 6.18.3 a la version 6.18.4.
# Created by: girazu
# Creation Date: 19/10/2011 10:00
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

UPDATE `invoice_detail` SET `source` = 4 WHERE `source` = 1 AND `source_id` IS NOT NULL;

UPDATE `invoice_detail` SET `source` = 3 WHERE `source` = 2 AND `source_id` IS NOT NULL;

UPDATE `invoice_detail` SET `source` = 7 WHERE `source` IN (1, 2) AND `source_id` IS NULL;

UPDATE `invoice_detail` SET `source` = 7 WHERE `source` = 0;

ALTER TABLE `warehouse` ADD `workplace` int(4) default NULL COMMENT 'Identificador del Centro de Trabajo';

ALTER TABLE `warehouse` ADD KEY `IDX_WAREHOUSE_WORKPLACE` (`workplace`);

ALTER TABLE `warehouse` ADD CONSTRAINT `FK_WAREHOUSE_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`);

ALTER TABLE `invoice_detail` ADD `warehouse` int(4) default NULL COMMENT 'Identificador del Almacen';

ALTER TABLE `invoice_detail` ADD KEY `IDX_INVOICE_DETAIL_WAREHOUSE` (`warehouse`);

ALTER TABLE `invoice_detail` ADD CONSTRAINT `FK_INVOICE_DETAIL_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`);

ALTER TABLE `sales_detail` DROP `source`;

ALTER TABLE `delivery_detail` DROP `source`;

ALTER TABLE `delivery_detail` DROP `type`;

ALTER TABLE `income_detail` DROP `source`;

ALTER TABLE `income_detail` DROP `type`;

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

CREATE TABLE `cost_profile` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `description` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion',
  `cost` double NOT NULL default '0' COMMENT 'Costo por hora',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Perfiles de Costos para Usuarios';

ALTER TABLE `task_holder` ADD `cost_profile` int(4) default NULL COMMENT 'Identificador del Perfil de Costos' AFTER `user_id`;

ALTER TABLE `task_holder` ADD KEY `IDX_TASK_HOLDER_COST_PROFILE` (`cost_profile`);

ALTER TABLE `task_holder` ADD CONSTRAINT `FK_TASK_HOLDER_COST_PROFILE` FOREIGN KEY (`cost_profile` ) REFERENCES `cost_profile` (`id` );

ALTER TABLE `daily_tracking` ADD `cost` double default 0 COMMENT 'Costo' AFTER `task`; 


UPDATE `db_version` SET `version_number` = '6.18.4';

COMMIT;
