# Database: aon_master
# Version: Actualizacion de la version 5.6.0 a la version 5.6.1.
# Created by: girazu
# Creation Date: 03/11/2010 17:57
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

DROP TABLE `account_entry_bank_statement`;

DROP TABLE `bank_statement`;

CREATE TABLE `bank_statement` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `rbank` int(4) NOT NULL COMMENT 'Identificador de Banco de la Compañia',
  `lot_number` int(4) NOT NULL default '0' COMMENT 'Numero de lote',
  `operation_date` date NOT NULL COMMENT 'Fecha de operacion',
  `common_concept` tinyint(2) NOT NULL default '0' COMMENT 'Concepto comun',
  `own_concept` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Concepto propio',
  `payment` tinyint(1) NOT NULL default '0' COMMENT 'Indica si es un pago',
  `amount` double(15,2) NOT NULL default '0.00' COMMENT 'Importe',
  `document` int(4) default '0' COMMENT 'Numero de documento',
  `reference1` varchar(12) collate latin1_spanish_ci default NULL COMMENT 'Referencia 1',
  `reference2` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Referencia 2',
  `description` varchar(80) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `status` tinyint(2) default '0' COMMENT 'Estado',
  PRIMARY KEY  (`id`),
  KEY `IDX_BANK_STATEMENT_RBANK` (`rbank`),
  CONSTRAINT `FK_BANK_STATEMENT_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Extractos bancarios';

CREATE TABLE `account_entry_bank_statement` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `account_entry` int(4) NOT NULL COMMENT 'Identificador de Asiento',
  `bank_statement` int(4) NOT NULL COMMENT 'Identificador de Extracto bancario',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACC_ENTRY_BANK_STATEMENT_ACC_ENTRY` (`account_entry`),
  KEY `IDX_ACC_ENTRY_BANK_STATEMENT_BANK_STATEMENT` (`bank_statement`),
  CONSTRAINT `FK_ACC_ENTRY_BANK_STATEMENT_ACC_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_ACC_ENTRY_BANK_STATEMENT_BANK_STATEMENT` FOREIGN KEY (`bank_statement`) REFERENCES `bank_statement` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Asientos Contables y Extractos bancarios';

CREATE TABLE `bank_statement_link` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `bank_statement` int(4) NOT NULL COMMENT 'Identificador de Extracto bancario',
  `source` tinyint(2) NOT NULL default 0 COMMENT 'Origen',
  `source_id` int(4) NOT NULL default 0 COMMENT 'Identificador del origen',
  `amount` double(15,2) NOT NULL default '0.00' COMMENT 'Importe',
  `reliability` tinyint(2) default 0 COMMENT 'Fiabilidad',
  `status` tinyint(2) default 0 COMMENT 'Estado',
  PRIMARY KEY  (`id`),
  KEY `IDX_BANK_STATEMENT_LINK_BANK_STATEMENT` (`bank_statement`),
  CONSTRAINT `FK_BANK_STATEMENT_LINK_BANK_STATEMENT` FOREIGN KEY (`bank_statement`) REFERENCES `bank_statement` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Enlaces del Extracto bancario';

CREATE TABLE `bank_concept` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Concepto',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Conceptos bancarios';

CREATE TABLE `bank_concept_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `bank_concept` int(4) NOT NULL default '0' COMMENT 'Identificador del Concepto bancario',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY `IDX_BANK_CONCEPT_ACCOUNT_BANK_CONCEPT` (`bank_concept`),
  KEY `IDX_BANK_CONCEPT_ACCOUNT_ACCOUNT` (`account`),
  CONSTRAINT `FK_BANK_CONCEPT_ACCOUNT_BANK_CONCEPT` FOREIGN KEY (`bank_concept`) REFERENCES `bank_concept` (`id`),
  CONSTRAINT `FK_BANK_CONCEPT_ACCOUNT_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Conceptos bancarios';

UPDATE `finance_tracking` SET `description` = REPLACE(`description`, "Apunte", "Asiento");


UPDATE `db_version` SET `version_number` = '5.6.1';

COMMIT;
