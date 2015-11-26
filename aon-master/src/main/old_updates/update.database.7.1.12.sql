# Database: aon_master
# Version: Actualizacion de la version 7.1.12 a la version 7.2.0.
# Created by: girazu
# Creation Date: 15/11/2012 12:45
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `customer` DROP FOREIGN KEY `FK_CUSTOMER_SEGMENT`;
ALTER TABLE `customer` DROP KEY `IDX_CUSTOMER_SEGMENT`;
ALTER TABLE `customer` DROP `segment`;

DROP TABLE `customer_segment`;

ALTER TABLE `finance` ADD `remarks` text collate latin1_spanish_ci COMMENT 'Observaciones del Vencimiento' AFTER `security_level`;

ALTER TABLE `commercial_activity` ADD `probability` int(4) default NULL COMMENT 'Probabilidad de la Actividad';
ALTER TABLE `commercial_activity` ADD `survey` int(4) default NULL COMMENT 'Identificador del Cuestionario';
ALTER TABLE `commercial_activity` ADD KEY `IDX_COMMERCIAL_ACTIVITY_SURVEY` (`survey`);
ALTER TABLE `commercial_activity` ADD CONSTRAINT `FK_COMMERCIAL_ACTIVITY_SURVEY` FOREIGN KEY (`survey`) REFERENCES `survey` (`id`);

ALTER TABLE `survey` ADD `scope` int(4) NOT NULL default 1 COMMENT 'Identificador del Ambito' AFTER `domain`;
UPDATE `survey` SET `scope` = (SELECT MIN(`id`) FROM `scope` WHERE `survey`.domain = `scope`.domain);
ALTER TABLE `survey` MODIFY `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito';
ALTER TABLE `survey` ADD KEY `IDX_SURVEY_SCOPE` (`scope`);
ALTER TABLE `survey` ADD CONSTRAINT `FK_SURVEY_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);

ALTER TABLE `mk_template` ADD `scope` int(4) NOT NULL default 1 COMMENT 'Identificador del Ambito' AFTER `domain`;
UPDATE `mk_template` SET `scope` = (SELECT MIN(`id`) FROM `scope` WHERE `mk_template`.domain = `scope`.domain);
ALTER TABLE `mk_template` MODIFY `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito';
ALTER TABLE `mk_template` ADD KEY `IDX_MK_TEMPLATE_SCOPE` (`scope`);
ALTER TABLE `mk_template` ADD CONSTRAINT `FK_MK_TEMPLATE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);

ALTER TABLE `mk_template` ADD `rattach` int(4) default NULL COMMENT 'Identificador del Archivo Adjunto';
ALTER TABLE `mk_template` ADD KEY `IDX_MK_TEMPLATE_RATTACH` (`rattach`);
ALTER TABLE `mk_template` ADD CONSTRAINT `FK_MK_TEMPLATE_RATTACH` FOREIGN KEY (`rattach`) REFERENCES `rattach` (`id`);

ALTER TABLE `commercial_tracking` DROP FOREIGN KEY `FK_COMMERCIAL_TRACKING_PROJECT_COMMERCIAL`;
ALTER TABLE `commercial_tracking` DROP KEY `IDX_COMMERCIAL_TRACKING_PROJECT_COMMERCIAL`;
ALTER TABLE `commercial_tracking` CHANGE `project` `project_commercial` int(4) NOT NULL COMMENT 'Identificador del Proyecto';
ALTER TABLE `commercial_tracking` ADD KEY `IDX_COMMERCIAL_TRACKING_PROJECT_COMMERCIAL` (`project_commercial`);
ALTER TABLE `commercial_tracking` ADD CONSTRAINT `FK_COMMERCIAL_TRACKING_PROJECT_COMMERCIAL` FOREIGN KEY (`project_commercial`) REFERENCES `project_commercial` (`project`);

ALTER TABLE `balance_detail` ADD `notes` VARCHAR(64) default NULL COMMENT 'Notas en el Balance' AFTER `sortKey`;

ALTER TABLE `account` ADD `active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT 'Indica si la Cuenta esta activo o no' AFTER `level`;

CREATE TABLE `amortization_invoice` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `amortization` int(4) NOT NULL COMMENT 'Identificador de la Ficha de Amortizacion',
  `invoice` int(4) NOT NULL COMMENT 'Identificador de la Factura',
  `sales` tinyint(1) NOT NULL default '0' COMMENT 'Indica si es venta de inmovilizado o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_AMORTIZATION_INVOICE_DOMAIN` (`domain`),
  KEY `IDX_AMORTIZATION_INVOICE_AMORTIZATION` (`amortization`),
  KEY `IDX_AMORTIZATION_INVOICE_INVOICE` (`invoice`),
  CONSTRAINT `FK_AMORTIZATION_INVOICE_AMORTIZATION` FOREIGN KEY (`amortization`) REFERENCES `amortization` (`id`),
  CONSTRAINT `FK_AMORTIZATION_INVOICE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_AMORTIZATION_INVOICE_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Vinculo entr la Ficha de Amortizacion y la factura.';


UPDATE `db_version` SET `version_number` = '7.2.0';

COMMIT;
