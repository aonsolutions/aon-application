# Database: aon_master
# Version: Actualizacion de la version 5.5.4 a la version 5.5.5.
# Created by: girazu
# Creation Date: 28/10/2010 10:31
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

CREATE TABLE `bank_statement` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `rbank` int(4) NOT NULL COMMENT 'Identificador de Banco de la Compañia',
  `operation_date` date NOT NULL COMMENT 'Fecha de operacion',
  `concept` tinyint(2) NOT NULL default 0 COMMENT 'Concepto comun',
  `payment` tinyint(1) NOT NULL default 0 COMMENT 'Indica si es un pago',
  `amount` double(15,2) NOT NULL default '0.00' COMMENT 'Importe',
  `document` int(4) default 0 COMMENT 'Numero de documento',
  `reference1` varchar(12) collate latin1_spanish_ci default NULL COMMENT 'Referencia 1',
  `reference2` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Referencia 2',
  `description` varchar(76) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `status` tinyint(2) default 0 COMMENT 'Estado',
  PRIMARY KEY  (`id`),
  KEY `IDX_BANK_STATEMENT_RBANK` (`rbank`),
  CONSTRAINT `FK_BANK_STATEMENT_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Extractos bancarios';

ALTER TABLE `geozone` ADD `code` varchar(3) collate latin1_spanish_ci default NULL COMMENT 'Codigo de la Zona Geografica';

ALTER TABLE `geozone` ADD `system` tinyint(1) NOT NULL default '0' COMMENT 'Indica si es una Zona Geografica del sistema';

UPDATE `geozone` SET `code` = LPAD(""+`id`,2,"0") WHERE `id` BETWEEN 0 AND 53 AND `id` IN (SELECT `child` FROM `geotree`);

UPDATE `geozone` SET `code` = "ES" WHERE `id` BETWEEN 0 AND 53 AND `id` IN (SELECT `parent` FROM `geotree`);

UPDATE `geozone` SET `system` = 1 WHERE `id` BETWEEN 0 AND 53;

UPDATE `tax` SET `vat_deduction_type` = 0 WHERE `vat_deduction_type` IS NULL;

UPDATE `tax` SET `withholding_type` = 0 WHERE `withholding_type` IS NULL;

UPDATE `invoice_tax` SET `vat_deduction_type` = 0 WHERE `vat_deduction_type` IS NULL;

UPDATE `invoice_tax` SET `withholding_type` = 0 WHERE `withholding_type` IS NULL;


UPDATE `db_version` SET `version_number` = '5.5.5';

COMMIT;
